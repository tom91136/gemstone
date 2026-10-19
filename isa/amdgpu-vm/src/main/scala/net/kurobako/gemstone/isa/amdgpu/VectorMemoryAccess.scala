package net.kurobako.gemstone.isa.amdgpu

import BufferInstructionDefs.*
import FlatInstructionDefs.*
import VMIR.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.vm.*

private object VectorMemoryAccess {
  def of(opcode: Opcode): Option[VectorMemoryAccess] = opcode match {
    case GlobalLoadUbyte | GlobalLoadU8 | FlatLoadUbyte | FlatLoadU8 | ScratchLoadUbyte | ScratchLoadU8 | BufferLoadUbyte |
        BufferLoadU8 =>
      Some(Load(1, false, LoadPlacement.Full))
    case GlobalLoadSbyte | GlobalLoadI8 | FlatLoadSbyte | FlatLoadI8 | ScratchLoadSbyte | ScratchLoadI8 | BufferLoadSbyte |
        BufferLoadI8 =>
      Some(Load(1, true, LoadPlacement.Full))
    case GlobalLoadUshort | GlobalLoadU16 | FlatLoadUshort | FlatLoadU16 | ScratchLoadUshort | ScratchLoadU16 |
        BufferLoadUshort | BufferLoadU16 =>
      Some(Load(2, false, LoadPlacement.Full))
    case GlobalLoadSshort | GlobalLoadI16 | FlatLoadSshort | FlatLoadI16 | ScratchLoadSshort | ScratchLoadI16 |
        BufferLoadSshort | BufferLoadI16 =>
      Some(Load(2, true, LoadPlacement.Full))
    case GlobalLoadShortD16 | FlatLoadShortD16 | ScratchLoadShortD16 | BufferLoadShortD16 | BufferLoadD16B16 |
        GlobalLoadD16B16 | FlatLoadD16B16 | ScratchLoadD16B16 =>
      Some(Load(2, false, LoadPlacement.Low))
    case GlobalLoadShortD16Hi | FlatLoadShortD16Hi | ScratchLoadShortD16Hi | BufferLoadShortD16Hi | BufferLoadD16HiB16 |
        GlobalLoadD16HiB16 | FlatLoadD16HiB16 | ScratchLoadD16HiB16 =>
      Some(Load(2, false, LoadPlacement.High))
    case GlobalLoadUbyteD16 | FlatLoadUbyteD16 | ScratchLoadUbyteD16 | BufferLoadUbyteD16 | GlobalLoadD16U8 |
        FlatLoadD16U8 | ScratchLoadD16U8 | BufferLoadD16U8 =>
      Some(Load(1, false, LoadPlacement.Low))
    case GlobalLoadUbyteD16Hi | FlatLoadUbyteD16Hi | ScratchLoadUbyteD16Hi | BufferLoadUbyteD16Hi | GlobalLoadD16HiU8 |
        FlatLoadD16HiU8 | ScratchLoadD16HiU8 | BufferLoadD16HiU8 =>
      Some(Load(1, false, LoadPlacement.High))
    case GlobalLoadSbyteD16 | FlatLoadSbyteD16 | ScratchLoadSbyteD16 | BufferLoadSbyteD16 | GlobalLoadD16I8 |
        FlatLoadD16I8 | ScratchLoadD16I8 | BufferLoadD16I8 =>
      Some(Load(1, true, LoadPlacement.Low))
    case GlobalLoadSbyteD16Hi | FlatLoadSbyteD16Hi | ScratchLoadSbyteD16Hi | BufferLoadSbyteD16Hi | GlobalLoadD16HiI8 |
        FlatLoadD16HiI8 | ScratchLoadD16HiI8 | BufferLoadD16HiI8 =>
      Some(Load(1, true, LoadPlacement.High))
    case GlobalLoadDword | GlobalLoadB32 | FlatLoadDword | FlatLoadB32 | ScratchLoadDword | ScratchLoadB32 | BufferLoadDword |
        BufferLoadB32 =>
      Some(Load(4, false, LoadPlacement.Full))
    case GlobalLoadDwordx2 | GlobalLoadB64 | FlatLoadDwordx2 | FlatLoadB64 | ScratchLoadDwordx2 | ScratchLoadB64 |
        BufferLoadDwordx2 | BufferLoadB64 =>
      Some(Load(8, false, LoadPlacement.Full))
    case GlobalLoadDwordx3 | GlobalLoadB96 | FlatLoadDwordx3 | FlatLoadB96 | ScratchLoadDwordx3 | ScratchLoadB96 |
        BufferLoadDwordx3 | BufferLoadB96 =>
      Some(Load(12, false, LoadPlacement.Full))
    case GlobalLoadDwordx4 | GlobalLoadB128 | FlatLoadDwordx4 | FlatLoadB128 | ScratchLoadDwordx4 | ScratchLoadB128 |
        BufferLoadDwordx4 | BufferLoadB128 =>
      Some(Load(16, false, LoadPlacement.Full))
    case GlobalStoreByte | GlobalStoreB8 | FlatStoreByte | FlatStoreB8 | ScratchStoreByte | ScratchStoreB8 | BufferStoreByte |
        BufferStoreB8 =>
      Some(Store(1, false))
    case GlobalStoreShort | GlobalStoreB16 | FlatStoreShort | FlatStoreB16 | ScratchStoreShort | ScratchStoreB16 |
        BufferStoreShort | BufferStoreB16 =>
      Some(Store(2, false))
    case GlobalStoreDword | GlobalStoreB32 | FlatStoreDword | FlatStoreB32 | ScratchStoreDword | ScratchStoreB32 |
        BufferStoreDword | BufferStoreB32 =>
      Some(Store(4, false))
    case GlobalStoreDwordx2 | GlobalStoreB64 | FlatStoreDwordx2 | FlatStoreB64 | ScratchStoreDwordx2 |
        ScratchStoreB64 | BufferStoreDwordx2 | BufferStoreB64 => Some(Store(8, false))
    case GlobalStoreDwordx3 | GlobalStoreB96 | FlatStoreDwordx3 | FlatStoreB96 | ScratchStoreDwordx3 |
        ScratchStoreB96 | BufferStoreDwordx3 | BufferStoreB96 => Some(Store(12, false))
    case GlobalStoreDwordx4 | GlobalStoreB128 | FlatStoreDwordx4 | FlatStoreB128 | ScratchStoreDwordx4 |
        ScratchStoreB128 | BufferStoreDwordx4 | BufferStoreB128 => Some(Store(16, false))
    case GlobalStoreByteD16Hi | GlobalStoreD16HiB8 | FlatStoreByteD16Hi | FlatStoreD16HiB8 | ScratchStoreByteD16Hi |
        ScratchStoreD16HiB8 | BufferStoreByteD16Hi | BufferStoreD16HiB8 => Some(Store(1, true))
    case GlobalStoreShortD16Hi | GlobalStoreD16HiB16 | FlatStoreShortD16Hi | FlatStoreD16HiB16 |
        ScratchStoreShortD16Hi | ScratchStoreD16HiB16 | BufferStoreShortD16Hi | BufferStoreD16HiB16 =>
      Some(Store(2, true))
    case GlobalAtomicAdd | GlobalAtomicAddU32 | FlatAtomicAdd | FlatAtomicAddU32 | BufferAtomicAdd |
        BufferAtomicAddU32 => Some(Atomic(AtomicOperation.Add, 4))
    case GlobalAtomicAddX2 | GlobalAtomicAddU64 | FlatAtomicAddX2 | FlatAtomicAddU64 | BufferAtomicAddX2 |
        BufferAtomicAddU64 => Some(Atomic(AtomicOperation.Add, 8))
    case GlobalAtomicSub | GlobalAtomicSubU32 | FlatAtomicSub | FlatAtomicSubU32 | BufferAtomicSub |
        BufferAtomicSubU32 => Some(Atomic(AtomicOperation.Subtract, 4))
    case GlobalAtomicSubX2 | GlobalAtomicSubU64 | FlatAtomicSubX2 | FlatAtomicSubU64 | BufferAtomicSubX2 |
        BufferAtomicSubU64 => Some(Atomic(AtomicOperation.Subtract, 8))
    case GlobalAtomicSwap | GlobalAtomicSwapB32 | FlatAtomicSwap | FlatAtomicSwapB32 | BufferAtomicSwap |
        BufferAtomicSwapB32 => Some(Atomic(AtomicOperation.Exchange, 4))
    case GlobalAtomicSwapX2 | GlobalAtomicSwapB64 | FlatAtomicSwapX2 | FlatAtomicSwapB64 | BufferAtomicSwapX2 |
        BufferAtomicSwapB64 => Some(Atomic(AtomicOperation.Exchange, 8))
    case GlobalAtomicInc | GlobalAtomicIncU32 | FlatAtomicInc | FlatAtomicIncU32 | BufferAtomicInc |
        BufferAtomicIncU32 => Some(Atomic(AtomicOperation.IncrementWrap, 4))
    case GlobalAtomicIncX2 | GlobalAtomicIncU64 | FlatAtomicIncX2 | FlatAtomicIncU64 | BufferAtomicIncX2 |
        BufferAtomicIncU64 => Some(Atomic(AtomicOperation.IncrementWrap, 8))
    case GlobalAtomicDec | GlobalAtomicDecU32 | FlatAtomicDec | FlatAtomicDecU32 | BufferAtomicDec |
        BufferAtomicDecU32 => Some(Atomic(AtomicOperation.DecrementWrap, 4))
    case GlobalAtomicDecX2 | GlobalAtomicDecU64 | FlatAtomicDecX2 | FlatAtomicDecU64 | BufferAtomicDecX2 |
        BufferAtomicDecU64 => Some(Atomic(AtomicOperation.DecrementWrap, 8))
    case GlobalAtomicUmin | GlobalAtomicMinU32 | FlatAtomicUmin | FlatAtomicMinU32 | BufferAtomicUmin |
        BufferAtomicMinU32 => Some(Atomic(AtomicOperation.MinimumUnsigned, 4))
    case GlobalAtomicUminX2 | GlobalAtomicMinU64 | FlatAtomicUminX2 | FlatAtomicMinU64 | BufferAtomicUminX2 |
        BufferAtomicMinU64 => Some(Atomic(AtomicOperation.MinimumUnsigned, 8))
    case GlobalAtomicUmax | GlobalAtomicMaxU32 | FlatAtomicUmax | FlatAtomicMaxU32 | BufferAtomicUmax |
        BufferAtomicMaxU32 => Some(Atomic(AtomicOperation.MaximumUnsigned, 4))
    case GlobalAtomicUmaxX2 | GlobalAtomicMaxU64 | FlatAtomicUmaxX2 | FlatAtomicMaxU64 | BufferAtomicUmaxX2 |
        BufferAtomicMaxU64 => Some(Atomic(AtomicOperation.MaximumUnsigned, 8))
    case GlobalAtomicSmin | GlobalAtomicMinI32 | FlatAtomicSmin | FlatAtomicMinI32 | BufferAtomicSmin |
        BufferAtomicMinI32 => Some(Atomic(AtomicOperation.MinimumSigned, 4))
    case GlobalAtomicSminX2 | GlobalAtomicMinI64 | FlatAtomicSminX2 | FlatAtomicMinI64 | BufferAtomicSminX2 |
        BufferAtomicMinI64 => Some(Atomic(AtomicOperation.MinimumSigned, 8))
    case GlobalAtomicSmax | GlobalAtomicMaxI32 | FlatAtomicSmax | FlatAtomicMaxI32 | BufferAtomicSmax |
        BufferAtomicMaxI32 => Some(Atomic(AtomicOperation.MaximumSigned, 4))
    case GlobalAtomicSmaxX2 | GlobalAtomicMaxI64 | FlatAtomicSmaxX2 | FlatAtomicMaxI64 | BufferAtomicSmaxX2 |
        BufferAtomicMaxI64 => Some(Atomic(AtomicOperation.MaximumSigned, 8))
    case GlobalAtomicAnd | GlobalAtomicAndB32 | FlatAtomicAnd | FlatAtomicAndB32 | BufferAtomicAnd |
        BufferAtomicAndB32 => Some(Atomic(AtomicOperation.And, 4))
    case GlobalAtomicAndX2 | GlobalAtomicAndB64 | FlatAtomicAndX2 | FlatAtomicAndB64 | BufferAtomicAndX2 |
        BufferAtomicAndB64 => Some(Atomic(AtomicOperation.And, 8))
    case GlobalAtomicOr | GlobalAtomicOrB32 | FlatAtomicOr | FlatAtomicOrB32 | BufferAtomicOr | BufferAtomicOrB32 =>
      Some(Atomic(AtomicOperation.Or, 4))
    case GlobalAtomicOrX2 | GlobalAtomicOrB64 | FlatAtomicOrX2 | FlatAtomicOrB64 | BufferAtomicOrX2 |
        BufferAtomicOrB64 => Some(Atomic(AtomicOperation.Or, 8))
    case GlobalAtomicXor | GlobalAtomicXorB32 | FlatAtomicXor | FlatAtomicXorB32 | BufferAtomicXor |
        BufferAtomicXorB32 => Some(Atomic(AtomicOperation.Xor, 4))
    case GlobalAtomicXorX2 | GlobalAtomicXorB64 | FlatAtomicXorX2 | FlatAtomicXorB64 | BufferAtomicXorX2 |
        BufferAtomicXorB64 => Some(Atomic(AtomicOperation.Xor, 8))
    case GlobalAtomicAddF32 | FlatAtomicAddF32 | BufferAtomicAddF32 => Some(Atomic(AtomicOperation.FloatAdd, 4))
    case GlobalAtomicAddF64 | FlatAtomicAddF64 | BufferAtomicAddF64 => Some(Atomic(AtomicOperation.FloatAdd, 8))
    case GlobalAtomicPkAddF16 | FlatAtomicPkAddF16                  => Some(Atomic(AtomicOperation.PackedHalfAdd, 4))
    case GlobalAtomicMinNumF32 | FlatAtomicMinNumF32                => Some(Atomic(AtomicOperation.FloatMinimum, 4))
    case GlobalAtomicMaxNumF32 | FlatAtomicMaxNumF32                => Some(Atomic(AtomicOperation.FloatMaximum, 4))
    case GlobalAtomicMinNumF64 | FlatAtomicMinNumF64                => Some(Atomic(AtomicOperation.FloatMinimum, 8))
    case GlobalAtomicMaxNumF64 | FlatAtomicMaxNumF64                => Some(Atomic(AtomicOperation.FloatMaximum, 8))
    case GlobalAtomicCsub | GlobalAtomicCsubU32 | GlobalAtomicSubClampU32 | FlatAtomicSubClampU32 =>
      Some(Extended(ExtendedAtomicOperation.SubtractClamp, 4))
    case GlobalAtomicCondSubU32 | FlatAtomicCondSubU32 =>
      Some(Extended(ExtendedAtomicOperation.ConditionalSubtract, 4))
    case GlobalAtomicFcmpswap | GlobalAtomicCmpswapF32 | FlatAtomicFcmpswap | FlatAtomicCmpswapF32 =>
      Some(Extended(ExtendedAtomicOperation.FloatCompareSwap, 4))
    case GlobalAtomicFcmpswapX2 | FlatAtomicFcmpswapX2 => Some(Extended(ExtendedAtomicOperation.FloatCompareSwap, 8))
    case GlobalAtomicPkAddBf16 | FlatAtomicPkAddBf16   => Some(Atomic(AtomicOperation.PackedBFloat16Add, 4))
    case GlobalAtomicFmin | GlobalAtomicMinF32 | FlatAtomicFmin | FlatAtomicMinF32 | BufferAtomicFmin |
        BufferAtomicMinF32 => Some(Atomic(AtomicOperation.FloatMinimum, 4))
    case GlobalAtomicFminX2 | GlobalAtomicMinF64 | FlatAtomicFminX2 | FlatAtomicMinF64 | BufferAtomicFminX2 |
        BufferAtomicMinF64 => Some(Atomic(AtomicOperation.FloatMinimum, 8))
    case GlobalAtomicFmax | GlobalAtomicMaxF32 | FlatAtomicFmax | FlatAtomicMaxF32 | BufferAtomicFmax |
        BufferAtomicMaxF32 => Some(Atomic(AtomicOperation.FloatMaximum, 4))
    case GlobalAtomicFmaxX2 | GlobalAtomicMaxF64 | FlatAtomicFmaxX2 | FlatAtomicMaxF64 | BufferAtomicFmaxX2 |
        BufferAtomicMaxF64 => Some(Atomic(AtomicOperation.FloatMaximum, 8))
    case GlobalAtomicCmpswap | GlobalAtomicCmpswapB32 | FlatAtomicCmpswap | FlatAtomicCmpswapB32 |
        BufferAtomicCmpswap | BufferAtomicCmpswapB32 => Some(CompareSwap(4))
    case GlobalAtomicCmpswapX2 | GlobalAtomicCmpswapB64 | FlatAtomicCmpswapX2 | FlatAtomicCmpswapB64 |
        BufferAtomicCmpswapX2 | BufferAtomicCmpswapB64 => Some(CompareSwap(8))
    case _ => None
  }
}

private enum VectorMemoryAccess {
  case Load(bytes: Int in 1 ~ 16, signed: Boolean, placement: LoadPlacement)
  case Store(bytes: Int in 1 ~ 16, high: Boolean)
  case Atomic(operation: AtomicOperation, bytes: Int in (4, 8))
  case CompareSwap(bytes: Int in (4, 8))

  /** An [[ExtendedAtomicOperation]] whose data follows its operand, as the comparand follows a compare-swap's data.
    */
  case Extended(operation: ExtendedAtomicOperation, bytes: Int in (4, 8))
}
