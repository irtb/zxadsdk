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
# Android SDK打包发布完整指南

## 1. 生成AAR文件

### 1.1 配置SDK模块的build.gradle

```gradle
// maxrtbadsdk/build.gradle
plugins {
    id 'com.android.library'
    id 'org.jetbrains.kotlin.android'
    id 'maven-publish'  // 添加发布插件
}

android {
    namespace 'com.maxrtb.maxrtbadsdk'
    compileSdk 36

    defaultConfig {
        minSdk 21
        targetSdk 36
        versionCode 1
        versionName "1.0.0"
        
        testInstrumentationRunner "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles "consumer-rules.pro"
        
        // 添加构建信息
        buildConfigField "String", "VERSION_NAME", "\"${versionName}\""
        buildConfigField "long", "BUILD_TIME", "${System.currentTimeMillis()}L"
    }

    buildTypes {
        release {
            minifyEnabled false
            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
            consumerProguardFiles 'consumer-rules.pro'
        }
        debug {
            minifyEnabled false
            debuggable true
        }
    }

    // 生成源码JAR（可选）
    task androidSourcesJar(type: Jar) {
        archiveClassifier.set('sources')
        from android.sourceSets.main.java.source
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
    // SDK依赖项
    api 'androidx.core:core-ktx:1.12.0'
    api 'androidx.appcompat:appcompat:1.6.1'
    api 'com.google.android.material:material:1.11.0'
    api 'androidx.constraintlayout:constraintlayout:2.1.4'
    api 'androidx.cardview:cardview:1.0.0'
    
    // 网络请求
    api 'com.squareup.retrofit2:retrofit:2.9.0'
    api 'com.squareup.retrofit2:converter-gson:2.9.0'
    api 'com.squareup.okhttp3:logging-interceptor:4.12.0'
    api 'com.google.code.gson:gson:2.10.1'
    
    // 图片加载
    api 'com.github.bumptech.glide:glide:4.16.0'
    
    // 视频播放
    api 'com.google.android.exoplayer:exoplayer:2.19.1'
    
    // 协程
    api 'org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3'
    
    // 测试依赖
    testImplementation 'junit:junit:4.13.2'
    androidTestImplementation 'androidx.test.ext:junit:1.1.5'
    androidTestImplementation 'androidx.test.espresso:espresso-core:3.5.1'
}

// 发布配置
afterEvaluate {
    publishing {
        publications {
            release(MavenPublication) {
                from components.release
                
                groupId = 'com.maxrtb'
                artifactId = 'maxrtb-ad-sdk'
                version = android.defaultConfig.versionName
                
                // 添加源码JAR（可选）
                artifact androidSourcesJar
                
                pom {
                    name = 'MaxRTB Ad SDK'
                    description = 'Android advertising SDK for MaxRTB'
                    url = 'https://github.com/maxrtb/android-sdk'
                    
                    licenses {
                        license {
                            name = 'The Apache License, Version 2.0'
                            url = 'http://www.apache.org/licenses/LICENSE-2.0.txt'
                        }
                    }
                    
                    developers {
                        developer {
                            id = 'maxrtb'
                            name = 'MaxRTB Team'
                            email = 'sdk@maxrtb.com'
                        }
                    }
                }
            }
        }
    }
}
```

### 1.2 添加混淆配置

**创建 `maxrtbadsdk/consumer-rules.pro`：**
```proguard
# MaxRTB SDK混淆规则

# 保留SDK公开API
-keep public class com.maxrtb.maxrtbadsdk.** {
    public *;
}

# 保留回调接口
-keep interface com.maxrtb.maxrtbadsdk.callback.** { *; }

# 保留数据模型
-keep class com.maxrtb.maxrtbadsdk.model.** { *; }

# 保留枚举
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Gson相关
-keepattributes Signature
-keepattributes *Annotation*
-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.** { *; }

# Retrofit相关
-keepattributes RuntimeVisibleAnnotations
-keepattributes RuntimeVisibleParameterAnnotations
-keepattributes AnnotationDefault
-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}

# ExoPlayer相关
-keep class com.google.android.exoplayer2.** { *; }
-dontwarn com.google.android.exoplayer2.**

# Glide相关
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep class * extends com.bumptech.glide.module.AppGlideModule {
 <init>(...);
}
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}
```

### 1.3 构建AAR文件

在Terminal中执行：
```bash
# 构建Release版本AAR
./gradlew :maxrtbadsdk:assembleRelease

# AAR文件位置：
# maxrtbadsdk/build/outputs/aar/maxrtbadsdk-release.aar
```

## 2. 版本管理

### 2.1 语义化版本控制

```gradle
// 使用语义化版本号
def versionMajor = 1      // 重大更新
def versionMinor = 0      // 功能更新  
def versionPatch = 0      // 修复更新
def versionBuild = 1      // 构建号

android {
    defaultConfig {
        versionCode versionMajor * 1000000 + versionMinor * 10000 + versionPatch * 100 + versionBuild
        versionName "${versionMajor}.${versionMinor}.${versionPatch}"
    }
}
```

### 2.2 Git标签管理

```bash
# 创建版本标签
git tag -a v1.0.0 -m "Release version 1.0.0"
git push origin v1.0.0

# 查看所有标签
git tag -l
```

## 3. 发布到Maven仓库

### 3.1 发布到本地Maven仓库（测试用）

```bash
# 发布到本地仓库
./gradlew :zxadsdk:publishToMavenLocal

# 发布位置：~/.m2/repository/com/maxrtb/zx-ad-sdk/
```

### 3.2 发布到私有Maven仓库

**在项目根目录的build.gradle中添加：**
```gradle
allprojects {
    repositories {
        google()
        mavenCentral()
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/maxrtb/android-sdk")
            credentials {
                username = project.findProperty("gpr.user") ?: System.getenv("USERNAME")
                password = project.findProperty("gpr.key") ?: System.getenv("TOKEN")
            }
        }
    }
}
```

**发布到GitHub Packages：**
```gradle
// 在SDK模块的build.gradle中
publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/maxrtb/android-sdk")
            credentials {
                username = project.findProperty("gpr.user") ?: System.getenv("USERNAME")
                password = project.findProperty("gpr.key") ?: System.getenv("TOKEN")
            }
        }
    }
}
```

### 3.3 发布到JitPack（推荐）

**步骤1：确保项目在GitHub上**

**步骤2：在SDK模块添加JitPack配置：**
```gradle
// build.gradle (项目级别)
allprojects {
    repositories {
        google()
        mavenCentral()
        maven { url 'https://jitpack.io' }
    }
}
```

**步骤3：创建GitHub Release**
```bash
git tag v1.0.0
git push origin v1.0.0
```

**步骤4：在JitPack.io上构建**
访问 https://jitpack.io/#yourusername/yourrepo

## 4. 创建集成文档

### 4.1 README.md示例

```markdown
# MaxRTB Android SDK

## 集成指南

### 1. 添加依赖

在项目根目录的 `build.gradle` 中添加：
```gradle
allprojects {
    repositories {
        google()
        mavenCentral()
        maven { url 'https://jitpack.io' }
    }
}
```

在应用模块的 `build.gradle` 中添加：
```gradle
dependencies {
    implementation 'com.github.maxrtb:android-sdk:1.0.0'
}
```

### 2. 权限配置

在 `AndroidManifest.xml` 中添加：
```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW" />
```

### 3. 初始化SDK

```kotlin
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // 初始化SDK
        MaxRTBAdSDK.init(this, "your_app_id")
    }
}
```

### 4. 加载广告

```kotlin
// 加载Banner广告
adSDK.loadAndShowAd(
    adSlotId = "your_slot_id",
    adType = AdType.BANNER,
    container = bannerContainer,
    callback = object : AdCallback {
        override fun onAdShow() {
            // 广告显示
        }
        
        override fun onAdClick() {
            // 广告点击
        }
        
        override fun onAdClose() {
            // 广告关闭
        }
        
        override fun onAdError(error: String) {
            // 广告错误
        }
    }
)
```

### 5. 混淆配置

如果使用了代码混淆，请在 `proguard-rules.pro` 中添加：
```proguard
-keep class com.maxrtb.maxrtbadsdk.** { *; }
```

## API文档

### MaxRTBAdSDK

主要SDK类，提供广告加载和显示功能。

#### 方法

- `init(context: Context, appId: String): MaxRTBAdSDK` - 初始化SDK
- `loadAd(adSlotId: String, adType: AdType, callback: AdLoadCallback)` - 加载广告
- `loadAndShowAd(adSlotId: String, adType: AdType, container: ViewGroup?, callback: AdCallback)` - 加载并显示广告
- `release()` - 释放资源

### AdType

广告类型枚举：

- `BANNER` - 横幅广告
- `INTERSTITIAL` - 插屏广告
- `NATIVE` - 原生广告
- `SPLASH` - 开屏广告
- `REWARDED_VIDEO` - 激励视频广告
- `FEED` - 信息流广告

### 回调接口

#### AdCallback
- `onAdShow()` - 广告显示回调
- `onAdClick()` - 广告点击回调
- `onAdClose()` - 广告关闭回调
- `onAdError(error: String)` - 广告错误回调
- `onRewardEarned()` - 奖励获得回调（仅激励视频）

#### AdLoadCallback
- `onAdLoaded(adData: AdData)` - 广告加载成功回调
- `onAdLoadFailed(error: String)` - 广告加载失败回调

## 更新日志

### v1.0.0 (2024-01-01)
- 初始版本发布
- 支持多种广告类型
- 完整的错误处理机制

## 技术支持

如有问题请联系：sdk@maxrtb.com
```

## 5. 发布流程自动化

### 5.1 GitHub Actions自动发布

**创建 `.github/workflows/release.yml`：**
```yaml
name: Release SDK

on:
  release:
    types: [published]

jobs:
  build-and-publish:
    runs-on: ubuntu-latest
    
    steps:
    - uses: actions/checkout@v3
    
    - name: Set up JDK 11
      uses: actions/setup-java@v3
      with:
        java-version: '11'
        distribution: 'adopt'
    
    - name: Grant execute permission for gradlew
      run: chmod +x gradlew
    
    - name: Build AAR
      run: ./gradlew :maxrtbadsdk:assembleRelease
    
    - name: Upload AAR to Release
      uses: actions/upload-release-asset@v1
      env:
        GITHUB_TOKEN: ${{ secrets.GITHUB_TOKEN }}
      with:
        upload_url: ${{ github.event.release.upload_url }}
        asset_path: ./maxrtbadsdk/build/outputs/aar/maxrtbadsdk-release.aar
        asset_name: maxrtb-ad-sdk-${{ github.event.release.tag_name }}.aar
        asset_content_type: application/java-archive
    
    - name: Publish to Maven
      run: ./gradlew :maxrtbadsdk:publish
      env:
        USERNAME: ${{ github.actor }}
        TOKEN: ${{ secrets.GITHUB_TOKEN }}
```

## 6. 质量检查清单

### 发布前检查

- [ ] 所有功能测试通过
- [ ] API文档完整
- [ ] 混淆配置正确
- [ ] 版本号更新
- [ ] 更新日志撰写
- [ ] 示例代码验证
- [ ] 权限声明完整
- [ ] 依赖版本检查
- [ ] 性能测试通过
- [ ] 内存泄漏检查

### 发布后验证

- [ ] AAR文件可正常下载
- [ ] 集成文档准确
- [ ] 示例项目运行正常
- [ ] 反馈渠道畅通

## 7. 分发方式总结

### 7.1 直接分发AAR文件
- 优点：简单直接
- 缺点：版本管理困难，依赖处理复杂

### 7.2 Maven仓库分发（推荐）
- 优点：版本管理完善，依赖自动处理
- 分类：
   - JitPack（免费，推荐）
   - GitHub Packages
   - 私有Maven仓库
   - Maven Central（需要审核）

### 7.3 最佳实践
1. 使用JitPack进行公开发布
2. 提供完整的集成文档
3. 维护更新日志
4. 建立反馈机制
5. 定期版本更新

通过以上流程，你可以将调试好的SDK专业地打包发布，供其他开发者便捷集成使用。

// 1. zxadsdk/build.gradle.kts 配置文件
plugins {
id("com.android.library")
alias(libs.plugins.kotlin.android)
id("maven-publish")
}

android {
namespace = "com.maxrtb.zxadsdk"
compileSdk = 36

    defaultConfig {
        minSdk = 21
        targetSdk = 36
        versionCode = 1
        versionName = "1.0.0"
        
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
        
        // 添加构建信息
        buildConfigField("String", "VERSION_NAME", "\"${versionName}\"")
        buildConfigField("long", "BUILD_TIME", "${System.currentTimeMillis()}L")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            consumerProguardFiles("consumer-rules.pro")
        }
        debug {
            isMinifyEnabled = false
            isDebuggable = true
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }
    
    buildToolsVersion = "36.0.0"
    
    // 生成源码JAR
    tasks.register<Jar>("androidSourcesJar") {
        archiveClassifier.set("sources")
        from(android.sourceSets.getByName("main").java.srcDirs)
    }
}

dependencies {
// Android基础库
implementation(libs.androidx.core.ktx.v1120)
implementation(libs.androidx.appcompat)
implementation(libs.material)
implementation(libs.androidx.constraintlayout)
implementation(libs.androidx.cardview)

    // 网络请求
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.logging.interceptor)
    implementation(libs.gson)

    // 图片加载
    implementation(libs.glide)

    // 视频播放
    implementation(libs.exoplayer)

    // 协程
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.androidx.runtime)
    implementation(libs.ui)

    // 测试依赖
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit.v130)
    androidTestImplementation(libs.androidx.espresso.core.v370)

    implementation(libs.androidx.cardview)
    implementation(libs.exoplayer)
    implementation(libs.glide.v4160)
}

// 发布配置
afterEvaluate {
publishing {
publications {
create<MavenPublication>("release") {
from(components["release"])

                groupId = "com.maxrtb"
                artifactId = "zx-ad-sdk"
                version = android.defaultConfig.versionName
                
                // 添加源码JAR
                artifact(tasks.getByName("androidSourcesJar"))
                
                pom {
                    name.set("ZxAd SDK")
                    description.set("Android advertising SDK for ZxAd platform")
                    url.set("https://github.com/maxrtb/zx-ad-sdk")
                    
                    licenses {
                        license {
                            name.set("The Apache License, Version 2.0")
                            url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                        }
                    }
                    
                    developers {
                        developer {
                            id.set("maxrtb")
                            name.set("MaxRTB Team")
                            email.set("sdk@maxrtb.com")
                        }
                    }
                }
            }
        }
        
        repositories {
            maven {
                name = "GitHubPackages"
                url = uri("https://maven.pkg.github.com/maxrtb/zx-ad-sdk")
                credentials {
                    username = project.findProperty("gpr.user") as String? ?: System.getenv("USERNAME")
                    password = project.findProperty("gpr.key") as String? ?: System.getenv("TOKEN")
                }
            }
        }
    }
}

// 2. zxadsdk/consumer-rules.pro 混淆配置
/*
# ZxAd SDK 混淆规则

# 保留SDK公开API
-keep public class com.maxrtb.zxadsdk.** {
public *;
}

# 保留回调接口
-keep interface com.maxrtb.zxadsdk.callback.** { *; }

# 保留数据模型
-keep class com.maxrtb.zxadsdk.model.** { *; }

# 保留枚举
-keepclassmembers enum * {
public static **[] values();
public static ** valueOf(java.lang.String);
}

# Gson相关
-keepattributes Signature
-keepattributes *Annotation*
-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.** { *; }

# Retrofit相关
-keepattributes RuntimeVisibleAnnotations
-keepattributes RuntimeVisibleParameterAnnotations
-keepattributes AnnotationDefault
-keepclassmembers,allowshrinking,allowobfuscation interface * {
@retrofit2.http.* <methods>;
}

# ExoPlayer相关
-keep class com.google.android.exoplayer2.** { *; }
-dontwarn com.google.android.exoplayer2.**

# Glide相关
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep class * extends com.bumptech.glide.module.AppGlideModule {
<init>(...);
}
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
**[] $VALUES;
public *;
}

# 防止SDK内部类被混淆
-keep class com.maxrtb.zxadsdk.renderer.** { *; }
-keep class com.maxrtb.zxadsdk.network.** { *; }
-keep class com.maxrtb.zxadsdk.utils.** { *; }
*/

// 3. 项目根目录 settings.gradle.kts
pluginManagement {
repositories {
google()
mavenCentral()
gradlePluginPortal()
}
}

dependencyResolutionManagement {
repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
repositories {
google()
mavenCentral()
maven { url = uri("https://jitpack.io") }
}
}

rootProject.name = "ZxAdSDK"
include(":app")        // Demo应用
include(":zxadsdk")    // SDK库

// 4. 构建和发布命令
/*
# 构建AAR文件
./gradlew :zxadsdk:assembleRelease

# 发布到本地Maven仓库（测试用）
./gradlew :zxadsdk:publishToMavenLocal

# 发布到远程仓库
./gradlew :zxadsdk:publish

# 清理构建
./gradlew clean

# 完整构建流程
./gradlew clean :zxadsdk:assembleRelease :zxadsdk:publishToMavenLocal

# 生成的AAR文件位置：
# zxadsdk/build/outputs/aar/zxadsdk-release.aar
*/

// 5. 集成文档示例 - README.md
/*
# ZxAd Android SDK

## 快速集成

### 1. 添加依赖

在项目根目录的 `build.gradle` 中添加：
```gradle
allprojects {
    repositories {
        google()
        mavenCentral()
        maven { url 'https://jitpack.io' }
    }
}
```

在应用模块的 `build.gradle` 中添加：
```gradle
dependencies {
    implementation 'com.github.maxrtb:zx-ad-sdk:1.0.0'
}
```

### 2. 权限配置

在 `AndroidManifest.xml` 中添加：
```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW" />
```

### 3. 初始化SDK

```kotlin
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // 初始化SDK
        ZxAdSDK.init(this, "your_app_id")
    }
}
```

### 4. 加载广告示例

#### Banner广告
```kotlin
class MainActivity : AppCompatActivity() {
    private lateinit var adSDK: ZxAdSDK
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 初始化SDK
        adSDK = ZxAdSDK.init(this, "your_app_id")
        
        // 加载Banner广告
        adSDK.loadAndShowAd(
            adSlotId = "banner_slot_001",
            adType = AdType.BANNER,
            container = findViewById(R.id.banner_container),
            callback = object : AdCallback {
                override fun onAdShow() {
                    Log.d("Ad", "Banner ad shown")
                }
                
                override fun onAdClick() {
                    Log.d("Ad", "Banner ad clicked")
                }
                
                override fun onAdClose() {
                    Log.d("Ad", "Banner ad closed")
                }
                
                override fun onAdError(error: String) {
                    Log.e("Ad", "Banner ad error: $error")
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

#### 插屏广告
```kotlin
// 加载插屏广告
adSDK.loadAndShowAd(
    adSlotId = "interstitial_slot_001",
    adType = AdType.INTERSTITIAL,
    callback = object : AdCallback {
        override fun onAdShow() {
            // 广告显示
        }
        
        override fun onAdClick() {
            // 广告点击
        }
        
        override fun onAdClose() {
            // 广告关闭
        }
        
        override fun onAdError(error: String) {
            // 广告错误
        }
    }
)
```

#### 激励视频广告
```kotlin
// 加载激励视频广告
adSDK.loadAndShowAd(
    adSlotId = "rewarded_slot_001",
    adType = AdType.REWARDED_VIDEO,
    callback = object : AdCallback {
        override fun onAdShow() {
            // 广告显示
        }
        
        override fun onAdClick() {
            // 广告点击
        }
        
        override fun onAdClose() {
            // 广告关闭
        }
        
        override fun onAdError(error: String) {
            // 广告错误
        }
        
        override fun onRewardEarned() {
            // 奖励获得
            Log.d("Ad", "用户获得奖励!")
        }
    }
)
```

### 5. 支持的广告类型

- `AdType.BANNER` - 横幅广告
- `AdType.INTERSTITIAL` - 插屏广告
- `AdType.NATIVE` - 原生广告
- `AdType.SPLASH` - 开屏广告
- `AdType.REWARDED_VIDEO` - 激励视频广告
- `AdType.FEED` - 信息流广告
- `AdType.FLOAT` - 浮窗广告

### 6. 混淆配置

如果使用了代码混淆，请在 `proguard-rules.pro` 中添加：
```proguard
-keep class com.maxrtb.zxadsdk.** { *; }
```

### 7. 技术支持

如有问题请联系：sdk@maxrtb.com

## 更新日志

### v1.0.0 (2024-01-01)
- 初始版本发布
- 支持多种广告类型
- 完整的错误处理机制
  */

// 6. 发布脚本 publish.sh
/*
#!/bin/bash

echo "Building ZxAd SDK..."

# 清理构建
./gradlew clean

# 构建Release版本
./gradlew :zxadsdk:assembleRelease

if [ $? -eq 0 ]; then
echo "✅ Build successful!"
echo "📦 AAR file: zxadsdk/build/outputs/aar/zxadsdk-release.aar"

    # 发布到本地仓库
    echo "Publishing to local repository..."
    ./gradlew :zxadsdk:publishToMavenLocal
    
    if [ $? -eq 0 ]; then
        echo "✅ Published to local repository!"
        echo "📍 Location: ~/.m2/repository/com/maxrtb/zx-ad-sdk/"
    else
        echo "❌ Failed to publish to local repository"
    fi
else
echo "❌ Build failed!"
exit 1
fi
*/