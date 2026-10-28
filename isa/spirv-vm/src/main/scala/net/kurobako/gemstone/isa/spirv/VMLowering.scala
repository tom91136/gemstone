package net.kurobako.gemstone.isa.spirv

import OpBuilder.*
import VMProgram.*
import net.kurobako.gemstone.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.vm.*
import scala.util.control.NonFatal

/** Lowers every instruction of a program into its per-pc op table. */
private object VMLowering {

  def lower(program: VMProgram): Array[Op] = {
    val functions = Array.fill[Option[Function]](program.module.instructions.size)(None)
    program.functions.values.foreach(function =>
      (function.start to function.end).foreach(functions(_) = Some(function))
    )
    val ops = Array.tabulate(program.module.instructions.size) { pc =>
      val site        = Site(program, pc, functions(pc))
      val instruction = program.module.instructions(pc)
      try lower(site, instruction)
      catch {
        case NonFatal(error) =>
          Op.Fail(Fault.InvalidRequest(s"malformed SPIR-V execution state: ${throwables.detail(error)}"))
      }
    }
    // a run of instructions without an execution effect, such as debug information, passes in one step from any of them
    val _ = ops.indices.reverse.foldLeft(0) { (following, pc) =>
      if (ops(pc) ne Op.Next) 0
      else {
        val run = if (pc + 1 < ops.length && functions(pc + 1) == functions(pc)) following + 1 else 1
        if (run > 1) ops(pc) = Op.Skip(run)
        run
      }
    }
    ops
  }

  /** The op families in the order lowering tries them, each with what its ops run. */
  def families(site: Site): Vec[(String, PartialFunction[Instruction, Op])] = Vec(
    "labels, branches, calls and returns"                   -> ControlOps.lower(site),
    "integer, floating-point and matrix arithmetic"         -> ArithmeticOps.lower(using site),
    "comparisons and selection"                             -> ComparisonOps.lower(using site),
    "conversions and value copies"                          -> ConversionOps.lower(using site),
    "variables, loads, stores and pointer operations"       -> MemoryOps.lower(using site),
    "atomic memory operations and memory barriers"          -> AtomicOps.lower(using site),
    "composite construction, extraction and permutation"    -> CompositeOps.lower(using site),
    "workgroup and subgroup collectives and barriers"       -> CollectiveOps.lower(site),
    "extended instructions"                                 -> ExtendedOps.lower(using site),
    "image access and sampling"                             -> ImageLowering.lower,
    "OpenCL pipes"                                          -> PipeLowering.lower,
    "device-side enqueue"                                   -> DeviceEnqueueLowering.lower,
    "integer dot products"                                  -> IntegerDotOps.lower(site),
    "Intel and VALVE vendor arithmetic"                     -> IntelArithmeticOps.lower(using site),
    "cooperative matrix loads, stores and products"         -> CooperativeMatrixOps.lower(using site),
    "cooperative vector loads, stores and products"         -> CooperativeVectorLowering.lower(site),
    "tensor layouts, views, and tensor loads and stores"    -> TensorAddressingLowering.lower,
    "tensor element reads, writes and size queries"         -> TensorArmOps.lower,
    "vendor gathers, scatters, predicated and raw accesses" -> VendorMemoryOps.lower(site),
    "Intel subgroup block reads and writes"                 -> IntelBlockOps.lower(site),
    "clock reads"                                           -> ClockOps.lower(site),
    "module-scope declarations the loader reads"            -> constants,
    "composite continuations the loader folds"              -> continuations
  )

  // SPV_INTEL_long_composites continuations fold into what they continue as the module loads; one left here follows
  // nothing it can continue
  private val continuations: PartialFunction[Instruction, Op] = {
    case instruction @ (_: Instruction.TypeStructContinuedINTEL | _: Instruction.ConstantCompositeContinuedINTEL |
        _: Instruction.SpecConstantCompositeContinuedINTEL | _: Instruction.CompositeConstructContinuedINTEL) =>
      Op.Fail(Fault.InvalidRequest(s"${instruction.opcode} does not follow the instruction it continues"))
  }

  // declarations outside the core grammar's declaration classes, whose ops never run; aliasing scopes only permit
  // optimisations
  private val constants: PartialFunction[Instruction, Op] = {
    case Instruction.ConstantFunctionPointerINTEL(_, _, _) | Instruction.AliasDomainDeclINTEL(_, _) |
        Instruction.AliasScopeDeclINTEL(_, _, _) | Instruction.AliasScopeListDeclINTEL(_, _) => Op.Next
    // SPV_INTEL_function_variants' conditional declarations, which the loader reads, or ignores where they only declare
    // what a module may use
    case Instruction.ConditionalCapabilityINTEL(_, _) | Instruction.ConditionalExtensionINTEL(_, _) |
        Instruction.ConditionalEntryPointINTEL(_, _, _, _, _) => Op.Next
    case Instruction.ConstantPipeStorage(_, _, _, _, _) => Op.Next
  }

  def unsupported(instruction: Instruction): Fault =
    Fault.Unsupported(s"${instruction.opcode} execution is not implemented")

  private def lower(site: Site, instruction: Instruction): Op =
    families(site).view.map(_._2.applyOrElse(instruction, _ => unmatched)).find(_ ne unmatched)
      .getOrElse(Op.Fail(unsupported(instruction)))
}
