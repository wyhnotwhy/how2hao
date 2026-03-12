// JVM-specific entry point
fun main() {
    val greeting = Greeting()
    println(greeting.greet())
    println("Running on JVM: ${System.getProperty("java.version")}")
}
