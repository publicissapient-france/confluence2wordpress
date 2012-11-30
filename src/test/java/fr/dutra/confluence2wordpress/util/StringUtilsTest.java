/**
 * Copyright 2011-2012 Alexandre Dutra
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
package fr.dutra.confluence2wordpress.util;
import static org.fest.assertions.api.Assertions.*;

import org.junit.Test;

public class StringUtilsTest {

    @Test
    public void test_isWhitespace() {
    	
    	assertThat(StringUtils.isWhitespace(null)).isTrue();
    	assertThat(StringUtils.isWhitespace("")).isTrue();
    	
    	assertThat(StringUtils.isWhitespace("\u0009")).isTrue();// HORIZONTAL TABULATION.
    	assertThat(StringUtils.isWhitespace("\n")).isTrue();// LINE FEED.
    	assertThat(StringUtils.isWhitespace("\u000B")).isTrue();// VERTICAL TABULATION.
    	assertThat(StringUtils.isWhitespace("\u000C")).isTrue();// FORM FEED.
    	assertThat(StringUtils.isWhitespace("\r")).isTrue();// CARRIAGE RETURN.
    	assertThat(StringUtils.isWhitespace("\u001C")).isTrue();// FILE SEPARATOR.
    	assertThat(StringUtils.isWhitespace("\u001D")).isTrue();// GROUP SEPARATOR.
    	assertThat(StringUtils.isWhitespace("\u001E")).isTrue();// RECORD SEPARATOR.
    	assertThat(StringUtils.isWhitespace("\u001F")).isTrue();// UNIT SEPARATOR.
    	
    	//Unicode Zs
    	assertThat(StringUtils.isWhitespace("\u0020")).isTrue();// SPACE	
    	assertThat(StringUtils.isWhitespace("\u00A0")).isTrue();// NO-BREAK SPACE	 
    	assertThat(StringUtils.isWhitespace("\u1680")).isTrue();// OGHAM SPACE MARK
    	assertThat(StringUtils.isWhitespace("\u180E")).isTrue();// MONGOLIAN VOWEL SEPARATOR	᠎
    	assertThat(StringUtils.isWhitespace("\u2000")).isTrue();// EN QUAD	 
    	assertThat(StringUtils.isWhitespace("\u2001")).isTrue();// EM QUAD	 
    	assertThat(StringUtils.isWhitespace("\u2002")).isTrue();// EN SPACE	 
    	assertThat(StringUtils.isWhitespace("\u2003")).isTrue();// EM SPACE	 
    	assertThat(StringUtils.isWhitespace("\u2004")).isTrue();// THREE-PER-EM SPACE	 
    	assertThat(StringUtils.isWhitespace("\u2005")).isTrue();// FOUR-PER-EM SPACE	 
    	assertThat(StringUtils.isWhitespace("\u2006")).isTrue();// SIX-PER-EM SPACE	 
    	assertThat(StringUtils.isWhitespace("\u2007")).isTrue();// FIGURE SPACE	 
    	assertThat(StringUtils.isWhitespace("\u2008")).isTrue();// PUNCTUATION SPACE	 
    	assertThat(StringUtils.isWhitespace("\u2009")).isTrue();// THIN SPACE	 
    	assertThat(StringUtils.isWhitespace("\u200A")).isTrue();// HAIR SPACE	 
    	assertThat(StringUtils.isWhitespace("\u202F")).isTrue();// NARROW NO-BREAK SPACE	 
    	assertThat(StringUtils.isWhitespace("\u205F")).isTrue();// MEDIUM MATHEMATICAL SPACE	 
    	assertThat(StringUtils.isWhitespace("\u3000")).isTrue();// IDEOGRAPHIC SPACE
    	
    	//Unicode Zl
    	assertThat(StringUtils.isWhitespace("\u2028")).isTrue();// LINE SEPARATOR
    	
    	//Unicode Zp
		assertThat(StringUtils.isWhitespace("\u2029")).isTrue();// PARAGRAPH SEPARATOR

    	// HTML whitespace
    	assertThat(StringUtils.isWhitespace("\u200B")).isTrue(); //Zero-width space (&#x200B;)
    
    	// Non whitespace
    	assertThat(StringUtils.isWhitespace("a")).isFalse();
    	assertThat(StringUtils.isWhitespace("               a            ")).isFalse();
    	
    }
}