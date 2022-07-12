package docbuild.shell

import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Task
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.TaskContainer
import org.gradle.api.tasks.TaskProvider
import org.gradle.kotlin.dsl.RegisteringDomainObjectDelegateProviderWithAction

fun Task.shell(cmd: Provider<List<String>>) {
    doFirst {
        this.project.exec {
            commandLine(cmd.get())
        }
    }
}

fun TaskContainer.shell(name: String, cmd: Provider<List<String>>): TaskProvider<Task> =
    register(name) {
        shell(cmd)
    }

fun <T : Task, C : NamedDomainObjectContainer<T>> C.shell(
    cmd: Provider<List<String>>,
    action: T.() -> Unit = {}
): RegisteringDomainObjectDelegateProviderWithAction<out C, T> =
    RegisteringDomainObjectDelegateProviderWithAction.of(this) {
        shell(cmd)
        action(this)
    }
