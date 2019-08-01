使用步骤:
=====
1): 引入如下包
```

<dependency>
    <groupId>com.sky.framework</groupId>
    <artifactId>framework-integrate-redis</artifactId>
</dependency>

```
    
2): 配置redis属性 (请在配置中心操作) ,如使用了redisson可不用配置下面信息,直接配置redisson信息即可

```
spring:
  redis:
    # Redis数据库索引（默认为0）
    database: 0
    # Redis服务器地址
    host: 127.0.0.1
    # Redis服务器连接端口
    port: 6379
    # Redis服务器连接密码（默认为空）
    password: 123456
    #连接池
    lettuce:
      pool:
        #连接池最大连接数（使用负值表示没有限制）
        max-active: 200
        #连接池最大阻塞等待时间（使用负值表示没有限制）
        max-wait: -1
        #连接池中的最大空闲连接
        max-idle: 10
        #连接池中的最小空闲连接
        min-idle: 0
    #连接超时时间（毫秒）
    timeout: 10000

```
另:使用redis官方推荐的redisson分布式锁
详细配置请[点我](https://github.com/redisson/redisson/wiki)

3) 如不需要使用redisson 请按如下配置排除jar包 ,且不要使用 RedissonLockUtil , 请使用 RedisLockUtil

```
<exclusions>
    <exclusion>
        <groupId>org.redisson</groupId>
        <artifactId>redisson</artifactId>
    </exclusion>
</exclusions>
```