// Generated from the pinned Mesa ACO and AMD machine-readable ISA inputs. Regenerate with: sbt codegen
package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.collection.*

@javax.annotation.processing.Generated(Array("sbt codegen"))
private object ImageInstructionDefs {
  case object ImageAtomicAdd extends Opcode("image_atomic_add", 680)

  case object ImageAtomicAddFlt extends Opcode("image_atomic_add_flt", 681)

  case object ImageAtomicAddUint extends Opcode("image_atomic_add_uint", 682)

  case object ImageAtomicAnd extends Opcode("image_atomic_and", 683)

  case object ImageAtomicCmpswap extends Opcode("image_atomic_cmpswap", 684)

  case object ImageAtomicDec extends Opcode("image_atomic_dec", 685)

  case object ImageAtomicDecUint extends Opcode("image_atomic_dec_uint", 686)

  case object ImageAtomicFcmpswap extends Opcode("image_atomic_fcmpswap", 687)

  case object ImageAtomicFmax extends Opcode("image_atomic_fmax", 688)

  case object ImageAtomicFmin extends Opcode("image_atomic_fmin", 689)

  case object ImageAtomicInc extends Opcode("image_atomic_inc", 690)

  case object ImageAtomicIncUint extends Opcode("image_atomic_inc_uint", 691)

  case object ImageAtomicMaxFlt extends Opcode("image_atomic_max_flt", 692)

  case object ImageAtomicMaxInt extends Opcode("image_atomic_max_int", 693)

  case object ImageAtomicMaxUint extends Opcode("image_atomic_max_uint", 694)

  case object ImageAtomicMinFlt extends Opcode("image_atomic_min_flt", 695)

  case object ImageAtomicMinInt extends Opcode("image_atomic_min_int", 696)

  case object ImageAtomicMinUint extends Opcode("image_atomic_min_uint", 697)

  case object ImageAtomicOr extends Opcode("image_atomic_or", 698)

  case object ImageAtomicPkAddBf16 extends Opcode("image_atomic_pk_add_bf16", 699)

  case object ImageAtomicPkAddF16 extends Opcode("image_atomic_pk_add_f16", 700)

  case object ImageAtomicRsub extends Opcode("image_atomic_rsub", 701)

  case object ImageAtomicSmax extends Opcode("image_atomic_smax", 702)

  case object ImageAtomicSmin extends Opcode("image_atomic_smin", 703)

  case object ImageAtomicSub extends Opcode("image_atomic_sub", 704)

  case object ImageAtomicSubUint extends Opcode("image_atomic_sub_uint", 705)

  case object ImageAtomicSwap extends Opcode("image_atomic_swap", 706)

  case object ImageAtomicUmax extends Opcode("image_atomic_umax", 707)

  case object ImageAtomicUmin extends Opcode("image_atomic_umin", 708)

  case object ImageAtomicXor extends Opcode("image_atomic_xor", 709)

  case object ImageBvh64IntersectRay extends Opcode("image_bvh64_intersect_ray", 710)

  case object ImageBvh8IntersectRay extends Opcode("image_bvh8_intersect_ray", 711)

  case object ImageBvhDualIntersectRay extends Opcode("image_bvh_dual_intersect_ray", 712)

  case object ImageBvhIntersectRay extends Opcode("image_bvh_intersect_ray", 713)

  case object ImageGather4 extends Opcode("image_gather4", 714)

  case object ImageGather4B extends Opcode("image_gather4_b", 715)

  case object ImageGather4BCl extends Opcode("image_gather4_b_cl", 716)

  case object ImageGather4BClO extends Opcode("image_gather4_b_cl_o", 717)

  case object ImageGather4BO extends Opcode("image_gather4_b_o", 718)

  case object ImageGather4C extends Opcode("image_gather4_c", 719)

  case object ImageGather4CB extends Opcode("image_gather4_c_b", 720)

  case object ImageGather4CBCl extends Opcode("image_gather4_c_b_cl", 721)

  case object ImageGather4CBClO extends Opcode("image_gather4_c_b_cl_o", 722)

  case object ImageGather4CBO extends Opcode("image_gather4_c_b_o", 723)

  case object ImageGather4CCl extends Opcode("image_gather4_c_cl", 724)

  case object ImageGather4CClO extends Opcode("image_gather4_c_cl_o", 725)

  case object ImageGather4CL_1 extends Opcode("image_gather4_c_l", 726)

  case object ImageGather4CLO_1 extends Opcode("image_gather4_c_l_o", 727)

  case object ImageGather4CLz extends Opcode("image_gather4_c_lz", 728)

  case object ImageGather4CLzO extends Opcode("image_gather4_c_lz_o", 729)

  case object ImageGather4CO extends Opcode("image_gather4_c_o", 730)

  case object ImageGather4Cl_2 extends Opcode("image_gather4_cl", 731)

  case object ImageGather4ClO_2 extends Opcode("image_gather4_cl_o", 732)

  case object ImageGather4L extends Opcode("image_gather4_l", 733)

  case object ImageGather4LO extends Opcode("image_gather4_l_o", 734)

  case object ImageGather4Lz extends Opcode("image_gather4_lz", 735)

  case object ImageGather4LzO extends Opcode("image_gather4_lz_o", 736)

  case object ImageGather4O extends Opcode("image_gather4_o", 737)

  case object ImageGather4h extends Opcode("image_gather4h", 738)

  case object ImageGather4hPck extends Opcode("image_gather4h_pck", 739)

  case object ImageGather8hPck extends Opcode("image_gather8h_pck", 740)

  case object ImageGetLod extends Opcode("image_get_lod", 741)

  case object ImageGetResinfo extends Opcode("image_get_resinfo", 742)

  case object ImageLoad extends Opcode("image_load", 743)

  case object ImageLoadBy2 extends Opcode("image_load_by2", 744)

  case object ImageLoadBy4 extends Opcode("image_load_by4", 745)

  case object ImageLoadMip extends Opcode("image_load_mip", 746)

  case object ImageLoadMipBy2 extends Opcode("image_load_mip_by2", 747)

  case object ImageLoadMipBy4 extends Opcode("image_load_mip_by4", 748)

  case object ImageLoadMipPck extends Opcode("image_load_mip_pck", 749)

  case object ImageLoadMipPck2 extends Opcode("image_load_mip_pck2", 750)

  case object ImageLoadMipPck4 extends Opcode("image_load_mip_pck4", 751)

  case object ImageLoadMipPckSgn extends Opcode("image_load_mip_pck_sgn", 752)

  case object ImageLoadPck extends Opcode("image_load_pck", 753)

  case object ImageLoadPck2 extends Opcode("image_load_pck2", 754)

  case object ImageLoadPck4 extends Opcode("image_load_pck4", 755)

  case object ImageLoadPckSgn extends Opcode("image_load_pck_sgn", 756)

  case object ImageMsaaLoad extends Opcode("image_msaa_load", 757)

  case object ImageSample extends Opcode("image_sample", 758)

  case object ImageSampleB extends Opcode("image_sample_b", 759)

  case object ImageSampleBCl extends Opcode("image_sample_b_cl", 760)

  case object ImageSampleBClO extends Opcode("image_sample_b_cl_o", 761)

  case object ImageSampleBO extends Opcode("image_sample_b_o", 762)

  case object ImageSampleC extends Opcode("image_sample_c", 763)

  case object ImageSampleCB extends Opcode("image_sample_c_b", 764)

  case object ImageSampleCBCl extends Opcode("image_sample_c_b_cl", 765)

  case object ImageSampleCBClO extends Opcode("image_sample_c_b_cl_o", 766)

  case object ImageSampleCBO extends Opcode("image_sample_c_b_o", 767)

  case object ImageSampleCCd extends Opcode("image_sample_c_cd", 768)

  case object ImageSampleCCdCl extends Opcode("image_sample_c_cd_cl", 769)

  case object ImageSampleCCdClG16 extends Opcode("image_sample_c_cd_cl_g16", 770)

  case object ImageSampleCCdClO extends Opcode("image_sample_c_cd_cl_o", 771)

  case object ImageSampleCCdClOG16 extends Opcode("image_sample_c_cd_cl_o_g16", 772)

  case object ImageSampleCCdG16 extends Opcode("image_sample_c_cd_g16", 773)

  case object ImageSampleCCdO extends Opcode("image_sample_c_cd_o", 774)

  case object ImageSampleCCdOG16 extends Opcode("image_sample_c_cd_o_g16", 775)

  case object ImageSampleCCl extends Opcode("image_sample_c_cl", 776)

  case object ImageSampleCClO extends Opcode("image_sample_c_cl_o", 777)

  case object ImageSampleCD_1 extends Opcode("image_sample_c_d", 778)

  case object ImageSampleCDCl_1 extends Opcode("image_sample_c_d_cl", 779)

  case object ImageSampleCDClG16_1 extends Opcode("image_sample_c_d_cl_g16", 780)

  case object ImageSampleCDClO_1 extends Opcode("image_sample_c_d_cl_o", 781)

  case object ImageSampleCDClOG16_1 extends Opcode("image_sample_c_d_cl_o_g16", 782)

  case object ImageSampleCDG16_1 extends Opcode("image_sample_c_d_g16", 783)

  case object ImageSampleCDO_1 extends Opcode("image_sample_c_d_o", 784)

  case object ImageSampleCDOG16_1 extends Opcode("image_sample_c_d_o_g16", 785)

  case object ImageSampleCL_1 extends Opcode("image_sample_c_l", 786)

  case object ImageSampleCLO_1 extends Opcode("image_sample_c_l_o", 787)

  case object ImageSampleCLz extends Opcode("image_sample_c_lz", 788)

  case object ImageSampleCLzO extends Opcode("image_sample_c_lz_o", 789)

  case object ImageSampleCO extends Opcode("image_sample_c_o", 790)

  case object ImageSampleCd_2 extends Opcode("image_sample_cd", 791)

  case object ImageSampleCdCl_2 extends Opcode("image_sample_cd_cl", 792)

  case object ImageSampleCdClG16_2 extends Opcode("image_sample_cd_cl_g16", 793)

  case object ImageSampleCdClO_2 extends Opcode("image_sample_cd_cl_o", 794)

  case object ImageSampleCdClOG16_2 extends Opcode("image_sample_cd_cl_o_g16", 795)

  case object ImageSampleCdG16_2 extends Opcode("image_sample_cd_g16", 796)

  case object ImageSampleCdO_2 extends Opcode("image_sample_cd_o", 797)

  case object ImageSampleCdOG16_2 extends Opcode("image_sample_cd_o_g16", 798)

  case object ImageSampleCl_2 extends Opcode("image_sample_cl", 799)

  case object ImageSampleClO_2 extends Opcode("image_sample_cl_o", 800)

  case object ImageSampleD extends Opcode("image_sample_d", 801)

  case object ImageSampleDCl extends Opcode("image_sample_d_cl", 802)

  case object ImageSampleDClG16 extends Opcode("image_sample_d_cl_g16", 803)

  case object ImageSampleDClO extends Opcode("image_sample_d_cl_o", 804)

  case object ImageSampleDClOG16 extends Opcode("image_sample_d_cl_o_g16", 805)

  case object ImageSampleDG16 extends Opcode("image_sample_d_g16", 806)

  case object ImageSampleDO extends Opcode("image_sample_d_o", 807)

  case object ImageSampleDOG16 extends Opcode("image_sample_d_o_g16", 808)

  case object ImageSampleL extends Opcode("image_sample_l", 809)

  case object ImageSampleLO extends Opcode("image_sample_l_o", 810)

  case object ImageSampleLz extends Opcode("image_sample_lz", 811)

  case object ImageSampleLzO extends Opcode("image_sample_lz_o", 812)

  case object ImageSampleO extends Opcode("image_sample_o", 813)

  case object ImageStore extends Opcode("image_store", 814)

  case object ImageStoreBy2 extends Opcode("image_store_by2", 815)

  case object ImageStoreBy4 extends Opcode("image_store_by4", 816)

  case object ImageStoreMip extends Opcode("image_store_mip", 817)

  case object ImageStoreMipBy2 extends Opcode("image_store_mip_by2", 818)

  case object ImageStoreMipBy4 extends Opcode("image_store_mip_by4", 819)

  case object ImageStoreMipPck extends Opcode("image_store_mip_pck", 820)

  case object ImageStoreMipPck2 extends Opcode("image_store_mip_pck2", 821)

  case object ImageStoreMipPck4 extends Opcode("image_store_mip_pck4", 822)

  case object ImageStorePck extends Opcode("image_store_pck", 823)

  case object ImageStorePck2 extends Opcode("image_store_pck2", 824)

  case object ImageStorePck4 extends Opcode("image_store_pck4", 825)

  val definitions: Vec[Opcode] = Vec(
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
