package com.sky.framework.common.sms;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sky.framework.common.LogUtil;
import com.sky.framework.common.encrypt.Md5Util;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author
 */
@Slf4j
public class SMSDianJiUtil {

    /**
     * 发送模板短信
     *
     * @param mobiles
     * @return
     */
    public static boolean sendMessage(String mobiles, String template, Object... Object) {
        try {
            String content = String.format(template, Object);
            return dianJiSms(mobiles, content) == 0;
        } catch (Exception e) {
            LogUtil.error(log, "模板转换异常:{} mobiles :{}", e.getMessage(), mobiles);
        }
        return false;
    }

    /**
     * 发送短信
     *
     * @param mobiles
     * @param content
     * @return
     */
    public static boolean sendMessage(String mobiles, String content) {
        return dianJiSms(mobiles, content) == 0;
    }

    /**
     * 点集短信接口
     *
     * @return
     */
    public static int dianJiSms(String mobile, String content) {
        CloseableHttpClient httpclient = null;
        CloseableHttpResponse httpResponse = null;
        HttpPost httpPost = null;
        try {
            LogUtil.info(log, String.format("dianJiSms start. mobile=%s,content=%s", mobile, content));

            String sendsmsurl = "http://sapi.appsms.cn:8088/msgHttp/json/mt";

            String account = "";

            String password = "";

            String resultContent = "";
            httpclient = HttpClients.createDefault();
            httpPost = new HttpPost(sendsmsurl);
            List<BasicNameValuePair> formparams = new ArrayList<BasicNameValuePair>();
            long timestamps = System.currentTimeMillis();
            formparams.add(new BasicNameValuePair("account", account));
            formparams
                    .add(new BasicNameValuePair("password", Md5Util.getMD532Str(password + mobile + timestamps)));
            formparams.add(new BasicNameValuePair("mobile", mobile));
            formparams.add(new BasicNameValuePair("content", content));
            formparams.add(new BasicNameValuePair("timestamps", timestamps + ""));
            UrlEncodedFormEntity uefEntity;
            long start = System.currentTimeMillis();
            uefEntity = new UrlEncodedFormEntity(formparams, "UTF-8");
            httpPost.setEntity(uefEntity);
            httpResponse = httpclient.execute(httpPost);
            HttpEntity entity = httpResponse.getEntity();
            log.info(String.format("dianJiSendSms url = %s,statusLine = %s", sendsmsurl,
                    httpResponse.getStatusLine().toString()));
            if (entity != null) {
                resultContent = EntityUtils.toString(entity, "UTF-8");
            }
            log.info("调用点集发送短信接口cost=" + (System.currentTimeMillis() - start) + ", result="
                    + String.format("resultContent = %s", resultContent));

            JSONObject json = JSON.parseObject(resultContent);
            if (json == null) {
                return 1;
            }
            JSONArray array = JSON.parseArray(JSON.toJSONString(json.get("Rets")));
            for (int i = 0; i < array.size(); i++) {
                JSONObject obj = array.getJSONObject(i);
                String repcode = obj.getString("Rspcode");
                if ("0".equals(repcode)) {
                    return 0;
                }
            }
            return 1;
        } catch (Exception e) {
            LogUtil.error(log, "调用点集发送短信接口报错，error=" + e.getMessage(), e);
        } finally {
            try {
                if (httpResponse != null) {
                    httpResponse.close();
                }
                if (httpclient != null) {
                    httpclient.close();
                }
                if (httpPost != null) {
                    httpPost.releaseConnection();
                }
            } catch (IOException e) {
                LogUtil.error(log, "调用点集发送短信接口报错，error=" + e.getMessage(), e);
            }
        }
        return 1;
    }

}
