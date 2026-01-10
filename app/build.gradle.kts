import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}

android {
    namespace = "project.side.ikdaman"

    val properties = Properties()
    properties.load(project.rootProject.file("key.properties").inputStream())

    defaultConfig {
        compileSdk = 36
        applicationId = "project.side.ikdaman"
        minSdk = 29
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"


        val kakaoAppKey = properties.getProperty("KAKAO_APP_KEY")
        buildConfigField("String", "KAKAO_APP_KEY", "\"$kakaoAppKey\"")
        manifestPlaceholders["KAKAO_APP_KEY"] = kakaoAppKey

        buildConfigField("String", "NAVER_CLIENT_ID", "\"${properties.getProperty("NAVER_CLIENT_ID")}\"")
        buildConfigField("String", "NAVER_CLIENT_SECRET", "\"${properties.getProperty("NAVER_CLIENT_SECRET")}\"")
    }

    buildFeatures {
        buildConfig = true
    }

    signingConfigs {
        create("release") {
            storeFile = project.rootProject.file("release.keystore")
            storePassword = properties.getProperty("KEYSTORE_PASSWORD")
            keyAlias = properties.getProperty("KEY_ALIAS")
            keyPassword = properties.getProperty("KEY_PASSWORD")
        }
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

kotlin {
    compilerOptions {
        jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11
    }
}

dependencies {
    implementation(project(":ui"))
    implementation(project(":presentation"))
    implementation(project(":domain"))
    implementation(project(":data"))
    implementation(project(":remote"))
    implementation(project(":local"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)
}
