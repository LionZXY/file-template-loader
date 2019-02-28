package com.github.rougsig.filetemplateloader.generator

import com.github.rougsig.filetemplateloader.entity.FileTemplate

abstract class PropGenerator {
  abstract val propName: String
  abstract val requiredProps: Set<String>

  abstract fun generateProp(props: Props): Props

  fun isGenerateAvailable(props: Props): Boolean {
    return requiredProps.minus(props.keys as Set<String>).isEmpty()
  }
}

fun FileTemplate.generateProps(props: Props): Props {
  val filteredProps = Props()
  (props as Map<String, String>)
    .filterKeys { k -> this.requiredProps.contains(k) }
    .forEach { k, v -> filteredProps.setProperty(k, v) }
  return propGenerators.filter { generatedProps.contains(it.propName) }.generateProps(filteredProps)
}

fun List<PropGenerator>.generateProps(props: Props): Props {
  val canBeGenerated = filter { it.isGenerateAvailable(props) }

  if (canBeGenerated.isEmpty() && isNotEmpty())
    throw IllegalStateException("can't generate props: ${joinToString { it.propName }}")

  canBeGenerated.forEach { it.generateProp(props) }
  if (isNotEmpty()) minus(canBeGenerated).generateProps(props)

  return props
}

fun copyPropsToLocalScopeProps(prefix: String, generatedProps: Set<String>, props: Props): Props {
  val localScopeProps = Props()

  (props as Map<String, String>)
    .forEach { k, v ->
      if (generatedProps.contains(k)) {
        localScopeProps.setProperty(k.removePrefix("${prefix}_"), v)
      } else {
        localScopeProps.setProperty(k, v)
      }
    }

  return localScopeProps
}
