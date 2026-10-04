package net.kurobako.gemstone.codegen

/** Projects FORMAT syntax into the logical top-level operands exposed by decoded instructions. */
object SASSInstructionModel {
  case class Slot(name: String, kind: String, optional: Boolean)
  case class FormShape(form: String, opcode: String, slots: Vector[Slot])
  case class Shape(opcode: String, slots: Vector[Slot], forms: Vector[FormShape])

  private val namedKinds = Set(
    "B",
    "BD",
    "CBU_STATE",
    "CBU_STATE_DIST",
    "Scoreboard",
    "C",
    "DESC",
    "SpecialRegister",
    "UImm",
    "SImm",
    "RSImm",
    "BITSET",
    "F16Imm",
    "F32Imm",
    "F64Imm",
    "PR",
    "CCPR"
  )
  private val attrs = Set("not", "negate", "absolute", "invert")

  private def allFields(nodes: Vector[SASSFormat.Node]): Vector[SASSFormat.Field] = nodes.flatMap {
    case field: SASSFormat.Field             => Vector(field)
    case SASSFormat.Group(_, _, children)    => allFields(children)
    case SASSFormat.Optional(None, children) => allFields(children)
    case SASSFormat.Optional(Some(_), _)     => Vector.empty
    case _                                   => Vector.empty
  }

  private def isOperandField(field: SASSFormat.Field): Boolean =
    field.kind.contains("Register") || field.kind.contains("Predicate") || namedKinds(field.kind)

  def modifierKinds(forms: Vector[SASSDescription.InstructionClass]): Set[String] = forms.flatMap { form =>
    val nodes = form.section("FORMAT").toVector.flatMap(section =>
      SASSFormat.parse(section.body).fold(error => throw IllegalArgumentException(error.toString), identity)
    )
    val fields = allFields(nodes).filterNot(field => Set("Pg", "UPg")(field.binding))
    // R2P also accepts its bank as a qualifier (R2P.PR).
    fields.takeWhile(field => !isOperandField(field)).map(_.kind) ++
      fields.filter(_.kind == "CCPR").map(_.kind)
  }.toSet

  private def fieldKind(field: SASSFormat.Field): String = {
    val kind = field.kind
    if (kind == "C") "Symbol"
    else if (kind == "DESC" || kind == "SpecialRegister") "SpecialRegister"
    else if (kind.contains("Predicate")) "Predicate"
    else if (Set("CBU_STATE", "CBU_STATE_DIST")(kind)) "Register|Symbol"
    else if (kind.contains("Register") || Set("B", "BD", "CBU_STATE", "CBU_STATE_DIST", "Scoreboard")(kind)) "Register"
    else if (kind == "BITSET") "Immediate|Group"
    else if (Set("UImm", "SImm", "RSImm", "F16Imm", "F32Imm", "F64Imm")(kind)) "Immediate"
    else if (Set("CC", "optCC", "Partsel", "PR", "CCPR")(kind)) "Symbol"
    else "Any"
  }

  private def fieldSlot(field: SASSFormat.Field, optionalContext: Boolean): Slot = {
    val nonOmittable = field.kind.contains("Register") && !field.suffix.contains("@")
    Slot(field.binding, fieldKind(field), optionalContext && field.default.nonEmpty && !nonOmittable)
  }

  private def groupSlot(open: Char, children: Vector[SASSFormat.Node], allowCC: Boolean): Vector[Slot] = {
    val fields = allFields(children).filter(field => isOperandField(field) || field.kind == "CC").filterNot(field =>
      Set("Pg", "UPg")(field.binding)
    )
    if (fields.isEmpty) Vector.empty
    else if (open == '(') slotsInNodes(children, depth = 1, allowCC = allowCC)
    else if (open == '[') {
      val hasConstantSelector = fields.exists(_.kind == "C")
      val register            = fields.find(field =>
        field.kind.contains("Register") ||
          Set("B", "BD", "CBU_STATE", "CBU_STATE_DIST", "Scoreboard")(field.kind)
      )
      if (hasConstantSelector && fields.count(_.kind != "C") >= 2)
        fields.find(_.kind == "C").map(field => Vector(Slot(field.binding, "ConstantBank", optional = false)))
          .getOrElse(Vector.empty)
      else register match {
        case Some(field) if Set("CBU_STATE", "CBU_STATE_DIST")(field.kind) =>
          Vector(Slot(field.binding, "Address|Symbol", optional = false))
        case Some(field) => Vector(Slot(field.binding, "Address", optional = false))
        case None        => slotsInNodes(children, depth = 1, allowCC = allowCC)
      }
    } else {
      val operandFields = fields.filterNot(field => attrs(field.kind) || field.kind == "Pg" || field.kind == "UPg")
      if (operandFields.size > 1) Vector(Slot(operandFields.head.binding, "Group", optional = false))
      else operandFields.headOption.map(field =>
        fieldSlot(field, optionalContext = false).copy(optional = field.default.nonEmpty)
      ).toVector
    }
  }

  private def splitComma(nodes: Vector[SASSFormat.Node]): Vector[Vector[SASSFormat.Node]] =
    nodes.foldLeft((Vector.empty[Vector[SASSFormat.Node]], Vector.empty[SASSFormat.Node])) {
      case ((done, current), SASSFormat.Text(raw))           => splitText(raw, done, current)
      case ((done, current), SASSFormat.Literal(raw, value)) => splitText(value, done, current, literal = Some(raw))
      case ((done, current), node)                           => (done, current :+ node)
    } match { case (done, current) => done :+ current }

  private def splitText(
      value: String,
      done: Vector[Vector[SASSFormat.Node]],
      current: Vector[SASSFormat.Node],
      literal: Option[String] = None
  ): (Vector[Vector[SASSFormat.Node]], Vector[SASSFormat.Node]) = {
    val commas = value.indices.filter(value(_) == ',')
    if (commas.isEmpty)
      (done, current :+ literal.fold[SASSFormat.Node](SASSFormat.Text(value))(SASSFormat.Literal(_, value)))
    else {
      val bounds = -1 +: commas :+ value.length
      val parts  = bounds.sliding(2).map(pair => value.substring(pair.head + 1, pair.last)).toVector
      val make: String => SASSFormat.Node =
        part => literal.fold[SASSFormat.Node](SASSFormat.Text(part))(raw => SASSFormat.Literal(raw, part))
      val first = current :+ make(parts.head)
      (
        done ++ Vector(first) ++ parts.slice(1, parts.size - 1).map(part => Vector(make(part))),
        Vector(make(parts.last))
      )
    }
  }

  private def afterOpcode(nodes: Vector[SASSFormat.Node]): Vector[SASSFormat.Node] = {
    val index = nodes.indexWhere { case SASSFormat.Text(raw) => raw.contains("Opcode"); case _ => false }
    if (index < 0) nodes
    else nodes(index) match {
      case SASSFormat.Text(raw) => Vector(SASSFormat.Text(raw.drop(raw.indexOf("Opcode") + "Opcode".length))) ++
          nodes.drop(index + 1)
      case _ => nodes.drop(index + 1)
    }
  }

  private def slotsInNodes(chunk: Vector[SASSFormat.Node], depth: Int, allowCC: Boolean): Vector[Slot] = {
    val state =
      chunk.zipWithIndex.foldLeft((Vector.empty[Slot], false)) { case ((found, previousOperand), (node, index)) =>
        val next = node match {
          case field: SASSFormat.Field
              if (isOperandField(field) || (allowCC && field.kind == "CC")) &&
                !Set("Pg", "UPg")(field.binding) =>
            val slashQualifier = chunk.lift(index - 1).exists {
              case SASSFormat.Text(raw)         => raw.trim.endsWith("/")
              case SASSFormat.Literal(_, value) => value.trim.endsWith("/")
              case _                            => false
            } && previousOperand
            val selector = slashQualifier || field.kind == "optCC" || field.kind == "Partsel" ||
              field.kind.startsWith("ISWZ")
            if (!selector) {
              (found :+ fieldSlot(field, optionalContext = depth == 0), true)
            } else (found, previousOperand)
          case SASSFormat.Optional(None, children) =>
            val slots = slotsInNodes(children, depth, allowCC)
            (found ++ slots, previousOperand || slots.nonEmpty)
          case SASSFormat.Optional(Some(_), _)     => (found, previousOperand)
          case SASSFormat.Group(open, _, children) =>
            val slots = groupSlot(open, children, allowCC)
            (found ++ slots, previousOperand || slots.nonEmpty)
          case _ => (found, previousOperand)
        }
        next
      }
    val values = state._1
    val fields = allFields(chunk).filter(isOperandField(_))
    values match {
      case Vector(c, bank, _) if fields.headOption.exists(_.kind == "C") && bank.kind == "Immediate" =>
        Vector(Slot(c.name, "ConstantBank", optional = false))
      case Vector(_, descriptor, address)
          if fields.headOption.exists(_.kind == "DESC") &&
            fields.lift(1).exists(_.kind.contains("UniformRegister")) &&
            descriptor.kind == "Address" && address.kind == "Address" =>
        Vector(address)
      case _ => values
    }
  }

  /** Each FORMAT row keeps its own layout; repeated layouts across targets are deduplicated. */
  def formShape(form: SASSDescription.InstructionClass): Vector[FormShape] = {
    val section = form.section("FORMAT").getOrElse(throw IllegalArgumentException(s"${form.name}: missing FORMAT"))
    val syntax  = SASSFormat.parse(section.body).fold(
      error => throw IllegalArgumentException(s"${form.name}: ${error.message} at ${error.offset}"),
      identity
    )
    val operandSyntax = afterOpcode(syntax).filter {
      case SASSFormat.Optional(Some(marker), _) if Set("&", "?")(marker) => false
      case _                                                             => true
    }
    val projected = splitComma(operandSyntax).zipWithIndex.flatMap { (chunk, index) =>
      slotsInNodes(chunk, depth = 0, allowCC = index > 0)
    }
    val slots =
      if (
        form.opcodes.exists(_.startsWith("P2R")) && projected.size == 4 &&
        projected.lift(1).exists(_.name == "PR")
      ) projected.patch(1, Vector.empty, 1)
      else if (form.opcodes.exists(opcode => opcode.startsWith("P2R") || opcode.startsWith("R2P"))) {
        val banks = allFields(operandSyntax).filter(_.kind == "CCPR").map(_.binding).toSet
        projected.map(slot => if (banks(slot.name)) slot.copy(optional = true) else slot)
      } else projected
    form.opcodes.map(opcode => FormShape(form.name, opcode.takeWhile(_ != '.'), slots))
  }

  /** Deduplicates equal opcode layouts while preserving the forms that provide each layout. */
  def shapes(forms: Iterable[SASSDescription.InstructionClass]): Vector[Shape] =
    forms.toVector.flatMap(formShape(_)).groupBy(_.opcode).toVector.sortBy(_._1).flatMap { case (opcode, rows) =>
      rows.groupBy(
        _.slots
      ).toVector.sortBy(_._1.map(slot => s"${slot.name}:${slot.kind}:${slot.optional}").mkString("\u0000"))
        .map { case (slots, same) => Shape(opcode, slots, same.distinct.sortBy(_.form)) }
    }
}
