// JS-specific entry point
@JsExport
fun greetFromJs(): String {
    return Greeting().greet()
}

fun main() {
    console.log(Greeting().greet())
}
