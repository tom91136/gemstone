package net.kurobako.gemstone

/** Immutable bytes used as a map key without re-hashing the full payload on every lookup. */
private[gemstone] final case class ByteKey(value: Vec[Byte]) {
  override val hashCode: Int = value.hashCode()
}
