# VoiceToText - 语音转文字 Android APP

一款使用AI技术将语音转换为文字的Android应用，支持实时语音识别和文字润色功能。

## 功能特性

- 🎤 **实时语音识别** - 使用豆包流式语音识别API，通过WebSocket实时上传音频
- ✨ **AI文字润色** - 使用Doubao-Seed-2.0-lite模型优化文本
- 📝 **文本编辑** - 支持编辑和复制识别结果
- ⚙️ **自定义配置** - 可配置多个服务商（API地址、密钥、模型）
- 🔒 **本地存储** - 配置信息存储在本地
- 🌐 **无需登录** - 纯本地使用，无需注册账号

## 技术栈

- **语言**: Kotlin 1.9.22
- **UI框架**: Jetpack Compose
- **架构模式**: MVVM + Clean Architecture
- **网络请求**: OkHttp 4.12（WebSocket + REST API）
- **音频处理**: MediaRecorder（MP3格式）
- **依赖注入**: Hilt
- **最小SDK**: Android 7.0 (API 24)
- **目标SDK**: Android 14 (API 34)

## 项目结构

```
voice/
├── app/
│   ├── src/main/
│   │   ├── java/com/voicetotext/
│   │   │   ├── VoiceToTextApplication.kt    # 应用入口
│   │   │   ├── data/                        # 数据层
│   │   │   │   ├── local/
│   │   │   │   │   └── PreferencesManager.kt  # SharedPreferences管理
│   │   │   │   └── remote/
│   │   │   │       ├── SpeechRecognitionService.kt  # 语音识别（WebSocket）
│   │   │   │       └── TextPolishingService.kt      # AI润色（REST）
│   │   │   ├── di/
│   │   │   │   └── AppModule.kt             # Hilt依赖注入
│   │   │   ├── presentation/                # 展示层
│   │   │   │   ├── MainActivity.kt          # 主Activity
│   │   │   │   ├── navigation/
│   │   │   │   │   └── AppNavigation.kt     # Compose导航
│   │   │   │   ├── ui/
│   │   │   │   │   ├── screens/
│   │   │   │   │   │   ├── MainScreen.kt    # 主页面
│   │   │   │   │   │   └── SettingsScreen.kt # 设置页面
│   │   │   │   │   └── theme/               # Compose主题
│   │   │   │   └── viewmodel/
│   │   │   │       ├── MainViewModel.kt     # 主页面ViewModel
│   │   │   │       └── SettingsViewModel.kt # 设置页ViewModel
│   │   │   └── util/
│   │   │       └── AudioRecorder.kt         # 音频录制工具
│   │   ├── res/                             # Android资源
│   │   └── AndroidManifest.xml              # 应用清单
│   └── build.gradle                          # 应用构建配置
├── .github/
│   └── workflows/
│       └── build.yml                        # GitHub Actions云编译
├── gradle/                                  # Gradle wrapper
├── build.gradle                             # 项目构建配置
├── settings.gradle                          # 项目设置
├── gradle.properties                        # Gradle属性
├── .gitignore                               # Git忽略配置
└── README.md                                # 项目说明
```

## 快速开始

### 方法一：使用Android Studio（本地编译）

1. 克隆或下载本项目到本地
2. 使用 Android Studio 打开项目
3. 等待 Gradle 同步完成（首次需要下载依赖）
4. 运行 `assembleDebug` 构建 Debug APK

### 方法二：使用GitHub Actions云端编译（推荐）

**无需安装任何软件！** 整个编译过程在云端自动完成。

#### 操作步骤：

1. **将项目上传到GitHub**
   - 创建新的GitHub仓库
   - 把 `d:\Desktop\voice` 文件夹的所有内容上传

2. **配置GitHub Secrets（仅发布版本需要）**
   - 如果只是编译Debug APK，可跳过此步
   - 在GitHub仓库的 Settings → Secrets 添加：
     - `KEYSTORE_FILE`: 签名的keystore文件（Base64编码）
     - `KEYSTORE_PASSWORD`: keystore密码
     - `KEY_ALIAS`: 密钥别名
     - `KEY_PASSWORD`: 密钥密码

3. **触发编译**
   - 进入仓库的 **Actions** 页面
   - 选择左侧 **"Build Android APK"** workflow
   - 点击 **"Run workflow"** 按钮
   - 选择 APK 类型：
     - `debug`: Debug版本（推荐，无需签名）
     - `release`: Release版本（需要签名配置）
   - 点击绿色按钮启动编译

4. **等待编译完成**
   - 编译通常需要 5-10 分钟
   - 可以在 Actions 页面查看实时进度

5. **下载APK**
   - 编译完成后，点击 workflow 运行记录
   - 在 "Artifacts" 部分下载 APK 文件
   - Debug APK 命名：`app-debug.apk`
   - Release APK 命名：`app-release.apk`

#### GitHub Actions 特点：

✅ **免费** - GitHub Actions 每月提供 2000 分钟免费编译时间  
✅ **自动** - 只需点击一次按钮  
✅ **快速** - 云端服务器比普通电脑快  
✅ **可靠** - 每次编译环境一致  
✅ **可追溯** - 保留编译历史记录  

详细说明请查看 [编译指南.md](编译指南.md)

## API配置说明

使用APP前需要在"设置"页面配置以下API：

### 1. 语音识别服务（豆包流式语音识别）

参考文档：https://www.volcengine.com/docs/6561/1354869

- **接口地址**: `https://openspeech.bytedance.com/api/v2/asr`
- **API密钥**: 火山引擎Access Token
- **模型名称**: `volc_zh_v3_streaming`

### 2. AI润色服务（Doubao-Seed-2.0-lite）

参考文档：https://www.volcengine.com/docs/82379/1494384

- **接口地址**: `https://ark.cn-beijing.volces.com/api/v3`
- **API密钥**: 火山引擎ARK API Key
- **模型名称**: `doubao-seed-2.0-lite`

### 配置步骤：

1. 注册火山引擎账号：https://www.volcengine.com/
2. 开通语音识别服务，获取Access Token
3. 开通ARK API服务，获取API Key
4. 打开APP，点击右上角 ⚙️ 设置图标
5. 填写上述配置信息
6. 点击"保存配置"
7. 返回主页面开始使用

详细配置说明请查看 [编译指南.md](编译指南.md)

## 使用说明

### 基本操作流程：

1. **首次使用**
   - 打开APP
   - 如果提示"配置不完整"，先配置API密钥
   - 点击红色警告卡片中的"前往设置"

2. **配置API**
   - 进入设置页面
   - 填写语音识别服务商信息
   - 填写AI润色服务商信息
   - 点击"保存配置"

3. **语音识别**
   - 返回主页面
   - 点击底部圆形麦克风按钮
   - 对着手机说话
   - 界面会实时显示"录音中"和识别状态
   - 说话完成后，点击"完成"按钮（绿色方块）
   - 识别结果会显示在状态区域

4. **AI润色**
   - 识别完成后，点击"✨ AI润色"按钮
   - 等待AI处理（通常几秒钟）
   - 润色后的文本会显示在文本编辑框中

5. **编辑和复制**
   - 可以直接在文本框中编辑结果
   - 长按文本可复制到剪贴板

### 界面说明：

- **顶部标题栏**: 显示"🎙️ 语音转文字"，右侧有设置按钮
- **中间文本框**: 显示识别和润色后的文本
- **状态卡片**: 显示录音状态、时长、识别进度
- **底部按钮区**: 
  - 麦克风图标：开始录音
  - 绿色方块：完成录音
  - 红色叉号：取消录音
  - ✨ AI润色：润色文本

## 本地编译

### 环境要求

- JDK 17 或更高版本
- Android Studio Hedgehog (2023.1.1) 或更高
- Android SDK Platform 34
- Android SDK Build-Tools

### 编译命令

#### Debug版本：
```bash
cd d:\Desktop\voice
./gradlew assembleDebug
```

#### Release版本：
```bash
./gradlew assembleRelease
```

#### 清理并重新编译：
```bash
./gradlew clean assembleDebug
```

### APK位置

- Debug: `app/build/outputs/apk/debug/app-debug.apk`
- Release: `app/build/outputs/apk/release/app-release.apk`

## 开发说明

### 技术亮点

1. **WebSocket流式识别**
   - 使用 OkHttp WebSocket 实现实时语音识别
   - 边录音边上传统发，提高响应速度

2. **MVVM架构**
   - ViewModel 管理UI状态
   - StateFlow 实现响应式编程
   - 清晰的数据流和业务逻辑分离

3. **Hilt依赖注入**
   - 统一管理依赖
   - 便于单元测试
   - 降低组件耦合

4. **Jetpack Compose**
   - 声明式UI
   - 现代化的开发体验
   - 优秀的动画支持

### 扩展建议

- 添加历史记录功能
- 支持多种语言识别
- 添加导出功能（TXT、Word等）
- 实现云端配置同步
- 添加深色模式
- 支持自定义润色Prompt

## 常见问题

详见 [编译指南.md](编译指南.md) 中的"常见问题"章节。

## 项目信息

- **版本**: 1.0.0
- **创建日期**: 2026-05-08
- **许可证**: MIT License
- **作者**: VoiceToText Team

## 更新日志

### v1.0.0 (2026-05-08)
- ✅ 完成基础功能开发
- ✅ 实现语音识别（WebSocket）
- ✅ 实现AI润色（REST API）
- ✅ 实现配置管理
- ✅ 实现Jetpack Compose UI
- ✅ 支持GitHub Actions云编译

## 贡献

欢迎提交 Issue 和 Pull Request！

## 致谢

- 火山引擎 - 提供语音识别和AI模型API
- Jetpack Compose Team - 优秀的UI框架
- Hilt Team - 依赖注入解决方案

---

**开始使用：**
1. 将项目上传到GitHub
2. 使用GitHub Actions编译APK
3. 配置API密钥
4. 安装并享受语音转文字的便捷！
