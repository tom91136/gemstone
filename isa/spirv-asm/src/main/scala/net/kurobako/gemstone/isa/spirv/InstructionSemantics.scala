package net.kurobako.gemstone.isa.spirv

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.semantics.render.*
import scala.util.control.NonFatal

/** The description of instructions as definitions, by rendering their staged semantics. */
object InstructionSemantics {

  /** The semantics of `instruction` as a definition over its ids, as `instance` names and types them, named after the
    * instruction, or why there is none: it is outside the staged families, which compute a result of a result type from
    * at least one id, or its semantics do not form a definition. `extendedSet` names the instruction set an OpExtInst
    * imports; an extended instruction is named within its set.
    */
  def describe(
      instruction: Instruction,
      instance: IdRenderer.Instance,
      extendedSet: Option[String] = None
  ): Either[Unrendered, Definition] = try {
    val declared = Scope(instance)
    val sinks    = IdRenderer(instance, declared)
    val rendered =
      if (
        instruction.definition.shapes.take(2).map(_.kind) != Vec(OperandKind.IdResultType, OperandKind.IdResult) ||
        SPIRVStaged.operandIds(instruction).operands.isEmpty
      ) None
      else if (instruction.opcodeNumber.toInt != SPIRV.Opcode.ExtInst)
        CoreFamily.values.view.flatMap(SPIRVStaged.core(_, Render, sinks, instruction)(None)).headOption
      else
        extendedSet.flatMap(ExtendedSet.of) match {
          case Some(ExtendedSet.GLSL) =>
            GLSLFamily.values.view.flatMap(SPIRVStaged.glsl(_, Render, sinks, instruction)(None)).headOption
          case Some(ExtendedSet.OpenCL) =>
            OpenCLFamily.values.view.flatMap(SPIRVStaged.openCL(_, Render, sinks, instruction)(None)).headOption
          case None => None
        }
    rendered.toRight(Unrendered.Unstaged(Vec.empty)).flatMap(steps =>
      Definition.from(steps, name(instruction, extendedSet), declared, Vec.empty, settle = true)
        .left.map(Unrendered.Undefined(_))
    )
  } catch {
    case NonFatal(error) =>
      Left(Unrendered.Undefined(s"${name(instruction, extendedSet)} does not render: $error"))
  }

  /** The mnemonic of `instruction`, or of the extended instruction it imports from `extendedSet`. */
  def name(instruction: Instruction, extendedSet: Option[String]): String = {
    val encoded = Operand.id(instruction.operands.lift(3).getOrElse(instruction.operands.head))
    extendedSet.filter(_ => instruction.opcodeNumber.toInt == SPIRV.Opcode.ExtInst)
      .flatMap(ExtendedInstruction.names.get).flatMap(_.collectFirst { case (name, `encoded`) => name })
      .getOrElse(instruction.definition.opcode.raw)
  }
}
