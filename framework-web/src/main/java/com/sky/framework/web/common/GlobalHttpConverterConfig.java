package com.sky.framework.web.common;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.sky.framework.web.constant.GlobalConstant;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;

import java.util.ArrayList;
import java.util.List;

/**
 * 配置FastJson为Spring Boot默认JSON解析框架
 *
 * @author
 */
@Configuration
@ConditionalOnProperty(value = GlobalConstant.GLOBAL_HTTP_CONVERTER, matchIfMissing = true)
public class GlobalHttpConverterConfig {

    @Bean
    public HttpMessageConverters fastJsonHttpMessageConverters() {
        //fastjson 新版检查 mediaType ,解决所有请求方法上添加 mediaType
        List<MediaType> supportedMediaTypes = new ArrayList<>();
        supportedMediaTypes.add(MediaType.APPLICATION_JSON);
        supportedMediaTypes.add(MediaType.APPLICATION_JSON_UTF8);
        supportedMediaTypes.add(MediaType.APPLICATION_ATOM_XML);
        supportedMediaTypes.add(MediaType.APPLICATION_FORM_URLENCODED);
        supportedMediaTypes.add(MediaType.APPLICATION_OCTET_STREAM);
        supportedMediaTypes.add(MediaType.APPLICATION_PDF);
        supportedMediaTypes.add(MediaType.APPLICATION_RSS_XML);
        supportedMediaTypes.add(MediaType.APPLICATION_XHTML_XML);
        supportedMediaTypes.add(MediaType.APPLICATION_XML);
        supportedMediaTypes.add(MediaType.IMAGE_GIF);
        supportedMediaTypes.add(MediaType.IMAGE_JPEG);
        supportedMediaTypes.add(MediaType.IMAGE_PNG);
        supportedMediaTypes.add(MediaType.TEXT_EVENT_STREAM);
        supportedMediaTypes.add(MediaType.TEXT_HTML);
        supportedMediaTypes.add(MediaType.TEXT_MARKDOWN);
        supportedMediaTypes.add(MediaType.TEXT_PLAIN);
        supportedMediaTypes.add(MediaType.TEXT_XML);
        supportedMediaTypes.add(MediaType.MULTIPART_FORM_DATA);
        supportedMediaTypes.add(MediaType.ALL);

        //创建配置类
        FastJsonConfig fastJsonConfig = new FastJsonConfig();
        //修改配置返回内容的过滤
        //WriteNullListAsEmpty  ：List字段如果为null,输出为[],而非null
        //WriteNullStringAsEmpty ： 字符类型字段如果为null,输出为"",而非null
        //DisableCircularReferenceDetect ：消除对同一对象循环引用的问题，默认为false（如果不配置有可能会进入死循环）
        //WriteNullBooleanAsFalse：Boolean字段如果为null,输出为false,而非null
        //WriteMapNullValue：是否输出值为null的字段,默认为false
        //SerializerFeature.PrettyFormat : 是否格式化
        fastJsonConfig.setSerializerFeatures(SerializerFeature.DisableCircularReferenceDetect);

        FastJsonHttpMessageConverter fastConverter = new FastJsonHttpMessageConverter();
        fastConverter.setSupportedMediaTypes(supportedMediaTypes);
        fastConverter.setFastJsonConfig(fastJsonConfig);
        HttpMessageConverter<?> converter = fastConverter;
        return new HttpMessageConverters(converter);
    }
}