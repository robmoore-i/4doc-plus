package docbuild.docker

import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.ProjectLayout
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import org.gradle.process.ExecOperations
import java.nio.file.Files
import javax.inject.Inject

/**
 * Represents `docker build <buildContext> -t <t>`
 *
 * See: https://docs.docker.com/engine/reference/commandline/build/
 */
abstract class DockerBuild @Inject constructor(
    private val execOps: ExecOperations,
    private val layout: ProjectLayout
) : DefaultTask() {

    @get:Input
    abstract val buildContext: Property<String>

    @get:InputFile
    abstract val dockerfile: RegularFileProperty

    @get:InputFiles
    abstract val resources: ConfigurableFileCollection

    @get:Input
    abstract val t: Property<String>

    @TaskAction
    fun build() {
        val d = layout.buildDirectory.dir(name).get().asFile
        d.mkdirs()
        dockerfile.get().asFile.copyTo(d.resolve("Dockerfile"), overwrite = true)
        execOps.exec {
            commandLine("docker", "build", buildContext.get(), "-t", t.get())
        }.assertNormalExitValue()
    }
}