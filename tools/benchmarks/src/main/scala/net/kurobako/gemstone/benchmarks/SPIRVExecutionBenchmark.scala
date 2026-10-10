package net.kurobako.gemstone.benchmarks

import cats.syntax.all.*
import net.kurobako.gemstone.benchmarks.BenchmarkArguments.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.constraint.auto.*
import net.kurobako.gemstone.isa.spirv.*
import net.kurobako.gemstone.vm.*
import scala.util.Using

/** Arithmetic or call-heavy dispatches representative of the SPIR-V interpreter's instruction loop. */
object SPIRVExecutionBenchmark {
  private enum Mode {
    case Arithmetic, Calls
  }

  val benchmark: Case = (
    choice("mode", "arithmetic" -> Mode.Arithmetic, "calls" -> Mode.Calls),
    iterations(20),
    positive("groups", "Workgroups per dispatch", 256),
    positive("instructions", "Integer additions or function calls per invocation", 64),
    workers
  ).mapN((mode, iterations, groups, instructions, workers) =>
    () => run(mode, iterations, groups, instructions, workers)
  )

  private def run(mode: Mode, iterations: Int > 0, groups: Int > 0, instructions: Int > 0, workers: Int > 0)
      : BenchmarkSample = {
    val memory = BenchmarkMemory(Math.multiplyExact(Math.multiplyExact(iterations.raw, groups.raw), 4))
    val output = memory.ids(0)
    val tasks  = Vec.tabulate(iterations.raw) { iteration =>
      KernelTask.validated(
        kernelName = "arithmetic",
        groupSize = LaunchGeometry.Dimensions.One,
        groupCount = LaunchGeometry.Dimensions(groups, 1, 1),
        arguments = Vec(KernelTask.Argument.Memory(output, (iteration.toLong * groups.raw * 4L).unsafe[Long >= 0]))
      ).fold(error => throw IllegalArgumentException(error), identity)
    }
    val sample = Using.resource(WorkerPool(workers, minGroups = 1L, minWorkItems = 1L)) { workgroups =>
      val program = ISA.encode(module(instructions, mode)).left.map(error => Fault.InvalidRequest(error.message))
        .flatMap(SPIRVBackend(Validation.Environment.OpenCLCompute, workgroups = workgroups).load(_))
        .fold(error => throw IllegalArgumentException(error.message), identity)
      val measurement = Measurement {
        tasks.foreach(task => Measurement.succeeded(program.execute(task, memory, TaskControl(), _ => Right(()))))
      }
      measurement.sample(iterations.toLong)
    }
    (0 until iterations.raw * groups.raw).foreach { invocation =>
      val actual   = memory.getInt(output, invocation * 4)
      val expected = invocation % groups.raw + instructions.raw
      if (actual != expected)
        throw IllegalStateException(s"SPIR-V result mismatch at invocation $invocation: $actual != $expected")
    }
    sample
  }

  private def module(instructions: Int > 0, mode: Mode): Module = {
    val calls = mode == Mode.Calls
    val chain = (1 to instructions.raw).map { index =>
      if (calls) s"%r$index = OpFunctionCall %uint %helper %r${index - 1}"
      else s"%r$index = OpIAdd %uint %r${index - 1} %one"
    }
    val helper =
      if (!calls) ""
      else """%helper = OpFunction %uint None %helper_type
             |%value = OpFunctionParameter %uint
             |%helper_entry = OpLabel
             |%sum = OpIAdd %uint %value %one
             |OpReturnValue %sum
             |OpFunctionEnd
             |""".stripMargin
    val source = s"""OpCapability Kernel
                    |OpCapability Int64
                    |OpCapability Addresses
                    |OpMemoryModel Physical64 OpenCL
                    |OpEntryPoint Kernel %main "arithmetic" %gid
                    |OpDecorate %gid BuiltIn GlobalInvocationId
                    |%void = OpTypeVoid
                    |%uint = OpTypeInt 32 0
                    |%ulong = OpTypeInt 64 0
                    |%v3ulong = OpTypeVector %ulong 3
                    |%input = OpTypePointer Input %v3ulong
                    |%global = OpTypePointer CrossWorkgroup %uint
                    |%kernel_type = OpTypeFunction %void %global
                    |%helper_type = OpTypeFunction %uint %uint
                    |%one = OpConstant %uint 1
                    |%gid = OpVariable %input Input
                    |%main = OpFunction %void None %kernel_type
                    |%output = OpFunctionParameter %global
                    |%entry = OpLabel
                    |%ids = OpLoad %v3ulong %gid
                    |%x = OpCompositeExtract %ulong %ids 0
                    |%r0 = OpUConvert %uint %x
                    |${chain.mkString("\n")}
                    |%pointer = OpPtrAccessChain %global %output %x
                    |OpStore %pointer %r${instructions.raw}
                    |OpReturn
                    |OpFunctionEnd
                    |$helper""".stripMargin
    asm.Parser.parse(source).fold(error => throw IllegalArgumentException(error.toString), identity)
  }
}
