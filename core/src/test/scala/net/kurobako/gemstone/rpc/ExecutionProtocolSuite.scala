package net.kurobako.gemstone.rpc

import net.kurobako.gemstone.{NonNegativeInt, NonNegativeLong, PositiveInt, Vec}
import net.kurobako.gemstone.memory.GuestAddress
import net.kurobako.gemstone.rpc.ExecutionProtocol.*
import net.kurobako.gemstone.rpc.Wire.Codec

final class ExecutionProtocolSuite extends munit.FunSuite {
  private final case class UncheckedKernelMetadata(
      argumentBlockSize: NonNegativeInt,
      explicitParameterCount: NonNegativeInt,
      parameters: Vec[KernelParameterMetadata],
      requiredWorkGroupSize: Option[Dimensions] = None,
      workGroupSizeHint: Option[Dimensions] = None,
      bindingIndices: Vec[NonNegativeInt] = Vec.empty,
      entryPointId: Option[NonNegativeInt] = None
  ) derives Codec

  private final case class UncheckedProgramGlobalMetadata(
      name: GlobalName,
      virtualAddress: Option[GuestAddress],
      size: NonNegativeLong,
      pointerOffsets: Vec[NonNegativeInt]
  ) derives Codec

  private final case class UncheckedProgramGlobal(size: NonNegativeLong, initial: Vec[Byte]) derives Codec

  test("identities and symbol names reject wire sentinels") {
    assert(AllocationId.either(0L).isLeft)
    assert(ProgramId.either(-1L).isLeft)
    assertEquals(CompletionId.either(1L), Right(CompletionId(1L)))
    assert(KernelName.either("").isLeft)
    assert(KernelName.either("bad\u0000name").isLeft)
    assertEquals(GlobalName.either("global"), Right(GlobalName("global")))
  }

  test("kernel metadata enforces its parameter layout at the wire boundary") {
    val scalar = KernelParameterMetadata(
      PositiveInt(4),
      NonNegativeInt.Zero,
      KernelParameterKind.Scalar,
      containsPointers = false
    )
    val pointer = KernelParameterMetadata(
      PositiveInt(8),
      NonNegativeInt(4),
      KernelParameterKind.Allocation,
      containsPointers = true
    )
    assertEquals(
      KernelMetadata.validated(
        NonNegativeInt(12),
        NonNegativeInt(2),
        Vec(scalar, pointer)
      ).map(_.parameters),
      Right(Vec(scalar, pointer))
    )
    assert(KernelMetadata.validated(NonNegativeInt(12), NonNegativeInt(3), Vec(scalar, pointer)).isLeft)
    assert(KernelMetadata.validated(
      NonNegativeInt(12),
      NonNegativeInt(2),
      Vec(scalar, pointer.copy(offset = NonNegativeInt(3)))
    ).isLeft)
    assert(KernelMetadata.validated(NonNegativeInt(8), NonNegativeInt(2), Vec(scalar, pointer)).isLeft)

    val invalid = UncheckedKernelMetadata(NonNegativeInt(12), NonNegativeInt(3), Vec(scalar, pointer))
    val decoded = Codec[UncheckedKernelMetadata].encode(invalid, ExecutionRPC.Limits)
      .flatMap(bytes => Codec[KernelMetadata].decode(bytes, ExecutionRPC.Limits))
    assert(decoded.isLeft)
  }

  test("program-global metadata validates pointer slots at the wire boundary") {
    assert(
      ProgramGlobalMetadata
        .validated(
          GlobalName("state"),
          Some(GuestAddress(0x1000L)),
          NonNegativeLong(24L),
          Vec(NonNegativeInt.Zero, NonNegativeInt(16))
        )
        .isRight
    )
    assert(ProgramGlobalMetadata.validated(
      GlobalName("state"),
      None,
      NonNegativeLong(24L),
      Vec(NonNegativeInt(8), NonNegativeInt.Zero)
    ).isLeft)
    assert(ProgramGlobalMetadata.validated(
      GlobalName("state"),
      None,
      NonNegativeLong(24L),
      Vec(NonNegativeInt(8), NonNegativeInt(8))
    ).isLeft)
    assert(ProgramGlobalMetadata.validated(
      GlobalName("state"),
      None,
      NonNegativeLong(16L),
      Vec(NonNegativeInt(9))
    ).isLeft)

    val invalid = UncheckedProgramGlobalMetadata(
      GlobalName("state"),
      None,
      NonNegativeLong(16L),
      Vec(NonNegativeInt(9))
    )
    val decoded = Codec[UncheckedProgramGlobalMetadata].encode(invalid, ExecutionRPC.Limits)
      .flatMap(bytes => Codec[ProgramGlobalMetadata].decode(bytes, ExecutionRPC.Limits))
    assert(decoded.isLeft)
  }

  test("program globals reject initial images larger than their extent") {
    val global =
      ProgramGlobal.validated(NonNegativeLong(2L), Vec[Byte](1, 2)).fold(message => fail(message), identity)
    assertEquals(
      Codec[ProgramGlobal].encode(global, ExecutionRPC.Limits)
        .flatMap(bytes => Codec[ProgramGlobal].decode(bytes, ExecutionRPC.Limits)),
      Right(global)
    )
    assert(ProgramGlobal.validated(NonNegativeLong(1L), Vec[Byte](1, 2)).isLeft)

    val invalid = UncheckedProgramGlobal(NonNegativeLong(1L), Vec[Byte](1, 2))
    val decoded = Codec[UncheckedProgramGlobal].encode(invalid, ExecutionRPC.Limits)
      .flatMap(bytes => Codec[ProgramGlobal].decode(bytes, ExecutionRPC.Limits))
    assert(decoded.isLeft)
  }
}
