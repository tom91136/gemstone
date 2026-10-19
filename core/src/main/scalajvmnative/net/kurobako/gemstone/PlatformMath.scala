package net.kurobako.gemstone

/** The JVM and Scala Native have a fused multiply-add. */
private[gemstone] object PlatformMath {
  final val FusedMultiplyAdd: true = true
}
