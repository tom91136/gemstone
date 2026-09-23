package net.kurobako.gemstone.isa.spirv.v1_6
import net.kurobako.gemstone.isa.*
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchGeometry.{Coordinates, Dimensions, GlobalDimensions}

import net.kurobako.gemstone.Vec

import net.kurobako.gemstone.binary.ByteRange
import net.kurobako.gemstone.binary.LittleEndian
import net.kurobako.gemstone.binary.ScalarByteWidth
import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

import java.util.concurrent.atomic.AtomicReference
final class ExecutionAtomicSuite extends munit.FunSuite with ExecutionTestSupport {
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
        Instruction.TypePointer(pointerType, operand("StorageClass", 5), u32),
        Instruction.TypeFunction(functionType, voidType, Vec(pointerType)),
        Instruction.Constant(u32, scope, operand("LiteralContextDependentNumber", 2)),
        Instruction.Constant(u32, semantics, operand("LiteralContextDependentNumber", 896)),
        Instruction.Constant(u32, zero, operand("LiteralContextDependentNumber", 0)),
        Instruction.Constant(u32, one, operand("LiteralContextDependentNumber", 1)),
        Instruction.Constant(u32, two, operand("LiteralContextDependentNumber", 2)),
        Instruction.Constant(u32, three, operand("LiteralContextDependentNumber", 3)),
        Instruction.EntryPoint(operand("ExecutionModel", 6), function, stringOperand("atomic_flag"), Vec.empty),
        Instruction.Function(voidType, function, operand("FunctionControl", 0), functionType),
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
    val allocation = MemoryRegion(122L)
    val memory     = ArrayMemory(allocation, 16)
    val dispatch   = KernelTask
      .validated(
        KernelName("atomic_flag"),
        Dimensions.One,
        Dimensions.One,
        Vec(KernelTask.Argument.Memory(allocation, 0L))
      )
      .toOption
      .get

    assertEquals(executeUnchecked(module, dispatch, memory), Right(()))
    assertEquals(
      memory.bytes,
      LittleEndian.toVec(1L, ScalarByteWidth(4)) ++ LittleEndian.toVec(0L, ScalarByteWidth(4)) ++
        LittleEndian.toVec(1L, ScalarByteWidth(4)) ++ LittleEndian.toVec(0L, ScalarByteWidth(4))
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
        Instruction.TypePointer(pointerType, operand("StorageClass", 5), u64),
        Instruction.TypeFunction(functionType, voidType, Vec(pointerType)),
        Instruction.Constant(u32, scope, operand("LiteralContextDependentNumber", 2)),
        Instruction.Constant(u32, semantics, operand("LiteralContextDependentNumber", 896)),
        Instruction.Constant(u64, addend, operand("LiteralContextDependentNumber", 3)),
        Instruction.Constant(u64, minimum, operand("LiteralContextDependentNumber", 7)),
        Instruction.Constant(u64, one, operand("LiteralContextDependentNumber", 1)),
        Instruction.Constant(u64, two, operand("LiteralContextDependentNumber", 2)),
        Instruction.EntryPoint(operand("ExecutionModel", 6), function, stringOperand("atomics"), Vec.empty),
        Instruction.Function(voidType, function, operand("FunctionControl", 0), functionType),
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
    val allocation = MemoryRegion(12L)
    val memory     = ArrayMemory(allocation, 24)
    assertEquals(memory.write(allocation, 0L, LittleEndian.toVec(10L, ScalarByteWidth(8))), Right(()))
    val dispatch = KernelTask
      .validated(
        KernelName("atomics"),
        Dimensions.One,
        Dimensions.One,
        Vec(KernelTask.Argument.Memory(allocation, 0L))
      )
      .toOption
      .get

    assertEquals(executeUnchecked(module, dispatch, memory), Right(()))
    assertEquals(
      memory.bytes,
      LittleEndian.toVec(7L, ScalarByteWidth(8)) ++ LittleEndian.toVec(7L, ScalarByteWidth(8)) ++
        LittleEndian.toVec(10L, ScalarByteWidth(8))
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
          Instruction.TypePointer(pointerType, operand("StorageClass", 5), u32),
          Instruction.TypeFunction(functionType, voidType, Vec(pointerType)),
          Instruction.Constant(u32, scope, operand("LiteralContextDependentNumber", 2)),
          Instruction.Constant(u32, semantics, operand("LiteralContextDependentNumber", 896)),
          Instruction.Constant(u32, source, operand("LiteralContextDependentNumber", sourceValue.toInt)),
          Instruction.Constant(u32, one, operand("LiteralContextDependentNumber", 1)),
          Instruction.EntryPoint(operand("ExecutionModel", 6), function, stringOperand("atomic"), Vec.empty),
          Instruction.Function(voidType, function, operand("FunctionControl", 0), functionType),
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
      val allocation = MemoryRegion(120L)
      val memory     = ArrayMemory(allocation, 8)
      assertEquals(memory.write(allocation, 0L, LittleEndian.toVec(initial, ScalarByteWidth(4))), Right(()))
      val dispatch = KernelTask
        .validated(
          KernelName("atomic"),
          Dimensions.One,
          Dimensions.One,
          Vec(KernelTask.Argument.Memory(allocation, 0L))
        )
        .toOption
        .get
      assertEquals(executeUnchecked(module, dispatch, memory), Right(()))
      assertEquals(
        memory.bytes,
        LittleEndian.toVec(updated, ScalarByteWidth(4)) ++ LittleEndian.toVec(initial, ScalarByteWidth(4))
      )
    }

    execute(Instruction.AtomicIIncrement(u32, result, target, scope, semantics), 10, 0, 11)
    execute(Instruction.AtomicIDecrement(u32, result, target, scope, semantics), 10, 0, 9)
    execute(Instruction.AtomicISub(u32, result, target, scope, semantics, source), 10, 3, 7)
    execute(Instruction.AtomicSMin(u32, result, target, scope, semantics, source), 1, 0xffffffffL, 0xffffffffL)
    execute(Instruction.AtomicSMax(u32, result, target, scope, semantics, source), 0xffffffffL, 1, 1)
    execute(Instruction.AtomicUMax(u32, result, target, scope, semantics, source), 10, 3, 10)
    execute(Instruction.AtomicAnd(u32, result, target, scope, semantics, source), 10, 3, 2)
    execute(Instruction.AtomicOr(u32, result, target, scope, semantics, source), 10, 3, 11)
    execute(Instruction.AtomicXor(u32, result, target, scope, semantics, source), 10, 3, 9)
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
        Instruction.TypePointer(pointerType, operand("StorageClass", 5), f32),
        Instruction.TypeFunction(functionType, voidType, Vec(pointerType)),
        Instruction.Constant(u32, scope, operand("LiteralContextDependentNumber", 2)),
        Instruction.Constant(u32, semantics, operand("LiteralContextDependentNumber", 896)),
        Instruction.Constant(f32, first, operand("LiteralContextDependentNumber", firstBits)),
        Instruction.Constant(f32, second, operand("LiteralContextDependentNumber", secondBits)),
        Instruction.Constant(u32, one, operand("LiteralContextDependentNumber", 1)),
        Instruction.Constant(u32, two, operand("LiteralContextDependentNumber", 2)),
        Instruction.EntryPoint(operand("ExecutionModel", 6), function, stringOperand("atomic_float"), Vec.empty),
        Instruction.Function(voidType, function, operand("FunctionControl", 0), functionType),
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
    val allocation = MemoryRegion(121L)
    val memory     = ArrayMemory(allocation, 12)
    val dispatch   = KernelTask
      .validated(
        KernelName("atomic_float"),
        Dimensions.One,
        Dimensions.One,
        Vec(KernelTask.Argument.Memory(allocation, 0L))
      )
      .toOption
      .get
    assertEquals(executeUnchecked(module, dispatch, memory), Right(()))
    assertEquals(
      memory.bytes,
      LittleEndian.toVec(secondBits.toLong, ScalarByteWidth(4)) ++
        LittleEndian.toVec(firstBits.toLong, ScalarByteWidth(4)) ++
        LittleEndian.toVec(firstBits.toLong, ScalarByteWidth(4))
    )
  }

  test("dirty-page flush preserves sparse runs and writes crossing bitmap and page boundaries") {
    val offsets = Vec[Long :| Positive0](
      0L.refineUnsafe[Positive0],
      8L.refineUnsafe[Positive0],
      62L.refineUnsafe[Positive0],
      66L.refineUnsafe[Positive0],
      4094L.refineUnsafe[Positive0],
      8191L.refineUnsafe[Positive0]
    )
    val parameters   = offsets.indices.map(index => operand("IdResult", 10 + index))
    val voidType     = operand("IdResult", 1)
    val u32          = operand("IdResult", 2)
    val pointerType  = operand("IdResult", 3)
    val functionType = operand("IdResult", 4)
    val function     = operand("IdResult", 5)
    val value        = operand("IdResult", 6)
    val module       = Module(
      Header(ISA.version, Word(0), Word(32), Word(0)),
      Vec[Instruction](
        Instruction.TypeVoid(voidType),
        Instruction.TypeInt(u32, operand("LiteralInteger", 32), operand("LiteralInteger", 0)),
        Instruction.TypePointer(pointerType, operand("StorageClass", 5), u32),
        Instruction.TypeFunction(functionType, voidType, Vec.fill(offsets.size)(pointerType)),
        Instruction.Constant(u32, value, operand("LiteralContextDependentNumber", 0x12345678)),
        Instruction.EntryPoint(operand("ExecutionModel", 6), function, stringOperand("sparse"), Vec.empty),
        Instruction.Function(voidType, function, operand("FunctionControl", 0), functionType)
      ) ++ Vec.from(parameters.map(Instruction.FunctionParameter(pointerType, _))) ++
        Vec[Instruction](Instruction.Label(operand("IdResult", 20))) ++
        Vec.from(parameters.map(Instruction.Store(_, value, None))) ++
        Vec[Instruction](
          Instruction.Load(u32, operand("IdResult", 21), parameters(2), None),
          Instruction.Store(parameters(0), operand("IdRef", 21), None),
          Instruction.Load(u32, operand("IdResult", 22), parameters(4), None),
          Instruction.Store(parameters(1), operand("IdRef", 22), None),
          Instruction.Return(),
          Instruction.FunctionEnd()
        ),
      Endianness.Little
    )
    val allocation = MemoryRegion(200L)
    val writes     = AtomicReference(Vec.empty[(Long, Int)])
    val memory     = ArrayMemory(
      allocation,
      8200,
      written = (offset, size) => {
        writes.updateAndGet(_ :+ (offset -> size))
        ()
      }
    )
    val dispatch = KernelTask.validated(
      kernelName = KernelName("sparse"),
      groupSize = Dimensions.One,
      groupCount = Dimensions.One,
      arguments = offsets.map(KernelTask.Argument.Memory(allocation, _)),
      globalSize = None
    ).toOption.get
    assertEquals(executeUnchecked(module, dispatch, memory), Right(()))
    assertEquals(writes.get(), Vec(0L -> 4, 8L -> 4, 62L -> 8, 4094L -> 2, 4096L -> 2, 8191L -> 1, 8192L -> 3))
    val expected = Array.ofDim[Byte](8200)
    offsets.foreach { offset =>
      val arrayOffset = offset.toInt.refineEither[Positive0].fold(message => fail(message), value => value)
      LittleEndian.write(0x12345678L, expected, arrayOffset, ScalarByteWidth(4))
    }
    assertEquals(memory.bytes, Vec.from(expected))
  }

  test("cross-workgroup IDs, partial extents and workgroup barriers stay isolated across launches") {
    for {
      groups   <- CrossWorkgroupFixture.groups
      local    <- Vec[Int :| Positive](31, 32, 33)
      parallel <- Vec(false, true)
      barrier  <- Vec(false, true)
      producer <- if (barrier) Vec(2, local - 1) else Vec(2)
    } {
      val count = (local * groups - (if (barrier) 0 else 1)).refineEither[Positive]
        .fold(message => fail(message), value => value)
      val offset: Int :| Positive0 = 7
      val allocation               = MemoryRegion(300L)
      val memory                   = ArrayMemory(allocation, (offset + count + 2) * 4)
      val prepared                 = loadUnchecked(CrossWorkgroupFixture.geometry(barrier, producer)).toOption.get
      for (seed <- Vec(1, 9)) {
        val schedule  = CrossWorkgroupFixture.Interleaving()
        val scheduler = if (parallel) schedule else Scheduler.Sequential
        val control   = if (parallel) TaskControl.forScheduler(cancellation = schedule, scheduler = scheduler)
        else TaskControl.default
        val dispatch = KernelTask.validated(
          KernelName("groups"),
          testDimensions(local),
          testDimensions(groups),
          Vec(
            KernelTask.Argument.Memory(allocation, 4L),
            KernelTask.Argument.Scalar(LittleEndian.toVec(seed.toLong, ScalarByteWidth(4))),
            KernelTask.Argument.Local(4L)
          ),
          globalSize = Some(testDimensions(count).toGlobalDimensions),
          globalOffset = Coordinates(offset, 0, 0)
        ).toOption.get
        assertEquals(
          VMInterpreter.execute(prepared, dispatch, memory, control, scheduler, Observer.discard),
          Right(()),
          s"groups=$groups local=$local parallel=$parallel barrier=$barrier"
        )
        assertEquals(schedule.calls.get(), if (parallel && groups > 1) 1 else 0)
        val expected = Vec.from(0 until count).flatMap { index =>
          LittleEndian.toVec(
            (index / local + seed) * 1000L + (index % local) * 10L + offset + index,
            ScalarByteWidth(4)
          )
        }
        assertEquals(memory.bytes, Vec.fill((offset + 1) * 4)(0.toByte) ++ expected ++ Vec.fill(4)(0.toByte))
      }
    }
  }

  test("device-scope atomic election publishes a payload across workgroups without waiting for all groups") {
    val prepared = loadUnchecked(CrossWorkgroupFixture.publication).toOption.get
    for {
      groups   <- CrossWorkgroupFixture.groups
      parallel <- Vec(false, true)
    } {
      val allocation = MemoryRegion(301L)
      val memory     = ArrayMemory(allocation, 12 + groups * 8)
      for (seed <- Vec(17L, 91L)) {
        assertEquals(memory.write(allocation, 0L, Vec.fill(12 + groups * 8)(0.toByte)), Right(()))
        val schedule  = CrossWorkgroupFixture.Interleaving()
        val scheduler = if (parallel) schedule else Scheduler.Sequential
        val control   = if (parallel) TaskControl.forScheduler(cancellation = schedule, scheduler = scheduler)
        else TaskControl.default
        val dispatch = KernelTask.validated(
          KernelName("publish"),
          Dimensions.One,
          testDimensions(groups),
          Vec(
            KernelTask.Argument.Memory(allocation, 12L),
            KernelTask.Argument.Memory(allocation, 0L),
            KernelTask.Argument.Memory(allocation, 4L),
            KernelTask.Argument.Memory(allocation, 8L),
            KernelTask.Argument.Scalar(LittleEndian.toVec(seed, ScalarByteWidth(4)))
          )
        ).toOption.get
        assertEquals(
          VMInterpreter.execute(prepared, dispatch, memory, control, scheduler, Observer.discard),
          Right(())
        )
        val words = memory.bytes.grouped(4).map(x => LittleEndian.decode(x, 0, ScalarByteWidth(4))).toVector
        assertEquals(words.take(3), Vector(groups.toLong, seed, 1L))
        assertEquals(words.drop(3).grouped(2).map(_.head).toVector.sorted, (0L until groups.toLong).toVector)
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
          TaskControl.forScheduler(cancellation = schedule, scheduler = scheduler)
        else TaskControl.default
        assertEquals(
          VMInterpreter.execute(prepared, dispatch, memory, observeControl, scheduler, Observer.discard),
          Right(())
        )
        val consumed = memory.bytes.grouped(4).map(x => LittleEndian.decode(x, 0, ScalarByteWidth(4))).toVector
        assertEquals(consumed.take(3), Vector(groups.toLong * 2, seed, 1L))
        assertEquals(
          consumed.drop(3).grouped(2).map(_.head).toVector.sorted,
          (groups.toLong until groups.toLong * 2).toVector
        )
        assert(consumed.drop(3).grouped(2).forall(_(1) == seed))
      }
    }
  }

}
