### Support nullable type parameter for custom serializer

I want to jsonify an object which contains generic types, so I tried following codes 
at first.

```kotlin
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
```

The error shows that I must use inline function with `reified T`.

```text
Exception in thread "main" java.lang.IllegalArgumentException: Captured type parameter T of zsu.serial.demo.DemoKt.submitRequest from generic non-reified function. Such functionality cannot be supported because T is erased, either specify serializer explicitly or make calling function inline with reified T.
	at zsu.serial.demo.DemoKt.submitRequest(Demo.kt:21)
	at zsu.serial.demo.DemoKt.main(Demo.kt:18)
	at zsu.serial.demo.DemoKt.main(Demo.kt)
```

I know that kotlin needs object's "real" type to find proper serializer, but we need to 
wrap request body with very complex processing in our real business cases. Kotlin's 
inline is infective, it will inline our huge wrapper logic. So I'm trying to create 
custom serializer to solve this problem.

In following case, I store the `bodyType` in the wrapper class, and add custom serializer 
to serialize class by `bodyType`'s serializer. Now we only needs to use inline when initialize 
this object, we don't need to inline every processing function invoke if we have multiple 
level wrappers (e.g. `HttpWrap<RequestWrap<HeaderWrap<T>>>`)

```kotlin
@Serializable(Request.Serializer::class)
class Request<T>(
    val body: T, val bodyType: KType,
) {
    class Serializer<T> : KSerializer<Request<T>> {
        // ... serialize by using bodyType to create a serializer
    }
}

inline fun <reified T> Request(body: T) = Request(body, typeOf<T>())

fun <T> submitRequest(request: Request<T>) {
    println(Json.encodeToString(request))
}

fun main() {
    submitRequest(Request(1))
}
```

But same error occurred after I run it. I found that kotlin serialization will try to 
resolve every `typeParameter` when build serializer even I **don't use** such information in
my custom resolver.

So here is my temporary solution, transform to a new object without type parameter. But this 
solution needs us to call `serializable` function to transform object by manual.

```kotlin
class Request<T>(
    val body: T, val bodyType: KType,
) {
    fun serializable() = SerializableRequest(body, bodyType)
}

@Serializable(SerializableRequest.Serializer::class)
class SerializableRequest(val body: Any?, val bodyType: KType) {
    class Serializer : KSerializer<SerializableRequest> {
        // ... serialize by using bodyType to create a serializer
    }
}

inline fun <reified T> Request(body: T) = Request(body, typeOf<T>())

fun <T> submitRequest(request: Request<T>) {
    println(Json.encodeToString(request.serializable()))
}

fun main() {
    submitRequest(Request(1))
}
```

I have a better solution if we can modify `KSerializer`'s initialization. Mark type parameter 
serializer's type as nullable to indicates that this custom `KSerializer` don't need specified 
type parameter as input. Here shows a type `TU` with optional `tSerializer` and required `uSerializer`.

```kotlin
@Serializable(TU.Serializer::class)
class TU<T, U>(t: T, u: U) {
    class Serializer<T, U>(
        tSerializer: KSerializer<T>?, // optional, null if we cannot found T's Serializer
        uSerializer: KSerializer<U>
    ) : KSerializer<TU<T, U>>
}
```

or replace nullable `KSerializer` with a `EmptyKSerializer`. This will be easier because we 
don't need to modify existed compiler plugin codes, but not suit for express `nullable`

```kotlin
class Serializer<T, U>(
    tSerializer: KSerializer<*>, // * means any serializer is acceptable
    uSerializer: KSerializer<T>,
) : KSerializer<TU<T, U>>

object EmptyKSerializer : KSerializer<Nothing>
```

---

Related to some other discussions about serializer for generic types:

- issue #2555 [Support polymorphic subclass serializers with inferred type arguments by refactoring Serializers to bind to KTypes instead of / in addition to KClass's](https://github.com/Kotlin/kotlinx.serialization/issues/2555)
- [Kotlinx Serialization: How to circumvent reified typeargs for deserialization?](https://www.reddit.com/r/Kotlin/comments/j37tyn/kotlinx_serialization_how_to_circumvent_reified/)
