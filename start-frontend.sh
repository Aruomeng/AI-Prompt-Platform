#!/bin/bash

# AI提示词共享平台 - 前端启动脚本

echo "================================================"
echo "  AI提示词共享平台 - 前端启动脚本"
echo "================================================"
echo ""

# 检查Node.js
if ! command -v node &> /dev/null; then
    echo "❌ Node.js未安装，请先安装Node.js 16+"
    exit 1
fi

NODE_VERSION=$(node --version)
echo "✅ Node.js 版本: $NODE_VERSION"

# 检查npm
if ! command -v npm &> /dev/null; then
    echo "❌ npm未安装"
    exit 1
fi

NPM_VERSION=$(npm --version)
echo "✅ npm 版本: $NPM_VERSION"
echo ""

cd "$(dirname "$0")/frontend"

# 检查package.json
if [ ! -f "package.json" ]; then
    echo "❌ package.json 不存在"
    exit 1
fi

echo "================================================"
echo "  安装依赖"
echo "================================================"
echo ""

# 安装依赖
echo "📦 安装npm依赖..."
npm install

if [ $? -ne 0 ]; then
    echo "❌ 依赖安装失败"
    exit 1
fi

echo ""
echo "✅ 依赖安装成功"
echo ""

# 启动开发服务器
echo "================================================"
echo "  启动开发服务器"
echo "================================================"
echo ""

echo "🚀 启动前端服务..."
echo "   访问地址: http://localhost:5173"
echo "   后端API: http://localhost:8080"
echo ""
echo "按 Ctrl+C 停止服务"
echo ""

npm run dev
