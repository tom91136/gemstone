package net.kurobako.gemstone

import scala.util.Using

/** Resources load from the class path next to their owner. */
private object PlatformResources {
  def read[A](owner: Class[?], name: String)(parse: ujson.Readable => A): A =
    Using.resource(
      Option(owner.getResourceAsStream(name))
        .getOrElse(throw IllegalStateException(s"missing resource $name for ${owner.getName}"))
    )(stream => parse(stream))
}
