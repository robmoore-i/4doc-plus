package docbuild.docker.mkdocs.federation

import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

abstract class RenderTemplateFile : DefaultTask() {

    @get:Input
    abstract val template: Property<String>

    @get:Input
    abstract val templateVariables: MapProperty<String, String>

    @get:OutputFile
    abstract val outputFile: RegularFileProperty

    @get:Internal
    abstract var renderFunction: (String, Map<String, String>) -> String

    @TaskAction
    fun render() {
        val dockerfile = outputFile.get().asFile
        dockerfile.parentFile.mkdirs()
        dockerfile.writeText(renderFunction(template.get(), templateVariables.get()))
        logger.lifecycle("Rendered ${dockerfile.absolutePath}.")
    }
}