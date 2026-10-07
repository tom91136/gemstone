package net.kurobako.gemstone.isa.sass.fixtures

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.testing.*

object FixtureMutations {
  enum Change {
    case Flip(bit: Int)
    case Field(shift: Int, width: Int, value: BigInt)
  }
  type Probe = (
      id: String,
      offset: Int,
      originalBytes: Vec[Byte],
      mutations: Vec[Change],
      expectedExitCode: Int,
      expectedOutput: String
  )

  private def probe(
      id: String,
      offset: Int,
      original: String,
      mutations: Vec[Change],
      expectedExitCode: Int,
      expectedOutput: String
  ): Probe = (
    id = id,
    offset = offset,
    originalBytes = bytes.hex(original).toVec,
    mutations = mutations,
    expectedExitCode = expectedExitCode,
    expectedOutput = expectedOutput
  )

  /** `nvdisasm` behaviour for edited instructions of the unoptimised sm_89 SAXPY cubin. */
  val saxpy: Vec[Probe] = Vec(
    probe("nop-unused-x-bit-127-set", 2848, "18790000000000000000000000c00f00", Vec(Change.Flip(127)), 0, "NOP;"),
    probe(
      "nop-src-rel-sb-zero",
      2848,
      "18790000000000000000000000c00f00",
      Vec(Change.Field(113, 3, BigInt(0))),
      0,
      "NOP;"
    ),
    probe(
      "nop-dst-wr-sb-zero",
      2848,
      "18790000000000000000000000c00f00",
      Vec(Change.Field(110, 3, BigInt(0))),
      0,
      "NOP;"
    ),
    probe(
      "exit-src-rel-sb-zero",
      2816,
      "4d790000000000000000800300de3f00",
      Vec(Change.Field(113, 3, BigInt(0))),
      1,
      "Illegal instruction found"
    ),
    probe(
      "exit-dst-wr-sb-zero",
      2816,
      "4d790000000000000000800300de3f00",
      Vec(Change.Field(110, 3, BigInt(0))),
      1,
      "Illegal instruction found"
    ),
    probe(
      "mov-const-bank-address",
      1792,
      "027a0100000a0000000f000000de3f00",
      Vec(Change.Field(54, 5, BigInt(3)), Change.Field(40, 14, BigInt(291))),
      0,
      "MOV R1, c[0x3][0x48c]"
    ),
    probe(
      "ldc-direct-byte-offset",
      1856,
      "827b020200580000000a0000001e3200",
      Vec(Change.Field(38, 16, BigInt(4660))),
      0,
      "LDC.64 R2, c[0x0][R2+0x1234]"
    )
  )

  def word(probe: Probe): BigInt =
    probe.mutations.foldLeft(BigInt(1, probe.originalBytes.toArray.reverse)) { (word, change) =>
      change match {
        case Change.Flip(bit)                  => word.flipBit(bit)
        case Change.Field(shift, width, value) =>
          val mask = ((BigInt(1) << width) - 1) << shift
          (word & ~mask) | (value << shift)
      }
    }

  def apply(base: Array[Byte], probe: Probe): Either[String, Array[Byte]] = {
    val original = probe.originalBytes.toArray
    if (probe.offset < 0 || probe.offset > base.length - original.length)
      Left(s"${probe.id}: invalid patch offset ${probe.offset}")
    else if (!base.slice(probe.offset, probe.offset + original.length).sameElements(original))
      Left(s"${probe.id}: base instruction bytes changed at ${probe.offset}")
    else {
      val changedWord = word(probe)
      val changed     = base.clone()
      original.indices.foreach(index => changed(probe.offset + index) = (changedWord >> (index * 8)).toByte)
      Right(changed)
    }
  }
}
