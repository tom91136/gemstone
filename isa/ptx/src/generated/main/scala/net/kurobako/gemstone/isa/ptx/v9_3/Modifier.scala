// Generated from the pinned PTX syntax domains. Regenerate with: sbt codegen
package net.kurobako.gemstone.isa.ptx.v9_3
import net.kurobako.gemstone.constraint.*

object Modifier {
  private lazy val byName: Map[String, Modifier] = Map(
    "L1" -> L1,
    "L1::evict_first" -> L1EvictFirst,
    "L1::evict_last" -> L1EvictLast,
    "L1::evict_normal" -> L1EvictNormal,
    "L1::evict_unchanged" -> L1EvictUnchanged,
    "L1::no_allocate" -> L1NoAllocate,
    "L2" -> L2,
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
    "atype" -> Atype,
    "b" -> B,
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
    "ca" -> Ca,
    "cas" -> Cas,
    "cc" -> Cc,
    "cg" -> Cg,
    "channel_data_type" -> ChannelDataType,
    "channel_order" -> ChannelOrder,
    "check_layout" -> CheckLayout,
    "clamp" -> Clamp,
    "cluster" -> Cluster,
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
    "cta_group" -> CtaGroup,
    "cube" -> Cube,
    "cv" -> Cv,
    "cvt" -> Cvt,
    "dealloc" -> Dealloc,
    "dec" -> Dec,
    "depth" -> Depth,
    "down" -> Down,
    "dtype" -> Dtype,
    "e2m1x2" -> E2m1x2,
    "e2m1x4" -> E2m1x4,
    "e2m3x2" -> E2m3x2,
    "e2m3x4" -> E2m3x4,
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
    "fence::after_thread_sync" -> FenceAfterThreadSync,
    "fence::before_thread_sync" -> FenceBeforeThreadSync,
    "fill_mode" -> FillMode,
    "filter_mode" -> FilterMode,
    "finite" -> Finite,
    "force_unnormalized_coords" -> ForceUnnormalizedCoords,
    "fp16x2" -> Fp16x2,
    "fractional" -> Fractional,
    "ftz" -> Ftz,
    "full" -> Full,
    "g" -> G,
    "ge" -> Ge,
    "generic::fabric" -> GenericFabric,
    "get_first_ctaid" -> GetFirstCtaid,
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
    "local" -> Local,
    "ls" -> Ls,
    "lt" -> Lt,
    "ltu" -> Ltu,
    "lu" -> Lu,
    "mask" -> Mask,
    "max" -> Max,
    "mbarrier" -> Mbarrier,
    "mbarrier::arrive::one" -> MbarrierArriveOne,
    "mbarrier::complete_tx::bytes" -> MbarrierCompleteTxBytes,
    "mbarrier_init" -> MbarrierInit,
    "memory_layout" -> MemoryLayout,
    "min" -> Min,
    "mma" -> Mma,
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
    "param" -> Param,
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
    "scaled::n2::ue8m0" -> ScaledN2Ue8m0,
    "shared" -> Shared,
    "shared::cluster" -> SharedCluster,
    "shared::cta" -> SharedCta,
    "shift" -> Shift,
    "shiftamt" -> Shiftamt,
    "shr15" -> Shr15,
    "shr7" -> Shr7,
    "sp" -> Sp,
    "st" -> St,
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
    "ue8m0x2" -> Ue8m0x2,
    "uni" -> Uni,
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
  def apply(name: NonEmpty[String]): Modifier = byName.getOrElse(name.raw, Unrecognized(name))
  def unapply(value: Modifier): Some[NonEmpty[String]] = Some(value.name)
}

@javax.annotation.processing.Generated(Array("sbt codegen"))
enum Modifier(val name: NonEmpty[String]) {
  case L1 extends Modifier("L1")
  case L1EvictFirst extends Modifier("L1::evict_first")
  case L1EvictLast extends Modifier("L1::evict_last")
  case L1EvictNormal extends Modifier("L1::evict_normal")
  case L1EvictUnchanged extends Modifier("L1::evict_unchanged")
  case L1NoAllocate extends Modifier("L1::no_allocate")
  case L2 extends Modifier("L2")
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
  case Atype extends Modifier("atype")
  case B extends Modifier("b")
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
  case Ca extends Modifier("ca")
  case Cas extends Modifier("cas")
  case Cc extends Modifier("cc")
  case Cg extends Modifier("cg")
  case ChannelDataType extends Modifier("channel_data_type")
  case ChannelOrder extends Modifier("channel_order")
  case CheckLayout extends Modifier("check_layout")
  case Clamp extends Modifier("clamp")
  case Cluster extends Modifier("cluster")
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
  case CtaGroup extends Modifier("cta_group")
  case Cube extends Modifier("cube")
  case Cv extends Modifier("cv")
  case Cvt extends Modifier("cvt")
  case Dealloc extends Modifier("dealloc")
  case Dec extends Modifier("dec")
  case Depth extends Modifier("depth")
  case Down extends Modifier("down")
  case Dtype extends Modifier("dtype")
  case E2m1x2 extends Modifier("e2m1x2")
  case E2m1x4 extends Modifier("e2m1x4")
  case E2m3x2 extends Modifier("e2m3x2")
  case E2m3x4 extends Modifier("e2m3x4")
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
  case FenceAfterThreadSync extends Modifier("fence::after_thread_sync")
  case FenceBeforeThreadSync extends Modifier("fence::before_thread_sync")
  case FillMode extends Modifier("fill_mode")
  case FilterMode extends Modifier("filter_mode")
  case Finite extends Modifier("finite")
  case ForceUnnormalizedCoords extends Modifier("force_unnormalized_coords")
  case Fp16x2 extends Modifier("fp16x2")
  case Fractional extends Modifier("fractional")
  case Ftz extends Modifier("ftz")
  case Full extends Modifier("full")
  case G extends Modifier("g")
  case Ge extends Modifier("ge")
  case GenericFabric extends Modifier("generic::fabric")
  case GetFirstCtaid extends Modifier("get_first_ctaid")
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
  case Local extends Modifier("local")
  case Ls extends Modifier("ls")
  case Lt extends Modifier("lt")
  case Ltu extends Modifier("ltu")
  case Lu extends Modifier("lu")
  case Mask extends Modifier("mask")
  case Max extends Modifier("max")
  case Mbarrier extends Modifier("mbarrier")
  case MbarrierArriveOne extends Modifier("mbarrier::arrive::one")
  case MbarrierCompleteTxBytes extends Modifier("mbarrier::complete_tx::bytes")
  case MbarrierInit extends Modifier("mbarrier_init")
  case MemoryLayout extends Modifier("memory_layout")
  case Min extends Modifier("min")
  case Mma extends Modifier("mma")
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
  case Param extends Modifier("param")
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
  case ScaledN2Ue8m0 extends Modifier("scaled::n2::ue8m0")
  case Shared extends Modifier("shared")
  case SharedCluster extends Modifier("shared::cluster")
  case SharedCta extends Modifier("shared::cta")
  case Shift extends Modifier("shift")
  case Shiftamt extends Modifier("shiftamt")
  case Shr15 extends Modifier("shr15")
  case Shr7 extends Modifier("shr7")
  case Sp extends Modifier("sp")
  case St extends Modifier("st")
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
  case Ue8m0x2 extends Modifier("ue8m0x2")
  case Uni extends Modifier("uni")
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
  case Unrecognized(spelling: NonEmpty[String]) extends Modifier(spelling)
  def bitWidth: Int = this match {
    case B1024 => 1024; case B128 => 128; case B16 => 16; case B32 => 32; case B64 => 64; case B8 => 8; case Bf16 => 16;
    case F16 => 16; case F32 => 32; case F64 => 64; case S16 => 16; case S2 => 2; case S32 => 32; case S4 => 4;
    case S64 => 64; case S8 => 8; case U16 => 16; case U2 => 2; case U32 => 32; case U4 => 4; case U64 => 64;
    case U8 => 8; case _ => 0
  }
  def isSigned: Boolean = this match { case S16 | S2 | S32 | S4 | S64 | S8 => true; case _ => false }
  def isFloating: Boolean = this match { case F16 | F16x2 | F32 | F32x2 | F64 => true; case _ => false }
}
