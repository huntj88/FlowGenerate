package me.jameshunt.generate

class ImportsGenerator {

    fun generate(flowName: String, imports: Set<String>, isAndroid: Boolean): String {
        val baseType = when(isAndroid) {
            true -> """
                import me.jameshunt.flow.FragmentFlowController
                import me.jameshunt.flow.FlowResult
                """
            false -> "import me.jameshunt.flow.BusinessFlowController"
        }

        return """
            package me.jameshunt.flow.generated

            import com.inmotionsoftware.promisekt.Promise
            import com.inmotionsoftware.promisekt.map
            import com.inmotionsoftware.promisekt.catch
            $baseType
            import me.jameshunt.flow.generated.Generated${flowName}Controller.${flowName}FlowState.*
            ${imports.joinToString("\n")}
        """
    }
}
