package net.kurobako.gemstone.isa.spirv.v1_6

enum ValidationIssue {
  case InvalidHeader(detail: String)
  case MissingKernelCapability
  case MissingKernelEntryPoint
  case MissingOpenClMemoryModel
  case UnsupportedVersion(version: SpirvVersion)

  def message: String = this match {
    case InvalidHeader(detail)       => detail
    case MissingKernelCapability     => "OpCapability Kernel is required"
    case MissingKernelEntryPoint     => "an OpEntryPoint Kernel is required"
    case MissingOpenClMemoryModel    => "an OpenCL physical-addressing OpMemoryModel is required"
    case UnsupportedVersion(version) =>
      s"SPIR-V ${version.major}.${version.minor} is newer than ${Version.major}.${Version.minor}"
  }
}

enum ValidationResult {
  case Valid
  case Invalid(issues: Vector[ValidationIssue])
  case Unsupported(issues: Vector[ValidationIssue])
}

object ComputeValidator {
  private val Kernel = 6L

  def validate(module: Module): ValidationResult = {
    val invalid     = Vector.newBuilder[ValidationIssue]
    val unsupported = Vector.newBuilder[ValidationIssue]
    if (module.header.bound.toUnsigned == 0L)
      invalid += ValidationIssue.InvalidHeader("id bound must be non-zero")
    if (module.header.schema.toUnsigned != 0L)
      invalid += ValidationIssue.InvalidHeader("reserved schema word must be zero")
    if (
      module.header.version.major > Version.major ||
      (module.header.version.major == Version.major && module.header.version.minor > Version.minor)
    ) unsupported += ValidationIssue.UnsupportedVersion(module.header.version)

    var kernelCapability = false
    var kernelEntry      = false
    var openClMemory     = false
    module.instructions.foreach {
      case Instruction.Capability(capability) if capability.toUnsigned == Kernel => kernelCapability = true
      case Instruction.EntryPoint(executionModel, _, _, _) if executionModel.toUnsigned == Kernel => kernelEntry = true
      case Instruction.MemoryModel(addressingModel, memoryModel)                                  =>
        val addressing = addressingModel.toUnsigned
        val memory     = memoryModel.toUnsigned
        if ((addressing == 1L || addressing == 2L) && memory >= 2L && memory <= 4L) openClMemory = true
      case _ => ()
    }
    if (!kernelCapability) invalid += ValidationIssue.MissingKernelCapability
    if (!kernelEntry) invalid += ValidationIssue.MissingKernelEntryPoint
    if (!openClMemory) invalid += ValidationIssue.MissingOpenClMemoryModel
    val invalidIssues     = invalid.result()
    val unsupportedIssues = unsupported.result()
    if (invalidIssues.nonEmpty) ValidationResult.Invalid(invalidIssues)
    else if (unsupportedIssues.nonEmpty) ValidationResult.Unsupported(unsupportedIssues)
    else ValidationResult.Valid
  }
}
