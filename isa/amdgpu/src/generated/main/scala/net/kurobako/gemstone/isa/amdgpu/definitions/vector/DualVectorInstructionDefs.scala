// Generated from the pinned Mesa ACO and AMD machine-readable ISA inputs. Regenerate with: sbt codegen
package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.collection.*
import Instruction.*

@javax.annotation.processing.Generated(Array("sbt codegen"))
private object DualVectorInstructionDefs {
  case object VDualAddF32 extends Definition(
        "v_dual_add_f32",
        decoder(form(3)((operands, mods) => Instruction.VDualAddF32(operands(0), operands(1), operands(2), mods))),
        1843
      )
  case object VDualAddF64 extends Definition(
        "v_dual_add_f64",
        decoder(form(3)((operands, mods) => Instruction.VDualAddF64(operands(0), operands(1), operands(2), mods))),
        1844
      )
  case object VDualAddNcU32 extends Definition(
        "v_dual_add_nc_u32",
        decoder(form(3)((operands, mods) => Instruction.VDualAddNcU32(operands(0), operands(1), operands(2), mods))),
        1845
      )
  case object VDualAndB32 extends Definition(
        "v_dual_and_b32",
        decoder(form(3)((operands, mods) => Instruction.VDualAndB32(operands(0), operands(1), operands(2), mods))),
        1846
      )
  case object VDualAshrrevI32 extends Definition(
        "v_dual_ashrrev_i32",
        decoder(form(3)((operands, mods) => Instruction.VDualAshrrevI32(operands(0), operands(1), operands(2), mods))),
        1847
      )
  case object VDualBitop2B32 extends Definition(
        "v_dual_bitop2_b32",
        decoder(form(3)((operands, mods) => Instruction.VDualBitop2B32(operands(0), operands(1), operands(2), mods))),
        1848
      )
  case object VDualCndmaskB32 extends Definition(
        "v_dual_cndmask_b32",
        decoder(
          form(3)((operands, mods) => Instruction.VDualCndmaskB32_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.VDualCndmaskB32_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        1849
      )
  case object VDualDot2accF32Bf16 extends Definition(
        "v_dual_dot2acc_f32_bf16",
        decoder(form(3)((operands, mods) =>
          Instruction.VDualDot2accF32Bf16(operands(0), operands(1), operands(2), mods)
        )),
        1850
      )
  case object VDualDot2accF32F16 extends Definition(
        "v_dual_dot2acc_f32_f16",
        decoder(form(3)((operands, mods) =>
          Instruction.VDualDot2accF32F16(operands(0), operands(1), operands(2), mods)
        )),
        1851
      )
  case object VDualFmaF32 extends Definition(
        "v_dual_fma_f32",
        decoder(form(4)((operands, mods) =>
          Instruction.VDualFmaF32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        1852
      )
  case object VDualFmaF64 extends Definition(
        "v_dual_fma_f64",
        decoder(form(4)((operands, mods) =>
          Instruction.VDualFmaF64(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        1853
      )
  case object VDualFmaakF32 extends Definition(
        "v_dual_fmaak_f32",
        decoder(form(4)((operands, mods) =>
          Instruction.VDualFmaakF32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        1854
      )
  case object VDualFmacF32 extends Definition(
        "v_dual_fmac_f32",
        decoder(form(3)((operands, mods) => Instruction.VDualFmacF32(operands(0), operands(1), operands(2), mods))),
        1855
      )
  case object VDualFmamkF32 extends Definition(
        "v_dual_fmamk_f32",
        decoder(form(4)((operands, mods) =>
          Instruction.VDualFmamkF32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        1856
      )
  case object VDualLshlrevB32 extends Definition(
        "v_dual_lshlrev_b32",
        decoder(form(3)((operands, mods) => Instruction.VDualLshlrevB32(operands(0), operands(1), operands(2), mods))),
        1857
      )
  case object VDualLshrrevB32 extends Definition(
        "v_dual_lshrrev_b32",
        decoder(form(3)((operands, mods) => Instruction.VDualLshrrevB32(operands(0), operands(1), operands(2), mods))),
        1858
      )
  case object VDualMaxF32 extends Definition(
        "v_dual_max_f32",
        decoder(form(3)((operands, mods) => Instruction.VDualMaxF32(operands(0), operands(1), operands(2), mods))),
        1859
      )
  case object VDualMaxI32 extends Definition(
        "v_dual_max_i32",
        decoder(form(3)((operands, mods) => Instruction.VDualMaxI32(operands(0), operands(1), operands(2), mods))),
        1860
      )
  case object VDualMaxNumF32 extends Definition(
        "v_dual_max_num_f32",
        decoder(form(3)((operands, mods) => Instruction.VDualMaxNumF32(operands(0), operands(1), operands(2), mods))),
        1861
      )
  case object VDualMaxNumF64 extends Definition(
        "v_dual_max_num_f64",
        decoder(form(3)((operands, mods) => Instruction.VDualMaxNumF64(operands(0), operands(1), operands(2), mods))),
        1862
      )
  case object VDualMinF32 extends Definition(
        "v_dual_min_f32",
        decoder(form(3)((operands, mods) => Instruction.VDualMinF32(operands(0), operands(1), operands(2), mods))),
        1863
      )
  case object VDualMinI32 extends Definition(
        "v_dual_min_i32",
        decoder(form(3)((operands, mods) => Instruction.VDualMinI32(operands(0), operands(1), operands(2), mods))),
        1864
      )
  case object VDualMinNumF32 extends Definition(
        "v_dual_min_num_f32",
        decoder(form(3)((operands, mods) => Instruction.VDualMinNumF32(operands(0), operands(1), operands(2), mods))),
        1865
      )
  case object VDualMinNumF64 extends Definition(
        "v_dual_min_num_f64",
        decoder(form(3)((operands, mods) => Instruction.VDualMinNumF64(operands(0), operands(1), operands(2), mods))),
        1866
      )
  case object VDualMovB32 extends Definition(
        "v_dual_mov_b32",
        decoder(form(2)((operands, mods) => Instruction.VDualMovB32(operands(0), operands(1), mods))),
        1867
      )
  case object VDualMulDx9ZeroF32 extends Definition(
        "v_dual_mul_dx9_zero_f32",
        decoder(form(3)((operands, mods) =>
          Instruction.VDualMulDx9ZeroF32(operands(0), operands(1), operands(2), mods)
        )),
        1868
      )
  case object VDualMulF32 extends Definition(
        "v_dual_mul_f32",
        decoder(form(3)((operands, mods) => Instruction.VDualMulF32(operands(0), operands(1), operands(2), mods))),
        1869
      )
  case object VDualMulF64 extends Definition(
        "v_dual_mul_f64",
        decoder(form(3)((operands, mods) => Instruction.VDualMulF64(operands(0), operands(1), operands(2), mods))),
        1870
      )
  case object VDualSubF32 extends Definition(
        "v_dual_sub_f32",
        decoder(form(3)((operands, mods) => Instruction.VDualSubF32(operands(0), operands(1), operands(2), mods))),
        1871
      )
  case object VDualSubNcU32 extends Definition(
        "v_dual_sub_nc_u32",
        decoder(form(3)((operands, mods) => Instruction.VDualSubNcU32(operands(0), operands(1), operands(2), mods))),
        1872
      )
  case object VDualSubrevF32 extends Definition(
        "v_dual_subrev_f32",
        decoder(form(3)((operands, mods) => Instruction.VDualSubrevF32(operands(0), operands(1), operands(2), mods))),
        1873
      )

  val definitions: Vec[Definition] = Vec(
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
