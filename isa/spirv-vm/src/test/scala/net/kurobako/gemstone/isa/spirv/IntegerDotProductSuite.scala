package net.kurobako.gemstone.isa.spirv

import fixtures.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.testing.*

class IntegerDotProductSuite extends munit.FunSuite {
  private def dot(results: Int, body: String): Vec[Long] = {
    val module = spv"""
      OpCapability DotProductInput4x8Bit
      OpCapability DotProductInput4x8BitPacked
      OpCapability DotProduct
      $openCL
      OpEntryPoint Kernel %main "dot"
      %void = OpTypeVoid
      %uint = OpTypeInt 32 0
      %uchar = OpTypeInt 8 0
      %ulong = OpTypeInt 64 0
      %v4uchar = OpTypeVector %uchar 4
      %uint_ptr = OpTypePointer CrossWorkgroup %uint
      %fn = OpTypeFunction %void %uint_ptr
      ${indices(results)}
      %c_n128 = OpConstant %uchar 128
      %c_127 = OpConstant %uchar 127
      %c_n1 = OpConstant %uchar 255
      %c_5 = OpConstant %uchar 5
      %c_3 = OpConstant %uchar 3
      %c_7 = OpConstant %uchar 7
      %c_255 = OpConstant %uchar 255
      %c_1 = OpConstant %uchar 1
      %c_200 = OpConstant %uchar 200
      %c_2 = OpConstant %uchar 2
      %c_100 = OpConstant %uchar 100
      %c_4 = OpConstant %uchar 4
      %signed_a = OpConstantComposite %v4uchar %c_n128 %c_127 %c_n1 %c_5
      %signed_b = OpConstantComposite %v4uchar %c_n128 %c_n128 %c_3 %c_7
      %unsigned_a = OpConstantComposite %v4uchar %c_255 %c_1 %c_200 %c_3
      %unsigned_b = OpConstantComposite %v4uchar %c_255 %c_2 %c_100 %c_4
      %packed_a = OpConstant %uint 0x05ff7f80
      %packed_b = OpConstant %uint 0x07038080
      %near_max = OpConstant %uint 2147483600
      %near_unsigned_max = OpConstant %uint 4294901760
      %near_min = OpConstant %uint 2147483696
      %main = OpFunction %void None %fn
      %out = OpFunctionParameter %uint_ptr
      %entry = OpLabel
      $body
      OpReturn
      OpFunctionEnd
    """
    val memory = RegionMemory()
    val out    = memory.allocate((results * 4L).unsafe[Long > 0])
    assertEquals(run(module, "dot", memory, Vec(buffer(out))), Right(()))
    words(memory, out, 0, results)
  }

  private def store(index: Int, value: String): String =
    s"""%slot_$index = OpInBoundsPtrAccessChain %uint_ptr %out %ulong_$index
       |OpStore %slot_$index $value
       |""".stripMargin

  private def indices(count: Int): String = (0 until count).map(index =>
    s"%ulong_$index = OpConstant %ulong $index"
  ).mkString("\n")

  private def program(results: Vector[(String, String)]): String =
    results.zipWithIndex.map { case ((name, instruction), index) => s"$name = $instruction\n${store(index, name)}" }
      .mkString

  test("dot products of 4x8-bit vectors sum widened lane products") {
    val results = Vector(
      "%sdot"  -> "OpSDot %uint %signed_a %signed_b",
      "%udot"  -> "OpUDot %uint %unsigned_a %unsigned_b",
      "%sudot" -> "OpSUDot %uint %signed_a %unsigned_a"
    )
    assertEquals(dot(results.size, program(results)).map(_.toInt), Vec(160, 85039, -32698))
  }

  test("packed 4x8-bit dot products read lanes from the low byte up") {
    val results = Vector(
      "%sdot"  -> "OpSDot %uint %packed_a %packed_b PackedVectorFormat4x8Bit",
      "%udot"  -> "OpUDot %uint %packed_a %packed_b PackedVectorFormat4x8Bit",
      "%sudot" -> "OpSUDot %uint %packed_a %packed_b PackedVectorFormat4x8Bit"
    )
    assertEquals(dot(results.size, program(results)).map(_.toInt), Vec(160, 33440, -96))
  }

  test("accumulating dot products saturate at the result type's signed or unsigned range") {
    val results = Vector(
      "%sdot"    -> "OpSDotAccSat %uint %signed_a %signed_b %near_max",
      "%udot"    -> "OpUDotAccSat %uint %unsigned_a %unsigned_b %near_unsigned_max",
      "%sudot"   -> "OpSUDotAccSat %uint %signed_a %unsigned_a %near_min",
      "%packed"  -> "OpSDotAccSat %uint %packed_a %packed_b %near_max PackedVectorFormat4x8Bit",
      "%inrange" -> "OpUDotAccSat %uint %packed_a %packed_b %packed_a PackedVectorFormat4x8Bit"
    )
    assertEquals(
      dot(results.size, program(results)),
      Vec(0x7fffffffL, 0xffffffffL, 0x80000000L, 0x7fffffffL, 0x05ff7f80L + 33440L)
    )
  }

  test("accumulating dot products of 64-bit components read unsigned lanes as unsigned") {
    val module = spv"""
      OpCapability DotProductInputAll
      OpCapability DotProduct
      $openCL
      OpEntryPoint Kernel %main "dot"
      %void = OpTypeVoid
      %ulong = OpTypeInt 64 0
      %v2ulong = OpTypeVector %ulong 2
      %ulong_ptr = OpTypePointer CrossWorkgroup %ulong
      %fn = OpTypeFunction %void %ulong_ptr
      %zero = OpConstant %ulong 0
      %one = OpConstant %ulong 1
      %two = OpConstant %ulong 2
      %top = OpConstant %ulong 9223372036854775808
      %high = OpConstantComposite %v2ulong %top %zero
      %unit = OpConstantComposite %v2ulong %one %zero
      %double = OpConstantComposite %v2ulong %two %zero
      %main = OpFunction %void None %fn
      %out = OpFunctionParameter %ulong_ptr
      %entry = OpLabel
      %exact = OpUDotAccSat %ulong %high %unit %zero
      OpStore %out %exact
      %saturated = OpUDotAccSat %ulong %high %double %zero
      %second = OpInBoundsPtrAccessChain %ulong_ptr %out %one
      OpStore %second %saturated
      %mixed = OpSUDotAccSat %ulong %unit %high %zero
      %third = OpInBoundsPtrAccessChain %ulong_ptr %out %two
      OpStore %third %mixed
      OpReturn
      OpFunctionEnd
    """
    val memory = RegionMemory()
    val out    = memory.allocate(24L)
    assertEquals(run(module, "dot", memory, Vec(buffer(out))), Right(()))
    assertEquals(
      Vec.tabulate(3)(index => LittleEndian.decodeInt64(memory.bytes(out), index * 8)),
      Vec(Long.MinValue, -1L, Long.MaxValue)
    )
  }
}
