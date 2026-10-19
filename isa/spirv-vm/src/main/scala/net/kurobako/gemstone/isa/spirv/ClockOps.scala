package net.kurobako.gemstone.isa.spirv

import Op.*
import VMProgram.*
import net.kurobako.gemstone.*
import net.kurobako.gemstone.vm.*

/** SPV_KHR_shader_clock. Subgroup and workgroup clocks count the instructions the invocation has issued, as the NVIDIA
  * VM counts a workgroup's issues for %clock; device clocks are nanoseconds since the Unix epoch on a clock that never
  * runs backwards, as its %globaltimer is.
  */
private object ClockOps {
  import Operand.*

  private enum Clock derives CanEqual {
    case Scalar, Halves
  }

  private val origin = platform.epochMillis * 1000000L - platform.monotonicNanos

  def lower(site: Site): PartialFunction[Instruction, Op] = { case Instruction.ReadClockKHR(t, d, scope) =>
    val resultType = id(t)
    val dest       = id(d)
    val scopeId    = id(scope)
    site.program.runtimeType(resultType) match {
      case Some(RuntimeType.Integer(width, _)) if width == 64 => read(resultType, dest, scopeId, Clock.Scalar)
      case Some(RuntimeType.VectorType(component, count))
          if count == 2 &&
            site.program.integerShape(component).contains(32 -> 1) =>
        read(resultType, dest, scopeId, Clock.Halves)
      case _ => Fail(Fault.InvalidRequest("OpReadClockKHR result is not a 64-bit integer or two 32-bit integers"))
    }
  }

  private def read(resultType: Int, dest: Int, scope: Int, clock: Clock): Op = step { (execution, _) =>
    scalar(execution.values, scope).flatMap { value =>
      val ticks =
        if (value == Scope.Device || value == Scope.CrossDevice) Right(origin + platform.monotonicNanos)
        else if (value == Scope.Workgroup || value == Scope.Subgroup) Right(execution.issued)
        else Left(Fault.InvalidRequest(s"OpReadClockKHR has invalid scope $value"))
      ticks.map(time =>
        clock match {
          case Clock.Halves =>
            execution.values.update(dest, VMValue.Bits(resultType, Array(time & 0xffffffffL, time >>> 32)))
          case Clock.Scalar => execution.values.updateScalar(dest, resultType, time)
        }
      )
    }
  }
}
