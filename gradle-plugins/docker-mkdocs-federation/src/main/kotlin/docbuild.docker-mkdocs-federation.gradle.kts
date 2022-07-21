import docbuild.docker.DockerBuild
import docbuild.docker.mkdocs.federation.FederatedMkdocsDockerApp

plugins {
    id("docbuild.docker")
}

val federatedMkdocsDockerAppContainer = container(FederatedMkdocsDockerApp::class)
extensions.add<NamedDomainObjectContainer<FederatedMkdocsDockerApp>>("federatedMkdocsDockerApps", federatedMkdocsDockerAppContainer)

federatedMkdocsDockerAppContainer.all {
    dockerApps {
        create(name) {
            this@create.imageName.set(this@all.imageName)
            this@create.containerName.set(this@all.containerName)
        }
    }
}


afterEvaluate {
    federatedMkdocsDockerAppContainer.all {
        val syncMkdocsSourcesTasks = projectNames.get().map { projectName ->
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
            val federationTaskGroup = "mkdocs federation"

            val syncMkdocsConfiguration = tasks.register<Sync>("syncMkdocsConfiguration${projectName.capitalize()}") {
                group = federationTaskGroup
                from(mkdocsConfiguration)
                into(mkdocsSources)
            }

            val syncMkdocsDocs = tasks.register<Sync>("syncMkdocsDocs${projectName.capitalize()}") {
                group = federationTaskGroup
                from(mkdocsDocs)
                into(mkdocsSources.map { it.dir("docs") })
            }

            tasks.register("syncMkdocsSources${projectName.capitalize()}") {
                group = federationTaskGroup
                dependsOn(syncMkdocsConfiguration, syncMkdocsDocs)
                outputs.dir(syncMkdocsConfiguration.map { it.destinationDir })
            }
        }

        tasks.named<DockerBuild>("dockerBuild${imageName.get().capitalize()}") {
            // TODO: Template and generate Dockerfile and nginx.conf as part of federation.
            resources.from(layout.projectDirectory.file("nginx.conf"))
            syncMkdocsSourcesTasks.forEach { resources.from(it) }
        }
    }
}