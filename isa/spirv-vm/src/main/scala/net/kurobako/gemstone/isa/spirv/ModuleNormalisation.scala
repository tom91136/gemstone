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

  /** Folds SPV_INTEL_long_composites continuations into the instruction they continue, leaving an OpNop in each one's
    * place so instruction positions hold.
    */
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  def mergeContinuations(module: Module): Module = if (!module.instructions.exists(continuation(_))) module
  else {
    val instructions = module.instructions.toArray
    var head         = -1
    var index        = 0
    while (index < instructions.length) {
      (instructions(index), if (head >= 0) Some(instructions(head)) else None) match {
        case (Instruction.TypeStructContinuedINTEL(members), Some(Instruction.TypeStruct(dest, previous))) =>
          instructions(head) = Instruction.TypeStruct(dest, previous ++ members)
          instructions(index) = Instruction.Nop()
        case (
              Instruction.ConstantCompositeContinuedINTEL(constituents),
              Some(Instruction.ConstantComposite(resultType, dest, previous))
            ) =>
          instructions(head) = Instruction.ConstantComposite(resultType, dest, previous ++ constituents)
          instructions(index) = Instruction.Nop()
        case (
              Instruction.SpecConstantCompositeContinuedINTEL(constituents),
              Some(Instruction.SpecConstantComposite(resultType, dest, previous))
            ) =>
          instructions(head) = Instruction.SpecConstantComposite(resultType, dest, previous ++ constituents)
          instructions(index) = Instruction.Nop()
        case (
              Instruction.CompositeConstructContinuedINTEL(constituents),
              Some(Instruction.CompositeConstruct(resultType, dest, previous))
            ) =>
          instructions(head) = Instruction.CompositeConstruct(resultType, dest, previous ++ constituents)
          instructions(index) = Instruction.Nop()
        case (
              Instruction.TypeStruct(_, _) | Instruction.ConstantComposite(_, _, _) |
              Instruction.SpecConstantComposite(_, _, _) | Instruction.CompositeConstruct(_, _, _),
              _
            ) => head = index
        case _ => head = -1
      }
      index += 1
    }
    module.copy(instructions = Vec.from(instructions))
  }

  private def continuation(instruction: Instruction): Boolean = instruction match {
    case Instruction.TypeStructContinuedINTEL(_) | Instruction.ConstantCompositeContinuedINTEL(_) |
        Instruction.SpecConstantCompositeContinuedINTEL(_) | Instruction.CompositeConstructContinuedINTEL(_) => true
    case _ => false
  }

}
