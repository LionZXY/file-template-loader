package com.github.rougsig.filetemplateloader.reader

const val FILE_TEMPLATE_FOLDER_NAME = ".fileTemplates"
const val FILE_NAME_DELIMITER = "."

const val FILE_TEMPLATE_EXTENSION = "ft"
const val FILE_TEMPLATE_TEMPLATE_EXTENSION = "template.json"
const val FILE_TEMPLATE_GROUP_EXTENSION = "group.json"
const val FILE_TEMPLATE_MODULE_EXTENSION = "module.json"

val FILE_TEMPLATE_EXTENSIONS = listOf(
  FILE_TEMPLATE_EXTENSION,
  FILE_TEMPLATE_GROUP_EXTENSION,
  FILE_TEMPLATE_MODULE_EXTENSION,
  FILE_TEMPLATE_TEMPLATE_EXTENSION
)

val FILE_TEMPLATE_EXTENSION_MATCHER = FILE_TEMPLATE_EXTENSIONS.joinToString("|") { "$FILE_NAME_DELIMITER$it" }.toRegex()
