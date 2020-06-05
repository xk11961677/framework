## sky 

[![Build Status](https://travis-ci.org/xk11961677/framework.svg?branch=master)](https://travis-ci.org/xk11961677/framework)
[![license](https://img.shields.io/badge/license-mit-ff69b4.svg)](https://mit-license.org/license.html)
[![springboot](https://img.shields.io/badge/springboot-2.1.4.RELEASE-orange.svg)](https://spring.io/projects/spring-boot)
![Gitter](https://img.shields.io/gitter/room/sky-docs/community)
![version](https://img.shields.io/badge/version-1.0.0-blueviolet.svg)
![helloworld](https://img.shields.io/badge/hello-world-blue)
[![codecov](https://codecov.io/gh/xk11961677/framework/branch/master/graph/badge.svg)](https://codecov.io/gh/xk11961677/framework)


## 文档地址
- [quick-start](https://xk11961677.github.io/sky-docs/)

## 代码结构
- [framework-dependencies](framework-dependencies) framework项目 1rd part jar包兼容版本依赖管理
- [framework-bom](framework-bom) framework项目 3rd part jar包版本依赖管理
- [framework-generator-plugin](framework-generator-plugin) 代码生成器插件支持jar包
- [framework-dao](framework-dao) 存储层代码封装,如:hive hbase等
    -  [framework-dao-hive](framework-dao/framework-dao-hive)  
    -  [framework-dao-hbase](framework-dao/framework-dao-hbase)  
    -  [framework-dao-mybatis](framework-dao/framework-dao-mybatis)  
    -  [framework-dao-mybatis-plus](framework-dao/framework-dao-mybatis-plus) 
- [framework-web](framework-web) Spring Boot Web 脚手架依赖
- [framework-model](framework-model) 公共DTO实体与分页实体
- [framework-integrate](framework-integrate) 整合其他三方库,如netty、spark等
    -  [framework-integrate-canal](framework-integrate/framework-integrate-canal)  同步mysql binlog 
    -  [framework-integrate-validator](framework-integrate/framework-integrate-validator)  业务参数验证器,使用方法请读本项目[readme.md](framework-integrate/framework-integrate-validator/readme.md)
    -  [framework-integrate-redis](framework-integrate/framework-integrate-redis)  redis,使用方法请读本项目[readme.md](framework-integrate/framework-integrate-redis/readme.md)
    -  [framework-integrate-rocketmq](framework-integrate/framework-integrate-rocketmq)  mq,使用方法请读本项目[readme.md](framework-integrate/framework-integrate-rocketmq/readme.md)
    -  [framework-integrate-job](framework-integrate/framework-integrate-job)  分布式定时任务,使用方法请读本项目[readme.md](framework-integrate/framework-integrate-job/readme.md)
    -  [framework-integrate-logback](framework-integrate/framework-integrate-logback)  自定义logback appender[readme.md](framework-integrate/framework-integrate-logback/readme.md)
- [framework-util](framework-util) 工具类
    -  [framework-util-common](framework-util/framework-util-common)  公共工具类包
    -  [framework-util-oss](framework-util/framework-util-oss)  对象资源存储[readme.md](framework-util/framework-util-oss/readme.md)
    -  [framework-util-threadpool](framework-util/framework-util-threadpool)  线程池工具类,使用方法请读本项目[readme.md](framework-util/framework-util-threadpool/readme.md)
    -  [framework-util-kv](framework-util/framework-util-kv)  将json转成key value格式工具
    -  [framework-util-notify](framework-util/framework-util-notify)  发送企业微信与钉钉工具


## git 分支开发规约
- 使用git flow 流程，分支名称分别以 feature-* 、 release-* 、hotfix-* 开头
- 版本号：<主版本>.<次版本>.<增量版本>-<代号>
   -  方式1: 升级版本号命令: mvn versions:set -DnewVersion=x.x.x
   -  方式2: 升级版本号命令
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
   -  方式3: 仅修改 pom.xml --> <revision>1.0.0-SNAPSHOT</revision> 属性
   -  推荐方式3
- 代号版本
    - SNAPSHOT: 用于develop/hotfix
    - RC数字: 用于测试阶段
    - RELEASE: 正式发布版
    - 具体列子:
        1. 开发版本: 1.1.0-SNAPSHOT、1.2.0-SNAPSHOT、2.1.0-SNAPSHOT
        2. 稳定版本:
            1. 候选发布版本: 1.1.0-RC1、1.2.0-RC2
            2. 正式发布版本: 1.1.0-RELEASE、1.1.1-RELEASE

## git message 规约
#### 用于生成规范changelog
#### 提交格式
1. [请点我](docs/script/changelog/commit.md)
2. idea 可使用 git commit template 插件
3. npm 可以使用 commitizen

#### 生成changelog方式
运行docs/script/changelog/gitlog.sh

## javadoc生成方式
mvn javadoc:javadoc

## 问题反馈
- 在使用中有任何问题，欢迎反馈

## FAQ
- [mybatis代码生成器(本地版本)](https://github.com/xk11961677/framework-mybaits-codegen)
- [mybatis代码生成器(web版本)](https://github.com/xk11961677/skycloud-base/tree/develop/skycloud-base-codegen)
- 所有配置项可在idea yml文件中 输入 fw. 即可提示

## 开发计划


## 关于作者

## [LICENSE](license)
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