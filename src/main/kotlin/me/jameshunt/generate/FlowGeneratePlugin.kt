package me.jameshunt.generate

import com.android.build.gradle.BaseExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import java.io.File

class FlowGeneratePlugin : Plugin<Project> {

    override fun apply(project: Project) {
        println("apply flow")
        val generatedSrcPath = "./${project.name}/build/generated/source/flow/src"

        setupGeneratedSourceDirectory(generatedSrcPath)

        generateCode(project.name, generatedSrcPath)

        addSourceSet(project)
    }

    private fun setupGeneratedSourceDirectory(generatedSrcPath: String) {
        val generatedSourceFolder = File(generatedSrcPath)

        if (generatedSourceFolder.exists()) return
        generatedSourceFolder.mkdirs()
    }

    private fun generateCode(projectName: String, generatedSrcPath: String) {
        File("./$projectName/src/main")
            .walk()
            .filter { it.extension == "puml" }
            .forEach { FlowGenerator(it).generate(generatedSrcPath) }
    }

    private fun addSourceSet(project: Project) {
        val sourceSets = (project.extensions.getByName("android") as BaseExtension).sourceSets.asMap
        sourceSets.forEach { sourceSetName, sourceSet ->

            if (sourceSetName == "main") {
                val generatedSrcPathSrcSet = "./build/generated/source/flow/src"
                sourceSet.java.srcDirs(File(generatedSrcPathSrcSet))
                return@forEach
            }
        }
    }
}
