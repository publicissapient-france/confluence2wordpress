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
package fr.xebia.confluence2wordpress.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.CleanerTransformations;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.SimpleHtmlSerializer;
import org.htmlcleaner.TagNode;
import org.htmlcleaner.TagNodeVisitor;
import org.htmlcleaner.TagTransformation;

import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.renderer.PageContext;
import com.atlassian.renderer.WikiStyleRenderer;

import fr.xebia.confluence2wordpress.core.visitors.AttachmentsProcessor;
import fr.xebia.confluence2wordpress.core.visitors.CdataStripper;
import fr.xebia.confluence2wordpress.core.visitors.CodeMacroConverter;
import fr.xebia.confluence2wordpress.core.visitors.CssClassNameCleaner;
import fr.xebia.confluence2wordpress.core.visitors.EmptySpanStripper;
import fr.xebia.confluence2wordpress.core.visitors.SyntaxHighlighterConverter;
import fr.xebia.confluence2wordpress.core.visitors.UrlConverter;
import fr.xebia.confluence2wordpress.rdp.AnchorTransformer;
import fr.xebia.confluence2wordpress.rdp.HeadingsCollector;
import fr.xebia.confluence2wordpress.rdp.RevueDePresseHelper;

public class Converter {

    private WikiStyleRenderer wikiStyleRenderer;

    public Converter(WikiStyleRenderer wikiStyleRenderer) {
        super();
        this.wikiStyleRenderer = wikiStyleRenderer;
    }

    public String convert(Page page, ConverterOptions options) {
        String wiki = preConvert(page.getContent(), options);
        String confluenceHtml = convertInternal(page.toPageContext(), wiki, options);
        String wordpressHtml = postConvert(confluenceHtml, options);
        return wordpressHtml;
    }

    protected String preConvert(String wiki, ConverterOptions options) {
        if(options.getDisableConfluenceMacros() != null) {
            for (String macro : options.getDisableConfluenceMacros()) {
                MacroDisabler disabler = MacroDisabler.forMacro(macro);
                wiki = disabler.disableMacro(wiki);
            }
        }
        return wiki;
    }

    protected String convertInternal(PageContext pageContext, String wiki, @SuppressWarnings("unused") ConverterOptions options) {
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

    protected String postConvert(String confluenceHtml, ConverterOptions options) {

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

        if(options.isOptimizeForRDP()) {
            AnchorTransformer t = new AnchorTransformer();
            body.traverse(t);
            HeadingsCollector collector = new HeadingsCollector();
            body.traverse(collector);
            String header = new RevueDePresseHelper().generateHeader(collector.getHeadings());
            html = header + html;
        }

        return html;
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
        visitors.add(new CodeMacroConverter());
        visitors.add(new SyntaxHighlighterConverter());
        if(options.isConvertCdata()) {
            visitors.add(new CdataStripper());
        }
        if(options.getResourcesBaseUrl() != null) {
            visitors.add(new UrlConverter(options.getResourcesBaseUrl()));
        }
        if(options.getAttachmentsMap() != null) {
            visitors.add(new AttachmentsProcessor(options.getAttachmentsMap()));
        }
        //regular visitors
        visitors.add(new CssClassNameCleaner());
        visitors.add(new EmptySpanStripper());
        return visitors;
    }

    protected String serialize(TagNode body, CleanerProperties properties) {
        //does not work very well
        //PrettyHtmlSerializer serializer = new PrettyHtmlSerializer(cleaner.getProperties());
        SimpleHtmlSerializer serializer = new SimpleHtmlSerializer(properties);
        try {
            return serializer.getAsString(body, true);
        } catch (IOException e) {
            // should not occur with string writers
            return null;
        }
    }

}