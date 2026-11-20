package net.kurobako.gemstone.isa.amdgpu

import VMIR.*
import hardwareFlagTables.*
import hardwareGolden.descriptor
import laneRuns.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.testing.*

class HardwareFlagSuite extends Suite {
  private val gfx1036 = Target("gfx1036")

  private val kernel = descriptor(0xf)

  private val results: Vec[VectorRegisterIndex] = Vec(10, 11, 12, 13, 14, 15, 16)

  private val captures: Vec[Instruction] = {
    import InstructionSource.{VectorRegister as Register, *}
    Vec(
      Instruction.VMove(10, Scalar(20)),
      Instruction.VMove(11, Scalar(21)),
      Instruction.VMove(12, Scalar(8)),
      Instruction.VMove(13, Scalar(9)),
      Instruction.VMove(14, Scalar(14)),
      Instruction.VMove(15, Register(8)),
      Instruction.VMove(16, Register(9))
    )
  }

  private def setup(x: Long, y: Long, c: Long): Vec[Instruction] = {
    import InstructionSource.Constant
    Vec(
      Instruction.SMove(4, Constant(x)),
      Instruction.SMove(5, Constant(y)),
      Instruction.SMove(6, Constant(c)),
      Instruction.VMove(2, Constant(x)),
      Instruction.VMove(3, Constant(y)),
      Instruction.VMove(4, Constant(c))
    )
  }

  private def words(text: String): Vec[Long] =
    Vec.from(text.trim.split(' ').map(java.lang.Long.parseLong(_, 16)))

  private def rows(text: String): Vec[(Vec[Long], Vec[Long])] =
    Vec.from(text.linesIterator.map(_.trim).filter(_.nonEmpty).map { row =>
      val arrow = row.indexOf("->")
      words(row.take(arrow)) -> words(row.drop(arrow + 2))
    })

  private def hex(values: Vec[Long]): String = values.map(value => f"$value%08x").mkString(" ")

  private def report(checked: Vec[(Vec[Long], Vec[Long], Vec[Long])])(using munit.Location): Unit = {
    val wrong = checked.filter((_, expected, produced) => produced != expected)
    if (wrong.nonEmpty)
      fail(
        s"${wrong.size} of ${checked.size} rows differ from the hardware:\n" +
          wrong.map { (inputs, expected, produced) =>
            s"  ${hex(inputs)}: hardware ${hex(expected)}, gemstone ${hex(produced)}"
          }.mkString("\n")
      )
  }

  private def independent(x: Long, y: Long, lo: Long, hi: Long): Vec[Instruction] = {
    import InstructionSource.Constant
    Vec(
      Instruction.VMove(2, Constant(x)),
      Instruction.VMove(3, Constant(y)),
      Instruction.VMove(4, Constant(lo)),
      Instruction.VMove(5, Constant(hi))
    )
  }

  private val freshResults: Vec[VectorRegisterIndex] = Vec(10, 11, 12)

  private val freshCaptures: Vec[Instruction] = {
    import InstructionSource.{VectorRegister as Register, *}
    Vec(
      Instruction.VMove(10, Scalar(14)),
      Instruction.VMove(11, Register(8)),
      Instruction.VMove(12, Register(9))
    )
  }

  private val freshRows: Vec[(Vec[Long], Vec[Long])] = rows("""
    80000000 80000000 ffffffff 7fffffff -> 0 ffffffff bfffffff
    80000000 80000000 fffffffe 7fffffff -> 0 fffffffe bfffffff
    80000000 80000000 00000000 80000000 -> 1 00000000 c0000000
    80000000 80000000 00000001 80000000 -> 1 00000001 c0000000
    80000000 80000000 00000002 80000000 -> 1 00000002 c0000000
    80000000 80000000 ffffffff bfffffff -> 1 ffffffff ffffffff
    80000000 80000000 00000000 c0000000 -> 0 00000000 00000000
    80000000 80000000 00000001 c0000000 -> 0 00000001 00000000
    80000000 80000000 00000002 c0000000 -> 0 00000002 00000000
    80000000 7fffffff ffffffff 7fffffff -> 0 7fffffff 40000000
    80000000 7fffffff 00000000 80000000 -> 1 80000000 40000000
    80000000 7fffffff 00000001 80000000 -> 1 80000001 40000000
    80000000 7fffffff 80000000 3fffffff -> 0 00000000 00000000
    80000000 7fffffff 80000001 3fffffff -> 0 00000001 00000000
    ffffffff 80000000 00000000 80000000 -> 1 80000000 80000000
    ffffffff 80000000 00000001 80000000 -> 1 80000001 80000000
  """)

  for form <- forms do
    test(form.text) {
      val operations = program(gfx1036, octets((prologue ++ form.code ++ epilogue)*))
      report(rows(form.rows).map { (inputs, expected) =>
        val produced = laneWords(
          gfx1036,
          Vec(Vec.empty[Long]),
          setup(inputs(0), inputs(1), inputs(2)) ++ operations ++ captures,
          results,
          computePgmRsrc1 = kernel,
          base = 40
        ).head
        (inputs, expected, produced)
      })
    }

  test("v_mad_i64_i32 v[8:9], s14, v2, v3, v[4:5] on fresh rows") {
    val operations = program(gfx1036, octets(0x08, 0x0e, 0x77, 0xd5, 0x02, 0x07, 0x12, 0x04))
    report(freshRows.map { (inputs, expected) =>
      val produced = laneWords(
        gfx1036,
        Vec(Vec.empty[Long]),
        independent(inputs(0), inputs(1), inputs(2), inputs(3)) ++ operations ++ freshCaptures,
        freshResults,
        computePgmRsrc1 = kernel,
        base = 40
      ).head
      (inputs, expected, produced)
    })
  }
}
