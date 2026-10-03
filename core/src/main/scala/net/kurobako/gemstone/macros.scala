package net.kurobako.gemstone

import scala.quoted.*

/** Compile-time checks for numeric bounds, finite choices and multiples. No arithmetic-result inference. */
object macros {
  private case class Span(low: BigInt, high: BigInt, step: BigInt = BigInt(1)) {
    def contains(value: BigInt): Boolean = low <= value && value <= high && value % step == 0
    def subsetOf(other: Span): Boolean   =
      low >= other.low && high <= other.high &&
        (if (low == high) other.contains(low) else step % other.step == 0)
  }

  private def floor(value: BigInt, divisor: BigInt): BigInt = {
    val quotient = value / divisor
    if (value < 0 && value % divisor != 0) quotient - 1 else quotient
  }

  private def normalize(spans: List[Span]): List[Span] =
    spans.flatMap { span =>
      val low  = -floor(-span.low, span.step) * span.step
      val high = floor(span.high, span.step) * span.step
      Option.when(low <= high)(Span(low, high, span.step))
    }

  private def intersect(left: List[Span], right: List[Span]): List[Span] = normalize(for {
    a <- left
    b <- right
  } yield Span(a.low.max(b.low), a.high.min(b.high), a.step / a.step.gcd(b.step) * b.step))

  /** Cover the source progression with target spans without enumerating its values. */
  private def coveredBy(source: Span, target: List[Span]): Boolean = {
    if (target.exists(source.subsetOf(_))) true
    else {
      val pieces = intersect(List(source), target).sortBy(_.low)
      val next = pieces.foldLeft(source.low) { (next, piece) =>
        if (piece.low <= next && piece.high >= next &&
            (piece.low == piece.high || piece.step == source.step)) piece.high + source.step
        else next
      }
      next > source.high
    }
  }

  private def carrier(using q: Quotes)(tpe: q.reflect.TypeRepr): Span = {
    import q.reflect.*
    if (tpe =:= TypeRepr.of[Byte]) Span(BigInt(Byte.MinValue), BigInt(Byte.MaxValue))
    else if (tpe =:= TypeRepr.of[Short]) Span(BigInt(Short.MinValue), BigInt(Short.MaxValue))
    else if (tpe =:= TypeRepr.of[Int]) Span(BigInt(Int.MinValue), BigInt(Int.MaxValue))
    else if (tpe =:= TypeRepr.of[Long]) Span(BigInt(Long.MinValue), BigInt(Long.MaxValue))
    else report.errorAndAbort(s"constraint supports Byte, Short, Int and Long, found ${tpe.show}")
  }

  private def constant(using q: Quotes)(tpe: q.reflect.TypeRepr): BigInt = {
    import q.reflect.*
    tpe.dealias.widenTermRefByName match {
      case ConstantType(IntConstant(value))  => BigInt(value)
      case ConstantType(LongConstant(value)) => BigInt(value)
      case _ => report.errorAndAbort(s"Expected an Int or Long literal type, found ${tpe.show}")
    }
  }

  private def choices(using q: Quotes)(tpe: q.reflect.TypeRepr): List[BigInt] = tpe.asType match {
    case '[EmptyTuple]   => Nil
    case '[head *: tail] => constant(q.reflect.TypeRepr.of[head]) :: choices(q.reflect.TypeRepr.of[tail])
    case _               => q.reflect.report.errorAndAbort(s"Expected a tuple of integer literals, found ${tpe.show}")
  }

  private def domain(using q: Quotes)(base: q.reflect.TypeRepr, predicate: q.reflect.TypeRepr): List[Span] = {
    val spans = uncheckedDomain(base, predicate)
    if (spans.isEmpty)
      q.reflect.report.errorAndAbort(
        s"Unsatisfiable constraint: ${base.show} where ${predicate.show} has no representable values"
      )
    spans
  }

  private def uncheckedDomain(using q: Quotes)(base: q.reflect.TypeRepr, predicate: q.reflect.TypeRepr): List[Span] = {
    import q.reflect.*
    import constraint.*
    val limits                                                      = carrier(base)
    def clipped(spans: List[Span]): List[Span]                      = intersect(spans, List(limits))
    def unary(bound: TypeRepr)(f: BigInt => List[Span]): List[Span] = clipped(f(constant(bound)))
    predicate.dealias match {
      case AndType(left, right)        => intersect(uncheckedDomain(base, left), uncheckedDomain(base, right))
      case p if p =:= TypeRepr.of[Any] => List(limits)
      case p                           => p.asType match {
          case '[GreaterThan[n]] => unary(TypeRepr.of[n])(n => List(Span(n + 1, limits.high)))
          case '[AtLeast[n]]     => unary(TypeRepr.of[n])(n => List(Span(n, limits.high)))
          case '[LessThan[n]]    => unary(TypeRepr.of[n])(n => List(Span(limits.low, n - 1)))
          case '[AtMost[n]]      => unary(TypeRepr.of[n])(n => List(Span(limits.low, n)))
          case '[In[t]]          =>
            val values = choices(TypeRepr.of[t])
            if (values.isEmpty) report.errorAndAbort("in requires at least one integer literal")
            clipped(values.map(n => Span(n, n)))
          case '[MultipleOf[n]] => unary(TypeRepr.of[n]) { n =>
              if (n <= 0) report.errorAndAbort("MultipleOf requires a positive divisor")
              List(limits.copy(step = n))
            }
          case _ => report.errorAndAbort(s"Unsupported constraint predicate: ${predicate.show}")
        }
    }
  }

  /** Normalized numeric ranges used by serializers to describe the same predicates they validate. */
  def numericDomain[A: Type, P: Type](using Quotes): List[(BigInt, BigInt, BigInt)] =
    domain(quotes.reflect.TypeRepr.of[A], quotes.reflect.TypeRepr.of[P]).map(span => (span.low, span.high, span.step))

  def prove[A: Type, P: Type, B: Type, Q: Type](using Quotes): Expr[Unit] = {
    import quotes.reflect.*
    val source   = domain(TypeRepr.of[A], TypeRepr.of[P])
    val target   = domain(TypeRepr.of[B], TypeRepr.of[Q])
    val same     = TypeRepr.of[A] =:= TypeRepr.of[B]
    val widening = carrier(TypeRepr.of[A]).subsetOf(carrier(TypeRepr.of[B]))
    if (!(same || widening) || !source.forall(coveredBy(_, target)))
      report.errorAndAbort(
        s"Cannot prove ${Type.show[A]} where ${Type.show[P]} implies ${Type.show[B]} where ${Type.show[Q]}; retain the producer refinement, or validate the original boundary value with .refine after explicitly converting its carrier"
      )
    '{ () }
  }

  /** Explicit widening shares the same proofs and numeric conversions as implicit widening. */
  def widen[A: Type, P: Type, R: Type](value: Expr[constraint.where[A, P]])(using Quotes): Expr[R] = {
    import quotes.reflect.*
    import constraint.*
    TypeRepr.of[R].dealias match {
      case AppliedType(base, List(b, q)) if base.typeSymbol == TypeRepr.of[where[Any, Any]].typeSymbol =>
        q.asType match {
          case '[q] =>
            val result =
              if (TypeRepr.of[A] =:= b) '{ auto.implication[A, P, q]($value) }
              else (Type.of[A], b.asType) match {
                case ('[Byte], '[Short]) => '{ auto.byteToShort[P, q](${ value.asExprOf[Byte where P] }) }
                case ('[Byte], '[Int])   => '{ auto.byteToInt[P, q](${ value.asExprOf[Byte where P] }) }
                case ('[Byte], '[Long])  => '{ auto.byteToLong[P, q](${ value.asExprOf[Byte where P] }) }
                case ('[Short], '[Int])  => '{ auto.shortToInt[P, q](${ value.asExprOf[Short where P] }) }
                case ('[Short], '[Long]) => '{ auto.shortToLong[P, q](${ value.asExprOf[Short where P] }) }
                case ('[Int], '[Long])   => '{ auto.intToLong[P, q](${ value.asExprOf[Int where P] }) }
                case _ => report.errorAndAbort(s"Cannot widen representation ${Type.show[A]} to ${b.show}")
              }
            result.asExprOf[R]
        }
      case _ => report.errorAndAbort("Supply a complete numeric refinement target, for example value.widen[Int >= 0]")
    }
  }

  /** Peel only our wrappers; collection elements and unrelated opaque types remain intact. */
  @SuppressWarnings(Array("scalafix:GemstoneNoTypeCasting"))
  def raw[A: Type](value: Expr[A])(using Quotes): Expr[Any] = {
    import quotes.reflect.*
    @annotation.tailrec
    def representation(tpe: TypeRepr): TypeRepr = tpe.dealias match {
      case AppliedType(base, List(a, _)) if base.typeSymbol == TypeRepr.of[constraint.where[Any, Any]].typeSymbol =>
        representation(a)
      case AppliedType(base, List(a)) if base.typeSymbol == TypeRepr.of[constraint.NonEmpty[Any]].typeSymbol =>
        representation(a)
      case target: TypeRef if target.name == "Type" =>
        target.qualifier.baseType(TypeRepr.of[constraint.Newtype[Any]].typeSymbol) match {
          case AppliedType(_, List(base)) => representation(base)
          case _                          => tpe
        }
      case _ => tpe
    }
    val result = representation(TypeRepr.of[A])
    result.asType match {
      // All recognized wrappers are opaque aliases of their carrier, so this changes only the static type.
      case '[b] => '{ $value.asInstanceOf[b] }
    }
  }

  def newtypeValue[A: Type, C: Type](value: Expr[A])(using Quotes): Expr[C] = {
    import quotes.reflect.*
    if (TypeRepr.of[A].widen.dealias <:< TypeRepr.of[C]) value.asExprOf[C]
    else unsafe[A, C](value)
  }

  def unsafe[A: Type, R: Type](value: Expr[A])(using Quotes): Expr[R] = {
    import quotes.reflect.*
    import constraint.*
    val source                    = TypeRepr.of[A].widen.dealias
    def compatible[B: Type]: Unit =
      if (!(source =:= TypeRepr.of[B]))
        report.errorAndAbort(
          s"Cannot refine ${Type.show[A]} as ${Type.show[R]}: expected representation ${Type.show[B]}"
        )
    TypeRepr.of[R].dealias match {
      case AppliedType(base, List(a, p)) if base.typeSymbol == TypeRepr.of[constraint.where[Any, Any]].typeSymbol =>
        (a.asType, p.asType) match {
          case ('[a], '[p]) =>
            compatible[a]
            '{ where.validate[a, p](${ value.asExprOf[a] }) }.asExprOf[R]
          case _ => report.errorAndAbort("Unsupported refinement target")
        }
      case AppliedType(base, List(a)) if base.typeSymbol == TypeRepr.of[constraint.NonEmpty[Any]].typeSymbol =>
        a.asType match {
          case '[a] =>
            compatible[a]
            Expr.summon[HasElements[a]] match {
              case Some(check) => '{ NonEmpty.validate[a](${ value.asExprOf[a] })(using $check) }.asExprOf[R]
              case None        => report.errorAndAbort(s"No immutable nonempty validation for ${Type.show[a]}")
            }
          case _ => report.errorAndAbort("Unsupported nonempty target")
        }
      case target: TypeRef if target.name == "Type" =>
        target.qualifier.baseType(TypeRepr.of[Newtype[Any]].typeSymbol) match {
          case AppliedType(_, List(base)) =>
            base.asType match {
              case '[c] =>
                val checked = newtypeValue[A, c](value)
                target.qualifier match {
                  case owner: TermRef =>
                    Select.overloaded(Ref(owner.termSymbol), "apply", Nil, List(checked.asTerm)).asExprOf[R]
                  case _ => report.errorAndAbort("Newtype must have a stable companion")
                }
              case _ => report.errorAndAbort("Unsupported Newtype representation")
            }
          case _ => report.errorAndAbort("Supply a refinement or Newtype target")
        }
      case _ => report.errorAndAbort("Supply a supported refinement target, for example value.refine[Int > 0]")
    }
  }

  def checkNumericLiteral[A: Type, P: Type](value: Expr[A])(using Quotes): Expr[Unit] = {
    import quotes.reflect.*
    val number = Type.of[A] match {
      case '[Byte]  => value.asExprOf[Byte].value.map(value => BigInt(value.toInt))
      case '[Short] => value.asExprOf[Short].value.map(value => BigInt(value.toInt))
      case '[Int]   => value.asExprOf[Int].value.map(BigInt(_))
      case '[Long]  => value.asExprOf[Long].value.map(BigInt(_))
      case _        =>
        report.errorAndAbort("Numeric refinement literals require the matching Byte, Short, Int or Long representation")
    }
    val literal = number.getOrElse(report.errorAndAbort(
      "Runtime inputs require .refine at the domain boundary; propagate already-refined values directly"
    ))
    if (!domain(TypeRepr.of[A], TypeRepr.of[P]).exists(_.contains(literal)))
      report.errorAndAbort(s"$literal does not satisfy ${Type.show[A]} where ${Type.show[P]}")
    '{ () }
  }

  def checkStringLiteral(value: Expr[String])(using Quotes): Expr[Unit] = {
    import quotes.reflect.*
    import constraint.*
    val directive = "%(?:[0-9]+\\$)?[-#+ 0,(<]*[0-9]*(?:\\.[0-9]+)?[tT]?[a-zA-Z%]".r
    def interpolation(parts: Seq[String], formatted: Boolean): Boolean = {
      val fixed = if (formatted) parts.map(directive.replaceAllIn(_, "")) else parts
      fixed.exists(_.nonEmpty)
    }
    def nonEmpty(expression: Expr[String]): Boolean = expression.value match {
      case Some(text) => text.nonEmpty
      case None       => expression match {
          case '{ StringContext(${ Varargs(parts) }*).s(${ Varargs(_) }*) } =>
            interpolation(parts.map(part => StringContext.processEscapes(part.valueOrAbort)), false)
          case '{ ($left: String) + ($right: String) } => nonEmpty(left) || nonEmpty(right)
          case _                                       => expression.asTerm.underlyingArgument match {
              case Apply(TypeApply(Select(context, "f"), _), _) =>
                context.asExpr match {
                  case '{ StringContext(${ Varargs(parts) }*) } =>
                    interpolation(parts.map(part => StringContext.processEscapes(part.valueOrAbort)), true)
                  case _ => false
                }
              case _ => expression.asTerm.tpe.widen <:< TypeRepr.of[NonEmpty[String]]
            }
        }
    }
    if (!nonEmpty(value))
      report.errorAndAbort(
        "Cannot prove this string is nonempty; retain a NonEmpty producer type or validate the boundary input with .refine"
      )
    '{ () }
  }

  def description[A: Type, P: Type](using Quotes): Expr[String] =
    Expr(s"value must satisfy ${Type.show[A]} where ${Type.show[P]}")

  def accepts[A: Type, P: Type](value: Expr[A])(using Quotes): Expr[Boolean] = {
    import quotes.reflect.*
    val spans                                   = domain(TypeRepr.of[A], TypeRepr.of[P])
    def test(number: Expr[Long]): Expr[Boolean] = spans.map { span =>
      val low  = Expr(span.low.toLong)
      val high = Expr(span.high.toLong)
      if (span.low == span.high) '{ $number == $low }
      else if (span.step > BigInt(Long.MaxValue)) '{ $number == $low || $number == $high }
      else if (span.step == 1) '{ $number >= $low && $number <= $high }
      else '{ $number >= $low && $number <= $high && $number % ${ Expr(span.step.toLong) } == 0L }
    }.foldRight(Expr(false))((check, rest) => '{ $check || $rest })
    Type.of[A] match {
      case '[Byte]  => test('{ ${ value.asExprOf[Byte] }.toLong })
      case '[Short] => test('{ ${ value.asExprOf[Short] }.toLong })
      case '[Int]   => test('{ ${ value.asExprOf[Int] }.toLong })
      case '[Long]  => test(value.asExprOf[Long])
      case _        => report.errorAndAbort("constraint supports Byte, Short, Int and Long")
    }
  }
}
