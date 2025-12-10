plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)

    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.appcuoiky"
    compileSdk = 34 // Mình để 34 cho ổn định, bạn muốn để 36 cũng được nhưng coi chừng lỗi tương thích

    defaultConfig {
        applicationId = "com.example.appcuoiky"
        minSdk = 26 // Nên để minSdk 24 để hỗ trợ nhiều máy hơn (29 hơi cao)
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    // 1. Các thư viện có sẵn trong libs (Giữ lại cái này là chuẩn nhất)
    implementation("androidx.core:core-ktx:1.12.0")
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation("androidx.constraintlayout:constraintlayout:2.1.4") // Nếu libs có thì dùng, không thì dùng dòng dưới

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // 2. Thư viện cho MVVM (ViewModel & LiveData)
    // Lưu ý cú pháp Kotlin DSL là ngoặc tròn (), và dùng dấu ngoặc kép " "
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.2")

    // 3. Thư viện Firebase
    implementation(platform("com.google.firebase:firebase-bom:34.6.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-firestore")
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.5")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.5")
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
    //cot bieu do
}