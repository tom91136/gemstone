package net.kurobako.gemstone.isa.spirv

import cats.data.Chain
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.*

object Validation {
  import Operand.*

  enum Issue {
    case InvalidHeader(detail: NonEmpty[String])
    case MissingKernelCapability
    case MissingKernelEntryPoint
    case MissingOpenCLMemoryModel
    case MissingShaderCapability
    case MissingGraphicsEntryPoint
    case MissingGLComputeEntryPoint
    case MissingGLSL450MemoryModel
    case UnsupportedVersion(version: Version)

    def message: NonEmpty[String] = this match {
      case InvalidHeader(detail)       => detail
      case MissingKernelCapability     => "OpCapability Kernel is required"
      case MissingKernelEntryPoint     => "an OpEntryPoint Kernel is required"
      case MissingOpenCLMemoryModel    => "an OpenCL physical-addressing OpMemoryModel is required"
      case MissingShaderCapability     => "OpCapability Shader is required"
      case MissingGraphicsEntryPoint   => "an OpEntryPoint Vertex or Fragment is required"
      case MissingGLComputeEntryPoint  => "an OpEntryPoint GLCompute is required"
      case MissingGLSL450MemoryModel   => "a Logical GLSL450 OpMemoryModel is required"
      case UnsupportedVersion(version) =>
        s"SPIR-V ${version.major}.${version.minor} is newer than ${Instruction.version.major}.${Instruction.version.minor}"
    }
  }

  enum Environment {
    case OpenCLCompute
    case OpenCLLibrary
    case VulkanCompute
    case VulkanGraphics
  }

  private case class Requirements(
      capability: Int,
      entryPoints: Set[Int],
      acceptsMemoryModel: (Long, Long) => Boolean,
      missingCapability: Issue,
      missingEntryPoint: Issue,
      missingMemoryModel: Issue,
      requireEntryPoint: Boolean
  )

  private def issueWhen(condition: Boolean)(issue: => Issue): Chain[Issue] =
    if (condition) Chain.one(issue) else Chain.empty

  def validate(module: Module, environment: Environment): ValidationOutcome[Issue] = environment match {
    case Environment.OpenCLCompute  => validateEnvironment(module, openCL(requireEntryPoint = true))
    case Environment.OpenCLLibrary  => validateEnvironment(module, openCL(requireEntryPoint = false))
    case Environment.VulkanCompute  => validateEnvironment(module, VulkanRequirements)
    case Environment.VulkanGraphics => validateEnvironment(
        module,
        VulkanRequirements.copy(
          entryPoints = Set(ExecutionModel.Vertex, ExecutionModel.Fragment),
          missingEntryPoint = Issue.MissingGraphicsEntryPoint
        )
      )
  }

  private def openCL(requireEntryPoint: Boolean): Requirements = Requirements(
    capability = Capability.Kernel,
    entryPoints = Set(ExecutionModel.Kernel),
    acceptsMemoryModel = (addressing, memory) =>
      (addressing == AddressingModel.Physical32 || addressing == AddressingModel.Physical64) &&
        (memory == MemoryModel.OpenCL || memory == MemoryModel.Vulkan),
    missingCapability = Issue.MissingKernelCapability,
    missingEntryPoint = Issue.MissingKernelEntryPoint,
    missingMemoryModel = Issue.MissingOpenCLMemoryModel,
    requireEntryPoint = requireEntryPoint
  )

  private val VulkanRequirements = Requirements(
    capability = Capability.Shader,
    entryPoints = Set(ExecutionModel.GLCompute),
    acceptsMemoryModel = (addressing, memory) => addressing == AddressingModel.Logical && memory == MemoryModel.GLSL450,
    missingCapability = Issue.MissingShaderCapability,
    missingEntryPoint = Issue.MissingGLComputeEntryPoint,
    missingMemoryModel = Issue.MissingGLSL450MemoryModel,
    requireEntryPoint = true
  )

  private def validateEnvironment(module: Module, requirements: Requirements): ValidationOutcome[Issue] = {
    val headerIssues =
      issueWhen(module.header.bound.toUnsigned.toLong == 0L)(Issue.InvalidHeader("id bound must be non-zero")) ++
        issueWhen(
          module.header.schema.toUnsigned.toLong != 0L
        )(Issue.InvalidHeader("reserved schema word must be zero"))
    val unsupported = Option.when(
      module.header.version.major > Instruction.version.major ||
        (module.header.version.major == Instruction.version.major &&
          module.header.version.minor > Instruction.version.minor)
    )(Issue.UnsupportedVersion(module.header.version))
    // decoded enumerant operands are single words
    val hasCapability = module.instructions.exists {
      case Instruction.Capability(capability) => unsigned(capability) == requirements.capability
      case _                                  => false
    }
    val hasEntryPoint = module.instructions.exists {
      case Instruction.EntryPoint(model, _, _, _) => requirements.entryPoints.contains(unsigned(model).toInt)
      case _                                      => false
    }
    val hasMemoryModel = module.instructions.exists {
      case Instruction.MemoryModel(addressing, memory) =>
        requirements.acceptsMemoryModel(unsigned(addressing), unsigned(memory))
      case _ => false
    }
    val invalidIssues = Vec.from(
      (headerIssues ++
        issueWhen(!hasCapability)(requirements.missingCapability) ++
        issueWhen(requirements.requireEntryPoint && !hasEntryPoint)(requirements.missingEntryPoint) ++
        issueWhen(!hasMemoryModel)(requirements.missingMemoryModel)).toVector.view
    )
    val unsupportedIssues = Vec.from(unsupported.toVector.view)
    if (invalidIssues.nonEmpty) ValidationOutcome.Rejected(invalidIssues.unsafe)
    else if (unsupportedIssues.nonEmpty) ValidationOutcome.Incomplete(unsupportedIssues.unsafe)
    else ValidationOutcome.Accepted
  }
}
