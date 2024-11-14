import com.vanniktech.maven.publish.SonatypeHost

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.androidLibiary) apply false
    //alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.jetbrainsKotlinAndroid) apply false
    alias(libs.plugins.mavenPublish) apply false
    alias(libs.plugins.kotlinKapt) apply false
}
ext {
    //Maven新版中央仓库
    set("SonatypeHostMavenCentral", SonatypeHost.CENTRAL_PORTAL)
}