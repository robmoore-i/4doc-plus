package docbuild.shell

import org.gradle.api.DefaultTask
import org.gradle.api.provider.ListProperty
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.gradle.process.ExecOperations
import org.gradle.work.DisableCachingByDefault
import javax.inject.Inject

@DisableCachingByDefault
abstract class Shell @Inject constructor(private val exec: ExecOperations) : DefaultTask() {

    @get:Input
    abstract val cmd: ListProperty<String>

    @TaskAction
    fun run() {
        exec.exec {
            commandLine(cmd.get())
        }
    }
}