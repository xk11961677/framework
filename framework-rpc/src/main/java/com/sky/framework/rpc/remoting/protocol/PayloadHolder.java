package com.sky.framework.rpc.remoting.protocol;

/**
 * @author
 */
public abstract class PayloadHolder {

    private byte serializerCode;

    private byte[] bytes;

    public byte serializerCode() {
        return serializerCode;
    }

    public byte[] bytes() {
        return bytes;
    }


    public void bytes(byte serializerCode,byte[] bytes) {
        this.bytes = bytes;
        this.serializerCode = serializerCode;
    }
}
