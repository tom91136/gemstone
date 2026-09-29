package net.kurobako.gemstone.isa.spirv.v1_6

import execution.*
import java.util.concurrent.atomic.AtomicReference
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.*
import net.kurobako.gemstone.isa.spirv.v1_6 as spv
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchGeometry.*
class ExecutionCollectiveSuite extends munit.FunSuite {

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
      Instruction.TypePointer(builtinType, operand("StorageClass", spv.StorageClass.Input), vector3),
      Instruction.TypePointer(outputType, operand("StorageClass", spv.StorageClass.CrossWorkgroup), u64),
      Instruction.TypeFunction(functionType, voidType, Vec(outputType)),
      Instruction.Constant(u32, subgroupScope, operand("LiteralContextDependentNumber", spv.Scope.Subgroup)),
      Instruction.Constant(u32, workgroupScope, operand("LiteralContextDependentNumber", spv.Scope.Workgroup)),
      Instruction.Constant(u64, three, operand("LiteralContextDependentNumber", 3)),
      Instruction.ConstantTrue(bool, trueValue),
      Instruction.Variable(builtinType, localBuiltin, operand("StorageClass", spv.StorageClass.Input), None),
      Instruction.Decorate(localBuiltin, operand("Decoration", spv.Decoration.BuiltIn, spv.BuiltIn.LocalInvocationId)),
      Instruction.EntryPoint(
        operand("ExecutionModel", spv.ExecutionModel.Kernel),
        function,
        stringOperand("collective"),
        Vec(localBuiltin)
      ),
      Instruction.Function(voidType, function, operand("FunctionControl", spv.FunctionControl.None), functionType),
      Instruction.FunctionParameter(outputType, output),
      Instruction.Label(operand("IdResult", 15)),
      Instruction.Load(vector3, localVector, localBuiltin, None),
      Instruction.CompositeExtract(u64, localX, localVector, Vec(operand("LiteralInteger", 0))),
      Instruction.ULessThan(bool, pred, localX, three),
      Instruction.GroupAny(bool, any, subgroupScope, pred),
      Instruction.LogicalNotEqual(bool, notAll, any, trueValue),
      Instruction.GroupUMin(u64, minimum, workgroupScope, operand("GroupOperation", spv.GroupOperation.Reduce), localX),
      Instruction.Store(output, minimum, None),
      Instruction.Return(),
      Instruction.FunctionEnd()
    )
    val module =
      Module(Header(ISA.version, Word(0), Word(24), Word(0)), instructions, Endianness.Little)
    val allocation = AllocationId(24L)
    val memory     = ArrayMemory(allocation, 8)
    val dispatch   = KernelTask
      .validated(
        "collective",
        Dimensions.One,
        Dimensions(8, 1, 1),
        Vec(KernelTask.Argument.Memory(allocation, 0L))
      )
      .toOption
      .get

    assertEquals(executeUnchecked(module, dispatch, memory), Right(()))
    assertEquals(memory.bytes, Vec.fill(8)(0.toByte))

    val workgroupAny = module.copy(instructions =
      instructions.updated(21, Instruction.GroupAny(bool, any, workgroupScope, pred))
    )
    assertEquals(executeUnchecked(workgroupAny, dispatch, memory), Right(()))

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
    val barrierDispatch = KernelTask
      .validated(
        "collective",
        Dimensions(8, 1, 1),
        Dimensions.One,
        Vec(KernelTask.Argument.Memory(allocation, 0L))
      )
      .toOption
      .get
    assertEquals(executeUnchecked(barrierModule, barrierDispatch, barrierMemory), Right(()))
    assertEquals(barrierMemory.bytes, Vec.fill(8)(0.toByte))

    val scanDestination  = operand("IdResult", 23)
    val broadcast        = operand("IdResult", 24)
    val scanInstructions = instructions
      .updated(
        23,
        Instruction.GroupIAdd(
          u64,
          minimum,
          workgroupScope,
          operand("GroupOperation", spv.GroupOperation.InclusiveScan),
          localX
        )
      )
      .updated(24, Instruction.PtrAccessChain(outputType, scanDestination, output, localX, Vec.empty))
      .patch(25, Vec(Instruction.Store(scanDestination, minimum, None)), 0)
    val scanModule = module.copy(
      instructions = scanInstructions,
      header = module.header.copy(bound = Word(25))
    )
    val scanDispatch = KernelTask
      .validated(
        "collective",
        Dimensions(8, 1, 1),
        Dimensions.One,
        Vec(KernelTask.Argument.Memory(allocation, 0L))
      )
      .toOption
      .get
    val scanMemory = ArrayMemory(allocation, 64)
    assertEquals(executeUnchecked(scanModule, scanDispatch, scanMemory), Right(()))
    assertEquals(
      scanMemory.bytes,
      Vec.from(Vec(0L, 1L, 3L, 6L, 10L, 15L, 21L, 28L).view.flatMap(LittleEndian.toVec(_, 8)))
    )

    val reductionModule = scanModule.copy(instructions =
      scanInstructions.updated(
        23,
        Instruction.GroupIAdd(
          u64,
          minimum,
          workgroupScope,
          operand("GroupOperation", spv.GroupOperation.Reduce),
          localX
        )
      )
    )
    val reductionMemory = ArrayMemory(allocation, 64)
    assertEquals(executeUnchecked(reductionModule, scanDispatch, reductionMemory), Right(()))
    assertEquals(reductionMemory.bytes, Vec.fill(8)(LittleEndian.toVec(28L, 8)).flatten)

    val multiplicationModule = scanModule.copy(instructions =
      scanInstructions.updated(
        23,
        Instruction.GroupIMulKHR(
          u64,
          minimum,
          workgroupScope,
          operand("GroupOperation", spv.GroupOperation.Reduce),
          three
        )
      )
    )
    val multiplicationMemory = ArrayMemory(allocation, 64)
    assertEquals(executeUnchecked(multiplicationModule, scanDispatch, multiplicationMemory), Right(()))
    assertEquals(multiplicationMemory.bytes, Vec.fill(8)(LittleEndian.toVec(6561L, 8)).flatten)

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
        Instruction.GroupFAdd(
          f32,
          floatResult,
          workgroupScope,
          operand("GroupOperation", spv.GroupOperation.InclusiveScan),
          floatThree
        )
      )
      .updated(27, Instruction.Store(scanDestination, floatResult, None))
    val floatingScanModule = module.copy(
      instructions = floatingScanInstructions,
      header = module.header.copy(bound = Word(28))
    )
    val floatingScanMemory = ArrayMemory(allocation, 64)
    assertEquals(executeUnchecked(floatingScanModule, scanDispatch, floatingScanMemory), Right(()))
    assertEquals(
      floatingScanMemory.bytes,
      Vec.from((1 to 8).view.flatMap(index =>
        LittleEndian.toVec(java.lang.Float.floatToRawIntBits(3.0f * index).toLong, 4) ++
          Vec.fill(4)(0.toByte)
      ))
    )
    val floatingMultiplicationModule = floatingScanModule.copy(instructions =
      floatingScanInstructions.updated(
        25,
        Instruction.GroupFMulKHR(
          f32,
          floatResult,
          workgroupScope,
          operand("GroupOperation", spv.GroupOperation.InclusiveScan),
          floatThree
        )
      )
    )
    val floatingMultiplicationMemory = ArrayMemory(allocation, 64)
    assertEquals(
      executeUnchecked(floatingMultiplicationModule, scanDispatch, floatingMultiplicationMemory),
      Right(())
    )
    assertEquals(
      floatingMultiplicationMemory.bytes,
      Vec.from((1 to 8).view.flatMap(index =>
        LittleEndian.toVec(
          java.lang.Float.floatToRawIntBits(math.pow(3.0, index).toFloat).toLong,
          4
        ) ++
          Vec.fill(4)(0.toByte)
      ))
    )
    assertEquals(
      VMInterpreter.floatingAddBits(
        32,
        java.lang.Float.floatToRawIntBits(0.1f).toLong,
        java.lang.Float.floatToRawIntBits(0.2f).toLong
      ),
      java.lang.Float.floatToRawIntBits(0.1f + 0.2f).toLong & 0xffffffffL
    )
    assertEquals(
      VMInterpreter.floatingMultiplyBits(
        32,
        java.lang.Float.floatToRawIntBits(0.1f).toLong,
        java.lang.Float.floatToRawIntBits(0.2f).toLong
      ),
      java.lang.Float.floatToRawIntBits(0.1f * 0.2f).toLong & 0xffffffffL
    )

    val exclusiveModule = scanModule.copy(instructions =
      scanInstructions.updated(
        23,
        Instruction.GroupIAdd(
          u64,
          minimum,
          workgroupScope,
          operand("GroupOperation", spv.GroupOperation.ExclusiveScan),
          localX
        )
      )
    )
    val exclusiveMemory = ArrayMemory(allocation, 64)
    assertEquals(executeUnchecked(exclusiveModule, scanDispatch, exclusiveMemory), Right(()))
    assertEquals(
      exclusiveMemory.bytes,
      Vec.from(Vec(0L, 0L, 1L, 3L, 6L, 10L, 15L, 21L).view.flatMap(LittleEndian.toVec(_, 8)))
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
    assertEquals(executeUnchecked(broadcastModule, scanDispatch, broadcastMemory), Right(()))
    assertEquals(broadcastMemory.bytes, Vec.fill(8)(LittleEndian.toVec(6L, 8)).flatten)

    val subgroupBroadcastModule = broadcastModule.copy(instructions =
      broadcastInstructions.updated(
        8,
        Instruction.Constant(u32, subgroupScope, operand("LiteralContextDependentNumber", spv.Scope.Subgroup))
      )
    )
    val subgroupBroadcastDispatch = KernelTask
      .validated(
        "collective",
        Dimensions(16, 1, 1),
        Dimensions.One,
        Vec(KernelTask.Argument.Memory(allocation, 0L))
      )
      .toOption
      .get
    val subgroupBroadcastMemory = ArrayMemory(allocation, 128)
    assertEquals(
      executeUnchecked(subgroupBroadcastModule, subgroupBroadcastDispatch, subgroupBroadcastMemory),
      Right(())
    )
    assertEquals(
      subgroupBroadcastMemory.bytes,
      Vec.fill(16)(LittleEndian.toVec(6L, 8)).flatten
    )

    val invalidScope = module.copy(instructions =
      instructions
        .updated(
          8,
          Instruction.Constant(u32, subgroupScope, operand("LiteralContextDependentNumber", spv.Scope.Device))
        )
        .updated(21, Instruction.GroupAny(bool, any, subgroupScope, pred))
    )
    executeUnchecked(invalidScope, dispatch, memory) match {
      case Left(Fault.Unsupported(message)) =>
        assertEquals(message, "OpGroupAny execution scope 1 is not Subgroup or Workgroup")
      case result => fail(s"expected typed unsupported collective scope failure, found $result")
    }

    val malformedLogical = module.copy(instructions =
      instructions.updated(22, Instruction.LogicalNotEqual(u64, notAll, any, trueValue))
    )
    executeUnchecked(malformedLogical, dispatch, memory) match {
      case Left(Fault.InvalidTask(message)) =>
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
        Instruction.TypePointer(builtinType, operand("StorageClass", spv.StorageClass.Input), vector3),
        Instruction.TypePointer(outputType, operand("StorageClass", spv.StorageClass.CrossWorkgroup), u32),
        Instruction.TypeFunction(functionType, voidType, Vec(outputType)),
        Instruction.Constant(u32, subgroupScope, operand("LiteralContextDependentNumber", spv.Scope.Subgroup)),
        Instruction.Constant(u64, three, operand("LiteralContextDependentNumber", 3)),
        Instruction.Variable(builtinType, localBuiltin, operand("StorageClass", spv.StorageClass.Input), None),
        Instruction.Decorate(
          localBuiltin,
          operand("Decoration", spv.Decoration.BuiltIn, spv.BuiltIn.LocalInvocationId)
        ),
        Instruction.EntryPoint(
          operand("ExecutionModel", spv.ExecutionModel.Kernel),
          function,
          stringOperand("ballot"),
          Vec(localBuiltin)
        ),
        Instruction.Function(voidType, function, operand("FunctionControl", spv.FunctionControl.None), functionType),
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
    val dispatch   = KernelTask
      .validated(
        "ballot",
        Dimensions(8, 1, 1),
        Dimensions.One,
        Vec(KernelTask.Argument.Memory(allocation, 0L))
      )
      .toOption
      .get

    assertEquals(executeUnchecked(module, dispatch, memory), Right(()))
    assertEquals(memory.bytes, LittleEndian.toVec(7, 4))
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
        Instruction.Constant(u32, workgroupScope, operand("LiteralContextDependentNumber", spv.Scope.Workgroup)),
        Instruction.EntryPoint(
          operand("ExecutionModel", spv.ExecutionModel.Kernel),
          plainFunction,
          stringOperand("plain"),
          Vec.empty
        ),
        Instruction.EntryPoint(
          operand("ExecutionModel", spv.ExecutionModel.Kernel),
          collectiveFunction,
          stringOperand("collective"),
          Vec.empty
        ),
        Instruction.Function(
          voidType,
          plainFunction,
          operand("FunctionControl", spv.FunctionControl.None),
          functionType
        ),
        Instruction.Label(plainLabel),
        Instruction.Return(),
        Instruction.FunctionEnd(),
        Instruction.Function(
          voidType,
          collectiveFunction,
          operand("FunctionControl", spv.FunctionControl.None),
          functionType
        ),
        Instruction.Label(collectiveLabel),
        Instruction.FunctionCall(voidType, callResult, collectiveHelper, Vec.empty),
        Instruction.Return(),
        Instruction.FunctionEnd(),
        Instruction.Function(
          voidType,
          collectiveHelper,
          operand("FunctionControl", spv.FunctionControl.None),
          functionType
        ),
        Instruction.Label(helperLabel),
        Instruction.ControlBarrier(workgroupScope, workgroupScope, workgroupScope),
        Instruction.Return(),
        Instruction.FunctionEnd()
      ),
      Endianness.Little
    )
    val allocation = AllocationId(240L)
    val geometry   = Dimensions(8, 1, 1)
    val prepared   = loadUnchecked(module).toOption.get

    def dispatch(name: NonEmpty[String]): KernelTask =
      KernelTask.validated(
        name,
        geometry,
        Dimensions(2, 1, 1),
        Vec.empty
      ).toOption.get

    def observed(name: NonEmpty[String]): ExecutionMetrics = {
      val observation = AtomicReference(Option.empty[ExecutionMetrics])
      assertEquals(
        VMInterpreter.execute(
          prepared,
          dispatch(name),
          ArrayMemory(allocation, 0),
          TaskControl(),
          Workgroups.Sequential,
          _ => Right(()),
          snapshot => observation.set(Some(snapshot))
        ),
        Right(())
      )
      observation.get().getOrElse(fail("missing SPIR-V performance observation"))
    }

    val plain = observed("plain")
    assertEquals(plain.invocations.raw, 16L)
    assertEquals(plain.invocationValueInstances.raw, 1L)
    assertEquals(plain.collectiveDispatches.raw, 0L)

    val collective = observed("collective")
    assertEquals(collective.invocations.raw, 16L)
    assertEquals(collective.invocationValueInstances.raw, 8L)
    assertEquals(collective.collectiveDispatches.raw, 1L)
    assertEquals(collective.estimatedDenseValueBytes.raw, 8L * 12L * 16L)
  }

}
