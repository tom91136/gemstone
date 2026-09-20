package net.kurobako.gemstone.rpc

import net.kurobako.gemstone.{NoNul, Vec}

import java.nio.charset.StandardCharsets

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

import scala.compiletime.{constValue, erasedValue, summonInline}
import scala.util.NotGiven
import scala.annotation.publicInBinary
import scala.deriving.Mirror
import scala.quoted.*
import scala.reflect.ClassTag

object Wire {
  private val InitialCapacity = 256

  final case class Limits(
      maximumElements: Int :| Positive,
      maximumBytes: Int :| Positive,
      maximumTextBytes: Int :| Positive
  )

  enum Constraint {
    case Positive
    case Positive0
    case IntInterval(minimum: Int, maximum: Int)
    case LongInterval(minimum: Long, maximum: Long)
    case MinimumLength(minimum: Int)
    case NotContains(value: String)
  }

  enum Shape {
    case UnitValue
    case BooleanValue
    case Int8
    case Int32
    case Int64
    case Text
    case Sequence(element: Shape)
    case Optional(element: Shape)
    case Product(name: String, fields: Vector[Field])
    case Sum(name: String, alternatives: Vector[Alternative])
    case Alias(name: String, underlying: Shape, constraints: Vector[Constraint])
  }

  final case class Field(name: String, shape: Shape)
  final case class Alternative(name: String, id: Long, shape: Shape)

  private[rpc] final class Writer(val limits: Limits) {
    @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
    private var mutableBytes = Array.ofDim[Byte](math.min(InitialCapacity, limits.maximumBytes))
    @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
    private var length = 0
    @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
    private var failure = Option.empty[String]

    def writeByte(value: Byte): Unit =
      if ensure(1) then {
        mutableBytes(length) = value
        length += 1
      }

    def writeInt(value: Int): Unit =
      if ensure(4) then {
        mutableBytes(length) = (value >>> 24).toByte
        mutableBytes(length + 1) = (value >>> 16).toByte
        mutableBytes(length + 2) = (value >>> 8).toByte
        mutableBytes(length + 3) = value.toByte
        length += 4
      }

    def writeLong(value: Long): Unit = {
      writeInt((value >>> 32).toInt)
      writeInt(value.toInt)
    }

    def writeBytes(value: Array[Byte]): Unit =
      if ensure(value.length) then {
        Array.copy(value, 0, mutableBytes, length, value.length)
        length += value.length
      }

    def reject(message: String): Unit = if failure.isEmpty then failure = Some(message)

    def result(): Either[String, Array[Byte]] =
      failure.toLeft(java.util.Arrays.copyOf(mutableBytes, length))

    private def ensure(additional: Int): Boolean =
      if failure.nonEmpty then false
      else if additional < 0 || additional > limits.maximumBytes - length then {
        reject(s"wire value exceeds the ${limits.maximumBytes}-byte limit")
        false
      } else {
        val required = length + additional
        if required > mutableBytes.length then {
          val doubled = math.min(limits.maximumBytes.toLong, math.max(1L, mutableBytes.length.toLong * 2L)).toInt
          mutableBytes = java.util.Arrays.copyOf(mutableBytes, math.max(required, doubled))
        }
        true
      }
  }

  private[rpc] final class Reader(bytes: Array[Byte], val limits: Limits) {
    @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
    private var mutableCursor = 0

    def remaining: Int = bytes.length - mutableCursor

    def readByte(): Either[String, Byte] =
      take(1).map(offset => bytes(offset))

    def readInt(): Either[String, Int] =
      take(4).map { offset =>
        (bytes(offset) & 0xff) << 24 |
          (bytes(offset + 1) & 0xff) << 16 |
          (bytes(offset + 2) & 0xff) << 8 |
          (bytes(offset + 3) & 0xff)
      }

    def readLong(): Either[String, Long] = for {
      high <- readInt()
      low  <- readInt()
    } yield (high.toLong << 32) | Integer.toUnsignedLong(low)

    def readBytes(size: Int): Either[String, Array[Byte]] =
      if (size < 0) Left(s"negative byte count: $size")
      else take(size).map(offset => java.util.Arrays.copyOfRange(bytes, offset, offset + size))

    def readCount(
        description: String :| MinLength[1],
        maximum: Int :| Positive
    ): Either[String, Int] =
      readInt().flatMap { count =>
        Either.cond(
          count >= 0 && count <= maximum,
          count,
          s"invalid $description count: $count"
        )
      }

    private def take(size: Int): Either[String, Int] =
      if (size < 0 || size > remaining)
        Left(s"wire value is truncated: required $size bytes, found $remaining")
      else {
        val offset = mutableCursor
        mutableCursor += size
        Right(offset)
      }
  }

  trait Codec[A] {
    def shape: Shape
    private[rpc] def write(writer: Writer, value: A): Unit
    private[rpc] def read(reader: Reader): Either[String, A]

    final def encode(value: A, limits: Limits): Either[String, Array[Byte]] = {
      val writer = Writer(limits)
      write(writer, value)
      writer.result()
    }

    final def decode(bytes: Array[Byte], limits: Limits): Either[String, A] = {
      val reader = Reader(bytes, limits)
      read(reader).flatMap { value =>
        Either.cond(
          reader.remaining == 0,
          value,
          s"wire value has ${reader.remaining} trailing bytes"
        )
      }
    }

  }

  trait Protocol[F[_]] {
    def shape: Shape.Sum
    def operations: Vector[OperationShape] :| MinLength[1]
    def encodePayload[A](value: F[A], limits: Limits): Either[String, (Long, Array[Byte])]
    def decodePayload(
        id: Long,
        bytes: Array[Byte],
        limits: Limits
    ): Either[String, Decoded[F]]

    final def schema[Failure: Codec](name: String :| (MinLength[1] & NoNul)): ProtocolShape =
      ProtocolShape(name, operations, Codec[Failure].shape)
  }

  object Protocol {
    def apply[F[_]](using protocol: Protocol[F]): Protocol[F] = protocol

    inline def derived[F[_]]: Protocol[F] = ${ derivedProtocol[F] }

    @SuppressWarnings(Array("scalafix:GemstoneNoAssume"))
    private def derivedProtocol[F[_]: Type](using Quotes): Expr[Protocol[F]] = {
      import quotes.reflect.*

      val operationType   = TypeRepr.of[F]
      val operationSymbol = operationType.typeSymbol
      if (!operationSymbol.flags.is(Flags.Enum))
        report.errorAndAbort(s"${operationType.show} is not an enum")
      val name       = operationSymbol.name
      val identified = operationSymbol.children.map { child =>
        val qualifiedName = s"$name.${child.name}"
        (child, qualifiedName, hash64(qualifiedName))
      }
      if (identified.isEmpty) report.errorAndAbort(s"$name has no protocol operations")
      identified.groupBy(_._3).collectFirst { case collision @ (_, cases) if cases.size > 1 => collision } match {
        case Some((id, collisions)) =>
          val names = collisions.map(_._2).mkString(", ")
          report.errorAndAbort(s"$name message ID collision ${java.lang.Long.toUnsignedString(id, 16)}: $names")
        case None => ()
      }
      val cases = identified.map { case (child, _, id) =>
        val singleton = child.flags.is(Flags.Module) || child.flags.is(Flags.StableRealizable)
        val childType =
          if (singleton) child.termRef.widen
          else child.typeRef
        val base  = childType.baseType(operationSymbol)
        val reply = base.typeArgs match {
          case value :: Nil => value
          case _            => report.errorAndAbort(s"${child.fullName} does not have one reply type")
        }
        reply.asType match {
          case '[answer] if singleton =>
            val replyCodec = Expr.summon[Codec[answer]].getOrElse(
              report.errorAndAbort(s"no wire codec for reply ${reply.show} of ${child.fullName}")
            )
            val value = Ref(child).asExprOf[F[answer]]
            val codec = '{
              Codec.instance[F[answer]](
                Shape.Product(${ Expr(child.name) }, Vector.empty),
                (_, _) => (),
                _ => Right($value)
              )
            }
            '{
              Protocol.protocolCase[F, answer, F[answer]](
                ${ Expr(child.name) },
                ${ Expr(id) },
                $codec,
                $replyCodec.shape
              )
            }
          case '[answer] => childType.asType match {
              case '[child] =>
                val replyCodec = Expr.summon[Codec[answer]].getOrElse(
                  report.errorAndAbort(s"no wire codec for reply ${reply.show} of ${child.fullName}")
                )
                val mirror = Expr.summon[Mirror.ProductOf[child]].getOrElse(
                  report.errorAndAbort(s"cannot derive a wire codec for ${child.fullName}")
                )
                val codec = '{ Codec.derived[child](using $mirror) }
                '{
                  Protocol.protocolCase[F, answer, child](
                    ${ Expr(child.name) },
                    ${ Expr(id) },
                    $codec,
                    $replyCodec.shape
                  )
                }
            }
        }
      }
      '{ Protocol.runtime[F](${ Expr(name) }, Vector(${ Expr.ofSeq(cases) }*).assume[MinLength[1]]) }
    }

    private final class DecodedValue[F[_], A](override val value: F[A]) extends Decoded[F] {
      override type Reply = A
    }

    private final class ProtocolImpl[F[_]](
        name: String,
        cases: Vector[ProtocolCase[F]] :| MinLength[1]
    ) extends Protocol[F] {
      override val operations: Vector[OperationShape] :| MinLength[1] =
        cases.map(value => OperationShape(Alternative(value.name, value.id, value.codec.shape), value.reply))
          .refineUnsafe[MinLength[1]]

      override val shape: Shape.Sum = Shape.Sum(name, operations.map(_.request))

      override def encodePayload[A](
          value: F[A],
          limits: Limits
      ): Either[String, (Long, Array[Byte])] = {
        val sumCase = cases(value.asInstanceOf[scala.reflect.Enum].ordinal)
        val writer  = Writer(limits)
        sumCase.codec.write(writer, value)
        writer.result().map(sumCase.id -> _)
      }

      override def decodePayload(
          id: Long,
          bytes: Array[Byte],
          limits: Limits
      ): Either[String, Decoded[F]] = {
        val reader = Reader(bytes, limits)
        readCase(id, reader).flatMap { value =>
          Either.cond(
            reader.remaining == 0,
            value,
            s"wire value has ${reader.remaining} trailing bytes"
          )
        }
      }

      private def readCase(id: Long, reader: Reader): Either[String, Decoded[F]] =
        cases.find(_.id == id)
          .toRight(s"unknown $name message ID: ${java.lang.Long.toUnsignedString(id, 16)}")
          .flatMap(_.decode(reader))
    }

    private[Wire] final class ProtocolCase[F[_]](
        val name: String,
        val id: Long,
        val codec: Codec[Any],
        val reply: Shape,
        val decode: Reader => Either[String, Decoded[F]]
    )

    @publicInBinary
    private[Wire] def runtime[F[_]](
        name: String,
        cases: Vector[ProtocolCase[F]] :| MinLength[1]
    ): Protocol[F] = ProtocolImpl(name, cases)

    @publicInBinary
    private[Wire] def protocolCase[F[_], A, C](
        name: String,
        id: Long,
        codec: Codec[C],
        reply: Shape
    ): ProtocolCase[F] = ProtocolCase(
      name,
      id,
      codec.asInstanceOf[Codec[Any]],
      reply,
      reader =>
        codec.read(reader).map(decoded => DecodedValue[F, A](decoded.asInstanceOf[F[A]]))
    )
  }

  sealed trait Decoded[F[_]] {
    type Reply
    val value: F[Reply]
  }

  object Codec {
    trait ConstraintShape[A, C] {
      def values: Vector[Constraint]
    }

    object ConstraintShape {
      given ConstraintShape[Int, Positive] with {
        override val values: Vector[Constraint] = Vector(Constraint.Positive)
      }

      given ConstraintShape[Int, Positive0] with {
        override val values: Vector[Constraint] = Vector(Constraint.Positive0)
      }

      given ConstraintShape[Long, Positive] with {
        override val values: Vector[Constraint] = Vector(Constraint.Positive)
      }

      given ConstraintShape[Long, Positive0] with {
        override val values: Vector[Constraint] = Vector(Constraint.Positive0)
      }

      given [A]: ConstraintShape[A, Pure] with {
        override val values: Vector[Constraint] = Vector.empty
      }

      given intInterval[Minimum <: Int & Singleton, Maximum <: Int & Singleton](using
          minimum: ValueOf[Minimum],
          maximum: ValueOf[Maximum]
      ): ConstraintShape[Int, Interval.Closed[Minimum, Maximum]] with {
        override val values: Vector[Constraint] =
          Vector(Constraint.IntInterval(minimum.value, maximum.value))
      }

      given longInterval[Minimum <: Long & Singleton, Maximum <: Long & Singleton](using
          minimum: ValueOf[Minimum],
          maximum: ValueOf[Maximum]
      ): ConstraintShape[Long, Interval.Closed[Minimum, Maximum]] with {
        override val values: Vector[Constraint] =
          Vector(Constraint.LongInterval(minimum.value, maximum.value))
      }

      given minimumLength[Minimum <: Int & Singleton](using
          minimum: ValueOf[Minimum]
      ): ConstraintShape[String, MinLength[Minimum]] with {
        override val values: Vector[Constraint] = Vector(Constraint.MinimumLength(minimum.value))
      }

      given notContains[Value <: String & Singleton](using
          value: ValueOf[Value]
      ): ConstraintShape[String, Not[Contain[Value]]] with {
        override val values: Vector[Constraint] = Vector(Constraint.NotContains(value.value))
      }

      given noNul: ConstraintShape[String, NoNul] with {
        override val values: Vector[Constraint] = Vector(Constraint.NotContains("\u0000"))
      }

      given minimumLengthNoNul[Minimum <: Int & Singleton](using
          minimum: ValueOf[Minimum]
      ): ConstraintShape[String, MinLength[Minimum] & NoNul] with {
        override val values: Vector[Constraint] =
          Vector(Constraint.MinimumLength(minimum.value), Constraint.NotContains("\u0000"))
      }

      given intersection[A, Left, Right](using
          left: ConstraintShape[A, Left],
          right: ConstraintShape[A, Right]
      ): ConstraintShape[A, Left & Right] with {
        override val values: Vector[Constraint] = left.values ++ right.values
      }
    }

    private final class CodecImpl[A](
        valueShape: Shape,
        writeValue: (Writer, A) => Unit,
        readValue: Reader => Either[String, A]
    ) extends Codec[A] {
      override val shape: Shape                                         = valueShape
      override private[rpc] def write(writer: Writer, value: A): Unit   = writeValue(writer, value)
      override private[rpc] def read(reader: Reader): Either[String, A] = readValue(reader)
    }

    def apply[A](using codec: Codec[A]): Codec[A] = codec

    def instance[A](
        valueShape: Shape,
        writeValue: (Writer, A) => Unit,
        readValue: Reader => Either[String, A]
    ): Codec[A] = CodecImpl(valueShape, writeValue, readValue)

    def validated[A](underlying: Codec[A])(
        validate: A => Either[String, A]
    ): Codec[A] =
      instance(
        underlying.shape,
        (writer, value) => underlying.write(writer, value),
        reader => underlying.read(reader).flatMap(value => validate(value))
      )

    given Codec[Unit] = instance(Shape.UnitValue, (_, _) => (), _ => Right(()))

    given Codec[Boolean] = instance(
      Shape.BooleanValue,
      (writer, value) => writer.writeByte(if (value) 1 else 0),
      _.readByte().flatMap {
        case 0     => Right(false)
        case 1     => Right(true)
        case value => Left(s"invalid Boolean value: $value")
      }
    )

    given Codec[Byte] = instance(Shape.Int8, _.writeByte(_), _.readByte())
    given Codec[Int]  = instance(Shape.Int32, _.writeInt(_), _.readInt())
    given Codec[Long] = instance(Shape.Int64, _.writeLong(_), _.readLong())

    given positiveInt: Codec[Int :| Positive]     = refinedInt[Positive](Vector(Constraint.Positive))
    given nonNegativeInt: Codec[Int :| Positive0] = refinedInt[Positive0](Vector(Constraint.Positive0))
    inline given intInterval[Minimum <: Int & Singleton, Maximum <: Int & Singleton](using
        runtime: RuntimeConstraint[Int, Interval.Closed[Minimum, Maximum]],
        minimum: ValueOf[Minimum],
        maximum: ValueOf[Maximum]
    ): Codec[Int :| Interval.Closed[Minimum, Maximum]] =
      refinedInt[Interval.Closed[Minimum, Maximum]](
        Vector(Constraint.IntInterval(minimum.value, maximum.value))
      )(using runtime)

    given positiveLong: Codec[Long :| Positive]     = refinedLong[Positive](Vector(Constraint.Positive))
    given nonNegativeLong: Codec[Long :| Positive0] = refinedLong[Positive0](Vector(Constraint.Positive0))
    inline given longInterval[Minimum <: Long & Singleton, Maximum <: Long & Singleton](using
        runtime: RuntimeConstraint[Long, Interval.Closed[Minimum, Maximum]],
        minimum: ValueOf[Minimum],
        maximum: ValueOf[Maximum]
    ): Codec[Long :| Interval.Closed[Minimum, Maximum]] =
      refinedLong[Interval.Closed[Minimum, Maximum]](
        Vector(Constraint.LongInterval(minimum.value, maximum.value))
      )(using runtime)

    inline given minimumLength[Minimum <: Int & Singleton](using
        runtime: RuntimeConstraint[String, MinLength[Minimum]],
        minimum: ValueOf[Minimum]
    ): Codec[String :| MinLength[Minimum]] =
      refinedString[MinLength[Minimum]](Vector(Constraint.MinimumLength(minimum.value)))(using runtime)

    inline given refinedType[A](using mirror: RefinedType.Mirror[A]): Codec[A] = {
      val base        = summonInline[Codec[mirror.BaseType]].asInstanceOf[Codec[Any]]
      val constraints = summonInline[ConstraintShape[mirror.BaseType, mirror.ConstraintType]]
      instance(
        Shape.Alias(refinedTypeName[A], base.shape, constraints.values),
        (writer, value) => base.write(writer, value),
        reader =>
          base.read(reader).flatMap(value =>
            mirror.ops.either(value.asInstanceOf[mirror.BaseType])
              .left.map(message => s"invalid refined value: $message")
              .map(_.asInstanceOf[A])
          )
      )
    }

    given Codec[String] = instance(
      Shape.Text,
      (writer, value) => {
        val bytes = value.getBytes(StandardCharsets.UTF_8)
        if bytes.length > writer.limits.maximumTextBytes then
          writer.reject(
            s"UTF-8 value exceeds the ${writer.limits.maximumTextBytes}-byte text limit"
          )
        else {
          writer.writeInt(bytes.length)
          writer.writeBytes(bytes)
        }
      },
      reader =>
        for {
          size  <- reader.readCount("UTF-8 byte", reader.limits.maximumTextBytes)
          bytes <- reader.readBytes(size)
        } yield String(bytes, StandardCharsets.UTF_8)
    )

    given Codec[Vec[Byte]] = instance(
      Shape.Sequence(Shape.Int8),
      (writer, values) =>
        if values.length > writer.limits.maximumBytes then
          writer.reject(
            s"byte collection exceeds the ${writer.limits.maximumBytes}-byte limit"
          )
        else {
          writer.writeInt(values.length)
          writer.writeBytes(Vec.toArray(values))
        },
      reader =>
        for {
          count <- reader.readCount("byte", reader.limits.maximumBytes)
          bytes <- reader.readBytes(count)
        } yield Vec.unsafeFromArray(bytes)
    )

    given [A: ClassTag](using element: Codec[A], notByte: NotGiven[A =:= Byte]): Codec[Vec[A]] = instance(
      Shape.Sequence(element.shape),
      (writer, values) =>
        if values.length > writer.limits.maximumElements then
          writer.reject(
            s"collection exceeds the ${writer.limits.maximumElements}-element limit"
          )
        else {
          writer.writeInt(values.length)
          values.foreach(element.write(writer, _))
        },
      reader =>
        reader.readCount("collection element", reader.limits.maximumElements) match {
          case Left(error)  => Left(error)
          case Right(count) =>
            val mutableBuilder = Vec.newBuilder[A]
            @scala.annotation.tailrec
            def read(index: Int): Either[String, Vec[A]] =
              if (index == count) Right(mutableBuilder.result())
              else
                element.read(reader) match {
                  case Left(error)  => Left(error)
                  case Right(value) =>
                    val _ = mutableBuilder += value
                    read(index + 1)
                }
            read(0)
        }
    )

    given [A](using element: Codec[A]): Codec[Option[A]] = instance(
      Shape.Optional(element.shape),
      (writer, value) =>
        value match {
          case Some(found) => writer.writeByte(1); element.write(writer, found)
          case None        => writer.writeByte(0)
        },
      reader =>
        reader.readByte().flatMap {
          case 0     => Right(None)
          case 1     => element.read(reader).map(Some(_))
          case value => Left(s"invalid optional value tag: $value")
        }
    )

    inline def derived[A](using mirror: Mirror.Of[A]): Codec[A] = inline mirror match {
      case product: Mirror.ProductOf[A] => productCodec(product)
      case sum: Mirror.SumOf[A]         => sumCodec(sum)
    }

    private inline def productCodec[A](mirror: Mirror.ProductOf[A]): Codec[A] = {
      val labels = labelsOf[mirror.MirroredElemLabels]
      val codecs = codecsOf[mirror.MirroredElemTypes]
      val name   = constValue[mirror.MirroredLabel].toString
      instance(
        Shape.Product(name, labels.zip(codecs.map(_.shape)).map { case (label, codec) => Field(label, codec) }),
        (writer, value) => {
          val product = value.asInstanceOf[Product]
          codecs.indices.foreach { index =>
            codecs(index).asInstanceOf[Codec[Any]].write(writer, product.productElement(index))
          }
        },
        reader => {
          val mutableValues = Array.ofDim[Any](codecs.length)
          @scala.annotation.tailrec
          def loop(index: Int): Either[String, A] =
            if (index == codecs.length) Right(mirror.fromProduct(Tuple.fromArray(mutableValues)))
            else
              codecs(index).asInstanceOf[Codec[Any]].read(reader) match {
                case Left(error)  => Left(error)
                case Right(value) =>
                  mutableValues(index) = value
                  loop(index + 1)
              }
          loop(0)
        }
      )
    }

    private inline def refinedInt[C](shapeConstraints: Vector[Constraint])(using
        constraint: RuntimeConstraint[Int, C]
    ): Codec[Int :| C] =
      instance(
        Shape.Alias("Int", Shape.Int32, shapeConstraints),
        (writer, value) => writer.writeInt(value),
        reader =>
          reader.readInt().flatMap(value =>
            value.refineEither[C].left.map(message => s"invalid refined value: $message")
          )
      )

    private inline def refinedLong[C](shapeConstraints: Vector[Constraint])(using
        constraint: RuntimeConstraint[Long, C]
    ): Codec[Long :| C] =
      instance(
        Shape.Alias("Long", Shape.Int64, shapeConstraints),
        (writer, value) => writer.writeLong(value),
        reader =>
          reader.readLong().flatMap(value =>
            value.refineEither[C].left.map(message => s"invalid refined value: $message")
          )
      )

    private inline def refinedString[C](shapeConstraints: Vector[Constraint])(using
        constraint: RuntimeConstraint[String, C]
    ): Codec[String :| C] =
      instance(
        Shape.Alias("String", Shape.Text, shapeConstraints),
        (writer, value) => summonInline[Codec[String]].write(writer, value),
        reader =>
          summonInline[Codec[String]].read(reader).flatMap(value =>
            value.refineEither[C].left.map(message => s"invalid refined value: $message")
          )
      )

    @publicInBinary
    private[Codec] inline def refinedTypeName[A]: String = ${ refinedTypeNameImpl[A] }

    private def refinedTypeNameImpl[A: Type](using Quotes): Expr[String] = {
      val parts = Type.show[A].split('.')
      val name  = if (parts.lastOption.contains("T")) parts(parts.length - 2) else parts.last
      Expr(name)
    }

    private inline def sumCodec[A](mirror: Mirror.SumOf[A]): Codec[A] = {
      val name  = constValue[mirror.MirroredLabel].toString
      val cases = casesOf[mirror.MirroredElemTypes](name)
      sumCodecRuntime(name, cases, mirror.ordinal)
    }

    @publicInBinary
    private[Codec] def sumCodecRuntime[A](
        name: String,
        cases: Vector[(String, Long, Codec[Any])],
        ordinal: A => Int
    ): Codec[A] = {
      cases.groupBy(_._2).collectFirst { case (id, collisions) if collisions.size > 1 => id -> collisions } match {
        case Some((id, collisions)) =>
          val names = collisions.map(_._1).mkString(", ")
          throw IllegalArgumentException(
            s"$name message ID collision ${java.lang.Long.toUnsignedString(id, 16)}: $names"
          )
        case None => ()
      }
      instance(
        Shape.Sum(name, cases.map(value => Alternative(value._1, value._2, value._3.shape))),
        (writer, value) => {
          val sumCase = cases(ordinal(value))
          writer.writeLong(sumCase._2)
          sumCase._3.write(writer, value)
        },
        reader =>
          reader.readLong().flatMap { id =>
            cases.find(_._2 == id)
              .toRight(s"unknown $name message ID: ${java.lang.Long.toUnsignedString(id, 16)}")
              .flatMap(_._3.read(reader).map(_.asInstanceOf[A]))
          }
      )
    }

    private inline def labelsOf[Labels <: Tuple]: Vector[String] = inline erasedValue[Labels] match {
      case _: EmptyTuple        => Vector.empty
      case _: (label *: labels) => constValue[label].toString +: labelsOf[labels]
    }

    private inline def codecsOf[Types <: Tuple]: Vector[Codec[?]] = inline erasedValue[Types] match {
      case _: EmptyTuple     => Vector.empty
      case _: (head *: tail) => summonInline[Codec[head]] +: codecsOf[tail]
    }

    private inline def casesOf[Types <: Tuple](sumName: String): Vector[(String, Long, Codec[Any])] =
      inline erasedValue[Types] match {
        case _: EmptyTuple     => Vector.empty
        case _: (head *: tail) =>
          val codec = derived[head](using summonInline[Mirror.Of[head]])
          val name  = codec.shape match {
            case Shape.Product(value, _) => value
            case value                   => throw IllegalArgumentException(s"sum case is not a product: $value")
          }
          (name, hash64(s"$sumName.$name"), codec.asInstanceOf[Codec[Any]]) +: casesOf[tail](sumName)
      }
  }

  final case class OperationShape(request: Alternative, reply: Shape)
  final case class ProtocolShape(
      name: String :| (MinLength[1] & NoNul),
      operations: Vector[OperationShape] :| MinLength[1],
      failure: Shape
  ) {
    val canonical: String :| MinLength[1] = {
      def render(shape: Shape): String = shape match {
        case Shape.UnitValue                        => "unit"
        case Shape.BooleanValue                     => "bool"
        case Shape.Int8                             => "i8"
        case Shape.Int32                            => "i32"
        case Shape.Int64                            => "i64"
        case Shape.Text                             => "text"
        case Shape.Sequence(element)                => s"seq[${render(element)}]"
        case Shape.Optional(element)                => s"option[${render(element)}]"
        case Shape.Alias(alias, value, constraints) =>
          val rendered = constraints.map {
            case Constraint.Positive                       => "positive"
            case Constraint.Positive0                      => "positive0"
            case Constraint.IntInterval(minimum, maximum)  => s"int-interval[$minimum,$maximum]"
            case Constraint.LongInterval(minimum, maximum) => s"long-interval[$minimum,$maximum]"
            case Constraint.MinimumLength(minimum)         => s"minimum-length[$minimum]"
            case Constraint.NotContains(value)             =>
              val encoded = value.getBytes(StandardCharsets.UTF_8).map(byte => f"${byte & 0xff}%02x").mkString
              s"not-contains[$encoded]"
          }.mkString(",")
          s"alias($alias,${render(value)})[$rendered]"
        case Shape.Product(product, fields) =>
          fields.map(field => s"${field.name}:${render(field.shape)}").mkString(s"product($product){", ",", "}")
        case Shape.Sum(sum, alternatives) =>
          alternatives.map(alternative =>
            s"${alternative.name}@${java.lang.Long.toUnsignedString(alternative.id, 16)}:${render(alternative.shape)}"
          ).mkString(s"sum($sum){", ",", "}")
      }
      operations.map(operation =>
        s"${operation.request.name}@${java.lang.Long.toUnsignedString(operation.request.id, 16)}:${render(operation.request.shape)}=>${render(operation.reply)}"
      ).mkString(s"protocol($name){", ",", s"};failure=${render(failure)}").refineUnsafe[MinLength[1]]
    }

    val fingerprint: Long = hash64(canonical)
  }

  def hash64(value: String): Long = {
    val bytes = value.getBytes(StandardCharsets.UTF_8)
    bytes.foldLeft(0xcbf29ce484222325L) { (hash, byte) =>
      (hash ^ (byte & 0xffL)) * 0x100000001b3L
    }
  }
}
