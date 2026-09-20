package net.kurobako.gemstone.isa.ptx.v9_3

import VirtualMachine.*

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*
import net.kurobako.gemstone.Vec

final class MachineSuite extends munit.FunSuite {
  private enum TestWrite extends Write {
    case Value(value: Int)
  }

  test("an executable program is immutable and bounds checked") {
    val program = ExecutableProgram(Vec("first", "second"))
    assertEquals(program.at(Pc.Entry), Right("first"))
    assert(program.at(Pc(2)).left.exists(_.isInstanceOf[ExecutionFault.InvalidProgram]))
  }

  test("lane masks support CUDA-sized blocks and set operations") {
    val mask = LaneMask.all(256).toOption.get
    assertEquals(mask.size, 256)
    val selected = mask.filterLanes(_ % 32 == 0)
    assertEquals(selected.size, 8)
    assertEquals(selected.union(mask.subtract(selected)).size, 256)
    assertEquals(selected.intersect(mask.subtract(selected)).size, 0)
  }

  test("lane mask exposes each warp's active bits") {
    val lanes = LaneMask.all(96).toOption.get
    assertEquals(lanes.warpBits(0), 0xffffffffL)
    assertEquals(lanes.warpBits(32), 0xffffffffL)
    assertEquals(lanes.warpBits(64), 0xffffffffL)
    assertEquals(lanes.filterLanes(_ != 70).warpBits(64), 0xffffffffL & ~(1L << 6))
  }

  test("effects validate every write before committing any") {
    val effects = Effects[TestWrite]
    val applied = Vec.newBuilder[Int]
    effects.stage(TestWrite.Value(1))
    effects.stage(TestWrite.Value(-1))
    val result = effects.commit(
      {
        case TestWrite.Value(value) if value >= 0 => Right(())
        case TestWrite.Value(value)               =>
          Left(ExecutionFault.InvalidState(s"negative write: $value"))
      },
      { case TestWrite.Value(value) => applied += value }
    )
    assert(result.isLeft)
    assertEquals(applied.result().toSeq, Seq.empty)
    assert(effects.isEmpty)
  }
}
