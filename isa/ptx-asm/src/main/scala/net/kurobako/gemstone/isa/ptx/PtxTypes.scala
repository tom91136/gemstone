package net.kurobako.gemstone.isa.ptx

import net.kurobako.gemstone.*
import net.kurobako.gemstone.collection.*

/** The type tables, special register classes and operand type rules of the PTX document. */
object PtxTypes {
  private lazy val data: ujson.Value = resources.json[ujson.Value](getClass, "operand-domains.json")

  lazy val all: Vec[PtxType] = Vec.from(data("types").arr.map { row =>
    PtxType(
      row(0).str,
      row(1).str,
      row(2).num.toInt,
      row(3).num.toInt,
      Option(row(4).str).filter(_.nonEmpty),
      Vec.from(row(5).arr.map(_.str))
    )
  })

  private lazy val byName: Map[String, PtxType] = all.map(value => value.name -> value).toMap

  def apply(name: String): Option[PtxType] = byName.get(name)

  def declaredBits(value: PtxType): Int = value.declared.flatMap(apply(_)).map(_.bits).maxOption.getOrElse(value.bits)

  private lazy val agreement: Map[String, Set[String]] = data("agreement").obj.map { (instruction, row) =>
    instruction -> row.arr.collect { case cell if cell(1).str == "okay" => cell(0).str }.toSet
  }.toMap

  private def widening(key: String): Map[(String, String), Widening] = {
    val matrix  = data(key)
    val columns = matrix("columns").arr.map(_.str)
    matrix("rows").arr.flatMap { row =>
      columns.zip(row(1).arr.map(_.str)).map { (operand, cell) =>
        (row(0).str, operand) ->
          (cell match {
            case "chop"          => Widening.Chop
            case "zext" | "sext" => Widening.Extend
            case "inv"           => Widening.Invalid
            case _               => Widening.Exact
          })
      }
    }.toMap
  }

  private lazy val sources: Map[(String, String), Widening]      = widening("source")
  private lazy val destinations: Map[(String, String), Widening] = widening("destination")

  /** Whether an operand of type `operand` is compatible with the instruction type, by the type checking rules: bit-size
    * types agree with every type of the same size, integers agree with integers, and floating-point types agree only
    * with themselves.
    */
  def agrees(instruction: PtxType, operand: PtxType): Boolean =
    instruction.scalar && operand.scalar && instruction.bits == operand.bits &&
      agreement.get(s"${instruction.kind}X").exists(_.contains(s"${operand.kind}X"))

  /** The widening the relaxed rules of `ld`, `st` and `cvt` apply to a source operand. */
  def source(instruction: PtxType, operand: PtxType): Option[Widening] =
    sources.get((instruction.name, operand.name))

  def destination(instruction: PtxType, operand: PtxType): Option[Widening] =
    destinations.get((instruction.name, operand.name))

  lazy val special: Map[String, SpecialRegister] = data("special").arr.flatMap { row =>
    apply(row(1).str).map(value => row(0).str -> SpecialRegister(row(0).str, value, row(2).num.toInt == 1))
  }.toMap

  lazy val rules: Vec[OperandTypeRule] = Vec.from(data("bindings").arr.map { row =>
    OperandTypeRule(
      row("opcodes").arr.map(_.str).toSet,
      row("any").arr.map(_.str).toSet,
      Some(row("fill").str).filter(_.nonEmpty),
      row("at").arr.map(cell => cell(0).num.toInt -> cell(1).str).toMap,
      row("section").str
    )
  })

  /** The type of each of `count` operands of an instruction, where the document fixes it: a row of the type table
    * decides, and otherwise the operands the syntax names a, b, c and d take the last type the instruction spells.
    */
  def operandTypes(opcode: String, modifiers: Vec[String], names: Vec[Option[String]]): Vec[Option[PtxType]] = {
    val types   = modifiers.flatMap(apply(_)).filter(_.kind != "opaque")
    val last    = types.lastOption
    val first   = types.headOption
    val present = modifiers.toSet
    val row     = rules.find(rule => rule.opcodes.contains(opcode) && (rule.any.isEmpty || rule.any.exists(present)))
    def named(token: String): Option[PtxType] = token match {
      case "first" => first
      case "last"  => last
      case other   => apply(other)
    }
    Vec.tabulate(names.size) { position =>
      row match {
        case Some(found) =>
          found.at.get(position).orElse(found.fill).flatMap(named(_))
        case None =>
          names(position).filter(name => Set("a", "b", "c", "d")(name)).flatMap(_ => last)
      }
    }
  }
}
