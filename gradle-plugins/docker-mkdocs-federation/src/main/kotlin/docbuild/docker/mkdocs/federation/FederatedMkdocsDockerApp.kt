package docbuild.docker.mkdocs.federation

import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.kotlin.dsl.listProperty
import org.gradle.kotlin.dsl.property

open class FederatedMkdocsDockerApp(@Suppress("CanBeParameter") val name: String, objects: ObjectFactory) {
    val imageName: Property<String> = objects.property<String>().convention(name)
    val containerName: Property<String> = objects.property<String>().convention("$name-local")

    /**
     * The names of the projects whose exported configurations will be used to build the federated app image.
     */
    val projectNames: ListProperty<String> = objects.listProperty()
}