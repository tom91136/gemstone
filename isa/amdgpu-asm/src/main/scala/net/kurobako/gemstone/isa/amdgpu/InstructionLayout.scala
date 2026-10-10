package net.kurobako.gemstone.isa.amdgpu

import cats.syntax.all.*
import net.kurobako.gemstone.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import upickle.default.*

private object InstructionLayout {
  case class Range(offset: Int, bits: Int, padding: Int) derives ReadWriter
  case class Field(name: EncodingField, ranges: Vector[Range]) derives ReadWriter {
    val width: Int   = ranges.map(range => range.bits + range.padding).sum
    val mask: BigInt =
      ranges.foldLeft(BigInt(0))((result, range) => result | (((BigInt(1) << range.bits) - 1) << range.offset))

    @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
    def get(word: InstructionWord): Long = {
      var value = 0L
      var index = ranges.size - 1
      while (index >= 0) {
        val range = ranges(index)
        value = (value << (range.bits + range.padding)) | (word.bits(range.offset, range.bits) << range.padding)
        index -= 1
      }
      value
    }
    def get(word: Long): Long = get(InstructionWord(word, middle = 0L, high = 0L))

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
    lazy val all: Vec[InstructionLayout] = resources.json[Vector[InstructionLayout]](getClass, "layouts.json").toVec

    private lazy val byFormat = all.groupBy(value => (value.isa, value.format)).view.mapValues(_.head).toMap

    def forFormat(isa: ISA, format: EncodingFormat): Option[InstructionLayout]       = byFormat.get((isa, format))
    def forFormat(target: Target, format: EncodingFormat): Option[InstructionLayout] = forFormat(target.isa, format)
  }
}

private case class InstructionLayout(
    isa: ISA,
    format: EncodingFormat,
    bits: Int,
    mask: String,
    prefix: String,
    fields: Vector[InstructionLayout.Field],
    conditions: Vector[Vector[InstructionLayout.Condition]] = Vector(Vector.empty)
) derives ReadWriter {
  val byName: Map[EncodingField, InstructionLayout.Field] = fields.map(field => field.name -> field).toMap
  val fixedMask: BigInt                                   = BigInt(mask, 16)
  val fixedPrefix: BigInt                                 = BigInt(prefix, 16)
  val wordMask: InstructionWord                           = InstructionWord.from(fixedMask)
  val wordPrefix: InstructionWord                         = InstructionWord.from(fixedPrefix)

  def matches(word: InstructionWord): Boolean = word.masked(wordMask) == wordPrefix

  /** Zero for fields this layout does not have. */
  def get(word: InstructionWord, field: EncodingField): Long = byName.get(field).fold(0L)(_.get(word))

  def signed(word: InstructionWord, field: EncodingField): Long = byName.get(field).fold(0L) { descriptor =>
    val shift = 64 - descriptor.width
    (descriptor.get(word) << shift) >> shift
  }

  /** The fixed bits with every field set, or None when a field is absent or its value does not fit. */
  def assemble(values: Map[EncodingField, BigInt]): Option[BigInt] =
    values.toVector.traverse { case (name, value) => byName.get(name).flatMap(_.put(value)) }
      .map(_.foldLeft(fixedPrefix)(_ | _))

  /** GFX7 SMRD selects a trailing literal offset with OFFSET = 255 and IMM clear. */
  def memoryLiteral(word: InstructionWord): Boolean =
    format == EncodingFormat.SMEM && bits == 32 && isa == ISA.GFX7 && get(word, EncodingField.IMM) == 0 &&
      get(word, EncodingField.OFFSET) == EncodingConstants.Source.LITERAL_CONST

  /** Whether the encoding conditions hold; a trailing source literal selects the conditions of the literal variant. */
  def accepts(word: InstructionWord, literalBytes: Int): Boolean = {
    val candidate =
      if (literalBytes == 0) Some(this)
      else format.withLiteral(literalBytes).flatMap(InstructionLayout.Catalogue.forFormat(isa, _))
    candidate.exists(candidate =>
      candidate.conditions.isEmpty || candidate.conditions.exists(_.forall(condition =>
        byName.get(condition.field).exists(field =>
          condition.values.exists(value => field.get(word) == value) == condition.included
        )
      ))
    )
  }
}
