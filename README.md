# raxcl-blog
<p align="center">
	<img src="https://img.shields.io/badge/JDK-1.8+-orange">
	<img src="https://img.shields.io/badge/SpringBoot-2.2.7.RELEASE-brightgreen">
	<img src="https://img.shields.io/badge/MyBatisPlus-3.5.5-red">
	<img src="https://img.shields.io/badge/Vue-2.6.11-brightgreen">
	<img src="https://img.shields.io/badge/license-MIT-blue">	
</p>

## 提醒
该分支为个人分支，包含了大量个人代码，且不包含所有敏感信息，该分支无法运行，如果你是不小心点进来，请移至 master 分支

## 简介
为了拥有一个属于自己的网站，基于springboot + vue 开发了前后端分离博客系统

## 待更新功能
部署教程，一键docker部署等功能

## 更新功能
1. mysql数据库持久化：每天定时任务将mysql备份至七牛云
2. 图床改为腾讯云的cos，腾讯云cdn加持（免费cdn国内凉了，改为使用付费，不过还好不算贵，一个月不到 1RMB）
3. 更新 ip2region 版本，以及相关方法
4. 升级 pox.xml 旧的依赖

## 博客预览
博客地址：[https://raxcl.cn](https://raxcl.cn)

后台管理平台：[https://admin.raxcl.cn](https://admin.raxcl.cn) 账号`Visitor`密码`666666`

博客上线部署过程可以参考：[https://blog.csdn.net/RAXCL/article/details/121276028](https://blog.csdn.net/RAXCL/article/details/121276028)

本项目长期维护，欢迎fork代码和star！。



## 快速开始

1. 创建 MySQL 数据库`r_blog`，并执行`/blog-api/r_blog.sql`初始化表数据
2. 修改配置信息`blog-api/src/main/resources/test(application.yml)`,需要改名为application.yml，然后修改必要配置信息
3. 安装 Redis 并启动
4. 启动后端服务
5. 分别在`blog-cms`和`blog-view`目录下执行`npm install`安装依赖
6. 分别在`blog-cms`和`blog-view`目录下执行`npm run serve`启动前后台页面



## 新增的功能
[vue-live2d](https://github.com/Raxcl/vue-live2d)

[logo](https://codepen.io)



## 后端

1. 核心框架：[Spring Boot](https://github.com/spring-projects/spring-boot)
2. 安全框架：[Spring Security](https://github.com/spring-projects/spring-security)
3. Token 认证：[jjwt](https://github.com/jwtk/jjwt)
4. 持久层框架：[MyBatis](https://github.com/mybatis/spring-boot-starter)
5. 分页插件：[PageHelper](https://github.com/pagehelper/Mybatis-PageHelper)
6. NoSQL缓存：[Redis](https://github.com/redis/redis)
7. Markdown 转 HTML：[commonmark-java](https://github.com/commonmark/commonmark-java)
8. 离线 IP 地址库：[ip2region](https://github.com/lionsoul2014/ip2region)
9. 定时任务：[quartz](https://github.com/quartz-scheduler/quartz)

邮件模板参考自[Typecho-CommentToMail-Template](https://github.com/MisakaTAT/Typecho-CommentToMail-Template)

基于 JDK8 开发，8以上要添加依赖：

```xml
<dependency>
    <groupId>javax.xml.bind</groupId>
    <artifactId>jaxb-api</artifactId>
    <version>2.3.0</version>
</dependency>
```



## 前端

核心框架：Vue2.x、Vue Router、Vuex

Vue 项目基于 @vue/cli4.x 构建

JS 依赖及参考的 css：[axios](https://github.com/axios/axios)、[moment](https://github.com/moment/moment)、[nprogress](https://github.com/rstacruz/nprogress)、[v-viewer](https://github.com/fengyuanchen/viewerjs)、[prismjs](https://github.com/PrismJS/prism)、[APlayer](https://github.com/DIYgod/APlayer)、[MetingJS](https://github.com/metowolf/MetingJS)、[lodash](https://github.com/lodash/lodash)、[mavonEditor](https://github.com/hinesboy/mavonEditor)、[vue-live2d](https://github.com/Raxcl/vue-live2d)、[jinrishici](https://www.jinrishici.com)



## 注意事项

一些常见问题：

- MySQL 确保数据库字符集为`utf8mb4`的情况下通常没有问题（”站点设置“及”文章详情“等许多表字段需要`utf8mb4`格式字符集来支持 emoji 表情，否则在导入 sql 文件时，即使成功导入，也会有部分字段内容不完整，导致前端页面渲染数据时报错）
- 确保 Maven 能够成功导入现版本依赖，请勿升级或降低依赖版本
- 数据库中默认用户名密码为`Admin`，`123456`，因为是个人博客，没打算做修改密码的页面，可在`top.naccl.util.HashUtils`下的`getBc`方法手动生成密码存入数据库
- 注意修改`application-dev.yml`的配置信息
  - Redis 若没有密码，留空即可
  - 注意修改`token.secretKey`，否则无法保证 token 安全性
  - `spring.mail.host`及`spring.mail.port`的默认配置为阿里云邮箱，其它邮箱服务商参考关键字`spring mail 服务器`（邮箱配置用于接收评论提醒）



## 隐藏功能

- 在前台访问`/login`路径登录后，可以以博主身份（带有博主标识）回复评论，且不需要填写昵称和邮箱即可提交
- 在 Markdown 中加入`<meting-js server="netease" type="song" id="歌曲id" theme="#25CCF7"></meting-js>` （注意以正文形式添加，而不是代码片段）可以在文章中添加 [APlayer](https://github.com/DIYgod/APlayer) 音乐播放器，`netease`为网易云音乐，其它配置及具体用法参考 [MetingJS](https://github.com/metowolf/MetingJS)
- 提供了两种隐藏文字效果：在 Markdown 中使用`@@`包住文字，文字会被渲染成“黑幕”效果，鼠标悬浮在上面时才会显示；使用`%%`包住文字，文字会被“蓝色覆盖层”遮盖，只有鼠标选中状态才会反色显示。例如：`@@隐藏文字@@`，`%%隐藏文字%%`
- 大部分个性化配置可以在后台“站点设置”中修改，小部分由于考虑到首屏加载速度（如首页大图）需要修改前端源码



## 致谢
感谢码神的[视频](https://www.bilibili.com/video/BV1Gb4y1d7zb?spm_id_from=0.0.header_right.fav_list.click)让我学会如何构建和部署一个博客，重构美化过程也参考了很多大佬的博客，如[NBlog](https://github.com/Naccl/NBlog)、[SKyBlog](https://github.com/yubifeng/SkyBlog)
等等...

感谢上面提到的每个开源项目


