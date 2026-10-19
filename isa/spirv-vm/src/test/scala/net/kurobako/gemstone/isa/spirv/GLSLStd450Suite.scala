package net.kurobako.gemstone.isa.spirv

import fixtures.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.testing.bits.*
import net.kurobako.gemstone.testing.either.*
import net.kurobako.gemstone.vm.*

class GLSLStd450Suite extends munit.FunSuite {
  private val LaneCounts     = Seq(1, 4)
  private val Decompositions = Seq("Modf", "Frexp")
  private val Norms          = Seq("Length", "Distance")

  private val packing =
    """%float = OpTypeFloat 32
      |%double = OpTypeFloat 64
      |%uint = OpTypeInt 32 0
      |%int = OpTypeInt 32 1
      |%long = OpTypeInt 64 1
      |%v2float = OpTypeVector %float 2
      |%v4float = OpTypeVector %float 4
      |%v2uint = OpTypeVector %uint 2
      |%modf_struct = OpTypeStruct %float %float
      |%frexp_struct = OpTypeStruct %float %int
      |""".stripMargin

  private def f(value: Float): Long = single(value)

  private def packed(operation: String, result: String, inputs: (String, Vec[Byte])*)(expected: Vec[Byte]): Unit =
    elementwise(packing, result, operation, inputs, expected)

  test("geometric functions combine whole vectors") {
    val types                             = "%float = OpTypeFloat 32\n%v4float = OpTypeVector %float 4\n"
    def floats(values: Float*): Vec[Byte] = encode(32, values.map(f(_))*)
    def vectors(values: Seq[Float]*)      = values.map(lanes => "%v4float" -> floats(lanes*))
    elementwise(types, "%v4float", "Cross", vectors(Seq(1f, 2f, 3f, 7f), Seq(4f, 5f, 6f, 8f)), floats(-3f, 6f, -3f, 0f))
    elementwise(
      types,
      "%v4float",
      "Reflect",
      vectors(Seq(1f, -1f, 0f, 2f), Seq(0f, 1f, 0f, 0f)),
      floats(1f, 1f, 0f, 2f)
    )
    elementwise(
      types,
      "%v4float",
      "FaceForward",
      vectors(Seq(1f, 2f, 3f, 4f), Seq(1f, 0f, 0f, 0f), Seq(1f, 0f, 0f, 0f)),
      floats(-1f, -2f, -3f, -4f)
    )
    elementwise(
      types,
      "%v4float",
      "FaceForward",
      vectors(Seq(1f, 2f, 3f, 4f), Seq(1f, 0f, 0f, 0f), Seq(-1f, 0f, 0f, 0f)),
      floats(1f, 2f, 3f, 4f)
    )
    elementwise(
      types,
      "%v4float",
      "Refract",
      vectors(Seq(0.6f, -0.8f, 0f, 0f), Seq(0f, 1f, 0f, 0f)) :+ ("%float" -> floats(1f)),
      floats(0.6f, -0.8f, 0f, 0f)
    )
    elementwise(
      types,
      "%v4float",
      "Refract",
      vectors(Seq(0.8f, -0.6f, 0f, 0f), Seq(0f, 1f, 0f, 0f)) :+ ("%float" -> floats(2f)),
      floats(0f, 0f, 0f, 0f)
    )
  }

  test("normalised packing clamps and rounds each channel in bit order") {
    packed("PackSnorm4x8", "%uint", "%v4float" -> encode(32, f(-1), f(0), f(1), f(0.25f)))(encode(32, 0x207f0081L))
    packed("PackUnorm4x8", "%uint", "%v4float" -> encode(32, f(-1), f(0.25f), f(1), f(2)))(encode(32, 0xffff4000L))
    packed("PackSnorm2x16", "%uint", "%v2float" -> encode(32, f(-1), f(1)))(encode(32, 0x7fff8001L))
    packed("PackUnorm2x16", "%uint", "%v2float" -> encode(32, f(-1), f(1)))(encode(32, 0xffff0000L))
  }

  test("normalised unpacking handles signed minimum encodings") {
    packed("UnpackSnorm2x16", "%v2float", "%uint" -> encode(32, 0x7fff8000L))(encode(32, f(-1), f(1)))
    packed("UnpackUnorm2x16", "%v2float", "%uint" -> encode(32, 0xffff0000L))(encode(32, f(0), f(1)))
    packed("UnpackSnorm4x8", "%v4float", "%uint" -> encode(32, 0x7f008180L))(encode(32, f(-1), f(-1), f(0), f(1)))
    packed("UnpackUnorm4x8", "%v4float", "%uint" -> encode(32, 0xff00ff00L))(encode(32, f(0), f(1), f(0), f(1)))
  }

  test("half packing preserves signed zero and unpacking preserves subnormals") {
    packed("PackHalf2x16", "%uint", "%v2float" -> encode(32, f(1), f(-2)))(encode(32, 0xc0003c00L))
    packed("PackHalf2x16", "%uint", "%v2float" -> encode(32, f(-0.0f), f(0)))(encode(32, 0x00008000L))
    packed("UnpackHalf2x16", "%v2float", "%uint" -> encode(32, 0x80000001L))(
      encode(32, f(Math.scalb(1.0f, -24)), f(-0.0f))
    )
  }

  test("double packing transfers bits without floating arithmetic") {
    packed("PackDouble2x32", "%double", "%v2uint" -> encode(32, 0x89abcdefL, 0x7ff12345L))(
      encode(32, 0x89abcdefL, 0x7ff12345L)
    )
    packed("UnpackDouble2x32", "%v2uint", "%double" -> encode(32, 0x89abcdefL, 0xfff12345L))(
      encode(32, 0x89abcdefL, 0xfff12345L)
    )
  }

  test("ModfStruct retains fractional signs and FrexpStruct normalises subnormals") {
    packed("ModfStruct", "%modf_struct", "%float" -> encode(32, f(-2.75f)))(encode(32, f(-0.75f), f(-2)))
    packed("ModfStruct", "%modf_struct", "%float" -> encode(32, f(Float.NegativeInfinity)))(
      encode(32, f(-0.0f), f(Float.NegativeInfinity))
    )
    packed("FrexpStruct", "%frexp_struct", "%float" -> encode(32, 1L))(encode(32, f(0.5f), -148L))
    packed("FrexpStruct", "%frexp_struct", "%float" -> encode(32, f(-0.0f)))(encode(32, f(-0.0f), 0L))
  }

  test("Sinh and Cosh retain GLSL exp intermediate overflow") {
    packed("Sinh", "%float", "%float" -> encode(32, f(89.41436f)))(encode(32, f(Float.PositiveInfinity)))
    packed("Cosh", "%float", "%float" -> encode(32, f(-89.41436f)))(encode(32, f(Float.PositiveInfinity)))
  }

  test("half results round the double value once") {
    for (
      (
        operation,
        input,
        expected
      ) <- Seq(("Exp", 0x1f79L, 0x3c07L), ("Log", 0x1d78L, 0xc53bL), ("Sin", 0x32b3L, 0x32a7L))
    )
      elementwise(
        "%half = OpTypeFloat 16\n",
        "%half",
        operation,
        Seq("%half" -> encode(16, input)),
        encode(16, expected)
      )
  }

  test("Ldexp accepts wide exponents without narrowing overflow") {
    packed("Ldexp", "%float", "%float" -> encode(32, f(1.5f)), "%long" -> encode(64, 3L))(encode(32, f(12.0f)))
    packed("Ldexp", "%float", "%float" -> encode(32, f(1.5f)), "%long" -> encode(64, Long.MaxValue))(
      encode(32, f(Float.PositiveInfinity))
    )
    packed("Ldexp", "%float", "%float" -> encode(32, f(-1.5f)), "%long" -> encode(64, Long.MinValue))(
      encode(32, f(-0.0f))
    )
  }

  for {
    width     <- Seq(32, 64)
    lanes     <- LaneCounts
    operation <- Decompositions
  } test(s"$operation stores its pointer component at width $width, lanes $lanes") {
    val subnormal                          = Math.scalb(1.0, if (width == 32) -140 else -1070)
    val exponent                           = if (width == 32) -139L else -1069L
    def floats(values: Double*): Vec[Byte] = encode(width, values.take(lanes).map(floatingBits(_, width))*)
    def vector(scalar: String): String     = if (lanes == 1) scalar else s"${scalar}_vector"
    val modf                               = operation == "Modf"
    val fractions                          =
      if (modf) floats(-0.5, negativeZero, subnormal, 0.0)
      else floats(-0.625, negativeZero, 0.5, Double.PositiveInfinity)
    val integral =
      if (modf) floats(-2.0, negativeZero, 0.0, Double.PositiveInfinity)
      else encode(64, Seq(2L, 0L, exponent, 0L).take(lanes)*)
    val integralType = if (modf) "%float" else "%long"
    elementwise(
      s"""%float = OpTypeFloat $width
         |%long = OpTypeInt 64 1
         |%float_vector = OpTypeVector %float 4
         |%long_vector = OpTypeVector %long 4
         |""".stripMargin,
      vector("%float"),
      operation,
      Seq(vector("%float") -> floats(-2.5, negativeZero, subnormal, Double.PositiveInfinity)),
      fractions,
      Some(vector(integralType) -> integral)
    )
  }

  for {
    width     <- Seq(16, 32, 64)
    operation <- Norms
    lanes     <- LaneCounts
  } test(s"$operation computes $width-bit results from $lanes components") {
    val left     = if (lanes == 1) Seq(-3.0) else Seq(3.0, 4.0, 0.0, 0.0)
    val right    = if (lanes == 1) Seq(1.0) else Seq.fill(4)(0.0)
    val operand  = if (lanes == 1) "%float" else "%float_vector"
    val expected = if (lanes == 4) 5.0 else if (operation == "Length") 3.0 else 4.0
    elementwise(
      s"""%float = OpTypeFloat $width
         |%float_vector = OpTypeVector %float 4
         |""".stripMargin,
      "%float",
      operation,
      (Seq(left) ++ (if (operation == "Distance") Seq(right) else Seq.empty))
        .map(values => operand -> encode(width, values.map(floatingBits(_, width))*)),
      encode(width, floatingBits(expected, width))
    )
  }

  private val integerOperations = Seq("SAbs", "SSign", "UMin", "SMin", "UMax", "SMax", "UClamp", "SClamp")

  for {
    (operation, width, lanes, declared) <-
    (pairwise(integerOperations, Seq(8, 16, 32, 64), Seq(1, 4), Seq(0, 1)) ++
      Seq("SMin", "SMax", "SClamp").flatMap(operation => Seq((operation, 32, 4, 1), (operation, 64, 4, 1)))).distinct ++
      pairwise(Seq("FindILsb", "FindSMsb", "FindUMsb"), Seq(1, 4), Seq(0, 1)).map((o, l, d) => (o, 32, l, d))
  } test(s"$operation at width $width, lanes $lanes, declared signedness $declared") {
    val signed  = operation.startsWith("S")
    val find    = operation.startsWith("Find")
    val arity   = if (operation.endsWith("Clamp")) 3 else if (find || Set("SAbs", "SSign")(operation)) 1 else 2
    val high    = BigInt(1) << (width - 1)
    val maximum = if (signed) high - 1 else (high << 1) - 1
    val inputs  =
      if (find) Seq(BigInt(0), BigInt(-1), -high, high - 1)
      else if (arity == 1) Seq(BigInt(0), BigInt(-5), -high, maximum)
      else if (signed) Seq(-high, BigInt(-5), BigInt(3), maximum)
      else Seq(BigInt(0), maximum, high, BigInt(3))
    val lower = if (signed) Seq(-7, -2, 0, 5).map(BigInt(_)) else Seq(BigInt(1), high, high - 1, BigInt(3))
    val upper =
      if (signed) Seq(BigInt(7), BigInt(2), BigInt(2), maximum - 1) else Seq(BigInt(2), maximum - 1, maximum, BigInt(5))
    val expected = operation match {
      case "SAbs"     => Seq(BigInt(0), BigInt(5), high, maximum)
      case "SSign"    => Seq(0, -1, -1, 1).map(BigInt(_))
      case "FindILsb" => Seq(-1, 0, 31, 0).map(BigInt(_))
      case "FindSMsb" => Seq(-1, -1, 30, 30).map(BigInt(_))
      case "FindUMsb" => Seq(-1, 31, 31, 30).map(BigInt(_))
      case "SMin"     => Seq(-high, BigInt(-5), BigInt(0), BigInt(5))
      case "SMax"     => Seq(BigInt(-7), BigInt(-2), BigInt(3), maximum)
      case "SClamp"   => Seq(BigInt(-7), BigInt(-2), BigInt(2), maximum - 1)
      case "UMin"     => Seq(BigInt(0), high, high - 1, BigInt(3))
      case "UMax"     => Seq(BigInt(1), maximum, high, BigInt(3))
      case _          => Seq(BigInt(1), maximum - 1, high, BigInt(3))
    }
    val operand                                 = if (lanes == 1) "%int" else "%int_vector"
    def lanesOf(values: Seq[BigInt]): Vec[Byte] = encode(width, values.take(lanes).map(_.toLong)*)
    elementwise(
      s"""%int = OpTypeInt $width $declared
         |%int_vector = OpTypeVector %int 4
         |""".stripMargin,
      operand,
      operation,
      Seq(inputs, lower, upper).take(arity).map(values => operand -> lanesOf(values)),
      lanesOf(expected)
    )
  }

  private val prologue = openCL.replace("OpMemoryModel", "%glsl = OpExtInstImport \"GLSL.std.450\"\nOpMemoryModel")

  private def encode(width: Int, values: Long*): Vec[Byte] = littleEndian(values, width)

  /** Kernel that stores one GLSL.std.450 instruction applied to scalar `inputs`; `pointer` is its trailing pointer
    * operand and the bytes expected behind it.
    */
  private def elementwise(
      types: String,
      result: String,
      operation: String,
      inputs: Seq[(String, Vec[Byte])],
      expected: Vec[Byte],
      pointer: Option[(String, Vec[Byte])] = None
  ): Unit = {
    val arguments = inputs.indices.map(index => s"%a$index")
    val module    = spv"""
      $prologue
      OpEntryPoint Kernel %main "glsl"
      %void = OpTypeVoid
      $types
      %out_ptr = OpTypePointer CrossWorkgroup $result
      ${pointer.fold("")((target, _) => s"%aux_ptr = OpTypePointer CrossWorkgroup $target")}
      %fn = OpTypeFunction %void %out_ptr ${pointer.fold("")(_ => "%aux_ptr")} ${inputs.map(_._1).mkString(" ")}
      %main = OpFunction %void None %fn
      %out = OpFunctionParameter %out_ptr
      ${pointer.fold("")(_ => "%aux = OpFunctionParameter %aux_ptr")}
      ${inputs.zip(arguments).map((input, argument) => s"$argument = OpFunctionParameter ${input._1}").mkString("\n")}
      %entry = OpLabel
      %result = OpExtInst $result %glsl $operation ${arguments.mkString(" ")} ${pointer.fold("")(_ => "%aux")}
      OpStore %out %result
      OpReturn
      OpFunctionEnd
    """
    val memory  = RegionMemory()
    val out     = memory.allocate(expected.size.toLong.refine[Long > 0].value)
    val aux     = pointer.map((_, bytes) => memory.allocate(bytes.size.toLong.refine[Long > 0].value))
    val scalars = inputs.map((_, bytes) => KernelTask.Argument.Scalar(bytes.unsafe))
    val buffers = Vec(buffer(out)) ++ Vec.from(aux.map(buffer(_)))
    assertEquals(run(module, "glsl", memory, buffers ++ Vec.from(scalars)), Right(()))
    assertEquals(memory.bytes(out), expected)
    pointer.zip(aux).foreach { case ((_, bytes), region) => assertEquals(memory.bytes(region), bytes) }
  }
}
