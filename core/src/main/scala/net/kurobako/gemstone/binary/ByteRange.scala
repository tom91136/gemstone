package net.kurobako.gemstone.binary

import net.kurobako.gemstone.constraint.*

object ByteRange {
  def contains(length: Long, offset: Long, size: Long): Boolean =
    length >= 0L && offset >= 0L && size >= 0L && offset <= length && size <= length - offset

  def start(length: Int, offset: Long, size: Int): Option[Int >= 0] =
    Option.when(contains(length.toLong, offset, size.toLong))(offset.toInt.unsafe[Int >= 0])
}
