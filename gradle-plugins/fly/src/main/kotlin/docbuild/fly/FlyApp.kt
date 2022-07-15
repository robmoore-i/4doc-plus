package docbuild.fly

import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.kotlin.dsl.property

open class FlyApp(@Suppress("CanBeParameter") val name: String, objects: ObjectFactory) {
    val appName: Property<String> = objects.property<String>().convention(name)
    val imageName: Property<String> = objects.property<String>().convention(name)
    val imageTag = "${System.currentTimeMillis()}"
}