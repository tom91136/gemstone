package net.kurobako.gemstone.isa.spirv.v1_6

import execution.*
import net.kurobako.gemstone.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.binary.Widths.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.*
import net.kurobako.gemstone.isa.spirv.v1_6 as spv
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchGeometry.*
class ExecutionLayoutSuite extends munit.FunSuite {
  test("OpenCL metadata retains required and hinted work-group sizes") {
    val entry  = operand(OperandKind.IdResult, 7)
    val module = Module(
      Header(ISA.version, Word(0), Word(16), Word(0)),
      Vec(
        Instruction.EntryPoint(
          operand(OperandKind.ExecutionModel, spv.ExecutionModel.Kernel),
          entry,
          stringOperand("main"),
          Vec.empty
        ),
        Instruction.ExecutionMode(entry, operand(OperandKind.ExecutionMode, spv.ExecutionMode.LocalSize, 8, 2, 1)),
        Instruction.ExecutionMode(entry, operand(OperandKind.ExecutionMode, spv.ExecutionMode.LocalSizeHint, 4, 3, 2)),
        Instruction.Function(
          operand(OperandKind.IdResultType, 1),
          entry,
          operand(OperandKind.FunctionControl, spv.FunctionControl.None),
          operand(OperandKind.IdRef, 2)
        ),
        Instruction.FunctionEnd()
      ),
      Endianness.Little
    )
    val loaded   = loadUnchecked(module).fold(error => fail(error.message), identity)
    val metadata = loaded.kernel("main").fold(error => fail(error.message), identity)
    assertEquals(metadata.requiredWorkGroupSize, Some(Dimensions(8, 2, 1)))
    assertEquals(metadata.workGroupSizeHint, Some(Dimensions(4, 3, 2)))
    assertEquals(metadata.entryPointId.map(_.raw), Some(7))
  }

  test("prepares byte-aligned nonstandard integer widths") {
    val module = Module(
      Header(ISA.version, Word(0), Word(4), Word(0)),
      Vec(
        Instruction.TypeInt(
          operand(OperandKind.IdResult, 1),
          operand(OperandKind.LiteralInteger, 24),
          operand(OperandKind.LiteralInteger, 0)
        ),
        Instruction.TypeInt(
          operand(OperandKind.IdResult, 2),
          operand(OperandKind.LiteralInteger, 48),
          operand(OperandKind.LiteralInteger, 0)
        )
      ),
      Endianness.Little
    )
    assertEquals(loadUnchecked(module).map(_ => ()), Right(()))
  }

  test("dispatch binds built-ins and typed arguments without exposing native pointers") {
    val u32          = operand(OperandKind.IdResult, 2)
    val u64          = operand(OperandKind.IdResult, 3)
    val globalId     = operand(OperandKind.IdResult, 8)
    val function     = operand(OperandKind.IdResult, 9)
    val output       = operand(OperandKind.IdResult, 10)
    val fillValue    = operand(OperandKind.IdResult, 11)
    val loadedGlobal = operand(OperandKind.IdResult, 13)
    val linearId     = operand(OperandKind.IdResult, 14)
    val destination  = operand(OperandKind.IdResult, 15)
    val module       = Module(
      Header(ISA.version, Word(0), Word(32), Word(0)),
      Vec(
        Instruction.TypeVoid(operand(OperandKind.IdResult, 1)),
        Instruction.TypeInt(u32, operand(OperandKind.LiteralInteger, 32), operand(OperandKind.LiteralInteger, 0)),
        Instruction.TypeInt(u64, operand(OperandKind.LiteralInteger, 64), operand(OperandKind.LiteralInteger, 0)),
        Instruction.TypeVector(operand(OperandKind.IdResult, 4), u64, operand(OperandKind.LiteralInteger, 3)),
        Instruction.TypePointer(
          operand(OperandKind.IdResult, 5),
          operand(OperandKind.StorageClass, spv.StorageClass.Input),
          operand(OperandKind.IdRef, 4)
        ),
        Instruction.TypePointer(
          operand(OperandKind.IdResult, 6),
          operand(OperandKind.StorageClass, spv.StorageClass.CrossWorkgroup),
          u32
        ),
        Instruction.TypeFunction(
          operand(OperandKind.IdResult, 7),
          operand(OperandKind.IdRef, 1),
          Vec(operand(OperandKind.IdRef, 6), u32)
        ),
        Instruction.Variable(
          operand(OperandKind.IdResultType, 5),
          globalId,
          operand(OperandKind.StorageClass, spv.StorageClass.Input),
          None
        ),
        Instruction.Decorate(
          globalId,
          operand(OperandKind.Decoration, spv.Decoration.BuiltIn, spv.BuiltIn.GlobalInvocationId)
        ),
        Instruction.EntryPoint(
          operand(OperandKind.ExecutionModel, spv.ExecutionModel.Kernel),
          function,
          stringOperand("fill"),
          Vec(globalId)
        ),
        Instruction.Function(
          operand(OperandKind.IdResultType, 1),
          function,
          operand(OperandKind.FunctionControl, spv.FunctionControl.None),
          operand(OperandKind.IdRef, 7)
        ),
        Instruction.FunctionParameter(operand(OperandKind.IdResultType, 6), output),
        Instruction.FunctionParameter(operand(OperandKind.IdResultType, 2), fillValue),
        Instruction.Label(operand(OperandKind.IdResult, 12)),
        Instruction.Load(operand(OperandKind.IdResultType, 4), loadedGlobal, globalId, None),
        Instruction.CompositeExtract(u64, linearId, loadedGlobal, Vec(operand(OperandKind.LiteralInteger, 0))),
        Instruction.PtrAccessChain(operand(OperandKind.IdResultType, 6), destination, output, linearId, Vec.empty),
        Instruction.Store(destination, fillValue, None),
        Instruction.Return(),
        Instruction.FunctionEnd()
      ),
      Endianness.Little
    )
    val allocation = AllocationId(1L)
    val memory     = ArrayMemory(allocation, 40)
    val dispatch   = KernelTask
      .validated(
        "fill",
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
      TaskControl(TaskControl.Limits(maxInstructions = 1L))
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
    class RecordingParallelism extends Workgroups {

      override def shouldParallel(tasks: Long >= 0, workItems: Long >= 0): Boolean = tasks.raw > 1L
      override def parallelFor(tasks: Long >= 0)(
          task: Long >= 0 => Either[Fault, Unit]
      ): Either[Fault, Unit] = {
        parallelCalls.incrementAndGet()
        Workgroups.Sequential.parallelFor(tasks)(task)
      }
    }
    val unrelatedAtomic = module.copy(
      header = module.header.copy(bound = Word(128)),
      instructions = module.instructions.take(10) ++ Vec(
        Instruction.Constant(
          u32,
          operand(OperandKind.IdResult, 103),
          operand(OperandKind.LiteralContextDependentNumber, 1)
        ),
        Instruction.Constant(
          u32,
          operand(OperandKind.IdResult, 104),
          operand(OperandKind.LiteralContextDependentNumber, 0)
        )
      ) ++ module.instructions.drop(10) ++ Vec(
        Instruction.Function(
          operand(OperandKind.IdResultType, 1),
          operand(OperandKind.IdResult, 100),
          operand(OperandKind.FunctionControl, spv.FunctionControl.None),
          operand(OperandKind.IdRef, 7)
        ),
        Instruction.FunctionParameter(operand(OperandKind.IdResultType, 6), operand(OperandKind.IdResult, 101)),
        Instruction.FunctionParameter(u32, operand(OperandKind.IdResult, 102)),
        Instruction.Label(operand(OperandKind.IdResult, 105)),
        Instruction.AtomicStore(
          operand(OperandKind.IdRef, 101),
          operand(OperandKind.IdScope, 103),
          operand(OperandKind.IdMemorySemantics, 104),
          operand(OperandKind.IdRef, 102)
        ),
        Instruction.Return(),
        Instruction.FunctionEnd()
      )
    )
    val parallelMemory = ArrayMemory(allocation, 40)
    val workgroups     = RecordingParallelism()
    assertEquals(
      VMInterpreter.execute(
        loadUnchecked(unrelatedAtomic).toOption.get,
        dispatch,
        parallelMemory,
        TaskControl(),
        workgroups,
        _ => Right(())
      ),
      Right(())
    )
    assertEquals(parallelMemory.bytes, memory.bytes)
    assertEquals(parallelCalls.get(), 1)

    // Alternate immediately available lanes with suspended host reads. A completed lane's frame may be reused,
    // but the suspended lanes must keep their own IDs, arguments and values until the transport is flushed.
    val increment = module.copy(instructions = module.instructions.flatMap {
      case Instruction.Store(pointer, value, _) => Vec(
          Instruction.Load(u32, operand(OperandKind.IdResult, 16), pointer, None),
          Instruction.IAdd(u32, operand(OperandKind.IdResult, 17), operand(OperandKind.IdRef, 16), value),
          Instruction.Store(pointer, operand(OperandKind.IdRef, 17), None)
        )
      case instruction => Vec(instruction)
    })
    for (schedule <- List[Workgroups](Workgroups.Sequential, RecordingParallelism())) {
      val backing = ArrayMemory(allocation, 40)
      val initial = Vec.from(0 until 8).flatMap(index => LittleEndian.toVec(index.toLong, 4))
      assertEquals(backing.write(allocation, 4L, initial), Right(()))
      class MixedMemory extends Memory {
        private val pending                                = scala.collection.mutable.Set.empty[Long]
        private val ready                                  = scala.collection.mutable.Set.empty[Long]
        override def defersHostReads: Boolean              = true
        override def flushHostReads(): Either[Fault, Unit] = {
          val _ = ready.addAll(pending)
          pending.clear()
          Right(())
        }
        override def read(region: AllocationId, offset: Long >= 0, size: Int >= 0): Either[Fault, Vec[Byte]] =
          awaitRead(readDeferred(region, offset, size))
        override def readDeferred(
            region: AllocationId,
            offset: Long >= 0,
            size: Int >= 0
        ): Memory.ReadResult[Vec[Byte]] =
          if (offset.raw % 8L == 0L || ready.contains(offset.raw)) backing.read(region, offset, size)
          else {
            val _ = pending.add(offset.raw)
            Left(Memory.Pending)
          }
        override def readValueDeferred(
            region: AllocationId,
            offset: Long >= 0,
            size: Int where Between[1, 8]
        ): Memory.ReadResult[Long] =
          readDeferred(region, offset, size.nonNegative).map(bytes =>
            LittleEndian.decode(bytes, 0, size.scalarByteWidth)
          )
        override def write(region: AllocationId, offset: Long >= 0, bytes: Vec[Byte]): Either[Fault, Unit] =
          backing.write(region, offset, bytes)
      }
      assertEquals(
        VMInterpreter.execute(
          loadUnchecked(increment).toOption.get,
          dispatch,
          MixedMemory(),
          TaskControl(),
          schedule,
          _ => Right(())
        ),
        Right(())
      )
      val expected = Vec.from(0 until 8).flatMap(index => LittleEndian.toVec(0x12345678L + index, 4))
      assertEquals(backing.bytes, Vec.fill(4)(0.toByte) ++ expected ++ Vec.fill(4)(0.toByte))
    }

    val workgroupSizeModule = module.copy(instructions =
      module.instructions.updated(
        8,
        Instruction.Decorate(
          globalId,
          operand(OperandKind.Decoration, spv.Decoration.BuiltIn, spv.BuiltIn.WorkgroupSize)
        )
      )
    )
    val workgroupAllocation = AllocationId(17L)
    val workgroupMemory     = ArrayMemory(workgroupAllocation, 40)
    val workgroupDispatch   = KernelTask
      .validated(
        "fill",
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
        Instruction.TypeVector(operand(OperandKind.IdResult, 4), u64, operand(OperandKind.LiteralInteger, 2))
      )
    )
    executeUnchecked(malformed, dispatch, memory) match {
      case Left(Fault.InvalidTask(message)) =>
        assertEquals(message, "SPIR-V BuiltIn 28 requires 3 integer lanes, result type %4 has 2")
      case result => fail(s"expected typed malformed BuiltIn shape failure, found $result")
    }
  }

  test("dispatch honors explicit subgroup size and materializes subgroup built-ins") {
    val voidType           = operand(OperandKind.IdResult, 1)
    val u32                = operand(OperandKind.IdResult, 2)
    val u64                = operand(OperandKind.IdResult, 3)
    val vector3            = operand(OperandKind.IdResult, 4)
    val globalIdType       = operand(OperandKind.IdResult, 5)
    val subgroupIdType     = operand(OperandKind.IdResult, 6)
    val outputType         = operand(OperandKind.IdResult, 7)
    val functionType       = operand(OperandKind.IdResult, 8)
    val globalId           = operand(OperandKind.IdResult, 9)
    val subgroupId         = operand(OperandKind.IdResult, 10)
    val function           = operand(OperandKind.IdResult, 11)
    val output             = operand(OperandKind.IdResult, 12)
    val loadedGlobalId     = operand(OperandKind.IdResult, 14)
    val localLinearId      = operand(OperandKind.IdResult, 15)
    val loadedSubgroupId   = operand(OperandKind.IdResult, 16)
    val destination        = operand(OperandKind.IdResult, 17)
    val undefValue         = operand(OperandKind.IdResult, 18)
    val subgroupDecoration = 11
    val instructions       = Vec(
      Instruction.TypeVoid(voidType),
      Instruction.TypeInt(u32, operand(OperandKind.LiteralInteger, 32), operand(OperandKind.LiteralInteger, 0)),
      Instruction.TypeInt(u64, operand(OperandKind.LiteralInteger, 64), operand(OperandKind.LiteralInteger, 0)),
      Instruction.TypeVector(vector3, u64, operand(OperandKind.LiteralInteger, 3)),
      Instruction.TypePointer(globalIdType, operand(OperandKind.StorageClass, spv.StorageClass.Input), vector3),
      Instruction.TypePointer(subgroupIdType, operand(OperandKind.StorageClass, spv.StorageClass.Input), u32),
      Instruction.TypePointer(outputType, operand(OperandKind.StorageClass, spv.StorageClass.CrossWorkgroup), u32),
      Instruction.TypeFunction(functionType, voidType, Vec(outputType)),
      Instruction.Variable(globalIdType, globalId, operand(OperandKind.StorageClass, spv.StorageClass.Input), None),
      Instruction.Variable(subgroupIdType, subgroupId, operand(OperandKind.StorageClass, spv.StorageClass.Input), None),
      Instruction.Decorate(
        globalId,
        operand(OperandKind.Decoration, spv.Decoration.BuiltIn, spv.BuiltIn.GlobalInvocationId)
      ),
      Instruction.Decorate(subgroupId, operand(OperandKind.Decoration, spv.Decoration.BuiltIn, spv.BuiltIn.SubgroupId)),
      Instruction.Undef(u32, undefValue),
      Instruction.EntryPoint(
        operand(OperandKind.ExecutionModel, spv.ExecutionModel.Kernel),
        function,
        stringOperand("subgroups"),
        Vec(globalId, subgroupId)
      ),
      Instruction.ExecutionMode(function, operand(OperandKind.ExecutionMode, spv.ExecutionMode.SubgroupSize, 32)),
      Instruction.Function(
        voidType,
        function,
        operand(OperandKind.FunctionControl, spv.FunctionControl.None),
        functionType
      ),
      Instruction.FunctionParameter(outputType, output),
      Instruction.Label(operand(OperandKind.IdResult, 13)),
      Instruction.Load(vector3, loadedGlobalId, globalId, None),
      Instruction.CompositeExtract(u64, localLinearId, loadedGlobalId, Vec(operand(OperandKind.LiteralInteger, 0))),
      Instruction.Load(u32, loadedSubgroupId, subgroupId, None),
      Instruction.PtrAccessChain(outputType, destination, output, localLinearId, Vec.empty),
      Instruction.Store(destination, loadedSubgroupId, None),
      Instruction.Return(),
      Instruction.FunctionEnd()
    )
    val module =
      Module(Header(ISA.version, Word(0), Word(19), Word(0)), instructions, Endianness.Little)
    val allocation = AllocationId(31L)
    val dispatch   = KernelTask
      .validated(
        "subgroups",
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
        Instruction.Decorate(
          subgroupId,
          operand(OperandKind.Decoration, spv.Decoration.BuiltIn, spv.BuiltIn.NumSubgroups)
        )
      )
    )
    val numSubgroupsMemory = ArrayMemory(allocation, 256)
    assertEquals(executeUnchecked(numSubgroupsModule, dispatch, numSubgroupsMemory), Right(()))
    assertEquals(numSubgroupsMemory.bytes, Vec.fill(64)(LittleEndian.toVec(2L, 4)).flatten)

    val numEnqueuedSubgroupsModule = module.copy(instructions =
      instructions.updated(
        subgroupDecoration,
        Instruction.Decorate(
          subgroupId,
          operand(OperandKind.Decoration, spv.Decoration.BuiltIn, spv.BuiltIn.NumEnqueuedSubgroups)
        )
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
    val voidType     = operand(OperandKind.IdResult, 1)
    val u32          = operand(OperandKind.IdResult, 2)
    val length       = operand(OperandKind.IdResult, 3)
    val arrayType    = operand(OperandKind.IdResult, 4)
    val globalType   = operand(OperandKind.IdResult, 5)
    val pointerType  = operand(OperandKind.IdResult, 6)
    val functionType = operand(OperandKind.IdResult, 7)
    val first        = operand(OperandKind.IdResult, 8)
    val second       = operand(OperandKind.IdResult, 9)
    val initial      = operand(OperandKind.IdResult, 10)
    val global       = operand(OperandKind.IdResult, 11)
    val function     = operand(OperandKind.IdResult, 12)
    val output       = operand(OperandKind.IdResult, 13)
    val element      = operand(OperandKind.IdResult, 14)
    val loaded       = operand(OperandKind.IdResult, 15)
    val one          = operand(OperandKind.IdResult, 16)
    val module       = Module(
      Header(ISA.version, Word(0), Word(24), Word(0)),
      Vec(
        Instruction.TypeVoid(voidType),
        Instruction.TypeInt(u32, operand(OperandKind.LiteralInteger, 32), operand(OperandKind.LiteralInteger, 0)),
        Instruction.Constant(u32, length, operand(OperandKind.LiteralContextDependentNumber, 2)),
        Instruction.TypeArray(arrayType, u32, length),
        Instruction.TypePointer(
          globalType,
          operand(OperandKind.StorageClass, spv.StorageClass.CrossWorkgroup),
          arrayType
        ),
        Instruction.TypePointer(pointerType, operand(OperandKind.StorageClass, spv.StorageClass.CrossWorkgroup), u32),
        Instruction.TypeFunction(functionType, voidType, Vec(pointerType)),
        Instruction.Constant(u32, first, operand(OperandKind.LiteralContextDependentNumber, 17)),
        Instruction.Constant(u32, second, operand(OperandKind.LiteralContextDependentNumber, 29)),
        Instruction.Constant(u32, one, operand(OperandKind.LiteralContextDependentNumber, 1)),
        Instruction.ConstantComposite(arrayType, initial, Vec(first, second)),
        Instruction.Variable(
          globalType,
          global,
          operand(OperandKind.StorageClass, spv.StorageClass.CrossWorkgroup),
          Some(initial)
        ),
        Instruction.Decorate(global, operand(OperandKind.Decoration, spv.Decoration.Constant)),
        Instruction.EntryPoint(
          operand(OperandKind.ExecutionModel, spv.ExecutionModel.Kernel),
          function,
          stringOperand("global"),
          Vec(global)
        ),
        Instruction.Function(
          voidType,
          function,
          operand(OperandKind.FunctionControl, spv.FunctionControl.None),
          functionType
        ),
        Instruction.FunctionParameter(pointerType, output),
        Instruction.Label(operand(OperandKind.IdResult, 17)),
        Instruction.AccessChain(pointerType, element, global, Vec(one)),
        Instruction.Load(u32, loaded, element, None),
        Instruction.Store(output, loaded, None),
        Instruction.Return(),
        Instruction.FunctionEnd()
      ),
      Endianness.Little
    )
    val allocation = AllocationId(20L)
    val memory     = ArrayMemory(allocation, 4)
    val dispatch   = KernelTask
      .validated(
        "global",
        Dimensions.One,
        Dimensions.One,
        Vec(KernelTask.Argument.Memory(allocation, 0L))
      )
      .toOption
      .get

    assertEquals(executeUnchecked(module, dispatch, memory), Right(()))
    assertEquals(memory.bytes, LittleEndian.toVec(29L, 4))
  }

  for (specialized <- Vec(false, true))
    test(s"nested composite constants materialize null pointer leaves (specialized=$specialized)") {
      val voidType     = operand(OperandKind.IdResult, 1)
      val u32          = operand(OperandKind.IdResult, 2)
      val pointer      = operand(OperandKind.IdResult, 3)
      val length       = operand(OperandKind.IdResult, 4)
      val arrayType    = operand(OperandKind.IdResult, 5)
      val structure    = operand(OperandKind.IdResult, 6)
      val outputType   = operand(OperandKind.IdResult, 7)
      val functionType = operand(OperandKind.IdResult, 8)
      val nullPointer  = operand(OperandKind.IdResult, 9)
      val array        = operand(OperandKind.IdResult, 10)
      val tag          = operand(OperandKind.IdResult, 11)
      val initial      = operand(OperandKind.IdResult, 12)
      val function     = operand(OperandKind.IdResult, 13)
      val output       = operand(OperandKind.IdResult, 14)
      def composite(resultType: Operand, destination: Operand, constituents: Vec[Operand]): Instruction =
        if (specialized) Instruction.SpecConstantComposite(resultType, destination, constituents)
        else Instruction.ConstantComposite(resultType, destination, constituents)
      val module = Module(
        Header(ISA.version, Word(0), Word(20), Word(0)),
        Vec(
          Instruction.TypeVoid(voidType),
          Instruction.TypeInt(u32, operand(OperandKind.LiteralInteger, 32), operand(OperandKind.LiteralInteger, 0)),
          Instruction.TypePointer(pointer, operand(OperandKind.StorageClass, 5), u32),
          Instruction.Constant(u32, length, operand(OperandKind.LiteralContextDependentNumber, 2)),
          Instruction.TypeArray(arrayType, pointer, length),
          Instruction.TypeStruct(structure, Vec(u32, arrayType)),
          Instruction.TypePointer(outputType, operand(OperandKind.StorageClass, 5), structure),
          Instruction.TypeFunction(functionType, voidType, Vec(outputType)),
          Instruction.ConstantNull(pointer, nullPointer),
          composite(arrayType, array, Vec(nullPointer, nullPointer)),
          Instruction.Constant(u32, tag, operand(OperandKind.LiteralContextDependentNumber, 17)),
          composite(structure, initial, Vec(tag, array)),
          Instruction.EntryPoint(operand(OperandKind.ExecutionModel, 6), function, stringOperand("nulls"), Vec.empty),
          Instruction.Function(voidType, function, operand(OperandKind.FunctionControl, 0), functionType),
          Instruction.FunctionParameter(outputType, output),
          Instruction.Label(operand(OperandKind.IdResult, 15)),
          Instruction.Store(output, initial, None),
          Instruction.Return(),
          Instruction.FunctionEnd()
        ),
        Endianness.Little
      )
      val allocation = AllocationId(20L)
      val memory     = ArrayMemory(allocation, 24)
      assertEquals(memory.write(allocation, 0L, Vec.fill(24)(0xff.toByte)), Right(()))
      val dispatch = KernelTask.validated(
        "nulls",
        Dimensions.One,
        Dimensions.One,
        Vec(KernelTask.Argument.Memory(allocation, 0L))
      ).toOption.get
      assertEquals(executeUnchecked(module, dispatch, memory), Right(()))
      assertEquals(memory.bytes, LittleEndian.toVec(17L, 4) ++ Vec.fill(20)(0.toByte))
    }

  test("Workgroup and Private variables are rematerialized at their SPIR-V lifetime boundaries") {
    def module(storageClass: Int, entryPoint: String): Module = {
      val voidType      = operand(OperandKind.IdResult, 1)
      val u32           = operand(OperandKind.IdResult, 2)
      val localPointer  = operand(OperandKind.IdResult, 3)
      val outputPointer = operand(OperandKind.IdResult, 4)
      val functionType  = operand(OperandKind.IdResult, 5)
      val initial       = operand(OperandKind.IdResult, 6)
      val one           = operand(OperandKind.IdResult, 7)
      val variable      = operand(OperandKind.IdResult, 8)
      val function      = operand(OperandKind.IdResult, 9)
      val output        = operand(OperandKind.IdResult, 10)
      val loaded        = operand(OperandKind.IdResult, 12)
      val incremented   = operand(OperandKind.IdResult, 13)
      val instructions  = Vec(
        Instruction.TypeVoid(voidType),
        Instruction.TypeInt(u32, operand(OperandKind.LiteralInteger, 32), operand(OperandKind.LiteralInteger, 0)),
        Instruction.TypePointer(localPointer, operand(OperandKind.StorageClass, storageClass), u32),
        Instruction.TypePointer(outputPointer, operand(OperandKind.StorageClass, spv.StorageClass.CrossWorkgroup), u32),
        Instruction.TypeFunction(functionType, voidType, Vec(outputPointer)),
        Instruction.Constant(u32, initial, operand(OperandKind.LiteralContextDependentNumber, 7)),
        Instruction.Constant(u32, one, operand(OperandKind.LiteralContextDependentNumber, 1)),
        Instruction.Variable(localPointer, variable, operand(OperandKind.StorageClass, storageClass), Some(initial)),
        Instruction.EntryPoint(
          operand(OperandKind.ExecutionModel, spv.ExecutionModel.Kernel),
          function,
          stringOperand(entryPoint),
          Vec(variable)
        ),
        Instruction.Function(
          voidType,
          function,
          operand(OperandKind.FunctionControl, spv.FunctionControl.None),
          functionType
        ),
        Instruction.FunctionParameter(outputPointer, output),
        Instruction.Label(operand(OperandKind.IdResult, 11)),
        Instruction.Load(u32, loaded, variable, None),
        Instruction.Store(output, loaded, None),
        Instruction.IAdd(u32, incremented, loaded, one),
        Instruction.Store(variable, incremented, None),
        Instruction.Return(),
        Instruction.FunctionEnd()
      )
      Module(Header(ISA.version, Word(0), Word(14), Word(0)), instructions, Endianness.Little)
    }

    val allocation        = AllocationId(41L)
    val workgroupMemory   = ArrayMemory(allocation, 4)
    val workgroupDispatch = KernelTask
      .validated(
        "workgroup_lifetime",
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
        "private_lifetime",
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
    val voidType       = operand(OperandKind.IdResult, 1)
    val u32            = operand(OperandKind.IdResult, 2)
    val two            = operand(OperandKind.IdResult, 3)
    val arrayType      = operand(OperandKind.IdResult, 4)
    val structureType  = operand(OperandKind.IdResult, 5)
    val outputPointer  = operand(OperandKind.IdResult, 6)
    val functionType   = operand(OperandKind.IdResult, 7)
    val first          = operand(OperandKind.IdResult, 8)
    val second         = operand(OperandKind.IdResult, 9)
    val header         = operand(OperandKind.IdResult, 10)
    val arrayValue     = operand(OperandKind.IdResult, 11)
    val structureValue = operand(OperandKind.IdResult, 12)
    val function       = operand(OperandKind.IdResult, 13)
    val output         = operand(OperandKind.IdResult, 14)
    val one            = operand(OperandKind.IdResult, 16)
    val loaded         = operand(OperandKind.IdResult, 17)
    val secondOutput   = operand(OperandKind.IdResult, 18)
    val module         = Module(
      Header(ISA.version, Word(0), Word(19), Word(0)),
      Vec(
        Instruction.TypeVoid(voidType),
        Instruction.TypeInt(u32, operand(OperandKind.LiteralInteger, 32), operand(OperandKind.LiteralInteger, 0)),
        Instruction.Constant(u32, two, operand(OperandKind.LiteralContextDependentNumber, 2)),
        Instruction.TypeArray(arrayType, u32, two),
        Instruction.TypeStruct(structureType, Vec(u32, arrayType)),
        Instruction.TypePointer(
          outputPointer,
          operand(OperandKind.StorageClass, spv.StorageClass.CrossWorkgroup),
          structureType
        ),
        Instruction.TypeFunction(functionType, voidType, Vec(outputPointer)),
        Instruction.Constant(u32, first, operand(OperandKind.LiteralContextDependentNumber, 11)),
        Instruction.Constant(u32, second, operand(OperandKind.LiteralContextDependentNumber, 22)),
        Instruction.Constant(u32, header, operand(OperandKind.LiteralContextDependentNumber, 5)),
        Instruction.Constant(u32, one, operand(OperandKind.LiteralContextDependentNumber, 1)),
        Instruction.ConstantComposite(arrayType, arrayValue, Vec(first, second)),
        Instruction.ConstantComposite(structureType, structureValue, Vec(header, arrayValue)),
        Instruction.Decorate(arrayType, operand(OperandKind.Decoration, spv.Decoration.ArrayStride, 16)),
        Instruction.MemberDecorate(
          structureType,
          operand(OperandKind.LiteralInteger, 1),
          operand(OperandKind.Decoration, spv.Decoration.Offset, 16)
        ),
        Instruction.EntryPoint(
          operand(OperandKind.ExecutionModel, spv.ExecutionModel.Kernel),
          function,
          stringOperand("layout"),
          Vec.empty
        ),
        Instruction.Function(
          voidType,
          function,
          operand(OperandKind.FunctionControl, spv.FunctionControl.None),
          functionType
        ),
        Instruction.FunctionParameter(outputPointer, output),
        Instruction.Label(operand(OperandKind.IdResult, 15)),
        Instruction.Store(output, structureValue, None),
        Instruction.Load(structureType, loaded, output, None),
        Instruction.PtrAccessChain(outputPointer, secondOutput, output, one, Vec.empty),
        Instruction.Store(secondOutput, loaded, None),
        Instruction.Return(),
        Instruction.FunctionEnd()
      ),
      Endianness.Little
    )
    val allocation = AllocationId(43L)
    val memory     = ArrayMemory(allocation, 96)
    val dispatch   = KernelTask
      .validated(
        "layout",
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
    val voidType     = operand(OperandKind.IdResult, 1)
    val u8           = operand(OperandKind.IdResult, 2)
    val u32          = operand(OperandKind.IdResult, 3)
    val u64          = operand(OperandKind.IdResult, 4)
    val three        = operand(OperandKind.IdResult, 5)
    val padding      = operand(OperandKind.IdResult, 6)
    val storage      = operand(OperandKind.IdResult, 7)
    val payloadBase  = operand(OperandKind.IdResult, 8)
    val payload      = operand(OperandKind.IdResult, 9)
    val optionalBase = operand(OperandKind.IdResult, 10)
    val optional     = operand(OperandKind.IdResult, 11)
    val optionalPtr  = operand(OperandKind.IdResult, 12)
    val bytePtr      = operand(OperandKind.IdResult, 13)
    val functionType = operand(OperandKind.IdResult, 14)
    val zero         = operand(OperandKind.IdResult, 15)
    val one          = operand(OperandKind.IdResult, 16)
    val function     = operand(OperandKind.IdResult, 17)
    val input        = operand(OperandKind.IdResult, 18)
    val output       = operand(OperandKind.IdResult, 19)
    val second       = operand(OperandKind.IdResult, 20)
    val engaged      = operand(OperandKind.IdResult, 21)
    val loaded       = operand(OperandKind.IdResult, 22)
    val module       = Module(
      Header(ISA.version, Word(0), Word(24), Word(0)),
      Vec(
        Instruction.TypeVoid(voidType),
        Instruction.TypeInt(u8, operand(OperandKind.LiteralInteger, 8), operand(OperandKind.LiteralInteger, 0)),
        Instruction.TypeInt(u32, operand(OperandKind.LiteralInteger, 32), operand(OperandKind.LiteralInteger, 0)),
        Instruction.TypeInt(u64, operand(OperandKind.LiteralInteger, 64), operand(OperandKind.LiteralInteger, 0)),
        Instruction.Constant(u64, three, operand(OperandKind.LiteralContextDependentNumber, 3)),
        Instruction.TypeArray(padding, u8, three),
        Instruction.TypeStruct(storage, Vec(u32)),
        Instruction.TypeStruct(payloadBase, Vec(storage, u8)),
        Instruction.Decorate(payloadBase, operand(OperandKind.Decoration, spv.Decoration.CPacked)),
        Instruction.TypeStruct(payload, Vec(payloadBase, padding)),
        Instruction.TypeStruct(optionalBase, Vec(payload)),
        Instruction.TypeStruct(optional, Vec(optionalBase)),
        Instruction.TypePointer(
          optionalPtr,
          operand(OperandKind.StorageClass, spv.StorageClass.CrossWorkgroup),
          optional
        ),
        Instruction.TypePointer(bytePtr, operand(OperandKind.StorageClass, spv.StorageClass.CrossWorkgroup), u8),
        Instruction.TypeFunction(functionType, voidType, Vec(optionalPtr, bytePtr)),
        Instruction.Constant(u32, zero, operand(OperandKind.LiteralContextDependentNumber, 0)),
        Instruction.Constant(u32, one, operand(OperandKind.LiteralContextDependentNumber, 1)),
        Instruction.EntryPoint(
          operand(OperandKind.ExecutionModel, spv.ExecutionModel.Kernel),
          function,
          stringOperand("packed_stride"),
          Vec.empty
        ),
        Instruction.Function(
          voidType,
          function,
          operand(OperandKind.FunctionControl, spv.FunctionControl.None),
          functionType
        ),
        Instruction.FunctionParameter(optionalPtr, input),
        Instruction.FunctionParameter(bytePtr, output),
        Instruction.Label(operand(OperandKind.IdResult, 23)),
        Instruction.PtrAccessChain(optionalPtr, second, input, one, Vec.empty),
        Instruction.AccessChain(bytePtr, engaged, second, Vec(zero, zero, zero, one)),
        Instruction.Load(u8, loaded, engaged, None),
        Instruction.Store(output, loaded, None),
        Instruction.Return(),
        Instruction.FunctionEnd()
      ),
      Endianness.Little
    )
    val allocation = AllocationId(122L)
    val memory     = ArrayMemory(allocation, 40)
    assertEquals(memory.write(allocation, 12L, Vec(1.toByte)), Right(()))
    val dispatch = KernelTask
      .validated(
        "packed_stride",
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
    val voidType = operand(OperandKind.IdResult, 1)
    val u32      = operand(OperandKind.IdResult, 2)
    val pointer  = operand(OperandKind.IdResult, 3)
    val length   = operand(OperandKind.IdResult, 4)
    val array    = operand(OperandKind.IdResult, 5)
    val module   = Module(
      Header(ISA.version, Word(0), Word(8), Word(0)),
      Vec(
        Instruction.TypeVoid(voidType),
        Instruction.TypeInt(u32, operand(OperandKind.LiteralInteger, 32), operand(OperandKind.LiteralInteger, 0)),
        Instruction.TypePointer(pointer, operand(OperandKind.StorageClass, spv.StorageClass.CrossWorkgroup), u32),
        Instruction.Constant(u32, length, operand(OperandKind.LiteralContextDependentNumber, 2)),
        Instruction.TypeArray(array, pointer, length),
        Instruction.Decorate(array, operand(OperandKind.Decoration, spv.Decoration.ArrayStride, 16))
      ),
      Endianness.Little
    )

    assertEquals(
      VMInterpreter.pointerLeafOffsetsForTest(module, 5).map(_.map { case (offset, kind) => offset.raw -> kind }),
      Right(Vec(0L -> 3, 16L -> 3))
    )
  }

  test("ByVal byte-array aggregates retain their private layout when their first word is relocated") {
    def result(value: Int) = operand(OperandKind.IdResult, value)
    val module             = Module(
      Header(ISA.version, Word(0), Word(34), Word(0)),
      Vec(
        Instruction.TypeVoid(result(1)),
        Instruction.TypeInt(result(2), operand(OperandKind.LiteralInteger, 64), operand(OperandKind.LiteralInteger, 0)),
        Instruction.TypeInt(result(3), operand(OperandKind.LiteralInteger, 8), operand(OperandKind.LiteralInteger, 0)),
        Instruction.TypeInt(result(4), operand(OperandKind.LiteralInteger, 32), operand(OperandKind.LiteralInteger, 0)),
        Instruction.Constant(result(2), result(5), operand(OperandKind.LiteralContextDependentNumber, 8, 0)),
        Instruction.TypeArray(result(6), result(3), result(5)),
        Instruction.TypeStruct(result(7), Vec(result(6), result(2))),
        Instruction.Decorate(result(7), operand(OperandKind.Decoration, spv.Decoration.CPacked)),
        Instruction.TypePointer(result(8), operand(OperandKind.StorageClass, spv.StorageClass.Function), result(7)),
        Instruction.TypePointer(result(9), operand(OperandKind.StorageClass, spv.StorageClass.Function), result(2)),
        Instruction.TypePointer(
          result(10),
          operand(OperandKind.StorageClass, spv.StorageClass.CrossWorkgroup),
          result(2)
        ),
        Instruction.TypePointer(result(11), operand(OperandKind.StorageClass, spv.StorageClass.Generic), result(4)),
        Instruction.TypeFunction(result(14), result(1), Vec(result(8), result(10))),
        Instruction.Constant(result(2), result(15), operand(OperandKind.LiteralContextDependentNumber, 1, 0)),
        Instruction.Decorate(
          result(21),
          operand(OperandKind.Decoration, spv.Decoration.FuncParamAttr, 2)
        ), // FuncParamAttr ByVal
        Instruction.EntryPoint(
          operand(OperandKind.ExecutionModel, spv.ExecutionModel.Kernel),
          result(20),
          stringOperand("byval_bytes"),
          Vec.empty
        ),
        Instruction.Function(
          result(1),
          result(20),
          operand(OperandKind.FunctionControl, spv.FunctionControl.None),
          result(14)
        ),
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
      AllocationId(1L)                -> AllocationId(3L),
      AllocationId(9007199254740993L) -> AllocationId(9007199254740995L)
    )
    for ((needle, output) <- allocationPairs) {
      class Memory extends vm.Memory {
        val storage: Map[AllocationId.Type, Array[Byte]] =
          Map(needle -> LittleEndian.toArray(7L, 4), output -> Array.ofDim[Byte](16))
        def read(
            id: AllocationId,
            offset: Long >= 0,
            size: Int >= 0
        ): Either[Fault, Vec[Byte]] =
          if (offset.raw > storage(id).length - size.raw)
            Left(Fault.InvalidTask("test read exceeds allocation"))
          else Right(Vec.from(storage(id).slice(offset.raw.toInt, offset.raw.toInt + size.raw)))
        def write(
            id: AllocationId,
            offset: Long >= 0,
            bytes: Vec[Byte]
        ): Either[Fault, Unit] = {
          val _ = bytes.copyToArray(storage(id), offset.raw.toInt, bytes.size); Right(())
        }
      }
      val memory    = Memory()
      val aggregate = LittleEndian.toVec(0x12345678L, 8) ++
        LittleEndian.toVec(99L, 8)
      val dispatch = KernelTask.validated(
        "byval_bytes",
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
    val voidType      = operand(OperandKind.IdResult, 1)
    val u32           = operand(OperandKind.IdResult, 2)
    val valuePointer  = operand(OperandKind.IdResult, 3)
    val structureType = operand(OperandKind.IdResult, 4)
    val structurePtr  = operand(OperandKind.IdResult, 5)
    val functionType  = operand(OperandKind.IdResult, 6)
    val one           = operand(OperandKind.IdResult, 7)
    val expected      = operand(OperandKind.IdResult, 8)
    val function      = operand(OperandKind.IdResult, 9)
    val argument      = operand(OperandKind.IdResult, 10)
    val member        = operand(OperandKind.IdResult, 12)
    val module        = Module(
      Header(ISA.version, Word(0), Word(14), Word(0)),
      Vec(
        Instruction.TypeVoid(voidType),
        Instruction.TypeInt(u32, operand(OperandKind.LiteralInteger, 32), operand(OperandKind.LiteralInteger, 0)),
        Instruction.TypePointer(valuePointer, operand(OperandKind.StorageClass, spv.StorageClass.CrossWorkgroup), u32),
        Instruction.TypeStruct(structureType, Vec(valuePointer, u32)),
        Instruction.TypePointer(
          structurePtr,
          operand(OperandKind.StorageClass, spv.StorageClass.CrossWorkgroup),
          structureType
        ),
        Instruction.TypeFunction(functionType, voidType, Vec(structurePtr)),
        Instruction.Constant(u32, one, operand(OperandKind.LiteralContextDependentNumber, 1)),
        Instruction.Constant(u32, expected, operand(OperandKind.LiteralContextDependentNumber, 42)),
        Instruction.EntryPoint(
          operand(OperandKind.ExecutionModel, spv.ExecutionModel.Kernel),
          function,
          stringOperand("external_aggregate"),
          Vec.empty
        ),
        Instruction.Function(
          voidType,
          function,
          operand(OperandKind.FunctionControl, spv.FunctionControl.None),
          functionType
        ),
        Instruction.FunctionParameter(structurePtr, argument),
        Instruction.Label(operand(OperandKind.IdResult, 11)),
        Instruction.AccessChain(valuePointer, member, argument, Vec(one)),
        Instruction.Store(member, expected, None),
        Instruction.Return(),
        Instruction.FunctionEnd()
      ),
      Endianness.Little
    )
    val allocation = AllocationId(45L)
    val memory     = ArrayMemory(allocation, 12)
    val dispatch   = KernelTask
      .validated(
        "external_aggregate",
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

  test("cross-workgroup IDs, partial extents and workgroup barriers stay isolated across launches") {
    for {
      groups   <- interleaving.groups
      local    <- Vec[Int > 0](31, 32, 33)
      parallel <- Vec(false, true)
      barrier  <- Vec(false, true)
      producer <- if (barrier) Vec(2, local.raw - 1) else Vec(2)
    } {
      val count            = (local.raw * groups.raw - (if (barrier) 0 else 1)).unsafe[Int > 0]
      val offset: Int >= 0 = 7
      val allocation       = AllocationId(300L)
      val memory           = ArrayMemory(allocation, (offset.raw + count.raw + 2) * 4)
      val prepared         = loadUnchecked(interleaving.geometry(barrier, producer)).toOption.get
      for (seed <- Vec(1, 9)) {
        val schedule   = interleaving.Interleaving()
        val workgroups = if (parallel) schedule else Workgroups.Sequential
        val control    = TaskControl()
        val dispatch   = KernelTask.validated(
          "groups",
          testDimensions(local),
          testDimensions(groups),
          Vec(
            KernelTask.Argument.Memory(allocation, 4L),
            KernelTask.Argument.Scalar(LittleEndian.toVec(seed.toLong, 4)),
            KernelTask.Argument.Local(4L)
          ),
          globalSize = Some(testDimensions(count).toGlobalDimensions),
          globalOffset = Coordinates(offset, 0, 0)
        ).toOption.get
        assertEquals(
          VMInterpreter.execute(
            prepared,
            dispatch,
            schedule.InterleavedMemory(memory),
            control,
            workgroups,
            _ => Right(())
          ),
          Right(()),
          s"groups=$groups local=$local parallel=$parallel barrier=$barrier"
        )
        assertEquals(schedule.calls.get(), if (parallel && groups > 1) 1 else 0)
        val expected = Vec.from(0 until count.raw).flatMap { index =>
          LittleEndian.toVec(
            (index / local.raw + seed) * 1000L + (index % local.raw) * 10L + offset.raw + index,
            4
          )
        }
        assertEquals(memory.bytes, Vec.fill((offset.raw + 1) * 4)(0.toByte) ++ expected ++ Vec.fill(4)(0.toByte))
      }
    }
  }
}
