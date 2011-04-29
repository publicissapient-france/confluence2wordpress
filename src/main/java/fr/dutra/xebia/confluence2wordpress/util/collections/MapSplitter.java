/**
 * 
 */
package fr.dutra.xebia.confluence2wordpress.util.collections;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;


/**
 * @author Alexandre Dutra
 *
 */
public class MapSplitter {

    public static Map<String, String> split(String str, String entrySep, String keyValueSep) {
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
}
