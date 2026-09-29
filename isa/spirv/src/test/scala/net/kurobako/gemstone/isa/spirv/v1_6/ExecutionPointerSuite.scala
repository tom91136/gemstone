package net.kurobako.gemstone.isa.spirv.v1_6

import java.util.concurrent.atomic.AtomicReference
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
    val voidType       = operand("IdResult", 1)
    val u32            = operand("IdResult", 2)
    val pointerType    = operand("IdResult", 3)
    val pointerPointer = operand("IdResult", 4)
    val functionType   = operand("IdResult", 5)
    val function       = operand("IdResult", 6)
    val target         = operand("IdResult", 7)
    val source         = operand("IdResult", 8)
    val module         = Module(
      Header(ISA.version, Word(0), Word(12), Word(0)),
      Vec(
        Instruction.TypeVoid(voidType),
        Instruction.TypeInt(u32, operand("LiteralInteger", 32), operand("LiteralInteger", 0)),
        Instruction.TypePointer(pointerType, operand("StorageClass", spv.StorageClass.CrossWorkgroup), u32),
        Instruction.TypePointer(pointerPointer, operand("StorageClass", spv.StorageClass.CrossWorkgroup), pointerType),
        Instruction.TypeFunction(functionType, voidType, Vec(pointerPointer, pointerType)),
        Instruction.EntryPoint(
          operand("ExecutionModel", spv.ExecutionModel.Kernel),
          function,
          stringOperand("store_pointer"),
          Vec.empty
        ),
        Instruction.Function(voidType, function, operand("FunctionControl", spv.FunctionControl.None), functionType),
        Instruction.FunctionParameter(pointerPointer, target),
        Instruction.FunctionParameter(pointerType, source),
        Instruction.Label(operand("IdResult", 9)),
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
    val voidType          = operand("IdResult", 1)
    val u8                = operand("IdResult", 2)
    val u64               = operand("IdResult", 3)
    val genericPointer    = operand("IdResult", 4)
    val globalBytePointer = operand("IdResult", 5)
    val outputPointer     = operand("IdResult", 6)
    val functionType      = operand("IdResult", 7)
    val nullPointer       = operand("IdResult", 8)
    val function          = operand("IdResult", 9)
    val output            = operand("IdResult", 10)
    val castPointer       = operand("IdResult", 12)
    val addressValue      = operand("IdResult", 13)
    val genericU64Pointer = operand("IdResult", 14)
    val zero              = operand("IdResult", 15)
    val genericOutput     = operand("IdResult", 16)
    val outputAddress     = operand("IdResult", 17)
    val restoredGeneric   = operand("IdResult", 18)
    val restoredOutput    = operand("IdResult", 19)
    val restoredNull      = operand("IdResult", 20)
    val instructions      = Vec(
      Instruction.TypeVoid(voidType),
      Instruction.TypeInt(u8, operand("LiteralInteger", 8), operand("LiteralInteger", 0)),
      Instruction.TypeInt(u64, operand("LiteralInteger", 64), operand("LiteralInteger", 0)),
      Instruction.TypePointer(genericPointer, operand("StorageClass", spv.StorageClass.Generic), u8),
      Instruction.TypePointer(globalBytePointer, operand("StorageClass", spv.StorageClass.CrossWorkgroup), u8),
      Instruction.TypePointer(outputPointer, operand("StorageClass", spv.StorageClass.CrossWorkgroup), u64),
      Instruction.TypeFunction(functionType, voidType, Vec(outputPointer)),
      Instruction.ConstantNull(genericPointer, nullPointer),
      Instruction.TypePointer(genericU64Pointer, operand("StorageClass", spv.StorageClass.Generic), u64),
      Instruction.Constant(u64, zero, operand("LiteralContextDependentNumber", 0)),
      Instruction.EntryPoint(
        operand("ExecutionModel", spv.ExecutionModel.Kernel),
        function,
        stringOperand("null_pointer"),
        Vec.empty
      ),
      Instruction.Function(voidType, function, operand("FunctionControl", spv.FunctionControl.None), functionType),
      Instruction.FunctionParameter(outputPointer, output),
      Instruction.Label(operand("IdResult", 11)),
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
}
