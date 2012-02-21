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
package fr.xebia.confluence2wordpress.util;



public final class EscapeUtils {

    public static String escape(String s) {
        if(s == null || "".equals(s)){
            return "";
        }
        if(shouldEscape(s)){
            StringBuilder sb = new StringBuilder();
            sb.append("\"");
            sb.append(s.replace("\"","\"\""));
            sb.append("\"");
            return sb.toString();
        }
        return s;
    }

    public static String unescape(String s) {
        if(s == null || "".equals(s)){
            return null;
        }
        if(s.startsWith("\"") && s.endsWith("\"")){
            String unescaped = s.substring(1, s.length() - 1);
            if(shouldEscape(unescaped)){
                return unescaped.replace("\"\"", "\"");
            }
        }
        return s;
    }

	public static boolean shouldEscape(String s) {
		return s.contains(",") || s.contains("=") || s.contains("\"");
	}

    private EscapeUtils() {
    }

}
