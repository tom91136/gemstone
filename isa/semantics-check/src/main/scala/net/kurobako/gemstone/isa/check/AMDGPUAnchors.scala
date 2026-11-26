package net.kurobako.gemstone.isa.check

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.flow.ControlFlowGraph
import net.kurobako.gemstone.flow.Liveness
import net.kurobako.gemstone.isa.amdgpu.*
import net.kurobako.gemstone.isa.amdgpu.asm.*
import net.kurobako.gemstone.vm.RegisterUse

/** The instructions of an AMDGPU kernel that depend on a code address, and the targets the decoded kernel gives them.
  */
private[check] object AMDGPUAnchors {

  enum Anchor {
    def at: Int
    def target: Long

    case Label(at: Int, target: Long)
    case Address(at: Int, low: Int, high: Int, target: Long)
  }

  private def literal(operand: Operand): Option[BigInt] = operand match {
    case Operand.IntegerLiteral(value, _)      => Some(value)
    case Operand.Negated(inner)                => literal(inner).map(-_)
    case Operand.Expression(s"lit(0x$digits)") => Some(BigInt(digits, 16))
    case _                                     => None
  }

  /** The anchors of a kernel of `size` bytes, or the first instruction whose target it does not give. */
  def of(
      target: Target,
      instructions: Vec[Option[Instruction]],
      offsets: Vec[Int],
      size: Int,
      uses: Vec[RegisterUse],
      graph: ControlFlowGraph
  ): Either[Int, Vec[Anchor]] = {
    val start                 = offsets.headOption.getOrElse(0).toLong
    def inside(address: Long) = address >= start && address < start + size
    def touched(index: Int)   = (uses(index).reads ++ uses(index).writes)
      .flatMap(name => Liveness.units(name, uses(index).unitsOf(name))).toSet

    def label(index: Int, value: Instruction): Option[Anchor] = value.operands.lastOption.flatMap(literal)
      .map(words => offsets(index) + 4L + 4L * words.toShort)
      .filter(reached => !inside(reached) || offsets.exists(_ == reached))
      .map(Anchor.Label(index, _))

    def address(index: Int, value: Instruction): Option[Anchor] = value.operands match {
      case Vec(Operand.Register(file, first, last)) if last == first + 1 =>
        val held                              = Set(s"s$first", s"s$last", "scc")
        val block                             = graph.blocks.find(block => block.first <= index && index <= block.last)
        def following(from: Int): Option[Int] =
          block.flatMap(block => (from + 1 to block.last).find(touched(_).exists(held)))
        def addend(at: Int, mnemonic: String, register: Int): Option[Long] = instructions(at).flatMap { value =>
          val half = Operand.Register(file, register, register)
          value.operands match {
            case Vec(`half`, `half`, added) if value.opcode.raw == mnemonic =>
              literal(added).map(_.toLong & 0xffffffffL)
            case _ => None
          }
        }
        for {
          low   <- following(index)
          lower <- addend(low, "s_add_u32", first)
          high  <- following(low)
          upper <- addend(high, "s_addc_u32", last)
          reached = offsets(index) + 4L + ((upper << 32) | lower)
          if !inside(reached)
        } yield Anchor.Address(index, low, high, reached)
      case _ => None
    }

    instructions.indices.foldLeft[Either[Int, Vec[Anchor]]](Right(Vec.empty[Anchor]))((result, index) =>
      result.flatMap(found =>
        instructions(index).toRight(index).flatMap { value =>
          val operands = value.definition.encodings(target).flatMap(_.operands)
          val labelled = operands.exists(_.kind == OperandKind.LABEL)
          val counter  = operands.filter(_.kind == OperandKind.PC)
          if (counter.exists(_.output) || labelled && counter.nonEmpty) Left(index)
          else if (labelled) label(index, value).map(found :+ _).toRight(index)
          else if (counter.nonEmpty) address(index, value).map(found :+ _).toRight(index)
          else Right(found)
        }
      )
    )
  }
}
