package docbuild.docker

import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.kotlin.dsl.property

open class DockerApp(@Suppress("CanBeParameter") val name: String, objects: ObjectFactory) {
    val imageName: Property<String> = objects.property<String>().convention(name)
    val containerName: Property<String> = objects.property<String>().convention("$name-local")
}