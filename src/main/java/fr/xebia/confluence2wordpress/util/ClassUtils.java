package fr.xebia.confluence2wordpress.util;

import java.util.HashMap;
import java.util.Map;


public final class ClassUtils {

    private static final Map<Class<?>, Class<?>> WRAPPERS = new HashMap<Class<?>, Class<?>>();

    static {
        WRAPPERS.put(byte.class,    Byte.class);
        WRAPPERS.put(short.class,   Short.class);
        WRAPPERS.put(char.class,    Character.class);
        WRAPPERS.put(int.class,     Integer.class);
        WRAPPERS.put(long.class,    Long.class);
        WRAPPERS.put(float.class,   Float.class);
        WRAPPERS.put(double.class,  Double.class);
        WRAPPERS.put(boolean.class, Boolean.class);
    }

    public static Class<?> getPrimitiveWrapperType(Class<?> primitiveType){
        if(! primitiveType.isPrimitive()){
            throw new IllegalArgumentException("Not a primitive type: " + primitiveType);
        }
        return WRAPPERS.get(primitiveType);
    }
    
    private ClassUtils() {
        // TODO Auto-generated constructor stub
    }

}
