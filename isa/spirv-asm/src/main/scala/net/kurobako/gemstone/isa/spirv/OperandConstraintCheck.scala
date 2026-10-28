package net.kurobako.gemstone.isa.spirv

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import scala.collection.mutable

object OperandConstraintCheck {
  import OperandConstraint.*

  private val rulesByOpcode: Map[String, Array[OperandRule]] =
    OperandConstraintTable.rules.groupBy(_.opcode).view.mapValues(_.toArray).toMap

  private val kindRules: Array[KindRule] = OperandConstraintTable.kinds.toArray

  private lazy val scopes: Set[Long] =
    Instruction.operandKinds.get(OperandKind.Scope).fold(Set.empty[Long])(_.enumerants.map(_.value).toSet)

  private lazy val memoryBits: Long = Instruction.operandKinds.get(OperandKind.MemorySemantics)
    .fold(0L)(info => ExtensionEnumerants.of(info).foldLeft(0L)(_ | _.value))

  private val scalarTypes: Set[String] = Set("OpTypeBool", "OpTypeInt", "OpTypeFloat")

  private val typeOpcodeNumbers: Set[Int] = InstructionRequirements.all.collect {
    case (num, req) if req.grammarClass == "Type-Declaration" && num != 39 => num
  }.toSet

  /** The constraint the table places on the operand at grammar position `index` of `opcode`, or else on its kind. */
  def of(opcode: String, index: Int, kind: OperandKind): Option[OperandConstraint] =
    OperandConstraintTable.rules.find(rule => rule.opcode == opcode && rule.index == index).map(_.constraint)
      .orElse(OperandConstraintTable.kinds.find(_.kind == kind).map(_.constraint))

  /** The operands of `module` that break the table, in module order. An operand naming an id the module does not
    * declare is not checked, as the loader reports undeclared ids itself.
    */
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  def violations(module: Module): Vec[OperandViolation] = {
    val declarations           = mutable.HashMap.empty[Int, Instruction]
    val instructions           = module.instructions
    var instIndex              = 0
    var hasMaskedGatherScatter = false
    while (instIndex < instructions.length) {
      val inst = instructions(instIndex)
      if (!hasMaskedGatherScatter) {
        inst match {
          case Instruction.Capability(declared)
              if Operand.unsigned(declared) == Capability.MaskedGatherScatterINTEL.toLong =>
            hasMaskedGatherScatter = true
          case _ =>
        }
      }
      val operands = inst.operands
      var opIndex  = 0
      while (opIndex < operands.length) {
        val op = operands(opIndex)
        if (op.kind == OperandKind.IdResult) {
          declarations(Operand.id(op)) = inst
        }
        opIndex += 1
      }
      instIndex += 1
    }

    val facts  = Facts(declarations, hasMaskedGatherScatter)
    val result = Vec.newBuilder[OperandViolation]

    instIndex = 0
    while (instIndex < instructions.length) {
      val instruction = instructions(instIndex)
      val opcode      = opcodeOf(instruction)
      val rulesOpt    = rulesByOpcode.get(opcode)
      val operands    = instruction.operands
      var position    = 0
      while (position < operands.length) {
        val operand = operands(position)
        rulesOpt match {
          case Some(rules) =>
            var r = 0
            while (r < rules.length) {
              val rule = rules(r)
              if (covers(rule, position) && breaks(rule.constraint, operand, facts)) {
                result += OperandViolation(instIndex, opcode, rule.name, rule.constraint)
              }
              r += 1
            }
          case None =>
        }
        var k = 0
        while (k < kindRules.length) {
          val kindRule = kindRules(k)
          if (kindRule.kind == operand.kind && breaks(kindRule.constraint, operand, facts)) {
            result += OperandViolation(instIndex, opcode, operand.kind.toString, kindRule.constraint)
          }
          k += 1
        }
        position += 1
      }
      instIndex += 1
    }

    result.result()
  }

  /** What the checks of one module consult: its declarations by result id, and whether it admits pointer components. */
  private class Facts(val declarations: mutable.HashMap[Int, Instruction], val pointerComponents: Boolean)

  private def covers(rule: OperandRule, position: Int): Boolean = rule.quantifier match {
    case OperandQuantifier.Repeated => position >= rule.index
    case _                          => position == rule.index
  }

  // avoids option allocation per operand
  @SuppressWarnings(Array("scalafix:DisableSyntax.null"))
  private def breaks(constraint: OperandConstraint, operand: Operand, facts: Facts): Boolean = constraint match {
    case OneOf(values) => !values.contains(Operand.unsigned(operand))
    case ByteWidth     => !isWholeBytes(Operand.unsigned(operand))
    case _             =>
      val decl = facts.declarations.getOrElse(Operand.id(operand), null)
      if (decl != null) !holds(constraint, decl, facts) else false
  }

  private def holds(constraint: OperandConstraint, declaration: Instruction, facts: Facts): Boolean =
    constraint match {
      case Type              => isType(declaration)
      case Declares(opcodes) => opcodes.contains(opcodeOf(declaration))
      case Value(kind)       => typedAs(declaration, facts.declarations, kind.opcodes)
      case ScopeId           => word32(declaration, facts.declarations)(scopes.contains)
      case MemorySemanticsId => word32(declaration, facts.declarations)(memorySemantics(_))
      case ScalarComponent   =>
        scalarTypes.contains(opcodeOf(declaration)) ||
        (facts.pointerComponents && opcodeOf(declaration) == "OpTypePointer")
      case ByteWidth | OneOf(_) => true
    }

  private def isWholeBytes(width: Long): Boolean = width > 0L && width % 8L == 0L

  /** Whether the type of `value` is one of `opcodes`, unless the module does not declare that type. */
  // avoids option allocation per operand
  @SuppressWarnings(Array("scalafix:DisableSyntax.null"))
  private def typedAs(
      value: Instruction,
      declarations: mutable.HashMap[Int, Instruction],
      opcodes: Vec[String]
  ): Boolean = {
    val typeId = resultTypeOf(value)
    if (typeId <= 0) false
    else {
      val typeDeclaration = declarations.getOrElse(typeId, null)
      if (typeDeclaration == null) true
      else opcodes.contains(opcodeOf(typeDeclaration))
    }
  }

  /** Whether `value` is a 32-bit integer whose `OpConstant` value, when it is one, satisfies `valid`. */
  // avoids option allocation per operand
  @SuppressWarnings(Array("scalafix:DisableSyntax.null"))
  private def word32(
      value: Instruction,
      declarations: mutable.HashMap[Int, Instruction]
  )(valid: Long => Boolean): Boolean = {
    val typeId = resultTypeOf(value)
    if (typeId <= 0) false
    else {
      val typeDeclaration = declarations.getOrElse(typeId, null)
      if (typeDeclaration == null) true
      else isInteger32(typeDeclaration) && (!isOpConstant(value) || valid(literal(value)))
    }
  }

  private def memorySemantics(value: Long): Boolean = (value & ~memoryBits) == 0L

  private def isInteger32(declaration: Instruction): Boolean =
    opcodeOf(declaration) == "OpTypeInt" &&
      declaration.operands.length > 1 &&
      Operand.unsigned(declaration.operands(1)) == 32L

  /** The value of an `OpConstant`'s literal, low word first, as the low 64 bits. */
  private def literal(constant: Instruction): Long = {
    val operand = constant.operands(2)
    Operand.unsigned(operand) | (if (operand.words.length > 1) operand.words(1).toUnsigned.toLong << 32 else 0L)
  }

  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private def resultTypeOf(instruction: Instruction): Int = {
    val operands = instruction.operands
    var i        = 0
    var result   = 0
    var found    = false
    while (i < operands.length && !found) {
      val op = operands(i)
      if (op.kind == OperandKind.IdResultType) { result = Operand.id(op); found = true }
      i += 1
    }
    result
  }

  private def opcodeOf(instruction: Instruction): String = instruction.definition.opcode.raw

  private def isOpConstant(instruction: Instruction): Boolean = opcodeOf(instruction) == "OpConstant"

  private def isType(instruction: Instruction): Boolean =
    typeOpcodeNumbers.contains(instruction.definition.opcodeNumber.toInt)
}
