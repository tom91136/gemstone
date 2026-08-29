package net.kurobako.gemstone.isa.spirv.v1_6

import net.kurobako.gemstone.Col

import net.kurobako.gemstone.binary.ByteRange
import net.kurobako.gemstone.binary.LittleEndian
import net.kurobako.gemstone.execution.ExecutionFailure
import net.kurobako.gemstone.execution.ExecutionControl
import net.kurobako.gemstone.execution.ExecutionLimits
import net.kurobako.gemstone.execution.ExecutionMemory
import net.kurobako.gemstone.execution.AllocationId
import net.kurobako.gemstone.execution.Dimensions
import net.kurobako.gemstone.execution.KernelArgument
import net.kurobako.gemstone.execution.KernelDispatch
import net.kurobako.gemstone.execution.ProgramId

import java.util.concurrent.atomic.AtomicReference

final class ExecutionSuite extends munit.FunSuite {
  test("dispatch binds built-ins and typed arguments without exposing native pointers") {
    val u32          = operand("IdResult", 2)
    val u64          = operand("IdResult", 3)
    val globalId     = operand("IdResult", 8)
    val function     = operand("IdResult", 9)
    val output       = operand("IdResult", 10)
    val fillValue    = operand("IdResult", 11)
    val loadedGlobal = operand("IdResult", 13)
    val linearId     = operand("IdResult", 14)
    val destination  = operand("IdResult", 15)
    val module       = Module(
      Header(ISA.version, Word(0), Word(32), Word(0)),
      Col(
        Instruction.TypeVoid(operand("IdResult", 1)),
        Instruction.TypeInt(u32, operand("LiteralInteger", 32), operand("LiteralInteger", 0)),
        Instruction.TypeInt(u64, operand("LiteralInteger", 64), operand("LiteralInteger", 0)),
        Instruction.TypeVector(operand("IdResult", 4), u64, operand("LiteralInteger", 3)),
        Instruction.TypePointer(operand("IdResult", 5), operand("StorageClass", 1), operand("IdRef", 4)),
        Instruction.TypePointer(operand("IdResult", 6), operand("StorageClass", 5), u32),
        Instruction.TypeFunction(operand("IdResult", 7), operand("IdRef", 1), Col(operand("IdRef", 6), u32)),
        Instruction.Variable(operand("IdResultType", 5), globalId, operand("StorageClass", 1), None),
        Instruction.Decorate(globalId, operand("Decoration", 11, 28)),
        Instruction.EntryPoint(operand("ExecutionModel", 6), function, stringOperand("fill"), Col(globalId)),
        Instruction.Function(operand("IdResultType", 1), function, operand("FunctionControl", 0), operand("IdRef", 7)),
        Instruction.FunctionParameter(operand("IdResultType", 6), output),
        Instruction.FunctionParameter(operand("IdResultType", 2), fillValue),
        Instruction.Label(operand("IdResult", 12)),
        Instruction.Load(operand("IdResultType", 4), loadedGlobal, globalId, None),
        Instruction.CompositeExtract(u64, linearId, loadedGlobal, Col(operand("LiteralInteger", 0))),
        Instruction.PtrAccessChain(operand("IdResultType", 6), destination, output, linearId, Col.empty),
        Instruction.Store(destination, fillValue, None),
        Instruction.Return(),
        Instruction.FunctionEnd()
      ),
      Endianness.Little
    )
    val allocation = AllocationId.fromLong(1L).toOption.get
    val memory     = ArrayMemory(allocation, 40)
    val dispatch   = KernelDispatch
      .create(
        ProgramId.fromLong(1L).toOption.get,
        "fill",
        Dimensions.create(4, 1, 1).toOption.get,
        Dimensions.create(2, 1, 1).toOption.get,
        Col(KernelArgument.Allocation(allocation, 4L), KernelArgument.Scalar(LittleEndian.toCol(0x12345678L, 4)))
      )
      .toOption
      .get

    val prepared = Interpreter.prepare(module).toOption.get
    val limited  = ExecutionControl.create(ExecutionLimits(maxInstructions = 1L, quantum = 1))
    assert(
      Interpreter.run(prepared, dispatch, memory, limited).left.exists(_.message.contains("instruction budget of 1"))
    )
    assertEquals(Interpreter.run(module, dispatch, memory), Right(()))
    assertEquals(memory.bytes.take(4), Col.fill(4)(0.toByte))
    assertEquals(memory.bytes.slice(4, 36), Col.fill(8)(LittleEndian.toCol(0x12345678L, 4)).flatten)
    assertEquals(memory.bytes.drop(36), Col.fill(4)(0.toByte))

    val workgroupSizeModule = module.copy(instructions =
      module.instructions.updated(
        8,
        Instruction.Decorate(globalId, operand("Decoration", 11, 25))
      )
    )
    val workgroupAllocation = AllocationId.fromLong(17L).toOption.get
    val workgroupMemory     = ArrayMemory(workgroupAllocation, 40)
    val workgroupDispatch   = KernelDispatch
      .create(
        ProgramId.fromLong(17L).toOption.get,
        "fill",
        Dimensions.create(4, 1, 1).toOption.get,
        Dimensions.create(2, 1, 1).toOption.get,
        Col(
          KernelArgument.Allocation(workgroupAllocation, 4L),
          KernelArgument.Scalar(LittleEndian.toCol(0x12345678L, 4))
        )
      )
      .toOption
      .get
    assertEquals(Interpreter.run(workgroupSizeModule, workgroupDispatch, workgroupMemory), Right(()))
    assertEquals(workgroupMemory.bytes.slice(20, 24), LittleEndian.toCol(0x12345678L, 4))
    assertEquals(
      workgroupMemory.bytes.take(20) ++ workgroupMemory.bytes.drop(24),
      Col.fill(36)(0.toByte)
    )

    val malformed = module.copy(instructions =
      module.instructions.updated(
        3,
        Instruction.TypeVector(operand("IdResult", 4), u64, operand("LiteralInteger", 2))
      )
    )
    Interpreter.run(malformed, dispatch, memory) match {
      case Left(ExecutionFailure.InvalidRequest(message)) =>
        assertEquals(message, "SPIR-V BuiltIn 28 requires 3 integer lanes, result type %4 has 2")
      case result => fail(s"expected typed malformed BuiltIn shape failure, found $result")
    }
  }

  test("dispatch honors explicit subgroup size and materializes subgroup built-ins") {
    val voidType           = operand("IdResult", 1)
    val u32                = operand("IdResult", 2)
    val u64                = operand("IdResult", 3)
    val vector3            = operand("IdResult", 4)
    val globalIdType       = operand("IdResult", 5)
    val subgroupIdType     = operand("IdResult", 6)
    val outputType         = operand("IdResult", 7)
    val functionType       = operand("IdResult", 8)
    val globalId           = operand("IdResult", 9)
    val subgroupId         = operand("IdResult", 10)
    val function           = operand("IdResult", 11)
    val output             = operand("IdResult", 12)
    val loadedGlobalId     = operand("IdResult", 14)
    val localLinearId      = operand("IdResult", 15)
    val loadedSubgroupId   = operand("IdResult", 16)
    val destination        = operand("IdResult", 17)
    val undefValue         = operand("IdResult", 18)
    val subgroupDecoration = 11
    val instructions       = Col(
      Instruction.TypeVoid(voidType),
      Instruction.TypeInt(u32, operand("LiteralInteger", 32), operand("LiteralInteger", 0)),
      Instruction.TypeInt(u64, operand("LiteralInteger", 64), operand("LiteralInteger", 0)),
      Instruction.TypeVector(vector3, u64, operand("LiteralInteger", 3)),
      Instruction.TypePointer(globalIdType, operand("StorageClass", 1), vector3),
      Instruction.TypePointer(subgroupIdType, operand("StorageClass", 1), u32),
      Instruction.TypePointer(outputType, operand("StorageClass", 5), u32),
      Instruction.TypeFunction(functionType, voidType, Col(outputType)),
      Instruction.Variable(globalIdType, globalId, operand("StorageClass", 1), None),
      Instruction.Variable(subgroupIdType, subgroupId, operand("StorageClass", 1), None),
      Instruction.Decorate(globalId, operand("Decoration", 11, 28)),
      Instruction.Decorate(subgroupId, operand("Decoration", 11, 40)),
      Instruction.Undef(u32, undefValue),
      Instruction.EntryPoint(
        operand("ExecutionModel", 6),
        function,
        stringOperand("subgroups"),
        Col(globalId, subgroupId)
      ),
      Instruction.ExecutionMode(function, operand("ExecutionMode", 35, 32)),
      Instruction.Function(voidType, function, operand("FunctionControl", 0), functionType),
      Instruction.FunctionParameter(outputType, output),
      Instruction.Label(operand("IdResult", 13)),
      Instruction.Load(vector3, loadedGlobalId, globalId, None),
      Instruction.CompositeExtract(u64, localLinearId, loadedGlobalId, Col(operand("LiteralInteger", 0))),
      Instruction.Load(u32, loadedSubgroupId, subgroupId, None),
      Instruction.PtrAccessChain(outputType, destination, output, localLinearId, Col.empty),
      Instruction.Store(destination, loadedSubgroupId, None),
      Instruction.Return(),
      Instruction.FunctionEnd()
    )
    val module     = Module(Header(ISA.version, Word(0), Word(19), Word(0)), instructions, Endianness.Little)
    val allocation = AllocationId.fromLong(31L).toOption.get
    val dispatch   = KernelDispatch
      .create(
        ProgramId.fromLong(31L).toOption.get,
        "subgroups",
        Dimensions.create(64, 1, 1).toOption.get,
        Dimensions.One,
        Col(KernelArgument.Allocation(allocation, 0L))
      )
      .toOption
      .get
    val memory = ArrayMemory(allocation, 256)

    assertEquals(Interpreter.run(module, dispatch, memory), Right(()))
    assertEquals(
      memory.bytes,
      Col.fill(32)(LittleEndian.toCol(0L, 4)).flatten ++ Col.fill(32)(LittleEndian.toCol(1L, 4)).flatten
    )

    val numSubgroupsModule = module.copy(instructions =
      instructions.updated(
        subgroupDecoration,
        Instruction.Decorate(subgroupId, operand("Decoration", 11, 38))
      )
    )
    val numSubgroupsMemory = ArrayMemory(allocation, 256)
    assertEquals(Interpreter.run(numSubgroupsModule, dispatch, numSubgroupsMemory), Right(()))
    assertEquals(numSubgroupsMemory.bytes, Col.fill(64)(LittleEndian.toCol(2L, 4)).flatten)

    val undefModule = module.copy(instructions =
      instructions.updated(22, Instruction.Store(destination, undefValue, None))
    )
    val undefMemory = ArrayMemory(allocation, 256)
    assertEquals(Interpreter.run(undefModule, dispatch, undefMemory), Right(()))
    assertEquals(undefMemory.bytes, Col.fill(256)(0.toByte))
  }

  test("dispatch materializes composite-initialized CrossWorkgroup globals") {
    val voidType     = operand("IdResult", 1)
    val u32          = operand("IdResult", 2)
    val length       = operand("IdResult", 3)
    val arrayType    = operand("IdResult", 4)
    val globalType   = operand("IdResult", 5)
    val pointerType  = operand("IdResult", 6)
    val functionType = operand("IdResult", 7)
    val first        = operand("IdResult", 8)
    val second       = operand("IdResult", 9)
    val initial      = operand("IdResult", 10)
    val global       = operand("IdResult", 11)
    val function     = operand("IdResult", 12)
    val output       = operand("IdResult", 13)
    val element      = operand("IdResult", 14)
    val loaded       = operand("IdResult", 15)
    val one          = operand("IdResult", 16)
    val module       = Module(
      Header(ISA.version, Word(0), Word(24), Word(0)),
      Col(
        Instruction.TypeVoid(voidType),
        Instruction.TypeInt(u32, operand("LiteralInteger", 32), operand("LiteralInteger", 0)),
        Instruction.Constant(u32, length, operand("LiteralContextDependentNumber", 2)),
        Instruction.TypeArray(arrayType, u32, length),
        Instruction.TypePointer(globalType, operand("StorageClass", 5), arrayType),
        Instruction.TypePointer(pointerType, operand("StorageClass", 5), u32),
        Instruction.TypeFunction(functionType, voidType, Col(pointerType)),
        Instruction.Constant(u32, first, operand("LiteralContextDependentNumber", 17)),
        Instruction.Constant(u32, second, operand("LiteralContextDependentNumber", 29)),
        Instruction.Constant(u32, one, operand("LiteralContextDependentNumber", 1)),
        Instruction.ConstantComposite(arrayType, initial, Col(first, second)),
        Instruction.Variable(globalType, global, operand("StorageClass", 5), Some(initial)),
        Instruction.Decorate(global, operand("Decoration", 22)),
        Instruction.EntryPoint(operand("ExecutionModel", 6), function, stringOperand("global"), Col(global)),
        Instruction.Function(voidType, function, operand("FunctionControl", 0), functionType),
        Instruction.FunctionParameter(pointerType, output),
        Instruction.Label(operand("IdResult", 17)),
        Instruction.AccessChain(pointerType, element, global, Col(one)),
        Instruction.Load(u32, loaded, element, None),
        Instruction.Store(output, loaded, None),
        Instruction.Return(),
        Instruction.FunctionEnd()
      ),
      Endianness.Little
    )
    val allocation = AllocationId.fromLong(20L).toOption.get
    val memory     = ArrayMemory(allocation, 4)
    val dispatch   = KernelDispatch
      .create(
        ProgramId.fromLong(20L).toOption.get,
        "global",
        Dimensions.One,
        Dimensions.One,
        Col(KernelArgument.Allocation(allocation, 0L))
      )
      .toOption
      .get

    assertEquals(Interpreter.run(module, dispatch, memory), Right(()))
    assertEquals(memory.bytes, LittleEndian.toCol(29L, 4))
  }

  test("Workgroup and Private variables are rematerialized at their SPIR-V lifetime boundaries") {
    def module(storageClass: Int, entryPoint: String): Module = {
      val voidType      = operand("IdResult", 1)
      val u32           = operand("IdResult", 2)
      val localPointer  = operand("IdResult", 3)
      val outputPointer = operand("IdResult", 4)
      val functionType  = operand("IdResult", 5)
      val initial       = operand("IdResult", 6)
      val one           = operand("IdResult", 7)
      val variable      = operand("IdResult", 8)
      val function      = operand("IdResult", 9)
      val output        = operand("IdResult", 10)
      val loaded        = operand("IdResult", 12)
      val incremented   = operand("IdResult", 13)
      val instructions  = Col(
        Instruction.TypeVoid(voidType),
        Instruction.TypeInt(u32, operand("LiteralInteger", 32), operand("LiteralInteger", 0)),
        Instruction.TypePointer(localPointer, operand("StorageClass", storageClass), u32),
        Instruction.TypePointer(outputPointer, operand("StorageClass", 5), u32),
        Instruction.TypeFunction(functionType, voidType, Col(outputPointer)),
        Instruction.Constant(u32, initial, operand("LiteralContextDependentNumber", 7)),
        Instruction.Constant(u32, one, operand("LiteralContextDependentNumber", 1)),
        Instruction.Variable(localPointer, variable, operand("StorageClass", storageClass), Some(initial)),
        Instruction.EntryPoint(operand("ExecutionModel", 6), function, stringOperand(entryPoint), Col(variable)),
        Instruction.Function(voidType, function, operand("FunctionControl", 0), functionType),
        Instruction.FunctionParameter(outputPointer, output),
        Instruction.Label(operand("IdResult", 11)),
        Instruction.Load(u32, loaded, variable, None),
        Instruction.Store(output, loaded, None),
        Instruction.IAdd(u32, incremented, loaded, one),
        Instruction.Store(variable, incremented, None),
        Instruction.Return(),
        Instruction.FunctionEnd()
      )
      Module(Header(ISA.version, Word(0), Word(14), Word(0)), instructions, Endianness.Little)
    }

    val allocation        = AllocationId.fromLong(41L).toOption.get
    val workgroupMemory   = ArrayMemory(allocation, 4)
    val workgroupDispatch = KernelDispatch
      .create(
        ProgramId.fromLong(41L).toOption.get,
        "workgroup_lifetime",
        Dimensions.One,
        Dimensions.create(2, 1, 1).toOption.get,
        Col(KernelArgument.Allocation(allocation, 0L))
      )
      .toOption
      .get
    assertEquals(Interpreter.run(module(4, "workgroup_lifetime"), workgroupDispatch, workgroupMemory), Right(()))
    assertEquals(workgroupMemory.bytes, LittleEndian.toCol(7L, 4))

    val privateMemory   = ArrayMemory(allocation, 4)
    val privateDispatch = KernelDispatch
      .create(
        ProgramId.fromLong(42L).toOption.get,
        "private_lifetime",
        Dimensions.create(2, 1, 1).toOption.get,
        Dimensions.One,
        Col(KernelArgument.Allocation(allocation, 0L))
      )
      .toOption
      .get
    assertEquals(Interpreter.run(module(6, "private_lifetime"), privateDispatch, privateMemory), Right(()))
    assertEquals(privateMemory.bytes, LittleEndian.toCol(7L, 4))
  }

  test("explicit ArrayStride and member Offset decorations determine composite byte layout") {
    val voidType       = operand("IdResult", 1)
    val u32            = operand("IdResult", 2)
    val two            = operand("IdResult", 3)
    val arrayType      = operand("IdResult", 4)
    val structureType  = operand("IdResult", 5)
    val outputPointer  = operand("IdResult", 6)
    val functionType   = operand("IdResult", 7)
    val first          = operand("IdResult", 8)
    val second         = operand("IdResult", 9)
    val header         = operand("IdResult", 10)
    val arrayValue     = operand("IdResult", 11)
    val structureValue = operand("IdResult", 12)
    val function       = operand("IdResult", 13)
    val output         = operand("IdResult", 14)
    val module         = Module(
      Header(ISA.version, Word(0), Word(16), Word(0)),
      Col(
        Instruction.TypeVoid(voidType),
        Instruction.TypeInt(u32, operand("LiteralInteger", 32), operand("LiteralInteger", 0)),
        Instruction.Constant(u32, two, operand("LiteralContextDependentNumber", 2)),
        Instruction.TypeArray(arrayType, u32, two),
        Instruction.TypeStruct(structureType, Col(u32, arrayType)),
        Instruction.TypePointer(outputPointer, operand("StorageClass", 5), structureType),
        Instruction.TypeFunction(functionType, voidType, Col(outputPointer)),
        Instruction.Constant(u32, first, operand("LiteralContextDependentNumber", 11)),
        Instruction.Constant(u32, second, operand("LiteralContextDependentNumber", 22)),
        Instruction.Constant(u32, header, operand("LiteralContextDependentNumber", 5)),
        Instruction.ConstantComposite(arrayType, arrayValue, Col(first, second)),
        Instruction.ConstantComposite(structureType, structureValue, Col(header, arrayValue)),
        Instruction.Decorate(arrayType, operand("Decoration", 6, 16)),
        Instruction.MemberDecorate(structureType, operand("LiteralInteger", 1), operand("Decoration", 35, 16)),
        Instruction.EntryPoint(operand("ExecutionModel", 6), function, stringOperand("layout"), Col.empty),
        Instruction.Function(voidType, function, operand("FunctionControl", 0), functionType),
        Instruction.FunctionParameter(outputPointer, output),
        Instruction.Label(operand("IdResult", 15)),
        Instruction.Store(output, structureValue, None),
        Instruction.Return(),
        Instruction.FunctionEnd()
      ),
      Endianness.Little
    )
    val allocation = AllocationId.fromLong(43L).toOption.get
    val memory     = ArrayMemory(allocation, 48)
    val dispatch   = KernelDispatch
      .create(
        ProgramId.fromLong(43L).toOption.get,
        "layout",
        Dimensions.One,
        Dimensions.One,
        Col(KernelArgument.Allocation(allocation, 0L))
      )
      .toOption
      .get

    assertEquals(Interpreter.run(module, dispatch, memory), Right(()))
    val expected = Array.fill[Byte](48)(0)
    LittleEndian.write(5L, expected, 0, 4)
    LittleEndian.write(11L, expected, 16, 4)
    LittleEndian.write(22L, expected, 32, 4)
    assertEquals(memory.bytes, Col.from(expected))
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
    val instructions      = Col(
      Instruction.TypeVoid(voidType),
      Instruction.TypeInt(u8, operand("LiteralInteger", 8), operand("LiteralInteger", 0)),
      Instruction.TypeInt(u64, operand("LiteralInteger", 64), operand("LiteralInteger", 0)),
      Instruction.TypePointer(genericPointer, operand("StorageClass", 8), u8),
      Instruction.TypePointer(globalBytePointer, operand("StorageClass", 5), u8),
      Instruction.TypePointer(outputPointer, operand("StorageClass", 5), u64),
      Instruction.TypeFunction(functionType, voidType, Col(outputPointer)),
      Instruction.ConstantNull(genericPointer, nullPointer),
      Instruction.TypePointer(genericU64Pointer, operand("StorageClass", 8), u64),
      Instruction.Constant(u64, zero, operand("LiteralContextDependentNumber", 0)),
      Instruction.EntryPoint(operand("ExecutionModel", 6), function, stringOperand("null_pointer"), Col.empty),
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
    val allocation = AllocationId.fromLong(23L).toOption.get
    val memory     = ArrayMemory(allocation, 8)
    val dispatch   = KernelDispatch
      .create(
        ProgramId.fromLong(23L).toOption.get,
        "null_pointer",
        Dimensions.One,
        Dimensions.One,
        Col(KernelArgument.Allocation(allocation, 0L))
      )
      .toOption
      .get

    assertEquals(Interpreter.run(module, dispatch, memory), Right(()))
    assertEquals(memory.bytes, Col.fill(8)(0.toByte))

    val invalidNull =
      module.copy(instructions = instructions.updated(7, Instruction.ConstantNull(voidType, nullPointer)))
    Interpreter.run(invalidNull, dispatch, memory) match {
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
    Interpreter.run(invalidCast, dispatch, memory) match {
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
    val instructions   = Col(
      Instruction.TypeVoid(voidType),
      Instruction.TypeInt(u32, operand("LiteralInteger", 32), operand("LiteralInteger", 0)),
      Instruction.TypeInt(u64, operand("LiteralInteger", 64), operand("LiteralInteger", 0)),
      Instruction.TypeBool(bool),
      Instruction.TypeVector(vector3, u64, operand("LiteralInteger", 3)),
      Instruction.TypePointer(builtinType, operand("StorageClass", 1), vector3),
      Instruction.TypePointer(outputType, operand("StorageClass", 5), u64),
      Instruction.TypeFunction(functionType, voidType, Col(outputType)),
      Instruction.Constant(u32, subgroupScope, operand("LiteralContextDependentNumber", 3)),
      Instruction.Constant(u32, workgroupScope, operand("LiteralContextDependentNumber", 2)),
      Instruction.Constant(u64, three, operand("LiteralContextDependentNumber", 3)),
      Instruction.ConstantTrue(bool, trueValue),
      Instruction.Variable(builtinType, localBuiltin, operand("StorageClass", 1), None),
      Instruction.Decorate(localBuiltin, operand("Decoration", 11, 27)),
      Instruction.EntryPoint(operand("ExecutionModel", 6), function, stringOperand("collective"), Col(localBuiltin)),
      Instruction.Function(voidType, function, operand("FunctionControl", 0), functionType),
      Instruction.FunctionParameter(outputType, output),
      Instruction.Label(operand("IdResult", 15)),
      Instruction.Load(vector3, localVector, localBuiltin, None),
      Instruction.CompositeExtract(u64, localX, localVector, Col(operand("LiteralInteger", 0))),
      Instruction.ULessThan(bool, pred, localX, three),
      Instruction.GroupAny(bool, any, subgroupScope, pred),
      Instruction.LogicalNotEqual(bool, notAll, any, trueValue),
      Instruction.GroupUMin(u64, minimum, workgroupScope, operand("GroupOperation", 0), localX),
      Instruction.Store(output, minimum, None),
      Instruction.Return(),
      Instruction.FunctionEnd()
    )
    val module     = Module(Header(ISA.version, Word(0), Word(24), Word(0)), instructions, Endianness.Little)
    val allocation = AllocationId.fromLong(24L).toOption.get
    val memory     = ArrayMemory(allocation, 8)
    val dispatch   = KernelDispatch
      .create(
        ProgramId.fromLong(24L).toOption.get,
        "collective",
        Dimensions.One,
        Dimensions.create(8, 1, 1).toOption.get,
        Col(KernelArgument.Allocation(allocation, 0L))
      )
      .toOption
      .get

    assertEquals(Interpreter.run(module, dispatch, memory), Right(()))
    assertEquals(memory.bytes, Col.fill(8)(0.toByte))

    val workgroupAny = module.copy(instructions =
      instructions.updated(21, Instruction.GroupAny(bool, any, workgroupScope, pred))
    )
    assertEquals(Interpreter.run(workgroupAny, dispatch, memory), Right(()))

    val barrierModule = module.copy(instructions =
      instructions.patch(
        24,
        Col(
          Instruction.ControlBarrier(workgroupScope, workgroupScope, workgroupScope),
          Instruction.ControlBarrier(workgroupScope, workgroupScope, workgroupScope)
        ),
        0
      )
    )
    val barrierMemory   = ArrayMemory(allocation, 8)
    val barrierDispatch = KernelDispatch
      .create(
        ProgramId.fromLong(26L).toOption.get,
        "collective",
        Dimensions.create(8, 1, 1).toOption.get,
        Dimensions.One,
        Col(KernelArgument.Allocation(allocation, 0L))
      )
      .toOption
      .get
    assertEquals(Interpreter.run(barrierModule, barrierDispatch, barrierMemory), Right(()))
    assertEquals(barrierMemory.bytes, Col.fill(8)(0.toByte))

    val scanDestination  = operand("IdResult", 23)
    val broadcast        = operand("IdResult", 24)
    val scanInstructions = instructions
      .updated(
        23,
        Instruction.GroupIAdd(u64, minimum, workgroupScope, operand("GroupOperation", 1), localX)
      )
      .updated(24, Instruction.PtrAccessChain(outputType, scanDestination, output, localX, Col.empty))
      .patch(25, Col(Instruction.Store(scanDestination, minimum, None)), 0)
    val scanModule = module.copy(
      instructions = scanInstructions,
      header = module.header.copy(bound = Word(25))
    )
    val scanDispatch = KernelDispatch
      .create(
        ProgramId.fromLong(25L).toOption.get,
        "collective",
        Dimensions.create(8, 1, 1).toOption.get,
        Dimensions.One,
        Col(KernelArgument.Allocation(allocation, 0L))
      )
      .toOption
      .get
    val scanMemory = ArrayMemory(allocation, 64)
    assertEquals(Interpreter.run(scanModule, scanDispatch, scanMemory), Right(()))
    assertEquals(
      scanMemory.bytes,
      Col.from(Col(0L, 1L, 3L, 6L, 10L, 15L, 21L, 28L).iterator.flatMap(LittleEndian.toCol(_, 8)))
    )

    val reductionModule = scanModule.copy(instructions =
      scanInstructions.updated(
        23,
        Instruction.GroupIAdd(u64, minimum, workgroupScope, operand("GroupOperation", 0), localX)
      )
    )
    val reductionMemory = ArrayMemory(allocation, 64)
    assertEquals(Interpreter.run(reductionModule, scanDispatch, reductionMemory), Right(()))
    assertEquals(reductionMemory.bytes, Col.fill(8)(LittleEndian.toCol(28L, 8)).flatten)

    val multiplicationModule = scanModule.copy(instructions =
      scanInstructions.updated(
        23,
        Instruction.GroupIMulKHR(u64, minimum, workgroupScope, operand("GroupOperation", 0), three)
      )
    )
    val multiplicationMemory = ArrayMemory(allocation, 64)
    assertEquals(Interpreter.run(multiplicationModule, scanDispatch, multiplicationMemory), Right(()))
    assertEquals(multiplicationMemory.bytes, Col.fill(8)(LittleEndian.toCol(6561L, 8)).flatten)

    val f32                      = operand("IdResult", 25)
    val floatThree               = operand("IdResult", 26)
    val floatResult              = operand("IdResult", 27)
    val floatingScanInstructions = instructions
      .patch(
        12,
        Col(
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
      .patch(26, Col.empty[Instruction], 1)
    val floatingScanModule = module.copy(
      instructions = floatingScanInstructions,
      header = module.header.copy(bound = Word(28))
    )
    assertEquals(Interpreter.run(floatingScanModule, scanDispatch, multiplicationMemory), Right(()))
    assertEquals(
      Interpreter.floatingAddBits(
        32,
        java.lang.Float.floatToRawIntBits(0.1f).toLong,
        java.lang.Float.floatToRawIntBits(0.2f).toLong
      ),
      java.lang.Float.floatToRawIntBits(0.1f + 0.2f).toLong & 0xffffffffL
    )

    val exclusiveModule = scanModule.copy(instructions =
      scanInstructions.updated(
        23,
        Instruction.GroupIAdd(u64, minimum, workgroupScope, operand("GroupOperation", 2), localX)
      )
    )
    val exclusiveMemory = ArrayMemory(allocation, 64)
    assertEquals(Interpreter.run(exclusiveModule, scanDispatch, exclusiveMemory), Right(()))
    assertEquals(
      exclusiveMemory.bytes,
      Col.from(Col(0L, 0L, 1L, 3L, 6L, 10L, 15L, 21L).iterator.flatMap(LittleEndian.toCol(_, 8)))
    )

    val broadcastInstructions = scanInstructions
      .patch(
        24,
        Col(Instruction.GroupBroadcast(u64, broadcast, workgroupScope, minimum, three)),
        0
      )
      .updated(26, Instruction.Store(scanDestination, broadcast, None))
    val broadcastModule = scanModule.copy(
      instructions = broadcastInstructions,
      header = module.header.copy(bound = Word(25))
    )
    val broadcastMemory = ArrayMemory(allocation, 64)
    assertEquals(Interpreter.run(broadcastModule, scanDispatch, broadcastMemory), Right(()))
    assertEquals(broadcastMemory.bytes, Col.fill(8)(LittleEndian.toCol(6L, 8)).flatten)

    val invalidScope = module.copy(instructions =
      instructions
        .updated(8, Instruction.Constant(u32, subgroupScope, operand("LiteralContextDependentNumber", 1)))
        .updated(21, Instruction.GroupAny(bool, any, subgroupScope, pred))
    )
    Interpreter.run(invalidScope, dispatch, memory) match {
      case Left(ExecutionFailure.Unsupported(message)) =>
        assertEquals(message, "OpGroupAny execution scope 1 is not Subgroup or Workgroup")
      case result => fail(s"expected typed unsupported collective scope failure, found $result")
    }

    val malformedLogical = module.copy(instructions =
      instructions.updated(22, Instruction.LogicalNotEqual(u64, notAll, any, trueValue))
    )
    Interpreter.run(malformedLogical, dispatch, memory) match {
      case Left(ExecutionFailure.InvalidRequest(message)) =>
        assertEquals(message, "OpLogicalNotEqual requires matching boolean scalar or vector operand and result types")
      case result => fail(s"expected typed malformed logical operation failure, found $result")
    }
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
      Col(
        Instruction.TypeVoid(voidType),
        Instruction.TypeInt(u32, operand("LiteralInteger", 32), operand("LiteralInteger", 0)),
        Instruction.TypePointer(pointerType, operand("StorageClass", 5), u32),
        Instruction.TypeFunction(entryType, voidType, Col(pointerType, u32)),
        Instruction.TypeFunction(helperType, u32, Col(u32)),
        Instruction.Constant(u32, one, operand("LiteralContextDependentNumber", 1)),
        Instruction.EntryPoint(operand("ExecutionModel", 6), entryFunction, stringOperand("call"), Col.empty),
        Instruction.Function(voidType, entryFunction, operand("FunctionControl", 0), entryType),
        Instruction.FunctionParameter(pointerType, output),
        Instruction.FunctionParameter(u32, input),
        Instruction.Label(operand("IdResult", 11)),
        Instruction.FunctionCall(u32, callResult, helperFunction, Col(input)),
        Instruction.Store(output, callResult, None),
        Instruction.Return(),
        Instruction.FunctionEnd(),
        Instruction.Function(u32, helperFunction, operand("FunctionControl", 0), helperType),
        Instruction.FunctionParameter(u32, helperParameter),
        Instruction.Label(operand("IdResult", 15)),
        Instruction.IAdd(u32, helperResult, helperParameter, one),
        Instruction.ReturnValue(helperResult),
        Instruction.FunctionEnd()
      ),
      Endianness.Little
    )
    val allocation = AllocationId.fromLong(21L).toOption.get
    val memory     = ArrayMemory(allocation, 4)
    val dispatch   = KernelDispatch
      .create(
        ProgramId.fromLong(21L).toOption.get,
        "call",
        Dimensions.One,
        Dimensions.One,
        Col(KernelArgument.Allocation(allocation, 0L), KernelArgument.Scalar(LittleEndian.toCol(41L, 4)))
      )
      .toOption
      .get

    assertEquals(Interpreter.run(module, dispatch, memory), Right(()))
    assertEquals(memory.bytes, LittleEndian.toCol(42L, 4))
  }

  test("dispatch executes typed OpenCL.std scalar f64 hypot") {
    val voidType     = operand("IdResult", 1)
    val f64          = operand("IdResult", 2)
    val pointerType  = operand("IdResult", 3)
    val functionType = operand("IdResult", 4)
    val opencl       = operand("IdResult", 5)
    val function     = operand("IdResult", 6)
    val output       = operand("IdResult", 7)
    val left         = operand("IdResult", 8)
    val right        = operand("IdResult", 9)
    val result       = operand("IdResult", 11)
    val module       = Module(
      Header(ISA.version, Word(0), Word(20), Word(0)),
      Col(
        Instruction.ExtInstImport(opencl, stringOperand("OpenCL.std")),
        Instruction.TypeVoid(voidType),
        Instruction.TypeFloat(f64, operand("LiteralInteger", 64), None),
        Instruction.TypePointer(pointerType, operand("StorageClass", 5), f64),
        Instruction.TypeFunction(functionType, voidType, Col(pointerType, f64, f64)),
        Instruction.EntryPoint(operand("ExecutionModel", 6), function, stringOperand("hypot"), Col.empty),
        Instruction.Function(voidType, function, operand("FunctionControl", 0), functionType),
        Instruction.FunctionParameter(pointerType, output),
        Instruction.FunctionParameter(f64, left),
        Instruction.FunctionParameter(f64, right),
        Instruction.Label(operand("IdResult", 10)),
        Instruction.ExtInst(f64, result, opencl, operand("LiteralExtInstInteger", 32), Col(left, right)),
        Instruction.Store(output, result, None),
        Instruction.Return(),
        Instruction.FunctionEnd()
      ),
      Endianness.Little
    )
    val allocation = AllocationId.fromLong(22L).toOption.get
    val memory     = ArrayMemory(allocation, 8)
    val dispatch   = KernelDispatch
      .create(
        ProgramId.fromLong(22L).toOption.get,
        "hypot",
        Dimensions.One,
        Dimensions.One,
        Col(
          KernelArgument.Allocation(allocation, 0L),
          KernelArgument.Scalar(LittleEndian.toCol(java.lang.Double.doubleToRawLongBits(3.0), 8)),
          KernelArgument.Scalar(LittleEndian.toCol(java.lang.Double.doubleToRawLongBits(4.0), 8))
        )
      )
      .toOption
      .get

    assertEquals(Interpreter.run(module, dispatch, memory), Right(()))
    assertEquals(memory.bytes, LittleEndian.toCol(java.lang.Double.doubleToRawLongBits(5.0), 8))
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
      Col(
        Instruction.TypeVoid(voidType),
        Instruction.TypeInt(u32, operand("LiteralInteger", 32), operand("LiteralInteger", 0)),
        Instruction.TypeBool(operand("IdResult", 18)),
        Instruction.TypePointer(pointerType, operand("StorageClass", 5), u32),
        Instruction.TypeFunction(operand("IdResult", 4), voidType, Col(pointerType, u32)),
        Instruction.Constant(u32, zero, operand("LiteralContextDependentNumber", 0)),
        Instruction.Constant(u32, one, operand("LiteralContextDependentNumber", 1)),
        Instruction.EntryPoint(operand("ExecutionModel", 6), function, stringOperand("sequence"), Col.empty),
        Instruction.Function(voidType, function, operand("FunctionControl", 0), operand("IdRef", 4)),
        Instruction.FunctionParameter(pointerType, output),
        Instruction.FunctionParameter(u32, bound),
        Instruction.Label(entry),
        Instruction.Branch(header),
        Instruction.Label(header),
        Instruction.Phi(u32, index, Col(phiPair(zero, entry), phiPair(next, body))),
        Instruction.LoopMerge(merge, body, operand("LoopControl", 0)),
        Instruction.ULessThan(operand("IdResultType", 18), condition, index, bound),
        Instruction.BranchConditional(condition, body, merge, Col.empty),
        Instruction.Label(body),
        Instruction.PtrAccessChain(pointerType, destination, output, index, Col.empty),
        Instruction.Store(destination, index, None),
        Instruction.IAdd(u32, next, index, one),
        Instruction.Branch(header),
        Instruction.Label(merge),
        Instruction.Return(),
        Instruction.FunctionEnd()
      ),
      Endianness.Little
    )
    val allocation = AllocationId.fromLong(2L).toOption.get
    val memory     = ArrayMemory(allocation, 36)
    val dispatch   = KernelDispatch
      .create(
        ProgramId.fromLong(2L).toOption.get,
        "sequence",
        Dimensions.One,
        Dimensions.One,
        Col(KernelArgument.Allocation(allocation, 4L), KernelArgument.Scalar(LittleEndian.toCol(8L, 4)))
      )
      .toOption
      .get

    val decoded = ISA.decode(ISA.encode(module).toOption.get).toOption.get
    assertEquals(Interpreter.run(decoded, dispatch, memory), Right(()))
    assertEquals(memory.bytes.take(4), Col.fill(4)(0.toByte))
    assertEquals(memory.bytes.drop(4), Col.from((0L until 8L).flatMap(LittleEndian.toCol(_, 4))))
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
    val instructions = Col(
      Instruction.TypeVoid(voidType),
      Instruction.TypeInt(u8, operand("LiteralInteger", 8), operand("LiteralInteger", 0)),
      Instruction.TypeInt(u32, operand("LiteralInteger", 32), operand("LiteralInteger", 0)),
      Instruction.TypePointer(outputType, operand("StorageClass", 5), u32),
      Instruction.TypePointer(localType, operand("StorageClass", 7), u32),
      Instruction.TypePointer(localBytePtr, operand("StorageClass", 7), u8),
      Instruction.TypeFunction(operand("IdResult", 7), voidType, Col(outputType)),
      Instruction.Constant(u32, value, operand("LiteralContextDependentNumber", 0x12345678)),
      Instruction.EntryPoint(operand("ExecutionModel", 6), function, stringOperand("lifetime"), Col.empty),
      Instruction.Function(voidType, function, operand("FunctionControl", 0), operand("IdRef", 7)),
      Instruction.FunctionParameter(outputType, output),
      Instruction.Label(operand("IdResult", 10)),
      Instruction.Variable(localType, local, operand("StorageClass", 7), None),
      Instruction.Bitcast(localBytePtr, bytePointer, local),
      Instruction.LifetimeStart(bytePointer, operand("LiteralInteger", 4)),
      Instruction.Store(local, value, None),
      Instruction.Load(u32, loaded, local, None),
      Instruction.Store(output, loaded, None),
      Instruction.LifetimeStop(bytePointer, operand("LiteralInteger", 4)),
      Instruction.Return(),
      Instruction.FunctionEnd()
    )
    val module     = Module(Header(ISA.version, Word(0), Word(20), Word(0)), instructions, Endianness.Little)
    val allocation = AllocationId.fromLong(3L).toOption.get
    val memory     = ArrayMemory(allocation, 4)
    val dispatch   = KernelDispatch
      .create(
        ProgramId.fromLong(3L).toOption.get,
        "lifetime",
        Dimensions.One,
        Dimensions.One,
        Col(KernelArgument.Allocation(allocation, 0L))
      )
      .toOption
      .get

    assertEquals(Interpreter.run(module, dispatch, memory), Right(()))
    assertEquals(memory.bytes, LittleEndian.toCol(0x12345678L, 4))

    val malformed = module.copy(instructions =
      instructions.updated(
        14,
        Instruction.LifetimeStart(bytePointer, operand("LiteralInteger", 4, 0))
      )
    )
    Interpreter.run(malformed, dispatch, memory) match {
      case Left(ExecutionFailure.InvalidRequest(message)) =>
        assertEquals(message, "OpLifetimeStart size must contain one word, found 2")
      case result => fail(s"expected typed malformed lifetime marker failure, found $result")
    }
  }

  test("dispatch converts signed integer bit patterns to floating-point vectors") {
    val voidType     = operand("IdResult", 1)
    val u32          = operand("IdResult", 2)
    val inputType    = operand("IdResult", 3)
    val f32          = operand("IdResult", 4)
    val resultType   = operand("IdResult", 5)
    val outputType   = operand("IdResult", 6)
    val function     = operand("IdResult", 8)
    val output       = operand("IdResult", 9)
    val input        = operand("IdResult", 10)
    val rhs          = operand("IdResult", 11)
    val converted    = operand("IdResult", 13)
    val added        = operand("IdResult", 14)
    val subtracted   = operand("IdResult", 15)
    val multiplied   = operand("IdResult", 16)
    val integerValue = operand("IdResult", 17)
    val result       = operand("IdResult", 18)
    val divided      = operand("IdResult", 19)
    val instructions = Col(
      Instruction.TypeVoid(voidType),
      Instruction.TypeInt(u32, operand("LiteralInteger", 32), operand("LiteralInteger", 0)),
      Instruction.TypeVector(inputType, u32, operand("LiteralInteger", 2)),
      Instruction.TypeFloat(f32, operand("LiteralInteger", 32), None),
      Instruction.TypeVector(resultType, f32, operand("LiteralInteger", 2)),
      Instruction.TypePointer(outputType, operand("StorageClass", 5), resultType),
      Instruction.TypeFunction(operand("IdResult", 7), voidType, Col(outputType, inputType, resultType)),
      Instruction.EntryPoint(operand("ExecutionModel", 6), function, stringOperand("signed_to_float"), Col.empty),
      Instruction.Function(voidType, function, operand("FunctionControl", 0), operand("IdRef", 7)),
      Instruction.FunctionParameter(outputType, output),
      Instruction.FunctionParameter(inputType, input),
      Instruction.FunctionParameter(resultType, rhs),
      Instruction.Label(operand("IdResult", 12)),
      Instruction.ConvertSToF(resultType, converted, input),
      Instruction.FAdd(resultType, added, converted, rhs),
      Instruction.FSub(resultType, subtracted, added, rhs),
      Instruction.FMul(resultType, multiplied, subtracted, rhs),
      Instruction.FDiv(resultType, divided, multiplied, rhs),
      Instruction.ConvertFToS(inputType, integerValue, divided),
      Instruction.ConvertSToF(resultType, result, integerValue),
      Instruction.Store(output, result, None),
      Instruction.Return(),
      Instruction.FunctionEnd()
    )
    val module     = Module(Header(ISA.version, Word(0), Word(20), Word(0)), instructions, Endianness.Little)
    val allocation = AllocationId.fromLong(4L).toOption.get
    val memory     = ArrayMemory(allocation, 8)
    val dispatch   = KernelDispatch
      .create(
        ProgramId.fromLong(4L).toOption.get,
        "signed_to_float",
        Dimensions.One,
        Dimensions.One,
        Col(
          KernelArgument.Allocation(allocation, 0L),
          KernelArgument.Scalar(LittleEndian.toCol(0xfffffffeL, 4) ++ LittleEndian.toCol(3L, 4)),
          KernelArgument.Scalar(
            LittleEndian.toCol(java.lang.Float.floatToRawIntBits(0.5f).toLong, 4) ++
              LittleEndian.toCol(java.lang.Float.floatToRawIntBits(1.5f).toLong, 4)
          )
        )
      )
      .toOption
      .get

    assertEquals(Interpreter.run(module, dispatch, memory), Right(()))
    assertEquals(
      memory.bytes,
      LittleEndian.toCol(java.lang.Float.floatToRawIntBits(-2.0f).toLong, 4) ++
        LittleEndian.toCol(java.lang.Float.floatToRawIntBits(3.0f).toLong, 4)
    )

    val malformed = module.copy(instructions =
      instructions.updated(
        13,
        Instruction.ConvertSToF(u32, converted, input)
      )
    )
    Interpreter.run(malformed, dispatch, memory) match {
      case Left(ExecutionFailure.InvalidRequest(message)) =>
        assertEquals(message, "OpConvertSToF result type %2 is not a floating-point scalar or vector")
      case result => fail(s"expected typed conversion result failure, found $result")
    }

    val malformedSubtract = module.copy(instructions =
      instructions.updated(
        15,
        Instruction.FSub(u32, subtracted, added, rhs)
      )
    )
    Interpreter.run(malformedSubtract, dispatch, memory) match {
      case Left(ExecutionFailure.InvalidRequest(message)) =>
        assertEquals(message, "OpFSub result type %2 is not a floating-point scalar or vector")
      case value => fail(s"expected typed floating subtraction failure, found $value")
    }

    val malformedAdd = module.copy(instructions =
      instructions.updated(
        14,
        Instruction.FAdd(u32, added, converted, rhs)
      )
    )
    Interpreter.run(malformedAdd, dispatch, memory) match {
      case Left(ExecutionFailure.InvalidRequest(message)) =>
        assertEquals(message, "OpFAdd result type %2 is not a floating-point scalar or vector")
      case value => fail(s"expected typed floating addition failure, found $value")
    }

    val malformedMultiply = module.copy(instructions =
      instructions.updated(
        16,
        Instruction.FMul(u32, multiplied, subtracted, rhs)
      )
    )
    Interpreter.run(malformedMultiply, dispatch, memory) match {
      case Left(ExecutionFailure.InvalidRequest(message)) =>
        assertEquals(message, "OpFMul result type %2 is not a floating-point scalar or vector")
      case value => fail(s"expected typed floating multiplication failure, found $value")
    }

    val malformedFloatToSigned = module.copy(instructions =
      instructions.updated(
        18,
        Instruction.ConvertFToS(resultType, integerValue, divided)
      )
    )
    Interpreter.run(malformedFloatToSigned, dispatch, memory) match {
      case Left(ExecutionFailure.InvalidRequest(message)) =>
        assertEquals(message, "OpConvertFToS result type %5 is not an integer scalar or vector")
      case value => fail(s"expected typed floating-to-signed conversion failure, found $value")
    }

    val malformedDivide = module.copy(instructions =
      instructions.updated(
        17,
        Instruction.FDiv(u32, divided, multiplied, rhs)
      )
    )
    Interpreter.run(malformedDivide, dispatch, memory) match {
      case Left(ExecutionFailure.InvalidRequest(message)) =>
        assertEquals(message, "OpFDiv result type %2 is not a floating-point scalar or vector")
      case value => fail(s"expected typed floating division failure, found $value")
    }

    val halfModule = module.copy(instructions =
      instructions
        .updated(1, Instruction.TypeInt(u32, operand("LiteralInteger", 16), operand("LiteralInteger", 0)))
        .updated(3, Instruction.TypeFloat(f32, operand("LiteralInteger", 16), None))
    )
    val halfAllocation = AllocationId.fromLong(5L).toOption.get
    val halfMemory     = ArrayMemory(halfAllocation, 4)
    val halfDispatch   = KernelDispatch
      .create(
        ProgramId.fromLong(5L).toOption.get,
        "signed_to_float",
        Dimensions.One,
        Dimensions.One,
        Col(
          KernelArgument.Allocation(halfAllocation, 0L),
          KernelArgument.Scalar(LittleEndian.toCol(0L, 2) ++ LittleEndian.toCol(3L, 2)),
          KernelArgument.Scalar(LittleEndian.toCol(0x0001L, 2) ++ LittleEndian.toCol(0x3e00L, 2))
        )
      )
      .toOption
      .get
    assertEquals(Interpreter.run(halfModule, halfDispatch, halfMemory), Right(()))
    assertEquals(halfMemory.bytes, LittleEndian.toCol(0L, 2) ++ LittleEndian.toCol(0x4200L, 2))
  }

  test("dispatch compares integer values with a typed boolean result") {
    val voidType     = operand("IdResult", 1)
    val u32          = operand("IdResult", 2)
    val bool         = operand("IdResult", 3)
    val pointerType  = operand("IdResult", 4)
    val function     = operand("IdResult", 6)
    val output       = operand("IdResult", 7)
    val left         = operand("IdResult", 8)
    val right        = operand("IdResult", 9)
    val remainder    = operand("IdResult", 11)
    val equal        = operand("IdResult", 12)
    val selected     = operand("IdResult", 13)
    val instructions = Col(
      Instruction.TypeVoid(voidType),
      Instruction.TypeInt(u32, operand("LiteralInteger", 32), operand("LiteralInteger", 0)),
      Instruction.TypeBool(bool),
      Instruction.TypePointer(pointerType, operand("StorageClass", 5), u32),
      Instruction.TypeFunction(operand("IdResult", 5), voidType, Col(pointerType, u32, u32)),
      Instruction.EntryPoint(operand("ExecutionModel", 6), function, stringOperand("integer_equal"), Col.empty),
      Instruction.Function(voidType, function, operand("FunctionControl", 0), operand("IdRef", 5)),
      Instruction.FunctionParameter(pointerType, output),
      Instruction.FunctionParameter(u32, left),
      Instruction.FunctionParameter(u32, right),
      Instruction.Label(operand("IdResult", 10)),
      Instruction.SRem(u32, remainder, left, right),
      Instruction.IEqual(bool, equal, remainder, right),
      Instruction.Select(u32, selected, equal, left, remainder),
      Instruction.Store(output, selected, None),
      Instruction.Return(),
      Instruction.FunctionEnd()
    )
    val module     = Module(Header(ISA.version, Word(0), Word(16), Word(0)), instructions, Endianness.Little)
    val allocation = AllocationId.fromLong(6L).toOption.get
    val memory     = ArrayMemory(allocation, 4)
    val dispatch   = KernelDispatch
      .create(
        ProgramId.fromLong(6L).toOption.get,
        "integer_equal",
        Dimensions.One,
        Dimensions.One,
        Col(
          KernelArgument.Allocation(allocation, 0L),
          KernelArgument.Scalar(LittleEndian.toCol(0xfffffff9L, 4)),
          KernelArgument.Scalar(LittleEndian.toCol(3L, 4))
        )
      )
      .toOption
      .get

    assertEquals(Interpreter.run(module, dispatch, memory), Right(()))
    assertEquals(memory.bytes, LittleEndian.toCol(0xffffffffL, 4))

    val divided = module.copy(instructions = instructions.updated(11, Instruction.SDiv(u32, remainder, left, right)))
    assertEquals(Interpreter.run(divided, dispatch, memory), Right(()))
    assertEquals(memory.bytes, LittleEndian.toCol(0xfffffffeL, 4))

    val xor = module.copy(instructions = instructions.updated(11, Instruction.BitwiseXor(u32, remainder, left, right)))
    assertEquals(Interpreter.run(xor, dispatch, memory), Right(()))
    assertEquals(memory.bytes, LittleEndian.toCol(0xfffffffaL, 4))

    val shiftedRight = module.copy(instructions =
      instructions.updated(11, Instruction.ShiftRightLogical(u32, remainder, left, right))
    )
    assertEquals(Interpreter.run(shiftedRight, dispatch, memory), Right(()))
    assertEquals(memory.bytes, LittleEndian.toCol(0x1fffffffL, 4))

    val shiftedLeft = module.copy(instructions =
      instructions.updated(11, Instruction.ShiftLeftLogical(u32, remainder, left, right))
    )
    assertEquals(Interpreter.run(shiftedLeft, dispatch, memory), Right(()))
    assertEquals(memory.bytes, LittleEndian.toCol(0xffffffc8L, 4))

    val not = module.copy(instructions = instructions.updated(11, Instruction.Not(u32, remainder, left)))
    assertEquals(Interpreter.run(not, dispatch, memory), Right(()))
    assertEquals(memory.bytes, LittleEndian.toCol(6L, 4))

    val signedLess =
      module.copy(instructions = instructions.updated(12, Instruction.SLessThan(bool, equal, left, right)))
    assertEquals(Interpreter.run(signedLess, dispatch, memory), Right(()))
    assertEquals(memory.bytes, LittleEndian.toCol(0xfffffff9L, 4))

    val signedGreater =
      module.copy(instructions = instructions.updated(12, Instruction.SGreaterThan(bool, equal, left, right)))
    assertEquals(Interpreter.run(signedGreater, dispatch, memory), Right(()))
    assertEquals(memory.bytes, LittleEndian.toCol(0xffffffffL, 4))

    val malformed = module.copy(instructions =
      instructions.updated(
        12,
        Instruction.IEqual(u32, equal, remainder, right)
      )
    )
    Interpreter.run(malformed, dispatch, memory) match {
      case Left(ExecutionFailure.InvalidRequest(message)) =>
        assertEquals(message, "OpIEqual result type %2 is not a boolean scalar or vector")
      case value => fail(s"expected typed integer equality failure, found $value")
    }

    val malformedRemainder = module.copy(instructions =
      instructions.updated(
        11,
        Instruction.SRem(bool, remainder, left, right)
      )
    )
    Interpreter.run(malformedRemainder, dispatch, memory) match {
      case Left(ExecutionFailure.InvalidRequest(message)) =>
        assertEquals(message, "OpSRem result type %3 is not an integer scalar or vector")
      case value => fail(s"expected typed signed remainder failure, found $value")
    }

    val malformedDivision = module.copy(instructions =
      instructions.updated(
        11,
        Instruction.SDiv(bool, remainder, left, right)
      )
    )
    Interpreter.run(malformedDivision, dispatch, memory) match {
      case Left(ExecutionFailure.InvalidRequest(message)) =>
        assertEquals(message, "OpSDiv result type %3 is not an integer scalar or vector")
      case value => fail(s"expected typed signed division failure, found $value")
    }
  }

  test("dispatch performs ordered floating equality") {
    val voidType     = operand("IdResult", 1)
    val f32          = operand("IdResult", 2)
    val bool         = operand("IdResult", 3)
    val pointerType  = operand("IdResult", 4)
    val function     = operand("IdResult", 6)
    val output       = operand("IdResult", 7)
    val left         = operand("IdResult", 8)
    val right        = operand("IdResult", 9)
    val equal        = operand("IdResult", 11)
    val lessEqual    = operand("IdResult", 12)
    val selected     = operand("IdResult", 13)
    val result       = operand("IdResult", 14)
    val instructions = Col(
      Instruction.TypeVoid(voidType),
      Instruction.TypeFloat(f32, operand("LiteralInteger", 32), None),
      Instruction.TypeBool(bool),
      Instruction.TypePointer(pointerType, operand("StorageClass", 5), f32),
      Instruction.TypeFunction(operand("IdResult", 5), voidType, Col(pointerType, f32, f32)),
      Instruction.EntryPoint(operand("ExecutionModel", 6), function, stringOperand("floating_equal"), Col.empty),
      Instruction.Function(voidType, function, operand("FunctionControl", 0), operand("IdRef", 5)),
      Instruction.FunctionParameter(pointerType, output),
      Instruction.FunctionParameter(f32, left),
      Instruction.FunctionParameter(f32, right),
      Instruction.Label(operand("IdResult", 10)),
      Instruction.FOrdEqual(bool, equal, left, right),
      Instruction.FOrdLessThanEqual(bool, lessEqual, left, right),
      Instruction.Select(f32, selected, equal, left, right),
      Instruction.Select(f32, result, lessEqual, left, selected),
      Instruction.Store(output, result, None),
      Instruction.Return(),
      Instruction.FunctionEnd()
    )
    val module = Module(Header(ISA.version, Word(0), Word(16), Word(0)), instructions, Endianness.Little)

    def run(id: Long, leftBits: Long, rightBits: Long): Col[Byte] = {
      val allocation = AllocationId.fromLong(id).toOption.get
      val memory     = ArrayMemory(allocation, 4)
      val dispatch   = KernelDispatch
        .create(
          ProgramId.fromLong(id).toOption.get,
          "floating_equal",
          Dimensions.One,
          Dimensions.One,
          Col(
            KernelArgument.Allocation(allocation, 0L),
            KernelArgument.Scalar(LittleEndian.toCol(leftBits, 4)),
            KernelArgument.Scalar(LittleEndian.toCol(rightBits, 4))
          )
        )
        .toOption
        .get
      assertEquals(Interpreter.run(module, dispatch, memory), Right(()))
      memory.bytes
    }

    assertEquals(run(7L, 0x80000000L, 0L), LittleEndian.toCol(0x80000000L, 4))
    assertEquals(run(8L, 0x7fc00000L, 0x40400000L), LittleEndian.toCol(0x40400000L, 4))
    assertEquals(run(9L, 0x40000000L, 0x40400000L), LittleEndian.toCol(0x40000000L, 4))

    val malformed = module.copy(instructions =
      instructions.updated(
        11,
        Instruction.FOrdEqual(f32, equal, left, right)
      )
    )
    val allocation = AllocationId.fromLong(10L).toOption.get
    val dispatch   = KernelDispatch
      .create(
        ProgramId.fromLong(10L).toOption.get,
        "floating_equal",
        Dimensions.One,
        Dimensions.One,
        Col(
          KernelArgument.Allocation(allocation, 0L),
          KernelArgument.Scalar(LittleEndian.toCol(0L, 4)),
          KernelArgument.Scalar(LittleEndian.toCol(0L, 4))
        )
      )
      .toOption
      .get
    Interpreter.run(malformed, dispatch, ArrayMemory(allocation, 4)) match {
      case Left(ExecutionFailure.InvalidRequest(message)) =>
        assertEquals(message, "OpFOrdEqual result type %2 is not a boolean scalar or vector")
      case value => fail(s"expected typed ordered equality failure, found $value")
    }

    val malformedLessEqual = module.copy(instructions =
      instructions.updated(
        12,
        Instruction.FOrdLessThanEqual(f32, lessEqual, left, right)
      )
    )
    Interpreter.run(malformedLessEqual, dispatch, ArrayMemory(allocation, 4)) match {
      case Left(ExecutionFailure.InvalidRequest(message)) =>
        assertEquals(message, "OpFOrdLessThanEqual result type %2 is not a boolean scalar or vector")
      case value => fail(s"expected typed ordered less-than-or-equal failure, found $value")
    }
  }

  test("dispatch converts floating-point values to unsigned integers without signed narrowing") {
    val voidType     = operand("IdResult", 1)
    val u64          = operand("IdResult", 2)
    val f64          = operand("IdResult", 3)
    val pointerType  = operand("IdResult", 4)
    val functionType = operand("IdResult", 5)
    val function     = operand("IdResult", 6)
    val output       = operand("IdResult", 7)
    val input        = operand("IdResult", 8)
    val result       = operand("IdResult", 10)
    val instructions = Col(
      Instruction.TypeVoid(voidType),
      Instruction.TypeInt(u64, operand("LiteralInteger", 64), operand("LiteralInteger", 0)),
      Instruction.TypeFloat(f64, operand("LiteralInteger", 64), None),
      Instruction.TypePointer(pointerType, operand("StorageClass", 5), u64),
      Instruction.TypeFunction(functionType, voidType, Col(pointerType, f64)),
      Instruction.EntryPoint(operand("ExecutionModel", 6), function, stringOperand("float_to_unsigned"), Col.empty),
      Instruction.Function(voidType, function, operand("FunctionControl", 0), functionType),
      Instruction.FunctionParameter(pointerType, output),
      Instruction.FunctionParameter(f64, input),
      Instruction.Label(operand("IdResult", 9)),
      Instruction.ConvertFToU(u64, result, input),
      Instruction.Store(output, result, None),
      Instruction.Return(),
      Instruction.FunctionEnd()
    )
    val module = Module(Header(ISA.version, Word(0), Word(11), Word(0)), instructions, Endianness.Little)

    def run(id: Long, value: Double): Either[ExecutionFailure, Col[Byte]] = {
      val allocation = AllocationId.fromLong(id).toOption.get
      val memory     = ArrayMemory(allocation, 8)
      val dispatch   = KernelDispatch
        .create(
          ProgramId.fromLong(id).toOption.get,
          "float_to_unsigned",
          Dimensions.One,
          Dimensions.One,
          Col(
            KernelArgument.Allocation(allocation, 0L),
            KernelArgument.Scalar(LittleEndian.toCol(java.lang.Double.doubleToRawLongBits(value), 8))
          )
        )
        .toOption
        .get
      Interpreter.run(module, dispatch, memory).map(_ => memory.bytes)
    }

    assertEquals(run(13L, 255.75), Right(LittleEndian.toCol(255L, 8)))
    assertEquals(run(14L, java.lang.Math.scalb(1.0, 63)), Right(LittleEndian.toCol(Long.MinValue, 8)))
    run(15L, -1.0) match {
      case Left(ExecutionFailure.InvalidRequest(message)) =>
        assert(message.contains("negative values are outside the unsigned range"))
      case value => fail(s"expected unsigned conversion range failure, found $value")
    }

    val malformed  = module.copy(instructions = instructions.updated(10, Instruction.ConvertFToU(f64, result, input)))
    val allocation = AllocationId.fromLong(16L).toOption.get
    val dispatch   = KernelDispatch
      .create(
        ProgramId.fromLong(16L).toOption.get,
        "float_to_unsigned",
        Dimensions.One,
        Dimensions.One,
        Col(
          KernelArgument.Allocation(allocation, 0L),
          KernelArgument.Scalar(LittleEndian.toCol(java.lang.Double.doubleToRawLongBits(1.0), 8))
        )
      )
      .toOption
      .get
    Interpreter.run(malformed, dispatch, ArrayMemory(allocation, 8)) match {
      case Left(ExecutionFailure.InvalidRequest(message)) =>
        assertEquals(message, "OpConvertFToU result type %3 is not an integer scalar or vector")
      case value => fail(s"expected typed unsigned conversion failure, found $value")
    }
  }

  test("dispatch executes typed 64-bit atomic add and unsigned minimum") {
    val voidType      = operand("IdResult", 1)
    val u32           = operand("IdResult", 2)
    val u64           = operand("IdResult", 3)
    val pointerType   = operand("IdResult", 4)
    val functionType  = operand("IdResult", 5)
    val scope         = operand("IdResult", 6)
    val semantics     = operand("IdResult", 7)
    val addend        = operand("IdResult", 8)
    val minimum       = operand("IdResult", 9)
    val one           = operand("IdResult", 10)
    val two           = operand("IdResult", 11)
    val function      = operand("IdResult", 12)
    val target        = operand("IdResult", 13)
    val addResult     = operand("IdResult", 15)
    val minimumResult = operand("IdResult", 16)
    val addOutput     = operand("IdResult", 17)
    val minimumOutput = operand("IdResult", 18)
    val module        = Module(
      Header(ISA.version, Word(0), Word(20), Word(0)),
      Col(
        Instruction.TypeVoid(voidType),
        Instruction.TypeInt(u32, operand("LiteralInteger", 32), operand("LiteralInteger", 0)),
        Instruction.TypeInt(u64, operand("LiteralInteger", 64), operand("LiteralInteger", 0)),
        Instruction.TypePointer(pointerType, operand("StorageClass", 5), u64),
        Instruction.TypeFunction(functionType, voidType, Col(pointerType)),
        Instruction.Constant(u32, scope, operand("LiteralContextDependentNumber", 2)),
        Instruction.Constant(u32, semantics, operand("LiteralContextDependentNumber", 896)),
        Instruction.Constant(u64, addend, operand("LiteralContextDependentNumber", 3)),
        Instruction.Constant(u64, minimum, operand("LiteralContextDependentNumber", 7)),
        Instruction.Constant(u64, one, operand("LiteralContextDependentNumber", 1)),
        Instruction.Constant(u64, two, operand("LiteralContextDependentNumber", 2)),
        Instruction.EntryPoint(operand("ExecutionModel", 6), function, stringOperand("atomics"), Col.empty),
        Instruction.Function(voidType, function, operand("FunctionControl", 0), functionType),
        Instruction.FunctionParameter(pointerType, target),
        Instruction.Label(operand("IdResult", 14)),
        Instruction.AtomicStore(target, scope, semantics, minimum),
        Instruction.AtomicIAdd(u64, addResult, target, scope, semantics, addend),
        Instruction.AtomicUMin(u64, minimumResult, target, scope, semantics, minimum),
        Instruction.PtrAccessChain(pointerType, addOutput, target, one, Col.empty),
        Instruction.Store(addOutput, addResult, None),
        Instruction.PtrAccessChain(pointerType, minimumOutput, target, two, Col.empty),
        Instruction.Store(minimumOutput, minimumResult, None),
        Instruction.Return(),
        Instruction.FunctionEnd()
      ),
      Endianness.Little
    )
    val allocation = AllocationId.fromLong(12L).toOption.get
    val memory     = ArrayMemory(allocation, 24)
    assertEquals(memory.write(allocation, 0L, LittleEndian.toCol(10L, 8)), Right(()))
    val dispatch = KernelDispatch
      .create(
        ProgramId.fromLong(12L).toOption.get,
        "atomics",
        Dimensions.One,
        Dimensions.One,
        Col(KernelArgument.Allocation(allocation, 0L))
      )
      .toOption
      .get

    assertEquals(Interpreter.run(module, dispatch, memory), Right(()))
    assertEquals(memory.bytes, LittleEndian.toCol(7L, 8) ++ LittleEndian.toCol(7L, 8) ++ LittleEndian.toCol(10L, 8))
  }

  private final class ArrayMemory(expectedId: AllocationId, size: Int) extends ExecutionMemory {
    private val content = AtomicReference(Col.fill(size)(0.toByte))

    def bytes: Col[Byte] = content.get()

    override def read(id: AllocationId, offset: Long, size: Int): Either[ExecutionFailure, Col[Byte]] =
      checked(id, offset, size).map(start => content.get().slice(start, start + size))

    override def write(id: AllocationId, offset: Long, bytes: Col[Byte]): Either[ExecutionFailure, Unit] =
      checked(id, offset, bytes.size).map { start =>
        content.updateAndGet(current =>
          Col.from(current.take(start).iterator ++ bytes.iterator ++ current.drop(start + bytes.size))
        )
        ()
      }

    private def checked(id: AllocationId, offset: Long, length: Int): Either[ExecutionFailure, Int] =
      Either.cond(
        id == expectedId && ByteRange.contains(size, offset, length.toLong),
        offset.toInt,
        ExecutionFailure.InvalidRequest(s"invalid test memory access at $offset + $length")
      )
  }

  private def operand(kind: String, values: Int*): Operand = Operand(kind, Col.from(values.map(Word(_))))

  private def phiPair(value: Operand, parent: Operand): Operand =
    Operand("PairIdRefIdRef", Col(value.word, parent.word))

  private def stringOperand(value: String): Operand = {
    val bytes = value.getBytes(java.nio.charset.StandardCharsets.UTF_8) :+ 0.toByte
    val words = bytes.grouped(4).map { group =>
      Word(LittleEndian.decode(group, 0, group.length).toInt)
    }
    Operand("LiteralString", Col.from(words))
  }
}
