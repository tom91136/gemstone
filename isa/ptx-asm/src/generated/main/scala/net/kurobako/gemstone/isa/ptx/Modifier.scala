// Generated from the pinned PTX syntax domains. Regenerate with: sbt codegen
package net.kurobako.gemstone.isa.ptx
import net.kurobako.gemstone.constraint.*

object Modifier {
  private lazy val byName: Map[String, Modifier] = Map(
    "128x128b" -> N128x128b,
    "128x256b" -> N128x256b,
    "16x128b" -> N16x128b,
    "16x256b" -> N16x256b,
    "16x32bx2" -> N16x32bx2,
    "16x64b" -> N16x64b,
    "1d" -> N1d,
    "2d" -> N2d,
    "2dms" -> N2dms,
    "32x32b" -> N32x32b,
    "3d" -> N3d,
    "4d" -> N4d,
    "4x256b" -> N4x256b,
    "5d" -> N5d,
    "L1" -> L1,
    "L1::evict_first" -> L1EvictFirst,
    "L1::evict_last" -> L1EvictLast,
    "L1::evict_normal" -> L1EvictNormal,
    "L1::evict_unchanged" -> L1EvictUnchanged,
    "L1::no_allocate" -> L1NoAllocate,
    "L2" -> L2,
    "L2::128B" -> L2128B,
    "L2::256B" -> L2256B,
    "L2::64B" -> L264B,
    "L2::cache_hint" -> L2CacheHint,
    "L2::evict_first" -> L2EvictFirst,
    "L2::evict_last" -> L2EvictLast,
    "L2::evict_normal" -> L2EvictNormal,
    "L2::evict_unchanged" -> L2EvictUnchanged,
    "NaN" -> NaN,
    "a" -> A,
    "a1d" -> A1d,
    "a2d" -> A2d,
    "a2dms" -> A2dms,
    "abs" -> Abs,
    "acc::f16" -> AccF16,
    "acc::f32" -> AccF32,
    "acq_rel" -> AcqRel,
    "acquire" -> Acquire,
    "acube" -> Acube,
    "add" -> Add,
    "addr_mode_0" -> AddrMode0,
    "addr_mode_1" -> AddrMode1,
    "addr_mode_2" -> AddrMode2,
    "alias" -> Alias,
    "aligned" -> Aligned,
    "all" -> All,
    "alloc" -> Alloc,
    "and" -> And,
    "any" -> Any,
    "approx" -> Approx,
    "array_size" -> ArraySize,
    "arrive" -> Arrive,
    "arrive_drop" -> ArriveDrop,
    "ashift" -> Ashift,
    "async" -> Async,
    "async::generic" -> AsyncGeneric,
    "b" -> B,
    "b1" -> B1,
    "b1024" -> B1024,
    "b128" -> B128,
    "b16" -> B16,
    "b32" -> B32,
    "b4e" -> B4e,
    "b4x16_p64" -> B4x16P64,
    "b64" -> B64,
    "b6x16_p32" -> B6x16P32,
    "b8" -> B8,
    "b8x16" -> B8x16,
    "ballot" -> Ballot,
    "base" -> Base,
    "bf16" -> Bf16,
    "bf16x2" -> Bf16x2,
    "bfly" -> Bfly,
    "block16" -> Block16,
    "block32" -> Block32,
    "block_scale" -> BlockScale,
    "box_dim" -> BoxDim,
    "bulk" -> Bulk,
    "bulk_group" -> BulkGroup,
    "c" -> C,
    "ca" -> Ca,
    "cas" -> Cas,
    "cc" -> Cc,
    "cg" -> Cg,
    "channel_data_type" -> ChannelDataType,
    "channel_order" -> ChannelOrder,
    "check_layout" -> CheckLayout,
    "clamp" -> Clamp,
    "cluster" -> Cluster,
    "col" -> Col,
    "collector::buffer::op" -> CollectorBufferOp,
    "commit" -> Commit,
    "commit_group" -> CommitGroup,
    "complete_tx" -> CompleteTx,
    "const" -> Const,
    "cp" -> Cp,
    "cp_fenceproxy" -> CpFenceproxy,
    "cp_mask" -> CpMask,
    "cs" -> Cs,
    "cta" -> Cta,
    "cta_group::1" -> CtaGroup1,
    "cta_group::2" -> CtaGroup2,
    "cube" -> Cube,
    "cv" -> Cv,
    "cvt" -> Cvt,
    "d" -> D,
    "dealloc" -> Dealloc,
    "dec" -> Dec,
    "depth" -> Depth,
    "down" -> Down,
    "e2m1" -> E2m1,
    "e2m1x2" -> E2m1x2,
    "e2m1x4" -> E2m1x4,
    "e2m3" -> E2m3,
    "e2m3x2" -> E2m3x2,
    "e2m3x4" -> E2m3x4,
    "e3m2" -> E3m2,
    "e3m2x2" -> E3m2x2,
    "e3m2x4" -> E3m2x4,
    "e4m3" -> E4m3,
    "e4m3x2" -> E4m3x2,
    "e4m3x4" -> E4m3x4,
    "e5m2" -> E5m2,
    "e5m2x2" -> E5m2x2,
    "e5m2x4" -> E5m2x4,
    "ecl" -> Ecl,
    "ecr" -> Ecr,
    "element_stride" -> ElementStride,
    "elemtype" -> Elemtype,
    "eq" -> Eq,
    "equ" -> Equ,
    "exch" -> Exch,
    "expect_tx" -> ExpectTx,
    "f16" -> F16,
    "f16x2" -> F16x2,
    "f32" -> F32,
    "f32x2" -> F32x2,
    "f4e" -> F4e,
    "f64" -> F64,
    "fabric::fabric" -> FabricFabric,
    "fabric::generic" -> FabricGeneric,
    "fence" -> Fence,
    "fence::after_thread_sync" -> FenceAfterThreadSync,
    "fence::before_thread_sync" -> FenceBeforeThreadSync,
    "fill_mode" -> FillMode,
    "filter_mode" -> FilterMode,
    "finite" -> Finite,
    "force_unnormalized_coords" -> ForceUnnormalizedCoords,
    "fractional" -> Fractional,
    "ftz" -> Ftz,
    "full" -> Full,
    "g" -> G,
    "ge" -> Ge,
    "generic::fabric" -> GenericFabric,
    "get_first_ctaid" -> GetFirstCtaid,
    "get_first_ctaid::x" -> GetFirstCtaidX,
    "get_first_ctaid::y" -> GetFirstCtaidY,
    "get_first_ctaid::z" -> GetFirstCtaidZ,
    "geu" -> Geu,
    "gl" -> Gl,
    "global" -> Global,
    "global_address" -> GlobalAddress,
    "global_dim" -> GlobalDim,
    "global_stride" -> GlobalStride,
    "gpu" -> Gpu,
    "grad" -> Grad,
    "gt" -> Gt,
    "gtu" -> Gtu,
    "height" -> Height,
    "hi" -> Hi,
    "hs" -> Hs,
    "idx" -> Idx,
    "ignore_oob" -> IgnoreOob,
    "im2col" -> Im2col,
    "im2col::w" -> Im2colW,
    "im2col::w::128" -> Im2colW128,
    "im2col_no_offs" -> Im2colNoOffs,
    "inc" -> Inc,
    "infinite" -> Infinite,
    "init" -> Init,
    "interleave_layout" -> InterleaveLayout,
    "inval" -> Inval,
    "is_canceled" -> IsCanceled,
    "kind::f16" -> KindF16,
    "kind::f8f6f4" -> KindF8f6f4,
    "kind::i8" -> KindI8,
    "kind::mxf4" -> KindMxf4,
    "kind::mxf4nvf4" -> KindMxf4nvf4,
    "kind::mxf8f6f4" -> KindMxf8f6f4,
    "kind::tf32" -> KindTf32,
    "l" -> L,
    "launch_dependents" -> LaunchDependents,
    "layout::v0" -> LayoutV0,
    "layout::v1" -> LayoutV1,
    "ld" -> Ld,
    "ld_reduce" -> LdReduce,
    "le" -> Le,
    "leu" -> Leu,
    "level" -> Level,
    "level::cache_hint" -> LevelCacheHint,
    "lo" -> Lo,
    "load" -> Load,
    "local" -> Local,
    "ls" -> Ls,
    "lt" -> Lt,
    "ltu" -> Ltu,
    "lu" -> Lu,
    "m16n16" -> M16n16,
    "m16n16k16" -> M16n16k16,
    "m16n16k8" -> M16n16k8,
    "m16n8" -> M16n8,
    "m16n8k128" -> M16n8k128,
    "m16n8k16" -> M16n8k16,
    "m16n8k256" -> M16n8k256,
    "m16n8k32" -> M16n8k32,
    "m16n8k4" -> M16n8k4,
    "m16n8k64" -> M16n8k64,
    "m16n8k8" -> M16n8k8,
    "m32n8k16" -> M32n8k16,
    "m64n104k16" -> M64n104k16,
    "m64n104k32" -> M64n104k32,
    "m64n104k64" -> M64n104k64,
    "m64n104k8" -> M64n104k8,
    "m64n112k16" -> M64n112k16,
    "m64n112k256" -> M64n112k256,
    "m64n112k32" -> M64n112k32,
    "m64n112k64" -> M64n112k64,
    "m64n112k8" -> M64n112k8,
    "m64n120k16" -> M64n120k16,
    "m64n120k32" -> M64n120k32,
    "m64n120k64" -> M64n120k64,
    "m64n120k8" -> M64n120k8,
    "m64n128k16" -> M64n128k16,
    "m64n128k256" -> M64n128k256,
    "m64n128k32" -> M64n128k32,
    "m64n128k64" -> M64n128k64,
    "m64n128k8" -> M64n128k8,
    "m64n136k16" -> M64n136k16,
    "m64n136k32" -> M64n136k32,
    "m64n136k64" -> M64n136k64,
    "m64n136k8" -> M64n136k8,
    "m64n144k16" -> M64n144k16,
    "m64n144k256" -> M64n144k256,
    "m64n144k32" -> M64n144k32,
    "m64n144k64" -> M64n144k64,
    "m64n144k8" -> M64n144k8,
    "m64n152k16" -> M64n152k16,
    "m64n152k32" -> M64n152k32,
    "m64n152k64" -> M64n152k64,
    "m64n152k8" -> M64n152k8,
    "m64n160k16" -> M64n160k16,
    "m64n160k256" -> M64n160k256,
    "m64n160k32" -> M64n160k32,
    "m64n160k64" -> M64n160k64,
    "m64n160k8" -> M64n160k8,
    "m64n168k16" -> M64n168k16,
    "m64n168k32" -> M64n168k32,
    "m64n168k64" -> M64n168k64,
    "m64n168k8" -> M64n168k8,
    "m64n16k16" -> M64n16k16,
    "m64n16k256" -> M64n16k256,
    "m64n16k32" -> M64n16k32,
    "m64n16k64" -> M64n16k64,
    "m64n16k8" -> M64n16k8,
    "m64n176k16" -> M64n176k16,
    "m64n176k256" -> M64n176k256,
    "m64n176k32" -> M64n176k32,
    "m64n176k64" -> M64n176k64,
    "m64n176k8" -> M64n176k8,
    "m64n184k16" -> M64n184k16,
    "m64n184k32" -> M64n184k32,
    "m64n184k64" -> M64n184k64,
    "m64n184k8" -> M64n184k8,
    "m64n192k16" -> M64n192k16,
    "m64n192k256" -> M64n192k256,
    "m64n192k32" -> M64n192k32,
    "m64n192k64" -> M64n192k64,
    "m64n192k8" -> M64n192k8,
    "m64n200k16" -> M64n200k16,
    "m64n200k32" -> M64n200k32,
    "m64n200k64" -> M64n200k64,
    "m64n200k8" -> M64n200k8,
    "m64n208k16" -> M64n208k16,
    "m64n208k256" -> M64n208k256,
    "m64n208k32" -> M64n208k32,
    "m64n208k64" -> M64n208k64,
    "m64n208k8" -> M64n208k8,
    "m64n216k16" -> M64n216k16,
    "m64n216k32" -> M64n216k32,
    "m64n216k64" -> M64n216k64,
    "m64n216k8" -> M64n216k8,
    "m64n224k16" -> M64n224k16,
    "m64n224k256" -> M64n224k256,
    "m64n224k32" -> M64n224k32,
    "m64n224k64" -> M64n224k64,
    "m64n224k8" -> M64n224k8,
    "m64n232k16" -> M64n232k16,
    "m64n232k32" -> M64n232k32,
    "m64n232k64" -> M64n232k64,
    "m64n232k8" -> M64n232k8,
    "m64n240k16" -> M64n240k16,
    "m64n240k256" -> M64n240k256,
    "m64n240k32" -> M64n240k32,
    "m64n240k64" -> M64n240k64,
    "m64n240k8" -> M64n240k8,
    "m64n248k16" -> M64n248k16,
    "m64n248k32" -> M64n248k32,
    "m64n248k64" -> M64n248k64,
    "m64n248k8" -> M64n248k8,
    "m64n24k16" -> M64n24k16,
    "m64n24k256" -> M64n24k256,
    "m64n24k32" -> M64n24k32,
    "m64n24k64" -> M64n24k64,
    "m64n24k8" -> M64n24k8,
    "m64n256k16" -> M64n256k16,
    "m64n256k256" -> M64n256k256,
    "m64n256k32" -> M64n256k32,
    "m64n256k64" -> M64n256k64,
    "m64n256k8" -> M64n256k8,
    "m64n32k16" -> M64n32k16,
    "m64n32k256" -> M64n32k256,
    "m64n32k32" -> M64n32k32,
    "m64n32k64" -> M64n32k64,
    "m64n32k8" -> M64n32k8,
    "m64n40k16" -> M64n40k16,
    "m64n40k32" -> M64n40k32,
    "m64n40k64" -> M64n40k64,
    "m64n40k8" -> M64n40k8,
    "m64n48k16" -> M64n48k16,
    "m64n48k256" -> M64n48k256,
    "m64n48k32" -> M64n48k32,
    "m64n48k64" -> M64n48k64,
    "m64n48k8" -> M64n48k8,
    "m64n56k16" -> M64n56k16,
    "m64n56k32" -> M64n56k32,
    "m64n56k64" -> M64n56k64,
    "m64n56k8" -> M64n56k8,
    "m64n64k16" -> M64n64k16,
    "m64n64k256" -> M64n64k256,
    "m64n64k32" -> M64n64k32,
    "m64n64k64" -> M64n64k64,
    "m64n64k8" -> M64n64k8,
    "m64n72k16" -> M64n72k16,
    "m64n72k32" -> M64n72k32,
    "m64n72k64" -> M64n72k64,
    "m64n72k8" -> M64n72k8,
    "m64n80k16" -> M64n80k16,
    "m64n80k256" -> M64n80k256,
    "m64n80k32" -> M64n80k32,
    "m64n80k64" -> M64n80k64,
    "m64n80k8" -> M64n80k8,
    "m64n88k16" -> M64n88k16,
    "m64n88k32" -> M64n88k32,
    "m64n88k64" -> M64n88k64,
    "m64n88k8" -> M64n88k8,
    "m64n8k16" -> M64n8k16,
    "m64n8k256" -> M64n8k256,
    "m64n8k32" -> M64n8k32,
    "m64n8k64" -> M64n8k64,
    "m64n8k8" -> M64n8k8,
    "m64n96k16" -> M64n96k16,
    "m64n96k256" -> M64n96k256,
    "m64n96k32" -> M64n96k32,
    "m64n96k64" -> M64n96k64,
    "m64n96k8" -> M64n96k8,
    "m8n16" -> M8n16,
    "m8n32k16" -> M8n32k16,
    "m8n8" -> M8n8,
    "m8n8k128" -> M8n8k128,
    "m8n8k16" -> M8n8k16,
    "m8n8k32" -> M8n8k32,
    "m8n8k4" -> M8n8k4,
    "mask" -> Mask,
    "max" -> Max,
    "mbarrier" -> Mbarrier,
    "mbarrier::arrive::one" -> MbarrierArriveOne,
    "mbarrier::complete_tx::bytes" -> MbarrierCompleteTxBytes,
    "mbarrier_init" -> MbarrierInit,
    "memory_layout" -> MemoryLayout,
    "min" -> Min,
    "mma" -> Mma,
    "mma_async" -> MmaAsync,
    "mmio" -> Mmio,
    "multicast::cluster" -> MulticastCluster,
    "multicast::cluster::all" -> MulticastClusterAll,
    "multimem" -> Multimem,
    "nan" -> Nan,
    "nc" -> Nc,
    "ne" -> Ne,
    "neu" -> Neu,
    "noComplete" -> NoComplete,
    "noftz" -> Noftz,
    "noinc" -> Noinc,
    "none" -> None,
    "normal" -> Normal,
    "normalized_coords" -> NormalizedCoords,
    "notanumber" -> Notanumber,
    "num" -> Num,
    "num_mipmap_levels" -> NumMipmapLevels,
    "num_samples" -> NumSamples,
    "number" -> Number,
    "oob" -> Oob,
    "op_restrict::fetching" -> OpRestrictFetching,
    "or" -> Or,
    "p" -> P,
    "pack" -> Pack,
    "pack::16b" -> Pack16b,
    "param" -> Param,
    "param::entry" -> ParamEntry,
    "param::func" -> ParamFunc,
    "parity" -> Parity,
    "pending_count" -> PendingCount,
    "phase_type::conditional" -> PhaseTypeConditional,
    "phase_type::primary" -> PhaseTypePrimary,
    "po" -> Po,
    "popc" -> Popc,
    "pred" -> Pred,
    "prefetch" -> Prefetch,
    "proxy" -> Proxy,
    "query_cancel" -> QueryCancel,
    "r" -> R,
    "range" -> Range,
    "rank" -> Rank,
    "rc16" -> Rc16,
    "rc8" -> Rc8,
    "read" -> Read,
    "red" -> Red,
    "reduce" -> Reduce,
    "relaxed" -> Relaxed,
    "release" -> Release,
    "relinquish_alloc_permit" -> RelinquishAllocPermit,
    "relu" -> Relu,
    "replace" -> Replace,
    "rm" -> Rm,
    "rmi" -> Rmi,
    "rn" -> Rn,
    "rna" -> Rna,
    "rni" -> Rni,
    "row" -> Row,
    "rp" -> Rp,
    "rpi" -> Rpi,
    "rs" -> Rs,
    "rz" -> Rz,
    "rzi" -> Rzi,
    "s16" -> S16,
    "s16x2" -> S16x2,
    "s2" -> S2,
    "s2f6x2" -> S2f6x2,
    "s32" -> S32,
    "s4" -> S4,
    "s64" -> S64,
    "s8" -> S8,
    "s8x4" -> S8x4,
    "samplerref" -> Samplerref,
    "sat" -> Sat,
    "satfinite" -> Satfinite,
    "sc" -> Sc,
    "scale_vec::1X" -> ScaleVec1X,
    "scale_vec::2X" -> ScaleVec2X,
    "scale_vec::4X" -> ScaleVec4X,
    "scaled::n2::ue8m0" -> ScaledN2Ue8m0,
    "shared" -> Shared,
    "shared::cluster" -> SharedCluster,
    "shared::cta" -> SharedCta,
    "shift" -> Shift,
    "shiftamt" -> Shiftamt,
    "shr15" -> Shr15,
    "shr7" -> Shr7,
    "sp" -> Sp,
    "sp::ordered_metadata" -> SpOrderedMetadata,
    "st" -> St,
    "store" -> Store,
    "submit" -> Submit,
    "subnormal" -> Subnormal,
    "surfref" -> Surfref,
    "swizzle_atomicity" -> SwizzleAtomicity,
    "swizzle_mode" -> SwizzleMode,
    "sync" -> Sync,
    "sync_restrict::reads" -> SyncRestrictReads,
    "sync_restrict::shared::cluster" -> SyncRestrictSharedCluster,
    "sync_restrict::shared::cta" -> SyncRestrictSharedCta,
    "sys" -> Sys,
    "tensor" -> Tensor,
    "tensormap" -> Tensormap,
    "tensormap::generic" -> TensormapGeneric,
    "test_wait" -> TestWait,
    "texref" -> Texref,
    "tf32" -> Tf32,
    "tile" -> Tile,
    "tile::gather4" -> TileGather4,
    "tile::scatter4" -> TileScatter4,
    "to" -> To,
    "trans" -> Trans,
    "trap" -> Trap,
    "try_cancel" -> TryCancel,
    "try_get" -> TryGet,
    "try_pullred" -> TryPullred,
    "try_put" -> TryPut,
    "try_red" -> TryRed,
    "try_wait" -> TryWait,
    "type" -> Type,
    "u16" -> U16,
    "u16x2" -> U16x2,
    "u2" -> U2,
    "u32" -> U32,
    "u4" -> U4,
    "u64" -> U64,
    "u8" -> U8,
    "u8x4" -> U8x4,
    "ue4m3" -> Ue4m3,
    "ue8m0" -> Ue8m0,
    "ue8m0x2" -> Ue8m0x2,
    "uni" -> Uni,
    "unpack::16b" -> Unpack16b,
    "up" -> Up,
    "v2" -> V2,
    "v4" -> V4,
    "v8" -> V8,
    "volatile" -> Volatile,
    "wait" -> Wait,
    "wait::ld" -> WaitLd,
    "wait::st" -> WaitSt,
    "wait_all" -> WaitAll,
    "wait_group" -> WaitGroup,
    "warp" -> Warp,
    "wb" -> Wb,
    "weak" -> Weak,
    "wide" -> Wide,
    "width" -> Width,
    "wrap" -> Wrap,
    "ws" -> Ws,
    "wt" -> Wt,
    "x1" -> X1,
    "x128" -> X128,
    "x16" -> X16,
    "x2" -> X2,
    "x32" -> X32,
    "x4" -> X4,
    "x64" -> X64,
    "x8" -> X8,
    "xor" -> Xor,
    "xorsign" -> Xorsign,
    "zero" -> Zero
  )
  def apply(name: NonEmpty[String]): Modifier = byName.getOrElse(name.raw, Unrecognised(name))
  def unapply(value: Modifier): Some[NonEmpty[String]] = Some(value.name)

  lazy val PackedHalfTypes: Set[Modifier] = Set(F16x2, Bf16x2)

  extension (modifier: Modifier) {
    def isInteger32: Boolean = PartialFunction.cond(modifier) { case U32 | S32 => true }
    def isPackedHalf: Boolean = PackedHalfTypes(modifier)
    def is64Bit: Boolean = PartialFunction.cond(modifier) { case B64 | U64 | S64 | F64 => true }
  }
}

@javax.annotation.processing.Generated(Array("sbt codegen"))
enum Modifier(val name: NonEmpty[String]) derives CanEqual {
  case N128x128b extends Modifier("128x128b")
  case N128x256b extends Modifier("128x256b")
  case N16x128b extends Modifier("16x128b")
  case N16x256b extends Modifier("16x256b")
  case N16x32bx2 extends Modifier("16x32bx2")
  case N16x64b extends Modifier("16x64b")
  case N1d extends Modifier("1d")
  case N2d extends Modifier("2d")
  case N2dms extends Modifier("2dms")
  case N32x32b extends Modifier("32x32b")
  case N3d extends Modifier("3d")
  case N4d extends Modifier("4d")
  case N4x256b extends Modifier("4x256b")
  case N5d extends Modifier("5d")
  case L1 extends Modifier("L1")
  case L1EvictFirst extends Modifier("L1::evict_first")
  case L1EvictLast extends Modifier("L1::evict_last")
  case L1EvictNormal extends Modifier("L1::evict_normal")
  case L1EvictUnchanged extends Modifier("L1::evict_unchanged")
  case L1NoAllocate extends Modifier("L1::no_allocate")
  case L2 extends Modifier("L2")
  case L2128B extends Modifier("L2::128B")
  case L2256B extends Modifier("L2::256B")
  case L264B extends Modifier("L2::64B")
  case L2CacheHint extends Modifier("L2::cache_hint")
  case L2EvictFirst extends Modifier("L2::evict_first")
  case L2EvictLast extends Modifier("L2::evict_last")
  case L2EvictNormal extends Modifier("L2::evict_normal")
  case L2EvictUnchanged extends Modifier("L2::evict_unchanged")
  case NaN extends Modifier("NaN")
  case A extends Modifier("a")
  case A1d extends Modifier("a1d")
  case A2d extends Modifier("a2d")
  case A2dms extends Modifier("a2dms")
  case Abs extends Modifier("abs")
  case AccF16 extends Modifier("acc::f16")
  case AccF32 extends Modifier("acc::f32")
  case AcqRel extends Modifier("acq_rel")
  case Acquire extends Modifier("acquire")
  case Acube extends Modifier("acube")
  case Add extends Modifier("add")
  case AddrMode0 extends Modifier("addr_mode_0")
  case AddrMode1 extends Modifier("addr_mode_1")
  case AddrMode2 extends Modifier("addr_mode_2")
  case Alias extends Modifier("alias")
  case Aligned extends Modifier("aligned")
  case All extends Modifier("all")
  case Alloc extends Modifier("alloc")
  case And extends Modifier("and")
  case Any extends Modifier("any")
  case Approx extends Modifier("approx")
  case ArraySize extends Modifier("array_size")
  case Arrive extends Modifier("arrive")
  case ArriveDrop extends Modifier("arrive_drop")
  case Ashift extends Modifier("ashift")
  case Async extends Modifier("async")
  case AsyncGeneric extends Modifier("async::generic")
  case B extends Modifier("b")
  case B1 extends Modifier("b1")
  case B1024 extends Modifier("b1024")
  case B128 extends Modifier("b128")
  case B16 extends Modifier("b16")
  case B32 extends Modifier("b32")
  case B4e extends Modifier("b4e")
  case B4x16P64 extends Modifier("b4x16_p64")
  case B64 extends Modifier("b64")
  case B6x16P32 extends Modifier("b6x16_p32")
  case B8 extends Modifier("b8")
  case B8x16 extends Modifier("b8x16")
  case Ballot extends Modifier("ballot")
  case Base extends Modifier("base")
  case Bf16 extends Modifier("bf16")
  case Bf16x2 extends Modifier("bf16x2")
  case Bfly extends Modifier("bfly")
  case Block16 extends Modifier("block16")
  case Block32 extends Modifier("block32")
  case BlockScale extends Modifier("block_scale")
  case BoxDim extends Modifier("box_dim")
  case Bulk extends Modifier("bulk")
  case BulkGroup extends Modifier("bulk_group")
  case C extends Modifier("c")
  case Ca extends Modifier("ca")
  case Cas extends Modifier("cas")
  case Cc extends Modifier("cc")
  case Cg extends Modifier("cg")
  case ChannelDataType extends Modifier("channel_data_type")
  case ChannelOrder extends Modifier("channel_order")
  case CheckLayout extends Modifier("check_layout")
  case Clamp extends Modifier("clamp")
  case Cluster extends Modifier("cluster")
  case Col extends Modifier("col")
  case CollectorBufferOp extends Modifier("collector::buffer::op")
  case Commit extends Modifier("commit")
  case CommitGroup extends Modifier("commit_group")
  case CompleteTx extends Modifier("complete_tx")
  case Const extends Modifier("const")
  case Cp extends Modifier("cp")
  case CpFenceproxy extends Modifier("cp_fenceproxy")
  case CpMask extends Modifier("cp_mask")
  case Cs extends Modifier("cs")
  case Cta extends Modifier("cta")
  case CtaGroup1 extends Modifier("cta_group::1")
  case CtaGroup2 extends Modifier("cta_group::2")
  case Cube extends Modifier("cube")
  case Cv extends Modifier("cv")
  case Cvt extends Modifier("cvt")
  case D extends Modifier("d")
  case Dealloc extends Modifier("dealloc")
  case Dec extends Modifier("dec")
  case Depth extends Modifier("depth")
  case Down extends Modifier("down")
  case E2m1 extends Modifier("e2m1")
  case E2m1x2 extends Modifier("e2m1x2")
  case E2m1x4 extends Modifier("e2m1x4")
  case E2m3 extends Modifier("e2m3")
  case E2m3x2 extends Modifier("e2m3x2")
  case E2m3x4 extends Modifier("e2m3x4")
  case E3m2 extends Modifier("e3m2")
  case E3m2x2 extends Modifier("e3m2x2")
  case E3m2x4 extends Modifier("e3m2x4")
  case E4m3 extends Modifier("e4m3")
  case E4m3x2 extends Modifier("e4m3x2")
  case E4m3x4 extends Modifier("e4m3x4")
  case E5m2 extends Modifier("e5m2")
  case E5m2x2 extends Modifier("e5m2x2")
  case E5m2x4 extends Modifier("e5m2x4")
  case Ecl extends Modifier("ecl")
  case Ecr extends Modifier("ecr")
  case ElementStride extends Modifier("element_stride")
  case Elemtype extends Modifier("elemtype")
  case Eq extends Modifier("eq")
  case Equ extends Modifier("equ")
  case Exch extends Modifier("exch")
  case ExpectTx extends Modifier("expect_tx")
  case F16 extends Modifier("f16")
  case F16x2 extends Modifier("f16x2")
  case F32 extends Modifier("f32")
  case F32x2 extends Modifier("f32x2")
  case F4e extends Modifier("f4e")
  case F64 extends Modifier("f64")
  case FabricFabric extends Modifier("fabric::fabric")
  case FabricGeneric extends Modifier("fabric::generic")
  case Fence extends Modifier("fence")
  case FenceAfterThreadSync extends Modifier("fence::after_thread_sync")
  case FenceBeforeThreadSync extends Modifier("fence::before_thread_sync")
  case FillMode extends Modifier("fill_mode")
  case FilterMode extends Modifier("filter_mode")
  case Finite extends Modifier("finite")
  case ForceUnnormalizedCoords extends Modifier("force_unnormalized_coords")
  case Fractional extends Modifier("fractional")
  case Ftz extends Modifier("ftz")
  case Full extends Modifier("full")
  case G extends Modifier("g")
  case Ge extends Modifier("ge")
  case GenericFabric extends Modifier("generic::fabric")
  case GetFirstCtaid extends Modifier("get_first_ctaid")
  case GetFirstCtaidX extends Modifier("get_first_ctaid::x")
  case GetFirstCtaidY extends Modifier("get_first_ctaid::y")
  case GetFirstCtaidZ extends Modifier("get_first_ctaid::z")
  case Geu extends Modifier("geu")
  case Gl extends Modifier("gl")
  case Global extends Modifier("global")
  case GlobalAddress extends Modifier("global_address")
  case GlobalDim extends Modifier("global_dim")
  case GlobalStride extends Modifier("global_stride")
  case Gpu extends Modifier("gpu")
  case Grad extends Modifier("grad")
  case Gt extends Modifier("gt")
  case Gtu extends Modifier("gtu")
  case Height extends Modifier("height")
  case Hi extends Modifier("hi")
  case Hs extends Modifier("hs")
  case Idx extends Modifier("idx")
  case IgnoreOob extends Modifier("ignore_oob")
  case Im2col extends Modifier("im2col")
  case Im2colW extends Modifier("im2col::w")
  case Im2colW128 extends Modifier("im2col::w::128")
  case Im2colNoOffs extends Modifier("im2col_no_offs")
  case Inc extends Modifier("inc")
  case Infinite extends Modifier("infinite")
  case Init extends Modifier("init")
  case InterleaveLayout extends Modifier("interleave_layout")
  case Inval extends Modifier("inval")
  case IsCanceled extends Modifier("is_canceled")
  case KindF16 extends Modifier("kind::f16")
  case KindF8f6f4 extends Modifier("kind::f8f6f4")
  case KindI8 extends Modifier("kind::i8")
  case KindMxf4 extends Modifier("kind::mxf4")
  case KindMxf4nvf4 extends Modifier("kind::mxf4nvf4")
  case KindMxf8f6f4 extends Modifier("kind::mxf8f6f4")
  case KindTf32 extends Modifier("kind::tf32")
  case L extends Modifier("l")
  case LaunchDependents extends Modifier("launch_dependents")
  case LayoutV0 extends Modifier("layout::v0")
  case LayoutV1 extends Modifier("layout::v1")
  case Ld extends Modifier("ld")
  case LdReduce extends Modifier("ld_reduce")
  case Le extends Modifier("le")
  case Leu extends Modifier("leu")
  case Level extends Modifier("level")
  case LevelCacheHint extends Modifier("level::cache_hint")
  case Lo extends Modifier("lo")
  case Load extends Modifier("load")
  case Local extends Modifier("local")
  case Ls extends Modifier("ls")
  case Lt extends Modifier("lt")
  case Ltu extends Modifier("ltu")
  case Lu extends Modifier("lu")
  case M16n16 extends Modifier("m16n16")
  case M16n16k16 extends Modifier("m16n16k16")
  case M16n16k8 extends Modifier("m16n16k8")
  case M16n8 extends Modifier("m16n8")
  case M16n8k128 extends Modifier("m16n8k128")
  case M16n8k16 extends Modifier("m16n8k16")
  case M16n8k256 extends Modifier("m16n8k256")
  case M16n8k32 extends Modifier("m16n8k32")
  case M16n8k4 extends Modifier("m16n8k4")
  case M16n8k64 extends Modifier("m16n8k64")
  case M16n8k8 extends Modifier("m16n8k8")
  case M32n8k16 extends Modifier("m32n8k16")
  case M64n104k16 extends Modifier("m64n104k16")
  case M64n104k32 extends Modifier("m64n104k32")
  case M64n104k64 extends Modifier("m64n104k64")
  case M64n104k8 extends Modifier("m64n104k8")
  case M64n112k16 extends Modifier("m64n112k16")
  case M64n112k256 extends Modifier("m64n112k256")
  case M64n112k32 extends Modifier("m64n112k32")
  case M64n112k64 extends Modifier("m64n112k64")
  case M64n112k8 extends Modifier("m64n112k8")
  case M64n120k16 extends Modifier("m64n120k16")
  case M64n120k32 extends Modifier("m64n120k32")
  case M64n120k64 extends Modifier("m64n120k64")
  case M64n120k8 extends Modifier("m64n120k8")
  case M64n128k16 extends Modifier("m64n128k16")
  case M64n128k256 extends Modifier("m64n128k256")
  case M64n128k32 extends Modifier("m64n128k32")
  case M64n128k64 extends Modifier("m64n128k64")
  case M64n128k8 extends Modifier("m64n128k8")
  case M64n136k16 extends Modifier("m64n136k16")
  case M64n136k32 extends Modifier("m64n136k32")
  case M64n136k64 extends Modifier("m64n136k64")
  case M64n136k8 extends Modifier("m64n136k8")
  case M64n144k16 extends Modifier("m64n144k16")
  case M64n144k256 extends Modifier("m64n144k256")
  case M64n144k32 extends Modifier("m64n144k32")
  case M64n144k64 extends Modifier("m64n144k64")
  case M64n144k8 extends Modifier("m64n144k8")
  case M64n152k16 extends Modifier("m64n152k16")
  case M64n152k32 extends Modifier("m64n152k32")
  case M64n152k64 extends Modifier("m64n152k64")
  case M64n152k8 extends Modifier("m64n152k8")
  case M64n160k16 extends Modifier("m64n160k16")
  case M64n160k256 extends Modifier("m64n160k256")
  case M64n160k32 extends Modifier("m64n160k32")
  case M64n160k64 extends Modifier("m64n160k64")
  case M64n160k8 extends Modifier("m64n160k8")
  case M64n168k16 extends Modifier("m64n168k16")
  case M64n168k32 extends Modifier("m64n168k32")
  case M64n168k64 extends Modifier("m64n168k64")
  case M64n168k8 extends Modifier("m64n168k8")
  case M64n16k16 extends Modifier("m64n16k16")
  case M64n16k256 extends Modifier("m64n16k256")
  case M64n16k32 extends Modifier("m64n16k32")
  case M64n16k64 extends Modifier("m64n16k64")
  case M64n16k8 extends Modifier("m64n16k8")
  case M64n176k16 extends Modifier("m64n176k16")
  case M64n176k256 extends Modifier("m64n176k256")
  case M64n176k32 extends Modifier("m64n176k32")
  case M64n176k64 extends Modifier("m64n176k64")
  case M64n176k8 extends Modifier("m64n176k8")
  case M64n184k16 extends Modifier("m64n184k16")
  case M64n184k32 extends Modifier("m64n184k32")
  case M64n184k64 extends Modifier("m64n184k64")
  case M64n184k8 extends Modifier("m64n184k8")
  case M64n192k16 extends Modifier("m64n192k16")
  case M64n192k256 extends Modifier("m64n192k256")
  case M64n192k32 extends Modifier("m64n192k32")
  case M64n192k64 extends Modifier("m64n192k64")
  case M64n192k8 extends Modifier("m64n192k8")
  case M64n200k16 extends Modifier("m64n200k16")
  case M64n200k32 extends Modifier("m64n200k32")
  case M64n200k64 extends Modifier("m64n200k64")
  case M64n200k8 extends Modifier("m64n200k8")
  case M64n208k16 extends Modifier("m64n208k16")
  case M64n208k256 extends Modifier("m64n208k256")
  case M64n208k32 extends Modifier("m64n208k32")
  case M64n208k64 extends Modifier("m64n208k64")
  case M64n208k8 extends Modifier("m64n208k8")
  case M64n216k16 extends Modifier("m64n216k16")
  case M64n216k32 extends Modifier("m64n216k32")
  case M64n216k64 extends Modifier("m64n216k64")
  case M64n216k8 extends Modifier("m64n216k8")
  case M64n224k16 extends Modifier("m64n224k16")
  case M64n224k256 extends Modifier("m64n224k256")
  case M64n224k32 extends Modifier("m64n224k32")
  case M64n224k64 extends Modifier("m64n224k64")
  case M64n224k8 extends Modifier("m64n224k8")
  case M64n232k16 extends Modifier("m64n232k16")
  case M64n232k32 extends Modifier("m64n232k32")
  case M64n232k64 extends Modifier("m64n232k64")
  case M64n232k8 extends Modifier("m64n232k8")
  case M64n240k16 extends Modifier("m64n240k16")
  case M64n240k256 extends Modifier("m64n240k256")
  case M64n240k32 extends Modifier("m64n240k32")
  case M64n240k64 extends Modifier("m64n240k64")
  case M64n240k8 extends Modifier("m64n240k8")
  case M64n248k16 extends Modifier("m64n248k16")
  case M64n248k32 extends Modifier("m64n248k32")
  case M64n248k64 extends Modifier("m64n248k64")
  case M64n248k8 extends Modifier("m64n248k8")
  case M64n24k16 extends Modifier("m64n24k16")
  case M64n24k256 extends Modifier("m64n24k256")
  case M64n24k32 extends Modifier("m64n24k32")
  case M64n24k64 extends Modifier("m64n24k64")
  case M64n24k8 extends Modifier("m64n24k8")
  case M64n256k16 extends Modifier("m64n256k16")
  case M64n256k256 extends Modifier("m64n256k256")
  case M64n256k32 extends Modifier("m64n256k32")
  case M64n256k64 extends Modifier("m64n256k64")
  case M64n256k8 extends Modifier("m64n256k8")
  case M64n32k16 extends Modifier("m64n32k16")
  case M64n32k256 extends Modifier("m64n32k256")
  case M64n32k32 extends Modifier("m64n32k32")
  case M64n32k64 extends Modifier("m64n32k64")
  case M64n32k8 extends Modifier("m64n32k8")
  case M64n40k16 extends Modifier("m64n40k16")
  case M64n40k32 extends Modifier("m64n40k32")
  case M64n40k64 extends Modifier("m64n40k64")
  case M64n40k8 extends Modifier("m64n40k8")
  case M64n48k16 extends Modifier("m64n48k16")
  case M64n48k256 extends Modifier("m64n48k256")
  case M64n48k32 extends Modifier("m64n48k32")
  case M64n48k64 extends Modifier("m64n48k64")
  case M64n48k8 extends Modifier("m64n48k8")
  case M64n56k16 extends Modifier("m64n56k16")
  case M64n56k32 extends Modifier("m64n56k32")
  case M64n56k64 extends Modifier("m64n56k64")
  case M64n56k8 extends Modifier("m64n56k8")
  case M64n64k16 extends Modifier("m64n64k16")
  case M64n64k256 extends Modifier("m64n64k256")
  case M64n64k32 extends Modifier("m64n64k32")
  case M64n64k64 extends Modifier("m64n64k64")
  case M64n64k8 extends Modifier("m64n64k8")
  case M64n72k16 extends Modifier("m64n72k16")
  case M64n72k32 extends Modifier("m64n72k32")
  case M64n72k64 extends Modifier("m64n72k64")
  case M64n72k8 extends Modifier("m64n72k8")
  case M64n80k16 extends Modifier("m64n80k16")
  case M64n80k256 extends Modifier("m64n80k256")
  case M64n80k32 extends Modifier("m64n80k32")
  case M64n80k64 extends Modifier("m64n80k64")
  case M64n80k8 extends Modifier("m64n80k8")
  case M64n88k16 extends Modifier("m64n88k16")
  case M64n88k32 extends Modifier("m64n88k32")
  case M64n88k64 extends Modifier("m64n88k64")
  case M64n88k8 extends Modifier("m64n88k8")
  case M64n8k16 extends Modifier("m64n8k16")
  case M64n8k256 extends Modifier("m64n8k256")
  case M64n8k32 extends Modifier("m64n8k32")
  case M64n8k64 extends Modifier("m64n8k64")
  case M64n8k8 extends Modifier("m64n8k8")
  case M64n96k16 extends Modifier("m64n96k16")
  case M64n96k256 extends Modifier("m64n96k256")
  case M64n96k32 extends Modifier("m64n96k32")
  case M64n96k64 extends Modifier("m64n96k64")
  case M64n96k8 extends Modifier("m64n96k8")
  case M8n16 extends Modifier("m8n16")
  case M8n32k16 extends Modifier("m8n32k16")
  case M8n8 extends Modifier("m8n8")
  case M8n8k128 extends Modifier("m8n8k128")
  case M8n8k16 extends Modifier("m8n8k16")
  case M8n8k32 extends Modifier("m8n8k32")
  case M8n8k4 extends Modifier("m8n8k4")
  case Mask extends Modifier("mask")
  case Max extends Modifier("max")
  case Mbarrier extends Modifier("mbarrier")
  case MbarrierArriveOne extends Modifier("mbarrier::arrive::one")
  case MbarrierCompleteTxBytes extends Modifier("mbarrier::complete_tx::bytes")
  case MbarrierInit extends Modifier("mbarrier_init")
  case MemoryLayout extends Modifier("memory_layout")
  case Min extends Modifier("min")
  case Mma extends Modifier("mma")
  case MmaAsync extends Modifier("mma_async")
  case Mmio extends Modifier("mmio")
  case MulticastCluster extends Modifier("multicast::cluster")
  case MulticastClusterAll extends Modifier("multicast::cluster::all")
  case Multimem extends Modifier("multimem")
  case Nan extends Modifier("nan")
  case Nc extends Modifier("nc")
  case Ne extends Modifier("ne")
  case Neu extends Modifier("neu")
  case NoComplete extends Modifier("noComplete")
  case Noftz extends Modifier("noftz")
  case Noinc extends Modifier("noinc")
  case None extends Modifier("none")
  case Normal extends Modifier("normal")
  case NormalizedCoords extends Modifier("normalized_coords")
  case Notanumber extends Modifier("notanumber")
  case Num extends Modifier("num")
  case NumMipmapLevels extends Modifier("num_mipmap_levels")
  case NumSamples extends Modifier("num_samples")
  case Number extends Modifier("number")
  case Oob extends Modifier("oob")
  case OpRestrictFetching extends Modifier("op_restrict::fetching")
  case Or extends Modifier("or")
  case P extends Modifier("p")
  case Pack extends Modifier("pack")
  case Pack16b extends Modifier("pack::16b")
  case Param extends Modifier("param")
  case ParamEntry extends Modifier("param::entry")
  case ParamFunc extends Modifier("param::func")
  case Parity extends Modifier("parity")
  case PendingCount extends Modifier("pending_count")
  case PhaseTypeConditional extends Modifier("phase_type::conditional")
  case PhaseTypePrimary extends Modifier("phase_type::primary")
  case Po extends Modifier("po")
  case Popc extends Modifier("popc")
  case Pred extends Modifier("pred")
  case Prefetch extends Modifier("prefetch")
  case Proxy extends Modifier("proxy")
  case QueryCancel extends Modifier("query_cancel")
  case R extends Modifier("r")
  case Range extends Modifier("range")
  case Rank extends Modifier("rank")
  case Rc16 extends Modifier("rc16")
  case Rc8 extends Modifier("rc8")
  case Read extends Modifier("read")
  case Red extends Modifier("red")
  case Reduce extends Modifier("reduce")
  case Relaxed extends Modifier("relaxed")
  case Release extends Modifier("release")
  case RelinquishAllocPermit extends Modifier("relinquish_alloc_permit")
  case Relu extends Modifier("relu")
  case Replace extends Modifier("replace")
  case Rm extends Modifier("rm")
  case Rmi extends Modifier("rmi")
  case Rn extends Modifier("rn")
  case Rna extends Modifier("rna")
  case Rni extends Modifier("rni")
  case Row extends Modifier("row")
  case Rp extends Modifier("rp")
  case Rpi extends Modifier("rpi")
  case Rs extends Modifier("rs")
  case Rz extends Modifier("rz")
  case Rzi extends Modifier("rzi")
  case S16 extends Modifier("s16")
  case S16x2 extends Modifier("s16x2")
  case S2 extends Modifier("s2")
  case S2f6x2 extends Modifier("s2f6x2")
  case S32 extends Modifier("s32")
  case S4 extends Modifier("s4")
  case S64 extends Modifier("s64")
  case S8 extends Modifier("s8")
  case S8x4 extends Modifier("s8x4")
  case Samplerref extends Modifier("samplerref")
  case Sat extends Modifier("sat")
  case Satfinite extends Modifier("satfinite")
  case Sc extends Modifier("sc")
  case ScaleVec1X extends Modifier("scale_vec::1X")
  case ScaleVec2X extends Modifier("scale_vec::2X")
  case ScaleVec4X extends Modifier("scale_vec::4X")
  case ScaledN2Ue8m0 extends Modifier("scaled::n2::ue8m0")
  case Shared extends Modifier("shared")
  case SharedCluster extends Modifier("shared::cluster")
  case SharedCta extends Modifier("shared::cta")
  case Shift extends Modifier("shift")
  case Shiftamt extends Modifier("shiftamt")
  case Shr15 extends Modifier("shr15")
  case Shr7 extends Modifier("shr7")
  case Sp extends Modifier("sp")
  case SpOrderedMetadata extends Modifier("sp::ordered_metadata")
  case St extends Modifier("st")
  case Store extends Modifier("store")
  case Submit extends Modifier("submit")
  case Subnormal extends Modifier("subnormal")
  case Surfref extends Modifier("surfref")
  case SwizzleAtomicity extends Modifier("swizzle_atomicity")
  case SwizzleMode extends Modifier("swizzle_mode")
  case Sync extends Modifier("sync")
  case SyncRestrictReads extends Modifier("sync_restrict::reads")
  case SyncRestrictSharedCluster extends Modifier("sync_restrict::shared::cluster")
  case SyncRestrictSharedCta extends Modifier("sync_restrict::shared::cta")
  case Sys extends Modifier("sys")
  case Tensor extends Modifier("tensor")
  case Tensormap extends Modifier("tensormap")
  case TensormapGeneric extends Modifier("tensormap::generic")
  case TestWait extends Modifier("test_wait")
  case Texref extends Modifier("texref")
  case Tf32 extends Modifier("tf32")
  case Tile extends Modifier("tile")
  case TileGather4 extends Modifier("tile::gather4")
  case TileScatter4 extends Modifier("tile::scatter4")
  case To extends Modifier("to")
  case Trans extends Modifier("trans")
  case Trap extends Modifier("trap")
  case TryCancel extends Modifier("try_cancel")
  case TryGet extends Modifier("try_get")
  case TryPullred extends Modifier("try_pullred")
  case TryPut extends Modifier("try_put")
  case TryRed extends Modifier("try_red")
  case TryWait extends Modifier("try_wait")
  case Type extends Modifier("type")
  case U16 extends Modifier("u16")
  case U16x2 extends Modifier("u16x2")
  case U2 extends Modifier("u2")
  case U32 extends Modifier("u32")
  case U4 extends Modifier("u4")
  case U64 extends Modifier("u64")
  case U8 extends Modifier("u8")
  case U8x4 extends Modifier("u8x4")
  case Ue4m3 extends Modifier("ue4m3")
  case Ue8m0 extends Modifier("ue8m0")
  case Ue8m0x2 extends Modifier("ue8m0x2")
  case Uni extends Modifier("uni")
  case Unpack16b extends Modifier("unpack::16b")
  case Up extends Modifier("up")
  case V2 extends Modifier("v2")
  case V4 extends Modifier("v4")
  case V8 extends Modifier("v8")
  case Volatile extends Modifier("volatile")
  case Wait extends Modifier("wait")
  case WaitLd extends Modifier("wait::ld")
  case WaitSt extends Modifier("wait::st")
  case WaitAll extends Modifier("wait_all")
  case WaitGroup extends Modifier("wait_group")
  case Warp extends Modifier("warp")
  case Wb extends Modifier("wb")
  case Weak extends Modifier("weak")
  case Wide extends Modifier("wide")
  case Width extends Modifier("width")
  case Wrap extends Modifier("wrap")
  case Ws extends Modifier("ws")
  case Wt extends Modifier("wt")
  case X1 extends Modifier("x1")
  case X128 extends Modifier("x128")
  case X16 extends Modifier("x16")
  case X2 extends Modifier("x2")
  case X32 extends Modifier("x32")
  case X4 extends Modifier("x4")
  case X64 extends Modifier("x64")
  case X8 extends Modifier("x8")
  case Xor extends Modifier("xor")
  case Xorsign extends Modifier("xorsign")
  case Zero extends Modifier("zero")
  case Unrecognised(spelling: NonEmpty[String]) extends Modifier(spelling)
}
