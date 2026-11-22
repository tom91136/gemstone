package net.kurobako.gemstone.isa.check

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.sass.*
import net.kurobako.gemstone.isa.sass.fixtures.Fixture
import scala.concurrent.duration.*

class SASSMovesSuite extends SASSMovesChecks {
  override def baseTimeout: FiniteDuration = 120.seconds

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

  protected lazy val samples: Vec[Sample] =
    samplesOf("loop", loopKernel, Vec(24), window => at(window.first) == 0x200 && at(window.last) == 0x220) ++
      samplesOf("predicated", predicatedKernel, Vec.empty, _ => true) ++
      samplesOf("guarded memory", guardedMemoryKernel, Vec.empty, _ => true) ++
      copySamples

  private lazy val guardedMemory: Vec[(Sample, Vec[SASSMoves.Candidate])] =
    first.filter(_._1.name == "guarded memory")

  private def at(index: Int): Int = index * 16

  test("every kind of move is produced on the fixtures") {
    assertEquals(all.map(_.kind).toSet, SASSMoves.Kind.values.toSet)
  }

  test("the guarded samples are predicated and guarded memory, and produce every kind of move") {
    assertEquals(guarded.map(_._1.name).distinct, Vec("predicated", "guarded memory"))
    assertEquals(guarded.flatMap(_._2).map(_.kind).toSet, SASSMoves.Kind.values.toSet)
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

  test("a window with every instruction deleted proposes no insertion") {
    val sample = samples.head
    val empty  = sample.moves.from(Vec.empty[DecodedInstruction]).toOption.get
    assertEquals(empty.candidates(SASSMoves.Kind.Insert, scala.util.Random(1), limit), Vec.empty[SASSMoves.Candidate])
  }
}
