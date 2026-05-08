@echo off
chcp 65001 >nul
echo ========================================
echo 正在推送代码到 GitHub...
echo ========================================
echo.

cd /d d:\Desktop\voice

set retry=0
set max_retry=5

:retry_push
set /a retry+=1
echo.
echo 第 %retry% 次尝试推送...
echo.

"C:\Program Files\Git\bin\git.exe" push origin main

if %errorlevel% equ 0 (
    echo.
    echo ========================================
    echo ✓ 推送成功！
    echo ========================================
    echo.
    echo 下一步：
    echo 1. 打开: https://github.com/RealMF879/voice2text/actions
    echo 2. 点击 "Build Android APK"
    echo 3. 点击 "Run workflow"
    echo 4. 选择 "debug"
    echo 5. 等待5-10分钟
    echo 6. 下载APK
) else (
    if %retry% lss %max_retry% (
        echo.
        echo ✗ 推送失败，正在重试...
        timeout /t 5 >nul
        goto :retry_push
    ) else (
        echo.
        echo ========================================
        echo ✗ 推送失败，已尝试 %max_retry% 次
        echo ========================================
        echo.
        echo 请稍后再试，或手动复制以下命令：
        echo cd d:\Desktop\voice
        echo "C:\Program Files\Git\bin\git.exe" push origin main
    )
)

pause
