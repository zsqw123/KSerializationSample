package zsu.serial.tu

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.serializer

@Serializable(TU.Serializer::class)
class TU<T, U>(t: T, u: U) {
    class Serializer<T, U>(tSerializer: KSerializer<*>, uSerializer: KSerializer<T>) : KSerializer<TU<T, U>> {
        override val descriptor: SerialDescriptor = error("")
        override fun deserialize(decoder: Decoder): TU<T, U> = error("")
        override fun serialize(encoder: Encoder, value: TU<T, U>) = error("")
    }
}

fun main() {
    TU.Serializer<String, String>(serializer<String>(), serializer<String>())
}

object EmptyKSerializer : KSerializer<Nothing> {
    private const val ILLEGAL_EMPTY_SERIALIZER =
        "`EmptyKSerializer` is a stub for type parameter, prohibited use directly as normal KSerializer"
    override val descriptor: SerialDescriptor get() = error(ILLEGAL_EMPTY_SERIALIZER)
    override fun deserialize(decoder: Decoder): Nothing = error(ILLEGAL_EMPTY_SERIALIZER)
    override fun serialize(encoder: Encoder, value: Nothing) = error(ILLEGAL_EMPTY_SERIALIZER)
}
