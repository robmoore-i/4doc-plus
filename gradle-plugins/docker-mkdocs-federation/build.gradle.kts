plugins {
    `kotlin-dsl`
}

kotlinDslPluginOptions {
    jvmTarget.set("11")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":docker"))
}
