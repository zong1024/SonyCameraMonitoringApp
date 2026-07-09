plugins {
    id("com.android.application")
}

android {
    namespace = "com.zongrui.cinelinkmonitor"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.zongrui.cinelinkmonitor"
        minSdk = 26
        // AUSBC 3.2.7's USBMonitor uses legacy dynamic receiver registration.
        // Target 33 keeps the side-loaded monitor compatible on Android 14+ until the UVC stack is patched.
        targetSdk = 33
        versionCode = 3
        versionName = "0.1.2"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlin {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
        }
    }

}

dependencies {
    implementation("androidx.appcompat:appcompat:1.7.1")
    implementation("com.github.jiangdongguo.AndroidUSBCamera:libausbc:3.2.7") {
        exclude(group = "com.gyf.immersionbar", module = "immersionbar")
        exclude(group = "com.zlc.glide", module = "webpdecoder")
    }
    implementation("com.github.jiangdongguo.AndroidUSBCamera:libuvc:3.2.7")

    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:2.2.10")
    testImplementation("junit:junit:4.13.2")
}
