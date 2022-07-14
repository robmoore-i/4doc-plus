import docbuild.docker.DockerBuild

plugins {
    base
    id("docbuild.mkdocs")
    id("docbuild.fly")
}

tasks {
    named<DockerBuild>("dockerBuild") {
        resources.from(layout.projectDirectory.file("nginx.conf"))
        resources.from(layout.projectDirectory.file("mkdocs.yml"))
        resources.from(layout.projectDirectory.dir("docs"))
    }
}
