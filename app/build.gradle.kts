import docbuild.docker.DockerBuild

plugins {
    base
    id("docbuild.fly")
}

dockerApps {
    register("docs")
}

flyApps {
    register("docs")
}

val syncMkdocsSourcesTasks = listOf("gbt", "ge").map { projectName ->
    val mkdocsConfiguration = configurations.create("mkdocsConfiguration${projectName.capitalize()}") {
        isCanBeConsumed = false
        isCanBeResolved = true
        attributes.attribute(Usage.USAGE_ATTRIBUTE, objects.named(name))
    }

    val mkdocsDocs = configurations.create("mkdocsDocs${projectName.capitalize()}") {
        isCanBeConsumed = false
        isCanBeResolved = true
        attributes.attribute(Usage.USAGE_ATTRIBUTE, objects.named(name))
    }

    dependencies {
        add(mkdocsConfiguration.name, project(":$projectName"))
        add(mkdocsDocs.name, project(":$projectName"))
    }

    val mkdocsSources = layout.buildDirectory.dir("mkdocsSources").map { it.dir("${projectName}-mkdocs") }

    val syncMkdocsConfiguration = tasks.register<Sync>("syncMkdocsConfiguration${projectName.capitalize()}") {
        from(mkdocsConfiguration)
        into(mkdocsSources)
    }

    val syncMkdocsDocs = tasks.register<Sync>("syncMkdocsDocs${projectName.capitalize()}") {
        from(mkdocsDocs)
        into(mkdocsSources.map { it.dir("docs") })
    }

    tasks.register("syncMkdocsSources${projectName.capitalize()}") {
        dependsOn(syncMkdocsConfiguration, syncMkdocsDocs)
        outputs.dir(syncMkdocsConfiguration.map { it.destinationDir })
    }
}

tasks.named<DockerBuild>("dockerBuildDocs") {
    resources.from(layout.projectDirectory.file("nginx.conf"))
    syncMkdocsSourcesTasks.forEach { resources.from(it) }
}
