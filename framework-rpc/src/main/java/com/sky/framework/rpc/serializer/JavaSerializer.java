package com.sky.framework.rpc.serializer;



import com.sky.framework.rpc.common.enums.SerializeEnum;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

/**
 * @author
 */
public class JavaSerializer implements ObjectSerializer {
    @Override
    public byte[] serialize(Object obj) throws RuntimeException {
        try (ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream()) {
            ObjectOutput objectOutput = new ObjectOutputStream(arrayOutputStream);
            objectOutput.writeObject(obj);
            objectOutput.flush();
            objectOutput.close();
            return arrayOutputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("JAVA serialize error " + e.getMessage());
        }
    }

    @Override
    public <T> T deSerialize(byte[] param, Class<T> clazz) throws RuntimeException {
        ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(param);
        try {
            ObjectInput input = new ObjectInputStream(arrayInputStream);
            return (T) input.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("JAVA deSerialize error " + e.getMessage());
        }
    }

    /**
     * 设置scheme
     *
     * @return scheme 命名
     */
    @Override
    public String getScheme() {
        return SerializeEnum.JDK.getSerialize();
    }
}
