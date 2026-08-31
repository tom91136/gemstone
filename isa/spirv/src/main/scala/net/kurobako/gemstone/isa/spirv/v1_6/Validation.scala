package net.kurobako.gemstone.isa.spirv.v1_6

import net.kurobako.gemstone.Col
import net.kurobako.gemstone.isa.ValidationResult

import cats.data.Chain

enum ValidationIssue {
  case InvalidHeader(detail: String)
  case MissingKernelCapability
  case MissingKernelEntryPoint
  case MissingOpenCLMemoryModel
  case UnsupportedVersion(version: SPIRVVersion)

  def message: String = this match {
    case InvalidHeader(detail)       => detail
    case MissingKernelCapability     => "OpCapability Kernel is required"
    case MissingKernelEntryPoint     => "an OpEntryPoint Kernel is required"
    case MissingOpenCLMemoryModel    => "an OpenCL physical-addressing OpMemoryModel is required"
    case UnsupportedVersion(version) =>
      s"SPIR-V ${version.major}.${version.minor} is newer than ${ISA.version.major}.${ISA.version.minor}"
  }
}

enum Environment {
  case OpenCLCompute
  case OpenCLLibrary
}

object Validator {
  private val Kernel = 6L

  private final case class Facts(kernelCapability: Boolean, kernelEntry: Boolean, openCLMemory: Boolean)
  private def issueWhen(condition: Boolean)(issue: => ValidationIssue): Chain[ValidationIssue] =
    if (condition) Chain.one(issue) else Chain.empty

  def validate(module: Module, environment: Environment): ValidationResult[ValidationIssue] = environment match {
    case Environment.OpenCLCompute => validateOpenCL(module, requireKernelEntry = true)
    case Environment.OpenCLLibrary => validateOpenCL(module, requireKernelEntry = false)
  }

  private def validateOpenCL(module: Module, requireKernelEntry: Boolean): ValidationResult[ValidationIssue] = {
    val headerIssues =
      issueWhen(module.header.bound.toUnsigned == 0L)(
        ValidationIssue.InvalidHeader("id bound must be non-zero")
      ) ++
        issueWhen(module.header.schema.toUnsigned != 0L)(
          ValidationIssue.InvalidHeader("reserved schema word must be zero")
        )
    val unsupported = Option.when(
      module.header.version.major > ISA.version.major ||
        (module.header.version.major == ISA.version.major && module.header.version.minor > ISA.version.minor)
    )(ValidationIssue.UnsupportedVersion(module.header.version))

    val facts = module.instructions.foldLeft(Facts(false, false, false)) {
      case (state, Instruction.Capability(capability)) if capability.toUnsigned == Kernel =>
        state.copy(kernelCapability = true)
      case (state, Instruction.EntryPoint(executionModel, _, _, _)) if executionModel.toUnsigned == Kernel =>
        state.copy(kernelEntry = true)
      case (state, Instruction.MemoryModel(addressingModel, memoryModel)) =>
        val addressing = addressingModel.toUnsigned
        val memory     = memoryModel.toUnsigned
        state.copy(openCLMemory =
          state.openCLMemory || ((addressing == 1L || addressing == 2L) && memory >= 2L && memory <= 4L)
        )
      case (state, _) => state
    }
    val invalidIssues = Col.from(
      (headerIssues ++
        issueWhen(!facts.kernelCapability)(ValidationIssue.MissingKernelCapability) ++
        issueWhen(requireKernelEntry && !facts.kernelEntry)(ValidationIssue.MissingKernelEntryPoint) ++
        issueWhen(!facts.openCLMemory)(ValidationIssue.MissingOpenCLMemoryModel)).iterator
    )
    val unsupportedIssues = Col.from(unsupported.iterator)
    if (invalidIssues.nonEmpty) ValidationResult.Invalid(invalidIssues)
    else if (unsupportedIssues.nonEmpty) ValidationResult.Unsupported(unsupportedIssues)
    else ValidationResult.Valid
  }
}
