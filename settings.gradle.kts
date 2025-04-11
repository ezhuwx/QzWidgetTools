pluginManagement {
    repositories {
        mavenLocal()
        gradlePluginPortal()
        maven ("https://repo.huaweicloud.com/repository/maven/")
        maven ("https://mirrors.cloud.tencent.com/nexus/repository/google/")
        maven ("https://mirrors.cloud.tencent.com/nexus/repository/jcenter/")
        maven ("https://maven.aliyun.com/repository/gradle-plugin")
        maven ("https://maven.aliyun.com/repository/public")
        maven ("https://maven.aliyun.com/repository/google")
        maven ("https://developer.huawei.com/repo/")
        maven ("https://jitpack.io")
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenLocal()
        mavenCentral()
        maven ("https://repo.huaweicloud.com/repository/maven/")
        maven ("https://mirrors.cloud.tencent.com/nexus/repository/google/")
        maven ("https://mirrors.cloud.tencent.com/nexus/repository/jcenter/")
        maven ("https://maven.aliyun.com/repository/public")
        maven ("https://maven.aliyun.com/repository/google")
        maven ("https://esri.jfrog.io/artifactory/arcgis")
        maven ("https://developer.huawei.com/repo/")
        maven ("https://jitpack.io")
    }
}

rootProject.name = "QzWidgetTools"
include(":app")

 