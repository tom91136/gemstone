package net.kurobako.gemstone.server

import net.kurobako.gemstone.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.graphics.GraphicsProtocol.*
import net.kurobako.gemstone.isa.*
import net.kurobako.gemstone.isa.spirv.*
import net.kurobako.gemstone.isa.spirv.graphics.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.rpc.*
import net.kurobako.gemstone.rpc.ExecutionRPC.*
import net.kurobako.gemstone.vm.*
import scala.util.Using

class GraphicsSubmissionSuite extends munit.FunSuite {
  private val format                                          = GraphicsFormat.Rgba8
  private val area                                            = GraphicsRect(0, 0, 2, 1)
  private def right[A](value: Either[ExecutionFailure, A]): A = value.fold(error => fail(error.message), identity)

  test("ordered image commands preserve aliases, view bounds and completion ownership") {
    Using.resource(ExecutionSession(Nil, graphics = Some(GraphicsRuntime()))) { service =>
      val unrelated = right(service.registerAllocation(8))
      right(service.registerAllocationAddress(unrelated, GuestAddress(0x1000L)))
      val id = right(service.registerAllocation(32))
      right(service.write(id, 0L, Vec.fill(32)(0x55.toByte)))
      val image = GraphicsImage(
        memory = GraphicsRange(id, 8L, 8L),
        levels = Vec.nonEmpty(GraphicsImageLevel(GraphicsExtent(2, 1, 1), 0L, 8L, 8L, 8L)),
        layers = 1,
        samples = 1,
        format = format,
        volume = false
      )
      val layers = GraphicsLayers(0, 0, 1, true, false, false)
      val copy   = GraphicsBufferImageCopy(
        0L,
        0,
        0,
        layers,
        GraphicsOffset(0, 0, 0),
        image.extent
      )
      val completion = right(service.submitGraphics(GraphicsSubmission(Vec(
        GraphicsCommand.ClearColour(image, area, Vec(0x3f800000, 0, 0, 0x3f800000)),
        GraphicsCommand.CopyBufferImage(
          GraphicsRange(id, 16L, 8L),
          image,
          Vec(copy),
          false
        )
      ))))
      right(service.awaitCompletion(completion))
      right(service.releaseAllocation(unrelated))
      assertEquals(right(service.readGraphicsStatistics(completion)).occlusionSamples.raw, 0L)
      assert(service.releaseAllocation(id).isLeft)
      val expected = Vector.fill(8)(0x55.toByte) ++
        Vector.fill(4)(Vector(255.toByte, 0.toByte, 0.toByte, 255.toByte)).flatten ++ Vector.fill(8)(0x55.toByte)
      assertEquals(right(service.read(id, 0L, 32)).toVector, expected)
      right(service.releaseCompletion(completion))
      assert(service.readGraphicsStatistics(completion).isLeft)
      right(service.releaseAllocation(id))
    }
  }

  test("explicit mip and array pitches preserve padding and other subresources") {
    Using.resource(ExecutionSession(Nil, graphics = Some(GraphicsRuntime()))) { service =>
      // Two array layers: padded 2x2 mip at 0, padded 1x1 mip at 80; readback at 96.
      val id       = right(service.registerAllocation(128))
      val expected = Array.fill[Byte](128)(0x55.toByte)
      val first    = Vec[Byte](1, 2, 3, 4)
      val second   = Vec[Byte](5, 6, 7, 8)
      val _        = first.copyToArray(expected, 80)
      val _        = second.copyToArray(expected, 88)
      right(service.write(id, 0L, Vec.from(expected)))
      val base = GraphicsImageLevel(
        extent = GraphicsExtent(2, 2, 1),
        offset = 0L,
        rowPitch = 16L,
        slicePitch = 32L,
        layerPitch = 40L
      )
      val mip = GraphicsImageLevel(
        extent = GraphicsExtent(1, 1, 1),
        offset = 80L,
        rowPitch = 8L,
        slicePitch = 8L,
        layerPitch = 8L
      )
      val image = GraphicsImage(
        memory = GraphicsRange(id, 0L, 96L),
        levels = Vec.nonEmpty(base, mip),
        layers = 2,
        samples = 1,
        format = format,
        volume = false
      )
      val copy = GraphicsBufferImageCopy(
        bufferOffset = 0L,
        rowLength = 0,
        imageHeight = 0,
        image = GraphicsLayers(level = 1, baseLayer = 0, layers = 2, colour = true, depth = false, stencil = false),
        offset = GraphicsOffset(0, 0, 0),
        extent = GraphicsExtent(1, 1, 1)
      )
      val completion = right(service.submitGraphics(GraphicsSubmission(Vec(
        GraphicsCommand.ClearColour(image, GraphicsRect(0, 0, 2, 2), Vec(0x3f800000, 0, 0, 0x3f800000)),
        GraphicsCommand.CopyBufferImage(
          buffer = GraphicsRange(id, 96L, 8L),
          image = image,
          regions = Vec(copy),
          toImage = false
        )
      ))))
      right(service.awaitCompletion(completion))
      val texel = Vec[Byte](-1, 0, 0, -1)
      for (offset <- Seq(0, 4, 16, 20, 40, 44, 56, 60)) {
        val _ = texel.copyToArray(expected, offset)
      }
      val _ = first.copyToArray(expected, 96)
      val _ = second.copyToArray(expected, 100)
      assertEquals(right(service.read(id, 0L, 128)), Vec.from(expected))
      right(service.releaseCompletion(completion))
      for (
        invalid <- Vec(
          image.copy(levels = Vec.nonEmpty(base.copy(rowPitch = 4L), mip)),
          image.copy(levels = Vec.nonEmpty(base.copy(slicePitch = 16L), mip)),
          image.copy(levels = Vec.nonEmpty(base.copy(layerPitch = 16L), mip)),
          image.copy(levels = Vec.nonEmpty(base, mip.copy(offset = 79L))),
          image.copy(levels = Vec.nonEmpty(base, mip.copy(layerPitch = Long.MaxValue)))
        )
      )
        assert(service.submitGraphics(GraphicsSubmission(Vec(GraphicsCommand.ClearColour(
          invalid,
          area,
          Vec(0, 0, 0, 0)
        )))).isLeft)
      assertEquals(right(service.read(id, 0L, 128)), Vec.from(expected))
      right(service.releaseAllocation(id))
    }
  }

  test("invalid image footprints fail before reserving a completion or changing storage") {
    Using.resource(ExecutionSession(Nil, graphics = Some(GraphicsRuntime()))) { service =>
      val id    = right(service.registerAllocation(8))
      val image = GraphicsImage(
        memory = GraphicsRange(id, 0L, 8L),
        levels = Vec.nonEmpty(GraphicsImageLevel(GraphicsExtent(3, 1, 1), 0L, 12L, 12L, 12L)),
        layers = 1,
        samples = 1,
        format = format,
        volume = false
      )
      assert(service.submitGraphics(GraphicsSubmission(Vec(GraphicsCommand.ClearColour(
        image,
        area,
        Vec(0, 0, 0, 0)
      )))).isLeft)
      assertEquals(right(service.read(id, 0L, 8)).toVector, Vector.fill(8)(0.toByte))
      right(service.releaseAllocation(id))
    }
  }

  test("compute descriptor preparation retains sources and releases scratch on success and failure") {
    val queued   = scala.collection.mutable.Queue.empty[Runnable]
    val captured = scala.collection.mutable.ArrayBuffer.empty[(AddressSpace, AllocationId)]
    object context extends scala.concurrent.ExecutionContext {
      override def execute(runnable: Runnable): Unit     = queued.enqueue(runnable)
      override def reportFailure(error: Throwable): Unit = throw error
    }
    class Consumer(reject: Boolean, standalone: Boolean) extends Backend.Program {
      override def symbols: Either[Fault, ProgramSymbols] = Right(ProgramSymbols(Vec.empty, Vec.empty))
      override def kernel(name: NonEmpty[String]): Either[Fault, KernelMetadata] = KernelMetadata.validated(
        8,
        1,
        Vec(KernelMetadata.Parameter(8, 0, KernelMetadata.ParameterKind.Allocation, false))
      )
        .left.map(Fault.InvalidRequest(_))
      override def execute(
          task: KernelTask,
          memory: AddressSpace,
          control: TaskControl,
          handler: Event => Either[Fault, Unit]
      ): Either[Fault, Unit] = {
        val region = task.arguments.head match {
          case KernelTask.Argument.Memory(region, _, _) => region
          case _                                        => fail("prepared argument is not memory")
        }
        captured += memory -> region
        if (standalone) {
          assert(task.pointerRelocations.isEmpty)
          val bias = memory.readValue(region, (ImageDescriptor.SamplerWord.MipLodBias * 4L).unsafe, 4).toOption.get
          assertEquals(java.lang.Float.intBitsToFloat(bias.toInt), -0.75f)
        } else {
          val texels = task.pointerRelocations.find(r =>
            r.source == KernelTask.RelocationSource.Memory(region) &&
              r.sourceOffset.raw == ImageDescriptor.Layout.Texels
          ).get
          val red = memory.readValue(texels.target, 0L, 4).toOption.get.toInt
          assertEqualsFloat(java.lang.Float.intBitsToFloat(red), 2f / 255f, 0.000001f)
        }
        if (reject) Left(Fault.Backend("consumer rejected execution")) else Right(())
      }
    }
    class ConsumerBackend(reject: Boolean, standalone: Boolean) extends Backend {
      override val format                                                 = ProgramFormat.SPIRVVulkanGraphics
      override def load(image: Vec[Byte]): Either[Fault, Backend.Program] =
        Right(Consumer(reject, standalone))
    }
    val level      = GraphicsImageLevel(GraphicsExtent(1, 1, 1), 0L, 8L, 8L, 8L)
    val components = GraphicsComponents(
      GraphicsSwizzle.Identity,
      GraphicsSwizzle.Identity,
      GraphicsSwizzle.Identity,
      GraphicsSwizzle.Identity
    )
    for {
      reject     <- Vector(false, true)
      standalone <- Vector(false, true)
    } {
      val backend = ConsumerBackend(reject, standalone)
      Using.resource(ExecutionSession(List(backend), executionContext = context, graphics = Some(GraphicsRuntime()))) {
        service =>
          val source      = right(service.registerAllocation(8))
          val placeholder = right(service.registerAllocation(8))
          val program     = right(service.load(ProgramEncoding.SPIRVVulkanGraphics, Vec.empty))
          val task        = KernelTask.validated(
            "sample",
            LaunchGeometry.Dimensions.One,
            LaunchGeometry.Dimensions.One,
            Vec(if (standalone) KernelTask.Argument.Descriptor
            else KernelTask.Argument.Memory(placeholder, 0L, Some(8L)))
          ).toOption.get
          val imageBinding = DescriptorBinding(
            argument = 0,
            descriptor = GraphicsDescriptor.Image(
              GraphicsImage(
                memory = GraphicsRange(source, 0L, 8L),
                levels = Vec.nonEmpty(level),
                layers = 1,
                samples = 1,
                format = GraphicsFormat.Etc2R8g8b8UnormBlock,
                volume = false
              ),
              components
            )
          )
          val binding = if (!standalone) imageBinding
          else DescriptorBinding(
            argument = 0,
            descriptor = GraphicsDescriptor.Sampler(GraphicsSampler(
              minFilter = GraphicsFilter.Linear,
              magFilter = GraphicsFilter.Nearest,
              addressU = GraphicsAddressMode.Repeat,
              addressV = GraphicsAddressMode.Repeat,
              addressW = GraphicsAddressMode.Repeat,
              unnormalized = false,
              compareEnable = false,
              compareOp = GraphicsCompare.Never,
              borderColour = GraphicsBorderColour.FloatTransparentBlack,
              mipLodBias = -0.75f,
              minLod = 0f,
              maxLod = 4f,
              mipmapMode = GraphicsMipmapMode.Nearest
            ))
          )
          assert(service.launch(program, task, Vec(binding.copy(argument = 1))).isLeft)
          assert(queued.isEmpty)
          val completion = right(service.launch(program, task, Vec(binding)))
          if (standalone) {
            right(service.releaseAllocation(source))
            right(service.releaseAllocation(placeholder))
          } else {
            assert(service.releaseAllocation(source).isLeft)
            assert(service.releaseAllocation(placeholder).isLeft)
          }
          queued.dequeue().run()
          assertEquals(service.awaitCompletion(completion).isLeft, reject)
          val (memory, descriptor) = captured.last
          assert(memory.read(descriptor, 0L, 1).isLeft)
          if (reject) assert(service.completionDirtyRanges(
            id = completion,
            offset = 0,
            limit = ExecutionRPC.MaximumElements
          ).isLeft)
          else assert(right(service.completionDirtyRanges(
            id = completion,
            offset = 0,
            limit = ExecutionRPC.MaximumElements
          )).isEmpty)
          right(service.releaseCompletion(completion))
          if (!standalone) {
            right(service.releaseAllocation(source))
            right(service.releaseAllocation(placeholder))
          }
      }
    }
  }

}
