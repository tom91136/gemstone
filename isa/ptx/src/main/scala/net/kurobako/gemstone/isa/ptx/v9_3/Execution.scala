package net.kurobako.gemstone.isa.ptx.v9_3

import net.kurobako.gemstone.Col

import net.kurobako.gemstone.binary.Bits.byteCount
import net.kurobako.gemstone.binary.Bits.low32
import net.kurobako.gemstone.binary.Bits.signExtend
import net.kurobako.gemstone.binary.Bits.truncate
import net.kurobako.gemstone.binary.IEEE754
import net.kurobako.gemstone.binary.LittleEndian
import net.kurobako.gemstone.execution.*
import net.kurobako.gemstone.memory.AddressMemory
import net.kurobako.gemstone.memory.GuestAddress
import net.kurobako.gemstone.trace.Trace
import net.kurobako.gemstone.trace.TraceEvent

import cats.syntax.all.*

import scala.collection.mutable // scalafix:ok GemstoneRuntimeCollections.mutable-collection; architectural state
import scala.collection.mutable.ArrayBuffer // scalafix:ok GemstoneRuntimeCollections.mutable-collection; scheduler queue

enum Value {
  case Register(name: String)
  case Immediate(value: Long)
  case Address(base: Value, offset: Long)
  case ThreadId(axis: Axis)
  case BlockId(axis: Axis)
  case BlockDim(axis: Axis)
  case GridDim(axis: Axis)
}

enum IntegerOperation {
  case Add
  case Subtract
  case MultiplyLow
  case MultiplyWideUnsigned32
  case MultiplyWideSigned32
  case ShiftLeft
}

enum IntegerUnaryOperation {
  case Negate
  case BitwiseNot
}

enum Comparison {
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

enum FloatOperation {
  case Add
  case Subtract
  case Multiply
}

enum FloatUnaryOperation {
  case Negate
}

enum ExecutableInstruction(val opcode: String) {
  case LoadParameter(destinations: Col[String], parameter: String, offset: Int, elementWidth: Int)
      extends ExecutableInstruction(s"ld.param.u$elementWidth")

  case Move(destination: String, source: Value, width: Int) extends ExecutableInstruction(s"mov.u$width")

  case IntegerUnary(operation: IntegerUnaryOperation, destination: String, source: Value, width: Int)
      extends ExecutableInstruction(s"${if (operation == IntegerUnaryOperation.Negate) "neg" else "not"}.b$width")

  case IntegerBinary(
      operation: IntegerOperation,
      destination: String,
      left: Value,
      right: Value,
      width: Int
  ) extends ExecutableInstruction(
        operation match {
          case IntegerOperation.Add                    => s"add.u$width"
          case IntegerOperation.Subtract               => s"sub.u$width"
          case IntegerOperation.MultiplyLow            => s"mul.lo.u$width"
          case IntegerOperation.MultiplyWideUnsigned32 => "mul.wide.u32"
          case IntegerOperation.MultiplyWideSigned32   => "mul.wide.s32"
          case IntegerOperation.ShiftLeft              => s"shl.b$width"
        }
      )

  case MultiplyAddLow32(destination: String, left: Value, right: Value, addend: Value)
      extends ExecutableInstruction("mad.lo.s32")

  case FloatBinary(
      operation: FloatOperation,
      destination: String,
      left: Value,
      right: Value,
      width: Int
  ) extends ExecutableInstruction(s"${operation.toString.toLowerCase}.f$width")

  case FloatUnary(operation: FloatUnaryOperation, destination: String, source: Value, width: Int)
      extends ExecutableInstruction(s"neg.f$width")

  case Convert(
      destination: String,
      source: Value,
      sourceType: String,
      destinationType: String,
      rounding: Option[String]
  ) extends ExecutableInstruction(s"cvt.${rounding.map(_ + ".").getOrElse("")}$destinationType.$sourceType")

  case LoadGlobal(destination: String, address: Value, width: Int)
      extends ExecutableInstruction(s"ld.global.u$width")

  case StoreGlobal(address: Value, source: Value, width: Int) extends ExecutableInstruction(s"st.global.u$width")

  case SetPredicate(
      destination: String,
      comparison: Comparison,
      kind: Char,
      left: Value,
      right: Value,
      width: Int
  ) extends ExecutableInstruction("setp")

  case Branch(target: Pc) extends ExecutableInstruction("bra")

  case BarrierSync(id: Int) extends ExecutableInstruction("bar.sync")

  case Return extends ExecutableInstruction("ret")
}

final case class ExecutionPredicate(name: String, negated: Boolean)

final case class PredicatedInstruction(instruction: ExecutableInstruction, predication: Option[ExecutionPredicate]) {
  def opcode: String = instruction.opcode
}

final case class KernelParameter(name: String, widthBytes: Int)
final case class Argument(bytes: Col[Byte])

object Argument {
  def apply(value: Long): Argument =
    Argument(LittleEndian.toCol(value, 8))
}

final case class Kernel(
    name: String,
    parameters: Col[KernelParameter],
    program: ExecutableProgram[PredicatedInstruction]
) {
  def instructions: Col[ExecutableInstruction] = program.instructions.map(_.instruction)
}

enum LoweringError {
  case Invalid(detail: String)
  case Unsupported(instruction: String, detail: String)

  def message: String = this match {
    case Invalid(detail)                  => detail
    case Unsupported(instruction, detail) => s"unsupported instruction: $instruction ($detail)"
  }
}

@SuppressWarnings(
  Array(
    "scalafix:DisableSyntax.var",
    "scalafix:DisableSyntax.return",
    "scalafix:GemstoneRuntimeCollections.mutable-collection"
  )
)
object Lowering {
  def lower(entry: Statement.Entry): Either[LoweringError, Kernel] = {
    val Statement.Entry(_, name, parameters, body) = entry
    val flat                                       = flatten(body)
    val labels                                     = mutable.Map.empty[String, Pc]
    var instructionIndex                           = 0
    var statementIndex                             = 0
    while (statementIndex < flat.size) {
      flat(statementIndex) match {
        case Statement.Label(label) =>
          if (labels.contains(label)) return Left(LoweringError.Invalid(s"duplicate label: $label"))
          labels(label) = Pc.unsafe(instructionIndex)
        case Statement.Instruction(_) => instructionIndex += 1
        case _                        => ()
      }
      statementIndex += 1
    }

    val lowered = Col.newBuilder[PredicatedInstruction]
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
      Kernel(name, loweredParameters, ExecutableProgram(lowered.result()))
    )
  }

  private def lowerParameters(parameters: Col[Parameter]): Either[LoweringError, Col[KernelParameter]] = {
    val result = Col.newBuilder[KernelParameter]
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
          val elements = parameter.dimensions.foldLeft[Either[LoweringError, Long]](Right(1L)) { (current, value) =>
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
            case Right(count) => result += KernelParameter(parameter.name, Math.toIntExact(width.toLong * count))
          }
        case None => return Left(LoweringError.Invalid(s"kernel parameter ${parameter.name} has no scalar width"))
      }
      index += 1
    }
    Right(result.result())
  }

  private def flatten(statements: Col[Statement]): Col[Statement] = statements.flatMap {
    case Statement.Block(body) => flatten(body)
    case value                 => Col(value)
  }

  private def lowerInstruction(
      instruction: Instruction,
      labels: Map[String, Pc]
  ): Either[LoweringError, PredicatedInstruction] = {
    val lowered = instruction match {
      case Instruction.Ld(destination, address, None, modifiers, _)
          if instruction.qualifiedOpcode.startsWith("ld.param.") =>
        for {
          destinationNames   <- registerNames(destination)
          parameterAndOffset <- parameterAddress(address)
          width              <- integerWidth(modifiers)
        } yield ExecutableInstruction.LoadParameter(
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
        } yield ExecutableInstruction.Move(destinationName, sourceValue, 32)
      case Instruction.Mov(destination, source, _, _)
          if instruction.qualifiedOpcode == "mov.u64" || instruction.qualifiedOpcode == "mov.b64" =>
        for {
          destinationName <- registerName(destination)
          sourceValue     <- value(source)
        } yield ExecutableInstruction.Move(destinationName, sourceValue, 64)
      case Instruction.Neg(destination, source, modifiers, _) if !instruction.qualifiedOpcode.contains(".f") =>
        for {
          destinationName <- registerName(destination)
          sourceValue     <- value(source)
          width           <- integerWidth(modifiers)
        } yield ExecutableInstruction.IntegerUnary(IntegerUnaryOperation.Negate, destinationName, sourceValue, width)
      case Instruction.Neg(destination, source, modifiers, _) if instruction.qualifiedOpcode.contains(".f") =>
        for {
          destinationName <- registerName(destination)
          sourceValue     <- value(source)
          width           <- integerWidth(modifiers)
        } yield ExecutableInstruction.FloatUnary(FloatUnaryOperation.Negate, destinationName, sourceValue, width)
      case Instruction.Not(destination, source, modifiers, _) =>
        for {
          destinationName <- registerName(destination)
          sourceValue     <- value(source)
          width           <- integerWidth(modifiers)
        } yield ExecutableInstruction.IntegerUnary(
          IntegerUnaryOperation.BitwiseNot,
          destinationName,
          sourceValue,
          width
        )
      case Instruction.Mul(destination, left, right, _, _) if instruction.qualifiedOpcode == "mul.wide.u32" =>
        for {
          destinationName <- registerName(destination)
          leftValue       <- value(left)
          rightValue      <- value(right)
        } yield ExecutableInstruction.IntegerBinary(
          IntegerOperation.MultiplyWideUnsigned32,
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
        } yield ExecutableInstruction.IntegerBinary(
          IntegerOperation.MultiplyWideSigned32,
          destinationName,
          leftValue,
          rightValue,
          64
        )
      case Instruction.Mul(destination, left, right, modifiers, _)
          if instruction.qualifiedOpcode.startsWith("mul.lo.") =>
        integerWidth(modifiers).flatMap(integerBinary(IntegerOperation.MultiplyLow, _, destination, left, right))
      case Instruction.Mul(destination, left, right, modifiers, _)
          if instruction.qualifiedOpcode.startsWith("mul.f") =>
        floatBinary(FloatOperation.Multiply, destination, left, right, modifiers)
      case Instruction.Shl(destination, left, right, _, _) if instruction.qualifiedOpcode == "shl.b32" =>
        integerBinary(IntegerOperation.ShiftLeft, 32, destination, left, right)
      case Instruction.Shl(destination, left, right, modifiers, _) if instruction.qualifiedOpcode.startsWith("shl.") =>
        integerWidth(modifiers).flatMap(integerBinary(IntegerOperation.ShiftLeft, _, destination, left, right))
      case Instruction.Mad(destination, left, right, addend, _, _)
          if instruction.qualifiedOpcode == "mad.lo.s32" =>
        for {
          destinationName <- registerName(destination)
          leftValue       <- value(left)
          rightValue      <- value(right)
          addendValue     <- value(addend)
        } yield ExecutableInstruction.MultiplyAddLow32(destinationName, leftValue, rightValue, addendValue)
      case Instruction.Add(destination, left, right, _, _)
          if instruction.qualifiedOpcode == "add.u32" || instruction.qualifiedOpcode == "add.s32" =>
        integerBinary(IntegerOperation.Add, 32, destination, left, right)
      case Instruction.Add(destination, left, right, _, _)
          if instruction.qualifiedOpcode == "add.u64" || instruction.qualifiedOpcode == "add.s64" =>
        integerBinary(IntegerOperation.Add, 64, destination, left, right)
      case Instruction.Sub(destination, left, right, _, _)
          if instruction.qualifiedOpcode == "sub.u32" || instruction.qualifiedOpcode == "sub.s32" =>
        integerBinary(IntegerOperation.Subtract, 32, destination, left, right)
      case Instruction.Sub(destination, left, right, _, _)
          if instruction.qualifiedOpcode == "sub.u64" || instruction.qualifiedOpcode == "sub.s64" =>
        integerBinary(IntegerOperation.Subtract, 64, destination, left, right)
      case Instruction.Add(destination, left, right, modifiers, _)
          if instruction.qualifiedOpcode.startsWith("add.f") =>
        floatBinary(FloatOperation.Add, destination, left, right, modifiers)
      case Instruction.Sub(destination, left, right, modifiers, _)
          if instruction.qualifiedOpcode.startsWith("sub.f") =>
        floatBinary(FloatOperation.Subtract, destination, left, right, modifiers)
      case Instruction.Add(destination, left, right, modifiers, _)
          if instruction.qualifiedOpcode.startsWith("add.") =>
        integerWidth(modifiers).flatMap(integerBinary(IntegerOperation.Add, _, destination, left, right))
      case Instruction.Sub(destination, left, right, modifiers, _)
          if instruction.qualifiedOpcode.startsWith("sub.") =>
        integerWidth(modifiers).flatMap(integerBinary(IntegerOperation.Subtract, _, destination, left, right))
      case Instruction.Cvta(destination, source, _, _) if instruction.qualifiedOpcode == "cvta.to.global.u64" =>
        for {
          destinationName <- registerName(destination)
          sourceValue     <- value(source)
        } yield ExecutableInstruction.Move(destinationName, sourceValue, 64)
      case Instruction.Cvt(destination, source, None, None, modifiers, _) =>
        val types = modifiers.map(_.name).filter(isScalarType)
        if (types.size < 2) invalid(s"expected conversion source and destination types: ${instruction.qualifiedOpcode}")
        else
          for {
            destinationName <- registerName(destination)
            sourceValue     <- value(source)
          } yield ExecutableInstruction.Convert(
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
        } yield ExecutableInstruction.LoadGlobal(destinationName, addressValue, width)
      case Instruction.St(address, source, None, modifiers, _)
          if instruction.qualifiedOpcode.startsWith("st.global.") =>
        for {
          addressValue <- addressValue(address)
          sourceValue  <- value(source)
          width        <- integerWidth(modifiers)
        } yield ExecutableInstruction.StoreGlobal(addressValue, sourceValue, width)
      case Instruction.Setp(destination, left, right, None, modifiers, _) =>
        lowerSetPredicate(destination, left, right, modifiers)
      case Instruction.Bra(target, _, _)
          if instruction.qualifiedOpcode == "bra" || instruction.qualifiedOpcode == "bra.uni" =>
        target match {
          case Operand.Symbol(label) => labels.get(label).toRight(LoweringError.Invalid(s"unknown label: $label"))
              .map(ExecutableInstruction.Branch.apply)
          case other => invalid(s"expected branch label, found ${Printer.render(other)}")
        }
      case Instruction.Bar(_, _, _, _, _, Some(_)) if instruction.qualifiedOpcode == "bar.sync" =>
        invalid("bar.sync must not be predicated in the supported execution profile")
      case Instruction.Bar(barrier, None, None, None, _, _) if instruction.qualifiedOpcode == "bar.sync" =>
        immediate(barrier).flatMap { value =>
          if (value >= 0L && value <= Int.MaxValue.toLong) Right(ExecutableInstruction.BarrierSync(value.toInt))
          else invalid(s"barrier id is outside Int range: $value")
        }
      case Instruction.Ret(_, _) => Right(ExecutableInstruction.Return)
      case _                     =>
        Left(LoweringError.Unsupported(Printer.render(instruction), "no handwritten semantic family"))
    }
    for {
      operation <- lowered
      predicate <- lowerPredicate(instruction.pred)
    } yield PredicatedInstruction(operation, predicate)
  }

  private def integerBinary(
      operation: IntegerOperation,
      width: Int,
      destination: Operand,
      left: Operand,
      right: Operand
  ): Either[LoweringError, ExecutableInstruction] =
    for {
      destinationName <- registerName(destination)
      leftValue       <- value(left)
      rightValue      <- value(right)
    } yield ExecutableInstruction.IntegerBinary(operation, destinationName, leftValue, rightValue, width)

  private def floatBinary(
      operation: FloatOperation,
      destination: Operand,
      left: Operand,
      right: Operand,
      modifiers: Col[Modifier]
  ): Either[LoweringError, ExecutableInstruction] =
    for {
      destinationName <- registerName(destination)
      leftValue       <- value(left)
      rightValue      <- value(right)
      width           <- integerWidth(modifiers)
    } yield ExecutableInstruction.FloatBinary(operation, destinationName, leftValue, rightValue, width)

  private def lowerSetPredicate(
      destination: Operand,
      left: Operand,
      right: Operand,
      instructionModifiers: Col[Modifier]
  ): Either[LoweringError, ExecutableInstruction] = {
    val modifiers   = instructionModifiers.map(_.name)
    val instruction = ("setp" +: modifiers).mkString(".")
    val comparison  = modifiers.collectFirst(Function.unlift {
      case "eq"  => Some(Comparison.Equal)
      case "equ" => Some(Comparison.EqualUnordered)
      case "ne"  => Some(Comparison.NotEqual)
      case "neu" => Some(Comparison.NotEqualUnordered)
      case "lt"  => Some(Comparison.LessThan)
      case "ltu" => Some(Comparison.LessThanUnordered)
      case "le"  => Some(Comparison.LessThanOrEqual)
      case "leu" => Some(Comparison.LessThanOrEqualUnordered)
      case "gt"  => Some(Comparison.GreaterThan)
      case "gtu" => Some(Comparison.GreaterThanUnordered)
      case "ge"  => Some(Comparison.GreaterThanOrEqual)
      case "geu" => Some(Comparison.GreaterThanOrEqualUnordered)
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
      predicate       <- comparison.toRight(LoweringError.Invalid(s"unsupported setp comparison: $instruction"))
      kindAndWidth    <- kind.toRight(LoweringError.Invalid(s"unsupported setp type: $instruction"))
      destinationName <- registerName(destination)
      leftValue       <- value(left)
      rightValue      <- value(right)
    } yield ExecutableInstruction.SetPredicate(
      destinationName,
      predicate,
      kindAndWidth._1,
      leftValue,
      rightValue,
      kindAndWidth._2
    )
  }

  private def lowerPredicate(value: Option[Predication]): Either[LoweringError, Option[ExecutionPredicate]] =
    value match {
      case None                                => Right(None)
      case Some(Predication(negated, operand)) =>
        registerName(operand).map(name => Some(ExecutionPredicate(name, negated)))
    }

  private def registerName(operand: Operand): Either[LoweringError, String] = operand match {
    case Operand.Register(name, None) => Right(name)
    case other                        => invalid(s"expected register, found ${Printer.render(other)}")
  }

  private def registerNames(operand: Operand): Either[LoweringError, Col[String]] = operand match {
    case Operand.VectorLiteral(items) =>
      items.foldLeft[Either[LoweringError, Col[String]]](Right(Col.empty)) { (current, item) =>
        for {
          names <- current
          name  <- registerName(item)
        } yield names :+ name
      }
    case other => registerName(other).map(Col(_))
  }

  private def addressValue(operand: Operand): Either[LoweringError, Value] = operand match {
    case Operand.Address(Col(single)) => valueWithOffset(single)
    case other                        => invalid(s"expected address operand, found ${Printer.render(other)}")
  }

  private def parameterAddress(operand: Operand): Either[LoweringError, (String, Int)] = operand match {
    case Operand.Address(Col(Operand.Symbol(name)))                      => Right(name -> 0)
    case Operand.Address(Col(Operand.Add(Operand.Symbol(name), offset))) =>
      immediate(offset).flatMap(value =>
        if (value >= 0L && value <= Int.MaxValue.toLong) Right(name -> value.toInt)
        else invalid(s"parameter offset is outside Int range: $value")
      )
    case other => invalid(s"expected parameter address, found ${Printer.render(other)}")
  }

  private def valueWithOffset(operand: Operand): Either[LoweringError, Value] = operand match {
    case Operand.Add(base, offset) =>
      for {
        baseValue   <- value(base)
        offsetValue <- immediate(offset)
      } yield Value.Address(baseValue, offsetValue)
    case Operand.Subtract(base, offset) =>
      for {
        baseValue   <- value(base)
        offsetValue <- immediate(offset)
      } yield Value.Address(baseValue, -offsetValue)
    case other => value(other)
  }

  private def integerWidth(modifiers: Col[Modifier]): Either[LoweringError, Int] =
    modifiers.reverseIterator.collectFirst {
      case Modifier(name) if isScalarType(name) => name.drop(1).toInt
    }.filter(Set(8, 16, 32, 64).contains).toRight(
      LoweringError.Invalid(
        s"expected an 8, 16, 32, or 64-bit integer type in ${modifiers.map(_.name).mkString(".")}"
      )
    )

  private def isScalarType(name: String): Boolean =
    name.length > 1 && "usbf".contains(name.head) && name.tail.forall(_.isDigit)

  private def value(operand: Operand): Either[LoweringError, Value] = operand match {
    case Operand.Register("%tid.x", None)    => Right(Value.ThreadId(Axis.X))
    case Operand.Register("%tid.y", None)    => Right(Value.ThreadId(Axis.Y))
    case Operand.Register("%tid.z", None)    => Right(Value.ThreadId(Axis.Z))
    case Operand.Register("%ctaid.x", None)  => Right(Value.BlockId(Axis.X))
    case Operand.Register("%ctaid.y", None)  => Right(Value.BlockId(Axis.Y))
    case Operand.Register("%ctaid.z", None)  => Right(Value.BlockId(Axis.Z))
    case Operand.Register("%ntid.x", None)   => Right(Value.BlockDim(Axis.X))
    case Operand.Register("%ntid.y", None)   => Right(Value.BlockDim(Axis.Y))
    case Operand.Register("%ntid.z", None)   => Right(Value.BlockDim(Axis.Z))
    case Operand.Register("%nctaid.x", None) => Right(Value.GridDim(Axis.X))
    case Operand.Register("%nctaid.y", None) => Right(Value.GridDim(Axis.Y))
    case Operand.Register("%nctaid.z", None) => Right(Value.GridDim(Axis.Z))
    case Operand.Register(name, None)        => Right(Value.Register(name))
    case other: Operand.Immediate            => immediate(other).map(Value.Immediate.apply)
    case other => invalid(s"expected register or immediate, found ${Printer.render(other)}")
  }

  private def immediate(operand: Operand): Either[LoweringError, Long] = operand match {
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

  private def invalid[A](detail: String): Either[LoweringError, A] = Left(LoweringError.Invalid(detail))
}

private enum StateWrite extends Write {
  case Register(lane: Int, name: String, value: Long)
  case Predicate(lane: Int, name: String, value: Boolean)
  case Memory(lane: Int, address: GuestAddress, width: Int, value: Long)
}

@SuppressWarnings(Array("scalafix:DisableSyntax.var", "scalafix:GemstoneRuntimeCollections.mutable-collection"))
private final class BlockState(val laneCount: Int, initialLive: LaneMask) {
  val registers: Array[mutable.Map[String, Long]]     = Array.fill(laneCount)(mutable.Map.empty[String, Long])
  val predicates: Array[mutable.Map[String, Boolean]] = Array.fill(laneCount)(mutable.Map.empty[String, Boolean])
  var live: LaneMask                                  = initialLive
}

private final class ExecutionContext(
    val kernel: Kernel,
    val block: Int,
    val group: Coordinates,
    val geometry: LaunchGeometry,
    val state: BlockState,
    val pc: Pc,
    val active: LaneMask,
    val arguments: Map[String, Argument],
    val memory: AddressMemory
) {
  def read(lane: Int, value: Value): Either[ExecutionFault, Long] = value match {
    case Value.Register(name) => state.registers(lane).get(name)
        .toRight(ExecutionFault.UndefinedRegister(name, pc, lane))
    case Value.Immediate(immediate)  => Right(immediate)
    case Value.Address(base, offset) => read(lane, base).map(_ + offset)
    case Value.ThreadId(axis)        => Right(axis.select(geometry.localCoordinates(lane)).toLong)
    case Value.BlockId(axis)         => Right(axis.select(group).toLong)
    case Value.BlockDim(axis)        => Right(axis.select(geometry.groupSize).toLong)
    case Value.GridDim(axis)         => Right(axis.select(geometry.groupCount).toLong)
  }
}

@SuppressWarnings(Array("scalafix:DisableSyntax.var"))
private object Semantics {
  extension (instruction: ExecutableInstruction) {
    def execute(context: ExecutionContext, effects: Effects[StateWrite]): Either[ExecutionFault, Step] =
      instruction match {
        case ExecutableInstruction.LoadParameter(destinations, parameter, offset, elementWidth) =>
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
                val value = LittleEndian.decode(argument.bytes, offset + index * elementBytes, elementBytes)
                context.active.foreachLane(lane => effects.stage(StateWrite.Register(lane, destination, value)))
              }
              Right(Step.Advance)
            }
          }
        case ExecutableInstruction.Move(destination, source, width) =>
          forEveryLane(context) { lane =>
            context.read(lane, source).map(value =>
              effects.stage(StateWrite.Register(lane, destination, truncate(value, width)))
            )
          }.map(_ => Step.Advance)
        case ExecutableInstruction.IntegerUnary(operation, destination, source, width) =>
          forEveryLane(context) { lane =>
            context.read(lane, source).map { value =>
              val result = operation match {
                case IntegerUnaryOperation.Negate     => -value
                case IntegerUnaryOperation.BitwiseNot => ~value
              }
              effects.stage(StateWrite.Register(lane, destination, truncate(result, width)))
            }
          }.map(_ => Step.Advance)
        case ExecutableInstruction.IntegerBinary(operation, destination, left, right, width) =>
          forEveryLane(context) { lane =>
            for {
              leftValue  <- context.read(lane, left)
              rightValue <- context.read(lane, right)
            } yield {
              val result = operation match {
                case IntegerOperation.Add                    => leftValue + rightValue
                case IntegerOperation.Subtract               => leftValue - rightValue
                case IntegerOperation.MultiplyLow            => leftValue * rightValue
                case IntegerOperation.MultiplyWideUnsigned32 =>
                  low32(leftValue) * low32(rightValue)
                case IntegerOperation.MultiplyWideSigned32 => leftValue.toInt.toLong * rightValue.toInt.toLong
                case IntegerOperation.ShiftLeft            => leftValue << (rightValue & (width - 1).toLong).toInt
              }
              effects.stage(StateWrite.Register(lane, destination, truncate(result, width)))
            }
          }.map(_ => Step.Advance)
        case ExecutableInstruction.Convert(destination, source, sourceType, destinationType, rounding) =>
          forEveryLane(context) { lane =>
            context.read(lane, source).map { value =>
              effects.stage(StateWrite.Register(
                lane,
                destination,
                convert(value, sourceType, destinationType, rounding)
              ))
            }
          }.map(_ => Step.Advance)
        case ExecutableInstruction.MultiplyAddLow32(destination, left, right, addend) =>
          forEveryLane(context) { lane =>
            for {
              leftValue   <- context.read(lane, left)
              rightValue  <- context.read(lane, right)
              addendValue <- context.read(lane, addend)
            } yield effects.stage(
              StateWrite.Register(lane, destination, truncate(leftValue * rightValue + addendValue, 32))
            )
          }.map(_ => Step.Advance)
        case ExecutableInstruction.FloatBinary(operation, destination, left, right, width) =>
          forEveryLane(context) { lane =>
            for {
              leftBits  <- context.read(lane, left)
              rightBits <- context.read(lane, right)
            } yield {
              val result =
                if (width == 32) {
                  val leftValue  = IEEE754.binary32ToFloat(leftBits)
                  val rightValue = IEEE754.binary32ToFloat(rightBits)
                  val value      = operation match {
                    case FloatOperation.Add      => leftValue + rightValue
                    case FloatOperation.Subtract => leftValue - rightValue
                    case FloatOperation.Multiply => leftValue * rightValue
                  }
                  IEEE754.floatToBinary32(value)
                } else {
                  val leftValue  = IEEE754.binary64ToDouble(leftBits)
                  val rightValue = IEEE754.binary64ToDouble(rightBits)
                  val value      = operation match {
                    case FloatOperation.Add      => leftValue + rightValue
                    case FloatOperation.Subtract => leftValue - rightValue
                    case FloatOperation.Multiply => leftValue * rightValue
                  }
                  IEEE754.doubleToBinary64(value)
                }
              effects.stage(StateWrite.Register(lane, destination, result))
            }
          }.map(_ => Step.Advance)
        case ExecutableInstruction.FloatUnary(FloatUnaryOperation.Negate, destination, source, width) =>
          forEveryLane(context) { lane =>
            context.read(lane, source).map { value =>
              val signBit = if (width == 32) 1L << 31 else 1L << 63
              effects.stage(StateWrite.Register(lane, destination, value ^ signBit))
            }
          }.map(_ => Step.Advance)
        case ExecutableInstruction.LoadGlobal(destination, address, width) =>
          forEveryLane(context) { lane =>
            context.read(lane, address).flatMap { rawAddress =>
              context.memory
                .readLittleEndian(GuestAddress(rawAddress), byteCount(width))
                .leftMap(error => ExecutionFault.Memory(error.message))
                .map(loaded => effects.stage(StateWrite.Register(lane, destination, loaded)))
            }
          }.map(_ => Step.Advance)
        case ExecutableInstruction.StoreGlobal(address, source, width) =>
          forEveryLane(context) { lane =>
            for {
              rawAddress <- context.read(lane, address)
              value      <- context.read(lane, source)
            } yield effects.stage(StateWrite.Memory(lane, GuestAddress(rawAddress), width, value))
          }.map(_ => Step.Advance)
        case ExecutableInstruction.SetPredicate(destination, comparison, kind, left, right, width) =>
          forEveryLane(context) { lane =>
            for {
              leftValue  <- context.read(lane, left)
              rightValue <- context.read(lane, right)
            } yield effects.stage(
              StateWrite.Predicate(lane, destination, compare(leftValue, rightValue, comparison, kind, width))
            )
          }.map(_ => Step.Advance)
        case ExecutableInstruction.Branch(target)  => Right(Step.Jump(target))
        case ExecutableInstruction.BarrierSync(id) =>
          Right(Step.Suspend(WaitKey(WaitScope.Workgroup, id.toLong, context.pc.value.toLong)))
        case ExecutableInstruction.Return => Right(Step.Exit(context.active))
      }
  }

  private def forEveryLane(context: ExecutionContext)(
      function: Int => Either[ExecutionFault, Unit]
  ): Either[ExecutionFault, Unit] = {
    var failure: Option[ExecutionFault] = None
    context.active.foreachLane { lane =>
      if (failure.isEmpty) function(lane).left.foreach(fault => failure = Some(fault))
    }
    failure.toLeft(())
  }

  private def compare(
      left: Long,
      right: Long,
      comparison: Comparison,
      kind: Char,
      width: Int
  ): Boolean = {
    val leftValue          = truncate(left, width)
    val rightValue         = truncate(right, width)
    val (order, unordered) =
      if (kind == 'f') {
        val leftFloat  = IEEE754.toDouble(leftValue, width)
        val rightFloat = IEEE754.toDouble(rightValue, width)
        val order      = if (leftFloat < rightFloat) -1 else if (leftFloat > rightFloat) 1 else 0
        order -> (leftFloat.isNaN || rightFloat.isNaN)
      } else {
        val integerOrder =
          if (kind == 's') java.lang.Long.compare(signExtend(leftValue, width), signExtend(rightValue, width))
          else if (width == 64) java.lang.Long.compareUnsigned(leftValue, rightValue)
          else java.lang.Long.compare(leftValue, rightValue)
        integerOrder -> false
      }
    comparison match {
      case Comparison.Equal                       => !unordered && order == 0
      case Comparison.EqualUnordered              => unordered || order == 0
      case Comparison.NotEqual                    => !unordered && order != 0
      case Comparison.NotEqualUnordered           => unordered || order != 0
      case Comparison.LessThan                    => !unordered && order < 0
      case Comparison.LessThanUnordered           => unordered || order < 0
      case Comparison.LessThanOrEqual             => !unordered && order <= 0
      case Comparison.LessThanOrEqualUnordered    => unordered || order <= 0
      case Comparison.GreaterThan                 => !unordered && order > 0
      case Comparison.GreaterThanUnordered        => unordered || order > 0
      case Comparison.GreaterThanOrEqual          => !unordered && order >= 0
      case Comparison.GreaterThanOrEqualUnordered => unordered || order >= 0
    }
  }

  private def convert(value: Long, sourceType: String, destinationType: String, rounding: Option[String]): Long = {
    val sourceWidth      = sourceType.drop(1).toInt
    val destinationWidth = destinationType.drop(1).toInt
    val sourceKind       = sourceType.head
    val destinationKind  = destinationType.head
    if (sourceKind == 'f') {
      val floating = IEEE754.toDouble(value, sourceWidth)
      if (destinationKind == 'f') {
        IEEE754.fromDouble(floating, destinationWidth)
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
        IEEE754.fromDouble(floating, destinationWidth)
      } else truncate(integer, destinationWidth)
    }
  }
}

final case class ExecutionError(message: String)
final case class ExecutionResult(trace: Trace)

@SuppressWarnings(
  Array(
    "scalafix:DisableSyntax.var",
    "scalafix:DisableSyntax.return",
    "scalafix:GemstoneRuntimeCollections.mutable-collection"
  )
)
object Interpreter {
  private final case class Cohort(pc: Pc, lanes: LaneMask)
  private final case class Waiting(cohort: Cohort, key: WaitKey)

  def execute(
      kernel: Kernel,
      geometry: LaunchGeometry,
      arguments: Map[String, Argument],
      memory: AddressMemory,
      control: ExecutionControl = ExecutionControl.default,
      captureTrace: Boolean = true
  ): Either[ExecutionError, ExecutionResult] = {
    val laneCount = geometry.groupSize.volumeInt match {
      case Left(message) => return Left(ExecutionError(message))
      case Right(value)  => value
    }
    val groupCount = geometry.groupCount.volumeInt match {
      case Left(message) => return Left(ExecutionError(message))
      case Right(value)  => value
    }
    val allLanes = LaneMask.all(laneCount) match {
      case Left(fault)  => return Left(ExecutionError(fault.message))
      case Right(value) => value
    }
    var trace = Trace.empty
    var step  = 0L
    var block = 0
    while (block < groupCount) {
      val group = geometry.groupCoordinates(block.toLong)
      val live  = allLanes.filterLanes(lane =>
        geometry.containsGlobal(geometry.globalCoordinates(group, geometry.localCoordinates(lane)))
      )
      executeBlock(
        kernel,
        block,
        group,
        geometry,
        live,
        arguments,
        memory,
        control,
        captureTrace,
        trace,
        step
      ) match {
        case Left(fault)                  => return Left(ExecutionError(fault.message))
        case Right((nextTrace, nextStep)) =>
          trace = nextTrace
          step = nextStep
      }
      block += 1
    }
    Right(ExecutionResult(trace))
  }

  private def executeBlock(
      kernel: Kernel,
      block: Int,
      group: Coordinates,
      geometry: LaunchGeometry,
      initialLive: LaneMask,
      arguments: Map[String, Argument],
      memory: AddressMemory,
      control: ExecutionControl,
      captureTrace: Boolean,
      initialTrace: Trace,
      initialStep: Long
  ): Either[ExecutionFault, (Trace, Long)] = {
    import Semantics.*

    val laneCount    = geometry.groupSize.volumeInt.fold(message => throw IllegalArgumentException(message), identity)
    val state        = BlockState(laneCount, initialLive)
    val ready        = ArrayBuffer(Cohort(Pc.Entry, state.live))
    val waiting      = ArrayBuffer.empty[Waiting]
    val barrierMasks = mutable.Map.empty[WaitKey, LaneMask]
    val effects      = Effects[StateWrite]
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
        val released = Col.from(waiting.filter(_.key == key))
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
      if (ready.isEmpty) {
        val waits = waiting.map(value => s"${value.key.scope}:${value.key.id}@${value.key.phase}").distinct.sorted
        return Left(ExecutionFault.Deadlock(waits.mkString(", ")))
      }
      val cohort = ready.remove(0)
      kernel.program.at(cohort.pc) match {
        case Left(fault)       => return Left(fault)
        case Right(executable) =>
          control.checkpoint(cohort.lanes.size) match {
            case Left(fault) => return Left(fault)
            case Right(())   => ()
          }
          quantum += cohort.lanes.size
          if (quantum >= control.limits.quantum) quantum = 0

          var active                                   = cohort.lanes
          var predicateFailure: Option[ExecutionFault] = None
          executable.predication.foreach { predicate =>
            active = cohort.lanes.filterLanes { lane =>
              state.predicates(lane).get(predicate.name) match {
                case Some(predicateValue) => predicateValue != predicate.negated
                case None                 =>
                  predicateFailure = Some(ExecutionFault.UndefinedRegister(predicate.name, cohort.pc, lane))
                  false
              }
            }
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

          val context = ExecutionContext(kernel, block, group, geometry, state, cohort.pc, active, arguments, memory)
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
                      case StateWrite.Memory(lane, address, _, value) =>
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
                  val keys     = Col.from(barrierMasks.keys)
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

  private def validateWrite(memory: AddressMemory)(write: StateWrite): Either[ExecutionFault, Unit] = write match {
    case StateWrite.Memory(_, address, width, _) =>
      memory.validateRange(address, byteCount(width)).leftMap(error => ExecutionFault.Memory(error.message))
    case _ => Right(())
  }

  private def applyWrite(state: BlockState, memory: AddressMemory)(write: StateWrite): Unit = write match {
    case StateWrite.Register(lane, name, value)      => state.registers(lane)(name) = value
    case StateWrite.Predicate(lane, name, value)     => state.predicates(lane)(name) = value
    case StateWrite.Memory(_, address, width, value) =>
      memory
        .writeLittleEndian(address, value, byteCount(width))
        .left
        .foreach(error => throw IllegalStateException(error.message))
  }
}
