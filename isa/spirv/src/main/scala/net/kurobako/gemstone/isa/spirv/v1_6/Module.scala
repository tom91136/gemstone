package net.kurobako.gemstone.isa.spirv.v1_6

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
  require(comments.forall(_.before.raw <= instructions.size), "comment index exceeds the instruction count")
}
