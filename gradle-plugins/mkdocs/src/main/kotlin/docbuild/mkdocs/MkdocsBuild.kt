package docbuild.mkdocs

import org.gradle.api.DefaultTask
import org.gradle.api.file.Directory
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.ProjectLayout
import org.gradle.api.file.RegularFile
import org.gradle.api.model.ObjectFactory
import org.gradle.api.tasks.*
import org.gradle.process.ExecOperations
import javax.inject.Inject

@CacheableTask
abstract class MkdocsBuild @Inject constructor(
    objects: ObjectFactory,
    layout: ProjectLayout,
    private val execOps: ExecOperations
) : DefaultTask() {

    @PathSensitive(value = PathSensitivity.RELATIVE)
    @get:InputFile
    val config: RegularFile = layout.projectDirectory.file("mkdocs.yml")

    @Suppress("unused" /* Recorded here as an input */)
    @PathSensitive(value = PathSensitivity.RELATIVE)
    @get:InputDirectory
    val docs: Directory = layout.projectDirectory.dir("docs")

    @get:OutputDirectory
    val outputDir: DirectoryProperty = objects.directoryProperty().convention(layout.buildDirectory.dir(name))

    @TaskAction
    fun build() {
        execOps.exec {
            commandLine("mkdocs", "build", "-d", outputDir.get().asFile.absolutePath)
        }.assertNormalExitValue()
    }
}