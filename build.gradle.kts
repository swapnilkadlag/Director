import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

fun properties(key: String) = project.findProperty(key).toString()

plugins {
    id("java")
    id("org.jetbrains.intellij") version ("1.13.3")
    id("org.jetbrains.kotlin.jvm") version ("1.8.20")
    id("org.jetbrains.changelog") version ("1.3.1")
}

group = properties("pluginGroup")
version = properties("pluginVersion")

repositories {
    mavenCentral()
}

dependencies {
    compileOnly("org.jetbrains.kotlin:kotlin-compiler:1.8.20")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.8.20")
}

intellij {
    pluginName.set(properties("pluginName"))
    version.set(properties("platformVersion"))
    type.set(properties("platformType"))
    plugins.set(properties("platformPlugins").split(',').map(String::trim).filter(String::isNotEmpty))
}

tasks {
//    runIde {
//        ideDir.set(file("C:\\Program Files\\Android\\Android Studio"))
//    }
    patchPluginXml {
        sinceBuild.set(properties("pluginSinceBuild"))
        untilBuild.set(properties("pluginUntilBuild"))
        changeNotes.set("Initial release")
    }
    compileKotlin {
        kotlinOptions.jvmTarget = properties("javaVersion")
    }
    compileJava {
        sourceCompatibility = "11"
        targetCompatibility = "11"
    }
    test {
        useJUnitPlatform()
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "11"
    }
}