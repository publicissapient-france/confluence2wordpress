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
package fr.xebia.confluence2wordpress.core.converter.preprocessors;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.atlassian.confluence.renderer.MacroManager;
import com.atlassian.renderer.v2.macro.Macro;

import fr.xebia.confluence2wordpress.core.converter.ConverterOptions;


public class IgnoredMacrosPreProcessor implements PreProcessor {

    private MacroManager macroManager;
    
    public IgnoredMacrosPreProcessor(MacroManager macroManager) {
        super();
        this.macroManager = macroManager;
    }

    @Override
    public String preProcess(String wiki, ConverterOptions options) {
    	if(options.getIgnoredConfluenceMacros() != null) {
            for (String macro : options.getIgnoredConfluenceMacros()) {
                boolean hasBody = true;
                Macro enabledMacro = macroManager.getEnabledMacro(macro);
                if(enabledMacro != null){
                    hasBody = enabledMacro.hasBody();
                }
            	Pattern p = Pattern.compile("\\{" + macro + "(:[^\\}]+)?\\}");
            	Matcher matcher = p.matcher(wiki);
                StringBuffer sb = new StringBuffer();
                boolean start = true;
                while (matcher.find()) {
                    if(hasBody){
                        if (start) {
                            matcher.appendReplacement(sb, "{excerpt:hidden=true}");
                        } else {
                            matcher.appendReplacement(sb, "{excerpt}");
                        }
                        start = !start;
                    } else {
                        matcher.appendReplacement(sb, "");
                    }
                }
                matcher.appendTail(sb);
                wiki = sb.toString();
            }
        }
    	return wiki;
    }

}
