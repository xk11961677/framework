/*
 * The MIT License (MIT)
 * Copyright © 2019 <sky>
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
package com.sky.framework.validator.callback;

import com.baidu.unbiz.fluentvalidator.ValidateCallback;
import com.baidu.unbiz.fluentvalidator.ValidationError;
import com.baidu.unbiz.fluentvalidator.Validator;
import com.baidu.unbiz.fluentvalidator.validator.element.ValidatorElementList;
import com.sky.framework.validator.ValidatorProperties;
import com.sky.framework.validator.exception.ValidateException;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author
 */
public class ValidateResultCallback implements ValidateCallback {

    @Resource
    private ValidatorProperties validatorProperties;

    /**
     * 所有验证完成并且成功后
     *
     * @param validatorElementList 验证器list
     */
    @Override
    public void onSuccess(ValidatorElementList validatorElementList) {
    }

    /**
     * 所有验证步骤结束，发现验证存在失败后
     *
     * @param validatorElementList 验证器list
     * @param errors               验证过程中发生的错误
     */
    @Override
    public void onFail(ValidatorElementList validatorElementList, List<ValidationError> errors) {
        ValidationError validationError = errors.get(0);
        throw new ValidateException(validationError.getErrorMsg(), validationError.getField(), validationError.getErrorCode(), validationError.getInvalidValue());
    }

    /**
     * 执行验证过程中发生了异常后
     *
     * @param validator 验证器
     * @param e         异常
     * @param target    正在验证的对象
     * @throws Exception
     */
    @Override
    public void onUncaughtException(Validator validator, Exception e, Object target) {
        throw new ValidateException(e, validatorProperties.getErrorMsg(), "", validatorProperties.getErrorCode(), target);
    }
}