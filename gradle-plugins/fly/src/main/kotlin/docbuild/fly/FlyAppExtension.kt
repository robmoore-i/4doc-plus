package docbuild.fly

import org.gradle.api.provider.Property

abstract class FlyAppExtension {
    abstract val appName: Property<String>
    val imageTag = "${System.currentTimeMillis()}"
}