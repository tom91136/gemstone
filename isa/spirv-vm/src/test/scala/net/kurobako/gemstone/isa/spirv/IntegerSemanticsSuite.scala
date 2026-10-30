package net.kurobako.gemstone.isa.spirv

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.semantics.render.*
import net.kurobako.gemstone.testing.Suite

class IntegerSemanticsSuite extends Suite {
  private def ref(id: Int): Operand = Operand(OperandKind.IdRef, Vec.nonEmpty(Word(id)))

  private val u32 = Shape.Scalar(Type.unsigned(32))
  private val f32 = Shape.Scalar(Type.Float(32))

  /** Ids 1 to 6 named `t1` to `t6`, each a scalar of `shape` unless `shapes` says otherwise. */
  private def instance(shape: Shape, shapes: (Int, Shape)*): IdRenderer.Instance =
    IdRenderer.Instance((1 to 6).map(id => id -> s"t$id").toMap, (1 to 6).map(_ -> shape).toMap ++ shapes)

  private def show(instruction: Instruction, ids: IdRenderer.Instance = instance(u32), set: Option[String] = None)(
      using munit.Location
  ): String =
    InstructionSemantics.describe(instruction, ids, set).fold(failure => fail(failure.message), ScalaBackend.show(_))

  test("instructions describe their semantics over their typed ids") {
    assertEquals(
      show(Instruction.IAdd(ref(1), ref(2), ref(3), ref(4))),
      "def OpIAdd(t3: UInt[32], t4: UInt[32]): (t2: UInt[32]) =\n  (t2 = t3 + t4)"
    )
    assertEquals(
      show(
        Instruction.SGreaterThan(ref(1), ref(2), ref(3), ref(4)),
        instance(Shape.Scalar(Type.signed(32)))
      ),
      "def OpSGreaterThan(t3: SInt[32], t4: SInt[32]): (t2: Bool) =\n  (t2 = t4 < t3)"
    )
    val pair = Shape.Structure(Vec(u32, u32))
    assertEquals(
      show(Instruction.IAddCarry(ref(1), ref(2), ref(3), ref(4)), instance(u32, 1 -> pair, 2 -> pair)),
      """def OpIAddCarry(t3: UInt[32], t4: UInt[32]): (t2_0: UInt[32], t2_1: Bool) = {
        |  val v0 = t3 + t4
        |  (
        |    t2_0 = v0,
        |    t2_1 = v0 < t3
        |  )
        |}""".stripMargin
    )
    assertEquals(
      show(
        Instruction.ExtInst(
          ref(1),
          ref(2),
          ref(5),
          ref(ExtendedInstruction.OpenClStd.Fma),
          Vec(ref(3), ref(4), ref(6))
        ),
        instance(f32),
        Some("OpenCL.std")
      ),
      "def fma(t3: Float32, t4: Float32, t6: Float32): (t2: Float32) =\n  (t2 = Float32.fma(t3, t4, t6))"
    )
    assertEquals(
      show(
        Instruction.ExtInst(ref(1), ref(2), ref(5), ref(ExtendedInstruction.GlslStd450.Length), Vec(ref(3))),
        instance(Shape.Scalar(Type.Float(64)), 3 -> Shape.Vector(Type.Float(64), 2)),
        Some("GLSL.std.450")
      ),
      "def Length(t3_0: Float64, t3_1: Float64): (t2: Float64) =\n" +
        "  (t2 = Elementary.norm(Elementary.norm(FloatingPoint[64](0), t3_0), t3_1))"
    )
    assertEquals(
      InstructionSemantics.describe(Instruction.Load(ref(1), ref(2), ref(3), None), instance(u32)),
      Left(Unrendered.Unstaged(Vec.empty))
    )
  }

  test("instructions without a result computed from ids have no semantics") {
    assertEquals(
      InstructionSemantics.describe(Instruction.Store(ref(1), ref(2), None), instance(u32)),
      Left(Unrendered.Unstaged(Vec.empty))
    )
    assertEquals(
      InstructionSemantics.describe(Instruction.Undef(ref(1), ref(2)), instance(u32)),
      Left(Unrendered.Unstaged(Vec.empty))
    )
  }

  test("the kernel table evaluates the same semantics") {
    val kernel = ArithmeticOps.integerKernel(SPIRV.Opcode.SMod, 32).getOrElse(fail("no SMod kernel"))
    assertEquals(kernel(-7L, 3L), 2L)
    assertEquals(kernel(7L, 0L), 0L)
    assertEquals(ArithmeticOps.integerKernel(SPIRV.Opcode.ShiftLeftLogical, 32).map(_(1L, 32L)), Some(0L))
    assertEquals(ArithmeticOps.integerKernel(SPIRV.Opcode.SNegate, 16).map(_(1L, 0L)), Some(0xffffL))
    assertEquals(ComparisonOps.comparisonKernel(SPIRV.Opcode.SLessThan, 16).map(_(0x8000L, 1L)), Some(1L))
    assertEquals(ArithmeticOps.integerKernel(SPIRV.Opcode.FAdd, 32), None)
  }
}
