使用步骤:
=====
1): 引入如下包
```

<dependency>
    <groupId>com.sky.framework</groupId>
    <artifactId>framework-util-oss</artifactId>
</dependency>

```
    
2): 配置自定义属性
```
oss:
  aliyun:
      access-key-id: xxxx
      access-key-secret: xxxx
      bucketName: sky
      endpoint: oss-cn-hangzhou.aliyuncs.com
  callbackUrl: http://127.0.0.1:8904/open/oss/callback
  dir-prefix: sky/
  strategy: aliyun
  url-prefix: http://sky.oss-cn-hangzhou.aliyuncs.com
```
3): 代码编写方式




