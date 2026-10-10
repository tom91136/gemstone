package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.constraint.*

class CodeWordsSuite extends munit.FunSuite {
  private def bytes(words: Long*): Array[Byte] = words.toArray.flatMap(LittleEndian.toArray(_, 8))

  private def words(code: Array[Byte], generation: Generation): Either[String, Vec[(Int >= 0, Array[Byte])]] =
    CodeWords.split(code, generation).map(CodeWords.words)

  private def expected(address: Int >= 0, raw: Long, slot: Int): (Int >= 0, Vec[Byte]) =
    (address, (LittleEndian.toArray(raw, 8) ++ Array.tabulate[Byte](3)(index => (slot >>> (index * 8)).toByte)).toVec)

  test("Kepler skips each control word and preserves NVIDIA oracle addresses") {
    val code = bytes(
      0x0880b8b0a0a08cc0L,
      0x64c03c00089c0006L,
      0x86400000129c0002L,
      0x86400000109c000eL,
      0x51080c00051c0002L,
      0x5b681c002a9c001eL,
      0x180000000000003cL,
      0x60c40800291c0012L
    )
    val actual = words(code, Generation.Kepler).fold(fail(_), identity)

    assertEquals(actual.map(_._1.raw), Vec(8, 16, 24, 32, 40, 48, 56))
    assertEquals(
      actual.map(_._2.toVec),
      Vec(
        LittleEndian.toArray(0x64c03c00089c0006L, 8).toVec,
        LittleEndian.toArray(0x86400000129c0002L, 8).toVec,
        LittleEndian.toArray(0x86400000109c000eL, 8).toVec,
        LittleEndian.toArray(0x51080c00051c0002L, 8).toVec,
        LittleEndian.toArray(0x5b681c002a9c001eL, 8).toVec,
        LittleEndian.toArray(0x180000000000003cL, 8).toVec,
        LittleEndian.toArray(0x60c40800291c0012L, 8).toVec
      )
    )
  }

  test("Maxwell unpacks three physical control slots into 88-bit logical words") {
    val code = bytes(
      0x001cfc00e22007f6L,
      0x4c98078000870001L,
      0xf0c8000002570000L,
      0xf0c8000002170002L
    )
    val actual = words(code, Generation.Maxwell).fold(fail(_), identity)

    assertEquals(
      actual.map { case (address, encoded) => (address, encoded.toVec) },
      Vec(
        expected(8, 0x4c98078000870001L, 0x007f6),
        expected(16, 0xf0c8000002570000L, 0x00711),
        expected(24, 0xf0c8000002170002L, 0x0073f)
      )
    )
    assertEquals(actual.map(_._1.raw), Vec(8, 16, 24))
    assertEquals(actual.map(_._2.length), Vec(11, 11, 11))
    assertEquals(actual.map(_._2(10) & 0xe0), Vec(0, 0, 0))
  }

  test("Pascal uses the same packed control-slot placement") {
    val code = bytes(
      0x001cfc00e22007f6L,
      0x4c98078000870001L,
      0xf0c8000002570000L,
      0xf0c8000002170002L
    )
    val generation = Target.parse("sm_60").fold(fail(_), _.generation)
    assertEquals(generation, Generation.Maxwell)
    val actual = words(code, generation).fold(fail(_), identity)

    assertEquals(actual.map(_._1.raw), Vec(8, 16, 24))
    assertEquals(
      actual.map(_._2.toVec),
      Vec(
        expected(8, 0x4c98078000870001L, 0x007f6)._2,
        expected(16, 0xf0c8000002570000L, 0x00711)._2,
        expected(24, 0xf0c8000002170002L, 0x0073f)._2
      )
    )
  }

  test("SM70 and newer preserve contiguous 128-bit words") {
    val code   = Array.tabulate[Byte](32)(_.toByte)
    val actual = words(code, Generation.Modern).fold(fail(_), identity)

    assertEquals(actual.map(_._1.raw), Vec(0, 16))
    assertEquals(actual.map(_._2.toVec), Vec(code.slice(0, 16).toVec, code.slice(16, 32).toVec))
  }

  test("split and join preserve arbitrary section bytes across bundle generations") {
    for ((generation, size) <- Vec((Generation.Kepler, 128), (Generation.Maxwell, 64), (Generation.Modern, 32))) {
      val code    = Array.tabulate[Byte](size)(index => (index * 37 + size).toByte)
      val section = CodeWords.split(code, generation).fold(fail(_), identity)
      assertEquals(CodeWords.join(section).fold(fail(_), _.toVec), code.toVec)
    }
  }

  test("edited Maxwell instruction and control fields survive reassembly") {
    val code                 = Array.tabulate[Byte](32)(index => (index * 19).toByte)
    val section              = CodeWords.split(code, Generation.Maxwell).fold(fail(_), identity)
    val bundle               = section.bundles.head
    val (controls, reserved) = bundle.control match {
      case CodeWords.BundleControl.Maxwell(slots, bit) => (slots, bit)
      case _                                           => fail("expected Maxwell bundle controls")
    }
    val editedControls = controls.updated(
      1,
      (controls(1).raw ^ 1).unsafe[CodeWords.ControlBits]
    )
    val editedWord   = bundle.words.head.copy(bytes = bundle.words.head.bytes.updated(0, 0x5a.toByte))
    val editedBundle = bundle.copy(
      control = CodeWords.BundleControl.Maxwell(editedControls, (reserved.raw ^ 1).unsafe[Int in (0, 1)]),
      words = bundle.words.updated(0, editedWord)
    )
    val edited  = section.copy(bundles = section.bundles.updated(0, editedBundle))
    val rebuilt = CodeWords.join(edited).fold(fail(_), identity)

    assertEquals(CodeWords.split(rebuilt, Generation.Maxwell), Right(edited))
  }

  test("join rejects invalid word addresses, widths, counts, and control values") {
    val section        = CodeWords.split(Array.fill[Byte](32)(0), Generation.Maxwell).fold(fail(_), identity)
    val bundle         = section.bundles.head
    val invalidAddress = bundle.copy(words =
      bundle.words.updated(
        0,
        bundle.words.head.copy(address = 9)
      )
    )
    val invalidWidth = bundle.copy(words =
      bundle.words.updated(
        0,
        bundle.words.head.copy(bytes = bundle.words.head.bytes.drop(1))
      )
    )
    val invalidCount = bundle.copy(words = bundle.words.drop(1))
    assert(0x200000.refine[CodeWords.ControlBits].isLeft)
    for (invalid <- Vec(invalidAddress, invalidWidth, invalidCount))
      assert(CodeWords.join(section.copy(bundles = Vec(invalid))).isLeft)
  }

  test("architectures map to their bundle generation and unsupported ones are rejected") {
    assertEquals(
      Vec("sm_35", "sm_37", "sm_50", "sm_53", "sm_60", "sm_62", "sm_70", "sm_89", "sm_121").map(sm =>
        Target.parse(sm).map(_.generation)
      ),
      (Vec.fill(2)(Generation.Kepler) ++ Vec.fill(4)(Generation.Maxwell) ++ Vec.fill(3)(Generation.Modern))
        .map(Right[String, Generation](_))
    )
    for (sm <- Vec("sm_30", "sm_34", "sm_54", "sm_122")) assert(Target.parse(sm).isLeft, sm)
  }

  test("rejects incomplete physical bundles") {
    assert(words(Array.fill[Byte](63)(0), Generation.Kepler).isLeft)
    assert(words(Array.fill[Byte](31)(0), Generation.Maxwell).isLeft)
    assert(words(Array.fill[Byte](15)(0), Generation.Modern).isLeft)
    assertEquals(words(Array.empty[Byte], Generation.Kepler), Right(Vec.empty[(Int >= 0, Array[Byte])]))
  }
}
