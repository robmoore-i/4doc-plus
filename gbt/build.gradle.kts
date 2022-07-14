plugins {
    id("docbuild.mkdocs")
}

val mkdocsConfiguration by configurations.creating {
    isCanBeConsumed = true
    isCanBeResolved = false
    outgoing.artifact(layout.projectDirectory.file("mkdocs.yml"))
    attributes.attribute(Usage.USAGE_ATTRIBUTE, objects.named(name))
}

val mkdocsDocs by configurations.creating {
    isCanBeConsumed = true
    isCanBeResolved = false
    outgoing.artifact(layout.projectDirectory.dir("docs"))
    attributes.attribute(Usage.USAGE_ATTRIBUTE, objects.named(name))
}
