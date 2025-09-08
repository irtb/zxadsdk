# MaxRTB 广告SDK - 完整开发部署指南

## 目录
1. [环境准备](#环境准备)
2. [创建SDK项目](#创建sdk项目)
3. [项目结构说明](#项目结构说明)
4. [代码实现](#代码实现)
5. [配置文件设置](#配置文件设置)
6. [打包SDK](#打包sdk)
7. [集成到APP](#集成到app)
8. [测试和发布](#测试和发布)
9. [常见问题](#常见问题)

## 环境准备

### 1. 安装Android Studio
1. 访问 [Android开发者官网](https://developer.android.com/studio)
2. 下载最新版本的Android Studio
3. 安装时选择标准安装，包含Android SDK
4. 首次启动时，等待SDK组件下载完成

### 2. 配置开发环境
1. 打开Android Studio
2. 进入 `File > Settings` (Windows) 或 `Android Studio > Preferences` (Mac)
3. 导航到 `Appearance & Behavior > System Settings > Android SDK`
4. 确保已安装以下组件：
   - Android SDK Platform 33 (或最新版本)
   - Android SDK Build-Tools 33.0.0 (或最新版本)
   - Android SDK Platform-Tools
   - Google Play services

## 创建SDK项目

### 第一步：创建新项目
1. 打开Android Studio
2. 选择 `File > New > New Project`
3. 选择 `Empty Activity`
4. 填写项目信息：
   - **Name**: `MaxRTBAdSDK`
   - **Package name**: `com.maxrtb.sdk`
   - **Save location**: 选择你的工作目录
   - **Language**: `Kotlin`
   - **Minimum SDK**: `API 21: Android 5.0 (Lollipop)`
5. 点击 `Finish`

### 第二步：转换为Library项目
1. 等待项目创建完成
2. 找到 `app/build.gradle` 文件
3. 将第一行从 `plugins { id 'com.android.application' }` 
   改为 `plugins { id 'com.android.library' }`
4. 删除 `defaultConfig` 中的 `applicationId` 行

## 项目结构说明

创建完成后，你的项目结构应该如下：

```
MaxRTBAdSDK/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/maxrtb/sdk/
│   │   │   │   ├── model/          # 数据模型
│   │   │   │   ├── network/        # 网络层
│   │   │   │   ├── renderer/       # 广告渲染器
│   │   │   │   ├── callback/       # 回调接口
│   │   │   │   ├── utils/          # 工具类
│   │   │   │   └── MaxRTBAdSDK.kt  # SDK主类
│   │   │   ├── res/
│   │   │   │   ├── layout/         # 布局文件
│   │   │   │   ├── drawable/       # 图标资源
│   │   │   │   └── values/         # 配置文件
│   │   │   └── AndroidManifest.xml
│   │   └── androidTest/
│   └── build.gradle
├── gradle/
├── build.gradle
└── settings.gradle
```

## 代码实现

### 第一步：创建包结构
在Android Studio中，右键点击 `app/src/main/java/com/maxrtb/sdk/`，依次创建以下包：
- `model`
- `network` 
- `renderer`
- `callback`
- `utils`

### 第二步：添加代码文件
按照以下顺序创建和添加代码文件：

#### 1. 数据模型 (model包)
创建以下文件并复制对应代码：
- `AdType.kt`
- `AdResponse.kt` 
- `AdRequest.kt`

#### 2. 网络层 (network包)  
- `ApiService.kt`
- `NetworkManager.kt`

#### 3. 回调接口 (callback包)
- `AdCallback.kt`
- `AdLoadCallback.kt`

#### 4. 工具类 (utils包)
- `DeviceUtil.kt`
- `NetworkUtil.kt`
- `DensityUtil.kt`
- `AdTracker.kt`

#### 5. 渲染器 (renderer包)
- `UniversalAdRenderer.kt`

#### 6. SDK主类
- `MaxRTBAdSDK.kt`

**注意**: 每个文件的具体代码内容请参考之前提供的完整代码。

## 配置文件设置

### 1. app/build.gradle
将文件内容替换为：

```gradle
plugins {
    id 'com.android.library'
    id 'org.jetbrains.kotlin.android'
}

android {
    namespace 'com.maxrtb.sdk'
    compileSdk 34

    defaultConfig {
        minSdk 21
        targetSdk 34
        versionCode 1
        versionName "1.0.0"

        testInstrumentationRunner "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles "consumer-rules.pro"
    }

    buildTypes {
        release {
            minifyEnabled false
            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
        }
    }
    
    compileOptions {
        sourceCompatibility JavaVersion.VERSION_1_8
        targetCompatibility JavaVersion.VERSION_1_8
    }
    
    kotlinOptions {
        jvmTarget = '1.8'
    }
}

dependencies {
    // Android基础库
    implementation 'androidx.core:core-ktx:1.12.0'
    implementation 'androidx.appcompat:appcompat:1.6.1'
    implementation 'com.google.android.material:material:1.10.0'
    implementation 'androidx.constraintlayout:constraintlayout:2.1.4'
    implementation 'androidx.cardview:cardview:1.0.0'

    // 网络请求
    implementation 'com.squareup.retrofit2:retrofit:2.9.0'
    implementation 'com.squareup.retrofit2:converter-gson:2.9.0'
    implementation 'com.squareup.okhttp3:logging-interceptor:4.11.0'
    implementation 'com.google.code.gson:gson:2.10.1'

    // 图片加载
    implementation 'com.github.bumptech.glide:glide:4.15.1'

    // 视频播放
    implementation 'com.google.android.exoplayer:exoplayer:2.19.1'

    // 协程
    implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3'

    // 测试依赖
    testImplementation 'junit:junit:4.13.2'
    androidTestImplementation 'androidx.test.ext:junit:1.1.5'
    androidTestImplementation 'androidx.test.espresso:espresso-core:3.5.1'
}
```

### 2. app/src/main/AndroidManifest.xml
替换为：

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android">

    <!-- 网络权限 -->
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
    <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
    
    <!-- 设备信息权限 -->
    <uses-permission android:name="android.permission.READ_PHONE_STATE" />
    
    <!-- 浮窗权限（可选，用于浮窗广告） -->
    <uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW" />
    
    <!-- 允许应用获取设备信息 -->
    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />

</manifest>
```

### 3. 创建布局文件
在 `app/src/main/res/layout/` 目录下创建以下布局文件：

#### ad_banner_layout.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<FrameLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:background="#F0F0F0">

    <ImageView
        android:id="@+id/banner_image"
        android:layout_width="match_parent"
        android:layout_height="60dp"
        android:scaleType="centerCrop" />

    <TextView
        android:id="@+id/banner_title"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_gravity="bottom|start"
        android:padding="8dp"
        android:textColor="@android:color/white"
        android:textSize="12sp"
        android:background="#80000000" />

    <ImageButton
        android:id="@+id/banner_close"
        android:layout_width="24dp"
        android:layout_height="24dp"
        android:layout_gravity="top|end"
        android:layout_margin="4dp"
        android:background="?attr/selectableItemBackgroundBorderless"
        android:src="@android:drawable/ic_menu_close_clear_cancel" />

</FrameLayout>
```

继续创建其他布局文件...（由于篇幅限制，请创建相应的布局文件）

### 4. 添加图标资源
在 `app/src/main/res/drawable/` 目录下，你需要添加以下图标：
- `ic_close.xml` - 关闭按钮图标
- `ic_volume_on.xml` - 音量开启图标
- `ic_volume_off.xml` - 音量关闭图标

可以从Android Studio的Vector Asset Studio生成这些图标。

## 打包SDK

### 第一步：清理和构建项目
1. 在Android Studio中选择 `Build > Clean Project`
2. 等待清理完成后，选择 `Build > Rebuild Project`
3. 确保没有编译错误

### 第二步：生成AAR文件
1. 在Android Studio底部的Terminal中执行：
```bash
./gradlew assembleRelease
```

2. 或者通过界面操作：
   - 点击右侧的 `Gradle` 面板
   - 展开 `MaxRTBAdSDK > app > Tasks > build`
   - 双击 `assembleRelease`

### 第三步：找到生成的AAR文件
构建完成后，AAR文件位于：
```
app/build/outputs/aar/app-release.aar
```

将此文件重命名为 `maxrtb-ad-sdk-1.0.0.aar`

## 集成到APP

### 方式一：本地AAR集成

#### 1. 在APP项目中添加AAR
1. 在你的APP项目根目录创建 `libs` 文件夹
2. 将 `maxrtb-ad-sdk-1.0.0.aar` 复制到 `libs` 文件夹
3. 在APP的 `app/build.gradle` 中添加：

```gradle
dependencies {
    implementation files('libs/maxrtb-ad-sdk-1.0.0.aar')
    
    // SDK依赖的第三方库
    implementation 'com.squareup.retrofit2:retrofit:2.9.0'
    implementation 'com.squareup.retrofit2:converter-gson:2.9.0'
    implementation 'com.squareup.okhttp3:logging-interceptor:4.11.0'
    implementation 'com.github.bumptech.glide:glide:4.15.1'
    implementation 'com.google.android.exoplayer:exoplayer:2.19.1'
    implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3'
}
```

#### 2. 添加权限
在APP的 `AndroidManifest.xml` 中添加必要权限：

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.READ_PHONE_STATE" />
<uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW" />
```

#### 3. 在APP中使用SDK
```kotlin
import com.maxrtb.sdk.MaxRTBAdSDK
import com.maxrtb.sdk.model.AdType
import com.maxrtb.sdk.callback.AdCallback

class MainActivity : AppCompatActivity() {
    
    private lateinit var adSDK: MaxRTBAdSDK
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        // 初始化SDK
        adSDK = MaxRTBAdSDK.init(this, "your_app_id")
        
        // 显示广告
        showBannerAd()
    }
    
    private fun showBannerAd() {
        val bannerContainer = findViewById<FrameLayout>(R.id.banner_container)
        
        adSDK.loadAndShowAd(
            adSlotId = "banner_slot_001",
            adType = AdType.BANNER,
            container = bannerContainer,
            callback = object : AdCallback {
                override fun onAdShow() {
                    Log.d("Ad", "Banner显示成功")
                }
                
                override fun onAdClick() {
                    Log.d("Ad", "Banner被点击")
                }
                
                override fun onAdClose() {
                    Log.d("Ad", "Banner被关闭")
                }
                
                override fun onAdError(error: String) {
                    Log.e("Ad", "Banner错误: $error")
                }
            }
        )
    }
    
    override fun onDestroy() {
        super.onDestroy()
        adSDK.release()
    }
}
```

### 方式二：发布到Maven仓库（推荐）

#### 1. 配置发布脚本
在SDK项目的 `app/build.gradle` 末尾添加：

```gradle
apply plugin: 'maven-publish'

publishing {
    publications {
        release(MavenPublication) {
            from components.release
            
            groupId = 'com.maxrtb'
            artifactId = 'ad-sdk'
            version = '1.0.0'
        }
    }
}
```

#### 2. 发布到本地Maven仓库
```bash
./gradlew publishToMavenLocal
```

#### 3. 在APP中集成
在APP的 `app/build.gradle` 中添加：

```gradle
dependencies {
    implementation 'com.maxrtb:ad-sdk:1.0.0'
}
```

## 测试和发布

### 1. 单元测试
创建测试类验证SDK功能：

```kotlin
// app/src/test/java/com/maxrtb/sdk/MaxRTBAdSDKTest.kt
class MaxRTBAdSDKTest {
    
    @Test
    fun testSDKInitialization() {
        // 测试SDK初始化
    }
    
    @Test
    fun testAdRequest() {
        // 测试广告请求
    }
}
```

### 2. 集成测试
创建一个测试APP项目：

1. 创建新的Android项目
2. 集成你的SDK
3. 测试各种广告类型的展示
4. 验证点击、关闭等交互功能

### 3. 性能测试
- 测试广告加载速度
- 内存使用情况监控
- 网络请求效率测试

### 4. 发布准备

#### 版本管理
在发布前：
1. 更新 `build.gradle` 中的版本号
2. 更新 `README.md` 中的版本信息
3. 创建版本发布说明

#### 混淆配置
在 `proguard-rules.pro` 中添加：

```proguard
# 保持SDK公开API不被混淆
-keep class com.maxrtb.sdk.MaxRTBAdSDK { *; }
-keep class com.maxrtb.sdk.model.** { *; }
-keep class com.maxrtb.sdk.callback.** { *; }

# 保持网络相关类
-keep class retrofit2.** { *; }
-keep class com.google.gson.** { *; }
```

## 常见问题

### Q1: 编译时出现依赖冲突怎么办？
A: 检查依赖版本兼容性，使用以下命令查看依赖树：
```bash
./gradlew app:dependencies
```

### Q2: 广告无法显示？
A: 检查以下几点：
- 网络权限是否添加
- API地址是否正确
- 广告位ID是否有效
- 网络连接是否正常

### Q3: 如何调试网络请求？
A: 在 `NetworkManager.kt` 中启用日志拦截器，查看请求详情。

### Q4: 视频广告播放失败？
A: 检查：
- ExoPlayer依赖是否正确
- 视频URL是否有效
- 设备是否支持该视频格式

### Q5: 如何处理权限申请？
A: 对于需要特殊权限的广告类型（如浮窗），在显示前检查并申请权限：

```kotlin
if (Settings.canDrawOverlays(context)) {
    // 显示浮窗广告
} else {
    // 申请权限
    val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
    context.startActivity(intent)
}
```

## 技术支持

- **文档**: 查看本README.md
- **示例代码**: 参考示例APP项目  
- **问题反馈**: 提交Issue到项目仓库
- **联系方式**: 技术支持邮箱

## 版本历史

### v1.0.0 (2025-01-15)
- 初始版本发布
- 支持12种广告类型
- 完整的广告渲染系统
- 网络请求和追踪功能

---

**注意**: 本文档适用于Android SDK API 21及以上版本。在生产环境使用前，请确保充分测试所有功能。