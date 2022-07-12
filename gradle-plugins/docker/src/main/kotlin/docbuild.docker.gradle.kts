import docbuild.docker.DockerAppExtension
import docbuild.shell.shell

val dockerApp = extensions.create<DockerAppExtension>("dockerApp")

tasks {
    val dockerBuild by shell(providers.provider { listOf("docker", "build", ".", "-t", dockerApp.imageName.get()) })

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
