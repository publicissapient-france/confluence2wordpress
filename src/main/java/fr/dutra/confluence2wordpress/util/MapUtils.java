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
/**
 * 
 */
package fr.dutra.confluence2wordpress.util;

import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.google.common.base.Function;
import com.google.common.base.Splitter;
import com.google.common.collect.Maps;


/**
 * @author Alexandre Dutra
 *
 */
public class MapUtils {

    private static final Function<String, String> TRIM_TO_NULL = new Function<String, String>() {
		@Override
		public String apply(String from) {
			return StringUtils.trimToNull(from);
		}
	};

	public static Map<String, String> split(String str, String entrySep, String keyValueSep) {
        if(StringUtils.isBlank(str)){
            return null;
        }
        Splitter keyValueSplitter = Splitter.on(keyValueSep).trimResults();
        Splitter entrySplitter = Splitter.on(entrySep).trimResults().omitEmptyStrings();
		return Maps.transformValues(entrySplitter.withKeyValueSeparator(keyValueSplitter).split(str), TRIM_TO_NULL);
    }

}
