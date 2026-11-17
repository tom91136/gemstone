package net.kurobako.gemstone.isa.check

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.explorer.*
import net.kurobako.gemstone.flow.Liveness
import net.kurobako.gemstone.flow.Window
import net.kurobako.gemstone.isa.amdgpu.*
import net.kurobako.gemstone.isa.amdgpu.asm.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.vm.RegisterUse

class AMDGPUMovesSuite extends Suite {
  private val kernel = WindowFixtures.kernel
  private val target = "gfx906"
  private val mode   = TargetMode(target.refine[Target].toOption.get)
  private val limit  = 24

  private val fixtures =
    Vec("loop" -> WindowFixtures.loop, "state" -> WindowFixtures.state, "copy" -> WindowFixtures.vectorCopy)

  private type Sample = (name: String, image: Vec[Byte], window: Window, moves: AMDGPUMoves)

  private lazy val samples: Vec[Sample] = fixtures.flatMap((name, image) =>
    AMDGPUWindows.of(image, kernel, target).toOption.get.map { window =>
      (name, image, window, AMDGPUMoves.of(image, kernel, target, window).toOption.get)
    }
  )

  private def proposed(seed: Long): Vec[(Sample, Vec[AMDGPUMoves.Candidate])] =
    samples.map(sample => sample -> sample.moves.propose(seed, limit))

  private lazy val first = proposed(1)

  private def text(candidate: AMDGPUMoves.Candidate): Vec[String] = candidate.instructions.map(Printer.render(_))

  private def bytes(candidate: AMDGPUMoves.Candidate): Vec[Byte] =
    candidate.instructions.flatMap(Binary.encode(_, mode).toOption.get)

  private def units(use: RegisterUse, names: Vec[String]): Set[String] =
    names.flatMap(name => Liveness.units(name, use.unitsOf(name))).toSet

  private def usesOf(candidate: AMDGPUMoves.Candidate): Vec[RegisterUse] = {
    val end  = Binary.encode(Instruction.decode("s_endpgm", Vec.empty).toOption.get, mode).toOption.get
    val code = bytes(candidate) ++ end
    AMDGPUModel.staticCode(WindowFixtures.withCode(code), kernel).toOption.get._2.take(candidate.instructions.size)
  }

  private def original(sample: Sample): Set[String] = {
    val uses = AMDGPUModel.staticCode(sample.image, kernel).toOption.get._2
    (sample.window.first to sample.window.last).flatMap(index => units(uses(index), uses(index).writes)).toSet
  }

  test("every proposed candidate is a legal instruction sequence that encodes and decodes back to itself") {
    val all = first.flatMap(_._2)
    assert(all.nonEmpty)
    all.foreach { candidate =>
      val encoded = bytes(candidate)
      val decoded = Disassembler(mode).decode(encoded).toOption.get.statements.collect {
        case Program.Statement.Instruction(value) => value
      }
      assertEquals(decoded.map(Printer.render(_)), text(candidate))
      assertEquals(decoded.flatMap(Binary.encode(_, mode).toOption.get), encoded)
    }
  }

  test("a candidate reads only what is live in or written before it, and writes no live-out register but the window's") {
    first.foreach { (sample, candidates) =>
      val writable = original(sample)
      candidates.foreach { candidate =>
        val defined = sample.window.liveIn
        usesOf(candidate).zipWithIndex.foldLeft(defined) { case (defined, (use, index)) =>
          val reads  = units(use, use.reads)
          val writes = units(use, use.writes)
          val clue   =
            s"${sample.name} ${sample.window.first}..${sample.window.last} ${candidate.kind} ${text(candidate)}"
          assertEquals(reads.diff(defined), Set.empty[String], s"reads $clue at $index")
          assertEquals(
            writes.intersect(sample.window.liveOut).diff(writable),
            Set.empty[String],
            s"writes $clue at $index"
          )
          defined ++ writes
        }
      }
    }
  }

  test("no candidate holds an instruction the equivalence checker rejects or one that writes EXEC") {
    val exec = Set("exec_lo", "exec_hi")
    first.flatMap(_._2).foreach { candidate =>
      val names = candidate.instructions.map(AMDGPUMoves.encoded(_))
      val kept  = AMDGPUWindows.admitted(target, names.toSet)
      names.foreach(name => assert(kept(name), s"$name is rejected in ${text(candidate)}"))
      usesOf(candidate).foreach(use => assertEquals(units(use, use.writes).intersect(exec), Set.empty[String]))
    }
  }

  test("the same seed gives the same candidates and another seed gives others") {
    assertEquals(proposed(1).map(_._2.map(c => (c.kind, text(c)))), first.map(_._2.map(c => (c.kind, text(c)))))
    assertNotEquals(proposed(2).map(_._2.map(c => (c.kind, text(c)))), first.map(_._2.map(c => (c.kind, text(c)))))
  }

  test("every kind of move is produced on the fixtures") {
    val kinds = first.flatMap(_._2.map(_.kind)).toSet
    assertEquals(kinds, AMDGPUMoves.Kind.values.toSet)
  }
}
