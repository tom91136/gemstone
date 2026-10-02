package net.kurobako.gemstone.isa.spirv.v1_6

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.spirv.v1_6.execution.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchGeometry.*

class NonUniformFloatingSuite extends munit.FunSuite {
  private def ref(n: Int): Operand = operand(OperandKind.IdRef, n)
  private def lit(n: Int): Operand = operand(OperandKind.LiteralInteger, n)

  for {
    width     <- Seq(16, 32, 64)
    lanes     <- Seq(1, 2, 4)
    kind      <- Seq("add", "multiply", "minimum", "maximum")
    operation <- 0 to 2
    branch    <- Seq("uniform", "divergent", "return")
  } test(s"nonuniform $kind $width x $lanes operation $operation $branch reconverges across partial subgroups") {
    val count                                     = 69 // Two complete subgroups and one partial subgroup.
    val valueType                                 = if (lanes == 1) 3 else 4
    def collective(destination: Int): Instruction = {
      val args = operand(OperandKind.GroupOperation, operation)
      kind match {
        case "add" => Instruction.GroupNonUniformFAdd(ref(valueType), ref(destination), ref(12), args, ref(25), None)
        case "multiply" =>
          Instruction.GroupNonUniformFMul(ref(valueType), ref(destination), ref(12), args, ref(25), None)
        case "minimum" =>
          Instruction.GroupNonUniformFMin(ref(valueType), ref(destination), ref(12), args, ref(25), None)
        case _ => Instruction.GroupNonUniformFMax(ref(valueType), ref(destination), ref(12), args, ref(25), None)
      }
    }
    val conditional = if (branch == "uniform") Vec.empty[Instruction]
    else Vec(
      Instruction.BitwiseAnd(ref(2), ref(26), ref(22), ref(13)),
      Instruction.IEqual(ref(9), ref(27), ref(26), ref(14)),
      Instruction.SelectionMerge(ref(33), operand(OperandKind.SelectionControl, SelectionControl.None)),
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
        Instruction.TypeFloat(ref(3), lit(width), None),
        Instruction.TypeVector(ref(4), ref(3), lit(4)),
        Instruction.TypePointer(ref(5), operand(OperandKind.StorageClass, StorageClass.CrossWorkgroup), ref(valueType)),
        Instruction.TypeVector(ref(6), ref(2), lit(3)),
        Instruction.TypePointer(ref(7), operand(OperandKind.StorageClass, StorageClass.Input), ref(6)),
        Instruction.Variable(ref(7), ref(8), operand(OperandKind.StorageClass, StorageClass.Input), None),
        Instruction.Decorate(ref(8), operand(OperandKind.Decoration, Decoration.BuiltIn, 27)),
        Instruction.TypeBool(ref(9)),
        Instruction.TypeFunction(ref(10), ref(1), Vec(ref(5), ref(5))),
        Instruction.Constant(ref(2), ref(12), operand(OperandKind.LiteralContextDependentNumber, 3)),
        Instruction.Constant(ref(2), ref(13), operand(OperandKind.LiteralContextDependentNumber, 1)),
        Instruction.Constant(ref(2), ref(14), operand(OperandKind.LiteralContextDependentNumber, 0)),
        Instruction.Constant(ref(2), ref(15), operand(OperandKind.LiteralContextDependentNumber, count)),
        Instruction.EntryPoint(
          operand(OperandKind.ExecutionModel, ExecutionModel.Kernel),
          ref(16),
          stringOperand("reduce"),
          Vec(ref(8))
        ),
        Instruction.Function(ref(1), ref(16), operand(OperandKind.FunctionControl, FunctionControl.None), ref(10)),
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
    val floatWidth                                 = width.unsafe[Int in (16, 32, 64)]
    def input(thread: Int, component: Int): Double =
      if (kind == "multiply") if ((thread + component) % 2 == 0) -1.0 else 1.0
      else (thread % 5 + component + 1).toDouble
    def encode(value: Double): Vec[Byte] = LittleEndian.toVec(IEEE754.fromDouble(value, floatWidth), bytes)
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
    assertEquals(executeUnchecked(module, task, memory), Right(()))
    val identity = kind match {
      case "add"      => 0.0
      case "multiply" => 1.0
      case "minimum"  => Double.PositiveInfinity
      case _          => Double.NegativeInfinity
    }
    def combine(a: Double, b: Double): Double = kind match {
      case "add"      => a + b
      case "multiply" => a * b
      case "minimum"  => math.min(a, b)
      case _          => math.max(a, b)
    }
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
