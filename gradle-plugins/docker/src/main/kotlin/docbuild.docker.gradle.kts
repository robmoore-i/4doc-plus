import docbuild.docker.DockerAppExtension
import docbuild.docker.DockerBuild
import docbuild.shell.Shell

val dockerApp = extensions.create<DockerAppExtension>("dockerApp").apply {
    imageName.set(name)
    containerName.set("$name-local")
}

tasks {
    val dockerBuild by registering(DockerBuild::class) {
        t.set(dockerApp.imageName.get())
    }

    val dockerRun by registering(Shell::class) {
        dependsOn(dockerBuild)
        cmd.set(providers.provider {
            listOf("docker", "run", "-d", "-p", "8080:8080", "--name", dockerApp.containerName.get(), dockerApp.imageName.get())
        })
    }

    register<Shell>("dockerDown") {
        cmd.set(providers.provider { listOf("docker", "rm", "-f", dockerApp.containerName.get()) })
    }

    register("dockerUp") {
        dependsOn(dockerRun)
    }
}
