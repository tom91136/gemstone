package net.kurobako.gemstone.rpc

import java.nio.charset.StandardCharsets
import net.kurobako.gemstone.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.msgpack.*
import scala.annotation.publicInBinary
import scala.compiletime.constValue
import scala.compiletime.erasedValue
import scala.compiletime.summonInline
import scala.deriving.Mirror
import scala.quoted.*
import scala.reflect.ClassTag
import scala.util.NotGiven

object Wire {
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

  private def decodeAll[A](bytes: Array[Byte], limits: Limits)(read: Reader => Either[String, A]): Either[String, A] =
    if (bytes.length > limits.maximumBytes.raw) Left(s"wire value exceeds the ${limits.maximumBytes}-byte limit")
    else {
      val reader = Reader(bytes, 0, bytes.length, limits)
      read(reader).flatMap { value =>
        Either.cond(reader.remaining == 0, value, s"wire value has ${reader.remaining} trailing bytes")
      }
    }

  object Codec {
    private class CodecImpl[A](
        valueShape: Shape,
        writeValue: (Writer, A) => Unit,
        readValue: Reader => Either[String, A],
        nullaryValue: Option[A]
    ) extends Codec[A] {
      override val shape: Shape                                          = valueShape
      override private[Wire] def write(writer: Writer, value: A): Unit   = writeValue(writer, value)
      override private[Wire] def read(reader: Reader): Either[String, A] = readValue(reader)
      override private[Wire] def nullary: Option[A]                      = nullaryValue
    }

    def apply[A](using codec: Codec[A]): Codec[A] = codec

    @publicInBinary
    private[Wire] def instance[A](
        valueShape: Shape,
        writeValue: (Writer, A) => Unit,
        readValue: Reader => Either[String, A],
        nullary: Option[A] = None
    ): Codec[A] = CodecImpl(valueShape, writeValue, readValue, nullary)

    def validated[A](underlying: Codec[A])(validate: A => Either[String, A]): Codec[A] =
      mapped(underlying.shape, underlying)(validate, identity)

    given Codec[Unit] = instance(
      Shape.UnitValue,
      (writer, _) => writer.writeArrayHeader(0),
      reader =>
        reader.readArrayHeader().flatMap { count =>
          Either.cond(count.raw == 0, (), s"expected unit array of size 0, got $count")
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
      (writer, values) => {
        writer.writeArrayHeader(values.nonNegativeSize)
        values.foreach(element.write(writer, _))
      },
      reader =>
        reader.readArrayHeader().flatMap { count =>
          val mutableBuilder = Vec.newBuilder[A]
          @scala.annotation.tailrec
          def read(index: Int): Either[String, Vec[A]] =
            if (index == count.raw) Right(mutableBuilder.result())
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

    private inline def productCodec[A](mirror: Mirror.ProductOf[A]): Codec[A] = {
      val labels = labelsOf[mirror.MirroredElemLabels]
      val codecs = codecsOf[mirror.MirroredElemTypes]
      val name   = constValue[mirror.MirroredLabel]
      productCodecRuntime(name, labels, codecs, mirror)
    }

    // Generic derivation pairs each product field with its statically derived codec.
    @SuppressWarnings(Array("scalafix:BannedSyntax.product-traversal"))
    @publicInBinary
    private[Codec] def productCodecRuntime[A](
        name: String,
        labels: Vector[String],
        codecs: Vector[Codec[?]],
        mirror: Mirror.ProductOf[A]
    ): Codec[A] = {
      val arity = labels.size.unsafe[Int >= 0]
      instance(
        Shape.Product(name, labels.zip(codecs.map(_.shape)).map { case (label, codec) => Field(label, codec) }),
        (writer, value) =>
          value match {
            case product: Product =>
              writer.writeArrayHeader(arity)
              codecs.indices.foreach { index =>
                codecs(index).erased.write(writer, product.productElement(index))
              }
            case _ => writer.reject(s"$name value is not a product")
          },
        reader =>
          reader.readArrayHeader().flatMap { count =>
            if (count.raw != arity.raw) Left(s"expected $name product array of size $arity, got $count")
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
          },
        Option.when(labels.isEmpty)(mirror.fromProduct(EmptyTuple))
      )
    }

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
      val singletons = cases.flatMap(_._2.nullary)
      val allNullary = singletons.length == cases.length
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
              singletons.lift(index).toRight(s"unknown $name ordinal: $index").map { case value: A @unchecked => value }
            }
          else
            reader.readArrayHeader().flatMap { count =>
              if (count.raw != 2) Left(s"expected $name sum array of size 2, got $count")
              else
                reader.readString().flatMap { tag =>
                  cases.find(_._1 == tag)
                    .toRight(s"unknown $name sum tag: $tag")
                    .flatMap(_._2.read(reader).map { case value: A @unchecked => value })
                }
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
                    if (count.raw == 0) Right($value) else Left(s"expected empty operation array, got $count")
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
      ): Either[String, Decoded[F]] = decodeAll(bytes, limits)(reader => readCase(tag, reader))

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
    private[Wire] def nullary: Option[A]

    final def encode(value: A, limits: Limits): Either[String, Array[Byte]] = {
      val writer = Writer(limits)
      write(writer, value)
      writer.result()
    }

    final def decode(bytes: Array[Byte], limits: Limits): Either[String, A] = decodeAll(bytes, limits)(read(_))

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
        reader => underlying.read(reader),
        underlying.nullary
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
