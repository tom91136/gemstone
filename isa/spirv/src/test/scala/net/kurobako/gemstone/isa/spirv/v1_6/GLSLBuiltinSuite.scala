package net.kurobako.gemstone.isa.spirv.v1_6

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.spirv.v1_6.execution.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchGeometry.*

class GLSLBuiltinSuite extends munit.FunSuite {
  private def f(value: Float): Long           = java.lang.Float.floatToRawIntBits(value).toLong & 0xffffffffL
  private def words(values: Long*): Vec[Byte] = Vec.from(values.flatMap(LittleEndian.toVec(_, 4)))
  private def run(opcode: Int, resultType: Int, inputs: Seq[(Int, Vec[Byte])], expected: Vec[Byte]): Unit = {
    def ref(n: Int): Operand = operand(OperandKind.IdRef, n)
    val module               = Module(
      Header(ISA.version, Word(0), Word(64), Word(0)),
      Vec(
        Instruction.TypeVoid(ref(1)),
        Instruction.TypeFloat(ref(2), operand(OperandKind.LiteralInteger, 32), None),
        Instruction.TypeFloat(ref(3), operand(OperandKind.LiteralInteger, 64), None),
        Instruction.TypeInt(ref(4), operand(OperandKind.LiteralInteger, 32), operand(OperandKind.LiteralInteger, 0)),
        Instruction.TypeInt(ref(11), operand(OperandKind.LiteralInteger, 64), operand(OperandKind.LiteralInteger, 1)),
        Instruction.TypeInt(ref(10), operand(OperandKind.LiteralInteger, 32), operand(OperandKind.LiteralInteger, 1)),
        Instruction.TypeVector(ref(5), ref(2), operand(OperandKind.LiteralInteger, 2)),
        Instruction.TypeVector(ref(6), ref(2), operand(OperandKind.LiteralInteger, 4)),
        Instruction.TypeVector(ref(7), ref(4), operand(OperandKind.LiteralInteger, 2)),
        Instruction.TypeStruct(ref(8), Vec(ref(2), ref(2))),
        Instruction.TypeStruct(ref(9), Vec(ref(2), ref(10))),
        Instruction.TypePointer(
          ref(30),
          operand(OperandKind.StorageClass, StorageClass.CrossWorkgroup),
          ref(resultType)
        ),
        Instruction.TypeFunction(ref(31), ref(1), Vec(ref(30)) ++ Vec.from(inputs.map(x => ref(x._1)))),
        Instruction.ExtInstImport(ref(32), stringOperand("GLSL.std.450")),
        Instruction.EntryPoint(
          operand(OperandKind.ExecutionModel, ExecutionModel.Kernel),
          ref(33),
          stringOperand("builtin"),
          Vec.empty
        ),
        Instruction.Function(ref(1), ref(33), operand(OperandKind.FunctionControl, FunctionControl.None), ref(31)),
        Instruction.FunctionParameter(ref(30), ref(35))
      ) ++ Vec.from(inputs.zipWithIndex.map((x, i) => Instruction.FunctionParameter(ref(x._1), ref(40 + i)))) ++ Vec(
        Instruction.Label(ref(34)),
        Instruction.ExtInst(
          ref(resultType),
          ref(50),
          ref(32),
          operand(OperandKind.LiteralExtInstInteger, opcode),
          Vec.from(inputs.indices.map(i => ref(40 + i)))
        ),
        Instruction.Store(ref(35), ref(50), None),
        Instruction.Return(),
        Instruction.FunctionEnd()
      ),
      Endianness.Little
    )
    val region = AllocationId(1L); val memory = ArrayMemory(region, expected.size)
    val task   = KernelTask.validated(
      "builtin",
      Dimensions.One,
      Dimensions.One,
      Vec(KernelTask.Argument.Memory(region, 0L)) ++
        Vec.from(inputs.map(x => KernelTask.Argument.Scalar(x._2)))
    ).toOption.get
    assertEquals(executeUnchecked(module, task, memory), Right(()))
    assertEquals(memory.bytes, expected)
  }

  test("normalized packing clamps and rounds each channel in bit order") {
    run(54, 4, Seq(6 -> words(f(-1), f(0), f(1), f(0.25f))), words(0x207f0081L))
    run(55, 4, Seq(6 -> words(f(-1), f(0.25f), f(1), f(2))), words(0xffff4000L))
    run(56, 4, Seq(5 -> words(f(-1), f(1))), words(0x7fff8001L))
    run(57, 4, Seq(5 -> words(f(-1), f(1))), words(0xffff0000L))
  }
  test("normalized unpacking handles signed minimum encodings") {
    run(60, 5, Seq(4 -> words(0x7fff8000L)), words(f(-1), f(1)))
    run(61, 5, Seq(4 -> words(0xffff0000L)), words(f(0), f(1)))
    run(63, 6, Seq(4 -> words(0x7f008180L)), words(f(-1), f(-1), f(0), f(1)))
    run(64, 6, Seq(4 -> words(0xff00ff00L)), words(f(0), f(1), f(0), f(1)))
  }
  test("half packing preserves signed zero and unpacking preserves subnormals") {
    run(58, 4, Seq(5 -> words(f(1), f(-2))), words(0xc0003c00L))
    run(58, 4, Seq(5 -> words(f(-0.0f), f(0))), words(0x00008000L))
    run(62, 5, Seq(4 -> words(0x80000001L)), words(f(Math.scalb(1.0f, -24)), f(-0.0f)))
  }
  test("double packing transfers bits without floating arithmetic") {
    run(59, 3, Seq(7 -> words(0x89abcdefL, 0x7ff12345L)), words(0x89abcdefL, 0x7ff12345L))
    run(65, 7, Seq(3 -> words(0x89abcdefL, 0xfff12345L)), words(0x89abcdefL, 0xfff12345L))
  }
  test("modf retains fractional signs and frexp normalizes subnormals") {
    run(36, 8, Seq(2 -> words(f(-2.75f))), words(f(-0.75f), f(-2)))
    run(36, 8, Seq(2 -> words(f(Float.NegativeInfinity))), words(f(-0.0f), f(Float.NegativeInfinity)))
    run(52, 9, Seq(2 -> words(1)), words(f(0.5f), -148L))
    run(52, 9, Seq(2 -> words(f(-0.0f))), words(f(-0.0f), 0L))
  }
  test("hyperbolic functions retain GLSL exp intermediate overflow") {
    run(19, 2, Seq(2 -> words(f(89.41436f))), words(f(Float.PositiveInfinity)))
    run(20, 2, Seq(2 -> words(f(-89.41436f))), words(f(Float.PositiveInfinity)))
  }
  test("GLSL Ldexp accepts wide exponents without narrowing overflow") {
    def wide(value: Long): Vec[Byte] = LittleEndian.toVec(value, 8)
    run(53, 2, Seq(2 -> words(f(1.5f)), 11 -> wide(3)), words(f(12.0f)))
    run(53, 2, Seq(2 -> words(f(1.5f)), 11 -> wide(Long.MaxValue)), words(f(Float.PositiveInfinity)))
    run(53, 2, Seq(2 -> words(f(-1.5f)), 11 -> wide(Long.MinValue)), words(f(-0.0f)))
  }

}
