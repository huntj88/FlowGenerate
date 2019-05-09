package me.jameshunt.generate

import com.android.build.gradle.BaseExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import java.io.File

class FlowGeneratePlugin : Plugin<Project> {

    override fun apply(project: Project) {
        println("apply flow")

        setupGeneratedSourceDirectory(project)
        addSourceSet(project)

        project.tasks.create("flowGenerate", FlowGenerator::class.java)
    }

    private fun setupGeneratedSourceDirectory(project: Project) {
        val generatedSourceFolder = File(project.generatedSourcePath())

        if (generatedSourceFolder.exists()) return
        generatedSourceFolder.mkdirs()
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

fun Project.generatedSourcePath() = "./${this.name}/build/generated/source/flow/src/me/jameshunt/flow/generated"
