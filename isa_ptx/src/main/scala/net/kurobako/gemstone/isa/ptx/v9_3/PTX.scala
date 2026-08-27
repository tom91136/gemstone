package net.kurobako.gemstone.isa.ptx.v9_3

import net.kurobako.gemstone.execution.*
import net.kurobako.gemstone.memory.CheckedMemory
import net.kurobako.gemstone.memory.GuestAddress
import net.kurobako.gemstone.scheduler.LaunchShape
import net.kurobako.gemstone.trace.Trace
import net.kurobako.gemstone.trace.TraceEvent
import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

enum PTXValue {
  case Register(name: String)
  case Immediate(value: Long)
  case Address(base: PTXValue, offset: Long)
  case ThreadIdX
  case BlockIdX
  case BlockDimX
}

enum PTXIntegerOperation {
  case Add
  case Subtract
  case MultiplyLow
  case MultiplyWideUnsigned32
  case MultiplyWideSigned32
  case ShiftLeft
}

enum PTXIntegerUnaryOperation {
  case Negate
  case BitwiseNot
}

enum PTXComparison {
  case Equal
  case EqualUnordered
  case NotEqual
  case NotEqualUnordered
  case LessThan
  case LessThanUnordered
  case LessThanOrEqual
  case LessThanOrEqualUnordered
  case GreaterThan
  case GreaterThanUnordered
  case GreaterThanOrEqual
  case GreaterThanOrEqualUnordered
}

enum PTXFloatOperation {
  case Add
  case Subtract
  case Multiply
}

enum PTXFloatUnaryOperation {
  case Negate
}

sealed trait PTXInstruction extends Product with Serializable {
  def opcode: String
}

object PTXInstruction {
  final case class LoadParameter(destinations: Vector[String], parameter: String, offset: Int, elementWidth: Int)
      extends PTXInstruction {
    override val opcode: String = s"ld.param.u$elementWidth"
  }

  final case class Move(destination: String, source: PTXValue, width: Int) extends PTXInstruction {
    override val opcode: String = s"mov.u$width"
  }

  final case class IntegerUnary(operation: PTXIntegerUnaryOperation, destination: String, source: PTXValue, width: Int)
      extends PTXInstruction {
    override val opcode: String = s"${if (operation == PTXIntegerUnaryOperation.Negate) "neg" else "not"}.b$width"
  }

  final case class IntegerBinary(
      operation: PTXIntegerOperation,
      destination: String,
      left: PTXValue,
      right: PTXValue,
      width: Int
  ) extends PTXInstruction {
    override val opcode: String = operation match {
      case PTXIntegerOperation.Add                    => s"add.u$width"
      case PTXIntegerOperation.Subtract               => s"sub.u$width"
      case PTXIntegerOperation.MultiplyLow            => s"mul.lo.u$width"
      case PTXIntegerOperation.MultiplyWideUnsigned32 => "mul.wide.u32"
      case PTXIntegerOperation.MultiplyWideSigned32   => "mul.wide.s32"
      case PTXIntegerOperation.ShiftLeft              => s"shl.b$width"
    }
  }

  final case class MultiplyAddLow32(destination: String, left: PTXValue, right: PTXValue, addend: PTXValue)
      extends PTXInstruction {
    override val opcode: String = "mad.lo.s32"
  }

  final case class FloatBinary(
      operation: PTXFloatOperation,
      destination: String,
      left: PTXValue,
      right: PTXValue,
      width: Int
  ) extends PTXInstruction {
    override val opcode: String = s"${operation.toString.toLowerCase}.f$width"
  }

  final case class FloatUnary(operation: PTXFloatUnaryOperation, destination: String, source: PTXValue, width: Int)
      extends PTXInstruction {
    override val opcode: String = s"neg.f$width"
  }

  final case class Convert(
      destination: String,
      source: PTXValue,
      sourceType: String,
      destinationType: String,
      rounding: Option[String]
  ) extends PTXInstruction {
    override val opcode: String = s"cvt.${rounding.map(_ + ".").getOrElse("")}$destinationType.$sourceType"
  }

  final case class LoadGlobal(destination: String, address: PTXValue, width: Int) extends PTXInstruction {
    override val opcode: String = s"ld.global.u$width"
  }

  final case class StoreGlobal(address: PTXValue, source: PTXValue, width: Int) extends PTXInstruction {
    override val opcode: String = s"st.global.u$width"
  }

  final case class SetPredicate(
      destination: String,
      comparison: PTXComparison,
      kind: Char,
      left: PTXValue,
      right: PTXValue,
      width: Int
  ) extends PTXInstruction {
    override val opcode: String = "setp"
  }

  final case class Branch(target: Pc) extends PTXInstruction {
    override val opcode: String = "bra"
  }

  final case class BarrierSync(id: Int) extends PTXInstruction {
    override val opcode: String = "bar.sync"
  }

  case object Return extends PTXInstruction {
    override val opcode: String = "ret"
  }
}

final case class PTXPredicate(name: String, negated: Boolean)

final case class PTXExecutable(instruction: PTXInstruction, predication: Option[PTXPredicate]) {
  def opcode: String = instruction.opcode
}

final case class PTXParameter(name: String, widthBytes: Int)
final case class PTXArgument(bytes: IArray[Byte])

object PTXArgument {
  def apply(value: Long): PTXArgument =
    PTXArgument(IArray.tabulate(8)(index => (value >>> (index * 8)).toByte))
}

final case class PTXKernel(name: String, parameters: Vector[PTXParameter], program: ExecutableProgram[PTXExecutable]) {
  def instructions: Vector[PTXInstruction] = program.instructions.map(_.instruction)
}

enum PTXLoweringError {
  case Invalid(detail: String)
  case Unsupported(instruction: String, detail: String)

  def message: String = this match {
    case Invalid(detail)                  => detail
    case Unsupported(instruction, detail) => s"unsupported instruction: $instruction ($detail)"
  }
}

final case class PTXParseError(line: Int, message: String)

object PTXLowerer {
  def lower(entry: Statement.Entry): Either[PTXLoweringError, PTXKernel] = {
    val Statement.Entry(_, name, parameters, body) = entry
    val flat                                       = flatten(body)
    val labels                                     = mutable.Map.empty[String, Pc]
    var instructionIndex                           = 0
    var statementIndex                             = 0
    while (statementIndex < flat.size) {
      flat(statementIndex) match {
        case Statement.Label(label) =>
          if (labels.contains(label)) return Left(PTXLoweringError.Invalid(s"duplicate label: $label"))
          labels(label) = Pc.unsafe(instructionIndex)
        case Statement.Instruction(_) => instructionIndex += 1
        case _                        => ()
      }
      statementIndex += 1
    }

    val lowered = Vector.newBuilder[PTXExecutable]
    statementIndex = 0
    while (statementIndex < flat.size) {
      flat(statementIndex) match {
        case Statement.Instruction(instruction) =>
          lowerInstruction(instruction, labels.toMap) match {
            case Left(error)  => return Left(error)
            case Right(value) => lowered += value
          }
        case _ => ()
      }
      statementIndex += 1
    }
    lowerParameters(parameters).map(loweredParameters =>
      PTXKernel(name, loweredParameters, ExecutableProgram(lowered.result()))
    )
  }

  private def lowerParameters(parameters: Vector[Parameter]): Either[PTXLoweringError, Vector[PTXParameter]] = {
    val result = Vector.newBuilder[PTXParameter]
    var index  = 0
    while (index < parameters.length) {
      val parameter = parameters(index)
      val widths    = parameter.qualifiers.collect {
        case "u8" | "s8" | "b8"            => 1
        case "u16" | "s16" | "b16"         => 2
        case "u32" | "s32" | "b32" | "f32" => 4
        case "u64" | "s64" | "b64" | "f64" => 8
      }
      widths.lastOption match {
        case Some(width) =>
          val elements = parameter.dimensions.foldLeft[Either[PTXLoweringError, Long]](Right(1L)) { (current, value) =>
            for {
              count   <- current
              element <- immediate(value)
              product <-
                if (element > 0 && count <= Int.MaxValue.toLong / element) Right(count * element)
                else invalid(s"kernel parameter ${parameter.name} has an invalid array dimension: $element")
            } yield product
          }
          elements match {
            case Left(error)  => return Left(error)
            case Right(count) => result += PTXParameter(parameter.name, Math.toIntExact(width.toLong * count))
          }
        case None => return Left(PTXLoweringError.Invalid(s"kernel parameter ${parameter.name} has no scalar width"))
      }
      index += 1
    }
    Right(result.result())
  }

  private def flatten(statements: Vector[Statement]): Vector[Statement] = statements.flatMap {
    case Statement.Block(body) => flatten(body)
    case value                 => Vector(value)
  }

  private def lowerInstruction(
      instruction: Instruction,
      labels: Map[String, Pc]
  ): Either[PTXLoweringError, PTXExecutable] = {
    val lowered = instruction match {
      case Instruction.Ld(destination, address, None, modifiers, _)
          if instruction.qualifiedOpcode.startsWith("ld.param.") =>
        for {
          destinationNames   <- registerNames(destination)
          parameterAndOffset <- parameterAddress(address)
          width              <- integerWidth(modifiers)
        } yield PTXInstruction.LoadParameter(
          destinationNames,
          parameterAndOffset._1,
          parameterAndOffset._2,
          width
        )
      case Instruction.Mov(destination, source, _, _)
          if instruction.qualifiedOpcode == "mov.u32" || instruction.qualifiedOpcode == "mov.b32" =>
        for {
          destinationName <- registerName(destination)
          sourceValue     <- value(source)
        } yield PTXInstruction.Move(destinationName, sourceValue, 32)
      case Instruction.Mov(destination, source, _, _)
          if instruction.qualifiedOpcode == "mov.u64" || instruction.qualifiedOpcode == "mov.b64" =>
        for {
          destinationName <- registerName(destination)
          sourceValue     <- value(source)
        } yield PTXInstruction.Move(destinationName, sourceValue, 64)
      case Instruction.Neg(destination, source, modifiers, _) if !instruction.qualifiedOpcode.contains(".f") =>
        for {
          destinationName <- registerName(destination)
          sourceValue     <- value(source)
          width           <- integerWidth(modifiers)
        } yield PTXInstruction.IntegerUnary(PTXIntegerUnaryOperation.Negate, destinationName, sourceValue, width)
      case Instruction.Neg(destination, source, modifiers, _) if instruction.qualifiedOpcode.contains(".f") =>
        for {
          destinationName <- registerName(destination)
          sourceValue     <- value(source)
          width           <- integerWidth(modifiers)
        } yield PTXInstruction.FloatUnary(PTXFloatUnaryOperation.Negate, destinationName, sourceValue, width)
      case Instruction.Not(destination, source, modifiers, _) =>
        for {
          destinationName <- registerName(destination)
          sourceValue     <- value(source)
          width           <- integerWidth(modifiers)
        } yield PTXInstruction.IntegerUnary(PTXIntegerUnaryOperation.BitwiseNot, destinationName, sourceValue, width)
      case Instruction.Mul(destination, left, right, _, _) if instruction.qualifiedOpcode == "mul.wide.u32" =>
        for {
          destinationName <- registerName(destination)
          leftValue       <- value(left)
          rightValue      <- value(right)
        } yield PTXInstruction.IntegerBinary(
          PTXIntegerOperation.MultiplyWideUnsigned32,
          destinationName,
          leftValue,
          rightValue,
          64
        )
      case Instruction.Mul(destination, left, right, _, _) if instruction.qualifiedOpcode == "mul.wide.s32" =>
        for {
          destinationName <- registerName(destination)
          leftValue       <- value(left)
          rightValue      <- value(right)
        } yield PTXInstruction.IntegerBinary(
          PTXIntegerOperation.MultiplyWideSigned32,
          destinationName,
          leftValue,
          rightValue,
          64
        )
      case Instruction.Mul(destination, left, right, modifiers, _)
          if instruction.qualifiedOpcode.startsWith("mul.lo.") =>
        integerWidth(modifiers).flatMap(integerBinary(PTXIntegerOperation.MultiplyLow, _, destination, left, right))
      case Instruction.Mul(destination, left, right, modifiers, _)
          if instruction.qualifiedOpcode.startsWith("mul.f") =>
        floatBinary(PTXFloatOperation.Multiply, destination, left, right, modifiers)
      case Instruction.Shl(destination, left, right, _, _) if instruction.qualifiedOpcode == "shl.b32" =>
        integerBinary(PTXIntegerOperation.ShiftLeft, 32, destination, left, right)
      case Instruction.Shl(destination, left, right, modifiers, _) if instruction.qualifiedOpcode.startsWith("shl.") =>
        integerWidth(modifiers).flatMap(integerBinary(PTXIntegerOperation.ShiftLeft, _, destination, left, right))
      case Instruction.Mad(destination, left, right, addend, _, _)
          if instruction.qualifiedOpcode == "mad.lo.s32" =>
        for {
          destinationName <- registerName(destination)
          leftValue       <- value(left)
          rightValue      <- value(right)
          addendValue     <- value(addend)
        } yield PTXInstruction.MultiplyAddLow32(destinationName, leftValue, rightValue, addendValue)
      case Instruction.Add(destination, left, right, _, _)
          if instruction.qualifiedOpcode == "add.u32" || instruction.qualifiedOpcode == "add.s32" =>
        integerBinary(PTXIntegerOperation.Add, 32, destination, left, right)
      case Instruction.Add(destination, left, right, _, _)
          if instruction.qualifiedOpcode == "add.u64" || instruction.qualifiedOpcode == "add.s64" =>
        integerBinary(PTXIntegerOperation.Add, 64, destination, left, right)
      case Instruction.Sub(destination, left, right, _, _)
          if instruction.qualifiedOpcode == "sub.u32" || instruction.qualifiedOpcode == "sub.s32" =>
        integerBinary(PTXIntegerOperation.Subtract, 32, destination, left, right)
      case Instruction.Sub(destination, left, right, _, _)
          if instruction.qualifiedOpcode == "sub.u64" || instruction.qualifiedOpcode == "sub.s64" =>
        integerBinary(PTXIntegerOperation.Subtract, 64, destination, left, right)
      case Instruction.Add(destination, left, right, modifiers, _)
          if instruction.qualifiedOpcode.startsWith("add.f") =>
        floatBinary(PTXFloatOperation.Add, destination, left, right, modifiers)
      case Instruction.Sub(destination, left, right, modifiers, _)
          if instruction.qualifiedOpcode.startsWith("sub.f") =>
        floatBinary(PTXFloatOperation.Subtract, destination, left, right, modifiers)
      case Instruction.Add(destination, left, right, modifiers, _)
          if instruction.qualifiedOpcode.startsWith("add.") =>
        integerWidth(modifiers).flatMap(integerBinary(PTXIntegerOperation.Add, _, destination, left, right))
      case Instruction.Sub(destination, left, right, modifiers, _)
          if instruction.qualifiedOpcode.startsWith("sub.") =>
        integerWidth(modifiers).flatMap(integerBinary(PTXIntegerOperation.Subtract, _, destination, left, right))
      case Instruction.Cvta(destination, source, _, _) if instruction.qualifiedOpcode == "cvta.to.global.u64" =>
        for {
          destinationName <- registerName(destination)
          sourceValue     <- value(source)
        } yield PTXInstruction.Move(destinationName, sourceValue, 64)
      case Instruction.Cvt(destination, source, None, None, modifiers, _) =>
        val types = modifiers.map(_.name).filter(name => name.matches("[usbf][0-9]+"))
        if (types.size < 2) invalid(s"expected conversion source and destination types: ${instruction.qualifiedOpcode}")
        else
          for {
            destinationName <- registerName(destination)
            sourceValue     <- value(source)
          } yield PTXInstruction.Convert(
            destinationName,
            sourceValue,
            types.last,
            types(types.size - 2),
            modifiers.map(_.name).find(name => Set("rn", "rz", "rzi", "rm", "rmi", "rp", "rpi").contains(name))
          )
      case Instruction.Ld(destination, address, None, modifiers, _)
          if instruction.qualifiedOpcode.startsWith("ld.global.") =>
        for {
          destinationName <- registerName(destination)
          addressValue    <- addressValue(address)
          width           <- integerWidth(modifiers)
        } yield PTXInstruction.LoadGlobal(destinationName, addressValue, width)
      case Instruction.St(address, source, None, modifiers, _)
          if instruction.qualifiedOpcode.startsWith("st.global.") =>
        for {
          addressValue <- addressValue(address)
          sourceValue  <- value(source)
          width        <- integerWidth(modifiers)
        } yield PTXInstruction.StoreGlobal(addressValue, sourceValue, width)
      case Instruction.Setp(destination, left, right, None, modifiers, _) =>
        lowerSetPredicate(destination, left, right, modifiers)
      case Instruction.Bra(target, _, _)
          if instruction.qualifiedOpcode == "bra" || instruction.qualifiedOpcode == "bra.uni" =>
        target match {
          case Operand.Symbol(label) => labels.get(label).toRight(PTXLoweringError.Invalid(s"unknown label: $label"))
              .map(PTXInstruction.Branch.apply)
          case other => invalid(s"expected branch label, found ${Printer.render(other)}")
        }
      case Instruction.Bar(_, _, _, _, _, Some(_)) if instruction.qualifiedOpcode == "bar.sync" =>
        invalid("bar.sync must not be predicated in the supported execution profile")
      case Instruction.Bar(barrier, None, None, None, _, _) if instruction.qualifiedOpcode == "bar.sync" =>
        immediate(barrier).flatMap { value =>
          if (value >= 0L && value <= Int.MaxValue.toLong) Right(PTXInstruction.BarrierSync(value.toInt))
          else invalid(s"barrier id is outside Int range: $value")
        }
      case Instruction.Ret(_, _) => Right(PTXInstruction.Return)
      case _                     =>
        Left(PTXLoweringError.Unsupported(Printer.render(instruction), "no handwritten semantic family"))
    }
    for {
      operation <- lowered
      predicate <- lowerPredicate(instruction.pred)
    } yield PTXExecutable(operation, predicate)
  }

  private def integerBinary(
      operation: PTXIntegerOperation,
      width: Int,
      destination: Operand,
      left: Operand,
      right: Operand
  ): Either[PTXLoweringError, PTXInstruction] =
    for {
      destinationName <- registerName(destination)
      leftValue       <- value(left)
      rightValue      <- value(right)
    } yield PTXInstruction.IntegerBinary(operation, destinationName, leftValue, rightValue, width)

  private def floatBinary(
      operation: PTXFloatOperation,
      destination: Operand,
      left: Operand,
      right: Operand,
      modifiers: Vector[Modifier]
  ): Either[PTXLoweringError, PTXInstruction] =
    for {
      destinationName <- registerName(destination)
      leftValue       <- value(left)
      rightValue      <- value(right)
      width           <- integerWidth(modifiers)
    } yield PTXInstruction.FloatBinary(operation, destinationName, leftValue, rightValue, width)

  private def lowerSetPredicate(
      destination: Operand,
      left: Operand,
      right: Operand,
      instructionModifiers: Vector[Modifier]
  ): Either[PTXLoweringError, PTXInstruction] = {
    val modifiers   = instructionModifiers.map(_.name)
    val instruction = ("setp" +: modifiers).mkString(".")
    val comparison  = modifiers.collectFirst(Function.unlift {
      case "eq"  => Some(PTXComparison.Equal)
      case "equ" => Some(PTXComparison.EqualUnordered)
      case "ne"  => Some(PTXComparison.NotEqual)
      case "neu" => Some(PTXComparison.NotEqualUnordered)
      case "lt"  => Some(PTXComparison.LessThan)
      case "ltu" => Some(PTXComparison.LessThanUnordered)
      case "le"  => Some(PTXComparison.LessThanOrEqual)
      case "leu" => Some(PTXComparison.LessThanOrEqualUnordered)
      case "gt"  => Some(PTXComparison.GreaterThan)
      case "gtu" => Some(PTXComparison.GreaterThanUnordered)
      case "ge"  => Some(PTXComparison.GreaterThanOrEqual)
      case "geu" => Some(PTXComparison.GreaterThanOrEqualUnordered)
      case _     => None
    })
    val kind = modifiers.collectFirst {
      case "u8"  => 'u' -> 8
      case "s8"  => 's' -> 8
      case "u16" => 'u' -> 16
      case "s16" => 's' -> 16
      case "u32" => 'u' -> 32
      case "s32" => 's' -> 32
      case "u64" => 'u' -> 64
      case "s64" => 's' -> 64
      case "f32" => 'f' -> 32
      case "f64" => 'f' -> 64
    }
    for {
      predicate       <- comparison.toRight(PTXLoweringError.Invalid(s"unsupported setp comparison: $instruction"))
      kindAndWidth    <- kind.toRight(PTXLoweringError.Invalid(s"unsupported setp type: $instruction"))
      destinationName <- registerName(destination)
      leftValue       <- value(left)
      rightValue      <- value(right)
    } yield PTXInstruction.SetPredicate(
      destinationName,
      predicate,
      kindAndWidth._1,
      leftValue,
      rightValue,
      kindAndWidth._2
    )
  }

  private def lowerPredicate(value: Option[Predication]): Either[PTXLoweringError, Option[PTXPredicate]] = value match {
    case None                                => Right(None)
    case Some(Predication(negated, operand)) => registerName(operand).map(name => Some(PTXPredicate(name, negated)))
  }

  private def registerName(operand: Operand): Either[PTXLoweringError, String] = operand match {
    case Operand.Register(name, None) => Right(name)
    case other                        => invalid(s"expected register, found ${Printer.render(other)}")
  }

  private def registerNames(operand: Operand): Either[PTXLoweringError, Vector[String]] = operand match {
    case Operand.VectorLiteral(items) =>
      items.foldLeft[Either[PTXLoweringError, Vector[String]]](Right(Vector.empty)) { (current, item) =>
        for {
          names <- current
          name  <- registerName(item)
        } yield names :+ name
      }
    case other => registerName(other).map(Vector(_))
  }

  private def addressValue(operand: Operand): Either[PTXLoweringError, PTXValue] = operand match {
    case Operand.Address(Vector(single)) => valueWithOffset(single)
    case other                           => invalid(s"expected address operand, found ${Printer.render(other)}")
  }

  private def parameterAddress(operand: Operand): Either[PTXLoweringError, (String, Int)] = operand match {
    case Operand.Address(Vector(Operand.Symbol(name)))                      => Right(name -> 0)
    case Operand.Address(Vector(Operand.Add(Operand.Symbol(name), offset))) =>
      immediate(offset).flatMap(value =>
        if (value >= 0L && value <= Int.MaxValue.toLong) Right(name -> value.toInt)
        else invalid(s"parameter offset is outside Int range: $value")
      )
    case other => invalid(s"expected parameter address, found ${Printer.render(other)}")
  }

  private def valueWithOffset(operand: Operand): Either[PTXLoweringError, PTXValue] = operand match {
    case Operand.Add(base, offset) =>
      for {
        baseValue   <- value(base)
        offsetValue <- immediate(offset)
      } yield PTXValue.Address(baseValue, offsetValue)
    case Operand.Subtract(base, offset) =>
      for {
        baseValue   <- value(base)
        offsetValue <- immediate(offset)
      } yield PTXValue.Address(baseValue, -offsetValue)
    case other => value(other)
  }

  private def integerWidth(modifiers: Vector[Modifier]): Either[PTXLoweringError, Int] =
    modifiers.reverseIterator.collectFirst {
      case Modifier(name) if name.matches("[usbf][0-9]+") => name.drop(1).toInt
    }.filter(Set(8, 16, 32, 64).contains).toRight(
      PTXLoweringError.Invalid(
        s"expected an 8, 16, 32, or 64-bit integer type in ${modifiers.map(_.name).mkString(".")}"
      )
    )

  private def value(operand: Operand): Either[PTXLoweringError, PTXValue] = operand match {
    case Operand.Register("%tid.x", None)   => Right(PTXValue.ThreadIdX)
    case Operand.Register("%ctaid.x", None) => Right(PTXValue.BlockIdX)
    case Operand.Register("%ntid.x", None)  => Right(PTXValue.BlockDimX)
    case Operand.Register(name, None)       => Right(PTXValue.Register(name))
    case other: Operand.Immediate           => immediate(other).map(PTXValue.Immediate.apply)
    case other => invalid(s"expected register or immediate, found ${Printer.render(other)}")
  }

  private def immediate(operand: Operand): Either[PTXLoweringError, Long] = operand match {
    case Operand.Immediate(raw) =>
      val signless = raw.dropWhile(character => character == '+' || character == '-')
      val sign     = if (raw.startsWith("-")) -1 else 1
      val parsed   =
        try
          Some(
            if (signless.startsWith("0x") || signless.startsWith("0X")) BigInt(signless.drop(2), 16) * sign
            else if (signless.startsWith("0b") || signless.startsWith("0B")) BigInt(signless.drop(2), 2) * sign
            else if (signless.startsWith("0f") || signless.startsWith("0F")) BigInt(signless.drop(2), 16) * sign
            else if (signless.startsWith("0d") || signless.startsWith("0D")) BigInt(signless.drop(2), 16) * sign
            else BigInt(raw)
          )
        catch {
          case _: NumberFormatException => None
        }
      parsed match {
        case Some(number) if number.isValidLong => Right(number.longValue)
        case Some(_)                            => invalid(s"integer immediate is outside 64-bit range: $raw")
        case None                               => invalid(s"expected integer immediate, found $raw")
      }
    case other => invalid(s"expected integer immediate, found ${Printer.render(other)}")
  }

  private def invalid[A](detail: String): Either[PTXLoweringError, A] = Left(PTXLoweringError.Invalid(detail))
}

object PTXParser {
  def parse(source: String): Either[PTXParseError, PTXKernel] =
    Parser.parse(source).left.map(error => PTXParseError(error.line, error.toString)).flatMap { program =>
      program.statements.collectFirst { case entry: Statement.Entry => entry } match {
        case None        => Left(PTXParseError(1, "missing .entry declaration"))
        case Some(entry) => PTXLowerer.lower(entry).left.map(error => PTXParseError(1, error.message))
      }
    }
}

private enum PTXWrite extends Write {
  case Register(lane: Int, name: String, value: Long)
  case Predicate(lane: Int, name: String, value: Boolean)
  case Memory(lane: Int, address: GuestAddress, width: Int, value: Long)
}

private final class PTXBlockState(val laneCount: Int) {
  val registers: Array[mutable.Map[String, Long]]     = Array.fill(laneCount)(mutable.Map.empty[String, Long])
  val predicates: Array[mutable.Map[String, Boolean]] = Array.fill(laneCount)(mutable.Map.empty[String, Boolean])
  var live: LaneMask                                  =
    LaneMask.all(laneCount).fold(fault => throw new IllegalArgumentException(fault.message), identity)
}

private final class PTXExecutionContext(
    val kernel: PTXKernel,
    val block: Int,
    val state: PTXBlockState,
    val pc: Pc,
    val active: LaneMask,
    val arguments: Map[String, PTXArgument],
    val memory: CheckedMemory
) {
  def read(lane: Int, value: PTXValue): Either[ExecutionFault, Long] = value match {
    case PTXValue.Register(name) => state.registers(lane).get(name)
        .toRight(ExecutionFault.UndefinedRegister(name, pc, lane))
    case PTXValue.Immediate(immediate)  => Right(immediate)
    case PTXValue.Address(base, offset) => read(lane, base).map(_ + offset)
    case PTXValue.ThreadIdX             => Right(lane.toLong)
    case PTXValue.BlockIdX              => Right(block.toLong)
    case PTXValue.BlockDimX             => Right(state.laneCount.toLong)
  }
}

private object PTXSemantics {
  extension (instruction: PTXInstruction) {
    def execute(context: PTXExecutionContext, effects: Effects[PTXWrite]): Either[ExecutionFault, Step] =
      instruction match {
        case PTXInstruction.LoadParameter(destinations, parameter, offset, elementWidth) =>
          context.arguments.get(parameter).toRight(
            ExecutionFault.InvalidState(s"missing kernel argument $parameter")
          ).flatMap { argument =>
            val elementBytes = elementWidth / 8
            if (offset < 0 || offset > argument.bytes.length - elementBytes * destinations.size)
              Left(ExecutionFault.InvalidState(
                s"parameter $parameter load at $offset exceeds ${argument.bytes.length} bytes"
              ))
            else {
              destinations.zipWithIndex.foreach { case (destination, index) =>
                val value = decodeLittleEndian(argument.bytes, offset + index * elementBytes, elementBytes)
                context.active.foreachLane(lane => effects.stage(PTXWrite.Register(lane, destination, value)))
              }
              Right(Step.Advance)
            }
          }
        case PTXInstruction.Move(destination, source, width) =>
          forEveryLane(context) { lane =>
            context.read(lane, source).map(value =>
              effects.stage(PTXWrite.Register(lane, destination, truncate(value, width)))
            )
          }.map(_ => Step.Advance)
        case PTXInstruction.IntegerUnary(operation, destination, source, width) =>
          forEveryLane(context) { lane =>
            context.read(lane, source).map { value =>
              val result = operation match {
                case PTXIntegerUnaryOperation.Negate     => -value
                case PTXIntegerUnaryOperation.BitwiseNot => ~value
              }
              effects.stage(PTXWrite.Register(lane, destination, truncate(result, width)))
            }
          }.map(_ => Step.Advance)
        case PTXInstruction.IntegerBinary(operation, destination, left, right, width) =>
          forEveryLane(context) { lane =>
            for {
              leftValue  <- context.read(lane, left)
              rightValue <- context.read(lane, right)
            } yield {
              val result = operation match {
                case PTXIntegerOperation.Add                    => leftValue + rightValue
                case PTXIntegerOperation.Subtract               => leftValue - rightValue
                case PTXIntegerOperation.MultiplyLow            => leftValue * rightValue
                case PTXIntegerOperation.MultiplyWideUnsigned32 =>
                  (leftValue & 0xffffffffL) * (rightValue & 0xffffffffL)
                case PTXIntegerOperation.MultiplyWideSigned32 => leftValue.toInt.toLong * rightValue.toInt.toLong
                case PTXIntegerOperation.ShiftLeft            => leftValue << (rightValue & (width - 1).toLong).toInt
              }
              effects.stage(PTXWrite.Register(lane, destination, truncate(result, width)))
            }
          }.map(_ => Step.Advance)
        case PTXInstruction.Convert(destination, source, sourceType, destinationType, rounding) =>
          forEveryLane(context) { lane =>
            context.read(lane, source).map { value =>
              effects.stage(PTXWrite.Register(
                lane,
                destination,
                convert(value, sourceType, destinationType, rounding)
              ))
            }
          }.map(_ => Step.Advance)
        case PTXInstruction.MultiplyAddLow32(destination, left, right, addend) =>
          forEveryLane(context) { lane =>
            for {
              leftValue   <- context.read(lane, left)
              rightValue  <- context.read(lane, right)
              addendValue <- context.read(lane, addend)
            } yield effects.stage(
              PTXWrite.Register(lane, destination, truncate(leftValue * rightValue + addendValue, 32))
            )
          }.map(_ => Step.Advance)
        case PTXInstruction.FloatBinary(operation, destination, left, right, width) =>
          forEveryLane(context) { lane =>
            for {
              leftBits  <- context.read(lane, left)
              rightBits <- context.read(lane, right)
            } yield {
              val result =
                if (width == 32) {
                  val leftValue  = java.lang.Float.intBitsToFloat(leftBits.toInt)
                  val rightValue = java.lang.Float.intBitsToFloat(rightBits.toInt)
                  val value      = operation match {
                    case PTXFloatOperation.Add      => leftValue + rightValue
                    case PTXFloatOperation.Subtract => leftValue - rightValue
                    case PTXFloatOperation.Multiply => leftValue * rightValue
                  }
                  java.lang.Float.floatToRawIntBits(value).toLong & 0xffffffffL
                } else {
                  val leftValue  = java.lang.Double.longBitsToDouble(leftBits)
                  val rightValue = java.lang.Double.longBitsToDouble(rightBits)
                  val value      = operation match {
                    case PTXFloatOperation.Add      => leftValue + rightValue
                    case PTXFloatOperation.Subtract => leftValue - rightValue
                    case PTXFloatOperation.Multiply => leftValue * rightValue
                  }
                  java.lang.Double.doubleToRawLongBits(value)
                }
              effects.stage(PTXWrite.Register(lane, destination, result))
            }
          }.map(_ => Step.Advance)
        case PTXInstruction.FloatUnary(PTXFloatUnaryOperation.Negate, destination, source, width) =>
          forEveryLane(context) { lane =>
            context.read(lane, source).map { value =>
              val signBit = if (width == 32) 1L << 31 else 1L << 63
              effects.stage(PTXWrite.Register(lane, destination, value ^ signBit))
            }
          }.map(_ => Step.Advance)
        case PTXInstruction.LoadGlobal(destination, address, width) =>
          forEveryLane(context) { lane =>
            context.read(lane, address).flatMap { rawAddress =>
              readMemory(context.memory, GuestAddress(rawAddress), width).map { loaded =>
                effects.stage(PTXWrite.Register(lane, destination, loaded))
              }
            }
          }.map(_ => Step.Advance)
        case PTXInstruction.StoreGlobal(address, source, width) =>
          forEveryLane(context) { lane =>
            for {
              rawAddress <- context.read(lane, address)
              value      <- context.read(lane, source)
            } yield effects.stage(PTXWrite.Memory(lane, GuestAddress(rawAddress), width, value))
          }.map(_ => Step.Advance)
        case PTXInstruction.SetPredicate(destination, comparison, kind, left, right, width) =>
          forEveryLane(context) { lane =>
            for {
              leftValue  <- context.read(lane, left)
              rightValue <- context.read(lane, right)
            } yield effects.stage(
              PTXWrite.Predicate(lane, destination, compare(leftValue, rightValue, comparison, kind, width))
            )
          }.map(_ => Step.Advance)
        case PTXInstruction.Branch(target)  => Right(Step.Jump(target))
        case PTXInstruction.BarrierSync(id) =>
          Right(Step.Suspend(WaitKey(WaitScope.Workgroup, id.toLong, context.pc.value.toLong)))
        case PTXInstruction.Return => Right(Step.Exit(context.active))
      }
  }

  private def forEveryLane(context: PTXExecutionContext)(
      function: Int => Either[ExecutionFault, Unit]
  ): Either[ExecutionFault, Unit] = {
    var failure: Option[ExecutionFault] = None
    context.active.foreachLane { lane =>
      if (failure.isEmpty) function(lane).left.foreach(fault => failure = Some(fault))
    }
    failure.toLeft(())
  }

  private def truncate(value: Long, width: Int): Long =
    if (width == 64) value else value & ((1L << width) - 1L)

  private def compare(
      left: Long,
      right: Long,
      comparison: PTXComparison,
      kind: Char,
      width: Int
  ): Boolean = {
    val leftValue          = truncate(left, width)
    val rightValue         = truncate(right, width)
    val (order, unordered) =
      if (kind == 'f') {
        val leftFloat = if (width == 32) java.lang.Float.intBitsToFloat(leftValue.toInt).toDouble
        else java.lang.Double.longBitsToDouble(leftValue)
        val rightFloat = if (width == 32) java.lang.Float.intBitsToFloat(rightValue.toInt).toDouble
        else java.lang.Double.longBitsToDouble(rightValue)
        val order = if (leftFloat < rightFloat) -1 else if (leftFloat > rightFloat) 1 else 0
        order -> (leftFloat.isNaN || rightFloat.isNaN)
      } else {
        val integerOrder =
          if (kind == 's') java.lang.Long.compare(signExtend(leftValue, width), signExtend(rightValue, width))
          else if (width == 64) java.lang.Long.compareUnsigned(leftValue, rightValue)
          else java.lang.Long.compare(leftValue, rightValue)
        integerOrder -> false
      }
    comparison match {
      case PTXComparison.Equal                       => !unordered && order == 0
      case PTXComparison.EqualUnordered              => unordered || order == 0
      case PTXComparison.NotEqual                    => !unordered && order != 0
      case PTXComparison.NotEqualUnordered           => unordered || order != 0
      case PTXComparison.LessThan                    => !unordered && order < 0
      case PTXComparison.LessThanUnordered           => unordered || order < 0
      case PTXComparison.LessThanOrEqual             => !unordered && order <= 0
      case PTXComparison.LessThanOrEqualUnordered    => unordered || order <= 0
      case PTXComparison.GreaterThan                 => !unordered && order > 0
      case PTXComparison.GreaterThanUnordered        => unordered || order > 0
      case PTXComparison.GreaterThanOrEqual          => !unordered && order >= 0
      case PTXComparison.GreaterThanOrEqualUnordered => unordered || order >= 0
    }
  }

  private def signExtend(value: Long, width: Int): Long =
    if (width == 64) value else (value << (64 - width)) >> (64 - width)

  private def convert(value: Long, sourceType: String, destinationType: String, rounding: Option[String]): Long = {
    val sourceWidth      = sourceType.drop(1).toInt
    val destinationWidth = destinationType.drop(1).toInt
    val sourceKind       = sourceType.head
    val destinationKind  = destinationType.head
    if (sourceKind == 'f') {
      val floating =
        if (sourceWidth == 32) java.lang.Float.intBitsToFloat(value.toInt).toDouble
        else java.lang.Double.longBitsToDouble(value)
      if (destinationKind == 'f') {
        if (destinationWidth == 32) java.lang.Float.floatToRawIntBits(floating.toFloat).toLong & 0xffffffffL
        else java.lang.Double.doubleToRawLongBits(floating)
      } else {
        val rounded = rounding match {
          case Some("rn")               => Math.rint(floating)
          case Some("rm") | Some("rmi") => Math.floor(floating)
          case Some("rp") | Some("rpi") => Math.ceil(floating)
          case _                        => if (floating < 0) Math.ceil(floating) else Math.floor(floating)
        }
        truncate(rounded.toLong, destinationWidth)
      }
    } else {
      val sourceBits = truncate(value, sourceWidth)
      val integer    =
        if (sourceKind == 's') signExtend(sourceBits, sourceWidth)
        else sourceBits
      if (destinationKind == 'f') {
        val floating =
          if (sourceKind == 'u' && sourceWidth == 64 && integer < 0)
            BigInt(java.lang.Long.toUnsignedString(integer)).toDouble
          else integer.toDouble
        if (destinationWidth == 32) java.lang.Float.floatToRawIntBits(floating.toFloat).toLong & 0xffffffffL
        else java.lang.Double.doubleToRawLongBits(floating)
      } else truncate(integer, destinationWidth)
    }
  }

  private def decodeLittleEndian(bytes: IArray[Byte], offset: Int, size: Int): Long = {
    var result = 0L
    var index  = 0
    while (index < size) {
      result |= (bytes(offset + index).toLong & 0xffL) << (index * 8)
      index += 1
    }
    result
  }

  private def readMemory(memory: CheckedMemory, address: GuestAddress, width: Int): Either[ExecutionFault, Long] = {
    val bytes  = width / 8
    var result = 0L
    var index  = 0
    while (index < bytes) {
      memory.readInt8(address.offset(index.toLong)) match {
        case Left(error)  => return Left(ExecutionFault.Memory(error.message))
        case Right(value) => result |= (value.toLong & 0xffL) << (index * 8)
      }
      index += 1
    }
    Right(result)
  }
}

final case class PTXExecutionError(message: String)
final case class PTXExecutionResult(trace: Trace)

object PTXInterpreter {
  private final case class Cohort(pc: Pc, lanes: LaneMask)
  private final case class Waiting(cohort: Cohort, key: WaitKey)

  def execute(
      kernel: PTXKernel,
      shape: LaunchShape,
      arguments: Map[String, PTXArgument],
      memory: CheckedMemory,
      limits: ExecutionLimits = ExecutionLimits.Default,
      cancellation: CancellationToken = CancellationToken.Never,
      captureTrace: Boolean = true
  ): Either[PTXExecutionError, PTXExecutionResult] = {
    if (limits.maxInstructions <= 0L || limits.quantum <= 0)
      return Left(PTXExecutionError(s"invalid execution limits: $limits"))
    val budget = ExecutionBudget(limits.maxInstructions) match {
      case Left(message) => return Left(PTXExecutionError(message))
      case Right(value)  => value
    }
    var trace = Trace.empty
    var step  = 0L
    var block = 0
    while (block < shape.blocks) {
      executeBlock(
        kernel,
        block,
        shape.threadsPerBlock,
        arguments,
        memory,
        limits,
        budget,
        cancellation,
        captureTrace,
        trace,
        step
      ) match {
        case Left(fault)                  => return Left(PTXExecutionError(fault.message))
        case Right((nextTrace, nextStep)) =>
          trace = nextTrace
          step = nextStep
      }
      block += 1
    }
    Right(PTXExecutionResult(trace))
  }

  private def executeBlock(
      kernel: PTXKernel,
      block: Int,
      laneCount: Int,
      arguments: Map[String, PTXArgument],
      memory: CheckedMemory,
      limits: ExecutionLimits,
      budget: ExecutionBudget,
      cancellation: CancellationToken,
      captureTrace: Boolean,
      initialTrace: Trace,
      initialStep: Long
  ): Either[ExecutionFault, (Trace, Long)] = {
    import PTXSemantics.*

    val state        = new PTXBlockState(laneCount)
    val ready        = ArrayBuffer(Cohort(Pc.Entry, state.live))
    val waiting      = ArrayBuffer.empty[Waiting]
    val barrierMasks = mutable.Map.empty[WaitKey, LaneMask]
    val effects      = new Effects[PTXWrite]
    var trace        = initialTrace
    var traceStep    = initialStep
    var quantum      = 0

    def enqueue(pc: Pc, lanes: LaneMask): Unit =
      if (lanes.nonEmpty) {
        val existing = ready.indexWhere(_.pc == pc)
        if (existing < 0) ready += Cohort(pc, lanes)
        else ready(existing) = Cohort(pc, ready(existing).lanes.union(lanes))
      }

    def advance(pc: Pc, lanes: LaneMask): Either[ExecutionFault, Unit] = pc.next.map(next => enqueue(next, lanes))

    def releaseBarrier(key: WaitKey): Either[ExecutionFault, Unit] = {
      val arrived = barrierMasks.getOrElse(key, LaneMask.Empty)
      if (arrived.intersect(state.live) == state.live) {
        val released = waiting.filter(_.key == key).toVector
        waiting.filterInPlace(_.key != key)
        val _             = barrierMasks.remove(key)
        var releasedIndex = 0
        while (releasedIndex < released.size) {
          val releasedCohort = released(releasedIndex).cohort
          advance(releasedCohort.pc, releasedCohort.lanes) match {
            case Left(fault) => return Left(fault)
            case Right(())   => ()
          }
          releasedIndex += 1
        }
      }
      Right(())
    }

    while (state.live.nonEmpty) {
      if (cancellation.isCancelled) return Left(ExecutionFault.Cancelled)
      if (ready.isEmpty) {
        val waits = waiting.map(value => s"${value.key.scope}:${value.key.id}@${value.key.phase}").distinct.sorted
        return Left(ExecutionFault.Deadlock(waits.mkString(", ")))
      }
      val cohort = ready.remove(0)
      kernel.program.at(cohort.pc) match {
        case Left(fault)       => return Left(fault)
        case Right(executable) =>
          var budgetFailure: Option[ExecutionFault] = None
          cohort.lanes.foreachLane { _ =>
            if (budgetFailure.isEmpty) budget.consume().left.foreach(fault => budgetFailure = Some(fault))
          }
          budgetFailure match {
            case Some(fault) => return Left(fault)
            case None        => ()
          }
          quantum += cohort.lanes.size
          if (quantum >= limits.quantum) quantum = 0

          var active                                   = cohort.lanes
          var predicateFailure: Option[ExecutionFault] = None
          executable.predication.foreach { predicate =>
            var selected = LaneMask.Empty
            cohort.lanes.foreachLane { lane =>
              state.predicates(lane).get(predicate.name) match {
                case Some(predicateValue) if predicateValue != predicate.negated =>
                  selected = selected.union(LaneMask.one(lane).toOption.get)
                case Some(_) => ()
                case None => predicateFailure = Some(ExecutionFault.UndefinedRegister(predicate.name, cohort.pc, lane))
              }
            }
            active = selected
          }
          predicateFailure match {
            case Some(fault) => return Left(fault)
            case None        => ()
          }
          val skipped = cohort.lanes.subtract(active)
          if (captureTrace)
            cohort.lanes.foreachLane { lane =>
              trace = trace
                .append(TraceEvent.SchedulerDecision(traceStep, block, lane))
                .append(TraceEvent.Instruction(
                  traceStep + 1L,
                  kernel.name,
                  block,
                  lane,
                  cohort.pc.value,
                  executable.opcode
                ))
              traceStep += 2L
            }

          val context = new PTXExecutionContext(kernel, block, state, cohort.pc, active, arguments, memory)
          effects.clear()
          val outcome =
            if (active.isEmpty) Right(Step.Advance)
            else executable.instruction.execute(context, effects)
          outcome match {
            case Left(fault)             => return Left(fault)
            case Right(Step.Trap(fault)) => return Left(fault)
            case Right(control)          =>
              val writes = effects.snapshot
              effects.commit(validateWrite(memory), applyWrite(state, memory)) match {
                case Left(fault) => return Left(fault)
                case Right(())   =>
                  if (captureTrace)
                    writes.foreach {
                      case PTXWrite.Memory(lane, address, _, value) =>
                        trace = trace.append(TraceEvent.OutputWrite(traceStep, block, lane, address.value, value.toInt))
                        traceStep += 1L
                      case _ => ()
                    }
              }
              control match {
                case Step.Advance =>
                  advance(cohort.pc, cohort.lanes) match {
                    case Left(fault) => return Left(fault)
                    case Right(())   => ()
                  }
                case Step.Jump(target) =>
                  enqueue(target, active)
                  advance(cohort.pc, skipped) match {
                    case Left(fault) => return Left(fault)
                    case Right(())   => ()
                  }
                case Step.Split(taken, target, fallthrough) =>
                  enqueue(target, taken)
                  advance(cohort.pc, fallthrough) match {
                    case Left(fault) => return Left(fault)
                    case Right(())   => ()
                  }
                case Step.Suspend(key) =>
                  advance(cohort.pc, skipped) match {
                    case Left(fault) => return Left(fault)
                    case Right(())   => ()
                  }
                  waiting += Waiting(cohort.copy(lanes = active), key)
                  barrierMasks(key) = barrierMasks.getOrElse(key, LaneMask.Empty).union(active)
                  releaseBarrier(key) match {
                    case Left(fault) => return Left(fault)
                    case Right(())   => ()
                  }
                case Step.Exit(mask) =>
                  state.live = state.live.subtract(mask)
                  advance(cohort.pc, skipped) match {
                    case Left(fault) => return Left(fault)
                    case Right(())   => ()
                  }
                  val keys     = barrierMasks.keys.toVector
                  var keyIndex = 0
                  while (keyIndex < keys.size) {
                    releaseBarrier(keys(keyIndex)) match {
                      case Left(fault) => return Left(fault)
                      case Right(())   => ()
                    }
                    keyIndex += 1
                  }
                case Step.Trap(fault) => return Left(fault)
              }
          }
      }
    }
    Right(trace -> traceStep)
  }

  private def validateWrite(memory: CheckedMemory)(write: PTXWrite): Either[ExecutionFault, Unit] = write match {
    case PTXWrite.Memory(_, address, width, _) =>
      memory.readInt8(address.offset(width / 8L - 1L)).left.map(error => ExecutionFault.Memory(error.message)).map(_ =>
        ()
      )
    case _ => Right(())
  }

  private def applyWrite(state: PTXBlockState, memory: CheckedMemory)(write: PTXWrite): Unit = write match {
    case PTXWrite.Register(lane, name, value)      => state.registers(lane)(name) = value
    case PTXWrite.Predicate(lane, name, value)     => state.predicates(lane)(name) = value
    case PTXWrite.Memory(_, address, width, value) =>
      var index = 0
      while (index < width / 8) {
        memory.writeInt8(address.offset(index.toLong), (value >>> (index * 8)).toByte) match {
          case Left(error) => throw new IllegalStateException(error.message)
          case Right(())   => ()
        }
        index += 1
      }
  }
}
