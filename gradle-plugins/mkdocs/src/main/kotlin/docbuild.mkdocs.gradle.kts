import docbuild.mkdocs.Mkdocs
import docbuild.mkdocs.MkdocsBuild
import docbuild.shell.Shell

tasks.register<Shell>("mkdocsServe") {
    cmd.set(listOf("mkdocs", "serve"))
}

tasks.register<MkdocsBuild>("mkdocsBuild")

val mkdocsSourcesDir = layout.buildDirectory.dir("mkdocs-sources")

val prepareMkdocsSourceDocs by tasks.registering(Sync::class) {
    from(layout.projectDirectory.dir("docs"))
    into(mkdocsSourcesDir.map { it.dir("docs") })
}

val prepareMkdocsSourceConfig by tasks.registering(Copy::class) {
    from(layout.projectDirectory.file("mkdocs.yml"))
    into(mkdocsSourcesDir)
}

val prepareMkdocsSources by tasks.registering {
    outputs.dir(mkdocsSourcesDir)
    dependsOn(prepareMkdocsSourceDocs, prepareMkdocsSourceConfig)
}

configurations.create("mkdocsSources") {
    isCanBeConsumed = true
    isCanBeResolved = false
    outgoing.artifact(prepareMkdocsSources)
    attributes.attribute(Mkdocs.mkdocsAttribute, objects.named(Mkdocs.MKDOCS_SOURCES))
}
