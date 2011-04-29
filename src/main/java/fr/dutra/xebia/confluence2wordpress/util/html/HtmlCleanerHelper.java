package fr.dutra.xebia.confluence2wordpress.util.html;

import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.CleanerTransformations;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.SimpleHtmlSerializer;
import org.htmlcleaner.TagNode;
import org.htmlcleaner.TagTransformation;

import fr.dutra.xebia.confluence2wordpress.util.html.visitors.CdataStripper;
import fr.dutra.xebia.confluence2wordpress.util.html.visitors.CodeSnippetConverter;
import fr.dutra.xebia.confluence2wordpress.util.html.visitors.CssClassNameCleaner;
import fr.dutra.xebia.confluence2wordpress.util.html.visitors.HeadingsCollector;
import fr.dutra.xebia.confluence2wordpress.util.html.visitors.UrlConverter;
import fr.dutra.xebia.confluence2wordpress.util.rdp.RevueDePresseHelper;

public class HtmlCleanerHelper {

    public String clean(String html, String htmlBaseUrl, String uploadedFilesBaseUrl, boolean includeRDPHeader) throws Exception {

        //HtmlCleaner is NOT thread-safe
        CleanerProperties properties = getCleanerProperties();
        HtmlCleaner cleaner = new HtmlCleaner(properties);
        CleanerTransformations transformations = getCleanerTransformations();
        cleaner.setTransformations(transformations);

        TagNode root = cleaner.clean(html);
        TagNode body = root.findElementByName("body", false);

        //tree transformations
        body.traverse(new UrlConverter(uploadedFilesBaseUrl));
        body.traverse(new CdataStripper());
        body.traverse(new CodeSnippetConverter());
        body.traverse(new CssClassNameCleaner());

        String result = serialize(body, properties);

        if(includeRDPHeader) {
            HeadingsCollector hc = new HeadingsCollector();
            body.traverse(hc);
            RevueDePresseHelper rdpHelper = new RevueDePresseHelper();
            result = rdpHelper.generateHeader(htmlBaseUrl, hc.getHeadings()) + result;
        }

        return result;
    }

    protected CleanerProperties getCleanerProperties() {
        CleanerProperties properties = new CleanerProperties();
        properties.setOmitXmlDeclaration(true);
        properties.setUseCdataForScriptAndStyle(false);
        properties.setOmitComments(true);
        properties.setUseEmptyElementTags(false);
        return properties;
    }

    protected CleanerTransformations getCleanerTransformations() {
        CleanerTransformations transformations = new CleanerTransformations();

        //tag deletions
        transformations.addTransformation(new TagTransformation("p"));
        transformations.addTransformation(new TagTransformation("br"));

        //tag transformations
        transformations.addTransformation(new TagTransformation("tt", "code", false));
        transformations.addTransformation(new TagTransformation("b", "strong", false));
        transformations.addTransformation(new TagTransformation("del", "strike", false));

        //font -> span
        TagTransformation tt = new TagTransformation("font", "span", true);
        tt.addAttributeTransformation("size");
        tt.addAttributeTransformation("face");
        tt.addAttributeTransformation(
            "style",
            "${style};font-family=${face};font-size=${size};"
        );
        transformations.addTransformation(tt);

        return transformations;
    }

    protected String serialize(TagNode body, CleanerProperties properties) throws Exception {
        //does not work very well
        //PrettyHtmlSerializer serializer = new PrettyHtmlSerializer(cleaner.getProperties());
        SimpleHtmlSerializer serializer = new SimpleHtmlSerializer(properties);
        String result = serializer.getAsString(body, true);
        return result;
    }

}