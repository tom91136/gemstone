package net.kurobako.gemstone.isa.spirv.asm

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.spirv.*

object Printer {
  def render(module: Module): String = {
    val header   = module.header
    val metadata = s"; SPIR-V\n; Version: ${header.version}\n; Generator: ${header.generator.toUnsigned}\n" +
      s"; Bound: ${header.bound.toUnsigned}\n; Schema: ${header.schema.toUnsigned}\n; Endianness: ${module.endianness}\n"
    val imports  = importMap(module)
    val comments = module.comments.groupMap(_.before.raw)(comment => s"; ${comment.text}")
    val lines    = module.instructions.zipWithIndex.flatMap { case (instruction, index) =>
      comments.getOrElse(index, Vec.empty[String]) :+ render(instruction, imports)
    } ++ comments.getOrElse(module.instructions.size, Vec.empty[String])
    metadata + lines.mkString("\n") + "\n"
  }

  /** The instruction as the module writes it, its extended instruction set under the module's imports. */
  def render(module: Module, instruction: Instruction): String = render(instruction, importMap(module))

  /** The extended instruction set each import names, by the id that names it. */
  private def importMap(module: Module): Map[Int, String] =
    module.instructions.collect { case Instruction.ExtInstImport(dest, name) =>
      dest.words.head.raw -> LiteralString.decode(name.words)
    }.toMap

  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private def render(instruction: Instruction, imports: Map[Int, String]): String = {
    val result =
      instruction.operands.find(_.kind == OperandKind.IdResult).map(value =>
        s"%${value.words.head.toUnsigned} = "
      ).getOrElse("")
    val operands = instruction.operands.filterNot(_.kind == OperandKind.IdResult)
    var rawTail  = false
    val text     = Vec.newBuilder[String]
    operands.zipWithIndex.foreach { case (operand, index) =>
      // Context-dependent literals use SPIR-V's raw-word syntax to retain every bit, including NaN payloads.
      val rendered =
        if (
          instruction.definition == Instruction.Definition.Switch &&
          operand.kind.category == OperandKind.Category.Composite
        )
          operand.words.map(raw(_))
        else if (instruction.definition == Instruction.Definition.SpecConstantOp && index >= 2)
          operand.words.map(raw(_))
        else if (operand.kind == OperandKind.LiteralExtInstInteger) {
          val names = instruction.operands.lift(2).flatMap(value => imports.get(value.words.head.raw))
            .flatMap(ExtendedInstruction.names.get)
          Vec(names.flatMap(_.find(_._2 == operand.words.head.raw).map(_._1)).getOrElse(raw(operand.words.head)))
        } else renderOperand(operand.kind, operand.words, 0)._1
      // A raw word switches the rest of the instruction to untyped token parsing in SPIRV-Tools.
      if (rawTail || rendered.exists(_.startsWith("!"))) {
        rawTail = true
        text ++= operand.words.map(raw(_))
      } else text ++= rendered
    }
    val tokens = text.result()
    result + instruction.definition.opcode + (if (tokens.isEmpty) "" else " " + tokens.mkString(" "))
  }
  private def raw(word: Word): String = f"!0x${word.raw}%08x"
  private def renderOperand(kind: OperandKind, words: Vec[Word], offset: Int): (Vec[String], Int) = {
    val value = words(offset).toUnsigned.toLong
    if (kind.category == OperandKind.Category.Id) (Vec(s"%$value"), offset + 1)
    else if (kind == OperandKind.LiteralContextDependentNumber) (words.drop(offset).map(raw(_)), words.size)
    else if (kind == OperandKind.LiteralString) {
      val end     = words.indexWhere(word => (0 until 4).exists(i => ((word.raw >>> (i * 8)) & 0xff) == 0), offset) + 1
      val encoded = words.slice(offset, end)
      val text    = LiteralString.decode(encoded)
      if (LiteralString.encode(text) != encoded) (encoded.map(raw(_)), end)
      else (Vec("\"" + text.replace("\\", "\\\\").replace("\"", "\\\"") + "\""), end)
    } else if (kind.category == OperandKind.Category.Composite) renderParameters(kind.components, words, offset)
    else if (kind == OperandKind.LiteralSpecConstantOpInteger) {
      val name =
        value.toInt.refine[SPIRVOpcodeNumber].toOption.flatMap(number => Instruction.definitionsFor(number).headOption)
          .map(_.opcode.stripPrefix("Op")).getOrElse(raw(words(offset)))
      (Vec(name), offset + 1)
    } else Instruction.operandKinds.get(kind) match {
      case Some(info) =>
        val selected = ExtensionEnumerants.of(info).filter(enumerant =>
          if (info.kind.category == OperandKind.Category.ValueEnum || enumerant.value == 0) enumerant.value == value
          else (value & enumerant.value) == enumerant.value
        )
        val (parameters, end) = renderParameters(selected.flatMap(_.parameterKinds), words, offset + 1)
        (Vec(if (selected.isEmpty) raw(words(offset)) else selected.map(_.name).mkString("|")) ++ parameters, end)
      case None => (Vec(value.toString), offset + 1)
    }
  }
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private def renderParameters(kinds: Iterable[OperandKind], words: Vec[Word], offset: Int): (Vec[String], Int) = {
    val values = Vec.newBuilder[String]
    var index  = offset
    kinds.foreach { kind =>
      val (next, end) = renderOperand(kind, words, index)
      values ++= next
      index = end
    }
    (values.result(), index)
  }
}
