package me.jameshunt.generate

class MethodsGenerator {

    fun generateAbstract(states: Set<State>): String {
        return states
            .filter { it.name != "[*]" }
            .filter { it.name != "Done" }
            .filter { it.name != "Back" }
            .joinToString("\n") {
                "protected abstract fun on${it.name}(state: ${it.name}): Promise<From${it.name}>"
            }
    }

    fun generateStart(
        states: Set<State>,
        input: String
    ): String {
        val from = states.firstOrNull { it.from.contains("[*]") }?.name!!

        val impl = when(input == "Unit") {
            true -> "to$from($from)"
            false -> "to$from($from(state.input))"
        }

        return """
            final override fun onStart(state: InitialState<$input>) {
                $impl
            }
        """
    }

    fun generateToMethods(states: Set<State>): String {
        return states
            .filter { it.name != "[*]" }
            .filter { it.name != "Done" }
            .filter { it.name != "Back" }
            .joinToString("") {
                it.generateToMethod(states.fromWhen(it))
            }
    }

    private fun State.generateToMethod(fromWhen: String): String {
        return """
            private fun to${this.name}(state: ${this.name}) {
                currentState = state
                on${this.name}(state).map {
                    when(it) {
                        $fromWhen
                    }
                }
            }
        """

//        else -> throw IllegalStateException("Illegal transition from: ${"$"}state, to: ${"$"}it")
    }

    private fun Set<State>.fromWhen(state: State): String {

        fun String.handleBackAndDone(): String {
            return when(this) {
                "Back" -> "it.onBack()"
                "Done" -> "it.onDone()"
                else -> "to$this(it)"
            }
        }

        return this.filter { it.from.contains(state.name) }.joinToString("\n") {
            "is ${it.name} -> ${it.name.handleBackAndDone()}"
        }
    }
}
