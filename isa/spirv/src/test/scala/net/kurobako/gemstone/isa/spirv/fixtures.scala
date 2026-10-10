package net.kurobako.gemstone.isa.spirv

import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference
import java.util.concurrent.locks.ReentrantLock
import munit.Location
import net.kurobako.gemstone.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.concurrent.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.testing.either.*
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchGeometry.*

/** SPIR-V test scaffolding: every test assembles, loads and dispatches through these entry points. */
private object fixtures extends munit.Assertions {

  extension (context: StringContext) def spv(arguments: Any*)(using Location): Module = assemble(context.s(arguments*))

  def assemble(source: String)(using Location): Module =
    asm.Parser.parse(source).fold(error => fail(s"$error in\n$source"), identity)

  val openCL: String =
    """OpCapability Addresses
      |OpCapability Kernel
      |OpCapability Int8
      |OpCapability Int16
      |OpCapability Int64
      |OpCapability Float16
      |OpCapability Float64
      |OpCapability Vector16
      |%opencl = OpExtInstImport "OpenCL.std"
      |OpMemoryModel Physical64 OpenCL
      |""".stripMargin

  val vulkan: String =
    """OpCapability Shader
      |%glsl = OpExtInstImport "GLSL.std.450"
      |OpMemoryModel Logical GLSL450
      |""".stripMargin

  type Program = VMProgram

  def environment(module: Module): Validation.Environment = {
    val models = module.instructions.collect { case Instruction.EntryPoint(model, _, _, _) => model.words.head.raw }
    if (models.contains(ExecutionModel.Kernel)) Validation.Environment.OpenCLCompute
    else if (models.contains(ExecutionModel.GLCompute)) Validation.Environment.VulkanCompute
    else if (models.nonEmpty) Validation.Environment.VulkanGraphics
    else Validation.Environment.OpenCLLibrary
  }

  def load(module: Module, workgroups: Workgroups = Workgroups.Sequential): Either[Fault, Program] =
    VMProgram.load(module, environment(module), workgroups)

  /** For deliberately invalid modules that validation would reject. */
  def loadUnchecked(module: Module, workgroups: Workgroups = Workgroups.Sequential): Either[Fault, Program] =
    VMProgram.loadUnchecked(module, workgroups)

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

  def floatBits(value: Float): Long = java.lang.Float.floatToRawIntBits(value).toLong & 0xffffffffL

  def doubleBits(value: Double): Long = java.lang.Double.doubleToRawLongBits(value)

  def word(memory: RegionMemory, region: AllocationId, offset: Int): Long =
    LittleEndian.decode(memory.bytes(region), offset, 4)

  def words(memory: RegionMemory, region: AllocationId, offset: Int, count: Int): Vec[Long] =
    Vec.tabulate(count)(index => word(memory, region, offset + index * 4))

  def writeWords(memory: RegionMemory, region: AllocationId, offset: Int, values: Long*): Unit =
    values.zipWithIndex.foreach((value, index) =>
      LittleEndian.write(value, memory.array(region), offset + index * 4, 4)
    )

  /** Raw operands, only for modules that the assembler cannot express. */
  def operand(kind: OperandKind, values: Int*): Operand =
    Operand(kind, Vec.from(values.map(Word(_))).unsafe[NonEmpty[Vec[Word]]])

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
    put(SamplerWord.MipLodBias, floatBits(sampler.mipLodBias))
    put(SamplerWord.MinLod, floatBits(sampler.minLod))
    put(SamplerWord.MaxLod, floatBits(sampler.maxLod))
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

  /** Cooperative interleaving: at most `parallelism` workgroups run, one external memory operation at a time. */
  @SuppressWarnings(Array("scalafix:DisableSyntax.var", "scalafix:DisableSyntax.return"))
  class Interleaving(val parallelism: Int > 0 = 4)
      extends Workgroups {
    val calls: AtomicInteger    = java.util.concurrent.atomic.AtomicInteger(0)
    private val lock            = ReentrantLock()
    private val changed         = lock.newCondition()
    private val slot            = ThreadLocal.withInitial[Int](() => -1)
    private var live            = Array.empty[Boolean]
    private var entered         = Array.empty[Boolean]
    private var turn            = 0
    private var deadline        = 0L
    private def advance(): Unit = {
      if (live.exists(value => value)) {
        turn = (turn + 1) % live.length
        while (!live(turn)) turn = (turn + 1) % live.length
      }
      changed.signalAll()
    }
    private def checkpoint(): Unit = lock.withLock {
      val current = slot.get()
      if (current >= 0) {
        if (entered(current)) advance() else entered(current) = true
        while (turn != current && platform.monotonicNanos < deadline) {
          val remaining = deadline - platform.monotonicNanos
          if (remaining > 0L) {
            val _ = changed.awaitNanos(math.min(10_000_000L, remaining))
          }
        }
        assert(turn == current, "cross-workgroup interleaving exceeded its deadline")
      }
      ()
    }
    // Writes pass straight through: the final flush holds the interpreter's shared flush lock.
    class InterleavedMemory(delegate: AddressSpace) extends ForwardingMemory(delegate) {
      override def read(id: AllocationId, offset: Long >= 0, size: Int >= 0): Either[Fault, Vec[Byte]] = {
        checkpoint()
        super.read(id, offset, size)
      }
      override def atomicModifyValue(
          id: AllocationId,
          offset: Long >= 0,
          size: Int in 1 ~ 8
      )(update: Long => Long): Either[Fault, Long] = {
        checkpoint()
        super.atomicModifyValue(id, offset, size)(update)
      }
    }

    override def shouldParallel(tasks: Long >= 0, workItems: Long >= 0): Boolean = tasks.raw > 1L
    // Stop the deterministic schedule at the first worker failure.
    override def parallelFor(tasks: Long >= 0)(task: Long >= 0 => Either[Fault, Unit])
        : Either[Fault, Unit] = {
      calls.incrementAndGet()
      var first = 0L
      while (first < tasks.raw) {
        val count = math.min(parallelism.raw.toLong, tasks.raw - first).toInt
        live = Array.fill(count)(true); entered = Array.fill(count)(false); turn = 0
        deadline = platform.monotonicNanos + 10_000_000_000L
        val failure = AtomicReference(Option.empty[Fault])
        val base    = first
        val threads = Array.tabulate(count) { index =>
          Thread(
            () => {
              slot.set(index)
              // The batching loop keeps base + index in [0, tasks).
              try task((base + index).unsafe[Long >= 0]).left.foreach(x => failure.compareAndSet(None, Some(x)))
              catch {
                case error: Throwable =>
                  val detail: NonEmpty[String] =
                    s"cross-workgroup worker failed: ${Option(error.getMessage).getOrElse(error.getClass.getName)}"

                  failure.set(Some(Fault.Backend(detail)))
              } finally lock.withLock { live(index) = false; if (turn == index) advance(); slot.remove() }
            },
            s"spirv-test-group-${base + index}"
          )
        }
        threads.foreach(_.setDaemon(true))
        threads.foreach(_.start()); threads.foreach(_.join(12000L))
        val timedOut = threads.exists(_.isAlive)
        if (timedOut) {
          threads.foreach(_.interrupt())
          lock.withLock(changed.signalAll())
          threads.foreach(_.join(1000L))
        }
        assert(!timedOut && threads.forall(!_.isAlive), "cross-workgroup worker did not stop")
        failure.get() match { case Some(error) => return Left(error); case None => () }
        first += count
      }
      Right(())
    }
  }
}
