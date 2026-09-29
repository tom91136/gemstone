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
class ExecutionAtomicSuite extends munit.FunSuite {
  test("atomic flag test-and-set returns the prior state and clear resets the flag") {
    val voidType     = operand("IdResult", 1)
    val u32          = operand("IdResult", 2)
    val boolType     = operand("IdResult", 3)
    val pointerType  = operand("IdResult", 4)
    val functionType = operand("IdResult", 5)
    val scope        = operand("IdResult", 6)
    val semantics    = operand("IdResult", 7)
    val zero         = operand("IdResult", 8)
    val one          = operand("IdResult", 9)
    val function     = operand("IdResult", 10)
    val target       = operand("IdResult", 11)
    val first        = operand("IdResult", 13)
    val second       = operand("IdResult", 14)
    val third        = operand("IdResult", 15)
    val firstValue   = operand("IdResult", 16)
    val secondValue  = operand("IdResult", 17)
    val thirdValue   = operand("IdResult", 18)
    val firstOutput  = operand("IdResult", 19)
    val secondOutput = operand("IdResult", 20)
    val thirdOutput  = operand("IdResult", 21)
    val two          = operand("IdResult", 22)
    val three        = operand("IdResult", 23)
    val module       = Module(
      Header(ISA.version, Word(0), Word(24), Word(0)),
      Vec(
        Instruction.TypeVoid(voidType),
        Instruction.TypeInt(u32, operand("LiteralInteger", 32), operand("LiteralInteger", 0)),
        Instruction.TypeBool(boolType),
        Instruction.TypePointer(pointerType, operand("StorageClass", spv.StorageClass.CrossWorkgroup), u32),
        Instruction.TypeFunction(functionType, voidType, Vec(pointerType)),
        Instruction.Constant(u32, scope, operand("LiteralContextDependentNumber", spv.Scope.Workgroup)),
        Instruction.Constant(
          u32,
          semantics,
          operand(
            "LiteralContextDependentNumber",
            spv.MemorySemantics.SubgroupMemory | spv.MemorySemantics.WorkgroupMemory |
              spv.MemorySemantics.CrossWorkgroupMemory
          )
        ),
        Instruction.Constant(u32, zero, operand("LiteralContextDependentNumber", 0)),
        Instruction.Constant(u32, one, operand("LiteralContextDependentNumber", 1)),
        Instruction.Constant(u32, two, operand("LiteralContextDependentNumber", 2)),
        Instruction.Constant(u32, three, operand("LiteralContextDependentNumber", 3)),
        Instruction.EntryPoint(
          operand("ExecutionModel", spv.ExecutionModel.Kernel),
          function,
          stringOperand("atomic_flag"),
          Vec.empty
        ),
        Instruction.Function(voidType, function, operand("FunctionControl", spv.FunctionControl.None), functionType),
        Instruction.FunctionParameter(pointerType, target),
        Instruction.Label(operand("IdResult", 12)),
        Instruction.AtomicFlagTestAndSet(boolType, first, target, scope, semantics),
        Instruction.AtomicFlagTestAndSet(boolType, second, target, scope, semantics),
        Instruction.AtomicFlagClear(target, scope, semantics),
        Instruction.AtomicFlagTestAndSet(boolType, third, target, scope, semantics),
        Instruction.Select(u32, firstValue, first, one, zero),
        Instruction.Select(u32, secondValue, second, one, zero),
        Instruction.Select(u32, thirdValue, third, one, zero),
        Instruction.PtrAccessChain(pointerType, firstOutput, target, one, Vec.empty),
        Instruction.Store(firstOutput, firstValue, None),
        Instruction.PtrAccessChain(pointerType, secondOutput, target, two, Vec.empty),
        Instruction.Store(secondOutput, secondValue, None),
        Instruction.PtrAccessChain(pointerType, thirdOutput, target, three, Vec.empty),
        Instruction.Store(thirdOutput, thirdValue, None),
        Instruction.Return(),
        Instruction.FunctionEnd()
      ),
      Endianness.Little
    )
    val allocation = AllocationId(122L)
    val memory     = ArrayMemory(allocation, 16)
    val dispatch   = KernelTask
      .validated(
        "atomic_flag",
        Dimensions.One,
        Dimensions.One,
        Vec(KernelTask.Argument.Memory(allocation, 0L))
      )
      .toOption
      .get

    assertEquals(executeUnchecked(module, dispatch, memory), Right(()))
    assertEquals(
      memory.bytes,
      LittleEndian.toVec(1L, 4) ++ LittleEndian.toVec(0L, 4) ++
        LittleEndian.toVec(1L, 4) ++ LittleEndian.toVec(0L, 4)
    )
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
      Vec(
        Instruction.TypeVoid(voidType),
        Instruction.TypeInt(u32, operand("LiteralInteger", 32), operand("LiteralInteger", 0)),
        Instruction.TypeInt(u64, operand("LiteralInteger", 64), operand("LiteralInteger", 0)),
        Instruction.TypePointer(pointerType, operand("StorageClass", spv.StorageClass.CrossWorkgroup), u64),
        Instruction.TypeFunction(functionType, voidType, Vec(pointerType)),
        Instruction.Constant(u32, scope, operand("LiteralContextDependentNumber", spv.Scope.Workgroup)),
        Instruction.Constant(
          u32,
          semantics,
          operand(
            "LiteralContextDependentNumber",
            spv.MemorySemantics.SubgroupMemory | spv.MemorySemantics.WorkgroupMemory |
              spv.MemorySemantics.CrossWorkgroupMemory
          )
        ),
        Instruction.Constant(u64, addend, operand("LiteralContextDependentNumber", 3)),
        Instruction.Constant(u64, minimum, operand("LiteralContextDependentNumber", 7)),
        Instruction.Constant(u64, one, operand("LiteralContextDependentNumber", 1)),
        Instruction.Constant(u64, two, operand("LiteralContextDependentNumber", 2)),
        Instruction.EntryPoint(
          operand("ExecutionModel", spv.ExecutionModel.Kernel),
          function,
          stringOperand("atomics"),
          Vec.empty
        ),
        Instruction.Function(voidType, function, operand("FunctionControl", spv.FunctionControl.None), functionType),
        Instruction.FunctionParameter(pointerType, target),
        Instruction.Label(operand("IdResult", 14)),
        Instruction.AtomicStore(target, scope, semantics, minimum),
        Instruction.AtomicIAdd(u64, addResult, target, scope, semantics, addend),
        Instruction.AtomicUMin(u64, minimumResult, target, scope, semantics, minimum),
        Instruction.PtrAccessChain(pointerType, addOutput, target, one, Vec.empty),
        Instruction.Store(addOutput, addResult, None),
        Instruction.PtrAccessChain(pointerType, minimumOutput, target, two, Vec.empty),
        Instruction.Store(minimumOutput, minimumResult, None),
        Instruction.Return(),
        Instruction.FunctionEnd()
      ),
      Endianness.Little
    )
    val allocation = AllocationId(12L)
    val memory     = ArrayMemory(allocation, 24)
    assertEquals(
      memory.write(allocation, 0L, LittleEndian.toVec(10L, 8)),
      Right(())
    )
    val dispatch = KernelTask
      .validated(
        "atomics",
        Dimensions.One,
        Dimensions.One,
        Vec(KernelTask.Argument.Memory(allocation, 0L))
      )
      .toOption
      .get

    assertEquals(executeUnchecked(module, dispatch, memory), Right(()))
    assertEquals(
      memory.bytes,
      LittleEndian.toVec(7L, 8) ++ LittleEndian.toVec(7L, 8) ++
        LittleEndian.toVec(10L, 8)
    )
  }

  test("dispatch executes the integer atomic operation family") {
    val voidType     = operand("IdResult", 1)
    val u32          = operand("IdResult", 2)
    val pointerType  = operand("IdResult", 3)
    val functionType = operand("IdResult", 4)
    val scope        = operand("IdResult", 5)
    val semantics    = operand("IdResult", 6)
    val source       = operand("IdResult", 7)
    val one          = operand("IdResult", 8)
    val function     = operand("IdResult", 9)
    val target       = operand("IdResult", 10)
    val result       = operand("IdResult", 12)
    val output       = operand("IdResult", 13)

    def execute(operation: Instruction, initial: Long, sourceValue: Long, updated: Long): Unit = {
      val module = Module(
        Header(ISA.version, Word(0), Word(14), Word(0)),
        Vec(
          Instruction.TypeVoid(voidType),
          Instruction.TypeInt(u32, operand("LiteralInteger", 32), operand("LiteralInteger", 0)),
          Instruction.TypePointer(pointerType, operand("StorageClass", spv.StorageClass.CrossWorkgroup), u32),
          Instruction.TypeFunction(functionType, voidType, Vec(pointerType)),
          Instruction.Constant(u32, scope, operand("LiteralContextDependentNumber", spv.Scope.Workgroup)),
          Instruction.Constant(
            u32,
            semantics,
            operand(
              "LiteralContextDependentNumber",
              spv.MemorySemantics.SubgroupMemory | spv.MemorySemantics.WorkgroupMemory |
                spv.MemorySemantics.CrossWorkgroupMemory
            )
          ),
          Instruction.Constant(u32, source, operand("LiteralContextDependentNumber", sourceValue.toInt)),
          Instruction.Constant(u32, one, operand("LiteralContextDependentNumber", 1)),
          Instruction.EntryPoint(
            operand("ExecutionModel", spv.ExecutionModel.Kernel),
            function,
            stringOperand("atomic"),
            Vec.empty
          ),
          Instruction.Function(voidType, function, operand("FunctionControl", spv.FunctionControl.None), functionType),
          Instruction.FunctionParameter(pointerType, target),
          Instruction.Label(operand("IdResult", 11)),
          operation,
          Instruction.PtrAccessChain(pointerType, output, target, one, Vec.empty),
          Instruction.Store(output, result, None),
          Instruction.Return(),
          Instruction.FunctionEnd()
        ),
        Endianness.Little
      )
      val allocation = AllocationId(120L)
      val memory     = ArrayMemory(allocation, 8)
      assertEquals(
        memory.write(allocation, 0L, LittleEndian.toVec(initial, 4)),
        Right(())
      )
      val dispatch = KernelTask
        .validated(
          "atomic",
          Dimensions.One,
          Dimensions.One,
          Vec(KernelTask.Argument.Memory(allocation, 0L))
        )
        .toOption
        .get
      assertEquals(executeUnchecked(module, dispatch, memory), Right(()))
      assertEquals(
        memory.bytes,
        LittleEndian.toVec(updated, 4) ++
          LittleEndian.toVec(initial, 4)
      )
    }

    execute(
      operation = Instruction.AtomicIIncrement(u32, result, target, scope, semantics),
      initial = 10,
      sourceValue = 0,
      updated = 11
    )
    execute(
      operation = Instruction.AtomicIDecrement(u32, result, target, scope, semantics),
      initial = 10,
      sourceValue = 0,
      updated = 9
    )
    execute(
      operation = Instruction.AtomicISub(u32, result, target, scope, semantics, source),
      initial = 10,
      sourceValue = 3,
      updated = 7
    )
    execute(
      operation = Instruction.AtomicSMin(u32, result, target, scope, semantics, source),
      initial = 1,
      sourceValue = 0xffffffffL,
      updated = 0xffffffffL
    )
    execute(
      operation = Instruction.AtomicSMax(u32, result, target, scope, semantics, source),
      initial = 0xffffffffL,
      sourceValue = 1,
      updated = 1
    )
    execute(
      operation = Instruction.AtomicUMax(u32, result, target, scope, semantics, source),
      initial = 10,
      sourceValue = 3,
      updated = 10
    )
    execute(
      operation = Instruction.AtomicAnd(u32, result, target, scope, semantics, source),
      initial = 10,
      sourceValue = 3,
      updated = 2
    )
    execute(
      operation = Instruction.AtomicOr(u32, result, target, scope, semantics, source),
      initial = 10,
      sourceValue = 3,
      updated = 11
    )
    execute(
      operation = Instruction.AtomicXor(u32, result, target, scope, semantics, source),
      initial = 10,
      sourceValue = 3,
      updated = 9
    )
  }

  test("dispatch treats floating atomic load, store, and exchange as typed raw bits") {
    val voidType     = operand("IdResult", 1)
    val u32          = operand("IdResult", 2)
    val f32          = operand("IdResult", 3)
    val pointerType  = operand("IdResult", 4)
    val functionType = operand("IdResult", 5)
    val scope        = operand("IdResult", 6)
    val semantics    = operand("IdResult", 7)
    val first        = operand("IdResult", 8)
    val second       = operand("IdResult", 9)
    val one          = operand("IdResult", 10)
    val two          = operand("IdResult", 11)
    val function     = operand("IdResult", 12)
    val target       = operand("IdResult", 13)
    val loaded       = operand("IdResult", 15)
    val exchanged    = operand("IdResult", 16)
    val firstOutput  = operand("IdResult", 17)
    val secondOutput = operand("IdResult", 18)
    val firstBits    = java.lang.Float.floatToRawIntBits(1.5f)
    val secondBits   = java.lang.Float.floatToRawIntBits(2.5f)
    val module       = Module(
      Header(ISA.version, Word(0), Word(19), Word(0)),
      Vec(
        Instruction.TypeVoid(voidType),
        Instruction.TypeInt(u32, operand("LiteralInteger", 32), operand("LiteralInteger", 0)),
        Instruction.TypeFloat(f32, operand("LiteralInteger", 32), None),
        Instruction.TypePointer(pointerType, operand("StorageClass", spv.StorageClass.CrossWorkgroup), f32),
        Instruction.TypeFunction(functionType, voidType, Vec(pointerType)),
        Instruction.Constant(u32, scope, operand("LiteralContextDependentNumber", spv.Scope.Workgroup)),
        Instruction.Constant(
          u32,
          semantics,
          operand(
            "LiteralContextDependentNumber",
            spv.MemorySemantics.SubgroupMemory | spv.MemorySemantics.WorkgroupMemory |
              spv.MemorySemantics.CrossWorkgroupMemory
          )
        ),
        Instruction.Constant(f32, first, operand("LiteralContextDependentNumber", firstBits)),
        Instruction.Constant(f32, second, operand("LiteralContextDependentNumber", secondBits)),
        Instruction.Constant(u32, one, operand("LiteralContextDependentNumber", 1)),
        Instruction.Constant(u32, two, operand("LiteralContextDependentNumber", 2)),
        Instruction.EntryPoint(
          operand("ExecutionModel", spv.ExecutionModel.Kernel),
          function,
          stringOperand("atomic_float"),
          Vec.empty
        ),
        Instruction.Function(voidType, function, operand("FunctionControl", spv.FunctionControl.None), functionType),
        Instruction.FunctionParameter(pointerType, target),
        Instruction.Label(operand("IdResult", 14)),
        Instruction.AtomicStore(target, scope, semantics, first),
        Instruction.AtomicLoad(f32, loaded, target, scope, semantics),
        Instruction.AtomicExchange(f32, exchanged, target, scope, semantics, second),
        Instruction.PtrAccessChain(pointerType, firstOutput, target, one, Vec.empty),
        Instruction.Store(firstOutput, loaded, None),
        Instruction.PtrAccessChain(pointerType, secondOutput, target, two, Vec.empty),
        Instruction.Store(secondOutput, exchanged, None),
        Instruction.Return(),
        Instruction.FunctionEnd()
      ),
      Endianness.Little
    )
    val allocation = AllocationId(121L)
    val memory     = ArrayMemory(allocation, 12)
    val dispatch   = KernelTask
      .validated(
        "atomic_float",
        Dimensions.One,
        Dimensions.One,
        Vec(KernelTask.Argument.Memory(allocation, 0L))
      )
      .toOption
      .get
    assertEquals(executeUnchecked(module, dispatch, memory), Right(()))
    assertEquals(
      memory.bytes,
      LittleEndian.toVec(secondBits.toLong, 4) ++
        LittleEndian.toVec(firstBits.toLong, 4) ++
        LittleEndian.toVec(firstBits.toLong, 4)
    )
  }

  test("device-scope atomic election publishes a payload across workgroups without waiting for all groups") {
    val prepared = loadUnchecked(interleaving.publication).toOption.get
    for {
      groups   <- interleaving.groups
      parallel <- Vec(false, true)
    } {
      val allocation = AllocationId(301L)
      val memory     = ArrayMemory(allocation, 12 + groups.raw * 8)
      for (seed <- Vec(17L, 91L)) {
        assertEquals(
          memory.write(allocation, 0L, Vec.fill(12 + groups.raw * 8)(0.toByte)),
          Right(())
        )
        val schedule   = interleaving.Interleaving()
        val workgroups = if (parallel) schedule else Workgroups.Sequential
        val control    = TaskControl()
        val dispatch   = KernelTask.validated(
          "publish",
          Dimensions.One,
          testDimensions(groups),
          Vec(
            KernelTask.Argument.Memory(allocation, 12L),
            KernelTask.Argument.Memory(allocation, 0L),
            KernelTask.Argument.Memory(allocation, 4L),
            KernelTask.Argument.Memory(allocation, 8L),
            KernelTask.Argument.Scalar(LittleEndian.toVec(seed, 4))
          )
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
          Right(())
        )
        val words =
          memory.bytes.grouped(4).map(x => LittleEndian.decode(x, 0, 4)).toVector
        assertEquals(words.take(3), Vector(groups.raw.toLong, seed, 1L))
        assertEquals(words.drop(3).grouped(2).map(_.head).toVector.sorted, (0L until groups.raw.toLong).toVector)
        val observations = words.drop(3).grouped(2).map(_(1)).toVector
        assert(observations.forall(x => x == 0L || x == seed))
        assert(observations.contains(seed))
        if (parallel) assertEquals(schedule.calls.get(), if (groups > 1) 1 else 0)
        if (groups > 1 && !parallel) {
          val consumers = words.drop(3).grouped(2).filter(_.head != 0L).toVector
          assert(consumers.nonEmpty)
          assert(consumers.forall(_(1) == seed))
        }
        // A completed producer phase guarantees readiness without requiring
        // simultaneously resident workgroups or a spinning in-kernel observer.
        val observeControl = if (parallel)
          TaskControl()
        else TaskControl()
        assertEquals(
          VMInterpreter.execute(
            prepared,
            dispatch,
            schedule.InterleavedMemory(memory),
            observeControl,
            workgroups,
            _ => Right(())
          ),
          Right(())
        )
        val consumed =
          memory.bytes.grouped(4).map(x => LittleEndian.decode(x, 0, 4)).toVector
        assertEquals(consumed.take(3), Vector(groups.raw.toLong * 2, seed, 1L))
        assertEquals(
          consumed.drop(3).grouped(2).map(_.head).toVector.sorted,
          (groups.raw.toLong until groups.raw.toLong * 2).toVector
        )
        assert(consumed.drop(3).grouped(2).forall(_(1) == seed))
      }
    }
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
        Instruction.TypePointer(pointer, operand("StorageClass", spv.StorageClass.CrossWorkgroup), f32),
        Instruction.TypeFunction(functionTy, voidType, Vec(pointer)),
        Instruction.Constant(u32, scope, operand("LiteralContextDependentNumber", spv.Scope.Workgroup)),
        Instruction.Constant(
          u32,
          semantics,
          operand(
            "LiteralContextDependentNumber",
            spv.MemorySemantics.SubgroupMemory | spv.MemorySemantics.WorkgroupMemory |
              spv.MemorySemantics.CrossWorkgroupMemory
          )
        ),
        Instruction.Constant(f32, initial, operand("LiteralContextDependentNumber", initialBits)),
        Instruction.Constant(f32, minimum, operand("LiteralContextDependentNumber", minimumBits)),
        Instruction.Constant(f32, maximum, operand("LiteralContextDependentNumber", maximumBits)),
        Instruction.Constant(u32, one, operand("LiteralContextDependentNumber", 1)),
        Instruction.Constant(u32, two, operand("LiteralContextDependentNumber", 2)),
        Instruction.EntryPoint(
          operand("ExecutionModel", spv.ExecutionModel.Kernel),
          function,
          stringOperand("atomic_float_min_max"),
          Vec.empty
        ),
        Instruction.Function(voidType, function, operand("FunctionControl", spv.FunctionControl.None), functionTy),
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
    val dispatch   = KernelTask
      .validated(
        "atomic_float_min_max",
        Dimensions.One,
        Dimensions.One,
        Vec(KernelTask.Argument.Memory(allocation, 0L))
      )
      .toOption
      .get

    assertEquals(executeUnchecked(module, dispatch, memory), Right(()), "floating atomic min/max execution")
    assertEquals(
      memory.bytes,
      LittleEndian.toVec(maximumBits, 4) ++
        LittleEndian.toVec(initialBits, 4) ++
        LittleEndian.toVec(minimumBits, 4)
    )
  }
}
