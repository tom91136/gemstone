package net.kurobako.gemstone.isa.check

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.flow.ControlFlowGraph
import net.kurobako.gemstone.flow.Liveness
import net.kurobako.gemstone.flow.Window
import net.kurobako.gemstone.isa.sass.*
import net.kurobako.gemstone.isa.sass.asm.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.vm.RegisterUse
import net.kurobako.gemstone.vm.Workgroups

abstract class SASSMovesChecks extends Suite {
  protected val targetName     = "sm_89"
  protected val target: Target = Target.parse(targetName).toOption.get
  protected val limit          = 24

  protected type Sample = (
      name: String,
      module: Cubin.Module,
      decoded: Map[NonEmpty[String], Vec[DecodedFunction]],
      function: NonEmpty[String],
      window: Window,
      moves: SASSMoves
  )

  protected def samples: Vec[Sample]

  protected def analysed(
      module: Cubin.Module,
      decoded: Map[NonEmpty[String], Vec[DecodedFunction]],
      function: NonEmpty[String]
  ): (ControlFlowGraph, Vec[RegisterUse], Vec[DecodedInstruction]) = {
    val program =
      LoadedProgram(module, decoded, Workgroups.Sequential, instruction => Printer.print(instruction).toOption)
    program.staticCode(function).fold(error => fail(error.message), identity)
  }

  protected def samplesOf(
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

  protected def proposed(seed: Long): Vec[(Sample, Vec[SASSMoves.Candidate])] =
    samples.map(sample => sample -> sample.moves.propose(seed, limit))

  protected lazy val first: Vec[(Sample, Vec[SASSMoves.Candidate])] = proposed(1)

  protected def all: Vec[SASSMoves.Candidate] = first.flatMap(_._2)

  protected def render(instruction: Instruction): String = Printer.print(instruction).toOption.get

  protected def text(candidate: SASSMoves.Candidate): Vec[String] =
    candidate.instructions.map(instruction => render(instruction.operation))

  protected def encode(instructions: Vec[Instruction]): Vec[Byte] =
    instructions.zipWithIndex.flatMap((instruction, index) =>
      Vec.unsafeWrapArray(Assembler.encode(instruction, target, (16 * index).unsafe[Int >= 0]).toOption.get)
    )

  protected def units(use: RegisterUse, names: Vec[String]): Set[String] =
    names.flatMap(name => Liveness.units(name, use.unitsOf(name))).toSet

  protected def clue(sample: Sample, candidate: SASSMoves.Candidate): String =
    s"${sample.name} ${sample.window.first}..${sample.window.last} ${candidate.kind} ${text(candidate).mkString(" ; ")}"

  protected def usesOf(sample: Sample, candidate: SASSMoves.Candidate): Vec[RegisterUse] = {
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

  protected def written(sample: Sample): Set[String] = {
    val (_, uses, _) = analysed(sample.module, sample.decoded, sample.function)
    (sample.window.first to sample.window.last).flatMap(index => units(uses(index), uses(index).writes)).toSet
  }

  protected def source(sample: Sample): Vec[DecodedInstruction] =
    analysed(sample.module, sample.decoded, sample.function)._3.slice(sample.window.first, sample.window.last + 1)

  protected lazy val guarded: Vec[(Sample, Vec[SASSMoves.Candidate], Vec[DecodedInstruction])] =
    first.map((sample, candidates) => (sample, candidates, source(sample)))
      .filter((_, _, original) => original.exists(_.guard.nonEmpty))

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

  test("every move keeps the guard of the instruction it changes or copies, and none guards another") {
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
}
