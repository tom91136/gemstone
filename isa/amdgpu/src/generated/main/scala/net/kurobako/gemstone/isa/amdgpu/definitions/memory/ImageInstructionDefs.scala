// Generated from the pinned Mesa ACO and AMD machine-readable ISA inputs. Do not edit by hand.
// scalafix:off GemstoneNoAssume; values were validated by the generator
package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.Vec

private[amdgpu] object ImageInstructionDefs {
  case object ImageAtomicAdd extends InstructionDef(
        AMDGPUOpcode.assume("image_atomic_add"),
        decoder(form(3)((operands, mods) => Instruction.ImageAtomicAdd(operands(0), operands(1), operands(2), mods))),
        679
      )
  case object ImageAtomicAddFlt extends InstructionDef(
        AMDGPUOpcode.assume("image_atomic_add_flt"),
        decoder(form(3)((operands, mods) =>
          Instruction.ImageAtomicAddFlt(operands(0), operands(1), operands(2), mods)
        )),
        680
      )
  case object ImageAtomicAddUint extends InstructionDef(
        AMDGPUOpcode.assume("image_atomic_add_uint"),
        decoder(form(3)((operands, mods) =>
          Instruction.ImageAtomicAddUint(operands(0), operands(1), operands(2), mods)
        )),
        681
      )
  case object ImageAtomicAnd extends InstructionDef(
        AMDGPUOpcode.assume("image_atomic_and"),
        decoder(form(3)((operands, mods) => Instruction.ImageAtomicAnd(operands(0), operands(1), operands(2), mods))),
        682
      )
  case object ImageAtomicCmpswap extends InstructionDef(
        AMDGPUOpcode.assume("image_atomic_cmpswap"),
        decoder(form(3)((operands, mods) =>
          Instruction.ImageAtomicCmpswap(operands(0), operands(1), operands(2), mods)
        )),
        683
      )
  case object ImageAtomicDec extends InstructionDef(
        AMDGPUOpcode.assume("image_atomic_dec"),
        decoder(form(3)((operands, mods) => Instruction.ImageAtomicDec(operands(0), operands(1), operands(2), mods))),
        684
      )
  case object ImageAtomicDecUint extends InstructionDef(
        AMDGPUOpcode.assume("image_atomic_dec_uint"),
        decoder(form(3)((operands, mods) =>
          Instruction.ImageAtomicDecUint(operands(0), operands(1), operands(2), mods)
        )),
        685
      )
  case object ImageAtomicFcmpswap extends InstructionDef(
        AMDGPUOpcode.assume("image_atomic_fcmpswap"),
        decoder(form(3)((operands, mods) =>
          Instruction.ImageAtomicFcmpswap(operands(0), operands(1), operands(2), mods)
        )),
        686
      )
  case object ImageAtomicFmax extends InstructionDef(
        AMDGPUOpcode.assume("image_atomic_fmax"),
        decoder(form(3)((operands, mods) => Instruction.ImageAtomicFmax(operands(0), operands(1), operands(2), mods))),
        687
      )
  case object ImageAtomicFmin extends InstructionDef(
        AMDGPUOpcode.assume("image_atomic_fmin"),
        decoder(form(3)((operands, mods) => Instruction.ImageAtomicFmin(operands(0), operands(1), operands(2), mods))),
        688
      )
  case object ImageAtomicInc extends InstructionDef(
        AMDGPUOpcode.assume("image_atomic_inc"),
        decoder(form(3)((operands, mods) => Instruction.ImageAtomicInc(operands(0), operands(1), operands(2), mods))),
        689
      )
  case object ImageAtomicIncUint extends InstructionDef(
        AMDGPUOpcode.assume("image_atomic_inc_uint"),
        decoder(form(3)((operands, mods) =>
          Instruction.ImageAtomicIncUint(operands(0), operands(1), operands(2), mods)
        )),
        690
      )
  case object ImageAtomicMaxFlt extends InstructionDef(
        AMDGPUOpcode.assume("image_atomic_max_flt"),
        decoder(form(3)((operands, mods) =>
          Instruction.ImageAtomicMaxFlt(operands(0), operands(1), operands(2), mods)
        )),
        691
      )
  case object ImageAtomicMaxInt extends InstructionDef(
        AMDGPUOpcode.assume("image_atomic_max_int"),
        decoder(form(3)((operands, mods) =>
          Instruction.ImageAtomicMaxInt(operands(0), operands(1), operands(2), mods)
        )),
        692
      )
  case object ImageAtomicMaxUint extends InstructionDef(
        AMDGPUOpcode.assume("image_atomic_max_uint"),
        decoder(form(3)((operands, mods) =>
          Instruction.ImageAtomicMaxUint(operands(0), operands(1), operands(2), mods)
        )),
        693
      )
  case object ImageAtomicMinFlt extends InstructionDef(
        AMDGPUOpcode.assume("image_atomic_min_flt"),
        decoder(form(3)((operands, mods) =>
          Instruction.ImageAtomicMinFlt(operands(0), operands(1), operands(2), mods)
        )),
        694
      )
  case object ImageAtomicMinInt extends InstructionDef(
        AMDGPUOpcode.assume("image_atomic_min_int"),
        decoder(form(3)((operands, mods) =>
          Instruction.ImageAtomicMinInt(operands(0), operands(1), operands(2), mods)
        )),
        695
      )
  case object ImageAtomicMinUint extends InstructionDef(
        AMDGPUOpcode.assume("image_atomic_min_uint"),
        decoder(form(3)((operands, mods) =>
          Instruction.ImageAtomicMinUint(operands(0), operands(1), operands(2), mods)
        )),
        696
      )
  case object ImageAtomicOr extends InstructionDef(
        AMDGPUOpcode.assume("image_atomic_or"),
        decoder(form(3)((operands, mods) => Instruction.ImageAtomicOr(operands(0), operands(1), operands(2), mods))),
        697
      )
  case object ImageAtomicPkAddBf16 extends InstructionDef(
        AMDGPUOpcode.assume("image_atomic_pk_add_bf16"),
        decoder(form(3)((operands, mods) =>
          Instruction.ImageAtomicPkAddBf16(operands(0), operands(1), operands(2), mods)
        )),
        698
      )
  case object ImageAtomicPkAddF16 extends InstructionDef(
        AMDGPUOpcode.assume("image_atomic_pk_add_f16"),
        decoder(form(3)((operands, mods) =>
          Instruction.ImageAtomicPkAddF16(operands(0), operands(1), operands(2), mods)
        )),
        699
      )
  case object ImageAtomicRsub extends InstructionDef(
        AMDGPUOpcode.assume("image_atomic_rsub"),
        decoder(form(3)((operands, mods) => Instruction.ImageAtomicRsub(operands(0), operands(1), operands(2), mods))),
        700
      )
  case object ImageAtomicSmax extends InstructionDef(
        AMDGPUOpcode.assume("image_atomic_smax"),
        decoder(form(3)((operands, mods) => Instruction.ImageAtomicSmax(operands(0), operands(1), operands(2), mods))),
        701
      )
  case object ImageAtomicSmin extends InstructionDef(
        AMDGPUOpcode.assume("image_atomic_smin"),
        decoder(form(3)((operands, mods) => Instruction.ImageAtomicSmin(operands(0), operands(1), operands(2), mods))),
        702
      )
  case object ImageAtomicSub extends InstructionDef(
        AMDGPUOpcode.assume("image_atomic_sub"),
        decoder(form(3)((operands, mods) => Instruction.ImageAtomicSub(operands(0), operands(1), operands(2), mods))),
        703
      )
  case object ImageAtomicSubUint extends InstructionDef(
        AMDGPUOpcode.assume("image_atomic_sub_uint"),
        decoder(form(3)((operands, mods) =>
          Instruction.ImageAtomicSubUint(operands(0), operands(1), operands(2), mods)
        )),
        704
      )
  case object ImageAtomicSwap extends InstructionDef(
        AMDGPUOpcode.assume("image_atomic_swap"),
        decoder(form(3)((operands, mods) => Instruction.ImageAtomicSwap(operands(0), operands(1), operands(2), mods))),
        705
      )
  case object ImageAtomicUmax extends InstructionDef(
        AMDGPUOpcode.assume("image_atomic_umax"),
        decoder(form(3)((operands, mods) => Instruction.ImageAtomicUmax(operands(0), operands(1), operands(2), mods))),
        706
      )
  case object ImageAtomicUmin extends InstructionDef(
        AMDGPUOpcode.assume("image_atomic_umin"),
        decoder(form(3)((operands, mods) => Instruction.ImageAtomicUmin(operands(0), operands(1), operands(2), mods))),
        707
      )
  case object ImageAtomicXor extends InstructionDef(
        AMDGPUOpcode.assume("image_atomic_xor"),
        decoder(form(3)((operands, mods) => Instruction.ImageAtomicXor(operands(0), operands(1), operands(2), mods))),
        708
      )
  case object ImageBvh64IntersectRay extends InstructionDef(
        AMDGPUOpcode.assume("image_bvh64_intersect_ray"),
        decoder(form(3)((operands, mods) =>
          Instruction.ImageBvh64IntersectRay(operands(0), operands(1), operands(2), mods)
        )),
        709
      )
  case object ImageBvh8IntersectRay extends InstructionDef(
        AMDGPUOpcode.assume("image_bvh8_intersect_ray"),
        decoder(form(3)((operands, mods) =>
          Instruction.ImageBvh8IntersectRay(operands(0), operands(1), operands(2), mods)
        )),
        710
      )
  case object ImageBvhDualIntersectRay extends InstructionDef(
        AMDGPUOpcode.assume("image_bvh_dual_intersect_ray"),
        decoder(form(3)((operands, mods) =>
          Instruction.ImageBvhDualIntersectRay(operands(0), operands(1), operands(2), mods)
        )),
        711
      )
  case object ImageBvhIntersectRay extends InstructionDef(
        AMDGPUOpcode.assume("image_bvh_intersect_ray"),
        decoder(form(3)((operands, mods) =>
          Instruction.ImageBvhIntersectRay(operands(0), operands(1), operands(2), mods)
        )),
        712
      )
  case object ImageGather4 extends InstructionDef(
        AMDGPUOpcode.assume("image_gather4"),
        decoder(form(4)((operands, mods) =>
          Instruction.ImageGather4(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        713
      )
  case object ImageGather4B extends InstructionDef(
        AMDGPUOpcode.assume("image_gather4_b"),
        decoder(form(4)((operands, mods) =>
          Instruction.ImageGather4B(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        714
      )
  case object ImageGather4BCl extends InstructionDef(
        AMDGPUOpcode.assume("image_gather4_b_cl"),
        decoder(form(4)((operands, mods) =>
          Instruction.ImageGather4BCl(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        715
      )
  case object ImageGather4BClO extends InstructionDef(
        AMDGPUOpcode.assume("image_gather4_b_cl_o"),
        decoder(form(4)((operands, mods) =>
          Instruction.ImageGather4BClO(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        716
      )
  case object ImageGather4BO extends InstructionDef(
        AMDGPUOpcode.assume("image_gather4_b_o"),
        decoder(form(4)((operands, mods) =>
          Instruction.ImageGather4BO(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        717
      )
  case object ImageGather4C extends InstructionDef(
        AMDGPUOpcode.assume("image_gather4_c"),
        decoder(form(4)((operands, mods) =>
          Instruction.ImageGather4C(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        718
      )
  case object ImageGather4CB extends InstructionDef(
        AMDGPUOpcode.assume("image_gather4_c_b"),
        decoder(form(4)((operands, mods) =>
          Instruction.ImageGather4CB(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        719
      )
  case object ImageGather4CBCl extends InstructionDef(
        AMDGPUOpcode.assume("image_gather4_c_b_cl"),
        decoder(form(4)((operands, mods) =>
          Instruction.ImageGather4CBCl(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        720
      )
  case object ImageGather4CBClO extends InstructionDef(
        AMDGPUOpcode.assume("image_gather4_c_b_cl_o"),
        decoder(form(4)((operands, mods) =>
          Instruction.ImageGather4CBClO(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        721
      )
  case object ImageGather4CBO extends InstructionDef(
        AMDGPUOpcode.assume("image_gather4_c_b_o"),
        decoder(form(4)((operands, mods) =>
          Instruction.ImageGather4CBO(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        722
      )
  case object ImageGather4CCl extends InstructionDef(
        AMDGPUOpcode.assume("image_gather4_c_cl"),
        decoder(form(4)((operands, mods) =>
          Instruction.ImageGather4CCl(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        723
      )
  case object ImageGather4CClO extends InstructionDef(
        AMDGPUOpcode.assume("image_gather4_c_cl_o"),
        decoder(form(4)((operands, mods) =>
          Instruction.ImageGather4CClO(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        724
      )
  case object ImageGather4CL_1 extends InstructionDef(
        AMDGPUOpcode.assume("image_gather4_c_l"),
        decoder(form(4)((operands, mods) =>
          Instruction.ImageGather4CL_1(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        725
      )
  case object ImageGather4CLO_1 extends InstructionDef(
        AMDGPUOpcode.assume("image_gather4_c_l_o"),
        decoder(form(4)((operands, mods) =>
          Instruction.ImageGather4CLO_1(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        726
      )
  case object ImageGather4CLz extends InstructionDef(
        AMDGPUOpcode.assume("image_gather4_c_lz"),
        decoder(form(4)((operands, mods) =>
          Instruction.ImageGather4CLz(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        727
      )
  case object ImageGather4CLzO extends InstructionDef(
        AMDGPUOpcode.assume("image_gather4_c_lz_o"),
        decoder(form(4)((operands, mods) =>
          Instruction.ImageGather4CLzO(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        728
      )
  case object ImageGather4CO extends InstructionDef(
        AMDGPUOpcode.assume("image_gather4_c_o"),
        decoder(form(4)((operands, mods) =>
          Instruction.ImageGather4CO(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        729
      )
  case object ImageGather4Cl_2 extends InstructionDef(
        AMDGPUOpcode.assume("image_gather4_cl"),
        decoder(form(4)((operands, mods) =>
          Instruction.ImageGather4Cl_2(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        730
      )
  case object ImageGather4ClO_2 extends InstructionDef(
        AMDGPUOpcode.assume("image_gather4_cl_o"),
        decoder(form(4)((operands, mods) =>
          Instruction.ImageGather4ClO_2(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        731
      )
  case object ImageGather4L extends InstructionDef(
        AMDGPUOpcode.assume("image_gather4_l"),
        decoder(form(4)((operands, mods) =>
          Instruction.ImageGather4L(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        732
      )
  case object ImageGather4LO extends InstructionDef(
        AMDGPUOpcode.assume("image_gather4_l_o"),
        decoder(form(4)((operands, mods) =>
          Instruction.ImageGather4LO(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        733
      )
  case object ImageGather4Lz extends InstructionDef(
        AMDGPUOpcode.assume("image_gather4_lz"),
        decoder(form(4)((operands, mods) =>
          Instruction.ImageGather4Lz(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        734
      )
  case object ImageGather4LzO extends InstructionDef(
        AMDGPUOpcode.assume("image_gather4_lz_o"),
        decoder(form(4)((operands, mods) =>
          Instruction.ImageGather4LzO(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        735
      )
  case object ImageGather4O extends InstructionDef(
        AMDGPUOpcode.assume("image_gather4_o"),
        decoder(form(4)((operands, mods) =>
          Instruction.ImageGather4O(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        736
      )
  case object ImageGather4h extends InstructionDef(
        AMDGPUOpcode.assume("image_gather4h"),
        decoder(form(4)((operands, mods) =>
          Instruction.ImageGather4h(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        737
      )
  case object ImageGather4hPck extends InstructionDef(
        AMDGPUOpcode.assume("image_gather4h_pck"),
        decoder(form(4)((operands, mods) =>
          Instruction.ImageGather4hPck(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        738
      )
  case object ImageGather8hPck extends InstructionDef(
        AMDGPUOpcode.assume("image_gather8h_pck"),
        decoder(form(4)((operands, mods) =>
          Instruction.ImageGather8hPck(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        739
      )
  case object ImageGetLod extends InstructionDef(
        AMDGPUOpcode.assume("image_get_lod"),
        decoder(form(4)((operands, mods) =>
          Instruction.ImageGetLod(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        740
      )
  case object ImageGetResinfo extends InstructionDef(
        AMDGPUOpcode.assume("image_get_resinfo"),
        decoder(form(3)((operands, mods) => Instruction.ImageGetResinfo(operands(0), operands(1), operands(2), mods))),
        741
      )
  case object ImageLoad extends InstructionDef(
        AMDGPUOpcode.assume("image_load"),
        decoder(form(3)((operands, mods) => Instruction.ImageLoad(operands(0), operands(1), operands(2), mods))),
        742
      )
  case object ImageLoadBy2 extends InstructionDef(
        AMDGPUOpcode.assume("image_load_by2"),
        decoder(form(3)((operands, mods) => Instruction.ImageLoadBy2(operands(0), operands(1), operands(2), mods))),
        743
      )
  case object ImageLoadBy4 extends InstructionDef(
        AMDGPUOpcode.assume("image_load_by4"),
        decoder(form(3)((operands, mods) => Instruction.ImageLoadBy4(operands(0), operands(1), operands(2), mods))),
        744
      )
  case object ImageLoadMip extends InstructionDef(
        AMDGPUOpcode.assume("image_load_mip"),
        decoder(form(3)((operands, mods) => Instruction.ImageLoadMip(operands(0), operands(1), operands(2), mods))),
        745
      )
  case object ImageLoadMipBy2 extends InstructionDef(
        AMDGPUOpcode.assume("image_load_mip_by2"),
        decoder(form(3)((operands, mods) => Instruction.ImageLoadMipBy2(operands(0), operands(1), operands(2), mods))),
        746
      )
  case object ImageLoadMipBy4 extends InstructionDef(
        AMDGPUOpcode.assume("image_load_mip_by4"),
        decoder(form(3)((operands, mods) => Instruction.ImageLoadMipBy4(operands(0), operands(1), operands(2), mods))),
        747
      )
  case object ImageLoadMipPck extends InstructionDef(
        AMDGPUOpcode.assume("image_load_mip_pck"),
        decoder(form(3)((operands, mods) => Instruction.ImageLoadMipPck(operands(0), operands(1), operands(2), mods))),
        748
      )
  case object ImageLoadMipPck2 extends InstructionDef(
        AMDGPUOpcode.assume("image_load_mip_pck2"),
        decoder(form(3)((operands, mods) => Instruction.ImageLoadMipPck2(operands(0), operands(1), operands(2), mods))),
        749
      )
  case object ImageLoadMipPck4 extends InstructionDef(
        AMDGPUOpcode.assume("image_load_mip_pck4"),
        decoder(form(3)((operands, mods) => Instruction.ImageLoadMipPck4(operands(0), operands(1), operands(2), mods))),
        750
      )
  case object ImageLoadMipPckSgn extends InstructionDef(
        AMDGPUOpcode.assume("image_load_mip_pck_sgn"),
        decoder(form(3)((operands, mods) =>
          Instruction.ImageLoadMipPckSgn(operands(0), operands(1), operands(2), mods)
        )),
        751
      )
  case object ImageLoadPck extends InstructionDef(
        AMDGPUOpcode.assume("image_load_pck"),
        decoder(form(3)((operands, mods) => Instruction.ImageLoadPck(operands(0), operands(1), operands(2), mods))),
        752
      )
  case object ImageLoadPck2 extends InstructionDef(
        AMDGPUOpcode.assume("image_load_pck2"),
        decoder(form(3)((operands, mods) => Instruction.ImageLoadPck2(operands(0), operands(1), operands(2), mods))),
        753
      )
  case object ImageLoadPck4 extends InstructionDef(
        AMDGPUOpcode.assume("image_load_pck4"),
        decoder(form(3)((operands, mods) => Instruction.ImageLoadPck4(operands(0), operands(1), operands(2), mods))),
        754
      )
  case object ImageLoadPckSgn extends InstructionDef(
        AMDGPUOpcode.assume("image_load_pck_sgn"),
        decoder(form(3)((operands, mods) => Instruction.ImageLoadPckSgn(operands(0), operands(1), operands(2), mods))),
        755
      )
  case object ImageMsaaLoad extends InstructionDef(
        AMDGPUOpcode.assume("image_msaa_load"),
        decoder(form(3)((operands, mods) => Instruction.ImageMsaaLoad(operands(0), operands(1), operands(2), mods))),
        756
      )
  case object ImageSample extends InstructionDef(
        AMDGPUOpcode.assume("image_sample"),
        decoder(form(4)((operands, mods) =>
          Instruction.ImageSample(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        757
      )
  case object ImageSampleB extends InstructionDef(
        AMDGPUOpcode.assume("image_sample_b"),
        decoder(form(4)((operands, mods) =>
          Instruction.ImageSampleB(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        758
      )
  case object ImageSampleBCl extends InstructionDef(
        AMDGPUOpcode.assume("image_sample_b_cl"),
        decoder(form(4)((operands, mods) =>
          Instruction.ImageSampleBCl(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        759
      )
  case object ImageSampleBClO extends InstructionDef(
        AMDGPUOpcode.assume("image_sample_b_cl_o"),
        decoder(form(4)((operands, mods) =>
          Instruction.ImageSampleBClO(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        760
      )
  case object ImageSampleBO extends InstructionDef(
        AMDGPUOpcode.assume("image_sample_b_o"),
        decoder(form(4)((operands, mods) =>
          Instruction.ImageSampleBO(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        761
      )
  case object ImageSampleC extends InstructionDef(
        AMDGPUOpcode.assume("image_sample_c"),
        decoder(form(4)((operands, mods) =>
          Instruction.ImageSampleC(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        762
      )
  case object ImageSampleCB extends InstructionDef(
        AMDGPUOpcode.assume("image_sample_c_b"),
        decoder(form(4)((operands, mods) =>
          Instruction.ImageSampleCB(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        763
      )
  case object ImageSampleCBCl extends InstructionDef(
        AMDGPUOpcode.assume("image_sample_c_b_cl"),
        decoder(form(4)((operands, mods) =>
          Instruction.ImageSampleCBCl(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        764
      )
  case object ImageSampleCBClO extends InstructionDef(
        AMDGPUOpcode.assume("image_sample_c_b_cl_o"),
        decoder(form(4)((operands, mods) =>
          Instruction.ImageSampleCBClO(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        765
      )
  case object ImageSampleCBO extends InstructionDef(
        AMDGPUOpcode.assume("image_sample_c_b_o"),
        decoder(form(4)((operands, mods) =>
          Instruction.ImageSampleCBO(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        766
      )
  case object ImageSampleCCd extends InstructionDef(
        AMDGPUOpcode.assume("image_sample_c_cd"),
        decoder(form(4)((operands, mods) =>
          Instruction.ImageSampleCCd(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        767
      )
  case object ImageSampleCCdCl extends InstructionDef(
        AMDGPUOpcode.assume("image_sample_c_cd_cl"),
        decoder(form(4)((operands, mods) =>
          Instruction.ImageSampleCCdCl(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        768
      )
  case object ImageSampleCCdClG16 extends InstructionDef(
        AMDGPUOpcode.assume("image_sample_c_cd_cl_g16"),
        decoder(form(4)((operands, mods) =>
          Instruction.ImageSampleCCdClG16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        769
      )
  case object ImageSampleCCdClO extends InstructionDef(
        AMDGPUOpcode.assume("image_sample_c_cd_cl_o"),
        decoder(form(4)((operands, mods) =>
          Instruction.ImageSampleCCdClO(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        770
      )
  case object ImageSampleCCdClOG16 extends InstructionDef(
        AMDGPUOpcode.assume("image_sample_c_cd_cl_o_g16"),
        decoder(form(4)((operands, mods) =>
          Instruction.ImageSampleCCdClOG16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        771
      )
  case object ImageSampleCCdG16 extends InstructionDef(
        AMDGPUOpcode.assume("image_sample_c_cd_g16"),
        decoder(form(4)((operands, mods) =>
          Instruction.ImageSampleCCdG16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        772
      )
  case object ImageSampleCCdO extends InstructionDef(
        AMDGPUOpcode.assume("image_sample_c_cd_o"),
        decoder(form(4)((operands, mods) =>
          Instruction.ImageSampleCCdO(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        773
      )
  case object ImageSampleCCdOG16 extends InstructionDef(
        AMDGPUOpcode.assume("image_sample_c_cd_o_g16"),
        decoder(form(4)((operands, mods) =>
          Instruction.ImageSampleCCdOG16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        774
      )
  case object ImageSampleCCl extends InstructionDef(
        AMDGPUOpcode.assume("image_sample_c_cl"),
        decoder(form(4)((operands, mods) =>
          Instruction.ImageSampleCCl(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        775
      )
  case object ImageSampleCClO extends InstructionDef(
        AMDGPUOpcode.assume("image_sample_c_cl_o"),
        decoder(form(4)((operands, mods) =>
          Instruction.ImageSampleCClO(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        776
      )
  case object ImageSampleCD_1 extends InstructionDef(
        AMDGPUOpcode.assume("image_sample_c_d"),
        decoder(form(4)((operands, mods) =>
          Instruction.ImageSampleCD_1(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        777
      )
  case object ImageSampleCDCl_1 extends InstructionDef(
        AMDGPUOpcode.assume("image_sample_c_d_cl"),
        decoder(form(4)((operands, mods) =>
          Instruction.ImageSampleCDCl_1(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        778
      )
  case object ImageSampleCDClG16_1 extends InstructionDef(
        AMDGPUOpcode.assume("image_sample_c_d_cl_g16"),
        decoder(form(4)((operands, mods) =>
          Instruction.ImageSampleCDClG16_1(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        779
      )
  case object ImageSampleCDClO_1 extends InstructionDef(
        AMDGPUOpcode.assume("image_sample_c_d_cl_o"),
        decoder(form(4)((operands, mods) =>
          Instruction.ImageSampleCDClO_1(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        780
      )
  case object ImageSampleCDClOG16_1 extends InstructionDef(
        AMDGPUOpcode.assume("image_sample_c_d_cl_o_g16"),
        decoder(form(4)((operands, mods) =>
          Instruction.ImageSampleCDClOG16_1(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        781
      )
  case object ImageSampleCDG16_1 extends InstructionDef(
        AMDGPUOpcode.assume("image_sample_c_d_g16"),
        decoder(form(4)((operands, mods) =>
          Instruction.ImageSampleCDG16_1(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        782
      )
  case object ImageSampleCDO_1 extends InstructionDef(
        AMDGPUOpcode.assume("image_sample_c_d_o"),
        decoder(form(4)((operands, mods) =>
          Instruction.ImageSampleCDO_1(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        783
      )
  case object ImageSampleCDOG16_1 extends InstructionDef(
        AMDGPUOpcode.assume("image_sample_c_d_o_g16"),
        decoder(form(4)((operands, mods) =>
          Instruction.ImageSampleCDOG16_1(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        784
      )
  case object ImageSampleCL_1 extends InstructionDef(
        AMDGPUOpcode.assume("image_sample_c_l"),
        decoder(form(4)((operands, mods) =>
          Instruction.ImageSampleCL_1(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        785
      )
  case object ImageSampleCLO_1 extends InstructionDef(
        AMDGPUOpcode.assume("image_sample_c_l_o"),
        decoder(form(4)((operands, mods) =>
          Instruction.ImageSampleCLO_1(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        786
      )
  case object ImageSampleCLz extends InstructionDef(
        AMDGPUOpcode.assume("image_sample_c_lz"),
        decoder(form(4)((operands, mods) =>
          Instruction.ImageSampleCLz(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        787
      )
  case object ImageSampleCLzO extends InstructionDef(
        AMDGPUOpcode.assume("image_sample_c_lz_o"),
        decoder(form(4)((operands, mods) =>
          Instruction.ImageSampleCLzO(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        788
      )
  case object ImageSampleCO extends InstructionDef(
        AMDGPUOpcode.assume("image_sample_c_o"),
        decoder(form(4)((operands, mods) =>
          Instruction.ImageSampleCO(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        789
      )
  case object ImageSampleCd_2 extends InstructionDef(
        AMDGPUOpcode.assume("image_sample_cd"),
        decoder(form(4)((operands, mods) =>
          Instruction.ImageSampleCd_2(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        790
      )
  case object ImageSampleCdCl_2 extends InstructionDef(
        AMDGPUOpcode.assume("image_sample_cd_cl"),
        decoder(form(4)((operands, mods) =>
          Instruction.ImageSampleCdCl_2(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        791
      )
  case object ImageSampleCdClG16_2 extends InstructionDef(
        AMDGPUOpcode.assume("image_sample_cd_cl_g16"),
        decoder(form(4)((operands, mods) =>
          Instruction.ImageSampleCdClG16_2(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        792
      )
  case object ImageSampleCdClO_2 extends InstructionDef(
        AMDGPUOpcode.assume("image_sample_cd_cl_o"),
        decoder(form(4)((operands, mods) =>
          Instruction.ImageSampleCdClO_2(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        793
      )
  case object ImageSampleCdClOG16_2 extends InstructionDef(
        AMDGPUOpcode.assume("image_sample_cd_cl_o_g16"),
        decoder(form(4)((operands, mods) =>
          Instruction.ImageSampleCdClOG16_2(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        794
      )
  case object ImageSampleCdG16_2 extends InstructionDef(
        AMDGPUOpcode.assume("image_sample_cd_g16"),
        decoder(form(4)((operands, mods) =>
          Instruction.ImageSampleCdG16_2(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        795
      )
  case object ImageSampleCdO_2 extends InstructionDef(
        AMDGPUOpcode.assume("image_sample_cd_o"),
        decoder(form(4)((operands, mods) =>
          Instruction.ImageSampleCdO_2(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        796
      )
  case object ImageSampleCdOG16_2 extends InstructionDef(
        AMDGPUOpcode.assume("image_sample_cd_o_g16"),
        decoder(form(4)((operands, mods) =>
          Instruction.ImageSampleCdOG16_2(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        797
      )
  case object ImageSampleCl_2 extends InstructionDef(
        AMDGPUOpcode.assume("image_sample_cl"),
        decoder(form(4)((operands, mods) =>
          Instruction.ImageSampleCl_2(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        798
      )
  case object ImageSampleClO_2 extends InstructionDef(
        AMDGPUOpcode.assume("image_sample_cl_o"),
        decoder(form(4)((operands, mods) =>
          Instruction.ImageSampleClO_2(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        799
      )
  case object ImageSampleD extends InstructionDef(
        AMDGPUOpcode.assume("image_sample_d"),
        decoder(form(4)((operands, mods) =>
          Instruction.ImageSampleD(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        800
      )
  case object ImageSampleDCl extends InstructionDef(
        AMDGPUOpcode.assume("image_sample_d_cl"),
        decoder(form(4)((operands, mods) =>
          Instruction.ImageSampleDCl(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        801
      )
  case object ImageSampleDClG16 extends InstructionDef(
        AMDGPUOpcode.assume("image_sample_d_cl_g16"),
        decoder(form(4)((operands, mods) =>
          Instruction.ImageSampleDClG16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        802
      )
  case object ImageSampleDClO extends InstructionDef(
        AMDGPUOpcode.assume("image_sample_d_cl_o"),
        decoder(form(4)((operands, mods) =>
          Instruction.ImageSampleDClO(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        803
      )
  case object ImageSampleDClOG16 extends InstructionDef(
        AMDGPUOpcode.assume("image_sample_d_cl_o_g16"),
        decoder(form(4)((operands, mods) =>
          Instruction.ImageSampleDClOG16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        804
      )
  case object ImageSampleDG16 extends InstructionDef(
        AMDGPUOpcode.assume("image_sample_d_g16"),
        decoder(form(4)((operands, mods) =>
          Instruction.ImageSampleDG16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        805
      )
  case object ImageSampleDO extends InstructionDef(
        AMDGPUOpcode.assume("image_sample_d_o"),
        decoder(form(4)((operands, mods) =>
          Instruction.ImageSampleDO(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        806
      )
  case object ImageSampleDOG16 extends InstructionDef(
        AMDGPUOpcode.assume("image_sample_d_o_g16"),
        decoder(form(4)((operands, mods) =>
          Instruction.ImageSampleDOG16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        807
      )
  case object ImageSampleL extends InstructionDef(
        AMDGPUOpcode.assume("image_sample_l"),
        decoder(form(4)((operands, mods) =>
          Instruction.ImageSampleL(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        808
      )
  case object ImageSampleLO extends InstructionDef(
        AMDGPUOpcode.assume("image_sample_l_o"),
        decoder(form(4)((operands, mods) =>
          Instruction.ImageSampleLO(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        809
      )
  case object ImageSampleLz extends InstructionDef(
        AMDGPUOpcode.assume("image_sample_lz"),
        decoder(form(4)((operands, mods) =>
          Instruction.ImageSampleLz(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        810
      )
  case object ImageSampleLzO extends InstructionDef(
        AMDGPUOpcode.assume("image_sample_lz_o"),
        decoder(form(4)((operands, mods) =>
          Instruction.ImageSampleLzO(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        811
      )
  case object ImageSampleO extends InstructionDef(
        AMDGPUOpcode.assume("image_sample_o"),
        decoder(form(4)((operands, mods) =>
          Instruction.ImageSampleO(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        812
      )
  case object ImageStore extends InstructionDef(
        AMDGPUOpcode.assume("image_store"),
        decoder(form(3)((operands, mods) => Instruction.ImageStore(operands(0), operands(1), operands(2), mods))),
        813
      )
  case object ImageStoreBy2 extends InstructionDef(
        AMDGPUOpcode.assume("image_store_by2"),
        decoder(form(3)((operands, mods) => Instruction.ImageStoreBy2(operands(0), operands(1), operands(2), mods))),
        814
      )
  case object ImageStoreBy4 extends InstructionDef(
        AMDGPUOpcode.assume("image_store_by4"),
        decoder(form(3)((operands, mods) => Instruction.ImageStoreBy4(operands(0), operands(1), operands(2), mods))),
        815
      )
  case object ImageStoreMip extends InstructionDef(
        AMDGPUOpcode.assume("image_store_mip"),
        decoder(form(3)((operands, mods) => Instruction.ImageStoreMip(operands(0), operands(1), operands(2), mods))),
        816
      )
  case object ImageStoreMipBy2 extends InstructionDef(
        AMDGPUOpcode.assume("image_store_mip_by2"),
        decoder(form(3)((operands, mods) => Instruction.ImageStoreMipBy2(operands(0), operands(1), operands(2), mods))),
        817
      )
  case object ImageStoreMipBy4 extends InstructionDef(
        AMDGPUOpcode.assume("image_store_mip_by4"),
        decoder(form(3)((operands, mods) => Instruction.ImageStoreMipBy4(operands(0), operands(1), operands(2), mods))),
        818
      )
  case object ImageStoreMipPck extends InstructionDef(
        AMDGPUOpcode.assume("image_store_mip_pck"),
        decoder(form(3)((operands, mods) => Instruction.ImageStoreMipPck(operands(0), operands(1), operands(2), mods))),
        819
      )
  case object ImageStoreMipPck2 extends InstructionDef(
        AMDGPUOpcode.assume("image_store_mip_pck2"),
        decoder(form(3)((operands, mods) =>
          Instruction.ImageStoreMipPck2(operands(0), operands(1), operands(2), mods)
        )),
        820
      )
  case object ImageStoreMipPck4 extends InstructionDef(
        AMDGPUOpcode.assume("image_store_mip_pck4"),
        decoder(form(3)((operands, mods) =>
          Instruction.ImageStoreMipPck4(operands(0), operands(1), operands(2), mods)
        )),
        821
      )
  case object ImageStorePck extends InstructionDef(
        AMDGPUOpcode.assume("image_store_pck"),
        decoder(form(3)((operands, mods) => Instruction.ImageStorePck(operands(0), operands(1), operands(2), mods))),
        822
      )
  case object ImageStorePck2 extends InstructionDef(
        AMDGPUOpcode.assume("image_store_pck2"),
        decoder(form(3)((operands, mods) => Instruction.ImageStorePck2(operands(0), operands(1), operands(2), mods))),
        823
      )
  case object ImageStorePck4 extends InstructionDef(
        AMDGPUOpcode.assume("image_store_pck4"),
        decoder(form(3)((operands, mods) => Instruction.ImageStorePck4(operands(0), operands(1), operands(2), mods))),
        824
      )

  val definitions: Vec[InstructionDef] = Vec(
    ImageAtomicAdd,
    ImageAtomicAddFlt,
    ImageAtomicAddUint,
    ImageAtomicAnd,
    ImageAtomicCmpswap,
    ImageAtomicDec,
    ImageAtomicDecUint,
    ImageAtomicFcmpswap,
    ImageAtomicFmax,
    ImageAtomicFmin,
    ImageAtomicInc,
    ImageAtomicIncUint,
    ImageAtomicMaxFlt,
    ImageAtomicMaxInt,
    ImageAtomicMaxUint,
    ImageAtomicMinFlt,
    ImageAtomicMinInt,
    ImageAtomicMinUint,
    ImageAtomicOr,
    ImageAtomicPkAddBf16,
    ImageAtomicPkAddF16,
    ImageAtomicRsub,
    ImageAtomicSmax,
    ImageAtomicSmin,
    ImageAtomicSub,
    ImageAtomicSubUint,
    ImageAtomicSwap,
    ImageAtomicUmax,
    ImageAtomicUmin,
    ImageAtomicXor,
    ImageBvh64IntersectRay,
    ImageBvh8IntersectRay,
    ImageBvhDualIntersectRay,
    ImageBvhIntersectRay,
    ImageGather4,
    ImageGather4B,
    ImageGather4BCl,
    ImageGather4BClO,
    ImageGather4BO,
    ImageGather4C,
    ImageGather4CB,
    ImageGather4CBCl,
    ImageGather4CBClO,
    ImageGather4CBO,
    ImageGather4CCl,
    ImageGather4CClO,
    ImageGather4CL_1,
    ImageGather4CLO_1,
    ImageGather4CLz,
    ImageGather4CLzO,
    ImageGather4CO,
    ImageGather4Cl_2,
    ImageGather4ClO_2,
    ImageGather4L,
    ImageGather4LO,
    ImageGather4Lz,
    ImageGather4LzO,
    ImageGather4O,
    ImageGather4h,
    ImageGather4hPck,
    ImageGather8hPck,
    ImageGetLod,
    ImageGetResinfo,
    ImageLoad,
    ImageLoadBy2,
    ImageLoadBy4,
    ImageLoadMip,
    ImageLoadMipBy2,
    ImageLoadMipBy4,
    ImageLoadMipPck,
    ImageLoadMipPck2,
    ImageLoadMipPck4,
    ImageLoadMipPckSgn,
    ImageLoadPck,
    ImageLoadPck2,
    ImageLoadPck4,
    ImageLoadPckSgn,
    ImageMsaaLoad,
    ImageSample,
    ImageSampleB,
    ImageSampleBCl,
    ImageSampleBClO,
    ImageSampleBO,
    ImageSampleC,
    ImageSampleCB,
    ImageSampleCBCl,
    ImageSampleCBClO,
    ImageSampleCBO,
    ImageSampleCCd,
    ImageSampleCCdCl,
    ImageSampleCCdClG16,
    ImageSampleCCdClO,
    ImageSampleCCdClOG16,
    ImageSampleCCdG16,
    ImageSampleCCdO,
    ImageSampleCCdOG16,
    ImageSampleCCl,
    ImageSampleCClO,
    ImageSampleCD_1,
    ImageSampleCDCl_1,
    ImageSampleCDClG16_1,
    ImageSampleCDClO_1,
    ImageSampleCDClOG16_1,
    ImageSampleCDG16_1,
    ImageSampleCDO_1,
    ImageSampleCDOG16_1,
    ImageSampleCL_1,
    ImageSampleCLO_1,
    ImageSampleCLz,
    ImageSampleCLzO,
    ImageSampleCO,
    ImageSampleCd_2,
    ImageSampleCdCl_2,
    ImageSampleCdClG16_2,
    ImageSampleCdClO_2,
    ImageSampleCdClOG16_2,
    ImageSampleCdG16_2,
    ImageSampleCdO_2,
    ImageSampleCdOG16_2,
    ImageSampleCl_2,
    ImageSampleClO_2,
    ImageSampleD,
    ImageSampleDCl,
    ImageSampleDClG16,
    ImageSampleDClO,
    ImageSampleDClOG16,
    ImageSampleDG16,
    ImageSampleDO,
    ImageSampleDOG16,
    ImageSampleL,
    ImageSampleLO,
    ImageSampleLz,
    ImageSampleLzO,
    ImageSampleO,
    ImageStore,
    ImageStoreBy2,
    ImageStoreBy4,
    ImageStoreMip,
    ImageStoreMipBy2,
    ImageStoreMipBy4,
    ImageStoreMipPck,
    ImageStoreMipPck2,
    ImageStoreMipPck4,
    ImageStorePck,
    ImageStorePck2,
    ImageStorePck4
  )
}
