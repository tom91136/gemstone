package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import scala.util.Using
import upickle.default.*

private object InstructionLayout {
  case class Range(offset: Int, bits: Int, padding: Int) derives ReadWriter
  case class Field(name: EncodingField, ranges: Vector[Range]) derives ReadWriter {
    val width: Int   = ranges.map(range => range.bits + range.padding).sum
    val mask: BigInt =
      ranges.foldLeft(BigInt(0))((result, range) => result | (((BigInt(1) << range.bits) - 1) << range.offset))
    def get(word: BigInt): BigInt = ranges.reverse.foldLeft(BigInt(0)) { (value, range) =>
      (value << (range.bits + range.padding)) |
        (((word >> range.offset) & ((BigInt(1) << range.bits) - 1)) << range.padding)
    }
    @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
    def get(word: Long): Long = {
      var value = 0L
      var index = ranges.size - 1
      while (index >= 0) {
        val range = ranges(index)
        value = (value << (range.bits + range.padding)) |
          (((word >>> range.offset) & (-1L >>> (64 - range.bits))) << range.padding)
        index -= 1
      }
      value
    }
    def put(value: BigInt): Option[BigInt] = {
      val (word, rest, valid) = ranges.foldLeft((BigInt(0), value, true)) { case ((word, remaining, valid), range) =>
        val low = remaining & ((BigInt(1) << (range.bits + range.padding)) - 1)
        (
          word | ((low >> range.padding) << range.offset),
          remaining >> (range.bits + range.padding),
          valid && (low & ((BigInt(1) << range.padding) - 1)) == 0
        )
      }
      Option.when(valid && rest == 0)(word)
    }
  }
  case class Condition(field: EncodingField, values: Vector[Int], included: Boolean) derives ReadWriter

  object Catalogue {
    lazy val all: Vec[InstructionLayout] = Using.resource(
      Option(
        getClass.getResourceAsStream("layouts.json")
      ).getOrElse(throw IllegalStateException("missing AMDGPU layouts.json"))
    )(input => read[Vector[InstructionLayout]](input).filter(_.prefixes.size == 1).toVec)

    private lazy val byFormat = all.groupBy(value => (value.isa, value.format)).view.mapValues(_.head).toMap
    lazy val byISA: Map[ISA, Vec[InstructionLayout]] = all.groupBy(value => value.isa)

    def forFormat(target: Target, format: EncodingFormat): Option[InstructionLayout] =
      byFormat.get((target.isa, format))
  }
}

private case class InstructionLayout(
    isa: ISA,
    format: EncodingFormat,
    bits: Int,
    mask: String,
    identifiers: Vector[String],
    fields: Vector[InstructionLayout.Field],
    conditions: Vector[Vector[InstructionLayout.Condition]] = Vector(Vector.empty)
) derives ReadWriter {
  val byName: Map[EncodingField, InstructionLayout.Field] = fields.map(field => field.name -> field).toMap
  val fixedMask: BigInt                                   = BigInt(mask, 16) &
    ~Vector(EncodingField.OP, EncodingField.OPX, EncodingField.OPY).flatMap(byName.get).foldLeft(BigInt(0))(
      (bits, field) => bits | field.mask
    )
  val prefixes: Vector[BigInt] = identifiers.map(value => BigInt(value, 16) & fixedMask).distinct
  private val lowMask          = fixedMask.toLong & 0xffffffffL
  private val lowPrefix        = prefixes.headOption.fold(0L)(_.toLong) & lowMask

  def matches(word: Long): Boolean                  = (word & lowMask) == lowPrefix
  def get(word: Long, field: EncodingField): Long   = byName(field).get(word)
  def signed(word: Long, field: EncodingField): Int = {
    val descriptor = byName(field)
    val shift      = 64 - descriptor.width
    ((descriptor.get(word) << shift) >> shift).toInt
  }

  def accepts(word: BigInt, literalBytes: Int = 0): Boolean = {
    val candidate = if (literalBytes == 0) Some(this)
    else InstructionLayout.Catalogue.all.find(value =>
      value.isa == isa &&
        format.withLiteral(literalBytes).contains(value.format)
    )
    candidate.exists(candidate =>
      candidate.conditions.isEmpty || candidate.conditions.exists(_.forall(condition =>
        byName.get(condition.field).exists(field =>
          condition.values.exists(value => field.get(word) == value) == condition.included
        )
      ))
    )
  }
  def get(word: BigInt, field: EncodingField): BigInt = byName.get(field).fold(BigInt(0))(_.get(word))
}
