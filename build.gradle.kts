// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false

    //KSP is used by room
    alias(libs.plugins.ksp)
    //Plugin for auto-serialization in KTOR
    alias(libs.plugins.kotlin.serialization)
    //Plugin for jetpack compose UI
    alias(libs.plugins.kotlin.compose) apply false
}