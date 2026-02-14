# 单元测试指南

## 1. 测试框架和工具

### 依赖配置
```xml
<!-- JUnit 5 -->
<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter</artifactId>
    <scope>test</scope>
</dependency>

<!-- Mockito -->
<dependency>
    <groupId>org.mockito</groupId>
    <artifactId>mockito-core</artifactId>
    <scope>test</scope>
</dependency>

<!-- Spring Test -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>
```

## 2. 测试结构

### 目录结构
```
src/test/java/com/platform/
├── controller/
│   └── AuthControllerTest.java
├── service/
│   ├── UserServiceTest.java
│   └── PromptServiceTest.java
├── mapper/
│   └── PromptMapperTest.java
└── integration/
    └── AuthIntegrationTest.java
```

## 3. 单元测试示例

### 3.1 Controller测试
```java
@ExtendWith(MockitoExtension.class)
@DisplayName("认证控制器测试")
class AuthControllerTest {
    
    @Mock
    private UserService userService;
    
    @InjectMocks
    private AuthController authController;
    
    @Test
    @DisplayName("用户注册成功")
    void testRegisterSuccess() {
        // Given
        UserRegisterDTO dto = new UserRegisterDTO();
        dto.setUsername("newuser");
        dto.setPassword("password123");
        dto.setEmail("new@example.com");
        
        User expectedUser = new User();
        expectedUser.setId(1L);
        expectedUser.setUsername("newuser");
        
        when(userService.register(anyString(), anyString(), anyString()))
            .thenReturn(expectedUser);
        
        // When
        Result<User> result = authController.register(dto);
        
        // Then
        assertNotNull(result);
        assertEquals(200, result.getCode());
        verify(userService, times(1)).register(anyString(), anyString(), anyString());
    }
}
```

### 3.2 Service测试
```java
@ExtendWith(MockitoExtension.class)
@DisplayName("提示词服务测试")
class PromptServiceTest {
    
    @Mock
    private PromptMapper promptMapper;
    
    @InjectMocks
    private PromptService promptService;
    
    @Test
    @DisplayName("创建提示词")
    void testCreatePrompt() {
        // Given
        Prompt prompt = new Prompt();
        prompt.setTitle("AI写作助手");
        prompt.setContent("你是一个专业的写作助手...");
        
        when(promptMapper.insert(any(Prompt.class)))
            .thenReturn(1);
        
        // When
        Prompt result = promptService.createPrompt(prompt);
        
        // Then
        assertNotNull(result);
        assertEquals("AI写作助手", result.getTitle());
        verify(promptMapper, times(1)).insert(any(Prompt.class));
    }
}
```

## 4. Mockito常用注解

### @Mock
用于创建Mock对象，模拟依赖
```java
@Mock
private UserService userService;
```

### @InjectMocks
自动将@Mock注入到被测试的类中
```java
@InjectMocks
private AuthController authController;
```

### @Spy
创建真实对象的Spy，可以部分Mock
```java
@Spy
private PromptService promptService;
```

### @ExtendWith(MockitoExtension.class)
启用Mockito扩展
```java
@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    // ...
}
```

## 5. Mockito常用方法

### when-then 设置返回值
```java
when(userService.login(anyString(), anyString()))
    .thenReturn(testUser);

when(promptService.getById(1L))
    .thenThrow(new RuntimeException("提示词不存在"));
```

### verify 验证方法调用
```java
// 验证方法被调用一次
verify(userService, times(1)).register(anyString(), anyString(), anyString());

// 验证方法从未被调用
verify(userService, never()).delete(any());

// 验证方法被至少调用一次
verify(userService, atLeastOnce()).getById(1L);
```

### ArgumentMatchers 参数匹配
```java
// 匹配任何类型的参数
when(userService.register(anyString(), anyString(), anyString()))
    .thenReturn(testUser);

// 匹配具体值
when(userService.login("admin", "password123"))
    .thenReturn(adminUser);

// 匹配正则表达式
when(userService.register(matches("^[a-zA-Z0-9_]+$"), anyString(), anyString()))
    .thenReturn(testUser);
```

## 6. JUnit 5 断言

### 基础断言
```java
// 判断是否为真
assertTrue(condition);

// 判断是否为假
assertFalse(condition);

// 判断是否为空
assertNull(value);
assertNotNull(value);

// 判断是否相等
assertEquals(expected, actual);
assertNotEquals(unexpected, actual);

// 判断是否相同（同一对象）
assertSame(expected, actual);
assertNotSame(unexpected, actual);
```

### 集合断言
```java
// 判断集合是否包含元素
assertTrue(list.contains(element));

// 判断集合大小
assertEquals(3, list.size());
```

### 异常断言
```java
// 验证异常
assertThrows(RuntimeException.class, () -> {
    userService.login("admin", "wrongpassword");
});

// 验证异常消息
RuntimeException exception = assertThrows(RuntimeException.class, () -> {
    userService.register("existingUser", "password123", "new@example.com");
});
assertTrue(exception.getMessage().contains("用户名已存在"));
```

## 7. 测试最佳实践

### 7.1 AAA模式 (Arrange-Act-Assert)
```java
@Test
void testUserLogin() {
    // Arrange - 准备测试数据
    UserLoginDTO loginDTO = new UserLoginDTO();
    loginDTO.setUsername("testuser");
    loginDTO.setPassword("password123");
    
    // Act - 执行被测试的方法
    Result<Map<String, Object>> result = authController.login(loginDTO);
    
    // Assert - 验证结果
    assertNotNull(result);
    assertEquals(200, result.getCode());
}
```

### 7.2 描述性的测试名称
```java
@Test
@DisplayName("用户登录成功 - 使用正确的用户名和密码")
void testLoginSuccessWithCorrectCredentials() {
    // ...
}
```

### 7.3 测试隔离
每个测试应该独立运行，不依赖其他测试的结果：
```java
@BeforeEach
void setUp() {
    // 在每个测试前初始化数据
    testUser = new User();
    testUser.setId(1L);
    testUser.setUsername("testuser");
}

@AfterEach
void tearDown() {
    // 在每个测试后清理资源
}
```

### 7.4 Mock vs Real对象
```java
// 使用Mock处理外部依赖
@Mock
private UserService userService;

// 创建真实对象进行集成测试
private PromptService promptService = new PromptService();
```

## 8. 运行测试

### 命令行运行
```bash
# 运行所有测试
mvn test

# 运行特定测试类
mvn test -Dtest=AuthControllerTest

# 运行特定测试方法
mvn test -Dtest=AuthControllerTest#testLoginSuccess

# 生成测试覆盖率报告
mvn test jacoco:report
```

### IDE运行
在IntelliJ IDEA或Eclipse中，可以直接右键测试类或方法运行。

## 9. 测试覆盖率

### 添加Jacoco插件
```xml
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <version>0.8.8</version>
    <executions>
        <execution>
            <goals>
                <goal>prepare-agent</goal>
            </goals>
        </execution>
        <execution>
            <id>report</id>
            <phase>test</phase>
            <goals>
                <goal>report</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

### 查看覆盖率报告
```bash
mvn jacoco:report
# 报告位置: target/site/jacoco/index.html
```

## 10. 常见问题

### Q: Mock对象总是返回null
**A:** 确保使用了@ExtendWith(MockitoExtension.class)或MockitoAnnotations.openMocks(this)

### Q: 如何测试异步方法
**A:** 使用CountDownLatch或Thread.sleep()等待异步完成

### Q: 如何Mock静态方法
**A:** 使用PowerMock库，或重构代码避免静态方法

### Q: 如何测试私有方法
**A:** 使用ReflectionTestUtils.invokeMethod()，或改为public方法

## 11. 持续集成

在CI/CD流程中自动运行测试：
```yaml
# GitHub Actions示例
name: Tests
on: [push, pull_request]
jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v2
      - name: Set up JDK 17
        uses: actions/setup-java@v2
        with:
          java-version: 17
      - name: Run tests
        run: mvn test
      - name: Generate coverage report
        run: mvn jacoco:report
```
