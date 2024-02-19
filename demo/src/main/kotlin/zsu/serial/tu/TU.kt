package zsu.serial.tu

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(TU.Serializer::class)
class TU<T, U>(t: T, u: U) {
    class Serializer<T, U>(tSerializer: KSerializer<T>?, uSerializer: KSerializer<T>) : KSerializer<TU<T, U>> {
        override val descriptor: SerialDescriptor = error("")
        override fun deserialize(decoder: Decoder): TU<T, U> = error("")
        override fun serialize(encoder: Encoder, value: TU<T, U>) = error("")
    }
}
