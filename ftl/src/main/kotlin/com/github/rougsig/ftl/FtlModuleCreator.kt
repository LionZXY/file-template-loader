package com.github.rougsig.ftl

import com.github.rougsig.ftl.io.DirectoryImpl
import com.github.rougsig.ftl.io.toVirtualFile
import com.intellij.openapi.module.Module
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.module.ModuleTypeId
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.rootManager
import com.intellij.openapi.vfs.LocalFileSystem

private const val MODULE_DIR_NAME = "ftl"
private const val LIB_DIR_NAME = "lib"
private const val TEMPLATE_DIR_NAME = "template"

internal fun createFtlModule(project: Project): Module {
  val localFileSystem = LocalFileSystem.getInstance()
  val basePath = project.basePath ?: error("project baseDir is null")
  val baseDir = localFileSystem.findFileByPath(basePath) ?: error("project baseDir not found")
  val moduleDir = DirectoryImpl.find(baseDir).createDirectory(MODULE_DIR_NAME)
  val moduleFile = moduleDir.createFile("$MODULE_DIR_NAME.iml")
  val libDir = moduleDir.createDirectory(LIB_DIR_NAME)
  val templateDir = moduleDir.createDirectory(TEMPLATE_DIR_NAME)

  val moduleManager = ModuleManager.getInstance(project)
  val modifiableModuleManager = moduleManager.modifiableModel
  val module = modifiableModuleManager.newModule(moduleFile.pathName, ModuleTypeId.JAVA_MODULE)
  val modifiableRootManager = module.rootManager.modifiableModel
  modifiableModuleManager.commit()

  modifiableRootManager.inheritSdk()
  modifiableRootManager
    .addContentEntry(templateDir.toVirtualFile())
    .addSourceFolder(templateDir.toVirtualFile(), false)

  val ftlLib = project.createFtlLibrary(libDir)
  modifiableRootManager.addLibraryEntry(ftlLib)
  modifiableRootManager.commit()

  return module
}
