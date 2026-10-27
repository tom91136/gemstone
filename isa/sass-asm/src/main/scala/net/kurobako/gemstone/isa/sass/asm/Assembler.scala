package net.kurobako.gemstone.isa.sass.asm

import cats.syntax.all.*
import net.kurobako.gemstone.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.catalogue.*
import net.kurobako.gemstone.isa.sass.*
import net.kurobako.gemstone.isa.sass.CodeWords.BundleControl as Control
import net.kurobako.gemstone.isa.sass.asm.Program.Statement.*

/** Assembles generated instruction cases with the target's recovered encoding tables. */
object Assembler {
  private type EncodedInstruction = (bytes: Vec[Byte], operation: Instruction)

  def assemble(source: String, target: Target, address: Int >= 0 = 0): Either[String, Array[Byte]] = source match {
    case s"$assembly// encoding: $annotationText" => for {
        annotation <- MachineSyntax.parse(annotationText)
        _          <- Either.cond(
          annotation.target == target.name && annotation.address == address.raw,
          (),
          "encoding annotation target/address differs from assembly context"
        )
        result <- MachineSyntax.decode(assembly, annotation)
      } yield result.bytes
    case _ => Parser.parse(source).flatMap(encode(_, target, address))
  }

  def encode(instruction: Instruction, target: Target, address: Int >= 0 = 0): Either[String, Array[Byte]] =
    encodeCanonical(instruction, target, address).map(_.bytes.toArray)

  private def encodeCanonical(
      instruction: Instruction,
      target: Target,
      address: Int >= 0
  ): Either[String, EncodedInstruction] = {
    val database = target.database
    val names    = instruction.definition.forms.toSet
    val forms    = database.forms.filter(form =>
      !form.alternate && names(form.name) &&
        form.opcodes.exists(_.name.takeWhile(_ != '.') == instruction.mnemonic.raw)
    )
    for {
      text <- Printer.print(instruction)
      attempts = forms.map { form =>
        for {
          // P2R forms that spell the predicate bank expect it as the second operand.
          encodingText <-
            if (
              instruction.mnemonic == "P2R" && instruction.operands.size == 3 &&
              form.declared.exists(field =>
                field.category == NVDisasm.FieldKind.ConditionRegisterBank ||
                  field.category == NVDisasm.FieldKind.PredicateRegister
              )
            )
              InstructionDefinitions.decode(
                instruction.mnemonic.raw,
                instruction.modifiers,
                instruction.operands.patch(1, Vec(Operand.Symbol("PR")), 0),
                instruction.guard
              ).flatMap(Printer.print(_))
            else Right(text)
          bindings <- FormatParser.parse(database, form, address, encodingText)
          opcode   <- form.opcodes.find(value => bindings.get("Opcode").contains(value.value))
            .toRight(s"missing opcode binding for ${form.name}")
          bytes <- Encoder.encode(NVDisasm.Encoding(database, form), opcode.name, bindings)
          // Canonical FORMAT spellings can normalise signed immediates and omit defaults.
          encoded <- verified(bytes, address, target)(
            equivalent(instruction, _),
            s"encoded ${form.name} changes the requested operands"
          )
        } yield encoded
      }
      encoded <- attempts.flatMap(_.toOption).distinctBy(_.bytes) match {
        case Vec(value)               => Right(value)
        case values if values.isEmpty =>
          Left(
            s"cannot assemble ${target.name} $text: ${attempts.flatMap(_.left.toOption).distinct.take(6).mkString("; ")}"
          )
        case _ => Left(s"ambiguous ${target.name} encoding for $text; select a form with an encoding annotation")
      }
    } yield encoded
  }

  def encode(instruction: DecodedInstruction): Either[String, Array[Byte]] =
    encodeCanonical(instruction).map(_.bytes.toArray)

  private def encodeCanonical(instruction: DecodedInstruction): Either[String, EncodedInstruction] =
    instruction.machine.toRight("instruction has no target encoding; supply a target explicitly").flatMap { machine =>
      for {
        annotation <- MachineSyntax.describe(instruction, machine)
        bytes      <- MachineSyntax.encode(annotation)
        encoded    <- verified(bytes, instruction.address, machine.target)(
          _ == instruction.operation,
          "machine fields do not describe the requested instruction; encode the new operation with an explicit target"
        )
      } yield encoded
    }

  private def verified(bytes: Array[Byte], address: Int >= 0, target: Target)(
      accepted: Instruction => Boolean,
      mismatch: String
  ): Either[String, EncodedInstruction] =
    InstructionDecoder.decode(address, bytes, target).flatMap(decoded =>
      Either.cond(accepted(decoded.operation), (bytes = bytes.toVec, operation = decoded.operation), mismatch)
    )

  private def equivalent(left: Instruction, right: Instruction): Boolean =
    left.mnemonic == right.mnemonic && left.modifiers == right.modifiers && equivalentGuard(left.guard, right.guard) &&
      left.operands.size == right.operands.size && left.operands.zip(right.operands).forall(equivalentOperand(_, _))

  private def equivalentGuard(left: Option[Instruction.Guard], right: Option[Instruction.Guard]): Boolean = {
    val always = Some(Instruction.Guard(OperandQueries.True))
    left == right || (left == always && right.isEmpty) || (right == always && left.isEmpty)
  }

  private def equivalentOperand(left: Operand, right: Operand): Boolean =
    left == right ||
      ((left, right) match {
        case (Operand.Qualified(leftValue, leftSelectors), Operand.Qualified(rightValue, rightSelectors)) =>
          leftSelectors == rightSelectors && equivalentOperand(leftValue, rightValue)
        case _ => false
      }) ||
      ((OperandQueries.integer(left), OperandQueries.integer(right)) match {
        case (Some(a), Some(b)) => a == b
        case _                  =>
          (floatingBits(left), floatingBits(right)) match {
            case (Some(a), Some(b))                  => a == b
            case (Some((format, width, bits)), None) => matchesFloating(right, format, width, bits)
            case (None, Some((format, width, bits))) => matchesFloating(left, format, width, bits)
            case _                                   => false
          }
      })

  private def floatingBits(operand: Operand): Option[(FloatEncoding, Int, BigInt)] = operand match {
    case Operand.FloatingBits(format, width, bits)                                    => Some((format, width, bits))
    case Operand.Unary(Operand.FloatingBits(format, width, bits), true, false, false) =>
      Some((format, width, bits ^ (BigInt(1) << (width - 1))))
    case _ => None
  }

  private def matchesFloating(operand: Operand, format: FloatEncoding, width: Int, bits: BigInt): Boolean = {
    val value = operand match {
      case Operand.Floating(value, _, negativeZero) =>
        Some(if (negativeZero) -0.0 else value.toDouble)
      case Operand.Unary(Operand.Floating(value, _, negativeZero), true, false, false) =>
        Some(if (negativeZero) 0.0 else -value.toDouble)
      case other => OperandQueries.integer(other).map(_.toDouble)
    }
    value.exists { number =>
      val expected = (format, width) match {
        case (FloatEncoding.F16Imm, 16) => Some(IEEE754.fromDouble(number, 16))
        case (FloatEncoding.F32Imm, 32) => Some(IEEE754.fromDouble(number, 32))
        case (FloatEncoding.F64Imm, 64) => Some(IEEE754.doubleToBinary64(number))
        case _                          => None
      }
      expected.exists(encoded => (BigInt(encoded) & ((BigInt(1) << 64) - 1)) == bits)
    }
  }
}

class Assembler(target: Target) {
  private val generation = target.generation

  private def assembly(value: String): Error = Error.Assembly(Error.detail(value))

  def encode(program: Program): Either[Error, Vec[Byte]] = for {
    _ <- program.target match {
      case Some(actual) if actual != target => Left(Error.TargetMismatch(target, actual))
      case _                                => Right(())
    }
    layout <- locate(program)
    (operations, labels, controls) = layout
    slots                          = generation.slots
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
                operation.reference.isEmpty && decoded.operation == instruction,
                (),
                assembly("preserved encoding does not match the instruction")
              )
              _ <- Either.cond(
                decoded.address == address,
                (),
                assembly("encoding annotation address differs from program layout")
              )
              machine <- decoded.machine.toRight(assembly("preserved instruction has no machine annotation"))
              _       <- Either.cond(machine.target == target, (), Error.TargetMismatch(target, machine.target))
              encoded <- Assembler.encodeCanonical(decoded).left.map(assembly(_))
            } yield encoded
          case None => Assembler.encodeCanonical(instruction, target, address).left.map(assembly(_))
        }
        expected = if (generation == Generation.Maxwell) 11 else generation.instructionBytes.raw
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
        generation match {
          case Generation.Kepler  => Control.Kepler(Vec.fill(8)(0.toByte))
          case Generation.Maxwell => Control.Maxwell(words.map(word => controlOf(word.bytes)), 0)
          case Generation.Modern  => Control.Modern
        }
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
              case Control.Maxwell(values, _) => validateControl(word.bytes, values(slot), address, word.operation)
              case _                          => Right(())
            }
          } yield CodeWords.CodeWord(address, word.bytes.take(generation.instructionBytes))
        }
        base = index * generation.bundleBytes
      } yield CodeWords.CodeBundle(base.unsafe[CodeWords.Address], control, located)
    }
    bytes <- CodeWords.join(CodeWords.Layout(generation, bundles)).left.map(assembly(_))
  } yield bytes.toVec

  private def validateControl(
      word: Vec[Byte],
      control: CodeWords.ControlBits,
      address: Int >= 0,
      canonical: Instruction
  ): Either[Error, Unit] =
    if (control == controlOf(word)) Right(())
    else for {
      patched <- InstructionDecoder.decode(address, (word.take(8) ++ CodeWords.controlBytes(control)).toArray, target)
        .left.map(assembly(_))
      _ <- Either.cond(canonical == patched.operation, (), assembly("bundle control changes instruction semantics"))
    } yield ()

  private def controlOf(bytes: Vec[Byte]): CodeWords.ControlBits =
    ((bytes(8) & 255) | ((bytes(9) & 255) << 8) | ((bytes(10) & 31) << 16)).unsafe[CodeWords.ControlBits]

  /** `program` laid out afresh after instructions were inserted or removed: each preserved encoding moves to the
    * address its position now gives it, and direct branch targets that named a preserved instruction's old address
    * follow that instruction. A branch whose distance to its target changed is encoded anew.
    */
  def relaid(program: Program): Either[Error, Program] = for {
    layout <- locate(program)
    (operations, _, _) = layout
    addresses <- operations.indices.toVec.traverse(addressOf(_))
    moved = operations.zip(addresses).collect { case (Operation(_, Some(preserved), _), address) =>
      BigInt(preserved.address) -> BigInt(address)
    }.toMap
    rebuilt <-
      program.statements.foldLeft[Either[Error, (Vec[Program.Statement], Int)]](Right(Vec.empty[Program.Statement] ->
        0)) {
        case (result, operation @ Operation(instruction, preserved, _)) => result.flatMap { (statements, index) =>
            val address                      = addresses(index)
            val retarget: Operand => Operand = {
              case Operand.Integer(destination) if moved.contains(destination) => Operand.Integer(moved(destination))
              case Operand.Bits(destination, width) if moved.contains(destination) =>
                Operand.Bits(moved(destination), width)
              case other => other
            }
            val operands =
              if (ControlFlow.directTarget(instruction)) instruction.operands.map(retarget) else instruction.operands
            val rebuilt =
              if (operands == instruction.operands) Right(instruction)
              else InstructionDefinitions.decode(
                instruction.mnemonic.raw,
                instruction.modifiers,
                operands,
                instruction.guard
              )
                .left.map(assembly(_))
            rebuilt.map { updated =>
              val kept = preserved.filter(old =>
                updated == instruction || moved.get(BigInt(old.address)).exists(now =>
                  instruction.operands.zip(operands).forall {
                    case (before, after) if before == after => true
                    case (before, after)                    =>
                      OperandQueries.integer(after).zip(OperandQueries.integer(before))
                        .exists((next, previous) => next - now == previous - old.address.raw)
                  }
                )
              ).map(_.copy(address = address, operation = updated))
              (statements :+ operation.copy(instruction = updated, preserved = kept), index + 1)
            }
          }
        case (result, statement) => result.map((statements, index) => (statements :+ statement, index))
      }
  } yield program.copy(statements = rebuilt._1)

  private def addressOf(index: Int): Either[Error, Int >= 0] = {
    val slots   = generation.slots
    val address = index.toLong / slots * generation.bundleBytes + generation.controlBytes +
      index % slots * generation.instructionBytes
    Either.cond(
      address <= Int.MaxValue,
      address.toInt.unsafe[Int >= 0],
      assembly("program address exceeds the supported range")
    )
  }

  private def locate(
      program: Program
  ): Either[Error, (Vec[Operation], Map[String, Int >= 0], Map[Int, Control])] = {
    val initial = (Vec.empty[Operation], Map.empty[String, Int >= 0], Map.empty[Int, Control])
    program.statements.foldLeft[Either[
      Error,
      (Vec[Operation], Map[String, Int >= 0], Map[Int, Control])
    ]](Right(initial)) {
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
              if (operations.size % generation.slots != 0 || controls.contains(operations.size))
                Left(assembly("bundle directive must precede a complete bundle"))
              else {
                val valid = (generation, control) match {
                  case (Generation.Kepler, Control.Kepler(bytes))       => bytes.size == 8
                  case (Generation.Maxwell, Control.Maxwell(values, _)) => values.size == 3
                  case (Generation.Modern, Control.Modern)              => true
                  case _                                                => false
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

  private def resolve(operation: Operation, labels: Map[String, Int >= 0]): Either[Error, Instruction] =
    operation.reference.fold(Right(operation.instruction)) { reference =>
      for {
        _       <- Parser.symbolicReference(operation).left.map(assembly(_))
        address <- labels.get(reference.name.raw).toRight(Error.UndefinedLabel(reference.name))
        instruction = operation.instruction
        resolved <- InstructionDefinitions.decode(
          instruction.mnemonic.raw,
          instruction.modifiers,
          instruction.operands.updated(reference.operand, Operand.Integer(BigInt(address))),
          instruction.guard
        ).left.map(assembly(_))
      } yield resolved
    }
}
