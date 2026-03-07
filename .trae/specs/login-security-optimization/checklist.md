# 登录注册安全优化检查清单

## 代码审查

### 后端
- [ ] Sa-Token 依赖正确引入
- [ ] Sa-Token Redis 依赖正确引入
- [ ] Redis 连接配置正确
- [ ] RsaUtil 密钥对安全生成（2048位）
- [ ] RsaUtil 解密异常处理完善
- [ ] PasswordUtil 正确使用 SaSecureUtil.bCrypt()
- [ ] AuthController 公钥接口无权限限制
- [ ] UserServiceImpl 密码迁移逻辑正确
- [ ] StpUtil.login() 正确调用
- [ ] Sa-Token 拦截器配置正确
- [ ] 旧 JWT 相关代码已删除
- [ ] 无敏感信息日志输出

### 前端
- [ ] jsencrypt 版本最新稳定版
- [ ] 公钥缓存策略合理
- [ ] 加密失败异常处理
- [ ] 无密码明文存储到 localStorage
- [ ] Token 传递方式与 Sa-Token 配置一致

## 功能验证

### 基本功能
- [ ] 新用户注册，密码 BCrypt 存储
- [ ] 新用户登录成功
- [ ] 旧用户（MD5密码）登录成功并自动迁移
- [ ] 密码错误登录失败
- [ ] Token 正常生成
- [ ] Token 验证正常
- [ ] 登出功能正常

### Redis 验证
- [ ] 登录后 Redis 中存在 Token 记录
- [ ] Token Key 格式正确（satoken:login:token:xxx）
- [ ] Token 过期时间正确（86400秒）
- [ ] 登出后 Redis 中 Token 被删除
- [ ] 服务重启后 Token 仍有效

### 安全验证
- [ ] 抓包查看密码字段为密文
- [ ] 数据库密码字段为 BCrypt 格式（$2a$开头）
- [ ] 相同密码产生不同哈希值
- [ ] 公钥接口可公开访问
- [ ] 登录接口拒绝明文密码
- [ ] 未登录访问受保护接口返回 401

### 兼容性
- [ ] Chrome/Firefox/Safari 浏览器测试
- [ ] 移动端浏览器测试
- [ ] 旧密码用户首次登录正常

## 性能验证

- [ ] RSA 加密耗时 < 100ms
- [ ] BCrypt 哈希耗时 < 500ms
- [ ] Redis 读写耗时 < 10ms
- [ ] 登录接口响应时间 < 1s
- [ ] 公钥缓存生效，无重复请求

## 部署检查

- [ ] Redis 服务正常运行
- [ ] Redis 连接配置正确
- [ ] Sa-Token 配置正确（token-name、timeout）
- [ ] 敏感配置不提交代码库
- [ ] HTTPS 正常启用
- [ ] 日志级别正确（不输出敏感信息）

## 回归测试

- [ ] 原有登录功能正常
- [ ] 权限拦截正常
- [ ] 其他接口无影响
- [ ] 前台接口无影响

## 废弃代码清理

- [ ] JwtUtil.java 已删除
- [ ] AuthInterceptor.java 已删除
- [ ] AuthAspect.java 已删除
- [ ] RequireAuth.java 已删除
- [ ] pom.xml jjwt 依赖已删除
