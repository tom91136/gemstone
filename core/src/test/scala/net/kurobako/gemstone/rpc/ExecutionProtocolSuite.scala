package net.kurobako.gemstone.rpc

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.rpc.ExecutionProtocol.{*, given}
import net.kurobako.gemstone.rpc.Wire.*
import net.kurobako.gemstone.vm.*

class ExecutionProtocolSuite extends munit.FunSuite {
  private case class UncheckedKernelMetadata(
      argumentBlockSize: Int >= 0,
      explicitParameterCount: Int >= 0,
      parameters: Vec[KernelMetadata.Parameter],
      requiredWorkGroupSize: Option[LaunchGeometry.Dimensions] = None,
      workGroupSizeHint: Option[LaunchGeometry.Dimensions] = None,
      bindingIndices: Vec[Int >= 0] = Vec.empty,
      entryPointId: Option[Int >= 0] = None,
      registerCount: Option[Int >= 0] = None
  ) derives Codec

  private case class UncheckedGlobalSymbol(
      name: NonEmpty[String],
      virtualAddress: Option[GuestAddress],
      size: Long >= 0,
      pointerOffsets: Vec[Int >= 0]
  ) derives Codec

  private case class UncheckedProgramGlobal(
      size: Long >= 0,
      initial: Vec[Byte],
      alias: Option[GlobalImage.Alias] = None
  ) derives Codec

  test("identities and symbol names reject wire sentinels") {
    assert(AllocationId.either(0L).isLeft)
    assert(ProgramId.either(-1L).isLeft)
    assertEquals(CompletionId.either(1L), Right(CompletionId(1L)))
    def symbol(value: String): Either[String, NonEmpty[String]] =
      Codec[String].encode(value, ExecutionRPC.Limits).flatMap(Codec[NonEmpty[String]].decode(_, ExecutionRPC.Limits))
    assert(symbol("").isLeft)
    assert(symbol("bad\u0000name").isLeft)
    assertEquals(
      symbol("global"),
      Right[Nothing, NonEmpty[String]]("global")
    )
  }

  test("NUL restrictions apply to native names at the RPC boundary, in both directions") {
    val text: NonEmpty[String] = "bad\u0000name"
    val generic                = Codec.nonEmpty[String]
    assertEquals(generic.encode(text, ExecutionRPC.Limits).flatMap(generic.decode(_, ExecutionRPC.Limits)), Right(text))
    assert(Codec[NonEmpty[String]].encode(text, ExecutionRPC.Limits).isLeft)
    assert(Codec[KernelSymbol]
      .encode(KernelSymbol(text, "valid"), ExecutionRPC.Limits).isLeft)
    assert(Wire.Protocol[ExecutionRPC.Operation]
      .encodePayload(ExecutionRPC.Operation.DescribeKernel(ProgramId(1L), text), ExecutionRPC.Limits).isLeft)
  }

  test("kernel metadata enforces its parameter layout at the wire boundary") {
    val scalar = KernelMetadata.Parameter(
      4,
      0,
      KernelMetadata.ParameterKind.Scalar,
      containsPointers = false
    )
    val pointer = KernelMetadata.Parameter(
      8,
      4,
      KernelMetadata.ParameterKind.Allocation,
      containsPointers = true
    )
    assertEquals(
      KernelMetadata.validated(
        12,
        2,
        Vec(scalar, pointer)
      ).map(_.parameters),
      Right(Vec(scalar, pointer))
    )
    assert(KernelMetadata.validated(12, 3, Vec(scalar, pointer)).isLeft)
    assert(KernelMetadata.validated(
      12,
      2,
      Vec(scalar, pointer.copy(offset = 3))
    ).isLeft)
    assert(KernelMetadata.validated(8, 2, Vec(scalar, pointer)).isLeft)

    val invalid = UncheckedKernelMetadata(12, 3, Vec(scalar, pointer))
    val decoded = Codec[UncheckedKernelMetadata].encode(invalid, ExecutionRPC.Limits)
      .flatMap(bytes => Codec[KernelMetadata].decode(bytes, ExecutionRPC.Limits))
    assert(decoded.isLeft)
  }

  test("program-global metadata validates pointer slots at the wire boundary") {
    assert(
      GlobalSymbol
        .validated(
          "state",
          Some(GuestAddress(0x1000L)),
          24L,
          Vec(0, 16)
        )
        .isRight
    )
    assert(GlobalSymbol.validated(
      "state",
      None,
      24L,
      Vec(8, 0)
    ).isLeft)
    assert(GlobalSymbol.validated(
      "state",
      None,
      24L,
      Vec(8, 8)
    ).isLeft)
    assert(GlobalSymbol.validated(
      "state",
      None,
      16L,
      Vec(9)
    ).isLeft)

    val invalid = UncheckedGlobalSymbol(
      "state",
      None,
      16L,
      Vec(9)
    )
    val decoded = Codec[UncheckedGlobalSymbol].encode(invalid, ExecutionRPC.Limits)
      .flatMap(bytes => Codec[GlobalSymbol].decode(bytes, ExecutionRPC.Limits))
    assert(decoded.isLeft)
  }

  test("program global aliases round-trip without independent initial contents") {
    val alias  = Some(GlobalImage.Alias("bank", 8L))
    val global = GlobalImage.validated(4L, Vec.empty, alias).toOption.get
    assertEquals(
      Codec[GlobalImage].encode(global, ExecutionRPC.Limits)
        .flatMap(bytes => Codec[GlobalImage].decode(bytes, ExecutionRPC.Limits)),
      Right(global)
    )
    assert(GlobalImage.validated(4L, Vec[Byte](1), alias).isLeft)
    val invalid = UncheckedProgramGlobal(4L, Vec[Byte](1), alias)
    assert(Codec[UncheckedProgramGlobal].encode(invalid, ExecutionRPC.Limits)
      .flatMap(bytes => Codec[GlobalImage].decode(bytes, ExecutionRPC.Limits)).isLeft)
  }

  test("program globals reject initial images larger than their extent") {
    val global =
      GlobalImage.validated(2L, Vec[Byte](1, 2)).fold(message => fail(message), identity)
    assertEquals(
      Codec[GlobalImage].encode(global, ExecutionRPC.Limits)
        .flatMap(bytes => Codec[GlobalImage].decode(bytes, ExecutionRPC.Limits)),
      Right(global)
    )
    assert(GlobalImage.validated(1L, Vec[Byte](1, 2)).isLeft)

    val invalid = UncheckedProgramGlobal(1L, Vec[Byte](1, 2))
    val decoded = Codec[UncheckedProgramGlobal].encode(invalid, ExecutionRPC.Limits)
      .flatMap(bytes => Codec[GlobalImage].decode(bytes, ExecutionRPC.Limits))
    assert(decoded.isLeft)
  }
}
