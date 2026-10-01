package net.kurobako.gemstone.isa.spirv.v1_6

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.spirv.v1_6.execution.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchGeometry.*

class TexelBufferSuite extends munit.FunSuite {
  for {
    model     <- Seq(0, 4, 5)
    dimension <- Seq(0, 5)
    format    <- Seq(1, 2, 3, 4, 5, 6, 7, 8, 9, 11, 13, 15, 18, 20) ++ (21 to 39)
    width     <- Seq(3, 20000)
    if dimension == 5 || width <= 16384
    position <- Seq("first", "last", "negative", "past_end")
    wrapped  <- if (dimension == 5) Seq(false, true) else Seq(false)
  } test(
    s"stage $model image dimension $dimension texel format $format, width $width, $position, sampled wrapper $wrapped: writes/atomics alias fetches, reads and size queries"
  ) {
    def ref(n: Int): Operand = operand("IdRef", n)
    def lit(n: Int): Operand = operand("LiteralInteger", n)
    val integer              = format >= 21 && format <= 39
    val signed               = format >= 21 && format <= 29
    val componentType        = if (integer) 2 else 3
    val components           = format match {
      case 9 | 3 | 15 | 20 | 24 | 28 | 29 | 33 | 38 | 39 => 1
      case 7 | 6 | 13 | 18 | 25 | 26 | 27 | 35 | 36 | 37 => 2
      case 8                                             => 3
      case _                                             => 4
    }
    val componentBytes = format match {
      case 4 | 5 | 13 | 15 | 18 | 20 | 23 | 27 | 29 | 32 | 37 | 39 => 1
      case 2 | 7 | 9 | 22 | 26 | 28 | 31 | 36 | 38                 => 2
      case _                                                       => 4
    }
    val texelBytes = if (Set(4, 8, 11, 34).contains(format)) 4 else components * componentBytes
    val coordinate = position match {
      case "last"     => width - 1
      case "negative" => -1
      case "past_end" => width
      case _          => 0
    }
    val valid       = coordinate >= 0 && coordinate < width
    val inputFloats = Seq(-1.0f, 0.25f, 1.0f, 2.0f)
    val inputBits   = if (signed) Seq(-123456L, -1L, 2147483647L, -2147483648L)
    else if (integer) Seq(7L, 42L, 0xffffffffL, 65536L)
    else inputFloats.map(v => java.lang.Float.floatToRawIntBits(v).toLong & 0xffffffffL)
    val constantInstructions: Vec[Instruction] = Vec.from(inputBits.zipWithIndex.map { (v, i) =>
      Instruction.Constant(
        ref(componentType),
        ref(20 + i),
        operand("LiteralContextDependentNumber", v.toInt)
      ): Instruction
    })
    val module = Module(
      Header(ISA.version, Word(0), Word(51), Word(0)),
      Vec(
        Instruction.EntryPoint(
          operand("ExecutionModel", model),
          ref(30),
          stringOperand("main"),
          Vec(ref(16), ref(17), ref(18))
        ),
        Instruction.Decorate(ref(16), operand("Decoration", Decoration.Binding, 0)),
        Instruction.Decorate(ref(16), operand("Decoration", Decoration.DescriptorSet, 0)),
        Instruction.Decorate(ref(17), operand("Decoration", Decoration.Binding, 1)),
        Instruction.Decorate(ref(17), operand("Decoration", Decoration.DescriptorSet, 0)),
        Instruction.Decorate(ref(18), operand("Decoration", Decoration.Binding, 2)),
        Instruction.Decorate(ref(18), operand("Decoration", Decoration.DescriptorSet, 0)),
        Instruction.MemberDecorate(ref(9), lit(0), operand("Decoration", Decoration.Offset, 0)),
        Instruction.MemberDecorate(ref(9), lit(1), operand("Decoration", Decoration.Offset, 16)),
        Instruction.MemberDecorate(ref(9), lit(2), operand("Decoration", Decoration.Offset, 32)),
        Instruction.MemberDecorate(ref(9), lit(3), operand("Decoration", Decoration.Offset, 36)),
        Instruction.TypeVoid(ref(1)),
        Instruction.TypeInt(ref(2), lit(32), lit(if (signed) 1 else 0)),
        Instruction.TypeFloat(ref(3), lit(32), None),
        Instruction.TypeVector(ref(4), ref(componentType), lit(4)),
        Instruction.TypeImage(
          ref(5),
          ref(componentType),
          operand("Dim", dimension),
          lit(0),
          lit(0),
          lit(0),
          lit(2),
          operand("ImageFormat", format),
          None
        ),
        Instruction.TypeImage(
          ref(6),
          ref(componentType),
          operand("Dim", dimension),
          lit(0),
          lit(0),
          lit(0),
          lit(1),
          operand("ImageFormat", ImageFormat.Unknown),
          None
        ),
        Instruction.TypePointer(ref(7), operand("StorageClass", StorageClass.UniformConstant), ref(5)),
        Instruction.TypePointer(ref(8), operand("StorageClass", StorageClass.UniformConstant), ref(6)),
        Instruction.TypeSampledImage(ref(48), ref(6)),
        Instruction.TypePointer(ref(49), operand("StorageClass", StorageClass.UniformConstant), ref(48)),
        Instruction.TypeStruct(ref(9), Vec(ref(4), ref(4), ref(2), ref(2))),
        Instruction.TypePointer(ref(10), operand("StorageClass", StorageClass.StorageBuffer), ref(9)),
        Instruction.TypePointer(ref(11), operand("StorageClass", StorageClass.StorageBuffer), ref(4)),
        Instruction.TypePointer(ref(12), operand("StorageClass", StorageClass.StorageBuffer), ref(2)),
        Instruction.TypePointer(ref(45), operand("StorageClass", StorageClass.Image), ref(2)),
        Instruction.TypeFunction(ref(13), ref(1), Vec.empty),
        Instruction.Variable(ref(7), ref(16), operand("StorageClass", StorageClass.UniformConstant), None),
        Instruction.Variable(
          ref(if (wrapped) 49 else 8),
          ref(17),
          operand("StorageClass", StorageClass.UniformConstant),
          None
        ),
        Instruction.Variable(ref(10), ref(18), operand("StorageClass", StorageClass.StorageBuffer), None),
        Instruction.Constant(ref(2), ref(19), lit(coordinate)),
        Instruction.Constant(ref(2), ref(26), lit(0)),
        Instruction.Constant(ref(2), ref(27), lit(1)),
        Instruction.Constant(ref(2), ref(28), lit(2)),
        Instruction.Constant(ref(2), ref(29), lit(3))
      ) ++ constantInstructions ++ Vec(
        Instruction.ConstantComposite(ref(4), ref(24), Vec(ref(20), ref(21), ref(22), ref(23))),
        Instruction.Function(ref(1), ref(30), operand("FunctionControl", FunctionControl.None), ref(13)),
        Instruction.Label(ref(31)),
        Instruction.Load(ref(5), ref(32), ref(16), None),
        if (wrapped) Instruction.Load(ref(48), ref(50), ref(17), None) else Instruction.Nop(),
        if (wrapped) Instruction.Image(ref(6), ref(33), ref(50))
        else Instruction.Load(ref(6), ref(33), ref(17), None),
        Instruction.ImageWrite(ref(32), ref(19), ref(24), None),
        if (format == 33 || format == 24) Instruction.ImageTexelPointer(ref(45), ref(46), ref(16), ref(19), ref(26))
        else Instruction.Nop(),
        if (format == 33 || format == 24) Instruction.AtomicIAdd(ref(2), ref(47), ref(46), ref(27), ref(26), ref(27))
        else Instruction.Nop(),
        Instruction.ImageRead(ref(4), ref(34), ref(32), ref(19), None),
        Instruction.ImageFetch(
          ref(4),
          ref(35),
          ref(33),
          ref(19),
          if (dimension == 5) None else Some(operand("ImageOperands", ImageOperands.Lod, 26))
        ),
        Instruction.ImageQuerySize(ref(2), ref(36), ref(32)),
        if (dimension == 5) Instruction.ImageQuerySize(ref(2), ref(37), ref(33))
        else Instruction.ImageQuerySizeLod(ref(2), ref(37), ref(33), ref(26)),
        Instruction.AccessChain(ref(11), ref(40), ref(18), Vec(ref(26))),
        Instruction.AccessChain(ref(11), ref(41), ref(18), Vec(ref(27))),
        Instruction.AccessChain(ref(12), ref(42), ref(18), Vec(ref(28))),
        Instruction.AccessChain(ref(12), ref(43), ref(18), Vec(ref(29))),
        Instruction.Store(ref(40), ref(34), None),
        Instruction.Store(ref(41), ref(35), None),
        Instruction.Store(ref(42), ref(36), None),
        Instruction.Store(ref(43), ref(37), None),
        Instruction.Return(),
        Instruction.FunctionEnd()
      ),
      Endianness.Little
    )
    val region                                   = AllocationId(701L)
    val payload                                  = 16 + width * texelBytes
    val outputOffset                             = 8 + payload + 8
    val memory                                   = ArrayMemory(region, outputOffset + 40)
    def writeWord(offset: Int, word: Long): Unit = assertEquals(
      memory.write(region, offset.toLong.unsafe[Long >= 0], LittleEndian.toVec(word, 4)),
      Right(())
    )
    writeWord(0, 0xfeedL)
    writeWord(8, width.toLong); writeWord(12, 1L); writeWord(16, 1L); writeWord(20, format.toLong)
    writeWord(8 + payload, 0xbeefL)
    val task = KernelTask.validated(
      "main",
      Dimensions.One,
      Dimensions.One,
      Vec(
        KernelTask.Argument.Memory(
          region,
          8L,
          Some(payload.toLong.unsafe[Long > 0])
        ),
        KernelTask.Argument.Memory(
          region,
          8L,
          Some(payload.toLong.unsafe[Long > 0])
        ),
        KernelTask.Argument.Memory(
          region,
          outputOffset.toLong.unsafe[Long >= 0],
          Some(40L)
        )
      )
    ).toOption.get
    assertEquals(executeUnchecked(module, task, memory), Right(()))
    val expected = (0 until 4).map { lane =>
      if (!valid) 0L
      else if (integer) {
        if (lane < components) {
          val width = if (format == 34) { if (lane == 3) 2 else 10 }
          else componentBytes * 8
          val low  = if (signed) -(1L << (width - 1)) else 0L
          val high = if (signed) (1L << (width - 1)) - 1 else (1L << width) - 1
          (Math.max(low, Math.min(high, inputBits(lane))) + (if (format == 33 || format == 24) 1L else 0L)) &
            0xffffffffL
        } else if (lane == 3) 1L else 0L
      } else {
        val v = if (lane >= components) { if (lane == 3) 1.0f else 0.0f }
        else if (Set(5, 18, 20).contains(format)) Seq(-1.0f, 32.0f / 127.0f, 1.0f, 1.0f)(lane)
        else if (Set(4, 13, 15).contains(format)) Seq(0.0f, 64.0f / 255.0f, 1.0f, 1.0f)(lane)
        else if (format == 11) Seq(0.0f, 256.0f / 1023.0f, 1.0f, 1.0f)(lane)
        else if (format == 8) Seq(0.0f, 0.25f, 1.0f, 1.0f)(lane)
        else inputFloats(lane)
        java.lang.Float.floatToRawIntBits(v).toLong & 0xffffffffL
      }
    }
    for {
      result <- 0 until 2
      lane   <- 0 until 4
    }
      assertEquals(
        LittleEndian.decode(memory.bytes, outputOffset + result * 16 + lane * 4, 4),
        expected(lane)
      )
    for (offset <- Seq(32, 36))
      assertEquals(LittleEndian.decode(memory.bytes, outputOffset + offset, 4), width.toLong)
    assertEquals(LittleEndian.decode(memory.bytes, 0, 4), 0xfeedL)
    assertEquals(LittleEndian.decode(memory.bytes, 8 + payload, 4), 0xbeefL)
    val texels = memory.bytes.slice(24, 8 + payload)
    if (!valid) assert(texels.forall(_ == 0))
    else {
      assert(texels.take(coordinate * texelBytes).forall(_ == 0))
      assert(texels.drop((coordinate + 1) * texelBytes).forall(_ == 0))
    }
  }
}
