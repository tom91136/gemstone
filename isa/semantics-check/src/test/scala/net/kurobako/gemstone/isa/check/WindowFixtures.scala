package net.kurobako.gemstone.isa.check

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.explorer.*
import net.kurobako.gemstone.isa.amdgpu.*

object WindowFixtures {
  val kernel = "vector_copy"

  private val loopSource =
    """s_mov_b32 s0, 0
      |v_mov_b32 v1, 0
      |loop:
      |v_add_f32 v1, v1, v2
      |v_mul_f32 v3, v1, v1
      |s_add_u32 s0, s0, 1
      |s_cmp_lt_u32 s0, 4
      |s_cbranch_scc1 loop
      |v_add_u32 v4, v3, v1
      |ds_add_u32 v4, v3
      |v_sub_u32 v5, v4, v3
      |global_store_dword v[6:7], v5, off
      |s_endpgm
      |""".stripMargin

  private val stateSource =
    """s_mov_b64 vcc, s[2:3]
      |v_mul_f32 v3, v1, v2
      |v_cndmask_b32 v4, v3, v1, vcc
      |v_cmp_lt_f32 vcc, v3, v4
      |v_add_co_u32 v5, vcc, v3, v4
      |v_add_u32 v7, v5, v3
      |s_mov_b64 exec, s[4:5]
      |v_add_u32 v6, v5, v4
      |s_cmp_lt_u32 s0, 4
      |s_cselect_b32 s1, 1, 0
      |s_endpgm
      |""".stripMargin

  val vectorCopy: Vec[Byte] = Hex.parse(codeObjects.vectorCopyGfx906).toOption.get

  def patched(source: String): Vec[Byte] = {
    val code = AMDGPUModel.assemble(source, Some("gfx906")).map(hex => Hex.parse(hex).toOption.get).toOption.get
    withCode(code)
  }

  def withCode(code: Vec[Byte]): Vec[Byte] =
    CodeObject.decode(vectorCopy).flatMap(_.replaceKernel(kernel.unsafe[NonEmpty[String]], code)).toOption.get.image

  val calibrated = "gfx1036"

  /** `search/fixtures.c` as `search/compile.sh` builds it for gfx1036. */
  lazy val compiled: Vec[Byte] = {
    val stream = getClass.getResourceAsStream("search/fixtures.gfx1036.hsaco")
    try Vec.from(stream.readAllBytes())
    finally stream.close()
  }

  /** The compiled object with `source` as its `window` kernel. */
  def assembled(source: String): Vec[Byte] = {
    val code = AMDGPUModel.assemble(source, Some(calibrated)).flatMap(Hex.parse(_)).toOption.get
    CodeObject.decode(compiled).flatMap(_.replaceKernel("window", code)).toOption.get.image
  }

  lazy val loop: Vec[Byte]  = patched(loopSource)
  lazy val state: Vec[Byte] = patched(stateSource)
}
