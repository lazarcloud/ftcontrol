package com.bylazar.ftcontrol.panels.configurables

import android.content.Context
import com.bylazar.ftcontrol.panels.Logger
import com.bylazar.ftcontrol.panels.configurables.variables.MyField
import com.bylazar.ftcontrol.panels.configurables.variables.generics.GenericField
import com.bylazar.ftcontrol.panels.json.GenericTypeJson

object ConfigurablesManager {
    var fieldsMap = mutableMapOf<String, MyField>()
    var configurableClasses: List<ClassFinder.ClassEntry> = listOf()

    var jvmFields = mutableListOf<GenericField>()
    var initialJvmFields = mutableListOf<GenericTypeJson>()

    var classFinder = ClassFinder()
    var variables = VariablesFinder()

    fun init(context: Context) {
        Logger.configurablesLog("Initializing configurables")

        fieldsMap = mutableMapOf()
        configurableClasses = listOf()
        jvmFields = mutableListOf()

        Logger.configurablesLog("Stage 1: Searching for configurables classes...")

        classFinder.updateClasses(context.packageCodePath)

        configurableClasses = classFinder.classes

        Logger.configurablesLog("Found ${classFinder.classes.size} configurable classes:")
        classFinder.classes.forEach { className ->
            Logger.configurablesLog("$className")
        }

        Logger.configurablesLog("Stage 2: Searching for configurables variables...")

        variables.updateJvmFields(classFinder.classes)

        Logger.configurablesLog("Found ${variables.jvmFields.size} configurable variables:")
        variables.jvmFields.forEach { info ->
            Logger.configurablesLog("${info.className}.${info.reference.name}")
            info.debug()
        }

        Logger.configurablesLog("Stage 3: Converting to JSON...")
        jvmFields.addAll(variables.jvmFields)
        initialJvmFields = jvmFields.map { it.toJsonType }.toMutableList()
    }
}