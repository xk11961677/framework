## Sky Cloud 

[![Build Status](https://travis-ci.org/xk11961677/framework.svg?branch=master)](https://travis-ci.org/xk11961677/framework)
[![license](https://img.shields.io/badge/license-MIT-ff69b4.svg)](https://mit-license.org/license.html)


<!-- 代码结构 -->


## 代码结构
- [framework-bom](framework-bom) framework项目jar包版本依赖管理
    -  版本号：<主版本>.<次版本>.<增量版本>-<代号>
    -  ~~升级版本号命令: mvn versions:set -DnewVersion=x.x.x~~
    -  ~~升级版本号命令~~
         -  mvn release:prepare  
             1. 把项目打一个release版本
             2. 在git的tag中打一个tag
             3. 自动升级SNAPSHOT 并提交更新后的pom文件到git
         -  mvn release:rollback
             1. 回滚,但不会删除tag
         -  mvn release:perform  
             1. 去git的tag上拿代码
             2. 用tag上的代码，打一个release版的包 
             3. deploy到的maven私服 
    -  直接修改<revision>1.0.0-SNAPSHOT</revision>属性版本 

    
- [framework-generator-plugin](framework-generator-plugin) 代码生成器插件
    
- [framework-web](framework-web) Spring Boot Web 脚手架依赖

- [framework-model](framework-model) 公共DTO实体与分页实体

- [framework-integrate](framework-integrate) 整合其他三方库,如netty、spark等
    -  [framework-integrate-validator](framework-integrate/framework-integrate-validator)  业务参数验证器,使用方法请读本项目[readme.md](framework-integrate/framework-integrate-validator/readme.md)
    -  [framework-integrate-redis](framework-integrate/framework-integrate-redis)  redis,使用方法请读本项目[readme.md](framework-integrate/framework-integrate-redis/readme.md)
    -  [framework-integrate-rocketmq](framework-integrate/framework-integrate-rocketmq)  mq,使用方法请读本项目[readme.md](framework-integrate/framework-integrate-rocketmq/readme.md)
    -  [framework-integrate-job](framework-integrate/framework-integrate-job)  分布式定时任务,使用方法请读本项目[readme.md](framework-integrate/framework-integrate-job/readme.md)

- [framework-util](framework-util) 工具类
    -  [framework-util-common](framework-util/framework-util-common)  公共工具类包
    -  [framework-util-threadpool](framework-util/framework-util-threadpool)  线程池工具类,使用方法请读本项目[readme.md](framework-util/framework-util-threadpool/readme.md)
   
  
## 关于作者
```javascript
  
```
## [LICENSE](LICENSE)
MIT License

Copyright (c) 2019 framework

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.