package net.kurobako.gemstone.explorer

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.vm.LaunchGeometry.*

object SandboxProgram {

  /** How large a sandbox allocation starts, before initial bytes grow it. */
  val AllocationBytes: Long > 0 = (64L * 1024L).unsafe[Long > 0]

  object Parameter {
    enum Kind derives CanEqual {
      case Pointer
      case Scalar(bits: Int = 32)
    }
  }

  /** A parameter a synthesised PTX entry point declares. SASS and AMDGPU templates carry their own parameters. */
  case class Parameter(name: String, kind: Parameter.Kind) derives CanEqual
}

/** A small program for the instruction sandbox. SASS and AMDGPU assemble `instructions` and patch the template's
  * kernel; PTX wraps `instructions` in a complete entry point and `parameters` declare its signature; SPIR-V reads
  * `instructions` as a complete module in the in-repo assembler's syntax, with `kernel` naming its entry point (the
  * first `OpEntryPoint` when omitted). `target` selects the SPIR-V validation environment (`opencl` or `vulkan`), while
  * `allocations` holds initial bytes for allocation parameters in order and `scalars` holds bytes for scalar
  * parameters. Allocations the caller omits start zeroed. The sandbox does not emulate: assembly, patching, loading or
  * launch failures are returned as [[SandboxFailure]] so the explorer can fall back to the Program page.
  */
case class SandboxProgram(
    language: Language,
    instructions: String,
    target: Option[String] = None,
    kernel: Option[String] = None,
    parameters: Vec[SandboxProgram.Parameter] = Vec.empty,
    allocations: Vec[Vec[Byte]] = Vec.empty,
    scalars: Vec[Vec[Byte]] = Vec.empty,
    group: Dimensions = Dimensions.One,
    count: Dimensions = Dimensions.One
)
