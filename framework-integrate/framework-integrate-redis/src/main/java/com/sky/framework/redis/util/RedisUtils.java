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

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;

import java.util.Collection;
import java.util.List;
import java.util.Map;
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

    //---------------------- common --------------------------

    /**
     * 指定缓存失效时间
     *
     * @param key     key值
     * @param timeout 缓存时间
     */
    public static void expire(String key, long timeout) {
        if (timeout > 0) {
            redisTemplate.expire(key, timeout, TimeUnit.SECONDS);
        } else {
            log.error("设置的时间不能为0或者小于0");
        }
    }

    /**
     * 获取缓存失效时间
     *
     * @param key key值
     */
    public static Long getExpire(String key, TimeUnit unit) {
        return redisTemplate.getExpire(key, unit);
    }

    /**
     * 指定key增加数值,负数则为自减
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
            log.error("删除失败, key:{}, exception:{}", key, e.getMessage(), e);
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
    public static Boolean setString(String key, String value) {
        try {
            stringRedisTemplate.opsForValue().set(key, value);
            return true;
        } catch (Exception e) {
            log.error("插入缓存失败,key:{} , exception:{}", key, e.getMessage(), e);
        }
        return false;
    }

    /**
     * 存入缓存 如果不存在则存入
     *
     * @param key
     * @param value
     * @param timeout
     * @return
     */
    public static Boolean setIfAbsentString(String key, String value, long timeout) {
        try {
            redisTemplate.opsForValue().setIfAbsent(key, value, timeout, TimeUnit.SECONDS);
            return true;
        } catch (Exception e) {
            log.error("插入缓存失败,key:{} , exception:{}", key, e.getMessage(), e);
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
     * 设置缓存, 有key过期时间参数
     *
     * @param key     key值
     * @param value   value值
     * @param timeout 时间 秒为单位
     * @return 成功返回true，失败返回异常信息
     */
    public static boolean setString(String key, String value, long timeout) {
        try {
            stringRedisTemplate.opsForValue().set(key, value, timeout, TimeUnit.SECONDS);
            return true;
        } catch (Exception e) {
            log.error("插入缓存失败,key:{} , exception:{}", key, e.getMessage(), e);
        }
        return false;
    }

    /**
     * 获取多个keys的值
     *
     * @param keys
     * @return
     */
    public static List<Object> multiGet(Collection<String> keys) {
        return redisTemplate.opsForValue().multiGet(keys);
    }

    //----------------------------- list ------------------------------

    /**
     * 将value放入list缓存
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
            log.error("插入List缓存失败,key:{} , exception:{}", key, e.getMessage(), e);
        }
        return false;
    }

    /**
     * 将value数据放入List缓存，并设置时间, 非原子性
     *
     * @param key     key值
     * @param value   数据的值
     * @param timeout 缓存过期时间
     * @return true插入成功，否则返回异常信息
     */
    public static Boolean setList(String key, Object value, long timeout) {
        try {
            if (timeout > 0) {
                redisTemplate.opsForList().rightPush(key, value);
                expire(key, timeout);
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("插入List缓存失败,key:{} , exception:{}", key, e.getMessage(), e);
        }
        return false;
    }

    /**
     * 将values放入List缓存,并设置时间, 非原子性
     *
     * @param key     key值
     * @param value   数据的值
     * @param timeout 缓存过期时间
     * @return true插入成功，否则返回异常信息
     */
    public static Boolean setListAll(String key, long timeout, Object... value) {
        try {
            if (timeout > 0) {
                redisTemplate.opsForList().rightPushAll(key, value);
                expire(key, timeout);
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("插入List缓存失败,key:{} , exception:{}", key, e.getMessage(), e);
        }
        return false;
    }

    /**
     * 根据索引获取List缓存中的内容
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
            log.error("获取List缓存中的内容失败,key:{} , exception:{}", key, e.getMessage(), e);
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
            log.error("删除List缓存中的内容失败,key:{} , exception:{}", key, e.getMessage(), e);
        }
        return 0L;
    }

    /**
     * 获取List缓存的数据大小
     *
     * @param key key值
     * @return 返回大小
     */
    public static long getListSize(String key) {
        try {
            return redisTemplate.opsForList().size(key);
        } catch (Exception e) {
            log.error("获取List缓存的数据大小异常,key:{} , exception:{}", key, e.getMessage(), e);
        }
        return 0L;
    }

    //----------------------------- hash ------------------------------

    /**
     * 获取存储在哈希表中指定的hashKey的值
     *
     * @param key
     * @param hashKey
     * @return
     */
    public Object hGet(String key, String hashKey) {
        return redisTemplate.opsForHash().get(key, hashKey);
    }

    /**
     * 获取给定key的键值对
     *
     * @param key
     * @return
     */
    public Map<Object, Object> hGetAll(String key) {
        return redisTemplate.opsForHash().entries(key);
    }

    /**
     * 获取所有给定字段的值
     *
     * @param key
     * @param fields
     * @return
     */
    public List<Object> hMultiGet(String key, Collection<Object> fields) {
        return redisTemplate.opsForHash().multiGet(key, fields);
    }

    /**
     * 设置key指定hashKey的value
     *
     * @param key
     * @param hashKey
     * @param value
     */
    public void hPut(String key, String hashKey, String value) {
        redisTemplate.opsForHash().put(key, hashKey, value);
    }

    /**
     * 设置key指定的value
     *
     * @param key
     * @param maps
     */
    public void hPutAll(String key, Map<String, String> maps) {
        redisTemplate.opsForHash().putAll(key, maps);
    }

    /**
     * 仅当hashKey不存在时才设置
     *
     * @param key
     * @param hashKey
     * @param value
     * @return
     */
    public Boolean hPutIfAbsent(String key, String hashKey, String value) {
        return redisTemplate.opsForHash().putIfAbsent(key, hashKey, value);
    }

    /**
     * 删除一个或多个哈希表字段
     *
     * @param key
     * @param hashKeys
     * @return
     */
    public Long hDelete(String key, Object... hashKeys) {
        return redisTemplate.opsForHash().delete(key, hashKeys);
    }

    /**
     * 查看哈希表 key 中，指定的字段是否存在
     *
     * @param key
     * @param hashKey
     * @return
     */
    public boolean hExists(String key, String hashKey) {
        return redisTemplate.opsForHash().hasKey(key, hashKey);
    }

    /**
     * 获取所有哈希表中的字段
     *
     * @param key
     * @return
     */
    public Set<Object> hKeys(String key) {
        return redisTemplate.opsForHash().keys(key);
    }

    /**
     * 获取哈希表中字段的数量
     *
     * @param key
     * @return
     */
    public Long hSize(String key) {
        return redisTemplate.opsForHash().size(key);
    }

    /**
     * 获取哈希表中所有值
     *
     * @param key
     * @return
     */
    public List<Object> hValues(String key) {
        return redisTemplate.opsForHash().values(key);
    }

    //----------------------------- set ------------------------------

    /**
     * set添加元素
     *
     * @param key
     * @param values
     * @return
     */
    public Long sAdd(String key, String... values) {
        return redisTemplate.opsForSet().add(key, values);
    }

    /**
     * set移除元素
     *
     * @param key
     * @param values
     * @return
     */
    public Long sRemove(String key, Object... values) {
        return redisTemplate.opsForSet().remove(key, values);
    }

    /**
     * 获取集合的大小
     *
     * @param key
     * @return
     */
    public Long sSize(String key) {
        return redisTemplate.opsForSet().size(key);
    }

    /**
     * 判断集合是否包含value
     *
     * @param key
     * @param value
     * @return
     */
    public Boolean sIsMember(String key, Object value) {
        return redisTemplate.opsForSet().isMember(key, value);
    }

    /**
     * 获取集合所有元素
     *
     * @param key
     * @return
     */
    public Set<Object> setMembers(String key) {
        return redisTemplate.opsForSet().members(key);
    }


    //----------------------------- z-set ------------------------------

    /**
     * 添加元素,有序集合是按照元素的score值由小到大排列
     *
     * @param key
     * @param value
     * @param score
     * @return
     */
    public Boolean zAdd(String key, String value, double score) {
        return redisTemplate.opsForZSet().add(key, value, score);
    }

    /**
     * 添加元素
     *
     * @param key
     * @param values
     * @return
     */
    public Long zAdd(String key, Set<ZSetOperations.TypedTuple<Object>> values) {
        return redisTemplate.opsForZSet().add(key, values);
    }

    /**
     * 删除元素
     *
     * @param key
     * @param values
     * @return
     */
    public Long zRemove(String key, Object... values) {
        return redisTemplate.opsForZSet().remove(key, values);
    }

    /**
     * 增加元素的score值，并返回增加后的值
     *
     * @param key
     * @param value
     * @param delta
     * @return
     */
    public Double zIncrementScore(String key, String value, double delta) {
        return redisTemplate.opsForZSet().incrementScore(key, value, delta);
    }

    /**
     * 返回元素在集合的排名,有序集合是按照元素的score值由小到大排列
     *
     * @param key
     * @param value
     * @return 0表示第一位
     */
    public Long zRank(String key, Object value) {
        return redisTemplate.opsForZSet().rank(key, value);
    }

    /**
     * 返回元素在集合的排名,按元素的score值由大到小排列
     *
     * @param key
     * @param value
     * @return
     */
    public Long zReverseRank(String key, Object value) {
        return redisTemplate.opsForZSet().reverseRank(key, value);
    }

    /**
     * 获取集合的元素, 从小到大排序
     *
     * @param key
     * @param start 开始位置
     * @param end   结束位置, -1查询所有
     * @return
     */
    public Set<Object> zRange(String key, long start, long end) {
        return redisTemplate.opsForZSet().range(key, start, end);
    }

    /**
     * 获取集合元素, 并且把score值也获取
     *
     * @param key
     * @param start
     * @param end
     * @return
     */
    public Set<ZSetOperations.TypedTuple<Object>> zRangeWithScores(String key, long start, long end) {
        return redisTemplate.opsForZSet().rangeWithScores(key, start, end);
    }

    /**
     * 根据Score值查询集合元素
     *
     * @param key
     * @param min 最小值
     * @param max 最大值
     * @return
     */
    public Set<Object> zRangeByScore(String key, double min, double max) {
        return redisTemplate.opsForZSet().rangeByScore(key, min, max);
    }

    /**
     * 根据Score值查询集合元素, 从小到大排序
     *
     * @param key
     * @param min 最小值
     * @param max 最大值
     * @return
     */
    public Set<ZSetOperations.TypedTuple<Object>> zRangeByScoreWithScores(String key, double min, double max) {
        return redisTemplate.opsForZSet().rangeByScoreWithScores(key, min, max);
    }

    /**
     * @param key
     * @param min
     * @param max
     * @param start
     * @param end
     * @return
     */
    public Set<ZSetOperations.TypedTuple<Object>> zRangeByScoreWithScores(String key, double min, double max, long start, long end) {
        return redisTemplate.opsForZSet().rangeByScoreWithScores(key, min, max, start, end);
    }

    /**
     * 获取集合的元素, 从大到小排序
     *
     * @param key
     * @param start
     * @param end
     * @return
     */
    public Set<Object> zReverseRange(String key, long start, long end) {
        return redisTemplate.opsForZSet().reverseRange(key, start, end);
    }

    /**
     * 获取集合的元素, 从大到小排序, 并返回score值
     *
     * @param key
     * @param start
     * @param end
     * @return
     */
    public Set<ZSetOperations.TypedTuple<Object>> zReverseRangeWithScores(String key, long start, long end) {
        return redisTemplate.opsForZSet().reverseRangeWithScores(key, start, end);
    }

    /**
     * 根据Score值查询集合元素, 从大到小排序
     *
     * @param key
     * @param min
     * @param max
     * @return
     */
    public Set<Object> zReverseRangeByScore(String key, double min, double max) {
        return redisTemplate.opsForZSet().reverseRangeByScore(key, min, max);
    }

    /**
     * 根据Score值查询集合元素, 从大到小排序
     *
     * @param key
     * @param min
     * @param max
     * @return
     */
    public Set<ZSetOperations.TypedTuple<Object>> zReverseRangeByScoreWithScores(String key, double min, double max) {
        return redisTemplate.opsForZSet().reverseRangeByScoreWithScores(key, min, max);
    }

    /**
     * @param key
     * @param min
     * @param max
     * @param start
     * @param end
     * @return
     */
    public Set<Object> zReverseRangeByScore(String key, double min, double max, long start, long end) {
        return redisTemplate.opsForZSet().reverseRangeByScore(key, min, max, start, end);
    }

    /**
     * 根据score值获取集合元素数量
     *
     * @param key
     * @param min
     * @param max
     * @return
     */
    public Long zCount(String key, double min, double max) {
        return redisTemplate.opsForZSet().count(key, min, max);
    }

    /**
     * 获取集合大小
     *
     * @param key
     * @return
     */
    public Long zSize(String key) {
        return redisTemplate.opsForZSet().size(key);
    }

    /**
     * 获取集合大小
     *
     * @param key
     * @return
     */
    public Long zZCard(String key) {
        return redisTemplate.opsForZSet().zCard(key);
    }

    /**
     * 获取集合中value元素的score值
     *
     * @param key
     * @param value
     * @return
     */
    public Double zScore(String key, Object value) {
        return redisTemplate.opsForZSet().score(key, value);
    }

    /**
     * 移除指定索引位置的成员
     *
     * @param key
     * @param start
     * @param end
     * @return
     */
    public Long zRemoveRange(String key, long start, long end) {
        return redisTemplate.opsForZSet().removeRange(key, start, end);
    }

    /**
     * 根据指定的score值的范围来移除成员
     *
     * @param key
     * @param min
     * @param max
     * @return
     */
    public Long zRemoveRangeByScore(String key, double min, double max) {
        return redisTemplate.opsForZSet().removeRangeByScore(key, min, max);
    }

    /**
     * 获取key和otherKey的并集并存储在destKey中
     *
     * @param key
     * @param otherKey
     * @param destKey
     * @return
     */
    public Long zUnionAndStore(String key, String otherKey, String destKey) {
        return redisTemplate.opsForZSet().unionAndStore(key, otherKey, destKey);
    }

}
