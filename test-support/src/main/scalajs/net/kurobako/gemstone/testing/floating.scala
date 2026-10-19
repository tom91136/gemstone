package net.kurobako.gemstone.testing

/** Scala.js floating-point values are JavaScript numbers, which do not keep NaN payloads. */
object floating {
  final val NaNPayloads: false = false
}
