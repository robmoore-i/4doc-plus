import docbuild.mkdocs.Mkdocs

plugins {
    id("docbuild.mkdocs") apply false
    id("docbuild.docker")
}

val mkdocsSources: Configuration by configurations.creating {
    isCanBeConsumed = false
    isCanBeResolved = true
    attributes.attribute(Mkdocs.mkdocsAttribute, objects.named(Mkdocs.MKDOCS_SOURCES))
}
