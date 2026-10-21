package net.kurobako.gemstone.rpc

import net.kurobako.gemstone.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.graphics.GraphicsProtocol.*
import net.kurobako.gemstone.graphics.ShaderMetadata.*
import net.kurobako.gemstone.isa.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.rpc.ExecutionRPC.{*, given}
import net.kurobako.gemstone.rpc.Wire.*
import net.kurobako.gemstone.vm.*

class ExecutionRPCSuite extends munit.FunSuite {
  private val allocation = AllocationId(1L)
  private val region     = AllocationId(1L)
  private val program    = ProgramId(2L)
  private val completion = CompletionId(3L)

  test("graphics closed domains reject invalid wire values and preserve descriptor alternatives") {
    def decodeInt[A: Wire.Codec](value: Int): Either[String, A] =
      Wire.Codec[Int].encode(value, ExecutionRPC.Limits).flatMap(Wire.Codec[A].decode(_, ExecutionRPC.Limits))
    assert(decodeInt[GraphicsFormat](-1).isLeft)
    assert(decodeInt[GraphicsFormat](GraphicsFormat.values.length).isLeft)
    GraphicsFormat.values.foreach(format => assertEquals(decodeInt[GraphicsFormat](format.ordinal), Right(format)))
    assert(decodeInt[GraphicsFilter](2).isLeft)
    assert(decodeInt[GraphicsSwizzle](7).isLeft)
    assert(decodeInt[GraphicsAddressMode](-1).isLeft)
    assert(decodeInt[Int in (1, 4)](2).isLeft)
    assert(decodeInt[Int in (2, 4)](1).isLeft)
    assert(decodeInt[Int in 0 ~ 3](4).isLeft)
    assertEquals(decodeInt[GraphicsFilter](1), Right(GraphicsFilter.Linear))
    val sampler = GraphicsSampler(
      minFilter = GraphicsFilter.Linear,
      magFilter = GraphicsFilter.Nearest,
      addressU = GraphicsAddressMode.Repeat,
      addressV = GraphicsAddressMode.ClampToBorder,
      addressW = GraphicsAddressMode.MirrorClampToEdge,
      unnormalized = false,
      compareEnable = true,
      compareOp = GraphicsCompare.GreaterOrEqual,
      borderColour = GraphicsBorderColour.FloatOpaqueWhite,
      mipLodBias = -0.75f,
      minLod = 0f,
      maxLod = 4f,
      mipmapMode = GraphicsMipmapMode.Linear
    )
    val image = GraphicsImage(
      GraphicsRange(allocation, 0L, 16L),
      Vec.nonEmpty(GraphicsImageLevel(GraphicsExtent(1, 1, 1), 0L, 16L, 16L, 16L)),
      1,
      1,
      GraphicsFormat.Rgba32f,
      false
    )
    val components  = GraphicsComponents(GraphicsSwizzle.B, GraphicsSwizzle.G, GraphicsSwizzle.R, GraphicsSwizzle.One)
    val descriptors = Vec(
      GraphicsDescriptor.Image(image, components),
      GraphicsDescriptor.Sampler(sampler),
      GraphicsDescriptor.Combined(image, components, sampler)
    )
    val codec    = Wire.Codec[Vec[DescriptorBinding]]
    val bindings = descriptors.zipWithIndex.map((descriptor, index) => DescriptorBinding(index.unsafe, descriptor))
    assertEquals(
      codec.encode(bindings, ExecutionRPC.Limits).flatMap(codec.decode(_, ExecutionRPC.Limits)),
      Right(bindings)
    )
    assert(scala.compiletime.testing.typeCheckErrors("""
      import net.kurobako.gemstone.graphics.GraphicsProtocol.*
      val filter: GraphicsFilter = GraphicsAddressMode.Repeat
    """).nonEmpty)
    assert(scala.compiletime.testing.typeCheckErrors("""
      import net.kurobako.gemstone.graphics.GraphicsProtocol.*
      val format: GraphicsFormat = GraphicsShaderFormat.R32f
    """).nonEmpty)
    assert(scala.compiletime.testing.typeCheckErrors("""
      import net.kurobako.gemstone.constraint.*
      val samples: Int in (1, 4) = 2
    """).nonEmpty)
  }

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
    Operation.SupplyHostSync(completion, ticket = 71, observed = -1L),
    Operation.RegisterAllocation(64L),
    Operation.RegisterAllocationAddress(allocation, GuestAddress(0x1000L)),
    Operation.RegisterAllocationAddress(allocation, GuestAddress(0x1000L), hostBacked = true),
    Operation.ReleaseAllocation(allocation),
    Operation.WriteAllocation(allocation, 4L, Vec[Byte](1, 2, 3)),
    Operation.ReadAllocation(allocation, 4L, 3),
    Operation.LoadProgram(ProgramEncoding.SPIRVOpenCLCompute, Vec[Byte](3, 2, 35, 7)),
    Operation.DescribeSpecialization(Vec[Byte](3, 2, 35, 7)),
    Operation.SpecializeImage(
      Vec[Byte](3, 2, 35, 7),
      Vec(SpecializationConstant(Binary32(0xffffffffL), Vec[Byte](1, 2, 3, 4)))
    ),
    Operation.PrepareShader(
      Vec[Byte](3, 2, 35, 7),
      "main",
      ShaderStageKind.Fragment,
      Vec(SpecializationConstant(Binary32(101L), Vec[Byte](1, 2, 3, 4)))
    ),
    Operation.SubmitGraphics(GraphicsSubmission(Vec.empty)),
    Operation.ReadGraphicsStatistics(completion),
    Operation.UnloadProgram(program),
    Operation.DescribeProgram(program),
    Operation.DescribeKernel(program, "kernel"),
    Operation.DescribeImage(ProgramEncoding.SPIRVOpenCLCompute, Vec[Byte](3, 2, 35, 7), "kernel"),
    Operation.DescribeGlobal(program, "global"),
    Operation.BindProgramGlobal(program, "global", allocation, 8L),
    Operation.WriteProgramGlobal(program, "global", 8L, Vec[Byte](4, 5)),
    Operation.BuildKernel(program, "kernel"),
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
        KernelTask.Argument.Scalar(Vec[Byte](9).unsafe)
      ),
      pointerRelocations = Vec(KernelTask.PointerRelocation(
        source = KernelTask.RelocationSource.Memory(region),
        sourceOffset = 0L,
        target = region,
        targetOffset = 8L
      ))
    ),
    Operation.QueryCompletion(completion),
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

  test("physical kernel register counts survive metadata transport") {
    val metadata = KernelMetadata.validated(0, 0, Vec.empty, registerCount = Some(26)).toOption.get
    assertEquals(
      Wire.Codec[KernelMetadata].encode(metadata, ExecutionRPC.Limits)
        .flatMap(Wire.Codec[KernelMetadata].decode(_, ExecutionRPC.Limits)),
      Right(metadata)
    )
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
    reply(Operation.RegisterAllocation(8L), AllocationId(128L), 0xd1, 0, 0x80)
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
  }

  test("operation decoding rejects unknown tags and trailing payload") {
    val (id, payload) = Wire.Protocol[Operation].encodePayload(Operation.Ping, ExecutionRPC.Limits)
      .fold(message => fail(message), value => value)
    assert(Wire.Protocol[Operation].decodePayload(id + "Unknown", payload, ExecutionRPC.Limits).isLeft)
    assert(Wire.Protocol[Operation].decodePayload(id, payload :+ 0.toByte, ExecutionRPC.Limits).isLeft)
  }

  test("encoding enforces total, text, and collection limits before producing bytes") {
    val limits = msgpack.Limits(
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
    val values = Vec.fill(ExecutionRPC.MaximumElements)(1)
    assertEquals(
      Wire.Codec[Vec[Int]].encode(values, ExecutionRPC.Limits)
        .flatMap(Wire.Codec[Vec[Int]].decode(_, ExecutionRPC.Limits)),
      Right(values)
    )
  }

  private case class UncheckedHostAtomic(
      address: GuestAddress,
      size: Int,
      expected: Long,
      desired: Long,
      operation: HostMemoryAtomicOperation
  ) derives Codec

  private case class UncheckedHostBytes(address: GuestAddress, bytes: Vec[Byte], atomicWidth: Int) derives Codec

  test("host atomic widths are validated at the wire boundary while zero denotes ordinary bytes") {
    def atomic(width: Int): Either[String, HostMemoryAtomic] =
      Codec[UncheckedHostAtomic].encode(
        UncheckedHostAtomic(GuestAddress(4096L), width, expected = 0L, desired = 1L, HostMemoryAtomicOperation.Store),
        ExecutionRPC.Limits
      ).flatMap(Codec[HostMemoryAtomic].decode(_, ExecutionRPC.Limits))
    def bytes(width: Int): Either[String, HostMemoryBytes] =
      Codec[UncheckedHostBytes].encode(
        UncheckedHostBytes(GuestAddress(4096L), Vec[Byte](1), width),
        ExecutionRPC.Limits
      ).flatMap(Codec[HostMemoryBytes].decode(_, ExecutionRPC.Limits))
    Vec(1, 2, 4, 8).foreach { width =>
      assertEquals(atomic(width).map(_.size.raw), Right(width))
      assertEquals(bytes(width).map(_.atomicWidth.raw), Right(width))
    }
    assert(atomic(0).isLeft)
    assertEquals(bytes(0).map(_.atomicWidth.raw), Right(0))
    Vec(-1, 3, 16).foreach { width =>
      assert(atomic(width).isLeft)
      assert(bytes(width).isLeft)
    }
  }

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
      alias: Option[ProgramGlobalAlias] = None
  ) derives Codec

  test("native symbol names reject empty and NUL-containing wire payloads") {
    def symbol(value: String): Either[String, NonEmpty[String]] =
      Codec[String].encode(value, ExecutionRPC.Limits).flatMap(Codec[NonEmpty[String]].decode(_, ExecutionRPC.Limits))
    assert(symbol("").isLeft)
    assert(symbol("bad\u0000name").isLeft)
    assertEquals(symbol("global"), Right[Nothing, NonEmpty[String]]("global"))
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
    val alias  = Some(ProgramGlobalAlias("bank", 8L))
    val global = ProgramGlobal.validated(4L, Vec.empty, alias).toOption.get
    assertEquals(
      Codec[ProgramGlobal].encode(global, ExecutionRPC.Limits)
        .flatMap(bytes => Codec[ProgramGlobal].decode(bytes, ExecutionRPC.Limits)),
      Right(global)
    )
    assert(ProgramGlobal.validated(4L, Vec[Byte](1), alias).isLeft)
    val invalid = UncheckedProgramGlobal(4L, Vec[Byte](1), alias)
    assert(Codec[UncheckedProgramGlobal].encode(invalid, ExecutionRPC.Limits)
      .flatMap(bytes => Codec[ProgramGlobal].decode(bytes, ExecutionRPC.Limits)).isLeft)
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

  test("nested failure details fit the wire instead of failing to encode") {
    val long    = "\u00e9" * ExecutionRPC.MaximumTextBytes
    val status  = CompletionStatus.Failed(ExecutionFailure.Failed(s"fault\u0000at $long"))
    val decoded = Codec[CompletionStatus].encode(status, ExecutionRPC.Limits)
      .flatMap(bytes => Codec[CompletionStatus].decode(bytes, ExecutionRPC.Limits))
    decoded match {
      case Right(CompletionStatus.Failed(ExecutionFailure.Failed(detail))) =>
        assert(detail.raw.startsWith("fault?at \u00e9"))
        assert(detail.raw.getBytes(java.nio.charset.StandardCharsets.UTF_8).length <= ExecutionRPC.MaximumTextBytes)
      case other => fail(s"unexpected decode: $other")
    }
  }

  test("every failure kind keeps its kind when its detail is fitted to the wire") {
    val long     = ("x" * (ExecutionRPC.MaximumTextBytes.raw + 1)).refine[NonEmpty[String]].toOption.get
    val failures = Vec[NonEmpty[String] => ExecutionFailure](
      ExecutionFailure.InvalidRequest(_),
      ExecutionFailure.NotFound(_),
      ExecutionFailure.Unsupported(_),
      ExecutionFailure.Failed(_),
      ExecutionFailure.InvalidProgram(_),
      ExecutionFailure.InvalidConfiguration(_),
      ExecutionFailure.OutOfResources(_),
      ExecutionFailure.IllegalAddress(_),
      ExecutionFailure.MisalignedAddress(_),
      ExecutionFailure.IllegalInstruction(_),
      ExecutionFailure.Trapped(_),
      ExecutionFailure.AssertionFailed(_),
      ExecutionFailure.TimedOut(_)
    )
    failures.foreach { make =>
      val decoded = Codec[ExecutionFailure].encode(make(long), ExecutionRPC.Limits)
        .flatMap(bytes => Codec[ExecutionFailure].decode(bytes, ExecutionRPC.Limits))
      assertEquals(decoded.map(_.productPrefix), Right(make("x").productPrefix))
      assertEquals(decoded.map(_.message.raw.length), Right(ExecutionRPC.MaximumTextBytes.raw))
    }
  }
}
