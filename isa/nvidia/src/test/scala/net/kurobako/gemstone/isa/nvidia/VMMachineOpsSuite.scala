package net.kurobako.gemstone.isa.nvidia

import VMExecution.*
import VMIR.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.constraint.auto.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.semantics.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchGeometry.*
import programs.*
import scala.collection.mutable

class VMMachineOpsSuite extends munit.FunSuite {
  private val sharedBase = VMAddressSpace.SharedBase

  private class Fixture(lanes: Int in 1 ~ 1024 = 4) {
    val memory: RegionMemory   = RegionMemory(AllocationId(1) -> 64)
    val global: Long           = GuestAddress(RegionMemory.Base).raw
    val workgroup: VMWorkgroup = VMWorkgroup(lanes)
    val context: VMContext     = VMContext(
      kernel(Vec(Instruction.NoOp)),
      LaunchGeometry.forLaunch(Dimensions(lanes, 1, 1), Dimensions.One).toOption.get,
      Coordinates(0, 0, 0),
      workgroup,
      VMDevice(DeviceHeap(memory), TaskControl(), Workgroups.Sequential),
      0,
      VMWriteBuffer(16),
      VMFrame(emptyRoutine, programs.frame(lanes), 0L, None, VMLaneMask.all(lanes)),
      (_, _, _, _, _) => Right(())
    )
    context.active = VMLaneMask.all(lanes)
    val ops: VMMachineOps = VMMachineOps(context)

    def setRegister(index: Int, values: Long*): Unit = values.zipWithIndex.foreach((value, at) =>
      context.lane(lane(at)).setRegister(r(index), value)
    )

    def commit(): Unit = context.effects.commit(context.frame.lanes, workgroup, context.device.memory, Pc(0)) match {
      case Left(fault) => fail(fault.toString)
      case _           => ()
    }

    def sharedBytes(address: Long, length: Long): Either[Fault, List[Byte]] =
      ops.loadBytes(lane(0), Space.Shared, address, length, "read")(data => Right(data.toList))
  }

  private def lane(index: Int): WorkgroupLane = WorkgroupLane.fromIndex(index)

  private def split(step: Either[Fault, Step]): Either[Fault, Vec[(Int, Int)]] = step.map {
    case Step.Branches(arms, _) => arms.map(arm => (arm.target.toInt, arm.lanes.size.raw))
    case other                  => fail(s"not a split: $other")
  }

  test("control requests yield the step the interpreter takes") {
    val f = Fixture()
    assertEquals(f.ops.advance, Right(Step.Advance))
    assertEquals(f.ops.done, Right(()))
    assertEquals(f.ops.pc, 0L)
    assertEquals(f.ops.explicitCluster, 0L)
    assertEquals(f.ops.jump(Pc(3)), Right(Step.Jump(Pc(3))))
    assertEquals(f.ops.jumpTo(5L), Right(Step.Jump(Pc(5))))
    assertEquals(f.ops.jumpTo(-1L), Left(Fault.Backend("branch target -1 names no instruction")))
    assertEquals(f.ops.branch(1L, Pc(3)), Right(Step.Jump(Pc(3))))
    assertEquals(f.ops.branch(0L, Pc(3)), Right(Step.Advance))
    assertEquals(f.ops.exit, Right(Step.Exit(f.context.active)))
    assertEquals(f.ops.returnControl, Right(Step.Return(f.context.active)))
  }

  test("a uniform read takes the first active lane and per-lane requests split the lanes by location") {
    val f = Fixture()
    f.setRegister(1, 2L, 2L, 5L, 5L)
    assertEquals(f.ops.uniform(register(1)), 2L)
    f.context.active = VMLaneMask.Empty
    assertEquals(f.ops.uniform(register(1)), 0L)
    f.context.active = VMLaneMask.all(4)
    assertEquals(split(f.ops.jumpEach(at => f.context.read(at, register(1)))), Right(Vec((2, 2), (5, 2))))
    assertEquals(
      split(f.ops.jumpEach(at => if (at.raw == 0) -1L else 1L)),
      Left(Fault.Backend("branch target -1 names no instruction"))
    )
    assertEquals(split(f.ops.branchEach(at => (at.raw & 1).toLong, Pc(7))), Right(Vec((7, 2))))
  }

  test("the lanes a branch does not take fall through and stay on the current unit") {
    val f = Fixture()
    f.setRegister(1, 1L, 0L, 1L, 0L)
    f.ops.branchEach(at => f.context.read(at, register(1)), Pc(7)) match {
      case Right(Step.Branches(arms, fallThrough)) =>
        assertEquals(arms.map(arm => (arm.target.toInt, arm.lanes.size.raw)), Vec((7, 2)))
        assertEquals((0 until 4).filter(fallThrough.contains).toList, List(1, 3))
      case other => fail(s"not a branch: $other")
    }
  }

  test("a write and a store take effect when the instruction completes") {
    val f = Fixture()
    assertEquals(f.ops.write(lane(1), r(3), 42L)(f.ops.done), Right(()))
    assertEquals(f.ops.store(lane(0), Space.Shared, sharedBase + 16, 7L, 32)(f.ops.done), Right(()))
    assertEquals(f.context.lane(lane(1)).register(r(3)), 0L)
    f.commit()
    assertEquals(f.context.lane(lane(1)).register(r(3)), 42L)
    assertEquals(f.ops.load(lane(0), Space.Shared, sharedBase + 16, 32)(Right(_)), Right(7L))
  }

  test("scalar loads read the global and shared spaces and constant memory faults") {
    val f = Fixture()
    f.memory.putInt(AllocationId(1), 0, 99)
    assertEquals(f.ops.load(lane(0), Space.Global, f.global, 32)(Right(_)), Right(99L))
    assertEquals(f.ops.load(lane(0), Space.Generic, f.global, 32)(Right(_)), Right(99L))
    assertEquals(f.ops.load(lane(0), Space.Constant, 0L, 32)(Right(_)), Left(VMMachineOps.constantFault))
    assertEquals(
      f.ops.store(lane(0), Space.Constant, address = 0L, value = 1L, width = 32)(f.ops.done),
      Left(VMMachineOps.constantFault)
    )
  }

  test("byte accesses read and write the shared, global and generic spaces at once") {
    val f    = Fixture()
    val data = Array[Byte](1, 2, 3, 4)
    assertEquals(f.ops.storeBytes(lane(0), Space.Shared, sharedBase + 32, data, "write")(f.ops.done), Right(()))
    assertEquals(f.sharedBytes(sharedBase + 32, 4L), Right(List[Byte](1, 2, 3, 4)))
    assertEquals(f.ops.storeBytes(lane(0), Space.Global, f.global + 8, data, "write")(f.ops.done), Right(()))
    assertEquals(
      f.ops.loadBytes(lane(0), Space.Generic, f.global + 8, 4L, "read")(bytes => Right(bytes.toList)),
      Right(List[Byte](1, 2, 3, 4))
    )
    assertEquals(
      f.ops.loadBytes(lane(0), Space.Global, f.global + 8, 4L, "read")(bytes => Right(bytes.toList)),
      Right(List[Byte](1, 2, 3, 4))
    )
    assertEquals(f.sharedBytes(address = 0x10L, length = 4L), Left(Fault.Backend("read is not shared: 0x10")))
    assert(f.ops.storeBytes(lane(0), Space.Local, 0L, data, "write")(f.ops.done).isLeft)
    assert(f.ops.loadBytes(lane(0), Space.Local, address = 0L, length = 4L, what = "read")(bytes =>
      Right(bytes.toList)
    ).isLeft)
  }

  test("a check and a lane loop stop at the first fault") {
    val f       = Fixture()
    val visited = mutable.ListBuffer.empty[Int]
    assertEquals(f.ops.require(1L, "holds")(Fault.Backend("fails"))(f.ops.done), Right(()))
    assertEquals(f.ops.require(0L, "holds")(Fault.Backend("fails"))(f.ops.done), Left(Fault.Backend("fails")))
    val result = f.ops.everyLane { at =>
      visited += at.raw
      if (at.raw == 1) Left(Fault.Backend("stop")) else f.ops.done
    }(f.ops.advance)
    assertEquals(result, Left(Fault.Backend("stop")))
    assertEquals(visited.toList, List(0, 1))
  }

  test("a shared address names the CTA that holds it and a multicast mask the CTAs it selects") {
    val f      = Fixture()
    val offset = VMAddressSpace.sharedAddress(sharedBase + 8)
    val named  = mutable.ListBuffer.empty[(Boolean, Long)]
    assertEquals(
      f.ops.sharedWindow(lane(0), sharedBase + 8, 8L, "window") { (owner, at) =>
        named += ((owner eq f.workgroup, at))
        f.ops.done
      },
      Right(())
    )
    assertEquals(named.toList, List((true, offset)))
    assertEquals(
      f.ops.sharedWindow(lane(0), address = 0x10L, bytes = 8L, what = "window")((_, _) => f.ops.done),
      Left(Fault.Backend("window is not shared: 0x10"))
    )
    val visited                         = mutable.ListBuffer.empty[Boolean]
    val each: VMWorkgroup => Flow[Unit] = cta => {
      visited += (cta eq f.workgroup)
      f.ops.done
    }
    f.setRegister(2, 1L, 1L, 1L, 1L)
    assertEquals(f.ops.eachDestination(lane(0), f.workgroup, None)(each(_))(f.ops.done), Right(()))
    assertEquals(f.ops.eachDestination(lane(0), f.workgroup, Some(register(2)))(each(_))(f.ops.done), Right(()))
    assertEquals(visited.toList, List(true, true))
    f.setRegister(2, 2L, 2L, 2L, 2L)
    assertEquals(
      f.ops.eachDestination(lane(0), f.workgroup, Some(register(2)))(each(_))(f.ops.done),
      Left(Fault.Backend("multicast mask 0x2 names a CTA outside the cluster"))
    )
  }

  test("a cluster store and a transaction completion reach the CTA's shared memory and mbarrier") {
    val f       = Fixture()
    val address = sharedBase + 8
    val offset  = VMAddressSpace.sharedAddress(address)
    assertEquals(f.ops.storeCluster(f.workgroup, offset, Array[Byte](9, 8))(f.ops.done), Right(()))
    assertEquals(f.sharedBytes(address, 2L), Right(List[Byte](9, 8)))
    assert(f.ops.completeTx(f.workgroup, offset, 16L)(f.ops.done).isLeft)
    f.workgroup.mbarriers(offset) = MBarrier.create(1).toOption.get
    assertEquals(f.ops.completeTx(f.workgroup, offset, 16L)(f.ops.done), Right(()))
  }

  test("an mbarrier arrival yields the phase token and a completion counts transaction bytes") {
    val f       = Fixture()
    val address = sharedBase + 8
    assert(f.ops.mbarrierArrive(
      lane(0),
      address,
      count = 1L,
      bytes = 0L,
      drop = false,
      complete = true
    )(_ => f.ops.done).isLeft)
    assert(f.ops.mbarrierTransaction(lane(0), address, 16L, complete = true)(f.ops.done).isLeft)
    f.workgroup.mbarriers(VMAddressSpace.sharedAddress(address)) = MBarrier.create(2).toOption.get
    assert(f.ops.mbarrierArrive(
      lane(0),
      address,
      count = 1L,
      bytes = 0L,
      drop = false,
      complete = true
    )(_ => f.ops.done).isRight)
    assertEquals(f.ops.mbarrierTransaction(lane(0), address, 16L, complete = true)(f.ops.done), Right(()))
  }

  test("a barrier parks the lanes it names by warp and calls fault on unknown functions") {
    val f = Fixture()
    f.ops.park(barrier = 1L, count = 32L, blocking = true) match {
      case Right(Step.WorkgroupBarrier(arrivals, true, None)) => assertEquals(arrivals.size, 1)
      case other                                              => fail(s"not a barrier: $other")
    }
    assertEquals(
      f.ops.park(barrier = 16L, count = 32L, blocking = true),
      Left(Fault.Backend("barrier id 16 is outside 0..15"))
    )
    assertEquals(
      f.ops.call(Value.Immediate(0x1234L), Vec.empty, Vec.empty),
      Left(Fault.Backend("invalid function address 0x1234"))
    )
    f.setRegister(1, 0x99L, 0x99L, 0x99L, 0x99L)
    assertEquals(
      f.ops.call(register(1), Vec.empty, Vec.empty),
      Left(Fault.Backend("invalid function address 0x99"))
    )
  }

  test("a unit is converged when every warp with an active lane has all of its lanes active") {
    assertEquals(Fixture(4).ops.converged, 0L)
    assertEquals(Fixture(32).ops.converged, 1L)
    val wide = Fixture(64)
    assertEquals(wide.ops.converged, 1L)
    wide.context.active = VMLaneMask.all(32)
    assertEquals(wide.ops.converged, 1L)
    wide.context.active = VMLaneMask.all(40)
    assertEquals(wide.ops.converged, 0L)
    wide.context.active = VMLaneMask.Empty
    assertEquals(wide.ops.converged, 1L)
    assertEquals(wide.ops.laneIndex(lane(37)), 5L)
  }

  test("a gathered view is read by the peers of each warp and written to every active lane") {
    val f = Fixture(64)
    f.setRegister(1, (0 until 64).map(index => 100L + index)*)
    val result = f.ops.gather(UnitAlgebra.Slot.First, register(1)) { view =>
      f.ops.writeEach(Vec(r(2), r(3))) { (at, target) =>
        f.ops.peer(at, view, 31L - f.ops.laneIndex(at)) + 1000L * target
      }(f.ops.done)
    }
    assertEquals(result, Right(()))
    f.commit()
    for (index <- 0 until 64) {
      val expected = 100L + (index & ~31) + 31 - (index & 31)
      assertEquals(f.context.lane(lane(index)).register(r(2)), expected, s"lane $index")
      assertEquals(f.context.lane(lane(index)).register(r(3)), expected + 1000L, s"lane $index")
    }
  }

  test("a write of a gathered view reads before any write is staged") {
    val f = Fixture(32)
    f.setRegister(1, (0 until 32).map(_.toLong)*)
    val result = f.ops.gather(UnitAlgebra.Slot.First, register(1)) { view =>
      f.ops.writeEach(Vec(r(1)))((at, _) => f.ops.peer(at, view, 31L - f.ops.laneIndex(at)))(f.ops.done)
    }
    assertEquals(result, Right(()))
    f.commit()
    assertEquals(
      (0 until 32).map(index => f.context.lane(lane(index)).register(r(1))),
      (0 until 32).reverse.map(_.toLong)
    )
  }

  test("a gathered load reads the first lanes of each unit and holds zero on the rest") {
    val f = Fixture(64)
    f.setRegister(1, (0 until 64).map(index => sharedBase + 8L * (index % 32))*)
    (0 until 32).foreach { word =>
      f.ops.storeBytes(lane(0), Space.Shared, sharedBase + 8L * word, Array.fill[Byte](8)((word + 1).toByte), "fill")(
        f.ops.done
      )
    }
    val result = f.ops.gatherLoad(UnitAlgebra.Slot.Second, 4, Space.Shared, register(1), 0L, 64) { view =>
      Right((0 until 64).map(index => f.ops.peer(lane(index), view, f.ops.laneIndex(lane(index)))).toList)
    }
    val expected = (0 until 64).map(index => if ((index & 31) < 4) 0x0101010101010101L * (index % 32 + 1) else 0L)
    assertEquals(result, Right(expected.toList))
    f.setRegister(1, 0L)
    assert(f.ops.gatherLoad(UnitAlgebra.Slot.First, 4, Space.Global, register(1), 0L, 64)(_ => f.ops.done).isLeft)
  }

  test("a gathered view holds zero in the inactive lanes") {
    val f = Fixture(64)
    f.setRegister(1, (0 until 64).map(index => 100L + index)*)
    assertEquals(f.ops.gather(UnitAlgebra.Slot.First, register(1))(_ => f.ops.done), Right(()))
    f.context.active = VMLaneMask.all(4)
    val result = f.ops.gather(UnitAlgebra.Slot.First, register(1)) { view =>
      f.ops.writeEach(Vec(r(2)))((at, _) => f.ops.peer(at, view, 4L + f.ops.laneIndex(at)))(f.ops.done)
    }
    assertEquals(result, Right(()))
    f.commit()
    assertEquals((0 until 4).map(index => f.context.lane(lane(index)).register(r(2))).toList, List.fill(4)(0L))
  }

  test("a gathered load holds zero in the inactive lanes") {
    val f = Fixture(64)
    f.setRegister(1, (0 until 64).map(index => sharedBase + 8L * (index % 32))*)
    (0 until 32).foreach { word =>
      f.ops.storeBytes(lane(0), Space.Shared, sharedBase + 8L * word, Array.fill[Byte](8)((word + 1).toByte), "fill")(
        f.ops.done
      )
    }
    val loaded = f.ops.gatherLoad(UnitAlgebra.Slot.First, 32, Space.Shared, register(1), 0L, 64)(_ => f.ops.done)
    assertEquals(loaded, Right(()))
    f.context.active = VMLaneMask.all(4)
    val result = f.ops.gatherLoad(UnitAlgebra.Slot.First, 32, Space.Shared, register(1), 0L, 64) { view =>
      Right((0 until 8).map(index => f.ops.peer(lane(0), view, index.toLong)).toList)
    }
    assertEquals(result.map(_.drop(4)), Right(List.fill(4)(0L)))
    assert(result.exists(_.head != 0L))
  }

  test("peer reads zero outside its unit") {
    val f = Fixture(64)
    f.setRegister(1, (0 until 64).map(index => 100L + index)*)
    val result = f.ops.gather(UnitAlgebra.Slot.First, register(1)) { view =>
      Right(List(32L, -1L, 31L).map(index => f.ops.peer(lane(5), view, index)))
    }
    assertEquals(result, Right(List(0L, 0L, 131L)))
  }

  test("active reports whether a lane of the unit that holds a lane is active") {
    val f = Fixture(64)
    f.context.active = VMLaneMask.all(36)
    assertEquals(f.ops.unitSize, 32L)
    assertEquals(f.ops.active(lane(3), 0L), 1L)
    assertEquals(f.ops.active(lane(3), 31L), 1L)
    assertEquals(f.ops.active(lane(3), 32L), 0L)
    assertEquals(f.ops.active(lane(3), -1L), 0L)
    assertEquals(f.ops.active(lane(37), 3L), 1L)
    assertEquals(f.ops.active(lane(37), 4L), 0L)
  }

  test("a primitive is applied to its arguments") {
    val f = Fixture()
    assertEquals(f.ops.compute(Primitive[Primitive.Unary]("negate", -_), 5L), -5L)
    assertEquals(f.ops.compute(Primitive[Primitive.Binary]("add", _ + _), 5L, 6L), 11L)
    assertEquals(f.ops.compute(Primitive[Primitive.Ternary]("fold", (a, b, c) => a * b + c), 5L, 6L, 7L), 37L)
  }
}
