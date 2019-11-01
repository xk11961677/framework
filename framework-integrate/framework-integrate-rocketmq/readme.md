使用步骤:
=====
1): 引入如下包
```

<dependency>
    <groupId>com.sky.framework</groupId>
    <artifactId>framework-integrate-rocketmq</artifactId>
</dependency>

```
    
2): 使用rocketmq-spring-boot-starter方式

2.1) 消费端使用原生
2.2) 生产端直接使用RocketMqUtils.asyncSend()方法
2.3) [rocketmq-spring-boot-starter文档](https://github.com/apache/rocketmq-spring)


3): 使用自定义方式(暂时不要使用)

3.1) 消费端实现MessageHandler类,并加@ConsumerListener注解

3.2) 生产端配置属性文件后，直接使用RocketMqUtils.sendMsg()方法

3.3) 配置属性文件
```
rocket:
  config:
    producer:
      - group: action_group3
      - group: action_group3
    consumer:
      - group: action_group1
        topic: action_topic1
      - group: action_group2
        topic: action_topic2
    namesrvAddr: 127.0.0.1:9876
```

3.4) Feature

* [x] 同步发送消息
* [ ] 异步发送消息
* [ ] 广播发送消息
* [ ] 有序发送和消费消息
* [ ] 发送延时消息
* [ ] 消息tag和key支持
* [ ] 自动序列化和反序列化消息体
* [ ] 消息的实际消费方IP追溯
* [ ] 发送事务消息