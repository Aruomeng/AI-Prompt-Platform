# 性能优化和缓存策略文档

## 1. 分页优化

### 1.1 数据库连接池优化
在 `application.yml` 中配置 HikariCP：
```yaml
spring:
  datasource:
    hikari:
      maximum-pool-size: 20      # 最大连接数
      minimum-idle: 5            # 最小空闲连接
      idle-timeout: 600000       # 空闲超时时间（ms）
      max-lifetime: 1800000      # 连接最大生命周期（ms）
```

### 1.2 分页查询最佳实践
- 推荐每页大小：10-20条记录
- 避免分页过大导致内存溢出
- 使用MyBatis Plus的Page对象进行分页

#### 示例代码：
```java
IPage<Prompt> page = new Page<>(pageNum, pageSize);
IPage<Prompt> result = promptService.page(page, 
    new QueryWrapper<Prompt>()
        .eq("status", "APPROVED")
        .orderByDesc("create_time"));
```

## 2. Redis缓存策略

### 2.1 缓存键设计
使用统一的命名规范：
```
prompt:list:{page}:{size}:{categoryId}    # 提示词列表
prompt:detail:{id}                        # 提示词详情
user:info:{userId}                        # 用户信息
category:all                              # 分类列表
```

### 2.2 缓存时间设置
- 热数据（列表）：5分钟 - 300秒
- 温数据（详情）：10分钟 - 600秒
- 冷数据（个人信息）：24小时 - 86400秒
- 用户会话：7天 - 604800秒

### 2.3 缓存失效策略
使用 `CacheUtils` 工具类：
```java
// 设置缓存
cacheUtils.set("prompt:list:1:10:1", data, 300);

// 获取缓存
Object cached = cacheUtils.get("prompt:list:1:10:1");

// 删除缓存
cacheUtils.delete("prompt:list:*:*:*");

// 检查缓存是否存在
boolean exists = cacheUtils.exists("prompt:list:1:10:1");
```

### 2.4 缓存更新触发时机
在以下操作后清除相关缓存：
- 创建提示词：清除列表缓存
- 更新提示词：清除详情和列表缓存
- 删除提示词：清除详情和列表缓存
- 点赞/收藏：清除详情缓存

## 3. 数据库查询优化

### 3.1 索引优化
确保以下字段都有索引：
```sql
CREATE INDEX idx_prompt_status ON prompt(status);
CREATE INDEX idx_prompt_user_id ON prompt(user_id);
CREATE INDEX idx_prompt_category_id ON prompt(category_id);
CREATE INDEX idx_prompt_create_time ON prompt(create_time);
CREATE INDEX idx_user_username ON user(username);
CREATE INDEX idx_user_email ON user(email);
CREATE INDEX idx_user_like_user_prompt ON user_like(user_id, prompt_id);
CREATE INDEX idx_user_favorite_user_prompt ON user_favorite(user_id, prompt_id);
```

### 3.2 MyBatis Plus配置优化
```yaml
mybatis-plus:
  configuration:
    # 使用log4j2而不是stdout
    log-impl: org.apache.ibatis.logging.log4j2.Log4j2Impl
    # 启用驼峰命名映射
    map-underscore-to-camel-case: true
  global-config:
    db-config:
      # 使用自增主键
      id-type: auto
```

## 4. Redis连接池优化

### 4.1 连接池配置
```yaml
spring:
  data:
    redis:
      host: localhost
      port: 6379
      timeout: 5000ms
      lettuce:
        pool:
          max-active: 20       # 最大活跃连接数
          max-wait: -1ms       # 最长等待时间
          max-idle: 10         # 最大空闲连接数
          min-idle: 5          # 最小空闲连接数
```

## 5. 日志性能优化

### 5.1 日志级别配置
```yaml
logging:
  level:
    root: info                          # 默认级别
    com.platform: debug                 # 应用日志
    com.baomidou.mybatisplus: debug    # MyBatis Plus
    org.springframework.web: info       # Spring Web
```

### 5.2 日志文件管理
- 单个文件最大大小：10MB
- 保留历史文件：30天
- 总大小上限：1GB

## 6. 常见性能问题排查

### 6.1 数据库性能
1. 检查慢查询日志
2. 分析查询执行计划：`EXPLAIN SELECT ...`
3. 检查索引使用情况

### 6.2 缓存命中率低
1. 检查缓存键是否正确
2. 查看缓存过期时间是否过短
3. 监控Redis内存使用

### 6.3 内存溢出
1. 检查分页大小是否过大
2. 查看缓存数据是否过多
3. 监控数据库连接池大小

## 7. 监控指标

### 关键指标
- 数据库连接池使用率
- Redis缓存命中率
- API平均响应时间
- 内存使用情况
- CPU使用率

### 监控工具建议
- Spring Boot Actuator：应用监控
- Redis CLI：Redis监控
- MySQL慢查询日志：数据库监控
- ELK Stack：日志分析

## 8. 压力测试

### 测试工具
```bash
# 使用 Apache Bench 进行压力测试
ab -n 1000 -c 100 http://localhost:8080/api/prompts/public/list

# 使用 wrk 进行性能测试
wrk -t12 -c400 -d30s http://localhost:8080/api/prompts/public/list
```

### 性能目标
- API响应时间 < 200ms
- 缓存命中率 > 80%
- 数据库连接池利用率 < 80%
- 成功率 = 100%
