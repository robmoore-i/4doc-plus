import docbuild.mkdocs.MkdocsBuild
import docbuild.shell.Shell

configurations.create("mkdocsConfiguration${name.capitalize()}") {
    isCanBeConsumed = true
    isCanBeResolved = false
    outgoing.artifact(layout.projectDirectory.file("mkdocs.yml"))
    attributes.attribute(Usage.USAGE_ATTRIBUTE, objects.named(name))
}

configurations.create("mkdocsDocs${name.capitalize()}") {
    isCanBeConsumed = true
    isCanBeResolved = false
    outgoing.artifact(layout.projectDirectory.dir("docs"))
    attributes.attribute(Usage.USAGE_ATTRIBUTE, objects.named(name))
}

tasks {
    register<Shell>("mkdocsServe") {
        cmd.set(listOf("mkdocs", "serve"))
    }

    register<MkdocsBuild>("mkdocsBuild")
}
