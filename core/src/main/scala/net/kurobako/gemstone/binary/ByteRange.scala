package net.kurobako.gemstone.binary

private[gemstone] object ByteRange {
  def contains(length: Int, offset: Long, size: Long): Boolean =
    length >= 0 && offset >= 0L && size >= 0L && offset <= length.toLong && size <= length.toLong - offset

  def start(length: Int, offset: Long, size: Int): Option[Int] =
    Option.when(contains(length, offset, size.toLong))(offset.toInt)
}
