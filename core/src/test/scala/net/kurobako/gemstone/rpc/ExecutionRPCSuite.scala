package net.kurobako.gemstone.rpc

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.rpc.ExecutionProtocol.*
import net.kurobako.gemstone.rpc.ExecutionRPC.*
import net.kurobako.gemstone.vm.*

class ExecutionRPCSuite extends munit.FunSuite {
  private val allocation = AllocationId(1L)
  private val region     = AllocationId(1L)
  private val program    = ProgramId(2L)
  private val completion = CompletionId(3L)

  test("allocation identifiers reject zero and negative values at the wire boundary") {
    for (invalid <- Vector(0L, -1L, Long.MinValue)) {
      val decoded = Wire.Codec[Long].encode(invalid, ExecutionRPC.Limits)
        .flatMap(Wire.Codec[AllocationId].decode(_, ExecutionRPC.Limits))
      assert(decoded.isLeft)
    }
  }

  test("specialization IDs retain unsigned 32-bit boundaries on the wire") {
    val codec = Wire.Codec[SpecializationConstant]
    for (id <- Vec(Binary32(0L), Binary32(0x7fffffffL), Binary32(0x80000000L), Binary32(0xffffffffL))) {
      val constant = SpecializationConstant(id, Vec[Byte](42))
      assertEquals(
        codec.encode(constant, ExecutionRPC.Limits).flatMap(codec.decode(_, ExecutionRPC.Limits)),
        Right(constant)
      )
    }
    for (invalid <- Vec(-1L, 0x100000000L)) {
      val encoded = Wire.Codec.derived[(Long, Vec[Byte])].encode((invalid, Vec[Byte](42)), ExecutionRPC.Limits)
      assert(encoded.flatMap(codec.decode(_, ExecutionRPC.Limits)).isLeft)
    }
  }

  private val operations: Vector[Operation[?]] = Vector(
    Operation.Ping,
    Operation.EnableHostMemory,
    Operation.PollHostMemory(completion),
    Operation.SupplyHostMemory(completion, Vec(HostMemoryBytes(GuestAddress(0x1000L), Vec[Byte](1, 2)))),
    Operation.SupplyHostSync(completion, 71, -1L),
    Operation.RegisterAllocation(AllocationKind.Shared, 64L),
    Operation.RegisterAllocationAddress(allocation, GuestAddress(0x1000L)),
    Operation.RegisterAllocationAddress(allocation, GuestAddress(0x1000L), true),
    Operation.ReleaseAllocation(allocation),
    Operation.WriteAllocation(allocation, 4L, Vec[Byte](1, 2, 3)),
    Operation.ReadAllocation(allocation, 4L, 3),
    Operation.LoadProgram("spirv-binary", Vec[Byte](3, 2, 35, 7)),
    Operation.DescribeSpecialization(Vec[Byte](3, 2, 35, 7)),
    Operation.SpecializeImage(
      Vec[Byte](3, 2, 35, 7),
      Vec(SpecializationConstant(Binary32(0xffffffffL), Vec[Byte](1, 2, 3, 4)))
    ),
    Operation.UnloadProgram(program),
    Operation.DescribeProgram(program),
    Operation.DescribeKernel(program, "kernel"),
    Operation.DescribeImage("spirv-binary", Vec[Byte](3, 2, 35, 7), "kernel"),
    Operation.Blas(BlasRequest(
      operation = BlasOperation.Axpy,
      element = BlasElement.F32,
      count = 2,
      rows = 0,
      columns = 0,
      inner = 0,
      transposeA = false,
      transposeB = false,
      alpha = Vec[Byte](0, 0, 128.toByte, 63),
      beta = Vec.empty,
      x = None,
      y = None,
      a = None,
      b = None,
      c = None,
      strideA = 0L,
      strideB = 0L,
      strideC = 0L,
      batch = 0
    )),
    Operation.DescribeGlobal(program, "global"),
    Operation.BindProgramGlobal(program, "global", allocation, 8L),
    Operation.WriteProgramGlobal(program, "global", 8L, Vec[Byte](4, 5)),
    Operation.Launch(
      program = program,
      kernelName = "kernel",
      groupSize = LaunchGeometry.Dimensions.One,
      groupCount = LaunchGeometry.Dimensions.One,
      globalSize = LaunchGeometry.Dimensions.One.toGlobalDimensions,
      workDim = 3,
      globalOffset = LaunchGeometry.Coordinates(0, 0, 0),
      privateSegmentBytes = 0,
      groupSegmentBytes = 16,
      arguments = Vec(
        KernelTask.Argument.Memory(region, 0L),
        KernelTask.Argument.Scalar(Vec[Byte](9))
      ),
      pointerRelocations = Vec(KernelTask.PointerRelocation(
        source = KernelTask.RelocationSource.Memory(region),
        sourceOffset = 0L,
        target = region,
        targetOffset = 8L
      ))
    ),
    Operation.QueryCompletion(completion),
    Operation.AwaitCompletion(completion),
    Operation.CompletionDirtyRanges(completion, 0),
    Operation.CompletionPointerRelocations(completion, 0),
    Operation.CompletionPointerRelocations(completion, 16384),
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

  test("operations use their exact names as tags") {
    val tags = operations.map(operation =>
      Wire.Protocol[Operation].encodePayload(operation, ExecutionRPC.Limits).toOption.get._1
    )
    assertEquals(tags, operations.map(_.productPrefix))
    assertEquals(tags.distinct.size, ExecutionRPC.Schema.operations.size)
    assertEquals(ExecutionRPC.Schema.operations.map(_.request.name).toSet, tags.toSet)
  }

  test("request and reply payloads match fixed MessagePack bytes in both directions") {
    def request[A](operation: Operation[A], id: String, bytes: Int*): Unit = {
      val expected = bytes.map(_.toByte).toArray
      val encoded  = Wire.Protocol[Operation].encodePayload(operation, ExecutionRPC.Limits)
        .fold(message => fail(message), identity)
      assertEquals(encoded._1, id)
      assertEquals(encoded._2.toVector, expected.toVector)
      assertEquals(
        Wire.Protocol[Operation].decodePayload(id, expected, ExecutionRPC.Limits).map(_.value),
        Right(operation)
      )
    }
    def reply[A](operation: Operation[A], value: A, bytes: Int*): Unit = {
      val expected = bytes.map(_.toByte).toArray
      assertEquals(operation.replyCodec.encode(value, ExecutionRPC.Limits).map(_.toVector), Right(expected.toVector))
      assertEquals(operation.replyCodec.decode(expected, ExecutionRPC.Limits), Right(value))
    }
    request(Operation.Ping, "Ping", 0x90)
    request(Operation.ReadAllocation(allocation, 4L, 3), "ReadAllocation", 0x93, 1, 4, 3)
    request(
      Operation.WriteAllocation(allocation, 4L, Vec[Byte](1, 2, 3)),
      "WriteAllocation",
      0x93,
      1,
      4,
      0xc4,
      3,
      1,
      2,
      3
    )
    request(
      Operation.DescribeKernel(program, "λ"),
      "DescribeKernel",
      0x92,
      2,
      0xa2,
      0xce,
      0xbb
    )
    reply(Operation.Ping, (), 0x90)
    reply(Operation.RegisterAllocation(AllocationKind.Shared, 8L), AllocationId(128L), 0xd1, 0, 0x80)
    reply(Operation.ReadAllocation(allocation, 4L, 3), Vec[Byte](1, 2, 3), 0xc4, 3, 1, 2, 3)
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
    assert(ExecutionRPC.Schema.canonical.contains("alias(String,text)[minimum-length[1],not-contains[00]]"))
    assertEquals(
      ExecutionRPC.Identity,
      "gemstone.execution/3"
    )
  }

  test("operation decoding rejects unknown tags and trailing payload") {
    val (id, payload) = Wire.Protocol[Operation].encodePayload(Operation.Ping, ExecutionRPC.Limits)
      .fold(message => fail(message), value => value)
    assert(Wire.Protocol[Operation].decodePayload(id + "Unknown", payload, ExecutionRPC.Limits).isLeft)
    assert(Wire.Protocol[Operation].decodePayload(id, payload :+ 0.toByte, ExecutionRPC.Limits).isLeft)
  }

  test("refined string codecs reject empty wire payloads") {
    val invalidName = Wire.Codec[String].encode("", ExecutionRPC.Limits).flatMap(
      Wire.Codec[NonEmpty[String]].decode(_, ExecutionRPC.Limits)
    )
    assert(invalidName.isLeft)
  }

  test("encoding enforces total, text, and collection limits before producing bytes") {
    val limits = Wire.Limits(
      maximumElements = 1,
      maximumBytes = 12,
      maximumTextBytes = 3
    )
    assert(Wire.Codec[String].encode("four", limits).isLeft)
    assert(Wire.Codec[Vec[Int]].encode(Vec(1, 2), limits).isLeft)
    assert(Wire.Codec[Vec[Byte]].encode(Vec.fill(12)(1.toByte), limits).isLeft)
    assertEquals(Wire.Codec[String].encode("ok", limits).flatMap(Wire.Codec[String].decode(_, limits)), Right("ok"))
  }

  test("maximum-sized element collections decode without consuming the call stack") {
    val values = Vec.fill(ExecutionRPC.MaximumElements.raw)(1)
    assertEquals(
      Wire.Codec[Vec[Int]].encode(values, ExecutionRPC.Limits)
        .flatMap(Wire.Codec[Vec[Int]].decode(_, ExecutionRPC.Limits)),
      Right(values)
    )
  }
}
