package net.kurobako.gemstone.isa.ptx

import java.nio.charset.StandardCharsets
import net.kurobako.gemstone.collection.*
import scala.util.Using

/** Every straight-line op_kernel op of the hardware probes, from `hardware/sm_<n>.txt`: the op entry's PTX between its
  * loads and stores, from the PTX embedded in the binaries the hardware ran (`cuobjdump -xptx all`), with the loads and
  * stores replaced by moves from `%hwi` and to `%hwo`, and sampled `inputs -> outputs` rows, or the first warp's rows
  * for an op that needs the whole warp.
  */
class HardwareTableSuite extends munit.FunSuite {
  import hardwareGolden.*

  private case class Op(probe: String, name: String, target: String, registers: String, body: String, rows: Vec[Row])

  private def ops(sm: Int): Vec[Op] = {
    val stream = Option(getClass.getResourceAsStream(s"/net/kurobako/gemstone/isa/ptx/hardware/sm_$sm.txt"))
      .getOrElse(fail(s"no hardware table for sm_$sm"))
    val text = Using.resource(stream)(input => String(input.readAllBytes(), StandardCharsets.UTF_8))
    Vec.from(text.split("\n\n").map(_.trim).filter(_.startsWith("op ")).map { block =>
      val lines                         = block.linesIterator.toVector
      val Array(_, probe, name, target) = lines(0).split(' '): @unchecked
      Op(
        probe,
        name,
        target,
        lines(1).stripPrefix("registers "),
        lines(2).stripPrefix("body "),
        rows(lines.drop(3).mkString("\n"))
      )
    })
  }

  for {
    sm <- Vec(35, 50, 61, 70, 89, 90, 120)
    op <- ops(sm)
  }
    test(s"sm_$sm hardware: ${op.probe} ${op.name}") {
      checkCore(op.target, op.registers, op.body, op.rows)
    }
}
