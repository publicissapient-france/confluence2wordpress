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
package fr.dutra.confluence2wordpress.util;



/**
 * @author Alexandre Dutra
 *
 */
public class StringUtils {

    /**
     * Determines whether a text contains only whitespace, according to three different definitions:
     * - Java
     * - Unicode (category Z)
     * - W3C / HTML 4 spec
     * @see "http://stackoverflow.com/questions/1822772/java-regular-expression-to-match-all-whitespace-characters"
     * @see "http://www.w3.org/TR/html401/struct/text.html#h-9.1"
     * @param text
     * @return
     */
    public static boolean isWhitespace(CharSequence text){
    	
    	if(text == null || text.length() == 0) return true;
    	
    	for(int i = 0; i < text.length(); i++) {
    		
    		char c = text.charAt(i);
    		
    		switch(c) {
    		
    			// Java Whitespace
	    		case '\u0009':// HORIZONTAL TABULATION.
	    		case '\n':    // LINE FEED.
	        	case '\u000B':// VERTICAL TABULATION.
	        	case '\u000C':// FORM FEED.
	        	case '\r':    // CARRIAGE RETURN.
	        	case '\u001C':// FILE SEPARATOR.
	        	case '\u001D':// GROUP SEPARATOR.
	        	case '\u001E':// RECORD SEPARATOR.
	        	case '\u001F':// UNIT SEPARATOR.
	        	
	        	//Unicode Zs
            	case '\u0020': // SPACE	
            	case '\u00A0': // NO-BREAK SPACE	 
            	case '\u1680': // OGHAM SPACE MARK
            	case '\u180E': // MONGOLIAN VOWEL SEPARATOR	᠎
            	case '\u2000': // EN QUAD	 
            	case '\u2001': // EM QUAD	 
            	case '\u2002': // EN SPACE	 
            	case '\u2003': // EM SPACE	 
            	case '\u2004': // THREE-PER-EM SPACE	 
            	case '\u2005': // FOUR-PER-EM SPACE	 
            	case '\u2006': // SIX-PER-EM SPACE	 
            	case '\u2007': // FIGURE SPACE	 
            	case '\u2008': // PUNCTUATION SPACE	 
            	case '\u2009': // THIN SPACE	 
            	case '\u200A': // HAIR SPACE	 
            	case '\u202F': // NARROW NO-BREAK SPACE	 
            	case '\u205F': // MEDIUM MATHEMATICAL SPACE	 
            	case '\u3000': // IDEOGRAPHIC SPACE
            	
            	//Unicode Zl
            	case '\u2028': // LINE SEPARATOR
            	
            	//Unicode Zp
        		case '\u2029': // PARAGRAPH SEPARATOR

            	//HTML whitespace
        		case '\u200B': //Zero-width space (&#x200B;)
            
        			break;
		
        		default:
        			return false;
    		}

    	}
    	//this is equivalent:
    	//return text.matches("[\\p{Z}\\p{javaWhitespace}case \u200B]+");
		return true;
    }

}