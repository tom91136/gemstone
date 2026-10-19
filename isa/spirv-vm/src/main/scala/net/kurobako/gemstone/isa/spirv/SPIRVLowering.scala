package net.kurobako.gemstone.isa.spirv

import VMLowering.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.isa.spirv.asm.*
import net.kurobako.gemstone.vm.*

/** How the VM takes instructions, as its lowering decides when a module loads, for tools that classify instructions
  * without running them.
  */
object SPIRVLowering {
  private lazy val minimal: Option[Module] = Parser.parse(
    """OpCapability Addresses
      |OpCapability Kernel
      |OpMemoryModel Physical64 OpenCL
      |OpEntryPoint Kernel %main "main"
      |%void = OpTypeVoid
      |%fn = OpTypeFunction %void
      |%main = OpFunction %void None %fn
      |%entry = OpLabel
      |OpReturn
      |OpFunctionEnd
      |""".stripMargin
  ).toOption

  private lazy val site: Option[Site] =
    minimal.flatMap(LoadedProgram.load(_, Validation.Environment.OpenCLCompute).toOption).map(Site(_, 0, None))

  /** What the ops of the VM family that lowers `instruction` run, or the fault where no family takes it. */
  def family(instruction: Instruction): Either[Fault, String] =
    site.flatMap(at =>
      families(at).collectFirst {
        case (name, family) if family.isDefinedAt(instruction) => name
      }
    ).toRight(unsupported(instruction))

  /** The fault each instruction of `module` raises when it runs, where lowering already decides it, in module order. */
  def failures(module: Module, environment: Validation.Environment): Either[Fault, Vec[Option[Fault]]] =
    LoadedProgram.load(module, environment).map(program => Vec.from(program.ops.map(_.failure)))

  /** Whether the loader defines the result of `instruction`, a type or constant declaration whose ids are numbered from
    * 1, or rejects it, once it is declared in a minimal module with its ids moved past the module's own.
    */
  def declares(instruction: Instruction): Boolean = minimal.exists { module =>
    val offset = module.header.bound.raw
    val words  = instruction.operands.flatMap(operand =>
      if (operand.kind.category == OperandKind.Category.Id) operand.words.map(word => Word(word.raw + offset))
      else operand.words
    )
    instruction.definition.decoder(RawInstruction(instruction.opcodeNumber, words)).toOption.exists { moved =>
      val at     = module.instructions.indexWhere(_.definition == Instruction.Definition.Function)
      val result = moved.definition.shapes.indexWhere(_.kind == OperandKind.IdResult)
      val probe  = module.copy(
        header = module.header.copy(bound = Word(offset * 2 + 64)),
        instructions = module.instructions.take(at) ++ Vec(moved) ++ module.instructions.drop(at)
      )
      LoadedProgram.loadUnchecked(probe).fold(
        _ => true,
        program =>
          result >= 0 && {
            val defined = Operand.id(moved.operands(result))
            program.runtimeType(defined).nonEmpty || program.constants.contains(defined)
          }
      )
    }
  }
}
