package net.kurobako.gemstone.rpc

import java.nio.charset.StandardCharsets
import net.kurobako.gemstone.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import scala.annotation.publicInBinary
import scala.compiletime.constValue
import scala.compiletime.erasedValue
import scala.compiletime.summonInline
import scala.deriving.Mirror
import scala.quoted.*
import scala.reflect.ClassTag
import scala.util.NotGiven

object Wire {
  private val InitialCapacity = 256

  case class Limits(
      maximumElements: Int > 0,
      maximumBytes: Int > 0,
      maximumTextBytes: Int > 0
  )

  enum Constraint {
    case Positive
    case Positive0
    case IntInterval(minimum: Int, maximum: Int)
    case LongInterval(minimum: Long, maximum: Long)
    case OneOf(values: Vector[Long])
    case MultipleOf(divisor: Long)
    case MinimumLength(minimum: Int)
    case NotContains(value: String)
  }

  enum Shape {
    case UnitValue
    case BooleanValue
    case Int8
    case Int16
    case Int32
    case Int64
    case Float32
    case Float64
    case Text
    case Sequence(element: Shape)
    case Optional(element: Shape)
    case Product(name: String, fields: Vector[Field])
    case Sum(name: String, alternatives: Vector[Alternative])
    case Alias(name: String, underlying: Shape, constraints: Vector[Constraint])
  }

  case class Field(name: String, shape: Shape)
  case class Alternative(name: String, shape: Shape)

  private class Writer(val limits: Limits) {
    @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
    private var mutableBytes = Array.ofDim[Byte](math.min(InitialCapacity, limits.maximumBytes.raw))
    @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
    private var length = 0
    @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
    private var failure = Option.empty[String]

    private def emit(value: Int): Unit = {
      mutableBytes(length) = value.toByte
      length += 1
    }

    private def reserve(additional: Int): Boolean =
      if (failure.nonEmpty) false
      else if (additional < 0 || additional > limits.maximumBytes.raw - length) {
        reject(s"wire value exceeds the ${limits.maximumBytes}-byte limit")
        false
      } else {
        val required = length + additional
        if (required > mutableBytes.length) {
          val doubled = math.min(limits.maximumBytes.raw.toLong, math.max(1L, mutableBytes.length.toLong * 2L)).toInt
          mutableBytes = java.util.Arrays.copyOf(mutableBytes, math.max(required, doubled))
        }
        true
      }

    private def writeRawByte(value: Int): Unit =
      if (reserve(1)) emit(value)

    private def writeRawInt(value: Int): Unit =
      if (reserve(4)) {
        emit(value >>> 24)
        emit(value >>> 16)
        emit(value >>> 8)
        emit(value)
      }

    private def writeRawLong(value: Long): Unit = {
      writeRawInt((value >>> 32).toInt)
      writeRawInt(value.toInt)
    }

    private def writeRawBytes(value: Array[Byte]): Unit =
      if (reserve(value.length)) {
        Array.copy(value, 0, mutableBytes, length, value.length)
        length += value.length
      }

    def writeNil(): Unit = writeRawByte(0xc0)

    def writeBoolean(value: Boolean): Unit = writeRawByte(if (value) 0xc3 else 0xc2)

    def writeInt(value: Int): Unit =
      if (value >= 0 && value <= 0x7f) writeRawByte(value)
      else if (value < 0 && value >= -32) writeRawByte(value & 0xff)
      else if (value >= Byte.MinValue && value <= Byte.MaxValue) {
        writeRawByte(0xd0)
        writeRawByte(value)
      } else if (value >= Short.MinValue && value <= Short.MaxValue) {
        writeRawByte(0xd1)
        writeRawByte(value >>> 8)
        writeRawByte(value)
      } else {
        writeRawByte(0xd2)
        writeRawInt(value)
      }

    def writeFloat(value: Float): Unit = {
      writeRawByte(0xca)
      writeRawInt(java.lang.Float.floatToRawIntBits(value))
    }
    def writeDouble(value: Double): Unit = {
      writeRawByte(0xcb)
      writeRawLong(java.lang.Double.doubleToRawLongBits(value))
    }

    def writeLong(value: Long): Unit =
      if (value >= Int.MinValue && value <= Int.MaxValue) writeInt(value.toInt)
      else {
        writeRawByte(0xd3)
        writeRawLong(value)
      }

    private def writeStringHeader(size: Int): Unit =
      if (size <= 31) writeRawByte(0xa0 | size)
      else if (size <= 0xff) {
        writeRawByte(0xd9)
        writeRawByte(size)
      } else if (size <= 0xffff) {
        writeRawByte(0xda)
        writeRawByte(size >>> 8)
        writeRawByte(size)
      } else {
        writeRawByte(0xdb)
        writeRawInt(size)
      }

    private def writeBinHeader(size: Int): Unit =
      if (size <= 0xff) {
        writeRawByte(0xc4)
        writeRawByte(size)
      } else if (size <= 0xffff) {
        writeRawByte(0xc5)
        writeRawByte(size >>> 8)
        writeRawByte(size)
      } else {
        writeRawByte(0xc6)
        writeRawInt(size)
      }

    def writeArrayHeader(size: Int): Unit =
      if (size < 0) reject(s"negative array size: $size")
      else if (size <= 15) writeRawByte(0x90 | size)
      else if (size <= 0xffff) {
        writeRawByte(0xdc)
        writeRawByte(size >>> 8)
        writeRawByte(size)
      } else {
        writeRawByte(0xdd)
        writeRawInt(size)
      }

    def writeString(value: String): Unit = {
      val bytes = value.getBytes(StandardCharsets.UTF_8)
      if (bytes.length > limits.maximumTextBytes.raw)
        reject(s"UTF-8 value exceeds the ${limits.maximumTextBytes}-byte text limit")
      else {
        writeStringHeader(bytes.length)
        writeRawBytes(bytes)
      }
    }

    def writeBytes(value: Array[Byte]): Unit =
      if (value.length > limits.maximumBytes.raw)
        reject(s"byte collection exceeds the ${limits.maximumBytes}-byte limit")
      else {
        writeBinHeader(value.length)
        writeRawBytes(value)
      }

    def reject(message: String): Unit = if (failure.isEmpty) failure = Some(message)

    def result(): Either[String, Array[Byte]] =
      failure.toLeft(java.util.Arrays.copyOf(mutableBytes, length))
  }

  private object Reader {
    def decode[A](bytes: Array[Byte], limits: Limits)(read: Reader => Either[String, A]): Either[String, A] =
      if (bytes.length > limits.maximumBytes.raw) Left(s"wire value exceeds the ${limits.maximumBytes}-byte limit")
      else {
        val reader = Reader(bytes, limits)
        read(reader).flatMap { value =>
          Either.cond(reader.remaining == 0, value, s"wire value has ${reader.remaining} trailing bytes")
        }
      }
  }

  private class Reader(bytes: Array[Byte], val limits: Limits)
      extends msgpack.Reader(
        bytes = bytes,
        start = 0,
        size = bytes.length,
        maximumTextBytes = limits.maximumTextBytes.raw,
        maximumBytes = limits.maximumBytes.raw
      )

  object Codec {
    private class CodecImpl[A](
        valueShape: Shape,
        writeValue: (Writer, A) => Unit,
        readValue: Reader => Either[String, A]
    ) extends Codec[A] {
      override val shape: Shape                                          = valueShape
      override private[Wire] def write(writer: Writer, value: A): Unit   = writeValue(writer, value)
      override private[Wire] def read(reader: Reader): Either[String, A] = readValue(reader)
    }

    def apply[A](using codec: Codec[A]): Codec[A] = codec

    @publicInBinary
    private[Wire] def instance[A](
        valueShape: Shape,
        writeValue: (Writer, A) => Unit,
        readValue: Reader => Either[String, A]
    ): Codec[A] = CodecImpl(valueShape, writeValue, readValue)

    def validated[A](underlying: Codec[A])(
        validate: A => Either[String, A]
    ): Codec[A] =
      instance(
        underlying.shape,
        (writer, value) =>
          validate(value) match {
            case Left(error)    => writer.reject(error)
            case Right(checked) => underlying.write(writer, checked)
          },
        reader => underlying.read(reader).flatMap(value => validate(value))
      )

    given Codec[Unit] = instance(
      Shape.UnitValue,
      (writer, _) => writer.writeArrayHeader(0),
      reader =>
        reader.readArrayHeader().flatMap { count =>
          Either.cond(count == 0, (), s"expected unit array of size 0, got $count")
        }
    )

    given Codec[Boolean] = instance(Shape.BooleanValue, (writer, value) => writer.writeBoolean(value), _.readBoolean())

    given Codec[Byte] = instance(
      Shape.Int8,
      (writer, value) => writer.writeInt(value.toInt),
      reader =>
        reader.readInt().flatMap { value =>
          if (value < Byte.MinValue || value > Byte.MaxValue) Left(s"integer out of Byte range: $value")
          else Right(value.toByte)
        }
    )
    given Codec[Short] = instance(
      Shape.Int16,
      (writer, value) => writer.writeInt(value.toInt),
      reader =>
        reader.readInt().flatMap { value =>
          if (value < Short.MinValue || value > Short.MaxValue) Left(s"integer out of Short range: $value")
          else Right(value.toShort)
        }
    )
    given Codec[Int]    = instance(Shape.Int32, (writer, value) => writer.writeInt(value), _.readInt())
    given Codec[Float]  = instance(Shape.Float32, (writer, value) => writer.writeFloat(value), _.readFloat())
    given Codec[Double] = instance(Shape.Float64, (writer, value) => writer.writeDouble(value), _.readDouble())
    given Codec[Long]   = instance(Shape.Int64, (writer, value) => writer.writeLong(value), _.readLong())

    /** A codec for a domain type backed by a primitive base, carrying the wire constraints of the original refinement.
      */
    def domain[A, B](name: String, base: Codec[B], constraints: Vector[Constraint])(
        embed: B => Either[String, A],
        extract: A => B
    ): Codec[A] =
      mapped(Shape.Alias(name, base.shape, constraints), base)(embed, extract)

    def mapped[A, B](shape: Shape, base: Codec[B])(
        embed: B => Either[String, A],
        extract: A => B
    ): Codec[A] = instance(
      shape,
      (writer, value) =>
        embed(extract(value)) match {
          case Left(error)    => writer.reject(error)
          case Right(checked) => base.write(writer, extract(checked))
        },
      reader => base.read(reader).flatMap(embed)
    )

    inline given refined[A, P](using base: Codec[A]): Codec[A where P] =
      ${ WireCodecMacros.refined[A, P]('base) }

    // Stryker rewrites the macro entry point to isEmpty, then leaves an invalid splice after rollback.
    @SuppressWarnings(Array("stryker4s.mutation.MethodExpression"))
    inline given nonEmpty[A](using base: Codec[A], elements: HasElements[A]): Codec[NonEmpty[A]] =
      ${ WireCodecMacros.nonEmpty[A]('base, 'elements) }

    inline given newtype[A, C](using representation: Newtype.Representation[A, C], base: Codec[C]): Codec[A] =
      ${ WireCodecMacros.newtype[A, C]('representation, 'base) }

    given Codec[String] = instance(Shape.Text, (writer, value) => writer.writeString(value), _.readString())

    given Codec[Vec[Byte]] = instance(
      Shape.Sequence(Shape.Int8),
      (writer, values) => writer.writeBytes(Vec.toArray(values)),
      reader => reader.readBytes().map(Vec.unsafeWrapArray)
    )

    given [A: ClassTag](using element: Codec[A], notByte: NotGiven[A =:= Byte]): Codec[Vec[A]] = instance(
      Shape.Sequence(element.shape),
      (writer, values) =>
        if (values.length > writer.limits.maximumElements.raw)
          writer.reject(
            s"collection exceeds the ${writer.limits.maximumElements}-element limit"
          )
        else {
          writer.writeArrayHeader(values.length)
          values.foreach(element.write(writer, _))
        },
      reader =>
        reader.readArrayHeader().flatMap { count =>
          if (count > reader.limits.maximumElements.raw) Left(
            s"collection exceeds the ${reader.limits.maximumElements}-element limit: $count"
          )
          else {
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
        }
    )

    // Flipping a boolean pattern creates duplicate cases, which cannot compile with our match checks.
    @SuppressWarnings(Array("stryker4s.mutation.BooleanLiteral"))
    given [A](using element: Codec[A]): Codec[Option[A]] = instance(
      Shape.Optional(element.shape),
      (writer, value) =>
        value match {
          case Some(found) => element.write(writer, found)
          case None        => writer.writeNil()
        },
      reader =>
        reader.tryReadNil().flatMap {
          case true  => Right(None)
          case false => element.read(reader).map(Some(_))
        }
    )

    inline def derived[A](using mirror: Mirror.Of[A]): Codec[A] = inline mirror match {
      case product: Mirror.ProductOf[A] => productCodec(product)
      case sum: Mirror.SumOf[A]         => sumCodec(sum)
    }

    /** Derive a product while keeping its stable wire name independent of its Scala name. */
    inline def product[A](name: String)(using mirror: Mirror.ProductOf[A]): Codec[A] =
      productCodecRuntime(name, labelsOf[mirror.MirroredElemLabels], codecsOf[mirror.MirroredElemTypes], mirror)

    private inline def productCodec[A](mirror: Mirror.ProductOf[A]): Codec[A] = {
      val labels = labelsOf[mirror.MirroredElemLabels]
      val codecs = codecsOf[mirror.MirroredElemTypes]
      val name   = constValue[mirror.MirroredLabel]
      productCodecRuntime(name, labels, codecs, mirror)
    }

    // Generic derivation pairs each product field with its statically derived codec.
    @SuppressWarnings(Array("scalafix:GemstoneNoProductTraversal"))
    @publicInBinary
    private[Codec] def productCodecRuntime[A](
        name: String,
        labels: Vector[String],
        codecs: Vector[Codec[?]],
        mirror: Mirror.ProductOf[A]
    ): Codec[A] =
      instance(
        Shape.Product(name, labels.zip(codecs.map(_.shape)).map { case (label, codec) => Field(label, codec) }),
        (writer, value) =>
          value match {
            case product: Product =>
              writer.writeArrayHeader(labels.length)
              codecs.indices.foreach { index =>
                codecs(index).erased.write(writer, product.productElement(index))
              }
            case _ => writer.reject(s"$name value is not a product")
          },
        reader =>
          reader.readArrayHeader().flatMap { count =>
            if (count != labels.length) Left(s"expected $name product array of size ${labels.length}, got $count")
            else {
              val mutableValues = Array.ofDim[Any](codecs.length)
              @scala.annotation.tailrec
              def loop(index: Int): Either[String, A] =
                if (index == codecs.length) Right(mirror.fromProduct(Tuple.fromArray(mutableValues)))
                else
                  codecs(index).erased.read(reader) match {
                    case Left(error)  => Left(error)
                    case Right(value) =>
                      mutableValues(index) = value
                      loop(index + 1)
                  }
              loop(0)
            }
          }
      )

    private inline def sumCodec[A](mirror: Mirror.SumOf[A]): Codec[A] = {
      val name  = constValue[mirror.MirroredLabel]
      val cases = casesOf[mirror.MirroredElemTypes]
      sumCodecRuntime(name, cases, mirror.ordinal)
    }

    @publicInBinary
    private[Codec] def sumCodecRuntime[A](
        name: String,
        cases: Vector[(String, Codec[Any])],
        ordinal: A => Int
    ): Codec[A] = {
      val allNullary = cases.forall { case (_, codec) =>
        codec.shape match {
          case Shape.Product(_, fields) => fields.isEmpty
          case _                        => false
        }
      }
      instance(
        Shape.Sum(name, cases.map(value => Alternative(value._1, value._2.shape))),
        (writer, value) =>
          if (allNullary) writer.writeInt(ordinal(value))
          else {
            val (caseName, codec) = cases(ordinal(value))
            writer.writeArrayHeader(2)
            writer.writeString(caseName)
            codec.write(writer, value)
          },
        reader =>
          if (allNullary)
            reader.readInt().flatMap { index =>
              if (index < 0 || index >= cases.length) Left(s"unknown $name ordinal: $index")
              else cases(index)._2.read(Reader(Array(0x90.toByte), NullaryLimits)).map { case value: A @unchecked =>
                value
              }
            }
          else
            reader.readArrayHeader().flatMap { count =>
              if (count != 2) Left(s"expected $name sum array of size 2, got $count")
              else
                reader.readString().flatMap { tag =>
                  cases.find(_._1 == tag)
                    .toRight(s"unknown $name sum tag: $tag")
                    .flatMap(_._2.read(reader).map { case value: A @unchecked => value })
                }
            }
      )
    }

    private val NullaryLimits: Limits = Limits(1, 1, 1)

    private inline def labelsOf[Labels <: Tuple]: Vector[String] = inline erasedValue[Labels] match {
      case _: EmptyTuple        => Vector.empty
      case _: (label *: labels) => constValue[label].toString +: labelsOf[labels]
    }

    private inline def codecsOf[Types <: Tuple]: Vector[Codec[?]] = inline erasedValue[Types] match {
      case _: EmptyTuple     => Vector.empty
      case _: (head *: tail) => summonInline[Codec[head]] +: codecsOf[tail]
    }

    private type Half[T <: Tuple] = scala.compiletime.ops.int./[Tuple.Size[T], 2]

    // Mutation branches duplicate recursive inline expansion beyond the JVM method-size limit.
    @SuppressWarnings(Array("stryker4s.mutation.StringLiteral"))
    private inline def casesOf[Types <: Tuple]: Vector[(String, Codec[Any])] =
      inline erasedValue[Types] match {
        case _: EmptyTuple           => Vector.empty
        case _: (head *: EmptyTuple) =>
          val codec = derived[head](using summonInline[Mirror.Of[head]])
          val name  = codec.shape match {
            case Shape.Product(value, _) => value
            case value                   => throw IllegalArgumentException(s"sum case is not a product: $value")
          }
          Vector(name -> codec.erased)
        // Split large enums so derivation depth grows logarithmically.
        case _ => casesOf[Tuple.Take[Types, Half[Types]]] ++ casesOf[Tuple.Drop[Types, Half[Types]]]
      }
  }

  object Protocol {
    def apply[F[_]](using protocol: Protocol[F]): Protocol[F] = protocol

    inline def derived[F[_]]: Protocol[F] = ${ derivedProtocol[F] }

    private def derivedProtocol[F[_]: Type](using Quotes): Expr[Protocol[F]] = {
      import quotes.reflect.*

      val operationType   = TypeRepr.of[F]
      val operationSymbol = operationType.typeSymbol
      if (!operationSymbol.flags.is(Flags.Enum))
        report.errorAndAbort(s"${operationType.show} is not an enum")
      val name     = operationSymbol.name
      val children = operationSymbol.children
      if (children.isEmpty) report.errorAndAbort(s"$name has no protocol operations")
      val cases = children.map { child =>
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
                (writer, _) => writer.writeArrayHeader(0),
                reader =>
                  reader.readArrayHeader().flatMap { count =>
                    if (count == 0) Right($value) else Left(s"expected empty operation array, got $count")
                  }
              )
            }
            '{
              Protocol.protocolCase[F, answer, F[answer]](
                ${ Expr(child.name) },
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
                    $codec,
                    $replyCodec.shape
                  )
                }
            }
        }
      }
      '{ Protocol.runtime[F](${ Expr(name) }, Vector(${ Expr.ofSeq(cases) }*)) }
    }

    private class DecodedValue[F[_], A](override val value: F[A]) extends Decoded[F] {
      override type Reply = A
    }

    private class ProtocolImpl[F[_]](
        name: String,
        cases: Vector[ProtocolCase[F]]
    ) extends Protocol[F] {
      override val operations: Vector[OperationShape] =
        cases.map(value => OperationShape(Alternative(value.name, value.codec.shape), value.reply))

      override val shape: Shape.Sum = Shape.Sum(name, operations.map(_.request))

      override def encodePayload[A](
          value: F[A],
          limits: Limits
      ): Either[String, (String, Array[Byte])] =
        value match {
          case enumValue: scala.reflect.Enum =>
            val sumCase = cases(enumValue.ordinal)
            val writer  = Writer(limits)
            sumCase.codec.write(writer, value)
            writer.result().map(sumCase.name -> _)
          case _ => Left(s"$name protocol value is not an enum case")
        }

      override def decodePayload(
          tag: String,
          bytes: Array[Byte],
          limits: Limits
      ): Either[String, Decoded[F]] = Reader.decode(bytes, limits)(reader => readCase(tag, reader))

      private def readCase(tag: String, reader: Reader): Either[String, Decoded[F]] =
        cases.find(_.name == tag)
          .toRight(s"unknown $name operation tag: $tag")
          .flatMap(_.decode(reader))
    }

    private[Wire] class ProtocolCase[F[_]](
        val name: String,
        val codec: Codec[Any],
        val reply: Shape,
        val decode: Reader => Either[String, Decoded[F]]
    )

    @publicInBinary
    private[Wire] def runtime[F[_]](
        name: String,
        cases: Vector[ProtocolCase[F]]
    ): Protocol[F] = ProtocolImpl(name, cases)

    @publicInBinary
    private[Wire] def protocolCase[F[_], A, C](
        name: String,
        codec: Codec[C],
        reply: Shape
    ): ProtocolCase[F] = ProtocolCase(
      name,
      codec.erased,
      reply,
      reader =>
        codec.read(reader).map { case decoded: F[A] @unchecked => DecodedValue[F, A](decoded) }
    )
  }

  trait Protocol[F[_]] {
    def shape: Shape.Sum
    def operations: Vector[OperationShape]
    def encodePayload[A](value: F[A], limits: Limits): Either[String, (String, Array[Byte])]
    def decodePayload(
        tag: String,
        bytes: Array[Byte],
        limits: Limits
    ): Either[String, Decoded[F]]

    final def schema[Failure: Codec](name: NonEmpty[String]): ProtocolShape =
      ProtocolShape(name, operations, Codec[Failure].shape)
  }

  sealed trait Decoded[F[_]] {
    type Reply
    val value: F[Reply]
  }

  trait Codec[A] {
    def shape: Shape
    private[Wire] def write(writer: Writer, value: A): Unit
    private[Wire] def read(reader: Reader): Either[String, A]

    final def encode(value: A, limits: Limits): Either[String, Array[Byte]] = {
      val writer = Writer(limits)
      write(writer, value)
      writer.result()
    }

    final def decode(bytes: Array[Byte], limits: Limits): Either[String, A] = Reader.decode(bytes, limits)(read(_))

    /** Existential adapter for heterogeneous derived-codec tables. */
    @publicInBinary
    private[Wire] final def erased: Codec[Any] = {
      val underlying = this
      Codec.instance(
        shape,
        (writer, value) =>
          value match {
            case typed: A @unchecked => underlying.write(writer, typed)
          },
        reader => underlying.read(reader)
      )
    }

  }

  case class OperationShape(request: Alternative, reply: Shape)
  case class ProtocolShape(
      name: NonEmpty[String],
      operations: Vector[OperationShape],
      failure: Shape
  ) {
    val canonical: String = {
      def render(shape: Shape): String = shape match {
        case Shape.UnitValue                        => "unit"
        case Shape.BooleanValue                     => "bool"
        case Shape.Int8                             => "i8"
        case Shape.Int16                            => "i16"
        case Shape.Int32                            => "i32"
        case Shape.Int64                            => "i64"
        case Shape.Float32                          => "f32"
        case Shape.Float64                          => "f64"
        case Shape.Text                             => "text"
        case Shape.Sequence(element)                => s"seq[${render(element)}]"
        case Shape.Optional(element)                => s"option[${render(element)}]"
        case Shape.Alias(alias, value, constraints) =>
          val rendered = constraints.map {
            case Constraint.Positive                       => "positive"
            case Constraint.Positive0                      => "positive0"
            case Constraint.IntInterval(minimum, maximum)  => s"int-interval[$minimum,$maximum]"
            case Constraint.LongInterval(minimum, maximum) => s"long-interval[$minimum,$maximum]"
            case Constraint.OneOf(values)                  => s"one-of[${values.mkString(",")}]"
            case Constraint.MultipleOf(divisor)            => s"multiple-of[$divisor]"
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
            s"${alternative.name}:${render(alternative.shape)}"
          ).mkString(s"sum($sum){", ",", "}")
      }
      operations.map(operation =>
        s"${operation.request.name}:${render(operation.request.shape)}=>${render(operation.reply)}"
      ).mkString(s"protocol($name){", ",", s"};failure=${render(failure)}")
    }

  }
}
