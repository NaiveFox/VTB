plugins {
  id("com.android.application")
  id("org.jetbrains.kotlin.android")
}

android {
  namespace = "ru.naivefox.vtbprank"
  compileSdk = 34

  defaultConfig {
    applicationId = "ru.naivefox.vtbprank"
    minSdk = 21
    targetSdk = 34
    versionCode = 1
    versionName = "1.0"
  }

  // ДВА ВКУСА: обычный (clean) и пранк (prank)
  flavorDimensions += "mode"
  productFlavors {
    create("clean") {
      dimension = "mode"
      applicationIdSuffix = ".clean"
      resValue("string", "app_name", "VTB")
    }
    create("prank") {
      dimension = "mode"
      applicationIdSuffix = ".prank"
      resValue("string", "app_name", "VTB")
    }
  }

  buildTypes {
    getByName("debug") { isMinifyEnabled = false }
    getByName("release") { isMinifyEnabled = false }
  }

  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
  }
  kotlinOptions { jvmTarget = "17" }
}

dependencies {
  implementation("androidx.core:core-ktx:1.13.1")
  implementation("androidx.appcompat:appcompat:1.7.0")
  implementation("com.google.android.material:material:1.12.0")
  implementation("androidx.constraintlayout:constraintlayout:2.1.4")
}
