package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.constraint.*

class SASSCodeWordsSuite extends munit.FunSuite {
  private def bytes(words: Long*): Array[Byte] = words.toArray.flatMap(LittleEndian.toArray(_, 8))

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
    val actual = SASSCodeWords.words(code, 35).fold(fail(_), identity)

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
    val actual = SASSCodeWords.words(code, 50).fold(fail(_), identity)

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
    val actual = SASSCodeWords.words(code, 60).fold(fail(_), identity)

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
    val actual = SASSCodeWords.words(code, 70).fold(fail(_), identity)

    assertEquals(actual.map(_._1.raw), Vec(0, 16))
    assertEquals(actual.map(_._2.toVec), Vec(code.slice(0, 16).toVec, code.slice(16, 32).toVec))
  }

  test("split and join preserve arbitrary section bytes across bundle generations") {
    for ((sm, size) <- Vec((35, 128), (50, 64), (60, 64), (70, 32), (89, 32))) {
      val code    = Array.tabulate[Byte](size)(index => (index * 37 + sm).toByte)
      val section = SASSCodeWords.split(code, sm).fold(fail(_), identity)
      assertEquals(SASSCodeWords.join(section).fold(fail(_), _.toVec), code.toVec)
    }
  }

  test("edited Maxwell instruction and control fields survive reassembly") {
    val code                 = Array.tabulate[Byte](32)(index => (index * 19).toByte)
    val section              = SASSCodeWords.split(code, 50).fold(fail(_), identity)
    val bundle               = section.bundles.head
    val (controls, reserved) = bundle.control match {
      case SASSCodeWords.BundleControl.Maxwell(slots, bit) => (slots, bit)
      case _                                               => fail("expected Maxwell bundle controls")
    }
    val editedControls = controls.updated(
      1,
      SASSCodeWords.ControlWord(
        (controls(1).value.raw ^ 1).unsafe[SASSCodeWords.ControlBits]
      )
    )
    val editedWord   = bundle.words.head.copy(bytes = bundle.words.head.bytes.updated(0, 0x5a.toByte))
    val editedBundle = bundle.copy(
      control = SASSCodeWords.BundleControl.Maxwell(editedControls, (reserved.raw ^ 1).unsafe[Int in (0, 1)]),
      words = bundle.words.updated(0, editedWord)
    )
    val edited  = section.copy(bundles = section.bundles.updated(0, editedBundle))
    val rebuilt = SASSCodeWords.join(edited).fold(fail(_), identity)

    assertEquals(SASSCodeWords.split(rebuilt, 50), Right(edited))
  }

  test("join rejects invalid word addresses, widths, counts, and control values") {
    val section        = SASSCodeWords.split(Array.fill[Byte](32)(0), 50).fold(fail(_), identity)
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
    assert(0x200000.refine[SASSCodeWords.ControlBits].isLeft)
    for (invalid <- Vec(invalidAddress, invalidWidth, invalidCount))
      assert(SASSCodeWords.join(section.copy(bundles = Vec(invalid))).isLeft)
  }

  test("rejects unsupported architectures and incomplete physical bundles") {
    assert(SASSCodeWords.words(Array.empty[Byte], 34).isLeft)
    assert(SASSCodeWords.words(Array.empty[Byte], 122).isLeft)
    assert(SASSCodeWords.words(Array.fill[Byte](63)(0), 35).isLeft)
    assert(SASSCodeWords.words(Array.fill[Byte](31)(0), 50).isLeft)
    assert(SASSCodeWords.words(Array.fill[Byte](15)(0), 89).isLeft)
    assertEquals(SASSCodeWords.words(Array.empty[Byte], 35), Right(Vec.empty[(Int >= 0, Array[Byte])]))
  }
}
