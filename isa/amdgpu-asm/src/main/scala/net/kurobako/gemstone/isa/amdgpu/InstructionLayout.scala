package net.kurobako.gemstone.isa.amdgpu

import cats.syntax.all.*
import net.kurobako.gemstone.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.isa.catalogue.*
import upickle.default.*

private object InstructionLayout {
  case class Field(name: EncodingField, ranges: Vector[BitRange]) derives ReadWriter {
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

  // a condition on a field the layout lacks never holds
  private class CompiledCondition(field: Option[InstructionLayout.Field], values: Array[Int], included: Boolean) {
    @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
    def holds(word: InstructionWord): Boolean = field match {
      case Some(present) =>
        val actual = present.get(word)
        var found  = false
        var i      = 0
        while (!found && i < values.length) {
          found = values(i) == actual
          i += 1
        }
        found == included
      case None => false
    }
  }

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
  val embedded: Boolean = byName.contains(EncodingField.LITERAL) || byName.contains(EncodingField.LITERAL64)
  val literalField: Option[InstructionLayout.Field] = byName.get(EncodingField.LITERAL)

  private def compileConditions(clauses: Vector[Vector[InstructionLayout.Condition]])
      : Array[Array[InstructionLayout.CompiledCondition]] =
    clauses.map(_.map(condition =>
      InstructionLayout.CompiledCondition(byName.get(condition.field), condition.values.toArray, condition.included)
    ).toArray).toArray

  private def compiledLiteral(bytes: Int): Option[Array[Array[InstructionLayout.CompiledCondition]]] =
    format.withLiteral(bytes).flatMap(InstructionLayout.Catalogue.forFormat(isa, _))
      .map(candidate => compileConditions(candidate.conditions))

  private lazy val compiled0 = Some(compileConditions(conditions))
  private lazy val compiled4 = compiledLiteral(4)
  private lazy val compiled8 = compiledLiteral(8)

  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private def conditionsHold(clauses: Array[Array[InstructionLayout.CompiledCondition]], word: InstructionWord)
      : Boolean = {
    var held = clauses.isEmpty
    var i    = 0
    while (!held && i < clauses.length) {
      val clause = clauses(i)
      var all    = true
      var j      = 0
      while (all && j < clause.length) {
        all = clause(j).holds(word)
        j += 1
      }
      held = all
      i += 1
    }
    held
  }

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
    val compiled = literalBytes match {
      case 0     => compiled0
      case 4     => compiled4
      case 8     => compiled8
      case bytes => compiledLiteral(bytes)
    }
    compiled match {
      case Some(clauses) => conditionsHold(clauses, word)
      case None          => false
    }
  }
}
