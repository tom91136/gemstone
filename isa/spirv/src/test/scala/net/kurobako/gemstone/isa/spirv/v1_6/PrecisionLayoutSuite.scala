package net.kurobako.gemstone.isa.spirv.v1_6

import net.kurobako.gemstone.Col
import net.kurobako.gemstone.binary.{ByteRange, IEEE754, LittleEndian}
import net.kurobako.gemstone.execution.*

final class PrecisionLayoutSuite extends munit.FunSuite {
  private def o(kind: String, values: Int*): Operand = Operand(kind, Col.from(values.map(Word(_))))
  private def r(value: Int)                          = o("IdResult", value)
  private def literal(value: Long, width: Int)       = Operand(
    "LiteralContextDependentNumber",
    Col.from(LittleEndian.toArray(value, width / 8).grouped(4).map(x =>
      Word(LittleEndian.decode(x, 0, x.length).toInt)
    ))
  )
  private val entry      = Operand("LiteralString", Col(Word(0x6b)))
  private val allocation = AllocationId.fromLong(1).toOption.get
  private class Memory(size: Int) extends ExecutionMemory {
    val bytes                                             = Array.ofDim[Byte](size)
    def read(id: AllocationId, offset: Long, length: Int) = {
      assert(id == allocation && ByteRange.contains(size, offset, length))
      Right(Col.from(bytes.slice(offset.toInt, offset.toInt + length)))
    }
    def write(id: AllocationId, offset: Long, value: Col[Byte]) = {
      assert(id == allocation && ByteRange.contains(size, offset, value.size))
      val _ = value.copyToArray(bytes, offset.toInt, value.size); Right(())
    }
    def recordPointerRelocation(value: PointerRelocation): Unit = ()
  }
  private def run(instructions: Col[Instruction], args: Col[KernelArgument], memory: Memory): Unit = {
    val profile = Col.from(Seq(4, 6, 7, 9, 10, 11).map(value => Instruction.Capability(o("Capability", value)))) :+
      Instruction.MemoryModel(o("AddressingModel", 2), o("MemoryModel", 2))
    val module   = Module(Header(ISA.version, Word(0), Word(64), Word(0)), profile ++ instructions, Endianness.Little)
    val dispatch =
      KernelDispatch.create(ProgramId.fromLong(1).toOption.get, "k", Dimensions.One, Dimensions.One, args).toOption.get
    assertEquals(
      Interpreter.run(
        Interpreter.prepare(module, Environment.OpenCLCompute).fold(error => fail(error.message), identity),
        dispatch,
        memory
      ),
      Right(())
    )
  }
  for {
    (width, count) <- Seq(32 -> 4, 32 -> 8, 32 -> 16, 64 -> 3, 64 -> 4, 64 -> 8)
    byValue        <- Seq(false, true)
  }
    test(s"natural OpenCL vector layout width=$width lanes=$count byval=$byValue preserves struct array stride") {
      val align    = (width / 8) * (if (count == 3) 4 else count)
      val size     = align * 2
      val expected = Array.ofDim[Byte](size * 2)
      for (base <- Seq(0, size)) {
        LittleEndian.write(IEEE754.fromDouble(2.25, width), expected, base, width / 8)
        for (lane <- 0 until count)
          LittleEndian.write(IEEE754.fromDouble(1.5, width), expected, base + align + lane * (width / 8), width / 8)
      }
      val definitions = Col[Instruction](
        Instruction.TypeVoid(r(1)),
        Instruction.TypeFloat(r(2), o("LiteralInteger", width), None),
        Instruction.TypeVector(r(3), r(2), o("LiteralInteger", count)),
        Instruction.TypeStruct(r(4), Col(r(2), r(3))),
        Instruction.TypePointer(r(5), o("StorageClass", 5), r(4)),
        Instruction.TypePointer(r(6), o("StorageClass", 7), r(4)),
        Instruction.TypeInt(r(7), o("LiteralInteger", 32), o("LiteralInteger", 0)),
        Instruction.Constant(r(7), r(8), literal(1, 32)),
        Instruction.Constant(r(2), r(9), literal(IEEE754.fromDouble(1.5, width), width)),
        Instruction.Constant(r(2), r(10), literal(IEEE754.fromDouble(2.25, width), width)),
        Instruction.ConstantComposite(r(3), r(11), Col.fill(count)(r(9))),
        Instruction.ConstantComposite(r(4), r(12), Col(r(10), r(11))),
        Instruction.TypeFunction(r(13), r(1), if (byValue) Col(r(6), r(5)) else Col(r(5))),
        Instruction.EntryPoint(o("ExecutionModel", 6), r(20), entry, Col.empty)
      ) ++
        (if (byValue) Col[Instruction](Instruction.Decorate(r(21), o("Decoration", 38, 2))) else Col.empty[Instruction])
      val parameters = if (byValue)
        Col[Instruction](Instruction.FunctionParameter(r(6), r(21)), Instruction.FunctionParameter(r(5), r(22)))
      else Col[Instruction](Instruction.FunctionParameter(r(5), r(22)))
      val instructions = definitions ++
        Col[Instruction](Instruction.Function(r(1), r(20), o("FunctionControl", 0), r(13))) ++ parameters ++
        Col[Instruction](Instruction.Label(r(23))) ++
        (if (byValue) Col[Instruction](Instruction.Load(r(4), r(24), r(21), None)) else Col.empty[Instruction]) ++
        Col[Instruction](
          Instruction.Store(r(22), if (byValue) r(24) else r(12), None),
          Instruction.PtrAccessChain(r(5), r(25), r(22), r(8), Col.empty),
          Instruction.Store(r(25), if (byValue) r(24) else r(12), None),
          Instruction.Return(),
          Instruction.FunctionEnd()
        )
      val args =
        (if (byValue) Col[KernelArgument](KernelArgument.Scalar(Col.from(expected.take(size))))
         else Col.empty[KernelArgument]) :+ KernelArgument.Allocation(allocation, 0)
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
      val instructions = Col[Instruction](
        Instruction.TypeVoid(r(1)),
        Instruction.TypeFloat(r(2), o("LiteralInteger", 64), None),
        Instruction.TypeFloat(r(3), o("LiteralInteger", 16), None),
        Instruction.TypePointer(r(4), o("StorageClass", 5), r(3)),
        Instruction.TypeFunction(r(5), r(1), Col(r(4))),
        Instruction.Constant(r(2), r(6), literal(IEEE754.doubleToBinary64(input), 64)),
        Instruction.EntryPoint(o("ExecutionModel", 6), r(20), entry, Col.empty),
        Instruction.Function(r(1), r(20), o("FunctionControl", 0), r(5)),
        Instruction.FunctionParameter(r(4), r(21)),
        Instruction.Label(r(22)),
        Instruction.FConvert(r(3), r(23), r(6)),
        Instruction.Store(r(21), r(23), None),
        Instruction.Return(),
        Instruction.FunctionEnd()
      )
      val memory = Memory(2)
      run(instructions, Col(KernelArgument.Allocation(allocation, 0)), memory)
      assertEquals(LittleEndian.decode(memory.bytes, 0, 2), expected)
    }
}
