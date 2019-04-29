package me.jameshunt.flowgenerate

import java.io.File

fun main() {
    val puml = File("src/main/resources/Summary.puml")
    FlowGenerator(puml).generate()
}
