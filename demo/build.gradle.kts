import dependency.test.AndroidTestRunner
import dependency.test.Espresso
import dependency.ui.*
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmOptions

plugins {
    id("com.android.application")
    id("kotlinx-serialization")
    kotlin("android")
    kotlin("android.extensions")
    kotlin("kapt")
}

android {
    compileSdkVersion(28)

    defaultConfig {
        minSdkVersion(24)
        targetSdkVersion(28)
        applicationId = "com.algolia.instantsearch.demo"
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    sourceSets["main"].java.srcDir("src/main/kotlin")

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
    }

    packagingOptions {
        // TODO: Solve these "More than one file was found with OS independent path 'META-INF/*' in the lib via Proguard
        exclude("META-INF/*.kotlin_module")
    }
    (kotlinOptions as KotlinJvmOptions).apply {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    implementation(project(":helper"))
    implementation(kotlin("stdlib-jdk8"))

    implementation(AppCompat())
    implementation(Glide())
    implementation(ContraintLayout())
    implementation(RecyclerView())
    implementation(MaterialDesign())
    implementation(MultiSlider())
    implementation(AndroidCore("ktx"))
    implementation(Paging())
    implementation(Lifecycle("extensions"))
    kapt(Lifecycle("compiler"))

    androidTestImplementation(AndroidTestRunner())
    androidTestImplementation(Espresso("core"))
}