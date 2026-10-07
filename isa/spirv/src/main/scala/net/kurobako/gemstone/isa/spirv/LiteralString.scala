package net.kurobako.gemstone.isa.spirv

import java.nio.charset.StandardCharsets
import net.kurobako.gemstone.collection.*

private object LiteralString {
  def encode(text: String): Vec[Word] = {
    val bytes = text.getBytes(StandardCharsets.UTF_8) :+ 0.toByte
    Vec.from(bytes.grouped(4).map(group =>
      Word(group.zipWithIndex.foldLeft(0) { case (word, (byte, index)) => word | ((byte & 0xff) << (index * 8)) })
    ))
  }

  def decode(words: Vec[Word]): String =
    String(
      words.flatMap(word => Vec.tabulate(4)(i => (word.raw >>> (i * 8)).toByte)).takeWhile(_ != 0).toArray,
      StandardCharsets.UTF_8
    )

  def decode(operand: Operand): String = decode(operand.words)
}
