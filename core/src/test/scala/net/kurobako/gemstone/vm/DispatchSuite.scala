package net.kurobako.gemstone.vm

import java.util.concurrent.ConcurrentLinkedQueue
import net.kurobako.gemstone.binary.Widths.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.semantics.*
import net.kurobako.gemstone.semantics.render.*
import net.kurobako.gemstone.testing.Suite
import scala.jdk.CollectionConverters.*

class DispatchSuite extends Suite {
  private class Operands(left: String, right: String) extends Sink.Binary[Term, Term] {
    inline def apply(inline kernel: (Term, Term) => Term): Term = kernel(Term.Variable(left), Term.Variable(right))
  }

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
        (index, _) => if (index == 1L) Left("second group") else Right(())
      }
    assertEquals(failure.left.map(_.raw), Left("second group"))
  }

  test("atomic operations wrap, compare and ignore NaN by their definition") {
    import AtomicOperation.*
    assertEquals(Add(old = 0xffffffffL, operand = 1L, 32), 0L)
    assertEquals(Subtract(old = 0L, operand = 1L, 32), 0xffffffffL)
    assertEquals(MinimumSigned(old = 0xffffffffL, operand = 1L, 32), 0xffffffffL)
    assertEquals(MinimumUnsigned(old = 0xffffffffL, operand = 1L, 32), 1L)
    assertEquals(MaximumUnsigned(old = -1L, operand = 1L, 64), -1L)
    assertEquals(MaximumSigned(old = -1L, operand = 1L, 64), 1L)
    assertEquals(Exchange(old = 3L, operand = 0x1_0000_0005L, 32), 5L)
    assertEquals(Increment(old = 0xffffffffL, operand = 0L, 32), 0L)
    assertEquals(Decrement(old = 0L, operand = 0L, 32), 0xffffffffL)
    assertEquals(IncrementWrap(old = 4L, operand = 4L, 32), 0L)
    assertEquals(IncrementWrap(old = 3L, operand = 4L, 32), 4L)
    assertEquals(IncrementWrap(old = 5L, operand = 4L, 32), 0L)
    assertEquals(DecrementWrap(old = 0L, operand = 4L, 32), 4L)
    assertEquals(DecrementWrap(old = 5L, operand = 4L, 32), 4L)
    assertEquals(DecrementWrap(old = 3L, operand = 4L, 32), 2L)
    assertEquals(IncrementWrap(old = -2L, operand = -1L, 64), -1L)
    assertEquals(DecrementWrap(old = -1L, operand = -2L, 64), -2L)
    assertEquals(FloatAdd(old = 0x3f800000L, operand = 0x40000000L, 32), 0x40400000L)
    assertEquals(FloatMinimum(old = 0x7fc00000L, operand = 0x3f800000L, 32), 0x3f800000L)
    assertEquals(FloatMaximum(old = 0x0L, operand = 0x8000000000000000L, 64), 0x0L)
    assertEquals(FloatAdd(old = 0x3c00L, operand = 0x3c00L, 16), 0x4000L)
    assertEquals(BFloat16Add(old = 0x3f80L, operand = 0x3f80L, 16), 0x4000L)
    assertEquals(BFloat16Add(old = 0x3f80L, operand = 0x3b80L, 16), 0x3f80L)
    assertEquals(PackedHalfAdd(old = 0x3c003c00L, operand = 0x3c004000L, 32), 0x40004200L)
    assertEquals(PackedHalfAdd(old = 0x7fdf7fdfL, operand = 0x7fff7fffL, 32), 0x7fdf7fdfL)
    assertEquals(PackedHalfAdd(old = 0x7fff7fffL, operand = 0x7fdf7fdfL, 32), 0x7fff7fffL)
    assertEquals(PackedHalfAdd(old = 0x3c007c01L, operand = 0xfc02bc00L, 32), 0xfe027e01L)
    assertEquals(PackedBFloat16Add(old = 0x3f80bf80L, operand = 0x3f803f80L, 32), 0x40000000L)
    // words of an mbarrier initialised for three arrivals, as an RTX 4060 Ti stores them
    assertEquals(BarrierArrive(old = 0x7ffffffd_3ffffffdL, operand = 1L, 64), 0x7ffffffe_3ffffffdL)
    assertEquals(BarrierArrive(old = 0x7fffffff_3ffffffdL, operand = 1L, 64), 0xfffffffd_3ffffffdL)
    assertEquals(BarrierArrive(old = 0xffffffff_3ffffffdL, operand = 1L, 64), 0x7ffffffd_3ffffffdL)
    assertEquals(BarrierArrive(old = 0x80000000_3ffffffeL, operand = 1L, 64), 0x80000001_3ffffffeL)
  }

  test("atomic operations render the value they store") {
    def rendered(operation: AtomicOperation, width: ScalarBitWidth): String =
      operation.staged(Render, width)(Operands("old", "operand")).show
    assertEquals(rendered(AtomicOperation.Exchange, 32), "zext32(operand)")
    assertEquals(rendered(AtomicOperation.MinimumUnsigned, 64), "min.u(old, operand)")
    assertEquals(
      rendered(AtomicOperation.DecrementWrap, 32),
      "a = zext32(old)\nb = zext32(operand)\n((a == 0) | (b <u a)) ? b : (a - 1)"
    )
    assertEquals(rendered(AtomicOperation.FloatMaximum, 16), "Float16.maxNum(zext16(old), zext16(operand))")
  }

  test("kernel arguments resolve memory addresses and apply argument pointer relocations") {
    val base = Map(AllocationId(1L) -> 0x1000L, AllocationId(2L) -> 0x8000L)
    def address(region: AllocationId, offset: Long >= 0): Either[Fault, GuestAddress] =
      base.get(region).map(start => GuestAddress(start + offset)).toRight(Fault.Memory("unknown allocation"))
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
        case KernelArguments.Resolved.Local(size)  => List(size.toInt)
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
