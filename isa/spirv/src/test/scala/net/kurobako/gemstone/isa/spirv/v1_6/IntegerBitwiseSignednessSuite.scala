package net.kurobako.gemstone.isa.spirv.v1_6

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.spirv.v1_6.execution.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchGeometry.*

class IntegerBitwiseSignednessSuite extends munit.FunSuite {
  for {
    width        <- Seq(8, 16, 32, 64)
    count        <- Seq(1, 4)
    resultSigned <- Seq(0, 1)
    leftSigned   <- Seq(0, 1)
    rightSigned  <- Seq(0, 1)
    operation    <- Seq("and", "or", "xor", "not")
    if operation != "not" || rightSigned == 0
  } test(s"$operation: $width bits, $count lanes, signedness $resultSigned/$leftSigned/$rightSigned") {
    def ref(n: Int): Operand            = operand("IdRef", n)
    def literal(n: Int): Operand        = operand("LiteralInteger", n)
    def valueType(signed: Int): Operand = ref((if (count == 1) 2 else 4) + signed)
    val resultType                      = valueType(resultSigned)
    val leftType                        = valueType(leftSigned)
    val rightType                       = valueType(rightSigned)
    val instruction: Instruction        = operation match {
      case "and" => Instruction.BitwiseAnd(resultType, ref(12), ref(10), ref(11))
      case "or"  => Instruction.BitwiseOr(resultType, ref(12), ref(10), ref(11))
      case "xor" => Instruction.BitwiseXor(resultType, ref(12), ref(10), ref(11))
      case _     => Instruction.Not(resultType, ref(12), ref(10))
    }
    val module = Module(
      Header(ISA.version, Word(0), Word(14), Word(0)),
      Vec(
        Instruction.TypeVoid(ref(1)),
        Instruction.TypeInt(ref(2), literal(width), literal(0)),
        Instruction.TypeInt(ref(3), literal(width), literal(1)),
        Instruction.TypeVector(ref(4), ref(2), literal(4)),
        Instruction.TypeVector(ref(5), ref(3), literal(4)),
        Instruction.TypePointer(ref(6), operand("StorageClass", StorageClass.CrossWorkgroup), resultType),
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
    val expected = left.zip(right).map { (a, b) =>
      operation match {
        case "and" => a & b
        case "or"  => a | b
        case "xor" => a ^ b
        case _     => ~a
      }
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
    assertEquals(executeUnchecked(module, task, memory), Right(()))
    assertEquals(memory.bytes, encoded(expected))
  }
}
