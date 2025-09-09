plugins {
    id("com.android.library")  // 直接使用插件ID
    alias(libs.plugins.kotlin.android)
    // 移除 kotlin.compose plugin，SDK不需要
}

android {
    namespace = "com.maxrtb.sdk"  // 改为SDK的包名
    compileSdk = 34

    defaultConfig {
        // 删除 applicationId，library不需要
        minSdk = 21  // 按照README.md中的要求
        // targetSdk, versionCode, versionName 在library中不需要

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8  // 改为Java 8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"  // 改为1.8
    }

    // 移除 buildFeatures compose相关配置
}

dependencies {
    // Android基础库
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.cardview:cardview:1.0.0")

    // 网络请求
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")
    implementation("com.google.code.gson:gson:2.10.1")

    // 图片加载
    implementation("com.github.bumptech.glide:glide:4.15.1")

    // 视频播放
    implementation("com.google.android.exoplayer:exoplayer:2.19.1")

    // 协程
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation(libs.androidx.runtime)
    implementation(libs.ui)

    // 测试依赖
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    implementation("androidx.cardview:cardview:1.0.0")
    implementation("com.google.android.exoplayer:exoplayer:2.19.1")
    implementation("com.github.bumptech.glide:glide:4.16.0")
}