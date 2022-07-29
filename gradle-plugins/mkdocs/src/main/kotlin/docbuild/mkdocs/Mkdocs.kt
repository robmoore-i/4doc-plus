package docbuild.mkdocs

import org.gradle.api.Named
import org.gradle.api.attributes.Attribute

interface Mkdocs : Named {
    companion object {
        val mkdocsAttribute: Attribute<Mkdocs> = Attribute.of("docbuild.mkdocs", Mkdocs::class.java)
        const val MKDOCS_CONFIG = "mkdocs-config"
        const val MKDOCS_DOCS = "mkdocs-docs"
        const val MKDOCS_SOURCES = "mkdocs-sources"
    }
}