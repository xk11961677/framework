package com.sky.framework.rpc.util;


/**
 * @author
 */
public class NumberUtils {

    /**
     * @param bytes
     * @return
     */
    public static int byteArrayToInt(byte[] bytes) {
        return Integer.valueOf(""
                + (((bytes[0] & 0x000000ff) << 24)
                | ((bytes[1] & 0x000000ff) << 16)
                | ((bytes[2] & 0x000000ff) << 8) | (bytes[3] & 0x000000ff)));
    }

    /**
     * @param num
     * @return
     */
    public static byte[] intToByteArray(int num) {
        byte[] bytes = new byte[4];
        bytes[0] = (byte) (num >> 24);
        bytes[1] = (byte) (num >> 16);
        bytes[2] = (byte) (num >> 8);
        bytes[3] = (byte) (num);
        return bytes;
    }
}
