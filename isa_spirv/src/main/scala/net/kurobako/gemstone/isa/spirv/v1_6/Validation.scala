package net.kurobako.gemstone.isa.spirv.v1_6

enum ValidationIssue {
  case InvalidHeader(detail: String)
  case UnknownOpcode(opcode: Opcode)
  case TooFewOperands(instruction: String, expected: Int, actual: Int)
  case MissingKernelCapability
  case MissingKernelEntryPoint
  case MissingOpenClMemoryModel
  case UnsupportedVersion(version: Version)

  def message: String = this match {
    case InvalidHeader(detail)                         => detail
    case UnknownOpcode(opcode)                         => s"unknown opcode ${opcode.number}"
    case TooFewOperands(instruction, expected, actual) =>
      s"$instruction needs at least $expected operands, found $actual"
    case MissingKernelCapability     => "OpCapability Kernel is required"
    case MissingKernelEntryPoint     => "an OpEntryPoint Kernel is required"
    case MissingOpenClMemoryModel    => "an OpenCL physical-addressing OpMemoryModel is required"
    case UnsupportedVersion(version) => s"SPIR-V ${version.major}.${version.minor} is newer than 1.6"
  }
}

enum ValidationResult {
  case Valid
  case Invalid(issues: Vector[ValidationIssue])
  case Unsupported(issues: Vector[ValidationIssue])
}

object ComputeValidator {
  private val OpMemoryModel = 14
  private val OpEntryPoint  = 15
  private val OpCapability  = 17
  private val Kernel        = 6

  def validate(module: Module): ValidationResult = {
    val invalid     = Vector.newBuilder[ValidationIssue]
    val unsupported = Vector.newBuilder[ValidationIssue]
    if (module.header.bound.toUnsigned == 0L)
      invalid += ValidationIssue.InvalidHeader("id bound must be non-zero")
    if (module.header.schema.toUnsigned != 0L)
      invalid += ValidationIssue.InvalidHeader("reserved schema word must be zero")
    if (module.header.version.major > 1 || (module.header.version.major == 1 && module.header.version.minor > 6))
      unsupported += ValidationIssue.UnsupportedVersion(module.header.version)

    var kernelCapability = false
    var kernelEntry      = false
    var openClMemory     = false
    module.instructions.foreach { instruction =>
      InstructionCatalog.byOpcode.get(instruction.opcode) match {
        case None => unsupported += ValidationIssue.UnknownOpcode(instruction.opcode)
        case Some(info) if instruction.operands.size < info.minimumOperandCount =>
          invalid += ValidationIssue.TooFewOperands(info.name, info.minimumOperandCount, instruction.operands.size)
        case Some(_) => ()
      }
      instruction.opcode.number match {
        case OpCapability if instruction.operands.headOption.exists(_.toUnsigned == Kernel.toLong) =>
          kernelCapability = true
        case OpEntryPoint if instruction.operands.headOption.exists(_.toUnsigned == Kernel.toLong) => kernelEntry = true
        case OpMemoryModel if instruction.operands.size >= 2                                       =>
          val addressing = instruction.operands(0).toUnsigned
          val memory     = instruction.operands(1).toUnsigned
          if ((addressing == 1L || addressing == 2L) && memory >= 2L && memory <= 4L) openClMemory = true
        case _ => ()
      }
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
