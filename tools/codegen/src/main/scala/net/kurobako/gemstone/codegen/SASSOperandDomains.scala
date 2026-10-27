package net.kurobako.gemstone.codegen

import net.kurobako.gemstone.isa.catalogue.*

/** Derives the operand domain of every FORMAT field of one SASS description. Vocabularies give the values, the encoding
  * gives the width each form stores them in, and the legality conditions give the register limits.
  */
object SASSOperandDomains {
  case class FieldDomain(binding: String, label: String, domain: OperandDomain)
  case class FormDomains(name: String, fields: Vector[FieldDomain])
  case class Derived(forms: Vector[FormDomains], banks: Map[String, Int], findings: Vector[DomainFinding])

  private case class Limit(parameter: String, reserved: Int, excluded: Set[String])
  private case class Stored(bits: Int, shift: Int)

  private val Banks   = Map("R" -> "r", "UR" -> "ur", "B" -> "b", "SB" -> "sb")
  private val BankRef = "(R|UR|B|SB)([0-9]+)".r
  private val Indexed = "(.*[^0-9])([0-9]+)".r

  private val ImmediateKinds: Map[String, (Boolean, Int)] = Map(
    "UImm"   -> (false, 32),
    "BITSET" -> (false, 32),
    "SImm"   -> (true, 32),
    "SSImm"  -> (true, 32),
    "RSImm"  -> (true, 32),
    "F16Imm" -> (false, 16),
    "F32Imm" -> (false, 32),
    "F64Imm" -> (false, 64)
  )

  private val ZeroNames = Map("RZ" -> "R", "URZ" -> "UR")

  def derive(description: SASSDescription.Description): Derived = {
    val compiled     = SASSEncoding.compile(description)
    val vocabularies = SASSSymbols.compile(description).map(vocabulary => vocabulary.name -> vocabulary).toMap
    val parameters   = SASSConditions.definitions(description.preamble).fold(
      error => throw IllegalArgumentException(error.message),
      _.parameters
    )
    val classes = description.classes.map(form => form.name -> form).toMap
    val derived = compiled.forms.map { form =>
      val source = classes(form.name)
      val nodes  = source.section("FORMAT").toVector.flatMap(section =>
        SASSFormat.parse(section.body).fold(
          error => throw IllegalArgumentException(s"${form.name}: ${error.message}"),
          identity
        )
      )
      val limits = registerLimits(source)
      val fields = formFields(nodes).foldLeft(Vector.empty[SASSFormat.Field])((seen, field) =>
        if (seen.exists(_.binding == field.binding)) seen else seen :+ field
      )
      val typed = fields.flatMap(field => typedField(form, field, vocabularies, limits, parameters))
      (FormDomains(form.name, typed.map(_._1)), typed.flatMap(_._2))
    }
    Derived(derived.map(_._1), banks(vocabularies.values.toVector), derived.flatMap(_._2))
  }

  private def banks(vocabularies: Vector[SASSSymbols.Vocabulary]): Map[String, Int] =
    vocabularies.flatMap(_.values).flatMap { value =>
      value.name match {
        case BankRef(bank, _)                 => Some(bank -> (value.number.toInt + 1))
        case zero if ZeroNames.contains(zero) => Some(ZeroNames(zero) -> (value.number.toInt + 1))
        case _                                => None
      }
    }.groupMapReduce(_._1)(_._2)(_ max _)

  private def formFields(nodes: Vector[SASSFormat.Node]): Vector[SASSFormat.Field] = nodes.flatMap {
    case field: SASSFormat.Field          => Vector(field)
    case SASSFormat.Group(_, _, children) => formFields(children)
    case SASSFormat.Optional(_, children) => formFields(children)
    case _                                => Vector.empty
  }

  private def typedField(
      form: SASSEncoding.Form,
      field: SASSFormat.Field,
      vocabularies: Map[String, SASSSymbols.Vocabulary],
      limits: Map[String, Vector[Limit]],
      parameters: Map[String, BigInt]
  ): Option[(FieldDomain, Vector[DomainFinding])] = {
    val stored = storedIn(form, field)
    vocabularies.get(field.kind) match {
      case Some(vocabulary) =>
        Some(vocabularyField(field, vocabulary, stored, limits.getOrElse(field.binding, Vector.empty), parameters))
      case None =>
        ImmediateKinds.get(field.kind).map((signed, fallback) => immediateField(field, signed, fallback, stored))
    }
  }

  private def vocabularyField(
      field: SASSFormat.Field,
      vocabulary: SASSSymbols.Vocabulary,
      stored: Option[Stored],
      limits: Vector[Limit],
      parameters: Map[String, BigInt]
  ): (FieldDomain, Vector[DomainFinding]) = {
    val values       = vocabulary.values.filterNot(_.name.startsWith("INVALID"))
    val whole        = compress(values.map(value => value.name -> value.number.toInt))
    val topCode      = values.map(_.number.toInt).maxOption.getOrElse(0)
    val widthFinding = stored.filter(s => topCode >= (1 << s.bits)).map { s =>
      DomainFinding(
        "field-width",
        field.kind,
        s"codes reach $topCode but the field stores ${s.bits} bits",
        Some("the stored width bounds the domain")
      )
    }
    val narrowed = stored.fold(whole)(s => whole.flatMap(within(_, 1 << s.bits)))
    val capped   = limits.foldLeft(narrowed -> Vector.empty[DomainFinding]) { case ((current, found), l) =>
      parameters.get(l.parameter).fold(current -> found) { count =>
        val result = current.flatMap(bounded(_, count.toInt - l.reserved, l.excluded))
        val before = current.collect { case r: OperandDomain.Registers => r.last }.maxOption
        val after  = result.collect { case r: OperandDomain.Registers => r.last }.maxOption
        val added  = (before, after) match {
          case (Some(b), Some(a)) if a < b =>
            Vector(DomainFinding(
              "register-limit",
              field.kind,
              s"vocabulary reaches index $b, ${l.parameter} = $count limits it to $a",
              Some("the OOR_REG_ERROR conditions bound the register")
            ))
          case _ => Vector.empty
        }
        result -> (found ++ added)
      }
    }
    (FieldDomain(field.binding, field.kind, OperandDomain.union(capped._1)), widthFinding.toVector ++ capped._2)
  }

  private def immediateField(
      field: SASSFormat.Field,
      signed: Boolean,
      fallback: Int,
      stored: Option[Stored]
  ): (FieldDomain, Vector[DomainFinding]) = {
    val floating  = field.kind.startsWith("F") && field.kind.endsWith("Imm")
    val declared  = if (floating) Some(fallback) else field.arguments.flatMap(_.takeWhile(_ != '/').trim.toIntOption)
    val effective = if (floating) None else stored.map(s => s.bits + s.shift)
    val bits      = (declared, effective) match {
      case (Some(d), Some(e)) => d.min(e)
      case (Some(d), None)    => d
      case (None, Some(e))    => e
      case (None, None)       => fallback
    }
    val findings = (declared, effective) match {
      case (Some(d), Some(e)) if d != e =>
        Vector(DomainFinding(
          "immediate-width",
          field.kind,
          s"declared $d bits, stored $e bits",
          Some(if (e < d) "the stored width bounds the value" else "the declared width bounds the value")
        ))
      case _ => Vector.empty
    }
    (FieldDomain(field.binding, field.kind, OperandDomain.Immediate(bits, signed)), findings)
  }

  private def compress(values: Vector[(String, Int)]): Vector[OperandDomain] = {
    val sorted    = values.sortBy(_._2)
    val registers = sorted.collect {
      case (name @ BankRef(bank, index), code) if canonical(name) => (bank, index.toInt, code)
    }.distinct
    val files = registers.groupBy(_._1).toVector.flatMap { (bank, items) =>
      runs(items.sortBy(_._2).map(item => item._2 -> item._3)).map(run =>
        OperandDomain.Registers(Banks(bank), first = run.head._1, last = run.last._1, code = run.head._2)
      )
    }
    val rest     = sorted.filter((name, _) => !BankRef.matches(name) || !canonical(name))
    val numbered = rest.collect { case (Indexed(prefix, index), code) => (prefix, index.toInt, code) }
    val ranges   = numbered.groupBy(_._1).toVector.flatMap { (prefix, items) =>
      runs(items.sortBy(_._2).map(item => item._2 -> item._3)).collect {
        case run if run.size > 1 =>
          OperandDomain.NamedRange(prefix, first = run.head._1, last = run.last._1, code = run.head._2)
      }
    }
    val covered = ranges.flatMap(_.codes.map(_._1)).toSet
    val named   = rest.collect { case (name, code) if !covered(name) => OperandDomain.Named(name, code) }
    (files ++ ranges ++ named).sortBy(part => (OperandDomain.firstCode(part), part.codes.headOption.map(_._1)))
  }

  private def canonical(name: String): Boolean = name match {
    case BankRef(_, index) => index == index.toInt.toString
    case _                 => false
  }

  private def runs(items: Vector[(Int, Int)]): Vector[Vector[(Int, Int)]] =
    items.foldLeft(Vector.empty[Vector[(Int, Int)]]) { (acc, item) =>
      acc.lastOption match {
        case Some(run) if run.last._1 + 1 == item._1 && run.last._2 + 1 == item._2 => acc.init :+ (run :+ item)
        case _                                                                     => acc :+ Vector(item)
      }
    }

  private def within(part: OperandDomain, limit: Int): Option[OperandDomain] = part match {
    case r: OperandDomain.Registers =>
      Option.when(r.code < limit)(r.copy(last = r.last.min(r.first + limit - 1 - r.code)))
    case n: OperandDomain.NamedRange =>
      Option.when(n.code < limit)(n.copy(last = n.last.min(n.first + limit - 1 - n.code)))
    case OperandDomain.Named(_, code) => Option.when(code < limit)(part)
    case other                        => Some(other)
  }

  private def bounded(part: OperandDomain, bound: Int, excluded: Set[String]): Option[OperandDomain] = part match {
    case r: OperandDomain.Registers if r.file == "r" || r.file == "ur" =>
      val top = if (r.file == "r" && excluded("R254")) bound.min(253) else bound
      Option.when(r.first <= top)(r.copy(last = r.last.min(top)))
    case other => Some(other)
  }

  private def storedIn(form: SASSEncoding.Form, field: SASSFormat.Field): Option[Stored] = {
    val expressions = form.fields.flatMap(assignment => assignment.expression.toOption.map(assignment -> _))
    def direct(expression: Expression): Option[(String, Int)] = expression match {
      case Expression.Reference(name, None, _)                                   => Some(name -> 0)
      case Expression.Relaxed(inner)                                             => direct(inner)
      case Expression.Scale(inner, factor) if factor > 0 && factor.bitCount == 1 =>
        direct(inner).map((name, shift) => name -> (shift + factor.bitLength - 1))
      case _ => None
    }
    def using(name: String)                   = expressions.filter(_._2.references(bases = false)(name))
    def attempt(name: String): Option[Stored] = {
      val found   = using(name)
      val directs = found.map((assignment, expression) =>
        direct(expression).filter(_._1 == name).map(d => Stored(assignment.positions.size, d._2))
      )
      Option.when(found.nonEmpty && directs.forall(_.isDefined))(directs.flatten).map(all =>
        Stored(all.map(_.bits).max, all.map(_.shift).max)
      )
    }
    attempt(field.binding).orElse(if (using(field.binding).isEmpty) attempt(field.kind) else None)
  }

  private def registerLimits(source: SASSDescription.InstructionClass): Map[String, Vector[Limit]] = {
    import ConditionExpression.*
    def constant(expression: ConditionExpression): Option[BigInt] = expression match {
      case Number(value)                              => Some(value)
      case Binary(BinaryOperator.Divide, left, right) =>
        constant(left).zip(constant(right)).collect { case (l, r) if r != 0 => l / r }
      case Binary(BinaryOperator.Multiply, left, right) => constant(left).zip(constant(right)).map(_ * _)
      case Binary(BinaryOperator.Add, left, right)      => constant(left).zip(constant(right)).map(_ + _)
      case _                                            => None
    }
    def parameterOf(expression: ConditionExpression, binding: String): Option[(String, Int)] = expression match {
      case Binary(
            BinaryOperator.LessEqual,
            Field(`binding`, None),
            Binary(BinaryOperator.Subtract, Parameter(name), reserved)
          ) if constant(reserved).exists(_ >= 1) => constant(reserved).map(name -> _.toInt)
      case Binary(_, left, right) => parameterOf(left, binding).orElse(parameterOf(right, binding))
      case _                      => None
    }
    def excludedBy(expression: ConditionExpression, binding: String): Set[String] = expression match {
      case Binary(BinaryOperator.NotEqual, Field(`binding`, None), EnumValue(_, name)) => Set(name)
      case Binary(_, left, right) => excludedBy(left, binding) ++ excludedBy(right, binding)
      case _                      => Set.empty
    }
    source.sections.filter(section => section.name == "CONDITION" || section.name == "CONDITIONS").flatMap { section =>
      SASSConditions.parse(section.body).toOption.toVector.flatten
    }.flatMap { condition =>
      condition.expression match {
        case Binary(
              BinaryOperator.LogicalOr,
              Binary(BinaryOperator.Equal, Field(binding, None), EnumValue(_, zero)),
              rest
            )
            if ZeroNames.contains(zero) =>
          parameterOf(rest, binding).map((name, reserved) =>
            binding -> Limit(name, reserved, excludedBy(rest, binding))
          )
        case _ => None
      }
    }.groupMap(_._1)(_._2)
  }
}
