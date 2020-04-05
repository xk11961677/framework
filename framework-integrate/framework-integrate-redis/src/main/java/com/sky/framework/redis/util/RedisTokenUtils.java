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
package com.sky.framework.redis.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 用户令牌工具类
 *
 * @author
 */
@Slf4j
public class RedisTokenUtils {

    /**
     * 会员token，唯一标识，放入缓存
     */
    public static String TOKEN_KEY = "token:%s";

    /**
     * token时效性暂定120天内有效
     */
    public static Long TOKEN_EXPIRE = 120L;

    /**
     * 加盐
     */
    private static String TOKEN_SALT = "12321321324243";

    /**
     * 验证会员token有效性 token标识该用户的唯一标识，接入方请求时需要传入此token,
     * 作为判断此用户是否已登录依据，若token不存在则不要重新登录后获取此token
     *
     * @author
     */
    public static boolean validUserToken(String channel, String userId, String userToken) {
        // 验证失败
        if (StringUtils.isBlank(userId) || StringUtils.isBlank(userToken) || StringUtils.isBlank(channel)) {
            return false;
        }
        // 使用token:userId 标识用户token对应的key
        String tokenKey = String.format(channel + ":" + TOKEN_KEY, userId);
        String tokenValue = ObjectUtils.toString(RedisUtils.getString(tokenKey));
        // 若Token为空，验证失败
        if (StringUtils.isBlank(tokenValue) || !userToken.equals(tokenValue)) {
            return false;
        } else {
            // 验证token成功
            Long tokenExpireDay = RedisUtils.getExpire(tokenKey, TimeUnit.DAYS);
            // 将永不失效及过期时间小于30天的token进行重置(120天)
            if (tokenExpireDay < 30) {
                RedisUtils.expire(tokenKey, (RedisTokenUtils.TOKEN_EXPIRE * 60 * 60 * 24));
                log.info("重新设置用户token有效期，tokenKey={}, expireDay={}", tokenKey, RedisTokenUtils.TOKEN_EXPIRE);
            }
            log.info("验证token成功,userId={}", userId);
            return true;
        }
    }

    /**
     * 获取用户Token，若失效直接生成新的token
     *
     * @author
     */
    public static String getUserToken(String channel, String userId) {
        // 1.使用token:userId 标识用户token对应的key
        String userToken = ObjectUtils.toString(RedisUtils.getString(String.format(channel + ":" + TOKEN_KEY, userId)));
        if (StringUtils.isBlank(userToken)) {
            // 2.生成token,userId:salt=会员Id+盐
            userToken = DigestUtils.md5Hex(String.format("%s %s", userId, TOKEN_SALT));
            // 3.放入缓存channel:userId:token
            RedisUtils.setString(String.format(channel + ":" + TOKEN_KEY, userId), userToken, (TOKEN_EXPIRE * 60 * 60 * 24));
            // 4.放入缓存token:userId
            RedisUtils.setString(userToken, userId, (TOKEN_EXPIRE * 60 * 60 * 24));
        }

        return userToken;
    }


    public static String getUserToken(String channel, String userId, String oauthToken) {
        // 1.使用token:userId 标识用户token对应的key
        String userToken = ObjectUtils.toString(RedisUtils.getString(String.format(channel + ":" + TOKEN_KEY, userId)));
        if (StringUtils.isBlank(userToken)) {
            // 2.生成token,userId:salt=会员Id+盐
            userToken = oauthToken;
            // 3.放入缓存channel:userId:token
            RedisUtils.setString(String.format(channel + ":" + TOKEN_KEY, userId), userToken, (TOKEN_EXPIRE * 60 * 60 * 24));
            // 4.放入缓存token:userId
            RedisUtils.setString(userToken, userId, (TOKEN_EXPIRE * 60 * 60 * 24));
        }
        return userToken;
    }

    public static String refreshUserToken(String channel, String userId, String oauthToken) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        // 时间戳
        String time_str = sdf.format(new Date());
        TOKEN_SALT += time_str;
        // 1.生成token,userId:salt=会员Id+盐
        String userToken = oauthToken;
        // 2.放入redis缓存,并设置有效期,key=channel:token:userId
        RedisUtils.setString(String.format(channel + ":" + TOKEN_KEY, userId), userToken, (TOKEN_EXPIRE * 60 * 60 * 24));
        // 3.放入缓存token:userId
        RedisUtils.setString(userToken, userId, (TOKEN_EXPIRE * 60 * 60 * 24));
        return userToken;
    }

    /**
     * 刷新Token
     *
     * @author
     */
    public static String refreshUserToken(String channel, String userId) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        // 时间戳
        String time_str = sdf.format(new Date());
        TOKEN_SALT += time_str;
        // 1.生成token,userId:salt=会员Id+盐
        String userToken = DigestUtils.md5Hex(String.format("%s %s", userId, TOKEN_SALT));
        // 2.放入redis缓存,并设置有效期,key=channel:token:userId
        RedisUtils.setString(String.format(channel + ":" + TOKEN_KEY, userId), userToken, (TOKEN_EXPIRE * 60 * 60 * 24));
        // 3.放入缓存token:userId
        RedisUtils.setString(userToken, userId, (TOKEN_EXPIRE * 60 * 60 * 24));
        return userToken;
    }

    /**
     * 注销用户时清除掉用户的token令牌
     *
     * @return
     * @author
     */
    public static void cancelUserToken(String userId) {
        long start = System.currentTimeMillis();

        // 1.初始化需要删除的token key值
        Set<String> tokenKeys = new HashSet<>();
        tokenKeys.add(String.format("token:%s", userId));
        tokenKeys.add(String.format("h5:token:%s", userId));
        tokenKeys.add(String.format("app:token:%s", userId));
        tokenKeys.add(String.format("wechat:token:%s", userId));
        tokenKeys.add(String.format("wiscoapp:token:%s", userId));
        tokenKeys.add(String.format("wiscowechat:token:%s", userId));

        // 2.查询key对应的value集合
        List<Object> valueList = RedisUtils.multiGet(tokenKeys);
        // 3.删除该用户的所有token key
        for (Object tt : tokenKeys) {
            String vv = ObjectUtils.toString(RedisUtils.getString(ObjectUtils.toString(tt)));
            if (vv != null) {
                RedisUtils.deleteKey(vv);
            }
        }
        RedisUtils.deleteKey(tokenKeys);
        log.info("注销用户时清空该用户token，cost={}ms, token key list={},{}", System.currentTimeMillis() - start, tokenKeys,
                valueList);
    }
}
