package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.nvidia.*
import net.kurobako.gemstone.isa.sass.Instruction.*

/** Fuse legacy trigonometric reduction only when its opaque fixed-point result cannot escape. */
private object LegacyRangeReduction {
  private def registers(operand: Operand): Set[Int] = operand match {
    case Operand.Register(RegisterBank.R, index)           => Set(index.raw)
    case Operand.Group(values)                             => values.flatMap(registers(_)).toSet
    case Operand.ConstantBank(_, offset)                   => registers(offset)
    case Operand.Address(base, index, _, _, _, descriptor) =>
      (base.toList ++ index.toList ++ descriptor.toList).flatMap(registers(_)).toSet ++
        base.toList.flatMap(registers(_)).map(_ + 1)
    case Operand.Unary(value, _, _, _) => registers(value)
    case Operand.Qualified(value, _)   => registers(value)
    case _                             => Set.empty
  }

  // traps and kills end the thread and yields resume in place, so none splits a lifetime
  private def branches(instruction: DecodedInstruction): Boolean = instruction.operation match {
    case _: Instruction.Bpt | _: Instruction.Kill | _: Instruction.Yield => false
    case operation                                                       => ControlFlow.transfers(operation)
  }

  private def scalarDestination(instruction: DecodedInstruction): Option[Operand] = {
    val scalar = instruction.operation match {
      case _: Instruction.Mov | _: Instruction.Mov32i | _: Instruction.Rro | _: Instruction.Mufu |
          _: Instruction.Fmul | _: Instruction.Fadd | _: Instruction.Ffma | _: Instruction.Lop |
          _: Instruction.Lop32i | _: Instruction.Iadd | _: Instruction.Iadd32i | _: Instruction.Imnmx |
          _: Instruction.Ldg | _: Instruction.Ld | _: Instruction.Lds | _: Instruction.Ldl => true
      case _ => false
    }
    if (
      scalar &&
      !instruction.modifiers.exists(Set(Modifier.`64`, Modifier.`128`, Modifier.F64, Modifier.S64, Modifier.U64))
    )
      instruction.operands.headOption.filter {
        case Operand.Register(RegisterBank.R, _) => true
        case _                                   => false
      }
    else None
  }

  def inputs(sm: Int, function: DecodedFunction): Map[Int, VMIR.Value.Register] =
    if (sm >= 70) Map.empty
    else {
      val reductions = function.instructions.zipWithIndex.filter((instruction, _) =>
        VMOperand.is[Instruction.Rro](instruction.operation)
      )
      val transfers = function.instructions.filter(branches(_))
      if (reductions.nonEmpty && transfers.exists(_.operands.exists(registers(_).nonEmpty)))
        throw IllegalArgumentException("legacy range reduction has indirect control flow")
      val targets = transfers.flatMap(_.operands.flatMap(VMOperand.integer(_))).toSet
      reductions.zipWithIndex.foldLeft(Map.empty[Int, VMIR.Value.Register]) {
        case (result, ((producer, index), ordinal)) =>
          def reject(detail: String = "unverified form"): Nothing = throw IllegalArgumentException(
            s"RRO at 0x${producer.address.raw.toHexString} requires a closed, straight-line MUFU trigonometric lifetime $detail"
          )
          if (producer.guard.nonEmpty || producer.modifiers != Vec(Modifier.SINCOS) || producer.operands.size != 2)
            reject()
          val destination                 = scalarDestination(producer).getOrElse(reject())
          val register                    = registers(destination).head
          val shadow: VMIR.Value.Register = VMIR.Value.Register(VMRegisters.reduction(ordinal))
          @annotation.tailrec
          def scan(
              position: Int,
              mappings: Map[Int, VMIR.Value.Register],
              consumed: Boolean
          ): Map[Int, VMIR.Value.Register] = {
            if (position >= function.instructions.size) reject()
            val next     = function.instructions(position)
            val terminal = next.guard.isEmpty &&
              VMOperand.is[Instruction.Exit](next.operation)
            if (terminal) {
              if (!consumed) reject()
              mappings
            } else {
              if (branches(next)) reject(s"across 0x${next.address.raw.toHexString}")
              val destinationMatches = scalarDestination(next).contains(destination)
              val reads              = if (scalarDestination(next).nonEmpty) next.operands.tail else next.operands
              val referenced         = reads.exists(registers(_)(register))
              // An implicit register pair can alias the reduced register through its preceding word.
              val wide =
                next.modifiers.exists(Set(Modifier.`64`, Modifier.`128`, Modifier.F64, Modifier.S64, Modifier.U64)) ||
                  (next.operation match {
                    case _: Instruction.Dadd | _: Instruction.Dmul | _: Instruction.Dfma | _: Instruction.Dsetp |
                        _: Instruction.Dset | _: Instruction.Dmnmx => true
                    case _ => false
                  })
              if (
                wide &&
                next.operands.exists(operand => registers(operand).exists(r => r <= register && register < r + 4))
              ) reject(s"with wide alias at 0x${next.address.raw.toHexString}")
              if (referenced) {
                if (targets.exists(target => target > producer.address.raw && target <= next.address.raw))
                  reject("control flow enters before a trigonometric consumer")
                val trigonometric = VMOperand.is[Instruction.Mufu](next.operation)
                if (
                  !trigonometric || next.guard.nonEmpty || next.operands.size != 2 ||
                  VMOperand.without(next.operands(1), OperandSelector.reuse) != destination ||
                  !Set(Vec(Modifier.SIN), Vec(Modifier.COS))(next.modifiers)
                ) reject(s"read by ${next.mnemonic.raw} at 0x${next.address.raw.toHexString}")
              }
              val updated = if (referenced) mappings.updated(next.address.raw, shadow) else mappings
              if (destinationMatches) {
                if (next.guard.nonEmpty || !(consumed || referenced)) reject()
                updated
              } else {
                if (next.operands.headOption.exists(registers(_)(register)) && !referenced) reject()
                scan(position + 1, updated, consumed || referenced)
              }
            }
          }
          scan(index + 1, result.updated(producer.address.raw, shadow), false)
      }
    }
}
