package net.kurobako.gemstone

import scala.quoted.*

/** Compile-time checks for numeric bounds, memberships and multiples. No arithmetic-result inference. */
object macros {
  private[gemstone] given (using q: Quotes): CanEqual[q.reflect.Symbol, q.reflect.Symbol] =
    CanEqual.derived

  private[gemstone] case class Span(low: BigInt, high: BigInt, step: BigInt = BigInt(1)) {
    def contains(value: BigInt): Boolean = low <= value && value <= high && value % step == 0
    def subsetOf(other: Span): Boolean   =
      low >= other.low && high <= other.high &&
        (if (low == high) other.contains(low) else step % other.step == 0)
  }

  private def floor(value: BigInt, divisor: BigInt): BigInt = {
    val quotient = value / divisor
    if (value < 0 && value % divisor != 0) quotient - 1 else quotient
  }

  private def normalise(spans: List[Span]): List[Span] =
    spans.flatMap { span =>
      val low  = -floor(-span.low, span.step) * span.step
      val high = floor(span.high, span.step) * span.step
      Option.when(low <= high)(Span(low, high, span.step))
    }

  private def intersect(left: List[Span], right: List[Span]): List[Span] = normalise(for {
    a <- left
    b <- right
  } yield Span(a.low.max(b.low), a.high.min(b.high), a.step / a.step.gcd(b.step) * b.step))

  /** Cover the source progression with target spans without enumerating its values. */
  private def coveredBy(source: Span, target: List[Span]): Boolean =
    if (target.exists(source.subsetOf(_))) true
    else {
      val pieces = intersect(List(source), target).sortBy(_.low)
      val next   = pieces.foldLeft(source.low) { (next, piece) =>
        if (
          piece.low <= next && piece.high >= next &&
          (piece.low == piece.high || piece.step == source.step)
        ) piece.high + source.step
        else next
      }
      next > source.high
    }

  /** The values a refinement admits: spans of integers, or closed intervals of a binary floating-point carrier's
    * representable values, which no NaN satisfies, and which only an unbounded refinement, `None`, leaves unrestricted.
    */
  private[gemstone] enum Domain {
    case Integers(spans: List[Span])
    case Reals(single: Boolean, intervals: Option[List[(low: Double, high: Double)]])

    def isEmpty: Boolean = this match {
      case Integers(spans)           => spans.isEmpty
      case Reals(_, Some(intervals)) => intervals.isEmpty
      case Reals(_, None)            => false
    }
  }

  private enum Carrier {
    case Integer(limits: Span)
    case Binary(single: Boolean)
  }

  private def carrier(using q: Quotes)(tpe: q.reflect.TypeRepr): Carrier = {
    import q.reflect.*
    if (tpe =:= TypeRepr.of[Float]) Carrier.Binary(single = true)
    else if (tpe =:= TypeRepr.of[Double]) Carrier.Binary(single = false)
    else
      view(tpe) match {
        case Some((limits, _)) => Carrier.Integer(limits)
        case None              =>
          report.errorAndAbort(s"constraint supports Float, Double and types with an IntegerView, found ${tpe.show}")
      }
  }

  /** The integers `tpe` holds and its [[constraint.IntegerView]], when it has one. */
  private def view(using q: Quotes)(tpe: q.reflect.TypeRepr): Option[(Span, q.reflect.Term)] = {
    import q.reflect.*
    tpe.widen.asType match {
      case '[a] =>
        Implicits.search(TypeRepr.of[constraint.IntegerView[a, ? <: Int, ? <: Boolean]]) match {
          case found: ImplicitSearchSuccess =>
            found.tree.tpe.widen.baseType(TypeRepr.of[constraint.IntegerView[Any, 1, true]].typeSymbol) match {
              case AppliedType(_, List(_, width, signed)) =>
                (width.dealias, signed.dealias) match {
                  case (ConstantType(IntConstant(bits)), ConstantType(BooleanConstant(twos)))
                      if bits >= 1 && bits <= 64 =>
                    val limits =
                      if (twos) Span(-(BigInt(1) << (bits - 1)), (BigInt(1) << (bits - 1)) - 1)
                      else Span(BigInt(0), (BigInt(1) << bits) - 1)
                    Some((limits, found.tree))
                  case _ =>
                    report.errorAndAbort(
                      s"IntegerView of ${tpe.show} needs a literal width from 1 to 64 and signedness"
                    )
                }
              case _ => None
            }
          case _ => None
        }
    }
  }

  /** Whether every value of carrier `a` is one of `b`, which proofs may then convert to. */
  private def widens(using q: Quotes)(a: q.reflect.TypeRepr, b: q.reflect.TypeRepr): Boolean =
    (carrier(a), carrier(b)) match {
      case (Carrier.Integer(source), Carrier.Integer(target)) => source.subsetOf(target)
      case _                                                  => false
    }

  private def constant(using q: Quotes)(tpe: q.reflect.TypeRepr): q.reflect.Constant = {
    import q.reflect.*
    tpe.dealias.widenTermRefByName match {
      case ConstantType(value) => value
      case _                   => report.errorAndAbort(s"Expected a numeric literal type, found ${tpe.show}")
    }
  }

  private def integer(using q: Quotes)(tpe: q.reflect.TypeRepr): BigInt = {
    import q.reflect.*
    constant(tpe) match {
      case IntConstant(value)  => BigInt(value)
      case LongConstant(value) => BigInt(value)
      case _                   => report.errorAndAbort(s"Expected an Int or Long literal type, found ${tpe.show}")
    }
  }

  /** A bound as the carrier compares it. An integer bound must be one the carrier represents, as rounding it would
    * silently move the edge of the domain.
    */
  private def real(using q: Quotes)(single: Boolean, tpe: q.reflect.TypeRepr): Double = {
    import q.reflect.*
    def exact(value: BigInt, rounded: Double): Double =
      if (BigInt(java.math.BigDecimal(rounded).toBigInteger) != value)
        report.errorAndAbort(
          s"Integer bound $value is not exactly representable as a ${if (single) "Float" else "Double"}; " +
            "use a bound the carrier represents exactly"
        )
      else rounded
    constant(tpe) match {
      case IntConstant(value)    => exact(BigInt(value), if (single) value.toFloat.toDouble else value.toDouble)
      case LongConstant(value)   => exact(BigInt(value), if (single) value.toFloat.toDouble else value.toDouble)
      case FloatConstant(value)  => value.toDouble
      case DoubleConstant(value) => value
      case _                     => report.errorAndAbort(s"Expected a numeric literal type, found ${tpe.show}")
    }
  }

  private def literals(using q: Quotes)(tpe: q.reflect.TypeRepr): List[q.reflect.TypeRepr] = tpe.asType match {
    case '[EmptyTuple]   => Nil
    case '[head *: tail] => q.reflect.TypeRepr.of[head] :: literals(q.reflect.TypeRepr.of[tail])
    case _               => q.reflect.report.errorAndAbort(s"Expected a tuple of numeric literals, found ${tpe.show}")
  }

  /** The bounds of each interval a membership admits, a literal or a tuple's element bounding one of its own. A union
    * is any of its alternatives.
    */
  private def alternatives(using q: Quotes)(tpe: q.reflect.TypeRepr): List[(q.reflect.TypeRepr, q.reflect.TypeRepr)] = {
    import q.reflect.*
    tpe.dealias match {
      case OrType(left, right) => alternatives(left) ++ alternatives(right)
      case AppliedType(interval, List(low, high))
          if interval.typeSymbol == TypeRepr.of[constraint.~[0, 0]].typeSymbol =>
        List((low, high))
      case tuple if tuple <:< TypeRepr.of[NonEmptyTuple] => literals(tuple).map(value => (value, value))
      case value                                         => List((value, value))
    }
  }

  /** The argument of a `Membership`, read from the type, as a quoted pattern would widen a union of literals. */
  private object Membership {
    def unapply(using q: Quotes)(predicate: q.reflect.TypeRepr): Option[q.reflect.TypeRepr] = {
      import q.reflect.*
      predicate match {
        case AppliedType(membership, List(members))
            if membership.typeSymbol == TypeRepr.of[constraint.Membership[0]].typeSymbol =>
          Some(members)
        case _ => None
      }
    }
  }

  private def domain(using q: Quotes)(base: q.reflect.TypeRepr, predicate: q.reflect.TypeRepr): Domain = {
    val found = carrier(base) match {
      case Carrier.Integer(limits) => Domain.Integers(integers(limits, predicate))
      case Carrier.Binary(single)  => Domain.Reals(single, reals(single, predicate))
    }
    if (found.isEmpty)
      q.reflect.report.errorAndAbort(
        s"Unsatisfiable constraint: ${base.show} where ${predicate.show} has no representable values"
      )
    found
  }

  private def integers(using q: Quotes)(limits: Span, predicate: q.reflect.TypeRepr): List[Span] = {
    import q.reflect.*
    import constraint.*
    def clipped(spans: List[Span]): List[Span]                      = intersect(spans, List(limits))
    def unary(bound: TypeRepr)(f: BigInt => List[Span]): List[Span] = clipped(f(integer(bound)))
    predicate.dealias match {
      case AndType(left, right)        => intersect(integers(limits, left), integers(limits, right))
      case p if p =:= TypeRepr.of[Any] => List(limits)
      case Membership(members)         =>
        clipped(alternatives(members).map((low, high) => Span(integer(low), integer(high))).sortBy(_.low))
      case p => p.asType match {
          case '[GreaterThan[n]] => unary(TypeRepr.of[n])(n => List(Span(n + 1, limits.high)))
          case '[AtLeast[n]]     => unary(TypeRepr.of[n])(n => List(Span(n, limits.high)))
          case '[LessThan[n]]    => unary(TypeRepr.of[n])(n => List(Span(limits.low, n - 1)))
          case '[AtMost[n]]      => unary(TypeRepr.of[n])(n => List(Span(limits.low, n)))
          case '[MultipleOf[n]]  => unary(TypeRepr.of[n]) { n =>
              if (n <= 0) report.errorAndAbort("MultipleOf requires a positive divisor")
              List(limits.copy(step = n))
            }
          case _ => report.errorAndAbort(s"Unsupported constraint predicate: ${predicate.show}")
        }
    }
  }

  private def after(single: Boolean)(value: Double): Double =
    if (single) Math.nextUp(value.toFloat).toDouble else Math.nextUp(value)

  private def before(single: Boolean)(value: Double): Double =
    if (single) Math.nextDown(value.toFloat).toDouble else Math.nextDown(value)

  /** Each bound moves to the nearest value the carrier represents inside it, so every interval is closed and an empty
    * one is plain to see.
    */
  private def reals(using
      q: Quotes
  )(single: Boolean, predicate: q.reflect.TypeRepr)
      : Option[List[(low: Double, high: Double)]] = {
    import q.reflect.*
    import constraint.*
    def bound(tpe: TypeRepr): Double   = real(single, tpe)
    def atLeast(bound: Double): Double =
      if (single && bound.toFloat.toDouble < bound) after(single)(bound.toFloat.toDouble)
      else if (single) bound.toFloat.toDouble
      else bound
    def atMost(bound: Double): Double =
      if (single && bound.toFloat.toDouble > bound) before(single)(bound.toFloat.toDouble)
      else if (single) bound.toFloat.toDouble
      else bound
    def above(bound: Double): Double = if (atLeast(bound) == bound) after(single)(bound) else atLeast(bound)
    def below(bound: Double): Double = if (atMost(bound) == bound) before(single)(bound) else atMost(bound)
    def intersect(left: Option[List[(low: Double, high: Double)]], right: Option[List[(low: Double, high: Double)]])
        : Option[List[(low: Double, high: Double)]] = (left, right) match {
      case (None, other)              => other
      case (other, None)              => other
      case (Some(these), Some(those)) => Some(for {
          a <- these
          b <- those
          if math.max(a.low, b.low) <= math.min(a.high, b.high)
        } yield (math.max(a.low, b.low), math.min(a.high, b.high)))
    }
    predicate.dealias match {
      case AndType(left, right)        => intersect(reals(single, left), reals(single, right))
      case p if p =:= TypeRepr.of[Any] => None
      case Membership(members)         =>
        Some(alternatives(members).flatMap { (low, high) =>
          Option.when(atLeast(bound(low)) <= atMost(bound(high)))((atLeast(bound(low)), atMost(bound(high))))
        }.sortBy(_._1)(using Ordering.Double.TotalOrdering))
      case p => p.asType match {
          case '[GreaterThan[n]] => Some(List((above(bound(TypeRepr.of[n])), Double.PositiveInfinity)))
          case '[AtLeast[n]]     => Some(List((atLeast(bound(TypeRepr.of[n])), Double.PositiveInfinity)))
          case '[LessThan[n]]    => Some(List((Double.NegativeInfinity, below(bound(TypeRepr.of[n])))))
          case '[AtMost[n]]      => Some(List((Double.NegativeInfinity, atMost(bound(TypeRepr.of[n])))))
          case '[MultipleOf[_]]  => report.errorAndAbort("MultipleOf requires an integer carrier")
          case _                 => report.errorAndAbort(s"Unsupported constraint predicate: ${predicate.show}")
        }
    }
  }

  /** Whether every value of `source` is one of `target`, merging target intervals that meet. */
  private def contained(
      single: Boolean,
      source: Option[List[(low: Double, high: Double)]],
      target: Option[List[(low: Double, high: Double)]]
  ): Boolean = (source, target) match {
    case (_, None)                  => true
    case (None, Some(_))            => false
    case (Some(inner), Some(outer)) =>
      val merged =
        outer.sortBy(_.low)(using Ordering.Double.TotalOrdering).foldLeft(List.empty[(low: Double, high: Double)]) {
          case (last :: rest, next) if next.low <= after(single)(last.high) =>
            (last.low, math.max(last.high, next.high)) :: rest
          case (done, next) => next :: done
        }
      inner.forall(interval => merged.exists(cover => cover.low <= interval.low && interval.high <= cover.high))
  }

  /** The domain serialisers describe, the same one validation checks. */
  private[gemstone] def numericDomain[A: Type, P: Type](using Quotes): Domain =
    domain(quotes.reflect.TypeRepr.of[A], quotes.reflect.TypeRepr.of[P])

  private def proven(using
      q: Quotes
  )(a: q.reflect.TypeRepr, p: q.reflect.TypeRepr, b: q.reflect.TypeRepr, r: q.reflect.TypeRepr)
      : Boolean = {
    val covered = (domain(a, p), domain(b, r)) match {
      case (Domain.Integers(source), Domain.Integers(target))      => source.forall(coveredBy(_, target))
      case (Domain.Reals(single, source), Domain.Reals(_, target)) => contained(single, source, target)
      case _                                                       => false
    }
    (a =:= b || widens(a, b)) && covered
  }

  private def prove[A: Type, P: Type, B: Type, Q: Type](using Quotes): Unit = {
    import quotes.reflect.*
    if (!proven(TypeRepr.of[A], TypeRepr.of[P], TypeRepr.of[B], TypeRepr.of[Q]))
      report.errorAndAbort(
        s"Cannot prove ${Type.show[A]} where ${Type.show[P]} implies ${Type.show[B]} where ${Type.show[Q]}; retain the producer refinement, or validate the original boundary value with .refine after explicitly converting its carrier"
      )
  }

  /** The single proven conversion behind implicit widening and `.widen`. */
  @SuppressWarnings(Array("scalafix:DisableSyntax.asInstanceOf"))
  def convert[A: Type, P: Type, B: Type, Q: Type](value: Expr[constraint.where[A, P]])(using
      Quotes
  ): Expr[constraint.where[B, Q]] = {
    import quotes.reflect.*
    import constraint.*
    prove[A, P, B, Q]
    val source    = '{ $value.raw }
    val converted = (Type.of[A], Type.of[B]) match {
      case _ if TypeRepr.of[A] =:= TypeRepr.of[B] => source
      case ('[Byte], '[Short])                    => '{ ${ source.asExprOf[Byte] }.toShort }
      case ('[Byte], '[Int])                      => '{ ${ source.asExprOf[Byte] }.toInt }
      case ('[Byte], '[Long])                     => '{ ${ source.asExprOf[Byte] }.toLong }
      case ('[Short], '[Int])                     => '{ ${ source.asExprOf[Short] }.toInt }
      case ('[Short], '[Long])                    => '{ ${ source.asExprOf[Short] }.toLong }
      case ('[Int], '[Long])                      => '{ ${ source.asExprOf[Int] }.toLong }
      case _ => report.errorAndAbort(s"Cannot widen representation ${Type.show[A]} to ${Type.show[B]}")
    }
    // `where` is an opaque alias of its carrier, so this changes only the static type.
    '{ ${ converted.asExprOf[B] }.asInstanceOf[B where Q] }
  }

  def widen[A: Type, P: Type, R: Type](value: Expr[constraint.where[A, P]])(using Quotes): Expr[R] = {
    import quotes.reflect.*
    import constraint.*
    TypeRepr.of[R].dealias match {
      case AppliedType(base, List(b, q)) if base.typeSymbol == TypeRepr.of[where[Any, Any]].typeSymbol =>
        if (!(TypeRepr.of[A] =:= b) && !widens(TypeRepr.of[A], b))
          report.errorAndAbort(s"Cannot widen representation ${Type.show[A]} to ${b.show}")
        // Quote the call so the target predicate is normalised before the proof runs.
        b.asType match {
          case '[b] =>
            q.asType match { case '[q] => ascribed[R]('{ constraint.convert[A, P, b, q]($value) }) }
        }
      case _ => report.errorAndAbort("Supply a complete numeric refinement target, for example value.widen[Int >= 0]")
    }
  }

  /** Peel only our wrappers; collection elements and unrelated opaque types remain intact. */
  @SuppressWarnings(Array("scalafix:DisableSyntax.asInstanceOf"))
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
      // All recognised wrappers are opaque aliases of their carrier, so this changes only the static type.
      case '[b] => '{ $value.asInstanceOf[b] }
    }
  }

  // The compiler checks an expansion against the expected type without dealiasing aliases of opaque types, so the
  // expansion carries the expected type itself.
  private def ascribed[T: Type](expr: Expr[Any])(using Quotes): Expr[T] = '{ ${ expr.asExprOf[T] }: T }

  def unsafe[A: Type, R: Type](value: Expr[A])(using Quotes): Expr[R] = ascribed[R](check[A, R](value, throwing = true))

  def refine[A: Type, R: Type](value: Expr[A])(using Quotes): Expr[Either[constraint.NonEmpty[String], R]] =
    ascribed[Either[constraint.NonEmpty[String], R]](check[A, R](value, throwing = false))

  /** `.unsafe` is `.refine` with the failure thrown; both share the generated predicate and its description. */
  @SuppressWarnings(Array("scalafix:DisableSyntax.asInstanceOf"))
  private def check[A: Type, R: Type](value: Expr[A], throwing: Boolean)(using Quotes): Expr[Any] = {
    import quotes.reflect.*
    import constraint.*
    @annotation.tailrec
    def unrefined(tpe: TypeRepr): TypeRepr = tpe.dealias match {
      case AppliedType(base, List(a, _)) if base.typeSymbol == TypeRepr.of[where[Any, Any]].typeSymbol => unrefined(a)
      case other                                                                                       => other
    }
    val source                    = unrefined(TypeRepr.of[A].widen)
    def compatible[B: Type]: Unit =
      if (!(source <:< TypeRepr.of[B]))
        report.errorAndAbort(
          s"Cannot refine ${Type.show[A]} as ${Type.show[R]}: expected representation ${Type.show[B]}"
        )
    TypeRepr.of[R].dealias match {
      case AppliedType(base, List(a, p)) if base.typeSymbol == TypeRepr.of[constraint.where[Any, Any]].typeSymbol =>
        (a.asType, p.asType) match {
          case ('[a], '[p]) =>
            compatible[a]
            TypeRepr.of[A].widen.dealias match {
              case AppliedType(refined, List(b, q))
                  if refined.typeSymbol == TypeRepr.of[where[Any, Any]].typeSymbol && proven(b, q, a.dealias, p) =>
                val target = TypeRepr.of[R].show(using Printer.TypeReprShortCode)
                report.errorAndAbort(
                  s"${TypeRepr.of[A].show(using Printer.TypeReprShortCode)} already implies $target, so the runtime check cannot fail; use .widen[$target]"
                )
              case _ =>
            }
            // `where` is an opaque alias of its carrier, so this changes only the static type.
            val carried = '{ $value.asInstanceOf[a] }
            if (throwing) '{ where.validate[a, p]($carried) } else '{ where.check[a, p]($carried) }
          case _ => report.errorAndAbort("Unsupported refinement target")
        }
      case AppliedType(base, List(a)) if base.typeSymbol == TypeRepr.of[constraint.NonEmpty[Any]].typeSymbol =>
        a.asType match {
          case '[a] =>
            compatible[a]
            Expr.summon[HasElements[a]] match {
              case Some(elements) =>
                if (throwing) '{ NonEmpty.validate[a](${ value.asExprOf[a] })(using $elements) }
                else '{ NonEmpty.check[a](${ value.asExprOf[a] })(using $elements) }
              case None => report.errorAndAbort(s"No immutable nonempty validation for ${Type.show[a]}")
            }
          case _ => report.errorAndAbort("Unsupported nonempty target")
        }
      case target: TypeRef if target.name == "Type" =>
        val owner = target.qualifier match {
          case owner: TermRef => Ref(owner.termSymbol)
          case _              => report.errorAndAbort("Newtype must have a stable companion")
        }
        target.qualifier.baseType(TypeRepr.of[Newtype[Any]].typeSymbol) match {
          case AppliedType(_, List(base)) =>
            base.asType match {
              case '[c] =>
                val carried = TypeRepr.of[A].widen.dealias <:< TypeRepr.of[c]
                if (throwing) {
                  val checked = if (carried) value.asExprOf[c] else check[A, c](value, throwing = true).asExprOf[c]
                  Select.overloaded(owner, "apply", Nil, List(checked.asTerm)).asExpr
                } else {
                  def attempt(checked: Expr[c]): Expr[Either[NonEmpty[String], R]] =
                    Select.unique(owner, "attempt").appliedTo(checked.asTerm).asExprOf[Either[NonEmpty[String], R]]
                  if (carried) attempt(value.asExprOf[c])
                  else {
                    val refined = check[A, c](value, throwing = false).asExprOf[Either[NonEmpty[String], c]]
                    '{ $refined.flatMap(checked => ${ attempt('checked) }) }
                  }
                }
              case _ => report.errorAndAbort("Unsupported Newtype representation")
            }
          case _ => report.errorAndAbort("Supply a refinement or Newtype target")
        }
      case _ => report.errorAndAbort("Supply a supported refinement target, for example value.refine[Int > 0]")
    }
  }

  /** A comparison's `CanEqual` search sees both operands widened, so whether one is an integer literal shows only in
    * the source: at the comparison's start when `leading`, otherwise after its first top-level `==` or `!=`.
    */
  def literalComparison[L: Type, R: Type](leading: Boolean)(using Quotes): Expr[CanEqual[L, R]] = {
    import quotes.reflect.*
    val position = Position.ofMacroExpansion
    val literal  = position.sourceFile.content.exists { text =>
      val operand = if (leading) Some(position.start) else comparedOperand(text, position.start)
      operand.flatMap(numeral(text, _)).exists(end => !continued(text, end))
    }
    if (!literal)
      report.errorAndAbort(s"${Type.show[L]} and ${Type.show[R]} compare only with an integer literal operand")
    '{ CanEqual.derived }
  }

  private val operatorCharacters = "!#%&*+-/:<=>?@\\^|~"
  private val boundaries         = Set("=>", "=", "<-", "|", "?=>")
  private val clauses            = Set("if", "then", "else", "match", "case", "do", "yield", "while", "for", "with")

  private def found(index: Int, text: String): Int = if (index < 0) text.length else index

  private def operatorEnd(text: String, from: Int): Int =
    found(text.indexWhere(!operatorCharacters.contains(_), from), text)

  private def lineEnd(text: String, from: Int): Int = found(text.indexOf('\n', from), text)

  private def commentEnd(text: String, from: Int): Int = {
    val end = text.indexOf("*/", from + 2)
    if (end < 0) text.length else end + 2
  }

  @annotation.tailrec
  private def whitespaceEnd(text: String, from: Int): Int =
    if (from >= text.length) from
    else if (text(from).isWhitespace) whitespaceEnd(text, from + 1)
    else if (text.startsWith("//", from)) whitespaceEnd(text, lineEnd(text, from))
    else if (text.startsWith("/*", from)) whitespaceEnd(text, commentEnd(text, from))
    else from

  @annotation.tailrec
  private def stringEnd(text: String, from: Int): Int =
    if (from >= text.length || text(from) == '\n') from
    else if (text(from) == '\\') stringEnd(text, from + 2)
    else if (text(from) == '"') from + 1
    else stringEnd(text, from + 1)

  /** The start of the right operand of the first `==` or `!=` outside brackets, stopping where the left operand must
    * have ended.
    */
  @annotation.tailrec
  private def comparedOperand(text: String, from: Int, depth: Int = 0): Option[Int] =
    if (from >= text.length) None
    else
      text(from) match {
        case '\n' | ',' | ';' if depth == 0 => None
        case '(' | '[' | '{'                => comparedOperand(text, from + 1, depth + 1)
        case ')' | ']' | '}'                => if (depth == 0) None else comparedOperand(text, from + 1, depth - 1)
        case '"' if text.startsWith("\"\"\"", from) =>
          comparedOperand(text, found(text.indexOf("\"\"\"", from + 3), text) + 3, depth)
        case '"'                                     => comparedOperand(text, stringEnd(text, from + 1), depth)
        case '\'' if text.startsWith("\\", from + 1) =>
          comparedOperand(text, found(text.indexOf('\'', from + 3), text) + 1, depth)
        case '\'' if text.startsWith("'", from + 2) => comparedOperand(text, from + 3, depth)
        case '`' => comparedOperand(text, found(text.indexOf('`', from + 1), text) + 1, depth)
        case '/' if text.startsWith("//", from)                  => comparedOperand(text, lineEnd(text, from), depth)
        case '/' if text.startsWith("/*", from)                  => comparedOperand(text, commentEnd(text, from), depth)
        case character if operatorCharacters.contains(character) =>
          val end      = operatorEnd(text, from)
          val operator = text.substring(from, end)
          if (depth == 0 && (operator == "==" || operator == "!=")) Some(end)
          else if (depth == 0 && boundaries.contains(operator)) None
          else comparedOperand(text, end, depth)
        case character if character.isLetter || character == '_' || character == '$' =>
          val word = found(text.indexWhere(c => !(c.isLetterOrDigit || c == '_' || c == '$'), from), text)
          val end  = if (text(word - 1) == '_') operatorEnd(text, word) else word
          if (depth == 0 && clauses.contains(text.substring(from, end))) None else comparedOperand(text, end, depth)
        case _ => comparedOperand(text, from + 1, depth)
      }

  /** The end of a decimal or hexadecimal integer literal, optionally negative, after any whitespace from `from`. */
  private def numeral(text: String, from: Int): Option[Int] = {
    val start                   = whitespaceEnd(text, from)
    val sign                    = if (text.startsWith("-", start)) start + 1 else start
    val hexadecimal             = text.startsWith("0x", sign) || text.startsWith("0X", sign)
    val digits                  = if (hexadecimal) sign + 2 else sign
    def digit(c: Char): Boolean = if (hexadecimal) Character.digit(c, 16) >= 0 else c.isDigit
    val end                     = found(text.indexWhere(c => !(c == '_' || digit(c)), digits), text)
    Option.when(digits < text.length && digit(text(digits)))(
      if (text.startsWith("L", end) || text.startsWith("l", end)) end + 1 else end
    )
  }

  /** Whether what follows a literal ending at `end` binds tighter than `==`, making the literal part of an operand. */
  private def continued(text: String, end: Int): Boolean =
    if (end < text.length && (text(end).isLetterOrDigit || text(end) == '_' || text(end) == '.')) true
    else {
      val next = whitespaceEnd(text, end)
      if (next >= text.length) false
      else if (".([{".contains(text(next))) true
      else if (operatorCharacters.contains(text(next))) {
        val operator = text.substring(next, operatorEnd(text, next))
        !(operator == "!=" || "=|&^".contains(operator.head))
      } else false
    }

  def checkNumericLiteral[A: Type, P: Type](value: Expr[A])(using Quotes): Expr[Unit] = {
    import quotes.reflect.*
    val number: Option[Either[BigInt, Double]] = Type.of[A] match {
      case '[Byte]   => value.asExprOf[Byte].value.map(value => Left(BigInt(value.toInt)))
      case '[Short]  => value.asExprOf[Short].value.map(value => Left(BigInt(value.toInt)))
      case '[Int]    => value.asExprOf[Int].value.map(value => Left(BigInt(value)))
      case '[Long]   => value.asExprOf[Long].value.map(value => Left(BigInt(value)))
      case '[Float]  => value.asExprOf[Float].value.map(value => Right(value.toDouble))
      case '[Double] => value.asExprOf[Double].value.map(Right(_))
      case _         =>
        report.errorAndAbort(
          "Numeric refinement literals require the matching Byte, Short, Int, Long, Float or Double representation"
        )
    }
    val checked = number.getOrElse(report.errorAndAbort(
      "Runtime inputs require .refine at the domain boundary; propagate already-refined values directly"
    ))
    val admitted = (domain(TypeRepr.of[A], TypeRepr.of[P]), checked) match {
      case (Domain.Integers(spans), Left(literal))            => spans.exists(_.contains(literal))
      case (Domain.Reals(_, None), Right(_))                  => true
      case (Domain.Reals(_, Some(intervals)), Right(literal)) =>
        intervals.exists(interval => interval.low <= literal && literal <= interval.high)
      case _ => false
    }
    if (!admitted)
      report.errorAndAbort(s"${checked.merge} does not satisfy ${Type.show[A]} where ${Type.show[P]}")
    '{ () }
  }

  private val directive = "%(?:[0-9]+\\$)?[-#+ 0,(<]*[0-9]*(?:\\.[0-9]+)?[tT]?[a-zA-Z%]".r

  def checkStringLiteral(value: Expr[String])(using Quotes): Expr[Unit] = {
    import quotes.reflect.*
    import constraint.*
    def interpolation(parts: Seq[String], formatted: Boolean): Boolean = {
      val fixed = if (formatted) parts.map(directive.replaceAllIn(_, "")) else parts
      fixed.exists(_.nonEmpty)
    }
    def nonEmpty(expression: Expr[String]): Boolean = expression.value match {
      case Some(text) => text.nonEmpty
      case None       => expression match {
          case '{ StringContext(${ Varargs(parts) }*).s(${ Varargs(_) }*) } =>
            interpolation(parts.map(part => StringContext.processEscapes(part.valueOrAbort)), formatted = false)
          case '{ ($left: String) + ($right: String) } => nonEmpty(left) || nonEmpty(right)
          case _                                       => expression.asTerm.underlyingArgument match {
              case Apply(TypeApply(Select(context, "f"), _), _) =>
                context.asExpr match {
                  case '{ StringContext(${ Varargs(parts) }*) } =>
                    interpolation(parts.map(part => StringContext.processEscapes(part.valueOrAbort)), formatted = true)
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

  /** An unsigned 64-bit carrier's values above `Long.MaxValue` read as negative, so spans reaching them compare
    * unsigned.
    */
  private def admits(spans: List[Span], number: Expr[Long])(using Quotes): Expr[Boolean] = spans.map { span =>
    val low  = Expr(span.low.toLong)
    val high = Expr(span.high.toLong)
    if (span.low == span.high) '{ $number == $low }
    else if (span.step > BigInt(Long.MaxValue)) '{ $number == $low || $number == $high }
    else if (span.high > BigInt(Long.MaxValue)) {
      val bounded =
        '{ java.lang.Long.compareUnsigned($number, $low) >= 0 && java.lang.Long.compareUnsigned($number, $high) <= 0 }
      if (span.step == 1) bounded
      else '{ $bounded && java.lang.Long.remainderUnsigned($number, ${ Expr(span.step.toLong) }) == 0L }
    } else if (span.step == 1) '{ $number >= $low && $number <= $high }
    else '{ $number >= $low && $number <= $high && $number % ${ Expr(span.step.toLong) } == 0L }
  }.foldRight(Expr(false))((check, rest) => '{ $check || $rest })

  /** Comparisons a NaN fails, so it satisfies no interval. */
  private def admitsReal(intervals: List[(low: Double, high: Double)], number: Expr[Double])(using
      Quotes
  ): Expr[Boolean] =
    intervals.map { interval =>
      val low  = Expr(interval.low)
      val high = Expr(interval.high)
      if (interval.low == interval.high) '{ $number == $low }
      else if (interval.low == Double.NegativeInfinity) '{ $number <= $high }
      else if (interval.high == Double.PositiveInfinity) '{ $number >= $low }
      else '{ $number >= $low && $number <= $high }
    }.foldRight(Expr(false))((check, rest) => '{ $check || $rest })

  def accepts[A: Type, P: Type](value: Expr[A])(using Quotes): Expr[Boolean] = {
    import quotes.reflect.*
    domain(TypeRepr.of[A], TypeRepr.of[P]) match {
      case Domain.Integers(spans) =>
        // A view call would box a primitive carrier.
        val converted = Type.of[A] match {
          case '[Byte]  => '{ ${ value.asExprOf[Byte] }.toLong }
          case '[Short] => '{ ${ value.asExprOf[Short] }.toLong }
          case '[Int]   => '{ ${ value.asExprOf[Int] }.toLong }
          case '[Long]  => value.asExprOf[Long]
          case _        =>
            val instance = view(TypeRepr.of[A]).map(_._2.asExprOf[constraint.IntegerView[A, ?, ?]]).getOrElse(
              report.errorAndAbort(s"No IntegerView for ${Type.show[A]}")
            )
            '{ $instance.toLong($value) }
        }
        '{
          val number = $converted
          ${ admits(spans, 'number) }
        }
      case Domain.Reals(_, None)            => '{ true }
      case Domain.Reals(_, Some(intervals)) =>
        val converted = Type.of[A] match {
          case '[Float] => '{ ${ value.asExprOf[Float] }.toDouble }
          case _        => value.asExprOf[Double]
        }
        '{
          val number = $converted
          ${ admitsReal(intervals, 'number) }
        }
    }
  }
}
