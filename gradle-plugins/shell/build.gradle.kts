plugins {
    base
    `kotlin-dsl`
}

kotlinDslPluginOptions {
    jvmTarget.set("11")
}

repositories {
    mavenCentral()
}
