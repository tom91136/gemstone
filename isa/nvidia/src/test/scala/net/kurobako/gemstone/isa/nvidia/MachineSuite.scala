package net.kurobako.gemstone.isa.nvidia

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.nvidia.*
import net.kurobako.gemstone.isa.nvidia.VMIR.*
import scala.compiletime.testing.typeCheckErrors

class MachineSuite extends munit.FunSuite {
  test("lane mask operations retain bounded counts and expose positive counts") {
    val full = VMLaneMask.all(1024).toOption.get
    val even = full.filterLanes(_.raw % 2 == 0)
    val odd  = full.subtract(even)
    assertEquals(full.positiveSize.map(_.raw), Some(1024))
    assertEquals(even.positiveSize.map(_.raw), Some(512))
    assertEquals(even.union(odd).positiveSize.map(_.raw), Some(1024))
    assertEquals(even.intersect(odd).positiveSize, None)
    assertEquals(VMLaneMask.Empty.positiveSize, None)
    assertEquals(full.subtract(full).size.raw, 0)
    assertEquals(
      scala.compiletime.testing.typeCheckErrors("""
      import net.kurobako.gemstone.constraint.*
      val full = VMLaneMask.all(1024).toOption.get
      val masks = Seq(full, full.filterLanes(_ => true), full.union(full),
        full.intersect(full), full.subtract(full), VMLaneMask.Empty)
      val sizes: Seq[Int in 0 ~ 1024] = masks.map(_.size)
      val positive: Option[Int in 1 ~ 1024] = full.positiveSize
    """),
      Nil
    )
  }

  test("an executable program looks up valid PCs and rejects out-of-range PCs") {
    val program = ExecutableProgram(Vec("first", "second"))
    assertEquals(program.at(Pc.Entry), Some("first"))
    assertEquals(program.at(Pc(2)), None)
  }

  test("lane masks support CUDA-sized blocks and set operations") {
    val mask = VMLaneMask.all(256).toOption.get
    assertEquals(mask.size.raw, 256)
    val selected  = mask.filterLanes(_.raw % 32 == 0)
    val remaining = mask.subtract(selected)
    val reunited  = selected.union(remaining)
    val overlap   = selected.intersect(remaining)
    (0 until 256).foreach { lane =>
      assertEquals(selected.contains(lane), lane  % 32 == 0, s"selected lane $lane")
      assertEquals(remaining.contains(lane), lane % 32 != 0, s"remaining lane $lane")
      assert(reunited.contains(lane), s"missing reunited lane $lane")
      assert(!overlap.contains(lane), s"unexpected overlapping lane $lane")
    }
  }

  test("lane mask exposes each warp's active bits") {
    val lanes = VMLaneMask.all(96).toOption.get
    assertEquals(lanes.warpBits(0), 0xffffffffL)
    assertEquals(lanes.warpBits(32), 0xffffffffL)
    assertEquals(lanes.warpBits(64), 0xffffffffL)
    assertEquals(lanes.filterLanes(_.raw != 70).warpBits(64), 0xffffffffL & ~(1L << 6))
  }

  test("lane iteration carries a bounded index including the last physical lane") {
    val lanes = Vec.newBuilder[WorkgroupLane]
    VMLaneMask.all(1024).toOption.get.foreachLane { lane =>
      val _ = lanes += lane
    }
    val values = lanes.result()
    assertEquals(values.size, 1024)
    assertEquals(values.last.raw.toInt, 1023)
    assertEquals(VMLaneMask.Empty.size.raw, 0)
    assert(VMLaneMask.one(-1).isLeft)
    assert(VMLaneMask.all(1025).isLeft)
    assertEquals(WorkgroupLane.inWarp(992, 31).raw.toInt, 1023)
    assertEquals(VMLaneMask.Empty.headOption, None)
    assertEquals(VMLaneMask.one(1023).toOption.get.headOption.map(_.raw.toInt), Some(1023))
  }

  test("compiled register slots reject sentinel and out-of-bank literals") {
    assert(typeCheckErrors("""
      import net.kurobako.gemstone.isa.nvidia.VMIR.*
      Value.RegisterSlot(-1)
    """).nonEmpty)
    assert(typeCheckErrors("""
      import net.kurobako.gemstone.isa.nvidia.VMIR.*
      Value.PredicateSlot(1024)
    """).nonEmpty)
    assert(typeCheckErrors("""
      import net.kurobako.gemstone.isa.nvidia.*
      VMRegisterFile(0)
    """).nonEmpty)
    assert(typeCheckErrors("""
      import net.kurobako.gemstone.isa.nvidia.*
      import net.kurobako.gemstone.constraint.*
      val lane: WorkgroupLane = 1024
    """).nonEmpty)
    assertEquals(VMRegisterFile.slot("%p1023").map(_.raw), Some(6143))
    assertEquals(VMPredicateFile.slot("%p1023").map(_.raw), Some(1023))
    assertEquals(VMRegisterFile.slot("%r1024"), None)
    assertEquals(VMPredicateFile.slot("%r0"), None)
  }

  test("workgroup lane construction rejects out-of-range values before narrowing") {
    val _ = intercept[IllegalArgumentException](WorkgroupLane.fromIndex(-1))
    val _ = intercept[IllegalArgumentException](WorkgroupLane.fromIndex(1024))
    val _ = intercept[IllegalArgumentException](WorkgroupLane.fromIndex(65536))
  }

}
