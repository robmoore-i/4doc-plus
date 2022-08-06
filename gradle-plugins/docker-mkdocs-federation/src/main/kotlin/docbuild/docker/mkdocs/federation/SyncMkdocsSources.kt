package docbuild.docker.mkdocs.federation

import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.FileSystemOperations
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.work.DisableCachingByDefault
import javax.inject.Inject

@DisableCachingByDefault
abstract class SyncMkdocsSources @Inject constructor(private val fsOps: FileSystemOperations) : DefaultTask() {

    @get:InputFiles
    abstract val mkdocsSourcesDir: ConfigurableFileCollection

    @get:OutputDirectory
    abstract val outputDir: DirectoryProperty

    @TaskAction
    fun sync() {
        for (file in mkdocsSourcesDir.files) {
            val projectName = file.parentFile.parentFile.name
            val contents = file.listFiles()!!.toList()
            if (contents.size != 2) {
                throw IllegalArgumentException("There should be exactly 2 files: 'mkdocs.yml' and 'docs'.")
            }
            val names = contents.map { it.name }
            if (!names.contains("mkdocs.yml") || !names.contains("docs")) {
                throw IllegalArgumentException("There should be exactly 2 files: 'mkdocs.yml' and 'docs'.")
            }
            fsOps.sync {
                from(file)
                into(outputDir.get().dir("$projectName-mkdocs"))
            }
        }
    }

    @Internal
    fun getProjectNames(): String {
        return outputDir.get().asFile.listFiles()?.toList()
            ?.joinToString(",") { f -> f.name.dropLast("-mkdocs".length) } ?: ""
    }
}