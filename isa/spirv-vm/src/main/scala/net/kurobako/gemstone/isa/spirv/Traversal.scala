package net.kurobako.gemstone.isa.spirv

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.vm.*
import scala.reflect.ClassTag

private object Traversal {

  /** The results of `step` over `items` in order, or the first fault, after which `step` does not run again. */
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  def traverse[A, B: ClassTag](items: IndexedSeq[A])(step: A => Either[Fault, B]): Either[Fault, Vec[B]] = {
    val found   = Vec.newBuilder[B]
    var index   = 0
    var failure = Option.empty[Fault]
    while (failure.isEmpty && index < items.size) {
      step(items(index)) match {
        case Right(value) => found += value
        case Left(fault)  => failure = Some(fault)
      }
      index += 1
    }
    failure.toLeft(found.result())
  }
}
