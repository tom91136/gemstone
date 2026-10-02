// Generated from the pinned AMDGPU instruction metadata. Regenerate with: sbt codegen
package net.kurobako.gemstone.isa.amdgpu
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.string.*

object Opcode {
  enum ComparisonKind {
    case Signed16, Signed32, Signed64, Unsigned16, Unsigned32, Unsigned64
    case Float16, Float32, Float64, ClassFloat32, ClassFloat64
  }

  enum ComparisonRelation {
    case Equal, NotEqual, Less, LessOrEqual, Greater, GreaterOrEqual
    case NotLess, NotLessOrEqual, NotGreater, NotGreaterOrEqual
    case Ordered, Unordered, NotLessOrGreater, Class
  }

  case class ComparisonOpcode(
      kind: ComparisonKind,
      relation: ComparisonRelation,
      updateExec: Boolean
  )

  private val AllOpcodes: Vec[Opcode] = Vec(
    SOP1InstructionDefs.definitions,
    SOP2InstructionDefs.definitions,
    SOPCInstructionDefs.definitions,
    SOPKInstructionDefs.definitions,
    SOPPInstructionDefs.definitions,
    ScalarMemoryInstructionDefs.definitions,
    VOP1InstructionDefs.definitions,
    VOP2InstructionDefs.definitions,
    VOP3InstructionDefs.definitions,
    PackedVectorInstructionDefs.definitions,
    VectorCompareInstructionDefs.definitions,
    DualVectorInstructionDefs.definitions,
    InterpolationInstructionDefs.definitions,
    MatrixInstructionDefs.definitions,
    DataShareInstructionDefs.definitions,
    BufferInstructionDefs.definitions,
    FlatInstructionDefs.definitions,
    ImageInstructionDefs.definitions,
    ExportInstructionDefs.definitions,
    LDSDirectInstructionDefs.definitions
  ).flatten
  val GlobalOpcodes: Set[Opcode] = Set(
    FlatInstructionDefs.GlobalAtomicAdd,
    FlatInstructionDefs.GlobalAtomicAddF32,
    FlatInstructionDefs.GlobalAtomicAddF64,
    FlatInstructionDefs.GlobalAtomicAddU32,
    FlatInstructionDefs.GlobalAtomicAddU64,
    FlatInstructionDefs.GlobalAtomicAddX2,
    FlatInstructionDefs.GlobalAtomicAnd,
    FlatInstructionDefs.GlobalAtomicAndB32,
    FlatInstructionDefs.GlobalAtomicAndB64,
    FlatInstructionDefs.GlobalAtomicAndX2,
    FlatInstructionDefs.GlobalAtomicCmpswap,
    FlatInstructionDefs.GlobalAtomicCmpswapB32,
    FlatInstructionDefs.GlobalAtomicCmpswapB64,
    FlatInstructionDefs.GlobalAtomicCmpswapF32,
    FlatInstructionDefs.GlobalAtomicCmpswapX2,
    FlatInstructionDefs.GlobalAtomicCondSubU32,
    FlatInstructionDefs.GlobalAtomicCsub,
    FlatInstructionDefs.GlobalAtomicCsubU32,
    FlatInstructionDefs.GlobalAtomicDec,
    FlatInstructionDefs.GlobalAtomicDecU32,
    FlatInstructionDefs.GlobalAtomicDecU64,
    FlatInstructionDefs.GlobalAtomicDecX2,
    FlatInstructionDefs.GlobalAtomicFcmpswap,
    FlatInstructionDefs.GlobalAtomicFcmpswapX2,
    FlatInstructionDefs.GlobalAtomicFmax,
    FlatInstructionDefs.GlobalAtomicFmaxX2,
    FlatInstructionDefs.GlobalAtomicFmin,
    FlatInstructionDefs.GlobalAtomicFminX2,
    FlatInstructionDefs.GlobalAtomicInc,
    FlatInstructionDefs.GlobalAtomicIncU32,
    FlatInstructionDefs.GlobalAtomicIncU64,
    FlatInstructionDefs.GlobalAtomicIncX2,
    FlatInstructionDefs.GlobalAtomicMaxF32,
    FlatInstructionDefs.GlobalAtomicMaxF64,
    FlatInstructionDefs.GlobalAtomicMaxI32,
    FlatInstructionDefs.GlobalAtomicMaxI64,
    FlatInstructionDefs.GlobalAtomicMaxNumF32,
    FlatInstructionDefs.GlobalAtomicMaxNumF64,
    FlatInstructionDefs.GlobalAtomicMaxU32,
    FlatInstructionDefs.GlobalAtomicMaxU64,
    FlatInstructionDefs.GlobalAtomicMinF32,
    FlatInstructionDefs.GlobalAtomicMinF64,
    FlatInstructionDefs.GlobalAtomicMinI32,
    FlatInstructionDefs.GlobalAtomicMinI64,
    FlatInstructionDefs.GlobalAtomicMinNumF32,
    FlatInstructionDefs.GlobalAtomicMinNumF64,
    FlatInstructionDefs.GlobalAtomicMinU32,
    FlatInstructionDefs.GlobalAtomicMinU64,
    FlatInstructionDefs.GlobalAtomicOr,
    FlatInstructionDefs.GlobalAtomicOrB32,
    FlatInstructionDefs.GlobalAtomicOrB64,
    FlatInstructionDefs.GlobalAtomicOrX2,
    FlatInstructionDefs.GlobalAtomicOrderedAddB64,
    FlatInstructionDefs.GlobalAtomicPkAddBf16,
    FlatInstructionDefs.GlobalAtomicPkAddF16,
    FlatInstructionDefs.GlobalAtomicSmax,
    FlatInstructionDefs.GlobalAtomicSmaxX2,
    FlatInstructionDefs.GlobalAtomicSmin,
    FlatInstructionDefs.GlobalAtomicSminX2,
    FlatInstructionDefs.GlobalAtomicSub,
    FlatInstructionDefs.GlobalAtomicSubClampU32,
    FlatInstructionDefs.GlobalAtomicSubU32,
    FlatInstructionDefs.GlobalAtomicSubU64,
    FlatInstructionDefs.GlobalAtomicSubX2,
    FlatInstructionDefs.GlobalAtomicSwap,
    FlatInstructionDefs.GlobalAtomicSwapB32,
    FlatInstructionDefs.GlobalAtomicSwapB64,
    FlatInstructionDefs.GlobalAtomicSwapX2,
    FlatInstructionDefs.GlobalAtomicUmax,
    FlatInstructionDefs.GlobalAtomicUmaxX2,
    FlatInstructionDefs.GlobalAtomicUmin,
    FlatInstructionDefs.GlobalAtomicUminX2,
    FlatInstructionDefs.GlobalAtomicXor,
    FlatInstructionDefs.GlobalAtomicXorB32,
    FlatInstructionDefs.GlobalAtomicXorB64,
    FlatInstructionDefs.GlobalAtomicXorX2,
    FlatInstructionDefs.GlobalInv,
    FlatInstructionDefs.GlobalLoadAddtidB32,
    FlatInstructionDefs.GlobalLoadAsyncToLdsB128,
    FlatInstructionDefs.GlobalLoadAsyncToLdsB32,
    FlatInstructionDefs.GlobalLoadAsyncToLdsB64,
    FlatInstructionDefs.GlobalLoadAsyncToLdsB8,
    FlatInstructionDefs.GlobalLoadB128,
    FlatInstructionDefs.GlobalLoadB32,
    FlatInstructionDefs.GlobalLoadB64,
    FlatInstructionDefs.GlobalLoadB96,
    FlatInstructionDefs.GlobalLoadBlock,
    FlatInstructionDefs.GlobalLoadD16B16,
    FlatInstructionDefs.GlobalLoadD16HiB16,
    FlatInstructionDefs.GlobalLoadD16HiI8,
    FlatInstructionDefs.GlobalLoadD16HiU8,
    FlatInstructionDefs.GlobalLoadD16I8,
    FlatInstructionDefs.GlobalLoadD16U8,
    FlatInstructionDefs.GlobalLoadDword,
    FlatInstructionDefs.GlobalLoadDwordAddtid,
    FlatInstructionDefs.GlobalLoadDwordx2,
    FlatInstructionDefs.GlobalLoadDwordx3,
    FlatInstructionDefs.GlobalLoadDwordx4,
    FlatInstructionDefs.GlobalLoadI16,
    FlatInstructionDefs.GlobalLoadI8,
    FlatInstructionDefs.GlobalLoadLdsDword,
    FlatInstructionDefs.GlobalLoadLdsDwordx3,
    FlatInstructionDefs.GlobalLoadLdsDwordx4,
    FlatInstructionDefs.GlobalLoadLdsSbyte,
    FlatInstructionDefs.GlobalLoadLdsSshort,
    FlatInstructionDefs.GlobalLoadLdsUbyte,
    FlatInstructionDefs.GlobalLoadLdsUshort,
    FlatInstructionDefs.GlobalLoadMonitorB128,
    FlatInstructionDefs.GlobalLoadMonitorB32,
    FlatInstructionDefs.GlobalLoadMonitorB64,
    FlatInstructionDefs.GlobalLoadSbyte,
    FlatInstructionDefs.GlobalLoadSbyteD16,
    FlatInstructionDefs.GlobalLoadSbyteD16Hi,
    FlatInstructionDefs.GlobalLoadShortD16,
    FlatInstructionDefs.GlobalLoadShortD16Hi,
    FlatInstructionDefs.GlobalLoadSshort,
    FlatInstructionDefs.GlobalLoadTr16B128,
    FlatInstructionDefs.GlobalLoadTr4B64,
    FlatInstructionDefs.GlobalLoadTr6B96,
    FlatInstructionDefs.GlobalLoadTr8B64,
    FlatInstructionDefs.GlobalLoadTrB128,
    FlatInstructionDefs.GlobalLoadTrB64,
    FlatInstructionDefs.GlobalLoadU16,
    FlatInstructionDefs.GlobalLoadU8,
    FlatInstructionDefs.GlobalLoadUbyte,
    FlatInstructionDefs.GlobalLoadUbyteD16,
    FlatInstructionDefs.GlobalLoadUbyteD16Hi,
    FlatInstructionDefs.GlobalLoadUshort,
    FlatInstructionDefs.GlobalPrefetchB8,
    FlatInstructionDefs.GlobalStoreAddtidB32,
    FlatInstructionDefs.GlobalStoreAsyncFromLdsB128,
    FlatInstructionDefs.GlobalStoreAsyncFromLdsB32,
    FlatInstructionDefs.GlobalStoreAsyncFromLdsB64,
    FlatInstructionDefs.GlobalStoreAsyncFromLdsB8,
    FlatInstructionDefs.GlobalStoreB128,
    FlatInstructionDefs.GlobalStoreB16,
    FlatInstructionDefs.GlobalStoreB32,
    FlatInstructionDefs.GlobalStoreB64,
    FlatInstructionDefs.GlobalStoreB8,
    FlatInstructionDefs.GlobalStoreB96,
    FlatInstructionDefs.GlobalStoreBlock,
    FlatInstructionDefs.GlobalStoreByte,
    FlatInstructionDefs.GlobalStoreByteD16Hi,
    FlatInstructionDefs.GlobalStoreD16HiB16,
    FlatInstructionDefs.GlobalStoreD16HiB8,
    FlatInstructionDefs.GlobalStoreDword,
    FlatInstructionDefs.GlobalStoreDwordAddtid,
    FlatInstructionDefs.GlobalStoreDwordx2,
    FlatInstructionDefs.GlobalStoreDwordx3,
    FlatInstructionDefs.GlobalStoreDwordx4,
    FlatInstructionDefs.GlobalStoreShort,
    FlatInstructionDefs.GlobalStoreShortD16Hi,
    FlatInstructionDefs.GlobalWb,
    FlatInstructionDefs.GlobalWbinv
  )
  val FlatOpcodes: Set[Opcode] = Set(
    FlatInstructionDefs.FlatAtomicAdd,
    FlatInstructionDefs.FlatAtomicAddF32,
    FlatInstructionDefs.FlatAtomicAddF64,
    FlatInstructionDefs.FlatAtomicAddU32,
    FlatInstructionDefs.FlatAtomicAddU64,
    FlatInstructionDefs.FlatAtomicAddX2,
    FlatInstructionDefs.FlatAtomicAnd,
    FlatInstructionDefs.FlatAtomicAndB32,
    FlatInstructionDefs.FlatAtomicAndB64,
    FlatInstructionDefs.FlatAtomicAndX2,
    FlatInstructionDefs.FlatAtomicCmpswap,
    FlatInstructionDefs.FlatAtomicCmpswapB32,
    FlatInstructionDefs.FlatAtomicCmpswapB64,
    FlatInstructionDefs.FlatAtomicCmpswapF32,
    FlatInstructionDefs.FlatAtomicCmpswapX2,
    FlatInstructionDefs.FlatAtomicCondSubU32,
    FlatInstructionDefs.FlatAtomicDec,
    FlatInstructionDefs.FlatAtomicDecU32,
    FlatInstructionDefs.FlatAtomicDecU64,
    FlatInstructionDefs.FlatAtomicDecX2,
    FlatInstructionDefs.FlatAtomicFcmpswap,
    FlatInstructionDefs.FlatAtomicFcmpswapX2,
    FlatInstructionDefs.FlatAtomicFmax,
    FlatInstructionDefs.FlatAtomicFmaxX2,
    FlatInstructionDefs.FlatAtomicFmin,
    FlatInstructionDefs.FlatAtomicFminX2,
    FlatInstructionDefs.FlatAtomicInc,
    FlatInstructionDefs.FlatAtomicIncU32,
    FlatInstructionDefs.FlatAtomicIncU64,
    FlatInstructionDefs.FlatAtomicIncX2,
    FlatInstructionDefs.FlatAtomicMaxF32,
    FlatInstructionDefs.FlatAtomicMaxF64,
    FlatInstructionDefs.FlatAtomicMaxI32,
    FlatInstructionDefs.FlatAtomicMaxI64,
    FlatInstructionDefs.FlatAtomicMaxNumF32,
    FlatInstructionDefs.FlatAtomicMaxNumF64,
    FlatInstructionDefs.FlatAtomicMaxU32,
    FlatInstructionDefs.FlatAtomicMaxU64,
    FlatInstructionDefs.FlatAtomicMinF32,
    FlatInstructionDefs.FlatAtomicMinF64,
    FlatInstructionDefs.FlatAtomicMinI32,
    FlatInstructionDefs.FlatAtomicMinI64,
    FlatInstructionDefs.FlatAtomicMinNumF32,
    FlatInstructionDefs.FlatAtomicMinNumF64,
    FlatInstructionDefs.FlatAtomicMinU32,
    FlatInstructionDefs.FlatAtomicMinU64,
    FlatInstructionDefs.FlatAtomicOr,
    FlatInstructionDefs.FlatAtomicOrB32,
    FlatInstructionDefs.FlatAtomicOrB64,
    FlatInstructionDefs.FlatAtomicOrX2,
    FlatInstructionDefs.FlatAtomicPkAddBf16,
    FlatInstructionDefs.FlatAtomicPkAddF16,
    FlatInstructionDefs.FlatAtomicSmax,
    FlatInstructionDefs.FlatAtomicSmaxX2,
    FlatInstructionDefs.FlatAtomicSmin,
    FlatInstructionDefs.FlatAtomicSminX2,
    FlatInstructionDefs.FlatAtomicSub,
    FlatInstructionDefs.FlatAtomicSubClampU32,
    FlatInstructionDefs.FlatAtomicSubU32,
    FlatInstructionDefs.FlatAtomicSubU64,
    FlatInstructionDefs.FlatAtomicSubX2,
    FlatInstructionDefs.FlatAtomicSwap,
    FlatInstructionDefs.FlatAtomicSwapB32,
    FlatInstructionDefs.FlatAtomicSwapB64,
    FlatInstructionDefs.FlatAtomicSwapX2,
    FlatInstructionDefs.FlatAtomicUmax,
    FlatInstructionDefs.FlatAtomicUmaxX2,
    FlatInstructionDefs.FlatAtomicUmin,
    FlatInstructionDefs.FlatAtomicUminX2,
    FlatInstructionDefs.FlatAtomicXor,
    FlatInstructionDefs.FlatAtomicXorB32,
    FlatInstructionDefs.FlatAtomicXorB64,
    FlatInstructionDefs.FlatAtomicXorX2,
    FlatInstructionDefs.FlatLoadB128,
    FlatInstructionDefs.FlatLoadB32,
    FlatInstructionDefs.FlatLoadB64,
    FlatInstructionDefs.FlatLoadB96,
    FlatInstructionDefs.FlatLoadD16B16,
    FlatInstructionDefs.FlatLoadD16HiB16,
    FlatInstructionDefs.FlatLoadD16HiI8,
    FlatInstructionDefs.FlatLoadD16HiU8,
    FlatInstructionDefs.FlatLoadD16I8,
    FlatInstructionDefs.FlatLoadD16U8,
    FlatInstructionDefs.FlatLoadDword,
    FlatInstructionDefs.FlatLoadDwordx2,
    FlatInstructionDefs.FlatLoadDwordx3,
    FlatInstructionDefs.FlatLoadDwordx4,
    FlatInstructionDefs.FlatLoadI16,
    FlatInstructionDefs.FlatLoadI8,
    FlatInstructionDefs.FlatLoadMonitorB128,
    FlatInstructionDefs.FlatLoadMonitorB32,
    FlatInstructionDefs.FlatLoadMonitorB64,
    FlatInstructionDefs.FlatLoadSbyte,
    FlatInstructionDefs.FlatLoadSbyteD16,
    FlatInstructionDefs.FlatLoadSbyteD16Hi,
    FlatInstructionDefs.FlatLoadShortD16,
    FlatInstructionDefs.FlatLoadShortD16Hi,
    FlatInstructionDefs.FlatLoadSshort,
    FlatInstructionDefs.FlatLoadU16,
    FlatInstructionDefs.FlatLoadU8,
    FlatInstructionDefs.FlatLoadUbyte,
    FlatInstructionDefs.FlatLoadUbyteD16,
    FlatInstructionDefs.FlatLoadUbyteD16Hi,
    FlatInstructionDefs.FlatLoadUshort,
    FlatInstructionDefs.FlatPrefetchB8,
    FlatInstructionDefs.FlatStoreB128,
    FlatInstructionDefs.FlatStoreB16,
    FlatInstructionDefs.FlatStoreB32,
    FlatInstructionDefs.FlatStoreB64,
    FlatInstructionDefs.FlatStoreB8,
    FlatInstructionDefs.FlatStoreB96,
    FlatInstructionDefs.FlatStoreByte,
    FlatInstructionDefs.FlatStoreByteD16Hi,
    FlatInstructionDefs.FlatStoreD16HiB16,
    FlatInstructionDefs.FlatStoreD16HiB8,
    FlatInstructionDefs.FlatStoreDword,
    FlatInstructionDefs.FlatStoreDwordx2,
    FlatInstructionDefs.FlatStoreDwordx3,
    FlatInstructionDefs.FlatStoreDwordx4,
    FlatInstructionDefs.FlatStoreShort,
    FlatInstructionDefs.FlatStoreShortD16Hi
  )
  val ComparisonOpcodes: Map[Opcode, ComparisonOpcode] = Map(
    VectorCompareInstructionDefs.VCmpClassF32 ->
      ComparisonOpcode(ComparisonKind.ClassFloat32, ComparisonRelation.Class, false),
    VectorCompareInstructionDefs.VCmpClassF64 ->
      ComparisonOpcode(ComparisonKind.ClassFloat64, ComparisonRelation.Class, false),
    VectorCompareInstructionDefs.VCmpEqF16 -> ComparisonOpcode(ComparisonKind.Float16, ComparisonRelation.Equal, false),
    VectorCompareInstructionDefs.VCmpEqF32 -> ComparisonOpcode(ComparisonKind.Float32, ComparisonRelation.Equal, false),
    VectorCompareInstructionDefs.VCmpEqF64 -> ComparisonOpcode(ComparisonKind.Float64, ComparisonRelation.Equal, false),
    VectorCompareInstructionDefs.VCmpEqI16 ->
      ComparisonOpcode(ComparisonKind.Signed16, ComparisonRelation.Equal, false),
    VectorCompareInstructionDefs.VCmpEqI32 ->
      ComparisonOpcode(ComparisonKind.Signed32, ComparisonRelation.Equal, false),
    VectorCompareInstructionDefs.VCmpEqI64 ->
      ComparisonOpcode(ComparisonKind.Signed64, ComparisonRelation.Equal, false),
    VectorCompareInstructionDefs.VCmpEqU16 ->
      ComparisonOpcode(ComparisonKind.Unsigned16, ComparisonRelation.Equal, false),
    VectorCompareInstructionDefs.VCmpEqU32 ->
      ComparisonOpcode(ComparisonKind.Unsigned32, ComparisonRelation.Equal, false),
    VectorCompareInstructionDefs.VCmpEqU64 ->
      ComparisonOpcode(ComparisonKind.Unsigned64, ComparisonRelation.Equal, false),
    VectorCompareInstructionDefs.VCmpGeF16 ->
      ComparisonOpcode(ComparisonKind.Float16, ComparisonRelation.GreaterOrEqual, false),
    VectorCompareInstructionDefs.VCmpGeF32 ->
      ComparisonOpcode(ComparisonKind.Float32, ComparisonRelation.GreaterOrEqual, false),
    VectorCompareInstructionDefs.VCmpGeF64 ->
      ComparisonOpcode(ComparisonKind.Float64, ComparisonRelation.GreaterOrEqual, false),
    VectorCompareInstructionDefs.VCmpGeI16 ->
      ComparisonOpcode(ComparisonKind.Signed16, ComparisonRelation.GreaterOrEqual, false),
    VectorCompareInstructionDefs.VCmpGeI32 ->
      ComparisonOpcode(ComparisonKind.Signed32, ComparisonRelation.GreaterOrEqual, false),
    VectorCompareInstructionDefs.VCmpGeI64 ->
      ComparisonOpcode(ComparisonKind.Signed64, ComparisonRelation.GreaterOrEqual, false),
    VectorCompareInstructionDefs.VCmpGeU16 ->
      ComparisonOpcode(ComparisonKind.Unsigned16, ComparisonRelation.GreaterOrEqual, false),
    VectorCompareInstructionDefs.VCmpGeU32 ->
      ComparisonOpcode(ComparisonKind.Unsigned32, ComparisonRelation.GreaterOrEqual, false),
    VectorCompareInstructionDefs.VCmpGeU64 ->
      ComparisonOpcode(ComparisonKind.Unsigned64, ComparisonRelation.GreaterOrEqual, false),
    VectorCompareInstructionDefs.VCmpGtF16 ->
      ComparisonOpcode(ComparisonKind.Float16, ComparisonRelation.Greater, false),
    VectorCompareInstructionDefs.VCmpGtF32 ->
      ComparisonOpcode(ComparisonKind.Float32, ComparisonRelation.Greater, false),
    VectorCompareInstructionDefs.VCmpGtF64 ->
      ComparisonOpcode(ComparisonKind.Float64, ComparisonRelation.Greater, false),
    VectorCompareInstructionDefs.VCmpGtI16 ->
      ComparisonOpcode(ComparisonKind.Signed16, ComparisonRelation.Greater, false),
    VectorCompareInstructionDefs.VCmpGtI32 ->
      ComparisonOpcode(ComparisonKind.Signed32, ComparisonRelation.Greater, false),
    VectorCompareInstructionDefs.VCmpGtI64 ->
      ComparisonOpcode(ComparisonKind.Signed64, ComparisonRelation.Greater, false),
    VectorCompareInstructionDefs.VCmpGtU16 ->
      ComparisonOpcode(ComparisonKind.Unsigned16, ComparisonRelation.Greater, false),
    VectorCompareInstructionDefs.VCmpGtU32 ->
      ComparisonOpcode(ComparisonKind.Unsigned32, ComparisonRelation.Greater, false),
    VectorCompareInstructionDefs.VCmpGtU64 ->
      ComparisonOpcode(ComparisonKind.Unsigned64, ComparisonRelation.Greater, false),
    VectorCompareInstructionDefs.VCmpLeF16 ->
      ComparisonOpcode(ComparisonKind.Float16, ComparisonRelation.LessOrEqual, false),
    VectorCompareInstructionDefs.VCmpLeF32 ->
      ComparisonOpcode(ComparisonKind.Float32, ComparisonRelation.LessOrEqual, false),
    VectorCompareInstructionDefs.VCmpLeF64 ->
      ComparisonOpcode(ComparisonKind.Float64, ComparisonRelation.LessOrEqual, false),
    VectorCompareInstructionDefs.VCmpLeI16 ->
      ComparisonOpcode(ComparisonKind.Signed16, ComparisonRelation.LessOrEqual, false),
    VectorCompareInstructionDefs.VCmpLeI32 ->
      ComparisonOpcode(ComparisonKind.Signed32, ComparisonRelation.LessOrEqual, false),
    VectorCompareInstructionDefs.VCmpLeI64 ->
      ComparisonOpcode(ComparisonKind.Signed64, ComparisonRelation.LessOrEqual, false),
    VectorCompareInstructionDefs.VCmpLeU16 ->
      ComparisonOpcode(ComparisonKind.Unsigned16, ComparisonRelation.LessOrEqual, false),
    VectorCompareInstructionDefs.VCmpLeU32 ->
      ComparisonOpcode(ComparisonKind.Unsigned32, ComparisonRelation.LessOrEqual, false),
    VectorCompareInstructionDefs.VCmpLeU64 ->
      ComparisonOpcode(ComparisonKind.Unsigned64, ComparisonRelation.LessOrEqual, false),
    VectorCompareInstructionDefs.VCmpLgF16 ->
      ComparisonOpcode(ComparisonKind.Float16, ComparisonRelation.NotEqual, false),
    VectorCompareInstructionDefs.VCmpLgF32 ->
      ComparisonOpcode(ComparisonKind.Float32, ComparisonRelation.NotEqual, false),
    VectorCompareInstructionDefs.VCmpLgF64 ->
      ComparisonOpcode(ComparisonKind.Float64, ComparisonRelation.NotEqual, false),
    VectorCompareInstructionDefs.VCmpLtF16 -> ComparisonOpcode(ComparisonKind.Float16, ComparisonRelation.Less, false),
    VectorCompareInstructionDefs.VCmpLtF32 -> ComparisonOpcode(ComparisonKind.Float32, ComparisonRelation.Less, false),
    VectorCompareInstructionDefs.VCmpLtF64 -> ComparisonOpcode(ComparisonKind.Float64, ComparisonRelation.Less, false),
    VectorCompareInstructionDefs.VCmpLtI16 -> ComparisonOpcode(ComparisonKind.Signed16, ComparisonRelation.Less, false),
    VectorCompareInstructionDefs.VCmpLtI32 -> ComparisonOpcode(ComparisonKind.Signed32, ComparisonRelation.Less, false),
    VectorCompareInstructionDefs.VCmpLtI64 -> ComparisonOpcode(ComparisonKind.Signed64, ComparisonRelation.Less, false),
    VectorCompareInstructionDefs.VCmpLtU16 ->
      ComparisonOpcode(ComparisonKind.Unsigned16, ComparisonRelation.Less, false),
    VectorCompareInstructionDefs.VCmpLtU32 ->
      ComparisonOpcode(ComparisonKind.Unsigned32, ComparisonRelation.Less, false),
    VectorCompareInstructionDefs.VCmpLtU64 ->
      ComparisonOpcode(ComparisonKind.Unsigned64, ComparisonRelation.Less, false),
    VectorCompareInstructionDefs.VCmpNeI16 ->
      ComparisonOpcode(ComparisonKind.Signed16, ComparisonRelation.NotEqual, false),
    VectorCompareInstructionDefs.VCmpNeI32 ->
      ComparisonOpcode(ComparisonKind.Signed32, ComparisonRelation.NotEqual, false),
    VectorCompareInstructionDefs.VCmpNeI64 ->
      ComparisonOpcode(ComparisonKind.Signed64, ComparisonRelation.NotEqual, false),
    VectorCompareInstructionDefs.VCmpNeU16 ->
      ComparisonOpcode(ComparisonKind.Unsigned16, ComparisonRelation.NotEqual, false),
    VectorCompareInstructionDefs.VCmpNeU32 ->
      ComparisonOpcode(ComparisonKind.Unsigned32, ComparisonRelation.NotEqual, false),
    VectorCompareInstructionDefs.VCmpNeU64 ->
      ComparisonOpcode(ComparisonKind.Unsigned64, ComparisonRelation.NotEqual, false),
    VectorCompareInstructionDefs.VCmpNeqF16 ->
      ComparisonOpcode(ComparisonKind.Float16, ComparisonRelation.NotEqual, false),
    VectorCompareInstructionDefs.VCmpNeqF32 ->
      ComparisonOpcode(ComparisonKind.Float32, ComparisonRelation.NotEqual, false),
    VectorCompareInstructionDefs.VCmpNeqF64 ->
      ComparisonOpcode(ComparisonKind.Float64, ComparisonRelation.NotEqual, false),
    VectorCompareInstructionDefs.VCmpNgeF16 ->
      ComparisonOpcode(ComparisonKind.Float16, ComparisonRelation.NotGreaterOrEqual, false),
    VectorCompareInstructionDefs.VCmpNgeF32 ->
      ComparisonOpcode(ComparisonKind.Float32, ComparisonRelation.NotGreaterOrEqual, false),
    VectorCompareInstructionDefs.VCmpNgeF64 ->
      ComparisonOpcode(ComparisonKind.Float64, ComparisonRelation.NotGreaterOrEqual, false),
    VectorCompareInstructionDefs.VCmpNgtF16 ->
      ComparisonOpcode(ComparisonKind.Float16, ComparisonRelation.NotGreater, false),
    VectorCompareInstructionDefs.VCmpNgtF32 ->
      ComparisonOpcode(ComparisonKind.Float32, ComparisonRelation.NotGreater, false),
    VectorCompareInstructionDefs.VCmpNgtF64 ->
      ComparisonOpcode(ComparisonKind.Float64, ComparisonRelation.NotGreater, false),
    VectorCompareInstructionDefs.VCmpNleF16 ->
      ComparisonOpcode(ComparisonKind.Float16, ComparisonRelation.NotLessOrEqual, false),
    VectorCompareInstructionDefs.VCmpNleF32 ->
      ComparisonOpcode(ComparisonKind.Float32, ComparisonRelation.NotLessOrEqual, false),
    VectorCompareInstructionDefs.VCmpNleF64 ->
      ComparisonOpcode(ComparisonKind.Float64, ComparisonRelation.NotLessOrEqual, false),
    VectorCompareInstructionDefs.VCmpNlgF16 ->
      ComparisonOpcode(ComparisonKind.Float16, ComparisonRelation.NotLessOrGreater, false),
    VectorCompareInstructionDefs.VCmpNlgF32 ->
      ComparisonOpcode(ComparisonKind.Float32, ComparisonRelation.NotLessOrGreater, false),
    VectorCompareInstructionDefs.VCmpNlgF64 ->
      ComparisonOpcode(ComparisonKind.Float64, ComparisonRelation.NotLessOrGreater, false),
    VectorCompareInstructionDefs.VCmpNltF16 ->
      ComparisonOpcode(ComparisonKind.Float16, ComparisonRelation.NotLess, false),
    VectorCompareInstructionDefs.VCmpNltF32 ->
      ComparisonOpcode(ComparisonKind.Float32, ComparisonRelation.NotLess, false),
    VectorCompareInstructionDefs.VCmpNltF64 ->
      ComparisonOpcode(ComparisonKind.Float64, ComparisonRelation.NotLess, false),
    VectorCompareInstructionDefs.VCmpOF16 ->
      ComparisonOpcode(ComparisonKind.Float16, ComparisonRelation.Ordered, false),
    VectorCompareInstructionDefs.VCmpOF32 ->
      ComparisonOpcode(ComparisonKind.Float32, ComparisonRelation.Ordered, false),
    VectorCompareInstructionDefs.VCmpOF64 ->
      ComparisonOpcode(ComparisonKind.Float64, ComparisonRelation.Ordered, false),
    VectorCompareInstructionDefs.VCmpUF16 ->
      ComparisonOpcode(ComparisonKind.Float16, ComparisonRelation.Unordered, false),
    VectorCompareInstructionDefs.VCmpUF32 ->
      ComparisonOpcode(ComparisonKind.Float32, ComparisonRelation.Unordered, false),
    VectorCompareInstructionDefs.VCmpUF64 ->
      ComparisonOpcode(ComparisonKind.Float64, ComparisonRelation.Unordered, false),
    VectorCompareInstructionDefs.VCmpxClassF32 ->
      ComparisonOpcode(ComparisonKind.ClassFloat32, ComparisonRelation.Class, true),
    VectorCompareInstructionDefs.VCmpxClassF64 ->
      ComparisonOpcode(ComparisonKind.ClassFloat64, ComparisonRelation.Class, true),
    VectorCompareInstructionDefs.VCmpxEqF16 -> ComparisonOpcode(ComparisonKind.Float16, ComparisonRelation.Equal, true),
    VectorCompareInstructionDefs.VCmpxEqF32 -> ComparisonOpcode(ComparisonKind.Float32, ComparisonRelation.Equal, true),
    VectorCompareInstructionDefs.VCmpxEqF64 -> ComparisonOpcode(ComparisonKind.Float64, ComparisonRelation.Equal, true),
    VectorCompareInstructionDefs.VCmpxEqI16 ->
      ComparisonOpcode(ComparisonKind.Signed16, ComparisonRelation.Equal, true),
    VectorCompareInstructionDefs.VCmpxEqI32 ->
      ComparisonOpcode(ComparisonKind.Signed32, ComparisonRelation.Equal, true),
    VectorCompareInstructionDefs.VCmpxEqI64 ->
      ComparisonOpcode(ComparisonKind.Signed64, ComparisonRelation.Equal, true),
    VectorCompareInstructionDefs.VCmpxEqU16 ->
      ComparisonOpcode(ComparisonKind.Unsigned16, ComparisonRelation.Equal, true),
    VectorCompareInstructionDefs.VCmpxEqU32 ->
      ComparisonOpcode(ComparisonKind.Unsigned32, ComparisonRelation.Equal, true),
    VectorCompareInstructionDefs.VCmpxEqU64 ->
      ComparisonOpcode(ComparisonKind.Unsigned64, ComparisonRelation.Equal, true),
    VectorCompareInstructionDefs.VCmpxGeF16 ->
      ComparisonOpcode(ComparisonKind.Float16, ComparisonRelation.GreaterOrEqual, true),
    VectorCompareInstructionDefs.VCmpxGeF32 ->
      ComparisonOpcode(ComparisonKind.Float32, ComparisonRelation.GreaterOrEqual, true),
    VectorCompareInstructionDefs.VCmpxGeF64 ->
      ComparisonOpcode(ComparisonKind.Float64, ComparisonRelation.GreaterOrEqual, true),
    VectorCompareInstructionDefs.VCmpxGeI16 ->
      ComparisonOpcode(ComparisonKind.Signed16, ComparisonRelation.GreaterOrEqual, true),
    VectorCompareInstructionDefs.VCmpxGeI32 ->
      ComparisonOpcode(ComparisonKind.Signed32, ComparisonRelation.GreaterOrEqual, true),
    VectorCompareInstructionDefs.VCmpxGeI64 ->
      ComparisonOpcode(ComparisonKind.Signed64, ComparisonRelation.GreaterOrEqual, true),
    VectorCompareInstructionDefs.VCmpxGeU16 ->
      ComparisonOpcode(ComparisonKind.Unsigned16, ComparisonRelation.GreaterOrEqual, true),
    VectorCompareInstructionDefs.VCmpxGeU32 ->
      ComparisonOpcode(ComparisonKind.Unsigned32, ComparisonRelation.GreaterOrEqual, true),
    VectorCompareInstructionDefs.VCmpxGeU64 ->
      ComparisonOpcode(ComparisonKind.Unsigned64, ComparisonRelation.GreaterOrEqual, true),
    VectorCompareInstructionDefs.VCmpxGtF16 ->
      ComparisonOpcode(ComparisonKind.Float16, ComparisonRelation.Greater, true),
    VectorCompareInstructionDefs.VCmpxGtF32 ->
      ComparisonOpcode(ComparisonKind.Float32, ComparisonRelation.Greater, true),
    VectorCompareInstructionDefs.VCmpxGtF64 ->
      ComparisonOpcode(ComparisonKind.Float64, ComparisonRelation.Greater, true),
    VectorCompareInstructionDefs.VCmpxGtI16 ->
      ComparisonOpcode(ComparisonKind.Signed16, ComparisonRelation.Greater, true),
    VectorCompareInstructionDefs.VCmpxGtI32 ->
      ComparisonOpcode(ComparisonKind.Signed32, ComparisonRelation.Greater, true),
    VectorCompareInstructionDefs.VCmpxGtI64 ->
      ComparisonOpcode(ComparisonKind.Signed64, ComparisonRelation.Greater, true),
    VectorCompareInstructionDefs.VCmpxGtU16 ->
      ComparisonOpcode(ComparisonKind.Unsigned16, ComparisonRelation.Greater, true),
    VectorCompareInstructionDefs.VCmpxGtU32 ->
      ComparisonOpcode(ComparisonKind.Unsigned32, ComparisonRelation.Greater, true),
    VectorCompareInstructionDefs.VCmpxGtU64 ->
      ComparisonOpcode(ComparisonKind.Unsigned64, ComparisonRelation.Greater, true),
    VectorCompareInstructionDefs.VCmpxLeF16 ->
      ComparisonOpcode(ComparisonKind.Float16, ComparisonRelation.LessOrEqual, true),
    VectorCompareInstructionDefs.VCmpxLeF32 ->
      ComparisonOpcode(ComparisonKind.Float32, ComparisonRelation.LessOrEqual, true),
    VectorCompareInstructionDefs.VCmpxLeF64 ->
      ComparisonOpcode(ComparisonKind.Float64, ComparisonRelation.LessOrEqual, true),
    VectorCompareInstructionDefs.VCmpxLeI16 ->
      ComparisonOpcode(ComparisonKind.Signed16, ComparisonRelation.LessOrEqual, true),
    VectorCompareInstructionDefs.VCmpxLeI32 ->
      ComparisonOpcode(ComparisonKind.Signed32, ComparisonRelation.LessOrEqual, true),
    VectorCompareInstructionDefs.VCmpxLeI64 ->
      ComparisonOpcode(ComparisonKind.Signed64, ComparisonRelation.LessOrEqual, true),
    VectorCompareInstructionDefs.VCmpxLeU16 ->
      ComparisonOpcode(ComparisonKind.Unsigned16, ComparisonRelation.LessOrEqual, true),
    VectorCompareInstructionDefs.VCmpxLeU32 ->
      ComparisonOpcode(ComparisonKind.Unsigned32, ComparisonRelation.LessOrEqual, true),
    VectorCompareInstructionDefs.VCmpxLeU64 ->
      ComparisonOpcode(ComparisonKind.Unsigned64, ComparisonRelation.LessOrEqual, true),
    VectorCompareInstructionDefs.VCmpxLgF16 ->
      ComparisonOpcode(ComparisonKind.Float16, ComparisonRelation.NotEqual, true),
    VectorCompareInstructionDefs.VCmpxLgF32 ->
      ComparisonOpcode(ComparisonKind.Float32, ComparisonRelation.NotEqual, true),
    VectorCompareInstructionDefs.VCmpxLgF64 ->
      ComparisonOpcode(ComparisonKind.Float64, ComparisonRelation.NotEqual, true),
    VectorCompareInstructionDefs.VCmpxLtF16 -> ComparisonOpcode(ComparisonKind.Float16, ComparisonRelation.Less, true),
    VectorCompareInstructionDefs.VCmpxLtF32 -> ComparisonOpcode(ComparisonKind.Float32, ComparisonRelation.Less, true),
    VectorCompareInstructionDefs.VCmpxLtF64 -> ComparisonOpcode(ComparisonKind.Float64, ComparisonRelation.Less, true),
    VectorCompareInstructionDefs.VCmpxLtI16 -> ComparisonOpcode(ComparisonKind.Signed16, ComparisonRelation.Less, true),
    VectorCompareInstructionDefs.VCmpxLtI32 -> ComparisonOpcode(ComparisonKind.Signed32, ComparisonRelation.Less, true),
    VectorCompareInstructionDefs.VCmpxLtI64 -> ComparisonOpcode(ComparisonKind.Signed64, ComparisonRelation.Less, true),
    VectorCompareInstructionDefs.VCmpxLtU16 ->
      ComparisonOpcode(ComparisonKind.Unsigned16, ComparisonRelation.Less, true),
    VectorCompareInstructionDefs.VCmpxLtU32 ->
      ComparisonOpcode(ComparisonKind.Unsigned32, ComparisonRelation.Less, true),
    VectorCompareInstructionDefs.VCmpxLtU64 ->
      ComparisonOpcode(ComparisonKind.Unsigned64, ComparisonRelation.Less, true),
    VectorCompareInstructionDefs.VCmpxNeI16 ->
      ComparisonOpcode(ComparisonKind.Signed16, ComparisonRelation.NotEqual, true),
    VectorCompareInstructionDefs.VCmpxNeI32 ->
      ComparisonOpcode(ComparisonKind.Signed32, ComparisonRelation.NotEqual, true),
    VectorCompareInstructionDefs.VCmpxNeI64 ->
      ComparisonOpcode(ComparisonKind.Signed64, ComparisonRelation.NotEqual, true),
    VectorCompareInstructionDefs.VCmpxNeU16 ->
      ComparisonOpcode(ComparisonKind.Unsigned16, ComparisonRelation.NotEqual, true),
    VectorCompareInstructionDefs.VCmpxNeU32 ->
      ComparisonOpcode(ComparisonKind.Unsigned32, ComparisonRelation.NotEqual, true),
    VectorCompareInstructionDefs.VCmpxNeU64 ->
      ComparisonOpcode(ComparisonKind.Unsigned64, ComparisonRelation.NotEqual, true),
    VectorCompareInstructionDefs.VCmpxNeqF16 ->
      ComparisonOpcode(ComparisonKind.Float16, ComparisonRelation.NotEqual, true),
    VectorCompareInstructionDefs.VCmpxNeqF32 ->
      ComparisonOpcode(ComparisonKind.Float32, ComparisonRelation.NotEqual, true),
    VectorCompareInstructionDefs.VCmpxNeqF64 ->
      ComparisonOpcode(ComparisonKind.Float64, ComparisonRelation.NotEqual, true),
    VectorCompareInstructionDefs.VCmpxNgeF16 ->
      ComparisonOpcode(ComparisonKind.Float16, ComparisonRelation.NotGreaterOrEqual, true),
    VectorCompareInstructionDefs.VCmpxNgeF32 ->
      ComparisonOpcode(ComparisonKind.Float32, ComparisonRelation.NotGreaterOrEqual, true),
    VectorCompareInstructionDefs.VCmpxNgeF64 ->
      ComparisonOpcode(ComparisonKind.Float64, ComparisonRelation.NotGreaterOrEqual, true),
    VectorCompareInstructionDefs.VCmpxNgtF16 ->
      ComparisonOpcode(ComparisonKind.Float16, ComparisonRelation.NotGreater, true),
    VectorCompareInstructionDefs.VCmpxNgtF32 ->
      ComparisonOpcode(ComparisonKind.Float32, ComparisonRelation.NotGreater, true),
    VectorCompareInstructionDefs.VCmpxNgtF64 ->
      ComparisonOpcode(ComparisonKind.Float64, ComparisonRelation.NotGreater, true),
    VectorCompareInstructionDefs.VCmpxNleF16 ->
      ComparisonOpcode(ComparisonKind.Float16, ComparisonRelation.NotLessOrEqual, true),
    VectorCompareInstructionDefs.VCmpxNleF32 ->
      ComparisonOpcode(ComparisonKind.Float32, ComparisonRelation.NotLessOrEqual, true),
    VectorCompareInstructionDefs.VCmpxNleF64 ->
      ComparisonOpcode(ComparisonKind.Float64, ComparisonRelation.NotLessOrEqual, true),
    VectorCompareInstructionDefs.VCmpxNlgF16 ->
      ComparisonOpcode(ComparisonKind.Float16, ComparisonRelation.NotLessOrGreater, true),
    VectorCompareInstructionDefs.VCmpxNlgF32 ->
      ComparisonOpcode(ComparisonKind.Float32, ComparisonRelation.NotLessOrGreater, true),
    VectorCompareInstructionDefs.VCmpxNlgF64 ->
      ComparisonOpcode(ComparisonKind.Float64, ComparisonRelation.NotLessOrGreater, true),
    VectorCompareInstructionDefs.VCmpxNltF16 ->
      ComparisonOpcode(ComparisonKind.Float16, ComparisonRelation.NotLess, true),
    VectorCompareInstructionDefs.VCmpxNltF32 ->
      ComparisonOpcode(ComparisonKind.Float32, ComparisonRelation.NotLess, true),
    VectorCompareInstructionDefs.VCmpxNltF64 ->
      ComparisonOpcode(ComparisonKind.Float64, ComparisonRelation.NotLess, true),
    VectorCompareInstructionDefs.VCmpxOF16 ->
      ComparisonOpcode(ComparisonKind.Float16, ComparisonRelation.Ordered, true),
    VectorCompareInstructionDefs.VCmpxOF32 ->
      ComparisonOpcode(ComparisonKind.Float32, ComparisonRelation.Ordered, true),
    VectorCompareInstructionDefs.VCmpxOF64 ->
      ComparisonOpcode(ComparisonKind.Float64, ComparisonRelation.Ordered, true),
    VectorCompareInstructionDefs.VCmpxUF16 ->
      ComparisonOpcode(ComparisonKind.Float16, ComparisonRelation.Unordered, true),
    VectorCompareInstructionDefs.VCmpxUF32 ->
      ComparisonOpcode(ComparisonKind.Float32, ComparisonRelation.Unordered, true),
    VectorCompareInstructionDefs.VCmpxUF64 ->
      ComparisonOpcode(ComparisonKind.Float64, ComparisonRelation.Unordered, true)
  )

  val definitions: Vec[Opcode] = AllOpcodes

  private lazy val byName = definitions.groupBy(_.opcode.raw)
  def forOpcode(name: String): Vec[Opcode] = byName.getOrElse(name.toLowerCaseRoot, Vec.empty)
}

abstract class Opcode(val opcode: NonEmpty[String], private val metadataId: Int) {
  lazy val meta: Metadata = Metadata.Catalogue.metadata(metadataId)
  override final def toString: String = opcode
}
