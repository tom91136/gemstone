package net.kurobako.gemstone.isa.sass

import cats.syntax.all.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.catalogue.*
import net.kurobako.gemstone.isa.sass.*
import net.kurobako.gemstone.isa.sass.Instruction as SASSInstruction
import net.kurobako.gemstone.string.*
import scala.collection.mutable

/** Decodes instruction words through their recovered FORMAT trees directly to structural operands. */
object InstructionDecoder {
  private lazy val RegisterBanks = SassDomains.banks
  private val ModifierSeparators = Array(';', ',', '*', '.', '/', ' ', '\t', '\r', '\n', '\f', '\u000b')

  private enum FormatPiece {
    case Text(value: String)
    case Field(field: NVDisasm.Syntax.Field, value: Option[Operand])
    case Group(open: String, close: String, children: Vec[FormatPiece])
  }

  def decode(address: Int >= 0, bytes: Array[Byte], target: Target): Either[String, DecodedInstruction] = {
    val database = target.database
    for {
      candidates <- FieldDecoder.candidates(database, bytes).left.map(error => s"${target.name} $error")
      rendered = candidates.filterNot(_.alternate).map(Candidate(database, target, address, _, bytes).decode)
      errors   = rendered.flatMap(_.left.toOption).filterNot(_.category == NVDisasm.FailureCategory.IllegalCandidate)
      _ <- Either.cond(errors.isEmpty, (), errors.map(_.message).distinct.mkString("; "))
      decoded = rendered.flatMap(_.toOption).distinctBy(value =>
        (value.mnemonic, value.modifiers, value.operands, value.guard)
      )
      result <- decoded match {
        case Vec(single) => Right(single)
        case Vec()       => Left(rendered.flatMap(_.left.toOption).map(_.message).distinct.mkString("; "))
        case _           => Left(s"ambiguous ${target.name} instruction: ${decoded.map { value =>
              val origin = value.machine.fold("")(machine => s" ${machine.form}")
              s"${value.mnemonic.raw}${value.modifiers.map(_.name).mkString(".")} ${value.operands.mkString(", ")}$origin"
            }.mkString("; ")}")
      }
    } yield result
  }

  private def unsupported(message: String): NVDisasm.CandidateFailure =
    NVDisasm.CandidateFailure(NVDisasm.FailureCategory.Unsupported, message)
  private def illegal(message: String): NVDisasm.CandidateFailure =
    NVDisasm.CandidateFailure(NVDisasm.FailureCategory.IllegalCandidate, message)

  private def signed(value: BigInt, bits: Int): BigInt =
    if (bits > 0 && value.testBit(bits - 1)) value - (BigInt(1) << bits) else value

  private def hexadecimal(value: BigInt): Operand =
    if (value < 0) Operand.Unary(Operand.Bits(-value, 0), negative = true, not = false, absolute = false)
    else Operand.Bits(value, 0)

  private def fieldWidth(field: NVDisasm.Syntax.Field): Option[Int] =
    field.arguments.flatMap(_.takeWhile(_ != '/').toIntOption)

  /** Vocabulary register spellings: `R7`, `RZ`, `UR3`, `URZ`, `B1`, `SB2`. */
  private def register(name: String): Option[Operand.Register] = {
    val bank = RegisterBanks.find(name.startsWith(_))
    bank.flatMap { prefix =>
      val suffix = name.drop(prefix.length)
      val index  =
        if (suffix == "Z") Some(255)
        else Option.when(suffix.nonEmpty && suffix.forall(c => c >= '0' && c <= '9'))(suffix).flatMap(_.toIntOption)
      index.flatMap(_.refine[Int in 0 ~ 255].toOption).map(Operand.Register(RegisterBank.fromSpelling(prefix), _))
    }
  }

  private def addressOperand(
      base: Operand.Register,
      index: Option[Operand.Register],
      displacement: BigInt,
      scale: Int in (1, 2, 4, 8, 16),
      mode: Option[AddressMode]
  ): Operand.Address = Operand.Address(
    Option.when(base.index != 255)(base).orElse(index.filter(_.index != 255)),
    index.filter(value => base.index != 255 && value.index != 255),
    displacement,
    scale,
    mode,
    None
  )

  private def afterSlash(pieces: Vec[FormatPiece], index: Int): Boolean = pieces.lift(index - 1).exists {
    case FormatPiece.Text(text) => text.trim.endsWith("/")
    case _                      => false
  }

  private class Candidate(
      database: NVDisasm.Database,
      target: Target,
      address: Int >= 0,
      original: FieldDecoder.Candidate,
      bytes: Array[Byte]
  ) {
    import NVDisasm.Syntax.*
    private val form      = original.form
    private val candidate = FieldDecoder.refine(database, original)
    private val encoding  = NVDisasm.Encoding(database, form)

    private def attribute(field: Field, name: String): BigInt = candidate.bindings.get(s"${field.binding}@$name")
      .orElse(candidate.bindings.get(s"${form.encodedBinding(field)}@$name")).getOrElse(BigInt(0))

    private def raw(field: Field): Option[BigInt] = candidate.bindings.get(field.binding)
      .orElse(candidate.bindings.get(form.encodedBinding(field)))
      .orElse(database.vocabulary(field).map(_.value).distinct match {
        case Vec(value) => Some(value)
        case _          => None
      })
      .orElse(Option.when(
        !form.encodedNames(form.encodedBinding(field))
      )(database.defaultValues(field).headOption).flatten)

    /** The bits a form stores for `field`, counting those its scale drops. */
    private def storedWidth(field: Field): Option[Int] = {
      val binding = form.encodedBinding(field)
      val widths  = form.fields.flatMap { assignment =>
        assignment.expression match {
          case Expression.Reference(`binding`, None, _) => Some(assignment.positions.size)
          case Expression.Scale(Expression.Reference(`binding`, None, _), factor) if factor.bitCount == 1 =>
            Some(assignment.positions.size + factor.bitLength - 1)
          case _ => None
        }
      }
      Option.when(widths.nonEmpty)(widths.sum)
    }

    /** A constant bank offset is declared one bit wider than the form stores, as an absolute offset is unsigned; an
      * offset added to a register is signed over the stored bits.
      */
    private def registerDisplacement(children: Vec[FormatPiece], base: Operand.Register, value: BigInt): BigInt =
      children.collectFirst {
        case FormatPiece.Field(field, Some(_)) if field.category == NVDisasm.FieldKind.SignedImmediate => field
      }.flatMap(field => fieldWidth(field).zip(storedWidth(field)).headOption).fold(value) { (declared, stored) =>
        if (!OperandQueries.isZero(base) && value >= 0 && stored < declared) signed(value, stored) else value
      }

    private def isDefault(field: Field, value: BigInt): Boolean = database.defaultValues(field).contains(value) &&
      FieldDecoder.Attribute.names.forall(name => attribute(field, name) == 0)

    private def changed(field: Field): Option[BigInt] = raw(field).filterNot(database.defaultValues(field).contains)

    private def laterPredicate(field: Field): Boolean = {
      val index = form.declared.indexWhere(value => value.binding == field.binding && value.kind == field.kind)
      form.declared.drop(index + 1).takeWhile(value => value.kind == field.kind && value.default.nonEmpty)
        .exists(changed(_).nonEmpty)
    }

    private def symbol(field: Field, value: BigInt): Option[String] = {
      val valid     = database.vocabulary(field).filter(_.value == value).map(_.name).filterNot(_.startsWith("INVALID"))
      val preferred = field.category match {
        case NVDisasm.FieldKind.SpecialRegister =>
          // Selector 255 is also spelled SRZ; keep that alias ahead of its generated SR255 name.
          valid.find(_ == "SRZ").orElse(valid.find(_.startsWith("SR_")))
            .orElse(valid.find(_.startsWith("SR"))).orElse(valid.lastOption)
        case category if category.isPredicate => valid.find(_.endsWith("T")).orElse(valid.headOption)
        case category if category.isRegister  => valid.minByOption(_.length)
        // legacy condition tests list flag aliases (ZFT, LEG, U) before the comparison names nvdisasm prints
        case NVDisasm.FieldKind.Vocabulary("Test") => valid.lastOption
        case _                                     =>
          val names = valid.toSet
          valid.find(name => name.startsWith("U") && names(name.drop(1)))
            .orElse(valid.filterNot(_.startsWith("_")).minByOption(_.length)).orElse(valid.headOption)
      }
      preferred.map(name =>
        field.category match {
          case NVDisasm.FieldKind.ConstantBank | NVDisasm.FieldKind.DescriptorRegister => name.toLowerCaseRoot
          case _                                                                       => name
        }
      )
    }

    private def isOperandValue(field: Field, allowCC: Boolean): Boolean =
      (field.category.isOperand || (allowCC && field.category == NVDisasm.FieldKind.ConditionCode)) &&
        !field.isGuard

    private def converted(field: Field, value: BigInt): Either[NVDisasm.CandidateFailure, Operand] =
      field.category match {
        case category if category.isPredicate =>
          val name =
            symbol(field, value).getOrElse(if (category == NVDisasm.FieldKind.UniformPredicate) "UPT" else "PT")
          Operand.predicate(name).toRight(unsupported(s"unknown predicate $name (${field.binding})"))
        case NVDisasm.FieldKind.SpecialRegister | NVDisasm.FieldKind.DescriptorRegister =>
          symbol(field, value).map(name => Operand.SpecialRegister(SpecialRegister.fromSpelling(name)))
            .toRight(unsupported(s"unknown special register $value (${field.binding})"))
        case NVDisasm.FieldKind.FloatingImmediate(baseFormat) =>
          val format = candidate.floatFormats.getOrElse(field.binding, baseFormat)
          format.width.toRight(unsupported(s"unsupported floating format ${format.spelling}")).map(width =>
            Operand.FloatingBits(format, width, value << FormatBindings.floatingShift(database, form, field, format))
          )
        case NVDisasm.FieldKind.Immediate         => Right(hexadecimal(value))
        case NVDisasm.FieldKind.SignedImmediate   => Right(hexadecimal(fieldWidth(field).fold(value)(signed(value, _))))
        case NVDisasm.FieldKind.RelativeImmediate =>
          val next = BigInt(address) + (if (database.width == 128) 16 else 8)
          Right(hexadecimal(next + signed(value, fieldWidth(field).getOrElse(0))))
        case NVDisasm.FieldKind.ConstantBank => symbol(field, value).map(_ => Operand.Symbol("c"))
            .toRight(illegal(s"illegal constant selector $value (${field.binding})"))
        case NVDisasm.FieldKind.ConditionCode | NVDisasm.FieldKind.ConditionRegisterBank =>
          symbol(field, value).map(Operand.Symbol.apply)
            .toRight(illegal(s"illegal condition selector $value (${field.binding})"))
        case NVDisasm.FieldKind.BarrierRegister | NVDisasm.FieldKind.Scoreboard =>
          SassDomains.field(database, form, field.binding).flatMap(_.register(value)).flatMap((bank, index) =>
            index.refine[Int in 0 ~ 255].toOption.map(Operand.Register(RegisterBank.fromSpelling(bank), _))
          ).toRight(unsupported(s"unknown register $value (${field.binding})"))
        case NVDisasm.FieldKind.CbuState =>
          val name = symbol(field, value).getOrElse(value.toString)
          Right(register(name).getOrElse(Operand.Symbol(name)))
        case category =>
          val name = symbol(field, value).getOrElse(value.toString)
          register(name).orElse(Option.when(name.startsWith("SR_") || name == "PR")(
            Operand.SpecialRegister(SpecialRegister.fromSpelling(name))
          )).orElse(if (category.isPredicateOperand) Operand.predicate(name) else None)
            .toRight(unsupported(s"cannot decode operand $name (${field.binding}:${field.kind})"))
      }

    private def fieldOperand(
        field: Field,
        depth: Int,
        qualifier: Boolean
    ): Either[NVDisasm.CandidateFailure, Option[Operand]] =
      if (field.category == NVDisasm.FieldKind.PredicateRegister) Right(Some(Operand.Symbol("PR")))
      else raw(field) match {
        case None => Left(unsupported(s"${candidate.name}: missing ${field.binding} (${field.kind})"))
        case Some(value) if database.vocabulary(field).nonEmpty && symbol(field, value).isEmpty =>
          Left(illegal(s"illegal ${field.kind}=$value (${field.binding})"))
        case Some(_) if !field.category.isOperand => Right(None)
        case Some(value)
            if (depth == 0 || qualifier) && !field.category.isRegister && isDefault(field, value) &&
              !laterPredicate(field) => Right(None)
        case Some(value) =>
          val not      = attribute(field, "not") == 1
          val negative = attribute(field, "negate") == 1
          val absolute = attribute(field, "absolute") == 1
          val inverted = attribute(field, "invert") == 1
          converted(field, value).flatMap {
            case Operand.Predicate(index, uniform, negated) if not || negative || inverted =>
              Right(Some(Operand.Predicate(index, uniform, negated ^ not ^ negative ^ inverted)))
            case _ if not && inverted => Left(unsupported(s"${field.binding} combines logical and bitwise inversion"))
            case operand if not || negative || absolute || inverted =>
              Right(Some(Operand.Unary(operand, negative, not || inverted, absolute)))
            case operand => Right(Some(operand))
          }
      }

    private def pieces(
        nodes: Vec[NVDisasm.Syntax],
        depth: Int = 0
    ): Either[NVDisasm.CandidateFailure, Vec[FormatPiece]] =
      nodes.zipWithIndex.traverse {
        case (node, index) =>
          val qualifier = nodes.lift(index - 1).exists {
            case Text(raw) => raw.trim.endsWith("/")
            case _         => false
          }
          val next: Either[NVDisasm.CandidateFailure, Vec[FormatPiece]] = node match {
            case Text(raw)    => Right(Vec(FormatPiece.Text(raw)))
            case Literal(raw) => Right(Vec(FormatPiece.Text(raw)))
            case field: Field =>
              fieldOperand(field, depth, qualifier).map(value => Vec(FormatPiece.Field(field, value)))
            case Optional(None, children)     => pieces(children, depth)
            case Optional(Some("&" | "?"), _) => Right(Vec.empty)
            case Optional(_, _)               => Left(unsupported(s"unsupported optional syntax in ${candidate.name}"))
            case Group("{", "}", children)
                if NVDisasm.Syntax.fields(children).forall(_.binding.startsWith("reuse_")) =>
              Right(Vec.empty)
            case group: Group if NVDisasm.Syntax.operatorGroup(group) => Right(Vec.empty)
            case Group(open, close, children)                         =>
              pieces(children, depth + 1).map(value => Vec(FormatPiece.Group(open, close, value)))
          }
          next
      }.map(_.flatten)

    private def guard: Option[SASSInstruction.Guard] = form.declared.find(_.isGuard)
      .flatMap(field =>
        raw(field).flatMap(symbol(field, _)).flatMap(Operand.predicate).map { decoded =>
          decoded.copy(negated = decoded.negated ^ (attribute(field, "not") == 1))
        }
      )
      .filterNot(value => value.index.isEmpty && !value.negated)
      .map(SASSInstruction.Guard.apply)

    /** Pieces after the opcode marker, which shares its text node with the guard spelling. */
    private def operandPieces(all: Vec[FormatPiece]): Vec[FormatPiece] = all.zipWithIndex.collectFirst {
      case (FormatPiece.Text(raw), index) if raw.contains("Opcode") =>
        FormatPiece.Text(raw.drop(raw.indexOf("Opcode") + "Opcode".length)) +: all.drop(index + 1)
    }.getOrElse(all)

    private def decodedModifiers(all: Vec[FormatPiece]): Vec[SASSInstruction.Modifier] = {
      val prefix = operandPieces(all).takeWhile {
        case FormatPiece.Field(field, _) => field.isGuard || !field.category.isOperand
        case _                           => true
      }
      def modifiers(pieces: Vec[FormatPiece], preserveDefaults: Boolean): Vec[SASSInstruction.Modifier] =
        pieces.flatMap {
          case FormatPiece.Text(raw) =>
            raw.split(ModifierSeparators).view.filter(_.nonEmpty)
              .map(SASSInstruction.Modifier(_))
          case FormatPiece.Group("{", "}", children) if !children.exists {
                case FormatPiece.Field(field, _) => field.category.isOperand
                case _                           => false
              } =>
            val grouped = children.collect { case FormatPiece.Field(field, _) => field }
            val altered = grouped.exists(field => raw(field).exists(value => !isDefault(field, value)))
            modifiers(children, preserveDefaults || (grouped.size > 1 && altered))
          case FormatPiece.Field(field, _) if !field.isGuard && field.kind != "EXP_DESC" =>
            raw(field).toVec.flatMap { value =>
              Option.when(!isDefault(field, value) || field.category == NVDisasm.FieldKind.StepField ||
                preserveDefaults)(
                symbol(field, value).getOrElse(value.toString).split('.').view.filter(_.nonEmpty)
                  .map(SASSInstruction.Modifier(_))
              ).toVec.flatten
            }
          case _ => Vec.empty
        }
      modifiers(prefix, preserveDefaults = false)
    }

    /** A selector field after `/` qualifies the preceding operand once an operand has been produced. */
    private def slashSelector(pieces: Vec[FormatPiece], index: Int, allowCC: Boolean): Boolean =
      afterSlash(pieces, index) && pieces.take(index).exists(qualifiesSelector(_, allowCC))

    private def qualifiesSelector(piece: FormatPiece, allowCC: Boolean): Boolean = piece match {
      case FormatPiece.Field(previous, Some(_)) => isOperandValue(previous, allowCC)
      case group: FormatPiece.Group             => operandValue(group).nonEmpty
      case _                                    => false
    }

    private def operandValue(piece: FormatPiece): Vec[Operand] = piece match {
      case FormatPiece.Field(_, value)           => value.toVec
      case FormatPiece.Group("{", "}", children) =>
        val selected = children.zipWithIndex.collect {
          case (FormatPiece.Field(field, _), index) if slashSelector(children, index, allowCC = false) =>
            changed(field).flatMap(symbol(field, _))
        }.flatten
        qualified(children.flatMap(operandValue(_)), selected) match {
          case Vec(single)               => Vec(single)
          case values if values.nonEmpty => Vec(Operand.Group(values))
          case _                         => Vec.empty
        }
      case FormatPiece.Group("[", "]", children) =>
        val mode = children.zipWithIndex.collect {
          case (FormatPiece.Field(field, _), index)
              if field.category == NVDisasm.FieldKind.AddressQualifier || afterSlash(children, index) =>
            changed(field).flatMap(symbol(field, _))
              .filter(PartialFunction.cond(_) { case "X1" | "X2" | "X4" | "X8" | "X16" | "U32" | "U64" | "32" | "64" =>
                true
              })
        }.flatten.headOption.map(AddressMode.fromSpelling)
        val scale: Int in (1, 2, 4, 8, 16) = mode.flatMap(AddressMode.scale).getOrElse(1)
        children.flatMap(operandValue(_)) match {
          case Vec(Operand.Symbol("c"), bank, offset) if OperandQueries.integer(bank).nonEmpty =>
            Vec(Operand.ConstantBank(OperandQueries.integer(bank).get, offset))
          case Vec(base: Operand.Register, displacement) if OperandQueries.integer(displacement).nonEmpty =>
            Vec(addressOperand(
              base,
              None,
              registerDisplacement(children, base, OperandQueries.integer(displacement).get),
              scale,
              mode
            ))
          case Vec(base: Operand.Register, index: Operand.Register, offset)
              if OperandQueries.integer(offset).nonEmpty =>
            Vec(addressOperand(base, Some(index), OperandQueries.integer(offset).get, scale, mode))
          case Vec(base: Operand.Register) => Vec(addressOperand(base, None, 0, scale, mode))
          case Vec(Operand.SpecialRegister(descriptor), value: Operand.Address) =>
            Vec(value.copy(descriptor = Some(Operand.SpecialRegister(descriptor))))
          case values => values
        }
      case FormatPiece.Group(_, _, children) => children.flatMap(operandValue(_))
      case FormatPiece.Text(_)               => Vec.empty
    }

    private def qualify(value: Operand, selector: String): Operand = value match {
      case Operand.Unary(inner, negative, not, absolute) =>
        Operand.Unary(qualify(inner, selector), negative, not, absolute)
      case Operand.Qualified(inner, selectors) =>
        Operand.Qualified(inner, OperandSelector.fromSpellings(selectors.map(_.spelling) :+ selector))
      case other => Operand.Qualified(other, OperandSelector.fromSpellings(Vec(selector)))
    }

    private def qualified(operands: Vec[Operand], selectors: Vec[String]): Vec[Operand] =
      selectors.foldLeft(operands)((values, selector) =>
        values.lastOption.fold(values)(last => values.init :+ qualify(last, selector))
      )

    private def constantOffset(value: Operand): Operand = value match {
      case Operand.Address(None, None, displacement, scale, _, None) if scale == 1 => Operand.Bits(displacement, 0)
      case other                                                                   => other
    }

    private def operandValues(chunk: Vec[FormatPiece], allowCC: Boolean): Vec[Operand] = {
      val hasDescriptor = candidate.bindings.get("e_desc").orElse(candidate.bindings.get("EXP_DESC"))
        .map(_ != BigInt(0)).getOrElse(form.declared.exists(field =>
          field.category == NVDisasm.FieldKind.DescriptorRegister && raw(field).nonEmpty
        ))
      val values = chunk.flatMap {
        case FormatPiece.Field(field, Some(value)) if isOperandValue(field, allowCC) => Vec(value)
        case FormatPiece.Field(field, None) if allowCC && field.category == NVDisasm.FieldKind.ConditionCode =>
          raw(field).flatMap(symbol(field, _)).map(Operand.Symbol.apply).toVec
        case group: FormatPiece.Group => operandValue(group)
        case _                        => Vec.empty[Operand]
      }
      val indexed = chunk.exists {
        case FormatPiece.Field(field, _) => field.kind == "CX"
        case _                           => false
      }
      val coalesced = values match {
        case Vec(bank: Operand.Address, offset) if indexed =>
          Vec(bank.copy(descriptor = Some(OperandQueries.IndexedBank)), offset)
        case Vec(Operand.Symbol("c"), bank, offset) if OperandQueries.integer(bank).nonEmpty =>
          Vec(Operand.ConstantBank(OperandQueries.integer(bank).get, constantOffset(offset)))
        case Vec(Operand.Unary(Operand.Symbol("c"), negative, not, absolute), bank, offset)
            if OperandQueries.integer(bank).nonEmpty =>
          Vec(Operand.Unary(
            Operand.ConstantBank(OperandQueries.integer(bank).get, constantOffset(offset)),
            negative,
            not,
            absolute
          ))
        // URZ, which assembled text without a descriptor selects, decodes as an empty address.
        case Vec(
              Operand.SpecialRegister(SpecialRegister.desc),
              Operand.Address(base, None, displacement, _, None, None),
              address: Operand.Address
            ) if displacement == 0 && base.forall(_.bank == RegisterBank.UR) =>
          if (hasDescriptor) Vec(address.copy(descriptor = Some(base.getOrElse(OperandQueries.UniformZero))))
          else Vec(address)
        case _ => values.filterNot(_ == Operand.SpecialRegister(SpecialRegister.desc))
      }
      val precededBySelector = chunk.scanLeft(false)((seen, piece) => seen || qualifiesSelector(piece, allowCC))
      // nvdisasm conveys exp_desc through the printed `desc[URx]` rather than a selector.
      val selectors = chunk.zipWithIndex.collect {
        case (FormatPiece.Field(field, _), index) if field.kind != "EXP_DESC" =>
          raw(field).flatMap(symbol(field, _)).filter(value =>
            (afterSlash(chunk, index) && precededBySelector(index)) ||
              (field.category == NVDisasm.FieldKind.OptionalConditionCode && value == "CC") ||
              field.category == NVDisasm.FieldKind.PartitionSelector ||
              field.category == NVDisasm.FieldKind.InstructionSelector
          ).filter(_ => changed(field).nonEmpty)
      }.flatten
      qualified(coalesced, selectors)
    }

    private def splitOperands(all: Vec[FormatPiece]): Vec[Vec[FormatPiece]] = {
      val finished = mutable.ArrayBuffer.empty[Vec[FormatPiece]]
      val current  = mutable.ArrayBuffer.empty[FormatPiece]
      all.foreach {
        case FormatPiece.Text(raw) =>
          val parts = raw.split(",", -1)
          current.append(FormatPiece.Text(parts.head))
          parts.tail.foreach { part =>
            finished.append(current.toVec)
            current.clear()
            current.append(FormatPiece.Text(part))
          }
        case piece => current.append(piece)
      }
      finished.append(current.toVec)
      finished.toVec
    }

    def decode: Either[NVDisasm.CandidateFailure, DecodedInstruction] = for {
      _ <- Either.cond(
        form.syntax.nonEmpty,
        (),
        NVDisasm.CandidateFailure(NVDisasm.FailureCategory.MissingMetadata, s"missing FORMAT tree ${candidate.name}")
      )
      invalidAttribute = form.declared.find(field =>
        FieldDecoder.Attribute.names.exists { name =>
          val value = attribute(field, name)
          value != 0 && value != 1
        }
      )
      _ <- invalidAttribute.map(field => unsupported(s"invalid attributes on ${field.binding}")).toLeft(())
      rawBindings = form.declared.foldLeft(candidate.bindings) { (bindings, field) =>
        raw(field).fold(bindings)(value => bindings.updated(field.binding, value))
      }
      _ <- Legality.check(encoding, Legality.completeBindings(form, rawBindings))
      _ <- Either.cond(
        candidate.issues.forall(_.structural),
        (),
        unsupported(candidate.issues.map(_.message).mkString("; "))
      )
      format <- pieces(form.syntax)
      opcodeParts = candidate.opcode.split('.').toVec
      modifiers   = opcodeParts.drop(1).map(SASSInstruction.Modifier(_)) ++ decodedModifiers(format)
      operands    = splitOperands(operandPieces(format)).zipWithIndex.flatMap { case (chunk, index) =>
        operandValues(chunk, allowCC = index > 0)
      }
      normalised =
        if (
          opcodeParts.head == "P2R" && operands.size == 4 &&
          Set[Operand](Operand.SpecialRegister(SpecialRegister.PR), Operand.Symbol("PR"))(operands(1))
        ) operands.patch(1, Vec.empty[Operand], 1)
        else operands
      operation <-
        InstructionDefinitions.decode(opcodeParts.head, modifiers, normalised, guard).left.map(unsupported(_))
    } yield DecodedInstruction(
      address,
      operation,
      Some(BigInt(1, bytes.reverse)),
      Some(DecodedInstruction.Machine(
        target,
        candidate.name,
        candidate.opcode,
        candidate.bindings,
        candidate.encodingValues
      ))
    )
  }
}
