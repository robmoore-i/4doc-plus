import docbuild.mkdocs.MkdocsBuild
import docbuild.shell.shell

tasks {
    shell("mkdocsServe", providers.provider { listOf("mkdocs", "serve") })

    register<MkdocsBuild>("mkdocsBuild")
}
