package net.kurobako.gemstone.isa.amdgpu

import PackedVectorInstructionDefs.*
import VOP1InstructionDefs.*
import VOP2InstructionDefs.*
import net.kurobako.gemstone.isa.amdgpu.Instruction as AMDGPUInstruction

/** GFX9 VOP3 opcode aliases the pinned ACO and AMD XML inputs do not carry.
  *
  * GFX9 encodes VOP1/VOP2 operations through the VOP3 opcode space (opcode 0x100 + the compact opcode, or 0x380 + the
  * VOP3P opcode for packed operations), but ACO only records the compact form. VOPC compares resolve through the
  * decoder's VOPC fallback and native VOP3 opcodes come from the generated metadata, so only the aliases that neither
  * source covers remain here. This is reviewed encoding data, not logic.
  */
private object Gfx9Vop3Aliases {
  private type VendorInstruction = AMDGPUInstruction.Definition

  val byOpcode: Map[Int, VendorInstruction] = Map(
    256 -> VCndmaskB32,
    257 -> VAddF32,
    258 -> VSubF32,
    259 -> VSubrevF32,
    260 -> VMulLegacyF32,
    261 -> VMulF32,
    266 -> VMinF32,
    267 -> VMaxF32,
    272 -> VLshrrevB32,
    274 -> VLshlrevB32,
    281 -> VAddCoU32,
    282 -> VSubCoU32,
    283 -> VSubrevCoU32,
    284 -> VAddcCoU32,
    285 -> VSubbCoU32,
    286 -> VSubbrevCoU32,
    298 -> VLshlrevB16,
    308 -> VAddU32,
    309 -> VSubU32,
    354 -> VRcpF32,
    369 -> VFrexpMantF64,
    897 -> VPkMulLoU16,
    899 -> VPkSubI16,
    901 -> VPkLshrrevB16,
    906 -> VPkAddU16,
    910 -> VPkFmaF16,
    911 -> VPkAddF16,
    912 -> VPkMulF16,
    913 -> VPkMinF16,
    914 -> VPkMaxF16
  )
}
