package net.kurobako.gemstone.isa.spirv.v1_6

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.binary.Widths.*
import net.kurobako.gemstone.isa.spirv.v1_6.execution.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchGeometry.*

class FloatingRoundingSuite extends munit.FunSuite {
  private def ref(n: Int): Operand = operand(OperandKind.IdRef, n)

  for {
    (sourceWidth, width) <- Seq[(Int in (32, 64), Int in (16, 32))]((32, 16), (64, 16), (64, 32))
    rawSourceWidth = sourceWidth.raw
    rawWidth = width.raw
    sourceWidthBytes = sourceWidth.widen[Int in (8, 16, 32, 64)].toMemoryValueByteWidth
    widthBytes = width.widen[Int in (8, 16, 32, 64)].toMemoryValueByteWidth
    mode                 <- 0 to 3
    grouped              <- Seq(false, true)
  } {
    val fraction = if (rawWidth == 16) 10 else 23
    val one      = if (rawWidth == 16) 0x3c00L else 0x3f800000L
    val infinity = if (rawWidth == 16) 0x7c00L else 0x7f800000L
    val sign     = 1L << (rawWidth - 1)
    val ulp      = math.scalb(1.0, -fraction)
    val tiny     = math.scalb(1.0, if (rawWidth == 16) -24 else -149)
    val maximum  = if (rawWidth == 16) 65504.0 else Float.MaxValue.toDouble
    // Expected encodings in RTE, RTZ, RTP, RTN order, independently specified.
    val cases = Seq(
      ("zero", 0.0, Seq(0L, 0L, 0L, 0L)),
      ("exact", 1.0, Seq(one, one, one, one)),
      ("below midpoint", 1.0 + ulp * 0.25, Seq(one, one, one + 1, one)),
      ("even midpoint", 1.0 + ulp * 0.5, Seq(one, one, one + 1, one)),
      ("above midpoint", 1.0 + ulp * 0.75, Seq(one + 1, one, one + 1, one)),
      ("odd midpoint", 1.0 + ulp * 1.5, Seq(one + 2, one + 1, one + 2, one + 1)),
      ("underflow", tiny * 0.25, Seq(0L, 0L, 1L, 0L)),
      ("underflow midpoint", tiny * 0.5, Seq(0L, 0L, 1L, 0L)),
      ("subnormal", tiny * 0.75, Seq(1L, 0L, 1L, 0L)),
      ("overflow", maximum * 2.0, Seq(infinity, infinity - 1, infinity, infinity - 1)),
      ("infinity", Double.PositiveInfinity, Seq(infinity, infinity, infinity, infinity))
    ) ++
      (if (rawSourceWidth == 64 && rawWidth == 16) Seq(
         ("double just above midpoint", 1.0 + ulp * 0.5 + math.scalb(1.0, -40), Seq(one + 1, one, one + 1, one)),
         ("double just below midpoint", 1.0 + ulp * 0.5 - math.scalb(1.0, -40), Seq(one, one, one + 1, one))
       )
       else Seq.empty)

    for {
      (label, value, expected) <- cases
      lanes                    <- Seq(1, 4)
    }
      test(s"FConvert $rawSourceWidth to $rawWidth mode $mode $label lanes $lanes grouped $grouped") {
        val decorations = if (grouped) Vec(
          Instruction.DecorationGroup(ref(30)),
          Instruction.Decorate(ref(30), operand(OperandKind.Decoration, Decoration.FPRoundingMode, mode)),
          Instruction.GroupDecorate(ref(30), Vec(ref(21)))
        )
        else Vec(Instruction.Decorate(ref(21), operand(OperandKind.Decoration, Decoration.FPRoundingMode, mode)))
        val sourceType = if (lanes == 1) 2 else 4
        val targetType = if (lanes == 1) 3 else 5
        val module     = Module(
          Header(ISA.version, Word(0), Word(32), Word(0)),
          decorations ++ Vec(
            Instruction.TypeVoid(ref(1)),
            Instruction.TypeFloat(ref(2), operand(OperandKind.LiteralInteger, rawSourceWidth), None),
            Instruction.TypeFloat(ref(3), operand(OperandKind.LiteralInteger, rawWidth), None),
            Instruction.TypeVector(ref(4), ref(2), operand(OperandKind.LiteralInteger, 4)),
            Instruction.TypeVector(ref(5), ref(3), operand(OperandKind.LiteralInteger, 4)),
            Instruction.TypePointer(
              ref(6),
              operand(OperandKind.StorageClass, StorageClass.CrossWorkgroup),
              ref(targetType)
            ),
            Instruction.TypeFunction(ref(7), ref(1), Vec(ref(6), ref(sourceType))),
            Instruction.EntryPoint(
              operand(OperandKind.ExecutionModel, ExecutionModel.Kernel),
              ref(16),
              stringOperand("convert"),
              Vec.empty
            ),
            Instruction.Function(ref(1), ref(16), operand(OperandKind.FunctionControl, FunctionControl.None), ref(7)),
            Instruction.FunctionParameter(ref(6), ref(18)),
            Instruction.FunctionParameter(ref(sourceType), ref(20)),
            Instruction.Label(ref(17)),
            Instruction.FConvert(ref(targetType), ref(21), ref(20)),
            Instruction.Store(ref(18), ref(21), None),
            Instruction.Return(),
            Instruction.FunctionEnd()
          ),
          Endianness.Little
        )
        for (negative <- Seq(false, true)) {
          val input = if (negative) -value else value
          val raw   = if (rawSourceWidth == 32) java.lang.Float.floatToRawIntBits(input.toFloat).toLong
          else java.lang.Double.doubleToRawLongBits(input)
          val region = AllocationId(1L)
          val memory = ArrayMemory(region, lanes * rawWidth / 8)
          val task   = KernelTask.validated(
            "convert",
            Dimensions.One,
            Dimensions.One,
            Vec(
              KernelTask.Argument.Memory(region, 0L),
              KernelTask.Argument.Scalar(Vec.from(Vector.fill(lanes)(raw)).flatMap(
                LittleEndian.toVec(_, sourceWidthBytes.widen[Int in 0 ~ 8])
              ))
            )
          ).toOption.get
          assertEquals(executeUnchecked(module, task, memory), Right(()))
          val magnitude = expected(if (negative && mode >= 2) 5 - mode else mode)
          val bits      = magnitude | (if (negative) sign else 0L)
          assertEquals(
            memory.bytes,
            Vec.from(Vector.fill(lanes)(bits)).flatMap(
              LittleEndian.toVec(_, widthBytes.widen[Int in 0 ~ 8])
            )
          )
        }
      }
  }
}
