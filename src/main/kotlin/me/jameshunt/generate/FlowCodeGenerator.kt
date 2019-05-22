package me.jameshunt.generate

import java.io.File

class FlowCodeGenerator(private val file: File) {

    fun generate(): String {
        val (states, inputOutput) = PumlParser().parse(file)
        val (input, output) = inputOutput

        val flowName = file.nameWithoutExtension

        val imports = ImportsGenerator().generate(flowName, states)

        val generatedClass =
            "abstract class Generated${flowName}Controller: FragmentFlowController<$input, $output>() {"
        val sealedClass = SealedClassGenerator().generate(flowName, states, output)
        val abstractMethods = MethodsGenerator().generateAbstract(states)
        val startMethod = MethodsGenerator().generateStart(states, input)

        val toMethods = MethodsGenerator().generateToMethods(states)

        return """
            $imports
            $generatedClass
            $sealedClass
            $abstractMethods
            $startMethod
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


