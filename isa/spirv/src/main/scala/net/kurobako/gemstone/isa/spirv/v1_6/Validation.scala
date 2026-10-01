package net.kurobako.gemstone.isa.spirv.v1_6

import cats.data.Chain
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.*

object Validation {
  enum Issue {
    case InvalidHeader(detail: NonEmpty[String])
    case MalformedOperand(opcode: NonEmpty[String], detail: NonEmpty[String])
    case MissingKernelCapability
    case MissingKernelEntryPoint
    case MissingOpenCLMemoryModel
    case MissingShaderCapability
    case MissingGraphicsEntryPoint
    case MissingGLComputeEntryPoint
    case MissingGLSL450MemoryModel
    case UnsupportedVersion(version: Version)

    def message: NonEmpty[String] = this match {
      case InvalidHeader(detail)            => detail
      case MalformedOperand(opcode, detail) => s"$opcode has a malformed operand: $detail"
      case MissingKernelCapability          => "OpCapability Kernel is required"
      case MissingKernelEntryPoint          => "an OpEntryPoint Kernel is required"
      case MissingOpenCLMemoryModel         => "an OpenCL physical-addressing OpMemoryModel is required"
      case MissingShaderCapability          => "OpCapability Shader is required"
      case MissingGraphicsEntryPoint        => "an OpEntryPoint Vertex or Fragment is required"
      case MissingGLComputeEntryPoint       => "an OpEntryPoint GLCompute is required"
      case MissingGLSL450MemoryModel        => "a Logical GLSL450 OpMemoryModel is required"
      case UnsupportedVersion(version)      =>
        s"SPIR-V ${version.major}.${version.minor} is newer than ${ISA.version.major}.${ISA.version.minor}"
    }
  }

  enum Environment {
    case OpenCLCompute
    case OpenCLLibrary
    case VulkanCompute
    case VulkanGraphics
  }

  private val Kernel = 6L

  private case class Requirements(
      capabilityWord: Long,
      entryPointWord: Long,
      acceptsMemoryModel: (Long, Long) => Boolean,
      missingCapability: Issue,
      missingEntryPoint: Issue,
      missingMemoryModel: Issue,
      requireEntryPoint: Boolean
  )

  private case class Facts(
      hasCapability: Boolean,
      hasEntryPoint: Boolean,
      hasMemoryModel: Boolean,
      issues: Chain[Issue]
  )
  private def issueWhen(condition: Boolean)(issue: => Issue): Chain[Issue] =
    if (condition) Chain.one(issue) else Chain.empty

  def validate(module: Module, environment: Environment): ValidationOutcome[Issue] = environment match {
    case Environment.OpenCLCompute  => validateEnvironment(module, openCL(requireEntryPoint = true))
    case Environment.OpenCLLibrary  => validateEnvironment(module, openCL(requireEntryPoint = false))
    case Environment.VulkanCompute  => validateEnvironment(module, VulkanRequirements)
    case Environment.VulkanGraphics => validateEnvironment(
        module,
        VulkanRequirements.copy(entryPointWord = 0L, missingEntryPoint = Issue.MissingGraphicsEntryPoint)
      )
  }

  private def openCL(requireEntryPoint: Boolean): Requirements = Requirements(
    capabilityWord = Kernel,
    entryPointWord = Kernel,
    acceptsMemoryModel = (addressing, memory) => (addressing == 1L || addressing == 2L) && memory >= 2L && memory <= 4L,
    missingCapability = Issue.MissingKernelCapability,
    missingEntryPoint = Issue.MissingKernelEntryPoint,
    missingMemoryModel = Issue.MissingOpenCLMemoryModel,
    requireEntryPoint = requireEntryPoint
  )

  private val VulkanRequirements = Requirements(
    capabilityWord = 1L,
    entryPointWord = 5L,
    acceptsMemoryModel = (addressing, memory) => addressing == 0L && memory == 1L,
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
      module.header.version.major > ISA.version.major ||
        (module.header.version.major == ISA.version.major &&
          module.header.version.minor > ISA.version.minor)
    )(Issue.UnsupportedVersion(module.header.version))
    val facts = module.instructions.foldLeft(Facts(false, false, false, Chain.empty)) {
      case (state, Instruction.Capability(capability)) => capability.singleWord match {
          case Right(word) if word.toUnsigned.toLong == requirements.capabilityWord => state.copy(hasCapability = true)
          case Right(_)                                                             => state
          case Left(detail)                                                         => state.copy(issues =
              state.issues.append(Issue.MalformedOperand("OpCapability", detail))
            )
        }
      case (state, Instruction.EntryPoint(executionModel, _, _, _)) => executionModel.singleWord match {
          case Right(word)
              if word.toUnsigned.toLong == requirements.entryPointWord ||
                (requirements.entryPointWord == 0L && word.toUnsigned.toLong == 4L) => state.copy(hasEntryPoint = true)
          case Right(_)     => state
          case Left(detail) => state.copy(issues =
              state.issues.append(Issue.MalformedOperand("OpEntryPoint", detail))
            )
        }
      case (state, Instruction.MemoryModel(addressingModel, memoryModel)) =>
        (addressingModel.singleWord, memoryModel.singleWord) match {
          case (Right(addressing), Right(memory)) =>
            state.copy(hasMemoryModel =
              state.hasMemoryModel ||
                requirements.acceptsMemoryModel(addressing.toUnsigned.toLong, memory.toUnsigned.toLong)
            )
          case (Left(detail), _) => state.copy(issues =
              state.issues.append(Issue.MalformedOperand("OpMemoryModel", detail))
            )
          case (_, Left(detail)) => state.copy(issues =
              state.issues.append(Issue.MalformedOperand("OpMemoryModel", detail))
            )
        }
      case (state, _) => state
    }
    val invalidIssues = Vec.from(
      (headerIssues ++ facts.issues ++
        issueWhen(!facts.hasCapability)(requirements.missingCapability) ++
        issueWhen(requirements.requireEntryPoint && !facts.hasEntryPoint)(requirements.missingEntryPoint) ++
        issueWhen(!facts.hasMemoryModel)(requirements.missingMemoryModel)).toVector.view
    )
    val unsupportedIssues = Vec.from(unsupported.toVector.view)
    if (invalidIssues.nonEmpty) ValidationOutcome.Rejected(invalidIssues.unsafe)
    else if (unsupportedIssues.nonEmpty) ValidationOutcome.Incomplete(unsupportedIssues.unsafe)
    else ValidationOutcome.Accepted
  }
}
