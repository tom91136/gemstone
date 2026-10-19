package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.isa.catalogue.*
import scala.annotation.tailrec

/** Binary structural decoding, independent of NVIDIA tools and handwritten execution semantics. Candidates retain
  * unresolved transformations and all raw fields. Callers must establish operand legality and execution support
  * separately; an ambiguous candidate set must never be resolved by taking its first entry.
  */
object FieldDecoder {
  case class Candidate(
      form: NVDisasm.Form,
      opcode: String,
      bindings: Map[String, BigInt],
      encodingValues: Map[String, BigInt],
      issues: Vec[NVDisasm.MetadataIssue],
      floatFormats: Map[String, FloatEncoding] = Map.empty
  ) {
    def name: String       = form.name
    def alternate: Boolean = form.alternate
  }

  /** Physical positions are ordered from the least significant logical bit to the most significant. */
  def extract(word: BigInt, positions: Vec[Int]): BigInt =
    positions.zipWithIndex.foldLeft(BigInt(0)) { case (value, (physical, logical)) =>
      if (word.testBit(physical)) value.setBit(logical) else value
    }

  /** The inverse of `extract` for positions that are clear in `word`. */
  def deposit(word: BigInt, positions: Vec[Int], value: BigInt): BigInt =
    positions.zipWithIndex.foldLeft(word) { case (bits, (physical, logical)) =>
      if (value.testBit(logical)) bits.setBit(physical) else bits
    }

  def candidates(database: NVDisasm.Database, bytes: Array[Byte]): Either[String, Vec[Candidate]] =
    if (bytes.length * 8 != database.width.raw)
      Left(s"instruction requires ${database.width / 8} bytes, found ${bytes.length}")
    else candidates(database, BigInt(1, bytes.reverse))

  def candidates(database: NVDisasm.Database, word: BigInt): Either[String, Vec[Candidate]] =
    if (word < 0 || word.bitLength > database.width) Left(s"instruction exceeds ${database.width} bits")
    else {
      val forms = database.opcodeGroups.flatMap { case (positions, group) =>
        group.getOrElse(extract(word, positions), Vec.empty[NVDisasm.Form])
      }.distinct
      val matches = forms.flatMap(form => decode(database, form, word))
      Either.cond(matches.nonEmpty, matches, s"unrecognised instruction 0x${word.toString(16)}")
    }

  /** Selects an assembler spelling only when the requested bindings are consistent with the encoded relations. */
  def withBindings(
      database: NVDisasm.Database,
      candidate: Candidate,
      bindings: Map[String, BigInt]
  ): Either[String, Candidate] =
    Option.when(bindings.keySet.subsetOf(candidate.form.encodedReferences))(bindings)
      .flatMap(resolve(database, candidate.form, candidate.encodingValues, _))
      .map(state =>
        candidate.copy(bindings = state.values, issues = state.issues.distinct, floatFormats = state.floats)
      )
      .toRight(s"requested bindings do not match ${candidate.name}")

  /** Completes a candidate with the display defaults, absent attributes and first legal vocabulary values its FORMAT
    * implies, so that bindings the encoding leaves free print canonically.
    */
  def refine(database: NVDisasm.Database, original: Candidate): Candidate = {
    val form                                                                       = original.form
    def bind(candidate: Candidate, name: String, value: BigInt): Option[Candidate] =
      withBindings(database, candidate, candidate.bindings.updated(name, value)).toOption
    val inferredDefaults = form.declared.flatMap { field =>
      val binding = form.encodedBinding(field)
      Option.when(form.encodedNames(binding) && !original.bindings.contains(binding))(
        database.defaultValues(field).headOption.map(binding -> _)
      ).flatten
    }
    val withDefaults = inferredDefaults.reverse.foldLeft(original) { case (candidate, (name, value)) =>
      if (candidate.bindings.contains(name)) candidate else bind(candidate, name, value).getOrElse(candidate)
    }
    val attributes = form.encodedNames.toVec.filter(name =>
      name.split('@').toVec match {
        case Vec(_, attribute) => Attribute.names(attribute)
        case _                 => false
      }
    ).sorted
    val withAttributes = attributes.foldLeft(withDefaults) { (candidate, name) =>
      if (candidate.bindings.contains(name)) candidate else bind(candidate, name, BigInt(0)).getOrElse(candidate)
    }
    form.declared.foldLeft(withAttributes) { (candidate, field) =>
      val binding = form.encodedBinding(field)
      if (candidate.bindings.contains(binding) || !form.encodedNames(binding) || field.category.isRegister) candidate
      else {
        val choices = database.defaultValues(field) ++ database.vocabulary(field).map(_.value).distinct.sorted.reverse
        choices.distinct.view.flatMap(bind(candidate, binding, _)).headOption.getOrElse(candidate)
      }
    }
  }

  /** True when a raw field value can decode consistently with every supplied semantic binding. Unbound references stay
    * unconstrained, which lets lossless assembly retain a non-injective table encoding after unrelated operands change.
    */
  def accepts(
      database: NVDisasm.Database,
      expression: Expression,
      value: BigInt,
      bindings: Map[String, BigInt]
  ): Boolean =
    invert(database, expression, value, Bindings(bindings, Vec.empty)).exists(_.issues.forall {
      case NVDisasm.MetadataIssue.UnresolvedSelector(_) => false
      case _                                            => true
    })

  /** Operand attribute bindings (`Ra@negate`) shared by decoding, refinement and assembly. */
  object Attribute {
    val names: Set[String] = Set("not", "negate", "absolute", "invert")
  }

  private case class Bindings(
      values: Map[String, BigInt],
      issues: Vec[NVDisasm.MetadataIssue],
      floats: Map[String, FloatEncoding] = Map.empty
  ) derives CanEqual

  private def resolve(
      database: NVDisasm.Database,
      form: NVDisasm.Form,
      raw: Map[String, BigInt],
      seed: Map[String, BigInt]
  ): Option[Bindings] = {
    @tailrec def loop(previous: Bindings): Option[Bindings] =
      form.orderedFields.foldLeft(Option(Bindings(previous.values, Vec.empty))) { (state, field) =>
        for {
          current <- state
          value   <- raw.get(field.name)
          result  <- invert(database, field.expression, value, current)
        } yield result
      } match {
        case Some(current) if current != previous => loop(current)
        case next                                 => next
      }
    loop(Bindings(seed, Vec.empty))
  }

  private def merge(state: Bindings, name: String, value: BigInt): Option[Bindings] =
    state.values.get(name) match {
      case Some(previous) if previous != value => None
      case _                                   => Some(state.copy(values = state.values.updated(name, value)))
    }

  private def invertFloat(
      database: NVDisasm.Database,
      expression: Expression,
      format: FloatEncoding,
      value: BigInt,
      state: Bindings
  ): Option[Bindings] =
    invert(database, expression, value, state).map(next =>
      next.copy(floats = next.floats ++ expression.references(bases = false).map(_ -> format))
    )

  private def invert(
      database: NVDisasm.Database,
      expression: Expression,
      value: BigInt,
      state: Bindings
  ): Option[Bindings] = expression match {
    case Expression.Constant(expected, relaxed) =>
      Option.when(relaxed || expected == value)(state)
    case Expression.Reference(name, attribute, _) =>
      merge(state, NVDisasm.bindingKey(name, attribute), value)
    case Expression.Scale(inner, factor)    => invert(database, inner, value * factor, state)
    case Expression.Multiply(inner, factor) =>
      Option.when(factor != 0 && value % factor == 0)(value / factor).flatMap(invert(database, inner, _, state))
    case Expression.Identity(first, second) =>
      invert(database, first, value, state).flatMap(invert(database, second, value, _))
    case Expression.Relaxed(inner)                                 => invert(database, inner, value, state)
    case Expression.FixedFloat(inner, format)                      => invertFloat(database, inner, format, value, state)
    case Expression.FloatConvert(inner, selector, cases, fallback) =>
      state.values.get(selector).fold(Option(state.copy(issues =
        state.issues :+ NVDisasm.MetadataIssue.UnresolvedSelector(selector)
      ))) { selected =>
        invertFloat(database, inner, cases.find(_._1.contains(selected)).fold(fallback)(_._2), value, state)
      }
    case Expression.Lookup(name, arguments) =>
      database.byTable.get(name).flatMap { table =>
        val rows      = table.byOutput.getOrElse(value, Vec.empty[NVDisasm.Row]).filter(_.inputs.size == arguments.size)
        val solutions = rows.flatMap { row =>
          arguments.zip(row.inputs).foldLeft(Option(state)) { case (current, (argument, input)) =>
            current.flatMap(invert(database, argument, input, _))
          }
        }.distinct
        if (solutions.size <= 1) solutions.headOption
        else {
          val common = solutions.head.values.filter { case (key, entry) =>
            solutions.forall(_.values.get(key).contains(entry))
          }
          Some(state.copy(
            values = common,
            issues = (state.issues :+ NVDisasm.MetadataIssue.AmbiguousInverse(name, value)).distinct
          ))
        }
      }
  }

  private def decode(database: NVDisasm.Database, form: NVDisasm.Form, word: BigInt): Vec[Candidate] = {
    val raw = form.fields.map(field => field.name -> extract(word, field.positions)).toMap
    form.opcodes.filter(opcode =>
      form.opcodeFields.nonEmpty && form.opcodeFields.forall(field => raw(field.name) == opcode.value)
    )
      .flatMap(opcode =>
        resolve(database, form, raw, Map.empty).toVec.map { state =>
          Candidate(form, opcode.name, state.values, raw, state.issues.distinct, state.floats)
        }
      )
  }
}
