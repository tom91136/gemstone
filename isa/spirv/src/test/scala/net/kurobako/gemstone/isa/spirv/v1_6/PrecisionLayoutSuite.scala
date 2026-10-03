package net.kurobako.gemstone.isa.spirv.v1_6

import execution.*
import net.kurobako.gemstone.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.binary.Widths.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.constraint.auto.*
import net.kurobako.gemstone.isa.*
import net.kurobako.gemstone.isa.spirv.v1_6 as spv
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchGeometry.*

class PrecisionLayoutSuite extends munit.FunSuite {
  private def o(kind: OperandKind, values: Int*): Operand = Operand(
    kind,
    Vec.from(values.map(Word(_))).unsafe[NonEmpty[Vec[Word]]]
  )
  private def r(value: Int)                                    = o(OperandKind.IdResult, value)
  private def literal(value: Long, width: Int in (16, 32, 64)) = {
    val byteWidth = width.scalarByteWidth
    Operand(
      OperandKind.LiteralContextDependentNumber,
      Vec.from(LittleEndian.toArray(value, byteWidth).grouped(4).map { bytes =>
        val chunkWidth = bytes.length.refine[Int in 0 ~ 8]
          .fold(message => throw IllegalArgumentException(message), value => value)
        Word(LittleEndian.decode(bytes, 0, chunkWidth).toInt)
      }).unsafe
    )
  }
  private val entry      = Operand(OperandKind.LiteralString, Vec.nonEmpty(Word(0x6b)))
  private val allocation = AllocationId(1)
  private class Memory(size: Int) extends vm.Memory {
    val bytes: Array[Byte]                                                                    = Array.ofDim[Byte](size)
    def read(id: AllocationId, offset: Long >= 0, length: Int >= 0): Either[Fault, Vec[Byte]] = {
      assert(id == allocation && ByteRange.contains(size, offset.raw, length.raw.toLong))
      Right(Vec.from(bytes.slice(offset.raw.toInt, offset.raw.toInt + length.raw)))
    }
    def write(id: AllocationId, offset: Long >= 0, value: Vec[Byte]): Either[Fault, Unit] = {
      assert(id == allocation && ByteRange.contains(size, offset.raw, value.size))
      val _ = value.copyToArray(bytes, offset.raw.toInt, value.size); Right(())
    }
  }
  private def run(instructions: Vec[Instruction], args: Vec[KernelTask.Argument], memory: Memory): Unit = {
    val profile =
      Vec.from(Seq(4, 6, 7, 9, 10, 11).map(value => Instruction.Capability(o(OperandKind.Capability, value)))) :+
        Instruction.MemoryModel(
          o(OperandKind.AddressingModel, spv.AddressingModel.Physical64),
          o(OperandKind.MemoryModel, spv.MemoryModel.OpenCL)
        )
    val module = Module(
      Header(ISA.version, Word(0), Word(64), Word(0)),
      profile ++ instructions,
      Endianness.Little
    )
    val dispatch =
      KernelTask.validated("k", Dimensions.One, Dimensions.One, args).toOption.get
    assertEquals(
      executeLoaded(
        VMCompiler.compile(module, Validation.Environment.OpenCLCompute)
          .map(VMProgramRuntime(_))
          .fold(error => fail(error.message), value => value),
        dispatch,
        memory
      ),
      Right(())
    )
  }
  for {
    (width, count) <- Seq(
      (32: Int in (16, 32, 64)) -> 4,
      (32: Int in (16, 32, 64)) -> 8,
      (32: Int in (16, 32, 64)) -> 16,
      (64: Int in (16, 32, 64)) -> 3,
      (64: Int in (16, 32, 64)) -> 4,
      (64: Int in (16, 32, 64)) -> 8
    )
    byValue <- Seq(false, true)
  }
    test(
      s"natural OpenCL vector layout width=$width lanes=$count byval=$byValue preserves struct array stride"
    ) {
      val byteWidth = width.scalarByteWidth
      val align     = byteWidth.raw * (if (count == 3) 4 else count)
      val size      = align * 2
      val expected  = Array.ofDim[Byte](size * 2)
      for (base <- Seq(0, size)) {
        val baseOffset =
          base.refine[Int >= 0].fold(message => fail(message), value => value)
        LittleEndian.write(IEEE754.fromDouble(2.25, width), expected, baseOffset.raw, byteWidth)
        for (lane <- 0 until count) {
          val laneOffset = (base + align + lane * byteWidth.raw).refine[Int >= 0]
            .fold(message => fail(message), value => value)
          LittleEndian.write(IEEE754.fromDouble(1.5, width), expected, laneOffset.raw, byteWidth)
        }
      }
      val definitions = Vec[Instruction](
        Instruction.TypeVoid(r(1)),
        Instruction.TypeFloat(r(2), o(OperandKind.LiteralInteger, width.raw), None),
        Instruction.TypeVector(r(3), r(2), o(OperandKind.LiteralInteger, count)),
        Instruction.TypeStruct(r(4), Vec(r(2), r(3))),
        Instruction.TypePointer(r(5), o(OperandKind.StorageClass, spv.StorageClass.CrossWorkgroup), r(4)),
        Instruction.TypePointer(r(6), o(OperandKind.StorageClass, spv.StorageClass.Function), r(4)),
        Instruction.TypeInt(r(7), o(OperandKind.LiteralInteger, 32), o(OperandKind.LiteralInteger, 0)),
        Instruction.Constant(r(7), r(8), literal(1, 32)),
        Instruction.Constant(r(2), r(9), literal(IEEE754.fromDouble(1.5, width), width)),
        Instruction.Constant(r(2), r(10), literal(IEEE754.fromDouble(2.25, width), width)),
        Instruction.ConstantComposite(r(3), r(11), Vec.fill(count)(r(9))),
        Instruction.ConstantComposite(r(4), r(12), Vec(r(10), r(11))),
        Instruction.TypeFunction(r(13), r(1), if (byValue) Vec(r(6), r(5)) else Vec(r(5))),
        Instruction.EntryPoint(o(OperandKind.ExecutionModel, spv.ExecutionModel.Kernel), r(20), entry, Vec.empty)
      ) ++
        (if (byValue)
           Vec[Instruction](Instruction.Decorate(r(21), o(OperandKind.Decoration, spv.Decoration.FuncParamAttr, 2)))
         else Vec.empty[Instruction])
      val parameters = if (byValue)
        Vec[Instruction](Instruction.FunctionParameter(r(6), r(21)), Instruction.FunctionParameter(r(5), r(22)))
      else Vec[Instruction](Instruction.FunctionParameter(r(5), r(22)))
      val instructions = definitions ++
        Vec[Instruction](Instruction.Function(
          r(1),
          r(20),
          o(OperandKind.FunctionControl, spv.FunctionControl.None),
          r(13)
        )) ++
        parameters ++
        Vec[Instruction](Instruction.Label(r(23))) ++
        (if (byValue) Vec[Instruction](Instruction.Load(r(4), r(24), r(21), None)) else Vec.empty[Instruction]) ++
        Vec[Instruction](
          Instruction.Store(r(22), if (byValue) r(24) else r(12), None),
          Instruction.PtrAccessChain(r(5), r(25), r(22), r(8), Vec.empty),
          Instruction.Store(r(25), if (byValue) r(24) else r(12), None),
          Instruction.Return(),
          Instruction.FunctionEnd()
        )
      val args =
        (if (byValue) Vec[KernelTask.Argument](KernelTask.Argument.Scalar(Vec.from(expected.take(size))))
         else Vec.empty[KernelTask.Argument]) :+ KernelTask.Argument.Memory(allocation, 0L)
      val memory = Memory(expected.length)
      run(instructions, args, memory)
      assertEquals(memory.bytes.toSeq, expected.toSeq)
    }
  for (
    (input, expected) <- Seq(
      (1 + math.pow(2, -11) + math.pow(2, -25)) -> 0x3c01L,
      (1 + math.pow(2, -11) - math.pow(2, -25)) -> 0x3c00L,
      Math.nextDown(65520.0)                    -> 0x7bffL
    )
  )
    test(s"OpFConvert double to half rounds once for $input") {
      val instructions = Vec[Instruction](
        Instruction.TypeVoid(r(1)),
        Instruction.TypeFloat(r(2), o(OperandKind.LiteralInteger, 64), None),
        Instruction.TypeFloat(r(3), o(OperandKind.LiteralInteger, 16), None),
        Instruction.TypePointer(r(4), o(OperandKind.StorageClass, spv.StorageClass.CrossWorkgroup), r(3)),
        Instruction.TypeFunction(r(5), r(1), Vec(r(4))),
        Instruction.Constant(r(2), r(6), literal(IEEE754.doubleToBinary64(input), 64)),
        Instruction.EntryPoint(o(OperandKind.ExecutionModel, spv.ExecutionModel.Kernel), r(20), entry, Vec.empty),
        Instruction.Function(r(1), r(20), o(OperandKind.FunctionControl, spv.FunctionControl.None), r(5)),
        Instruction.FunctionParameter(r(4), r(21)),
        Instruction.Label(r(22)),
        Instruction.FConvert(r(3), r(23), r(6)),
        Instruction.Store(r(21), r(23), None),
        Instruction.Return(),
        Instruction.FunctionEnd()
      )
      val memory = Memory(2)
      run(instructions, Vec(KernelTask.Argument.Memory(allocation, 0L)), memory)
      assertEquals(LittleEndian.decode(memory.bytes, 0, 2), expected)
    }
}
