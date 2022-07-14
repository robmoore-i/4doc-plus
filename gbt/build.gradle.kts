import docbuild.docker.DockerBuild
import docbuild.shell.shell

plugins {
    id("docbuild.shell")
    id("docbuild.docker")
    id("docbuild.mkdocs")
}

tasks {
    named<DockerBuild>("dockerBuild") {
        resources.from(layout.projectDirectory.file("nginx.conf"))
        resources.from(named("mkdocsBuild"))
    }

    shell("flyDeploy", providers.provider { listOf("fly", "deploy") })
}
