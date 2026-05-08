# GitHub手动修复指南

## 问题描述
GitHub Actions编译失败，错误为：
```
e: Unresolved reference: Model
```
原因：`SettingsScreen.kt` 中使用了不存在的 `Icons.Default.Model`

## 解决方案

### 方法一：在GitHub网页直接编辑（推荐）

#### 步骤1：打开SettingsScreen.kt

在浏览器中访问：
```
https://github.com/RealMF879/voice2text/edit/main/app/src/main/java/com/voicetotext/presentation/ui/screens/SettingsScreen.kt
```

#### 步骤2：找到并修改两处错误

找到第213行和第306行：
```kotlin
Icon(Icons.Default.Model, contentDescription = null)
```

**改为：**
```kotlin
Icon(Icons.Default.Memory, contentDescription = null)
```

#### 步骤3：提交修改

点击绿色按钮 "Commit changes"

#### 步骤4：重新编译

1. 打开：https://github.com/RealMF879/voice2text/actions
2. 点击 "Build Android APK"
3. 点击 "Run workflow"
4. 选择 "debug"
5. 等待5-10分钟
6. 下载APK

---

### 方法二：等待网络恢复后推送

当网络恢复后，运行：
```powershell
cd d:\Desktop\voice
"C:\Program Files\Git\bin\git.exe" push origin main
```

---

## 修改内容

**文件：** `app/src/main/java/com/voicetotext/presentation/ui/screens/SettingsScreen.kt`

**修改1：** 第213行
```kotlin
// 之前（错误）
Icon(Icons.Default.Model, contentDescription = null)

// 之后（正确）
Icon(Icons.Default.Memory, contentDescription = null)
```

**修改2：** 第306行
```kotlin
// 之前（错误）
Icon(Icons.Default.Model, contentDescription = null)

// 之后（正确）
Icon(Icons.Default.Memory, contentDescription = null)
```

---

## 状态检查

本地待推送的提交：
- commit: 953c1db
- message: "Fix: Replace non-existent Icons.Default.Model with Icons.Default.Memory"
- branch: main
- ahead of origin/main by 1 commit

---

## 其他脚本文件

项目文件夹中还包含以下辅助脚本：
- `push-fix.ps1` - 推送修复脚本
- `push-to-github.ps1` - 推送脚本
- `推送修复.bat` - 中文推送批处理
- `自动推送.bat` - 自动重试推送

这些文件不影响主要功能，可以忽略或删除。

---

最后更新：2026-05-08
