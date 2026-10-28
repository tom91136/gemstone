// Generated from the pinned Mesa ACO and AMD machine-readable ISA inputs. Regenerate with: sbt codegen
package net.kurobako.gemstone.isa.amdgpu

import javax.annotation.processing.Generated

import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.collection.*

@Generated(Array("sbt codegen"))
private object DualVectorInstructionDefs {
  case object VDualAddF32 extends Opcode("v_dual_add_f32", 1888)

  case object VDualAddF64 extends Opcode("v_dual_add_f64", 1889)

  case object VDualAddNcU32 extends Opcode("v_dual_add_nc_u32", 1890)

  case object VDualAndB32 extends Opcode("v_dual_and_b32", 1891)

  case object VDualAshrrevI32 extends Opcode("v_dual_ashrrev_i32", 1892)

  case object VDualBitop2B32 extends Opcode("v_dual_bitop2_b32", 1893)

  case object VDualCndmaskB32 extends Opcode("v_dual_cndmask_b32", 1894)

  case object VDualDot2accF32Bf16 extends Opcode("v_dual_dot2acc_f32_bf16", 1895)

  case object VDualDot2accF32F16 extends Opcode("v_dual_dot2acc_f32_f16", 1896)

  case object VDualFmaF32 extends Opcode("v_dual_fma_f32", 1897)

  case object VDualFmaF64 extends Opcode("v_dual_fma_f64", 1898)

  case object VDualFmaakF32 extends Opcode("v_dual_fmaak_f32", 1899)

  case object VDualFmacF32 extends Opcode("v_dual_fmac_f32", 1900)

  case object VDualFmamkF32 extends Opcode("v_dual_fmamk_f32", 1901)

  case object VDualLshlrevB32 extends Opcode("v_dual_lshlrev_b32", 1902)

  case object VDualLshrrevB32 extends Opcode("v_dual_lshrrev_b32", 1903)

  case object VDualMaxF32 extends Opcode("v_dual_max_f32", 1904)

  case object VDualMaxI32 extends Opcode("v_dual_max_i32", 1905)

  case object VDualMaxNumF32 extends Opcode("v_dual_max_num_f32", 1906)

  case object VDualMaxNumF64 extends Opcode("v_dual_max_num_f64", 1907)

  case object VDualMinF32 extends Opcode("v_dual_min_f32", 1908)

  case object VDualMinI32 extends Opcode("v_dual_min_i32", 1909)

  case object VDualMinNumF32 extends Opcode("v_dual_min_num_f32", 1910)

  case object VDualMinNumF64 extends Opcode("v_dual_min_num_f64", 1911)

  case object VDualMovB32 extends Opcode("v_dual_mov_b32", 1912)

  case object VDualMulDx9ZeroF32 extends Opcode("v_dual_mul_dx9_zero_f32", 1913)

  case object VDualMulF32 extends Opcode("v_dual_mul_f32", 1914)

  case object VDualMulF64 extends Opcode("v_dual_mul_f64", 1915)

  case object VDualSubF32 extends Opcode("v_dual_sub_f32", 1916)

  case object VDualSubNcU32 extends Opcode("v_dual_sub_nc_u32", 1917)

  case object VDualSubrevF32 extends Opcode("v_dual_subrev_f32", 1918)

  val definitions: Vec[Opcode] = Vec(
    VDualAddF32,
    VDualAddF64,
    VDualAddNcU32,
    VDualAndB32,
    VDualAshrrevI32,
    VDualBitop2B32,
    VDualCndmaskB32,
    VDualDot2accF32Bf16,
    VDualDot2accF32F16,
    VDualFmaF32,
    VDualFmaF64,
    VDualFmaakF32,
    VDualFmacF32,
    VDualFmamkF32,
    VDualLshlrevB32,
    VDualLshrrevB32,
    VDualMaxF32,
    VDualMaxI32,
    VDualMaxNumF32,
    VDualMaxNumF64,
    VDualMinF32,
    VDualMinI32,
    VDualMinNumF32,
    VDualMinNumF64,
    VDualMovB32,
    VDualMulDx9ZeroF32,
    VDualMulF32,
    VDualMulF64,
    VDualSubF32,
    VDualSubNcU32,
    VDualSubrevF32
  )
}
