package docbuild.fly

import org.gradle.api.Project
import org.gradle.kotlin.dsl.the

fun Project.flyApp(action: FlyAppExtension.() -> Unit) = project.the<FlyAppExtension>().apply(action)
