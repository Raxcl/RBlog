/*
 Navicat MySQL Data Transfer

 Source Server         : raxcl
 Source Server Type    : MySQL
 Source Server Version : 80027
 Source Host           : 81.71.87.241:3307
 Source Schema         : r_blog

 Target Server Type    : MySQL
 Target Server Version : 80027
 File Encoding         : 65001

 Date: 21/12/2021 16:18:54
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for about
-- ----------------------------
DROP TABLE IF EXISTS `about`;
CREATE TABLE `about`  (
  `id` bigint NOT NULL,
  `name_en` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `name_zh` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `value` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of about
-- ----------------------------
INSERT INTO `about` VALUES (1, 'title', '标题', '关于我的标题');
INSERT INTO `about` VALUES (2, 'musicId', '网易云歌曲ID', '423015580');
INSERT INTO `about` VALUES (3, 'content', '正文Markdown', '关于我的正文');
INSERT INTO `about` VALUES (4, 'commentEnabled', '评论开关', 'true');

-- ----------------------------
-- Table structure for blog
-- ----------------------------
DROP TABLE IF EXISTS `blog`;
CREATE TABLE `blog`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '文章标题',
  `first_picture` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '文章首图，用于随机文章展示',
  `content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '文章正文',
  `description` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '描述',
  `is_published` bit(1) NOT NULL COMMENT '公开或私密',
  `is_recommend` bit(1) NOT NULL COMMENT '推荐开关',
  `is_appreciation` bit(1) NOT NULL COMMENT '赞赏开关',
  `is_comment_enabled` bit(1) NOT NULL COMMENT '评论开关',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  `views` int NOT NULL COMMENT '浏览次数',
  `words` int NOT NULL COMMENT '文章字数',
  `read_time` int NOT NULL COMMENT '阅读时长(分钟)',
  `category_id` bigint NOT NULL COMMENT '文章分类',
  `is_top` bit(1) NOT NULL COMMENT '是否置顶',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '密码保护',
  `user_id` bigint NULL DEFAULT NULL COMMENT '文章作者',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `type_id`(`category_id`) USING BTREE,
  INDEX `user_id`(`user_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of blog
-- ----------------------------
INSERT INTO `blog` VALUES (1, '本站第一篇文章的标题', '1', '本站第一篇文章的正文', '本站第一篇文章的描述', b'1', b'0', b'0', b'0', '2021-11-27 15:58:03', '2021-11-27 15:58:03', 31, 1000, 5, 1, b'0', '', 1);

-- ----------------------------
-- Table structure for blog_tag
-- ----------------------------
DROP TABLE IF EXISTS `blog_tag`;
CREATE TABLE `blog_tag`  (
  `blog_id` bigint NOT NULL,
  `tag_id` bigint NOT NULL
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of blog_tag
-- ----------------------------
INSERT INTO `blog_tag` VALUES (1, 1);

-- ----------------------------
-- Table structure for category
-- ----------------------------
DROP TABLE IF EXISTS `category`;
CREATE TABLE `category`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `category_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of category
-- ----------------------------
INSERT INTO `category` VALUES (1, '伊始');

-- ----------------------------
-- Table structure for city_visitor
-- ----------------------------
DROP TABLE IF EXISTS `city_visitor`;
CREATE TABLE `city_visitor`  (
  `city` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '城市名称',
  `uv` int NOT NULL COMMENT '独立访客数量',
  PRIMARY KEY (`city`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of city_visitor
-- ----------------------------

-- ----------------------------
-- Table structure for comment
-- ----------------------------
DROP TABLE IF EXISTS `comment`;
CREATE TABLE `comment`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `nickname` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '昵称',
  `email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '邮箱',
  `content` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '评论内容',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '头像(图片路径)',
  `create_time` datetime NULL DEFAULT NULL COMMENT '评论时间',
  `ip` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '评论者ip地址',
  `is_published` bit(1) NOT NULL COMMENT '公开或回收站',
  `is_admin_comment` bit(1) NOT NULL COMMENT '博主回复',
  `page` int NOT NULL COMMENT '0普通文章，1关于我页面，2友链页面',
  `is_notice` bit(1) NOT NULL COMMENT '接收邮件提醒',
  `blog_id` bigint NULL DEFAULT NULL COMMENT '所属的文章',
  `parent_comment_id` bigint NOT NULL COMMENT '父评论id，-1为根评论',
  `website` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '个人网站',
  `qq` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '如果评论昵称为QQ号，则将昵称和头像置为QQ昵称和QQ头像，并将此字段置为QQ号备份',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of comment
-- ----------------------------

-- ----------------------------
-- Table structure for exception_log
-- ----------------------------
DROP TABLE IF EXISTS `exception_log`;
CREATE TABLE `exception_log`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `uri` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '请求接口',
  `method` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '请求方式',
  `param` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '请求参数',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '操作描述',
  `error` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '异常信息',
  `ip` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'ip',
  `ip_source` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'ip来源',
  `os` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '操作系统',
  `browser` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '浏览器',
  `create_time` datetime NOT NULL COMMENT '操作时间',
  `user_agent` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'user-agent用户代理',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of exception_log
-- ----------------------------

-- ----------------------------
-- Table structure for friend
-- ----------------------------
DROP TABLE IF EXISTS `friend`;
CREATE TABLE `friend`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `nickname` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '昵称',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '描述',
  `website` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '站点',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '头像',
  `is_published` bit(1) NOT NULL COMMENT '公开或隐藏',
  `views` int NOT NULL COMMENT '点击次数',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of friend
-- ----------------------------

-- ----------------------------
-- Table structure for login_log
-- ----------------------------
DROP TABLE IF EXISTS `login_log`;
CREATE TABLE `login_log`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户名称',
  `ip` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'ip',
  `ip_source` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'ip来源',
  `os` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '操作系统',
  `browser` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '浏览器',
  `status` bit(1) NULL DEFAULT NULL COMMENT '登录状态',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '操作描述',
  `create_time` datetime NOT NULL COMMENT '登录时间',
  `user_agent` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'user-agent用户代理',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of login_log
-- ----------------------------
INSERT INTO `login_log` VALUES (1, 'Admin', '10.60.17.56', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', b'1', '登录成功', '2021-11-23 11:41:03', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `login_log` VALUES (2, 'Admin', '10.60.17.56', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', b'1', '登录成功', '2021-11-23 13:26:01', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `login_log` VALUES (3, 'Admin', '10.60.17.56', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', b'1', '登录成功', '2021-11-27 15:41:02', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `login_log` VALUES (4, 'Admin', '10.60.17.56', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', b'1', '登录成功', '2021-12-17 16:03:53', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');

-- ----------------------------
-- Table structure for moment
-- ----------------------------
DROP TABLE IF EXISTS `moment`;
CREATE TABLE `moment`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '动态内容',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `likes` int NULL DEFAULT NULL COMMENT '点赞数量',
  `is_published` bit(1) NOT NULL COMMENT '是否公开',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of moment
-- ----------------------------

-- ----------------------------
-- Table structure for operation_log
-- ----------------------------
DROP TABLE IF EXISTS `operation_log`;
CREATE TABLE `operation_log`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '操作者用户名',
  `uri` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '请求接口',
  `method` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '请求方式',
  `param` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '请求参数',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '操作描述',
  `ip` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'ip',
  `ip_source` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'ip来源',
  `os` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '操作系统',
  `browser` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '浏览器',
  `times` int NOT NULL COMMENT '请求耗时（毫秒）',
  `create_time` datetime NOT NULL COMMENT '操作时间',
  `user_agent` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'user-agent用户代理',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of operation_log
-- ----------------------------
INSERT INTO `operation_log` VALUES (1, 'Admin', '/admin/siteSettings', 'POST', '{\"map\":{\"settings\":[{\"id\":1,\"nameEn\":\"webTitleSuffix\",\"nameZh\":\"网页标题后缀\",\"value\":\"网页标题后缀\",\"type\":1},{\"id\":2,\"nameEn\":\"blogName\",\"nameZh\":\"博客名称\",\"value\":\"博客名称\",\"type\":1},{\"id\":3,\"nameEn\":\"footerImgTitle\",\"nameZh\":\"页脚图片标题\",\"value\":\"页脚图片标题\",\"type\":1},{\"id\":4,\"nameEn\":\"footerImgUrl\",\"nameZh\":\"页脚图片路径\",\"value\":\"/img/qr.png\",\"type\":1},{\"id\":5,\"nameEn\":\"copyright\",\"nameZh\":\"Copyright\",\"value\":\"{\\\"title\\\":\\\"Copyright © 2019 - 2020\\\",\\\"siteName\\\":\\\"站点名称\\\"}\",\"type\":1},{\"id\":6,\"nameEn\":\"beian\",\"nameZh\":\"ICP备案号\",\"value\":\"\",\"type\":1},{\"id\":25,\"nameEn\":\"reward\",\"nameZh\":\"赞赏码路径\",\"value\":\"/img/reward.jpg\",\"type\":1},{\"id\":26,\"nameEn\":\"commentAdminFlag\",\"nameZh\":\"博主评论标识\",\"value\":\"博主评论标识\",\"type\":1},{\"id\":7,\"nameEn\":\"badge\",\"nameZh\":\"徽标\",\"value\":\"{\\\"title\\\":\\\"由 Spring Boot 强力驱动\\\",\\\"url\\\":\\\"https://spring.io/projects/spring-boot/\\\",\\\"subject\\\":\\\"Powered\\\",\\\"value\\\":\\\"Spring Boot\\\",\\\"color\\\":\\\"blue\\\"}\",\"type\":2},{\"id\":8,\"nameEn\":\"badge\",\"nameZh\":\"徽标\",\"value\":\"{\\\"title\\\":\\\"Vue.js 客户端渲染\\\",\\\"url\\\":\\\"https://cn.vuejs.org/\\\",\\\"subject\\\":\\\"SPA\\\",\\\"value\\\":\\\"Vue.js\\\",\\\"color\\\":\\\"brightgreen\\\"}\",\"type\":2},{\"id\":9,\"nameEn\":\"badge\",\"nameZh\":\"徽标\",\"value\":\"{\\\"title\\\":\\\"UI 框架 Semantic-UI\\\",\\\"url\\\":\\\"https://semantic-ui.com/\\\",\\\"subject\\\":\\\"UI\\\",\\\"value\\\":\\\"Semantic-UI\\\",\\\"color\\\":\\\"semantic-ui\\\"}\",\"type\":2},{\"id\":10,\"nameEn\":\"badge\",\"nameZh\":\"徽标\",\"value\":\"{\\\"title\\\":\\\"阿里云提供服务器及域名相关服务\\\",\\\"url\\\":\\\"https://www.aliyun.com/\\\",\\\"subject\\\":\\\"VPS & DNS\\\",\\\"value\\\":\\\"Aliyun\\\",\\\"color\\\":\\\"blueviolet\\\"}\",\"type\":2},{\"id\":11,\"nameEn\":\"badge\",\"nameZh\":\"徽标\",\"value\":\"{\\\"title\\\":\\\"jsDelivr 提供 CDN 加速服务\\\",\\\"url\\\":\\\"https://www.jsdelivr.com/\\\",\\\"subject\\\":\\\"CDN\\\",\\\"value\\\":\\\"jsDelivr\\\",\\\"color\\\":\\\"orange\\\"}\",\"type\":2},{\"id\":12,\"nameEn\":\"badge\",\"nameZh\":\"徽标\",\"value\":\"{\\\"title\\\":\\\"GitHub 提供图床\\\",\\\"url\\\":\\\"https://github.com/\\\",\\\"subject\\\":\\\"OSS\\\",\\\"value\\\":\\\"GitHub\\\",\\\"color\\\":\\\"github\\\"}\",\"type\":2},{\"id\":13,\"nameEn\":\"badge\",\"nameZh\":\"徽标\",\"value\":\"{\\\"title\\\":\\\"本站点采用 CC BY 4.0 国际许可协议进行许可\\\",\\\"url\\\":\\\"https://creativecommons.or', '更新站点配置信息', '10.60.17.56', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 585, '2021-11-23 11:42:27', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `operation_log` VALUES (2, 'Admin', '/admin/siteSettings', 'POST', '{\"map\":{\"settings\":[{\"id\":1,\"nameEn\":\"webTitleSuffix\",\"nameZh\":\"网页标题后缀\",\"value\":\"网页标题后缀\",\"type\":1},{\"id\":2,\"nameEn\":\"blogName\",\"nameZh\":\"博客名称\",\"value\":\"博客名称\",\"type\":1},{\"id\":3,\"nameEn\":\"footerImgTitle\",\"nameZh\":\"页脚图片标题\",\"value\":\"页脚图片标题\",\"type\":1},{\"id\":4,\"nameEn\":\"footerImgUrl\",\"nameZh\":\"页脚图片路径\",\"value\":\"/img/qr.png\",\"type\":1},{\"id\":5,\"nameEn\":\"copyright\",\"nameZh\":\"Copyright\",\"value\":\"{\\\"title\\\":\\\"Copyright © 2019 - 2020\\\",\\\"siteName\\\":\\\"站点名称\\\"}\",\"type\":1},{\"id\":6,\"nameEn\":\"beian\",\"nameZh\":\"ICP备案号\",\"value\":\"\",\"type\":1},{\"id\":25,\"nameEn\":\"reward\",\"nameZh\":\"赞赏码路径\",\"value\":\"/img/reward.jpg\",\"type\":1},{\"id\":26,\"nameEn\":\"commentAdminFlag\",\"nameZh\":\"博主评论标识\",\"value\":\"博主评论标识\",\"type\":1},{\"id\":7,\"nameEn\":\"badge\",\"nameZh\":\"徽标\",\"value\":\"{\\\"title\\\":\\\"由 Spring Boot 强力驱动\\\",\\\"url\\\":\\\"https://spring.io/projects/spring-boot/\\\",\\\"subject\\\":\\\"Powered\\\",\\\"value\\\":\\\"Spring Boot\\\",\\\"color\\\":\\\"blue\\\"}\",\"type\":2},{\"id\":8,\"nameEn\":\"badge\",\"nameZh\":\"徽标\",\"value\":\"{\\\"title\\\":\\\"Vue.js 客户端渲染\\\",\\\"url\\\":\\\"https://cn.vuejs.org/\\\",\\\"subject\\\":\\\"SPA\\\",\\\"value\\\":\\\"Vue.js\\\",\\\"color\\\":\\\"brightgreen\\\"}\",\"type\":2},{\"id\":9,\"nameEn\":\"badge\",\"nameZh\":\"徽标\",\"value\":\"{\\\"title\\\":\\\"UI 框架 Semantic-UI\\\",\\\"url\\\":\\\"https://semantic-ui.com/\\\",\\\"subject\\\":\\\"UI\\\",\\\"value\\\":\\\"Semantic-UI\\\",\\\"color\\\":\\\"semantic-ui\\\"}\",\"type\":2},{\"id\":10,\"nameEn\":\"badge\",\"nameZh\":\"徽标\",\"value\":\"{\\\"title\\\":\\\"阿里云提供服务器及域名相关服务\\\",\\\"url\\\":\\\"https://www.aliyun.com/\\\",\\\"subject\\\":\\\"VPS & DNS\\\",\\\"value\\\":\\\"Aliyun\\\",\\\"color\\\":\\\"blueviolet\\\"}\",\"type\":2},{\"id\":11,\"nameEn\":\"badge\",\"nameZh\":\"徽标\",\"value\":\"{\\\"title\\\":\\\"jsDelivr 提供 CDN 加速服务\\\",\\\"url\\\":\\\"https://www.jsdelivr.com/\\\",\\\"subject\\\":\\\"CDN\\\",\\\"value\\\":\\\"jsDelivr\\\",\\\"color\\\":\\\"orange\\\"}\",\"type\":2},{\"id\":12,\"nameEn\":\"badge\",\"nameZh\":\"徽标\",\"value\":\"{\\\"title\\\":\\\"GitHub 提供图床\\\",\\\"url\\\":\\\"https://github.com/\\\",\\\"subject\\\":\\\"OSS\\\",\\\"value\\\":\\\"GitHub\\\",\\\"color\\\":\\\"github\\\"}\",\"type\":2},{\"id\":13,\"nameEn\":\"badge\",\"nameZh\":\"徽标\",\"value\":\"{\\\"title\\\":\\\"本站点采用 CC BY 4.0 国际许可协议进行许可\\\",\\\"url\\\":\\\"https://creativecommons.or', '更新站点配置信息', '10.60.17.56', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 594, '2021-11-23 11:43:10', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `operation_log` VALUES (3, 'Admin', '/admin/siteSettings', 'POST', '{\"map\":{\"settings\":[{\"id\":1,\"nameEn\":\"webTitleSuffix\",\"nameZh\":\"网页标题后缀\",\"value\":\"网页标题后缀\",\"type\":1},{\"id\":2,\"nameEn\":\"blogName\",\"nameZh\":\"博客名称\",\"value\":\"博客名称\",\"type\":1},{\"id\":3,\"nameEn\":\"footerImgTitle\",\"nameZh\":\"页脚图片标题\",\"value\":\"页脚图片标题\",\"type\":1},{\"id\":4,\"nameEn\":\"footerImgUrl\",\"nameZh\":\"页脚图片路径\",\"value\":\"/img/qr.png\",\"type\":1},{\"id\":5,\"nameEn\":\"copyright\",\"nameZh\":\"Copyright\",\"value\":\"{\\\"title\\\":\\\"Copyright © 2019 - 2020\\\",\\\"siteName\\\":\\\"站点名称\\\"}\",\"type\":1},{\"id\":6,\"nameEn\":\"beian\",\"nameZh\":\"ICP备案号\",\"value\":\"\",\"type\":1},{\"id\":25,\"nameEn\":\"reward\",\"nameZh\":\"赞赏码路径\",\"value\":\"/img/reward.jpg\",\"type\":1},{\"id\":26,\"nameEn\":\"commentAdminFlag\",\"nameZh\":\"博主评论标识\",\"value\":\"博主评论标识\",\"type\":1},{\"id\":7,\"nameEn\":\"badge\",\"nameZh\":\"徽标\",\"value\":\"{\\\"title\\\":\\\"由 Spring Boot 强力驱动\\\",\\\"url\\\":\\\"https://spring.io/projects/spring-boot/\\\",\\\"subject\\\":\\\"Powered\\\",\\\"value\\\":\\\"Spring Boot\\\",\\\"color\\\":\\\"blue\\\"}\",\"type\":2},{\"id\":8,\"nameEn\":\"badge\",\"nameZh\":\"徽标\",\"value\":\"{\\\"title\\\":\\\"Vue.js 客户端渲染\\\",\\\"url\\\":\\\"https://cn.vuejs.org/\\\",\\\"subject\\\":\\\"SPA\\\",\\\"value\\\":\\\"Vue.js\\\",\\\"color\\\":\\\"brightgreen\\\"}\",\"type\":2},{\"id\":9,\"nameEn\":\"badge\",\"nameZh\":\"徽标\",\"value\":\"{\\\"title\\\":\\\"UI 框架 Semantic-UI\\\",\\\"url\\\":\\\"https://semantic-ui.com/\\\",\\\"subject\\\":\\\"UI\\\",\\\"value\\\":\\\"Semantic-UI\\\",\\\"color\\\":\\\"semantic-ui\\\"}\",\"type\":2},{\"id\":10,\"nameEn\":\"badge\",\"nameZh\":\"徽标\",\"value\":\"{\\\"title\\\":\\\"阿里云提供服务器及域名相关服务\\\",\\\"url\\\":\\\"https://www.aliyun.com/\\\",\\\"subject\\\":\\\"VPS & DNS\\\",\\\"value\\\":\\\"Aliyun\\\",\\\"color\\\":\\\"blueviolet\\\"}\",\"type\":2},{\"id\":11,\"nameEn\":\"badge\",\"nameZh\":\"徽标\",\"value\":\"{\\\"title\\\":\\\"jsDelivr 提供 CDN 加速服务\\\",\\\"url\\\":\\\"https://www.jsdelivr.com/\\\",\\\"subject\\\":\\\"CDN\\\",\\\"value\\\":\\\"jsDelivr\\\",\\\"color\\\":\\\"orange\\\"}\",\"type\":2},{\"id\":12,\"nameEn\":\"badge\",\"nameZh\":\"徽标\",\"value\":\"{\\\"title\\\":\\\"GitHub 提供图床\\\",\\\"url\\\":\\\"https://github.com/\\\",\\\"subject\\\":\\\"OSS\\\",\\\"value\\\":\\\"GitHub\\\",\\\"color\\\":\\\"github\\\"}\",\"type\":2},{\"id\":13,\"nameEn\":\"badge\",\"nameZh\":\"徽标\",\"value\":\"{\\\"title\\\":\\\"本站点采用 CC BY 4.0 国际许可协议进行许可\\\",\\\"url\\\":\\\"https://creativecommons.or', '更新站点配置信息', '10.60.17.56', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 556, '2021-11-23 11:44:03', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `operation_log` VALUES (4, 'Admin', '/admin/friendInfo/content', 'PUT', '{\"map\":{\"content\":\"友链页面信息\\n\\n\"}}', '修改友链页面信息', '10.60.17.56', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 73, '2021-11-23 11:45:49', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `operation_log` VALUES (5, 'Admin', '/admin/about', 'PUT', '{\"map\":{\"title\":\"关于我的标题\",\"musicId\":\"423015580\",\"content\":\"\",\"commentEnabled\":\"true\"}}', '修改关于我页面', '10.60.17.56', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 97, '2021-11-23 11:46:06', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `operation_log` VALUES (6, 'Admin', '/admin/about', 'PUT', '{\"map\":{\"title\":\"关于我的标题\",\"musicId\":\"423015580\",\"content\":\"关于我的正文\",\"commentEnabled\":\"true\"}}', '修改关于我页面', '10.60.17.56', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 107, '2021-11-23 11:47:15', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `operation_log` VALUES (7, 'Admin', '/admin/siteSettings', 'POST', '{\"map\":{\"settings\":[{\"id\":1,\"nameEn\":\"webTitleSuffix\",\"nameZh\":\"网页标题后缀\",\"value\":\"RBlog\",\"type\":1},{\"id\":2,\"nameEn\":\"blogName\",\"nameZh\":\"博客名称\",\"value\":\"RBlog\",\"type\":1},{\"id\":3,\"nameEn\":\"footerImgTitle\",\"nameZh\":\"页脚图片标题\",\"value\":\"我是页脚\",\"type\":1},{\"id\":4,\"nameEn\":\"footerImgUrl\",\"nameZh\":\"页脚图片路径\",\"value\":\"/img/qr.png\",\"type\":1},{\"id\":5,\"nameEn\":\"copyright\",\"nameZh\":\"Copyright\",\"value\":\"{\\\"title\\\":\\\"Copyright © 2019 - 2020\\\",\\\"siteName\\\":\\\"站点名称\\\"}\",\"type\":1},{\"id\":6,\"nameEn\":\"beian\",\"nameZh\":\"ICP备案号\",\"value\":\"\",\"type\":1},{\"id\":25,\"nameEn\":\"reward\",\"nameZh\":\"赞赏码路径\",\"value\":\"/img/reward.jpg\",\"type\":1},{\"id\":26,\"nameEn\":\"commentAdminFlag\",\"nameZh\":\"博主评论标识\",\"value\":\"本站大佬\",\"type\":1},{\"id\":7,\"nameEn\":\"badge\",\"nameZh\":\"徽标\",\"value\":\"{\\\"title\\\":\\\"由 Spring Boot 强力驱动\\\",\\\"url\\\":\\\"https://spring.io/projects/spring-boot/\\\",\\\"subject\\\":\\\"Powered\\\",\\\"value\\\":\\\"Spring Boot\\\",\\\"color\\\":\\\"blue\\\"}\",\"type\":2},{\"id\":8,\"nameEn\":\"badge\",\"nameZh\":\"徽标\",\"value\":\"{\\\"title\\\":\\\"Vue.js 客户端渲染\\\",\\\"url\\\":\\\"https://cn.vuejs.org/\\\",\\\"subject\\\":\\\"SPA\\\",\\\"value\\\":\\\"Vue.js\\\",\\\"color\\\":\\\"brightgreen\\\"}\",\"type\":2},{\"id\":9,\"nameEn\":\"badge\",\"nameZh\":\"徽标\",\"value\":\"{\\\"title\\\":\\\"UI 框架 Semantic-UI\\\",\\\"url\\\":\\\"https://semantic-ui.com/\\\",\\\"subject\\\":\\\"UI\\\",\\\"value\\\":\\\"Semantic-UI\\\",\\\"color\\\":\\\"semantic-ui\\\"}\",\"type\":2},{\"id\":10,\"nameEn\":\"badge\",\"nameZh\":\"徽标\",\"value\":\"{\\\"title\\\":\\\"阿里云提供服务器及域名相关服务\\\",\\\"url\\\":\\\"https://www.aliyun.com/\\\",\\\"subject\\\":\\\"VPS & DNS\\\",\\\"value\\\":\\\"Aliyun\\\",\\\"color\\\":\\\"blueviolet\\\"}\",\"type\":2},{\"id\":11,\"nameEn\":\"badge\",\"nameZh\":\"徽标\",\"value\":\"{\\\"title\\\":\\\"jsDelivr 提供 CDN 加速服务\\\",\\\"url\\\":\\\"https://www.jsdelivr.com/\\\",\\\"subject\\\":\\\"CDN\\\",\\\"value\\\":\\\"jsDelivr\\\",\\\"color\\\":\\\"orange\\\"}\",\"type\":2},{\"id\":12,\"nameEn\":\"badge\",\"nameZh\":\"徽标\",\"value\":\"{\\\"title\\\":\\\"GitHub 提供图床\\\",\\\"url\\\":\\\"https://github.com/\\\",\\\"subject\\\":\\\"OSS\\\",\\\"value\\\":\\\"GitHub\\\",\\\"color\\\":\\\"github\\\"}\",\"type\":2},{\"id\":13,\"nameEn\":\"badge\",\"nameZh\":\"徽标\",\"value\":\"{\\\"title\\\":\\\"本站点采用 CC BY 4.0 国际许可协议进行许可\\\",\\\"url\\\":\\\"https://creativecommons.org/li', '更新站点配置信息', '10.60.17.56', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 499, '2021-11-27 15:50:06', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `operation_log` VALUES (8, 'Admin', '/admin/siteSettings', 'POST', '{\"map\":{\"settings\":[{\"id\":1,\"nameEn\":\"webTitleSuffix\",\"nameZh\":\"网页标题后缀\",\"value\":\"RBlog\",\"type\":1},{\"id\":2,\"nameEn\":\"blogName\",\"nameZh\":\"博客名称\",\"value\":\"RBlog\",\"type\":1},{\"id\":3,\"nameEn\":\"footerImgTitle\",\"nameZh\":\"页脚图片标题\",\"value\":\"我是页脚\",\"type\":1},{\"id\":4,\"nameEn\":\"footerImgUrl\",\"nameZh\":\"页脚图片路径\",\"value\":\"/img/qr.png\",\"type\":1},{\"id\":5,\"nameEn\":\"copyright\",\"nameZh\":\"Copyright\",\"value\":\"{\\\"title\\\":\\\"Copyright © 2021 - 2022\\\",\\\"siteName\\\":\\\"RBlog\\\"}\",\"type\":1},{\"id\":6,\"nameEn\":\"beian\",\"nameZh\":\"ICP备案号\",\"value\":\"粤ICP备2021158929号\",\"type\":1},{\"id\":25,\"nameEn\":\"reward\",\"nameZh\":\"赞赏码路径\",\"value\":\"/img/reward.jpg\",\"type\":1},{\"id\":26,\"nameEn\":\"commentAdminFlag\",\"nameZh\":\"博主评论标识\",\"value\":\"本站大佬\",\"type\":1},{\"id\":7,\"nameEn\":\"badge\",\"nameZh\":\"徽标\",\"value\":\"{\\\"title\\\":\\\"由 Spring Boot 强力驱动\\\",\\\"url\\\":\\\"https://spring.io/projects/spring-boot/\\\",\\\"subject\\\":\\\"Powered\\\",\\\"value\\\":\\\"Spring Boot\\\",\\\"color\\\":\\\"blue\\\"}\",\"type\":2},{\"id\":8,\"nameEn\":\"badge\",\"nameZh\":\"徽标\",\"value\":\"{\\\"title\\\":\\\"Vue.js 客户端渲染\\\",\\\"url\\\":\\\"https://cn.vuejs.org/\\\",\\\"subject\\\":\\\"SPA\\\",\\\"value\\\":\\\"Vue.js\\\",\\\"color\\\":\\\"brightgreen\\\"}\",\"type\":2},{\"id\":9,\"nameEn\":\"badge\",\"nameZh\":\"徽标\",\"value\":\"{\\\"title\\\":\\\"UI 框架 Semantic-UI\\\",\\\"url\\\":\\\"https://semantic-ui.com/\\\",\\\"subject\\\":\\\"UI\\\",\\\"value\\\":\\\"Semantic-UI\\\",\\\"color\\\":\\\"semantic-ui\\\"}\",\"type\":2},{\"id\":10,\"nameEn\":\"badge\",\"nameZh\":\"徽标\",\"value\":\"{\\\"title\\\":\\\"阿里云提供服务器及域名相关服务\\\",\\\"url\\\":\\\"https://www.aliyun.com/\\\",\\\"subject\\\":\\\"VPS & DNS\\\",\\\"value\\\":\\\"Aliyun\\\",\\\"color\\\":\\\"blueviolet\\\"}\",\"type\":2},{\"id\":11,\"nameEn\":\"badge\",\"nameZh\":\"徽标\",\"value\":\"{\\\"title\\\":\\\"jsDelivr 提供 CDN 加速服务\\\",\\\"url\\\":\\\"https://www.jsdelivr.com/\\\",\\\"subject\\\":\\\"CDN\\\",\\\"value\\\":\\\"jsDelivr\\\",\\\"color\\\":\\\"orange\\\"}\",\"type\":2},{\"id\":12,\"nameEn\":\"badge\",\"nameZh\":\"徽标\",\"value\":\"{\\\"title\\\":\\\"GitHub 提供图床\\\",\\\"url\\\":\\\"https://github.com/\\\",\\\"subject\\\":\\\"OSS\\\",\\\"value\\\":\\\"GitHub\\\",\\\"color\\\":\\\"github\\\"}\",\"type\":2},{\"id\":13,\"nameEn\":\"badge\",\"nameZh\":\"徽标\",\"value\":\"{\\\"title\\\":\\\"本站点采用 CC BY 4.0 国际许可协议进行许可\\\",\\\"url\\\":\\\"https://creat', '更新站点配置信息', '10.60.17.56', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 537, '2021-11-27 15:56:32', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `operation_log` VALUES (9, 'Admin', '/admin/blog', 'POST', '{\"blog\":{\"id\":1,\"title\":\"本站第一篇文章的标题\",\"firstPicture\":\"1\",\"content\":\"本站第一篇文章的正文\",\"description\":\"本站第一篇文章的描述\",\"published\":true,\"recommend\":false,\"appreciation\":false,\"commentEnabled\":false,\"top\":false,\"createTime\":1637999882591,\"updateTime\":1637999882591,\"views\":0,\"words\":1000,\"readTime\":5,\"password\":\"\",\"user\":{\"id\":1,\"username\":null,\"password\":null,\"nickname\":null,\"avatar\":null,\"email\":null,\"createTime\":null,\"updateTime\":null,\"role\":null},\"category\":{\"id\":1,\"name\":\"伊始\",\"blogs\":[]},\"tags\":[],\"cate\":\"伊始\",\"tagList\":[\"建站\"]}}', '发布博客', '10.60.17.56', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 282, '2021-11-27 15:58:03', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');

-- ----------------------------
-- Table structure for schedule_job
-- ----------------------------
DROP TABLE IF EXISTS `schedule_job`;
CREATE TABLE `schedule_job`  (
  `job_id` bigint NOT NULL AUTO_INCREMENT COMMENT '任务id',
  `bean_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'spring bean名称',
  `method_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '方法名',
  `params` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '参数',
  `cron` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'cron表达式',
  `status` tinyint NULL DEFAULT NULL COMMENT '任务状态',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`job_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of schedule_job
-- ----------------------------
INSERT INTO `schedule_job` VALUES (1, 'redisSyncScheduleTask', 'syncBlogViewsToDatabase', '', '0 0 1 * * ?', 1, '每天凌晨一点，从Redis将博客浏览量同步到数据库', '2020-11-17 23:45:42');
INSERT INTO `schedule_job` VALUES (2, 'visitorSyncScheduleTask', 'syncVisitInfoToDatabase', '', '0 0 0 * * ?', 1, '清空当天Redis访客标识，记录当天的PV和UV，更新当天所有访客的PV和最后访问时间，更新城市新增访客UV数', '2021-02-05 08:14:28');

-- ----------------------------
-- Table structure for schedule_job_log
-- ----------------------------
DROP TABLE IF EXISTS `schedule_job_log`;
CREATE TABLE `schedule_job_log`  (
  `log_id` bigint NOT NULL AUTO_INCREMENT COMMENT '任务日志id',
  `job_id` bigint NOT NULL COMMENT '任务id',
  `bean_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'spring bean名称',
  `method_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '方法名',
  `params` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '参数',
  `status` tinyint NOT NULL COMMENT '任务执行结果',
  `error` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '异常信息',
  `times` int NOT NULL COMMENT '耗时（单位：毫秒）',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`log_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of schedule_job_log
-- ----------------------------
INSERT INTO `schedule_job_log` VALUES (1, 2, 'visitorSyncScheduleTask', 'syncVisitInfoToDatabase', '', 1, NULL, 53, '2021-12-18 08:00:00');
INSERT INTO `schedule_job_log` VALUES (2, 1, 'redisSyncScheduleTask', 'syncBlogViewsToDatabase', '', 1, NULL, 9, '2021-12-18 09:00:00');
INSERT INTO `schedule_job_log` VALUES (3, 2, 'visitorSyncScheduleTask', 'syncVisitInfoToDatabase', '', 1, NULL, 19, '2021-12-19 08:00:00');
INSERT INTO `schedule_job_log` VALUES (4, 1, 'redisSyncScheduleTask', 'syncBlogViewsToDatabase', '', 1, NULL, 2, '2021-12-19 09:00:00');
INSERT INTO `schedule_job_log` VALUES (5, 2, 'visitorSyncScheduleTask', 'syncVisitInfoToDatabase', '', 1, NULL, 15, '2021-12-20 08:00:00');
INSERT INTO `schedule_job_log` VALUES (6, 1, 'redisSyncScheduleTask', 'syncBlogViewsToDatabase', '', 1, NULL, 7, '2021-12-20 09:00:00');
INSERT INTO `schedule_job_log` VALUES (7, 2, 'visitorSyncScheduleTask', 'syncVisitInfoToDatabase', '', 1, NULL, 7, '2021-12-21 08:00:00');
INSERT INTO `schedule_job_log` VALUES (8, 1, 'redisSyncScheduleTask', 'syncBlogViewsToDatabase', '', 1, NULL, 6, '2021-12-21 09:00:00');

-- ----------------------------
-- Table structure for site_setting
-- ----------------------------
DROP TABLE IF EXISTS `site_setting`;
CREATE TABLE `site_setting`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name_en` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `name_zh` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `value` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL,
  `type` int NULL DEFAULT NULL COMMENT '1基础设置，2页脚徽标，3资料卡，4友链信息',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 30 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of site_setting
-- ----------------------------
INSERT INTO `site_setting` VALUES (1, 'webTitleSuffix', '网页标题后缀', 'RBlog', 1);
INSERT INTO `site_setting` VALUES (2, 'blogName', '博客名称', 'RBlog', 1);
INSERT INTO `site_setting` VALUES (3, 'footerImgTitle', '页脚图片标题', '我是页脚', 1);
INSERT INTO `site_setting` VALUES (4, 'footerImgUrl', '页脚图片路径', '/img/qr.png', 1);
INSERT INTO `site_setting` VALUES (5, 'copyright', 'Copyright', '{\"title\":\"Copyright © 2021 - 2022\",\"siteName\":\"RBlog\"}', 1);
INSERT INTO `site_setting` VALUES (6, 'beian', 'ICP备案号', '粤ICP备2021158929号', 1);
INSERT INTO `site_setting` VALUES (7, 'badge', '徽标', '{\"title\":\"由 Spring Boot 强力驱动\",\"url\":\"https://spring.io/projects/spring-boot/\",\"subject\":\"Powered\",\"value\":\"Spring Boot\",\"color\":\"blue\"}', 2);
INSERT INTO `site_setting` VALUES (8, 'badge', '徽标', '{\"title\":\"Vue.js 客户端渲染\",\"url\":\"https://cn.vuejs.org/\",\"subject\":\"SPA\",\"value\":\"Vue.js\",\"color\":\"brightgreen\"}', 2);
INSERT INTO `site_setting` VALUES (9, 'badge', '徽标', '{\"title\":\"UI 框架 Semantic-UI\",\"url\":\"https://semantic-ui.com/\",\"subject\":\"UI\",\"value\":\"Semantic-UI\",\"color\":\"semantic-ui\"}', 2);
INSERT INTO `site_setting` VALUES (10, 'badge', '徽标', '{\"title\":\"阿里云提供服务器及域名相关服务\",\"url\":\"https://www.aliyun.com/\",\"subject\":\"VPS & DNS\",\"value\":\"Aliyun\",\"color\":\"blueviolet\"}', 2);
INSERT INTO `site_setting` VALUES (11, 'badge', '徽标', '{\"title\":\"jsDelivr 提供 CDN 加速服务\",\"url\":\"https://www.jsdelivr.com/\",\"subject\":\"CDN\",\"value\":\"jsDelivr\",\"color\":\"orange\"}', 2);
INSERT INTO `site_setting` VALUES (12, 'badge', '徽标', '{\"title\":\"GitHub 提供图床\",\"url\":\"https://github.com/\",\"subject\":\"OSS\",\"value\":\"GitHub\",\"color\":\"github\"}', 2);
INSERT INTO `site_setting` VALUES (13, 'badge', '徽标', '{\"title\":\"本站点采用 CC BY 4.0 国际许可协议进行许可\",\"url\":\"https://creativecommons.org/licenses/by/4.0/\",\"subject\":\"CC\",\"value\":\"BY 4.0\",\"color\":\"lightgray\"}', 2);
INSERT INTO `site_setting` VALUES (14, 'avatar', '图片路径', '/img/avatar.jpg', 3);
INSERT INTO `site_setting` VALUES (15, 'name', '昵称', '梦魇梦狸', 3);
INSERT INTO `site_setting` VALUES (16, 'rollText', '滚动个签', '\"眼中有世界，心中方有天地；\",\"危楼高百尺，手可摘星宸\"', 3);
INSERT INTO `site_setting` VALUES (17, 'github', 'GitHub地址', 'https://github.com/raxcl', 3);
INSERT INTO `site_setting` VALUES (18, 'qq', 'QQ链接', 'http://sighttp.qq.com/authd?IDKEY=', 3);
INSERT INTO `site_setting` VALUES (19, 'bilibili', 'bilibili链接', 'https://space.bilibili.com/', 3);
INSERT INTO `site_setting` VALUES (20, 'netease', '网易云音乐', 'https://music.163.com/#/user/home?id=', 3);
INSERT INTO `site_setting` VALUES (21, 'email', 'email', 'raxcl@qq.com', 3);
INSERT INTO `site_setting` VALUES (22, 'favorite', '自定义', '{\"title\":\"追番回忆录\",\"content\":\"摇铃啊摇铃\"}', 3);
INSERT INTO `site_setting` VALUES (23, 'favorite', '自定义', '{\"title\":\"能力印章\",\"content\":\"java\"}', 3);
INSERT INTO `site_setting` VALUES (24, 'favorite', '自定义', '{\"title\":\"未来笔记\",\"content\":\"双城记\"}', 3);
INSERT INTO `site_setting` VALUES (25, 'reward', '赞赏码路径', '/img/reward.jpg', 1);
INSERT INTO `site_setting` VALUES (26, 'commentAdminFlag', '博主评论标识', '本站大佬', 1);
INSERT INTO `site_setting` VALUES (27, 'friendContent', '友链页面信息', '友链页面信息\n\n', 4);
INSERT INTO `site_setting` VALUES (28, 'friendCommentEnabled', '友链页面评论开关', '1', 4);
INSERT INTO `site_setting` VALUES (29, 'gongan', '公安备案号', '粤公网安备 44030502008422号', 1);

-- ----------------------------
-- Table structure for tag
-- ----------------------------
DROP TABLE IF EXISTS `tag`;
CREATE TABLE `tag`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `tag_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `color` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '标签颜色(可选)',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of tag
-- ----------------------------
INSERT INTO `tag` VALUES (1, '建站', NULL);

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户名',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '密码',
  `nickname` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '昵称',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '头像地址',
  `email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '邮箱',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  `role` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '角色访问权限',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (1, 'Admin', '$2a$10$4wnwMW8Z4Bn6wR4K1YlbquQunlHM/4rvudVBX8oyfx16xeVtI6i7C', 'Admin', '/img/avatar.jpg', 'admin@naccl.top', '2020-09-21 16:47:18', '2020-09-21 16:47:22', 'ROLE_admin');

-- ----------------------------
-- Table structure for visit_log
-- ----------------------------
DROP TABLE IF EXISTS `visit_log`;
CREATE TABLE `visit_log`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `uuid` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '访客标识码',
  `uri` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '请求接口',
  `method` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '请求方式',
  `param` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '请求参数',
  `behavior` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '访问行为',
  `content` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '访问内容',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `ip` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'ip',
  `ip_source` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'ip来源',
  `os` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '操作系统',
  `browser` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '浏览器',
  `times` int NOT NULL COMMENT '请求耗时（毫秒）',
  `create_time` datetime NOT NULL COMMENT '访问时间',
  `user_agent` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'user-agent用户代理',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 244 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of visit_log
-- ----------------------------
INSERT INTO `visit_log` VALUES (1, 'da376f08-974e-35ba-93ee-260405633908', '/blogs', 'GET', '{\"pageNum\":1}', '访问页面', '首页', '第1页', '10.60.17.56', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 99, '2021-11-23 11:28:14', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (2, 'da376f08-974e-35ba-93ee-260405633908', '/blogs', 'GET', '{\"pageNum\":1}', '访问页面', '首页', '第1页', '10.60.17.56', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 42, '2021-11-23 11:33:13', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (3, 'da376f08-974e-35ba-93ee-260405633908', '/blogs', 'GET', '{\"pageNum\":1}', '访问页面', '首页', '第1页', '10.60.17.56', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 51, '2021-11-23 11:43:17', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (4, 'da376f08-974e-35ba-93ee-260405633908', '/blogs', 'GET', '{\"pageNum\":1}', '访问页面', '首页', '第1页', '10.60.17.56', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 26, '2021-11-23 11:44:15', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (5, 'da376f08-974e-35ba-93ee-260405633908', '/archives', 'GET', '{}', '访问页面', '文章归档', '', '10.60.17.56', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 52, '2021-11-23 11:44:38', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (6, 'da376f08-974e-35ba-93ee-260405633908', '/moments', 'GET', '{\"pageNum\":1,\"jwt\":\"\"}', '访问页面', '动态', '第1页', '10.60.17.56', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 25, '2021-11-23 11:44:41', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (7, 'da376f08-974e-35ba-93ee-260405633908', '/friends', 'GET', '{}', '访问页面', '友链', '', '10.60.17.56', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 137, '2021-11-23 11:44:43', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (8, 'da376f08-974e-35ba-93ee-260405633908', '/about', 'GET', '{}', '访问页面', '关于我', '', '10.60.17.56', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 41, '2021-11-23 11:44:46', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (9, 'da376f08-974e-35ba-93ee-260405633908', '/friends', 'GET', '{}', '访问页面', '友链', '', '10.60.17.56', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 29, '2021-11-23 11:44:50', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (10, 'da376f08-974e-35ba-93ee-260405633908', '/moments', 'GET', '{\"pageNum\":1,\"jwt\":\"\"}', '访问页面', '动态', '第1页', '10.60.17.56', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 21, '2021-11-23 11:44:51', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (11, 'da376f08-974e-35ba-93ee-260405633908', '/friends', 'GET', '{}', '访问页面', '友链', '', '10.60.17.56', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 31, '2021-11-23 11:44:52', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (12, 'da376f08-974e-35ba-93ee-260405633908', '/friends', 'GET', '{}', '访问页面', '友链', '', '10.60.17.56', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 53, '2021-11-23 11:46:28', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (13, 'da376f08-974e-35ba-93ee-260405633908', '/friends', 'GET', '{}', '访问页面', '友链', '', '10.60.17.56', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 36, '2021-11-23 11:47:19', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (14, 'da376f08-974e-35ba-93ee-260405633908', '/about', 'GET', '{}', '访问页面', '关于我', '', '10.60.17.56', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 56, '2021-11-23 11:47:23', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (15, 'da376f08-974e-35ba-93ee-260405633908', '/friends', 'GET', '{}', '访问页面', '友链', '', '10.60.17.56', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 29, '2021-11-23 11:47:27', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (16, 'da376f08-974e-35ba-93ee-260405633908', '/moments', 'GET', '{\"pageNum\":1,\"jwt\":\"\"}', '访问页面', '动态', '第1页', '10.60.17.56', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 21, '2021-11-23 11:51:58', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (17, 'da376f08-974e-35ba-93ee-260405633908', '/archives', 'GET', '{}', '访问页面', '文章归档', '', '10.60.17.56', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 9, '2021-11-23 11:51:59', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (18, 'da376f08-974e-35ba-93ee-260405633908', '/moments', 'GET', '{\"pageNum\":1,\"jwt\":\"\"}', '访问页面', '动态', '第1页', '10.60.17.56', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 20, '2021-11-23 11:52:00', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (19, 'da376f08-974e-35ba-93ee-260405633908', '/about', 'GET', '{}', '访问页面', '关于我', '', '10.60.17.56', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 9, '2021-11-23 11:52:01', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (20, 'da376f08-974e-35ba-93ee-260405633908', '/blogs', 'GET', '{\"pageNum\":1}', '访问页面', '首页', '第1页', '10.60.17.56', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 18, '2021-11-23 11:52:12', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (21, 'da376f08-974e-35ba-93ee-260405633908', '/archives', 'GET', '{}', '访问页面', '文章归档', '', '10.60.17.56', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 134, '2021-11-23 13:25:52', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (22, 'da376f08-974e-35ba-93ee-260405633908', '/archives', 'GET', '{}', '访问页面', '文章归档', '', '10.60.17.56', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 10, '2021-11-23 13:32:24', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (23, 'da376f08-974e-35ba-93ee-260405633908', '/blogs', 'GET', '{\"pageNum\":1}', '访问页面', '首页', '第1页', '10.60.17.56', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 40, '2021-11-23 13:32:24', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (24, 'da376f08-974e-35ba-93ee-260405633908', '/about', 'GET', '{}', '访问页面', '关于我', '', '10.60.17.56', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 124, '2021-11-23 19:23:54', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (25, 'da376f08-974e-35ba-93ee-260405633908', '/friends', 'GET', '{}', '访问页面', '友链', '', '10.60.17.56', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 50, '2021-11-23 19:24:15', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (26, 'da376f08-974e-35ba-93ee-260405633908', '/friends', 'GET', '{}', '访问页面', '友链', '', '10.60.17.56', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 101, '2021-11-23 19:31:56', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (27, 'da376f08-974e-35ba-93ee-260405633908', '/friends', 'GET', '{}', '访问页面', '友链', '', '10.60.17.56', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 33, '2021-11-23 19:32:19', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (28, 'da376f08-974e-35ba-93ee-260405633908', '/moments', 'GET', '{\"pageNum\":1,\"jwt\":\"\"}', '访问页面', '动态', '第1页', '10.60.17.56', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 22, '2021-11-23 19:32:23', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (29, 'da376f08-974e-35ba-93ee-260405633908', '/archives', 'GET', '{}', '访问页面', '文章归档', '', '10.60.17.56', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 53, '2021-11-23 19:32:28', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (30, 'da376f08-974e-35ba-93ee-260405633908', '/about', 'GET', '{}', '访问页面', '关于我', '', '10.60.17.56', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 11, '2021-11-23 19:32:29', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (31, '265620e0-d7b2-37e3-8aa5-cccbd1882162', '/about', 'GET', '{}', '访问页面', '关于我', '', '10.60.17.56', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 21, '2021-11-23 19:32:38', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (32, 'da376f08-974e-35ba-93ee-260405633908', '/about', 'GET', '{}', '访问页面', '关于我', '', '10.60.17.56', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 123, '2021-11-23 19:45:22', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (33, 'da376f08-974e-35ba-93ee-260405633908', '/friends', 'GET', '{}', '访问页面', '友链', '', '10.60.17.56', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 60, '2021-11-23 19:45:25', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (34, 'da376f08-974e-35ba-93ee-260405633908', '/about', 'GET', '{}', '访问页面', '关于我', '', '10.60.17.56', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 10, '2021-11-23 19:45:26', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (35, 'da376f08-974e-35ba-93ee-260405633908', '/about', 'GET', '{}', '访问页面', '关于我', '', '10.60.17.56', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 22, '2021-11-23 19:58:14', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (36, 'da376f08-974e-35ba-93ee-260405633908', '/friends', 'GET', '{}', '访问页面', '友链', '', '10.60.17.56', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 78, '2021-11-23 19:58:20', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (37, 'da376f08-974e-35ba-93ee-260405633908', '/moments', 'GET', '{\"pageNum\":1,\"jwt\":\"\"}', '访问页面', '动态', '第1页', '10.60.17.56', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 19, '2021-11-23 19:58:22', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (38, 'da376f08-974e-35ba-93ee-260405633908', '/about', 'GET', '{}', '访问页面', '关于我', '', '10.60.17.56', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 11, '2021-11-23 19:58:23', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (39, 'da376f08-974e-35ba-93ee-260405633908', '/friends', 'GET', '{}', '访问页面', '友链', '', '10.60.17.56', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 41, '2021-11-23 19:58:24', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (40, 'da376f08-974e-35ba-93ee-260405633908', '/friends', 'GET', '{}', '访问页面', '友链', '', '10.60.17.56', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 43, '2021-11-23 19:58:27', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (41, 'da376f08-974e-35ba-93ee-260405633908', '/moments', 'GET', '{\"pageNum\":1,\"jwt\":\"\"}', '访问页面', '动态', '第1页', '10.60.17.56', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 18, '2021-11-23 19:58:29', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (42, 'da376f08-974e-35ba-93ee-260405633908', '/archives', 'GET', '{}', '访问页面', '文章归档', '', '10.60.17.56', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 50, '2021-11-23 19:58:31', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (43, 'da376f08-974e-35ba-93ee-260405633908', '/moments', 'GET', '{\"pageNum\":1,\"jwt\":\"\"}', '访问页面', '动态', '第1页', '10.60.17.56', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 19, '2021-11-23 19:58:36', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (44, 'da376f08-974e-35ba-93ee-260405633908', '/about', 'GET', '{}', '访问页面', '关于我', '', '10.60.17.56', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 11, '2021-11-23 19:58:36', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (45, 'da376f08-974e-35ba-93ee-260405633908', '/about', 'GET', '{}', '访问页面', '关于我', '', '10.60.17.56', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 20, '2021-11-23 19:58:39', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (46, 'da376f08-974e-35ba-93ee-260405633908', '/blogs', 'GET', '{\"pageNum\":1}', '访问页面', '首页', '第1页', '10.60.17.56', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 41, '2021-11-23 19:58:41', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (47, 'da376f08-974e-35ba-93ee-260405633908', '/friends', 'GET', '{}', '访问页面', '友链', '', '10.60.17.56', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 31, '2021-11-23 19:58:44', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (48, 'da376f08-974e-35ba-93ee-260405633908', '/archives', 'GET', '{}', '访问页面', '文章归档', '', '10.60.17.56', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 12, '2021-11-23 19:59:34', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (49, 'da376f08-974e-35ba-93ee-260405633908', '/moments', 'GET', '{\"pageNum\":1,\"jwt\":\"\"}', '访问页面', '动态', '第1页', '10.60.17.56', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 8, '2021-11-23 19:59:35', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (50, 'da376f08-974e-35ba-93ee-260405633908', '/archives', 'GET', '{}', '访问页面', '文章归档', '', '10.60.17.56', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 11, '2021-11-23 19:59:36', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (51, 'da376f08-974e-35ba-93ee-260405633908', '/moments', 'GET', '{\"pageNum\":1,\"jwt\":\"\"}', '访问页面', '动态', '第1页', '10.60.17.56', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 20, '2021-11-23 19:59:36', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (52, 'da376f08-974e-35ba-93ee-260405633908', '/friends', 'GET', '{}', '访问页面', '友链', '', '10.60.17.56', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 20, '2021-11-23 19:59:37', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (53, 'da376f08-974e-35ba-93ee-260405633908', '/about', 'GET', '{}', '访问页面', '关于我', '', '10.60.17.56', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 11, '2021-11-23 19:59:37', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (54, 'da376f08-974e-35ba-93ee-260405633908', '/friends', 'GET', '{}', '访问页面', '友链', '', '10.60.17.56', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 36, '2021-11-23 19:59:38', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (55, 'da376f08-974e-35ba-93ee-260405633908', '/moments', 'GET', '{\"pageNum\":1,\"jwt\":\"\"}', '访问页面', '动态', '第1页', '10.60.17.56', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 11, '2021-11-23 19:59:39', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (56, 'da376f08-974e-35ba-93ee-260405633908', '/archives', 'GET', '{}', '访问页面', '文章归档', '', '10.60.17.56', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 10, '2021-11-23 19:59:39', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (57, 'da376f08-974e-35ba-93ee-260405633908', '/archives', 'GET', '{}', '访问页面', '文章归档', '', '10.60.17.56', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 47, '2021-11-23 20:04:44', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (58, 'da376f08-974e-35ba-93ee-260405633908', '/archives', 'GET', '{}', '访问页面', '文章归档', '', '10.60.17.56', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 22, '2021-11-23 20:05:04', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (59, 'da376f08-974e-35ba-93ee-260405633908', '/archives', 'GET', '{}', '访问页面', '文章归档', '', '10.60.17.56', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 21, '2021-11-23 20:05:13', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (60, 'da376f08-974e-35ba-93ee-260405633908', '/archives', 'GET', '{}', '访问页面', '文章归档', '', '10.60.17.56', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 20, '2021-11-23 20:05:16', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (61, 'da376f08-974e-35ba-93ee-260405633908', '/archives', 'GET', '{}', '访问页面', '文章归档', '', '10.60.17.56', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 22, '2021-11-23 20:05:33', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (62, 'da376f08-974e-35ba-93ee-260405633908', '/archives', 'GET', '{}', '访问页面', '文章归档', '', '10.60.17.56', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 20, '2021-11-23 20:05:37', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (63, 'da376f08-974e-35ba-93ee-260405633908', '/friends', 'GET', '{}', '访问页面', '友链', '', '10.60.17.56', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 130, '2021-11-23 20:26:13', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (64, 'da376f08-974e-35ba-93ee-260405633908', '/about', 'GET', '{}', '访问页面', '关于我', '', '10.60.17.56', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 38, '2021-11-23 20:26:13', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (65, 'da376f08-974e-35ba-93ee-260405633908', '/friends', 'GET', '{}', '访问页面', '友链', '', '10.60.17.56', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 28, '2021-11-23 20:26:34', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (66, 'da376f08-974e-35ba-93ee-260405633908', '/friends', 'GET', '{}', '访问页面', '友链', '', '10.60.17.56', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 28, '2021-11-23 20:27:53', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (67, 'da376f08-974e-35ba-93ee-260405633908', '/friends', 'GET', '{}', '访问页面', '友链', '', '10.60.17.56', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 34, '2021-11-23 20:28:21', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (68, 'da376f08-974e-35ba-93ee-260405633908', '/friends', 'GET', '{}', '访问页面', '友链', '', '10.60.17.56', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 36, '2021-11-23 20:28:26', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (69, 'da376f08-974e-35ba-93ee-260405633908', '/friends', 'GET', '{}', '访问页面', '友链', '', '10.60.17.56', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 29, '2021-11-23 20:28:35', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (70, 'da376f08-974e-35ba-93ee-260405633908', '/friends', 'GET', '{}', '访问页面', '友链', '', '10.60.17.56', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 28, '2021-11-23 20:28:36', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (71, 'da376f08-974e-35ba-93ee-260405633908', '/friends', 'GET', '{}', '访问页面', '友链', '', '10.60.17.56', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 39, '2021-11-23 20:28:39', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (72, 'da376f08-974e-35ba-93ee-260405633908', '/friends', 'GET', '{}', '访问页面', '友链', '', '10.60.17.56', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 31, '2021-11-23 20:28:41', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (73, 'da376f08-974e-35ba-93ee-260405633908', '/friends', 'GET', '{}', '访问页面', '友链', '', '10.60.17.56', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 39, '2021-11-23 20:29:09', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (74, 'da376f08-974e-35ba-93ee-260405633908', '/friends', 'GET', '{}', '访问页面', '友链', '', '10.60.17.56', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 30, '2021-11-23 20:30:27', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (75, 'da376f08-974e-35ba-93ee-260405633908', '/blogs', 'GET', '{\"pageNum\":1}', '访问页面', '首页', '第1页', '10.60.17.56', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 49, '2021-11-23 20:30:30', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (76, 'da376f08-974e-35ba-93ee-260405633908', '/archives', 'GET', '{}', '访问页面', '文章归档', '', '10.60.17.56', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 61, '2021-11-23 20:30:34', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (77, 'da376f08-974e-35ba-93ee-260405633908', '/blogs', 'GET', '{\"pageNum\":1}', '访问页面', '首页', '第1页', '10.60.17.56', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 19, '2021-11-23 20:30:35', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (78, 'da376f08-974e-35ba-93ee-260405633908', '/moments', 'GET', '{\"pageNum\":1,\"jwt\":\"\"}', '访问页面', '动态', '第1页', '10.60.17.56', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 17, '2021-11-23 20:30:37', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (79, 'da376f08-974e-35ba-93ee-260405633908', '/blogs', 'GET', '{\"pageNum\":1}', '访问页面', '首页', '第1页', '10.60.17.56', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 26, '2021-11-23 20:30:37', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (80, 'da376f08-974e-35ba-93ee-260405633908', '/archives', 'GET', '{}', '访问页面', '文章归档', '', '10.60.17.56', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 9, '2021-11-23 20:30:38', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (81, 'da376f08-974e-35ba-93ee-260405633908', '/moments', 'GET', '{\"pageNum\":1,\"jwt\":\"\"}', '访问页面', '动态', '第1页', '10.60.17.56', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 18, '2021-11-23 20:30:39', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (82, 'da376f08-974e-35ba-93ee-260405633908', '/archives', 'GET', '{}', '访问页面', '文章归档', '', '10.60.17.56', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 10, '2021-11-23 20:30:40', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (83, 'da376f08-974e-35ba-93ee-260405633908', '/blogs', 'GET', '{\"pageNum\":1}', '访问页面', '首页', '第1页', '10.60.17.56', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 20, '2021-11-23 20:30:42', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (84, 'da376f08-974e-35ba-93ee-260405633908', '/moments', 'GET', '{\"pageNum\":1,\"jwt\":\"\"}', '访问页面', '动态', '第1页', '10.60.17.56', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 18, '2021-11-23 20:30:43', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (85, 'da376f08-974e-35ba-93ee-260405633908', '/blogs', 'GET', '{\"pageNum\":1}', '访问页面', '首页', '第1页', '10.60.17.56', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 19, '2021-11-23 20:30:43', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (86, 'da376f08-974e-35ba-93ee-260405633908', '/moments', 'GET', '{\"pageNum\":1,\"jwt\":\"\"}', '访问页面', '动态', '第1页', '10.60.17.56', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 20, '2021-11-23 20:30:44', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (87, 'da376f08-974e-35ba-93ee-260405633908', '/blogs', 'GET', '{\"pageNum\":1}', '访问页面', '首页', '第1页', '10.60.17.56', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 20, '2021-11-23 20:30:45', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (88, 'da376f08-974e-35ba-93ee-260405633908', '/friends', 'GET', '{}', '访问页面', '友链', '', '10.60.17.56', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 28, '2021-11-23 20:30:46', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (89, 'da376f08-974e-35ba-93ee-260405633908', '/blogs', 'GET', '{\"pageNum\":1}', '访问页面', '首页', '第1页', '10.60.17.56', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 19, '2021-11-23 20:30:46', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (90, 'da376f08-974e-35ba-93ee-260405633908', '/moments', 'GET', '{\"pageNum\":1,\"jwt\":\"\"}', '访问页面', '动态', '第1页', '10.60.17.56', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 18, '2021-11-23 20:30:47', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (91, 'da376f08-974e-35ba-93ee-260405633908', '/blogs', 'GET', '{\"pageNum\":1}', '访问页面', '首页', '第1页', '10.60.17.56', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 19, '2021-11-23 20:30:50', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (92, 'da376f08-974e-35ba-93ee-260405633908', '/moments', 'GET', '{\"pageNum\":1,\"jwt\":\"\"}', '访问页面', '动态', '第1页', '10.60.17.56', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 18, '2021-11-23 20:30:50', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (93, 'da376f08-974e-35ba-93ee-260405633908', '/about', 'GET', '{}', '访问页面', '关于我', '', '10.60.17.56', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 10, '2021-11-23 20:30:51', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (94, 'da376f08-974e-35ba-93ee-260405633908', '/blogs', 'GET', '{\"pageNum\":1}', '访问页面', '首页', '第1页', '10.60.17.56', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 21, '2021-11-23 20:30:52', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (95, 'da376f08-974e-35ba-93ee-260405633908', '/about', 'GET', '{}', '访问页面', '关于我', '', '10.60.17.56', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 11, '2021-11-23 20:30:53', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (96, 'da376f08-974e-35ba-93ee-260405633908', '/blogs', 'GET', '{\"pageNum\":1}', '访问页面', '首页', '第1页', '10.60.17.56', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 22, '2021-11-23 20:30:54', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (97, 'da376f08-974e-35ba-93ee-260405633908', '/moments', 'GET', '{\"pageNum\":1,\"jwt\":\"\"}', '访问页面', '动态', '第1页', '10.60.17.56', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 19, '2021-11-23 20:30:58', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (98, 'da376f08-974e-35ba-93ee-260405633908', '/about', 'GET', '{}', '访问页面', '关于我', '', '10.60.17.56', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 11, '2021-11-23 20:30:59', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (99, 'da376f08-974e-35ba-93ee-260405633908', '/archives', 'GET', '{}', '访问页面', '文章归档', '', '10.60.17.56', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 13, '2021-11-23 20:31:00', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (100, 'da376f08-974e-35ba-93ee-260405633908', '/moments', 'GET', '{\"pageNum\":1,\"jwt\":\"\"}', '访问页面', '动态', '第1页', '10.60.17.56', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 18, '2021-11-23 20:31:01', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (101, 'da376f08-974e-35ba-93ee-260405633908', '/archives', 'GET', '{}', '访问页面', '文章归档', '', '10.60.17.56', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 10, '2021-11-23 20:31:02', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (102, 'da376f08-974e-35ba-93ee-260405633908', '/moments', 'GET', '{\"pageNum\":1,\"jwt\":\"\"}', '访问页面', '动态', '第1页', '10.60.17.56', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 20, '2021-11-23 20:31:02', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (103, 'da376f08-974e-35ba-93ee-260405633908', '/about', 'GET', '{}', '访问页面', '关于我', '', '10.60.17.56', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 11, '2021-11-23 20:31:03', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (104, 'da376f08-974e-35ba-93ee-260405633908', '/blogs', 'GET', '{\"pageNum\":1}', '访问页面', '首页', '第1页', '10.60.17.56', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 20, '2021-11-23 20:31:04', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (105, 'da376f08-974e-35ba-93ee-260405633908', '/moments', 'GET', '{\"pageNum\":1,\"jwt\":\"\"}', '访问页面', '动态', '第1页', '10.60.17.56', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 20, '2021-11-23 20:31:05', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (106, 'da376f08-974e-35ba-93ee-260405633908', '/blogs', 'GET', '{\"pageNum\":1}', '访问页面', '首页', '第1页', '10.60.17.56', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 22, '2021-11-23 20:31:06', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (107, 'da376f08-974e-35ba-93ee-260405633908', '/archives', 'GET', '{}', '访问页面', '文章归档', '', '10.60.17.56', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 10, '2021-11-23 20:31:39', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (108, 'da376f08-974e-35ba-93ee-260405633908', '/moments', 'GET', '{\"pageNum\":1,\"jwt\":\"\"}', '访问页面', '动态', '第1页', '10.60.17.56', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 18, '2021-11-23 20:31:40', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (109, 'da376f08-974e-35ba-93ee-260405633908', '/blogs', 'GET', '{\"pageNum\":1}', '访问页面', '首页', '第1页', '10.60.17.56', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 20, '2021-11-23 20:31:40', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (110, 'da376f08-974e-35ba-93ee-260405633908', '/blogs', 'GET', '{\"pageNum\":1}', '访问页面', '首页', '第1页', '10.60.17.56', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 29, '2021-11-23 20:43:31', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (111, 'da376f08-974e-35ba-93ee-260405633908', '/moments', 'GET', '{\"pageNum\":1,\"jwt\":\"\"}', '访问页面', '动态', '第1页', '10.60.17.56', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 21, '2021-11-23 20:43:35', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (112, 'da376f08-974e-35ba-93ee-260405633908', '/friends', 'GET', '{}', '访问页面', '友链', '', '10.60.17.56', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 31, '2021-11-23 20:43:37', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (113, 'da376f08-974e-35ba-93ee-260405633908', '/about', 'GET', '{}', '访问页面', '关于我', '', '10.60.17.56', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 9, '2021-11-23 20:43:39', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (114, 'bc192880-925d-3ca1-9697-5b494d4318f2', '/api/archives', 'GET', '{}', '访问页面', '文章归档', '', '172.17.0.5', '内网IP|内网IP', 'Unknown ??', 'PostmanRuntime 7.28.4', 49, '2021-11-27 15:18:16', 'PostmanRuntime/7.28.4');
INSERT INTO `visit_log` VALUES (115, 'bc192880-925d-3ca1-9697-5b494d4318f2', '/api/archives', 'GET', '{}', '访问页面', '文章归档', '', '172.17.0.5', '内网IP|内网IP', 'Unknown ??', 'PostmanRuntime 7.28.4', 72, '2021-11-27 15:30:09', 'PostmanRuntime/7.28.4');
INSERT INTO `visit_log` VALUES (116, '0f5fe4ae-3f4b-3e37-a288-751a0c8fccad', '/api/blogs', 'GET', '{\"pageNum\":1}', '访问页面', '首页', '第1页', '172.17.0.5', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 145, '2021-11-27 15:30:33', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (117, '0f5fe4ae-3f4b-3e37-a288-751a0c8fccad', '/api/blogs', 'GET', '{\"pageNum\":1}', '访问页面', '首页', '第1页', '172.17.0.5', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 17, '2021-11-27 15:30:47', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (118, '0f5fe4ae-3f4b-3e37-a288-751a0c8fccad', '/api/archives', 'GET', '{}', '访问页面', '文章归档', '', '172.17.0.5', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 2, '2021-11-27 15:30:51', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (119, '0f5fe4ae-3f4b-3e37-a288-751a0c8fccad', '/api/moments', 'GET', '{\"pageNum\":1,\"jwt\":\"\"}', '访问页面', '动态', '第1页', '172.17.0.5', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 14, '2021-11-27 15:30:52', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (120, '0f5fe4ae-3f4b-3e37-a288-751a0c8fccad', '/api/friends', 'GET', '{}', '访问页面', '友链', '', '172.17.0.5', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 93, '2021-11-27 15:30:53', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (121, '0f5fe4ae-3f4b-3e37-a288-751a0c8fccad', '/api/about', 'GET', '{}', '访问页面', '关于我', '', '172.17.0.5', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 16, '2021-11-27 15:30:54', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (122, '0f5fe4ae-3f4b-3e37-a288-751a0c8fccad', '/api/friends', 'GET', '{}', '访问页面', '友链', '', '172.17.0.5', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 4, '2021-11-27 15:30:55', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (123, '0f5fe4ae-3f4b-3e37-a288-751a0c8fccad', '/api/blogs', 'GET', '{\"pageNum\":1}', '访问页面', '首页', '第1页', '172.17.0.5', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 3, '2021-11-27 15:30:57', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (124, '0f5fe4ae-3f4b-3e37-a288-751a0c8fccad', '/api/archives', 'GET', '{}', '访问页面', '文章归档', '', '172.17.0.5', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 2, '2021-11-27 15:31:20', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (125, '0f5fe4ae-3f4b-3e37-a288-751a0c8fccad', '/api/blogs', 'GET', '{\"pageNum\":1}', '访问页面', '首页', '第1页', '172.17.0.5', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 4, '2021-11-27 15:31:31', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (126, '0f5fe4ae-3f4b-3e37-a288-751a0c8fccad', '/api/blogs', 'GET', '{\"pageNum\":1}', '访问页面', '首页', '第1页', '172.17.0.5', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 5, '2021-11-27 15:31:57', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (127, '0f5fe4ae-3f4b-3e37-a288-751a0c8fccad', '/api/friends', 'GET', '{}', '访问页面', '友链', '', '172.17.0.5', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 3, '2021-11-27 15:32:08', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (128, '0f5fe4ae-3f4b-3e37-a288-751a0c8fccad', '/api/about', 'GET', '{}', '访问页面', '关于我', '', '172.17.0.5', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 2, '2021-11-27 15:32:09', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (129, '0f5fe4ae-3f4b-3e37-a288-751a0c8fccad', '/api/blogs', 'GET', '{\"pageNum\":1}', '访问页面', '首页', '第1页', '172.17.0.5', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 151, '2021-11-27 15:34:11', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (130, '0f5fe4ae-3f4b-3e37-a288-751a0c8fccad', '/api/friends', 'GET', '{}', '访问页面', '友链', '', '172.17.0.5', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 17, '2021-11-27 15:34:18', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (131, '0f5fe4ae-3f4b-3e37-a288-751a0c8fccad', '/api/moments', 'GET', '{\"pageNum\":1,\"jwt\":\"\"}', '访问页面', '动态', '第1页', '172.17.0.5', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 12, '2021-11-27 15:34:19', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (132, 'da376f08-974e-35ba-93ee-260405633908', '/api/archives', 'GET', '{}', '访问页面', '文章归档', '', '10.60.17.56', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 137, '2021-11-27 15:39:35', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (133, 'da376f08-974e-35ba-93ee-260405633908', '/api/moments', 'GET', '{\"pageNum\":1,\"jwt\":\"\"}', '访问页面', '动态', '第1页', '10.60.17.56', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 38, '2021-11-27 15:39:36', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (134, 'da376f08-974e-35ba-93ee-260405633908', '/api/friends', 'GET', '{}', '访问页面', '友链', '', '10.60.17.56', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 45, '2021-11-27 15:39:37', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (135, 'da376f08-974e-35ba-93ee-260405633908', '/api/archives', 'GET', '{}', '访问页面', '文章归档', '', '10.60.17.56', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 11, '2021-11-27 15:39:39', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (136, 'da376f08-974e-35ba-93ee-260405633908', '/blogs', 'GET', '{\"pageNum\":1}', '访问页面', '首页', '第1页', '10.60.17.56', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 47, '2021-11-27 15:45:00', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (137, 'da376f08-974e-35ba-93ee-260405633908', '/archives', 'GET', '{}', '访问页面', '文章归档', '', '10.60.17.56', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 15, '2021-11-27 15:45:06', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (138, 'da376f08-974e-35ba-93ee-260405633908', '/archives', 'GET', '{}', '访问页面', '文章归档', '', '10.60.17.56', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 20, '2021-11-27 15:45:39', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (139, 'da376f08-974e-35ba-93ee-260405633908', '/archives', 'GET', '{}', '访问页面', '文章归档', '', '10.60.17.56', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 17, '2021-11-27 15:56:57', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (140, 'da376f08-974e-35ba-93ee-260405633908', '/archives', 'GET', '{}', '访问页面', '文章归档', '', '10.60.17.56', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 74, '2021-11-27 15:58:09', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (141, 'da376f08-974e-35ba-93ee-260405633908', '/blogs', 'GET', '{\"pageNum\":1}', '访问页面', '首页', '第1页', '10.60.17.56', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 119, '2021-11-27 15:58:11', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (142, 'da376f08-974e-35ba-93ee-260405633908', '/friends', 'GET', '{}', '访问页面', '友链', '', '10.60.17.56', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 29, '2021-11-27 15:58:19', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (143, 'da376f08-974e-35ba-93ee-260405633908', '/about', 'GET', '{}', '访问页面', '关于我', '', '10.60.17.56', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 11, '2021-11-27 15:58:19', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (144, 'da376f08-974e-35ba-93ee-260405633908', '/moments', 'GET', '{\"pageNum\":1,\"jwt\":\"\"}', '访问页面', '动态', '第1页', '10.60.17.56', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 21, '2021-11-27 15:58:20', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (145, 'da376f08-974e-35ba-93ee-260405633908', '/moments', 'GET', '{\"pageNum\":1,\"jwt\":\"\"}', '访问页面', '动态', '第1页', '10.60.17.56', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 17, '2021-11-27 15:58:36', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (146, 'ef7e98be-35be-3731-8c18-10951f274db0', '/blog/blogs', 'GET', '{\"pageNum\":1}', '访问页面', '首页', '第1页', '172.17.0.5', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 1, '2021-12-17 14:21:06', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (147, 'ef7e98be-35be-3731-8c18-10951f274db0', '/blog/archives', 'GET', '{}', '访问页面', '文章归档', '', '172.17.0.5', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 3, '2021-12-17 14:21:14', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (148, 'ef7e98be-35be-3731-8c18-10951f274db0', '/blog/moments', 'GET', '{\"pageNum\":1,\"jwt\":\"\"}', '访问页面', '动态', '第1页', '172.17.0.5', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 2, '2021-12-17 14:21:15', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (149, 'da376f08-974e-35ba-93ee-260405633908', '/blogs', 'GET', '{\"pageNum\":1}', '访问页面', '首页', '第1页', '10.60.17.56', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 88, '2021-12-17 14:21:56', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (150, 'da376f08-974e-35ba-93ee-260405633908', '/blogs', 'GET', '{\"pageNum\":1}', '访问页面', '首页', '第1页', '10.60.17.56', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 54, '2021-12-17 14:28:28', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (151, 'da376f08-974e-35ba-93ee-260405633908', '/blogs', 'GET', '{\"pageNum\":1}', '访问页面', '首页', '第1页', '10.60.17.56', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 68, '2021-12-17 14:30:44', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (152, 'da376f08-974e-35ba-93ee-260405633908', '/blogs', 'GET', '{\"pageNum\":1}', '访问页面', '首页', '第1页', '10.60.17.56', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 44, '2021-12-17 14:33:55', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (153, 'da376f08-974e-35ba-93ee-260405633908', '/archives', 'GET', '{}', '访问页面', '文章归档', '', '10.60.17.56', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 11, '2021-12-17 14:39:22', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (154, 'da376f08-974e-35ba-93ee-260405633908', '/archives', 'GET', '{}', '访问页面', '文章归档', '', '10.60.17.56', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 17, '2021-12-17 14:39:25', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (155, 'da376f08-974e-35ba-93ee-260405633908', '/archives', 'GET', '{}', '访问页面', '文章归档', '', '10.60.17.56', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 10, '2021-12-17 14:40:53', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (156, 'da376f08-974e-35ba-93ee-260405633908', '/archives', 'GET', '{}', '访问页面', '文章归档', '', '10.60.17.56', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 24, '2021-12-17 14:41:40', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (157, 'ef7e98be-35be-3731-8c18-10951f274db0', '/blog/blogs', 'GET', '{\"pageNum\":1}', '访问页面', '首页', '第1页', '172.17.0.5', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 4, '2021-12-17 14:47:14', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (158, 'ef7e98be-35be-3731-8c18-10951f274db0', '/blog/archives', 'GET', '{}', '访问页面', '文章归档', '', '172.17.0.5', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 0, '2021-12-17 14:47:16', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (159, 'ef7e98be-35be-3731-8c18-10951f274db0', '/blog/moments', 'GET', '{\"pageNum\":1,\"jwt\":\"\"}', '访问页面', '动态', '第1页', '172.17.0.5', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 1, '2021-12-17 14:47:19', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (160, 'ef7e98be-35be-3731-8c18-10951f274db0', '/blog/archives', 'GET', '{}', '访问页面', '文章归档', '', '172.17.0.5', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 0, '2021-12-17 14:47:21', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (161, 'ef7e98be-35be-3731-8c18-10951f274db0', '/blog/friends', 'GET', '{}', '访问页面', '友链', '', '172.17.0.5', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 1, '2021-12-17 14:47:22', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (162, 'ef7e98be-35be-3731-8c18-10951f274db0', '/blog/moments', 'GET', '{\"pageNum\":1,\"jwt\":\"\"}', '访问页面', '动态', '第1页', '172.17.0.5', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 1, '2021-12-17 14:47:23', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (163, 'ef7e98be-35be-3731-8c18-10951f274db0', '/blog/moments', 'GET', '{\"pageNum\":1,\"jwt\":\"\"}', '访问页面', '动态', '第1页', '172.17.0.5', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 4, '2021-12-17 14:48:23', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (164, 'ef7e98be-35be-3731-8c18-10951f274db0', '/blog/blogs', 'GET', '{\"pageNum\":1}', '访问页面', '首页', '第1页', '172.17.0.5', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 1, '2021-12-17 14:49:46', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (165, 'ef7e98be-35be-3731-8c18-10951f274db0', '/blog/archives', 'GET', '{}', '访问页面', '文章归档', '', '172.17.0.5', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 2, '2021-12-17 14:49:48', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (166, 'da376f08-974e-35ba-93ee-260405633908', '/blogs', 'GET', '{\"pageNum\":1}', '访问页面', '首页', '第1页', '10.60.17.56', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 191, '2021-12-17 14:50:41', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (167, 'da376f08-974e-35ba-93ee-260405633908', '/archives', 'GET', '{}', '访问页面', '文章归档', '', '10.60.17.56', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 12, '2021-12-17 14:51:56', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (168, 'da376f08-974e-35ba-93ee-260405633908', '/moments', 'GET', '{\"pageNum\":1,\"jwt\":\"\"}', '访问页面', '动态', '第1页', '10.60.17.56', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 38, '2021-12-17 14:52:41', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (169, 'da376f08-974e-35ba-93ee-260405633908', '/friends', 'GET', '{}', '访问页面', '友链', '', '10.60.17.56', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 31, '2021-12-17 14:52:43', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (170, 'da376f08-974e-35ba-93ee-260405633908', '/about', 'GET', '{}', '访问页面', '关于我', '', '10.60.17.56', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 20, '2021-12-17 14:52:55', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (171, 'da376f08-974e-35ba-93ee-260405633908', '/about', 'GET', '{}', '访问页面', '关于我', '', '10.60.17.56', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 18, '2021-12-17 14:58:21', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (172, 'da376f08-974e-35ba-93ee-260405633908', '/friends', 'GET', '{}', '访问页面', '友链', '', '10.60.17.56', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 33, '2021-12-17 14:58:23', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (173, 'da376f08-974e-35ba-93ee-260405633908', '/moments', 'GET', '{\"pageNum\":1,\"jwt\":\"\"}', '访问页面', '动态', '第1页', '10.60.17.56', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 23, '2021-12-17 14:58:23', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (174, 'da376f08-974e-35ba-93ee-260405633908', '/archives', 'GET', '{}', '访问页面', '文章归档', '', '10.60.17.56', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 9, '2021-12-17 14:58:24', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (175, 'da376f08-974e-35ba-93ee-260405633908', '/moments', 'GET', '{\"pageNum\":1,\"jwt\":\"\"}', '访问页面', '动态', '第1页', '10.60.17.56', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 20, '2021-12-17 14:58:27', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (176, 'ef7e98be-35be-3731-8c18-10951f274db0', '/blog/blogs', 'GET', '{\"pageNum\":1}', '访问页面', '首页', '第1页', '172.17.0.5', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 2, '2021-12-17 15:01:25', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (177, 'ef7e98be-35be-3731-8c18-10951f274db0', '/blog/archives', 'GET', '{}', '访问页面', '文章归档', '', '172.17.0.5', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 1, '2021-12-17 15:01:27', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (178, 'ef7e98be-35be-3731-8c18-10951f274db0', '/blog/moments', 'GET', '{\"pageNum\":1,\"jwt\":\"\"}', '访问页面', '动态', '第1页', '172.17.0.5', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 1, '2021-12-17 15:01:30', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (179, 'ef7e98be-35be-3731-8c18-10951f274db0', '/blog/friends', 'GET', '{}', '访问页面', '友链', '', '172.17.0.5', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 1, '2021-12-17 15:01:31', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (180, 'ef7e98be-35be-3731-8c18-10951f274db0', '/blog/moments', 'GET', '{\"pageNum\":1,\"jwt\":\"\"}', '访问页面', '动态', '第1页', '172.17.0.5', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 1, '2021-12-17 15:01:32', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (181, 'ef7e98be-35be-3731-8c18-10951f274db0', '/blog/archives', 'GET', '{}', '访问页面', '文章归档', '', '172.17.0.5', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 1, '2021-12-17 15:01:33', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (182, 'ef7e98be-35be-3731-8c18-10951f274db0', '/blog/moments', 'GET', '{\"pageNum\":1,\"jwt\":\"\"}', '访问页面', '动态', '第1页', '172.17.0.5', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 1, '2021-12-17 15:01:34', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (183, 'ef7e98be-35be-3731-8c18-10951f274db0', '/blog/friends', 'GET', '{}', '访问页面', '友链', '', '172.17.0.5', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 1, '2021-12-17 15:01:35', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (184, 'ef7e98be-35be-3731-8c18-10951f274db0', '/blog/moments', 'GET', '{\"pageNum\":1,\"jwt\":\"\"}', '访问页面', '动态', '第1页', '172.17.0.5', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 1, '2021-12-17 15:01:41', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (185, 'ef7e98be-35be-3731-8c18-10951f274db0', '/blog/friends', 'GET', '{}', '访问页面', '友链', '', '172.17.0.5', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 1, '2021-12-17 15:01:42', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (186, 'ef7e98be-35be-3731-8c18-10951f274db0', '/blog/friends', 'GET', '{}', '访问页面', '友链', '', '172.17.0.5', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 2, '2021-12-17 15:02:40', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (187, 'ef7e98be-35be-3731-8c18-10951f274db0', '/blog/blogs', 'GET', '{\"pageNum\":1}', '访问页面', '首页', '第1页', '172.17.0.5', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 1, '2021-12-17 15:02:44', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (188, 'ef7e98be-35be-3731-8c18-10951f274db0', '/blog/archives', 'GET', '{}', '访问页面', '文章归档', '', '172.17.0.5', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 0, '2021-12-17 15:02:48', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (189, 'ef7e98be-35be-3731-8c18-10951f274db0', '/blog/moments', 'GET', '{\"pageNum\":1,\"jwt\":\"\"}', '访问页面', '动态', '第1页', '172.17.0.5', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 1, '2021-12-17 15:02:50', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (190, 'ef7e98be-35be-3731-8c18-10951f274db0', '/blog/about', 'GET', '{}', '访问页面', '关于我', '', '172.17.0.5', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 0, '2021-12-17 15:02:51', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (191, 'ef7e98be-35be-3731-8c18-10951f274db0', '/blog/moments', 'GET', '{\"pageNum\":1,\"jwt\":\"\"}', '访问页面', '动态', '第1页', '172.17.0.5', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 2, '2021-12-17 15:02:52', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (192, 'ef7e98be-35be-3731-8c18-10951f274db0', '/blog/archives', 'GET', '{}', '访问页面', '文章归档', '', '172.17.0.5', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 0, '2021-12-17 15:02:53', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (193, 'ef7e98be-35be-3731-8c18-10951f274db0', '/blog/blogs', 'GET', '{\"pageNum\":1}', '访问页面', '首页', '第1页', '172.17.0.5', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 1, '2021-12-17 15:02:58', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (194, 'ef7e98be-35be-3731-8c18-10951f274db0', '/blog/archives', 'GET', '{}', '访问页面', '文章归档', '', '172.17.0.5', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 0, '2021-12-17 15:03:02', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (195, 'ef7e98be-35be-3731-8c18-10951f274db0', '/blog/archives', 'GET', '{}', '访问页面', '文章归档', '', '172.17.0.5', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 1, '2021-12-17 15:03:04', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (196, 'ef7e98be-35be-3731-8c18-10951f274db0', '/blog/moments', 'GET', '{\"pageNum\":1,\"jwt\":\"\"}', '访问页面', '动态', '第1页', '172.17.0.5', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 1, '2021-12-17 15:03:10', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (197, 'ef7e98be-35be-3731-8c18-10951f274db0', '/blog/friends', 'GET', '{}', '访问页面', '友链', '', '172.17.0.5', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 1, '2021-12-17 15:03:12', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (198, 'ef7e98be-35be-3731-8c18-10951f274db0', '/blog/moments', 'GET', '{\"pageNum\":1,\"jwt\":\"\"}', '访问页面', '动态', '第1页', '172.17.0.5', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 1, '2021-12-17 15:03:14', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (199, 'ef7e98be-35be-3731-8c18-10951f274db0', '/blog/moments', 'GET', '{\"pageNum\":1,\"jwt\":\"\"}', '访问页面', '动态', '第1页', '172.17.0.5', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 2, '2021-12-17 15:03:16', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (200, 'ef7e98be-35be-3731-8c18-10951f274db0', '/blog/blogs', 'GET', '{\"pageNum\":1}', '访问页面', '首页', '第1页', '172.17.0.5', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 2, '2021-12-17 15:03:18', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (201, '515d80b5-eff7-3075-8b96-f63dbab91ef6', '/blog/about', 'GET', '{}', '访问页面', '关于我', '', '11.146.76.235', '美国|加利福尼亚|阿里巴巴', 'Mac OS X 10', 'Chrome 90.0.4430.212', 1, '2021-12-17 15:37:05', 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/90.0.4430.212 Safari/537.36');
INSERT INTO `visit_log` VALUES (202, 'da376f08-974e-35ba-93ee-260405633908', '/blogs', 'GET', '{\"pageNum\":1}', '访问页面', '首页', '第1页', '10.60.17.56', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 78, '2021-12-17 16:17:55', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (203, 'da376f08-974e-35ba-93ee-260405633908', '/archives', 'GET', '{}', '访问页面', '文章归档', '', '10.60.17.56', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 16, '2021-12-17 16:18:19', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (204, 'da376f08-974e-35ba-93ee-260405633908', '/archives', 'GET', '{}', '访问页面', '文章归档', '', '10.60.17.56', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 10, '2021-12-17 16:18:21', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (205, 'da376f08-974e-35ba-93ee-260405633908', '/moments', 'GET', '{\"pageNum\":1,\"jwt\":\"\"}', '访问页面', '动态', '第1页', '10.60.17.56', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 91, '2021-12-17 16:18:23', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (206, 'da376f08-974e-35ba-93ee-260405633908', '/moments', 'GET', '{\"pageNum\":1,\"jwt\":\"\"}', '访问页面', '动态', '第1页', '10.60.17.56', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 20, '2021-12-17 16:18:25', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (207, 'da376f08-974e-35ba-93ee-260405633908', '/moments', 'GET', '{\"pageNum\":1,\"jwt\":\"\"}', '访问页面', '动态', '第1页', '10.60.17.56', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 20, '2021-12-17 16:25:06', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (208, 'da376f08-974e-35ba-93ee-260405633908', '/blogs', 'GET', '{\"pageNum\":1}', '访问页面', '首页', '第1页', '10.60.17.56', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 58, '2021-12-17 16:25:31', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (209, 'da376f08-974e-35ba-93ee-260405633908', '/blogs', 'GET', '{\"pageNum\":1}', '访问页面', '首页', '第1页', '10.60.17.56', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 70, '2021-12-17 16:26:16', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (210, 'da376f08-974e-35ba-93ee-260405633908', '/blogs', 'GET', '{\"pageNum\":1}', '访问页面', '首页', '第1页', '10.60.17.56', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 60, '2021-12-17 16:26:44', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (211, 'da376f08-974e-35ba-93ee-260405633908', '/blogs', 'GET', '{\"pageNum\":1}', '访问页面', '首页', '第1页', '10.60.17.56', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 58, '2021-12-17 16:27:06', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (212, 'da376f08-974e-35ba-93ee-260405633908', '/blogs', 'GET', '{\"pageNum\":1}', '访问页面', '首页', '第1页', '10.60.17.56', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 58, '2021-12-17 16:30:50', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (213, 'da376f08-974e-35ba-93ee-260405633908', '/blogs', 'GET', '{\"pageNum\":1}', '访问页面', '首页', '第1页', '10.60.17.56', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 56, '2021-12-17 16:31:10', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (214, 'da376f08-974e-35ba-93ee-260405633908', '/archives', 'GET', '{}', '访问页面', '文章归档', '', '10.60.17.56', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 12, '2021-12-17 16:31:17', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (215, 'da376f08-974e-35ba-93ee-260405633908', '/archives', 'GET', '{}', '访问页面', '文章归档', '', '10.60.17.56', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 23, '2021-12-17 16:31:19', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (216, 'da376f08-974e-35ba-93ee-260405633908', '/archives', 'GET', '{}', '访问页面', '文章归档', '', '10.60.17.56', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 14, '2021-12-17 16:31:32', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (217, 'da376f08-974e-35ba-93ee-260405633908', '/archives', 'GET', '{}', '访问页面', '文章归档', '', '10.60.17.56', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 11, '2021-12-17 16:35:31', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (218, '25823f9b-2325-3c93-b774-08c76df71255', '/blog/blogs', 'GET', '{\"pageNum\":1}', '访问页面', '首页', '第1页', '172.17.0.5', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 2, '2021-12-17 16:36:01', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (219, '25823f9b-2325-3c93-b774-08c76df71255', '/blog/archives', 'GET', '{}', '访问页面', '文章归档', '', '172.17.0.5', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 0, '2021-12-17 16:36:05', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (220, 'ef7e98be-35be-3731-8c18-10951f274db0', '/blog/blogs', 'GET', '{\"pageNum\":1}', '访问页面', '首页', '第1页', '172.17.0.5', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 1, '2021-12-17 16:39:04', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (221, 'ef7e98be-35be-3731-8c18-10951f274db0', '/blog/archives', 'GET', '{}', '访问页面', '文章归档', '', '172.17.0.5', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 0, '2021-12-17 16:39:09', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (222, 'ef7e98be-35be-3731-8c18-10951f274db0', '/blog/archives', 'GET', '{}', '访问页面', '文章归档', '', '172.17.0.5', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 2, '2021-12-17 16:39:12', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (223, 'ef7e98be-35be-3731-8c18-10951f274db0', '/blog/archives', 'GET', '{}', '访问页面', '文章归档', '', '172.17.0.5', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 0, '2021-12-17 16:39:14', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (224, 'ef7e98be-35be-3731-8c18-10951f274db0', '/blog/archives', 'GET', '{}', '访问页面', '文章归档', '', '172.17.0.5', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 0, '2021-12-17 16:39:15', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (225, '25823f9b-2325-3c93-b774-08c76df71255', '/blog/blogs', 'GET', '{\"pageNum\":1}', '访问页面', '首页', '第1页', '172.17.0.5', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 2, '2021-12-17 16:39:26', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (226, '25823f9b-2325-3c93-b774-08c76df71255', '/blog/blogs', 'GET', '{\"pageNum\":1}', '访问页面', '首页', '第1页', '172.17.0.5', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 1, '2021-12-17 16:39:41', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (227, '25823f9b-2325-3c93-b774-08c76df71255', '/blog/blogs', 'GET', '{\"pageNum\":1}', '访问页面', '首页', '第1页', '172.17.0.5', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 0, '2021-12-17 16:40:33', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (228, '25823f9b-2325-3c93-b774-08c76df71255', '/blog/blogs', 'GET', '{\"pageNum\":1}', '访问页面', '首页', '第1页', '172.17.0.5', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 4, '2021-12-17 16:44:57', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (229, '25823f9b-2325-3c93-b774-08c76df71255', '/blog/moments', 'GET', '{\"pageNum\":1,\"jwt\":\"\"}', '访问页面', '动态', '第1页', '172.17.0.5', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 1, '2021-12-17 16:45:01', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (230, '25823f9b-2325-3c93-b774-08c76df71255', '/blog/archives', 'GET', '{}', '访问页面', '文章归档', '', '172.17.0.5', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 0, '2021-12-17 16:45:03', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (231, '25823f9b-2325-3c93-b774-08c76df71255', '/blog/friends', 'GET', '{}', '访问页面', '友链', '', '172.17.0.5', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 2, '2021-12-17 16:45:04', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (232, '57fd23a4-96ac-3145-8147-81c4f35ba2a5', '/blog/blogs', 'GET', '{\"pageNum\":1}', '访问页面', '首页', '第1页', '172.17.0.5', '内网IP|内网IP', 'Windows 10', 'Chrome 79.0.3945.79', 1, '2021-12-17 20:11:13', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/79.0.3945.79 Safari/537.36');
INSERT INTO `visit_log` VALUES (233, '57fd23a4-96ac-3145-8147-81c4f35ba2a5', '/blog/blogs', 'GET', '{\"pageNum\":1}', '访问页面', '首页', '第1页', '172.17.0.5', '内网IP|内网IP', 'Windows 10', 'Chrome 79.0.3945.79', 4, '2021-12-17 20:14:11', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/79.0.3945.79 Safari/537.36');
INSERT INTO `visit_log` VALUES (234, 'd5d7efd7-b9d3-35fa-9f04-ee9c855bf18e', '/blog/blogs', 'GET', '{\"pageNum\":1}', '访问页面', '首页', '第1页', '172.17.0.5', '内网IP|内网IP', 'Windows 10', 'Chrome 76.0.3809.71', 1, '2021-12-17 20:36:19', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/76.0.3809.71 Safari/537.36');
INSERT INTO `visit_log` VALUES (235, '9a33b93a-a6f2-3ca8-af75-6e5807a6d94d', '/blog/friends', 'GET', '{}', '访问页面', '友链', '', '11.167.8.100', '美国|北卡罗来纳|阿里巴巴', 'Mac OS X 10', 'Chrome 90.0.4430.212', 2, '2021-12-17 22:36:34', 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/90.0.4430.212 Safari/537.36');
INSERT INTO `visit_log` VALUES (236, '5bd7ea18-d842-3215-a07f-67d4b5d1859f', '/blog/blogs', 'GET', '{\"pageNum\":1}', '访问页面', '首页', '第1页', '172.17.0.5', '内网IP|内网IP', 'Mac OS X 11', 'Chrome 87.0.4280.88', 1, '2021-12-17 22:48:02', 'Mozilla/5.0 (Macintosh; Intel Mac OS X 11_0_0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.88 Safari/537.36');
INSERT INTO `visit_log` VALUES (237, '5bd7ea18-d842-3215-a07f-67d4b5d1859f', '/blog/blogs', 'GET', '{\"pageNum\":1}', '访问页面', '首页', '第1页', '172.17.0.5', '内网IP|内网IP', 'Mac OS X 11', 'Chrome 87.0.4280.88', 1, '2021-12-17 22:48:14', 'Mozilla/5.0 (Macintosh; Intel Mac OS X 11_0_0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.88 Safari/537.36');
INSERT INTO `visit_log` VALUES (238, '5bd7ea18-d842-3215-a07f-67d4b5d1859f', '/blog/blogs', 'GET', '{\"pageNum\":1}', '访问页面', '首页', '第1页', '172.17.0.5', '内网IP|内网IP', 'Mac OS X 11', 'Chrome 87.0.4280.88', 1, '2021-12-17 22:48:32', 'Mozilla/5.0 (Macintosh; Intel Mac OS X 11_0_0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.88 Safari/537.36');
INSERT INTO `visit_log` VALUES (239, '39d030dc-acab-3a0b-b4a6-2ed00fe37e4b', '/blog/archives', 'GET', '{}', '访问页面', '文章归档', '', '11.134.196.225', '美国|阿里巴巴', 'Mac OS X 10', 'Chrome 90.0.4430.212', 0, '2021-12-17 23:45:23', 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/90.0.4430.212 Safari/537.36');
INSERT INTO `visit_log` VALUES (240, 'fd63abbd-13f3-3d1e-a06b-11fa50b4f9f1', '/blog/blogs', 'GET', '{\"pageNum\":1}', '访问页面', '首页', '第1页', '11.167.25.203', '美国|北卡罗来纳|阿里巴巴', 'Mac OS X 10', 'Chrome 90.0.4430.212', 2, '2021-12-18 13:21:36', 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/90.0.4430.212 Safari/537.36');
INSERT INTO `visit_log` VALUES (241, '0db903cc-1694-32f6-8485-f2d47ab52637', '/blog/blogs', 'GET', '{\"pageNum\":1}', '访问页面', '首页', '第1页', '172.17.0.5', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', 1, '2021-12-18 20:07:35', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visit_log` VALUES (242, '48449f66-c51d-31a0-9ca9-5ea49c539290', '/blog/blogs', 'GET', '{\"pageNum\":1}', '访问页面', '首页', '第1页', '172.17.0.5', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.131', 1, '2021-12-19 17:21:24', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.131 Safari/537.36');
INSERT INTO `visit_log` VALUES (243, '48449f66-c51d-31a0-9ca9-5ea49c539290', '/blog/blog', 'GET', '{\"id\":1,\"jwt\":\"\"}', '查看博客', '本站第一篇文章的标题', '文章标题：本站第一篇文章的标题', '172.17.0.5', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.131', 4, '2021-12-19 17:21:29', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.131 Safari/537.36');

-- ----------------------------
-- Table structure for visit_record
-- ----------------------------
DROP TABLE IF EXISTS `visit_record`;
CREATE TABLE `visit_record`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `pv` int NOT NULL COMMENT '访问量',
  `uv` int NOT NULL COMMENT '独立用户',
  `date` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '日期\"02-23\"',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of visit_record
-- ----------------------------
INSERT INTO `visit_record` VALUES (1, 94, 9, '12-17');
INSERT INTO `visit_record` VALUES (2, 2, 2, '12-18');
INSERT INTO `visit_record` VALUES (3, 2, 1, '12-19');
INSERT INTO `visit_record` VALUES (4, 0, 0, '12-20');

-- ----------------------------
-- Table structure for visitor
-- ----------------------------
DROP TABLE IF EXISTS `visitor`;
CREATE TABLE `visitor`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `uuid` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '访客标识码',
  `ip` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'ip',
  `ip_source` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'ip来源',
  `os` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '操作系统',
  `browser` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '浏览器',
  `create_time` datetime NOT NULL COMMENT '首次访问时间',
  `last_time` datetime NOT NULL COMMENT '最后访问时间',
  `pv` int NULL DEFAULT NULL COMMENT '访问页数统计',
  `user_agent` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'user-agent用户代理',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 16 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of visitor
-- ----------------------------
INSERT INTO `visitor` VALUES (1, 'da376f08-974e-35ba-93ee-260405633908', '10.60.17.56', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', '2021-11-23 11:28:14', '2021-12-17 16:35:31', 34, 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visitor` VALUES (2, '265620e0-d7b2-37e3-8aa5-cccbd1882162', '10.60.17.56', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', '2021-11-23 19:32:38', '2021-11-23 19:32:38', 0, 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visitor` VALUES (3, 'bc192880-925d-3ca1-9697-5b494d4318f2', '172.17.0.5', '内网IP|内网IP', 'Unknown ??', 'PostmanRuntime 7.28.4', '2021-11-27 15:18:16', '2021-11-27 15:18:16', 0, 'PostmanRuntime/7.28.4');
INSERT INTO `visitor` VALUES (4, '0f5fe4ae-3f4b-3e37-a288-751a0c8fccad', '172.17.0.5', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', '2021-11-27 15:30:33', '2021-11-27 15:30:33', 0, 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visitor` VALUES (5, 'ef7e98be-35be-3731-8c18-10951f274db0', '172.17.0.5', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', '2021-12-17 14:21:06', '2021-12-17 16:39:15', 42, 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visitor` VALUES (6, '515d80b5-eff7-3075-8b96-f63dbab91ef6', '11.146.76.235', '美国|加利福尼亚|阿里巴巴', 'Mac OS X 10', 'Chrome 90.0.4430.212', '2021-12-17 15:37:05', '2021-12-17 15:37:05', 1, 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/90.0.4430.212 Safari/537.36');
INSERT INTO `visitor` VALUES (7, '25823f9b-2325-3c93-b774-08c76df71255', '172.17.0.5', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', '2021-12-17 16:36:01', '2021-12-17 16:45:04', 9, 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visitor` VALUES (8, '57fd23a4-96ac-3145-8147-81c4f35ba2a5', '172.17.0.5', '内网IP|内网IP', 'Windows 10', 'Chrome 79.0.3945.79', '2021-12-17 20:11:13', '2021-12-17 20:14:11', 2, 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/79.0.3945.79 Safari/537.36');
INSERT INTO `visitor` VALUES (9, 'd5d7efd7-b9d3-35fa-9f04-ee9c855bf18e', '172.17.0.5', '内网IP|内网IP', 'Windows 10', 'Chrome 76.0.3809.71', '2021-12-17 20:36:19', '2021-12-17 20:36:19', 1, 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/76.0.3809.71 Safari/537.36');
INSERT INTO `visitor` VALUES (10, '9a33b93a-a6f2-3ca8-af75-6e5807a6d94d', '11.167.8.100', '美国|北卡罗来纳|阿里巴巴', 'Mac OS X 10', 'Chrome 90.0.4430.212', '2021-12-17 22:36:34', '2021-12-17 22:36:34', 1, 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/90.0.4430.212 Safari/537.36');
INSERT INTO `visitor` VALUES (11, '5bd7ea18-d842-3215-a07f-67d4b5d1859f', '172.17.0.5', '内网IP|内网IP', 'Mac OS X 11', 'Chrome 87.0.4280.88', '2021-12-17 22:48:02', '2021-12-17 22:48:32', 3, 'Mozilla/5.0 (Macintosh; Intel Mac OS X 11_0_0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.88 Safari/537.36');
INSERT INTO `visitor` VALUES (12, '39d030dc-acab-3a0b-b4a6-2ed00fe37e4b', '11.134.196.225', '美国|阿里巴巴', 'Mac OS X 10', 'Chrome 90.0.4430.212', '2021-12-17 23:45:23', '2021-12-17 23:45:23', 1, 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/90.0.4430.212 Safari/537.36');
INSERT INTO `visitor` VALUES (13, 'fd63abbd-13f3-3d1e-a06b-11fa50b4f9f1', '11.167.25.203', '美国|北卡罗来纳|阿里巴巴', 'Mac OS X 10', 'Chrome 90.0.4430.212', '2021-12-18 13:21:36', '2021-12-18 13:21:36', 1, 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/90.0.4430.212 Safari/537.36');
INSERT INTO `visitor` VALUES (14, '0db903cc-1694-32f6-8485-f2d47ab52637', '172.17.0.5', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.159', '2021-12-18 20:07:35', '2021-12-18 20:07:35', 1, 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36');
INSERT INTO `visitor` VALUES (15, '48449f66-c51d-31a0-9ca9-5ea49c539290', '172.17.0.5', '内网IP|内网IP', 'Windows 10', 'Chrome 92.0.4515.131', '2021-12-19 17:21:24', '2021-12-19 17:21:29', 2, 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.131 Safari/537.36');

SET FOREIGN_KEY_CHECKS = 1;
