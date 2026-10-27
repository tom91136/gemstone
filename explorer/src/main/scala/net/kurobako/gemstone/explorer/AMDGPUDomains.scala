package net.kurobako.gemstone.explorer

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.isa.amdgpu.*
import net.kurobako.gemstone.isa.amdgpu.asm.*
import net.kurobako.gemstone.isa.amdgpu.domain.*

/** The values an operand field of an AMDGPU encoding takes, written as a one-line summary and a list of every value
  * with its codes. Registers and inline integers appear as runs.
  */
private object AMDGPUDomains {

  private val Ignored    = "ignored"
  private val Unmodelled = "unmodelled"

  /** The domain of `field` in `value`; none for fields the catalogue gives no operand domain and for fields the opcode
    * does not read.
    */
  def of(value: Metadata.Encoding, field: EncodingField, bits: Int): Option[View.Domain] =
    DomainCatalogue.field(value.isa, value.format, value.opcode, field).flatMap { table =>
      table.id match {
        case Ignored    => None
        case Unmodelled => Some(View.Domain(s"$bits-bit field, values not modelled", Vec.empty))
        case _          => Some(describe(table, value.operands.find(_.field.contains(field)).fold(0)(_.bits)))
      }
    }

  private def describe(table: DomainTable, operandBits: Int): View.Domain =
    if (table.runs.isEmpty) View.Domain(s"${table.id} (packed sub-fields)", Vec.empty)
    else View.Domain(summary(table.runs), rows(table, operandBits))

  private def prefix(file: RegisterFile): String =
    if (file == RegisterFile.Accumulator) "a" else DomainTable.prefix(file)

  private def span(name: String, first: Int, last: Int): String =
    if (first == last) s"$name$first" else if (name.length > 1) s"$name$first-$last" else s"$name$first-$name$last"

  private def inlineRange(integers: Vector[Run.Integers]): String = {
    val ranges = Vec.runs(integers.flatMap(run => math.min(run.first, run.last) to math.max(run.first, run.last)))
    ranges.map((from, to) => if (from == to) from.toString else s"$from..$to").mkString("inline ", ", ", "")
  }

  private def paired(names: Vector[String]): Vector[String] = {
    val high = names.indices.filter(at =>
      at > 0 && names(at - 1).endsWith("_lo") && names(at) == names(at - 1).stripSuffix("_lo") + "_hi"
    ).toSet
    names.indices.filterNot(high).map(at => if (high(at + 1)) s"${names(at)}/hi" else names(at)).toVector
  }

  private def summary(runs: Vector[Run]): String = {
    val integers     = runs.collect { case run @ Run.Integers(_, _, _) => run }
    val floats       = runs.count { case Run.Floats(_, _) => true; case _ => false }
    val firstInteger = runs.indexWhere { case Run.Integers(_, _, _) => true; case _ => false }
    val firstFloat   = runs.indexWhere { case Run.Floats(_, _) => true; case _ => false }
    paired(runs.zipWithIndex.flatMap {
      case (Run.Registers(file, first, last, _), _) =>
        Some(span(prefix(file), first, last))
      case (Run.Numbered(name, first, last, _), _) => Some(span(name, first, last))
      case (Run.Named(name, _), _)                 => Some(name)
      case (Run.Integers(_, _, _), at)             => Option.when(at == firstInteger)(inlineRange(integers))
      case (Run.Floats(_, _), at)                  => Option.when(at == firstFloat)(s"$floats float constants")
      case (Run.Literal(4, _), _)                  => Some("literal")
      case (Run.Literal(_, _), _)                  => Some("literal64")
      case (run: Run.Immediate, _)                 => Some(immediate(run))
    }).mkString(", ")
  }

  private def immediate(run: Run.Immediate): String =
    s"${run.bits}-bit ${if (run.signed) "signed" else "unsigned"} integer ${run.min}..${run.max}"

  private def codes(first: Int, count: Int): String = if (count == 1) first.toString else s"$first-${first + count - 1}"

  private def alignments(table: DomainTable, operandBits: Int): Vec[(String, String)] =
    table.runs.collect { case Run.Registers(file = file) => file }.distinct.toVec.flatMap { file =>
      Some(table.alignment(file, operandBits)).filter(_ > 1).map(alignment =>
        "alignment" ->
          s"a $operandBits-bit operand starts at a ${prefix(file)} register that is a multiple of $alignment"
      )
    }

  private def rows(table: DomainTable, operandBits: Int): Vec[(String, String)] =
    table.runs.toVec.map {
      case Run.Registers(file, first, last, code) =>
        codes(code, last - first + 1) -> span(prefix(file), first, last)
      case Run.Numbered(name, first, last, code) =>
        codes(code, last - first + 1) -> span(name, first, last)
      case Run.Named(name, code)           => code.toString -> name
      case Run.Integers(first, last, code) =>
        codes(code, math.abs(last - first) + 1) -> (if (first == last) s"inline $first" else s"inline $first..$last")
      case Run.Floats(value, code)  => code.toString -> value.toString
      case Run.Literal(bytes, code) => code.toString -> s"${bytes * 8}-bit literal after the instruction"
      case run: Run.Immediate       => "field"       -> immediate(run)
    } ++ alignments(table, operandBits)
}
