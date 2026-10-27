package net.kurobako.gemstone.explorer

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.string.*
import net.kurobako.gemstone.vm.LaunchGeometry.*

/** The explorer-side registry of machine sandbox recipes. The built-ins cover the control and bulk-copy snippets the
  * catalogue already names.
  */
object SandboxRecipes {

  private val Builtin: Map[(Language, String), SandboxRecipe] = Map(
    (Language.PTX, "bra") -> SandboxRecipe(
      "  mov.u32 %r0, 5;\n  bra SKIP;\n  mov.u32 %r1, 99;\nSKIP:\n  add.u32 %r2, %r0, 7;",
      "The branch skips the second move; the PC evidence shows the jump and the instruction it lands on."
    ),
    (Language.PTX, "cp.async.bulk") -> SandboxRecipe(
      """  .shared .align 16 .b8 buffer[16];
        |  .shared .align 8 .b64 bar;
        |  .reg .u64 %in, %out, %token;
        |  .reg .u32 %value;
        |  .reg .pred %done;
        |  ld.param.u64 %in, [in];
        |  ld.param.u64 %out, [out];
        |  mbarrier.init.shared::cta.b64 [bar], 1;
        |  cp.async.bulk.shared::cta.global.mbarrier::complete_tx::bytes [buffer], [%in], 16, [bar];
        |  mbarrier.arrive.expect_tx.shared::cta.b64 %token, [bar], 16;
        |WAIT:
        |  mbarrier.try_wait.parity.shared::cta.b64 %done, [bar], 0;
        |  @!%done bra WAIT;
        |  ld.shared.u32 %value, [buffer];
        |  st.global.u32 [%out], %value;
        |""".stripMargin,
      "The bulk copy reads 16 bytes through the mbarrier; the output allocation receives the first copied word.",
      target = Some("sm_90"),
      parameters = Vec(
        SandboxProgram.Parameter("in", SandboxProgram.Parameter.Kind.Pointer),
        SandboxProgram.Parameter("out", SandboxProgram.Parameter.Kind.Pointer)
      ),
      allocations = Vec(
        Vec[Byte](1, 0, 0, 0, 2, 0, 0, 0, 3, 0, 0, 0, 4, 0, 0, 0),
        Vec.empty[Byte]
      )
    ),
    (Language.SASS, "bra") -> SandboxRecipe(
      "MOV R1, 0x2a; BRA SKIP; MOV R2, 0x1; SKIP: IADD3 R3, R1, 0x1, RZ; EXIT;",
      "BRA skips the second MOV; R2 stays zero while R3 is computed after the label.",
      target = Some("sm_89")
    ),
    (Language.SASS, "exit") -> SandboxRecipe(
      "MOV R1, 0x2a; EXIT;",
      "EXIT retires the unit; the final register evidence holds R1.",
      target = Some("sm_50")
    ),
    (Language.SASS, "stg") -> SandboxRecipe(
      "MOV R2, c[0][0x160]; MOV R3, c[0][0x164]; MOV R4, 0x2a; STG.E [R2.64], R4; EXIT;",
      "STG stores 42 through the template's first allocation pointer; the memory evidence shows the changed bytes.",
      target = Some("sm_89"),
      allocations = Vec(Vec.empty[Byte])
    ),
    (Language.AMDGPU, "s_barrier") -> SandboxRecipe(
      "s_barrier\ns_endpgm",
      "The wave arrives at the workgroup barrier and retires; the timeline records the block/resume.",
      target = Some("gfx900")
    ),
    (Language.AMDGPU, "s_branch") -> SandboxRecipe(
      "s_mov_b32 s10, 5\ns_branch SKIP\ns_mov_b32 s11, 99\nSKIP:\ns_add_u32 s12, s10, 7\ns_endpgm",
      "s_branch skips the second move; s11 stays zero and s12 is computed after the label.",
      target = Some("gfx900")
    ),
    (Language.AMDGPU, "s_endpgm") -> SandboxRecipe(
      "s_mov_b32 s10, 0x2a\ns_endpgm",
      "s_endpgm retires the wave; the final register evidence holds s10.",
      target = Some("gfx900")
    ),
    (Language.AMDGPU, "global_store_dword") -> SandboxRecipe(
      """s_load_dwordx2 s[6:7], s[4:5], 0x0
        |s_mov_b32 s8, 0x2a
        |v_mov_b32 v0, s8
        |v_mov_b32 v1, 0
        |global_store_dword v1, v0, s[6:7]
        |s_endpgm""".stripMargin,
      "global_store_dword writes 42 into the first allocation; the memory evidence shows the changed bytes.",
      target = Some("gfx900"),
      allocations = Vec(Vec.empty[Byte])
    ),
    (Language.SPIRV, "opbranchconditional") -> SandboxRecipe(
      """OpCapability Addresses
        |OpCapability Kernel
        |OpMemoryModel Physical64 OpenCL
        |OpEntryPoint Kernel %main "branch"
        |%void = OpTypeVoid
        |%uint = OpTypeInt 32 0
        |%bool = OpTypeBool
        |%uint_ptr = OpTypePointer CrossWorkgroup %uint
        |%fn = OpTypeFunction %void %uint_ptr
        |%helper_fn = OpTypeFunction %uint %uint
        |%false = OpConstantFalse %bool
        |%true = OpConstantTrue %bool
        |%zero = OpConstant %uint 0
        |%one = OpConstant %uint 1
        |%ten = OpConstant %uint 10
        |%main = OpFunction %void None %fn
        |%out = OpFunctionParameter %uint_ptr
        |%entry = OpLabel
        |OpSelectionMerge %merge None
        |OpBranchConditional %true %then %else
        |%then = OpLabel
        |OpBranch %merge
        |%else = OpLabel
        |OpBranch %merge
        |%merge = OpLabel
        |%value = OpPhi %uint %ten %then %one %else
        |%called = OpFunctionCall %uint %helper %value
        |OpStore %out %called
        |OpReturn
        |OpFunctionEnd
        |%helper = OpFunction %uint None %helper_fn
        |%x = OpFunctionParameter %uint
        |%helper_entry = OpLabel
        |%sum = OpIAdd %uint %x %ten
        |OpReturnValue %sum
        |OpFunctionEnd
        |""".stripMargin,
      "The branch takes the true arm; OpPhi selects 10, the helper adds 10, and the result 20 reaches memory.",
      allocations = Vec(Vec.empty[Byte])
    ),
    (Language.SPIRV, "opcontrolbarrier") -> SandboxRecipe(
      """OpCapability Addresses
        |OpCapability Kernel
        |OpMemoryModel Physical64 OpenCL
        |OpEntryPoint Kernel %main "barrier"
        |OpExecutionMode %main LocalSize 4 1 1
        |%void = OpTypeVoid
        |%uint = OpTypeInt 32 0
        |%uint_ptr = OpTypePointer CrossWorkgroup %uint
        |%fn = OpTypeFunction %void %uint_ptr
        |%workgroup = OpConstant %uint 2
        |%semantics = OpConstant %uint 256
        |%one = OpConstant %uint 1
        |%main = OpFunction %void None %fn
        |%out = OpFunctionParameter %uint_ptr
        |%entry = OpLabel
        |OpControlBarrier %workgroup %workgroup %semantics
        |OpStore %out %one
        |OpReturn
        |OpFunctionEnd
        |""".stripMargin,
      "Four invocations park at the workgroup barrier, resume together, and all store 1.",
      allocations = Vec(Vec.empty[Byte]),
      group = Dimensions(4, 1, 1)
    ),
    (Language.SPIRV, "opnop") -> SandboxRecipe(
      """OpCapability Shader
        |OpMemoryModel Logical GLSL450
        |OpEntryPoint GLCompute %main "main"
        |OpExecutionMode %main LocalSize 1 1 1
        |%void = OpTypeVoid
        |%fn = OpTypeFunction %void
        |%main = OpFunction %void None %fn
        |%entry = OpLabel
        |OpNop
        |OpReturn
        |OpFunctionEnd
        |""".stripMargin,
      "The module loads and runs; OpNop advances the PC and the function returns."
    )
  )

  /** The recipe for `name`, when one is registered. Names are compared case-insensitively. */
  def find(language: Language, name: String): Option[SandboxRecipe] =
    Builtin.get((language, key(name)))

  private def key(name: String): String = name.trim.toLowerCaseRoot
}
