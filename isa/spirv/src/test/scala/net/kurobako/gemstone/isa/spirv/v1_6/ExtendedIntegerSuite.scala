package net.kurobako.gemstone.isa.spirv.v1_6

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.binary.Widths.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.spirv.v1_6.execution.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchGeometry.*

class ExtendedIntegerSuite extends munit.FunSuite {
  for {
    width <- Seq[Int in (8, 16, 32, 64)](8, 16, 32, 64)
    rawWidth   = width.raw
    widthBytes = width.widen[Int in (8, 16, 32, 64)].toMemoryValueByteWidth
    count     <- Seq(1, 4)
    operation <- Seq("carry", "borrow", "umul", "smul")
    signed    <- if (operation == "smul") Seq(0, 1) else Seq(0)
  } test(s"extended $operation: $rawWidth bits, $count lanes, signedness $signed") {
    def ref(n: Int): Operand = operand(OperandKind.IdRef, n)
    val valueType            = ref(if (count == 1) 2 else 3)
    val instruction          = operation match {
      case "carry"  => Instruction.IAddCarry(ref(4), ref(12), ref(10), ref(11))
      case "borrow" => Instruction.ISubBorrow(ref(4), ref(12), ref(10), ref(11))
      case "umul"   => Instruction.UMulExtended(ref(4), ref(12), ref(10), ref(11))
      case _        => Instruction.SMulExtended(ref(4), ref(12), ref(10), ref(11))
    }
    val module = Module(
      Header(ISA.version, Word(0), Word(14), Word(0)),
      Vec(
        Instruction.TypeVoid(ref(1)),
        Instruction.TypeInt(
          ref(2),
          operand(OperandKind.LiteralInteger, rawWidth),
          operand(OperandKind.LiteralInteger, signed)
        ),
        Instruction.TypeVector(ref(3), ref(2), operand(OperandKind.LiteralInteger, 4)),
        Instruction.TypeStruct(ref(4), Vec(valueType, valueType)),
        Instruction.TypePointer(ref(5), operand(OperandKind.StorageClass, StorageClass.CrossWorkgroup), ref(4)),
        Instruction.TypeFunction(ref(6), ref(1), Vec(ref(5), valueType, valueType)),
        Instruction.EntryPoint(
          operand(OperandKind.ExecutionModel, ExecutionModel.Kernel),
          ref(7),
          stringOperand("extended"),
          Vec.empty
        ),
        Instruction.Function(ref(1), ref(7), operand(OperandKind.FunctionControl, FunctionControl.None), ref(6)),
        Instruction.FunctionParameter(ref(5), ref(9)),
        Instruction.FunctionParameter(valueType, ref(10)),
        Instruction.FunctionParameter(valueType, ref(11)),
        Instruction.Label(ref(13)),
        instruction,
        Instruction.Store(ref(9), ref(12), None),
        Instruction.Return(),
        Instruction.FunctionEnd()
      ),
      Endianness.Little
    )
    val program  = loadUnchecked(module).toOption.get
    val wordMask = (BigInt(1) << rawWidth) - 1
    val pairs    = Seq(
      (-1L, -1L),
      (Long.MaxValue, Long.MaxValue),
      (0L, 1L),
      (Long.MinValue, 2L),
      (Long.MaxValue, Long.MinValue),
      (Long.MinValue, Long.MinValue),
      (1L, -1L),
      (0x123456789abcdefL, 0xfedcba9876543210L),
      (1L << (rawWidth - 1), 1L << (rawWidth - 1)),
      ((1L << (rawWidth - 1)) - 1, 2L)
    )
    def encoded(values: Seq[Long]): Vec[Byte] =
      Vec.from(values.flatMap(LittleEndian.toVec(_, widthBytes.widen[Int in 0 ~ 8])))
    for (index <- pairs.indices) {
      val inputs  = (0 until count).map(lane => pairs((index + lane) % pairs.size))
      val results = inputs.map { (a, b) =>
        def value(n: Long): BigInt = {
          val bits = BigInt(n) & wordMask
          if (operation == "smul" && bits.testBit(rawWidth - 1)) bits - (BigInt(1) << rawWidth) else bits
        }
        val x    = value(a); val y = value(b)
        val full = if (operation == "carry") x + y else if (operation == "borrow") x - y else x * y
        val high = if (operation == "borrow") { if (x < y) BigInt(1) else BigInt(0) }
        else full >> rawWidth
        ((full & wordMask).toLong, (high & wordMask).toLong)
      }
      val region = AllocationId(1L); val memory = ArrayMemory(region, rawWidth / 8 * count * 2)
      val task   = KernelTask.validated(
        "extended",
        Dimensions.One,
        Dimensions.One,
        Vec(
          KernelTask.Argument.Memory(region, 0L),
          KernelTask.Argument.Scalar(encoded(inputs.map(_._1))),
          KernelTask.Argument.Scalar(encoded(inputs.map(_._2)))
        )
      ).toOption.get
      assertEquals(executeLoaded(program, task, memory), Right(()))
      assertEquals(memory.bytes, encoded(results.map(_._1) ++ results.map(_._2)))
    }
  }

  for {
    baseWidth <- Seq[Int in (8, 16, 32, 64)](8, 16, 32, 64)
    rawBaseWidth   = baseWidth.raw
    baseWidthBytes = baseWidth.widen[Int in (8, 16, 32, 64)].toMemoryValueByteWidth
    resultWidth <- Seq[Int in (8, 32, 64)](8, 32, 64)
    rawResultWidth   = resultWidth.raw
    resultWidthBytes = resultWidth.widen[Int in (8, 16, 32, 64)].toMemoryValueByteWidth
    count  <- Seq(1, 4)
    signed <- Seq(0, 1)
  }
    test(
      s"bitcount accepts $rawBaseWidth-bit input and $rawResultWidth-bit result, $count lanes, input signedness $signed"
    ) {
      def ref(n: Int): Operand = operand(OperandKind.IdRef, n)
      val inputType            = ref(if (count == 1) 2 else 4); val resultType = ref(if (count == 1) 3 else 5)
      val module               = Module(
        Header(ISA.version, Word(0), Word(14), Word(0)),
        Vec(
          Instruction.TypeVoid(ref(1)),
          Instruction.TypeInt(
            ref(2),
            operand(OperandKind.LiteralInteger, rawBaseWidth),
            operand(OperandKind.LiteralInteger, signed)
          ),
          Instruction.TypeInt(
            ref(3),
            operand(OperandKind.LiteralInteger, rawResultWidth),
            operand(OperandKind.LiteralInteger, 1 - signed)
          ),
          Instruction.TypeVector(ref(4), ref(2), operand(OperandKind.LiteralInteger, 4)),
          Instruction.TypeVector(ref(5), ref(3), operand(OperandKind.LiteralInteger, 4)),
          Instruction.TypePointer(ref(6), operand(OperandKind.StorageClass, StorageClass.CrossWorkgroup), resultType),
          Instruction.TypeFunction(ref(7), ref(1), Vec(ref(6), inputType)),
          Instruction.EntryPoint(
            operand(OperandKind.ExecutionModel, ExecutionModel.Kernel),
            ref(8),
            stringOperand("count"),
            Vec.empty
          ),
          Instruction.Function(ref(1), ref(8), operand(OperandKind.FunctionControl, FunctionControl.None), ref(7)),
          Instruction.FunctionParameter(ref(6), ref(9)),
          Instruction.FunctionParameter(inputType, ref(10)),
          Instruction.Label(ref(11)),
          Instruction.BitCount(resultType, ref(12), ref(10)),
          Instruction.Store(ref(9), ref(12), None),
          Instruction.Return(),
          Instruction.FunctionEnd()
        ),
        Endianness.Little
      )
      val inputs   = Seq(-1L, 0L, 0x5555555555555555L, 1L << (rawBaseWidth - 1)).take(count)
      val expected = Seq(rawBaseWidth, 0, rawBaseWidth / 2, 1).take(count)
      val region   = AllocationId(1L); val memory = ArrayMemory(region, rawResultWidth / 8 * count)
      val task     = KernelTask.validated(
        "count",
        Dimensions.One,
        Dimensions.One,
        Vec(
          KernelTask.Argument.Memory(region, 0L),
          KernelTask.Argument.Scalar(Vec.from(inputs.flatMap(LittleEndian.toVec(
            _,
            baseWidthBytes.widen[Int in 0 ~ 8]
          ))))
        )
      ).toOption.get
      assertEquals(executeUnchecked(module, task, memory), Right(()))
      assertEquals(
        memory.bytes,
        Vec.from(expected.flatMap(n => LittleEndian.toVec(n.toLong, resultWidthBytes.widen[Int in 0 ~ 8])))
      )
    }
}
