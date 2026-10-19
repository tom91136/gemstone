package net.kurobako.gemstone.isa.amdgpu

import VMIR.*
import laneRuns.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.constraint.*

/** Hardware probe results replayed through the instructions the hardware ran, with each lane's registers from v2 on set
  * as the probe's loads set them.
  */
private object hardwareGolden extends munit.Assertions {

  /** An op on sampled lanes: `rows` holds `inputs -> outputs` in hex words as the probe kit prints them. */
  case class Golden(op: String, form: String, code: String, loads: String, outputs: String, rows: String)

  /** An op on lanes 0 to 63 of a shared input wave: `results` holds every lane's outputs in lane order. */
  case class WaveOp(op: String, form: String, code: String, loads: String, outputs: String, results: String)

  private case class Load(register: Int, word: Int, shift: Int, mask: Long, signed: Boolean)

  private def words(text: String): Vec[Long] =
    Vec.from(text.stripMargin.split(Array(' ', '\n')).filter(_.nonEmpty).map(java.lang.Long.parseLong(_, 16)))

  private def octets(code: String): Vec[Byte] = words(code).flatMap(LittleEndian.toVec(_, 4))

  // `2=0>16&ffffs`: v2 holds word 0 shifted right by 16, masked to 0xffff and sign-extended
  private val LoadSpec = raw"(\d+)=(\d+)(?:>(\d+))?(?:&([0-9a-f]+))?(s?)".r

  private def loads(spec: String): Vec[Load] = Vec.from(spec.split(' ').map {
    case LoadSpec(register, word, shift, mask, signed) =>
      Load(
        register.toInt,
        word.toInt,
        Option(shift).fold(0)(_.toInt),
        Option(mask).fold(0xffffffffL)(java.lang.Long.parseLong(_, 16)),
        signed.nonEmpty
      )
    case other => fail(s"bad load $other")
  })

  private def registers(spec: Vec[Load], inputs: Vec[Long]): Vec[Long] = {
    val top = spec.map(_.register).max
    Vec.tabulate(top - 1) { index =>
      spec.find(_.register == index + 2).fold(0L) { load =>
        val value = (inputs(load.word) >>> load.shift) & load.mask
        val width = java.lang.Long.bitCount(load.mask).unsafe[Int in 1 ~ 64]
        if (load.signed) Bits.signExtend(value, width) & 0xffffffffL else value
      }
    }
  }

  private def run(
      target: Target,
      wave32: Boolean,
      inputs: Vec[Vec[Long]],
      code: String,
      spec: String,
      outputs: String,
      expected: Vec[Vec[Long]],
      texts: Vec[String]
  )(using munit.Location): Unit = {
    val load    = loads(spec)
    val program = laneRuns.program(target, octets(code))
    val lanes   = inputs.map(registers(load, _))
    val stored  = Vec.from(outputs.split(' ').map(_.toInt))
    val results = stored.map(register =>
      laneResults(target, lanes, program, register.unsafe[VectorRegisterIndex], wave32 = wave32)
    )
    val produced = Vec.tabulate(inputs.size)(lane => results.map(_(lane)))
    val wrong    = inputs.indices.filter(lane => produced(lane) != expected(lane))
    if (wrong.nonEmpty)
      fail(
        s"${wrong.size} of ${inputs.size} lanes differ from the hardware:\n" + wrong.take(8).map(lane =>
          s"  lane $lane: ${texts(lane)}\n  gemstone ${produced(lane).map(word => f"$word%08x").mkString(" ")}"
        ).mkString("\n")
      )
  }

  def check(target: Target, wave32: Boolean, golden: Golden)(using munit.Location): Unit = {
    val rows              = Vec.from(golden.rows.stripMargin.linesIterator.map(_.trim).filter(_.nonEmpty))
    val (inputs, outputs) = rows.map { row =>
      val arrow = row.indexOf("->")
      words(row.take(arrow)) -> words(row.drop(arrow + 2))
    }.unzip
    run(target, wave32, inputs, golden.code, golden.loads, golden.outputs, outputs, rows)
  }

  def check(target: Target, lanes: String, op: WaveOp)(using munit.Location): Unit = {
    val inputs   = Vec.from(lanes.stripMargin.linesIterator.map(_.trim).filter(_.nonEmpty)).map(words(_))
    val count    = op.outputs.split(' ').length
    val expected = words(op.results).grouped(count).toVec
    val texts    = inputs.zip(expected).map((in, out) =>
      s"${in.map(word => f"$word%08x").mkString(" ")} -> ${out.map(word => f"$word%08x").mkString(" ")}"
    )
    run(target, wave32 = false, inputs, op.code, op.loads, op.outputs, expected, texts)
  }
}
