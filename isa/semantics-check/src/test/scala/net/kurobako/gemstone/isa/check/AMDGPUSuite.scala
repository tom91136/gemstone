package net.kurobako.gemstone.isa.check

import net.kurobako.gemstone.isa.amdgpu.*

/** Checks AMDGPU definitions against one active lane of a complete wave on the VM. */
abstract class AMDGPUSuite extends DifferentialSuite[AMDGPUHarness, Lookup.Spellings](AMDGPUReference)
