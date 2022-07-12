import docbuild.shell.shell

tasks {
    shell("mkdocsServe", providers.provider { listOf("mkdocs", "serve") })
    shell("mkdocsBuild", providers.provider { listOf("mkdocs", "build") })
}
