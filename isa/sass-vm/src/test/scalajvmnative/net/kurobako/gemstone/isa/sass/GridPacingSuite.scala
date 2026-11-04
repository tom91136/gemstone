package net.kurobako.gemstone.isa.sass

import java.util.concurrent.atomic.AtomicBoolean
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.sass.Harness.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.testing.either.*
import net.kurobako.gemstone.vm.*
import scala.util.Using

/** blocks must leave the grid barrier together, or a descheduled block queues rows into the wrong level */
class GridPacingSuite extends Suite {
  private val Rows   = 12
  private val Levels = Rows + 4
  private val Blocks = 4

  // parameters: levels, queue, queue count and barrier counters, then the row and level counts
  private val text =
    "S2R R0, SR_TID.X; S2R R8, SR_CTAID.X; ISETP.NE.AND P1, PT, R0, RZ, PT; MOV32I R10, 0x2; " +
      "level: SHR.U32 R3, R10, 0x1e; ISCADD R2.CC, R10, c[0x0][0x160], 0x2; IADD.X R3, R3, c[0x0][0x164]; " +
      "SSY `(barrier); @P1 SYNC; " +
      "IADD32I R11, R10, -0x2; SHR.U32 R13, R11, 0x1e; ISCADD R12.CC, R11, c[0x0][0x160], 0x2; " +
      "IADD.X R13, R13, c[0x0][0x164]; LDG.E.CV R4, [R12]; LDG.E.CV R6, [R12+0x4]; IADD R5, R4, R8; " +
      "item: ISETP.GE.U32.AND P0, PT, R5, R6, PT; @P0 BRA `(arrive); " +
      "SHR.U32 R13, R5, 0x1e; ISCADD R12.CC, R5, c[0x0][0x168], 0x2; IADD.X R13, R13, c[0x0][0x16c]; " +
      "LDG.E R14, [R12]; IADD32I R14, R14, 0x1; IADD32I R5, R5, 0x4; " +
      "ISETP.GE.U32.AND P0, PT, R14, c[0x0][0x180], PT; @P0 BRA `(item); " +
      "MOV R12, c[0x0][0x170]; MOV R13, c[0x0][0x174]; MOV32I R15, 0x1; ATOM.E.ADD R16, [R12], R15; " +
      "SHR.U32 R13, R16, 0x1e; ISCADD R12.CC, R16, c[0x0][0x168], 0x2; IADD.X R13, R13, c[0x0][0x16c]; " +
      "STG.E [R12], R14; BRA `(item); " +
      "arrive: SHR.U32 R13, R10, 0x1e; ISCADD R12.CC, R10, c[0x0][0x178], 0x2; IADD.X R13, R13, c[0x0][0x17c]; " +
      "MOV32I R15, 0x1; RED.E.ADD [R12], R15; " +
      "spin: LDG.E.CV R15, [R12]; ISETP.NE.AND P0, PT, R15, 0x4, PT; @P0 BRA `(spin); SYNC; " +
      "barrier: BAR.SYNC 0x0; MOV R12, c[0x0][0x170]; MOV R13, c[0x0][0x174]; " +
      "fetch: LDG.E.CV R9, [R12]; @!P1 STG.E [R2], R9; BAR.SYNC 0x0; " +
      "IADD32I R10, R10, 0x1; ISETP.LT.U32.AND P0, PT, R10, c[0x0][0x184], PT; @P0 BRA `(level); EXIT"

  // Listing.program places one statement every 16 bytes
  private def address(label: String): Long =
    text.split(';').map(_.trim).filter(_.nonEmpty).indexWhere(_.startsWith(s"$label:")) * 16L

  private val arrive = address("arrive")
  private val fetch  = address("fetch")

  test("a block descheduled after the grid barrier still stores the level end every block agreed on") {
    val kernel   = program(50, text)
    val compiled = kernel.compile(8, 8, 8, 8, 4, 4).fold(fail(_), identity)
    val memory   = Memory()
    val levels   = memory.allocate(words(Vec.tabulate(Levels)(level => if (level < 2) level.toLong else 0L)))
    val queue    = memory.allocate(words(Vec.fill(Rows)(0L)))
    val count    = memory.allocate(words(Vec(1L)))
    val counters = memory.allocate(words(Vec.fill(Levels)(0L)))
    val task     = KernelTask.validated(
      kernel.name,
      LaunchGeometry.Dimensions(32, 1, 1),
      LaunchGeometry.Dimensions(Blocks.unsafe[Int > 0], 1, 1),
      Vec(levels, queue, count, counters).map(KernelTask.Argument.Memory(_, 0L)) ++
        Vec(Rows.toLong, Levels.toLong).map(value =>
          KernelTask.Argument.Scalar(LittleEndian.toVec(value, 4).refine[NonEmpty[Vec[Byte]]].value)
        )
    ).value
    // block 0 arrives late so the others poll long enough to engage pacing before block 2 is descheduled
    val held                     = AtomicBoolean()
    val stalled                  = AtomicBoolean()
    val observer: LaunchObserver = (at, _) => {
      def first(address: Long, block: Int, flag: AtomicBoolean): Boolean =
        at.instruction.exists(_.raw == address) && at.workgroup.exists(_.x.raw == block) &&
          flag.compareAndSet(false, true)
      if (first(arrive, 0, held)) Thread.sleep(50)
      if (first(fetch, 2, stalled)) Thread.sleep(200)
      Right(())
    }
    Using.resource(WorkerPool(Blocks.unsafe[Int > 0]))(pool =>
      memory.run(compiled, task, TaskControl(observer = Some(observer)), pool).fold(
        error => fail(error.message),
        identity
      )
    )
    assert(held.get() && stalled.get())
    assertEquals(memory.readWords(count, 1).map(_.toInt), Vec(Rows))
    assertEquals(
      memory.readWords(levels, Levels.unsafe[Int >= 0]).map(_.toInt),
      Vec.tabulate(Levels)(math.min(_, Rows))
    )
  }
}
