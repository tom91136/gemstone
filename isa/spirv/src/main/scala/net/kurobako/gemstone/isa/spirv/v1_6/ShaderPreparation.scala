package net.kurobako.gemstone.isa.spirv.v1_6

import Operand.*
import VMIR.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.graphics.ShaderMetadata.*
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
      constants: Map[Binary32, Vec[Byte]]
  ): Either[Fault, PreparedShader] =
    try
      for {
        decoded     <- ISA.decode(image).left.map(error => Fault.InvalidTask(error.message))
        selected    <- select(ModuleNormalization.expandDecorationGroups(decoded), entry, stage)
        normalized  <- ISA.encode(selected).left.map(error => Fault.InvalidTask(error.message))
        specialized <- Specialization.specialize(normalized, constants).left.map(detail =>
          Fault.InvalidTask(s"specialization: $detail")
        )
        module <- ISA.decode(specialized).left.map(error => Fault.InvalidTask(error.message))
        lowered = ShaderLowering.lower(module, stage)
        ir <- VMCompiler.compile(
          lowered.module,
          if (stage == ShaderStageKind.Compute)
            Validation.Environment.VulkanCompute
          else Validation.Environment.VulkanGraphics
        )
        program = VMProgramRuntime(ir)
        metadata <- VMShaderInterface.metadata(program, entry)
        bytes    <- ISA.encode(lowered.module).left.map(error => Fault.InvalidTask(error.message))
      } yield {
        // This is the argument order used by execution, including expanded descriptor arrays.
        val arguments   = VMShaderInterface.argumentVariables(program, entry)
        val counts      = scala.collection.mutable.Map.empty[Int, Int >= 0]
        val descriptors = arguments.filter(program.descriptorBindings.contains(_)).map { variable =>
          val (set, binding) = program.descriptorBindings(variable)
          val element        = counts.getOrElse(variable, 0: Int >= 0)
          counts.update(variable, element + 1)
          val pointee = program.types(program.globals(variable).pointerType) match {
            case RuntimeType.Pointer(_, value) => value
            case _                             => throw IllegalArgumentException("descriptor variable is not a pointer")
          }
          val resourceType = program.types(pointee) match {
            case RuntimeType.Array(value, _) => program.types(value)
            case value                       => value
          }
          val resource = resourceType match {
            case RuntimeType.Sampler         => ShaderResourceKind.Sampler
            case RuntimeType.SampledImage(_) => ShaderResourceKind.SampledImage
            case _: RuntimeType.StorageImage | _: RuntimeType.Texture | _: RuntimeType.CubeTexture |
                _: RuntimeType.InputAttachment | _: RuntimeType.MultisampledTexture => ShaderResourceKind.Image
            case _ => ShaderResourceKind.Buffer
          }
          ShaderDescriptor(
            set,
            binding,
            element,
            resource
          )
        }
        val pushConstants =
          arguments.count(variable => program.globals(variable).storageClass == StorageClass.PushConstant)
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
          Fault.InvalidTask(s"shader preparation: ${Option(error.getMessage).getOrElse(error.getClass.getSimpleName)}")
        )
    }

  private def select(module: Module, entry: NonEmpty[String], stage: ShaderStageKind): Either[Fault, Module] = {
    val model = stage match {
      case ShaderStageKind.Vertex   => ExecutionModel.Vertex
      case ShaderStageKind.Fragment => ExecutionModel.Fragment
      case ShaderStageKind.Compute  => ExecutionModel.GLCompute
    }
    val matches = module.instructions.collect {
      case value @ Instruction.EntryPoint(executionModel, _, name, _)
          if unsigned(executionModel) == model && literalString(name) == entry.raw => value
    }
    if (matches.size != 1)
      Left(Fault.InvalidTask(s"expected one $stage entry point '${entry.raw}', found ${matches.size}"))
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

  private def literalString(value: Operand): String = {
    val bytes = value.words.toVector.flatMap(word =>
      (0 until 4).map(shift => (word.raw >>> (shift * 8)).toByte)
    ).takeWhile(_ != 0)
    String(bytes.toArray, java.nio.charset.StandardCharsets.UTF_8)
  }
}
