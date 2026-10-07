package net.kurobako.gemstone.rpc

import net.kurobako.gemstone.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.rpc.Wire.*
import scala.quoted.*

/** Derive validation and schema metadata from the refinement or nominal representation. */
object WireCodecMacros {
  def refined[A: Type, P: Type](base: Expr[Codec[A]])(using Quotes): Expr[Codec[A where P]] = {
    import quotes.reflect.*
    val int                                 = !(TypeRepr.of[A] =:= TypeRepr.of[Long])
    val (minimum, maximum, _)               = macros.numericDomain[A, Any].head
    val ranges                              = macros.numericDomain[A, P]
    val constraints: List[Expr[Constraint]] = ranges match {
      case List((low, high, step)) if step == 1 && high == maximum && low == 1 => List('{ Constraint.Positive })
      case List((low, high, step)) if step == 1 && high == maximum && low == 0 => List('{ Constraint.Positive0 })
      case List((low, high, step)) if step <= BigInt(Long.MaxValue)            =>
        val bounds = if (low == minimum && high == maximum) Nil
        else if (int) List('{ Constraint.IntInterval(${ Expr(low.toInt) }, ${ Expr(high.toInt) }) })
        else List('{ Constraint.LongInterval(${ Expr(low.toLong) }, ${ Expr(high.toLong) }) })
        bounds ++ (if (step == 1) Nil else List('{ Constraint.MultipleOf(${ Expr(step.toLong) }) }))
      case other =>
        // Finite choices, or a divisor wider than Long (at most two representable multiples).
        val values = other.flatMap { (low, high, step) =>
          if (low == high) List(low.toLong)
          else if (step > BigInt(Long.MaxValue)) List(low, low + step).filter(_ <= high).map(_.toLong)
          else report.errorAndAbort("unsupported disjoint wire predicate ranges")
        }.distinct.sorted
        val encoded = Expr.ofSeq(values.map(Expr(_)))
        List('{ Constraint.OneOf($encoded.toVector) })
    }
    val metadata = Expr.ofSeq(constraints)
    val name     = Expr(Type.of[A] match {
      case '[Byte]  => "Byte"
      case '[Short] => "Short"
      case '[Int]   => "Int"
      case '[Long]  => "Long"
      case _        => report.errorAndAbort(s"unsupported numeric carrier ${Type.show[A]}")
    })
    '{
      Codec.domain[A where P, A]($name, $base, $metadata.toVector)(
        value => value.refine[A where P],
        _.raw
      )
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
