plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("kotlin-parcelize")
}

android {
    namespace = "com.maxrtb.demo"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.maxrtb.demo"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
    }
    buildTypes {
        debug {
            isDebuggable = true
            isMinifyEnabled = false
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
        }
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }

    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // 导入ZxAdSDK
    implementation(project(":zxadsdk"))

    // Android基础库
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout.v214)
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.fragment.ktx)

    // Material Design 组件
    implementation(libs.material)
    implementation(libs.material.v1110)

    // CardView
    implementation(libs.androidx.cardview)

    // 生命周期组件
    implementation(libs.androidx.lifecycle.runtime.ktx.v270)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.livedata.ktx)

    // RecyclerView (用于展示日志)
    implementation(libs.androidx.recyclerview)

    // 协程
    implementation(libs.kotlinx.coroutines.android.v173)

    // 调试工具
    debugImplementation(libs.leakcanary.android)

    // 网络调试 (Charles/Proxy man替代品)
    debugImplementation(libs.library)
    releaseImplementation(libs.library.no.op)

    // 测试相关
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit.v130)
    androidTestImplementation(libs.androidx.espresso.core.v370)

    // 如果SDK中的这些依赖没有传递过来，也需要添加：
    implementation(libs.glide)
    implementation(libs.exoplayer)
    implementation(libs.retrofit.v290)
    implementation(libs.converter.gson.v290)
    implementation(libs.logging.interceptor.v4120)
    implementation(libs.kotlinx.coroutines.android)

}