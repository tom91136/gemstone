package net.kurobako.gemstone

/** Scala.js has no fused multiply-add. */
private[gemstone] object PlatformMath {
  final val FusedMultiplyAdd: false = false
}
