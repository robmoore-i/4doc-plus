import docbuild.docker.DockerAppExtension
import docbuild.docker.DockerBuild
import docbuild.shell.Shell

plugins {
    base
}

val dockerApp = extensions.create<DockerAppExtension>("dockerApp").apply {
    imageName.set(name)
    containerName.set("$name-local")
}

tasks {
    val dockerTaskGroup = "docker"

    val dockerBuild by registering(DockerBuild::class) {
        group = dockerTaskGroup
        description = "Builds the docker image."
        t.set("${dockerApp.imageName.get()}:latest")
    }

    register<Shell>("dockerUp") {
        group = dockerTaskGroup
        description = "Build the docker image and run a local container with it."
        dependsOn(dockerBuild)
        cmd.set(providers.provider {
            listOf("docker", "run", "-d", "-p", "8080:8080", "--name", dockerApp.containerName.get(), dockerApp.imageName.get())
        })
    }

    register<Shell>("dockerDown") {
        group = dockerTaskGroup
        description = "Bring down the local container."
        cmd.set(providers.provider { listOf("docker", "rm", "-f", dockerApp.containerName.get()) })
    }
}
