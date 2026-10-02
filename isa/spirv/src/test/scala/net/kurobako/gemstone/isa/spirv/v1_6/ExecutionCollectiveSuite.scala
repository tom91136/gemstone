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
import scala.collection.immutable.ArraySeq
class ExecutionCollectiveSuite extends munit.FunSuite {

  for {
    logical  <- Seq(false, true)
    operator <- Seq("And", "Or", "Xor")
    vector   <- Seq(false, true)
  }
    test(s"KHR group $operator logical=$logical vector=$vector reductions and scans") {
      for {
        width     <- if (logical) Seq(64) else Seq(8, 16, 32, 64)
        scope     <- Seq(2, 3)
        operation <- 0 to 2
      } {
        val module     = khrGroupModule(operator, logical, vector, width, scope, operation)
        val allocation = AllocationId(130L)
        val lanes      = if (vector) 2 else 1
        val memory     = ArrayMemory(allocation, 80 * lanes * 8)
        val task       = KernelTask.validated(
          "shuffle",
          Dimensions(40, 1, 1),
          Dimensions(2, 1, 1),
          Vec(KernelTask.Argument.Memory(allocation, 0L))
        ).toOption.get
        assertEquals(
          executeUnchecked(module, task, memory),
          Right(()),
          s"width=$width scope=$scope operation=$operation"
        )
        val mask     = if (width == 64) -1L else (1L << width) - 1L
        val identity = if (operator == "And") { if (logical) 1L else mask }
        else 0L
        def combine(a: Long, b: Long): Long = operator match {
          case "And" => a & b
          case "Or"  => a | b
          case _     => a ^ b
        }
        val expected = (0 until 80).flatMap { global =>
          val start = global / 40 * 40 + (if (scope == 3) global % 40 / 32 * 32 else 0)
          val end   = math.min(global / 40 * 40 + 40, start + (if (scope == 3) 32 else 40))
          val until = if (operation == 0) end else if (operation == 1) global + 1 else global
          (0 until lanes).map { lane =>
            (start until until).foldLeft(identity) { (acc, index) =>
              val first = if (logical) { if (index % 4 != 3) 1L else 0L }
              else ((1L << (width - 1)) + index) & mask
              val value = if (lane == 0) first else if (logical) first ^ 1L else ~first & mask
              combine(acc, value)
            }
          }
        }
        assertEquals(memory.bytes, Vec.from(expected.flatMap(LittleEndian.toVec(_, 8))))
      }
    }

  test("KHR group operations reject unsupported scope, operation, and wrong operand types") {
    val allocation = AllocationId(131L)
    val task       = KernelTask.validated(
      "shuffle",
      Dimensions(2, 1, 1),
      Dimensions.One,
      Vec(KernelTask.Argument.Memory(allocation, 0L))
    ).toOption.get
    for {
      logical            <- Seq(false, true)
      (scope, operation) <- Seq((1, 0), (2, 3))
    } {
      val module = khrGroupModule("And", logical, false, 32, scope, operation)
      assert(executeUnchecked(
        module,
        task,
        ArrayMemory(allocation, 16)
      ).swap.toOption.exists {
        case Fault.Unsupported(_) => true
        case _                    => false
      })
    }
    for (logical <- Seq(false, true)) {
      val module = khrGroupModule("And", logical, false, 32, 2, 0)
      val wrong  = module.copy(instructions = module.instructions.map {
        case Instruction.GroupLogicalAndKHR(t, d, s, o, _) =>
          Instruction.GroupLogicalAndKHR(t, d, s, o, operand(OperandKind.IdResult, 17))
        case Instruction.GroupBitwiseAndKHR(t, d, s, o, _) =>
          Instruction.GroupBitwiseAndKHR(t, d, s, o, operand(OperandKind.IdResult, 30))
        case x => x
      })
      assert(executeUnchecked(
        wrong,
        task,
        ArrayMemory(allocation, 16)
      ).swap.toOption.exists {
        case Fault.InvalidTask(_) => true
        case _                    => false
      })
    }
  }

  private def khrGroupModule(
      operator: String,
      logical: Boolean,
      vector: Boolean,
      width: Int,
      scope: Int,
      operation: Int
  ): Module = {
    def ref(id: Int)   = operand(OperandKind.IdResult, id)
    val scalarType     = ref(24)
    val vectorType     = ref(25)
    val dataType       = if (vector) vectorType else scalarType
    val payload        = if (vector) ref(30) else ref(28)
    val groupOperation = operand(OperandKind.GroupOperation, operation)
    val collective     = (logical, operator) match {
      case (false, "And") => Instruction.GroupBitwiseAndKHR(dataType, ref(31), ref(18), groupOperation, payload)
      case (false, "Or")  => Instruction.GroupBitwiseOrKHR(dataType, ref(31), ref(18), groupOperation, payload)
      case (false, _)     => Instruction.GroupBitwiseXorKHR(dataType, ref(31), ref(18), groupOperation, payload)
      case (true, "And")  => Instruction.GroupLogicalAndKHR(dataType, ref(31), ref(18), groupOperation, payload)
      case (true, "Or")   => Instruction.GroupLogicalOrKHR(dataType, ref(31), ref(18), groupOperation, payload)
      case (true, _)      => Instruction.GroupLogicalXorKHR(dataType, ref(31), ref(18), groupOperation, payload)
    }
    val original = shuffleModule(false, vector, scope, false)
    original.copy(
      header = original.header.copy(bound = Word(36)),
      instructions = original.instructions.flatMap {
        case Instruction.Constant(_, dest, _) if dest == ref(15) =>
          val high = 1L << (width - 1)
          Vec(Instruction.Constant(
            ref(3),
            dest,
            operand(OperandKind.LiteralContextDependentNumber, high.toInt, (high >>> 32).toInt)
          ))
        case entry: Instruction.EntryPoint => Vec(
            if (logical) Instruction.TypeBool(scalarType)
            else Instruction.TypeInt(
              scalarType,
              operand(OperandKind.LiteralInteger, width),
              operand(OperandKind.LiteralInteger, 0)
            ),
            Instruction.TypeVector(vectorType, scalarType, operand(OperandKind.LiteralInteger, 2)),
            Instruction.Constant(ref(3), ref(26), operand(OperandKind.LiteralContextDependentNumber, 0, 0)),
            Instruction.Constant(ref(3), ref(27), operand(OperandKind.LiteralContextDependentNumber, 1, 0)),
            Instruction.ConstantComposite(ref(5), ref(32), Vec(ref(26), ref(26))),
            Instruction.ConstantComposite(ref(5), ref(33), Vec(ref(27), ref(27))),
            entry
          )
        case _: Instruction.SubgroupShuffleINTEL => Vec(
            if (logical) Instruction.INotEqual(scalarType, ref(28), ref(21), ref(19))
            else if (width == 64) Instruction.CopyObject(scalarType, ref(28), ref(17))
            else Instruction.UConvert(scalarType, ref(28), ref(17)),
            if (logical) Instruction.LogicalNot(scalarType, ref(29), ref(28))
            else Instruction.Not(scalarType, ref(29), ref(28)),
            Instruction.CompositeConstruct(vectorType, ref(30), Vec(ref(28), ref(29))),
            collective,
            if (logical) Instruction.Select(
              if (vector) ref(5) else ref(3),
              ref(22),
              ref(31),
              if (vector) ref(33) else ref(27),
              if (vector) ref(32) else ref(26)
            )
            else if (width == 64) Instruction.CopyObject(if (vector) ref(5) else ref(3), ref(22), ref(31))
            else Instruction.UConvert(if (vector) ref(5) else ref(3), ref(22), ref(31))
          )
        case instruction => Vec(instruction)
      }
    )
  }

  for {
    xor    <- Seq(false, true)
    vector <- Seq(false, true)
  }
    test(s"subgroup shuffle xor=$xor vector=$vector handles lane selectors and partial subgroups") {
      for {
        mask    <- Seq(0, 1, 3, 32, Int.MinValue)
        varying <- Seq(false, true)
      } {
        val module     = shuffleModule(xor, vector, mask, varying)
        val allocation = AllocationId(125L)
        val lanes      = if (vector) 2 else 1
        val memory     = ArrayMemory(allocation, 80 * lanes * 8)
        val task       = KernelTask.validated(
          "shuffle",
          Dimensions(40, 1, 1),
          Dimensions(2, 1, 1),
          Vec(KernelTask.Argument.Memory(allocation, 0L))
        ).toOption.get
        assertEquals(executeUnchecked(module, task, memory), Right(()), s"mask=$mask varying=$varying")
        for (global <- 0 until 80) {
          val local         = global % 40
          val subgroupStart = local / 32 * 32
          val subgroupSize  = math.min(32, 40 - subgroupStart)
          val selector      = if (varying) global & 3 else mask
          val source        = if (xor) (local % 32) ^ selector else selector
          // An out-of-range source has an unspecified value; execution still completes.
          if (source >= 0 && source < subgroupSize) {
            val sourceGlobal = (global / 40 * 40 + subgroupStart + source).toLong
            val expected     = if (vector) Vec(sourceGlobal, 0x1234567800000000L + sourceGlobal)
            else Vec(0x1234567800000000L + sourceGlobal)
            val actual = memory.bytes.slice(global * lanes * 8, (global + 1) * lanes * 8)
            assertEquals(actual, Vec.from(expected.view.flatMap(LittleEndian.toVec(_, 8))))
          }
        }
      }
    }

  test("control barriers accept memory scopes independently and propagate memory fence failures") {
    for {
      scope          <- 0 to 6
      executionScope <- Seq(2, 3)
      rejectFence    <- Seq(false, true)
    } {
      val original  = shuffleModule(false, false, scope, false)
      val u32       = operand(OperandKind.IdResult, 2)
      val execution = operand(OperandKind.IdResult, 24)
      val semantics = operand(OperandKind.IdResult, 25)
      val module    = original.copy(
        header = original.header.copy(bound = Word(26)),
        instructions = original.instructions.flatMap {
          case value @ Instruction.Constant(_, dest, _) if dest == operand(OperandKind.IdResult, 19) =>
            Vec(
              value,
              Instruction.Constant(u32, execution, operand(OperandKind.LiteralContextDependentNumber, executionScope)),
              Instruction.Constant(u32, semantics, operand(OperandKind.LiteralContextDependentNumber, 0x308))
            )
          case Instruction.SubgroupShuffleINTEL(resultType, dest, data, _) =>
            Vec(
              Instruction.ControlBarrier(execution, operand(OperandKind.IdResult, 18), semantics),
              Instruction.CopyObject(resultType, dest, data)
            )
          case instruction => Vec(instruction)
        }
      )
      val allocation = AllocationId(126L)
      val storage    = ArrayMemory(allocation, 80 * 8)
      val fences     = java.util.concurrent.atomic.AtomicInteger()
      val failure    = Fault.Backend("test fence failure")
      class FencedMemory extends Memory {
        override def read(id: AllocationId, offset: Long >= 0, size: Int >= 0): Either[Fault, ArraySeq[Byte]] =
          storage.read(id, offset, size)
        override def write(id: AllocationId, offset: Long >= 0, bytes: Vec[Byte]): Either[Fault, Unit] =
          storage.write(id, offset, bytes)
        override def fenceHostMemory(): Either[Fault, Unit] = {
          fences.incrementAndGet()
          if (rejectFence) Left(failure) else Right(())
        }
      }
      val memory = FencedMemory()
      val task   = KernelTask.validated(
        "shuffle",
        Dimensions(40, 1, 1),
        Dimensions(2, 1, 1),
        Vec(KernelTask.Argument.Memory(allocation, 0L))
      ).toOption.get
      val result        = executeUnchecked(module, task, memory)
      val externalScope = scope == 0 || scope == 1 || scope >= 5
      if (rejectFence && externalScope) {
        assertEquals(result, Left(failure))
        assertEquals(storage.bytes, Vec.fill(80 * 8)(0.toByte))
      } else {
        assertEquals(result, Right(()), s"memory scope=$scope execution scope=$executionScope")
        assertEquals(fences.get() > 0, externalScope)
        assertEquals(storage.bytes.take(8), LittleEndian.toVec(0x1234567800000000L, 8))
      }
    }
  }

  test("workgroup broadcast selects both coordinates of a two-dimensional local id") {
    val original = shuffleModule(false, false, 1, false)
    val u64      = operand(OperandKind.IdResult, 3)
    val vector2  = operand(OperandKind.IdResult, 5)
    val globalY  = operand(OperandKind.IdResult, 24)
    val row      = operand(OperandKind.IdResult, 25)
    val four     = operand(OperandKind.IdResult, 26)
    val linear   = operand(OperandKind.IdResult, 27)
    val selector = operand(OperandKind.IdResult, 28)
    val two      = operand(OperandKind.IdResult, 29)
    val one      = operand(OperandKind.IdResult, 30)
    val scope    = operand(OperandKind.IdResult, 31)
    val module   = original.copy(
      header = original.header.copy(bound = Word(32)),
      instructions = original.instructions.flatMap {
        case value @ Instruction.Constant(_, dest, _) if dest == operand(OperandKind.IdResult, 19) =>
          Vec(
            value,
            Instruction.Constant(u64, four, operand(OperandKind.LiteralContextDependentNumber, 4)),
            Instruction.Constant(u64, two, operand(OperandKind.LiteralContextDependentNumber, 2)),
            Instruction.Constant(u64, one, operand(OperandKind.LiteralContextDependentNumber, 1)),
            Instruction.ConstantComposite(vector2, selector, Vec(two, one)),
            Instruction.Constant(
              operand(OperandKind.IdResult, 2),
              scope,
              operand(OperandKind.LiteralContextDependentNumber, 2)
            )
          )
        case value @ Instruction.CompositeExtract(_, dest, _, _) if dest == operand(OperandKind.IdResult, 14) =>
          Vec(
            value,
            Instruction.CompositeExtract(
              u64,
              globalY,
              operand(OperandKind.IdResult, 13),
              Vec(operand(OperandKind.LiteralInteger, 1))
            ),
            Instruction.IMul(u64, row, globalY, four),
            Instruction.IAdd(u64, linear, row, dest)
          )
        case Instruction.IAdd(resultType, dest, a, _) if dest == operand(OperandKind.IdResult, 17) =>
          Vec(Instruction.IAdd(resultType, dest, a, linear))
        case Instruction.SubgroupShuffleINTEL(resultType, dest, data, _) =>
          Vec(Instruction.GroupBroadcast(resultType, dest, scope, data, selector))
        case Instruction.PtrAccessChain(resultType, dest, base, _, indices) =>
          Vec(Instruction.PtrAccessChain(resultType, dest, base, linear, indices))
        case instruction => Vec(instruction)
      }
    )
    val allocation = AllocationId(127L)
    val memory     = ArrayMemory(allocation, 12 * 8)
    val task       = KernelTask.validated(
      "shuffle",
      Dimensions(4, 3, 1),
      Dimensions.One,
      Vec(KernelTask.Argument.Memory(allocation, 0L))
    ).toOption.get
    assertEquals(executeUnchecked(module, task, memory), Right(()))
    assertEquals(memory.bytes, Vec.fill(12)(LittleEndian.toVec(0x1234567800000006L, 8)).flatten)
  }

  private def shuffleModule(xor: Boolean, vector: Boolean, mask: Int, varying: Boolean): Module = {
    val voidType      = operand(OperandKind.IdResult, 1)
    val u32           = operand(OperandKind.IdResult, 2)
    val u64           = operand(OperandKind.IdResult, 3)
    val vector3       = operand(OperandKind.IdResult, 4)
    val vector2       = operand(OperandKind.IdResult, 5)
    val builtinType   = operand(OperandKind.IdResult, 6)
    val outputType    = operand(OperandKind.IdResult, 7)
    val functionType  = operand(OperandKind.IdResult, 8)
    val globalBuiltin = operand(OperandKind.IdResult, 9)
    val function      = operand(OperandKind.IdResult, 10)
    val output        = operand(OperandKind.IdResult, 11)
    val globalVector  = operand(OperandKind.IdResult, 13)
    val globalX       = operand(OperandKind.IdResult, 14)
    val high          = operand(OperandKind.IdResult, 15)
    val payload       = operand(OperandKind.IdResult, 16)
    val highData      = operand(OperandKind.IdResult, 17)
    val constantMask  = operand(OperandKind.IdResult, 18)
    val three         = operand(OperandKind.IdResult, 19)
    val index32       = operand(OperandKind.IdResult, 20)
    val variableMask  = operand(OperandKind.IdResult, 21)
    val result        = operand(OperandKind.IdResult, 22)
    val destination   = operand(OperandKind.IdResult, 23)
    val dataType      = if (vector) vector2 else u64
    val data          = if (vector) payload else highData
    val selector      = if (varying) variableMask else constantMask
    Module(
      Header(ISA.version, Word(0), Word(24), Word(0)),
      Vec(
        Instruction.TypeVoid(voidType),
        Instruction.TypeInt(u32, operand(OperandKind.LiteralInteger, 32), operand(OperandKind.LiteralInteger, 0)),
        Instruction.TypeInt(u64, operand(OperandKind.LiteralInteger, 64), operand(OperandKind.LiteralInteger, 0)),
        Instruction.TypeVector(vector3, u64, operand(OperandKind.LiteralInteger, 3)),
        Instruction.TypeVector(vector2, u64, operand(OperandKind.LiteralInteger, 2)),
        Instruction.TypePointer(builtinType, operand(OperandKind.StorageClass, 1), vector3),
        Instruction.TypePointer(outputType, operand(OperandKind.StorageClass, 5), dataType),
        Instruction.TypeFunction(functionType, voidType, Vec(outputType)),
        Instruction.Constant(u64, high, operand(OperandKind.LiteralContextDependentNumber, 0, 0x12345678)),
        Instruction.Constant(u32, constantMask, operand(OperandKind.LiteralContextDependentNumber, mask)),
        Instruction.Constant(u32, three, operand(OperandKind.LiteralContextDependentNumber, 3)),
        Instruction.Variable(builtinType, globalBuiltin, operand(OperandKind.StorageClass, 1), None),
        Instruction.Decorate(globalBuiltin, operand(OperandKind.Decoration, 11, 28)),
        Instruction.EntryPoint(
          operand(OperandKind.ExecutionModel, 6),
          function,
          stringOperand("shuffle"),
          Vec(globalBuiltin)
        ),
        Instruction.Function(voidType, function, operand(OperandKind.FunctionControl, 0), functionType),
        Instruction.FunctionParameter(outputType, output),
        Instruction.Label(operand(OperandKind.IdResult, 12)),
        Instruction.Load(vector3, globalVector, globalBuiltin, None),
        Instruction.CompositeExtract(u64, globalX, globalVector, Vec(operand(OperandKind.LiteralInteger, 0))),
        Instruction.IAdd(u64, highData, high, globalX),
        Instruction.CompositeConstruct(vector2, payload, Vec(globalX, highData)),
        Instruction.UConvert(u32, index32, globalX),
        Instruction.BitwiseAnd(u32, variableMask, index32, three),
        if (xor) Instruction.SubgroupShuffleXorINTEL(dataType, result, data, selector)
        else Instruction.SubgroupShuffleINTEL(dataType, result, data, selector),
        Instruction.PtrAccessChain(outputType, destination, output, globalX, Vec.empty),
        Instruction.Store(destination, result, None),
        Instruction.Return(),
        Instruction.FunctionEnd()
      ),
      Endianness.Little
    )
  }

  test("dispatch rendezvouses subgroup and workgroup collectives") {
    val voidType       = operand(OperandKind.IdResult, 1)
    val u32            = operand(OperandKind.IdResult, 2)
    val u64            = operand(OperandKind.IdResult, 3)
    val bool           = operand(OperandKind.IdResult, 4)
    val vector3        = operand(OperandKind.IdResult, 5)
    val builtinType    = operand(OperandKind.IdResult, 6)
    val outputType     = operand(OperandKind.IdResult, 7)
    val functionType   = operand(OperandKind.IdResult, 8)
    val subgroupScope  = operand(OperandKind.IdResult, 9)
    val workgroupScope = operand(OperandKind.IdResult, 10)
    val three          = operand(OperandKind.IdResult, 11)
    val localBuiltin   = operand(OperandKind.IdResult, 12)
    val function       = operand(OperandKind.IdResult, 13)
    val output         = operand(OperandKind.IdResult, 14)
    val localVector    = operand(OperandKind.IdResult, 16)
    val localX         = operand(OperandKind.IdResult, 17)
    val pred           = operand(OperandKind.IdResult, 18)
    val any            = operand(OperandKind.IdResult, 19)
    val minimum        = operand(OperandKind.IdResult, 20)
    val trueValue      = operand(OperandKind.IdResult, 21)
    val notAll         = operand(OperandKind.IdResult, 22)
    val instructions   = Vec(
      Instruction.TypeVoid(voidType),
      Instruction.TypeInt(u32, operand(OperandKind.LiteralInteger, 32), operand(OperandKind.LiteralInteger, 0)),
      Instruction.TypeInt(u64, operand(OperandKind.LiteralInteger, 64), operand(OperandKind.LiteralInteger, 0)),
      Instruction.TypeBool(bool),
      Instruction.TypeVector(vector3, u64, operand(OperandKind.LiteralInteger, 3)),
      Instruction.TypePointer(builtinType, operand(OperandKind.StorageClass, spv.StorageClass.Input), vector3),
      Instruction.TypePointer(outputType, operand(OperandKind.StorageClass, spv.StorageClass.CrossWorkgroup), u64),
      Instruction.TypeFunction(functionType, voidType, Vec(outputType)),
      Instruction.Constant(u32, subgroupScope, operand(OperandKind.LiteralContextDependentNumber, spv.Scope.Subgroup)),
      Instruction.Constant(
        u32,
        workgroupScope,
        operand(OperandKind.LiteralContextDependentNumber, spv.Scope.Workgroup)
      ),
      Instruction.Constant(u64, three, operand(OperandKind.LiteralContextDependentNumber, 3)),
      Instruction.ConstantTrue(bool, trueValue),
      Instruction.Variable(builtinType, localBuiltin, operand(OperandKind.StorageClass, spv.StorageClass.Input), None),
      Instruction.Decorate(
        localBuiltin,
        operand(OperandKind.Decoration, spv.Decoration.BuiltIn, spv.BuiltIn.LocalInvocationId)
      ),
      Instruction.EntryPoint(
        operand(OperandKind.ExecutionModel, spv.ExecutionModel.Kernel),
        function,
        stringOperand("collective"),
        Vec(localBuiltin)
      ),
      Instruction.Function(
        voidType,
        function,
        operand(OperandKind.FunctionControl, spv.FunctionControl.None),
        functionType
      ),
      Instruction.FunctionParameter(outputType, output),
      Instruction.Label(operand(OperandKind.IdResult, 15)),
      Instruction.Load(vector3, localVector, localBuiltin, None),
      Instruction.CompositeExtract(u64, localX, localVector, Vec(operand(OperandKind.LiteralInteger, 0))),
      Instruction.ULessThan(bool, pred, localX, three),
      Instruction.GroupAny(bool, any, subgroupScope, pred),
      Instruction.LogicalNotEqual(bool, notAll, any, trueValue),
      Instruction.GroupUMin(
        u64,
        minimum,
        workgroupScope,
        operand(OperandKind.GroupOperation, spv.GroupOperation.Reduce),
        localX
      ),
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

    val scanDestination  = operand(OperandKind.IdResult, 23)
    val broadcast        = operand(OperandKind.IdResult, 24)
    val scanInstructions = instructions
      .updated(
        23,
        Instruction.GroupIAdd(
          u64,
          minimum,
          workgroupScope,
          operand(OperandKind.GroupOperation, spv.GroupOperation.InclusiveScan),
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
          operand(OperandKind.GroupOperation, spv.GroupOperation.Reduce),
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
          operand(OperandKind.GroupOperation, spv.GroupOperation.Reduce),
          three
        )
      )
    )
    val multiplicationMemory = ArrayMemory(allocation, 64)
    assertEquals(executeUnchecked(multiplicationModule, scanDispatch, multiplicationMemory), Right(()))
    assertEquals(multiplicationMemory.bytes, Vec.fill(8)(LittleEndian.toVec(6561L, 8)).flatten)

    val f32                      = operand(OperandKind.IdResult, 25)
    val floatThree               = operand(OperandKind.IdResult, 26)
    val floatResult              = operand(OperandKind.IdResult, 27)
    val floatingScanInstructions = scanInstructions
      .patch(
        12,
        Vec(
          Instruction.TypeFloat(f32, operand(OperandKind.LiteralInteger, 32), None),
          Instruction.Constant(
            f32,
            floatThree,
            operand(OperandKind.LiteralContextDependentNumber, java.lang.Float.floatToRawIntBits(3.0f))
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
          operand(OperandKind.GroupOperation, spv.GroupOperation.InclusiveScan),
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
          operand(OperandKind.GroupOperation, spv.GroupOperation.InclusiveScan),
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
          operand(OperandKind.GroupOperation, spv.GroupOperation.ExclusiveScan),
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
        Instruction.Constant(u32, subgroupScope, operand(OperandKind.LiteralContextDependentNumber, spv.Scope.Subgroup))
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
          Instruction.Constant(u32, subgroupScope, operand(OperandKind.LiteralContextDependentNumber, spv.Scope.Device))
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
    val voidType      = operand(OperandKind.IdResult, 1)
    val u32           = operand(OperandKind.IdResult, 2)
    val u64           = operand(OperandKind.IdResult, 3)
    val bool          = operand(OperandKind.IdResult, 4)
    val vector3       = operand(OperandKind.IdResult, 5)
    val ballotType    = operand(OperandKind.IdResult, 6)
    val builtinType   = operand(OperandKind.IdResult, 7)
    val outputType    = operand(OperandKind.IdResult, 8)
    val functionType  = operand(OperandKind.IdResult, 9)
    val subgroupScope = operand(OperandKind.IdResult, 10)
    val three         = operand(OperandKind.IdResult, 11)
    val localBuiltin  = operand(OperandKind.IdResult, 12)
    val function      = operand(OperandKind.IdResult, 13)
    val output        = operand(OperandKind.IdResult, 14)
    val label         = operand(OperandKind.IdResult, 15)
    val localVector   = operand(OperandKind.IdResult, 16)
    val localX        = operand(OperandKind.IdResult, 17)
    val predicate     = operand(OperandKind.IdResult, 18)
    val ballot        = operand(OperandKind.IdResult, 19)
    val firstWord     = operand(OperandKind.IdResult, 20)
    val module        = Module(
      Header(ISA.version, Word(0), Word(21), Word(0)),
      Vec(
        Instruction.TypeVoid(voidType),
        Instruction.TypeInt(u32, operand(OperandKind.LiteralInteger, 32), operand(OperandKind.LiteralInteger, 0)),
        Instruction.TypeInt(u64, operand(OperandKind.LiteralInteger, 64), operand(OperandKind.LiteralInteger, 0)),
        Instruction.TypeBool(bool),
        Instruction.TypeVector(vector3, u64, operand(OperandKind.LiteralInteger, 3)),
        Instruction.TypeVector(ballotType, u32, operand(OperandKind.LiteralInteger, 4)),
        Instruction.TypePointer(builtinType, operand(OperandKind.StorageClass, spv.StorageClass.Input), vector3),
        Instruction.TypePointer(outputType, operand(OperandKind.StorageClass, spv.StorageClass.CrossWorkgroup), u32),
        Instruction.TypeFunction(functionType, voidType, Vec(outputType)),
        Instruction.Constant(
          u32,
          subgroupScope,
          operand(OperandKind.LiteralContextDependentNumber, spv.Scope.Subgroup)
        ),
        Instruction.Constant(u64, three, operand(OperandKind.LiteralContextDependentNumber, 3)),
        Instruction.Variable(
          builtinType,
          localBuiltin,
          operand(OperandKind.StorageClass, spv.StorageClass.Input),
          None
        ),
        Instruction.Decorate(
          localBuiltin,
          operand(OperandKind.Decoration, spv.Decoration.BuiltIn, spv.BuiltIn.LocalInvocationId)
        ),
        Instruction.EntryPoint(
          operand(OperandKind.ExecutionModel, spv.ExecutionModel.Kernel),
          function,
          stringOperand("ballot"),
          Vec(localBuiltin)
        ),
        Instruction.Function(
          voidType,
          function,
          operand(OperandKind.FunctionControl, spv.FunctionControl.None),
          functionType
        ),
        Instruction.FunctionParameter(outputType, output),
        Instruction.Label(label),
        Instruction.Load(vector3, localVector, localBuiltin, None),
        Instruction.CompositeExtract(u64, localX, localVector, Vec(operand(OperandKind.LiteralInteger, 0))),
        Instruction.ULessThan(bool, predicate, localX, three),
        Instruction.GroupNonUniformBallot(ballotType, ballot, subgroupScope, predicate),
        Instruction.CompositeExtract(u32, firstWord, ballot, Vec(operand(OperandKind.LiteralInteger, 0))),
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
    val voidType           = operand(OperandKind.IdResult, 1)
    val u32                = operand(OperandKind.IdResult, 2)
    val functionType       = operand(OperandKind.IdResult, 3)
    val workgroupScope     = operand(OperandKind.IdResult, 4)
    val plainFunction      = operand(OperandKind.IdResult, 5)
    val plainLabel         = operand(OperandKind.IdResult, 6)
    val collectiveFunction = operand(OperandKind.IdResult, 7)
    val collectiveLabel    = operand(OperandKind.IdResult, 8)
    val callResult         = operand(OperandKind.IdResult, 9)
    val collectiveHelper   = operand(OperandKind.IdResult, 10)
    val helperLabel        = operand(OperandKind.IdResult, 11)
    val module             = Module(
      Header(ISA.version, Word(0), Word(12), Word(0)),
      Vec(
        Instruction.TypeVoid(voidType),
        Instruction.TypeInt(u32, operand(OperandKind.LiteralInteger, 32), operand(OperandKind.LiteralInteger, 0)),
        Instruction.TypeFunction(functionType, voidType, Vec.empty),
        Instruction.Constant(
          u32,
          workgroupScope,
          operand(OperandKind.LiteralContextDependentNumber, spv.Scope.Workgroup)
        ),
        Instruction.EntryPoint(
          operand(OperandKind.ExecutionModel, spv.ExecutionModel.Kernel),
          plainFunction,
          stringOperand("plain"),
          Vec.empty
        ),
        Instruction.EntryPoint(
          operand(OperandKind.ExecutionModel, spv.ExecutionModel.Kernel),
          collectiveFunction,
          stringOperand("collective"),
          Vec.empty
        ),
        Instruction.Function(
          voidType,
          plainFunction,
          operand(OperandKind.FunctionControl, spv.FunctionControl.None),
          functionType
        ),
        Instruction.Label(plainLabel),
        Instruction.Return(),
        Instruction.FunctionEnd(),
        Instruction.Function(
          voidType,
          collectiveFunction,
          operand(OperandKind.FunctionControl, spv.FunctionControl.None),
          functionType
        ),
        Instruction.Label(collectiveLabel),
        Instruction.FunctionCall(voidType, callResult, collectiveHelper, Vec.empty),
        Instruction.Return(),
        Instruction.FunctionEnd(),
        Instruction.Function(
          voidType,
          collectiveHelper,
          operand(OperandKind.FunctionControl, spv.FunctionControl.None),
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
