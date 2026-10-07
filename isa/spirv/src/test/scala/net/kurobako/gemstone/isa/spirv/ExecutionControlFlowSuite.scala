package net.kurobako.gemstone.isa.spirv

import fixtures.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.testing.either.*
import net.kurobako.gemstone.vm.*

class ExecutionControlFlowSuite extends munit.FunSuite {
  for {
    width  <- Seq(8, 16, 32, 64)
    signed <- Seq(false, true)
  } test(s"OpSwitch compares $width-bit literals, signed $signed") {
    val literal = if (signed) "-7" else s"0x${((BigInt(1) << width) - 7).toString(16)}"
    val module  = spv"""
      $openCL
      OpEntryPoint Kernel %main "switch"
      %void = OpTypeVoid
      %selector_type = OpTypeInt $width ${if (signed) 1 else 0}
      %uint = OpTypeInt 32 0
      %out_ptr = OpTypePointer CrossWorkgroup %uint
      %fn = OpTypeFunction %void %out_ptr %selector_type
      %matched = OpConstant %uint 99
      %unmatched = OpConstant %uint 0
      %main = OpFunction %void None %fn
      %out = OpFunctionParameter %out_ptr
      %selector = OpFunctionParameter %selector_type
      %entry = OpLabel
      OpSwitch %selector %default $literal %case
      %default = OpLabel
      OpStore %out %unmatched
      OpReturn
      %case = OpLabel
      OpStore %out %matched
      OpReturn
      OpFunctionEnd
    """
    assertEquals(ISA.encode(module).flatMap(ISA.decode(_)), Right(module))
    for ((selector, expected) <- Seq(-7L -> 99L, 7L -> 0L)) {
      val memory = RegionMemory()
      val out    = memory.allocate(4L)
      assertEquals(
        run(module, "switch", memory, Vec(buffer(out), scalar(selector, (width / 8).refine[Int in 0 ~ 8].value))),
        Right(())
      )
      assertEquals(word(memory, out, 0), expected)
    }
  }

  test("indirect function call rejects a pointer from another program") {
    val module = spv"""
      $openCL
      OpEntryPoint Kernel %main "indirect"
      OpName %helper "helper"
      OpName %not_a_function "not_a_function"
      %void = OpTypeVoid
      %uint = OpTypeInt 32 0
      %ulong = OpTypeInt 64 0
      %uint_ptr = OpTypePointer CrossWorkgroup %uint
      %entry_fn = OpTypeFunction %void %uint_ptr %ulong
      %helper_fn = OpTypeFunction %uint
      %not_a_function = OpConstant %uint 37
      %main = OpFunction %void None %entry_fn
      %out = OpFunctionParameter %uint_ptr
      %token = OpFunctionParameter %ulong
      %entry = OpLabel
      %result = OpFunctionPointerCallINTEL %uint %token
      OpStore %out %result
      OpReturn
      OpFunctionEnd
      %helper = OpFunction %uint None %helper_fn
      %helper_entry = OpLabel
      OpReturnValue %not_a_function
      OpFunctionEnd
    """
    val program = load(module).value
    val helper  = program.kernel("helper").value.entryPointId.map(_.raw.toLong).getOrElse(fail("helper has no id"))
    val memory  = RegionMemory()
    val out     = memory.allocate(4L)
    def call(pointer: Long) = task("indirect", Vec(buffer(out), scalar(pointer, 8)))
    val wrongProgram        = (2L << 32) | helper
    assertEquals(load(module).flatMap(execute(_, call(helper), memory)), Right(()))
    assertEquals(word(memory, out, 0), 37L)
    assert(load(module).flatMap(execute(_, call(wrongProgram), memory)).isLeft)
    assertEquals(program.kernelNames, Vec("indirect"))
    assert(program.kernel("missing").isLeft)
    assert(program.kernel("not_a_function").isLeft)
    assertEquals(program.bindFunctionPointerNamespace(ProgramId(1L)), Right(()))
    assertEquals(program.bindFunctionPointerNamespace(ProgramId(1L)), Right(()))
    assert(program.bindFunctionPointerNamespace(ProgramId(2L)).isLeft)
    assert(program.bindFunctionPointerNamespace(ProgramId(0x100000000L)).isLeft)
    assert(execute(program, call(wrongProgram), memory).isLeft)
    assertEquals(execute(program, call((1L << 32) | helper), memory), Right(()))
  }

  for (nested <- Seq(false, true))
    test(s"OpFunctionCall passes arguments and return values${if (nested) " through a nested call" else ""}") {
      val nestedFunction = """%nested = OpFunction %uint None %helper_fn
                             |%nested_parameter = OpFunctionParameter %uint
                             |%nested_entry = OpLabel
                             |%nested_sum = OpIAdd %uint %nested_parameter %one
                             |OpReturnValue %nested_sum
                             |OpFunctionEnd""".stripMargin
      val module = spv"""
        $openCL
        OpEntryPoint Kernel %main "call"
        %file = OpString "call.cl"
        %void = OpTypeVoid
        %uint = OpTypeInt 32 0
        %uint_ptr = OpTypePointer CrossWorkgroup %uint
        %entry_fn = OpTypeFunction %void %uint_ptr %uint
        %helper_fn = OpTypeFunction %uint %uint
        %one = OpConstant %uint 1
        %main = OpFunction %void None %entry_fn
        %out = OpFunctionParameter %uint_ptr
        %input = OpFunctionParameter %uint
        %entry = OpLabel
        OpLine %file 1 1
        %called = OpFunctionCall %uint %helper %input
        OpStore %out %called
        OpNoLine
        OpReturn
        OpFunctionEnd
        %helper = OpFunction %uint None %helper_fn
        %parameter = OpFunctionParameter %uint
        %helper_entry = OpLabel
        %sum = ${if (nested) "OpFunctionCall %uint %nested %parameter" else "OpIAdd %uint %parameter %one"}
        OpReturnValue %sum
        OpFunctionEnd
        ${if (nested) nestedFunction else ""}
      """
      val memory = RegionMemory()
      val out    = memory.allocate(4L)
      assertEquals(run(module, "call", memory, Vec(buffer(out), scalar(41L, 4))), Right(()))
      assertEquals(word(memory, out, 0), 42L)
    }

  test("structured loop with OpPhi stores a sequence") {
    val module = spv"""
      $openCL
      OpEntryPoint Kernel %main "sequence"
      %void = OpTypeVoid
      %uint = OpTypeInt 32 0
      %bool = OpTypeBool
      %uint_ptr = OpTypePointer CrossWorkgroup %uint
      %fn = OpTypeFunction %void %uint_ptr %uint
      %zero = OpConstant %uint 0
      %one = OpConstant %uint 1
      %main = OpFunction %void None %fn
      %out = OpFunctionParameter %uint_ptr
      %bound = OpFunctionParameter %uint
      %entry = OpLabel
      OpBranch %header
      %header = OpLabel
      %index = OpPhi %uint %zero %entry %next %body
      OpLoopMerge %merge %body None
      %continue = OpULessThan %bool %index %bound
      OpBranchConditional %continue %body %merge
      %body = OpLabel
      %element = OpPtrAccessChain %uint_ptr %out %index
      OpStore %element %index
      %next = OpIAdd %uint %index %one
      OpBranch %header
      %merge = OpLabel
      OpReturn
      OpFunctionEnd
    """
    assertEquals(ISA.encode(module).flatMap(ISA.decode(_)), Right(module))
    val memory = RegionMemory()
    val out    = memory.allocate(36L)
    assertEquals(run(module, "sequence", memory, Vec(buffer(out, 4L), scalar(8L, 4))), Right(()))
    assertEquals(words(memory, out, 0, 9), Vec(0L) ++ Vec.from(0L until 8L))
  }

  test("lifetime markers around function variables accept one-word sizes only") {
    val module = spv"""
      $openCL
      OpEntryPoint Kernel %main "lifetime"
      %void = OpTypeVoid
      %uchar = OpTypeInt 8 0
      %uint = OpTypeInt 32 0
      %out_ptr = OpTypePointer CrossWorkgroup %uint
      %local_ptr = OpTypePointer Function %uint
      %local_bytes_ptr = OpTypePointer Function %uchar
      %fn = OpTypeFunction %void %out_ptr
      %value = OpConstant %uint 0x12345678
      %main = OpFunction %void None %fn
      %out = OpFunctionParameter %out_ptr
      %entry = OpLabel
      %local = OpVariable %local_ptr Function
      %local_bytes = OpBitcast %local_bytes_ptr %local
      OpLifetimeStart %local_bytes 4
      OpStore %local %value
      %loaded = OpLoad %uint %local
      OpStore %out %loaded
      OpLifetimeStop %local_bytes 4
      OpReturn
      OpFunctionEnd
    """
    val memory = RegionMemory()
    val out    = memory.allocate(4L)
    assertEquals(run(module, "lifetime", memory, Vec(buffer(out))), Right(()))
    assertEquals(word(memory, out, 0), 0x12345678L)
    // a two-word size does not decode from binary, so only the instruction ADT can carry it
    val malformed = module.copy(instructions = module.instructions.map {
      case Instruction.LifetimeStart(pointer, _) =>
        Instruction.LifetimeStart(pointer, operand(OperandKind.LiteralInteger, 4, 0))
      case instruction => instruction
    })
    assertEquals(
      loadUnchecked(malformed).flatMap(execute(_, task("lifetime", Vec(buffer(out))), memory)),
      Left(Fault.InvalidRequest("OpLifetimeStart size must contain one word, found 2"))
    )
  }
}
