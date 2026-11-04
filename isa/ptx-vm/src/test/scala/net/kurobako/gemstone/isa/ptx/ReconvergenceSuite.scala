package net.kurobako.gemstone.isa.ptx

import Reconvergence.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.testing.either.*

class ReconvergenceSuite extends Suite {
  private class Analysed(val statements: Vec[Program.Statement], val schedule: Reconvergence.Schedule) {
    def labelAt(name: String): Int = statements.indexWhere {
      case Program.Statement.Label(label) => label.raw == name
      case _                              => false
    }

    def instructionAt(opcode: String, nth: Int = 0): Int = statements.indices.filter { index =>
      statements(index) match {
        case Program.Statement.Instruction(instruction) => instruction.qualifiedOpcode.raw.startsWith(opcode)
        case _                                          => false
      }
    }(nth)

    def firstInstructionAfter(label: String): Int = (labelAt(label) until statements.size).find { index =>
      statements(index) match {
        case Program.Statement.Instruction(_) => true
        case _                                => false
      }
    }.get

    def before(index: Int): Vec[Action] = schedule.before.getOrElse(index, Vec.empty[Action])

    def atLabel(label: String): Vec[Action] = before(firstInstructionAfter(label))

    def actions: Vec[Action] = Vec.from(schedule.before.toSeq.sortBy(_._1)).flatMap(_._2)
  }

  private def analyse(body: String, registers: String = ""): Analysed = {
    val program = Parser.parse(s""".version 8.0
      |.target sm_80
      |.address_size 64
      |.visible .entry probe(.param .u64 data) {
      |  .reg .u64 %base;
      |  .reg .u32 %t, %i, %v, %old, %w;
      |  .reg .pred %p, %q, %a, %b;
      |  $registers
      |  ld.param.u64 %base, [data];
      |  mov.u32 %t, %tid.x;
      |  $body
      |}""".stripMargin).value
    val scoped = program.statements.collectFirst { case entry: Program.Statement.Entry => entry.body }.flatten.get
    val flat   = scoped.filter {
      case Program.Statement.Directive(path = path) => path.raw != Vec("branchtargets")
      case _                                        => true
    }
    Analysed(flat, Reconvergence.schedule(flat, VMCompiler.branchTargets(scoped)))
  }

  private val store = "st.global.u32 [%base], %v; ret;"

  test("an if and else rejoin at the label after both arms") {
    val found = analyse(s"""setp.ne.u32 %p, %t, 0;
      |@%p bra ELSE;
      |add.u32 %v, %v, 1;
      |bra JOIN;
      |ELSE:
      |add.u32 %v, %v, 2;
      |JOIN:
      |$store""".stripMargin)
    assertEquals(found.before(found.instructionAt("bra", 0)), Vec[Action](Action.Setup(0)))
    assertEquals(found.atLabel("JOIN"), Vec[Action](Action.Synchronise(0)))
    assertEquals(found.actions.size, 2)
    assertEquals(found.schedule.retargets, Map.empty)
  }

  test("an if without an else rejoins where the branch lands") {
    val found = analyse(s"""setp.ne.u32 %p, %t, 0;
      |@%p bra JOIN;
      |add.u32 %v, %v, 1;
      |JOIN:
      |$store""".stripMargin)
    assertEquals(found.before(found.instructionAt("bra")), Vec[Action](Action.Setup(0)))
    assertEquals(found.atLabel("JOIN"), Vec[Action](Action.Synchronise(0)))
    assertEquals(found.actions.size, 2)
  }

  test("a branch that exits a loop sets up in the loop's preheader") {
    val found = analyse(s"""mov.u32 %i, 0;
      |LOOP:
      |add.u32 %i, %i, 1;
      |setp.lt.u32 %p, %i, %t;
      |@%p bra LOOP;
      |$store""".stripMargin)
    assertEquals(found.before(found.labelAt("LOOP")), Vec[Action](Action.Setup(0)))
    assertEquals(found.before(found.instructionAt("st")), Vec[Action](Action.Synchronise(0)))
    assertEquals(found.actions.size, 2)
    assertEquals(found.schedule.retargets, Map.empty)
  }

  test("branches from outside a loop enter through the preheader") {
    val found = analyse(s"""setp.eq.u32 %q, %t, 99;
      |@%q bra.uni LOOP;
      |add.u32 %i, %i, 5;
      |LOOP:
      |add.u32 %i, %i, 1;
      |setp.lt.u32 %p, %i, %t;
      |@%p bra LOOP;
      |$store""".stripMargin)
    val label = "convergence preheader 2": NonEmpty[String]
    assertEquals(
      found.before(found.labelAt("LOOP")),
      Vec[Action](Action.Label(label), Action.Setup(0))
    )
    assertEquals(
      found.schedule.retargets,
      Map(found.instructionAt("bra.uni") -> Map(("LOOP": NonEmpty[String]) -> label))
    )
    assertEquals(found.before(found.instructionAt("st")), Vec[Action](Action.Synchronise(0)))
  }

  test("a loop with a break shares one barrier between its two exits") {
    val found = analyse(s"""mov.u32 %i, 0;
      |LOOP:
      |setp.eq.u32 %a, %i, %t;
      |@%a bra DONE;
      |add.u32 %i, %i, 1;
      |setp.eq.u32 %b, %i, 7;
      |@%b bra DONE;
      |add.u32 %v, %v, 1;
      |bra.uni LOOP;
      |DONE:
      |$store""".stripMargin)
    assertEquals(found.before(found.labelAt("LOOP")), Vec[Action](Action.Setup(0)))
    assertEquals(found.atLabel("DONE"), Vec[Action](Action.Synchronise(0)))
    assertEquals(found.actions.size, 2)
  }

  test("nested branches with one rejoin synchronise the inner barrier first") {
    val found = analyse(s"""setp.ne.u32 %p, %t, 0;
      |@%p bra END;
      |setp.ne.u32 %q, %t, 1;
      |@%q bra END;
      |add.u32 %v, %v, 1;
      |END:
      |$store""".stripMargin)
    assertEquals(found.before(found.instructionAt("bra", 0)), Vec[Action](Action.Setup(0)))
    assertEquals(found.before(found.instructionAt("bra", 1)), Vec[Action](Action.Setup(1)))
    assertEquals(found.atLabel("END"), Vec[Action](Action.Synchronise(1), Action.Synchronise(0)))
  }

  test("nested branches with their own rejoins each get a barrier") {
    val found = analyse(s"""setp.ne.u32 %p, %t, 0;
      |@%p bra OUTER;
      |setp.ne.u32 %q, %t, 1;
      |@%q bra INNER;
      |add.u32 %v, %v, 1;
      |INNER:
      |add.u32 %v, %v, 2;
      |OUTER:
      |$store""".stripMargin)
    assertEquals(found.atLabel("INNER"), Vec[Action](Action.Synchronise(1)))
    assertEquals(found.atLabel("OUTER"), Vec[Action](Action.Synchronise(0)))
  }

  test("a branch whose arms both leave the routine needs no barrier") {
    val found = analyse("""setp.ne.u32 %p, %t, 0;
      |@%p bra EARLY;
      |st.global.u32 [%base], %v;
      |ret;
      |EARLY:
      |ret;""".stripMargin)
    assertEquals(found.schedule, Reconvergence.Schedule.empty)
  }

  test("a predicated return is no divergence point") {
    val found = analyse(s"""setp.ne.u32 %p, %t, 0;
      |@%p ret;
      |$store""".stripMargin)
    assertEquals(found.schedule, Reconvergence.Schedule.empty)
  }

  test("a uniform branch gets no barrier") {
    val found = analyse(s"""setp.ne.u32 %p, %t, 0;
      |@%p bra.uni JOIN;
      |add.u32 %v, %v, 1;
      |JOIN:
      |$store""".stripMargin)
    assertEquals(found.schedule, Reconvergence.Schedule.empty)
  }

  test("an unconditional branch gets no barrier") {
    val found = analyse(s"""bra JOIN;
      |JOIN:
      |$store""".stripMargin)
    assertEquals(found.schedule, Reconvergence.Schedule.empty)
  }

  test("an indexed branch rejoins after its arms") {
    val found = analyse(
      s"""table: .branchtargets ZERO, ONE;
      |brx.idx %t, table;
      |ZERO:
      |add.u32 %v, %v, 1;
      |bra JOIN;
      |ONE:
      |add.u32 %v, %v, 2;
      |JOIN:
      |$store""".stripMargin
    )
    assertEquals(found.before(found.instructionAt("brx")), Vec[Action](Action.Setup(0)))
    assertEquals(found.atLabel("JOIN"), Vec[Action](Action.Synchronise(0)))
    assertEquals(found.actions.size, 2)
  }

  test("a spin loop on an atomic compare and swap yields at its top") {
    val found = analyse("""LOCK:
      |atom.global.cas.b32 %old, [%base], 0, 1;
      |setp.ne.u32 %p, %old, 0;
      |@%p bra LOCK;
      |ret;""".stripMargin)
    assertEquals(found.before(found.labelAt("LOCK")), Vec[Action](Action.Setup(0)))
    assertEquals(found.before(found.instructionAt("atom")), Vec[Action](Action.Yield))
    assertEquals(found.before(found.instructionAt("ret")), Vec[Action](Action.Synchronise(0)))
  }

  Vec(
    "atom.global.add.u32 %old, [%base], 1;"    -> true,
    "red.global.add.u32 [%base], 1;"           -> true,
    "ld.volatile.global.u32 %old, [%base];"    -> true,
    "ld.relaxed.gpu.global.u32 %old, [%base];" -> true,
    "ld.acquire.gpu.global.u32 %old, [%base];" -> true,
    "ld.global.u32 %old, [%base];"             -> false,
    "ld.global.nc.u32 %old, [%base];"          -> false,
    "st.global.u32 [%base], %old;"             -> false
  ).foreach { (instruction, yields) =>
    test(s"a loop around `${instruction.dropRight(1)}` ${if (yields) "yields" else "does not yield"}") {
      val found = analyse(s"""mov.u32 %old, 0;
        |LOOP:
        |$instruction
        |setp.ne.u32 %p, %old, 3;
        |@%p bra.uni LOOP;
        |$store""".stripMargin)
      assertEquals(found.actions.contains(Action.Yield), yields)
    }
  }

  test("a loop of pointer chasing loads does not yield") {
    val found = analyse(s"""LOOP:
      |ld.global.u32 %old, [%base];
      |ld.global.u32 %w, [%base+4];
      |setp.ne.u32 %p, %old, %w;
      |@%p bra.uni LOOP;
      |$store""".stripMargin)
    assertEquals(found.schedule, Reconvergence.Schedule.empty)
  }

  test("code no branch reaches adds nothing") {
    val found = analyse("""ret;
      |DEAD:
      |setp.ne.u32 %p, %t, 0;
      |@%p bra DEAD2;
      |add.u32 %v, %v, 1;
      |DEAD2:
      |ret;""".stripMargin)
    assertEquals(found.schedule, Reconvergence.Schedule.empty)
  }

  test("a loop entered by falling in from its own body sets up in an out-of-line stub") {
    val found = analyse(s"""bra.uni TEST;
      |BODY:
      |add.u32 %i, %i, 1;
      |setp.ne.u32 %q, %i, 5;
      |TEST:
      |setp.lt.u32 %p, %i, %t;
      |@%p bra EXIT;
      |bra.uni BODY;
      |EXIT:
      |$store""".stripMargin)
    val label = "convergence preheader 2": NonEmpty[String]
    assertEquals(
      found.schedule.retargets,
      Map(found.instructionAt("bra.uni", 0) -> Map(("TEST": NonEmpty[String]) -> label))
    )
    assertEquals(
      found.before(found.statements.size),
      Vec[Action](Action.Label(label), Action.Setup(0), Action.Jump("TEST"))
    )
    assertEquals(found.atLabel("EXIT"), Vec[Action](Action.Synchronise(0)))
    assertEquals(found.actions.size, 4)
  }

  test("a stub is guarded when the routine's last statement can fall through") {
    val found = analyse("""bra.uni TEST;
      |BODY:
      |add.u32 %i, %i, 1;
      |TEST:
      |setp.lt.u32 %p, %i, %t;
      |@%p bra EXIT;
      |bra.uni BODY;
      |EXIT:
      |ld.global.u32 %v, [%base];""".stripMargin)
    val label = "convergence preheader 2": NonEmpty[String]
    val end   = "convergence end": NonEmpty[String]
    assertEquals(
      found.before(found.statements.size),
      Vec[Action](Action.Jump(end), Action.Label(label), Action.Setup(0), Action.Jump("TEST"), Action.Label(end))
    )
  }

  private def setupSlots(found: Analysed): Vec[Int] = found.actions.collect { case Action.Setup(slot) => slot }

  test("sequential branches share one token slot") {
    val found = analyse(s"""setp.ne.u32 %p, %t, 0;
      |@%p bra J0;
      |add.u32 %v, %v, 1;
      |J0:
      |setp.ne.u32 %q, %t, 1;
      |@%q bra J1;
      |add.u32 %v, %v, 2;
      |J1:
      |setp.ne.u32 %a, %t, 2;
      |@%a bra J2;
      |add.u32 %v, %v, 3;
      |J2:
      |$store""".stripMargin)
    assertEquals(setupSlots(found), Vec(0, 0, 0))
    assertEquals(found.schedule.slots, 1)
  }

  test("nested branches use distinct token slots") {
    val found = analyse(s"""setp.ne.u32 %p, %t, 0;
      |@%p bra OUTER;
      |setp.ne.u32 %q, %t, 1;
      |@%q bra INNER;
      |add.u32 %v, %v, 1;
      |INNER:
      |add.u32 %v, %v, 2;
      |OUTER:
      |$store""".stripMargin)
    assertEquals(setupSlots(found), Vec(0, 1))
    assertEquals(found.schedule.slots, 2)
  }

  test("a loop exit hoisted over an inner branch does not share its slot") {
    val found = analyse(s"""mov.u32 %i, 0;
      |LOOP:
      |add.u32 %i, %i, 1;
      |setp.ge.u32 %p, %i, %t;
      |@%p bra DONE;
      |setp.ne.u32 %q, %t, 1;
      |@%q bra SKIP;
      |add.u32 %v, %v, 1;
      |SKIP:
      |add.u32 %v, %v, 2;
      |bra.uni LOOP;
      |DONE:
      |$store""".stripMargin)
    assertEquals(found.before(found.labelAt("LOOP")), Vec[Action](Action.Setup(0)))
    assertEquals(found.before(found.instructionAt("bra", 1)), Vec[Action](Action.Setup(1)))
    assertEquals(found.schedule.slots, 2)
  }

  test("sequential loops share one token slot") {
    val found = analyse(s"""mov.u32 %i, 0;
      |L0:
      |add.u32 %i, %i, 1;
      |setp.lt.u32 %p, %i, %t;
      |@%p bra L0;
      |mov.u32 %i, 0;
      |L1:
      |add.u32 %i, %i, 1;
      |setp.lt.u32 %q, %i, %t;
      |@%q bra L1;
      |$store""".stripMargin)
    assertEquals(setupSlots(found), Vec(0, 0))
    assertEquals(found.schedule.slots, 1)
  }

  test("a join reachable by a lane holding another live token does not share that token's slot") {
    val found = analyse(s"""setp.ne.u32 %p, %t, 0;
      |@%p bra SARM;
      |setp.ne.u32 %q, %t, 1;
      |@%q bra JB;
      |bra JA;
      |SARM:
      |setp.ne.u32 %a, %t, 2;
      |@%a bra JA;
      |add.u32 %v, %v, 1;
      |JA:
      |add.u32 %v, %v, 2;
      |JB:
      |$store""".stripMargin)
    val entered = setupSlots(found)
    assertEquals(entered.size, 3)
    assertEquals(entered.distinct.size, 3)
    val inner = found.atLabel("JA").collect { case Action.Synchronise(slot) => slot }
    assertEquals(inner.size, 1)
    assertEquals(found.before(found.instructionAt("bra", 1)).size, 1)
    assert(found.before(found.instructionAt("bra", 1)) != Vec[Action](Action.Setup(inner.head)))
  }
}
