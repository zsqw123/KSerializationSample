package zsu.serial.demo

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
class Request<T>(
    val body: T,
)

fun <T> submitRequest(content: T) {
    val request = Request(content)
    println(Json.encodeToString(request))
}

fun main() {
    submitRequest(1)
}
