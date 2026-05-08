# VoiceToText - 推送代码到GitHub
# 此脚本将代码推送到GitHub仓库

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "正在推送代码到 GitHub..." -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

cd d:\Desktop\voice

Write-Host "正在推送到: https://github.com/RealMF879/voice2text.git" -ForegroundColor Yellow
Write-Host ""

& "C:\Program Files\Git\bin\git.exe" push -u origin main

if ($LASTEXITCODE -eq 0) {
    Write-Host ""
    Write-Host "========================================" -ForegroundColor Green
    Write-Host "✓ 推送成功！" -ForegroundColor Green
    Write-Host "========================================" -ForegroundColor Green
    Write-Host ""
    Write-Host "下一步操作：" -ForegroundColor Cyan
    Write-Host "1. 打开浏览器访问: https://github.com/RealMF879/voice2text" -ForegroundColor White
    Write-Host "2. 点击 'Actions' 标签" -ForegroundColor White
    Write-Host "3. 如果提示启用workflow，点击 'I understand my workflows, go ahead and enable them'" -ForegroundColor White
    Write-Host "4. 点击 'Build Android APK' workflow" -ForegroundColor White
    Write-Host "5. 点击 'Run workflow'" -ForegroundColor White
    Write-Host "6. 选择 'debug' 类型" -ForegroundColor White
    Write-Host "7. 点击绿色按钮启动编译" -ForegroundColor White
    Write-Host "8. 等待5-10分钟编译完成" -ForegroundColor White
    Write-Host "9. 在 'Artifacts' 中下载APK" -ForegroundColor White
} else {
    Write-Host ""
    Write-Host "✗ 推送失败" -ForegroundColor Red
    Write-Host ""
    Write-Host "可能的原因：" -ForegroundColor Yellow
    Write-Host "- 网络连接问题" -ForegroundColor White
    Write-Host "- GitHub仓库不存在" -ForegroundColor White
    Write-Host "- 权限问题" -ForegroundColor White
    Write-Host ""
    Write-Host "解决方案：" -ForegroundColor Yellow
    Write-Host "1. 确保已创建GitHub仓库: https://github.com/new" -ForegroundColor White
    Write-Host "   仓库名: voice2text" -ForegroundColor White
    Write-Host "2. 检查网络连接" -ForegroundColor White
    Write-Host "3. 稍后重新运行此脚本" -ForegroundColor White
}

Write-Host ""
Write-Host "按任意键退出..." -ForegroundColor Gray
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")
