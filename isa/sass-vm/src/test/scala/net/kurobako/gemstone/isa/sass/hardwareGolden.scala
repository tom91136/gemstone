package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.testing.either.*
import net.kurobako.gemstone.vm.*

/** Hardware probe results, `inputs -> outputs` in hex words as the probe kit prints them, replayed through one SASS
  * snippet per row on a single thread.
  */
private object hardwareGolden extends munit.Assertions {
  import Harness.*

  case class Row(inputs: Vec[Long], outputs: Vec[Long], text: String)

  private def words(text: String): Vec[Long] =
    Vec.from(text.trim.split(' ').filter(_.nonEmpty).map(java.lang.Long.parseLong(_, 16)))

  def rows(table: String): Vec[Row] = Vec.from(table.stripMargin.linesIterator.map(_.trim).filter(_.nonEmpty).map {
    line =>
      val arrow = line.indexOf("->")
      Row(words(line.take(arrow)), words(line.drop(arrow + 2)), line)
  })

  /** A register that holds an input, with the mask of the probe's load: `R2` takes the word, `R2:u16` its low half. */
  private def input(spec: String, value: Long): (String, Long) = spec.split(':') match {
    case Array(register, "u16") => register -> (value & 0xffffL)
    case Array(register, "u8")  => register -> (value & 0xffL)
    case Array(register)        => register -> (value & 0xffffffffL)
    case _                      => fail(s"bad input register $spec")
  }

  private def compare(
      sm: Int,
      table: Vec[Row],
      count: Int,
      result: Either[Fault, Run],
      argument: Int,
      selected: Option[Vec[Vec[Long]]] = None
  )(using munit.Location): Unit = result match {
    case Left(fault) => fail(s"sm_$sm rejected or faulted on the hardware's instructions: ${fault.message}")
    case Right(run)  =>
      val produced = selected.getOrElse(run.words(argument).grouped(count).toVec)
      val wrong    = table.zip(produced).filter((row, words) => row.outputs != words)
      if (wrong.nonEmpty)
        fail(
          s"${wrong.size} of ${table.size} rows differ from the hardware:\n" + wrong.take(8).map((row, words) =>
            s"  ${row.text}\n  gemstone ${words.map(word => f"$word%08x").mkString(" ")}"
          ).mkString("\n")
        )
  }

  /** Constant banks by name (`constant2`) with their bytes in hex. */
  private def banks(constants: Vec[(String, String)]): Vec[(String, Vec[Byte])] =
    constants.map((name, hex) => s".nv.$name.kernel" -> Vec.unsafeWrapArray(bytes.hex(hex)))

  /** The probe kit's `op_kernel(in, out, n)` from the hardware's machine code `code`, one thread per row. */
  def checkKernel(sm: Int, code: String, table: Vec[Row], constants: Vec[(String, String)] = Vec.empty)(using
      munit.Location
  ): Unit = {
    val count  = table.head.outputs.size
    val kernel = fromHex(code, sm, "kernel").copy(constants = banks(constants))
    val inputs = table.flatMap(_.inputs)
    compare(
      sm,
      table,
      count,
      kernel.run(table.size)(Arg.words(inputs*), Arg.zeros(table.size * count * 4), Arg.u32(table.size.toLong)),
      1
    )
  }

  /** A kernel that takes only its output over `blocks` of 64 threads, with rows named by block and thread. */
  def checkGrid(sm: Int, code: String, blocks: Int, table: Vec[Row])(using munit.Location): Unit = {
    val threads = 64
    val count   = table.head.outputs.size
    val kernel  = fromHex(code, sm, "kernel")
    kernel.run(threads, blocks)(Arg.zeros(blocks * threads * count * 4)) match {
      case Left(fault)   => fail(s"sm_$sm rejected or faulted on the hardware's instructions: ${fault.message}")
      case Right(result) =>
        val words = result.words(0).grouped(count).toVec
        val rows  = table.map { row =>
          val Array(block, thread) = row.text.trim.split(' ').take(2).map(_.toInt): @unchecked
          row -> words(block * threads + thread)
        }
        compare(sm, rows.map(_._1), count, Right(result), 0, Some(rows.map(_._2)))
    }
  }

  /** Runs `body` once per row with the row's inputs moved into `inputs`, stores `outputs`, and compares every row. */
  def check(
      sm: Int,
      inputs: Vec[String],
      body: String,
      outputs: Vec[String],
      table: Vec[Row],
      constants: Vec[(String, String)] = Vec.empty
  )(using munit.Location): Unit = {
    val move  = if (sm < 70) "MOV32I" else "MOV"
    val count = outputs.size
    val code  = table.zipWithIndex.map { (row, index) =>
      val moves = inputs.zip(row.inputs).map { (spec, value) =>
        val (register, masked) = input(spec, value)
        f"$move $register, 0x$masked%x"
      }
      val stores = outputs.zipWithIndex.map { (register, slot) =>
        val offset = (index * count + slot) * 4
        if (sm < 70) f"STG.E [R40+0x$offset%x], $register" else f"STG.E [R40.64+0x$offset%x], $register"
      }
      (moves ++ Vec(body) ++ stores).mkString("; ")
    }
    val text   = s"MOV R40, c[0x0][0x160]; MOV R41, c[0x0][0x164]; ${code.mkString("; ")}; EXIT"
    val kernel = Kernel(sm, "kernel", Vec(Listing.program(text).value), constants = banks(constants))
    compare(sm, table, count, kernel.run(1)(Arg.zeros(table.size * count * 4)), 0)
  }
}
