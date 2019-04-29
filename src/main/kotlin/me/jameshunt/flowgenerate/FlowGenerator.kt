package me.jameshunt.flowgenerate

import java.io.File

class FlowGenerator(private val file: File) {

    fun generate() {
        val states = PumlParser().parse(file)
    }

}

data class State(
    val name: String,
    val variables: Set<String>,
    val from: Set<String>
)


