package com.sky.framework.common.encrypt;

import com.sky.framework.model.dto.MessageReq;

/**
 * @author
 */
public interface Verifier {

    /**
     * 校验
     *
     * @param messageReq 参数
     * @param secret     秘钥
     * @return 返回校验结果，true：成功
     */
    boolean verify(MessageReq messageReq, String secret);
}
