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

    val dockerBuild by registering {
        mustRunAfter(mkdocsBuild)
        shell(listOf("docker", "build", ".", "-t", "gradle-4doc"))
    }

    val containerName = "gradle-4doc-plus"

    val dockerRun by registering {
        mustRunAfter(dockerBuild)
        shell(listOf("docker", "run", "-d", "-p", "8080:8080", "--name", containerName, "gradle-4doc"))
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