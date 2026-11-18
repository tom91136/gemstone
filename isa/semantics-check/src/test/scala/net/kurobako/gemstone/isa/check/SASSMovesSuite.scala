package net.kurobako.gemstone.isa.check

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.flow.ControlFlowGraph
import net.kurobako.gemstone.flow.Liveness
import net.kurobako.gemstone.flow.Window
import net.kurobako.gemstone.isa.sass.*
import net.kurobako.gemstone.isa.sass.asm.*
import net.kurobako.gemstone.isa.sass.fixtures.Fixture
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.vm.RegisterUse
import net.kurobako.gemstone.vm.Workgroups
import scala.concurrent.duration.*

class SASSMovesSuite extends Suite {
  override def baseTimeout: FiniteDuration = 120.seconds

  private val targetName = "sm_89"
  private val target     = Target.parse(targetName).toOption.get
  private val limit      = 24

  private lazy val loopKernel: Harness.Kernel = Harness.load(89, Fixture.listing("divergentSm89"), "divergent")

  private lazy val predicatedKernel: Harness.Kernel = Harness.program(
    89,
    """MOV R2, R1
      |ISETP.GT.U32.AND P0, PT, R0, 0x10, PT
      |@P0 MOV R2, R3
      |IADD3 R4, R2, 0x1, RZ
      |EXIT""".stripMargin
  )

  private lazy val guardedMemoryKernel: Harness.Kernel = Harness.program(
    89,
    """ISETP.GT.U32.AND P0, PT, R0, 0x10, PT
      |@P0 LDG.E R5, [R2.64]
      |IADD3 R4, R5, 0x1, RZ
      |@!P0 STG.E [R2.64], R4
      |EXIT""".stripMargin
  )

  private type Sample = (
      name: String,
      module: Cubin.Module,
      decoded: Map[NonEmpty[String], Vec[DecodedFunction]],
      function: NonEmpty[String],
      window: Window,
      moves: SASSMoves
  )

  private def analysed(
      module: Cubin.Module,
      decoded: Map[NonEmpty[String], Vec[DecodedFunction]],
      function: NonEmpty[String]
  ): (ControlFlowGraph, Vec[RegisterUse], Vec[DecodedInstruction]) = {
    val program =
      LoadedProgram(module, decoded, Workgroups.Sequential, instruction => Printer.print(instruction).toOption)
    program.staticCode(function).fold(error => fail(error.message), identity)
  }

  private def samplesOf(
      name: String,
      kernel: Harness.Kernel,
      parameters: Vec[Int],
      keep: Window => Boolean
  ): Vec[Sample] = {
    val module                      = kernel.module(parameters)
    val decoded                     = Map(kernel.name -> kernel.functions)
    val (graph, uses, instructions) = analysed(module, decoded, kernel.name)
    SASSWindows.of(graph, uses, instructions, targetName, 3).filter(keep).map { window =>
      (
        name = name,
        module = module,
        decoded = decoded,
        function = kernel.name,
        window = window,
        moves = SASSMoves.of(module, decoded, kernel.name, targetName, window).toOption.get
      )
    }
  }

  private lazy val copySamples: Vec[Sample] = {
    val module   = Cubin.decode(Fixture.copyAddCubin).toOption.get
    val function = "copy_add".unsafe[NonEmpty[String]]
    SASSWindows.of(Fixture.copyAddCubin, "copy_add", targetName).toOption.get.map { window =>
      (
        name = "copy",
        module = module,
        decoded = Map.empty[NonEmpty[String], Vec[DecodedFunction]],
        function = function,
        window = window,
        moves = SASSMoves.of(Fixture.copyAddCubin, "copy_add", targetName, window).toOption.get
      )
    }
  }

  private lazy val samples: Vec[Sample] =
    samplesOf("loop", loopKernel, Vec(24), window => at(window.first) == 0x200 && at(window.last) == 0x220) ++
      samplesOf("predicated", predicatedKernel, Vec.empty, _ => true) ++
      copySamples

  private def at(index: Int): Int = index * 16

  private def proposed(seed: Long): Vec[(Sample, Vec[SASSMoves.Candidate])] =
    samples.map(sample => sample -> sample.moves.propose(seed, limit))

  private lazy val first = proposed(1)

  private lazy val guardedMemory: Vec[(Sample, Vec[SASSMoves.Candidate])] =
    samplesOf("guarded memory", guardedMemoryKernel, Vec.empty, _ => true)
      .map(sample => sample -> sample.moves.propose(1, limit))

  private def all: Vec[SASSMoves.Candidate] = first.flatMap(_._2)

  private def render(instruction: Instruction): String = Printer.print(instruction).toOption.get

  private def text(candidate: SASSMoves.Candidate): Vec[String] =
    candidate.instructions.map(instruction => render(instruction.operation))

  private def encode(instructions: Vec[Instruction]): Vec[Byte] =
    instructions.zipWithIndex.flatMap((instruction, index) =>
      Vec.unsafeWrapArray(Assembler.encode(instruction, target, (16 * index).unsafe[Int >= 0]).toOption.get)
    )

  private def units(use: RegisterUse, names: Vec[String]): Set[String] =
    names.flatMap(name => Liveness.units(name, use.unitsOf(name))).toSet

  private def clue(sample: Sample, candidate: SASSMoves.Candidate): String =
    s"${sample.name} ${sample.window.first}..${sample.window.last} ${candidate.kind} ${text(candidate).mkString(" ; ")}"

  private def usesOf(sample: Sample, candidate: SASSMoves.Candidate): Vec[RegisterUse] = {
    val exit = DecodedInstruction(
      (16 * candidate.instructions.size).unsafe[Int >= 0],
      Parser.parse("EXIT;").toOption.get,
      None,
      None,
      None
    )
    val body         = candidate.instructions :+ exit
    val (_, uses, _) =
      analysed(sample.module, Map(sample.function -> Vec(DecodedFunction(sample.function, body))), sample.function)
    uses.take(candidate.instructions.size)
  }

  private def written(sample: Sample): Set[String] = {
    val (_, uses, _) = analysed(sample.module, sample.decoded, sample.function)
    (sample.window.first to sample.window.last).flatMap(index => units(uses(index), uses(index).writes)).toSet
  }

  test("every proposed candidate encodes and decodes back to itself") {
    assert(all.nonEmpty)
    all.foreach { candidate =>
      val sequence = candidate.instructions.map(_.operation)
      val encoded  = encode(sequence)
      val decoded  = new Disassembler(target).decode(encoded).toOption.get.statements.collect {
        case statement: Program.Statement.Operation => statement.instruction
      }
      assertEquals(decoded.map(render), sequence.map(render))
      assertEquals(encode(decoded), encoded)
    }
  }

  test("every candidate holds only instructions that the window checker admits") {
    assert(all.nonEmpty)
    all.foreach { candidate =>
      val admitted = SASSWindows.admitted(targetName, candidate.instructions)
      assert(admitted.forall(verdict => verdict), s"rejected in ${clue(first.head._1, candidate)}")
    }
  }

  test("a candidate reads only what is live in or written before it") {
    assert(all.nonEmpty)
    first.foreach { (sample, candidates) =>
      candidates.foreach { candidate =>
        usesOf(sample, candidate).zipWithIndex.foldLeft(sample.window.liveIn) { case (defined, (use, index)) =>
          assertEquals(
            units(use, use.reads).diff(defined),
            Set.empty[String],
            s"reads ${clue(sample, candidate)} at $index"
          )
          defined ++ units(use, use.writes)
        }
      }
    }
  }

  test("a candidate writes no live-out register that the window does not write") {
    assert(all.nonEmpty)
    first.foreach { (sample, candidates) =>
      val writable = written(sample)
      candidates.foreach { candidate =>
        usesOf(sample, candidate).zipWithIndex.foreach { (use, index) =>
          assertEquals(
            units(use, use.writes).intersect(sample.window.liveOut).diff(writable),
            Set.empty[String],
            s"writes ${clue(sample, candidate)} at $index"
          )
        }
      }
    }
  }

  test("the same seed gives the same candidates and another seed gives others") {
    assert(all.nonEmpty)
    def shape(proposals: Vec[(Sample, Vec[SASSMoves.Candidate])]) =
      proposals.map(_._2.map(candidate => (candidate.kind, text(candidate))))
    assertEquals(shape(proposed(1)), shape(first))
    assertNotEquals(shape(proposed(2)), shape(first))
  }

  test("every kind of move is produced on the fixtures") {
    assertEquals(all.map(_.kind).toSet, SASSMoves.Kind.values.toSet)
  }

  test("kept instructions keep their encoding word and scheduling, changed and inserted ones have neither") {
    first.filter(_._1.name == "copy").foreach { (sample, candidates) =>
      val (_, _, instructions) = analysed(sample.module, sample.decoded, sample.function)
      val source               = instructions.slice(sample.window.first, sample.window.last + 1)
      val words                = source.flatMap(_.encodingWord).toSet
      val schedules            = source.flatMap(_.scheduling).toSet
      candidates.foreach { candidate =>
        val fresh = if (candidate.kind == SASSMoves.Kind.Delete) 0 else 1
        assertEquals(candidate.instructions.count(_.encodingWord.isEmpty), fresh, clue(sample, candidate))
        assertEquals(candidate.instructions.count(_.scheduling.isEmpty), fresh, clue(sample, candidate))
        candidate.instructions.foreach { instruction =>
          assert(instruction.encodingWord.forall(words.contains), clue(sample, candidate))
          assert(instruction.scheduling.forall(schedules.contains), clue(sample, candidate))
        }
      }
    }
  }

  private def source(sample: Sample): Vec[DecodedInstruction] =
    analysed(sample.module, sample.decoded, sample.function)._3.slice(sample.window.first, sample.window.last + 1)

  test("every move keeps the guard of the instruction it changes or copies, and none guards another") {
    val guarded = (first ++ guardedMemory).map((sample, candidates) => (sample, candidates, source(sample)))
      .filter((_, _, original) => original.exists(_.guard.nonEmpty))
    assertEquals(guarded.map(_._1.name).distinct, Vec("predicated", "guarded memory"))
    assertEquals(guarded.flatMap(_._2).map(_.kind).toSet, SASSMoves.Kind.values.toSet)
    val changed = guarded.flatMap { (sample, candidates, original) =>
      val held = original.flatMap(_.guard)
      val own  = held.toSet
      val text = original.map(instruction => render(instruction.operation)).toSet
      candidates.flatMap { candidate =>
        val kept = candidate.instructions.flatMap(_.guard)
        candidate.kind match {
          case SASSMoves.Kind.Delete =>
            assert(kept.forall(own) && held.size - kept.size <= 1, clue(sample, candidate))
          case SASSMoves.Kind.Insert =>
            assert(kept.forall(own) && kept.size - held.size <= 1, clue(sample, candidate))
          case _ => assertEquals(kept, held, clue(sample, candidate))
        }
        candidate.instructions.filter(_.guard.nonEmpty).map(instruction => render(instruction.operation))
          .filterNot(text)
      }
    }
    assert(changed.nonEmpty, "no move changed or copied a guarded instruction")
  }

  test("a guarded load or store stays as it is, guard and all") {
    val held = guardedMemory.map((sample, candidates) => (sample, candidates, accesses(source(sample))))
    assert(held.exists((_, candidates, kept) => kept.exists(_.guard.nonEmpty) && candidates.nonEmpty), held.map(_._3))
    held.foreach((sample, candidates, kept) =>
      candidates.foreach { candidate =>
        assertEquals(accesses(candidate.instructions), kept, clue(sample, candidate))
        assert(SASSWindows.admitted(targetName, candidate.instructions).forall(identity), clue(sample, candidate))
      }
    )
  }

  private val memory = Set("LDG", "STG", "LDGSTS")

  /** The loads and stores of `instructions` as decoded, wherever they sit. */
  private def accesses(instructions: Vec[DecodedInstruction]): Vec[DecodedInstruction] =
    instructions.filter(instruction => memory(instruction.mnemonic.raw)).map(_.copy(address = 0.unsafe[Int >= 0]))

  test("a window's loads and stores are kept as decoded and in their order, and no move creates one") {
    val sources = first.map { (sample, candidates) =>
      val (_, _, instructions) = analysed(sample.module, sample.decoded, sample.function)
      (sample, candidates, accesses(instructions.slice(sample.window.first, sample.window.last + 1)))
    }
    assert(sources.exists((_, candidates, kept) => kept.nonEmpty && candidates.nonEmpty), "no window holds a load")
    sources.foreach((sample, candidates, kept) =>
      candidates.foreach(candidate => assertEquals(accesses(candidate.instructions), kept, clue(sample, candidate)))
    )
  }
}
