package net.kurobako.gemstone.rpc

import net.kurobako.gemstone.Vec
import net.kurobako.gemstone.memory.{AllocationKind, GuestAddress}
import net.kurobako.gemstone.rpc.ExecutionRPC.Operation
import net.kurobako.gemstone.rpc.ExecutionProtocol.*

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

final class ExecutionRPCSuite extends munit.FunSuite {
  private val allocation = AllocationId(1)
  private val program    = ProgramId(2)
  private val completion = CompletionId(3)

  private val operations: Vector[Operation[?]] = Vector(
    Operation.Ping,
    Operation.RegisterAllocation(AllocationKind.Shared, 64),
    Operation.RegisterAllocationAddress(allocation, GuestAddress(0x1000L)),
    Operation.ReleaseAllocation(allocation),
    Operation.WriteAllocation(allocation, 4, Vec[Byte](1, 2, 3)),
    Operation.ReadAllocation(allocation, 4, 3),
    Operation.LoadProgram("spirv-binary", Vec[Byte](3, 2, 35, 7)),
    Operation.UnloadProgram(program),
    Operation.DescribeProgram(program),
    Operation.DescribeKernel(program, KernelName("kernel")),
    Operation.DescribeGlobal(program, GlobalName("global")),
    Operation.BindProgramGlobal(program, GlobalName("global"), allocation, 8),
    Operation.WriteProgramGlobal(program, GlobalName("global"), 8, Vec[Byte](4, 5)),
    Operation.Launch(
      program,
      KernelName("kernel"),
      Dimensions.One,
      Dimensions.One,
      Dimensions.One.toGlobalDimensions,
      3,
      Coordinates(0, 0, 0),
      0,
      16,
      Vec(KernelArgument.Allocation(allocation, 0), KernelArgument.Scalar(Vec[Byte](9))),
      Vec(PointerRelocation(RelocationSource.Allocation(allocation), 0, allocation, 8))
    ),
    Operation.QueryCompletion(completion),
    Operation.AwaitCompletion(completion),
    Operation.CompletionDirtyRanges(completion),
    Operation.CompletionPointerRelocations(completion),
    Operation.CompletionPrints(completion),
    Operation.ReleaseCompletion(completion)
  )

  test("every operation round-trips through its independently addressable payload") {
    operations.foreach { operation =>
      val (id, payload) = Wire.Protocol[Operation].encodePayload(operation, ExecutionRPC.Limits)
        .fold(message => fail(message), value => value)
      val decoded = Wire.Protocol[Operation].decodePayload(id, payload, ExecutionRPC.Limits)
        .fold(message => fail(message), value => value)
      assertEquals(decoded.value, operation)
    }
  }

  test("operation IDs are stable hashes of fully-qualified message names") {
    val identifiers = ExecutionRPC.Schema.operations.map(_.request.id)
    assertEquals(identifiers.distinct.size, operations.size)
    ExecutionRPC.Schema.operations.foreach { operation =>
      assertEquals(
        operation.request.id,
        Wire.hash64(s"Operation.${operation.request.name}")
      )
    }
  }

  test("schema identity covers request and reply shapes") {
    assert(ExecutionRPC.Schema.canonical.contains("Launch"))
    assert(ExecutionRPC.Schema.canonical.contains("CompletionStatus"))
    assert(ExecutionRPC.Schema.canonical.contains("positive0"))
    assert(ExecutionRPC.Schema.canonical.contains("int-interval[1,3]"))
    assert(
      ExecutionRPC.Schema.canonical.contains("alias(AllocationId,i64)[positive]"),
      ExecutionRPC.Schema.canonical
    )
    assert(ExecutionRPC.Schema.canonical.contains("alias(KernelName,text)[minimum-length[1],not-contains[00]]"))
    assertEquals(
      ExecutionRPC.Identity,
      s"${ExecutionRPC.Name}@${java.lang.Long.toUnsignedString(ExecutionRPC.Schema.fingerprint, 16)}"
    )
  }

  test("operation decoding rejects unknown IDs and trailing payload") {
    val (id, payload) = Wire.Protocol[Operation].encodePayload(Operation.Ping, ExecutionRPC.Limits)
      .fold(message => fail(message), value => value)
    assert(Wire.Protocol[Operation].decodePayload(id ^ 1L, payload, ExecutionRPC.Limits).isLeft)
    assert(Wire.Protocol[Operation].decodePayload(id, payload :+ 0.toByte, ExecutionRPC.Limits).isLeft)
  }

  test("newtype codecs validate raw payloads at decode boundaries") {
    val invalidIdentifier = Wire.Codec[Long].encode(0L, ExecutionRPC.Limits).flatMap(
      Wire.Codec[AllocationId].decode(_, ExecutionRPC.Limits)
    )
    val invalidName = Wire.Codec[String].encode("bad\u0000name", ExecutionRPC.Limits).flatMap(
      Wire.Codec[KernelName].decode(_, ExecutionRPC.Limits)
    )
    assert(invalidIdentifier.isLeft)
    assert(invalidName.isLeft)
  }

  test("encoding enforces total, text, and collection limits before producing bytes") {
    val limits = Wire.Limits(maximumElements = 1, maximumBytes = 12, maximumTextBytes = 3)
    assert(Wire.Codec[String].encode("four", limits).isLeft)
    assert(Wire.Codec[Vec[Int]].encode(Vec(1, 2), limits).isLeft)
    assert(Wire.Codec[Vec[Byte]].encode(Vec.fill(12)(1.toByte), limits).isLeft)
    assertEquals(Wire.Codec[String].encode("ok", limits).flatMap(Wire.Codec[String].decode(_, limits)), Right("ok"))
  }

  test("maximum-sized element collections decode without consuming the call stack") {
    val values = Vec.fill(ExecutionRPC.MaximumElements)(1)
    assertEquals(
      Wire.Codec[Vec[Int]].encode(values, ExecutionRPC.Limits)
        .flatMap(Wire.Codec[Vec[Int]].decode(_, ExecutionRPC.Limits)),
      Right(values)
    )
  }
}
