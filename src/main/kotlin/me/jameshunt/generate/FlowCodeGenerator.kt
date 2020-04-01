package me.jameshunt.generate

import java.io.File

class FlowCodeGenerator(private val file: File) {

    fun generate(isAndroid: Boolean): String {
        val (states, inputOutput, imports) = PumlParser().parse(file.readText())
        val (input, output) = inputOutput

        val flowName = file.nameWithoutExtension

        val importsString = ImportsGenerator().generate(flowName, imports, isAndroid)
        val generatedClass = ClassSignatureGenerator().generate(flowName, input, output, isAndroid)

        val sealedClass = SealedClassGenerator().generate(flowName, states, output)

        val methodsGenerator = MethodsGenerator()
        val abstractMethods = methodsGenerator.generateAbstract(states)
        val startMethod = methodsGenerator.generateStart(states, input)
        val extensionToMethods = methodsGenerator.generateExtensionToMethods(states)
        val toMethods = methodsGenerator.generateToMethods(states, isAndroid)

        return """
            $importsString
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
