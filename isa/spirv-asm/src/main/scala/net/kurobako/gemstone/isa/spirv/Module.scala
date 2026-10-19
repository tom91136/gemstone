package net.kurobako.gemstone.isa.spirv

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*

object Module {

  /** Source comment before the indexed instruction, or after the last instruction at instructions.size. */
  case class Comment(before: Int >= 0, text: String)
}

case class Module(
    header: Header,
    instructions: Vec[Instruction],
    endianness: Endianness,
    comments: Vec[Module.Comment] = Vec.empty
) {
  require(comments.forall(_.before <= instructions.size), "comment index exceeds the instruction count")

  /** `count` result ids above every id in use, and this module with its bound raised past them. */
  def freshIds(count: Int >= 0): (ids: Vec[Int], module: Module) = {
    val first = header.bound.raw
    (ids = Vec.tabulate(count)(first + _), module = copy(header = header.copy(bound = Word(first + count))))
  }
}
