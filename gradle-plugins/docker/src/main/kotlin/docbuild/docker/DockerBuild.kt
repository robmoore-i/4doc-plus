package docbuild.docker

import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.ProjectLayout
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.TaskAction
import org.gradle.process.ExecOperations
import javax.inject.Inject

/**
 * Represents `docker build <buildContext> -t <t>`
 *
 * See: https://docs.docker.com/engine/reference/commandline/build/
 */
abstract class DockerBuild @Inject constructor(
    objects: ObjectFactory,
    private val execOps: ExecOperations,
    private val layout: ProjectLayout
) : DefaultTask() {

    @get:InputFile
    val dockerfile: RegularFileProperty = objects.fileProperty().convention(layout.projectDirectory.file("Dockerfile"))

    @get:InputFiles
    abstract val resources: ConfigurableFileCollection

    @get:Input
    abstract val t: Property<String>

    @TaskAction
    fun build() {
        val d = layout.buildDirectory.dir(name).get().asFile
        d.mkdirs()
        dockerfile.get().asFile.copyTo(d.resolve("Dockerfile"), overwrite = true)
        resources.forEach { it.copyRecursively(d.resolve(it.name), overwrite = true) }
        execOps.exec {
            commandLine("docker", "build", d.absolutePath, "-t", t.get())
        }.assertNormalExitValue()
    }
}