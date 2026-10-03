package net.kurobako.gemstone.isa.ptx.v9_3

import execution.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.nvidia.VMIR.*
import net.kurobako.gemstone.memory.*

class FmaRoundingSuite extends munit.FunSuite {
  for (width <- Vec(32, 64)) test(s"fma.f$width rounds once in every directed mode") {
    val one      = if (width == 32) 0x3f800000L else 0x3ff0000000000000L
    val half     = if (width == 32) 0x3f000000L else 0x3fe0000000000000L
    val two      = if (width == 32) 0x40000000L else 0x4000000000000000L
    val sign     = if (width == 32) 0x80000000L else Long.MinValue
    val maximum  = if (width == 32) 0x7f7fffffL else 0x7fefffffffffffffL
    val infinity = maximum + 1
    // Expected order is RN, RM, RP, RZ. The minimum subnormal addend is
    // deliberately too small for even a binary64 intermediate in the f32 case.
    val cases = Vec(
      (one, one, 1L, Vec(one, one, one + 1, one)),
      (one | sign, one, 1L | sign, Vec(one | sign, (one + 1) | sign, one | sign, one | sign)),
      (maximum, two, 0L, Vec(infinity, maximum, infinity, maximum)),
      (maximum | sign, two, 0L, Vec(infinity | sign, infinity | sign, maximum | sign, maximum | sign)),
      (one, one, one | sign, Vec(0L, sign, 0L, 0L)),
      (0L, one, 0L, Vec.fill(4)(0L)),
      (sign, one, sign, Vec.fill(4)(sign)),
      (1L, half, 0L, Vec(0L, 0L, 1L, 0L)),
      (1L | sign, half, sign, Vec(sign, 1L | sign, sign, sign)),
      (infinity, one, one, Vec.fill(4)(infinity))
    )
    def literal(bits: Long): String =
      if (width == 32) "0f" + "%08x".format(bits) else "0d" + "%016x".format(bits)
    val modes        = Vec("rn", "rm", "rp", "rz")
    val size         = width / 8
    val instructions = cases.zipWithIndex.flatMap { case ((a, b, c, _), row) =>
      modes.zipWithIndex.map { case (mode, col) =>
        s"fma.$mode.f$width %r, ${literal(a)}, ${literal(b)}, ${literal(c)}; " +
          s"st.global.b$width [%p+${(row * 4 + col) * size}], %r;"
      }
    }.mkString("\n")
    val source = s""".version 9.3
      |.target sm_80
      |.address_size 64
      |.visible .entry fused(.param .u64 output) {
      | .reg .b$width %r;
      | .reg .u64 %p;
      | ld.param.u64 %p, [output];
      | $instructions
      | ret;
      |}
      |""".stripMargin
    val kernel = compileKernel(source)
    val memory = ArrayCheckedMemory.allocate((cases.size * 4 * size).unsafe[Int >= 0])
    val start  = memory.allocation.start
    assertEquals(
      execute(
        kernel,
        1,
        1,
        Map(("output": NonEmpty[String]) -> Argument.fromLong(start.raw)),
        memory
      ),
      Right(())
    )
    val actual = (0 until cases.size * 4).toVec.map { index =>
      memory.readLittleEndian(start.offset(index.toLong * size), size.unsafe[Int in 1 ~ 8]).toOption.get
    }
    assertEquals(actual, cases.flatMap(_._4))
  }
  test("scalar and packed binary16 FMA avoid an intermediate binary32 rounding") {
    // Each exact product-plus-addend lies on the opposite side of a half-ULP
    // boundary from its binary32 rounding. Expected half bits are independently
    // rounded from the exact binary16 inputs.
    val cases = Vec(
      (0x5f90, 0xc7c0, 0x037c, 0xeb53),
      (0x630a, 0x82b3, 0x45c0, 0x45b7),
      (0xc65e, 0xc957, 0x712f, 0x7137),
      (0xb86d, 0xcc76, 0x8fc2, 0x48ef),
      (0x0001, 0x3800, 0x0000, 0x0000),
      (0x8000, 0x3c00, 0x8000, 0x8000)
    )
    def pair(value: Int): Long = value.toLong | (value.toLong << 16)
    val instructions           = cases.zipWithIndex.map { case ((a, b, c, _), index) =>
      s"""mov.b16 %a,$a; mov.b16 %b,$b; mov.b16 %c,$c;
         |fma.rn.f16 %h,%a,%b,%c; st.global.b16 [%p+${index * 8}],%h;
         |mov.b32 %x,${pair(a)}; mov.b32 %y,${pair(b)}; mov.b32 %z,${pair(c)};
         |fma.rn.f16x2 %v,%x,%y,%z; st.global.b32 [%p+${index * 8 + 4}],%v;""".stripMargin
    }.mkString("\n")
    val source = s""".version 9.3
      |.target sm_80
      |.address_size 64
      |.visible .entry fused(.param .u64 output) {
      |.reg .b16 %a,%b,%c,%h;
      |.reg .b32 %x,%y,%z,%v;
      |.reg .u64 %p;
      |ld.param.u64 %p,[output];
      |$instructions
      |ret;
      |}""".stripMargin
    val kernel = compileKernel(source)
    val memory = ArrayCheckedMemory.allocate((cases.size * 8).unsafe[Int >= 0])
    assertEquals(
      execute(
        kernel,
        1,
        1,
        Map(("output": NonEmpty[String]) -> Argument.fromLong(memory.allocation.start.raw)),
        memory
      ),
      Right(())
    )
    cases.zipWithIndex.foreach { case ((_, _, _, expected), index) =>
      val base = memory.allocation.start.offset(index.toLong * 8)
      assertEquals(memory.readLittleEndian(base, 2), Right(expected.toLong))
      assertEquals(memory.readLittleEndian(base.offset(4), 4), Right(pair(expected)))
    }
  }

}
