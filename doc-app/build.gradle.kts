import docbuild.docker.DockerBuild

plugins {
    base
    id("docbuild.fly")
}

dockerApp {
    imageName.set("gbt")
    containerName.set("gbt-local")
}

flyApp {
    appName.set("gbt")
}

val mkdocsConfiguration by configurations.creating {
    isCanBeConsumed = false
    isCanBeResolved = true
    attributes.attribute(Usage.USAGE_ATTRIBUTE, objects.named(name))
}

val mkdocsDocs by configurations.creating {
    isCanBeConsumed = false
    isCanBeResolved = true
    attributes.attribute(Usage.USAGE_ATTRIBUTE, objects.named(name))
}

dependencies {
    add(mkdocsConfiguration.name, project(":gbt"))
    add(mkdocsDocs.name, project(":gbt"))
}

tasks {
    named<DockerBuild>("dockerBuild") {
        resources.from(layout.projectDirectory.file("nginx.conf"))
        resources.from(mkdocsConfiguration)
        resources.from(mkdocsDocs)
    }
}