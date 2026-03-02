# Markdown 博客系统开发计划

## 项目概述
构建一个功能完整的 Markdown 博客系统，采用 Spring Boot + Vue3 + MySQL + MinIO 技术栈。

## 技术栈
- 后端：Spring Boot 2.7.x + MyBatis-Plus
- 前端：Vue3 + Vite + Composition API
- 数据库：MySQL 8.0
- 对象存储：MinIO
- 认证：JWT

## 开发阶段

### 阶段一：基础设施（Task 1-2）

#### 1. 数据库设计
- 创建数据库 `blog`
- 用户表 `user`：id, username, password, nickname, avatar, created_at
- 分类表 `category`：id, name, sort, created_at
- 标签表 `tag`：id, name, created_at
- 文章表 `article`：id, title, summary, content, cover, status, view_count, category_id, created_at, updated_at
- 文章标签关联表 `article_tag`：article_id, tag_id
- 访问记录表 `visit_log`：id, article_id, ip, user_agent, created_at

#### 2. 后端项目搭建
- 创建 Spring Boot 项目
- 配置 Maven 依赖：spring-boot-starter-web, mybatis-plus-boot-starter, mysql-connector-java, minio, jjwt
- 配置 application.yml：数据源、MinIO、JWT 密钥
- 配置 MyBatis-Plus 分页插件
- 配置 MinIO Client Bean
- 实现 JWT 工具类：生成、解析、验证 token
- 实现认证拦截器

### 阶段二：后端核心模块（Task 3-6）

#### 3. 用户认证模块
- UserController：POST /api/admin/login
- UserService：验证用户名密码，生成 token
- 认证拦截器：拦截 /api/admin/** 路径

#### 4. 文章管理模块
- ArticleController：CRUD 接口
  - POST /api/admin/articles
  - PUT /api/admin/articles/{id}
  - DELETE /api/admin/articles/{id}
  - GET /api/admin/articles
- CategoryController：分类管理接口
- TagController：标签管理接口
- FileController：图片上传接口 POST /api/admin/upload

#### 5. 前台展示模块
- ArticleController（公开接口）：
  - GET /api/articles：分页列表，支持搜索、排序
  - GET /api/articles/{id}：文章详情
  - GET /api/categories：分类列表
  - GET /api/tags：标签列表
  - GET /api/archives：归档时间线

#### 6. 访问统计模块
- VisitLogService：记录访问日志
- 防刷机制：同一 IP 对同一文章 24 小时内只计一次
- ArticleService：阅读量 +1

### 阶段三：前端项目（Task 7-9）

#### 7. 前端项目搭建
- 创建 Vue3 + Vite 项目
- 安装依赖：vue-router, pinia, axios, marked, highlight.js, element-plus
- 配置路由：前台路由 + 管理后台路由
- 配置 Pinia 状态管理
- 封装 Axios：请求拦截器添加 token，响应拦截器处理错误

#### 8. 管理后台
- 登录页：/admin/login
- 文章管理页：/admin/articles
  - 文章列表：表格展示，支持搜索、筛选
  - 文章编辑页：Markdown 编辑器
- 分类管理页：/admin/categories
- 标签管理页：/admin/tags
- Markdown 编辑器组件：
  - 左右分栏：编辑区 + 预览区
  - 工具栏：加粗、斜体、标题、链接、图片、代码块
  - 图片上传：拖拽、粘贴、点击上传

#### 9. 前台展示
- 首页：/ 文章列表，分页展示
- 文章详情：/article/:id
  - Markdown 渲染
  - 代码高亮
  - 目录导航
- 标签云：侧边栏组件
- 归档页：/archives 时间线展示

### 阶段四：测试与优化（Task 10）

#### 10. 集成测试与优化
- 后端单元测试：Service 层测试
- 前后端联调
- 性能优化：
  - 数据库索引优化
  - 接口响应缓存
  - 前端资源压缩

## 项目结构

### 后端结构
```
blog-server/
├── src/main/java/com/blog/
│   ├── controller/
│   │   ├── admin/          # 后台管理接口
│   │   └── front/          # 前台公开接口
│   ├── service/
│   ├── mapper/
│   ├── entity/
│   ├── dto/
│   ├── vo/
│   ├── config/             # 配置类
│   ├── interceptor/        # 拦截器
│   └── util/               # 工具类
└── src/main/resources/
    └── application.yml
```

### 前端结构
```
blog-web/
├── src/
│   ├── views/
│   │   ├── admin/          # 管理后台页面
│   │   └── front/          # 前台页面
│   ├── components/         # 公共组件
│   ├── api/                # 接口封装
│   ├── store/              # Pinia 状态
│   ├── router/             # 路由配置
│   └── utils/              # 工具函数
└── vite.config.js
```

## 执行顺序
1. Task 1 → Task 2（串行）
2. Task 3, Task 4, Task 6 可并行（依赖 Task 2）
3. Task 5（依赖 Task 4）
4. Task 7 可与 Task 3-6 并行
5. Task 8（依赖 Task 7, Task 3, Task 4）
6. Task 9（依赖 Task 7, Task 5）
7. Task 10（依赖 Task 8, Task 9）
