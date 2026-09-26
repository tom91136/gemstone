// Generated PTX instruction model from the pinned NVIDIA ISA 9.3 specification. Regenerate with: sbt codegen
package net.kurobako.gemstone.isa.ptx.v9_3

import Program.*
import ISA.*
import Metadata.*

import net.kurobako.gemstone.{NonEmptyString, NonEmptyVec, NonNegativeInt, PositiveInt, Vec}
import net.kurobako.gemstone.Vec.*

@javax.annotation.processing.Generated(Array("sbt codegen"))
enum Instruction(val definition: Instruction.Definition, operandValues: Operand*) {
  case Abs(dest: Operand, srcA: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.Abs, dest, srcA)
  case Activemask(dest: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.Activemask, dest)
  case Add(dest: Operand, srcA: Operand, srcB: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.Add, dest, srcA, srcB)
  case AddCc(dest: Operand, srcA: Operand, srcB: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.AddCc, dest, srcA, srcB)
  case Addc(dest: Operand, srcA: Operand, srcB: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.Addc, dest, srcA, srcB)
  case Alloca(
      pointer: Operand,
      size: Operand,
      alignment: Option[Operand],
      mods: Vec[Modifier],
      pred: Option[Predication]
  ) extends Instruction(Instruction.Definition.Alloca, (Vec(pointer, size) ++ alignment.toVec)*)
  case And(dest: Operand, srcA: Operand, srcB: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.And, dest, srcA, srcB)
  case ApplyPriority(address: Operand, size: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.ApplyPriority, address, size)
  case Atom(
      dest: Operand,
      address: Operand,
      value: Operand,
      compareOrCachePolicy: Option[Operand],
      mods: Vec[Modifier],
      pred: Option[Predication]
  ) extends Instruction(Instruction.Definition.Atom, (Vec(dest, address, value) ++ compareOrCachePolicy.toVec)*)
  case Bar(
      barrier: Operand,
      threadCount: Option[Operand],
      predicate: Option[Operand],
      value: Option[Operand],
      mods: Vec[Modifier],
      pred: Option[Predication]
  ) extends Instruction(
        Instruction.Definition.Bar,
        (Vec(barrier) ++ threadCount.toVec ++ predicate.toVec ++ value.toVec)*
      )
  case BarWarpSync(barrier: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.BarWarpSync, barrier)
  case Barrier(
      barrier: Operand,
      threadCount: Option[Operand],
      predicate: Option[Operand],
      value: Option[Operand],
      mods: Vec[Modifier],
      pred: Option[Predication]
  ) extends Instruction(
        Instruction.Definition.Barrier,
        (Vec(barrier) ++ threadCount.toVec ++ predicate.toVec ++ value.toVec)*
      )
  case BarrierCluster(mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.BarrierCluster)
  case Bfe(dest: Operand, srcA: Operand, srcB: Operand, srcC: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.Bfe, dest, srcA, srcB, srcC)
  case Bfi(
      dest: Operand,
      insertValue: Operand,
      baseValue: Operand,
      bitPosition: Operand,
      bitLength: Operand,
      mods: Vec[Modifier],
      pred: Option[Predication]
  ) extends Instruction(Instruction.Definition.Bfi, dest, insertValue, baseValue, bitPosition, bitLength)
  case Bfind(dest: Operand, srcA: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.Bfind, dest, srcA)
  case Bmsk(dest: Operand, srcA: Operand, srcB: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.Bmsk, dest, srcA, srcB)
  case Bra(target: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.Bra, target)
  case Brev(dest: Operand, srcA: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.Brev, dest, srcA)
  case Brkpt(mods: Vec[Modifier], pred: Option[Predication]) extends Instruction(Instruction.Definition.Brkpt)
  case BrxIdx(index: Operand, tlist: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.BrxIdx, index, tlist)
  case Call(
      returnParams: Option[Operand],
      function: Operand,
      params: Option[Operand],
      targets: Option[Operand],
      mods: Vec[Modifier],
      pred: Option[Predication]
  ) extends Instruction(
        Instruction.Definition.Call,
        (returnParams.toVec ++ Vec(function) ++ params.toVec ++ targets.toVec)*
      )
  case Clmad(dest: Operand, srcA: Operand, srcB: Operand, srcC: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.Clmad, dest, srcA, srcB, srcC)
  case ClusterLaunchControlQueryCancel(
      predicate: Operand,
      tryCancelResponse: Operand,
      mods: Vec[Modifier],
      pred: Option[Predication]
  ) extends Instruction(Instruction.Definition.ClusterLaunchControlQueryCancel, predicate, tryCancelResponse)
  case ClusterLaunchControlTryCancel(
      address: Operand,
      barrierAddr: Operand,
      mods: Vec[Modifier],
      pred: Option[Predication]
  ) extends Instruction(Instruction.Definition.ClusterLaunchControlTryCancel, address, barrierAddr)
  case Clz(dest: Operand, srcA: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.Clz, dest, srcA)
  case Cnot(dest: Operand, srcA: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.Cnot, dest, srcA)
  case CopySign(dest: Operand, srcA: Operand, srcB: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.CopySign, dest, srcA, srcB)
  case Cos(dest: Operand, srcA: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.Cos, dest, srcA)
  case CpAsync(
      destAddr: Operand,
      srcAddr: Operand,
      copySize: Operand,
      srcSizeOrIgnore: Option[Operand],
      cachePolicy: Option[Operand],
      mods: Vec[Modifier],
      pred: Option[Predication]
  ) extends Instruction(
        Instruction.Definition.CpAsync,
        (Vec(destAddr, srcAddr, copySize) ++ srcSizeOrIgnore.toVec ++ cachePolicy.toVec)*
      )
  case CpAsyncBulkMBarrier(
      destAddr: Operand,
      srcAddr: Operand,
      size: Operand,
      barrierAddr: Operand,
      ignoredBytesLeft: Option[Operand],
      ignoredBytesRight: Option[Operand],
      ctaMask: Option[Operand],
      cachePolicy: Option[Operand],
      mods: Vec[Modifier],
      pred: Option[Predication]
  ) extends Instruction(
        Instruction.Definition.CpAsyncBulkMBarrier,
        (Vec(destAddr, srcAddr, size) ++ ignoredBytesLeft.toVec ++ ignoredBytesRight.toVec ++ Vec(barrierAddr) ++
          ctaMask.toVec ++ cachePolicy.toVec)*
      )
  case CpAsyncBulkGroup(
      destAddr: Operand,
      srcAddr: Operand,
      size: Operand,
      cachePolicy: Option[Operand],
      byteMask: Option[Operand],
      mods: Vec[Modifier],
      pred: Option[Predication]
  ) extends Instruction(
        Instruction.Definition.CpAsyncBulkGroup,
        (Vec(destAddr, srcAddr, size) ++ cachePolicy.toVec ++ byteMask.toVec)*
      )
  case CpAsyncBulkCommitGroup(mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.CpAsyncBulkCommitGroup)
  case CpAsyncBulkPrefetch(
      srcAddr: Operand,
      size: Operand,
      cachePolicy: Option[Operand],
      mods: Vec[Modifier],
      pred: Option[Predication]
  ) extends Instruction(Instruction.Definition.CpAsyncBulkPrefetch, (Vec(srcAddr, size) ++ cachePolicy.toVec)*)
  case CpAsyncBulkPrefetchTensor(
      tensorMapAndCoordinates: Operand,
      im2colInfo: Option[Operand],
      cachePolicy: Option[Operand],
      mods: Vec[Modifier],
      pred: Option[Predication]
  ) extends Instruction(
        Instruction.Definition.CpAsyncBulkPrefetchTensor,
        (Vec(tensorMapAndCoordinates) ++ im2colInfo.toVec ++ cachePolicy.toVec)*
      )
  case CpAsyncBulkTensorMBarrier(
      destAddr: Operand,
      tensorMapAndCoordinates: Operand,
      barrierAddr: Operand,
      im2colInfo: Option[Operand],
      ctaMask: Option[Operand],
      cachePolicy: Option[Operand],
      mods: Vec[Modifier],
      pred: Option[Predication]
  ) extends Instruction(
        Instruction.Definition.CpAsyncBulkTensorMBarrier,
        (Vec(destAddr, tensorMapAndCoordinates, barrierAddr) ++ im2colInfo.toVec ++ ctaMask.toVec ++ cachePolicy.toVec)*
      )
  case CpAsyncBulkTensorGroup(
      tensorMapAndCoordinates: Operand,
      srcAddr: Operand,
      cachePolicy: Option[Operand],
      mods: Vec[Modifier],
      pred: Option[Predication]
  ) extends Instruction(
        Instruction.Definition.CpAsyncBulkTensorGroup,
        (Vec(tensorMapAndCoordinates, srcAddr) ++ cachePolicy.toVec)*
      )
  case CpAsyncBulkWaitGroup(pendingGroupCount: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.CpAsyncBulkWaitGroup, pendingGroupCount)
  case CpAsyncCommitGroup(mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.CpAsyncCommitGroup)
  case CpAsyncMbarrierArrive(address: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.CpAsyncMbarrierArrive, address)
  case CpAsyncWaitAll(mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.CpAsyncWaitAll)
  case CpAsyncWaitGroup(pendingGroupCount: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.CpAsyncWaitGroup, pendingGroupCount)
  case CpReduceAsyncBulk(
      destAddr: Operand,
      srcAddr: Operand,
      size: Operand,
      cachePolicy: Option[Operand],
      mods: Vec[Modifier],
      pred: Option[Predication]
  ) extends Instruction(Instruction.Definition.CpReduceAsyncBulk, (Vec(destAddr, srcAddr, size) ++ cachePolicy.toVec)*)
  case CpReduceAsyncBulkTensor(
      address: Operand,
      srcAddr: Operand,
      cachePolicy: Option[Operand],
      mods: Vec[Modifier],
      pred: Option[Predication]
  ) extends Instruction(Instruction.Definition.CpReduceAsyncBulkTensor, (Vec(address, srcAddr) ++ cachePolicy.toVec)*)
  case CreatePolicy(
      cachePolicy: Operand,
      accessProperty: Option[Operand],
      primarySize: Option[Operand],
      totalSize: Option[Operand],
      mods: Vec[Modifier],
      pred: Option[Predication]
  ) extends Instruction(
        Instruction.Definition.CreatePolicy,
        (Vec(cachePolicy) ++ accessProperty.toVec ++ primarySize.toVec ++ totalSize.toVec)*
      )
  case Cvt(
      dest: Operand,
      src: Operand,
      secondarySrc: Option[Operand],
      roundingBits: Option[Operand],
      mods: Vec[Modifier],
      pred: Option[Predication]
  ) extends Instruction(Instruction.Definition.Cvt, (Vec(dest, src) ++ secondarySrc.toVec ++ roundingBits.toVec)*)
  case CvtPack(
      dest: Operand,
      src: Operand,
      secondarySrc: Operand,
      roundingBits: Option[Operand],
      mods: Vec[Modifier],
      pred: Option[Predication]
  ) extends Instruction(Instruction.Definition.CvtPack, (Vec(dest, src, secondarySrc) ++ roundingBits.toVec)*)
  case Cvta(dest: Operand, src: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.Cvta, dest, src)
  case Discard(address: Operand, size: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.Discard, address, size)
  case Div(dest: Operand, srcA: Operand, srcB: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.Div, dest, srcA, srcB)
  case Dp2a(dest: Operand, srcA: Operand, srcB: Operand, srcC: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.Dp2a, dest, srcA, srcB, srcC)
  case Dp4a(dest: Operand, srcA: Operand, srcB: Operand, srcC: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.Dp4a, dest, srcA, srcB, srcC)
  case ElectSync(dest: Operand, memberMask: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.ElectSync, dest, memberMask)
  case Ex2(dest: Operand, srcA: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.Ex2, dest, srcA)
  case Exit(mods: Vec[Modifier], pred: Option[Predication]) extends Instruction(Instruction.Definition.Exit)
  case FabricSubmit(mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.FabricSubmit)
  case FabricTryGet(
      destAddr: Operand,
      srcAddr: Operand,
      size: Operand,
      barrierAddr: Operand,
      mods: Vec[Modifier],
      pred: Option[Predication]
  ) extends Instruction(Instruction.Definition.FabricTryGet, destAddr, srcAddr, size, barrierAddr)
  case FabricTryPullred(
      destAddr: Operand,
      srcAddr: Operand,
      size: Operand,
      barrierAddr: Operand,
      memberMask: Operand,
      mods: Vec[Modifier],
      pred: Option[Predication]
  ) extends Instruction(Instruction.Definition.FabricTryPullred, destAddr, srcAddr, size, barrierAddr, memberMask)
  case FabricTryPut(
      destAddr: Operand,
      srcAddr: Operand,
      size: Operand,
      barrierAddr: Operand,
      bytemask: Option[Operand],
      mods: Vec[Modifier],
      pred: Option[Predication]
  ) extends Instruction(
        Instruction.Definition.FabricTryPut,
        (Vec(destAddr, srcAddr, size, barrierAddr) ++ bytemask.toVec)*
      )
  case FabricTryRed(
      destAddr: Operand,
      srcAddr: Operand,
      size: Operand,
      barrierAddr: Operand,
      mods: Vec[Modifier],
      pred: Option[Predication]
  ) extends Instruction(Instruction.Definition.FabricTryRed, destAddr, srcAddr, size, barrierAddr)
  case FabricWait(mods: Vec[Modifier], pred: Option[Predication]) extends Instruction(Instruction.Definition.FabricWait)
  case Fence(address: Option[Operand], size: Option[Operand], mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.Fence, (address.toVec ++ size.toVec)*)
  case Fma(dest: Operand, srcA: Operand, srcB: Operand, srcC: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.Fma, dest, srcA, srcB, srcC)
  case Fns(dest: Operand, mask: Operand, base: Operand, offset: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.Fns, dest, mask, base, offset)
  case GetCtaRank(dest: Operand, srcA: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.GetCtaRank, dest, srcA)
  case GridDependencyControl(mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.GridDependencyControl)
  case IsSpacePredicate(predicate: Operand, srcA: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.IsSpacePredicate, predicate, srcA)
  case IsTypePredicate(predicate: Operand, srcA: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.IsTypePredicate, predicate, srcA)
  case Ld(dest: Operand, address: Operand, cachePolicy: Option[Operand], mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.Ld, (Vec(dest, address) ++ cachePolicy.toVec)*)
  case LdGlobalNc(
      dest: Operand,
      address: Operand,
      cachePolicy: Option[Operand],
      mods: Vec[Modifier],
      pred: Option[Predication]
  ) extends Instruction(Instruction.Definition.LdGlobalNc, (Vec(dest, address) ++ cachePolicy.toVec)*)
  case Ldu(dest: Operand, address: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.Ldu, dest, address)
  case Lg2(dest: Operand, srcA: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.Lg2, dest, srcA)
  case Lop3(
      dest: Operand,
      srcA: Operand,
      srcB: Operand,
      srcC: Operand,
      immLut: Operand,
      predicateQ: Option[Operand],
      mods: Vec[Modifier],
      pred: Option[Predication]
  ) extends Instruction(Instruction.Definition.Lop3, (Vec(dest, srcA, srcB, srcC, immLut) ++ predicateQ.toVec)*)
  case Mad(dest: Operand, srcA: Operand, srcB: Operand, srcC: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.Mad, dest, srcA, srcB, srcC)
  case MadCc(dest: Operand, srcA: Operand, srcB: Operand, srcC: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.MadCc, dest, srcA, srcB, srcC)
  case Mad24(dest: Operand, srcA: Operand, srcB: Operand, srcC: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.Mad24, dest, srcA, srcB, srcC)
  case Madc(dest: Operand, srcA: Operand, srcB: Operand, srcC: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.Madc, dest, srcA, srcB, srcC)
  case Mapa(dest: Operand, srcA: Operand, srcB: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.Mapa, dest, srcA, srcB)
  case MatchAllSync(dest: Operand, srcA: Operand, memberMask: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.MatchAllSync, dest, srcA, memberMask)
  case MatchAnySync(dest: Operand, srcA: Operand, memberMask: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.MatchAnySync, dest, srcA, memberMask)
  case Max(
      dest: Operand,
      srcA: Operand,
      srcB: Operand,
      srcC: Option[Operand],
      mods: Vec[Modifier],
      pred: Option[Predication]
  ) extends Instruction(Instruction.Definition.Max, (Vec(dest, srcA, srcB) ++ srcC.toVec)*)
  case MbarrierArrive(
      state: Operand,
      barrierAddr: Operand,
      countOrTransactionCount: Option[Operand],
      mods: Vec[Modifier],
      pred: Option[Predication]
  ) extends Instruction(
        Instruction.Definition.MbarrierArrive,
        (Vec(state, barrierAddr) ++ countOrTransactionCount.toVec)*
      )
  case MbarrierArriveDrop(
      state: Operand,
      barrierAddr: Operand,
      countOrTransactionCount: Option[Operand],
      mods: Vec[Modifier],
      pred: Option[Predication]
  ) extends Instruction(
        Instruction.Definition.MbarrierArriveDrop,
        (Vec(state, barrierAddr) ++ countOrTransactionCount.toVec)*
      )
  case MbarrierCheckLayout(predicate: Operand, barrierAddr: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.MbarrierCheckLayout, predicate, barrierAddr)
  case MbarrierCompleteTx(
      barrierAddr: Operand,
      transactionCount: Operand,
      mods: Vec[Modifier],
      pred: Option[Predication]
  ) extends Instruction(Instruction.Definition.MbarrierCompleteTx, barrierAddr, transactionCount)
  case MbarrierExpectTx(barrierAddr: Operand, transactionCount: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.MbarrierExpectTx, barrierAddr, transactionCount)
  case MbarrierInit(barrierAddr: Operand, count: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.MbarrierInit, barrierAddr, count)
  case MbarrierInval(barrierAddr: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.MbarrierInval, barrierAddr)
  case MbarrierPendingCount(dest: Operand, state: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.MbarrierPendingCount, dest, state)
  case MbarrierTestWait(
      completion: Operand,
      reportValue: Option[Operand],
      barrierAddr: Operand,
      stateOrPhase: Operand,
      mods: Vec[Modifier],
      pred: Option[Predication]
  ) extends Instruction(
        Instruction.Definition.MbarrierTestWait,
        (Vec(completion) ++ reportValue.toVec ++ Vec(barrierAddr, stateOrPhase))*
      )
  case MbarrierTryWait(
      completion: Operand,
      reportValue: Option[Operand],
      barrierAddr: Operand,
      stateOrPhase: Operand,
      timeHint: Option[Operand],
      mods: Vec[Modifier],
      pred: Option[Predication]
  ) extends Instruction(
        Instruction.Definition.MbarrierTryWait,
        (Vec(completion) ++ reportValue.toVec ++ Vec(barrierAddr, stateOrPhase) ++ timeHint.toVec)*
      )
  case Membar(mods: Vec[Modifier], pred: Option[Predication]) extends Instruction(Instruction.Definition.Membar)
  case Min(
      dest: Operand,
      srcA: Operand,
      srcB: Operand,
      srcC: Option[Operand],
      mods: Vec[Modifier],
      pred: Option[Predication]
  ) extends Instruction(Instruction.Definition.Min, (Vec(dest, srcA, srcB) ++ srcC.toVec)*)
  case Mov(dest: Operand, src: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.Mov, dest, src)
  case Mul(dest: Operand, srcA: Operand, srcB: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.Mul, dest, srcA, srcB)
  case Mul24(dest: Operand, srcA: Operand, srcB: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.Mul24, dest, srcA, srcB)
  case MultimemCpAsyncBulk(
      destAddr: Operand,
      srcAddr: Operand,
      size: Operand,
      byteMask: Option[Operand],
      mods: Vec[Modifier],
      pred: Option[Predication]
  ) extends Instruction(Instruction.Definition.MultimemCpAsyncBulk, (Vec(destAddr, srcAddr, size) ++ byteMask.toVec)*)
  case MultimemCpReduceAsyncBulk(
      destAddr: Operand,
      srcAddr: Operand,
      size: Operand,
      mods: Vec[Modifier],
      pred: Option[Predication]
  ) extends Instruction(Instruction.Definition.MultimemCpReduceAsyncBulk, destAddr, srcAddr, size)
  case MultimemLdReduce(dest: Operand, address: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.MultimemLdReduce, dest, address)
  case MultimemRed(address: Operand, value: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.MultimemRed, address, value)
  case MultimemRedAsync(address: Operand, value: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.MultimemRedAsync, address, value)
  case MultimemSt(address: Operand, value: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.MultimemSt, address, value)
  case MultimemStAsync(address: Operand, value: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.MultimemStAsync, address, value)
  case NanoSleep(nanoseconds: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.NanoSleep, nanoseconds)
  case Neg(dest: Operand, srcA: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.Neg, dest, srcA)
  case Not(dest: Operand, srcA: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.Not, dest, srcA)
  case Or(dest: Operand, srcA: Operand, srcB: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.Or, dest, srcA, srcB)
  case PmEvent(event: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.PmEvent, event)
  case Popc(dest: Operand, srcA: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.Popc, dest, srcA)
  case Prefetch(address: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.Prefetch, address)
  case Prefetchu(address: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.Prefetchu, address)
  case Prmt(dest: Operand, srcA: Operand, srcB: Operand, srcC: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.Prmt, dest, srcA, srcB, srcC)
  case Rcp(dest: Operand, srcA: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.Rcp, dest, srcA)
  case RcpApproxFtzF64(dest: Operand, srcA: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.RcpApproxFtzF64, dest, srcA)
  case Red(
      address: Operand,
      value: Operand,
      cachePolicy: Option[Operand],
      mods: Vec[Modifier],
      pred: Option[Predication]
  ) extends Instruction(Instruction.Definition.Red, (Vec(address, value) ++ cachePolicy.toVec)*)
  case RedAsync(
      address: Operand,
      value: Operand,
      cachePolicy: Option[Operand],
      mods: Vec[Modifier],
      pred: Option[Predication]
  ) extends Instruction(Instruction.Definition.RedAsync, (Vec(address, value) ++ cachePolicy.toVec)*)
  case ReduxSync(dest: Operand, src: Operand, memberMask: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.ReduxSync, dest, src, memberMask)
  case Rem(dest: Operand, srcA: Operand, srcB: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.Rem, dest, srcA, srcB)
  case Ret(mods: Vec[Modifier], pred: Option[Predication]) extends Instruction(Instruction.Definition.Ret)
  case Rsqrt(dest: Operand, srcA: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.Rsqrt, dest, srcA)
  case RsqrtApproxFtzF64(dest: Operand, srcA: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.RsqrtApproxFtzF64, dest, srcA)
  case Sad(dest: Operand, srcA: Operand, srcB: Operand, srcC: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.Sad, dest, srcA, srcB, srcC)
  case Selp(dest: Operand, srcA: Operand, srcB: Operand, srcC: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.Selp, dest, srcA, srcB, srcC)
  case Set(
      dest: Operand,
      left: Operand,
      right: Operand,
      booleanSrc: Option[Operand],
      mods: Vec[Modifier],
      pred: Option[Predication]
  ) extends Instruction(Instruction.Definition.Set, (Vec(dest, left, right) ++ booleanSrc.toVec)*)
  case SetMaxRegisters(immRegCount: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.SetMaxRegisters, immRegCount)
  case Setp(
      dest: Operand,
      left: Operand,
      right: Operand,
      booleanSrc: Option[Operand],
      mods: Vec[Modifier],
      pred: Option[Predication]
  ) extends Instruction(Instruction.Definition.Setp, (Vec(dest, left, right) ++ booleanSrc.toVec)*)
  case Shf(dest: Operand, srcA: Operand, srcB: Operand, srcC: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.Shf, dest, srcA, srcB, srcC)
  case Shfl(dest: Operand, srcA: Operand, srcB: Operand, srcC: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.Shfl, dest, srcA, srcB, srcC)
  case ShflSync(
      dest: Operand,
      srcA: Operand,
      srcB: Operand,
      srcC: Operand,
      memberMask: Operand,
      mods: Vec[Modifier],
      pred: Option[Predication]
  ) extends Instruction(Instruction.Definition.ShflSync, dest, srcA, srcB, srcC, memberMask)
  case Shl(dest: Operand, srcA: Operand, srcB: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.Shl, dest, srcA, srcB)
  case Shr(dest: Operand, srcA: Operand, srcB: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.Shr, dest, srcA, srcB)
  case Sin(dest: Operand, srcA: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.Sin, dest, srcA)
  case Slct(dest: Operand, srcA: Operand, srcB: Operand, srcC: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.Slct, dest, srcA, srcB, srcC)
  case Sqrt(dest: Operand, srcA: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.Sqrt, dest, srcA)
  case St(
      address: Operand,
      value: Operand,
      cachePolicy: Option[Operand],
      mods: Vec[Modifier],
      pred: Option[Predication]
  ) extends Instruction(Instruction.Definition.St, (Vec(address, value) ++ cachePolicy.toVec)*)
  case StAsync(
      address: Operand,
      value: Operand,
      cachePolicy: Option[Operand],
      mods: Vec[Modifier],
      pred: Option[Predication]
  ) extends Instruction(Instruction.Definition.StAsync, (Vec(address, value) ++ cachePolicy.toVec)*)
  case StBulk(address: Operand, value: Operand, cachePolicy: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.StBulk, address, value, cachePolicy)
  case StackRestore(srcA: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.StackRestore, srcA)
  case StackSave(dest: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.StackSave, dest)
  case Sub(dest: Operand, srcA: Operand, srcB: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.Sub, dest, srcA, srcB)
  case SubCc(dest: Operand, srcA: Operand, srcB: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.SubCc, dest, srcA, srcB)
  case Subc(dest: Operand, srcA: Operand, srcB: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.Subc, dest, srcA, srcB)
  case Suld(dest: Operand, address: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.Suld, dest, address)
  case Suq(dest: Operand, address: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.Suq, dest, address)
  case Sured(address: Operand, srcC: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.Sured, address, srcC)
  case Sust(address: Operand, srcC: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.Sust, address, srcC)
  case Szext(dest: Operand, srcA: Operand, srcB: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.Szext, dest, srcA, srcB)
  case Tanh(dest: Operand, srcA: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.Tanh, dest, srcA)
  case Tcgen05Alloc(destAddr: Operand, columnCount: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.Tcgen05Alloc, destAddr, columnCount)
  case Tcgen05Commit(barrierAddr: Operand, ctaMask: Option[Operand], mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.Tcgen05Commit, (Vec(barrierAddr) ++ ctaMask.toVec)*)
  case Tcgen05Cp(destAddr: Operand, srcDesc: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.Tcgen05Cp, destAddr, srcDesc)
  case Tcgen05Dealloc(tensorMemoryAddr: Operand, columnCount: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.Tcgen05Dealloc, tensorMemoryAddr, columnCount)
  case Tcgen05FenceAfterThreadSync(mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.Tcgen05FenceAfterThreadSync)
  case Tcgen05FenceBeforeThreadSync(mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.Tcgen05FenceBeforeThreadSync)
  case Tcgen05Ld(
      destRegs: Operand,
      reductionValue: Operand,
      address: Option[Operand],
      halfSplitOffset: Option[Operand],
      mods: Vec[Modifier],
      pred: Option[Predication]
  ) extends Instruction(
        Instruction.Definition.Tcgen05Ld,
        (Vec(destRegs, reductionValue) ++ address.toVec ++ halfSplitOffset.toVec)*
      )
  case Tcgen05Mma(
      destAddr: Operand,
      srcA: Operand,
      srcBDesc: Operand,
      instDesc: Operand,
      scaleAAddr: Option[Operand],
      scaleBAddr: Option[Operand],
      disabledOutputLanes: Option[Operand],
      enableInputD: Operand,
      scaleInputD: Option[Operand],
      mods: Vec[Modifier],
      pred: Option[Predication]
  ) extends Instruction(
        Instruction.Definition.Tcgen05Mma,
        (Vec(destAddr, srcA, srcBDesc, instDesc) ++ scaleAAddr.toVec ++ scaleBAddr.toVec ++ disabledOutputLanes.toVec ++
          Vec(enableInputD) ++ scaleInputD.toVec)*
      )
  case Tcgen05MmaSp(
      destAddr: Operand,
      srcA: Operand,
      srcBDesc: Operand,
      sparseMetadataAddr: Operand,
      instDesc: Operand,
      scaleAAddr: Option[Operand],
      scaleBAddr: Option[Operand],
      disabledOutputLanes: Option[Operand],
      enableInputD: Operand,
      scaleInputD: Option[Operand],
      mods: Vec[Modifier],
      pred: Option[Predication]
  ) extends Instruction(
        Instruction.Definition.Tcgen05MmaSp,
        (Vec(destAddr, srcA, srcBDesc, sparseMetadataAddr, instDesc) ++ scaleAAddr.toVec ++ scaleBAddr.toVec ++
          disabledOutputLanes.toVec ++ Vec(enableInputD) ++ scaleInputD.toVec)*
      )
  case Tcgen05MmaWs(
      destAddr: Operand,
      srcA: Operand,
      srcBDesc: Operand,
      instDesc: Operand,
      enableInputD: Operand,
      zeroColumnMaskDesc: Option[Operand],
      mods: Vec[Modifier],
      pred: Option[Predication]
  ) extends Instruction(
        Instruction.Definition.Tcgen05MmaWs,
        (Vec(destAddr, srcA, srcBDesc, instDesc, enableInputD) ++ zeroColumnMaskDesc.toVec)*
      )
  case Tcgen05MmaWsSp(
      destAddr: Operand,
      srcA: Operand,
      srcBDesc: Operand,
      sparseMetadataAddr: Operand,
      instDesc: Operand,
      enableInputD: Operand,
      zeroColumnMaskDesc: Option[Operand],
      mods: Vec[Modifier],
      pred: Option[Predication]
  ) extends Instruction(
        Instruction.Definition.Tcgen05MmaWsSp,
        (Vec(destAddr, srcA, srcBDesc, sparseMetadataAddr, instDesc, enableInputD) ++ zeroColumnMaskDesc.toVec)*
      )
  case Tcgen05RelinquishAllocPermit(mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.Tcgen05RelinquishAllocPermit)
  case Tcgen05Shift(tensorMemoryAddr: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.Tcgen05Shift, tensorMemoryAddr)
  case Tcgen05St(
      address: Operand,
      halfSplitOffset: Operand,
      srcRegs: Option[Operand],
      mods: Vec[Modifier],
      pred: Option[Predication]
  ) extends Instruction(Instruction.Definition.Tcgen05St, (Vec(address, halfSplitOffset) ++ srcRegs.toVec)*)
  case Tcgen05WaitLd(mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.Tcgen05WaitLd)
  case Tcgen05WaitSt(mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.Tcgen05WaitSt)
  case TensorMapCopyFenceProxy(
      dest: Operand,
      srcOrOrdinal: Operand,
      valueOrSize: Operand,
      mods: Vec[Modifier],
      pred: Option[Predication]
  ) extends Instruction(Instruction.Definition.TensorMapCopyFenceProxy, dest, srcOrOrdinal, valueOrSize)
  case TensorMapReplace(
      dest: Operand,
      srcOrOrdinal: Operand,
      valueOrSize: Option[Operand],
      mods: Vec[Modifier],
      pred: Option[Predication]
  ) extends Instruction(Instruction.Definition.TensorMapReplace, (Vec(dest, srcOrOrdinal) ++ valueOrSize.toVec)*)
  case TestPredicate(predicate: Operand, srcA: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.TestPredicate, predicate, srcA)
  case Tex(
      dest: Operand,
      textureAndCoordinates: Operand,
      lodOffsetOrDerivativeX: Option[Operand],
      depthCompareOrDerivativeY: Option[Operand],
      offset: Option[Operand],
      depthCompare: Option[Operand],
      mods: Vec[Modifier],
      pred: Option[Predication]
  ) extends Instruction(
        Instruction.Definition.Tex,
        (Vec(dest, textureAndCoordinates) ++ lodOffsetOrDerivativeX.toVec ++ depthCompareOrDerivativeY.toVec ++
          offset.toVec ++ depthCompare.toVec)*
      )
  case Tld4(
      dest: Operand,
      textureAndCoordinates: Operand,
      offset: Option[Operand],
      depthCompare: Option[Operand],
      mods: Vec[Modifier],
      pred: Option[Predication]
  ) extends Instruction(
        Instruction.Definition.Tld4,
        (Vec(dest, textureAndCoordinates) ++ offset.toVec ++ depthCompare.toVec)*
      )
  case Trap(mods: Vec[Modifier], pred: Option[Predication]) extends Instruction(Instruction.Definition.Trap)
  case Txq(dest: Operand, texture: Operand, lod: Option[Operand], mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.Txq, (Vec(dest, texture) ++ lod.toVec)*)
  case Vabsdiff(
      dest: Operand,
      srcA: Operand,
      srcB: Operand,
      srcC: Option[Operand],
      mods: Vec[Modifier],
      pred: Option[Predication]
  ) extends Instruction(Instruction.Definition.Vabsdiff, (Vec(dest, srcA, srcB) ++ srcC.toVec)*)
  case Vabsdiff2(
      dest: Operand,
      srcA: Operand,
      srcB: Operand,
      srcC: Operand,
      mods: Vec[Modifier],
      pred: Option[Predication]
  ) extends Instruction(Instruction.Definition.Vabsdiff2, dest, srcA, srcB, srcC)
  case Vabsdiff4(
      dest: Operand,
      srcA: Operand,
      srcB: Operand,
      srcC: Operand,
      mods: Vec[Modifier],
      pred: Option[Predication]
  ) extends Instruction(Instruction.Definition.Vabsdiff4, dest, srcA, srcB, srcC)
  case Vadd(
      dest: Operand,
      srcA: Operand,
      srcB: Operand,
      srcC: Option[Operand],
      mods: Vec[Modifier],
      pred: Option[Predication]
  ) extends Instruction(Instruction.Definition.Vadd, (Vec(dest, srcA, srcB) ++ srcC.toVec)*)
  case Vadd2(dest: Operand, srcA: Operand, srcB: Operand, srcC: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.Vadd2, dest, srcA, srcB, srcC)
  case Vadd4(dest: Operand, srcA: Operand, srcB: Operand, srcC: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.Vadd4, dest, srcA, srcB, srcC)
  case Vavrg2(
      dest: Operand,
      srcA: Operand,
      srcB: Operand,
      srcC: Operand,
      mods: Vec[Modifier],
      pred: Option[Predication]
  ) extends Instruction(Instruction.Definition.Vavrg2, dest, srcA, srcB, srcC)
  case Vavrg4(
      dest: Operand,
      srcA: Operand,
      srcB: Operand,
      srcC: Operand,
      mods: Vec[Modifier],
      pred: Option[Predication]
  ) extends Instruction(Instruction.Definition.Vavrg4, dest, srcA, srcB, srcC)
  case Vmad(dest: Operand, srcA: Operand, srcB: Operand, srcC: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.Vmad, dest, srcA, srcB, srcC)
  case Vmax(
      dest: Operand,
      srcA: Operand,
      srcB: Operand,
      srcC: Option[Operand],
      mods: Vec[Modifier],
      pred: Option[Predication]
  ) extends Instruction(Instruction.Definition.Vmax, (Vec(dest, srcA, srcB) ++ srcC.toVec)*)
  case Vmax2(dest: Operand, srcA: Operand, srcB: Operand, srcC: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.Vmax2, dest, srcA, srcB, srcC)
  case Vmax4(dest: Operand, srcA: Operand, srcB: Operand, srcC: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.Vmax4, dest, srcA, srcB, srcC)
  case Vmin(
      dest: Operand,
      srcA: Operand,
      srcB: Operand,
      srcC: Option[Operand],
      mods: Vec[Modifier],
      pred: Option[Predication]
  ) extends Instruction(Instruction.Definition.Vmin, (Vec(dest, srcA, srcB) ++ srcC.toVec)*)
  case Vmin2(dest: Operand, srcA: Operand, srcB: Operand, srcC: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.Vmin2, dest, srcA, srcB, srcC)
  case Vmin4(dest: Operand, srcA: Operand, srcB: Operand, srcC: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.Vmin4, dest, srcA, srcB, srcC)
  case Vote(dest: Operand, srcA: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.Vote, dest, srcA)
  case VoteSync(dest: Operand, srcA: Operand, memberMask: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.VoteSync, dest, srcA, memberMask)
  case Vset(
      dest: Operand,
      srcA: Operand,
      srcB: Operand,
      srcC: Option[Operand],
      mods: Vec[Modifier],
      pred: Option[Predication]
  ) extends Instruction(Instruction.Definition.Vset, (Vec(dest, srcA, srcB) ++ srcC.toVec)*)
  case Vset2(dest: Operand, srcA: Operand, srcB: Operand, srcC: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.Vset2, dest, srcA, srcB, srcC)
  case Vset4(dest: Operand, srcA: Operand, srcB: Operand, srcC: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.Vset4, dest, srcA, srcB, srcC)
  case Vshl(
      dest: Operand,
      srcA: Operand,
      srcB: Operand,
      srcC: Option[Operand],
      mods: Vec[Modifier],
      pred: Option[Predication]
  ) extends Instruction(Instruction.Definition.Vshl, (Vec(dest, srcA, srcB) ++ srcC.toVec)*)
  case Vshr(
      dest: Operand,
      srcA: Operand,
      srcB: Operand,
      srcC: Option[Operand],
      mods: Vec[Modifier],
      pred: Option[Predication]
  ) extends Instruction(Instruction.Definition.Vshr, (Vec(dest, srcA, srcB) ++ srcC.toVec)*)
  case Vsub(
      dest: Operand,
      srcA: Operand,
      srcB: Operand,
      srcC: Option[Operand],
      mods: Vec[Modifier],
      pred: Option[Predication]
  ) extends Instruction(Instruction.Definition.Vsub, (Vec(dest, srcA, srcB) ++ srcC.toVec)*)
  case Vsub2(dest: Operand, srcA: Operand, srcB: Operand, srcC: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.Vsub2, dest, srcA, srcB, srcC)
  case Vsub4(dest: Operand, srcA: Operand, srcB: Operand, srcC: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.Vsub4, dest, srcA, srcB, srcC)
  case Xor(dest: Operand, srcA: Operand, srcB: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.Xor, dest, srcA, srcB)

  final val operands: Vec[Operand] = operandValues.toVec

  final def opcode: Opcode = definition.opcode

  final def decoder: Instruction.Decoder = definition.decoder

  final def meta: Metadata = definition.meta

  def mods: Vec[Modifier]

  def pred: Option[Predication]

  final def qualifiedOpcode: String = (opcode +: mods.map(_.name)).mkString(".")

}

@javax.annotation.processing.Generated(Array("sbt codegen"))
object Instruction {
  enum DecodeError {
    case UnknownOpcode(opcode: String)
    case InvalidOperandCount(opcode: String, expected: Vec[Int], actual: Int)
    case AmbiguousInstruction(opcode: String, definitions: Vec[String])
  }

  type Decoder = (
      operands: Vec[Operand],
      mods: Vec[Modifier],
      pred: Option[Predication]
  ) => Either[DecodeError, Instruction]

  enum Definition(val opcode: Opcode, val decoder: Decoder, metadata: () => Metadata) {
    case Abs extends Definition(
          Opcode("abs"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 2) Right(Instruction.Abs(operands(0), operands(1), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("abs", Vec(2), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("integer-arithmetic-instructions-abs"),
                NonEmptyString("9.7.1.11. Integer Arithmetic Instructions: abs"),
                NonEmptyString("abs"),
                NonEmptyString("Integer Arithmetic Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(NonEmptyString("abs.type  d, a;\n\n.type = { .s16, .s32, .s64 };"))),
                Version(PositiveInt(1), NonNegativeInt.Zero),
                Target(10),
                Vec(InstructionForm(
                  Opcode("abs"),
                  Vec(OperandConstraint.Register, OperandConstraint.Value),
                  Vec(ModifierGroup(
                    ModifierName("s16"),
                    ModifierName("s32"),
                    ModifierName("s64")
                  )),
                  _root_.scala.collection.immutable.Set(),
                  true
                ))
              ),
              Metadata.Entry(
                NonEmptyString("floating-point-instructions-abs"),
                NonEmptyString("9.7.3.9. Floating Point Instructions: abs"),
                NonEmptyString("abs"),
                NonEmptyString("Floating Point Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(NonEmptyString("abs{.ftz}.f32  d, a;\nabs.f64        d, a;"))),
                Version(PositiveInt(1), NonNegativeInt.Zero),
                Target(13),
                Vec(
                  InstructionForm(
                    Opcode("abs"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(ModifierGroup(ModifierName("f32"))),
                    _root_.scala.collection.immutable.Set(ModifierName("ftz")),
                    true
                  ),
                  InstructionForm(
                    Opcode("abs"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(ModifierGroup(ModifierName("f64"))),
                    _root_.scala.collection.immutable.Set(),
                    true
                  )
                )
              ),
              Metadata.Entry(
                NonEmptyString("half-precision-floating-point-instructions-abs"),
                NonEmptyString("9.7.4.6. Half Precision Floating Point Instructions: abs"),
                NonEmptyString("abs"),
                NonEmptyString("Half Precision Floating Point Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(NonEmptyString(
                  "abs{.ftz}.f16    d, a;\nabs{.ftz}.f16x2  d, a;\nabs.bf16         d, a;\nabs.bf16x2       d, a;"
                ))),
                Version(PositiveInt(6), NonNegativeInt(5)),
                Target(53),
                Vec(
                  InstructionForm(
                    Opcode("abs"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(ModifierGroup(ModifierName("f16"))),
                    _root_.scala.collection.immutable.Set(ModifierName("ftz")),
                    false
                  ),
                  InstructionForm(
                    Opcode("abs"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(ModifierGroup(ModifierName("f16x2"))),
                    _root_.scala.collection.immutable.Set(ModifierName("ftz")),
                    false
                  ),
                  InstructionForm(
                    Opcode("abs"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(ModifierGroup(ModifierName("bf16"))),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Opcode("abs"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(ModifierGroup(ModifierName("bf16x2"))),
                    _root_.scala.collection.immutable.Set(),
                    false
                  )
                )
              )
            )))
        )
    case Activemask extends Definition(
          Opcode("activemask"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 1) Right(Instruction.Activemask(operands(0), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("activemask", Vec(1), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("parallel-synchronization-and-communication-instructions-activemask"),
                NonEmptyString("9.7.14.12. Parallel Synchronization and Communication Instructions: activemask"),
                NonEmptyString("activemask"),
                NonEmptyString("Parallel Synchronization and Communication Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(NonEmptyString("activemask.b32 d;"))),
                Version(PositiveInt(6), NonNegativeInt(2)),
                Target(30),
                Vec(InstructionForm(
                  Opcode("activemask"),
                  Vec(OperandConstraint.Register),
                  Vec(ModifierGroup(ModifierName("b32"))),
                  _root_.scala.collection.immutable.Set(),
                  true
                ))
              )
            )))
        )
    case Add extends Definition(
          Opcode("add"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 3) Right(Instruction.Add(operands(0), operands(1), operands(2), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("add", Vec(3), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("integer-arithmetic-instructions-add"),
                NonEmptyString("9.7.1.1. Integer Arithmetic Instructions: add"),
                NonEmptyString("add"),
                NonEmptyString("Integer Arithmetic Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(NonEmptyString(
                  "add.type1        d, a, b;\nadd{.sat}.type2  d, a, b;\n\n.type1 = { .u16, .u64,\n           .s16, .s64 };\n\n.type2 = { .u32, .u16x2, .u8x4,\n           .s32, .s16x2, .s8x4 };"
                ))),
                Version(PositiveInt(1), NonNegativeInt.Zero),
                Target(10),
                Vec(
                  InstructionForm(
                    Opcode("add"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(ModifierGroup(
                      ModifierName("s16"),
                      ModifierName("s64"),
                      ModifierName("u16"),
                      ModifierName("u64")
                    )),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Opcode("add"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(ModifierGroup(
                      ModifierName("s16x2"),
                      ModifierName("s32"),
                      ModifierName("s8x4"),
                      ModifierName("u16x2"),
                      ModifierName("u32"),
                      ModifierName("u8x4")
                    )),
                    _root_.scala.collection.immutable.Set(ModifierName("sat")),
                    false
                  )
                )
              ),
              Metadata.Entry(
                NonEmptyString("floating-point-instructions-add"),
                NonEmptyString("9.7.3.3. Floating Point Instructions: add"),
                NonEmptyString("add"),
                NonEmptyString("Floating Point Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(NonEmptyString(
                  "add{.rnd}{.ftz}{.sat}.f32  d, a, b;\nadd{.rnd}{.ftz}.f32x2      d, a, b;\nadd{.rnd}.f64              d, a, b;\n\n.rnd = { .rn, .rz, .rm, .rp };"
                ))),
                Version(PositiveInt(1), NonNegativeInt.Zero),
                Target(13),
                Vec(
                  InstructionForm(
                    Opcode("add"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(ModifierGroup(ModifierName("f32"))),
                    _root_.scala.collection.immutable.Set(
                      ModifierName("ftz"),
                      ModifierName("rm"),
                      ModifierName("rn"),
                      ModifierName("rp"),
                      ModifierName("rz"),
                      ModifierName("sat")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode("add"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(ModifierGroup(ModifierName("f32x2"))),
                    _root_.scala.collection.immutable.Set(
                      ModifierName("ftz"),
                      ModifierName("rm"),
                      ModifierName("rn"),
                      ModifierName("rp"),
                      ModifierName("rz")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode("add"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(ModifierGroup(ModifierName("f64"))),
                    _root_.scala.collection.immutable.Set(
                      ModifierName("rm"),
                      ModifierName("rn"),
                      ModifierName("rp"),
                      ModifierName("rz")
                    ),
                    false
                  )
                )
              ),
              Metadata.Entry(
                NonEmptyString("half-precision-floating-point-instructions-add"),
                NonEmptyString("9.7.4.1. Half Precision Floating Point Instructions: add"),
                NonEmptyString("add"),
                NonEmptyString("Half Precision Floating Point Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(NonEmptyString(
                  "add{.rnd}{.ftz}{.sat}.f16   d, a, b;\nadd{.rnd}{.ftz}{.sat}.f16x2 d, a, b;\n\nadd{.rnd}.bf16   d, a, b;\nadd{.rnd}.bf16x2 d, a, b;\n\n.rnd = { .rn };"
                ))),
                Version(PositiveInt(4), NonNegativeInt(2)),
                Target(53),
                Vec(
                  InstructionForm(
                    Opcode("add"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(ModifierGroup(ModifierName("f16"))),
                    _root_.scala.collection.immutable.Set(
                      ModifierName("ftz"),
                      ModifierName("rn"),
                      ModifierName("sat")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode("add"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(ModifierGroup(ModifierName("f16x2"))),
                    _root_.scala.collection.immutable.Set(
                      ModifierName("ftz"),
                      ModifierName("rn"),
                      ModifierName("sat")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode("add"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(ModifierGroup(ModifierName("bf16"))),
                    _root_.scala.collection.immutable.Set(ModifierName("rn")),
                    false
                  ),
                  InstructionForm(
                    Opcode("add"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(ModifierGroup(ModifierName("bf16x2"))),
                    _root_.scala.collection.immutable.Set(ModifierName("rn")),
                    false
                  )
                )
              ),
              Metadata.Entry(
                NonEmptyString("mixed-precision-floating-point-instructions-add"),
                NonEmptyString("9.7.5.1. Mixed Precision Floating Point Instructions: add"),
                NonEmptyString("add"),
                NonEmptyString("Mixed Precision Floating Point Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(NonEmptyString(
                  "add{.rnd}{.sat}.f32.atype  d, a, c;\n\n.atype = { .f16, .bf16};\n.rnd   = { .rn, .rz, .rm, .rp };"
                ))),
                Version(PositiveInt(1), NonNegativeInt.Zero),
                Target(100),
                Vec(InstructionForm(
                  Opcode("add"),
                  Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                  Vec(
                    ModifierGroup(ModifierName("f32")),
                    ModifierGroup(
                      ModifierName("bf16"),
                      ModifierName("f16")
                    )
                  ),
                  _root_.scala.collection.immutable.Set(
                    ModifierName("rm"),
                    ModifierName("rn"),
                    ModifierName("rp"),
                    ModifierName("rz"),
                    ModifierName("sat")
                  ),
                  true
                ))
              )
            )))
        )
    case AddCc extends Definition(
          Opcode("add.cc"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 3) Right(Instruction.AddCc(operands(0), operands(1), operands(2), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("add.cc", Vec(3), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("extended-precision-arithmetic-instructions-add-cc"),
                NonEmptyString("9.7.2.1. Extended-Precision Arithmetic Instructions: add.cc"),
                NonEmptyString("add.cc"),
                NonEmptyString("Extended-Precision Arithmetic Instructions"),
                NonEmptyVec.unsafeFromVec(
                  Vec(NonEmptyString("add.cc.type  d, a, b;\n\n.type = { .u32, .s32, .u64, .s64 };"))
                ),
                Version(PositiveInt(1), NonNegativeInt.Zero),
                Target(20),
                Vec(InstructionForm(
                  Opcode("add.cc"),
                  Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                  Vec(ModifierGroup(
                    ModifierName("s32"),
                    ModifierName("s64"),
                    ModifierName("u32"),
                    ModifierName("u64")
                  )),
                  _root_.scala.collection.immutable.Set(),
                  true
                ))
              )
            )))
        )
    case Addc extends Definition(
          Opcode("addc"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 3) Right(Instruction.Addc(operands(0), operands(1), operands(2), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("addc", Vec(3), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("extended-precision-arithmetic-instructions-addc"),
                NonEmptyString("9.7.2.2. Extended-Precision Arithmetic Instructions: addc"),
                NonEmptyString("addc"),
                NonEmptyString("Extended-Precision Arithmetic Instructions"),
                NonEmptyVec.unsafeFromVec(
                  Vec(NonEmptyString("addc{.cc}.type  d, a, b;\n\n.type = { .u32, .s32, .u64, .s64 };"))
                ),
                Version(PositiveInt(1), NonNegativeInt.Zero),
                Target(20),
                Vec(InstructionForm(
                  Opcode("addc"),
                  Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                  Vec(ModifierGroup(
                    ModifierName("s32"),
                    ModifierName("s64"),
                    ModifierName("u32"),
                    ModifierName("u64")
                  )),
                  _root_.scala.collection.immutable.Set(ModifierName("cc")),
                  true
                ))
              )
            )))
        )
    case Alloca extends Definition(
          Opcode("alloca"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 2 || operands.size == 3)
              Right(Instruction.Alloca(operands(0), operands(1), operands.lift(2), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("alloca", Vec(2, 3), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("stack-manipulation-instructions-alloca"),
                NonEmptyString("9.7.18.3. Stack Manipulation Instructions: alloca"),
                NonEmptyString("alloca"),
                NonEmptyString("Stack Manipulation Instructions"),
                NonEmptyVec.unsafeFromVec(
                  Vec(NonEmptyString("alloca.type  ptr, size{, immAlign};\n\n.type = { .u32, .u64 };"))
                ),
                Version(PositiveInt(7), NonNegativeInt(3)),
                Target(52),
                Vec(InstructionForm(
                  Opcode("alloca"),
                  Vec(OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value),
                  Vec(ModifierGroup(
                    ModifierName("u32"),
                    ModifierName("u64")
                  )),
                  _root_.scala.collection.immutable.Set(),
                  true
                ))
              )
            )))
        )
    case And extends Definition(
          Opcode("and"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 3) Right(Instruction.And(operands(0), operands(1), operands(2), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("and", Vec(3), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("logic-and-shift-instructions-and"),
                NonEmptyString("9.7.8.1. Logic and Shift Instructions: and"),
                NonEmptyString("and"),
                NonEmptyString("Logic and Shift Instructions"),
                NonEmptyVec.unsafeFromVec(
                  Vec(NonEmptyString("and.type d, a, b;\n\n.type = { .pred, .b16, .b32, .b64 };"))
                ),
                Version(PositiveInt(1), NonNegativeInt.Zero),
                Target(10),
                Vec(InstructionForm(
                  Opcode("and"),
                  Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                  Vec(ModifierGroup(
                    ModifierName("b16"),
                    ModifierName("b32"),
                    ModifierName("b64"),
                    ModifierName("pred")
                  )),
                  _root_.scala.collection.immutable.Set(),
                  true
                ))
              )
            )))
        )
    case ApplyPriority extends Definition(
          Opcode("applypriority"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 2) Right(Instruction.ApplyPriority(operands(0), operands(1), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("applypriority", Vec(2), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("data-movement-and-conversion-instructions-applypriority"),
                NonEmptyString("9.7.9.17. Data Movement and Conversion Instructions: applypriority"),
                NonEmptyString("applypriority"),
                NonEmptyString("Data Movement and Conversion Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(NonEmptyString(
                  "applypriority{.global}.level::eviction_priority  [a], size;\n\n.level::eviction_priority = { .L2::evict_normal };"
                ))),
                Version(PositiveInt(7), NonNegativeInt(4)),
                Target(80),
                Vec(InstructionForm(
                  Opcode("applypriority"),
                  Vec(OperandConstraint.Address, OperandConstraint.Value),
                  Vec(
                    ModifierGroup(ModifierName("L2::evict_normal"))
                  ),
                  _root_.scala.collection.immutable.Set(ModifierName("global")),
                  true
                ))
              )
            )))
        )
    case Atom extends Definition(
          Opcode("atom"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 3 || operands.size == 4)
              Right(Instruction.Atom(operands(0), operands(1), operands(2), operands.lift(3), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("atom", Vec(3, 4), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("parallel-synchronization-and-communication-instructions-atom"),
                NonEmptyString("9.7.14.5. Parallel Synchronization and Communication Instructions: atom"),
                NonEmptyString("atom"),
                NonEmptyString("Parallel Synchronization and Communication Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(
                  NonEmptyString(
                    "atom{.sem}{.scope}{.space}.op{.level::cache_hint}.type d, [a], b{, cache_policy};\n\natom{.sem}{.scope}{.space}.cas.type1 d, [a], b, c;\n\natom{.sem}{.scope}{.space}.exch{.level::cache_hint}.type2 d, [a], b {, cache_policy};\n\natom{.sem}{.scope}{.space}.add.noftz{.level::cache_hint}.f16     d, [a], b{, cache_policy};\natom{.sem}{.scope}{.space}.add.noftz{.level::cache_hint}.f16x2   d, [a], b{, cache_policy};\n\natom{.sem}{.scope}{.space}.add.noftz{.level::cache_hint}.bf16    d, [a], b{, cache_policy};\natom{.sem}{.scope}{.space}.add.noftz{.level::cache_hint}.bf16x2  d, [a], b{, cache_policy};\n\n.space =              { .global, .shared{::cta, ::cluster} };\n.sem =                { .relaxed, .acquire, .release, .acq_rel };\n.scope =              { .cta, .cluster, .gpu, .sys };\n\n.op =                 { .and, .or, .xor,\n                        .add, .inc, .dec,\n                        .min, .max };\n.level::cache_hint =  { .L2::cache_hint };\n.type =               { .b32, .b64, .u32, .u64, .s32, .s64, .f32, .f64 };\n.type1 =              { .b16, .b32, .b64, .b128 };\n.type2 =              { .b32, .b64, .b128 };"
                  ),
                  NonEmptyString(
                    "atom{.sem}{.scope}{.global}.add{.level::cache_hint}.vec_32_bit.f32                  d, [a], b{, cache_policy};\natom{.sem}{.scope}{.global}.op.noftz{.level::cache_hint}.vec_16_bit.half_word_type  d, [a], b{, cache_policy};\natom{.sem}{.scope}{.global}.op.noftz{.level::cache_hint}.vec_32_bit.packed_type     d, [a], b{, cache_policy};\n\n.sem =               { .relaxed, .acquire, .release, .acq_rel };\n.scope =             { .cta, .cluster, .gpu, .sys };\n.op =                { .add, .min, .max };\n.half_word_type =    { .f16, .bf16 };\n.packed_type =       { .f16x2, .bf16x2 };\n.vec_16_bit =        { .v2, .v4, .v8 }\n.vec_32_bit =        { .v2, .v4 };\n.level::cache_hint = { .L2::cache_hint }"
                  )
                )),
                Version(PositiveInt(1), NonNegativeInt.Zero),
                Target(11),
                Vec(
                  InstructionForm(
                    Opcode("atom"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(
                        ModifierName("add"),
                        ModifierName("and"),
                        ModifierName("dec"),
                        ModifierName("inc"),
                        ModifierName("max"),
                        ModifierName("min"),
                        ModifierName("or"),
                        ModifierName("xor")
                      ),
                      ModifierGroup(
                        ModifierName("b32"),
                        ModifierName("b64"),
                        ModifierName("f32"),
                        ModifierName("f64"),
                        ModifierName("s32"),
                        ModifierName("s64"),
                        ModifierName("u32"),
                        ModifierName("u64")
                      )
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName("L2::cache_hint"),
                      ModifierName("acq_rel"),
                      ModifierName("acquire"),
                      ModifierName("cluster"),
                      ModifierName("cta"),
                      ModifierName("global"),
                      ModifierName("gpu"),
                      ModifierName("relaxed"),
                      ModifierName("release"),
                      ModifierName("sys")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode("atom"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(ModifierName("cas")),
                      ModifierGroup(
                        ModifierName("b128"),
                        ModifierName("b16"),
                        ModifierName("b32"),
                        ModifierName("b64")
                      )
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName("acq_rel"),
                      ModifierName("acquire"),
                      ModifierName("cluster"),
                      ModifierName("cta"),
                      ModifierName("global"),
                      ModifierName("gpu"),
                      ModifierName("relaxed"),
                      ModifierName("release"),
                      ModifierName("sys")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode("atom"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(ModifierName("exch")),
                      ModifierGroup(
                        ModifierName("b128"),
                        ModifierName("b32"),
                        ModifierName("b64")
                      )
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName("L2::cache_hint"),
                      ModifierName("acq_rel"),
                      ModifierName("acquire"),
                      ModifierName("cluster"),
                      ModifierName("cta"),
                      ModifierName("global"),
                      ModifierName("gpu"),
                      ModifierName("relaxed"),
                      ModifierName("release"),
                      ModifierName("sys")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode("atom"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(ModifierName("add")),
                      ModifierGroup(ModifierName("noftz")),
                      ModifierGroup(ModifierName("f16"))
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName("L2::cache_hint"),
                      ModifierName("acq_rel"),
                      ModifierName("acquire"),
                      ModifierName("cluster"),
                      ModifierName("cta"),
                      ModifierName("global"),
                      ModifierName("gpu"),
                      ModifierName("relaxed"),
                      ModifierName("release"),
                      ModifierName("sys")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode("atom"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(ModifierName("add")),
                      ModifierGroup(ModifierName("noftz")),
                      ModifierGroup(ModifierName("f16x2"))
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName("L2::cache_hint"),
                      ModifierName("acq_rel"),
                      ModifierName("acquire"),
                      ModifierName("cluster"),
                      ModifierName("cta"),
                      ModifierName("global"),
                      ModifierName("gpu"),
                      ModifierName("relaxed"),
                      ModifierName("release"),
                      ModifierName("sys")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode("atom"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(ModifierName("add")),
                      ModifierGroup(ModifierName("noftz")),
                      ModifierGroup(ModifierName("bf16"))
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName("L2::cache_hint"),
                      ModifierName("acq_rel"),
                      ModifierName("acquire"),
                      ModifierName("cluster"),
                      ModifierName("cta"),
                      ModifierName("global"),
                      ModifierName("gpu"),
                      ModifierName("relaxed"),
                      ModifierName("release"),
                      ModifierName("sys")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode("atom"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(ModifierName("add")),
                      ModifierGroup(ModifierName("noftz")),
                      ModifierGroup(ModifierName("bf16x2"))
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName("L2::cache_hint"),
                      ModifierName("acq_rel"),
                      ModifierName("acquire"),
                      ModifierName("cluster"),
                      ModifierName("cta"),
                      ModifierName("global"),
                      ModifierName("gpu"),
                      ModifierName("relaxed"),
                      ModifierName("release"),
                      ModifierName("sys")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode("atom"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(ModifierName("add")),
                      ModifierGroup(
                        ModifierName("v2"),
                        ModifierName("v4")
                      ),
                      ModifierGroup(ModifierName("f32"))
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName("L2::cache_hint"),
                      ModifierName("acq_rel"),
                      ModifierName("acquire"),
                      ModifierName("cluster"),
                      ModifierName("cta"),
                      ModifierName("global"),
                      ModifierName("gpu"),
                      ModifierName("relaxed"),
                      ModifierName("release"),
                      ModifierName("sys")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode("atom"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(
                        ModifierName("add"),
                        ModifierName("max"),
                        ModifierName("min")
                      ),
                      ModifierGroup(ModifierName("noftz")),
                      ModifierGroup(
                        ModifierName("v2"),
                        ModifierName("v4"),
                        ModifierName("v8")
                      ),
                      ModifierGroup(
                        ModifierName("bf16"),
                        ModifierName("f16")
                      )
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName("L2::cache_hint"),
                      ModifierName("acq_rel"),
                      ModifierName("acquire"),
                      ModifierName("cluster"),
                      ModifierName("cta"),
                      ModifierName("global"),
                      ModifierName("gpu"),
                      ModifierName("relaxed"),
                      ModifierName("release"),
                      ModifierName("sys")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode("atom"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(
                        ModifierName("add"),
                        ModifierName("max"),
                        ModifierName("min")
                      ),
                      ModifierGroup(ModifierName("noftz")),
                      ModifierGroup(
                        ModifierName("v2"),
                        ModifierName("v4")
                      ),
                      ModifierGroup(
                        ModifierName("bf16x2"),
                        ModifierName("f16x2")
                      )
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName("L2::cache_hint"),
                      ModifierName("acq_rel"),
                      ModifierName("acquire"),
                      ModifierName("cluster"),
                      ModifierName("cta"),
                      ModifierName("global"),
                      ModifierName("gpu"),
                      ModifierName("relaxed"),
                      ModifierName("release"),
                      ModifierName("sys")
                    ),
                    false
                  )
                )
              )
            )))
        )
    case Bar extends Definition(
          Opcode("bar"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 1 || operands.size == 2 || operands.size == 3 || operands.size == 4)
              Right(Instruction.Bar(operands(0), operands.lift(1), operands.lift(2), operands.lift(3), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("bar", Vec(1, 2, 3, 4), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("parallel-synchronization-and-communication-instructions-bar"),
                NonEmptyString("9.7.14.1. Parallel Synchronization and Communication Instructions: bar, barrier"),
                NonEmptyString("bar, barrier"),
                NonEmptyString("Parallel Synchronization and Communication Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(NonEmptyString(
                  "barrier{.cta}.sync{.aligned}      a{, b};\nbarrier{.cta}.arrive{.aligned}    a, b;\n\nbarrier{.cta}.red.popc{.aligned}.u32  d, a{, b}, {!}c;\nbarrier{.cta}.red.op{.aligned}.pred   p, a{, b}, {!}c;\n\nbar{.cta}.sync      a{, b};\nbar{.cta}.arrive    a, b;\n\nbar{.cta}.red.popc.u32  d, a{, b}, {!}c;\nbar{.cta}.red.op.pred   p, a{, b}, {!}c;\n\n.op = { .and, .or };"
                ))),
                Version(PositiveInt(1), NonNegativeInt.Zero),
                Target(20),
                Vec(
                  InstructionForm(
                    Opcode("bar"),
                    Vec(OperandConstraint.Value, OperandConstraint.Value),
                    Vec(ModifierGroup(ModifierName("sync"))),
                    _root_.scala.collection.immutable.Set(ModifierName("cta")),
                    false
                  ),
                  InstructionForm(
                    Opcode("bar"),
                    Vec(OperandConstraint.Value, OperandConstraint.Value),
                    Vec(ModifierGroup(ModifierName("arrive"))),
                    _root_.scala.collection.immutable.Set(ModifierName("cta")),
                    false
                  ),
                  InstructionForm(
                    Opcode("bar"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(ModifierName("red")),
                      ModifierGroup(ModifierName("popc")),
                      ModifierGroup(ModifierName("u32"))
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName("cta")),
                    false
                  ),
                  InstructionForm(
                    Opcode("bar"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(ModifierName("red")),
                      ModifierGroup(
                        ModifierName("and"),
                        ModifierName("or")
                      ),
                      ModifierGroup(ModifierName("pred"))
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName("cta")),
                    false
                  )
                )
              )
            )))
        )
    case BarWarpSync extends Definition(
          Opcode("bar.warp.sync"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 1) Right(Instruction.BarWarpSync(operands(0), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("bar.warp.sync", Vec(1), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("parallel-synchronization-and-communication-instructions-bar-warp-sync"),
                NonEmptyString("9.7.14.2. Parallel Synchronization and Communication Instructions: bar.warp.sync"),
                NonEmptyString("bar.warp.sync"),
                NonEmptyString("Parallel Synchronization and Communication Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(NonEmptyString("bar.warp.sync      membermask;"))),
                Version(PositiveInt(6), NonNegativeInt.Zero),
                Target(30),
                Vec(InstructionForm(
                  Opcode("bar.warp.sync"),
                  Vec(OperandConstraint.Value),
                  Vec(),
                  _root_.scala.collection.immutable.Set(),
                  true
                ))
              )
            )))
        )
    case Barrier extends Definition(
          Opcode("barrier"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 1 || operands.size == 2 || operands.size == 3 || operands.size == 4)
              Right(Instruction.Barrier(operands(0), operands.lift(1), operands.lift(2), operands.lift(3), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("barrier", Vec(1, 2, 3, 4), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("parallel-synchronization-and-communication-instructions-bar"),
                NonEmptyString("9.7.14.1. Parallel Synchronization and Communication Instructions: bar, barrier"),
                NonEmptyString("bar, barrier"),
                NonEmptyString("Parallel Synchronization and Communication Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(NonEmptyString(
                  "barrier{.cta}.sync{.aligned}      a{, b};\nbarrier{.cta}.arrive{.aligned}    a, b;\n\nbarrier{.cta}.red.popc{.aligned}.u32  d, a{, b}, {!}c;\nbarrier{.cta}.red.op{.aligned}.pred   p, a{, b}, {!}c;\n\nbar{.cta}.sync      a{, b};\nbar{.cta}.arrive    a, b;\n\nbar{.cta}.red.popc.u32  d, a{, b}, {!}c;\nbar{.cta}.red.op.pred   p, a{, b}, {!}c;\n\n.op = { .and, .or };"
                ))),
                Version(PositiveInt(1), NonNegativeInt.Zero),
                Target(20),
                Vec(
                  InstructionForm(
                    Opcode("barrier"),
                    Vec(OperandConstraint.Value, OperandConstraint.Value),
                    Vec(ModifierGroup(ModifierName("sync"))),
                    _root_.scala.collection.immutable.Set(ModifierName("aligned"), ModifierName("cta")),
                    false
                  ),
                  InstructionForm(
                    Opcode("barrier"),
                    Vec(OperandConstraint.Value, OperandConstraint.Value),
                    Vec(ModifierGroup(ModifierName("arrive"))),
                    _root_.scala.collection.immutable.Set(ModifierName("aligned"), ModifierName("cta")),
                    false
                  ),
                  InstructionForm(
                    Opcode("barrier"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(ModifierName("red")),
                      ModifierGroup(ModifierName("popc")),
                      ModifierGroup(ModifierName("u32"))
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName("aligned"), ModifierName("cta")),
                    false
                  ),
                  InstructionForm(
                    Opcode("barrier"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(ModifierName("red")),
                      ModifierGroup(
                        ModifierName("and"),
                        ModifierName("or")
                      ),
                      ModifierGroup(ModifierName("pred"))
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName("aligned"), ModifierName("cta")),
                    false
                  )
                )
              )
            )))
        )
    case BarrierCluster extends Definition(
          Opcode("barrier.cluster"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 0) Right(Instruction.BarrierCluster(mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("barrier.cluster", Vec(0), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("parallel-synchronization-and-communication-instructions-barrier-cluster"),
                NonEmptyString("9.7.14.3. Parallel Synchronization and Communication Instructions: barrier.cluster"),
                NonEmptyString("barrier.cluster"),
                NonEmptyString("Parallel Synchronization and Communication Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(NonEmptyString(
                  "barrier.cluster.arrive{.sem}{.aligned};\nbarrier.cluster.wait{.acquire}{.aligned};\n\n.sem = {.release, .relaxed}"
                ))),
                Version(PositiveInt(7), NonNegativeInt(8)),
                Target(90),
                Vec(
                  InstructionForm(
                    Opcode("barrier.cluster"),
                    Vec(),
                    Vec(ModifierGroup(ModifierName("arrive"))),
                    _root_.scala.collection.immutable.Set(
                      ModifierName("aligned"),
                      ModifierName("relaxed"),
                      ModifierName("release")
                    ),
                    true
                  ),
                  InstructionForm(
                    Opcode("barrier.cluster"),
                    Vec(),
                    Vec(ModifierGroup(ModifierName("wait"))),
                    _root_.scala.collection.immutable.Set(
                      ModifierName("acquire"),
                      ModifierName("aligned")
                    ),
                    true
                  )
                )
              )
            )))
        )
    case Bfe extends Definition(
          Opcode("bfe"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 4)
              Right(Instruction.Bfe(operands(0), operands(1), operands(2), operands(3), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("bfe", Vec(4), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("integer-arithmetic-instructions-bfe"),
                NonEmptyString("9.7.1.20. Integer Arithmetic Instructions: bfe"),
                NonEmptyString("bfe"),
                NonEmptyString("Integer Arithmetic Instructions"),
                NonEmptyVec.unsafeFromVec(
                  Vec(NonEmptyString("bfe.type  d, a, b, c;\n\n.type = { .u32, .u64,\n          .s32, .s64 };"))
                ),
                Version(PositiveInt(2), NonNegativeInt.Zero),
                Target(20),
                Vec(InstructionForm(
                  Opcode("bfe"),
                  Vec(
                    OperandConstraint.Register,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Vec(ModifierGroup(
                    ModifierName("s32"),
                    ModifierName("s64"),
                    ModifierName("u32"),
                    ModifierName("u64")
                  )),
                  _root_.scala.collection.immutable.Set(),
                  true
                ))
              )
            )))
        )
    case Bfi extends Definition(
          Opcode("bfi"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 5)
              Right(Instruction.Bfi(operands(0), operands(1), operands(2), operands(3), operands(4), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("bfi", Vec(5), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("integer-arithmetic-instructions-bfi"),
                NonEmptyString("9.7.1.21. Integer Arithmetic Instructions: bfi"),
                NonEmptyString("bfi"),
                NonEmptyString("Integer Arithmetic Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(NonEmptyString("bfi.type  f, a, b, c, d;\n\n.type = { .b32, .b64 };"))),
                Version(PositiveInt(2), NonNegativeInt.Zero),
                Target(20),
                Vec(InstructionForm(
                  Opcode("bfi"),
                  Vec(
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Register
                  ),
                  Vec(ModifierGroup(
                    ModifierName("b32"),
                    ModifierName("b64")
                  )),
                  _root_.scala.collection.immutable.Set(),
                  true
                ))
              )
            )))
        )
    case Bfind extends Definition(
          Opcode("bfind"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 2) Right(Instruction.Bfind(operands(0), operands(1), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("bfind", Vec(2), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("integer-arithmetic-instructions-bfind"),
                NonEmptyString("9.7.1.17. Integer Arithmetic Instructions: bfind"),
                NonEmptyString("bfind"),
                NonEmptyString("Integer Arithmetic Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(NonEmptyString(
                  "bfind.type           d, a;\nbfind.shiftamt.type  d, a;\n\n.type = { .u32, .u64,\n          .s32, .s64 };"
                ))),
                Version(PositiveInt(2), NonNegativeInt.Zero),
                Target(20),
                Vec(
                  InstructionForm(
                    Opcode("bfind"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(ModifierGroup(
                      ModifierName("s32"),
                      ModifierName("s64"),
                      ModifierName("u32"),
                      ModifierName("u64")
                    )),
                    _root_.scala.collection.immutable.Set(),
                    true
                  ),
                  InstructionForm(
                    Opcode("bfind"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(ModifierName("shiftamt")),
                      ModifierGroup(
                        ModifierName("s32"),
                        ModifierName("s64"),
                        ModifierName("u32"),
                        ModifierName("u64")
                      )
                    ),
                    _root_.scala.collection.immutable.Set(),
                    true
                  )
                )
              )
            )))
        )
    case Bmsk extends Definition(
          Opcode("bmsk"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 3) Right(Instruction.Bmsk(operands(0), operands(1), operands(2), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("bmsk", Vec(3), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("integer-arithmetic-instructions-bmsk"),
                NonEmptyString("9.7.1.23. Integer Arithmetic Instructions: bmsk"),
                NonEmptyString("bmsk"),
                NonEmptyString("Integer Arithmetic Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(NonEmptyString("bmsk.mode.b32  d, a, b;\n\n.mode = { .clamp, .wrap };"))),
                Version(PositiveInt(7), NonNegativeInt(6)),
                Target(70),
                Vec(InstructionForm(
                  Opcode("bmsk"),
                  Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                  Vec(
                    ModifierGroup(
                      ModifierName("clamp"),
                      ModifierName("wrap")
                    ),
                    ModifierGroup(ModifierName("b32"))
                  ),
                  _root_.scala.collection.immutable.Set(),
                  true
                ))
              )
            )))
        )
    case Bra extends Definition(
          Opcode("bra"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 1) Right(Instruction.Bra(operands(0), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("bra", Vec(1), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("control-flow-instructions-bra"),
                NonEmptyString("9.7.13.3. Control Flow Instructions: bra"),
                NonEmptyString("bra"),
                NonEmptyString("Control Flow Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(NonEmptyString(
                  "@p   bra{.uni}  tgt;           // tgt is a label\n     bra{.uni}  tgt;           // unconditional branch"
                ))),
                Version(PositiveInt(1), NonNegativeInt.Zero),
                Target(10),
                Vec(InstructionForm(
                  Opcode("bra"),
                  Vec(OperandConstraint.Value),
                  Vec(),
                  _root_.scala.collection.immutable.Set(ModifierName("uni")),
                  true
                ))
              )
            )))
        )
    case Brev extends Definition(
          Opcode("brev"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 2) Right(Instruction.Brev(operands(0), operands(1), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("brev", Vec(2), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("integer-arithmetic-instructions-brev"),
                NonEmptyString("9.7.1.19. Integer Arithmetic Instructions: brev"),
                NonEmptyString("brev"),
                NonEmptyString("Integer Arithmetic Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(NonEmptyString("brev.type  d, a;\n\n.type = { .b32, .b64 };"))),
                Version(PositiveInt(2), NonNegativeInt.Zero),
                Target(20),
                Vec(InstructionForm(
                  Opcode("brev"),
                  Vec(OperandConstraint.Register, OperandConstraint.Value),
                  Vec(ModifierGroup(
                    ModifierName("b32"),
                    ModifierName("b64")
                  )),
                  _root_.scala.collection.immutable.Set(),
                  true
                ))
              )
            )))
        )
    case Brkpt extends Definition(
          Opcode("brkpt"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 0) Right(Instruction.Brkpt(mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("brkpt", Vec(0), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("miscellaneous-instructions-brkpt"),
                NonEmptyString("9.7.20.1. Miscellaneous Instructions: brkpt"),
                NonEmptyString("brkpt"),
                NonEmptyString("Miscellaneous Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(NonEmptyString("brkpt;"))),
                Version(PositiveInt(1), NonNegativeInt.Zero),
                Target(11),
                Vec(InstructionForm(
                  Opcode("brkpt"),
                  Vec(),
                  Vec(),
                  _root_.scala.collection.immutable.Set(),
                  true
                ))
              )
            )))
        )
    case BrxIdx extends Definition(
          Opcode("brx.idx"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 2) Right(Instruction.BrxIdx(operands(0), operands(1), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("brx.idx", Vec(2), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("control-flow-instructions-brx-idx"),
                NonEmptyString("9.7.13.4. Control Flow Instructions: brx.idx"),
                NonEmptyString("brx.idx"),
                NonEmptyString("Control Flow Instructions"),
                NonEmptyVec.unsafeFromVec(
                  Vec(NonEmptyString("@p    brx.idx{.uni} index, tlist;\n      brx.idx{.uni} index, tlist;"))
                ),
                Version(PositiveInt(6), NonNegativeInt.Zero),
                Target(30),
                Vec(InstructionForm(
                  Opcode("brx.idx"),
                  Vec(OperandConstraint.Value, OperandConstraint.Value),
                  Vec(),
                  _root_.scala.collection.immutable.Set(ModifierName("uni")),
                  true
                ))
              )
            )))
        )
    case Call extends Definition(
          Opcode("call"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 1 || operands.size == 2 || operands.size == 3 || operands.size == 4) Right(if (
              operands.size >= 2 && Operand.isParenthesized(operands(0))
            ) Instruction.Call(Some(operands(0)), operands(1), operands.lift(2), operands.lift(3), mods, pred)
            else Instruction.Call(None, operands(0), operands.lift(1), operands.lift(2), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("call", Vec(1, 2, 3, 4), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("control-flow-instructions-call"),
                NonEmptyString("9.7.13.5. Control Flow Instructions: call"),
                NonEmptyString("call"),
                NonEmptyString("Control Flow Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(NonEmptyString(
                  "// direct call to named function, func is a symbol\ncall{.uni} (ret-param), func, (param-list);\ncall{.uni} func, (param-list);\ncall{.uni} func;\n\n// indirect call via pointer, with full list of call targets\ncall{.uni} (ret-param), fptr, (param-list), flist;\ncall{.uni} fptr, (param-list), flist;\ncall{.uni} fptr, flist;\n\n// indirect call via pointer, with no knowledge of call targets\ncall{.uni} (ret-param), fptr, (param-list), fproto;\ncall{.uni} fptr, (param-list), fproto;\ncall{.uni} fptr, fproto;"
                ))),
                Version(PositiveInt(1), NonNegativeInt.Zero),
                Target(20),
                Vec(
                  InstructionForm(
                    Opcode("call"),
                    Vec(
                      OperandConstraint.Unknown(NonEmptyString("unparsed")),
                      OperandConstraint.Symbol,
                      OperandConstraint.Unknown(NonEmptyString("unparsed"))
                    ),
                    Vec(),
                    _root_.scala.collection.immutable.Set(ModifierName("uni")),
                    false
                  ),
                  InstructionForm(
                    Opcode("call"),
                    Vec(OperandConstraint.Symbol, OperandConstraint.Unknown(NonEmptyString("unparsed"))),
                    Vec(),
                    _root_.scala.collection.immutable.Set(ModifierName("uni")),
                    false
                  ),
                  InstructionForm(
                    Opcode("call"),
                    Vec(OperandConstraint.Symbol),
                    Vec(),
                    _root_.scala.collection.immutable.Set(ModifierName("uni")),
                    true
                  ),
                  InstructionForm(
                    Opcode("call"),
                    Vec(
                      OperandConstraint.Unknown(NonEmptyString("unparsed")),
                      OperandConstraint.Value,
                      OperandConstraint.Unknown(NonEmptyString("unparsed")),
                      OperandConstraint.Value
                    ),
                    Vec(),
                    _root_.scala.collection.immutable.Set(ModifierName("uni")),
                    false
                  ),
                  InstructionForm(
                    Opcode("call"),
                    Vec(
                      OperandConstraint.Value,
                      OperandConstraint.Unknown(NonEmptyString("unparsed")),
                      OperandConstraint.Value
                    ),
                    Vec(),
                    _root_.scala.collection.immutable.Set(ModifierName("uni")),
                    false
                  ),
                  InstructionForm(
                    Opcode("call"),
                    Vec(OperandConstraint.Value, OperandConstraint.Value),
                    Vec(),
                    _root_.scala.collection.immutable.Set(ModifierName("uni")),
                    true
                  ),
                  InstructionForm(
                    Opcode("call"),
                    Vec(
                      OperandConstraint.Unknown(NonEmptyString("unparsed")),
                      OperandConstraint.Value,
                      OperandConstraint.Unknown(NonEmptyString("unparsed")),
                      OperandConstraint.Value
                    ),
                    Vec(),
                    _root_.scala.collection.immutable.Set(ModifierName("uni")),
                    false
                  ),
                  InstructionForm(
                    Opcode("call"),
                    Vec(
                      OperandConstraint.Value,
                      OperandConstraint.Unknown(NonEmptyString("unparsed")),
                      OperandConstraint.Value
                    ),
                    Vec(),
                    _root_.scala.collection.immutable.Set(ModifierName("uni")),
                    false
                  ),
                  InstructionForm(
                    Opcode("call"),
                    Vec(OperandConstraint.Value, OperandConstraint.Value),
                    Vec(),
                    _root_.scala.collection.immutable.Set(ModifierName("uni")),
                    true
                  )
                )
              )
            )))
        )
    case Clmad extends Definition(
          Opcode("clmad"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 4)
              Right(Instruction.Clmad(operands(0), operands(1), operands(2), operands(3), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("clmad", Vec(4), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("integer-arithmetic-instructions-clmad"),
                NonEmptyString("9.7.1.5. Integer Arithmetic Instructions: clmad"),
                NonEmptyString("clmad"),
                NonEmptyString("Integer Arithmetic Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(NonEmptyString("clmad.mode.u64 d, a, b, c;\n\n.mode = { .hi, .lo };"))),
                Version(PositiveInt(9), NonNegativeInt(3)),
                Target(80),
                Vec(InstructionForm(
                  Opcode("clmad"),
                  Vec(
                    OperandConstraint.Register,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Vec(
                    ModifierGroup(
                      ModifierName("hi"),
                      ModifierName("lo")
                    ),
                    ModifierGroup(ModifierName("u64"))
                  ),
                  _root_.scala.collection.immutable.Set(),
                  true
                ))
              )
            )))
        )
    case ClusterLaunchControlQueryCancel extends Definition(
          Opcode("clusterlaunchcontrol.query_cancel"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 2)
              Right(Instruction.ClusterLaunchControlQueryCancel(operands(0), operands(1), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount(
              "clusterlaunchcontrol.query_cancel",
              Vec(2),
              operands.size
            ))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString(
                  "parallel-synchronization-and-communication-instructions-clusterlaunchcontrol-query-cancel"
                ),
                NonEmptyString(
                  "9.7.14.19. Parallel Synchronization and Communication Instructions: clusterlaunchcontrol.query_cancel"
                ),
                NonEmptyString("clusterlaunchcontrol.query_cancel"),
                NonEmptyString("Parallel Synchronization and Communication Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(NonEmptyString(
                  "clusterlaunchcontrol.query_cancel.is_canceled.pred.b128 pred, try_cancel_response;\n\nclusterlaunchcontrol.query_cancel.get_first_ctaid.v4.b32.b128 {xdim, ydim, zdim, _},  try_cancel_response;\n\nclusterlaunchcontrol.query_cancel.get_first_ctaid{::dimension}.b32.b128 reg, try_cancel_response;\n\n::dimension = { ::x, ::y, ::z };"
                ))),
                Version(PositiveInt(8), NonNegativeInt(6)),
                Target(100),
                Vec(
                  InstructionForm(
                    Opcode("clusterlaunchcontrol.query_cancel"),
                    Vec(OperandConstraint.Value, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(ModifierName("is_canceled")),
                      ModifierGroup(ModifierName("pred")),
                      ModifierGroup(ModifierName("b128"))
                    ),
                    _root_.scala.collection.immutable.Set(),
                    true
                  ),
                  InstructionForm(
                    Opcode("clusterlaunchcontrol.query_cancel"),
                    Vec(OperandConstraint.VectorOperand, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(ModifierName("get_first_ctaid")),
                      ModifierGroup(ModifierName("v4")),
                      ModifierGroup(ModifierName("b32")),
                      ModifierGroup(ModifierName("b128"))
                    ),
                    _root_.scala.collection.immutable.Set(),
                    true
                  ),
                  InstructionForm(
                    Opcode("clusterlaunchcontrol.query_cancel"),
                    Vec(OperandConstraint.Value, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(ModifierName("get_first_ctaid")),
                      ModifierGroup(ModifierName("b32")),
                      ModifierGroup(ModifierName("b128"))
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  )
                )
              )
            )))
        )
    case ClusterLaunchControlTryCancel extends Definition(
          Opcode("clusterlaunchcontrol.try_cancel"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 2)
              Right(Instruction.ClusterLaunchControlTryCancel(operands(0), operands(1), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount(
              "clusterlaunchcontrol.try_cancel",
              Vec(2),
              operands.size
            ))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString(
                  "parallel-synchronization-and-communication-instructions-clusterlaunchcontrol-try-cancel"
                ),
                NonEmptyString(
                  "9.7.14.18. Parallel Synchronization and Communication Instructions: clusterlaunchcontrol.try_cancel"
                ),
                NonEmptyString("clusterlaunchcontrol.try_cancel"),
                NonEmptyString("Parallel Synchronization and Communication Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(NonEmptyString(
                  "clusterlaunchcontrol.try_cancel.async{.space}.completion_mechanism{.multicast::cluster::all}.b128 [addr], [mbar];\n\n.completion_mechanism = { .mbarrier::complete_tx::bytes };\n.space = { .shared::cta };"
                ))),
                Version(PositiveInt(8), NonNegativeInt(6)),
                Target(100),
                Vec(InstructionForm(
                  Opcode("clusterlaunchcontrol.try_cancel"),
                  Vec(OperandConstraint.Address, OperandConstraint.Address),
                  Vec(
                    ModifierGroup(ModifierName("async")),
                    ModifierGroup(ModifierName("mbarrier::complete_tx::bytes")),
                    ModifierGroup(ModifierName("b128"))
                  ),
                  _root_.scala.collection.immutable.Set(
                    ModifierName("multicast::cluster::all"),
                    ModifierName("shared::cta")
                  ),
                  false
                ))
              )
            )))
        )
    case Clz extends Definition(
          Opcode("clz"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 2) Right(Instruction.Clz(operands(0), operands(1), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("clz", Vec(2), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("integer-arithmetic-instructions-clz"),
                NonEmptyString("9.7.1.16. Integer Arithmetic Instructions: clz"),
                NonEmptyString("clz"),
                NonEmptyString("Integer Arithmetic Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(NonEmptyString("clz.type  d, a;\n\n.type = { .b32, .b64 };"))),
                Version(PositiveInt(2), NonNegativeInt.Zero),
                Target(20),
                Vec(InstructionForm(
                  Opcode("clz"),
                  Vec(OperandConstraint.Register, OperandConstraint.Value),
                  Vec(ModifierGroup(
                    ModifierName("b32"),
                    ModifierName("b64")
                  )),
                  _root_.scala.collection.immutable.Set(),
                  true
                ))
              )
            )))
        )
    case Cnot extends Definition(
          Opcode("cnot"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 2) Right(Instruction.Cnot(operands(0), operands(1), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("cnot", Vec(2), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("logic-and-shift-instructions-cnot"),
                NonEmptyString("9.7.8.5. Logic and Shift Instructions: cnot"),
                NonEmptyString("cnot"),
                NonEmptyString("Logic and Shift Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(NonEmptyString("cnot.type d, a;\n\n.type = { .b16, .b32, .b64 };"))),
                Version(PositiveInt(1), NonNegativeInt.Zero),
                Target(10),
                Vec(InstructionForm(
                  Opcode("cnot"),
                  Vec(OperandConstraint.Register, OperandConstraint.Value),
                  Vec(ModifierGroup(
                    ModifierName("b16"),
                    ModifierName("b32"),
                    ModifierName("b64")
                  )),
                  _root_.scala.collection.immutable.Set(),
                  true
                ))
              )
            )))
        )
    case CopySign extends Definition(
          Opcode("copysign"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 3) Right(Instruction.CopySign(operands(0), operands(1), operands(2), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("copysign", Vec(3), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("floating-point-instructions-copysign"),
                NonEmptyString("9.7.3.2. Floating Point Instructions: copysign"),
                NonEmptyString("copysign"),
                NonEmptyString("Floating Point Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(NonEmptyString("copysign.type  d, a, b;\n\n.type = { .f32, .f64 };"))),
                Version(PositiveInt(2), NonNegativeInt.Zero),
                Target(20),
                Vec(InstructionForm(
                  Opcode("copysign"),
                  Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                  Vec(ModifierGroup(
                    ModifierName("f32"),
                    ModifierName("f64")
                  )),
                  _root_.scala.collection.immutable.Set(),
                  true
                ))
              )
            )))
        )
    case Cos extends Definition(
          Opcode("cos"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 2) Right(Instruction.Cos(operands(0), operands(1), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("cos", Vec(2), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("floating-point-instructions-cos"),
                NonEmptyString("9.7.3.19. Floating Point Instructions: cos"),
                NonEmptyString("cos"),
                NonEmptyString("Floating Point Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(NonEmptyString("cos.approx{.ftz}.f32  d, a;"))),
                Version(PositiveInt(1), NonNegativeInt.Zero),
                Target(10),
                Vec(InstructionForm(
                  Opcode("cos"),
                  Vec(OperandConstraint.Register, OperandConstraint.Value),
                  Vec(
                    ModifierGroup(ModifierName("approx")),
                    ModifierGroup(ModifierName("f32"))
                  ),
                  _root_.scala.collection.immutable.Set(ModifierName("ftz")),
                  true
                ))
              )
            )))
        )
    case CpAsync extends Definition(
          Opcode("cp.async"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 3 || operands.size == 4 || operands.size == 5) Right(Instruction.CpAsync(
              operands(0),
              operands(1),
              operands(2),
              operands.lift(3),
              operands.lift(4),
              mods,
              pred
            ))
            else Left(Instruction.DecodeError.InvalidOperandCount("cp.async", Vec(3, 4, 5), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("data-movement-and-conversion-instructions-cp-async"),
                NonEmptyString("9.7.9.26.3.1. Data Movement and Conversion Instructions: cp.async"),
                NonEmptyString("cp.async"),
                NonEmptyString("Data Movement and Conversion Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(NonEmptyString(
                  "cp.async.ca.shared{::cta}.global{.level::cache_hint}{.level::prefetch_size}\n                         [dst], [src], cp-size{, src-size}{, cache_policy} ;\ncp.async.cg.shared{::cta}.global{.level::cache_hint}{.level::prefetch_size}\n                         [dst], [src], 16{, src-size}{, cache_policy} ;\ncp.async.ca.shared{::cta}.global{.level::cache_hint}{.level::prefetch_size}\n                         [dst], [src], cp-size{, ignore-src}{, cache_policy} ;\ncp.async.cg.shared{::cta}.global{.level::cache_hint}{.level::prefetch_size}\n                         [dst], [src], 16{, ignore-src}{, cache_policy} ;\n\n.level::cache_hint =     { .L2::cache_hint }\n.level::prefetch_size =  { .L2::64B, .L2::128B, .L2::256B }\ncp-size =                { 4, 8, 16 }"
                ))),
                Version(PositiveInt(7), NonNegativeInt.Zero),
                Target(30),
                Vec(
                  InstructionForm(
                    Opcode("cp.async"),
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(ModifierName("ca")),
                      ModifierGroup(ModifierName("shared")),
                      ModifierGroup(ModifierName("global"))
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName("L2::cache_hint")),
                    false
                  ),
                  InstructionForm(
                    Opcode("cp.async"),
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(ModifierName("cg")),
                      ModifierGroup(ModifierName("shared")),
                      ModifierGroup(ModifierName("global"))
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName("L2::cache_hint")),
                    false
                  ),
                  InstructionForm(
                    Opcode("cp.async"),
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(ModifierName("ca")),
                      ModifierGroup(ModifierName("shared")),
                      ModifierGroup(ModifierName("global"))
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName("L2::cache_hint")),
                    false
                  ),
                  InstructionForm(
                    Opcode("cp.async"),
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(ModifierName("cg")),
                      ModifierGroup(ModifierName("shared")),
                      ModifierGroup(ModifierName("global"))
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName("L2::cache_hint")),
                    false
                  )
                )
              )
            )))
        )
    case CpAsyncBulkMBarrier extends Definition(
          Opcode("cp.async.bulk"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            val modifierNames = mods.map(_.name)
            if (
              !modifierNames.contains("bulk_group") &&
              (if (modifierNames.contains("ignore_oob")) operands.size == 6 || operands.size == 7
               else if (modifierNames.contains("multicast::cluster")) operands.size >= 4 && operands.size <= 6
               else operands.size == 4 || operands.size == 5)
            ) Right(if (modifierNames.contains("ignore_oob")) Instruction.CpAsyncBulkMBarrier(
              operands(0),
              operands(1),
              operands(2),
              operands(5),
              Some(operands(3)),
              Some(operands(4)),
              None,
              operands.lift(6),
              mods,
              pred
            )
            else if (modifierNames.contains("multicast::cluster")) Instruction.CpAsyncBulkMBarrier(
              operands(0),
              operands(1),
              operands(2),
              operands(3),
              None,
              None,
              operands.lift(4),
              operands.lift(5),
              mods,
              pred
            )
            else Instruction.CpAsyncBulkMBarrier(
              operands(0),
              operands(1),
              operands(2),
              operands(3),
              None,
              None,
              None,
              operands.lift(4),
              mods,
              pred
            ))
            else if (!modifierNames.contains("bulk_group"))
              Left(Instruction.DecodeError.InvalidOperandCount("cp.async.bulk", Vec(4, 5, 6, 7), operands.size))
            else Left(Instruction.DecodeError.UnknownOpcode((("cp.async.bulk") +: modifierNames).mkString(".")))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("data-movement-and-conversion-instructions-cp-async-bulk"),
                NonEmptyString("9.7.9.26.4.1. Data Movement and Conversion Instructions: cp.async.bulk"),
                NonEmptyString("cp.async.bulk"),
                NonEmptyString("Data Movement and Conversion Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(NonEmptyString(
                  "// global -> shared::cta\ncp.async.bulk{.sem}.dst.src.completion_mechanism{.level::cache_hint}{.ignore_oob}\n                      [dstMem], [srcMem], size{, ignoreBytesLeft, ignoreBytesRight}, [mbar] {, cache_policy};\n\n.sem =                  { .weak }\n.dst =                  { .shared::cta }\n.src =                  { .global }\n.completion_mechanism = { .mbarrier::complete_tx::bytes }\n.level::cache_hint =    { .L2::cache_hint }\n\ncp.async.bulk.sem.scope.dst.src.completion_mechanism{.level::cache_hint}{.ignore_oob}.type\n                      [dstMem], [srcMem], size{, ignoreBytesLeft, ignoreBytesRight}, [mbar] {, cache_policy};\n\n.sem =                  { .relaxed }\n.scope =                { .cta, .cluster, .gpu, .sys }\n.dst =                  { .shared::cta }\n.src =                  { .global }\n.completion_mechanism = { .mbarrier::complete_tx::bytes }\n.level::cache_hint =    { .L2::cache_hint }\n.type =                 { .b128 }\n\n\n// global -> shared::cluster\ncp.async.bulk{.sem}.dst.src.completion_mechanism{.multicast}{.level::cache_hint}\n                      [dstMem], [srcMem], size, [mbar] {, ctaMask} {, cache_policy};\n\n.sem =                  { .weak }\n.dst =                  { .shared::cluster }\n.src =                  { .global }\n.completion_mechanism = { .mbarrier::complete_tx::bytes }\n.level::cache_hint =    { .L2::cache_hint }\n.multicast =            { .multicast::cluster }\n\ncp.async.bulk.sem.scope.dst.src.completion_mechanism{.multicast}{.level::cache_hint}.type\n                      [dstMem], [srcMem], size, [mbar] {, ctaMask} {, cache_policy};\n\n.sem =                  { .relaxed }\n.scope =                { .cta, .cluster, .gpu, .sys }\n.dst =                  { .shared::cluster }\n.src =                  { .global }\n.completion_mechanism = { .mbarrier::complete_tx::bytes }\n.level::cache_hint =    { .L2::cache_hint }\n.multicast =            { .multicast::cluster }\n.type =                 { .b128 }\n\n\n// shared::cta -> shared::cluster\ncp.async.bulk{.sem}.dst.src.completion_mechanism [dstMem], [srcMem], size, [mbar];\n\n.sem =                  { .weak }\n.dst =                  { .shared::cluster }\n.src =                  { .shared::cta }\n.completion_mechanism = { .mbarrier::complete_tx::bytes }\n\ncp.async.bulk.sem.scope.dst.src.completion_mechanism.type [dstMem], [srcMem], size, [mbar];\n\n.sem =                  { .relaxed }\n.scope =                { .cta, .cluster }\n.dst =                  { .shared::cluster }\n.src =                  { .shared::cta }\n.completion_mechanism = { .mbarrier::complete_tx::bytes }\n.type =                 { .b128 }\n\n\n// shared::cta -> global\ncp.async.bulk{.sem}.dst.src.completion_mechanism{.level::cache_hint}{.cp_mask}\n                      [dstMem], [srcMem], size {, cache_policy} {, byteMask};\n\n.sem =                  { .weak }\n.dst =                  { .global }\n.src =                  { .shared::cta }\n.completion_mechanism = { .bulk_group }\n.level::cache_hint =    { .L2::cache_hint }\n\ncp.async.bulk.sem.scope.dst.src.completion_mechanism{.level::cache_hint}{.cp_mask}.type\n                      [dstMem], [srcMem], size {, cache_policy} {, byteMask};\n\n.sem =                  { .relaxed }\n.scope =                { .cta, .cluster, .gpu, .sys }\n.dst =                  { .global }\n.src =                  { .shared::cta }\n.completion_mechanism = { .bulk_group }\n.level::cache_hint =    { .L2::cache_hint }\n.type =                 { .b128 }"
                ))),
                Version(PositiveInt(8), NonNegativeInt.Zero),
                Target(90),
                Vec(
                  InstructionForm(
                    Opcode("cp.async.bulk"),
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Address,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(ModifierName("global")),
                      ModifierGroup(ModifierName("shared::cta")),
                      ModifierGroup(ModifierName("bulk_group"))
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName("L2::cache_hint"),
                      ModifierName("ignore_oob"),
                      ModifierName("relaxed")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode("cp.async.bulk"),
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Address,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(ModifierName("relaxed")),
                      ModifierGroup(
                        ModifierName("cluster"),
                        ModifierName("cta"),
                        ModifierName("gpu"),
                        ModifierName("sys")
                      ),
                      ModifierGroup(ModifierName("global")),
                      ModifierGroup(ModifierName("shared::cta")),
                      ModifierGroup(ModifierName("bulk_group")),
                      ModifierGroup(ModifierName("b128"))
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName("L2::cache_hint"),
                      ModifierName("ignore_oob")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode("cp.async.bulk"),
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(ModifierName("global")),
                      ModifierGroup(ModifierName("shared::cta")),
                      ModifierGroup(ModifierName("bulk_group"))
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName("L2::cache_hint"),
                      ModifierName("multicast::cluster"),
                      ModifierName("relaxed")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode("cp.async.bulk"),
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(ModifierName("relaxed")),
                      ModifierGroup(
                        ModifierName("cluster"),
                        ModifierName("cta"),
                        ModifierName("gpu"),
                        ModifierName("sys")
                      ),
                      ModifierGroup(ModifierName("global")),
                      ModifierGroup(ModifierName("shared::cta")),
                      ModifierGroup(ModifierName("bulk_group")),
                      ModifierGroup(ModifierName("b128"))
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName("L2::cache_hint"),
                      ModifierName("multicast::cluster")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode("cp.async.bulk"),
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Address
                    ),
                    Vec(
                      ModifierGroup(ModifierName("global")),
                      ModifierGroup(ModifierName("shared::cta")),
                      ModifierGroup(ModifierName("bulk_group"))
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName("relaxed")),
                    false
                  ),
                  InstructionForm(
                    Opcode("cp.async.bulk"),
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Address
                    ),
                    Vec(
                      ModifierGroup(ModifierName("relaxed")),
                      ModifierGroup(
                        ModifierName("cluster"),
                        ModifierName("cta"),
                        ModifierName("gpu"),
                        ModifierName("sys")
                      ),
                      ModifierGroup(ModifierName("global")),
                      ModifierGroup(ModifierName("shared::cta")),
                      ModifierGroup(ModifierName("bulk_group")),
                      ModifierGroup(ModifierName("b128"))
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Opcode("cp.async.bulk"),
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(ModifierName("global")),
                      ModifierGroup(ModifierName("shared::cta")),
                      ModifierGroup(ModifierName("bulk_group"))
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName("L2::cache_hint"),
                      ModifierName("cp_mask"),
                      ModifierName("relaxed")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode("cp.async.bulk"),
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(ModifierName("relaxed")),
                      ModifierGroup(
                        ModifierName("cluster"),
                        ModifierName("cta"),
                        ModifierName("gpu"),
                        ModifierName("sys")
                      ),
                      ModifierGroup(ModifierName("global")),
                      ModifierGroup(ModifierName("shared::cta")),
                      ModifierGroup(ModifierName("bulk_group")),
                      ModifierGroup(ModifierName("b128"))
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName("L2::cache_hint"),
                      ModifierName("cp_mask")
                    ),
                    false
                  )
                )
              )
            )))
        )
    case CpAsyncBulkGroup extends Definition(
          Opcode("cp.async.bulk"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            val modifierNames = mods.map(_.name)
            if (
              modifierNames.contains("bulk_group") && (operands.size == 3 || operands.size == 4 || operands.size == 5)
            ) Right(if (operands.size == 3)
              Instruction.CpAsyncBulkGroup(operands(0), operands(1), operands(2), None, None, mods, pred)
            else if (operands.size == 4 && modifierNames.contains("cp_mask"))
              Instruction.CpAsyncBulkGroup(operands(0), operands(1), operands(2), None, Some(operands(3)), mods, pred)
            else Instruction.CpAsyncBulkGroup(
              operands(0),
              operands(1),
              operands(2),
              Some(operands(3)),
              operands.lift(4),
              mods,
              pred
            ))
            else if (modifierNames.contains("bulk_group"))
              Left(Instruction.DecodeError.InvalidOperandCount("cp.async.bulk", Vec(3, 4, 5), operands.size))
            else Left(Instruction.DecodeError.UnknownOpcode((("cp.async.bulk") +: modifierNames).mkString(".")))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("data-movement-and-conversion-instructions-cp-async-bulk"),
                NonEmptyString("9.7.9.26.4.1. Data Movement and Conversion Instructions: cp.async.bulk"),
                NonEmptyString("cp.async.bulk"),
                NonEmptyString("Data Movement and Conversion Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(NonEmptyString(
                  "// global -> shared::cta\ncp.async.bulk{.sem}.dst.src.completion_mechanism{.level::cache_hint}{.ignore_oob}\n                      [dstMem], [srcMem], size{, ignoreBytesLeft, ignoreBytesRight}, [mbar] {, cache_policy};\n\n.sem =                  { .weak }\n.dst =                  { .shared::cta }\n.src =                  { .global }\n.completion_mechanism = { .mbarrier::complete_tx::bytes }\n.level::cache_hint =    { .L2::cache_hint }\n\ncp.async.bulk.sem.scope.dst.src.completion_mechanism{.level::cache_hint}{.ignore_oob}.type\n                      [dstMem], [srcMem], size{, ignoreBytesLeft, ignoreBytesRight}, [mbar] {, cache_policy};\n\n.sem =                  { .relaxed }\n.scope =                { .cta, .cluster, .gpu, .sys }\n.dst =                  { .shared::cta }\n.src =                  { .global }\n.completion_mechanism = { .mbarrier::complete_tx::bytes }\n.level::cache_hint =    { .L2::cache_hint }\n.type =                 { .b128 }\n\n\n// global -> shared::cluster\ncp.async.bulk{.sem}.dst.src.completion_mechanism{.multicast}{.level::cache_hint}\n                      [dstMem], [srcMem], size, [mbar] {, ctaMask} {, cache_policy};\n\n.sem =                  { .weak }\n.dst =                  { .shared::cluster }\n.src =                  { .global }\n.completion_mechanism = { .mbarrier::complete_tx::bytes }\n.level::cache_hint =    { .L2::cache_hint }\n.multicast =            { .multicast::cluster }\n\ncp.async.bulk.sem.scope.dst.src.completion_mechanism{.multicast}{.level::cache_hint}.type\n                      [dstMem], [srcMem], size, [mbar] {, ctaMask} {, cache_policy};\n\n.sem =                  { .relaxed }\n.scope =                { .cta, .cluster, .gpu, .sys }\n.dst =                  { .shared::cluster }\n.src =                  { .global }\n.completion_mechanism = { .mbarrier::complete_tx::bytes }\n.level::cache_hint =    { .L2::cache_hint }\n.multicast =            { .multicast::cluster }\n.type =                 { .b128 }\n\n\n// shared::cta -> shared::cluster\ncp.async.bulk{.sem}.dst.src.completion_mechanism [dstMem], [srcMem], size, [mbar];\n\n.sem =                  { .weak }\n.dst =                  { .shared::cluster }\n.src =                  { .shared::cta }\n.completion_mechanism = { .mbarrier::complete_tx::bytes }\n\ncp.async.bulk.sem.scope.dst.src.completion_mechanism.type [dstMem], [srcMem], size, [mbar];\n\n.sem =                  { .relaxed }\n.scope =                { .cta, .cluster }\n.dst =                  { .shared::cluster }\n.src =                  { .shared::cta }\n.completion_mechanism = { .mbarrier::complete_tx::bytes }\n.type =                 { .b128 }\n\n\n// shared::cta -> global\ncp.async.bulk{.sem}.dst.src.completion_mechanism{.level::cache_hint}{.cp_mask}\n                      [dstMem], [srcMem], size {, cache_policy} {, byteMask};\n\n.sem =                  { .weak }\n.dst =                  { .global }\n.src =                  { .shared::cta }\n.completion_mechanism = { .bulk_group }\n.level::cache_hint =    { .L2::cache_hint }\n\ncp.async.bulk.sem.scope.dst.src.completion_mechanism{.level::cache_hint}{.cp_mask}.type\n                      [dstMem], [srcMem], size {, cache_policy} {, byteMask};\n\n.sem =                  { .relaxed }\n.scope =                { .cta, .cluster, .gpu, .sys }\n.dst =                  { .global }\n.src =                  { .shared::cta }\n.completion_mechanism = { .bulk_group }\n.level::cache_hint =    { .L2::cache_hint }\n.type =                 { .b128 }"
                ))),
                Version(PositiveInt(8), NonNegativeInt.Zero),
                Target(90),
                Vec(
                  InstructionForm(
                    Opcode("cp.async.bulk"),
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Address,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(ModifierName("global")),
                      ModifierGroup(ModifierName("shared::cta")),
                      ModifierGroup(ModifierName("bulk_group"))
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName("L2::cache_hint"),
                      ModifierName("ignore_oob"),
                      ModifierName("relaxed")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode("cp.async.bulk"),
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Address,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(ModifierName("relaxed")),
                      ModifierGroup(
                        ModifierName("cluster"),
                        ModifierName("cta"),
                        ModifierName("gpu"),
                        ModifierName("sys")
                      ),
                      ModifierGroup(ModifierName("global")),
                      ModifierGroup(ModifierName("shared::cta")),
                      ModifierGroup(ModifierName("bulk_group")),
                      ModifierGroup(ModifierName("b128"))
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName("L2::cache_hint"),
                      ModifierName("ignore_oob")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode("cp.async.bulk"),
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(ModifierName("global")),
                      ModifierGroup(ModifierName("shared::cta")),
                      ModifierGroup(ModifierName("bulk_group"))
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName("L2::cache_hint"),
                      ModifierName("multicast::cluster"),
                      ModifierName("relaxed")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode("cp.async.bulk"),
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(ModifierName("relaxed")),
                      ModifierGroup(
                        ModifierName("cluster"),
                        ModifierName("cta"),
                        ModifierName("gpu"),
                        ModifierName("sys")
                      ),
                      ModifierGroup(ModifierName("global")),
                      ModifierGroup(ModifierName("shared::cta")),
                      ModifierGroup(ModifierName("bulk_group")),
                      ModifierGroup(ModifierName("b128"))
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName("L2::cache_hint"),
                      ModifierName("multicast::cluster")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode("cp.async.bulk"),
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Address
                    ),
                    Vec(
                      ModifierGroup(ModifierName("global")),
                      ModifierGroup(ModifierName("shared::cta")),
                      ModifierGroup(ModifierName("bulk_group"))
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName("relaxed")),
                    false
                  ),
                  InstructionForm(
                    Opcode("cp.async.bulk"),
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Address
                    ),
                    Vec(
                      ModifierGroup(ModifierName("relaxed")),
                      ModifierGroup(
                        ModifierName("cluster"),
                        ModifierName("cta"),
                        ModifierName("gpu"),
                        ModifierName("sys")
                      ),
                      ModifierGroup(ModifierName("global")),
                      ModifierGroup(ModifierName("shared::cta")),
                      ModifierGroup(ModifierName("bulk_group")),
                      ModifierGroup(ModifierName("b128"))
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Opcode("cp.async.bulk"),
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(ModifierName("global")),
                      ModifierGroup(ModifierName("shared::cta")),
                      ModifierGroup(ModifierName("bulk_group"))
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName("L2::cache_hint"),
                      ModifierName("cp_mask"),
                      ModifierName("relaxed")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode("cp.async.bulk"),
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(ModifierName("relaxed")),
                      ModifierGroup(
                        ModifierName("cluster"),
                        ModifierName("cta"),
                        ModifierName("gpu"),
                        ModifierName("sys")
                      ),
                      ModifierGroup(ModifierName("global")),
                      ModifierGroup(ModifierName("shared::cta")),
                      ModifierGroup(ModifierName("bulk_group")),
                      ModifierGroup(ModifierName("b128"))
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName("L2::cache_hint"),
                      ModifierName("cp_mask")
                    ),
                    false
                  )
                )
              )
            )))
        )
    case CpAsyncBulkCommitGroup extends Definition(
          Opcode("cp.async.bulk.commit_group"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 0) Right(Instruction.CpAsyncBulkCommitGroup(mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("cp.async.bulk.commit_group", Vec(0), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("data-movement-and-conversion-instructions-cp-async-bulk-commit-group"),
                NonEmptyString("9.7.9.26.6.1. Data Movement and Conversion Instructions: cp.async.bulk.commit_group"),
                NonEmptyString("cp.async.bulk.commit_group"),
                NonEmptyString("Data Movement and Conversion Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(NonEmptyString("cp.async.bulk.commit_group;"))),
                Version(PositiveInt(8), NonNegativeInt.Zero),
                Target(90),
                Vec(InstructionForm(
                  Opcode("cp.async.bulk.commit_group"),
                  Vec(),
                  Vec(),
                  _root_.scala.collection.immutable.Set(),
                  true
                ))
              )
            )))
        )
    case CpAsyncBulkPrefetch extends Definition(
          Opcode("cp.async.bulk.prefetch"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 2 || operands.size == 3)
              Right(Instruction.CpAsyncBulkPrefetch(operands(0), operands(1), operands.lift(2), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("cp.async.bulk.prefetch", Vec(2, 3), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("data-movement-and-conversion-instructions-cp-async-bulk-prefetch"),
                NonEmptyString("9.7.9.26.4.3. Data Movement and Conversion Instructions: cp.async.bulk.prefetch"),
                NonEmptyString("cp.async.bulk.prefetch"),
                NonEmptyString("Data Movement and Conversion Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(NonEmptyString(
                  "cp.async.bulk.prefetch.L2.src{.level::cache_hint}   [srcMem], size {, cache_policy};\n\n.src =                { .global }\n.level::cache_hint =  { .L2::cache_hint }"
                ))),
                Version(PositiveInt(8), NonNegativeInt.Zero),
                Target(90),
                Vec(InstructionForm(
                  Opcode("cp.async.bulk.prefetch"),
                  Vec(OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value),
                  Vec(
                    ModifierGroup(ModifierName("L2")),
                    ModifierGroup(ModifierName("global"))
                  ),
                  _root_.scala.collection.immutable.Set(ModifierName("L2::cache_hint")),
                  true
                ))
              )
            )))
        )
    case CpAsyncBulkPrefetchTensor extends Definition(
          Opcode("cp.async.bulk.prefetch.tensor"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 1 || operands.size == 2 || operands.size == 3)
              Right(Instruction.CpAsyncBulkPrefetchTensor(operands(0), operands.lift(1), operands.lift(2), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount(
              "cp.async.bulk.prefetch.tensor",
              Vec(1, 2, 3),
              operands.size
            ))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("data-movement-and-conversion-instructions-cp-async-bulk-prefetch-tensor"),
                NonEmptyString(
                  "9.7.9.26.5.4. Data Movement and Conversion Instructions: cp.async.bulk.prefetch.tensor"
                ),
                NonEmptyString("cp.async.bulk.prefetch.tensor"),
                NonEmptyString("Data Movement and Conversion Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(NonEmptyString(
                  "// global -> L2:\ncp.async.bulk.prefetch.tensor.dim.L2.src{.load_mode}{.level::cache_hint} [tensorMap, tensorCoords]\n                                                             {, im2colInfo } {, cache_policy}\n\n.src =                { .global }\n.dim =                { .1d, .2d, .3d, .4d, .5d }\n.load_mode =          { .tile, .tile::gather4, .im2col, .im2col::w, .im2col::w::128 }\n.level::cache_hint =  { .L2::cache_hint }"
                ))),
                Version(PositiveInt(8), NonNegativeInt.Zero),
                Target(90),
                Vec(InstructionForm(
                  Opcode("cp.async.bulk.prefetch.tensor"),
                  Vec(OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value),
                  Vec(
                    ModifierGroup(ModifierName("L2")),
                    ModifierGroup(ModifierName("global"))
                  ),
                  _root_.scala.collection.immutable.Set(
                    ModifierName("L2::cache_hint"),
                    ModifierName("im2col"),
                    ModifierName("im2col::w"),
                    ModifierName("tile"),
                    ModifierName("tile::gather4")
                  ),
                  false
                ))
              )
            )))
        )
    case CpAsyncBulkTensorMBarrier extends Definition(
          Opcode("cp.async.bulk.tensor"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            val modifierNames = mods.map(_.name)
            if (
              !modifierNames.contains("bulk_group") &&
              (if (modifierNames.exists(_.raw.startsWith("im2col")) && modifierNames.contains("multicast::cluster"))
                 operands.size >= 5 && operands.size <= 6
               else if (
                 modifierNames.exists(_.raw.startsWith("im2col")) || modifierNames.contains("multicast::cluster")
               ) operands.size >= 4 && operands.size <= 5
               else operands.size == 3 || operands.size == 4)
            ) Right(if (
              modifierNames.exists(_.raw.startsWith("im2col")) && modifierNames.contains("multicast::cluster")
            ) Instruction.CpAsyncBulkTensorMBarrier(
              operands(0),
              operands(1),
              operands(2),
              Some(operands(3)),
              operands.lift(4),
              operands.lift(5),
              mods,
              pred
            )
            else if (modifierNames.exists(_.raw.startsWith("im2col"))) Instruction.CpAsyncBulkTensorMBarrier(
              operands(0),
              operands(1),
              operands(2),
              Some(operands(3)),
              None,
              operands.lift(4),
              mods,
              pred
            )
            else if (modifierNames.contains("multicast::cluster")) Instruction.CpAsyncBulkTensorMBarrier(
              operands(0),
              operands(1),
              operands(2),
              None,
              operands.lift(3),
              operands.lift(4),
              mods,
              pred
            )
            else Instruction.CpAsyncBulkTensorMBarrier(
              operands(0),
              operands(1),
              operands(2),
              None,
              None,
              operands.lift(3),
              mods,
              pred
            ))
            else if (!modifierNames.contains("bulk_group"))
              Left(Instruction.DecodeError.InvalidOperandCount("cp.async.bulk.tensor", Vec(3, 4, 5, 6), operands.size))
            else Left(Instruction.DecodeError.UnknownOpcode((("cp.async.bulk.tensor") +: modifierNames).mkString(".")))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("data-movement-and-conversion-instructions-cp-async-bulk-tensor"),
                NonEmptyString("9.7.9.26.5.2. Data Movement and Conversion Instructions: cp.async.bulk.tensor"),
                NonEmptyString("cp.async.bulk.tensor"),
                NonEmptyString("Data Movement and Conversion Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(NonEmptyString(
                  "// global -> shared::cta\ncp.async.bulk.tensor.dim.dst.src{.load_mode}.completion_mechanism{.cta_group}{.level::cache_hint}\n                                   [dstMem], [tensorMap, tensorCoords], [mbar]{, im2colInfo} {, cache_policy}\n\n.dst =                  { .shared::cta }\n.src =                  { .global }\n.dim =                  { .1d, .2d, .3d, .4d, .5d }\n.completion_mechanism = { .mbarrier::complete_tx::bytes }\n.cta_group =            { .cta_group::1, .cta_group::2 }\n.load_mode =            { .tile, .tile::gather4, .im2col, .im2col::w, .im2col::w::128 }\n.level::cache_hint =    { .L2::cache_hint }\n\n\n// global -> shared::cluster\ncp.async.bulk.tensor.dim.dst.src{.load_mode}.completion_mechanism{.multicast}{.cta_group}{.level::cache_hint}\n                                   [dstMem], [tensorMap, tensorCoords], [mbar]{, im2colInfo}\n                                   {, ctaMask} {, cache_policy}\n\n.dst =                  { .shared::cluster }\n.src =                  { .global }\n.dim =                  { .1d, .2d, .3d, .4d, .5d }\n.completion_mechanism = { .mbarrier::complete_tx::bytes }\n.cta_group =            { .cta_group::1, .cta_group::2 }\n.load_mode =            { .tile, .tile::gather4, .im2col, .im2col::w, .im2col::w::128 }\n.level::cache_hint =    { .L2::cache_hint }\n.multicast =            { .multicast::cluster  }\n\n\n// shared::cta -> global\ncp.async.bulk.tensor.dim.dst.src{.load_mode}.completion_mechanism{.level::cache_hint}\n                                   [tensorMap, tensorCoords], [srcMem] {, cache_policy}\n\n.dst =                  { .global }\n.src =                  { .shared::cta }\n.dim =                  { .1d, .2d, .3d, .4d, .5d }\n.completion_mechanism = { .bulk_group }\n.load_mode =            { .tile, .tile::scatter4, .im2col_no_offs }\n.level::cache_hint =    { .L2::cache_hint }"
                ))),
                Version(PositiveInt(8), NonNegativeInt.Zero),
                Target(90),
                Vec(
                  InstructionForm(
                    Opcode("cp.async.bulk.tensor"),
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Address,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(ModifierName("global")),
                      ModifierGroup(ModifierName("shared::cta")),
                      ModifierGroup(ModifierName("bulk_group"))
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName("L2::cache_hint"),
                      ModifierName("im2col_no_offs"),
                      ModifierName("tile"),
                      ModifierName("tile::scatter4")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode("cp.async.bulk.tensor"),
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Address,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(ModifierName("global")),
                      ModifierGroup(ModifierName("shared::cta")),
                      ModifierGroup(ModifierName("bulk_group"))
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName("L2::cache_hint"),
                      ModifierName("im2col_no_offs"),
                      ModifierName("multicast::cluster"),
                      ModifierName("tile"),
                      ModifierName("tile::scatter4")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode("cp.async.bulk.tensor"),
                    Vec(OperandConstraint.Address, OperandConstraint.Address, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(ModifierName("global")),
                      ModifierGroup(ModifierName("shared::cta")),
                      ModifierGroup(ModifierName("bulk_group"))
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName("L2::cache_hint"),
                      ModifierName("im2col_no_offs"),
                      ModifierName("tile"),
                      ModifierName("tile::scatter4")
                    ),
                    false
                  )
                )
              )
            )))
        )
    case CpAsyncBulkTensorGroup extends Definition(
          Opcode("cp.async.bulk.tensor"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            val modifierNames = mods.map(_.name)
            if (modifierNames.contains("bulk_group") && (operands.size == 2 || operands.size == 3))
              Right(Instruction.CpAsyncBulkTensorGroup(operands(0), operands(1), operands.lift(2), mods, pred))
            else if (modifierNames.contains("bulk_group"))
              Left(Instruction.DecodeError.InvalidOperandCount("cp.async.bulk.tensor", Vec(2, 3), operands.size))
            else Left(Instruction.DecodeError.UnknownOpcode((("cp.async.bulk.tensor") +: modifierNames).mkString(".")))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("data-movement-and-conversion-instructions-cp-async-bulk-tensor"),
                NonEmptyString("9.7.9.26.5.2. Data Movement and Conversion Instructions: cp.async.bulk.tensor"),
                NonEmptyString("cp.async.bulk.tensor"),
                NonEmptyString("Data Movement and Conversion Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(NonEmptyString(
                  "// global -> shared::cta\ncp.async.bulk.tensor.dim.dst.src{.load_mode}.completion_mechanism{.cta_group}{.level::cache_hint}\n                                   [dstMem], [tensorMap, tensorCoords], [mbar]{, im2colInfo} {, cache_policy}\n\n.dst =                  { .shared::cta }\n.src =                  { .global }\n.dim =                  { .1d, .2d, .3d, .4d, .5d }\n.completion_mechanism = { .mbarrier::complete_tx::bytes }\n.cta_group =            { .cta_group::1, .cta_group::2 }\n.load_mode =            { .tile, .tile::gather4, .im2col, .im2col::w, .im2col::w::128 }\n.level::cache_hint =    { .L2::cache_hint }\n\n\n// global -> shared::cluster\ncp.async.bulk.tensor.dim.dst.src{.load_mode}.completion_mechanism{.multicast}{.cta_group}{.level::cache_hint}\n                                   [dstMem], [tensorMap, tensorCoords], [mbar]{, im2colInfo}\n                                   {, ctaMask} {, cache_policy}\n\n.dst =                  { .shared::cluster }\n.src =                  { .global }\n.dim =                  { .1d, .2d, .3d, .4d, .5d }\n.completion_mechanism = { .mbarrier::complete_tx::bytes }\n.cta_group =            { .cta_group::1, .cta_group::2 }\n.load_mode =            { .tile, .tile::gather4, .im2col, .im2col::w, .im2col::w::128 }\n.level::cache_hint =    { .L2::cache_hint }\n.multicast =            { .multicast::cluster  }\n\n\n// shared::cta -> global\ncp.async.bulk.tensor.dim.dst.src{.load_mode}.completion_mechanism{.level::cache_hint}\n                                   [tensorMap, tensorCoords], [srcMem] {, cache_policy}\n\n.dst =                  { .global }\n.src =                  { .shared::cta }\n.dim =                  { .1d, .2d, .3d, .4d, .5d }\n.completion_mechanism = { .bulk_group }\n.load_mode =            { .tile, .tile::scatter4, .im2col_no_offs }\n.level::cache_hint =    { .L2::cache_hint }"
                ))),
                Version(PositiveInt(8), NonNegativeInt.Zero),
                Target(90),
                Vec(
                  InstructionForm(
                    Opcode("cp.async.bulk.tensor"),
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Address,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(ModifierName("global")),
                      ModifierGroup(ModifierName("shared::cta")),
                      ModifierGroup(ModifierName("bulk_group"))
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName("L2::cache_hint"),
                      ModifierName("im2col_no_offs"),
                      ModifierName("tile"),
                      ModifierName("tile::scatter4")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode("cp.async.bulk.tensor"),
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Address,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(ModifierName("global")),
                      ModifierGroup(ModifierName("shared::cta")),
                      ModifierGroup(ModifierName("bulk_group"))
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName("L2::cache_hint"),
                      ModifierName("im2col_no_offs"),
                      ModifierName("multicast::cluster"),
                      ModifierName("tile"),
                      ModifierName("tile::scatter4")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode("cp.async.bulk.tensor"),
                    Vec(OperandConstraint.Address, OperandConstraint.Address, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(ModifierName("global")),
                      ModifierGroup(ModifierName("shared::cta")),
                      ModifierGroup(ModifierName("bulk_group"))
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName("L2::cache_hint"),
                      ModifierName("im2col_no_offs"),
                      ModifierName("tile"),
                      ModifierName("tile::scatter4")
                    ),
                    false
                  )
                )
              )
            )))
        )
    case CpAsyncBulkWaitGroup extends Definition(
          Opcode("cp.async.bulk.wait_group"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 1) Right(Instruction.CpAsyncBulkWaitGroup(operands(0), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("cp.async.bulk.wait_group", Vec(1), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("data-movement-and-conversion-instructions-cp-async-bulk-wait-group"),
                NonEmptyString("9.7.9.26.6.2. Data Movement and Conversion Instructions: cp.async.bulk.wait_group"),
                NonEmptyString("cp.async.bulk.wait_group"),
                NonEmptyString("Data Movement and Conversion Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(NonEmptyString("cp.async.bulk.wait_group{.read} N;"))),
                Version(PositiveInt(8), NonNegativeInt.Zero),
                Target(90),
                Vec(InstructionForm(
                  Opcode("cp.async.bulk.wait_group"),
                  Vec(OperandConstraint.Value),
                  Vec(),
                  _root_.scala.collection.immutable.Set(ModifierName("read")),
                  true
                ))
              )
            )))
        )
    case CpAsyncCommitGroup extends Definition(
          Opcode("cp.async.commit_group"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 0) Right(Instruction.CpAsyncCommitGroup(mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("cp.async.commit_group", Vec(0), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("data-movement-and-conversion-instructions-cp-async-commit-group"),
                NonEmptyString("9.7.9.26.3.2. Data Movement and Conversion Instructions: cp.async.commit_group"),
                NonEmptyString("cp.async.commit_group"),
                NonEmptyString("Data Movement and Conversion Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(NonEmptyString("cp.async.commit_group ;"))),
                Version(PositiveInt(7), NonNegativeInt.Zero),
                Target(80),
                Vec(InstructionForm(
                  Opcode("cp.async.commit_group"),
                  Vec(),
                  Vec(),
                  _root_.scala.collection.immutable.Set(),
                  true
                ))
              )
            )))
        )
    case CpAsyncMbarrierArrive extends Definition(
          Opcode("cp.async.mbarrier.arrive"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 1) Right(Instruction.CpAsyncMbarrierArrive(operands(0), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("cp.async.mbarrier.arrive", Vec(1), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("parallel-synchronization-and-communication-instructions-cp-async-mbarrier-arrive"),
                NonEmptyString(
                  "9.7.14.16.18. Parallel Synchronization and Communication Instructions: cp.async.mbarrier.arrive"
                ),
                NonEmptyString("cp.async.mbarrier.arrive"),
                NonEmptyString("Parallel Synchronization and Communication Instructions"),
                NonEmptyVec.unsafeFromVec(
                  Vec(NonEmptyString("cp.async.mbarrier.arrive{.noinc}{.shared{::cta}}.b64 [addr];"))
                ),
                Version(PositiveInt(7), NonNegativeInt.Zero),
                Target(80),
                Vec(InstructionForm(
                  Opcode("cp.async.mbarrier.arrive"),
                  Vec(OperandConstraint.Address),
                  Vec(ModifierGroup(ModifierName("b64"))),
                  _root_.scala.collection.immutable.Set(ModifierName("noinc"), ModifierName("shared")),
                  false
                ))
              )
            )))
        )
    case CpAsyncWaitAll extends Definition(
          Opcode("cp.async.wait_all"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 0) Right(Instruction.CpAsyncWaitAll(mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("cp.async.wait_all", Vec(0), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("data-movement-and-conversion-instructions-cp-async-wait-group"),
                NonEmptyString(
                  "9.7.9.26.3.3. Data Movement and Conversion Instructions: cp.async.wait_group / cp.async.wait_all"
                ),
                NonEmptyString("cp.async.wait_group / cp.async.wait_all"),
                NonEmptyString("Data Movement and Conversion Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(NonEmptyString("cp.async.wait_group N;\ncp.async.wait_all ;"))),
                Version(PositiveInt(7), NonNegativeInt.Zero),
                Target(80),
                Vec(InstructionForm(
                  Opcode("cp.async.wait_all"),
                  Vec(),
                  Vec(),
                  _root_.scala.collection.immutable.Set(),
                  true
                ))
              )
            )))
        )
    case CpAsyncWaitGroup extends Definition(
          Opcode("cp.async.wait_group"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 1) Right(Instruction.CpAsyncWaitGroup(operands(0), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("cp.async.wait_group", Vec(1), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("data-movement-and-conversion-instructions-cp-async-wait-group"),
                NonEmptyString(
                  "9.7.9.26.3.3. Data Movement and Conversion Instructions: cp.async.wait_group / cp.async.wait_all"
                ),
                NonEmptyString("cp.async.wait_group / cp.async.wait_all"),
                NonEmptyString("Data Movement and Conversion Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(NonEmptyString("cp.async.wait_group N;\ncp.async.wait_all ;"))),
                Version(PositiveInt(7), NonNegativeInt.Zero),
                Target(80),
                Vec(InstructionForm(
                  Opcode("cp.async.wait_group"),
                  Vec(OperandConstraint.Value),
                  Vec(),
                  _root_.scala.collection.immutable.Set(),
                  true
                ))
              )
            )))
        )
    case CpReduceAsyncBulk extends Definition(
          Opcode("cp.reduce.async.bulk"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 3 || operands.size == 4)
              Right(Instruction.CpReduceAsyncBulk(operands(0), operands(1), operands(2), operands.lift(3), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("cp.reduce.async.bulk", Vec(3, 4), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("data-movement-and-conversion-instructions-cp-reduce-async-bulk"),
                NonEmptyString("9.7.9.26.4.2. Data Movement and Conversion Instructions: cp.reduce.async.bulk"),
                NonEmptyString("cp.reduce.async.bulk"),
                NonEmptyString("Data Movement and Conversion Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(NonEmptyString(
                  "cp.reduce.async.bulk{.sem.scope}.dst.src.completion_mechanism.redOp.type\n              [dstMem], [srcMem], size, [mbar]\n\n.sem =                  { .relaxed }\n.scope =                { .cta, .cluster }\n.dst =                  { .shared::cluster }\n.src =                  { .shared::cta }\n.completion_mechanism = { .mbarrier::complete_tx::bytes }\n.redOp=                 { .and, .or, .xor,\n                          .add, .inc, .dec,\n                          .min, .max }\n.type =                 { .b32, .u32, .s32, .b64, .u64 }\n\n\ncp.reduce.async.bulk{.sem.scope}.dst.src.completion_mechanism{.level::cache_hint}.redOp.type\n               [dstMem], [srcMem], size{, cache_policy}\n\n.sem =                  { .relaxed }\n.scope =                { .cta, .cluster, .gpu, .sys }\n.dst =                  { .global      }\n.src =                  { .shared::cta }\n.completion_mechanism = { .bulk_group }\n.level::cache_hint    = { .L2::cache_hint }\n.redOp=                 { .and, .or, .xor,\n                          .add, .inc, .dec,\n                          .min, .max }\n.type =                 { .f16, .bf16, .b32, .u32, .s32, .b64, .u64, .s64, .f32, .f64 }\n\n\ncp.reduce.async.bulk{.sem.scope}.dst.src.completion_mechanism{.level::cache_hint}.add.noftz.type\n               [dstMem], [srcMem], size{, cache_policy}\n\n.sem =                  { .relaxed }\n.scope =                { .cta, .cluster, .gpu, .sys }\n.dst  =                 { .global }\n.src  =                 { .shared::cta }\n.completion_mechanism = { .bulk_group }\n.type =                 { .f16, .bf16 }"
                ))),
                Version(PositiveInt(8), NonNegativeInt.Zero),
                Target(90),
                Vec(
                  InstructionForm(
                    Opcode("cp.reduce.async.bulk"),
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Address
                    ),
                    Vec(
                      ModifierGroup(ModifierName("global")),
                      ModifierGroup(ModifierName("shared::cta")),
                      ModifierGroup(ModifierName("bulk_group")),
                      ModifierGroup(
                        ModifierName("add"),
                        ModifierName("and"),
                        ModifierName("dec"),
                        ModifierName("inc"),
                        ModifierName("max"),
                        ModifierName("min"),
                        ModifierName("or"),
                        ModifierName("xor")
                      ),
                      ModifierGroup(
                        ModifierName("bf16"),
                        ModifierName("f16")
                      )
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName("cluster"),
                      ModifierName("cta"),
                      ModifierName("gpu"),
                      ModifierName("relaxed"),
                      ModifierName("sys")
                    ),
                    true
                  ),
                  InstructionForm(
                    Opcode("cp.reduce.async.bulk"),
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(ModifierName("global")),
                      ModifierGroup(ModifierName("shared::cta")),
                      ModifierGroup(ModifierName("bulk_group")),
                      ModifierGroup(
                        ModifierName("add"),
                        ModifierName("and"),
                        ModifierName("dec"),
                        ModifierName("inc"),
                        ModifierName("max"),
                        ModifierName("min"),
                        ModifierName("or"),
                        ModifierName("xor")
                      ),
                      ModifierGroup(
                        ModifierName("bf16"),
                        ModifierName("f16")
                      )
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName("L2::cache_hint"),
                      ModifierName("cluster"),
                      ModifierName("cta"),
                      ModifierName("gpu"),
                      ModifierName("relaxed"),
                      ModifierName("sys")
                    ),
                    true
                  ),
                  InstructionForm(
                    Opcode("cp.reduce.async.bulk"),
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(ModifierName("global")),
                      ModifierGroup(ModifierName("shared::cta")),
                      ModifierGroup(ModifierName("bulk_group")),
                      ModifierGroup(ModifierName("add")),
                      ModifierGroup(ModifierName("noftz")),
                      ModifierGroup(
                        ModifierName("bf16"),
                        ModifierName("f16")
                      )
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName("L2::cache_hint"),
                      ModifierName("cluster"),
                      ModifierName("cta"),
                      ModifierName("gpu"),
                      ModifierName("relaxed"),
                      ModifierName("sys")
                    ),
                    true
                  )
                )
              )
            )))
        )
    case CpReduceAsyncBulkTensor extends Definition(
          Opcode("cp.reduce.async.bulk.tensor"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 2 || operands.size == 3)
              Right(Instruction.CpReduceAsyncBulkTensor(operands(0), operands(1), operands.lift(2), mods, pred))
            else
              Left(Instruction.DecodeError.InvalidOperandCount("cp.reduce.async.bulk.tensor", Vec(2, 3), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("data-movement-and-conversion-instructions-cp-reduce-async-bulk-tensor"),
                NonEmptyString("9.7.9.26.5.3. Data Movement and Conversion Instructions: cp.reduce.async.bulk.tensor"),
                NonEmptyString("cp.reduce.async.bulk.tensor"),
                NonEmptyString("Data Movement and Conversion Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(NonEmptyString(
                  "// shared::cta -> global:\ncp.reduce.async.bulk.tensor.dim.dst.src.redOp{.load_mode}.completion_mechanism{.level::cache_hint}\n                                          [tensorMap, tensorCoords], [srcMem] {,cache_policy}\n\n.dst =                  { .global }\n.src =                  { .shared::cta }\n.dim =                  { .1d, .2d, .3d, .4d, .5d }\n.completion_mechanism = { .bulk_group }\n.load_mode =            { .tile, .im2col_no_offs }\n.redOp =                { .add, .min, .max, .inc, .dec, .and, .or, .xor}"
                ))),
                Version(PositiveInt(8), NonNegativeInt.Zero),
                Target(90),
                Vec(InstructionForm(
                  Opcode("cp.reduce.async.bulk.tensor"),
                  Vec(OperandConstraint.Address, OperandConstraint.Address, OperandConstraint.Value),
                  Vec(
                    ModifierGroup(ModifierName("global")),
                    ModifierGroup(ModifierName("shared::cta")),
                    ModifierGroup(
                      ModifierName("add"),
                      ModifierName("and"),
                      ModifierName("dec"),
                      ModifierName("inc"),
                      ModifierName("max"),
                      ModifierName("min"),
                      ModifierName("or"),
                      ModifierName("xor")
                    ),
                    ModifierGroup(ModifierName("bulk_group"))
                  ),
                  _root_.scala.collection.immutable.Set(
                    ModifierName("im2col_no_offs"),
                    ModifierName("level::cache_hint"),
                    ModifierName("tile")
                  ),
                  true
                ))
              )
            )))
        )
    case CreatePolicy extends Definition(
          Opcode("createpolicy"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 1 || operands.size == 2 || operands.size == 4) Right(Instruction.CreatePolicy(
              operands(0),
              operands.lift(1),
              operands.lift(2),
              operands.lift(3),
              mods,
              pred
            ))
            else Left(Instruction.DecodeError.InvalidOperandCount("createpolicy", Vec(1, 2, 4), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("data-movement-and-conversion-instructions-createpolicy"),
                NonEmptyString("9.7.9.19. Data Movement and Conversion Instructions: createpolicy"),
                NonEmptyString("createpolicy"),
                NonEmptyString("Data Movement and Conversion Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(NonEmptyString(
                  "// Range-based policy\ncreatepolicy.range{.global}.level::primary_priority{.level::secondary_priority}.b64\n                                   cache_policy, [a], primary-size, total-size;\n\n// Fraction-based policy\ncreatepolicy.fractional.level::primary_priority{.level::secondary_priority}.b64\n                                   cache_policy{, fraction};\n\n// Converting the access property from CUDA APIs\ncreatepolicy.cvt.L2.b64            cache_policy, access-property;\n\n.level::primary_priority =   { .L2::evict_last, .L2::evict_normal,\n                               .L2::evict_first, .L2::evict_unchanged };\n.level::secondary_priority = { .L2::evict_first, .L2::evict_unchanged };"
                ))),
                Version(PositiveInt(7), NonNegativeInt(4)),
                Target(80),
                Vec(
                  InstructionForm(
                    Opcode("createpolicy"),
                    Vec(
                      OperandConstraint.Value,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(ModifierName("range")),
                      ModifierGroup(
                        ModifierName("L2::evict_first"),
                        ModifierName("L2::evict_last"),
                        ModifierName("L2::evict_normal"),
                        ModifierName("L2::evict_unchanged")
                      ),
                      ModifierGroup(ModifierName("b64"))
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName("L2::evict_first"),
                      ModifierName("L2::evict_unchanged"),
                      ModifierName("global")
                    ),
                    true
                  ),
                  InstructionForm(
                    Opcode("createpolicy"),
                    Vec(OperandConstraint.Value, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(ModifierName("fractional")),
                      ModifierGroup(
                        ModifierName("L2::evict_first"),
                        ModifierName("L2::evict_last"),
                        ModifierName("L2::evict_normal"),
                        ModifierName("L2::evict_unchanged")
                      ),
                      ModifierGroup(ModifierName("b64"))
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName("L2::evict_first"),
                      ModifierName("L2::evict_unchanged")
                    ),
                    true
                  ),
                  InstructionForm(
                    Opcode("createpolicy"),
                    Vec(OperandConstraint.Value, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(ModifierName("cvt")),
                      ModifierGroup(ModifierName("L2")),
                      ModifierGroup(ModifierName("b64"))
                    ),
                    _root_.scala.collection.immutable.Set(),
                    true
                  )
                )
              )
            )))
        )
    case Cvt extends Definition(
          Opcode("cvt"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 2 || operands.size == 3 || operands.size == 4)
              Right(Instruction.Cvt(operands(0), operands(1), operands.lift(2), operands.lift(3), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("cvt", Vec(2, 3, 4), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("data-movement-and-conversion-instructions-cvt"),
                NonEmptyString("9.7.9.22. Data Movement and Conversion Instructions: cvt"),
                NonEmptyString("cvt"),
                NonEmptyString("Data Movement and Conversion Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(NonEmptyString(
                  "cvt{.irnd}{.ftz}{.sat}.dtype.atype         d, a;  // integer rounding\ncvt{.frnd}{.ftz}{.sat}.dtype.atype         d, a;  // fp rounding\n\ncvt.frnd2{.relu}{.satfinite}.f16.f32       d, a;\ncvt.frnd2{.relu}{.satfinite}.f16x2.f32     d, a, b;\ncvt.rs{.relu}{.satfinite}.f16x2.f32        d, a, b, rbits;\n\ncvt.frnd2{.relu}{.satfinite}.bf16.f32      d, a;\ncvt.frnd2{.relu}{.satfinite}.bf16x2.f32    d, a, b;\ncvt.rs{.relu}{.satfinite}.bf16x2.f32       d, a, b, rbits;\n\ncvt.rna{.satfinite}.tf32.f32               d, a;\ncvt.frnd2{.satfinite}{.relu}.tf32.f32      d, a;\n\ncvt.rn.satfinite{.relu}.f8x2type.f32       d, a, b;\ncvt.rn.satfinite{.relu}.f8x2type.fp16x2    d, a;\ncvt.rn{.relu}.f16x2.f8x2type               d, a;\ncvt.rn{.relu}{.satfinite}{.scaled::n2::ue8m0}.bf16x2.f8x2type d, a{, scale-factor};\ncvt.rs{.relu}.satfinite.f8x4type.f32       d, {a, b, e, f}, rbits;\n\ncvt.rn.satfinite{.relu}.f4x2type.f32        d, a, b;\ncvt.rn.satfinite{.relu}.f4x2type.fp16x2type d, a;\ncvt.rn{.relu}.f16x2.f4x2type                d, a;\ncvt.rn{.relu}{.satfinite}{.scaled::n2::ue8m0}.bf16x2.f4x2type d, a{, scale-factor};\ncvt.rs{.relu}.satfinite.f4x4type.f32        d, {a, b, e, f}, rbits;\n\ncvt.rn.satfinite{.relu}.f6x2type.f32        d, a, b;\ncvt.rn.satfinite{.relu}.f6x2type.fp16x2type d, a;\ncvt.rn{.relu}.f16x2.f6x2type                d, a;\ncvt.rn{.relu}{.satfinite}{.scaled::n2::ue8m0}.bf16x2.f6x2type d, a{, scale-factor};\ncvt.rs{.relu}.satfinite.f6x4type.f32        d, {a, b, e, f}, rbits;\n\ncvt.frnd3{.satfinite}.ue8m0x2.f32          d, a, b;\ncvt.frnd3{.satfinite}.ue8m0x2.bf16x2       d, a;\ncvt.rn.bf16x2.ue8m0x2                      d, a;\n\ncvt.rn.satfinite{.relu}{.scaled::n2::ue8m0}.s2f6x2.f32      d, a, b{, scale-factor};\ncvt.rn.satfinite{.relu}{.scaled::n2::ue8m0}.s2f6x2.bf16x2   d, a{, scale-factor};\ncvt.rn{.satfinite}{.relu}{.scaled::n2::ue8m0}.bf16x2.s2f6x2 d, a{, scale-factor};\n\n.irnd   = { .rni, .rzi, .rmi, .rpi };\n.frnd   = { .rn,  .rz,  .rm,  .rp  };\n.frnd2  = { .rn,  .rz };\n.frnd3  = { .rz,  .rp };\n.dtype = .atype = { .u8,   .u16, .u32, .u64,\n                    .s8,   .s16, .s32, .s64,\n                    .bf16, .f16, .f32, .f64 };\n.f8x2type = { .e4m3x2, .e5m2x2 };\n.f4x2type = { .e2m1x2 };\n.f6x2type = { .e2m3x2, .e3m2x2 };\n.f4x4type = { .e2m1x4 };\n.f8x4type = { .e4m3x4, .e5m2x4 };\n.f6x4type = { .e2m3x4, .e3m2x4 };\n.fp16x2type = { .f16x2, .bf16x2 };"
                ))),
                Version(PositiveInt(1), NonNegativeInt.Zero),
                Target(13),
                Vec(
                  InstructionForm(
                    Opcode("cvt"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(ModifierName("dtype")),
                      ModifierGroup(
                        ModifierName("bf16"),
                        ModifierName("f16"),
                        ModifierName("f32"),
                        ModifierName("f64"),
                        ModifierName("s16"),
                        ModifierName("s32"),
                        ModifierName("s64"),
                        ModifierName("s8"),
                        ModifierName("u16"),
                        ModifierName("u32"),
                        ModifierName("u64"),
                        ModifierName("u8")
                      )
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName("ftz"),
                      ModifierName("rmi"),
                      ModifierName("rni"),
                      ModifierName("rpi"),
                      ModifierName("rzi"),
                      ModifierName("sat")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode("cvt"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(ModifierName("dtype")),
                      ModifierGroup(
                        ModifierName("bf16"),
                        ModifierName("f16"),
                        ModifierName("f32"),
                        ModifierName("f64"),
                        ModifierName("s16"),
                        ModifierName("s32"),
                        ModifierName("s64"),
                        ModifierName("s8"),
                        ModifierName("u16"),
                        ModifierName("u32"),
                        ModifierName("u64"),
                        ModifierName("u8")
                      )
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName("ftz"),
                      ModifierName("rm"),
                      ModifierName("rn"),
                      ModifierName("rp"),
                      ModifierName("rz"),
                      ModifierName("sat")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode("cvt"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(
                        ModifierName("rn"),
                        ModifierName("rz")
                      ),
                      ModifierGroup(ModifierName("f16")),
                      ModifierGroup(ModifierName("f32"))
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName("relu"),
                      ModifierName("satfinite")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode("cvt"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(
                        ModifierName("rn"),
                        ModifierName("rz")
                      ),
                      ModifierGroup(ModifierName("f16x2")),
                      ModifierGroup(ModifierName("f32"))
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName("relu"),
                      ModifierName("satfinite")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode("cvt"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(ModifierName("rs")),
                      ModifierGroup(ModifierName("f16x2")),
                      ModifierGroup(ModifierName("f32"))
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName("relu"),
                      ModifierName("satfinite")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode("cvt"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(
                        ModifierName("rn"),
                        ModifierName("rz")
                      ),
                      ModifierGroup(ModifierName("bf16")),
                      ModifierGroup(ModifierName("f32"))
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName("relu"),
                      ModifierName("satfinite")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode("cvt"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(
                        ModifierName("rn"),
                        ModifierName("rz")
                      ),
                      ModifierGroup(ModifierName("bf16x2")),
                      ModifierGroup(ModifierName("f32"))
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName("relu"),
                      ModifierName("satfinite")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode("cvt"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(ModifierName("rs")),
                      ModifierGroup(ModifierName("bf16x2")),
                      ModifierGroup(ModifierName("f32"))
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName("relu"),
                      ModifierName("satfinite")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode("cvt"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(ModifierName("rna")),
                      ModifierGroup(ModifierName("tf32")),
                      ModifierGroup(ModifierName("f32"))
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName("satfinite")),
                    false
                  ),
                  InstructionForm(
                    Opcode("cvt"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(
                        ModifierName("rn"),
                        ModifierName("rz")
                      ),
                      ModifierGroup(ModifierName("tf32")),
                      ModifierGroup(ModifierName("f32"))
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName("relu"),
                      ModifierName("satfinite")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode("cvt"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(ModifierName("rn")),
                      ModifierGroup(ModifierName("satfinite")),
                      ModifierGroup(
                        ModifierName("e4m3x2"),
                        ModifierName("e5m2x2")
                      ),
                      ModifierGroup(ModifierName("f32"))
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName("relu")),
                    false
                  ),
                  InstructionForm(
                    Opcode("cvt"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(ModifierName("rn")),
                      ModifierGroup(ModifierName("satfinite")),
                      ModifierGroup(
                        ModifierName("e4m3x2"),
                        ModifierName("e5m2x2")
                      ),
                      ModifierGroup(ModifierName("fp16x2"))
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName("relu")),
                    false
                  ),
                  InstructionForm(
                    Opcode("cvt"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(ModifierName("rn")),
                      ModifierGroup(ModifierName("f16x2")),
                      ModifierGroup(
                        ModifierName("e4m3x2"),
                        ModifierName("e5m2x2")
                      )
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName("relu")),
                    false
                  ),
                  InstructionForm(
                    Opcode("cvt"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(ModifierName("rn")),
                      ModifierGroup(ModifierName("bf16x2")),
                      ModifierGroup(
                        ModifierName("e4m3x2"),
                        ModifierName("e5m2x2")
                      )
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName("relu"),
                      ModifierName("satfinite"),
                      ModifierName("scaled::n2::ue8m0")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode("cvt"),
                    Vec(OperandConstraint.Register, OperandConstraint.VectorOperand, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(ModifierName("rs")),
                      ModifierGroup(ModifierName("satfinite")),
                      ModifierGroup(
                        ModifierName("e4m3x4"),
                        ModifierName("e5m2x4")
                      ),
                      ModifierGroup(ModifierName("f32"))
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName("relu")),
                    false
                  ),
                  InstructionForm(
                    Opcode("cvt"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(ModifierName("rn")),
                      ModifierGroup(ModifierName("satfinite")),
                      ModifierGroup(ModifierName("e2m1x2")),
                      ModifierGroup(ModifierName("f32"))
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName("relu")),
                    false
                  ),
                  InstructionForm(
                    Opcode("cvt"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(ModifierName("rn")),
                      ModifierGroup(ModifierName("satfinite")),
                      ModifierGroup(ModifierName("e2m1x2")),
                      ModifierGroup(
                        ModifierName("bf16x2"),
                        ModifierName("f16x2")
                      )
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName("relu")),
                    false
                  ),
                  InstructionForm(
                    Opcode("cvt"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(ModifierName("rn")),
                      ModifierGroup(ModifierName("f16x2")),
                      ModifierGroup(ModifierName("e2m1x2"))
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName("relu")),
                    false
                  ),
                  InstructionForm(
                    Opcode("cvt"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(ModifierName("rn")),
                      ModifierGroup(ModifierName("bf16x2")),
                      ModifierGroup(ModifierName("e2m1x2"))
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName("relu"),
                      ModifierName("satfinite"),
                      ModifierName("scaled::n2::ue8m0")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode("cvt"),
                    Vec(OperandConstraint.Register, OperandConstraint.VectorOperand, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(ModifierName("rs")),
                      ModifierGroup(ModifierName("satfinite")),
                      ModifierGroup(ModifierName("e2m1x4")),
                      ModifierGroup(ModifierName("f32"))
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName("relu")),
                    false
                  ),
                  InstructionForm(
                    Opcode("cvt"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(ModifierName("rn")),
                      ModifierGroup(ModifierName("satfinite")),
                      ModifierGroup(
                        ModifierName("e2m3x2"),
                        ModifierName("e3m2x2")
                      ),
                      ModifierGroup(ModifierName("f32"))
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName("relu")),
                    false
                  ),
                  InstructionForm(
                    Opcode("cvt"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(ModifierName("rn")),
                      ModifierGroup(ModifierName("satfinite")),
                      ModifierGroup(
                        ModifierName("e2m3x2"),
                        ModifierName("e3m2x2")
                      ),
                      ModifierGroup(
                        ModifierName("bf16x2"),
                        ModifierName("f16x2")
                      )
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName("relu")),
                    false
                  ),
                  InstructionForm(
                    Opcode("cvt"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(ModifierName("rn")),
                      ModifierGroup(ModifierName("f16x2")),
                      ModifierGroup(
                        ModifierName("e2m3x2"),
                        ModifierName("e3m2x2")
                      )
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName("relu")),
                    false
                  ),
                  InstructionForm(
                    Opcode("cvt"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(ModifierName("rn")),
                      ModifierGroup(ModifierName("bf16x2")),
                      ModifierGroup(
                        ModifierName("e2m3x2"),
                        ModifierName("e3m2x2")
                      )
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName("relu"),
                      ModifierName("satfinite"),
                      ModifierName("scaled::n2::ue8m0")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode("cvt"),
                    Vec(OperandConstraint.Register, OperandConstraint.VectorOperand, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(ModifierName("rs")),
                      ModifierGroup(ModifierName("satfinite")),
                      ModifierGroup(
                        ModifierName("e2m3x4"),
                        ModifierName("e3m2x4")
                      ),
                      ModifierGroup(ModifierName("f32"))
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName("relu")),
                    false
                  ),
                  InstructionForm(
                    Opcode("cvt"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(
                        ModifierName("rp"),
                        ModifierName("rz")
                      ),
                      ModifierGroup(ModifierName("ue8m0x2")),
                      ModifierGroup(ModifierName("f32"))
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName("satfinite")),
                    false
                  ),
                  InstructionForm(
                    Opcode("cvt"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(
                        ModifierName("rp"),
                        ModifierName("rz")
                      ),
                      ModifierGroup(ModifierName("ue8m0x2")),
                      ModifierGroup(ModifierName("bf16x2"))
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName("satfinite")),
                    false
                  ),
                  InstructionForm(
                    Opcode("cvt"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(ModifierName("rn")),
                      ModifierGroup(ModifierName("bf16x2")),
                      ModifierGroup(ModifierName("ue8m0x2"))
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Opcode("cvt"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(ModifierName("rn")),
                      ModifierGroup(ModifierName("satfinite")),
                      ModifierGroup(ModifierName("s2f6x2")),
                      ModifierGroup(ModifierName("f32"))
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName("relu"),
                      ModifierName("scaled::n2::ue8m0")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode("cvt"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(ModifierName("rn")),
                      ModifierGroup(ModifierName("satfinite")),
                      ModifierGroup(ModifierName("s2f6x2")),
                      ModifierGroup(ModifierName("bf16x2"))
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName("relu"),
                      ModifierName("scaled::n2::ue8m0")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode("cvt"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(ModifierName("rn")),
                      ModifierGroup(ModifierName("bf16x2")),
                      ModifierGroup(ModifierName("s2f6x2"))
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName("relu"),
                      ModifierName("satfinite"),
                      ModifierName("scaled::n2::ue8m0")
                    ),
                    false
                  )
                )
              )
            )))
        )
    case CvtPack extends Definition(
          Opcode("cvt.pack"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 3 || operands.size == 4)
              Right(Instruction.CvtPack(operands(0), operands(1), operands(2), operands.lift(3), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("cvt.pack", Vec(3, 4), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("data-movement-and-conversion-instructions-cvt-pack"),
                NonEmptyString("9.7.9.23. Data Movement and Conversion Instructions: cvt.pack"),
                NonEmptyString("cvt.pack"),
                NonEmptyString("Data Movement and Conversion Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(NonEmptyString(
                  "cvt.pack.sat.convertType.abType  d, a, b;\n    .convertType  = { .u16, .s16 }\n    .abType       = { .s32 }\n\ncvt.pack.sat.convertType.abType.cType  d, a, b, c;\n    .convertType  = { .u2, .s2, .u4, .s4, .u8, .s8 }\n    .abType       = { .s32 }\n    .cType        = { .b32 }"
                ))),
                Version(PositiveInt(6), NonNegativeInt(5)),
                Target(72),
                Vec(
                  InstructionForm(
                    Opcode("cvt.pack"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(ModifierName("sat")),
                      ModifierGroup(
                        ModifierName("s2"),
                        ModifierName("s4"),
                        ModifierName("s8"),
                        ModifierName("u2"),
                        ModifierName("u4"),
                        ModifierName("u8")
                      ),
                      ModifierGroup(ModifierName("s32"))
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Opcode("cvt.pack"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(ModifierName("sat")),
                      ModifierGroup(
                        ModifierName("s2"),
                        ModifierName("s4"),
                        ModifierName("s8"),
                        ModifierName("u2"),
                        ModifierName("u4"),
                        ModifierName("u8")
                      ),
                      ModifierGroup(ModifierName("s32")),
                      ModifierGroup(ModifierName("b32"))
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  )
                )
              )
            )))
        )
    case Cvta extends Definition(
          Opcode("cvta"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 2) Right(Instruction.Cvta(operands(0), operands(1), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("cvta", Vec(2), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("data-movement-and-conversion-instructions-cvta"),
                NonEmptyString("9.7.9.21. Data Movement and Conversion Instructions: cvta"),
                NonEmptyString("cvta"),
                NonEmptyString("Data Movement and Conversion Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(NonEmptyString(
                  "// convert const, global, local, or shared address to generic address\ncvta.space.size  p, a;        // source address in register a\ncvta.space.size  p, var;      // get generic address of var\ncvta.space.size  p, var+imm;  // generic address of var+offset\n\n// convert generic address to const, global, local, or shared address\ncvta.to.space.size  p, a;\n\n.space = { .const, .global, .local, .shared{::cta, ::cluster}, .param{::entry} };\n.size  = { .u32, .u64 };"
                ))),
                Version(PositiveInt(2), NonNegativeInt.Zero),
                Target(20),
                Vec(
                  InstructionForm(
                    Opcode("cvta"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(
                        ModifierName("const"),
                        ModifierName("global"),
                        ModifierName("local")
                      ),
                      ModifierGroup(
                        ModifierName("u32"),
                        ModifierName("u64")
                      )
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Opcode("cvta"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(
                        ModifierName("const"),
                        ModifierName("global"),
                        ModifierName("local")
                      ),
                      ModifierGroup(
                        ModifierName("u32"),
                        ModifierName("u64")
                      )
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Opcode("cvta"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(
                        ModifierName("const"),
                        ModifierName("global"),
                        ModifierName("local")
                      ),
                      ModifierGroup(
                        ModifierName("u32"),
                        ModifierName("u64")
                      )
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Opcode("cvta"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(ModifierName("to")),
                      ModifierGroup(
                        ModifierName("const"),
                        ModifierName("global"),
                        ModifierName("local")
                      ),
                      ModifierGroup(
                        ModifierName("u32"),
                        ModifierName("u64")
                      )
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  )
                )
              )
            )))
        )
    case Discard extends Definition(
          Opcode("discard"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 2) Right(Instruction.Discard(operands(0), operands(1), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("discard", Vec(2), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("data-movement-and-conversion-instructions-discard"),
                NonEmptyString("9.7.9.18. Data Movement and Conversion Instructions: discard"),
                NonEmptyString("discard"),
                NonEmptyString("Data Movement and Conversion Instructions"),
                NonEmptyVec.unsafeFromVec(
                  Vec(NonEmptyString("discard{.global}.level  [a], size;\n\n.level = { .L2 };"))
                ),
                Version(PositiveInt(7), NonNegativeInt(4)),
                Target(80),
                Vec(InstructionForm(
                  Opcode("discard"),
                  Vec(OperandConstraint.Address, OperandConstraint.Value),
                  Vec(ModifierGroup(ModifierName("L2"))),
                  _root_.scala.collection.immutable.Set(ModifierName("global")),
                  true
                ))
              )
            )))
        )
    case Div extends Definition(
          Opcode("div"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 3) Right(Instruction.Div(operands(0), operands(1), operands(2), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("div", Vec(3), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("integer-arithmetic-instructions-div"),
                NonEmptyString("9.7.1.9. Integer Arithmetic Instructions: div"),
                NonEmptyString("div"),
                NonEmptyString("Integer Arithmetic Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(
                  NonEmptyString("div.type  d, a, b;\n\n.type = { .u16, .u32, .u64,\n          .s16, .s32, .s64 };")
                )),
                Version(PositiveInt(1), NonNegativeInt.Zero),
                Target(10),
                Vec(InstructionForm(
                  Opcode("div"),
                  Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                  Vec(ModifierGroup(
                    ModifierName("s16"),
                    ModifierName("s32"),
                    ModifierName("s64"),
                    ModifierName("u16"),
                    ModifierName("u32"),
                    ModifierName("u64")
                  )),
                  _root_.scala.collection.immutable.Set(),
                  true
                ))
              ),
              Metadata.Entry(
                NonEmptyString("floating-point-instructions-div"),
                NonEmptyString("9.7.3.8. Floating Point Instructions: div"),
                NonEmptyString("div"),
                NonEmptyString("Floating Point Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(NonEmptyString(
                  "div.approx{.ftz}.f32  d, a, b;  // fast, approximate divide\ndiv.full{.ftz}.f32    d, a, b;  // full-range approximate divide\ndiv.rnd{.ftz}.f32     d, a, b;  // IEEE 754 compliant rounding\ndiv.rnd.f64           d, a, b;  // IEEE 754 compliant rounding\n\n.rnd = { .rn, .rz, .rm, .rp };"
                ))),
                Version(PositiveInt(1), NonNegativeInt.Zero),
                Target(13),
                Vec(
                  InstructionForm(
                    Opcode("div"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(ModifierName("approx")),
                      ModifierGroup(ModifierName("f32"))
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName("ftz")),
                    false
                  ),
                  InstructionForm(
                    Opcode("div"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(ModifierName("full")),
                      ModifierGroup(ModifierName("f32"))
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName("ftz")),
                    false
                  ),
                  InstructionForm(
                    Opcode("div"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(
                        ModifierName("rm"),
                        ModifierName("rn"),
                        ModifierName("rp"),
                        ModifierName("rz")
                      ),
                      ModifierGroup(ModifierName("f32"))
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName("ftz")),
                    false
                  ),
                  InstructionForm(
                    Opcode("div"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(
                        ModifierName("rm"),
                        ModifierName("rn"),
                        ModifierName("rp"),
                        ModifierName("rz")
                      ),
                      ModifierGroup(ModifierName("f64"))
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  )
                )
              )
            )))
        )
    case Dp2a extends Definition(
          Opcode("dp2a"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 4)
              Right(Instruction.Dp2a(operands(0), operands(1), operands(2), operands(3), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("dp2a", Vec(4), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("integer-arithmetic-instructions-dp2a"),
                NonEmptyString("9.7.1.25. Integer Arithmetic Instructions: dp2a"),
                NonEmptyString("dp2a"),
                NonEmptyString("Integer Arithmetic Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(NonEmptyString(
                  "dp2a.mode.atype.btype  d, a, b, c;\n\n.atype = .btype = { .u32, .s32 };\n.mode = { .lo, .hi };"
                ))),
                Version(PositiveInt(5), NonNegativeInt.Zero),
                Target(61),
                Vec(InstructionForm(
                  Opcode("dp2a"),
                  Vec(
                    OperandConstraint.Register,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Vec(
                    ModifierGroup(
                      ModifierName("hi"),
                      ModifierName("lo")
                    ),
                    ModifierGroup(ModifierName("atype")),
                    ModifierGroup(
                      ModifierName("s32"),
                      ModifierName("u32")
                    )
                  ),
                  _root_.scala.collection.immutable.Set(),
                  true
                ))
              )
            )))
        )
    case Dp4a extends Definition(
          Opcode("dp4a"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 4)
              Right(Instruction.Dp4a(operands(0), operands(1), operands(2), operands(3), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("dp4a", Vec(4), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("integer-arithmetic-instructions-dp4a"),
                NonEmptyString("9.7.1.24. Integer Arithmetic Instructions: dp4a"),
                NonEmptyString("dp4a"),
                NonEmptyString("Integer Arithmetic Instructions"),
                NonEmptyVec.unsafeFromVec(
                  Vec(NonEmptyString("dp4a.atype.btype  d, a, b, c;\n\n.atype = .btype = { .u32, .s32 };"))
                ),
                Version(PositiveInt(5), NonNegativeInt.Zero),
                Target(61),
                Vec(InstructionForm(
                  Opcode("dp4a"),
                  Vec(
                    OperandConstraint.Register,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Vec(
                    ModifierGroup(ModifierName("atype")),
                    ModifierGroup(
                      ModifierName("s32"),
                      ModifierName("u32")
                    )
                  ),
                  _root_.scala.collection.immutable.Set(),
                  true
                ))
              )
            )))
        )
    case ElectSync extends Definition(
          Opcode("elect.sync"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 2) Right(Instruction.ElectSync(operands(0), operands(1), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("elect.sync", Vec(2), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("parallel-synchronization-and-communication-instructions-elect-sync"),
                NonEmptyString("9.7.14.15. Parallel Synchronization and Communication Instructions: elect.sync"),
                NonEmptyString("elect.sync"),
                NonEmptyString("Parallel Synchronization and Communication Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(NonEmptyString("elect.sync d|p, membermask;"))),
                Version(PositiveInt(8), NonNegativeInt.Zero),
                Target(90),
                Vec(InstructionForm(
                  Opcode("elect.sync"),
                  Vec(OperandConstraint.Register, OperandConstraint.Value),
                  Vec(),
                  _root_.scala.collection.immutable.Set(),
                  true
                ))
              )
            )))
        )
    case Ex2 extends Definition(
          Opcode("ex2"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 2) Right(Instruction.Ex2(operands(0), operands(1), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("ex2", Vec(2), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("floating-point-instructions-ex2"),
                NonEmptyString("9.7.3.21. Floating Point Instructions: ex2"),
                NonEmptyString("ex2"),
                NonEmptyString("Floating Point Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(NonEmptyString("ex2.approx{.ftz}.f32  d, a;"))),
                Version(PositiveInt(1), NonNegativeInt.Zero),
                Target(10),
                Vec(InstructionForm(
                  Opcode("ex2"),
                  Vec(OperandConstraint.Register, OperandConstraint.Value),
                  Vec(
                    ModifierGroup(ModifierName("approx")),
                    ModifierGroup(ModifierName("f32"))
                  ),
                  _root_.scala.collection.immutable.Set(ModifierName("ftz")),
                  true
                ))
              ),
              Metadata.Entry(
                NonEmptyString("half-precision-floating-point-instructions-ex2"),
                NonEmptyString("9.7.4.10. Half Precision Floating Point Instructions: ex2"),
                NonEmptyString("ex2"),
                NonEmptyString("Half Precision Floating Point Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(NonEmptyString(
                  "ex2.approx.atype     d, a;\nex2.approx.ftz.btype d, a;\n\n.atype = { .f16,  .f16x2}\n.btype = { .bf16, .bf16x2}"
                ))),
                Version(PositiveInt(7), NonNegativeInt.Zero),
                Target(75),
                Vec(
                  InstructionForm(
                    Opcode("ex2"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(ModifierName("approx")),
                      ModifierGroup(
                        ModifierName("f16"),
                        ModifierName("f16x2")
                      )
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Opcode("ex2"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(ModifierName("approx")),
                      ModifierGroup(ModifierName("ftz")),
                      ModifierGroup(
                        ModifierName("bf16"),
                        ModifierName("bf16x2")
                      )
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  )
                )
              )
            )))
        )
    case Exit extends Definition(
          Opcode("exit"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 0) Right(Instruction.Exit(mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("exit", Vec(0), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("control-flow-instructions-exit"),
                NonEmptyString("9.7.13.7. Control Flow Instructions: exit"),
                NonEmptyString("exit"),
                NonEmptyString("Control Flow Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(NonEmptyString("exit;"))),
                Version(PositiveInt(1), NonNegativeInt.Zero),
                Target(10),
                Vec(InstructionForm(Opcode("exit"), Vec(), Vec(), _root_.scala.collection.immutable.Set(), true))
              )
            )))
        )
    case FabricSubmit extends Definition(
          Opcode("fabric.submit"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 0) Right(Instruction.FabricSubmit(mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("fabric.submit", Vec(0), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("fabric-instructions-submit"),
                NonEmptyString("9.7.10.5.5. Fabric Instructions: fabric.submit"),
                NonEmptyString("fabric.submit"),
                NonEmptyString("Fabric Instructions"),
                NonEmptyVec.unsafeFromVec(
                  Vec(NonEmptyString("fabric.submit{.submitop};\n\n.submitop = { .op_restrict::fetching }"))
                ),
                Version(PositiveInt(9), NonNegativeInt(3)),
                Target(100),
                Vec(InstructionForm(
                  Opcode("fabric.submit"),
                  Vec(),
                  Vec(),
                  _root_.scala.collection.immutable.Set(ModifierName("op_restrict::fetching")),
                  true
                ))
              )
            )))
        )
    case FabricTryGet extends Definition(
          Opcode("fabric.try_get"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 4)
              Right(Instruction.FabricTryGet(operands(0), operands(1), operands(2), operands(3), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("fabric.try_get", Vec(4), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("fabric-instructions-try-get"),
                NonEmptyString("9.7.10.5.1. Fabric Instructions: fabric.try_get"),
                NonEmptyString("fabric.try_get"),
                NonEmptyString("Fabric Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(NonEmptyString(
                  "fabric.try_get.async.dst.completion_mechanism.sem.scope.b128 [dst], [srcLeId, srcDataOff], size, [bar];\n\n.dst = { .shared::cta }\n.completion_mechanism = { .mbarrier::complete_tx::bytes.mbarrier::report::fabric }\n.sem = { .relaxed }\n.scope = { .sys }"
                ))),
                Version(PositiveInt(9), NonNegativeInt(3)),
                Target(100),
                Vec(InstructionForm(
                  Opcode("fabric.try_get"),
                  Vec(
                    OperandConstraint.Address,
                    OperandConstraint.Address,
                    OperandConstraint.Value,
                    OperandConstraint.Address
                  ),
                  Vec(
                    ModifierGroup(ModifierName("async")),
                    ModifierGroup(ModifierName("shared::cta")),
                    ModifierGroup(ModifierName("relaxed")),
                    ModifierGroup(ModifierName("sys")),
                    ModifierGroup(ModifierName("b128"))
                  ),
                  _root_.scala.collection.immutable.Set(),
                  true
                ))
              )
            )))
        )
    case FabricTryPullred extends Definition(
          Opcode("fabric.try_pullred"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 5) Right(Instruction.FabricTryPullred(
              operands(0),
              operands(1),
              operands(2),
              operands(3),
              operands(4),
              mods,
              pred
            ))
            else Left(Instruction.DecodeError.InvalidOperandCount("fabric.try_pullred", Vec(5), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("fabric-instructions-try-pullred"),
                NonEmptyString("9.7.10.5.4. Fabric Instructions: fabric.try_pullred"),
                NonEmptyString("fabric.try_pullred"),
                NonEmptyString("Fabric Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(NonEmptyString(
                  "fabric.try_pullred.async.multimem.dst.completion_mechanism.sem.scope.redOpBit.typeBit.sync [dst], [srcLeId, srcDataOff], size, [bar], imm-membermask;\n\nfabric.try_pullred.async.multimem.dst.completion_mechanism.sem.scope.redOpMinMax.typeMinMax.sync [dst], [srcLeId, srcDataOff], size, [bar], imm-membermask;\n\nfabric.try_pullred.async.multimem.dst.completion_mechanism.sem.scope.redOpAdd.typeAdd.sync [dst], [srcLeId, srcDataOff], size, [bar], imm-membermask;\n\nfabric.try_pullred.async.multimem.dst.completion_mechanism.sem.scope.redOpAddAcc16.typeAddAcc16.sync [dst], [srcLeId, srcDataOff], size, [bar], imm-membermask;\n\nfabric.try_pullred.async.multimem.dst.completion_mechanism.sem.scope.redOpAddAcc32.typeAddAcc32.sync [dst], [srcLeId, srcDataOff], size, [bar], imm-membermask;\n\n.dst = { .shared::cta }\n.completion_mechanism = { .mbarrier::complete_tx::bytes.mbarrier::report::fabric }\n.sem = { .relaxed }\n.scope = { .sys }\n.redOpBit = { .and, .xor, .or }\n.typeBit = { .b32, .b64 }\n.redOpMinMax = { .min, .max }\n.typeMinMax = { .u32, .s32, .u64, .s64, .bf16, .f16, .e4m3, .e5m2 }\n.redOpAdd = { .add }\n.typeAdd = { .u32, .u64, .f16, .bf16, .f32 }\n.redOpAddAcc16 = { .add.acc::f16 }\n.typeAddAcc16 = { .e4m3, .e5m2 }\n.redOpAddAcc32 = { .add.acc::f32 }\n.typeAddAcc32 = { .f16, .bf16 }"
                ))),
                Version(PositiveInt(9), NonNegativeInt(3)),
                Target(100),
                Vec(
                  InstructionForm(
                    Opcode("fabric.try_pullred"),
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Address,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(ModifierName("async")),
                      ModifierGroup(ModifierName("multimem")),
                      ModifierGroup(ModifierName("shared::cta")),
                      ModifierGroup(ModifierName("relaxed")),
                      ModifierGroup(ModifierName("sys")),
                      ModifierGroup(
                        ModifierName("and"),
                        ModifierName("or"),
                        ModifierName("xor")
                      ),
                      ModifierGroup(
                        ModifierName("b32"),
                        ModifierName("b64")
                      ),
                      ModifierGroup(ModifierName("sync"))
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Opcode("fabric.try_pullred"),
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Address,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(ModifierName("async")),
                      ModifierGroup(ModifierName("multimem")),
                      ModifierGroup(ModifierName("shared::cta")),
                      ModifierGroup(ModifierName("relaxed")),
                      ModifierGroup(ModifierName("sys")),
                      ModifierGroup(
                        ModifierName("max"),
                        ModifierName("min")
                      ),
                      ModifierGroup(
                        ModifierName("bf16"),
                        ModifierName("e4m3"),
                        ModifierName("e5m2"),
                        ModifierName("f16"),
                        ModifierName("s32"),
                        ModifierName("s64"),
                        ModifierName("u32"),
                        ModifierName("u64")
                      ),
                      ModifierGroup(ModifierName("sync"))
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Opcode("fabric.try_pullred"),
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Address,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(ModifierName("async")),
                      ModifierGroup(ModifierName("multimem")),
                      ModifierGroup(ModifierName("shared::cta")),
                      ModifierGroup(ModifierName("relaxed")),
                      ModifierGroup(ModifierName("sys")),
                      ModifierGroup(ModifierName("add")),
                      ModifierGroup(
                        ModifierName("bf16"),
                        ModifierName("f16"),
                        ModifierName("f32"),
                        ModifierName("u32"),
                        ModifierName("u64")
                      ),
                      ModifierGroup(ModifierName("sync"))
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Opcode("fabric.try_pullred"),
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Address,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(ModifierName("async")),
                      ModifierGroup(ModifierName("multimem")),
                      ModifierGroup(ModifierName("shared::cta")),
                      ModifierGroup(ModifierName("relaxed")),
                      ModifierGroup(ModifierName("sys")),
                      ModifierGroup(
                        ModifierName("e4m3"),
                        ModifierName("e5m2")
                      ),
                      ModifierGroup(ModifierName("sync"))
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Opcode("fabric.try_pullred"),
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Address,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(ModifierName("async")),
                      ModifierGroup(ModifierName("multimem")),
                      ModifierGroup(ModifierName("shared::cta")),
                      ModifierGroup(ModifierName("relaxed")),
                      ModifierGroup(ModifierName("sys")),
                      ModifierGroup(
                        ModifierName("bf16"),
                        ModifierName("f16")
                      ),
                      ModifierGroup(ModifierName("sync"))
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  )
                )
              )
            )))
        )
    case FabricTryPut extends Definition(
          Opcode("fabric.try_put"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 4 || operands.size == 5) Right(Instruction.FabricTryPut(
              operands(0),
              operands(1),
              operands(2),
              operands(3),
              operands.lift(4),
              mods,
              pred
            ))
            else Left(Instruction.DecodeError.InvalidOperandCount("fabric.try_put", Vec(4, 5), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("fabric-instructions-try-put"),
                NonEmptyString("9.7.10.5.2. Fabric Instructions: fabric.try_put"),
                NonEmptyString("fabric.try_put"),
                NonEmptyString("Fabric Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(NonEmptyString(
                  "fabric.try_put.async{.multimem}.src.completion_mechanism0.sem.scope.b128 [dstLeId, dstDataOff], [src], size, [bar];\n\nfabric.try_put.async{.multimem}.src.completion_mechanism0.cp_mask.sem.scope.b128 [dstLeId, dstDataOff], [src], size, [bar], bytemask;\n\nfabric.try_put.async{.multimem}.src.completion_mechanism1.sem.scope.b128 [dstLeId, dstDataOff, dstCounterOff], [src], size, [bar];\n\n.src = { .shared::cta }\n.completion_mechanism0 = { .mbarrier::complete_tx::16B.mbarrier::report::fabric }\n.completion_mechanism1 = { .mbarrier::complete_tx::16B.mbarrier::report::fabric.counted::bytes }\n.sem = { .relaxed }\n.scope = { .sys }"
                ))),
                Version(PositiveInt(9), NonNegativeInt(3)),
                Target(100),
                Vec(
                  InstructionForm(
                    Opcode("fabric.try_put"),
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Address
                    ),
                    Vec(
                      ModifierGroup(ModifierName("async")),
                      ModifierGroup(ModifierName("shared::cta")),
                      ModifierGroup(ModifierName("relaxed")),
                      ModifierGroup(ModifierName("sys")),
                      ModifierGroup(ModifierName("b128"))
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName("multimem")),
                    true
                  ),
                  InstructionForm(
                    Opcode("fabric.try_put"),
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Address,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(ModifierName("async")),
                      ModifierGroup(ModifierName("shared::cta")),
                      ModifierGroup(ModifierName("cp_mask")),
                      ModifierGroup(ModifierName("relaxed")),
                      ModifierGroup(ModifierName("sys")),
                      ModifierGroup(ModifierName("b128"))
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName("multimem")),
                    true
                  ),
                  InstructionForm(
                    Opcode("fabric.try_put"),
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Address
                    ),
                    Vec(
                      ModifierGroup(ModifierName("async")),
                      ModifierGroup(ModifierName("shared::cta")),
                      ModifierGroup(ModifierName("relaxed")),
                      ModifierGroup(ModifierName("sys")),
                      ModifierGroup(ModifierName("b128"))
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName("multimem")),
                    true
                  )
                )
              )
            )))
        )
    case FabricTryRed extends Definition(
          Opcode("fabric.try_red"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 4)
              Right(Instruction.FabricTryRed(operands(0), operands(1), operands(2), operands(3), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("fabric.try_red", Vec(4), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("fabric-instructions-try-red"),
                NonEmptyString("9.7.10.5.3. Fabric Instructions: fabric.try_red"),
                NonEmptyString("fabric.try_red"),
                NonEmptyString("Fabric Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(NonEmptyString(
                  "fabric.try_red.async{.multimem}.src.completion_mechanism0.sem.scope.redOpBit.typeBit [dstLeId, dstDataOff], [src], size, [bar];\n\nfabric.try_red.async{.multimem}.src.completion_mechanism0.sem.scope.redOpMinMax.typeMinMax [dstLeId, dstDataOff], [src], size, [bar];\n\nfabric.try_red.async{.multimem}.src.completion_mechanism0.sem.scope.redOpArith.typeArith [dstLeId, dstDataOff], [src], size, [bar];\n\nfabric.try_red.async{.multimem}.src.completion_mechanism1.sem.scope.redOpBit.typeBit [dstLeId, dstDataOff, dstCounterOff], [src], size, [bar];\n\nfabric.try_red.async{.multimem}.src.completion_mechanism1.sem.scope.redOpMinMax.typeMinMax [dstLeId, dstDataOff, dstCounterOff], [src], size, [bar];\n\nfabric.try_red.async{.multimem}.src.completion_mechanism1.sem.scope.redOpAdd.typeAdd [dstLeId, dstDataOff, dstCounterOff], [src], size, [bar];\n\n.src = { .shared::cta }\n.completion_mechanism0 = { .mbarrier::complete_tx::16B.mbarrier::report::fabric }\n.completion_mechanism1 = { .mbarrier::complete_tx::16B.mbarrier::report::fabric.counted::bytes }\n.sem = { .relaxed }\n.scope = { .sys }\n.redOpBit = { .and, .xor, .or }\n.typeBit = { .b32, .b64 }\n.redOpMinMax = { .min, .max }\n.typeMinMax = { .u32, .s32, .u64, .s64, .f16, .bf16 }\n.redOpArith = { .add }\n.typeArith = { .u32, .u64, .f16, .bf16, .f32, .f64 }\n.redOpAdd = { .add }\n.typeAdd = { .u32, .u64, .f16, .bf16, .f32, .f64 }"
                ))),
                Version(PositiveInt(9), NonNegativeInt(3)),
                Target(100),
                Vec(
                  InstructionForm(
                    Opcode("fabric.try_red"),
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Address
                    ),
                    Vec(
                      ModifierGroup(ModifierName("async")),
                      ModifierGroup(ModifierName("shared::cta")),
                      ModifierGroup(ModifierName("relaxed")),
                      ModifierGroup(ModifierName("sys")),
                      ModifierGroup(
                        ModifierName("and"),
                        ModifierName("or"),
                        ModifierName("xor")
                      ),
                      ModifierGroup(
                        ModifierName("b32"),
                        ModifierName("b64")
                      )
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName("multimem")),
                    true
                  ),
                  InstructionForm(
                    Opcode("fabric.try_red"),
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Address
                    ),
                    Vec(
                      ModifierGroup(ModifierName("async")),
                      ModifierGroup(ModifierName("shared::cta")),
                      ModifierGroup(ModifierName("relaxed")),
                      ModifierGroup(ModifierName("sys")),
                      ModifierGroup(
                        ModifierName("max"),
                        ModifierName("min")
                      ),
                      ModifierGroup(
                        ModifierName("bf16"),
                        ModifierName("f16"),
                        ModifierName("s32"),
                        ModifierName("s64"),
                        ModifierName("u32"),
                        ModifierName("u64")
                      )
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName("multimem")),
                    true
                  ),
                  InstructionForm(
                    Opcode("fabric.try_red"),
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Address
                    ),
                    Vec(
                      ModifierGroup(ModifierName("async")),
                      ModifierGroup(ModifierName("shared::cta")),
                      ModifierGroup(ModifierName("relaxed")),
                      ModifierGroup(ModifierName("sys")),
                      ModifierGroup(ModifierName("add")),
                      ModifierGroup(
                        ModifierName("bf16"),
                        ModifierName("f16"),
                        ModifierName("f32"),
                        ModifierName("f64"),
                        ModifierName("u32"),
                        ModifierName("u64")
                      )
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName("multimem")),
                    true
                  ),
                  InstructionForm(
                    Opcode("fabric.try_red"),
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Address
                    ),
                    Vec(
                      ModifierGroup(ModifierName("async")),
                      ModifierGroup(ModifierName("shared::cta")),
                      ModifierGroup(ModifierName("relaxed")),
                      ModifierGroup(ModifierName("sys")),
                      ModifierGroup(
                        ModifierName("and"),
                        ModifierName("or"),
                        ModifierName("xor")
                      ),
                      ModifierGroup(
                        ModifierName("b32"),
                        ModifierName("b64")
                      )
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName("multimem")),
                    true
                  ),
                  InstructionForm(
                    Opcode("fabric.try_red"),
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Address
                    ),
                    Vec(
                      ModifierGroup(ModifierName("async")),
                      ModifierGroup(ModifierName("shared::cta")),
                      ModifierGroup(ModifierName("relaxed")),
                      ModifierGroup(ModifierName("sys")),
                      ModifierGroup(
                        ModifierName("max"),
                        ModifierName("min")
                      ),
                      ModifierGroup(
                        ModifierName("bf16"),
                        ModifierName("f16"),
                        ModifierName("s32"),
                        ModifierName("s64"),
                        ModifierName("u32"),
                        ModifierName("u64")
                      )
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName("multimem")),
                    true
                  ),
                  InstructionForm(
                    Opcode("fabric.try_red"),
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Address
                    ),
                    Vec(
                      ModifierGroup(ModifierName("async")),
                      ModifierGroup(ModifierName("shared::cta")),
                      ModifierGroup(ModifierName("relaxed")),
                      ModifierGroup(ModifierName("sys")),
                      ModifierGroup(ModifierName("add")),
                      ModifierGroup(
                        ModifierName("bf16"),
                        ModifierName("f16"),
                        ModifierName("f32"),
                        ModifierName("f64"),
                        ModifierName("u32"),
                        ModifierName("u64")
                      )
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName("multimem")),
                    true
                  )
                )
              )
            )))
        )
    case FabricWait extends Definition(
          Opcode("fabric.wait"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 0) Right(Instruction.FabricWait(mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("fabric.wait", Vec(0), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("fabric-instructions-wait"),
                NonEmptyString("9.7.10.5.6. Fabric Instructions: fabric.wait"),
                NonEmptyString("fabric.wait"),
                NonEmptyString("Fabric Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(NonEmptyString("fabric.wait.sync_restrict::reads;"))),
                Version(PositiveInt(9), NonNegativeInt(3)),
                Target(100),
                Vec(InstructionForm(
                  Opcode("fabric.wait"),
                  Vec(),
                  Vec(
                    ModifierGroup(ModifierName("sync_restrict::reads"))
                  ),
                  _root_.scala.collection.immutable.Set(),
                  true
                ))
              )
            )))
        )
    case Fence extends Definition(
          Opcode("fence"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 0 || operands.size == 2)
              Right(Instruction.Fence(operands.lift(0), operands.lift(1), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("fence", Vec(0, 2), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("parallel-synchronization-and-communication-instructions-membar"),
                NonEmptyString("9.7.14.4. Parallel Synchronization and Communication Instructions: membar / fence"),
                NonEmptyString("membar / fence"),
                NonEmptyString("Parallel Synchronization and Communication Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(NonEmptyString(
                  "// Thread fence:\nfence{.sem}.scope;\n\n// Thread fence:\nfence.acquire.sync_restrict::shared::cluster.cluster;\nfence.release.sync_restrict::shared::cta.cluster;\n\n// Operation fence:\nfence.op_restrict.release.cluster;\n\n// Proxy fence (bi-directional):\nfence.proxy.proxykind;\n\n// Proxy fence (uni-directional):\nfence.proxy.to_proxykind::from_proxykind.release.scope;\nfence.proxy.to_proxykind::from_proxykind.acquire.scope  [addr], size;\nfence.proxy.async::generic.acquire.sync_restrict::shared::cluster.cluster;\nfence.proxy.async::generic.release.sync_restrict::shared::cta.cluster;\nfence.proxy.to_proxykind::from_proxykind_fabric.alias.sem_fabric.sys;\n\n// Old style membar:\nmembar.level;\nmembar.proxy.proxykind;\n\n.sem       = { .sc, .acq_rel, .acquire, .release };\n.scope     = { .cta, .cluster, .gpu, .sys };\n.level     = { .cta, .gl, .sys };\n.proxykind = { .alias, .async, .async.global, .async.shared::{cta, cluster} };\n.op_restrict = { .mbarrier_init };\n.to_proxykind::from_proxykind = {.tensormap::generic};\n.to_proxykind::from_proxykind_fabric = {.generic::fabric, .fabric::generic, .fabric::fabric };\n.sem_fabric = { .acquire, .release };"
                ))),
                Version(PositiveInt(1), NonNegativeInt.Zero),
                Target(20),
                Vec(
                  InstructionForm(
                    Opcode("fence"),
                    Vec(),
                    Vec(ModifierGroup(
                      ModifierName("cluster"),
                      ModifierName("cta"),
                      ModifierName("gpu"),
                      ModifierName("sys")
                    )),
                    _root_.scala.collection.immutable.Set(
                      ModifierName("acq_rel"),
                      ModifierName("acquire"),
                      ModifierName("release"),
                      ModifierName("sc")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode("fence"),
                    Vec(),
                    Vec(
                      ModifierGroup(ModifierName("acquire")),
                      ModifierGroup(ModifierName("sync_restrict::shared::cluster")),
                      ModifierGroup(ModifierName("cluster"))
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Opcode("fence"),
                    Vec(),
                    Vec(
                      ModifierGroup(ModifierName("release")),
                      ModifierGroup(ModifierName("sync_restrict::shared::cta")),
                      ModifierGroup(ModifierName("cluster"))
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Opcode("fence"),
                    Vec(),
                    Vec(
                      ModifierGroup(ModifierName("mbarrier_init")),
                      ModifierGroup(ModifierName("release")),
                      ModifierGroup(ModifierName("cluster"))
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Opcode("fence"),
                    Vec(),
                    Vec(
                      ModifierGroup(ModifierName("proxy")),
                      ModifierGroup(
                        ModifierName("alias"),
                        ModifierName("async"),
                        ModifierName("cluster")
                      )
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Opcode("fence"),
                    Vec(),
                    Vec(
                      ModifierGroup(ModifierName("proxy")),
                      ModifierGroup(ModifierName("tensormap::generic")),
                      ModifierGroup(ModifierName("release")),
                      ModifierGroup(
                        ModifierName("cluster"),
                        ModifierName("cta"),
                        ModifierName("gpu"),
                        ModifierName("sys")
                      )
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Opcode("fence"),
                    Vec(OperandConstraint.Address, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(ModifierName("proxy")),
                      ModifierGroup(ModifierName("tensormap::generic")),
                      ModifierGroup(ModifierName("acquire")),
                      ModifierGroup(
                        ModifierName("cluster"),
                        ModifierName("cta"),
                        ModifierName("gpu"),
                        ModifierName("sys")
                      )
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Opcode("fence"),
                    Vec(),
                    Vec(
                      ModifierGroup(ModifierName("proxy")),
                      ModifierGroup(ModifierName("async::generic")),
                      ModifierGroup(ModifierName("acquire")),
                      ModifierGroup(ModifierName("sync_restrict::shared::cluster")),
                      ModifierGroup(ModifierName("cluster"))
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Opcode("fence"),
                    Vec(),
                    Vec(
                      ModifierGroup(ModifierName("proxy")),
                      ModifierGroup(ModifierName("async::generic")),
                      ModifierGroup(ModifierName("release")),
                      ModifierGroup(ModifierName("sync_restrict::shared::cta")),
                      ModifierGroup(ModifierName("cluster"))
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Opcode("fence"),
                    Vec(),
                    Vec(
                      ModifierGroup(ModifierName("proxy")),
                      ModifierGroup(
                        ModifierName("fabric::fabric"),
                        ModifierName("fabric::generic"),
                        ModifierName("generic::fabric")
                      ),
                      ModifierGroup(ModifierName("alias")),
                      ModifierGroup(
                        ModifierName("acquire"),
                        ModifierName("release")
                      ),
                      ModifierGroup(ModifierName("sys"))
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  )
                )
              )
            )))
        )
    case Fma extends Definition(
          Opcode("fma"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 4)
              Right(Instruction.Fma(operands(0), operands(1), operands(2), operands(3), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("fma", Vec(4), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("floating-point-instructions-fma"),
                NonEmptyString("9.7.3.6. Floating Point Instructions: fma"),
                NonEmptyString("fma"),
                NonEmptyString("Floating Point Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(NonEmptyString(
                  "fma.rnd{.ftz}{.sat}.f32  d, a, b, c;\nfma.rnd{.ftz}.f32x2      d, a, b, c;\nfma.rnd.f64              d, a, b, c;\n\n.rnd = { .rn, .rz, .rm, .rp };"
                ))),
                Version(PositiveInt(1), NonNegativeInt.Zero),
                Target(13),
                Vec(
                  InstructionForm(
                    Opcode("fma"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(
                        ModifierName("rm"),
                        ModifierName("rn"),
                        ModifierName("rp"),
                        ModifierName("rz")
                      ),
                      ModifierGroup(ModifierName("f32"))
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName("ftz"), ModifierName("sat")),
                    false
                  ),
                  InstructionForm(
                    Opcode("fma"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(
                        ModifierName("rm"),
                        ModifierName("rn"),
                        ModifierName("rp"),
                        ModifierName("rz")
                      ),
                      ModifierGroup(ModifierName("f32x2"))
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName("ftz")),
                    false
                  ),
                  InstructionForm(
                    Opcode("fma"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(
                        ModifierName("rm"),
                        ModifierName("rn"),
                        ModifierName("rp"),
                        ModifierName("rz")
                      ),
                      ModifierGroup(ModifierName("f64"))
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  )
                )
              ),
              Metadata.Entry(
                NonEmptyString("half-precision-floating-point-instructions-fma"),
                NonEmptyString("9.7.4.4. Half Precision Floating Point Instructions: fma"),
                NonEmptyString("fma"),
                NonEmptyString("Half Precision Floating Point Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(NonEmptyString(
                  "fma.rnd{.ftz}{.sat}.f16     d, a, b, c;\nfma.rnd{.ftz}{.sat}.f16x2   d, a, b, c;\nfma.rnd{.ftz}.relu.f16      d, a, b, c;\nfma.rnd{.ftz}.relu.f16x2    d, a, b, c;\nfma.rnd{.relu}.bf16         d, a, b, c;\nfma.rnd{.relu}.bf16x2       d, a, b, c;\nfma.rnd.oob.{relu}.type     d, a, b, c;\n\n.rnd = { .rn };"
                ))),
                Version(PositiveInt(4), NonNegativeInt(2)),
                Target(53),
                Vec(
                  InstructionForm(
                    Opcode("fma"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(ModifierName("rn")),
                      ModifierGroup(ModifierName("f16"))
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName("ftz"), ModifierName("sat")),
                    false
                  ),
                  InstructionForm(
                    Opcode("fma"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(ModifierName("rn")),
                      ModifierGroup(ModifierName("f16x2"))
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName("ftz"), ModifierName("sat")),
                    false
                  ),
                  InstructionForm(
                    Opcode("fma"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(ModifierName("rn")),
                      ModifierGroup(ModifierName("relu")),
                      ModifierGroup(ModifierName("f16"))
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName("ftz")),
                    false
                  ),
                  InstructionForm(
                    Opcode("fma"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(ModifierName("rn")),
                      ModifierGroup(ModifierName("relu")),
                      ModifierGroup(ModifierName("f16x2"))
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName("ftz")),
                    false
                  ),
                  InstructionForm(
                    Opcode("fma"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(ModifierName("rn")),
                      ModifierGroup(ModifierName("bf16"))
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName("relu")),
                    false
                  ),
                  InstructionForm(
                    Opcode("fma"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(ModifierName("rn")),
                      ModifierGroup(ModifierName("bf16x2"))
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName("relu")),
                    false
                  ),
                  InstructionForm(
                    Opcode("fma"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(ModifierName("rn")),
                      ModifierGroup(ModifierName("oob")),
                      ModifierGroup(ModifierName("type"))
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  )
                )
              ),
              Metadata.Entry(
                NonEmptyString("mixed-precision-floating-point-instructions-fma"),
                NonEmptyString("9.7.5.3. Mixed Precision Floating Point Instructions: fma"),
                NonEmptyString("fma"),
                NonEmptyString("Mixed Precision Floating Point Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(NonEmptyString(
                  "fma.rnd{.sat}.f32.abtype  d, a, b, c;\n\n.abtype = { .f16, .bf16};\n.rnd    = { .rn, .rz, .rm, .rp };"
                ))),
                Version(PositiveInt(1), NonNegativeInt.Zero),
                Target(100),
                Vec(InstructionForm(
                  Opcode("fma"),
                  Vec(
                    OperandConstraint.Register,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Vec(
                    ModifierGroup(
                      ModifierName("rm"),
                      ModifierName("rn"),
                      ModifierName("rp"),
                      ModifierName("rz")
                    ),
                    ModifierGroup(ModifierName("f32")),
                    ModifierGroup(
                      ModifierName("bf16"),
                      ModifierName("f16")
                    )
                  ),
                  _root_.scala.collection.immutable.Set(ModifierName("sat")),
                  true
                ))
              )
            )))
        )
    case Fns extends Definition(
          Opcode("fns"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 4)
              Right(Instruction.Fns(operands(0), operands(1), operands(2), operands(3), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("fns", Vec(4), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("integer-arithmetic-instructions-fns"),
                NonEmptyString("9.7.1.18. Integer Arithmetic Instructions: fns"),
                NonEmptyString("fns"),
                NonEmptyString("Integer Arithmetic Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(NonEmptyString("fns.b32 d, mask, base, offset;"))),
                Version(PositiveInt(6), NonNegativeInt.Zero),
                Target(30),
                Vec(InstructionForm(
                  Opcode("fns"),
                  Vec(
                    OperandConstraint.Register,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Vec(ModifierGroup(ModifierName("b32"))),
                  _root_.scala.collection.immutable.Set(),
                  true
                ))
              )
            )))
        )
    case GetCtaRank extends Definition(
          Opcode("getctarank"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 2) Right(Instruction.GetCtaRank(operands(0), operands(1), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("getctarank", Vec(2), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("data-movement-and-conversion-instructions-getctarank"),
                NonEmptyString("9.7.9.25. Data Movement and Conversion Instructions: getctarank"),
                NonEmptyString("getctarank"),
                NonEmptyString("Data Movement and Conversion Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(NonEmptyString(
                  "getctarank{.space}.type d, a;\n\n// Get cta rank from source shared memory address in register a.\ngetctarank.shared::cluster.type d, a;\n\n// Get cta rank from shared memory variable.\ngetctarank.shared::cluster.type d, var;\n\n// Get cta rank from shared memory variable+offset.\ngetctarank.shared::cluster.type d, var + imm;\n\n// Get cta rank from generic address of shared memory variable in register a.\ngetctarank.type d, a;\n\n.space = { .shared::cluster }\n.type  = { .u32, .u64 }"
                ))),
                Version(PositiveInt(7), NonNegativeInt(8)),
                Target(90),
                Vec(
                  InstructionForm(
                    Opcode("getctarank"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(ModifierGroup(
                      ModifierName("u32"),
                      ModifierName("u64")
                    )),
                    _root_.scala.collection.immutable.Set(ModifierName("shared::cluster")),
                    true
                  ),
                  InstructionForm(
                    Opcode("getctarank"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(ModifierName("shared::cluster")),
                      ModifierGroup(
                        ModifierName("u32"),
                        ModifierName("u64")
                      )
                    ),
                    _root_.scala.collection.immutable.Set(),
                    true
                  ),
                  InstructionForm(
                    Opcode("getctarank"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(ModifierName("shared::cluster")),
                      ModifierGroup(
                        ModifierName("u32"),
                        ModifierName("u64")
                      )
                    ),
                    _root_.scala.collection.immutable.Set(),
                    true
                  ),
                  InstructionForm(
                    Opcode("getctarank"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(ModifierName("shared::cluster")),
                      ModifierGroup(
                        ModifierName("u32"),
                        ModifierName("u64")
                      )
                    ),
                    _root_.scala.collection.immutable.Set(),
                    true
                  ),
                  InstructionForm(
                    Opcode("getctarank"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(ModifierGroup(
                      ModifierName("u32"),
                      ModifierName("u64")
                    )),
                    _root_.scala.collection.immutable.Set(),
                    true
                  )
                )
              )
            )))
        )
    case GridDependencyControl extends Definition(
          Opcode("griddepcontrol"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 0) Right(Instruction.GridDependencyControl(mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("griddepcontrol", Vec(0), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("parallel-synchronization-and-communication-instructions-griddepcontrol"),
                NonEmptyString("9.7.14.14. Parallel Synchronization and Communication Instructions: griddepcontrol"),
                NonEmptyString("griddepcontrol"),
                NonEmptyString("Parallel Synchronization and Communication Instructions"),
                NonEmptyVec.unsafeFromVec(
                  Vec(NonEmptyString("griddepcontrol.action;\n\n.action   = { .launch_dependents, .wait }"))
                ),
                Version(PositiveInt(7), NonNegativeInt(8)),
                Target(90),
                Vec(InstructionForm(
                  Opcode("griddepcontrol"),
                  Vec(),
                  Vec(ModifierGroup(
                    ModifierName("launch_dependents"),
                    ModifierName("wait")
                  )),
                  _root_.scala.collection.immutable.Set(),
                  true
                ))
              )
            )))
        )
    case IsSpacePredicate extends Definition(
          Opcode("isspacep"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 2) Right(Instruction.IsSpacePredicate(operands(0), operands(1), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("isspacep", Vec(2), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("data-movement-and-conversion-instructions-isspacep"),
                NonEmptyString("9.7.9.20. Data Movement and Conversion Instructions: isspacep"),
                NonEmptyString("isspacep"),
                NonEmptyString("Data Movement and Conversion Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(NonEmptyString(
                  "isspacep.space  p, a;    // result is .pred\n\n.space = { const, .global, .local, .shared{::cta, ::cluster}, .param{::entry} };"
                ))),
                Version(PositiveInt(2), NonNegativeInt.Zero),
                Target(20),
                Vec(InstructionForm(
                  Opcode("isspacep"),
                  Vec(OperandConstraint.Register, OperandConstraint.Value),
                  Vec(ModifierGroup(
                    ModifierName("const"),
                    ModifierName("global"),
                    ModifierName("local")
                  )),
                  _root_.scala.collection.immutable.Set(),
                  false
                ))
              )
            )))
        )
    case IsTypePredicate extends Definition(
          Opcode("istypep"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 2) Right(Instruction.IsTypePredicate(operands(0), operands(1), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("istypep", Vec(2), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("texture-instructions-istypep"),
                NonEmptyString("9.7.11.6. Texture Instructions: istypep"),
                NonEmptyString("istypep"),
                NonEmptyString("Texture Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(NonEmptyString(
                  "istypep.type   p, a;  // result is .pred\n\n.type = { .texref, .samplerref, .surfref };"
                ))),
                Version(PositiveInt(4), NonNegativeInt.Zero),
                Target(30),
                Vec(InstructionForm(
                  Opcode("istypep"),
                  Vec(OperandConstraint.Register, OperandConstraint.Value),
                  Vec(ModifierGroup(
                    ModifierName("samplerref"),
                    ModifierName("surfref"),
                    ModifierName("texref")
                  )),
                  _root_.scala.collection.immutable.Set(),
                  true
                ))
              )
            )))
        )
    case Ld extends Definition(
          Opcode("ld"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 2 || operands.size == 3)
              Right(Instruction.Ld(operands(0), operands(1), operands.lift(2), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("ld", Vec(2, 3), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("data-movement-and-conversion-instructions-ld"),
                NonEmptyString("9.7.9.8. Data Movement and Conversion Instructions: ld"),
                NonEmptyString("ld"),
                NonEmptyString("Data Movement and Conversion Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(NonEmptyString(
                  "ld{.weak}{.ss}{.cop}{.level::cache_hint}{.level::prefetch_size}{.vec}.type  d, [a]{.unified}{, cache_policy};\n\nld{.weak}{.ss}{.level1::eviction_priority}{.level2::eviction_priority}{.level::cache_hint}{.level::prefetch_size}{.vec}.type  d, [a]{.unified}{, cache_policy};\n\nld.volatile{.ss}{.level::prefetch_size}{.vec}.type  d, [a];\n\nld.relaxed.scope{.ss}{.level1::eviction_priority}{.level2::eviction_priority}{.level::cache_hint}{.level::prefetch_size}{.vec}.type  d, [a]{, cache_policy};\n\nld.acquire.scope{.ss}{.level1::eviction_priority}{.level2::eviction_priority}{.level::cache_hint}{.level::prefetch_size}{.vec}.type  d, [a]{, cache_policy};\n\nld.mmio.sem.sys{.global}.type  d, [a];\n\n.ss =                       { .const, .global, .local, .param{::entry, ::func}, .shared{::cta, ::cluster} };\n.cop =                      { .ca, .cg, .cs, .lu, .cv };\n.sem =                      { .acquire, .relaxed };\n.level1::eviction_priority = { .L1::evict_normal, .L1::evict_unchanged,\n                               .L1::evict_first, .L1::evict_last, .L1::no_allocate };\n.level2::eviction_priority = {.L2::evict_normal, .L2::evict_first, .L2::evict_last};\n.level::cache_hint =        { .L2::cache_hint };\n.level::prefetch_size =     { .L2::64B, .L2::128B, .L2::256B }\n.scope =                    { .cta, .cluster, .gpu, .sys };\n.vec =                      { .v2, .v4, .v8 };\n.type =                     { .b8, .b16, .b32, .b64, .b128,\n                              .u8, .u16, .u32, .u64,\n                              .s8, .s16, .s32, .s64,\n                              .f32, .f64 };"
                ))),
                Version(PositiveInt(1), NonNegativeInt.Zero),
                Target(13),
                Vec(
                  InstructionForm(
                    Opcode("ld"),
                    Vec(OperandConstraint.Register, OperandConstraint.Address, OperandConstraint.Value),
                    Vec(ModifierGroup(
                      ModifierName("b128"),
                      ModifierName("b16"),
                      ModifierName("b32"),
                      ModifierName("b64"),
                      ModifierName("b8"),
                      ModifierName("f32"),
                      ModifierName("f64"),
                      ModifierName("s16"),
                      ModifierName("s32"),
                      ModifierName("s64"),
                      ModifierName("s8"),
                      ModifierName("u16"),
                      ModifierName("u32"),
                      ModifierName("u64"),
                      ModifierName("u8")
                    )),
                    _root_.scala.collection.immutable.Set(
                      ModifierName("L2::cache_hint"),
                      ModifierName("ca"),
                      ModifierName("cg"),
                      ModifierName("const"),
                      ModifierName("cs"),
                      ModifierName("cv"),
                      ModifierName("global"),
                      ModifierName("local"),
                      ModifierName("lu"),
                      ModifierName("v2"),
                      ModifierName("v4"),
                      ModifierName("v8"),
                      ModifierName("weak")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode("ld"),
                    Vec(OperandConstraint.Register, OperandConstraint.Address, OperandConstraint.Value),
                    Vec(ModifierGroup(
                      ModifierName("b128"),
                      ModifierName("b16"),
                      ModifierName("b32"),
                      ModifierName("b64"),
                      ModifierName("b8"),
                      ModifierName("f32"),
                      ModifierName("f64"),
                      ModifierName("s16"),
                      ModifierName("s32"),
                      ModifierName("s64"),
                      ModifierName("s8"),
                      ModifierName("u16"),
                      ModifierName("u32"),
                      ModifierName("u64"),
                      ModifierName("u8")
                    )),
                    _root_.scala.collection.immutable.Set(
                      ModifierName("L1::evict_first"),
                      ModifierName("L1::evict_last"),
                      ModifierName("L1::evict_normal"),
                      ModifierName("L1::evict_unchanged"),
                      ModifierName("L1::no_allocate"),
                      ModifierName("L2::cache_hint"),
                      ModifierName("L2::evict_first"),
                      ModifierName("L2::evict_last"),
                      ModifierName("L2::evict_normal"),
                      ModifierName("const"),
                      ModifierName("global"),
                      ModifierName("local"),
                      ModifierName("v2"),
                      ModifierName("v4"),
                      ModifierName("v8"),
                      ModifierName("weak")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode("ld"),
                    Vec(OperandConstraint.Register, OperandConstraint.Address),
                    Vec(
                      ModifierGroup(ModifierName("volatile")),
                      ModifierGroup(
                        ModifierName("b128"),
                        ModifierName("b16"),
                        ModifierName("b32"),
                        ModifierName("b64"),
                        ModifierName("b8"),
                        ModifierName("f32"),
                        ModifierName("f64"),
                        ModifierName("s16"),
                        ModifierName("s32"),
                        ModifierName("s64"),
                        ModifierName("s8"),
                        ModifierName("u16"),
                        ModifierName("u32"),
                        ModifierName("u64"),
                        ModifierName("u8")
                      )
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName("const"),
                      ModifierName("global"),
                      ModifierName("local"),
                      ModifierName("v2"),
                      ModifierName("v4"),
                      ModifierName("v8")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode("ld"),
                    Vec(OperandConstraint.Register, OperandConstraint.Address, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(ModifierName("relaxed")),
                      ModifierGroup(
                        ModifierName("cluster"),
                        ModifierName("cta"),
                        ModifierName("gpu"),
                        ModifierName("sys")
                      ),
                      ModifierGroup(
                        ModifierName("b128"),
                        ModifierName("b16"),
                        ModifierName("b32"),
                        ModifierName("b64"),
                        ModifierName("b8"),
                        ModifierName("f32"),
                        ModifierName("f64"),
                        ModifierName("s16"),
                        ModifierName("s32"),
                        ModifierName("s64"),
                        ModifierName("s8"),
                        ModifierName("u16"),
                        ModifierName("u32"),
                        ModifierName("u64"),
                        ModifierName("u8")
                      )
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName("L1::evict_first"),
                      ModifierName("L1::evict_last"),
                      ModifierName("L1::evict_normal"),
                      ModifierName("L1::evict_unchanged"),
                      ModifierName("L1::no_allocate"),
                      ModifierName("L2::cache_hint"),
                      ModifierName("L2::evict_first"),
                      ModifierName("L2::evict_last"),
                      ModifierName("L2::evict_normal"),
                      ModifierName("const"),
                      ModifierName("global"),
                      ModifierName("local"),
                      ModifierName("v2"),
                      ModifierName("v4"),
                      ModifierName("v8")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode("ld"),
                    Vec(OperandConstraint.Register, OperandConstraint.Address, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(ModifierName("acquire")),
                      ModifierGroup(
                        ModifierName("cluster"),
                        ModifierName("cta"),
                        ModifierName("gpu"),
                        ModifierName("sys")
                      ),
                      ModifierGroup(
                        ModifierName("b128"),
                        ModifierName("b16"),
                        ModifierName("b32"),
                        ModifierName("b64"),
                        ModifierName("b8"),
                        ModifierName("f32"),
                        ModifierName("f64"),
                        ModifierName("s16"),
                        ModifierName("s32"),
                        ModifierName("s64"),
                        ModifierName("s8"),
                        ModifierName("u16"),
                        ModifierName("u32"),
                        ModifierName("u64"),
                        ModifierName("u8")
                      )
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName("L1::evict_first"),
                      ModifierName("L1::evict_last"),
                      ModifierName("L1::evict_normal"),
                      ModifierName("L1::evict_unchanged"),
                      ModifierName("L1::no_allocate"),
                      ModifierName("L2::cache_hint"),
                      ModifierName("L2::evict_first"),
                      ModifierName("L2::evict_last"),
                      ModifierName("L2::evict_normal"),
                      ModifierName("const"),
                      ModifierName("global"),
                      ModifierName("local"),
                      ModifierName("v2"),
                      ModifierName("v4"),
                      ModifierName("v8")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode("ld"),
                    Vec(OperandConstraint.Register, OperandConstraint.Address),
                    Vec(
                      ModifierGroup(ModifierName("mmio")),
                      ModifierGroup(
                        ModifierName("acquire"),
                        ModifierName("relaxed")
                      ),
                      ModifierGroup(ModifierName("sys")),
                      ModifierGroup(
                        ModifierName("b128"),
                        ModifierName("b16"),
                        ModifierName("b32"),
                        ModifierName("b64"),
                        ModifierName("b8"),
                        ModifierName("f32"),
                        ModifierName("f64"),
                        ModifierName("s16"),
                        ModifierName("s32"),
                        ModifierName("s64"),
                        ModifierName("s8"),
                        ModifierName("u16"),
                        ModifierName("u32"),
                        ModifierName("u64"),
                        ModifierName("u8")
                      )
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName("global")),
                    false
                  )
                )
              )
            )))
        )
    case LdGlobalNc extends Definition(
          Opcode("ld.global.nc"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 2 || operands.size == 3)
              Right(Instruction.LdGlobalNc(operands(0), operands(1), operands.lift(2), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("ld.global.nc", Vec(2, 3), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("data-movement-and-conversion-instructions-ld-global-nc"),
                NonEmptyString("9.7.9.9. Data Movement and Conversion Instructions: ld.global.nc"),
                NonEmptyString("ld.global.nc"),
                NonEmptyString("Data Movement and Conversion Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(NonEmptyString(
                  "ld.global{.cop}.nc{.level::cache_hint}{.level::prefetch_size}.type                 d, [a]{, cache_policy};\nld.global{.cop}.nc{.level::cache_hint}{.level::prefetch_size}.vec.type             d, [a]{, cache_policy};\n\nld.global.nc{.level1::eviction_priority}{.level2::eviction_priority}{.level::cache_hint}{.level::prefetch_size}.type      d, [a]{, cache_policy};\nld.global.nc{.level1::eviction_priority}{.level2::eviction_priority}{.level::cache_hint}{.level::prefetch_size}.vec.type  d, [a]{, cache_policy};\n\n.cop  =                     { .ca, .cg, .cs };     // cache operation\n.level1::eviction_priority = { .L1::evict_normal, .L1::evict_unchanged,\n                               .L1::evict_first, .L1::evict_last, .L1::no_allocate};\n.level2::eviction_priority = {.L2::evict_normal, .L2::evict_first, .L2::evict_last};\n.level::cache_hint =        { .L2::cache_hint };\n.level::prefetch_size =     { .L2::64B, .L2::128B, .L2::256B }\n.vec  =                     { .v2, .v4, .v8 };\n.type =                     { .b8, .b16, .b32, .b64, .b128,\n                              .u8, .u16, .u32, .u64,\n                              .s8, .s16, .s32, .s64,\n                              .f32, .f64 };"
                ))),
                Version(PositiveInt(3), NonNegativeInt(1)),
                Target(32),
                Vec(
                  InstructionForm(
                    Opcode("ld.global.nc"),
                    Vec(OperandConstraint.Register, OperandConstraint.Address, OperandConstraint.Value),
                    Vec(ModifierGroup(
                      ModifierName("b128"),
                      ModifierName("b16"),
                      ModifierName("b32"),
                      ModifierName("b64"),
                      ModifierName("b8"),
                      ModifierName("f32"),
                      ModifierName("f64"),
                      ModifierName("s16"),
                      ModifierName("s32"),
                      ModifierName("s64"),
                      ModifierName("s8"),
                      ModifierName("u16"),
                      ModifierName("u32"),
                      ModifierName("u64"),
                      ModifierName("u8")
                    )),
                    _root_.scala.collection.immutable.Set(
                      ModifierName("L2::cache_hint"),
                      ModifierName("ca"),
                      ModifierName("cg"),
                      ModifierName("cs")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode("ld.global.nc"),
                    Vec(OperandConstraint.Register, OperandConstraint.Address, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(
                        ModifierName("v2"),
                        ModifierName("v4"),
                        ModifierName("v8")
                      ),
                      ModifierGroup(
                        ModifierName("b128"),
                        ModifierName("b16"),
                        ModifierName("b32"),
                        ModifierName("b64"),
                        ModifierName("b8"),
                        ModifierName("f32"),
                        ModifierName("f64"),
                        ModifierName("s16"),
                        ModifierName("s32"),
                        ModifierName("s64"),
                        ModifierName("s8"),
                        ModifierName("u16"),
                        ModifierName("u32"),
                        ModifierName("u64"),
                        ModifierName("u8")
                      )
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName("L2::cache_hint"),
                      ModifierName("ca"),
                      ModifierName("cg"),
                      ModifierName("cs")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode("ld.global.nc"),
                    Vec(OperandConstraint.Register, OperandConstraint.Address, OperandConstraint.Value),
                    Vec(ModifierGroup(
                      ModifierName("b128"),
                      ModifierName("b16"),
                      ModifierName("b32"),
                      ModifierName("b64"),
                      ModifierName("b8"),
                      ModifierName("f32"),
                      ModifierName("f64"),
                      ModifierName("s16"),
                      ModifierName("s32"),
                      ModifierName("s64"),
                      ModifierName("s8"),
                      ModifierName("u16"),
                      ModifierName("u32"),
                      ModifierName("u64"),
                      ModifierName("u8")
                    )),
                    _root_.scala.collection.immutable.Set(
                      ModifierName("L1::evict_first"),
                      ModifierName("L1::evict_last"),
                      ModifierName("L1::evict_normal"),
                      ModifierName("L1::evict_unchanged"),
                      ModifierName("L1::no_allocate"),
                      ModifierName("L2::cache_hint"),
                      ModifierName("L2::evict_first"),
                      ModifierName("L2::evict_last"),
                      ModifierName("L2::evict_normal")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode("ld.global.nc"),
                    Vec(OperandConstraint.Register, OperandConstraint.Address, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(
                        ModifierName("v2"),
                        ModifierName("v4"),
                        ModifierName("v8")
                      ),
                      ModifierGroup(
                        ModifierName("b128"),
                        ModifierName("b16"),
                        ModifierName("b32"),
                        ModifierName("b64"),
                        ModifierName("b8"),
                        ModifierName("f32"),
                        ModifierName("f64"),
                        ModifierName("s16"),
                        ModifierName("s32"),
                        ModifierName("s64"),
                        ModifierName("s8"),
                        ModifierName("u16"),
                        ModifierName("u32"),
                        ModifierName("u64"),
                        ModifierName("u8")
                      )
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName("L1::evict_first"),
                      ModifierName("L1::evict_last"),
                      ModifierName("L1::evict_normal"),
                      ModifierName("L1::evict_unchanged"),
                      ModifierName("L1::no_allocate"),
                      ModifierName("L2::cache_hint"),
                      ModifierName("L2::evict_first"),
                      ModifierName("L2::evict_last"),
                      ModifierName("L2::evict_normal")
                    ),
                    false
                  )
                )
              )
            )))
        )
    case Ldu extends Definition(
          Opcode("ldu"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 2) Right(Instruction.Ldu(operands(0), operands(1), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("ldu", Vec(2), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("data-movement-and-conversion-instructions-ldu"),
                NonEmptyString("9.7.9.10. Data Movement and Conversion Instructions: ldu"),
                NonEmptyString("ldu"),
                NonEmptyString("Data Movement and Conversion Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(NonEmptyString(
                  "ldu{.ss}.type      d, [a];       // load from address\nldu{.ss}.vec.type  d, [a];       // vec load from address\n\n.ss   = { .global };             // state space\n.vec  = { .v2, .v4 };\n.type = { .b8, .b16, .b32, .b64, .b128,\n          .u8, .u16, .u32, .u64,\n          .s8, .s16, .s32, .s64,\n                     .f32, .f64 };"
                ))),
                Version(PositiveInt(2), NonNegativeInt.Zero),
                Target(13),
                Vec(
                  InstructionForm(
                    Opcode("ldu"),
                    Vec(OperandConstraint.Register, OperandConstraint.Address),
                    Vec(ModifierGroup(
                      ModifierName("b128"),
                      ModifierName("b16"),
                      ModifierName("b32"),
                      ModifierName("b64"),
                      ModifierName("b8"),
                      ModifierName("f32"),
                      ModifierName("f64"),
                      ModifierName("s16"),
                      ModifierName("s32"),
                      ModifierName("s64"),
                      ModifierName("s8"),
                      ModifierName("u16"),
                      ModifierName("u32"),
                      ModifierName("u64"),
                      ModifierName("u8")
                    )),
                    _root_.scala.collection.immutable.Set(ModifierName("global")),
                    false
                  ),
                  InstructionForm(
                    Opcode("ldu"),
                    Vec(OperandConstraint.Register, OperandConstraint.Address),
                    Vec(
                      ModifierGroup(
                        ModifierName("v2"),
                        ModifierName("v4")
                      ),
                      ModifierGroup(
                        ModifierName("b128"),
                        ModifierName("b16"),
                        ModifierName("b32"),
                        ModifierName("b64"),
                        ModifierName("b8"),
                        ModifierName("f32"),
                        ModifierName("f64"),
                        ModifierName("s16"),
                        ModifierName("s32"),
                        ModifierName("s64"),
                        ModifierName("s8"),
                        ModifierName("u16"),
                        ModifierName("u32"),
                        ModifierName("u64"),
                        ModifierName("u8")
                      )
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName("global")),
                    false
                  )
                )
              )
            )))
        )
    case Lg2 extends Definition(
          Opcode("lg2"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 2) Right(Instruction.Lg2(operands(0), operands(1), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("lg2", Vec(2), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("floating-point-instructions-lg2"),
                NonEmptyString("9.7.3.20. Floating Point Instructions: lg2"),
                NonEmptyString("lg2"),
                NonEmptyString("Floating Point Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(NonEmptyString("lg2.approx{.ftz}.f32  d, a;"))),
                Version(PositiveInt(1), NonNegativeInt.Zero),
                Target(10),
                Vec(InstructionForm(
                  Opcode("lg2"),
                  Vec(OperandConstraint.Register, OperandConstraint.Value),
                  Vec(
                    ModifierGroup(ModifierName("approx")),
                    ModifierGroup(ModifierName("f32"))
                  ),
                  _root_.scala.collection.immutable.Set(ModifierName("ftz")),
                  true
                ))
              )
            )))
        )
    case Lop3 extends Definition(
          Opcode("lop3"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 5 || operands.size == 6) Right(Instruction.Lop3(
              operands(0),
              operands(1),
              operands(2),
              operands(3),
              operands(4),
              operands.lift(5),
              mods,
              pred
            ))
            else Left(Instruction.DecodeError.InvalidOperandCount("lop3", Vec(5, 6), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("logic-and-shift-instructions-lop3"),
                NonEmptyString("9.7.8.6. Logic and Shift Instructions: lop3"),
                NonEmptyString("lop3"),
                NonEmptyString("Logic and Shift Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(NonEmptyString(
                  "lop3.b32 d, a, b, c, immLut;\nlop3.BoolOp.b32 d|p, a, b, c, immLut, q;\n\n.BoolOp   = { .or , .and };"
                ))),
                Version(PositiveInt(4), NonNegativeInt(3)),
                Target(50),
                Vec(
                  InstructionForm(
                    Opcode("lop3"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(ModifierGroup(ModifierName("b32"))),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Opcode("lop3"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Register
                    ),
                    Vec(
                      ModifierGroup(
                        ModifierName("and"),
                        ModifierName("or")
                      ),
                      ModifierGroup(ModifierName("b32"))
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  )
                )
              )
            )))
        )
    case Mad extends Definition(
          Opcode("mad"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 4)
              Right(Instruction.Mad(operands(0), operands(1), operands(2), operands(3), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("mad", Vec(4), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("integer-arithmetic-instructions-mad"),
                NonEmptyString("9.7.1.4. Integer Arithmetic Instructions: mad"),
                NonEmptyString("mad"),
                NonEmptyString("Integer Arithmetic Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(NonEmptyString(
                  "mad.mode.type  d, a, b, c;\nmad.hi.sat.s32 d, a, b, c;\n\n.mode = { .hi, .lo, .wide };\n.type = { .u16, .u32, .u64,\n          .s16, .s32, .s64 };"
                ))),
                Version(PositiveInt(1), NonNegativeInt.Zero),
                Target(10),
                Vec(
                  InstructionForm(
                    Opcode("mad"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(
                        ModifierName("hi"),
                        ModifierName("lo"),
                        ModifierName("wide")
                      ),
                      ModifierGroup(
                        ModifierName("s16"),
                        ModifierName("s32"),
                        ModifierName("s64"),
                        ModifierName("u16"),
                        ModifierName("u32"),
                        ModifierName("u64")
                      )
                    ),
                    _root_.scala.collection.immutable.Set(),
                    true
                  ),
                  InstructionForm(
                    Opcode("mad"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(ModifierName("hi")),
                      ModifierGroup(ModifierName("sat")),
                      ModifierGroup(ModifierName("s32"))
                    ),
                    _root_.scala.collection.immutable.Set(),
                    true
                  )
                )
              ),
              Metadata.Entry(
                NonEmptyString("floating-point-instructions-mad"),
                NonEmptyString("9.7.3.7. Floating Point Instructions: mad"),
                NonEmptyString("mad"),
                NonEmptyString("Floating Point Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(NonEmptyString(
                  "mad{.ftz}{.sat}.f32      d, a, b, c;    // .target sm_1x\nmad.rnd{.ftz}{.sat}.f32  d, a, b, c;    // .target sm_20\nmad.rnd.f64              d, a, b, c;    // .target sm_13 and higher\n\n.rnd = { .rn, .rz, .rm, .rp };"
                ))),
                Version(PositiveInt(1), NonNegativeInt.Zero),
                Target(13),
                Vec(
                  InstructionForm(
                    Opcode("mad"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(ModifierGroup(ModifierName("f32"))),
                    _root_.scala.collection.immutable.Set(ModifierName("ftz"), ModifierName("sat")),
                    false
                  ),
                  InstructionForm(
                    Opcode("mad"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(
                        ModifierName("rm"),
                        ModifierName("rn"),
                        ModifierName("rp"),
                        ModifierName("rz")
                      ),
                      ModifierGroup(ModifierName("f32"))
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName("ftz"), ModifierName("sat")),
                    false
                  ),
                  InstructionForm(
                    Opcode("mad"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(
                        ModifierName("rm"),
                        ModifierName("rn"),
                        ModifierName("rp"),
                        ModifierName("rz")
                      ),
                      ModifierGroup(ModifierName("f64"))
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  )
                )
              )
            )))
        )
    case MadCc extends Definition(
          Opcode("mad.cc"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 4)
              Right(Instruction.MadCc(operands(0), operands(1), operands(2), operands(3), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("mad.cc", Vec(4), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("extended-precision-arithmetic-instructions-mad-cc"),
                NonEmptyString("9.7.2.5. Extended-Precision Arithmetic Instructions: mad.cc"),
                NonEmptyString("mad.cc"),
                NonEmptyString("Extended-Precision Arithmetic Instructions"),
                NonEmptyVec.unsafeFromVec(
                  Vec(NonEmptyString("mad{.hi,.lo}.cc.type  d, a, b, c;\n\n.type = { .u32, .s32, .u64, .s64 };"))
                ),
                Version(PositiveInt(1), NonNegativeInt.Zero),
                Target(20),
                Vec(InstructionForm(
                  Opcode("mad.cc"),
                  Vec(
                    OperandConstraint.Register,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Vec(ModifierGroup(
                    ModifierName("s32"),
                    ModifierName("s64"),
                    ModifierName("u32"),
                    ModifierName("u64")
                  )),
                  _root_.scala.collection.immutable.Set(ModifierName("hi"), ModifierName("lo")),
                  true
                ))
              )
            )))
        )
    case Mad24 extends Definition(
          Opcode("mad24"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 4)
              Right(Instruction.Mad24(operands(0), operands(1), operands(2), operands(3), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("mad24", Vec(4), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("integer-arithmetic-instructions-mad24"),
                NonEmptyString("9.7.1.7. Integer Arithmetic Instructions: mad24"),
                NonEmptyString("mad24"),
                NonEmptyString("Integer Arithmetic Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(NonEmptyString(
                  "mad24.mode.type  d, a, b, c;\nmad24.hi.sat.s32 d, a, b, c;\n\n.mode = { .hi, .lo };\n.type = { .u32, .s32 };"
                ))),
                Version(PositiveInt(1), NonNegativeInt.Zero),
                Target(10),
                Vec(
                  InstructionForm(
                    Opcode("mad24"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(
                        ModifierName("hi"),
                        ModifierName("lo")
                      ),
                      ModifierGroup(
                        ModifierName("s32"),
                        ModifierName("u32")
                      )
                    ),
                    _root_.scala.collection.immutable.Set(),
                    true
                  ),
                  InstructionForm(
                    Opcode("mad24"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(ModifierName("hi")),
                      ModifierGroup(ModifierName("sat")),
                      ModifierGroup(ModifierName("s32"))
                    ),
                    _root_.scala.collection.immutable.Set(),
                    true
                  )
                )
              )
            )))
        )
    case Madc extends Definition(
          Opcode("madc"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 4)
              Right(Instruction.Madc(operands(0), operands(1), operands(2), operands(3), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("madc", Vec(4), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("extended-precision-arithmetic-instructions-madc"),
                NonEmptyString("9.7.2.6. Extended-Precision Arithmetic Instructions: madc"),
                NonEmptyString("madc"),
                NonEmptyString("Extended-Precision Arithmetic Instructions"),
                NonEmptyVec.unsafeFromVec(
                  Vec(NonEmptyString("madc{.hi,.lo}{.cc}.type  d, a, b, c;\n\n.type = { .u32, .s32, .u64, .s64 };"))
                ),
                Version(PositiveInt(1), NonNegativeInt.Zero),
                Target(20),
                Vec(InstructionForm(
                  Opcode("madc"),
                  Vec(
                    OperandConstraint.Register,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Vec(ModifierGroup(
                    ModifierName("s32"),
                    ModifierName("s64"),
                    ModifierName("u32"),
                    ModifierName("u64")
                  )),
                  _root_.scala.collection.immutable.Set(
                    ModifierName("cc"),
                    ModifierName("hi"),
                    ModifierName("lo")
                  ),
                  true
                ))
              )
            )))
        )
    case Mapa extends Definition(
          Opcode("mapa"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 3) Right(Instruction.Mapa(operands(0), operands(1), operands(2), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("mapa", Vec(3), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("data-movement-and-conversion-instructions-mapa"),
                NonEmptyString("9.7.9.24. Data Movement and Conversion Instructions: mapa"),
                NonEmptyString("mapa"),
                NonEmptyString("Data Movement and Conversion Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(NonEmptyString(
                  "mapa{.space}.type          d, a, b;\n\n// Maps shared memory address in register a into CTA b.\nmapa.shared::cluster.type  d, a, b;\n\n// Maps shared memory variable into CTA b.\nmapa.shared::cluster.type  d, sh, b;\n\n// Maps shared memory variable into CTA b.\nmapa.shared::cluster.type  d, sh + imm, b;\n\n// Maps generic address in register a into CTA b.\nmapa.type                  d, a, b;\n\n.space = { .shared::cluster }\n.type  = { .u32, .u64 }"
                ))),
                Version(PositiveInt(7), NonNegativeInt(8)),
                Target(90),
                Vec(
                  InstructionForm(
                    Opcode("mapa"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(ModifierGroup(
                      ModifierName("u32"),
                      ModifierName("u64")
                    )),
                    _root_.scala.collection.immutable.Set(ModifierName("shared::cluster")),
                    true
                  ),
                  InstructionForm(
                    Opcode("mapa"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(ModifierName("shared::cluster")),
                      ModifierGroup(
                        ModifierName("u32"),
                        ModifierName("u64")
                      )
                    ),
                    _root_.scala.collection.immutable.Set(),
                    true
                  ),
                  InstructionForm(
                    Opcode("mapa"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(ModifierName("shared::cluster")),
                      ModifierGroup(
                        ModifierName("u32"),
                        ModifierName("u64")
                      )
                    ),
                    _root_.scala.collection.immutable.Set(),
                    true
                  ),
                  InstructionForm(
                    Opcode("mapa"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(ModifierName("shared::cluster")),
                      ModifierGroup(
                        ModifierName("u32"),
                        ModifierName("u64")
                      )
                    ),
                    _root_.scala.collection.immutable.Set(),
                    true
                  ),
                  InstructionForm(
                    Opcode("mapa"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(ModifierGroup(
                      ModifierName("u32"),
                      ModifierName("u64")
                    )),
                    _root_.scala.collection.immutable.Set(),
                    true
                  )
                )
              )
            )))
        )
    case MatchAllSync extends Definition(
          Opcode("match.all.sync"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 3) Right(Instruction.MatchAllSync(operands(0), operands(1), operands(2), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("match.all.sync", Vec(3), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("parallel-synchronization-and-communication-instructions-match-sync"),
                NonEmptyString("9.7.14.11. Parallel Synchronization and Communication Instructions: match.sync"),
                NonEmptyString("match.sync"),
                NonEmptyString("Parallel Synchronization and Communication Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(NonEmptyString(
                  "match.any.sync.type  d, a, membermask;\nmatch.all.sync.type  d[|p], a, membermask;\n\n.type = { .b32, .b64 };"
                ))),
                Version(PositiveInt(6), NonNegativeInt.Zero),
                Target(70),
                Vec(InstructionForm(
                  Opcode("match.all.sync"),
                  Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                  Vec(ModifierGroup(
                    ModifierName("b32"),
                    ModifierName("b64")
                  )),
                  _root_.scala.collection.immutable.Set(),
                  true
                ))
              )
            )))
        )
    case MatchAnySync extends Definition(
          Opcode("match.any.sync"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 3) Right(Instruction.MatchAnySync(operands(0), operands(1), operands(2), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("match.any.sync", Vec(3), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("parallel-synchronization-and-communication-instructions-match-sync"),
                NonEmptyString("9.7.14.11. Parallel Synchronization and Communication Instructions: match.sync"),
                NonEmptyString("match.sync"),
                NonEmptyString("Parallel Synchronization and Communication Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(NonEmptyString(
                  "match.any.sync.type  d, a, membermask;\nmatch.all.sync.type  d[|p], a, membermask;\n\n.type = { .b32, .b64 };"
                ))),
                Version(PositiveInt(6), NonNegativeInt.Zero),
                Target(70),
                Vec(InstructionForm(
                  Opcode("match.any.sync"),
                  Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                  Vec(ModifierGroup(
                    ModifierName("b32"),
                    ModifierName("b64")
                  )),
                  _root_.scala.collection.immutable.Set(),
                  true
                ))
              )
            )))
        )
    case Max extends Definition(
          Opcode("max"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 3 || operands.size == 4)
              Right(Instruction.Max(operands(0), operands(1), operands(2), operands.lift(3), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("max", Vec(3, 4), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("integer-arithmetic-instructions-max"),
                NonEmptyString("9.7.1.14. Integer Arithmetic Instructions: max"),
                NonEmptyString("max"),
                NonEmptyString("Integer Arithmetic Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(NonEmptyString(
                  "max.type1         d, a, b;\nmax{.relu}.type2  d, a, b;\n\n.type1 = { .u16, .u32, .u64,\n           .u16x2, .u8x4, .s16, .s64 };\n.type2 = { .s16x2, .s32, .s8x4 };"
                ))),
                Version(PositiveInt(1), NonNegativeInt.Zero),
                Target(10),
                Vec(
                  InstructionForm(
                    Opcode("max"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(ModifierGroup(
                      ModifierName("s16"),
                      ModifierName("s64"),
                      ModifierName("u16"),
                      ModifierName("u16x2"),
                      ModifierName("u32"),
                      ModifierName("u64"),
                      ModifierName("u8x4")
                    )),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Opcode("max"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(ModifierGroup(
                      ModifierName("s16x2"),
                      ModifierName("s32"),
                      ModifierName("s8x4")
                    )),
                    _root_.scala.collection.immutable.Set(ModifierName("relu")),
                    false
                  )
                )
              ),
              Metadata.Entry(
                NonEmptyString("floating-point-instructions-max"),
                NonEmptyString("9.7.3.12. Floating Point Instructions: max"),
                NonEmptyString("max"),
                NonEmptyString("Floating Point Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(NonEmptyString(
                  "max{.ftz}{.NaN}{.xorsign.abs}.f32  d, a, b;\nmax{.ftz}{.NaN}{.abs}.f32          d, a, b, c;\nmax.f64                            d, a, b;"
                ))),
                Version(PositiveInt(1), NonNegativeInt.Zero),
                Target(13),
                Vec(
                  InstructionForm(
                    Opcode("max"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(ModifierGroup(ModifierName("f32"))),
                    _root_.scala.collection.immutable.Set(
                      ModifierName("NaN"),
                      ModifierName("abs"),
                      ModifierName("ftz"),
                      ModifierName("xorsign")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode("max"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(ModifierGroup(ModifierName("f32"))),
                    _root_.scala.collection.immutable.Set(
                      ModifierName("NaN"),
                      ModifierName("abs"),
                      ModifierName("ftz")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode("max"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(ModifierGroup(ModifierName("f64"))),
                    _root_.scala.collection.immutable.Set(),
                    false
                  )
                )
              ),
              Metadata.Entry(
                NonEmptyString("half-precision-floating-point-instructions-max"),
                NonEmptyString("9.7.4.8. Half Precision Floating Point Instructions: max"),
                NonEmptyString("max"),
                NonEmptyString("Half Precision Floating Point Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(NonEmptyString(
                  "max{.ftz}{.NaN}{.xorsign.abs}.f16      d, a, b;\nmax{.ftz}{.NaN}{.xorsign.abs}.f16x2    d, a, b;\nmax{.NaN}{.xorsign.abs}.bf16           d, a, b;\nmax{.NaN}{.xorsign.abs}.bf16x2         d, a, b;"
                ))),
                Version(PositiveInt(7), NonNegativeInt.Zero),
                Target(80),
                Vec(
                  InstructionForm(
                    Opcode("max"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(ModifierGroup(ModifierName("f16"))),
                    _root_.scala.collection.immutable.Set(
                      ModifierName("NaN"),
                      ModifierName("abs"),
                      ModifierName("ftz"),
                      ModifierName("xorsign")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode("max"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(ModifierGroup(ModifierName("f16x2"))),
                    _root_.scala.collection.immutable.Set(
                      ModifierName("NaN"),
                      ModifierName("abs"),
                      ModifierName("ftz"),
                      ModifierName("xorsign")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode("max"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(ModifierGroup(ModifierName("bf16"))),
                    _root_.scala.collection.immutable.Set(
                      ModifierName("NaN"),
                      ModifierName("abs"),
                      ModifierName("xorsign")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode("max"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(ModifierGroup(ModifierName("bf16x2"))),
                    _root_.scala.collection.immutable.Set(
                      ModifierName("NaN"),
                      ModifierName("abs"),
                      ModifierName("xorsign")
                    ),
                    false
                  )
                )
              )
            )))
        )
    case MbarrierArrive extends Definition(
          Opcode("mbarrier.arrive"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 2 || operands.size == 3)
              Right(Instruction.MbarrierArrive(operands(0), operands(1), operands.lift(2), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("mbarrier.arrive", Vec(2, 3), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("parallel-synchronization-and-communication-instructions-mbarrier-arrive"),
                NonEmptyString(
                  "9.7.14.16.16. Parallel Synchronization and Communication Instructions: mbarrier.arrive"
                ),
                NonEmptyString("mbarrier.arrive"),
                NonEmptyString("Parallel Synchronization and Communication Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(NonEmptyString(
                  "mbarrier.arrive{.sem.scope}{.shared{::cta}}.b64           state, [addr]{, count};\nmbarrier.arrive{.sem.scope}{.shared::cluster}.b64         _, [addr] {,count}\nmbarrier.arrive.expect_tx{.sem.scope}{.shared{::cta}}.b64 state, [addr], txCount;\nmbarrier.arrive.expect_tx{.sem.scope}{.shared::cluster}.b64   _, [addr], txCount;\nmbarrier.arrive.noComplete{.release.cta}{.shared{::cta}}.b64  state, [addr], count;\n\n.sem   = { .release, .relaxed }\n.scope = { .cta, .cluster }"
                ))),
                Version(PositiveInt(7), NonNegativeInt.Zero),
                Target(80),
                Vec(
                  InstructionForm(
                    Opcode("mbarrier.arrive"),
                    Vec(OperandConstraint.Value, OperandConstraint.Address, OperandConstraint.Value),
                    Vec(ModifierGroup(ModifierName("b64"))),
                    _root_.scala.collection.immutable.Set(
                      ModifierName("cluster"),
                      ModifierName("cta"),
                      ModifierName("relaxed"),
                      ModifierName("release"),
                      ModifierName("shared")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode("mbarrier.arrive"),
                    Vec(OperandConstraint.Value, OperandConstraint.Address, OperandConstraint.Value),
                    Vec(ModifierGroup(ModifierName("b64"))),
                    _root_.scala.collection.immutable.Set(
                      ModifierName("cluster"),
                      ModifierName("cta"),
                      ModifierName("relaxed"),
                      ModifierName("release"),
                      ModifierName("shared::cluster")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode("mbarrier.arrive"),
                    Vec(OperandConstraint.Value, OperandConstraint.Address, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(ModifierName("expect_tx")),
                      ModifierGroup(ModifierName("b64"))
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName("cluster"),
                      ModifierName("cta"),
                      ModifierName("relaxed"),
                      ModifierName("release"),
                      ModifierName("shared")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode("mbarrier.arrive"),
                    Vec(OperandConstraint.Value, OperandConstraint.Address, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(ModifierName("expect_tx")),
                      ModifierGroup(ModifierName("b64"))
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName("cluster"),
                      ModifierName("cta"),
                      ModifierName("relaxed"),
                      ModifierName("release"),
                      ModifierName("shared::cluster")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode("mbarrier.arrive"),
                    Vec(OperandConstraint.Value, OperandConstraint.Address, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(ModifierName("noComplete")),
                      ModifierGroup(ModifierName("b64"))
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName("cta"),
                      ModifierName("release"),
                      ModifierName("shared")
                    ),
                    false
                  )
                )
              )
            )))
        )
    case MbarrierArriveDrop extends Definition(
          Opcode("mbarrier.arrive_drop"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 2 || operands.size == 3)
              Right(Instruction.MbarrierArriveDrop(operands(0), operands(1), operands.lift(2), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("mbarrier.arrive_drop", Vec(2, 3), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("parallel-synchronization-and-communication-instructions-mbarrier-arrive-drop"),
                NonEmptyString(
                  "9.7.14.16.17. Parallel Synchronization and Communication Instructions: mbarrier.arrive_drop"
                ),
                NonEmptyString("mbarrier.arrive_drop"),
                NonEmptyString("Parallel Synchronization and Communication Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(NonEmptyString(
                  "mbarrier.arrive_drop{.sem.scope}{.shared{::cta}}.b64              state, [addr] {, count};\nmbarrier.arrive_drop{.sem.scope}{.shared::cluster}.b64            _,     [addr] {, count};\nmbarrier.arrive_drop.expect_tx{.sem.scope}{.shared{::cta}}.b64    state, [addr], tx_count;\nmbarrier.arrive_drop.expect_tx{.sem.scope}{.shared::cluster}.b64  _,     [addr], tx_count;\nmbarrier.arrive_drop.noComplete{.release.cta}{.shared{::cta}}.b64 state, [addr], count;\n\n.sem   = { .release, .relaxed }\n.scope = { .cta, .cluster }"
                ))),
                Version(PositiveInt(7), NonNegativeInt.Zero),
                Target(80),
                Vec(
                  InstructionForm(
                    Opcode("mbarrier.arrive_drop"),
                    Vec(OperandConstraint.Value, OperandConstraint.Address, OperandConstraint.Value),
                    Vec(ModifierGroup(ModifierName("b64"))),
                    _root_.scala.collection.immutable.Set(
                      ModifierName("cluster"),
                      ModifierName("cta"),
                      ModifierName("relaxed"),
                      ModifierName("release"),
                      ModifierName("shared")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode("mbarrier.arrive_drop"),
                    Vec(OperandConstraint.Value, OperandConstraint.Address, OperandConstraint.Value),
                    Vec(ModifierGroup(ModifierName("b64"))),
                    _root_.scala.collection.immutable.Set(
                      ModifierName("cluster"),
                      ModifierName("cta"),
                      ModifierName("relaxed"),
                      ModifierName("release"),
                      ModifierName("shared::cluster")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode("mbarrier.arrive_drop"),
                    Vec(OperandConstraint.Value, OperandConstraint.Address, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(ModifierName("expect_tx")),
                      ModifierGroup(ModifierName("b64"))
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName("cluster"),
                      ModifierName("cta"),
                      ModifierName("relaxed"),
                      ModifierName("release"),
                      ModifierName("shared")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode("mbarrier.arrive_drop"),
                    Vec(OperandConstraint.Value, OperandConstraint.Address, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(ModifierName("expect_tx")),
                      ModifierGroup(ModifierName("b64"))
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName("cluster"),
                      ModifierName("cta"),
                      ModifierName("relaxed"),
                      ModifierName("release"),
                      ModifierName("shared::cluster")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode("mbarrier.arrive_drop"),
                    Vec(OperandConstraint.Value, OperandConstraint.Address, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(ModifierName("noComplete")),
                      ModifierGroup(ModifierName("b64"))
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName("cta"),
                      ModifierName("release"),
                      ModifierName("shared")
                    ),
                    false
                  )
                )
              )
            )))
        )
    case MbarrierCheckLayout extends Definition(
          Opcode("mbarrier.check_layout"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 2) Right(Instruction.MbarrierCheckLayout(operands(0), operands(1), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("mbarrier.check_layout", Vec(2), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("parallel-synchronization-and-communication-instructions-mbarrier-check-layout"),
                NonEmptyString(
                  "9.7.14.16.21. Parallel Synchronization and Communication Instructions: mbarrier.check_layout"
                ),
                NonEmptyString("mbarrier.check_layout"),
                NonEmptyString("Parallel Synchronization and Communication Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(NonEmptyString(
                  "mbarrier.check_layout.layout{.ss}.b64 p, [addr];\n\n.layout = { .layout::v0, .layout::v1 }\n.ss = { .shared::cta }"
                ))),
                Version(PositiveInt(9), NonNegativeInt(3)),
                Target(90),
                Vec(InstructionForm(
                  Opcode("mbarrier.check_layout"),
                  Vec(OperandConstraint.Register, OperandConstraint.Address),
                  Vec(
                    ModifierGroup(
                      ModifierName("layout::v0"),
                      ModifierName("layout::v1")
                    ),
                    ModifierGroup(ModifierName("b64"))
                  ),
                  _root_.scala.collection.immutable.Set(ModifierName("shared::cta")),
                  true
                ))
              )
            )))
        )
    case MbarrierCompleteTx extends Definition(
          Opcode("mbarrier.complete_tx"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 2) Right(Instruction.MbarrierCompleteTx(operands(0), operands(1), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("mbarrier.complete_tx", Vec(2), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("parallel-synchronization-and-communication-instructions-mbarrier-complete-tx"),
                NonEmptyString(
                  "9.7.14.16.15. Parallel Synchronization and Communication Instructions: mbarrier.complete_tx"
                ),
                NonEmptyString("mbarrier.complete_tx"),
                NonEmptyString("Parallel Synchronization and Communication Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(NonEmptyString(
                  "mbarrier.complete_tx{.sem.scope}{.space}.b64 [addr], txCount;\n\n.sem   = { .relaxed }\n.scope = { .cta, .cluster }\n.space = { .shared{::cta}, .shared::cluster }"
                ))),
                Version(PositiveInt(8), NonNegativeInt.Zero),
                Target(90),
                Vec(InstructionForm(
                  Opcode("mbarrier.complete_tx"),
                  Vec(OperandConstraint.Address, OperandConstraint.Value),
                  Vec(ModifierGroup(ModifierName("b64"))),
                  _root_.scala.collection.immutable.Set(
                    ModifierName("cluster"),
                    ModifierName("cta"),
                    ModifierName("relaxed")
                  ),
                  false
                ))
              )
            )))
        )
    case MbarrierExpectTx extends Definition(
          Opcode("mbarrier.expect_tx"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 2) Right(Instruction.MbarrierExpectTx(operands(0), operands(1), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("mbarrier.expect_tx", Vec(2), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("parallel-synchronization-and-communication-instructions-mbarrier-expect-tx"),
                NonEmptyString(
                  "9.7.14.16.14. Parallel Synchronization and Communication Instructions: mbarrier.expect_tx"
                ),
                NonEmptyString("mbarrier.expect_tx"),
                NonEmptyString("Parallel Synchronization and Communication Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(NonEmptyString(
                  "mbarrier.expect_tx{.sem.scope}{.space}.b64 [addr], txCount;\n\n.sem   = { .relaxed }\n.scope = { .cta, .cluster }\n.space = { .shared{::cta}, .shared::cluster }"
                ))),
                Version(PositiveInt(8), NonNegativeInt.Zero),
                Target(90),
                Vec(InstructionForm(
                  Opcode("mbarrier.expect_tx"),
                  Vec(OperandConstraint.Address, OperandConstraint.Value),
                  Vec(ModifierGroup(ModifierName("b64"))),
                  _root_.scala.collection.immutable.Set(
                    ModifierName("cluster"),
                    ModifierName("cta"),
                    ModifierName("relaxed")
                  ),
                  false
                ))
              )
            )))
        )
    case MbarrierInit extends Definition(
          Opcode("mbarrier.init"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 2) Right(Instruction.MbarrierInit(operands(0), operands(1), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("mbarrier.init", Vec(2), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("parallel-synchronization-and-communication-instructions-mbarrier-init"),
                NonEmptyString("9.7.14.16.12. Parallel Synchronization and Communication Instructions: mbarrier.init"),
                NonEmptyString("mbarrier.init"),
                NonEmptyString("Parallel Synchronization and Communication Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(NonEmptyString(
                  "mbarrier.init{.layout}{.shared{::cta}}.b64 [addr], count;\n\n.layout = { .layout::v0, .layout::v1 }"
                ))),
                Version(PositiveInt(7), NonNegativeInt.Zero),
                Target(80),
                Vec(InstructionForm(
                  Opcode("mbarrier.init"),
                  Vec(OperandConstraint.Address, OperandConstraint.Value),
                  Vec(ModifierGroup(ModifierName("b64"))),
                  _root_.scala.collection.immutable.Set(
                    ModifierName("layout::v0"),
                    ModifierName("layout::v1"),
                    ModifierName("shared")
                  ),
                  false
                ))
              )
            )))
        )
    case MbarrierInval extends Definition(
          Opcode("mbarrier.inval"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 1) Right(Instruction.MbarrierInval(operands(0), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("mbarrier.inval", Vec(1), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("parallel-synchronization-and-communication-instructions-mbarrier-inval"),
                NonEmptyString("9.7.14.16.13. Parallel Synchronization and Communication Instructions: mbarrier.inval"),
                NonEmptyString("mbarrier.inval"),
                NonEmptyString("Parallel Synchronization and Communication Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(NonEmptyString("mbarrier.inval{.shared{::cta}}.b64 [addr];"))),
                Version(PositiveInt(7), NonNegativeInt.Zero),
                Target(80),
                Vec(InstructionForm(
                  Opcode("mbarrier.inval"),
                  Vec(OperandConstraint.Address),
                  Vec(ModifierGroup(ModifierName("b64"))),
                  _root_.scala.collection.immutable.Set(ModifierName("shared")),
                  false
                ))
              )
            )))
        )
    case MbarrierPendingCount extends Definition(
          Opcode("mbarrier.pending_count"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 2) Right(Instruction.MbarrierPendingCount(operands(0), operands(1), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("mbarrier.pending_count", Vec(2), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("parallel-synchronization-and-communication-instructions-mbarrier-pending-count"),
                NonEmptyString(
                  "9.7.14.16.20. Parallel Synchronization and Communication Instructions: mbarrier.pending_count"
                ),
                NonEmptyString("mbarrier.pending_count"),
                NonEmptyString("Parallel Synchronization and Communication Instructions"),
                NonEmptyVec.unsafeFromVec(
                  Vec(NonEmptyString("mbarrier.pending_count{.layout}.b64 count, state;\n\n.layout = { .layout::v0 }"))
                ),
                Version(PositiveInt(7), NonNegativeInt.Zero),
                Target(80),
                Vec(InstructionForm(
                  Opcode("mbarrier.pending_count"),
                  Vec(OperandConstraint.Value, OperandConstraint.Value),
                  Vec(ModifierGroup(ModifierName("b64"))),
                  _root_.scala.collection.immutable.Set(ModifierName("layout::v0")),
                  false
                ))
              )
            )))
        )
    case MbarrierTestWait extends Definition(
          Opcode("mbarrier.test_wait"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 3 || operands.size == 4) Right(if (operands.size == 3)
              Instruction.MbarrierTestWait(operands(0), None, operands(1), operands(2), mods, pred)
            else Instruction.MbarrierTestWait(operands(0), Some(operands(1)), operands(2), operands(3), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("mbarrier.test_wait", Vec(3, 4), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("parallel-synchronization-and-communication-instructions-mbarrier-test-wait-try-wait"),
                NonEmptyString(
                  "9.7.14.16.19. Parallel Synchronization and Communication Instructions: mbarrier.test_wait / mbarrier.try_wait"
                ),
                NonEmptyString("mbarrier.test_wait / mbarrier.try_wait"),
                NonEmptyString("Parallel Synchronization and Communication Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(NonEmptyString(
                  "// without parity\nmbarrier.test_wait{.phase_type::primary}{.sem.scope}{.ss}.b64      waitComplete, [addr], state;\n\nmbarrier.test_wait.phase_type::primary{.sem}{.scope}{.ss}.b64      waitComplete|reportPredicate\n                                                                   {, reportValue}, [addr], state;\n\n// with parity\nmbarrier.test_wait.parity{.phase_type}{.sem.scope}{.ss}.b64        waitComplete, [addr], phaseParity;\n\nmbarrier.test_wait.parity.phase_type::primary{.sem}{.scope}{.ss}.b64  waitComplete|reportPredicate\n                                                                      {, reportValue}, [addr], phaseParity;\n\n// without parity\nmbarrier.try_wait{.phase_type::primary}{.sem.scope}{.ss}.b64      waitComplete, [addr], state {, timeHint};\n\nmbarrier.try_wait.phase_type::primary{.sem}{.scope}{.ss}.b64      waitComplete|reportPredicate {, reportValue},\n                                                                  [addr], state {, timeHint};\n\n// with parity\nmbarrier.try_wait.parity{.phase_type}{.sem.scope}{.ss}.b64            waitComplete, [addr], phaseParity {, timeHint};\n\nmbarrier.try_wait.parity.phase_type::primary{.sem}{.scope}{.ss}.b64   waitComplete|reportPredicate {, reportValue},\n                                                                      [addr], phaseParity {, timeHint};\n\n\n.ss   = { .shared{::cta} }\n.sem   = { .acquire, .relaxed }\n.scope = { .cta, .cluster }\n.phase_type = { .phase_type::primary, .phase_type::conditional }"
                ))),
                Version(PositiveInt(1), NonNegativeInt.Zero),
                Target(80),
                Vec(
                  InstructionForm(
                    Opcode("mbarrier.test_wait"),
                    Vec(OperandConstraint.Value, OperandConstraint.Address, OperandConstraint.Value),
                    Vec(ModifierGroup(ModifierName("b64"))),
                    _root_.scala.collection.immutable.Set(
                      ModifierName("acquire"),
                      ModifierName("cluster"),
                      ModifierName("cta"),
                      ModifierName("phase_type::primary"),
                      ModifierName("relaxed")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode("mbarrier.test_wait"),
                    Vec(
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Address,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(ModifierName("phase_type::primary")),
                      ModifierGroup(ModifierName("b64"))
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName("acquire"),
                      ModifierName("cluster"),
                      ModifierName("cta"),
                      ModifierName("relaxed")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode("mbarrier.test_wait"),
                    Vec(OperandConstraint.Value, OperandConstraint.Address, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(ModifierName("parity")),
                      ModifierGroup(ModifierName("b64"))
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName("acquire"),
                      ModifierName("cluster"),
                      ModifierName("cta"),
                      ModifierName("phase_type::conditional"),
                      ModifierName("phase_type::primary"),
                      ModifierName("relaxed")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode("mbarrier.test_wait"),
                    Vec(
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Address,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(ModifierName("parity")),
                      ModifierGroup(ModifierName("phase_type::primary")),
                      ModifierGroup(ModifierName("b64"))
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName("acquire"),
                      ModifierName("cluster"),
                      ModifierName("cta"),
                      ModifierName("relaxed")
                    ),
                    false
                  )
                )
              )
            )))
        )
    case MbarrierTryWait extends Definition(
          Opcode("mbarrier.try_wait"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 3 || operands.size == 4 || operands.size == 5) Right(if (operands.size == 3)
              Instruction.MbarrierTryWait(operands(0), None, operands(1), operands(2), None, mods, pred)
            else if (operands.size == 4 && !Operand.isWithPredicate(operands(0)))
              Instruction.MbarrierTryWait(operands(0), None, operands(1), operands(2), Some(operands(3)), mods, pred)
            else Instruction.MbarrierTryWait(
              operands(0),
              Some(operands(1)),
              operands(2),
              operands(3),
              operands.lift(4),
              mods,
              pred
            ))
            else Left(Instruction.DecodeError.InvalidOperandCount("mbarrier.try_wait", Vec(3, 4, 5), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("parallel-synchronization-and-communication-instructions-mbarrier-test-wait-try-wait"),
                NonEmptyString(
                  "9.7.14.16.19. Parallel Synchronization and Communication Instructions: mbarrier.test_wait / mbarrier.try_wait"
                ),
                NonEmptyString("mbarrier.test_wait / mbarrier.try_wait"),
                NonEmptyString("Parallel Synchronization and Communication Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(NonEmptyString(
                  "// without parity\nmbarrier.test_wait{.phase_type::primary}{.sem.scope}{.ss}.b64      waitComplete, [addr], state;\n\nmbarrier.test_wait.phase_type::primary{.sem}{.scope}{.ss}.b64      waitComplete|reportPredicate\n                                                                   {, reportValue}, [addr], state;\n\n// with parity\nmbarrier.test_wait.parity{.phase_type}{.sem.scope}{.ss}.b64        waitComplete, [addr], phaseParity;\n\nmbarrier.test_wait.parity.phase_type::primary{.sem}{.scope}{.ss}.b64  waitComplete|reportPredicate\n                                                                      {, reportValue}, [addr], phaseParity;\n\n// without parity\nmbarrier.try_wait{.phase_type::primary}{.sem.scope}{.ss}.b64      waitComplete, [addr], state {, timeHint};\n\nmbarrier.try_wait.phase_type::primary{.sem}{.scope}{.ss}.b64      waitComplete|reportPredicate {, reportValue},\n                                                                  [addr], state {, timeHint};\n\n// with parity\nmbarrier.try_wait.parity{.phase_type}{.sem.scope}{.ss}.b64            waitComplete, [addr], phaseParity {, timeHint};\n\nmbarrier.try_wait.parity.phase_type::primary{.sem}{.scope}{.ss}.b64   waitComplete|reportPredicate {, reportValue},\n                                                                      [addr], phaseParity {, timeHint};\n\n\n.ss   = { .shared{::cta} }\n.sem   = { .acquire, .relaxed }\n.scope = { .cta, .cluster }\n.phase_type = { .phase_type::primary, .phase_type::conditional }"
                ))),
                Version(PositiveInt(1), NonNegativeInt.Zero),
                Target(80),
                Vec(
                  InstructionForm(
                    Opcode("mbarrier.try_wait"),
                    Vec(
                      OperandConstraint.Value,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(ModifierGroup(ModifierName("b64"))),
                    _root_.scala.collection.immutable.Set(
                      ModifierName("acquire"),
                      ModifierName("cluster"),
                      ModifierName("cta"),
                      ModifierName("phase_type::primary"),
                      ModifierName("relaxed")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode("mbarrier.try_wait"),
                    Vec(
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(ModifierName("phase_type::primary")),
                      ModifierGroup(ModifierName("b64"))
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName("acquire"),
                      ModifierName("cluster"),
                      ModifierName("cta"),
                      ModifierName("relaxed")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode("mbarrier.try_wait"),
                    Vec(
                      OperandConstraint.Value,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(ModifierName("parity")),
                      ModifierGroup(ModifierName("b64"))
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName("acquire"),
                      ModifierName("cluster"),
                      ModifierName("cta"),
                      ModifierName("phase_type::conditional"),
                      ModifierName("phase_type::primary"),
                      ModifierName("relaxed")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode("mbarrier.try_wait"),
                    Vec(
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(ModifierName("parity")),
                      ModifierGroup(ModifierName("phase_type::primary")),
                      ModifierGroup(ModifierName("b64"))
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName("acquire"),
                      ModifierName("cluster"),
                      ModifierName("cta"),
                      ModifierName("relaxed")
                    ),
                    false
                  )
                )
              )
            )))
        )
    case Membar extends Definition(
          Opcode("membar"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 0) Right(Instruction.Membar(mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("membar", Vec(0), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("parallel-synchronization-and-communication-instructions-membar"),
                NonEmptyString("9.7.14.4. Parallel Synchronization and Communication Instructions: membar / fence"),
                NonEmptyString("membar / fence"),
                NonEmptyString("Parallel Synchronization and Communication Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(NonEmptyString(
                  "// Thread fence:\nfence{.sem}.scope;\n\n// Thread fence:\nfence.acquire.sync_restrict::shared::cluster.cluster;\nfence.release.sync_restrict::shared::cta.cluster;\n\n// Operation fence:\nfence.op_restrict.release.cluster;\n\n// Proxy fence (bi-directional):\nfence.proxy.proxykind;\n\n// Proxy fence (uni-directional):\nfence.proxy.to_proxykind::from_proxykind.release.scope;\nfence.proxy.to_proxykind::from_proxykind.acquire.scope  [addr], size;\nfence.proxy.async::generic.acquire.sync_restrict::shared::cluster.cluster;\nfence.proxy.async::generic.release.sync_restrict::shared::cta.cluster;\nfence.proxy.to_proxykind::from_proxykind_fabric.alias.sem_fabric.sys;\n\n// Old style membar:\nmembar.level;\nmembar.proxy.proxykind;\n\n.sem       = { .sc, .acq_rel, .acquire, .release };\n.scope     = { .cta, .cluster, .gpu, .sys };\n.level     = { .cta, .gl, .sys };\n.proxykind = { .alias, .async, .async.global, .async.shared::{cta, cluster} };\n.op_restrict = { .mbarrier_init };\n.to_proxykind::from_proxykind = {.tensormap::generic};\n.to_proxykind::from_proxykind_fabric = {.generic::fabric, .fabric::generic, .fabric::fabric };\n.sem_fabric = { .acquire, .release };"
                ))),
                Version(PositiveInt(1), NonNegativeInt.Zero),
                Target(20),
                Vec(
                  InstructionForm(
                    Opcode("membar"),
                    Vec(),
                    Vec(ModifierGroup(
                      ModifierName("cta"),
                      ModifierName("gl"),
                      ModifierName("sys")
                    )),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Opcode("membar"),
                    Vec(),
                    Vec(
                      ModifierGroup(ModifierName("proxy")),
                      ModifierGroup(
                        ModifierName("alias"),
                        ModifierName("async"),
                        ModifierName("cluster")
                      )
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  )
                )
              )
            )))
        )
    case Min extends Definition(
          Opcode("min"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 3 || operands.size == 4)
              Right(Instruction.Min(operands(0), operands(1), operands(2), operands.lift(3), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("min", Vec(3, 4), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("integer-arithmetic-instructions-min"),
                NonEmptyString("9.7.1.13. Integer Arithmetic Instructions: min"),
                NonEmptyString("min"),
                NonEmptyString("Integer Arithmetic Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(NonEmptyString(
                  "min.type1         d, a, b;\nmin{.relu}.type2  d, a, b;\n\n.type1 = { .u16, .u32, .u64,\n           .u16x2, .u8x4, .s16, .s64 };\n.type2 = { .s16x2, .s32, .s8x4 };"
                ))),
                Version(PositiveInt(1), NonNegativeInt.Zero),
                Target(10),
                Vec(
                  InstructionForm(
                    Opcode("min"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(ModifierGroup(
                      ModifierName("s16"),
                      ModifierName("s64"),
                      ModifierName("u16"),
                      ModifierName("u16x2"),
                      ModifierName("u32"),
                      ModifierName("u64"),
                      ModifierName("u8x4")
                    )),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Opcode("min"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(ModifierGroup(
                      ModifierName("s16x2"),
                      ModifierName("s32"),
                      ModifierName("s8x4")
                    )),
                    _root_.scala.collection.immutable.Set(ModifierName("relu")),
                    false
                  )
                )
              ),
              Metadata.Entry(
                NonEmptyString("floating-point-instructions-min"),
                NonEmptyString("9.7.3.11. Floating Point Instructions: min"),
                NonEmptyString("min"),
                NonEmptyString("Floating Point Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(NonEmptyString(
                  "min{.ftz}{.NaN}{.xorsign.abs}.f32  d, a, b;\nmin{.ftz}{.NaN}{.abs}.f32          d, a, b, c;\nmin.f64                            d, a, b;"
                ))),
                Version(PositiveInt(1), NonNegativeInt.Zero),
                Target(13),
                Vec(
                  InstructionForm(
                    Opcode("min"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(ModifierGroup(ModifierName("f32"))),
                    _root_.scala.collection.immutable.Set(
                      ModifierName("NaN"),
                      ModifierName("abs"),
                      ModifierName("ftz"),
                      ModifierName("xorsign")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode("min"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(ModifierGroup(ModifierName("f32"))),
                    _root_.scala.collection.immutable.Set(
                      ModifierName("NaN"),
                      ModifierName("abs"),
                      ModifierName("ftz")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode("min"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(ModifierGroup(ModifierName("f64"))),
                    _root_.scala.collection.immutable.Set(),
                    false
                  )
                )
              ),
              Metadata.Entry(
                NonEmptyString("half-precision-floating-point-instructions-min"),
                NonEmptyString("9.7.4.7. Half Precision Floating Point Instructions: min"),
                NonEmptyString("min"),
                NonEmptyString("Half Precision Floating Point Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(NonEmptyString(
                  "min{.ftz}{.NaN}{.xorsign.abs}.f16      d, a, b;\nmin{.ftz}{.NaN}{.xorsign.abs}.f16x2    d, a, b;\nmin{.NaN}{.xorsign.abs}.bf16           d, a, b;\nmin{.NaN}{.xorsign.abs}.bf16x2         d, a, b;"
                ))),
                Version(PositiveInt(7), NonNegativeInt.Zero),
                Target(80),
                Vec(
                  InstructionForm(
                    Opcode("min"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(ModifierGroup(ModifierName("f16"))),
                    _root_.scala.collection.immutable.Set(
                      ModifierName("NaN"),
                      ModifierName("abs"),
                      ModifierName("ftz"),
                      ModifierName("xorsign")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode("min"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(ModifierGroup(ModifierName("f16x2"))),
                    _root_.scala.collection.immutable.Set(
                      ModifierName("NaN"),
                      ModifierName("abs"),
                      ModifierName("ftz"),
                      ModifierName("xorsign")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode("min"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(ModifierGroup(ModifierName("bf16"))),
                    _root_.scala.collection.immutable.Set(
                      ModifierName("NaN"),
                      ModifierName("abs"),
                      ModifierName("xorsign")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode("min"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(ModifierGroup(ModifierName("bf16x2"))),
                    _root_.scala.collection.immutable.Set(
                      ModifierName("NaN"),
                      ModifierName("abs"),
                      ModifierName("xorsign")
                    ),
                    false
                  )
                )
              )
            )))
        )
    case Mov extends Definition(
          Opcode("mov"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 2) Right(Instruction.Mov(operands(0), operands(1), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("mov", Vec(2), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("data-movement-and-conversion-instructions-mov"),
                NonEmptyString("9.7.9.3. Data Movement and Conversion Instructions: mov"),
                NonEmptyString("mov"),
                NonEmptyString("Data Movement and Conversion Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(NonEmptyString(
                  "mov.type  d, a;\nmov.type  d, sreg;\nmov.type  d, avar;       // get address of variable\nmov.type  d, avar+imm;   // get address of variable with offset\nmov.u32   d, fname;      // get address of device function\nmov.u64   d, fname;      // get address of device function\nmov.u32   d, kernel;     // get address of entry function\nmov.u64   d, kernel;     // get address of entry function\n\n.type = { .pred,\n          .b16, .b32, .b64,\n          .u16, .u32, .u64,\n          .s16, .s32, .s64,\n                .f32, .f64 };"
                ))),
                Version(PositiveInt(1), NonNegativeInt.Zero),
                Target(13),
                Vec(
                  InstructionForm(
                    Opcode("mov"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(ModifierGroup(
                      ModifierName("b16"),
                      ModifierName("b32"),
                      ModifierName("b64"),
                      ModifierName("f32"),
                      ModifierName("f64"),
                      ModifierName("pred"),
                      ModifierName("s16"),
                      ModifierName("s32"),
                      ModifierName("s64"),
                      ModifierName("u16"),
                      ModifierName("u32"),
                      ModifierName("u64")
                    )),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Opcode("mov"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(ModifierGroup(
                      ModifierName("b16"),
                      ModifierName("b32"),
                      ModifierName("b64"),
                      ModifierName("f32"),
                      ModifierName("f64"),
                      ModifierName("pred"),
                      ModifierName("s16"),
                      ModifierName("s32"),
                      ModifierName("s64"),
                      ModifierName("u16"),
                      ModifierName("u32"),
                      ModifierName("u64")
                    )),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Opcode("mov"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(ModifierGroup(
                      ModifierName("b16"),
                      ModifierName("b32"),
                      ModifierName("b64"),
                      ModifierName("f32"),
                      ModifierName("f64"),
                      ModifierName("pred"),
                      ModifierName("s16"),
                      ModifierName("s32"),
                      ModifierName("s64"),
                      ModifierName("u16"),
                      ModifierName("u32"),
                      ModifierName("u64")
                    )),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Opcode("mov"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(ModifierGroup(
                      ModifierName("b16"),
                      ModifierName("b32"),
                      ModifierName("b64"),
                      ModifierName("f32"),
                      ModifierName("f64"),
                      ModifierName("pred"),
                      ModifierName("s16"),
                      ModifierName("s32"),
                      ModifierName("s64"),
                      ModifierName("u16"),
                      ModifierName("u32"),
                      ModifierName("u64")
                    )),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Opcode("mov"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(ModifierGroup(ModifierName("u32"))),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Opcode("mov"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(ModifierGroup(ModifierName("u64"))),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Opcode("mov"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(ModifierGroup(ModifierName("u32"))),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Opcode("mov"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(ModifierGroup(ModifierName("u64"))),
                    _root_.scala.collection.immutable.Set(),
                    false
                  )
                )
              ),
              Metadata.Entry(
                NonEmptyString("data-movement-and-conversion-instructions-mov-2"),
                NonEmptyString("9.7.9.4. Data Movement and Conversion Instructions: mov"),
                NonEmptyString("mov"),
                NonEmptyString("Data Movement and Conversion Instructions"),
                NonEmptyVec.unsafeFromVec(
                  Vec(NonEmptyString("mov.type  d, a;\n\n.type = { .b16, .b32, .b64, .b128 };"))
                ),
                Version(PositiveInt(1), NonNegativeInt.Zero),
                Target(10),
                Vec(InstructionForm(
                  Opcode("mov"),
                  Vec(OperandConstraint.Register, OperandConstraint.Value),
                  Vec(ModifierGroup(
                    ModifierName("b128"),
                    ModifierName("b16"),
                    ModifierName("b32"),
                    ModifierName("b64")
                  )),
                  _root_.scala.collection.immutable.Set(),
                  false
                ))
              )
            )))
        )
    case Mul extends Definition(
          Opcode("mul"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 3) Right(Instruction.Mul(operands(0), operands(1), operands(2), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("mul", Vec(3), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("integer-arithmetic-instructions-mul"),
                NonEmptyString("9.7.1.3. Integer Arithmetic Instructions: mul"),
                NonEmptyString("mul"),
                NonEmptyString("Integer Arithmetic Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(NonEmptyString(
                  "mul.mode.type  d, a, b;\n\n.mode = { .hi, .lo, .wide };\n.type = { .u16, .u32, .u64,\n          .s16, .s32, .s64 };"
                ))),
                Version(PositiveInt(1), NonNegativeInt.Zero),
                Target(10),
                Vec(InstructionForm(
                  Opcode("mul"),
                  Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                  Vec(
                    ModifierGroup(
                      ModifierName("hi"),
                      ModifierName("lo"),
                      ModifierName("wide")
                    ),
                    ModifierGroup(
                      ModifierName("s16"),
                      ModifierName("s32"),
                      ModifierName("s64"),
                      ModifierName("u16"),
                      ModifierName("u32"),
                      ModifierName("u64")
                    )
                  ),
                  _root_.scala.collection.immutable.Set(),
                  true
                ))
              ),
              Metadata.Entry(
                NonEmptyString("floating-point-instructions-mul"),
                NonEmptyString("9.7.3.5. Floating Point Instructions: mul"),
                NonEmptyString("mul"),
                NonEmptyString("Floating Point Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(NonEmptyString(
                  "mul{.rnd}{.ftz}{.sat}.f32  d, a, b;\nmul{.rnd}{.ftz}.f32x2      d, a, b;\nmul{.rnd}.f64              d, a, b;\n\n.rnd = { .rn, .rz, .rm, .rp };"
                ))),
                Version(PositiveInt(1), NonNegativeInt.Zero),
                Target(13),
                Vec(
                  InstructionForm(
                    Opcode("mul"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(ModifierGroup(ModifierName("f32"))),
                    _root_.scala.collection.immutable.Set(
                      ModifierName("ftz"),
                      ModifierName("rm"),
                      ModifierName("rn"),
                      ModifierName("rp"),
                      ModifierName("rz"),
                      ModifierName("sat")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode("mul"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(ModifierGroup(ModifierName("f32x2"))),
                    _root_.scala.collection.immutable.Set(
                      ModifierName("ftz"),
                      ModifierName("rm"),
                      ModifierName("rn"),
                      ModifierName("rp"),
                      ModifierName("rz")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode("mul"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(ModifierGroup(ModifierName("f64"))),
                    _root_.scala.collection.immutable.Set(
                      ModifierName("rm"),
                      ModifierName("rn"),
                      ModifierName("rp"),
                      ModifierName("rz")
                    ),
                    false
                  )
                )
              ),
              Metadata.Entry(
                NonEmptyString("half-precision-floating-point-instructions-mul"),
                NonEmptyString("9.7.4.3. Half Precision Floating Point Instructions: mul"),
                NonEmptyString("mul"),
                NonEmptyString("Half Precision Floating Point Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(NonEmptyString(
                  "mul{.rnd}{.ftz}{.sat}.f16   d, a, b;\nmul{.rnd}{.ftz}{.sat}.f16x2 d, a, b;\n\nmul{.rnd}.bf16   d, a, b;\nmul{.rnd}.bf16x2 d, a, b;\n\n.rnd = { .rn };"
                ))),
                Version(PositiveInt(4), NonNegativeInt(2)),
                Target(53),
                Vec(
                  InstructionForm(
                    Opcode("mul"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(ModifierGroup(ModifierName("f16"))),
                    _root_.scala.collection.immutable.Set(
                      ModifierName("ftz"),
                      ModifierName("rn"),
                      ModifierName("sat")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode("mul"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(ModifierGroup(ModifierName("f16x2"))),
                    _root_.scala.collection.immutable.Set(
                      ModifierName("ftz"),
                      ModifierName("rn"),
                      ModifierName("sat")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode("mul"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(ModifierGroup(ModifierName("bf16"))),
                    _root_.scala.collection.immutable.Set(ModifierName("rn")),
                    false
                  ),
                  InstructionForm(
                    Opcode("mul"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(ModifierGroup(ModifierName("bf16x2"))),
                    _root_.scala.collection.immutable.Set(ModifierName("rn")),
                    false
                  )
                )
              )
            )))
        )
    case Mul24 extends Definition(
          Opcode("mul24"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 3) Right(Instruction.Mul24(operands(0), operands(1), operands(2), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("mul24", Vec(3), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("integer-arithmetic-instructions-mul24"),
                NonEmptyString("9.7.1.6. Integer Arithmetic Instructions: mul24"),
                NonEmptyString("mul24"),
                NonEmptyString("Integer Arithmetic Instructions"),
                NonEmptyVec.unsafeFromVec(
                  Vec(NonEmptyString("mul24.mode.type  d, a, b;\n\n.mode = { .hi, .lo };\n.type = { .u32, .s32 };"))
                ),
                Version(PositiveInt(1), NonNegativeInt.Zero),
                Target(10),
                Vec(InstructionForm(
                  Opcode("mul24"),
                  Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                  Vec(
                    ModifierGroup(
                      ModifierName("hi"),
                      ModifierName("lo")
                    ),
                    ModifierGroup(
                      ModifierName("s32"),
                      ModifierName("u32")
                    )
                  ),
                  _root_.scala.collection.immutable.Set(),
                  true
                ))
              )
            )))
        )
    case MultimemCpAsyncBulk extends Definition(
          Opcode("multimem.cp.async.bulk"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 3 || operands.size == 4) Right(Instruction.MultimemCpAsyncBulk(
              operands(0),
              operands(1),
              operands(2),
              operands.lift(3),
              mods,
              pred
            ))
            else Left(Instruction.DecodeError.InvalidOperandCount("multimem.cp.async.bulk", Vec(3, 4), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("data-movement-and-conversion-instructions-multimem-cp-async-bulk"),
                NonEmptyString("9.7.9.26.4.4. Data Movement and Conversion Instructions: multimem.cp.async.bulk"),
                NonEmptyString("multimem.cp.async.bulk"),
                NonEmptyString("Data Movement and Conversion Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(NonEmptyString(
                  "multimem.cp.async.bulk{.sem}.dst.src.completion_mechanism{.cp_mask}\n    [dstMem], [srcMem], size{, byteMask};\n\n    .sem                  = { .weak }\n    .dst                  = { .global }\n    .src                  = { .shared::cta }\n    .completion_mechanism = { .bulk_group }\n\nmultimem.cp.async.bulk.sem.scope.dst.src.completion_mechanism{.cp_mask}.type\n    [dstMem], [srcMem], size{, byteMask};\n\n    .sem                  = { .relaxed }\n    .scope                = { .cta, .cluster, .gpu, .sys }\n    .dst                  = { .global }\n    .src                  = { .shared::cta }\n    .completion_mechanism = { .bulk_group }\n    .type                 = { .b128 }"
                ))),
                Version(PositiveInt(9), NonNegativeInt(1)),
                Target(90),
                Vec(
                  InstructionForm(
                    Opcode("multimem.cp.async.bulk"),
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(ModifierName("global")),
                      ModifierGroup(ModifierName("shared::cta")),
                      ModifierGroup(ModifierName("bulk_group"))
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName("cp_mask"),
                      ModifierName("relaxed")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode("multimem.cp.async.bulk"),
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(ModifierName("relaxed")),
                      ModifierGroup(
                        ModifierName("cluster"),
                        ModifierName("cta"),
                        ModifierName("gpu"),
                        ModifierName("sys")
                      ),
                      ModifierGroup(ModifierName("global")),
                      ModifierGroup(ModifierName("shared::cta")),
                      ModifierGroup(ModifierName("bulk_group")),
                      ModifierGroup(ModifierName("b128"))
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName("cp_mask")),
                    false
                  )
                )
              )
            )))
        )
    case MultimemCpReduceAsyncBulk extends Definition(
          Opcode("multimem.cp.reduce.async.bulk"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 3)
              Right(Instruction.MultimemCpReduceAsyncBulk(operands(0), operands(1), operands(2), mods, pred))
            else
              Left(Instruction.DecodeError.InvalidOperandCount("multimem.cp.reduce.async.bulk", Vec(3), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("data-movement-and-conversion-instructions-multimem-cp-reduce-async-bulk"),
                NonEmptyString(
                  "9.7.9.26.4.5. Data Movement and Conversion Instructions: multimem.cp.reduce.async.bulk"
                ),
                NonEmptyString("multimem.cp.reduce.async.bulk"),
                NonEmptyString("Data Movement and Conversion Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(NonEmptyString(
                  "multimem.cp.reduce.async.bulk{.sem.scope}.dst.src.completion_mechanism.redOp.type  [dstMem], [srcMem], size;\n\n    .sem                  =  { .relaxed }\n    .scope                = { .cta, .cluster, .gpu, .sys }\n    .dst                  = { .global }\n    .src                  = { .shared::cta }\n    .completion_mechanism = { .bulk_group }\n    .redOp                = { .and, .or, .xor,\n                              .add, .inc, .dec,\n                              .min, .max }\n    .type                 = { .f16, .bf16,\n                              .b32, .u32, .s32,\n                              .b64, .u64, .s64,\n                              .f32, .f64 }\n\nmultimem.cp.reduce.async.bulk{.sem.scope}.dst.src.completion_mechanism.add.noftz.type  [dstMem], [srcMem], size;\n\n    .sem                  = { .relaxed }\n    .scope                = { .cta, .cluster, .gpu, .sys }\n    .dst                  = { .global }\n    .src                  = { .shared::cta }\n    .completion_mechanism = { .bulk_group }\n    .type                 = { .f16, .bf16 }"
                ))),
                Version(PositiveInt(9), NonNegativeInt(1)),
                Target(90),
                Vec(
                  InstructionForm(
                    Opcode("multimem.cp.reduce.async.bulk"),
                    Vec(OperandConstraint.Address, OperandConstraint.Address, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(ModifierName("global")),
                      ModifierGroup(ModifierName("shared::cta")),
                      ModifierGroup(ModifierName("bulk_group")),
                      ModifierGroup(
                        ModifierName("add"),
                        ModifierName("and"),
                        ModifierName("dec"),
                        ModifierName("inc"),
                        ModifierName("max"),
                        ModifierName("min"),
                        ModifierName("or"),
                        ModifierName("xor")
                      ),
                      ModifierGroup(
                        ModifierName("bf16"),
                        ModifierName("f16")
                      )
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName("cluster"),
                      ModifierName("cta"),
                      ModifierName("gpu"),
                      ModifierName("relaxed"),
                      ModifierName("sys")
                    ),
                    true
                  ),
                  InstructionForm(
                    Opcode("multimem.cp.reduce.async.bulk"),
                    Vec(OperandConstraint.Address, OperandConstraint.Address, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(ModifierName("global")),
                      ModifierGroup(ModifierName("shared::cta")),
                      ModifierGroup(ModifierName("bulk_group")),
                      ModifierGroup(ModifierName("add")),
                      ModifierGroup(ModifierName("noftz")),
                      ModifierGroup(
                        ModifierName("bf16"),
                        ModifierName("f16")
                      )
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName("cluster"),
                      ModifierName("cta"),
                      ModifierName("gpu"),
                      ModifierName("relaxed"),
                      ModifierName("sys")
                    ),
                    true
                  )
                )
              )
            )))
        )
    case MultimemLdReduce extends Definition(
          Opcode("multimem.ld_reduce"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 2) Right(Instruction.MultimemLdReduce(operands(0), operands(1), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("multimem.ld_reduce", Vec(2), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("data-movement-and-conversion-instructions-multimem"),
                NonEmptyString(
                  "9.7.9.15. Data Movement and Conversion Instructions: multimem.ld_reduce, multimem.st, multimem.red"
                ),
                NonEmptyString("multimem.ld_reduce, multimem.st, multimem.red"),
                NonEmptyString("Data Movement and Conversion Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(NonEmptyString(
                  "// Integer type:\n\nmultimem.ld_reduce{.ldsem}{.scope}{.ss}.op.type      d, [a];\nmultimem.ld_reduce.weak{.ss}.op.type                 d, [a];\n\nmultimem.st{.stsem}{.scope}{.ss}.type                [a], b;\nmultimem.st.weak{.ss}.type                           [a], b;\n\nmultimem.red{.redsem}{.scope}{.ss}.op.type           [a], b;\n\n.ss =       { .global }\n.ldsem =    { .relaxed, .acquire }\n.stsem =    { .relaxed, .release }\n.redsem =   { .relaxed, .release }\n.scope =    { .cta, .cluster, .gpu, .sys }\n.op  =      { .min, .max, .add, .and, .or, .xor }\n.type =     { .b32, .b64,  .u32, .u64, .s32, .s64 }\n\n// Floating point type:\n\nmultimem.ld_reduce{.ldsem}{.scope}{.ss}.op{.acc_prec}{.vec}.type    d, [a];\nmultimem.ld_reduce.weak{.ss}.op{.acc_prec}{.vec}.type               d, [a];\n\nmultimem.st{.stsem}{.scope}{.ss}{.vec}.type                         [a], b;\nmultimem.st.weak{.ss}{.vec}.type                                    [a], b;\n\nmultimem.red{.redsem}{.scope}{.ss}.redop{.vec}.redtype              [a], b;\n\n.ss =       { .global }\n.ldsem =    { .relaxed, .acquire }\n.stsem =    { .relaxed, .release }\n.redsem =   { .relaxed, .release }\n.scope =    { .cta, .cluster, .gpu, .sys }\n.op  =      { .min, .max, .add }\n.redop  =   { .add }\n.acc_prec = { .acc::f32, .acc::f16 }\n.vec =      { .v2, .v4, .v8 }\n.type=      { .f16, .f16x2, .bf16, .bf16x2, .f32, .f64, .e5m2, .e5m2x2, .e5m2x4, .e4m3, .e4m3x2, .e4m3x4 }\n.redtype =  { .f16, .f16x2, .bf16, .bf16x2, .f32, .f64 }"
                ))),
                Version(PositiveInt(8), NonNegativeInt(1)),
                Target(90),
                Vec(
                  InstructionForm(
                    Opcode("multimem.ld_reduce"),
                    Vec(OperandConstraint.Register, OperandConstraint.Address),
                    Vec(
                      ModifierGroup(
                        ModifierName("add"),
                        ModifierName("max"),
                        ModifierName("min")
                      ),
                      ModifierGroup(
                        ModifierName("bf16"),
                        ModifierName("bf16x2"),
                        ModifierName("e4m3"),
                        ModifierName("e4m3x2"),
                        ModifierName("e4m3x4"),
                        ModifierName("e5m2"),
                        ModifierName("e5m2x2"),
                        ModifierName("e5m2x4"),
                        ModifierName("f16"),
                        ModifierName("f16x2"),
                        ModifierName("f32"),
                        ModifierName("f64")
                      )
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName("acquire"),
                      ModifierName("cluster"),
                      ModifierName("cta"),
                      ModifierName("global"),
                      ModifierName("gpu"),
                      ModifierName("relaxed"),
                      ModifierName("sys")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode("multimem.ld_reduce"),
                    Vec(OperandConstraint.Register, OperandConstraint.Address),
                    Vec(
                      ModifierGroup(ModifierName("weak")),
                      ModifierGroup(
                        ModifierName("add"),
                        ModifierName("max"),
                        ModifierName("min")
                      ),
                      ModifierGroup(
                        ModifierName("bf16"),
                        ModifierName("bf16x2"),
                        ModifierName("e4m3"),
                        ModifierName("e4m3x2"),
                        ModifierName("e4m3x4"),
                        ModifierName("e5m2"),
                        ModifierName("e5m2x2"),
                        ModifierName("e5m2x4"),
                        ModifierName("f16"),
                        ModifierName("f16x2"),
                        ModifierName("f32"),
                        ModifierName("f64")
                      )
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName("global")),
                    false
                  ),
                  InstructionForm(
                    Opcode("multimem.ld_reduce"),
                    Vec(OperandConstraint.Register, OperandConstraint.Address),
                    Vec(
                      ModifierGroup(
                        ModifierName("add"),
                        ModifierName("max"),
                        ModifierName("min")
                      ),
                      ModifierGroup(
                        ModifierName("bf16"),
                        ModifierName("bf16x2"),
                        ModifierName("e4m3"),
                        ModifierName("e4m3x2"),
                        ModifierName("e4m3x4"),
                        ModifierName("e5m2"),
                        ModifierName("e5m2x2"),
                        ModifierName("e5m2x4"),
                        ModifierName("f16"),
                        ModifierName("f16x2"),
                        ModifierName("f32"),
                        ModifierName("f64")
                      )
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName("acc::f16"),
                      ModifierName("acc::f32"),
                      ModifierName("acquire"),
                      ModifierName("cluster"),
                      ModifierName("cta"),
                      ModifierName("global"),
                      ModifierName("gpu"),
                      ModifierName("relaxed"),
                      ModifierName("sys"),
                      ModifierName("v2"),
                      ModifierName("v4"),
                      ModifierName("v8")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode("multimem.ld_reduce"),
                    Vec(OperandConstraint.Register, OperandConstraint.Address),
                    Vec(
                      ModifierGroup(ModifierName("weak")),
                      ModifierGroup(
                        ModifierName("add"),
                        ModifierName("max"),
                        ModifierName("min")
                      ),
                      ModifierGroup(
                        ModifierName("bf16"),
                        ModifierName("bf16x2"),
                        ModifierName("e4m3"),
                        ModifierName("e4m3x2"),
                        ModifierName("e4m3x4"),
                        ModifierName("e5m2"),
                        ModifierName("e5m2x2"),
                        ModifierName("e5m2x4"),
                        ModifierName("f16"),
                        ModifierName("f16x2"),
                        ModifierName("f32"),
                        ModifierName("f64")
                      )
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName("acc::f16"),
                      ModifierName("acc::f32"),
                      ModifierName("global"),
                      ModifierName("v2"),
                      ModifierName("v4"),
                      ModifierName("v8")
                    ),
                    false
                  )
                )
              )
            )))
        )
    case MultimemRed extends Definition(
          Opcode("multimem.red"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 2) Right(Instruction.MultimemRed(operands(0), operands(1), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("multimem.red", Vec(2), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("data-movement-and-conversion-instructions-multimem"),
                NonEmptyString(
                  "9.7.9.15. Data Movement and Conversion Instructions: multimem.ld_reduce, multimem.st, multimem.red"
                ),
                NonEmptyString("multimem.ld_reduce, multimem.st, multimem.red"),
                NonEmptyString("Data Movement and Conversion Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(NonEmptyString(
                  "// Integer type:\n\nmultimem.ld_reduce{.ldsem}{.scope}{.ss}.op.type      d, [a];\nmultimem.ld_reduce.weak{.ss}.op.type                 d, [a];\n\nmultimem.st{.stsem}{.scope}{.ss}.type                [a], b;\nmultimem.st.weak{.ss}.type                           [a], b;\n\nmultimem.red{.redsem}{.scope}{.ss}.op.type           [a], b;\n\n.ss =       { .global }\n.ldsem =    { .relaxed, .acquire }\n.stsem =    { .relaxed, .release }\n.redsem =   { .relaxed, .release }\n.scope =    { .cta, .cluster, .gpu, .sys }\n.op  =      { .min, .max, .add, .and, .or, .xor }\n.type =     { .b32, .b64,  .u32, .u64, .s32, .s64 }\n\n// Floating point type:\n\nmultimem.ld_reduce{.ldsem}{.scope}{.ss}.op{.acc_prec}{.vec}.type    d, [a];\nmultimem.ld_reduce.weak{.ss}.op{.acc_prec}{.vec}.type               d, [a];\n\nmultimem.st{.stsem}{.scope}{.ss}{.vec}.type                         [a], b;\nmultimem.st.weak{.ss}{.vec}.type                                    [a], b;\n\nmultimem.red{.redsem}{.scope}{.ss}.redop{.vec}.redtype              [a], b;\n\n.ss =       { .global }\n.ldsem =    { .relaxed, .acquire }\n.stsem =    { .relaxed, .release }\n.redsem =   { .relaxed, .release }\n.scope =    { .cta, .cluster, .gpu, .sys }\n.op  =      { .min, .max, .add }\n.redop  =   { .add }\n.acc_prec = { .acc::f32, .acc::f16 }\n.vec =      { .v2, .v4, .v8 }\n.type=      { .f16, .f16x2, .bf16, .bf16x2, .f32, .f64, .e5m2, .e5m2x2, .e5m2x4, .e4m3, .e4m3x2, .e4m3x4 }\n.redtype =  { .f16, .f16x2, .bf16, .bf16x2, .f32, .f64 }"
                ))),
                Version(PositiveInt(8), NonNegativeInt(1)),
                Target(90),
                Vec(
                  InstructionForm(
                    Opcode("multimem.red"),
                    Vec(OperandConstraint.Address, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(
                        ModifierName("add"),
                        ModifierName("max"),
                        ModifierName("min")
                      ),
                      ModifierGroup(
                        ModifierName("bf16"),
                        ModifierName("bf16x2"),
                        ModifierName("e4m3"),
                        ModifierName("e4m3x2"),
                        ModifierName("e4m3x4"),
                        ModifierName("e5m2"),
                        ModifierName("e5m2x2"),
                        ModifierName("e5m2x4"),
                        ModifierName("f16"),
                        ModifierName("f16x2"),
                        ModifierName("f32"),
                        ModifierName("f64")
                      )
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName("cluster"),
                      ModifierName("cta"),
                      ModifierName("global"),
                      ModifierName("gpu"),
                      ModifierName("relaxed"),
                      ModifierName("release"),
                      ModifierName("sys")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode("multimem.red"),
                    Vec(OperandConstraint.Address, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(ModifierName("add")),
                      ModifierGroup(
                        ModifierName("bf16"),
                        ModifierName("bf16x2"),
                        ModifierName("f16"),
                        ModifierName("f16x2"),
                        ModifierName("f32"),
                        ModifierName("f64")
                      )
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName("cluster"),
                      ModifierName("cta"),
                      ModifierName("global"),
                      ModifierName("gpu"),
                      ModifierName("relaxed"),
                      ModifierName("release"),
                      ModifierName("sys"),
                      ModifierName("v2"),
                      ModifierName("v4"),
                      ModifierName("v8")
                    ),
                    false
                  )
                )
              )
            )))
        )
    case MultimemRedAsync extends Definition(
          Opcode("multimem.red.async"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 2) Right(Instruction.MultimemRedAsync(operands(0), operands(1), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("multimem.red.async", Vec(2), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("parallel-synchronization-and-communication-instructions-multimem-red-async"),
                NonEmptyString("9.7.14.8. Parallel Synchronization and Communication Instructions: multimem.red.async"),
                NonEmptyString("multimem.red.async"),
                NonEmptyString("Parallel Synchronization and Communication Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(NonEmptyString(
                  "multimem.red.async.sem.scope{.ss}.op.type [a], b;\n\n.sem   = { .release };\n.scope = { .gpu, .sys };\n.ss    = { .global };\n.op    = { .add };\n.type  = { .u32, .s32, .u64 };"
                ))),
                Version(PositiveInt(9), NonNegativeInt(3)),
                Target(100),
                Vec(InstructionForm(
                  Opcode("multimem.red.async"),
                  Vec(OperandConstraint.Address, OperandConstraint.Value),
                  Vec(
                    ModifierGroup(ModifierName("release")),
                    ModifierGroup(
                      ModifierName("gpu"),
                      ModifierName("sys")
                    ),
                    ModifierGroup(ModifierName("add")),
                    ModifierGroup(
                      ModifierName("s32"),
                      ModifierName("u32"),
                      ModifierName("u64")
                    )
                  ),
                  _root_.scala.collection.immutable.Set(ModifierName("global")),
                  true
                ))
              )
            )))
        )
    case MultimemSt extends Definition(
          Opcode("multimem.st"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 2) Right(Instruction.MultimemSt(operands(0), operands(1), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("multimem.st", Vec(2), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("data-movement-and-conversion-instructions-multimem"),
                NonEmptyString(
                  "9.7.9.15. Data Movement and Conversion Instructions: multimem.ld_reduce, multimem.st, multimem.red"
                ),
                NonEmptyString("multimem.ld_reduce, multimem.st, multimem.red"),
                NonEmptyString("Data Movement and Conversion Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(NonEmptyString(
                  "// Integer type:\n\nmultimem.ld_reduce{.ldsem}{.scope}{.ss}.op.type      d, [a];\nmultimem.ld_reduce.weak{.ss}.op.type                 d, [a];\n\nmultimem.st{.stsem}{.scope}{.ss}.type                [a], b;\nmultimem.st.weak{.ss}.type                           [a], b;\n\nmultimem.red{.redsem}{.scope}{.ss}.op.type           [a], b;\n\n.ss =       { .global }\n.ldsem =    { .relaxed, .acquire }\n.stsem =    { .relaxed, .release }\n.redsem =   { .relaxed, .release }\n.scope =    { .cta, .cluster, .gpu, .sys }\n.op  =      { .min, .max, .add, .and, .or, .xor }\n.type =     { .b32, .b64,  .u32, .u64, .s32, .s64 }\n\n// Floating point type:\n\nmultimem.ld_reduce{.ldsem}{.scope}{.ss}.op{.acc_prec}{.vec}.type    d, [a];\nmultimem.ld_reduce.weak{.ss}.op{.acc_prec}{.vec}.type               d, [a];\n\nmultimem.st{.stsem}{.scope}{.ss}{.vec}.type                         [a], b;\nmultimem.st.weak{.ss}{.vec}.type                                    [a], b;\n\nmultimem.red{.redsem}{.scope}{.ss}.redop{.vec}.redtype              [a], b;\n\n.ss =       { .global }\n.ldsem =    { .relaxed, .acquire }\n.stsem =    { .relaxed, .release }\n.redsem =   { .relaxed, .release }\n.scope =    { .cta, .cluster, .gpu, .sys }\n.op  =      { .min, .max, .add }\n.redop  =   { .add }\n.acc_prec = { .acc::f32, .acc::f16 }\n.vec =      { .v2, .v4, .v8 }\n.type=      { .f16, .f16x2, .bf16, .bf16x2, .f32, .f64, .e5m2, .e5m2x2, .e5m2x4, .e4m3, .e4m3x2, .e4m3x4 }\n.redtype =  { .f16, .f16x2, .bf16, .bf16x2, .f32, .f64 }"
                ))),
                Version(PositiveInt(8), NonNegativeInt(1)),
                Target(90),
                Vec(
                  InstructionForm(
                    Opcode("multimem.st"),
                    Vec(OperandConstraint.Address, OperandConstraint.Value),
                    Vec(ModifierGroup(
                      ModifierName("bf16"),
                      ModifierName("bf16x2"),
                      ModifierName("e4m3"),
                      ModifierName("e4m3x2"),
                      ModifierName("e4m3x4"),
                      ModifierName("e5m2"),
                      ModifierName("e5m2x2"),
                      ModifierName("e5m2x4"),
                      ModifierName("f16"),
                      ModifierName("f16x2"),
                      ModifierName("f32"),
                      ModifierName("f64")
                    )),
                    _root_.scala.collection.immutable.Set(
                      ModifierName("cluster"),
                      ModifierName("cta"),
                      ModifierName("global"),
                      ModifierName("gpu"),
                      ModifierName("relaxed"),
                      ModifierName("release"),
                      ModifierName("sys")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode("multimem.st"),
                    Vec(OperandConstraint.Address, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(ModifierName("weak")),
                      ModifierGroup(
                        ModifierName("bf16"),
                        ModifierName("bf16x2"),
                        ModifierName("e4m3"),
                        ModifierName("e4m3x2"),
                        ModifierName("e4m3x4"),
                        ModifierName("e5m2"),
                        ModifierName("e5m2x2"),
                        ModifierName("e5m2x4"),
                        ModifierName("f16"),
                        ModifierName("f16x2"),
                        ModifierName("f32"),
                        ModifierName("f64")
                      )
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName("global")),
                    false
                  ),
                  InstructionForm(
                    Opcode("multimem.st"),
                    Vec(OperandConstraint.Address, OperandConstraint.Value),
                    Vec(ModifierGroup(
                      ModifierName("bf16"),
                      ModifierName("bf16x2"),
                      ModifierName("e4m3"),
                      ModifierName("e4m3x2"),
                      ModifierName("e4m3x4"),
                      ModifierName("e5m2"),
                      ModifierName("e5m2x2"),
                      ModifierName("e5m2x4"),
                      ModifierName("f16"),
                      ModifierName("f16x2"),
                      ModifierName("f32"),
                      ModifierName("f64")
                    )),
                    _root_.scala.collection.immutable.Set(
                      ModifierName("cluster"),
                      ModifierName("cta"),
                      ModifierName("global"),
                      ModifierName("gpu"),
                      ModifierName("relaxed"),
                      ModifierName("release"),
                      ModifierName("sys"),
                      ModifierName("v2"),
                      ModifierName("v4"),
                      ModifierName("v8")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode("multimem.st"),
                    Vec(OperandConstraint.Address, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(ModifierName("weak")),
                      ModifierGroup(
                        ModifierName("bf16"),
                        ModifierName("bf16x2"),
                        ModifierName("e4m3"),
                        ModifierName("e4m3x2"),
                        ModifierName("e4m3x4"),
                        ModifierName("e5m2"),
                        ModifierName("e5m2x2"),
                        ModifierName("e5m2x4"),
                        ModifierName("f16"),
                        ModifierName("f16x2"),
                        ModifierName("f32"),
                        ModifierName("f64")
                      )
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName("global"),
                      ModifierName("v2"),
                      ModifierName("v4"),
                      ModifierName("v8")
                    ),
                    false
                  )
                )
              )
            )))
        )
    case MultimemStAsync extends Definition(
          Opcode("multimem.st.async"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 2) Right(Instruction.MultimemStAsync(operands(0), operands(1), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("multimem.st.async", Vec(2), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("data-movement-and-conversion-instructions-multimem-st-async"),
                NonEmptyString("9.7.9.13. Data Movement and Conversion Instructions: multimem.st.async"),
                NonEmptyString("multimem.st.async"),
                NonEmptyString("Data Movement and Conversion Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(NonEmptyString(
                  "multimem.st.async.sem.scope{.ss}.type [a], b;\n\n.sem   = { .release };\n.scope = { .gpu, .sys };\n.ss    = { .global };\n.type  = { .b8, .b16, .b32, .b64,\n            .u8, .u16, .u32, .u64,\n            .s8, .s16, .s32, .s64,\n            .f32, .f64 };"
                ))),
                Version(PositiveInt(9), NonNegativeInt(3)),
                Target(100),
                Vec(InstructionForm(
                  Opcode("multimem.st.async"),
                  Vec(OperandConstraint.Address, OperandConstraint.Value),
                  Vec(
                    ModifierGroup(ModifierName("release")),
                    ModifierGroup(
                      ModifierName("gpu"),
                      ModifierName("sys")
                    ),
                    ModifierGroup(
                      ModifierName("b16"),
                      ModifierName("b32"),
                      ModifierName("b64"),
                      ModifierName("b8"),
                      ModifierName("f32"),
                      ModifierName("f64"),
                      ModifierName("s16"),
                      ModifierName("s32"),
                      ModifierName("s64"),
                      ModifierName("s8"),
                      ModifierName("u16"),
                      ModifierName("u32"),
                      ModifierName("u64"),
                      ModifierName("u8")
                    )
                  ),
                  _root_.scala.collection.immutable.Set(ModifierName("global")),
                  true
                ))
              )
            )))
        )
    case NanoSleep extends Definition(
          Opcode("nanosleep"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 1) Right(Instruction.NanoSleep(operands(0), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("nanosleep", Vec(1), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("miscellaneous-instructions-nanosleep"),
                NonEmptyString("9.7.20.2. Miscellaneous Instructions: nanosleep"),
                NonEmptyString("nanosleep"),
                NonEmptyString("Miscellaneous Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(NonEmptyString("nanosleep.u32 t;"))),
                Version(PositiveInt(1), NonNegativeInt.Zero),
                Target(70),
                Vec(InstructionForm(
                  Opcode("nanosleep"),
                  Vec(OperandConstraint.Value),
                  Vec(ModifierGroup(ModifierName("u32"))),
                  _root_.scala.collection.immutable.Set(),
                  true
                ))
              )
            )))
        )
    case Neg extends Definition(
          Opcode("neg"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 2) Right(Instruction.Neg(operands(0), operands(1), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("neg", Vec(2), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("integer-arithmetic-instructions-neg"),
                NonEmptyString("9.7.1.12. Integer Arithmetic Instructions: neg"),
                NonEmptyString("neg"),
                NonEmptyString("Integer Arithmetic Instructions"),
                NonEmptyVec.unsafeFromVec(
                  Vec(NonEmptyString("neg.type  d, a;\n\n.type = { .s8x4, .s16, .s32, .s64 };"))
                ),
                Version(PositiveInt(1), NonNegativeInt.Zero),
                Target(10),
                Vec(InstructionForm(
                  Opcode("neg"),
                  Vec(OperandConstraint.Register, OperandConstraint.Value),
                  Vec(ModifierGroup(
                    ModifierName("s16"),
                    ModifierName("s32"),
                    ModifierName("s64"),
                    ModifierName("s8x4")
                  )),
                  _root_.scala.collection.immutable.Set(),
                  false
                ))
              ),
              Metadata.Entry(
                NonEmptyString("floating-point-instructions-neg"),
                NonEmptyString("9.7.3.10. Floating Point Instructions: neg"),
                NonEmptyString("neg"),
                NonEmptyString("Floating Point Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(NonEmptyString("neg{.ftz}.f32  d, a;\nneg.f64        d, a;"))),
                Version(PositiveInt(1), NonNegativeInt.Zero),
                Target(13),
                Vec(
                  InstructionForm(
                    Opcode("neg"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(ModifierGroup(ModifierName("f32"))),
                    _root_.scala.collection.immutable.Set(ModifierName("ftz")),
                    true
                  ),
                  InstructionForm(
                    Opcode("neg"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(ModifierGroup(ModifierName("f64"))),
                    _root_.scala.collection.immutable.Set(),
                    true
                  )
                )
              ),
              Metadata.Entry(
                NonEmptyString("half-precision-floating-point-instructions-neg"),
                NonEmptyString("9.7.4.5. Half Precision Floating Point Instructions: neg"),
                NonEmptyString("neg"),
                NonEmptyString("Half Precision Floating Point Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(NonEmptyString(
                  "neg{.ftz}.f16    d, a;\nneg{.ftz}.f16x2  d, a;\nneg.bf16         d, a;\nneg.bf16x2       d, a;"
                ))),
                Version(PositiveInt(6), NonNegativeInt.Zero),
                Target(53),
                Vec(
                  InstructionForm(
                    Opcode("neg"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(ModifierGroup(ModifierName("f16"))),
                    _root_.scala.collection.immutable.Set(ModifierName("ftz")),
                    false
                  ),
                  InstructionForm(
                    Opcode("neg"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(ModifierGroup(ModifierName("f16x2"))),
                    _root_.scala.collection.immutable.Set(ModifierName("ftz")),
                    false
                  ),
                  InstructionForm(
                    Opcode("neg"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(ModifierGroup(ModifierName("bf16"))),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Opcode("neg"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(ModifierGroup(ModifierName("bf16x2"))),
                    _root_.scala.collection.immutable.Set(),
                    false
                  )
                )
              )
            )))
        )
    case Not extends Definition(
          Opcode("not"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 2) Right(Instruction.Not(operands(0), operands(1), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("not", Vec(2), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("logic-and-shift-instructions-not"),
                NonEmptyString("9.7.8.4. Logic and Shift Instructions: not"),
                NonEmptyString("not"),
                NonEmptyString("Logic and Shift Instructions"),
                NonEmptyVec.unsafeFromVec(
                  Vec(NonEmptyString("not.type d, a;\n\n.type = { .pred, .b16, .b32, .b64 };"))
                ),
                Version(PositiveInt(1), NonNegativeInt.Zero),
                Target(10),
                Vec(InstructionForm(
                  Opcode("not"),
                  Vec(OperandConstraint.Register, OperandConstraint.Value),
                  Vec(ModifierGroup(
                    ModifierName("b16"),
                    ModifierName("b32"),
                    ModifierName("b64"),
                    ModifierName("pred")
                  )),
                  _root_.scala.collection.immutable.Set(),
                  true
                ))
              )
            )))
        )
    case Or extends Definition(
          Opcode("or"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 3) Right(Instruction.Or(operands(0), operands(1), operands(2), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("or", Vec(3), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("logic-and-shift-instructions-or"),
                NonEmptyString("9.7.8.2. Logic and Shift Instructions: or"),
                NonEmptyString("or"),
                NonEmptyString("Logic and Shift Instructions"),
                NonEmptyVec.unsafeFromVec(
                  Vec(NonEmptyString("or.type d, a, b;\n\n.type = { .pred, .b16, .b32, .b64 };"))
                ),
                Version(PositiveInt(1), NonNegativeInt.Zero),
                Target(10),
                Vec(InstructionForm(
                  Opcode("or"),
                  Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                  Vec(ModifierGroup(
                    ModifierName("b16"),
                    ModifierName("b32"),
                    ModifierName("b64"),
                    ModifierName("pred")
                  )),
                  _root_.scala.collection.immutable.Set(),
                  true
                ))
              )
            )))
        )
    case PmEvent extends Definition(
          Opcode("pmevent"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 1) Right(Instruction.PmEvent(operands(0), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("pmevent", Vec(1), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("miscellaneous-instructions-pmevent"),
                NonEmptyString("9.7.20.3. Miscellaneous Instructions: pmevent"),
                NonEmptyString("pmevent"),
                NonEmptyString("Miscellaneous Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(NonEmptyString(
                  "pmevent       a;    // trigger a single performance monitor event\npmevent.mask  a;    // trigger one or more performance monitor events"
                ))),
                Version(PositiveInt(1), NonNegativeInt.Zero),
                Target(20),
                Vec(
                  InstructionForm(
                    Opcode("pmevent"),
                    Vec(OperandConstraint.Value),
                    Vec(),
                    _root_.scala.collection.immutable.Set(),
                    true
                  ),
                  InstructionForm(
                    Opcode("pmevent"),
                    Vec(OperandConstraint.Value),
                    Vec(ModifierGroup(ModifierName("mask"))),
                    _root_.scala.collection.immutable.Set(),
                    true
                  )
                )
              )
            )))
        )
    case Popc extends Definition(
          Opcode("popc"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 2) Right(Instruction.Popc(operands(0), operands(1), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("popc", Vec(2), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("integer-arithmetic-instructions-popc"),
                NonEmptyString("9.7.1.15. Integer Arithmetic Instructions: popc"),
                NonEmptyString("popc"),
                NonEmptyString("Integer Arithmetic Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(NonEmptyString("popc.type  d, a;\n\n.type = { .b32, .b64 };"))),
                Version(PositiveInt(2), NonNegativeInt.Zero),
                Target(20),
                Vec(InstructionForm(
                  Opcode("popc"),
                  Vec(OperandConstraint.Register, OperandConstraint.Value),
                  Vec(ModifierGroup(
                    ModifierName("b32"),
                    ModifierName("b64")
                  )),
                  _root_.scala.collection.immutable.Set(),
                  true
                ))
              )
            )))
        )
    case Prefetch extends Definition(
          Opcode("prefetch"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 1) Right(Instruction.Prefetch(operands(0), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("prefetch", Vec(1), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("data-movement-and-conversion-instructions-prefetch-prefetchu"),
                NonEmptyString("9.7.9.16. Data Movement and Conversion Instructions: prefetch, prefetchu"),
                NonEmptyString("prefetch, prefetchu"),
                NonEmptyString("Data Movement and Conversion Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(NonEmptyString(
                  "prefetch{.space}.level                    [a];   // prefetch to data cache\nprefetch.global.level::eviction_priority  [a];   // prefetch to data cache\n\nprefetchu.L1  [a];             // prefetch to uniform cache\n\nprefetch{.tensormap_space}.tensormap [a];  // prefetch the tensormap\n\n.space =                    { .global, .local };\n.level =                    { .L1, .L2 };\n.level::eviction_priority = { .L2::evict_last, .L2::evict_normal };\n.tensormap_space =          { .const, .param };"
                ))),
                Version(PositiveInt(2), NonNegativeInt.Zero),
                Target(20),
                Vec(
                  InstructionForm(
                    Opcode("prefetch"),
                    Vec(OperandConstraint.Address),
                    Vec(ModifierGroup(
                      ModifierName("L1"),
                      ModifierName("L2")
                    )),
                    _root_.scala.collection.immutable.Set(ModifierName("global"), ModifierName("local")),
                    false
                  ),
                  InstructionForm(
                    Opcode("prefetch"),
                    Vec(OperandConstraint.Address),
                    Vec(
                      ModifierGroup(ModifierName("global")),
                      ModifierGroup(
                        ModifierName("L2::evict_last"),
                        ModifierName("L2::evict_normal")
                      )
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Opcode("prefetch"),
                    Vec(OperandConstraint.Address),
                    Vec(ModifierGroup(ModifierName("tensormap"))),
                    _root_.scala.collection.immutable.Set(ModifierName("const"), ModifierName("param")),
                    false
                  )
                )
              )
            )))
        )
    case Prefetchu extends Definition(
          Opcode("prefetchu"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 1) Right(Instruction.Prefetchu(operands(0), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("prefetchu", Vec(1), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("data-movement-and-conversion-instructions-prefetch-prefetchu"),
                NonEmptyString("9.7.9.16. Data Movement and Conversion Instructions: prefetch, prefetchu"),
                NonEmptyString("prefetch, prefetchu"),
                NonEmptyString("Data Movement and Conversion Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(NonEmptyString(
                  "prefetch{.space}.level                    [a];   // prefetch to data cache\nprefetch.global.level::eviction_priority  [a];   // prefetch to data cache\n\nprefetchu.L1  [a];             // prefetch to uniform cache\n\nprefetch{.tensormap_space}.tensormap [a];  // prefetch the tensormap\n\n.space =                    { .global, .local };\n.level =                    { .L1, .L2 };\n.level::eviction_priority = { .L2::evict_last, .L2::evict_normal };\n.tensormap_space =          { .const, .param };"
                ))),
                Version(PositiveInt(2), NonNegativeInt.Zero),
                Target(20),
                Vec(InstructionForm(
                  Opcode("prefetchu"),
                  Vec(OperandConstraint.Address),
                  Vec(ModifierGroup(ModifierName("L1"))),
                  _root_.scala.collection.immutable.Set(),
                  false
                ))
              )
            )))
        )
    case Prmt extends Definition(
          Opcode("prmt"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 4)
              Right(Instruction.Prmt(operands(0), operands(1), operands(2), operands(3), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("prmt", Vec(4), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("data-movement-and-conversion-instructions-prmt"),
                NonEmptyString("9.7.9.7. Data Movement and Conversion Instructions: prmt"),
                NonEmptyString("prmt"),
                NonEmptyString("Data Movement and Conversion Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(
                  NonEmptyString("prmt.b32{.mode}  d, a, b, c;\n\n.mode = { .f4e, .b4e, .rc8, .ecl, .ecr, .rc16 };")
                )),
                Version(PositiveInt(2), NonNegativeInt.Zero),
                Target(20),
                Vec(InstructionForm(
                  Opcode("prmt"),
                  Vec(
                    OperandConstraint.Register,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Vec(ModifierGroup(ModifierName("b32"))),
                  _root_.scala.collection.immutable.Set(
                    ModifierName("b4e"),
                    ModifierName("ecl"),
                    ModifierName("ecr"),
                    ModifierName("f4e"),
                    ModifierName("rc16"),
                    ModifierName("rc8")
                  ),
                  true
                ))
              )
            )))
        )
    case Rcp extends Definition(
          Opcode("rcp"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 2) Right(Instruction.Rcp(operands(0), operands(1), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("rcp", Vec(2), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("floating-point-instructions-rcp"),
                NonEmptyString("9.7.3.13. Floating Point Instructions: rcp"),
                NonEmptyString("rcp"),
                NonEmptyString("Floating Point Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(NonEmptyString(
                  "rcp.approx{.ftz}.f32  d, a;  // fast, approximate reciprocal\nrcp.rnd{.ftz}.f32     d, a;  // IEEE 754 compliant rounding\nrcp.rnd.f64           d, a;  // IEEE 754 compliant rounding\n\n.rnd = { .rn, .rz, .rm, .rp };"
                ))),
                Version(PositiveInt(1), NonNegativeInt.Zero),
                Target(13),
                Vec(
                  InstructionForm(
                    Opcode("rcp"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(ModifierName("approx")),
                      ModifierGroup(ModifierName("f32"))
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName("ftz")),
                    false
                  ),
                  InstructionForm(
                    Opcode("rcp"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(
                        ModifierName("rm"),
                        ModifierName("rn"),
                        ModifierName("rp"),
                        ModifierName("rz")
                      ),
                      ModifierGroup(ModifierName("f32"))
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName("ftz")),
                    false
                  ),
                  InstructionForm(
                    Opcode("rcp"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(
                        ModifierName("rm"),
                        ModifierName("rn"),
                        ModifierName("rp"),
                        ModifierName("rz")
                      ),
                      ModifierGroup(ModifierName("f64"))
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  )
                )
              )
            )))
        )
    case RcpApproxFtzF64 extends Definition(
          Opcode("rcp.approx.ftz.f64"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 2) Right(Instruction.RcpApproxFtzF64(operands(0), operands(1), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("rcp.approx.ftz.f64", Vec(2), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("floating-point-instructions-rcp-approx-ftz-f64"),
                NonEmptyString("9.7.3.14. Floating Point Instructions: rcp.approx.ftz.f64"),
                NonEmptyString("rcp.approx.ftz.f64"),
                NonEmptyString("Floating Point Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(NonEmptyString("rcp.approx.ftz.f64  d, a;"))),
                Version(PositiveInt(1), NonNegativeInt.Zero),
                Target(20),
                Vec(InstructionForm(
                  Opcode("rcp.approx.ftz.f64"),
                  Vec(OperandConstraint.Register, OperandConstraint.Value),
                  Vec(),
                  _root_.scala.collection.immutable.Set(),
                  true
                ))
              )
            )))
        )
    case Red extends Definition(
          Opcode("red"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 2 || operands.size == 3)
              Right(Instruction.Red(operands(0), operands(1), operands.lift(2), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("red", Vec(2, 3), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("parallel-synchronization-and-communication-instructions-red"),
                NonEmptyString("9.7.14.6. Parallel Synchronization and Communication Instructions: red"),
                NonEmptyString("red"),
                NonEmptyString("Parallel Synchronization and Communication Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(
                  NonEmptyString(
                    "red{.sem}{.scope}{.space}.op{.level::cache_hint}.type          [a], b{, cache_policy};\n\nred{.sem}{.scope}{.space}.add.noftz{.level::cache_hint}.f16    [a], b{, cache_policy};\n\nred{.sem}{.scope}{.space}.add.noftz{.level::cache_hint}.f16x2  [a], b{, cache_policy};\n\nred{.sem}{.scope}{.space}.add.noftz{.level::cache_hint}.bf16\n                                                      [a], b {, cache_policy};\n\nred{.sem}{.scope}{.space}.add.noftz{.level::cache_hint}.bf16x2\n                                                      [a], b {, cache_policy};\n\n.space =              { .global, .shared{::cta, ::cluster} };\n.sem =                { .relaxed, .release };\n.scope =              { .cta, .cluster, .gpu, .sys };\n\n.op =                 { .and, .or, .xor,\n                        .add, .inc, .dec,\n                        .min, .max };\n.level::cache_hint =  { .L2::cache_hint };\n.type =               { .b32, .b64, .u32, .u64, .s32, .s64, .f32, .f64 };"
                  ),
                  NonEmptyString(
                    "red{.sem}{.scope}{.global}.add{.level::cache_hint}.vec_32_bit.f32 [a], b{, cache_policy};\nred{.sem}{.scope}{.global}.op.noftz{.level::cache_hint}. vec_16_bit.half_word_type [a], b{, cache_policy};\nred{.sem}{.scope}{.global}.op.noftz{.level::cache_hint}.vec_32_bit.packed_type [a], b {, cache_policy};\n\n.sem =                { .relaxed, .release };\n.scope =              { .cta, .cluster, .gpu, .sys };\n.op =                 { .add, .min, .max };\n.half_word_type =     { .f16, .bf16 };\n.packed_type =        { .f16x2,.bf16x2 };\n.vec_16_bit =         { .v2, .v4, .v8 }\n.vec_32_bit =         { .v2, .v4 };\n.level::cache_hint =  { .L2::cache_hint }"
                  )
                )),
                Version(PositiveInt(1), NonNegativeInt(2)),
                Target(11),
                Vec(
                  InstructionForm(
                    Opcode("red"),
                    Vec(OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(
                        ModifierName("add"),
                        ModifierName("and"),
                        ModifierName("dec"),
                        ModifierName("inc"),
                        ModifierName("max"),
                        ModifierName("min"),
                        ModifierName("or"),
                        ModifierName("xor")
                      ),
                      ModifierGroup(
                        ModifierName("b32"),
                        ModifierName("b64"),
                        ModifierName("f32"),
                        ModifierName("f64"),
                        ModifierName("s32"),
                        ModifierName("s64"),
                        ModifierName("u32"),
                        ModifierName("u64")
                      )
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName("L2::cache_hint"),
                      ModifierName("cluster"),
                      ModifierName("cta"),
                      ModifierName("global"),
                      ModifierName("gpu"),
                      ModifierName("relaxed"),
                      ModifierName("release"),
                      ModifierName("sys")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode("red"),
                    Vec(OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(ModifierName("add")),
                      ModifierGroup(ModifierName("noftz")),
                      ModifierGroup(ModifierName("f16"))
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName("L2::cache_hint"),
                      ModifierName("cluster"),
                      ModifierName("cta"),
                      ModifierName("global"),
                      ModifierName("gpu"),
                      ModifierName("relaxed"),
                      ModifierName("release"),
                      ModifierName("sys")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode("red"),
                    Vec(OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(ModifierName("add")),
                      ModifierGroup(ModifierName("noftz")),
                      ModifierGroup(ModifierName("f16x2"))
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName("L2::cache_hint"),
                      ModifierName("cluster"),
                      ModifierName("cta"),
                      ModifierName("global"),
                      ModifierName("gpu"),
                      ModifierName("relaxed"),
                      ModifierName("release"),
                      ModifierName("sys")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode("red"),
                    Vec(OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(ModifierName("add")),
                      ModifierGroup(ModifierName("noftz")),
                      ModifierGroup(ModifierName("bf16"))
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName("L2::cache_hint"),
                      ModifierName("cluster"),
                      ModifierName("cta"),
                      ModifierName("global"),
                      ModifierName("gpu"),
                      ModifierName("relaxed"),
                      ModifierName("release"),
                      ModifierName("sys")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode("red"),
                    Vec(OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(ModifierName("add")),
                      ModifierGroup(ModifierName("noftz")),
                      ModifierGroup(ModifierName("bf16x2"))
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName("L2::cache_hint"),
                      ModifierName("cluster"),
                      ModifierName("cta"),
                      ModifierName("global"),
                      ModifierName("gpu"),
                      ModifierName("relaxed"),
                      ModifierName("release"),
                      ModifierName("sys")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode("red"),
                    Vec(OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(ModifierName("add")),
                      ModifierGroup(
                        ModifierName("v2"),
                        ModifierName("v4")
                      ),
                      ModifierGroup(ModifierName("f32"))
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName("L2::cache_hint"),
                      ModifierName("cluster"),
                      ModifierName("cta"),
                      ModifierName("global"),
                      ModifierName("gpu"),
                      ModifierName("relaxed"),
                      ModifierName("release"),
                      ModifierName("sys")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode("red"),
                    Vec(OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(
                        ModifierName("add"),
                        ModifierName("max"),
                        ModifierName("min")
                      ),
                      ModifierGroup(ModifierName("noftz"))
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName("L2::cache_hint"),
                      ModifierName("cluster"),
                      ModifierName("cta"),
                      ModifierName("global"),
                      ModifierName("gpu"),
                      ModifierName("relaxed"),
                      ModifierName("release"),
                      ModifierName("sys")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode("red"),
                    Vec(OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(
                        ModifierName("add"),
                        ModifierName("max"),
                        ModifierName("min")
                      ),
                      ModifierGroup(ModifierName("noftz")),
                      ModifierGroup(
                        ModifierName("v2"),
                        ModifierName("v4")
                      ),
                      ModifierGroup(
                        ModifierName("bf16x2"),
                        ModifierName("f16x2")
                      )
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName("L2::cache_hint"),
                      ModifierName("cluster"),
                      ModifierName("cta"),
                      ModifierName("global"),
                      ModifierName("gpu"),
                      ModifierName("relaxed"),
                      ModifierName("release"),
                      ModifierName("sys")
                    ),
                    false
                  )
                )
              )
            )))
        )
    case RedAsync extends Definition(
          Opcode("red.async"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 2 || operands.size == 3)
              Right(Instruction.RedAsync(operands(0), operands(1), operands.lift(2), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("red.async", Vec(2, 3), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("parallel-synchronization-and-communication-instructions-red-async"),
                NonEmptyString("9.7.14.7. Parallel Synchronization and Communication Instructions: red.async"),
                NonEmptyString("red.async"),
                NonEmptyString("Parallel Synchronization and Communication Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(NonEmptyString(
                  "// Increment and Decrement reductions\nred.async.sem.scope{.ss}.completion_mechanism.op.type [a], b, [mbar];\n\n.sem  =                 { .relaxed };\n.scope =                { .cluster };\n.ss   =                 { .shared::cluster };\n.op   =                 { .inc, .dec };\n.type =                 { .u32 };\n.completion_mechanism = { .mbarrier::complete_tx::bytes };\n\n\n// MIN and MAX reductions\nred.async.sem.scope{.ss}.completion_mechanism.op.type [a], b, [mbar];\n\n.sem  = { .relaxed };\n.scope = { .cluster };\n.ss   = { .shared::cluster };\n.op   = { .min, .max };\n.type = { .u32, .s32 };\n.completion_mechanism = { .mbarrier::complete_tx::bytes };\n\n// Bitwise AND, OR and XOR reductions\nred.async.sem.scope{.ss}.completion_mechanism.op.type [a], b, [mbar];\n\n.sem  = { .relaxed };\n.scope = { .cluster };\n.ss   = { .shared::cluster };\n.op   = { .and, .or, .xor };\n.type = { .b32 };\n.completion_mechanism = { .mbarrier::complete_tx::bytes };\n\n// ADD reductions\nred.async.sem.scope{.ss}.completion_mechanism.add.type [a], b, [mbar];\n\n.sem  = { .relaxed };\n.scope = { .cluster };\n.ss   = { .shared::cluster };\n.type = { .u32, .s32, .u64 };\n.completion_mechanism = { .mbarrier::complete_tx::bytes };\n\nred.async{.mmio}.sem.scope{.ss}.add.type [a], b;\n\n.sem  = { .release };\n.scope = { .gpu, .sys };\n.ss   = { .global };\n.type = { .u32, .s32, .u64, .s64 };"
                ))),
                Version(PositiveInt(8), NonNegativeInt(1)),
                Target(90),
                Vec(
                  InstructionForm(
                    Opcode("red.async"),
                    Vec(OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Address),
                    Vec(
                      ModifierGroup(ModifierName("release")),
                      ModifierGroup(
                        ModifierName("gpu"),
                        ModifierName("sys")
                      ),
                      ModifierGroup(ModifierName("mbarrier::complete_tx::bytes")),
                      ModifierGroup(
                        ModifierName("and"),
                        ModifierName("or"),
                        ModifierName("xor")
                      ),
                      ModifierGroup(
                        ModifierName("s32"),
                        ModifierName("s64"),
                        ModifierName("u32"),
                        ModifierName("u64")
                      )
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName("global")),
                    false
                  ),
                  InstructionForm(
                    Opcode("red.async"),
                    Vec(OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Address),
                    Vec(
                      ModifierGroup(ModifierName("release")),
                      ModifierGroup(
                        ModifierName("gpu"),
                        ModifierName("sys")
                      ),
                      ModifierGroup(ModifierName("mbarrier::complete_tx::bytes")),
                      ModifierGroup(ModifierName("add")),
                      ModifierGroup(
                        ModifierName("s32"),
                        ModifierName("s64"),
                        ModifierName("u32"),
                        ModifierName("u64")
                      )
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName("global")),
                    false
                  ),
                  InstructionForm(
                    Opcode("red.async"),
                    Vec(OperandConstraint.Address, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(ModifierName("release")),
                      ModifierGroup(
                        ModifierName("gpu"),
                        ModifierName("sys")
                      ),
                      ModifierGroup(ModifierName("add")),
                      ModifierGroup(
                        ModifierName("s32"),
                        ModifierName("s64"),
                        ModifierName("u32"),
                        ModifierName("u64")
                      )
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName("global"), ModifierName("mmio")),
                    false
                  )
                )
              )
            )))
        )
    case ReduxSync extends Definition(
          Opcode("redux.sync"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 3) Right(Instruction.ReduxSync(operands(0), operands(1), operands(2), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("redux.sync", Vec(3), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("parallel-synchronization-and-communication-instructions-redux-sync"),
                NonEmptyString("9.7.14.13. Parallel Synchronization and Communication Instructions: redux.sync"),
                NonEmptyString("redux.sync"),
                NonEmptyString("Parallel Synchronization and Communication Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(NonEmptyString(
                  "redux.sync.op.type dst, src, membermask;\n.op   = {.add, .min, .max}\n.type = {.u32, .s32}\n\nredux.sync.op.b32 dst, src, membermask;\n.op   = {.and, .or, .xor}\n\nredux.sync.op{.abs.}{.NaN}.f32 dst, src, membermask;\n.op   = { .min, .max }"
                ))),
                Version(PositiveInt(7), NonNegativeInt.Zero),
                Target(80),
                Vec(
                  InstructionForm(
                    Opcode("redux.sync"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(
                        ModifierName("max"),
                        ModifierName("min")
                      ),
                      ModifierGroup(
                        ModifierName("s32"),
                        ModifierName("u32")
                      )
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Opcode("redux.sync"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(
                        ModifierName("max"),
                        ModifierName("min")
                      ),
                      ModifierGroup(ModifierName("b32"))
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Opcode("redux.sync"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(
                        ModifierName("max"),
                        ModifierName("min")
                      ),
                      ModifierGroup(ModifierName("f32"))
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName("NaN"), ModifierName("abs")),
                    false
                  )
                )
              )
            )))
        )
    case Rem extends Definition(
          Opcode("rem"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 3) Right(Instruction.Rem(operands(0), operands(1), operands(2), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("rem", Vec(3), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("integer-arithmetic-instructions-rem"),
                NonEmptyString("9.7.1.10. Integer Arithmetic Instructions: rem"),
                NonEmptyString("rem"),
                NonEmptyString("Integer Arithmetic Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(
                  NonEmptyString("rem.type  d, a, b;\n\n.type = { .u16, .u32, .u64,\n          .s16, .s32, .s64 };")
                )),
                Version(PositiveInt(1), NonNegativeInt.Zero),
                Target(10),
                Vec(InstructionForm(
                  Opcode("rem"),
                  Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                  Vec(ModifierGroup(
                    ModifierName("s16"),
                    ModifierName("s32"),
                    ModifierName("s64"),
                    ModifierName("u16"),
                    ModifierName("u32"),
                    ModifierName("u64")
                  )),
                  _root_.scala.collection.immutable.Set(),
                  true
                ))
              )
            )))
        )
    case Ret extends Definition(
          Opcode("ret"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 0) Right(Instruction.Ret(mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("ret", Vec(0), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("control-flow-instructions-ret"),
                NonEmptyString("9.7.13.6. Control Flow Instructions: ret"),
                NonEmptyString("ret"),
                NonEmptyString("Control Flow Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(NonEmptyString("ret{.uni};"))),
                Version(PositiveInt(1), NonNegativeInt.Zero),
                Target(10),
                Vec(InstructionForm(
                  Opcode("ret"),
                  Vec(),
                  Vec(),
                  _root_.scala.collection.immutable.Set(ModifierName("uni")),
                  true
                ))
              )
            )))
        )
    case Rsqrt extends Definition(
          Opcode("rsqrt"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 2) Right(Instruction.Rsqrt(operands(0), operands(1), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("rsqrt", Vec(2), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("floating-point-instructions-rsqrt"),
                NonEmptyString("9.7.3.16. Floating Point Instructions: rsqrt"),
                NonEmptyString("rsqrt"),
                NonEmptyString("Floating Point Instructions"),
                NonEmptyVec.unsafeFromVec(
                  Vec(NonEmptyString("rsqrt.approx{.ftz}.f32  d, a;\nrsqrt.approx.f64        d, a;"))
                ),
                Version(PositiveInt(1), NonNegativeInt.Zero),
                Target(13),
                Vec(
                  InstructionForm(
                    Opcode("rsqrt"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(ModifierName("approx")),
                      ModifierGroup(ModifierName("f32"))
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName("ftz")),
                    true
                  ),
                  InstructionForm(
                    Opcode("rsqrt"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(ModifierName("approx")),
                      ModifierGroup(ModifierName("f64"))
                    ),
                    _root_.scala.collection.immutable.Set(),
                    true
                  )
                )
              )
            )))
        )
    case RsqrtApproxFtzF64 extends Definition(
          Opcode("rsqrt.approx.ftz.f64"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 2) Right(Instruction.RsqrtApproxFtzF64(operands(0), operands(1), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("rsqrt.approx.ftz.f64", Vec(2), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("floating-point-instructions-rsqrt-approx-ftz-f64"),
                NonEmptyString("9.7.3.17. Floating Point Instructions: rsqrt.approx.ftz.f64"),
                NonEmptyString("rsqrt.approx.ftz.f64"),
                NonEmptyString("Floating Point Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(NonEmptyString("rsqrt.approx.ftz.f64 d, a;"))),
                Version(PositiveInt(1), NonNegativeInt.Zero),
                Target(20),
                Vec(InstructionForm(
                  Opcode("rsqrt.approx.ftz.f64"),
                  Vec(OperandConstraint.Register, OperandConstraint.Value),
                  Vec(),
                  _root_.scala.collection.immutable.Set(),
                  true
                ))
              )
            )))
        )
    case Sad extends Definition(
          Opcode("sad"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 4)
              Right(Instruction.Sad(operands(0), operands(1), operands(2), operands(3), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("sad", Vec(4), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("integer-arithmetic-instructions-sad"),
                NonEmptyString("9.7.1.8. Integer Arithmetic Instructions: sad"),
                NonEmptyString("sad"),
                NonEmptyString("Integer Arithmetic Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(
                  NonEmptyString("sad.type  d, a, b, c;\n\n.type = { .u16, .u32, .u64,\n          .s16, .s32, .s64 };")
                )),
                Version(PositiveInt(1), NonNegativeInt.Zero),
                Target(10),
                Vec(InstructionForm(
                  Opcode("sad"),
                  Vec(
                    OperandConstraint.Register,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Vec(ModifierGroup(
                    ModifierName("s16"),
                    ModifierName("s32"),
                    ModifierName("s64"),
                    ModifierName("u16"),
                    ModifierName("u32"),
                    ModifierName("u64")
                  )),
                  _root_.scala.collection.immutable.Set(),
                  true
                ))
              )
            )))
        )
    case Selp extends Definition(
          Opcode("selp"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 4)
              Right(Instruction.Selp(operands(0), operands(1), operands(2), operands(3), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("selp", Vec(4), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("comparison-and-selection-instructions-selp"),
                NonEmptyString("9.7.6.3. Comparison and Selection Instructions: selp"),
                NonEmptyString("selp"),
                NonEmptyString("Comparison and Selection Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(NonEmptyString(
                  "selp.type d, a, b, c;\n\n.type = { .b16, .b32, .b64,\n          .u16, .u32, .u64,\n          .s16, .s32, .s64,\n                .f32, .f64 };"
                ))),
                Version(PositiveInt(1), NonNegativeInt.Zero),
                Target(13),
                Vec(InstructionForm(
                  Opcode("selp"),
                  Vec(
                    OperandConstraint.Register,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Vec(ModifierGroup(
                    ModifierName("b16"),
                    ModifierName("b32"),
                    ModifierName("b64"),
                    ModifierName("f32"),
                    ModifierName("f64"),
                    ModifierName("s16"),
                    ModifierName("s32"),
                    ModifierName("s64"),
                    ModifierName("u16"),
                    ModifierName("u32"),
                    ModifierName("u64")
                  )),
                  _root_.scala.collection.immutable.Set(),
                  true
                ))
              )
            )))
        )
    case Set extends Definition(
          Opcode("set"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 3 || operands.size == 4)
              Right(Instruction.Set(operands(0), operands(1), operands(2), operands.lift(3), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("set", Vec(3, 4), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("comparison-and-selection-instructions-set"),
                NonEmptyString("9.7.6.1. Comparison and Selection Instructions: set"),
                NonEmptyString("set"),
                NonEmptyString("Comparison and Selection Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(NonEmptyString(
                  "set.CmpOp{.ftz}.dtype.stype         d, a, b;\nset.CmpOp.BoolOp{.ftz}.dtype.stype  d, a, b, {!}c;\n\n.CmpOp  = { eq, ne, lt, le, gt, ge, lo, ls, hi, hs,\n            equ, neu, ltu, leu, gtu, geu, num, nan };\n.BoolOp = { and, or, xor };\n.dtype  = { .u32, .s32, .f32 };\n.stype  = { .b16, .b32, .b64,\n            .u16, .u32, .u64,\n            .s16, .s32, .s64,\n                  .f32, .f64 };"
                ))),
                Version(PositiveInt(1), NonNegativeInt.Zero),
                Target(13),
                Vec(
                  InstructionForm(
                    Opcode("set"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(
                        ModifierName("eq"),
                        ModifierName("equ"),
                        ModifierName("ge"),
                        ModifierName("geu"),
                        ModifierName("gt"),
                        ModifierName("gtu"),
                        ModifierName("hi"),
                        ModifierName("hs"),
                        ModifierName("le"),
                        ModifierName("leu"),
                        ModifierName("lo"),
                        ModifierName("ls"),
                        ModifierName("lt"),
                        ModifierName("ltu"),
                        ModifierName("nan"),
                        ModifierName("ne"),
                        ModifierName("neu"),
                        ModifierName("num")
                      ),
                      ModifierGroup(
                        ModifierName("f32"),
                        ModifierName("s32"),
                        ModifierName("u32")
                      ),
                      ModifierGroup(
                        ModifierName("b16"),
                        ModifierName("b32"),
                        ModifierName("b64"),
                        ModifierName("f32"),
                        ModifierName("f64"),
                        ModifierName("s16"),
                        ModifierName("s32"),
                        ModifierName("s64"),
                        ModifierName("u16"),
                        ModifierName("u32"),
                        ModifierName("u64")
                      )
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName("ftz")),
                    true
                  ),
                  InstructionForm(
                    Opcode("set"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(
                        ModifierName("eq"),
                        ModifierName("equ"),
                        ModifierName("ge"),
                        ModifierName("geu"),
                        ModifierName("gt"),
                        ModifierName("gtu"),
                        ModifierName("hi"),
                        ModifierName("hs"),
                        ModifierName("le"),
                        ModifierName("leu"),
                        ModifierName("lo"),
                        ModifierName("ls"),
                        ModifierName("lt"),
                        ModifierName("ltu"),
                        ModifierName("nan"),
                        ModifierName("ne"),
                        ModifierName("neu"),
                        ModifierName("num")
                      ),
                      ModifierGroup(
                        ModifierName("and"),
                        ModifierName("or"),
                        ModifierName("xor")
                      ),
                      ModifierGroup(
                        ModifierName("f32"),
                        ModifierName("s32"),
                        ModifierName("u32")
                      ),
                      ModifierGroup(
                        ModifierName("b16"),
                        ModifierName("b32"),
                        ModifierName("b64"),
                        ModifierName("f32"),
                        ModifierName("f64"),
                        ModifierName("s16"),
                        ModifierName("s32"),
                        ModifierName("s64"),
                        ModifierName("u16"),
                        ModifierName("u32"),
                        ModifierName("u64")
                      )
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName("ftz")),
                    true
                  )
                )
              ),
              Metadata.Entry(
                NonEmptyString("half-precision-comparison-instructions-set"),
                NonEmptyString("9.7.7.1. Half Precision Comparison Instructions: set"),
                NonEmptyString("set"),
                NonEmptyString("Half Precision Comparison Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(NonEmptyString(
                  "set.CmpOp{.ftz}.f16.stype            d, a, b;\nset.CmpOp.BoolOp{.ftz}.f16.stype     d, a, b, {!}c;\n\nset.CmpOp.bf16.stype                 d, a, b;\nset.CmpOp.BoolOp.bf16.stype          d, a, b, {!}c;\n\nset.CmpOp{.ftz}.dtype.f16            d, a, b;\nset.CmpOp.BoolOp{.ftz}.dtype.f16     d, a, b, {!}c;\n.dtype  = { .u16, .s16, .u32, .s32}\n\nset.CmpOp.dtype.bf16                 d, a, b;\nset.CmpOp.BoolOp.dtype.bf16          d, a, b, {!}c;\n.dtype  = { .u16, .s16, .u32, .s32}\n\nset.CmpOp{.ftz}.dtype.f16x2          d, a, b;\nset.CmpOp.BoolOp{.ftz}.dtype.f16x2   d, a, b, {!}c;\n.dtype  = { .f16x2, .u32, .s32}\n\nset.CmpOp.dtype.bf16x2               d, a, b;\nset.CmpOp.BoolOp.dtype.bf16x2        d, a, b, {!}c;\n.dtype  = { .bf16x2, .u32, .s32}\n\n.CmpOp  = { eq, ne, lt, le, gt, ge,\n            equ, neu, ltu, leu, gtu, geu, num, nan };\n.BoolOp = { and, or, xor };\n.stype  = { .b16, .b32, .b64,\n            .u16, .u32, .u64,\n            .s16, .s32, .s64,\n            .f16, .f32, .f64};"
                ))),
                Version(PositiveInt(4), NonNegativeInt(2)),
                Target(53),
                Vec(
                  InstructionForm(
                    Opcode("set"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(
                        ModifierName("eq"),
                        ModifierName("equ"),
                        ModifierName("ge"),
                        ModifierName("geu"),
                        ModifierName("gt"),
                        ModifierName("gtu"),
                        ModifierName("le"),
                        ModifierName("leu"),
                        ModifierName("lt"),
                        ModifierName("ltu"),
                        ModifierName("nan"),
                        ModifierName("ne"),
                        ModifierName("neu"),
                        ModifierName("num")
                      ),
                      ModifierGroup(ModifierName("f16")),
                      ModifierGroup(
                        ModifierName("b16"),
                        ModifierName("b32"),
                        ModifierName("b64"),
                        ModifierName("f16"),
                        ModifierName("f32"),
                        ModifierName("f64"),
                        ModifierName("s16"),
                        ModifierName("s32"),
                        ModifierName("s64"),
                        ModifierName("u16"),
                        ModifierName("u32"),
                        ModifierName("u64")
                      )
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName("ftz")),
                    false
                  ),
                  InstructionForm(
                    Opcode("set"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(
                        ModifierName("eq"),
                        ModifierName("equ"),
                        ModifierName("ge"),
                        ModifierName("geu"),
                        ModifierName("gt"),
                        ModifierName("gtu"),
                        ModifierName("le"),
                        ModifierName("leu"),
                        ModifierName("lt"),
                        ModifierName("ltu"),
                        ModifierName("nan"),
                        ModifierName("ne"),
                        ModifierName("neu"),
                        ModifierName("num")
                      ),
                      ModifierGroup(
                        ModifierName("and"),
                        ModifierName("or"),
                        ModifierName("xor")
                      ),
                      ModifierGroup(ModifierName("f16")),
                      ModifierGroup(
                        ModifierName("b16"),
                        ModifierName("b32"),
                        ModifierName("b64"),
                        ModifierName("f16"),
                        ModifierName("f32"),
                        ModifierName("f64"),
                        ModifierName("s16"),
                        ModifierName("s32"),
                        ModifierName("s64"),
                        ModifierName("u16"),
                        ModifierName("u32"),
                        ModifierName("u64")
                      )
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName("ftz")),
                    false
                  ),
                  InstructionForm(
                    Opcode("set"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(
                        ModifierName("eq"),
                        ModifierName("equ"),
                        ModifierName("ge"),
                        ModifierName("geu"),
                        ModifierName("gt"),
                        ModifierName("gtu"),
                        ModifierName("le"),
                        ModifierName("leu"),
                        ModifierName("lt"),
                        ModifierName("ltu"),
                        ModifierName("nan"),
                        ModifierName("ne"),
                        ModifierName("neu"),
                        ModifierName("num")
                      ),
                      ModifierGroup(ModifierName("bf16")),
                      ModifierGroup(
                        ModifierName("b16"),
                        ModifierName("b32"),
                        ModifierName("b64"),
                        ModifierName("f16"),
                        ModifierName("f32"),
                        ModifierName("f64"),
                        ModifierName("s16"),
                        ModifierName("s32"),
                        ModifierName("s64"),
                        ModifierName("u16"),
                        ModifierName("u32"),
                        ModifierName("u64")
                      )
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Opcode("set"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(
                        ModifierName("eq"),
                        ModifierName("equ"),
                        ModifierName("ge"),
                        ModifierName("geu"),
                        ModifierName("gt"),
                        ModifierName("gtu"),
                        ModifierName("le"),
                        ModifierName("leu"),
                        ModifierName("lt"),
                        ModifierName("ltu"),
                        ModifierName("nan"),
                        ModifierName("ne"),
                        ModifierName("neu"),
                        ModifierName("num")
                      ),
                      ModifierGroup(
                        ModifierName("and"),
                        ModifierName("or"),
                        ModifierName("xor")
                      ),
                      ModifierGroup(ModifierName("bf16")),
                      ModifierGroup(
                        ModifierName("b16"),
                        ModifierName("b32"),
                        ModifierName("b64"),
                        ModifierName("f16"),
                        ModifierName("f32"),
                        ModifierName("f64"),
                        ModifierName("s16"),
                        ModifierName("s32"),
                        ModifierName("s64"),
                        ModifierName("u16"),
                        ModifierName("u32"),
                        ModifierName("u64")
                      )
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Opcode("set"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(
                        ModifierName("eq"),
                        ModifierName("equ"),
                        ModifierName("ge"),
                        ModifierName("geu"),
                        ModifierName("gt"),
                        ModifierName("gtu"),
                        ModifierName("le"),
                        ModifierName("leu"),
                        ModifierName("lt"),
                        ModifierName("ltu"),
                        ModifierName("nan"),
                        ModifierName("ne"),
                        ModifierName("neu"),
                        ModifierName("num")
                      ),
                      ModifierGroup(
                        ModifierName("bf16x2"),
                        ModifierName("s32"),
                        ModifierName("u32")
                      ),
                      ModifierGroup(ModifierName("f16"))
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName("ftz")),
                    false
                  ),
                  InstructionForm(
                    Opcode("set"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(
                        ModifierName("eq"),
                        ModifierName("equ"),
                        ModifierName("ge"),
                        ModifierName("geu"),
                        ModifierName("gt"),
                        ModifierName("gtu"),
                        ModifierName("le"),
                        ModifierName("leu"),
                        ModifierName("lt"),
                        ModifierName("ltu"),
                        ModifierName("nan"),
                        ModifierName("ne"),
                        ModifierName("neu"),
                        ModifierName("num")
                      ),
                      ModifierGroup(
                        ModifierName("and"),
                        ModifierName("or"),
                        ModifierName("xor")
                      ),
                      ModifierGroup(
                        ModifierName("bf16x2"),
                        ModifierName("s32"),
                        ModifierName("u32")
                      ),
                      ModifierGroup(ModifierName("f16"))
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName("ftz")),
                    false
                  ),
                  InstructionForm(
                    Opcode("set"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(
                        ModifierName("eq"),
                        ModifierName("equ"),
                        ModifierName("ge"),
                        ModifierName("geu"),
                        ModifierName("gt"),
                        ModifierName("gtu"),
                        ModifierName("le"),
                        ModifierName("leu"),
                        ModifierName("lt"),
                        ModifierName("ltu"),
                        ModifierName("nan"),
                        ModifierName("ne"),
                        ModifierName("neu"),
                        ModifierName("num")
                      ),
                      ModifierGroup(
                        ModifierName("bf16x2"),
                        ModifierName("s32"),
                        ModifierName("u32")
                      ),
                      ModifierGroup(ModifierName("bf16"))
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Opcode("set"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(
                        ModifierName("eq"),
                        ModifierName("equ"),
                        ModifierName("ge"),
                        ModifierName("geu"),
                        ModifierName("gt"),
                        ModifierName("gtu"),
                        ModifierName("le"),
                        ModifierName("leu"),
                        ModifierName("lt"),
                        ModifierName("ltu"),
                        ModifierName("nan"),
                        ModifierName("ne"),
                        ModifierName("neu"),
                        ModifierName("num")
                      ),
                      ModifierGroup(
                        ModifierName("and"),
                        ModifierName("or"),
                        ModifierName("xor")
                      ),
                      ModifierGroup(
                        ModifierName("bf16x2"),
                        ModifierName("s32"),
                        ModifierName("u32")
                      ),
                      ModifierGroup(ModifierName("bf16"))
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Opcode("set"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(
                        ModifierName("eq"),
                        ModifierName("equ"),
                        ModifierName("ge"),
                        ModifierName("geu"),
                        ModifierName("gt"),
                        ModifierName("gtu"),
                        ModifierName("le"),
                        ModifierName("leu"),
                        ModifierName("lt"),
                        ModifierName("ltu"),
                        ModifierName("nan"),
                        ModifierName("ne"),
                        ModifierName("neu"),
                        ModifierName("num")
                      ),
                      ModifierGroup(
                        ModifierName("bf16x2"),
                        ModifierName("s32"),
                        ModifierName("u32")
                      ),
                      ModifierGroup(ModifierName("f16x2"))
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName("ftz")),
                    false
                  ),
                  InstructionForm(
                    Opcode("set"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(
                        ModifierName("eq"),
                        ModifierName("equ"),
                        ModifierName("ge"),
                        ModifierName("geu"),
                        ModifierName("gt"),
                        ModifierName("gtu"),
                        ModifierName("le"),
                        ModifierName("leu"),
                        ModifierName("lt"),
                        ModifierName("ltu"),
                        ModifierName("nan"),
                        ModifierName("ne"),
                        ModifierName("neu"),
                        ModifierName("num")
                      ),
                      ModifierGroup(
                        ModifierName("and"),
                        ModifierName("or"),
                        ModifierName("xor")
                      ),
                      ModifierGroup(
                        ModifierName("bf16x2"),
                        ModifierName("s32"),
                        ModifierName("u32")
                      ),
                      ModifierGroup(ModifierName("f16x2"))
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName("ftz")),
                    false
                  ),
                  InstructionForm(
                    Opcode("set"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(
                        ModifierName("eq"),
                        ModifierName("equ"),
                        ModifierName("ge"),
                        ModifierName("geu"),
                        ModifierName("gt"),
                        ModifierName("gtu"),
                        ModifierName("le"),
                        ModifierName("leu"),
                        ModifierName("lt"),
                        ModifierName("ltu"),
                        ModifierName("nan"),
                        ModifierName("ne"),
                        ModifierName("neu"),
                        ModifierName("num")
                      ),
                      ModifierGroup(
                        ModifierName("bf16x2"),
                        ModifierName("s32"),
                        ModifierName("u32")
                      ),
                      ModifierGroup(ModifierName("bf16x2"))
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Opcode("set"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(
                        ModifierName("eq"),
                        ModifierName("equ"),
                        ModifierName("ge"),
                        ModifierName("geu"),
                        ModifierName("gt"),
                        ModifierName("gtu"),
                        ModifierName("le"),
                        ModifierName("leu"),
                        ModifierName("lt"),
                        ModifierName("ltu"),
                        ModifierName("nan"),
                        ModifierName("ne"),
                        ModifierName("neu"),
                        ModifierName("num")
                      ),
                      ModifierGroup(
                        ModifierName("and"),
                        ModifierName("or"),
                        ModifierName("xor")
                      ),
                      ModifierGroup(
                        ModifierName("bf16x2"),
                        ModifierName("s32"),
                        ModifierName("u32")
                      ),
                      ModifierGroup(ModifierName("bf16x2"))
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  )
                )
              )
            )))
        )
    case SetMaxRegisters extends Definition(
          Opcode("setmaxnreg"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 1) Right(Instruction.SetMaxRegisters(operands(0), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("setmaxnreg", Vec(1), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("miscellaneous-instructions-setmaxnreg"),
                NonEmptyString("9.7.20.5. Miscellaneous Instructions: setmaxnreg"),
                NonEmptyString("setmaxnreg"),
                NonEmptyString("Miscellaneous Instructions"),
                NonEmptyVec.unsafeFromVec(
                  Vec(NonEmptyString("setmaxnreg.action.sync.aligned.u32 imm-reg-count;\n\n.action = { .inc, .dec };"))
                ),
                Version(PositiveInt(8), NonNegativeInt.Zero),
                Target(100),
                Vec(InstructionForm(
                  Opcode("setmaxnreg"),
                  Vec(OperandConstraint.Value),
                  Vec(
                    ModifierGroup(
                      ModifierName("dec"),
                      ModifierName("inc")
                    ),
                    ModifierGroup(ModifierName("sync")),
                    ModifierGroup(ModifierName("aligned")),
                    ModifierGroup(ModifierName("u32"))
                  ),
                  _root_.scala.collection.immutable.Set(),
                  false
                ))
              )
            )))
        )
    case Setp extends Definition(
          Opcode("setp"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 3 || operands.size == 4)
              Right(Instruction.Setp(operands(0), operands(1), operands(2), operands.lift(3), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("setp", Vec(3, 4), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("comparison-and-selection-instructions-setp"),
                NonEmptyString("9.7.6.2. Comparison and Selection Instructions: setp"),
                NonEmptyString("setp"),
                NonEmptyString("Comparison and Selection Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(NonEmptyString(
                  "setp.CmpOp{.ftz}.type         p[|q], a, b;\nsetp.CmpOp.BoolOp{.ftz}.type  p[|q], a, b, {!}c;\n\n.CmpOp  = { eq, ne, lt, le, gt, ge, lo, ls, hi, hs,\n            equ, neu, ltu, leu, gtu, geu, num, nan };\n.BoolOp = { and, or, xor };\n.type   = { .b16, .b32, .b64,\n            .u16, .u32, .u64,\n            .s16, .s32, .s64,\n                  .f32, .f64 };"
                ))),
                Version(PositiveInt(1), NonNegativeInt.Zero),
                Target(13),
                Vec(
                  InstructionForm(
                    Opcode("setp"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(
                        ModifierName("eq"),
                        ModifierName("equ"),
                        ModifierName("ge"),
                        ModifierName("geu"),
                        ModifierName("gt"),
                        ModifierName("gtu"),
                        ModifierName("hi"),
                        ModifierName("hs"),
                        ModifierName("le"),
                        ModifierName("leu"),
                        ModifierName("lo"),
                        ModifierName("ls"),
                        ModifierName("lt"),
                        ModifierName("ltu"),
                        ModifierName("nan"),
                        ModifierName("ne"),
                        ModifierName("neu"),
                        ModifierName("num")
                      ),
                      ModifierGroup(
                        ModifierName("b16"),
                        ModifierName("b32"),
                        ModifierName("b64"),
                        ModifierName("f32"),
                        ModifierName("f64"),
                        ModifierName("s16"),
                        ModifierName("s32"),
                        ModifierName("s64"),
                        ModifierName("u16"),
                        ModifierName("u32"),
                        ModifierName("u64")
                      )
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName("ftz")),
                    true
                  ),
                  InstructionForm(
                    Opcode("setp"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(
                        ModifierName("eq"),
                        ModifierName("equ"),
                        ModifierName("ge"),
                        ModifierName("geu"),
                        ModifierName("gt"),
                        ModifierName("gtu"),
                        ModifierName("hi"),
                        ModifierName("hs"),
                        ModifierName("le"),
                        ModifierName("leu"),
                        ModifierName("lo"),
                        ModifierName("ls"),
                        ModifierName("lt"),
                        ModifierName("ltu"),
                        ModifierName("nan"),
                        ModifierName("ne"),
                        ModifierName("neu"),
                        ModifierName("num")
                      ),
                      ModifierGroup(
                        ModifierName("and"),
                        ModifierName("or"),
                        ModifierName("xor")
                      ),
                      ModifierGroup(
                        ModifierName("b16"),
                        ModifierName("b32"),
                        ModifierName("b64"),
                        ModifierName("f32"),
                        ModifierName("f64"),
                        ModifierName("s16"),
                        ModifierName("s32"),
                        ModifierName("s64"),
                        ModifierName("u16"),
                        ModifierName("u32"),
                        ModifierName("u64")
                      )
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName("ftz")),
                    true
                  )
                )
              ),
              Metadata.Entry(
                NonEmptyString("half-precision-comparison-instructions-setp"),
                NonEmptyString("9.7.7.2. Half Precision Comparison Instructions: setp"),
                NonEmptyString("setp"),
                NonEmptyString("Half Precision Comparison Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(NonEmptyString(
                  "setp.CmpOp{.ftz}.f16           p, a, b;\nsetp.CmpOp.BoolOp{.ftz}.f16    p, a, b, {!}c;\n\nsetp.CmpOp{.ftz}.f16x2         p|q, a, b;\nsetp.CmpOp.BoolOp{.ftz}.f16x2  p|q, a, b, {!}c;\n\nsetp.CmpOp.bf16                p, a, b;\nsetp.CmpOp.BoolOp.bf16         p, a, b, {!}c;\n\nsetp.CmpOp.bf16x2              p|q, a, b;\nsetp.CmpOp.BoolOp.bf16x2       p|q, a, b, {!}c;\n\n.CmpOp  = { eq, ne, lt, le, gt, ge,\n            equ, neu, ltu, leu, gtu, geu, num, nan };\n.BoolOp = { and, or, xor };"
                ))),
                Version(PositiveInt(4), NonNegativeInt(2)),
                Target(53),
                Vec(
                  InstructionForm(
                    Opcode("setp"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(
                        ModifierName("eq"),
                        ModifierName("equ"),
                        ModifierName("ge"),
                        ModifierName("geu"),
                        ModifierName("gt"),
                        ModifierName("gtu"),
                        ModifierName("le"),
                        ModifierName("leu"),
                        ModifierName("lt"),
                        ModifierName("ltu"),
                        ModifierName("nan"),
                        ModifierName("ne"),
                        ModifierName("neu"),
                        ModifierName("num")
                      ),
                      ModifierGroup(ModifierName("f16"))
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName("ftz")),
                    false
                  ),
                  InstructionForm(
                    Opcode("setp"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(
                        ModifierName("eq"),
                        ModifierName("equ"),
                        ModifierName("ge"),
                        ModifierName("geu"),
                        ModifierName("gt"),
                        ModifierName("gtu"),
                        ModifierName("le"),
                        ModifierName("leu"),
                        ModifierName("lt"),
                        ModifierName("ltu"),
                        ModifierName("nan"),
                        ModifierName("ne"),
                        ModifierName("neu"),
                        ModifierName("num")
                      ),
                      ModifierGroup(
                        ModifierName("and"),
                        ModifierName("or"),
                        ModifierName("xor")
                      ),
                      ModifierGroup(ModifierName("f16"))
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName("ftz")),
                    false
                  ),
                  InstructionForm(
                    Opcode("setp"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(
                        ModifierName("eq"),
                        ModifierName("equ"),
                        ModifierName("ge"),
                        ModifierName("geu"),
                        ModifierName("gt"),
                        ModifierName("gtu"),
                        ModifierName("le"),
                        ModifierName("leu"),
                        ModifierName("lt"),
                        ModifierName("ltu"),
                        ModifierName("nan"),
                        ModifierName("ne"),
                        ModifierName("neu"),
                        ModifierName("num")
                      ),
                      ModifierGroup(ModifierName("f16x2"))
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName("ftz")),
                    false
                  ),
                  InstructionForm(
                    Opcode("setp"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(
                        ModifierName("eq"),
                        ModifierName("equ"),
                        ModifierName("ge"),
                        ModifierName("geu"),
                        ModifierName("gt"),
                        ModifierName("gtu"),
                        ModifierName("le"),
                        ModifierName("leu"),
                        ModifierName("lt"),
                        ModifierName("ltu"),
                        ModifierName("nan"),
                        ModifierName("ne"),
                        ModifierName("neu"),
                        ModifierName("num")
                      ),
                      ModifierGroup(
                        ModifierName("and"),
                        ModifierName("or"),
                        ModifierName("xor")
                      ),
                      ModifierGroup(ModifierName("f16x2"))
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName("ftz")),
                    false
                  ),
                  InstructionForm(
                    Opcode("setp"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(
                        ModifierName("eq"),
                        ModifierName("equ"),
                        ModifierName("ge"),
                        ModifierName("geu"),
                        ModifierName("gt"),
                        ModifierName("gtu"),
                        ModifierName("le"),
                        ModifierName("leu"),
                        ModifierName("lt"),
                        ModifierName("ltu"),
                        ModifierName("nan"),
                        ModifierName("ne"),
                        ModifierName("neu"),
                        ModifierName("num")
                      ),
                      ModifierGroup(ModifierName("bf16"))
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Opcode("setp"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(
                        ModifierName("eq"),
                        ModifierName("equ"),
                        ModifierName("ge"),
                        ModifierName("geu"),
                        ModifierName("gt"),
                        ModifierName("gtu"),
                        ModifierName("le"),
                        ModifierName("leu"),
                        ModifierName("lt"),
                        ModifierName("ltu"),
                        ModifierName("nan"),
                        ModifierName("ne"),
                        ModifierName("neu"),
                        ModifierName("num")
                      ),
                      ModifierGroup(
                        ModifierName("and"),
                        ModifierName("or"),
                        ModifierName("xor")
                      ),
                      ModifierGroup(ModifierName("bf16"))
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Opcode("setp"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(
                        ModifierName("eq"),
                        ModifierName("equ"),
                        ModifierName("ge"),
                        ModifierName("geu"),
                        ModifierName("gt"),
                        ModifierName("gtu"),
                        ModifierName("le"),
                        ModifierName("leu"),
                        ModifierName("lt"),
                        ModifierName("ltu"),
                        ModifierName("nan"),
                        ModifierName("ne"),
                        ModifierName("neu"),
                        ModifierName("num")
                      ),
                      ModifierGroup(ModifierName("bf16x2"))
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Opcode("setp"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(
                        ModifierName("eq"),
                        ModifierName("equ"),
                        ModifierName("ge"),
                        ModifierName("geu"),
                        ModifierName("gt"),
                        ModifierName("gtu"),
                        ModifierName("le"),
                        ModifierName("leu"),
                        ModifierName("lt"),
                        ModifierName("ltu"),
                        ModifierName("nan"),
                        ModifierName("ne"),
                        ModifierName("neu"),
                        ModifierName("num")
                      ),
                      ModifierGroup(
                        ModifierName("and"),
                        ModifierName("or"),
                        ModifierName("xor")
                      ),
                      ModifierGroup(ModifierName("bf16x2"))
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  )
                )
              )
            )))
        )
    case Shf extends Definition(
          Opcode("shf"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 4)
              Right(Instruction.Shf(operands(0), operands(1), operands(2), operands(3), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("shf", Vec(4), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("logic-and-shift-instructions-shf"),
                NonEmptyString("9.7.8.7. Logic and Shift Instructions: shf"),
                NonEmptyString("shf"),
                NonEmptyString("Logic and Shift Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(NonEmptyString(
                  "shf.l.mode.b32  d, a, b, c;  // left shift\nshf.r.mode.b32  d, a, b, c;  // right shift\n\n.mode = { .clamp, .wrap };"
                ))),
                Version(PositiveInt(3), NonNegativeInt(1)),
                Target(32),
                Vec(
                  InstructionForm(
                    Opcode("shf"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(ModifierName("l")),
                      ModifierGroup(
                        ModifierName("clamp"),
                        ModifierName("wrap")
                      ),
                      ModifierGroup(ModifierName("b32"))
                    ),
                    _root_.scala.collection.immutable.Set(),
                    true
                  ),
                  InstructionForm(
                    Opcode("shf"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(ModifierName("r")),
                      ModifierGroup(
                        ModifierName("clamp"),
                        ModifierName("wrap")
                      ),
                      ModifierGroup(ModifierName("b32"))
                    ),
                    _root_.scala.collection.immutable.Set(),
                    true
                  )
                )
              )
            )))
        )
    case Shfl extends Definition(
          Opcode("shfl"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 4)
              Right(Instruction.Shfl(operands(0), operands(1), operands(2), operands(3), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("shfl", Vec(4), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("data-movement-and-conversion-instructions-shfl"),
                NonEmptyString("9.7.9.5. Data Movement and Conversion Instructions: shfl (deprecated)"),
                NonEmptyString("shfl (deprecated)"),
                NonEmptyString("Data Movement and Conversion Instructions"),
                NonEmptyVec.unsafeFromVec(
                  Vec(NonEmptyString("shfl.mode.b32  d[|p], a, b, c;\n\n.mode = { .up, .down, .bfly, .idx };"))
                ),
                Version(PositiveInt(3), NonNegativeInt.Zero),
                Target(30),
                Vec(InstructionForm(
                  Opcode("shfl"),
                  Vec(
                    OperandConstraint.Register,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Vec(
                    ModifierGroup(
                      ModifierName("bfly"),
                      ModifierName("down"),
                      ModifierName("idx"),
                      ModifierName("up")
                    ),
                    ModifierGroup(ModifierName("b32"))
                  ),
                  _root_.scala.collection.immutable.Set(),
                  false
                ))
              )
            )))
        )
    case ShflSync extends Definition(
          Opcode("shfl.sync"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 5)
              Right(Instruction.ShflSync(operands(0), operands(1), operands(2), operands(3), operands(4), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("shfl.sync", Vec(5), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("data-movement-and-conversion-instructions-shfl-sync"),
                NonEmptyString("9.7.9.6. Data Movement and Conversion Instructions: shfl.sync"),
                NonEmptyString("shfl.sync"),
                NonEmptyString("Data Movement and Conversion Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(NonEmptyString(
                  "shfl.sync.mode.b32  d[|p], a, b, c, membermask;\n\n.mode = { .up, .down, .bfly, .idx };"
                ))),
                Version(PositiveInt(6), NonNegativeInt.Zero),
                Target(30),
                Vec(InstructionForm(
                  Opcode("shfl.sync"),
                  Vec(
                    OperandConstraint.Register,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Vec(
                    ModifierGroup(
                      ModifierName("bfly"),
                      ModifierName("down"),
                      ModifierName("idx"),
                      ModifierName("up")
                    ),
                    ModifierGroup(ModifierName("b32"))
                  ),
                  _root_.scala.collection.immutable.Set(),
                  true
                ))
              )
            )))
        )
    case Shl extends Definition(
          Opcode("shl"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 3) Right(Instruction.Shl(operands(0), operands(1), operands(2), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("shl", Vec(3), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("logic-and-shift-instructions-shl"),
                NonEmptyString("9.7.8.8. Logic and Shift Instructions: shl"),
                NonEmptyString("shl"),
                NonEmptyString("Logic and Shift Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(NonEmptyString("shl.type d, a, b;\n\n.type = { .b16, .b32, .b64 };"))),
                Version(PositiveInt(1), NonNegativeInt.Zero),
                Target(10),
                Vec(InstructionForm(
                  Opcode("shl"),
                  Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                  Vec(ModifierGroup(
                    ModifierName("b16"),
                    ModifierName("b32"),
                    ModifierName("b64")
                  )),
                  _root_.scala.collection.immutable.Set(),
                  true
                ))
              )
            )))
        )
    case Shr extends Definition(
          Opcode("shr"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 3) Right(Instruction.Shr(operands(0), operands(1), operands(2), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("shr", Vec(3), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("logic-and-shift-instructions-shr"),
                NonEmptyString("9.7.8.9. Logic and Shift Instructions: shr"),
                NonEmptyString("shr"),
                NonEmptyString("Logic and Shift Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(NonEmptyString(
                  "shr.type d, a, b;\n\n.type = { .b16, .b32, .b64,\n          .u16, .u32, .u64,\n          .s16, .s32, .s64 };"
                ))),
                Version(PositiveInt(1), NonNegativeInt.Zero),
                Target(10),
                Vec(InstructionForm(
                  Opcode("shr"),
                  Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                  Vec(ModifierGroup(
                    ModifierName("b16"),
                    ModifierName("b32"),
                    ModifierName("b64"),
                    ModifierName("s16"),
                    ModifierName("s32"),
                    ModifierName("s64"),
                    ModifierName("u16"),
                    ModifierName("u32"),
                    ModifierName("u64")
                  )),
                  _root_.scala.collection.immutable.Set(),
                  true
                ))
              )
            )))
        )
    case Sin extends Definition(
          Opcode("sin"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 2) Right(Instruction.Sin(operands(0), operands(1), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("sin", Vec(2), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("floating-point-instructions-sin"),
                NonEmptyString("9.7.3.18. Floating Point Instructions: sin"),
                NonEmptyString("sin"),
                NonEmptyString("Floating Point Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(NonEmptyString("sin.approx{.ftz}.f32  d, a;"))),
                Version(PositiveInt(1), NonNegativeInt.Zero),
                Target(10),
                Vec(InstructionForm(
                  Opcode("sin"),
                  Vec(OperandConstraint.Register, OperandConstraint.Value),
                  Vec(
                    ModifierGroup(ModifierName("approx")),
                    ModifierGroup(ModifierName("f32"))
                  ),
                  _root_.scala.collection.immutable.Set(ModifierName("ftz")),
                  true
                ))
              )
            )))
        )
    case Slct extends Definition(
          Opcode("slct"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 4)
              Right(Instruction.Slct(operands(0), operands(1), operands(2), operands(3), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("slct", Vec(4), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("comparison-and-selection-instructions-slct"),
                NonEmptyString("9.7.6.4. Comparison and Selection Instructions: slct"),
                NonEmptyString("slct"),
                NonEmptyString("Comparison and Selection Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(NonEmptyString(
                  "slct.dtype.s32        d, a, b, c;\nslct{.ftz}.dtype.f32  d, a, b, c;\n\n.dtype = { .b16, .b32, .b64,\n           .u16, .u32, .u64,\n           .s16, .s32, .s64,\n                 .f32, .f64 };"
                ))),
                Version(PositiveInt(1), NonNegativeInt.Zero),
                Target(13),
                Vec(
                  InstructionForm(
                    Opcode("slct"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(
                        ModifierName("b16"),
                        ModifierName("b32"),
                        ModifierName("b64"),
                        ModifierName("f32"),
                        ModifierName("f64"),
                        ModifierName("s16"),
                        ModifierName("s32"),
                        ModifierName("s64"),
                        ModifierName("u16"),
                        ModifierName("u32"),
                        ModifierName("u64")
                      ),
                      ModifierGroup(ModifierName("s32"))
                    ),
                    _root_.scala.collection.immutable.Set(),
                    true
                  ),
                  InstructionForm(
                    Opcode("slct"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(
                        ModifierName("b16"),
                        ModifierName("b32"),
                        ModifierName("b64"),
                        ModifierName("f32"),
                        ModifierName("f64"),
                        ModifierName("s16"),
                        ModifierName("s32"),
                        ModifierName("s64"),
                        ModifierName("u16"),
                        ModifierName("u32"),
                        ModifierName("u64")
                      ),
                      ModifierGroup(ModifierName("f32"))
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName("ftz")),
                    true
                  )
                )
              )
            )))
        )
    case Sqrt extends Definition(
          Opcode("sqrt"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 2) Right(Instruction.Sqrt(operands(0), operands(1), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("sqrt", Vec(2), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("floating-point-instructions-sqrt"),
                NonEmptyString("9.7.3.15. Floating Point Instructions: sqrt"),
                NonEmptyString("sqrt"),
                NonEmptyString("Floating Point Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(NonEmptyString(
                  "sqrt.approx{.ftz}.f32  d, a; // fast, approximate square root\nsqrt.rnd{.ftz}.f32     d, a; // IEEE 754 compliant rounding\nsqrt.rnd.f64           d, a; // IEEE 754 compliant rounding\n\n.rnd = { .rn, .rz, .rm, .rp };"
                ))),
                Version(PositiveInt(1), NonNegativeInt.Zero),
                Target(13),
                Vec(
                  InstructionForm(
                    Opcode("sqrt"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(ModifierName("approx")),
                      ModifierGroup(ModifierName("f32"))
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName("ftz")),
                    false
                  ),
                  InstructionForm(
                    Opcode("sqrt"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(
                        ModifierName("rm"),
                        ModifierName("rn"),
                        ModifierName("rp"),
                        ModifierName("rz")
                      ),
                      ModifierGroup(ModifierName("f32"))
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName("ftz")),
                    false
                  ),
                  InstructionForm(
                    Opcode("sqrt"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(
                        ModifierName("rm"),
                        ModifierName("rn"),
                        ModifierName("rp"),
                        ModifierName("rz")
                      ),
                      ModifierGroup(ModifierName("f64"))
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  )
                )
              )
            )))
        )
    case St extends Definition(
          Opcode("st"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 2 || operands.size == 3)
              Right(Instruction.St(operands(0), operands(1), operands.lift(2), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("st", Vec(2, 3), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("data-movement-and-conversion-instructions-st"),
                NonEmptyString("9.7.9.11. Data Movement and Conversion Instructions: st"),
                NonEmptyString("st"),
                NonEmptyString("Data Movement and Conversion Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(NonEmptyString(
                  "st{.weak}{.ss}{.cop}{.level::cache_hint}{.vec}.type   [a], b{, cache_policy};\nst{.weak}{.ss}{.level1::eviction_priority}{.level2::eviction_priority}{.level::cache_hint}{.vec}.type\n                                                      [a], b{, cache_policy};\nst.volatile{.ss}{.vec}.type                           [a], b;\nst.relaxed.scope{.ss}{.level1::eviction_priority}{.level2::eviction_priority}{.level::cache_hint}{.vec}.type\n                                                      [a], b{, cache_policy};\nst.release.scope{.ss}{.level1::eviction_priority}{.level2::eviction_priority}{.level::cache_hint}{.vec}.type\n                                                      [a], b{, cache_policy};\nst.mmio.sem.sys{.global}.type         [a], b;\n\n.ss =                       { .global, .local, .param{::func}, .shared{::cta, ::cluster} };\n.sem =                      { .release, .relaxed };\n.level1::eviction_priority = { .L1::evict_normal, .L1::evict_unchanged,\n                               .L1::evict_first, .L1::evict_last, .L1::no_allocate };\n.level2::eviction_priority = { .L2::evict_normal, .L2::evict_first, .L2::evict_last };\n.level::cache_hint =        { .L2::cache_hint };\n.cop =                      { .wb, .cg, .cs, .wt };\n.sem =                      { .relaxed, .release };\n.scope =                    { .cta, .cluster, .gpu, .sys };\n.vec =                      { .v2, .v4, .v8 };\n.type =                     { .b8, .b16, .b32, .b64, .b128,\n                              .u8, .u16, .u32, .u64,\n                              .s8, .s16, .s32, .s64,\n                              .f32, .f64 };"
                ))),
                Version(PositiveInt(1), NonNegativeInt.Zero),
                Target(13),
                Vec(
                  InstructionForm(
                    Opcode("st"),
                    Vec(OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(ModifierGroup(
                      ModifierName("b128"),
                      ModifierName("b16"),
                      ModifierName("b32"),
                      ModifierName("b64"),
                      ModifierName("b8"),
                      ModifierName("f32"),
                      ModifierName("f64"),
                      ModifierName("s16"),
                      ModifierName("s32"),
                      ModifierName("s64"),
                      ModifierName("s8"),
                      ModifierName("u16"),
                      ModifierName("u32"),
                      ModifierName("u64"),
                      ModifierName("u8")
                    )),
                    _root_.scala.collection.immutable.Set(
                      ModifierName("L2::cache_hint"),
                      ModifierName("cg"),
                      ModifierName("cs"),
                      ModifierName("global"),
                      ModifierName("local"),
                      ModifierName("v2"),
                      ModifierName("v4"),
                      ModifierName("v8"),
                      ModifierName("wb"),
                      ModifierName("weak"),
                      ModifierName("wt")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode("st"),
                    Vec(OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(ModifierGroup(
                      ModifierName("b128"),
                      ModifierName("b16"),
                      ModifierName("b32"),
                      ModifierName("b64"),
                      ModifierName("b8"),
                      ModifierName("f32"),
                      ModifierName("f64"),
                      ModifierName("s16"),
                      ModifierName("s32"),
                      ModifierName("s64"),
                      ModifierName("s8"),
                      ModifierName("u16"),
                      ModifierName("u32"),
                      ModifierName("u64"),
                      ModifierName("u8")
                    )),
                    _root_.scala.collection.immutable.Set(
                      ModifierName("L1::evict_first"),
                      ModifierName("L1::evict_last"),
                      ModifierName("L1::evict_normal"),
                      ModifierName("L1::evict_unchanged"),
                      ModifierName("L1::no_allocate"),
                      ModifierName("L2::cache_hint"),
                      ModifierName("L2::evict_first"),
                      ModifierName("L2::evict_last"),
                      ModifierName("L2::evict_normal"),
                      ModifierName("global"),
                      ModifierName("local"),
                      ModifierName("v2"),
                      ModifierName("v4"),
                      ModifierName("v8"),
                      ModifierName("weak")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode("st"),
                    Vec(OperandConstraint.Address, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(ModifierName("volatile")),
                      ModifierGroup(
                        ModifierName("b128"),
                        ModifierName("b16"),
                        ModifierName("b32"),
                        ModifierName("b64"),
                        ModifierName("b8"),
                        ModifierName("f32"),
                        ModifierName("f64"),
                        ModifierName("s16"),
                        ModifierName("s32"),
                        ModifierName("s64"),
                        ModifierName("s8"),
                        ModifierName("u16"),
                        ModifierName("u32"),
                        ModifierName("u64"),
                        ModifierName("u8")
                      )
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName("global"),
                      ModifierName("local"),
                      ModifierName("v2"),
                      ModifierName("v4"),
                      ModifierName("v8")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode("st"),
                    Vec(OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(ModifierName("relaxed")),
                      ModifierGroup(
                        ModifierName("cluster"),
                        ModifierName("cta"),
                        ModifierName("gpu"),
                        ModifierName("sys")
                      ),
                      ModifierGroup(
                        ModifierName("b128"),
                        ModifierName("b16"),
                        ModifierName("b32"),
                        ModifierName("b64"),
                        ModifierName("b8"),
                        ModifierName("f32"),
                        ModifierName("f64"),
                        ModifierName("s16"),
                        ModifierName("s32"),
                        ModifierName("s64"),
                        ModifierName("s8"),
                        ModifierName("u16"),
                        ModifierName("u32"),
                        ModifierName("u64"),
                        ModifierName("u8")
                      )
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName("L1::evict_first"),
                      ModifierName("L1::evict_last"),
                      ModifierName("L1::evict_normal"),
                      ModifierName("L1::evict_unchanged"),
                      ModifierName("L1::no_allocate"),
                      ModifierName("L2::cache_hint"),
                      ModifierName("L2::evict_first"),
                      ModifierName("L2::evict_last"),
                      ModifierName("L2::evict_normal"),
                      ModifierName("global"),
                      ModifierName("local"),
                      ModifierName("v2"),
                      ModifierName("v4"),
                      ModifierName("v8")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode("st"),
                    Vec(OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(ModifierName("release")),
                      ModifierGroup(
                        ModifierName("cluster"),
                        ModifierName("cta"),
                        ModifierName("gpu"),
                        ModifierName("sys")
                      ),
                      ModifierGroup(
                        ModifierName("b128"),
                        ModifierName("b16"),
                        ModifierName("b32"),
                        ModifierName("b64"),
                        ModifierName("b8"),
                        ModifierName("f32"),
                        ModifierName("f64"),
                        ModifierName("s16"),
                        ModifierName("s32"),
                        ModifierName("s64"),
                        ModifierName("s8"),
                        ModifierName("u16"),
                        ModifierName("u32"),
                        ModifierName("u64"),
                        ModifierName("u8")
                      )
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName("L1::evict_first"),
                      ModifierName("L1::evict_last"),
                      ModifierName("L1::evict_normal"),
                      ModifierName("L1::evict_unchanged"),
                      ModifierName("L1::no_allocate"),
                      ModifierName("L2::cache_hint"),
                      ModifierName("L2::evict_first"),
                      ModifierName("L2::evict_last"),
                      ModifierName("L2::evict_normal"),
                      ModifierName("global"),
                      ModifierName("local"),
                      ModifierName("v2"),
                      ModifierName("v4"),
                      ModifierName("v8")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode("st"),
                    Vec(OperandConstraint.Address, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(ModifierName("mmio")),
                      ModifierGroup(
                        ModifierName("relaxed"),
                        ModifierName("release")
                      ),
                      ModifierGroup(ModifierName("sys")),
                      ModifierGroup(
                        ModifierName("b128"),
                        ModifierName("b16"),
                        ModifierName("b32"),
                        ModifierName("b64"),
                        ModifierName("b8"),
                        ModifierName("f32"),
                        ModifierName("f64"),
                        ModifierName("s16"),
                        ModifierName("s32"),
                        ModifierName("s64"),
                        ModifierName("s8"),
                        ModifierName("u16"),
                        ModifierName("u32"),
                        ModifierName("u64"),
                        ModifierName("u8")
                      )
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName("global")),
                    false
                  )
                )
              )
            )))
        )
    case StAsync extends Definition(
          Opcode("st.async"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 2 || operands.size == 3)
              Right(Instruction.StAsync(operands(0), operands(1), operands.lift(2), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("st.async", Vec(2, 3), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("data-movement-and-conversion-instructions-st-async"),
                NonEmptyString("9.7.9.12. Data Movement and Conversion Instructions: st.async"),
                NonEmptyString("st.async"),
                NonEmptyString("Data Movement and Conversion Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(NonEmptyString(
                  "st.async{.weak}{.ss}.completion_mechanism{.vec}.type [a], b, [mbar];\n\n.ss   =                 { .shared::cluster };\n.type =                 { .b32, .b64, .b128,\n                          .u32, .u64,\n                          .s32, .s64,\n                          .f32, .f64 };\n.vec  =                 { .v2, .v4 };\n.completion_mechanism = { .mbarrier::complete_tx::bytes };\n\nst.async{.mmio}.sem.scope{.ss}.type [a], b;\n\n.sem =                  { .release };\n.scope =                { .gpu, .sys };\n.ss =                   { .global };\n.type =                 { .b8, .b16, .b32, .b64,\n                          .u8, .u16, .u32, .u64,\n                          .s8, .s16, .s32, .s64,\n                                     .f32, .f64 };"
                ))),
                Version(PositiveInt(8), NonNegativeInt(1)),
                Target(90),
                Vec(
                  InstructionForm(
                    Opcode("st.async"),
                    Vec(OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Address),
                    Vec(
                      ModifierGroup(ModifierName("mbarrier::complete_tx::bytes")),
                      ModifierGroup(
                        ModifierName("b16"),
                        ModifierName("b32"),
                        ModifierName("b64"),
                        ModifierName("b8"),
                        ModifierName("f32"),
                        ModifierName("f64"),
                        ModifierName("s16"),
                        ModifierName("s32"),
                        ModifierName("s64"),
                        ModifierName("s8"),
                        ModifierName("u16"),
                        ModifierName("u32"),
                        ModifierName("u64"),
                        ModifierName("u8")
                      )
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName("global"),
                      ModifierName("v2"),
                      ModifierName("v4"),
                      ModifierName("weak")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode("st.async"),
                    Vec(OperandConstraint.Address, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(ModifierName("release")),
                      ModifierGroup(
                        ModifierName("gpu"),
                        ModifierName("sys")
                      ),
                      ModifierGroup(
                        ModifierName("b16"),
                        ModifierName("b32"),
                        ModifierName("b64"),
                        ModifierName("b8"),
                        ModifierName("f32"),
                        ModifierName("f64"),
                        ModifierName("s16"),
                        ModifierName("s32"),
                        ModifierName("s64"),
                        ModifierName("s8"),
                        ModifierName("u16"),
                        ModifierName("u32"),
                        ModifierName("u64"),
                        ModifierName("u8")
                      )
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName("global"), ModifierName("mmio")),
                    false
                  )
                )
              )
            )))
        )
    case StBulk extends Definition(
          Opcode("st.bulk"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 3) Right(Instruction.StBulk(operands(0), operands(1), operands(2), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("st.bulk", Vec(3), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("data-movement-and-conversion-instructions-st-bulk"),
                NonEmptyString("9.7.9.14. Data Movement and Conversion Instructions: st.bulk"),
                NonEmptyString("st.bulk"),
                NonEmptyString("Data Movement and Conversion Instructions"),
                NonEmptyVec.unsafeFromVec(
                  Vec(NonEmptyString("st.bulk{.weak}{.shared::cta}  [a], size, initval; // initval must be zero"))
                ),
                Version(PositiveInt(8), NonNegativeInt(6)),
                Target(100),
                Vec(InstructionForm(
                  Opcode("st.bulk"),
                  Vec(OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value),
                  Vec(),
                  _root_.scala.collection.immutable.Set(
                    ModifierName("shared::cta"),
                    ModifierName("weak")
                  ),
                  true
                ))
              )
            )))
        )
    case StackRestore extends Definition(
          Opcode("stackrestore"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 1) Right(Instruction.StackRestore(operands(0), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("stackrestore", Vec(1), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("stack-manipulation-instructions-stackrestore"),
                NonEmptyString("9.7.18.2. Stack Manipulation Instructions: stackrestore"),
                NonEmptyString("stackrestore"),
                NonEmptyString("Stack Manipulation Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(NonEmptyString("stackrestore.type  a;\n\n.type = { .u32, .u64 };"))),
                Version(PositiveInt(7), NonNegativeInt(3)),
                Target(52),
                Vec(InstructionForm(
                  Opcode("stackrestore"),
                  Vec(OperandConstraint.Value),
                  Vec(ModifierGroup(
                    ModifierName("u32"),
                    ModifierName("u64")
                  )),
                  _root_.scala.collection.immutable.Set(),
                  true
                ))
              )
            )))
        )
    case StackSave extends Definition(
          Opcode("stacksave"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 1) Right(Instruction.StackSave(operands(0), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("stacksave", Vec(1), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("stack-manipulation-instructions-stacksave"),
                NonEmptyString("9.7.18.1. Stack Manipulation Instructions: stacksave"),
                NonEmptyString("stacksave"),
                NonEmptyString("Stack Manipulation Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(NonEmptyString("stacksave.type  d;\n\n.type = { .u32, .u64 };"))),
                Version(PositiveInt(7), NonNegativeInt(3)),
                Target(52),
                Vec(InstructionForm(
                  Opcode("stacksave"),
                  Vec(OperandConstraint.Register),
                  Vec(ModifierGroup(
                    ModifierName("u32"),
                    ModifierName("u64")
                  )),
                  _root_.scala.collection.immutable.Set(),
                  true
                ))
              )
            )))
        )
    case Sub extends Definition(
          Opcode("sub"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 3) Right(Instruction.Sub(operands(0), operands(1), operands(2), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("sub", Vec(3), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("integer-arithmetic-instructions-sub"),
                NonEmptyString("9.7.1.2. Integer Arithmetic Instructions: sub"),
                NonEmptyString("sub"),
                NonEmptyString("Integer Arithmetic Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(NonEmptyString(
                  "sub.type1        d, a, b;\nsub{.sat}.type2  d, a, b;\n\n.type1 = { .u16, .u32, .u64,\n           .s16, .s64 };\n\n.type2 = { .s32, .u8x4, .s8x4 };"
                ))),
                Version(PositiveInt(1), NonNegativeInt.Zero),
                Target(10),
                Vec(
                  InstructionForm(
                    Opcode("sub"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(ModifierGroup(
                      ModifierName("s16"),
                      ModifierName("s64"),
                      ModifierName("u16"),
                      ModifierName("u32"),
                      ModifierName("u64")
                    )),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Opcode("sub"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(ModifierGroup(
                      ModifierName("s32"),
                      ModifierName("s8x4"),
                      ModifierName("u8x4")
                    )),
                    _root_.scala.collection.immutable.Set(ModifierName("sat")),
                    false
                  )
                )
              ),
              Metadata.Entry(
                NonEmptyString("floating-point-instructions-sub"),
                NonEmptyString("9.7.3.4. Floating Point Instructions: sub"),
                NonEmptyString("sub"),
                NonEmptyString("Floating Point Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(NonEmptyString(
                  "sub{.rnd}{.ftz}{.sat}.f32  d, a, b;\nsub{.rnd}{.ftz}.f32x2      d, a, b;\nsub{.rnd}.f64              d, a, b;\n\n.rnd = { .rn, .rz, .rm, .rp };"
                ))),
                Version(PositiveInt(1), NonNegativeInt.Zero),
                Target(13),
                Vec(
                  InstructionForm(
                    Opcode("sub"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(ModifierGroup(ModifierName("f32"))),
                    _root_.scala.collection.immutable.Set(
                      ModifierName("ftz"),
                      ModifierName("rm"),
                      ModifierName("rn"),
                      ModifierName("rp"),
                      ModifierName("rz"),
                      ModifierName("sat")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode("sub"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(ModifierGroup(ModifierName("f32x2"))),
                    _root_.scala.collection.immutable.Set(
                      ModifierName("ftz"),
                      ModifierName("rm"),
                      ModifierName("rn"),
                      ModifierName("rp"),
                      ModifierName("rz")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode("sub"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(ModifierGroup(ModifierName("f64"))),
                    _root_.scala.collection.immutable.Set(
                      ModifierName("rm"),
                      ModifierName("rn"),
                      ModifierName("rp"),
                      ModifierName("rz")
                    ),
                    false
                  )
                )
              ),
              Metadata.Entry(
                NonEmptyString("half-precision-floating-point-instructions-sub"),
                NonEmptyString("9.7.4.2. Half Precision Floating Point Instructions: sub"),
                NonEmptyString("sub"),
                NonEmptyString("Half Precision Floating Point Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(NonEmptyString(
                  "sub{.rnd}{.ftz}{.sat}.f16   d, a, b;\nsub{.rnd}{.ftz}{.sat}.f16x2 d, a, b;\n\nsub{.rnd}.bf16   d, a, b;\nsub{.rnd}.bf16x2 d, a, b;\n\n.rnd = { .rn };"
                ))),
                Version(PositiveInt(4), NonNegativeInt(2)),
                Target(53),
                Vec(
                  InstructionForm(
                    Opcode("sub"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(ModifierGroup(ModifierName("f16"))),
                    _root_.scala.collection.immutable.Set(
                      ModifierName("ftz"),
                      ModifierName("rn"),
                      ModifierName("sat")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode("sub"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(ModifierGroup(ModifierName("f16x2"))),
                    _root_.scala.collection.immutable.Set(
                      ModifierName("ftz"),
                      ModifierName("rn"),
                      ModifierName("sat")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode("sub"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(ModifierGroup(ModifierName("bf16"))),
                    _root_.scala.collection.immutable.Set(ModifierName("rn")),
                    false
                  ),
                  InstructionForm(
                    Opcode("sub"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(ModifierGroup(ModifierName("bf16x2"))),
                    _root_.scala.collection.immutable.Set(ModifierName("rn")),
                    false
                  )
                )
              ),
              Metadata.Entry(
                NonEmptyString("mixed-precision-floating-point-instructions-sub"),
                NonEmptyString("9.7.5.2. Mixed Precision Floating Point Instructions: sub"),
                NonEmptyString("sub"),
                NonEmptyString("Mixed Precision Floating Point Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(NonEmptyString(
                  "sub{.rnd}{.sat}.f32.atype  d, a, c;\n\n.atype = { .f16, .bf16};\n.rnd   = { .rn, .rz, .rm, .rp };"
                ))),
                Version(PositiveInt(1), NonNegativeInt.Zero),
                Target(100),
                Vec(InstructionForm(
                  Opcode("sub"),
                  Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                  Vec(
                    ModifierGroup(ModifierName("f32")),
                    ModifierGroup(
                      ModifierName("bf16"),
                      ModifierName("f16")
                    )
                  ),
                  _root_.scala.collection.immutable.Set(
                    ModifierName("rm"),
                    ModifierName("rn"),
                    ModifierName("rp"),
                    ModifierName("rz"),
                    ModifierName("sat")
                  ),
                  true
                ))
              )
            )))
        )
    case SubCc extends Definition(
          Opcode("sub.cc"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 3) Right(Instruction.SubCc(operands(0), operands(1), operands(2), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("sub.cc", Vec(3), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("extended-precision-arithmetic-instructions-sub-cc"),
                NonEmptyString("9.7.2.3. Extended-Precision Arithmetic Instructions: sub.cc"),
                NonEmptyString("sub.cc"),
                NonEmptyString("Extended-Precision Arithmetic Instructions"),
                NonEmptyVec.unsafeFromVec(
                  Vec(NonEmptyString("sub.cc.type  d, a, b;\n\n.type = { .u32, .s32, .u64, .s64 };"))
                ),
                Version(PositiveInt(1), NonNegativeInt.Zero),
                Target(20),
                Vec(InstructionForm(
                  Opcode("sub.cc"),
                  Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                  Vec(ModifierGroup(
                    ModifierName("s32"),
                    ModifierName("s64"),
                    ModifierName("u32"),
                    ModifierName("u64")
                  )),
                  _root_.scala.collection.immutable.Set(),
                  true
                ))
              )
            )))
        )
    case Subc extends Definition(
          Opcode("subc"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 3) Right(Instruction.Subc(operands(0), operands(1), operands(2), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("subc", Vec(3), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("extended-precision-arithmetic-instructions-subc"),
                NonEmptyString("9.7.2.4. Extended-Precision Arithmetic Instructions: subc"),
                NonEmptyString("subc"),
                NonEmptyString("Extended-Precision Arithmetic Instructions"),
                NonEmptyVec.unsafeFromVec(
                  Vec(NonEmptyString("subc{.cc}.type  d, a, b;\n\n.type = { .u32, .s32, .u64, .s64 };"))
                ),
                Version(PositiveInt(1), NonNegativeInt.Zero),
                Target(20),
                Vec(InstructionForm(
                  Opcode("subc"),
                  Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                  Vec(ModifierGroup(
                    ModifierName("s32"),
                    ModifierName("s64"),
                    ModifierName("u32"),
                    ModifierName("u64")
                  )),
                  _root_.scala.collection.immutable.Set(ModifierName("cc")),
                  true
                ))
              )
            )))
        )
    case Suld extends Definition(
          Opcode("suld"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 2) Right(Instruction.Suld(operands(0), operands(1), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("suld", Vec(2), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("surface-instructions-suld"),
                NonEmptyString("9.7.12.1. Surface Instructions: suld"),
                NonEmptyString("suld"),
                NonEmptyString("Surface Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(NonEmptyString(
                  "suld.b.geom{.cop}.vec.dtype.clamp  d, [a, b];  // unformatted\n\n.geom  = { .1d, .2d, .3d, .a1d, .a2d };\n.cop   = { .ca, .cg, .cs, .cv };               // cache operation\n.vec   = { none, .v2, .v4 };\n.dtype = { .b8 , .b16, .b32, .b64 };\n.clamp = { .trap, .clamp, .zero };"
                ))),
                Version(PositiveInt(1), NonNegativeInt.Zero),
                Target(20),
                Vec(InstructionForm(
                  Opcode("suld"),
                  Vec(OperandConstraint.Register, OperandConstraint.Address),
                  Vec(
                    ModifierGroup(ModifierName("b")),
                    ModifierGroup(
                      ModifierName("a1d"),
                      ModifierName("a2d")
                    ),
                    ModifierGroup(
                      ModifierName("none"),
                      ModifierName("v2"),
                      ModifierName("v4")
                    ),
                    ModifierGroup(
                      ModifierName("b16"),
                      ModifierName("b32"),
                      ModifierName("b64"),
                      ModifierName("b8")
                    ),
                    ModifierGroup(
                      ModifierName("clamp"),
                      ModifierName("trap"),
                      ModifierName("zero")
                    )
                  ),
                  _root_.scala.collection.immutable.Set(
                    ModifierName("ca"),
                    ModifierName("cg"),
                    ModifierName("cs"),
                    ModifierName("cv")
                  ),
                  true
                ))
              )
            )))
        )
    case Suq extends Definition(
          Opcode("suq"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 2) Right(Instruction.Suq(operands(0), operands(1), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("suq", Vec(2), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("surface-instructions-suq"),
                NonEmptyString("9.7.12.4. Surface Instructions: suq"),
                NonEmptyString("suq"),
                NonEmptyString("Surface Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(NonEmptyString(
                  "suq.query.b32   d, [a];\n\n.query = { .width, .height, .depth,\n           .channel_data_type, .channel_order,\n           .array_size, .memory_layout };"
                ))),
                Version(PositiveInt(1), NonNegativeInt(5)),
                Target(10),
                Vec(InstructionForm(
                  Opcode("suq"),
                  Vec(OperandConstraint.Register, OperandConstraint.Address),
                  Vec(
                    ModifierGroup(
                      ModifierName("array_size"),
                      ModifierName("channel_data_type"),
                      ModifierName("channel_order"),
                      ModifierName("depth"),
                      ModifierName("height"),
                      ModifierName("memory_layout"),
                      ModifierName("width")
                    ),
                    ModifierGroup(ModifierName("b32"))
                  ),
                  _root_.scala.collection.immutable.Set(),
                  false
                ))
              )
            )))
        )
    case Sured extends Definition(
          Opcode("sured"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 2) Right(Instruction.Sured(operands(0), operands(1), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("sured", Vec(2), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("surface-instructions-sured"),
                NonEmptyString("9.7.12.3. Surface Instructions: sured"),
                NonEmptyString("sured"),
                NonEmptyString("Surface Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(NonEmptyString(
                  "sured.b.op.geom.ctype.clamp  [a,b],c; // byte addressing\nsured.p.op.geom.ctype.clamp  [a,b],c; // sample addressing\n\n.op    = { .add, .min, .max, .and, .or };\n.geom  = { .1d, .2d, .3d };\n.ctype = { .u32, .u64, .s32, .b32, .s64 };  // for sured.b\n.ctype = { .b32, .b64 };                    // for sured.p\n.clamp = { .trap, .clamp, .zero };"
                ))),
                Version(PositiveInt(2), NonNegativeInt.Zero),
                Target(20),
                Vec(
                  InstructionForm(
                    Opcode("sured"),
                    Vec(OperandConstraint.Address, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(ModifierName("b")),
                      ModifierGroup(
                        ModifierName("add"),
                        ModifierName("and"),
                        ModifierName("max"),
                        ModifierName("min"),
                        ModifierName("or")
                      ),
                      ModifierGroup(
                        ModifierName("b32"),
                        ModifierName("b64")
                      ),
                      ModifierGroup(
                        ModifierName("clamp"),
                        ModifierName("trap"),
                        ModifierName("zero")
                      )
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Opcode("sured"),
                    Vec(OperandConstraint.Address, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(ModifierName("p")),
                      ModifierGroup(
                        ModifierName("add"),
                        ModifierName("and"),
                        ModifierName("max"),
                        ModifierName("min"),
                        ModifierName("or")
                      ),
                      ModifierGroup(
                        ModifierName("b32"),
                        ModifierName("b64")
                      ),
                      ModifierGroup(
                        ModifierName("clamp"),
                        ModifierName("trap"),
                        ModifierName("zero")
                      )
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  )
                )
              )
            )))
        )
    case Sust extends Definition(
          Opcode("sust"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 2) Right(Instruction.Sust(operands(0), operands(1), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("sust", Vec(2), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("surface-instructions-sust"),
                NonEmptyString("9.7.12.2. Surface Instructions: sust"),
                NonEmptyString("sust"),
                NonEmptyString("Surface Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(NonEmptyString(
                  "sust.b.{1d,2d,3d}{.cop}.vec.ctype.clamp  [a, b], c;  // unformatted\nsust.p.{1d,2d,3d}.vec.b32.clamp          [a, b], c;  // formatted\n\nsust.b.{a1d,a2d}{.cop}.vec.ctype.clamp   [a, b], c;  // unformatted\n\n.cop   = { .wb, .cg, .cs, .wt };                     // cache operation\n.vec   = { none, .v2, .v4 };\n.ctype = { .b8 , .b16, .b32, .b64 };\n.clamp = { .trap, .clamp, .zero };"
                ))),
                Version(PositiveInt(1), NonNegativeInt.Zero),
                Target(20),
                Vec(
                  InstructionForm(
                    Opcode("sust"),
                    Vec(OperandConstraint.Address, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(ModifierName("b")),
                      ModifierGroup(
                        ModifierName("none"),
                        ModifierName("v2"),
                        ModifierName("v4")
                      ),
                      ModifierGroup(
                        ModifierName("b16"),
                        ModifierName("b32"),
                        ModifierName("b64"),
                        ModifierName("b8")
                      ),
                      ModifierGroup(
                        ModifierName("clamp"),
                        ModifierName("trap"),
                        ModifierName("zero")
                      )
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName("cg"),
                      ModifierName("cs"),
                      ModifierName("wb"),
                      ModifierName("wt")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode("sust"),
                    Vec(OperandConstraint.Address, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(ModifierName("p")),
                      ModifierGroup(
                        ModifierName("none"),
                        ModifierName("v2"),
                        ModifierName("v4")
                      ),
                      ModifierGroup(ModifierName("b32")),
                      ModifierGroup(
                        ModifierName("clamp"),
                        ModifierName("trap"),
                        ModifierName("zero")
                      )
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  )
                )
              )
            )))
        )
    case Szext extends Definition(
          Opcode("szext"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 3) Right(Instruction.Szext(operands(0), operands(1), operands(2), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("szext", Vec(3), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("integer-arithmetic-instructions-szext"),
                NonEmptyString("9.7.1.22. Integer Arithmetic Instructions: szext"),
                NonEmptyString("szext"),
                NonEmptyString("Integer Arithmetic Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(
                  NonEmptyString("szext.mode.type  d, a, b;\n\n.mode = { .clamp, .wrap };\n.type = { .u32, .s32 };")
                )),
                Version(PositiveInt(7), NonNegativeInt(6)),
                Target(70),
                Vec(InstructionForm(
                  Opcode("szext"),
                  Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                  Vec(
                    ModifierGroup(
                      ModifierName("clamp"),
                      ModifierName("wrap")
                    ),
                    ModifierGroup(
                      ModifierName("s32"),
                      ModifierName("u32")
                    )
                  ),
                  _root_.scala.collection.immutable.Set(),
                  true
                ))
              )
            )))
        )
    case Tanh extends Definition(
          Opcode("tanh"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 2) Right(Instruction.Tanh(operands(0), operands(1), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("tanh", Vec(2), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("floating-point-instructions-tanh"),
                NonEmptyString("9.7.3.22. Floating Point Instructions: tanh"),
                NonEmptyString("tanh"),
                NonEmptyString("Floating Point Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(NonEmptyString("tanh.approx.f32 d, a;"))),
                Version(PositiveInt(7), NonNegativeInt.Zero),
                Target(75),
                Vec(InstructionForm(
                  Opcode("tanh"),
                  Vec(OperandConstraint.Register, OperandConstraint.Value),
                  Vec(
                    ModifierGroup(ModifierName("approx")),
                    ModifierGroup(ModifierName("f32"))
                  ),
                  _root_.scala.collection.immutable.Set(),
                  true
                ))
              ),
              Metadata.Entry(
                NonEmptyString("half-precision-floating-point-instructions-tanh"),
                NonEmptyString("9.7.4.9. Half Precision Floating Point Instructions: tanh"),
                NonEmptyString("tanh"),
                NonEmptyString("Half Precision Floating Point Instructions"),
                NonEmptyVec.unsafeFromVec(
                  Vec(NonEmptyString("tanh.approx.type d, a;\n\n.type = {.f16, .f16x2, .bf16, .bf16x2}"))
                ),
                Version(PositiveInt(7), NonNegativeInt.Zero),
                Target(75),
                Vec(InstructionForm(
                  Opcode("tanh"),
                  Vec(OperandConstraint.Register, OperandConstraint.Value),
                  Vec(
                    ModifierGroup(ModifierName("approx")),
                    ModifierGroup(
                      ModifierName("bf16"),
                      ModifierName("bf16x2"),
                      ModifierName("f16"),
                      ModifierName("f16x2")
                    )
                  ),
                  _root_.scala.collection.immutable.Set(),
                  false
                ))
              )
            )))
        )
    case Tcgen05Alloc extends Definition(
          Opcode("tcgen05.alloc"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 2) Right(Instruction.Tcgen05Alloc(operands(0), operands(1), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("tcgen05.alloc", Vec(2), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("tcgen05-instructions-tcgen05-alloc-dealloc-relinquish-alloc-permit"),
                NonEmptyString(
                  "9.7.17.7.1. Tensorcore 5th Generation Instructions: tcgen05.alloc, tcgen05.dealloc, tcgen05.relinquish_alloc_permit"
                ),
                NonEmptyString("tcgen05.alloc, tcgen05.dealloc, tcgen05.relinquish_alloc_permit"),
                NonEmptyString("Tensorcore 5th Generation Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(NonEmptyString(
                  "tcgen05.alloc.cta_group.sync.aligned{.shared::cta}.b32  [dst], nCols;\n\ntcgen05.dealloc.cta_group.sync.aligned.b32              taddr, nCols;\n\ntcgen05.relinquish_alloc_permit.cta_group.sync.aligned;\n\n.cta_group = { .cta_group::1, .cta_group::2 }"
                ))),
                Version(PositiveInt(8), NonNegativeInt(6)),
                Target(100),
                Vec(InstructionForm(
                  Opcode("tcgen05.alloc"),
                  Vec(OperandConstraint.Address, OperandConstraint.Value),
                  Vec(
                    ModifierGroup(ModifierName("sync")),
                    ModifierGroup(ModifierName("aligned")),
                    ModifierGroup(ModifierName("b32"))
                  ),
                  _root_.scala.collection.immutable.Set(ModifierName("shared::cta")),
                  false
                ))
              )
            )))
        )
    case Tcgen05Commit extends Definition(
          Opcode("tcgen05.commit"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 1 || operands.size == 2)
              Right(Instruction.Tcgen05Commit(operands(0), operands.lift(1), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("tcgen05.commit", Vec(1, 2), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("tcgen-async-sync-operations-commit"),
                NonEmptyString("9.7.17.12.1. TensorCore 5th Generation Instructions: tcgen05.commit"),
                NonEmptyString("tcgen05.commit"),
                NonEmptyString("TensorCore 5th Generation Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(NonEmptyString(
                  "tcgen05.commit.cta_group.completion_mechanism{.shared::cluster}{.multicast}.b64\n                                                            [mbar] {, ctaMask};\n\n.completion_mechanism = { .mbarrier::arrive::one }\n.cta_group            = { .cta_group::1, .cta_group::2 }\n.multicast            = { .multicast::cluster }"
                ))),
                Version(PositiveInt(8), NonNegativeInt(6)),
                Target(100),
                Vec(InstructionForm(
                  Opcode("tcgen05.commit"),
                  Vec(OperandConstraint.Address, OperandConstraint.Value),
                  Vec(
                    ModifierGroup(ModifierName("mbarrier::arrive::one")),
                    ModifierGroup(ModifierName("b64"))
                  ),
                  _root_.scala.collection.immutable.Set(
                    ModifierName("multicast::cluster"),
                    ModifierName("shared::cluster")
                  ),
                  false
                ))
              )
            )))
        )
    case Tcgen05Cp extends Definition(
          Opcode("tcgen05.cp"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 2) Right(Instruction.Tcgen05Cp(operands(0), operands(1), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("tcgen05.cp", Vec(2), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("tcgen05-instructions-tcgen05-cp"),
                NonEmptyString("9.7.17.9.2. Tensorcore 5th Generation Instructions: tcgen05.cp"),
                NonEmptyString("tcgen05.cp"),
                NonEmptyString("Tensorcore 5th Generation Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(NonEmptyString(
                  "tcgen05.cp.cta_group.shape{.multicast}{.dst_fmt.src_fmt} [taddr], s-desc;\n\n.cta_group = { .cta_group::1, .cta_group::2 }\n.src_fmt   = { .b6x16_p32 , .b4x16_p64 }\n.dst_fmt   = { .b8x16 }\n.shape     = { .128x256b, .4x256b, .128x128b, .64x128b**, .32x128b*** }\n.multicast = { .warpx2::02_13** , .warpx2::01_23**, .warpx4*** }"
                ))),
                Version(PositiveInt(8), NonNegativeInt(6)),
                Target(100),
                Vec(InstructionForm(
                  Opcode("tcgen05.cp"),
                  Vec(OperandConstraint.Address, OperandConstraint.Value),
                  Vec(),
                  _root_.scala.collection.immutable.Set(
                    ModifierName("b4x16_p64"),
                    ModifierName("b6x16_p32"),
                    ModifierName("b8x16")
                  ),
                  false
                ))
              )
            )))
        )
    case Tcgen05Dealloc extends Definition(
          Opcode("tcgen05.dealloc"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 2) Right(Instruction.Tcgen05Dealloc(operands(0), operands(1), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("tcgen05.dealloc", Vec(2), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("tcgen05-instructions-tcgen05-alloc-dealloc-relinquish-alloc-permit"),
                NonEmptyString(
                  "9.7.17.7.1. Tensorcore 5th Generation Instructions: tcgen05.alloc, tcgen05.dealloc, tcgen05.relinquish_alloc_permit"
                ),
                NonEmptyString("tcgen05.alloc, tcgen05.dealloc, tcgen05.relinquish_alloc_permit"),
                NonEmptyString("Tensorcore 5th Generation Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(NonEmptyString(
                  "tcgen05.alloc.cta_group.sync.aligned{.shared::cta}.b32  [dst], nCols;\n\ntcgen05.dealloc.cta_group.sync.aligned.b32              taddr, nCols;\n\ntcgen05.relinquish_alloc_permit.cta_group.sync.aligned;\n\n.cta_group = { .cta_group::1, .cta_group::2 }"
                ))),
                Version(PositiveInt(8), NonNegativeInt(6)),
                Target(100),
                Vec(InstructionForm(
                  Opcode("tcgen05.dealloc"),
                  Vec(OperandConstraint.Value, OperandConstraint.Value),
                  Vec(
                    ModifierGroup(ModifierName("sync")),
                    ModifierGroup(ModifierName("aligned")),
                    ModifierGroup(ModifierName("b32"))
                  ),
                  _root_.scala.collection.immutable.Set(),
                  false
                ))
              )
            )))
        )
    case Tcgen05FenceAfterThreadSync extends Definition(
          Opcode("tcgen05.fence::after_thread_sync"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 0) Right(Instruction.Tcgen05FenceAfterThreadSync(mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount(
              "tcgen05.fence::after_thread_sync",
              Vec(0),
              operands.size
            ))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("tcgen05-special-sync-operations-fence"),
                NonEmptyString("9.7.17.11.1. TensorCore 5th Generation Instructions: tcgen05.fence"),
                NonEmptyString("tcgen05.fence"),
                NonEmptyString("TensorCore 5th Generation Instructions"),
                NonEmptyVec.unsafeFromVec(
                  Vec(NonEmptyString("tcgen05.fence::before_thread_sync ;\ntcgen05.fence::after_thread_sync  ;"))
                ),
                Version(PositiveInt(8), NonNegativeInt(6)),
                Target(100),
                Vec(InstructionForm(
                  Opcode("tcgen05.fence::after_thread_sync"),
                  Vec(),
                  Vec(),
                  _root_.scala.collection.immutable.Set(),
                  false
                ))
              )
            )))
        )
    case Tcgen05FenceBeforeThreadSync extends Definition(
          Opcode("tcgen05.fence::before_thread_sync"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 0) Right(Instruction.Tcgen05FenceBeforeThreadSync(mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount(
              "tcgen05.fence::before_thread_sync",
              Vec(0),
              operands.size
            ))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("tcgen05-special-sync-operations-fence"),
                NonEmptyString("9.7.17.11.1. TensorCore 5th Generation Instructions: tcgen05.fence"),
                NonEmptyString("tcgen05.fence"),
                NonEmptyString("TensorCore 5th Generation Instructions"),
                NonEmptyVec.unsafeFromVec(
                  Vec(NonEmptyString("tcgen05.fence::before_thread_sync ;\ntcgen05.fence::after_thread_sync  ;"))
                ),
                Version(PositiveInt(8), NonNegativeInt(6)),
                Target(100),
                Vec(InstructionForm(
                  Opcode("tcgen05.fence::before_thread_sync"),
                  Vec(),
                  Vec(),
                  _root_.scala.collection.immutable.Set(),
                  false
                ))
              )
            )))
        )
    case Tcgen05Ld extends Definition(
          Opcode("tcgen05.ld"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 2 || operands.size == 3 || operands.size == 4)
              Right(Instruction.Tcgen05Ld(operands(0), operands(1), operands.lift(2), operands.lift(3), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("tcgen05.ld", Vec(2, 3, 4), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("tcgen05-instructions-tcgen05-ld"),
                NonEmptyString("9.7.17.8.3. Tensorcore 5th Generation Instructions: tcgen05.ld"),
                NonEmptyString("tcgen05.ld"),
                NonEmptyString("Tensorcore 5th Generation Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(NonEmptyString(
                  "// Base load instruction:\n\ntcgen05.ld.sync.aligned.shape1.num{.pack}.b32    r, [taddr];\n\ntcgen05.ld.sync.aligned.shape2.num{.pack}.b32    r, [taddr], immHalfSplitoff;\n\n.shape1 = { .16x64b, .16x128b, .16x256b, .32x32b }\n.shape2 = { .16x32bx2 }\n.num    = { .x1, .x2, .x4, .x8, .x16, .x32, .x64, .x128 }\n.pack   = { .pack::16b }\n\n// Floating point type load along with reduction :\n\ntcgen05.ld.red.sync.aligned.shape3.num.redOp{.abs}{.NaN}.f32 r, redval, [taddr];\n\ntcgen05.ld.red.sync.aligned.shape4.num.redOp{.abs}{.NaN}.f32 r, redval, [taddr], immHalfSplitoff;\n\n// Integer type load along with reduction :\n\ntcgen05.ld.red.sync.aligned.shape3.num.redOp.type r, redval, [taddr];\n\ntcgen05.ld.red.sync.aligned.shape4.num.redOp.type r, redval, [taddr], immHalfSplitoff;\n\n.shape3 = { .32x32b   }\n.shape4 = { .16x32bx2 }\n.redOp  = { .min, .max }\n.type   = { .u32, .s32 }"
                ))),
                Version(PositiveInt(8), NonNegativeInt(6)),
                Target(100),
                Vec(
                  InstructionForm(
                    Opcode("tcgen05.ld"),
                    Vec(OperandConstraint.Register, OperandConstraint.Address),
                    Vec(
                      ModifierGroup(ModifierName("sync")),
                      ModifierGroup(ModifierName("aligned")),
                      ModifierGroup(
                        ModifierName("x1"),
                        ModifierName("x128"),
                        ModifierName("x16"),
                        ModifierName("x2"),
                        ModifierName("x32"),
                        ModifierName("x4"),
                        ModifierName("x64"),
                        ModifierName("x8")
                      ),
                      ModifierGroup(ModifierName("b32"))
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Opcode("tcgen05.ld"),
                    Vec(OperandConstraint.Register, OperandConstraint.Address, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(ModifierName("sync")),
                      ModifierGroup(ModifierName("aligned")),
                      ModifierGroup(
                        ModifierName("x1"),
                        ModifierName("x128"),
                        ModifierName("x16"),
                        ModifierName("x2"),
                        ModifierName("x32"),
                        ModifierName("x4"),
                        ModifierName("x64"),
                        ModifierName("x8")
                      ),
                      ModifierGroup(ModifierName("b32"))
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Opcode("tcgen05.ld"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Address),
                    Vec(
                      ModifierGroup(ModifierName("red")),
                      ModifierGroup(ModifierName("sync")),
                      ModifierGroup(ModifierName("aligned")),
                      ModifierGroup(
                        ModifierName("x1"),
                        ModifierName("x128"),
                        ModifierName("x16"),
                        ModifierName("x2"),
                        ModifierName("x32"),
                        ModifierName("x4"),
                        ModifierName("x64"),
                        ModifierName("x8")
                      ),
                      ModifierGroup(
                        ModifierName("max"),
                        ModifierName("min")
                      ),
                      ModifierGroup(ModifierName("f32"))
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName("NaN"), ModifierName("abs")),
                    false
                  ),
                  InstructionForm(
                    Opcode("tcgen05.ld"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Address,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(ModifierName("red")),
                      ModifierGroup(ModifierName("sync")),
                      ModifierGroup(ModifierName("aligned")),
                      ModifierGroup(
                        ModifierName("x1"),
                        ModifierName("x128"),
                        ModifierName("x16"),
                        ModifierName("x2"),
                        ModifierName("x32"),
                        ModifierName("x4"),
                        ModifierName("x64"),
                        ModifierName("x8")
                      ),
                      ModifierGroup(
                        ModifierName("max"),
                        ModifierName("min")
                      ),
                      ModifierGroup(ModifierName("f32"))
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName("NaN"), ModifierName("abs")),
                    false
                  ),
                  InstructionForm(
                    Opcode("tcgen05.ld"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Address),
                    Vec(
                      ModifierGroup(ModifierName("red")),
                      ModifierGroup(ModifierName("sync")),
                      ModifierGroup(ModifierName("aligned")),
                      ModifierGroup(
                        ModifierName("x1"),
                        ModifierName("x128"),
                        ModifierName("x16"),
                        ModifierName("x2"),
                        ModifierName("x32"),
                        ModifierName("x4"),
                        ModifierName("x64"),
                        ModifierName("x8")
                      ),
                      ModifierGroup(
                        ModifierName("max"),
                        ModifierName("min")
                      ),
                      ModifierGroup(
                        ModifierName("s32"),
                        ModifierName("u32")
                      )
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Opcode("tcgen05.ld"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Address,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(ModifierName("red")),
                      ModifierGroup(ModifierName("sync")),
                      ModifierGroup(ModifierName("aligned")),
                      ModifierGroup(
                        ModifierName("x1"),
                        ModifierName("x128"),
                        ModifierName("x16"),
                        ModifierName("x2"),
                        ModifierName("x32"),
                        ModifierName("x4"),
                        ModifierName("x64"),
                        ModifierName("x8")
                      ),
                      ModifierGroup(
                        ModifierName("max"),
                        ModifierName("min")
                      ),
                      ModifierGroup(
                        ModifierName("s32"),
                        ModifierName("u32")
                      )
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  )
                )
              )
            )))
        )
    case Tcgen05Mma extends Definition(
          Opcode("tcgen05.mma"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 5 || operands.size == 6 || operands.size == 7) Right(if (operands.size == 5)
              Instruction.Tcgen05Mma(
                operands(0),
                operands(1),
                operands(2),
                operands(3),
                None,
                None,
                None,
                operands(4),
                None,
                mods,
                pred
              )
            else if (Operand.isAddress(operands(4))) Instruction.Tcgen05Mma(
              operands(0),
              operands(1),
              operands(2),
              operands(3),
              Some(operands(4)),
              Some(operands(5)),
              None,
              operands(6),
              None,
              mods,
              pred
            )
            else Instruction.Tcgen05Mma(
              operands(0),
              operands(1),
              operands(2),
              operands(3),
              None,
              None,
              Some(operands(4)),
              operands(5),
              operands.lift(6),
              mods,
              pred
            ))
            else Left(Instruction.DecodeError.InvalidOperandCount("tcgen05.mma", Vec(5, 6, 7), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("tcgen05-mma-instructions-mma"),
                NonEmptyString("9.7.17.10.9.1. TensorCore 5th Generation Instructions: tcgen05.mma"),
                NonEmptyString("tcgen05.mma"),
                NonEmptyString("TensorCore 5th Generation Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(NonEmptyString(
                  "// 1. Floating-point type without block scaling:\n\ntcgen05.mma.cta_group.kind   [d-tmem],  a-desc,  b-desc, idesc,\n                             { disable-output-lane }, enable-input-d {, scale-input-d};\n\ntcgen05.mma.cta_group.kind   [d-tmem], [a-tmem], b-desc, idesc,\n                             { disable-output-lane }, enable-input-d {, scale-input-d};\n\n.kind      = { .kind::f16, .kind::tf32, .kind::f8f6f4 }\n.cta_group = { .cta_group::1, .cta_group::2 }\n\n----------------------------------------------------------------------------------\n\n// 2. Floating-point type with block scaling:\n\ntcgen05.mma.cta_group.kind.block_scale{.scale_vectorsize}\n                                        [d-tmem],  a-desc,  b-desc, idesc,\n                                        [scale-A-tmem], [scale-B-tmem], enable-input-d;\n\ntcgen05.mma.cta_group.kind.block_scale{.scale_vectorsize}\n                                        [d-tmem], [a-tmem], b-desc, idesc,\n                                        [scale-A-tmem], [scale-B-tmem], enable-input-d;\n\n.kind = { .kind::mxf8f6f4, .kind::mxf4, .kind::mxf4nvf4 }\n.cta_group      = { .cta_group::1,   .cta_group::2 }\n.scale_vectorsize = { .scale_vec::1X, .scale_vec::2X, .scale_vec::4X, .block16, .block32 }\n\n----------------------------------------------------------------------------------\n\n// 3. Convolution MMA for floating-point type without block scaling:\n\ntcgen05.mma.cta_group.kind.collector_usage [d-tmem],  a-desc,  b-desc, idesc,\n                                           { disable-output-lane }, enable-input-d {, scale-input-d};\n\ntcgen05.mma.cta_group.kind{.ashift}.collector_usage [d-tmem], [a-tmem], b-desc, idesc,\n                                                    { disable-output-lane }, enable-input-d {, scale-input-d};\n\ntcgen05.mma.cta_group.kind.ashift{.collector_usage} [d-tmem], [a-tmem], b-desc, idesc,\n                                                    { disable-output-lane }, enable-input-d {, scale-input-d};\n\n.kind      = { .kind::f16, .kind::tf32, .kind::f8f6f4 }\n.cta_group = { .cta_group::1,   .cta_group::2 }\n.collector_usage = { .collector::buffer::op }\n::buffer         = { ::a }\n::op             = { ::fill, ::use, ::lastuse, ::discard* }\n\n----------------------------------------------------------------------------------\n\n// 4. Activation Stationary MMA for floating-point type with block scaling:\n\ntcgen05.mma.cta_group.kind.block_scale{.scale_vectorsize}.collector_usage\n                                            [d-tmem],  a-desc,  b-desc, idesc,\n                                            [scale-A-tmem], [scale-B-tmem], enable-input-d;\n\ntcgen05.mma.cta_group.kind.block_scale{.scale_vectorsize}.collector_usage\n                                            [d-tmem], [a-tmem], b-desc, idesc,\n                                            [scale-A-tmem], [scale-B-tmem], enable-input-d;\n\n.cta_group       = { .cta_group::1,   .cta_group::2 }\n.scale_vectorsize  = { .scale_vec::1X, .scale_vec::2X, .scale_vec::4X, .block16, .block32 }\n.kind            = { .kind::mxf8f6f4, .kind::mxf4, .kind::mxf4nvf4 }\n.collector_usage = { .collector::buffer::op }\n::buffer         = { ::a }\n::op             = { ::fill, ::use, ::lastuse, ::discard* }\n\n----------------------------------------------------------------------------------\n\n// 5. Integer type:\n\ntcgen05.mma.cta_group.kind::i8  [d-tmem],  a-desc,  b-desc, idesc,\n                                { disable-output-lane }, enable-input-d;\n\ntcgen05.mma.cta_group.kind::i8  [d-tmem], [a-tmem], b-desc, idesc,\n                                { disable-output-lane }, enable-input-d;\n\n.cta_group = { .cta_group::1,   .cta_group::2  }\n\n----------------------------------------------------------------------------------\n\n// 6. Convolution MMA for integer type:\n\ntcgen05.mma.cta_group.kind::i8.collector_usage          [d-tmem],  a-desc,  b-desc, idesc,\n                                                        { disable-output-lane }, enable-input-d;\n\ntcgen05.mma.cta_group.kind::i8.ashift{.collector_usage} [d-tmem], [a-tmem], b-desc, idesc,\n                                                        { disable-output-lane }, enable-input-d;\n\ntcgen05.mma.cta_group.kind::i8{.ashift}.collector_usage [d-tmem], [a-tmem], b-desc, idesc,\n                                                        { disable-output-lane }, enable-input-d;\n\n.cta_group       = { .cta_group::1,   .cta_group::2  }\n.collector_usage = { .collector::buffer::op }\n::buffer         = { ::a }\n::op             = { ::fill, ::use, ::lastuse, ::discard* }"
                ))),
                Version(PositiveInt(8), NonNegativeInt(6)),
                Target(100),
                Vec(
                  InstructionForm(
                    Opcode("tcgen05.mma"),
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.VectorOperand,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(ModifierGroup(
                      ModifierName("kind::mxf4"),
                      ModifierName("kind::mxf4nvf4"),
                      ModifierName("kind::mxf8f6f4")
                    )),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Opcode("tcgen05.mma"),
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.VectorOperand,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(ModifierGroup(
                      ModifierName("kind::mxf4"),
                      ModifierName("kind::mxf4nvf4"),
                      ModifierName("kind::mxf8f6f4")
                    )),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Opcode("tcgen05.mma"),
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Address,
                      OperandConstraint.Address,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(
                        ModifierName("kind::mxf4"),
                        ModifierName("kind::mxf4nvf4"),
                        ModifierName("kind::mxf8f6f4")
                      ),
                      ModifierGroup(ModifierName("block_scale"))
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName("block16"),
                      ModifierName("block32")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode("tcgen05.mma"),
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Address,
                      OperandConstraint.Address,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(
                        ModifierName("kind::mxf4"),
                        ModifierName("kind::mxf4nvf4"),
                        ModifierName("kind::mxf8f6f4")
                      ),
                      ModifierGroup(ModifierName("block_scale"))
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName("block16"),
                      ModifierName("block32")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode("tcgen05.mma"),
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.VectorOperand,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(
                        ModifierName("kind::mxf4"),
                        ModifierName("kind::mxf4nvf4"),
                        ModifierName("kind::mxf8f6f4")
                      ),
                      ModifierGroup(ModifierName("collector::buffer::op"))
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Opcode("tcgen05.mma"),
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.VectorOperand,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(
                        ModifierName("kind::mxf4"),
                        ModifierName("kind::mxf4nvf4"),
                        ModifierName("kind::mxf8f6f4")
                      ),
                      ModifierGroup(ModifierName("collector::buffer::op"))
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName("ashift")),
                    false
                  ),
                  InstructionForm(
                    Opcode("tcgen05.mma"),
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.VectorOperand,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(
                        ModifierName("kind::mxf4"),
                        ModifierName("kind::mxf4nvf4"),
                        ModifierName("kind::mxf8f6f4")
                      ),
                      ModifierGroup(ModifierName("ashift"))
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName("collector::buffer::op")),
                    false
                  ),
                  InstructionForm(
                    Opcode("tcgen05.mma"),
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Address,
                      OperandConstraint.Address,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(
                        ModifierName("kind::mxf4"),
                        ModifierName("kind::mxf4nvf4"),
                        ModifierName("kind::mxf8f6f4")
                      ),
                      ModifierGroup(ModifierName("block_scale")),
                      ModifierGroup(ModifierName("collector::buffer::op"))
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName("block16"),
                      ModifierName("block32")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode("tcgen05.mma"),
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Address,
                      OperandConstraint.Address,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(
                        ModifierName("kind::mxf4"),
                        ModifierName("kind::mxf4nvf4"),
                        ModifierName("kind::mxf8f6f4")
                      ),
                      ModifierGroup(ModifierName("block_scale")),
                      ModifierGroup(ModifierName("collector::buffer::op"))
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName("block16"),
                      ModifierName("block32")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode("tcgen05.mma"),
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.VectorOperand,
                      OperandConstraint.Value
                    ),
                    Vec(ModifierGroup(ModifierName("kind::i8"))),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Opcode("tcgen05.mma"),
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.VectorOperand,
                      OperandConstraint.Value
                    ),
                    Vec(ModifierGroup(ModifierName("kind::i8"))),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Opcode("tcgen05.mma"),
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.VectorOperand,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(ModifierName("kind::i8")),
                      ModifierGroup(ModifierName("collector::buffer::op"))
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Opcode("tcgen05.mma"),
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.VectorOperand,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(ModifierName("kind::i8")),
                      ModifierGroup(ModifierName("ashift"))
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName("collector::buffer::op")),
                    false
                  ),
                  InstructionForm(
                    Opcode("tcgen05.mma"),
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.VectorOperand,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(ModifierName("kind::i8")),
                      ModifierGroup(ModifierName("collector::buffer::op"))
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName("ashift")),
                    false
                  )
                )
              )
            )))
        )
    case Tcgen05MmaSp extends Definition(
          Opcode("tcgen05.mma.sp"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 6 || operands.size == 7 || operands.size == 8) Right(if (operands.size == 6)
              Instruction.Tcgen05MmaSp(
                operands(0),
                operands(1),
                operands(2),
                operands(3),
                operands(4),
                None,
                None,
                None,
                operands(5),
                None,
                mods,
                pred
              )
            else if (Operand.isAddress(operands(5))) Instruction.Tcgen05MmaSp(
              operands(0),
              operands(1),
              operands(2),
              operands(3),
              operands(4),
              Some(operands(5)),
              Some(operands(6)),
              None,
              operands(7),
              None,
              mods,
              pred
            )
            else Instruction.Tcgen05MmaSp(
              operands(0),
              operands(1),
              operands(2),
              operands(3),
              operands(4),
              None,
              None,
              Some(operands(5)),
              operands(6),
              operands.lift(7),
              mods,
              pred
            ))
            else Left(Instruction.DecodeError.InvalidOperandCount("tcgen05.mma.sp", Vec(6, 7, 8), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("tcgen05-mma-instructions-mma-sp"),
                NonEmptyString("9.7.17.10.9.2. TensorCore 5th Generation Instructions: tcgen05.mma.sp"),
                NonEmptyString("tcgen05.mma.sp"),
                NonEmptyString("TensorCore 5th Generation Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(NonEmptyString(
                  "// 1. Floating-point type without block scaling:\n\ntcgen05.mma.sp.cta_group.kind  [d-tmem],  a-desc,  b-desc, [sp-meta-tmem] ,  idesc,\n                               { disable-output-lane }, enable-input-d{, scale-input-d};\n\ntcgen05.mma.sp.cta_group.kind  [d-tmem], [a-tmem], b-desc, [sp-meta-tmem] , idesc,\n                               { disable-output-lane }, enable-input-d{, scale-input-d};\n\n.kind       = { .kind::f16, , .kind::tf32, .kind::f8f6f4 }\n.cta_group  = { .cta_group::1,  .cta_group::2 }\n\n----------------------------------------------------------------------------------\n\n// 2. Floating-point type with block scaling:\n\ntcgen05.mma.sp.cta_group.kind.block_scale{.scale_vectorsize}\n                                         [d-tmem],  a-desc,  b-desc , [sp-meta-tmem] , idesc,\n                                         [scale-A-tmem], [scale-B-tmem], enable-input-d;\n\ntcgen05.mma.sp.cta_group.kind.block_scale{.scale_vectorsize}\n                                         [d-tmem], [a-tmem], b-desc , [sp-meta-tmem] , idesc,\n                                         [scale-A-tmem], [scale-B-tmem], enable-input-d;\n\n.scale_vectorsize = { .scale_vec::1X, .scale_vec::2X, .scale_vec::4X, .block16, .block32 }\n.cta_group      = { .cta_group::1,  .cta_group::2 }\n.kind = { .kind::mxf8f6f4, .kind::mxf4, .kind::mxf4nvf4 }\n\n----------------------------------------------------------------------------------\n\n// 3. Convolution MMA with floating-point type without block scaling:\n\ntcgen05.mma.sp.cta_group.kind.collector_usage           [d-tmem],  a-desc,  b-desc,\n                                                        [sp-meta-tmem] ,  idesc,\n                                                        { disable-output-lane }, enable-input-d\n                                                        {, scale-input-d};\n\ntcgen05.mma.sp.cta_group.kind.ashift{.collector_usage}  [d-tmem], [a-tmem], b-desc,\n                                                        [sp-meta-tmem] , idesc,\n                                                        { disable-output-lane }, enable-input-d\n                                                        {, scale-input-d};\n\ntcgen05.mma.sp.cta_group.kind{.ashift}.collector_usage  [d-tmem], [a-tmem], b-desc,\n                                                        [sp-meta-tmem] , idesc,\n                                                        { disable-output-lane }, enable-input-d\n                                                        {, scale-input-d};\n\n.kind            = { .kind::f16, .kind::tf32, .kind::f8f6f4 }\n.collector_usage = { .collector::buffer::op }\n::buffer         = { ::a }\n::op             = { ::fill, ::use, ::lastuse, ::discard* }\n\n----------------------------------------------------------------------------------\n\n// 4. Activation Stationary MMA with floating-point type with block scaling:\n\ntcgen05.mma.sp.cta_group.kind.block_scale{.scale_vectorsize}.collector_usage\n                                         [d-tmem],  a-desc,  b-desc , [sp-meta-tmem] , idesc,\n                                         [scale-A-tmem], [scale-B-tmem], enable-input-d;\n\ntcgen05.mma.sp.cta_group.kind.block_scale{.scale_vectorsize}.collector_usage\n                                         [d-tmem], [a-tmem], b-desc , [sp-meta-tmem] , idesc,\n                                         [scale-A-tmem], [scale-B-tmem], enable-input-d;\n\n.kind = { .kind::mxf8f6f4, .kind::mxf4, .kind::mxf4nvf4 }\n.scale_vectorsize = { .scale_vec::1X, .scale_vec::2X, .scale_vec::4X, .block16, .block32 }\n.collector_usage = { .collector::buffer::op }\n::buffer         = { ::a }\n::op             = { ::fill, ::use, ::lastuse, ::discard* }\n\n----------------------------------------------------------------------------------\n\n// 5. Integer type:\n\ntcgen05.mma.sp.cta_group.kind::i8 [d-tmem],  a-desc,  b-desc, [sp-meta-tmem] , idesc,\n                                  { disable-output-lane }, enable-input-d;\n\ntcgen05.mma.sp.cta_group.kind::i8 [d-tmem], [a-tmem], b-desc, [sp-meta-tmem] , idesc,\n                                  { disable-output-lane }, enable-input-d;\n\n.cta_group      = { .cta_group::1,  .cta_group::2 }\n\n----------------------------------------------------------------------------------\n\n// 6. Convolution MMA with Integer type:\n\ntcgen05.mma.sp.cta_group.kind::i8.collector_usage          [d-tmem],  a-desc,  b-desc,\n                                                           [sp-meta-tmem] , idesc,\n                                                           { disable-output-lane }, enable-input-d;\n\ntcgen05.mma.sp.cta_group.kind::i8.ashift{.collector_usage} [d-tmem], [a-tmem], b-desc,\n                                                           [sp-meta-tmem], idesc ,\n                                                           { disable-output-lane }, enable-input-d;\n\ntcgen05.mma.sp.cta_group.kind::i8{.ashift}.collector_usage [d-tmem], [a-tmem], b-desc,\n                                                           [sp-meta-tmem], idesc ,\n                                                           { disable-output-lane }, enable-input-d;\n\n.collector_usage = { .collector::buffer::op }\n::buffer         = { ::a }\n::op             = { ::fill, ::use, ::lastuse, ::discard* }"
                ))),
                Version(PositiveInt(8), NonNegativeInt(6)),
                Target(100),
                Vec(
                  InstructionForm(
                    Opcode("tcgen05.mma.sp"),
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.VectorOperand,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(ModifierGroup(
                      ModifierName("kind::mxf4"),
                      ModifierName("kind::mxf4nvf4"),
                      ModifierName("kind::mxf8f6f4")
                    )),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Opcode("tcgen05.mma.sp"),
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.VectorOperand,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(ModifierGroup(
                      ModifierName("kind::mxf4"),
                      ModifierName("kind::mxf4nvf4"),
                      ModifierName("kind::mxf8f6f4")
                    )),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Opcode("tcgen05.mma.sp"),
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Address,
                      OperandConstraint.Address,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(
                        ModifierName("kind::mxf4"),
                        ModifierName("kind::mxf4nvf4"),
                        ModifierName("kind::mxf8f6f4")
                      ),
                      ModifierGroup(ModifierName("block_scale"))
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName("block16"),
                      ModifierName("block32")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode("tcgen05.mma.sp"),
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Address,
                      OperandConstraint.Address,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(
                        ModifierName("kind::mxf4"),
                        ModifierName("kind::mxf4nvf4"),
                        ModifierName("kind::mxf8f6f4")
                      ),
                      ModifierGroup(ModifierName("block_scale"))
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName("block16"),
                      ModifierName("block32")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode("tcgen05.mma.sp"),
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.VectorOperand,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(
                        ModifierName("kind::mxf4"),
                        ModifierName("kind::mxf4nvf4"),
                        ModifierName("kind::mxf8f6f4")
                      ),
                      ModifierGroup(ModifierName("collector::buffer::op"))
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Opcode("tcgen05.mma.sp"),
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.VectorOperand,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(
                        ModifierName("kind::mxf4"),
                        ModifierName("kind::mxf4nvf4"),
                        ModifierName("kind::mxf8f6f4")
                      ),
                      ModifierGroup(ModifierName("ashift"))
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName("collector::buffer::op")),
                    false
                  ),
                  InstructionForm(
                    Opcode("tcgen05.mma.sp"),
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.VectorOperand,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(
                        ModifierName("kind::mxf4"),
                        ModifierName("kind::mxf4nvf4"),
                        ModifierName("kind::mxf8f6f4")
                      ),
                      ModifierGroup(ModifierName("collector::buffer::op"))
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName("ashift")),
                    false
                  ),
                  InstructionForm(
                    Opcode("tcgen05.mma.sp"),
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Address,
                      OperandConstraint.Address,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(
                        ModifierName("kind::mxf4"),
                        ModifierName("kind::mxf4nvf4"),
                        ModifierName("kind::mxf8f6f4")
                      ),
                      ModifierGroup(ModifierName("block_scale")),
                      ModifierGroup(ModifierName("collector::buffer::op"))
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName("block16"),
                      ModifierName("block32")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode("tcgen05.mma.sp"),
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Address,
                      OperandConstraint.Address,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(
                        ModifierName("kind::mxf4"),
                        ModifierName("kind::mxf4nvf4"),
                        ModifierName("kind::mxf8f6f4")
                      ),
                      ModifierGroup(ModifierName("block_scale")),
                      ModifierGroup(ModifierName("collector::buffer::op"))
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName("block16"),
                      ModifierName("block32")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode("tcgen05.mma.sp"),
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.VectorOperand,
                      OperandConstraint.Value
                    ),
                    Vec(ModifierGroup(ModifierName("kind::i8"))),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Opcode("tcgen05.mma.sp"),
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.VectorOperand,
                      OperandConstraint.Value
                    ),
                    Vec(ModifierGroup(ModifierName("kind::i8"))),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Opcode("tcgen05.mma.sp"),
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.VectorOperand,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(ModifierName("kind::i8")),
                      ModifierGroup(ModifierName("collector::buffer::op"))
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Opcode("tcgen05.mma.sp"),
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.VectorOperand,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(ModifierName("kind::i8")),
                      ModifierGroup(ModifierName("ashift"))
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName("collector::buffer::op")),
                    false
                  ),
                  InstructionForm(
                    Opcode("tcgen05.mma.sp"),
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.VectorOperand,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(ModifierName("kind::i8")),
                      ModifierGroup(ModifierName("collector::buffer::op"))
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName("ashift")),
                    false
                  )
                )
              )
            )))
        )
    case Tcgen05MmaWs extends Definition(
          Opcode("tcgen05.mma.ws"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 5 || operands.size == 6) Right(Instruction.Tcgen05MmaWs(
              operands(0),
              operands(1),
              operands(2),
              operands(3),
              operands(4),
              operands.lift(5),
              mods,
              pred
            ))
            else Left(Instruction.DecodeError.InvalidOperandCount("tcgen05.mma.ws", Vec(5, 6), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("tcgen05-mma-instructions-mma-ws"),
                NonEmptyString("9.7.17.10.9.3. TensorCore 5th Generation Instructions: tcgen05.mma.ws"),
                NonEmptyString("tcgen05.mma.ws"),
                NonEmptyString("TensorCore 5th Generation Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(NonEmptyString(
                  "// 1. Floating-point type without block scaling:\n\ntcgen05.mma.ws.cta_group::1.kind{.collector_usage}    [d-tmem],  a-desc,  b-desc,  idesc,\n                                                      enable-input-d {, zero-column-mask-desc };\n\ntcgen05.mma.ws.cta_group::1.kind{.collector_usage}    [d-tmem], [a-tmem], b-desc, idesc,\n                                                      enable-input-d {, zero-column-mask-desc };\n\n.kind = { .kind::f16, .kind::tf32, .kind::f8f6f4 }\n\n----------------------------------------------------------------------------------\n\n// 2. Integer type:\n\ntcgen05.mma.ws.cta_group::1.kind::i8{.collector_usage} [d-tmem],  a-desc,  b-desc, idesc,\n                                                       enable-input-d {, zero-column-mask-desc};\n\ntcgen05.mma.ws.cta_group::1.kind::i8{.collector_usage} [d-tmem], [a-tmem], b-desc, idesc,\n                                                       enable-input-d {, zero-column-mask-desc};\n\n.collector_usage = { .collector::buffer::op }\n::buffer = { ::b0, ::b1, ::b2, ::b3 }\n::op   = { ::fill, ::use, ::lastuse, ::discard}"
                ))),
                Version(PositiveInt(8), NonNegativeInt(6)),
                Target(100),
                Vec(
                  InstructionForm(
                    Opcode("tcgen05.mma.ws"),
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(ModifierName("cta_group")),
                      ModifierGroup(
                        ModifierName("kind::f16"),
                        ModifierName("kind::f8f6f4"),
                        ModifierName("kind::tf32")
                      )
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName("collector::buffer::op")),
                    false
                  ),
                  InstructionForm(
                    Opcode("tcgen05.mma.ws"),
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(ModifierName("cta_group")),
                      ModifierGroup(
                        ModifierName("kind::f16"),
                        ModifierName("kind::f8f6f4"),
                        ModifierName("kind::tf32")
                      )
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName("collector::buffer::op")),
                    false
                  ),
                  InstructionForm(
                    Opcode("tcgen05.mma.ws"),
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(ModifierName("cta_group")),
                      ModifierGroup(ModifierName("kind::i8"))
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName("collector::buffer::op")),
                    false
                  ),
                  InstructionForm(
                    Opcode("tcgen05.mma.ws"),
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(ModifierName("cta_group")),
                      ModifierGroup(ModifierName("kind::i8"))
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName("collector::buffer::op")),
                    false
                  )
                )
              )
            )))
        )
    case Tcgen05MmaWsSp extends Definition(
          Opcode("tcgen05.mma.ws.sp"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 6 || operands.size == 7) Right(Instruction.Tcgen05MmaWsSp(
              operands(0),
              operands(1),
              operands(2),
              operands(3),
              operands(4),
              operands(5),
              operands.lift(6),
              mods,
              pred
            ))
            else Left(Instruction.DecodeError.InvalidOperandCount("tcgen05.mma.ws.sp", Vec(6, 7), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("tcgen05-mma-instructions-mma-ws-sp"),
                NonEmptyString("9.7.17.10.9.4. TensorCore 5th Generation Instructions: tcgen05.mma.ws.sp"),
                NonEmptyString("tcgen05.mma.ws.sp"),
                NonEmptyString("TensorCore 5th Generation Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(NonEmptyString(
                  "// 1. Floating-point type without block scaling:\n\ntcgen05.mma.ws.sp.cta_group::1.kind{.collector_usage} [d-tmem],  a-desc,  b-desc,\n                                                      [sp-meta-tmem] ,  idesc,\n                                                      enable-input-d {, zero-column-mask-desc};\n\ntcgen05.mma.ws.sp.cta_group::1.kind{.collector_usage} [d-tmem], [a-tmem], b-desc,\n                                                      [sp-meta-tmem] , idesc,\n                                                      enable-input-d {, zero-column-mask-desc};\n\n.kind = { .kind::f16, .kind::tf32, .kind::f8f6f4 }\n\n----------------------------------------------------------------------------------\n\n// 2. Integer type:\n\ntcgen05.mma.ws.sp.cta_group::1.kind::i8{.collector_usage} [d-tmem], a-desc, b-desc,\n                                                          [sp-meta-tmem] , idesc,\n                                                          enable-input-d {, zero-column-mask-desc};\n\ntcgen05.mma.ws.sp.cta_group::1.kind::i8{.collector_usage} [d-tmem], [a-tmem], b-desc,\n                                                          [sp-meta-tmem] , idesc,\n                                                          enable-input-d {, zero-column-mask-desc};\n\n.collector_usage = { .collector::buffer::op }\n::buffer = { ::b0, ::b1, ::b2, ::b3 }\n::op   = { ::fill, ::use, ::lastuse, ::discard }"
                ))),
                Version(PositiveInt(8), NonNegativeInt(6)),
                Target(100),
                Vec(
                  InstructionForm(
                    Opcode("tcgen05.mma.ws.sp"),
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(ModifierName("cta_group")),
                      ModifierGroup(
                        ModifierName("kind::f16"),
                        ModifierName("kind::f8f6f4"),
                        ModifierName("kind::tf32")
                      )
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName("collector::buffer::op")),
                    false
                  ),
                  InstructionForm(
                    Opcode("tcgen05.mma.ws.sp"),
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(ModifierName("cta_group")),
                      ModifierGroup(
                        ModifierName("kind::f16"),
                        ModifierName("kind::f8f6f4"),
                        ModifierName("kind::tf32")
                      )
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName("collector::buffer::op")),
                    false
                  ),
                  InstructionForm(
                    Opcode("tcgen05.mma.ws.sp"),
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(ModifierName("cta_group")),
                      ModifierGroup(ModifierName("kind::i8"))
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName("collector::buffer::op")),
                    false
                  ),
                  InstructionForm(
                    Opcode("tcgen05.mma.ws.sp"),
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(ModifierName("cta_group")),
                      ModifierGroup(ModifierName("kind::i8"))
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName("collector::buffer::op")),
                    false
                  )
                )
              )
            )))
        )
    case Tcgen05RelinquishAllocPermit extends Definition(
          Opcode("tcgen05.relinquish_alloc_permit"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 0) Right(Instruction.Tcgen05RelinquishAllocPermit(mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount(
              "tcgen05.relinquish_alloc_permit",
              Vec(0),
              operands.size
            ))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("tcgen05-instructions-tcgen05-alloc-dealloc-relinquish-alloc-permit"),
                NonEmptyString(
                  "9.7.17.7.1. Tensorcore 5th Generation Instructions: tcgen05.alloc, tcgen05.dealloc, tcgen05.relinquish_alloc_permit"
                ),
                NonEmptyString("tcgen05.alloc, tcgen05.dealloc, tcgen05.relinquish_alloc_permit"),
                NonEmptyString("Tensorcore 5th Generation Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(NonEmptyString(
                  "tcgen05.alloc.cta_group.sync.aligned{.shared::cta}.b32  [dst], nCols;\n\ntcgen05.dealloc.cta_group.sync.aligned.b32              taddr, nCols;\n\ntcgen05.relinquish_alloc_permit.cta_group.sync.aligned;\n\n.cta_group = { .cta_group::1, .cta_group::2 }"
                ))),
                Version(PositiveInt(8), NonNegativeInt(6)),
                Target(100),
                Vec(InstructionForm(
                  Opcode("tcgen05.relinquish_alloc_permit"),
                  Vec(),
                  Vec(
                    ModifierGroup(ModifierName("sync")),
                    ModifierGroup(ModifierName("aligned"))
                  ),
                  _root_.scala.collection.immutable.Set(),
                  false
                ))
              )
            )))
        )
    case Tcgen05Shift extends Definition(
          Opcode("tcgen05.shift"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 1) Right(Instruction.Tcgen05Shift(operands(0), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("tcgen05.shift", Vec(1), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("tcgen05-instructions-tcgen05-shift"),
                NonEmptyString("9.7.17.9.3. Tensorcore 5th Generation Instructions: tcgen05.shift"),
                NonEmptyString("tcgen05.shift"),
                NonEmptyString("Tensorcore 5th Generation Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(NonEmptyString(
                  "tcgen05.shift.cta_group.down  [taddr];\n\n.cta_group = { .cta_group::1, .cta_group::2 }"
                ))),
                Version(PositiveInt(8), NonNegativeInt(6)),
                Target(10),
                Vec(InstructionForm(
                  Opcode("tcgen05.shift"),
                  Vec(OperandConstraint.Address),
                  Vec(ModifierGroup(ModifierName("down"))),
                  _root_.scala.collection.immutable.Set(),
                  true
                ))
              )
            )))
        )
    case Tcgen05St extends Definition(
          Opcode("tcgen05.st"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 2 || operands.size == 3)
              Right(Instruction.Tcgen05St(operands(0), operands(1), operands.lift(2), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("tcgen05.st", Vec(2, 3), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("tcgen05-instructions-tcgen05-st"),
                NonEmptyString("9.7.17.8.4. Tensorcore 5th Generation Instructions: tcgen05.st"),
                NonEmptyString("tcgen05.st"),
                NonEmptyString("Tensorcore 5th Generation Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(NonEmptyString(
                  "tcgen05.st.sync.aligned.shape1.num{.unpack}.b32    [taddr], r;\n\ntcgen05.st.sync.aligned.shape2.num{.unpack}.b32    [taddr], immHalfSplitoff, r;\n\n.shape1 = { .16x64b, .16x128b, .16x256b, .32x32b }\n.shape2 = { .16x32bx2 }\n.num    = { .x1, .x2, .x4, .x8, .x16, .x32, .x64, .x128 }\n.unpack = { .unpack::16b }"
                ))),
                Version(PositiveInt(8), NonNegativeInt(6)),
                Target(100),
                Vec(
                  InstructionForm(
                    Opcode("tcgen05.st"),
                    Vec(OperandConstraint.Address, OperandConstraint.Register),
                    Vec(
                      ModifierGroup(ModifierName("sync")),
                      ModifierGroup(ModifierName("aligned")),
                      ModifierGroup(
                        ModifierName("x1"),
                        ModifierName("x128"),
                        ModifierName("x16"),
                        ModifierName("x2"),
                        ModifierName("x32"),
                        ModifierName("x4"),
                        ModifierName("x64"),
                        ModifierName("x8")
                      ),
                      ModifierGroup(ModifierName("b32"))
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Opcode("tcgen05.st"),
                    Vec(OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Register),
                    Vec(
                      ModifierGroup(ModifierName("sync")),
                      ModifierGroup(ModifierName("aligned")),
                      ModifierGroup(
                        ModifierName("x1"),
                        ModifierName("x128"),
                        ModifierName("x16"),
                        ModifierName("x2"),
                        ModifierName("x32"),
                        ModifierName("x4"),
                        ModifierName("x64"),
                        ModifierName("x8")
                      ),
                      ModifierGroup(ModifierName("b32"))
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  )
                )
              )
            )))
        )
    case Tcgen05WaitLd extends Definition(
          Opcode("tcgen05.wait::ld"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 0) Right(Instruction.Tcgen05WaitLd(mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("tcgen05.wait::ld", Vec(0), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("tcgen05-instructions-tcgen05-wait"),
                NonEmptyString("9.7.17.8.5. Tensorcore 5th Generation Instructions: tcgen05.wait"),
                NonEmptyString("tcgen05.wait"),
                NonEmptyString("Tensorcore 5th Generation Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(
                  NonEmptyString("tcgen05.wait_operation.sync.aligned;\n\n.wait_operation = { .wait::ld, .wait::st }")
                )),
                Version(PositiveInt(8), NonNegativeInt(6)),
                Target(100),
                Vec(InstructionForm(
                  Opcode("tcgen05.wait::ld"),
                  Vec(),
                  Vec(
                    ModifierGroup(ModifierName("sync")),
                    ModifierGroup(ModifierName("aligned"))
                  ),
                  _root_.scala.collection.immutable.Set(),
                  false
                ))
              )
            )))
        )
    case Tcgen05WaitSt extends Definition(
          Opcode("tcgen05.wait::st"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 0) Right(Instruction.Tcgen05WaitSt(mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("tcgen05.wait::st", Vec(0), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("tcgen05-instructions-tcgen05-wait"),
                NonEmptyString("9.7.17.8.5. Tensorcore 5th Generation Instructions: tcgen05.wait"),
                NonEmptyString("tcgen05.wait"),
                NonEmptyString("Tensorcore 5th Generation Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(
                  NonEmptyString("tcgen05.wait_operation.sync.aligned;\n\n.wait_operation = { .wait::ld, .wait::st }")
                )),
                Version(PositiveInt(8), NonNegativeInt(6)),
                Target(100),
                Vec(InstructionForm(
                  Opcode("tcgen05.wait::st"),
                  Vec(),
                  Vec(
                    ModifierGroup(ModifierName("sync")),
                    ModifierGroup(ModifierName("aligned"))
                  ),
                  _root_.scala.collection.immutable.Set(),
                  false
                ))
              )
            )))
        )
    case TensorMapCopyFenceProxy extends Definition(
          Opcode("tensormap.cp_fenceproxy"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 3)
              Right(Instruction.TensorMapCopyFenceProxy(operands(0), operands(1), operands(2), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("tensormap.cp_fenceproxy", Vec(3), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("parallel-synchronization-and-communication-instructions-tensormap-cp-fenceproxy"),
                NonEmptyString(
                  "9.7.14.17. Parallel Synchronization and Communication Instructions: tensormap.cp_fenceproxy"
                ),
                NonEmptyString("tensormap.cp_fenceproxy"),
                NonEmptyString("Parallel Synchronization and Communication Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(NonEmptyString(
                  "tensormap.cp_fenceproxy.cp_qualifiers.fence_qualifiers.sync.aligned  [dst], [src], size;\n\n.cp_qualifiers    = { .global.shared::cta }\n.fence_qualifiers = { .to_proxy::from_proxy.release.scope }\n.to_proxy::from_proxy  = { .tensormap::generic }\n.scope            = { .cta, .cluster, .gpu , .sys }"
                ))),
                Version(PositiveInt(8), NonNegativeInt(3)),
                Target(90),
                Vec(InstructionForm(
                  Opcode("tensormap.cp_fenceproxy"),
                  Vec(OperandConstraint.Address, OperandConstraint.Address, OperandConstraint.Value),
                  Vec(
                    ModifierGroup(ModifierName("sync")),
                    ModifierGroup(ModifierName("aligned"))
                  ),
                  _root_.scala.collection.immutable.Set(),
                  true
                ))
              )
            )))
        )
    case TensorMapReplace extends Definition(
          Opcode("tensormap.replace"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 2 || operands.size == 3)
              Right(Instruction.TensorMapReplace(operands(0), operands(1), operands.lift(2), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("tensormap.replace", Vec(2, 3), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("data-movement-and-conversion-instructions-tensormap-replace"),
                NonEmptyString("9.7.9.27. Data Movement and Conversion Instructions: tensormap.replace"),
                NonEmptyString("tensormap.replace"),
                NonEmptyString("Data Movement and Conversion Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(NonEmptyString(
                  "tensormap.replace.mode.field1{.ss}.b1024.type  [addr], new_val;\ntensormap.replace.mode.field2{.ss}.b1024.type  [addr], ord, new_val;\ntensormap.replace.mode.field3{.ss}.b1024.type  [addr], new_val;\n\n.mode    = { .tile }\n.field1  = { .global_address, .rank }\n.field2  = { .box_dim, .global_dim, .global_stride, .element_stride  }\n.field3  = { .elemtype,  .interleave_layout, .swizzle_mode, .swizzle_atomicity, .fill_mode }\n.ss      = { .global, .shared::cta }\n.type    = { .b32, .b64 }"
                ))),
                Version(PositiveInt(8), NonNegativeInt(3)),
                Target(100),
                Vec(
                  InstructionForm(
                    Opcode("tensormap.replace"),
                    Vec(OperandConstraint.Address, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(ModifierName("tile")),
                      ModifierGroup(
                        ModifierName("global_address"),
                        ModifierName("rank")
                      ),
                      ModifierGroup(ModifierName("b1024")),
                      ModifierGroup(
                        ModifierName("b32"),
                        ModifierName("b64")
                      )
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName("global"),
                      ModifierName("shared::cta")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode("tensormap.replace"),
                    Vec(OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(ModifierName("tile")),
                      ModifierGroup(
                        ModifierName("box_dim"),
                        ModifierName("element_stride"),
                        ModifierName("global_dim"),
                        ModifierName("global_stride")
                      ),
                      ModifierGroup(ModifierName("b1024")),
                      ModifierGroup(
                        ModifierName("b32"),
                        ModifierName("b64")
                      )
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName("global"),
                      ModifierName("shared::cta")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode("tensormap.replace"),
                    Vec(OperandConstraint.Address, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(ModifierName("tile")),
                      ModifierGroup(
                        ModifierName("elemtype"),
                        ModifierName("fill_mode"),
                        ModifierName("interleave_layout"),
                        ModifierName("swizzle_atomicity"),
                        ModifierName("swizzle_mode")
                      ),
                      ModifierGroup(ModifierName("b1024")),
                      ModifierGroup(
                        ModifierName("b32"),
                        ModifierName("b64")
                      )
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName("global"),
                      ModifierName("shared::cta")
                    ),
                    false
                  )
                )
              )
            )))
        )
    case TestPredicate extends Definition(
          Opcode("testp"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 2) Right(Instruction.TestPredicate(operands(0), operands(1), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("testp", Vec(2), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("floating-point-instructions-testp"),
                NonEmptyString("9.7.3.1. Floating Point Instructions: testp"),
                NonEmptyString("testp"),
                NonEmptyString("Floating Point Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(NonEmptyString(
                  "testp.op.type  p, a;  // result is .pred\n\n.op   = { .finite, .infinite,\n          .number, .notanumber,\n          .normal, .subnormal };\n.type = { .f32, .f64 };"
                ))),
                Version(PositiveInt(2), NonNegativeInt.Zero),
                Target(20),
                Vec(InstructionForm(
                  Opcode("testp"),
                  Vec(OperandConstraint.Register, OperandConstraint.Value),
                  Vec(
                    ModifierGroup(
                      ModifierName("finite"),
                      ModifierName("infinite"),
                      ModifierName("normal"),
                      ModifierName("notanumber"),
                      ModifierName("number"),
                      ModifierName("subnormal")
                    ),
                    ModifierGroup(
                      ModifierName("f32"),
                      ModifierName("f64")
                    )
                  ),
                  _root_.scala.collection.immutable.Set(),
                  true
                ))
              )
            )))
        )
    case Tex extends Definition(
          Opcode("tex"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (
              operands.size == 2 || operands.size == 3 || operands.size == 4 || operands.size == 5 || operands.size == 6
            ) Right(Instruction.Tex(
              operands(0),
              operands(1),
              operands.lift(2),
              operands.lift(3),
              operands.lift(4),
              operands.lift(5),
              mods,
              pred
            ))
            else Left(Instruction.DecodeError.InvalidOperandCount("tex", Vec(2, 3, 4, 5, 6), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("texture-instructions-tex"),
                NonEmptyString("9.7.11.3. Texture Instructions: tex"),
                NonEmptyString("tex"),
                NonEmptyString("Texture Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(NonEmptyString(
                  "tex.geom.v4.dtype.ctype  d, [a, c] {, e} {, f};\ntex.geom.v4.dtype.ctype  d[|p], [a, b, c] {, e} {, f};  // explicit sampler\n\ntex.geom.v2.f16x2.ctype  d[|p], [a, c] {, e} {, f};\ntex.geom.v2.f16x2.ctype  d[|p], [a, b, c] {, e} {, f};  // explicit sampler\n\n// mipmaps\ntex.base.geom.v4.dtype.ctype   d[|p], [a, {b,} c] {, e} {, f};\ntex.level.geom.v4.dtype.ctype  d[|p], [a, {b,} c], lod {, e} {, f};\ntex.grad.geom.v4.dtype.ctype   d[|p], [a, {b,} c], dPdx, dPdy {, e} {, f};\n\ntex.base.geom.v2.f16x2.ctype   d[|p], [a, {b,} c] {, e} {, f};\ntex.level.geom.v2.f16x2.ctype  d[|p], [a, {b,} c], lod {, e} {, f};\ntex.grad.geom.v2.f16x2.ctype   d[|p], [a, {b,} c], dPdx, dPdy {, e} {, f};\n\n.geom  = { .1d, .2d, .3d, .a1d, .a2d, .cube, .acube, .2dms, .a2dms };\n.dtype = { .u32, .s32, .f16,  .f32 };\n.ctype = {       .s32, .f32 };          // .cube, .acube require .f32\n                                        // .2dms, .a2dms require .s32"
                ))),
                Version(PositiveInt(1), NonNegativeInt.Zero),
                Target(10),
                Vec(
                  InstructionForm(
                    Opcode("tex"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(
                        ModifierName("a1d"),
                        ModifierName("a2d"),
                        ModifierName("a2dms"),
                        ModifierName("acube"),
                        ModifierName("cube")
                      ),
                      ModifierGroup(ModifierName("v4")),
                      ModifierGroup(
                        ModifierName("f16"),
                        ModifierName("f32"),
                        ModifierName("s32"),
                        ModifierName("u32")
                      ),
                      ModifierGroup(
                        ModifierName("f32"),
                        ModifierName("s32")
                      )
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Opcode("tex"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(
                        ModifierName("a1d"),
                        ModifierName("a2d"),
                        ModifierName("a2dms"),
                        ModifierName("acube"),
                        ModifierName("cube")
                      ),
                      ModifierGroup(ModifierName("v4")),
                      ModifierGroup(
                        ModifierName("f16"),
                        ModifierName("f32"),
                        ModifierName("s32"),
                        ModifierName("u32")
                      ),
                      ModifierGroup(
                        ModifierName("f32"),
                        ModifierName("s32")
                      )
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Opcode("tex"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(
                        ModifierName("a1d"),
                        ModifierName("a2d"),
                        ModifierName("a2dms"),
                        ModifierName("acube"),
                        ModifierName("cube")
                      ),
                      ModifierGroup(ModifierName("v2")),
                      ModifierGroup(ModifierName("f16x2")),
                      ModifierGroup(
                        ModifierName("f32"),
                        ModifierName("s32")
                      )
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Opcode("tex"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(
                        ModifierName("a1d"),
                        ModifierName("a2d"),
                        ModifierName("a2dms"),
                        ModifierName("acube"),
                        ModifierName("cube")
                      ),
                      ModifierGroup(ModifierName("v2")),
                      ModifierGroup(ModifierName("f16x2")),
                      ModifierGroup(
                        ModifierName("f32"),
                        ModifierName("s32")
                      )
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Opcode("tex"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(ModifierName("base")),
                      ModifierGroup(
                        ModifierName("a1d"),
                        ModifierName("a2d"),
                        ModifierName("a2dms"),
                        ModifierName("acube"),
                        ModifierName("cube")
                      ),
                      ModifierGroup(ModifierName("v4")),
                      ModifierGroup(
                        ModifierName("f16"),
                        ModifierName("f32"),
                        ModifierName("s32"),
                        ModifierName("u32")
                      ),
                      ModifierGroup(
                        ModifierName("f32"),
                        ModifierName("s32")
                      )
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Opcode("tex"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(ModifierName("level")),
                      ModifierGroup(
                        ModifierName("a1d"),
                        ModifierName("a2d"),
                        ModifierName("a2dms"),
                        ModifierName("acube"),
                        ModifierName("cube")
                      ),
                      ModifierGroup(ModifierName("v4")),
                      ModifierGroup(
                        ModifierName("f16"),
                        ModifierName("f32"),
                        ModifierName("s32"),
                        ModifierName("u32")
                      ),
                      ModifierGroup(
                        ModifierName("f32"),
                        ModifierName("s32")
                      )
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Opcode("tex"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(ModifierName("grad")),
                      ModifierGroup(
                        ModifierName("a1d"),
                        ModifierName("a2d"),
                        ModifierName("a2dms"),
                        ModifierName("acube"),
                        ModifierName("cube")
                      ),
                      ModifierGroup(ModifierName("v4")),
                      ModifierGroup(
                        ModifierName("f16"),
                        ModifierName("f32"),
                        ModifierName("s32"),
                        ModifierName("u32")
                      ),
                      ModifierGroup(
                        ModifierName("f32"),
                        ModifierName("s32")
                      )
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Opcode("tex"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(ModifierName("base")),
                      ModifierGroup(
                        ModifierName("a1d"),
                        ModifierName("a2d"),
                        ModifierName("a2dms"),
                        ModifierName("acube"),
                        ModifierName("cube")
                      ),
                      ModifierGroup(ModifierName("v2")),
                      ModifierGroup(ModifierName("f16x2")),
                      ModifierGroup(
                        ModifierName("f32"),
                        ModifierName("s32")
                      )
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Opcode("tex"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(ModifierName("level")),
                      ModifierGroup(
                        ModifierName("a1d"),
                        ModifierName("a2d"),
                        ModifierName("a2dms"),
                        ModifierName("acube"),
                        ModifierName("cube")
                      ),
                      ModifierGroup(ModifierName("v2")),
                      ModifierGroup(ModifierName("f16x2")),
                      ModifierGroup(
                        ModifierName("f32"),
                        ModifierName("s32")
                      )
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Opcode("tex"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(ModifierName("grad")),
                      ModifierGroup(
                        ModifierName("a1d"),
                        ModifierName("a2d"),
                        ModifierName("a2dms"),
                        ModifierName("acube"),
                        ModifierName("cube")
                      ),
                      ModifierGroup(ModifierName("v2")),
                      ModifierGroup(ModifierName("f16x2")),
                      ModifierGroup(
                        ModifierName("f32"),
                        ModifierName("s32")
                      )
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  )
                )
              )
            )))
        )
    case Tld4 extends Definition(
          Opcode("tld4"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 2 || operands.size == 3 || operands.size == 4)
              Right(Instruction.Tld4(operands(0), operands(1), operands.lift(2), operands.lift(3), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("tld4", Vec(2, 3, 4), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("texture-instructions-tld4"),
                NonEmptyString("9.7.11.4. Texture Instructions: tld4"),
                NonEmptyString("tld4"),
                NonEmptyString("Texture Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(NonEmptyString(
                  "tld4.comp.2d.v4.dtype.f32    d[|p], [a, c] {, e} {, f};\ntld4.comp.geom.v4.dtype.f32  d[|p], [a, b, c] {, e} {, f};  // explicit sampler\n\n.comp  = { .r, .g, .b, .a };\n.geom  = { .2d, .a2d, .cube, .acube };\n.dtype = { .u32, .s32, .f32 };"
                ))),
                Version(PositiveInt(2), NonNegativeInt(2)),
                Target(20),
                Vec(
                  InstructionForm(
                    Opcode("tld4"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(
                        ModifierName("a"),
                        ModifierName("b"),
                        ModifierName("g"),
                        ModifierName("r")
                      ),
                      ModifierGroup(ModifierName("v4")),
                      ModifierGroup(
                        ModifierName("f32"),
                        ModifierName("s32"),
                        ModifierName("u32")
                      ),
                      ModifierGroup(ModifierName("f32"))
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Opcode("tld4"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(
                        ModifierName("a"),
                        ModifierName("b"),
                        ModifierName("g"),
                        ModifierName("r")
                      ),
                      ModifierGroup(
                        ModifierName("a2d"),
                        ModifierName("acube"),
                        ModifierName("cube")
                      ),
                      ModifierGroup(ModifierName("v4")),
                      ModifierGroup(
                        ModifierName("f32"),
                        ModifierName("s32"),
                        ModifierName("u32")
                      ),
                      ModifierGroup(ModifierName("f32"))
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  )
                )
              )
            )))
        )
    case Trap extends Definition(
          Opcode("trap"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 0) Right(Instruction.Trap(mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("trap", Vec(0), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("miscellaneous-instructions-trap"),
                NonEmptyString("9.7.20.4. Miscellaneous Instructions: trap"),
                NonEmptyString("trap"),
                NonEmptyString("Miscellaneous Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(NonEmptyString("trap;"))),
                Version(PositiveInt(1), NonNegativeInt.Zero),
                Target(10),
                Vec(InstructionForm(Opcode("trap"), Vec(), Vec(), _root_.scala.collection.immutable.Set(), true))
              )
            )))
        )
    case Txq extends Definition(
          Opcode("txq"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 2 || operands.size == 3)
              Right(Instruction.Txq(operands(0), operands(1), operands.lift(2), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("txq", Vec(2, 3), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("texture-instructions-txq"),
                NonEmptyString("9.7.11.5. Texture Instructions: txq"),
                NonEmptyString("txq"),
                NonEmptyString("Texture Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(NonEmptyString(
                  "txq.tquery.b32         d, [a];       // texture attributes\ntxq.level.tlquery.b32  d, [a], lod;  // texture attributes\ntxq.squery.b32         d, [a];       // sampler attributes\n\n.tquery  = { .width, .height, .depth,\n             .channel_data_type, .channel_order,\n             .normalized_coords, .array_size,\n             .num_mipmap_levels, .num_samples};\n\n.tlquery = { .width, .height, .depth };\n\n.squery  = { .force_unnormalized_coords, .filter_mode,\n             .addr_mode_0, addr_mode_1, addr_mode_2 };"
                ))),
                Version(PositiveInt(1), NonNegativeInt(5)),
                Target(10),
                Vec(
                  InstructionForm(
                    Opcode("txq"),
                    Vec(OperandConstraint.Register, OperandConstraint.Address),
                    Vec(
                      ModifierGroup(
                        ModifierName("array_size"),
                        ModifierName("channel_data_type"),
                        ModifierName("channel_order"),
                        ModifierName("depth"),
                        ModifierName("height"),
                        ModifierName("normalized_coords"),
                        ModifierName("num_mipmap_levels"),
                        ModifierName("num_samples"),
                        ModifierName("width")
                      ),
                      ModifierGroup(ModifierName("b32"))
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Opcode("txq"),
                    Vec(OperandConstraint.Register, OperandConstraint.Address, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(ModifierName("level")),
                      ModifierGroup(
                        ModifierName("depth"),
                        ModifierName("height"),
                        ModifierName("width")
                      ),
                      ModifierGroup(ModifierName("b32"))
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Opcode("txq"),
                    Vec(OperandConstraint.Register, OperandConstraint.Address),
                    Vec(
                      ModifierGroup(
                        ModifierName("addr_mode_0"),
                        ModifierName("addr_mode_1"),
                        ModifierName("addr_mode_2"),
                        ModifierName("filter_mode"),
                        ModifierName("force_unnormalized_coords")
                      ),
                      ModifierGroup(ModifierName("b32"))
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  )
                )
              )
            )))
        )
    case Vabsdiff extends Definition(
          Opcode("vabsdiff"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 3 || operands.size == 4)
              Right(Instruction.Vabsdiff(operands(0), operands(1), operands(2), operands.lift(3), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("vabsdiff", Vec(3, 4), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("scalar-video-instructions-vadd-vsub-vabsdiff-vmin-vmax"),
                NonEmptyString("9.7.19.1.1. Scalar Video Instructions: vadd, vsub, vabsdiff, vmin, vmax"),
                NonEmptyString("vadd, vsub, vabsdiff, vmin, vmax"),
                NonEmptyString("Scalar Video Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(NonEmptyString(
                  "// 32-bit scalar operation, with optional secondary operation\nvop.dtype.atype.btype{.sat}       d, a{.asel}, b{.bsel};\nvop.dtype.atype.btype{.sat}.op2   d, a{.asel}, b{.bsel}, c;\n\n// 32-bit scalar operation, with optional data merge\nvop.dtype.atype.btype{.sat}  d.dsel, a{.asel}, b{.bsel}, c;\n\n vop   = { vadd, vsub, vabsdiff, vmin, vmax };\n.dtype = .atype = .btype = { .u32, .s32 };\n.dsel  = .asel  = .bsel  = { .b0, .b1, .b2, .b3, .h0, .h1 };\n.op2   = { .add, .min, .max };"
                ))),
                Version(PositiveInt(2), NonNegativeInt.Zero),
                Target(20),
                Vec(
                  InstructionForm(
                    Opcode("vabsdiff"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(ModifierName("dtype")),
                      ModifierGroup(ModifierName("atype")),
                      ModifierGroup(
                        ModifierName("s32"),
                        ModifierName("u32")
                      )
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName("sat")),
                    true
                  ),
                  InstructionForm(
                    Opcode("vabsdiff"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(ModifierName("dtype")),
                      ModifierGroup(ModifierName("atype")),
                      ModifierGroup(
                        ModifierName("s32"),
                        ModifierName("u32")
                      ),
                      ModifierGroup(
                        ModifierName("add"),
                        ModifierName("max"),
                        ModifierName("min")
                      )
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName("sat")),
                    true
                  ),
                  InstructionForm(
                    Opcode("vabsdiff"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(ModifierName("dtype")),
                      ModifierGroup(ModifierName("atype")),
                      ModifierGroup(
                        ModifierName("s32"),
                        ModifierName("u32")
                      )
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName("sat")),
                    true
                  )
                )
              )
            )))
        )
    case Vabsdiff2 extends Definition(
          Opcode("vabsdiff2"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 4)
              Right(Instruction.Vabsdiff2(operands(0), operands(1), operands(2), operands(3), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("vabsdiff2", Vec(4), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("simd-video-instructions-vadd2-vsub2-vavrg2-vabsdiff2-vmin2-vmax2"),
                NonEmptyString("9.7.19.2.1. SIMD Video Instructions: vadd2, vsub2, vavrg2, vabsdiff2, vmin2, vmax2"),
                NonEmptyString("vadd2, vsub2, vavrg2, vabsdiff2, vmin2, vmax2"),
                NonEmptyString("SIMD Video Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(NonEmptyString(
                  "// SIMD instruction with secondary SIMD merge operation\nvop2.dtype.atype.btype{.sat}  d{.mask}, a{.asel}, b{.bsel}, c;\n\n// SIMD instruction with secondary accumulate operation\nvop2.dtype.atype.btype.add  d{.mask}, a{.asel}, b{.bsel}, c;\n\n vop2  = { vadd2, vsub2, vavrg2, vabsdiff2, vmin2, vmax2 };\n.dtype = .atype = .btype = { .u32, .s32 };\n.mask  = { .h0, .h1, .h10 };  // defaults to .h10\n.asel  = .bsel  = { .hxy, where x,y are from { 0, 1, 2, 3 } };\n   .asel defaults to .h10\n   .bsel defaults to .h32"
                ))),
                Version(PositiveInt(3), NonNegativeInt.Zero),
                Target(30),
                Vec(
                  InstructionForm(
                    Opcode("vabsdiff2"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(ModifierName("dtype")),
                      ModifierGroup(ModifierName("atype")),
                      ModifierGroup(
                        ModifierName("s32"),
                        ModifierName("u32")
                      )
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName("sat")),
                    true
                  ),
                  InstructionForm(
                    Opcode("vabsdiff2"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(ModifierName("dtype")),
                      ModifierGroup(ModifierName("atype")),
                      ModifierGroup(
                        ModifierName("s32"),
                        ModifierName("u32")
                      ),
                      ModifierGroup(ModifierName("add"))
                    ),
                    _root_.scala.collection.immutable.Set(),
                    true
                  )
                )
              )
            )))
        )
    case Vabsdiff4 extends Definition(
          Opcode("vabsdiff4"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 4)
              Right(Instruction.Vabsdiff4(operands(0), operands(1), operands(2), operands(3), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("vabsdiff4", Vec(4), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("simd-video-instructions-vadd4-vsub4-vavrg4-vabsdiff4-vmin4-vmax4"),
                NonEmptyString("9.7.19.2.3. SIMD Video Instructions: vadd4, vsub4, vavrg4, vabsdiff4, vmin4, vmax4"),
                NonEmptyString("vadd4, vsub4, vavrg4, vabsdiff4, vmin4, vmax4"),
                NonEmptyString("SIMD Video Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(NonEmptyString(
                  "// SIMD instruction with secondary SIMD merge operation\nvop4.dtype.atype.btype{.sat}  d{.mask}, a{.asel}, b{.bsel}, c;\n\n// SIMD instruction with secondary accumulate operation\nvop4.dtype.atype.btype.add  d{.mask}, a{.asel}, b{.bsel}, c;\nvop4  = { vadd4, vsub4, vavrg4, vabsdiff4, vmin4, vmax4 };\n\n.dtype = .atype = .btype = { .u32, .s32 };\n.mask  = { .b0,\n           .b1, .b10\n           .b2, .b20, .b21, .b210,\n           .b3, .b30, .b31, .b310, .b32, .b320, .b321, .b3210 };\n    defaults to .b3210\n.asel = .bsel = .bxyzw, where x,y,z,w are from { 0, ..., 7 };\n   .asel defaults to .b3210\n   .bsel defaults to .b7654"
                ))),
                Version(PositiveInt(3), NonNegativeInt.Zero),
                Target(30),
                Vec(
                  InstructionForm(
                    Opcode("vabsdiff4"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(ModifierName("dtype")),
                      ModifierGroup(ModifierName("atype")),
                      ModifierGroup(
                        ModifierName("s32"),
                        ModifierName("u32")
                      )
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName("sat")),
                    true
                  ),
                  InstructionForm(
                    Opcode("vabsdiff4"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(ModifierName("dtype")),
                      ModifierGroup(ModifierName("atype")),
                      ModifierGroup(
                        ModifierName("s32"),
                        ModifierName("u32")
                      ),
                      ModifierGroup(ModifierName("add"))
                    ),
                    _root_.scala.collection.immutable.Set(),
                    true
                  )
                )
              )
            )))
        )
    case Vadd extends Definition(
          Opcode("vadd"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 3 || operands.size == 4)
              Right(Instruction.Vadd(operands(0), operands(1), operands(2), operands.lift(3), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("vadd", Vec(3, 4), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("scalar-video-instructions-vadd-vsub-vabsdiff-vmin-vmax"),
                NonEmptyString("9.7.19.1.1. Scalar Video Instructions: vadd, vsub, vabsdiff, vmin, vmax"),
                NonEmptyString("vadd, vsub, vabsdiff, vmin, vmax"),
                NonEmptyString("Scalar Video Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(NonEmptyString(
                  "// 32-bit scalar operation, with optional secondary operation\nvop.dtype.atype.btype{.sat}       d, a{.asel}, b{.bsel};\nvop.dtype.atype.btype{.sat}.op2   d, a{.asel}, b{.bsel}, c;\n\n// 32-bit scalar operation, with optional data merge\nvop.dtype.atype.btype{.sat}  d.dsel, a{.asel}, b{.bsel}, c;\n\n vop   = { vadd, vsub, vabsdiff, vmin, vmax };\n.dtype = .atype = .btype = { .u32, .s32 };\n.dsel  = .asel  = .bsel  = { .b0, .b1, .b2, .b3, .h0, .h1 };\n.op2   = { .add, .min, .max };"
                ))),
                Version(PositiveInt(2), NonNegativeInt.Zero),
                Target(20),
                Vec(
                  InstructionForm(
                    Opcode("vadd"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(ModifierName("dtype")),
                      ModifierGroup(ModifierName("atype")),
                      ModifierGroup(
                        ModifierName("s32"),
                        ModifierName("u32")
                      )
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName("sat")),
                    true
                  ),
                  InstructionForm(
                    Opcode("vadd"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(ModifierName("dtype")),
                      ModifierGroup(ModifierName("atype")),
                      ModifierGroup(
                        ModifierName("s32"),
                        ModifierName("u32")
                      ),
                      ModifierGroup(
                        ModifierName("add"),
                        ModifierName("max"),
                        ModifierName("min")
                      )
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName("sat")),
                    true
                  ),
                  InstructionForm(
                    Opcode("vadd"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(ModifierName("dtype")),
                      ModifierGroup(ModifierName("atype")),
                      ModifierGroup(
                        ModifierName("s32"),
                        ModifierName("u32")
                      )
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName("sat")),
                    true
                  )
                )
              )
            )))
        )
    case Vadd2 extends Definition(
          Opcode("vadd2"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 4)
              Right(Instruction.Vadd2(operands(0), operands(1), operands(2), operands(3), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("vadd2", Vec(4), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("simd-video-instructions-vadd2-vsub2-vavrg2-vabsdiff2-vmin2-vmax2"),
                NonEmptyString("9.7.19.2.1. SIMD Video Instructions: vadd2, vsub2, vavrg2, vabsdiff2, vmin2, vmax2"),
                NonEmptyString("vadd2, vsub2, vavrg2, vabsdiff2, vmin2, vmax2"),
                NonEmptyString("SIMD Video Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(NonEmptyString(
                  "// SIMD instruction with secondary SIMD merge operation\nvop2.dtype.atype.btype{.sat}  d{.mask}, a{.asel}, b{.bsel}, c;\n\n// SIMD instruction with secondary accumulate operation\nvop2.dtype.atype.btype.add  d{.mask}, a{.asel}, b{.bsel}, c;\n\n vop2  = { vadd2, vsub2, vavrg2, vabsdiff2, vmin2, vmax2 };\n.dtype = .atype = .btype = { .u32, .s32 };\n.mask  = { .h0, .h1, .h10 };  // defaults to .h10\n.asel  = .bsel  = { .hxy, where x,y are from { 0, 1, 2, 3 } };\n   .asel defaults to .h10\n   .bsel defaults to .h32"
                ))),
                Version(PositiveInt(3), NonNegativeInt.Zero),
                Target(30),
                Vec(
                  InstructionForm(
                    Opcode("vadd2"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(ModifierName("dtype")),
                      ModifierGroup(ModifierName("atype")),
                      ModifierGroup(
                        ModifierName("s32"),
                        ModifierName("u32")
                      )
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName("sat")),
                    true
                  ),
                  InstructionForm(
                    Opcode("vadd2"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(ModifierName("dtype")),
                      ModifierGroup(ModifierName("atype")),
                      ModifierGroup(
                        ModifierName("s32"),
                        ModifierName("u32")
                      ),
                      ModifierGroup(ModifierName("add"))
                    ),
                    _root_.scala.collection.immutable.Set(),
                    true
                  )
                )
              )
            )))
        )
    case Vadd4 extends Definition(
          Opcode("vadd4"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 4)
              Right(Instruction.Vadd4(operands(0), operands(1), operands(2), operands(3), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("vadd4", Vec(4), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("simd-video-instructions-vadd4-vsub4-vavrg4-vabsdiff4-vmin4-vmax4"),
                NonEmptyString("9.7.19.2.3. SIMD Video Instructions: vadd4, vsub4, vavrg4, vabsdiff4, vmin4, vmax4"),
                NonEmptyString("vadd4, vsub4, vavrg4, vabsdiff4, vmin4, vmax4"),
                NonEmptyString("SIMD Video Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(NonEmptyString(
                  "// SIMD instruction with secondary SIMD merge operation\nvop4.dtype.atype.btype{.sat}  d{.mask}, a{.asel}, b{.bsel}, c;\n\n// SIMD instruction with secondary accumulate operation\nvop4.dtype.atype.btype.add  d{.mask}, a{.asel}, b{.bsel}, c;\nvop4  = { vadd4, vsub4, vavrg4, vabsdiff4, vmin4, vmax4 };\n\n.dtype = .atype = .btype = { .u32, .s32 };\n.mask  = { .b0,\n           .b1, .b10\n           .b2, .b20, .b21, .b210,\n           .b3, .b30, .b31, .b310, .b32, .b320, .b321, .b3210 };\n    defaults to .b3210\n.asel = .bsel = .bxyzw, where x,y,z,w are from { 0, ..., 7 };\n   .asel defaults to .b3210\n   .bsel defaults to .b7654"
                ))),
                Version(PositiveInt(3), NonNegativeInt.Zero),
                Target(30),
                Vec(
                  InstructionForm(
                    Opcode("vadd4"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(ModifierName("dtype")),
                      ModifierGroup(ModifierName("atype")),
                      ModifierGroup(
                        ModifierName("s32"),
                        ModifierName("u32")
                      )
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName("sat")),
                    true
                  ),
                  InstructionForm(
                    Opcode("vadd4"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(ModifierName("dtype")),
                      ModifierGroup(ModifierName("atype")),
                      ModifierGroup(
                        ModifierName("s32"),
                        ModifierName("u32")
                      ),
                      ModifierGroup(ModifierName("add"))
                    ),
                    _root_.scala.collection.immutable.Set(),
                    true
                  )
                )
              )
            )))
        )
    case Vavrg2 extends Definition(
          Opcode("vavrg2"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 4)
              Right(Instruction.Vavrg2(operands(0), operands(1), operands(2), operands(3), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("vavrg2", Vec(4), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("simd-video-instructions-vadd2-vsub2-vavrg2-vabsdiff2-vmin2-vmax2"),
                NonEmptyString("9.7.19.2.1. SIMD Video Instructions: vadd2, vsub2, vavrg2, vabsdiff2, vmin2, vmax2"),
                NonEmptyString("vadd2, vsub2, vavrg2, vabsdiff2, vmin2, vmax2"),
                NonEmptyString("SIMD Video Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(NonEmptyString(
                  "// SIMD instruction with secondary SIMD merge operation\nvop2.dtype.atype.btype{.sat}  d{.mask}, a{.asel}, b{.bsel}, c;\n\n// SIMD instruction with secondary accumulate operation\nvop2.dtype.atype.btype.add  d{.mask}, a{.asel}, b{.bsel}, c;\n\n vop2  = { vadd2, vsub2, vavrg2, vabsdiff2, vmin2, vmax2 };\n.dtype = .atype = .btype = { .u32, .s32 };\n.mask  = { .h0, .h1, .h10 };  // defaults to .h10\n.asel  = .bsel  = { .hxy, where x,y are from { 0, 1, 2, 3 } };\n   .asel defaults to .h10\n   .bsel defaults to .h32"
                ))),
                Version(PositiveInt(3), NonNegativeInt.Zero),
                Target(30),
                Vec(
                  InstructionForm(
                    Opcode("vavrg2"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(ModifierName("dtype")),
                      ModifierGroup(ModifierName("atype")),
                      ModifierGroup(
                        ModifierName("s32"),
                        ModifierName("u32")
                      )
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName("sat")),
                    true
                  ),
                  InstructionForm(
                    Opcode("vavrg2"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(ModifierName("dtype")),
                      ModifierGroup(ModifierName("atype")),
                      ModifierGroup(
                        ModifierName("s32"),
                        ModifierName("u32")
                      ),
                      ModifierGroup(ModifierName("add"))
                    ),
                    _root_.scala.collection.immutable.Set(),
                    true
                  )
                )
              )
            )))
        )
    case Vavrg4 extends Definition(
          Opcode("vavrg4"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 4)
              Right(Instruction.Vavrg4(operands(0), operands(1), operands(2), operands(3), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("vavrg4", Vec(4), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("simd-video-instructions-vadd4-vsub4-vavrg4-vabsdiff4-vmin4-vmax4"),
                NonEmptyString("9.7.19.2.3. SIMD Video Instructions: vadd4, vsub4, vavrg4, vabsdiff4, vmin4, vmax4"),
                NonEmptyString("vadd4, vsub4, vavrg4, vabsdiff4, vmin4, vmax4"),
                NonEmptyString("SIMD Video Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(NonEmptyString(
                  "// SIMD instruction with secondary SIMD merge operation\nvop4.dtype.atype.btype{.sat}  d{.mask}, a{.asel}, b{.bsel}, c;\n\n// SIMD instruction with secondary accumulate operation\nvop4.dtype.atype.btype.add  d{.mask}, a{.asel}, b{.bsel}, c;\nvop4  = { vadd4, vsub4, vavrg4, vabsdiff4, vmin4, vmax4 };\n\n.dtype = .atype = .btype = { .u32, .s32 };\n.mask  = { .b0,\n           .b1, .b10\n           .b2, .b20, .b21, .b210,\n           .b3, .b30, .b31, .b310, .b32, .b320, .b321, .b3210 };\n    defaults to .b3210\n.asel = .bsel = .bxyzw, where x,y,z,w are from { 0, ..., 7 };\n   .asel defaults to .b3210\n   .bsel defaults to .b7654"
                ))),
                Version(PositiveInt(3), NonNegativeInt.Zero),
                Target(30),
                Vec(
                  InstructionForm(
                    Opcode("vavrg4"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(ModifierName("dtype")),
                      ModifierGroup(ModifierName("atype")),
                      ModifierGroup(
                        ModifierName("s32"),
                        ModifierName("u32")
                      )
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName("sat")),
                    true
                  ),
                  InstructionForm(
                    Opcode("vavrg4"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(ModifierName("dtype")),
                      ModifierGroup(ModifierName("atype")),
                      ModifierGroup(
                        ModifierName("s32"),
                        ModifierName("u32")
                      ),
                      ModifierGroup(ModifierName("add"))
                    ),
                    _root_.scala.collection.immutable.Set(),
                    true
                  )
                )
              )
            )))
        )
    case Vmad extends Definition(
          Opcode("vmad"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 4)
              Right(Instruction.Vmad(operands(0), operands(1), operands(2), operands(3), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("vmad", Vec(4), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("scalar-video-instructions-vmad"),
                NonEmptyString("9.7.19.1.3. Scalar Video Instructions: vmad"),
                NonEmptyString("vmad"),
                NonEmptyString("Scalar Video Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(NonEmptyString(
                  "// 32-bit scalar operation\nvmad.dtype.atype.btype{.sat}{.scale}     d, {-}a{.asel}, {-}b{.bsel},\n                                         {-}c;\nvmad.dtype.atype.btype.po{.sat}{.scale}  d, a{.asel}, b{.bsel}, c;\n\n.dtype = .atype = .btype = { .u32, .s32 };\n.asel  = .bsel  = { .b0, .b1, .b2, .b3, .h0, .h1 };\n.scale = { .shr7, .shr15 };"
                ))),
                Version(PositiveInt(2), NonNegativeInt.Zero),
                Target(20),
                Vec(
                  InstructionForm(
                    Opcode("vmad"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(ModifierName("dtype")),
                      ModifierGroup(ModifierName("atype")),
                      ModifierGroup(
                        ModifierName("s32"),
                        ModifierName("u32")
                      )
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName("sat"),
                      ModifierName("shr15"),
                      ModifierName("shr7")
                    ),
                    true
                  ),
                  InstructionForm(
                    Opcode("vmad"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(ModifierName("dtype")),
                      ModifierGroup(ModifierName("atype")),
                      ModifierGroup(
                        ModifierName("s32"),
                        ModifierName("u32")
                      ),
                      ModifierGroup(ModifierName("po"))
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName("sat"),
                      ModifierName("shr15"),
                      ModifierName("shr7")
                    ),
                    true
                  )
                )
              )
            )))
        )
    case Vmax extends Definition(
          Opcode("vmax"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 3 || operands.size == 4)
              Right(Instruction.Vmax(operands(0), operands(1), operands(2), operands.lift(3), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("vmax", Vec(3, 4), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("scalar-video-instructions-vadd-vsub-vabsdiff-vmin-vmax"),
                NonEmptyString("9.7.19.1.1. Scalar Video Instructions: vadd, vsub, vabsdiff, vmin, vmax"),
                NonEmptyString("vadd, vsub, vabsdiff, vmin, vmax"),
                NonEmptyString("Scalar Video Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(NonEmptyString(
                  "// 32-bit scalar operation, with optional secondary operation\nvop.dtype.atype.btype{.sat}       d, a{.asel}, b{.bsel};\nvop.dtype.atype.btype{.sat}.op2   d, a{.asel}, b{.bsel}, c;\n\n// 32-bit scalar operation, with optional data merge\nvop.dtype.atype.btype{.sat}  d.dsel, a{.asel}, b{.bsel}, c;\n\n vop   = { vadd, vsub, vabsdiff, vmin, vmax };\n.dtype = .atype = .btype = { .u32, .s32 };\n.dsel  = .asel  = .bsel  = { .b0, .b1, .b2, .b3, .h0, .h1 };\n.op2   = { .add, .min, .max };"
                ))),
                Version(PositiveInt(2), NonNegativeInt.Zero),
                Target(20),
                Vec(
                  InstructionForm(
                    Opcode("vmax"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(ModifierName("dtype")),
                      ModifierGroup(ModifierName("atype")),
                      ModifierGroup(
                        ModifierName("s32"),
                        ModifierName("u32")
                      )
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName("sat")),
                    true
                  ),
                  InstructionForm(
                    Opcode("vmax"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(ModifierName("dtype")),
                      ModifierGroup(ModifierName("atype")),
                      ModifierGroup(
                        ModifierName("s32"),
                        ModifierName("u32")
                      ),
                      ModifierGroup(
                        ModifierName("add"),
                        ModifierName("max"),
                        ModifierName("min")
                      )
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName("sat")),
                    true
                  ),
                  InstructionForm(
                    Opcode("vmax"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(ModifierName("dtype")),
                      ModifierGroup(ModifierName("atype")),
                      ModifierGroup(
                        ModifierName("s32"),
                        ModifierName("u32")
                      )
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName("sat")),
                    true
                  )
                )
              )
            )))
        )
    case Vmax2 extends Definition(
          Opcode("vmax2"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 4)
              Right(Instruction.Vmax2(operands(0), operands(1), operands(2), operands(3), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("vmax2", Vec(4), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("simd-video-instructions-vadd2-vsub2-vavrg2-vabsdiff2-vmin2-vmax2"),
                NonEmptyString("9.7.19.2.1. SIMD Video Instructions: vadd2, vsub2, vavrg2, vabsdiff2, vmin2, vmax2"),
                NonEmptyString("vadd2, vsub2, vavrg2, vabsdiff2, vmin2, vmax2"),
                NonEmptyString("SIMD Video Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(NonEmptyString(
                  "// SIMD instruction with secondary SIMD merge operation\nvop2.dtype.atype.btype{.sat}  d{.mask}, a{.asel}, b{.bsel}, c;\n\n// SIMD instruction with secondary accumulate operation\nvop2.dtype.atype.btype.add  d{.mask}, a{.asel}, b{.bsel}, c;\n\n vop2  = { vadd2, vsub2, vavrg2, vabsdiff2, vmin2, vmax2 };\n.dtype = .atype = .btype = { .u32, .s32 };\n.mask  = { .h0, .h1, .h10 };  // defaults to .h10\n.asel  = .bsel  = { .hxy, where x,y are from { 0, 1, 2, 3 } };\n   .asel defaults to .h10\n   .bsel defaults to .h32"
                ))),
                Version(PositiveInt(3), NonNegativeInt.Zero),
                Target(30),
                Vec(
                  InstructionForm(
                    Opcode("vmax2"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(ModifierName("dtype")),
                      ModifierGroup(ModifierName("atype")),
                      ModifierGroup(
                        ModifierName("s32"),
                        ModifierName("u32")
                      )
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName("sat")),
                    true
                  ),
                  InstructionForm(
                    Opcode("vmax2"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(ModifierName("dtype")),
                      ModifierGroup(ModifierName("atype")),
                      ModifierGroup(
                        ModifierName("s32"),
                        ModifierName("u32")
                      ),
                      ModifierGroup(ModifierName("add"))
                    ),
                    _root_.scala.collection.immutable.Set(),
                    true
                  )
                )
              )
            )))
        )
    case Vmax4 extends Definition(
          Opcode("vmax4"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 4)
              Right(Instruction.Vmax4(operands(0), operands(1), operands(2), operands(3), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("vmax4", Vec(4), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("simd-video-instructions-vadd4-vsub4-vavrg4-vabsdiff4-vmin4-vmax4"),
                NonEmptyString("9.7.19.2.3. SIMD Video Instructions: vadd4, vsub4, vavrg4, vabsdiff4, vmin4, vmax4"),
                NonEmptyString("vadd4, vsub4, vavrg4, vabsdiff4, vmin4, vmax4"),
                NonEmptyString("SIMD Video Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(NonEmptyString(
                  "// SIMD instruction with secondary SIMD merge operation\nvop4.dtype.atype.btype{.sat}  d{.mask}, a{.asel}, b{.bsel}, c;\n\n// SIMD instruction with secondary accumulate operation\nvop4.dtype.atype.btype.add  d{.mask}, a{.asel}, b{.bsel}, c;\nvop4  = { vadd4, vsub4, vavrg4, vabsdiff4, vmin4, vmax4 };\n\n.dtype = .atype = .btype = { .u32, .s32 };\n.mask  = { .b0,\n           .b1, .b10\n           .b2, .b20, .b21, .b210,\n           .b3, .b30, .b31, .b310, .b32, .b320, .b321, .b3210 };\n    defaults to .b3210\n.asel = .bsel = .bxyzw, where x,y,z,w are from { 0, ..., 7 };\n   .asel defaults to .b3210\n   .bsel defaults to .b7654"
                ))),
                Version(PositiveInt(3), NonNegativeInt.Zero),
                Target(30),
                Vec(
                  InstructionForm(
                    Opcode("vmax4"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(ModifierName("dtype")),
                      ModifierGroup(ModifierName("atype")),
                      ModifierGroup(
                        ModifierName("s32"),
                        ModifierName("u32")
                      )
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName("sat")),
                    true
                  ),
                  InstructionForm(
                    Opcode("vmax4"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(ModifierName("dtype")),
                      ModifierGroup(ModifierName("atype")),
                      ModifierGroup(
                        ModifierName("s32"),
                        ModifierName("u32")
                      ),
                      ModifierGroup(ModifierName("add"))
                    ),
                    _root_.scala.collection.immutable.Set(),
                    true
                  )
                )
              )
            )))
        )
    case Vmin extends Definition(
          Opcode("vmin"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 3 || operands.size == 4)
              Right(Instruction.Vmin(operands(0), operands(1), operands(2), operands.lift(3), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("vmin", Vec(3, 4), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("scalar-video-instructions-vadd-vsub-vabsdiff-vmin-vmax"),
                NonEmptyString("9.7.19.1.1. Scalar Video Instructions: vadd, vsub, vabsdiff, vmin, vmax"),
                NonEmptyString("vadd, vsub, vabsdiff, vmin, vmax"),
                NonEmptyString("Scalar Video Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(NonEmptyString(
                  "// 32-bit scalar operation, with optional secondary operation\nvop.dtype.atype.btype{.sat}       d, a{.asel}, b{.bsel};\nvop.dtype.atype.btype{.sat}.op2   d, a{.asel}, b{.bsel}, c;\n\n// 32-bit scalar operation, with optional data merge\nvop.dtype.atype.btype{.sat}  d.dsel, a{.asel}, b{.bsel}, c;\n\n vop   = { vadd, vsub, vabsdiff, vmin, vmax };\n.dtype = .atype = .btype = { .u32, .s32 };\n.dsel  = .asel  = .bsel  = { .b0, .b1, .b2, .b3, .h0, .h1 };\n.op2   = { .add, .min, .max };"
                ))),
                Version(PositiveInt(2), NonNegativeInt.Zero),
                Target(20),
                Vec(
                  InstructionForm(
                    Opcode("vmin"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(ModifierName("dtype")),
                      ModifierGroup(ModifierName("atype")),
                      ModifierGroup(
                        ModifierName("s32"),
                        ModifierName("u32")
                      )
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName("sat")),
                    true
                  ),
                  InstructionForm(
                    Opcode("vmin"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(ModifierName("dtype")),
                      ModifierGroup(ModifierName("atype")),
                      ModifierGroup(
                        ModifierName("s32"),
                        ModifierName("u32")
                      ),
                      ModifierGroup(
                        ModifierName("add"),
                        ModifierName("max"),
                        ModifierName("min")
                      )
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName("sat")),
                    true
                  ),
                  InstructionForm(
                    Opcode("vmin"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(ModifierName("dtype")),
                      ModifierGroup(ModifierName("atype")),
                      ModifierGroup(
                        ModifierName("s32"),
                        ModifierName("u32")
                      )
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName("sat")),
                    true
                  )
                )
              )
            )))
        )
    case Vmin2 extends Definition(
          Opcode("vmin2"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 4)
              Right(Instruction.Vmin2(operands(0), operands(1), operands(2), operands(3), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("vmin2", Vec(4), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("simd-video-instructions-vadd2-vsub2-vavrg2-vabsdiff2-vmin2-vmax2"),
                NonEmptyString("9.7.19.2.1. SIMD Video Instructions: vadd2, vsub2, vavrg2, vabsdiff2, vmin2, vmax2"),
                NonEmptyString("vadd2, vsub2, vavrg2, vabsdiff2, vmin2, vmax2"),
                NonEmptyString("SIMD Video Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(NonEmptyString(
                  "// SIMD instruction with secondary SIMD merge operation\nvop2.dtype.atype.btype{.sat}  d{.mask}, a{.asel}, b{.bsel}, c;\n\n// SIMD instruction with secondary accumulate operation\nvop2.dtype.atype.btype.add  d{.mask}, a{.asel}, b{.bsel}, c;\n\n vop2  = { vadd2, vsub2, vavrg2, vabsdiff2, vmin2, vmax2 };\n.dtype = .atype = .btype = { .u32, .s32 };\n.mask  = { .h0, .h1, .h10 };  // defaults to .h10\n.asel  = .bsel  = { .hxy, where x,y are from { 0, 1, 2, 3 } };\n   .asel defaults to .h10\n   .bsel defaults to .h32"
                ))),
                Version(PositiveInt(3), NonNegativeInt.Zero),
                Target(30),
                Vec(
                  InstructionForm(
                    Opcode("vmin2"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(ModifierName("dtype")),
                      ModifierGroup(ModifierName("atype")),
                      ModifierGroup(
                        ModifierName("s32"),
                        ModifierName("u32")
                      )
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName("sat")),
                    true
                  ),
                  InstructionForm(
                    Opcode("vmin2"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(ModifierName("dtype")),
                      ModifierGroup(ModifierName("atype")),
                      ModifierGroup(
                        ModifierName("s32"),
                        ModifierName("u32")
                      ),
                      ModifierGroup(ModifierName("add"))
                    ),
                    _root_.scala.collection.immutable.Set(),
                    true
                  )
                )
              )
            )))
        )
    case Vmin4 extends Definition(
          Opcode("vmin4"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 4)
              Right(Instruction.Vmin4(operands(0), operands(1), operands(2), operands(3), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("vmin4", Vec(4), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("simd-video-instructions-vadd4-vsub4-vavrg4-vabsdiff4-vmin4-vmax4"),
                NonEmptyString("9.7.19.2.3. SIMD Video Instructions: vadd4, vsub4, vavrg4, vabsdiff4, vmin4, vmax4"),
                NonEmptyString("vadd4, vsub4, vavrg4, vabsdiff4, vmin4, vmax4"),
                NonEmptyString("SIMD Video Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(NonEmptyString(
                  "// SIMD instruction with secondary SIMD merge operation\nvop4.dtype.atype.btype{.sat}  d{.mask}, a{.asel}, b{.bsel}, c;\n\n// SIMD instruction with secondary accumulate operation\nvop4.dtype.atype.btype.add  d{.mask}, a{.asel}, b{.bsel}, c;\nvop4  = { vadd4, vsub4, vavrg4, vabsdiff4, vmin4, vmax4 };\n\n.dtype = .atype = .btype = { .u32, .s32 };\n.mask  = { .b0,\n           .b1, .b10\n           .b2, .b20, .b21, .b210,\n           .b3, .b30, .b31, .b310, .b32, .b320, .b321, .b3210 };\n    defaults to .b3210\n.asel = .bsel = .bxyzw, where x,y,z,w are from { 0, ..., 7 };\n   .asel defaults to .b3210\n   .bsel defaults to .b7654"
                ))),
                Version(PositiveInt(3), NonNegativeInt.Zero),
                Target(30),
                Vec(
                  InstructionForm(
                    Opcode("vmin4"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(ModifierName("dtype")),
                      ModifierGroup(ModifierName("atype")),
                      ModifierGroup(
                        ModifierName("s32"),
                        ModifierName("u32")
                      )
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName("sat")),
                    true
                  ),
                  InstructionForm(
                    Opcode("vmin4"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(ModifierName("dtype")),
                      ModifierGroup(ModifierName("atype")),
                      ModifierGroup(
                        ModifierName("s32"),
                        ModifierName("u32")
                      ),
                      ModifierGroup(ModifierName("add"))
                    ),
                    _root_.scala.collection.immutable.Set(),
                    true
                  )
                )
              )
            )))
        )
    case Vote extends Definition(
          Opcode("vote"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 2) Right(Instruction.Vote(operands(0), operands(1), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("vote", Vec(2), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("parallel-synchronization-and-communication-instructions-vote"),
                NonEmptyString("9.7.14.9. Parallel Synchronization and Communication Instructions: vote (deprecated)"),
                NonEmptyString("vote (deprecated)"),
                NonEmptyString("Parallel Synchronization and Communication Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(NonEmptyString(
                  "vote.mode.pred  d, {!}a;\nvote.ballot.b32 d, {!}a;  // 'ballot' form, returns bitmask\n\n.mode = { .all, .any, .uni };"
                ))),
                Version(PositiveInt(1), NonNegativeInt(2)),
                Target(12),
                Vec(
                  InstructionForm(
                    Opcode("vote"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(
                        ModifierName("all"),
                        ModifierName("any"),
                        ModifierName("uni")
                      ),
                      ModifierGroup(ModifierName("pred"))
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Opcode("vote"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(ModifierName("ballot")),
                      ModifierGroup(ModifierName("b32"))
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  )
                )
              )
            )))
        )
    case VoteSync extends Definition(
          Opcode("vote.sync"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 3) Right(Instruction.VoteSync(operands(0), operands(1), operands(2), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("vote.sync", Vec(3), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("parallel-synchronization-and-communication-instructions-vote-sync"),
                NonEmptyString("9.7.14.10. Parallel Synchronization and Communication Instructions: vote.sync"),
                NonEmptyString("vote.sync"),
                NonEmptyString("Parallel Synchronization and Communication Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(NonEmptyString(
                  "vote.sync.mode.pred  d, {!}a, membermask;\nvote.sync.ballot.b32 d, {!}a, membermask;  // 'ballot' form, returns bitmask\n\n.mode = { .all, .any, .uni };"
                ))),
                Version(PositiveInt(6), NonNegativeInt.Zero),
                Target(30),
                Vec(
                  InstructionForm(
                    Opcode("vote.sync"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(
                        ModifierName("all"),
                        ModifierName("any"),
                        ModifierName("uni")
                      ),
                      ModifierGroup(ModifierName("pred"))
                    ),
                    _root_.scala.collection.immutable.Set(),
                    true
                  ),
                  InstructionForm(
                    Opcode("vote.sync"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(ModifierName("ballot")),
                      ModifierGroup(ModifierName("b32"))
                    ),
                    _root_.scala.collection.immutable.Set(),
                    true
                  )
                )
              )
            )))
        )
    case Vset extends Definition(
          Opcode("vset"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 3 || operands.size == 4)
              Right(Instruction.Vset(operands(0), operands(1), operands(2), operands.lift(3), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("vset", Vec(3, 4), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("scalar-video-instructions-vset"),
                NonEmptyString("9.7.19.1.4. Scalar Video Instructions: vset"),
                NonEmptyString("vset"),
                NonEmptyString("Scalar Video Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(NonEmptyString(
                  "// 32-bit scalar operation, with optional secondary operation\nvset.atype.btype.cmp       d, a{.asel}, b{.bsel};\nvset.atype.btype.cmp.op2   d, a{.asel}, b{.bsel}, c;\n\n// 32-bit scalar operation, with optional data merge\nvset.atype.btype.cmp  d.dsel, a{.asel}, b{.bsel}, c;\n\n.atype = .btype = { .u32, .s32 };\n.cmp   = { .eq, .ne, .lt, .le, .gt, .ge };\n.dsel  = .asel  = .bsel  = { .b0, .b1, .b2, .b3, .h0, .h1 };\n.op2   = { .add, .min, .max };"
                ))),
                Version(PositiveInt(2), NonNegativeInt.Zero),
                Target(20),
                Vec(
                  InstructionForm(
                    Opcode("vset"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(ModifierName("atype")),
                      ModifierGroup(
                        ModifierName("s32"),
                        ModifierName("u32")
                      ),
                      ModifierGroup(
                        ModifierName("eq"),
                        ModifierName("ge"),
                        ModifierName("gt"),
                        ModifierName("le"),
                        ModifierName("lt"),
                        ModifierName("ne")
                      )
                    ),
                    _root_.scala.collection.immutable.Set(),
                    true
                  ),
                  InstructionForm(
                    Opcode("vset"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(ModifierName("atype")),
                      ModifierGroup(
                        ModifierName("s32"),
                        ModifierName("u32")
                      ),
                      ModifierGroup(
                        ModifierName("eq"),
                        ModifierName("ge"),
                        ModifierName("gt"),
                        ModifierName("le"),
                        ModifierName("lt"),
                        ModifierName("ne")
                      ),
                      ModifierGroup(
                        ModifierName("add"),
                        ModifierName("max"),
                        ModifierName("min")
                      )
                    ),
                    _root_.scala.collection.immutable.Set(),
                    true
                  ),
                  InstructionForm(
                    Opcode("vset"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(ModifierName("atype")),
                      ModifierGroup(
                        ModifierName("s32"),
                        ModifierName("u32")
                      ),
                      ModifierGroup(
                        ModifierName("eq"),
                        ModifierName("ge"),
                        ModifierName("gt"),
                        ModifierName("le"),
                        ModifierName("lt"),
                        ModifierName("ne")
                      )
                    ),
                    _root_.scala.collection.immutable.Set(),
                    true
                  )
                )
              )
            )))
        )
    case Vset2 extends Definition(
          Opcode("vset2"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 4)
              Right(Instruction.Vset2(operands(0), operands(1), operands(2), operands(3), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("vset2", Vec(4), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("simd-video-instructions-vset2"),
                NonEmptyString("9.7.19.2.2. SIMD Video Instructions: vset2"),
                NonEmptyString("vset2"),
                NonEmptyString("SIMD Video Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(NonEmptyString(
                  "// SIMD instruction with secondary SIMD merge operation\nvset2.atype.btype.cmp  d{.mask}, a{.asel}, b{.bsel}, c;\n\n// SIMD instruction with secondary accumulate operation\nvset2.atype.btype.cmp.add  d{.mask}, a{.asel}, b{.bsel}, c;\n\n.atype = .btype = { .u32, .s32 };\n.cmp   = { .eq, .ne, .lt, .le, .gt, .ge };\n.mask  = { .h0, .h1, .h10 };  // defaults to .h10\n.asel  = .bsel  = { .hxy, where x,y are from { 0, 1, 2, 3 } };\n   .asel defaults to .h10\n   .bsel defaults to .h32"
                ))),
                Version(PositiveInt(3), NonNegativeInt.Zero),
                Target(30),
                Vec(
                  InstructionForm(
                    Opcode("vset2"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(ModifierName("atype")),
                      ModifierGroup(
                        ModifierName("s32"),
                        ModifierName("u32")
                      ),
                      ModifierGroup(
                        ModifierName("eq"),
                        ModifierName("ge"),
                        ModifierName("gt"),
                        ModifierName("le"),
                        ModifierName("lt"),
                        ModifierName("ne")
                      )
                    ),
                    _root_.scala.collection.immutable.Set(),
                    true
                  ),
                  InstructionForm(
                    Opcode("vset2"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(ModifierName("atype")),
                      ModifierGroup(
                        ModifierName("s32"),
                        ModifierName("u32")
                      ),
                      ModifierGroup(
                        ModifierName("eq"),
                        ModifierName("ge"),
                        ModifierName("gt"),
                        ModifierName("le"),
                        ModifierName("lt"),
                        ModifierName("ne")
                      ),
                      ModifierGroup(ModifierName("add"))
                    ),
                    _root_.scala.collection.immutable.Set(),
                    true
                  )
                )
              )
            )))
        )
    case Vset4 extends Definition(
          Opcode("vset4"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 4)
              Right(Instruction.Vset4(operands(0), operands(1), operands(2), operands(3), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("vset4", Vec(4), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("simd-video-instructions-vset4"),
                NonEmptyString("9.7.19.2.4. SIMD Video Instructions: vset4"),
                NonEmptyString("vset4"),
                NonEmptyString("SIMD Video Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(NonEmptyString(
                  "// SIMD instruction with secondary SIMD merge operation\nvset4.atype.btype.cmp  d{.mask}, a{.asel}, b{.bsel}, c;\n\n// SIMD instruction with secondary accumulate operation\nvset4.atype.btype.cmp.add  d{.mask}, a{.asel}, b{.bsel}, c;\n\n.atype = .btype = { .u32, .s32 };\n.cmp   = { .eq, .ne, .lt, .le, .gt, .ge };\n.mask  = { .b0,\n           .b1, .b10\n           .b2, .b20, .b21, .b210,\n           .b3, .b30, .b31, .b310, .b32, .b320, .b321, .b3210 };\n    defaults to .b3210\n.asel = .bsel = .bxyzw, where x,y,z,w are from { 0, ..., 7 };\n   .asel defaults to .b3210\n   .bsel defaults to .b7654"
                ))),
                Version(PositiveInt(3), NonNegativeInt.Zero),
                Target(30),
                Vec(
                  InstructionForm(
                    Opcode("vset4"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(ModifierName("atype")),
                      ModifierGroup(
                        ModifierName("s32"),
                        ModifierName("u32")
                      ),
                      ModifierGroup(
                        ModifierName("eq"),
                        ModifierName("ge"),
                        ModifierName("gt"),
                        ModifierName("le"),
                        ModifierName("lt"),
                        ModifierName("ne")
                      )
                    ),
                    _root_.scala.collection.immutable.Set(),
                    true
                  ),
                  InstructionForm(
                    Opcode("vset4"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(ModifierName("atype")),
                      ModifierGroup(
                        ModifierName("s32"),
                        ModifierName("u32")
                      ),
                      ModifierGroup(
                        ModifierName("eq"),
                        ModifierName("ge"),
                        ModifierName("gt"),
                        ModifierName("le"),
                        ModifierName("lt"),
                        ModifierName("ne")
                      ),
                      ModifierGroup(ModifierName("add"))
                    ),
                    _root_.scala.collection.immutable.Set(),
                    true
                  )
                )
              )
            )))
        )
    case Vshl extends Definition(
          Opcode("vshl"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 3 || operands.size == 4)
              Right(Instruction.Vshl(operands(0), operands(1), operands(2), operands.lift(3), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("vshl", Vec(3, 4), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("scalar-video-instructions-vshl-vshr"),
                NonEmptyString("9.7.19.1.2. Scalar Video Instructions: vshl, vshr"),
                NonEmptyString("vshl, vshr"),
                NonEmptyString("Scalar Video Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(NonEmptyString(
                  "// 32-bit scalar operation, with optional secondary operation\nvop.dtype.atype.u32{.sat}.mode       d, a{.asel}, b{.bsel};\nvop.dtype.atype.u32{.sat}.mode.op2   d, a{.asel}, b{.bsel}, c;\n\n// 32-bit scalar operation, with optional data merge\nvop.dtype.atype.u32{.sat}.mode  d.dsel, a{.asel}, b{.bsel}, c;\n\n vop   = { vshl, vshr };\n.dtype = .atype = { .u32, .s32 };\n.mode  = { .clamp, .wrap };\n.dsel  = .asel  = .bsel  = { .b0, .b1, .b2, .b3, .h0, .h1 };\n.op2   = { .add, .min, .max };"
                ))),
                Version(PositiveInt(2), NonNegativeInt.Zero),
                Target(20),
                Vec(
                  InstructionForm(
                    Opcode("vshl"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(ModifierName("dtype")),
                      ModifierGroup(
                        ModifierName("s32"),
                        ModifierName("u32")
                      ),
                      ModifierGroup(ModifierName("u32")),
                      ModifierGroup(
                        ModifierName("clamp"),
                        ModifierName("wrap")
                      )
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName("sat")),
                    true
                  ),
                  InstructionForm(
                    Opcode("vshl"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(ModifierName("dtype")),
                      ModifierGroup(
                        ModifierName("s32"),
                        ModifierName("u32")
                      ),
                      ModifierGroup(ModifierName("u32")),
                      ModifierGroup(
                        ModifierName("clamp"),
                        ModifierName("wrap")
                      ),
                      ModifierGroup(
                        ModifierName("add"),
                        ModifierName("max"),
                        ModifierName("min")
                      )
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName("sat")),
                    true
                  ),
                  InstructionForm(
                    Opcode("vshl"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(ModifierName("dtype")),
                      ModifierGroup(
                        ModifierName("s32"),
                        ModifierName("u32")
                      ),
                      ModifierGroup(ModifierName("u32")),
                      ModifierGroup(
                        ModifierName("clamp"),
                        ModifierName("wrap")
                      )
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName("sat")),
                    true
                  )
                )
              )
            )))
        )
    case Vshr extends Definition(
          Opcode("vshr"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 3 || operands.size == 4)
              Right(Instruction.Vshr(operands(0), operands(1), operands(2), operands.lift(3), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("vshr", Vec(3, 4), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("scalar-video-instructions-vshl-vshr"),
                NonEmptyString("9.7.19.1.2. Scalar Video Instructions: vshl, vshr"),
                NonEmptyString("vshl, vshr"),
                NonEmptyString("Scalar Video Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(NonEmptyString(
                  "// 32-bit scalar operation, with optional secondary operation\nvop.dtype.atype.u32{.sat}.mode       d, a{.asel}, b{.bsel};\nvop.dtype.atype.u32{.sat}.mode.op2   d, a{.asel}, b{.bsel}, c;\n\n// 32-bit scalar operation, with optional data merge\nvop.dtype.atype.u32{.sat}.mode  d.dsel, a{.asel}, b{.bsel}, c;\n\n vop   = { vshl, vshr };\n.dtype = .atype = { .u32, .s32 };\n.mode  = { .clamp, .wrap };\n.dsel  = .asel  = .bsel  = { .b0, .b1, .b2, .b3, .h0, .h1 };\n.op2   = { .add, .min, .max };"
                ))),
                Version(PositiveInt(2), NonNegativeInt.Zero),
                Target(20),
                Vec(
                  InstructionForm(
                    Opcode("vshr"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(ModifierName("dtype")),
                      ModifierGroup(
                        ModifierName("s32"),
                        ModifierName("u32")
                      ),
                      ModifierGroup(ModifierName("u32")),
                      ModifierGroup(
                        ModifierName("clamp"),
                        ModifierName("wrap")
                      )
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName("sat")),
                    true
                  ),
                  InstructionForm(
                    Opcode("vshr"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(ModifierName("dtype")),
                      ModifierGroup(
                        ModifierName("s32"),
                        ModifierName("u32")
                      ),
                      ModifierGroup(ModifierName("u32")),
                      ModifierGroup(
                        ModifierName("clamp"),
                        ModifierName("wrap")
                      ),
                      ModifierGroup(
                        ModifierName("add"),
                        ModifierName("max"),
                        ModifierName("min")
                      )
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName("sat")),
                    true
                  ),
                  InstructionForm(
                    Opcode("vshr"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(ModifierName("dtype")),
                      ModifierGroup(
                        ModifierName("s32"),
                        ModifierName("u32")
                      ),
                      ModifierGroup(ModifierName("u32")),
                      ModifierGroup(
                        ModifierName("clamp"),
                        ModifierName("wrap")
                      )
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName("sat")),
                    true
                  )
                )
              )
            )))
        )
    case Vsub extends Definition(
          Opcode("vsub"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 3 || operands.size == 4)
              Right(Instruction.Vsub(operands(0), operands(1), operands(2), operands.lift(3), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("vsub", Vec(3, 4), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("scalar-video-instructions-vadd-vsub-vabsdiff-vmin-vmax"),
                NonEmptyString("9.7.19.1.1. Scalar Video Instructions: vadd, vsub, vabsdiff, vmin, vmax"),
                NonEmptyString("vadd, vsub, vabsdiff, vmin, vmax"),
                NonEmptyString("Scalar Video Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(NonEmptyString(
                  "// 32-bit scalar operation, with optional secondary operation\nvop.dtype.atype.btype{.sat}       d, a{.asel}, b{.bsel};\nvop.dtype.atype.btype{.sat}.op2   d, a{.asel}, b{.bsel}, c;\n\n// 32-bit scalar operation, with optional data merge\nvop.dtype.atype.btype{.sat}  d.dsel, a{.asel}, b{.bsel}, c;\n\n vop   = { vadd, vsub, vabsdiff, vmin, vmax };\n.dtype = .atype = .btype = { .u32, .s32 };\n.dsel  = .asel  = .bsel  = { .b0, .b1, .b2, .b3, .h0, .h1 };\n.op2   = { .add, .min, .max };"
                ))),
                Version(PositiveInt(2), NonNegativeInt.Zero),
                Target(20),
                Vec(
                  InstructionForm(
                    Opcode("vsub"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(ModifierName("dtype")),
                      ModifierGroup(ModifierName("atype")),
                      ModifierGroup(
                        ModifierName("s32"),
                        ModifierName("u32")
                      )
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName("sat")),
                    true
                  ),
                  InstructionForm(
                    Opcode("vsub"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(ModifierName("dtype")),
                      ModifierGroup(ModifierName("atype")),
                      ModifierGroup(
                        ModifierName("s32"),
                        ModifierName("u32")
                      ),
                      ModifierGroup(
                        ModifierName("add"),
                        ModifierName("max"),
                        ModifierName("min")
                      )
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName("sat")),
                    true
                  ),
                  InstructionForm(
                    Opcode("vsub"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(ModifierName("dtype")),
                      ModifierGroup(ModifierName("atype")),
                      ModifierGroup(
                        ModifierName("s32"),
                        ModifierName("u32")
                      )
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName("sat")),
                    true
                  )
                )
              )
            )))
        )
    case Vsub2 extends Definition(
          Opcode("vsub2"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 4)
              Right(Instruction.Vsub2(operands(0), operands(1), operands(2), operands(3), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("vsub2", Vec(4), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("simd-video-instructions-vadd2-vsub2-vavrg2-vabsdiff2-vmin2-vmax2"),
                NonEmptyString("9.7.19.2.1. SIMD Video Instructions: vadd2, vsub2, vavrg2, vabsdiff2, vmin2, vmax2"),
                NonEmptyString("vadd2, vsub2, vavrg2, vabsdiff2, vmin2, vmax2"),
                NonEmptyString("SIMD Video Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(NonEmptyString(
                  "// SIMD instruction with secondary SIMD merge operation\nvop2.dtype.atype.btype{.sat}  d{.mask}, a{.asel}, b{.bsel}, c;\n\n// SIMD instruction with secondary accumulate operation\nvop2.dtype.atype.btype.add  d{.mask}, a{.asel}, b{.bsel}, c;\n\n vop2  = { vadd2, vsub2, vavrg2, vabsdiff2, vmin2, vmax2 };\n.dtype = .atype = .btype = { .u32, .s32 };\n.mask  = { .h0, .h1, .h10 };  // defaults to .h10\n.asel  = .bsel  = { .hxy, where x,y are from { 0, 1, 2, 3 } };\n   .asel defaults to .h10\n   .bsel defaults to .h32"
                ))),
                Version(PositiveInt(3), NonNegativeInt.Zero),
                Target(30),
                Vec(
                  InstructionForm(
                    Opcode("vsub2"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(ModifierName("dtype")),
                      ModifierGroup(ModifierName("atype")),
                      ModifierGroup(
                        ModifierName("s32"),
                        ModifierName("u32")
                      )
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName("sat")),
                    true
                  ),
                  InstructionForm(
                    Opcode("vsub2"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(ModifierName("dtype")),
                      ModifierGroup(ModifierName("atype")),
                      ModifierGroup(
                        ModifierName("s32"),
                        ModifierName("u32")
                      ),
                      ModifierGroup(ModifierName("add"))
                    ),
                    _root_.scala.collection.immutable.Set(),
                    true
                  )
                )
              )
            )))
        )
    case Vsub4 extends Definition(
          Opcode("vsub4"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 4)
              Right(Instruction.Vsub4(operands(0), operands(1), operands(2), operands(3), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("vsub4", Vec(4), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("simd-video-instructions-vadd4-vsub4-vavrg4-vabsdiff4-vmin4-vmax4"),
                NonEmptyString("9.7.19.2.3. SIMD Video Instructions: vadd4, vsub4, vavrg4, vabsdiff4, vmin4, vmax4"),
                NonEmptyString("vadd4, vsub4, vavrg4, vabsdiff4, vmin4, vmax4"),
                NonEmptyString("SIMD Video Instructions"),
                NonEmptyVec.unsafeFromVec(Vec(NonEmptyString(
                  "// SIMD instruction with secondary SIMD merge operation\nvop4.dtype.atype.btype{.sat}  d{.mask}, a{.asel}, b{.bsel}, c;\n\n// SIMD instruction with secondary accumulate operation\nvop4.dtype.atype.btype.add  d{.mask}, a{.asel}, b{.bsel}, c;\nvop4  = { vadd4, vsub4, vavrg4, vabsdiff4, vmin4, vmax4 };\n\n.dtype = .atype = .btype = { .u32, .s32 };\n.mask  = { .b0,\n           .b1, .b10\n           .b2, .b20, .b21, .b210,\n           .b3, .b30, .b31, .b310, .b32, .b320, .b321, .b3210 };\n    defaults to .b3210\n.asel = .bsel = .bxyzw, where x,y,z,w are from { 0, ..., 7 };\n   .asel defaults to .b3210\n   .bsel defaults to .b7654"
                ))),
                Version(PositiveInt(3), NonNegativeInt.Zero),
                Target(30),
                Vec(
                  InstructionForm(
                    Opcode("vsub4"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(ModifierName("dtype")),
                      ModifierGroup(ModifierName("atype")),
                      ModifierGroup(
                        ModifierName("s32"),
                        ModifierName("u32")
                      )
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName("sat")),
                    true
                  ),
                  InstructionForm(
                    Opcode("vsub4"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(ModifierName("dtype")),
                      ModifierGroup(ModifierName("atype")),
                      ModifierGroup(
                        ModifierName("s32"),
                        ModifierName("u32")
                      ),
                      ModifierGroup(ModifierName("add"))
                    ),
                    _root_.scala.collection.immutable.Set(),
                    true
                  )
                )
              )
            )))
        )
    case Xor extends Definition(
          Opcode("xor"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 3) Right(Instruction.Xor(operands(0), operands(1), operands(2), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("xor", Vec(3), operands.size))
          },
          () =>
            Metadata(NonEmptyVec.unsafeFromVec(Vec(
              Metadata.Entry(
                NonEmptyString("logic-and-shift-instructions-xor"),
                NonEmptyString("9.7.8.3. Logic and Shift Instructions: xor"),
                NonEmptyString("xor"),
                NonEmptyString("Logic and Shift Instructions"),
                NonEmptyVec.unsafeFromVec(
                  Vec(NonEmptyString("xor.type d, a, b;\n\n.type = { .pred, .b16, .b32, .b64 };"))
                ),
                Version(PositiveInt(1), NonNegativeInt.Zero),
                Target(10),
                Vec(InstructionForm(
                  Opcode("xor"),
                  Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                  Vec(ModifierGroup(
                    ModifierName("b16"),
                    ModifierName("b32"),
                    ModifierName("b64"),
                    ModifierName("pred")
                  )),
                  _root_.scala.collection.immutable.Set(),
                  true
                ))
              )
            )))
        )

    final lazy val meta: Metadata = metadata()

    private[v9_3] final val fixedMods: Vec[String] = Vec.from(opcode.raw.split('.')).drop(1)
  }

  val version: Version = Version(PositiveInt(9), NonNegativeInt(3))

  val definitions: Vec[Definition] = Definition.values.toVec

  private def normalizeOpcode(value: String): String = value.map(_.toLower)

  private lazy val DefinitionsByMnemonic: Map[String, Vec[Definition]] =
    definitions.groupMap(_.opcode.raw.takeWhile(_ != '.'))(value => value)

  def decode(
      opcode: String,
      operands: Vec[Operand],
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Predication] = None
  ): Either[DecodeError, Instruction] = {
    val opcodeParts = Vec.from(normalizeOpcode(opcode).split('.')).filter(_.nonEmpty)
    val name = opcodeParts.headOption.getOrElse("")
    val allModifiers = opcodeParts.drop(1).map(value => Modifier(ModifierName.unsafe(value))) ++ mods
    decodeParsed(name, operands, allModifiers, pred)
  }

  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private[v9_3] def decodeParsed(
      mnemonic: String,
      operands: Vec[Operand],
      mods: Vec[Modifier],
      pred: Option[Predication]
  ): Either[DecodeError, Instruction] = {
    val name = normalizeOpcode(mnemonic)
    val allModifiers = mods
    val definitions = DefinitionsByMnemonic.getOrElse(name, Vec.empty[Definition])
    var specificity = -1
    var index = 0
    while (index < definitions.size) {
      val definition = definitions(index)
      if (definition.fixedMods.size > specificity && fixedModifiersMatch(allModifiers, definition.fixedMods))
        specificity = definition.fixedMods.size
      index += 1
    }

    var successful: Option[(Definition, Instruction)] = None
    var ambiguous = Vec.empty[String]
    var expected = Vec.empty[Int]
    index = 0
    while (index < definitions.size) {
      val definition = definitions(index)
      if (definition.fixedMods.size == specificity && fixedModifiersMatch(allModifiers, definition.fixedMods))
        definition.decoder(operands, allModifiers.drop(specificity), pred) match {
          case Right(instruction) => successful match {
              case None => successful = Some(definition -> instruction)
              case Some((first, _)) =>
                if (ambiguous.isEmpty) ambiguous = Vec(first.toString)
                ambiguous = ambiguous :+ definition.toString
            }
          case Left(DecodeError.InvalidOperandCount(_, counts, _)) => expected = expected ++ counts
          case Left(_) => ()
        }
      index += 1
    }

    if (ambiguous.nonEmpty)
      Left(DecodeError.AmbiguousInstruction(qualifiedOpcode(name, allModifiers), ambiguous))
    else successful match {
      case Some((_, instruction)) => Right(instruction)
      case None if expected.nonEmpty =>
        Left(DecodeError.InvalidOperandCount(
          qualifiedOpcode(name, allModifiers),
          expected.distinct.sorted,
          operands.size
        ))
      case None => Left(DecodeError.UnknownOpcode(qualifiedOpcode(name, allModifiers)))
    }
  }

  private def fixedModifiersMatch(modifiers: Vec[Modifier], fixed: Vec[String]): Boolean =
    fixed.size <= modifiers.size && fixed.indices.forall(index => modifiers(index).name.raw == fixed(index))

  private def qualifiedOpcode(name: String, modifiers: Vec[Modifier]): String =
    (name +: modifiers.map(_.name)).mkString(".")
}
