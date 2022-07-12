import docbuild.docker.dockerApp
import docbuild.shell.shell

plugins {
    id("docbuild.shell")
    id("docbuild.docker")
    id("docbuild.mkdocs")
}

dockerApp {
    imageName.set("gbt")
    containerName.set("gbt-local")
}

tasks {
    named("dockerBuild") {
        dependsOn(named("mkdocsBuild"))
    }

    shell("flyDeploy", providers.provider { listOf("fly", "deploy") })
}
