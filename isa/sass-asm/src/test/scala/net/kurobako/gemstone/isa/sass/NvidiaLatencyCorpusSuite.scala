package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.profile.NvidiaLatency
import net.kurobako.gemstone.profile.NvidiaLatency.Producer.*
import net.kurobako.gemstone.testing.*

class NvidiaLatencyCorpusSuite extends Suite {

  private val corpus: Map[Int, Map[NvidiaLatency.Producer, Int]] = Map(
    35 -> Map.empty,
    37 -> Map.empty,
    50 -> Map(Fadd32 -> 0xff6, Fmul32 -> 0xff6, Ffma32 -> 0x40ff6, Iadd32 -> 0xff6, Lop32 -> 0xff6),
    52 -> Map(Fadd32 -> 0xff6, Fmul32 -> 0xff6, Ffma32 -> 0x40ff6, Iadd32 -> 0xff6, Lop32 -> 0xff6),
    53 -> Map(Fadd32 -> 0xff6, Fmul32 -> 0xff6, Ffma32 -> 0x40ff6, Iadd32 -> 0xff6, Lop32 -> 0xff6),
    60 -> Map(Fadd32 -> 0xff6, Fmul32 -> 0xff6, Ffma32 -> 0x40ff6, Iadd32 -> 0xff6, Lop32 -> 0xff6),
    61 -> Map(Fadd32 -> 0xff6, Fmul32 -> 0xff6, Ffma32 -> 0x40ff6, Iadd32 -> 0xff6, Lop32 -> 0xff6),
    62 -> Map(Fadd32 -> 0xff6, Fmul32 -> 0xff6, Ffma32 -> 0x40ff6, Iadd32 -> 0xff6, Lop32 -> 0xff6),
    70 -> Map(Fadd32 -> 0xfe4, Fmul32 -> 0xfe4, Ffma32 -> 0xfe4, Iadd32 -> 0xfe4, Imul32 -> 0xfe4, Lop32 -> 0xfe4),
    72 -> Map(Fadd32 -> 0xfe4, Fmul32 -> 0xfe4, Ffma32 -> 0xfe4, Iadd32 -> 0xfe4, Imul32 -> 0xfe4, Lop32 -> 0xfe4),
    75 -> Map(
      Fadd32 -> 0xfe4,
      Fmul32 -> 0xfe4,
      Ffma32 -> 0xfe4,
      Iadd32 -> 0xfe4,
      Imul32 -> 0xfe4,
      Lop32  -> 0xfe4,
      Shl32  -> 0xfe4
    ),
    80 -> Map(Fadd32 -> 0xfe4, Fmul32 -> 0xfe4, Ffma32 -> 0xfe4, Iadd32 -> 0xfe4, Imul32 -> 0xfe4, Lop32 -> 0xfe4),
    86 -> Map(
      Fadd32 -> 0xfe4,
      Fmul32 -> 0xfe4,
      Ffma32 -> 0xfe4,
      Iadd32 -> 0xfe4,
      Imul32 -> 0xfe4,
      Lop32  -> 0xfe4,
      I2f32  -> 0xfe5
    ),
    87 -> Map(
      Fadd32 -> 0xfe4,
      Fmul32 -> 0xfe4,
      Ffma32 -> 0xfe4,
      Iadd32 -> 0xfe4,
      Imul32 -> 0xfe4,
      Lop32  -> 0xfe4,
      I2f32  -> 0xfe5
    ),
    88 -> Map(
      Fadd32 -> 0xfe4,
      Fmul32 -> 0xfe4,
      Ffma32 -> 0xfe4,
      Iadd32 -> 0xfe4,
      Imul32 -> 0xfe4,
      Lop32  -> 0xfe4,
      I2f32  -> 0xfe5
    ),
    89 -> Map(
      Fadd32 -> 0xfe4,
      Fmul32 -> 0xfe4,
      Ffma32 -> 0xfe4,
      Iadd32 -> 0xfe4,
      Imul32 -> 0xfe4,
      Lop32  -> 0xfe4,
      I2f32  -> 0xfe5
    ),
    90  -> Map(Shl32 -> 0xfe4, Fma64 -> 0xfe8, I2f32 -> 0xfe5),
    100 -> Map(
      Fadd32 -> 0xfe4,
      Fmul32 -> 0xfe4,
      Ffma32 -> 0xfe4,
      Iadd32 -> 0xfe4,
      Imul32 -> 0xfe4,
      Lop32  -> 0xfe4,
      Shl32  -> 0xfe4,
      I2f32  -> 0xfe5
    ),
    101 -> Map(
      Fadd32 -> 0xfe4,
      Fmul32 -> 0xfe4,
      Ffma32 -> 0xfe4,
      Iadd32 -> 0xfe4,
      Imul32 -> 0xfe4,
      Lop32  -> 0xfe4,
      Shl32  -> 0xfe4,
      I2f32  -> 0xfe5
    ),
    103 -> Map(
      Fadd32 -> 0xfe4,
      Fmul32 -> 0xfe4,
      Ffma32 -> 0xfe4,
      Iadd32 -> 0xfe4,
      Imul32 -> 0xfe4,
      Lop32  -> 0xfe4,
      Shl32  -> 0xfe4,
      I2f32  -> 0xfe5
    ),
    110 -> Map(
      Fadd32 -> 0xfe4,
      Fmul32 -> 0xfe4,
      Ffma32 -> 0xfe4,
      Iadd32 -> 0xfe4,
      Imul32 -> 0xfe4,
      Lop32  -> 0xfe4,
      Shl32  -> 0xfe4,
      I2f32  -> 0xfe5
    ),
    120 -> Map(Fadd32 -> 0xfe4, Fmul32 -> 0xfe4, Imul32 -> 0xfe4, Iadd32 -> 0xfe5, Lop32 -> 0xfe5, I2f32 -> 0xfe5),
    121 -> Map(Fadd32 -> 0xfe4, Fmul32 -> 0xfe4, Imul32 -> 0xfe4, Iadd32 -> 0xfe5, Lop32 -> 0xfe5, I2f32 -> 0xfe5)
  )

  test("the corpus lists exactly the producers with a fixed latency") {
    val defined = (0 to 130).flatMap { sm =>
      NvidiaLatency.Producer.values.filter(NvidiaLatency.fixed(sm, _).isDefined).map(sm -> _)
    }
    val listed = corpus.toSeq.flatMap((sm, rows) => rows.keys.map(sm -> _))
    assertEquals(defined.toSet, listed.toSet)
  }

  test("harvested control words decode to the fixed latency of their producer") {
    corpus.foreach { (sm, rows) =>
      val (generation, shift) = if sm < 70 then (Generation.Maxwell, 64) else (Generation.Modern, 105)
      rows.foreach { (producer, word) =>
        val expected = NvidiaLatency.fixed(sm, producer).map(_.value)
        val decoded  = Scheduling.decode(generation, BigInt(word) << shift)
        assertEquals(decoded.map(_.map(s => (s.stall.raw, s.writeBarrier))), Right(expected.map(_ -> None)))
      }
    }
  }
}
