package me.jameshunt.generate

import java.io.File

class FlowCodeGenerator(private val file: File) {

    fun generate(isAndroid: Boolean): String {
        val (states, inputOutput) = PumlParser().parse(file)
        val (input, output) = inputOutput

        val flowName = file.nameWithoutExtension

        val imports = ImportsGenerator().generate(flowName, states, isAndroid)
        val generatedClass = ClassSignatureGenerator().generate(flowName, input, output, isAndroid)

        val sealedClass = SealedClassGenerator().generate(flowName, states, output)
        val abstractMethods = MethodsGenerator().generateAbstract(states)
        val startMethod = MethodsGenerator().generateStart(states, input)

        val extensionToMethods = MethodsGenerator().generateExtensionToMethods(states)
        val toMethods = MethodsGenerator().generateToMethods(states, isAndroid)

        return """
            $imports
            $generatedClass
            $sealedClass
            $abstractMethods
            $startMethod
            $extensionToMethods
            $toMethods
            }
        """
    }

    fun writeToDisk(generatedSrcPath: String, classText: String) {
        File("$generatedSrcPath/Generated${file.nameWithoutExtension}Controller.kt").writeText(classText)
    }
}

data class StateSet(
    val states: Set<State>,
    val inputOutput: Pair<String, String>
)

data class State(
    val name: String,
    val variables: Set<String>,
    val imports: Set<String>,
    val from: Set<String>
)


