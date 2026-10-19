package net.kurobako.gemstone.rpc

import net.kurobako.gemstone.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.rpc.Wire.*
import scala.quoted.*

/** Derive validation and schema metadata from the refinement or nominal representation. */
object WireCodecMacros {
  def refined[A: Type, P: Type](base: Expr[Codec[A]])(using Quotes): Expr[Codec[A where P]] = {
    import quotes.reflect.*
    val constraints: List[Expr[Constraint]] = macros.numericDomain[A, P] match {
      case macros.Domain.Integers(spans)                => integers[A](spans)
      case macros.Domain.Reals(_, None)                 => Nil
      case macros.Domain.Reals(_, Some(List(interval))) => List(real(interval))
      case macros.Domain.Reals(_, Some(intervals))      =>
        List('{
          Constraint.AnyOf(${ Expr.ofSeq(intervals.map(interval => '{ Vector(${ real(interval) }) })) }.toVector)
        })
    }
    val metadata = Expr.ofSeq(constraints)
    val name     = Expr(Type.of[A] match {
      case '[Byte]   => "Byte"
      case '[Short]  => "Short"
      case '[Int]    => "Int"
      case '[Long]   => "Long"
      case '[Float]  => "Float"
      case '[Double] => "Double"
      case _         => report.errorAndAbort(s"unsupported numeric carrier ${Type.show[A]}")
    })
    '{
      Codec.domain[A where P, A]($name, $base, $metadata.toVector)(
        value => value.refine[A where P],
        value => value
      )
    }
  }

  private def real(interval: (low: Double, high: Double))(using Quotes): Expr[Constraint] =
    '{ Constraint.DoubleInterval(${ Expr(interval.low) }, ${ Expr(interval.high) }) }

  /** The values of a span holding at most two: a point, or one whose divisor is wider than `Long`. */
  private def finite(span: macros.Span): Option[List[Long]] =
    if (span.low == span.high) Some(List(span.low.toLong))
    else Option.when(span.step > BigInt(Long.MaxValue))(List(span.low.toLong, span.high.toLong))

  private def integers[A: Type](spans: List[macros.Span])(using Quotes): List[Expr[Constraint]] = {
    import quotes.reflect.*
    val int    = !(TypeRepr.of[A] =:= TypeRepr.of[Long])
    val limits = macros.numericDomain[A, Any] match {
      case macros.Domain.Integers(List(limits)) => limits
      case _                                    => report.errorAndAbort(s"unsupported numeric carrier ${Type.show[A]}")
    }
    def span(span: macros.Span): List[Expr[Constraint]] =
      if (span.step > BigInt(Long.MaxValue)) List('{
        Constraint.OneOf(${ Expr(finite(span).toList.flatten) }.toVector)
      })
      else {
        val bounds =
          if (span.low == limits.low && span.high == limits.high) Nil
          else if (int) List('{ Constraint.IntInterval(${ Expr(span.low.toInt) }, ${ Expr(span.high.toInt) }) })
          else List('{ Constraint.LongInterval(${ Expr(span.low.toLong) }, ${ Expr(span.high.toLong) }) })
        bounds ++ (if (span.step == 1) Nil else List('{ Constraint.MultipleOf(${ Expr(span.step.toLong) }) }))
      }
    spans match {
      case List(only) if only.step == 1 && only.high == limits.high && only.low == 1 => List('{ Constraint.Positive })
      case List(only) if only.step == 1 && only.high == limits.high && only.low == 0 => List('{ Constraint.Positive0 })
      case List(only) if only.step <= BigInt(Long.MaxValue)                          => span(only)
      case _ if spans.forall(finite(_).nonEmpty)                                     =>
        val values = Expr.ofSeq(spans.flatMap(finite(_).toList.flatten).distinct.sorted.map(Expr(_)))
        List('{ Constraint.OneOf($values.toVector) })
      case _ =>
        val alternatives = Expr.ofSeq(spans.map(only => '{ Vector(${ Expr.ofSeq(span(only)) }*) }))
        List('{ Constraint.AnyOf($alternatives.toVector) })
    }
  }

  def nonEmpty[A: Type](base: Expr[Codec[A]], elements: Expr[HasElements[A]])(using
      Quotes
  ): Expr[Codec[NonEmpty[A]]] = {
    import quotes.reflect.*
    def short(tpe: TypeRepr): String = tpe.show(using Printer.TypeReprShortCode).filter(_.isLetterOrDigit)
    val name                         = Expr(TypeRepr.of[A].dealias match {
      case string if string =:= TypeRepr.of[String]                       => "String"
      case AppliedType(_, List(element)) if element =:= TypeRepr.of[Byte] => "NonEmptyBytes"
      case AppliedType(_, List(element))                                  => s"NonEmpty${short(element)}s"
      case other                                                          => s"NonEmpty${short(other)}"
    })
    '{
      Codec.domain[NonEmpty[A], A]($name, $base, Vector(Constraint.MinimumLength(1)))(
        value => NonEmpty.check[A](value)(using $elements),
        value => value
      )
    }
  }

  def newtype[A: Type, C: Type](representation: Expr[Newtype.Representation[A, C]], base: Expr[Codec[C]])(using
      Quotes
  ): Expr[Codec[A]] = {
    import quotes.reflect.*
    val name = TypeRepr.of[A].dealias match {
      case ref: TypeRef => Expr(ref.qualifier.termSymbol.name)
      case other        => report.errorAndAbort(s"expected a named Newtype, found ${other.show}")
    }
    '{
      val underlying = $base
      // Flatten schema aliases while preserving the base codec's validation on reads and writes.
      val shape = underlying.shape match {
        case Shape.Alias(_, carrier, constraints) => Shape.Alias($name, carrier, constraints)
        case carrier                              => Shape.Alias($name, carrier, Vector.empty)
      }
      Codec.mapped[A, C](shape, underlying)($representation.wrap(_), $representation.unwrap(_))
    }
  }
}
