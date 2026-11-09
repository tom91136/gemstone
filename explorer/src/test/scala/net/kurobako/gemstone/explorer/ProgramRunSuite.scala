package net.kurobako.gemstone.explorer

import net.kurobako.gemstone.profile.*
import net.kurobako.gemstone.testing.*

class ProgramRunSuite extends Suite {

  test("AMDGPU launches default to gfx1036") {
    assertEquals(ProgramRun.defaultArchitecture(Language.AMDGPU), Some(Architecture.gfx1036))
  }

  test("PTX and SASS launches default to the probed sm_89") {
    assertEquals(ProgramRun.defaultArchitecture(Language.PTX), Some(Architecture.sm89))
    assertEquals(ProgramRun.defaultArchitecture(Language.SASS), Some(Architecture.sm89))
  }

  test("SPIR-V launches have no default architecture") {
    assertEquals(ProgramRun.defaultArchitecture(Language.SPIRV), None)
  }
}
