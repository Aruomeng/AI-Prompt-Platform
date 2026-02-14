# API接口文档

## 基础信息

- **基础URL**: `http://localhost:8080`
- **API版本**: v1
- **文档格式**: OpenAPI 3.0
- **在线文档**: `http://localhost:8080/swagger-ui.html`

## 认证方式

所有需要认证的接口都需要在请求头中添加：
```
Authorization: Bearer {token}
```

## 1. 认证管理 API

### 1.1 用户注册

**端点**: `POST /api/auth/register`

**描述**: 新用户注册，创建账户

**请求体**:
```json
{
  "username": "newuser",
  "password": "password123",
  "email": "newuser@example.com"
}
```

**响应示例** (成功 200):
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 3,
    "username": "newuser",
    "email": "newuser@example.com",
    "nickname": null,
    "avatar": null,
    "role": "USER",
    "createTime": "2024-02-14T10:30:00",
    "updateTime": "2024-02-14T10:30:00"
  }
}
```

**错误响应** (409):
```json
{
  "code": 409,
  "message": "用户名已存在",
  "data": null
}
```

---

### 1.2 用户登录

**端点**: `POST /api/auth/login`

**描述**: 用户登录，获取JWT Token

**请求体**:
```json
{
  "username": "admin",
  "password": "password123"
}
```

**响应示例** (成功 200):
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "user": {
      "id": 1,
      "username": "admin",
      "email": "admin@example.com",
      "nickname": "管理员",
      "avatar": null,
      "role": "ADMIN",
      "createTime": "2024-02-14T10:00:00",
      "updateTime": "2024-02-14T10:00:00"
    }
  }
}
```

**错误响应** (401):
```json
{
  "code": 401,
  "message": "密码错误",
  "data": null
}
```

---

### 1.3 获取当前用户信息

**端点**: `GET /api/auth/user-info`

**描述**: 获取登录用户的详细信息

**请求头**:
```
Authorization: Bearer {token}
```

**响应示例** (成功 200):
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1,
    "username": "admin",
    "email": "admin@example.com",
    "nickname": "管理员",
    "avatar": null,
    "role": "ADMIN",
    "createTime": "2024-02-14T10:00:00"
  }
}
```

---

## 2. 提示词管理 API

### 2.1 获取提示词列表

**端点**: `GET /api/prompts/public/list`

**描述**: 分页获取已审核的提示词列表，支持分类和关键词筛选

**查询参数**:
| 参数 | 类型 | 必需 | 默认值 | 说明 |
|------|------|------|--------|------|
| page | Long | 否 | 1 | 页码 |
| size | Long | 否 | 10 | 每页数量 |
| categoryId | Long | 否 | - | 分类ID |
| keyword | String | 否 | - | 搜索关键词 |

**响应示例** (成功 200):
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "total": 3,
    "records": [
      {
        "id": 1,
        "title": "AI写作助手",
        "content": "你是一个专业的写作助手...",
        "description": "通用AI写作助手",
        "categoryId": 1,
        "tags": "写作,助手,通用",
        "userId": 1,
        "usageCount": 100,
        "likeCount": 50,
        "favoriteCount": 30,
        "status": "APPROVED",
        "createTime": "2024-02-14T10:00:00"
      }
    ],
    "current": 1,
    "size": 10,
    "pages": 1
  }
}
```

---

### 2.2 获取提示词详情

**端点**: `GET /api/prompts/public/{id}`

**描述**: 根据ID获取提示词的详细信息，同时增加使用次数

**路径参数**:
| 参数 | 类型 | 说明 |
|------|------|------|
| id | Long | 提示词ID |

**响应示例** (成功 200):
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1,
    "title": "AI写作助手",
    "content": "你是一个专业的写作助手...",
    "description": "通用AI写作助手",
    "categoryId": 1,
    "tags": "写作,助手,通用",
    "userId": 1,
    "usageCount": 101,
    "likeCount": 50,
    "favoriteCount": 30,
    "status": "APPROVED",
    "createTime": "2024-02-14T10:00:00"
  }
}
```

---

### 2.3 创建提示词

**端点**: `POST /api/prompts/create`

**描述**: 创建新的提示词，创建后需要管理员审核

**请求头**:
```
Authorization: Bearer {token}
```

**请求体**:
```json
{
  "title": "新的提示词",
  "content": "提示词内容详细描述...",
  "description": "简短描述",
  "categoryId": 1,
  "tags": "标签1,标签2"
}
```

**响应示例** (成功 200):
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 4,
    "title": "新的提示词",
    "content": "提示词内容详细描述...",
    "description": "简短描述",
    "categoryId": 1,
    "tags": "标签1,标签2",
    "userId": 1,
    "usageCount": 0,
    "likeCount": 0,
    "favoriteCount": 0,
    "status": "PENDING",
    "createTime": "2024-02-14T11:00:00"
  }
}
```

---

### 2.4 更新提示词

**端点**: `PUT /api/prompts/{id}`

**描述**: 更新已创建的提示词，只有提示词拥有者可以更新

**路径参数**:
| 参数 | 类型 | 说明 |
|------|------|------|
| id | Long | 提示词ID |

**请求头**:
```
Authorization: Bearer {token}
```

**请求体**:
```json
{
  "title": "更新的标题",
  "content": "更新的内容...",
  "description": "更新的描述",
  "categoryId": 2,
  "tags": "新标签1,新标签2"
}
```

**响应示例** (成功 200):
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1,
    "title": "更新的标题",
    "status": "PENDING"
  }
}
```

---

### 2.5 删除提示词

**端点**: `DELETE /api/prompts/{id}`

**描述**: 删除指定的提示词，只有拥有者可以删除

**路径参数**:
| 参数 | 类型 | 说明 |
|------|------|------|
| id | Long | 提示词ID |

**请求头**:
```
Authorization: Bearer {token}
```

**响应示例** (成功 200):
```json
{
  "code": 200,
  "message": "操作成功",
  "data": null
}
```

---

### 2.6 获取我的提示词

**端点**: `GET /api/prompts/my`

**描述**: 获取当前登录用户创建的提示词列表

**请求头**:
```
Authorization: Bearer {token}
```

**查询参数**:
| 参数 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| page | Long | 1 | 页码 |
| size | Long | 10 | 每页数量 |

**响应示例**: 同"获取提示词列表"

---

### 2.7 获取我的收藏

**端点**: `GET /api/prompts/my-favorites`

**描述**: 获取当前登录用户收藏的提示词列表

**请求头**:
```
Authorization: Bearer {token}
```

**查询参数**:
| 参数 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| page | Long | 1 | 页码 |
| size | Long | 10 | 每页数量 |

**响应示例**: 同"获取提示词列表"

---

### 2.8 点赞提示词

**端点**: `POST /api/prompts/{id}/like`

**描述**: 对指定的提示词进行点赞

**路径参数**:
| 参数 | 类型 | 说明 |
|------|------|------|
| id | Long | 提示词ID |

**请求头**:
```
Authorization: Bearer {token}
```

**响应示例** (成功 200):
```json
{
  "code": 200,
  "message": "操作成功",
  "data": null
}
```

---

### 2.9 取消点赞

**端点**: `DELETE /api/prompts/{id}/like`

**描述**: 取消对指定提示词的点赞

**路径参数**:
| 参数 | 类型 | 说明 |
|------|------|------|
| id | Long | 提示词ID |

**请求头**:
```
Authorization: Bearer {token}
```

---

### 2.10 收藏提示词

**端点**: `POST /api/prompts/{id}/favorite`

**描述**: 收藏指定的提示词

**路径参数**:
| 参数 | 类型 | 说明 |
|------|------|------|
| id | Long | 提示词ID |

**请求头**:
```
Authorization: Bearer {token}
```

---

### 2.11 取消收藏

**端点**: `DELETE /api/prompts/{id}/favorite`

**描述**: 取消对指定提示词的收藏

**路径参数**:
| 参数 | 类型 | 说明 |
|------|------|------|
| id | Long | 提示词ID |

**请求头**:
```
Authorization: Bearer {token}
```

---

### 2.12 检查点赞状态

**端点**: `GET /api/prompts/{id}/like-status`

**描述**: 检查当前用户是否对指定提示词进行了点赞

**路径参数**:
| 参数 | 类型 | 说明 |
|------|------|------|
| id | Long | 提示词ID |

**请求头**:
```
Authorization: Bearer {token}
```

**响应示例** (成功 200):
```json
{
  "code": 200,
  "message": "操作成功",
  "data": true
}
```

---

### 2.13 检查收藏状态

**端点**: `GET /api/prompts/{id}/favorite-status`

**描述**: 检查当前用户是否收藏了指定提示词

**路径参数**:
| 参数 | 类型 | 说明 |
|------|------|------|
| id | Long | 提示词ID |

**请求头**:
```
Authorization: Bearer {token}
```

**响应示例** (成功 200):
```json
{
  "code": 200,
  "message": "操作成功",
  "data": false
}
```

---

## 3. 通用错误响应

### 401 未授权
```json
{
  "code": 401,
  "message": "未授权",
  "data": null
}
```

### 403 禁止访问
```json
{
  "code": 403,
  "message": "禁止访问",
  "data": null
}
```

### 404 资源不存在
```json
{
  "code": 404,
  "message": "资源不存在",
  "data": null
}
```

### 500 内部服务器错误
```json
{
  "code": 500,
  "message": "系统内部错误，请稍后重试",
  "data": null
}
```

---

## 4. 在线API文档

启动应用后，访问以下地址查看完整的交互式API文档：

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/v3/api-docs

## 5. API调用示例

### 使用 curl

```bash
# 登录
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"password123"}'

# 获取提示词列表
curl -X GET "http://localhost:8080/api/prompts/public/list?page=1&size=10" \
  -H "Authorization: Bearer {token}"

# 创建提示词
curl -X POST http://localhost:8080/api/prompts/create \
  -H "Authorization: Bearer {token}" \
  -H "Content-Type: application/json" \
  -d '{
    "title":"新提示词",
    "content":"内容",
    "categoryId":1,
    "tags":"tag1"
  }'
```

### 使用 Postman

1. 导入 OpenAPI 规范：http://localhost:8080/v3/api-docs
2. 在 Environment 中设置 `token` 变量
3. 在请求中使用 `Bearer {{token}}`

---

## 6. 访问限制

当前API不限制请求频率，生产环境建议添加速率限制：
- 未认证用户：每分钟60次请求
- 认证用户：每分钟300次请求
- 管理员：无限制
