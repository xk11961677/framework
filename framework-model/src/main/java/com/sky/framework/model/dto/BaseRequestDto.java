package com.sky.framework.model.dto;

import lombok.Data;

/**
 * 请求实体基础类
 * @author
 */
@SuppressWarnings("serial")
@Data
public class BaseRequestDto extends BaseDto {

    /**
     * 版本号
     */
    private String version;
    /**
     * 应用系统编码
     */
    private String appCode;
    /**
     * 平台编码
     */
    private String sourceCode;
    /**
     * 字符集
     */
    private String charset;
    /**
     * 扩展字段
     */
    private String extParam;
    /**
     * 唯一ID 用于验证幂等性
      */
    private String uniqueId;

    /**
     * access_token  访问token
     */
    private String accessToken;

    /**
     * refresh_token  刷新token
     */
    private String refreshToken;

    /**
     * client_id
     */
    private String clientId;

    /**
     * secret_key
     */
    private String secretKey;
}
