import docbuild.mkdocs.Mkdocs
import docbuild.mkdocs.MkdocsBuild
import docbuild.shell.Shell

configurations.create("mkdocsConfiguration${name.capitalize()}") {
    isCanBeConsumed = true
    isCanBeResolved = false
    outgoing.artifact(layout.projectDirectory.file("mkdocs.yml"))
    attributes.attribute(Mkdocs.mkdocsAttribute, objects.named(Mkdocs.MKDOCS_CONFIG))
}

configurations.create("mkdocsDocs${name.capitalize()}") {
    isCanBeConsumed = true
    isCanBeResolved = false
    outgoing.artifact(layout.projectDirectory.dir("docs"))
    attributes.attribute(Mkdocs.mkdocsAttribute, objects.named(Mkdocs.MKDOCS_DOCS))
}

configurations.create("mkdocsSources${name.capitalize()}") {
    isCanBeConsumed = true
    isCanBeResolved = false
    outgoing.artifact(layout.projectDirectory.file("mkdocs.yml"))
    outgoing.artifact(layout.projectDirectory.dir("docs"))
    attributes.attribute(Mkdocs.mkdocsAttribute, objects.named(Mkdocs.MKDOCS_SOURCES))
}

tasks {
    register<Shell>("mkdocsServe") {
        cmd.set(listOf("mkdocs", "serve"))
    }

    register<MkdocsBuild>("mkdocsBuild")
}
