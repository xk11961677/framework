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

import com.sky.framework.common.LogUtils;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.ObjectUtils;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author
 */
@Slf4j
public class RedisUtils {

    @Setter
    private static RedisTemplate<String, Object> redisTemplate;

    @Setter
    private static StringRedisTemplate stringRedisTemplate;

    /**
     * 指定缓存失效时间
     *
     * @param key  key值
     * @param time 缓存时间
     */
    public static void expire(String key, long time) {
        if (time > 0) {
            redisTemplate.expire(key, time, TimeUnit.SECONDS);
        } else {
            LogUtils.info(log, "设置的时间不能为0或者小于0！！");
        }
    }

    /**
     * 获取缓存失效时间
     *
     * @param key  key值
     */
    public static Long getExpire(String key, TimeUnit unit) {
        Long expire = redisTemplate.getExpire(key, unit);
        return expire;
    }

    /**
     * 指定key增加数值
     *
     * @param key
     * @param incr
     * @return
     */
    public static Long increment(String key, long incr) {
        return redisTemplate.opsForValue().increment(key, incr);
    }

    /**
     * 判断key是否存在
     *
     * @param key 传入ke值
     * @return true 存在  false  不存在
     */
    public static Boolean existsKey(String key) {
        return redisTemplate.hasKey(key);
    }

    /**
     * 判断key存储的值类型
     *
     * @param key key值
     * @return DataType[string、list、set、zset、hash]
     */
    public static DataType typeKey(String key) {
        return redisTemplate.type(key);
    }

    /**
     * 删除指定的一个数据
     *
     * @param key key值
     * @return true 删除成功，否则返回异常信息
     */
    public static Boolean deleteKey(String key) {
        try {
            return redisTemplate.delete(key);
        } catch (Exception e) {
            LogUtils.info(log, "删除失败！" + e.getMessage(), e);
        }
        return false;
    }

    /**
     * 删除多个数据
     *
     * @param keys key的集合
     * @return true删除成功，false删除失败
     */
    public static Boolean deleteKey(Collection<String> keys) {
        return redisTemplate.delete(keys) != 0;
    }

    //-------------------- String ----------------------------

    /**
     * 普通缓存放入
     *
     * @param key   键值
     * @param value 值
     * @return true成功 要么异常
     */
    public static Boolean setString(String key, Object value) {
        try {
            stringRedisTemplate.opsForValue().set(key, ObjectUtils.toString(value));
            return true;
        } catch (Exception e) {
            LogUtils.info(log, "插入缓存失败！" + e.getMessage(), e);
        }
        return false;
    }

    /**
     * 存入缓存 如果不存在则存入
     * @param key
     * @param value
     * @param time
     * @return
     */
    public static Boolean setIfAbsentString(String key,Object value, long time) {
        try {
            redisTemplate.opsForValue().setIfAbsent(key,value,time,TimeUnit.SECONDS);
            return true;
        } catch (Exception e) {
            LogUtils.info(log, "插入缓存失败！" + e.getMessage(), e);
        }
        return false;
    }

    /**
     * 普通缓存获取
     *
     * @param key 键
     * @return 值
     */
    public static Object getString(String key) {
        return key == null ? null : stringRedisTemplate.opsForValue().get(key);
    }

    /**
     * 设置缓存存在时间
     *
     * @param key   key值
     * @param value value值
     * @param time  时间 秒为单位
     * @return 成功返回true，失败返回异常信息
     */
    public static boolean setString(String key, Object value, long time) {
        try {
            stringRedisTemplate.opsForValue().set(key, ObjectUtils.toString(value), time, TimeUnit.SECONDS);
            return true;
        } catch (Exception e) {
            LogUtils.info(log, "插入缓存失败！" + e.getMessage(), e);
        }
        return false;
    }

    //----------------------------- list ------------------------------

    /**
     * 将list放入缓存
     *
     * @param key   key的值
     * @param value 放入缓存的数据
     * @return true 代表成功，否则返回异常信息
     */
    public static Boolean setList(String key, Object value) {
        try {
            redisTemplate.opsForList().rightPush(key, value);
            return true;
        } catch (Exception e) {
            LogUtils.info(log, "插入List缓存失败！" + e.getMessage(), e);
        }
        return false;
    }

    /**
     * 将Object数据放入List缓存，并设置时间
     *
     * @param key   key值
     * @param value 数据的值
     * @param time  缓存的时间
     * @return true插入成功，否则返回异常信息
     */
    public static Boolean setList(String key, Object value, long time) {
        try {
            if (time > 0) {
                redisTemplate.opsForList().rightPush(key, value);
                expire(key, time);
                return true;
            }
            return false;
        } catch (Exception e) {
            LogUtils.info(log, "插入List缓存失败！" + e.getMessage(), e);
        }
        return false;
    }

    /**
     * 将list集合放入List缓存，并设置时间
     *
     * @param key   key值
     * @param value 数据的值
     * @param time  缓存的时间
     * @return true插入成功，否则返回异常信息
     */
    public static Boolean setListAll(String key, Object value, long time) {
        try {
            if (time > 0) {
                redisTemplate.opsForList().rightPushAll(key, value);
                expire(key, time);
                return true;
            }
            return false;
        } catch (Exception e) {
            LogUtils.info(log, "插入List缓存失败！" + e.getMessage(), e);
        }
        return false;
    }

    /**
     * 根据索引获取缓存List中的内容
     *
     * @param key   key的值
     * @param start 索引开始
     * @param end   索引结束 0 到 -1代表所有值
     * @return 返回数据
     */
    public static List<Object> getList(String key, long start, long end) {
        try {
            return redisTemplate.opsForList().range(key, start, end);
        } catch (Exception e) {
            LogUtils.info(log, "获取缓存List中的内容失败了！" + e.getMessage(), e);
        }
        return null;
    }

    /**
     * 删除List缓存中多个list数据
     *
     * @param key   key值
     * @param count 移除多少个
     * @param value 可以传null  或者传入存入的Value的值
     * @return 返回删除了多少个
     */
    public static long deleteListIndex(String key, long count, Object value) {
        try {
            return redisTemplate.opsForList().remove(key, count, value);
        } catch (Exception e) {
            LogUtils.info(log, "删除List中的内容失败了！" + e.getMessage(), e);
        }
        return 0L;
    }

    /**
     * 获取List缓存的数据
     *
     * @param key key值
     * @return 返回长度
     */
    public static long getListSize(String key) {
        try {
            return redisTemplate.opsForList().size(key);
        } catch (Exception e) {
            LogUtils.info(log, "获取List长度失败！" + e.getMessage(), e);
        }
        return 0L;
    }


    public static List<Object> multiGet(Set<String> keys) {
        List<Object> values = redisTemplate.opsForValue().multiGet(keys);
        return values;
    }

}
