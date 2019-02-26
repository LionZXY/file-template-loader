package com.github.rougsig.filetemplateloader.generator

import org.apache.commons.io.output.NullWriter
import org.apache.velocity.VelocityContext
import org.apache.velocity.app.Velocity
import org.apache.velocity.app.event.EventCartridge
import org.apache.velocity.app.event.ReferenceInsertionEventHandler
import java.util.*

fun extractProps(text: String): Set<String> {
  val props = text.getReferences()
    .map {
      it.replace("{", "")
        .replace("}", "")
        .replace("\$", "")
    }
    .toSet()

  return props.plus(props.extractPropsBase())
}

fun Set<String>.extractPropsBase(): Set<String> {
  return mapNotNull(String::extractPropBase).toSet()
}

fun String.extractPropBase(): String? {
  return PROP_MODIFICATOR_MATCHER.find(this)?.value?.let {
    PROP_MODIFICATOR_MATCHER.replace(this) { "" }
  }
}

fun Set<String>.filterProps(props: Props): Set<String> {
  return this.minus(props.keys as Set<String>)
}

private fun String.getReferences(): Set<String> {
  val names = HashSet<String>()
  val velocityContext = VelocityContext()
  val ec = EventCartridge()
  ec.addEventHandler(ReferenceInsertionEventHandler { reference, _ -> names.add(reference) })
  ec.attachToContext(velocityContext)
  Velocity.evaluate(velocityContext, NullWriter.NULL_WRITER, "", this)
  return names
}
