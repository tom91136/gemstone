package net.kurobako.gemstone.rpc

import net.kurobako.gemstone.Constraint.NonEmptyString

import net.kurobako.gemstone.Constraint.{NonNegative, Positive}
import net.kurobako.gemstone.Vec
import net.kurobako.gemstone.memory.GuestAddress
import net.kurobako.gemstone.isa.{GlobalSymbol, KernelMetadata as ISAKernelMetadata}
import net.kurobako.gemstone.rpc.ExecutionProtocol.{*, given}
import net.kurobako.gemstone.rpc.Wire.Codec
import net.kurobako.gemstone.vm.LaunchGeometry

final class ExecutionProtocolSuite extends munit.FunSuite {
  private case class UncheckedKernelMetadata(
      argumentBlockSize: NonNegative[Int],
      explicitParameterCount: NonNegative[Int],
      parameters: Vec[ISAKernelMetadata.Parameter],
      requiredWorkGroupSize: Option[LaunchGeometry.Dimensions] = None,
      workGroupSizeHint: Option[LaunchGeometry.Dimensions] = None,
      bindingIndices: Vec[NonNegative[Int]] = Vec.empty,
      entryPointId: Option[NonNegative[Int]] = None
  ) derives Codec

  private case class UncheckedGlobalSymbol(
      name: NonEmptyString,
      virtualAddress: Option[GuestAddress],
      size: NonNegative[Long],
      pointerOffsets: Vec[NonNegative[Int]]
  ) derives Codec

  private case class UncheckedProgramGlobal(size: NonNegative[Long], initial: Vec[Byte]) derives Codec

  test("identities and symbol names reject wire sentinels") {
    assert(AllocationId.either(0L).isLeft)
    assert(ProgramId.either(-1L).isLeft)
    assertEquals(CompletionId.either(1L), Right(CompletionId(1L)))
    assert(NonEmptyString.either("").isLeft)
    assert(NonEmptyString.either("bad\u0000name").isLeft)
    assertEquals(NonEmptyString.either("global"), Right(NonEmptyString("global")))
  }

  test("kernel metadata enforces its parameter layout at the wire boundary") {
    val scalar = ISAKernelMetadata.Parameter(
      4,
      0,
      ISAKernelMetadata.ParameterKind.Scalar,
      containsPointers = false
    )
    val pointer = ISAKernelMetadata.Parameter(
      8,
      4,
      ISAKernelMetadata.ParameterKind.Allocation,
      containsPointers = true
    )
    assertEquals(
      ISAKernelMetadata.validated(
        12,
        2,
        Vec(scalar, pointer)
      ).map(_.parameters),
      Right(Vec(scalar, pointer))
    )
    assert(ISAKernelMetadata.validated(12, 3, Vec(scalar, pointer)).isLeft)
    assert(ISAKernelMetadata.validated(
      12,
      2,
      Vec(scalar, pointer.copy(offset = 3))
    ).isLeft)
    assert(ISAKernelMetadata.validated(8, 2, Vec(scalar, pointer)).isLeft)

    val invalid = UncheckedKernelMetadata(12, 3, Vec(scalar, pointer))
    val decoded = Codec[UncheckedKernelMetadata].encode(invalid, ExecutionRPC.Limits)
      .flatMap(bytes => Codec[ISAKernelMetadata].decode(bytes, ExecutionRPC.Limits))
    assert(decoded.isLeft)
  }

  test("program-global metadata validates pointer slots at the wire boundary") {
    assert(
      GlobalSymbol
        .validated(
          NonEmptyString("state"),
          Some(GuestAddress(0x1000L)),
          24L,
          Vec(0, 16)
        )
        .isRight
    )
    assert(GlobalSymbol.validated(
      NonEmptyString("state"),
      None,
      24L,
      Vec(8, 0)
    ).isLeft)
    assert(GlobalSymbol.validated(
      NonEmptyString("state"),
      None,
      24L,
      Vec(8, 8)
    ).isLeft)
    assert(GlobalSymbol.validated(
      NonEmptyString("state"),
      None,
      16L,
      Vec(9)
    ).isLeft)

    val invalid = UncheckedGlobalSymbol(
      NonEmptyString("state"),
      None,
      16L,
      Vec(9)
    )
    val decoded = Codec[UncheckedGlobalSymbol].encode(invalid, ExecutionRPC.Limits)
      .flatMap(bytes => Codec[GlobalSymbol].decode(bytes, ExecutionRPC.Limits))
    assert(decoded.isLeft)
  }

  test("program globals reject initial images larger than their extent") {
    val global =
      ProgramGlobal.validated(2L, Vec[Byte](1, 2)).fold(message => fail(message), identity)
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
