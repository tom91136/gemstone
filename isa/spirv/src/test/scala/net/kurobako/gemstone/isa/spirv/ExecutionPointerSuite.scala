package net.kurobako.gemstone.isa.spirv

import fixtures.*
import java.util.concurrent.atomic.AtomicReference
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.testing.either.*
import net.kurobako.gemstone.vm.*

class ExecutionPointerSuite extends munit.FunSuite {
  test("generic pointers keep private and workgroup identity through integer round trips") {
    val module = spv"""
      OpCapability GenericPointer
      $openCL
      OpEntryPoint Kernel %main "generic" %shared
      %void = OpTypeVoid
      %uint = OpTypeInt 32 0
      %ulong = OpTypeInt 64 0
      %bool = OpTypeBool
      %private_ptr = OpTypePointer Function %uint
      %holder_ptr = OpTypePointer Function %private_ptr
      %local_ptr = OpTypePointer Workgroup %uint
      %generic_ptr = OpTypePointer Generic %uint
      %out_ptr = OpTypePointer CrossWorkgroup %ulong
      %fn = OpTypeFunction %void %out_ptr
      %seven = OpConstant %uint 7
      %nine = OpConstant %uint 9
      %ulong_1 = OpConstant %ulong 1
      %ulong_2 = OpConstant %ulong 2
      %ulong_3 = OpConstant %ulong 3
      %shared = OpVariable %local_ptr Workgroup
      %main = OpFunction %void None %fn
      %out = OpFunctionParameter %out_ptr
      %entry = OpLabel
      %private = OpVariable %private_ptr Function
      %holder = OpVariable %holder_ptr Function
      %generic = OpPtrCastToGeneric %generic_ptr %private
      %address = OpConvertPtrToU %ulong %generic
      %restored = OpConvertUToPtr %generic_ptr %address
      %as_local = OpGenericCastToPtrExplicit %local_ptr %restored Workgroup
      %as_private = OpGenericCastToPtrExplicit %private_ptr %restored Function
      OpStore %holder %as_private
      %held = OpLoad %private_ptr %holder
      OpStore %held %seven
      %stored = OpLoad %uint %private
      %wide = OpUConvert %ulong %stored
      OpStore %out %wide
      %shared_generic = OpPtrCastToGeneric %generic_ptr %shared
      %shared_local = OpGenericCastToPtrExplicit %local_ptr %shared_generic Workgroup
      OpStore %shared_local %nine
      %shared_value = OpLoad %uint %shared
      %shared_wide = OpUConvert %ulong %shared_value
      %out1 = OpPtrAccessChain %out_ptr %out %ulong_1
      OpStore %out1 %shared_wide
      %null_address = OpConvertPtrToU %ulong %as_local
      %out2 = OpPtrAccessChain %out_ptr %out %ulong_2
      OpStore %out2 %null_address
      %differs = OpINotEqual %bool %address %null_address
      %flag = OpSelect %ulong %differs %ulong_1 %null_address
      %out3 = OpPtrAccessChain %out_ptr %out %ulong_3
      OpStore %out3 %flag
      OpReturn
      OpFunctionEnd
    """
    val memory = RegionMemory()
    val out    = memory.allocate(32L)
    assertEquals(run(module, "generic", memory, Vec(buffer(out))), Right(()))
    assertEquals(memory.bytes(out), littleEndian(Seq(7L, 9L, 0L, 1L), 64))
  }

  test("pointer arithmetic outside an allocation round-trips through integers") {
    val body = """%minus_one = OpISub %ulong %ulong_0 %ulong_1
                 |%before = OpPtrAccessChain %uint_ptr %first %minus_one
                 |%address = OpConvertPtrToU %ulong %before
                 |%restored = OpConvertUToPtr %uint_ptr %address
                 |%back = OpPtrAccessChain %uint_ptr %restored %ulong_1
                 |%same = OpPtrEqual %bool %back %first
                 |%result = OpSelect %ulong %same %ulong_1 %ulong_0""".stripMargin
    assertEquals(pointers(body, 0L, 0L), ulong(1L))
  }

  test("pointer stores write the address and report an output relocation") {
    val module = spv"""
      $openCL
      OpEntryPoint Kernel %main "store_pointer"
      %void = OpTypeVoid
      %uint = OpTypeInt 32 0
      %uint_ptr = OpTypePointer CrossWorkgroup %uint
      %uint_ptr_ptr = OpTypePointer CrossWorkgroup %uint_ptr
      %fn = OpTypeFunction %void %uint_ptr_ptr %uint_ptr
      %main = OpFunction %void None %fn
      %target = OpFunctionParameter %uint_ptr_ptr
      %source = OpFunctionParameter %uint_ptr
      %entry = OpLabel
      OpStore %target %source
      OpReturn
      OpFunctionEnd
    """
    val memory     = RegionMemory()
    val allocation = memory.allocate(16L)
    val relocated  = AtomicReference(Option.empty[KernelTask.PointerRelocation])
    val program    = load(module).value
    val pointers   = task("store_pointer", Vec(buffer(allocation, 8L), buffer(allocation)))
    val recorded   = execute(
      program,
      pointers,
      memory,
      handler = {
        case Event.PointerRelocation(source, sourceOffset, target, targetOffset) =>
          relocated.set(Some(KernelTask.PointerRelocation(
            KernelTask.RelocationSource.Memory(source),
            sourceOffset,
            target,
            targetOffset
          )))
          Right(())
        case _ => Right(())
      }
    )
    assertEquals(recorded, Right(()))
    assertEquals(
      relocated.get(),
      Some(KernelTask.PointerRelocation(KernelTask.RelocationSource.Memory(allocation), 8L, allocation, 0L))
    )
    assert(memory.bytes(allocation).slice(8, 16).exists(_ != 0))
    val failure = Fault.EventHandling("relocation sink closed")
    assertEquals(
      execute(program, pointers, RegionMemory(allocation -> 16), handler = _ => Left(failure)),
      Left(failure)
    )
  }

  test("typed null pointers survive generic casts and integer conversion") {
    def module(nullType: String) = spv"""
      OpCapability GenericPointer
      $openCL
      OpEntryPoint Kernel %main "null_pointer"
      %void = OpTypeVoid
      %uchar = OpTypeInt 8 0
      %ulong = OpTypeInt 64 0
      %generic_ptr = OpTypePointer Generic %uchar
      %global_ptr = OpTypePointer CrossWorkgroup %uchar
      %out_ptr = OpTypePointer CrossWorkgroup %ulong
      %generic_ulong_ptr = OpTypePointer Generic %ulong
      %fn = OpTypeFunction %void %out_ptr
      %null = OpConstantNull $nullType
      %zero = OpConstant %ulong 0
      %main = OpFunction %void None %fn
      %out = OpFunctionParameter %out_ptr
      %entry = OpLabel
      %cast = OpGenericCastToPtr %global_ptr %null
      %address = OpConvertPtrToU %ulong %cast
      %restored_null = OpConvertUToPtr %generic_ptr %address
      %restored_cast = OpGenericCastToPtr %global_ptr %restored_null
      %restored_address = OpConvertPtrToU %ulong %restored_cast
      OpStore %out %restored_address
      %generic_out = OpPtrCastToGeneric %generic_ulong_ptr %out
      %out_address = OpConvertPtrToU %ulong %generic_out
      %restored_generic = OpConvertUToPtr %generic_ulong_ptr %out_address
      %restored_out = OpGenericCastToPtr %out_ptr %restored_generic
      OpStore %restored_out %zero
      OpReturn
      OpFunctionEnd
    """
    val memory    = RegionMemory()
    val out       = memory.allocate(Vec.fill(8)(0x55.toByte))
    val arguments = Vec(buffer(out))
    assertEquals(run(module("%generic_ptr"), "null_pointer", memory, arguments), Right(()))
    assertEquals(memory.bytes(out), Vec.fill(8)(0.toByte))
    def invalid(nullType: String): Fault =
      loadUnchecked(module(nullType)).flatMap(execute(_, task("null_pointer", arguments), memory)).leftValue
    assertEquals(
      invalid("%void"),
      Fault.InvalidRequest(
        "OpConstantNull %11 has invalid result type: SPIR-V type %3 has no scalar data leaves: Some(Void)"
      )
    )
    assertEquals(
      invalid("%global_ptr"),
      Fault.InvalidRequest("OpGenericCastToPtr cannot cast storage class 5 pointee %4 to storage class 5 pointee %4")
    )
  }

  for (copy <- Seq("OpCopyMemory %holder %source", "OpCopyMemorySized %holder_bytes %source_bytes %ulong_8"))
    test(s"${copy.takeWhile(_ != ' ')} relocates a client pointer copied into private storage") {
      val module = spv"""
        $openCL
        OpEntryPoint Kernel %main "copy_pointer"
        %void = OpTypeVoid
        %uchar = OpTypeInt 8 0
        %uint = OpTypeInt 32 0
        %ulong = OpTypeInt 64 0
        %uint_ptr = OpTypePointer CrossWorkgroup %uint
        %holder_type = OpTypeStruct %uint_ptr
        %source_ptr = OpTypePointer CrossWorkgroup %holder_type
        %private_ptr = OpTypePointer Function %holder_type
        %field_ptr = OpTypePointer Function %uint_ptr
        %global_bytes = OpTypePointer CrossWorkgroup %uchar
        %private_bytes = OpTypePointer Function %uchar
        %fn = OpTypeFunction %void %source_ptr %uint_ptr
        %uint_0 = OpConstant %uint 0
        %ulong_8 = OpConstant %ulong 8
        %main = OpFunction %void None %fn
        %source = OpFunctionParameter %source_ptr
        %out = OpFunctionParameter %uint_ptr
        %entry = OpLabel
        %holder = OpVariable %private_ptr Function
        %holder_bytes = OpBitcast %private_bytes %holder
        %source_bytes = OpBitcast %global_bytes %source
        $copy
        %field = OpAccessChain %field_ptr %holder %uint_0
        %target = OpLoad %uint_ptr %field
        %value = OpLoad %uint %target
        OpStore %out %value
        OpReturn
        OpFunctionEnd
      """
      val memory = RegionMemory()
      val source = memory.allocate(littleEndian(Seq(0x7f00deadbeefL), 64))
      val target = memory.allocate(littleEndian(Seq(1L, 23L), 32))
      val out    = memory.allocate(4L)
      val moved  = KernelTask.PointerRelocation(KernelTask.RelocationSource.Memory(source), 0L, target, 4L)
      assertEquals(
        run(module, "copy_pointer", memory, Vec(buffer(source), buffer(out)), relocations = Vec(moved)),
        Right(())
      )
      assertEquals(word(memory, out, 0), 23L)
    }

  for (
    (name, overwrite, expected) <- Seq(
      ("a 64-bit store", "OpStore %slot_ulong %ulong_pattern", 0x1122334455667788L),
      ("a 32-bit store over its high half", "OpStore %slot_high %uint_pattern", 0x11223344deadbeefL),
      ("a copy", "OpCopyMemorySized %slot_bytes %pattern_bytes %ulong_8", 0x1122334455667788L)
    )
  )
    test(s"$name over a relocated client pointer slot replaces the relocation") {
      val module = spv"""
        $openCL
        OpEntryPoint Kernel %main "overwrite"
        %void = OpTypeVoid
        %uchar = OpTypeInt 8 0
        %uint = OpTypeInt 32 0
        %ulong = OpTypeInt 64 0
        %uint_ptr = OpTypePointer CrossWorkgroup %uint
        %ulong_ptr = OpTypePointer CrossWorkgroup %ulong
        %bytes_ptr = OpTypePointer CrossWorkgroup %uchar
        %slot_ptr = OpTypePointer CrossWorkgroup %uint_ptr
        %fn = OpTypeFunction %void %slot_ptr %ulong_ptr %ulong_ptr
        %ulong_1 = OpConstant %ulong 1
        %ulong_8 = OpConstant %ulong 8
        %ulong_pattern = OpConstant %ulong 0x1122334455667788
        %uint_pattern = OpConstant %uint 0x11223344
        %main = OpFunction %void None %fn
        %slot = OpFunctionParameter %slot_ptr
        %pattern = OpFunctionParameter %ulong_ptr
        %out = OpFunctionParameter %ulong_ptr
        %entry = OpLabel
        %slot_ulong = OpBitcast %ulong_ptr %slot
        %slot_uint = OpBitcast %uint_ptr %slot
        %slot_high = OpPtrAccessChain %uint_ptr %slot_uint %ulong_1
        %slot_bytes = OpBitcast %bytes_ptr %slot
        %pattern_bytes = OpBitcast %bytes_ptr %pattern
        $overwrite
        %loaded = OpLoad %uint_ptr %slot
        %address = OpConvertPtrToU %ulong %loaded
        OpStore %out %address
        OpReturn
        OpFunctionEnd
      """
      val memory  = RegionMemory()
      val slot    = memory.allocate(littleEndian(Seq(0x7f00deadbeefL), 64))
      val pattern = memory.allocate(littleEndian(Seq(0x1122334455667788L), 64))
      val target  = memory.allocate(4L)
      val out     = memory.allocate(8L)
      val client  = KernelTask.PointerRelocation(KernelTask.RelocationSource.Memory(slot), 0L, target, 0L)
      assertEquals(
        run(module, "overwrite", memory, Vec(buffer(slot), buffer(pattern), buffer(out)), relocations = Vec(client)),
        Right(())
      )
      assertEquals(memory.bytes(out), LittleEndian.toVec(expected, 8))
    }

  test("pointers to dispatch-local storage stored in global memory resolve within their workgroup") {
    val module = spv"""
      OpCapability GenericPointer
      $openCL
      OpEntryPoint Kernel %main "local_pointer" %local_ids
      OpDecorate %local_ids BuiltIn LocalInvocationId
      %void = OpTypeVoid
      %bool = OpTypeBool
      %uint = OpTypeInt 32 0
      %ulong = OpTypeInt 64 0
      %ulong3 = OpTypeVector %ulong 3
      %ulong3_ptr = OpTypePointer Input %ulong3
      %uint_ptr = OpTypePointer CrossWorkgroup %uint
      %generic_ptr = OpTypePointer Generic %uint
      %slot_ptr = OpTypePointer CrossWorkgroup %generic_ptr
      %shared_ptr = OpTypePointer Workgroup %uint
      %private_ptr = OpTypePointer Function %uint
      %fn = OpTypeFunction %void %slot_ptr %uint_ptr
      %ulong_0 = OpConstant %ulong 0
      %ulong_1 = OpConstant %ulong 1
      %uint_7 = OpConstant %uint 7
      %uint_9 = OpConstant %uint 9
      %workgroup = OpConstant %uint ${Scope.Workgroup}
      %semantics = OpConstant %uint ${MemorySemantics.WorkgroupMemory | MemorySemantics.CrossWorkgroupMemory |
        MemorySemantics.AcquireRelease}
      %local_ids = OpVariable %ulong3_ptr Input
      %shared = OpVariable %shared_ptr Workgroup
      %main = OpFunction %void None %fn
      %slots = OpFunctionParameter %slot_ptr
      %out = OpFunctionParameter %uint_ptr
      %entry = OpLabel
      %private = OpVariable %private_ptr Function
      %local_vector = OpLoad %ulong3 %local_ids
      %local = OpCompositeExtract %ulong %local_vector 0
      %first = OpIEqual %bool %local %ulong_0
      %private_slot = OpPtrAccessChain %slot_ptr %slots %ulong_1
      %private_generic = OpPtrCastToGeneric %generic_ptr %private
      OpStore %private_slot %private_generic
      %private_reloaded = OpLoad %generic_ptr %private_slot
      OpStore %private_reloaded %uint_9
      %private_value = OpLoad %uint %private
      %private_out = OpPtrAccessChain %uint_ptr %out %ulong_1
      OpSelectionMerge %published None
      OpBranchConditional %first %publish %published
      %publish = OpLabel
      %shared_generic = OpPtrCastToGeneric %generic_ptr %shared
      OpStore %slots %shared_generic
      OpStore %private_out %private_value
      OpBranch %published
      %published = OpLabel
      OpControlBarrier %workgroup %workgroup %semantics
      OpSelectionMerge %done None
      OpBranchConditional %first %done %consume
      %consume = OpLabel
      %shared_reloaded = OpLoad %generic_ptr %slots
      OpStore %shared_reloaded %uint_7
      OpBranch %done
      %done = OpLabel
      OpControlBarrier %workgroup %workgroup %semantics
      %shared_value = OpLoad %uint %shared
      OpStore %out %shared_value
      OpReturn
      OpFunctionEnd
    """
    val memory = RegionMemory()
    val slots  = memory.allocate(16L)
    val out    = memory.allocate(8L)
    assertEquals(
      run(module, "local_pointer", memory, Vec(buffer(slots), buffer(out)), local = testDimensions(2)),
      Right(())
    )
    assertEquals(words(memory, out, 0, 2), Vec(7L, 9L))
  }

  private def pointers(body: String, first: Long >= 0, second: Long >= 0, checked: Boolean = true)
      : Either[Fault, Vec[Byte]] = {
    val module = spv"""
      $openCL
      OpEntryPoint Kernel %main "pointers"
      %void = OpTypeVoid
      %uint = OpTypeInt 32 0
      %ulong = OpTypeInt 64 0
      %bool = OpTypeBool
      %uint_ptr = OpTypePointer CrossWorkgroup %uint
      %ulong_ptr = OpTypePointer CrossWorkgroup %ulong
      %uint2 = OpTypeVector %uint 2
      %double = OpTypeFloat 64
      %local_uint_ptr = OpTypePointer Workgroup %uint
      %fn = OpTypeFunction %void %uint_ptr %uint_ptr %ulong_ptr
      %ulong_0 = OpConstant %ulong 0
      %ulong_1 = OpConstant %ulong 1
      %pattern_low = OpConstant %uint 0x89abcdef
      %pattern_high = OpConstant %uint 0xfedcba98
      %pattern = OpConstantComposite %uint2 %pattern_low %pattern_high
      %main = OpFunction %void None %fn
      %first = OpFunctionParameter %uint_ptr
      %second = OpFunctionParameter %uint_ptr
      %out = OpFunctionParameter %ulong_ptr
      %entry = OpLabel
      $body
      OpStore %out %result
      OpReturn
      OpFunctionEnd
    """
    val memory     = RegionMemory()
    val allocation = memory.allocate(32L)
    val launch     =
      task("pointers", Vec(buffer(allocation, first), buffer(allocation, second), buffer(allocation, 24L)))
    (if (checked) load(module) else loadUnchecked(module))
      .flatMap(execute(_, launch, memory))
      .map(_ => memory.bytes(allocation).slice(24, 32))
  }

  private def ulong(value: Long): Either[Fault, Vec[Byte]] = Right(LittleEndian.toVec(value, 8))

  for (
    (first, second, equal, difference) <- Seq[(Long >= 0, Long >= 0, Boolean, Long)](
      (0L, 0L, true, 0L),
      (0L, 12L, false, -3L),
      (12L, 0L, false, 3L)
    )
  )
    test(s"pointer equality and signed element difference at $first and $second") {
      def compare(opcode: String) =
        s"""%compared = $opcode %bool %first %second
           |%result = OpSelect %ulong %compared %ulong_1 %ulong_0""".stripMargin
      assertEquals(pointers(compare("OpPtrEqual"), first, second), ulong(if (equal) 1L else 0L))
      assertEquals(pointers(compare("OpPtrNotEqual"), first, second), ulong(if (equal) 0L else 1L))
      assertEquals(pointers("%result = OpPtrDiff %ulong %first %second", first, second), ulong(difference))
    }

  test("pointer difference truncates a signed result to the result width") {
    val body = """%difference = OpPtrDiff %uint %first %second
                 |%result = OpUConvert %ulong %difference""".stripMargin
    assertEquals(pointers(body, 0L, 12L), ulong(0xfffffffdL))
  }

  test("pointer equality compares addresses after pointee changes and null bitcasts") {
    val alias = """%as_ulong = OpBitcast %ulong_ptr %first
                  |%as_uint = OpBitcast %uint_ptr %as_ulong
                  |%same = OpPtrEqual %bool %first %as_uint
                  |%result = OpSelect %ulong %same %ulong_1 %ulong_0""".stripMargin
    assertEquals(pointers(alias, 8L, 0L), ulong(1L))
    val nullRoundTrip = """%null = OpBitcast %uint_ptr %ulong_0
                          |%result = OpBitcast %ulong %null""".stripMargin
    assertEquals(pointers(nullRoundTrip, 0L, 0L), ulong(0L))
  }

  for (integer <- Seq("ulong", "uint2"))
    test(s"pointer bitcast through $integer preserves dereference identity") {
      val body = s"""%address = OpBitcast %$integer %first
                    |%restored = OpBitcast %uint_ptr %address
                    |OpStore %restored %pattern_low
                    |%loaded = OpLoad %uint %first
                    |%result = OpUConvert %ulong %loaded""".stripMargin
      assertEquals(pointers(body, 8L, 0L), ulong(0x89abcdefL))
    }

  test("integer-vector pointer bitcast preserves lane order and the address sign bit") {
    val body = """%pointer = OpBitcast %uint_ptr %pattern
                 |%result = OpBitcast %ulong %pointer""".stripMargin
    assertEquals(pointers(body, 0L, 0L), ulong(0xfedcba9889abcdefL))
  }

  for ((target, reason) <- Seq(("uint", "width"), ("double", "floating point"), ("local_uint_ptr", "storage class")))
    test(s"pointer bitcast rejects incompatible $reason") {
      val body = s"""%cast = OpBitcast %$target %first
                    |%result = OpCopyObject %ulong %ulong_0""".stripMargin
      pointers(body, 0L, 0L, checked = false) match {
        case Left(Fault.InvalidRequest(message)) => assert(message.contains("OpBitcast"), message)
        case result                              => fail(s"expected invalid pointer bitcast, found $result")
      }
    }
}
