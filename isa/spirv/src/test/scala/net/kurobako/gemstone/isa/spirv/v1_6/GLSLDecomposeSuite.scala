package net.kurobako.gemstone.isa.spirv.v1_6

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.spirv.v1_6.execution.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchGeometry.*

class GLSLDecomposeSuite extends munit.FunSuite {
  for {
    width  <- Seq(32, 64)
    count  <- Seq(1, 4)
    opcode <- Seq(35, 51)
  }
    test(s"pointer decomposition opcode=$opcode width=$width lanes=$count") {
      def ref(n: Int): Operand       = operand("IdRef", n)
      def lit(n: Int): Operand       = operand("LiteralInteger", n)
      def floatBits(v: Double): Long = if (width == 32) java.lang.Float.floatToRawIntBits(v.toFloat).toLong &
        0xffffffffL
      else java.lang.Double.doubleToRawLongBits(v)
      val source =
        Seq(-2.5, -0.0, Math.scalb(1.0, if (width == 32) -140 else -1070), Double.PositiveInfinity).take(count)
      val exponent  = Seq(2L, 0L, if (width == 32) -139L else -1069L, 0L).take(count)
      val fractions = if (opcode == 35)
        Seq(-0.5, -0.0, Math.scalb(1.0, if (width == 32) -140 else -1070), 0.0).take(count)
      else Seq(-0.625, -0.0, 0.5, Double.PositiveInfinity).take(count)
      val integral =
        if (opcode == 35) Seq(-2.0, -0.0, 0.0, Double.PositiveInfinity).take(count).map(floatBits(_)) else exponent
      val valueType    = if (count == 1) 2 else 4
      val integralType = if (opcode == 35) valueType else if (count == 1) 3 else 5
      val module       = Module(
        Header(ISA.version, Word(0), Word(64), Word(0)),
        Vec(
          Instruction.TypeVoid(ref(1)),
          Instruction.TypeFloat(ref(2), lit(width), None),
          Instruction.TypeInt(ref(3), lit(64), lit(1)),
          Instruction.TypeVector(ref(4), ref(2), lit(4)),
          Instruction.TypeVector(ref(5), ref(3), lit(4)),
          Instruction.TypePointer(ref(6), operand("StorageClass", StorageClass.CrossWorkgroup), ref(valueType)),
          Instruction.TypePointer(ref(7), operand("StorageClass", StorageClass.CrossWorkgroup), ref(integralType)),
          Instruction.TypeFunction(ref(8), ref(1), Vec(ref(6), ref(7), ref(valueType))),
          Instruction.ExtInstImport(ref(9), stringOperand("GLSL.std.450")),
          Instruction.EntryPoint(
            operand("ExecutionModel", ExecutionModel.Kernel),
            ref(10),
            stringOperand("decompose"),
            Vec.empty
          ),
          Instruction.Function(ref(1), ref(10), operand("FunctionControl", FunctionControl.None), ref(8)),
          Instruction.FunctionParameter(ref(6), ref(11)),
          Instruction.FunctionParameter(ref(7), ref(12)),
          Instruction.FunctionParameter(ref(valueType), ref(13)),
          Instruction.Label(ref(14)),
          Instruction.ExtInst(
            ref(valueType),
            ref(15),
            ref(9),
            operand("LiteralExtInstInteger", opcode),
            Vec(ref(13), ref(12))
          ),
          Instruction.Store(ref(11), ref(15), None),
          Instruction.Return(),
          Instruction.FunctionEnd()
        ),
        Endianness.Little
      )
      def bytes(v: Seq[Long], bits: Int): Vec[Byte] =
        Vec.from(v.flatMap(LittleEndian.toVec(_, (bits / 8).unsafe[Int where Between[0, 8]])))
      val expected = bytes(fractions.map(floatBits(_)), width) ++ bytes(integral, if (opcode == 35) width else 64)
      val region   = AllocationId(1L); val memory = ArrayMemory(region, expected.size)
      val task     = KernelTask.validated(
        "decompose",
        Dimensions.One,
        Dimensions.One,
        Vec(
          KernelTask.Argument.Memory(region, 0L),
          KernelTask.Argument.Memory(region, (count * width / 8).toLong.unsafe[Long >= 0]),
          KernelTask.Argument.Scalar(bytes(source.map(floatBits(_)), width))
        )
      ).toOption.get
      assertEquals(executeUnchecked(module, task, memory), Right(()))
      assertEquals(memory.bytes, expected)
    }
}
