import docbuild.docker.DockerAppExtension
import docbuild.docker.DockerBuild
import docbuild.shell.shell

val dockerApp = extensions.create<DockerAppExtension>("dockerApp").apply {
    imageName.set(name)
    containerName.set("$name-local")
}

tasks {
    val dockerBuild by registering(DockerBuild::class) {
        buildContext.set(".")
        dockerfile.set(layout.projectDirectory.file("Dockerfile"))
        t.set(dockerApp.imageName.get())
    }

    val dockerRun by shell(providers.provider {
        listOf("docker", "run", "-d", "-p", "8080:8080", "--name", dockerApp.containerName.get(), dockerApp.imageName.get())
    }) {
        dependsOn(dockerBuild)
    }

    shell("dockerDown", providers.provider { listOf("docker", "rm", "-f", dockerApp.containerName.get()) })

    register("dockerUp") {
        dependsOn(dockerRun)
    }
}
