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
