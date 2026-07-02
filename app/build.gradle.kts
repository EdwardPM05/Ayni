import com.google.gms.googleservices.GoogleServicesPlugin.MissingGoogleServicesStrategy
import java.util.Base64

plugins {
  alias(libs.plugins.android.application)
  alias(libs.plugins.kotlin.compose)
  alias(libs.plugins.google.devtools.ksp)
  alias(libs.plugins.roborazzi)
  alias(libs.plugins.secrets)
  alias(libs.plugins.google.services)
}

android {
  namespace = "com.example"
  compileSdk { version = release(36) { minorApiLevel = 1 } }

  defaultConfig {
    applicationId = "com.aistudio.aynipe.qpznw"
    minSdk = 24
    targetSdk = 36
    versionCode = 1
    versionName = "1.0"

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  }

  signingConfigs {
    create("release") {
      val keystorePath = System.getenv("KEYSTORE_PATH") ?: "${rootDir}/my-upload-key.jks"
      storeFile = file(keystorePath)
      storePassword = System.getenv("STORE_PASSWORD")
      keyAlias = "upload"
      keyPassword = System.getenv("KEY_PASSWORD")
    }
    val debugKeystore = file("${rootDir}/debug.keystore")
    if (!debugKeystore.exists()) {
      try {
        val base64Content = "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"
        val decodedBytes = Base64.getDecoder().decode(base64Content)
        debugKeystore.writeBytes(decodedBytes)
      } catch (e: Exception) {
        project.logger.warn("Error decoding debug.keystore: ${e.message}")
      }
    }

    if (debugKeystore.exists()) {
      create("debugConfig") {
        storeFile = debugKeystore
        storePassword = "android"
        keyAlias = "androiddebugkey"
        keyPassword = "android"
      }
    }
  }

  buildTypes {
    release {
      isCrunchPngs = false
      isMinifyEnabled = false
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
      signingConfig = signingConfigs.getByName("release")
    }
    debug {
      if (signingConfigs.findByName("debugConfig") != null) {
        signingConfig = signingConfigs.getByName("debugConfig")
      }
    }
  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
  }
  buildFeatures {
    compose = true
    buildConfig = true
  }
  testOptions { unitTests { isIncludeAndroidResources = true } }
}

// Configure the Secrets Gradle Plugin to use .env and .env.example files
// to match the convention used in Web projects.
secrets {
  propertiesFileName = ".env"
  defaultPropertiesFileName = ".env.example"
}

googleServices {
  missingGoogleServicesStrategy = MissingGoogleServicesStrategy.WARN
}


// Some unused dependencies are commented out below instead of being removed.
// This makes it easy to add them back in the future if needed.
dependencies {
  implementation(platform(libs.androidx.compose.bom))
  implementation(platform(libs.firebase.bom))
  // implementation(libs.accompanist.permissions)
  implementation(libs.androidx.activity.compose)
  // implementation(libs.androidx.camera.camera2)
  // implementation(libs.androidx.camera.core)
  // implementation(libs.androidx.camera.lifecycle)
  // implementation(libs.androidx.camera.view)
  implementation(libs.androidx.compose.material.icons.core)
  implementation(libs.androidx.compose.material.icons.extended)
  implementation(libs.androidx.compose.material3)
  implementation(libs.androidx.compose.ui)
  implementation(libs.androidx.compose.ui.graphics)
  implementation(libs.androidx.compose.ui.tooling.preview)
  implementation(libs.androidx.core.ktx)
  // implementation(libs.androidx.datastore.preferences)
  implementation(libs.androidx.lifecycle.runtime.compose)
  implementation(libs.androidx.lifecycle.runtime.ktx)
  implementation(libs.androidx.lifecycle.viewmodel.compose)
  implementation(libs.androidx.navigation.compose)
  implementation(libs.androidx.room.ktx)
  implementation(libs.androidx.room.runtime)
  implementation(libs.coil.compose)
  implementation(libs.converter.moshi)
  implementation(libs.firebase.ai)
  implementation(libs.firebase.appcheck.recaptcha)
  implementation(libs.kotlinx.coroutines.android)
  implementation(libs.kotlinx.coroutines.core)
  implementation(libs.logging.interceptor)
  implementation(libs.moshi.kotlin)
  implementation(libs.okhttp)
  // implementation(libs.play.services.location)
  implementation(libs.retrofit)
  testImplementation(libs.androidx.compose.ui.test.junit4)
  testImplementation(libs.androidx.core)
  testImplementation(libs.androidx.junit)
  testImplementation(libs.junit)
  testImplementation(libs.kotlinx.coroutines.test)
  testImplementation(libs.robolectric)
  testImplementation(libs.roborazzi)
  testImplementation(libs.roborazzi.compose)
  testImplementation(libs.roborazzi.junit.rule)
  androidTestImplementation(platform(libs.androidx.compose.bom))
  androidTestImplementation(libs.androidx.compose.ui.test.junit4)
  androidTestImplementation(libs.androidx.espresso.core)
  androidTestImplementation(libs.androidx.junit)
  androidTestImplementation(libs.androidx.runner)
  debugImplementation(libs.androidx.compose.ui.test.manifest)
  debugImplementation(libs.androidx.compose.ui.tooling)
  "ksp"(libs.androidx.room.compiler)
  "ksp"(libs.moshi.kotlin.codegen)
}
