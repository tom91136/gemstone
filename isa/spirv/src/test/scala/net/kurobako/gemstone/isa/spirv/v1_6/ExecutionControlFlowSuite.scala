package net.kurobako.gemstone.isa.spirv.v1_6

import execution.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.binary.Widths.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.*
import net.kurobako.gemstone.isa.spirv.v1_6 as spv
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchGeometry.*
class ExecutionControlFlowSuite extends munit.FunSuite {
  for {
    width <- Seq[Int in (8, 16, 32, 64)](8, 16, 32, 64)
    rawWidth   = width.raw
    widthBytes = width.widen[Int in (8, 16, 32, 64)].toMemoryValueByteWidth
    signed <- Seq(false, true)
  }
    test(s"OpSwitch compares $rawWidth-bit literals at selector rawWidth, signed=$signed") {
      def resultType(n: Int): Operand = operand(OperandKind.IdResultType, n)
      def ref(n: Int): Operand        = operand(OperandKind.IdRef, n)
      def result(n: Int): Operand     = operand(OperandKind.IdResult, n)
      val literal                     = if (rawWidth == 64) operand(OperandKind.PairLiteralIntegerIdRef, -7, -1, 22)
      else operand(OperandKind.PairLiteralIntegerIdRef, -7, 22)
      val module = Module(
        Header(ISA.version, Word(0), Word(32), Word(0)),
        Vec(
          Instruction.TypeVoid(result(1)),
          Instruction.TypeInt(
            result(2),
            operand(OperandKind.LiteralInteger, rawWidth),
            operand(OperandKind.LiteralInteger, if (signed) 1 else 0)
          ),
          Instruction.TypeInt(
            result(3),
            operand(OperandKind.LiteralInteger, 32),
            operand(OperandKind.LiteralInteger, 0)
          ),
          Instruction.TypePointer(result(4), operand(OperandKind.StorageClass, 5), ref(3)),
          Instruction.TypeFunction(result(5), ref(1), Vec(ref(4), ref(2))),
          Instruction.Constant(resultType(3), result(6), operand(OperandKind.LiteralContextDependentNumber, 99)),
          Instruction.Constant(resultType(3), result(7), operand(OperandKind.LiteralContextDependentNumber, 0)),
          Instruction.EntryPoint(operand(OperandKind.ExecutionModel, 6), ref(10), stringOperand("switch"), Vec.empty),
          Instruction.Function(resultType(1), result(10), operand(OperandKind.FunctionControl, 0), ref(5)),
          Instruction.FunctionParameter(resultType(4), result(11)),
          Instruction.FunctionParameter(resultType(2), result(12)),
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
      val decoded = ISA.encode(module).flatMap(ISA.decode(_)).fold(error => fail(error.toString), identity)
      assertEquals(decoded, module)
      for ((selector, expected) <- Seq(-7L -> 99L, 7L -> 0L)) {
        val region = AllocationId(30L)
        val memory = ArrayMemory(region, 4)
        val task   = KernelTask.validated(
          "switch",
          Dimensions.One,
          Dimensions.One,
          Vec(
            KernelTask.Argument.Memory(region, 0L),
            KernelTask.Argument.Scalar(LittleEndian.toVec(selector, widthBytes.widen[Int in 0 ~ 8]))
          )
        ).fold(fail(_), identity)
        assertEquals(executeUnchecked(decoded, task, memory), Right(()))
        assertEquals(LittleEndian.decode(memory.bytes, 0, 4), expected)
      }
    }

  test("indirect function call rejects a pointer from another program") {
    val voidType       = operand(OperandKind.IdResult, 1)
    val u32            = operand(OperandKind.IdResult, 2)
    val u64            = operand(OperandKind.IdResult, 3)
    val pointerType    = operand(OperandKind.IdResult, 4)
    val entryType      = operand(OperandKind.IdResult, 5)
    val helperType     = operand(OperandKind.IdResult, 6)
    val helperValue    = operand(OperandKind.IdResult, 7)
    val entryFunction  = operand(OperandKind.IdResult, 8)
    val helperFunction = operand(OperandKind.IdResult, 9)
    val output         = operand(OperandKind.IdResult, 10)
    val token          = operand(OperandKind.IdResult, 11)
    val result         = operand(OperandKind.IdResult, 13)
    val module         = Module(
      Header(ISA.version, Word(0), Word(17), Word(0)),
      Vec(
        Instruction.TypeVoid(voidType),
        Instruction.TypeInt(u32, operand(OperandKind.LiteralInteger, 32), operand(OperandKind.LiteralInteger, 0)),
        Instruction.TypeInt(u64, operand(OperandKind.LiteralInteger, 64), operand(OperandKind.LiteralInteger, 0)),
        Instruction.TypePointer(pointerType, operand(OperandKind.StorageClass, spv.StorageClass.CrossWorkgroup), u32),
        Instruction.TypeFunction(entryType, voidType, Vec(pointerType, u64)),
        Instruction.TypeFunction(helperType, u32, Vec.empty),
        Instruction.Constant(u32, helperValue, operand(OperandKind.LiteralContextDependentNumber, 37)),
        Instruction.Name(helperFunction, stringOperand("helper")),
        Instruction.Name(helperValue, stringOperand("not_a_function")),
        Instruction.EntryPoint(
          operand(OperandKind.ExecutionModel, spv.ExecutionModel.Kernel),
          entryFunction,
          stringOperand("indirect"),
          Vec.empty
        ),
        Instruction.Function(
          voidType,
          entryFunction,
          operand(OperandKind.FunctionControl, spv.FunctionControl.None),
          entryType
        ),
        Instruction.FunctionParameter(pointerType, output),
        Instruction.FunctionParameter(u64, token),
        Instruction.Label(operand(OperandKind.IdResult, 12)),
        Instruction.FunctionPointerCallINTEL(u32, result, Vec(token)),
        Instruction.Store(output, result, None),
        Instruction.Return(),
        Instruction.FunctionEnd(),
        Instruction.Function(
          u32,
          helperFunction,
          operand(OperandKind.FunctionControl, spv.FunctionControl.None),
          helperType
        ),
        Instruction.Label(operand(OperandKind.IdResult, 14)),
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
    val voidType        = operand(OperandKind.IdResult, 1)
    val u32             = operand(OperandKind.IdResult, 2)
    val pointerType     = operand(OperandKind.IdResult, 3)
    val entryType       = operand(OperandKind.IdResult, 4)
    val helperType      = operand(OperandKind.IdResult, 5)
    val one             = operand(OperandKind.IdResult, 6)
    val entryFunction   = operand(OperandKind.IdResult, 7)
    val helperFunction  = operand(OperandKind.IdResult, 8)
    val output          = operand(OperandKind.IdResult, 9)
    val input           = operand(OperandKind.IdResult, 10)
    val callResult      = operand(OperandKind.IdResult, 12)
    val helperParameter = operand(OperandKind.IdResult, 14)
    val helperResult    = operand(OperandKind.IdResult, 16)
    val module          = Module(
      Header(ISA.version, Word(0), Word(24), Word(0)),
      Vec(
        Instruction.TypeVoid(voidType),
        Instruction.TypeInt(u32, operand(OperandKind.LiteralInteger, 32), operand(OperandKind.LiteralInteger, 0)),
        Instruction.TypePointer(pointerType, operand(OperandKind.StorageClass, spv.StorageClass.CrossWorkgroup), u32),
        Instruction.TypeFunction(entryType, voidType, Vec(pointerType, u32)),
        Instruction.TypeFunction(helperType, u32, Vec(u32)),
        Instruction.Constant(u32, one, operand(OperandKind.LiteralContextDependentNumber, 1)),
        Instruction.String(operand(OperandKind.IdResult, 17), stringOperand("call.cl")),
        Instruction.EntryPoint(
          operand(OperandKind.ExecutionModel, spv.ExecutionModel.Kernel),
          entryFunction,
          stringOperand("call"),
          Vec.empty
        ),
        Instruction.Function(
          voidType,
          entryFunction,
          operand(OperandKind.FunctionControl, spv.FunctionControl.None),
          entryType
        ),
        Instruction.FunctionParameter(pointerType, output),
        Instruction.FunctionParameter(u32, input),
        Instruction.Label(operand(OperandKind.IdResult, 11)),
        Instruction.Line(
          operand(OperandKind.IdRef, 17),
          operand(OperandKind.LiteralInteger, 1),
          operand(OperandKind.LiteralInteger, 1)
        ),
        Instruction.FunctionCall(u32, callResult, helperFunction, Vec(input)),
        Instruction.Store(output, callResult, None),
        Instruction.NoLine(),
        Instruction.Return(),
        Instruction.FunctionEnd(),
        Instruction.Function(
          u32,
          helperFunction,
          operand(OperandKind.FunctionControl, spv.FunctionControl.None),
          helperType
        ),
        Instruction.FunctionParameter(u32, helperParameter),
        Instruction.Label(operand(OperandKind.IdResult, 15)),
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

    val nestedFunction  = operand(OperandKind.IdResult, 18)
    val nestedParameter = operand(OperandKind.IdResult, 19)
    val nestedResult    = operand(OperandKind.IdResult, 21)
    val nestedModule    = module.copy(instructions =
      module.instructions.map {
        case Instruction.IAdd(resultType, destination, left, _) if destination == helperResult =>
          Instruction.FunctionCall(resultType, destination, nestedFunction, Vec(left))
        case instruction => instruction
      } ++ Vec(
        Instruction.Function(
          u32,
          nestedFunction,
          operand(OperandKind.FunctionControl, spv.FunctionControl.None),
          helperType
        ),
        Instruction.FunctionParameter(u32, nestedParameter),
        Instruction.Label(operand(OperandKind.IdResult, 20)),
        Instruction.IAdd(u32, nestedResult, nestedParameter, one),
        Instruction.ReturnValue(nestedResult),
        Instruction.FunctionEnd()
      )
    )
    val nestedMemory = ArrayMemory(allocation, 4)
    assertEquals(executeUnchecked(nestedModule, dispatch, nestedMemory), Right(()))
    assertEquals(nestedMemory.bytes, LittleEndian.toVec(42L, 4))
  }

  test("dispatch executes structured loops and Phi values") {
    val voidType    = operand(OperandKind.IdResult, 1)
    val u32         = operand(OperandKind.IdResult, 2)
    val pointerType = operand(OperandKind.IdResult, 3)
    val function    = operand(OperandKind.IdResult, 5)
    val output      = operand(OperandKind.IdResult, 6)
    val bound       = operand(OperandKind.IdResult, 7)
    val entry       = operand(OperandKind.IdResult, 8)
    val header      = operand(OperandKind.IdResult, 9)
    val body        = operand(OperandKind.IdResult, 10)
    val merge       = operand(OperandKind.IdResult, 11)
    val index       = operand(OperandKind.IdResult, 12)
    val condition   = operand(OperandKind.IdResult, 13)
    val destination = operand(OperandKind.IdResult, 14)
    val next        = operand(OperandKind.IdResult, 15)
    val zero        = operand(OperandKind.IdResult, 16)
    val one         = operand(OperandKind.IdResult, 17)
    val module      = Module(
      Header(ISA.version, Word(0), Word(32), Word(0)),
      Vec(
        Instruction.TypeVoid(voidType),
        Instruction.TypeInt(u32, operand(OperandKind.LiteralInteger, 32), operand(OperandKind.LiteralInteger, 0)),
        Instruction.TypeBool(operand(OperandKind.IdResult, 18)),
        Instruction.TypePointer(pointerType, operand(OperandKind.StorageClass, spv.StorageClass.CrossWorkgroup), u32),
        Instruction.TypeFunction(operand(OperandKind.IdResult, 4), voidType, Vec(pointerType, u32)),
        Instruction.Constant(u32, zero, operand(OperandKind.LiteralContextDependentNumber, 0)),
        Instruction.Constant(u32, one, operand(OperandKind.LiteralContextDependentNumber, 1)),
        Instruction.EntryPoint(
          operand(OperandKind.ExecutionModel, spv.ExecutionModel.Kernel),
          function,
          stringOperand("sequence"),
          Vec.empty
        ),
        Instruction.Function(
          voidType,
          function,
          operand(OperandKind.FunctionControl, spv.FunctionControl.None),
          operand(OperandKind.IdRef, 4)
        ),
        Instruction.FunctionParameter(pointerType, output),
        Instruction.FunctionParameter(u32, bound),
        Instruction.Label(entry),
        Instruction.Branch(header),
        Instruction.Label(header),
        Instruction.Phi(u32, index, Vec(phiPair(zero, entry), phiPair(next, body))),
        Instruction.LoopMerge(merge, body, operand(OperandKind.LoopControl, spv.LoopControl.None)),
        Instruction.ULessThan(operand(OperandKind.IdResultType, 18), condition, index, bound),
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
    val voidType     = operand(OperandKind.IdResult, 1)
    val u8           = operand(OperandKind.IdResult, 2)
    val u32          = operand(OperandKind.IdResult, 3)
    val outputType   = operand(OperandKind.IdResult, 4)
    val localType    = operand(OperandKind.IdResult, 5)
    val localBytePtr = operand(OperandKind.IdResult, 6)
    val function     = operand(OperandKind.IdResult, 8)
    val output       = operand(OperandKind.IdResult, 9)
    val local        = operand(OperandKind.IdResult, 11)
    val bytePointer  = operand(OperandKind.IdResult, 12)
    val loaded       = operand(OperandKind.IdResult, 13)
    val value        = operand(OperandKind.IdResult, 14)
    val instructions = Vec(
      Instruction.TypeVoid(voidType),
      Instruction.TypeInt(u8, operand(OperandKind.LiteralInteger, 8), operand(OperandKind.LiteralInteger, 0)),
      Instruction.TypeInt(u32, operand(OperandKind.LiteralInteger, 32), operand(OperandKind.LiteralInteger, 0)),
      Instruction.TypePointer(outputType, operand(OperandKind.StorageClass, spv.StorageClass.CrossWorkgroup), u32),
      Instruction.TypePointer(localType, operand(OperandKind.StorageClass, spv.StorageClass.Function), u32),
      Instruction.TypePointer(localBytePtr, operand(OperandKind.StorageClass, spv.StorageClass.Function), u8),
      Instruction.TypeFunction(operand(OperandKind.IdResult, 7), voidType, Vec(outputType)),
      Instruction.Constant(u32, value, operand(OperandKind.LiteralContextDependentNumber, 0x12345678)),
      Instruction.EntryPoint(
        operand(OperandKind.ExecutionModel, spv.ExecutionModel.Kernel),
        function,
        stringOperand("lifetime"),
        Vec.empty
      ),
      Instruction.Function(
        voidType,
        function,
        operand(OperandKind.FunctionControl, spv.FunctionControl.None),
        operand(OperandKind.IdRef, 7)
      ),
      Instruction.FunctionParameter(outputType, output),
      Instruction.Label(operand(OperandKind.IdResult, 10)),
      Instruction.Variable(localType, local, operand(OperandKind.StorageClass, spv.StorageClass.Function), None),
      Instruction.Bitcast(localBytePtr, bytePointer, local),
      Instruction.LifetimeStart(bytePointer, operand(OperandKind.LiteralInteger, 4)),
      Instruction.Store(local, value, None),
      Instruction.Load(u32, loaded, local, None),
      Instruction.Store(output, loaded, None),
      Instruction.LifetimeStop(bytePointer, operand(OperandKind.LiteralInteger, 4)),
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
        Instruction.LifetimeStart(bytePointer, operand(OperandKind.LiteralInteger, 4, 0))
      )
    )
    executeUnchecked(malformed, dispatch, memory) match {
      case Left(Fault.InvalidTask(message)) =>
        assertEquals(message, "OpLifetimeStart size must contain one word, found 2")
      case result => fail(s"expected typed malformed lifetime marker failure, found $result")
    }
  }

}
