package net.kurobako.gemstone.isa.spirv.v1_6

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.spirv.v1_6.execution.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchGeometry.*

class CubeSamplingSuite extends munit.FunSuite {
  private case class Sample(
      name: String,
      dir: Seq[Float],
      expected: Float,
      linear: Boolean = false,
      compare: Boolean = false,
      lod: Int = 0,
      grad: Int = 0,
      gather: Option[Seq[Float]] = None
  )
  private val cases = Seq(
    Sample("gather seam", Seq(1, 0, 1), .625f, gather = Some(Seq(.625f, .125f, .125f, .625f))),
    Sample("gather corner", Seq(1, 1, 1), .625f, gather = Some(Seq(.625f, .125f, .625f, .375f))),
    Sample("gather comparison corner", Seq(1, 1, 1), 1f, compare = true, gather = Some(Seq(1f, 0f, 1f, 0f))),
    Sample("positive X", Seq(1, 0, 0), .125f),
    Sample("negative X", Seq(-1, 0, 0), .25f),
    Sample("positive Y", Seq(0, 1, 0), .375f),
    Sample("negative Y", Seq(0, -1, 0), .5f),
    Sample("positive Z", Seq(0, 0, 1), .625f),
    Sample("negative Z", Seq(0, 0, -1), .75f),
    Sample("linear X Z seam", Seq(1, 0, 1), .375f, linear = true),
    Sample("linear X negative Y seam", Seq(1, -1, 0), .3125f, linear = true),
    Sample("linear corner", Seq(1, 1, 1), .375f, linear = true),
    Sample("comparison seam", Seq(1, 0, 1), .5f, linear = true, compare = true),
    Sample("explicit mip", Seq(1, 0, 0), .875f, lod = 1),
    Sample("projected gradient mip", Seq(1, 0, 0), .875f, grad = 1),
    Sample("radial gradient does not change mip", Seq(1, 0, 0), .125f, grad = 2)
  )
  for {
    sample  <- cases
    arrayed <- Seq(false, true)
    layer   <- if (arrayed && !sample.compare) Seq(-1f, 0f, 1f, 2f) else Seq(0f)
  } test(s"cube ${sample.name} array=$arrayed layer=$layer") {
    def ref(n: Int): Operand              = operand(OperandKind.IdRef, n)
    def lit(n: Int): Operand              = operand(OperandKind.LiteralInteger, n)
    def fc(n: Int, v: Float): Instruction = Instruction.Constant(
      ref(3),
      ref(n),
      operand(OperandKind.LiteralContextDependentNumber, java.lang.Float.floatToRawIntBits(v))
    )
    val module = Module(
      Header(ISA.version, Word(0), Word(60), Word(0)),
      Vec(
        Instruction.EntryPoint(
          operand(OperandKind.ExecutionModel, ExecutionModel.GLCompute),
          ref(40),
          stringOperand("main"),
          Vec(ref(16), ref(17), ref(18))
        ),
        Instruction.Decorate(ref(16), operand(OperandKind.Decoration, Decoration.Binding, 0)),
        Instruction.Decorate(ref(16), operand(OperandKind.Decoration, Decoration.DescriptorSet, 0)),
        Instruction.Decorate(ref(17), operand(OperandKind.Decoration, Decoration.Binding, 1)),
        Instruction.Decorate(ref(17), operand(OperandKind.Decoration, Decoration.DescriptorSet, 0)),
        Instruction.Decorate(ref(18), operand(OperandKind.Decoration, Decoration.Binding, 2)),
        Instruction.Decorate(ref(18), operand(OperandKind.Decoration, Decoration.DescriptorSet, 0)),
        Instruction.MemberDecorate(ref(11), lit(0), operand(OperandKind.Decoration, Decoration.Offset, 0)),
        Instruction.MemberDecorate(ref(11), lit(1), operand(OperandKind.Decoration, Decoration.Offset, 16)),
        Instruction.MemberDecorate(ref(11), lit(2), operand(OperandKind.Decoration, Decoration.Offset, 32)),
        Instruction.TypeVoid(ref(1)),
        Instruction.TypeInt(ref(2), lit(32), lit(0)),
        Instruction.TypeFloat(ref(3), lit(32), None),
        Instruction.TypeVector(ref(4), ref(3), lit(3)),
        Instruction.TypeVector(ref(5), ref(3), lit(4)),
        Instruction.TypeImage(
          ref(6),
          ref(3),
          operand(OperandKind.Dim, Dim.Cube),
          lit(if (sample.compare) 1 else 0),
          lit(if (arrayed) 1 else 0),
          lit(0),
          lit(1),
          operand(OperandKind.ImageFormat, ImageFormat.Unknown),
          None
        ),
        Instruction.TypePointer(ref(7), operand(OperandKind.StorageClass, StorageClass.UniformConstant), ref(6)),
        Instruction.TypeSampler(ref(8)),
        Instruction.TypePointer(ref(9), operand(OperandKind.StorageClass, StorageClass.UniformConstant), ref(8)),
        Instruction.TypeSampledImage(ref(10), ref(6)),
        Instruction.TypeVector(ref(27), ref(2), lit(if (arrayed) 3 else 2)),
        Instruction.TypeStruct(ref(11), Vec(ref(5), ref(27), ref(2))),
        Instruction.TypePointer(ref(12), operand(OperandKind.StorageClass, StorageClass.StorageBuffer), ref(11)),
        Instruction.TypePointer(ref(13), operand(OperandKind.StorageClass, StorageClass.StorageBuffer), ref(5)),
        Instruction.TypePointer(ref(14), operand(OperandKind.StorageClass, StorageClass.StorageBuffer), ref(27)),
        Instruction.TypePointer(ref(48), operand(OperandKind.StorageClass, StorageClass.StorageBuffer), ref(2)),
        Instruction.TypeFunction(ref(15), ref(1), Vec.empty),
        Instruction.Variable(ref(7), ref(16), operand(OperandKind.StorageClass, StorageClass.UniformConstant), None),
        Instruction.Variable(ref(9), ref(17), operand(OperandKind.StorageClass, StorageClass.UniformConstant), None),
        Instruction.Variable(ref(12), ref(18), operand(OperandKind.StorageClass, StorageClass.StorageBuffer), None),
        Instruction.Constant(ref(2), ref(19), lit(0)),
        Instruction.Constant(ref(2), ref(49), lit(1)),
        Instruction.Constant(ref(2), ref(50), lit(2)),
        fc(20, sample.dir(0)),
        fc(21, sample.dir(1)),
        fc(22, sample.dir(2)),
        fc(23, sample.lod.toFloat),
        fc(24, .4f),
        fc(26, if (!arrayed && sample.compare) Float.NaN else layer),
        fc(33, 0),
        fc(34, 1),
        Instruction.ConstantComposite(
          ref(if (arrayed || sample.compare) 5 else 4),
          ref(25),
          if (arrayed || sample.compare) Vec(ref(20), ref(21), ref(22), ref(26)) else Vec(ref(20), ref(21), ref(22))
        ),
        Instruction.ConstantComposite(
          ref(4),
          ref(54),
          if (sample.grad == 2) Vec(ref(34), ref(33), ref(33)) else Vec(ref(33), ref(33), ref(34))
        ),
        Instruction.ConstantComposite(ref(4), ref(55), Vec(ref(33), ref(33), ref(33))),
        Instruction.Function(ref(1), ref(40), operand(OperandKind.FunctionControl, FunctionControl.None), ref(15)),
        Instruction.Label(ref(41)),
        Instruction.Load(ref(6), ref(28), ref(16), None),
        Instruction.Load(ref(8), ref(29), ref(17), None),
        Instruction.SampledImage(ref(10), ref(30), ref(28), ref(29))
      ) ++
        (if (sample.gather.isDefined) Vec(
           if (sample.compare) Instruction.ImageDrefGather(ref(5), ref(31), ref(30), ref(25), ref(24), None)
           else Instruction.ImageGather(ref(5), ref(31), ref(30), ref(25), ref(19), None)
         )
         else if (sample.compare) Vec(
           Instruction.ImageSampleDrefExplicitLod(
             ref(3),
             ref(32),
             ref(30),
             ref(25),
             ref(24),
             operand(OperandKind.ImageOperands, ImageOperands.Lod, 23)
           ),
           Instruction.CompositeConstruct(ref(5), ref(31), Vec(ref(32), ref(33), ref(33), ref(34)))
         )
         else Vec(Instruction.ImageSampleExplicitLod(
           ref(5),
           ref(31),
           ref(30),
           ref(25),
           if (sample.grad == 0) operand(OperandKind.ImageOperands, ImageOperands.Lod, 23)
           else operand(OperandKind.ImageOperands, ImageOperands.Grad, 54, 55)
         ))) ++ Vec(
          Instruction.AccessChain(ref(13), ref(42), ref(18), Vec(ref(19))),
          Instruction.Store(ref(42), ref(31), None),
          Instruction.ImageQuerySizeLod(ref(27), ref(45), ref(28), ref(19)),
          Instruction.AccessChain(ref(14), ref(46), ref(18), Vec(ref(49))),
          Instruction.Store(ref(46), ref(45), None),
          Instruction.ImageQueryLevels(ref(2), ref(47), ref(28)),
          Instruction.AccessChain(ref(48), ref(51), ref(18), Vec(ref(50))),
          Instruction.Store(ref(51), ref(47), None),
          Instruction.Return(),
          Instruction.FunctionEnd()
        ),
      Endianness.Little
    )
    val region                                            = AllocationId(905L); val memory = ArrayMemory(region, 4096)
    def write(at: Int, value: Long, bytes: Int = 4): Unit = assertEquals(
      memory.write(
        region,
        at.toLong.unsafe[Long >= 0],
        LittleEndian.toVec(value, bytes.refine[Int where Between[0, 8]].toOption.get)
      ),
      Right(())
    )
    def fb(v: Float): Long = java.lang.Float.floatToRawIntBits(v).toLong & 0xffffffffL
    val layers             = if (arrayed) 12 else 6
    write(0, 4); write(4, 4); write(8, layers); write(12, 0x80000003L); write(24, layers * 80, 8); write(32, 2);
    write(40, 4); write(44, 4); write(48, 0, 8); write(56, 2); write(60, 2); write(64, layers * 64, 8)
    val sampler = Seq(
      if (sample.linear) 1 else 0,
      if (sample.linear) 1 else 0,
      0,
      0,
      0,
      if (sample.compare) 1 else 0,
      1,
      0,
      0,
      0,
      java.lang.Float.floatToRawIntBits(1f),
      0
    )
    for ((v, i) <- sampler.zipWithIndex) write(128 + i * 4, v.toLong)
    for {
      mip   <- 0 to 1
      face  <- 0 until layers
      pixel <- 0 until (if (mip == 0) 16 else 4)
    } {
      val at = 1024 + (if (mip == 0) face * 64 else layers * 64 + face * 16) + pixel * 4
      write(at, fb((face % 6 + 1) / 8f + (face / 6) * 2f + mip * .75f))
    }
    val task = KernelTask.validated(
      "main",
      Dimensions.One,
      Dimensions.One,
      Vec(
        KernelTask.Argument.Memory(
          region,
          0L,
          Some(72L)
        ),
        KernelTask.Argument.Memory(
          region,
          128L,
          Some(48L)
        ),
        KernelTask.Argument.Memory(
          region,
          192L,
          Some(36L)
        )
      ),
      pointerRelocations =
        Vec(KernelTask.PointerRelocation(
          KernelTask.RelocationSource.Memory(region),
          16L,
          region,
          1024L
        ))
    ).toOption.get
    assertEquals(executeUnchecked(module, task, memory), Right(()))
    val expected = sample.expected + (if (arrayed && !sample.compare && layer >= 1) 2f else 0f)
    val channels = sample.gather.map(_.map(_ + (if (arrayed && !sample.compare && layer >= 1) 2f else 0f)))
      .getOrElse(Seq(expected, 0f, 0f, 1f))
    for ((value, lane) <- channels.zipWithIndex)
      assertEquals(LittleEndian.decode(memory.bytes, 192 + lane * 4, 4), fb(value))
    assertEquals(LittleEndian.decode(memory.bytes, 208, 4), 4L)
    assertEquals(LittleEndian.decode(memory.bytes, 212, 4), 4L)
    if (arrayed) assertEquals(LittleEndian.decode(memory.bytes, 216, 4), 2L)
    assertEquals(LittleEndian.decode(memory.bytes, 224, 4), 2L)
  }
}
