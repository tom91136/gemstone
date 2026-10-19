package net.kurobako.gemstone

import scala.scalajs.reflect.annotation.EnableReflectiveInstantiation

object EmbeddedResource {

  /** The module name the build generates for a resource file name. */
  def moduleName(name: String): String = "Resource_" + name.map(c => if (c.isLetterOrDigit) c else '_')
}

/** A resource the build embeds as a module in its owner's package, since Scala.js has no class path. */
@EnableReflectiveInstantiation
trait EmbeddedResource {
  def text: String
}
