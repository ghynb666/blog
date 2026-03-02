# Markdown 博客系统 Spec

## Why
构建一个功能完整的 Markdown 博客系统，支持文章管理、前台展示、用户认证和访问统计等核心功能。

## What Changes
- 创建 Spring Boot 后端项目，实现 RESTful API
- 创建 Vue3 前端项目，实现管理后台和前台展示
- 集成 MySQL 数据库和 MinIO 对象存储
- 实现 JWT 认证机制
- 实现 Markdown 编辑器和渲染功能

## Impact
- 新增后端项目结构：controller、service、mapper、entity、config 等
- 新增前端项目结构：views、components、api、store、router 等
- 新增数据库表：用户、文章、标签、分类、访问记录等

## ADDED Requirements

### Requirement: 用户认证
系统 SHALL 提供基于 JWT 的用户认证功能。

#### Scenario: 用户登录
- **WHEN** 用户提交正确的用户名和密码
- **THEN** 系统返回有效的 JWT token

#### Scenario: 访问保护
- **WHEN** 未认证用户访问后台管理接口
- **THEN** 系统返回 401 未授权错误

### Requirement: 文章管理
系统 SHALL 提供完整的文章 CRUD 功能。

#### Scenario: 创建文章
- **WHEN** 用户提交文章内容
- **THEN** 系统保存文章并返回文章 ID

#### Scenario: 发布文章
- **WHEN** 用户发布草稿文章
- **THEN** 文章状态变更为已发布，前台可见

### Requirement: Markdown 编辑器
系统 SHALL 提供功能完善的 Markdown 编辑器。

#### Scenario: 实时预览
- **WHEN** 用户在编辑器中输入 Markdown 内容
- **THEN** 预览区实时显示渲染后的 HTML

#### Scenario: 图片上传
- **WHEN** 用户拖拽或粘贴图片到编辑器
- **THEN** 图片上传至 MinIO 并插入 Markdown 链接

### Requirement: 前台展示
系统 SHALL 提供文章前台展示功能。

#### Scenario: 文章列表
- **WHEN** 用户访问首页
- **THEN** 显示分页的文章列表

#### Scenario: 文章详情
- **WHEN** 用户点击文章
- **THEN** 显示文章详情页，包含代码高亮和目录导航

### Requirement: 访问统计
系统 SHALL 提供文章访问量统计功能。

#### Scenario: 阅读量统计
- **WHEN** 用户访问文章详情页
- **THEN** 文章阅读量增加 1（带防刷机制）
