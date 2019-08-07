package me.jameshunt.generate

private val importRegex = "import [\\S]+".toRegex()
private val dataRegex = "([a-zA-Z]+)\\s*:\\s*((val|var) [a-zA-Z]+: \\S+)".toRegex()
private val transitionRegex = "(\\S+)\\s*[-]+>\\s*(\\S+)".toRegex()

fun String.isImport() = this.contains(importRegex)
fun String.isData() = this.contains(dataRegex)
fun String.isTransition() = this.contains(transitionRegex)

class PumlParser {

    sealed class LineType {
        data class Import(val line: String) : LineType()
        data class Data(val line: String) : LineType()
        data class Transition(val line: String) : LineType()
    }

    fun parse(fileAsString: String): FlowData {
        val lines = fileAsString.lines().mapNotNull {
            when {
                it.isTransition() -> LineType.Transition(it)
                it.isData() -> LineType.Data(it)
                it.isImport() -> LineType.Import(it)
                else -> null
            }
        }

        val importLines = lines.mapNotNull { it as? LineType.Import }.map { it.line }.toSet()
        val dataLines = lines.mapNotNull { it as? LineType.Data }
        val transitionLines = lines.mapNotNull { it as? LineType.Transition }

        val states = transitionLines
            .identifyStates()
            .addVariableIfAny(dataLines)
            .addTransitionFrom(transitionLines)

        return FlowData(states, states.inputOutput(), importLines)
    }

    private fun List<LineType.Transition>.identifyStates(): Set<State> {
        return this
            .map {
                listOf(
                    transitionRegex.find(it.line)!!.groups[1]?.value!!,
                    transitionRegex.find(it.line)!!.groups[2]?.value!!
                )
            }
            .flatten()
            .fold(setOf()) { acc, stateName ->
                acc + State(
                    name = stateName,
                    variable = null,
                    transitionFrom = setOf()
                )
            }
    }

    private fun Set<State>.addVariableIfAny(dataLines: List<LineType.Data>): Set<State> {
        val statesWithVariables = dataLines
            .map { stateData ->
                val stateName = dataRegex.find(stateData.line)!!.groups[1]?.value!!
                val variable = dataRegex.find(stateData.line)!!.groups[2]?.value!!

                this.first { it.name == stateName }.let {
                    if (it.variable != null) throw IllegalStateException("only one variable per state plz")
                    it.copy(variable = variable)
                }
            }
            .map { Pair(it.name, it) }
            .toMap()

        val rawStates = this.map { Pair(it.name, it) }.toMap()
        return (rawStates + statesWithVariables).map { it.value }.toSet()
    }

    private fun Set<State>.addTransitionFrom(lines: List<LineType.Transition>): Set<State> {

        val existingStates: MutableMap<String, State> = this
            .map { Pair(it.name, it) }
            .toMap()
            .toMutableMap()

        lines
            .map { stateData ->
                val from = transitionRegex.find(stateData.line)!!.groups[1]?.value!!
                val to = transitionRegex.find(stateData.line)!!.groups[2]?.value!!
                this.first { it.name == to }.let {
                    it.copy(transitionFrom = it.transitionFrom + from)
                }
            }
            .forEach {
                val existing = existingStates[it.name]
                existingStates[it.name] = existing!!.copy(
                    transitionFrom = existing.transitionFrom + it.transitionFrom
                )
            }

        return existingStates.values.toSet()
    }

    private fun Set<State>.inputOutput(): Pair<String, String> {
        val input = this
            .first { it.transitionFrom.contains("[*]") }
            .variable
            ?.split(" ")
            ?.last()
            ?: "Unit"

        val output = this
            .firstOrNull { it.name == "Done" }
            ?.variable
            ?.split(" ")
            ?.last()
            ?: "Unit"

        return Pair(input, output)
    }
}

data class FlowData(
    val states: Set<State>,
    val inputOutput: Pair<String, String>,
    val imports: Set<String>
)

data class State(
    val name: String,
    val variable: String?,
    val transitionFrom: Set<String>
)
