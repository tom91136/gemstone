package net.kurobako.gemstone.benchmarks

import cats.syntax.all.*
import com.monovore.decline.Command
import net.kurobako.gemstone.benchmarks.BenchmarkArguments.*
import net.kurobako.gemstone.isa.amdgpu.*
import net.kurobako.gemstone.isa.ptx.*
import net.kurobako.gemstone.isa.sass.*

object PlatformBenchmarks {
  val cases: Vector[(String, Case)] = Vector(
    "ptx-vector-add"           -> PTXVectorAddBenchmark.benchmark,
    "ptx-calls"                -> PTXCallBenchmark.benchmark,
    "nvidia-control-flow"      -> NVIDIAControlFlowBenchmark.benchmark,
    "cuda-cloverleaf-flux"     -> CUDACloverleafFluxBenchmark.benchmark,
    "ptx-program-lifecycle"    -> PTXProgramLifecycleBenchmark.benchmark,
    "ptx-cache"                -> PTXCacheBenchmark.benchmark,
    "spirv-execution"          -> SPIRVExecutionBenchmark.benchmark,
    "opencl-cts-async-copy"    -> OpenCLCTSCopyBenchmark.benchmark,
    "amdgpu-vector-copy"       -> AMDGPUVectorCopyBenchmark.benchmark,
    "amdgpu-dwordx4-copy"      -> AMDGPUDwordx4CopyBenchmark.benchmark,
    "amdgpu-alu"               -> AMDGPUALUBenchmark.benchmark,
    "amdgpu-atomic"            -> AMDGPUAtomicBenchmark.benchmark,
    "amdgpu-dispatch-memory"   -> AMDGPUDispatchMemoryBenchmark.benchmark,
    "amdgpu-program-lifecycle" -> AMDGPUProgramLifecycleBenchmark.benchmark,
    "hip-cloverleaf-flux"      -> HIPCloverleafFluxBenchmark.benchmark,
    "journal-inspection"       -> JournalInspectionBenchmark.benchmark,
    "session-memory"           -> SessionMemoryBenchmark.benchmark,
    "rpc-roundtrip"            -> RPCRoundTripBenchmark.benchmark
  )

  def parse(name: String, arguments: Seq[String]): Either[String, () => BenchmarkSample] =
    cases.collectFirst { case (`name`, options) => options }.toRight(s"unknown benchmark: $name").flatMap(options =>
      Command(name, s"Options for the $name benchmark")(options).parse(arguments).leftMap(_.toString)
    )
}
