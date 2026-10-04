package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.sass.*
import net.kurobako.gemstone.isa.sass.Instruction as SASSInstruction
import net.kurobako.gemstone.string.*

/** Decodes generated SASS FORMAT schemas directly to structural execution operands. */
object InstructionDecoder {
  private enum FormatPiece {
    case Text(value: String)
    case Field(field: NVDisasm.Syntax.Field, value: Option[Operand])
    case Group(open: String, close: String, children: Vec[FormatPiece])
  }

  /** Decode FORMAT fields directly to typed operands; this path never consumes rendered disassembly text. */
  def decodeTyped(
      address: Int >= 0,
      bytes: Array[Byte],
      target: String = "SM89"
  ): Either[String, DecodedInstruction] =
    for {
      database   <- NVDisasm.forTarget(target).toRight(s"no generated decoder for $target")
      candidates <- FieldDecoder.candidates(database, bytes)
      rendered =
        candidates.filterNot(_.alternate).map(candidate => decodeCandidate(database, address, candidate, bytes))
      errors = rendered.flatMap(_.left.toOption).filterNot(_.category == NVDisasm.FailureCategory.IllegalCandidate)
      _ <- Either.cond(errors.isEmpty, (), errors.map(_.message).distinct.mkString("; "))
      decoded = rendered.flatMap(_.toOption).distinctBy(value =>
        (value.mnemonic, value.modifiers, value.operands, value.guard)
      )
      result <- if (decoded.size == 1) Right(decoded.head)
      else if (decoded.isEmpty) Left(rendered.flatMap(_.left.toOption).map(_.message).distinct.mkString("; "))
      else Left(s"ambiguous $target instruction: ${decoded.map { value =>
          val origin = value.machine.fold("")(machine => s" ${machine.form.name}#${machine.form.formId}")
          s"${value.mnemonic.raw}${value.modifiers.map(_.name).mkString(".")} ${value.operands.mkString(", ")}$origin"
        }.mkString("; ")}")
    } yield result

  private def decodeCandidate(
      database: NVDisasm.Database,
      address: Int >= 0,
      originalCandidate: FieldDecoder.Candidate,
      bytes: Array[Byte]
  ): Either[NVDisasm.CandidateFailure, DecodedInstruction] = {
    import NVDisasm.Syntax.*
    val form                                          = originalCandidate.form
    val analysis                                      = SASSFormatBindings.Analysis(database, form)
    val declared                                      = analysis.declared
    val candidate                                     = FieldDecoder.refine(database, originalCandidate, analysis)
    val encodedNames                                  = analysis.encodedNames
    def attribute(field: Field, name: String): BigInt = candidate.bindings
      .get(s"${field.binding}@$name")
      .orElse(candidate.bindings.get(s"${analysis.encodedBinding(field)}@$name"))
      .getOrElse(BigInt(0))
    def raw(field: Field): Option[BigInt] = candidate.bindings.get(field.binding)
      .orElse(candidate.bindings.get(analysis.encodedBinding(field)))
      .orElse(Option.when(
        analysis.vocabulary(field).map(_.value).distinct.size == 1
      )(analysis.vocabulary(field).head.value))
      .orElse(Option.when(
        !encodedNames(analysis.encodedBinding(field))
      )(analysis.defaultValues(field).headOption).flatten)
    def isDefault(field: Field, value: BigInt): Boolean = analysis.defaultValues(field).contains(value) &&
      Vec("not", "negate", "absolute", "invert").forall(name => attribute(field, name) == 0)
    def laterPredicate(field: Field): Boolean = {
      val index = declared.indexWhere(value => value.binding == field.binding && value.kind == field.kind)
      declared.drop(index + 1).takeWhile(value => value.kind == field.kind && value.default.nonEmpty)
        .exists(next => raw(next).exists(value => !analysis.defaultValues(next).contains(value)))
    }
    def symbol(field: Field, value: BigInt): Option[String] = {
      val names     = analysis.vocabulary(field).filter(_.value == value).map(_.name)
      val valid     = names.filterNot(_.startsWith("INVALID"))
      val preferred = if (NVDisasm.fieldKind(field.kind) == NVDisasm.FieldKind.SpecialRegister)
        valid.find(_.startsWith("SR_")).orElse(valid.lastOption)
      else if (NVDisasm.fieldKind(field.kind).isPredicate) valid.find(_.endsWith("T")).orElse(valid.headOption)
      else if (NVDisasm.fieldKind(field.kind).isRegister) valid.sortBy(_.length).headOption
      else valid.find(name => name.startsWith("U") && valid.contains(name.drop(1)))
        .orElse(valid.filterNot(_.startsWith("_")).sortBy(_.length).headOption).orElse(valid.headOption)
      preferred.map(name =>
        if (Set(NVDisasm.FieldKind.ConstantBank, NVDisasm.FieldKind.DescriptorRegister)(NVDisasm.fieldKind(field.kind)))
          name.toLowerCaseRoot
        else name
      )
    }
    def signed(value: BigInt, bits: Int): BigInt =
      if (bits > 0 && value.testBit(bits - 1)) value - (BigInt(1) << bits) else value
    def hexadecimalOperand(value: BigInt): Operand =
      if (value < 0) Operand.Unary(Operand.Bits(-value, 0), negative = true, not = false, absolute = false)
      else Operand.Bits(value, 0)
    def fieldWidth(field: Field): Option[Int]            = field.arguments.flatMap(_.takeWhile(_ != '/').toIntOption)
    def register(name: String): Option[Operand.Register] = {
      val parts = name match {
        case s"UR$index" => Some("UR" -> index)
        case s"UP$index" => Some("UP" -> index)
        case s"SB$index" => Some("SB" -> index)
        case s"R$index"  => Some("R" -> index)
        case s"B$index"  => Some("B" -> index)
        case _           => None
      }
      parts.flatMap { (bank, suffix) =>
        val index = if (suffix == "Z") Some(255)
        else Option.when(suffix.nonEmpty && suffix.forall(c => c >= '0' && c <= '9'))(suffix).flatMap(_.toIntOption)
        index.flatMap(_.refine[Int in 0 ~ 255].toOption)
          .map(value => Operand.Register(RegisterBank.fromSpelling(bank), value))
      }
    }
    def isOperandKind(field: Field): Boolean = {
      val category = NVDisasm.fieldKind(field.kind)
      category.isRegister || category.isPredicateOperand || category.isFloatingImmediate ||
      Set(
        NVDisasm.FieldKind.BarrierRegister,
        NVDisasm.FieldKind.CbuState,
        NVDisasm.FieldKind.Scoreboard,
        NVDisasm.FieldKind.ConstantBank,
        NVDisasm.FieldKind.DescriptorRegister,
        NVDisasm.FieldKind.SpecialRegister,
        NVDisasm.FieldKind.Immediate,
        NVDisasm.FieldKind.SignedImmediate,
        NVDisasm.FieldKind.RelativeImmediate,
        NVDisasm.FieldKind.ConditionRegisterBank,
        NVDisasm.FieldKind.PredicateRegister
      )(category)
    }
    def unsupported(message: String): NVDisasm.CandidateFailure =
      NVDisasm.CandidateFailure(NVDisasm.FailureCategory.Unsupported, message)
    def illegal(message: String): NVDisasm.CandidateFailure =
      NVDisasm.CandidateFailure(NVDisasm.FailureCategory.IllegalCandidate, message)
    def fieldOperand(
        field: Field,
        depth: Int,
        qualifier: Boolean
    ): Either[NVDisasm.CandidateFailure, Option[Operand]] =
      if (NVDisasm.fieldKind(field.kind) == NVDisasm.FieldKind.PredicateRegister) Right(Some(Operand.Symbol("PR")))
      else raw(field) match {
        case None => Left(unsupported(s"${candidate.name}: missing ${field.binding} (${field.kind})"))
        case Some(value) if analysis.vocabulary(field).nonEmpty && symbol(field, value).isEmpty =>
          Left(illegal(s"illegal ${field.kind}=$value (${field.binding})"))
        case Some(_) if !isOperandKind(field) => Right(None)
        case Some(value)
            if (depth == 0 || qualifier) && !NVDisasm.fieldKind(field.kind).isRegister &&
              isDefault(field, value) && !laterPredicate(field) => Right(None)
        case Some(value) =>
          val kind                                                  = field.kind
          val converted: Either[NVDisasm.CandidateFailure, Operand] = NVDisasm.fieldKind(field.kind) match {
            case category if category.isPredicate =>
              val name = symbol(field, value).getOrElse(
                if (category == NVDisasm.FieldKind.UniformPredicate) "UPT" else "PT"
              )
              predicate(name).map[Operand](identity)
                .toRight(unsupported(s"unknown predicate $name (${field.binding})"))
            case NVDisasm.FieldKind.SpecialRegister =>
              symbol(field, value).map(name => Operand.SpecialRegister(SpecialRegister.fromSpelling(name)))
                .toRight(unsupported(s"unknown special register $value (${field.binding})"))
            case NVDisasm.FieldKind.FloatingImmediate(baseFormat) =>
              val format = candidate.floatFormats.getOrElse(field.binding, baseFormat)
              val width  = format.width.toRight(unsupported(s"unsupported floating format ${format.spelling}"))
              val bits   = value << SASSFormatBindings.floatingShift(database, form, field, format)
              width.map(value => Operand.FloatingBits(format, value, bits))
            case NVDisasm.FieldKind.Immediate       => Right(hexadecimalOperand(value))
            case NVDisasm.FieldKind.SignedImmediate =>
              Right(hexadecimalOperand(fieldWidth(field).fold(value)(signed(value, _))))
            case NVDisasm.FieldKind.RelativeImmediate =>
              val bits = fieldWidth(field).getOrElse(0)
              val pc   = BigInt(address.raw) + (if (database.width == 128) 16 else 8)
              Right(hexadecimalOperand(pc + signed(value, bits)))
            case NVDisasm.FieldKind.ConstantBank => symbol(field, value).map(_ => Operand.Symbol("c"))
                .toRight(illegal(s"illegal constant selector $value (${field.binding})"))
            case NVDisasm.FieldKind.ConditionCode => symbol(field, value).map(Operand.Symbol.apply)
                .toRight(illegal(s"illegal condition-code selector $value (${field.binding})"))
            case NVDisasm.FieldKind.ConditionRegisterBank => symbol(field, value).map(Operand.Symbol.apply)
                .toRight(illegal(s"illegal condition register bank $value (${field.binding})"))
            case NVDisasm.FieldKind.DescriptorRegister =>
              symbol(field, value).map(name => Operand.SpecialRegister(SpecialRegister.fromSpelling(name)))
                .toRight(unsupported(s"unknown descriptor $value (${field.binding})"))
            case NVDisasm.FieldKind.BarrierRegister =>
              symbol(field, value).flatMap(name => register(name)).map[Operand](identity)
                .toRight(unsupported(s"unknown barrier register $value (${field.binding})"))
            case NVDisasm.FieldKind.CbuState =>
              val name = symbol(field, value).getOrElse(value.toString)
              Right(register(name).map[Operand](identity).getOrElse(Operand.Symbol(name)))
            case NVDisasm.FieldKind.Scoreboard =>
              symbol(field, value).flatMap(name => register(name)).map[Operand](identity)
                .toRight(unsupported(s"unknown scoreboard register $value (${field.binding})"))
            case _ =>
              val name = symbol(field, value).getOrElse(value.toString)
              register(name).map[Operand](
                identity
              ).orElse(Option.when(name.startsWith("SR_") || name == "PR")(
                Operand.SpecialRegister(SpecialRegister.fromSpelling(name))
              )).orElse(if (NVDisasm.fieldKind(kind).isPredicateOperand) predicate(name) else None)
                .toRight(unsupported(s"cannot decode operand $name (${field.binding}:${field.kind})"))
          }
          converted.map { value =>
            val not       = attribute(field, "not") == 1
            val negative  = attribute(field, "negate") == 1
            val absolute  = attribute(field, "absolute") == 1
            val inverted  = attribute(field, "invert") == 1
            val decorated = value match {
              case Operand.Predicate(index, uniform, negated) if not || negative || inverted =>
                Operand.Predicate(index, uniform, negated ^ not ^ negative ^ inverted)
              case _ if (not || negative || absolute || inverted) && !(not && inverted) =>
                Operand.Unary(value, negative, not || inverted, absolute)
              case _ if not && inverted =>
                Operand.Decorated(
                  value,
                  Map(
                    OperandDecoration.Not      -> Operand.Integer(1),
                    OperandDecoration.Invert   -> Operand.Integer(1),
                    OperandDecoration.Negate   -> Operand.Integer(if (negative) 1 else 0),
                    OperandDecoration.Absolute -> Operand.Integer(if (absolute) 1 else 0)
                  )
                )
              case _ => value
            }
            Some(decorated)
          }
      }
    def pieces(nodes: Vec[NVDisasm.Syntax], depth: Int = 0): Either[NVDisasm.CandidateFailure, Vec[FormatPiece]] =
      nodes.zipWithIndex.foldLeft[Either[NVDisasm.CandidateFailure, Vec[FormatPiece]]](Right(Vec.empty)) {
        case (result, (node, index)) =>
          result.flatMap { previous =>
            val qualifier = nodes.lift(index - 1).exists {
              case Text(raw) => raw.trim.endsWith("/")
              case _         => false
            }
            val next: Either[NVDisasm.CandidateFailure, Vec[FormatPiece]] = node match {
              case Text(raw)    => Right(Vec(FormatPiece.Text(raw)))
              case Literal(raw) => Right(Vec(FormatPiece.Text(raw)))
              case field: Field =>
                fieldOperand(field, depth, qualifier).map(value => Vec(FormatPiece.Field(field, value)))
              case Optional(marker, children) if marker.isEmpty        => pieces(children, depth)
              case Optional(marker, _) if marker.exists(Set("&", "?")) => Right(Vec.empty)
              case Optional(_, _) => Left(unsupported(s"unsupported optional syntax in ${candidate.name}"))
              case Group("{", "}", children)
                  if SASSFormatBindings.fields(children).forall(_.binding.startsWith("reuse_")) =>
                Right(Vec.empty)
              case Group("[", "]", children) if children.forall {
                    case Text(raw) => Set("!", "-", "~", "||")(raw.trim)
                    case _         => false
                  } => Right(Vec.empty)
              case Group(open, close, children) => pieces(children, depth + 1).map(value =>
                  Vec(FormatPiece.Group(open, close, value))
                )
            }
            next.map(previous ++ _)
          }
      }
    def guard: Option[SASSInstruction.Guard] = declared.find(field => Set("Pg", "UPg")(field.binding))
      .flatMap { field =>
        raw(field).flatMap(value => symbol(field, value)).flatMap { name =>
          predicate(name).map { decoded =>
            val not = attribute(field, "not") == 1
            decoded.copy(negated = decoded.negated ^ not)
          }
        }
      }
      .filterNot(value => value.index.isEmpty && !value.negated)
      .map(SASSInstruction.Guard.apply)
    def operandPieces(all: Vec[FormatPiece]): Vec[FormatPiece] = {
      val afterOpcode = all.dropWhile {
        case FormatPiece.Text(raw) if raw.contains("Opcode") => false
        case _                                               => true
      }.headOption.fold(all) {
        case FormatPiece.Text(raw) =>
          val marker = raw.indexOf("Opcode")
          Vec(FormatPiece.Text(raw.drop(marker + "Opcode".length))) ++ all.dropWhile {
            case FormatPiece.Text(value) if value.contains("Opcode") => false
            case _                                                   => true
          }.drop(1)
        case _ => all
      }
      afterOpcode
    }
    def decodedModifiers(all: Vec[FormatPiece]): Vec[SASSInstruction.Modifier] = {
      val prefix = operandPieces(all).takeWhile {
        case FormatPiece.Field(field, _) if Set("Pg", "UPg")(field.binding) => true
        case FormatPiece.Field(field, _)                                    => !isOperandKind(field)
        case _                                                              => true
      }
      def modifiers(pieces: Vec[FormatPiece], preserveDefaults: Boolean = false): Vec[SASSInstruction.Modifier] =
        pieces.flatMap {
          case FormatPiece.Text(raw) =>
            raw.split(Array(';', ',', '*', '.', '/', ' ', '\t', '\r', '\n', '\f', '\u000b')).toVec.filter(_.nonEmpty)
              .map(SASSInstruction.Modifier(_))
          case FormatPiece.Group("{", "}", children) if !children.exists {
                case FormatPiece.Field(field, _) => isOperandKind(field)
                case _                           => false
              } =>
            val groupedFields = children.collect { case FormatPiece.Field(field, _) => field }
            val changed       = groupedFields.exists(field => raw(field).exists(value => !isDefault(field, value)))
            modifiers(children, preserveDefaults || (groupedFields.size > 1 && changed))
          case FormatPiece.Field(field, _) if !Set("Pg", "UPg")(field.binding) =>
            raw(field).toVec.flatMap { value =>
              val isDefault = analysis.defaultValues(field).contains(value) &&
                Vec("not", "negate", "absolute", "invert").forall(attribute(field, _) == 0)
              val isStep = NVDisasm.fieldKind(field.kind) == NVDisasm.FieldKind.StepField
              Option.when(!isDefault || isStep || preserveDefaults) {
                val spelling = symbol(field, value).getOrElse(value.toString)
                spelling.split('.').toVec.filter(_.nonEmpty).map(SASSInstruction.Modifier(_))
              }.toVec.flatten
            }
          case _ => Vec.empty
        }
      modifiers(prefix)
    }
    def operandValue(piece: FormatPiece): Vec[Operand] = piece match {
      case FormatPiece.Field(_, Some(value))     => Vec(value)
      case FormatPiece.Field(_, None)            => Vec.empty
      case FormatPiece.Group("{", "}", children) =>
        val childrenValues = children.flatMap(child => operandValue(child))
        val selected       = children.zipWithIndex.collect {
          case (FormatPiece.Field(field, _), index)
              if children.lift(index - 1).exists {
                case FormatPiece.Text(text) => text.trim.endsWith("/")
                case _                      => false
              } && children.take(index).exists {
                case FormatPiece.Field(previous, Some(_)) if isOperandValueField(previous) => true
                case group: FormatPiece.Group => operandValue(group).nonEmpty
                case _                        => false
              } =>
            raw(field).filter(value => !analysis.defaultValues(field).contains(value))
              .flatMap(symbol(field, _))
        }.flatten
        val qualified = selected.foldLeft(childrenValues) { (values, selector) =>
          values.lastOption.fold(values)(last => values.init :+ qualify(last, selector))
        }
        qualified match {
          case Vec(single)               => Vec(single)
          case values if values.nonEmpty => Vec(Operand.Group(values))
          case _                         => Vec.empty
        }
      case FormatPiece.Group("[", "]", children) =>
        val values       = children.flatMap(child => operandValue(child))
        val addressModes = children.zipWithIndex.collect {
          case (FormatPiece.Field(field, _), index)
              if NVDisasm.fieldKind(field.kind) == NVDisasm.FieldKind.AddressQualifier ||
                children.lift(index - 1).exists {
                  case FormatPiece.Text(text) => text.trim.endsWith("/")
                  case _                      => false
                } =>
            raw(field).filterNot(analysis.defaultValues(field).contains).flatMap(symbol(field, _)).filter(value =>
              Set("X1", "X2", "X4", "X8", "X16", "U32", "U64", "32", "64")(value)
            )
        }.flatten
        val mode                           = addressModes.headOption.map(AddressMode.fromSpelling)
        val scale: Int in (1, 2, 4, 8, 16) = mode.flatMap(AddressMode.scale).getOrElse(1)
        values match {
          case Vec(Operand.Symbol("c"), Operand.Bits(bank, _), offset) =>
            Vec(Operand.ConstantBank(bank, offset))
          case Vec(Operand.Symbol("c"), Operand.Integer(bank), offset) =>
            Vec(Operand.ConstantBank(bank, offset))
          case Vec(Operand.Register(bank, index), displacement) if offsetValue(displacement).nonEmpty =>
            Vec(Operand.Address(
              Option.when(index.raw != 255)(Operand.Register(
                bank,
                index
              )),
              None,
              offsetValue(displacement).get,
              scale,
              mode,
              None
            ))
          case Vec(Operand.Register(baseBank, baseIndex), Operand.Register(indexBank, indexIndex), offset)
              if offsetValue(offset).nonEmpty =>
            Vec(Operand.Address(
              Option.when[Operand.Register](baseIndex.raw != 255)(
                Operand.Register(baseBank, baseIndex)
              ).orElse(Option.when[Operand.Register](indexIndex.raw != 255)(
                Operand.Register(indexBank, indexIndex)
              )),
              Option.when[Operand.Register](
                baseIndex.raw != 255 &&
                  indexIndex.raw != 255
              )(
                Operand.Register(indexBank, indexIndex)
              ),
              offsetValue(offset).get,
              scale,
              mode,
              None
            ))
          case Vec(
                Operand.Register(bank, index),
                Operand.Unary(Operand.Bits(displacement, _), true, false, false)
              ) =>
            Vec(Operand.Address(
              Option.when(index.raw != 255)(Operand.Register(
                bank,
                index
              )),
              None,
              -displacement,
              scale,
              mode,
              None
            ))
          case Vec(
                Operand.SpecialRegister(descriptor),
                Operand.Address(base, index, displacement, scale, mode, _)
              ) =>
            Vec(Operand.Address(
              base,
              index,
              displacement,
              scale,
              mode,
              Some(Operand.SpecialRegister(descriptor))
            ))
          case Vec(Operand.Register(bank, index)) =>
            Vec(Operand.Address(
              Option.when(index.raw != 255)(Operand.Register(
                bank,
                index
              )),
              None,
              0,
              scale,
              mode,
              None
            ))
          case _ => values
        }
      case FormatPiece.Group(_, _, children) => children.flatMap(child => operandValue(child))
      case FormatPiece.Text(_)               => Vec.empty
    }
    def qualify(value: Operand, selector: String): Operand = value match {
      case Operand.Unary(inner, negative, not, absolute) =>
        Operand.Unary(qualify(inner, selector), negative, not, absolute)
      case Operand.Decorated(inner, attributes) => Operand.Decorated(qualify(inner, selector), attributes)
      case Operand.Qualified(inner, selectors)  =>
        Operand.Qualified(inner, OperandSelector.fromSpellings(selectors.map(_.spelling) :+ selector))
      case other => Operand.Qualified(other, OperandSelector.fromSpellings(Vec(selector)))
    }
    def isOperandValueField(field: Field, allowCC: Boolean = false): Boolean =
      (isOperandKind(field) || (allowCC && NVDisasm.fieldKind(field.kind) == NVDisasm.FieldKind.ConditionCode)) &&
        !Set("Pg", "UPg")(field.binding)
    def offsetValue(value: Operand): Option[BigInt] = value match {
      case Operand.Integer(number)                                    => Some(number)
      case Operand.Bits(number, _)                                    => Some(number)
      case Operand.Unary(Operand.Bits(number, _), true, false, false) => Some(-number)
      case _                                                          => None
    }
    def constantOffset(value: Operand): Operand = value match {
      case Operand.Address(None, None, displacement, scale, _, None) if scale.raw == 1 =>
        Operand.Bits(displacement, 0)
      case other => other
    }
    def operandValues(chunk: Vec[FormatPiece], allowCC: Boolean): Vec[Operand] = {
      val hasDescriptor = candidate.bindings.get("e_desc").orElse(candidate.bindings.get("EXP_DESC"))
        .map(_ != BigInt(0)).getOrElse(declared.exists(field =>
          NVDisasm.fieldKind(field.kind) == NVDisasm.FieldKind.DescriptorRegister && raw(field).nonEmpty
        ))
      val values = chunk.flatMap {
        case FormatPiece.Field(field, Some(value)) if isOperandValueField(field, allowCC) => Vec(value)
        case FormatPiece.Field(field, None)
            if allowCC && NVDisasm.fieldKind(field.kind) == NVDisasm.FieldKind.ConditionCode =>
          raw(field).flatMap(symbol(field, _)).map(Operand.Symbol.apply).toVec
        case group: FormatPiece.Group => operandValue(group)
        case _                        => Vec.empty[Operand]
      }
      val coalesced = values match {
        case Vec(Operand.Symbol("c"), bank, offset) if offsetValue(bank).nonEmpty =>
          Vec(Operand.ConstantBank(offsetValue(bank).get, constantOffset(offset)))
        case Vec(Operand.Unary(Operand.Symbol("c"), negative, not, absolute), bank, offset)
            if offsetValue(bank).nonEmpty =>
          Vec(Operand.Unary(
            Operand.ConstantBank(offsetValue(bank).get, constantOffset(offset)),
            negative,
            not,
            absolute
          ))
        case Vec(Operand.Decorated(Operand.Symbol("c"), attributes), bank, offset)
            if offsetValue(bank).nonEmpty =>
          Vec(Operand.Decorated(
            Operand.ConstantBank(offsetValue(bank).get, constantOffset(offset)),
            attributes
          ))
        case Vec(
              Operand.SpecialRegister(SpecialRegister.desc),
              Operand.Address(Some(descriptor @ Operand.Register(bank, _)), _, _, _, _, _),
              address @ Operand.Address(_, _, _, _, _, _)
            ) if bank == RegisterBank.UR =>
          if (hasDescriptor) Vec(address.copy(descriptor = Some(descriptor))) else Vec(address)
        case _ => values.filterNot(_ == Operand.SpecialRegister(SpecialRegister.desc))
      }
      val selectors = chunk.zipWithIndex.collect { case (FormatPiece.Field(field, _), index) =>
        val name           = raw(field).flatMap(symbol(field, _))
        val slashQualifier = chunk.lift(index - 1).exists {
          case FormatPiece.Text(text) => text.trim.endsWith("/")
          case _                      => false
        } && chunk.take(index).exists {
          case FormatPiece.Field(previous, Some(_)) if isOperandValueField(previous, allowCC) => true
          case group: FormatPiece.Group => operandValue(group).nonEmpty
          case _                        => false
        }
        name.filter(value =>
          slashQualifier ||
            (NVDisasm.fieldKind(field.kind) == NVDisasm.FieldKind.OptionalConditionCode && value == "CC") ||
            NVDisasm.fieldKind(field.kind) == NVDisasm.FieldKind.PartitionSelector ||
            NVDisasm.fieldKind(field.kind) == NVDisasm.FieldKind.InstructionSelector
        )
          .filter(_ => !analysis.defaultValues(field).contains(raw(field).getOrElse(BigInt(-1))))
      }.flatten
      selectors.foldLeft(coalesced) { (operands, selector) =>
        operands.lastOption.fold(operands) { last =>
          operands.init :+ qualify(last, selector)
        }
      }
    }
    def splitOperands(all: Vec[FormatPiece]): Vec[Vec[FormatPiece]] = {
      val split = all.foldLeft((Vec.empty[Vec[FormatPiece]], Vec.empty[FormatPiece])) {
        case ((finished, current), piece @ FormatPiece.Text(raw)) =>
          val boundaries = -1 +: raw.indices.filter(raw(_) == ',').toVec :+ raw.length
          val parts      = boundaries.sliding(2).map(pair => raw.substring(pair(0) + 1, pair(1))).toVec
          val first      = current :+ piece.copy(value = parts.head)
          val rest       = parts.tail
          rest.foldLeft((finished, first)) { case ((done, active), part) =>
            (done :+ active, Vec(FormatPiece.Text(part)))
          }
        case ((finished, current), piece) => (finished, current :+ piece)
      }
      split._1 :+ split._2
    }
    for {
      _ <- Either.cond(
        form.syntax.nonEmpty,
        (),
        NVDisasm.CandidateFailure(
          NVDisasm.FailureCategory.MissingMetadata,
          s"missing FORMAT tree ${candidate.name}"
        )
      )
      invalidAttribute = declared.find(field =>
        Vec("not", "negate", "absolute", "invert").exists { name =>
          val value = attribute(field, name)
          value != 0 && value != 1
        }
      )
      _ <- Either.cond(
        invalidAttribute.isEmpty,
        (),
        NVDisasm.CandidateFailure(
          NVDisasm.FailureCategory.Unsupported,
          s"invalid attributes on ${invalidAttribute.map(_.binding).getOrElse(candidate.name)}"
        )
      )
      rawBindings = declared.foldLeft(candidate.bindings) { (bindings, field) =>
        raw(field).fold(bindings)(value => bindings.updated(field.binding, value))
      }
      completeBindings = Legality.completeBindings(form, rawBindings)
      _ <- Legality.check(database, form, completeBindings)
      _ <- Either.cond(
        candidate.issues.forall(_.permittedForStructuralRoundTrip),
        (),
        NVDisasm.CandidateFailure(
          NVDisasm.FailureCategory.Unsupported,
          candidate.issues.map(_.message).mkString("; ")
        )
      )
      format <- pieces(form.syntax)
      opcodeParts = candidate.opcode.split('.').toVec
      modifiers   = opcodeParts.drop(1).map(SASSInstruction.Modifier(_)) ++ decodedModifiers(format)
      operands    = splitOperands(operandPieces(format)).zipWithIndex.flatMap { case (chunk, index) =>
        operandValues(chunk, allowCC = index > 0)
      }
      normalizedOperands =
        if (
          opcodeParts.headOption.contains("P2R") && operands.size == 4 &&
          Set[Operand](Operand.SpecialRegister(SpecialRegister.PR), Operand.Symbol("PR"))(operands(1))
        )
          operands.patch(1, Vec.empty[Operand], 1)
        else operands
      operation <- Instruction.decode(
        opcodeParts.headOption.getOrElse(candidate.opcode),
        modifiers,
        normalizedOperands,
        guard
      ).left.map(message => NVDisasm.CandidateFailure(NVDisasm.FailureCategory.Unsupported, message))
    } yield DecodedInstruction(
      address,
      operation,
      Some(BigInt(1, bytes.reverse)),
      Some(DecodedInstruction.Machine(
        database.target,
        DecodedInstruction.Form(candidate.name, candidate.formId),
        candidate.opcode,
        candidate.bindings,
        candidate.encodingValues
      ))
    )
  }

  private def predicate(name: String): Option[Operand.Predicate] = {
    val negated  = name.startsWith("!")
    val spelling = name.stripPrefix("!")
    val parts    = spelling match {
      case "PT"        => Some(false -> "T")
      case "UPT"       => Some(true -> "T")
      case s"UP$index" => Some(true -> index)
      case s"P$index"  => Some(false -> index)
      case _           => None
    }
    parts.flatMap { (uniform, index) =>
      if (index == "T") Some(Operand.Predicate(None, uniform, negated))
      else if (index.length == 1 && index.head >= '0' && index.head <= '6')
        index.toIntOption.flatMap(_.refine[Int in 0 ~ 6].toOption)
          .map(value => Operand.Predicate(Some(value), uniform, negated))
      else None
    }
  }

}
