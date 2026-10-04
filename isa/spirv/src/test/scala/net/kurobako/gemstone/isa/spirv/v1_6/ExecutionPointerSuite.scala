package net.kurobako.gemstone.isa.spirv.v1_6

import java.util.concurrent.atomic.AtomicReference
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.*
import net.kurobako.gemstone.isa.spirv.v1_6 as spv
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchGeometry.*
class ExecutionPointerSuite extends munit.FunSuite {
  import execution.*

  test("out-of-range pointer arithmetic can be converted to an integer without dereferencing") {
    val allocation = AllocationId(124L)
    val memory     = VMMemory.MemoryState(
      ArrayMemory(allocation, 8),
      Vec.empty,
      _ => Right(())
    )
    val pointer = VMMemory.Pointer(VMMemory.Region.External(allocation), -1L, 0, 5)
    val address = memory.pointerInteger(pointer).toOption.get
    assert(address != 0L)
    assertEquals(memory.pointerInteger(pointer), Right(address))
    assert(memory.pointerInteger(pointer.copy(offset = -2L)).toOption.get != address)
    assertEquals(memory.integerPointer(address, 0, 5), Right(pointer))
    assert(memory.read(pointer, 1).isLeft)
  }

  test("pointer stores dirty the destination and report an output relocation") {
    val voidType       = operand(OperandKind.IdResult, 1)
    val u32            = operand(OperandKind.IdResult, 2)
    val pointerType    = operand(OperandKind.IdResult, 3)
    val pointerPointer = operand(OperandKind.IdResult, 4)
    val functionType   = operand(OperandKind.IdResult, 5)
    val function       = operand(OperandKind.IdResult, 6)
    val target         = operand(OperandKind.IdResult, 7)
    val source         = operand(OperandKind.IdResult, 8)
    val module         = Module(
      Header(ISA.version, Word(0), Word(12), Word(0)),
      Vec(
        Instruction.TypeVoid(voidType),
        Instruction.TypeInt(u32, operand(OperandKind.LiteralInteger, 32), operand(OperandKind.LiteralInteger, 0)),
        Instruction.TypePointer(pointerType, operand(OperandKind.StorageClass, spv.StorageClass.CrossWorkgroup), u32),
        Instruction.TypePointer(
          pointerPointer,
          operand(OperandKind.StorageClass, spv.StorageClass.CrossWorkgroup),
          pointerType
        ),
        Instruction.TypeFunction(functionType, voidType, Vec(pointerPointer, pointerType)),
        Instruction.EntryPoint(
          operand(OperandKind.ExecutionModel, spv.ExecutionModel.Kernel),
          function,
          stringOperand("store_pointer"),
          Vec.empty
        ),
        Instruction.Function(
          voidType,
          function,
          operand(OperandKind.FunctionControl, spv.FunctionControl.None),
          functionType
        ),
        Instruction.FunctionParameter(pointerPointer, target),
        Instruction.FunctionParameter(pointerType, source),
        Instruction.Label(operand(OperandKind.IdResult, 9)),
        Instruction.Store(target, source, None),
        Instruction.Return(),
        Instruction.FunctionEnd()
      ),
      Endianness.Little
    )
    val allocation                            = AllocationId(44L)
    val relocated                             = AtomicReference(Option.empty[KernelTask.PointerRelocation])
    val memory                                = ArrayMemory(allocation, 16)
    val handler: Event => Either[Fault, Unit] = event => {
      event match {
        case Event.PointerRelocation(source, sourceOffset, target, targetOffset) =>
          relocated.set(Some(KernelTask.PointerRelocation(
            KernelTask.RelocationSource.Memory(source),
            sourceOffset,
            target,
            targetOffset
          )))
        case _ => ()
      }
      Right(())
    }
    val dispatch = KernelTask
      .validated(
        "store_pointer",
        Dimensions.One,
        Dimensions.One,
        Vec(
          KernelTask.Argument.Memory(allocation, 8L),
          KernelTask.Argument.Memory(allocation, 0L)
        )
      )
      .toOption
      .get

    val prepared = loadUnchecked(module).toOption.get
    assertEquals(
      VMInterpreter.execute(prepared, dispatch, memory, TaskControl(), Workgroups.Sequential, handler),
      Right(())
    )
    assertEquals(
      relocated.get(),
      Some(KernelTask.PointerRelocation(
        KernelTask.RelocationSource.Memory(allocation),
        8L,
        allocation,
        0L
      ))
    )
    assert(memory.bytes.slice(8, 16).exists(_ != 0))
    val failure = Fault.EventHandling("relocation sink closed")
    assertEquals(
      VMInterpreter.execute(
        prepared,
        dispatch,
        ArrayMemory(allocation, 16),
        TaskControl(),
        Workgroups.Sequential,
        _ => Left(failure)
      ),
      Left(failure)
    )
  }

  test("dispatch preserves typed null pointers through generic casts and integer conversion") {
    val voidType          = operand(OperandKind.IdResult, 1)
    val u8                = operand(OperandKind.IdResult, 2)
    val u64               = operand(OperandKind.IdResult, 3)
    val genericPointer    = operand(OperandKind.IdResult, 4)
    val globalBytePointer = operand(OperandKind.IdResult, 5)
    val outputPointer     = operand(OperandKind.IdResult, 6)
    val functionType      = operand(OperandKind.IdResult, 7)
    val nullPointer       = operand(OperandKind.IdResult, 8)
    val function          = operand(OperandKind.IdResult, 9)
    val output            = operand(OperandKind.IdResult, 10)
    val castPointer       = operand(OperandKind.IdResult, 12)
    val addressValue      = operand(OperandKind.IdResult, 13)
    val genericU64Pointer = operand(OperandKind.IdResult, 14)
    val zero              = operand(OperandKind.IdResult, 15)
    val genericOutput     = operand(OperandKind.IdResult, 16)
    val outputAddress     = operand(OperandKind.IdResult, 17)
    val restoredGeneric   = operand(OperandKind.IdResult, 18)
    val restoredOutput    = operand(OperandKind.IdResult, 19)
    val restoredNull      = operand(OperandKind.IdResult, 20)
    val instructions      = Vec(
      Instruction.TypeVoid(voidType),
      Instruction.TypeInt(u8, operand(OperandKind.LiteralInteger, 8), operand(OperandKind.LiteralInteger, 0)),
      Instruction.TypeInt(u64, operand(OperandKind.LiteralInteger, 64), operand(OperandKind.LiteralInteger, 0)),
      Instruction.TypePointer(genericPointer, operand(OperandKind.StorageClass, spv.StorageClass.Generic), u8),
      Instruction.TypePointer(
        globalBytePointer,
        operand(OperandKind.StorageClass, spv.StorageClass.CrossWorkgroup),
        u8
      ),
      Instruction.TypePointer(outputPointer, operand(OperandKind.StorageClass, spv.StorageClass.CrossWorkgroup), u64),
      Instruction.TypeFunction(functionType, voidType, Vec(outputPointer)),
      Instruction.ConstantNull(genericPointer, nullPointer),
      Instruction.TypePointer(genericU64Pointer, operand(OperandKind.StorageClass, spv.StorageClass.Generic), u64),
      Instruction.Constant(u64, zero, operand(OperandKind.LiteralContextDependentNumber, 0)),
      Instruction.EntryPoint(
        operand(OperandKind.ExecutionModel, spv.ExecutionModel.Kernel),
        function,
        stringOperand("null_pointer"),
        Vec.empty
      ),
      Instruction.Function(
        voidType,
        function,
        operand(OperandKind.FunctionControl, spv.FunctionControl.None),
        functionType
      ),
      Instruction.FunctionParameter(outputPointer, output),
      Instruction.Label(operand(OperandKind.IdResult, 11)),
      Instruction.GenericCastToPtr(globalBytePointer, castPointer, nullPointer),
      Instruction.ConvertPtrToU(u64, addressValue, castPointer),
      Instruction.ConvertUToPtr(genericPointer, restoredNull, addressValue),
      Instruction.GenericCastToPtr(globalBytePointer, castPointer, restoredNull),
      Instruction.ConvertPtrToU(u64, addressValue, castPointer),
      Instruction.Store(output, addressValue, None),
      Instruction.PtrCastToGeneric(genericU64Pointer, genericOutput, output),
      Instruction.ConvertPtrToU(u64, outputAddress, genericOutput),
      Instruction.ConvertUToPtr(genericU64Pointer, restoredGeneric, outputAddress),
      Instruction.GenericCastToPtr(outputPointer, restoredOutput, restoredGeneric),
      Instruction.Store(restoredOutput, zero, None),
      Instruction.Return(),
      Instruction.FunctionEnd()
    )
    val module =
      Module(Header(ISA.version, Word(0), Word(21), Word(0)), instructions, Endianness.Little)
    val allocation = AllocationId(23L)
    val memory     = ArrayMemory(allocation, 8)
    val dispatch   = KernelTask
      .validated(
        "null_pointer",
        Dimensions.One,
        Dimensions.One,
        Vec(KernelTask.Argument.Memory(allocation, 0L))
      )
      .toOption
      .get

    assertEquals(executeUnchecked(module, dispatch, memory), Right(()))
    assertEquals(memory.bytes, Vec.fill(8)(0.toByte))

    val invalidNull =
      module.copy(instructions = instructions.updated(7, Instruction.ConstantNull(voidType, nullPointer)))
    executeUnchecked(invalidNull, dispatch, memory) match {
      case Left(Fault.InvalidTask(message)) =>
        assertEquals(
          message,
          "OpConstantNull %8 has invalid result type: SPIR-V type %1 has no scalar data leaves: Some(Void)"
        )
      case result => fail(s"expected typed invalid null failure, found $result")
    }

    val invalidCast = module.copy(instructions =
      instructions
        .updated(7, Instruction.ConstantNull(globalBytePointer, nullPointer))
        .updated(14, Instruction.GenericCastToPtr(globalBytePointer, castPointer, nullPointer))
    )
    executeUnchecked(invalidCast, dispatch, memory) match {
      case Left(Fault.InvalidTask(message)) =>
        assertEquals(message, "OpGenericCastToPtr cannot cast storage class 5 pointee %2 to storage class 5 pointee %2")
      case result => fail(s"expected typed invalid generic cast failure, found $result")
    }
  }

  private def pointerId(value: Int): Operand = operand(OperandKind.IdRef, value)

  // %21 and %22 are uint pointers; %23 is the ulong output; the body produces %30.
  private def pointerProgram(body: Vec[Instruction]): Module = {
    val id = pointerId
    Module(
      Header(ISA.version, Word(0), Word(40), Word(0)),
      Vec(
        Instruction.TypeVoid(id(1)),
        Instruction.TypeInt(id(2), operand(OperandKind.LiteralInteger, 32), operand(OperandKind.LiteralInteger, 0)),
        Instruction.TypeInt(id(3), operand(OperandKind.LiteralInteger, 64), operand(OperandKind.LiteralInteger, 0)),
        Instruction.TypeBool(id(4)),
        Instruction.TypePointer(id(5), operand(OperandKind.StorageClass, spv.StorageClass.CrossWorkgroup), id(2)),
        Instruction.TypePointer(id(6), operand(OperandKind.StorageClass, spv.StorageClass.CrossWorkgroup), id(3)),
        Instruction.TypeVector(id(7), id(2), operand(OperandKind.LiteralInteger, 2)),
        Instruction.TypeFloat(id(8), operand(OperandKind.LiteralInteger, 64), None),
        Instruction.TypePointer(id(9), operand(OperandKind.StorageClass, spv.StorageClass.Workgroup), id(2)),
        Instruction.TypeFunction(id(11), id(1), Vec(id(5), id(5), id(6))),
        Instruction.Constant(id(3), id(12), operand(OperandKind.LiteralContextDependentNumber, 0, 0)),
        Instruction.Constant(id(3), id(13), operand(OperandKind.LiteralContextDependentNumber, 1, 0)),
        Instruction.Constant(id(2), id(14), operand(OperandKind.LiteralContextDependentNumber, 0x89abcdef)),
        Instruction.Constant(id(2), id(15), operand(OperandKind.LiteralContextDependentNumber, 0xfedcba98)),
        Instruction.ConstantComposite(id(7), id(16), Vec(id(14), id(15))),
        Instruction.EntryPoint(
          operand(OperandKind.ExecutionModel, spv.ExecutionModel.Kernel),
          id(20),
          stringOperand("pointers"),
          Vec.empty
        ),
        Instruction.Function(id(1), id(20), operand(OperandKind.FunctionControl, spv.FunctionControl.None), id(11)),
        Instruction.FunctionParameter(id(5), id(21)),
        Instruction.FunctionParameter(id(5), id(22)),
        Instruction.FunctionParameter(id(6), id(23)),
        Instruction.Label(id(24))
      ) ++ body ++ Vec(Instruction.Store(id(23), id(30), None), Instruction.Return(), Instruction.FunctionEnd()),
      Endianness.Little
    )
  }

  private def executePointers(body: Vec[Instruction], first: Long >= 0, second: Long >= 0): Either[Fault, Vec[Byte]] = {
    val allocation = AllocationId(125L)
    val memory     = ArrayMemory(allocation, 32)
    val dispatch   = KernelTask.validated(
      "pointers",
      Dimensions.One,
      Dimensions.One,
      Vec(
        KernelTask.Argument.Memory(allocation, first),
        KernelTask.Argument.Memory(allocation, second),
        KernelTask.Argument.Memory(allocation, 24L)
      )
    ).toOption.get
    executeUnchecked(pointerProgram(body), dispatch, memory).map(_ => memory.bytes.slice(24, 32))
  }

  private val comparisons: Vec[(Long >= 0, Long >= 0, Boolean, Long)] =
    Vec((0L, 0L, true, 0L), (0L, 12L, false, -3L), (12L, 0L, false, 3L))

  for ((first, second, equal, difference) <- comparisons)
    test(s"pointer equality and signed element difference at $first and $second") {
      val id       = pointerId
      val equality = Vec(
        Instruction.PtrEqual(id(4), id(31), id(21), id(22)),
        Instruction.Select(id(3), id(30), id(31), id(13), id(12))
      )
      val inequality = equality.updated(0, Instruction.PtrNotEqual(id(4), id(31), id(21), id(22)))
      assertEquals(
        executePointers(body = equality, first = first, second = second),
        Right(LittleEndian.toVec(if (equal) 1L else 0L, 8))
      )
      assertEquals(
        executePointers(body = inequality, first = first, second = second),
        Right(LittleEndian.toVec(if (equal) 0L else 1L, 8))
      )
      assertEquals(
        executePointers(body = Vec(Instruction.PtrDiff(id(3), id(30), id(21), id(22))), first = first, second = second),
        Right(LittleEndian.toVec(difference, 8))
      )
    }

  test("pointer difference truncates a signed result to the result width") {
    val id   = pointerId
    val body = Vec(Instruction.PtrDiff(id(2), id(31), id(21), id(22)), Instruction.UConvert(id(3), id(30), id(31)))
    assertEquals(executePointers(body = body, first = 0L, second = 12L), Right(LittleEndian.toVec(0xfffffffdL, 8)))
  }

  test("pointer equality compares addresses after pointee changes and null bitcasts") {
    val id    = pointerId
    val alias = Vec(
      Instruction.Bitcast(id(6), id(31), id(21)),
      Instruction.Bitcast(id(5), id(32), id(31)),
      Instruction.PtrEqual(id(4), id(33), id(21), id(32)),
      Instruction.Select(id(3), id(30), id(33), id(13), id(12))
    )
    assertEquals(executePointers(body = alias, first = 8L, second = 0L), Right(LittleEndian.toVec(1L, 8)))
    val nullRoundTrip = Vec(Instruction.Bitcast(id(5), id(31), id(12)), Instruction.Bitcast(id(3), id(30), id(31)))
    assertEquals(executePointers(body = nullRoundTrip, first = 0L, second = 0L), Right(LittleEndian.toVec(0L, 8)))
  }

  for (integerType <- Vec(3, 7))
    test(s"pointer bitcast through integer type %$integerType preserves dereference identity") {
      val id   = pointerId
      val body = Vec(
        Instruction.Bitcast(id(integerType), id(31), id(21)),
        Instruction.Bitcast(id(5), id(32), id(31)),
        Instruction.Store(id(32), id(14), None),
        Instruction.Load(id(2), id(33), id(21), None),
        Instruction.UConvert(id(3), id(30), id(33))
      )
      assertEquals(executePointers(body = body, first = 8L, second = 0L), Right(LittleEndian.toVec(0x89abcdefL, 8)))
    }

  test("integer-vector pointer bitcast preserves lane order and the address sign bit") {
    val id   = pointerId
    val body = Vec(Instruction.Bitcast(id(5), id(31), id(16)), Instruction.Bitcast(id(3), id(30), id(31)))
    assertEquals(
      executePointers(body = body, first = 0L, second = 0L),
      Right(LittleEndian.toVec(0xfedcba9889abcdefL, 8))
    )
  }

  for ((targetType, reason) <- Vec((2, "width"), (8, "floating point"), (9, "storage class")))
    test(s"pointer bitcast rejects incompatible $reason") {
      val id   = pointerId
      val body = Vec(Instruction.Bitcast(id(targetType), id(31), id(21)), Instruction.CopyObject(id(3), id(30), id(12)))
      executePointers(body = body, first = 0L, second = 0L) match {
        case Left(Fault.InvalidTask(message)) => assert(message.contains("OpBitcast"), message)
        case result                           => fail(s"expected invalid pointer bitcast, found $result")
      }
    }
}
