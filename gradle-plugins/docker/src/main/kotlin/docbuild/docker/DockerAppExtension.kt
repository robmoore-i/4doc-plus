package docbuild.docker

import org.gradle.api.provider.Property

abstract class DockerAppExtension {
    abstract val imageName: Property<String>
    abstract val containerName: Property<String>
}