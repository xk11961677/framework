package com.sky.framework.common.ding;

import com.sky.framework.common.LogUtil;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import java.io.IOException;

/**
 * @author
 */
@Slf4j
public class DingTalkMessage {

    public static String URL = "";

    private DingTalkMessageBuilder builder;

    public DingTalkMessage(DingTalkMessageBuilder builder) {
        this.builder = builder;
    }

    public void send() {
        String json = builder.build();
        OkHttpClient okHttpClient = new OkHttpClient();
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);
        Request request = new Request.Builder().post(body).url(URL).build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.getMessage();
            }

            @Override
            public void onResponse(Call call, okhttp3.Response response) {
                try {
                    ResponseBody responseBody = response.body();
                    if (responseBody != null) {
                        LogUtil.info(log, responseBody.string());
                    }
                } catch (Exception ignore) {
                }
            }
        });
    }
}
