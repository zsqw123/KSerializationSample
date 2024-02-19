plugins {
    id("inside") apply false
    kotlin("plugin.serialization") version "2.0.0-Beta4" apply false
}

subprojects {
    apply(plugin = "inside")
}
