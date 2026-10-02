package net.kurobako.gemstone.codegen

import java.nio.charset.StandardCharsets.UTF_8
import java.security.MessageDigest
import net.kurobako.gemstone.string.*

/** Typed logical forms and small constructor/metadata chunks; bit layouts remain in the swizzled resources. */
object SASSInstructionRenderer {
  private val ChunkSize = 256
  case class Case(
      name: String,
      family: String,
      mnemonic: String,
      slots: Vector[SASSInstructionModel.Slot],
      forms: Vector[String]
  )

  private def identifier(value: String): String = {
    val words  = value.split(Array('.', '_', '-', ' ')).filter(_.nonEmpty)
    val result = words.map(word => word.head.toUpper.toString + word.tail.toLowerCaseRoot).mkString
    if (result.headOption.exists(_.isLetter)) result else s"Form$result"
  }

  private def suffix(kind: String): String = kind match {
    case "Register"        => "Reg"
    case "Predicate"       => "Pred"
    case "Immediate"       => "Imm"
    case "ConstantBank"    => "Const"
    case "SpecialRegister" => "Special"
    case other             => other
  }

  private def fieldName(value: String): String = value match {
    case "Rd" | "URd" => "dst"
    case "Ra" | "URa" => "srcA"
    case "Rb" | "URb" => "srcB"
    case "Rc" | "URc" => "srcC"
    case "Pd" | "UPd" => "dstPredicate"
    case other        =>
      val clean = other.filter(c => c.isLetterOrDigit || c == '_')
      val base  = if (clean.headOption.exists(_.isLetter)) clean.head.toLower.toString + clean.tail else s"value$clean"
      if (
        Set(
          "then",
          "end",
          "using",
          "extension",
          "opaque",
          "open",
          "infix",
          "as",
          "macro",
          "this",
          "super",
          "type",
          "val",
          "var",
          "def",
          "given",
          "inline",
          "match",
          "case",
          "object",
          "enum",
          "yield",
          "export",
          "import",
          "mods",
          "pred",
          "definition",
          "operands",
          "modifiers",
          "guard"
        )(base)
      ) s"${base}Value"
      else base
  }

  def cases(shapes: Vector[SASSInstructionModel.Shape]): Vector[Case] = {
    val opcodes    = shapes.map(_.opcode).distinct.sorted
    val collisions = opcodes.groupBy(value => identifier(value).toLowerCaseRoot).filter(_._2.size > 1).keySet
    val families   = opcodes.map { opcode =>
      val base = identifier(opcode)
      val hash =
        MessageDigest.getInstance("SHA-256").digest(opcode.getBytes(UTF_8)).take(6).map(b => f"${b & 255}%02x").mkString
      opcode -> (if (collisions(base.toLowerCaseRoot)) s"${base}_$hash" else base)
    }.toMap
    // An omitted default is a distinct logical arity. Deduplication by operand kinds gives text and binary
    // decoding the same canonical case even when architectures use different binding names or defaults.
    val expanded = shapes.flatMap { shape =>
      shape.slots.foldLeft(Vector(Vector.empty[SASSInstructionModel.Slot])) { (prefixes, slot) =>
        prefixes.flatMap(prefix =>
          (if (slot.optional) Vector(prefix) else Vector.empty) ++
            slot.kind.split('|').toVector.map(kind => prefix :+ slot.copy(kind = kind, optional = false))
        )
      }.map(slots => (shape.opcode, slots, shape.forms.map(_.form)))
    }
    val kinds =
      Set("Register", "Predicate", "Immediate", "Address", "ConstantBank", "SpecialRegister", "Group", "Symbol")
    require(expanded.forall(_._2.forall(slot => kinds(slot.kind))), "unmodelled SASS operand kind")
    expanded.groupBy((opcode, slots, _) => (opcode, slots.map(_.kind))).toVector
      .sortBy { case ((opcode, kinds), _) => (opcode, kinds.mkString("/")) }
      .map { case ((opcode, kinds), rows) =>
        val family = families(opcode)
        val chosen = rows.map(_._2).sortBy(_.map(_.name).mkString("/")).head
        val names  = chosen.map(slot => fieldName(slot.name))
        val slots  = chosen.zipWithIndex.map { (slot, index) =>
          val base = names(index)
          val name = if (names.count(_ == base) > 1) s"$base${names.take(index).count(_ == base) + 1}" else base
          slot.copy(name = name)
        }
        Case(
          family + (if (kinds.isEmpty) "NoOperands" else kinds.map(suffix(_)).mkString),
          family,
          opcode,
          slots,
          rows.flatMap(_._3).distinct.sorted
        )
      }
  }

  def render(
      shapes: Vector[SASSInstructionModel.Shape],
      modifiers: Vector[String] = Vector.empty
  ): Map[String, String] = {
    val model = cases(shapes)
    Map("Instruction.scala" -> renderInstructions(model, modifiers)) ++
      model.grouped(ChunkSize).zipWithIndex.map { (chunk, index) =>
        s"definitions/InstructionDefinitions$index.scala" -> renderDefinitions(chunk, index)
      }
  }

  def renderInstructions(model: Vector[Case], modifiers: Vector[String] = Vector.empty): String = {
    require(modifiers.forall(value => !value.contains('`') && !value.contains('\n')), "invalid modifier identifier")
    val modifierCases =
      modifiers.map(value => s"case `$value` extends Modifier(${ujson.Str(value).render()})").mkString("\n")
    val modifierValues = modifiers.map(value => s"`$value`").mkString(", ")
    val aliases        = model.groupBy(_.family).toVector.sortBy(_._1).map { (family, cases) =>
      s"type $family = ${cases.map(_.name).mkString(" | ")}"
    }.mkString("\n")
    val definitions = model.zipWithIndex.map { (entry, index) =>
      s"inline def ${entry.name}Definition: Definition = InstructionDefinitions${index / ChunkSize}.${entry.name}"
    }.mkString("\n")
    val chunks = model.grouped(ChunkSize).toVector.indices.map(index =>
      s"InstructionDefinitions$index.definitions"
    ).mkString(",\n")
    val cases = model.map { entry =>
      val fields    = entry.slots.map(slot => s"${slot.name}: InstructionOperand.${slot.kind}")
      val arguments = (fields ++ Vector("mods: Vec[Modifier] = Vec.empty", "pred: Option[Guard] = None")).mkString(", ")
      val operands  = entry.slots.map(_.name).map(", " + _).mkString
      s"case ${entry.name}($arguments) extends Instruction(Instruction.${entry.name}Definition, mods, pred$operands)"
    }.mkString("\n")
    s"""${GeneratedHeader.scala("SASS typed logical instruction forms from pinned NVIDIA descriptions")}
       |package net.kurobako.gemstone.isa.sass
       |import net.kurobako.gemstone.collection.*
       |import net.kurobako.gemstone.collection.Vec.*
       |import net.kurobako.gemstone.constraint.*
       |
       |${GeneratedHeader.annotation()}object Instruction {
       |  object Modifier {
       |    private lazy val known: Map[String, Modifier] = Vec[Modifier]($modifierValues).map(value => value.name -> value).toMap
       |    def fromName(name: String): Either[String, Modifier] =
       |      name.refine[NonEmpty[String]].left.map(_.raw).map(value => known.getOrElse(name, Vendor(value)))
       |    def apply(name: String): Modifier = fromName(name).fold(error => throw IllegalArgumentException(error), identity)
       |  }
       |  enum Modifier(val spelling: NonEmpty[String]) {
       |    inline def name: String = spelling
       |    $modifierCases
       |    case Vendor(value: NonEmpty[String]) extends Modifier(value)
       |  }
       |  case class Guard(predicate: Operand.Predicate)
       |  enum OperandKind { case Register, Predicate, Immediate, Address, ConstantBank, SpecialRegister, Group, Symbol }
       |  case class Slot(name: NonEmpty[String], kind: OperandKind)
       |
       |  object Definition {
       |    lazy val all: Vec[Definition] = Vec($chunks).flatten
       |    private lazy val byMnemonic: Map[String, Vec[Definition]] = all.groupBy(_.mnemonic.raw)
       |    def forMnemonic(mnemonic: String): Vec[Definition] = byMnemonic.getOrElse(mnemonic, Vec.empty)
       |  }
       |  abstract class Definition(
       |      val name: NonEmpty[String],
       |      val mnemonic: NonEmpty[String],
       |      val slots: Vec[Slot],
       |      val forms: Vec[String]
       |  ) {
       |    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction]
       |    override final def toString: String = name.raw
       |  }
       |  def decode(mnemonic: String, modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Either[String, Instruction] = {
       |    val definitions = Definition.forMnemonic(mnemonic)
       |    definitions.view.flatMap(_.decode(modifiers, operands, guard)).headOption
       |      .toRight(if (definitions.isEmpty) s"unknown SASS mnemonic $$mnemonic"
       |               else s"invalid operands for $$mnemonic: $$operands")
       |  }
       |  $aliases
       |  $definitions
       |}
       |import Instruction.*
       |${GeneratedHeader.annotation()}enum Instruction(
       |    val definition: Definition,
       |    val modifiers: Vec[Modifier],
       |    val guard: Option[Guard],
       |    operandValues: Operand*
       |) {
       |  inline def mnemonic: NonEmpty[String] = definition.mnemonic
       |  val operands: Vec[Operand] = operandValues.toVec
       |  $cases
       |}
       |""".stripMargin
  }

  private def renderDefinitions(model: Vector[Case], index: Int): String = {
    val definitions = model.map { entry =>
      val slots   = entry.slots.map(slot => s"Slot(${ujson.write(slot.name)}, OperandKind.${slot.kind})").mkString(", ")
      val forms   = entry.forms.map(ujson.write(_)).mkString(", ")
      val pattern = entry.slots.map(slot => s"InstructionOperand.${slot.kind}(${slot.name})").mkString(", ")
      val values  = (entry.slots.map(_.name) ++ Vector("modifiers", "guard")).mkString(", ")
      s"""object ${entry.name} extends Definition(${ujson.write(entry.name)}, ${ujson.write(
          entry.mnemonic
        )}, Vec($slots), Vec($forms)) {
         |  def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] = operands match {
         |    case Vec($pattern) => Some(Instruction.${entry.name}($values))
         |    case _ => None
         |  }
         |}""".stripMargin
    }.mkString("\n")
    s"""${GeneratedHeader.scala("SASS instruction definitions and checked operand constructors")}
       |package net.kurobako.gemstone.isa.sass
       |import net.kurobako.gemstone.collection.*
       |import net.kurobako.gemstone.constraint.*
       |import Instruction.*
       |${GeneratedHeader.annotation()}object InstructionDefinitions$index {
       |  $definitions
       |  val definitions: Vec[Definition] = Vec(${model.map(_.name).mkString(", ")})
       |}
       |""".stripMargin
  }
}
