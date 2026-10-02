package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.isa.sass.*
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
    def formId: Option[Int] = form.formId
    def name: String        = form.name
    def alternate: Boolean  = form.alternate
  }

  /** Physical positions are ordered from the least significant logical bit to the most significant. */
  def extract(word: BigInt, positions: Vec[Int]): BigInt =
    positions.zipWithIndex.foldLeft(BigInt(0)) { case (value, (physical, logical)) =>
      if (word.testBit(physical)) value.setBit(logical) else value
    }

  def candidates(target: String, bytes: Array[Byte]): Either[String, Vec[Candidate]] =
    NVDisasm.forTarget(target).toRight(s"no generated binary decoder for $target").flatMap { database =>
      candidates(database, bytes)
    }

  def candidates(database: NVDisasm.Database, bytes: Array[Byte]): Either[String, Vec[Candidate]] =
    if (bytes.length * 8 != database.width)
      Left(s"${database.target} instruction requires ${database.width / 8} bytes, found ${bytes.length}")
    else candidates(database, BigInt(1, bytes.reverse))

  def candidates(database: NVDisasm.Database, word: BigInt): Either[String, Vec[Candidate]] =
    if (word < 0 || word.bitLength > database.width) Left(s"instruction exceeds ${database.width} bits")
    else {
      val forms = database.opcodeGroups.flatMap { case (positions, group) =>
        group.getOrElse(extract(word, positions), Vec.empty[NVDisasm.Form])
      }.distinct
      val matches = forms.flatMap(form => decode(database, form, word))
      Either.cond(matches.nonEmpty, matches, s"unrecognized ${database.target} instruction 0x${word.toString(16)}")
    }

  /** Selects an assembler spelling only when the requested bindings are consistent with the encoded relations. */
  def withBindings(
      database: NVDisasm.Database,
      candidate: Candidate,
      bindings: Map[String, BigInt]
  ): Either[String, Candidate] = {
    val form  = candidate.form
    val names = form.fields.flatMap(field =>
      SASSFormatBindings.references(field.expression, includeBaseForAttributes = false)
    ).toSet
    val result = Option.when(bindings.keySet.subsetOf(names))(bindings)
      .flatMap(resolve(database, form, candidate.encodingValues, _))
    result.map(state =>
      candidate.copy(bindings = state.values, issues = state.issues.distinct, floatFormats = state.floatFormats)
    ).toRight(s"requested bindings do not match ${candidate.name}")
  }

  def refine(
      database: NVDisasm.Database,
      original: Candidate,
      analysis: SASSFormatBindings.Analysis
  ): Candidate = {
    import analysis.*
    val inferredDefaults = declared.flatMap { field =>
      val binding = encodedBinding(field)
      Option.when(encodedNames(binding) && !original.bindings.contains(binding))(
        defaultValues(field).headOption.map(binding -> _)
      ).flatten
    }
    val withDefaults = inferredDefaults.reverse.foldLeft(original) { case (candidate, (name, value)) =>
      if (candidate.bindings.contains(name)) candidate
      else
        withBindings(
          database,
          candidate,
          candidate.bindings.updated(name, value)
        ).getOrElse(candidate)
    }
    val attributes = encodedNames.toVec.filter { name =>
      name.split('@').toVec match {
        case Vec(_, attribute) => Set("not", "negate", "absolute", "invert")(attribute)
        case _                 => false
      }
    }.sorted
    val withAttributes = attributes.foldLeft(withDefaults) { (candidate, name) =>
      if (candidate.bindings.contains(name)) candidate
      else withBindings(
        database,
        candidate,
        candidate.bindings.updated(name, BigInt(0))
      ).getOrElse(candidate)
    }
    declared.foldLeft(withAttributes) { (candidate, field) =>
      val binding = encodedBinding(field)
      if (candidate.bindings.contains(binding) || !encodedNames(binding) || NVDisasm.fieldKind(field.kind).isRegister)
        candidate
      else {
        val choices = defaultValues(field) ++ vocabulary(field).map(_.value).distinct.sorted.reverse
        choices.distinct.view.flatMap(raw =>
          withBindings(database, candidate, candidate.bindings.updated(binding, raw)).toOption
        ).headOption.getOrElse(candidate)
      }
    }
  }

  /** True when a raw field value can decode consistently with every supplied semantic binding. Unbound references stay
    * unconstrained, which lets lossless assembly retain a non-injective table encoding after unrelated operands change.
    */
  def accepts(
      database: NVDisasm.Database,
      expression: NVDisasm.Expression,
      value: BigInt,
      bindings: Map[String, BigInt]
  ): Boolean =
    invert(database, expression, value, Bindings(bindings, Vec.empty)).exists { state =>
      !state.issues.exists(issue =>
        issue.kind == NVDisasm.MetadataIssueKind.MissingMetadata ||
          issue.kind == NVDisasm.MetadataIssueKind.Unsupported
      )
    }

  private def orderedFields(form: NVDisasm.Form): Vec[NVDisasm.Assignment] = form.fields.sortBy { field =>
    field.expression match {
      case NVDisasm.Expression.FloatConvert(_, _, _, _) => 1
      case _                                            => 0
    }
  }

  private case class Bindings(
      values: Map[String, BigInt],
      issues: Vec[NVDisasm.MetadataIssue],
      floatFormats: Map[String, FloatEncoding] = Map.empty
  )

  private def resolve(
      database: NVDisasm.Database,
      form: NVDisasm.Form,
      raw: Map[String, BigInt],
      seed: Map[String, BigInt]
  ): Option[Bindings] = {
    val fields                                              = orderedFields(form)
    @tailrec def loop(previous: Bindings): Option[Bindings] = {
      val next = fields.foldLeft(Option(Bindings(previous.values, form.typedIssues))) { (state, field) =>
        for {
          current <- state
          value   <- raw.get(field.name)
          result  <- invert(database, field.expression, value, current)
        } yield result
      }
      next match {
        case Some(current)
            if current.values != previous.values || current.floatFormats != previous.floatFormats ||
              current.issues != previous.issues =>
          loop(current)
        case _ => next
      }
    }
    loop(Bindings(seed, form.typedIssues))
  }

  private def merge(state: Bindings, name: String, value: BigInt): Option[Bindings] =
    state.values.get(name) match {
      case Some(previous) if previous != value => None
      case _                                   => Some(state.copy(values = state.values.updated(name, value)))
    }

  private def invert(
      database: NVDisasm.Database,
      expression: NVDisasm.Expression,
      value: BigInt,
      state: Bindings
  ): Option[Bindings] = expression match {
    case NVDisasm.Expression.Constant(expected, relaxed) =>
      Option.when(relaxed || expected == value)(state)
    case NVDisasm.Expression.Reference(name, attribute, _) =>
      merge(state, attribute.fold(name)(suffix => s"$name@$suffix"), value)
    case NVDisasm.Expression.Scale(inner, factor)    => invert(database, inner, value * factor, state)
    case NVDisasm.Expression.Multiply(inner, factor) =>
      Option.when(factor != 0 && value % factor == 0)(value / factor).flatMap(invert(database, inner, _, state))
    case NVDisasm.Expression.Identity(first, second) =>
      invert(database, first, value, state).flatMap(invert(database, second, value, _))
    case NVDisasm.Expression.Relaxed(inner)            => invert(database, inner, value, state)
    case NVDisasm.Expression.FixedFloat(inner, format) =>
      invert(database, inner, value, state).map(next =>
        next.copy(floatFormats = next.floatFormats ++ SASSFormatBindings.references(inner, false).map(_ -> format))
      )
    case NVDisasm.Expression.FloatConvert(inner, selector, cases, fallback) =>
      state.values.get(selector).fold(Option(state.copy(issues =
        state.issues :+ NVDisasm.MetadataIssue.unresolvedSelector(selector)
      ))) { selected =>
        val format = cases.find(_._1.contains(selected)).fold(fallback)(_._2)
        invert(database, inner, value, state).map(next =>
          next.copy(floatFormats = next.floatFormats ++ SASSFormatBindings.references(inner, false).map(_ -> format))
        )
      }
    case NVDisasm.Expression.Lookup(name, arguments) =>
      database.byTable.get(name) match {
        case None => Some(state.copy(issues = state.issues :+ NVDisasm.MetadataIssue.missingLookup(name)))
        case Some(table) if table.issues.nonEmpty =>
          Some(state.copy(issues = state.issues :+ NVDisasm.MetadataIssue.unsupportedLookup(name, table.issues)))
        case Some(table) =>
          val rows = table.byOutput.getOrElse(value, Vec.empty[NVDisasm.Row]).filter(_.inputs.size == arguments.size)
          val solutions = rows.flatMap { row =>
            arguments.zip(row.inputs).foldLeft(Option(state)) { case (current, (argument, input)) =>
              current.flatMap(invert(database, argument, input, _))
            }
          }.distinct
          if (solutions.isEmpty) None
          else if (solutions.size == 1) solutions.headOption
          else {
            val common = solutions.head.values.filter { case (key, entry) =>
              solutions.forall(_.values.get(key).contains(entry))
            }
            Some(state.copy(
              values = common,
              issues = (state.issues :+ NVDisasm.MetadataIssue.ambiguousInverse(name, value)).distinct
            ))
          }
      }
    case NVDisasm.Expression.Unsupported(source) =>
      Some(state.copy(issues = state.issues :+ NVDisasm.MetadataIssue.unresolvedExpression(source)))
  }

  private def decode(database: NVDisasm.Database, form: NVDisasm.Form, word: BigInt): Vec[Candidate] = {
    val raw          = form.fields.map(field => field.name -> extract(word, field.positions)).toMap
    val opcodeFields = form.fields.filter(_.expression match {
      case NVDisasm.Expression.Reference("Opcode", None, _) => true
      case _                                                => false
    })
    if (opcodeFields.isEmpty) Vec.empty
    else form.opcodes.groupBy(_.value).toVec.sortBy(_._1).flatMap { case (opcodeValue, aliases) =>
      if (!opcodeFields.forall(field => raw(field.name) == opcodeValue)) Vec.empty
      else {
        val result = resolve(database, form, raw, Map.empty)
        result.toVec.map { state =>
          val names  = aliases.map(_.name).distinct
          val opcode = names.find(name => name.forall(c => !c.isLetter || c.isUpper)).getOrElse(names.head)
          Candidate(
            form,
            opcode,
            state.values,
            raw,
            state.issues.distinct,
            state.floatFormats
          )
        }
      }
    }
  }
}
