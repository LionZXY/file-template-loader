package com.github.rougsig.filetemplateloader

import com.github.rougsig.filetemplateloader.reader.FILE_TEMPLATE_FOLDER_NAME
import com.github.rougsig.filetemplateloader.reader.readFileTemplate
import com.intellij.openapi.project.guessProjectDir
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixtureTestCase

class FileTemplateReaderTest : LightPlatformCodeInsightFixtureTestCase() {
  private val gson = createUnitTestGson()

  override fun getTestDataPath(): String = calculateTestDataPath()

  override fun setUp() {
    super.setUp()
    myFixture.copyDirectoryToProject("", "")
  }

  private fun doTest(testFileName: String) {
    val template = project
      .guessProjectDir()!!
      .findChild(FILE_TEMPLATE_FOLDER_NAME)!!
      .readFileTemplate(testFileName)

    val json = gson.toJson(template)

    assertSameLinesWithFile(
      "$testDataPath/fileTemplateReader/$testFileName.txt",
      json
    )
  }

  fun testGitignoreFt() = doTest(".gitignore.ft")

  fun testDummyKtFt() = doTest("Dummy.kt.ft")

  fun testDomainGroupJson() = doTest("Domain.group.json")

  fun testMviScreenGroupJson() = doTest("MviScreen.group.json")

  fun testRepositoryGroupJson() = doTest("Repository.group.json")

  fun testRoutingFlowGroupJson() = doTest("RoutingFlow.group.json")

  fun testRepositoryModuleGroupJson() = doTest("RepositoryModule.group.json")
}
