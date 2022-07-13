import docbuild.shell.shell

plugins {
    id("docbuild.shell")
    id("docbuild.docker")
    id("docbuild.mkdocs")
}

tasks {
    named("dockerBuild") {
        dependsOn(named("mkdocsBuild"))
    }

    shell("flyDeploy", providers.provider { listOf("fly", "deploy") })
}
