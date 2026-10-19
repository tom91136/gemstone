package net.kurobako.gemstone.isa.sass

import java.nio.charset.StandardCharsets
import net.kurobako.gemstone.collection.*
import scala.util.Using

/** Every straight-line op_kernel op of the hardware probes, from `hardware/sm_<n>.txt`: the instructions between the
  * probe's loads and stores as `cuobjdump -sass` printed them from the binaries the hardware ran, the input word each
  * loaded register holds, the registers stored, and sampled `inputs -> outputs` rows.
  */
class HardwareTableSuite extends munit.FunSuite {
  import hardwareGolden.*

  private case class Op(
      probe: String,
      name: String,
      inputs: Vec[String],
      body: String,
      outputs: Vec[String],
      rows: Vec[Row]
  )

  // `R6=1:u16>16`: R6 holds the half at bit 16 of input word 1, zero-extended
  private val Input = raw"(R\d+)=(\d+)(?::([us])(8|16)(?:>(\d+))?)?".r

  private def loaded(spec: String, inputs: Vec[Long]): Long = spec match {
    case Input(_, word, sign, width, shift) =>
      Option(width).fold(inputs(word.toInt)) { width =>
        val bits  = width.toInt
        val value = (inputs(word.toInt) >>> Option(shift).fold(0)(_.toInt)) & ((1L << bits) - 1)
        if (sign == "s" && (value >>> (bits - 1)) == 1L) (value | (-1L << bits)) & 0xffffffffL else value
      }
    case other => fail(s"bad input $other")
  }

  private def ops(sm: Int): Vec[Op] = {
    val stream = Option(getClass.getResourceAsStream(s"/net/kurobako/gemstone/isa/sass/hardware/sm_$sm.txt"))
      .getOrElse(fail(s"no hardware table for sm_$sm"))
    val text = Using.resource(stream)(input => String(input.readAllBytes(), StandardCharsets.UTF_8))
    Vec.from(text.split("\n\n").map(_.trim).filter(_.startsWith("op ")).map { block =>
      val lines                            = block.linesIterator.toVector
      val Array(_, probe, name)            = lines(0).split(' '): @unchecked
      def field(line: String, key: String) = line.stripPrefix(s"$key ")
      val specs                            = Vec.from(field(lines(1), "inputs").split(' '))
      val table                            = rows(lines.drop(4).mkString("\n"))
      Op(
        probe,
        name,
        specs,
        field(lines(2), "body"),
        Vec.from(field(lines(3), "outputs").split(' ')),
        table.map(row => row.copy(inputs = specs.map(loaded(_, row.inputs))))
      )
    })
  }

  for {
    sm <- Vec(35, 50, 61, 70, 89, 90)
    op <- ops(sm)
  }
    test(s"sm_$sm hardware: ${op.probe} ${op.name}") {
      check(sm, op.inputs.map(_.takeWhile(_ != '=')), op.body, op.outputs, op.rows)
    }
}
