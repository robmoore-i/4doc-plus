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

// Configs

val mkdocsConfigurationGe = configurations.create("mkdocsConfigurationGe") {
    isCanBeConsumed = false
    isCanBeResolved = true
    attributes.attribute(Mkdocs.mkdocsAttribute, objects.named(Mkdocs.MKDOCS_CONFIG))
}

val mkdocsDocsGe = configurations.create("mkdocsDocsGe") {
    isCanBeConsumed = false
    isCanBeResolved = true
    attributes.attribute(Mkdocs.mkdocsAttribute, objects.named(Mkdocs.MKDOCS_DOCS))
}

val mkdocsConfigurationGbt = configurations.create("mkdocsConfigurationGbt") {
    isCanBeConsumed = false
    isCanBeResolved = true
    attributes.attribute(Mkdocs.mkdocsAttribute, objects.named(Mkdocs.MKDOCS_CONFIG))
}

val mkdocsDocsGbt = configurations.create("mkdocsDocsGbt") {
    isCanBeConsumed = false
    isCanBeResolved = true
    attributes.attribute(Mkdocs.mkdocsAttribute, objects.named(Mkdocs.MKDOCS_DOCS))
}

dependencies {
    add(mkdocsConfigurationGe.name, project(":ge"))
    add(mkdocsDocsGe.name, project(":ge"))
    add(mkdocsConfigurationGbt.name, project(":gbt"))
    add(mkdocsDocsGbt.name, project(":gbt"))
}

val federationTaskGroup = "mkdocs federation"

// Ge

val mkdocsSourcesGe = layout.buildDirectory.dir("mkdocsSources").map { it.dir("ge-mkdocs") }

val syncMkdocsConfigurationGe = tasks.register<Sync>("syncMkdocsConfigurationGe") {
    group = federationTaskGroup
    from(mkdocsConfigurationGe)
    into(mkdocsSourcesGe)
}

val syncMkdocsDocsGe = tasks.register<Sync>("syncMkdocsDocsGe") {
    group = federationTaskGroup
    from(mkdocsDocsGe)
    into(mkdocsSourcesGe.map { it.dir("docs") })
}

val syncMkdocsSourcesGe by tasks.registering {
    group = federationTaskGroup
    dependsOn(syncMkdocsConfigurationGe, syncMkdocsDocsGe)
    outputs.dir(syncMkdocsConfigurationGe.map { it.destinationDir })
}

// Gbt

val mkdocsSourcesGbt = layout.buildDirectory.dir("mkdocsSources").map { it.dir("gbt-mkdocs") }

val syncMkdocsConfigurationGbt = tasks.register<Sync>("syncMkdocsConfigurationGbt") {
    group = federationTaskGroup
    from(mkdocsConfigurationGbt)
    into(mkdocsSourcesGbt)
}

val syncMkdocsDocsGbt = tasks.register<Sync>("syncMkdocsDocsGbt") {
    group = federationTaskGroup
    from(mkdocsDocsGbt)
    into(mkdocsSourcesGbt.map { it.dir("docs") })
}

val syncMkdocsSourcesGbt by tasks.registering {
    group = federationTaskGroup
    dependsOn(syncMkdocsConfigurationGbt, syncMkdocsDocsGbt)
    outputs.dir(syncMkdocsConfigurationGbt.map { it.destinationDir })
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
