package net.kurobako.gemstone.isa.ptx

import net.kurobako.gemstone.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*

/** The immediate operands an instruction takes, checked against the prose range, alignment and type rules. */
object OperandRules {
  private lazy val data: ujson.Value = resources.json[ujson.Value](getClass, "operand-domains.json")

  lazy val rules: Vec[OperandRule] = Vec.from(data("rules").arr.map { row =>
    OperandRule(
      row("opcodes").arr.map(_.str).toSet,
      row("any").arr.map(_.str).toSet,
      row("all").arr.map(_.str).toSet,
      row("none").arr.map(_.str).toSet,
      row("position").num.toInt,
      row("kind").str,
      Vec.from(row("values").arr.map(_.num.toLong)),
      row("elements").bool,
      row("section").str
    )
  })

  private def constant(operand: Operand): Option[Either[String, Literal]] = operand match {
    case Operand.Immediate(raw)                 => Some(Literal.parse(raw.raw).toRight(raw.raw))
    case Operand.Negate(Operand.Immediate(raw)) => Some(Literal.parse(raw.raw).map(_.negated).toRight(raw.raw))
    case _                                      => None
  }

  private def integers(operand: Operand, elements: Boolean): Vec[BigInt] = {
    val operands = if (elements) operand match {
      case Operand.VectorLiteral(items) => items
      case _                            => Vec.empty[Operand]
    }
    else Vec(operand)
    operands.flatMap(constant(_)).collect { case Right(Literal.Integer(value, _)) => value }
  }

  /** The rules of the document that the immediates of `instruction` break, by operand position. */
  def violations(instruction: Instruction): Vec[(position: Int, violation: OperandViolation)] = {
    val modifiers = (instruction.fixedModifiers ++ instruction.mods).map(_.name.raw)
    val opcode    = instruction.opcode.raw
    val present   = modifiers.toSet
    val values    = rules.filter(_.applies(opcode, present)).flatMap { rule =>
      instruction.operands.lift(rule.position).toVec.flatMap(operand =>
        integers(operand, rule.elements).flatMap(value =>
          rule.check(value).map(violation => (position = rule.position, violation = violation))
        )
      )
    }
    val malformed = instruction.operands.zipWithIndex.flatMap((operand, position) =>
      constant(operand).collect { case Left(raw) => (position = position, violation = OperandViolation.Malformed(raw)) }
    )
    val typed = typedConstants(instruction, opcode, modifiers)
    (malformed ++ values ++ typed).distinct
  }

  /** The constants that do not fit the type the document gives their operand. */
  private def typedConstants(
      instruction: Instruction,
      opcode: String,
      modifiers: Vec[String]
  ): Vec[(position: Int, violation: OperandViolation)] = {
    val count = instruction.operands.size
    val names = ModifierDomains.matching(
      instruction.definition,
      instruction.mods
    ).filter(_.accepts(count)).map(_.names(count)) match {
      case Vec()   => Vec.fill(count)(Option.empty[String])
      case results => Vec.tabulate(count)(position => ModifierForm.agreed(results.map(_(position))).flatten)
    }
    val types = PtxTypes.operandTypes(opcode.takeWhile(_ != '.'), modifiers, names)
    instruction.operands.zipWithIndex.flatMap { (operand, position) =>
      for {
        tpe       <- types(position).toVec
        literal   <- constant(operand).flatMap(_.toOption).toVec
        violation <- mismatch(literal, tpe)
      } yield (position = position, violation = violation)
    }
  }

  private def mismatch(literal: Literal, tpe: PtxType): Option[OperandViolation] = literal match {
    case Literal.Integer(value, _) if tpe.scalar && !tpe.floating && tpe.bits <= 64 =>
      val low  = -(BigInt(1) << (tpe.bits - 1))
      val high = (BigInt(1) << tpe.bits) - 1
      Option.when(value < low || value > high)(OperandViolation.TooWide(value, tpe.name))
    case Literal.Binary32(_) if tpe.scalar && (tpe.integer || (tpe.bits != 32 && tpe.floating)) =>
      Some(OperandViolation.WrongKind("single-precision constant", tpe.name))
    case Literal.Binary64(_) if tpe.scalar && (tpe.integer || (tpe.bits != 64 && tpe.floating)) =>
      Some(OperandViolation.WrongKind("double-precision constant", tpe.name))
    case Literal.Decimal(text) if tpe.integer =>
      Some(OperandViolation.WrongKind(s"floating-point constant $text", tpe.name))
    case _ => None
  }
}
