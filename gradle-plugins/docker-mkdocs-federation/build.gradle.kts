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
    implementation(project(":mkdocs"))
    implementation(project(":docker"))
}
