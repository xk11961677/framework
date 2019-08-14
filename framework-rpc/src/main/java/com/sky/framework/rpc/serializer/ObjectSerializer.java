package com.sky.framework.rpc.serializer;




/**
 * @author
 */
public interface ObjectSerializer {
    /**
     * 序列化对象
     *
     * @param obj 需要序更列化的对象
     * @return byte []
     * @throws RuntimeException 异常信息
     */
    byte[] serialize(Object obj) throws RuntimeException;


    /**
     * 反序列化对象
     *
     * @param param 需要反序列化的byte []
     * @param clazz java对象
     * @param <T>   泛型支持
     * @return 对象
     * @throws RuntimeException 异常信息
     */
    <T> T deSerialize(byte[] param, Class<T> clazz) throws RuntimeException;


    /**
     * 设置scheme
     *
     * @return scheme 命名
     */
    String getScheme();
}
