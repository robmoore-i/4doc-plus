plugins {
    base
    id("docbuild.docker-mkdocs-federation")
    id("docbuild.fly")
}

val mkdocsSources by configurations.existing

dependencies {
    mkdocsSources(project(":ge"))
    mkdocsSources(project(":gbt"))
}

flyApps {
    register("docs")
}
