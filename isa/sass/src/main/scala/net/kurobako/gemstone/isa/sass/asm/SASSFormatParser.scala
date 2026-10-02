package net.kurobako.gemstone.isa.sass.asm

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.isa.sass.*
import net.kurobako.gemstone.isa.sass.asm.*
import scala.collection.mutable
import scala.util.Try

/** Parses one canonical SASS instruction using the generated FORMAT tree for its selected form. */
object SASSFormatParser {
  private enum Piece {
    case Fixed(value: String, optional: Boolean = false)
    case Field(value: NVDisasm.Syntax.Field, depth: Int, qualifier: Boolean)
    case OptionalGroup(size: Int)
  }

  private case class State(offset: Int, bindings: Map[String, BigInt])

  def parse(
      database: NVDisasm.Database,
      form: NVDisasm.Form,
      address: Int,
      text: String
  ): Either[String, Map[String, BigInt]] = {
    import NVDisasm.Syntax.*
    val source          = normalize(text.stripSuffix(";"))
    val hasOpcodeMarker = form.syntax.exists {
      case NVDisasm.Syntax.Text(raw) => raw.contains("Opcode")
      case _                         => false
    }
    val opcodeFields: Vec[Field] = if (hasOpcodeMarker) Vec(Field("Opcode", "Opcode", None, None))
    else Vec.empty[Field]
    val fields   = SASSFormatBindings.fields(form.syntax, includeMarkedOptionals = true) ++ opcodeFields
    val analysis = SASSFormatBindings.Analysis(
      database,
      form,
      Some(fields),
      includeBaseForAttributes = false
    )
    val declared                             = analysis.declared
    val encodedNames                         = analysis.encodedNames
    def encodedBinding(field: Field): String = analysis.encodedBinding(field)

    flatten(form).flatMap { format =>
      val pieces = format
      val seeded = declared.foldLeft(Map.empty[String, BigInt]) { (bindings, field) =>
        val name = encodedBinding(field)
        if (bindings.contains(name)) bindings
        else defaultValue(database, field).orElse(singleVocabularyValue(database, field))
          .fold(bindings)(value => bindings.updated(name, value))
      }
      val names    = declared.map(field => (field.kind, field.binding) -> encodedBinding(field)).toMap
      val required = encodedNames
      val defaults = seeded ++ required.filter(_.contains("@")).map(_ -> BigInt(0))
      consume(database, form, pieces, source, address, Map.empty, names).flatMap { parsed =>
        val completeText = parsed.filter(_.offset == source.length).map(state => defaults ++ state.bindings).distinct
        val completeBindings = completeText.filter(bindings => required.subsetOf(bindings.keySet))
        val legal            = completeBindings.flatMap { bindings =>
          Legality.check(database, form, Legality.completeBindings(form, bindings)).toOption.map(_ => bindings)
        }
        if (parsed.isEmpty) Left(s"text does not match ${form.name} FORMAT: $text")
        else if (completeText.isEmpty) Left(s"trailing or incomplete text for ${form.name} FORMAT: $text")
        else if (completeBindings.isEmpty) {
          val missing = required -- completeText.flatMap(_.keySet).toSet
          Left(s"${form.name} FORMAT is missing required bindings: ${missing.toVec.sorted.mkString(", ")}")
        } else if (legal.isEmpty) {
          val reason = completeBindings.flatMap(bindings =>
            Legality.check(database, form, Legality.completeBindings(form, bindings)).left.toOption.map(_.message).toVec
          ).headOption.getOrElse("unknown legality failure")
          Left(reason)
        } else if (legal.size == 1) Right(legal.head)
        else Left(s"ambiguous ${form.name} FORMAT: ${legal.size} binding sets")
      }
    }
  }

  private def flatten(form: NVDisasm.Form): Either[String, Vec[Piece]] = {
    import NVDisasm.Syntax.*
    val opcodeField: Field = Field("Opcode", "Opcode", None, None)
    @annotation.tailrec
    def nextFieldHasDefault(remaining: Vec[NVDisasm.Syntax]): Boolean = remaining match {
      case Vec()                                           => false
      case Text(raw) +: tail if raw.forall(_.isWhitespace) => nextFieldHasDefault(tail)
      case (field: Field) +: _                             => field.default.nonEmpty
      case _                                               => false
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
                (value.trim.endsWith(".") && nextFieldHasDefault(tail))
            ) +: rest
          }
        }
      case Literal(raw) +: tail =>
        val value = formatText(raw)
        loop(tail, depth, None).map { rest =>
          if (value.isEmpty) rest
          else Piece.Fixed(value, optional = value == "," && nextFieldHasDefault(tail)) +: rest
        }
      case (field: Field) +: tail =>
        val omittedPredicate = afterComma(tail).filter(_ =>
          field.default.nonEmpty && NVDisasm.fieldKind(field.kind).isPredicate &&
            !Set("Pg", "UPg")(field.binding)
        )
        omittedPredicate match {
          case Some(remaining) => loop(remaining, depth, None).map { rest =>
              val group = Vec[Piece](Piece.Field(field, depth + 1, qualifier = false), Piece.Fixed(","))
              Piece.OptionalGroup(group.size) +: (group ++ rest)
            }
          case None =>
            loop(tail, depth, None).map(Piece.Field(field, depth, previousText.exists(_.trim.endsWith("."))) +: _)
        }
      case Group(open, close, children) +: tail
          if open == "[" && close == "]" && children.forall {
            case Text(raw) => Set("!", "-", "~", "||")(raw.trim)
            case _         => false
          } =>
        loop(tail, depth, None)
      case Group("{", "}", children) +: tail
          if SASSFormatBindings.fields(
            children,
            includeMarkedOptionals = true
          ).forall(_.binding.startsWith("reuse_")) =>
        loop(tail, depth, None)
      case Group("{", "}", children) +: tail
          if SASSFormatBindings.fields(children, includeMarkedOptionals = true).exists(field =>
            NVDisasm.fieldKind(field.kind) == NVDisasm.FieldKind.ConditionCode
          ) &&
            SASSFormatBindings.fields(children, includeMarkedOptionals = true).forall(_.default.nonEmpty) =>
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
      case Optional(Some(marker), _) +: tail if Set("&", "?")(marker) => loop(tail, depth, None)
      case Optional(marker, _) +: _ => Left(s"unsupported optional FORMAT syntax ${marker.getOrElse("<none>")}")
      case _                        => Left("unsupported FORMAT sequence")
    }
    loop(form.syntax, 0, None)
  }

  private def consume(
      database: NVDisasm.Database,
      form: NVDisasm.Form,
      pieces: Vec[Piece],
      source: String,
      address: Int,
      initial: Map[String, BigInt],
      encodedNames: Map[(String, String), String]
  ): Either[String, Vec[State]] = {
    val maximumWork                               = 100000
    val visited                                   = mutable.HashSet.empty[(Int, State)]
    def run(index: Int, state: State): Vec[State] =
      if (visited.size > maximumWork) Vec.empty
      else if (!visited.add(index -> state)) Vec.empty
      else if (visited.size > maximumWork) Vec.empty
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
          val capturedEnds = (state.offset to source.length).view
          capturedEnds.flatMap { end =>
            if (visited.size > maximumWork) Vec.empty[State]
            else {
              val spelling = source.substring(state.offset, end)
              decodeField(database, field, spelling, address, form, depth, qualifier).toVec.flatMap { parsed =>
                val encoded = encodedNames.getOrElse((field.kind, field.binding), field.binding)
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
    val result = run(0, State(0, initial))
    Either.cond(visited.size <= maximumWork, result, s"${form.name}: FORMAT parse exceeded $maximumWork search states")
  }

  private def decodeField(
      database: NVDisasm.Database,
      field: NVDisasm.Syntax.Field,
      spelling: String,
      address: Int,
      form: NVDisasm.Form,
      depth: Int,
      qualifier: Boolean
  ): Option[Vec[(String, BigInt)]] = {
    val raw = spelling.trim
    if (raw.isEmpty && (depth == 0 || qualifier) && !NVDisasm.fieldKind(field.kind).isRegister)
      defaultValue(database, field).map(value => Vec("" -> value))
    else if (raw.isEmpty) None
    else {
      val (token, attributes) =
        if (
          Set(
            NVDisasm.FieldKind.SignedImmediate,
            NVDisasm.FieldKind.RelativeImmediate
          )(NVDisasm.fieldKind(field.kind)) ||
          NVDisasm.fieldKind(field.kind).isFloatingImmediate
        )
          raw -> Vec.empty[(String, Int)]
        else decorations(raw)
      val names   = database.byVocabulary.get(field.kind).fold(Vec.empty[NVDisasm.SymbolValue])(_.values)
      val numeric = SASSFormatBindings.number(token)
      val value   = names.find(_.name == token).map(_.value).orElse {
        if (field.binding == "Opcode") fieldOpcode(form, token)
        else None
      }
      value.orElse(parseRegister(NVDisasm.fieldKind(field.kind), token))
        .orElse(parseImmediate(database, field, token, address, numeric))
        .map(number => Vec("" -> number) ++ attributes.map { case (name, bit) => name -> BigInt(bit) })
    }
  }

  private def parseRegister(category: NVDisasm.FieldKind, token: String): Option[BigInt] =
    if (
      !category.isRegister && !Set(
        NVDisasm.FieldKind.BarrierRegister,
        NVDisasm.FieldKind.Scoreboard,
        NVDisasm.FieldKind.CbuState
      )(category)
    ) None
    else SASSOperandParser.parse(token).toOption.collect { case Operand.Register(bank, index) =>
      if (token.endsWith("Z")) BigInt(if (bank == RegisterBank.UR) 63 else 255) else BigInt(index.raw)
    }

  private def parseImmediate(
      database: NVDisasm.Database,
      field: NVDisasm.Syntax.Field,
      token: String,
      address: Int,
      numeric: Option[BigInt]
  ): Option[BigInt] = {
    val width = field.arguments.flatMap(_.takeWhile(_ != '/').toIntOption)
    NVDisasm.fieldKind(field.kind) match {
      case NVDisasm.FieldKind.Immediate       => numeric.filter(_ >= 0)
      case NVDisasm.FieldKind.SignedImmediate =>
        val parsed = SASSOperandParser.parse(token).toOption
        parsed.flatMap {
          case Operand.Integer(value) if value >= 0              => Some(value)
          case Operand.Bits(value, _)                            => Some(value)
          case Operand.Unary(Operand.Integer(value), true, _, _) =>
            width.filter(_ > 0).map(bits => (BigInt(1) << bits) - value)
          case Operand.Unary(Operand.Bits(value, _), true, _, _) =>
            width.filter(_ > 0).map(bits => (BigInt(1) << bits) - value)
          case _ => None
        }
      case NVDisasm.FieldKind.RelativeImmediate => numeric.flatMap { target =>
          val bits  = width.getOrElse(0)
          val delta = target - (BigInt(address) + (if (database.width == 128) 16 else 8))
          if (bits > 0 && delta >= -(BigInt(1) << (bits - 1)) && delta < (BigInt(1) << (bits - 1)))
            Some(if (delta < 0) (BigInt(1) << bits) + delta else delta)
          else None
        }
      case NVDisasm.FieldKind.FloatingImmediate(format) => floatingBits(format, token)
      case _                                            => None
    }
  }

  private def fieldOpcode(form: NVDisasm.Form, token: String): Option[BigInt] =
    form.opcodes.find(_.name == token).map(_.value)

  private def defaultValue(database: NVDisasm.Database, field: NVDisasm.Syntax.Field): Option[BigInt] =
    field.default match {
      case Some(value) => database.byVocabulary.get(field.kind).toVec.flatMap(_.values)
          .find(_.name == value).map(_.value).orElse(SASSFormatBindings.number(value))
      case None => database.byVocabulary.get(field.kind).flatMap(_.default)
    }

  private def singleVocabularyValue(database: NVDisasm.Database, field: NVDisasm.Syntax.Field): Option[BigInt] =
    database.byVocabulary.get(field.kind).flatMap(_.values.map(_.value).distinct match {
      case Vec(value) => Some(value)
      case _          => None
    })

  private def floatingBits(kind: FloatEncoding, token: String): Option[BigInt] = {
    val expectedWidth = kind.width.map(_.raw).getOrElse(0)
    val rawBits       = token match {
      case s"bits<$format>:$bits(0x$hex)" if format == kind.spelling && bits.toIntOption.contains(expectedWidth) =>
        Try(BigInt(hex, 16)).toOption
      case s"bits<$bits>(0x$hex)" if bits.toIntOption.contains(expectedWidth) => Try(BigInt(hex, 16)).toOption
      case _                                                                  => None
    }
    val parsed = rawBits.orElse {
      SASSOperandParser.parse(token).toOption.flatMap {
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
    // F64 and E8M7 FORMAT values represent the high bits; their low payload bits are implicit zeroes.
    val shift = kind match {
      case FloatEncoding.F64Imm  => 32
      case FloatEncoding.E8M7Imm => 16
      case _                     => 0
    }
    parsed.filter(bits =>
      bits >= 0 && bits.bitLength <= expectedWidth &&
        (bits & ((BigInt(1) << shift) - 1)) == 0
    ).map(_ >> shift)
  }

  private def floatToRaw(kind: FloatEncoding, value: Double): Option[BigInt] = kind match {
    case FloatEncoding.F16Imm  => Some(BigInt(IEEE754.floatToBinary16(value.toFloat).raw & 0xffff))
    case FloatEncoding.BF16Imm =>
      val bits = java.lang.Float.floatToRawIntBits(value.toFloat)
      Some(BigInt((bits + 0x7fff + ((bits >>> 16) & 1)) >>> 16))
    case FloatEncoding.F32Imm | FloatEncoding.TF32Imm | FloatEncoding.E8M7Imm =>
      Some(BigInt(java.lang.Float.floatToRawIntBits(value.toFloat) & 0xffffffffL))
    case FloatEncoding.F64Imm =>
      Some(BigInt(java.lang.Long.toUnsignedString(java.lang.Double.doubleToRawLongBits(value))))
    case _ => None
  }

  private def decorations(raw: String): (String, Vec[(String, Int)]) = {
    val marks  = raw.takeWhile("!-~".contains)
    val parsed = marks.foldLeft((raw.drop(marks.length), Vec.empty[(String, Int)])) {
      case ((remaining, attributes), '!') => remaining -> (attributes :+ ("not"    -> 1))
      case ((remaining, attributes), '-') => remaining -> (attributes :+ ("negate" -> 1))
      case ((remaining, attributes), '~') => remaining -> (attributes :+ ("invert" -> 1))
      case (state, _)                     => state
    }
    val (remaining, attributes) = parsed
    if (remaining.startsWith("|") && remaining.endsWith("|") && remaining.length >= 2)
      remaining.drop(1).dropRight(1) -> (attributes :+ ("absolute" -> 1))
    else remaining                   -> attributes
  }

  // FORMAT whitespace controls presentation; Parser has already checked lexical instruction boundaries.
  private def normalize(value: String): String = value.filterNot(_.isWhitespace)

  private def formatText(value: String): String =
    normalize(value.replace('/', '.').replace("*", "").replace(";", ""))
}
