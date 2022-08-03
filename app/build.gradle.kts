import docbuild.docker.DockerBuild
import docbuild.docker.mkdocs.federation.DockerfileTemplate
import docbuild.docker.mkdocs.federation.NginxConfTemplate
import docbuild.docker.mkdocs.federation.PrepareMkdocsSources
import docbuild.docker.mkdocs.federation.RenderTemplateFile

plugins {
    base
    id("docbuild.docker-mkdocs-federation")
    id("docbuild.fly")
}

dockerApps {
    register("docs")
}

val mkdocsSources by configurations.existing

dependencies {
    mkdocsSources(project(":ge"))
    mkdocsSources(project(":gbt"))
}

val syncMkdocsSources by tasks.registering(PrepareMkdocsSources::class) {
    mkdocsSourcesDir.from(mkdocsSources)
    outputDir.set(layout.buildDirectory.dir("mkdocs-sources"))
}

// Docker

val renderDockerfile = tasks.register<RenderTemplateFile>("renderDockerfileForDocs") {
    template.set(DockerfileTemplate.template)
    templateVariables.put("projectNames", "ge,gbt")
    outputFile.set(layout.buildDirectory.dir(name).map { it.file("Dockerfile") })
    renderFunction = { template, variables ->
        val projectNames = variables["projectNames"]?.split(",")!!
        DockerfileTemplate.render(template, projectNames)
    }
}

val renderNginxConf = tasks.register<RenderTemplateFile>("renderNginxConfForDocs") {
    template.set(NginxConfTemplate.template)
    templateVariables.put("projectNames", "ge,gbt")
    outputFile.set(layout.buildDirectory.dir(name).map { it.file("nginx.conf") })
    renderFunction = { template, variables ->
        val projectNames = variables["projectNames"]?.split(",")!!
        NginxConfTemplate.render(template, projectNames)
    }
}

tasks.named<DockerBuild>("dockerBuildDocs") {
    dockerfile.set(renderDockerfile.flatMap { it.outputFile })
    resources.from(renderNginxConf.flatMap { it.outputFile })
    resources.from(syncMkdocsSources.flatMap { it.outputDir })
}

// Fly

flyApps {
    register("docs")
}
