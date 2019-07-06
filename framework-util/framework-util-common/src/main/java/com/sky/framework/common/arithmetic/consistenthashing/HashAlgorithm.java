package com.sky.framework.common.arithmetic.consistenthashing;


import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author
 */
public enum HashAlgorithm {

    /**
     * MD5-based hash algorithm used by ketama.
     */
    KETAMA_HASH;

    /**
     * 将16字节的数组拆分成4个字节的int值
     * 通过nTime 进行拆分
     * 通过位移操作产生 long型值
     * 此值为对应node 的key
     *
     * @param digest
     * @param nTime
     * @return,通过位移计算后的long值
     */
    public long hash(byte[] digest, int nTime) {
        long rv = ((long) (digest[3 + nTime * 4] & 0xFF) << 24)
                | ((long) (digest[2 + nTime * 4] & 0xFF) << 16)
                | ((long) (digest[1 + nTime * 4] & 0xFF) << 8)
                | (digest[0 + nTime * 4] & 0xFF);

        return rv & 0xffffffffL; /* Truncate to 32-bits */
    }

    /**
     * 获取 MD5加密后的key 的字节数组
     * <p>
     *
     * @return，16字节的字节数组
     */
    public byte[] computeMd5(String k) {
        MessageDigest md5;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5 not supported", e);
        }
        md5.reset();
        byte[] keyBytes = null;
        try {
            keyBytes = k.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Unknown string :" + k, e);
        }

        md5.update(keyBytes);
        return md5.digest();
    }
}

