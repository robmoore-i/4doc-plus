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

dependencies {
    implementation(project(":shell"))
    implementation(project(":docker"))
    implementation("com.google.guava:guava:31.1-jre")
}
