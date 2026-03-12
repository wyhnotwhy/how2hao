#!/usr/bin/env python3
"""
APK 构建成功通知器
监控 GitHub Actions 构建状态，成功后发送通知
"""

import json
import urllib.request
import urllib.error
import os
import sys
from datetime import datetime

# 配置
REPO = "wyhnotwhy/how2hao"
WORKFLOW_NAME = "Build APK"
LAST_BUILD_FILE = ".last_build_id"

# 颜色代码
class Colors:
    GREEN = '\033[92m'
    BLUE = '\033[94m'
    YELLOW = '\033[93m'
    RED = '\033[91m'
    END = '\033[0m'
    BOLD = '\033[1m'

def log(message, color=None):
    """打印带颜色的日志"""
    if color:
        print(f"{color}{message}{Colors.END}")
    else:
        print(message)

def fetch_latest_build():
    """获取最新的 GitHub Actions 构建"""
    url = f"https://api.github.com/repos/{REPO}/actions/runs?per_page=5"
    try:
        with urllib.request.urlopen(url, timeout=10) as response:
            return json.loads(response.read().decode())
    except Exception as e:
        log(f"获取构建信息失败: {e}", Colors.RED)
        return None

def fetch_artifacts(run_id):
    """获取构建产物信息"""
    url = f"https://api.github.com/repos/{REPO}/actions/runs/{run_id}/artifacts"
    try:
        with urllib.request.urlopen(url, timeout=10) as response:
            return json.loads(response.read().decode())
    except Exception as e:
        log(f"获取产物信息失败: {e}", Colors.RED)
        return None

def get_last_notified_id():
    """读取上次通知的构建 ID"""
    if os.path.exists(LAST_BUILD_FILE):
        try:
            with open(LAST_BUILD_FILE, 'r') as f:
                return f.read().strip()
        except:
            return None
    return None

def save_notified_id(run_id):
    """保存已通知的构建 ID"""
    try:
        with open(LAST_BUILD_FILE, 'w') as f:
            f.write(str(run_id))
    except Exception as e:
        log(f"保存构建 ID 失败: {e}", Colors.YELLOW)

def format_notification(build, artifacts):
    """格式化通知消息"""
    run_id = build['id']
    run_number = build['run_number']
    html_url = build['html_url']
    created_at = build['created_at']
    
    # 获取 APK 下载链接
    apk_url = None
    if artifacts and 'artifacts' in artifacts and len(artifacts['artifacts']) > 0:
        artifact = artifacts['artifacts'][0]
        apk_url = artifact.get('archive_download_url', '')
    
    message = f"""
{'='*50}
✅ APK 构建成功！
{'='*50}

📱 构建信息：
   • 仓库：{REPO}
   • 构建编号：#{run_number}
   • 构建 ID：{run_id}
   • 构建时间：{created_at}

🔗 下载地址：
   {html_url}

📥 下载步骤：
   1. 点击上面的链接访问构建页面
   2. 页面底部找到 "Artifacts" 部分
   3. 点击 "app-debug" 下载 APK

{'='*50}
"""
    return message

def send_notification(message):
    """发送通知（可以通过多种方式）"""
    log(message, Colors.GREEN)
    
    # 可以在这里添加其他通知方式：
    # - 发送到 Discord
    # - 发送到 Telegram
    # - 发送邮件
    # - 保存到文件
    
    # 保存到通知日志
    notification_file = "apk_notifications.log"
    timestamp = datetime.now().strftime("%Y-%m-%d %H:%M:%S")
    with open(notification_file, 'a', encoding='utf-8') as f:
        f.write(f"\n{'='*50}\n")
        f.write(f"通知时间: {timestamp}\n")
        f.write(f"{'='*50}\n")
        f.write(message)
        f.write("\n")
    
    log(f"\n💾 通知已保存到: {notification_file}", Colors.BLUE)

def main():
    log(f"{Colors.BLUE}{'='*50}{Colors.END}")
    log(f"{Colors.BLUE}   APK 构建监听器{Colors.END}")
    log(f"{Colors.BLUE}{'='*50}{Colors.END}")
    log("")
    
    # 获取最新构建
    log("正在检查最新构建状态...")
    data = fetch_latest_build()
    
    if not data or 'workflow_runs' not in data or len(data['workflow_runs']) == 0:
        log("未找到构建记录", Colors.YELLOW)
        return 1
    
    # 获取最新的构建
    latest_build = data['workflow_runs'][0]
    run_id = latest_build['id']
    status = latest_build['status']
    conclusion = latest_build.get('conclusion')
    
    log(f"最新构建状态: {Colors.YELLOW}{status}{Colors.END}")
    
    # 检查是否已经通知过
    last_id = get_last_notified_id()
    
    if status == 'completed':
        if conclusion == 'success':
            if str(run_id) != last_id:
                # 获取产物信息
                artifacts = fetch_artifacts(run_id)
                
                # 发送通知
                message = format_notification(latest_build, artifacts)
                send_notification(message)
                
                # 保存已通知的 ID
                save_notified_id(run_id)
                
                return 0
            else:
                log("\n该构建已经通知过了", Colors.BLUE)
                log(f"构建页面: {latest_build['html_url']}")
                return 0
        else:
            log(f"\n{Colors.RED}❌ 最新构建失败{Colors.END}")
            log(f"构建页面: {latest_build['html_url']}")
            return 1
    else:
        log(f"\n{Colors.YELLOW}⏳ 构建正在进行中...{Colors.END}")
        log(f"构建页面: {latest_build['html_url']}")
        log("\n提示：可以使用以下命令持续监控")
        log(f"  watch -n 30 python3 {sys.argv[0]}")
        return 2

if __name__ == '__main__':
    sys.exit(main())
