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

object DockerApp {
    const val imageName = "gbt"
    const val containerName = "gbt-local"
}

tasks {
    shell("mkdocsServe", listOf("mkdocs", "serve"))

    val mkdocsBuild by registering {
        shell(listOf("mkdocs", "build"))
    }

    val dockerBuild by registering {
        dependsOn(mkdocsBuild)
        shell(listOf("docker", "build", ".", "-t", DockerApp.imageName))
    }

    val dockerRun by registering {
        dependsOn(dockerBuild)
        shell(listOf("docker", "run", "-d", "-p", "8080:8080", "--name", DockerApp.containerName, DockerApp.imageName))
    }

    shell("dockerDown", listOf("docker", "rm", "-f", DockerApp.containerName))

    register("dockerUp") {
        dependsOn(mkdocsBuild, dockerBuild, dockerRun)
    }

    shell("flyDeploy", listOf("fly", "deploy"))
}