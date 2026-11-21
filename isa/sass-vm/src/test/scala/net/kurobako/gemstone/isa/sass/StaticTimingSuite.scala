package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.profile.Pipe
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.vm.*

class StaticTimingSuite extends Suite {

  private val name = "sumloop".unsafe[NonEmpty[String]]

  private lazy val program: LoadedProgram = {
    val image = getClass.getResourceAsStream("/net/kurobako/gemstone/isa/sass/controlwords/branches.sm_89.cubin")
    val bytes = Vec.unsafeWrapArray(image.readAllBytes())
    LoadedProgram(
      Cubin.decode(bytes).fold(error => fail(error.raw), identity),
      Map.empty,
      Workgroups.Sequential,
      _ => None
    )
  }

  private lazy val instructions: Vec[DecodedInstruction] =
    program.staticCode(name).fold(error => fail(error.message), _._3)

  private lazy val timing = program.staticTiming(name).fold(error => fail(error.message), identity)

  test("each instruction has its offset") {
    assertEquals(timing.map(_.offset), instructions.map(_.address.raw.toLong))
  }

  test("each instruction that lowers has the schedule its control word encodes") {
    val unlowered = instructions.zip(timing).filter((_, row) => row.schedule.isEmpty).map(_._1.mnemonic.raw).distinct
    assertEquals(unlowered, Vec("NOP"))
    instructions.zip(timing).filter(_._2.schedule.nonEmpty).foreach { (instruction, row) =>
      assertEquals(
        row.schedule,
        instruction.scheduling.map(word => Schedule.Encoded(word.stall, word.writeBarrier.nonEmpty))
      )
    }
  }

  test("integer arithmetic issues on its pipes") {
    val pipes = instructions.zip(timing).map((instruction, row) => instruction.mnemonic.raw -> row.pipe)
    assert(pipes.contains("IADD3" -> Some(Pipe.Alu)), pipes)
    assert(pipes.contains("IMAD" -> Some(Pipe.Fma)), pipes)
    assert(pipes.contains("ISETP" -> Some(Pipe.Alu)), pipes)
  }

  test("a launch's resources hold the kernel's registers and the lanes asked for") {
    val resources =
      program.staticResources(name, 128.unsafe[Int > 0], 0L.unsafe[Long >= 0]).fold(e => fail(e.message), identity)
    assertEquals(resources.workgroupLanes.raw, 128)
    assertEquals(resources.subgroupSize.raw, 32)
    assert(resources.registersPerLane.exists(_.raw > 0), resources)
  }
}
