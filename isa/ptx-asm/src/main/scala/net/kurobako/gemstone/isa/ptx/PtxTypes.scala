package net.kurobako.gemstone.isa.ptx

import net.kurobako.gemstone.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import scala.collection.mutable

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
    * with themselves. A packed or alternate instruction type is compatible with the register declaration the table
    * gives it.
    */
  def agrees(instruction: PtxType, operand: PtxType): Boolean =
    instruction.name == operand.name || instruction.declared.contains(operand.name) ||
      (instruction.scalar && operand.scalar && instruction.bits == operand.bits &&
        agreement.get(s"${instruction.kind}X").exists(_.contains(s"${operand.kind}X")))

  /** The widening the relaxed rules of `ld`, `st` and `cvt` apply to a source operand. */
  def source(instruction: PtxType, operand: PtxType): Option[Widening] =
    sources.get((instruction.name, operand.name))

  def destination(instruction: PtxType, operand: PtxType): Option[Widening] =
    destinations.get((instruction.name, operand.name))

  /** Whether a source operand of `ld`, `st` or `cvt` accepts the register declaration `operand`. */
  def acceptsSource(instruction: PtxType, operand: PtxType): Boolean =
    relaxed(sources, instruction, operand)

  /** Whether a destination operand of `ld`, `st` or `cvt` accepts the register declaration `operand`. */
  def acceptsDestination(instruction: PtxType, operand: PtxType): Boolean =
    relaxed(destinations, instruction, operand)

  private def relaxed(
      table: Map[(String, String), Widening],
      instruction: PtxType,
      operand: PtxType
  ): Boolean =
    table.get((instruction.name, operand.name)) match {
      case Some(Widening.Invalid) => false
      case Some(_)                => true
      case None                   => instruction.name == operand.name || instruction.declared.contains(operand.name)
    }

  /** The register declaration types an operand of the instruction type may name, in table order. */
  def allowedOperands(instruction: PtxType): Vec[String] = allowed(agrees(instruction, _))

  def allowedSources(instruction: PtxType): Vec[String] = allowed(acceptsSource(instruction, _))

  def allowedDestinations(instruction: PtxType): Vec[String] = allowed(acceptsDestination(instruction, _))

  private lazy val declarations: Vec[PtxType] = all.filter(value => value.scalar || value.predicate)

  private def allowed(accepts: PtxType => Boolean): Vec[String] =
    declarations.filter(accepts).map(_.name)

  /** The double-width type of a 16- or 32-bit integer type, as the `.wide` operand type of `mul` and `mad`. */
  def wide(value: PtxType): Option[PtxType] =
    Option.when(
      value.integer && (value.bits == 16 || value.bits == 32)
    )(s"${value.kind}${value.bits * 2}").flatMap(apply)

  lazy val special: Map[String, SpecialRegister] = data("special").arr.flatMap { row =>
    apply(row(1).str).map(value => row(0).str -> SpecialRegister(row(0).str, value, row(2).num.toInt == 1))
  }.toMap

  lazy val rules: Vec[OperandTypeRule] = Vec.from(data("bindings").arr.map { row =>
    OperandTypeRule(
      row("opcodes").arr.map(_.str).toSet,
      row("any").arr.map(_.str).toSet,
      Some(row("fill").str).filter(_.nonEmpty),
      row("at").arr.map(cell => cell(0).num.toInt -> cell(1).str).toMap,
      row.obj.get("by").toVector.flatMap(_.arr.map(cell => cell(0).str -> cell(1).str)).toMap,
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
      case "wide"  => last.flatMap(wide)
      case other   => apply(other)
    }
    Vec.tabulate(names.size) { position =>
      row match {
        case Some(found) =>
          names(position).flatMap(found.by.get).orElse(found.at.get(position)).orElse(found.fill).flatMap(named(_))
        case None =>
          names(position).filter(name => Set("a", "b", "c", "d")(name)).flatMap(_ => last)
      }
    }
  }

  /** The syntax name of each operand of `instruction`, where every way of choosing the optional operands agrees. */
  def operandNames(instruction: Instruction): Vec[Option[String]] = {
    val count = instruction.operands.size
    ModifierDomains.matching(instruction.definition, instruction.mods)
      .filter(_.accepts(count)).map(_.names(count)) match {
      case Vec()   => Vec.fill(count)(Option.empty[String])
      case results => Vec.tabulate(count)(position => ModifierForm.agreed(results.map(_(position))).flatten)
    }
  }

  /** Matrix operands hold fragments whose registers are wider than the element type the syntax names. */
  private val Fragments =
    Set(Opcode.Mma, Opcode.Wmma, Opcode.Wgmma, Opcode.Movmatrix, Opcode.Ldmatrix, Opcode.Stmatrix)

  /** The types the document gives each operand of `instruction`, by the type slot of the matched syntax form. An empty
    * vector leaves the operand unconstrained.
    */
  def registerTypes(instruction: Instruction): Vec[Vec[PtxType]] = {
    val count = instruction.operands.size
    if (Fragments(instruction.definition.mnemonic)) Vec.fill(count)(Vec.empty[PtxType])
    else {
      val all     = instruction.fixedModifiers ++ instruction.mods
      val present = all.map(_.name.raw).toSet
      val opcode  = instruction.opcode.raw
      val binding =
        rules.exists(rule => rule.opcodes.contains(opcode) && (rule.any.isEmpty || rule.any.exists(present)))
      if (binding) operandTypes(opcode, all.map(_.name.raw), operandNames(instruction)).map(_.toVec)
      else {
        val actual = all.flatMap(modifier => apply(modifier.name.raw))
        val forms  = ModifierDomains.matching(instruction.definition, instruction.mods).filter(_.accepts(count))
        Vec.tabulate(count)(position =>
          forms.flatMap(form => formTypes(instruction, form, actual).lift(position).toVec.flatten).distinct
        )
      }
    }
  }

  private def formTypes(instruction: Instruction, form: ModifierForm, actual: Vec[PtxType]): Vec[Vec[PtxType]] = {
    val count = instruction.operands.size
    val names = form.names(count)
    val slots = form.required.filter(slot =>
      slot.values.nonEmpty && slot.values.forall(value => PtxTypes(value.name.raw).nonEmpty)
    )
    val typed  = Vec.tabulate(slots.size)(index => (slots(index).name, actual.lift(index)))
    val single =
      if (typed.size == 1) typed.head._2
      else if (typed.isEmpty && actual.size == 1) actual.headOption
      else None
    val found                                           = mutable.Map.empty[String, Vec[PtxType]]
    def add(name: String, value: Option[PtxType]): Unit =
      value.foreach(tpe => found.update(name, found.getOrElse(name, Vec.empty[PtxType]) :+ tpe))
    if (single.nonEmpty) Vec("d", "a", "b", "c").foreach(add(_, single))
    else if (instruction.definition.mnemonic == Opcode.Cvt && typed.size >= 2) {
      add("d", typed.head._2)
      Vec("a", "b", "c").foreach(add(_, typed.last._2))
    } else
      typed.foreach { (slot, value) =>
        slot match {
          case "dtype" => add("d", value)
          case "atype" => add("a", value)
          case "btype" => add("b", value)
          case "ctype" => add("c", value)
          case "stype" => Vec("a", "b", "c").foreach(add(_, value))
          case _       => ()
        }
      }
    Vec.tabulate(count)(position => names(position).toVec.flatMap(name => found.getOrElse(name, Vec.empty[PtxType])))
  }
}
