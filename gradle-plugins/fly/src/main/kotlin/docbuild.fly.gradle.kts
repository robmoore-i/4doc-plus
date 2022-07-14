import docbuild.docker.DockerBuild
import docbuild.fly.FlyAppExtension
import docbuild.fly.FlyDeploy
import docbuild.shell.Shell

plugins {
    id("docbuild.docker")
}

val flyApp = extensions.create<FlyAppExtension>("flyApp").apply {
    appName.set(name)
}

tasks {
    val dockerBuild by existing(DockerBuild::class)

    val builtImage = dockerBuild.flatMap { it.t }
    val flyImage = providers.provider { "registry.fly.io/${flyApp.appName.get()}:${flyApp.imageTag}" }

    val flyTaskGroup = "fly"

    val dockerTag by registering(Shell::class) {
        group = flyTaskGroup
        description = "Tag the latest built image for the Fly registry."
        mustRunAfter(dockerBuild)
        cmd.set(providers.provider { listOf("docker", "tag", builtImage.get(), flyImage.get()) })
        doLast {
            logger.lifecycle("Tagged {} to {}", builtImage.get(), flyImage.get())
        }
    }

    val dockerPush by registering(Shell::class) {
        group = flyTaskGroup
        description = "Push the tagged image to the Fly registry."
        dependsOn(dockerTag)
        cmd.set(providers.provider { listOf("docker", "push", flyImage.get()) })
        doLast {
            logger.lifecycle("Pushed {}", flyImage.get())
        }
    }

    register<FlyDeploy>("flyDeploy") {
        group = flyTaskGroup
        description = "Deploy the Fly app by building a new image."
        dependsOn(dockerBuild, dockerPush)
        appName.set(flyApp.appName)
        image.set(flyImage)
    }

    register<FlyDeploy>("flyDeployLatest") {
        group = flyTaskGroup
        description = "Deploy the Fly app using the latest built local image."
        dependsOn(dockerTag, dockerPush)
        appName.set(flyApp.appName)
        image.set(flyImage)
    }

    register<FlyDeploy>("flyDeployImage") {
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
        appName.set(flyApp.appName)
        image.set(providers.gradleProperty(propertyName))
    }
}
