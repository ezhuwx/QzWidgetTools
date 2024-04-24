import java.text.SimpleDateFormat
import java.util.Date
import com.android.build.gradle.AppExtension
plugins {
    alias(libs.plugins.androidLibiary)
    //alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
}

android {
    namespace = "com.ez.widget"
    compileSdk = 34
    version = "1.0.1.01"
    defaultConfig {
        //applicationId = "com.ez.widget"
        minSdk = 21
        //noinspection ExpiredTargetSdkVersion
        lint.targetSdk = 31
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
    buildFeatures {
        viewBinding = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    afterEvaluate {
    //生产文件名称
    libraryVariants.find { variant -> variant.name.lowercase() == "release" }?.outputs
        ?.find { output -> output.outputFile.name.endsWith(".aar") }?.let { output ->
            //包名版本号
            val packageVersion = "QzWidgetTools_${version}".replace(".", "")
            //时间后缀
            val timeSuffix = "${SimpleDateFormat("YYMMdd").format(Date())}.aar"
            //重命名
            output.outputFile.renameTo(
                File(output.outputFile.parent, "${packageVersion}_$timeSuffix")
            )
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    //自适应布局
    api(libs.autoSize)
    //RecyclerViewAdapter
    implementation(libs.adapterHelper)
    //日期选择
    implementation(libs.pickerView)
    //图表
    implementation(libs.androidChart)
}