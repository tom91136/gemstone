package net.kurobako.gemstone.isa.spirv.v1_6

import net.kurobako.gemstone.Vec
import net.kurobako.gemstone.isa.ValidationOutcome

import cats.data.Chain
import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

object Validation {
  enum Issue {
    case InvalidHeader(detail: String)
    case MalformedOperand(opcode: SPIRVOpcode, detail: String)
    case MissingKernelCapability
    case MissingKernelEntryPoint
    case MissingOpenCLMemoryModel
    case UnsupportedVersion(version: SPIRVVersion)

    def message: String = this match {
      case InvalidHeader(detail)            => detail
      case MalformedOperand(opcode, detail) => s"$opcode has a malformed operand: $detail"
      case MissingKernelCapability          => "OpCapability Kernel is required"
      case MissingKernelEntryPoint          => "an OpEntryPoint Kernel is required"
      case MissingOpenCLMemoryModel         => "an OpenCL physical-addressing OpMemoryModel is required"
      case UnsupportedVersion(version)      =>
        s"SPIR-V ${version.major}.${version.minor} is newer than ${ISA.version.major}.${ISA.version.minor}"
    }
  }

  enum Environment {
    case OpenCLCompute
    case OpenCLLibrary
  }

  private val Kernel = 6L

  private final case class Facts(
      kernelCapability: Boolean,
      kernelEntry: Boolean,
      openCLMemory: Boolean,
      issues: Chain[Issue]
  )
  private def issueWhen(condition: Boolean)(issue: => Issue): Chain[Issue] =
    if (condition) Chain.one(issue) else Chain.empty

  def validate(module: Module, environment: Environment): ValidationOutcome[Issue] = environment match {
    case Environment.OpenCLCompute => validateOpenCL(module, requireKernelEntry = true)
    case Environment.OpenCLLibrary => validateOpenCL(module, requireKernelEntry = false)
  }

  private def validateOpenCL(module: Module, requireKernelEntry: Boolean): ValidationOutcome[Issue] = {
    val headerIssues =
      issueWhen(module.header.bound.toUnsigned == 0L)(
        Issue.InvalidHeader("id bound must be non-zero")
      ) ++
        issueWhen(module.header.schema.toUnsigned != 0L)(
          Issue.InvalidHeader("reserved schema word must be zero")
        )
    val unsupported = Option.when(
      module.header.version.major > ISA.version.major ||
        (module.header.version.major == ISA.version.major && module.header.version.minor > ISA.version.minor)
    )(Issue.UnsupportedVersion(module.header.version))

    val facts = module.instructions.foldLeft(Facts(false, false, false, Chain.empty)) {
      case (state, Instruction.Capability(capability)) => capability.singleWord match {
          case Right(word) if word.toUnsigned == Kernel => state.copy(kernelCapability = true)
          case Right(_)                                 => state
          case Left(detail)                             => state.copy(issues =
              state.issues.append(
                Issue.MalformedOperand(SPIRVOpcode("OpCapability"), detail)
              )
            )
        }
      case (state, Instruction.EntryPoint(executionModel, _, _, _)) => executionModel.singleWord match {
          case Right(word) if word.toUnsigned == Kernel => state.copy(kernelEntry = true)
          case Right(_)                                 => state
          case Left(detail)                             => state.copy(issues =
              state.issues.append(
                Issue.MalformedOperand(SPIRVOpcode("OpEntryPoint"), detail)
              )
            )
        }
      case (state, Instruction.MemoryModel(addressingModel, memoryModel)) =>
        (addressingModel.singleWord, memoryModel.singleWord) match {
          case (Right(addressingWord), Right(memoryWord)) =>
            val addressing = addressingWord.toUnsigned
            val memory     = memoryWord.toUnsigned
            state.copy(openCLMemory =
              state.openCLMemory || ((addressing == 1L || addressing == 2L) && memory >= 2L && memory <= 4L)
            )
          case (Left(detail), _) => state.copy(issues =
              state.issues.append(
                Issue.MalformedOperand(SPIRVOpcode("OpMemoryModel"), detail)
              )
            )
          case (_, Left(detail)) => state.copy(issues =
              state.issues.append(
                Issue.MalformedOperand(SPIRVOpcode("OpMemoryModel"), detail)
              )
            )
        }
      case (state, _) => state
    }
    val invalidIssues = Vec.from(
      (headerIssues ++ facts.issues ++
        issueWhen(!facts.kernelCapability)(Issue.MissingKernelCapability) ++
        issueWhen(requireKernelEntry && !facts.kernelEntry)(Issue.MissingKernelEntryPoint) ++
        issueWhen(!facts.openCLMemory)(Issue.MissingOpenCLMemoryModel)).iterator
    )
    val unsupportedIssues = Vec.from(unsupported.iterator)
    if (invalidIssues.nonEmpty) ValidationOutcome.Rejected(invalidIssues.refineUnsafe[MinLength[1]])
    else if (unsupportedIssues.nonEmpty) ValidationOutcome.Incomplete(unsupportedIssues.refineUnsafe[MinLength[1]])
    else ValidationOutcome.Accepted
  }
}
