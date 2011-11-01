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
package fr.xebia.confluence2wordpress.util.collections;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;


/**
 * @author Alexandre Dutra
 *
 */
public class CollectionUtils {

    public static List<String> split(String str, String sep) {
        if(StringUtils.isEmpty(str)){
            return null;
        }
        String[] tokens = StringUtils.splitPreserveAllTokens(str, sep);
        List<String> list = new ArrayList<String>(tokens.length);
        for (String token : tokens) {
            list.add(StringUtils.trimToNull(token));
        }
        return list;
    }

}
