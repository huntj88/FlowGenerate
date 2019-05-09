package me.jameshunt.generate

import com.android.build.gradle.BaseExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import java.io.File

class FlowGeneratePlugin : Plugin<Project> {

    override fun apply(project: Project) {
        println("apply flow")

        FlowGenerator().generate(project.name)

        addSourceSet(project)
    }

    private fun addSourceSet(project: Project) {
        val sourceSets = (project.extensions.getByName("android") as BaseExtension).sourceSets.asMap
        sourceSets.forEach { sourceSetName, sourceSet ->

            if (sourceSetName == "main") {
                sourceSet.java.srcDirs(File("./build/generated/source/flow/src"))
                return@forEach
            }
        }
    }
}
