/*
 Navicat Premium Dump SQL

 Source Server         : task-flow
 Source Server Type    : MySQL
 Source Server Version : 80035 (8.0.35)
 Source Host           : 121.40.119.149:3306
 Source Schema         : task-flow

 Target Server Type    : MySQL
 Target Server Version : 80035 (8.0.35)
 File Encoding         : 65001

 Date: 16/05/2026 22:08:53
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for oauth_config
-- ----------------------------
DROP TABLE IF EXISTS `oauth_config`;
CREATE TABLE `oauth_config` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `platform` varchar(50) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '平台类型',
  `status` int DEFAULT '1' COMMENT '状态',
  `config_key` varchar(200) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '配置key',
  `config_value` varchar(200) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '配置 value',
  `redirect_uri` varchar(200) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '回调地址',
  `is_system` tinyint DEFAULT '0' COMMENT '是否系统内置',
  `create_by` varchar(64) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='Oauth配置表';

-- ----------------------------
-- Records of oauth_config
-- ----------------------------
BEGIN;
INSERT INTO `oauth_config` (`id`, `platform`, `status`, `config_key`, `config_value`, `redirect_uri`, `is_system`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (1, 'github', 1, 'Ov23lix9ML2xzIWV1Hdx', '9daca7da4dac909abdefd078258d53aaa178daa2', 'https://task.junoyi.com', 0, 'super_admin', '2026-05-09 17:23:30', NULL, NULL, 'Github');
COMMIT;

-- ----------------------------
-- Table structure for project
-- ----------------------------
DROP TABLE IF EXISTS `project`;
CREATE TABLE `project` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID（自增主键）',
  `no` varchar(50) DEFAULT NULL COMMENT '项目编号',
  `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '项目名称',
  `description` varchar(500) DEFAULT NULL COMMENT '项目描述',
  `leader` bigint DEFAULT NULL COMMENT '项目负责人ID',
  `type` int DEFAULT NULL COMMENT '项目类型',
  `status` int DEFAULT NULL COMMENT '项目状态',
  `priority` int DEFAULT NULL COMMENT '项目优先级',
  `del_flag` int DEFAULT '0' COMMENT '软删除标记（0未删除，1删除）',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '创建时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='项目主数据表';

-- ----------------------------
-- Records of project
-- ----------------------------
BEGIN;
INSERT INTO `project` (`id`, `no`, `name`, `description`, `leader`, `type`, `status`, `priority`, `del_flag`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (10, 'PJ-20260516-5255', '测试项目1', '这是第一个测试项目', 2, 4, 0, 1, 0, 'super_admin', '2026-05-16 21:11:22', NULL, NULL, '');
INSERT INTO `project` (`id`, `no`, `name`, `description`, `leader`, `type`, `status`, `priority`, `del_flag`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (11, 'PJ-20260516-5256', '测试项目2', '这是第二个测试项目', 2, 1, 1, 2, 0, 'super_admin', '2026-05-16 21:12:14', 'super_admin', '2026-05-16 21:12:39', '');
COMMIT;

-- ----------------------------
-- Table structure for project_member
-- ----------------------------
DROP TABLE IF EXISTS `project_member`;
CREATE TABLE `project_member` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `project_id` bigint NOT NULL COMMENT '项目ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `role` varchar(20) NOT NULL DEFAULT 'member' COMMENT '项目角色(owner/admin/member/viewer)',
  `status` tinyint DEFAULT '1' COMMENT '状态(1:在职 0:离职)',
  `join_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '加入时间',
  `leave_time` datetime DEFAULT NULL COMMENT '离开时间',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_project_user` (`project_id`,`user_id`),
  KEY `idx_project_id` (`project_id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='项目成员表';

-- ----------------------------
-- Records of project_member
-- ----------------------------
BEGIN;
INSERT INTO `project_member` (`id`, `project_id`, `user_id`, `role`, `status`, `join_time`, `leave_time`, `create_time`, `update_time`) VALUES (16, 11, 2, 'owner', 1, '2026-05-16 21:12:14', NULL, '2026-05-16 21:12:14', '2026-05-16 21:12:14');
COMMIT;

-- ----------------------------
-- Table structure for sys_auth_log
-- ----------------------------
DROP TABLE IF EXISTS `sys_auth_log`;
CREATE TABLE `sys_auth_log` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '日志ID',
  `user_id` bigint DEFAULT NULL COMMENT '用户ID',
  `user_name` varchar(64) DEFAULT NULL COMMENT '用户名',
  `nick_name` varchar(64) DEFAULT NULL COMMENT '用户昵称',
  `login_ip` varchar(128) DEFAULT NULL COMMENT '登录IP',
  `ip_region` varchar(255) DEFAULT NULL COMMENT 'IP归属地',
  `session_id` varchar(128) DEFAULT NULL COMMENT '会话ID',
  `identity` varchar(64) DEFAULT NULL COMMENT '身份（角色名称）',
  `login_type` varchar(32) DEFAULT NULL COMMENT '登录方式（password-账号密码, wechat_work-企业微信）',
  `browser` varchar(64) DEFAULT NULL COMMENT '浏览器',
  `os` varchar(64) DEFAULT NULL COMMENT '操作系统',
  `device_type` varchar(32) DEFAULT NULL COMMENT '设备类型（Desktop/Mobile/Tablet）',
  `status` tinyint DEFAULT '0' COMMENT '登录状态（0-失败，1-成功）',
  `msg` varchar(500) DEFAULT NULL COMMENT '提示消息（失败原因等）',
  `login_time` datetime DEFAULT NULL COMMENT '登录时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_user_name` (`user_name`),
  KEY `idx_login_time` (`login_time`),
  KEY `idx_status` (`status`),
  KEY `idx_login_ip` (`login_ip`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='系统登录日志表';

-- ----------------------------
-- Records of sys_auth_log
-- ----------------------------
BEGIN;
INSERT INTO `sys_auth_log` (`id`, `user_id`, `user_name`, `nick_name`, `login_ip`, `ip_region`, `session_id`, `identity`, `login_type`, `browser`, `os`, `device_type`, `status`, `msg`, `login_time`) VALUES (1, 1, 'super_admin', '超级管理员', '127.0.0.1', '内网IP', '44d71fdf133248038121a4477ec03f76', '超级管理员', 'password', 'Chrome 145', 'macOS 10.15.7', 'Desktop', 1, '登录成功', '2026-03-03 13:00:11');
INSERT INTO `sys_auth_log` (`id`, `user_id`, `user_name`, `nick_name`, `login_ip`, `ip_region`, `session_id`, `identity`, `login_type`, `browser`, `os`, `device_type`, `status`, `msg`, `login_time`) VALUES (2, 1, 'super_admin', '超级管理员', '127.0.0.1', '内网IP', 'ac5127690dd34983a0fd2b7a9b8677c7', '超级管理员', 'password', 'Chrome 145', 'macOS 10.15.7', 'Desktop', 1, '登录成功', '2026-03-03 13:08:03');
INSERT INTO `sys_auth_log` (`id`, `user_id`, `user_name`, `nick_name`, `login_ip`, `ip_region`, `session_id`, `identity`, `login_type`, `browser`, `os`, `device_type`, `status`, `msg`, `login_time`) VALUES (3, 1, 'super_admin', '超级管理员', '127.0.0.1', '内网IP', '1db5a53e80c84a91a50ea0c7c4841449', '超级管理员', 'password', 'Chrome 145', 'macOS 10.15.7', 'Desktop', 1, '登录成功', '2026-03-03 13:32:59');
INSERT INTO `sys_auth_log` (`id`, `user_id`, `user_name`, `nick_name`, `login_ip`, `ip_region`, `session_id`, `identity`, `login_type`, `browser`, `os`, `device_type`, `status`, `msg`, `login_time`) VALUES (4, 1, 'super_admin', '超级管理员', '127.0.0.1', '内网IP', 'c9cd6c7134bb46a695ff8830ffd57724', '超级管理员', 'password', 'Chrome 147', 'Windows 10/11', 'Desktop', 1, '登录成功', '2026-05-07 16:01:55');
INSERT INTO `sys_auth_log` (`id`, `user_id`, `user_name`, `nick_name`, `login_ip`, `ip_region`, `session_id`, `identity`, `login_type`, `browser`, `os`, `device_type`, `status`, `msg`, `login_time`) VALUES (5, 1, 'super_admin', '超级管理员', '127.0.0.1', '内网IP', 'f985cc33124b41a88a4302c35631e258', '超级管理员', 'password', 'Chrome 146', 'macOS 10.15.7', 'Desktop', 1, '登录成功', '2026-05-07 16:33:50');
INSERT INTO `sys_auth_log` (`id`, `user_id`, `user_name`, `nick_name`, `login_ip`, `ip_region`, `session_id`, `identity`, `login_type`, `browser`, `os`, `device_type`, `status`, `msg`, `login_time`) VALUES (6, 1, 'super_admin', '超级管理员', '127.0.0.1', '内网IP', 'f077c5d0330d49bb940cc628401b0b9c', '超级管理员', 'password', 'Chrome 146', 'macOS 10.15.7', 'Desktop', 1, '登录成功', '2026-05-07 20:08:48');
INSERT INTO `sys_auth_log` (`id`, `user_id`, `user_name`, `nick_name`, `login_ip`, `ip_region`, `session_id`, `identity`, `login_type`, `browser`, `os`, `device_type`, `status`, `msg`, `login_time`) VALUES (7, 1, 'super_admin', '超级管理员', '127.0.0.1', '内网IP', '16211cb5766a41068ed356ce93dd117a', '超级管理员', 'password', 'Chrome 147', 'Windows 10/11', 'Desktop', 1, '登录成功', '2026-05-07 21:45:29');
INSERT INTO `sys_auth_log` (`id`, `user_id`, `user_name`, `nick_name`, `login_ip`, `ip_region`, `session_id`, `identity`, `login_type`, `browser`, `os`, `device_type`, `status`, `msg`, `login_time`) VALUES (8, 1, 'super_admin', '超级管理员', '127.0.0.1', '内网IP', 'c628943c5a4a4ac5a5e24ff3ae59dd78', '超级管理员', 'password', 'Chrome 147', 'Windows 10/11', 'Desktop', 1, '登录成功', '2026-05-07 22:35:20');
INSERT INTO `sys_auth_log` (`id`, `user_id`, `user_name`, `nick_name`, `login_ip`, `ip_region`, `session_id`, `identity`, `login_type`, `browser`, `os`, `device_type`, `status`, `msg`, `login_time`) VALUES (9, 1, 'super_admin', '超级管理员', '127.0.0.1', '内网IP', '985643f592a548f1949aa49ea3a74ee2', '超级管理员', 'password', 'Chrome 146', 'macOS 10.15.7', 'Desktop', 1, '登录成功', '2026-05-08 18:43:37');
INSERT INTO `sys_auth_log` (`id`, `user_id`, `user_name`, `nick_name`, `login_ip`, `ip_region`, `session_id`, `identity`, `login_type`, `browser`, `os`, `device_type`, `status`, `msg`, `login_time`) VALUES (10, 1, 'super_admin', '超级管理员', '127.0.0.1', '内网IP', 'a229f4c2641e4cc9b582f2d7610383b6', '超级管理员', 'password', 'Chrome 146', 'macOS 10.15.7', 'Desktop', 1, '登录成功', '2026-05-08 18:46:40');
INSERT INTO `sys_auth_log` (`id`, `user_id`, `user_name`, `nick_name`, `login_ip`, `ip_region`, `session_id`, `identity`, `login_type`, `browser`, `os`, `device_type`, `status`, `msg`, `login_time`) VALUES (11, 1, 'super_admin', '超级管理员', '127.0.0.1', '内网IP', '3a5f805532934d5b9bd9d014b56b07b3', '超级管理员', 'password', 'Chrome 146', 'macOS 10.15.7', 'Desktop', 1, '登录成功', '2026-05-09 18:19:01');
INSERT INTO `sys_auth_log` (`id`, `user_id`, `user_name`, `nick_name`, `login_ip`, `ip_region`, `session_id`, `identity`, `login_type`, `browser`, `os`, `device_type`, `status`, `msg`, `login_time`) VALUES (12, 1, 'super_admin', '超级管理员', '127.0.0.1', '内网IP', 'a0fdbf09af9f4806a1c64567983053ac', '超级管理员', 'password', 'Chrome 146', 'macOS 10.15.7', 'Desktop', 1, '登录成功', '2026-05-10 15:35:10');
INSERT INTO `sys_auth_log` (`id`, `user_id`, `user_name`, `nick_name`, `login_ip`, `ip_region`, `session_id`, `identity`, `login_type`, `browser`, `os`, `device_type`, `status`, `msg`, `login_time`) VALUES (13, 1, 'super_admin', '超级管理员', '127.0.0.1', '内网IP', 'd0c9c3a4a3ca4f6c99d42fbace7ecac1', '超级管理员', 'password', 'Chrome 146', 'macOS 10.15.7', 'Desktop', 1, '登录成功', '2026-05-10 16:19:50');
INSERT INTO `sys_auth_log` (`id`, `user_id`, `user_name`, `nick_name`, `login_ip`, `ip_region`, `session_id`, `identity`, `login_type`, `browser`, `os`, `device_type`, `status`, `msg`, `login_time`) VALUES (14, 1, 'super_admin', '超级管理员', '127.0.0.1', '内网IP', '069f6ec84cf14b289419338ed26a2c6a', '超级管理员', 'password', 'Chrome 146', 'macOS 10.15.7', 'Desktop', 1, '登录成功', '2026-05-10 16:33:27');
INSERT INTO `sys_auth_log` (`id`, `user_id`, `user_name`, `nick_name`, `login_ip`, `ip_region`, `session_id`, `identity`, `login_type`, `browser`, `os`, `device_type`, `status`, `msg`, `login_time`) VALUES (15, 1, 'super_admin', '超级管理员', '127.0.0.1', '内网IP', '4b36f6569b0a4fc6bd1ddbf65b842e3c', '超级管理员', 'password', 'Chrome 146', 'macOS 10.15.7', 'Desktop', 1, '登录成功', '2026-05-16 21:21:53');
COMMIT;

-- ----------------------------
-- Table structure for sys_config
-- ----------------------------
DROP TABLE IF EXISTS `sys_config`;
CREATE TABLE `sys_config` (
  `config_id` bigint NOT NULL AUTO_INCREMENT COMMENT '设置ID',
  `config_key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '设置键名',
  `config_value` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '设置键值',
  `config_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '设置名称',
  `config_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT 'text' COMMENT '设置类型（text/number/boolean/json）',
  `sort` int DEFAULT '0' COMMENT '排序',
  `is_system` tinyint NOT NULL DEFAULT '0' COMMENT '是否系统内置（0否 1是）',
  `status` tinyint DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`config_id`) USING BTREE,
  UNIQUE KEY `uk_setting_key` (`config_key`)
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统设置表';

-- ----------------------------
-- Records of sys_config
-- ----------------------------
BEGIN;
INSERT INTO `sys_config` (`config_id`, `config_key`, `config_value`, `config_name`, `config_type`, `sort`, `is_system`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (1, 'sys.system.name', '研发任务管理系统', '系统名称', 'text', 1, 1, 0, 'system', '2026-01-28 22:04:26', 'super_admin', '2026-05-07 08:38:19', '系统显示名称');
INSERT INTO `sys_config` (`config_id`, `config_key`, `config_value`, `config_name`, `config_type`, `sort`, `is_system`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (2, 'sys.system.version', '1.0.0', '系统版本', 'text', 2, 1, 0, 'system', '2026-01-28 22:04:27', '', NULL, '系统版本号');
INSERT INTO `sys_config` (`config_id`, `config_key`, `config_value`, `config_name`, `config_type`, `sort`, `is_system`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (3, 'sys.system.logo', '/system/info/logo', '系统Logo', 'text', 3, 1, 0, 'system', '2026-01-28 22:04:28', 'super_admin', '2026-01-28 22:06:38', '系统Logo图片地址');
INSERT INTO `sys_config` (`config_id`, `config_key`, `config_value`, `config_name`, `config_type`, `sort`, `is_system`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (4, 'sys.system.copyright', '安庆钧逸网络科技有限公司', '版权所有者', 'text', 4, 1, 0, 'system', '2026-01-28 22:04:29', '', NULL, '版权所有者名称');
INSERT INTO `sys_config` (`config_id`, `config_key`, `config_value`, `config_name`, `config_type`, `sort`, `is_system`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (5, 'sys.system.copyrightYear', '2025', '版权年份', 'text', 5, 1, 0, 'system', '2026-01-28 22:04:33', '', NULL, '版权年份');
INSERT INTO `sys_config` (`config_id`, `config_key`, `config_value`, `config_name`, `config_type`, `sort`, `is_system`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (6, 'sys.system.registration', '皖ICP备@xxxxxxxxx', '备案号', 'text', 6, 1, 0, 'system', '2026-01-28 22:04:34', 'super_admin', '2026-01-28 22:10:20', '网站备案号');
INSERT INTO `sys_config` (`config_id`, `config_key`, `config_value`, `config_name`, `config_type`, `sort`, `is_system`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (7, 'sys.watermark.enabled', 'false', '全局水印', 'boolean', 7, 1, 0, 'system', '2026-01-28 22:04:34', 'super_admin', '2026-02-06 20:57:06', '是否启用全局水印');
INSERT INTO `sys_config` (`config_id`, `config_key`, `config_value`, `config_name`, `config_type`, `sort`, `is_system`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (8, 'sys.watermark.text', '{nickname}({username}) - JunoYi - {date}', '自定义水印文本', 'text', 8, 1, 0, 'system', '2026-02-06 19:31:44', 'super_admin', '2026-02-06 20:56:52', '可使用占位符：{nickname} 用户昵称、{username} 用户账号、、{date} 当前日期、{time} 时间、{year} 年、{month} 月、{day} 日');
INSERT INTO `sys_config` (`config_id`, `config_key`, `config_value`, `config_name`, `config_type`, `sort`, `is_system`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (9, 'sys.menu.layout.editable', 'false', '允许修改菜单布局', 'boolean', 9, 1, 0, 'system', '2026-02-06 21:00:12', 'super_admin', '2026-05-07 08:38:28', '是否允许用户修改菜单布局');
INSERT INTO `sys_config` (`config_id`, `config_key`, `config_value`, `config_name`, `config_type`, `sort`, `is_system`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (10, 'sys.menu.layout.default', 'double', '默认菜单布局', 'text', 10, 1, 0, 'system', '2026-02-06 21:03:02', 'super_admin', '2026-05-07 08:38:35', '系统默认菜单布局类型： left(垂直)、top(水平)、mix(混合)、double(双列)');
COMMIT;

-- ----------------------------
-- Table structure for sys_dept
-- ----------------------------
DROP TABLE IF EXISTS `sys_dept`;
CREATE TABLE `sys_dept` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '部门主键ID',
  `parent_id` bigint DEFAULT NULL COMMENT '父部门ID',
  `name` varchar(50) DEFAULT NULL COMMENT '部门名称',
  `sort` int DEFAULT NULL COMMENT '排序',
  `leader` varchar(20) DEFAULT NULL COMMENT '负责人',
  `phonenumber` varchar(20) DEFAULT NULL COMMENT '联系电话',
  `email` varchar(50) DEFAULT NULL COMMENT '邮箱',
  `status` int DEFAULT '1' COMMENT '状态（0禁用，1正常）',
  `del_flag` tinyint DEFAULT '0' COMMENT '删除标识（软删除）',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='系统部门权限表';

-- ----------------------------
-- Records of sys_dept
-- ----------------------------
BEGIN;
INSERT INTO `sys_dept` (`id`, `parent_id`, `name`, `sort`, `leader`, `phonenumber`, `email`, `status`, `del_flag`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (1, 0, '钧逸网络科技有限公司', 1, '小饭', '18877667671', 'admin@junoyi.com', 1, 0, 'super_admin', '2026-01-02 04:00:44', 'super_admin', '2026-02-06 18:01:16', '主公司');
INSERT INTO `sys_dept` (`id`, `parent_id`, `name`, `sort`, `leader`, `phonenumber`, `email`, `status`, `del_flag`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (2, 1, '杭州总公司', 1, '小饭', '18877667671', 'admin@junoyi.com', 1, 0, 'super_admin', '2026-01-02 04:03:41', 'super_admin', '2026-01-04 14:50:55', '总公司');
INSERT INTO `sys_dept` (`id`, `parent_id`, `name`, `sort`, `leader`, `phonenumber`, `email`, `status`, `del_flag`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (3, 1, '上海分公司', 2, '小王', '17788667643', 'user1@junoyi.com', 1, 0, 'super_admin', '2026-01-02 04:05:12', 'super_admin', '2026-01-04 14:50:55', '分公司');
INSERT INTO `sys_dept` (`id`, `parent_id`, `name`, `sort`, `leader`, `phonenumber`, `email`, `status`, `del_flag`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (4, 2, '研发部门', 2, '小苏', '19988333223', 'user2@junoyi.com', 1, 0, 'super_admin', '2026-01-02 04:06:30', 'super_admin', '2026-01-04 14:50:55', '研发部门');
INSERT INTO `sys_dept` (`id`, `parent_id`, `name`, `sort`, `leader`, `phonenumber`, `email`, `status`, `del_flag`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (5, 2, '市场部门', 3, '小刘', '19984346473', 'user3@junoyi.com', 1, 0, 'super_admin', '2026-01-02 04:07:31', 'super_admin', '2026-02-11 19:37:28', '市场部门');
INSERT INTO `sys_dept` (`id`, `parent_id`, `name`, `sort`, `leader`, `phonenumber`, `email`, `status`, `del_flag`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (6, 2, '财务部门', 1, '小程', '13888448323', 'user4@junoyi.com', 1, 0, 'super_admin', '2026-01-02 04:08:21', 'super_admin', '2026-01-04 14:50:55', '财务部门');
INSERT INTO `sys_dept` (`id`, `parent_id`, `name`, `sort`, `leader`, `phonenumber`, `email`, `status`, `del_flag`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (7, 0, '测试部门1', 0, '测试1222', '18888888888', 'test@junoyi.com', 0, 1, 'super_admin', '2026-01-02 14:35:35', 'super_admin', '2026-01-02 14:45:10', '');
INSERT INTO `sys_dept` (`id`, `parent_id`, `name`, `sort`, `leader`, `phonenumber`, `email`, `status`, `del_flag`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (8, 7, '测试部门2', 0, '测试2', '18888888888', 'test2@junoyi.com', 0, 1, 'super_admin', '2026-01-02 14:44:55', 'super_admin', '2026-01-02 14:45:07', '');
COMMIT;

-- ----------------------------
-- Table structure for sys_dept_group
-- ----------------------------
DROP TABLE IF EXISTS `sys_dept_group`;
CREATE TABLE `sys_dept_group` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `dept_id` bigint DEFAULT NULL COMMENT '部门ID',
  `group_id` bigint DEFAULT NULL COMMENT '权限组ID',
  `expire_time` datetime DEFAULT NULL COMMENT '过期时间（临时权限）',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='部门-权限组关联表';

-- ----------------------------
-- Records of sys_dept_group
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for sys_dict_data
-- ----------------------------
DROP TABLE IF EXISTS `sys_dict_data`;
CREATE TABLE `sys_dict_data` (
  `dict_code` bigint NOT NULL AUTO_INCREMENT COMMENT '字典编码',
  `dict_sort` int DEFAULT '0' COMMENT '字典排序',
  `dict_label` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT '' COMMENT '字典标签',
  `dict_value` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT '' COMMENT '字典键值',
  `dict_type` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT '' COMMENT '字典类型',
  `css_class` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '样式属性（其他样式扩展）',
  `list_class` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '表格回显样式',
  `is_default` char(1) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT 'N' COMMENT '是否默认（Y是 N否）',
  `status` char(1) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `create_by` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`dict_code`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=195 DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC COMMENT='字典数据表';

-- ----------------------------
-- Records of sys_dict_data
-- ----------------------------
BEGIN;
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (1, 1, '未设定', '0', 'sys_user_sex', '', 'warning', 'Y', '0', 'system', '2026-02-11 16:37:39', 'super_admin', '2026-02-11 19:17:55', '未设定性别');
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (2, 2, '男', '1', 'sys_user_sex', '', 'default', 'N', '0', 'system', '2026-02-11 16:37:39', 'super_admin', '2026-02-11 19:17:49', '男性');
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (3, 3, '女', '2', 'sys_user_sex', '', 'default', 'N', '0', 'system', '2026-02-11 16:37:39', 'super_admin', '2026-02-11 19:17:43', '女性');
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (4, 1, '停用', '0', 'sys_user_status', '', 'danger', 'N', '0', 'system', '2026-02-11 16:37:41', 'super_admin', '2026-02-11 19:16:20', '用户已停用');
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (5, 2, '正常', '1', 'sys_user_status', '', 'success', 'Y', '0', 'system', '2026-02-11 16:37:41', 'super_admin', '2026-02-11 19:16:12', '用户正常状态');
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (6, 1, '停用', '0', 'sys_dept_status', '', 'danger', 'N', '0', 'system', '2026-02-11 16:37:44', 'super_admin', '2026-02-11 19:37:14', '部门已停用');
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (7, 2, '正常', '1', 'sys_dept_status', '', 'success', 'Y', '0', 'system', '2026-02-11 16:37:44', '', NULL, '部门正常状态');
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (8, 1, '停用', '0', 'sys_role_status', '', 'danger', 'N', '0', 'system', '2026-02-11 16:37:46', '', NULL, '角色已停用');
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (9, 2, '正常', '1', 'sys_role_status', '', 'success', 'Y', '0', 'system', '2026-02-11 16:37:46', '', NULL, '角色正常状态');
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (15, 1, '停用', '0', 'sys_common_status', '', 'danger', 'N', '0', 'system', '2026-02-11 16:37:53', '', NULL, '停用状态');
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (16, 2, '正常', '1', 'sys_common_status', '', 'success', 'Y', '0', 'system', '2026-02-11 16:37:53', '', NULL, '正常状态');
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (17, 1, '否', 'N', 'sys_yes_no', '', 'default', 'Y', '0', 'system', '2026-02-11 16:37:56', '', NULL, '否');
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (18, 2, '是', 'Y', 'sys_yes_no', '', 'primary', 'N', '0', 'system', '2026-02-11 16:37:56', '', NULL, '是');
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (21, 1, '全部数据权限', '1', 'sys_data_scope', '', 'primary', 'N', '0', 'system', '2026-02-11 16:38:01', '', NULL, '可以查看所有数据');
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (22, 2, '自定义数据权限', '2', 'sys_data_scope', '', 'success', 'N', '0', 'system', '2026-02-11 16:38:01', '', NULL, '自定义部门数据权限');
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (23, 3, '本部门数据权限', '3', 'sys_data_scope', '', 'warning', 'N', '0', 'system', '2026-02-11 16:38:01', '', NULL, '只能查看本部门数据');
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (24, 4, '本部门及以下数据权限', '4', 'sys_data_scope', '', 'info', 'N', '0', 'system', '2026-02-11 16:38:01', '', NULL, '可以查看本部门及下级部门数据');
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (25, 5, '仅本人数据权限', '5', 'sys_data_scope', '', 'danger', 'Y', '0', 'system', '2026-02-11 16:38:01', '', NULL, '只能查看自己的数据');
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (26, 1, '账号密码', 'password', 'sys_login_type', '', 'primary', 'Y', '0', 'system', '2026-02-11 16:38:32', '', NULL, '账号密码登录');
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (27, 2, '企业微信', 'wechat_work', 'sys_login_type', '', 'success', 'N', '0', 'system', '2026-02-11 16:38:32', '', NULL, '企业微信登录');
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (30, 1, '桌面端', 'Desktop', 'sys_device_type', '', 'primary', 'Y', '0', 'system', '2026-02-11 16:38:34', '', NULL, '桌面设备');
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (31, 2, '移动端', 'Mobile', 'sys_device_type', '', 'success', 'N', '0', 'system', '2026-02-11 16:38:34', '', NULL, '移动设备');
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (33, 1, '信息', 'info', 'sys_log_level', '', 'primary', 'Y', '0', 'system', '2026-02-11 16:38:36', '', NULL, '普通信息日志');
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (34, 2, '警告', 'warn', 'sys_log_level', '', 'warning', 'N', '0', 'system', '2026-02-11 16:38:36', '', NULL, '警告日志');
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (35, 3, '错误', 'error', 'sys_log_level', '', 'danger', 'N', '0', 'system', '2026-02-11 16:38:36', '', NULL, '错误日志');
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (36, 1, '查看', 'view', 'sys_oper_type', '', 'info', 'N', '0', 'system', '2026-02-11 16:38:38', '', NULL, '查看操作');
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (37, 2, '新增', 'create', 'sys_oper_type', '', 'success', 'N', '0', 'system', '2026-02-11 16:38:38', '', NULL, '新增操作');
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (38, 3, '修改', 'update', 'sys_oper_type', '', 'primary', 'N', '0', 'system', '2026-02-11 16:38:38', 'super_admin', '2026-02-11 17:26:50', '修改操作');
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (39, 4, '删除', 'delete', 'sys_oper_type', '', 'danger', 'N', '0', 'system', '2026-02-11 16:38:38', '', NULL, '删除操作');
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (40, 5, '导出', 'export', 'sys_oper_type', '', 'warning', 'N', '0', 'system', '2026-02-11 16:38:38', '', NULL, '导出操作');
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (41, 6, '导入', 'import', 'sys_oper_type', '', 'warning', 'N', '0', 'system', '2026-02-11 16:38:38', '', NULL, '导入操作');
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (42, 1, '用户管理', 'user', 'sys_oper_module', '', 'primary', 'N', '0', 'system', '2026-02-11 17:18:05', '', NULL, '用户管理模块');
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (43, 2, '角色管理', 'role', 'sys_oper_module', '', 'success', 'N', '0', 'system', '2026-02-11 17:18:05', '', NULL, '角色管理模块');
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (44, 3, '部门管理', 'dept', 'sys_oper_module', '', 'info', 'N', '0', 'system', '2026-02-11 17:18:05', '', NULL, '部门管理模块');
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (45, 4, '菜单管理', 'menu', 'sys_oper_module', '', 'warning', 'N', '0', 'system', '2026-02-11 17:18:05', '', NULL, '菜单管理模块');
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (46, 5, '权限管理', 'permission', 'sys_oper_module', '', 'danger', 'N', '0', 'system', '2026-02-11 17:18:05', '', NULL, '权限管理模块');
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (47, 6, '权限组管理', 'perm_group', 'sys_oper_module', '', 'primary', 'N', '0', 'system', '2026-02-11 17:18:05', '', NULL, '权限组管理模块');
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (48, 7, '字典类型', 'dict_type', 'sys_oper_module', '', 'success', 'N', '0', 'system', '2026-02-11 17:18:05', '', NULL, '字典类型管理模块');
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (49, 8, '字典数据', 'dict_data', 'sys_oper_module', '', 'info', 'N', '0', 'system', '2026-02-11 17:18:05', '', NULL, '字典数据管理模块');
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (50, 9, '参数配置', 'config', 'sys_oper_module', '', 'warning', 'N', '0', 'system', '2026-02-11 17:18:05', '', NULL, '参数配置管理模块');
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (51, 10, '文件管理', 'file', 'sys_oper_module', '', 'primary', 'N', '0', 'system', '2026-02-11 17:18:05', '', NULL, '文件管理模块');
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (52, 11, '操作日志', 'oper_log', 'sys_oper_module', '', 'info', 'N', '0', 'system', '2026-02-11 17:18:05', '', NULL, '操作日志模块');
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (53, 12, '登录日志', 'login_log', 'sys_oper_module', '', 'success', 'N', '0', 'system', '2026-02-11 17:18:05', '', NULL, '登录日志模块');
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (54, 13, '会话管理', 'session', 'sys_oper_module', '', 'warning', 'N', '0', 'system', '2026-02-11 17:18:05', '', NULL, '会话管理模块');
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (55, 14, '缓存管理', 'cache', 'sys_oper_module', '', 'danger', 'N', '0', 'system', '2026-02-11 17:18:05', '', NULL, '缓存管理模块');
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (56, 15, '系统信息', 'system', 'sys_oper_module', '', 'primary', 'N', '0', 'system', '2026-02-11 17:18:05', '', NULL, '系统信息模块');
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (57, 20, '用户组管理', 'user_group', 'sys_oper_module', '', 'success', 'N', '0', 'system', '2026-02-11 17:18:05', '', NULL, '用户组管理模块');
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (58, 21, '角色组管理', 'role_group', 'sys_oper_module', '', 'info', 'N', '0', 'system', '2026-02-11 17:18:05', '', NULL, '角色组管理模块');
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (59, 22, '部门组管理', 'dept_group', 'sys_oper_module', '', 'warning', 'N', '0', 'system', '2026-02-11 17:18:05', '', NULL, '部门组管理模块');
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (60, 30, '通知公告', 'notice', 'sys_oper_module', '', 'primary', 'N', '0', 'system', '2026-02-11 17:18:05', '', NULL, '通知公告模块');
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (61, 31, '任务调度', 'task', 'sys_oper_module', '', 'success', 'N', '0', 'system', '2026-02-11 17:18:05', '', NULL, '任务调度模块');
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (62, 32, '定时任务', 'job', 'sys_oper_module', '', 'info', 'N', '0', 'system', '2026-02-11 17:18:05', '', NULL, '定时任务模块');
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (63, 33, '数据监控', 'monitor', 'sys_oper_module', '', 'warning', 'N', '0', 'system', '2026-02-11 17:18:05', '', NULL, '数据监控模块');
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (64, 34, '服务监控', 'server', 'sys_oper_module', '', 'danger', 'N', '0', 'system', '2026-02-11 17:18:05', '', NULL, '服务监控模块');
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (65, 40, '业务模块', 'business', 'sys_oper_module', '', 'primary', 'N', '0', 'system', '2026-02-11 17:18:05', '', NULL, '业务模块');
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (66, 41, '订单管理', 'order', 'sys_oper_module', '', 'success', 'N', '0', 'system', '2026-02-11 17:18:05', '', NULL, '订单管理模块');
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (67, 42, '商品管理', 'product', 'sys_oper_module', '', 'info', 'N', '0', 'system', '2026-02-11 17:18:05', '', NULL, '商品管理模块');
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (68, 43, '客户管理', 'customer', 'sys_oper_module', '', 'warning', 'N', '0', 'system', '2026-02-11 17:18:05', '', NULL, '客户管理模块');
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (164, 6, 'Github', 'github', 'oauth_platform', '', '', 'N', '0', 'super_admin', '2026-05-07 20:45:15', 'super_admin', '2026-05-07 20:48:27', '');
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (165, 7, 'Gitee', 'gitee', 'oauth_platform', '', '', 'N', '0', 'super_admin', '2026-05-07 20:45:23', 'super_admin', '2026-05-07 20:48:37', '');
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (166, 0, '企业微信', 'wework', 'oauth_platform', '', '', 'N', '0', 'super_admin', '2026-05-07 20:45:41', '', NULL, '');
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (167, 1, '飞书', 'feishu', 'oauth_platform', '', '', 'N', '0', 'super_admin', '2026-05-07 20:46:33', '', NULL, '');
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (168, 3, '钉钉', 'dingtalk', 'oauth_platform', '', '', 'N', '0', 'super_admin', '2026-05-07 20:46:46', '', NULL, '');
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (169, 4, '微信', 'wechat', 'oauth_platform', '', '', 'N', '0', 'super_admin', '2026-05-07 20:47:49', 'super_admin', '2026-05-07 20:47:57', '');
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (170, 5, '支付宝', 'alipay', 'oauth_platform', '', '', 'N', '0', 'super_admin', '2026-05-07 20:48:21', '', NULL, '');
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (171, 0, '停用', '0', 'oauth_status', '', '', 'N', '0', 'super_admin', '2026-05-08 16:06:50', '', NULL, '');
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (172, 1, '启用', '1', 'oauth_status', '', '', 'N', '0', 'super_admin', '2026-05-08 16:06:59', '', NULL, '');
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (173, 0, '待启动', '0', 'project_status', '', 'warning', 'N', '0', 'super_admin', '2026-05-16 21:03:16', 'super_admin', '2026-05-16 21:12:57', '');
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (174, 1, '进行中', '1', 'project_status', '', 'primary', 'N', '0', 'super_admin', '2026-05-16 21:03:24', 'super_admin', '2026-05-16 21:13:22', '');
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (175, 2, '暂停', '2', 'project_status', '', 'warning', 'N', '0', 'super_admin', '2026-05-16 21:03:32', 'super_admin', '2026-05-16 21:13:26', '');
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (176, 3, '待验收', '3', 'project_status', '', 'warning', 'N', '0', 'super_admin', '2026-05-16 21:03:42', 'super_admin', '2026-05-16 21:13:31', '');
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (177, 4, '已完成', '4', 'project_status', '', 'success', 'N', '0', 'super_admin', '2026-05-16 21:04:07', 'super_admin', '2026-05-16 21:13:36', '');
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (178, 5, '已终止', '5', 'project_status', '', 'danger', 'N', '0', 'super_admin', '2026-05-16 21:04:19', 'super_admin', '2026-05-16 21:13:44', '');
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (179, 6, '长期维护', '6', 'project_status', '', 'info', 'N', '0', 'super_admin', '2026-05-16 21:04:29', 'super_admin', '2026-05-16 21:13:49', '');
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (180, 1, '低', '1', 'project_priority', '', '', 'N', '0', 'super_admin', '2026-05-16 21:07:24', '', NULL, '');
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (181, 2, '中', '2', 'project_priority', '', '', 'N', '0', 'super_admin', '2026-05-16 21:08:02', '', NULL, '');
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (182, 3, '高', '3', 'project_priority', '', '', 'N', '0', 'super_admin', '2026-05-16 21:08:09', '', NULL, '');
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (183, 4, '紧急', '4', 'project_priority', '', '', 'N', '0', 'super_admin', '2026-05-16 21:08:18', '', NULL, '');
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (184, 1, '客户开发', '1', 'project_type', '', '', 'N', '0', 'super_admin', '2026-05-16 21:08:59', '', NULL, '');
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (185, 2, '客户维护', '2', 'project_type', '', '', 'N', '0', 'super_admin', '2026-05-16 21:09:07', '', NULL, '');
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (186, 3, '客户二期', '3', 'project_type', '', '', 'N', '0', 'super_admin', '2026-05-16 21:09:17', '', NULL, '');
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (187, 4, '产品研发', '4', 'project_type', '', '', 'N', '0', 'super_admin', '2026-05-16 21:09:28', '', NULL, '');
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (188, 5, '产品迭代', '5', 'project_type', '', '', 'N', '0', 'super_admin', '2026-05-16 21:09:43', '', NULL, '');
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (189, 6, '内部工具', '6', 'project_type', '', '', 'N', '0', 'super_admin', '2026-05-16 21:09:55', '', NULL, '');
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (190, 7, '内部系统', '7', 'project_type', '', '', 'N', '0', 'super_admin', '2026-05-16 21:10:08', '', NULL, '');
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (191, 8, '实验性', '8', 'project_type', '', '', 'N', '0', 'super_admin', '2026-05-16 21:10:19', '', NULL, '');
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (192, 9, '开源', '9', 'project_type', '', '', 'N', '0', 'super_admin', '2026-05-16 21:10:30', '', NULL, '');
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (193, 10, '技术研发', '10', 'project_type', '', '', 'N', '0', 'super_admin', '2026-05-16 21:10:41', '', NULL, '');
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (194, 7, '归档', '7', 'project_status', '', '', 'N', '0', 'super_admin', '2026-05-16 22:04:34', '', NULL, '');
COMMIT;

-- ----------------------------
-- Table structure for sys_dict_type
-- ----------------------------
DROP TABLE IF EXISTS `sys_dict_type`;
CREATE TABLE `sys_dict_type` (
  `dict_id` bigint NOT NULL AUTO_INCREMENT COMMENT '字典主键',
  `dict_name` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT '' COMMENT '字典名称',
  `dict_type` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT '' COMMENT '字典类型',
  `status` char(1) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `create_by` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`dict_id`) USING BTREE,
  UNIQUE KEY `dict_type` (`dict_type`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=54 DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC COMMENT='字典类型表';

-- ----------------------------
-- Records of sys_dict_type
-- ----------------------------
BEGIN;
INSERT INTO `sys_dict_type` (`dict_id`, `dict_name`, `dict_type`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (1, '用户性别', 'sys_user_sex', '0', 'system', '2026-02-11 16:37:36', '', NULL, '用户性别字典');
INSERT INTO `sys_dict_type` (`dict_id`, `dict_name`, `dict_type`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (2, '用户状态', 'sys_user_status', '0', 'system', '2026-02-11 16:37:36', '', NULL, '用户状态字典');
INSERT INTO `sys_dict_type` (`dict_id`, `dict_name`, `dict_type`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (3, '部门状态', 'sys_dept_status', '0', 'system', '2026-02-11 16:37:36', '', NULL, '部门状态字典');
INSERT INTO `sys_dict_type` (`dict_id`, `dict_name`, `dict_type`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (4, '角色状态', 'sys_role_status', '0', 'system', '2026-02-11 16:37:36', '', NULL, '角色状态字典');
INSERT INTO `sys_dict_type` (`dict_id`, `dict_name`, `dict_type`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (7, '通用状态', 'sys_common_status', '0', 'system', '2026-02-11 16:37:36', '', NULL, '通用状态字典（启用/停用）');
INSERT INTO `sys_dict_type` (`dict_id`, `dict_name`, `dict_type`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (8, '是否选项', 'sys_yes_no', '0', 'system', '2026-02-11 16:37:36', '', NULL, '是否选项字典');
INSERT INTO `sys_dict_type` (`dict_id`, `dict_name`, `dict_type`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (10, '数据范围', 'sys_data_scope', '0', 'system', '2026-02-11 16:37:36', '', NULL, '数据权限范围字典');
INSERT INTO `sys_dict_type` (`dict_id`, `dict_name`, `dict_type`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (11, '登录类型', 'sys_login_type', '0', 'system', '2026-02-11 16:37:36', '', NULL, '用户登录方式字典');
INSERT INTO `sys_dict_type` (`dict_id`, `dict_name`, `dict_type`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (12, '设备类型', 'sys_device_type', '0', 'system', '2026-02-11 16:37:36', '', NULL, '设备类型字典');
INSERT INTO `sys_dict_type` (`dict_id`, `dict_name`, `dict_type`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (13, '操作日志级别', 'sys_log_level', '0', 'system', '2026-02-11 16:37:36', '', NULL, '操作日志级别字典');
INSERT INTO `sys_dict_type` (`dict_id`, `dict_name`, `dict_type`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (14, '操作类型', 'sys_oper_type', '0', 'system', '2026-02-11 16:37:36', '', NULL, '操作类型字典');
INSERT INTO `sys_dict_type` (`dict_id`, `dict_name`, `dict_type`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (15, '操作模块', 'sys_oper_module', '0', 'system', '2026-02-11 17:18:03', '', NULL, '操作日志模块字典');
INSERT INTO `sys_dict_type` (`dict_id`, `dict_name`, `dict_type`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (49, 'Oauth平台', 'oauth_platform', '0', 'super_admin', '2026-05-07 20:44:36', '', NULL, '');
INSERT INTO `sys_dict_type` (`dict_id`, `dict_name`, `dict_type`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (50, 'Oauth状态', 'oauth_status', '0', 'super_admin', '2026-05-08 16:06:37', '', NULL, '');
INSERT INTO `sys_dict_type` (`dict_id`, `dict_name`, `dict_type`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (51, '项目状态', 'project_status', '0', 'super_admin', '2026-05-16 21:01:20', '', NULL, '');
INSERT INTO `sys_dict_type` (`dict_id`, `dict_name`, `dict_type`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (52, '项目优先级', 'project_priority', '0', 'super_admin', '2026-05-16 21:07:13', '', NULL, '');
INSERT INTO `sys_dict_type` (`dict_id`, `dict_name`, `dict_type`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (53, '项目类型', 'project_type', '0', 'super_admin', '2026-05-16 21:08:43', '', NULL, '');
COMMIT;

-- ----------------------------
-- Table structure for sys_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `parent_id` bigint DEFAULT '0' COMMENT '父菜单ID ( 0 表示顶级）',
  `name` varchar(100) DEFAULT NULL COMMENT '路由名称',
  `path` varchar(200) DEFAULT NULL COMMENT '路由路径',
  `component` varchar(200) DEFAULT NULL COMMENT '组件路径',
  `title` varchar(100) DEFAULT NULL COMMENT '菜单标题（支持 i18n key)',
  `icon` varchar(100) DEFAULT NULL COMMENT '菜单图标',
  `menu_type` tinyint DEFAULT NULL COMMENT '菜单类型( 0目录 1菜单 2按钮)',
  `sort` int DEFAULT NULL COMMENT '排序号',
  `permission` varchar(200) DEFAULT NULL COMMENT '权限标识符',
  `is_hide` tinyint DEFAULT '0' COMMENT '是否隐藏菜单（0否 1是）',
  `is_hide_tab` tinyint DEFAULT '0' COMMENT '是否隐藏标签页（0否 1是）',
  `keep_alive` tinyint DEFAULT '1' COMMENT '是否缓存（0 否 1是）',
  `is_iframe` tinyint DEFAULT '0' COMMENT '是否iframe：0=否, 1=是',
  `link` varchar(500) DEFAULT NULL COMMENT '外部链接地址',
  `is_full_page` tinyint DEFAULT '0' COMMENT '是否全屏页面：0=否, 1=是',
  `fixed_tab` int DEFAULT '0' COMMENT '是否固定标签页0=否, 1=是',
  `active_path` varchar(200) DEFAULT NULL COMMENT '激活菜单路径（详情页用）',
  `show_badge` tinyint DEFAULT '0' COMMENT '是否显示徽章',
  `show_text_badge` varchar(50) DEFAULT NULL COMMENT '文本徽章内容（如 New）',
  `status` tinyint DEFAULT '1' COMMENT '状态：0=禁用, 1=启用',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=141 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='系统菜单表';

-- ----------------------------
-- Records of sys_menu
-- ----------------------------
BEGIN;
INSERT INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `title`, `icon`, `menu_type`, `sort`, `permission`, `is_hide`, `is_hide_tab`, `keep_alive`, `is_iframe`, `link`, `is_full_page`, `fixed_tab`, `active_path`, `show_badge`, `show_text_badge`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (1, 0, 'Dashboard', '/dashboard', '/index/index', 'menus.dashboard.title', 'ri:pie-chart-line', 0, 1, NULL, 0, 0, 0, 0, NULL, 0, 0, NULL, 0, NULL, 1, 'system', '2025-12-30 15:29:16', 'super_admin', '2026-05-16 22:08:16', '仪表盘目录');
INSERT INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `title`, `icon`, `menu_type`, `sort`, `permission`, `is_hide`, `is_hide_tab`, `keep_alive`, `is_iframe`, `link`, `is_full_page`, `fixed_tab`, `active_path`, `show_badge`, `show_text_badge`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (2, 1, 'Console', 'console', '/dashboard/console', 'menus.dashboard.console', 'ri:home-smile-2-line', 1, 1, NULL, 0, 0, 0, 0, NULL, 0, 1, NULL, 0, NULL, 1, 'system', '2025-12-30 15:29:16', 'super_admin', '2026-05-16 22:08:16', '控制台');
INSERT INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `title`, `icon`, `menu_type`, `sort`, `permission`, `is_hide`, `is_hide_tab`, `keep_alive`, `is_iframe`, `link`, `is_full_page`, `fixed_tab`, `active_path`, `show_badge`, `show_text_badge`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (3, 1, 'Analysis', 'analysis', '/dashboard/analysis', 'menus.dashboard.analysis', 'ri:align-item-bottom-line', 1, 4, NULL, 0, 0, 0, 0, NULL, 0, 0, NULL, 0, NULL, 1, 'system', '2025-12-30 15:29:16', 'super_admin', '2026-05-16 22:08:16', '数据分析');
INSERT INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `title`, `icon`, `menu_type`, `sort`, `permission`, `is_hide`, `is_hide_tab`, `keep_alive`, `is_iframe`, `link`, `is_full_page`, `fixed_tab`, `active_path`, `show_badge`, `show_text_badge`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (4, 1, 'Ecommerce', 'ecommerce', '/dashboard/ecommerce', 'menus.dashboard.ecommerce', 'ri:bar-chart-box-line', 1, 3, NULL, 0, 0, 0, 0, NULL, 0, 0, NULL, 0, NULL, 1, 'system', '2025-12-30 15:29:16', 'super_admin', '2026-05-16 22:08:16', '电商数据');
INSERT INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `title`, `icon`, `menu_type`, `sort`, `permission`, `is_hide`, `is_hide_tab`, `keep_alive`, `is_iframe`, `link`, `is_full_page`, `fixed_tab`, `active_path`, `show_badge`, `show_text_badge`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (5, 0, 'System', '/system', '/index/index', 'menus.system.title', 'ri:settings-3-line', 0, 5, NULL, 0, 0, 0, 0, NULL, 0, 0, NULL, 0, NULL, 1, 'system', '2025-12-30 15:29:16', 'super_admin', '2026-05-16 22:08:17', '系统管理目录');
INSERT INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `title`, `icon`, `menu_type`, `sort`, `permission`, `is_hide`, `is_hide_tab`, `keep_alive`, `is_iframe`, `link`, `is_full_page`, `fixed_tab`, `active_path`, `show_badge`, `show_text_badge`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (6, 5, 'User', 'user', '/system/user', 'menus.system.user', 'ri:user-line', 1, 1, 'system.ui.user.view', 0, 0, 1, 0, '', 0, 0, '', 0, '', 1, 'system', '2025-12-30 15:29:16', 'super_admin', '2026-05-16 22:08:17', '用户管理');
INSERT INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `title`, `icon`, `menu_type`, `sort`, `permission`, `is_hide`, `is_hide_tab`, `keep_alive`, `is_iframe`, `link`, `is_full_page`, `fixed_tab`, `active_path`, `show_badge`, `show_text_badge`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (7, 5, 'Role', 'role', '/system/role', 'menus.system.role', 'ri:user-settings-line', 1, 2, 'system.ui.role.view', 0, 0, 1, 0, '', 0, 0, '', 0, '', 1, 'system', '2025-12-30 15:29:16', 'super_admin', '2026-05-16 22:08:17', '角色管理');
INSERT INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `title`, `icon`, `menu_type`, `sort`, `permission`, `is_hide`, `is_hide_tab`, `keep_alive`, `is_iframe`, `link`, `is_full_page`, `fixed_tab`, `active_path`, `show_badge`, `show_text_badge`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (8, 5, 'UserCenter', 'user-center', '/system/user-center', 'menus.system.userCenter', 'ri:user-line', 1, 11, NULL, 1, 1, 1, 0, '', 0, 0, '', 0, '', 1, 'system', '2025-12-30 15:29:16', 'super_admin', '2026-05-16 22:08:17', '个人中心（隐藏）');
INSERT INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `title`, `icon`, `menu_type`, `sort`, `permission`, `is_hide`, `is_hide_tab`, `keep_alive`, `is_iframe`, `link`, `is_full_page`, `fixed_tab`, `active_path`, `show_badge`, `show_text_badge`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (9, 5, 'Menus', 'menu', '/system/menu', 'menus.system.menu', 'ri:menu-search-line', 1, 7, 'system.ui.menu.view', 0, 0, 1, 0, '', 0, 0, '', 0, '', 1, 'system', '2025-12-30 15:29:16', 'super_admin', '2026-05-16 22:08:17', '菜单管理');
INSERT INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `title`, `icon`, `menu_type`, `sort`, `permission`, `is_hide`, `is_hide_tab`, `keep_alive`, `is_iframe`, `link`, `is_full_page`, `fixed_tab`, `active_path`, `show_badge`, `show_text_badge`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (93, 0, 'Monitor', '/monitor', '/index/index', 'menus.monitor.title', 'mdi:monitor-dashboard', 0, 6, '', 0, 0, 1, 0, '', 0, 0, '', 0, '', 1, 'system', '2025-12-31 13:13:18', 'super_admin', '2026-05-16 22:08:17', '');
INSERT INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `title`, `icon`, `menu_type`, `sort`, `permission`, `is_hide`, `is_hide_tab`, `keep_alive`, `is_iframe`, `link`, `is_full_page`, `fixed_tab`, `active_path`, `show_badge`, `show_text_badge`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (94, 93, 'Cache', 'cache', '/system/cache', 'menus.monitor.cache', 'simple-icons:redis', 1, 2, 'system.ui.cache.view', 0, 0, 1, 0, '', 0, 0, '', 0, '', 1, 'system', '2025-12-31 13:13:18', 'super_admin', '2026-05-16 22:08:18', '');
INSERT INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `title`, `icon`, `menu_type`, `sort`, `permission`, `is_hide`, `is_hide_tab`, `keep_alive`, `is_iframe`, `link`, `is_full_page`, `fixed_tab`, `active_path`, `show_badge`, `show_text_badge`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (101, 5, 'PermissionGroup', 'group', '/system/permission', 'menus.system.permissionGroup', 'ri:folder-lock-line', 1, 4, 'system.ui.permission.view', 0, 0, 1, 0, '', 0, 0, '', 0, '', 1, NULL, '2025-12-31 19:02:19', 'super_admin', '2026-05-16 22:08:17', '');
INSERT INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `title`, `icon`, `menu_type`, `sort`, `permission`, `is_hide`, `is_hide_tab`, `keep_alive`, `is_iframe`, `link`, `is_full_page`, `fixed_tab`, `active_path`, `show_badge`, `show_text_badge`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (102, 5, 'Department', 'department', '/system/department', 'menus.system.dept', 'ri:building-2-line', 1, 3, 'system.ui.dept.view', 0, 0, 1, 0, '', 0, 0, '', 0, '', 1, NULL, '2025-12-31 19:05:34', 'super_admin', '2026-05-16 22:08:17', '');
INSERT INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `title`, `icon`, `menu_type`, `sort`, `permission`, `is_hide`, `is_hide_tab`, `keep_alive`, `is_iframe`, `link`, `is_full_page`, `fixed_tab`, `active_path`, `show_badge`, `show_text_badge`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (115, 93, 'Session', 'session', '/system/session', 'menus.monitor.session', 'ri:chat-settings-line', 1, 1, 'system.ui.session.view', 0, 0, 1, 0, '', 0, 0, '', 0, '', 1, NULL, '2026-01-05 16:28:14', 'super_admin', '2026-05-16 22:08:17', '');
INSERT INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `title`, `icon`, `menu_type`, `sort`, `permission`, `is_hide`, `is_hide_tab`, `keep_alive`, `is_iframe`, `link`, `is_full_page`, `fixed_tab`, `active_path`, `show_badge`, `show_text_badge`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (116, 5, 'PermissionPool', 'permission-pool', '/system/permission-pool', 'menus.system.permissionPool', '', 1, 5, 'system.ui.permission.pool.view', 1, 0, 1, 0, '', 0, 0, '', 0, '', 1, NULL, '2026-01-07 16:43:49', 'super_admin', '2026-05-16 22:08:17', '');
INSERT INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `title`, `icon`, `menu_type`, `sort`, `permission`, `is_hide`, `is_hide_tab`, `keep_alive`, `is_iframe`, `link`, `is_full_page`, `fixed_tab`, `active_path`, `show_badge`, `show_text_badge`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (118, 93, 'System-Info', 'info', '/system/info', 'menus.monitor.info', 'ri:information-2-line', 1, 3, 'system.ui.info.view', 0, 0, 1, 0, '', 0, 0, '', 0, '', 1, NULL, '2026-01-18 18:50:03', 'super_admin', '2026-05-16 22:08:18', '');
INSERT INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `title`, `icon`, `menu_type`, `sort`, `permission`, `is_hide`, `is_hide_tab`, `keep_alive`, `is_iframe`, `link`, `is_full_page`, `fixed_tab`, `active_path`, `show_badge`, `show_text_badge`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (119, 122, 'AuthLog', 'auth-log', '/system/log/auth', 'menus.system.auth-log', 'ri:book-ai-line', 1, 1, 'system.ui.auth-log.view', 0, 0, 1, 0, '', 0, 0, '', 0, '', 1, NULL, '2026-01-18 18:53:23', 'super_admin', '2026-05-16 22:08:17', '');
INSERT INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `title`, `icon`, `menu_type`, `sort`, `permission`, `is_hide`, `is_hide_tab`, `keep_alive`, `is_iframe`, `link`, `is_full_page`, `fixed_tab`, `active_path`, `show_badge`, `show_text_badge`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (120, 122, 'OperLog', 'oper-log', '/system/log/oper', 'menus.system.oper-log', 'ri:book-read-line', 1, 2, 'system.ui.oper-log.view', 0, 0, 1, 0, '', 0, 0, '', 0, '', 1, NULL, '2026-01-18 18:54:15', 'super_admin', '2026-05-16 22:08:17', '');
INSERT INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `title`, `icon`, `menu_type`, `sort`, `permission`, `is_hide`, `is_hide_tab`, `keep_alive`, `is_iframe`, `link`, `is_full_page`, `fixed_tab`, `active_path`, `show_badge`, `show_text_badge`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (121, 5, 'Dictionary', 'dict', '/system/dict', 'menus.system.dict', 'ri:book-2-fill', 1, 6, 'system.ui.dict.view', 0, 0, 1, 0, '', 0, 0, '', 0, '', 1, NULL, '2026-01-18 19:02:45', 'super_admin', '2026-05-16 22:08:17', '');
INSERT INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `title`, `icon`, `menu_type`, `sort`, `permission`, `is_hide`, `is_hide_tab`, `keep_alive`, `is_iframe`, `link`, `is_full_page`, `fixed_tab`, `active_path`, `show_badge`, `show_text_badge`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (122, 5, 'LogManage', 'log', '', 'menus.system.log', 'ri:survey-line', 0, 10, '', 0, 0, 1, 0, '', 0, 0, '', 0, '', 1, NULL, '2026-01-18 19:07:31', 'super_admin', '2026-05-16 22:08:17', '');
INSERT INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `title`, `icon`, `menu_type`, `sort`, `permission`, `is_hide`, `is_hide_tab`, `keep_alive`, `is_iframe`, `link`, `is_full_page`, `fixed_tab`, `active_path`, `show_badge`, `show_text_badge`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (124, 5, 'Config', 'config', '/system/config', 'menus.system.config', 'ri:settings-4-line', 1, 8, 'system.ui.config.view', 0, 0, 1, 0, '', 0, 0, '', 0, '', 1, NULL, '2026-02-06 12:24:07', 'super_admin', '2026-05-16 22:08:17', '');
INSERT INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `title`, `icon`, `menu_type`, `sort`, `permission`, `is_hide`, `is_hide_tab`, `keep_alive`, `is_iframe`, `link`, `is_full_page`, `fixed_tab`, `active_path`, `show_badge`, `show_text_badge`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (125, 1, 'MyTask', 'my-task', '/dashboard/my-task', '我的任务', 'ri:pages-line', 1, 2, '', 0, 0, 0, 0, '', 0, 0, '', 0, '', 1, NULL, '2026-05-07 08:33:39', 'super_admin', '2026-05-16 22:08:16', '');
INSERT INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `title`, `icon`, `menu_type`, `sort`, `permission`, `is_hide`, `is_hide_tab`, `keep_alive`, `is_iframe`, `link`, `is_full_page`, `fixed_tab`, `active_path`, `show_badge`, `show_text_badge`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (126, 0, 'Task', '/task', '/index/index', '任务中心', 'ri:file-list-line', 0, 2, '', 0, 0, 1, 0, '', 0, 0, '', 0, '', 1, NULL, '2026-05-07 08:35:29', 'super_admin', '2026-05-16 22:08:16', '');
INSERT INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `title`, `icon`, `menu_type`, `sort`, `permission`, `is_hide`, `is_hide_tab`, `keep_alive`, `is_iframe`, `link`, `is_full_page`, `fixed_tab`, `active_path`, `show_badge`, `show_text_badge`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (127, 126, 'TaskAnalysis', 'analysis', '/task/analysis', '任务分析', 'ri:table-line', 1, 1, 'task.ui.analysis.view', 0, 0, 1, 0, '', 0, 0, '', 0, '', 1, NULL, '2026-05-07 08:37:05', 'super_admin', '2026-05-16 22:08:16', '');
INSERT INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `title`, `icon`, `menu_type`, `sort`, `permission`, `is_hide`, `is_hide_tab`, `keep_alive`, `is_iframe`, `link`, `is_full_page`, `fixed_tab`, `active_path`, `show_badge`, `show_text_badge`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (128, 126, 'TaskList', 'list', '/task/list', '任务管理', 'ri:list-view', 1, 2, 'task.ui.list.view', 0, 0, 1, 0, '', 0, 0, '', 0, '', 1, NULL, '2026-05-07 08:37:54', 'super_admin', '2026-05-16 22:08:16', '');
INSERT INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `title`, `icon`, `menu_type`, `sort`, `permission`, `is_hide`, `is_hide_tab`, `keep_alive`, `is_iframe`, `link`, `is_full_page`, `fixed_tab`, `active_path`, `show_badge`, `show_text_badge`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (129, 0, 'Project', '/project', '/index/index', '项目协作', 'ri:projector-line', 0, 3, '', 0, 0, 1, 0, '', 0, 0, '', 0, '', 1, NULL, '2026-05-07 08:39:41', 'super_admin', '2026-05-16 22:08:16', '');
INSERT INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `title`, `icon`, `menu_type`, `sort`, `permission`, `is_hide`, `is_hide_tab`, `keep_alive`, `is_iframe`, `link`, `is_full_page`, `fixed_tab`, `active_path`, `show_badge`, `show_text_badge`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (130, 129, 'ProjectList', 'list', '/project/list', '项目管理', 'ri:list-ordered', 1, 3, 'project.ui.list.view', 0, 0, 0, 0, '', 0, 0, '', 0, '', 1, NULL, '2026-05-07 08:40:29', 'super_admin', '2026-05-16 22:08:16', '');
INSERT INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `title`, `icon`, `menu_type`, `sort`, `permission`, `is_hide`, `is_hide_tab`, `keep_alive`, `is_iframe`, `link`, `is_full_page`, `fixed_tab`, `active_path`, `show_badge`, `show_text_badge`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (131, 129, 'ProjectDetail', 'detail', '/project/detail', '项目详情', '', 1, 5, '', 1, 1, 0, 0, '', 0, 0, '', 0, '', 1, NULL, '2026-05-07 08:41:38', 'super_admin', '2026-05-16 22:08:17', '');
INSERT INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `title`, `icon`, `menu_type`, `sort`, `permission`, `is_hide`, `is_hide_tab`, `keep_alive`, `is_iframe`, `link`, `is_full_page`, `fixed_tab`, `active_path`, `show_badge`, `show_text_badge`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (132, 129, 'ProjectApproval', 'approval', '/project/approval', '项目立项', 'ri:target-line', 1, 2, '', 0, 0, 1, 0, '', 0, 0, '', 0, '', 1, NULL, '2026-05-07 08:44:23', 'super_admin', '2026-05-16 22:08:16', '');
INSERT INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `title`, `icon`, `menu_type`, `sort`, `permission`, `is_hide`, `is_hide_tab`, `keep_alive`, `is_iframe`, `link`, `is_full_page`, `fixed_tab`, `active_path`, `show_badge`, `show_text_badge`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (133, 0, 'Data', '/data', '/index/index', '数据中心', 'ri:file-excel-2-line', 0, 4, '', 0, 0, 1, 0, '', 0, 0, '', 0, '', 1, NULL, '2026-05-07 08:49:46', 'super_admin', '2026-05-16 22:08:17', '');
INSERT INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `title`, `icon`, `menu_type`, `sort`, `permission`, `is_hide`, `is_hide_tab`, `keep_alive`, `is_iframe`, `link`, `is_full_page`, `fixed_tab`, `active_path`, `show_badge`, `show_text_badge`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (134, 133, 'ProjectStats', 'project-stats', '/data/project-stats', '项目统计', 'ri:projector-fill', 1, 1, '', 0, 0, 1, 0, '', 0, 0, '', 0, '', 1, NULL, '2026-05-07 08:50:52', 'super_admin', '2026-05-16 22:08:17', '');
INSERT INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `title`, `icon`, `menu_type`, `sort`, `permission`, `is_hide`, `is_hide_tab`, `keep_alive`, `is_iframe`, `link`, `is_full_page`, `fixed_tab`, `active_path`, `show_badge`, `show_text_badge`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (135, 133, 'DataWorkingHoursStats', 'working-hours-stats', '/data/working-hours-stats', '工时统计', 'ri:timer-line', 1, 2, '', 0, 0, 1, 0, '', 0, 0, '', 0, '', 1, NULL, '2026-05-07 09:04:08', 'super_admin', '2026-05-16 22:08:17', '');
INSERT INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `title`, `icon`, `menu_type`, `sort`, `permission`, `is_hide`, `is_hide_tab`, `keep_alive`, `is_iframe`, `link`, `is_full_page`, `fixed_tab`, `active_path`, `show_badge`, `show_text_badge`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (136, 5, 'ThirdPartyAuth', 'third-party-auth', '', '第三方认证', 'ri:lock-2-line', 0, 9, '', 0, 0, 1, 0, '', 0, 0, '', 0, '', 1, NULL, '2026-05-07 20:12:07', 'super_admin', '2026-05-16 22:08:17', '');
INSERT INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `title`, `icon`, `menu_type`, `sort`, `permission`, `is_hide`, `is_hide_tab`, `keep_alive`, `is_iframe`, `link`, `is_full_page`, `fixed_tab`, `active_path`, `show_badge`, `show_text_badge`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (137, 136, 'AccountBinding', 'account-binding', '/system/third-party-auth/account-binding', '账号绑定', 'ri:admin-line', 1, 2, 'oauth.ui.account-binding.view', 0, 0, 1, 0, '', 0, 0, '', 0, '', 1, NULL, '2026-05-07 20:19:02', 'super_admin', '2026-05-16 22:08:17', '');
INSERT INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `title`, `icon`, `menu_type`, `sort`, `permission`, `is_hide`, `is_hide_tab`, `keep_alive`, `is_iframe`, `link`, `is_full_page`, `fixed_tab`, `active_path`, `show_badge`, `show_text_badge`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (138, 136, 'OauthConfig', 'oauth-config', '/system/third-party-auth/oauth-config', 'OAuth配置', 'ri:settings-2-line', 1, 1, 'oauth.ui.config.view', 1, 0, 1, 0, '', 0, 0, '', 0, '', 0, NULL, '2026-05-07 20:18:30', 'super_admin', '2026-05-16 22:08:17', '');
INSERT INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `title`, `icon`, `menu_type`, `sort`, `permission`, `is_hide`, `is_hide_tab`, `keep_alive`, `is_iframe`, `link`, `is_full_page`, `fixed_tab`, `active_path`, `show_badge`, `show_text_badge`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (139, 129, 'ProjectActive', 'active', '/project/active', '活跃项目', 'ri:flashlight-line', 1, 1, '', 0, 0, 0, 0, '', 0, 0, '', 0, '', 1, NULL, '2026-05-16 21:23:13', 'super_admin', '2026-05-16 22:08:16', '');
INSERT INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `title`, `icon`, `menu_type`, `sort`, `permission`, `is_hide`, `is_hide_tab`, `keep_alive`, `is_iframe`, `link`, `is_full_page`, `fixed_tab`, `active_path`, `show_badge`, `show_text_badge`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (140, 129, 'ProjectArchived', 'archived', '/project/archived', '已归档项目', 'ri:archive-drawer-line', 1, 4, 'project.ui.archived.view', 0, 0, 1, 0, '', 0, 0, '', 0, '', 1, NULL, '2026-05-16 21:45:55', 'super_admin', '2026-05-16 22:08:16', '');
COMMIT;

-- ----------------------------
-- Table structure for sys_oper_log
-- ----------------------------
DROP TABLE IF EXISTS `sys_oper_log`;
CREATE TABLE `sys_oper_log` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `level` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'info' COMMENT '日志级别：info/warn/error',
  `action` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '动作：view/create/update/delete/export/import',
  `module` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '模块：vulnerability/webshell/project/user等',
  `user_id` bigint DEFAULT NULL COMMENT '操作用户ID',
  `user_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '用户名',
  `nick_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '昵称',
  `message` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '详情描述',
  `target_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '对象ID',
  `target_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '对象名称',
  `path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '请求路径',
  `method` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '请求方法：GET/POST/PUT/DELETE',
  `ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '操作IP',
  `raw_data` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '原始数据JSON',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_module` (`module`),
  KEY `idx_action` (`action`),
  KEY `idx_level` (`level`),
  KEY `idx_target_id` (`target_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB AUTO_INCREMENT=57 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='操作日志表';

-- ----------------------------
-- Records of sys_oper_log
-- ----------------------------
BEGIN;
INSERT INTO `sys_oper_log` (`id`, `level`, `action`, `module`, `user_id`, `user_name`, `nick_name`, `message`, `target_id`, `target_name`, `path`, `method`, `ip`, `raw_data`, `create_time`) VALUES (1, 'info', 'create', 'oauth_config', 1, 'super_admin', '超级管理员', '创建了OAuth配置「飞书」', '3', '飞书', '/oauth/config', 'POST', '127.0.0.1', '{\"id\":null,\"platform\":\"feishu\",\"status\":1,\"configKey\":\"\",\"configValue\":\"\",\"redirectUrl\":\"https://task.demo.junoyi.com\",\"remark\":\"\"}', '2026-05-08 22:13:20');
INSERT INTO `sys_oper_log` (`id`, `level`, `action`, `module`, `user_id`, `user_name`, `nick_name`, `message`, `target_id`, `target_name`, `path`, `method`, `ip`, `raw_data`, `create_time`) VALUES (2, 'info', 'update', 'oauth_config', 1, 'super_admin', '超级管理员', '更新了OAuth配置「飞书」', '3', '飞书', '/oauth/config', 'PUT', '127.0.0.1', '{\"id\":3,\"platform\":\"feishu\",\"status\":1,\"configKey\":\"3232\",\"configValue\":\"3232\",\"redirectUrl\":\"https://task.demo.junoyi.com\",\"remark\":\"\"}', '2026-05-08 22:13:39');
INSERT INTO `sys_oper_log` (`id`, `level`, `action`, `module`, `user_id`, `user_name`, `nick_name`, `message`, `target_id`, `target_name`, `path`, `method`, `ip`, `raw_data`, `create_time`) VALUES (3, 'info', 'update', 'oauth_config', 1, 'super_admin', '超级管理员', '更新了OAuth配置「飞书」', '3', '飞书', '/oauth/config', 'PUT', '127.0.0.1', '{\"id\":3,\"platform\":\"feishu\",\"status\":1,\"configKey\":\"32323\",\"configValue\":\"323232\",\"redirectUrl\":\"https://task.demo.junoyi.com\",\"remark\":\"\"}', '2026-05-08 22:13:48');
INSERT INTO `sys_oper_log` (`id`, `level`, `action`, `module`, `user_id`, `user_name`, `nick_name`, `message`, `target_id`, `target_name`, `path`, `method`, `ip`, `raw_data`, `create_time`) VALUES (4, 'info', 'update', 'oauth_config', 1, 'super_admin', '超级管理员', '更新了OAuth配置「飞书」', '3', '飞书', '/oauth/config', 'PUT', '127.0.0.1', '{\"id\":3,\"platform\":\"feishu\",\"status\":1,\"configKey\":\"32\",\"configValue\":\"3213\",\"redirectUrl\":\"https://task.demo.junoyi.com\",\"remark\":\"\"}', '2026-05-08 22:18:08');
INSERT INTO `sys_oper_log` (`id`, `level`, `action`, `module`, `user_id`, `user_name`, `nick_name`, `message`, `target_id`, `target_name`, `path`, `method`, `ip`, `raw_data`, `create_time`) VALUES (5, 'info', 'delete', 'oauth_config', 1, 'super_admin', '超级管理员', '删除了OAuth配置「飞书」', '3', '飞书', '/oauth/config/3', 'DELETE', '127.0.0.1', NULL, '2026-05-08 22:18:23');
INSERT INTO `sys_oper_log` (`id`, `level`, `action`, `module`, `user_id`, `user_name`, `nick_name`, `message`, `target_id`, `target_name`, `path`, `method`, `ip`, `raw_data`, `create_time`) VALUES (6, 'info', 'create', 'oauth_config', 1, 'super_admin', '超级管理员', '创建了OAuth配置「钉钉」', '4', '钉钉', '/oauth/config', 'POST', '127.0.0.1', '{\"id\":null,\"platform\":\"dingtalk\",\"status\":1,\"configKey\":\"32132\",\"configValue\":\"321321\",\"redirectUrl\":\"https://demo.junoyi.com\",\"remark\":\"32132132\"}', '2026-05-08 22:18:38');
INSERT INTO `sys_oper_log` (`id`, `level`, `action`, `module`, `user_id`, `user_name`, `nick_name`, `message`, `target_id`, `target_name`, `path`, `method`, `ip`, `raw_data`, `create_time`) VALUES (7, 'info', 'create', 'oauth_config', 1, 'super_admin', '超级管理员', '创建了OAuth配置「支付宝」', '5', '支付宝', '/oauth/config', 'POST', '127.0.0.1', '{\"id\":null,\"platform\":\"alipay\",\"status\":1,\"configKey\":\"1\",\"configValue\":\"2\",\"redirectUrl\":\"https://junoyi.com\",\"remark\":\"企业微信\"}', '2026-05-08 22:18:56');
INSERT INTO `sys_oper_log` (`id`, `level`, `action`, `module`, `user_id`, `user_name`, `nick_name`, `message`, `target_id`, `target_name`, `path`, `method`, `ip`, `raw_data`, `create_time`) VALUES (8, 'info', 'delete', 'oauth_config', 1, 'super_admin', '超级管理员', '批量删除了 2 个OAuth配置', NULL, NULL, '/oauth/config/batch', 'DELETE', '127.0.0.1', NULL, '2026-05-08 22:19:02');
INSERT INTO `sys_oper_log` (`id`, `level`, `action`, `module`, `user_id`, `user_name`, `nick_name`, `message`, `target_id`, `target_name`, `path`, `method`, `ip`, `raw_data`, `create_time`) VALUES (9, 'info', 'create', 'oauth_config', 1, 'super_admin', '超级管理员', '创建了OAuth配置「企业微信」', '1', '企业微信', '/oauth/config', 'POST', '127.0.0.1', '{\"id\":null,\"platform\":\"wework\",\"status\":1,\"configKey\":\"test132\",\"configValue\":\"Test1342\",\"redirectUrl\":\"https://www.test.com\",\"remark\":\"321321\"}', '2026-05-08 22:39:55');
INSERT INTO `sys_oper_log` (`id`, `level`, `action`, `module`, `user_id`, `user_name`, `nick_name`, `message`, `target_id`, `target_name`, `path`, `method`, `ip`, `raw_data`, `create_time`) VALUES (10, 'info', 'create', 'oauth_config', 1, 'super_admin', '超级管理员', '创建了OAuth配置「飞书」', '2', '飞书', '/oauth/config', 'POST', '127.0.0.1', '{\"id\":null,\"platform\":\"feishu\",\"status\":1,\"configKey\":\"feishu132\",\"configValue\":\"test321321\",\"redirectUrl\":\"https://redgfare.com\",\"remark\":\"321321321\"}', '2026-05-08 22:40:25');
INSERT INTO `sys_oper_log` (`id`, `level`, `action`, `module`, `user_id`, `user_name`, `nick_name`, `message`, `target_id`, `target_name`, `path`, `method`, `ip`, `raw_data`, `create_time`) VALUES (11, 'info', 'update', 'oauth_config', 1, 'super_admin', '超级管理员', '更新了OAuth配置「飞书」', '2', '飞书', '/oauth/config', 'PUT', '127.0.0.1', '{\"id\":2,\"platform\":\"feishu\",\"status\":1,\"configKey\":\"feishu132\",\"configValue\":\"fdsaf\",\"redirectUrl\":\"https://redgfare.com\",\"remark\":\"321321321\"}', '2026-05-08 22:40:33');
INSERT INTO `sys_oper_log` (`id`, `level`, `action`, `module`, `user_id`, `user_name`, `nick_name`, `message`, `target_id`, `target_name`, `path`, `method`, `ip`, `raw_data`, `create_time`) VALUES (12, 'info', 'update', 'oauth_config', 1, 'super_admin', '超级管理员', '更新了OAuth配置「企业微信」', '1', '企业微信', '/oauth/config', 'PUT', '127.0.0.1', '{\"id\":1,\"platform\":\"wework\",\"status\":1,\"configKey\":\"test13232\",\"configValue\":\"Test1342\",\"redirectUrl\":\"https://www.test.com\",\"remark\":\"321321\"}', '2026-05-08 22:40:41');
INSERT INTO `sys_oper_log` (`id`, `level`, `action`, `module`, `user_id`, `user_name`, `nick_name`, `message`, `target_id`, `target_name`, `path`, `method`, `ip`, `raw_data`, `create_time`) VALUES (13, 'info', 'delete', 'oauth_config', 1, 'super_admin', '超级管理员', '批量删除了 2 个OAuth配置', NULL, NULL, '/oauth/config/batch', 'DELETE', '127.0.0.1', NULL, '2026-05-08 22:40:48');
INSERT INTO `sys_oper_log` (`id`, `level`, `action`, `module`, `user_id`, `user_name`, `nick_name`, `message`, `target_id`, `target_name`, `path`, `method`, `ip`, `raw_data`, `create_time`) VALUES (14, 'info', 'update', 'menu', 1, 'super_admin', '超级管理员', '更新了菜单「OAuth配置」', '138', 'OAuth配置', '/system/menu', 'PUT', '127.0.0.1', NULL, '2026-05-09 17:39:21');
INSERT INTO `sys_oper_log` (`id`, `level`, `action`, `module`, `user_id`, `user_name`, `nick_name`, `message`, `target_id`, `target_name`, `path`, `method`, `ip`, `raw_data`, `create_time`) VALUES (15, 'info', 'update', 'menu', 1, 'super_admin', '超级管理员', '更新了菜单「OAuth配置」', '138', 'OAuth配置', '/system/menu', 'PUT', '127.0.0.1', NULL, '2026-05-09 17:39:24');
INSERT INTO `sys_oper_log` (`id`, `level`, `action`, `module`, `user_id`, `user_name`, `nick_name`, `message`, `target_id`, `target_name`, `path`, `method`, `ip`, `raw_data`, `create_time`) VALUES (16, 'info', 'create', 'task', 1, 'super_admin', '超级管理员', '创建了任务「测试任务」', '76', '测试任务', '/task/list', 'POST', '127.0.0.1', '{\"id\":null,\"title\":\"测试任务\",\"description\":\"这是一条测试任务\",\"priority\":1,\"dueTime\":\"2026-05-12 00:00:00\",\"userIds\":[3],\"ownerUserId\":3,\"remark\":\"\",\"syncSchedule\":false}', '2026-05-10 20:54:06');
INSERT INTO `sys_oper_log` (`id`, `level`, `action`, `module`, `user_id`, `user_name`, `nick_name`, `message`, `target_id`, `target_name`, `path`, `method`, `ip`, `raw_data`, `create_time`) VALUES (17, 'info', 'update', 'task', 1, 'super_admin', '超级管理员', '更新了任务「测试任务」', '76', '测试任务', '/task/list', 'PUT', '127.0.0.1', '{\"id\":76,\"title\":\"测试任务\",\"description\":\"这是一条测试任务\",\"priority\":1,\"dueTime\":\"2026-05-24 00:00:00\",\"userIds\":[3],\"ownerUserId\":3,\"remark\":\"\",\"syncSchedule\":true}', '2026-05-16 16:55:44');
INSERT INTO `sys_oper_log` (`id`, `level`, `action`, `module`, `user_id`, `user_name`, `nick_name`, `message`, `target_id`, `target_name`, `path`, `method`, `ip`, `raw_data`, `create_time`) VALUES (18, 'info', 'update', 'menu', 1, 'super_admin', '超级管理员', '更新了菜单「项目管理」', '130', '项目管理', '/system/menu', 'PUT', '127.0.0.1', NULL, '2026-05-16 20:38:15');
INSERT INTO `sys_oper_log` (`id`, `level`, `action`, `module`, `user_id`, `user_name`, `nick_name`, `message`, `target_id`, `target_name`, `path`, `method`, `ip`, `raw_data`, `create_time`) VALUES (19, 'info', 'create', 'dict_type', 1, 'super_admin', '超级管理员', '创建了字典类型「项目状态」', '51', '项目状态', '/system/dict/type', 'POST', '127.0.0.1', '{\"dictId\":null,\"dictName\":\"项目状态\",\"dictType\":\"project_status\",\"status\":\"0\",\"remark\":\"\"}', '2026-05-16 21:01:20');
INSERT INTO `sys_oper_log` (`id`, `level`, `action`, `module`, `user_id`, `user_name`, `nick_name`, `message`, `target_id`, `target_name`, `path`, `method`, `ip`, `raw_data`, `create_time`) VALUES (20, 'info', 'create', 'dict_data', 1, 'super_admin', '超级管理员', '创建了字典数据「待启动」', '173', '待启动', '/system/dict/data', 'POST', '127.0.0.1', '{\"dictCode\":null,\"dictSort\":0,\"dictLabel\":\"待启动\",\"dictValue\":\"0\",\"dictType\":\"project_status\",\"cssClass\":\"\",\"listClass\":\"\",\"isDefault\":\"N\",\"status\":\"0\",\"remark\":\"\"}', '2026-05-16 21:03:16');
INSERT INTO `sys_oper_log` (`id`, `level`, `action`, `module`, `user_id`, `user_name`, `nick_name`, `message`, `target_id`, `target_name`, `path`, `method`, `ip`, `raw_data`, `create_time`) VALUES (21, 'info', 'create', 'dict_data', 1, 'super_admin', '超级管理员', '创建了字典数据「进行中」', '174', '进行中', '/system/dict/data', 'POST', '127.0.0.1', '{\"dictCode\":null,\"dictSort\":1,\"dictLabel\":\"进行中\",\"dictValue\":\"1\",\"dictType\":\"project_status\",\"cssClass\":\"\",\"listClass\":\"\",\"isDefault\":\"N\",\"status\":\"0\",\"remark\":\"\"}', '2026-05-16 21:03:24');
INSERT INTO `sys_oper_log` (`id`, `level`, `action`, `module`, `user_id`, `user_name`, `nick_name`, `message`, `target_id`, `target_name`, `path`, `method`, `ip`, `raw_data`, `create_time`) VALUES (22, 'info', 'create', 'dict_data', 1, 'super_admin', '超级管理员', '创建了字典数据「暂停」', '175', '暂停', '/system/dict/data', 'POST', '127.0.0.1', '{\"dictCode\":null,\"dictSort\":2,\"dictLabel\":\"暂停\",\"dictValue\":\"2\",\"dictType\":\"project_status\",\"cssClass\":\"\",\"listClass\":\"\",\"isDefault\":\"N\",\"status\":\"0\",\"remark\":\"\"}', '2026-05-16 21:03:32');
INSERT INTO `sys_oper_log` (`id`, `level`, `action`, `module`, `user_id`, `user_name`, `nick_name`, `message`, `target_id`, `target_name`, `path`, `method`, `ip`, `raw_data`, `create_time`) VALUES (23, 'info', 'create', 'dict_data', 1, 'super_admin', '超级管理员', '创建了字典数据「待验收」', '176', '待验收', '/system/dict/data', 'POST', '127.0.0.1', '{\"dictCode\":null,\"dictSort\":3,\"dictLabel\":\"待验收\",\"dictValue\":\"3\",\"dictType\":\"project_status\",\"cssClass\":\"\",\"listClass\":\"\",\"isDefault\":\"N\",\"status\":\"0\",\"remark\":\"\"}', '2026-05-16 21:03:42');
INSERT INTO `sys_oper_log` (`id`, `level`, `action`, `module`, `user_id`, `user_name`, `nick_name`, `message`, `target_id`, `target_name`, `path`, `method`, `ip`, `raw_data`, `create_time`) VALUES (24, 'info', 'create', 'dict_data', 1, 'super_admin', '超级管理员', '创建了字典数据「已完成」', '177', '已完成', '/system/dict/data', 'POST', '127.0.0.1', '{\"dictCode\":null,\"dictSort\":4,\"dictLabel\":\"已完成\",\"dictValue\":\"4\",\"dictType\":\"project_status\",\"cssClass\":\"\",\"listClass\":\"\",\"isDefault\":\"N\",\"status\":\"0\",\"remark\":\"\"}', '2026-05-16 21:04:07');
INSERT INTO `sys_oper_log` (`id`, `level`, `action`, `module`, `user_id`, `user_name`, `nick_name`, `message`, `target_id`, `target_name`, `path`, `method`, `ip`, `raw_data`, `create_time`) VALUES (25, 'info', 'create', 'dict_data', 1, 'super_admin', '超级管理员', '创建了字典数据「已终止」', '178', '已终止', '/system/dict/data', 'POST', '127.0.0.1', '{\"dictCode\":null,\"dictSort\":5,\"dictLabel\":\"已终止\",\"dictValue\":\"5\",\"dictType\":\"project_status\",\"cssClass\":\"\",\"listClass\":\"\",\"isDefault\":\"N\",\"status\":\"0\",\"remark\":\"\"}', '2026-05-16 21:04:19');
INSERT INTO `sys_oper_log` (`id`, `level`, `action`, `module`, `user_id`, `user_name`, `nick_name`, `message`, `target_id`, `target_name`, `path`, `method`, `ip`, `raw_data`, `create_time`) VALUES (26, 'info', 'create', 'dict_data', 1, 'super_admin', '超级管理员', '创建了字典数据「长期维护」', '179', '长期维护', '/system/dict/data', 'POST', '127.0.0.1', '{\"dictCode\":null,\"dictSort\":6,\"dictLabel\":\"长期维护\",\"dictValue\":\"6\",\"dictType\":\"project_status\",\"cssClass\":\"\",\"listClass\":\"\",\"isDefault\":\"N\",\"status\":\"0\",\"remark\":\"\"}', '2026-05-16 21:04:29');
INSERT INTO `sys_oper_log` (`id`, `level`, `action`, `module`, `user_id`, `user_name`, `nick_name`, `message`, `target_id`, `target_name`, `path`, `method`, `ip`, `raw_data`, `create_time`) VALUES (27, 'info', 'create', 'dict_type', 1, 'super_admin', '超级管理员', '创建了字典类型「项目优先级」', '52', '项目优先级', '/system/dict/type', 'POST', '127.0.0.1', '{\"dictId\":null,\"dictName\":\"项目优先级\",\"dictType\":\"project_priority\",\"status\":\"0\",\"remark\":\"\"}', '2026-05-16 21:07:13');
INSERT INTO `sys_oper_log` (`id`, `level`, `action`, `module`, `user_id`, `user_name`, `nick_name`, `message`, `target_id`, `target_name`, `path`, `method`, `ip`, `raw_data`, `create_time`) VALUES (28, 'info', 'create', 'dict_data', 1, 'super_admin', '超级管理员', '创建了字典数据「低」', '180', '低', '/system/dict/data', 'POST', '127.0.0.1', '{\"dictCode\":null,\"dictSort\":1,\"dictLabel\":\"低\",\"dictValue\":\"1\",\"dictType\":\"project_priority\",\"cssClass\":\"\",\"listClass\":\"\",\"isDefault\":\"N\",\"status\":\"0\",\"remark\":\"\"}', '2026-05-16 21:07:24');
INSERT INTO `sys_oper_log` (`id`, `level`, `action`, `module`, `user_id`, `user_name`, `nick_name`, `message`, `target_id`, `target_name`, `path`, `method`, `ip`, `raw_data`, `create_time`) VALUES (29, 'info', 'create', 'dict_data', 1, 'super_admin', '超级管理员', '创建了字典数据「中」', '181', '中', '/system/dict/data', 'POST', '127.0.0.1', '{\"dictCode\":null,\"dictSort\":2,\"dictLabel\":\"中\",\"dictValue\":\"2\",\"dictType\":\"project_priority\",\"cssClass\":\"\",\"listClass\":\"\",\"isDefault\":\"N\",\"status\":\"0\",\"remark\":\"\"}', '2026-05-16 21:08:02');
INSERT INTO `sys_oper_log` (`id`, `level`, `action`, `module`, `user_id`, `user_name`, `nick_name`, `message`, `target_id`, `target_name`, `path`, `method`, `ip`, `raw_data`, `create_time`) VALUES (30, 'info', 'create', 'dict_data', 1, 'super_admin', '超级管理员', '创建了字典数据「高」', '182', '高', '/system/dict/data', 'POST', '127.0.0.1', '{\"dictCode\":null,\"dictSort\":3,\"dictLabel\":\"高\",\"dictValue\":\"3\",\"dictType\":\"project_priority\",\"cssClass\":\"\",\"listClass\":\"\",\"isDefault\":\"N\",\"status\":\"0\",\"remark\":\"\"}', '2026-05-16 21:08:09');
INSERT INTO `sys_oper_log` (`id`, `level`, `action`, `module`, `user_id`, `user_name`, `nick_name`, `message`, `target_id`, `target_name`, `path`, `method`, `ip`, `raw_data`, `create_time`) VALUES (31, 'info', 'create', 'dict_data', 1, 'super_admin', '超级管理员', '创建了字典数据「紧急」', '183', '紧急', '/system/dict/data', 'POST', '127.0.0.1', '{\"dictCode\":null,\"dictSort\":4,\"dictLabel\":\"紧急\",\"dictValue\":\"4\",\"dictType\":\"project_priority\",\"cssClass\":\"\",\"listClass\":\"\",\"isDefault\":\"N\",\"status\":\"0\",\"remark\":\"\"}', '2026-05-16 21:08:18');
INSERT INTO `sys_oper_log` (`id`, `level`, `action`, `module`, `user_id`, `user_name`, `nick_name`, `message`, `target_id`, `target_name`, `path`, `method`, `ip`, `raw_data`, `create_time`) VALUES (32, 'info', 'create', 'dict_type', 1, 'super_admin', '超级管理员', '创建了字典类型「项目类型」', '53', '项目类型', '/system/dict/type', 'POST', '127.0.0.1', '{\"dictId\":null,\"dictName\":\"项目类型\",\"dictType\":\"project_type\",\"status\":\"0\",\"remark\":\"\"}', '2026-05-16 21:08:43');
INSERT INTO `sys_oper_log` (`id`, `level`, `action`, `module`, `user_id`, `user_name`, `nick_name`, `message`, `target_id`, `target_name`, `path`, `method`, `ip`, `raw_data`, `create_time`) VALUES (33, 'info', 'create', 'dict_data', 1, 'super_admin', '超级管理员', '创建了字典数据「客户开发」', '184', '客户开发', '/system/dict/data', 'POST', '127.0.0.1', '{\"dictCode\":null,\"dictSort\":1,\"dictLabel\":\"客户开发\",\"dictValue\":\"1\",\"dictType\":\"project_type\",\"cssClass\":\"\",\"listClass\":\"\",\"isDefault\":\"N\",\"status\":\"0\",\"remark\":\"\"}', '2026-05-16 21:08:59');
INSERT INTO `sys_oper_log` (`id`, `level`, `action`, `module`, `user_id`, `user_name`, `nick_name`, `message`, `target_id`, `target_name`, `path`, `method`, `ip`, `raw_data`, `create_time`) VALUES (34, 'info', 'create', 'dict_data', 1, 'super_admin', '超级管理员', '创建了字典数据「客户维护」', '185', '客户维护', '/system/dict/data', 'POST', '127.0.0.1', '{\"dictCode\":null,\"dictSort\":2,\"dictLabel\":\"客户维护\",\"dictValue\":\"2\",\"dictType\":\"project_type\",\"cssClass\":\"\",\"listClass\":\"\",\"isDefault\":\"N\",\"status\":\"0\",\"remark\":\"\"}', '2026-05-16 21:09:07');
INSERT INTO `sys_oper_log` (`id`, `level`, `action`, `module`, `user_id`, `user_name`, `nick_name`, `message`, `target_id`, `target_name`, `path`, `method`, `ip`, `raw_data`, `create_time`) VALUES (35, 'info', 'create', 'dict_data', 1, 'super_admin', '超级管理员', '创建了字典数据「客户二期」', '186', '客户二期', '/system/dict/data', 'POST', '127.0.0.1', '{\"dictCode\":null,\"dictSort\":3,\"dictLabel\":\"客户二期\",\"dictValue\":\"3\",\"dictType\":\"project_type\",\"cssClass\":\"\",\"listClass\":\"\",\"isDefault\":\"N\",\"status\":\"0\",\"remark\":\"\"}', '2026-05-16 21:09:17');
INSERT INTO `sys_oper_log` (`id`, `level`, `action`, `module`, `user_id`, `user_name`, `nick_name`, `message`, `target_id`, `target_name`, `path`, `method`, `ip`, `raw_data`, `create_time`) VALUES (36, 'info', 'create', 'dict_data', 1, 'super_admin', '超级管理员', '创建了字典数据「产品研发」', '187', '产品研发', '/system/dict/data', 'POST', '127.0.0.1', '{\"dictCode\":null,\"dictSort\":4,\"dictLabel\":\"产品研发\",\"dictValue\":\"4\",\"dictType\":\"project_type\",\"cssClass\":\"\",\"listClass\":\"\",\"isDefault\":\"N\",\"status\":\"0\",\"remark\":\"\"}', '2026-05-16 21:09:28');
INSERT INTO `sys_oper_log` (`id`, `level`, `action`, `module`, `user_id`, `user_name`, `nick_name`, `message`, `target_id`, `target_name`, `path`, `method`, `ip`, `raw_data`, `create_time`) VALUES (37, 'info', 'create', 'dict_data', 1, 'super_admin', '超级管理员', '创建了字典数据「产品迭代」', '188', '产品迭代', '/system/dict/data', 'POST', '127.0.0.1', '{\"dictCode\":null,\"dictSort\":5,\"dictLabel\":\"产品迭代\",\"dictValue\":\"5\",\"dictType\":\"project_type\",\"cssClass\":\"\",\"listClass\":\"\",\"isDefault\":\"N\",\"status\":\"0\",\"remark\":\"\"}', '2026-05-16 21:09:43');
INSERT INTO `sys_oper_log` (`id`, `level`, `action`, `module`, `user_id`, `user_name`, `nick_name`, `message`, `target_id`, `target_name`, `path`, `method`, `ip`, `raw_data`, `create_time`) VALUES (38, 'info', 'create', 'dict_data', 1, 'super_admin', '超级管理员', '创建了字典数据「内部工具」', '189', '内部工具', '/system/dict/data', 'POST', '127.0.0.1', '{\"dictCode\":null,\"dictSort\":6,\"dictLabel\":\"内部工具\",\"dictValue\":\"6\",\"dictType\":\"project_type\",\"cssClass\":\"\",\"listClass\":\"\",\"isDefault\":\"N\",\"status\":\"0\",\"remark\":\"\"}', '2026-05-16 21:09:55');
INSERT INTO `sys_oper_log` (`id`, `level`, `action`, `module`, `user_id`, `user_name`, `nick_name`, `message`, `target_id`, `target_name`, `path`, `method`, `ip`, `raw_data`, `create_time`) VALUES (39, 'info', 'create', 'dict_data', 1, 'super_admin', '超级管理员', '创建了字典数据「内部系统」', '190', '内部系统', '/system/dict/data', 'POST', '127.0.0.1', '{\"dictCode\":null,\"dictSort\":7,\"dictLabel\":\"内部系统\",\"dictValue\":\"7\",\"dictType\":\"project_type\",\"cssClass\":\"\",\"listClass\":\"\",\"isDefault\":\"N\",\"status\":\"0\",\"remark\":\"\"}', '2026-05-16 21:10:08');
INSERT INTO `sys_oper_log` (`id`, `level`, `action`, `module`, `user_id`, `user_name`, `nick_name`, `message`, `target_id`, `target_name`, `path`, `method`, `ip`, `raw_data`, `create_time`) VALUES (40, 'info', 'create', 'dict_data', 1, 'super_admin', '超级管理员', '创建了字典数据「实验性」', '191', '实验性', '/system/dict/data', 'POST', '127.0.0.1', '{\"dictCode\":null,\"dictSort\":8,\"dictLabel\":\"实验性\",\"dictValue\":\"8\",\"dictType\":\"project_type\",\"cssClass\":\"\",\"listClass\":\"\",\"isDefault\":\"N\",\"status\":\"0\",\"remark\":\"\"}', '2026-05-16 21:10:19');
INSERT INTO `sys_oper_log` (`id`, `level`, `action`, `module`, `user_id`, `user_name`, `nick_name`, `message`, `target_id`, `target_name`, `path`, `method`, `ip`, `raw_data`, `create_time`) VALUES (41, 'info', 'create', 'dict_data', 1, 'super_admin', '超级管理员', '创建了字典数据「开源」', '192', '开源', '/system/dict/data', 'POST', '127.0.0.1', '{\"dictCode\":null,\"dictSort\":9,\"dictLabel\":\"开源\",\"dictValue\":\"9\",\"dictType\":\"project_type\",\"cssClass\":\"\",\"listClass\":\"\",\"isDefault\":\"N\",\"status\":\"0\",\"remark\":\"\"}', '2026-05-16 21:10:30');
INSERT INTO `sys_oper_log` (`id`, `level`, `action`, `module`, `user_id`, `user_name`, `nick_name`, `message`, `target_id`, `target_name`, `path`, `method`, `ip`, `raw_data`, `create_time`) VALUES (42, 'info', 'create', 'dict_data', 1, 'super_admin', '超级管理员', '创建了字典数据「技术研发」', '193', '技术研发', '/system/dict/data', 'POST', '127.0.0.1', '{\"dictCode\":null,\"dictSort\":10,\"dictLabel\":\"技术研发\",\"dictValue\":\"10\",\"dictType\":\"project_type\",\"cssClass\":\"\",\"listClass\":\"\",\"isDefault\":\"N\",\"status\":\"0\",\"remark\":\"\"}', '2026-05-16 21:10:41');
INSERT INTO `sys_oper_log` (`id`, `level`, `action`, `module`, `user_id`, `user_name`, `nick_name`, `message`, `target_id`, `target_name`, `path`, `method`, `ip`, `raw_data`, `create_time`) VALUES (43, 'info', 'create', 'project', 1, 'super_admin', '超级管理员', '创建了项目「测试项目1」（编号：PJ-20260516-5256）', '11', '测试项目1', '/project/list', 'POST', '127.0.0.1', '{\"id\":null,\"no\":null,\"name\":\"测试项目1\",\"description\":\"这是第一个测试项目\",\"leader\":2,\"type\":4,\"status\":null,\"priority\":1,\"remark\":\"\"}', '2026-05-16 21:12:14');
INSERT INTO `sys_oper_log` (`id`, `level`, `action`, `module`, `user_id`, `user_name`, `nick_name`, `message`, `target_id`, `target_name`, `path`, `method`, `ip`, `raw_data`, `create_time`) VALUES (44, 'info', 'update', 'project', 1, 'super_admin', '超级管理员', '修改了项目「测试项目2」（编号：PJ-20260516-5256）', '11', '测试项目2', '/project/list', 'PUT', '127.0.0.1', '{\"id\":11,\"no\":\"PJ-20260516-5256\",\"name\":\"测试项目2\",\"description\":\"这是第二个测试项目\",\"leader\":2,\"type\":1,\"status\":1,\"priority\":2,\"remark\":\"\"}', '2026-05-16 21:12:39');
INSERT INTO `sys_oper_log` (`id`, `level`, `action`, `module`, `user_id`, `user_name`, `nick_name`, `message`, `target_id`, `target_name`, `path`, `method`, `ip`, `raw_data`, `create_time`) VALUES (45, 'info', 'update', 'dict_data', 1, 'super_admin', '超级管理员', '更新了字典数据「待启动」', '173', '待启动', '/system/dict/data', 'PUT', '127.0.0.1', '{\"dictCode\":173,\"dictSort\":0,\"dictLabel\":\"待启动\",\"dictValue\":\"0\",\"dictType\":\"project_status\",\"cssClass\":\"\",\"listClass\":\"warning\",\"isDefault\":\"N\",\"status\":\"0\",\"remark\":\"\"}', '2026-05-16 21:12:57');
INSERT INTO `sys_oper_log` (`id`, `level`, `action`, `module`, `user_id`, `user_name`, `nick_name`, `message`, `target_id`, `target_name`, `path`, `method`, `ip`, `raw_data`, `create_time`) VALUES (46, 'info', 'update', 'dict_data', 1, 'super_admin', '超级管理员', '更新了字典数据「暂停」', '175', '暂停', '/system/dict/data', 'PUT', '127.0.0.1', '{\"dictCode\":175,\"dictSort\":2,\"dictLabel\":\"暂停\",\"dictValue\":\"2\",\"dictType\":\"project_status\",\"cssClass\":\"\",\"listClass\":\"danger\",\"isDefault\":\"N\",\"status\":\"0\",\"remark\":\"\"}', '2026-05-16 21:13:08');
INSERT INTO `sys_oper_log` (`id`, `level`, `action`, `module`, `user_id`, `user_name`, `nick_name`, `message`, `target_id`, `target_name`, `path`, `method`, `ip`, `raw_data`, `create_time`) VALUES (47, 'info', 'update', 'dict_data', 1, 'super_admin', '超级管理员', '更新了字典数据「进行中」', '174', '进行中', '/system/dict/data', 'PUT', '127.0.0.1', '{\"dictCode\":174,\"dictSort\":1,\"dictLabel\":\"进行中\",\"dictValue\":\"1\",\"dictType\":\"project_status\",\"cssClass\":\"\",\"listClass\":\"info\",\"isDefault\":\"N\",\"status\":\"0\",\"remark\":\"\"}', '2026-05-16 21:13:16');
INSERT INTO `sys_oper_log` (`id`, `level`, `action`, `module`, `user_id`, `user_name`, `nick_name`, `message`, `target_id`, `target_name`, `path`, `method`, `ip`, `raw_data`, `create_time`) VALUES (48, 'info', 'update', 'dict_data', 1, 'super_admin', '超级管理员', '更新了字典数据「进行中」', '174', '进行中', '/system/dict/data', 'PUT', '127.0.0.1', '{\"dictCode\":174,\"dictSort\":1,\"dictLabel\":\"进行中\",\"dictValue\":\"1\",\"dictType\":\"project_status\",\"cssClass\":\"\",\"listClass\":\"primary\",\"isDefault\":\"N\",\"status\":\"0\",\"remark\":\"\"}', '2026-05-16 21:13:22');
INSERT INTO `sys_oper_log` (`id`, `level`, `action`, `module`, `user_id`, `user_name`, `nick_name`, `message`, `target_id`, `target_name`, `path`, `method`, `ip`, `raw_data`, `create_time`) VALUES (49, 'info', 'update', 'dict_data', 1, 'super_admin', '超级管理员', '更新了字典数据「暂停」', '175', '暂停', '/system/dict/data', 'PUT', '127.0.0.1', '{\"dictCode\":175,\"dictSort\":2,\"dictLabel\":\"暂停\",\"dictValue\":\"2\",\"dictType\":\"project_status\",\"cssClass\":\"\",\"listClass\":\"warning\",\"isDefault\":\"N\",\"status\":\"0\",\"remark\":\"\"}', '2026-05-16 21:13:26');
INSERT INTO `sys_oper_log` (`id`, `level`, `action`, `module`, `user_id`, `user_name`, `nick_name`, `message`, `target_id`, `target_name`, `path`, `method`, `ip`, `raw_data`, `create_time`) VALUES (50, 'info', 'update', 'dict_data', 1, 'super_admin', '超级管理员', '更新了字典数据「待验收」', '176', '待验收', '/system/dict/data', 'PUT', '127.0.0.1', '{\"dictCode\":176,\"dictSort\":3,\"dictLabel\":\"待验收\",\"dictValue\":\"3\",\"dictType\":\"project_status\",\"cssClass\":\"\",\"listClass\":\"warning\",\"isDefault\":\"N\",\"status\":\"0\",\"remark\":\"\"}', '2026-05-16 21:13:31');
INSERT INTO `sys_oper_log` (`id`, `level`, `action`, `module`, `user_id`, `user_name`, `nick_name`, `message`, `target_id`, `target_name`, `path`, `method`, `ip`, `raw_data`, `create_time`) VALUES (51, 'info', 'update', 'dict_data', 1, 'super_admin', '超级管理员', '更新了字典数据「已完成」', '177', '已完成', '/system/dict/data', 'PUT', '127.0.0.1', '{\"dictCode\":177,\"dictSort\":4,\"dictLabel\":\"已完成\",\"dictValue\":\"4\",\"dictType\":\"project_status\",\"cssClass\":\"\",\"listClass\":\"success\",\"isDefault\":\"N\",\"status\":\"0\",\"remark\":\"\"}', '2026-05-16 21:13:36');
INSERT INTO `sys_oper_log` (`id`, `level`, `action`, `module`, `user_id`, `user_name`, `nick_name`, `message`, `target_id`, `target_name`, `path`, `method`, `ip`, `raw_data`, `create_time`) VALUES (52, 'info', 'update', 'dict_data', 1, 'super_admin', '超级管理员', '更新了字典数据「已终止」', '178', '已终止', '/system/dict/data', 'PUT', '127.0.0.1', '{\"dictCode\":178,\"dictSort\":5,\"dictLabel\":\"已终止\",\"dictValue\":\"5\",\"dictType\":\"project_status\",\"cssClass\":\"\",\"listClass\":\"danger\",\"isDefault\":\"N\",\"status\":\"0\",\"remark\":\"\"}', '2026-05-16 21:13:44');
INSERT INTO `sys_oper_log` (`id`, `level`, `action`, `module`, `user_id`, `user_name`, `nick_name`, `message`, `target_id`, `target_name`, `path`, `method`, `ip`, `raw_data`, `create_time`) VALUES (53, 'info', 'update', 'dict_data', 1, 'super_admin', '超级管理员', '更新了字典数据「长期维护」', '179', '长期维护', '/system/dict/data', 'PUT', '127.0.0.1', '{\"dictCode\":179,\"dictSort\":6,\"dictLabel\":\"长期维护\",\"dictValue\":\"6\",\"dictType\":\"project_status\",\"cssClass\":\"\",\"listClass\":\"info\",\"isDefault\":\"N\",\"status\":\"0\",\"remark\":\"\"}', '2026-05-16 21:13:49');
INSERT INTO `sys_oper_log` (`id`, `level`, `action`, `module`, `user_id`, `user_name`, `nick_name`, `message`, `target_id`, `target_name`, `path`, `method`, `ip`, `raw_data`, `create_time`) VALUES (54, 'info', 'create', 'menu', 1, 'super_admin', '超级管理员', '创建了菜单「活跃项目」', '139', '活跃项目', '/system/menu', 'POST', '127.0.0.1', NULL, '2026-05-16 21:23:13');
INSERT INTO `sys_oper_log` (`id`, `level`, `action`, `module`, `user_id`, `user_name`, `nick_name`, `message`, `target_id`, `target_name`, `path`, `method`, `ip`, `raw_data`, `create_time`) VALUES (55, 'info', 'create', 'menu', 1, 'super_admin', '超级管理员', '创建了菜单「已归档项目」', '140', '已归档项目', '/system/menu', 'POST', '127.0.0.1', NULL, '2026-05-16 21:45:55');
INSERT INTO `sys_oper_log` (`id`, `level`, `action`, `module`, `user_id`, `user_name`, `nick_name`, `message`, `target_id`, `target_name`, `path`, `method`, `ip`, `raw_data`, `create_time`) VALUES (56, 'info', 'create', 'dict_data', 1, 'super_admin', '超级管理员', '创建了字典数据「归档」', '194', '归档', '/system/dict/data', 'POST', '127.0.0.1', '{\"dictCode\":null,\"dictSort\":7,\"dictLabel\":\"归档\",\"dictValue\":\"7\",\"dictType\":\"project_status\",\"cssClass\":\"\",\"listClass\":\"\",\"isDefault\":\"N\",\"status\":\"0\",\"remark\":\"\"}', '2026-05-16 22:04:35');
COMMIT;

-- ----------------------------
-- Table structure for sys_perm_group
-- ----------------------------
DROP TABLE IF EXISTS `sys_perm_group`;
CREATE TABLE `sys_perm_group` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `group_code` varchar(50) DEFAULT NULL COMMENT '权限组编码',
  `group_name` varchar(50) DEFAULT NULL COMMENT '权限组名称',
  `priority` int DEFAULT NULL COMMENT '优先级（数值越大优先级越高）',
  `description` varchar(500) DEFAULT NULL COMMENT '权限组描述',
  `status` int DEFAULT '1' COMMENT '状态（0停用，1启用）',
  `permissions` text COMMENT '权限列表',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(5000) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='系统权限组表';

-- ----------------------------
-- Records of sys_perm_group
-- ----------------------------
BEGIN;
INSERT INTO `sys_perm_group` (`id`, `group_code`, `group_name`, `priority`, `description`, `status`, `permissions`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (1, 'default_user', '默认用户权限组', 10, '系统所有登录用户的基础权限', 1, '[]', 'system', '2025-12-29 21:02:33', 'super_admin', '2026-01-08 19:38:18', '默认用户权限组');
INSERT INTO `sys_perm_group` (`id`, `group_code`, `group_name`, `priority`, `description`, `status`, `permissions`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (2, 'default_admin', '默认管理权限组', 100, '系统所有管理用户的基础权限', 1, '[\"system.ui.user.button.dept\",\"system.ui.auth-log.view\",\"system.ui.oper-log.view\",\"system.ui.menu.button.edit\",\"system.ui.dict.view\",\"system.ui.menu.button.add\",\"system.ui.session.button.logout\",\"system.ui.auth-log.button.delete\",\"system.ui.role.button.permission\",\"system.ui.dict.button.add\",\"system.ui.permission.pool.view\",\"system.ui.user.button.permission\",\"system.ui.config.button.edit\",\"system.ui.role.button.edit\",\"system.ui.oper-log.button.clear\",\"system.ui.user.button.role\",\"system.ui.dict.button.edit\",\"system.ui.dept.view\",\"system.ui.info.view\",\"system.ui.permission.button.delete\",\"system.ui.menu.view\",\"system.ui.permission.pool.button.delete\",\"system.ui.cache.button.detail\",\"system.ui.user.button.edit\",\"system.ui.user.view\",\"system.ui.permission.pool.button.add\",\"system.ui.permission.view\",\"system.ui.permission.button.edit\",\"system.ui.auth-log.button.clear\",\"system.ui.cache.button.clear\",\"system.ui.config.view\",\"system.ui.dept.button.permission\",\"system.ui.role.view\",\"system.ui.session.view\",\"system.ui.dept.button.add\",\"system.ui.cache.view\",\"system.ui.config.button.delete\",\"system.ui.config.button.add\",\"system.ui.user.button.individual-perm\",\"system.ui.role.button.delete\",\"system.ui.dept.button.edit\",\"system.ui.cache.button.delete\",\"system.ui.dict.button.delete\",\"system.ui.dept.button.delete\",\"system.ui.permission.button.add\",\"system.ui.user.button.delete\",\"system.ui.user.button.add\",\"system.ui.menu.button.delete\",\"system.ui.role.button.add\",\"system.ui.oper-log.button.delete\",\"system.ui.permission-pool.button.status\"]', 'system', '2025-12-29 21:04:58', 'super_admin', '2026-02-11 21:59:28', '默认管理权限组');
COMMIT;

-- ----------------------------
-- Table structure for sys_permission
-- ----------------------------
DROP TABLE IF EXISTS `sys_permission`;
CREATE TABLE `sys_permission` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `permission` varchar(200) DEFAULT NULL COMMENT '权限字符串',
  `description` varchar(255) DEFAULT NULL COMMENT '描述',
  `status` int DEFAULT '1' COMMENT '状态（1启用，0禁用）',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=144 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='系统权限池（开发期与运维期的权限注册表）';

-- ----------------------------
-- Records of sys_permission
-- ----------------------------
BEGIN;
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (1, 'system.ui.user.view', '系统用户管理页面权限', 1, 'super_admin', '2026-01-14 16:24:26', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (2, 'system.ui.user.button.edit', '系统用户管理用户编辑按钮权限', 1, 'super_admin', '2026-01-14 16:24:48', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (3, 'system.ui.user.button.role', '系统用户管理分配角色按钮权限', 1, 'super_admin', '2026-01-14 16:25:19', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (4, 'system.ui.user.button.dept', '系统用户管理分配部门按钮权限', 1, 'super_admin', '2026-01-14 16:25:43', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (5, 'system.ui.user.button.permission', '系统用户管理分配权限组按钮权限', 1, 'super_admin', '2026-01-14 16:26:13', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (6, 'system.ui.user.button.individual-perm', '系统用户管理分配独立权限按钮权限', 1, 'super_admin', '2026-01-14 16:26:38', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (7, 'system.ui.user.button.delete', '系统用户管理删除按钮权限', 1, 'super_admin', '2026-01-14 16:29:01', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (8, 'system.ui.user.button.add', '系统用户管理添加按钮权限', 1, 'super_admin', '2026-01-14 16:29:32', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (9, 'system.api.user.get.list', '系统用户管理获取用户列表接口权限', 1, 'super_admin', '2026-01-14 16:31:23', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (10, 'system.api.user.add', '系统用户管理添加用户接口权限', 1, 'super_admin', '2026-01-14 16:31:59', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (11, 'system.api.user.update', '系统用户管理更新用户接口权限', 1, 'super_admin', '2026-01-14 16:32:35', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (12, 'system.api.user.delete.id', '系统用户管理删除指定用户接口权限', 1, 'super_admin', '2026-01-14 16:33:33', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (13, 'system.api.user.delete.batch', '系统用户管理批量删除接口权限', 1, 'super_admin', '2026-01-14 16:34:05', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (14, 'system.api.user.get.roles', '系统用户管理获取用户角色接口权限', 1, 'super_admin', '2026-01-14 16:34:55', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (15, 'system.api.user.update.roles', '系统用户管理绑定角色接口权限', 1, 'super_admin', '2026-01-14 16:35:36', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (16, 'system.api.user.get.depts', '系统用户管理获取用户部门接口权限', 1, 'super_admin', '2026-01-14 16:36:31', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (17, 'system.api.user.update.depts', '系统用户管理绑定部门接口权限', 1, 'super_admin', '2026-01-14 16:36:57', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (18, 'system.api.user.get.permisson-group', '系统用户管理获取已绑定的权限组接口权限', 1, 'super_admin', '2026-01-14 16:37:56', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (19, 'system.api.user.update.permisson-group', '系统用户管理绑定权限组接口权限', 1, 'super_admin', '2026-01-14 16:38:32', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (20, 'system.api.user.get.individual-perm', '系统用户管理获取用户独立权限接口权限', 1, 'super_admin', '2026-01-14 16:40:01', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (21, 'system.api.user.add.individual-perm', '系统用户管理添加用户独立权限接口权限', 1, 'super_admin', '2026-01-14 16:40:41', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (22, 'system.api.user.delete.individual-perm', '系统用户管理删除用户独立权限接口权限', 1, 'super_admin', '2026-01-14 16:41:15', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (23, 'system.ui.role.view', '系统角色管理页面权限', 1, 'super_admin', '2026-01-14 17:03:58', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (24, 'system.ui.role.button.add', '系统角色管理添加按钮权限', 1, 'super_admin', '2026-01-14 17:04:56', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (25, 'system.ui.role.button.edit', '系统角色管理编辑按钮权限', 1, 'super_admin', '2026-01-14 17:05:43', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (26, 'system.ui.role.button.permission', '系统角色管理分配权限组按钮权限', 1, 'super_admin', '2026-01-14 17:06:05', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (27, 'system.ui.role.button.delete', '系统角色管理删除按钮权限', 1, 'super_admin', '2026-01-14 17:06:33', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (28, 'system.api.role.get.list', '系统角色管理获取角色列表接口权限', 1, 'super_admin', '2026-01-14 17:08:04', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (29, 'system.api.role.get.options', '系统角色管理获取角色下拉选项列表接口权限', 1, 'super_admin', '2026-01-14 17:08:59', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (30, 'system.api.role.get.id', '系统角色管理通过ID获取角色接口权限', 1, 'super_admin', '2026-01-14 17:10:10', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (31, 'system.api.role.add', '系统角色管理添加角色接口权限', 1, 'super_admin', '2026-01-14 17:10:36', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (32, 'system.api.role.update', '系统角色管理更新角色接口权限', 1, 'super_admin', '2026-01-14 17:11:09', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (33, 'system.api.role.delete.id', '系统角色管理删除角色接口权限', 1, 'super_admin', '2026-01-14 17:11:44', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (34, 'system.api.role.delete.batch', '系统角色管理批量删除角色接口权限', 1, 'super_admin', '2026-01-14 17:12:12', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (35, 'system.api.role.get.permission-group', '系统角色管理获取已绑定的权限组接口权限', 1, 'super_admin', '2026-01-14 17:12:54', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (36, 'system.api.role.update.permission-group', '系统角色管理绑定权限组接口权限', 1, 'super_admin', '2026-01-14 17:13:43', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (37, 'system.ui.dept.view', '系统部门管理页面权限', 1, 'super_admin', '2026-01-14 17:23:13', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (38, 'system.ui.dept.button.add', '系统部门管理添加按钮权限', 1, 'super_admin', '2026-01-14 17:24:09', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (39, 'system.ui.dept.button.edit', '系统部门管理编辑按钮权限', 1, 'super_admin', '2026-01-14 17:24:50', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (40, 'system.ui.dept.button.permission', '系统部门管理分配权限组按钮权限', 1, 'super_admin', '2026-01-14 17:25:36', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (41, 'system.ui.dept.button.delete', '系统部门管理删除按钮权限', 1, 'super_admin', '2026-01-14 17:25:57', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (42, 'system.api.dept.get.tree', '系统部门管理获取部门树列表接口权限', 1, 'super_admin', '2026-01-14 17:28:03', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (43, 'system.api.dept.get.id', '系统部门管理获取部门接口权限', 1, 'super_admin', '2026-01-14 17:28:37', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (44, 'system.api.dept.add', '系统部门管理添加部门接口权限', 1, 'super_admin', '2026-01-14 17:29:02', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (45, 'system.api.dept.update', '系统部门管理修改部门接口权限', 1, 'super_admin', '2026-01-14 17:29:35', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (46, 'system.api.dept.update.sort', '系统部门管理修改部门排序接口权限', 1, 'super_admin', '2026-01-14 17:30:05', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (47, 'system.api.dept.delete.id', '系统部门管理删除部门接口权限', 1, 'super_admin', '2026-01-14 17:30:32', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (48, 'system.api.dept.get.permission-group', '系统部门管理获取已绑定的权限组接口权限', 1, 'super_admin', '2026-01-14 17:31:16', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (49, 'system.api.dept.update.permission-group', '系统部门管理绑定权限组接口权限', 1, 'super_admin', '2026-01-14 17:31:51', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (50, 'system.ui.permission.view', '系统权限组管理页面权限', 1, 'super_admin', '2026-01-14 17:42:49', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (51, 'system.ui.permission.button.add', '系统权限组管理添加按钮权限', 1, 'super_admin', '2026-01-14 17:44:15', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (52, 'system.ui.permission.button.edit', '系统权限组管理编辑按钮权限', 1, 'super_admin', '2026-01-14 17:47:56', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (53, 'system.ui.permission.button.delete', '系统权限组管理删除按钮权限', 1, 'super_admin', '2026-01-14 17:48:26', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (54, 'system.api.permission.get.list', '系统权限组管理获取权限组列表接口权限', 1, 'super_admin', '2026-01-14 17:50:19', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (55, 'sytem.api.permission.get.options', '系统权限组管理获取权限组下拉选项列表接口权限', 1, 'super_admin', '2026-01-14 17:52:16', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (56, 'system.api.permission.add', '系统权限组管理添加权限组接口权限', 1, 'super_admin', '2026-01-14 17:52:47', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (57, 'system.api.permission.update', '系统权限组管理更新权限组接口权限', 1, 'super_admin', '2026-01-14 17:53:20', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (58, 'system.api.permission.delete.id', '系统权限组管理删除权限组接口权限', 1, 'super_admin', '2026-01-14 17:56:41', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (59, 'system.api.permission.delete.batch', '系统权限组管理批量删除权限组接口权限', 1, 'super_admin', '2026-01-14 17:57:08', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (60, 'system.ui.permission.pool.view', '系统权限池页面权限', 1, 'super_admin', '2026-01-14 18:42:53', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (61, 'system.ui.permission.pool.button.delete', '系统权限池删除按钮权限', 1, 'super_admin', '2026-01-14 18:43:46', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (62, 'system.ui.permission.pool.button.add', '系统权限池添加按钮权限', 1, 'super_admin', '2026-01-14 18:44:37', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (63, 'system.ui.permission-pool.button.status', '系统权限池状态修改控件权限', 1, 'super_admin', '2026-01-14 18:52:18', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (64, 'system.api.permission.pool.get.list', '系统权限池获取权限列表接口权限', 1, 'super_admin', '2026-01-14 18:53:49', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (65, 'system.api.permission.pool.get.options', '系统权限池获取权限下拉选项列表接口权限', 1, 'super_admin', '2026-01-14 18:54:51', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (66, 'system.api.permission.pool.add', '系统权限池添加权限接口权限', 1, 'super_admin', '2026-01-14 18:55:14', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (67, 'system.api.permission.pool.delete.id', '系统权限池删除权限接口权限', 1, 'super_admin', '2026-01-14 18:55:42', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (68, 'system.api.permission.pool.delete.batch', '系统权限池批量删除权限接口权限', 1, 'super_admin', '2026-01-14 18:56:12', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (69, 'system.api.permission.pool.update.status', '系统权限池更新权限状态接口权限', 1, 'super_admin', '2026-01-14 18:56:49', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (70, 'system.ui.menu.view', '系统菜单管理页面权限', 1, 'super_admin', '2026-01-14 19:09:21', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (71, 'system.ui.menu.button.add', '系统菜单管理添加按钮权限', 1, 'super_admin', '2026-01-14 19:09:51', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (72, 'system.ui.menu.button.edit', '系统菜单管理编辑按钮权限', 1, 'super_admin', '2026-01-14 19:22:32', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (73, 'system.ui.menu.button.delete', '系统菜单管理删除按钮权限', 1, 'super_admin', '2026-01-14 19:23:33', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (74, 'system.api.menu.get.tree', '系统菜单管理获取菜单树列表接口权限', 1, 'super_admin', '2026-01-14 19:27:07', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (75, 'system.api.menu.get.list', '系统菜单管理获取菜单列表接口权限', 1, 'super_admin', '2026-01-14 19:27:38', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (76, 'system.api.menu.get.id', '系统菜单管理获取菜单接口权限', 1, 'super_admin', '2026-01-14 19:28:16', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (77, 'system.api.menu.add', '系统菜单管理添加菜单接口权限', 1, 'super_admin', '2026-01-14 19:28:34', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (78, 'system.api.menu.update', '系统菜单管理修改菜单接口权限', 1, 'super_admin', '2026-01-14 19:29:40', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (79, 'system.api.menu.delete.id', '系统菜单管理删除菜单接口权限', 1, 'super_admin', '2026-01-14 19:30:38', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (80, 'system.api.menu.update.sort', '系统菜单管理菜单排序接口权限', 1, 'super_admin', '2026-01-14 19:32:10', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (81, 'system.ui.session.view', '系统会话监控页面权限', 1, 'super_admin', '2026-01-14 19:53:36', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (82, 'system.ui.session.button.logout', '系统会话监控会话下线按钮权限', 1, 'super_admin', '2026-01-14 19:54:35', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (83, 'system.api.session.get.list', '系统会话监控获取会话列表接口权限', 1, 'super_admin', '2026-01-14 19:55:50', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (84, 'system.api.session.logout.id', '系统会话监控下线接口权限', 1, 'super_admin', '2026-01-14 19:56:41', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (85, 'system.api.session.logout.batch', '系统会话监控批量下线接口权限', 1, 'super_admin', '2026-01-14 19:57:46', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (86, 'system.ui.cache.view', '系统缓存监控页面权限', 1, 'super_admin', '2026-01-14 20:06:05', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (87, 'system.ui.cache.button.delete', '系统缓存监控删除按钮权限', 1, 'super_admin', '2026-01-14 20:06:40', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (88, 'system.ui.cache.button.clear', '系统缓存监控清空缓存按钮权限', 1, 'super_admin', '2026-01-14 20:07:08', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (89, 'system.ui.cache.button.detail', '系统缓存监控查看详情按钮权限', 1, 'super_admin', '2026-01-14 20:08:10', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (90, 'system.api.cache.get.info', '系统缓存监控获取redis信息接口权限', 1, 'super_admin', '2026-01-14 20:09:15', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (91, 'system.api.cache.get.keys', '系统缓存监控获取keys接口权限', 1, 'super_admin', '2026-01-14 20:10:05', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (92, 'system.api.cache.get.key', '系统缓存监控获取key详情接口权限', 1, 'super_admin', '2026-01-14 20:10:33', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (93, 'system.api.cache.delete.key', '系统缓存监控删除指定缓存接口权限', 1, 'super_admin', '2026-01-14 20:11:04', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (94, 'system.api.cache.delete.batch', '系统缓存监控批量删除缓存接口权限', 1, 'super_admin', '2026-01-14 20:11:36', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (95, 'system.api.cache.clear', '系统缓存监控清空缓存接口权限', 1, 'super_admin', '2026-01-14 20:12:04', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (97, 'system.ui.auth-log.view', '系统日志管理登录日志页面权限', 1, 'super_admin', '2026-02-03 19:40:55', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (98, 'system.ui.auth-log.button.delete', '系统日志管理登录日志删除按钮权限', 1, 'super_admin', '2026-02-03 19:42:17', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (99, 'system.ui.auth-log.button.clear', '系统日志管理登录日志清除按钮权限', 1, 'super_admin', '2026-02-03 19:42:53', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (100, 'system.api.auth-log.get.list', '系统日志管理登录日志获取日志列表接口权限', 1, 'super_admin', '2026-02-03 19:45:59', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (101, 'system.api.auth-log.del', '系统日志管理登录日志删除接口权限', 1, 'super_admin', '2026-02-03 19:47:02', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (102, 'system.api.auth-log.clear', '系统日志管理登录日志清理接口权限', 1, 'super_admin', '2026-02-03 19:47:35', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (103, 'system.ui.oper-log.view', '系统日志管理操作日志页面接口权限', 1, 'super_admin', '2026-02-05 22:57:51', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (104, 'system.ui.oper-log.button.delete', '系统日志管理操作日志删除按钮权限', 1, 'super_admin', '2026-02-05 22:58:21', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (105, 'system.ui.oper-log.button.clear', '系统日志管理操作日志清空按钮权限', 1, 'super_admin', '2026-02-05 22:59:39', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (107, 'system.api.oper-log.get.list', '系统日志管理操作日志获取日志列表接口权限', 1, 'super_admin', '2026-02-06 23:57:43', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (108, 'system.api.oper-log.delete.batch', '系统日志管理操作日志批量删除接口权限', 1, 'super_admin', '2026-02-06 23:58:30', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (109, 'system.api.oper-log.clear', '系统日志管理操作日志清空日志接口权限', 1, 'super_admin', '2026-02-06 23:59:27', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (110, 'system.ui.config.view', '系统参数配置页面权限', 1, 'super_admin', '2026-02-07 00:00:12', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (111, 'system.ui.config.button.add', '系统参数配置添加参数按钮权限', 1, 'super_admin', '2026-02-07 00:00:39', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (112, 'system.ui.config.button.delete', '系统参数配置删除参数按钮权限', 1, 'super_admin', '2026-02-07 00:01:06', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (113, 'system.ui.config.button.edit', '系统参数配置修改按钮权限', 1, 'super_admin', '2026-02-07 00:01:38', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (114, 'system.api.config.get.list', '系统参数配置获取列表接口权限', 1, 'super_admin', '2026-02-07 00:04:20', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (115, 'system.api.config.get.id', '系统参数配置获取参数详情接口权限', 1, 'super_admin', '2026-02-07 00:04:44', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (116, 'system.api.config.get.key', '系统参数配置根据Key获取参数值接口权限', 1, 'super_admin', '2026-02-07 00:05:13', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (117, 'system.api.config.add', '系统参数配置添加参数接口权限', 1, 'super_admin', '2026-02-07 00:05:34', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (118, 'system.api.config.update', '系统参数配置更新参数接口权限', 1, 'super_admin', '2026-02-07 00:05:56', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (119, 'system.api.config.delete.id', '系统参数配置删除参数接口权限', 1, 'super_admin', '2026-02-07 00:06:15', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (120, 'system.api.config.delete.batch', '系统参数配置批量删除参数接口权限', 1, 'super_admin', '2026-02-07 00:06:42', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (121, 'system.api.config.refresh', '系统参数配置刷新参数缓存接口权限', 1, 'super_admin', '2026-02-07 00:07:08', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (122, 'system.ui.dict.view', '系统字典管理页面权限', 1, 'super_admin', '2026-02-11 17:50:44', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (123, 'system.ui.dict.button.add', '系统字典管理添加按钮权限', 1, 'super_admin', '2026-02-11 17:51:12', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (124, 'system.ui.dict.button.edit', '系统字典管理编辑按钮权限', 1, 'super_admin', '2026-02-11 17:51:45', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (125, 'system.ui.dict.button.delete', '系统字典管理删除按钮权限', 1, 'super_admin', '2026-02-11 17:52:30', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (126, 'system.api.dict.get.list', '系统字典管理获取列表接口权限', 1, 'super_admin', '2026-02-11 17:59:47', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (127, 'system.api.dict.get.all', '系统字典管理获取全部类型接口权限', 1, 'super_admin', '2026-02-11 18:02:21', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (128, 'system.api.dict.get.id', '系统字典管理获取详情接口权限', 1, 'super_admin', '2026-02-11 18:02:46', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (129, 'system.api.dict.add.type', '系统字典管理添加类型接口权限', 1, 'super_admin', '2026-02-11 18:03:15', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (130, 'system.api.dict.update.type', '系统字典管理更新类型接口权限', 1, 'super_admin', '2026-02-11 18:03:43', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (131, 'system.api.dict.delete.type.id', '系统字典管理删除类型接口权限', 1, 'super_admin', '2026-02-11 18:04:10', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (132, 'system.api.dict.delete.type.batch', '系统字典管理批量删除类型接口权限', 1, 'super_admin', '2026-02-11 18:05:04', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (133, 'system.api.dict.refresh.type', '系统字典管理刷新指定类型缓存接口权限', 1, 'super_admin', '2026-02-11 18:05:52', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (134, 'system.api.dict.refresh.all', '系统字典管理刷新所有类型缓存接口权限', 1, 'super_admin', '2026-02-11 18:06:21', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (135, 'system.api.dict.add.data', '系统字典管理添加字典数据接口权限', 1, 'super_admin', '2026-02-11 18:08:44', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (136, 'system.api.dict.update.data', '系统字典管理修改字典数据接口权限', 1, 'super_admin', '2026-02-11 18:09:11', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (137, 'system.api.dict.delete.data.code', '系统字典管理删除字典数据接口权限', 1, 'super_admin', '2026-02-11 18:09:54', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (138, 'system.api.dict.delete.code.batch', '系统字典管理批量删除字典数据接口权限', 1, 'super_admin', '2026-02-11 18:10:27', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (139, 'system.ui.info.view', '系统信息页面权限', 1, 'super_admin', '2026-02-11 21:56:35', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (140, 'project.ui.list.view', '项目协作项目管理页面权限', 1, 'super_admin', '2026-05-16 20:38:48', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (141, 'project.ui.list.add.button', '项目协作添加项目权限', 1, 'super_admin', '2026-05-16 20:39:03', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (142, 'project.ui.list.edit.button', '项目协作项目管理修改项目权限', 1, 'super_admin', '2026-05-16 20:39:53', NULL, NULL, NULL);
INSERT INTO `sys_permission` (`id`, `permission`, `description`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (143, 'project.ui.list.delete.button', '项目协作项目管理删除项目权限', 1, 'super_admin', '2026-05-16 20:40:11', NULL, NULL, NULL);
COMMIT;

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '角色id',
  `role_name` varchar(30) DEFAULT NULL COMMENT '角色名称',
  `role_key` varchar(30) DEFAULT NULL COMMENT '角色关键词',
  `sort` int DEFAULT NULL COMMENT '排序',
  `data_scope` char(1) DEFAULT NULL COMMENT '数据显示范围',
  `status` int DEFAULT '1' COMMENT '状态（1正常，0禁用）',
  `del_flag` tinyint DEFAULT '0' COMMENT '软删除标识符（0正常，1删除）',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` text COMMENT '备注',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='系统角色表';

-- ----------------------------
-- Records of sys_role
-- ----------------------------
BEGIN;
INSERT INTO `sys_role` (`id`, `role_name`, `role_key`, `sort`, `data_scope`, `status`, `del_flag`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (1, '超级管理员', 'super_admin', 1, '1', 1, 0, 'system', '2025-12-05 08:25:15', 'super_admin', '2026-02-11 19:28:57', '超级管理员');
INSERT INTO `sys_role` (`id`, `role_name`, `role_key`, `sort`, `data_scope`, `status`, `del_flag`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (2, '管理员', 'admin', 2, '4', 1, 0, 'super_admin', '2025-12-05 08:26:57', 'super_admin', '2026-01-13 17:56:31', '管理员');
INSERT INTO `sys_role` (`id`, `role_name`, `role_key`, `sort`, `data_scope`, `status`, `del_flag`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (3, '测试角色', 'test_role', 3, '1', 1, 1, 'super_admin', '2026-01-01 16:21:46', 'super_admin', '2026-01-01 16:33:17', '测试用');
INSERT INTO `sys_role` (`id`, `role_name`, `role_key`, `sort`, `data_scope`, `status`, `del_flag`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (4, '测试角色1', 'test_role1', 1, '1', 1, 1, 'super_admin', '2026-01-01 16:58:32', NULL, NULL, '测试1');
INSERT INTO `sys_role` (`id`, `role_name`, `role_key`, `sort`, `data_scope`, `status`, `del_flag`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (5, '测试2', 'test2', 1, '1', 1, 1, 'super_admin', '2026-01-01 16:58:46', NULL, NULL, 'test2');
INSERT INTO `sys_role` (`id`, `role_name`, `role_key`, `sort`, `data_scope`, `status`, `del_flag`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (6, '普通用户', 'common', 3, '4', 1, 0, 'super_admin', '2026-01-03 03:44:07', 'super_admin', '2026-01-05 16:19:15', '普通用户');
COMMIT;

-- ----------------------------
-- Table structure for sys_role_group
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_group`;
CREATE TABLE `sys_role_group` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `role_id` bigint DEFAULT NULL COMMENT '角色ID',
  `group_id` bigint DEFAULT NULL COMMENT '权限组ID',
  `expire_time` datetime DEFAULT NULL COMMENT '过期时间（临时权限组使用）',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='角色-权限组关联表';

-- ----------------------------
-- Records of sys_role_group
-- ----------------------------
BEGIN;
INSERT INTO `sys_role_group` (`id`, `role_id`, `group_id`, `expire_time`, `create_time`) VALUES (5, 2, 2, NULL, '2026-03-01 15:50:06');
COMMIT;

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
  `user_id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `user_name` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户账号',
  `nick_name` varchar(100) DEFAULT NULL COMMENT '用户昵称',
  `avatar` varchar(255) DEFAULT NULL COMMENT '头像',
  `email` varchar(50) DEFAULT NULL COMMENT '邮箱',
  `phonenumber` varchar(11) DEFAULT NULL COMMENT '手机号',
  `sex` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '0' COMMENT '性别(0 未设定， 1男，2女）',
  `password` varchar(255) DEFAULT NULL COMMENT '密码（加密后）',
  `salt` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '加密盐',
  `status` int DEFAULT '1' COMMENT '状态（1正常，0停用）',
  `del_flag` tinyint DEFAULT '0' COMMENT '软删除标识符号（0未删除，1删除）',
  `pwd_update_time` datetime DEFAULT NULL COMMENT '密码修改时间',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` text COMMENT '备注',
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `uk_user_name` (`user_name`,`del_flag`) COMMENT '用户名唯一索引（配合软删除）',
  UNIQUE KEY `uk_email` (`email`,`del_flag`) COMMENT '邮箱唯一索引（配合软删除）',
  UNIQUE KEY `uk_phonenumber` (`phonenumber`,`del_flag`) COMMENT '手机号唯一索引（配合软删除）'
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户表';

-- ----------------------------
-- Records of sys_user
-- ----------------------------
BEGIN;
INSERT INTO `sys_user` (`user_id`, `user_name`, `nick_name`, `avatar`, `email`, `phonenumber`, `sex`, `password`, `salt`, `status`, `del_flag`, `pwd_update_time`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (1, 'super_admin', '超级管理员', NULL, 'exmple@junoyi.com', '18899887871', '1', 'm/ctuGNjUwrpOxdqrd2fQsfVN1Mnbu6EKwJWXN+P3W4=', '3dvSoCjGtCXZnSB+6ENWtQ==', 1, 0, NULL, 'system', '2025-12-05 08:13:00', 'super_admin', '2026-02-11 19:18:12', '');
INSERT INTO `sys_user` (`user_id`, `user_name`, `nick_name`, `avatar`, `email`, `phonenumber`, `sex`, `password`, `salt`, `status`, `del_flag`, `pwd_update_time`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (2, 'admin', '用户管理员', NULL, 'admin@junoyi.com', '18899887872', '1', 'm/ctuGNjUwrpOxdqrd2fQsfVN1Mnbu6EKwJWXN+P3W4=', '3dvSoCjGtCXZnSB+6ENWtQ==', 1, 0, NULL, 'super_admin', '2025-12-26 08:22:32', 'super_admin', '2026-01-02 22:08:40', '');
INSERT INTO `sys_user` (`user_id`, `user_name`, `nick_name`, `avatar`, `email`, `phonenumber`, `sex`, `password`, `salt`, `status`, `del_flag`, `pwd_update_time`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (3, 'user1', '钧逸用户1', NULL, 'user1@junoyi.com', '18899887873', '1', 'm/ctuGNjUwrpOxdqrd2fQsfVN1Mnbu6EKwJWXN+P3W4=', '3dvSoCjGtCXZnSB+6ENWtQ==', 1, 0, NULL, 'admin', '2025-12-26 09:02:10', 'admin', '2025-12-26 09:02:15', '钧逸用户');
INSERT INTO `sys_user` (`user_id`, `user_name`, `nick_name`, `avatar`, `email`, `phonenumber`, `sex`, `password`, `salt`, `status`, `del_flag`, `pwd_update_time`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (4, 'user2', '钧逸用户2', NULL, 'user2@junoyi.com', '18822334459', '2', '2QEBgS3NRYr0BK1IDLbJBeu7N+4a/Dmqt+uk/OfLZKE=', '+/gh5ppNj92gnzr7nK4HpQ==', 1, 0, NULL, 'super_admin', '2026-01-02 20:54:39', 'super_admin', '2026-02-11 16:48:15', '');
INSERT INTO `sys_user` (`user_id`, `user_name`, `nick_name`, `avatar`, `email`, `phonenumber`, `sex`, `password`, `salt`, `status`, `del_flag`, `pwd_update_time`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (5, 'test1', '测试用户1', NULL, 'test1@junoyi.com', '18866776675', '0', '3ljua1Xq5gFzwIZvOnZFzM0z0q03DsoHCjSyjZ9CUm0=', 'bickL1fDbw3dlFzWEOVahw==', 0, 1, NULL, 'super_admin', '2026-01-03 00:08:38', 'super_admin', '2026-01-03 00:11:02', '测试1');
INSERT INTO `sys_user` (`user_id`, `user_name`, `nick_name`, `avatar`, `email`, `phonenumber`, `sex`, `password`, `salt`, `status`, `del_flag`, `pwd_update_time`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (6, 'test2', '测试用户2', NULL, 'test2@junoyi.com', '19988776676', '1', '4E2Iup83NXU8WbQaV9VKxzqFt66YkhKix1EkCGLR+Gs=', 'b6+tSxzDuUH4iVzpKuelxg==', 0, 1, NULL, 'super_admin', '2026-01-03 00:09:10', 'super_admin', '2026-01-03 00:11:09', '测试2');
INSERT INTO `sys_user` (`user_id`, `user_name`, `nick_name`, `avatar`, `email`, `phonenumber`, `sex`, `password`, `salt`, `status`, `del_flag`, `pwd_update_time`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (7, 'test3', '测试用户3', NULL, 'test3@junoyi.com', '18877887676', '0', 'v16QwJwNASg+2zXHOZsD2jo8UewWi0A6xfj9+3TCbgg=', 'Pa8+xWPBWZRbjbn/1Vw7pg==', 0, 1, NULL, 'super_admin', '2026-01-03 00:09:48', 'super_admin', '2026-01-03 00:11:09', '测试用户3');
COMMIT;

-- ----------------------------
-- Table structure for sys_user_dept
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_dept`;
CREATE TABLE `sys_user_dept` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` bigint DEFAULT NULL COMMENT '用户ID',
  `dept_id` bigint DEFAULT NULL COMMENT '部门ID',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户-部门关联表';

-- ----------------------------
-- Records of sys_user_dept
-- ----------------------------
BEGIN;
INSERT INTO `sys_user_dept` (`id`, `user_id`, `dept_id`) VALUES (3, 1, 4);
INSERT INTO `sys_user_dept` (`id`, `user_id`, `dept_id`) VALUES (4, 1, 5);
INSERT INTO `sys_user_dept` (`id`, `user_id`, `dept_id`) VALUES (5, 1, 6);
INSERT INTO `sys_user_dept` (`id`, `user_id`, `dept_id`) VALUES (7, 3, 6);
INSERT INTO `sys_user_dept` (`id`, `user_id`, `dept_id`) VALUES (8, 4, 6);
COMMIT;

-- ----------------------------
-- Table structure for sys_user_group
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_group`;
CREATE TABLE `sys_user_group` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `group_id` bigint NOT NULL COMMENT '权限组ID',
  `expire_time` datetime DEFAULT NULL COMMENT '过期时间（支持临时授权）',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_group` (`user_id`,`group_id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户-权限组关联表';

-- ----------------------------
-- Records of sys_user_group
-- ----------------------------
BEGIN;
INSERT INTO `sys_user_group` (`id`, `user_id`, `group_id`, `expire_time`, `create_time`) VALUES (2, 3, 1, NULL, '2025-12-29 21:05:28');
COMMIT;

-- ----------------------------
-- Table structure for sys_user_perm
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_perm`;
CREATE TABLE `sys_user_perm` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `permission` varchar(200) NOT NULL COMMENT '权限节点',
  `expire_time` datetime DEFAULT NULL COMMENT '过期时间',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_perm` (`user_id`,`permission`)
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户独立权限表';

-- ----------------------------
-- Records of sys_user_perm
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for sys_user_platform
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_platform`;
CREATE TABLE `sys_user_platform` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `platform_type` int DEFAULT NULL COMMENT '登录终端平台类型（0后台Web，1前台Web，2小程序，3APP，4桌面端）',
  `platform_uid` varchar(255) DEFAULT NULL COMMENT '平台唯一标识符',
  `login_ip` varchar(128) DEFAULT NULL COMMENT '登录IP',
  `login_time` datetime DEFAULT NULL COMMENT '登录时间',
  `token` varchar(255) DEFAULT NULL COMMENT '登录使用到的token',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户登录终端信息表';

-- ----------------------------
-- Records of sys_user_platform
-- ----------------------------
BEGIN;
INSERT INTO `sys_user_platform` (`id`, `user_id`, `platform_type`, `platform_uid`, `login_ip`, `login_time`, `token`) VALUES (1, 1, 0, 'admin_web', '127.0.0.1', '2025-12-18 21:19:35', 'fdafdasfdasfdafdsafdsa');
COMMIT;

-- ----------------------------
-- Table structure for sys_user_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户角色关联id',
  `user_id` bigint DEFAULT NULL COMMENT '用户id',
  `role_id` bigint DEFAULT NULL COMMENT '角色id',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=34 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户角色数据关联表';

-- ----------------------------
-- Records of sys_user_role
-- ----------------------------
BEGIN;
INSERT INTO `sys_user_role` (`id`, `user_id`, `role_id`) VALUES (5, 3, 6);
INSERT INTO `sys_user_role` (`id`, `user_id`, `role_id`) VALUES (6, 4, 6);
INSERT INTO `sys_user_role` (`id`, `user_id`, `role_id`) VALUES (27, 1, 1);
INSERT INTO `sys_user_role` (`id`, `user_id`, `role_id`) VALUES (33, 2, 2);
COMMIT;

-- ----------------------------
-- Table structure for sys_user_third_auth
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_third_auth`;
CREATE TABLE `sys_user_third_auth` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint DEFAULT NULL COMMENT '用户ID',
  `auth_type` varchar(20) DEFAULT NULL COMMENT '登录类型',
  `auth_key` varchar(255) DEFAULT NULL COMMENT '平台唯一标识符',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户第三登录绑定表';

-- ----------------------------
-- Records of sys_user_third_auth
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for task
-- ----------------------------
DROP TABLE IF EXISTS `task`;
CREATE TABLE `task` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '任务主键ID',
  `title` varchar(255) DEFAULT NULL COMMENT '任务标题',
  `description` text COMMENT '任务描述',
  `status` int DEFAULT '0' COMMENT '任务状态',
  `priority` int DEFAULT '1' COMMENT '优先级',
  `type` int DEFAULT '0' COMMENT '任务类型',
  `project_id` bigint DEFAULT NULL COMMENT '所属项目ID(如果无视为普通任务，有视为项目任务）',
  `biz_id` bigint DEFAULT NULL COMMENT '关联业务ID（如订单/客户等）',
  `start_time` datetime DEFAULT NULL COMMENT '计划开始时间',
  `due_time` datetime DEFAULT NULL COMMENT '截止时间',
  `finish_time` datetime DEFAULT NULL COMMENT '实际完成时间',
  `wecom_schedule_id` varchar(64) DEFAULT NULL COMMENT '企业微信日历ID',
  `creator_id` bigint DEFAULT NULL COMMENT '创建者',
  `del_flag` tinyint DEFAULT '0' COMMENT '软删除标识',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT NULL,
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=77 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='任务主表';

-- ----------------------------
-- Records of task
-- ----------------------------
BEGIN;
INSERT INTO `task` (`id`, `title`, `description`, `status`, `priority`, `type`, `project_id`, `biz_id`, `start_time`, `due_time`, `finish_time`, `wecom_schedule_id`, `creator_id`, `del_flag`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (76, '测试任务', '这是一条测试任务', 0, 1, 0, NULL, NULL, NULL, '2026-05-24 00:00:00', NULL, NULL, 1, 0, 'super_admin', '2026-05-10 20:54:05', 'super_admin', '2026-05-16 16:55:44', '');
COMMIT;

-- ----------------------------
-- Table structure for task_attachment
-- ----------------------------
DROP TABLE IF EXISTS `task_attachment`;
CREATE TABLE `task_attachment` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `task_id` bigint DEFAULT NULL COMMENT '任务ID',
  `record_id` bigint DEFAULT NULL COMMENT '任务记录ID',
  `file_name` varchar(255) DEFAULT NULL COMMENT '文件名',
  `file_url` varchar(255) DEFAULT NULL COMMENT '文件路径',
  `upload_user` bigint DEFAULT NULL COMMENT '上传的用户',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='任务附件表';

-- ----------------------------
-- Records of task_attachment
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for task_record
-- ----------------------------
DROP TABLE IF EXISTS `task_record`;
CREATE TABLE `task_record` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `task_id` bigint DEFAULT NULL COMMENT '任务ID',
  `operator_id` bigint DEFAULT NULL COMMENT '操作者ID',
  `action_type` int DEFAULT '1' COMMENT '任务操作类型',
  `remark` text COMMENT '完成说明 / 打回原因',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=137 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='任务操作记录表';

-- ----------------------------
-- Records of task_record
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for task_user
-- ----------------------------
DROP TABLE IF EXISTS `task_user`;
CREATE TABLE `task_user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `task_id` bigint DEFAULT NULL COMMENT '任务ID',
  `user_id` bigint DEFAULT NULL COMMENT '用户ID',
  `task_role` int DEFAULT '1' COMMENT '任务角色',
  `create_time` datetime DEFAULT NULL COMMENT '任务安排时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=122 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='任务用户关联表';

-- ----------------------------
-- Records of task_user
-- ----------------------------
BEGIN;
INSERT INTO `task_user` (`id`, `task_id`, `user_id`, `task_role`, `create_time`) VALUES (121, 76, 3, 2, '2026-05-16 16:55:44');
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
