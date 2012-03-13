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
package fr.dutra.confluence2wordpress.util;

import java.io.IOException;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

/**
 * @author Alexandre Dutra
 *
 */
public class JsonUtils {
	
	private static final ObjectMapper mapper = new ObjectMapper();

    public static <T> T unmarshal(String json, Class<T> clazz) throws JsonParseException, JsonMappingException, IOException {
    	return mapper.readValue(json, clazz);
    }

    public static <T> String marshal(T bean) throws JsonGenerationException, JsonMappingException, IOException {
    	return mapper.writeValueAsString(bean);
    }

}