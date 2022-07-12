fun Task.shell(cmd: List<String>) {
    doFirst {
        exec {
            commandLine(cmd)
        }
    }
}

tasks {
    register("mkdocsServe") {
        shell(listOf("mkdocs", "serve"))
    }

    val mkdocsBuild by registering {
        shell(listOf("mkdocs", "build"))
    }

    val imageName = "gbt"

    val dockerBuild by registering {
        mustRunAfter(mkdocsBuild)
        shell(listOf("docker", "build", ".", "-t", imageName))
    }

    val containerName = "gbt-local"

    val dockerRun by registering {
        mustRunAfter(dockerBuild)
        shell(listOf("docker", "run", "-d", "-p", "8080:8080", "--name", containerName, imageName))
    }

    register("dockerDown") {
        shell(listOf("docker", "rm", "-f", containerName))
    }

    register("dockerUp") {
        dependsOn(mkdocsBuild, dockerBuild, dockerRun)
    }

    register("flyDeploy") {
        shell(listOf("fly", "deploy"))
    }
}