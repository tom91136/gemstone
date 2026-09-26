package net.kurobako.gemstone.server

import scala.util.control.NonFatal

private[server] object Throwables {

  /** A throwable's message when it has one, otherwise its class name. */
  def detail(error: Throwable): String =
    Option(error.getMessage).filter(_.nonEmpty).getOrElse(error.getClass.getName)

  /** Runs each cleanup action, attaching later failures to the first so one failure cannot skip the rest. */
  def closeAllQuietly(actions: Iterable[() => Unit]): Unit = {
    val failures = actions.flatMap { action =>
      try { action(); Nil }
      catch { case NonFatal(error) => List(error) }
    }
    failures.headOption.foreach { first =>
      failures.tail.foreach(error => first.addSuppressed(error))
      throw first
    }
  }
}
