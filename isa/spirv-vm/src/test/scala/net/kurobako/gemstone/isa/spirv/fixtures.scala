package net.kurobako.gemstone.isa.spirv

import munit.Location
import net.kurobako.gemstone.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.testing.bits.*
import net.kurobako.gemstone.testing.either.*
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchGeometry.*

/** SPIR-V test scaffolding: every test assembles, loads and dispatches through these entry points. */
private object fixtures extends munit.Assertions {

  export assembly.*

  type Program = VMProgram

  def load(module: Module, workgroups: Workgroups = Workgroups.Sequential): Either[Fault, Program] =
    LoadedProgram.load(module, environment(module), workgroups)

  /** For deliberately invalid modules that validation would reject. */
  def loadUnchecked(module: Module, workgroups: Workgroups = Workgroups.Sequential): Either[Fault, Program] =
    LoadedProgram.loadUnchecked(module, workgroups)

  def task(
      entry: NonEmpty[String],
      arguments: Vec[KernelTask.Argument] = Vec.empty,
      local: Dimensions = Dimensions.One,
      groups: Dimensions = Dimensions.One,
      relocations: Vec[KernelTask.PointerRelocation] = Vec.empty,
      globalSize: Option[LaunchGeometry.GlobalDimensions] = None,
      globalOffset: LaunchGeometry.Coordinates = LaunchGeometry.Coordinates.origin
  )(using Location): KernelTask = KernelTask.validated(
    entry,
    local,
    groups,
    arguments,
    globalSize = globalSize,
    pointerRelocations = relocations,
    globalOffset = globalOffset
  ).value

  def execute(
      program: Program,
      task: KernelTask,
      memory: AddressSpace,
      control: TaskControl = TaskControl(),
      handler: Event => Either[Fault, Unit] = _ => Right(())
  ): Either[Fault, Unit] = program.execute(task, memory, control, handler)

  def run(
      module: Module,
      entry: NonEmpty[String],
      memory: AddressSpace,
      arguments: Vec[KernelTask.Argument] = Vec.empty,
      local: Dimensions = Dimensions.One,
      groups: Dimensions = Dimensions.One,
      relocations: Vec[KernelTask.PointerRelocation] = Vec.empty
  )(using Location): Either[Fault, Unit] =
    load(module).flatMap(execute(_, task(entry, arguments, local, groups, relocations), memory))

  def buffer(region: AllocationId, offset: Long >= 0 = 0L, range: Option[Long > 0] = None): KernelTask.Argument =
    KernelTask.Argument.Memory(region, offset, range)

  def scalar(value: Long, width: Int in 0 ~ 8): KernelTask.Argument =
    KernelTask.Argument.Scalar(LittleEndian.toVec(value, width).unsafe)

  def scalarBytes(bytes: Vec[Byte])(using Location): KernelTask.Argument =
    KernelTask.Argument.Scalar(bytes.refine[NonEmpty[Vec[Byte]]].value)

  /** Packs `values` little-endian at `width` bits each. */
  def littleEndian(values: Seq[Long], width: Int)(using Location): Vec[Byte] = {
    val bytes = (width / 8).refine[Int in 0 ~ 8].value
    Vec.from(values.flatMap(LittleEndian.toVec(_, bytes)))
  }

  /** Bits of `value` rounded once to binary16, binary32 or binary64. */
  def floatingBits(value: Double, width: Int)(using Location): Long =
    IEEE754.fromDouble(value, width.refine[Int in (16, 32, 64)].value)

  // Scala Native emits a -0.0 literal, boxed or in an all-zero array, as +0.0
  val negativeZero: Double = java.lang.Double.longBitsToDouble(Long.MinValue)

  val booleans: Seq[Boolean] = Seq(false, true)

  def word(memory: RegionMemory, region: AllocationId, offset: Int): Long =
    LittleEndian.decode(memory.bytes(region), offset, 4)

  def words(memory: RegionMemory, region: AllocationId, offset: Int, count: Int): Vec[Long] =
    Vec.tabulate(count)(index => word(memory, region, offset + index * 4))

  def writeWords(memory: RegionMemory, region: AllocationId, offset: Int, values: Long*): Unit =
    values.zipWithIndex.foreach((value, index) =>
      LittleEndian.write(value, memory.array(region), offset + index * 4, 4)
    )

  /** Covering set in which every pair of values from two different dimensions appears in at least one row. */
  def pairwise(sizes: Int*): Seq[Seq[Int]] = {
    val dimensions = sizes.indices
    val uncovered  = scala.collection.mutable.LinkedHashSet.from(for {
      i <- dimensions
      j <- dimensions if i < j
      a <- 0 until sizes(i)
      b <- 0 until sizes(j)
    } yield (i, a, j, b))
    val rows = Seq.newBuilder[Seq[Int]]
    while (uncovered.nonEmpty) {
      val (i, a, j, b) = uncovered.head
      val row          = Array.fill(sizes.size)(-1)
      row(i) = a; row(j) = b
      for (k <- dimensions if row(k) < 0)
        row(k) = (0 until sizes(k)).maxBy { value =>
          dimensions.count(other =>
            row(other) >= 0 && uncovered.contains(
              if (other < k) (other, row(other), k, value) else (k, value, other, row(other))
            )
          )
        }
      for {
        x <- dimensions
        y <- dimensions if x < y
      } uncovered -= ((x, row(x), y, row(y)))
      rows += row.toSeq
    }
    rows.result()
  }

  def pairwise[A, B, C](as: Seq[A], bs: Seq[B], cs: Seq[C]): Seq[(A, B, C)] =
    pairwise(as.size, bs.size, cs.size).map(row => (as(row(0)), bs(row(1)), cs(row(2))))

  def pairwise[A, B, C, D](as: Seq[A], bs: Seq[B], cs: Seq[C], ds: Seq[D]): Seq[(A, B, C, D)] =
    pairwise(as.size, bs.size, cs.size, ds.size).map(row => (as(row(0)), bs(row(1)), cs(row(2)), ds(row(3))))

  def pairwise[A, B, C, D, E](as: Seq[A], bs: Seq[B], cs: Seq[C], ds: Seq[D], es: Seq[E]): Seq[(A, B, C, D, E)] =
    pairwise(as.size, bs.size, cs.size, ds.size, es.size).map(row =>
      (as(row(0)), bs(row(1)), cs(row(2)), ds(row(3)), es(row(4)))
    )

  def pairwise[A, B, C, D, E, F](
      as: Seq[A],
      bs: Seq[B],
      cs: Seq[C],
      ds: Seq[D],
      es: Seq[E],
      fs: Seq[F]
  ): Seq[(A, B, C, D, E, F)] =
    pairwise(as.size, bs.size, cs.size, ds.size, es.size, fs.size).map(row =>
      (as(row(0)), bs(row(1)), cs(row(2)), ds(row(3)), es(row(4)), fs(row(5)))
    )

  /** One mip level of an image view: extent and byte offset into the texel allocation. */
  case class ImageLevel(width: Int, height: Int = 1, offset: Long = 0L)

  /** Image view in the `ImageDescriptor.Layout` ABI; `slices` is the layer count, or the depth of a volume. */
  case class ImageView(
      format: Int,
      levels: Vec[ImageLevel],
      texelBytes: Long,
      slices: Int = 1,
      volume: Boolean = false,
      samples: Int = 1,
      swizzle: Option[Vec[Int]] = None,
      rowPitch: Option[Long] = None
  ) {
    def size: Int = ImageDescriptor.Layout.LevelTable + levels.size * ImageDescriptor.Layout.LevelBytes +
      swizzle.fold(0)(_ => ImageDescriptor.Layout.SwizzleBytes) +
      rowPitch.fold(0)(_ => ImageDescriptor.Layout.PitchBytes)
  }

  /** Writes `view` and returns the relocation that points its texel pointer at `texels`. */
  def writeImage(
      memory: RegionMemory,
      region: AllocationId,
      offset: Int,
      view: ImageView,
      texels: AllocationId,
      texelOffset: Long >= 0 = 0L
  ): KernelTask.PointerRelocation = {
    import ImageDescriptor.*
    val bytes                                                    = memory.array(region)
    def put(at: Int, value: Long, width: Int in 0 ~ 8 = 4): Unit = LittleEndian.write(value, bytes, offset + at, width)
    put(Layout.Width, view.levels.head.width.toLong)
    put(Layout.Height, view.levels.head.height.toLong)
    put(Layout.Slices, view.slices.toLong)
    put(
      Layout.Format,
      view.format.toLong | Flags.LevelTable | (if (view.volume) Flags.Volume else 0L) |
        (if (view.swizzle.nonEmpty) Flags.Swizzle else 0L) | (if (view.rowPitch.nonEmpty) Flags.Pitched else 0L)
    )
    put(Layout.TexelBytes, view.texelBytes, 8)
    put(Layout.Levels, view.levels.size.toLong)
    put(Layout.Samples, if (view.samples == 1) 0L else view.samples.toLong)
    view.levels.zipWithIndex.foreach { (level, index) =>
      val base = Layout.LevelTable + index * Layout.LevelBytes
      put(base + Layout.LevelWidth, level.width.toLong)
      put(base + Layout.LevelHeight, level.height.toLong)
      put(base + Layout.LevelOffset, level.offset, 8)
    }
    val table = Layout.LevelTable + view.levels.size * Layout.LevelBytes
    view.swizzle.foreach(_.zipWithIndex.foreach((component, lane) => put(table + lane * 4, component.toLong)))
    view.rowPitch.foreach(put(table + view.swizzle.fold(0)(_ => Layout.SwizzleBytes), _, 8))
    KernelTask.PointerRelocation(
      KernelTask.RelocationSource.Memory(region),
      (offset + Layout.Texels).toLong.refine[Long >= 0].value,
      texels,
      texelOffset
    )
  }

  /** Sampler packet in the `ImageDescriptor.SamplerWord` ABI. */
  case class Sampler(
      minFilter: Int = ImageDescriptor.Filter.Nearest,
      magFilter: Int = ImageDescriptor.Filter.Nearest,
      addressU: Int = ImageDescriptor.AddressMode.ClampToEdge,
      addressV: Int = ImageDescriptor.AddressMode.ClampToEdge,
      addressW: Int = ImageDescriptor.AddressMode.ClampToEdge,
      unnormalized: Boolean = false,
      compareOp: Option[Int] = None,
      borderColour: Int = ImageDescriptor.BorderColour.FloatTransparentBlack,
      mipLodBias: Float = 0f,
      minLod: Float = 0f,
      maxLod: Float = 1000f,
      mipmapMode: Int = ImageDescriptor.MipmapMode.Nearest
  )

  val SamplerBytes: Int = ImageDescriptor.SamplerWord.Count * 4

  def writeSampler(memory: RegionMemory, region: AllocationId, offset: Int, sampler: Sampler): Unit = {
    import ImageDescriptor.*
    val bytes                              = memory.array(region)
    def put(index: Int, value: Long): Unit = LittleEndian.write(value, bytes, offset + index * 4, 4)
    put(SamplerWord.MinFilter, sampler.minFilter.toLong)
    put(SamplerWord.MagFilter, sampler.magFilter.toLong)
    put(SamplerWord.AddressU, sampler.addressU.toLong)
    put(SamplerWord.AddressV, sampler.addressV.toLong)
    put(SamplerWord.AddressW, sampler.addressW.toLong)
    put(SamplerWord.Unnormalized, if (sampler.unnormalized) 1L else 0L)
    put(SamplerWord.CompareEnable, if (sampler.compareOp.nonEmpty) 1L else 0L)
    put(SamplerWord.CompareOp, sampler.compareOp.getOrElse(ImageDescriptor.CompareOp.Never).toLong)
    put(SamplerWord.BorderColour, sampler.borderColour.toLong)
    put(SamplerWord.MipLodBias, single(sampler.mipLodBias))
    put(SamplerWord.MinLod, single(sampler.minLod))
    put(SamplerWord.MaxLod, single(sampler.maxLod))
    put(SamplerWord.MipmapMode, sampler.mipmapMode.toLong)
  }

  val CombinedBytes: Int = ImageDescriptor.Layout.InlineHeaderBytes + SamplerBytes

  /** Combined image sampler: a relocated pointer to the image view and its size, then the sampler packet. */
  def writeCombined(
      memory: RegionMemory,
      region: AllocationId,
      offset: Int,
      view: (Int, ImageView),
      sampler: Sampler
  ): KernelTask.PointerRelocation = {
    LittleEndian.write(view._2.size.toLong, memory.array(region), offset + 8, 8)
    writeSampler(memory, region, offset + ImageDescriptor.Layout.InlineHeaderBytes, sampler)
    KernelTask.PointerRelocation(
      KernelTask.RelocationSource.Memory(region),
      offset.toLong.refine[Long >= 0].value,
      region,
      view._1.toLong.refine[Long >= 0].value
    )
  }
}
