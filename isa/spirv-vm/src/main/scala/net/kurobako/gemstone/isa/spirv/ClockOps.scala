package net.kurobako.gemstone.isa.spirv

import Op.*
import VMInvocation.*
import VMMemory.*
import VMProgram.*
import net.kurobako.gemstone.vm.*

/** SPV_KHR_shader_clock. Subgroup and workgroup clocks count the instructions the invocation has issued, as the NVIDIA
  * VM counts a workgroup's issues for %clock; device clocks are nanoseconds since the Unix epoch on a clock that never
  * runs backwards, as its %globaltimer is.
  */
private object ClockOps {
  import Operand.*

  def lower(site: Site): PartialFunction[Instruction, Op] = { case Instruction.ReadClockKHR(t, d, scope) =>
    val resultType = id(t)
    val dest       = id(d)
    val scopeId    = id(scope)
    site.program.runtimeType(resultType) match {
      case Some(RuntimeType.Integer(width, _)) if width == 64 => Read(scopeId, dest, resultType, halves = false)
      case Some(RuntimeType.VectorType(component, count))
          if count == 2 &&
            site.program.integerShape(component).contains(32 -> 1) =>
        Read(scopeId, dest, resultType, halves = true)
      case _ => Fail(Fault.InvalidRequest("OpReadClockKHR result is not a 64-bit integer or two 32-bit integers"))
    }
  }

  private class Read(scope: Int, dest: Int, resultType: Int, halves: Boolean) extends Op {
    def run(execution: InvocationState, memory: MemoryState): AddressSpace.ReadResult[Unit] =
      ClockSemantics.read(InvocationMachine(execution))(scope, dest, resultType, halves)
  }
}
