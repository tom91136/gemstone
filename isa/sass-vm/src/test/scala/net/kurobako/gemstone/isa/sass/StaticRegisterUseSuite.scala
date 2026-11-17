package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.isa.sass.Harness.*
import net.kurobako.gemstone.isa.sass.asm.*
import net.kurobako.gemstone.isa.sass.fixtures.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.vm.*
import scala.collection.mutable
import scala.collection.mutable.HashMap

/** Every register an executed fixture kernel reads or writes at an instruction, as the machine reports it, is in the
  * set the static use of that instruction names. The static side may name more: guard predicates of instructions the
  * kernel never guards, condition flags, and a slot a predicated write may leave unchanged.
  */
class StaticRegisterUseSuite extends Suite {

  private class Collector extends LaunchObserver {
    private val addresses                = mutable.HashMap.empty[Long, Long]
    val uses: HashMap[Long, RegisterUse] = mutable.HashMap.empty[Long, RegisterUse]

    def instruction(at: Fault.Location, state: ArchitecturalState): Either[Fault, Unit] = {
      at.instruction.foreach(address => addresses(state.issued) = address.raw)
      Right(())
    }

    override def registers(ordinal: Long, use: => RegisterUse): Unit =
      addresses.get(ordinal).foreach(address =>
        uses(address) = uses.getOrElse(address, RegisterUse.empty).merge(use)
      )
  }

  private def differences(kernel: Kernel, block: Int, args: Arg*)(using munit.Location): Vec[String] = {
    val image  = kernel.module(Vec.from(args.map(_.size)))
    val loaded = LoadedProgram(image, Map(kernel.name -> kernel.functions), Workgroups.Sequential, Harness.text)
    val (_, staticUses, instructions) = loaded.staticCode(kernel.name).toOption.get
    assertEquals(staticUses.size, instructions.size, "one use per decoded instruction")
    val collector = Collector()
    val _         = kernel.launch(block, control = TaskControl(observer = Some(collector)))(args*)
    val found     = Vec.newBuilder[String]
    instructions.zipWithIndex.foreach { (instruction, index) =>
      val address = instruction.address.raw
      collector.uses.get(address).foreach { dynamic =>
        val static        = staticUses(index)
        val missingReads  = dynamic.reads.toSet -- static.reads.toSet
        val missingWrites = dynamic.writes.toSet -- static.writes.toSet
        if (missingReads.nonEmpty || missingWrites.nonEmpty)
          found += s"0x${address.toHexString} `${Printer.print(instruction).toOption.getOrElse("")}` " +
            s"reads ${missingReads.toSeq.sorted} writes ${missingWrites.toSeq.sorted}"
      }
    }
    found.result()
  }

  test("a loop kernel's dynamic register use is within its static register use") {
    val kernel = Harness.load(89, Fixture.listing("saxpyO0Sm89"), "saxpy")
    val y      = Arg.zeros(16)
    val x      = Arg.words(0L, 1L, 2L, 3L)
    val found  = differences(kernel, 2, y, x, Arg.f32(2.0f), Arg.u32(2L))
    assert(found.isEmpty, found.mkString("\n"))
  }

  test("a predicated write is read by its instruction and never loses a dynamic registration") {
    val kernel = program(
      89,
      """S2R R0, SR_TID.X
        |ISETP.GT.U32.AND P0, PT, R0, 0x10, PT
        |@P0 MOV R1, 0x2
        |MOV R2, R1
        |EXIT""".stripMargin
    )
    val found = differences(kernel, 32)
    assert(found.isEmpty, found.mkString("\n"))
  }

  test("the fixture kernels the profile suite runs keep their dynamic register use") {
    val kernel = Harness.load(89, Fixture.listing("sharedStrideSm89"), "shared_stride", 4224)
    val found  = differences(kernel, 32, Arg.zeros(65536), Arg.u32(1L))
    assert(found.isEmpty, found.mkString("\n"))
  }
}
