package fr.dutra.xebia.wiki2html.core;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MacroDisabler {

    private static final Map<String, MacroDisabler> INSTANCES = new HashMap<String, MacroDisabler>();

    private final Pattern p;

    public static MacroDisabler forMacro(String name) {
        if( ! INSTANCES.containsKey(name)){
            INSTANCES.put(name, new MacroDisabler(name));
        }
        return INSTANCES.get(name);
    }

    private MacroDisabler(String macro) {
        super();
        this.p = Pattern.compile("\\{" + macro + "(:[^\\}]+)?\\}");
    }

    public String disableMacro(String wiki) {
        Matcher matcher = p.matcher(wiki);
        StringBuffer sb = new StringBuffer();
        boolean start = true;
        while (matcher.find()) {
            if (start) {
                matcher.appendReplacement(sb, "{excerpt:hidden=true}");
            } else {
                matcher.appendReplacement(sb, "{excerpt}");
            }
            start = !start;
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

}
