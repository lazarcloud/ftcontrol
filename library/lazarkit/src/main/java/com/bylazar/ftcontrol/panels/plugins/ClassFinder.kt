package com.bylazar.ftcontrol.panels.plugins

import com.bylazar.ftcontrol.panels.Logger
import com.bylazar.ftcontrol.panels.configurables.utils.extractClassNamesFromDex
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.zip.ZipFile

class ClassFinder {
    private val ignored: Set<String> = HashSet(
        mutableListOf(
            "com.google",
            "com.sun",
            "sun",
            "com.qualcomm",
            "org.opencv",
            "java",
            "javax",
            "android",
            "com.android",
            "kotlin",
            "dalvik",
            "org.firstinspires.ftc.robotcore",
            "org.intellij",
            "org.firstinspires.inspection",
            "org.firstinspires.ftc.robotserver",
            "org.firstinspires.ftc.ftccommon",
            "org.firstinspires.ftc.robotcontroller",
            "org.firstinspires.ftc.onbotjava",
            "org.firstinspires.ftc.vision",
            "org.firstinspires.ftc.apriltag",
            "org.slf4j",
            "org.threeten",
            "org.w3c",
            "org.xmlpull",
            "org.java_websocket",
            "fi.iki",
            "okhttp3",
            "com.journeyapps",
            "dk.sgjesse",
            "org.openjsse",
            "com.jakewharton",
            "org.openftc",
            "jdk.Exported",
            "org.json",
            "org.xml",
            "nl.minvws",
            "okio",
            "org.bouncycastle",
            "org.conscrypt",
            "org.jetbrains"
        )
    )

    data class ClassEntry(
        val className: String,
    )

    lateinit var apkPath: String

    private val allClasses: List<ClassEntry> by lazy {
        mutableListOf<ClassEntry>().apply {
            try {
                ZipFile(apkPath).use { zipFile ->
                    val entries = zipFile.entries()
                    while (entries.hasMoreElements()) {
                        val entry = entries.nextElement()
                        if (entry.name.endsWith(".dex")) {
                            zipFile.getInputStream(entry).use { inputStream ->
                                val dexBytes = inputStream.readBytes()
                                val dexBuffer =
                                    ByteBuffer.wrap(dexBytes).order(ByteOrder.LITTLE_ENDIAN)
                                val classNames = dexBuffer.extractClassNamesFromDex()
                                val filteredClassNames = classNames.filter { className ->
                                    val isNotIgnored =
                                        ignored.none { prefix -> className.startsWith(prefix) }
                                    return@filter isNotIgnored
                                }
                                val processedClassNames = filteredClassNames.map {
                                    ClassEntry(
                                        className = it,
                                    )
                                }
                                addAll(processedClassNames)
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                Logger.pluginsLog("Exception occurred: ${e.message}")
            } catch (e: IllegalArgumentException) {
                Logger.pluginsLog("IllegalArgumentException occurred: ${e.message}")
            }
        }
    }

    val getAllClasses: List<ClassEntry>
        get() = allClasses
}