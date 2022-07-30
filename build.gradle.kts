plugins {
    id("org.jetbrains.intellij") version("1.4.0")
    id("org.jetbrains.kotlin.jvm") version("1.7.0")
    id("org.jetbrains.changelog") version("1.3.1")
    id("java")
}

group = "org.sk"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    compileOnly("org.jetbrains.kotlin:kotlin-compiler:1.7.0")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.7.0")
}

intellij {
    version.set("2022.1.3")
    plugins.set(listOf("android"))
}

tasks {
    runIde {
        ideDir.set(file("C:\\Program Files\\Android\\Android Studio"))
    }
    patchPluginXml {
        sinceBuild.set("203")
        changeNotes.set("Initial release")
    }
    compileKotlin {
        kotlinOptions.jvmTarget = "11"
    }
    test {
        useJUnitPlatform()
    }
}
