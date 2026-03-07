# 登录注册功能安全优化规范

## 1. 概述

### 1.1 背景
当前项目登录功能存在安全隐患：
- 前端密码明文传输
- 后端使用 MD5 哈希（无盐值，已不安全）
- Token 存储在内存中（服务重启丢失）
- 缺乏防重放攻击机制

### 1.2 目标
实现 RSA + Sa-Token + Redis 的安全登录方案，确保密码传输和存储安全，Token 持久化存储。

### 1.3 技术选型
| 组件 | 技术 | 说明 |
|------|------|------|
| 前端加密 | jsencrypt | RSA 公钥加密 |
| 后端认证 | Sa-Token | 轻量级权限认证框架 |
| 密码哈希 | Sa-Token BCrypt | 内置 BCrypt，强度可调 |
| Token存储 | Redis | 持久化存储，支持分布式 |

## 2. 系统设计

### 2.1 登录流程

```
┌─────────────┐     1.请求公钥      ┌─────────────┐     ┌─────────────┐
│   前端      │ ──────────────────► │   后端      │     │    Redis    │
│             │ ◄────────────────── │             │     │             │
│             │     2.返回公钥      │             │     │             │
│             │                     │             │     │             │
│  3.RSA加密  │     4.发送密文      │             │     │             │
│   密码      │ ──────────────────► │  5.RSA解密  │     │             │
│             │                     │             │     │             │
│             │                     │  6.验证密码 │     │             │
│             │                     │             │     │             │
│             │                     │  7.生成Token│ ──► │ 8.存储Token │
│             │                     │             │     │             │
│             │ ◄────────────────── │  9.返回Token│ ◄── │             │
└─────────────┘                     └─────────────┘     └─────────────┘
```

### 2.2 Token 存储方案对比

| 存储方式 | 优点 | 缺点 |
|----------|------|------|
| 内存（默认） | 简单，无需依赖 | 服务重启丢失，不支持分布式 |
| **Redis** | 持久化，支持分布式，高性能 | 需要 Redis 服务 |
| 数据库 | 持久化 | 性能较低 |

**选择 Redis 的原因**：
1. 支持服务重启后 Token 仍然有效
2. 支持多实例部署（分布式）
3. 高性能读写
4. 支持过期时间自动清理

### 2.3 Sa-Token + Redis 核心功能

| 功能 | 说明 |
|------|------|
| StpUtil.login(id) | 登录，生成 Token 并存入 Redis |
| StpUtil.getTokenValue() | 获取当前 Token |
| StpUtil.checkLogin() | 检查是否登录（从 Redis 验证） |
| StpUtil.logout() | 注销登录（从 Redis 删除） |
| SaSecureUtil.bCrypt() | BCrypt 密码哈希 |
| SaSecureUtil.checkBCrypt() | BCrypt 密码验证 |

### 2.4 API 设计

#### 2.4.1 获取公钥
```
GET /api/admin/public-key
Response: { "code": 200, "data": { "publicKey": "MIIBIjAN..." } }
```

#### 2.4.2 登录
```
POST /api/admin/login
Request: { "username": "admin", "password": "RSA加密后的Base64字符串" }
Response: { "code": 200, "data": { "token": "xxx", "userInfo": {...} } }
```

#### 2.4.3 注册
```
POST /api/admin/register
Request: { "username": "xxx", "password": "RSA加密后的Base64字符串", "email": "xxx" }
Response: { "code": 200, "data": { "token": "xxx", "userInfo": {...} } }
```

#### 2.4.4 登出
```
POST /api/admin/logout
Response: { "code": 200, "message": "登出成功" }
```

### 2.5 数据库设计

用户表 `user` 无需修改，BCrypt 哈希结果固定长度 60 字符，现有 `password` 字段（varchar(255)）足够。

## 3. 详细设计

### 3.1 后端模块

#### 3.1.1 Maven 依赖
```xml
<!-- Sa-Token -->
<dependency>
    <groupId>cn.dev33</groupId>
    <artifactId>sa-token-spring-boot-starter</artifactId>
    <version>1.37.0</version>
</dependency>

<!-- Sa-Token 整合 Redis -->
<dependency>
    <groupId>cn.dev33</groupId>
    <artifactId>sa-token-dao-redis-jackson</artifactId>
    <version>1.37.0</version>
</dependency>

<!-- Redis 连接池 -->
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-pool2</artifactId>
</dependency>
```

#### 3.1.2 application.yml 配置
```yaml
spring:
  redis:
    host: localhost
    port: 6379
    password: 
    database: 0
    lettuce:
      pool:
        max-active: 8
        max-idle: 8
        min-idle: 0

sa-token:
  token-name: Authorization
  timeout: 86400
  active-timeout: -1
  is-concurrent: true
  is-share: true
  token-style: uuid
  is-log: false
```

#### 3.1.3 Redis 配置类
```java
@Configuration
public class RedisConfig {
    
    @Bean
    public RedisConnectionFactory redisConnectionFactory(RedisProperties properties) {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setHostName(properties.getHost());
        config.setPort(properties.getPort());
        config.setPassword(properties.getPassword());
        config.setDatabase(properties.getDatabase());
        return new LettuceConnectionFactory(config);
    }
}
```

#### 3.1.4 RSA 工具类
```java
@Component
public class RsaUtil {
    private static KeyPair keyPair;
    
    @PostConstruct
    public void init() {
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(2048);
            keyPair = generator.generateKeyPair();
        } catch (Exception e) {
            throw new RuntimeException("RSA密钥对生成失败", e);
        }
    }
    
    public String getPublicKey() {
        return Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());
    }
    
    public String decrypt(String encryptedBase64) {
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, keyPair.getPrivate());
            byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(encryptedBase64));
            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("RSA解密失败", e);
        }
    }
}
```

#### 3.1.5 密码工具类（使用 Sa-Token）
```java
public class PasswordUtil {
    
    public static String hash(String password) {
        return SaSecureUtil.bCrypt(password);
    }
    
    public static boolean check(String password, String hash) {
        return SaSecureUtil.checkBCrypt(password, hash);
    }
    
    public static boolean isBCrypt(String hash) {
        return hash != null && hash.startsWith("$2a$");
    }
}
```

#### 3.1.6 AuthController 改造
```java
@RestController
@RequestMapping("/api/admin")
public class AuthController {
    
    @Autowired
    private RsaUtil rsaUtil;
    
    @Autowired
    private UserService userService;
    
    @GetMapping("/public-key")
    public Result<Map<String, String>> getPublicKey() {
        return Result.success(Map.of("publicKey", rsaUtil.getPublicKey()));
    }
    
    @PostMapping("/login")
    public Result<LoginVO> login(@RequestBody LoginDTO dto) {
        String password = rsaUtil.decrypt(dto.getPassword());
        dto.setPassword(password);
        return Result.success(userService.login(dto));
    }
    
    @PostMapping("/logout")
    public Result<Void> logout() {
        StpUtil.logout();
        return Result.success();
    }
}
```

#### 3.1.7 UserService 改造
```java
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    
    @Override
    public LoginVO login(LoginDTO dto) {
        User user = getOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, dto.getUsername()));
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        String storedPassword = user.getPassword();
        boolean valid;
        
        if (PasswordUtil.isBCrypt(storedPassword)) {
            valid = PasswordUtil.check(dto.getPassword(), storedPassword);
        } else {
            valid = DigestUtils.md5DigestAsHex(dto.getPassword().getBytes()).equals(storedPassword);
            if (valid) {
                user.setPassword(PasswordUtil.hash(dto.getPassword()));
                updateById(user);
            }
        }
        
        if (!valid) {
            throw new RuntimeException("密码错误");
        }
        
        StpUtil.login(user.getId());
        
        LoginVO vo = new LoginVO();
        vo.setToken(StpUtil.getTokenValue());
        vo.setUserInfo(LoginVO.UserInfo.fromUser(user));
        return vo;
    }
}
```

#### 3.1.8 Sa-Token 拦截器配置
```java
@Configuration
public class SaTokenConfig implements WebMvcConfigurer {
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SaInterceptor(handle -> StpUtil.checkLogin()))
                .addPathPatterns("/api/admin/**")
                .excludePathPatterns(
                    "/api/admin/login",
                    "/api/admin/public-key",
                    "/api/admin/register"
                );
    }
}
```

### 3.2 前端模块

#### 3.2.1 RSA 加密工具
```javascript
// utils/rsa.js
import JSEncrypt from 'jsencrypt'
import request from './request'

let publicKeyCache = null

export async function getPublicKey() {
    if (publicKeyCache) return publicKeyCache
    const res = await request.get('/admin/public-key')
    publicKeyCache = res.data.publicKey
    return publicKeyCache
}

export async function encryptPassword(password) {
    const publicKey = await getPublicKey()
    const encrypt = new JSEncrypt()
    encrypt.setPublicKey(publicKey)
    return encrypt.encrypt(password)
}
```

#### 3.2.2 登录组件改造
```javascript
import { encryptPassword } from '@/utils/rsa'

const handleLogin = async () => {
    await formRef.value.validate()
    loading.value = true
    try {
        const encryptedPassword = await encryptPassword(form.password)
        await userStore.login({
            username: form.username,
            password: encryptedPassword
        })
        ElMessage.success('登录成功')
        router.push('/admin')
    } catch (e) {
        console.error('登录失败', e)
    } finally {
        loading.value = false
    }
}
```

#### 3.2.3 请求拦截器
```javascript
// utils/request.js
request.interceptors.request.use(config => {
    const token = localStorage.getItem('token')
    if (token) {
        config.headers.Authorization = token
    }
    return config
})
```

### 3.3 Redis 数据结构

Sa-Token 在 Redis 中的存储格式：

```
Key: satoken:login:token:{tokenValue}
Value: {userId}
TTL: 86400 (24小时)

Key: satoken:login:session:{userId}
Value: {tokenList, sessionData}
TTL: 86400 (24小时)
```

## 4. 安全增强

### 4.1 登录限流
```yaml
sa-token:
  is-safe-token: true
```

### 4.2 密码强度校验
- 最少 8 位
- 包含大小写字母和数字
- 前后端双重校验

### 4.3 Token 安全
- Redis 存储防止伪造
- 自动过期清理
- 支持主动踢下线

## 5. 测试要点

1. RSA 加解密正确性
2. Sa-Token BCrypt 哈希验证
3. 旧密码迁移兼容性
4. Token 存入 Redis 验证
5. 服务重启后 Token 仍有效
6. 前端公钥缓存
