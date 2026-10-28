package net.kurobako.gemstone.isa.spirv.graphics

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.graphics.GraphicsProtocol.*
import net.kurobako.gemstone.isa.spirv.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.testing.either.*
import net.kurobako.gemstone.vm.*
import scala.collection.mutable

class GraphicsMemorySuite extends munit.FunSuite {
  private val allocation = AllocationId(1L)
  private val base       = GuestAddress(0x10000L)

  private class Backing(length: Int = 16) extends RegionMemory() {
    allocate(allocation, length, base)
  }

  test("scratch pointers preserve record identity and bounds without shadowing backing memory") {
    val backing = Backing()
    val access  = GraphicsAccess(backing, TaskControl())
    val first   = Array[Byte](1, 2, 3, 4)
    val (a, _)  = access.allocate(first)
    val (b, _)  = access.allocate(Array[Byte](5, 6, 7, 8))
    val address = access.address(a, 0L).value
    assertEquals(access.read(address, 4).value.toVector, Vector[Byte](1, 2, 3, 4))
    assertEquals(access.writeValue(address.offset(1L), 0x2211L, 2), Right(()))
    assertEquals(first.toVector, Vector[Byte](1, 0x11, 0x22, 4))
    assertEquals(access.read(b, 0L, 4).value.toVector, Vector[Byte](5, 6, 7, 8))
    assert(access.read(address.offset(3L), 2).isLeft)
    assert(access.writeValue(a, 3L, 0L, 2).isLeft)
    assertEquals(access.writeValue(base, 0x44332211L, 4), Right(()))
    assertEquals(backing.bytes(allocation).take(4).toVector, Vector[Byte](0x11, 0x22, 0x33, 0x44))
    access.releaseScratch()
    assert(access.read(address, 1).isLeft)
    assert(access.read(a, 0L, 1).isLeft)
    assertEquals(access.readValue(base, 4), Right(0x44332211L))
  }

  test("graphics memory preserves deferred reads and absolute host-memory operations") {
    val remote = GuestAddress(0x20000L)
    class RemoteBacking extends Backing {
      override def readDeferred(
          region: AllocationId,
          offset: Long >= 0,
          size: Int >= 0
      ): AddressSpace.ReadResult[Vec[Byte]] =
        Left(AddressSpace.Pending)
      override def readValueDeferred(
          region: AllocationId,
          offset: Long >= 0,
          size: Int in 1 ~ 8
      ): AddressSpace.ReadResult[Long] =
        Left(AddressSpace.Pending)
      override def locate(address: GuestAddress, size: Int >= 0): Either[Fault, AddressSpace.Location] =
        if (address == remote) Right(AddressSpace.Location.Absolute(address)) else super.locate(address, size)
      override def readDeferred(address: GuestAddress, size: Int >= 0): AddressSpace.ReadResult[Vec[Byte]] =
        if (address == remote) Right(Vec.fill(size)(42.toByte)) else super.readDeferred(address, size)
      override def write(address: GuestAddress, bytes: Vec[Byte]): Either[Fault, Unit] =
        if (address == remote) write(allocation, 0L, bytes) else super.write(address, bytes)
    }
    val backing = RemoteBacking()
    val access  = GraphicsAccess(backing, TaskControl())
    assertEquals(access.readDeferred(allocation, 0L, 1), Left(AddressSpace.Pending))
    assertEquals(access.readValueDeferred(allocation, 0L, 1), Left(AddressSpace.Pending))
    assertEquals(access.readDeferred(remote, 2), Right(Vec[Byte](42, 42)))
    assertEquals(access.write(remote, Vec[Byte](11, 12)), Right(()))
    assertEquals(backing.bytes(allocation).take(2), Vec[Byte](11, 12))
    val (scratch, _) = access.allocate(Array[Byte](7))
    assertEquals(access.readDeferred(scratch, 0L, 1), Right(Vec[Byte](7)))
    assertEquals(access.readValueDeferred(scratch, 0L, 1), Right(7L))
  }

  test("fixed-function image work observes cancellation and the submission budget") {
    val image = GraphicsImage(
      memory = GraphicsRange(allocation, 0L, 16L),
      levels = Vec.nonEmpty(GraphicsImageLevel(GraphicsExtent(2, 2, 1), 0L, 8L, 16L, 16L)),
      layers = 1,
      samples = 1,
      format = GraphicsFormat.Rgba8,
      volume = false
    )
    val submission =
      GraphicsSubmission(Vec(GraphicsCommand.ClearColour(image, GraphicsRect(0, 0, 2, 2), Vec(0, 0, 0, 0))))
    val cancelled = TaskControl()
    cancelled.cancel()
    assertEquals(
      GraphicsRuntime().execute(submission, TaskContext(Backing(), cancelled, _ => Right(()))),
      Left(Fault.Cancelled)
    )
    val limited = TaskControl(TaskControl.Limits(1L))
    assertEquals(
      GraphicsRuntime().execute(submission, TaskContext(Backing(), limited, _ => Right(()))),
      Left(Fault.BudgetExceeded(1L))
    )
  }

  private def sampler: GraphicsSampler = GraphicsSampler(
    minFilter = GraphicsFilter.Linear,
    magFilter = GraphicsFilter.Nearest,
    addressU = GraphicsAddressMode.ClampToBorder,
    addressV = GraphicsAddressMode.MirroredRepeat,
    addressW = GraphicsAddressMode.ClampToEdge,
    unnormalized = false,
    compareEnable = true,
    compareOp = GraphicsCompare.GreaterOrEqual,
    borderColour = GraphicsBorderColour.FloatOpaqueWhite,
    mipLodBias = -0.75f,
    minLod = 1.25f,
    maxLod = 4.5f,
    mipmapMode = GraphicsMipmapMode.Linear
  )

  test("standalone and combined samplers preserve all fields and release descriptor storage") {
    val standalone = DescriptorBinding(0, GraphicsDescriptor.Sampler(sampler))
    assertEquals(
      GraphicsRuntime().validateDescriptorBindings(Vec(standalone), 1, _ => None),
      Right(Set.empty[AllocationId])
    )
    val bindings =
      Vec(standalone, DescriptorBinding(1, GraphicsDescriptor.Combined(compressedImage, IdentityComponents, sampler)))
    val result = GraphicsRuntime().withDescriptorBindings(bindings, Backing(80), TaskControl()) {
      (memory, descriptors, relocations) =>
        val plain    = memory.read(descriptors(0).allocation, 0L, descriptors(0).size.toInt.unsafe).value
        val combined = memory.read(
          descriptors(1).allocation,
          ImageDescriptor.Layout.InlineHeaderBytes.toLong.unsafe,
          plain.size.unsafe
        ).value
        assertEquals(combined, plain)
        import ImageDescriptor.*
        def word(index: Int): Long = LittleEndian.decode(plain, index * 4, 4)
        assertEquals(word(SamplerWord.MinFilter), Filter.Linear.toLong)
        assertEquals(word(SamplerWord.MagFilter), Filter.Nearest.toLong)
        assertEquals(word(SamplerWord.AddressU), AddressMode.ClampToBorder.toLong)
        assertEquals(word(SamplerWord.AddressV), AddressMode.MirroredRepeat.toLong)
        assertEquals(word(SamplerWord.AddressW), AddressMode.ClampToEdge.toLong)
        assertEquals(word(SamplerWord.Unnormalized), 0L)
        assertEquals(word(SamplerWord.CompareEnable), 1L)
        assertEquals(word(SamplerWord.CompareOp), CompareOp.GreaterOrEqual.toLong)
        assertEquals(word(SamplerWord.BorderColour), BorderColour.FloatOpaqueWhite.toLong)
        assertEquals(word(SamplerWord.MipmapMode), MipmapMode.Linear.toLong)
        assertEquals(java.lang.Float.intBitsToFloat(word(SamplerWord.MipLodBias).toInt), -0.75f)
        assertEquals(java.lang.Float.intBitsToFloat(word(SamplerWord.MinLod).toInt), 1.25f)
        assertEquals(java.lang.Float.intBitsToFloat(word(SamplerWord.MaxLod).toInt), 4.5f)
        assert(!relocations.exists(_.source == KernelTask.RelocationSource.Memory(descriptors(0).allocation)))
        Right((memory, descriptors))
    }.value
    result._2.foreach(range => assert(result._1.read(range.allocation, 0L, 1).isLeft))
    val cancelled = TaskControl()
    cancelled.cancel()
    assertEquals(
      GraphicsRuntime().withDescriptorBindings(Vec(standalone), Backing(80), cancelled) { (_, _, _) =>
        fail("cancelled sampler preparation reached execution")
      },
      Left(Fault.Cancelled)
    )
    val invalid = Vec(sampler.copy(mipLodBias = Float.NaN), sampler.copy(minLod = 5f))
      .map(value => DescriptorBinding(0, GraphicsDescriptor.Sampler(value)))
    invalid.foreach(binding => assert(GraphicsRuntime().validateDescriptorBindings(Vec(binding), 1, _ => None).isLeft))
  }

  private val IdentityComponents = GraphicsComponents(
    GraphicsSwizzle.Identity,
    GraphicsSwizzle.Identity,
    GraphicsSwizzle.Identity,
    GraphicsSwizzle.Identity
  )
  private def imageBinding(index: Int >= 0, image: GraphicsImage): DescriptorBinding =
    DescriptorBinding(index, GraphicsDescriptor.Image(image, IdentityComponents))
  private def compressedImage: GraphicsImage = GraphicsImage(
    memory = GraphicsRange(allocation, 0L, 80L),
    // Padding separates both the selected layers and mip levels.
    levels = Vec.nonEmpty(
      GraphicsImageLevel(GraphicsExtent(4, 4, 1), 8L, 8L, 8L, 16L),
      GraphicsImageLevel(GraphicsExtent(2, 2, 1), 48L, 8L, 8L, 8L)
    ),
    layers = 2,
    samples = 1,
    format = GraphicsFormat.Etc2R8g8b8UnormBlock,
    volume = false
  )
  private def compressedBinding: DescriptorBinding = imageBinding(0, compressedImage)

  test("sampled image preparation preserves mip/layer selections and shares decoded storage across descriptors") {
    val backing = Backing(80)
    // ETC individual-mode constant red bases 17, 34, 51, 68, each with modifier +2.
    Vector(8, 24, 48, 56).zipWithIndex.foreach { (offset, index) =>
      assertEquals(backing.writeValue(allocation, offset.toLong.unsafe, (index + 1L) * 0x11, 1), Right(()))
    }
    val source   = compressedBinding
    val combined = DescriptorBinding(1, GraphicsDescriptor.Combined(compressedImage, IdentityComponents, sampler))
    val swizzled = DescriptorBinding(
      2,
      GraphicsDescriptor.Image(
        compressedImage,
        GraphicsComponents(GraphicsSwizzle.B, GraphicsSwizzle.G, GraphicsSwizzle.R, GraphicsSwizzle.One)
      )
    )
    val bindings = Vec(source, combined, swizzled)
    assertEquals(
      GraphicsRuntime().validateDescriptorBindings(bindings, 3, id => Option.when(id == allocation)(80)),
      Right(Set(allocation))
    )
    val result =
      GraphicsRuntime().withDescriptorBindings(bindings, backing, TaskControl()) { (memory, descriptors, relocations) =>
        def read(range: GraphicsRange, offset: Long, width: Int in 1 ~ 8): Long =
          memory.readValue(range.allocation, (range.offset.raw + offset).unsafe, width).value
        def target(range: GraphicsRange, offset: Long): GraphicsRange = {
          val relocation = relocations.find(r =>
            r.source == KernelTask.RelocationSource.Memory(range.allocation) &&
              r.sourceOffset.raw == offset
          ).get
          GraphicsRange(relocation.target, relocation.targetOffset, 1L)
        }
        val descriptor = descriptors.head
        assertEquals(target(descriptors(1), 0L).allocation, descriptor.allocation)
        assertEquals(read(descriptors(1), 8L, 8), descriptor.size.raw)
        val pixels = target(descriptor, ImageDescriptor.Layout.Texels)
        assertEquals(target(descriptors(2), ImageDescriptor.Layout.Texels).allocation, pixels.allocation)
        assertEquals(read(descriptor, ImageDescriptor.Layout.TexelBytes, 8), 480L)
        assertEquals(
          read(
            descriptor,
            ImageDescriptor.Layout.LevelTable + ImageDescriptor.Layout.LevelBytes +
              ImageDescriptor.Layout.LevelOffset,
            8
          ),
          384L
        )
        Vector(0L, 192L, 384L, 432L).zipWithIndex.foreach { (offset, index) =>
          val red = java.lang.Float.intBitsToFloat(read(pixels, offset, 4).toInt)
          assertEqualsFloat(red, ((index + 1) * 17 + 2).toFloat / 255f, 0.000001f)
        }
        val swizzleOffset = ImageDescriptor.Layout.LevelTable + 2 * ImageDescriptor.Layout.LevelBytes
        assertEquals(read(descriptors(2), swizzleOffset, 4), ImageDescriptor.Swizzle.B.toLong)
        assertEquals(
          read(descriptors(2), ImageDescriptor.Layout.Format, 4) & ImageDescriptor.Flags.Swizzle,
          ImageDescriptor.Flags.Swizzle
        )
        Right((memory, descriptor, pixels))
      }.value
    // The borrowed memory is no longer usable for any prepared storage after the execution scope ends.
    assert(result._1.read(result._2.allocation, 0L, 1).isLeft)
    assert(result._1.read(result._3.allocation, 0L, 1).isLeft)
    assertEquals(backing.readValue(allocation, 8L, 1), Right(0x11L))
  }

  test("sampled image preparation rejects malformed views before execution") {
    val valid    = compressedBinding
    val overflow = compressedImage.copy(levels =
      Vec.nonEmpty(GraphicsImageLevel(
        GraphicsExtent(2147483647, 2147483647, 1),
        0L,
        8L,
        8L,
        8L
      ))
    )
    val invalid = Vector(
      Vec(valid.copy(argument = 1)),
      Vec(valid, valid),
      Vec(imageBinding(0, compressedImage.copy(samples = 4))),
      Vec(imageBinding(0, compressedImage.copy(memory = compressedImage.memory.copy(size = 63L)))),
      Vec(imageBinding(0, overflow))
    )
    invalid.foreach(bindings =>
      assert(GraphicsRuntime().validateDescriptorBindings(bindings, 1, _ => Some(80)).isLeft)
    )
  }

  test("sampled image preparation observes cancellation and releases scratch on consumer failure") {
    val binding   = compressedBinding
    val cancelled = TaskControl()
    cancelled.cancel()
    val result = GraphicsRuntime().withDescriptorBindings(Vec(binding), Backing(80), cancelled) { (_, _, _) =>
      fail("cancelled preparation reached the shader")
    }
    assertEquals(result, Left(Fault.Cancelled))
    val captured = mutable.ArrayBuffer.empty[(AddressSpace, GraphicsRange)]
    val failed   =
      GraphicsRuntime().withDescriptorBindings(Vec(binding), Backing(80), TaskControl()) { (memory, descriptors, _) =>
        captured += memory -> descriptors.head
        Left(Fault.Unsupported("test consumer failure"))
      }
    assertEquals(failed, Left(Fault.Unsupported("test consumer failure")))
    assert(captured.head._1.read(captured.head._2.allocation, 0L, 1).isLeft)
  }

  private def storageImage: GraphicsImage = GraphicsImage(
    memory = GraphicsRange(allocation, 16L, 96L),
    // A selected mip starts eight bytes into the view, with two layers and four samples per texel.
    levels = Vec.nonEmpty(GraphicsImageLevel(GraphicsExtent(2, 1, 1), 8L, 32L, 32L, 32L)),
    layers = 2,
    samples = 4,
    format = GraphicsFormat.R32ui,
    volume = false
  )
  private def storageBinding: DescriptorBinding = imageBinding(0, storageImage)

  test("storage images and texel buffers retain overlapping backing ranges and multisample layout") {
    val backing = Backing(128)
    assertEquals(backing.write(allocation, 0L, Vec.fill(128)(0x55.toByte)), Right(()))
    val image  = storageBinding
    val texels = imageBinding(
      1,
      storageImage.copy(
        memory = GraphicsRange(allocation, 56L, 8L),
        levels = Vec.nonEmpty(GraphicsImageLevel(GraphicsExtent(2, 1, 1), 0L, 8L, 8L, 8L)),
        layers = 1,
        samples = 1
      )
    )
    val bindings = Vec(image, texels)
    assertEquals(GraphicsRuntime().validateDescriptorBindings(bindings, 2, _ => Some(128)), Right(Set(allocation)))
    val result =
      GraphicsRuntime().withDescriptorBindings(bindings, backing, TaskControl()) { (memory, descriptors, relocations) =>
        def readDescriptor(index: Int, offset: Int, width: Int in 1 ~ 8): Long =
          memory.readValue(descriptors(index).allocation, offset.toLong.unsafe, width).value
        def target(index: Int): KernelTask.PointerRelocation = relocations.find(r =>
          r.source == KernelTask.RelocationSource.Memory(descriptors(index).allocation) &&
            r.sourceOffset.raw == ImageDescriptor.Layout.Texels
        ).get
        assertEquals(readDescriptor(0, ImageDescriptor.Layout.Samples, 4), 4L)
        assertEquals(readDescriptor(1, ImageDescriptor.Layout.Samples, 4), 0L)
        assertEquals(readDescriptor(0, ImageDescriptor.Layout.TexelBytes, 8), 96L)
        assertEquals(readDescriptor(1, ImageDescriptor.Layout.TexelBytes, 8), 8L)
        assertEquals(target(0).target, allocation)
        assertEquals(target(1).target, allocation)
        assertEquals(target(0).targetOffset.raw, 16L)
        assertEquals(target(1).targetOffset.raw, 56L)
        val mip         = readDescriptor(0, ImageDescriptor.Layout.LevelTable + ImageDescriptor.Layout.LevelOffset, 8)
        val secondLayer = (target(0).targetOffset.raw + mip + 32L).unsafe[Long >= 0]
        assertEquals(memory.writeValue(target(0).target, secondLayer, 0x12345678L, 4), Right(()))
        assertEquals(memory.readValue(target(1).target, target(1).targetOffset, 4), Right(0x12345678L))
        assertEquals(memory.writeValue(target(1).target, 60L, 0x11223344L, 4), Right(()))
        Right((memory, descriptors))
      }.value
    result._2.foreach(range => assert(result._1.read(range.allocation, 0L, 1).isLeft))
    val expected = Array.fill[Byte](128)(0x55.toByte)
    LittleEndian.write(0x12345678L, expected, 56, 4)
    LittleEndian.write(0x11223344L, expected, 60, 4)
    assertEquals(backing.read(allocation, 0L, 128).value.toArray.toVector, expected.toVector)
  }

  test("depth and stencil views share storage stride and preserve the other aspect and padding") {
    val backing = Backing(16)
    assertEquals(backing.write(allocation, 0L, Vec.fill(16)(0x55.toByte)), Right(()))
    val depth = GraphicsImage(
      memory = GraphicsRange(allocation, 0L, 16L),
      levels = Vec.nonEmpty(GraphicsImageLevel(GraphicsExtent(2, 1, 1), 0L, 16L, 16L, 16L)),
      layers = 1,
      samples = 1,
      format = GraphicsFormat.D32SfloatS8Depth,
      volume = false
    )
    val stencil = depth.copy(format = GraphicsFormat.D32SfloatS8Stencil)
    val clear   = GraphicsSubmission(Vec(
      GraphicsCommand.ClearDepthStencil(
        depth,
        GraphicsRect(0, 0, 1, 1),
        0.25f,
        0,
        clearDepth = true,
        clearStencil = false
      ),
      GraphicsCommand.ClearDepthStencil(
        stencil,
        GraphicsRect(1, 0, 1, 1),
        0f,
        0x42,
        clearDepth = false,
        clearStencil = true
      )
    ))
    assertEquals(GraphicsRuntime().validate(clear, _ => Some(16)), Right(Set(allocation)))
    assert(GraphicsRuntime().execute(clear, TaskContext(backing, TaskControl(), _ => Right(()))).isRight)
    val expected = Array.fill[Byte](16)(0x55.toByte)
    LittleEndian.write(java.lang.Float.floatToRawIntBits(0.25f).toLong, expected, 0, 4)
    // Combined D32/S8 pixels have an eight-byte stride and stencil in byte four.
    expected(12) = 0x42.toByte
    assertEquals(backing.bytes(allocation).toVector, expected.toVector)
    val bindings = Vec(imageBinding(0, depth), imageBinding(1, stencil))
    assertEquals(GraphicsRuntime().validateDescriptorBindings(bindings, 2, _ => Some(16)), Right(Set(allocation)))
    val result =
      GraphicsRuntime().withDescriptorBindings(bindings, backing, TaskControl()) { (memory, descriptors, relocations) =>
        val codes = descriptors.map(range =>
          memory.readValue(range.allocation, ImageDescriptor.Layout.Format.toLong.unsafe, 4).value &
            ImageDescriptor.Flags.FormatMask
        )
        assertEquals(
          codes,
          Vec(ImageDescriptor.Format.D32SfloatS8Depth.toLong, ImageDescriptor.Format.D32SfloatS8Stencil.toLong)
        )
        assertEquals(relocations.map(_.target), Vec(allocation, allocation))
        assertEquals(relocations.map(_.targetOffset.raw), Vec(0L, 0L))
        Right(())
      }
    assertEquals(result, Right(()))
  }

  test("shader descriptors reject padded layouts and format mismatches while transfers allow padding") {
    val valid   = storageImage
    val level   = valid.levels.head
    val invalid = Vector(
      valid.copy(format = GraphicsFormat.Rg32ui),
      valid.copy(levels = Vec.nonEmpty(level.copy(rowPitch = 36L, slicePitch = 36L, layerPitch = 36L))),
      valid.copy(memory = valid.memory.copy(size = 71L))
    )
    invalid.foreach(image =>
      assert(GraphicsRuntime().validateDescriptorBindings(Vec(imageBinding(0, image)), 1, _ => Some(128)).isLeft)
    )
    val padded = invalid(1)
    val clear  = GraphicsSubmission(Vec(GraphicsCommand.ClearColour(padded, GraphicsRect(0, 0, 2, 1), Vec(0, 0, 0, 0))))
    assertEquals(GraphicsRuntime().validate(clear, _ => Some(128)), Right(Set(allocation)))
  }

}
