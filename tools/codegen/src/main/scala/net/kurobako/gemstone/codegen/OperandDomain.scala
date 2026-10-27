package net.kurobako.gemstone.codegen

import net.kurobako.gemstone.isa.catalogue.*
import net.kurobako.gemstone.string.*
import upickle.default.*

case class DomainFinding(check: String, subject: String, detail: String, resolution: Option[String])

object OperandDomain {
  case class FloatConstant(text: String, code: Int) derives ReadWriter
  case class SubField(name: String, ranges: Vector[BitRange], domain: OperandDomain) derives ReadWriter

  val RegisterFiles: Map[String, String] = Map(
    "sgpr"    -> "s",
    "ttmp"    -> "ttmp",
    "vgpr"    -> "v",
    "accvgpr" -> "acc",
    "r"       -> "R",
    "ur"      -> "UR",
    "b"       -> "B",
    "sb"      -> "SB"
  )

  private val RegisterName = "(s|ttmp|v|acc)([0-9]+)".r
  private val NumberedName = "(attr|mrt|pos|param|dual_src_blend|p)([0-9]+)".r
  private val IntegerName  = "-?[0-9]+".r
  private val FloatName    = raw"-?[0-9]+\.[0-9]+".r
  private val LiteralName  = "src_literal([0-9]*)".r
  private val Marker       = "src_simm([0-9]+)".r
  private val FileOf       = RegisterFiles.map(_.swap)

  /** Builds the smallest domain list for `(name, code)` pairs; consecutive registers and numbers become ranges. */
  def compress(values: Vector[(String, Int)]): Vector[OperandDomain] = {
    val entries   = values.map((name, code) => name.toLowerCaseRoot -> code)
    val registers = entries.collect { case (RegisterName(prefix, index), code) => (FileOf(prefix), index.toInt, code) }
      .groupBy(_._1).toVector.flatMap { case (file, items) =>
        ascendingRuns(items.sortBy(_._2).map(item => item._2 -> item._3)).map { run =>
          Registers(file, first = run.head._1, last = run.last._1, code = run.head._2)
        }
      }
    val numbered = entries.collect { case (NumberedName(prefix, index), code) => (prefix, index.toInt, code) }
      .groupBy(_._1).toVector.flatMap { case (prefix, items) =>
        ascendingRuns(items.sortBy(_._2).map(item => item._2 -> item._3)).map { run =>
          NamedRange(prefix, first = run.head._1, last = run.last._1, code = run.head._2)
        }
      }
    val integers = signedRuns(
      entries.collect { case (name @ IntegerName(), code) => name.toInt -> code }.sortBy(_._2)
    ).map(run => IntegerRange(first = run.head._1, last = run.last._1, code = run.head._2))
    val floats   = entries.collect { case (name @ FloatName(), code) => FloatConstant(name, code) }.sortBy(_.code)
    val literals = entries.collect { case (LiteralName(bits), code) =>
      Literal(bytes = if (bits.isEmpty) 4 else bits.toInt / 8, code = code)
    }
    val handled = (name: String) =>
      RegisterName.matches(name) || NumberedName.matches(name) || IntegerName.matches(name) ||
        FloatName.matches(name) || LiteralName.matches(name) || Marker.matches(name)
    val named = entries.collect { case (name, code) if !handled(name) => Named(name, code) }
    val parts = registers ++ numbered ++ integers ++ Option.when(floats.nonEmpty)(FloatSet(floats)) ++ literals ++ named
    parts.sortBy(firstCode(_))
  }

  /** The immediate widths named by `src_simm<N>` marker values. */
  def markers(values: Vector[(String, Int)]): Vector[OperandDomain] = values.map(_._1.toLowerCaseRoot)
    .collect { case Marker(bits) => Immediate(bits.toInt, signed = false) }.distinct

  def firstCode(domain: OperandDomain): Int = domain.codes.map(_._2).minOption.getOrElse(Int.MaxValue)

  def union(members: Vector[OperandDomain]): OperandDomain = members match {
    case Vector(single) => single
    case Vector()       => Reserved
    case _              => Union(members.flatMap { case Union(inner) => inner; case other => Vector(other) })
  }

  def requireResolved(isa: String, open: Vector[DomainFinding]): Unit =
    if (open.nonEmpty)
      throw IllegalStateException(
        s"${open.size} unresolved $isa operand domain disagreements:\n" +
          open.take(40).map(f => s"  ${f.check} ${f.subject}: ${f.detail}").mkString("\n")
      )

  private def ascendingRuns(items: Vector[(Int, Int)]): Vector[Vector[(Int, Int)]] =
    items.foldLeft(Vector.empty[Vector[(Int, Int)]]) { (acc, item) =>
      acc.lastOption match {
        case Some(run) if continuesAscending(run, item) => acc.init :+ (run :+ item)
        case _                                          => acc :+ Vector(item)
      }
    }

  private def signedRuns(items: Vector[(Int, Int)]): Vector[Vector[(Int, Int)]] =
    items.foldLeft(Vector.empty[Vector[(Int, Int)]]) { (acc, item) =>
      acc.lastOption match {
        case Some(run) if continuesSigned(run, item) => acc.init :+ (run :+ item)
        case _                                       => acc :+ Vector(item)
      }
    }

  private def continuesAscending(run: Vector[(Int, Int)], item: (Int, Int)): Boolean = {
    val (index, code) = run.last
    item._2 == code + 1 && item._1 == index + 1
  }

  private def continuesSigned(run: Vector[(Int, Int)], item: (Int, Int)): Boolean = {
    val (index, code) = run.last
    val direction     = if (run.size == 1) item._1 - index else index - run(run.size - 2)._1
    item._2 == code + 1 && item._1 - index == direction && (direction == 1 || direction == -1)
  }
}

/** The values an operand field accepts, independent of any instruction set. Codes are encoded field values. */
enum OperandDomain derives ReadWriter, CanEqual {

  /** `first..last` registers of one file, encoded from `code` upwards; `width` is the register size in bits. */
  case Registers(file: String, first: Int, last: Int, code: Int, alignment: Int = 1, width: Int = 32)

  case Named(name: String, code: Int)

  /** Numbered names `prefix<first>..prefix<last>` encoded from `code` upwards. */
  case NamedRange(prefix: String, first: Int, last: Int, code: Int)

  /** Inline integers from `first` to `last`, stepping towards `last`, encoded from `code` upwards. */
  case IntegerRange(first: Int, last: Int, code: Int)

  case FloatSet(constants: Vector[OperandDomain.FloatConstant])

  /** A trailing literal of `bytes` bytes selected by `code`. */
  case Literal(bytes: Int, code: Int)

  /** An inline field; absent bounds mean the full range of `bits`. */
  case Immediate(bits: Int, signed: Boolean, min: Option[Long] = None, max: Option[Long] = None)

  case Partitioned(fields: Vector[OperandDomain.SubField])

  case Union(members: Vector[OperandDomain])

  case Ignored

  case Reserved

  /** A format field no operand of the instruction set describes, such as a modifier or flag. */
  case Unmodelled

  /** Every named code, in no particular order; empty for domains without named codes. */
  def codes: Vector[(String, Int)] = this match {
    case Registers(file, first, last, code, _, _) =>
      val prefix = OperandDomain.RegisterFiles(file)
      Vector.tabulate(last - first + 1)(offset => s"$prefix${first + offset}" -> (code + offset))
    case Named(name, code)                  => Vector(name -> code)
    case NamedRange(prefix, first, last, c) => Vector.tabulate(last - first + 1)(i => s"$prefix${first + i}" -> (c + i))
    case IntegerRange(first, last, code)    =>
      val step = if (last >= first) 1 else -1
      Vector.tabulate(math.abs(last - first) + 1)(offset => s"${first + step * offset}" -> (code + offset))
    case FloatSet(constants)  => constants.map(constant => constant.text -> constant.code)
    case Literal(bytes, code) => Vector((if (bytes == 4) "src_literal" else s"src_literal${bytes * 8}") -> code)
    case Union(members)       => members.flatMap(_.codes)
    case _                    => Vector.empty
  }

  def isLiteral: Boolean = this match {
    case Literal(_, _) => true
    case Union(values) => values.exists(_.isLiteral)
    case _             => false
  }
}
