package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.nvidia.*
import net.kurobako.gemstone.isa.sass.Instruction.*

/** Fuse legacy trigonometric reduction only when its opaque fixed-point result cannot escape. */
private object LegacyRangeReduction {
  private val ReductionModifiers = Set(Vec(Modifier.SINCOS), Vec(Modifier.EX2))

  private def registers(operand: Operand): Set[Int] = operand match {
    case Operand.Register(RegisterBank.R, index)                              => Set(index)
    case Operand.Group(values)                                                => values.flatMap(registers(_)).toSet
    case Operand.ConstantBank(_, offset)                                      => registers(offset)
    case Operand.Address(base = base, index = index, descriptor = descriptor) =>
      (base.toList ++ index.toList ++ descriptor.toList).flatMap(registers(_)).toSet ++
        base.toList.flatMap(registers(_)).map(_ + 1)
    case Operand.Unary(value = value) => registers(value)
    case Operand.Qualified(value, _)  => registers(value)
    case _                            => Set.empty
  }

  // traps and kills end the thread and yields resume in place, so none splits a lifetime
  private def branches(instruction: DecodedInstruction): Boolean = instruction.operation.form match {
    case _: Form.Bpt | _: Form.Kill | _: Form.Yield => false
    case _                                          => ControlFlow.transfers(instruction.operation)
  }

  private def wideModifier(instruction: DecodedInstruction): Boolean = instruction.modifiers.exists(
    PartialFunction.cond(_) {
      case Modifier.`64` | Modifier.`128` | Modifier.F64 | Modifier.S64 | Modifier.U64 => true
    }
  )

  private def scalarDestination(instruction: DecodedInstruction): Option[Operand] = {
    val scalar = instruction.operation.form match {
      case _: Form.Mov | _: Form.Mov32i | _: Form.Rro | _: Form.Mufu |
          _: Form.Fmul | _: Form.Fmul32i | _: Form.Fadd | _: Form.Fadd32i |
          _: Form.Ffma | _: Form.Ffma32i | _: Form.Lop |
          _: Form.Lop32i | _: Form.Iadd | _: Form.Iadd32i | _: Form.Imnmx |
          _: Form.Iscadd | _: Form.Shl | _: Form.Shr | _: Form.Sel |
          _: Form.Xmad | _: Form.Imad |
          _: Form.Ldg | _: Form.Ld | _: Form.Lds | _: Form.Ldl => true
      case _ => false
    }
    if (
      scalar &&
      !wideModifier(instruction)
    )
      instruction.operands.headOption.filter(OperandQueries.isRegister(_))
    else None
  }

  /** The registers a 64- or 128-bit load writes. */
  private def wideLoadDestination(instruction: DecodedInstruction): Set[Int] = instruction.operation.form match {
    case _: Form.Ldg | _: Form.Ld | _: Form.Lds | _: Form.Ldl if wideModifier(instruction) =>
      val words = if (instruction.modifiers.contains(Modifier.`128`)) 4 else 2
      instruction.operands.headOption.filter(OperandQueries.isRegister(_)).toSet
        .flatMap(registers(_)).flatMap(first => first until first + words)
    case _ => Set.empty
  }

  /** The shadow register each fused reduction's consumers read, by consumer address, and how many shadows there are. */
  case class Reductions(inputs: Map[Int, VMIR.Value.Register], count: Int >= 0)

  def inputs(sm: Int, function: DecodedFunction, layout: VMRegisters.Layout): Reductions =
    if (sm >= 70) Reductions(Map.empty, 0)
    else {
      val reductions = function.instructions.zipWithIndex.filter((instruction, _) =>
        OperandQueries.is[Form.Rro](instruction.operation)
      )
      val transfers = function.instructions.filter(branches(_))
      if (reductions.nonEmpty && transfers.exists(_.operands.exists(registers(_).nonEmpty)))
        throw IllegalArgumentException("legacy range reduction has indirect control flow")
      val targets = transfers.flatMap(_.operands.flatMap(OperandQueries.integer(_))).toSet
      val mapped  = reductions.zipWithIndex.foldLeft(Map.empty[Int, VMIR.Value.Register]) {
        case (result, ((producer, index), ordinal)) =>
          def reject(detail: String = "unverified form"): Nothing = throw IllegalArgumentException(
            s"RRO at 0x${producer.address.raw.toHexString} requires a closed, straight-line MUFU trigonometric lifetime $detail"
          )
          if (
            producer.guard.nonEmpty || !ReductionModifiers(producer.modifiers) ||
            producer.operands.size != 2
          ) reject()
          // EX2 reductions feed MUFU.EX2, the others MUFU.SIN and MUFU.COS
          val consumers =
            if (producer.modifiers == Vec(Modifier.EX2)) Set(Vec(Modifier.EX2))
            else Set(Vec(Modifier.SIN), Vec(Modifier.COS))
          val destination                 = scalarDestination(producer).getOrElse(reject())
          val register                    = registers(destination).head
          val shadow: VMIR.Value.Register = VMIR.Value.Register(layout.reduction(ordinal))
          @annotation.tailrec
          def scan(
              position: Int,
              mappings: Map[Int, VMIR.Value.Register],
              consumed: Boolean
          ): Map[Int, VMIR.Value.Register] = {
            if (position >= function.instructions.size) reject()
            val next     = function.instructions(position)
            val terminal = next.guard.isEmpty &&
              OperandQueries.is[Form.Exit](next.operation)
            if (terminal) {
              if (!consumed) reject()
              mappings
            } else {
              if (branches(next)) reject(s"across 0x${next.address.raw.toHexString}")
              val loaded             = wideLoadDestination(next)
              val destinationMatches =
                scalarDestination(next).exists(registers(_) == Set(register)) || loaded(register)
              val reads =
                if (scalarDestination(next).nonEmpty || loaded.nonEmpty) next.operands.tail else next.operands
              val referenced = reads.exists(registers(_)(register))
              // An implicit register pair can alias the reduced register through its preceding word.
              val wide =
                wideModifier(next) ||
                  (next.operation.form match {
                    case _: Form.Dadd | _: Form.Dmul | _: Form.Dfma | _: Form.Dsetp |
                        _: Form.Dset | _: Form.Dmnmx => true
                    case _ => false
                  })
              // an address names its whole register pair, so only data operands widen
              def aliases(operand: Operand): Boolean = operand match {
                case Operand.Address(_, _, _, _, _, _) => false
                case _ => registers(operand).exists(r => r <= register && register < r + 4)
              }
              if (wide && reads.exists(aliases(_)))
                reject(s"with wide alias at 0x${next.address.raw.toHexString}")
              if (referenced) {
                if (targets.exists(target => target > producer.address.raw && target <= next.address.raw))
                  reject("control flow enters before a trigonometric consumer")
                val trigonometric = OperandQueries.is[Form.Mufu](next.operation)
                if (
                  !trigonometric || next.guard.nonEmpty || next.operands.size != 2 ||
                  OperandQueries.without(next.operands(1), OperandSelector.reuse) != destination ||
                  !consumers(next.modifiers)
                ) reject(s"read by ${next.mnemonic.raw} at 0x${next.address.raw.toHexString}")
              }
              val updated = if (referenced) mappings.updated(next.address, shadow) else mappings
              if (destinationMatches) {
                if (next.guard.nonEmpty || !(consumed || referenced)) reject()
                updated
              } else {
                if (next.operands.headOption.exists(registers(_)(register)) && !referenced) reject()
                scan(position + 1, updated, consumed || referenced)
              }
            }
          }
          scan(index + 1, result.updated(producer.address, shadow), false)
      }
      Reductions(mapped, reductions.nonNegativeSize)
    }
}
