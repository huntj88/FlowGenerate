package me.jameshunt.generate

class ClassSignatureGenerator {

    fun generate(flowName: String, input: String, output: String, isAndroid: Boolean): String {
        val baseClass = when(isAndroid) {
            true -> "FragmentFlowController"
            false -> "BusinessFlowController"
        }

        return "abstract class Generated${flowName}Controller: $baseClass<$input, $output>() {"
    }
}
