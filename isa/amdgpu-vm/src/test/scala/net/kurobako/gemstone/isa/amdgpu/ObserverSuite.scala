package net.kurobako.gemstone.isa.amdgpu

import VMIR.*
import fixtures.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.vm.*
import scala.collection.mutable

class ObserverSuite extends munit.FunSuite {
  private val operations = Vec(
    Instruction.SMove(5, InstructionSource.Constant(42L)),
    Instruction.Wait,
    Instruction.End
  )

  test("an observer sees each wave instruction's byte offset and the wave's registers") {
    val seen                     = mutable.ArrayBuffer.empty[(Long, Long, Long)]
    val observer: LaunchObserver = (at, state) =>
      state match {
        case wave: WaveView =>
          seen += ((at.instruction.fold(-1L)(_.raw), wave.exec, wave.scalar(5)))
          Right(())
        case other => Left(Fault.InvalidRequest(s"unexpected state $other"))
      }
    val result =
      executeKernel(irKernel(operations), singleTask(), RegionMemory(), TaskControl(observer = Some(observer)))
    assertEquals(result, Right(()))
    assertEquals(seen.toVector, Vector((0L, 1L, 0L), (4L, 1L, 42L), (8L, 1L, 42L)))
  }

  test("an observer stops a wave before the instruction executes") {
    val stop: LaunchObserver = (at, _) => if (at.instruction.exists(_.raw == 4L)) Left(Fault.Cancelled) else Right(())
    assertEquals(
      executeKernel(irKernel(operations), singleTask(), RegionMemory(), TaskControl(observer = Some(stop))),
      Left(Fault.Cancelled)
    )
  }
}
