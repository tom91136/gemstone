// Generated from the pinned PTX syntax domains. Regenerate with: sbt codegen
package net.kurobako.gemstone.isa.ptx
import javax.annotation.processing.Generated

import net.kurobako.gemstone.constraint.*

object Modifier {
  private lazy val byName: Map[String, Modifier] = Vector(
    N128x128b,
    N128x256b,
    N16x128b,
    N16x256b,
    N16x32bx2,
    N16x64b,
    N1d,
    N2d,
    N2dms,
    N32x32b,
    N3d,
    N4d,
    N4x256b,
    N5d,
    L1,
    L1EvictFirst,
    L1EvictLast,
    L1EvictNormal,
    L1EvictUnchanged,
    L1NoAllocate,
    L2,
    L2128B,
    L2256B,
    L264B,
    L2CacheHint,
    L2EvictFirst,
    L2EvictLast,
    L2EvictNormal,
    L2EvictUnchanged,
    NaN,
    A,
    A1d,
    A2d,
    A2dms,
    Abs,
    AccF16,
    AccF32,
    AcqRel,
    Acquire,
    Acube,
    Add,
    AddrMode0,
    AddrMode1,
    AddrMode2,
    Alias,
    Aligned,
    All,
    Alloc,
    And,
    Any,
    Approx,
    ArraySize,
    Arrive,
    ArriveDrop,
    Ashift,
    Async,
    AsyncGeneric,
    B,
    B1,
    B1024,
    B128,
    B16,
    B32,
    B4e,
    B4x16P64,
    B64,
    B6x16P32,
    B8,
    B8x16,
    Ballot,
    Base,
    Bf16,
    Bf16x2,
    Bfly,
    Block16,
    Block32,
    BlockScale,
    BoxDim,
    Bulk,
    BulkGroup,
    C,
    Ca,
    Cas,
    Cc,
    Cg,
    ChannelDataType,
    ChannelOrder,
    CheckLayout,
    Clamp,
    Cluster,
    Col,
    CollectorBufferOp,
    Commit,
    CommitGroup,
    CompleteTx,
    Const,
    Cp,
    CpFenceproxy,
    CpMask,
    Cs,
    Cta,
    CtaGroup1,
    CtaGroup2,
    Cube,
    Cv,
    Cvt,
    D,
    Dealloc,
    Dec,
    Depth,
    Down,
    E2m1,
    E2m1x2,
    E2m1x4,
    E2m3,
    E2m3x2,
    E2m3x4,
    E3m2,
    E3m2x2,
    E3m2x4,
    E4m3,
    E4m3x2,
    E4m3x4,
    E5m2,
    E5m2x2,
    E5m2x4,
    Ecl,
    Ecr,
    ElementStride,
    Elemtype,
    Eq,
    Equ,
    Exch,
    ExpectTx,
    F16,
    F16x2,
    F32,
    F32x2,
    F4e,
    F64,
    FabricFabric,
    FabricGeneric,
    Fence,
    FenceAfterThreadSync,
    FenceBeforeThreadSync,
    FillMode,
    FilterMode,
    Finite,
    ForceUnnormalizedCoords,
    Fractional,
    Ftz,
    Full,
    G,
    Ge,
    GenericFabric,
    GetFirstCtaid,
    GetFirstCtaidX,
    GetFirstCtaidY,
    GetFirstCtaidZ,
    Geu,
    Gl,
    Global,
    GlobalAddress,
    GlobalDim,
    GlobalStride,
    Gpu,
    Grad,
    Gt,
    Gtu,
    Height,
    Hi,
    Hs,
    Idx,
    IgnoreOob,
    Im2col,
    Im2colW,
    Im2colW128,
    Im2colNoOffs,
    Inc,
    Infinite,
    Init,
    InterleaveLayout,
    Inval,
    IsCanceled,
    KindF16,
    KindF8f6f4,
    KindI8,
    KindMxf4,
    KindMxf4nvf4,
    KindMxf8f6f4,
    KindTf32,
    L,
    LaunchDependents,
    LayoutV0,
    LayoutV1,
    Ld,
    LdReduce,
    Le,
    Leu,
    Level,
    LevelCacheHint,
    Lo,
    Load,
    Local,
    Ls,
    Lt,
    Ltu,
    Lu,
    M16n16,
    M16n16k16,
    M16n16k8,
    M16n8,
    M16n8k128,
    M16n8k16,
    M16n8k256,
    M16n8k32,
    M16n8k4,
    M16n8k64,
    M16n8k8,
    M32n8k16,
    M64n104k16,
    M64n104k32,
    M64n104k64,
    M64n104k8,
    M64n112k16,
    M64n112k256,
    M64n112k32,
    M64n112k64,
    M64n112k8,
    M64n120k16,
    M64n120k32,
    M64n120k64,
    M64n120k8,
    M64n128k16,
    M64n128k256,
    M64n128k32,
    M64n128k64,
    M64n128k8,
    M64n136k16,
    M64n136k32,
    M64n136k64,
    M64n136k8,
    M64n144k16,
    M64n144k256,
    M64n144k32,
    M64n144k64,
    M64n144k8,
    M64n152k16,
    M64n152k32,
    M64n152k64,
    M64n152k8,
    M64n160k16,
    M64n160k256,
    M64n160k32,
    M64n160k64,
    M64n160k8,
    M64n168k16,
    M64n168k32,
    M64n168k64,
    M64n168k8,
    M64n16k16,
    M64n16k256,
    M64n16k32,
    M64n16k64,
    M64n16k8,
    M64n176k16,
    M64n176k256,
    M64n176k32,
    M64n176k64,
    M64n176k8,
    M64n184k16,
    M64n184k32,
    M64n184k64,
    M64n184k8,
    M64n192k16,
    M64n192k256,
    M64n192k32,
    M64n192k64,
    M64n192k8,
    M64n200k16,
    M64n200k32,
    M64n200k64,
    M64n200k8,
    M64n208k16,
    M64n208k256,
    M64n208k32,
    M64n208k64,
    M64n208k8,
    M64n216k16,
    M64n216k32,
    M64n216k64,
    M64n216k8,
    M64n224k16,
    M64n224k256,
    M64n224k32,
    M64n224k64,
    M64n224k8,
    M64n232k16,
    M64n232k32,
    M64n232k64,
    M64n232k8,
    M64n240k16,
    M64n240k256,
    M64n240k32,
    M64n240k64,
    M64n240k8,
    M64n248k16,
    M64n248k32,
    M64n248k64,
    M64n248k8,
    M64n24k16,
    M64n24k256,
    M64n24k32,
    M64n24k64,
    M64n24k8,
    M64n256k16,
    M64n256k256,
    M64n256k32,
    M64n256k64,
    M64n256k8,
    M64n32k16,
    M64n32k256,
    M64n32k32,
    M64n32k64,
    M64n32k8,
    M64n40k16,
    M64n40k32,
    M64n40k64,
    M64n40k8,
    M64n48k16,
    M64n48k256,
    M64n48k32,
    M64n48k64,
    M64n48k8,
    M64n56k16,
    M64n56k32,
    M64n56k64,
    M64n56k8,
    M64n64k16,
    M64n64k256,
    M64n64k32,
    M64n64k64,
    M64n64k8,
    M64n72k16,
    M64n72k32,
    M64n72k64,
    M64n72k8,
    M64n80k16,
    M64n80k256,
    M64n80k32,
    M64n80k64,
    M64n80k8,
    M64n88k16,
    M64n88k32,
    M64n88k64,
    M64n88k8,
    M64n8k16,
    M64n8k256,
    M64n8k32,
    M64n8k64,
    M64n8k8,
    M64n96k16,
    M64n96k256,
    M64n96k32,
    M64n96k64,
    M64n96k8,
    M8n16,
    M8n32k16,
    M8n8,
    M8n8k128,
    M8n8k16,
    M8n8k32,
    M8n8k4,
    Mask,
    Max,
    Mbarrier,
    MbarrierArriveOne,
    MbarrierCompleteTxBytes,
    MbarrierInit,
    MemoryLayout,
    Min,
    Mma,
    MmaAsync,
    Mmio,
    MulticastCluster,
    MulticastClusterAll,
    Multimem,
    Nan,
    Nc,
    Ne,
    Neu,
    NoComplete,
    Noftz,
    Noinc,
    None,
    Normal,
    NormalizedCoords,
    Notanumber,
    Num,
    NumMipmapLevels,
    NumSamples,
    Number,
    Oob,
    OpRestrictFetching,
    Or,
    P,
    Pack,
    Pack16b,
    Param,
    ParamEntry,
    ParamFunc,
    Parity,
    PendingCount,
    PhaseTypeConditional,
    PhaseTypePrimary,
    Po,
    Popc,
    Pred,
    Prefetch,
    Proxy,
    QueryCancel,
    R,
    Range,
    Rank,
    Rc16,
    Rc8,
    Read,
    Red,
    Reduce,
    Relaxed,
    Release,
    RelinquishAllocPermit,
    Relu,
    Replace,
    Rm,
    Rmi,
    Rn,
    Rna,
    Rni,
    Row,
    Rp,
    Rpi,
    Rs,
    Rz,
    Rzi,
    S16,
    S16x2,
    S2,
    S2f6x2,
    S32,
    S4,
    S64,
    S8,
    S8x4,
    Samplerref,
    Sat,
    Satfinite,
    Sc,
    ScaleVec1X,
    ScaleVec2X,
    ScaleVec4X,
    ScaledN2Ue8m0,
    Shared,
    SharedCluster,
    SharedCta,
    Shift,
    Shiftamt,
    Shr15,
    Shr7,
    Sp,
    SpOrderedMetadata,
    St,
    Store,
    Submit,
    Subnormal,
    Surfref,
    SwizzleAtomicity,
    SwizzleMode,
    Sync,
    SyncRestrictReads,
    SyncRestrictSharedCluster,
    SyncRestrictSharedCta,
    Sys,
    Tensor,
    Tensormap,
    TensormapGeneric,
    TestWait,
    Texref,
    Tf32,
    Tile,
    TileGather4,
    TileScatter4,
    To,
    Trans,
    Trap,
    TryCancel,
    TryGet,
    TryPullred,
    TryPut,
    TryRed,
    TryWait,
    Type,
    U16,
    U16x2,
    U2,
    U32,
    U4,
    U64,
    U8,
    U8x4,
    Ue4m3,
    Ue8m0,
    Ue8m0x2,
    Uni,
    Unpack16b,
    Up,
    V2,
    V4,
    V8,
    Volatile,
    Wait,
    WaitLd,
    WaitSt,
    WaitAll,
    WaitGroup,
    Warp,
    Wb,
    Weak,
    Wide,
    Width,
    Wrap,
    Ws,
    Wt,
    X1,
    X128,
    X16,
    X2,
    X32,
    X4,
    X64,
    X8,
    Xor,
    Xorsign,
    Zero
  ).view.map(value => value.name.raw -> value).toMap
  def apply(name: NonEmpty[String]): Modifier = byName.getOrElse(name.raw, Unrecognised(name))
  def unapply(value: Modifier): Some[NonEmpty[String]] = Some(value.name)

  lazy val PackedHalfTypes: Set[Modifier] = Set(F16x2, Bf16x2)

  extension (modifier: Modifier) {
    def isInteger32: Boolean = PartialFunction.cond(modifier) { case U32 | S32 => true }
    def isPackedHalf: Boolean = PackedHalfTypes(modifier)
    def is64Bit: Boolean = PartialFunction.cond(modifier) { case B64 | U64 | S64 | F64 => true }
  }
}

@Generated(Array("sbt codegen"))
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
