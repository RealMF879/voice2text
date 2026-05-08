# VoiceToText - Android APP

一款使用AI技术将语音转换为文字的Android应用，支持实时语音识别和文字润色功能。

## 功能特性

- 🎤 **实时语音识别** - 使用豆包流式语音识别API
- ✨ **AI文字润色** - 使用Doubao-Seed-2.0-lite模型优化文本
- 📝 **文本编辑** - 支持编辑和复制识别结果
- ⚙️ **自定义配置** - 可配置多个服务商

## 技术栈

- Kotlin 1.9.22
- Jetpack Compose
- MVVM + Clean Architecture
- OkHttp 4.12（WebSocket + REST）
- Hilt依赖注入
- Android 7.0+ (API 24)

## 项目结构

```
voice/
├── app/
│   └── src/main/java/com/voicetotext/
│       ├── data/                      # 数据层
│       │   ├── local/                 # 本地存储
│       │   └── remote/                # 远程服务
│       ├── di/                         # 依赖注入
│       ├── presentation/              # 展示层
│       │   ├── ui/                    # UI组件
│       │   └── viewmodel/             # ViewModel
│       └── util/                      # 工具类
├── .github/workflows/                  # GitHub Actions
└── gradle/                           # Gradle配置
```

## 快速开始

### 方法一：使用Android Studio

1. 克隆或下载本项目
2. 使用Android Studio打开项目
3. 等待Gradle同步完成
4. 运行 `assembleDebug` 构建Debug APK

### 方法二：使用GitHub Actions云端编译

无需安装任何软件，自动编译APK！

1. 将项目上传到GitHub
2. 点击 "Actions" 标签
3. 选择 "Build Android APK" workflow
4. 点击 "Run workflow"
5. 选择 APK 类型（debug/release）
6. 等待编译完成
7. 下载生成的 APK 文件

详细说明请查看 [编译指南](编译指南.md)

## API配置

使用前需要在应用中配置以下API：

### 语音识别服务（豆包）

- **接口地址**: `https://openspeech.bytedance.com/api/v2/asr`
- **API密钥**: 火山引擎Access Token
- **模型名称**: `volc_zh_v3_streaming`

### AI润色服务

- **接口地址**: `https://ark.cn-beijing.volces.com/api/v3`
- **API密钥**: 火山引擎ARK API Key
- **模型名称**: `doubao-seed-2.0-lite`

详细配置说明请查看 [编译指南](编译指南.md)

## 使用说明

1. 首次使用需要配置API密钥
2. 点击主页面右上角设置图标
3. 填写语音识别和AI润色服务的配置信息
4. 保存配置后返回主页面
5. 点击麦克风按钮开始录音
6. 说话完成后点击完成按钮
7. 点击"AI润色"按钮进行文字优化
8. 润色后的文本将显示在文本框中

## 编译

### Debug版本
```bash
./gradlew assembleDebug
```

### Release版本
```bash
./gradlew assembleRelease
```

### 云端编译
使用GitHub Actions自动编译，无需本地环境。

## 开发

### 环境要求

- JDK 17+
- Android Studio Hedgehog (2023.1.1)+
- Android SDK Platform 34

### 依赖

所有依赖已配置在 `app/build.gradle` 中。

## 许可证

本项目仅供学习交流使用。

## 联系方式

如有问题或建议，请通过GitHub Issues反馈。
