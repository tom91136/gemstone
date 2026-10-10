package net.kurobako.gemstone.isa.spirv

import fixtures.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.testing.either.*
import net.kurobako.gemstone.vm.*

class ConversionSuite extends munit.FunSuite {
  private val roundings = Seq("RTE", "RTZ", "RTP", "RTN")

  /** Kernel storing `%converted = opcode %result_type %input` for a by-value `%input`. */
  private def converter(opcode: String, source: String, result: String, lanes: Int = 1, decorations: String = "")
      : String = {
    def types(name: String, scalar: String) =
      if (lanes == 1) s"%${name}_type = $scalar"
      else s"%${name}_scalar = $scalar\n%${name}_type = OpTypeVector %${name}_scalar $lanes"
    s"""
      $openCL
      OpEntryPoint Kernel %main "convert"
      $decorations
      %void = OpTypeVoid
      ${types("source", source)}
      ${types("result", result)}
      %out_ptr = OpTypePointer CrossWorkgroup %result_type
      %fn = OpTypeFunction %void %out_ptr %source_type
      %main = OpFunction %void None %fn
      %out = OpFunctionParameter %out_ptr
      %input = OpFunctionParameter %source_type
      %entry = OpLabel
      %converted = $opcode %result_type %input
      OpStore %out %converted
      OpReturn
      OpFunctionEnd
    """
  }

  private def convert(source: String, input: Vec[Byte], outputBytes: Int): Either[Fault, Vec[Byte]] = {
    val memory = RegionMemory()
    val out    = memory.allocate(outputBytes.toLong.refine[Long > 0].value)
    run(assemble(source), "convert", memory, Vec(buffer(out), scalarBytes(input))).map(_ => memory.bytes(out))
  }

  private def chain(width: Int): String = s"""
    $openCL
    OpEntryPoint Kernel %main "signed_to_float"
    %void = OpTypeVoid
    %uint = OpTypeInt $width 0
    %uint2 = OpTypeVector %uint 2
    %float = OpTypeFloat $width
    %float2 = OpTypeVector %float 2
    %out_ptr = OpTypePointer CrossWorkgroup %float2
    %fn = OpTypeFunction %void %out_ptr %uint2 %float2
    %main = OpFunction %void None %fn
    %out = OpFunctionParameter %out_ptr
    %input = OpFunctionParameter %uint2
    %rhs = OpFunctionParameter %float2
    %entry = OpLabel
    %converted = OpConvertSToF %float2 %input
    %added = OpFAdd %float2 %converted %rhs
    %subtracted = OpFSub %float2 %added %rhs
    %multiplied = OpFMul %float2 %subtracted %rhs
    %divided = OpFDiv %float2 %multiplied %rhs
    %integer = OpConvertFToS %uint2 %divided
    %result = OpConvertSToF %float2 %integer
    OpStore %out %result
    OpReturn
    OpFunctionEnd
  """

  for (
    (width, input, rhs, expected) <- Seq(
      (32, Seq(0xfffffffeL, 3L), Seq(floatBits(0.5f), floatBits(1.5f)), Seq(floatBits(-2f), floatBits(3f))),
      (16, Seq(0L, 3L), Seq(0x0001L, 0x3e00L), Seq(0L, 0x4200L))
    )
  ) test(s"signed integer vectors survive a float$width arithmetic round trip") {
    val memory    = RegionMemory()
    val out       = memory.allocate((width / 4).toLong.refine[Long > 0].value)
    val arguments = Vec(buffer(out), scalarBytes(littleEndian(input, width)), scalarBytes(littleEndian(rhs, width)))
    assertEquals(run(assemble(chain(width)), "signed_to_float", memory, arguments), Right(()))
    assertEquals(memory.bytes(out), littleEndian(expected, width))
  }

  test("floating arithmetic and conversions reject mistyped results") {
    val memory = RegionMemory()
    val out    = memory.allocate(8L)
    val launch = task("signed_to_float", Vec(buffer(out), scalar(0L, 8), scalar(0L, 8)))
    for (
      (original, replacement, message) <- Seq(
        ("OpConvertSToF %float2 %input", "OpConvertSToF %uint %input", "OpConvertSToF result type %4"),
        ("OpFAdd %float2", "OpFAdd %uint", "OpFAdd result type %4"),
        ("OpFSub %float2", "OpFSub %uint", "OpFSub result type %4"),
        ("OpFMul %float2", "OpFMul %uint", "OpFMul result type %4"),
        ("OpFDiv %float2", "OpFDiv %uint", "OpFDiv result type %4"),
        ("OpConvertFToS %uint2", "OpConvertFToS %float2", "OpConvertFToS result type %7")
      )
    ) {
      val kind = if (original.startsWith("OpConvertFToS")) "an integer" else "a floating-point"
      assertEquals(
        loadUnchecked(assemble(chain(32).replace(original, replacement))).flatMap(execute(_, launch, memory)),
        Left(Fault.InvalidRequest(s"$message is not $kind scalar or vector")),
        replacement
      )
    }
  }

  test("OpConvertFToU converts doubles without signed narrowing") {
    val source                  = converter("OpConvertFToU", "OpTypeFloat 64", "OpTypeInt 64 0")
    def unsigned(value: Double) = convert(source, LittleEndian.toVec(doubleBits(value), 8), 8)
    assertEquals(unsigned(255.75), Right(LittleEndian.toVec(255L, 8)))
    assertEquals(unsigned(Math.scalb(1.0, 63)), Right(LittleEndian.toVec(Long.MinValue, 8)))
    assertEquals(unsigned(-0.75), Right(LittleEndian.toVec(0L, 8)))
    unsigned(-1.0) match {
      case Left(Fault.InvalidRequest(message)) =>
        assert(message.contains("negative values are outside the unsigned range"), message)
      case result => fail(s"expected unsigned conversion range failure, found $result")
    }
    val memory    = RegionMemory()
    val out       = memory.allocate(8L)
    val malformed = source.replace("OpConvertFToU %result_type", "OpConvertFToU %source_type")
    assertEquals(
      loadUnchecked(assemble(malformed)).flatMap(execute(_, task("convert", Vec(buffer(out), scalar(0L, 8))), memory)),
      Left(Fault.InvalidRequest("OpConvertFToU result type %4 is not an integer scalar or vector"))
    )
  }

  for ((sourceWidth, resultWidth, lanes) <- pairwise(Seq(16, 32, 64), Seq(8, 16, 32, 64), Seq(1, 4)))
    test(s"OpConvertFToU rounds and saturates float$sourceWidth to uint$resultWidth, $lanes lanes") {
      def check(rounding: String, saturate: Boolean, input: Seq[Double], expected: Seq[Long]): Unit = {
        val decorations = s"OpDecorate %converted FPRoundingMode $rounding" +:
          Option.when(saturate)("OpDecorate %converted SaturatedConversion").toSeq
        val source = converter(
          "OpConvertFToU",
          s"OpTypeFloat $sourceWidth",
          s"OpTypeInt $resultWidth 0",
          lanes,
          decorations.mkString("\n")
        )
        assertEquals(
          convert(source, littleEndian(input.map(floatingBits(_, sourceWidth)), sourceWidth), lanes * resultWidth / 8),
          Right(littleEndian(expected, resultWidth)),
          s"$rounding saturate $saturate $input"
        )
      }
      val samples  = Seq(2.3, 3.8, 2.5, 3.5)
      val expected = Seq(Seq(2L, 4L, 2L, 4L), Seq(2L, 3L, 2L, 3L), Seq(3L, 4L, 3L, 4L), Seq(2L, 3L, 2L, 3L))
      for {
        mode     <- roundings.indices
        start    <- if (lanes == 1) samples.indices else Seq(0)
        saturate <- Seq(false, true)
      } check(
        roundings(mode),
        saturate,
        Seq.tabulate(lanes)(lane => samples((start + lane) % samples.size)),
        Seq.tabulate(lanes)(lane => expected(mode)((start + lane) % samples.size))
      )
      for (mode <- roundings.take(3)) check(mode, saturate = false, Seq.fill(lanes)(-0.5), Seq.fill(lanes)(0L))
      val maximum    = ((BigInt(1) << resultWidth) - 1).toLong
      val boundaries = Seq(Double.NaN -> 0L, Double.NegativeInfinity -> 0L, Double.PositiveInfinity -> maximum)
      for ((value, saturated) <- boundaries)
        check("RTE", saturate = true, Seq.fill(lanes)(value), Seq.fill(lanes)(saturated))
      if (resultWidth == 64 && sourceWidth != 16)
        check("RTE", saturate = true, Seq.fill(lanes)(Math.scalb(1.0, 63)), Seq.fill(lanes)(Long.MinValue))
    }

  test("saturated OpConvertFToS maps NaN to zero") {
    val saturated = "OpDecorate %converted SaturatedConversion"
    val source    = converter("OpConvertFToS", "OpTypeFloat 32", "OpTypeInt 32 1", decorations = saturated)
    assertEquals(convert(source, LittleEndian.toVec(floatBits(Float.NaN), 4), 4), Right(LittleEndian.toVec(0L, 4)))
  }

  for {
    width  <- Seq(32, 64)
    signed <- Seq(false, true)
  } test(s"GLCompute OpConvertFTo${if (signed) "S" else "U"} saturates float$width infinities and maps NaN to zero") {
    val module = spv"""
      ${if (width == 64) "OpCapability Float64" else ""}
      $vulkan
      OpEntryPoint GLCompute %main "convert" %input %output
      OpExecutionMode %main LocalSize 1 1 1
      OpDecorate %input_block BufferBlock
      OpDecorate %output_block BufferBlock
      OpMemberDecorate %input_block 0 Offset 0
      OpMemberDecorate %output_block 0 Offset 0
      OpDecorate %input DescriptorSet 0
      OpDecorate %input Binding 0
      OpDecorate %output DescriptorSet 0
      OpDecorate %output Binding 1
      %void = OpTypeVoid
      %float = OpTypeFloat $width
      %int = OpTypeInt 32 ${if (signed) 1 else 0}
      %float4 = OpTypeVector %float 4
      %int4 = OpTypeVector %int 4
      %input_block = OpTypeStruct %float4
      %output_block = OpTypeStruct %int4
      %input_block_ptr = OpTypePointer Uniform %input_block
      %output_block_ptr = OpTypePointer Uniform %output_block
      %float4_ptr = OpTypePointer Uniform %float4
      %int4_ptr = OpTypePointer Uniform %int4
      %fn = OpTypeFunction %void
      %zero = OpConstant %int 0
      %input = OpVariable %input_block_ptr Uniform
      %output = OpVariable %output_block_ptr Uniform
      %main = OpFunction %void None %fn
      %entry = OpLabel
      %input_value = OpAccessChain %float4_ptr %input %zero
      %output_value = OpAccessChain %int4_ptr %output %zero
      %loaded = OpLoad %float4 %input_value
      %converted = OpConvertFTo${if (signed) "S" else "U"} %int4 %loaded
      OpStore %output_value %converted
      OpReturn
      OpFunctionEnd
    """
    val values = Seq(Double.NegativeInfinity, -0.75, Double.PositiveInfinity, Double.NaN)
    val memory = RegionMemory()
    val input  = memory.allocate(littleEndian(values.map(floatingBits(_, width)), width))
    val out    = memory.allocate(16L)
    assertEquals(run(module, "convert", memory, Vec(buffer(input), buffer(out))), Right(()))
    assertEquals(
      words(memory, out, offset = 0, count = 4),
      if (signed) Vec(0x80000000L, 0L, 0x7fffffffL, 0L) else Vec(0L, 0L, 0xffffffffL, 0L)
    )
  }

  for (
    ((sourceWidth, width), mode, grouped, lanes) <-
      pairwise(Seq((32, 16), (64, 16), (64, 32)), roundings.indices, Seq(false, true), Seq(1, 4))
  ) test(
    s"OpFConvert float$sourceWidth to float$width rounds ${roundings(mode)}" +
      s"${if (grouped) " through a decoration group" else ""}, $lanes lanes"
  ) {
    val fraction = if (width == 16) 10 else 23
    val one      = if (width == 16) 0x3c00L else 0x3f800000L
    val infinity = if (width == 16) 0x7c00L else 0x7f800000L
    val sign     = 1L << (width - 1)
    val ulp      = math.scalb(1.0, -fraction)
    val tiny     = math.scalb(1.0, if (width == 16) -24 else -149)
    val maximum  = if (width == 16) 65504.0 else Float.MaxValue.toDouble
    // expected magnitudes in RTE, RTZ, RTP, RTN order
    val cases = Seq(
      (0.0, Seq(0L, 0L, 0L, 0L)),
      (1.0, Seq(one, one, one, one)),
      (1.0 + ulp * 0.25, Seq(one, one, one + 1, one)),
      (1.0 + ulp * 0.5, Seq(one, one, one + 1, one)),
      (1.0 + ulp * 0.75, Seq(one + 1, one, one + 1, one)),
      (1.0 + ulp * 1.5, Seq(one + 2, one + 1, one + 2, one + 1)),
      (tiny * 0.25, Seq(0L, 0L, 1L, 0L)),
      (tiny * 0.5, Seq(0L, 0L, 1L, 0L)),
      (tiny * 0.75, Seq(1L, 0L, 1L, 0L)),
      (maximum * 2.0, Seq(infinity, infinity - 1, infinity, infinity - 1)),
      (Double.PositiveInfinity, Seq(infinity, infinity, infinity, infinity))
    ) ++
      (if (sourceWidth == 64 && width == 16) Seq(
         (1.0 + ulp * 0.5 + math.scalb(1.0, -40), Seq(one + 1, one, one + 1, one)),
         (1.0 + ulp * 0.5 - math.scalb(1.0, -40), Seq(one, one, one + 1, one))
       )
       else Seq.empty)
    val decorations =
      if (grouped) s"""OpDecorate %group FPRoundingMode ${roundings(mode)}
                      |%group = OpDecorationGroup
                      |OpGroupDecorate %group %converted""".stripMargin
      else s"OpDecorate %converted FPRoundingMode ${roundings(mode)}"
    val source = converter("OpFConvert", s"OpTypeFloat $sourceWidth", s"OpTypeFloat $width", lanes, decorations)
    for {
      (value, expected) <- cases
      negative          <- Seq(false, true)
    } {
      val input     = if (negative) -value else value
      val magnitude = expected(if (negative && mode >= 2) 5 - mode else mode)
      assertEquals(
        convert(
          source,
          littleEndian(Seq.fill(lanes)(floatingBits(input, sourceWidth)), sourceWidth),
          lanes * width / 8
        ),
        Right(littleEndian(Seq.fill(lanes)(magnitude | (if (negative) sign else 0L)), width)),
        input.toString
      )
    }
  }

  test("64-bit integer to float32 conversions round once") {
    // each source lies just above a binary32 tie that a binary64 intermediate would round onto
    for (
      (opcode, signedness, input, expected) <- Seq(
        ("OpConvertUToF", 0, 0x8000008000000001L, 0x5f000001L),
        ("OpConvertSToF", 1, 0x4000004000000001L, 0x5e800001L),
        ("OpConvertSToF", 1, 0xbfffffbfffffffffL, 0xde800001L)
      )
    )
      assertEquals(
        convert(converter(opcode, s"OpTypeInt 64 $signedness", "OpTypeFloat 32"), littleEndian(Seq(input), 64), 4),
        Right(littleEndian(Seq(expected), 32)),
        opcode
      )
  }

  // 2^24 + 3 lies between the float32 neighbours 2^24 + 2 and 2^24 + 4
  for (
    (opcode, input, expected) <- Seq(
      ("OpConvertUToF", 16777219L, Seq(16777220f, 16777218f, 16777220f, 16777218f)),
      ("OpConvertSToF", 16777219L, Seq(16777220f, 16777218f, 16777220f, 16777218f)),
      ("OpConvertSToF", -16777219L, Seq(-16777220f, -16777218f, -16777218f, -16777220f))
    )
  )
    test(s"$opcode of $input to float32 follows its FPRoundingMode decoration") {
      for ((rounding, value) <- roundings.zip(expected)) {
        val decoration = s"OpDecorate %converted FPRoundingMode $rounding"
        val source     = converter(opcode, "OpTypeInt 32 0", "OpTypeFloat 32", decorations = decoration)
        assertEquals(
          convert(source, littleEndian(Seq(input), 32), 4),
          Right(littleEndian(Seq(floatBits(value)), 32)),
          rounding
        )
      }
    }

  test("OpFConvert of a double constant to half rounds once") {
    for (
      (input, expected) <- Seq(
        (1 + math.pow(2, -11) + math.pow(2, -25)) -> 0x3c01L,
        (1 + math.pow(2, -11) - math.pow(2, -25)) -> 0x3c00L,
        Math.nextDown(65520.0)                    -> 0x7bffL
      )
    ) {
      val module = spv"""
        $openCL
        OpEntryPoint Kernel %main "convert"
        %void = OpTypeVoid
        %double = OpTypeFloat 64
        %half = OpTypeFloat 16
        %out_ptr = OpTypePointer CrossWorkgroup %half
        %fn = OpTypeFunction %void %out_ptr
        %value = OpConstant %double $input
        %main = OpFunction %void None %fn
        %out = OpFunctionParameter %out_ptr
        %entry = OpLabel
        %converted = OpFConvert %half %value
        OpStore %out %converted
        OpReturn
        OpFunctionEnd
      """
      val memory = RegionMemory()
      val out    = memory.allocate(2L)
      assertEquals(run(module, "convert", memory, Vec(buffer(out))), Right(()))
      assertEquals(LittleEndian.decode(memory.bytes(out), 0, 2), expected, input)
    }
  }

  private def halfModule(instruction: String, load: Boolean): Module = spv"""
    $openCL
    OpEntryPoint Kernel %main "half_memory"
    %void = OpTypeVoid
    %ulong = OpTypeInt 64 0
    %half = OpTypeFloat 16
    %float = OpTypeFloat 32
    %float3 = OpTypeVector %float 3
    %half_ptr = OpTypePointer CrossWorkgroup %half
    %data_type = ${if (load) "OpTypePointer CrossWorkgroup %float3" else "OpTypeVector %float 3"}
    %fn = OpTypeFunction %void %half_ptr %data_type
    %offset = OpConstant %ulong 1
    %main = OpFunction %void None %fn
    %halves = OpFunctionParameter %half_ptr
    %data = OpFunctionParameter %data_type
    %entry = OpLabel
    $instruction
    ${if (load) "OpStore %data %loaded" else ""}
    OpReturn
    OpFunctionEnd
  """

  private def runHalf(module: Module, initial: Vec[Byte], data: AllocationId => KernelTask.Argument): Vec[Byte] = {
    val memory = RegionMemory()
    val halves = memory.allocate(initial)
    assertEquals(run(module, "half_memory", memory, Vec(buffer(halves), data(halves))), Right(()))
    memory.bytes(halves)
  }

  // OpenCL.std aligned half3 uses four-half strides; the padding element is not accessed
  for ((layout, instruction, start) <- Seq(("packed", "vload_halfn", 6), ("aligned", "vloada_halfn", 8)))
    test(s"$layout half3 load uses the correct stride and preserves signed zero and subnormals") {
      val sentinel = Vec.fill(48)(0x55.toByte)
      val halves   = littleEndian(Seq(0x8000L, 1L, 0xc100L), 16)
      val initial  = sentinel.take(start) ++ halves ++ sentinel.drop(start + halves.size)
      val module   = halfModule(s"%loaded = OpExtInst %float3 %opencl $instruction %offset %halves 3", load = true)
      // float3 storage may overwrite its fourth, padding component
      assertEquals(
        runHalf(module, initial, buffer(_, 32L)).take(44),
        initial.take(32) ++ littleEndian(Seq(0x80000000L, 0x33800000L, 0xc0200000L), 32)
      )
    }

  private def store(instruction: String, rounding: Option[Int], input: Seq[Long], expected: Seq[Long], start: Int)
      : Unit = {
    val initial = Vec.fill(24)(0x55.toByte)
    val module  = halfModule(
      s"%stored = OpExtInst %void %opencl $instruction %data %offset %halves${rounding.fold("")(mode => s" !$mode")}",
      load = false
    )
    val bytes = littleEndian(expected, 16)
    assertEquals(
      runHalf(module, initial, _ => scalarBytes(littleEndian(input, 32))),
      initial.take(start) ++ bytes ++ initial.drop(start + bytes.size)
    )
  }

  private val halfRoundings = Seq(
    (Vec(0x3c00L, 0xbc00L, 0x3c02L), Vec(0x7c00L, 0xfc00L, 0x0000L)),
    (Vec(0x3c00L, 0xbc00L, 0x3c01L), Vec(0x7bffL, 0xfbffL, 0x0000L)),
    (Vec(0x3c01L, 0xbc00L, 0x3c02L), Vec(0x7c00L, 0xfbffL, 0x0001L)),
    (Vec(0x3c00L, 0xbc01L, 0x3c01L), Vec(0x7bffL, 0xfc00L, 0x0000L))
  )
  for {
    (layout, instruction, start) <- Seq(("packed", "vstore_halfn_r", 6), ("aligned", "vstorea_halfn_r", 8))
    mode                         <- roundings.indices
    overflow                     <- Seq(false, true)
  } test(s"$layout half3 store rounds ${if (overflow) "overflow and underflow" else "ties"} ${roundings(mode)}") {
    val (ties, limits) = halfRoundings(mode)
    if (overflow) store(instruction, Some(mode), Seq(0x477ff000L, 0xc77ff000L, 0x33000000L), limits, start)
    else store(instruction, Some(mode), Seq(0x3f801000L, 0xbf801000L, 0x3f803000L), ties, start)
  }

  for ((mode, tiny) <- roundings.indices.zip(Seq(0x8000L, 0x8000L, 0x8000L, 0x8001L)))
    test(s"half store handles negative underflow and infinities ${roundings(mode)}") {
      store("vstore_halfn_r", Some(mode), Seq(0xb3000000L, 0x7f800000L, 0xff800000L), Seq(tiny, 0x7c00L, 0xfc00L), 6)
    }

  test("half store without a rounding operand defaults to nearest even at overflow") {
    store("vstore_halfn", None, Seq(0x477ff000L, 0xc77ff000L, 0x80000000L), Seq(0x7c00L, 0xfc00L, 0x8000L), 6)
  }
}
