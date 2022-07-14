package docbuild.fly

import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.gradle.process.ExecOperations
import org.gradle.work.DisableCachingByDefault
import javax.inject.Inject

@DisableCachingByDefault
abstract class FlyDeploy @Inject constructor(private val execOps: ExecOperations) : DefaultTask() {

    @get:Input
    abstract val appName: Property<String>

    @get:Input
    abstract val image: Property<String>

    @TaskAction
    fun deploy() {
        execOps.exec {
            commandLine("fly", "deploy", "--app", appName.get(), "--image", image.get())
        }.assertNormalExitValue()
        logger.lifecycle("Deployed {} using image {}", appName.get(), image.get())
    }
}