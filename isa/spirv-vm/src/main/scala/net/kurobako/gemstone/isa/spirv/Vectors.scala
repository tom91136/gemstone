package net.kurobako.gemstone.isa.spirv

private[isa] trait Vectors {
  def loaded[T](lane: Any, offset: Any, pointer: Any, count: Any, size: Any): T
  def stored(offset: Any, pointer: Any, count: Any, size: Any, lanes: Any*): Unit
}
