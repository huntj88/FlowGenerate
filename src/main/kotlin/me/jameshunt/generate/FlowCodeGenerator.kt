package me.jameshunt.generate

import java.io.File

class FlowCodeGenerator(private val file: File) {

    fun generate(generatedSrcPath: String) {
        val (states, inputOutput) = PumlParser().parse(file)
        val (input, output) = inputOutput

        val flowName = file.nameWithoutExtension

        val imports = ImportsGenerator().generate(flowName, states)

        val generatedClass =
            "abstract class Generated${flowName}Controller(viewId: ViewId): FragmentFlowController<$input, $output>(viewId) {"
        val sealedClass = SealedClassGenerator().generate(flowName, states, output)
        val abstractMethods = MethodsGenerator().generateAbstract(states)
        val startMethod = MethodsGenerator().generateStart(states, input)

        val toMethods = MethodsGenerator().generateToMethods(states)

        val classText = """
            $imports
            $generatedClass
            $sealedClass
            $abstractMethods
            $startMethod
            $toMethods
            }
        """

        File("$generatedSrcPath/Generated${flowName}Controller.kt").writeText(classText)
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


