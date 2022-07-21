plugins {
    base
    id("docbuild.docker-mkdocs-federation")
    id("docbuild.fly")
}

federatedMkdocsDockerApps {
    register("docs") {
        projectNames.set(listOf("gbt", "ge"))
    }
}

flyApps {
    register("docs")
}
