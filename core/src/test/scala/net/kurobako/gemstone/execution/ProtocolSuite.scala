package net.kurobako.gemstone.execution

import net.kurobako.gemstone.Vec
import net.kurobako.gemstone.memory.GuestAddress
import net.kurobako.gemstone.rpc.ExecutionRPC
import net.kurobako.gemstone.rpc.Wire.Codec

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

final class ProtocolSuite extends munit.FunSuite {
  private final case class UncheckedKernelMetadata(
      argumentBlockSize: Int :| Positive0,
      explicitParameterCount: Int :| Positive0,
      parameters: Vec[KernelParameterMetadata]
  ) derives Codec

  private final case class UncheckedProgramGlobalMetadata(
      name: GlobalName,
      virtualAddress: Option[GuestAddress],
      size: Long :| Positive0,
      pointerOffsets: Vec[Int :| Positive0]
  ) derives Codec

  private final case class UncheckedProgramGlobal(size: Long :| Positive0, initial: Vec[Byte]) derives Codec

  test("identities reject the wire sentinel") {
    assert(AllocationId.either(0L).isLeft)
    assert(ProgramId.either(-1L).isLeft)
    assertEquals(CompletionId.either(1L), Right(CompletionId(1L)))
  }

  test("symbol names reject empty and NUL-containing values") {
    assert(KernelName.either("").isLeft)
    assert(KernelName.either("bad\u0000name").isLeft)
    assertEquals(GlobalName.either("global"), Right(GlobalName("global")))
  }

  test("runtime coordinates are refined at their boundary") {
    val coordinates = for {
      x <- 1.refineEither[Positive0]
      y <- 2.refineEither[Positive0]
      z <- 3.refineEither[Positive0]
    } yield Coordinates(x, y, z)
    assertEquals(coordinates, Right(Coordinates(1, 2, 3)))
    assert((-1).refineEither[Positive0].isLeft)
  }

  test("dispatch dimensions and kernel names are validated centrally") {
    val program = ProgramId(1)
    assert(0.refineEither[Positive].isLeft)
    val one = Dimensions.One
    assert(KernelName.either("").isLeft)
    assert(
      KernelDispatch.validated(program, KernelName("fill"), one, one, Vec(KernelArgument.Scalar(Vec(1.toByte)))).isRight
    )
  }

  test("launch geometry maps every linear group and local lane into three-dimensional coordinates") {
    val groupSize  = Dimensions(2, 3, 2)
    val groupCount = Dimensions(3, 2, 1)
    val geometry   = LaunchGeometry.forLaunch(groupSize, groupCount).fold(message => fail(message), value => value)

    assertEquals(geometry.localCoordinates(7), Right(Coordinates(1, 0, 1)))
    assertEquals(geometry.groupCoordinates(4), Right(Coordinates(1, 1, 0)))
    assertEquals(
      geometry.globalCoordinates(Coordinates(1, 1, 0), Coordinates(1, 2, 1)),
      Right(Coordinates(3, 5, 1))
    )
  }

  test("launch geometry carries the OpenCL work dimension and global offset") {
    val groupSize  = Dimensions(4, 1, 1)
    val groupCount = Dimensions(2, 1, 1)
    val geometry   = LaunchGeometry.forLaunch(
      groupSize,
      groupCount,
      Some(GlobalDimensions(7, 1, 1)),
      1,
      Coordinates(11, 0, 0)
    ).fold(message => fail(message), value => value)
    assertEquals(geometry.workDim, 1)
    assertEquals(geometry.globalOffset, Coordinates(11, 0, 0))
    assertEquals(
      geometry.globalCoordinates(Coordinates(1, 0, 0), Coordinates(0, 0, 0)),
      Right(Coordinates(15, 0, 0))
    )
    assert(geometry.containsGlobal(Coordinates(11, 0, 0)))
    assert(geometry.containsGlobal(Coordinates(17, 0, 0)))
    assert(!geometry.containsGlobal(Coordinates(10, 0, 0)))
    assert(!geometry.containsGlobal(Coordinates(18, 0, 0)))
    assert(0.refineEither[Interval.Closed[1, 3]].isLeft)
  }

  test("launch geometry reports the active size of partial work-groups") {
    val geometry = LaunchGeometry
      .forLaunch(
        groupSize = Dimensions(4, 3, 2),
        groupCount = Dimensions(3, 2, 2),
        globalSize = Some(GlobalDimensions(10, 5, 3))
      )
      .fold(message => fail(message), value => value)

    assertEquals(geometry.activeGroupSize(Coordinates(0, 0, 0)), Right(Dimensions(4, 3, 2)))
    assertEquals(geometry.activeGroupSize(Coordinates(2, 1, 1)), Right(Dimensions(2, 2, 1)))
    assertEquals(geometry.activeGroupSize(Coordinates(3, 0, 0)).isLeft, true)
    assertEquals(Dimensions(2, 2, 1).coordinates(3), Right(Coordinates(1, 1, 0)))
  }

  test("virtual launch geometry preserves padded extents beyond Int.MaxValue") {
    val groupSize  = Dimensions(256, 1, 1)
    val groupCount = Dimensions(16777216, 1, 1)
    val geometry   = LaunchGeometry.virtual(groupSize, groupCount).fold(message => fail(message), value => value)

    assertEquals(geometry.globalExtent, GlobalDimensions(4294967296L, 1L, 1L))
    assert(geometry.containsGlobal(Coordinates(Int.MaxValue, 0, 0)))
    val last = geometry.globalCoordinatesLong(Coordinates(16777215, 0, 0), Coordinates(255, 0, 0))
      .fold(message => fail(message), value => value)
    assertEquals(last, LongCoordinates(4294967295L, 0L, 0L))
    assert(geometry.containsGlobal(last))
    assert(!geometry.containsGlobal(LongCoordinates(4294967296L, 0L, 0L)))
  }

  test("pointer relocations cannot refer to an absent kernel argument") {
    val program    = ProgramId(1)
    val target     = AllocationId(2)
    val relocation = PointerRelocation(
      source = RelocationSource.Argument(1),
      sourceOffset = 0L,
      target = target,
      targetOffset = 0L
    )
    assert(
      KernelDispatch
        .validated(
          program,
          KernelName("kernel"),
          Dimensions.One,
          Dimensions.One,
          Vec(KernelArgument.Scalar(Vec.empty)),
          pointerRelocations = Vec(relocation)
        )
        .left
        .exists(_.contains("in-range source argument"))
    )
  }

  test("kernel metadata enforces its parameter layout") {
    val scalar  = KernelParameterMetadata(4, 0, KernelParameterKind.Scalar, containsPointers = false)
    val pointer = KernelParameterMetadata(8, 4, KernelParameterKind.Allocation, containsPointers = true)
    assertEquals(
      KernelMetadata.validated(12, 2, Vec(scalar, pointer)).map(_.parameters),
      Right(Vec(scalar, pointer))
    )
    assert(KernelMetadata.validated(12, 3, Vec(scalar, pointer)).isLeft)
    assert(
      KernelMetadata
        .validated(12, 2, Vec(scalar, pointer.copy(offset = 3)))
        .left
        .exists(_.contains("ordered and non-overlapping"))
    )
    assert(KernelMetadata.validated(8, 2, Vec(scalar, pointer)).isLeft)

    val invalid = UncheckedKernelMetadata(12, 3, Vec(scalar, pointer))
    val decoded = Codec[UncheckedKernelMetadata].encode(invalid, ExecutionRPC.Limits)
      .flatMap(bytes => Codec[KernelMetadata].decode(bytes, ExecutionRPC.Limits))
    assert(decoded.isLeft)
  }

  test("program-global metadata enforces ordered in-range 64-bit pointer slots") {
    assert(
      ProgramGlobalMetadata
        .validated(GlobalName("state"), Some(GuestAddress(0x1000L)), 24L, Vec(0, 16))
        .isRight
    )
    assert(ProgramGlobalMetadata.validated(GlobalName("state"), None, 24L, Vec(8, 0)).isLeft)
    assert(ProgramGlobalMetadata.validated(GlobalName("state"), None, 24L, Vec(8, 8)).isLeft)
    assert(ProgramGlobalMetadata.validated(GlobalName("state"), None, 16L, Vec(9)).isLeft)

    val invalid = UncheckedProgramGlobalMetadata(GlobalName("state"), None, 16L, Vec(9))
    val decoded = Codec[UncheckedProgramGlobalMetadata].encode(invalid, ExecutionRPC.Limits)
      .flatMap(bytes => Codec[ProgramGlobalMetadata].decode(bytes, ExecutionRPC.Limits))
    assert(decoded.isLeft)
  }

  test("program globals reject initial images larger than their extent") {
    val global = ProgramGlobal.validated(2L, Vec[Byte](1, 2)).fold(message => fail(message), value => value)
    assertEquals(
      Codec[ProgramGlobal].encode(global, ExecutionRPC.Limits)
        .flatMap(bytes => Codec[ProgramGlobal].decode(bytes, ExecutionRPC.Limits)),
      Right(global)
    )
    assert(ProgramGlobal.validated(1L, Vec[Byte](1, 2)).isLeft)

    val invalid = UncheckedProgramGlobal(1L, Vec[Byte](1, 2))
    val decoded = Codec[UncheckedProgramGlobal].encode(invalid, ExecutionRPC.Limits)
      .flatMap(bytes => Codec[ProgramGlobal].decode(bytes, ExecutionRPC.Limits))
    assert(decoded.isLeft)
  }
}
