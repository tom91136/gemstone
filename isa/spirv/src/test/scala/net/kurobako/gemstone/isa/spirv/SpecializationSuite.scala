package net.kurobako.gemstone.isa.spirv

import fixtures.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.binary.Widths.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.testing.either.*
import net.kurobako.gemstone.vm.*

class SpecializationSuite extends munit.FunSuite {
  private def specialize(image: Vec[Byte], values: (Long, Vec[Byte])*): Either[Fault, Vec[Byte]] =
    Specialization.specialize(
      image,
      Vec.from(values.map((id, bytes) =>
        SpecializationConstant(Binary32(id.refine[Long in 0L ~ 4294967295L].value), bytes)
      ))
    )

  private def image(module: Module): Vec[Byte] = ISA.encode(module).value

  private def bytes(value: Long): Vec[Byte] = LittleEndian.toVec(value, 4)

  private def scalars(numberId: Long = 101L, flagId: Long = 102L, number: Long = 7L, flag: Boolean = false) =
    image(spv"""
      OpDecorate %number SpecId $numberId
      OpDecorate %flag SpecId $flagId
      %int = OpTypeInt 32 1
      %bool = OpTypeBool
      %number = OpSpecConstant %int $number
      %flag = ${if (flag) "OpSpecConstantTrue" else "OpSpecConstantFalse"} %bool
    """)

  test("describe enumerates scalar specialization constants") {
    assertEquals(
      Specialization.describe(scalars()),
      Right(Vec(SpecializationInfo(Binary32(101L), 4), SpecializationInfo(Binary32(102L), 1)))
    )
  }

  test("unsigned specialization IDs are ordered and patched without changing their bits") {
    val image = scalars(numberId = 0xffffffffL, flagId = 0x80000000L)
    assertEquals(
      Specialization.describe(image),
      Right(Vec(SpecializationInfo(Binary32(0x80000000L), 1), SpecializationInfo(Binary32(0xffffffffL), 4)))
    )
    assertEquals(
      specialize(image, 0xffffffffL -> bytes(42L)),
      Right(scalars(numberId = 0xffffffffL, flagId = 0x80000000L, number = 42L))
    )
  }

  test("specialize patches numeric and boolean constants") {
    assertEquals(
      specialize(scalars(), 101L -> bytes(42L), 102L -> Vec[Byte](1)),
      Right(scalars(number = 42L, flag = true))
    )
    assertEquals(specialize(scalars(), 102L -> Vec[Byte](0)), Right(scalars()))
  }

  test("specialize ignores unknown ids and rejects wrong sizes") {
    assertEquals(specialize(scalars(), 999L -> bytes(1L)), Right(scalars()))
    assert(specialize(scalars(), 101L -> Vec[Byte](1, 2, 3)).isLeft)
    assert(specialize(Vec[Byte](1, 2, 3)).isLeft)
  }

  test("specialize patches big-endian modules") {
    val module = image(spv"""
      ; Endianness: Big
      OpDecorate %number SpecId 101
      %int = OpTypeInt 32 1
      %number = OpSpecConstant %int 7
    """)
    assertEquals(module.take(4), Vec[Byte](0x07, 0x23, 0x02, 0x03))
    assertEquals(Specialization.describe(module), Right(Vec(SpecializationInfo(Binary32(101L), 4))))
    val patched = ISA.decode(specialize(module, 101L -> bytes(9L)).value).value
    assertEquals(patched.endianness, Endianness.Big)
    assertEquals(
      patched.instructions.collect { case Instruction.SpecConstant(_, _, value) => value.words.head.raw },
      Vec(9)
    )
  }

  test("malformed negative scalar widths cannot become byte sizes") {
    // word 15 is the OpTypeInt width, which the assembler rejects when negative
    val malformed = scalars().patch(15 * 4, bytes(0xfffffff8L), 4)
    assertEquals(Specialization.describe(malformed), Right(Vec(SpecializationInfo(Binary32(102L), 1))))
    assertEquals(
      specialize(malformed, 101L -> bytes(1L)),
      Left(Fault.InvalidRequest("specialization: specialization constant 101 is not a scalar constant"))
    )
  }

  for {
    specId         <- Seq[Long in 0L ~ 4294967295L](100L, 0x80000000L, 0xffffffffL)
    (kind, widths) <- Seq(
      "OpTypeBool"  -> Seq[Int in (8, 16, 32, 64)](8),
      "OpTypeInt"   -> Seq[Int in (8, 16, 32, 64)](8, 16, 32, 64),
      "OpTypeFloat" -> Seq[Int in (8, 16, 32, 64)](16, 32, 64)
    )
    width <- widths
    byteWidth = width.toMemoryValueByteWidth
  } test(s"specialize updates every SpecId alias for spec $specId, $kind and width ${width.raw}") {
    val boolean                     = kind == "OpTypeBool"
    def literal(bits: Long): String =
      if (width.raw == 64) s"!0x${(bits & 0xffffffffL).toHexString} !0x${(bits >>> 32).toHexString}"
      else s"!0x${bits.toHexString}"
    def module(first: Option[Long], second: Option[Long]): Vec[Byte] = {
      def constant(bits: Option[Long], default: Long): String =
        if (boolean) if (bits.getOrElse(default) == 0L) "OpSpecConstantFalse %t" else "OpSpecConstantTrue %t"
        else s"OpSpecConstant %t ${literal(bits.getOrElse(default))}"
      image(spv"""
        OpDecorate %first SpecId ${specId.raw}
        OpDecorate %second SpecId ${specId.raw}
        OpDecorate %other SpecId 101
        %t = $kind ${if (boolean) "" else width.raw.toString} ${if (kind == "OpTypeInt") "0" else ""}
        %first = ${constant(first, 0L)}
        %second = ${constant(second, 1L)}
        %other = ${constant(None, 0L)}
      """)
    }
    val source = module(None, None)
    assertEquals(
      Specialization.describe(source),
      Right(Vec(
        SpecializationInfo(Binary32(specId), byteWidth.widen[Int > 0]),
        SpecializationInfo(Binary32(101L), byteWidth.widen[Int > 0])
      ).sortBy(_.specId.toLong))
    )
    for (zero <- Seq(false, true)) {
      val replacement = Vec.tabulate(byteWidth.raw)(i => if (zero) 0.toByte else (0xa0 + i).toByte)
      val bits        = LittleEndian.decode(replacement, 0, byteWidth.widen[Int in 0 ~ 8])
      assertEquals(specialize(source, specId.raw -> replacement), Right(module(Some(bits), Some(bits))))
    }
  }

  test("Vulkan boolean specialization consumes all four VkBool32 bytes") {
    for (value <- Seq(0L, 1L, 0x100L, 0x80000000L)) {
      val result = specialize(scalars(), 102L -> bytes(value), 999L -> Vec[Byte](1))
      assertEquals(result, Right(scalars(flag = value != 0L)))
    }
    assert(specialize(scalars(), 102L -> Vec[Byte](1, 0)).isLeft)
  }

  test("specialization updates every scalar sharing a SpecId") {
    def shared(value: Option[Long]): Vec[Byte] = image(spv"""
      OpDecorate %signed SpecId 17
      OpDecorate %unsigned SpecId 17
      OpDecorate %floating SpecId 17
      OpDecorate %flag SpecId 17
      OpDecorate %other SpecId 18
      %int = OpTypeInt 32 1
      %uint = OpTypeInt 32 0
      %float = OpTypeFloat 32
      %bool = OpTypeBool
      %signed = OpSpecConstant %int !0x${value.getOrElse(13L).toHexString}
      %unsigned = OpSpecConstant %uint !0x${value.getOrElse(21L).toHexString}
      %floating = OpSpecConstant %float !0x${value.getOrElse(0x41880000L).toHexString}
      %flag = ${if (value.exists(_ != 0L)) "OpSpecConstantTrue" else "OpSpecConstantFalse"} %bool
      %other = OpSpecConstant %uint 42
    """)
    for (value <- Seq(0L, 0x4000001fL, 0x80000000L))
      assertEquals(specialize(shared(None), 17L -> bytes(value)), Right(shared(Some(value))))
  }

  /** Stores each load-time `OpSpecConstantOp` result through its own pointer parameter; booleans store as 0 or 1. */
  private def evaluate(declarations: String, operations: (String, String)*): Seq[Vec[Byte]] = {
    def stored(resultType: String): String  = if (resultType == "%bool") "%uint" else resultType
    def pointer(resultType: String): String = s"%ptr_${stored(resultType).drop(1)}"
    val indexed                             = operations.zipWithIndex
    val module                              = spv"""
      $openCL
      OpEntryPoint Kernel %main "evaluate"
      %void = OpTypeVoid
      %bool = OpTypeBool
      %uint = OpTypeInt 32 0
      %uint_0 = OpConstant %uint 0
      %uint_1 = OpConstant %uint 1
      $declarations
      ${operations.map((t, _) => stored(t)).distinct.map(t => s"${pointer(t)} = OpTypePointer CrossWorkgroup $t")
        .mkString("\n")}
      ${indexed.map { case ((t, operation), i) => s"%result_$i = OpSpecConstantOp $t $operation" }.mkString("\n")}
      %fn = OpTypeFunction %void ${operations.map((t, _) => pointer(t)).mkString(" ")}
      %main = OpFunction %void None %fn
      ${indexed.map { case ((t, _), i) => s"%out_$i = OpFunctionParameter ${pointer(t)}" }.mkString("\n")}
      %entry = OpLabel
      ${indexed.map { case ((t, _), i) =>
        if (t == "%bool") s"%stored_$i = OpSelect %uint %result_$i %uint_1 %uint_0\nOpStore %out_$i %stored_$i"
        else s"OpStore %out_$i %result_$i"
      }.mkString("\n")}
      OpReturn
      OpFunctionEnd
    """
    val memory  = RegionMemory()
    val regions = operations.map(_ => memory.allocate(16L))
    assertEquals(run(module, "evaluate", memory, Vec.from(regions.map(buffer(_)))), Right(()))
    regions.map(memory.bytes(_))
  }

  private def lanes(bytes: Vec[Byte], width: Int in 0 ~ 8, offsets: Int*): Seq[Long] =
    offsets.map(LittleEndian.decode(bytes, _, width))

  for (width <- Seq[Int in (8, 16, 32, 64)](8, 16, 32, 64))
    test(s"integer specialization arithmetic wraps to width ${width.raw}") {
      val mask  = if (width.raw == 64) -1L else (1L << width.raw) - 1
      val cases = Seq(
        ("IAdd", mask, Some(2L), 1L),
        ("ISub", 0L, Some(1L), mask),
        ("IMul", mask, Some(2L), mask - 1),
        ("SNegate", 1L, None, mask),
        ("Not", mask, None, 0L),
        ("UDiv", mask, Some(2L), mask >>> 1),
        ("UMod", mask, Some(2L), 1L),
        ("SDiv", mask - 4, Some(2L), mask - 1), // -5 / 2 == -2
        ("SRem", mask - 4, Some(2L), mask),     // -5 rem 2 == -1
        ("SMod", mask - 4, Some(2L), 1L),
        ("SMod", 5L, Some(mask - 1), mask), // 5 mod -2 == -1
        ("ShiftRightLogical", mask, Some(1L), mask >>> 1),
        ("ShiftRightArithmetic", mask, Some(1L), mask),
        ("ShiftLeftLogical", 1L, Some(width.raw - 1L), 1L << (width.raw - 1)),
        ("BitwiseOr", 5L, Some(3L), 7L),
        ("BitwiseXor", 5L, Some(3L), 6L),
        ("BitwiseAnd", 5L, Some(3L), 1L)
      )
      val declarations = s"%word = OpTypeInt ${width.raw} 1\n" + cases.zipWithIndex.map {
        case ((_, left, right, _), i) =>
          s"%left_$i = OpSpecConstant %word 0x${left.toHexString}\n" +
            right.fold("")(value => s"%right_$i = OpSpecConstant %word 0x${value.toHexString}")
      }.mkString("\n")
      val results = evaluate(
        declarations,
        cases.zipWithIndex.map { case ((opcode, _, right, _), i) =>
          "%word" -> s"$opcode %left_$i ${right.fold("")(_ => s"%right_$i")}"
        }*
      )
      val byteWidth = width.toMemoryValueByteWidth
      for (((opcode, _, _, expected), result) <- cases.zip(results))
        assertEquals(LittleEndian.decode(result, 0, byteWidth.widen[Int in 0 ~ 8]), expected, opcode)
    }

  test("vector specialization computes each lane with width truncation") {
    val result = evaluate(
      """%uchar = OpTypeInt 8 0
        |%uchar2 = OpTypeVector %uchar 2
        |%uchar_255 = OpConstant %uchar 255
        |%uchar_2 = OpConstant %uchar 2
        |%left = OpSpecConstantComposite %uchar2 %uchar_255 %uchar_2
        |%right = OpSpecConstantComposite %uchar2 %uchar_2 %uchar_2""".stripMargin,
      "%uchar2" -> "IAdd %left %right"
    ).head
    assertEquals(lanes(result, 1, 0, 1), Seq(1L, 4L))
  }

  for (width <- Seq(32, 64)) test(s"specialization comparisons respect opcode signedness at width $width") {
    val cases = Seq(
      "IEqual"            -> 0L,
      "INotEqual"         -> 1L,
      "UGreaterThan"      -> 1L,
      "SGreaterThan"      -> 0L,
      "UGreaterThanEqual" -> 1L,
      "SGreaterThanEqual" -> 0L,
      "ULessThan"         -> 0L,
      "SLessThan"         -> 1L,
      "ULessThanEqual"    -> 0L,
      "SLessThanEqual"    -> 1L
    )
    val results = evaluate(
      s"""%word = OpTypeInt $width 1
         |%minus_one = OpSpecConstant %word -1
         |%one = OpSpecConstant %word 1""".stripMargin,
      cases.map((opcode, _) => "%bool" -> s"$opcode %minus_one %one")*
    )
    assertEquals(results.map(lanes(_, 4, 0).head), cases.map(_._2))
  }

  test("a negative 16-bit specialization constant compares equal to the same bits from a conversion") {
    val results = evaluate(
      """%short = OpTypeInt 16 1
        |%int = OpTypeInt 32 1
        |%minus_one = OpSpecConstant %short -1
        |%wide = OpSpecConstant %int 0xffff
        |%narrow = OpSpecConstantOp %short UConvert %wide""".stripMargin,
      "%bool" -> "IEqual %minus_one %narrow",
      "%bool" -> "ULessThan %narrow %minus_one",
      "%int"  -> "UConvert %minus_one"
    )
    assertEquals(results.map(lanes(_, 4, 0).head), Seq(1L, 0L, 0xffffL))
  }

  test("boolean specialization expressions and vector select") {
    val results = evaluate(
      """%int = OpTypeInt 32 1
        |%int2 = OpTypeVector %int 2
        |%bool2 = OpTypeVector %bool 2
        |%true = OpSpecConstantTrue %bool
        |%false = OpSpecConstantFalse %bool
        |%nine = OpSpecConstant %int 9
        |%seven = OpSpecConstant %int 7
        |%mask = OpSpecConstantComposite %bool2 %true %false
        |%nine_seven = OpSpecConstantComposite %int2 %nine %seven
        |%seven_nine = OpSpecConstantComposite %int2 %seven %nine""".stripMargin,
      "%bool" -> "LogicalEqual %true %false",
      "%bool" -> "LogicalNotEqual %true %false",
      "%bool" -> "LogicalOr %true %false",
      "%bool" -> "LogicalAnd %true %false",
      "%bool" -> "LogicalNot %false",
      "%int2" -> "Select %mask %nine_seven %seven_nine"
    )
    assertEquals(results.take(5).map(lanes(_, 4, 0).head), Seq(0L, 1L, 1L, 0L, 1L))
    assertEquals(lanes(results(5), 4, 0, 4), Seq(9L, 9L))
  }

  test("specialization conversions extend the source sign and convert floating values") {
    val results = evaluate(
      """%int = OpTypeInt 32 1
        |%long = OpTypeInt 64 1
        |%float = OpTypeFloat 32
        |%double = OpTypeFloat 64
        |%int_minus_one = OpSpecConstant %int -1
        |%long_wide = OpSpecConstant %long 0x100000003
        |%float_value = OpSpecConstant %float 1.5
        |%double_value = OpSpecConstant %double -2.5""".stripMargin,
      "%long"   -> "SConvert %int_minus_one",
      "%long"   -> "UConvert %int_minus_one",
      "%int"    -> "SConvert %long_wide",
      "%double" -> "FConvert %float_value",
      "%float"  -> "FConvert %double_value"
    )
    assertEquals(
      results.zip(Seq(8, 8, 4, 8, 4)).map((bytes, width) => lanes(bytes, width.refine[Int in 0 ~ 8].value, 0).head),
      Seq(-1L, 0xffffffffL, 3L, doubleBits(1.5), 0xc0200000L)
    )
  }

  test("specialization extract and insert follow nested composite types") {
    val results = evaluate(
      """%int = OpTypeInt 32 1
        |%int2 = OpTypeVector %int 2
        |%pair = OpTypeStruct %int %int2
        |%nine = OpSpecConstant %int 9
        |%seven = OpSpecConstant %int 7
        |%vector = OpSpecConstantComposite %int2 %nine %seven
        |%composite = OpSpecConstantComposite %pair %seven %vector""".stripMargin,
      "%int2" -> "CompositeExtract %composite 1",
      "%int"  -> "CompositeExtract %composite 1 0",
      "%pair" -> "CompositeInsert %seven %composite 1 0",
      "%int2" -> "VectorShuffle %vector %vector 3 0"
    )
    assertEquals(lanes(results(0), 4, 0, 4), Seq(9L, 7L))
    assertEquals(lanes(results(1), 4, 0), Seq(9L))
    assertEquals(lanes(results(2), 4, 0, 8, 12), Seq(7L, 7L, 7L))
    assertEquals(lanes(results(3), 4, 0, 4), Seq(7L, 9L))
  }

  test("quantisation preserves normal half values and flushes all smaller magnitudes") {
    val cases = Seq(
      0x3f800001L -> 0x3f800000L,
      0x38800000L -> 0x38800000L,
      0x387fffffL -> 0L,
      0xb87fffffL -> 0x80000000L,
      0x80000000L -> 0x80000000L,
      0x7f800000L -> 0x7f800000L,
      0xff800000L -> 0xff800000L,
      0x47800000L -> 0x7f800000L
    )
    val inputs    = cases.map(_._1) :+ 0x7fc00001L
    val constants = inputs.zipWithIndex.map((bits, i) => s"%input_$i = OpSpecConstant %float !0x${bits.toHexString}")
    val results   = evaluate(
      ("%float = OpTypeFloat 32" +: constants).mkString("\n"),
      inputs.indices.map(i => "%float" -> s"QuantizeToF16 %input_$i")*
    )
    val bits = results.map(lanes(_, 4, 0).head)
    assertEquals(bits.init, cases.map(_._2))
    assert(java.lang.Float.intBitsToFloat(bits.last.toInt).isNaN)
  }
}
