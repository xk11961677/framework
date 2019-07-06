package com.sky.framework.common.encrypt;

import com.alibaba.fastjson.JSON;
import com.sky.framework.model.dto.MessageReq;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * @author
 */
@Slf4j
public class DefaultMd5Verifier implements Verifier {

    private static final DefaultEncrypter encrypter = new DefaultEncrypter();

    @Override
    public boolean verify(MessageReq messageReq, String secret) {
        boolean isSame = false;
        try {
            String signCode = messageReq.getSign();
            Map<String, ?> map = JSON.parseObject(JSON.toJSONString(messageReq), Map.class);
            map.remove("sign");
            String clientSign = buildSign(map, secret);
            if(log.isInfoEnabled()) {
                log.info("sign after :{} "+ clientSign);
            }
            isSame = signCode.equals(clientSign);
        } catch (Exception e) {
            log.error("md5 verifier exception :{}" + e.getMessage(), e);
        }
        return isSame;
    }

    /**
     * 构建签名
     *
     * @param paramsMap
     * @param secret
     * @return
     * @throws IOException
     */
    public String buildSign(Map<String, ?> paramsMap, String secret) throws IOException {
        Set<String> keySet = paramsMap.keySet();
        List<String> paramNames = new ArrayList<String>(keySet);

        Collections.sort(paramNames);

        StringBuilder paramNameValue = new StringBuilder();

        for (String paramName : paramNames) {
            paramNameValue.append(paramName).append(paramsMap.get(paramName));
        }

        String source = secret + paramNameValue.toString() + secret;

        return encrypt(source);
    }

    /**
     * 生成md5,全部大写。
     *
     * @param source
     * @return 返回MD5全部大写
     */
    public String encrypt(String source) {
        return encrypter.md5(source).toUpperCase();
    }

}
