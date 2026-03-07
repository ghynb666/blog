# 登录注册安全优化任务清单

## 后端任务

### 1. 依赖配置
- [ ] 1.1 pom.xml 添加 Sa-Token 依赖
- [ ] 1.2 pom.xml 添加 Sa-Token Redis 依赖
- [ ] 1.3 pom.xml 添加 Redis 连接池依赖
- [ ] 1.4 pom.xml 删除 jjwt 依赖
- [ ] 1.5 application.yml 添加 Redis 配置
- [ ] 1.6 application.yml 添加 Sa-Token 配置

### 2. Redis 配置
- [ ] 2.1 创建 RedisConfig.java
- [ ] 2.2 配置 RedisConnectionFactory
- [ ] 2.3 验证 Redis 连接

### 3. 工具类开发
- [ ] 3.1 创建 RsaUtil.java
  - [ ] 3.1.1 实现密钥对生成（2048位）
  - [ ] 3.1.2 实现 getPublicKey() 方法
  - [ ] 3.1.3 实现 decrypt() 方法
  - [ ] 3.1.4 添加异常处理
- [ ] 3.2 创建 PasswordUtil.java
  - [ ] 3.2.1 实现 hash() 方法
  - [ ] 3.2.2 实现 check() 方法
  - [ ] 3.2.3 实现 isBCrypt() 方法

### 4. Sa-Token 配置
- [ ] 4.1 创建 SaTokenConfig.java
- [ ] 4.2 配置拦截器
- [ ] 4.3 排除登录、公钥、注册接口

### 5. 控制器改造
- [ ] 5.1 AuthController.java
  - [ ] 5.1.1 新增 getPublicKey() 接口
  - [ ] 5.1.2 改造 login() 接口，添加 RSA 解密
  - [ ] 5.1.3 改造 logout() 接口
  - [ ] 5.1.4 新增 register() 接口（如需）

### 6. 服务层改造
- [ ] 6.1 UserServiceImpl.java
  - [ ] 6.1.1 改造 login() 方法，使用 StpUtil.login()
  - [ ] 6.1.2 实现密码迁移逻辑（MD5 → BCrypt）
  - [ ] 6.1.3 改造用户创建逻辑

### 7. 拦截器改造
- [ ] 7.1 删除 AuthInterceptor.java
- [ ] 7.2 删除 AuthAspect.java
- [ ] 7.3 删除 RequireAuth.java
- [ ] 7.4 WebConfig.java 改用 Sa-Token 拦截器

## 前端任务

### 8. 依赖安装
- [ ] 8.1 npm install jsencrypt

### 9. 工具函数开发
- [ ] 9.1 创建 src/utils/rsa.js
  - [ ] 9.1.1 实现 getPublicKey() 函数
  - [ ] 9.1.2 实现 encryptPassword() 函数
  - [ ] 9.1.3 添加公钥缓存逻辑

### 10. API 接口
- [ ] 10.1 src/api/auth.js 添加 getPublicKey 接口

### 11. 组件改造
- [ ] 11.1 Login.vue
  - [ ] 11.1.1 导入 RSA 加密工具
  - [ ] 11.1.2 改造 handleLogin() 方法
- [ ] 11.2 User.vue（如有修改密码功能）
  - [ ] 11.2.1 修改密码时加密传输

### 12. 请求拦截器
- [ ] 12.1 src/utils/request.js 确认 Token 传递方式

## 清理任务

### 13. 删除废弃代码
- [ ] 13.1 删除 JwtUtil.java
- [ ] 13.2 删除 AuthInterceptor.java
- [ ] 13.3 删除 AuthAspect.java
- [ ] 13.4 删除 RequireAuth.java
- [ ] 13.5 清理 pom.xml 中 jjwt 依赖

## 测试任务

### 14. 单元测试
- [ ] 14.1 RsaUtil 测试（加解密）
- [ ] 14.2 PasswordUtil 测试（哈希验证）

### 15. 集成测试
- [ ] 15.1 登录流程测试
- [ ] 15.2 注册流程测试（如有）
- [ ] 15.3 旧密码迁移测试
- [ ] 15.4 Token 存入 Redis 验证
- [ ] 15.5 服务重启后 Token 验证
- [ ] 15.6 登出测试
