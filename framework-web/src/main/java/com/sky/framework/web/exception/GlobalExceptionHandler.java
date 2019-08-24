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
package com.sky.framework.web.exception;

import com.sky.framework.common.LogUtils;
import com.sky.framework.common.ding.DingTalkMessage;
import com.sky.framework.common.ding.DingTalkMessageBuilder;
import com.sky.framework.model.dto.MessageRes;
import com.sky.framework.model.enums.FailureCodeEnum;
import com.sky.framework.model.exception.BusinessException;
import com.sky.framework.threadpool.core.CommonThreadPool;
import com.sky.framework.threadpool.core.DefaultAsynchronousHandler;
import com.sky.framework.web.common.notice.HttpExceptionNotice;
import com.sky.framework.web.constant.WebConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Set;

import static com.sky.framework.web.constant.WebConstants.GLOBAL_EXCEPTION_DT_ENABLE;

/**
 * 全局的的异常拦截器
 *
 * @author
 */
@Slf4j
@ConditionalOnProperty(value = WebConstants.GLOBAL_EXCEPTION_ENABLE, matchIfMissing = true)
@RestControllerAdvice
@SuppressWarnings("all")
public class GlobalExceptionHandler {


    @Value("${spring.application.name:'请填写项目名称'}")
    private String name;

    @Value("${" + GLOBAL_EXCEPTION_DT_ENABLE + ":true}")
    private boolean dingTalk;

    /**
     * 参数非法异常.
     *
     * @param
     * @return
     */
//    @ExceptionHandler(IllegalArgumentException.class)
//    @ResponseStatus(HttpStatus.OK)
//    @ResponseBody
//    public MessageRes illegalArgumentException(IllegalArgumentException e) {
//        LogUtils.error(log, "参数非法异常={}", e.getMessage(), e);
//        return MessageRes.fail(FailureCodeEnum.GL999999.getCode(), e.getMessage());
//    }

    /**
     * 不支持的方法请求类型405
     *
     * @param e
     * @return
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public MessageRes httpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        LogUtils.error(log, "不支持的方法请求类型:{}", e);
        return MessageRes.fail(FailureCodeEnum.GL990007.getCode(), e.getMessage());
    }


    /**
     * 自定义业务异常
     *
     * @param e
     * @return
     */
    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public MessageRes businessException(BusinessException e) {
        LogUtils.error(log, "自定义业务异常:{}", e);
        return MessageRes.fail((e.getCode() == 0 ? FailureCodeEnum.GL999999.getCode() : e.getCode()), e.getMessage());
    }


    /**
     * hibernate validator 参数验证处理(三种异常,支持快速失败或全部错误显示)
     * method(@Validated dto) 非application/json MethodArgumentNotValidException
     * method(@NotBlank(message = "名称不能为空") String name)  ConstraintViolationException
     * method(@Validated dto) application/json BindException
     *
     * @param e
     * @return
     */
    @ExceptionHandler({MethodArgumentNotValidException.class, ConstraintViolationException.class, BindException.class})
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public MessageRes exception(Exception e) {
        LogUtils.debug(log, "业务验证器异常:{}", e);
        BindingResult result = null;
        if (e instanceof MethodArgumentNotValidException) {
            result = ((MethodArgumentNotValidException) e).getBindingResult();
        } else if (e instanceof BindException) {
            result = ((BindException) e).getBindingResult();
        } else if (e instanceof ConstraintViolationException) {
            Set<ConstraintViolation<?>> constraintViolations = ((ConstraintViolationException) e).getConstraintViolations();
            StringBuilder errorMsg = new StringBuilder();
            for (ConstraintViolation<?> violation : constraintViolations) {
                errorMsg.append(violation.getMessage()).append(",");
            }
            errorMsg.delete(errorMsg.length() - 1, errorMsg.length());
            return MessageRes.fail(FailureCodeEnum.GL990001.getCode(), errorMsg.toString());
        }
        if (result != null) {
            StringBuilder errorMsg = new StringBuilder();
            for (ObjectError error : result.getAllErrors()) {
                errorMsg.append(error.getDefaultMessage()).append(",");
            }
            errorMsg.delete(errorMsg.length() - 1, errorMsg.length());
            return MessageRes.fail(FailureCodeEnum.GL990001.getCode(), errorMsg.toString());
        }
        return MessageRes.fail(FailureCodeEnum.GL990001.getCode(), FailureCodeEnum.GL990001.getMsg());
    }


    /**
     * 全局[Exception]异常
     *
     * @param request
     * @param e
     * @return
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public MessageRes exception(HttpServletRequest request, Exception e) {
        LogUtils.error(log, "全局[Exception]异常:{}", e);
        this.asyncSendDingTalk(request, e);
        String message = e.getMessage();
        message = StringUtils.isEmpty(message) ? FailureCodeEnum.GL999999.getMsg() : message;
        return MessageRes.fail(FailureCodeEnum.GL999999.getCode(), message);
    }

    /**
     * 全局[Throwable]异常
     *
     * @param e
     * @return
     */
    @ExceptionHandler(Throwable.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public MessageRes throwable(Throwable e) {
        LogUtils.error(log, "全局[Throwable]异常:{}", e);
        String message = e.getMessage();
        message = StringUtils.isEmpty(message) ? FailureCodeEnum.GL999999.getMsg() : message;
        return MessageRes.fail(FailureCodeEnum.GL999999.getCode(), message);
    }

    /**
     * 发送钉钉通知
     *
     * @param request
     * @param e
     */
    private void asyncSendDingTalk(HttpServletRequest request, Exception e) {
        if (!dingTalk) {
            return;
        }
        CommonThreadPool.execute(new DefaultAsynchronousHandler() {
            @Override
            public Object call() {
                try {
                    String uri = request.getRequestURI();
                    Map<String, String[]> parameterMap = request.getParameterMap();
                    String paramFromApplicationJson = getJsonParameters(request);
                    HttpExceptionNotice httpExceptionNotice = new HttpExceptionNotice(e, null, uri, parameterMap, paramFromApplicationJson);
                    httpExceptionNotice.setProject(name);
                    String text = httpExceptionNotice.createText();
                    DingTalkMessage dingTalkMessage = new DingTalkMessage(new DingTalkMessageBuilder().markdownMessage("异常信息", text));
                    dingTalkMessage.send();
                } catch (Exception e) {
                    LogUtils.error(log, "send ding talk exception:{}", e);
                }
                return null;
            }
        });
    }

    /**
     * 获取application/json格式参数
     *
     * @param request
     * @return
     */
    private String getJsonParameters(HttpServletRequest request) {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream()));
            String line;
            StringBuilder sb = new StringBuilder();
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            String body = sb.toString();
            return body;
        } catch (Exception e) {
            LogUtils.error(log, "get param exception:{}", e);
        }
        return null;
    }
}
