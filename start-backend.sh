#!/bin/bash

# AI提示词共享平台 - 启动脚本

echo "================================================"
echo "  AI提示词共享平台 启动脚本"
echo "================================================"
echo ""

# 检查Java
if ! command -v java &> /dev/null; then
    echo "❌ Java未安装，请先安装Java 17+"
    exit 1
fi

JAVA_VERSION=$(java -version 2>&1 | grep -oP 'version "\K.*?(?=")')
echo "✅ Java 版本: $JAVA_VERSION"
echo ""

# 检查MySQL
if ! command -v mysql &> /dev/null; then
    echo "⚠️  MySQL未安装，请先安装MySQL 8.0+"
    echo "   或确保MySQL服务正在运行"
fi

# 检查Redis
if ! command -v redis-cli &> /dev/null; then
    echo "⚠️  Redis未安装，请先安装Redis 6.0+"
    echo "   或确保Redis服务正在运行"
fi

echo ""
echo "================================================"
echo "  启动后端服务"
echo "================================================"
echo ""

cd "$(dirname "$0")/backend"

# 检查pom.xml
if [ ! -f "pom.xml" ]; then
    echo "❌ pom.xml 不存在，请确保在项目根目录运行此脚本"
    exit 1
fi

# 检查Maven
if ! command -v mvn &> /dev/null; then
    echo "❌ Maven未安装，请先安装Maven"
    echo "   访问: https://maven.apache.org/download.cgi"
    exit 1
fi

echo "✅ Maven 已安装"
echo ""

# 清理编译
echo "🔨 清理项目..."
mvn clean -q

# 编译项目
echo "🔨 编译项目..."
mvn compile -q

if [ $? -ne 0 ]; then
    echo "❌ 项目编译失败"
    exit 1
fi

echo "✅ 项目编译成功"
echo ""

# 运行测试
read -p "是否运行单元测试? (y/n) " -n 1 -r
echo
if [[ $REPLY =~ ^[Yy]$ ]]; then
    echo "🧪 运行单元测试..."
    mvn test -q
    if [ $? -eq 0 ]; then
        echo "✅ 所有测试通过"
    else
        echo "⚠️  某些测试失败"
    fi
    echo ""
fi

# 启动应用
echo "🚀 启动后端服务..."
echo "   API基础URL: http://localhost:8080"
echo "   Swagger文档: http://localhost:8080/swagger-ui.html"
echo "   OpenAPI规范: http://localhost:8080/v3/api-docs"
echo ""
echo "按 Ctrl+C 停止服务"
echo ""

mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Xmx512m -Xms256m"
