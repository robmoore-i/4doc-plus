tasks.register("mkdocsServe") {
  doFirst {
    exec {
      commandLine(listOf("mkdocs", "serve"))
    }
  }
}

val mkdocsBuild by tasks.registering {
  doFirst {
    exec {
      commandLine(listOf("mkdocs", "build"))
    }
  }
}

val dockerBuild by tasks.registering {
  mustRunAfter(mkdocsBuild)
  doFirst {
    exec {
      commandLine(listOf("docker", "build", ".", "-t", "gradle-4doc"))
    }
  }
}

val containerName = "gradle-4doc-plus"

val dockerRun by tasks.registering {
  mustRunAfter(dockerBuild)
  doFirst {
    exec {
      commandLine(listOf("docker", "run", "-d", "-p", "8080:8080", "--name", containerName, "gradle-4doc"))
    }
  }
}

tasks.register("dockerClean") {
  doFirst {
    exec {
      commandLine(listOf("docker", "rm", "-f", containerName))
    }
  }
}

tasks.register("dockerUp") {
  dependsOn(mkdocsBuild, dockerBuild, dockerRun)
}
