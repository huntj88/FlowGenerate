package me.jameshunt.generate

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import java.io.File

open class FlowGenerator : DefaultTask() {

    @TaskAction
    fun generate() {
        generateCode()
    }

    private fun generateCode() {
        File("./${this.project.name}/src/main")
            .walk()
            .filter { it.extension == "puml" }
            .forEach { file ->
                FlowCodeGenerator(file).let { generator ->
                    val classText = generator.generate()
                    generator.writeToDisk(this.project.generatedSourcePath(), classText)
                }
            }
    }

}

internal fun generateCodeTest() {
    val testFile = File("./src/main/kotlin/me/jameshunt/generate/Settings.puml")
    FlowCodeGenerator(testFile).generate().let(::println)
}
