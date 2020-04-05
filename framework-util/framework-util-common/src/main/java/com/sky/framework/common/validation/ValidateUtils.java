/*
 * The MIT License (MIT)
 * Copyright © 2019-2020 <sky>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the “Software”), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.sky.framework.common.validation;

import com.sky.framework.model.enums.FailureCodeEnum;
import com.sky.framework.model.exception.BusinessException;
import org.apache.commons.lang.StringUtils;
import org.hibernate.validator.HibernateValidator;
import org.hibernate.validator.internal.engine.path.PathImpl;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.*;

/**
 * @author
 */
@SuppressWarnings("all")
public class ValidateUtils {
    /**
     * 校验实体类
     *
     * @param t
     * @return
     */
    public static <T> List<Map> validate(T t) {
        List<Map> errList = new ArrayList<>();
        Map<String, String> errorMap;
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<T>> errorSet = validator.validate(t);
        for (ConstraintViolation<T> c : errorSet) {
            errorMap = new HashMap<>();
            errorMap.put("field", c.getPropertyPath().toString());
            errorMap.put("msg", c.getMessage());
            errList.add(errorMap);
        }
        return errList;
    }

    /**
     * 使用 ValidList 校验List, 返回对应索引和错误消息
     *
     * @param t
     * @param <T>
     * @return
     */
    public static <T> List<Map> validateList(T t) {
        List<Map> errList = new ArrayList<>();
        Map<String, Object> errorMap;
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<T>> errorSet = validator.validate(t);
        for (ConstraintViolation<T> c : errorSet) {
            errorMap = new HashMap<>();
            int index = ((PathImpl) c.getPropertyPath()).getLeafNode().getIndex();
            errorMap.put("index", index);
            errorMap.put("field", c.getPropertyPath().toString());
            errorMap.put("msg", c.getMessage());
            errList.add(errorMap);
        }
        return errList;
    }


    /**
     * 支持分组校验并抛出业务异常
     *
     * @param object
     * @param groups
     */
    public static <T> void validThrow(T t, Class<?>... groups) {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<T>> errorSet = validator.validate(t, groups);
        validThrowCommon(errorSet);
    }

    public static <T> void validThrowFailFast(T t, Class<?>... groups) {
        ValidatorFactory validatorFactory = Validation.byProvider(HibernateValidator.class)
                .configure()
                .addProperty("hibernate.validator.fail_fast", "true")
                .buildValidatorFactory();
        Validator validator = validatorFactory.getValidator();
        Set<ConstraintViolation<T>> errorSet = validator.validate(t, groups);
        validThrowCommon(errorSet);
    }

    private static <T> void validThrowCommon(Set<ConstraintViolation<T>> errorSet) {
        StringBuilder errorMsg = new StringBuilder();
        for (ConstraintViolation<?> violation : errorSet) {
            errorMsg.append(violation.getMessage()).append(",");
        }
        if (!StringUtils.isEmpty(errorMsg.toString())) {
            errorMsg.delete(errorMsg.length() - 1, errorMsg.length());
            throw new BusinessException(FailureCodeEnum.GL990001.getCode(), errorMsg.toString());
        }
    }
}
