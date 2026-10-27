package net.kurobako.gemstone.explorer

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.*
import net.kurobako.gemstone.isa.amdgpu.*
import net.kurobako.gemstone.isa.ptx.*
import net.kurobako.gemstone.isa.sass.*
import net.kurobako.gemstone.isa.spirv.*
import net.kurobako.gemstone.isa.spirv.Validation as SPIRVValidation
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchGeometry.*

/** Runs a loaded program on the real VM and records the timeline of the launch, so the explorer shows the machine that
  * runs the program rather than a model of one.
  */
object ProgramRun {

  /** The least an allocation argument gets: enough for the examples' output buffers and the strides their kernels use.
    * A kernel that indexes beyond this still faults, which the timeline shows.
    */
  private val MinimumAllocation: Long = 64L * 1024L

  object Loaded {
    def apply(
        image: ProgramListing.Image,
        listing: Either[String, Listing],
        kernels: Vec[NonEmpty[String]],
        backend: Backend,
        program: Backend.Program
    ): Loaded = new Loaded(image, listing, kernels, backend, program)
  }

  /** A program the device loaded, the listing of its instructions, and the kernels it exports. */
  class Loaded(
      val image: ProgramListing.Image,
      val listing: Either[String, Listing],
      val kernels: Vec[NonEmpty[String]],
      val backend: Backend,
      val program: Backend.Program
  ) extends AutoCloseable {

    /** The program's instructions, or none when they cannot be listed. */
    def lines: Vec[ProgramLine] = listing.fold(_ => Vec.empty, _.lines)

    def close(): Unit = program.close()
  }

  object Run {
    def apply(
        trace: Trace,
        memory: ArrayMemory,
        allocations: Vec[AllocationId],
        failure: Option[Fault],
        complete: Boolean
    ): Run = new Run(trace, memory, allocations, failure, complete)
  }

  /** One launch: the trace it recorded, the memory its allocation arguments bound, and the fault that ended it. */
  class Run(
      val trace: Trace,
      val memory: ArrayMemory,
      val allocations: Vec[AllocationId],
      val failure: Option[Fault],
      val complete: Boolean
  )

  /** The backends that load `language`, in the order to try them. SPIR-V modules validate against one environment. */
  private def backends(language: Language): Vec[Backend] = language match {
    case Language.PTX    => Vec(PTXBackend())
    case Language.SASS   => Vec(CubinBackend())
    case Language.AMDGPU => Vec(AMDGPUBackend())
    case Language.SPIRV  =>
      Vec(
        SPIRVBackend(SPIRVValidation.Environment.OpenCLCompute),
        SPIRVBackend(SPIRVValidation.Environment.VulkanCompute)
      )
  }

  /** The target the listing decodes with: a cubin or code object names the architecture it was compiled for, which the
    * selector may not know.
    */
  private def listingTarget(image: ProgramListing.Image): Option[String] =
    image.language match {
      case Language.SASS =>
        Cubin.decode(image.bytes).toOption.map(module => s"sm_${module.sm.raw}").orElse(image.target)
      case Language.AMDGPU => codeObjectTarget(image.bytes).orElse(image.target)
      case _               => image.target
    }

  /** The AMDGPU target embedded in a code object's ISA note, such as `gfx900`. */
  private def codeObjectTarget(image: Vec[Byte]): Option[String] = {
    val text = String(Vec.toArray(image), java.nio.charset.StandardCharsets.ISO_8859_1)
    val at   = text.indexOf("amdgcn-amd-amdhsa--")
    Option.when(at >= 0) {
      text.substring(at + "amdgcn-amd-amdhsa--".length).takeWhile(_.isLetterOrDigit)
    }.filter(_.nonEmpty)
  }

  /** Loads `image` as its language. The listing is reported apart from the load, so a program still runs when its
    * instructions cannot be listed.
    */
  def load(image: ProgramListing.Image): Either[String, Loaded] = {
    val attempts = backends(image.language).map(backend => backend -> backend.load(image.bytes))
    attempts.collectFirst { case (backend, Right(program)) => backend -> program } match {
      case None =>
        Left(attempts.flatMap((_, loaded) => loaded.left.toOption.map(_.description.raw)).mkString("; "))
      case Some((backend, program)) =>
        program.symbols.left.map(_.description.raw).map { symbols =>
          val resolved = image.copy(target = listingTarget(image))
          Loaded(
            resolved,
            ProgramListing.of(resolved).map(Listing(_)),
            symbols.kernels.map(_.name),
            backend,
            program
          )
        }
    }
  }

  /** Runs `kernel` over `count` workgroups of `group` lanes, as the loaded program describes it. */
  def run(loaded: Loaded, kernel: NonEmpty[String], group: Dimensions, count: Dimensions): Either[String, Run] =
    loaded.program.kernel(kernel).left.map(_.description.raw).map { metadata =>
      val memory         = ArrayMemory()
      val lanes          = volume(group)
      val groups         = volume(count)
      val size: Long > 0 = math.max(MinimumAllocation, lanes * groups * 4L).unsafe[Long > 0]
      val arguments      = defaultArguments(loaded.image.language, metadata, memory, size)
      launch(loaded, kernel, group, count, memory, arguments)
    }

  /** Runs `kernel` over `count` workgroups of `group` lanes against `memory`, with `arguments` as the kernel's
    * parameters. The caller supplies and owns the memory, so allocations can hold input bytes before the launch and be
    * read back afterwards; [[Run.allocations]] names the memory arguments in parameter order.
    */
  def runWithArguments(
      loaded: Loaded,
      kernel: NonEmpty[String],
      group: Dimensions,
      count: Dimensions,
      memory: ArrayMemory,
      arguments: Vec[KernelTask.Argument]
  ): Either[String, Run] =
    loaded.program.kernel(kernel).left.map(_.description.raw).map(_ =>
      launch(loaded, kernel, group, count, memory, arguments)
    )

  /** The arguments [[run]] gives a kernel of `language` with `metadata`: memory for every allocation and for a SASS
    * eight-byte scalar, which a cubin cannot distinguish from a pointer, and zero bytes for every other scalar.
    */
  def defaultArguments(
      language: Language,
      metadata: KernelMetadata,
      memory: ArrayMemory,
      size: Long > 0
  ): Vec[KernelTask.Argument] = metadata.parameters.map {
    case KernelMetadata.Parameter(kind = KernelMetadata.ParameterKind.Allocation) =>
      KernelTask.Argument.Memory(memory.allocate(size), 0L, Some(size))
    case KernelMetadata.Parameter(width = width, kind = KernelMetadata.ParameterKind.Local) =>
      KernelTask.Argument.Local(width.widen[Long > 0])
    case KernelMetadata.Parameter(kind = KernelMetadata.ParameterKind.Scalar, width = width)
        if language == Language.SASS && width.raw == 8 =>
      // A cubin says how wide a parameter is, not whether it is a pointer; on CUDA 64-bit its eight-byte scalars are
      // addresses, so give them memory of their own to read and write.
      KernelTask.Argument.Memory(memory.allocate(size), 0L, Some(size))
    case KernelMetadata.Parameter(width = width, kind = KernelMetadata.ParameterKind.Scalar) =>
      KernelTask.Argument.Scalar(Vec.fill(width.raw)(0.toByte).unsafe[NonEmpty[Vec[Byte]]])
  }

  /** Runs one launch against caller-owned memory, recording its issues. */
  private def launch(
      loaded: Loaded,
      kernel: NonEmpty[String],
      group: Dimensions,
      count: Dimensions,
      memory: ArrayMemory,
      arguments: Vec[KernelTask.Argument]
  ): Run = {
    val allocations = arguments.collect { case KernelTask.Argument.Memory(region, _, _) => region }.distinct
    val recording   = Recording()
    val result      = for {
      task   <- KernelTask.validated(kernel, group, count, arguments).left.map(_.raw)
      events <- Device(BackendRegistry(Vec(loaded.backend)), memory).launch(loaded.program, task, Some(recording))
    } yield events
    Run(
      recording.trace,
      memory,
      allocations,
      result.swap.toOption.collect { case fault: Fault => fault },
      recording.complete
    )
  }

  private def volume(dimensions: Dimensions): Long =
    dimensions.x.widen[Long > 0].raw * dimensions.y.widen[Long > 0].raw * dimensions.z.widen[Long > 0].raw
}
