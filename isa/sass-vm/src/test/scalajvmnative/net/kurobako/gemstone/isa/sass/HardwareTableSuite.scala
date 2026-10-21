package net.kurobako.gemstone.isa.sass

import java.nio.charset.StandardCharsets
import net.kurobako.gemstone.collection.*
import scala.util.Using

/** Every straight-line op_kernel op of the hardware probes, from `hardware/sm_<n>.txt`: the instructions between the
  * probe's loads and stores as `cuobjdump -sass` printed them from the binaries the hardware ran, the input word each
  * loaded register holds, the registers stored, and sampled `inputs -> outputs` rows. Whole-warp ops are `kernel`
  * blocks: the op_kernel's machine code from the probe's cubin, the constant banks it reads, and its first warp.
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

  private case class WarpKernel(probe: String, name: String, code: String, banks: Vec[(String, String)], rows: Vec[Row])

  private def blocks(sm: Int, kind: String): Vec[String] = {
    val stream = Option(getClass.getResourceAsStream(s"/net/kurobako/gemstone/isa/sass/hardware/sm_$sm.txt"))
      .getOrElse(fail(s"no hardware table for sm_$sm"))
    val text = Using.resource(stream)(input => String(input.readAllBytes(), StandardCharsets.UTF_8))
    Vec.from(text.split("\n\n").map(_.trim).filter(_.startsWith(s"$kind ")))
  }

  private def kernels(sm: Int): Vec[WarpKernel] = blocks(sm, "kernel").map { block =>
    val lines                 = block.linesIterator.toVector
    val Array(_, probe, name) = lines(0).split(' '): @unchecked
    val (banks, table)        = lines.drop(2).span(_.startsWith("bank "))
    def bank(line: String)    = line.split(' ') match {
      case Array(_, label, hex) => label -> hex
      case _                    => fail(s"bad bank $line")
    }
    WarpKernel(probe, name, lines(1).stripPrefix("code "), Vec.from(banks.map(bank(_))), rows(table.mkString("\n")))
  }

  private def ops(sm: Int): Vec[Op] =
    blocks(sm, "op").map { block =>
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
    }

  private val Tables = Vec(35, 50, 61, 70, 89, 90, 120)

  for {
    sm <- Tables
    op <- ops(sm)
  }
    test(s"sm_$sm hardware: ${op.probe} ${op.name}") {
      check(sm, op.inputs.map(_.takeWhile(_ != '=')), op.body, op.outputs, op.rows)
    }

  for {
    sm     <- Tables
    kernel <- kernels(sm)
  }
    test(s"sm_$sm hardware: ${kernel.probe} ${kernel.name} first warp") {
      checkKernel(sm, kernel.code, kernel.rows, kernel.banks)
    }
}
