package net.kurobako.gemstone.texture

import java.util.concurrent.atomic.AtomicInteger

class ArrayMemory(val base: Long, val bytes: Array[Byte]) extends TexelMemory {
  val writes: AtomicInteger = AtomicInteger(0)

  def read(address: Long, size: Int): Option[Array[Byte]] =
    Option.when(address >= base && address + size <= base + bytes.length) {
      val from = (address - base).toInt
      bytes.slice(from, from + size)
    }

  def write(address: Long, data: Array[Byte]): Boolean =
    address >= base && address + data.length <= base + bytes.length && {
      data.copyToArray(bytes, (address - base).toInt)
      writes.incrementAndGet()
      true
    }
}
