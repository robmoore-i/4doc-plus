import docbuild.docker.DockerBuild
import docbuild.shell.shell

plugins {
    base
    id("docbuild.shell")
    id("docbuild.docker")
    id("docbuild.mkdocs")
}

tasks {
    named<DockerBuild>("dockerBuild") {
        resources.from(layout.projectDirectory.file("nginx.conf"))
        resources.from(layout.projectDirectory.file("mkdocs.yml"))
        resources.from(layout.projectDirectory.dir("docs"))
    }

    shell("flyDeploy", providers.provider { listOf("fly", "deploy") })
}
