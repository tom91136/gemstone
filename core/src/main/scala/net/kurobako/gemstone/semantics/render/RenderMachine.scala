package net.kurobako.gemstone.semantics.render

import net.kurobako.gemstone.binary.Widths.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.semantics.*
import net.kurobako.gemstone.vm.*

object RenderMachine {

  /** The statements rendered so far, whatever the semantics goes on to yield. */
  type Rendered[A] = Term.Steps

  /** The machine whose operations rendered semantics call. */
  val Machine: (contextType: String, context: String) =
    ("net.kurobako.gemstone.semantics.prelude.Machine", "machine")

  /** The lanes of a unit, which rendered semantics read across. */
  val Lanes: (contextType: String, context: String) =
    ("net.kurobako.gemstone.semantics.prelude.Lanes", "lanes")

  val Word: Type = Type.unsigned(64)

  /** The step name of a statement, which only its place in the order depends on. */
  private val Statement = "machine"

  def space(space: Space): Term = Term.Constant(s"net.kurobako.gemstone.semantics.Space.$space")

  /** `call` on `owner` as the next statement. */
  def statement(owner: (contextType: String, context: String), call: MachineCall): Term.Steps =
    Term.Steps(Term.Step(Statement, Term.context(owner, call, None, effect = true)))

  /** `value` bound to `name`. */
  def binding(name: String, value: Term): Term.Steps = Term.Steps(Term.Step(name, value))

  /** `body` run once for each value `source` yields, which it reads as `variable`, as the next statement. */
  def each(variable: String, source: Term, body: Term.Steps): Term.Steps =
    Term.Steps(Term.Step(Statement, Term.Each(variable, source, body.all)))

  /** `request` as the next statement, applying to the lanes `guard` selects. */
  def guarded(guard: Option[Term], request: Term): Term.Steps = Term.Steps(Term.Step(
    Statement,
    guard.fold(request)(condition => Term.Select(condition, request, Term.Constant("()")))
  ))

  /** `condition` without the conjuncts that always hold, or none if every one does. */
  def requirement(condition: Term): Option[Term] = {
    def conjuncts(term: Term): Vec[Term] = term match {
      case Term.Binary("&", left, right) => conjuncts(left) ++ conjuncts(right)
      case Term.Literal(1L)              => Vec.empty
      case other                         => Vec(other)
    }
    conjuncts(condition).reduceLeftOption(Term.Binary("&", _, _))
  }
}

/** Machine semantics rendered as statements for a [[Definition]]: each effect, check and control request becomes a call
  * on the `machine` context, in the order the semantics makes them, and each write an assignment to the destination
  * `destination` names. A definition describes one lane, so [[everyLane]] renders its body once, over the operands
  * `operand` names, under `guard`, the predicate that selects the lanes the instruction executes for, if it has one. A
  * request for control outside a lane body reads under the guard too. Program locations read as `location` gives them,
  * of type `locationType` if they are computed, and the names of the values a read binds come from `temporary`. A unit
  * operation calls the `lanes` context, and a primitive the function its name gives.
  */
class RenderMachine[O, W, P, S](
    operand: O => Term,
    destination: W => String,
    location: P => Term,
    temporary: () => String,
    guard: Option[Term] = None,
    locationType: Option[Type] = None
) extends MachineAlgebra[Term, O, W, Unit, P, Term, S, RenderMachine.Rendered]
    with UnitAlgebra[Term, O, W, Unit, RenderMachine.Rendered] {
  import RenderMachine.*

  private def request(method: String, arguments: Vec[(Term, Option[Type])]): Term.Steps =
    guarded(guard, Term.context(Machine, MachineCall(method, Vec.empty, arguments), None, effect = true))

  private def blocked(body: Term.Steps): Term.Steps = guard match {
    case Some(condition) if body.all.nonEmpty =>
      Term.Steps(Term.Step("machine", Term.When(condition, body.all)))
    case _ => body
  }

  inline def pc: Term = Term.context(Machine, MachineCall("pc", Vec.empty, Vec.empty), Some(Word))

  inline def advance: Term.Steps = Term.Steps.empty
  inline def done: Term.Steps    = Term.Steps.empty

  inline def uniform(operand: O): Term = Term.Call("uniform", Vec(this.operand(operand)))

  inline def jump(target: P): Term.Steps                   = request("jump", Vec((location(target), locationType)))
  inline def jumpTo(target: Term): Term.Steps              = request("jumpTo", Vec((target, Some(Word))))
  inline def branch(condition: Term, taken: P): Term.Steps =
    request("branch", Vec((condition, Some(Type.Bool)), (location(taken), locationType)))
  inline def branch(condition: Term, taken: P, otherwise: P): Term.Steps =
    request(
      "branch",
      Vec((condition, Some(Type.Bool)), (location(taken), locationType), (location(otherwise), locationType))
    )
  inline def jumpEach(inline target: Unit => Term): Term.Steps                      = jumpTo(target(()))
  inline def branchEach(inline condition: Unit => Term, taken: P): Term.Steps       = branch(condition(()), taken)
  inline def exit: Term.Steps                                                       = request("exit", Vec.empty)
  inline def park(barrier: Term, count: Term, inline blocking: Boolean): Term.Steps =
    request(
      "park",
      Vec((barrier, Some(Word)), (count, Some(Word)), (Term.Constant(blocking.toString), None))
    )

  inline def everyLane[A](inline body: Unit => Term.Steps)(inline next: => Term.Steps): Term.Steps =
    blocked(body(())) ++ next

  inline def read(lane: Unit, operand: O): Term = this.operand(operand)

  inline def write[A](lane: Unit, target: W, value: Term)(inline next: => Term.Steps): Term.Steps =
    binding(destination(target), value) ++ next

  inline def load[A](lane: Unit, inline space: Space, address: Term, width: ScalarBitWidth)(
      inline next: Term => Term.Steps
  ): Term.Steps = {
    val tpe  = Type.unsigned(Type.width(width.raw))
    val name = temporary()
    binding(
      name,
      Term.context(
        Machine,
        MachineCall("load", Vec(tpe), Vec((RenderMachine.space(space), None), (address, Some(Word)))),
        Some(tpe)
      )
    ) ++ next(Term.Variable(name))
  }

  inline def store[A](lane: Unit, inline space: Space, address: Term, value: Term, width: ScalarBitWidth)(
      inline next: => Term.Steps
  ): Term.Steps = {
    val tpe = Type.unsigned(Type.width(width.raw))
    statement(
      Machine,
      MachineCall("store", Vec(tpe), Vec((RenderMachine.space(space), None), (address, Some(Word)), (value, Some(tpe))))
    ) ++ next
  }

  inline def loadBytes[A](lane: Unit, inline space: Space, address: Term, length: Term, what: String)(
      inline next: Term => Term.Steps
  ): Term.Steps = {
    val name = temporary()
    binding(
      name,
      Term.context(
        Machine,
        MachineCall(
          "loadBytes",
          Vec.empty,
          Vec((RenderMachine.space(space), None), (address, Some(Word)), (length, Some(Word)))
        ),
        None
      )
    ) ++ next(Term.Variable(name))
  }

  inline def storeBytes[A](lane: Unit, inline space: Space, address: Term, data: Term, what: String)(
      inline next: => Term.Steps
  ): Term.Steps = statement(
    Machine,
    MachineCall("storeBytes", Vec.empty, Vec((RenderMachine.space(space), None), (address, Some(Word)), (data, None)))
  ) ++ next

  private def called(primitive: Primitive[?], arguments: Vec[Term]): Term = {
    val split = primitive.name.lastIndexOf('.')
    Term.Primitive(
      primitive.name.take(split),
      primitive.name.drop(split + 1),
      arguments,
      arguments.map(_ => Some(Word)),
      Word
    )
  }

  inline def compute(primitive: Primitive[Primitive.Unary], first: Term): Term = called(primitive, Vec(first))
  inline def compute(primitive: Primitive[Primitive.Binary], first: Term, second: Term): Term =
    called(primitive, Vec(first, second))
  inline def compute(primitive: Primitive[Primitive.Ternary], first: Term, second: Term, third: Term): Term =
    called(primitive, Vec(first, second, third))

  inline def converged: Term = Term.context(Lanes, MachineCall("converged", Vec.empty, Vec.empty), Some(Type.Bool))

  inline def unitSize: Term = Term.context(Lanes, MachineCall("size", Vec.empty, Vec.empty), Some(Word))

  inline def laneIndex(lane: Unit): Term = Term.context(Lanes, MachineCall("index", Vec.empty, Vec.empty), Some(Word))

  inline def gather[A](slot: UnitAlgebra.Slot, operand: O)(inline next: Term => Term.Steps): Term.Steps = {
    val name = temporary()
    binding(
      name,
      Term.context(Lanes, MachineCall("gather", Vec.empty, Vec((this.operand(operand), Some(Word)))), None)
    ) ++
      next(Term.Variable(name))
  }

  inline def gatherLoad[A](
      slot: UnitAlgebra.Slot,
      lanes: Int,
      inline space: Space,
      address: O,
      offset: Long,
      width: ScalarBitWidth
  )(
      inline next: Term => Term.Steps
  ): Term.Steps = {
    val tpe  = Type.unsigned(Type.width(width.raw))
    val name = temporary()
    binding(
      name,
      Term.context(
        Lanes,
        MachineCall(
          "gatherLoad",
          Vec(tpe),
          Vec(
            (Term.Constant(lanes.toString), None),
            (RenderMachine.space(space), None),
            (this.operand(address), Some(Word)),
            (Term.Constant(offset.toString), None)
          )
        ),
        None
      )
    ) ++ next(Term.Variable(name))
  }

  inline def peer(lane: Unit, view: Term, index: Term): Term =
    Term.context(Lanes, MachineCall("peer", Vec.empty, Vec((view, None), (index, Some(Word)))), Some(Word))

  inline def active(lane: Unit, index: Term): Term =
    Term.context(Lanes, MachineCall("active", Vec.empty, Vec((index, Some(Word)))), Some(Word))

  inline def writeEach[A](targets: Vec[W])(inline value: (
      Unit,
      Int
  ) => Term)(inline next: => Term.Steps): Term.Steps = {
    val writes = targets.zipWithIndex.map((target, index) => binding(destination(target), value((), index)))
    blocked(Term.Steps.concat(writes)) ++ next
  }

  inline def require[A](condition: Term, label: String)(inline fault: => Fault)(
      inline next: => Term.Steps
  ): Term.Steps = {
    val rest = next
    requirement(condition) match {
      case Some(checked) =>
        statement(
          Machine,
          MachineCall("require", Vec.empty, Vec((checked, Some(Type.Bool)), (Term.Constant(s"\"$label\""), None)))
        ) ++ rest
      case None => rest
    }
  }
}
