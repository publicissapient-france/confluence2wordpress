/**
 * Copyright 2011 Alexandre Dutra
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package fr.xebia.confluence2wordpress.core.converter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.xpath.XPathExpressionException;

import net.htmlparser.jericho.Source;
import net.htmlparser.jericho.SourceFormatter;

import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.CleanerTransformations;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.HtmlSerializer;
import org.htmlcleaner.SimpleHtmlSerializer;
import org.htmlcleaner.TagNode;
import org.htmlcleaner.TagNodeVisitor;
import org.htmlcleaner.TagTransformation;
import org.htmlcleaner.WhitespaceTolerantTagInfoProvider;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.DefaultConversionContext;
import com.atlassian.confluence.content.render.xhtml.Renderer;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.xhtml.api.XhtmlContent;

import fr.xebia.confluence2wordpress.core.converter.postprocessors.CodeMacroPostprocessor;
import fr.xebia.confluence2wordpress.core.converter.postprocessors.PostProcessor;
import fr.xebia.confluence2wordpress.core.converter.postprocessors.PressReviewHeaderPostProcessor;
import fr.xebia.confluence2wordpress.core.converter.postprocessors.TableOfContentsPostProcessor;
import fr.xebia.confluence2wordpress.core.converter.preprocessors.CodeMacroPreprocessor;
import fr.xebia.confluence2wordpress.core.converter.preprocessors.IgnoredMacrosPreProcessor;
import fr.xebia.confluence2wordpress.core.converter.preprocessors.MoreMacroPreprocessor;
import fr.xebia.confluence2wordpress.core.converter.preprocessors.PreProcessor;
import fr.xebia.confluence2wordpress.core.converter.visitors.AnchorProcessor;
import fr.xebia.confluence2wordpress.core.converter.visitors.AttributesCleaner;
import fr.xebia.confluence2wordpress.core.converter.visitors.EmptyParagraphStripper;
import fr.xebia.confluence2wordpress.core.converter.visitors.EmptySpanStripper;
import fr.xebia.confluence2wordpress.core.converter.visitors.ImageProcessor;
import fr.xebia.confluence2wordpress.core.converter.visitors.MoreMacroProcessor;
import fr.xebia.confluence2wordpress.core.converter.visitors.TagAttributesProcessor;
import fr.xebia.confluence2wordpress.core.sync.SynchronizedAttachment;
import fr.xebia.confluence2wordpress.util.XPathUtils;

public class DefaultConverter implements Converter {

    private static final String ERROR_DIV_END = "</div>";

	private static final String ERROR_DIV_START = "<div class=\"error\">";

	private static final String XPATH_ERROR_MSG = "/div[@class = 'error']";

	private static final String XPATH_COUNT_ALL_BUT_ERRORS = "count(/*[name() != 'div' or @class != 'error'])";

	private final Renderer renderer;

	private final XhtmlContent xhtmlUtils;
	
    public DefaultConverter(Renderer renderer, XhtmlContent xhtmlUtils) {
		super();
		this.renderer = renderer;
		this.xhtmlUtils = xhtmlUtils;
	}

	public String convert(ContentEntityObject page, ConverterOptions options) throws ConversionException {
    	
        String storage = page.getBodyAsString();
        DefaultConversionContext conversionContext = new DefaultConversionContext(page.toPageContext());

        //storage pre-processing
        List<PreProcessor> preProcessors = getPreProcessors(options, conversionContext);
        for (PreProcessor preProcessor : preProcessors) {
        	storage = preProcessor.preProcess(storage, options);
        }
        
        //wiki -> html conversion
        String originalTitle = page.getTitle();
        String view;
        try {
        	//temporarily replace page title to get correct anchors
        	//(I know it's ugly)
        	//Otherwise the anchors are built by
        	//com.atlassian.renderer.v2.macro.basic.BasicAnchorMacro.getAnchor(RenderContext, String)
        	//basically it's GeneralUtil.urlEncode((page title + "-" + heading title).trim().replaceAll(" ", ""))
        	//see also com.atlassian.confluence.util.GeneralUtil.urlEncode(String)
            page.setTitle(options.getPageTitle());
			view = renderer.render(storage, conversionContext);
        } finally {
            page.setTitle(originalTitle);
        }
        
        handleConversionErrors(view);
        
        //HTML cleanup
        HtmlCleaner cleaner = getHtmlCleaner(options);
        TagNode root = cleaner.clean(view);
        TagNode body = root.findElementByName("body", false);

        //DOM traversal
        List<TagNodeVisitor> visitors = getTagNodeVisitors(options);
        for (TagNodeVisitor visitor : visitors) {
            body.traverse(visitor);
        }
        
        //serialization
        String html = serialize(body, cleaner.getProperties(), options);

        //HTML post-processing
        List<PostProcessor> postProcessors = getPostProcessors(options);
        for (PostProcessor postProcessor : postProcessors) {
            html = postProcessor.postProcess(html, body, options);
        }
        
        return html;
    }

    private void handleConversionErrors(String view) throws ConversionException {
        /*
         * <div class="error">error msg</div>
         */
    	if(view.startsWith(ERROR_DIV_START) && view.endsWith(ERROR_DIV_END)) {
	    	try {
				int count = XPathUtils.evaluateXPathAsInt(view , XPATH_COUNT_ALL_BUT_ERRORS);
				if(count == 0) {
					String errorMsg = XPathUtils.evaluateXPathAsString(view, XPATH_ERROR_MSG);
					throw new ConversionException(errorMsg);
				}
			} catch (XPathExpressionException e) {
				//in case of exception, we assume the view is not the expected one
			}
    	}
	}

	private HtmlCleaner getHtmlCleaner(ConverterOptions options) {
        //HtmlCleaner is NOT thread-safe
    	CleanerProperties cleanerProps = getCleanerProperties(options);
        HtmlCleaner cleaner = new HtmlCleaner(new WhitespaceTolerantTagInfoProvider(), cleanerProps);
        CleanerTransformations transformations = getCleanerTransformations(options);
        cleaner.setTransformations(transformations);
		return cleaner;
	}

	private String serialize(TagNode body, CleanerProperties cleanerProps, ConverterOptions options) {
		String html;
        try {
            //PrettyHtmlSerializer does not work very well
            HtmlSerializer serializer = new SimpleHtmlSerializer(cleanerProps);
            html = serializer.getAsString(body, "UTF-8", true);
        } catch (IOException e) {
            // should not occur with string writers
            return null;
        }
        if(options.isFormatHtml()) {
	        SourceFormatter sourceFormatter = new SourceFormatter(new Source(html));
	        sourceFormatter.setIndentString("  ");
			try {
				html = sourceFormatter.toString();
			} catch (Exception e) {
				//TODO
			}
        }
		return html;
    }
    
	private CleanerProperties getCleanerProperties(ConverterOptions options) {
        CleanerProperties properties = new CleanerProperties();
        properties.setOmitXmlDeclaration(options.isOmitXmlDeclaration());
        properties.setUseCdataForScriptAndStyle(options.isUseCdataForScriptAndStyle());
        properties.setOmitComments(options.isOmitComments());
        properties.setUseEmptyElementTags(options.isUseEmptyElementTags());
        return properties;
    }

	private CleanerTransformations getCleanerTransformations(ConverterOptions options) {
        CleanerTransformations transformations = new CleanerTransformations();
        //tag transformations
        if(options.getTagTransformations() != null) {
            for (Entry<String,String> entry : options.getTagTransformations().entrySet()) {
                transformations.addTransformation(new TagTransformation(entry.getKey(), entry.getValue()));
            }
        }
        //font -> span
        if(options.isConvertFontTagToSpan()) {
            TagTransformation tt = new TagTransformation("font", "span", true);
            tt.addAttributeTransformation("size");
            tt.addAttributeTransformation("face");
            tt.addAttributeTransformation(
                "style",
                "${style};font-family=${face};font-size=${size};"
                );
            transformations.addTransformation(tt);
        }
        return transformations;
    }

	private List<PreProcessor> getPreProcessors(ConverterOptions options, ConversionContext conversionContext) {
        List<PreProcessor> processors = new ArrayList<PreProcessor>();
        processors.add(new IgnoredMacrosPreProcessor(xhtmlUtils, conversionContext));
        processors.add(new MoreMacroPreprocessor(xhtmlUtils, conversionContext));
        processors.add(new CodeMacroPreprocessor(xhtmlUtils, conversionContext, options.getSyntaxHighlighterPlugin()));
        return processors;
	}

	private List<TagNodeVisitor> getTagNodeVisitors(ConverterOptions options) {
        List<TagNodeVisitor> visitors = new ArrayList<TagNodeVisitor>();
        visitors.add(new MoreMacroProcessor());
        List<SynchronizedAttachment> attachments = options.getSynchronizedAttachments();
		if(attachments != null && ! attachments.isEmpty()) {
            visitors.add(new ImageProcessor(attachments, options.getConfluenceRootUrl()));
            visitors.add(new AnchorProcessor(attachments, options.getConfluenceRootUrl()));
        }
		//must be done:
		//after image and anchor processing
        visitors.add(new AttributesCleaner());
        Map<String, String> tagAttributes = options.getTagAttributes();
		if(tagAttributes != null && ! tagAttributes.isEmpty()) {
        	visitors.add(new TagAttributesProcessor(tagAttributes));
        }
        visitors.add(new EmptySpanStripper());
        visitors.add(new EmptyParagraphStripper());
        return visitors;
    }

	private List<PostProcessor> getPostProcessors(ConverterOptions options) {
        List<PostProcessor> processors = new ArrayList<PostProcessor>();
        processors.add(new CodeMacroPostprocessor());
        processors.add(new TableOfContentsPostProcessor());
        processors.add(new PressReviewHeaderPostProcessor());
        return processors;
    }


}