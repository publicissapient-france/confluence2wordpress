package fr.xebia.confluence2wordpress.core.metadata;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.lang.StringUtils;


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

    public String serialize(Object value){
        if(value == null){
            return null;
        }
        if(value instanceof String){
            return (String) value;
        }
        if(value instanceof List){
            return serializeList((List<?>) value);
        }
        return ConvertUtils.convert(value);
    }

    @SuppressWarnings("unchecked")
    public <T> T deserialize(String value, Class<T> destinationType, Class<?> componentType){
        if(value == null){
            return null;
        }
        if(destinationType.equals(String.class)){
            return (T) value;
        }
        if(List.class.isAssignableFrom(destinationType)){
            return (T) deserializeList(value, componentType);
        }
        return (T) ConvertUtils.convert(value, destinationType);
    }

    private String serializeList(List<?> coll) {
        List<String> items = new ArrayList<String>();
        for (Object item : coll) {
            items.add(escape(ConvertUtils.convert(item)));
        }
        return StringUtils.trimToNull(StringUtils.join(items, ','));
    }

    @SuppressWarnings("unchecked")
    private <T> List<T> deserializeList(String value, Class<T> componentType) {
        String[] items = value.split(CSV_PATTERN, -1);
        for (int i = 0; i < items.length; i++) {
            items[i] = unescape(items[i]);
        }
        if(String.class.equals(componentType)){
            //return modifiable list
            return new ArrayList<T>((List<T>) Arrays.asList(items));
        }
        List<T> coll = new ArrayList<T>(items.length);
        for (int i = 0; i < items.length; i++) {
            coll.add((T) ConvertUtils.convert(items[i], componentType));
        }
        return coll;
    }
    
    private String escape(String s) {
        if(s == null){
            return "";
        }
        if(s.contains(",") || s.contains("|") || s.contains("\"")){
            StringBuilder sb = new StringBuilder();
            sb.append("\"");
            sb.append(s.replace("\"","\"\""));
            sb.append("\"");
            return sb.toString();
        }
        return s;
    }

    private String unescape(String s) {
        if("".equals(s)){
            return null;
        }
        if(s.startsWith("\"") && s.endsWith("\"")){
            String unescaped = s.substring(1, s.length() - 1);
            if(unescaped.contains(",") || unescaped.contains("\"\"")){
                return unescaped.replace("\"\"", "\"");
            }
        }
        return s;
    }

}
