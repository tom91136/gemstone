package net.kurobako.gemstone

import scala.language.implicitConversions

/** A refined value reads as its carrier wherever the carrier is expected. The view lives outside [[constraint]], so an
  * expansion binds nothing for its opaque types and compiles to the bare value.
  */
trait Unrefined {
  type where[A, P]

  @SuppressWarnings(Array("scalafix:DisableSyntax.asInstanceOf"))
  implicit inline def unrefined[A, P](inline value: where[A, P]): A = value.asInstanceOf[A]
}
