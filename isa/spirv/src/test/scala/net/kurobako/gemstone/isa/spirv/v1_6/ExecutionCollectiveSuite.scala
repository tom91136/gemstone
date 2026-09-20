package net.kurobako.gemstone.isa.spirv.v1_6

import net.kurobako.gemstone.Vec

import net.kurobako.gemstone.binary.ByteRange
import net.kurobako.gemstone.binary.FloatingPointWidth
import net.kurobako.gemstone.binary.LittleEndian
import net.kurobako.gemstone.binary.ScalarByteWidth
import net.kurobako.gemstone.execution.ExecutionFailure
import net.kurobako.gemstone.execution.ExecutionControl
import net.kurobako.gemstone.execution.ExecutionLimits
import net.kurobako.gemstone.execution.ExecutionMemory
import net.kurobako.gemstone.execution.AllocationId
import net.kurobako.gemstone.execution.Dimensions
import net.kurobako.gemstone.execution.KernelArgument
import net.kurobako.gemstone.execution.KernelDispatch
import net.kurobako.gemstone.execution.KernelName
import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*
import net.kurobako.gemstone.execution.PointerRelocation
import net.kurobako.gemstone.execution.ProgramId
import net.kurobako.gemstone.execution.RelocationSource

import java.util.concurrent.atomic.AtomicReference
final class ExecutionCollectiveSuite extends munit.FunSuite with ExecutionTestSupport {
  test("floating minimum and maximum preserve IEEE signed zero") {
    assertEquals(Interpreter.floatingMinimumBits(FloatingPointWidth(16), 0x0000L, 0x8000L), 0x8000L)
    assertEquals(Interpreter.floatingMaximumBits(FloatingPointWidth(16), 0x0000L, 0x8000L), 0x0000L)
    assertEquals(Interpreter.floatingMinimumBits(FloatingPointWidth(32), 0L, 0x80000000L), 0x80000000L)
    assertEquals(Interpreter.floatingMaximumBits(FloatingPointWidth(32), 0L, 0x80000000L), 0L)
    assertEquals(Interpreter.floatingMinimumBits(FloatingPointWidth(64), 0L, 0x8000000000000000L), 0x8000000000000000L)
    assertEquals(Interpreter.floatingMaximumBits(FloatingPointWidth(64), 0L, 0x8000000000000000L), 0L)
  }

  test("dispatch executes floating atomic minimum and maximum and returns old values") {
    val voidType    = operand("IdResult", 1)
    val u32         = operand("IdResult", 2)
    val f32         = operand("IdResult", 3)
    val pointer     = operand("IdResult", 4)
    val functionTy  = operand("IdResult", 5)
    val scope       = operand("IdResult", 6)
    val semantics   = operand("IdResult", 7)
    val initial     = operand("IdResult", 8)
    val minimum     = operand("IdResult", 9)
    val maximum     = operand("IdResult", 10)
    val one         = operand("IdResult", 11)
    val two         = operand("IdResult", 12)
    val function    = operand("IdResult", 13)
    val target      = operand("IdResult", 14)
    val oldMinimum  = operand("IdResult", 15)
    val oldMaximum  = operand("IdResult", 16)
    val minOutput   = operand("IdResult", 17)
    val maxOutput   = operand("IdResult", 18)
    val initialBits = java.lang.Float.floatToRawIntBits(3.0f)
    val minimumBits = java.lang.Float.floatToRawIntBits(1.0f)
    val maximumBits = java.lang.Float.floatToRawIntBits(5.0f)
    val module      = Module(
      Header(ISA.version, Word(0), Word(19), Word(0)),
      Vec(
        Instruction.TypeVoid(voidType),
        Instruction.TypeInt(u32, operand("LiteralInteger", 32), operand("LiteralInteger", 0)),
        Instruction.TypeFloat(f32, operand("LiteralInteger", 32), None),
        Instruction.TypePointer(pointer, operand("StorageClass", 5), f32),
        Instruction.TypeFunction(functionTy, voidType, Vec(pointer)),
        Instruction.Constant(u32, scope, operand("LiteralContextDependentNumber", 2)),
        Instruction.Constant(u32, semantics, operand("LiteralContextDependentNumber", 896)),
        Instruction.Constant(f32, initial, operand("LiteralContextDependentNumber", initialBits)),
        Instruction.Constant(f32, minimum, operand("LiteralContextDependentNumber", minimumBits)),
        Instruction.Constant(f32, maximum, operand("LiteralContextDependentNumber", maximumBits)),
        Instruction.Constant(u32, one, operand("LiteralContextDependentNumber", 1)),
        Instruction.Constant(u32, two, operand("LiteralContextDependentNumber", 2)),
        Instruction.EntryPoint(
          operand("ExecutionModel", 6),
          function,
          stringOperand("atomic_float_min_max"),
          Vec.empty
        ),
        Instruction.Function(voidType, function, operand("FunctionControl", 0), functionTy),
        Instruction.FunctionParameter(pointer, target),
        Instruction.Label(operand("IdResult", 19)),
        Instruction.AtomicStore(target, scope, semantics, initial),
        Instruction.AtomicFMinEXT(f32, oldMinimum, target, scope, semantics, minimum),
        Instruction.AtomicFMaxEXT(f32, oldMaximum, target, scope, semantics, maximum),
        Instruction.PtrAccessChain(pointer, minOutput, target, one, Vec.empty),
        Instruction.Store(minOutput, oldMinimum, None),
        Instruction.PtrAccessChain(pointer, maxOutput, target, two, Vec.empty),
        Instruction.Store(maxOutput, oldMaximum, None),
        Instruction.Return(),
        Instruction.FunctionEnd()
      ),
      Endianness.Little
    )
    val allocation = AllocationId(122L)
    val memory     = ArrayMemory(allocation, 12)
    val dispatch   = KernelDispatch
      .validated(
        ProgramId(122L),
        KernelName("atomic_float_min_max"),
        Dimensions.One,
        Dimensions.One,
        Vec(KernelArgument.Allocation(allocation, 0L))
      )
      .toOption
      .get

    assertEquals(Interpreter.runUnchecked(module, dispatch, memory), Right(()), "floating atomic min/max execution")
    assertEquals(
      memory.bytes,
      LittleEndian.toVec(maximumBits, ScalarByteWidth(4)) ++
        LittleEndian.toVec(initialBits, ScalarByteWidth(4)) ++ LittleEndian.toVec(minimumBits, ScalarByteWidth(4))
    )
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
        Instruction.TypePointer(pointerType, operand("StorageClass", 5), u32),
        Instruction.TypePointer(pointerPointer, operand("StorageClass", 5), pointerType),
        Instruction.TypeFunction(functionType, voidType, Vec(pointerPointer, pointerType)),
        Instruction.EntryPoint(operand("ExecutionModel", 6), function, stringOperand("store_pointer"), Vec.empty),
        Instruction.Function(voidType, function, operand("FunctionControl", 0), functionType),
        Instruction.FunctionParameter(pointerPointer, target),
        Instruction.FunctionParameter(pointerType, source),
        Instruction.Label(operand("IdResult", 9)),
        Instruction.Store(target, source, None),
        Instruction.Return(),
        Instruction.FunctionEnd()
      ),
      Endianness.Little
    )
    val allocation = AllocationId(44L)
    val relocated  = AtomicReference(Option.empty[PointerRelocation])
    val memory     = ArrayMemory(allocation, 16, value => relocated.set(Some(value)))
    val dispatch   = KernelDispatch
      .validated(
        ProgramId(44L),
        KernelName("store_pointer"),
        Dimensions.One,
        Dimensions.One,
        Vec(KernelArgument.Allocation(allocation, 8L), KernelArgument.Allocation(allocation, 0L))
      )
      .toOption
      .get

    assertEquals(Interpreter.runUnchecked(module, dispatch, memory), Right(()))
    assertEquals(
      relocated.get(),
      Some(PointerRelocation(RelocationSource.Allocation(allocation), 8L, allocation, 0L))
    )
    assert(memory.bytes.slice(8, 16).exists(_ != 0))
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
      Instruction.TypePointer(genericPointer, operand("StorageClass", 8), u8),
      Instruction.TypePointer(globalBytePointer, operand("StorageClass", 5), u8),
      Instruction.TypePointer(outputPointer, operand("StorageClass", 5), u64),
      Instruction.TypeFunction(functionType, voidType, Vec(outputPointer)),
      Instruction.ConstantNull(genericPointer, nullPointer),
      Instruction.TypePointer(genericU64Pointer, operand("StorageClass", 8), u64),
      Instruction.Constant(u64, zero, operand("LiteralContextDependentNumber", 0)),
      Instruction.EntryPoint(operand("ExecutionModel", 6), function, stringOperand("null_pointer"), Vec.empty),
      Instruction.Function(voidType, function, operand("FunctionControl", 0), functionType),
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
    val module     = Module(Header(ISA.version, Word(0), Word(21), Word(0)), instructions, Endianness.Little)
    val allocation = AllocationId(23L)
    val memory     = ArrayMemory(allocation, 8)
    val dispatch   = KernelDispatch
      .validated(
        ProgramId(23L),
        KernelName("null_pointer"),
        Dimensions.One,
        Dimensions.One,
        Vec(KernelArgument.Allocation(allocation, 0L))
      )
      .toOption
      .get

    assertEquals(Interpreter.runUnchecked(module, dispatch, memory), Right(()))
    assertEquals(memory.bytes, Vec.fill(8)(0.toByte))

    val invalidNull =
      module.copy(instructions = instructions.updated(7, Instruction.ConstantNull(voidType, nullPointer)))
    Interpreter.runUnchecked(invalidNull, dispatch, memory) match {
      case Left(ExecutionFailure.InvalidRequest(message)) =>
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
    Interpreter.runUnchecked(invalidCast, dispatch, memory) match {
      case Left(ExecutionFailure.InvalidRequest(message)) =>
        assertEquals(message, "OpGenericCastToPtr cannot cast storage class 5 pointee %2 to storage class 5 pointee %2")
      case result => fail(s"expected typed invalid generic cast failure, found $result")
    }
  }

  test("dispatch rendezvouses subgroup and workgroup collectives") {
    val voidType       = operand("IdResult", 1)
    val u32            = operand("IdResult", 2)
    val u64            = operand("IdResult", 3)
    val bool           = operand("IdResult", 4)
    val vector3        = operand("IdResult", 5)
    val builtinType    = operand("IdResult", 6)
    val outputType     = operand("IdResult", 7)
    val functionType   = operand("IdResult", 8)
    val subgroupScope  = operand("IdResult", 9)
    val workgroupScope = operand("IdResult", 10)
    val three          = operand("IdResult", 11)
    val localBuiltin   = operand("IdResult", 12)
    val function       = operand("IdResult", 13)
    val output         = operand("IdResult", 14)
    val localVector    = operand("IdResult", 16)
    val localX         = operand("IdResult", 17)
    val pred           = operand("IdResult", 18)
    val any            = operand("IdResult", 19)
    val minimum        = operand("IdResult", 20)
    val trueValue      = operand("IdResult", 21)
    val notAll         = operand("IdResult", 22)
    val instructions   = Vec(
      Instruction.TypeVoid(voidType),
      Instruction.TypeInt(u32, operand("LiteralInteger", 32), operand("LiteralInteger", 0)),
      Instruction.TypeInt(u64, operand("LiteralInteger", 64), operand("LiteralInteger", 0)),
      Instruction.TypeBool(bool),
      Instruction.TypeVector(vector3, u64, operand("LiteralInteger", 3)),
      Instruction.TypePointer(builtinType, operand("StorageClass", 1), vector3),
      Instruction.TypePointer(outputType, operand("StorageClass", 5), u64),
      Instruction.TypeFunction(functionType, voidType, Vec(outputType)),
      Instruction.Constant(u32, subgroupScope, operand("LiteralContextDependentNumber", 3)),
      Instruction.Constant(u32, workgroupScope, operand("LiteralContextDependentNumber", 2)),
      Instruction.Constant(u64, three, operand("LiteralContextDependentNumber", 3)),
      Instruction.ConstantTrue(bool, trueValue),
      Instruction.Variable(builtinType, localBuiltin, operand("StorageClass", 1), None),
      Instruction.Decorate(localBuiltin, operand("Decoration", 11, 27)),
      Instruction.EntryPoint(operand("ExecutionModel", 6), function, stringOperand("collective"), Vec(localBuiltin)),
      Instruction.Function(voidType, function, operand("FunctionControl", 0), functionType),
      Instruction.FunctionParameter(outputType, output),
      Instruction.Label(operand("IdResult", 15)),
      Instruction.Load(vector3, localVector, localBuiltin, None),
      Instruction.CompositeExtract(u64, localX, localVector, Vec(operand("LiteralInteger", 0))),
      Instruction.ULessThan(bool, pred, localX, three),
      Instruction.GroupAny(bool, any, subgroupScope, pred),
      Instruction.LogicalNotEqual(bool, notAll, any, trueValue),
      Instruction.GroupUMin(u64, minimum, workgroupScope, operand("GroupOperation", 0), localX),
      Instruction.Store(output, minimum, None),
      Instruction.Return(),
      Instruction.FunctionEnd()
    )
    val module     = Module(Header(ISA.version, Word(0), Word(24), Word(0)), instructions, Endianness.Little)
    val allocation = AllocationId(24L)
    val memory     = ArrayMemory(allocation, 8)
    val dispatch   = KernelDispatch
      .validated(
        ProgramId(24L),
        KernelName("collective"),
        Dimensions.One,
        Dimensions(8, 1, 1),
        Vec(KernelArgument.Allocation(allocation, 0L))
      )
      .toOption
      .get

    assertEquals(Interpreter.runUnchecked(module, dispatch, memory), Right(()))
    assertEquals(memory.bytes, Vec.fill(8)(0.toByte))

    val workgroupAny = module.copy(instructions =
      instructions.updated(21, Instruction.GroupAny(bool, any, workgroupScope, pred))
    )
    assertEquals(Interpreter.runUnchecked(workgroupAny, dispatch, memory), Right(()))

    val barrierModule = module.copy(instructions =
      instructions.patch(
        24,
        Vec(
          Instruction.ControlBarrier(workgroupScope, workgroupScope, workgroupScope),
          Instruction.ControlBarrier(workgroupScope, workgroupScope, workgroupScope)
        ),
        0
      )
    )
    val barrierMemory   = ArrayMemory(allocation, 8)
    val barrierDispatch = KernelDispatch
      .validated(
        ProgramId(26L),
        KernelName("collective"),
        Dimensions(8, 1, 1),
        Dimensions.One,
        Vec(KernelArgument.Allocation(allocation, 0L))
      )
      .toOption
      .get
    assertEquals(Interpreter.runUnchecked(barrierModule, barrierDispatch, barrierMemory), Right(()))
    assertEquals(barrierMemory.bytes, Vec.fill(8)(0.toByte))

    val scanDestination  = operand("IdResult", 23)
    val broadcast        = operand("IdResult", 24)
    val scanInstructions = instructions
      .updated(
        23,
        Instruction.GroupIAdd(u64, minimum, workgroupScope, operand("GroupOperation", 1), localX)
      )
      .updated(24, Instruction.PtrAccessChain(outputType, scanDestination, output, localX, Vec.empty))
      .patch(25, Vec(Instruction.Store(scanDestination, minimum, None)), 0)
    val scanModule = module.copy(
      instructions = scanInstructions,
      header = module.header.copy(bound = Word(25))
    )
    val scanDispatch = KernelDispatch
      .validated(
        ProgramId(25L),
        KernelName("collective"),
        Dimensions(8, 1, 1),
        Dimensions.One,
        Vec(KernelArgument.Allocation(allocation, 0L))
      )
      .toOption
      .get
    val scanMemory = ArrayMemory(allocation, 64)
    assertEquals(Interpreter.runUnchecked(scanModule, scanDispatch, scanMemory), Right(()))
    assertEquals(
      scanMemory.bytes,
      Vec.from(Vec(0L, 1L, 3L, 6L, 10L, 15L, 21L, 28L).iterator.flatMap(LittleEndian.toVec(_, ScalarByteWidth(8))))
    )

    val reductionModule = scanModule.copy(instructions =
      scanInstructions.updated(
        23,
        Instruction.GroupIAdd(u64, minimum, workgroupScope, operand("GroupOperation", 0), localX)
      )
    )
    val reductionMemory = ArrayMemory(allocation, 64)
    assertEquals(Interpreter.runUnchecked(reductionModule, scanDispatch, reductionMemory), Right(()))
    assertEquals(reductionMemory.bytes, Vec.fill(8)(LittleEndian.toVec(28L, ScalarByteWidth(8))).flatten)

    val multiplicationModule = scanModule.copy(instructions =
      scanInstructions.updated(
        23,
        Instruction.GroupIMulKHR(u64, minimum, workgroupScope, operand("GroupOperation", 0), three)
      )
    )
    val multiplicationMemory = ArrayMemory(allocation, 64)
    assertEquals(Interpreter.runUnchecked(multiplicationModule, scanDispatch, multiplicationMemory), Right(()))
    assertEquals(multiplicationMemory.bytes, Vec.fill(8)(LittleEndian.toVec(6561L, ScalarByteWidth(8))).flatten)

    val f32                      = operand("IdResult", 25)
    val floatThree               = operand("IdResult", 26)
    val floatResult              = operand("IdResult", 27)
    val floatingScanInstructions = scanInstructions
      .patch(
        12,
        Vec(
          Instruction.TypeFloat(f32, operand("LiteralInteger", 32), None),
          Instruction.Constant(
            f32,
            floatThree,
            operand("LiteralContextDependentNumber", java.lang.Float.floatToRawIntBits(3.0f))
          )
        ),
        0
      )
      .updated(
        25,
        Instruction.GroupFAdd(f32, floatResult, workgroupScope, operand("GroupOperation", 1), floatThree)
      )
      .updated(27, Instruction.Store(scanDestination, floatResult, None))
    val floatingScanModule = module.copy(
      instructions = floatingScanInstructions,
      header = module.header.copy(bound = Word(28))
    )
    val floatingScanMemory = ArrayMemory(allocation, 64)
    assertEquals(Interpreter.runUnchecked(floatingScanModule, scanDispatch, floatingScanMemory), Right(()))
    assertEquals(
      floatingScanMemory.bytes,
      Vec.from((1 to 8).iterator.flatMap(index =>
        LittleEndian.toVec(java.lang.Float.floatToRawIntBits(3.0f * index).toLong, ScalarByteWidth(4)) ++
          Vec.fill(4)(0.toByte)
      ))
    )
    val floatingMultiplicationModule = floatingScanModule.copy(instructions =
      floatingScanInstructions.updated(
        25,
        Instruction.GroupFMulKHR(f32, floatResult, workgroupScope, operand("GroupOperation", 1), floatThree)
      )
    )
    val floatingMultiplicationMemory = ArrayMemory(allocation, 64)
    assertEquals(
      Interpreter.runUnchecked(floatingMultiplicationModule, scanDispatch, floatingMultiplicationMemory),
      Right(())
    )
    assertEquals(
      floatingMultiplicationMemory.bytes,
      Vec.from((1 to 8).iterator.flatMap(index =>
        LittleEndian.toVec(
          java.lang.Float.floatToRawIntBits(math.pow(3.0, index).toFloat).toLong,
          ScalarByteWidth(4)
        ) ++
          Vec.fill(4)(0.toByte)
      ))
    )
    assertEquals(
      Interpreter.floatingAddBits(
        FloatingPointWidth(32),
        java.lang.Float.floatToRawIntBits(0.1f).toLong,
        java.lang.Float.floatToRawIntBits(0.2f).toLong
      ),
      java.lang.Float.floatToRawIntBits(0.1f + 0.2f).toLong & 0xffffffffL
    )
    assertEquals(
      Interpreter.floatingMultiplyBits(
        FloatingPointWidth(32),
        java.lang.Float.floatToRawIntBits(0.1f).toLong,
        java.lang.Float.floatToRawIntBits(0.2f).toLong
      ),
      java.lang.Float.floatToRawIntBits(0.1f * 0.2f).toLong & 0xffffffffL
    )

    val exclusiveModule = scanModule.copy(instructions =
      scanInstructions.updated(
        23,
        Instruction.GroupIAdd(u64, minimum, workgroupScope, operand("GroupOperation", 2), localX)
      )
    )
    val exclusiveMemory = ArrayMemory(allocation, 64)
    assertEquals(Interpreter.runUnchecked(exclusiveModule, scanDispatch, exclusiveMemory), Right(()))
    assertEquals(
      exclusiveMemory.bytes,
      Vec.from(Vec(0L, 0L, 1L, 3L, 6L, 10L, 15L, 21L).iterator.flatMap(LittleEndian.toVec(_, ScalarByteWidth(8))))
    )

    val broadcastInstructions = scanInstructions
      .patch(
        24,
        Vec(Instruction.GroupBroadcast(u64, broadcast, workgroupScope, minimum, three)),
        0
      )
      .updated(26, Instruction.Store(scanDestination, broadcast, None))
    val broadcastModule = scanModule.copy(
      instructions = broadcastInstructions,
      header = module.header.copy(bound = Word(25))
    )
    val broadcastMemory = ArrayMemory(allocation, 64)
    assertEquals(Interpreter.runUnchecked(broadcastModule, scanDispatch, broadcastMemory), Right(()))
    assertEquals(broadcastMemory.bytes, Vec.fill(8)(LittleEndian.toVec(6L, ScalarByteWidth(8))).flatten)

    val subgroupBroadcastModule = broadcastModule.copy(instructions =
      broadcastInstructions.updated(
        8,
        Instruction.Constant(u32, subgroupScope, operand("LiteralContextDependentNumber", 3))
      )
    )
    val subgroupBroadcastDispatch = KernelDispatch
      .validated(
        ProgramId(27L),
        KernelName("collective"),
        Dimensions(16, 1, 1),
        Dimensions.One,
        Vec(KernelArgument.Allocation(allocation, 0L))
      )
      .toOption
      .get
    val subgroupBroadcastMemory = ArrayMemory(allocation, 128)
    assertEquals(
      Interpreter.runUnchecked(subgroupBroadcastModule, subgroupBroadcastDispatch, subgroupBroadcastMemory),
      Right(())
    )
    assertEquals(subgroupBroadcastMemory.bytes, Vec.fill(16)(LittleEndian.toVec(6L, ScalarByteWidth(8))).flatten)

    val invalidScope = module.copy(instructions =
      instructions
        .updated(8, Instruction.Constant(u32, subgroupScope, operand("LiteralContextDependentNumber", 1)))
        .updated(21, Instruction.GroupAny(bool, any, subgroupScope, pred))
    )
    Interpreter.runUnchecked(invalidScope, dispatch, memory) match {
      case Left(ExecutionFailure.Unsupported(message)) =>
        assertEquals(message, "OpGroupAny execution scope 1 is not Subgroup or Workgroup")
      case result => fail(s"expected typed unsupported collective scope failure, found $result")
    }

    val malformedLogical = module.copy(instructions =
      instructions.updated(22, Instruction.LogicalNotEqual(u64, notAll, any, trueValue))
    )
    Interpreter.runUnchecked(malformedLogical, dispatch, memory) match {
      case Left(ExecutionFailure.InvalidRequest(message)) =>
        assertEquals(message, "OpLogicalNotEqual requires matching boolean scalar or vector operand and result types")
      case result => fail(s"expected typed malformed logical operation failure, found $result")
    }
  }

  test("dispatch resolves subgroup ballots") {
    val voidType      = operand("IdResult", 1)
    val u32           = operand("IdResult", 2)
    val u64           = operand("IdResult", 3)
    val bool          = operand("IdResult", 4)
    val vector3       = operand("IdResult", 5)
    val ballotType    = operand("IdResult", 6)
    val builtinType   = operand("IdResult", 7)
    val outputType    = operand("IdResult", 8)
    val functionType  = operand("IdResult", 9)
    val subgroupScope = operand("IdResult", 10)
    val three         = operand("IdResult", 11)
    val localBuiltin  = operand("IdResult", 12)
    val function      = operand("IdResult", 13)
    val output        = operand("IdResult", 14)
    val label         = operand("IdResult", 15)
    val localVector   = operand("IdResult", 16)
    val localX        = operand("IdResult", 17)
    val predicate     = operand("IdResult", 18)
    val ballot        = operand("IdResult", 19)
    val firstWord     = operand("IdResult", 20)
    val module        = Module(
      Header(ISA.version, Word(0), Word(21), Word(0)),
      Vec(
        Instruction.TypeVoid(voidType),
        Instruction.TypeInt(u32, operand("LiteralInteger", 32), operand("LiteralInteger", 0)),
        Instruction.TypeInt(u64, operand("LiteralInteger", 64), operand("LiteralInteger", 0)),
        Instruction.TypeBool(bool),
        Instruction.TypeVector(vector3, u64, operand("LiteralInteger", 3)),
        Instruction.TypeVector(ballotType, u32, operand("LiteralInteger", 4)),
        Instruction.TypePointer(builtinType, operand("StorageClass", 1), vector3),
        Instruction.TypePointer(outputType, operand("StorageClass", 5), u32),
        Instruction.TypeFunction(functionType, voidType, Vec(outputType)),
        Instruction.Constant(u32, subgroupScope, operand("LiteralContextDependentNumber", 3)),
        Instruction.Constant(u64, three, operand("LiteralContextDependentNumber", 3)),
        Instruction.Variable(builtinType, localBuiltin, operand("StorageClass", 1), None),
        Instruction.Decorate(localBuiltin, operand("Decoration", 11, 27)),
        Instruction.EntryPoint(operand("ExecutionModel", 6), function, stringOperand("ballot"), Vec(localBuiltin)),
        Instruction.Function(voidType, function, operand("FunctionControl", 0), functionType),
        Instruction.FunctionParameter(outputType, output),
        Instruction.Label(label),
        Instruction.Load(vector3, localVector, localBuiltin, None),
        Instruction.CompositeExtract(u64, localX, localVector, Vec(operand("LiteralInteger", 0))),
        Instruction.ULessThan(bool, predicate, localX, three),
        Instruction.GroupNonUniformBallot(ballotType, ballot, subgroupScope, predicate),
        Instruction.CompositeExtract(u32, firstWord, ballot, Vec(operand("LiteralInteger", 0))),
        Instruction.Store(output, firstWord, None),
        Instruction.Return(),
        Instruction.FunctionEnd()
      ),
      Endianness.Little
    )
    val allocation = AllocationId(125L)
    val memory     = ArrayMemory(allocation, 4)
    val dispatch   = KernelDispatch
      .validated(
        ProgramId(125L),
        KernelName("ballot"),
        Dimensions(8, 1, 1),
        Dimensions.One,
        Vec(KernelArgument.Allocation(allocation, 0L))
      )
      .toOption
      .get

    assertEquals(Interpreter.runUnchecked(module, dispatch, memory), Right(()))
    assertEquals(memory.bytes, LittleEndian.toVec(7, ScalarByteWidth(4)))
  }

  test("dispatch retains per-lane state only for entry points that reach collectives") {
    val voidType           = operand("IdResult", 1)
    val u32                = operand("IdResult", 2)
    val functionType       = operand("IdResult", 3)
    val workgroupScope     = operand("IdResult", 4)
    val plainFunction      = operand("IdResult", 5)
    val plainLabel         = operand("IdResult", 6)
    val collectiveFunction = operand("IdResult", 7)
    val collectiveLabel    = operand("IdResult", 8)
    val callResult         = operand("IdResult", 9)
    val collectiveHelper   = operand("IdResult", 10)
    val helperLabel        = operand("IdResult", 11)
    val module             = Module(
      Header(ISA.version, Word(0), Word(12), Word(0)),
      Vec(
        Instruction.TypeVoid(voidType),
        Instruction.TypeInt(u32, operand("LiteralInteger", 32), operand("LiteralInteger", 0)),
        Instruction.TypeFunction(functionType, voidType, Vec.empty),
        Instruction.Constant(u32, workgroupScope, operand("LiteralContextDependentNumber", 2)),
        Instruction.EntryPoint(operand("ExecutionModel", 6), plainFunction, stringOperand("plain"), Vec.empty),
        Instruction.EntryPoint(
          operand("ExecutionModel", 6),
          collectiveFunction,
          stringOperand("collective"),
          Vec.empty
        ),
        Instruction.Function(voidType, plainFunction, operand("FunctionControl", 0), functionType),
        Instruction.Label(plainLabel),
        Instruction.Return(),
        Instruction.FunctionEnd(),
        Instruction.Function(voidType, collectiveFunction, operand("FunctionControl", 0), functionType),
        Instruction.Label(collectiveLabel),
        Instruction.FunctionCall(voidType, callResult, collectiveHelper, Vec.empty),
        Instruction.Return(),
        Instruction.FunctionEnd(),
        Instruction.Function(voidType, collectiveHelper, operand("FunctionControl", 0), functionType),
        Instruction.Label(helperLabel),
        Instruction.ControlBarrier(workgroupScope, workgroupScope, workgroupScope),
        Instruction.Return(),
        Instruction.FunctionEnd()
      ),
      Endianness.Little
    )
    val programId  = ProgramId(240L)
    val allocation = AllocationId(240L)
    val geometry   = Dimensions(8, 1, 1)
    val prepared   = Interpreter.prepareUnchecked(module).toOption.get

    def dispatch(name: KernelName): KernelDispatch =
      KernelDispatch.validated(programId, name, geometry, Dimensions.One, Vec.empty).toOption.get

    val plainMetrics = Performance.Metrics()
    assertEquals(
      Interpreter.run(prepared, dispatch(KernelName("plain")), ArrayMemory(allocation, 0), plainMetrics),
      Right(())
    )
    assertEquals(plainMetrics.snapshot.invocations, 8L)
    assertEquals(plainMetrics.snapshot.invocationValueInstances, 1L)
    assertEquals(plainMetrics.snapshot.collectiveDispatches, 0L)

    val collectiveMetrics = Performance.Metrics()
    assertEquals(
      Interpreter.run(prepared, dispatch(KernelName("collective")), ArrayMemory(allocation, 0), collectiveMetrics),
      Right(())
    )
    assertEquals(collectiveMetrics.snapshot.invocations, 8L)
    assertEquals(collectiveMetrics.snapshot.invocationValueInstances, 8L)
    assertEquals(collectiveMetrics.snapshot.collectiveDispatches, 1L)
    assertEquals(collectiveMetrics.snapshot.estimatedDenseValueBytes, 8L * 12L * 16L)
  }

}
