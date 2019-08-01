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
package com.sky.framework.validator.exception;

import lombok.Data;

/**
 * 验证器外抛异常
 *
 * @author
 */
@Data
public class ValidateException extends RuntimeException {

    private String errorMsg;

    private String field;

    private int errorCode;

    private Object target;

    private Object invalidValue;

    public ValidateException(Exception e) {
        super(e);
    }

    public ValidateException(Exception cause, String errorMsg, String field, int errorCode, Object target) {
        super(errorMsg, cause);
        this.errorMsg = errorMsg;
        this.errorCode = errorCode;
        this.field = field;
        this.target = target;
    }

    public ValidateException(Exception cause, String errorMsg, String field, int errorCode) {
        this(cause, errorMsg, null, errorCode, null);
    }

    public ValidateException(Exception cause, String errorMsg, int errorCode) {
        this(cause, errorMsg, null, errorCode);
    }

    public ValidateException(String errorMsg, String field, int errorCode) {
        this(errorMsg, field, errorCode, null);
    }

    /**
     *
     * @param errorMsg
     * @param field
     * @param errorCode
     * @param invalidValue
     */
    public ValidateException(String errorMsg, String field, int errorCode, Object invalidValue) {
        super(errorMsg);
        this.errorMsg = errorMsg;
        this.errorCode = errorCode;
        this.field = field;
        this.invalidValue = invalidValue;
    }

}
