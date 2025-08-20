# AI提示词共享平台

一个现代化的AI提示词分享和管理平台，采用前后端分离架构。

## 项目架构

### 技术栈

**后端 (Spring Boot)**
- Java 17
- Spring Boot 3.x
- MyBatis Plus
- MySQL 8.0
- Redis
- JWT认证
- Swagger文档

**前端 (Vue 3)**
- Vue 3 + Composition API
- Element Plus UI组件库
- Vite构建工具
- Pinia状态管理
- Vue Router 4

## 项目结构

```
ai-prompt-platform/
├── backend/           # Spring Boot后端
│   ├── src/main/java/com/platform/
│   │   ├── controller/    # 控制器层
│   │   ├── dto/          # 数据传输对象
│   │   ├── entity/       # 实体类
│   │   ├── mapper/       # MyBatis Mapper
│   │   ├── service/      # 业务逻辑层
│   │   └── utils/        # 工具类
│   └── pom.xml
├── frontend/          # Vue 3前端
│   ├── src/
│   │   ├── api/          # API接口封装
│   │   ├── components/   # 可复用组件
│   │   ├── views/        # 页面组件
│   │   ├── stores/       # 状态管理
│   │   └── utils/        # 工具函数
│   └── package.json
└── README.md
```

## 功能特性

### 用户功能
- 用户注册/登录（JWT认证）
- 个人信息管理

### 提示词功能
- 提示词创建与管理
- 提示词浏览与搜索
- 分类筛选
- 点赞/收藏功能
- 使用次数统计
- 复制提示词

### 管理功能
- 提示词审核
- 分类管理
- 用户管理

## 快速开始

### 1. 启动后端服务

#### 环境要求
- Java 17+
- MySQL 8.0+
- Redis 6.0+

#### 配置数据库
```sql
CREATE DATABASE ai_prompt_platform CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

#### 启动步骤
```bash
cd backend
# 配置application.yml中的数据库和Redis连接
mvn clean compile
mvn spring-boot:run
```

后端服务将运行在: http://localhost:8080

### 2. 启动前端服务

#### 环境要求
- Node.js 16+
- npm 或 yarn

#### 启动步骤
```bash
cd frontend
npm install
npm run dev
```

前端服务将运行在: http://localhost:3000

### 3. 访问应用

- 首页: http://localhost:3000
- 提示词列表: http://localhost:3000/prompts
- 用户登录: http://localhost:3000/login
- 用户注册: http://localhost:3000/register

## API文档

启动后端服务后，可以访问Swagger文档：
http://localhost:8080/swagger-ui.html

## 开发说明

### 后端开发
- 使用Spring Boot 3.x最新特性
- MyBatis Plus简化数据库操作
- 全局异常处理
- 统一的响应格式
- JWT认证拦截器

### 前端开发
- Vue 3 Composition API
- Element Plus组件库
- 响应式设计
- 状态管理使用Pinia
- 路由守卫处理权限

## 部署说明

### 生产部署
- 后端：打包成jar文件部署
- 前端：构建静态文件部署到nginx或CDN

### Docker部署（可选）
提供Docker Compose配置文件，支持一键部署。

## 贡献指南

1. Fork项目
2. 创建功能分支
3. 提交代码
4. 创建Pull Request

## 许可证

MIT License