import docbuild.docker.DockerApp
import docbuild.docker.DockerBuild
import docbuild.shell.Shell

plugins {
    base
}

val dockerAppContainer = container(DockerApp::class)
extensions.add<NamedDomainObjectContainer<DockerApp>>("dockerApps", dockerAppContainer)

val dockerTaskGroup = "docker"

dockerAppContainer.all {
    val dockerBuild = tasks.register<DockerBuild>("dockerBuild${imageName.get().capitalize()}") {
        group = dockerTaskGroup
        description = "Builds the ${imageName.get()} docker image."
        t.set("${imageName.get()}:latest")
    }

    tasks.register<Shell>("dockerUp${imageName.get().capitalize()}") {
        group = dockerTaskGroup
        description = "Build the docker image and run a local container with it."
        dependsOn(dockerBuild)
        cmd.set(providers.provider {
            listOf("docker", "run", "-d", "-p", "8080:8080", "--name", containerName.get(), imageName.get())
        })
    }

    tasks.register<Shell>("dockerDown${imageName.get().capitalize()}") {
        group = dockerTaskGroup
        description = "Bring down the local container."
        cmd.set(providers.provider { listOf("docker", "rm", "-f", containerName.get()) })
    }
}
