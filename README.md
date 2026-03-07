# Blog

一个基于 Spring Boot + Vue 3 的博客系统。

## 技术栈

### 后端 (blog-server)
- Spring Boot 2.7.18
- MyBatis Plus 3.5.5
- MySQL 8.0
- MinIO (文件存储)
- JWT 认证

### 前端 (blog-web)
- Vue 3.4
- Vite 5
- Element Plus 2.4
- Pinia (状态管理)
- Vue Router 4
- Marked (Markdown 渲染)
- Highlight.js (代码高亮)

## 功能

### 前台
- 文章列表/详情
- 分类/标签筛选
- 归档
- 目录导航

### 后台
- 文章管理 (支持 Markdown)
- 分类管理
- 标签管理
- 文件上传
- 访问统计

## 启动

### 后端
```bash
cd blog-server
mvn spring-boot:run
```

### 前端
```bash
cd blog-web
npm install
npm run dev
```

## 目录结构

```
Blog/
├── blog-server/          # 后端服务
│   └── src/main/java/com/blog/
│       ├── controller/   # 控制器
│       ├── service/      # 服务层
│       ├── mapper/       # 数据访问
│       ├── entity/       # 实体类
│       ├── dto/          # 数据传输对象
│       ├── vo/           # 视图对象
│       ├── config/       # 配置类
│       └── util/         # 工具类
├── blog-web/             # 前端项目
│   └── src/
│       ├── views/        # 页面
│       ├── components/   # 组件
│       ├── api/          # API 接口
│       ├── router/       # 路由
│       └── store/        # 状态管理
```
