plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("maven-publish")
}






android {
    namespace = "com.crazysoultion.library"
    compileSdk = 33

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
}

publishing {
    publications {
        create<MavenPublication>("bar") {
            groupId = "com.youtube"
            artifactId = "ratingview"
            version = "1.0.0"
            artifactId = ("$buildDir/outputss/rating-view-release.aar")
        }
    }
        repositories {
            maven {
                name = "GithubPackages"
                url = uri("https://maven.pkg.github.com/NayabAnsar/Ratingview")
                credentials {
                    username = "NayabAnsar/Ratingview"
                    password = "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"
                }
            }
        }
    }

dependencies {

    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    implementation("com.youtube:ratingview:1.0")
}