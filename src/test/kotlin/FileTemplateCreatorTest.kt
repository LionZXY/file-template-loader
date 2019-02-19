package com.github.rougsig.filetemplateloader

import com.github.rougsig.filetemplateloader.constant.PROPS_NAME
import com.github.rougsig.filetemplateloader.constant.PROPS_PACKAGE_NAME
import com.github.rougsig.filetemplateloader.extension.calculatePackageName
import com.github.rougsig.filetemplateloader.extension.createSubDirs
import com.github.rougsig.filetemplateloader.extension.writeAction
import com.github.rougsig.filetemplateloader.reader.readConfig
import com.github.rougsig.filetemplateloader.reader.readFileTemplateGroups
import com.github.rougsig.filetemplateloader.reader.readFileTemplates
import com.google.gson.Gson
import com.intellij.openapi.fileTypes.FileTypeManager
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixtureTestCase
import org.jetbrains.kotlin.idea.util.sourceRoots
import org.jetbrains.plugins.groovy.GroovyFileType
import java.util.*

class FileTemplateCreatorTest : LightPlatformCodeInsightFixtureTestCase() {
  override fun getTestDataPath(): String = calculateTestDataPath()

  fun testCalculatePackageName() {
    val projectDirectory = myFixture.copyDirectoryToProject("file-template-creator", "")

    val config = project.readConfig()

    val src = myModule.sourceRoots.first()
    val dir = psiManager.findDirectory(src)!!

    val packageName = dir.calculatePackageName(config)

    assertEquals("com.github.rougsig.light.idea.test.case", packageName)
  }

  fun testCreateFileTemplate() {
    val projectDirectory = myFixture.copyDirectoryToProject("file-template-creator", "")
    val fileTemplateDirectory = projectDirectory.findChild(".fileTemplates")!!

    val templates = readFileTemplates(fileTemplateDirectory)
    val config = project.readConfig()

    val src = myModule.sourceRoots.first()
    val dir = psiManager.findDirectory(src)!!

    val repositoryFileTemplate = templates.find { it.fileName == "Repository" }!!

    val props = Properties(config)
    props.setProperty(PROPS_NAME, "FileTemplateRepository")
    props.setProperty(PROPS_PACKAGE_NAME, "com.github.rougsig.filetemplateloader")
    val template = project.writeAction {
      repositoryFileTemplate.create(dir, props)
    }.first()
    assertFileTemplate(
      "repository/FileTemplateRepository.kt",
      "Repository",
      "",
      props,
      template,
      "",
      ""
    )
  }

  fun testCreateFileTemplateGroup() {
    val projectDirectory = myFixture.copyDirectoryToProject("file-template-creator", "")
    val fileTemplateDirectory = projectDirectory.findChild(".fileTemplates")!!

    val templates = readFileTemplates(fileTemplateDirectory)
    val templateGroups = readFileTemplateGroups(templates, fileTemplateDirectory, Gson())
    val config = readConfig(fileTemplateDirectory)

    val src = myModule.sourceRoots.first()
    val dir = psiManager.findDirectory(src)!!

    val repositoryFileTemplateGroup = templateGroups.find { it.name == "Repository" }!!

    val props = Properties(config)
    props.setProperty("FLOW_NAME", "FileTemplate")
    props.setProperty(PROPS_PACKAGE_NAME, "com.github.rougsig.filetemplateloader")
    repositoryFileTemplateGroup.generateProps(props)

    val group = project.writeAction {
      repositoryFileTemplateGroup.create(dir, props)
    }

    val repository = group.find { it.name == "FileTemplateRepository.kt" }!!
    assertFileTemplate(
      "repository/FileTemplateRepository.kt",
      "Repository",
      "",
      props,
      repository,
      "FileTemplateRepository",
      "com.github.rougsig.filetemplateloader.FileTemplateRepository"
    )

    val repositoryImpl = group.find { it.name == "FileTemplateRepositoryImpl.kt" }!!
    assertFileTemplate(
      "repository/FileTemplateRepositoryImpl.kt",
      "RepositoryImpl",
      "",
      props,
      repositoryImpl,
      "FileTemplateRepositoryImpl",
      "com.github.rougsig.filetemplateloader.FileTemplateRepositoryImpl"
    )

    val repositoryBindings = group.find { it.name == "FileTemplateRepositoryBindings.kt" }!!
    assertFileTemplate(
      "repository/di/FileTemplateRepositoryBindings.kt",
      "RepositoryBindings",
      "\\di",
      props,
      repositoryBindings,
      "FileTemplateRepositoryBindings",
      "com.github.rougsig.filetemplateloader.di.FileTemplateRepositoryBindings"
    )
  }

  fun testCreateFileTemplateGroupWithRootDirectory() {
    val projectDirectory = myFixture.copyDirectoryToProject("file-template-creator", "")
    val fileTemplateDirectory = projectDirectory.findChild(".fileTemplates")!!

    val templates = readFileTemplates(fileTemplateDirectory)
    val templateGroups = readFileTemplateGroups(templates, fileTemplateDirectory, Gson())
    val config = readConfig(fileTemplateDirectory)

    val src = psiManager.findDirectory(myModule.sourceRoots.first())!!

    val viewFileTemplateGroup = templateGroups.find { it.name == "View" }!!

    val props = Properties(config)
    props.setProperty("VIEW_NAME", "FileTemplate")
    props.setProperty(PROPS_PACKAGE_NAME, "com.github.rougsig.filetemplateloader")
    viewFileTemplateGroup.generateProps(props)

    val group = project.writeAction {
      val kotlin = src.createSubDirs("./main/kotlin")
      viewFileTemplateGroup.create(kotlin, props)
    }

    val repositoryImpl = group.find { it.name == "FileTemplateView.kt" }!!
    assertFileTemplate(
      "view/FileTemplateView.kt",
      "View",
      "\\main\\kotlin",
      props,
      repositoryImpl,
      "FileTemplateView",
      "com.github.rougsig.filetemplateloader.FileTemplateView"
    )

    val repositoryBindings = group.find { it.name == "file_template_view.xml" }!!
    assertFileTemplate(
      "view/file_template_view.xml",
      "Layout",
      "\\main\\view",
      props,
      repositoryBindings,
      "file_template_view",
      ""
    )
  }

  fun testCreateFileTemplateGroupWithEntries() {
    project.writeAction {
      FileTypeManager.getInstance().associatePattern(GroovyFileType.GROOVY_FILE_TYPE, "*.gradle")
    }

    myFixture.copyDirectoryToProject("file-template-selector", "")

    val templates = project.readFileTemplates()
    val templateGroups = project.readFileTemplateGroups(templates, Gson())
    val config = project.readConfig()

    val src = psiManager.findDirectory(myModule.sourceRoots.first())!!

    val routeFileTemplateGroup = templateGroups.find { it.name == "Route" }!!

    val props = Properties(config)
    props.setProperty("ROUTE_NAME", "FileTemplate")
    props.setProperty(PROPS_PACKAGE_NAME, "com.github.rougsig.filetemplateloader")
    routeFileTemplateGroup.generateProps(props)

    project.writeAction {
      val kotlin = src.createSubDirs("./src/kotlin")
      routeFileTemplateGroup.create(kotlin, props)
    }

    assertSameLinesWithFile(
      "${calculateTestDataPath()}/file-template-result/route/ScreenFactory.txt",
      src.createSubDirs("./src/kotlin").findFile("ScreenFactory.kt")!!.text
    )

    assertSameLinesWithFile(
      "${calculateTestDataPath()}/file-template-result/route/settings.txt",
      src.findFile("settings.gradle")!!.text
    )
  }
}
