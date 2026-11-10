package net.kurobako.gemstone.isa.check

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.isa.check.Equivalence.*
import net.kurobako.gemstone.isa.check.Equivalence.Verdict
import net.kurobako.gemstone.semantics.render.*
import net.kurobako.gemstone.semantics.render.Verdict as Solved
import scala.collection.mutable
import scala.concurrent.duration.*

/** Equivalence proofs: the differential filter, then one definition per sequence for a solver, over the lane cases
  * whose rendered semantics are checked against the VMs.
  */
object Proof {

  /** An instruction a proof relies on, and the runs of the differential check that its definition passed. */
  case class Cover(instance: String, target: Option[String], runs: Int)

  /** `filtered` is the number of inputs the filter ran on both sequences. */
  case class Report(instructions: Vec[Cover], filtered: Int)

  enum Source derives CanEqual {
    case Filter, Solver
  }

  enum Result derives CanEqual {
    case Proved(report: Report)
    case Rejected(counterexample: Counterexample, by: Source)
    case Refused(reason: String)
    case Undecided(reason: String, filtered: Int)
    case Unavailable(filtered: Int)

    /** The solver tells the sequences apart on `model` and the VMs do not, so the VMs or the rendered semantics are
      * wrong.
      */
    case Disagreement(model: Vec[(String, Long)], filtered: Int)
  }

  type Solver = (Definition, Definition, Boolean) => Solved

  val Limit: FiniteDuration = 30.seconds

  val bitwuzla: Solver = Bitwuzla.equivalence(_, _, _, Limit)

  private val Entry = raw"\(\s*(?:\|([^|]*)\||([^\s()|]+))\s+#([bx])([0-9a-fA-F]+)\s*\)".r

  def prove[H, L <: Lookup](
      reference: Reference[H, L],
      executor: (Case, Vec[Long], H) => Either[String, Vec[Long]],
      lane: Vec[Case],
      left: Vec[Step],
      right: Vec[Step],
      liveOut: LiveOut,
      solver: Solver = bitwuzla,
      runs: Int = DifferentialSuite.Runs
  ): Result = Equivalence.check(reference, executor, left, right, liveOut, runs) match {
    case Verdict.Rejected(counterexample) => Result.Rejected(counterexample, Source.Filter)
    case Verdict.Refused(reason)          => Result.Refused(reason)
    case Verdict.Undecided(reason)        => Result.Undecided(reason, 0)
    case Verdict.NotRejected(filtered)    =>
      uncovered(lane, left ++ right) match {
        case Some(kase) => Result.Refused(s"${describe(kase)} is not a case of the differential lane")
        case None       =>
          validate(left.iterator ++ right.iterator, liveOut) match {
            case Left(reason) => Result.Refused(reason)
            case Right(types) =>
              val (l, r) = (compose(left, liveOut, types), compose(right, liveOut, types))
              solver(l, r, liveOut.memory) match {
                case Solved.Equal                  => Result.Proved(report(left ++ right, filtered))
                case Solved.Unavailable            => Result.Unavailable(filtered)
                case Solved.Undecided(reason)      => Result.Undecided(reason, filtered)
                case Solved.Different(description) =>
                  counterexample(
                    reference,
                    executor,
                    left,
                    right,
                    liveOut,
                    types,
                    description,
                    touchesMemory(l, r),
                    filtered
                  )
              }
          }
      }
  }

  /** One definition of the whole sequence, over its live-in locations, producing the live-out ones. */
  def compose(steps: Vec[Step], liveOut: LiveOut, types: Map[String, Type]): Definition = {
    val used     = mutable.HashSet.from(types.keys)
    val indices  = Iterator.from(0)
    val inputs   = mutable.LinkedHashSet.empty[String]
    val latest   = mutable.HashMap.empty[String, Term]
    val contexts = mutable.LinkedHashMap.empty[String, String]
    val body     = Vec.newBuilder[Definition.Statement]
    val required = Vec.newBuilder[Term]

    def fresh(base: String): String = Iterator.continually(s"${base}_${indices.next()}").find(used.add).get

    def read(location: String): Term = latest.getOrElse(location, { inputs += location; Term.Variable(location) })

    steps.foreach { step =>
      val definition = step.kase.definition
      val operands   = definition.parameters.map { parameter =>
        parameter.name ->
          (step.parameters(parameter.name) match {
            case Operand.Location(location) => read(location)
            case Operand.Immediate(value)   => typed(Term.Literal(value), parameter.tpe)
          })
      }.toMap
      val scope = definition.body.foldLeft(operands) { (scope, statement) =>
        val term = rewrite(statement.term, scope, fresh(_))
        statement.target match {
          case Definition.Target.Binding(name) =>
            val binding = fresh(name)
            body += Definition.Statement(Definition.Target.Binding(binding), term)
            scope.updated(name, Term.Variable(binding))
          case Definition.Target.Method(name) =>
            val binding = fresh(name)
            body += Definition.Statement(Definition.Target.Method(binding), term)
            scope.updated(name, Term.Variable(binding))
          case Definition.Target.Effect =>
            body += Definition.Statement(Definition.Target.Effect, term)
            scope
        }
      }
      required ++= definition.requirements.map(rewrite(_, scope, fresh(_)))
      contexts ++= definition.contexts.map(context => (context.name, context.tpe))
      val written = definition.results.map { result =>
        val binding = fresh(result.name)
        body += Definition.Statement(
          Definition.Target.Binding(binding),
          typed(rewrite(result.value, scope, fresh(_)), result.tpe)
        )
        step.results(result.name) -> Term.Variable(binding)
      }
      latest ++= written
    }

    val results = liveOut.locations.map(location => (location, types(location), read(location)))
    Definition(
      "sequence",
      inputs.toVec.map(location => (location, types(location))),
      contexts.toVec,
      required.result(),
      body.result(),
      results
    )
  }

  private def typed(term: Term, tpe: Type): Term = tpe.unranged match {
    case Type.Integer(width, signed) => Term.Call(s"${if (signed) "SInt" else "UInt"}[$width]", Vec(term))
    case _                           => term
  }

  private def rewrite(term: Term, scope: Map[String, Term], fresh: String => String): Term = term match {
    case Term.Variable(name)         => scope.getOrElse(name, term)
    case Term.Let(name, value, body) =>
      val renamed = fresh(name)
      Term.Let(
        renamed,
        rewrite(value, scope, fresh),
        rewrite(body, scope.updated(name, Term.Variable(renamed)), fresh)
      )
    case Term.Each(variable, source, body) =>
      val renamed = fresh(variable)
      Term.Each(
        renamed,
        rewrite(source, scope, fresh),
        statements(body, scope.updated(variable, Term.Variable(renamed)), fresh)
      )
    case Term.When(condition, body) =>
      Term.When(rewrite(condition, scope, fresh), statements(body, scope, fresh))
    case _ => term.mapChildren(rewrite(_, scope, fresh))
  }

  private def statements(body: Vec[Term.Step], scope: Map[String, Term], fresh: String => String): Vec[Term.Step] =
    body.foldLeft((scope, Vec.empty[Term.Step])) { case ((known, done), step) =>
      val value = rewrite(step.value, known, fresh)
      if (step.name.isEmpty) (known, done :+ Term.Step("", value))
      else {
        val renamed = fresh(step.name)
        (known.updated(step.name, Term.Variable(renamed)), done :+ Term.Step(renamed, value))
      }
    }._2

  private def uncovered(lane: Vec[Case], steps: Vec[Step]): Option[Case] = {
    val indexed = lane.groupBy(kase => (kase.instance, kase.target))
    steps.map(_.kase).find(kase => !indexed.getOrElse((kase.instance, kase.target), Vec.empty[Case]).contains(kase))
  }

  private def describe(kase: Case): String = s"${kase.instance}${kase.target.fold("")(target => s" on $target")}"

  private def report(steps: Vec[Step], filtered: Int): Report = Report(
    steps.map(_.kase).distinctBy(kase => (kase.instance, kase.target)).map(kase =>
      Cover(kase.instance, kase.target, DifferentialSuite.Runs)
    ),
    filtered
  )

  private def touchesMemory(definitions: Definition*): Boolean = definitions.exists(definition =>
    (definition.body.map(_.term) ++ definition.requirements).exists(term =>
      terms(term).exists {
        case _: Term.Context => true
        case _               => false
      }
    )
  )

  private def counterexample[H, L <: Lookup](
      reference: Reference[H, L],
      executor: (Case, Vec[Long], H) => Either[String, Vec[Long]],
      left: Vec[Step],
      right: Vec[Step],
      liveOut: LiveOut,
      types: Map[String, Type],
      description: String,
      memory: Boolean,
      filtered: Int
  ): Result = {
    val model = Entry.findAllMatchIn(description).map { found =>
      val name = Option(found.group(1)).getOrElse(found.group(2))
      name -> java.lang.Long.parseUnsignedLong(found.group(4), if (found.group(3) == "b") 2 else 16)
    }.toMap
    val inputs  = model.collect { case (name, value) if types.contains(name) => name -> types(name).represent(value) }
    val outside = inputs.collectFirst {
      case (name, value) if !inRange(types(name), value) => name
    }
    outside match {
      case Some(name) => Result.Undecided(s"the solver's value for $name is outside its range", filtered)
      case None       =>
        Equivalence.replay(reference, executor, left, right, liveOut, inputs) match {
          case Verdict.Rejected(found)          => Result.Rejected(found, Source.Solver)
          case Verdict.NotRejected(_) if memory =>
            Result.Undecided("the solver's model does not cover the initial memory the sequences read", filtered)
          case Verdict.NotRejected(_)    => Result.Disagreement(Vec.from(inputs.toSeq.sortBy(_._1)), filtered)
          case Verdict.Undecided(reason) => Result.Undecided(reason, filtered)
          case Verdict.Refused(reason)   => Result.Refused(reason)
        }
    }
  }

  private def inRange(tpe: Type, value: Long): Boolean = tpe match {
    case Type.Ranged(_, intervals) => intervals.exists((low, high) => value >= low && value <= high)
    case _                         => true
  }
}
