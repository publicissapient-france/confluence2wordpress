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

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.google.common.base.Function;
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
        if(StringUtils.isEmpty(str)){
            return null;
        }
        String[] tokens = StringUtils.splitPreserveAllTokens(str, entrySep);
        Map<String, String> map = new HashMap<String, String>(tokens.length);
        for (String token : tokens) {
            String[] keyValue = StringUtils.splitPreserveAllTokens(token, keyValueSep, 2);
            String key = keyValue.length > 0 ? StringUtils.trimToNull(keyValue[0]) : null;
            String value = keyValue.length > 1 ? StringUtils.trimToNull(keyValue[1]) : null;
            map.put(key, value);
        }
        return map;
    }

    public static <K> Map<K,String> trimValues(Map<K, String> map) {
		return Maps.transformValues(map, TRIM_TO_NULL);
    }

}
