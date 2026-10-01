package net.kurobako.gemstone.isa.spirv.v1_6

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.binary.Widths.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.spirv.v1_6.execution.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchGeometry.*

class NonUniformIntegerSuite extends munit.FunSuite {
  private def ref(n: Int): Operand = operand("IdRef", n)
  private def lit(n: Int): Operand = operand("LiteralInteger", n)

  for {
    width     <- Seq(8, 16, 32, 64)
    lanes     <- Seq(1, 2, 4)
    kind      <- Seq("add", "multiply", "smin", "smax", "umin", "umax", "and", "or", "xor")
    operation <- 0 to 2
    branch    <- Seq("uniform", "divergent", "return")
    deferred  <- if (width == 32 && lanes == 1 && kind == "add") Seq(false, true) else Seq(false)
  } test(
    s"nonuniform $kind $width x $lanes operation $operation $branch deferred=$deferred reconverges across partial subgroups"
  ) {
    val count                                     = 69 // Two complete subgroups and one partial subgroup.
    val valueType                                 = if (lanes == 1) 3 else 4
    def collective(destination: Int): Instruction = {
      val args = operand("GroupOperation", operation)
      kind match {
        case "add" => Instruction.GroupNonUniformIAdd(ref(valueType), ref(destination), ref(12), args, ref(25), None)
        case "multiply" =>
          Instruction.GroupNonUniformIMul(ref(valueType), ref(destination), ref(12), args, ref(25), None)
        case "smin" => Instruction.GroupNonUniformSMin(ref(valueType), ref(destination), ref(12), args, ref(25), None)
        case "smax" => Instruction.GroupNonUniformSMax(ref(valueType), ref(destination), ref(12), args, ref(25), None)
        case "umin" => Instruction.GroupNonUniformUMin(ref(valueType), ref(destination), ref(12), args, ref(25), None)
        case "umax" => Instruction.GroupNonUniformUMax(ref(valueType), ref(destination), ref(12), args, ref(25), None)
        case "and"  =>
          Instruction.GroupNonUniformBitwiseAnd(ref(valueType), ref(destination), ref(12), args, ref(25), None)
        case "or" =>
          Instruction.GroupNonUniformBitwiseOr(ref(valueType), ref(destination), ref(12), args, ref(25), None)
        case _ => Instruction.GroupNonUniformBitwiseXor(ref(valueType), ref(destination), ref(12), args, ref(25), None)
      }
    }

    val conditional = if (branch == "uniform") Vec.empty[Instruction]
    else Vec(
      Instruction.BitwiseAnd(ref(2), ref(26), ref(22), ref(13)),
      Instruction.IEqual(ref(9), ref(27), ref(26), ref(14)),
      Instruction.SelectionMerge(ref(33), operand("SelectionControl", SelectionControl.None)),
      Instruction.BranchConditional(ref(27), ref(30), ref(31), Vec.empty),
      Instruction.Label(ref(30))
    )
    val merge = if (branch == "uniform") Vec.empty[Instruction]
    else Vec(
      Instruction.Branch(ref(33)),
      Instruction.Label(ref(31)),
      if (branch == "return") Instruction.Return() else Instruction.Branch(ref(33)),
      Instruction.Label(ref(33))
    )
    val module = Module(
      Header(ISA.version, Word(0), Word(48), Word(0)),
      Vec(
        Instruction.TypeVoid(ref(1)),
        Instruction.TypeInt(ref(2), lit(32), lit(0)),
        Instruction.TypeInt(ref(3), lit(width), lit(0)),
        Instruction.TypeVector(ref(4), ref(3), lit(4)),
        Instruction.TypePointer(ref(5), operand("StorageClass", StorageClass.CrossWorkgroup), ref(valueType)),
        Instruction.TypeVector(ref(6), ref(2), lit(3)),
        Instruction.TypePointer(ref(7), operand("StorageClass", StorageClass.Input), ref(6)),
        Instruction.Variable(ref(7), ref(8), operand("StorageClass", StorageClass.Input), None),
        Instruction.Decorate(ref(8), operand("Decoration", Decoration.BuiltIn, 27)),
        Instruction.TypeBool(ref(9)),
        Instruction.TypeFunction(ref(10), ref(1), Vec(ref(5), ref(5))),
        Instruction.Constant(ref(2), ref(12), operand("LiteralContextDependentNumber", 3)),
        Instruction.Constant(ref(2), ref(13), operand("LiteralContextDependentNumber", 1)),
        Instruction.Constant(ref(2), ref(14), operand("LiteralContextDependentNumber", 0)),
        Instruction.Constant(ref(2), ref(15), operand("LiteralContextDependentNumber", count)),
        Instruction.EntryPoint(
          operand("ExecutionModel", ExecutionModel.Kernel),
          ref(16),
          stringOperand("reduce"),
          Vec(ref(8))
        ),
        Instruction.Function(ref(1), ref(16), operand("FunctionControl", FunctionControl.None), ref(10)),
        Instruction.FunctionParameter(ref(5), ref(18)),
        Instruction.FunctionParameter(ref(5), ref(19)),
        Instruction.Label(ref(20)),
        Instruction.Load(ref(6), ref(21), ref(8), None),
        Instruction.CompositeExtract(ref(2), ref(22), ref(21), Vec(lit(0))),
        Instruction.PtrAccessChain(ref(5), ref(23), ref(18), ref(22), Vec.empty),
        Instruction.PtrAccessChain(ref(5), ref(24), ref(19), ref(22), Vec.empty),
        Instruction.Load(ref(valueType), ref(25), ref(23), None)
      ).map {
        case Instruction.TypeVector(t, component, _) if t == ref(4) =>
          Instruction.TypeVector(t, component, lit(math.max(2, lanes)))
        case instruction => instruction
      } ++ conditional ++ Vec(
        collective(28),
        Instruction.Store(ref(24), ref(28), None)
      ) ++ merge ++ Vec(
        Instruction.IAdd(ref(2), ref(34), ref(22), ref(15)),
        Instruction.PtrAccessChain(ref(5), ref(35), ref(19), ref(34), Vec.empty),
        collective(36),
        Instruction.Store(ref(35), ref(36), None),
        Instruction.Return(),
        Instruction.FunctionEnd()
      ),
      Endianness.Little
    )
    val bytes                                      = (width / 8).unsafe[Int where Between[0, 8]]
    val bits                                       = (BigInt(1) << width) - 1
    def input(thread: Int, component: Int): BigInt =
      if (kind == "multiply") BigInt(if ((thread + component) % 2 == 0) -1 else 1) & bits
      else if (thread % 5 == 0) ((BigInt(1) << (width - 1)) + component) & bits
      else BigInt(thread % 7 + component - 3) & bits
    def encode(value: BigInt): Vec[Byte] = LittleEndian.toVec((value & bits).toLong, bytes)
    val source = Vec.from((0 until count).flatMap(t => (0 until lanes).flatMap(c => encode(input(t, c)))))
    val region = AllocationId(1L)
    val memory = ArrayMemory(region, source.size * 3)
    assertEquals(
      memory.write(region, 0L, source ++ Vec.fill(source.size * 2)(0x55.toByte)),
      Right(())
    )
    val task = KernelTask.validated(
      "reduce",
      Dimensions(
        69,
        1,
        1
      ),
      Dimensions.One,
      Vec(
        KernelTask.Argument.Memory(region, 0L),
        KernelTask.Argument.Memory(region, source.size.toLong.unsafe[Long >= 0])
      )
    ).toOption.get
    class DeferredMemory extends Memory {
      private val pending                                = scala.collection.mutable.Set.empty[Long]
      private val ready                                  = scala.collection.mutable.Set.empty[Long]
      override def defersHostReads: Boolean              = true
      override def flushHostReads(): Either[Fault, Unit] = {
        val _ = ready.addAll(pending)
        pending.clear()
        Right(())
      }
      override def read(id: AllocationId, offset: Long >= 0, size: Int >= 0): Either[Fault, Vec[Byte]] =
        awaitRead(readDeferred(id, offset, size))
      override def readValueDeferred(
          id: AllocationId,
          offset: Long >= 0,
          size: Int where Between[1, 8]
      ): Memory.ReadResult[Long] =
        readDeferred(id, offset, size.nonNegative).map(bytes =>
          LittleEndian.decode(bytes, 0, size.scalarByteWidth)
        )
      override def readDeferred(id: AllocationId, offset: Long >= 0, size: Int >= 0): Memory.ReadResult[Vec[Byte]] =
        if (offset.raw % 8L == 0L || ready.contains(offset.raw)) memory.read(id, offset, size)
        else {
          val _ = pending.add(offset.raw)
          Left(Memory.Pending)
        }
      override def write(id: AllocationId, offset: Long >= 0, value: Vec[Byte]): Either[Fault, Unit] =
        memory.write(id, offset, value)
    }
    val backing = if (deferred) DeferredMemory() else memory
    assertEquals(executeUnchecked(module, task, backing), Right(()))
    def signed(x: BigInt): BigInt = if (x.testBit(width - 1)) x - (BigInt(1) << width) else x
    val identity                  = kind match {
      case "multiply"     => BigInt(1)
      case "smin"         => (BigInt(1) << (width - 1)) - 1
      case "smax"         => BigInt(1) << (width - 1)
      case "umin" | "and" => bits
      case _              => BigInt(0)
    }
    def combine(a: BigInt, b: BigInt): BigInt =
      (kind match {
        case "add"      => a + b
        case "multiply" => a * b
        case "smin"     => if (signed(a) < signed(b)) a else b
        case "smax"     => if (signed(a) > signed(b)) a else b
        case "umin"     => a.min(b)
        case "umax"     => a.max(b)
        case "and"      => a & b
        case "or"       => a | b
        case _          => a ^ b
      }) & bits
    val expected = Vec.from((0 until 2).flatMap { output =>
      (0 until count).flatMap { thread =>
        val masked = branch != "uniform" && (output == 0 || branch == "return")
        if (masked && thread % 2 != 0) Vec.fill(lanes * width / 8)(0x55.toByte)
        else {
          val start    = thread / 32 * 32
          val active   = (start until math.min(start + 32, count)).filter(t => !masked || t % 2 == 0)
          val selected = active.filter(t => operation == 0 || (if (operation == 1) t <= thread else t < thread))
          (0 until lanes).flatMap(c => encode(selected.foldLeft(identity)((a, t) => combine(a, input(t, c)))))
        }
      }
    })
    assertEquals(memory.bytes.drop(source.size), expected)
  }
}
