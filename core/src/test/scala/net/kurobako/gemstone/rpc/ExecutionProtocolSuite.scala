package net.kurobako.gemstone.rpc

import net.kurobako.gemstone.Vec
import net.kurobako.gemstone.memory.GuestAddress
import net.kurobako.gemstone.rpc.ExecutionProtocol.*
import net.kurobako.gemstone.rpc.Wire.Codec

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

final class ExecutionProtocolSuite extends munit.FunSuite {
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

  test("identities and symbol names reject wire sentinels") {
    assert(AllocationId.either(0L).isLeft)
    assert(ProgramId.either(-1L).isLeft)
    assertEquals(CompletionId.either(1L), Right(CompletionId(1L)))
    assert(KernelName.either("").isLeft)
    assert(KernelName.either("bad\u0000name").isLeft)
    assertEquals(GlobalName.either("global"), Right(GlobalName("global")))
  }

  test("kernel metadata enforces its parameter layout at the wire boundary") {
    val scalar  = KernelParameterMetadata(4, 0, KernelParameterKind.Scalar, containsPointers = false)
    val pointer = KernelParameterMetadata(8, 4, KernelParameterKind.Allocation, containsPointers = true)
    assertEquals(
      KernelMetadata.validated(12, 2, Vec(scalar, pointer)).map(_.parameters),
      Right(Vec(scalar, pointer))
    )
    assert(KernelMetadata.validated(12, 3, Vec(scalar, pointer)).isLeft)
    assert(KernelMetadata.validated(12, 2, Vec(scalar, pointer.copy(offset = 3))).isLeft)
    assert(KernelMetadata.validated(8, 2, Vec(scalar, pointer)).isLeft)

    val invalid = UncheckedKernelMetadata(12, 3, Vec(scalar, pointer))
    val decoded = Codec[UncheckedKernelMetadata].encode(invalid, ExecutionRPC.Limits)
      .flatMap(bytes => Codec[KernelMetadata].decode(bytes, ExecutionRPC.Limits))
    assert(decoded.isLeft)
  }

  test("program-global metadata validates pointer slots at the wire boundary") {
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
    val global = ProgramGlobal.validated(2L, Vec[Byte](1, 2)).fold(message => fail(message), identity)
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
