package net.kurobako.gemstone.isa.spirv

import fixtures.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.testing.either.*
import net.kurobako.gemstone.vm.*

class TensorArmSuite extends munit.FunSuite {

  private val constants: String = Seq(0L, 1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 77L, 4294967295L)
    .map(value => s"%u$value = OpConstant %uint $value")
    .mkString("\n")

  private def module(rank: Int, body: String): Module = spv"""
    OpCapability Shader
    OpCapability TensorsARM
    OpExtension "SPV_ARM_tensors"
    OpMemoryModel Logical GLSL450
    OpEntryPoint GLCompute %main "main" %tensor %out
    OpExecutionMode %main LocalSize 1 1 1
    OpDecorate %tensor DescriptorSet 0
    OpDecorate %tensor Binding 0
    OpDecorate %out DescriptorSet 0
    OpDecorate %out Binding 1
    OpDecorate %words ArrayStride 4
    OpDecorate %block Block
    OpMemberDecorate %block 0 Offset 0
    %void = OpTypeVoid
    %fn = OpTypeFunction %void
    %int = OpTypeInt 32 1
    %uint = OpTypeInt 32 0
    %int_0 = OpConstant %int 0
    %int_1 = OpConstant %int 1
    $constants
    %rank = OpConstant %uint $rank
    %coords = OpTypeArray %uint %rank
    %results2 = OpTypeArray %uint %u2
    %results3 = OpTypeArray %uint %u3
    %results4 = OpTypeArray %uint %u4
    %tensor_t = OpTypeTensorARM %uint %rank
    %tensor_ptr = OpTypePointer UniformConstant %tensor_t
    %tensor = OpVariable %tensor_ptr UniformConstant
    %words = OpTypeRuntimeArray %uint
    %block = OpTypeStruct %words
    %block_ptr = OpTypePointer StorageBuffer %block
    %word_ptr = OpTypePointer StorageBuffer %uint
    %out = OpVariable %block_ptr StorageBuffer
    %main = OpFunction %void None %fn
    %entry = OpLabel
    %t = OpLoad %tensor_t %tensor
    $body
    OpReturn
    OpFunctionEnd
  """

  /** Stores each id of `values` at the output word of the same index. */
  private def stores(values: String*): String =
    values.zipWithIndex.map((value, index) =>
      s"%at$index = OpAccessChain %word_ptr %out %int_0 %u$index\nOpStore %at$index $value"
    ).mkString("\n")

  /** Extracts the `count` elements of the array `%name` into `%name_0` onwards. */
  private def elements(name: String, count: Int): String = (0 until count).map(index =>
    s"%${name}_$index = OpCompositeExtract %uint %$name $index"
  ).mkString("\n")

  private def execute(
      rank: Int,
      body: String,
      memory: RegionMemory,
      descriptor: AllocationId,
      relocation: KernelTask.PointerRelocation,
      out: AllocationId
  ): Either[Fault, Unit] = {
    val size = (8 * (1 + 2 * rank)).toLong.refine[Long > 0].value
    run(
      module(rank, body),
      "main",
      memory,
      Vec(buffer(descriptor, range = Some(size)), buffer(out, range = Some(32L))),
      relocations = Vec(relocation)
    )
  }

  test("rank 1 scalar and array reads return the elements from the coordinate along the innermost dimension") {
    val memory     = RegionMemory()
    val data       = memory.allocate(20L)
    val descriptor = memory.allocate(24L)
    val out        = memory.allocate(32L)
    writeWords(memory, data, 0, 10L, 11L, 12L, 13L, 14L)
    val relocation = writeTensor(memory, descriptor, 0, Seq(5L), Seq(4L), data)
    val body       = Seq(
      "%c1 = OpCompositeConstruct %coords %u1",
      "%run = OpTensorReadARM %results3 %t %c1",
      "%c4 = OpCompositeConstruct %coords %u4",
      "%one = OpTensorReadARM %uint %t %c4",
      elements("run", 3),
      stores("%run_0", "%run_1", "%run_2", "%one")
    ).mkString("\n")
    assertEquals(execute(1, body, memory, descriptor, relocation, out), Right(()))
    assertEquals(words(memory, out, offset = 0, count = 4), Vec(11L, 12L, 13L, 14L))
  }

  test("rank 2 reads follow the byte strides of the descriptor rather than a packed layout") {
    val memory     = RegionMemory()
    val data       = memory.allocate(32L)
    val descriptor = memory.allocate(40L)
    val out        = memory.allocate(32L)
    writeWords(memory, data, 0, 100L, 101L, 102L, 999L, 103L, 104L, 105L, 999L)
    val relocation = writeTensor(memory, descriptor, 0, Seq(2L, 3L), Seq(16L, 4L), data)
    val body       = Seq(
      "%c1 = OpCompositeConstruct %coords %u1 %u0",
      "%run = OpTensorReadARM %results3 %t %c1",
      "%c2 = OpCompositeConstruct %coords %u0 %u2",
      "%one = OpTensorReadARM %uint %t %c2",
      elements("run", 3),
      stores("%run_0", "%run_1", "%run_2", "%one")
    ).mkString("\n")
    assertEquals(execute(2, body, memory, descriptor, relocation, out), Right(()))
    assertEquals(words(memory, out, offset = 0, count = 4), Vec(103L, 104L, 105L, 102L))
  }

  test("rank 3 reads address the plane, row and column of the coordinate") {
    val memory     = RegionMemory()
    val data       = memory.allocate(64L)
    val descriptor = memory.allocate(56L)
    val out        = memory.allocate(32L)
    writeWords(
      memory,
      data,
      0,
      (for {
        plane  <- 0 until 2
        row    <- 0 until 2
        column <- 0 until 4
      } yield if (column < 3) 100L * plane + 10L * row + column else 999L)*
    )
    val relocation = writeTensor(memory, descriptor, 0, Seq(2L, 2L, 3L), Seq(32L, 16L, 4L), data)
    val body       = Seq(
      "%c1 = OpCompositeConstruct %coords %u1 %u1 %u0",
      "%run = OpTensorReadARM %results2 %t %c1",
      "%c2 = OpCompositeConstruct %coords %u0 %u1 %u2",
      "%one = OpTensorReadARM %uint %t %c2",
      elements("run", 2),
      stores("%run_0", "%run_1", "%one")
    ).mkString("\n")
    assertEquals(execute(3, body, memory, descriptor, relocation, out), Right(()))
    assertEquals(words(memory, out, offset = 0, count = 3), Vec(110L, 111L, 12L))
  }

  test("scalar and array writes store consecutive elements along the innermost dimension") {
    val memory     = RegionMemory()
    val data       = memory.allocate(32L)
    val descriptor = memory.allocate(40L)
    val out        = memory.allocate(32L)
    val relocation = writeTensor(memory, descriptor, 0, Seq(2L, 3L), Seq(16L, 4L), data)
    val body       = Seq(
      "%c1 = OpCompositeConstruct %coords %u0 %u0",
      "OpTensorWriteARM %t %c1 %u9",
      "%c2 = OpCompositeConstruct %coords %u1 %u1",
      "%obj = OpCompositeConstruct %results2 %u7 %u8",
      "OpTensorWriteARM %t %c2 %obj"
    ).mkString("\n")
    assertEquals(execute(2, body, memory, descriptor, relocation, out), Right(()))
    assertEquals(words(memory, data, offset = 0, count = 8), Vec(9L, 0L, 0L, 0L, 0L, 7L, 8L, 0L))
  }

  test("out-of-bounds reads return the out-of-bounds value for each element past the shape") {
    val memory     = RegionMemory()
    val data       = memory.allocate(24L)
    val descriptor = memory.allocate(24L)
    val out        = memory.allocate(32L)
    writeWords(memory, data, 0, 10L, 11L, 12L, 13L, 14L, 15L)
    val relocation = writeTensor(memory, descriptor, 0, Seq(4L), Seq(4L), data)
    val body       = Seq(
      "%c = OpCompositeConstruct %coords %u2",
      "%run = OpTensorReadARM %results4 %t %c OutOfBoundsValueARM %u77",
      elements("run", 4),
      stores("%run_0", "%run_1", "%run_2", "%run_3")
    ).mkString("\n")
    assertEquals(execute(1, body, memory, descriptor, relocation, out), Right(()))
    assertEquals(words(memory, out, offset = 0, count = 4), Vec(12L, 13L, 77L, 77L))
  }

  test("out-of-bounds reads without the out-of-bounds value return zero") {
    val memory     = RegionMemory()
    val data       = memory.allocate(24L)
    val descriptor = memory.allocate(24L)
    val out        = memory.allocate(32L)
    writeWords(memory, data, 0, 10L, 11L, 12L, 13L, 14L, 15L)
    val relocation = writeTensor(memory, descriptor, 0, Seq(4L), Seq(4L), data)
    val body       = Seq(
      "%c = OpCompositeConstruct %coords %u2",
      "%run = OpTensorReadARM %results4 %t %c",
      elements("run", 4),
      stores("%run_0", "%run_1", "%run_2", "%run_3")
    ).mkString("\n")
    assertEquals(execute(1, body, memory, descriptor, relocation, out), Right(()))
    assertEquals(words(memory, out, offset = 0, count = 4), Vec(12L, 13L, 0L, 0L))
  }

  test("out-of-bounds reads check every dimension, and a negative coordinate lies outside") {
    val memory     = RegionMemory()
    val data       = memory.allocate(32L)
    val descriptor = memory.allocate(40L)
    val out        = memory.allocate(32L)
    writeWords(memory, data, 0, 100L, 101L, 102L, 999L, 103L, 104L, 105L, 999L)
    val relocation = writeTensor(memory, descriptor, 0, Seq(2L, 3L), Seq(16L, 4L), data)
    val body       = Seq(
      "%c1 = OpCompositeConstruct %coords %u0 %u2",
      "%run = OpTensorReadARM %results3 %t %c1 OutOfBoundsValueARM %u77",
      "%c2 = OpCompositeConstruct %coords %u2 %u0",
      "%row = OpTensorReadARM %uint %t %c2 OutOfBoundsValueARM %u77",
      "%c3 = OpCompositeConstruct %coords %u4294967295 %u0",
      "%negative = OpTensorReadARM %uint %t %c3 OutOfBoundsValueARM %u77",
      elements("run", 3),
      stores("%run_0", "%run_1", "%run_2", "%row", "%negative")
    ).mkString("\n")
    assertEquals(execute(2, body, memory, descriptor, relocation, out), Right(()))
    assertEquals(words(memory, out, offset = 0, count = 5), Vec(102L, 77L, 77L, 77L, 77L))
  }

  test("out-of-bounds writes are dropped and leave the words past the shape unchanged") {
    val memory     = RegionMemory()
    val data       = memory.allocate(24L)
    val descriptor = memory.allocate(24L)
    val out        = memory.allocate(32L)
    writeWords(memory, data, 0, 10L, 11L, 12L, 13L, 14L, 15L)
    val relocation = writeTensor(memory, descriptor, 0, Seq(4L), Seq(4L), data)
    val body       = Seq(
      "%c = OpCompositeConstruct %coords %u2",
      "%obj = OpCompositeConstruct %results3 %u7 %u8 %u9",
      "OpTensorWriteARM %t %c %obj"
    ).mkString("\n")
    assertEquals(execute(1, body, memory, descriptor, relocation, out), Right(()))
    assertEquals(words(memory, data, offset = 0, count = 6), Vec(10L, 11L, 7L, 8L, 14L, 15L))
  }

  test("size queries return the extent of the tensor along each dimension") {
    val memory     = RegionMemory()
    val data       = memory.allocate(4L)
    val descriptor = memory.allocate(56L)
    val out        = memory.allocate(32L)
    val relocation = writeTensor(memory, descriptor, 0, Seq(2L, 3L, 4L), Seq(48L, 16L, 4L), data)
    val body       = Seq(
      "%size0 = OpTensorQuerySizeARM %uint %t %u0",
      "%size1 = OpTensorQuerySizeARM %uint %t %u1",
      "%size2 = OpTensorQuerySizeARM %uint %t %u2",
      stores("%size0", "%size1", "%size2")
    ).mkString("\n")
    assertEquals(execute(3, body, memory, descriptor, relocation, out), Right(()))
    assertEquals(words(memory, out, offset = 0, count = 3), Vec(2L, 3L, 4L))
  }

  test("a read that makes an element visible faults as unsupported") {
    val memory     = RegionMemory()
    val data       = memory.allocate(16L)
    val descriptor = memory.allocate(24L)
    val out        = memory.allocate(32L)
    val relocation = writeTensor(memory, descriptor, 0, Seq(4L), Seq(4L), data)
    val body       = Seq(
      "%c = OpCompositeConstruct %coords %u1",
      "%run = OpTensorReadARM %uint %t %c NonPrivateElementARM|MakeElementVisibleARM %int_1"
    ).mkString("\n")
    execute(1, body, memory, descriptor, relocation, out) match {
      case Left(Fault.Unsupported(message)) => assert(message.contains("MakeElement"))
      case other                            => fail(s"unexpected $other")
    }
  }

  test("a write that makes an element available faults as unsupported") {
    val memory     = RegionMemory()
    val data       = memory.allocate(16L)
    val descriptor = memory.allocate(24L)
    val out        = memory.allocate(32L)
    val relocation = writeTensor(memory, descriptor, 0, Seq(4L), Seq(4L), data)
    val body       = Seq(
      "%c = OpCompositeConstruct %coords %u1",
      "OpTensorWriteARM %t %c %u9 NonPrivateElementARM|MakeElementAvailableARM %int_1"
    ).mkString("\n")
    execute(1, body, memory, descriptor, relocation, out) match {
      case Left(Fault.Unsupported(message)) => assert(message.contains("MakeElement"))
      case other                            => fail(s"unexpected $other")
    }
  }

  test("nontemporal writes and non-private reads act as plain accesses") {
    val memory     = RegionMemory()
    val data       = memory.allocate(16L)
    val descriptor = memory.allocate(24L)
    val out        = memory.allocate(32L)
    writeWords(memory, data, 0, 10L, 11L, 12L, 13L)
    val relocation = writeTensor(memory, descriptor, 0, Seq(4L), Seq(4L), data)
    val body       = Seq(
      "%c = OpCompositeConstruct %coords %u3",
      "OpTensorWriteARM %t %c %u9 NontemporalARM",
      "%run = OpTensorReadARM %uint %t %c NonPrivateElementARM",
      stores("%run")
    ).mkString("\n")
    assertEquals(execute(1, body, memory, descriptor, relocation, out), Right(()))
    assertEquals(words(memory, out, offset = 0, count = 1), Vec(9L))
    assertEquals(words(memory, data, offset = 0, count = 4), Vec(10L, 11L, 12L, 9L))
  }
}
