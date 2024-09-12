plugins {
    alias(libs.plugins.androidApplication)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.supershor.markmate"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.supershor.markmate"
        minSdk = 24
        targetSdk = 34
        versionCode = 9
        versionName = "9.0"

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
}

dependencies {
    //noinspection UseTomlInstead
    implementation ("com.hbb20:ccp:2.7.3")
    //noinspection GradleDependency,UseTomlInstead
    implementation ("com.airbnb.android:lottie:3.4.0")
    //noinspection UseTomlInstead
    implementation("com.intuit.sdp:sdp-android:1.1.0")
    //noinspection UseTomlInstead
    implementation("com.intuit.sdp:sdp-android:1.1.0")
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.database)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}