package net.kurobako.gemstone.isa.check

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.isa.amdgpu.LaneMasks
import net.kurobako.gemstone.semantics.Space
import net.kurobako.gemstone.semantics.render.*
import net.kurobako.gemstone.testing.Edges
import scala.annotation.tailrec
import scala.reflect.ClassTag
import scala.util.Random

object Equivalence {

  /** The inputs each definition runs on, which a proof reports as the differential coverage of its instructions. */
  val Runs: Int = 64

  type Operand = Form.Operand
  val Operand: Form.Operand.type = Form.Operand

  type Step = Form
  val Step: Form.type = Form

  case class LiveOut(locations: Vec[String], memory: Boolean)

  enum Verdict {
    case Rejected(counterexample: Counterexample)
    case NotRejected(executed: Int)
    case Refused(reason: String)
    case Undecided(reason: String)
  }

  case class Counterexample(difference: Difference, inputs: Vec[(String, Long)])

  enum Difference {
    case Value(location: String, expected: Long, obtained: Long)
    case Memory(expected: Any, obtained: Any)
    case Refusal(left: Option[String], right: Option[String])
  }

  private enum Outcome derives CanEqual {
    case Skipped
    case Agreed
    case Differed(counterexample: Counterexample)
  }

  def vm[H, L <: Lookup](reference: Reference[H, L])(using
      lookup: ClassTag[L]
  )
      : (Case, Vec[Long], H) => Either[String, Vec[Long]] = (kase, inputs, harness) =>
    lookup.unapply(kase.lookup) match {
      case None        => Left(s"${kase.instance} is not a ${lookup.runtimeClass.getSimpleName}")
      case Some(found) =>
        val parameters = kase.definition.parameters.map(parameter => (parameter.name, parameter.tpe))
        val results    = kase.definition.results.map(result => (result.name, result.tpe))
        // a single-lane harness throws on a cross-lane access it does not model
        try reference.execute(kase.target, kase.instance, found, parameters, inputs, results, harness)
        catch { case error: IllegalArgumentException => Left(error.toString) }
    }

  /** Whether the sequences differ on sampled inputs. A surviving verdict is not an equivalence proof. */
  def check[H, L <: Lookup](
      reference: Reference[H, L],
      executor: (Case, Vec[Long], H) => Either[String, Vec[Long]],
      left: Vec[Step],
      right: Vec[Step],
      liveOut: LiveOut,
      runs: Int = Runs
  ): Verdict = typed(reference, left, right, liveOut) match {
    case Left(reason) => Verdict.Refused(reason)
    case Right(types) =>
      val names  = types.keys.toVec.sorted
      val random = Random(0L)

      @tailrec def loop(seed: Int, executed: Int): Verdict =
        if (seed == runs) Verdict.NotRejected(executed)
        else {
          val sampled = names.map(name => (name, Edges.sample(types(name), random)))
          compare(reference, executor, left, right, liveOut, types, sampled, seed) match {
            case Outcome.Skipped                  => loop(seed + 1, executed)
            case Outcome.Agreed                   => loop(seed + 1, executed + 1)
            case Outcome.Differed(counterexample) => Verdict.Rejected(counterexample)
          }
        }

      loop(0, 0) match {
        case Verdict.NotRejected(0) => Verdict.Undecided("unable to decide, no input ran on both sequences")
        case verdict                => verdict
      }
  }

  /** The sequences on `inputs`, locations not named holding zero, and `memory` where it is set, as the filter runs
    * them, over `lane` where a proof names one.
    */
  def replay[H, L <: Lookup](
      reference: Reference[H, L],
      executor: (Case, Vec[Long], H) => Either[String, Vec[Long]],
      left: Vec[Step],
      right: Vec[Step],
      liveOut: LiveOut,
      inputs: Map[String, Long],
      memory: Map[Space, Map[Long, Byte]] = Map.empty,
      allocations: Map[Space, Long] = Map.empty,
      lane: Option[Int] = None
  ): Verdict = typed(reference, left, right, liveOut).map { types =>
    val sampled = types.keys.toVec.sorted.map(name => (name, inputs.getOrElse(name, 0L)))
    compare(reference, executor, left, right, liveOut, types, sampled, 0, memory, allocations, lane) match {
      case Outcome.Skipped                  => Verdict.Undecided("the inputs do not run on both sequences")
      case Outcome.Agreed                   => Verdict.NotRejected(1)
      case Outcome.Differed(counterexample) => Verdict.Rejected(counterexample)
    }
  }.fold(Verdict.Refused(_), identity)

  private def typed[H, L <: Lookup](
      reference: Reference[H, L],
      left: Vec[Step],
      right: Vec[Step],
      liveOut: LiveOut
  ): Either[String, Map[String, Type]] =
    if (liveOut.memory && !reference.observesMemory) Left("memory is not observed on this ISA")
    else crossLane(left).orElse(crossLane(right)).toLeft(()).flatMap(_ =>
      validate(left.iterator ++ right.iterator, liveOut)
    )

  private def compare[H, L <: Lookup](
      reference: Reference[H, L],
      executor: (Case, Vec[Long], H) => Either[String, Vec[Long]],
      left: Vec[Step],
      right: Vec[Step],
      liveOut: LiveOut,
      types: Map[String, Type],
      sampled: Vec[(String, Long)],
      seed: Int,
      memory: Map[Space, Map[Long, Byte]] = Map.empty,
      allocations: Map[Space, Long] = Map.empty,
      lane: Option[Int] = None
  ): Outcome = {
    val initial      = sampled.toMap
    val leftHarness  = reference.harness(seed.toLong, lane)
    val rightHarness = reference.harness(seed.toLong, lane)
    allocations.foreach { (space, bytes) =>
      reference.allocate(leftHarness, space, bytes)
      reference.allocate(rightHarness, space, bytes)
    }
    memory.foreach { (space, bytes) =>
      reference.seed(leftHarness, space, bytes)
      reference.seed(rightHarness, space, bytes)
    }
    (run(left, leftHarness, initial, types, executor), run(right, rightHarness, initial, types, executor)) match {
      case (Left(_), Left(_))       => Outcome.Skipped
      case (Left(reason), Right(_)) =>
        Outcome.Differed(Counterexample(Difference.Refusal(Some(reason), None), sampled))
      case (Right(_), Left(reason)) =>
        Outcome.Differed(Counterexample(Difference.Refusal(None, Some(reason)), sampled))
      case (Right(expected), Right(obtained)) =>
        val value = liveOut.locations.collectFirst {
          case location if !Edges.same(types(location))(expected(location), obtained(location)) =>
            Difference.Value(location, expected(location), obtained(location))
        }
        val leftState  = (reference.state(leftHarness), reference.loads(leftHarness))
        val rightState = (reference.state(rightHarness), reference.loads(rightHarness))
        val memory     = Option.when(liveOut.memory && !leftState.equals(rightState))(
          Difference.Memory(leftState, rightState)
        )
        value.orElse(memory) match {
          case Some(difference) => Outcome.Differed(Counterexample(difference, sampled))
          case None             => Outcome.Agreed
        }
    }
  }

  private[check] def validate(steps: Iterator[Step], liveOut: LiveOut): Either[String, Map[String, Type]] =
    steps.foldLeft[Either[String, Map[String, Type]]](Right(Map.empty))((types, step) =>
      types.flatMap(known => typings(step).flatMap(merge(known, _)))
    ).flatMap(types =>
      liveOut.locations.find(location => !types.contains(location)) match {
        case Some(location) => Left(s"live-out $location is used by neither sequence")
        case None           => Right(types)
      }
    )

  /** Why `steps` cannot be proved one lane at a time: an instruction reads whole a mask that a lane wrote before it, so
    * its result depends on the other lanes.
    */
  private[check] def crossLane(steps: Vec[Step]): Option[String] =
    steps.foldLeft[Either[String, Set[String]]](Right(Set.empty)) { (found, step) =>
      found.flatMap { ballots =>
        val definition = step.kase.definition
        val terms      = definition.requirements ++ definition.body.map(_.term) ++ definition.results.map(_.value)
        val whole      = definition.parameters.map(_.name).find(name =>
          (step.parameters(name) match {
            case Operand.Location(location) => ballots(location)
            case Operand.Joined(locations)  => locations.exists(ballots)
            case Operand.Immediate(_)       => false
          }) && terms.exists(LaneMasks.whole(_, name))
        )
        whole.toLeft {
          val named = definition.parameters.map(_.name).toSet
          val bound = definition.body.exists(statement => LaneMasks.ballots(statement.term))
          // a ballot bound in the body reaches the results that name what the body binds
          def mask(value: Term) = LaneMasks.ballots(value) ||
            (bound && Rejection.terms(value).exists {
              case Term.Variable(name) => !named(name)
              case _                   => false
            })
          definition.results.foldLeft(ballots) { (masks, result) =>
            val written = step.results(result.name)
            if (mask(result.value)) masks ++ written else masks -- written
          }
        }.left.map(name => s"${step.kase.instance} reads $name whole after a lane wrote it")
      }
    }.left.toOption

  private[check] def typings(step: Step): Either[String, Vec[(String, Type)]] = {
    val kase       = step.kase
    val definition = kase.definition
    if (Rejection.undefined(kase)) Left(s"${kase.instance} has no definition")
    else Rejection.unsupported(kase) match {
      case Some(reason)                                                                    => Left(reason)
      case None if !bound(definition.parameters.map(_.name).toSet, step.parameters.keySet) =>
        Left(s"${kase.instance} does not bind its ${definition.parameters.size} operands")
      case None if !bound(definition.results.map(_.name).toSet, step.results.keySet) =>
        Left(s"${kase.instance} does not bind its ${definition.results.size} results")
      case None =>
        val unit   = Form.Unit
        val inputs = definition.parameters.flatMap(parameter =>
          step.parameters(parameter.name) match {
            case Operand.Location(name)    => Vec((name, parameter.tpe))
            case Operand.Joined(locations) => locations.map((_, unit))
            case Operand.Immediate(_)      => Vec.empty[(String, Type)]
          }
        )
        val written = definition.results.flatMap(result =>
          step.results(result.name) match {
            case Vec(single) => Vec((single, result.tpe))
            case several     => several.map((_, unit))
          }
        )
        Right(inputs ++ written)
    }
  }

  private def bound(names: Set[String], keys: Set[String]): Boolean =
    names.size == keys.size && names.subsetOf(keys)

  private[check] def merge(known: Map[String, Type], bindings: Vec[(String, Type)]): Either[String, Map[String, Type]] =
    bindings.foldLeft[Either[String, Map[String, Type]]](Right(known))((types, binding) =>
      types.flatMap(current =>
        current.get(binding._1) match {
          case Some(other) if !Form.compatible(other, binding._2) =>
            Left(s"${binding._1} is used as $other and ${binding._2}")
          case Some(_) => Right(current)
          case None    => Right(current.updated(binding._1, binding._2))
        }
      )
    )

  private def run[H](
      steps: Vec[Step],
      harness: H,
      initial: Map[String, Long],
      types: Map[String, Type],
      executor: (Case, Vec[Long], H) => Either[String, Vec[Long]]
  ): Either[String, Map[String, Long]] =
    steps.foldLeft[Either[String, Map[String, Long]]](Right(initial))((state, step) =>
      state.flatMap(env =>
        executor(step.kase, step.inputs(env), harness).map(outputs =>
          env ++ step.outputs(outputs).map((location, value) => (location, types(location).represent(value)))
        )
      )
    )
}
