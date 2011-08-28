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
import java.util.Map.Entry;

import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.CleanerTransformations;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.HtmlSerializer;
import org.htmlcleaner.SimpleHtmlSerializer;
import org.htmlcleaner.TagNode;
import org.htmlcleaner.TagNodeVisitor;
import org.htmlcleaner.TagTransformation;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.renderer.PageContext;
import com.atlassian.renderer.WikiStyleRenderer;

import fr.xebia.confluence2wordpress.core.converter.postprocessors.ConversionPostProcessor;
import fr.xebia.confluence2wordpress.core.converter.postprocessors.PressReviewHeaderPostProcessor;
import fr.xebia.confluence2wordpress.core.converter.postprocessors.TableOfContentsPostProcessor;
import fr.xebia.confluence2wordpress.core.converter.visitors.AttachmentsProcessor;
import fr.xebia.confluence2wordpress.core.converter.visitors.CdataStripper;
import fr.xebia.confluence2wordpress.core.converter.visitors.CodeMacroProcessor;
import fr.xebia.confluence2wordpress.core.converter.visitors.CssClassNameCleaner;
import fr.xebia.confluence2wordpress.core.converter.visitors.EmptySpanStripper;
import fr.xebia.confluence2wordpress.core.converter.visitors.NewCodeMacroProcessor;

public class Converter {

    private WikiStyleRenderer wikiStyleRenderer;
    
    public Converter(WikiStyleRenderer wikiStyleRenderer) {
        super();
        this.wikiStyleRenderer = wikiStyleRenderer;
    }

    public String convert(ContentEntityObject page, ConverterOptions options) {
        String wiki = processWiki(page.getContent(), options);
        String confluenceHtml = processConfluenceHtml(page.toPageContext(), wiki, options);
        String wordpressHtml = processWordpressHtml(confluenceHtml, options);
        return wordpressHtml;
    }

    protected String processWiki(String wiki, ConverterOptions options) {
        if(options.getDisableConfluenceMacros() != null) {
            for (String macro : options.getDisableConfluenceMacros()) {
                MacroDisabler disabler = MacroDisabler.forMacro(macro);
                wiki = disabler.disableMacro(wiki);
            }
        }
        return wiki;
    }

    protected String processConfluenceHtml(PageContext pageContext, String wiki, ConverterOptions options) {
        //see DefaultWysiwygConverter and RenderContext
        /*
         * Can't play with some classes here:
         * java.lang.LinkageError: loader constraint violation: when resolving method
         * "com.atlassian.confluence.renderer.PageContext.pushRenderMode(Lcom/atlassian/renderer/v2/RenderMode;)V"
         * the class loader (instance of org/apache/felix/framework/ModuleImpl$ModuleClassLoader) of the current class,
         * fr/dutra/xebia/confluence2wordpress/rpc/ConvertRpcImpl$1, and the class loader (instance of org/apache/catalina/loader/WebappClassLoader)
         * for resolved class, com/atlassian/confluence/renderer/PageContext,
         * have different Class objects for the type com/atlassian/renderer/v2/RenderMode used in the signature
         */
        //pageContext.pushRenderMode(RenderMode.ALL_WITH_NO_MACRO_ERRORS);
        String confluenceHtml = wikiStyleRenderer.convertWikiToXHtml(pageContext, wiki);
        return confluenceHtml;
    }

    protected String processWordpressHtml(String confluenceHtml, ConverterOptions options) {

        //HtmlCleaner is NOT thread-safe
        CleanerProperties cleanerProps = getCleanerProperties(options);
        HtmlCleaner cleaner = new HtmlCleaner(cleanerProps);

        CleanerTransformations transformations = getCleanerTransformations(options);
        cleaner.setTransformations(transformations);

        TagNode root = cleaner.clean(confluenceHtml);
        TagNode body = root.findElementByName("body", false);

        //tree transformations
        List<TagNodeVisitor> visitors = getTagNodeVisitors(options);
        for (TagNodeVisitor visitor : visitors) {
            body.traverse(visitor);
        }
        
        String html = serialize(body, cleanerProps);

        List<ConversionPostProcessor> postProcessors = getPostProcessors(options);
        for (ConversionPostProcessor conversionPostProcessor : postProcessors) {
            html = conversionPostProcessor.postProcess(html, body, options);
        }
        
        return html;
    }

    protected String serialize(TagNode body, CleanerProperties cleanerProps) {
        
        try {
            //PrettyHtmlSerializer does not work very well
            //JTidy is too violent and Jericho almost OK but no
            HtmlSerializer serializer = new SimpleHtmlSerializer(cleanerProps);
            return serializer.getAsString(body, "UTF-8", true);
        } catch (IOException e) {
            // should not occur with string writers
            return null;
        }
        
    }
    
    protected CleanerProperties getCleanerProperties(ConverterOptions options) {
        CleanerProperties properties = new CleanerProperties();
        properties.setOmitXmlDeclaration(options.isOmitXmlDeclaration());
        properties.setUseCdataForScriptAndStyle(options.isUseCdataForScriptAndStyle());
        properties.setOmitComments(options.isOmitComments());
        properties.setUseEmptyElementTags(options.isUseEmptyElementTags());
        return properties;
    }

    protected CleanerTransformations getCleanerTransformations(ConverterOptions options) {

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

    protected List<TagNodeVisitor> getTagNodeVisitors(ConverterOptions options) {
        List<TagNodeVisitor> visitors = new ArrayList<TagNodeVisitor>();
        visitors.add(new CodeMacroProcessor());
        visitors.add(new NewCodeMacroProcessor(options.getSyntaxHighlighterPlugin().getSubstitutionMap()));
        if(options.isConvertCdata()) {
            visitors.add(new CdataStripper());
        }
        if(options.getAttachmentsMap() != null) {
            visitors.add(new AttachmentsProcessor(options.getAttachmentsMap()));
        }
        //regular visitors
        visitors.add(new CssClassNameCleaner());
        visitors.add(new EmptySpanStripper());
        return visitors;
    }
    
    protected List<ConversionPostProcessor> getPostProcessors(ConverterOptions options) {

        List<ConversionPostProcessor> processors = new ArrayList<ConversionPostProcessor>();
        processors.add(new TableOfContentsPostProcessor());
        processors.add(new PressReviewHeaderPostProcessor());
        return processors;
        
    }


}