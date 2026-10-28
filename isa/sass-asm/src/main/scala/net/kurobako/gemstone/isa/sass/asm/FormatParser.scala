package net.kurobako.gemstone.isa.sass.asm

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.isa.catalogue.*
import net.kurobako.gemstone.isa.sass.*
import net.kurobako.gemstone.isa.sass.asm.*
import net.kurobako.gemstone.string.*
import scala.collection.mutable

/** Parses one canonical SASS instruction using the generated FORMAT tree for its selected form. */
object FormatParser {
  private enum Piece {
    case Fixed(value: String, optional: Boolean = false)
    case Field(value: NVDisasm.Syntax.Field, depth: Int, qualifier: Boolean)
    case OptionalGroup(size: Int)
  }

  private case class State(offset: Int, bindings: Map[String, BigInt])

  /** What parsing a form needs of the form and its database alone, found once. */
  private case class Prepared(
      pieces: Either[String, Vec[Piece]],
      required: Set[String],
      names: Map[(String, String), String],
      defaults: Map[String, BigInt],
      descriptorField: Option[String],
      explicitDescriptor: Option[(String, BigInt)],
      typed: Vec[(String, NVDisasm.Syntax.Field, FieldDomain)]
  )

  // forms and databases are large and never copied, so the caches compare them by identity
  private val layouts =
    java.util.Collections.synchronizedMap(java.util.IdentityHashMap[NVDisasm.Form, Either[String, Vec[Piece]]]())
  private val prepared =
    java.util.Collections.synchronizedMap(java.util.IdentityHashMap[
      NVDisasm.Database,
      java.util.Map[NVDisasm.Form, Prepared]
    ]())

  private def layout(form: NVDisasm.Form): Either[String, Vec[Piece]] =
    layouts.computeIfAbsent(form, _ => flatten(form))

  private def prepare(database: NVDisasm.Database, form: NVDisasm.Form): Prepared =
    prepared.computeIfAbsent(
      database,
      _ => java.util.Collections.synchronizedMap(java.util.IdentityHashMap[NVDisasm.Form, Prepared]())
    )
      .computeIfAbsent(form, _ => build(database, form))

  private def build(database: NVDisasm.Database, form: NVDisasm.Form): Prepared = {
    val declared = NVDisasm.Syntax.fields(form.syntax, includeMarkedOptionals = true) ++
      Option.when(form.syntax.exists {
        case NVDisasm.Syntax.Text(raw) => raw.contains("Opcode")
        case _                         => false
      })(opcodeField)
    val kindBindings                                         = NVDisasm.Syntax.kindBindings(declared)
    val required                                             = form.encodedReferences
    def encodedBinding(field: NVDisasm.Syntax.Field): String =
      if (required(field.binding) || !kindBindings.get(field.kind).contains(field.binding)) field.binding
      else if (required(field.kind)) field.kind
      else field.binding
    val names    = declared.map(field => (field.kind, field.binding) -> encodedBinding(field)).toMap
    val defaults = declared.foldLeft(Map.empty[String, BigInt]) { (bindings, field) =>
      val name = encodedBinding(field)
      if (bindings.contains(name)) bindings
      else
        defaultValue(database, field).orElse(database.vocabulary(field).map(_.value).distinct match {
          case Vec(value) => Some(value)
          case _          => None
        }).fold(bindings)(value => bindings.updated(name, value))
    } ++ required.filter(_.contains("@")).map(_ -> BigInt(0)) ++ descriptorRegisters(form.syntax).flatMap(field =>
      database.vocabulary(field).find(_.name == "URZ").map(encodedBinding(field) -> _.value)
    )
    // Writing `desc[URx]` selects the descriptor, which the encoding records in its exp_desc qualifier.
    val descriptorField    = declared.find(_.category == NVDisasm.FieldKind.DescriptorRegister).map(encodedBinding(_))
    val explicitDescriptor = declared.find(_.kind == "EXP_DESC").flatMap(field =>
      database.vocabulary(field).find(entry => !defaultValue(database, field).contains(entry.value))
        .map(encodedBinding(field) -> _.value)
    ).orElse(Option.when(required("e_desc"))("e_desc" -> BigInt(1)))
    val typed = declared.flatMap { field =>
      SassDomains.field(database, form, field.binding).filter(domain =>
        domain.label == field.kind && domain.immediate.isEmpty
      ).map((encodedBinding(field), field, _))
    }
    Prepared(layout(form), required, names, defaults, descriptorField, explicitDescriptor, typed)
  }

  /** The first bound value no domain of the form holds; a field's own default is always written. */
  private def outsideDomain(
      encoding: NVDisasm.Encoding,
      prepared: Prepared,
      bindings: Map[String, BigInt]
  ): Option[String] = {
    val NVDisasm.Encoding(database, form) = encoding
    prepared.typed.collectFirst(Function.unlift { (name, field, domain) =>
      bindings.get(name).filterNot(value => domain.contains(value) || database.defaultValues(field).contains(value))
        .map(value =>
          s"${field.binding}=$value is outside the ${field.kind} domain of ${form.name}: ${domain.describe}"
        )
    })
  }

  def parse(
      database: NVDisasm.Database,
      form: NVDisasm.Form,
      address: Int,
      text: String
  ): Either[String, Map[String, BigInt]] = {
    val source      = normalise(text.stripSuffix(";"))
    val preparation = prepare(database, form)
    val encoding    = NVDisasm.Encoding(database, form)
    val Prepared(pieces, required, names, defaults, descriptorField, explicitDescriptor, _) = preparation
    def described(bindings: Map[String, BigInt]): Map[String, BigInt]                       =
      (descriptorField, explicitDescriptor) match {
        case (Some(descriptor), Some((name, value))) if bindings.contains(descriptor) =>
          bindings.updated(name, value)
        case _ => bindings
      }
    def legality(bindings: Map[String, BigInt]) =
      Legality.check(encoding, Legality.completeBindings(form, bindings))
    pieces.flatMap(consume(database, form, _, source, address, names)).flatMap { parsed =>
      val complete =
        parsed.filter(_.offset == source.length).map(state => defaults ++ described(state.bindings)).distinct
      val bound = complete.filter(bindings => required.subsetOf(bindings.keySet))
      val legal = bound.filter(legality(_).isRight)
      val typed = legal.filter(outsideDomain(encoding, preparation, _).isEmpty)
      if (parsed.isEmpty) Left(s"text does not match ${form.name} FORMAT: $text")
      else if (complete.isEmpty) Left(s"trailing or incomplete text for ${form.name} FORMAT: $text")
      else if (bound.isEmpty) {
        val missing = required -- complete.flatMap(_.keySet).toSet
        Left(s"${form.name} FORMAT is missing required bindings: ${missing.toVec.sorted.mkString(", ")}")
      } else
        if (legal.isEmpty) Left(bound.flatMap(legality(_).left.toOption).head.message)
        else
          typed match {
            case Vec(bindings) => Right(bindings)
            case Vec()         => Left(legal.flatMap(outsideDomain(encoding, preparation, _)).head)
            case _             =>
              // fields whose values print the same text read as their default
              typed.filter(bindings =>
                typed.forall(other =>
                  other.keySet.forall(key =>
                    other.get(key) == bindings.get(key) || bindings.get(key).exists(defaults.get(key).contains)
                  )
                )
              ) match {
                case Vec(bindings) => Right(bindings)
                case _             => Left(s"ambiguous ${form.name} FORMAT: ${typed.size} binding sets")
              }
          }
    }
  }

  private val opcodeField = NVDisasm.Syntax.field("Opcode", "Opcode", None, None)

  /** Text of `form` in its canonical layout with each field `spell` spells, or why the FORMAT cannot be laid out. A
    * field left unspelled takes its default, and optional text appears only where the field it introduces is spelled.
    * The opcode is spelled `opcode`, and a space sets the operands apart from it and its modifiers.
    */
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  def render(
      form: NVDisasm.Form,
      opcode: String,
      spell: NVDisasm.Syntax.Field => Option[String],
      braced: Boolean = true
  ): Either[String, String] = layout(form).map { pieces =>
    def spelled(piece: Piece): Option[String] = piece match {
      case Piece.Field(field, _, _) if field.binding == "Opcode" => Some(opcode)
      case Piece.Field(field, _, _)                              => spell(field).filter(_.nonEmpty)
      case _                                                     => None
    }
    def omitted(from: Int, size: Int): Boolean = !pieces.slice(from + 1, from + 1 + size).exists(spelled(_).nonEmpty)
    @annotation.tailrec
    def introduces(from: Int): Boolean = pieces.lift(from) match {
      case Some(Piece.OptionalGroup(size)) if omitted(from, size) => introduces(from + size + 1)
      case Some(piece: Piece.Field)                               => spelled(piece).nonEmpty
      case Some(_)                                                => introduces(from + 1)
      case None                                                   => false
    }
    val text     = mutable.StringBuilder()
    var operands = false
    var adjacent = false
    var index    = 0
    while (index < pieces.size) {
      pieces(index) match {
        case Piece.OptionalGroup(size) =>
          if (omitted(index, size)) index += size
        case Piece.Fixed("{" | "}", _) if !braced => ()
        case Piece.Fixed(value, optional)         =>
          if (!optional || introduces(index + 1)) {
            if (operands && !value.startsWith(".") && !text.endsWith(" ")) { text ++= " "; operands = false }
            text ++= (if (value == ",") ", " else value)
            adjacent = false
          }
        case piece @ Piece.Field(field, _, qualifier) =>
          spelled(piece).foreach { value =>
            if (field.binding == "Opcode") {
              if (text.nonEmpty) text ++= " "
              operands = true
            } else if ((operands || adjacent) && !qualifier) {
              text ++= " "
              operands = false
            }
            text ++= value
            adjacent = field.binding != "Opcode"
          }
      }
      index += 1
    }
    text.result()
  }

  /** The register fields inside the `[...]` that follows a memory descriptor field. */
  private def descriptorRegisters(syntax: Vec[NVDisasm.Syntax]): Vec[NVDisasm.Syntax.Field] = {
    import NVDisasm.Syntax.*
    syntax.zip(syntax.drop(1)).flatMap {
      case (field: Field, Group("[", "]", children)) if field.category == NVDisasm.FieldKind.DescriptorRegister =>
        NVDisasm.Syntax.fields(children, includeMarkedOptionals = true).filter(_.category.isRegister)
      case _ => Vec.empty[Field]
    } ++ syntax.flatMap {
      case Group(_, _, children) => descriptorRegisters(children)
      case Optional(_, children) => descriptorRegisters(children)
      case _                     => Vec.empty[Field]
    }
  }

  private def flatten(form: NVDisasm.Form): Either[String, Vec[Piece]] = {
    import NVDisasm.Syntax.*
    @annotation.tailrec
    def nextFieldHasDefault(remaining: Vec[NVDisasm.Syntax]): Boolean = remaining match {
      case Vec()                                                          => false
      case Text(raw) +: tail if raw.forall(_.isWhitespace)                => nextFieldHasDefault(tail)
      case (group: Group) +: tail if NVDisasm.Syntax.operatorGroup(group) => nextFieldHasDefault(tail)
      case (field: Field) +: _                                            => field.default.nonEmpty
      case _                                                              => false
    }
    def afterComma(remaining: Vec[NVDisasm.Syntax]): Option[Vec[NVDisasm.Syntax]] = {
      val significant = remaining.dropWhile {
        case Text(raw) => raw.forall(_.isWhitespace)
        case _         => false
      }
      significant match {
        case Literal(",") +: tail => Some(tail)
        case _                    => None
      }
    }
    def loop(
        remaining: Vec[NVDisasm.Syntax],
        depth: Int,
        previousText: Option[String]
    ): Either[String, Vec[Piece]] = remaining match {
      case Vec()             => Right(Vec.empty)
      case Text(raw) +: tail =>
        val withPredicate = raw.replace("PREDICATE @", "@")
        if (withPredicate.contains("Opcode")) {
          val suffix = formatText(withPredicate.drop(withPredicate.indexOf("Opcode") + "Opcode".length))
          loop(tail, depth, Some(suffix)).map { rest =>
            val optionalQualifier = suffix.trim.startsWith(".") && nextFieldHasDefault(tail)
            Piece.Field(opcodeField, depth, qualifier = false) +:
              (if (suffix.isEmpty) rest
               else Piece.Fixed(suffix, optional = suffix.trim.isEmpty || optionalQualifier) +: rest)
          }
        } else {
          val formatted = formatText(withPredicate)
          val value     = if (raw.contains("PREDICATE")) formatted.trim else formatted
          loop(tail, depth, Some(value)).map { rest =>
            if (value.isEmpty) rest
            else Piece.Fixed(
              value,
              optional = (value.contains("@") && raw.contains("PREDICATE")) ||
                ((value.trim.endsWith(".") || value.trim == "+") && nextFieldHasDefault(tail))
            ) +: rest
          }
        }
      case Literal(raw) +: tail =>
        val value = formatText(raw)
        loop(tail, depth, None).map { rest =>
          if (value.isEmpty) rest
          else Piece.Fixed(value, optional = value == "," && nextFieldHasDefault(tail)) +: rest
        }
      // nvdisasm omits a memory descriptor the instruction does not use, so text may leave out `desc[URx]`.
      case (field: Field) +: (group @ Group("[", "]", _)) +: tail
          if field.category == NVDisasm.FieldKind.DescriptorRegister =>
        for {
          register <- loop(Vec(group), depth, None)
          rest     <- loop(tail, depth, None)
          descriptor = Piece.Field(field, depth, qualifier = false) +: register
        } yield Piece.OptionalGroup(descriptor.size) +: (descriptor ++ rest)
      // nvdisasm prints `[RZ + offset]` as `[offset]`, so a defaulted zero register may go with its `+`.
      case (field: Field) +: Text(plus) +: tail
          if field.category == NVDisasm.FieldKind.ZeroRegister && field.default.nonEmpty &&
            formatText(plus).trim == "+" =>
        loop(tail, depth, None).map { rest =>
          val group = Vec[Piece](Piece.Field(field, depth, qualifier = false), Piece.Fixed(formatText(plus)))
          Piece.OptionalGroup(group.size) +: (group ++ rest)
        }
      case (field: Field) +: tail =>
        val omittedPredicate = afterComma(tail).filter(_ =>
          field.default.nonEmpty && field.category.isPredicate &&
            !Set("Pg", "UPg")(field.binding)
        )
        omittedPredicate match {
          case Some(afterPredicate) => loop(afterPredicate, depth, None).map { rest =>
              val group = Vec[Piece](Piece.Field(field, depth + 1, qualifier = false), Piece.Fixed(","))
              Piece.OptionalGroup(group.size) +: (group ++ rest)
            }
          case None =>
            loop(tail, depth, None).map(Piece.Field(field, depth, previousText.exists(_.trim.endsWith("."))) +: _)
        }
      case (group: Group) +: tail if NVDisasm.Syntax.operatorGroup(group) => loop(tail, depth, None)
      case Group("{", "}", children) +: tail
          if NVDisasm.Syntax.fields(
            children,
            includeMarkedOptionals = true
          ).forall(_.binding.startsWith("reuse_")) =>
        loop(tail, depth, None)
      case Group("{", "}", children) +: tail
          if NVDisasm.Syntax.fields(children, includeMarkedOptionals = true).exists(field =>
            field.category == NVDisasm.FieldKind.ConditionCode
          ) &&
            NVDisasm.Syntax.fields(children, includeMarkedOptionals = true).forall(_.default.nonEmpty) =>
        // Maxwell puts the condition's comma outside the braces; Kepler includes it inside.
        val trailing = afterComma(tail)
        for {
          inner <- loop(children, depth + 1, None)
          rest  <- loop(trailing.getOrElse(tail), depth, None)
          group = inner ++ trailing.toVec.map(_ => Piece.Fixed(","))
        } yield Piece.OptionalGroup(group.size) +: (group ++ rest)
      case Group(open, close, children) +: tail =>
        for {
          inner <- loop(children, depth + 1, None)
          rest  <- loop(tail, depth, None)
        } yield Piece.Fixed(formatText(open)) +:
          (inner ++ (if (close.isEmpty) rest else Piece.Fixed(formatText(close)) +: rest))
      case Optional(None, children) +: tail =>
        for {
          inner <- loop(children, depth, previousText)
          rest  <- loop(tail, depth, None)
        } yield inner ++ rest
      case Optional(Some("&" | "?"), _) +: tail => loop(tail, depth, None)
      case Optional(marker, _) +: _ => Left(s"unsupported optional FORMAT syntax ${marker.getOrElse("<none>")}")
      case _                        => Left("unsupported FORMAT sequence")
    }
    // A form's statement ends at its `;`; a FORMAT_ALIAS after it derives a field and is not written.
    val statement = form.syntax.indexWhere {
      case Text(raw) => raw.contains(';')
      case _         => false
    } match {
      case -1 => form.syntax
      case at =>
        form.syntax.take(at) ++
          (form.syntax(at) match {
            case Text(raw) => Vec(Text(raw.takeWhile(_ != ';')))
            case _         => Vec.empty[NVDisasm.Syntax]
          })
    }
    loop(statement, 0, None)
  }

  /** Reads the fields of `form`, written at `address`, against the vocabulary of `database`. */
  private class FieldReader(database: NVDisasm.Database, form: NVDisasm.Form, address: Int) {
    def decodeField(field: NVDisasm.Syntax.Field, spelling: String, depth: Int, qualifier: Boolean)
        : Option[Vec[(String, BigInt)]] = {
      val raw = spelling.trim
      if (raw.isEmpty && (depth == 0 || qualifier || field.default.nonEmpty) && !field.category.isRegister)
        defaultValue(database, field).map(value => Vec("" -> value))
      else if (raw.isEmpty) None
      else {
        val (token, attributes) = field.category match {
          case NVDisasm.FieldKind.SignedImmediate | NVDisasm.FieldKind.RelativeImmediate |
              NVDisasm.FieldKind.FloatingImmediate(_) => raw -> Vec.empty[(String, Int)]
          case _ => decorations(raw)
        }
        database.vocabulary(field).find(_.name == token).map(_.value)
          .orElse(Option.when(field.binding == "Opcode")(form.opcodes.find(_.name == token).map(_.value)).flatten)
          .orElse(parseRegister(SassDomains.field(database, form, field.binding), token))
          .orElse(parseImmediate(field, token, NVDisasm.number(token)))
          .map(number => Vec("" -> number) ++ attributes.map { case (name, bit) => name -> BigInt(bit) })
      }
    }

    private def parseImmediate(field: NVDisasm.Syntax.Field, token: String, numeric: Option[BigInt]): Option[BigInt] = {
      val width = field.arguments.flatMap(_.takeWhile(_ != '/').toIntOption)
      field.category match {
        case NVDisasm.FieldKind.Immediate       => numeric.filter(_ >= 0)
        case NVDisasm.FieldKind.SignedImmediate =>
          val parsed                    = OperandParser.parse(token).toOption
          def complement(value: BigInt) = width.filter(_ > 0).map(bits => (BigInt(1) << bits) - value)
          parsed.flatMap {
            case Operand.Integer(value) if value >= 0              => Some(value)
            case Operand.Bits(value, _)                            => Some(value)
            case Operand.Unary(Operand.Integer(value), true, _, _) => complement(value)
            case Operand.Unary(Operand.Bits(value, _), true, _, _) => complement(value)
            case _                                                 => None
          }
        case NVDisasm.FieldKind.RelativeImmediate => numeric.flatMap { target =>
            val bits  = width.getOrElse(0)
            val delta = target - (BigInt(address) + (if (database.width == 128) 16 else 8))
            if (bits > 0 && delta >= -(BigInt(1) << (bits - 1)) && delta < (BigInt(1) << (bits - 1)))
              Some(if (delta < 0) (BigInt(1) << bits) + delta else delta)
            else None
          }
        case NVDisasm.FieldKind.FloatingImmediate(format) =>
          floatingBits(format, token, FormatBindings.floatingShift(database, form, field, format))
        case _ => None
      }
    }
  }

  private def consume(
      database: NVDisasm.Database,
      form: NVDisasm.Form,
      pieces: Vec[Piece],
      source: String,
      address: Int,
      encodedNames: Map[(String, String), String]
  ): Either[String, Vec[State]] = {
    val reader                                    = FieldReader(database, form, address)
    val maximumWork                               = 100000
    val visited                                   = mutable.HashSet.empty[(Int, State)]
    def run(index: Int, state: State): Vec[State] =
      if (visited.size > maximumWork || !visited.add(index -> state)) Vec.empty
      else if (index == pieces.size) Vec(state)
      else if (state.offset > source.length) Vec.empty
      else pieces(index) match {
        case Piece.OptionalGroup(size)    => run(index + 1, state) ++ run(index + size + 1, state)
        case Piece.Fixed(value, optional) =>
          val matched = if (source.startsWith(value, state.offset))
            run(index + 1, state.copy(offset = state.offset + value.length))
          else Vec.empty[State]
          if (optional) matched ++ run(index + 1, state) else matched
        case Piece.Field(field, depth, qualifier) =>
          val encoded      = encodedNames.getOrElse((field.kind, field.binding), field.binding)
          val capturedEnds = (state.offset to source.length).view
          capturedEnds.flatMap { end =>
            if (visited.size > maximumWork) Vec.empty[State]
            else {
              val spelling = source.substring(state.offset, end)
              reader.decodeField(field, spelling, depth, qualifier).toVec.flatMap { parsed =>
                val updates = parsed.map { case (suffix, value) =>
                  (if (suffix.isEmpty) encoded else s"$encoded@$suffix") -> value
                }.toMap
                val merged = updates.foldLeft(Option(state.bindings)) { case (current, (name, value)) =>
                  current.flatMap(previous =>
                    previous.get(name).fold(Option(previous.updated(name, value)))(old =>
                      Option.when(old == value)(previous)
                    )
                  )
                }
                merged.toVec.flatMap(values => run(index + 1, State(end, values)))
              }
            }
          }.toVec
      }
    val result = run(0, State(0, Map.empty))
    Either.cond(visited.size <= maximumWork, result, s"${form.name}: FORMAT parse exceeded $maximumWork search states")
  }

  private def parseRegister(domain: Option[FieldDomain], token: String): Option[BigInt] =
    // Zero-register encodings come from the architecture vocabulary.
    if (token.endsWith("Z")) None
    else domain.flatMap(_.bank).flatMap(expected =>
      OperandParser.parse(token).toOption.collect {
        case Operand.Register(bank, index) if bank == RegisterBank.fromSpelling(expected) => BigInt(index)
      }
    )

  /** The display default, falling back to the vocabulary's starred default value. */
  private def defaultValue(database: NVDisasm.Database, field: NVDisasm.Syntax.Field): Option[BigInt] =
    if (field.default.nonEmpty) database.defaultValues(field).headOption
    else database.byVocabulary.get(field.kind).flatMap(_.default)

  private def floatingBits(kind: FloatEncoding, token: String, shift: Int): Option[BigInt] = {
    val expectedWidth = kind.width.rawElements.getOrElse(0)
    val rawBits       = token match {
      case s"bits<$format>:$bits(0x$hex)" if format == kind.spelling && bits.toIntOption.contains(expectedWidth) =>
        parseBigIntLiteral(s"0x$hex")
      case s"bits<$bits>(0x$hex)" if bits.toIntOption.contains(expectedWidth) => parseBigIntLiteral(s"0x$hex")
      case _                                                                  => None
    }
    val parsed = rawBits.orElse {
      OperandParser.parse(token).toOption.flatMap {
        case Operand.Integer(value)                   => floatToRaw(kind, value.toDouble)
        case Operand.Floating(value, _, negativeZero) =>
          val number = value.toDouble
          floatToRaw(kind, if (negativeZero) -0.0 else number)
        case Operand.FloatingSymbol(FloatingConstant.INF) => floatToRaw(kind, Double.PositiveInfinity)
        case Operand.FloatingSymbol(FloatingConstant.QNAN | FloatingConstant.SNAN | FloatingConstant.NAN) =>
          floatToRaw(kind, Double.NaN)
        case Operand.Unary(Operand.Floating(value, _, _), negative, _, _) =>
          floatToRaw(kind, if (negative) -value.toDouble else value.toDouble)
        case Operand.Unary(Operand.FloatingSymbol(FloatingConstant.INF), negative, _, _) =>
          floatToRaw(kind, if (negative) Double.NegativeInfinity else Double.PositiveInfinity)
        case Operand.Unary(
              Operand.FloatingSymbol(FloatingConstant.QNAN | FloatingConstant.SNAN | FloatingConstant.NAN),
              _,
              _,
              _
            ) => floatToRaw(kind, Double.NaN)
        case _ => None
      }
    }
    parsed.filter(bits =>
      bits >= 0 && bits.bitLength <= expectedWidth &&
        (bits & ((BigInt(1) << shift) - 1)) == 0
    ).map(_ >> shift)
  }

  private def floatToRaw(kind: FloatEncoding, value: Double): Option[BigInt] = kind match {
    case FloatEncoding.F16Imm  => Some(BigInt(IEEE754.fromDouble(value, 16)))
    case FloatEncoding.BF16Imm => Some(BigInt(IEEE754.doubleToBfloat16(value).toLong))
    case FloatEncoding.F32Imm | FloatEncoding.TF32Imm | FloatEncoding.E8M7Imm =>
      Some(BigInt(IEEE754.fromDouble(value, 32)))
    case FloatEncoding.F64Imm => Some(BigInt(IEEE754.doubleToBinary64(value)) & ((BigInt(1) << 64) - 1))
    case _                    => None
  }

  private def decorations(raw: String): (String, Vec[(String, Int)]) = {
    val marks      = raw.takeWhile("!-~".contains)
    val remaining  = raw.drop(marks.length)
    val attributes = marks.collect {
      case '!' => "not"    -> 1
      case '-' => "negate" -> 1
      case '~' => "invert" -> 1
    }.toVec
    if (remaining.startsWith("|") && remaining.endsWith("|") && remaining.length >= 2)
      remaining.drop(1).dropRight(1) -> (attributes :+ ("absolute" -> 1))
    else remaining                   -> attributes
  }

  // FORMAT whitespace controls presentation; Parser has already checked lexical instruction boundaries.
  private def normalise(value: String): String = value.filterNot(_.isWhitespace)

  private def formatText(value: String): String =
    normalise(value.replace('/', '.').replace("*", "").replace(";", ""))
}
