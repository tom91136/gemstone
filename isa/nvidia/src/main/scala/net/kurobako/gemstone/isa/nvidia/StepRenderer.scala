package net.kurobako.gemstone.isa.nvidia

import StepRenderer.*
import VMIR.*
import cats.syntax.all.*
import net.kurobako.gemstone.binary.Widths.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.semantics.*
import net.kurobako.gemstone.semantics.render.*
import net.kurobako.gemstone.semantics.render.Term.Steps.*
import scala.annotation.publicInBinary
import scala.collection.mutable

@publicInBinary
private[isa] object StepRenderer {

  /** The steps the instructions lower to, as assignments under `names`; `None` when any has no staged semantics. */
  def describe(instructions: Vec[Instruction], names: Names): Option[Term.Steps] = {
    val sinks = StepRenderer(names)
    instructions.traverse(NvidiaStaged.staged(Render, _)(sinks)(None)).map(Term.Steps.concat(_))
  }

  case class Spec(name: String, order: Vec[String], requirements: Vec[Term])

  /** The instructions' semantics as the definition `spec` names. Operands and state are declared with the types `names`
    * gives them, those in `spec.order` first and in that order.
    */
  def definition(spec: Spec, instructions: Vec[Instruction], names: Names): Either[Unrendered, Definition] = {
    val sinks    = StepRenderer(names)
    val rendered = instructions.map(instruction =>
      NvidiaStaged.staged(Render, instruction)(sinks)(None).toRight(instruction.productPrefix)
    )
    rendered.collect { case Left(unstaged) => unstaged }.distinct match {
      case Vec() =>
        Definition.from(
          Term.Steps.concat(rendered.flatMap(_.toOption)),
          spec.name,
          names,
          spec.order,
          spec.requirements,
          settle = true
        )
          .left.map(Unrendered.Undefined(_))
      case unstaged => Left(Unrendered.Unstaged(unstaged))
    }
  }

  /** The machine semantics of those of `instructions` that have some, each as the definition `spec` names over `names`
    * and declared in `spec.order`, or why it is not one. A machine instruction runs under its predicate, if it has one,
    * after the unpredicated lane steps before it, which bind what it reads. When no instruction has machine semantics,
    * the unpredicated lane steps form one definition if they read or write a name in `state`, the machine state, or a
    * predicated store does; no other predicated step is rendered.
    */
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  def machine(spec: Spec, instructions: Vec[PredicatedInstruction], names: Names, state: Set[String] = Set.empty)
      : Vec[Either[Unrendered, Definition]] = {
    val sinks      = StepRenderer(names)
    val lane       = mutable.ArrayBuffer.empty[Term.Steps]
    val defined    = mutable.ArrayBuffer.empty[Either[Unrendered, Definition]]
    var skipped    = false
    var guardedOut = false
    instructions.foreach { case PredicatedInstruction(instruction, predication) =>
      val guard = predication.map(guardTerm(names, _))
      MachineRendering.render(instruction, NvidiaMachineRender(names, guard)) match {
        case Some(steps) =>
          defined +=
            Definition.from(Term.Steps.concat(Vec.from(lane)) ++ steps, spec.name, names, spec.order, spec.requirements)
              .left.map(Unrendered.Undefined(_))
        case None =>
          (NvidiaStaged.staged(Render, instruction)(sinks)(None), guard) match {
            case (Some(steps), None)                                   => lane += steps
            case (Some(steps), Some(condition)) if storesMemory(steps) =>
              guardedOut = true
              lane += Term.Steps(Term.Step("machine", Term.When(condition, steps.all)))
            case _ => skipped ||= predication.nonEmpty
          }
      }
    }
    val steps = Term.Steps.concat(Vec.from(lane))
    if (defined.isEmpty && !skipped && (guardedOut || touches(steps, state)))
      Vec(Definition.from(steps, spec.name, names, spec.order, spec.requirements).left.map(Unrendered.Undefined(_)))
    else Vec.from(defined)
  }

  private def storesMemory(steps: Term.Steps): Boolean = steps.all.exists(_.name == Machine.memory)

  /** Whether `steps` assign a name in `state` or read one. */
  private def touches(steps: Term.Steps, state: Set[String]): Boolean = {
    val read = steps.all.flatMap(_.value.variables).toSet
    steps.all.exists(step => state(step.name)) || state.exists(read)
  }

  /** The condition that holds on the lanes `predication` selects, over `names`. */
  private def guardTerm(names: Names, predication: ExecutionPredicate): Term = {
    val holds = names.read(predication.predicate)
    if (predication.negated) Render.not(holds) else holds
  }

  def carry(names: Names): String = names.declare(Machine.carry, Type.Bool)

  /** The operands of an element of a warpgroup product, as [[MatrixTerms]] names them, and its scale-d operand. */
  class WarpgroupTerms(names: Names, operation: Matrix.Operation.WarpgroupMultiply)
      extends MatrixTerms(names, operation.product) with WarpgroupOperands[Term] {
    def scale: Term = names.value(operation.scale)
  }

  /** The operands of an element of a tcgen05.mma product, with whether D accumulates. */
  class TensorMemoryTerms(names: Names, operation: Matrix.Operation.Multiply, enable: Value)
      extends MatrixTerms(names, operation) with TensorMemoryOperands[Term] {
    def enabled: Term = names.value(enable)
  }

  /** An atomic read-modify-write as a load of the old value and a store of the value its kernel computes, the old value
    * going to `fetched` when present.
    */
  class Atomic(
      names: Names,
      fetched: Option[Register],
      address: Value,
      source: Value,
      width: ScalarBitWidth,
      space: MemorySpace
  ) extends Sink.Binary[Term, Option[Term.Steps]] {
    inline def apply(inline kernel: (Term, Term) => Term): Option[Term.Steps] = lifted {
      val tpe       = Machine.bits(width.widen[Int in 1 ~ 64])
      val target    = names.value(address)
      val old       = names.temporary()
      val loaded    = Term.Steps(Term.Step(old, Machine.load(Machine.loadMethod(space), tpe, target)))
      val exchanged = assign(
        names,
        Machine.memory,
        Machine.store(Machine.storeMethod(space), tpe, target, kernel(Term.Variable(old), names.value(source)))
      )
      loaded ++ exchanged ++
        Term.Steps(fetched.toVec.map(register => Term.Step(names.register(register), Term.Variable(old))))
    }
  }
}

/** Renders NVIDIA semantics as assignments to each destination of its kernel over the operands, all under `names`. A
  * kernel's bindings become temporaries, an output that reads another's old value is assigned before it, and machine
  * operations read and write the lane's contexts. Each sink renders in a method of its own.
  */
private[isa] class StepRenderer(names: Names) extends NvidiaSinks[Term, Option[Term.Steps]] {
  inline def unary(destination: Register, source: Value)(inline kernel: Term => Term): Option[Term.Steps] =
    lifted(assign(names, names.register(destination), kernel(names.value(source))))

  inline def binary(destination: Register, left: Value, right: Value)(inline kernel: (Term, Term) => Term)
      : Option[Term.Steps] =
    lifted(assign(names, names.register(destination), kernel(names.value(left), names.value(right))))

  inline def ternary(destination: Register, first: Value, second: Value, third: Value)(
      inline kernel: (Term, Term, Term) => Term
  ): Option[Term.Steps] = lifted(assign(
    names,
    names.register(destination),
    kernel(names.value(first), names.value(second), names.value(third))
  ))

  inline def quaternary(destination: Register, first: Value, second: Value, third: Value, fourth: Value)(
      inline kernel: (Term, Term, Term, Term) => Term
  ): Option[Term.Steps] = lifted(assign(
    names,
    names.register(destination),
    kernel(names.value(first), names.value(second), names.value(third), names.value(fourth))
  ))

  inline def carryIn(destination: Register, left: Value, right: Value)(inline result: (Term, Term, Term) => Term)
      : Option[Term.Steps] = lifted(assign(
    names,
    names.register(destination),
    result(names.value(left), names.value(right), Term.Variable(carry(names)))
  ))

  inline def carryIn(destination: Register, first: Value, second: Value, third: Value)(
      inline result: (Term, Term, Term, Term) => Term
  ): Option[Term.Steps] = lifted(assign(
    names,
    names.register(destination),
    result(names.value(first), names.value(second), names.value(third), Term.Variable(carry(names)))
  ))

  inline def carryInOut(destination: Register, left: Value, right: Value)(
      inline result: (Term, Term, Term) => Term,
      inline carry: (Term, Term, Term, Term) => Term
  ): Option[Term.Steps] = lifted {
    val a        = names.value(left)
    val b        = names.value(right)
    val incoming = Term.Variable(StepRenderer.carry(names))
    flagged(names, names.register(destination), StepRenderer.carry(names), result(a, b, incoming), Vec(a, b))(sum =>
      carry(a, b, incoming, sum)
    )
  }

  inline def carryInOut(destination: Register, first: Value, second: Value, third: Value)(
      inline result: (Term, Term, Term, Term) => Term,
      inline carry: (Term, Term, Term, Term, Term) => Term
  ): Option[Term.Steps] = lifted {
    val a        = names.value(first)
    val b        = names.value(second)
    val c        = names.value(third)
    val incoming = Term.Variable(StepRenderer.carry(names))
    flagged(names, names.register(destination), StepRenderer.carry(names), result(a, b, c, incoming), Vec(a, b, c))(
      sum => carry(a, b, c, incoming, sum)
    )
  }

  inline def produceCarries(destination: Register, first: Value, second: Value, third: Value)(
      inline result: (Term, Term, Term) => Term,
      inline carries: (Term, Term, Term) => Term
  ): Option[Term.Steps] = lifted {
    val a     = names.value(first)
    val b     = names.value(second)
    val c     = names.value(third)
    val count = assign(names, names.declare(Machine.carries, Machine.carriesType), carries(a, b, c))
    count ++ assign(names, names.register(destination), result(a, b, c))
  }

  inline def consumeCarries(destination: Register, first: Value, second: Value, third: Value)(
      inline result: (Term, Term, Term, Term) => Term
  ): Option[Term.Steps] = lifted(assign(
    names,
    names.register(destination),
    result(
      names.value(first),
      names.value(second),
      names.value(third),
      Term.Variable(names.declare(Machine.carries, Machine.carriesType))
    )
  ))

  inline def test(destination: Predicate, source: Value)(inline kernel: Term => Term): Option[Term.Steps] =
    lifted(assign(names, names.predicate(destination), kernel(names.value(source))))

  inline def compare(destination: Predicate, left: Value, right: Value)(inline kernel: (Term, Term) => Term)
      : Option[Term.Steps] =
    lifted(assign(names, names.predicate(destination), kernel(names.value(left), names.value(right))))

  inline def compare(destination: Predicate, left: Value, right: Value, other: Predicate)(
      inline kernel: (Term, Term, Term) => Term
  ): Option[Term.Steps] = lifted(assign(
    names,
    names.predicate(destination),
    kernel(names.value(left), names.value(right), names.read(other))
  ))

  inline def predicate(destination: Predicate, source: Predicate)(inline kernel: Term => Term): Option[Term.Steps] =
    lifted(assign(names, names.predicate(destination), kernel(names.read(source))))

  inline def predicate(destination: Predicate, left: Predicate, right: Predicate)(
      inline kernel: (Term, Term) => Term
  ): Option[Term.Steps] =
    lifted(assign(names, names.predicate(destination), kernel(names.read(left), names.read(right))))

  inline def select(destination: Register, guard: Predicate, whenTrue: Value, whenFalse: Value)(
      inline kernel: (Term, Term, Term) => Term
  ): Option[Term.Steps] = lifted(assign(
    names,
    names.register(destination),
    kernel(names.read(guard), names.value(whenTrue), names.value(whenFalse))
  ))

  inline def elements(
      destinations: Vec[Register],
      sources: Vec[Value]
  )(inline kernel: Term => Term): Option[Term.Steps] =
    lifted(parallel(names, destinations.map(names.register(_)), sources.map(source => kernel(names.value(source)))))

  inline def split(low: Option[Register], high: Option[Register], source: Value)(
      inline lowHalf: Term => Term,
      inline highHalf: Term => Term
  ): Option[Term.Steps] = lifted {
    val value  = names.value(source)
    val halves = Vec(low.map((_, lowHalf(value))), high.map((_, highHalf(value)))).flatten
    // a half written over the source goes last
    val (clobbering, preserving) = halves.partition((slot, _) => Term.reads(value, names.register(slot)))
    Term.Steps.concat((preserving ++ clobbering).map((slot, half) => assign(names, names.register(slot), half)))
  }

  inline def broadcast(destination: Register, source: Value)(inline kernel: Term => Term): Option[Term.Steps] =
    lifted(assign(
      names,
      names.register(destination),
      kernel(Machine.leader(names.value(source), names.tpe(Value.Register(destination))))
    ))

  inline def writeFlag(flag: VMConditionCodes.Flag, source: Value)(inline kernel: Term => Term): Option[Term.Steps] =
    lifted(assign(names, names.declare(Machine.flag(flag), Type.Bool), kernel(names.value(source))))

  inline def readFlag(destination: Register, flag: VMConditionCodes.Flag)(inline kernel: Term => Term)
      : Option[Term.Steps] = lifted(assign(
    names,
    names.register(destination),
    kernel(Term.Variable(names.declare(Machine.flag(flag), Type.Bool)))
  ))

  inline def testFlag(destination: Predicate, flag: VMConditionCodes.Flag)(inline kernel: Term => Term)
      : Option[Term.Steps] = lifted(assign(
    names,
    names.predicate(destination),
    kernel(Term.Variable(names.declare(Machine.flag(flag), Type.Bool)))
  ))

  inline def shuffle(
      destination: Register,
      validity: Option[Predicate],
      operand: Value,
      selector: Value,
      bounds: Value
  )(
      inline source: (Term, Term, Term, Term) => Term,
      inline valid: (Term, Term, Term, Term) => Term,
      inline value: Term => Term
  ): Option[Term.Steps] = lifted {
    val select = names.value(selector)
    val bound  = names.value(bounds)
    val from   = assign(names, names.temporary(), source(Machine.lane, select, bound, Machine.warpLanes))
    val checks = Term.Steps.concat(validity.toVec.map(slot =>
      assign(names, names.predicate(slot), valid(Machine.lane, select, bound, Machine.warpLanes))
    ))
    val read = value(Machine.shuffle(names.value(operand), names.tpe(operand), Term.Variable(from.last.name)))
    from ++ checks ++ assign(names, names.register(destination), read)
  }

  inline def ballot(destination: Register, voter: Predicate, memberMask: Value)(inline kernel: (Term, Term) => Term)
      : Option[Term.Steps] = lifted {
    val mask = names.value(memberMask)
    assign(names, names.register(destination), kernel(Machine.ballot(names.read(voter), mask), Machine.members(mask)))
  }

  inline def vote(destination: Predicate, voter: Predicate, memberMask: Value)(inline kernel: (Term, Term) => Term)
      : Option[Term.Steps] = lifted {
    val mask = names.value(memberMask)
    assign(names, names.predicate(destination), kernel(Machine.ballot(names.read(voter), mask), Machine.members(mask)))
  }

  inline def elect(elected: Option[Register], leader: Predicate, memberMask: Value)(
      inline choose: Term => Term,
      inline isLeader: (Term, Term) => Term
  ): Option[Term.Steps] = lifted {
    val chosen = assign(
      names,
      elected.fold(names.temporary())(names.register(_)),
      choose(Machine.participants(names.value(memberMask)))
    )
    chosen ++ assign(names, names.predicate(leader), isLeader(Machine.lane, Term.Variable(chosen.last.name)))
  }

  inline def reduceFloat(
      destination: Register,
      source: Value,
      memberMask: Value,
      maximum: Boolean,
      propagateNaN: Boolean
  )(
      inline element: Term => Term,
      inline combine: (Term, Term) => Term
  ): Option[Term.Steps] = lifted(assign(
    names,
    names.register(destination),
    Machine.reduceFloat(maximum, propagateNaN, element(names.value(source)), Machine.members(names.value(memberMask)))
  ))

  inline def matching(
      found: Option[Register],
      predicate: Option[Predicate],
      source: Value,
      memberMask: Value,
      width: Int in 1 ~ 64,
      all: Boolean
  )(inline holds: Term => Term): Option[Term.Steps] = lifted {
    val matched = assign(
      names,
      found.fold(names.temporary())(names.register(_)),
      Machine.matching(all, names.value(source), Type.unsigned(width), Machine.members(names.value(memberMask)))
    )
    matched ++ Term.Steps.concat(predicate.toVec.map(slot =>
      assign(names, names.predicate(slot), holds(Term.Variable(matched.last.name)))
    ))
  }

  inline def reduce(
      destination: Register,
      source: Value,
      memberMask: Value,
      operation: IntegerOperation,
      width: Int in 1 ~ 64
  )(
      inline element: Term => Term,
      inline combine: (Term, Term) => Term
  ): Option[Term.Steps] = {
    val signed = operation match {
      case IntegerOperation.Minimum(signed) => signed
      case IntegerOperation.Maximum(signed) => signed
      case _                                => false
    }
    val tpe = Type.Integer(width, signed)
    Machine.reduce(operation, tpe, element(names.value(source)), Machine.members(names.value(memberMask))).map(
      reduced =>
        assign(
          names,
          names.register(destination),
          if (signed) Term.Call(s"zext$width", Vec(reduced)) else reduced
        )
    )
  }

  inline def cancelledCta(destination: Register, low: Value, high: Value)(inline kernel: (Term, Term) => Term)
      : Option[Term.Steps] =
    lifted(assign(
      names,
      names.register(destination),
      Machine.cancelledCta(kernel(names.value(low), names.value(high)), names.value(high))
    ))

  inline def load(destination: Register, address: Value, width: ScalarBitWidth, access: MemoryAccess)(
      inline element: (Term, Term) => Term,
      inline value: Term => Term
  ): Option[Term.Steps] = lifted {
    val tpe = Machine.bits(width.widen[Int in 1 ~ 64])
    assign(
      names,
      names.register(destination),
      value(names.constant(address, tpe).getOrElse(
        Machine.load(Machine.loadMethod(access), tpe, element(names.value(address), Term.Literal(0L)))
      ))
    )
  }

  inline def loadVector(destinations: Vec[Register], address: Value, width: ScalarBitWidth, access: MemoryAccess)(
      inline element: (Term, Term) => Term,
      inline value: Term => Term
  ): Option[Term.Steps] = lifted {
    val base = names.value(address)
    val tpe  = Machine.bits(width.widen[Int in 1 ~ 64])
    parallel(
      names,
      destinations.map(names.register(_)),
      destinations.indices.toVec.map(index =>
        value(Machine.load(Machine.loadMethod(access), tpe, element(base, Term.Literal(index.toLong))))
      )
    )
  }

  inline def loadParameter(destinations: Vec[Register], parameter: Parameter, offset: Int >= 0, width: ScalarBitWidth)(
      inline element: (Term, Term) => Term,
      inline value: Term => Term
  ): Option[Term.Steps] = lifted {
    val tpe = Machine.bits(width.widen[Int in 1 ~ 64])
    parallel(
      names,
      destinations.map(names.register(_)),
      destinations.indices.toVec.map(index =>
        value(Machine.loadParameter(
          parameter,
          tpe,
          element(Term.Literal(offset.toLong), Term.Literal(index.toLong))
        ))
      )
    )
  }

  inline def store(address: Value, source: Value, width: ScalarBitWidth, access: MemoryAccess)(
      inline element: (Term, Term) => Term,
      inline value: Term => Term
  ): Option[Term.Steps] = lifted(assign(
    names,
    Machine.memory,
    Machine.store(
      Machine.storeMethod(access),
      Machine.bits(width.widen[Int in 1 ~ 64]),
      element(names.value(address), Term.Literal(0L)),
      value(names.value(source))
    )
  ))

  inline def storeVector(address: Value, sources: Vec[Value], width: ScalarBitWidth, access: MemoryAccess)(
      inline element: (Term, Term) => Term,
      inline value: Term => Term
  ): Option[Term.Steps] = lifted {
    val base = names.value(address)
    val tpe  = Machine.bits(width.widen[Int in 1 ~ 64])
    Term.Steps.concat(sources.zipWithIndex.map((source, index) =>
      assign(
        names,
        Machine.memory,
        Machine.store(
          Machine.storeMethod(access),
          tpe,
          element(base, Term.Literal(index.toLong)),
          value(names.value(source))
        )
      )
    ))
  }

  inline def storeParameter(parameter: Parameter, offset: Int >= 0, source: Value, width: ScalarBitWidth)(
      inline element: (Term, Term) => Term,
      inline value: Term => Term
  ): Option[Term.Steps] = lifted(assign(
    names,
    Machine.memory,
    Machine.storeParameter(
      parameter,
      Machine.bits(width.widen[Int in 1 ~ 64]),
      element(Term.Literal(offset.toLong), Term.Literal(0L)),
      value(names.value(source))
    )
  ))

  inline def atomic(
      fetched: Option[Register],
      address: Value,
      source: Value,
      width: ScalarBitWidth,
      space: MemorySpace
  ): Atomic = Atomic(names, fetched, address, source, width, space)

  /** One element of D from its operands: the element `c` of C, row `i` of A as `a0` on and column `j` of B as `b0` on,
    * with the metadata indices `e0` on of a sparse A and the scale factors `sa0` and `sb0` on; the fragments that hold
    * them are spread across the warp's lanes.
    */
  inline def multiplyMatrices(operation: Matrix.Operation.Multiply)(
      inline element: MatrixOperands[Term] => Term
  ): Option[Term.Steps] = lifted {
    val operands = MatrixTerms(names, operation)
    val result   = element(operands)
    operands.steps ++ assign(names, names.declare("d", MatrixTerms.elementType(operation.d.element)), result)
  }

  /** One element of D as [[multiplyMatrices]] renders it, with the predicate that keeps D as the operand it names. */
  inline def multiplyWarpgroup(operation: Matrix.Operation.WarpgroupMultiply)(
      inline element: WarpgroupOperands[Term] => Term
  ): Option[Term.Steps] = lifted {
    val operands = StepRenderer.WarpgroupTerms(names, operation)
    val result   = element(operands)
    operands.steps ++ assign(names, names.declare("d", MatrixTerms.elementType(operation.product.d.element)), result)
  }

  /** One element of D of a tcgen05.mma whose instruction descriptor is an immediate, from the same operands as
    * [[multiplyMatrices]] and whether D accumulates, `enable`; the matrices are in shared and tensor memory.
    */
  inline def multiplyTensorMemory(operation: Tcgen05.Multiply)(
      inline element: (TensorMemory.Product, TensorMemoryOperands[Term]) => Term
  ): Option[Term.Steps] = operation.descriptor match {
    case Value.Immediate(bits) =>
      TensorMemory.product(operation, bits).toOption.flatMap { product =>
        lifted {
          val operands = StepRenderer.TensorMemoryTerms(names, product.multiply, operation.enable)
          val result   = element(product, operands)
          operands.steps ++
            assign(names, names.declare("d", MatrixTerms.elementType(product.multiply.d.element)), result)
        }
      }
    case _ => None
  }

  inline def compareExchange(
      destination: Register,
      address: Value,
      comparator: Value,
      source: Value,
      width: ScalarBitWidth,
      space: MemorySpace
  )(inline kernel: (Term, Term, Term) => Term): Option[Term.Steps] = lifted {
    val tpe    = Machine.bits(width.widen[Int in 1 ~ 64])
    val target = names.value(address)
    val old    = names.temporary()
    Term.Steps(Term.Step(old, Machine.load(Machine.loadMethod(space), tpe, target))) ++
      assign(
        names,
        Machine.memory,
        Machine.store(
          Machine.storeMethod(space),
          tpe,
          target,
          kernel(Term.Variable(old), names.value(comparator), names.value(source))
        )
      ) :+ Term.Step(names.register(destination), Term.Variable(old))
  }
}
