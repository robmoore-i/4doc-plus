import docbuild.docker.DockerBuild
import docbuild.docker.mkdocs.federation.DockerfileTemplate
import docbuild.docker.mkdocs.federation.NginxConfTemplate
import docbuild.docker.mkdocs.federation.RenderTemplateFile
import docbuild.docker.mkdocs.federation.SyncMkdocsSources
import docbuild.mkdocs.Mkdocs

plugins {
    id("docbuild.mkdocs") apply false
    id("docbuild.docker")
}

dockerApps {
    register("docs")
}

val mkdocsSources: Configuration by configurations.creating {
    isCanBeConsumed = false
    isCanBeResolved = true
    attributes.attribute(Mkdocs.mkdocsAttribute, objects.named(Mkdocs.MKDOCS_SOURCES))
}

val syncMkdocsSources by tasks.registering(SyncMkdocsSources::class) {
    mkdocsSourcesDir.from(mkdocsSources)
    outputDir.set(layout.buildDirectory.dir("mkdocs-sources"))
}

val projectNamesProvider: Provider<String> = syncMkdocsSources.map { it.getProjectNames() }

val renderDockerfile = tasks.register<RenderTemplateFile>("renderDockerfile") {
    template.set(DockerfileTemplate.template)
    templateVariables.put("projectNames", projectNamesProvider)
    outputFile.set(layout.buildDirectory.dir(name).map { it.file("Dockerfile") })
    renderFunction = { template, variables ->
        val projectNames = variables["projectNames"]?.split(",")!!
        DockerfileTemplate.render(template, projectNames)
    }
}

val renderNginxConf = tasks.register<RenderTemplateFile>("renderNginxConf") {
    template.set(NginxConfTemplate.template)
    templateVariables.put("projectNames", projectNamesProvider)
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
