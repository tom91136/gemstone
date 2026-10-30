package net.kurobako.gemstone.explorer

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.testing.Suite

/** The patch-and-run path must link and run in the browser's Scala.js build, not only on the JVM. */
class SandboxJSSuite extends Suite {

  test("SASS assembles, patches and runs on the real VM in Scala.js") {
    val result = SandboxRunner
      .run(SandboxProgram(Language.SASS, "MOV R1, 0x2a; IADD3 R2, R1, 0x1, RZ; EXIT;", target = Some("sm_50")))
      .toOption
      .get
    assertEquals(result.failure, None)
    assert(result.complete)
    assertEquals(result.pcs.distinct, Vec(8L, 16L, 24L))
    assertEquals(result.registers.find(_.name == "R2").map(_.bits), Some(43L))
  }

  test("AMDGPU assembles, patches and runs on the real VM in Scala.js") {
    val result = SandboxRunner
      .run(SandboxProgram(
        Language.AMDGPU,
        "s_mov_b32 s10, 0x2a\ns_add_u32 s11, s10, 1\ns_endpgm",
        target = Some("gfx900")
      ))
      .toOption
      .get
    assertEquals(result.failure, None)
    assert(result.complete)
    assertEquals(result.pcs, Vec(0L, 4L, 8L))
    assertEquals(result.registers.find(_.name == "s11").map(_.bits), Some(43L))
  }

  test("SPIR-V assembles, loads and runs through the sandbox in Scala.js") {
    val result = SandboxRunner
      .run(SandboxProgram(
        Language.SPIRV,
        SandboxSPIRVModules.store,
        kernel = Some("store"),
        allocations = Vec(Vec.empty[Byte])
      ))
      .toOption
      .get
    assertEquals(result.failure, None)
    assert(result.complete)
    assert(result.spirv.nonEmpty)
    assert(result.spirvSteps.nonEmpty)
    assertEquals(LittleEndian.decode(result.bytes(0), 0, 4), 42L)
  }
}
