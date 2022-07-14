package docbuild.shell

import org.gradle.api.DefaultTask
import org.gradle.api.provider.ListProperty
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.gradle.work.DisableCachingByDefault
import javax.inject.Inject

@DisableCachingByDefault
abstract class Shell @Inject constructor() : DefaultTask() {

    @get:Input
    abstract val cmd: ListProperty<String>

    @TaskAction
    fun run() {
        project.exec {
            commandLine(cmd.get())
        }
    }
}