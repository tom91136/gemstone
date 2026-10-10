package net.kurobako.gemstone.isa.spirv

import Operand.*
import net.kurobako.gemstone.collection.*

/** Resolve decoration indirection before specialization, reflection, or compilation. */
private object ModuleNormalisation {
  def expandDecorationGroups(module: Module): Module = {
    val groups      = module.instructions.collect { case Instruction.DecorationGroup(dest) => id(dest) }.toSet
    val decorations = module.instructions.collect {
      case Instruction.Decorate(target, decoration) if groups.contains(id(target)) => id(target) -> decoration
    }.groupMap(_._1)(_._2)
    module.copy(instructions = module.instructions.flatMap {
      case Instruction.GroupDecorate(group, targets) =>
        require(groups.contains(id(group)), "undefined decoration group")
        targets.flatMap(target =>
          decorations.getOrElse(id(group), Vec.empty[Operand]).map(Instruction.Decorate(target, _))
        )
      case Instruction.GroupMemberDecorate(group, targets) =>
        require(groups.contains(id(group)), "undefined decoration group")
        targets.flatMap { pair =>
          require(
            pair.kind == OperandKind.PairIdRefLiteralInteger && pair.words.length == 2,
            "OpGroupMemberDecorate requires target/member pairs"
          )
          val target = Operand(OperandKind.IdRef, Vec.nonEmpty(pair.words.head))
          val member = Operand(OperandKind.LiteralInteger, Vec.nonEmpty(pair.words.apply(1)))
          decorations.getOrElse(id(group), Vec.empty[Operand]).map(Instruction.MemberDecorate(target, member, _))
        }
      case Instruction.DecorationGroup(_)                                 => Vec.empty
      case Instruction.Decorate(target, _) if groups.contains(id(target)) => Vec.empty
      case instruction                                                    => Vec(instruction)
    })
  }

}
