plugins {
    id("org.jetbrains.kotlin.android")
    id("com.android.library")
    id("maven-publish")
    id("kotlin-android")
}

android {
    compileSdk = 34
    namespace = "com.vungn.admob"

    defaultConfig {
        minSdk = 21
        testOptions.targetSdk = 34
        lint.targetSdk = 34
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = "17"
    }
}

kotlin {
    jvmToolchain(17)
}

afterEvaluate {
    group = "com.vungn.admob"
    version = "1.0.0"
    publishing {
        publications {
            create<MavenPublication>("EasyAdMob") {
                from(components["release"])
                pom {
                    name = "Easy AdMob Integration for Android"
                    description =
                        "This library streamlines the integration of Google AdMob into Android projects, providing developers with a simplified, reliable, and efficient setup. It enables seamless ad implementation with minimal configuration, making it ideal for projects of any scale. Perfect for Android developers aiming to enhance their app’s monetization potential quickly and effectively."
                    url = "https://github.com/VuNgN/EasyAdMob/"
                    licenses {
                        license {
                            name = "The VuNgN License, Version 1.0"
                            url = "https://github.com/VuNgN/License/blob/license/LICENSE.txt"
                        }
                    }
                    developers {
                        developer {
                            id = "VuNgN"
                            name = "Nguyễn Ngọc Vũ"
                            email = "vu.nguyeenngoc@gmail.com"
                        }
                    }
                }
                groupId = "com.vungn.admob"
                artifactId = "EasyAdMob"
                version = "1.0.0"
            }

            repositories {
                maven {
                    name = "EasyAdMob"
                    url = uri(layout.buildDirectory.dir("repo"))
                }
            }
        }
    }
}

dependencies {
    // Ads
    implementation("com.google.android.gms:play-services-ads:23.1.0")
    implementation("com.google.guava:guava:32.0.1-android")
    implementation("com.google.guava:listenablefuture:9999.0-empty-to-avoid-conflict-with-guava")

    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
}
