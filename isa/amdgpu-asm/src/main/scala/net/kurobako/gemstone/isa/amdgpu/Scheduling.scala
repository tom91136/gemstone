package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.*
import upickle.default.*

/** LLVM scheduling-model timing per mnemonic, keyed by the processor target. */
object Scheduling {
  case class Timing(latency: Int, cycles: Int, writes: String) {
    def trans32: Boolean = writes.split('+').contains("WriteTrans32")
  }

  private case class Table(
      model: String,
      entries: Map[String, (Int, Int, String)],
      lds: Map[String, (Int, Int, String)]
  ) derives ReadWriter

  private case class Catalogue(processors: Map[String, Int], tables: Vector[Table]) derives ReadWriter

  private lazy val timings: Map[String, Map[(String, Boolean), Timing]] = {
    val catalogue = resources.json[Catalogue](getClass, "scheduling.json")
    val tables    = catalogue.tables.map { table =>
      def keyed(rows: Map[String, (Int, Int, String)], lds: Boolean) =
        rows.map { case (mnemonic, (latency, cycles, writes)) => (mnemonic, lds) -> Timing(latency, cycles, writes) }
      keyed(table.entries, lds = false) ++ keyed(table.lds, lds = true)
    }
    catalogue.processors.map((name, index) => name -> tables(index))
  }

  /** The LLVM timing of `mnemonic` in the plain or `lds` form on `target`, or `None` when LLVM has no such record. */
  def timing(target: Target, mnemonic: String, lds: Boolean): Option[Timing] =
    timings.get(target.raw).flatMap { table =>
      table.get((mnemonic, lds)).orElse(if (lds) table.get((mnemonic, false)) else None)
    }

  def isVectorAlu(target: Target, opcode: Opcode): Boolean = opcode.encodings(target).exists(encoding =>
    encoding.format.isVector || encoding.format == EncodingFormat.VINTRP || encoding.format == EncodingFormat.VINTERP
  )
}
