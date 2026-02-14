# AI提示词共享平台 - 启动指南

## 📋 前置要求

### 系统要求
- **操作系统**: Linux / macOS / Windows
- **内存**: 至少4GB RAM
- **磁盘**: 至少1GB可用空间

### 必要软件

#### Java环境
- **Java 17+** 
- **Maven 3.6+**

安装验证：
```bash
java -version      # 确认Java版本 >= 17
mvn -version       # 确认Maven版本 >= 3.6
```

#### 数据库
- **MySQL 8.0+**

安装验证：
```bash
mysql --version    # 确认MySQL版本 >= 8.0
```

#### 缓存
- **Redis 6.0+**

安装验证：
```bash
redis-cli --version  # 确认Redis版本 >= 6.0
```

#### Node.js（前端）
- **Node.js 16+**
- **npm 8+ 或 yarn 1.22+**

安装验证：
```bash
node --version     # 确认Node.js版本 >= 16
npm --version      # 确认npm版本 >= 8
```

---

## 🗄️ 数据库初始化

### 1. 创建数据库

使用MySQL客户端执行以下命令：

```bash
# 登录MySQL
mysql -u root -p

# 创建数据库
CREATE DATABASE ai_prompt_platform CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

# 查看创建结果
SHOW DATABASES;

# 退出
exit;
```

### 2. 导入初始化脚本

```bash
# 方式一：使用mysql命令
mysql -u root -p ai_prompt_platform < backend/sql/init.sql

# 方式二：在MySQL客户端中执行
mysql -u root -p
use ai_prompt_platform;
source /path/to/backend/sql/init.sql;
```

### 3. 验证数据库初始化

```bash
mysql -u root -p ai_prompt_platform

# 查看表
SHOW TABLES;

# 查看初始数据
SELECT * FROM user;
SELECT * FROM prompt_category;
SELECT * FROM prompt;

# 退出
exit;
```

**默认用户**：
- 用户名: `admin`
- 密码: `password123`（需在应用中验证）
- 角色: `ADMIN`

---

## 🔧 配置文件

### 后端配置 (application.yml)

位置：`backend/src/main/resources/application.yml`

#### 数据库配置
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/ai_prompt_platform?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8
    username: root          # MySQL用户名
    password: TYH041113     # MySQL密码
    driver-class-name: com.mysql.cj.jdbc.Driver
```

#### Redis配置
```yaml
spring:
  data:
    redis:
      host: localhost       # Redis服务器地址
      port: 6379            # Redis端口
      database: 0           # 数据库编号
      timeout: 5000ms       # 连接超时时间
```

#### 服务器配置
```yaml
server:
  port: 8080                # 服务端口
```

### 前端配置 (vite.config.js)

位置：`frontend/vite.config.js`

```javascript
export default {
  // API代理配置
  server: {
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api/, '/api')
      }
    }
  }
}
```

---

## 🚀 启动应用

### 方式一：使用启动脚本（推荐）

#### 后端启动
```bash
# 在项目根目录运行
chmod +x start-backend.sh
./start-backend.sh

# 脚本会自动：
# 1. 检查Java和Maven
# 2. 清理编译
# 3. 编译项目
# 4. 运行单元测试（可选）
# 5. 启动应用
```

#### 前端启动
```bash
# 在项目根目录运行
chmod +x start-frontend.sh
./start-frontend.sh

# 脚本会自动：
# 1. 检查Node.js和npm
# 2. 安装依赖
# 3. 启动开发服务器
```

### 方式二：手动启动

#### 后端启动
```bash
cd backend

# 清理和编译
mvn clean compile

# 运行单元测试
mvn test

# 启动应用
mvn spring-boot:run
```

启动成功标志：
```
Application started in X seconds (JVM running for X seconds)
```

#### 前端启动
```bash
cd frontend

# 安装依赖
npm install

# 启动开发服务器
npm run dev
```

启动成功标志：
```
VITE v5.0.0  ready in XXX ms
```

---

## 📱 访问应用

### 应用地址

| 模块 | 地址 | 说明 |
|------|------|------|
| 前端首页 | http://localhost:5173 | Vue 3应用首页 |
| 后端API基础URL | http://localhost:8080 | REST API基础地址 |
| **Swagger API文档** | http://localhost:8080/swagger-ui.html | 📄 交互式API文档 |
| OpenAPI规范 | http://localhost:8080/v3/api-docs | OpenAPI JSON规范 |

### 测试登录

使用以下默认用户登录：

**管理员账户**：
- 用户名: `admin`
- 密码: `password123`

**普通用户账户**：
- 用户名: `testuser`
- 密码: `password123`

---

## 🧪 运行测试

### 运行所有测试
```bash
cd backend
mvn test
```

### 运行特定测试类
```bash
# 运行认证控制器测试
mvn test -Dtest=AuthControllerTest

# 运行用户服务测试
mvn test -Dtest=UserServiceTest

# 运行提示词服务测试
mvn test -Dtest=PromptServiceTest
```

### 运行特定测试方法
```bash
mvn test -Dtest=AuthControllerTest#testLoginSuccess
```

### 生成测试覆盖率报告
```bash
cd backend
mvn test jacoco:report

# 打开报告
open target/site/jacoco/index.html  # macOS
xdg-open target/site/jacoco/index.html  # Linux
```

---

## 📊 查看日志

### 后端日志

**实时日志**（启动时在控制台显示）：
```
2024-02-14 11:30:45.123 [main] INFO com.platform.AiPromptPlatformApplication - Starting AiPromptPlatformApplication
2024-02-14 11:30:50.456 [main] INFO o.s.b.w.embedded.tomcat.TomcatWebServer - Tomcat started on port(s): 8080
```

**日志文件**：
```
logs/ai-prompt-platform.log
```

查看日志：
```bash
# 实时查看日志
tail -f logs/ai-prompt-platform.log

# 查看最后100行
tail -100 logs/ai-prompt-platform.log

# 查看包含ERROR的日志
grep ERROR logs/ai-prompt-platform.log
```

### 前端日志

在浏览器开发者工具中查看：
1. 按 F12 打开开发者工具
2. 选择 "Console" 标签
3. 查看应用日志和错误

---

## 🔌 API测试

### 使用Swagger UI（推荐）
1. 启动后端服务
2. 访问 http://localhost:8080/swagger-ui.html
3. 在页面中直接测试各个API端点

### 使用curl命令

#### 登录获取Token
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "password123"
  }'

# 从响应中获取token值
```

#### 获取提示词列表
```bash
curl -X GET "http://localhost:8080/api/prompts/public/list?page=1&size=10" \
  -H "Authorization: Bearer {token}"
```

#### 创建提示词
```bash
curl -X POST http://localhost:8080/api/prompts/create \
  -H "Authorization: Bearer {token}" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "新的提示词",
    "content": "提示词内容...",
    "description": "描述",
    "categoryId": 1,
    "tags": "标签1,标签2"
  }'
```

### 使用Postman

1. 下载安装 Postman
2. 在 Postman 中导入 OpenAPI 规范：
   - File → Import → URL
   - 输入：http://localhost:8080/v3/api-docs
3. 在 Environment 中设置 `token` 变量
4. 在请求中使用 `Bearer {{token}}`

---

## 🔍 故障排查

### 问题1：数据库连接失败

**症状**：
```
com.mysql.cj.jdbc.exceptions.CommunicationsException: Communications link failure
```

**解决方案**：
1. 检查MySQL是否运行：`mysql --version`
2. 检查MySQL服务：
   ```bash
   # macOS
   brew services list
   
   # Linux
   systemctl status mysql
   
   # Windows
   services.msc
   ```
3. 检查配置文件中的数据库连接信息
4. 确保数据库已创建：`mysql -u root -p -e "SHOW DATABASES;"`

### 问题2：Redis连接失败

**症状**：
```
io.lettuce.core.RedisConnectionException: Unable to connect to localhost:6379
```

**解决方案**：
1. 检查Redis是否运行：
   ```bash
   redis-cli ping
   # 应该返回 PONG
   ```
2. 启动Redis服务：
   ```bash
   # macOS
   redis-server /usr/local/etc/redis.conf
   
   # Linux
   redis-server
   ```
3. 检查Redis端口是否正确（默认6379）

### 问题3：端口被占用

**症状**：
```
Address already in use: bind
```

**解决方案**：
```bash
# 查找占用8080端口的进程
lsof -i :8080

# 杀死进程
kill -9 <PID>

# 或修改应用端口
# 在 application.yml 中修改 server.port
```

### 问题4：Maven编译失败

**症状**：
```
[ERROR] COMPILATION ERROR
```

**解决方案**：
1. 检查Java版本：`java -version`（需要17+）
2. 清理Maven缓存：`mvn clean`
3. 检查网络连接（下载依赖）
4. 更新Maven：`mvn --version && mvn -U clean install`

### 问题5：前端启动失败

**症状**：
```
error Command failed with exit code 1
```

**解决方案**：
1. 检查Node.js版本：`node --version`（需要16+）
2. 清理node_modules：`rm -rf node_modules && npm install`
3. 检查npm版本：`npm --version`
4. 更新npm：`npm install -g npm@latest`

---

## 📈 性能优化

### 缓存配置
Redis会自动缓存：
- 提示词列表（5分钟）
- 提示词详情（10分钟）
- 用户信息（24小时）

### 数据库优化
- 使用HikariCP连接池
- 自动创建必要索引
- MyBatis Plus优化查询

### 日志优化
- 日志文件自动轮转（10MB/文件）
- 保留30天历史日志
- 总大小限制1GB

---

## 📚 相关文档

- **API文档**: `backend/API_DOCUMENTATION.md`
- **测试指南**: `backend/TESTING_GUIDE.md`
- **性能优化**: `backend/PERFORMANCE_OPTIMIZATION.md`
- **优化总结**: `OPTIMIZATION_SUMMARY.md`

---

## ✅ 检查清单

启动前请确认：

- [ ] Java 17+ 已安装
- [ ] Maven 3.6+ 已安装
- [ ] MySQL 8.0+ 已安装并运行
- [ ] Redis 6.0+ 已安装并运行
- [ ] Node.js 16+ 已安装
- [ ] 数据库已初始化
- [ ] 配置文件已正确修改
- [ ] 网络连接正常

启动后请确认：

- [ ] 后端服务运行正常（端口8080）
- [ ] 前端服务运行正常（端口5173）
- [ ] Swagger文档可访问
- [ ] 能够使用默认用户登录
- [ ] 能够查看测试数据

---

## 🆘 获取帮助

如遇到问题，请：

1. 查看相关文档
2. 检查日志文件
3. 查看故障排查部分
4. 访问项目仓库：https://github.com/Aruomeng/ai-prompt-platform

---

**最后更新**: 2024年2月14日  
**版本**: 1.0.0
