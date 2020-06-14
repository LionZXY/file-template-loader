package com.github.rougsig.ftl

import com.github.rougsig.ftl.extenstion.writeAction
import com.intellij.openapi.project.Project

class FtlProjectModule(project: Project) {
  init {
    project.writeAction { createFtlModule(project) }
  }
}
