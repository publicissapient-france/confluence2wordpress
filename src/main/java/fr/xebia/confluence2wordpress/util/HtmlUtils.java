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
/**
 * 
 */
package fr.xebia.confluence2wordpress.util;

import org.apache.commons.lang.StringUtils;

/**
 * @author Alexandre Dutra
 *
 */
public class HtmlUtils{

	/**
	 * @see "http://www.w3.org/TR/html401/struct/text.html#h-9.1"
	 * ASCII space (&#x0020;)
	 * ASCII tab (&#x0009;)
	 * carriage return (&#x000D;)
	 * line feed (&#x000A;)
	 * ASCII form feed (&#x000C;)
	 * Zero-width space (&#x200B;)
	 */
    private static final String WHITESPACE = " \t\r\n\u000C\u200B";

	public static String escapeHtml(String text) {
        //better than StringEscapeUtils.escapeXml(text);
        return text.replace("<", "&lt;").replace("&", "&amp;");
    }
    
	public static String escapeQuotes(String text) {
        return text.replace("\"", "&quot;");
    }
	
    public static boolean isHtmlWhitespace(String text){
		return StringUtils.containsOnly(text,WHITESPACE);
    }

    public static String stripCdata(String text){
        if(text.startsWith("<![CDATA[") && text.endsWith("]]>")) {
            text = StringUtils.substringBetween(text, "<![CDATA[", "]]>");
            //need to unescape because even between cdata characters like "<" come escaped
            return escapeHtml(text);
        }
        return text;
    }
}
