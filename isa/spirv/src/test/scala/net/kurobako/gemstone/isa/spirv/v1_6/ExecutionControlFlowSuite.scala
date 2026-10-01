package net.kurobako.gemstone.isa.spirv.v1_6

import execution.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.*
import net.kurobako.gemstone.isa.spirv.v1_6 as spv
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchGeometry.*
class ExecutionControlFlowSuite extends munit.FunSuite {
  for {
    width  <- Seq(8, 16, 32, 64)
    signed <- Seq(false, true)
  }
    test(s"OpSwitch compares $width-bit literals at selector width, signed=$signed") {
      def ref(n: Int): Operand    = operand("IdRef", n)
      def result(n: Int): Operand = operand("IdResult", n)
      val literal                 = if (width == 64) operand("PairLiteralIntegerIdRef", -7, -1, 22)
      else operand("PairLiteralIntegerIdRef", -7, 22)
      val module = Module(
        Header(ISA.version, Word(0), Word(32), Word(0)),
        Vec(
          Instruction.TypeVoid(result(1)),
          Instruction.TypeInt(
            result(2),
            operand("LiteralInteger", width),
            operand("LiteralInteger", if (signed) 1 else 0)
          ),
          Instruction.TypeInt(result(3), operand("LiteralInteger", 32), operand("LiteralInteger", 0)),
          Instruction.TypePointer(result(4), operand("StorageClass", 5), ref(3)),
          Instruction.TypeFunction(result(5), ref(1), Vec(ref(4), ref(2))),
          Instruction.Constant(ref(3), result(6), operand("LiteralContextDependentNumber", 99)),
          Instruction.Constant(ref(3), result(7), operand("LiteralContextDependentNumber", 0)),
          Instruction.EntryPoint(operand("ExecutionModel", 6), ref(10), stringOperand("switch"), Vec.empty),
          Instruction.Function(ref(1), result(10), operand("FunctionControl", 0), ref(5)),
          Instruction.FunctionParameter(ref(4), result(11)),
          Instruction.FunctionParameter(ref(2), result(12)),
          Instruction.Label(result(20)),
          Instruction.Switch(ref(12), ref(21), Vec(literal)),
          Instruction.Label(result(21)),
          Instruction.Store(ref(11), ref(7), None),
          Instruction.Return(),
          Instruction.Label(result(22)),
          Instruction.Store(ref(11), ref(6), None),
          Instruction.Return(),
          Instruction.FunctionEnd()
        ),
        Endianness.Little
      )
      for ((selector, expected) <- Seq(-7L -> 99L, 7L -> 0L)) {
        val region = AllocationId(30L)
        val memory = ArrayMemory(region, 4)
        val task   = KernelTask.validated(
          "switch",
          Dimensions.One,
          Dimensions.One,
          Vec(
            KernelTask.Argument.Memory(region, 0L),
            KernelTask.Argument.Scalar(LittleEndian.toVec(selector, (width / 8).unsafe[Int where Between[0, 8]]))
          )
        ).fold(fail(_), identity)
        assertEquals(executeUnchecked(module, task, memory), Right(()))
        assertEquals(LittleEndian.decode(memory.bytes, 0, 4), expected)
      }
    }

  test("indirect function call rejects a pointer from another program") {
    val voidType       = operand("IdResult", 1)
    val u32            = operand("IdResult", 2)
    val u64            = operand("IdResult", 3)
    val pointerType    = operand("IdResult", 4)
    val entryType      = operand("IdResult", 5)
    val helperType     = operand("IdResult", 6)
    val helperValue    = operand("IdResult", 7)
    val entryFunction  = operand("IdResult", 8)
    val helperFunction = operand("IdResult", 9)
    val output         = operand("IdResult", 10)
    val token          = operand("IdResult", 11)
    val result         = operand("IdResult", 13)
    val module         = Module(
      Header(ISA.version, Word(0), Word(17), Word(0)),
      Vec(
        Instruction.TypeVoid(voidType),
        Instruction.TypeInt(u32, operand("LiteralInteger", 32), operand("LiteralInteger", 0)),
        Instruction.TypeInt(u64, operand("LiteralInteger", 64), operand("LiteralInteger", 0)),
        Instruction.TypePointer(pointerType, operand("StorageClass", spv.StorageClass.CrossWorkgroup), u32),
        Instruction.TypeFunction(entryType, voidType, Vec(pointerType, u64)),
        Instruction.TypeFunction(helperType, u32, Vec.empty),
        Instruction.Constant(u32, helperValue, operand("LiteralContextDependentNumber", 37)),
        Instruction.Name(helperFunction, stringOperand("helper")),
        Instruction.Name(helperValue, stringOperand("not_a_function")),
        Instruction.EntryPoint(
          operand("ExecutionModel", spv.ExecutionModel.Kernel),
          entryFunction,
          stringOperand("indirect"),
          Vec.empty
        ),
        Instruction.Function(voidType, entryFunction, operand("FunctionControl", spv.FunctionControl.None), entryType),
        Instruction.FunctionParameter(pointerType, output),
        Instruction.FunctionParameter(u64, token),
        Instruction.Label(operand("IdResult", 12)),
        Instruction.FunctionPointerCallINTEL(u32, result, Vec(token)),
        Instruction.Store(output, result, None),
        Instruction.Return(),
        Instruction.FunctionEnd(),
        Instruction.Function(u32, helperFunction, operand("FunctionControl", spv.FunctionControl.None), helperType),
        Instruction.Label(operand("IdResult", 14)),
        Instruction.ReturnValue(helperValue),
        Instruction.FunctionEnd()
      ),
      Endianness.Little
    )
    val allocation              = AllocationId(30L)
    val memory                  = ArrayMemory(allocation, 4)
    def dispatch(pointer: Long) = KernelTask
      .validated(
        "indirect",
        Dimensions.One,
        Dimensions.One,
        Vec(
          KernelTask.Argument.Memory(allocation, 0L),
          KernelTask.Argument.Scalar(LittleEndian.toVec(pointer, 8))
        )
      )
      .toOption
      .get

    assertEquals(executeUnchecked(module, dispatch(9L), memory), Right(()))
    assertEquals(memory.bytes, LittleEndian.toVec(37L, 4))
    val wrongProgram = (2L << 32) | 9L
    assert(executeUnchecked(module, dispatch(wrongProgram), memory).isLeft)
    val loaded = loadUnchecked(module).toOption.get
    val helper = loaded.kernel("helper").fold(error => fail(error.message), identity)
    assertEquals(helper.entryPointId.map(_.raw), Some(9))
    assertEquals(loaded.kernelNames, Vec("indirect"))
    assert(loaded.kernel("missing").isLeft)
    assert(loaded.kernel("not_a_function").isLeft)
    assertEquals(loaded.bindFunctionPointerNamespace(1L), Right(()))
    assertEquals(loaded.bindFunctionPointerNamespace(1L), Right(()))
    assert(loaded.bindFunctionPointerNamespace(2L).isLeft)
    assert(loaded.bindFunctionPointerNamespace(0L).isLeft)
    assert(loaded.bindFunctionPointerNamespace(0x100000000L).isLeft)
    assert(executeLoaded(loaded, dispatch(wrongProgram), memory).isLeft)
    val pointer = (1L << 32) | helper.entryPointId.get.raw.toLong
    assertEquals(executeLoaded(loaded, dispatch(pointer), memory), Right(()))
  }

  test("dispatch executes typed function calls and return values") {
    val voidType        = operand("IdResult", 1)
    val u32             = operand("IdResult", 2)
    val pointerType     = operand("IdResult", 3)
    val entryType       = operand("IdResult", 4)
    val helperType      = operand("IdResult", 5)
    val one             = operand("IdResult", 6)
    val entryFunction   = operand("IdResult", 7)
    val helperFunction  = operand("IdResult", 8)
    val output          = operand("IdResult", 9)
    val input           = operand("IdResult", 10)
    val callResult      = operand("IdResult", 12)
    val helperParameter = operand("IdResult", 14)
    val helperResult    = operand("IdResult", 16)
    val module          = Module(
      Header(ISA.version, Word(0), Word(24), Word(0)),
      Vec(
        Instruction.TypeVoid(voidType),
        Instruction.TypeInt(u32, operand("LiteralInteger", 32), operand("LiteralInteger", 0)),
        Instruction.TypePointer(pointerType, operand("StorageClass", spv.StorageClass.CrossWorkgroup), u32),
        Instruction.TypeFunction(entryType, voidType, Vec(pointerType, u32)),
        Instruction.TypeFunction(helperType, u32, Vec(u32)),
        Instruction.Constant(u32, one, operand("LiteralContextDependentNumber", 1)),
        Instruction.String(operand("IdResult", 17), stringOperand("call.cl")),
        Instruction.EntryPoint(
          operand("ExecutionModel", spv.ExecutionModel.Kernel),
          entryFunction,
          stringOperand("call"),
          Vec.empty
        ),
        Instruction.Function(voidType, entryFunction, operand("FunctionControl", spv.FunctionControl.None), entryType),
        Instruction.FunctionParameter(pointerType, output),
        Instruction.FunctionParameter(u32, input),
        Instruction.Label(operand("IdResult", 11)),
        Instruction.Line(operand("IdRef", 17), operand("LiteralInteger", 1), operand("LiteralInteger", 1)),
        Instruction.FunctionCall(u32, callResult, helperFunction, Vec(input)),
        Instruction.Store(output, callResult, None),
        Instruction.NoLine(),
        Instruction.Return(),
        Instruction.FunctionEnd(),
        Instruction.Function(u32, helperFunction, operand("FunctionControl", spv.FunctionControl.None), helperType),
        Instruction.FunctionParameter(u32, helperParameter),
        Instruction.Label(operand("IdResult", 15)),
        Instruction.IAdd(u32, helperResult, helperParameter, one),
        Instruction.ReturnValue(helperResult),
        Instruction.FunctionEnd()
      ),
      Endianness.Little
    )
    val allocation = AllocationId(21L)
    val memory     = ArrayMemory(allocation, 4)
    val dispatch   = KernelTask
      .validated(
        "call",
        Dimensions.One,
        Dimensions.One,
        Vec(
          KernelTask.Argument.Memory(allocation, 0L),
          KernelTask.Argument.Scalar(LittleEndian.toVec(41L, 4))
        )
      )
      .toOption
      .get

    assertEquals(executeUnchecked(module, dispatch, memory), Right(()))
    assertEquals(memory.bytes, LittleEndian.toVec(42L, 4))
  }

  test("dispatch executes structured loops and Phi values") {
    val voidType    = operand("IdResult", 1)
    val u32         = operand("IdResult", 2)
    val pointerType = operand("IdResult", 3)
    val function    = operand("IdResult", 5)
    val output      = operand("IdResult", 6)
    val bound       = operand("IdResult", 7)
    val entry       = operand("IdResult", 8)
    val header      = operand("IdResult", 9)
    val body        = operand("IdResult", 10)
    val merge       = operand("IdResult", 11)
    val index       = operand("IdResult", 12)
    val condition   = operand("IdResult", 13)
    val destination = operand("IdResult", 14)
    val next        = operand("IdResult", 15)
    val zero        = operand("IdResult", 16)
    val one         = operand("IdResult", 17)
    val module      = Module(
      Header(ISA.version, Word(0), Word(32), Word(0)),
      Vec(
        Instruction.TypeVoid(voidType),
        Instruction.TypeInt(u32, operand("LiteralInteger", 32), operand("LiteralInteger", 0)),
        Instruction.TypeBool(operand("IdResult", 18)),
        Instruction.TypePointer(pointerType, operand("StorageClass", spv.StorageClass.CrossWorkgroup), u32),
        Instruction.TypeFunction(operand("IdResult", 4), voidType, Vec(pointerType, u32)),
        Instruction.Constant(u32, zero, operand("LiteralContextDependentNumber", 0)),
        Instruction.Constant(u32, one, operand("LiteralContextDependentNumber", 1)),
        Instruction.EntryPoint(
          operand("ExecutionModel", spv.ExecutionModel.Kernel),
          function,
          stringOperand("sequence"),
          Vec.empty
        ),
        Instruction.Function(
          voidType,
          function,
          operand("FunctionControl", spv.FunctionControl.None),
          operand("IdRef", 4)
        ),
        Instruction.FunctionParameter(pointerType, output),
        Instruction.FunctionParameter(u32, bound),
        Instruction.Label(entry),
        Instruction.Branch(header),
        Instruction.Label(header),
        Instruction.Phi(u32, index, Vec(phiPair(zero, entry), phiPair(next, body))),
        Instruction.LoopMerge(merge, body, operand("LoopControl", spv.LoopControl.None)),
        Instruction.ULessThan(operand("IdResultType", 18), condition, index, bound),
        Instruction.BranchConditional(condition, body, merge, Vec.empty),
        Instruction.Label(body),
        Instruction.PtrAccessChain(pointerType, destination, output, index, Vec.empty),
        Instruction.Store(destination, index, None),
        Instruction.IAdd(u32, next, index, one),
        Instruction.Branch(header),
        Instruction.Label(merge),
        Instruction.Return(),
        Instruction.FunctionEnd()
      ),
      Endianness.Little
    )
    val allocation = AllocationId(2L)
    val memory     = ArrayMemory(allocation, 36)
    val dispatch   = KernelTask
      .validated(
        "sequence",
        Dimensions.One,
        Dimensions.One,
        Vec(
          KernelTask.Argument.Memory(allocation, 4L),
          KernelTask.Argument.Scalar(LittleEndian.toVec(8L, 4))
        )
      )
      .toOption
      .get

    val decoded = ISA.decode(ISA.encode(module).toOption.get).toOption.get
    assertEquals(executeUnchecked(decoded, dispatch, memory), Right(()))
    assertEquals(memory.bytes.take(4), Vec.fill(4)(0.toByte))
    assertEquals(
      memory.bytes.drop(4),
      Vec.from((0L until 8L).flatMap(LittleEndian.toVec(_, 4)))
    )
  }

  test("dispatch validates lifetime markers around function objects") {
    val voidType     = operand("IdResult", 1)
    val u8           = operand("IdResult", 2)
    val u32          = operand("IdResult", 3)
    val outputType   = operand("IdResult", 4)
    val localType    = operand("IdResult", 5)
    val localBytePtr = operand("IdResult", 6)
    val function     = operand("IdResult", 8)
    val output       = operand("IdResult", 9)
    val local        = operand("IdResult", 11)
    val bytePointer  = operand("IdResult", 12)
    val loaded       = operand("IdResult", 13)
    val value        = operand("IdResult", 14)
    val instructions = Vec(
      Instruction.TypeVoid(voidType),
      Instruction.TypeInt(u8, operand("LiteralInteger", 8), operand("LiteralInteger", 0)),
      Instruction.TypeInt(u32, operand("LiteralInteger", 32), operand("LiteralInteger", 0)),
      Instruction.TypePointer(outputType, operand("StorageClass", spv.StorageClass.CrossWorkgroup), u32),
      Instruction.TypePointer(localType, operand("StorageClass", spv.StorageClass.Function), u32),
      Instruction.TypePointer(localBytePtr, operand("StorageClass", spv.StorageClass.Function), u8),
      Instruction.TypeFunction(operand("IdResult", 7), voidType, Vec(outputType)),
      Instruction.Constant(u32, value, operand("LiteralContextDependentNumber", 0x12345678)),
      Instruction.EntryPoint(
        operand("ExecutionModel", spv.ExecutionModel.Kernel),
        function,
        stringOperand("lifetime"),
        Vec.empty
      ),
      Instruction.Function(
        voidType,
        function,
        operand("FunctionControl", spv.FunctionControl.None),
        operand("IdRef", 7)
      ),
      Instruction.FunctionParameter(outputType, output),
      Instruction.Label(operand("IdResult", 10)),
      Instruction.Variable(localType, local, operand("StorageClass", spv.StorageClass.Function), None),
      Instruction.Bitcast(localBytePtr, bytePointer, local),
      Instruction.LifetimeStart(bytePointer, operand("LiteralInteger", 4)),
      Instruction.Store(local, value, None),
      Instruction.Load(u32, loaded, local, None),
      Instruction.Store(output, loaded, None),
      Instruction.LifetimeStop(bytePointer, operand("LiteralInteger", 4)),
      Instruction.Return(),
      Instruction.FunctionEnd()
    )
    val module =
      Module(Header(ISA.version, Word(0), Word(20), Word(0)), instructions, Endianness.Little)
    val allocation = AllocationId(3L)
    val memory     = ArrayMemory(allocation, 4)
    val dispatch   = KernelTask
      .validated(
        "lifetime",
        Dimensions.One,
        Dimensions.One,
        Vec(KernelTask.Argument.Memory(allocation, 0L))
      )
      .toOption
      .get

    assertEquals(executeUnchecked(module, dispatch, memory), Right(()))
    assertEquals(memory.bytes, LittleEndian.toVec(0x12345678L, 4))

    val malformed = module.copy(instructions =
      instructions.updated(
        14,
        Instruction.LifetimeStart(bytePointer, operand("LiteralInteger", 4, 0))
      )
    )
    executeUnchecked(malformed, dispatch, memory) match {
      case Left(Fault.InvalidTask(message)) =>
        assertEquals(message, "OpLifetimeStart size must contain one word, found 2")
      case result => fail(s"expected typed malformed lifetime marker failure, found $result")
    }
  }

}
