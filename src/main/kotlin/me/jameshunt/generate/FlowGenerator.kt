package me.jameshunt.generate

import java.io.File
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

open class FlowGenerator: DefaultTask() {

    @TaskAction
    fun generate() {
        generateCode()
    }

    private fun generateCode() {
        File("./${this.project.name}/src/main")
            .walk()
            .filter { it.extension == "puml" }
            .forEach { FlowCodeGenerator(it).generate(this.project.generatedSourcePath()) }
    }

}
