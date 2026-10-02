package net.kurobako.gemstone.isa.spirv.v1_6

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.spirv.v1_6.execution.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchGeometry.*

class InputAttachmentSuite extends munit.FunSuite {
  for {
    sampled   <- Seq(false, true)
    format    <- Seq(1, 3, 4, 6, 21, 23, 30, 32, 33, 35, 1000, 1001, 1002, 1003, 1004)
    extension <- if (Set(21, 23, 30, 32, 33, 35, 1003, 1004).contains(format))
      Seq(0, if (format == 21 || format == 23) 0x1000 else 0x2000)
    else Seq(0)
    (samples, sample) <- Seq((1, 0), (4, 0), (4, 3), (4, -1), (4, 4))
    if !sampled || samples == 4
    arrayed <- if (sampled) Seq(false, true) else Seq(false)
    layer   <- if (arrayed) Seq(0, 1, -1, 2) else Seq(0)
    (x, y)  <- Seq((0, 0), (2, 1), (-1, 0), (3, 0), (0, 2))
  } test(
    s"sampled=$sampled attachment format=$format extension=$extension samples=$samples sample=$sample pixel=($x,$y) arrayed=$arrayed layer=$layer"
  ) {
    def ref(n: Int): Operand = operand(OperandKind.IdRef, n)
    def lit(n: Int): Operand = operand(OperandKind.LiteralInteger, n)
    val integer              = Set(21, 23, 30, 32, 33, 35, 1003, 1004).contains(format)
    val component            = if (integer) 2 else 3
    val module               = Module(
      Header(ISA.version, Word(0), Word(46), Word(0)),
      Vec(
        Instruction.EntryPoint(
          operand(OperandKind.ExecutionModel, ExecutionModel.Fragment),
          ref(30),
          stringOperand("main"),
          Vec(ref(16), ref(17), ref(18))
        ),
        Instruction.Decorate(ref(16), operand(OperandKind.Decoration, Decoration.Binding, 0)),
        Instruction.Decorate(ref(16), operand(OperandKind.Decoration, Decoration.DescriptorSet, 0)),
        Instruction.Decorate(ref(16), operand(OperandKind.Decoration, Decoration.InputAttachmentIndex, 0)),
        Instruction.Decorate(ref(17), operand(OperandKind.Decoration, Decoration.Binding, 1)),
        Instruction.Decorate(ref(17), operand(OperandKind.Decoration, Decoration.DescriptorSet, 0)),
        Instruction.Decorate(ref(18), operand(OperandKind.Decoration, Decoration.BuiltIn, 15)),
        Instruction.MemberDecorate(ref(9), lit(0), operand(OperandKind.Decoration, Decoration.Offset, 0)),
        Instruction.MemberDecorate(ref(9), lit(1), operand(OperandKind.Decoration, Decoration.Offset, 16)),
        Instruction.MemberDecorate(ref(9), lit(2), operand(OperandKind.Decoration, Decoration.Offset, 24)),
        Instruction.TypeVoid(ref(1)),
        Instruction.TypeInt(ref(2), lit(32), lit(if ((format == 21 || format == 23) && extension == 0) 1 else 0)),
        Instruction.TypeFloat(ref(3), lit(32), None),
        Instruction.TypeVector(ref(4), ref(component), lit(4)),
        Instruction.TypeImage(
          ref(5),
          ref(component),
          operand(OperandKind.Dim, if (sampled) 1 else 6),
          lit(0),
          lit(if (arrayed) 1 else 0),
          lit(if (samples == 1) 0 else 1),
          lit(if (sampled) 1 else 2),
          operand(OperandKind.ImageFormat, ImageFormat.Unknown),
          None
        ),
        Instruction.TypeVector(ref(6), ref(2), lit(if (arrayed) 3 else 2)),
        Instruction.TypePointer(ref(7), operand(OperandKind.StorageClass, StorageClass.UniformConstant), ref(5)),
        Instruction.TypeVector(ref(8), ref(3), lit(4)),
        Instruction.TypeStruct(ref(9), Vec(ref(4), ref(2), ref(6))),
        Instruction.TypePointer(ref(10), operand(OperandKind.StorageClass, StorageClass.StorageBuffer), ref(9)),
        Instruction.TypePointer(ref(11), operand(OperandKind.StorageClass, StorageClass.StorageBuffer), ref(4)),
        Instruction.TypePointer(ref(12), operand(OperandKind.StorageClass, StorageClass.Input), ref(8)),
        Instruction.TypePointer(ref(14), operand(OperandKind.StorageClass, StorageClass.StorageBuffer), ref(2)),
        Instruction.TypePointer(ref(15), operand(OperandKind.StorageClass, StorageClass.StorageBuffer), ref(6)),
        Instruction.TypeFunction(ref(13), ref(1), Vec.empty),
        Instruction.Variable(ref(7), ref(16), operand(OperandKind.StorageClass, StorageClass.UniformConstant), None),
        Instruction.Variable(ref(10), ref(17), operand(OperandKind.StorageClass, StorageClass.StorageBuffer), None),
        Instruction.Variable(ref(12), ref(18), operand(OperandKind.StorageClass, StorageClass.Input), None),
        Instruction.Constant(ref(2), ref(25), lit(1)),
        Instruction.Constant(ref(2), ref(26), lit(2)),
        Instruction.ConstantComposite(
          ref(6),
          ref(27),
          if (arrayed) Vec(ref(25), ref(26), ref(26)) else Vec(ref(25), ref(26))
        ),
        Instruction.Constant(ref(2), ref(20), lit(0)),
        Instruction.Constant(ref(2), ref(23), lit(if (sampled) x else 0)),
        Instruction.Constant(ref(2), ref(24), lit(if (sampled) y else 0)),
        Instruction.Constant(ref(2), ref(28), lit(layer)),
        Instruction.ConstantComposite(
          ref(6),
          ref(21),
          if (arrayed) Vec(ref(23), ref(24), ref(28)) else Vec(ref(23), ref(24))
        ),
        Instruction.Constant(ref(2), ref(22), lit(sample)),
        Instruction.Function(ref(1), ref(30), operand(OperandKind.FunctionControl, FunctionControl.None), ref(13)),
        Instruction.Label(ref(31)),
        Instruction.Load(ref(5), ref(32), ref(16), None),
        if (sampled)
          Instruction.ImageFetch(
            ref(4),
            ref(33),
            ref(32),
            ref(21),
            Some(operand(OperandKind.ImageOperands, ImageOperands.Sample | extension, 22))
          )
        else Instruction.ImageRead(
          ref(4),
          ref(33),
          ref(32),
          ref(21),
          if (samples == 1) { if (extension == 0) None else Some(operand(OperandKind.ImageOperands, extension)) }
          else Some(operand(OperandKind.ImageOperands, ImageOperands.Sample | extension, 22))
        ),
        Instruction.AccessChain(ref(11), ref(34), ref(17), Vec(ref(20))),
        Instruction.Store(ref(34), ref(33), None),
        if (sampled) Instruction.ImageQuerySamples(ref(2), ref(36), ref(32))
        else Instruction.CopyObject(ref(2), ref(36), ref(25)),
        if (sampled) Instruction.ImageQuerySize(ref(6), ref(37), ref(32))
        else Instruction.CopyObject(ref(6), ref(37), ref(27)),
        Instruction.AccessChain(ref(14), ref(38), ref(17), Vec(ref(25))),
        Instruction.AccessChain(ref(15), ref(39), ref(17), Vec(ref(26))),
        Instruction.Store(ref(38), ref(36), None),
        Instruction.Store(ref(39), ref(37), None),
        Instruction.Return(),
        Instruction.FunctionEnd()
      ),
      Endianness.Little
    )
    val region                                                = AllocationId(802L)
    val memory                                                = ArrayMemory(region, 1504)
    def write(offset: Int, value: Long, bytes: Int = 4): Unit = assertEquals(
      memory.write(
        region,
        offset.toLong.unsafe[Long >= 0],
        LittleEndian.toVec(value, bytes.refine[Int where Between[0, 8]].toOption.get)
      ),
      Right(())
    )
    def floatBits(value: Float): Long = java.lang.Float.floatToRawIntBits(value).toLong & 0xffffffffL
    val components = format match { case 3 | 33 | 1000 | 1002 | 1003 | 1004 => 1; case 6 | 35 => 2; case _ => 4 }
    val texelBytes =
      format match {
        case 1 | 21 | 30 => 16; case 6 | 35 | 1002 | 1004 => 8; case 1000 => 2; case 1003 => 1; case _ => 4
      }
    write(0, 0xfeedL); write(1500, 0xbeefL)
    write(8, 3); write(12, 2); write(16, if (arrayed) 2 else 1); write(20, 0x80000000L | format)
    write(32, 6L * samples * texelBytes * (if (arrayed) 2 else 1), 8); write(40, 1);
    write(44, if (samples == 1) 0 else samples)
    write(48, 3); write(52, 2); write(56, 0, 8)
    for {
      imageLayer <- 0 until (if (arrayed) 2 else 1)
      pixel      <- 0 until 6
      slot       <- 0 until samples
    } {
      val offset = 128 + ((imageLayer * 6 + pixel) * samples + slot) * texelBytes
      val base   = imageLayer * 96 + pixel * 16 + slot * 4 + 1
      format match {
        case 1 | 3 | 6 | 1002 =>
          for (lane <- 0 until components) write(offset + lane * 4, floatBits((base + lane).toFloat / 8))
        case 21 | 30 | 33 | 35 => for (lane <- 0 until components) write(offset + lane * 4, 0x80000000L + base + lane)
        case 1000              => write(offset, base * 257L, 2)
        case 1003              => write(offset, base, 1)
        case 1004              => write(offset, floatBits(.75f)); write(offset + 4, base, 1); write(offset + 5, 0xa5, 1)
        case _ => write(offset, (0 until 4).foldLeft(0L)((word, lane) => word | ((base + lane).toLong << (lane * 8))))
      }
    }
    write(1264, floatBits(x + .5f)); write(1268, floatBits(y + .5f)); write(1272, floatBits(.5f));
    write(1276, floatBits(1.0f))
    val task = KernelTask.validated(
      "main",
      Dimensions.One,
      Dimensions.One,
      Vec(
        KernelTask.Argument.Memory(
          region,
          8L,
          Some(56L)
        ),
        KernelTask.Argument.Memory(
          region,
          1200L,
          Some(40L)
        ),
        KernelTask.Argument.Memory(
          region,
          1264L,
          Some(16L)
        )
      ),
      pointerRelocations =
        Vec(KernelTask.PointerRelocation(
          KernelTask.RelocationSource.Memory(region),
          24L,
          region,
          128L
        ))
    ).toOption.get
    assertEquals(executeUnchecked(module, task, memory), Right(()))
    val valid = x >= 0 && x < 3 && y >= 0 && y < 2 && sample >= 0 && sample < samples && layer >= 0 &&
      layer <
      (if (arrayed) 2 else 1)
    val base = layer * 96 + (y * 3 + x) * 16 + sample * 4 + 1
    for (lane <- 0 until 4) {
      val value = if (!valid) 0L
      else if (integer) {
        if (format == 1003 || format == 1004) { if (lane == 0) base.toLong else if (lane == 3) 1L else 0L }
        else if (format == 23) (base + lane).toByte.toLong & 0xffffffffL
        else if (format != 32) { if (lane < components) 0x80000000L + base + lane else if (lane == 3) 1L else 0L }
        else (base + lane).toLong
      } else {
        val v = if (lane >= components) { if (lane == 3) 1f else 0f }
        else if (format == 1000) base * 257f / 65535f
        else if (format == 4 || format == 1001) (base + (if (format == 1001 && lane < 3) 2 - lane else lane)).toFloat /
          255f
        else (base + lane).toFloat / 8f
        floatBits(v)
      }
      assertEquals(LittleEndian.decode(memory.bytes, 1200 + lane * 4, 4), value)
    }
    assertEquals(LittleEndian.decode(memory.bytes, 1216, 4), if (sampled) 4L else 1L)
    assertEquals(LittleEndian.decode(memory.bytes, 1224, 4), if (sampled) 3L else 1L)
    assertEquals(LittleEndian.decode(memory.bytes, 1228, 4), 2L)
    assertEquals(LittleEndian.decode(memory.bytes, 0, 4), 0xfeedL)
    if (arrayed) assertEquals(LittleEndian.decode(memory.bytes, 1232, 4), 2L)
    assertEquals(LittleEndian.decode(memory.bytes, 1500, 4), 0xbeefL)
  }
}
