package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.testing.*

class SchedulingSuite extends Suite {
  test("kepler carries no scheduling") {
    assertEquals(Scheduling.decode(Generation.Kepler, BigInt(0x1c3c02)), Right(None))
  }

  test("maxwell control follows the 64 instruction bits") {
    val encoding = BigInt("1071fef17007c20c7090c", 16)
    val expected = Scheduling(
      15.unsafe[Int in 0 ~ 15],
      yieldHint = true,
      Some(0.unsafe[Int in 0 ~ 5]),
      None,
      0x20.unsafe[Int in 0 ~ 0x3f],
      0.unsafe[Int in 0 ~ 0xf]
    )
    assertEquals(Scheduling.decode(Generation.Maxwell, encoding), Right(Some(expected)))
  }

  test("volta control with a write barrier and no read barrier") {
    val encoding = BigInt("0e0f64000000540c00000006040c7236", 16)
    val expected = Scheduling(
      2.unsafe[Int in 0 ~ 15],
      yieldHint = true,
      Some(5.unsafe[Int in 0 ~ 5]),
      None,
      0x20.unsafe[Int in 0 ~ 0x3f],
      3.unsafe[Int in 0 ~ 0xf]
    )
    assertEquals(Scheduling.decode(Generation.Modern, encoding), Right(Some(expected)))
  }

  test("volta control with both barriers") {
    val encoding = BigInt("106400000000040000000002027229", 16)
    val expected = Scheduling(
      2.unsafe[Int in 0 ~ 15],
      yieldHint = true,
      Some(1.unsafe[Int in 0 ~ 5]),
      Some(0.unsafe[Int in 0 ~ 5]),
      0x1.unsafe[Int in 0 ~ 0x3f],
      0.unsafe[Int in 0 ~ 0xf]
    )
    assertEquals(Scheduling.decode(Generation.Modern, encoding), Right(Some(expected)))
  }

  test("reserved scoreboard 6 is rejected") {
    assertEquals(
      Scheduling.decode(Generation.Modern, BigInt(6) << 110),
      Left("reserved scoreboard 6 in SASS control")
    )
  }

  test("maxwell control of a fixed-latency producer") {
    val expected = Scheduling(
      6.unsafe[Int in 0 ~ 15],
      yieldHint = true,
      None,
      None,
      0x1.unsafe[Int in 0 ~ 0x3f],
      0.unsafe[Int in 0 ~ 0xf]
    )
    assertEquals(Scheduling.decode(Generation.Maxwell, BigInt(0xff6) << 64), Right(Some(expected)))
  }

  test("volta control of a fixed-latency producer") {
    val expected = Scheduling(
      8.unsafe[Int in 0 ~ 15],
      yieldHint = false,
      None,
      None,
      0x1.unsafe[Int in 0 ~ 0x3f],
      0.unsafe[Int in 0 ~ 0xf]
    )
    assertEquals(Scheduling.decode(Generation.Modern, BigInt(0xfe8) << 105), Right(Some(expected)))
  }
}
