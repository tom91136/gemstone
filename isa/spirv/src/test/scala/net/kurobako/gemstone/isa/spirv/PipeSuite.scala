package net.kurobako.gemstone.isa.spirv

import fixtures.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.vm.*

class PipeSuite extends munit.FunSuite {
  private val header =
    s"""OpCapability Pipes
       |OpCapability Groups
       |OpCapability GenericPointer
       |$openCL""".stripMargin

  private val types =
    """%void = OpTypeVoid
      |%bool = OpTypeBool
      |%uint = OpTypeInt 32 0
      |%ulong = OpTypeInt 64 0
      |%writer = OpTypePipe WriteOnly
      |%reader = OpTypePipe ReadOnly
      |%reservation = OpTypeReserveId
      |%uint_ptr = OpTypePointer CrossWorkgroup %uint
      |%private_ptr = OpTypePointer Function %uint
      |%generic_ptr = OpTypePointer Generic %uint
      |%v3ulong = OpTypeVector %ulong 3
      |%v3ulong_ptr = OpTypePointer Input %v3ulong
      |%uint_0 = OpConstant %uint 0
      |%uint_1 = OpConstant %uint 1
      |%uint_2 = OpConstant %uint 2
      |%uint_4 = OpConstant %uint 4
      |%workgroup = OpConstant %uint 2
      |%global_ids = OpVariable %v3ulong_ptr Input
      |%local_ids = OpVariable %v3ulong_ptr Input
      |""".stripMargin

  private case class Pipe(region: AllocationId, bytes: Int)

  private def pipe(
      memory: RegionMemory,
      capacity: Int,
      packetBytes: Int = 4,
      read: Long = 0L,
      write: Long = 0L
  ): Pipe = {
    val bytes  = PipeLayout.Packets + capacity * packetBytes
    val region = memory.allocate(bytes.toLong.unsafe[Long > 0])
    writeWords(memory, region, PipeLayout.ReadIndex, read, write, capacity.toLong, packetBytes.toLong)
    Pipe(region, bytes)
  }

  private def argument(pipe: Pipe): KernelTask.Argument =
    buffer(pipe.region, range = Some(pipe.bytes.toLong.unsafe[Long > 0]))

  private def launch(
      module: Module,
      entry: String,
      memory: RegionMemory,
      arguments: Vec[KernelTask.Argument],
      local: Int > 0,
      groups: Int > 0
  )
      : Either[Fault, Unit] =
    load(module).flatMap(execute(
      _,
      task(entry.unsafe[NonEmpty[String]], arguments, testDimensions(local), testDimensions(groups)),
      memory
    ))

  private def ring(memory: RegionMemory, pipe: Pipe, count: Int): Vec[Long] =
    words(memory, pipe.region, PipeLayout.Packets, count)

  test("write_pipe appends one packet per invocation until the pipe is full") {
    val module = spv"""
      $header
      OpEntryPoint Kernel %main "producer" %global_ids
      OpDecorate %global_ids BuiltIn GlobalInvocationId
      $types
      %fn = OpTypeFunction %void %writer %uint_ptr
      %main = OpFunction %void None %fn
      %out = OpFunctionParameter %writer
      %status = OpFunctionParameter %uint_ptr
      %entry = OpLabel
      %packet = OpVariable %private_ptr Function
      %ids = OpLoad %v3ulong %global_ids
      %id = OpCompositeExtract %ulong %ids 0
      %value = OpUConvert %uint %id
      OpStore %packet %value
      %generic = OpPtrCastToGeneric %generic_ptr %packet
      %written = OpWritePipe %uint %out %generic %uint_4 %uint_4
      %slot = OpPtrAccessChain %uint_ptr %status %id
      OpStore %slot %written
      OpReturn
      OpFunctionEnd
    """
    val memory   = RegionMemory()
    val target   = pipe(memory, 3)
    val statuses = memory.allocate(20L)
    assertEquals(launch(module, "producer", memory, Vec(argument(target), buffer(statuses)), 1, 5), Right(()))
    assertEquals(words(memory, statuses, 0, 5).map(_.toInt < 0), Vec(false, false, false, true, true))
    assertEquals(ring(memory, target, 3), Vec(0L, 1L, 2L))
    assertEquals(word(memory, target.region, PipeLayout.WriteIndex), 3L)
  }

  test("read_pipe consumes packets in order and fails once the pipe is empty") {
    val module = spv"""
      $header
      OpEntryPoint Kernel %main "consumer" %global_ids
      OpDecorate %global_ids BuiltIn GlobalInvocationId
      $types
      %fn = OpTypeFunction %void %reader %uint_ptr %uint_ptr
      %main = OpFunction %void None %fn
      %in = OpFunctionParameter %reader
      %values = OpFunctionParameter %uint_ptr
      %status = OpFunctionParameter %uint_ptr
      %entry = OpLabel
      %packet = OpVariable %private_ptr Function %uint_0
      %ids = OpLoad %v3ulong %global_ids
      %id = OpCompositeExtract %ulong %ids 0
      %generic = OpPtrCastToGeneric %generic_ptr %packet
      %read = OpReadPipe %uint %in %generic %uint_4 %uint_4
      %value = OpLoad %uint %packet
      %value_slot = OpPtrAccessChain %uint_ptr %values %id
      OpStore %value_slot %value
      %status_slot = OpPtrAccessChain %uint_ptr %status %id
      OpStore %status_slot %read
      OpReturn
      OpFunctionEnd
    """
    val memory   = RegionMemory()
    val source   = pipe(memory, 4, read = 1L, write = 3L)
    val values   = memory.allocate(12L)
    val statuses = memory.allocate(12L)
    writeWords(memory, source.region, PipeLayout.Packets, 5L, 7L, 9L, 11L)
    assertEquals(
      launch(module, "consumer", memory, Vec(argument(source), buffer(values), buffer(statuses)), 1, 3),
      Right(())
    )
    assertEquals(words(memory, values, 0, 2), Vec(7L, 9L))
    assertEquals(words(memory, statuses, 0, 3).map(_.toInt < 0), Vec(false, false, true))
    assertEquals(word(memory, source.region, PipeLayout.ReadIndex), 3L)
  }

  test("reservations write packets at their reserved slots and reject reservations that do not fit") {
    val module = spv"""
      $header
      OpEntryPoint Kernel %main "reserve"
      $types
      %fn = OpTypeFunction %void %writer %uint_ptr
      %uint_10 = OpConstant %uint 10
      %uint_11 = OpConstant %uint 11
      %main = OpFunction %void None %fn
      %out = OpFunctionParameter %writer
      %results = OpFunctionParameter %uint_ptr
      %entry = OpLabel
      %packet = OpVariable %private_ptr Function
      %generic = OpPtrCastToGeneric %generic_ptr %packet
      %first = OpReserveWritePipePackets %reservation %out %uint_2 %uint_4 %uint_4
      %first_valid = OpIsValidReserveId %bool %first
      OpStore %packet %uint_10
      %at_1 = OpReservedWritePipe %uint %out %first %uint_1 %generic %uint_4 %uint_4
      OpStore %packet %uint_11
      %at_0 = OpReservedWritePipe %uint %out %first %uint_0 %generic %uint_4 %uint_4
      %at_2 = OpReservedWritePipe %uint %out %first %uint_2 %generic %uint_4 %uint_4
      OpCommitWritePipe %out %first %uint_4 %uint_4
      %second = OpReserveWritePipePackets %reservation %out %uint_2 %uint_4 %uint_4
      %second_valid = OpIsValidReserveId %bool %second
      %first_flag = OpSelect %uint %first_valid %uint_1 %uint_0
      %second_flag = OpSelect %uint %second_valid %uint_1 %uint_0
      OpStore %results %first_flag
      %r1 = OpPtrAccessChain %uint_ptr %results %uint_1
      OpStore %r1 %second_flag
      %r2 = OpPtrAccessChain %uint_ptr %results %uint_2
      OpStore %r2 %at_1
      %r3 = OpPtrAccessChain %uint_ptr %results %uint_4
      OpStore %r3 %at_2
      OpReturn
      OpFunctionEnd
    """
    val memory  = RegionMemory()
    val target  = pipe(memory, 3)
    val results = memory.allocate(20L)
    assertEquals(launch(module, "reserve", memory, Vec(argument(target), buffer(results)), 1, 1), Right(()))
    assertEquals(words(memory, results, 0, 3), Vec(1L, 0L, 0L))
    assert(word(memory, results, 16).toInt < 0)
    assertEquals(ring(memory, target, 2), Vec(11L, 10L))
    assertEquals(word(memory, target.region, PipeLayout.WriteIndex), 2L)
  }

  test("packet queries count across index wraparound") {
    val module = spv"""
      $header
      OpEntryPoint Kernel %main "query"
      $types
      %fn = OpTypeFunction %void %reader %uint_ptr
      %main = OpFunction %void None %fn
      %in = OpFunctionParameter %reader
      %out = OpFunctionParameter %uint_ptr
      %entry = OpLabel
      %count = OpGetNumPipePackets %uint %in %uint_4 %uint_4
      %capacity = OpGetMaxPipePackets %uint %in %uint_4 %uint_4
      OpStore %out %count
      %out1 = OpPtrAccessChain %uint_ptr %out %uint_1
      OpStore %out1 %capacity
      OpReturn
      OpFunctionEnd
    """
    val memory = RegionMemory()
    val source = pipe(memory, 4, read = 0xffffffffL, write = 1L)
    val out    = memory.allocate(8L)
    assertEquals(launch(module, "query", memory, Vec(argument(source), buffer(out)), 1, 1), Right(()))
    assertEquals(words(memory, out, 0, 2), Vec(2L, 4L))
  }

  test("work-group reservations give every invocation the same reservation") {
    val module = spv"""
      $header
      OpEntryPoint Kernel %main "group" %local_ids
      OpDecorate %local_ids BuiltIn LocalInvocationId
      $types
      %fn = OpTypeFunction %void %writer
      %main = OpFunction %void None %fn
      %out = OpFunctionParameter %writer
      %entry = OpLabel
      %packet = OpVariable %private_ptr Function
      %ids = OpLoad %v3ulong %local_ids
      %id = OpCompositeExtract %ulong %ids 0
      %local = OpUConvert %uint %id
      %reserved = OpGroupReserveWritePipePackets %reservation %workgroup %out %uint_4 %uint_4 %uint_4
      %valid = OpIsValidReserveId %bool %reserved
      OpSelectionMerge %done None
      OpBranchConditional %valid %write %done
      %write = OpLabel
      OpStore %packet %local
      %generic = OpPtrCastToGeneric %generic_ptr %packet
      %status = OpReservedWritePipe %uint %out %reserved %local %generic %uint_4 %uint_4
      OpGroupCommitWritePipe %workgroup %out %reserved %uint_4 %uint_4
      OpBranch %done
      %done = OpLabel
      OpReturn
      OpFunctionEnd
    """
    val memory = RegionMemory()
    val target = pipe(memory, 6, read = 4L, write = 4L)
    assertEquals(launch(module, "group", memory, Vec(argument(target)), 4, 1), Right(()))
    assertEquals(words(memory, target.region, PipeLayout.Packets, 6), Vec(2L, 3L, 0L, 0L, 0L, 1L))
    assertEquals(word(memory, target.region, PipeLayout.WriteIndex), 8L)
  }

  test("packets wider than a machine word round-trip through the ring") {
    val module = spv"""
      $header
      OpEntryPoint Kernel %main "wide"
      $types
      %uint_3 = OpConstant %uint 3
      %uint_12 = OpConstant %uint 12
      %triple = OpTypeArray %uint %uint_3
      %triple_ptr = OpTypePointer Function %triple
      %generic_triple = OpTypePointer Generic %triple
      %fn = OpTypeFunction %void %writer %reader %uint_ptr
      %uint_7 = OpConstant %uint 7
      %uint_8 = OpConstant %uint 8
      %uint_9 = OpConstant %uint 9
      %values = OpConstantComposite %triple %uint_7 %uint_8 %uint_9
      %main = OpFunction %void None %fn
      %out = OpFunctionParameter %writer
      %in = OpFunctionParameter %reader
      %result = OpFunctionParameter %uint_ptr
      %entry = OpLabel
      %source = OpVariable %triple_ptr Function %values
      %target = OpVariable %triple_ptr Function
      %from = OpPtrCastToGeneric %generic_triple %source
      %to = OpPtrCastToGeneric %generic_triple %target
      %written = OpWritePipe %uint %out %from %uint_12 %uint_4
      %read = OpReadPipe %uint %in %to %uint_12 %uint_4
      %copy = OpLoad %triple %target
      %last = OpCompositeExtract %uint %copy 2
      OpStore %result %last
      OpReturn
      OpFunctionEnd
    """
    val memory = RegionMemory()
    val wide   = pipe(memory, 2, packetBytes = 12)
    val result = memory.allocate(4L)
    assertEquals(launch(module, "wide", memory, Vec(argument(wide), argument(wide), buffer(result)), 1, 1), Right(()))
    assertEquals(words(memory, wide.region, PipeLayout.Packets, 3), Vec(7L, 8L, 9L))
    assertEquals(word(memory, result, 0), 9L)
  }
}
