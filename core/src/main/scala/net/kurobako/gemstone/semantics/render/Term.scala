package net.kurobako.gemstone.semantics.render

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.constraint.*
import scala.annotation.targetName
import scala.annotation.threadUnsafe
import scala.collection.mutable

object Term {

  private val Extension = raw"(zext|sext)(\d+)".r

  private[gemstone] def simple(path: String): String = path.split('.').last

  /** `value` in decimal when it is small, unless it is all ones in at least 8 bits, and in hexadecimal otherwise. */
  private[gemstone] def literal(value: Long): String =
    if (value >= 0 && value < 4096 && !(value >= 0xff && (value & (value + 1)) == 0L)) value.toString
    else if (value > -4096 && value < 0) value.toString
    else if (value < 0) s"-0x${java.lang.Long.toHexString(-value)}"
    else s"0x${java.lang.Long.toHexString(value)}"

  /** An extension of a literal, evaluated. */
  def fold(term: Term): Term = term match {
    case Call(function, Vec(Literal(value))) =>
      function match {
        case Extension(kind, bits) =>
          bits.toInt.refine[Int in 1 ~ 64].fold(
            _ => term,
            width => Literal(if (kind == "zext") Bits.truncate(value, width) else Bits.signExtend(value, width))
          )
        case _ => term
      }
    case _ => term
  }

  /** `body` of `value` bound to `name`, or to a variant of it where `body` or `value` already reads `name`, so the
    * binding never captures an operand.
    */
  def let(name: String, value: Term, body: Term => Term): Term = {
    val placeholder = s"$name'"
    val bound       = body(Variable(placeholder))
    val fresh       = (Iterator(name) ++ Iterator.from(1).map(index => s"$name$index"))
      .find(candidate => !names(value, candidate) && !names(bound, candidate)).getOrElse(name)
    Let(fresh, value, rename(bound, placeholder, fresh))
  }

  /** Whether `term` reads or binds `name`. */
  private def names(term: Term, name: String): Boolean = term match {
    case Variable(variable) => variable == name
    case Let(binder, _, _)  => binder == name || term.children.exists(names(_, name))
    case _                  => term.children.exists(names(_, name))
  }

  /** Whether `term` reads `name` where no binding of its own shadows it. */
  def reads(term: Term, name: String): Boolean = term match {
    case Variable(variable)       => variable == name
    case Let(binder, value, body) => reads(value, name) || (binder != name && reads(body, name))
    case _                        => term.children.exists(reads(_, name))
  }

  private def rename(term: Term, from: String, to: String): Term = term match {
    case Variable(`from`) => Variable(to)
    case _                => term.mapChildren(rename(_, from, to))
  }

  /** `method` of the context `owner` names, as its type and name, over `arguments` each of the type it gives or fixed
    * per instruction.
    */
  def context(
      owner: (contextType: String, context: String),
      method: String,
      typeArguments: Vec[Type],
      arguments: Vec[(Term, Option[Type])],
      result: Option[Type],
      effect: Boolean = false
  ): Term = Context(
    owner.contextType,
    owner.context,
    method,
    typeArguments,
    arguments.map(_._1),
    arguments.map(_._2),
    result,
    effect
  )

  def truncate(value: Term, width: Int in 1 ~ 64): Term = value match {
    case _ if width.raw == 64 => value
    case Literal(literal)     => Literal(Bits.truncate(literal, width))
    case _                    => Call(s"zext$width", Vec(value))
  }

  /** A bitwise select, folded where the sides are complements or either is all zeros or all ones. */
  def blend(mask: Term, whenSet: Term, whenClear: Term): Term = (whenSet, whenClear) match {
    case _ if whenSet == whenClear                  => whenSet
    case (Unary("~", set), _) if set == whenClear   => Binary("^", mask, whenClear)
    case (_, Unary("~", clear)) if clear == whenSet => Unary("~", Binary("^", mask, whenSet))
    case (Literal(-1L), Literal(0L))                => mask
    case (Literal(0L), Literal(-1L))                => Unary("~", mask)
    case (_, Literal(0L))                           => Binary("&", mask, whenSet)
    case (Literal(0L), _)                           => Binary("&", Unary("~", mask), whenClear)
    case (Literal(-1L), _)                          => Binary("|", mask, whenClear)
    case (_, Literal(-1L))                          => Binary("|", Unary("~", mask), whenSet)
    case _                                          => Call("blend", Vec(mask, whenSet, whenClear))
  }

  /** Subexpressions `values` share, bound once each under names from `fresh` in order of first evaluation, and the
    * values that read them.
    */
  private[gemstone] def share(
      values: Vec[Term],
      fresh: () => String
  ): (bindings: Vec[(name: String, value: Term)], values: Vec[Term]) = {
    val counts                        = mutable.HashMap.empty[Term, Int]
    def compound(term: Term): Boolean = term match {
      case Variable(_) | Literal(_) | Constant(_)                       => false
      case Call(function, Vec(Variable(_))) if function.startsWith(".") => false
      case Call(_, Vec(Literal(_)))                                     => false
      case _                                                            => true
    }
    // a division stays where it is, since a selection may guard its divisor
    val partials                     = mutable.HashMap.empty[Term, Boolean]
    def partial(term: Term): Boolean = partials.getOrElseUpdate(
      term,
      term match {
        case Binary("/" | "%", _, _) => true
        case _                       => term.children.exists(partial(_))
      }
    )
    def count(term: Term): Unit = {
      if (compound(term) && !partial(term)) counts(term) = counts.getOrElse(term, 0) + 1
      if (counts.getOrElse(term, 0) <= 1) term.children.foreach(count(_))
    }
    values.foreach(count(_))
    val bindings                  = mutable.LinkedHashMap.empty[Term, String]
    def rewrite(term: Term): Term = {
      val inner = term.mapChildren(rewrite(_))
      if (counts.getOrElse(term, 0) > 1) Variable(bindings.getOrElseUpdate(inner, fresh())) else inner
    }
    val rewritten = values.map(rewrite(_))
    (bindings.toVec.map((value, name) => (name, value)), rewritten)
  }

  /** `value` assigned to `name`. */
  case class Step(name: String, value: Term)

  object Steps {
    val empty: Steps = Steps(Vec.empty[Step])

    def apply(steps: Step*): Steps = Steps(Vec(steps*))

    /** The steps of each of `parts`, in order. */
    def concat(parts: Vec[Steps]): Steps = Steps(parts.flatMap(_.all))

    /** `value` assigned to `target`, after its bindings. */
    def assign(scope: Scope[?], target: => String, value: Term): Steps = {
      val (bindings, result) = hoisted(scope, value)
      bindings :+ Step(target, result)
    }

    /** `value`'s bindings as assignments to temporaries in the order they appear, and what remains of it. Bindings are
      * pure, so each can be assigned before the expression that holds it.
      */
    def hoisted(scope: Scope[?], value: Term): (bindings: Steps, value: Term) = {
      val steps                   = Vec.newBuilder[Step]
      def hoist(term: Term): Term = term match {
        case Let(name, bound, body) =>
          val hoisted   = hoist(bound)
          val temporary = scope.temporary()
          steps += Step(temporary, hoisted)
          hoist(body.substitute(other => Option.when(other == name)(Variable(temporary))))
        case _ => term.mapChildren(hoist(_))
      }
      val result = hoist(value)
      (Steps(steps.result()), result)
    }

    /** `values` assigned to `targets` in order, each read before any is written. */
    def parallel(scope: Scope[?], targets: Vec[String], values: Vec[Term]): Steps =
      if (values.exists(value => targets.exists(reads(value, _)))) {
        val temporaries = values.map(_ => scope.temporary())
        concat(temporaries.zip(values).map((temporary, value) => assign(scope, temporary, value))) ++
          Steps(targets.zip(temporaries).map((target, temporary) => Step(target, Variable(temporary))))
      } else concat(targets.zip(values).map((target, value) => assign(scope, target, value)))

    /** A result into `target` and the flag `next` computes from it into `flag`. When an operand reads the target, the
      * result goes through a temporary, so the flag sees the operands' old values.
      */
    inline def flagged(scope: Scope[?], target: => String, flag: => String, value: Term, operands: Vec[Term])(
        inline next: Term => Term
    ): Steps = {
      val (bindings, result) = hoisted(scope, value)
      val written            = target
      if (operands.exists(reads(_, written))) {
        val temporary = scope.temporary()
        (bindings :+ Step(temporary, result)) ++ assign(scope, flag, next(Variable(temporary))) :+
          Step(written, Variable(temporary))
      } else (bindings :+ Step(written, result)) ++ assign(scope, flag, next(Variable(written)))
    }

    /** The steps `steps` gives, in a method of their own so no expansion grows too large. */
    inline def lifted(inline steps: Steps): Option[Steps] = {
      def rendered(): Option[Steps] = Some(steps)
      rendered()
    }
  }

  /** Semantics rendered as assignments in order, each of a value to a name, where later steps read the latest value. */
  case class Steps(all: Vec[Step]) {
    def last: Step = all.last

    @targetName("concat")
    def ++(other: Steps): Steps = Steps(all ++ other.all)

    @targetName("append")
    def :+(step: Step): Steps = Steps(all :+ step)

    /** The assignments as pseudo-code, one a line. */
    def show: String = all.map(step => s"${step.name} = ${step.value.show}").mkString("\n")

    /** The steps with each operand's last write left at the operand's type: a register holds the low bits of a narrower
      * value zero-extended, which the operand's type already says.
      */
    def settled(types: Map[String, Type]): Steps = Steps(all.zipWithIndex.map { case (Step(name, value), index) =>
      val read = all.drop(index + 1).exists(later => reads(later.value, name))
      (types.get(name).map(_.unranged), value) match {
        case (Some(Type.Integer(width, _)), Term.Call(function, Vec(inner)))
            if !read && function == s"zext$width" => Step(name, inner)
        case _ => Step(name, value)
      }
    })
  }
}

/** A rendered semantics expression. */
enum Term derives CanEqual {
  case Variable(name: String)
  case Literal(value: Long)
  case Unary(operator: String, value: Term)
  case Binary(operator: String, left: Term, right: Term)
  case Call(function: String, arguments: Vec[Term])
  case Select(condition: Term, whenTrue: Term, whenFalse: Term)
  case Let(name: String, value: Term, body: Term)

  /** `method` of the object `owner` applied to `arguments`, each an operand of the prelude type in `parameters` or,
    * where that is absent, a value fixed per instruction; it returns a `result`.
    */
  case Primitive(owner: String, method: String, arguments: Vec[Term], parameters: Vec[Option[Type]], result: Type)

  /** A value fixed per instruction, as code. */
  case Constant(code: String)

  /** An operation of the machine a lane runs on, such as a memory access or a warp collective: `method` of the context
    * `context` of type `contextType`, which may return a `result`; an `effect` changes machine state, such as a store
    * or an atomic.
    */
  case Context(
      contextType: String,
      context: String,
      method: String,
      typeArguments: Vec[Type],
      arguments: Vec[Term],
      parameters: Vec[Option[Type]],
      result: Option[Type],
      effect: Boolean
  )

  /** The terms this one is built from, in order. */
  def children: Vec[Term] = this match {
    case Unary(_, value)                        => Vec(value)
    case Binary(_, left, right)                 => Vec(left, right)
    case Call(_, arguments)                     => arguments
    case Select(condition, whenTrue, whenFalse) => Vec(condition, whenTrue, whenFalse)
    case Let(_, value, body)                    => Vec(value, body)
    case Primitive(arguments = arguments)       => arguments
    case Context(arguments = arguments)         => arguments
    case Variable(_) | Literal(_) | Constant(_) => Vec.empty
  }

  /** This term with `function` applied to each of its [[children]] in order. */
  def mapChildren(function: Term => Term): Term = this match {
    case Unary(operator, value)                 => Unary(operator, function(value))
    case Binary(operator, left, right)          => Binary(operator, function(left), function(right))
    case Call(name, arguments)                  => Call(name, arguments.map(function))
    case Select(condition, whenTrue, whenFalse) => Select(function(condition), function(whenTrue), function(whenFalse))
    case Let(name, value, body)                 => Let(name, function(value), function(body))
    case Primitive(owner, method, arguments, parameters, result) =>
      Primitive(owner, method, arguments.map(function), parameters, result)
    case Context(contextType, context, method, typeArguments, arguments, parameters, result, effect) =>
      Context(contextType, context, method, typeArguments, arguments.map(function), parameters, result, effect)
    case Variable(_) | Literal(_) | Constant(_) => this
  }

  /** This term with each variable `names` defines replaced. */
  def substitute(names: String => Option[Term]): Term = this match {
    case Variable(name)         => names(name).getOrElse(this)
    case Call(_, _)             => Term.fold(mapChildren(_.substitute(names)))
    case Let(name, value, body) =>
      Let(name, value.substitute(names), body.substitute(other => if (other == name) None else names(other)))
    case _ => mapChildren(_.substitute(names))
  }

  /** This term with its bindings substituted, which are pure. */
  private[gemstone] def expand: Term = this match {
    case Let(name, value, body) =>
      val bound = value.expand
      body.expand.substitute(other => Option.when(other == name)(bound))
    case _ => mapChildren(_.expand)
  }

  /** The variables this term reads, in order of appearance. */
  private[gemstone] def variables: Vec[String] = {
    val found                                       = mutable.LinkedHashSet.empty[String]
    def visit(term: Term, bound: Set[String]): Unit = term match {
      case Variable(name)         => if (!bound(name)) found += name
      case Let(name, value, body) =>
        visit(value, bound)
        visit(body, bound + name)
      case _ => term.children.foreach(visit(_, bound))
    }
    visit(this, Set.empty)
    found.toVec
  }

  /** The contexts this term uses, with their types. */
  private[gemstone] def contexts: Vec[(String, String)] = {
    val found                   = mutable.LinkedHashSet.empty[(String, String)]
    def visit(term: Term): Unit = {
      term match {
        case Context(contextType = contextType, context = context) => found += ((context, contextType))
        case _                                                     => ()
      }
      term.children.foreach(visit(_))
    }
    visit(this)
    found.toVec
  }

  /** Whether this term changes machine state. */
  private[gemstone] def hasEffect: Boolean = this match {
    case Context(effect = true) => true
    case _                      => children.exists(_.hasEffect)
  }

  @threadUnsafe override lazy val hashCode: Int = scala.util.hashing.MurmurHash3.caseClassHash(this)

  /** Pseudo-code, one binding per line, with every compound operand parenthesised so no precedence table is needed. */
  def show: String = {
    def operand(term: Term): String = term match {
      case Variable(_) | Literal(_) | Call(_, _) | Unary(_, _) => term.show
      case _                                                   => s"(${term.show})"
    }
    this match {
      case Variable(name)                         => name
      case Literal(value)                         => Term.literal(value)
      case Unary(operator, value)                 => s"$operator${operand(value)}"
      case Binary(operator, left, right)          => s"${operand(left)} $operator ${operand(right)}"
      case Call(function, arguments)              => arguments.map(_.show).mkString(s"$function(", ", ", ")")
      case Select(condition, whenTrue, whenFalse) =>
        s"${operand(condition)} ? ${operand(whenTrue)} : ${operand(whenFalse)}"
      case Let(name, value, body)                    => s"$name = ${value.show}\n${body.show}"
      case Primitive(owner, method, arguments, _, _) =>
        arguments.map(_.show).mkString(s"${Term.simple(owner)}.$method(", ", ", ")")
      case Constant(code)                                                     => code
      case Context(context = context, method = method, arguments = arguments) =>
        arguments.map(_.show).mkString(s"$context.$method(", ", ", ")")
    }
  }
}
