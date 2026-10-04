package net.kurobako.gemstone.benchmarks

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.amdgpu.*
import net.kurobako.gemstone.isa.ptx.v9_3.*
import net.kurobako.gemstone.isa.sass.*

object PlatformBenchmarks {
  private val entries: NonEmpty[Vec[(BenchmarkName, Array[String] => BenchmarkSample)]] = Vec.nonEmpty(
    (BenchmarkName("ptx-execution"), PTXExecutionBenchmark.run(_)),
    (BenchmarkName("ptx-calls"), PTXCallBenchmark.run(_)),
    (BenchmarkName("nvidia-control-flow"), NVIDIAControlFlowBenchmark.run(_)),
    (BenchmarkName("cuda-cloverleaf-flux"), CUDACloverleafFluxBenchmark.run(_)),
    (BenchmarkName("ptx-program-lifecycle"), PTXProgramLifecycleBenchmark.run(_)),
    (BenchmarkName("ptx-cache"), PTXCacheBenchmark.run(_)),
    (BenchmarkName("ptx-register-index"), PTXRegisterIndexBenchmark.run(_)),
    (BenchmarkName("spirv-execution"), SPIRVExecutionBenchmark.run(_)),
    (BenchmarkName("opencl-cts-async-copy"), OpenCLCTSCopyBenchmark.run(_)),
    (BenchmarkName("amdgpu-execution"), AMDGPUExecutionBenchmark.run(_)),
    (BenchmarkName("amdgpu-program-lifecycle"), AMDGPUProgramLifecycleBenchmark.run(_)),
    (BenchmarkName("hip-cloverleaf-flux"), HIPCloverleafFluxBenchmark.run(_)),
    (BenchmarkName("hsa-vector-copy"), HSAVectorCopyBenchmark.run(_)),
    (BenchmarkName("amdgpu-alu"), AMDGPUALUExecutionBenchmark.run(_)),
    (BenchmarkName("amdgpu-atomic"), AMDGPUAtomicBenchmark.run(_)),
    (BenchmarkName("amdgpu-vector-memory"), VectorMemoryBenchmark.run(_)),
    (BenchmarkName("amdgpu-dispatch-memory"), AMDGPUDispatchMemoryBenchmark.run(_)),
    (BenchmarkName("journal-inspection"), JournalInspectionBenchmark.run(_)),
    (BenchmarkName("session-memory"), SessionMemoryBenchmark.run(_)),
    (BenchmarkName("rpc-roundtrip"), RPCRoundTripBenchmark.run(_))
  )

  val names: NonEmpty[Vec[BenchmarkName]] = Vec.nonEmpty(entries.head._1, entries.tail.map(_._1)*)
  private val runners                     = entries.toVector.toMap

  def run(invocation: BenchmarkInvocation): BenchmarkSample =
    runners.getOrElse(invocation.name, throw IllegalArgumentException(s"unknown benchmark: ${invocation.name}"))(
      invocation.arguments.toArray
    )
}
