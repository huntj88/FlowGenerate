package me.jameshunt.generate

import org.junit.Test

class FlowGeneratorTest {

    @Test
    fun testGenerate() {
        generateCodeTest()
    }

    @Test
    fun testParseV2() {

        val fileAsString = """
            import java.util.concurrent.atomic.AtomicInteger

            @startuml
            
            Settings : val blah: AtomicInteger?
            
            [*] -> Settings
            
            Settings --> UserSettings
            Settings --> Logout
            Settings --> Back
            
            Settings --> NotLoggedIn
            UserSettings --> Settings
            
            Logout --> Settings
            
            NotLoggedIn --> Login
            NotLoggedIn --> Back
            
            Login --> Settings
            
            Settings -> Done
            
            @enduml

        """.trimIndent()
        PumlParser().parse(fileAsString).let(::println)
    }
}
