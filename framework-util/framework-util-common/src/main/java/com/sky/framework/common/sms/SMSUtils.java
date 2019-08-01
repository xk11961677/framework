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
//package com.sky.framework.common.sms;
//
//import com.alibaba.fastjson.JSONObject;
//import com.sky.framework.common.LogUtil;
//import com.taobao.api.ApiException;
//import com.taobao.api.DefaultTaobaoClient;
//import com.taobao.api.TaobaoClient;
//import com.taobao.api.request.AlibabaAliqinFcSmsNumSendRequest;
//import com.taobao.api.response.AlibabaAliqinFcSmsNumSendResponse;
//import lombok.extern.slf4j.Slf4j;
//
//
///**
// * 短信发送工具类,阿里大鱼
// * 使用前请初始化短信url key secret signature
// *
// * @author
// */
//@Slf4j
//public class SMSUtil {
//
//    public static String SMS_URL = "http://gw.api.taobao.com/router/rest";
//
//    public static String SMS_APP_KEY = "";
//
//    public static String SMS_APP_SECRET = "";
//
//    public static String SMS_SIGNATURE = "";
//
//    /**
//     * 发送失败,重试发送
//     *
//     * @param template
//     * @param variable
//     * @param mobile
//     * @return
//     */
//    public static boolean sendMsgRetry(String template, String variable, String mobile) {
//        int resultCode = -1;
//        for (int i = 0; i < 2; i++) {
//            resultCode = sendMsg(template, variable, mobile);
//            if (resultCode == 0) {
//                return true;
//            }
//        }
//        return false;
//    }
//
//    /**
//     * @param smsTemplate
//     * @param variable
//     * @param mobiles
//     * @return
//     */
//    public static int sendMsg(String smsTemplate, String variable, String mobiles) {
//        TaobaoClient client = new DefaultTaobaoClient(SMS_URL, SMS_APP_KEY, SMS_APP_SECRET);
//        AlibabaAliqinFcSmsNumSendRequest req = new AlibabaAliqinFcSmsNumSendRequest();
//        req.setExtend("extend");
//        req.setSmsType("normal");
//        req.setSmsFreeSignName(SMS_SIGNATURE);
//        JSONObject variableJson = new JSONObject();
//        variableJson.put("code", String.valueOf(variable));
//        req.setSmsParamString(variableJson.toString());
//        req.setRecNum(mobiles);
//        req.setSmsTemplateCode(smsTemplate);
//
//        AlibabaAliqinFcSmsNumSendResponse rsp;
//
//        try {
//            rsp = client.execute(req);
//            if (rsp.getBody().indexOf("error_response") != -1) {
//                return 1;
//            }
//            JSONObject result = JSONObject.parseObject(rsp.getBody());
//            result = result.getJSONObject("alibaba_aliqin_fc_sms_num_send_response");
//            result = result.getJSONObject("result");
//            LogUtil.info(log, String.format("\nalibaba=  %s", result.toJSONString()));
//            return Integer.parseInt(result.get("err_code").toString());
//        } catch (ApiException e) {
//            LogUtil.error(log, "alidayu sms exception:{}", e);
//        }
//        return 1;
//    }
//
//}
