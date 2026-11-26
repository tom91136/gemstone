package net.kurobako.gemstone.isa.check

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.isa.amdgpu.LaneMasks
import net.kurobako.gemstone.isa.check.Equivalence.*
import net.kurobako.gemstone.isa.check.Equivalence.Verdict
import net.kurobako.gemstone.semantics.Space
import net.kurobako.gemstone.semantics.render.*
import net.kurobako.gemstone.semantics.render.Verdict as Solved
import scala.annotation.tailrec
import scala.collection.mutable
import scala.concurrent.duration.*

/** Equivalence proofs: the differential filter, then one definition per sequence for a solver, over the lane cases
  * whose rendered semantics are checked against the VMs.
  */
// proof results are reported to callers and asserted by suites
@SuppressWarnings(Array("bonsai:test-only-member"))
object Proof {

  /** An instruction a proof relies on, and the runs of the differential check behind its definition. `checked` names
    * the lane cases whose runs those are, where the instruction is not itself one of them.
    */
  case class Cover(instance: String, target: Option[String], runs: Int, checked: Vec[String] = Vec.empty)

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
      runs: Int = Equivalence.Runs
  ): Result = Equivalence.check(reference, executor, left, right, liveOut, runs) match {
    case Verdict.Rejected(counterexample) => Result.Rejected(counterexample, Source.Filter)
    case Verdict.Refused(reason)          => Result.Refused(reason)
    case Verdict.Undecided(reason)        => Result.Undecided(reason, 0)
    case Verdict.NotRejected(filtered)    =>
      uncovered(lane, left ++ right) match {
        case Some(kase) => Result.Refused(s"${describe(kase)} is not a case of the differential lane")
        case None       =>
          modeFailures(left ++ right).orElse(unallocated(reference, left ++ right)) match {
            case Some(reason) => Result.Refused(reason)
            case None         =>
              validate(left.iterator ++ right.iterator, liveOut) match {
                case Left(reason) => Result.Refused(reason)
                case Right(types) =>
                  val (l, r) = (
                    bounded(compose(left, liveOut, types), reference),
                    bounded(compose(right, liveOut, types), reference)
                  )
                  solver(l, r, liveOut.memory) match {
                    case Solved.Equal                  => Result.Proved(report(left ++ right, filtered))
                    case Solved.Unavailable            => Result.Unavailable(filtered)
                    case Solved.Undecided(reason)      => Result.Undecided(reason, filtered)
                    case Solved.Different(description) =>
                      def replayed(model: String) = counterexample(
                        reference,
                        executor,
                        left,
                        right,
                        liveOut,
                        types,
                        model,
                        touchesMemory(l, r),
                        l.parameters.exists(_.name == LaneMasks.Lane) || r.parameters.exists(_.name == LaneMasks.Lane),
                        filtered
                      )
                      replayed(description) match {
                        // a model whose accesses fault on both sequences shows nothing; one that aligns them may run
                        case undecided: Result.Undecided if touchesMemory(l, r) =>
                          solver(aligned(l), aligned(r), liveOut.memory) match {
                            case Solved.Different(other) =>
                              replayed(other) match {
                                case rejected: Result.Rejected => rejected
                                case _                         => undecided
                              }
                            case _ => undecided
                          }
                        case result => result
                      }
                  }
              }
          }
      }
  }

  /** Why `steps` access shared memory that `reference` does not allocate, naming its instruction. */
  private def unallocated[H, L <: Lookup](reference: Reference[H, L], steps: Vec[Step]): Option[String] =
    steps.find(step => GlobalAccess.shares(step.kase.definition)).filter(_ =>
      reference.allocation(Space.Shared).isEmpty
    )
      .map(step => s"${step.kase.instance} accesses shared memory, which this ISA does not allocate")

  /** `definition` where the shared allocation it reads is at most the most the reference gives a workgroup. */
  private def bounded[H, L <: Lookup](definition: Definition, reference: Reference[H, L]): Definition =
    reference.allocation(Space.Shared).filter(_ => definition.parameters.exists(_.name == SmtBackend.SharedSize))
      .fold(definition)(limit =>
        definition.copy(requirements =
          definition.requirements :+ Term.Binary("<=", Term.Variable(SmtBackend.SharedSize), Term.Literal(limit))
        )
      )

  /** Why a mode of one of `steps` is not rendered, naming its instruction. */
  private def modeFailures(steps: Vec[Step]): Option[String] =
    steps.map(_.kase).map(Modes.check).collectFirst { case Left(reason) => reason }

  /** One definition of the whole sequence, over its live-in locations, producing the live-out ones. */
  def compose(steps: Vec[Step], liveOut: LiveOut, types: Map[String, Type]): Definition = {
    val lanes = steps.exists { step =>
      val definition = step.kase.definition
      (definition.requirements ++ definition.body.map(_.term) ++ definition.results.map(_.value))
        .exists(LaneMasks.uses(_))
    }
    val used     = mutable.HashSet.from(types.keys) += LaneMasks.Lane
    val indices  = Iterator.from(0)
    val inputs   = mutable.LinkedHashSet.empty[String]
    val latest   = mutable.HashMap.empty[String, Term]
    val contexts = mutable.LinkedHashMap.empty[String, String]
    val body     = Vec.newBuilder[Definition.Statement]
    val required = Vec.newBuilder[Term]

    @tailrec
    def fresh(base: String): String = {
      val candidate = s"${base}_${indices.next()}"
      if (used.add(candidate)) candidate else fresh(base)
    }

    def read(location: String): Term = latest.getOrElse(location, { inputs += location; Term.Variable(location) })

    steps.foreach { step =>
      val definition = step.kase.definition
      val operands   = definition.parameters.map { parameter =>
        parameter.name ->
          (step.parameters(parameter.name) match {
            case Operand.Location(location) => held(read(location), types(location), parameter.tpe)
            case Operand.Immediate(value)   => typed(Term.Literal(value), parameter.tpe)
            case Operand.Joined(locations)  => typed(joined(locations.map(read(_))), parameter.tpe)
          })
      }.toMap
      val scope = definition.body.foldLeft(operands) { (scope, statement) =>
        val term = rewrite(LaneMasks.plain(statement.term), scope, fresh(_))
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
      required ++= definition.requirements.map(term => rewrite(LaneMasks.plain(term), scope, fresh(_)))
      contexts ++= definition.contexts.filterNot(_.tpe == LaneMasks.contextType)
        .map(context => (context.name, context.tpe))
      val written = Vec.newBuilder[(String, Term)]
      definition.results.foreach { result =>
        val binding = fresh(result.name)
        body += Definition.Statement(
          Definition.Target.Binding(binding),
          typed(rewrite(LaneMasks.plain(result.value), scope, fresh(_)), result.tpe)
        )
        step.results(result.name) match {
          case Vec(single) => written += (single -> held(Term.Variable(binding), result.tpe, types(single)))
          case several     => several.zipWithIndex.foreach((location, index) =>
              written += (location -> unit(Term.Variable(binding), index))
            )
        }
      }
      latest ++= written.result()
    }

    val results = liveOut.locations.map(location => (location, types(location), read(location)))
    val lane    = Term.Binary("<", Term.Variable(LaneMasks.Lane), Term.Literal(LaneMasks.Lanes.toLong))
    val shares  = steps.exists(step => GlobalAccess.shares(step.kase.definition))
    Definition(
      "sequence",
      inputs.toVec.map(location => (location, types(location))) ++
        (if (lanes) Vec((LaneMasks.Lane, LaneMasks.LaneType)) else Vec.empty[(String, Type)]) ++
        (if (shares) Vec((SmtBackend.SharedSize, Form.Pair)) else Vec.empty),
      contexts.toVec,
      required.result() ++ (if (lanes) Vec(lane) else Vec.empty),
      body.result(),
      results
    )
  }

  /** `definition` over the inputs where each load and store of its statements is aligned to its width. */
  private def aligned(definition: Definition): Definition = {
    val machine  = RenderMachine.Machine.contextType
    val accesses = definition.body.flatMap(statement => Rejection.terms(statement.term)).collect {
      case Term.Context(
            contextType = `machine`,
            method = "load" | "store",
            typeArguments = Vec(tpe),
            arguments = arguments
          )
          if tpe.bits > 8 =>
        Term.Binary("==", Term.Binary("&", arguments(1), Term.Literal(tpe.bits / 8 - 1L)), Term.Literal(0L))
    }
    definition.copy(requirements = definition.requirements ++ accesses)
  }

  /** `term`, the value a location of type `from` holds, as an operand of type `to`. */
  private def held(term: Term, from: Type, to: Type): Term = if (from == to) term else typed(term, to)

  /** The value whose 32-bit units `units` are, low first. */
  private def joined(units: Vec[Term]): Term = units.zipWithIndex.map { (unit, index) =>
    val widened = typed(unit, Form.Pair)
    if (index == 0) widened else Term.Binary("<<", widened, Term.Literal(index.toLong * Form.UnitBits))
  }.reduceLeft(Term.Binary("|", _, _))

  /** The 32-bit unit `index` of `value`. */
  private def unit(value: Term, index: Int): Term = typed(
    if (index == 0) value else Term.Binary(">>", typed(value, Form.Pair), Term.Literal(index.toLong * Form.UnitBits)),
    Form.Unit
  )

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
    steps.flatMap(step => if (step.lane.cases.isEmpty) Vec(step.kase) else step.lane.cases)
      .find(kase => !indexed.getOrElse((kase.instance, kase.target), Vec.empty[Case]).contains(kase))
  }

  private def describe(kase: Case): String = s"${kase.instance}${kase.target.fold("")(target => s" on $target")}"

  private def report(steps: Vec[Step], filtered: Int): Report = Report(
    steps.distinctBy(step => (step.kase.instance, step.kase.target)).map(step =>
      Cover(step.kase.instance, step.kase.target, Equivalence.Runs, step.lane.cases.map(_.instance))
    ),
    filtered
  )

  private def touchesMemory(definitions: Definition*): Boolean = definitions.exists(definition =>
    (definition.body.map(_.term) ++ definition.requirements).exists(term =>
      Rejection.terms(term).exists {
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
      lanes: Boolean,
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
    val lane = model.get(LaneMasks.Lane).filter(_ => lanes)
    outside match {
      case Some(name) => Result.Undecided(s"the solver's value for $name is outside its range", filtered)
      case None if lane.isDefined && reference.lane.isEmpty =>
        Result.Undecided(
          s"the solver's counterexample is in lane ${lane.get}, which the reference does not run",
          filtered
        )
      case None =>
        val allocations = model.get(SmtBackend.SharedSize).map(Space.Shared -> _).toMap
        Equivalence.replay(
          reference,
          executor,
          left,
          right,
          liveOut,
          inputs,
          SmtBackend.seeds(model),
          allocations,
          lane.map(_.toInt)
        ) match {
          case Verdict.Rejected(found)          => Result.Rejected(found, Source.Solver)
          case Verdict.NotRejected(_) if memory =>
            Result.Undecided(
              "the solver's model shows no difference on replay with its memory, so it rests on the width of an access",
              filtered
            )
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
