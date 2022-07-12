package docbuild.docker

import org.gradle.api.Project
import org.gradle.kotlin.dsl.the

fun Project.dockerApp(action: DockerAppExtension.() -> Unit) = project.the<DockerAppExtension>().apply(action)
