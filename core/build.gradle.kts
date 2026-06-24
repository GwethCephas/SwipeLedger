plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.cephcoding.core"
    compileSdk = 37

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

}

dependencies {
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core.ktx)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)

    androidTestImplementation(libs.kotlinx.coroutines.test)

    // Room
    implementation(libs.bundles.room)
    ksp(libs.androidx.room.compiler)

    //Koin
    implementation(libs.bundles.koin)

    // SQLCipher
    implementation(libs.sqlcipher.android)

    // Biometric
    implementation(libs.androidx.biometric)
}