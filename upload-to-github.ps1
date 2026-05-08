# VoiceToText - GitHub上传工具
# 此脚本帮助您将项目一键上传到GitHub

param(
    [Parameter(Mandatory=$true)]
    [string]$GitHubUsername,
    
    [Parameter(Mandatory=$true)]
    [string]$RepositoryName
)

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "VoiceToText - GitHub上传工具" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# 检查Git是否安装
Write-Host "[1/6] 检查Git安装..." -ForegroundColor Yellow
try {
    $gitVersion = git --version
    Write-Host "✓ Git已安装: $gitVersion" -ForegroundColor Green
} catch {
    Write-Host "✗ Git未安装" -ForegroundColor Red
    Write-Host "请先安装Git: https://git-scm.com/downloads" -ForegroundColor Red
    exit 1
}

# 导航到项目目录
Write-Host ""
Write-Host "[2/6] 导航到项目目录..." -ForegroundColor Yellow
$projectPath = "d:\Desktop\voice"
Set-Location $projectPath
Write-Host "✓ 当前目录: $(Get-Location)" -ForegroundColor Green

# 初始化Git仓库
Write-Host ""
Write-Host "[3/6] 初始化Git仓库..." -ForegroundColor Yellow
if (Test-Path ".git") {
    Write-Host "✓ Git仓库已存在" -ForegroundColor Green
} else {
    git init
    git branch -M main
    Write-Host "✓ Git仓库初始化完成" -ForegroundColor Green
}

# 添加所有文件
Write-Host ""
Write-Host "[4/6] 添加文件到Git..." -ForegroundColor Yellow
git add .
Write-Host "✓ 文件已添加" -ForegroundColor Green

# 创建提交
Write-Host ""
Write-Host "[5/6] 创建提交..." -ForegroundColor Yellow
$commitMessage = "Initial commit: VoiceToText Android APP - 语音转文字应用"
git commit -m $commitMessage
Write-Host "✓ 提交完成" -ForegroundColor Green

# 添加远程仓库
Write-Host ""
Write-Host "[6/6] 连接GitHub仓库..." -ForegroundColor Yellow
$remoteUrl = "https://github.com/$GitHubUsername/$RepositoryName.git"

# 检查是否已有remote
$existingRemote = git remote get-url origin 2>$null
if ($existingRemote) {
    Write-Host "远程仓库已存在: $existingRemote" -ForegroundColor Yellow
    $confirm = Read-Host "是否更新远程仓库地址? (y/n)"
    if ($confirm -eq "y") {
        git remote set-url origin $remoteUrl
        Write-Host "✓ 远程仓库地址已更新" -ForegroundColor Green
    }
} else {
    git remote add origin $remoteUrl
    Write-Host "✓ 远程仓库已添加: $remoteUrl" -ForegroundColor Green
}

# 推送到GitHub
Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "准备推送到GitHub..." -ForegroundColor Cyan
Write-Host "仓库地址: $remoteUrl" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

$confirmPush = Read-Host "是否立即推送到GitHub? (y/n)"
if ($confirmPush -eq "y") {
    Write-Host ""
    Write-Host "正在推送...（可能需要输入GitHub用户名和密码/token）" -ForegroundColor Yellow
    
    try {
        git push -u origin main
        
        Write-Host ""
        Write-Host "========================================" -ForegroundColor Green
        Write-Host "✓ 推送成功！" -ForegroundColor Green
        Write-Host "========================================" -ForegroundColor Green
        Write-Host ""
        Write-Host "下一步操作：" -ForegroundColor Cyan
        Write-Host "1. 打开浏览器访问: https://github.com/$GitHubUsername/$RepositoryName" -ForegroundColor White
        Write-Host "2. 点击 'Actions' 标签" -ForegroundColor White
        Write-Host "3. 点击 'Build Android APK' workflow" -ForegroundColor White
        Write-Host "4. 点击 'Run workflow'" -ForegroundColor White
        Write-Host "5. 选择 'debug' 类型" -ForegroundColor White
        Write-Host "6. 等待编译完成（5-10分钟）" -ForegroundColor White
        Write-Host "7. 在 'Artifacts' 中下载APK" -ForegroundColor White
        Write-Host ""
    } catch {
        Write-Host ""
        Write-Host "✗ 推送失败" -ForegroundColor Red
        Write-Host "错误信息: $_" -ForegroundColor Red
        Write-Host ""
        Write-Host "可能的原因：" -ForegroundColor Yellow
        Write-Host "- GitHub用户名或仓库名错误" -ForegroundColor White
        Write-Host "- 未创建对应的GitHub仓库" -ForegroundColor White
        Write-Host "- GitHub访问权限不足" -ForegroundColor White
        Write-Host ""
        Write-Host "请先在GitHub网站上创建仓库：" -ForegroundColor Yellow
        Write-Host "https://github.com/new" -ForegroundColor Cyan
    }
} else {
    Write-Host ""
    Write-Host "已取消推送" -ForegroundColor Yellow
    Write-Host ""
    Write-Host "您可以稍后手动推送：" -ForegroundColor Cyan
    Write-Host "  cd $projectPath" -ForegroundColor White
    Write-Host "  git push -u origin main" -ForegroundColor White
}

Write-Host ""
Write-Host "感谢使用 VoiceToText 上传工具！" -ForegroundColor Cyan
Write-Host ""
