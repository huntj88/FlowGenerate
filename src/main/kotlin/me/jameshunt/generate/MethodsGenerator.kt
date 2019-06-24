package me.jameshunt.generate

class MethodsGenerator {

    fun generateAbstract(states: Set<State>): String {
        return states
            .filter { it.name != "[*]" }
            .filter { it.name != "Done" }
            .filter { it.name != "Back" }
            .joinToString("\n") {
                "protected abstract suspend fun on${it.name}(state: ${it.name}): From${it.name}"
            }
    }

    fun generateStart(
        states: Set<State>,
        input: String
    ): String {
        val from = states.firstOrNull { it.from.contains("[*]") }?.name!!

        val impl = when (input == "Unit") {
            true -> "to$from($from)"
            false -> "to$from($from(state.input))"
        }

        return """
            final override suspend fun onStart(state: InitialState<$input>) {
                $impl
            }
        """
    }

    fun generateExtensionToMethods(states: Set<State>): String {
        return states.map { state ->
            state.from
                .filter { it != "[*]" }
                .joinToString("\n") { from ->
                    val variables = state.variables.joinToString(", ") { variable ->
                        variable.removePrefix("val ").removePrefix("var ")
                    }

                    val constructor = when (state.variables.isEmpty()) {
                        true -> {
                            if(state.name == "Done") {
                                "Done(Unit)"
                            } else {
                                state.name
                            }
                        }
                        false -> {
                            val variableNamesRegex = "(\\S+):\\s*\\S+".toRegex()
                            val namesWithoutType = variableNamesRegex.findAll(variables)
                                .map { it.groupValues[1] }
                                .joinToString(",")

                            "${state.name}($namesWithoutType)"
                        }
                    }

                    "protected fun $from.to${state.name}($variables): From$from = $constructor\n"
                }

        }.joinToString("\n")
    }

    fun generateToMethods(states: Set<State>, isAndroid: Boolean): String {
        return states
            .filter { it.name != "[*]" }
            .filter { it.name != "Done" }
            .filter { it.name != "Back" }
            .joinToString("") {
                it.generateToMethod(states.fromWhen(it, isAndroid))
            }
    }

    private fun State.generateToMethod(fromWhen: String): String {
        return """
            private suspend fun to${this.name}(state: ${this.name}) {
                try {
                    on${this.name}(state).let {
                        when(it) {
                            $fromWhen
                        }
                    }
                } catch (t: Throwable) {
                    t.printStackTrace()
                    super.onCatch(t)
                }
            }
        """
//        else -> throw IllegalStateException("Illegal transition from: ${"$"}state, to: ${"$"}it")
    }

    private fun Set<State>.fromWhen(state: State, isAndroid: Boolean): String {

        fun String.handleBackAndDoneAndroid(): String = when (this) {
            "Back" -> "super.onDone(FlowResult.Back)"
            "Done" -> "super.onDone(FlowResult.Completed(it.output))"
            else -> "to$this(it)"
        }

        fun String.handleDone(): String = when (this) {
            "Done" -> "super.onDone(it.output)"
            else -> "to$this(it)"
        }

        fun String.handleSpecialCases(): String = when(isAndroid) {
            true -> handleBackAndDoneAndroid()
            false -> handleDone()
        }

        return this.filter { it.from.contains(state.name) }.joinToString("\n") {
            "is ${it.name} -> ${it.name.handleSpecialCases()}"
        }
    }
}
