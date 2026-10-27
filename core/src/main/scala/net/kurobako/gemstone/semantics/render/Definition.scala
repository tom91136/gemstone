package net.kurobako.gemstone.semantics.render

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import scala.annotation.tailrec
import scala.collection.mutable

object Definition {

  case class Statement(target: Target, term: Term)

  enum Target derives CanEqual {
    case Binding(name: String)

    /** A binding computed by a method of its own, which the compiler lifts out of the definition, so that no one
      * method's frame grows with the whole computation.
      */
    case Method(name: String)
    case Effect
  }

  /** The definition named `name` of `steps` over what `scope` declares, the names in `order` first, each at its last
    * place there, then in the order they first appear; a later register of an operand, such as `SRC0+1`, follows the
    * operand. Under `settle`, the steps are [[Term.Steps.settled]] first. `requirements` must hold of the operands for
    * the instruction to encode.
    */
  def from(
      steps: Term.Steps,
      name: String,
      scope: Scope[?],
      order: Vec[String],
      requirements: Vec[Term] = Vec.empty,
      settle: Boolean = false
  ): Either[String, Definition] = scope.conflicting match {
    case Vec() =>
      val declared = scope.declared(order)
      val types    = declared.map(entry => (entry.name, entry.tpe)).toMap
      of(name, (if (settle) steps.settled(types) else steps).all, declared, requirements, scope.kept)
    case names => Left(s"${names.mkString(", ")} take more than one type")
  }

  /** The definition `steps` amount to, where each step assigns its value to a name and later steps read the latest
    * value. `declared` types every operand and piece of state the instruction reads or writes, in operand order; the
    * other names the steps assign are temporaries. An effect, such as a store, or one under a condition, a selection of
    * it or `Term.Constant("()")`, is assigned to any name and kept in order with every machine operation, as is each
    * machine operation when there is an effect. `requirements` must hold of the operands for the instruction to encode.
    * Reading a name that is neither declared nor assigned is an error. A write to a declared name under a condition
    * keeps the name's previous value where the condition fails, so it is an error only in a loop or when its value or
    * condition reads a binding made under a condition. Each assignment to a name in `kept` stays a binding of its own,
    * as long sums that later steps read several times need to.
    */
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  def of(
      name: String,
      steps: Vec[Term.Step],
      declared: Vec[(name: String, tpe: Type)],
      requirements: Vec[Term] = Vec.empty,
      kept: Set[String] = Set.empty
  ): Either[String, Definition] = {
    val types                    = declared.map(entry => (entry.name, entry.tpe)).toMap
    val values                   = mutable.HashMap.empty[String, Term]
    val inputs                   = mutable.LinkedHashSet.empty[String]
    val outputs                  = mutable.LinkedHashSet.empty[String]
    val contexts                 = mutable.LinkedHashMap.empty[String, String]
    var body                     = Vec.newBuilder[Statement]
    val taken                    = mutable.HashSet.from(steps.map(_.name) ++ declared.map(_.name))
    val ordered                  = steps.exists(_.value.hasEffect)
    val indices                  = Iterator.from(0)
    @tailrec def fresh(): String = {
      val name = s"v${indices.next()}"
      if (taken.add(name)) name else fresh()
    }
    var known: Map[String, Type] = types
    val bound                    = mutable.HashSet.empty[String]
    var scopes                   = List.empty[Option[Term]]
    val scoped                   = mutable.HashSet.empty[String]

    def read(term: Term): Either[String, Term] = {
      val expanded = term.expand
      val unbound  = expanded.variables.filterNot(values.contains).filterNot(bound)
      unbound.find(!types.contains(_)) match {
        case Some(missing) => Left(s"$name reads $missing, which is neither declared nor assigned")
        case None          =>
          unbound.foreach(inputs += _)
          expanded.contexts.foreach((context, tpe) => contexts(context) = tpe)
          Right(expanded.substitute(values.get))
      }
    }

    def block(inner: Vec[Term.Step], variables: Set[String], scope: Option[Term]): Either[String, Vec[Term.Step]] = {
      val outer = body
      body = Vec.newBuilder[Statement]
      bound ++= variables
      scopes = scope :: scopes
      val failure = run(inner)
      scopes = scopes.tail
      bound --= variables
      val statements = body.result()
      body = outer
      failure.toLeft(statements.map {
        case Statement(Target.Binding(binding), value) => Term.Step(binding, value)
        case Statement(_, value)                       => Term.Step("", value)
      })
    }

    def conditioned(step: Term.Step): Option[String] =
      if (scopes.contains(None)) Some(s"$name writes ${step.name} in a loop")
      else {
        val guard = scopes.flatten.reduceLeft[Term]((left, right) => Term.Binary("&", left, right))
        (for {
          value    <- read(step.value)
          previous <- read(Term.Variable(step.name))
        } yield Term.Select(guard, value, previous)).fold(
          Some(_),
          selected =>
            if (selected.hasEffect || selected.variables.exists(scoped))
              Some(s"$name writes ${step.name} from a value under a condition")
            else {
              values(step.name) = selected
              outputs += step.name
              None
            }
        )
      }

    def one(step: Term.Step): Option[String] = step.value match {
      case Term.Each(variable, source, inner) =>
        val emitted = for {
          header <- read(source).map(Typer.typedTerm(_, known))
          nested <- block(inner, Set(variable), None)
        } yield Term.Each(variable, header, nested)
        emitted.fold(Some(_), effect => { body += Statement(Target.Effect, effect); None })
      case Term.When(condition, inner) =>
        val emitted = for {
          raw <- read(condition)
          header = Typer.condition(raw, known)
          nested <- block(inner, Set.empty, Some(raw))
        } yield Term.When(header, nested)
        emitted.fold(
          Some(_),
          {
            case Term.When(_, Vec()) => None
            case effect              =>
              body += Statement(Target.Effect, effect)
              None
          }
        )
      case _ if types.contains(step.name) && scopes.nonEmpty => conditioned(step)
      case _                                                 =>
        read(step.value).fold(
          Some(_),
          value => {
            if (value.hasEffect || (ordered && value.contexts.nonEmpty) || kept(step.name)) value match {
              case Term.Context(result = None, effect = true) |
                  Term.Select(_, Term.Context(result = None), Term.Constant("()")) =>
                body += Statement(Target.Effect, Typer.typedTerm(value, known))
              case Term.Context(result = None) =>
                val binding = fresh()
                if (scopes.nonEmpty) scoped += binding
                body += Statement(Target.Binding(binding), Typer.typedTerm(value, known))
                values(step.name) = Term.Variable(binding)
              case _ =>
                val binding = fresh()
                if (scopes.nonEmpty) scoped += binding
                val (tpe, settled) = Typer.settle(value, known, None)
                body += Statement(Target.Binding(binding), settled)
                known = known.updated(binding, tpe)
                values(step.name) = Term.Variable(binding)
                if (types.contains(step.name)) outputs += step.name
            }
            else {
              values(step.name) = value
              if (types.contains(step.name)) outputs += step.name
            }
            None
          }
        )
    }

    def run(list: Vec[Term.Step]): Option[String] =
      list.foldLeft(Option.empty[String])((failure, step) => failure.orElse(one(step)))

    run(steps).toLeft {
      val parameters = declared.filter(entry => inputs(entry.name)).map(entry => (entry.name, entry.tpe))
      val typed      = outputs.toVec.map(output => (output, Typer.settle(values(output), known, types.get(output))))
      val (shared, terms) = Term.share(typed.map(_._2.term), fresh)
      Definition(
        name,
        parameters,
        contexts.toVec,
        requirements.map(requirement => Typer.condition(requirement, known)),
        body.result() ++ shared.map((name, value) => Statement(Target.Binding(name), value)),
        typed.zip(terms).map { case ((output, (tpe, _)), term) => (output, tpe, term) }
      )
    }
  }
}

/** An instruction's semantics as a function from the operands and state it reads to those it writes, which a
  * [[Backend]] writes out. It runs against [[prelude]], the objects whose primitives it calls and the contexts it
  * takes.
  */
case class Definition(
    name: String,
    parameters: Vec[(name: String, tpe: Type)],
    contexts: Vec[(name: String, tpe: String)],
    requirements: Vec[Term],
    body: Vec[Definition.Statement],
    results: Vec[(name: String, tpe: Type, value: Term)]
) derives CanEqual
