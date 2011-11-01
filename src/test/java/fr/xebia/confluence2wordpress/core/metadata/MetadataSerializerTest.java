package fr.xebia.confluence2wordpress.core.metadata;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.joda.time.format.ISODateTimeFormat;
import org.junit.Test;


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
        assertEquals(Boolean.TRUE, s.deserialize("true", boolean.class, null));
        assertEquals(Boolean.FALSE, s.deserialize("false", boolean.class, null));
        assertEquals(Boolean.TRUE, s.deserialize("true", Boolean.class, null));
        assertEquals(Boolean.FALSE, s.deserialize("false", Boolean.class, null));
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
        assertEquals(expected, s.deserialize(date, Date.class, null));
    }
    
    @Test
    public void testSerializeString() {
        assertEquals("foo", s.serialize("foo"));
    }

    @Test
    public void testSerializeStringArray() {
        assertEquals("\"foo, \"\"bar\",\"bar, \"\"foo\"", s.serialize(Arrays.asList("foo, \"bar","bar, \"foo")));
    }
    
    @Test
    public void testSerializeDecimalArray() {
        assertEquals("-1.0,3.1", s.serialize(Arrays.asList(-1.0,3.1)));
    }
    
    @Test
    public void testDeserializeStringArray() {
        assertEquals(
            Arrays.asList(null, null, "foo, \"bar", null, "bar, \"foo", "foobar", "foo bar", null, null), 
            s.deserialize(",,\"foo, \"\"bar\",,\"bar, \"\"foo\",foobar,foo bar,,", List.class, String.class));
    }

    @Test
    public void testDeserializeDecimalArray() {
        assertEquals(
            Arrays.asList(-1.0,3.1), 
            s.deserialize("-1.0,3.1", List.class, Double.class));
    }
}
