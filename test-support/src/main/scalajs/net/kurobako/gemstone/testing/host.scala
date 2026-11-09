package net.kurobako.gemstone.testing

import scala.scalajs.js

private object host {
  def variable(name: String): Option[String] =
    if (js.typeOf(js.Dynamic.global.process: js.Any) == "undefined") None
    else (js.Dynamic.global.process.env.selectDynamic(name): Any) match {
      case value: String if value.nonEmpty => Some(value)
      case _                               => None
    }

  // Node before 21 has no navigator; one thread gives the longest timeouts, which only waits that succeed use
  def availableThreads: Long =
    if (js.typeOf(js.Dynamic.global.navigator: js.Any) == "undefined") 1
    else (js.Dynamic.global.navigator.hardwareConcurrency: Any) match {
      case count: Double if count >= 1 => count.toLong
      case _                           => 1
    }
}
