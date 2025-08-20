# AI提示词共享平台 - 前端

基于 Vue 3 + Element Plus 构建的现代化前端应用。

## 技术栈

- **Vue 3** - 渐进式JavaScript框架
- **Element Plus** - 基于Vue 3的组件库
- **Vue Router** - Vue.js官方路由管理器
- **Pinia** - Vue状态管理库
- **Axios** - HTTP客户端
- **Vite** - 下一代前端构建工具

## 功能特性

### 用户功能
- 用户注册与登录
- JWT认证机制
- 用户个人信息管理

### 提示词功能
- 浏览公开提示词列表
- 查看提示词详情
- 创建个人提示词
- 管理我的提示词
- 点赞和收藏功能
- 复制提示词到剪贴板

### 搜索与筛选
- 按分类筛选提示词
- 关键词搜索
- 分页浏览

## 项目结构

```
src/
├── api/          # API接口封装
├── components/   # 可复用组件
├── router/       # 路由配置
├── stores/       # 状态管理
├── utils/        # 工具函数
├── views/        # 页面组件
└── main.js       # 应用入口
```

## 快速开始

### 安装依赖
```bash
npm install
```

### 开发环境运行
```bash
npm run dev
```

### 构建生产版本
```bash
npm run build
```

### 预览构建结果
```bash
npm run preview
```

## 环境配置

### 开发环境
- 端口: 3000
- API代理: 自动代理到后端 http://localhost:8080

### 生产环境
- 构建输出目录: `dist`
- 静态资源CDN配置支持

## 页面路由

- `/` - 首页
- `/login` - 用户登录
- `/register` - 用户注册
- `/prompts` - 提示词列表
- `/prompts/:id` - 提示词详情
- `/create-prompt` - 创建提示词（需登录）
- `/my-prompts` - 我的提示词（需登录）

## 组件说明

### NavigationBar.vue
导航栏组件，包含用户状态显示和路由导航。

### PromptCard.vue
提示词卡片组件，展示提示词基本信息和交互按钮。

## API接口

所有API请求都通过 `/api` 前缀代理到后端服务，详见 `src/api/` 目录下的各个模块。

## 开发规范

- 使用Composition API编写组件
- 状态管理统一使用Pinia
- API调用统一封装在api模块
- 路由守卫处理认证逻辑