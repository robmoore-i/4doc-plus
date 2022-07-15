import docbuild.docker.DockerBuild

plugins {
    base
    id("docbuild.fly")
}

dockerApps {
    register("gbt")
    register("ge")
}

flyApps {
    register("gbt")
}

dockerApps.all {
    val mkdocsConfiguration = configurations.create("mkdocsConfiguration${name.capitalize()}") {
        isCanBeConsumed = false
        isCanBeResolved = true
        attributes.attribute(Usage.USAGE_ATTRIBUTE, objects.named(name))
    }

    val mkdocsDocs = configurations.create("mkdocsDocs${name.capitalize()}") {
        isCanBeConsumed = false
        isCanBeResolved = true
        attributes.attribute(Usage.USAGE_ATTRIBUTE, objects.named(name))
    }

    dependencies {
        add(mkdocsConfiguration.name, project(":$name"))
        add(mkdocsDocs.name, project(":$name"))
    }

    tasks.named<DockerBuild>("dockerBuild${imageName.get().capitalize()}") {
        resources.from(layout.projectDirectory.file("nginx.conf"))
        resources.from(mkdocsConfiguration)
        resources.from(mkdocsDocs)
    }
}