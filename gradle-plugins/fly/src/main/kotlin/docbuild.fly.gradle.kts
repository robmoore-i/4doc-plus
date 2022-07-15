import docbuild.docker.DockerBuild
import docbuild.fly.FlyApp
import docbuild.fly.FlyDeploy
import docbuild.shell.Shell

plugins {
    id("docbuild.docker")
}

val flyAppContainer = container(FlyApp::class)
extensions.add<NamedDomainObjectContainer<FlyApp>>("flyApps", flyAppContainer)

flyAppContainer.all {
    val dockerBuild = tasks.named<DockerBuild>("dockerBuild${imageName.get().capitalize()}")
    val localImage = dockerBuild.flatMap { it.t }

    val flyAppName = appName.get()
    val flyImage = providers.provider { "registry.fly.io/${flyAppName}:${imageTag}" }

    val flyTaskGroup = "fly"

    val dockerTag = tasks.register<Shell>("dockerTag${imageName.get().capitalize()}") {
        group = flyTaskGroup
        description = "Tag the latest built image for the Fly registry."
        mustRunAfter(dockerBuild)
        cmd.set(providers.provider { listOf("docker", "tag", localImage.get(), flyImage.get()) })
        doLast {
            logger.lifecycle("Tagged {} to {}", localImage.get(), flyImage.get())
        }
    }

    val dockerPush = tasks.register<Shell>("dockerPush${imageName.get().capitalize()}") {
        group = flyTaskGroup
        description = "Push the tagged image to the Fly registry."
        dependsOn(dockerTag)
        cmd.set(providers.provider { listOf("docker", "push", flyImage.get()) })
        doLast {
            logger.lifecycle("Pushed {}", flyImage.get())
        }
    }

    tasks.register<FlyDeploy>("flyDeploy${imageName.get().capitalize()}") {
        group = flyTaskGroup
        description = "Deploy the Fly app by building a new image."
        dependsOn(dockerBuild, dockerPush)
        appName.set(flyAppName)
        image.set(flyImage)
    }

    tasks.register<FlyDeploy>("flyDeployLatest${imageName.get().capitalize()}") {
        group = flyTaskGroup
        description = "Deploy the Fly app using the latest built local image."
        dependsOn(dockerTag, dockerPush)
        appName.set(flyAppName)
        image.set(flyImage)
    }

    tasks.register<FlyDeploy>("flyDeployImage${imageName.get().capitalize()}") {
        group = flyTaskGroup
        description = "Deploy the Fly app using a specific image."
        val propertyName = "image"
        doFirst {
            image.get().let {
                if (it.isBlank()) {
                    throw RuntimeException("Blank property '$propertyName'.")
                }
                if (it.count { c -> c == ':' } != 1) {
                    throw RuntimeException("Invalid image id '${it}'. Should contain exactly one colon (':').")
                }
            }
        }
        appName.set(flyAppName)
        image.set(providers.gradleProperty(propertyName))
    }
}
