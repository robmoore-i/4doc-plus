package docbuild.fly


import org.gradle.api.DefaultTask
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.gradle.process.ExecOperations
import org.gradle.work.DisableCachingByDefault
import java.io.ByteArrayOutputStream
import javax.inject.Inject

@DisableCachingByDefault
abstract class FlyLaunch @Inject constructor(objects: ObjectFactory, private val execOps: ExecOperations) : DefaultTask() {

    @get:Input
    abstract val appName: Property<String>

    @get:Input
    abstract val image: Property<String>

    @get:Input
    val region: Property<String> = objects.property(String::class.java).convention("sin")

    @TaskAction
    fun deploy() {
        val out = ByteArrayOutputStream()
        val flyListCommand = listOf("fly", "list", "apps", "--json")
        execOps.exec {
            commandLine(flyListCommand)
            standardOutput = out
        }.assertNormalExitValue()
        val flyListOutput = out.toString()
        if (flyListOutput.contains(appName.get())) {
            logger.lifecycle("App already launched. See output of '${flyListCommand.joinToString(" ")}':\n$flyListOutput")
            return
        }

        execOps.exec {
            commandLine(
                "fly", "launch",
                "--name", appName.get(),
                "--image", image.get(),
                "--region", region.get(),
                "--local-only", "--now", "--auto-confirm"
            )
        }.assertNormalExitValue()
        logger.lifecycle("Launched '{}' in region '{}'.", appName.get(), region.get())
    }

}