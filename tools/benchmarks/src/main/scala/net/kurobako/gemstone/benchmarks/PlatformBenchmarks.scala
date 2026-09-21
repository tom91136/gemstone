package net.kurobako.gemstone.benchmarks

import net.kurobako.gemstone.isa.amdgpu.AMDGPUALUExecutionBenchmark
import net.kurobako.gemstone.isa.amdgpu.AMDGPUExecutionBenchmark
import net.kurobako.gemstone.isa.amdgpu.VectorMemoryBenchmark
import net.kurobako.gemstone.isa.ptx.v9_3.PTXExecutionBenchmark
import net.kurobako.gemstone.isa.ptx.v9_3.PTXProgramLifecycleBenchmark
import net.kurobako.gemstone.isa.ptx.v9_3.PTXRegisterIndexBenchmark
import net.kurobako.gemstone.server.execution.SPIRVExecutionBenchmark

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

object PlatformBenchmarks {
  val names: Vector[BenchmarkName] :| MinLength[1] = Vector(
    BenchmarkName("ptx-execution"),
    BenchmarkName("ptx-program-lifecycle"),
    BenchmarkName("ptx-register-index"),
    BenchmarkName("program-image-key"),
    BenchmarkName("spirv-execution"),
    BenchmarkName("amdgpu-execution"),
    BenchmarkName("amdgpu-alu"),
    BenchmarkName("amdgpu-vector-memory"),
    BenchmarkName("journal-inspection")
  ).refineUnsafe[MinLength[1]]

  def run(invocation: BenchmarkInvocation): Unit = invocation.name.value match {
    case "ptx-execution"         => PTXExecutionBenchmark.run(invocation.arguments.toArray)
    case "ptx-program-lifecycle" => PTXProgramLifecycleBenchmark.run(invocation.arguments.toArray)
    case "ptx-register-index"    => PTXRegisterIndexBenchmark.run(invocation.arguments.toArray)
    case "program-image-key"     => ProgramImageKeyBenchmark.run(invocation.arguments.toArray)
    case "spirv-execution"       => SPIRVExecutionBenchmark.run(invocation.arguments.toArray)
    case "amdgpu-execution"      => AMDGPUExecutionBenchmark.run(invocation.arguments.toArray)
    case "amdgpu-alu"            => AMDGPUALUExecutionBenchmark.run(invocation.arguments.toArray)
    case "amdgpu-vector-memory"  => VectorMemoryBenchmark.run(invocation.arguments.toArray)
    case "journal-inspection"    => JournalInspectionBenchmark.run(invocation.arguments.toArray)
  }
}
