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
        val isAndroid = project.extensions.findByName("android") != null

        File("./${this.project.name}/src/main")
            .walk()
            .filter { it.extension == "puml" }
            .forEach { file ->
                FlowCodeGenerator(file).let { generator ->
                    val classText = generator.generate(isAndroid = isAndroid)
                    generator.writeToDisk(this.project.generatedSourcePath(), classText)
                }
            }
    }

}

internal fun generateCodeTest() {
    val testFile = File("./src/main/kotlin/me/jameshunt/generate/SettingsBusiness.puml")
    FlowCodeGenerator(testFile).generate(false).let(::println)
}
