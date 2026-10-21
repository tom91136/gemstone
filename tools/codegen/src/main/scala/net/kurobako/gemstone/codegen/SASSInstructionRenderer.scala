package net.kurobako.gemstone.codegen

import net.kurobako.gemstone.string.*

/** Typed logical forms and small constructor/metadata chunks; bit layouts remain in the swizzled resources. */
object SASSInstructionRenderer {
  private val ChunkSize      = 256
  private val NameSeparators = Array('.', '_', '-', ' ')
  private val ModelledKinds  =
    Set("Register", "Predicate", "Immediate", "Address", "ConstantBank", "SpecialRegister", "Group", "Symbol")
  case class Case(
      name: String,
      family: String,
      mnemonic: String,
      slots: Vector[SASSInstructionModel.Slot],
      forms: Vector[String]
  )

  private def identifier(value: String, separator: String = ""): String = {
    val words  = value.split(NameSeparators).filter(_.nonEmpty)
    val result = words.map(word => word.head.toUpper.toString + word.tail.toLowerCaseRoot).mkString(separator)
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

  // members of the generated enum and Scala soft keywords
  private val reserved =
    Set("mods", "pred", "definition", "operands", "modifiers", "guard", "end", "extension", "infix", "as", "macro")

  private def fieldName(value: String): String = value match {
    case "Rd" | "URd" => "dst"
    case "Ra" | "URa" => "srcA"
    case "Rb" | "URb" => "srcB"
    case "Rc" | "URc" => "srcC"
    case "Pd" | "UPd" => "dstPredicate"
    case other        =>
      val clean = other.filter(c => c.isLetterOrDigit || c == '_')
      val base  = if (clean.headOption.exists(_.isLetter)) clean.head.toLower.toString + clean.tail else s"value$clean"
      if (ScalaKeywords(base) || reserved(base)) s"${base}Value" else base
  }

  def cases(shapes: Vector[SASSInstructionModel.Shape]): Vector[Case] = {
    val opcodes    = shapes.map(_.opcode).distinct.sorted
    val collisions = opcodes.groupBy(value => identifier(value).toLowerCaseRoot).filter(_._2.size > 1).keySet
    // Names that differ only in case collide on case-insensitive file systems; keep their word boundaries.
    val families = opcodes.map { opcode =>
      val base = identifier(opcode)
      opcode -> (if (collisions(base.toLowerCaseRoot)) identifier(opcode, "_") else base)
    }.toMap
    require(
      families.values.groupBy(_.toLowerCaseRoot).forall(_._2.size == 1),
      "SASS opcode names collide without case"
    )
    // An omitted default is a distinct logical arity. Deduplication by operand kinds gives text and binary
    // decoding the same canonical case even when architectures use different binding names or defaults.
    val expanded = shapes.flatMap { shape =>
      val forms = shape.forms.map(_.form)
      shape.slots.foldLeft(Vector(Vector.empty[SASSInstructionModel.Slot])) { (prefixes, slot) =>
        prefixes.flatMap(prefix =>
          (if (slot.optional) Vector(prefix) else Vector.empty) ++
            slot.kind.split('|').toVector.map(kind => prefix :+ slot.copy(kind = kind, optional = false))
        )
      }.map(slots => (shape.opcode, slots, forms))
    }
    require(expanded.forall(_._2.forall(slot => ModelledKinds(slot.kind))), "unmodelled SASS operand kind")
    expanded.groupBy((opcode, slots, _) => (opcode, slots.map(_.kind))).toVector
      .sortBy { case ((opcode, kinds), _) => (opcode, kinds.mkString("/")) }
      .map { case ((opcode, kinds), rows) =>
        val family = families(opcode)
        val chosen = rows.map(_._2).minBy(_.map(_.name).mkString("/"))
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
    val model  = cases(shapes)
    val chunks = model.grouped(ChunkSize).toVector
    Map(
      "Instruction.scala"                        -> renderInstructions(model, modifiers),
      "Form.scala"                               -> renderForms(model),
      "definitions/InstructionDefinitions.scala" -> renderDecoding(chunks.size)
    ) ++ chunks.zipWithIndex.map { (chunk, index) =>
      s"definitions/InstructionDefinitions$index.scala" -> renderDefinitions(chunk, index)
    }
  }

  def renderInstructions(model: Vector[Case], modifiers: Vector[String] = Vector.empty): String = {
    require(modifiers.forall(value => !value.contains('`') && !value.contains('\n')), "invalid modifier identifier")
    val modifierCases =
      modifiers.map(value => s"case `$value` extends Modifier(${ujson.Str(value).render()})").mkString("\n")
    val modifierValues = modifiers.map(value => s"`$value`").mkString(", ")
    val holders        = model.grouped(ChunkSize).zipWithIndex.map { (chunk, index) =>
      val values = chunk.map { entry =>
        val forms = entry.forms.map(ujson.write(_)).mkString(", ")
        s"val ${entry.name}: Definition = Definition(${ujson.write(entry.name)}, ${ujson.write(entry.mnemonic)}, Vec($forms))"
      }.mkString("\n")
      s"""object Definitions$index {
         |  $values
         |  val definitions: Vec[Definition] = Vec(${chunk.map(_.name).mkString(", ")})
         |}""".stripMargin
    }.mkString("\n")
    val chunks =
      model.grouped(ChunkSize).toVector.indices.map(index => s"Definitions$index.definitions").mkString(",\n")
    s"""${GeneratedHeader.scala("SASS typed logical instruction forms from pinned NVIDIA descriptions")}
       |package net.kurobako.gemstone.isa.sass
       |import net.kurobako.gemstone.collection.*
       |import net.kurobako.gemstone.constraint.*
       |
       |${GeneratedHeader.annotation()}object Instruction {
       |  object Modifier {
       |    private lazy val known: Map[String, Modifier] = Vec[Modifier]($modifierValues).map(value => value.name -> value).toMap
       |    def fromName(name: String): Either[String, Modifier] =
       |      name.refine[NonEmpty[String]].left.map(_.raw).map(value => known.getOrElse(name, Vendor(value)))
       |    def apply(name: String): Modifier = fromName(name).fold(error => throw IllegalArgumentException(error), identity)
       |
       |    extension (modifier: Modifier) {
       |      def is64BitInteger: Boolean = PartialFunction.cond(modifier) { case U64 | S64 => true }
       |      def isSigned32Or64: Boolean = PartialFunction.cond(modifier) { case S32 | S64 => true }
       |      def isSigned8Or16: Boolean  = PartialFunction.cond(modifier) { case S8 | S16 => true }
       |      def isHalf: Boolean         = PartialFunction.cond(modifier) { case F16 | BF16 => true }
       |      def isPackedHalf: Boolean   = PartialFunction.cond(modifier) { case F16x2 | BF16x2 => true }
       |      def isBitwise: Boolean      = PartialFunction.cond(modifier) { case AND | OR | XOR => true }
       |      def isZeroTest: Boolean     = PartialFunction.cond(modifier) { case Z | NZ => true }
       |    }
       |  }
       |  enum Modifier(val spelling: NonEmpty[String]) derives CanEqual {
       |    inline def name: String = spelling
       |    $modifierCases
       |    case Vendor(value: NonEmpty[String]) extends Modifier(value)
       |  }
       |  case class Guard(predicate: Operand.Predicate) derives CanEqual
       |
       |  object Definition {
       |    lazy val all: Vec[Definition] = Vec($chunks).flatten
       |    private lazy val byMnemonic: Map[String, Vec[Definition]] = all.groupBy(_.mnemonic.raw)
       |    def forMnemonic(mnemonic: String): Vec[Definition] = byMnemonic.getOrElse(mnemonic, Vec.empty)
       |  }
       |  class Definition(
       |      val name: NonEmpty[String],
       |      val mnemonic: NonEmpty[String],
       |      val forms: Vec[String]
       |  ) {
       |    override final def toString: String = name.raw
       |  }
       |  $holders
       |}
       |import Instruction.*
       |${GeneratedHeader.annotation()}case class Instruction(
       |    form: Form,
       |    modifiers: Vec[Modifier] = Vec.empty,
       |    guard: Option[Guard] = None
       |) derives CanEqual {
       |  inline def definition: Definition = Definition.all(form.ordinal)
       |  inline def mnemonic: NonEmpty[String] = definition.mnemonic
       |  inline def operands: Vec[Operand] = form.operands
       |}
       |""".stripMargin
  }

  private def renderForms(model: Vector[Case]): String = {
    val aliases = model.groupBy(_.family).toVector.sortBy(_._1).map { (family, cases) =>
      s"  type $family = ${cases.map(_.name).mkString(" | ")}"
    }.mkString("\n")
    val cases = model.map { entry =>
      val arguments = entry.slots.map(slot => s"${slot.name}: InstructionOperand.${slot.kind}").mkString(", ")
      val operands  = entry.slots.map(_.name).mkString(", ")
      s"case ${entry.name}($arguments) extends Form($operands)"
    }.mkString("\n")
    s"""${GeneratedHeader.scala("SASS typed logical instruction forms from pinned NVIDIA descriptions")}
       |package net.kurobako.gemstone.isa.sass
       |import net.kurobako.gemstone.collection.*
       |import net.kurobako.gemstone.collection.Vec.*
       |${GeneratedHeader.annotation()}object Form {
       |$aliases
       |}
       |${GeneratedHeader.annotation()}enum Form(operandValues: Operand*) derives CanEqual {
       |  val operands: Vec[Operand] = operandValues.toVec
       |  $cases
       |}
       |""".stripMargin
  }

  private def renderDecoding(chunks: Int): String = {
    val decoders = Vector.tabulate(chunks)(index => s"InstructionDefinitions$index.decoders").mkString(",\n")
    s"""${GeneratedHeader.scala("SASS instruction decoding by mnemonic")}
       |package net.kurobako.gemstone.isa.sass
       |import net.kurobako.gemstone.collection.*
       |import Instruction.*
       |${GeneratedHeader.annotation()}object InstructionDefinitions {
       |  lazy val decoders: Vec[FormDecoder] = Vec($decoders).flatten
       |  private lazy val byMnemonic: Map[String, Vec[FormDecoder]] = decoders.groupBy(_.definition.mnemonic.raw)
       |  def decode(mnemonic: String, modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Either[String, Instruction] = {
       |    val decoders = byMnemonic.getOrElse(mnemonic, Vec.empty[FormDecoder])
       |    decoders.view.flatMap(_.decode(operands)).headOption.map(Instruction(_, modifiers, guard))
       |      .toRight(if (decoders.isEmpty) s"unknown SASS mnemonic $$mnemonic"
       |               else s"invalid operands for $$mnemonic: $$operands")
       |  }
       |}
       |""".stripMargin
  }

  private def renderDefinitions(model: Vector[Case], index: Int): String = {
    val decoders = model.map { entry =>
      val kinds = entry.slots.map(slot => s", ${slot.kind}").mkString
      s"FormDecoder(Definitions$index.${entry.name}$kinds)(Form.${entry.name}.apply)"
    }.mkString(",\n")
    s"""${GeneratedHeader.scala("SASS instruction decoders and checked operand constructors")}
       |package net.kurobako.gemstone.isa.sass
       |import net.kurobako.gemstone.collection.*
       |import Instruction.*
       |import InstructionOperand.*
       |${GeneratedHeader.annotation()}object InstructionDefinitions$index {
       |  val decoders: Vec[FormDecoder] = Vec(
       |    $decoders
       |  )
       |}
       |""".stripMargin
  }
}
