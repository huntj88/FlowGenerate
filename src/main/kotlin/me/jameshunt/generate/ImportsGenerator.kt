package me.jameshunt.generate

class ImportsGenerator {

    fun generate(flowName: String, states: Set<State>, isAndroid: Boolean): String {

        //todo: add the rest of kotlin builtIns
        //todo: make types with generics work
        val skipImport = listOf("Unit", "String", "Int", "Boolean", "Float", "Double", "Char", "Byte", "ByteArray")

        val variables = states
            .filter { state ->
                // check that its not a builtin
                val typeNames = state.variables
                    .map {
                        // get type, and remove question mark
                        it.split(" ").last().takeWhile { it != '?' }
                    }

                skipImport
                    .intersect(typeNames)
                    .isEmpty()
            }
            .flatMap { it.imports }.toSet()
            .joinToString("\n") { "import $it" }

        val baseType = when(isAndroid) {
            true -> """
                import me.jameshunt.flow.FragmentFlowController
                import me.jameshunt.flow.FlowResult
                """
            false -> "import me.jameshunt.flow.BusinessFlowController"
        }

        return """
            package me.jameshunt.flow.generated

            import kotlinx.coroutines.async
            import kotlinx.coroutines.coroutineScope
            $baseType
            import me.jameshunt.flow.generated.Generated${flowName}Controller.${flowName}FlowState.*
            $variables
        """
    }
}
