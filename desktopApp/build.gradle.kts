import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

dependencies {
    implementation(project(":shared"))

    implementation(compose.desktop.currentOs)
    implementation(libs.compose.components.resources)
    implementation(libs.kotlinx.coroutinesSwing)

    implementation(libs.compose.uiToolingPreview)
}

compose.desktop {
    application {
        mainClass = "com.tomahawk.space.MainKt"

        jvmArgs += listOf(
            "--add-opens=java.base/sun.misc=ALL-UNNAMED",
        )

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)

            packageName = "Tomahawk Space"
            packageVersion = "1.0.0"
            description = "NASA Astronomy Picture of the Day explorer"
            copyright = "© 2024 Tomahawk. All rights reserved."
            vendor = "Tomahawk"

            // Включаем jdk.unsupported (содержит sun.misc.Unsafe для Protobuf/DataStore)
            modules("jdk.unsupported")

            // Иконка для каждой платформы
            windows {
                iconFile.set(project.file("src/main/resources/ic_launcher.ico"))
                // Уникальный GUID для инсталлятора — обновляй при смене мажорной версии
                upgradeUuid = "3FA2F6F0-1A2B-4C3D-8E5F-6A7B8C9D0E1F"
                menuGroup = "Tomahawk Space"
                perUserInstall = true      // установка без прав администратора
                shortcut = true            // ярлык на рабочем столе
                dirChooser = true          // пользователь выбирает папку установки
            }

            linux {
                iconFile.set(project.file("src/main/resources/ic_launcher.png"))
                debMaintainer = "support@tomahawk.app"
                appCategory = "Science"
            }

            macOS {
                iconFile.set(project.file("src/main/resources/ic_launcher.icns"))
                bundleID = "com.tomahawk.space"
                appStore = false
            }
        }
    }
}
