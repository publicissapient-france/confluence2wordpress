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
package fr.dutra.confluence2wordpress.core.metadata;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;

import org.joda.time.format.ISODateTimeFormat;
import org.junit.Test;

import com.google.common.collect.Maps;

import fr.dutra.confluence2wordpress.core.metadata.MetadataSerializer;


public class MetadataSerializerTest {

    private MetadataSerializer s = new MetadataSerializer();
    
    @Test
    public void testSerializeBoolean() {
        assertEquals("true", s.serialize(true));
        assertEquals("false", s.serialize(false));
        assertEquals("true", s.serialize(Boolean.TRUE));
        assertEquals("false", s.serialize(Boolean.FALSE));
    }

    @Test
    public void testDeserializeBoolean() {
        assertEquals(Boolean.TRUE, s.deserialize("true", boolean.class));
        assertEquals(Boolean.FALSE, s.deserialize("false", boolean.class));
        assertEquals(Boolean.TRUE, s.deserialize("true", Boolean.class));
        assertEquals(Boolean.FALSE, s.deserialize("false", Boolean.class));
    }

    @Test
    public void testSerializeInteger() {
        assertEquals("0", s.serialize(0));
        assertEquals("0", s.serialize(Integer.valueOf(0)));
        assertEquals("-1230", s.serialize(-1230));
        assertEquals("-1230", s.serialize(Integer.valueOf("-1230")));
    }

    @Test
    public void testSerializeDecimal() {
        assertEquals("-1.23", s.serialize(-1.23));
        assertEquals("-1.23", s.serialize(Float.valueOf("-1.23")));
    }

    @Test
    public void testSerializeDate() {
        Date date = new Date();
        String expected = ISODateTimeFormat.dateTime().print(date.getTime());
        assertEquals(expected, s.serialize(date));
    }
    
    @Test
    public void testDeserializeDate() {
        String date = "2011-10-18T17:40:18.886+02:00";
        Date expected = ISODateTimeFormat.dateTime().parseDateTime(date).toDate();
        assertEquals(expected, s.deserialize(date, Date.class));
    }
    
    @Test
    public void testSerializeString() {
        assertEquals("foo", s.serialize("foo"));
    }

    @Test
    public void testSerializeStringArray() {
        assertEquals("\"foo, \"\"bar\",\"bar, \"\"foo\"", s.serializeList(Arrays.asList("foo, \"bar","bar, \"foo")));
    }
    
    @Test
    public void testSerializeDecimalArray() {
        assertEquals("-1.0,3.1", s.serializeList(Arrays.asList(-1.0,3.1)));
    }
    
    @Test
    public void testDeserializeStringArray() {
        assertEquals(
            Arrays.asList(null, null, "foo, \"bar", null, "bar, \"foo", "foobar", "foo bar", null, null), 
            s.deserializeList(",,\"foo, \"\"bar\",,\"bar, \"\"foo\",foobar,foo bar,,", String.class));
    }

    @Test
    public void testDeserializeDecimalArray() {
        assertEquals(
            Arrays.asList(-1.0,3.1), 
            s.deserializeList("-1.0,3.1", Double.class));
    }
    
    @Test
    public void testSerializeMap() {
    	HashMap<String,String> map = Maps.newLinkedHashMap();
    	map.put("foo, =\"bar", "bar, =\"foo");
    	map.put("foo2, =\"bar", "bar, =\"foo2");
                   // K---------------K V---------------V K----------------K V----------------V 	
        assertEquals("\"foo, =\"\"bar\"=\"bar, =\"\"foo\",\"foo2, =\"\"bar\"=\"bar, =\"\"foo2\"", s.serializeMap(map));
    }
    
    @Test
    public void testSerializeMapWithEmptyValues() {
    	HashMap<String,String> map = Maps.newLinkedHashMap();
    	map.put("k1", "");
    	map.put("k2", null);
        assertEquals("k1=,k2=", s.serializeMap(map));
    }
    
    @Test
    public void testDeserializeMap() {
    	HashMap<String,String> map = Maps.newLinkedHashMap();
    	map.put("foo, =\"bar", "bar, =\"foo");
    	map.put("foo2, =\"bar", "bar, =\"foo2");
                          // K---------------K V---------------V K----------------K V----------------V 	
        String serialized = "\"foo, =\"\"bar\"=\"bar, =\"\"foo\",\"foo2, =\"\"bar\"=\"bar, =\"\"foo2\"";
		assertEquals(map, s.deserializeMap(serialized, String.class, String.class));
    }
    
    @Test
    public void testDeserializeMapWithEmptyValues() {
    	HashMap<String,String> map = Maps.newLinkedHashMap();
    	map.put("k1", null);
        String serialized = " k1 = ";
		assertEquals(map, s.deserializeMap(serialized, String.class, String.class));
    }
    
    @Test
    public void testDeserializeDecimalMap() {
    	HashMap<String,Double> map = Maps.newLinkedHashMap();
    	map.put("foo, =\"bar", 0.12);
    	map.put("foo2, =\"bar", -3.5);
                          // K---------------K V--V K----------------K V--V 	
        String serialized = "\"foo, =\"\"bar\"=0.12,\"foo2, =\"\"bar\"=-3.5";
		assertEquals(map, s.deserializeMap(serialized, String.class, Double.class));
    }
}
