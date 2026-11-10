package net.kurobako.gemstone.isa.check

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.semantics.render.*
import net.kurobako.gemstone.testing.Edges
import scala.annotation.tailrec
import scala.reflect.ClassTag
import scala.util.Random

object Equivalence {

  enum Operand {
    case Location(name: String)
    case Immediate(value: Long)
  }

  case class Step(kase: Case, parameters: Map[String, Operand], results: Map[String, String])

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
      runs: Int = DifferentialSuite.Runs
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

  /** The sequences on `inputs`, locations not named holding zero, as the filter runs them. */
  def replay[H, L <: Lookup](
      reference: Reference[H, L],
      executor: (Case, Vec[Long], H) => Either[String, Vec[Long]],
      left: Vec[Step],
      right: Vec[Step],
      liveOut: LiveOut,
      inputs: Map[String, Long]
  ): Verdict = typed(reference, left, right, liveOut).map { types =>
    val sampled = types.keys.toVec.sorted.map(name => (name, inputs.getOrElse(name, 0L)))
    compare(reference, executor, left, right, liveOut, types, sampled, 0) match {
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
    else validate(left.iterator ++ right.iterator, liveOut)

  private def compare[H, L <: Lookup](
      reference: Reference[H, L],
      executor: (Case, Vec[Long], H) => Either[String, Vec[Long]],
      left: Vec[Step],
      right: Vec[Step],
      liveOut: LiveOut,
      types: Map[String, Type],
      sampled: Vec[(String, Long)],
      seed: Int
  ): Outcome = {
    val initial      = sampled.toMap
    val leftHarness  = reference.harness(seed.toLong)
    val rightHarness = reference.harness(seed.toLong)
    (run(left, leftHarness, initial, executor), run(right, rightHarness, initial, executor)) match {
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
        val (leftState, rightState) = (reference.state(leftHarness), reference.state(rightHarness))
        val memory                  = Option.when(liveOut.memory && !leftState.equals(rightState))(
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

  private[check] def typings(step: Step): Either[String, Vec[(String, Type)]] = {
    val kase       = step.kase
    val definition = kase.definition
    if (definition.body.isEmpty && definition.results.isEmpty) Left(s"${kase.instance} has no definition")
    else unsupported(kase) match {
      case Some(reason) => Left(s"${kase.instance} is refused: $reason")
      case None if !bound(definition.parameters.map(_.name).toSet, step.parameters.keySet) =>
        Left(s"${kase.instance} does not bind its ${definition.parameters.size} operands")
      case None if !bound(definition.results.map(_.name).toSet, step.results.keySet) =>
        Left(s"${kase.instance} does not bind its ${definition.results.size} results")
      case None =>
        val inputs = definition.parameters.flatMap(parameter =>
          step.parameters(parameter.name) match {
            case Operand.Location(name) => Some((name, parameter.tpe))
            case Operand.Immediate(_)   => None
          }
        )
        val written = definition.results.map(result => (step.results(result.name), result.tpe))
        Right(inputs ++ written)
    }
  }

  private def bound(names: Set[String], keys: Set[String]): Boolean =
    names.size == keys.size && names.subsetOf(keys)

  private[check] def merge(known: Map[String, Type], bindings: Vec[(String, Type)]): Either[String, Map[String, Type]] =
    bindings.foldLeft[Either[String, Map[String, Type]]](Right(known))((types, binding) =>
      types.flatMap(current =>
        current.get(binding._1) match {
          case Some(other) if other != binding._2 => Left(s"${binding._1} is used as $other and ${binding._2}")
          case _                                  => Right(current.updated(binding._1, binding._2))
        }
      )
    )

  private def unsupported(kase: Case): Option[String] = {
    val definition = kase.definition
    val roots      = definition.body.iterator.map(_.term) ++ definition.requirements.iterator ++
      definition.results.iterator.map(_.value)
    roots.flatMap(terms(_)).flatMap(refusal(_)).nextOption()
  }

  private[check] def terms(term: Term): Iterator[Term] = Iterator(term) ++ term.children.iterator.flatMap(terms(_))

  private def refusal(term: Term): Option[String] = term match {
    case Term.Context(contextType = contextType, method = method, result = result, effect = effect) =>
      val owner   = contextType.substring(contextType.lastIndexOf('.') + 1)
      val machine = contextType == RenderMachine.Machine.contextType
      if (!machine || !Set("load", "store", "storeBytes").contains(method))
        Some(s"$owner.$method is not a plain memory access")
      else if (effect && result.nonEmpty) Some(s"atomic update through $owner.$method")
      else None
    case _ => None
  }

  private def run[H](
      steps: Vec[Step],
      harness: H,
      initial: Map[String, Long],
      executor: (Case, Vec[Long], H) => Either[String, Vec[Long]]
  ): Either[String, Map[String, Long]] =
    steps.foldLeft[Either[String, Map[String, Long]]](Right(initial))((state, step) =>
      state.flatMap { env =>
        val inputs = step.kase.definition.parameters.map(parameter =>
          step.parameters(parameter.name) match {
            case Operand.Location(name)   => env(name)
            case Operand.Immediate(value) => value
          }
        )
        executor(step.kase, inputs, harness).map(outputs =>
          env ++ step.kase.definition.results.zip(outputs).map((result, value) => (step.results(result.name), value))
        )
      }
    )
}
