package net.kurobako.gemstone.benchmarks

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.amdgpu.*
import net.kurobako.gemstone.isa.ptx.v9_3.*

object PlatformBenchmarks {
  val names: NonEmpty[Vec[BenchmarkName]] = Vec.nonEmpty(
    BenchmarkName("ptx-execution"),
    BenchmarkName("cuda-cloverleaf-flux"),
    BenchmarkName("ptx-program-lifecycle"),
    BenchmarkName("ptx-register-index"),
    BenchmarkName("spirv-execution"),
    BenchmarkName("opencl-cts-async-copy"),
    BenchmarkName("amdgpu-execution"),
    BenchmarkName("hip-cloverleaf-flux"),
    BenchmarkName("hsa-vector-copy"),
    BenchmarkName("amdgpu-alu"),
    BenchmarkName("amdgpu-atomic"),
    BenchmarkName("amdgpu-vector-memory"),
    BenchmarkName("journal-inspection")
  )

  def run(invocation: BenchmarkInvocation): Unit = invocation.name.raw match {
    case "ptx-execution"         => PTXExecutionBenchmark.run(invocation.arguments.toArray)
    case "cuda-cloverleaf-flux"  => CUDACloverleafFluxBenchmark.run(invocation.arguments.toArray)
    case "ptx-program-lifecycle" => PTXProgramLifecycleBenchmark.run(invocation.arguments.toArray)
    case "ptx-register-index"    => PTXRegisterIndexBenchmark.run(invocation.arguments.toArray)
    case "spirv-execution"       => SPIRVExecutionBenchmark.run(invocation.arguments.toArray)
    case "opencl-cts-async-copy" => OpenCLCTSCopyBenchmark.run(invocation.arguments.toArray)
    case "amdgpu-execution"      => AMDGPUExecutionBenchmark.run(invocation.arguments.toArray)
    case "hip-cloverleaf-flux"   => HIPCloverleafFluxBenchmark.run(invocation.arguments.toArray)
    case "hsa-vector-copy"       => HSAVectorCopyBenchmark.run(invocation.arguments.toArray)
    case "amdgpu-alu"            => AMDGPUALUExecutionBenchmark.run(invocation.arguments.toArray)
    case "amdgpu-atomic"         => AMDGPUAtomicBenchmark.run(invocation.arguments.toArray)
    case "amdgpu-vector-memory"  => VectorMemoryBenchmark.run(invocation.arguments.toArray)
    case "journal-inspection"    => JournalInspectionBenchmark.run(invocation.arguments.toArray)
  }
}
