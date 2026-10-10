package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.benchmarks.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.vm.*

object SequentialDispatch {

  /** Measure `iterations` single-threaded dispatches of `kernel`, each of which must succeed. */
  def repeat(iterations: Int > 0, kernel: VMIR.Kernel, task: KernelTask, memory: AddressSpace): BenchmarkSample =
    Measurement.repeatSucceeding(iterations)(
      VMInterpreter.execute(kernel, task, memory, TaskControl(), Workgroups.Sequential, Vec.empty, _ => Right(()))
    )
}
