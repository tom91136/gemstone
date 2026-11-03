package net.kurobako.gemstone.benchmarks

import cats.syntax.all.*
import com.monovore.decline.Command
import net.kurobako.gemstone.benchmarks.BenchmarkArguments.*
import net.kurobako.gemstone.isa.amdgpu.*
import net.kurobako.gemstone.isa.ptx.*
import net.kurobako.gemstone.isa.sass.*

object PlatformBenchmarks {
  enum Platform derives CanEqual {
    case Nvidia, Amdgpu, Portable
  }

  /** `platform` selects the default profile architecture. A `None` platform marks a case that runs no VM. */
  case class Entry(name: String, platform: Option[Platform], benchmark: Case)

  val cases: Vector[Entry] = Vector(
    Entry("ptx-vector-add", Some(Platform.Nvidia), PTXVectorAddBenchmark.benchmark),
    Entry("ptx-integer-alu", Some(Platform.Nvidia), PTXIntegerALUBenchmark.benchmark),
    Entry("ptx-calls", Some(Platform.Nvidia), PTXCallBenchmark.benchmark),
    Entry("ptx-tensor", Some(Platform.Nvidia), PTXTensorBenchmark.benchmark),
    Entry("nvidia-control-flow", Some(Platform.Nvidia), NVIDIAControlFlowBenchmark.benchmark),
    Entry("cuda-cloverleaf-flux", Some(Platform.Nvidia), CUDACloverleafFluxBenchmark.benchmark),
    Entry("ptx-program-lifecycle", Some(Platform.Nvidia), PTXProgramLifecycleBenchmark.benchmark),
    Entry("ptx-cache", Some(Platform.Nvidia), PTXCacheBenchmark.benchmark),
    Entry("ptx-bulk-copy", Some(Platform.Nvidia), PTXBulkCopyBenchmark.benchmark),
    Entry("spirv-execution", Some(Platform.Portable), SPIRVExecutionBenchmark.benchmark),
    Entry("spirv-cooperative-matrix", Some(Platform.Portable), SPIRVCooperativeMatrixBenchmark.benchmark),
    Entry("spirv-image-sampling", Some(Platform.Portable), SPIRVImageSamplingBenchmark.benchmark),
    Entry("opencl-cts-async-copy", Some(Platform.Portable), OpenCLCTSCopyBenchmark.benchmark),
    Entry("amdgpu-vector-copy", Some(Platform.Amdgpu), AMDGPUVectorCopyBenchmark.benchmark),
    Entry("amdgpu-dwordx4-copy", Some(Platform.Amdgpu), AMDGPUDwordx4CopyBenchmark.benchmark),
    Entry("amdgpu-alu", Some(Platform.Amdgpu), AMDGPUALUBenchmark.benchmark),
    Entry("amdgpu-atomic", Some(Platform.Amdgpu), AMDGPUAtomicBenchmark.benchmark),
    Entry("amdgpu-dispatch-memory", Some(Platform.Amdgpu), AMDGPUDispatchMemoryBenchmark.benchmark),
    Entry("amdgpu-program-lifecycle", Some(Platform.Amdgpu), AMDGPUProgramLifecycleBenchmark.benchmark),
    Entry("hip-cloverleaf-flux", Some(Platform.Amdgpu), HIPCloverleafFluxBenchmark.benchmark),
    Entry("journal-inspection", None, JournalInspectionBenchmark.benchmark),
    Entry("session-memory", None, SessionMemoryBenchmark.benchmark),
    Entry("rpc-roundtrip", None, RPCRoundTripBenchmark.benchmark)
  )

  def find(name: String): Either[String, Entry] =
    cases.find(_.name == name).toRight(s"unknown benchmark: $name")

  def parse(name: String, arguments: Seq[String]): Either[String, LaunchControl => BenchmarkSample] =
    find(name).flatMap(entry =>
      Command(name, s"Options for the $name benchmark")(entry.benchmark).parse(arguments).leftMap(_.toString)
    )
}
