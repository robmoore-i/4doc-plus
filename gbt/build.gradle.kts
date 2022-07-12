fun Task.shell(cmd: List<String>) {
    doFirst {
        exec {
            commandLine(cmd)
        }
    }
}

fun TaskContainer.shell(name: String, cmd: List<String>): TaskProvider<Task> =
    register(name) {
        shell(cmd)
    }

fun <T : Task, C : NamedDomainObjectContainer<T>> C.shell(
    cmd: List<String>,
    action: T.() -> Unit = {}
): RegisteringDomainObjectDelegateProviderWithAction<out C, T> =
    RegisteringDomainObjectDelegateProviderWithAction.of(this) {
        shell(cmd)
        action(this)
    }

object DockerApp {
    const val imageName = "gbt"
    const val containerName = "gbt-local"
}

tasks {
    shell("mkdocsServe", listOf("mkdocs", "serve"))

    val mkdocsBuild by shell(listOf("mkdocs", "build"))

    val dockerBuild by shell(listOf("docker", "build", ".", "-t", DockerApp.imageName)) {
        dependsOn(mkdocsBuild)
    }

    val dockerRun by shell(listOf("docker", "run", "-d", "-p", "8080:8080", "--name", DockerApp.containerName, DockerApp.imageName)) {
        dependsOn(dockerBuild)
    }

    shell("dockerDown", listOf("docker", "rm", "-f", DockerApp.containerName))

    register("dockerUp") {
        dependsOn(mkdocsBuild, dockerBuild, dockerRun)
    }

    shell("flyDeploy", listOf("fly", "deploy"))
}