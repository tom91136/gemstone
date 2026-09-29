package net.kurobako.gemstone

import net.kurobako.gemstone.constraint.*
import scala.util.control.NonFatal

object throwables {

  /** A throwable's message when it has one, otherwise its class name. */
  def detail(error: Throwable): NonEmpty[String] =
    Option(error.getMessage).filter(_.nonEmpty).getOrElse(error.getClass.getName).unsafe[NonEmpty[String]]

  /** Runs each cleanup action, attaching later failures to the first so one failure cannot skip the rest. */
  def closeAll(actions: Iterable[() => Unit]): Unit = {
    val failures = actions.flatMap { action =>
      try { action(); Nil }
      catch { case NonFatal(error) => List(error) }
    }
    failures.headOption.foreach { first =>
      failures.tail.filterNot(_ eq first).foreach(error => first.addSuppressed(error))
      throw first
    }
  }
}
