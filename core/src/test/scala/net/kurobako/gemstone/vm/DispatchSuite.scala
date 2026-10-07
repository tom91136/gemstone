package net.kurobako.gemstone.vm

import java.util.concurrent.ConcurrentLinkedQueue
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.memory.*
import scala.jdk.CollectionConverters.*

class DispatchSuite extends munit.FunSuite {
  private def geometry(globalX: Long): LaunchGeometry =
    LaunchGeometry.forLaunch(
      LaunchGeometry.Dimensions(4, 2, 1),
      LaunchGeometry.Dimensions(3, 1, 1),
      Some(LaunchGeometry.GlobalDimensions(globalX.unsafe[Long > 0], 2L, 1L))
    ).fold(message => fail(message), identity)

  test("launch geometry stores validated volumes and maps linear indices without failure") {
    val launch = geometry(12L)
    assertEquals(launch.groupVolume.raw, 8)
    assertEquals(launch.groupTotal.raw, 3L)
    assert(launch.uniform)
    assertEquals(launch.localCoordinates(5), LaunchGeometry.Coordinates(1, 1, 0))
    assertEquals(launch.groupCoordinates(2L), LaunchGeometry.Coordinates(2, 0, 0))
    assertEquals(
      LaunchGeometry.Dimensions(3, 5, 7).coordinates(3L * 5L * 7L - 1L),
      LaunchGeometry.Coordinates(2, 4, 6)
    )
    assert(
      LaunchGeometry.forLaunch(LaunchGeometry.Dimensions(65536, 65536, 1), LaunchGeometry.Dimensions.One).isLeft
    )
  }

  test("live lanes take the full-group fast path and mask partial groups by global extent") {
    val launch = geometry(10L)
    assert(!launch.uniform)
    assert(launch.isFull(LaunchGeometry.Coordinates(1, 0, 0)))
    assert(!launch.isFull(LaunchGeometry.Coordinates(2, 0, 0)))
    assertEquals(launch.liveLanes(LaunchGeometry.Coordinates(0, 0, 0), 0, 8), 0xffL)
    assertEquals(launch.liveLanes(LaunchGeometry.Coordinates(2, 0, 0), 0, 8), 0x33L)
    assertEquals(launch.liveLanes(LaunchGeometry.Coordinates(2, 0, 0), 4, 4), 0x3L)
    assert(launch.contains(LaunchGeometry.Coordinates(2, 0, 0), LaunchGeometry.Coordinates(1, 1, 0)))
    assert(!launch.contains(LaunchGeometry.Coordinates(2, 0, 0), LaunchGeometry.Coordinates(2, 0, 0)))
  }

  test("group dispatch visits every group once and reports the first ISA failure") {
    val launch  = geometry(12L)
    val visited = ConcurrentLinkedQueue[(Long, LaunchGeometry.Coordinates)]()
    assertEquals(
      Dispatch.forEachGroup(launch, Workgroups.Sequential) { (index, group) =>
        visited.add(index.raw -> group)
        Right(())
      },
      Right(())
    )
    assertEquals(visited.asScala.toList.map(_._1), List(0L, 1L, 2L))
    assertEquals(visited.asScala.toList.map(_._2.x.raw), List(0, 1, 2))
    val failure =
      Dispatch.forEachGroup[NonEmpty[String]](launch, Workgroups.Sequential, Fault.Backend(_), _.message) {
        (index, _) => if (index.raw == 1L) Left("second group") else Right(())
      }
    assertEquals(failure.left.map(_.raw), Left("second group"))
  }

  test("atomic operations wrap, compare and ignore NaN by their definition") {
    import AtomicOperation.*
    assertEquals(Add(0xffffffffL, 1L, 32), 0L)
    assertEquals(Subtract(0L, 1L, 32), 0xffffffffL)
    assertEquals(MinimumSigned(0xffffffffL, 1L, 32), 0xffffffffL)
    assertEquals(MinimumUnsigned(0xffffffffL, 1L, 32), 1L)
    assertEquals(MaximumUnsigned(-1L, 1L, 64), -1L)
    assertEquals(MaximumSigned(-1L, 1L, 64), 1L)
    assertEquals(Exchange(3L, 0x1_0000_0005L, 32), 5L)
    assertEquals(Increment(0xffffffffL, 0L, 32), 0L)
    assertEquals(Decrement(0L, 0L, 32), 0xffffffffL)
    assertEquals(IncrementWrap(4L, 4L, 32), 0L)
    assertEquals(IncrementWrap(3L, 4L, 32), 4L)
    assertEquals(IncrementWrap(5L, 4L, 32), 0L)
    assertEquals(DecrementWrap(0L, 4L, 32), 4L)
    assertEquals(DecrementWrap(5L, 4L, 32), 4L)
    assertEquals(DecrementWrap(3L, 4L, 32), 2L)
    assertEquals(IncrementWrap(-2L, -1L, 64), -1L)
    assertEquals(DecrementWrap(-1L, -2L, 64), -2L)
    assertEquals(FloatAdd(0x3f800000L, 0x40000000L, 32), 0x40400000L)
    assertEquals(FloatMinimum(0x7fc00000L, 0x3f800000L, 32), 0x3f800000L)
    assertEquals(FloatMaximum(0x0L, 0x8000000000000000L, 64), 0x0L)
    assertEquals(FloatAdd(0x3c00L, 0x3c00L, 16), 0x4000L)
  }

  test("kernel arguments resolve memory addresses and apply argument pointer relocations") {
    val base = Map(AllocationId(1L) -> 0x1000L, AllocationId(2L) -> 0x8000L)
    def address(region: AllocationId, offset: Long >= 0): Either[Fault, GuestAddress] =
      base.get(region).map(start => GuestAddress(start + offset.raw)).toRight(Fault.Memory("unknown allocation"))
    val task = KernelTask.validated(
      "kernel",
      LaunchGeometry.Dimensions.One,
      LaunchGeometry.Dimensions.One,
      Vec(
        KernelTask.Argument.Scalar(Vec.fill[Byte](12)(0x7f.toByte).unsafe),
        KernelTask.Argument.Memory(AllocationId(1L), 0x10L),
        KernelTask.Argument.Local(64L)
      ),
      pointerRelocations = Vec(
        KernelTask.PointerRelocation(KernelTask.RelocationSource.Argument(0), 4L, AllocationId(2L), 8L),
        KernelTask.PointerRelocation(KernelTask.RelocationSource.Memory(AllocationId(1L)), 0L, AllocationId(2L), 0L)
      )
    ).fold(message => fail(message), identity)
    val resolved = KernelArguments.resolve(task, address(_, _)).fold(fault => fail(fault.message), identity)
    assertEquals(
      resolved.map {
        case KernelArguments.Resolved.Bytes(bytes) => bytes.raw.toList.map(_ & 0xff)
        case KernelArguments.Resolved.Local(size)  => List(size.raw.toInt)
      },
      Vec(
        List(0x7f, 0x7f, 0x7f, 0x7f, 0x08, 0x80, 0, 0, 0, 0, 0, 0),
        List(0x10, 0x10, 0, 0, 0, 0, 0, 0),
        List(64)
      )
    )
    val overflowing = task.withPointerRelocations(Vec(
      KernelTask.PointerRelocation(KernelTask.RelocationSource.Argument(0), 5L, AllocationId(2L), 0L)
    )).fold(message => fail(message), identity)
    assert(KernelArguments.resolve(overflowing, address(_, _)).isLeft)
    val empty = task.withArguments(Vec(
      KernelTask.Argument.Descriptor,
      KernelTask.Argument.Memory(AllocationId(1L), 0L),
      KernelTask.Argument.Local(64L)
    )).fold(message => fail(message), identity)
    assert(KernelArguments.resolve(empty, address(_, _)).isLeft)
  }

  test("local coordinates wrap x, then y, then z across a multidimensional group") {
    val launch = LaunchGeometry.forLaunch(LaunchGeometry.Dimensions(64, 16, 2), LaunchGeometry.Dimensions.One)
      .fold(message => fail(message), identity)
    assertEquals(launch.localCoordinates(0), LaunchGeometry.Coordinates(0, 0, 0))
    assertEquals(launch.localCoordinates(63), LaunchGeometry.Coordinates(63, 0, 0))
    assertEquals(launch.localCoordinates(64), LaunchGeometry.Coordinates(0, 1, 0))
    assertEquals(launch.localCoordinates(1023), LaunchGeometry.Coordinates(63, 15, 0))
    assertEquals(launch.localCoordinates(1024), LaunchGeometry.Coordinates(0, 0, 1))
  }
}
