package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.benchmarks.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.vm.*

object SequentialDispatch {

  /** The kernel of `code` on `target` with `kernargSize` argument bytes, no group or private segment, and the register
    * and code properties the kernels of these benchmarks share.
    */
  def compile(name: NonEmpty[String], target: Target, kernargSize: Int >= 0, code: Vec[Byte]): VMIR.Kernel =
    VMCompiler().compile(CodeObjectKernel(
      name,
      target,
      kernargSize,
      0,
      0,
      0x80,
      0x08,
      code,
      code.nonNegativeSize
    )).fold(error => throw IllegalStateException(error.message), identity)

  /** Measure `iterations` single-threaded dispatches of `kernel`, each of which must succeed. */
  def repeat(iterations: Int > 0, kernel: VMIR.Kernel, task: KernelTask, memory: AddressSpace): BenchmarkSample = {
    val launch = KernelLaunch(task, TaskContext(memory, TaskControl(), _ => Right(())), Workgroups.Sequential)
    Measurement.repeatSucceeding(iterations)(VMInterpreter.execute(kernel, launch, Vec.empty))
  }
}
