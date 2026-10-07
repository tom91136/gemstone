package net.kurobako.gemstone.isa.spirv

import VMProgram.*
import net.kurobako.gemstone.*
import net.kurobako.gemstone.vm.*
import scala.util.control.NonFatal

/** Lowers every instruction of a program into its per-pc op table. */
private object VMLowering {
  import Operand.*

  /** Load-time view of one instruction: its program, position and enclosing function. */
  class Site(val program: VMProgram, val pc: Int, val function: Option[Function]) {
    def label(operand: Operand): Either[Fault, Int] = function match {
      case Some(value) =>
        value.labels.get(id(operand))
          .toRight(Fault.InvalidRequest(s"SPIR-V label %${id(operand)} is absent in function %${value.id}"))
      case None => Left(Fault.InvalidRequest(s"SPIR-V label %${id(operand)} is outside a function"))
    }
  }

  def lower(program: VMProgram): Array[Op] = {
    val functions = Array.fill[Option[Function]](program.module.instructions.size)(None)
    program.functions.values.foreach(function =>
      (function.start to function.end).foreach(functions(_) = Some(function))
    )
    Array.tabulate(program.module.instructions.size) { pc =>
      val site = Site(program, pc, functions(pc))
      try lower(site, program.module.instructions(pc))
      catch {
        case NonFatal(error) =>
          Op.Fail(Fault.InvalidRequest(s"malformed SPIR-V execution state: ${throwables.detail(error)}"))
      }
    }
  }

  private def lower(site: Site, instruction: Instruction): Op =
    ControlOps.lower(site)
      .orElse(ArithmeticOps.lower(site))
      .orElse(ComparisonOps.lower(site))
      .orElse(ConversionOps.lower(site))
      .orElse(MemoryOps.lower(site))
      .orElse(AtomicOps.lower(site))
      .orElse(CompositeOps.lower(site))
      .orElse(CollectiveOps.lower(site))
      .orElse(ExtendedOps.lower(site))
      .orElse(ImageOps.lower(site))
      .applyOrElse(instruction, other => Op.Fail(Fault.Unsupported(s"${other.opcode} execution is not implemented")))
}
