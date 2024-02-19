package zsu.serial.demo2

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encodeToString
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlin.reflect.KType
import kotlin.reflect.typeOf

class Request<T>(
    val body: T, val bodyType: KType,
) {
    fun serializable() = SerializableRequest(body, bodyType)
}

@Serializable(SerializableRequest.Serializer::class)
class SerializableRequest(val body: Any?, val bodyType: KType) {
    class Serializer : KSerializer<SerializableRequest> {
        override val descriptor: SerialDescriptor = error("")
        override fun deserialize(decoder: Decoder): SerializableRequest = error("")
        override fun serialize(encoder: Encoder, value: SerializableRequest) = error("")
    }
}

inline fun <reified T> Request(body: T) = Request(body, typeOf<T>())

fun <T> submitRequest(request: Request<T>) {
    println(Json.encodeToString(request.serializable()))
}

fun main() {
    submitRequest(Request(1))
}
