package net.kurobako.gemstone.explorer

import java.nio.charset.StandardCharsets
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.*
import net.kurobako.gemstone.isa.amdgpu.*
import net.kurobako.gemstone.isa.amdgpu.CodeObject as AmdCodeObject
import net.kurobako.gemstone.isa.amdgpu.Target as AmdTarget
import net.kurobako.gemstone.isa.amdgpu.asm as amdAsm
import net.kurobako.gemstone.isa.sass.*
import net.kurobako.gemstone.isa.sass.Target as SassTarget
import net.kurobako.gemstone.isa.sass.asm as sassAsm
import net.kurobako.gemstone.isa.spirv.*
import net.kurobako.gemstone.isa.spirv.Capability as SpirvCapability
import net.kurobako.gemstone.isa.spirv.ISA as SpirvISA
import net.kurobako.gemstone.isa.spirv.Instruction as SpirvInstruction
import net.kurobako.gemstone.isa.spirv.LiteralString as SpirvLiteralString
import net.kurobako.gemstone.isa.spirv.Module as SpirvModule
import net.kurobako.gemstone.isa.spirv.Operand as SpirvOperand
import net.kurobako.gemstone.isa.spirv.Validation as SpirvValidation
import net.kurobako.gemstone.isa.spirv.asm as spirvAsm
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.string.*
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchGeometry.*

/** Runs a small synthetic program on the real VM: the ISA assembler produces code, a template image is patched, and
  * [[ProgramRun]] launches it. There is no second interpreter or scheduler here; the returned trace is the VM's own
  * recording. Setup failures are typed so the explorer can show the Program page instead.
  */
object SandboxRunner {

  def run(program: SandboxProgram): Either[SandboxFailure, SandboxRun] = program.language match {
    case Language.SASS   => sass(program)
    case Language.AMDGPU => amdgpu(program)
    case Language.PTX    => ptx(program)
    case Language.SPIRV  => spirv(program)
  }

  private def sass(program: SandboxProgram): Either[SandboxFailure, SandboxRun] = {
    val template = program.target match {
      case None        => Right(SassTemplate.sm89)
      case Some(value) =>
        SassTarget.parse(value).left.map(detail => SandboxFailure.Assembly(Language.SASS, detail)).flatMap {
          requested =>
            if (requested.sm == SassTemplate.sm50.target.sm) Right(SassTemplate.sm50)
            else if (requested.sm == SassTemplate.sm89.target.sm) Right(SassTemplate.sm89)
            else
              Left(
                SandboxFailure.Unavailable(
                  Language.SASS,
                  s"no SASS template for ${requested.name}; the sandbox supports sm_50 and sm_89."
                )
              )
        }
    }
    for {
      image  <- template
      _      <- validateTemplateParameters(program)
      _      <- validateKernel(program, image.kernel)
      source <- sassAsm.Parser.parseProgram(program.instructions)
        .left.map(error => SandboxFailure.Assembly(Language.SASS, error.message))
      code <- sassAsm.Assembler(image.target).encode(source)
        .left.map(error => SandboxFailure.Assembly(Language.SASS, error.message))
      patched <- Cubin.replaceKernel(image.bytes, image.kernel, code)
        .left.map(error => SandboxFailure.Patch(Language.SASS, error.raw))
      loaded <- ProgramRun.load(ProgramListing.Image(Language.SASS, patched, Some(image.target.name)))
        .left.map(detail => SandboxFailure.Load(Language.SASS, detail))
      result <- runLoadedAndClose(program, loaded, image.kernel)
    } yield result
  }

  private def amdgpu(program: SandboxProgram): Either[SandboxFailure, SandboxRun] =
    for {
      bytes <- Hex.parse(AmdgpuExample.codeObject)
        .left.map(detail => SandboxFailure.Unavailable(Language.AMDGPU, detail))
      object_ <- AmdCodeObject.decode(bytes)
        .left.map(error => SandboxFailure.Load(Language.AMDGPU, error.message.raw))
      recorded <- object_.target.left.map(error => SandboxFailure.Load(Language.AMDGPU, error.message.raw))
      target   <- amdgpuTarget(program.target, recorded)
      kernel = AmdgpuExample.kernel.unsafe[NonEmpty[String]]
      _      <- validateTemplateParameters(program)
      _      <- validateKernel(program, kernel)
      source <- amdAsm.Parser.parse(program.instructions)
        .left.map(error => SandboxFailure.Assembly(Language.AMDGPU, error.diagnostic.raw))
      code <- amdAsm.Assembler.encode(source, TargetMode(target, TargetDefaults.waveSize(target)))
        .left.map(error => SandboxFailure.Assembly(Language.AMDGPU, error.toString))
      patched <- object_.replaceKernel(kernel, code)
        .left.map(error => SandboxFailure.Patch(Language.AMDGPU, error.message.raw))
      loaded <- ProgramRun.load(ProgramListing.Image(Language.AMDGPU, patched.image, Some(target.name.raw)))
        .left.map(detail => SandboxFailure.Load(Language.AMDGPU, detail))
      result <- runLoadedAndClose(program, loaded, kernel)
    } yield result

  private def amdgpuTarget(
      requested: Option[String],
      recorded: AmdTarget
  ): Either[SandboxFailure, AmdTarget] = requested match {
    case None        => Right(recorded)
    case Some(value) =>
      value.refine[AmdTarget].left.map(detail => SandboxFailure.Assembly(Language.AMDGPU, detail.raw)).flatMap {
        target =>
          if (target == recorded) Right(target)
          else
            Left(
              SandboxFailure.Unavailable(
                Language.AMDGPU,
                s"the sandbox template is ${recorded.name.raw}, not ${target.name.raw}."
              )
            )
      }
  }

  /** Runs a complete SPIR-V assembly module through the W4 facade, which uses the real assembler, loader, op table and
    * interpreter. The runner adds only the explorer evidence: a generic trace for PCs and units, a listing for the
    * panel, and the facade's typed invocation steps.
    */
  private def spirv(program: SandboxProgram): Either[SandboxFailure, SandboxRun] =
    for {
      _ <- Either.cond(
        program.parameters.isEmpty,
        (),
        SandboxFailure.Setup(Language.SPIRV, "SPIR-V parameters are declared by the module's function signature")
      )
      module <- spirvAsm.Parser.parse(program.instructions)
        .left.map(error => SandboxFailure.Assembly(Language.SPIRV, error.diagnostic.raw))
      image <- SpirvISA.encode(module)
        .left.map(error => SandboxFailure.Assembly(Language.SPIRV, error.message.raw))
      entry       <- spirvEntry(program, module)
      environment <- spirvEnvironment(program, module)
      _           <- spirvPreload(image, environment)
      lines = ProgramListing
        .of(ProgramListing.Image(Language.SPIRV, image, None))
        .fold(_ => Vec.empty[ProgramLine], identity)
      result <- SPIRVSandbox.run(
        program.instructions,
        entry.raw,
        program.allocations,
        program.scalars,
        program.group,
        program.count,
        environment
      ).left.map(spirvFailure(_))
    } yield {
      val trace = spirvTrace(entry, result)
      SandboxRun(
        program,
        entry,
        lines,
        trace,
        spirvRegisters(result.values),
        result.memory,
        result.allocations,
        result.failure,
        result.complete,
        Some(result)
      )
    }

  private def spirvEntry(
      program: SandboxProgram,
      module: SpirvModule
  ): Either[SandboxFailure, NonEmpty[String]] = program.kernel match {
    case Some(value) =>
      value.refine[NonEmpty[String]].left.map(_ =>
        SandboxFailure.Setup(Language.SPIRV, "the sandbox kernel name is empty")
      )
    case None =>
      module.instructions.collectFirst {
        case SpirvInstruction.EntryPoint(name = name) => SpirvLiteralString.decode(name.words)
      }.flatMap(_.refine[NonEmpty[String]].toOption)
        .toRight(SandboxFailure.Setup(Language.SPIRV, "the module declares no OpEntryPoint; name the entry point"))
  }

  /** A target string selects the validation environment; without one the module's capability chooses it. */
  private def spirvEnvironment(
      program: SandboxProgram,
      module: SpirvModule
  ): Either[SandboxFailure, SpirvValidation.Environment] = program.target match {
    case Some(value) =>
      value.trim.toLowerCaseRoot match {
        case "opencl" | "openclcompute" | "kernel" => Right(SpirvValidation.Environment.OpenCLCompute)
        case "vulkan" | "vulkancompute" | "shader" => Right(SpirvValidation.Environment.VulkanCompute)
        case "vulkangraphics" | "vulkan-graphics"  => Right(SpirvValidation.Environment.VulkanGraphics)
        case "opencllibrary" | "opencl-library"    => Right(SpirvValidation.Environment.OpenCLLibrary)
        case other                                 =>
          Left(
            SandboxFailure.Unavailable(
              Language.SPIRV,
              s"unsupported SPIR-V environment '$other'; use opencl or vulkan."
            )
          )
      }
    case None =>
      val capabilities = module.instructions.collect {
        case SpirvInstruction.Capability(capability) => SpirvOperand.unsigned(capability)
      }.toSet
      if (capabilities.contains(SpirvCapability.Kernel.toLong)) Right(SpirvValidation.Environment.OpenCLCompute)
      else if (capabilities.contains(SpirvCapability.Shader.toLong)) Right(SpirvValidation.Environment.VulkanCompute)
      else Right(SpirvValidation.Environment.OpenCLCompute)
  }

  /** The W4 facade reports load faults and execution faults through the same `Fault`; loading once here keeps the
    * explorer's typed failure honest, then the facade owns the observed launch.
    */
  private def spirvPreload(
      image: Vec[Byte],
      environment: SpirvValidation.Environment
  ): Either[SandboxFailure, Unit] = environment match {
    case SpirvValidation.Environment.OpenCLCompute | SpirvValidation.Environment.VulkanCompute =>
      SPIRVBackend(environment).load(image) match {
        case Left(fault)   => Left(SandboxFailure.Load(Language.SPIRV, fault.description.raw))
        case Right(loaded) =>
          try Right(())
          finally loaded.close()
      }
    case _ => Right(())
  }

  private def spirvFailure(fault: Fault): SandboxFailure =
    if (fault.location.nonEmpty) SandboxFailure.Launch(Language.SPIRV, fault.description.raw)
    else
      fault match {
        case Fault.InvalidRequest(_) | Fault.InvalidConfiguration(_) =>
          SandboxFailure.Setup(Language.SPIRV, fault.description.raw)
        case _ => SandboxFailure.Load(Language.SPIRV, fault.description.raw)
      }

  private def spirvTrace(kernel: NonEmpty[String], result: SPIRVSandbox.Result): Trace = {
    val units = result.transitions.map(event => event.unit.ordinal.raw -> event.unit).toMap
    val steps = result.steps.map { step =>
      val ordinal = step.ordinal.unsafe[Int >= 0]
      val unit    = units.getOrElse(
        step.ordinal,
        MachineUnit(Coordinates.origin, ordinal, 1, MachineUnit.State.Running)
      )
      val pc = math.max(0, step.pc).toLong.unsafe[Long >= 0]
      TraceStep(
        step.issued,
        Fault.Location(kernel, None, Some(pc), None, Some(ordinal)),
        unit,
        spirvRegisters(step.values)
      )
    }
    Trace(steps, result.transitions)
  }

  private def spirvRegisters(values: Vec[(Int, InvocationView.Value)]): Vec[RegisterValue] =
    values.flatMap { (id, value) =>
      value match {
        case InvocationView.Value.Bits(lanes) =>
          lanes.zipWithIndex.map((bits, lane) => RegisterValue(lane, s"%$id", bits))
        case InvocationView.Value.Address(location) =>
          Vec(RegisterValue(0, s"%$id", location))
      }
    }

  private def ptx(program: SandboxProgram): Either[SandboxFailure, SandboxRun] =
    for {
      source <- ptxSource(program)
      target = program.target.getOrElse("sm_80")
      image  = ProgramListing.Image(Language.PTX, Vec.from(source.getBytes(StandardCharsets.UTF_8)), Some(target))
      loaded <- ProgramRun.load(image)
        .left.map(detail => SandboxFailure.Load(Language.PTX, detail))
      result <- runLoadedAndClose(program, loaded, "kernel".unsafe[NonEmpty[String]])
    } yield result

  /** A complete PTX entry point around the caller's instructions. The wrapper declares four register files wide enough
    * for a snippet and appends the `ret` the caller need not write.
    */
  private def ptxSource(program: SandboxProgram): Either[SandboxFailure, String] = {
    val invalid = program.parameters.collectFirst {
      case SandboxProgram.Parameter(name, SandboxProgram.Parameter.Kind.Scalar(bits)) if bits != 32 && bits != 64 =>
        SandboxFailure.Setup(Language.PTX, s"PTX scalar parameter '$name' has unsupported width $bits")
    }
    invalid.toLeft {
      val kernel     = program.kernel.getOrElse("kernel")
      val parameters = program.parameters.map { case SandboxProgram.Parameter(name, kind) =>
        kind match {
          case SandboxProgram.Parameter.Kind.Pointer      => s".param .u64 .ptr .global $name"
          case SandboxProgram.Parameter.Kind.Scalar(bits) => s".param .${if (bits == 64) "u64" else "u32"} $name"
        }
      }.mkString(", ")
      s""".version 8.0
         |.target ${program.target.getOrElse("sm_80")}
         |.address_size 64
         |.visible .entry $kernel($parameters) {
         |  .reg .b32 %r<32>;
         |  .reg .b64 %rd<32>;
         |  .reg .pred %p<8>;
         |${program.instructions}
         |  ret;
         |}
         |""".stripMargin
    }
  }

  private def runLoadedAndClose(
      program: SandboxProgram,
      loaded: ProgramRun.Loaded,
      defaultKernel: NonEmpty[String]
  ): Either[SandboxFailure, SandboxRun] =
    try runLoaded(program, loaded, defaultKernel)
    finally loaded.close()

  private def runLoaded(
      program: SandboxProgram,
      loaded: ProgramRun.Loaded,
      defaultKernel: NonEmpty[String]
  ): Either[SandboxFailure, SandboxRun] =
    kernelName(program, defaultKernel).flatMap { kernel =>
      initialise(program, kernel, loaded).flatMap { (memory, arguments) =>
        ProgramRun.runWithArguments(loaded, kernel, program.group, program.count, memory, arguments)
          .left.map(detail => SandboxFailure.Launch(program.language, detail))
          .map(run =>
            SandboxRun(
              program,
              kernel,
              loaded.lines.filter(line => line.function.forall(_ == kernel.raw)),
              run.trace,
              run.trace.steps.lastOption.fold(Vec.empty[RegisterValue])(_.values),
              run.memory,
              run.allocations,
              run.failure,
              run.complete
            )
          )
      }
    }

  private def validateTemplateParameters(program: SandboxProgram): Either[SandboxFailure, Unit] =
    Either.cond(
      program.parameters.isEmpty,
      (),
      SandboxFailure.Setup(program.language, "SASS and AMDGPU templates carry their own parameters")
    )

  private def validateKernel(program: SandboxProgram, template: NonEmpty[String]): Either[SandboxFailure, Unit] =
    program.kernel match {
      case Some(name) if name != template.raw =>
        Left(
          SandboxFailure.Setup(
            program.language,
            s"the sandbox template patches '${template.raw}', not '$name'"
          )
        )
      case _ => Right(())
    }

  private def kernelName(
      program: SandboxProgram,
      default: NonEmpty[String]
  ): Either[SandboxFailure, NonEmpty[String]] = program.kernel match {
    case Some(value) =>
      value.refine[NonEmpty[String]].left.map(_ =>
        SandboxFailure.Setup(program.language, "the sandbox kernel name is empty")
      )
    case None => Right(default)
  }

  /** The kernel's memory and arguments, with the caller's initial bytes in place and every omitted allocation zeroed.
    */
  private def initialise(
      program: SandboxProgram,
      kernel: NonEmpty[String],
      loaded: ProgramRun.Loaded
  ): Either[SandboxFailure, (ArrayMemory, Vec[KernelTask.Argument])] =
    loaded.program.kernel(kernel).left.map(fault => SandboxFailure.Load(program.language, fault.description.raw))
      .flatMap { metadata =>
        val memory    = ArrayMemory()
        val arguments = Vec.newBuilder[KernelTask.Argument]
        def loop(
            index: Int,
            allocationIndex: Int,
            scalarIndex: Int
        ): Either[SandboxFailure, (allocationIndex: Int, scalarIndex: Int)] =
          if (index == metadata.parameters.size)
            Right((allocationIndex = allocationIndex, scalarIndex = scalarIndex))
          else {
            val parameter = metadata.parameters(index)
            def memoryArgument: Either[SandboxFailure, (allocationIndex: Int, scalarIndex: Int)] =
              allocate(program, memory, allocationIndex).flatMap { (allocation, size) =>
                arguments += KernelTask.Argument.Memory(allocation, 0L, Some(size))
                loop(index = index + 1, allocationIndex = allocationIndex + 1, scalarIndex = scalarIndex)
              }
            parameter.kind match {
              case KernelMetadata.ParameterKind.Allocation => memoryArgument
              case KernelMetadata.ParameterKind.Local      =>
                arguments += KernelTask.Argument.Local(parameter.width.widen[Long > 0])
                loop(index = index + 1, allocationIndex = allocationIndex, scalarIndex = scalarIndex)
              case KernelMetadata.ParameterKind.Scalar
                  if program.language == Language.SASS && parameter.width.raw == 8 =>
                // As in ProgramRun.run, a cubin's eight-byte scalar on CUDA 64-bit is an address.
                memoryArgument
              case KernelMetadata.ParameterKind.Scalar =>
                val bytes = program.scalars.lift(scalarIndex).getOrElse(Vec.empty[Byte])
                val width = parameter.width.raw
                if (bytes.size > width)
                  Left(
                    SandboxFailure.Setup(
                      program.language,
                      s"scalar parameter $scalarIndex holds $width bytes, received ${bytes.size}"
                    )
                  )
                else {
                  arguments += KernelTask.Argument.Scalar(
                    KernelTask.zeroExtended(bytes, width).unsafe[NonEmpty[Vec[Byte]]]
                  )
                  loop(index = index + 1, allocationIndex = allocationIndex, scalarIndex = scalarIndex + 1)
                }
            }
          }
        loop(index = 0, allocationIndex = 0, scalarIndex = 0).flatMap { (allocationIndex, scalarIndex) =>
          if (allocationIndex < program.allocations.size)
            Left(
              SandboxFailure.Setup(
                program.language,
                s"kernel takes $allocationIndex allocation parameter(s), received ${program.allocations.size}"
              )
            )
          else if (scalarIndex < program.scalars.size)
            Left(
              SandboxFailure.Setup(
                program.language,
                s"kernel takes $scalarIndex scalar parameter(s), received ${program.scalars.size}"
              )
            )
          else Right((memory, arguments.result()))
        }
      }

  private def allocate(
      program: SandboxProgram,
      memory: ArrayMemory,
      index: Int
  ): Either[SandboxFailure, (AllocationId, Long > 0)] = {
    val initial = program.allocations.lift(index).getOrElse(Vec.empty[Byte])
    val size    = math.max(SandboxProgram.AllocationBytes.raw, initial.size.toLong).unsafe[Long > 0]
    val id      = memory.allocate(size)
    if (initial.isEmpty) Right((id, size))
    else
      memory.write(id, 0L, initial)
        .left.map(fault => SandboxFailure.Setup(program.language, fault.description.raw))
        .map(_ => (id, size))
  }
}
