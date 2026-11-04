package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.isa.catalogue.*
import net.kurobako.gemstone.isa.sass.BitField.*
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
      floatFormats: Map[String, FloatEncoding] = Map.empty,
      alternatives: Vec[Candidate] = Vec.empty
  ) {
    def name: String       = form.name
    def alternate: Boolean = form.alternate
  }

  def candidates(database: NVDisasm.Database, bytes: Array[Byte]): Either[String, Vec[Candidate]] =
    if (bytes.length * 8 != database.width.raw)
      Left(s"instruction requires ${database.width / 8} bytes, found ${bytes.length}")
    else candidates(database, BigInt(1, bytes.reverse))

  def candidates(database: NVDisasm.Database, word: BigInt): Either[String, Vec[Candidate]] =
    if (word < 0 || word.bitLength > database.width) Left(s"instruction exceeds ${database.width} bits")
    else {
      val matches = matching(database, Word(word))
      Either.cond(matches.nonEmpty, matches, s"unrecognised instruction 0x${word.toString(16)}")
    }

  /** The non-alternate `candidates`; alternates resolve only to tell an unrecognised word apart. */
  def primary(database: NVDisasm.Database, bytes: Array[Byte]): Either[String, Vec[Candidate]] =
    if (bytes.length * 8 != database.width.raw)
      Left(s"instruction requires ${database.width / 8} bytes, found ${bytes.length}")
    else {
      val word = BigInt(1, bytes.reverse)
      if (word.bitLength > database.width) Left(s"instruction exceeds ${database.width} bits")
      else {
        val split   = Word(word)
        val matches = matching(database, split, alternate = false)
        if (matches.nonEmpty || matching(database, split).nonEmpty) Right(matches)
        else Left(s"unrecognised instruction 0x${word.toString(16)}")
      }
    }

  private def matching(database: NVDisasm.Database, word: Word, alternate: Boolean = true): Vec[Candidate] =
    database.opcodeGroups.flatMap((extractor, group) => group.getOrElse(extractor(word), Vec.empty[NVDisasm.Form]))
      .filter(alternate || !_.alternate).flatMap(form => decode(database, form, word))

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
      val binding = form.binding(field)
      Option.when(form.encodedNames(binding) && !original.bindings.contains(binding))(
        database.defaultValues(field).headOption.map(binding -> _)
      ).flatten
    }
    val withDefaults = inferredDefaults.reverse.foldLeft(original) { case (candidate, (name, value)) =>
      if (candidate.bindings.contains(name)) candidate else bind(candidate, name, value).getOrElse(candidate)
    }
    val withAttributes = form.attributeNames.foldLeft(withDefaults) { (candidate, name) =>
      if (candidate.bindings.contains(name)) candidate else bind(candidate, name, BigInt(0)).getOrElse(candidate)
    }
    form.declared.foldLeft(withAttributes) { (candidate, field) =>
      val binding = form.binding(field)
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

  private case class Bindings(
      values: Map[String, BigInt],
      issues: Vec[NVDisasm.MetadataIssue],
      floats: Map[String, FloatEncoding] = Map.empty,
      alternatives: Vec[Bindings] = Vec.empty
  ) derives CanEqual

  private def resolve(
      database: NVDisasm.Database,
      form: NVDisasm.Form,
      raw: Map[String, BigInt],
      seed: Map[String, BigInt]
  ): Option[Bindings] = {
    def pass(previous: Bindings): Option[Bindings] =
      form.orderedFields.foldLeft(Option(Bindings(previous.values, Vec.empty))) { (state, field) =>
        for {
          current <- state
          value   <- raw.get(field.name)
          result  <- invert(database, field.expression, value, current)
        } yield result
      }
    @tailrec def loop(previous: Bindings): Option[Bindings] = pass(previous) match {
      case Some(current) if current != previous => loop(current)
      case next                                 => next
    }
    if (form.singlePass) pass(Bindings(seed, Vec.empty)) else loop(Bindings(seed, Vec.empty))
  }

  /** One state per consistent solution of an ambiguous lookup, for when legality rejects the common bindings alone. */
  private def expand(
      database: NVDisasm.Database,
      form: NVDisasm.Form,
      raw: Map[String, BigInt],
      state: Bindings,
      depth: Int
  ): Vec[Bindings] =
    if (state.alternatives.isEmpty) Vec(state)
    else {
      val solutions =
        if (depth == 0) Vec.empty[Bindings]
        else state.alternatives.flatMap(alternative =>
          resolve(database, form, raw, alternative.values).toVec.flatMap(expand(database, form, raw, _, depth - 1))
        )
      if (solutions.isEmpty) Vec(state) else solutions
    }

  private def merge(state: Bindings, name: String, value: BigInt): Option[Bindings] =
    state.values.get(name) match {
      case Some(previous) => Option.when(previous == value)(state)
      case None           => Some(state.copy(values = state.values.updated(name, value)))
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
        val rows  = table.byOutput.getOrElse(value, Vec.empty[NVDisasm.Row]).filter(_.inputs.size == arguments.size)
        val found = rows.flatMap { row =>
          arguments.zip(row.inputs).foldLeft(Option(state)) { case (current, (argument, input)) =>
            current.flatMap(invert(database, argument, input, _))
          }
        }
        val solutions = if (found.size > 1) found.distinct else found
        if (solutions.size <= 1) solutions.headOption
        else {
          val common = solutions.head.values.filter { case (key, entry) =>
            solutions.forall(_.values.get(key).contains(entry))
          }
          val issue = NVDisasm.MetadataIssue.AmbiguousInverse(name, value)
          Some(state.copy(
            values = common,
            issues = (state.issues :+ issue).distinct,
            alternatives = if (issue.structural || state.alternatives.nonEmpty) state.alternatives else solutions
          ))
        }
      }
  }

  private def decode(database: NVDisasm.Database, form: NVDisasm.Form, word: Word): Vec[Candidate] =
    if (form.pinned.exists((field, expected) => field.extractor(word) != expected)) Vec.empty
    else resolved(database, form, word)

  private def resolved(database: NVDisasm.Database, form: NVDisasm.Form, word: Word): Vec[Candidate] = {
    val builder = Map.newBuilder[String, BigInt]
    form.fields.foreach(field => builder += field.name -> field.extractor(word))
    val raw          = builder.result()
    val opcodeValues = form.opcodeFields.map(field => raw(field.name)).distinct
    val matching     = form.opcodes.filter(opcode => opcodeValues.size == 1 && opcodeValues.head == opcode.value)
    if (matching.isEmpty) Vec.empty
    else {
      def candidate(opcode: NVDisasm.SymbolValue, state: Bindings): Candidate =
        Candidate(form, opcode.name, state.values, raw, state.issues.distinct, state.floats)
      val resolved = resolve(database, form, raw, Map.empty).toVec
      matching.flatMap(opcode =>
        resolved.map(state =>
          candidate(opcode, state).copy(alternatives =
            if (state.alternatives.isEmpty) Vec.empty
            else expand(database, form, raw, state, 4).map(candidate(opcode, _))
          )
        )
      )
    }
  }
}
