package net.kurobako.gemstone.benchmarks

import cats.syntax.all.*
import net.kurobako.gemstone.benchmarks.BenchmarkArguments.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.spirv.*
import net.kurobako.gemstone.vm.*

/** Chained cooperative matrix multiply-accumulates; each product adds 16 to every element of ones. */
object SPIRVCooperativeMatrixBenchmark {
  private val Rows    = 8
  private val Depth   = 16
  private val Columns = 16

  private case class Options(iterations: Int > 0, products: Int > 0)

  val benchmark: Case = (
    iterations(2),
    positive("products", "Cooperative matrix multiply-accumulates per invocation", 64)
  ).mapN(Options(_, _)).map(options => () => run(options))

  private def run(options: Options): BenchmarkSample = {
    val memory = BenchmarkMemory(Rows * Depth * 4, Depth * Columns * 4, Rows * Columns * 4)
    val left   = memory.ids(0)
    val right  = memory.ids(1)
    val output = memory.ids(2)
    val one    = java.lang.Float.floatToRawIntBits(1f)
    (0 until Rows * Depth).foreach(index => memory.putInt(left, index * 4, one))
    (0 until Depth * Columns).foreach(index => memory.putInt(right, index * 4, one))
    val task = KernelTask.validated(
      kernelName = "cooperative",
      groupSize = LaunchGeometry.Dimensions.One,
      groupCount = LaunchGeometry.Dimensions.One,
      arguments = Vec(
        KernelTask.Argument.Memory(left, 0L),
        KernelTask.Argument.Memory(right, 0L),
        KernelTask.Argument.Memory(output, 0L)
      )
    ).fold(error => throw IllegalArgumentException(error), identity)
    val program =
      ISA.encode(module(options.products)).left.map(error => Fault.InvalidRequest(error.message))
        .flatMap(SPIRVBackend(Validation.Environment.OpenCLCompute).load(_))
        .fold(error => throw IllegalArgumentException(error.message), identity)
    val sample = Measurement.repeatSucceeding(options.iterations)(
      program.execute(task, TaskContext(memory, TaskControl(), _ => Right(())))
    )
    val expected = java.lang.Float.floatToRawIntBits(16f * options.products.raw.toFloat)
    (0 until Rows * Columns).foreach { index =>
      val actual = memory.getInt(output, index * 4)
      if (actual != expected)
        throw IllegalStateException(s"cooperative matrix mismatch at element $index: $actual != $expected")
    }
    sample
  }

  private def module(products: Int > 0): Module = {
    val chain = (1 to products.raw).map { index =>
      s"%acc$index = OpCooperativeMatrixMulAddKHR %c_type %a %b %acc${index - 1}"
    }
    val source = s"""OpCapability Addresses
                    |OpCapability Kernel
                    |OpCapability Int64
                    |OpCapability CooperativeMatrixKHR
                    |OpExtension "SPV_KHR_cooperative_matrix"
                    |OpMemoryModel Physical64 OpenCL
                    |OpEntryPoint Kernel %main "cooperative"
                    |%void = OpTypeVoid
                    |%uint = OpTypeInt 32 0
                    |%float = OpTypeFloat 32
                    |%float_ptr = OpTypePointer CrossWorkgroup %float
                    |%fn = OpTypeFunction %void %float_ptr %float_ptr %float_ptr
                    |%scope = OpConstant %uint 3
                    |%rows = OpConstant %uint $Rows
                    |%depth = OpConstant %uint $Depth
                    |%columns = OpConstant %uint $Columns
                    |%use_a = OpConstant %uint 0
                    |%use_b = OpConstant %uint 1
                    |%use_c = OpConstant %uint 2
                    |%row_major = OpConstant %uint 0
                    |%stride_a = OpConstant %uint $Depth
                    |%stride_b = OpConstant %uint $Columns
                    |%stride_c = OpConstant %uint $Columns
                    |%zero = OpConstant %float 0
                    |%a_type = OpTypeCooperativeMatrixKHR %float %scope %rows %depth %use_a
                    |%b_type = OpTypeCooperativeMatrixKHR %float %scope %depth %columns %use_b
                    |%c_type = OpTypeCooperativeMatrixKHR %float %scope %rows %columns %use_c
                    |%main = OpFunction %void None %fn
                    |%a_in = OpFunctionParameter %float_ptr
                    |%b_in = OpFunctionParameter %float_ptr
                    |%c_out = OpFunctionParameter %float_ptr
                    |%entry = OpLabel
                    |%a = OpCooperativeMatrixLoadKHR %a_type %a_in %row_major %stride_a
                    |%b = OpCooperativeMatrixLoadKHR %b_type %b_in %row_major %stride_b
                    |%acc0 = OpCompositeConstruct %c_type %zero
                    |${chain.mkString("\n")}
                    |OpCooperativeMatrixStoreKHR %c_out %acc${products.raw} %row_major %stride_c
                    |OpReturn
                    |OpFunctionEnd
                    |""".stripMargin
    asm.Parser.parse(source).fold(error => throw IllegalArgumentException(error.toString), identity)
  }
}
