package net.kurobako.gemstone.isa.spirv.v1_6

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.spirv.v1_6.execution.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchGeometry.*

class BitFieldSuite extends munit.FunSuite {
  for {
    width            <- Seq(8, 16, 32, 64)
    count            <- Seq(1, 4)
    resultSigned     <- Seq(0, 1)
    leftSigned       <- Seq(resultSigned)
    rightSigned      <- Seq(resultSigned)
    operation        <- Seq("uextract", "sextract", "insert", "reverse")
    (offset, length) <-
      Seq((0, 0), (width, 0), (0, width), (width - 1, 1), (1, width - 2), (-1, 1), (width, 1), (0, width + 1))
    if operation != "reverse" || (offset == 0 && length == 0)
  } test(
    s"$operation: $width bits, $count lanes, signedness $resultSigned/$leftSigned/$rightSigned, offset $offset length $length"
  ) {
    def ref(n: Int): Operand            = operand("IdRef", n)
    def literal(n: Int): Operand        = operand("LiteralInteger", n)
    def valueType(signed: Int): Operand = ref((if (count == 1) 2 else 4) + signed)
    val resultType                      = valueType(resultSigned)
    val leftType                        = valueType(leftSigned)
    val rightType                       = valueType(rightSigned)
    val instruction: Instruction        = operation match {
      case "uextract" => Instruction.BitFieldUExtract(resultType, ref(12), ref(10), ref(15), ref(16))
      case "sextract" => Instruction.BitFieldSExtract(resultType, ref(12), ref(10), ref(15), ref(16))
      case "insert"   => Instruction.BitFieldInsert(resultType, ref(12), ref(10), ref(11), ref(15), ref(16))
      case _          => Instruction.BitReverse(resultType, ref(12), ref(10))
    }
    val module = Module(
      Header(ISA.version, Word(0), Word(17), Word(0)),
      Vec(
        Instruction.TypeVoid(ref(1)),
        Instruction.TypeInt(ref(2), literal(width), literal(0)),
        Instruction.TypeInt(ref(3), literal(width), literal(1)),
        Instruction.TypeVector(ref(4), ref(2), literal(4)),
        Instruction.TypeVector(ref(5), ref(3), literal(4)),
        Instruction.TypePointer(ref(6), operand("StorageClass", StorageClass.CrossWorkgroup), resultType),
        Instruction.TypeInt(ref(14), literal(32), literal(1)),
        Instruction.Constant(ref(14), ref(15), operand("LiteralContextDependentNumber", offset)),
        Instruction.Constant(ref(14), ref(16), operand("LiteralContextDependentNumber", length)),
        Instruction.TypeFunction(ref(7), ref(1), Vec(ref(6), leftType, rightType)),
        Instruction.EntryPoint(
          operand("ExecutionModel", ExecutionModel.Kernel),
          ref(8),
          stringOperand("bits"),
          Vec.empty
        ),
        Instruction.Function(ref(1), ref(8), operand("FunctionControl", FunctionControl.None), ref(7)),
        Instruction.FunctionParameter(ref(6), ref(9)),
        Instruction.FunctionParameter(leftType, ref(10)),
        Instruction.FunctionParameter(rightType, ref(11)),
        Instruction.Label(ref(13)),
        instruction,
        Instruction.Store(ref(9), ref(12), None),
        Instruction.Return(),
        Instruction.FunctionEnd()
      ),
      Endianness.Little
    )
    val left     = Seq(-1L, 0L, Long.MinValue | 0x80L, 0x5555555555555555L).take(count)
    val right    = Seq(0xaaaaaaaaaaaaaaaaL, -1L, 0x7fL, 0L).take(count)
    val valid    = offset >= 0 && length >= 0 && offset + length <= width
    val wordMask = (BigInt(1) << width) - 1
    val expected = left.zip(right).map { (a, b) =>
      val word      = BigInt(a) & wordMask
      val fieldMask = if (valid) (BigInt(1) << length) - 1 else BigInt(0)
      val extracted = if (valid) (word >> offset) & fieldMask else BigInt(0)
      val result    = operation match {
        case "uextract" => extracted
        case "sextract" =>
          if (length > 0 && valid && extracted.testBit(length - 1)) extracted - (BigInt(1) << length) else extracted
        case "insert" =>
          if (valid) (word & (wordMask ^ (fieldMask << offset))) | ((BigInt(b) & fieldMask) << offset) else BigInt(0)
        case _ =>
          (0 until width).foldLeft(BigInt(0))((n, bit) => if (word.testBit(bit)) n.setBit(width - 1 - bit) else n)
      }
      (result & wordMask).toLong
    }
    def encoded(values: Seq[Long]): Vec[Byte] = Vec.from(values.flatMap(value =>
      LittleEndian.toVec(value, (width / 8).unsafe[Int where Between[0, 8]])
    ))
    val region = AllocationId(1L)
    val memory = ArrayMemory(region, width / 8 * count)
    val task   = KernelTask.validated(
      "bits",
      Dimensions.One,
      Dimensions.One,
      Vec(
        KernelTask.Argument.Memory(region, 0L),
        KernelTask.Argument.Scalar(encoded(left)),
        KernelTask.Argument.Scalar(encoded(right))
      )
    ).toOption.get
    if (valid) {
      assertEquals(executeUnchecked(module, task, memory), Right(()))
      assertEquals(memory.bytes, encoded(expected))
    } else {
      assert(executeUnchecked(module, task, memory).isLeft)
      assertEquals(memory.bytes, Vec.fill(width / 8 * count)(0.toByte))
    }
  }
}
