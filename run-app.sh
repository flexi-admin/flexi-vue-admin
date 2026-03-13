#!/bin/bash

# 运行脚本：安装framework到本地仓库，检查8080端口，启动app程序，检查5173端口，启动前端项目

echo "开始执行app运行脚本..."

# 获取脚本所在目录的绝对路径
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
echo "脚本所在目录: $SCRIPT_DIR"

# 1. 安装framework到本地仓库
echo "步骤1：安装framework到本地仓库"
cd "$SCRIPT_DIR/backend/framework"
echo "当前目录: $(pwd)"
mvn clean install
if [ $? -ne 0 ]; then
    echo "安装framework失败，退出脚本"
    exit 1
fi

# 2. 检查8080端口是否被占用，如果被占用，先kill掉
echo "步骤2：检查8080端口是否被占用"
PORT=8080
PID=$(lsof -t -i:$PORT 2>/dev/null)
if [ -n "$PID" ]; then
    echo "端口 $PORT 被进程 $PID 占用，正在终止..."
    kill -9 $PID
    if [ $? -eq 0 ]; then
        echo "进程 $PID 已成功终止"
    else
        echo "终止进程 $PID 失败"
        exit 1
    fi
else
    echo "端口 $PORT 未被占用"
fi

# 3. 启动app程序
echo "步骤3：启动app程序"
cd "$SCRIPT_DIR/backend/app"
echo "当前目录: $(pwd)"
mvn spring-boot:run &

# 等待app启动完成
sleep 5

# 4. 检查5173端口是否被占用，如果被占用，先kill掉
echo "步骤4：检查5173端口是否被占用"
FRONTEND_PORT=5173
FRONTEND_PID=$(lsof -t -i:$FRONTEND_PORT 2>/dev/null)
if [ -n "$FRONTEND_PID" ]; then
    echo "端口 $FRONTEND_PORT 被进程 $FRONTEND_PID 占用，正在终止..."
    kill -9 $FRONTEND_PID
    if [ $? -eq 0 ]; then
        echo "进程 $FRONTEND_PID 已成功终止"
    else
        echo "终止进程 $FRONTEND_PID 失败"
        exit 1
    fi
else
    echo "端口 $FRONTEND_PORT 未被占用"
fi

# 5. 启动前端项目
echo "步骤5：启动前端项目"
cd "$SCRIPT_DIR/frontend"
echo "当前目录: $(pwd)"
npm run dev
