package net.kurobako.gemstone.isa.sass.fixtures

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*

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

  def read(metadata: String): Vec[Probe] =
    ujson.read(metadata)("probes").arr.toVec.map { probe =>
      val expected = probe("expected")
      val original = FixtureBytes.readHex(probe("base_instruction_hex").str).toVec
      require(original.size == 16, s"${probe("id").str}: expected a 128-bit instruction")
      val mutations = probe.obj.get("mutations").map(_.arr.toVec).getOrElse(Vec(probe("mutation")))
      (
        id = probe("id").str,
        offset = probe("offset").num.toInt,
        originalBytes = original,
        mutations = mutations.map { change =>
          change.obj.get("bit") match {
            case Some(bit) => Change.Flip(bit.num.toInt)
            case None      => Change.Field(
                change("shift").num.toInt,
                change("width").num.toInt,
                BigInt(change("value").num.toLong)
              )
          }
        },
        expectedExitCode = expected("nvdisasm_exit_code").num.toInt,
        expectedOutput = expected.obj.getOrElse("text", expected("diagnostic")).str
      )
    }

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
