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

  /** `search/hidden.cl` as `search/compile.sh` builds it: `hidden_args` takes two buffers and a count, and the compiler
    * adds its hidden arguments after them.
    */
  lazy val hidden: Vec[Byte] = {
    val stream = getClass.getResourceAsStream("search/hidden.gfx1036.hsaco")
    try Vec.from(stream.readAllBytes())
    finally stream.close()
  }

  /** `tools/benchmarks/fixtures/cloverleaf-flux.cu` as `search/compile.sh` builds it for gfx1036. */
  lazy val flux: Vec[Byte] = {
    val stream = getClass.getResourceAsStream("search/flux.gfx1036.hsaco")
    try Vec.from(stream.readAllBytes())
    finally stream.close()
  }

  /** The compiled object with `source` as its `kernel`, which keeps the parameters the object gives that kernel. */
  def assembled(source: String, kernel: NonEmpty[String] = "window"): Vec[Byte] = {
    val code = AMDGPUModel.assemble(source, Some(calibrated)).flatMap(Hex.parse(_)).toOption.get
    CodeObject.decode(compiled).flatMap(_.replaceKernel(kernel, code)).toOption.get.image
  }

  val streaming: NonEmpty[String] = "square_plus_one"

  /** A loop over the parameters of `square_plus_one` that loads an element, stores a result, and moves a value no
    * instruction reads.
    */
  lazy val stream: Vec[Byte] = streamOf("v_mov_b32 v7, v5\n")

  /** [[stream]] with `dead` after its last multiply-add, storing `stored`. */
  def streamOf(dead: String, stored: String = "v6"): Vec[Byte] = assembled(
    s"""s_load_dword s8, s[4:5], 0x10
      |s_load_dwordx4 s[0:3], s[4:5], 0x0
      |v_mov_b32 v0, 0
      |s_waitcnt lgkmcnt(0)
      |loop:
      |s_add_u32 s0, s0, 4
      |s_addc_u32 s1, s1, 0
      |s_load_dword s9, s[0:1], 0x0
      |s_waitcnt lgkmcnt(0)
      |v_fma_f32 v6, v6, s9, s6
      |v_fma_f32 v5, v5, s9, s6
      |${dead}global_store_dword v0, $stored, s[2:3]
      |s_add_i32 s8, s8, -1
      |s_cmp_lg_u32 s8, 0
      |s_cbranch_scc1 loop
      |s_endpgm
      |""".stripMargin,
    streaming
  )

  /** A loop of `trips` iterations that skips its second block on the iteration that leaves `skipped` to go. */
  def branching(trips: Int, skipped: Int): Vec[Byte] = assembled(
    s"""s_mov_b32 s8, $trips
       |loop:
       |s_cmp_eq_u32 s8, $skipped
       |s_cbranch_scc1 join
       |v_fma_f32 v6, v6, s5, s6
       |join:
       |s_add_i32 s8, s8, -1
       |s_cmp_lg_u32 s8, 0
       |s_cbranch_scc1 loop
       |s_endpgm
       |""".stripMargin
  )

  lazy val loop: Vec[Byte]  = patched(loopSource)
  lazy val state: Vec[Byte] = patched(stateSource)
}
