package net.kurobako.gemstone.isa.spirv

import Operand.*
import VMProgram.*
import cats.syntax.all.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.graphics.ShaderMetadata.*
import net.kurobako.gemstone.isa.*
import net.kurobako.gemstone.vm.*
import scala.util.control.NonFatal

/** The Vulkan shader boundary. Native callers supply bytes and specialization values; all entry selection, lowering and
  * reflection use the same compiler as execution.
  */
object ShaderPreparation {
  def prepare(
      image: Vec[Byte],
      entry: NonEmpty[String],
      stage: ShaderStageKind,
      constants: Vec[SpecializationConstant]
  ): Either[Fault, PreparedShader] =
    try
      for {
        decoded  <- ISA.decode(image).left.map(error => Fault.InvalidRequest(error.message))
        selected <- select(ModuleNormalisation.expandDecorationGroups(decoded), entry, stage)
        module   <- Specialization.specialize(selected, constants)
        lowered = ShaderLowering.lower(module, stage)
        program <- VMProgram.load(
          lowered.module,
          if (stage == ShaderStageKind.Compute) Validation.Environment.VulkanCompute
          else Validation.Environment.VulkanGraphics
        )
        metadata <- VMShaderInterface.metadata(program, entry)
        bytes    <- ISA.encode(lowered.module).left.map(error => Fault.InvalidRequest(error.message))
        // This is the argument order used by execution, including expanded descriptor arrays.
        arguments = VMShaderInterface.argumentVariables(program, entry)
        descriptors <- descriptorsOf(program, arguments)
      } yield {
        val pushConstants = arguments.count(variable =>
          program.globals.get(variable).exists(_.storageClass == StorageClass.PushConstant)
        )
        require(pushConstants <= 1, "multiple push constant blocks")
        PreparedShader(
          bytes,
          entry,
          stage,
          descriptors,
          pushConstants == 1,
          ShaderReflection.interfaces(program, entry, stage),
          metadata.requiredWorkGroupSize.map(size => ShaderWorkgroup(size.x, size.y, size.z)),
          lowered.earlyFragmentTests,
          lowered.fragmentQuads
        )
      }
    catch {
      case NonFatal(error) => Left(
          Fault.InvalidRequest(
            s"shader preparation: ${Option(error.getMessage).getOrElse(error.getClass.getSimpleName)}"
          )
        )
    }

  private def descriptorsOf(program: VMProgram, arguments: Vec[Int]): Either[Fault, Vec[ShaderDescriptor]] = {
    val counts = scala.collection.mutable.Map.empty[Int, Int >= 0]
    arguments.flatMap(variable => program.descriptorBindings.get(variable).map(variable -> _)).traverse {
      case (variable, (set, binding)) =>
        val element = counts.getOrElse(variable, 0: Int >= 0)
        counts.update(variable, element + 1)
        program.globals.get(variable).flatMap(global => program.types.get(global.pointerType)) match {
          case Some(RuntimeType.Pointer(_, pointee)) =>
            Right(ShaderDescriptor(set, binding, element, resourceKind(program, pointee)))
          case _ => Left(Fault.InvalidRequest("shader preparation: descriptor variable is not a pointer"))
        }
    }
  }

  private def resourceKind(program: VMProgram, pointee: Int): ShaderResourceKind = {
    val resourceType = program.types.get(pointee) match {
      case Some(RuntimeType.Array(element, _)) => program.types.get(element)
      case other                               => other
    }
    resourceType match {
      case Some(RuntimeType.Sampler)         => ShaderResourceKind.Sampler
      case Some(RuntimeType.SampledImage(_)) => ShaderResourceKind.SampledImage
      case Some(
            _: RuntimeType.StorageImage | _: RuntimeType.Texture | RuntimeType.CubeTexture(_, _) |
            RuntimeType.InputAttachment(_, _) | RuntimeType.MultisampledTexture(_, _)
          ) => ShaderResourceKind.Image
      case _ => ShaderResourceKind.Buffer
    }
  }

  private def select(module: Module, entry: NonEmpty[String], stage: ShaderStageKind): Either[Fault, Module] = {
    val model = stage match {
      case ShaderStageKind.Vertex   => ExecutionModel.Vertex
      case ShaderStageKind.Fragment => ExecutionModel.Fragment
      case ShaderStageKind.Compute  => ExecutionModel.GLCompute
    }
    val matches = module.instructions.collect {
      case value @ Instruction.EntryPoint(executionModel, _, name, _)
          if unsigned(executionModel) == model && LiteralString.decode(name) == entry.raw => value
    }
    if (matches.size != 1)
      Left(Fault.InvalidRequest(s"expected one $stage entry point '${entry.raw}', found ${matches.size}"))
    else {
      val selected = matches.head
      val function = id(selected.entryPoint)
      Right(module.copy(instructions = module.instructions.filter {
        case value: Instruction.EntryPoint          => value == selected
        case Instruction.ExecutionMode(target, _)   => id(target) == function
        case Instruction.ExecutionModeId(target, _) => id(target) == function
        case _                                      => true
      }))
    }
  }
}
