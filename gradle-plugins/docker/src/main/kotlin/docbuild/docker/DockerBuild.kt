package docbuild.docker

import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import org.gradle.process.ExecOperations
import javax.inject.Inject

/**
 * Represents `docker build <buildContext> -t <t>`
 *
 * See: https://docs.docker.com/engine/reference/commandline/build/
 */
abstract class DockerBuild @Inject constructor(private val execOps: ExecOperations) : DefaultTask() {

    @get:Input
    abstract val buildContext: Property<String>

    @get:Internal
    abstract val t: Property<String>

    @TaskAction
    fun build() {
        execOps.exec {
            commandLine("docker", "build", buildContext.get(), "-t", t.get())
        }.assertNormalExitValue()
    }
}