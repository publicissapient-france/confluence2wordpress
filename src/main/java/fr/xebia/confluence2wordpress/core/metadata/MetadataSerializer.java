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
package fr.xebia.confluence2wordpress.core.metadata;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.joda.convert.StringConvert;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Joiner.MapJoiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import fr.xebia.confluence2wordpress.util.ClassUtils;
import fr.xebia.confluence2wordpress.util.EscapeUtils;


/**
 * @author Alexandre Dutra
 *
 */
public class MetadataSerializer {

    
    /**
     * A pattern that matches commas followed by zero or an even
     * number of occurrences of quoted strings.
     * The guy that invented it is a genius :)
     * @see "http://stackoverflow.com/questions/1757065/java-splitting-a-comma-separated-string-but-ignoring-commas-in-quotes"
     */
    private static final String CSV_PATTERN = ",(?=([^\"]*\"[^\"]*\")*[^\"]*$)";

    private static final String KEY_VALUE_PATTERN = "=(?=([^\"]*\"[^\"]*\")*[^\"]*$)";

    private static final Joiner LIST_JOINER = Joiner.on(',');
    
    private static final MapJoiner MAP_JOINER = Joiner.on(',').withKeyValueSeparator("=");
    
	private static final Function<Object,String> CONVERT_ESCAPE_AND_TRIM = new Function<Object,String>(){
		@Override public String apply(Object input) {
			return convertToString(input);
		}

	};
	    
    public String serialize(Object value){
        if(value == null){
            return null;
        }
        return convertToString(value);
    }

    public <T> T deserialize(String value, Class<T> destinationType){
        if(value == null){
            return null;
        }
        return convertFromString(value, destinationType);
    }
    
    public String serializeList(List<?> list) {
    	if(list == null || list.isEmpty()){
            return null;
        }
    	List<String> items = Lists.transform(list, CONVERT_ESCAPE_AND_TRIM);
        return LIST_JOINER.join(items);
    }
    
    public <T> List<T> deserializeList(String value, Class<T> componentType) {
    	if(value == null){
            return null;
        }
        String[] items = value.split(CSV_PATTERN, -1);
        List<T> coll = Lists.newArrayListWithExpectedSize(items.length);
        for (int i = 0; i < items.length; i++) {
            coll.add(convertFromString(items[i], componentType));
        }
        return coll;
    }

    public String serializeMap(Map<?,?> map) {
    	if(map == null || map.isEmpty()){
            return null;
        }
    	Map<String,String> transformedMap = Maps.newLinkedHashMap();
    	for (Entry<?,?> entry : map.entrySet()) {
            transformedMap.put(CONVERT_ESCAPE_AND_TRIM.apply(entry.getKey()), CONVERT_ESCAPE_AND_TRIM.apply(entry.getValue()));
        }
    	return MAP_JOINER.join(transformedMap);
    }
    
    public <K,V> Map<K,V> deserializeMap(String value, Class<K> keyType, Class<V> valueType){
        if(value == null){
            return null;
        }
        String[] items = value.split(CSV_PATTERN, -1);
        Map<K,V> map = Maps.newLinkedHashMap();
        for (int i = 0; i < items.length; i++) {
        	String input = items[i];
        	String[] tokens = input.split(KEY_VALUE_PATTERN);
        	K convertedKey = convertFromString(tokens[0], keyType);
        	V convertedValue = convertFromString(tokens[1], valueType);
        	map.put(convertedKey, convertedValue);
        }
        return map;
    }

	private static String convertToString(Object input) {
		//we should avoid null values
		return StringUtils.trimToEmpty(EscapeUtils.escape(StringConvert.INSTANCE.convertToString(input)));
	}
	
	@SuppressWarnings("unchecked")
	private static <T> T convertFromString(String input, Class<T> destinationType) {
        if(destinationType.isPrimitive()){
            //joda convert framework cannot handle primitive types
            destinationType = (Class<T>) ClassUtils.getPrimitiveWrapperType(destinationType);
        }
        //we should avoid empty strings
		return StringConvert.INSTANCE.convertFromString(destinationType, EscapeUtils.unescape(StringUtils.trimToNull(input)));
	}
}
