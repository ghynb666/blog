# 登录功能安全优化方案

## 当前状态分析

### 前端
- **无加密**：密码明文传输
- 位置：`Login.vue` → `userStore.login(form)` → 直接发送明文

### 后端
- **MD5加密**：使用 Spring 的 `DigestUtils.md5DigestAsHex()`
- 位置：`UserServiceImpl.java:28`
- 问题：MD5 已不安全，无盐值

## 存在的安全问题

| 问题 | 风险等级 | 说明 |
|------|----------|------|
| 前端明文传输 | 高 | 即使 HTTPS，中间人攻击仍可能获取明文密码 |
| MD5 哈希 | 高 | 彩虹表可破解，碰撞攻击 |
| 无盐值 | 高 | 相同密码产生相同哈希，批量破解 |
| 无防重放 | 中 | 登录请求可被截获重放 |

## 生产环境最佳实践

### 方案一：RSA 非对称加密（推荐）

```
前端：RSA公钥加密密码 → 传输密文
后端：RSA私钥解密 → BCrypt哈希验证
```

**优点**：安全性高，密文只能由服务端解密
**缺点**：需要管理密钥对

### 方案二：HTTPS + BCrypt

```
前端：明文（依赖 HTTPS 加密传输）
后端：BCrypt 哈希（自带盐值）
```

**优点**：实现简单
**缺点**：依赖 HTTPS 安全性

### 方案三：前端哈希 + 后端验证

```
前端：SHA-256(password + timestamp + nonce)
后端：验证时间戳有效性，BCrypt验证
```

**优点**：防重放攻击
**缺点**：实现复杂

## 推荐方案：RSA + BCrypt

### 实现步骤

#### 1. 后端改造

**1.1 添加依赖**
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```

**1.2 创建 RSA 工具类**
- 生成密钥对（启动时或配置）
- 提供公钥接口 `/api/admin/public-key`
- 私钥解密方法

**1.3 密码存储改造**
- 使用 BCrypt 替代 MD5
- 迁移现有用户密码（重新哈希）

**1.4 登录接口改造**
```java
// 1. RSA私钥解密密码
String password = RsaUtil.decrypt(encryptedPassword, privateKey);
// 2. BCrypt验证
if (!BCrypt.checkpw(password, user.getPassword())) {
    throw new RuntimeException("密码错误");
}
```

#### 2. 前端改造

**2.1 安装 jsencrypt**
```bash
npm install jsencrypt
```

**2.2 登录流程改造**
```javascript
// 1. 获取公钥
const { publicKey } = await getPublicKey()
// 2. RSA加密密码
const encrypt = new JSEncrypt()
encrypt.setPublicKey(publicKey)
const encryptedPassword = encrypt.encrypt(password)
// 3. 发送加密密码
await login({ username, password: encryptedPassword })
```

#### 3. 数据库迁移

**3.1 添加盐值字段（可选，BCrypt自带）**

**3.2 密码迁移脚本**
```sql
-- 用户下次登录时自动迁移到BCrypt
-- 或批量迁移（需要用户重置密码）
```

## 实施清单

- [ ] 后端：添加 BCrypt 依赖
- [ ] 后端：创建 RSA 工具类
- [ ] 后端：提供公钥接口
- [ ] 后端：改造登录接口（RSA解密 + BCrypt验证）
- [ ] 后端：用户注册/修改密码使用 BCrypt
- [ ] 前端：安装 jsencrypt
- [ ] 前端：登录时 RSA 加密密码
- [ ] 前端：添加请求时间戳防重放
- [ ] 数据库：密码迁移策略
- [ ] 测试：登录功能测试
- [ ] 测试：安全测试

## 额外安全建议

1. **登录限流**：同一 IP/用户登录失败次数限制
2. **验证码**：失败 N 次后显示验证码
3. **密码强度**：前端校验密码复杂度
4. **Token 刷新**：短期 Token + Refresh Token 机制
5. **审计日志**：记录登录成功/失败日志
