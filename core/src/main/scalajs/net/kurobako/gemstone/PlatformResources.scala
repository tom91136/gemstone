package net.kurobako.gemstone

import scala.scalajs.reflect.Reflect

/** Resources load from the modules the build embeds next to their owner. */
private object PlatformResources {
  // loadModule yields Any, and the EmbeddedResource objects are generated into other modules, so it cannot be sealed
  @SuppressWarnings(Array("baumkuchen:open-type-test"))
  def read[A](owner: Class[?], name: String)(parse: ujson.Readable => A): A = {
    val prefix = owner.getName.take(owner.getName.lastIndexOf('.') + 1)
    Reflect.lookupLoadableModuleClass(s"$prefix${EmbeddedResource.moduleName(name)}$$")
      .map(_.loadModule())
      .collect { case resource: EmbeddedResource => parse(resource.text) }
      .getOrElse(throw IllegalStateException(s"missing resource $name for ${owner.getName}"))
  }
}
