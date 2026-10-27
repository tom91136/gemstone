package net.kurobako.gemstone.explorer

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.isa.ptx.*

/** The values an operand of a PTX form accepts: the register class by type, the immediates the prose rules limit, and
  * the shape of addresses and vectors. Every row of the summary comes from the generated domains.
  */
private object PTXDomains {

  private val Addresses = Vec(
    "[reg]"     -> "an integer or bit-size register holding a byte address",
    "[reg+imm]" -> "a register plus a signed 32-bit byte offset",
    "[var+imm]" -> "a variable plus a signed 32-bit byte offset",
    "[imm]"     -> "an unsigned 32-bit absolute byte address",
    "alignment" -> "a multiple of the access size"
  )

  def name(domain: ModifierDomain): String = s".${domain.name}"

  /** The typed alternative of `definition` whose required slots take the same values as `groups`. */
  def alternative(definition: Instruction.Definition, groups: Vec[Set[Modifier]]): Option[ModifierForm] = {
    val forms = ModifierDomains.forms(definition)
    forms.find(_.required.map(_.values) == groups).orElse(forms.headOption)
  }

  private val Nouns =
    Map("b" -> "untyped bits", "s" -> "signed integer", "u" -> "unsigned integer", "f" -> "floating-point")

  private def describe(value: PtxType): String = value.kind match {
    case "b" | "s" | "u" | "f" => s"${value.bits}-bit ${Nouns(value.kind)} register"
    case "pred"                => "1-bit predicate register"
    case "packed"              =>
      s"${value.elements} x .${value.element.getOrElse("")} in a ${value.bits}-bit register, declared ${value.declared.map("." +
          _).mkString(" or ")}"
    case "alt" => s"${value.bits}-bit alternate format, declared ${value.declared.map("." + _).mkString(" or ")}"
    case other => other
  }

  private def registers(types: Vec[PtxType]): Vec[(String, String)] =
    types.map(value => s".${value.name}" -> describe(value))

  private def rules(opcode: String, position: Int): Vec[(String, String)] =
    OperandRules.rules.filter(rule => rule.opcodes.contains(opcode) && rule.position == position).map { rule =>
      val condition = (rule.any.toVec.sorted.map("." + _) ++ rule.all.toVec.sorted.map("." + _)).mkString(
        " ",
        " or ",
        ""
      )
      val limit = rule.kind match {
        case "range"    => s"${rule.values.head}..${rule.values(1)}"
        case "multiple" => s"a multiple of ${rule.values.head}"
        case "oneof"    => rule.values.mkString(", ")
        case _          => if (rule.values.isEmpty) "a power of two" else s"a power of two up to ${rule.values.head}"
      }
      s"immediate${if (rule.any.isEmpty && rule.all.isEmpty) "" else s" with$condition"}" -> s"$limit (${rule.section})"
    }

  /** The domain of the operand at `position` of the form, when the document says anything about it. */
  def operand(
      definition: Instruction.Definition,
      alternative: Option[ModifierForm],
      position: Int,
      constraint: Metadata.OperandConstraint,
      syntaxName: Option[String]
  ): Option[View.Domain] = {
    val opcode = definition.opcode.raw
    val slots  = alternative.toVec.flatMap(form => form.required ++ form.optional)
    val types  = slots.findLast(_.values.exists(value => PtxTypes(value.name.raw).nonEmpty))
      .toVec.flatMap(_.values.toVec.sortBy(_.name.raw).flatMap(value => PtxTypes(value.name.raw)))
    val bound =
      PtxTypes.rules.find(rule => rule.opcodes.contains(opcode.takeWhile(_ != '.'))).flatMap(_.at.get(position))
        .flatMap(PtxTypes(_))
    val uniform = bound.isEmpty && syntaxName.exists(Set("a", "b", "c", "d"))
    val values  =
      (constraint match {
        case Metadata.OperandConstraint.Address       => Addresses
        case Metadata.OperandConstraint.VectorOperand =>
          Vec("{...}" -> "a vector of registers, one per element of the .vN modifier")
        case Metadata.OperandConstraint.Symbol => Vec.empty[(String, String)]
        case _ if bound.nonEmpty               => registers(bound.toVec)
        case _ if uniform                      => registers(types)
        case _                                 => Vec.empty[(String, String)]
      }) ++ rules(opcode, position)
    Option.when(values.nonEmpty)(View.Domain(summary(constraint, bound, uniform, types, values), values))
  }

  private def summary(
      constraint: Metadata.OperandConstraint,
      bound: Option[PtxType],
      uniform: Boolean,
      types: Vec[PtxType],
      values: Vec[(String, String)]
  ): String = constraint match {
    case Metadata.OperandConstraint.Address       => "address: [reg], [reg+imm], [imm]"
    case Metadata.OperandConstraint.VectorOperand => "vector of registers"
    case _                                        =>
      val register = bound.map(value => s".${value.name} register").orElse(
        Option.when(uniform && types.nonEmpty)(s"register of the instruction type (${types.take(4).map("." +
            _.name).mkString(" ")}${if (types.size > 4) " ..." else ""})")
      )
      val limits = values.filter(_._1.startsWith("immediate")).map(_._2)
      (register.toVec ++ limits.headOption.map(limit => s"immediate $limit")).mkString(", ")
  }
}
