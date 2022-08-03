import docbuild.docker.DockerBuild
import docbuild.docker.mkdocs.federation.DockerfileTemplate
import docbuild.docker.mkdocs.federation.NginxConfTemplate
import docbuild.docker.mkdocs.federation.RenderTemplateFile
import docbuild.mkdocs.Mkdocs

plugins {
    base
    id("docbuild.docker-mkdocs-federation")
    id("docbuild.fly")
}

dockerApps {
    register("docs")
}

val mkdocsSourcesGe: Configuration by configurations.creating {
    isCanBeConsumed = false
    isCanBeResolved = true
    attributes.attribute(Mkdocs.mkdocsAttribute, objects.named(Mkdocs.MKDOCS_SOURCES))
}

val mkdocsSourcesGbt: Configuration by configurations.creating {
    isCanBeConsumed = false
    isCanBeResolved = true
    attributes.attribute(Mkdocs.mkdocsAttribute, objects.named(Mkdocs.MKDOCS_SOURCES))
}

dependencies {
    mkdocsSourcesGe(project(":ge"))
    mkdocsSourcesGbt(project(":gbt"))
}

val federationTaskGroup = "mkdocs federation"
val mkdocsSources = layout.buildDirectory.dir("mkdocsSources")

val syncMkdocsSourcesGe by tasks.registering(Sync::class) {
    group = federationTaskGroup
    from(mkdocsSourcesGe)
    into(mkdocsSources.map { it.dir("ge-mkdocs") })
}

val syncMkdocsSourcesGbt by tasks.registering(Sync::class) {
    group = federationTaskGroup
    from(mkdocsSourcesGbt)
    into(mkdocsSources.map { it.dir("gbt-mkdocs") })
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
    resources.from(syncMkdocsSourcesGe)
    resources.from(syncMkdocsSourcesGbt)
}

// Fly

flyApps {
    register("docs")
}
