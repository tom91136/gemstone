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
    * Reading a name that is neither declared nor assigned is an error. Each assignment to a name in `kept` stays a
    * binding of its own, as long sums that later steps read several times need to.
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
    val body                     = Vec.newBuilder[Statement]
    val taken                    = mutable.HashSet.from(steps.map(_.name) ++ declared.map(_.name))
    val ordered                  = steps.exists(_.value.hasEffect)
    val indices                  = Iterator.from(0)
    @tailrec def fresh(): String = {
      val name = s"v${indices.next()}"
      if (taken.add(name)) name else fresh()
    }
    var known: Map[String, Type] = types
    val undeclared               = steps.foldLeft(Option.empty[String]) { (failure, step) =>
      failure.orElse {
        val expanded = step.value.expand
        expanded.variables.filterNot(values.contains).find(!types.contains(_)) match {
          case Some(missing) => Some(s"$name reads $missing, which is neither declared nor assigned")
          case None          =>
            expanded.variables.filterNot(values.contains).foreach(inputs += _)
            expanded.contexts.foreach((context, tpe) => contexts(context) = tpe)
            val value = expanded.substitute(values.get)
            if (value.hasEffect || (ordered && value.contexts.nonEmpty) || kept(step.name)) value match {
              case Term.Context(result = None) | Term.Select(_, Term.Context(result = None), Term.Constant("()")) =>
                body += Statement(Target.Effect, Typer.typedTerm(value, known))
              case _ =>
                val binding        = fresh()
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
      }
    }
    undeclared.toLeft {
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
