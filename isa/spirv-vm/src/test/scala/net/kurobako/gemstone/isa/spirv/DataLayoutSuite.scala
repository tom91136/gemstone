package net.kurobako.gemstone.isa.spirv

import fixtures.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.testing.either.*
import net.kurobako.gemstone.vm.*

class DataLayoutSuite extends munit.FunSuite {
  private def resultId(module: Module)(select: PartialFunction[Instruction, Operand])(using munit.Location): Int =
    module.instructions.collectFirst(select).map(_.words.head.raw).getOrElse(fail("no matching instruction"))

  test("byte-aligned nonstandard integer widths load") {
    val module = spv"""
      $openCL
      %uint24 = OpTypeInt 24 0
      %uint48 = OpTypeInt 48 0
    """
    assertEquals(load(module).map(_ => ()), Right(()))
  }

  test("explicit ArrayStride and member Offset decorations determine composite byte layout") {
    val module = spv"""
      $openCL
      OpEntryPoint Kernel %main "layout"
      OpDecorate %array ArrayStride 16
      OpMemberDecorate %record 1 Offset 16
      %void = OpTypeVoid
      %uint = OpTypeInt 32 0
      %two = OpConstant %uint 2
      %array = OpTypeArray %uint %two
      %record = OpTypeStruct %uint %array
      %record_ptr = OpTypePointer CrossWorkgroup %record
      %fn = OpTypeFunction %void %record_ptr
      %first = OpConstant %uint 11
      %second = OpConstant %uint 22
      %header = OpConstant %uint 5
      %one = OpConstant %uint 1
      %elements = OpConstantComposite %array %first %second
      %value = OpConstantComposite %record %header %elements
      %main = OpFunction %void None %fn
      %out = OpFunctionParameter %record_ptr
      %entry = OpLabel
      OpStore %out %value
      %loaded = OpLoad %record %out
      %next = OpPtrAccessChain %record_ptr %out %one
      OpStore %next %loaded
      OpReturn
      OpFunctionEnd
    """
    val memory = RegionMemory()
    val out    = memory.allocate(96L)
    assertEquals(run(module, "layout", memory, Vec(buffer(out))), Right(()))
    val record = Vec(5L, 0L, 0L, 0L, 11L, 0L, 0L, 0L, 22L, 0L, 0L, 0L)
    assertEquals(words(memory, out, offset = 0, count = 24), record ++ record)
  }

  test("pointer arithmetic uses the SPIR-V stride for packed optional-like structures") {
    val module = spv"""
      $openCL
      OpEntryPoint Kernel %main "packed_stride"
      OpDecorate %payload_base CPacked
      %void = OpTypeVoid
      %uchar = OpTypeInt 8 0
      %uint = OpTypeInt 32 0
      %ulong = OpTypeInt 64 0
      %three = OpConstant %ulong 3
      %padding = OpTypeArray %uchar %three
      %storage = OpTypeStruct %uint
      %payload_base = OpTypeStruct %storage %uchar
      %payload = OpTypeStruct %payload_base %padding
      %optional_base = OpTypeStruct %payload
      %optional = OpTypeStruct %optional_base
      %optional_ptr = OpTypePointer CrossWorkgroup %optional
      %uchar_ptr = OpTypePointer CrossWorkgroup %uchar
      %fn = OpTypeFunction %void %optional_ptr %uchar_ptr
      %zero = OpConstant %uint 0
      %one = OpConstant %uint 1
      %main = OpFunction %void None %fn
      %input = OpFunctionParameter %optional_ptr
      %output = OpFunctionParameter %uchar_ptr
      %entry = OpLabel
      %second = OpPtrAccessChain %optional_ptr %input %one
      %engaged = OpAccessChain %uchar_ptr %second %zero %zero %zero %one
      %flag = OpLoad %uchar %engaged
      OpStore %output %flag
      OpReturn
      OpFunctionEnd
    """
    val memory = RegionMemory()
    val region = memory.allocate(40L)
    memory.array(region)(12) = 1
    assertEquals(run(module, "packed_stride", memory, Vec(buffer(region), buffer(region, 32L))), Right(()))
    assertEquals(memory.bytes(region)(32), 1.toByte)
  }

  for (composite <- Seq("ConstantComposite", "SpecConstantComposite"))
    test(s"nested Op$composite constants materialise null pointer leaves") {
      val module = spv"""
        $openCL
        OpEntryPoint Kernel %main "nulls"
        %void = OpTypeVoid
        %uint = OpTypeInt 32 0
        %uint_ptr = OpTypePointer CrossWorkgroup %uint
        %two = OpConstant %uint 2
        %pointers = OpTypeArray %uint_ptr %two
        %record = OpTypeStruct %uint %pointers
        %record_ptr = OpTypePointer CrossWorkgroup %record
        %fn = OpTypeFunction %void %record_ptr
        %null = OpConstantNull %uint_ptr
        %nulls = Op$composite %pointers %null %null
        %tag = OpConstant %uint 17
        %initial = Op$composite %record %tag %nulls
        %main = OpFunction %void None %fn
        %out = OpFunctionParameter %record_ptr
        %entry = OpLabel
        OpStore %out %initial
        OpReturn
        OpFunctionEnd
      """
      val memory = RegionMemory()
      val out    = memory.allocate(Vec.fill(24)(0xff.toByte))
      assertEquals(run(module, "nulls", memory, Vec(buffer(out))), Right(()))
      assertEquals(memory.bytes(out), LittleEndian.toVec(17L, 4) ++ Vec.fill(20)(0.toByte))
    }

  test("pointer-bearing arrays use their decorated stride for relocation leaves") {
    val module = spv"""
      $openCL
      OpDecorate %pointers ArrayStride 16
      %uint = OpTypeInt 32 0
      %uint_ptr = OpTypePointer CrossWorkgroup %uint
      %two = OpConstant %uint 2
      %pointers = OpTypeArray %uint_ptr %two
    """
    val pointer = resultId(module) { case Instruction.TypePointer(id, _, _) => id }
    val array   = resultId(module) { case Instruction.TypeArray(id, _, _) => id }
    assertEquals(
      load(module).flatMap(_.pointerLeafOffsets(array, 0L)).map(_.map((offset, kind) => offset.raw -> kind)),
      Right(Vec(0L -> pointer, 16L -> pointer))
    )
  }

  test("ByVal byte-array aggregates retain their private layout when their first word is relocated") {
    val module = spv"""
      $openCL
      OpEntryPoint Kernel %main "byval_bytes"
      OpDecorate %record CPacked
      OpDecorate %aggregate FuncParamAttr ByVal
      %void = OpTypeVoid
      %ulong = OpTypeInt 64 0
      %uchar = OpTypeInt 8 0
      %uint = OpTypeInt 32 0
      %eight = OpConstant %ulong 8
      %bytes = OpTypeArray %uchar %eight
      %record = OpTypeStruct %bytes %ulong
      %record_ptr = OpTypePointer Function %record
      %ulong_private_ptr = OpTypePointer Function %ulong
      %ulong_ptr = OpTypePointer CrossWorkgroup %ulong
      %uint_generic_ptr = OpTypePointer Generic %uint
      %fn = OpTypeFunction %void %record_ptr %ulong_ptr
      %one = OpConstant %ulong 1
      %main = OpFunction %void None %fn
      %aggregate = OpFunctionParameter %record_ptr
      %out = OpFunctionParameter %ulong_ptr
      %entry = OpLabel
      %words = OpBitcast %ulong_private_ptr %aggregate
      %tail_ptr = OpPtrAccessChain %ulong_private_ptr %words %one
      %tail = OpLoad %ulong %tail_ptr
      OpStore %out %tail
      %address = OpLoad %ulong %words
      %pointer = OpConvertUToPtr %uint_generic_ptr %address
      %pointee = OpLoad %uint %pointer
      %widened = OpUConvert %ulong %pointee
      %pointee_out = OpPtrAccessChain %ulong_ptr %out %one
      OpStore %pointee_out %widened
      OpReturn
      OpFunctionEnd
    """
    val copied = Vec(99L, 0L, 7L, 0L)
    for (
      (needle, output) <- Seq(
        AllocationId(1L)                -> AllocationId(3L),
        AllocationId(9007199254740993L) -> AllocationId(9007199254740995L)
      )
    ) {
      val memory = RegionMemory(needle -> 4, output -> 16)
      memory.putInt(needle, 0, 7)
      val aggregate = (LittleEndian.toVec(0x12345678L, 8) ++ LittleEndian.toVec(99L, 8)).refine[NonEmpty[Vec[Byte]]]
      assertEquals(
        run(
          module,
          "byval_bytes",
          memory,
          Vec(KernelTask.Argument.Scalar(aggregate.value), buffer(output)),
          relocations = Vec(KernelTask.PointerRelocation(KernelTask.RelocationSource.Argument(0), 0L, needle, 0L))
        ),
        Right(())
      )
      assertEquals(words(memory, output, offset = 0, count = 4), copied)
      assertEquals(word(memory, needle, 0), 7L)
    }
  }

  test("pointer-sized relocated arguments bind externally when their aggregate pointee is larger") {
    val module = spv"""
      $openCL
      OpEntryPoint Kernel %main "external_aggregate"
      %void = OpTypeVoid
      %uint = OpTypeInt 32 0
      %uint_ptr = OpTypePointer CrossWorkgroup %uint
      %record = OpTypeStruct %uint_ptr %uint
      %record_ptr = OpTypePointer CrossWorkgroup %record
      %fn = OpTypeFunction %void %record_ptr
      %one = OpConstant %uint 1
      %answer = OpConstant %uint 42
      %main = OpFunction %void None %fn
      %argument = OpFunctionParameter %record_ptr
      %entry = OpLabel
      %member = OpAccessChain %uint_ptr %argument %one
      OpStore %member %answer
      OpReturn
      OpFunctionEnd
    """
    val memory = RegionMemory()
    val region = memory.allocate(12L)
    assertEquals(
      run(
        module,
        "external_aggregate",
        memory,
        Vec(scalar(0L, 8)),
        relocations = Vec(KernelTask.PointerRelocation(KernelTask.RelocationSource.Argument(0), 0L, region, 0L))
      ),
      Right(())
    )
    assertEquals(word(memory, region, 8), 42L)
  }
}
