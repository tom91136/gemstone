// Generated from the pinned AMD ISA XML, LLVM tables and MC oracle. Regenerate with: sbt codegen

package net.kurobako.gemstone.isa.amdgpu.domain

import javax.annotation.processing.Generated

@Generated(Array("sbt codegen"))
object OperandDomains {
  val all: Vector[Domain[? <: DomainValue]] = Vector(
    Accvgpr,
    Attr,
    Dsmem,
    Exec,
    FlatScratch,
    Gpumem,
    Label,
    Param,
    Pc,
    Sdst,
    SdstExec,
    SdstM0,
    SdstNull,
    Sgpr,
    Simm16,
    Simm24,
    Simm32,
    Simm4,
    Simm5,
    Simm64,
    Simm8,
    Simm8At7,
    SmemOffset,
    SmemOffsetNok,
    Src,
    SrcAccvgpr,
    SrcAccvgprOrConst,
    SrcNoinline,
    SrcNolds,
    SrcNolit,
    SrcSimple,
    SrcVgpr,
    SrcVgprOrAccvgpr,
    SrcVgprOrAccvgprOrConst,
    SrcVgprOrInline,
    Sreg,
    SregLiteral,
    SregM0,
    SregM0Inl,
    SregNonull,
    SregNovcc,
    Ssrc,
    SsrcBarrierId,
    SsrcLanesel,
    SsrcNolds,
    SsrcNolit,
    SsrcSpecialScc,
    Tgt,
    Vcc,
    Vgpr,
    VgprOrAccvgpr,
    VgprOrLds
  )
}
