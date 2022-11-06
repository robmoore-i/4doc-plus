import docbuild.docker.DockerBuild
import docbuild.fly.FlyApp
import docbuild.fly.FlyDeploy
import docbuild.fly.FlyLaunch
import docbuild.shell.Shell
import com.google.common.base.CaseFormat

plugins {
    id("docbuild.docker")
}

val flyAppContainer = container(FlyApp::class)
extensions.add<NamedDomainObjectContainer<FlyApp>>("flyApps", flyAppContainer)

flyAppContainer.all {
    val caseConverter = CaseFormat.LOWER_HYPHEN.converterTo(CaseFormat.UPPER_CAMEL)
    val imageTaskFragment = caseConverter.convert(imageName.get())
    val dockerBuild = tasks.named<DockerBuild>("dockerBuild$imageTaskFragment")
    val localImage = dockerBuild.flatMap { it.t }

    val flyAppName = appName.get()
    val flyImage = providers.provider { "registry.fly.io/${flyAppName}:${imageTag}" }

    val flyTaskGroup = "fly"

    val dockerTag = tasks.register<Shell>("dockerTag$imageTaskFragment") {
        group = flyTaskGroup
        description = "Tag the latest built image for the Fly registry."
        mustRunAfter(dockerBuild)
        cmd.set(providers.provider { listOf("docker", "tag", localImage.get(), flyImage.get()) })
        doLast {
            logger.lifecycle("Tagged {} to {}", localImage.get(), flyImage.get())
        }
    }

    val dockerPush = tasks.register<Shell>("dockerPush$imageTaskFragment") {
        group = flyTaskGroup
        description = "Push the tagged image to the Fly registry."
        dependsOn(dockerTag)
        cmd.set(providers.provider { listOf("docker", "push", flyImage.get()) })
        doLast {
            logger.lifecycle("Pushed {}", flyImage.get())
        }
    }

    val flyAppTaskFragment = caseConverter.convert(flyAppName)

    tasks.register<FlyLaunch>("flyLaunch$flyAppTaskFragment") {
        group = flyTaskGroup
        description = "Launch the Fly app anew."
        dependsOn(dockerBuild)
        appName.set(flyAppName)
        image.set(localImage)
    }

    tasks.register<FlyDeploy>("flyDeploy$flyAppTaskFragment") {
        group = flyTaskGroup
        description = "Deploy the Fly app by building a new image."
        dependsOn(dockerBuild, dockerPush)
        appName.set(flyAppName)
        image.set(flyImage)
    }
}
