package net.kurobako.gemstone.isa.spirv.v1_6

import net.kurobako.gemstone.Constraint.IntBetween

import net.kurobako.gemstone.Constraint.{NonNegative, Positive}

import net.kurobako.gemstone.Constraint.NonEmptyString
import net.kurobako.gemstone.isa.*
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchGeometry.{Coordinates, Dimensions, GlobalDimensions}

import net.kurobako.gemstone.Vec

import net.kurobako.gemstone.binary.ByteRange
import net.kurobako.gemstone.binary.LittleEndian
import net.kurobako.gemstone.NonEmptyVec

import java.util.concurrent.atomic.AtomicReference
final class ExecutionLayoutSuite extends munit.FunSuite with ExecutionTestSupport {
  test("prepares byte-aligned nonstandard integer widths") {
    val module = Module(
      Header(ISA.version, Word(0), Word(4), Word(0)),
      Vec(
        Instruction.TypeInt(operand("IdResult", 1), operand("LiteralInteger", 24), operand("LiteralInteger", 0)),
        Instruction.TypeInt(operand("IdResult", 2), operand("LiteralInteger", 48), operand("LiteralInteger", 0))
      ),
      Endianness.Little
    )
    assertEquals(loadUnchecked(module).map(_ => ()), Right(()))
  }

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
      Vec(
        Instruction.TypeVoid(operand("IdResult", 1)),
        Instruction.TypeInt(u32, operand("LiteralInteger", 32), operand("LiteralInteger", 0)),
        Instruction.TypeInt(u64, operand("LiteralInteger", 64), operand("LiteralInteger", 0)),
        Instruction.TypeVector(operand("IdResult", 4), u64, operand("LiteralInteger", 3)),
        Instruction.TypePointer(operand("IdResult", 5), operand("StorageClass", 1), operand("IdRef", 4)),
        Instruction.TypePointer(operand("IdResult", 6), operand("StorageClass", 5), u32),
        Instruction.TypeFunction(operand("IdResult", 7), operand("IdRef", 1), Vec(operand("IdRef", 6), u32)),
        Instruction.Variable(operand("IdResultType", 5), globalId, operand("StorageClass", 1), None),
        Instruction.Decorate(globalId, operand("Decoration", 11, 28)),
        Instruction.EntryPoint(operand("ExecutionModel", 6), function, stringOperand("fill"), Vec(globalId)),
        Instruction.Function(operand("IdResultType", 1), function, operand("FunctionControl", 0), operand("IdRef", 7)),
        Instruction.FunctionParameter(operand("IdResultType", 6), output),
        Instruction.FunctionParameter(operand("IdResultType", 2), fillValue),
        Instruction.Label(operand("IdResult", 12)),
        Instruction.Load(operand("IdResultType", 4), loadedGlobal, globalId, None),
        Instruction.CompositeExtract(u64, linearId, loadedGlobal, Vec(operand("LiteralInteger", 0))),
        Instruction.PtrAccessChain(operand("IdResultType", 6), destination, output, linearId, Vec.empty),
        Instruction.Store(destination, fillValue, None),
        Instruction.Return(),
        Instruction.FunctionEnd()
      ),
      Endianness.Little
    )
    val allocation = MemoryRegion(1L)
    val memory     = ArrayMemory(allocation, 40)
    val dispatch   = KernelTask
      .validated(
        NonEmptyString("fill"),
        Dimensions(4, 1, 1),
        Dimensions(2, 1, 1),
        Vec(
          KernelTask.Argument.Memory(allocation, 4L),
          KernelTask.Argument.Scalar(LittleEndian.toVec(0x12345678L, 4))
        )
      )
      .toOption
      .get

    val prepared = loadUnchecked(module).toOption.get
    val limited  =
      TaskControl.forScheduler(TaskLimits(maxInstructions = 1L, quantum = 1))
    assert(
      executeLoaded(prepared, dispatch, memory, limited).left.exists(_.message.contains("instruction budget of 1"))
    )
    assertEquals(executeUnchecked(module, dispatch, memory), Right(()))
    assertEquals(memory.bytes.take(4), Vec.fill(4)(0.toByte))
    assertEquals(
      memory.bytes.slice(4, 36),
      Vec.fill(8)(LittleEndian.toVec(0x12345678L, 4)).flatten
    )
    assertEquals(memory.bytes.drop(36), Vec.fill(4)(0.toByte))

    val parallelCalls = java.util.concurrent.atomic.AtomicInteger(0)
    final class RecordingParallelism extends Scheduler {
      override val parallelism                                                                     = 4
      override def shouldParallel(tasks: NonNegative[Long], workItems: NonNegative[Long]): Boolean = tasks.raw > 1L
      override def parallelFor(tasks: NonNegative[Long])(
          task: NonNegative[Long] => Either[Fault, Unit]
      ) = {
        parallelCalls.incrementAndGet()
        Scheduler.Sequential.parallelFor(tasks)(task)
      }
    }
    val unrelatedAtomic = module.copy(
      header = module.header.copy(bound = Word(128)),
      instructions = module.instructions.take(10) ++ Vec(
        Instruction.Constant(u32, operand("IdResult", 103), operand("LiteralContextDependentNumber", 1)),
        Instruction.Constant(u32, operand("IdResult", 104), operand("LiteralContextDependentNumber", 0))
      ) ++ module.instructions.drop(10) ++ Vec(
        Instruction.Function(
          operand("IdResultType", 1),
          operand("IdResult", 100),
          operand("FunctionControl", 0),
          operand("IdRef", 7)
        ),
        Instruction.FunctionParameter(operand("IdResultType", 6), operand("IdResult", 101)),
        Instruction.FunctionParameter(u32, operand("IdResult", 102)),
        Instruction.Label(operand("IdResult", 105)),
        Instruction.AtomicStore(
          operand("IdRef", 101),
          operand("IdScope", 103),
          operand("IdMemorySemantics", 104),
          operand("IdRef", 102)
        ),
        Instruction.Return(),
        Instruction.FunctionEnd()
      )
    )
    val parallelMemory = ArrayMemory(allocation, 40)
    val scheduler      = RecordingParallelism()
    assertEquals(
      VMInterpreter.execute(
        loadUnchecked(unrelatedAtomic).toOption.get,
        dispatch,
        parallelMemory,
        TaskControl.forScheduler(scheduler = scheduler),
        scheduler,
        Observer.discard
      ),
      Right(())
    )
    assertEquals(parallelMemory.bytes, memory.bytes)
    assertEquals(parallelCalls.get(), 1)

    val workgroupSizeModule = module.copy(instructions =
      module.instructions.updated(
        8,
        Instruction.Decorate(globalId, operand("Decoration", 11, 25))
      )
    )
    val workgroupAllocation = MemoryRegion(17L)
    val workgroupMemory     = ArrayMemory(workgroupAllocation, 40)
    val workgroupDispatch   = KernelTask
      .validated(
        NonEmptyString("fill"),
        Dimensions(4, 1, 1),
        Dimensions(2, 1, 1),
        Vec(
          KernelTask.Argument.Memory(workgroupAllocation, 4L),
          KernelTask.Argument.Scalar(LittleEndian.toVec(0x12345678L, 4))
        )
      )
      .toOption
      .get
    assertEquals(executeUnchecked(workgroupSizeModule, workgroupDispatch, workgroupMemory), Right(()))
    assertEquals(workgroupMemory.bytes.slice(20, 24), LittleEndian.toVec(0x12345678L, 4))
    assertEquals(
      workgroupMemory.bytes.take(20) ++ workgroupMemory.bytes.drop(24),
      Vec.fill(36)(0.toByte)
    )

    val malformed = module.copy(instructions =
      module.instructions.updated(
        3,
        Instruction.TypeVector(operand("IdResult", 4), u64, operand("LiteralInteger", 2))
      )
    )
    executeUnchecked(malformed, dispatch, memory) match {
      case Left(Fault.InvalidTask(message)) =>
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
    val instructions       = Vec(
      Instruction.TypeVoid(voidType),
      Instruction.TypeInt(u32, operand("LiteralInteger", 32), operand("LiteralInteger", 0)),
      Instruction.TypeInt(u64, operand("LiteralInteger", 64), operand("LiteralInteger", 0)),
      Instruction.TypeVector(vector3, u64, operand("LiteralInteger", 3)),
      Instruction.TypePointer(globalIdType, operand("StorageClass", 1), vector3),
      Instruction.TypePointer(subgroupIdType, operand("StorageClass", 1), u32),
      Instruction.TypePointer(outputType, operand("StorageClass", 5), u32),
      Instruction.TypeFunction(functionType, voidType, Vec(outputType)),
      Instruction.Variable(globalIdType, globalId, operand("StorageClass", 1), None),
      Instruction.Variable(subgroupIdType, subgroupId, operand("StorageClass", 1), None),
      Instruction.Decorate(globalId, operand("Decoration", 11, 28)),
      Instruction.Decorate(subgroupId, operand("Decoration", 11, 40)),
      Instruction.Undef(u32, undefValue),
      Instruction.EntryPoint(
        operand("ExecutionModel", 6),
        function,
        stringOperand("subgroups"),
        Vec(globalId, subgroupId)
      ),
      Instruction.ExecutionMode(function, operand("ExecutionMode", 35, 32)),
      Instruction.Function(voidType, function, operand("FunctionControl", 0), functionType),
      Instruction.FunctionParameter(outputType, output),
      Instruction.Label(operand("IdResult", 13)),
      Instruction.Load(vector3, loadedGlobalId, globalId, None),
      Instruction.CompositeExtract(u64, localLinearId, loadedGlobalId, Vec(operand("LiteralInteger", 0))),
      Instruction.Load(u32, loadedSubgroupId, subgroupId, None),
      Instruction.PtrAccessChain(outputType, destination, output, localLinearId, Vec.empty),
      Instruction.Store(destination, loadedSubgroupId, None),
      Instruction.Return(),
      Instruction.FunctionEnd()
    )
    val module =
      Module(Header(ISA.version, Word(0), Word(19), Word(0)), instructions, Endianness.Little)
    val allocation = MemoryRegion(31L)
    val dispatch   = KernelTask
      .validated(
        NonEmptyString("subgroups"),
        Dimensions(64, 1, 1),
        Dimensions.One,
        Vec(KernelTask.Argument.Memory(allocation, 0L))
      )
      .toOption
      .get
    val memory = ArrayMemory(allocation, 256)

    assertEquals(executeUnchecked(module, dispatch, memory), Right(()))
    assertEquals(
      memory.bytes,
      Vec.fill(32)(LittleEndian.toVec(0L, 4)).flatten ++
        Vec.fill(32)(LittleEndian.toVec(1L, 4)).flatten
    )

    val defaultSubgroupModule = module.copy(instructions = instructions.patch(14, Vec.empty, 1))
    val defaultSubgroupMemory = ArrayMemory(allocation, 256)
    assertEquals(executeUnchecked(defaultSubgroupModule, dispatch, defaultSubgroupMemory), Right(()))
    assertEquals(
      defaultSubgroupMemory.bytes,
      Vec.fill(32)(LittleEndian.toVec(0L, 4)).flatten ++
        Vec.fill(32)(LittleEndian.toVec(1L, 4)).flatten
    )

    val numSubgroupsModule = module.copy(instructions =
      instructions.updated(
        subgroupDecoration,
        Instruction.Decorate(subgroupId, operand("Decoration", 11, 38))
      )
    )
    val numSubgroupsMemory = ArrayMemory(allocation, 256)
    assertEquals(executeUnchecked(numSubgroupsModule, dispatch, numSubgroupsMemory), Right(()))
    assertEquals(numSubgroupsMemory.bytes, Vec.fill(64)(LittleEndian.toVec(2L, 4)).flatten)

    val numEnqueuedSubgroupsModule = module.copy(instructions =
      instructions.updated(
        subgroupDecoration,
        Instruction.Decorate(subgroupId, operand("Decoration", 11, 39))
      )
    )
    val numEnqueuedSubgroupsMemory = ArrayMemory(allocation, 256)
    assertEquals(executeUnchecked(numEnqueuedSubgroupsModule, dispatch, numEnqueuedSubgroupsMemory), Right(()))
    assertEquals(
      numEnqueuedSubgroupsMemory.bytes,
      Vec.fill(64)(LittleEndian.toVec(2L, 4)).flatten
    )

    val undefModule = module.copy(instructions =
      instructions.updated(22, Instruction.Store(destination, undefValue, None))
    )
    val undefMemory = ArrayMemory(allocation, 256)
    assertEquals(executeUnchecked(undefModule, dispatch, undefMemory), Right(()))
    assertEquals(undefMemory.bytes, Vec.fill(256)(0.toByte))
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
      Vec(
        Instruction.TypeVoid(voidType),
        Instruction.TypeInt(u32, operand("LiteralInteger", 32), operand("LiteralInteger", 0)),
        Instruction.Constant(u32, length, operand("LiteralContextDependentNumber", 2)),
        Instruction.TypeArray(arrayType, u32, length),
        Instruction.TypePointer(globalType, operand("StorageClass", 5), arrayType),
        Instruction.TypePointer(pointerType, operand("StorageClass", 5), u32),
        Instruction.TypeFunction(functionType, voidType, Vec(pointerType)),
        Instruction.Constant(u32, first, operand("LiteralContextDependentNumber", 17)),
        Instruction.Constant(u32, second, operand("LiteralContextDependentNumber", 29)),
        Instruction.Constant(u32, one, operand("LiteralContextDependentNumber", 1)),
        Instruction.ConstantComposite(arrayType, initial, Vec(first, second)),
        Instruction.Variable(globalType, global, operand("StorageClass", 5), Some(initial)),
        Instruction.Decorate(global, operand("Decoration", 22)),
        Instruction.EntryPoint(operand("ExecutionModel", 6), function, stringOperand("global"), Vec(global)),
        Instruction.Function(voidType, function, operand("FunctionControl", 0), functionType),
        Instruction.FunctionParameter(pointerType, output),
        Instruction.Label(operand("IdResult", 17)),
        Instruction.AccessChain(pointerType, element, global, Vec(one)),
        Instruction.Load(u32, loaded, element, None),
        Instruction.Store(output, loaded, None),
        Instruction.Return(),
        Instruction.FunctionEnd()
      ),
      Endianness.Little
    )
    val allocation = MemoryRegion(20L)
    val memory     = ArrayMemory(allocation, 4)
    val dispatch   = KernelTask
      .validated(
        NonEmptyString("global"),
        Dimensions.One,
        Dimensions.One,
        Vec(KernelTask.Argument.Memory(allocation, 0L))
      )
      .toOption
      .get

    assertEquals(executeUnchecked(module, dispatch, memory), Right(()))
    assertEquals(memory.bytes, LittleEndian.toVec(29L, 4))
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
      val instructions  = Vec(
        Instruction.TypeVoid(voidType),
        Instruction.TypeInt(u32, operand("LiteralInteger", 32), operand("LiteralInteger", 0)),
        Instruction.TypePointer(localPointer, operand("StorageClass", storageClass), u32),
        Instruction.TypePointer(outputPointer, operand("StorageClass", 5), u32),
        Instruction.TypeFunction(functionType, voidType, Vec(outputPointer)),
        Instruction.Constant(u32, initial, operand("LiteralContextDependentNumber", 7)),
        Instruction.Constant(u32, one, operand("LiteralContextDependentNumber", 1)),
        Instruction.Variable(localPointer, variable, operand("StorageClass", storageClass), Some(initial)),
        Instruction.EntryPoint(operand("ExecutionModel", 6), function, stringOperand(entryPoint), Vec(variable)),
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

    val allocation        = MemoryRegion(41L)
    val workgroupMemory   = ArrayMemory(allocation, 4)
    val workgroupDispatch = KernelTask
      .validated(
        NonEmptyString("workgroup_lifetime"),
        Dimensions.One,
        Dimensions(2, 1, 1),
        Vec(KernelTask.Argument.Memory(allocation, 0L))
      )
      .toOption
      .get
    assertEquals(
      executeUnchecked(module(4, "workgroup_lifetime"), workgroupDispatch, workgroupMemory),
      Right(())
    )
    assertEquals(workgroupMemory.bytes, LittleEndian.toVec(7L, 4))

    val privateMemory   = ArrayMemory(allocation, 4)
    val privateDispatch = KernelTask
      .validated(
        NonEmptyString("private_lifetime"),
        Dimensions(2, 1, 1),
        Dimensions.One,
        Vec(KernelTask.Argument.Memory(allocation, 0L))
      )
      .toOption
      .get
    assertEquals(executeUnchecked(module(6, "private_lifetime"), privateDispatch, privateMemory), Right(()))
    assertEquals(privateMemory.bytes, LittleEndian.toVec(7L, 4))
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
    val one            = operand("IdResult", 16)
    val loaded         = operand("IdResult", 17)
    val secondOutput   = operand("IdResult", 18)
    val module         = Module(
      Header(ISA.version, Word(0), Word(19), Word(0)),
      Vec(
        Instruction.TypeVoid(voidType),
        Instruction.TypeInt(u32, operand("LiteralInteger", 32), operand("LiteralInteger", 0)),
        Instruction.Constant(u32, two, operand("LiteralContextDependentNumber", 2)),
        Instruction.TypeArray(arrayType, u32, two),
        Instruction.TypeStruct(structureType, Vec(u32, arrayType)),
        Instruction.TypePointer(outputPointer, operand("StorageClass", 5), structureType),
        Instruction.TypeFunction(functionType, voidType, Vec(outputPointer)),
        Instruction.Constant(u32, first, operand("LiteralContextDependentNumber", 11)),
        Instruction.Constant(u32, second, operand("LiteralContextDependentNumber", 22)),
        Instruction.Constant(u32, header, operand("LiteralContextDependentNumber", 5)),
        Instruction.Constant(u32, one, operand("LiteralContextDependentNumber", 1)),
        Instruction.ConstantComposite(arrayType, arrayValue, Vec(first, second)),
        Instruction.ConstantComposite(structureType, structureValue, Vec(header, arrayValue)),
        Instruction.Decorate(arrayType, operand("Decoration", 6, 16)),
        Instruction.MemberDecorate(structureType, operand("LiteralInteger", 1), operand("Decoration", 35, 16)),
        Instruction.EntryPoint(operand("ExecutionModel", 6), function, stringOperand("layout"), Vec.empty),
        Instruction.Function(voidType, function, operand("FunctionControl", 0), functionType),
        Instruction.FunctionParameter(outputPointer, output),
        Instruction.Label(operand("IdResult", 15)),
        Instruction.Store(output, structureValue, None),
        Instruction.Load(structureType, loaded, output, None),
        Instruction.PtrAccessChain(outputPointer, secondOutput, output, one, Vec.empty),
        Instruction.Store(secondOutput, loaded, None),
        Instruction.Return(),
        Instruction.FunctionEnd()
      ),
      Endianness.Little
    )
    val allocation = MemoryRegion(43L)
    val memory     = ArrayMemory(allocation, 96)
    val dispatch   = KernelTask
      .validated(
        NonEmptyString("layout"),
        Dimensions.One,
        Dimensions.One,
        Vec(KernelTask.Argument.Memory(allocation, 0L))
      )
      .toOption
      .get

    assertEquals(executeUnchecked(module, dispatch, memory), Right(()))
    val expected        = Array.fill[Byte](96)(0)
    val expectedOffsets = Vec(
      (0, 16, 32),
      (48, 64, 80)
    )
    for ((first, second, third) <- expectedOffsets) {
      LittleEndian.write(5L, expected, first, 4)
      LittleEndian.write(11L, expected, second, 4)
      LittleEndian.write(22L, expected, third, 4)
    }
    assertEquals(memory.bytes, Vec.from(expected))
  }

  test("pointer arithmetic uses the SPIR-V stride for packed optional-like structures") {
    val voidType     = operand("IdResult", 1)
    val u8           = operand("IdResult", 2)
    val u32          = operand("IdResult", 3)
    val u64          = operand("IdResult", 4)
    val three        = operand("IdResult", 5)
    val padding      = operand("IdResult", 6)
    val storage      = operand("IdResult", 7)
    val payloadBase  = operand("IdResult", 8)
    val payload      = operand("IdResult", 9)
    val optionalBase = operand("IdResult", 10)
    val optional     = operand("IdResult", 11)
    val optionalPtr  = operand("IdResult", 12)
    val bytePtr      = operand("IdResult", 13)
    val functionType = operand("IdResult", 14)
    val zero         = operand("IdResult", 15)
    val one          = operand("IdResult", 16)
    val function     = operand("IdResult", 17)
    val input        = operand("IdResult", 18)
    val output       = operand("IdResult", 19)
    val second       = operand("IdResult", 20)
    val engaged      = operand("IdResult", 21)
    val loaded       = operand("IdResult", 22)
    val module       = Module(
      Header(ISA.version, Word(0), Word(24), Word(0)),
      Vec(
        Instruction.TypeVoid(voidType),
        Instruction.TypeInt(u8, operand("LiteralInteger", 8), operand("LiteralInteger", 0)),
        Instruction.TypeInt(u32, operand("LiteralInteger", 32), operand("LiteralInteger", 0)),
        Instruction.TypeInt(u64, operand("LiteralInteger", 64), operand("LiteralInteger", 0)),
        Instruction.Constant(u64, three, operand("LiteralContextDependentNumber", 3)),
        Instruction.TypeArray(padding, u8, three),
        Instruction.TypeStruct(storage, Vec(u32)),
        Instruction.TypeStruct(payloadBase, Vec(storage, u8)),
        Instruction.Decorate(payloadBase, operand("Decoration", 10)),
        Instruction.TypeStruct(payload, Vec(payloadBase, padding)),
        Instruction.TypeStruct(optionalBase, Vec(payload)),
        Instruction.TypeStruct(optional, Vec(optionalBase)),
        Instruction.TypePointer(optionalPtr, operand("StorageClass", 5), optional),
        Instruction.TypePointer(bytePtr, operand("StorageClass", 5), u8),
        Instruction.TypeFunction(functionType, voidType, Vec(optionalPtr, bytePtr)),
        Instruction.Constant(u32, zero, operand("LiteralContextDependentNumber", 0)),
        Instruction.Constant(u32, one, operand("LiteralContextDependentNumber", 1)),
        Instruction.EntryPoint(operand("ExecutionModel", 6), function, stringOperand("packed_stride"), Vec.empty),
        Instruction.Function(voidType, function, operand("FunctionControl", 0), functionType),
        Instruction.FunctionParameter(optionalPtr, input),
        Instruction.FunctionParameter(bytePtr, output),
        Instruction.Label(operand("IdResult", 23)),
        Instruction.PtrAccessChain(optionalPtr, second, input, one, Vec.empty),
        Instruction.AccessChain(bytePtr, engaged, second, Vec(zero, zero, zero, one)),
        Instruction.Load(u8, loaded, engaged, None),
        Instruction.Store(output, loaded, None),
        Instruction.Return(),
        Instruction.FunctionEnd()
      ),
      Endianness.Little
    )
    val allocation = MemoryRegion(122L)
    val memory     = ArrayMemory(allocation, 40)
    assertEquals(memory.write(allocation, 12L, Vec(1.toByte)), Right(()))
    val dispatch = KernelTask
      .validated(
        NonEmptyString("packed_stride"),
        Dimensions.One,
        Dimensions.One,
        Vec(
          KernelTask.Argument.Memory(allocation, 0L),
          KernelTask.Argument.Memory(allocation, 32L)
        )
      )
      .toOption
      .get

    assertEquals(executeUnchecked(module, dispatch, memory), Right(()))
    assertEquals(memory.bytes(32), 1.toByte)
  }

  test("pointer-bearing arrays use their decorated stride for relocation leaves") {
    val voidType = operand("IdResult", 1)
    val u32      = operand("IdResult", 2)
    val pointer  = operand("IdResult", 3)
    val length   = operand("IdResult", 4)
    val array    = operand("IdResult", 5)
    val module   = Module(
      Header(ISA.version, Word(0), Word(8), Word(0)),
      Vec(
        Instruction.TypeVoid(voidType),
        Instruction.TypeInt(u32, operand("LiteralInteger", 32), operand("LiteralInteger", 0)),
        Instruction.TypePointer(pointer, operand("StorageClass", 5), u32),
        Instruction.Constant(u32, length, operand("LiteralContextDependentNumber", 2)),
        Instruction.TypeArray(array, pointer, length),
        Instruction.Decorate(array, operand("Decoration", 6, 16))
      ),
      Endianness.Little
    )

    assertEquals(
      VMInterpreter.pointerLeafOffsetsForTest(module, 5),
      Right(Vec(0L -> 3, 16L -> 3))
    )
  }

  test("ByVal byte-array aggregates retain their private layout when their first word is relocated") {
    def result(value: Int) = operand("IdResult", value)
    val module             = Module(
      Header(ISA.version, Word(0), Word(34), Word(0)),
      Vec(
        Instruction.TypeVoid(result(1)),
        Instruction.TypeInt(result(2), operand("LiteralInteger", 64), operand("LiteralInteger", 0)),
        Instruction.TypeInt(result(3), operand("LiteralInteger", 8), operand("LiteralInteger", 0)),
        Instruction.TypeInt(result(4), operand("LiteralInteger", 32), operand("LiteralInteger", 0)),
        Instruction.Constant(result(2), result(5), operand("LiteralContextDependentNumber", 8, 0)),
        Instruction.TypeArray(result(6), result(3), result(5)),
        Instruction.TypeStruct(result(7), Vec(result(6), result(2))),
        Instruction.Decorate(result(7), operand("Decoration", 10)),
        Instruction.TypePointer(result(8), operand("StorageClass", 7), result(7)),
        Instruction.TypePointer(result(9), operand("StorageClass", 7), result(2)),
        Instruction.TypePointer(result(10), operand("StorageClass", 5), result(2)),
        Instruction.TypePointer(result(11), operand("StorageClass", 8), result(4)),
        Instruction.TypeFunction(result(14), result(1), Vec(result(8), result(10))),
        Instruction.Constant(result(2), result(15), operand("LiteralContextDependentNumber", 1, 0)),
        Instruction.Decorate(result(21), operand("Decoration", 38, 2)), // FuncParamAttr ByVal
        Instruction.EntryPoint(operand("ExecutionModel", 6), result(20), stringOperand("byval_bytes"), Vec.empty),
        Instruction.Function(result(1), result(20), operand("FunctionControl", 0), result(14)),
        Instruction.FunctionParameter(result(8), result(21)),
        Instruction.FunctionParameter(result(10), result(22)),
        Instruction.Label(result(23)),
        Instruction.Bitcast(result(9), result(24), result(21)),
        Instruction.PtrAccessChain(result(9), result(25), result(24), result(15), Vec.empty),
        Instruction.Load(result(2), result(26), result(25), None),
        Instruction.Store(result(22), result(26), None),
        Instruction.Load(result(2), result(27), result(24), None),
        Instruction.ConvertUToPtr(result(11), result(28), result(27)),
        Instruction.Load(result(4), result(29), result(28), None),
        Instruction.UConvert(result(2), result(30), result(29)),
        Instruction.PtrAccessChain(result(10), result(31), result(22), result(15), Vec.empty),
        Instruction.Store(result(31), result(30), None),
        Instruction.Return(),
        Instruction.FunctionEnd()
      ),
      Endianness.Little
    )
    val allocationPairs = Vec(
      MemoryRegion(1L)                -> MemoryRegion(3L),
      MemoryRegion(9007199254740993L) -> MemoryRegion(9007199254740995L)
    )
    for ((needle, output) <- allocationPairs) {
      final class Memory extends net.kurobako.gemstone.vm.Memory {
        val storage =
          Map(needle -> LittleEndian.toArray(7L, 4), output -> Array.ofDim[Byte](16))
        def read(
            id: MemoryRegion,
            offset: NonNegative[Long],
            size: NonNegative[Int]
        ): Either[Fault, Vec[Byte]] =
          if (offset.raw > storage(id).length - size.raw)
            Left(Fault.InvalidTask("test read exceeds allocation"))
          else Right(Vec.from(storage(id).slice(offset.raw.toInt, offset.raw.toInt + size.raw)))
        def write(
            id: MemoryRegion,
            offset: NonNegative[Long],
            bytes: Vec[Byte]
        ): Either[Fault, Unit] = {
          val _ = bytes.copyToArray(storage(id), offset.raw.toInt, bytes.size); Right(())
        }
      }
      val memory    = Memory()
      val aggregate = LittleEndian.toVec(0x12345678L, 8) ++
        LittleEndian.toVec(99L, 8)
      val dispatch = KernelTask.validated(
        NonEmptyString("byval_bytes"),
        Dimensions.One,
        Dimensions.One,
        Vec(KernelTask.Argument.Scalar(aggregate), KernelTask.Argument.Memory(output, 0L)),
        pointerRelocations = Vec(KernelTask.PointerRelocation(
          KernelTask.RelocationSource.Argument(0),
          0L,
          needle,
          0L
        ))
      ).toOption.get
      assertEquals(executeUnchecked(module, dispatch, memory), Right(()))
      val decodedAggregate = LittleEndian.decode(memory.storage(output), 0, 8)
      val decodedPointer   = LittleEndian.decode(memory.storage(output), 8, 8)
      val decodedPointee   = LittleEndian.decode(memory.storage(needle), 0, 4)
      assertEquals(decodedAggregate, 99L)
      assertEquals(decodedPointer, 7L)
      assertEquals(decodedPointee, 7L)
    }
  }

  test("pointer-sized relocated arguments bind externally when their aggregate pointee is larger") {
    val voidType      = operand("IdResult", 1)
    val u32           = operand("IdResult", 2)
    val valuePointer  = operand("IdResult", 3)
    val structureType = operand("IdResult", 4)
    val structurePtr  = operand("IdResult", 5)
    val functionType  = operand("IdResult", 6)
    val one           = operand("IdResult", 7)
    val expected      = operand("IdResult", 8)
    val function      = operand("IdResult", 9)
    val argument      = operand("IdResult", 10)
    val member        = operand("IdResult", 12)
    val module        = Module(
      Header(ISA.version, Word(0), Word(14), Word(0)),
      Vec(
        Instruction.TypeVoid(voidType),
        Instruction.TypeInt(u32, operand("LiteralInteger", 32), operand("LiteralInteger", 0)),
        Instruction.TypePointer(valuePointer, operand("StorageClass", 5), u32),
        Instruction.TypeStruct(structureType, Vec(valuePointer, u32)),
        Instruction.TypePointer(structurePtr, operand("StorageClass", 5), structureType),
        Instruction.TypeFunction(functionType, voidType, Vec(structurePtr)),
        Instruction.Constant(u32, one, operand("LiteralContextDependentNumber", 1)),
        Instruction.Constant(u32, expected, operand("LiteralContextDependentNumber", 42)),
        Instruction.EntryPoint(operand("ExecutionModel", 6), function, stringOperand("external_aggregate"), Vec.empty),
        Instruction.Function(voidType, function, operand("FunctionControl", 0), functionType),
        Instruction.FunctionParameter(structurePtr, argument),
        Instruction.Label(operand("IdResult", 11)),
        Instruction.AccessChain(valuePointer, member, argument, Vec(one)),
        Instruction.Store(member, expected, None),
        Instruction.Return(),
        Instruction.FunctionEnd()
      ),
      Endianness.Little
    )
    val allocation = MemoryRegion(45L)
    val memory     = ArrayMemory(allocation, 12)
    val dispatch   = KernelTask
      .validated(
        NonEmptyString("external_aggregate"),
        Dimensions.One,
        Dimensions.One,
        Vec(KernelTask.Argument.Scalar(Vec.fill(8)(0.toByte))),
        pointerRelocations =
          Vec(KernelTask.PointerRelocation(
            KernelTask.RelocationSource.Argument(0),
            0L,
            allocation,
            0L
          ))
      )
      .toOption
      .get

    assertEquals(executeUnchecked(module, dispatch, memory), Right(()))
    assertEquals(memory.bytes.drop(8), LittleEndian.toVec(42L, 4))
  }

}
