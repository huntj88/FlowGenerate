package me.jameshunt.generate

class ImportsGenerator {

    fun generate(flowName: String, states: Set<State>): String {

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
                    .also {
                        println(it)
                    }
                    .isEmpty()
            }
            .flatMap { it.imports }.toSet()
            .joinToString("\n") { "import $it" }

        return """
            package me.jameshunt.flow.generated

            import me.jameshunt.flow.FragmentFlowController
            import me.jameshunt.flow.ViewId
            import me.jameshunt.flow.promise.Promise
            import me.jameshunt.flow.promise.then
            import me.jameshunt.flow.generated.Generated${flowName}Controller.${flowName}FlowState.*
            $variables
        """
    }
}
