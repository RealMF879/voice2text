# VoiceToText - 推送修复到GitHub
# 此脚本将修复推送到GitHub

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "正在推送修复到 GitHub..." -ForegroundColor Cyan
Write-Host "修复内容: Upgrade actions/upload-artifact v3 -> v4" -ForegroundColor Yellow
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

cd d:\Desktop\voice

& "C:\Program Files\Git\bin\git.exe" push origin main

if ($LASTEXITCODE -eq 0) {
    Write-Host ""
    Write-Host "========================================" -ForegroundColor Green
    Write-Host "✓ 推送成功！" -ForegroundColor Green
    Write-Host "========================================" -ForegroundColor Green
    Write-Host ""
    Write-Host "下一步：" -ForegroundColor Cyan
    Write-Host "1. 打开: https://github.com/RealMF879/voice2text/actions" -ForegroundColor White
    Write-Host "2. 点击 'Build Android APK'" -ForegroundColor White
    Write-Host "3. 点击 'Run workflow'" -ForegroundColor White
    Write-Host "4. 选择 'debug'" -ForegroundColor White
    Write-Host "5. 点击绿色按钮" -ForegroundColor White
    Write-Host "6. 等待5-10分钟编译完成" -ForegroundColor White
    Write-Host "7. 下载APK（Artifacts -> debug-apk）" -ForegroundColor White
} else {
    Write-Host ""
    Write-Host "✗ 推送失败，可能是网络问题" -ForegroundColor Red
    Write-Host "请稍后重新运行此脚本" -ForegroundColor Yellow
}

Write-Host ""
Write-Host "按任意键退出..." -ForegroundColor Gray
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")
