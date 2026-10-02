// Generated from the pinned AMDGPU instruction forms. Regenerate with: sbt codegen
package net.kurobako.gemstone.isa.amdgpu.asm
import net.kurobako.gemstone.isa.amdgpu.*
import Instruction.*
private object ImageInstructionForms {
  val decoders: Map[Opcode, Decoder] = Map(
    ImageInstructionDefs.ImageAtomicAdd ->
      decoder(form(3)((operands, mods) => Instruction.ImageAtomicAdd(operands(0), operands(1), operands(2), mods))),
    ImageInstructionDefs.ImageAtomicAddFlt ->
      decoder(form(3)((operands, mods) => Instruction.ImageAtomicAddFlt(operands(0), operands(1), operands(2), mods))),
    ImageInstructionDefs.ImageAtomicAddUint ->
      decoder(form(3)((operands, mods) => Instruction.ImageAtomicAddUint(operands(0), operands(1), operands(2), mods))),
    ImageInstructionDefs.ImageAtomicAnd ->
      decoder(form(3)((operands, mods) => Instruction.ImageAtomicAnd(operands(0), operands(1), operands(2), mods))),
    ImageInstructionDefs.ImageAtomicCmpswap ->
      decoder(form(3)((operands, mods) => Instruction.ImageAtomicCmpswap(operands(0), operands(1), operands(2), mods))),
    ImageInstructionDefs.ImageAtomicDec ->
      decoder(form(3)((operands, mods) => Instruction.ImageAtomicDec(operands(0), operands(1), operands(2), mods))),
    ImageInstructionDefs.ImageAtomicDecUint ->
      decoder(form(3)((operands, mods) => Instruction.ImageAtomicDecUint(operands(0), operands(1), operands(2), mods))),
    ImageInstructionDefs.ImageAtomicFcmpswap -> decoder(form(3)((operands, mods) =>
      Instruction.ImageAtomicFcmpswap(operands(0), operands(1), operands(2), mods)
    )),
    ImageInstructionDefs.ImageAtomicFmax ->
      decoder(form(3)((operands, mods) => Instruction.ImageAtomicFmax(operands(0), operands(1), operands(2), mods))),
    ImageInstructionDefs.ImageAtomicFmin ->
      decoder(form(3)((operands, mods) => Instruction.ImageAtomicFmin(operands(0), operands(1), operands(2), mods))),
    ImageInstructionDefs.ImageAtomicInc ->
      decoder(form(3)((operands, mods) => Instruction.ImageAtomicInc(operands(0), operands(1), operands(2), mods))),
    ImageInstructionDefs.ImageAtomicIncUint ->
      decoder(form(3)((operands, mods) => Instruction.ImageAtomicIncUint(operands(0), operands(1), operands(2), mods))),
    ImageInstructionDefs.ImageAtomicMaxFlt ->
      decoder(form(3)((operands, mods) => Instruction.ImageAtomicMaxFlt(operands(0), operands(1), operands(2), mods))),
    ImageInstructionDefs.ImageAtomicMaxInt ->
      decoder(form(3)((operands, mods) => Instruction.ImageAtomicMaxInt(operands(0), operands(1), operands(2), mods))),
    ImageInstructionDefs.ImageAtomicMaxUint ->
      decoder(form(3)((operands, mods) => Instruction.ImageAtomicMaxUint(operands(0), operands(1), operands(2), mods))),
    ImageInstructionDefs.ImageAtomicMinFlt ->
      decoder(form(3)((operands, mods) => Instruction.ImageAtomicMinFlt(operands(0), operands(1), operands(2), mods))),
    ImageInstructionDefs.ImageAtomicMinInt ->
      decoder(form(3)((operands, mods) => Instruction.ImageAtomicMinInt(operands(0), operands(1), operands(2), mods))),
    ImageInstructionDefs.ImageAtomicMinUint ->
      decoder(form(3)((operands, mods) => Instruction.ImageAtomicMinUint(operands(0), operands(1), operands(2), mods))),
    ImageInstructionDefs.ImageAtomicOr ->
      decoder(form(3)((operands, mods) => Instruction.ImageAtomicOr(operands(0), operands(1), operands(2), mods))),
    ImageInstructionDefs.ImageAtomicPkAddBf16 -> decoder(form(3)((operands, mods) =>
      Instruction.ImageAtomicPkAddBf16(operands(0), operands(1), operands(2), mods)
    )),
    ImageInstructionDefs.ImageAtomicPkAddF16 -> decoder(form(3)((operands, mods) =>
      Instruction.ImageAtomicPkAddF16(operands(0), operands(1), operands(2), mods)
    )),
    ImageInstructionDefs.ImageAtomicRsub ->
      decoder(form(3)((operands, mods) => Instruction.ImageAtomicRsub(operands(0), operands(1), operands(2), mods))),
    ImageInstructionDefs.ImageAtomicSmax ->
      decoder(form(3)((operands, mods) => Instruction.ImageAtomicSmax(operands(0), operands(1), operands(2), mods))),
    ImageInstructionDefs.ImageAtomicSmin ->
      decoder(form(3)((operands, mods) => Instruction.ImageAtomicSmin(operands(0), operands(1), operands(2), mods))),
    ImageInstructionDefs.ImageAtomicSub ->
      decoder(form(3)((operands, mods) => Instruction.ImageAtomicSub(operands(0), operands(1), operands(2), mods))),
    ImageInstructionDefs.ImageAtomicSubUint ->
      decoder(form(3)((operands, mods) => Instruction.ImageAtomicSubUint(operands(0), operands(1), operands(2), mods))),
    ImageInstructionDefs.ImageAtomicSwap ->
      decoder(form(3)((operands, mods) => Instruction.ImageAtomicSwap(operands(0), operands(1), operands(2), mods))),
    ImageInstructionDefs.ImageAtomicUmax ->
      decoder(form(3)((operands, mods) => Instruction.ImageAtomicUmax(operands(0), operands(1), operands(2), mods))),
    ImageInstructionDefs.ImageAtomicUmin ->
      decoder(form(3)((operands, mods) => Instruction.ImageAtomicUmin(operands(0), operands(1), operands(2), mods))),
    ImageInstructionDefs.ImageAtomicXor ->
      decoder(form(3)((operands, mods) => Instruction.ImageAtomicXor(operands(0), operands(1), operands(2), mods))),
    ImageInstructionDefs.ImageBvh64IntersectRay -> decoder(form(3)((operands, mods) =>
      Instruction.ImageBvh64IntersectRay(operands(0), operands(1), operands(2), mods)
    )),
    ImageInstructionDefs.ImageBvh8IntersectRay -> decoder(form(3)((operands, mods) =>
      Instruction.ImageBvh8IntersectRay(operands(0), operands(1), operands(2), mods)
    )),
    ImageInstructionDefs.ImageBvhDualIntersectRay -> decoder(form(3)((operands, mods) =>
      Instruction.ImageBvhDualIntersectRay(operands(0), operands(1), operands(2), mods)
    )),
    ImageInstructionDefs.ImageBvhIntersectRay -> decoder(form(3)((operands, mods) =>
      Instruction.ImageBvhIntersectRay(operands(0), operands(1), operands(2), mods)
    )),
    ImageInstructionDefs.ImageGather4 -> decoder(form(4)((operands, mods) =>
      Instruction.ImageGather4(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    ImageInstructionDefs.ImageGather4B -> decoder(form(4)((operands, mods) =>
      Instruction.ImageGather4B(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    ImageInstructionDefs.ImageGather4BCl -> decoder(form(4)((operands, mods) =>
      Instruction.ImageGather4BCl(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    ImageInstructionDefs.ImageGather4BClO -> decoder(form(4)((operands, mods) =>
      Instruction.ImageGather4BClO(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    ImageInstructionDefs.ImageGather4BO -> decoder(form(4)((operands, mods) =>
      Instruction.ImageGather4BO(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    ImageInstructionDefs.ImageGather4C -> decoder(form(4)((operands, mods) =>
      Instruction.ImageGather4C(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    ImageInstructionDefs.ImageGather4CB -> decoder(form(4)((operands, mods) =>
      Instruction.ImageGather4CB(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    ImageInstructionDefs.ImageGather4CBCl -> decoder(form(4)((operands, mods) =>
      Instruction.ImageGather4CBCl(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    ImageInstructionDefs.ImageGather4CBClO -> decoder(form(4)((operands, mods) =>
      Instruction.ImageGather4CBClO(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    ImageInstructionDefs.ImageGather4CBO -> decoder(form(4)((operands, mods) =>
      Instruction.ImageGather4CBO(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    ImageInstructionDefs.ImageGather4CCl -> decoder(form(4)((operands, mods) =>
      Instruction.ImageGather4CCl(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    ImageInstructionDefs.ImageGather4CClO -> decoder(form(4)((operands, mods) =>
      Instruction.ImageGather4CClO(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    ImageInstructionDefs.ImageGather4CL_1 -> decoder(form(4)((operands, mods) =>
      Instruction.ImageGather4CL_1(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    ImageInstructionDefs.ImageGather4CLO_1 -> decoder(form(4)((operands, mods) =>
      Instruction.ImageGather4CLO_1(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    ImageInstructionDefs.ImageGather4CLz -> decoder(form(4)((operands, mods) =>
      Instruction.ImageGather4CLz(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    ImageInstructionDefs.ImageGather4CLzO -> decoder(form(4)((operands, mods) =>
      Instruction.ImageGather4CLzO(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    ImageInstructionDefs.ImageGather4CO -> decoder(form(4)((operands, mods) =>
      Instruction.ImageGather4CO(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    ImageInstructionDefs.ImageGather4Cl_2 -> decoder(form(4)((operands, mods) =>
      Instruction.ImageGather4Cl_2(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    ImageInstructionDefs.ImageGather4ClO_2 -> decoder(form(4)((operands, mods) =>
      Instruction.ImageGather4ClO_2(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    ImageInstructionDefs.ImageGather4L -> decoder(form(4)((operands, mods) =>
      Instruction.ImageGather4L(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    ImageInstructionDefs.ImageGather4LO -> decoder(form(4)((operands, mods) =>
      Instruction.ImageGather4LO(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    ImageInstructionDefs.ImageGather4Lz -> decoder(form(4)((operands, mods) =>
      Instruction.ImageGather4Lz(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    ImageInstructionDefs.ImageGather4LzO -> decoder(form(4)((operands, mods) =>
      Instruction.ImageGather4LzO(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    ImageInstructionDefs.ImageGather4O -> decoder(form(4)((operands, mods) =>
      Instruction.ImageGather4O(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    ImageInstructionDefs.ImageGather4h -> decoder(form(4)((operands, mods) =>
      Instruction.ImageGather4h(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    ImageInstructionDefs.ImageGather4hPck -> decoder(form(4)((operands, mods) =>
      Instruction.ImageGather4hPck(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    ImageInstructionDefs.ImageGather8hPck -> decoder(form(4)((operands, mods) =>
      Instruction.ImageGather8hPck(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    ImageInstructionDefs.ImageGetLod -> decoder(form(4)((operands, mods) =>
      Instruction.ImageGetLod(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    ImageInstructionDefs.ImageGetResinfo ->
      decoder(form(3)((operands, mods) => Instruction.ImageGetResinfo(operands(0), operands(1), operands(2), mods))),
    ImageInstructionDefs.ImageLoad ->
      decoder(form(3)((operands, mods) => Instruction.ImageLoad(operands(0), operands(1), operands(2), mods))),
    ImageInstructionDefs.ImageLoadBy2 ->
      decoder(form(3)((operands, mods) => Instruction.ImageLoadBy2(operands(0), operands(1), operands(2), mods))),
    ImageInstructionDefs.ImageLoadBy4 ->
      decoder(form(3)((operands, mods) => Instruction.ImageLoadBy4(operands(0), operands(1), operands(2), mods))),
    ImageInstructionDefs.ImageLoadMip ->
      decoder(form(3)((operands, mods) => Instruction.ImageLoadMip(operands(0), operands(1), operands(2), mods))),
    ImageInstructionDefs.ImageLoadMipBy2 ->
      decoder(form(3)((operands, mods) => Instruction.ImageLoadMipBy2(operands(0), operands(1), operands(2), mods))),
    ImageInstructionDefs.ImageLoadMipBy4 ->
      decoder(form(3)((operands, mods) => Instruction.ImageLoadMipBy4(operands(0), operands(1), operands(2), mods))),
    ImageInstructionDefs.ImageLoadMipPck ->
      decoder(form(3)((operands, mods) => Instruction.ImageLoadMipPck(operands(0), operands(1), operands(2), mods))),
    ImageInstructionDefs.ImageLoadMipPck2 ->
      decoder(form(3)((operands, mods) => Instruction.ImageLoadMipPck2(operands(0), operands(1), operands(2), mods))),
    ImageInstructionDefs.ImageLoadMipPck4 ->
      decoder(form(3)((operands, mods) => Instruction.ImageLoadMipPck4(operands(0), operands(1), operands(2), mods))),
    ImageInstructionDefs.ImageLoadMipPckSgn ->
      decoder(form(3)((operands, mods) => Instruction.ImageLoadMipPckSgn(operands(0), operands(1), operands(2), mods))),
    ImageInstructionDefs.ImageLoadPck ->
      decoder(form(3)((operands, mods) => Instruction.ImageLoadPck(operands(0), operands(1), operands(2), mods))),
    ImageInstructionDefs.ImageLoadPck2 ->
      decoder(form(3)((operands, mods) => Instruction.ImageLoadPck2(operands(0), operands(1), operands(2), mods))),
    ImageInstructionDefs.ImageLoadPck4 ->
      decoder(form(3)((operands, mods) => Instruction.ImageLoadPck4(operands(0), operands(1), operands(2), mods))),
    ImageInstructionDefs.ImageLoadPckSgn ->
      decoder(form(3)((operands, mods) => Instruction.ImageLoadPckSgn(operands(0), operands(1), operands(2), mods))),
    ImageInstructionDefs.ImageMsaaLoad ->
      decoder(form(3)((operands, mods) => Instruction.ImageMsaaLoad(operands(0), operands(1), operands(2), mods))),
    ImageInstructionDefs.ImageSample -> decoder(form(4)((operands, mods) =>
      Instruction.ImageSample(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    ImageInstructionDefs.ImageSampleB -> decoder(form(4)((operands, mods) =>
      Instruction.ImageSampleB(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    ImageInstructionDefs.ImageSampleBCl -> decoder(form(4)((operands, mods) =>
      Instruction.ImageSampleBCl(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    ImageInstructionDefs.ImageSampleBClO -> decoder(form(4)((operands, mods) =>
      Instruction.ImageSampleBClO(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    ImageInstructionDefs.ImageSampleBO -> decoder(form(4)((operands, mods) =>
      Instruction.ImageSampleBO(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    ImageInstructionDefs.ImageSampleC -> decoder(form(4)((operands, mods) =>
      Instruction.ImageSampleC(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    ImageInstructionDefs.ImageSampleCB -> decoder(form(4)((operands, mods) =>
      Instruction.ImageSampleCB(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    ImageInstructionDefs.ImageSampleCBCl -> decoder(form(4)((operands, mods) =>
      Instruction.ImageSampleCBCl(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    ImageInstructionDefs.ImageSampleCBClO -> decoder(form(4)((operands, mods) =>
      Instruction.ImageSampleCBClO(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    ImageInstructionDefs.ImageSampleCBO -> decoder(form(4)((operands, mods) =>
      Instruction.ImageSampleCBO(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    ImageInstructionDefs.ImageSampleCCd -> decoder(form(4)((operands, mods) =>
      Instruction.ImageSampleCCd(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    ImageInstructionDefs.ImageSampleCCdCl -> decoder(form(4)((operands, mods) =>
      Instruction.ImageSampleCCdCl(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    ImageInstructionDefs.ImageSampleCCdClG16 -> decoder(form(4)((operands, mods) =>
      Instruction.ImageSampleCCdClG16(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    ImageInstructionDefs.ImageSampleCCdClO -> decoder(form(4)((operands, mods) =>
      Instruction.ImageSampleCCdClO(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    ImageInstructionDefs.ImageSampleCCdClOG16 -> decoder(form(4)((operands, mods) =>
      Instruction.ImageSampleCCdClOG16(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    ImageInstructionDefs.ImageSampleCCdG16 -> decoder(form(4)((operands, mods) =>
      Instruction.ImageSampleCCdG16(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    ImageInstructionDefs.ImageSampleCCdO -> decoder(form(4)((operands, mods) =>
      Instruction.ImageSampleCCdO(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    ImageInstructionDefs.ImageSampleCCdOG16 -> decoder(form(4)((operands, mods) =>
      Instruction.ImageSampleCCdOG16(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    ImageInstructionDefs.ImageSampleCCl -> decoder(form(4)((operands, mods) =>
      Instruction.ImageSampleCCl(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    ImageInstructionDefs.ImageSampleCClO -> decoder(form(4)((operands, mods) =>
      Instruction.ImageSampleCClO(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    ImageInstructionDefs.ImageSampleCD_1 -> decoder(form(4)((operands, mods) =>
      Instruction.ImageSampleCD_1(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    ImageInstructionDefs.ImageSampleCDCl_1 -> decoder(form(4)((operands, mods) =>
      Instruction.ImageSampleCDCl_1(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    ImageInstructionDefs.ImageSampleCDClG16_1 -> decoder(form(4)((operands, mods) =>
      Instruction.ImageSampleCDClG16_1(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    ImageInstructionDefs.ImageSampleCDClO_1 -> decoder(form(4)((operands, mods) =>
      Instruction.ImageSampleCDClO_1(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    ImageInstructionDefs.ImageSampleCDClOG16_1 -> decoder(form(4)((operands, mods) =>
      Instruction.ImageSampleCDClOG16_1(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    ImageInstructionDefs.ImageSampleCDG16_1 -> decoder(form(4)((operands, mods) =>
      Instruction.ImageSampleCDG16_1(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    ImageInstructionDefs.ImageSampleCDO_1 -> decoder(form(4)((operands, mods) =>
      Instruction.ImageSampleCDO_1(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    ImageInstructionDefs.ImageSampleCDOG16_1 -> decoder(form(4)((operands, mods) =>
      Instruction.ImageSampleCDOG16_1(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    ImageInstructionDefs.ImageSampleCL_1 -> decoder(form(4)((operands, mods) =>
      Instruction.ImageSampleCL_1(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    ImageInstructionDefs.ImageSampleCLO_1 -> decoder(form(4)((operands, mods) =>
      Instruction.ImageSampleCLO_1(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    ImageInstructionDefs.ImageSampleCLz -> decoder(form(4)((operands, mods) =>
      Instruction.ImageSampleCLz(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    ImageInstructionDefs.ImageSampleCLzO -> decoder(form(4)((operands, mods) =>
      Instruction.ImageSampleCLzO(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    ImageInstructionDefs.ImageSampleCO -> decoder(form(4)((operands, mods) =>
      Instruction.ImageSampleCO(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    ImageInstructionDefs.ImageSampleCd_2 -> decoder(form(4)((operands, mods) =>
      Instruction.ImageSampleCd_2(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    ImageInstructionDefs.ImageSampleCdCl_2 -> decoder(form(4)((operands, mods) =>
      Instruction.ImageSampleCdCl_2(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    ImageInstructionDefs.ImageSampleCdClG16_2 -> decoder(form(4)((operands, mods) =>
      Instruction.ImageSampleCdClG16_2(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    ImageInstructionDefs.ImageSampleCdClO_2 -> decoder(form(4)((operands, mods) =>
      Instruction.ImageSampleCdClO_2(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    ImageInstructionDefs.ImageSampleCdClOG16_2 -> decoder(form(4)((operands, mods) =>
      Instruction.ImageSampleCdClOG16_2(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    ImageInstructionDefs.ImageSampleCdG16_2 -> decoder(form(4)((operands, mods) =>
      Instruction.ImageSampleCdG16_2(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    ImageInstructionDefs.ImageSampleCdO_2 -> decoder(form(4)((operands, mods) =>
      Instruction.ImageSampleCdO_2(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    ImageInstructionDefs.ImageSampleCdOG16_2 -> decoder(form(4)((operands, mods) =>
      Instruction.ImageSampleCdOG16_2(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    ImageInstructionDefs.ImageSampleCl_2 -> decoder(form(4)((operands, mods) =>
      Instruction.ImageSampleCl_2(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    ImageInstructionDefs.ImageSampleClO_2 -> decoder(form(4)((operands, mods) =>
      Instruction.ImageSampleClO_2(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    ImageInstructionDefs.ImageSampleD -> decoder(form(4)((operands, mods) =>
      Instruction.ImageSampleD(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    ImageInstructionDefs.ImageSampleDCl -> decoder(form(4)((operands, mods) =>
      Instruction.ImageSampleDCl(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    ImageInstructionDefs.ImageSampleDClG16 -> decoder(form(4)((operands, mods) =>
      Instruction.ImageSampleDClG16(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    ImageInstructionDefs.ImageSampleDClO -> decoder(form(4)((operands, mods) =>
      Instruction.ImageSampleDClO(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    ImageInstructionDefs.ImageSampleDClOG16 -> decoder(form(4)((operands, mods) =>
      Instruction.ImageSampleDClOG16(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    ImageInstructionDefs.ImageSampleDG16 -> decoder(form(4)((operands, mods) =>
      Instruction.ImageSampleDG16(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    ImageInstructionDefs.ImageSampleDO -> decoder(form(4)((operands, mods) =>
      Instruction.ImageSampleDO(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    ImageInstructionDefs.ImageSampleDOG16 -> decoder(form(4)((operands, mods) =>
      Instruction.ImageSampleDOG16(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    ImageInstructionDefs.ImageSampleL -> decoder(form(4)((operands, mods) =>
      Instruction.ImageSampleL(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    ImageInstructionDefs.ImageSampleLO -> decoder(form(4)((operands, mods) =>
      Instruction.ImageSampleLO(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    ImageInstructionDefs.ImageSampleLz -> decoder(form(4)((operands, mods) =>
      Instruction.ImageSampleLz(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    ImageInstructionDefs.ImageSampleLzO -> decoder(form(4)((operands, mods) =>
      Instruction.ImageSampleLzO(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    ImageInstructionDefs.ImageSampleO -> decoder(form(4)((operands, mods) =>
      Instruction.ImageSampleO(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    ImageInstructionDefs.ImageStore ->
      decoder(form(3)((operands, mods) => Instruction.ImageStore(operands(0), operands(1), operands(2), mods))),
    ImageInstructionDefs.ImageStoreBy2 ->
      decoder(form(3)((operands, mods) => Instruction.ImageStoreBy2(operands(0), operands(1), operands(2), mods))),
    ImageInstructionDefs.ImageStoreBy4 ->
      decoder(form(3)((operands, mods) => Instruction.ImageStoreBy4(operands(0), operands(1), operands(2), mods))),
    ImageInstructionDefs.ImageStoreMip ->
      decoder(form(3)((operands, mods) => Instruction.ImageStoreMip(operands(0), operands(1), operands(2), mods))),
    ImageInstructionDefs.ImageStoreMipBy2 ->
      decoder(form(3)((operands, mods) => Instruction.ImageStoreMipBy2(operands(0), operands(1), operands(2), mods))),
    ImageInstructionDefs.ImageStoreMipBy4 ->
      decoder(form(3)((operands, mods) => Instruction.ImageStoreMipBy4(operands(0), operands(1), operands(2), mods))),
    ImageInstructionDefs.ImageStoreMipPck ->
      decoder(form(3)((operands, mods) => Instruction.ImageStoreMipPck(operands(0), operands(1), operands(2), mods))),
    ImageInstructionDefs.ImageStoreMipPck2 ->
      decoder(form(3)((operands, mods) => Instruction.ImageStoreMipPck2(operands(0), operands(1), operands(2), mods))),
    ImageInstructionDefs.ImageStoreMipPck4 ->
      decoder(form(3)((operands, mods) => Instruction.ImageStoreMipPck4(operands(0), operands(1), operands(2), mods))),
    ImageInstructionDefs.ImageStorePck ->
      decoder(form(3)((operands, mods) => Instruction.ImageStorePck(operands(0), operands(1), operands(2), mods))),
    ImageInstructionDefs.ImageStorePck2 ->
      decoder(form(3)((operands, mods) => Instruction.ImageStorePck2(operands(0), operands(1), operands(2), mods))),
    ImageInstructionDefs.ImageStorePck4 ->
      decoder(form(3)((operands, mods) => Instruction.ImageStorePck4(operands(0), operands(1), operands(2), mods)))
  )
}
