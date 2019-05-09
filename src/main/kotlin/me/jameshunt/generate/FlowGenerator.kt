package me.jameshunt.generate

import java.io.File

class FlowGenerator {

    fun generate(projectName: String) {
        val generatedSrcPath = "./$projectName/build/generated/source/flow/src/me/jameshunt/flow/generated"

        setupGeneratedSourceDirectory(generatedSrcPath)

        generateCode(projectName, generatedSrcPath)
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
            .forEach { FlowCodeGenerator(it).generate(generatedSrcPath) }
    }

}
