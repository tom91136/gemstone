package net.kurobako.gemstone.isa.nvidia

import net.kurobako.gemstone.isa.nvidia.VMIR.MufuFlavour.*

/** The special-function unit's approximations, against the hardware's own results.
  *
  * The rows are the probe results of the GPUs the repository's hardware tables were taken from, in the order of
  * `isa/ptx-vm/src/test/resources/net/kurobako/gemstone/isa/ptx/hardware/sm_*.txt`.
  */
class MufuSuite extends munit.FunSuite {

  private def check(name: String, rows: String)(function: Long => Long): Unit = {
    val wrong = rows.stripMargin.linesIterator.map(_.trim).filter(_.nonEmpty).flatMap { line =>
      val arrow    = line.indexOf("->")
      val input    = java.lang.Long.parseLong(line.take(arrow).trim, 16)
      val expected = java.lang.Long.parseLong(line.substring(arrow + 2).trim, 16)
      val produced = function(input)
      Option.when(produced != expected)(s"  $line\n  gemstone ${f"$produced%08x"}")
    }.toList
    if (wrong.nonEmpty) fail(s"${wrong.size} $name rows differ:\n" + wrong.take(8).mkString("\n"))
  }

  // Quadro K620 sm_50, driver 580, CUDA 12.8
  test("the unit's base-2 exponential matches the hardware") {
    check(
      "ex2",
      """
        |bf000000 -> 3f3504f2
        |3f78f767 -> 3ffb2bba
        |c25be958 -> 2401fa67
        |41d5a0d9 -> 4cd07283
        |c2990000 -> 193504f2
        |00000000 -> 3f800000
        |4effffff -> 7f800000
        |2f800000 -> 3f800000
        |b7a6fb6f -> 3f7fff19
        |c2640000 -> 23000000
        |"""
    )(Mufu.exponent2)
  }

  test("the unit's base-2 logarithm matches the hardware") {
    check(
      "lg2",
      """
        |00800000 -> c2fbffff
        |6ffb2b0e -> 42c1f1ec
        |42292ad7 -> 40acdfb3
        |3a58f3ce -> c123d1fb
        |42580000 -> 40b82809
        |00000000 -> ff800000
        |4b000000 -> 41b80000
        |3fb504f3 -> 3effffff
        |42a70000 -> 40cc474e
        |4b000001 -> 41b80000
        |"""
    )(Mufu.logarithm2)
  }

  test("the unit's reciprocal matches the hardware") {
    check(
      "rcp",
      """
        |461c4000 -> 38d1b718
        |bcd513e8 -> c219c8c3
        |44321758 -> 3ab7fee1
        |1d9bcb1e -> 6152546d
        |c2a50000 -> bc46980d
        |00000000 -> 7f800000
        |4b800000 -> 33800000
        |7e800000 -> 00800000
        |c2aa0000 -> bc40c0c1
        |71157e49 -> 0ddb319b
        |"""
    )(Mufu.reciprocal)
  }

  test("the unit's reciprocal square root matches the hardware") {
    check(
      "rsqrt",
      """
        |00000001 -> 64b504f2
        |03f3fc1e -> 5d396c47
        |710bf0da -> 26ad1fcb
        |41a40000 -> 3e6229ec
        |42b10000 -> 3dd9b32f
        |00000000 -> 7f800000
        |4effffff -> 37b504f4
        |7e800000 -> 20000000
        |c3140000 -> 7fffffff
        |c2cd0000 -> 7fffffff
        |"""
    )(Mufu.reciprocalSquareRoot)
  }

  test("the unit's square root matches the hardware") {
    check(
      "sqrt",
      """
        |40135d8e -> 3fc23b0e
        |4732a576 -> 4355da94
        |42060000 -> 40b936a0
        |00000000 -> 00000000
        |ffffffff -> 7fffffff
        |7559f880 -> 5a6c38a7
        |"""
    )(Mufu.squareRoot(_, Modern))
  }

  test("the unit's sine matches the hardware") {
    check(
      "sin",
      """
        |00000001 -> 00000000
        |c013287d -> bf3f0314
        |4242c75b -> bf7fffff
        |00000000 -> 00000000
        |7f800001 -> 7fffffff
        |bfaadc0e -> bf78e816
        |"""
    )(Mufu.sine(_, Legacy))
  }

  test("the unit's cosine matches the hardware") {
    check(
      "cos",
      """
        |3f800000 -> 3f0a5141
        |47443bcb -> be1caec3
        |c2cc0000 -> 3dd00e33
        |00000000 -> 3f800000
        |7f800001 -> 7fffffff
        |426231d7 -> 3f800000
        |"""
    )(Mufu.cosine(_, Legacy))
  }
}
