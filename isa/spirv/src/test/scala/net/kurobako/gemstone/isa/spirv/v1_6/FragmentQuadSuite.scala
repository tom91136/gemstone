package net.kurobako.gemstone.isa.spirv.v1_6

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.spirv.v1_6.execution.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.vm.*

class FragmentQuadSuite extends munit.FunSuite {
  private def ref(n: Int)                                                          = operand("IdRef", n)
  private def dest(n: Int)                                                         = operand("IdResult", n)
  private def lit(n: Int)                                                          = operand("LiteralInteger", n)
  private def derivative(name: String, destination: Int, source: Int): Instruction = name match {
    case "dx"          => Instruction.DPdx(ref(2), dest(destination), ref(source))
    case "dy"          => Instruction.DPdy(ref(2), dest(destination), ref(source))
    case "width"       => Instruction.Fwidth(ref(2), dest(destination), ref(source))
    case "dxFine"      => Instruction.DPdxFine(ref(2), dest(destination), ref(source))
    case "dyFine"      => Instruction.DPdyFine(ref(2), dest(destination), ref(source))
    case "widthFine"   => Instruction.FwidthFine(ref(2), dest(destination), ref(source))
    case "dxCoarse"    => Instruction.DPdxCoarse(ref(2), dest(destination), ref(source))
    case "dyCoarse"    => Instruction.DPdyCoarse(ref(2), dest(destination), ref(source))
    case "widthCoarse" => Instruction.FwidthCoarse(ref(2), dest(destination), ref(source))
  }
  private def divergentFlow(flow: String): Vec[Instruction] = flow match {
    case "selection" => Vec(
        Instruction.FOrdLessThan(ref(12), dest(33), ref(22), ref(37)),
        Instruction.SelectionMerge(ref(44), operand("SelectionControl", SelectionControl.None)),
        Instruction.BranchConditional(ref(33), ref(40), ref(44), Vec.empty),
        Instruction.Label(dest(40)),
        derivative("dx", 41, 23),
        Instruction.Branch(ref(44)),
        Instruction.Label(dest(44))
      )
    case "loop" | "break" | "continue" => Vec(
        Instruction.Branch(ref(40)),
        Instruction.Label(dest(40)),
        Instruction.Phi(ref(2), dest(42), Vec(operand("PairIdRefIdRef", 36, 21), operand("PairIdRefIdRef", 46, 43))),
        Instruction.LoopMerge(ref(44), ref(43), operand("LoopControl", LoopControl.None))
      ) ++
        (if (flow != "break") Vec(
           Instruction.FOrdLessThan(ref(12), dest(33), ref(42), ref(22)),
           Instruction.BranchConditional(ref(33), ref(41), ref(44), Vec.empty),
           Instruction.Label(dest(41))
         )
         else Vec(
           Instruction.Branch(ref(41)),
           Instruction.Label(dest(41)),
           Instruction.FOrdLessThan(ref(12), dest(33), ref(42), ref(22)),
           Instruction.SelectionMerge(ref(47), operand("SelectionControl", SelectionControl.None)),
           Instruction.BranchConditional(ref(33), ref(47), ref(44), Vec.empty),
           Instruction.Label(dest(47))
         )) ++
        (if (flow == "continue") Vec(
           Instruction.FOrdLessThan(ref(12), dest(48), ref(42), ref(37)),
           Instruction.SelectionMerge(ref(47), operand("SelectionControl", SelectionControl.None)),
           Instruction.BranchConditional(ref(48), ref(43), ref(47), Vec.empty),
           Instruction.Label(dest(47))
         )
         else Vec.empty[Instruction]) ++ Vec(
          derivative("dx", 45, 23),
          Instruction.Branch(ref(43)),
          Instruction.Label(dest(43)),
          Instruction.FAdd(ref(2), dest(46), ref(42), ref(35)),
          Instruction.Branch(ref(40)),
          Instruction.Label(dest(44))
        )
    case "call" | "return_value" => Vec(
        Instruction.FunctionCall(ref(if (flow == "call") 1 else 2), dest(39), ref(60), Vec.empty)
      )
    case _ => Vec.empty
  }
  private def helperFunction(flow: String): Vec[Instruction] =
    if (flow != "call" && flow != "return_value") Vec.empty
    else {
      def ret: Instruction = if (flow == "call") Instruction.Return() else Instruction.ReturnValue(ref(62))
      Vec(
        Instruction.Function(
          ref(if (flow == "call") 1 else 2),
          dest(60),
          operand("FunctionControl", FunctionControl.None),
          ref(if (flow == "call") 5 else 70)
        ),
        Instruction.Label(dest(61)),
        Instruction.Load(ref(2), dest(62), ref(6), None),
        Instruction.FOrdLessThan(ref(12), dest(63), ref(62), ref(37)),
        Instruction.SelectionMerge(ref(64), operand("SelectionControl", SelectionControl.None)),
        Instruction.BranchConditional(ref(63), ref(66), ref(64), Vec.empty),
        Instruction.Label(dest(66)),
        ret,
        Instruction.Label(dest(64)),
        derivative("dy", 65, 62),
        ret,
        Instruction.FunctionEnd()
      )
    }
  private def module(operation: String, repeated: Boolean, flow: String): Module = Module(
    Header(ISA.version, Word(0), Word(80), Word(0)),
    Vec(
      Instruction.Capability(operand("Capability", Capability.Shader)),
      Instruction.Capability(operand("Capability", Capability.DerivativeControl)),
      Instruction.MemoryModel(
        operand("AddressingModel", AddressingModel.Logical),
        operand("MemoryModel", MemoryModel.GLSL450)
      ),
      Instruction.EntryPoint(
        operand("ExecutionModel", ExecutionModel.Fragment),
        ref(20),
        stringOperand("main"),
        Vec(ref(6), ref(7), ref(13), ref(16))
      ),
      Instruction.ExecutionMode(ref(20), operand("ExecutionMode", ExecutionMode.OriginUpperLeft)),
      Instruction.Decorate(ref(6), operand("Decoration", Decoration.Location, 0)),
      Instruction.Decorate(ref(7), operand("Decoration", Decoration.Location, 0)),
      Instruction.Decorate(ref(16), operand("Decoration", Decoration.BuiltIn, 23)),
      Instruction.Decorate(ref(13), operand("Decoration", Decoration.Binding, 0)),
      Instruction.Decorate(ref(13), operand("Decoration", Decoration.DescriptorSet, 0)),
      Instruction.Decorate(ref(9), operand("Decoration", Decoration.Block)),
      Instruction.MemberDecorate(ref(9), lit(0), operand("Decoration", Decoration.Offset, 0)),
      Instruction.MemberDecorate(ref(9), lit(1), operand("Decoration", Decoration.Offset, 4)),
      Instruction.TypeVoid(dest(1)),
      Instruction.TypeFloat(dest(2), lit(32), None),
      Instruction.TypePointer(dest(3), operand("StorageClass", StorageClass.Input), ref(2)),
      Instruction.TypePointer(dest(4), operand("StorageClass", StorageClass.Output), ref(2)),
      Instruction.TypeFunction(dest(5), ref(1), Vec.empty),
      Instruction.TypeFunction(dest(70), ref(2), Vec.empty),
      Instruction.Constant(
        ref(2),
        dest(35),
        operand("LiteralContextDependentNumber", java.lang.Float.floatToRawIntBits(1f))
      ),
      Instruction.Constant(ref(2), dest(36), operand("LiteralContextDependentNumber", 0)),
      Instruction.Constant(
        ref(2),
        dest(37),
        operand("LiteralContextDependentNumber", java.lang.Float.floatToRawIntBits(3f))
      ),
      Instruction.TypeInt(dest(8), lit(32), lit(0)),
      Instruction.TypeStruct(dest(9), Vec(ref(8), ref(2))),
      Instruction.TypePointer(dest(10), operand("StorageClass", StorageClass.StorageBuffer), ref(9)),
      Instruction.TypePointer(dest(11), operand("StorageClass", StorageClass.StorageBuffer), ref(8)),
      Instruction.TypePointer(dest(31), operand("StorageClass", StorageClass.StorageBuffer), ref(2)),
      Instruction.TypeBool(dest(12)),
      Instruction.TypePointer(dest(14), operand("StorageClass", StorageClass.Input), ref(12)),
      Instruction.Constant(ref(8), dest(15), operand("LiteralContextDependentNumber", 0)),
      Instruction.Constant(ref(8), dest(17), operand("LiteralContextDependentNumber", 1)),
      Instruction.Variable(ref(3), dest(6), operand("StorageClass", StorageClass.Input), None),
      Instruction.Variable(ref(4), dest(7), operand("StorageClass", StorageClass.Output), None),
      Instruction.Variable(ref(10), dest(13), operand("StorageClass", StorageClass.StorageBuffer), None),
      Instruction.Variable(ref(14), dest(16), operand("StorageClass", StorageClass.Input), None),
      Instruction.Function(ref(1), dest(20), operand("FunctionControl", FunctionControl.None), ref(5)),
      Instruction.Label(dest(21)),
      Instruction.Load(ref(2), dest(22), ref(6), None),
      // Helpers may read their own output variables even though raster output is discarded.
      Instruction.Store(ref(7), ref(22), None),
      Instruction.Load(ref(2), dest(32), ref(7), None),
      Instruction.FMul(ref(2), dest(23), ref(32), ref(32))
    ) ++ divergentFlow(flow) ++ Vec(derivative(operation, 24, 23)) ++
      (if (repeated) Vec(derivative("dyFine", 25, 24)) else Vec.empty[Instruction]) ++ Vec(
        Instruction.Store(ref(7), ref(if (repeated) 25 else 24), None),
        Instruction.AccessChain(ref(11), dest(26), ref(13), Vec(ref(15))),
        Instruction.AtomicIAdd(ref(8), dest(27), ref(26), ref(17), ref(15), ref(17)),
        Instruction.AccessChain(ref(31), dest(28), ref(13), Vec(ref(17))),
        Instruction.Store(ref(28), ref(22), None),
        Instruction.Return(),
        Instruction.FunctionEnd()
      ) ++ helperFunction(flow),
    Endianness.Little
  )
  private val expectations = List(
    "dx"          -> Seq(3f, 3f, 16f, 16f),
    "dy"          -> Seq(8f, 21f, 8f, 21f),
    "width"       -> Seq(11f, 24f, 24f, 37f),
    "dxFine"      -> Seq(3f, 3f, 16f, 16f),
    "dyFine"      -> Seq(8f, 21f, 8f, 21f),
    "widthFine"   -> Seq(11f, 24f, 24f, 37f),
    "dxCoarse"    -> Seq.fill(4)(3f),
    "dyCoarse"    -> Seq.fill(4)(8f),
    "widthCoarse" -> Seq.fill(4)(11f)
  )
  for {
    (operation, expected) <- expectations
    repeated              <- List(false, true) if !repeated || operation == "dxFine"
    flow                  <- List("uniform", "selection", "loop", "break", "continue", "call", "return_value")
  }
    test(s"$operation repeated=$repeated flow=$flow uses nonlinear lane values and suppresses helper side effects") {

      val program = LoadedProgram.load(
        module(operation, repeated, flow),
        Validation.Environment.VulkanGraphics
      ).fold(e => fail(e.message), identity)
      val region                      = AllocationId(705L)
      val memory                      = ArrayMemory(region, 96)
      def write(at: Long, bits: Long) = assertEquals(
        memory.write(region, at.unsafe[Long >= 0], LittleEndian.toVec(bits, 4)),
        Right(())
      )
      for ((v, i) <- Seq(1f, 2f, 3f, 5f, 2f, 4f, 6f, 10f).zipWithIndex)
        write(16L + i * 4, java.lang.Float.floatToRawIntBits(v).toLong)
      write(8, 0xfeedL); write(88, 0xbeefL)
      assertEquals(
        memory.write(region, 80L, Vec[Byte](0, 1, 0, 1, 0, 1, 0, 1)),
        Right(())
      )
      val task = KernelTask.validated(
        "main",
        testDimensions(4),
        testDimensions(2),
        Vec(
          KernelTask.Argument.Memory(
            region,
            0L,
            Some(8L)
          ),
          KernelTask.Argument.Memory(
            region,
            16L,
            Some(32L)
          ),
          KernelTask.Argument.Memory(
            region,
            48L,
            Some(32L)
          ),
          KernelTask.Argument.Memory(
            region,
            80L,
            Some(8L)
          )
        )
      ).fold(fail(_), identity)
      val _     = program.execute(task, memory, TaskControl()).fold(e => fail(e.message), identity)
      val first = if (repeated) Seq.fill(4)(13f) else expected
      for ((value, i) <- (first ++ first.map(_ * 4)).zipWithIndex)
        assertEquals(
          LittleEndian.decode(memory.bytes, 48 + i * 4, 4),
          java.lang.Float.floatToRawIntBits(value).toLong & 0xffffffffL
        )
      assertEquals(LittleEndian.decode(memory.bytes, 0, 4), 4L)
      assertEquals(
        LittleEndian.decode(memory.bytes, 4, 4),
        java.lang.Float.floatToRawIntBits(6f).toLong
      )
      assertEquals(LittleEndian.decode(memory.bytes, 8, 4), 0xfeedL)
      assertEquals(LittleEndian.decode(memory.bytes, 88, 4), 0xbeefL)
    }
}
