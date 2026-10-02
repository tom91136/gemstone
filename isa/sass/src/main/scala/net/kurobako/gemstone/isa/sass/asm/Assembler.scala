package net.kurobako.gemstone.isa.sass.asm

import cats.syntax.all.*
import net.kurobako.gemstone.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.sass.*
import net.kurobako.gemstone.isa.sass.SASSCodeWords.BundleControl as Control
import net.kurobako.gemstone.isa.sass.asm.Program.Statement.*

/** Assembles generated instruction cases with the target's recovered encoding tables. */
object Assembler {
  private type EncodedInstruction = (bytes: Vec[Byte], operation: Instruction)

  def assemble(source: String, target: String, address: Int = 0): Either[String, Array[Byte]] = source match {
    case s"$assembly// encoding: $annotationText" => for {
        annotation <- MachineSyntax.parse(annotationText)
        _          <- Either.cond(
          annotation.target == target && annotation.address == address,
          (),
          "encoding annotation target/address differs from assembly context"
        )
        result <- MachineSyntax.decode(assembly, annotation)
      } yield result.bytes
    case _ => for {
        instruction <- Parser.parse(source)
        bytes       <- encode(instruction, target, address)
      } yield bytes
  }

  def encode(instruction: Instruction, target: String, address: Int = 0): Either[String, Array[Byte]] =
    encodeCanonical(instruction, target, address).map(_.bytes.toArray)

  private def encodeCanonical(
      instruction: Instruction,
      target: String,
      address: Int
  ): Either[String, EncodedInstruction] = for {
    _        <- address.refine[Int >= 0].left.map(_.raw)
    database <- NVDisasm.forTarget(target).toRight(s"unknown SASS target $target")
    text     <- Printer.print(instruction)
    forms = database.forms.filter(form =>
      !form.alternate && instruction.definition.forms.contains(form.name) &&
        form.opcodes.exists(_.name.takeWhile(_ != '.') == instruction.mnemonic.raw)
    )
    attempts = forms.map { form =>
      for {
        bindings <- SASSFormatParser.parse(database, form, address, text)
        opcode   <- form.opcodes.find(value => bindings.get("Opcode").contains(value.value))
          .toRight(s"missing opcode binding for ${form.name}")
        bytes   <- Encoder.encode(database, form, opcode.name, bindings)
        decoded <- InstructionDecoder.decodeTyped(address, bytes, target)
        // Canonical FORMAT spellings can normalize signed immediates and omit defaults.
        _ <- Either.cond(
          equivalent(instruction, decoded.operation),
          (),
          s"encoded ${form.name} changes the requested operands"
        )
      } yield (bytes = bytes.toVec, operation = decoded.operation)
    }
    choices = attempts.flatMap(_.toOption).distinctBy(_.bytes)
    encoded <- choices match {
      case Vec(value)               => Right(value)
      case values if values.isEmpty =>
        Left(s"cannot assemble $target $text: ${attempts.flatMap(_.left.toOption).distinct.take(6).mkString("; ")}")
      case _ => Left(s"ambiguous $target encoding for $text; select a form with an encoding annotation")
    }
  } yield encoded

  def encode(instruction: DecodedInstruction): Either[String, Array[Byte]] =
    encodeCanonical(instruction).map(_.bytes.toArray)

  private def encodeCanonical(instruction: DecodedInstruction): Either[String, EncodedInstruction] =
    instruction.machine.toRight("instruction has no target encoding; supply a target explicitly").flatMap { machine =>
      for {
        annotation <- MachineSyntax.describe(instruction, machine)
        bytes      <- MachineSyntax.encode(annotation)
        actual     <- Disassembler.decode(bytes, machine.target, instruction.address.raw)
        _          <- Either.cond(
          actual.operation == instruction.operation,
          (),
          "machine fields do not describe the requested instruction; encode the new operation with an explicit target"
        )
      } yield (bytes = bytes.toVec, operation = actual.operation)
    }

  private def equivalent(left: Instruction, right: Instruction): Boolean =
    left.mnemonic == right.mnemonic && left.modifiers == right.modifiers && equivalentGuard(left.guard, right.guard) &&
      left.operands.size == right.operands.size && left.operands.zip(right.operands).forall { (a, b) =>
        equivalentOperand(a, b)
      }

  private def equivalentGuard(left: Option[Instruction.Guard], right: Option[Instruction.Guard]): Boolean =
    left == right ||
      (left.exists(_.predicate == Operand.Predicate(None, uniform = false, negated = false)) && right.isEmpty) ||
      (right.exists(_.predicate == Operand.Predicate(None, uniform = false, negated = false)) && left.isEmpty)

  private def equivalentOperand(left: Operand, right: Operand): Boolean = if (left == right) true
  else (integerValue(left), integerValue(right)) match {
    case (Some(a), Some(b)) => a == b
    case _                  =>
      (floatingBits(left), floatingBits(right)) match {
        case (Some((format, width, bits)), Some((otherFormat, otherWidth, otherBits))) =>
          format == otherFormat && width == otherWidth && bits == otherBits
        case (Some((format, width, bits)), None) => matchesFloating(right, format, width, bits)
        case (None, Some((format, width, bits))) => matchesFloating(left, format, width, bits)
        case _                                   => false
      }
  }

  private def integerValue(operand: Operand): Option[BigInt] = operand match {
    case Operand.Integer(value)                                    => Some(value)
    case Operand.Bits(value, _)                                    => Some(value)
    case Operand.Unary(Operand.Integer(value), true, false, false) => Some(-value)
    case Operand.Unary(Operand.Bits(value, _), true, false, false) => Some(-value)
    case _                                                         => None
  }

  private def floatingBits(operand: Operand): Option[(FloatEncoding, Int, BigInt)] = operand match {
    case Operand.FloatingBits(format, width, bits)                                    => Some((format, width.raw, bits))
    case Operand.Unary(Operand.FloatingBits(format, width, bits), true, false, false) =>
      Some((format, width.raw, bits ^ (BigInt(1) << (width.raw - 1))))
    case _ => None
  }

  private def matchesFloating(operand: Operand, format: FloatEncoding, width: Int, bits: BigInt): Boolean = {
    val signedValue = operand match {
      case Operand.Floating(value, _, negativeZero) => Some(if (negativeZero) -0.0 else value.toDouble)
      case Operand.Integer(value)                   => Some(value.toDouble)
      case Operand.Bits(value, _)                   => Some(value.toDouble)
      case Operand.Unary(Operand.Floating(value, _, negativeZero), true, false, false) =>
        Some(if (negativeZero) 0.0 else -value.toDouble)
      case Operand.Unary(Operand.Integer(value), true, false, false) => Some(-value.toDouble)
      case Operand.Unary(Operand.Bits(value, _), true, false, false) => Some(-value.toDouble)
      case _                                                         => None
    }
    signedValue.exists { value =>
      val expected = format match {
        case FloatEncoding.F16Imm if width == 16 => Some(BigInt(IEEE754.floatToBinary16(value.toFloat).raw & 0xffff))
        case FloatEncoding.F32Imm if width == 32 =>
          Some(BigInt(java.lang.Integer.toUnsignedLong(java.lang.Float.floatToRawIntBits(value.toFloat))))
        case FloatEncoding.F64Imm if width == 64 =>
          Some(BigInt(java.lang.Long.toUnsignedString(java.lang.Double.doubleToRawLongBits(value))))
        case _ => None
      }
      expected.contains(bits)
    }
  }
}

class Assembler(target: Target) extends isa.Encoder[Program, Vec[Byte], Error] {
  private def assembly(value: String): Error = Error.Assembly(Error.detail(value))

  override def encode(program: Program): Either[Error, Vec[Byte]] = for {
    _ <- program.target match {
      case Some(actual) if actual.sm != target.sm => Left(Error.TargetMismatch(target, actual))
      case _                                      => Right(())
    }
    _      <- Target.parse(target.name).left.map(assembly(_))
    layout <- locate(program)
    (operations, labels, controls) = layout
    slots                          = if (target.sm.raw < 50) 7 else if (target.sm.raw < 70) 3 else 1
    _ <- Either.cond(
      controls.isEmpty || operations.size % slots == 0,
      (),
      assembly("explicit bundles must contain every instruction slot")
    )
    padding <- if (operations.size % slots == 0) Right(Vec.empty[Operation])
    else Parser.parse("NOP;").left.map(assembly(_)).map(value =>
      Vec.fill[Operation](slots - operations.size % slots)(Operation(value))
    )
    padded = operations ++ padding
    encoded <- padded.zipWithIndex.traverse { (operation, index) =>
      for {
        address     <- addressOf(index)
        instruction <- resolve(operation, labels)
        encoded     <- operation.preserved match {
          case Some(decoded) => for {
              _ <- Either.cond(
                operation.references.isEmpty && decoded.operation == instruction,
                (),
                assembly("preserved encoding does not match the instruction")
              )
              _ <- Either.cond(
                decoded.address.raw == address,
                (),
                assembly("encoding annotation address differs from program layout")
              )
              machine <- decoded.machine.toRight(assembly("preserved instruction has no machine annotation"))
              actual  <- Target.parse(machine.target).left.map(assembly(_))
              _       <- Either.cond(actual.sm == target.sm, (), Error.TargetMismatch(target, actual))
              encoded <- Assembler.encodeCanonical(decoded).left.map(assembly(_))
            } yield encoded
          case None => Assembler.encodeCanonical(instruction, target.name, address).left.map(assembly(_))
        }
        expected = if (target.sm.raw < 50) 8 else if (target.sm.raw < 70) 11 else 16
        _ <- Either.cond(
          encoded.bytes.size == expected,
          (),
          assembly(s"instruction at $address requires $expected bytes")
        )
        _ <- Either.cond(
          expected != 11 || (encoded.bytes(10) & 0xe0) == 0,
          (),
          assembly("Maxwell instruction control exceeds 21 bits")
        )
      } yield encoded
    }
    bundles <- encoded.grouped(slots).toVec.zipWithIndex.traverse { (words, index) =>
      val first            = index * slots
      val control: Control = controls.getOrElse(
        first,
        if (target.sm.raw < 50) Control.Kepler(Vec.fill(8)(0.toByte))
        else if (target.sm.raw < 70) Control.Maxwell(words.map(word => controlOf(word.bytes)), 0)
        else Control.Modern
      )
      for {
        _ <- control match {
          case Control.Maxwell(values, _) =>
            Either.cond(
              values.size == slots && words.zipWithIndex.forall { (word, slot) =>
                padded(first + slot).preserved.isEmpty || values(slot) == controlOf(word.bytes)
              },
              (),
              assembly("bundle controls disagree with preserved instruction controls")
            )
          case _ => Right(())
        }
        located <- words.zipWithIndex.traverse { (word, slot) =>
          for {
            address <- addressOf(first + slot)
            _       <- control match {
              case Control.Maxwell(values, _) =>
                validateControl(word.bytes, values(slot), address, word.operation)
              case _ => Right(())
            }
          } yield SASSCodeWords.CodeWord(
            address.unsafe[SASSCodeWords.Address],
            word.bytes.take(if (slots == 1) 16 else 8)
          )
        }
        base = index * (if (slots == 7) 64 else if (slots == 3) 32 else 16)
      } yield SASSCodeWords.CodeBundle(base.unsafe[SASSCodeWords.Address], control, located)
    }
    bytes <- SASSCodeWords.join(SASSCodeWords.CodeSection(target.sm, bundles)).left.map(assembly(_))
  } yield bytes.toVec

  private def validateControl(
      word: Vec[Byte],
      control: SASSCodeWords.ControlWord,
      address: Int,
      canonical: Instruction
  ): Either[Error, Unit] =
    if (control == controlOf(word)) Right(())
    else {
      val suffix = Vec.tabulate[Byte](3)(index => (control.value.raw >>> (index * 8)).toByte)
      for {
        patched <- Disassembler.decode((word.take(8) ++ suffix).toArray, target.name, address).left.map(assembly(_))
        _       <- Either.cond(
          canonical == patched.operation,
          (),
          assembly("bundle control changes instruction semantics")
        )
      } yield ()
    }

  private def controlOf(bytes: Vec[Byte]): SASSCodeWords.ControlWord =
    SASSCodeWords.ControlWord(
      ((bytes(8) & 255) | ((bytes(9) & 255) << 8) | ((bytes(10) & 31) << 16)).unsafe[SASSCodeWords.ControlBits]
    )

  private def addressOf(index: Int): Either[Error, Int] = {
    val address = if (target.sm.raw < 50) index.toLong / 7 * 64 + 8 + index % 7 * 8
    else if (target.sm.raw < 70) index.toLong / 3 * 32 + 8 + index % 3 * 8
    else index.toLong * 16
    Either.cond(address <= Int.MaxValue, address.toInt, assembly("program address exceeds the supported range"))
  }

  private def locate(
      program: Program
  ): Either[Error, (Vec[Operation], Map[String, Int], Map[Int, Control])] = {
    val initial = (Vec.empty[Operation], Map.empty[String, Int], Map.empty[Int, Control])
    val slots   = if (target.sm.raw < 50) 7 else if (target.sm.raw < 70) 3 else 1
    program.statements.foldLeft[Either[Error, (Vec[Operation], Map[String, Int], Map[Int, Control])]](Right(initial)) {
      case (result, statement) => result.flatMap { (operations, labels, controls) =>
          statement match {
            case operation: Operation => Right((operations :+ operation, labels, controls))
            case Label(name)          =>
              if (!Parser.validName(name.raw)) Left(assembly(s"invalid label ${name.raw}"))
              else if (labels.contains(name.raw)) Left(Error.DuplicateLabel(name))
              else addressOf(operations.size).map(address =>
                (operations, labels.updated(name.raw, address), controls)
              )
            case Bundle(control) =>
              if (operations.size % slots != 0 || controls.contains(operations.size))
                Left(assembly("bundle directive must precede a complete bundle"))
              else {
                val valid = (target.sm.raw, control) match {
                  case (sm, Control.Kepler(bytes))      => sm < 50 && bytes.size == 8
                  case (sm, Control.Maxwell(values, _)) => sm >= 50 && sm < 70 && values.size == 3
                  case (sm, Control.Modern)             => sm >= 70
                }
                Either.cond(
                  valid,
                  (operations, labels, controls.updated(operations.size, control)),
                  assembly("bundle control does not match the target")
                )
              }
          }
        }
    }.flatMap { (operations, labels, controls) =>
      Either.cond(
        !controls.contains(operations.size),
        (operations, labels, controls),
        assembly("bundle directive has no instructions")
      )
    }
  }

  private def resolve(operation: Operation, labels: Map[String, Int]): Either[Error, Instruction] =
    if (operation.references.isEmpty) Right(operation.instruction)
    else for {
      reference <- Parser.symbolicReference(operation).left.map(assembly(_))
      address   <- labels.get(reference.name.raw).toRight(Error.UndefinedLabel(reference.name))
      instruction = operation.instruction
      operands    = instruction.operands.updated(reference.operand.raw, Operand.Integer(BigInt(address)))
      resolved <- Instruction.decode(
        instruction.mnemonic.raw,
        instruction.modifiers,
        operands,
        instruction.guard
      ).left.map(assembly(_))
    } yield resolved
}
