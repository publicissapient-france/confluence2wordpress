package fr.xebia.confluence2wordpress.core.converter.preprocessors;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fr.xebia.confluence2wordpress.core.converter.ConverterOptions;


public class IgnoredMacrosPreProcessor implements PreProcessor {

    @Override
    public String preProcess(String wiki, ConverterOptions options) {
    	if(options.getIgnoredConfluenceMacros() != null) {
            for (String macro : options.getIgnoredConfluenceMacros()) {
            	Pattern p = Pattern.compile("\\{" + macro + "(:[^\\}]+)?\\}");
            	Matcher matcher = p.matcher(wiki);
                StringBuffer sb = new StringBuffer();
                boolean start = true;
                while (matcher.find()) {
                    if (start) {
                        matcher.appendReplacement(sb, "{excerpt:hidden=true}");
                    } else {
                        matcher.appendReplacement(sb, "{excerpt}");
                    }
                    start = !start;
                }
                matcher.appendTail(sb);
                wiki = sb.toString();
            }
        }
    	return wiki;
    }

}
