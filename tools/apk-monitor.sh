#!/bin/bash

# APK 构建监听器
# 监控 GitHub Actions 构建状态，构建成功时发送通知

REPO="wyhnotwhy/how2hao"
WORKFLOW_NAME="Build APK"
LAST_BUILD_ID_FILE=".last_build_id"

# 颜色定义
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

echo -e "${BLUE}========================================${NC}"
echo -e "${BLUE}   APK 构建监听器${NC}"
echo -e "${BLUE}========================================${NC}"
echo ""

# 检查 jq 是否安装
if ! command -v jq &> /dev/null; then
    echo -e "${YELLOW}提示：安装 jq 可以获得更好的格式化输出${NC}"
    echo "Ubuntu/Debian: sudo apt-get install jq"
    echo "macOS: brew install jq"
    echo ""
fi

# 获取最近的构建
fetch_latest_build() {
    curl -s "https://api.github.com/repos/${REPO}/actions/runs?per_page=1" 2>/dev/null
}

# 解析构建信息
parse_build_info() {
    local json="$1"
    
    if command -v jq &> /dev/null; then
        echo "$json" | jq -r '.workflow_runs[0] | "\(.id)|\(.status)|\(.conclusion)|\(.html_url)|\(.run_number)"'
    else
        # 简单的字符串提取
        local id=$(echo "$json" | grep -o '"id": [0-9]*' | head -1 | grep -o '[0-9]*')
        local status=$(echo "$json" | grep -o '"status": "[^"]*"' | head -1 | cut -d'"' -f4)
        local conclusion=$(echo "$json" | grep -o '"conclusion": "[^"]*"' | head -1 | cut -d'"' -f4)
        local url=$(echo "$json" | grep -o '"html_url": "[^"]*"' | head -1 | cut -d'"' -f4)
        local number=$(echo "$json" | grep -o '"run_number": [0-9]*' | head -1 | grep -o '[0-9]*')
        echo "${id}|${status}|${conclusion}|${url}|${number}"
    fi
}

# 获取 APK 下载链接
get_apk_url() {
    local run_id="$1"
    local artifacts_json=$(curl -s "https://api.github.com/repos/${REPO}/actions/runs/${run_id}/artifacts" 2>/dev/null)
    
    if command -v jq &> /dev/null; then
        echo "$artifacts_json" | jq -r '.artifacts[0] | "\(.name): \(.archive_download_url)"'
    else
        echo "$artifacts_json" | grep -o '"archive_download_url": "[^"]*"' | head -1 | cut -d'"' -f4
    fi
}

# 发送通知
send_notification() {
    local run_id="$1"
    local url="$2"
    local run_number="$3"
    
    echo ""
    echo -e "${GREEN}========================================${NC}"
    echo -e "${GREEN}   ✅ APK 构建成功！${NC}"
    echo -e "${GREEN}========================================${NC}"
    echo ""
    echo -e "📱 ${YELLOW}构建编号：${NC}#${run_number}"
    echo -e "🔗 ${YELLOW}构建页面：${NC}${url}"
    echo ""
    echo -e "📥 ${YELLOW}下载步骤：${NC}"
    echo "   1. 访问上面的构建页面"
    echo "   2. 页面底部找到 'Artifacts' 部分"
    echo "   3. 点击 'app-debug' 下载 APK"
    echo ""
    echo -e "${GREEN}========================================${NC}"
    
    # 保存最后通知的构建 ID
    echo "$run_id" > "$LAST_BUILD_ID_FILE"
}

# 主循环
main() {
    local last_notified_id=""
    
    # 读取上次通知的构建 ID
    if [ -f "$LAST_BUILD_ID_FILE" ]; then
        last_notified_id=$(cat "$LAST_BUILD_ID_FILE")
    fi
    
    echo "开始监控仓库：${REPO}"
    echo "工作流：${WORKFLOW_NAME}"
    echo ""
    
    if [ -n "$last_notified_id" ]; then
        echo "上次通知的构建 ID: ${last_notified_id}"
        echo ""
    fi
    
    # 获取最新构建
    echo "检查最新构建状态..."
    local build_json=$(fetch_latest_build)
    
    if [ -z "$build_json" ] || [ "$build_json" = "null" ]; then
        echo -e "${RED}错误：无法获取构建信息${NC}"
        echo "请检查网络连接或仓库地址是否正确"
        exit 1
    fi
    
    # 解析构建信息
    local build_info=$(parse_build_info "$build_json")
    local run_id=$(echo "$build_info" | cut -d'|' -f1)
    local status=$(echo "$build_info" | cut -d'|' -f2)
    local conclusion=$(echo "$build_info" | cut -d'|' -f3)
    local url=$(echo "$build_info" | cut -d'|' -f4)
    local run_number=$(echo "$build_info" | cut -d'|' -f5)
    
    echo -e "最新构建状态：${YELLOW}${status}${NC}"
    
    if [ "$status" = "completed" ]; then
        if [ "$conclusion" = "success" ]; then
            if [ "$run_id" != "$last_notified_id" ]; then
                send_notification "$run_id" "$url" "$run_number"
            else
                echo ""
                echo -e "${BLUE}该构建已经通知过了${NC}"
                echo -e "构建页面：${url}"
            fi
        else
            echo ""
            echo -e "${RED}❌ 最新构建失败${NC}"
            echo -e "构建页面：${url}"
        fi
    else
        echo ""
        echo -e "${YELLOW}⏳ 构建正在进行中...${NC}"
        echo -e "构建页面：${url}"
        echo ""
        echo "可以使用以下命令持续监控："
        echo "  watch -n 30 ./tools/apk-monitor.sh"
    fi
}

# 运行主函数
main "$@"
