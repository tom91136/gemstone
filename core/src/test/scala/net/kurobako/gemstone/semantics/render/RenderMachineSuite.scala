package net.kurobako.gemstone.semantics.render

import net.kurobako.gemstone.binary.Widths.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.semantics.*
import net.kurobako.gemstone.testing.Suite
import net.kurobako.gemstone.vm.*
import scala.collection.mutable

class RenderMachineSuite extends Suite {
  private type Flow[A] = Either[Fault, A]

  private class Toy(val operands: Map[String, Long], val memory: mutable.Map[(Space, Long), Long])
      extends MachineAlgebra[Long, String, String, Int, Int, Long, String, Flow] {
    val registers: mutable.Map[String, Long] = mutable.Map.empty

    inline def pc: Long                                          = 40L
    inline def advance: Flow[String]                             = Right("advance")
    inline def done: Flow[Unit]                                  = Right(())
    inline def uniform(operand: String): Long                    = operands(operand)
    inline def jump(target: Int): Flow[String]                   = Right(s"jump $target")
    inline def jumpTo(target: Long): Flow[String]                = Right(s"jumpTo $target")
    inline def exit: Flow[String]                                = Right("exit")
    inline def branch(condition: Long, taken: Int): Flow[String] =
      if (condition != 0L) Right(s"jump $taken") else advance
    inline def branch(condition: Long, taken: Int, otherwise: Int): Flow[String] =
      Right(s"jump ${if (condition != 0L) taken else otherwise}")
    inline def jumpEach(inline target: Int => Long): Flow[String] = Right(s"jumpEach ${target(0)} ${target(1)}")
    inline def branchEach(inline condition: Int => Long, taken: Int): Flow[String] =
      Right(s"branchEach ${condition(0) != 0L} ${condition(1) != 0L} $taken")
    inline def park(barrier: Long, count: Long, inline blocking: Boolean): Flow[String] =
      Right(s"park $barrier $count $blocking")
    inline def everyLane[A](inline body: Int => Flow[Unit])(inline next: => Flow[A]): Flow[A] =
      body(0).flatMap(_ => next)
    inline def read(lane: Int, operand: String): Long                                             = operands(operand)
    inline def write[A](lane: Int, target: String, value: Long)(inline next: => Flow[A]): Flow[A] = {
      registers(target) = value
      next
    }
    inline def load[A](lane: Int, inline space: Space, address: Long, width: ScalarBitWidth)(
        inline next: Long => Flow[A]
    ): Flow[A] = next(memory((space, address)))
    inline def store[A](lane: Int, inline space: Space, address: Long, value: Long, width: ScalarBitWidth)(
        inline next: => Flow[A]
    ): Flow[A] = {
      memory((space, address)) = value
      next
    }
    inline def loadBytes[A](lane: Int, inline space: Space, address: Long, length: Long, what: String)(
        inline next: Long => Flow[A]
    ): Flow[A] = next(memory((space, address)))
    inline def storeBytes[A](lane: Int, inline space: Space, address: Long, data: Long, what: String)(
        inline next: => Flow[A]
    ): Flow[A] = {
      memory((space, address)) = data
      next
    }
    inline def compute(primitive: Primitive[Primitive.Unary], first: Long): Long = primitive.function(first)
    inline def compute(primitive: Primitive[Primitive.Binary], first: Long, second: Long): Long =
      primitive.function(first, second)
    inline def compute(primitive: Primitive[Primitive.Ternary], first: Long, second: Long, third: Long): Long =
      primitive.function(first, second, third)
    inline def require[A](condition: Long, label: String)(inline fault: => Fault)(inline next: => Flow[A]): Flow[A] =
      if (condition != 0L) next else Left(fault)
  }

  private def renderer(guard: Option[Term] = None) = {
    val scope = Scope[String](_ => Vec.empty)
    (
      scope,
      RenderMachine[String, String, Int, String](
        Term.Variable(_),
        identity,
        location => Term.Literal(location.toLong),
        () => scope.temporary(),
        guard
      )
    )
  }

  private def text(scope: Scope[String], steps: Term.Steps, declared: (String, Type)*): String = {
    declared.foreach((name, tpe) => scope.declare(name, tpe))
    Definition.from(steps, "op", scope, Vec.empty).fold(failure => fail(failure), ScalaBackend.show(_))
  }

  private val word = Type.unsigned(64)

  private inline def update[V, L, D, S, F[_]](
      inline algebra: Algebra[V],
      inline machine: MachineAlgebra[V, String, String, L, Int, D, S, F]
  ): F[S] = machine.everyLane { lane =>
    val address = machine.read(lane, "address")
    machine.load(lane, Space.Shared, address, 32) { first =>
      machine.load(lane, Space.Shared, algebra.add(address, algebra.literal(4L)), 32) { second =>
        machine.require(algebra.lessThan(first, algebra.literal(100L), signed = false), "word below 100")(
          Fault.Backend("word out of range")
        ) {
          machine.store(lane, Space.Global, address, algebra.add(first, second), 32) {
            machine.loadBytes(lane, Space.Global, address, algebra.literal(4L), "copy source") { bytes =>
              machine.storeBytes(lane, Space.Local, address, bytes, "copy target") {
                machine.write(lane, "result", algebra.add(first, second))(machine.done)
              }
            }
          }
        }
      }
    }
  }(machine.branch(machine.uniform("flag"), taken = 3))

  private inline def spread[V, L, D, S, F[_]](
      inline algebra: Algebra[V],
      inline machine: MachineAlgebra[V, String, String, L, Int, D, S, F]
  ): F[S] = machine.everyLane { lane =>
    machine.store(lane, Space.Generic, machine.read(lane, "address"), algebra.literal(1L), 8)(machine.done)
  }(machine.jumpEach(lane => algebra.add(machine.read(lane, "address"), machine.pc)))

  test("reads bind fresh names and a write is a result of the definition") {
    val (scope, machine) = renderer()
    assertEquals(
      text(scope, update(Render, machine), "address" -> word, "flag" -> Type.Bool, "result" -> word),
      """def op(address: UInt[64], flag: Bool)(using machine: Machine): (result: UInt[64]) = {
        |  val v0 = machine.load[UInt[32]](Space.Shared, address)
        |  val v1 = machine.load[UInt[32]](Space.Shared, address + 4)
        |  machine.require(v0 < 100, "word below 100")
        |  machine.store[UInt[32]](Space.Global, address, v0 + v1)
        |  val v2 = machine.loadBytes(Space.Global, address, UInt[64](4))
        |  machine.storeBytes(Space.Local, address, v2)
        |  machine.branch(uniform(flag), 3)
        |  (result = v0.to[UInt[64]] + v1.to[UInt[64]])
        |}""".stripMargin
    )
  }

  test("the same semantics executes in order and stops at the first fault") {
    val toy = Toy(Map("address" -> 8L, "flag" -> 1L), mutable.Map((Space.Shared, 8L) -> 5L, (Space.Shared, 12L) -> 2L))
    assertEquals(update(Execute, toy), Right("jump 3"))
    assertEquals(toy.memory((Space.Global, 8L)), 7L)
    assertEquals(toy.memory((Space.Local, 8L)), 7L)
    assertEquals(toy.registers.toMap, Map("result" -> 7L))
    val faulting =
      Toy(Map("address" -> 8L, "flag" -> 1L), mutable.Map((Space.Shared, 8L) -> 500L, (Space.Shared, 12L) -> 2L))
    assertEquals(update(Execute, faulting), Left(Fault.Backend("word out of range")))
    assert(!faulting.memory.contains((Space.Global, 8L)))
  }

  test("control requests render as calls on the machine, under the guard if there is one") {
    val (scope, machine) = renderer(Some(Term.Variable("p")))
    val target           = Term.Variable("target")
    assertEquals(
      text(
        scope,
        machine.jump(7) ++ machine.jumpTo(target) ++ machine.branch(target, taken = 3) ++ machine.exit ++
          machine.park(target, target, blocking = true),
        "p"      -> Type.Bool,
        "target" -> word
      ),
      """def op(p: Bool, target: UInt[64])(using machine: Machine): Unit = {
        |  if (p) machine.jump(7) else ()
        |  if (p) machine.jumpTo(target) else ()
        |  if (p) machine.branch(target != UInt[64](0), 3) else ()
        |  if (p) machine.exit() else ()
        |  if (p) machine.park(target, target, true) else ()
        |  ()
        |}""".stripMargin
    )
    val (plain, unguarded) = renderer()
    assertEquals(
      text(plain, unguarded.jump(7)),
      """def op()(using machine: Machine): Unit = {
        |  machine.jump(7)
        |  ()
        |}""".stripMargin
    )
  }

  test("a two-target branch renders both locations and executes the one the condition selects") {
    val (scope, machine) = renderer()
    assertEquals(
      text(scope, machine.branch(machine.uniform("flag"), taken = 3, otherwise = 4), "flag" -> Type.Bool),
      """def op(flag: Bool)(using machine: Machine): Unit = {
        |  machine.branch(uniform(flag), 3, 4)
        |  ()
        |}""".stripMargin
    )
    val toy = Toy(Map("flag" -> 1L), mutable.Map.empty)
    assertEquals(toy.branch(1L, taken = 3, otherwise = 4), Right("jump 3"))
    assertEquals(toy.branch(0L, taken = 3, otherwise = 4), Right("jump 4"))
  }

  test("a computed location renders as an expression of the type the machine takes it at") {
    val scope   = Scope[String](_ => Vec.empty)
    val machine = RenderMachine[String, String, Int, String](
      Term.Variable(_),
      identity,
      offset =>
        Term.Binary(
          "+",
          Term.context(RenderMachine.Machine, MachineCall("pc", Vec.empty, Vec.empty), Some(word)),
          Term.Literal(offset.toLong)
        ),
      () => scope.temporary(),
      locationType = Some(word)
    )
    assertEquals(
      text(scope, machine.jump(4) ++ machine.branch(Term.Variable("p"), taken = 8), "p" -> Type.Bool),
      """def op(p: Bool)(using machine: Machine): Unit = {
        |  machine.jump(machine.pc() + 4)
        |  machine.branch(p, machine.pc() + 8)
        |  ()
        |}""".stripMargin
    )
  }

  test("a lane body runs under the guard and per-lane requests render once") {
    val (scope, machine) = renderer(Some(Term.Variable("p")))
    assertEquals(
      text(scope, spread(Render, machine), "p" -> Type.Bool, "address" -> word),
      """def op(p: Bool, address: UInt[64])(using machine: Machine): Unit = {
        |  if (p) {
        |    machine.store[UInt[8]](Space.Generic, address, UInt[8](1))
        |  }
        |  if (p) machine.jumpTo(address + machine.pc()) else ()
        |  ()
        |}""".stripMargin
    )
  }

  test("a write under the guard keeps the previous value where the guard fails") {
    val (scope, machine) = renderer(Some(Term.Variable("p")))
    assertEquals(
      text(
        scope,
        machine.everyLane(lane =>
          machine.write(lane, "result", machine.read(lane, "address"))(machine.done)
        )(machine.advance),
        "p"       -> Type.Bool,
        "address" -> word,
        "result"  -> word
      ),
      """def op(p: Bool, address: UInt[64], result: UInt[64]): (result: UInt[64]) =
        |  (result = if (p) address else result)""".stripMargin
    )
  }

  test("a write in a loop or of a value computed under the guard is rejected") {
    val (scope, machine) = renderer(Some(Term.Variable("p")))
    val _                = scope.declare("p", Type.Bool)
    val _                = scope.declare("address", word)
    val _                = scope.declare("count", word)
    val _                = scope.declare("result", word)
    val looped           = machine.everyLane(_ =>
      RenderMachine.each("i", Term.Variable("count"), machine.write((), "result", Term.Literal(1L))(machine.done))
    )(machine.advance)
    assertEquals(
      Definition.from(looped, "op", scope, Vec.empty).left.toOption,
      Some("op writes result in a loop")
    )
    val computed = machine.everyLane { lane =>
      val address = machine.read(lane, "address")
      machine.store(lane, Space.Global, address, Term.Literal(1L), 32)(
        machine.load(lane, Space.Global, address, 32)(loaded => machine.write(lane, "result", loaded)(machine.done))
      )
    }(machine.advance)
    assertEquals(
      Definition.from(computed, "op", scope, Vec.empty).left.toOption,
      Some("op writes result from a value under a condition")
    )
  }

  test("a primitive renders as a call by name and executes its function") {
    val sum              = Primitive[Primitive.Binary]("net.kurobako.gemstone.semantics.Example.sum", _ + _)
    val (scope, machine) = renderer()
    assertEquals(
      text(
        scope,
        machine.write((), "result", machine.compute(sum, Term.Variable("a"), Term.Variable("b")))(machine.done),
        "a"      -> word,
        "b"      -> word,
        "result" -> word
      ),
      """def op(a: UInt[64], b: UInt[64]): (result: UInt[64]) =
        |  (result = Example.sum(a, b))""".stripMargin
    )
    assertEquals(Toy(Map.empty, mutable.Map.empty).compute(sum, 2L, 3L), 5L)
  }

  test("a unit renders its reads as a view of every lane and its writes per lane") {
    val (scope, machine) = renderer()
    assertEquals(
      text(
        scope,
        machine.gather(UnitAlgebra.Slot.First, "address") { view =>
          machine.writeEach(Vec("first", "second")) { (lane, index) =>
            machine.peer(lane, view, Term.Binary("+", machine.laneIndex(lane), Term.Literal(index.toLong + 1L)))
          }(machine.advance)
        },
        "address" -> word,
        "first"   -> word,
        "second"  -> word
      ),
      """def op(address: UInt[64])(using lanes: Lanes): (first: UInt[64], second: UInt[64]) = {
        |  val v0 = lanes.gather(address)
        |  val v1 = lanes.index()
        |  (
        |    first = lanes.peer(v0, v1 + 1),
        |    second = lanes.peer(v0, v1 + 2)
        |  )
        |}""".stripMargin
    )
  }

  test("a unit renders its width and the activity of a lane") {
    val (scope, machine) = renderer()
    assertEquals(
      text(
        scope,
        machine.writeEach(Vec("active", "size")) { (lane, index) =>
          if (index == 0) machine.active(lane, Term.Literal(1L)) else machine.unitSize
        }(machine.advance),
        "active" -> word,
        "size"   -> word
      ),
      """def op()(using lanes: Lanes): (active: UInt[64], size: UInt[64]) =
        |  (
        |    active = lanes.active(UInt[64](1)),
        |    size = lanes.size()
        |  )""".stripMargin
    )
  }
}
