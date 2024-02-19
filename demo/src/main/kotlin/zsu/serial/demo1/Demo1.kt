package zsu.serial.demo1

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encodeToString
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlin.reflect.KType
import kotlin.reflect.typeOf

@Serializable(Request.Serializer::class)
class Request<T>(
    val body: T, val bodyType: KType,
) {
    class Serializer<T> : KSerializer<Request<T>> {
        override val descriptor: SerialDescriptor = error("")
        override fun deserialize(decoder: Decoder): Request<T> = error("")
        override fun serialize(encoder: Encoder, value: Request<T>) = error("")
    }
}

inline fun <reified T> Request(body: T) = Request(body, typeOf<T>())

fun <T> submitRequest(request: Request<T>) {
    println(Json.encodeToString(request))
}

fun main() {
    submitRequest(Request(1))
}
