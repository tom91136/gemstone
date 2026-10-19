package net.kurobako.gemstone.isa.nvidia

@SuppressWarnings(Array("scalafix:DisableSyntax.var"))
private class LongStack {
  private var values = Array.ofDim[Long](8)
  private var length = 0

  def nonEmpty: Boolean = length != 0

  def push(value: Long): Unit = {
    if (length == values.length) values = java.util.Arrays.copyOf(values, length * 2)
    values(length) = value
    length += 1
  }

  def pop(): Long = {
    length -= 1
    values(length)
  }

  def clear(): Unit = length = 0
}
