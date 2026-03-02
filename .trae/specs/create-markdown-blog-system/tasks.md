# Tasks

- [x] Task 1: 数据库设计与初始化
  - [x] 1.1 创建数据库和表结构（用户表、文章表、标签表、分类表、文章标签关联表、访问记录表）
  - [x] 1.2 编写初始化 SQL 脚本

- [x] Task 2: 后端项目搭建
  - [x] 2.1 创建 Spring Boot 项目，配置 Maven 依赖
  - [x] 2.2 配置 MySQL 数据源和 MyBatis-Plus
  - [x] 2.3 配置 MinIO 客户端
  - [x] 2.4 配置 JWT 工具类和拦截器

- [x] Task 3: 用户认证模块
  - [x] 3.1 创建用户实体和 Mapper
  - [x] 3.2 实现登录接口，返回 JWT token
  - [x] 3.3 实现认证拦截器，保护后台接口

- [x] Task 4: 文章管理模块
  - [x] 4.1 创建文章、标签、分类实体和 Mapper
  - [x] 4.2 实现文章 CRUD 接口
  - [x] 4.3 实现标签和分类管理接口
  - [x] 4.4 实现图片上传接口（MinIO）

- [x] Task 5: 前台展示模块
  - [x] 5.1 实现文章列表接口（分页、搜索、排序）
  - [x] 5.2 实现文章详情接口
  - [x] 5.3 实现标签云接口
  - [x] 5.4 实现归档时间线接口

- [x] Task 6: 访问统计模块
  - [x] 6.1 实现访问记录实体和 Mapper
  - [x] 6.2 实现阅读量统计接口
  - [x] 6.3 实现防刷机制（IP + 时间窗口）

- [x] Task 7: 前端项目搭建
  - [x] 7.1 创建 Vue3 + Vite 项目
  - [x] 7.2 配置路由和状态管理
  - [x] 7.3 配置 Axios 请求封装和 JWT 拦截

- [x] Task 8: 前端管理后台
  - [x] 8.1 实现登录页面
  - [x] 8.2 实现 Markdown 编辑器组件（实时预览、工具栏、图片上传）
  - [x] 8.3 实现文章管理页面（列表、编辑、发布）
  - [x] 8.4 实现标签和分类管理页面

- [x] Task 9: 前端前台展示
  - [x] 9.1 实现首页文章列表
  - [x] 9.2 实现文章详情页（代码高亮、目录导航）
  - [x] 9.3 实现标签云组件
  - [x] 9.4 实现归档时间线页面

- [x] Task 10: 集成测试与优化
  - [x] 10.1 后端单元测试
  - [x] 10.2 前后端联调
  - [x] 10.3 性能优化

# Task Dependencies
- Task 2 depends on Task 1
- Task 3 depends on Task 2
- Task 4 depends on Task 2
- Task 5 depends on Task 4
- Task 6 depends on Task 2
- Task 7 可并行
- Task 8 depends on Task 7, Task 3, Task 4
- Task 9 depends on Task 7, Task 5
- Task 10 depends on Task 8, Task 9
