package net.kurobako.gemstone.isa.sass.asm

import cats.syntax.all.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.isa.catalogue.*
import net.kurobako.gemstone.isa.sass.*
import scala.collection.mutable

/** Forward encoder for generated SASS layouts. Every field is recomputed when its bindings are available; preserved
  * assigned values are retained only for relaxed constants or when the decoder proves compatibility with bindings.
  */
object Encoder {
  def encode(
      database: NVDisasm.Database,
      form: NVDisasm.Form,
      opcode: String,
      bindings: Map[String, BigInt],
      preserved: Option[BigInt] = None
  ): Either[String, Array[Byte]] = {
    val opcodeValues = form.opcodes.filter(_.name == opcode).map(_.value).distinct
    val opcodeValue  = opcodeValues match {
      case Vec(value)               => Right(value)
      case values if values.isEmpty => Left(s"unknown opcode $opcode for ${form.name}")
      case _                        => Left(s"ambiguous opcode $opcode for ${form.name}")
    }
    val withOpcode = opcodeValue.flatMap { value =>
      bindings.get("Opcode") match {
        case Some(previous) if previous != value => Left(s"Opcode binding $previous conflicts with $opcode=$value")
        case _                                   => Right(bindings.updated("Opcode", value))
      }
    }
    val width = database.width
    for {
      original <- preserved match {
        case Some(value) if value < 0 || value.bitLength > width => Left(s"preserved word exceeds $width bits")
        case value                                               => Right(value.getOrElse(BigInt(0)))
      }
      values        <- withOpcode
      _             <- Legality.check(database, form, Legality.completeBindings(form, values)).left.map(_.message)
      _             <- validatePositions(database, form)
      encodedFields <- form.fields.traverse { field =>
        val oldField = preserved.map(word => FieldDecoder.extract(word, field.positions))
        val encoded  = field.expression match {
          case Expression.Constant(_, true) if oldField.nonEmpty => Right(oldField.get)
          case _ => evaluate(database, field.expression, values).orElse {
              oldField.filter(FieldDecoder.accepts(database, field.expression, _, values))
                .toRight(s"cannot encode ${field.name} from supplied bindings")
            }
        }
        encoded.map(value => field -> value)
      }
      encodedWord <- assign(width, form, encodedFields, original)
    } yield Array.tabulate(width / 8) { byteIndex =>
      ((encodedWord >> (byteIndex * 8)) & 0xff).toByte
    }
  }

  private def validatePositions(database: NVDisasm.Database, form: NVDisasm.Form): Either[String, Unit] =
    form.fields.foldLeft[Either[String, Unit]](Right(())) { (result, field) =>
      result.flatMap { _ =>
        val positions = field.positions
        if (positions.distinct.size != positions.size)
          Left(s"${form.name}.${field.name} assigns a bit more than once")
        else if (positions.exists(position => position < 0 || position >= database.width))
          Left(s"${form.name}.${field.name} has a position outside the ${database.width}-bit word")
        else Right(())
      }
    }

  private def assign(
      width: Int,
      form: NVDisasm.Form,
      fields: Vec[(NVDisasm.Assignment, BigInt)],
      original: BigInt
  ): Either[String, BigInt] = {
    val initial = original & ((BigInt(1) << width) - 1) & ~form.assignedMask
    val bits    = mutable.Map.empty[Int, Boolean]
    fields.foldLeft[Either[String, Unit]](Right(())) { case (result, (field, value)) =>
      result.flatMap { _ =>
        val capacity = BigInt(1) << field.positions.size
        if (value < 0 || value >= capacity)
          Left(s"${form.name}.${field.name} value $value does not fit ${field.positions.size} bits")
        else field.positions.zipWithIndex.foldLeft[Either[String, Unit]](Right(())) {
          case (current, (position, logical)) =>
            current.flatMap { _ =>
              val bit = value.testBit(logical)
              bits.get(position) match {
                case Some(previous) if previous != bit =>
                  Left(s"conflicting assignments to bit $position in ${form.name}")
                case _ =>
                  bits.update(position, bit)
                  Right(())
              }
            }
        }
      }
    }.map(_ =>
      bits.foldLeft(initial) { case (word, (position, value)) =>
        if (value) word.setBit(position) else word.clearBit(position)
      }
    )
  }

  private def evaluate(
      database: NVDisasm.Database,
      expression: Expression,
      bindings: Map[String, BigInt]
  ): Either[String, BigInt] = expression match {
    case Expression.Constant(value, _)            => Right(value)
    case Expression.Reference(name, attribute, _) =>
      val key = NVDisasm.bindingKey(name, attribute)
      bindings.get(key).toRight(s"missing encoding binding $key")
    case Expression.Scale(inner, factor) =>
      if (factor == 0) Left("zero encoding scale")
      else evaluate(database, inner, bindings).flatMap { value =>
        Either.cond(value % factor == 0, value / factor, s"value $value cannot be represented by scale $factor")
      }
    case Expression.Multiply(inner, factor) =>
      if (factor == 0) Left("zero encoding multiplier")
      else evaluate(database, inner, bindings).map(_ * factor)
    case Expression.Identity(first, second) => for {
        left  <- evaluate(database, first, bindings)
        right <- evaluate(database, second, bindings)
        value <- Either.cond(left == right, left, s"conflicting identity encodings $left and $right")
      } yield value
    case Expression.FixedFloat(inner, _)        => evaluate(database, inner, bindings)
    case Expression.Relaxed(inner)              => evaluate(database, inner, bindings)
    case Expression.FloatConvert(value = inner) => evaluate(database, inner, bindings)
    case Expression.Lookup(name, arguments)     =>
      database.byTable.get(name).toRight(s"missing encoding lookup $name").flatMap { table =>
        arguments.traverse(evaluate(database, _, bindings)).flatMap { inputs =>
          table.lookup(inputs) match {
            case Vec(value)               => Right(value)
            case values if values.isEmpty => Left(s"no encoding lookup row for $name($inputs)")
            case values                   => Left(s"ambiguous encoding lookup $name($inputs): ${values.mkString(", ")}")
          }
        }
      }
  }

}
