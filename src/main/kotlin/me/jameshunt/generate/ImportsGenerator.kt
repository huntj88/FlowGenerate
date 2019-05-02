package me.jameshunt.generate

class ImportsGenerator {

    fun generate(flowName: String, states: Set<State>): String {
       val variables =  states
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
