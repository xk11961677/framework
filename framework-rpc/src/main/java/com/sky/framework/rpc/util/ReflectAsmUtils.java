package com.sky.framework.rpc.util;

import com.esotericsoftware.reflectasm.MethodAccess;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author
 */
public class ReflectAsmUtils {

    public static Map<String, Class> clazzConstainer = new ConcurrentHashMap<>();

    public static Map<Class, MethodAccess> accessConstainer = new ConcurrentHashMap<>();

    public static Map<Class, Object> objectConstainer = new ConcurrentHashMap<>();

    /**
     * @param clazz
     * @param method
     * @param parameterTypes
     * @param arguments
     * @return
     */
    public static Object invoke(String clazz, String method, Class<?>[] parameterTypes, Object[] arguments) {
        Class aClass = clazzConstainer.get(clazz);
        MethodAccess access = accessConstainer.get(aClass);
        Object target = objectConstainer.get(aClass);
        int index = access.getIndex(method, parameterTypes);
        Object result = access.invoke(target, index, arguments);
        return result;
    }
}
