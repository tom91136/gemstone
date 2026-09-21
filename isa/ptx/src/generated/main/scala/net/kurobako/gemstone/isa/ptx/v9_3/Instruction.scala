// Generated PTX instruction model from the pinned NVIDIA ISA 9.3 specification. Regenerate with: sbt codegen
// scalafix:off GemstoneNoAssume; values were validated by the generator
package net.kurobako.gemstone.isa.ptx.v9_3

import Program.*
import ISA.*
import Metadata.*

import net.kurobako.gemstone.NoNul
import net.kurobako.gemstone.Vec
import net.kurobako.gemstone.Vec.*

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

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
          Opcode.assume("abs"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 2) Right(Instruction.Abs(operands(0), operands(1), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("abs", Vec(2), operands.size))
          },
          () =>
            Metadata(Vec(
              Metadata.Entry(
                "integer-arithmetic-instructions-abs".assume[MinLength[1] & NoNul],
                "9.7.1.11. Integer Arithmetic Instructions: abs".assume[MinLength[1] & NoNul],
                "abs".assume[MinLength[1] & NoNul],
                "Integer Arithmetic Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "abs.type  d, a;\n\n.type = { .s16, .s32, .s64 };"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(1, 0),
                Target(10),
                Vec(InstructionForm(
                  Opcode.assume("abs"),
                  Vec(OperandConstraint.Register, OperandConstraint.Value),
                  Vec(_root_.scala.collection.immutable.Set(
                    ModifierName.assume("s16"),
                    ModifierName.assume("s32"),
                    ModifierName.assume("s64")
                  ).assume[MinLength[1]]),
                  _root_.scala.collection.immutable.Set(),
                  true
                ))
              ),
              Metadata.Entry(
                "floating-point-instructions-abs".assume[MinLength[1] & NoNul],
                "9.7.3.9. Floating Point Instructions: abs".assume[MinLength[1] & NoNul],
                "abs".assume[MinLength[1] & NoNul],
                "Floating Point Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "abs{.ftz}.f32  d, a;\nabs.f64        d, a;"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(1, 0),
                Target(13),
                Vec(
                  InstructionForm(
                    Opcode.assume("abs"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(_root_.scala.collection.immutable.Set(ModifierName.assume("f32")).assume[MinLength[1]]),
                    _root_.scala.collection.immutable.Set(ModifierName.assume("ftz")),
                    true
                  ),
                  InstructionForm(
                    Opcode.assume("abs"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(_root_.scala.collection.immutable.Set(ModifierName.assume("f64")).assume[MinLength[1]]),
                    _root_.scala.collection.immutable.Set(),
                    true
                  )
                )
              ),
              Metadata.Entry(
                "half-precision-floating-point-instructions-abs".assume[MinLength[1] & NoNul],
                "9.7.4.6. Half Precision Floating Point Instructions: abs".assume[MinLength[1] & NoNul],
                "abs".assume[MinLength[1] & NoNul],
                "Half Precision Floating Point Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "abs{.ftz}.f16    d, a;\nabs{.ftz}.f16x2  d, a;\nabs.bf16         d, a;\nabs.bf16x2       d, a;"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(6, 5),
                Target(53),
                Vec(
                  InstructionForm(
                    Opcode.assume("abs"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(_root_.scala.collection.immutable.Set(ModifierName.assume("f16")).assume[MinLength[1]]),
                    _root_.scala.collection.immutable.Set(ModifierName.assume("ftz")),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("abs"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(_root_.scala.collection.immutable.Set(ModifierName.assume("f16x2")).assume[MinLength[1]]),
                    _root_.scala.collection.immutable.Set(ModifierName.assume("ftz")),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("abs"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(_root_.scala.collection.immutable.Set(ModifierName.assume("bf16")).assume[MinLength[1]]),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("abs"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(_root_.scala.collection.immutable.Set(ModifierName.assume("bf16x2")).assume[MinLength[1]]),
                    _root_.scala.collection.immutable.Set(),
                    false
                  )
                )
              )
            ).assume[MinLength[1]])
        )
    case Activemask extends Definition(
          Opcode.assume("activemask"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 1) Right(Instruction.Activemask(operands(0), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("activemask", Vec(1), operands.size))
          },
          () =>
            Metadata(Vec(
              Metadata.Entry(
                "parallel-synchronization-and-communication-instructions-activemask".assume[MinLength[1] & NoNul],
                "9.7.14.12. Parallel Synchronization and Communication Instructions: activemask".assume[MinLength[
                  1
                ] & NoNul],
                "activemask".assume[MinLength[1] & NoNul],
                "Parallel Synchronization and Communication Instructions".assume[MinLength[1] & NoNul],
                Vec[String]("activemask.b32 d;").map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(6, 2),
                Target(30),
                Vec(InstructionForm(
                  Opcode.assume("activemask"),
                  Vec(OperandConstraint.Register),
                  Vec(_root_.scala.collection.immutable.Set(ModifierName.assume("b32")).assume[MinLength[1]]),
                  _root_.scala.collection.immutable.Set(),
                  true
                ))
              )
            ).assume[MinLength[1]])
        )
    case Add extends Definition(
          Opcode.assume("add"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 3) Right(Instruction.Add(operands(0), operands(1), operands(2), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("add", Vec(3), operands.size))
          },
          () =>
            Metadata(Vec(
              Metadata.Entry(
                "integer-arithmetic-instructions-add".assume[MinLength[1] & NoNul],
                "9.7.1.1. Integer Arithmetic Instructions: add".assume[MinLength[1] & NoNul],
                "add".assume[MinLength[1] & NoNul],
                "Integer Arithmetic Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "add.type1        d, a, b;\nadd{.sat}.type2  d, a, b;\n\n.type1 = { .u16, .u64,\n           .s16, .s64 };\n\n.type2 = { .u32, .u16x2, .u8x4,\n           .s32, .s16x2, .s8x4 };"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(1, 0),
                Target(10),
                Vec(
                  InstructionForm(
                    Opcode.assume("add"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(_root_.scala.collection.immutable.Set(
                      ModifierName.assume("s16"),
                      ModifierName.assume("s64"),
                      ModifierName.assume("u16"),
                      ModifierName.assume("u64")
                    ).assume[MinLength[1]]),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("add"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(_root_.scala.collection.immutable.Set(
                      ModifierName.assume("s16x2"),
                      ModifierName.assume("s32"),
                      ModifierName.assume("s8x4"),
                      ModifierName.assume("u16x2"),
                      ModifierName.assume("u32"),
                      ModifierName.assume("u8x4")
                    ).assume[MinLength[1]]),
                    _root_.scala.collection.immutable.Set(ModifierName.assume("sat")),
                    false
                  )
                )
              ),
              Metadata.Entry(
                "floating-point-instructions-add".assume[MinLength[1] & NoNul],
                "9.7.3.3. Floating Point Instructions: add".assume[MinLength[1] & NoNul],
                "add".assume[MinLength[1] & NoNul],
                "Floating Point Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "add{.rnd}{.ftz}{.sat}.f32  d, a, b;\nadd{.rnd}{.ftz}.f32x2      d, a, b;\nadd{.rnd}.f64              d, a, b;\n\n.rnd = { .rn, .rz, .rm, .rp };"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(1, 0),
                Target(13),
                Vec(
                  InstructionForm(
                    Opcode.assume("add"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(_root_.scala.collection.immutable.Set(ModifierName.assume("f32")).assume[MinLength[1]]),
                    _root_.scala.collection.immutable.Set(
                      ModifierName.assume("ftz"),
                      ModifierName.assume("rm"),
                      ModifierName.assume("rn"),
                      ModifierName.assume("rp"),
                      ModifierName.assume("rz"),
                      ModifierName.assume("sat")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("add"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(_root_.scala.collection.immutable.Set(ModifierName.assume("f32x2")).assume[MinLength[1]]),
                    _root_.scala.collection.immutable.Set(
                      ModifierName.assume("ftz"),
                      ModifierName.assume("rm"),
                      ModifierName.assume("rn"),
                      ModifierName.assume("rp"),
                      ModifierName.assume("rz")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("add"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(_root_.scala.collection.immutable.Set(ModifierName.assume("f64")).assume[MinLength[1]]),
                    _root_.scala.collection.immutable.Set(
                      ModifierName.assume("rm"),
                      ModifierName.assume("rn"),
                      ModifierName.assume("rp"),
                      ModifierName.assume("rz")
                    ),
                    false
                  )
                )
              ),
              Metadata.Entry(
                "half-precision-floating-point-instructions-add".assume[MinLength[1] & NoNul],
                "9.7.4.1. Half Precision Floating Point Instructions: add".assume[MinLength[1] & NoNul],
                "add".assume[MinLength[1] & NoNul],
                "Half Precision Floating Point Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "add{.rnd}{.ftz}{.sat}.f16   d, a, b;\nadd{.rnd}{.ftz}{.sat}.f16x2 d, a, b;\n\nadd{.rnd}.bf16   d, a, b;\nadd{.rnd}.bf16x2 d, a, b;\n\n.rnd = { .rn };"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(4, 2),
                Target(53),
                Vec(
                  InstructionForm(
                    Opcode.assume("add"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(_root_.scala.collection.immutable.Set(ModifierName.assume("f16")).assume[MinLength[1]]),
                    _root_.scala.collection.immutable.Set(
                      ModifierName.assume("ftz"),
                      ModifierName.assume("rn"),
                      ModifierName.assume("sat")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("add"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(_root_.scala.collection.immutable.Set(ModifierName.assume("f16x2")).assume[MinLength[1]]),
                    _root_.scala.collection.immutable.Set(
                      ModifierName.assume("ftz"),
                      ModifierName.assume("rn"),
                      ModifierName.assume("sat")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("add"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(_root_.scala.collection.immutable.Set(ModifierName.assume("bf16")).assume[MinLength[1]]),
                    _root_.scala.collection.immutable.Set(ModifierName.assume("rn")),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("add"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(_root_.scala.collection.immutable.Set(ModifierName.assume("bf16x2")).assume[MinLength[1]]),
                    _root_.scala.collection.immutable.Set(ModifierName.assume("rn")),
                    false
                  )
                )
              ),
              Metadata.Entry(
                "mixed-precision-floating-point-instructions-add".assume[MinLength[1] & NoNul],
                "9.7.5.1. Mixed Precision Floating Point Instructions: add".assume[MinLength[1] & NoNul],
                "add".assume[MinLength[1] & NoNul],
                "Mixed Precision Floating Point Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "add{.rnd}{.sat}.f32.atype  d, a, c;\n\n.atype = { .f16, .bf16};\n.rnd   = { .rn, .rz, .rm, .rp };"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(1, 0),
                Target(100),
                Vec(InstructionForm(
                  Opcode.assume("add"),
                  Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                  Vec(
                    _root_.scala.collection.immutable.Set(ModifierName.assume("f32")).assume[MinLength[1]],
                    _root_.scala.collection.immutable.Set(
                      ModifierName.assume("bf16"),
                      ModifierName.assume("f16")
                    ).assume[MinLength[1]]
                  ),
                  _root_.scala.collection.immutable.Set(
                    ModifierName.assume("rm"),
                    ModifierName.assume("rn"),
                    ModifierName.assume("rp"),
                    ModifierName.assume("rz"),
                    ModifierName.assume("sat")
                  ),
                  true
                ))
              )
            ).assume[MinLength[1]])
        )
    case AddCc extends Definition(
          Opcode.assume("add.cc"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 3) Right(Instruction.AddCc(operands(0), operands(1), operands(2), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("add.cc", Vec(3), operands.size))
          },
          () =>
            Metadata(Vec(
              Metadata.Entry(
                "extended-precision-arithmetic-instructions-add-cc".assume[MinLength[1] & NoNul],
                "9.7.2.1. Extended-Precision Arithmetic Instructions: add.cc".assume[MinLength[1] & NoNul],
                "add.cc".assume[MinLength[1] & NoNul],
                "Extended-Precision Arithmetic Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "add.cc.type  d, a, b;\n\n.type = { .u32, .s32, .u64, .s64 };"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(1, 0),
                Target(20),
                Vec(InstructionForm(
                  Opcode.assume("add.cc"),
                  Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                  Vec(_root_.scala.collection.immutable.Set(
                    ModifierName.assume("s32"),
                    ModifierName.assume("s64"),
                    ModifierName.assume("u32"),
                    ModifierName.assume("u64")
                  ).assume[MinLength[1]]),
                  _root_.scala.collection.immutable.Set(),
                  true
                ))
              )
            ).assume[MinLength[1]])
        )
    case Addc extends Definition(
          Opcode.assume("addc"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 3) Right(Instruction.Addc(operands(0), operands(1), operands(2), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("addc", Vec(3), operands.size))
          },
          () =>
            Metadata(Vec(
              Metadata.Entry(
                "extended-precision-arithmetic-instructions-addc".assume[MinLength[1] & NoNul],
                "9.7.2.2. Extended-Precision Arithmetic Instructions: addc".assume[MinLength[1] & NoNul],
                "addc".assume[MinLength[1] & NoNul],
                "Extended-Precision Arithmetic Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "addc{.cc}.type  d, a, b;\n\n.type = { .u32, .s32, .u64, .s64 };"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(1, 0),
                Target(20),
                Vec(InstructionForm(
                  Opcode.assume("addc"),
                  Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                  Vec(_root_.scala.collection.immutable.Set(
                    ModifierName.assume("s32"),
                    ModifierName.assume("s64"),
                    ModifierName.assume("u32"),
                    ModifierName.assume("u64")
                  ).assume[MinLength[1]]),
                  _root_.scala.collection.immutable.Set(ModifierName.assume("cc")),
                  true
                ))
              )
            ).assume[MinLength[1]])
        )
    case Alloca extends Definition(
          Opcode.assume("alloca"),
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
            Metadata(Vec(
              Metadata.Entry(
                "stack-manipulation-instructions-alloca".assume[MinLength[1] & NoNul],
                "9.7.18.3. Stack Manipulation Instructions: alloca".assume[MinLength[1] & NoNul],
                "alloca".assume[MinLength[1] & NoNul],
                "Stack Manipulation Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "alloca.type  ptr, size{, immAlign};\n\n.type = { .u32, .u64 };"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(7, 3),
                Target(52),
                Vec(InstructionForm(
                  Opcode.assume("alloca"),
                  Vec(OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value),
                  Vec(_root_.scala.collection.immutable.Set(
                    ModifierName.assume("u32"),
                    ModifierName.assume("u64")
                  ).assume[MinLength[1]]),
                  _root_.scala.collection.immutable.Set(),
                  true
                ))
              )
            ).assume[MinLength[1]])
        )
    case And extends Definition(
          Opcode.assume("and"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 3) Right(Instruction.And(operands(0), operands(1), operands(2), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("and", Vec(3), operands.size))
          },
          () =>
            Metadata(Vec(
              Metadata.Entry(
                "logic-and-shift-instructions-and".assume[MinLength[1] & NoNul],
                "9.7.8.1. Logic and Shift Instructions: and".assume[MinLength[1] & NoNul],
                "and".assume[MinLength[1] & NoNul],
                "Logic and Shift Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "and.type d, a, b;\n\n.type = { .pred, .b16, .b32, .b64 };"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(1, 0),
                Target(10),
                Vec(InstructionForm(
                  Opcode.assume("and"),
                  Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                  Vec(_root_.scala.collection.immutable.Set(
                    ModifierName.assume("b16"),
                    ModifierName.assume("b32"),
                    ModifierName.assume("b64"),
                    ModifierName.assume("pred")
                  ).assume[MinLength[1]]),
                  _root_.scala.collection.immutable.Set(),
                  true
                ))
              )
            ).assume[MinLength[1]])
        )
    case ApplyPriority extends Definition(
          Opcode.assume("applypriority"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 2) Right(Instruction.ApplyPriority(operands(0), operands(1), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("applypriority", Vec(2), operands.size))
          },
          () =>
            Metadata(Vec(
              Metadata.Entry(
                "data-movement-and-conversion-instructions-applypriority".assume[MinLength[1] & NoNul],
                "9.7.9.17. Data Movement and Conversion Instructions: applypriority".assume[MinLength[1] & NoNul],
                "applypriority".assume[MinLength[1] & NoNul],
                "Data Movement and Conversion Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "applypriority{.global}.level::eviction_priority  [a], size;\n\n.level::eviction_priority = { .L2::evict_normal };"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(7, 4),
                Target(80),
                Vec(InstructionForm(
                  Opcode.assume("applypriority"),
                  Vec(OperandConstraint.Address, OperandConstraint.Value),
                  Vec(
                    _root_.scala.collection.immutable.Set(ModifierName.assume("L2::evict_normal")).assume[MinLength[1]]
                  ),
                  _root_.scala.collection.immutable.Set(ModifierName.assume("global")),
                  true
                ))
              )
            ).assume[MinLength[1]])
        )
    case Atom extends Definition(
          Opcode.assume("atom"),
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
            Metadata(Vec(
              Metadata.Entry(
                "parallel-synchronization-and-communication-instructions-atom".assume[MinLength[1] & NoNul],
                "9.7.14.5. Parallel Synchronization and Communication Instructions: atom".assume[MinLength[1] & NoNul],
                "atom".assume[MinLength[1] & NoNul],
                "Parallel Synchronization and Communication Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "atom{.sem}{.scope}{.space}.op{.level::cache_hint}.type d, [a], b{, cache_policy};\n\natom{.sem}{.scope}{.space}.cas.type1 d, [a], b, c;\n\natom{.sem}{.scope}{.space}.exch{.level::cache_hint}.type2 d, [a], b {, cache_policy};\n\natom{.sem}{.scope}{.space}.add.noftz{.level::cache_hint}.f16     d, [a], b{, cache_policy};\natom{.sem}{.scope}{.space}.add.noftz{.level::cache_hint}.f16x2   d, [a], b{, cache_policy};\n\natom{.sem}{.scope}{.space}.add.noftz{.level::cache_hint}.bf16    d, [a], b{, cache_policy};\natom{.sem}{.scope}{.space}.add.noftz{.level::cache_hint}.bf16x2  d, [a], b{, cache_policy};\n\n.space =              { .global, .shared{::cta, ::cluster} };\n.sem =                { .relaxed, .acquire, .release, .acq_rel };\n.scope =              { .cta, .cluster, .gpu, .sys };\n\n.op =                 { .and, .or, .xor,\n                        .add, .inc, .dec,\n                        .min, .max };\n.level::cache_hint =  { .L2::cache_hint };\n.type =               { .b32, .b64, .u32, .u64, .s32, .s64, .f32, .f64 };\n.type1 =              { .b16, .b32, .b64, .b128 };\n.type2 =              { .b32, .b64, .b128 };",
                  "atom{.sem}{.scope}{.global}.add{.level::cache_hint}.vec_32_bit.f32                  d, [a], b{, cache_policy};\natom{.sem}{.scope}{.global}.op.noftz{.level::cache_hint}.vec_16_bit.half_word_type  d, [a], b{, cache_policy};\natom{.sem}{.scope}{.global}.op.noftz{.level::cache_hint}.vec_32_bit.packed_type     d, [a], b{, cache_policy};\n\n.sem =               { .relaxed, .acquire, .release, .acq_rel };\n.scope =             { .cta, .cluster, .gpu, .sys };\n.op =                { .add, .min, .max };\n.half_word_type =    { .f16, .bf16 };\n.packed_type =       { .f16x2, .bf16x2 };\n.vec_16_bit =        { .v2, .v4, .v8 }\n.vec_32_bit =        { .v2, .v4 };\n.level::cache_hint = { .L2::cache_hint }"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(1, 0),
                Target(11),
                Vec(
                  InstructionForm(
                    Opcode.assume("atom"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("add"),
                        ModifierName.assume("and"),
                        ModifierName.assume("dec"),
                        ModifierName.assume("inc"),
                        ModifierName.assume("max"),
                        ModifierName.assume("min"),
                        ModifierName.assume("or"),
                        ModifierName.assume("xor")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("b32"),
                        ModifierName.assume("b64"),
                        ModifierName.assume("f32"),
                        ModifierName.assume("f64"),
                        ModifierName.assume("s32"),
                        ModifierName.assume("s64"),
                        ModifierName.assume("u32"),
                        ModifierName.assume("u64")
                      ).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName.assume("L2::cache_hint"),
                      ModifierName.assume("acq_rel"),
                      ModifierName.assume("acquire"),
                      ModifierName.assume("cluster"),
                      ModifierName.assume("cta"),
                      ModifierName.assume("global"),
                      ModifierName.assume("gpu"),
                      ModifierName.assume("relaxed"),
                      ModifierName.assume("release"),
                      ModifierName.assume("sys")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("atom"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("cas")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("b128"),
                        ModifierName.assume("b16"),
                        ModifierName.assume("b32"),
                        ModifierName.assume("b64")
                      ).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName.assume("acq_rel"),
                      ModifierName.assume("acquire"),
                      ModifierName.assume("cluster"),
                      ModifierName.assume("cta"),
                      ModifierName.assume("global"),
                      ModifierName.assume("gpu"),
                      ModifierName.assume("relaxed"),
                      ModifierName.assume("release"),
                      ModifierName.assume("sys")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("atom"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("exch")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("b128"),
                        ModifierName.assume("b32"),
                        ModifierName.assume("b64")
                      ).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName.assume("L2::cache_hint"),
                      ModifierName.assume("acq_rel"),
                      ModifierName.assume("acquire"),
                      ModifierName.assume("cluster"),
                      ModifierName.assume("cta"),
                      ModifierName.assume("global"),
                      ModifierName.assume("gpu"),
                      ModifierName.assume("relaxed"),
                      ModifierName.assume("release"),
                      ModifierName.assume("sys")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("atom"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("add")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("noftz")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("f16")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName.assume("L2::cache_hint"),
                      ModifierName.assume("acq_rel"),
                      ModifierName.assume("acquire"),
                      ModifierName.assume("cluster"),
                      ModifierName.assume("cta"),
                      ModifierName.assume("global"),
                      ModifierName.assume("gpu"),
                      ModifierName.assume("relaxed"),
                      ModifierName.assume("release"),
                      ModifierName.assume("sys")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("atom"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("add")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("noftz")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("f16x2")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName.assume("L2::cache_hint"),
                      ModifierName.assume("acq_rel"),
                      ModifierName.assume("acquire"),
                      ModifierName.assume("cluster"),
                      ModifierName.assume("cta"),
                      ModifierName.assume("global"),
                      ModifierName.assume("gpu"),
                      ModifierName.assume("relaxed"),
                      ModifierName.assume("release"),
                      ModifierName.assume("sys")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("atom"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("add")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("noftz")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("bf16")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName.assume("L2::cache_hint"),
                      ModifierName.assume("acq_rel"),
                      ModifierName.assume("acquire"),
                      ModifierName.assume("cluster"),
                      ModifierName.assume("cta"),
                      ModifierName.assume("global"),
                      ModifierName.assume("gpu"),
                      ModifierName.assume("relaxed"),
                      ModifierName.assume("release"),
                      ModifierName.assume("sys")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("atom"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("add")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("noftz")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("bf16x2")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName.assume("L2::cache_hint"),
                      ModifierName.assume("acq_rel"),
                      ModifierName.assume("acquire"),
                      ModifierName.assume("cluster"),
                      ModifierName.assume("cta"),
                      ModifierName.assume("global"),
                      ModifierName.assume("gpu"),
                      ModifierName.assume("relaxed"),
                      ModifierName.assume("release"),
                      ModifierName.assume("sys")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("atom"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("add")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("v2"),
                        ModifierName.assume("v4")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("f32")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName.assume("L2::cache_hint"),
                      ModifierName.assume("acq_rel"),
                      ModifierName.assume("acquire"),
                      ModifierName.assume("cluster"),
                      ModifierName.assume("cta"),
                      ModifierName.assume("global"),
                      ModifierName.assume("gpu"),
                      ModifierName.assume("relaxed"),
                      ModifierName.assume("release"),
                      ModifierName.assume("sys")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("atom"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("add"),
                        ModifierName.assume("max"),
                        ModifierName.assume("min")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("noftz")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("v2"),
                        ModifierName.assume("v4"),
                        ModifierName.assume("v8")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("bf16"),
                        ModifierName.assume("f16")
                      ).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName.assume("L2::cache_hint"),
                      ModifierName.assume("acq_rel"),
                      ModifierName.assume("acquire"),
                      ModifierName.assume("cluster"),
                      ModifierName.assume("cta"),
                      ModifierName.assume("global"),
                      ModifierName.assume("gpu"),
                      ModifierName.assume("relaxed"),
                      ModifierName.assume("release"),
                      ModifierName.assume("sys")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("atom"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("add"),
                        ModifierName.assume("max"),
                        ModifierName.assume("min")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("noftz")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("v2"),
                        ModifierName.assume("v4")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("bf16x2"),
                        ModifierName.assume("f16x2")
                      ).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName.assume("L2::cache_hint"),
                      ModifierName.assume("acq_rel"),
                      ModifierName.assume("acquire"),
                      ModifierName.assume("cluster"),
                      ModifierName.assume("cta"),
                      ModifierName.assume("global"),
                      ModifierName.assume("gpu"),
                      ModifierName.assume("relaxed"),
                      ModifierName.assume("release"),
                      ModifierName.assume("sys")
                    ),
                    false
                  )
                )
              )
            ).assume[MinLength[1]])
        )
    case Bar extends Definition(
          Opcode.assume("bar"),
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
            Metadata(Vec(
              Metadata.Entry(
                "parallel-synchronization-and-communication-instructions-bar".assume[MinLength[1] & NoNul],
                "9.7.14.1. Parallel Synchronization and Communication Instructions: bar, barrier".assume[MinLength[
                  1
                ] & NoNul],
                "bar, barrier".assume[MinLength[1] & NoNul],
                "Parallel Synchronization and Communication Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "barrier{.cta}.sync{.aligned}      a{, b};\nbarrier{.cta}.arrive{.aligned}    a, b;\n\nbarrier{.cta}.red.popc{.aligned}.u32  d, a{, b}, {!}c;\nbarrier{.cta}.red.op{.aligned}.pred   p, a{, b}, {!}c;\n\nbar{.cta}.sync      a{, b};\nbar{.cta}.arrive    a, b;\n\nbar{.cta}.red.popc.u32  d, a{, b}, {!}c;\nbar{.cta}.red.op.pred   p, a{, b}, {!}c;\n\n.op = { .and, .or };"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(1, 0),
                Target(20),
                Vec(
                  InstructionForm(
                    Opcode.assume("bar"),
                    Vec(OperandConstraint.Value, OperandConstraint.Value),
                    Vec(_root_.scala.collection.immutable.Set(ModifierName.assume("sync")).assume[MinLength[1]]),
                    _root_.scala.collection.immutable.Set(ModifierName.assume("cta")),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("bar"),
                    Vec(OperandConstraint.Value, OperandConstraint.Value),
                    Vec(_root_.scala.collection.immutable.Set(ModifierName.assume("arrive")).assume[MinLength[1]]),
                    _root_.scala.collection.immutable.Set(ModifierName.assume("cta")),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("bar"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("red")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("popc")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("u32")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName.assume("cta")),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("bar"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("red")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("and"),
                        ModifierName.assume("or")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("pred")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName.assume("cta")),
                    false
                  )
                )
              )
            ).assume[MinLength[1]])
        )
    case BarWarpSync extends Definition(
          Opcode.assume("bar.warp.sync"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 1) Right(Instruction.BarWarpSync(operands(0), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("bar.warp.sync", Vec(1), operands.size))
          },
          () =>
            Metadata(Vec(
              Metadata.Entry(
                "parallel-synchronization-and-communication-instructions-bar-warp-sync".assume[MinLength[1] & NoNul],
                "9.7.14.2. Parallel Synchronization and Communication Instructions: bar.warp.sync".assume[MinLength[
                  1
                ] & NoNul],
                "bar.warp.sync".assume[MinLength[1] & NoNul],
                "Parallel Synchronization and Communication Instructions".assume[MinLength[1] & NoNul],
                Vec[String]("bar.warp.sync      membermask;").map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(6, 0),
                Target(30),
                Vec(InstructionForm(
                  Opcode.assume("bar.warp.sync"),
                  Vec(OperandConstraint.Value),
                  Vec(),
                  _root_.scala.collection.immutable.Set(),
                  true
                ))
              )
            ).assume[MinLength[1]])
        )
    case Barrier extends Definition(
          Opcode.assume("barrier"),
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
            Metadata(Vec(
              Metadata.Entry(
                "parallel-synchronization-and-communication-instructions-bar".assume[MinLength[1] & NoNul],
                "9.7.14.1. Parallel Synchronization and Communication Instructions: bar, barrier".assume[MinLength[
                  1
                ] & NoNul],
                "bar, barrier".assume[MinLength[1] & NoNul],
                "Parallel Synchronization and Communication Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "barrier{.cta}.sync{.aligned}      a{, b};\nbarrier{.cta}.arrive{.aligned}    a, b;\n\nbarrier{.cta}.red.popc{.aligned}.u32  d, a{, b}, {!}c;\nbarrier{.cta}.red.op{.aligned}.pred   p, a{, b}, {!}c;\n\nbar{.cta}.sync      a{, b};\nbar{.cta}.arrive    a, b;\n\nbar{.cta}.red.popc.u32  d, a{, b}, {!}c;\nbar{.cta}.red.op.pred   p, a{, b}, {!}c;\n\n.op = { .and, .or };"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(1, 0),
                Target(20),
                Vec(
                  InstructionForm(
                    Opcode.assume("barrier"),
                    Vec(OperandConstraint.Value, OperandConstraint.Value),
                    Vec(_root_.scala.collection.immutable.Set(ModifierName.assume("sync")).assume[MinLength[1]]),
                    _root_.scala.collection.immutable.Set(ModifierName.assume("aligned"), ModifierName.assume("cta")),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("barrier"),
                    Vec(OperandConstraint.Value, OperandConstraint.Value),
                    Vec(_root_.scala.collection.immutable.Set(ModifierName.assume("arrive")).assume[MinLength[1]]),
                    _root_.scala.collection.immutable.Set(ModifierName.assume("aligned"), ModifierName.assume("cta")),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("barrier"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("red")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("popc")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("u32")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName.assume("aligned"), ModifierName.assume("cta")),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("barrier"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("red")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("and"),
                        ModifierName.assume("or")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("pred")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName.assume("aligned"), ModifierName.assume("cta")),
                    false
                  )
                )
              )
            ).assume[MinLength[1]])
        )
    case BarrierCluster extends Definition(
          Opcode.assume("barrier.cluster"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 0) Right(Instruction.BarrierCluster(mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("barrier.cluster", Vec(0), operands.size))
          },
          () =>
            Metadata(Vec(
              Metadata.Entry(
                "parallel-synchronization-and-communication-instructions-barrier-cluster".assume[MinLength[1] & NoNul],
                "9.7.14.3. Parallel Synchronization and Communication Instructions: barrier.cluster".assume[MinLength[
                  1
                ] & NoNul],
                "barrier.cluster".assume[MinLength[1] & NoNul],
                "Parallel Synchronization and Communication Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "barrier.cluster.arrive{.sem}{.aligned};\nbarrier.cluster.wait{.acquire}{.aligned};\n\n.sem = {.release, .relaxed}"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(7, 8),
                Target(90),
                Vec(
                  InstructionForm(
                    Opcode.assume("barrier.cluster"),
                    Vec(),
                    Vec(_root_.scala.collection.immutable.Set(ModifierName.assume("arrive")).assume[MinLength[1]]),
                    _root_.scala.collection.immutable.Set(
                      ModifierName.assume("aligned"),
                      ModifierName.assume("relaxed"),
                      ModifierName.assume("release")
                    ),
                    true
                  ),
                  InstructionForm(
                    Opcode.assume("barrier.cluster"),
                    Vec(),
                    Vec(_root_.scala.collection.immutable.Set(ModifierName.assume("wait")).assume[MinLength[1]]),
                    _root_.scala.collection.immutable.Set(
                      ModifierName.assume("acquire"),
                      ModifierName.assume("aligned")
                    ),
                    true
                  )
                )
              )
            ).assume[MinLength[1]])
        )
    case Bfe extends Definition(
          Opcode.assume("bfe"),
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
            Metadata(Vec(
              Metadata.Entry(
                "integer-arithmetic-instructions-bfe".assume[MinLength[1] & NoNul],
                "9.7.1.20. Integer Arithmetic Instructions: bfe".assume[MinLength[1] & NoNul],
                "bfe".assume[MinLength[1] & NoNul],
                "Integer Arithmetic Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "bfe.type  d, a, b, c;\n\n.type = { .u32, .u64,\n          .s32, .s64 };"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(2, 0),
                Target(20),
                Vec(InstructionForm(
                  Opcode.assume("bfe"),
                  Vec(
                    OperandConstraint.Register,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Vec(_root_.scala.collection.immutable.Set(
                    ModifierName.assume("s32"),
                    ModifierName.assume("s64"),
                    ModifierName.assume("u32"),
                    ModifierName.assume("u64")
                  ).assume[MinLength[1]]),
                  _root_.scala.collection.immutable.Set(),
                  true
                ))
              )
            ).assume[MinLength[1]])
        )
    case Bfi extends Definition(
          Opcode.assume("bfi"),
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
            Metadata(Vec(
              Metadata.Entry(
                "integer-arithmetic-instructions-bfi".assume[MinLength[1] & NoNul],
                "9.7.1.21. Integer Arithmetic Instructions: bfi".assume[MinLength[1] & NoNul],
                "bfi".assume[MinLength[1] & NoNul],
                "Integer Arithmetic Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "bfi.type  f, a, b, c, d;\n\n.type = { .b32, .b64 };"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(2, 0),
                Target(20),
                Vec(InstructionForm(
                  Opcode.assume("bfi"),
                  Vec(
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Register
                  ),
                  Vec(_root_.scala.collection.immutable.Set(
                    ModifierName.assume("b32"),
                    ModifierName.assume("b64")
                  ).assume[MinLength[1]]),
                  _root_.scala.collection.immutable.Set(),
                  true
                ))
              )
            ).assume[MinLength[1]])
        )
    case Bfind extends Definition(
          Opcode.assume("bfind"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 2) Right(Instruction.Bfind(operands(0), operands(1), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("bfind", Vec(2), operands.size))
          },
          () =>
            Metadata(Vec(
              Metadata.Entry(
                "integer-arithmetic-instructions-bfind".assume[MinLength[1] & NoNul],
                "9.7.1.17. Integer Arithmetic Instructions: bfind".assume[MinLength[1] & NoNul],
                "bfind".assume[MinLength[1] & NoNul],
                "Integer Arithmetic Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "bfind.type           d, a;\nbfind.shiftamt.type  d, a;\n\n.type = { .u32, .u64,\n          .s32, .s64 };"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(2, 0),
                Target(20),
                Vec(
                  InstructionForm(
                    Opcode.assume("bfind"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(_root_.scala.collection.immutable.Set(
                      ModifierName.assume("s32"),
                      ModifierName.assume("s64"),
                      ModifierName.assume("u32"),
                      ModifierName.assume("u64")
                    ).assume[MinLength[1]]),
                    _root_.scala.collection.immutable.Set(),
                    true
                  ),
                  InstructionForm(
                    Opcode.assume("bfind"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("shiftamt")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("s32"),
                        ModifierName.assume("s64"),
                        ModifierName.assume("u32"),
                        ModifierName.assume("u64")
                      ).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(),
                    true
                  )
                )
              )
            ).assume[MinLength[1]])
        )
    case Bmsk extends Definition(
          Opcode.assume("bmsk"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 3) Right(Instruction.Bmsk(operands(0), operands(1), operands(2), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("bmsk", Vec(3), operands.size))
          },
          () =>
            Metadata(Vec(
              Metadata.Entry(
                "integer-arithmetic-instructions-bmsk".assume[MinLength[1] & NoNul],
                "9.7.1.23. Integer Arithmetic Instructions: bmsk".assume[MinLength[1] & NoNul],
                "bmsk".assume[MinLength[1] & NoNul],
                "Integer Arithmetic Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "bmsk.mode.b32  d, a, b;\n\n.mode = { .clamp, .wrap };"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(7, 6),
                Target(70),
                Vec(InstructionForm(
                  Opcode.assume("bmsk"),
                  Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                  Vec(
                    _root_.scala.collection.immutable.Set(
                      ModifierName.assume("clamp"),
                      ModifierName.assume("wrap")
                    ).assume[MinLength[1]],
                    _root_.scala.collection.immutable.Set(ModifierName.assume("b32")).assume[MinLength[1]]
                  ),
                  _root_.scala.collection.immutable.Set(),
                  true
                ))
              )
            ).assume[MinLength[1]])
        )
    case Bra extends Definition(
          Opcode.assume("bra"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 1) Right(Instruction.Bra(operands(0), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("bra", Vec(1), operands.size))
          },
          () =>
            Metadata(Vec(
              Metadata.Entry(
                "control-flow-instructions-bra".assume[MinLength[1] & NoNul],
                "9.7.13.3. Control Flow Instructions: bra".assume[MinLength[1] & NoNul],
                "bra".assume[MinLength[1] & NoNul],
                "Control Flow Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "@p   bra{.uni}  tgt;           // tgt is a label\n     bra{.uni}  tgt;           // unconditional branch"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(1, 0),
                Target(10),
                Vec(InstructionForm(
                  Opcode.assume("bra"),
                  Vec(OperandConstraint.Value),
                  Vec(),
                  _root_.scala.collection.immutable.Set(ModifierName.assume("uni")),
                  true
                ))
              )
            ).assume[MinLength[1]])
        )
    case Brev extends Definition(
          Opcode.assume("brev"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 2) Right(Instruction.Brev(operands(0), operands(1), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("brev", Vec(2), operands.size))
          },
          () =>
            Metadata(Vec(
              Metadata.Entry(
                "integer-arithmetic-instructions-brev".assume[MinLength[1] & NoNul],
                "9.7.1.19. Integer Arithmetic Instructions: brev".assume[MinLength[1] & NoNul],
                "brev".assume[MinLength[1] & NoNul],
                "Integer Arithmetic Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "brev.type  d, a;\n\n.type = { .b32, .b64 };"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(2, 0),
                Target(20),
                Vec(InstructionForm(
                  Opcode.assume("brev"),
                  Vec(OperandConstraint.Register, OperandConstraint.Value),
                  Vec(_root_.scala.collection.immutable.Set(
                    ModifierName.assume("b32"),
                    ModifierName.assume("b64")
                  ).assume[MinLength[1]]),
                  _root_.scala.collection.immutable.Set(),
                  true
                ))
              )
            ).assume[MinLength[1]])
        )
    case Brkpt extends Definition(
          Opcode.assume("brkpt"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 0) Right(Instruction.Brkpt(mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("brkpt", Vec(0), operands.size))
          },
          () =>
            Metadata(Vec(
              Metadata.Entry(
                "miscellaneous-instructions-brkpt".assume[MinLength[1] & NoNul],
                "9.7.20.1. Miscellaneous Instructions: brkpt".assume[MinLength[1] & NoNul],
                "brkpt".assume[MinLength[1] & NoNul],
                "Miscellaneous Instructions".assume[MinLength[1] & NoNul],
                Vec[String]("brkpt;").map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(1, 0),
                Target(11),
                Vec(InstructionForm(
                  Opcode.assume("brkpt"),
                  Vec(),
                  Vec(),
                  _root_.scala.collection.immutable.Set(),
                  true
                ))
              )
            ).assume[MinLength[1]])
        )
    case BrxIdx extends Definition(
          Opcode.assume("brx.idx"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 2) Right(Instruction.BrxIdx(operands(0), operands(1), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("brx.idx", Vec(2), operands.size))
          },
          () =>
            Metadata(Vec(
              Metadata.Entry(
                "control-flow-instructions-brx-idx".assume[MinLength[1] & NoNul],
                "9.7.13.4. Control Flow Instructions: brx.idx".assume[MinLength[1] & NoNul],
                "brx.idx".assume[MinLength[1] & NoNul],
                "Control Flow Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "@p    brx.idx{.uni} index, tlist;\n      brx.idx{.uni} index, tlist;"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(6, 0),
                Target(30),
                Vec(InstructionForm(
                  Opcode.assume("brx.idx"),
                  Vec(OperandConstraint.Value, OperandConstraint.Value),
                  Vec(),
                  _root_.scala.collection.immutable.Set(ModifierName.assume("uni")),
                  true
                ))
              )
            ).assume[MinLength[1]])
        )
    case Call extends Definition(
          Opcode.assume("call"),
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
            Metadata(Vec(
              Metadata.Entry(
                "control-flow-instructions-call".assume[MinLength[1] & NoNul],
                "9.7.13.5. Control Flow Instructions: call".assume[MinLength[1] & NoNul],
                "call".assume[MinLength[1] & NoNul],
                "Control Flow Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "// direct call to named function, func is a symbol\ncall{.uni} (ret-param), func, (param-list);\ncall{.uni} func, (param-list);\ncall{.uni} func;\n\n// indirect call via pointer, with full list of call targets\ncall{.uni} (ret-param), fptr, (param-list), flist;\ncall{.uni} fptr, (param-list), flist;\ncall{.uni} fptr, flist;\n\n// indirect call via pointer, with no knowledge of call targets\ncall{.uni} (ret-param), fptr, (param-list), fproto;\ncall{.uni} fptr, (param-list), fproto;\ncall{.uni} fptr, fproto;"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(1, 0),
                Target(20),
                Vec(
                  InstructionForm(
                    Opcode.assume("call"),
                    Vec(
                      OperandConstraint.Unknown("unparsed"),
                      OperandConstraint.Symbol,
                      OperandConstraint.Unknown("unparsed")
                    ),
                    Vec(),
                    _root_.scala.collection.immutable.Set(ModifierName.assume("uni")),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("call"),
                    Vec(OperandConstraint.Symbol, OperandConstraint.Unknown("unparsed")),
                    Vec(),
                    _root_.scala.collection.immutable.Set(ModifierName.assume("uni")),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("call"),
                    Vec(OperandConstraint.Symbol),
                    Vec(),
                    _root_.scala.collection.immutable.Set(ModifierName.assume("uni")),
                    true
                  ),
                  InstructionForm(
                    Opcode.assume("call"),
                    Vec(
                      OperandConstraint.Unknown("unparsed"),
                      OperandConstraint.Value,
                      OperandConstraint.Unknown("unparsed"),
                      OperandConstraint.Value
                    ),
                    Vec(),
                    _root_.scala.collection.immutable.Set(ModifierName.assume("uni")),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("call"),
                    Vec(OperandConstraint.Value, OperandConstraint.Unknown("unparsed"), OperandConstraint.Value),
                    Vec(),
                    _root_.scala.collection.immutable.Set(ModifierName.assume("uni")),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("call"),
                    Vec(OperandConstraint.Value, OperandConstraint.Value),
                    Vec(),
                    _root_.scala.collection.immutable.Set(ModifierName.assume("uni")),
                    true
                  ),
                  InstructionForm(
                    Opcode.assume("call"),
                    Vec(
                      OperandConstraint.Unknown("unparsed"),
                      OperandConstraint.Value,
                      OperandConstraint.Unknown("unparsed"),
                      OperandConstraint.Value
                    ),
                    Vec(),
                    _root_.scala.collection.immutable.Set(ModifierName.assume("uni")),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("call"),
                    Vec(OperandConstraint.Value, OperandConstraint.Unknown("unparsed"), OperandConstraint.Value),
                    Vec(),
                    _root_.scala.collection.immutable.Set(ModifierName.assume("uni")),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("call"),
                    Vec(OperandConstraint.Value, OperandConstraint.Value),
                    Vec(),
                    _root_.scala.collection.immutable.Set(ModifierName.assume("uni")),
                    true
                  )
                )
              )
            ).assume[MinLength[1]])
        )
    case Clmad extends Definition(
          Opcode.assume("clmad"),
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
            Metadata(Vec(
              Metadata.Entry(
                "integer-arithmetic-instructions-clmad".assume[MinLength[1] & NoNul],
                "9.7.1.5. Integer Arithmetic Instructions: clmad".assume[MinLength[1] & NoNul],
                "clmad".assume[MinLength[1] & NoNul],
                "Integer Arithmetic Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "clmad.mode.u64 d, a, b, c;\n\n.mode = { .hi, .lo };"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(9, 3),
                Target(80),
                Vec(InstructionForm(
                  Opcode.assume("clmad"),
                  Vec(
                    OperandConstraint.Register,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Vec(
                    _root_.scala.collection.immutable.Set(
                      ModifierName.assume("hi"),
                      ModifierName.assume("lo")
                    ).assume[MinLength[1]],
                    _root_.scala.collection.immutable.Set(ModifierName.assume("u64")).assume[MinLength[1]]
                  ),
                  _root_.scala.collection.immutable.Set(),
                  true
                ))
              )
            ).assume[MinLength[1]])
        )
    case ClusterLaunchControlQueryCancel extends Definition(
          Opcode.assume("clusterlaunchcontrol.query_cancel"),
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
            Metadata(Vec(
              Metadata.Entry(
                "parallel-synchronization-and-communication-instructions-clusterlaunchcontrol-query-cancel".assume[
                  MinLength[1] & NoNul
                ],
                "9.7.14.19. Parallel Synchronization and Communication Instructions: clusterlaunchcontrol.query_cancel".assume[
                  MinLength[1] & NoNul
                ],
                "clusterlaunchcontrol.query_cancel".assume[MinLength[1] & NoNul],
                "Parallel Synchronization and Communication Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "clusterlaunchcontrol.query_cancel.is_canceled.pred.b128 pred, try_cancel_response;\n\nclusterlaunchcontrol.query_cancel.get_first_ctaid.v4.b32.b128 {xdim, ydim, zdim, _},  try_cancel_response;\n\nclusterlaunchcontrol.query_cancel.get_first_ctaid{::dimension}.b32.b128 reg, try_cancel_response;\n\n::dimension = { ::x, ::y, ::z };"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(8, 6),
                Target(100),
                Vec(
                  InstructionForm(
                    Opcode.assume("clusterlaunchcontrol.query_cancel"),
                    Vec(OperandConstraint.Value, OperandConstraint.Value),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("is_canceled")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("pred")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("b128")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(),
                    true
                  ),
                  InstructionForm(
                    Opcode.assume("clusterlaunchcontrol.query_cancel"),
                    Vec(OperandConstraint.VectorOperand, OperandConstraint.Value),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume(
                        "get_first_ctaid"
                      )).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("v4")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("b32")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("b128")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(),
                    true
                  ),
                  InstructionForm(
                    Opcode.assume("clusterlaunchcontrol.query_cancel"),
                    Vec(OperandConstraint.Value, OperandConstraint.Value),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume(
                        "get_first_ctaid"
                      )).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("b32")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("b128")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  )
                )
              )
            ).assume[MinLength[1]])
        )
    case ClusterLaunchControlTryCancel extends Definition(
          Opcode.assume("clusterlaunchcontrol.try_cancel"),
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
            Metadata(Vec(
              Metadata.Entry(
                "parallel-synchronization-and-communication-instructions-clusterlaunchcontrol-try-cancel".assume[
                  MinLength[1] & NoNul
                ],
                "9.7.14.18. Parallel Synchronization and Communication Instructions: clusterlaunchcontrol.try_cancel".assume[
                  MinLength[1] & NoNul
                ],
                "clusterlaunchcontrol.try_cancel".assume[MinLength[1] & NoNul],
                "Parallel Synchronization and Communication Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "clusterlaunchcontrol.try_cancel.async{.space}.completion_mechanism{.multicast::cluster::all}.b128 [addr], [mbar];\n\n.completion_mechanism = { .mbarrier::complete_tx::bytes };\n.space = { .shared::cta };"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(8, 6),
                Target(100),
                Vec(InstructionForm(
                  Opcode.assume("clusterlaunchcontrol.try_cancel"),
                  Vec(OperandConstraint.Address, OperandConstraint.Address),
                  Vec(
                    _root_.scala.collection.immutable.Set(ModifierName.assume("async")).assume[MinLength[1]],
                    _root_.scala.collection.immutable.Set(ModifierName.assume(
                      "mbarrier::complete_tx::bytes"
                    )).assume[MinLength[1]],
                    _root_.scala.collection.immutable.Set(ModifierName.assume("b128")).assume[MinLength[1]]
                  ),
                  _root_.scala.collection.immutable.Set(
                    ModifierName.assume("multicast::cluster::all"),
                    ModifierName.assume("shared::cta")
                  ),
                  false
                ))
              )
            ).assume[MinLength[1]])
        )
    case Clz extends Definition(
          Opcode.assume("clz"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 2) Right(Instruction.Clz(operands(0), operands(1), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("clz", Vec(2), operands.size))
          },
          () =>
            Metadata(Vec(
              Metadata.Entry(
                "integer-arithmetic-instructions-clz".assume[MinLength[1] & NoNul],
                "9.7.1.16. Integer Arithmetic Instructions: clz".assume[MinLength[1] & NoNul],
                "clz".assume[MinLength[1] & NoNul],
                "Integer Arithmetic Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "clz.type  d, a;\n\n.type = { .b32, .b64 };"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(2, 0),
                Target(20),
                Vec(InstructionForm(
                  Opcode.assume("clz"),
                  Vec(OperandConstraint.Register, OperandConstraint.Value),
                  Vec(_root_.scala.collection.immutable.Set(
                    ModifierName.assume("b32"),
                    ModifierName.assume("b64")
                  ).assume[MinLength[1]]),
                  _root_.scala.collection.immutable.Set(),
                  true
                ))
              )
            ).assume[MinLength[1]])
        )
    case Cnot extends Definition(
          Opcode.assume("cnot"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 2) Right(Instruction.Cnot(operands(0), operands(1), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("cnot", Vec(2), operands.size))
          },
          () =>
            Metadata(Vec(
              Metadata.Entry(
                "logic-and-shift-instructions-cnot".assume[MinLength[1] & NoNul],
                "9.7.8.5. Logic and Shift Instructions: cnot".assume[MinLength[1] & NoNul],
                "cnot".assume[MinLength[1] & NoNul],
                "Logic and Shift Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "cnot.type d, a;\n\n.type = { .b16, .b32, .b64 };"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(1, 0),
                Target(10),
                Vec(InstructionForm(
                  Opcode.assume("cnot"),
                  Vec(OperandConstraint.Register, OperandConstraint.Value),
                  Vec(_root_.scala.collection.immutable.Set(
                    ModifierName.assume("b16"),
                    ModifierName.assume("b32"),
                    ModifierName.assume("b64")
                  ).assume[MinLength[1]]),
                  _root_.scala.collection.immutable.Set(),
                  true
                ))
              )
            ).assume[MinLength[1]])
        )
    case CopySign extends Definition(
          Opcode.assume("copysign"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 3) Right(Instruction.CopySign(operands(0), operands(1), operands(2), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("copysign", Vec(3), operands.size))
          },
          () =>
            Metadata(Vec(
              Metadata.Entry(
                "floating-point-instructions-copysign".assume[MinLength[1] & NoNul],
                "9.7.3.2. Floating Point Instructions: copysign".assume[MinLength[1] & NoNul],
                "copysign".assume[MinLength[1] & NoNul],
                "Floating Point Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "copysign.type  d, a, b;\n\n.type = { .f32, .f64 };"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(2, 0),
                Target(20),
                Vec(InstructionForm(
                  Opcode.assume("copysign"),
                  Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                  Vec(_root_.scala.collection.immutable.Set(
                    ModifierName.assume("f32"),
                    ModifierName.assume("f64")
                  ).assume[MinLength[1]]),
                  _root_.scala.collection.immutable.Set(),
                  true
                ))
              )
            ).assume[MinLength[1]])
        )
    case Cos extends Definition(
          Opcode.assume("cos"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 2) Right(Instruction.Cos(operands(0), operands(1), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("cos", Vec(2), operands.size))
          },
          () =>
            Metadata(Vec(
              Metadata.Entry(
                "floating-point-instructions-cos".assume[MinLength[1] & NoNul],
                "9.7.3.19. Floating Point Instructions: cos".assume[MinLength[1] & NoNul],
                "cos".assume[MinLength[1] & NoNul],
                "Floating Point Instructions".assume[MinLength[1] & NoNul],
                Vec[String]("cos.approx{.ftz}.f32  d, a;").map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(1, 0),
                Target(10),
                Vec(InstructionForm(
                  Opcode.assume("cos"),
                  Vec(OperandConstraint.Register, OperandConstraint.Value),
                  Vec(
                    _root_.scala.collection.immutable.Set(ModifierName.assume("approx")).assume[MinLength[1]],
                    _root_.scala.collection.immutable.Set(ModifierName.assume("f32")).assume[MinLength[1]]
                  ),
                  _root_.scala.collection.immutable.Set(ModifierName.assume("ftz")),
                  true
                ))
              )
            ).assume[MinLength[1]])
        )
    case CpAsync extends Definition(
          Opcode.assume("cp.async"),
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
            Metadata(Vec(
              Metadata.Entry(
                "data-movement-and-conversion-instructions-cp-async".assume[MinLength[1] & NoNul],
                "9.7.9.26.3.1. Data Movement and Conversion Instructions: cp.async".assume[MinLength[1] & NoNul],
                "cp.async".assume[MinLength[1] & NoNul],
                "Data Movement and Conversion Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "cp.async.ca.shared{::cta}.global{.level::cache_hint}{.level::prefetch_size}\n                         [dst], [src], cp-size{, src-size}{, cache_policy} ;\ncp.async.cg.shared{::cta}.global{.level::cache_hint}{.level::prefetch_size}\n                         [dst], [src], 16{, src-size}{, cache_policy} ;\ncp.async.ca.shared{::cta}.global{.level::cache_hint}{.level::prefetch_size}\n                         [dst], [src], cp-size{, ignore-src}{, cache_policy} ;\ncp.async.cg.shared{::cta}.global{.level::cache_hint}{.level::prefetch_size}\n                         [dst], [src], 16{, ignore-src}{, cache_policy} ;\n\n.level::cache_hint =     { .L2::cache_hint }\n.level::prefetch_size =  { .L2::64B, .L2::128B, .L2::256B }\ncp-size =                { 4, 8, 16 }"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(7, 0),
                Target(30),
                Vec(
                  InstructionForm(
                    Opcode.assume("cp.async"),
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("ca")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("shared")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("global")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName.assume("L2::cache_hint")),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("cp.async"),
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("cg")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("shared")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("global")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName.assume("L2::cache_hint")),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("cp.async"),
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("ca")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("shared")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("global")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName.assume("L2::cache_hint")),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("cp.async"),
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("cg")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("shared")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("global")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName.assume("L2::cache_hint")),
                    false
                  )
                )
              )
            ).assume[MinLength[1]])
        )
    case CpAsyncBulkMBarrier extends Definition(
          Opcode.assume("cp.async.bulk"),
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
            Metadata(Vec(
              Metadata.Entry(
                "data-movement-and-conversion-instructions-cp-async-bulk".assume[MinLength[1] & NoNul],
                "9.7.9.26.4.1. Data Movement and Conversion Instructions: cp.async.bulk".assume[MinLength[1] & NoNul],
                "cp.async.bulk".assume[MinLength[1] & NoNul],
                "Data Movement and Conversion Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "// global -> shared::cta\ncp.async.bulk{.sem}.dst.src.completion_mechanism{.level::cache_hint}{.ignore_oob}\n                      [dstMem], [srcMem], size{, ignoreBytesLeft, ignoreBytesRight}, [mbar] {, cache_policy};\n\n.sem =                  { .weak }\n.dst =                  { .shared::cta }\n.src =                  { .global }\n.completion_mechanism = { .mbarrier::complete_tx::bytes }\n.level::cache_hint =    { .L2::cache_hint }\n\ncp.async.bulk.sem.scope.dst.src.completion_mechanism{.level::cache_hint}{.ignore_oob}.type\n                      [dstMem], [srcMem], size{, ignoreBytesLeft, ignoreBytesRight}, [mbar] {, cache_policy};\n\n.sem =                  { .relaxed }\n.scope =                { .cta, .cluster, .gpu, .sys }\n.dst =                  { .shared::cta }\n.src =                  { .global }\n.completion_mechanism = { .mbarrier::complete_tx::bytes }\n.level::cache_hint =    { .L2::cache_hint }\n.type =                 { .b128 }\n\n\n// global -> shared::cluster\ncp.async.bulk{.sem}.dst.src.completion_mechanism{.multicast}{.level::cache_hint}\n                      [dstMem], [srcMem], size, [mbar] {, ctaMask} {, cache_policy};\n\n.sem =                  { .weak }\n.dst =                  { .shared::cluster }\n.src =                  { .global }\n.completion_mechanism = { .mbarrier::complete_tx::bytes }\n.level::cache_hint =    { .L2::cache_hint }\n.multicast =            { .multicast::cluster }\n\ncp.async.bulk.sem.scope.dst.src.completion_mechanism{.multicast}{.level::cache_hint}.type\n                      [dstMem], [srcMem], size, [mbar] {, ctaMask} {, cache_policy};\n\n.sem =                  { .relaxed }\n.scope =                { .cta, .cluster, .gpu, .sys }\n.dst =                  { .shared::cluster }\n.src =                  { .global }\n.completion_mechanism = { .mbarrier::complete_tx::bytes }\n.level::cache_hint =    { .L2::cache_hint }\n.multicast =            { .multicast::cluster }\n.type =                 { .b128 }\n\n\n// shared::cta -> shared::cluster\ncp.async.bulk{.sem}.dst.src.completion_mechanism [dstMem], [srcMem], size, [mbar];\n\n.sem =                  { .weak }\n.dst =                  { .shared::cluster }\n.src =                  { .shared::cta }\n.completion_mechanism = { .mbarrier::complete_tx::bytes }\n\ncp.async.bulk.sem.scope.dst.src.completion_mechanism.type [dstMem], [srcMem], size, [mbar];\n\n.sem =                  { .relaxed }\n.scope =                { .cta, .cluster }\n.dst =                  { .shared::cluster }\n.src =                  { .shared::cta }\n.completion_mechanism = { .mbarrier::complete_tx::bytes }\n.type =                 { .b128 }\n\n\n// shared::cta -> global\ncp.async.bulk{.sem}.dst.src.completion_mechanism{.level::cache_hint}{.cp_mask}\n                      [dstMem], [srcMem], size {, cache_policy} {, byteMask};\n\n.sem =                  { .weak }\n.dst =                  { .global }\n.src =                  { .shared::cta }\n.completion_mechanism = { .bulk_group }\n.level::cache_hint =    { .L2::cache_hint }\n\ncp.async.bulk.sem.scope.dst.src.completion_mechanism{.level::cache_hint}{.cp_mask}.type\n                      [dstMem], [srcMem], size {, cache_policy} {, byteMask};\n\n.sem =                  { .relaxed }\n.scope =                { .cta, .cluster, .gpu, .sys }\n.dst =                  { .global }\n.src =                  { .shared::cta }\n.completion_mechanism = { .bulk_group }\n.level::cache_hint =    { .L2::cache_hint }\n.type =                 { .b128 }"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(8, 0),
                Target(90),
                Vec(
                  InstructionForm(
                    Opcode.assume("cp.async.bulk"),
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
                      _root_.scala.collection.immutable.Set(ModifierName.assume("global")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("shared::cta")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("bulk_group")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName.assume("L2::cache_hint"),
                      ModifierName.assume("ignore_oob"),
                      ModifierName.assume("relaxed")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("cp.async.bulk"),
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
                      _root_.scala.collection.immutable.Set(ModifierName.assume("relaxed")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("cluster"),
                        ModifierName.assume("cta"),
                        ModifierName.assume("gpu"),
                        ModifierName.assume("sys")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("global")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("shared::cta")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("bulk_group")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("b128")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName.assume("L2::cache_hint"),
                      ModifierName.assume("ignore_oob")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("cp.async.bulk"),
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("global")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("shared::cta")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("bulk_group")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName.assume("L2::cache_hint"),
                      ModifierName.assume("multicast::cluster"),
                      ModifierName.assume("relaxed")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("cp.async.bulk"),
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("relaxed")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("cluster"),
                        ModifierName.assume("cta"),
                        ModifierName.assume("gpu"),
                        ModifierName.assume("sys")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("global")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("shared::cta")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("bulk_group")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("b128")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName.assume("L2::cache_hint"),
                      ModifierName.assume("multicast::cluster")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("cp.async.bulk"),
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Address
                    ),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("global")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("shared::cta")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("bulk_group")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName.assume("relaxed")),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("cp.async.bulk"),
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Address
                    ),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("relaxed")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("cluster"),
                        ModifierName.assume("cta"),
                        ModifierName.assume("gpu"),
                        ModifierName.assume("sys")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("global")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("shared::cta")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("bulk_group")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("b128")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("cp.async.bulk"),
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("global")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("shared::cta")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("bulk_group")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName.assume("L2::cache_hint"),
                      ModifierName.assume("cp_mask"),
                      ModifierName.assume("relaxed")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("cp.async.bulk"),
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("relaxed")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("cluster"),
                        ModifierName.assume("cta"),
                        ModifierName.assume("gpu"),
                        ModifierName.assume("sys")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("global")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("shared::cta")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("bulk_group")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("b128")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName.assume("L2::cache_hint"),
                      ModifierName.assume("cp_mask")
                    ),
                    false
                  )
                )
              )
            ).assume[MinLength[1]])
        )
    case CpAsyncBulkGroup extends Definition(
          Opcode.assume("cp.async.bulk"),
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
            Metadata(Vec(
              Metadata.Entry(
                "data-movement-and-conversion-instructions-cp-async-bulk".assume[MinLength[1] & NoNul],
                "9.7.9.26.4.1. Data Movement and Conversion Instructions: cp.async.bulk".assume[MinLength[1] & NoNul],
                "cp.async.bulk".assume[MinLength[1] & NoNul],
                "Data Movement and Conversion Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "// global -> shared::cta\ncp.async.bulk{.sem}.dst.src.completion_mechanism{.level::cache_hint}{.ignore_oob}\n                      [dstMem], [srcMem], size{, ignoreBytesLeft, ignoreBytesRight}, [mbar] {, cache_policy};\n\n.sem =                  { .weak }\n.dst =                  { .shared::cta }\n.src =                  { .global }\n.completion_mechanism = { .mbarrier::complete_tx::bytes }\n.level::cache_hint =    { .L2::cache_hint }\n\ncp.async.bulk.sem.scope.dst.src.completion_mechanism{.level::cache_hint}{.ignore_oob}.type\n                      [dstMem], [srcMem], size{, ignoreBytesLeft, ignoreBytesRight}, [mbar] {, cache_policy};\n\n.sem =                  { .relaxed }\n.scope =                { .cta, .cluster, .gpu, .sys }\n.dst =                  { .shared::cta }\n.src =                  { .global }\n.completion_mechanism = { .mbarrier::complete_tx::bytes }\n.level::cache_hint =    { .L2::cache_hint }\n.type =                 { .b128 }\n\n\n// global -> shared::cluster\ncp.async.bulk{.sem}.dst.src.completion_mechanism{.multicast}{.level::cache_hint}\n                      [dstMem], [srcMem], size, [mbar] {, ctaMask} {, cache_policy};\n\n.sem =                  { .weak }\n.dst =                  { .shared::cluster }\n.src =                  { .global }\n.completion_mechanism = { .mbarrier::complete_tx::bytes }\n.level::cache_hint =    { .L2::cache_hint }\n.multicast =            { .multicast::cluster }\n\ncp.async.bulk.sem.scope.dst.src.completion_mechanism{.multicast}{.level::cache_hint}.type\n                      [dstMem], [srcMem], size, [mbar] {, ctaMask} {, cache_policy};\n\n.sem =                  { .relaxed }\n.scope =                { .cta, .cluster, .gpu, .sys }\n.dst =                  { .shared::cluster }\n.src =                  { .global }\n.completion_mechanism = { .mbarrier::complete_tx::bytes }\n.level::cache_hint =    { .L2::cache_hint }\n.multicast =            { .multicast::cluster }\n.type =                 { .b128 }\n\n\n// shared::cta -> shared::cluster\ncp.async.bulk{.sem}.dst.src.completion_mechanism [dstMem], [srcMem], size, [mbar];\n\n.sem =                  { .weak }\n.dst =                  { .shared::cluster }\n.src =                  { .shared::cta }\n.completion_mechanism = { .mbarrier::complete_tx::bytes }\n\ncp.async.bulk.sem.scope.dst.src.completion_mechanism.type [dstMem], [srcMem], size, [mbar];\n\n.sem =                  { .relaxed }\n.scope =                { .cta, .cluster }\n.dst =                  { .shared::cluster }\n.src =                  { .shared::cta }\n.completion_mechanism = { .mbarrier::complete_tx::bytes }\n.type =                 { .b128 }\n\n\n// shared::cta -> global\ncp.async.bulk{.sem}.dst.src.completion_mechanism{.level::cache_hint}{.cp_mask}\n                      [dstMem], [srcMem], size {, cache_policy} {, byteMask};\n\n.sem =                  { .weak }\n.dst =                  { .global }\n.src =                  { .shared::cta }\n.completion_mechanism = { .bulk_group }\n.level::cache_hint =    { .L2::cache_hint }\n\ncp.async.bulk.sem.scope.dst.src.completion_mechanism{.level::cache_hint}{.cp_mask}.type\n                      [dstMem], [srcMem], size {, cache_policy} {, byteMask};\n\n.sem =                  { .relaxed }\n.scope =                { .cta, .cluster, .gpu, .sys }\n.dst =                  { .global }\n.src =                  { .shared::cta }\n.completion_mechanism = { .bulk_group }\n.level::cache_hint =    { .L2::cache_hint }\n.type =                 { .b128 }"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(8, 0),
                Target(90),
                Vec(
                  InstructionForm(
                    Opcode.assume("cp.async.bulk"),
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
                      _root_.scala.collection.immutable.Set(ModifierName.assume("global")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("shared::cta")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("bulk_group")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName.assume("L2::cache_hint"),
                      ModifierName.assume("ignore_oob"),
                      ModifierName.assume("relaxed")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("cp.async.bulk"),
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
                      _root_.scala.collection.immutable.Set(ModifierName.assume("relaxed")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("cluster"),
                        ModifierName.assume("cta"),
                        ModifierName.assume("gpu"),
                        ModifierName.assume("sys")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("global")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("shared::cta")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("bulk_group")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("b128")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName.assume("L2::cache_hint"),
                      ModifierName.assume("ignore_oob")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("cp.async.bulk"),
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("global")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("shared::cta")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("bulk_group")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName.assume("L2::cache_hint"),
                      ModifierName.assume("multicast::cluster"),
                      ModifierName.assume("relaxed")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("cp.async.bulk"),
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("relaxed")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("cluster"),
                        ModifierName.assume("cta"),
                        ModifierName.assume("gpu"),
                        ModifierName.assume("sys")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("global")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("shared::cta")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("bulk_group")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("b128")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName.assume("L2::cache_hint"),
                      ModifierName.assume("multicast::cluster")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("cp.async.bulk"),
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Address
                    ),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("global")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("shared::cta")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("bulk_group")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName.assume("relaxed")),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("cp.async.bulk"),
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Address
                    ),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("relaxed")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("cluster"),
                        ModifierName.assume("cta"),
                        ModifierName.assume("gpu"),
                        ModifierName.assume("sys")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("global")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("shared::cta")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("bulk_group")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("b128")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("cp.async.bulk"),
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("global")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("shared::cta")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("bulk_group")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName.assume("L2::cache_hint"),
                      ModifierName.assume("cp_mask"),
                      ModifierName.assume("relaxed")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("cp.async.bulk"),
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("relaxed")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("cluster"),
                        ModifierName.assume("cta"),
                        ModifierName.assume("gpu"),
                        ModifierName.assume("sys")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("global")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("shared::cta")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("bulk_group")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("b128")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName.assume("L2::cache_hint"),
                      ModifierName.assume("cp_mask")
                    ),
                    false
                  )
                )
              )
            ).assume[MinLength[1]])
        )
    case CpAsyncBulkCommitGroup extends Definition(
          Opcode.assume("cp.async.bulk.commit_group"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 0) Right(Instruction.CpAsyncBulkCommitGroup(mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("cp.async.bulk.commit_group", Vec(0), operands.size))
          },
          () =>
            Metadata(Vec(
              Metadata.Entry(
                "data-movement-and-conversion-instructions-cp-async-bulk-commit-group".assume[MinLength[1] & NoNul],
                "9.7.9.26.6.1. Data Movement and Conversion Instructions: cp.async.bulk.commit_group".assume[MinLength[
                  1
                ] & NoNul],
                "cp.async.bulk.commit_group".assume[MinLength[1] & NoNul],
                "Data Movement and Conversion Instructions".assume[MinLength[1] & NoNul],
                Vec[String]("cp.async.bulk.commit_group;").map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(8, 0),
                Target(90),
                Vec(InstructionForm(
                  Opcode.assume("cp.async.bulk.commit_group"),
                  Vec(),
                  Vec(),
                  _root_.scala.collection.immutable.Set(),
                  true
                ))
              )
            ).assume[MinLength[1]])
        )
    case CpAsyncBulkPrefetch extends Definition(
          Opcode.assume("cp.async.bulk.prefetch"),
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
            Metadata(Vec(
              Metadata.Entry(
                "data-movement-and-conversion-instructions-cp-async-bulk-prefetch".assume[MinLength[1] & NoNul],
                "9.7.9.26.4.3. Data Movement and Conversion Instructions: cp.async.bulk.prefetch".assume[MinLength[
                  1
                ] & NoNul],
                "cp.async.bulk.prefetch".assume[MinLength[1] & NoNul],
                "Data Movement and Conversion Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "cp.async.bulk.prefetch.L2.src{.level::cache_hint}   [srcMem], size {, cache_policy};\n\n.src =                { .global }\n.level::cache_hint =  { .L2::cache_hint }"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(8, 0),
                Target(90),
                Vec(InstructionForm(
                  Opcode.assume("cp.async.bulk.prefetch"),
                  Vec(OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value),
                  Vec(
                    _root_.scala.collection.immutable.Set(ModifierName.assume("L2")).assume[MinLength[1]],
                    _root_.scala.collection.immutable.Set(ModifierName.assume("global")).assume[MinLength[1]]
                  ),
                  _root_.scala.collection.immutable.Set(ModifierName.assume("L2::cache_hint")),
                  true
                ))
              )
            ).assume[MinLength[1]])
        )
    case CpAsyncBulkPrefetchTensor extends Definition(
          Opcode.assume("cp.async.bulk.prefetch.tensor"),
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
            Metadata(Vec(
              Metadata.Entry(
                "data-movement-and-conversion-instructions-cp-async-bulk-prefetch-tensor".assume[MinLength[1] & NoNul],
                "9.7.9.26.5.4. Data Movement and Conversion Instructions: cp.async.bulk.prefetch.tensor".assume[
                  MinLength[1] & NoNul
                ],
                "cp.async.bulk.prefetch.tensor".assume[MinLength[1] & NoNul],
                "Data Movement and Conversion Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "// global -> L2:\ncp.async.bulk.prefetch.tensor.dim.L2.src{.load_mode}{.level::cache_hint} [tensorMap, tensorCoords]\n                                                             {, im2colInfo } {, cache_policy}\n\n.src =                { .global }\n.dim =                { .1d, .2d, .3d, .4d, .5d }\n.load_mode =          { .tile, .tile::gather4, .im2col, .im2col::w, .im2col::w::128 }\n.level::cache_hint =  { .L2::cache_hint }"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(8, 0),
                Target(90),
                Vec(InstructionForm(
                  Opcode.assume("cp.async.bulk.prefetch.tensor"),
                  Vec(OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value),
                  Vec(
                    _root_.scala.collection.immutable.Set(ModifierName.assume("L2")).assume[MinLength[1]],
                    _root_.scala.collection.immutable.Set(ModifierName.assume("global")).assume[MinLength[1]]
                  ),
                  _root_.scala.collection.immutable.Set(
                    ModifierName.assume("L2::cache_hint"),
                    ModifierName.assume("im2col"),
                    ModifierName.assume("im2col::w"),
                    ModifierName.assume("tile"),
                    ModifierName.assume("tile::gather4")
                  ),
                  false
                ))
              )
            ).assume[MinLength[1]])
        )
    case CpAsyncBulkTensorMBarrier extends Definition(
          Opcode.assume("cp.async.bulk.tensor"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            val modifierNames = mods.map(_.name)
            if (
              !modifierNames.contains("bulk_group") &&
              (if (modifierNames.exists(_.value.startsWith("im2col")) && modifierNames.contains("multicast::cluster"))
                 operands.size >= 5 && operands.size <= 6
               else if (
                 modifierNames.exists(_.value.startsWith("im2col")) || modifierNames.contains("multicast::cluster")
               ) operands.size >= 4 && operands.size <= 5
               else operands.size == 3 || operands.size == 4)
            ) Right(if (
              modifierNames.exists(_.value.startsWith("im2col")) && modifierNames.contains("multicast::cluster")
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
            else if (modifierNames.exists(_.value.startsWith("im2col"))) Instruction.CpAsyncBulkTensorMBarrier(
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
            Metadata(Vec(
              Metadata.Entry(
                "data-movement-and-conversion-instructions-cp-async-bulk-tensor".assume[MinLength[1] & NoNul],
                "9.7.9.26.5.2. Data Movement and Conversion Instructions: cp.async.bulk.tensor".assume[MinLength[
                  1
                ] & NoNul],
                "cp.async.bulk.tensor".assume[MinLength[1] & NoNul],
                "Data Movement and Conversion Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "// global -> shared::cta\ncp.async.bulk.tensor.dim.dst.src{.load_mode}.completion_mechanism{.cta_group}{.level::cache_hint}\n                                   [dstMem], [tensorMap, tensorCoords], [mbar]{, im2colInfo} {, cache_policy}\n\n.dst =                  { .shared::cta }\n.src =                  { .global }\n.dim =                  { .1d, .2d, .3d, .4d, .5d }\n.completion_mechanism = { .mbarrier::complete_tx::bytes }\n.cta_group =            { .cta_group::1, .cta_group::2 }\n.load_mode =            { .tile, .tile::gather4, .im2col, .im2col::w, .im2col::w::128 }\n.level::cache_hint =    { .L2::cache_hint }\n\n\n// global -> shared::cluster\ncp.async.bulk.tensor.dim.dst.src{.load_mode}.completion_mechanism{.multicast}{.cta_group}{.level::cache_hint}\n                                   [dstMem], [tensorMap, tensorCoords], [mbar]{, im2colInfo}\n                                   {, ctaMask} {, cache_policy}\n\n.dst =                  { .shared::cluster }\n.src =                  { .global }\n.dim =                  { .1d, .2d, .3d, .4d, .5d }\n.completion_mechanism = { .mbarrier::complete_tx::bytes }\n.cta_group =            { .cta_group::1, .cta_group::2 }\n.load_mode =            { .tile, .tile::gather4, .im2col, .im2col::w, .im2col::w::128 }\n.level::cache_hint =    { .L2::cache_hint }\n.multicast =            { .multicast::cluster  }\n\n\n// shared::cta -> global\ncp.async.bulk.tensor.dim.dst.src{.load_mode}.completion_mechanism{.level::cache_hint}\n                                   [tensorMap, tensorCoords], [srcMem] {, cache_policy}\n\n.dst =                  { .global }\n.src =                  { .shared::cta }\n.dim =                  { .1d, .2d, .3d, .4d, .5d }\n.completion_mechanism = { .bulk_group }\n.load_mode =            { .tile, .tile::scatter4, .im2col_no_offs }\n.level::cache_hint =    { .L2::cache_hint }"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(8, 0),
                Target(90),
                Vec(
                  InstructionForm(
                    Opcode.assume("cp.async.bulk.tensor"),
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Address,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("global")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("shared::cta")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("bulk_group")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName.assume("L2::cache_hint"),
                      ModifierName.assume("im2col_no_offs"),
                      ModifierName.assume("tile"),
                      ModifierName.assume("tile::scatter4")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("cp.async.bulk.tensor"),
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Address,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("global")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("shared::cta")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("bulk_group")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName.assume("L2::cache_hint"),
                      ModifierName.assume("im2col_no_offs"),
                      ModifierName.assume("multicast::cluster"),
                      ModifierName.assume("tile"),
                      ModifierName.assume("tile::scatter4")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("cp.async.bulk.tensor"),
                    Vec(OperandConstraint.Address, OperandConstraint.Address, OperandConstraint.Value),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("global")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("shared::cta")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("bulk_group")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName.assume("L2::cache_hint"),
                      ModifierName.assume("im2col_no_offs"),
                      ModifierName.assume("tile"),
                      ModifierName.assume("tile::scatter4")
                    ),
                    false
                  )
                )
              )
            ).assume[MinLength[1]])
        )
    case CpAsyncBulkTensorGroup extends Definition(
          Opcode.assume("cp.async.bulk.tensor"),
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
            Metadata(Vec(
              Metadata.Entry(
                "data-movement-and-conversion-instructions-cp-async-bulk-tensor".assume[MinLength[1] & NoNul],
                "9.7.9.26.5.2. Data Movement and Conversion Instructions: cp.async.bulk.tensor".assume[MinLength[
                  1
                ] & NoNul],
                "cp.async.bulk.tensor".assume[MinLength[1] & NoNul],
                "Data Movement and Conversion Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "// global -> shared::cta\ncp.async.bulk.tensor.dim.dst.src{.load_mode}.completion_mechanism{.cta_group}{.level::cache_hint}\n                                   [dstMem], [tensorMap, tensorCoords], [mbar]{, im2colInfo} {, cache_policy}\n\n.dst =                  { .shared::cta }\n.src =                  { .global }\n.dim =                  { .1d, .2d, .3d, .4d, .5d }\n.completion_mechanism = { .mbarrier::complete_tx::bytes }\n.cta_group =            { .cta_group::1, .cta_group::2 }\n.load_mode =            { .tile, .tile::gather4, .im2col, .im2col::w, .im2col::w::128 }\n.level::cache_hint =    { .L2::cache_hint }\n\n\n// global -> shared::cluster\ncp.async.bulk.tensor.dim.dst.src{.load_mode}.completion_mechanism{.multicast}{.cta_group}{.level::cache_hint}\n                                   [dstMem], [tensorMap, tensorCoords], [mbar]{, im2colInfo}\n                                   {, ctaMask} {, cache_policy}\n\n.dst =                  { .shared::cluster }\n.src =                  { .global }\n.dim =                  { .1d, .2d, .3d, .4d, .5d }\n.completion_mechanism = { .mbarrier::complete_tx::bytes }\n.cta_group =            { .cta_group::1, .cta_group::2 }\n.load_mode =            { .tile, .tile::gather4, .im2col, .im2col::w, .im2col::w::128 }\n.level::cache_hint =    { .L2::cache_hint }\n.multicast =            { .multicast::cluster  }\n\n\n// shared::cta -> global\ncp.async.bulk.tensor.dim.dst.src{.load_mode}.completion_mechanism{.level::cache_hint}\n                                   [tensorMap, tensorCoords], [srcMem] {, cache_policy}\n\n.dst =                  { .global }\n.src =                  { .shared::cta }\n.dim =                  { .1d, .2d, .3d, .4d, .5d }\n.completion_mechanism = { .bulk_group }\n.load_mode =            { .tile, .tile::scatter4, .im2col_no_offs }\n.level::cache_hint =    { .L2::cache_hint }"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(8, 0),
                Target(90),
                Vec(
                  InstructionForm(
                    Opcode.assume("cp.async.bulk.tensor"),
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Address,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("global")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("shared::cta")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("bulk_group")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName.assume("L2::cache_hint"),
                      ModifierName.assume("im2col_no_offs"),
                      ModifierName.assume("tile"),
                      ModifierName.assume("tile::scatter4")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("cp.async.bulk.tensor"),
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Address,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("global")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("shared::cta")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("bulk_group")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName.assume("L2::cache_hint"),
                      ModifierName.assume("im2col_no_offs"),
                      ModifierName.assume("multicast::cluster"),
                      ModifierName.assume("tile"),
                      ModifierName.assume("tile::scatter4")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("cp.async.bulk.tensor"),
                    Vec(OperandConstraint.Address, OperandConstraint.Address, OperandConstraint.Value),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("global")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("shared::cta")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("bulk_group")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName.assume("L2::cache_hint"),
                      ModifierName.assume("im2col_no_offs"),
                      ModifierName.assume("tile"),
                      ModifierName.assume("tile::scatter4")
                    ),
                    false
                  )
                )
              )
            ).assume[MinLength[1]])
        )
    case CpAsyncBulkWaitGroup extends Definition(
          Opcode.assume("cp.async.bulk.wait_group"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 1) Right(Instruction.CpAsyncBulkWaitGroup(operands(0), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("cp.async.bulk.wait_group", Vec(1), operands.size))
          },
          () =>
            Metadata(Vec(
              Metadata.Entry(
                "data-movement-and-conversion-instructions-cp-async-bulk-wait-group".assume[MinLength[1] & NoNul],
                "9.7.9.26.6.2. Data Movement and Conversion Instructions: cp.async.bulk.wait_group".assume[MinLength[
                  1
                ] & NoNul],
                "cp.async.bulk.wait_group".assume[MinLength[1] & NoNul],
                "Data Movement and Conversion Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "cp.async.bulk.wait_group{.read} N;"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(8, 0),
                Target(90),
                Vec(InstructionForm(
                  Opcode.assume("cp.async.bulk.wait_group"),
                  Vec(OperandConstraint.Value),
                  Vec(),
                  _root_.scala.collection.immutable.Set(ModifierName.assume("read")),
                  true
                ))
              )
            ).assume[MinLength[1]])
        )
    case CpAsyncCommitGroup extends Definition(
          Opcode.assume("cp.async.commit_group"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 0) Right(Instruction.CpAsyncCommitGroup(mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("cp.async.commit_group", Vec(0), operands.size))
          },
          () =>
            Metadata(Vec(
              Metadata.Entry(
                "data-movement-and-conversion-instructions-cp-async-commit-group".assume[MinLength[1] & NoNul],
                "9.7.9.26.3.2. Data Movement and Conversion Instructions: cp.async.commit_group".assume[MinLength[
                  1
                ] & NoNul],
                "cp.async.commit_group".assume[MinLength[1] & NoNul],
                "Data Movement and Conversion Instructions".assume[MinLength[1] & NoNul],
                Vec[String]("cp.async.commit_group ;").map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(7, 0),
                Target(80),
                Vec(InstructionForm(
                  Opcode.assume("cp.async.commit_group"),
                  Vec(),
                  Vec(),
                  _root_.scala.collection.immutable.Set(),
                  true
                ))
              )
            ).assume[MinLength[1]])
        )
    case CpAsyncMbarrierArrive extends Definition(
          Opcode.assume("cp.async.mbarrier.arrive"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 1) Right(Instruction.CpAsyncMbarrierArrive(operands(0), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("cp.async.mbarrier.arrive", Vec(1), operands.size))
          },
          () =>
            Metadata(Vec(
              Metadata.Entry(
                "parallel-synchronization-and-communication-instructions-cp-async-mbarrier-arrive".assume[MinLength[
                  1
                ] & NoNul],
                "9.7.14.16.18. Parallel Synchronization and Communication Instructions: cp.async.mbarrier.arrive".assume[
                  MinLength[1] & NoNul
                ],
                "cp.async.mbarrier.arrive".assume[MinLength[1] & NoNul],
                "Parallel Synchronization and Communication Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "cp.async.mbarrier.arrive{.noinc}{.shared{::cta}}.b64 [addr];"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(7, 0),
                Target(80),
                Vec(InstructionForm(
                  Opcode.assume("cp.async.mbarrier.arrive"),
                  Vec(OperandConstraint.Address),
                  Vec(_root_.scala.collection.immutable.Set(ModifierName.assume("b64")).assume[MinLength[1]]),
                  _root_.scala.collection.immutable.Set(ModifierName.assume("noinc"), ModifierName.assume("shared")),
                  false
                ))
              )
            ).assume[MinLength[1]])
        )
    case CpAsyncWaitAll extends Definition(
          Opcode.assume("cp.async.wait_all"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 0) Right(Instruction.CpAsyncWaitAll(mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("cp.async.wait_all", Vec(0), operands.size))
          },
          () =>
            Metadata(Vec(
              Metadata.Entry(
                "data-movement-and-conversion-instructions-cp-async-wait-group".assume[MinLength[1] & NoNul],
                "9.7.9.26.3.3. Data Movement and Conversion Instructions: cp.async.wait_group / cp.async.wait_all".assume[
                  MinLength[1] & NoNul
                ],
                "cp.async.wait_group / cp.async.wait_all".assume[MinLength[1] & NoNul],
                "Data Movement and Conversion Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "cp.async.wait_group N;\ncp.async.wait_all ;"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(7, 0),
                Target(80),
                Vec(InstructionForm(
                  Opcode.assume("cp.async.wait_all"),
                  Vec(),
                  Vec(),
                  _root_.scala.collection.immutable.Set(),
                  true
                ))
              )
            ).assume[MinLength[1]])
        )
    case CpAsyncWaitGroup extends Definition(
          Opcode.assume("cp.async.wait_group"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 1) Right(Instruction.CpAsyncWaitGroup(operands(0), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("cp.async.wait_group", Vec(1), operands.size))
          },
          () =>
            Metadata(Vec(
              Metadata.Entry(
                "data-movement-and-conversion-instructions-cp-async-wait-group".assume[MinLength[1] & NoNul],
                "9.7.9.26.3.3. Data Movement and Conversion Instructions: cp.async.wait_group / cp.async.wait_all".assume[
                  MinLength[1] & NoNul
                ],
                "cp.async.wait_group / cp.async.wait_all".assume[MinLength[1] & NoNul],
                "Data Movement and Conversion Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "cp.async.wait_group N;\ncp.async.wait_all ;"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(7, 0),
                Target(80),
                Vec(InstructionForm(
                  Opcode.assume("cp.async.wait_group"),
                  Vec(OperandConstraint.Value),
                  Vec(),
                  _root_.scala.collection.immutable.Set(),
                  true
                ))
              )
            ).assume[MinLength[1]])
        )
    case CpReduceAsyncBulk extends Definition(
          Opcode.assume("cp.reduce.async.bulk"),
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
            Metadata(Vec(
              Metadata.Entry(
                "data-movement-and-conversion-instructions-cp-reduce-async-bulk".assume[MinLength[1] & NoNul],
                "9.7.9.26.4.2. Data Movement and Conversion Instructions: cp.reduce.async.bulk".assume[MinLength[
                  1
                ] & NoNul],
                "cp.reduce.async.bulk".assume[MinLength[1] & NoNul],
                "Data Movement and Conversion Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "cp.reduce.async.bulk{.sem.scope}.dst.src.completion_mechanism.redOp.type\n              [dstMem], [srcMem], size, [mbar]\n\n.sem =                  { .relaxed }\n.scope =                { .cta, .cluster }\n.dst =                  { .shared::cluster }\n.src =                  { .shared::cta }\n.completion_mechanism = { .mbarrier::complete_tx::bytes }\n.redOp=                 { .and, .or, .xor,\n                          .add, .inc, .dec,\n                          .min, .max }\n.type =                 { .b32, .u32, .s32, .b64, .u64 }\n\n\ncp.reduce.async.bulk{.sem.scope}.dst.src.completion_mechanism{.level::cache_hint}.redOp.type\n               [dstMem], [srcMem], size{, cache_policy}\n\n.sem =                  { .relaxed }\n.scope =                { .cta, .cluster, .gpu, .sys }\n.dst =                  { .global      }\n.src =                  { .shared::cta }\n.completion_mechanism = { .bulk_group }\n.level::cache_hint    = { .L2::cache_hint }\n.redOp=                 { .and, .or, .xor,\n                          .add, .inc, .dec,\n                          .min, .max }\n.type =                 { .f16, .bf16, .b32, .u32, .s32, .b64, .u64, .s64, .f32, .f64 }\n\n\ncp.reduce.async.bulk{.sem.scope}.dst.src.completion_mechanism{.level::cache_hint}.add.noftz.type\n               [dstMem], [srcMem], size{, cache_policy}\n\n.sem =                  { .relaxed }\n.scope =                { .cta, .cluster, .gpu, .sys }\n.dst  =                 { .global }\n.src  =                 { .shared::cta }\n.completion_mechanism = { .bulk_group }\n.type =                 { .f16, .bf16 }"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(8, 0),
                Target(90),
                Vec(
                  InstructionForm(
                    Opcode.assume("cp.reduce.async.bulk"),
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Address
                    ),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("global")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("shared::cta")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("bulk_group")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("add"),
                        ModifierName.assume("and"),
                        ModifierName.assume("dec"),
                        ModifierName.assume("inc"),
                        ModifierName.assume("max"),
                        ModifierName.assume("min"),
                        ModifierName.assume("or"),
                        ModifierName.assume("xor")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("bf16"),
                        ModifierName.assume("f16")
                      ).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName.assume("cluster"),
                      ModifierName.assume("cta"),
                      ModifierName.assume("gpu"),
                      ModifierName.assume("relaxed"),
                      ModifierName.assume("sys")
                    ),
                    true
                  ),
                  InstructionForm(
                    Opcode.assume("cp.reduce.async.bulk"),
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("global")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("shared::cta")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("bulk_group")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("add"),
                        ModifierName.assume("and"),
                        ModifierName.assume("dec"),
                        ModifierName.assume("inc"),
                        ModifierName.assume("max"),
                        ModifierName.assume("min"),
                        ModifierName.assume("or"),
                        ModifierName.assume("xor")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("bf16"),
                        ModifierName.assume("f16")
                      ).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName.assume("L2::cache_hint"),
                      ModifierName.assume("cluster"),
                      ModifierName.assume("cta"),
                      ModifierName.assume("gpu"),
                      ModifierName.assume("relaxed"),
                      ModifierName.assume("sys")
                    ),
                    true
                  ),
                  InstructionForm(
                    Opcode.assume("cp.reduce.async.bulk"),
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("global")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("shared::cta")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("bulk_group")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("add")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("noftz")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("bf16"),
                        ModifierName.assume("f16")
                      ).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName.assume("L2::cache_hint"),
                      ModifierName.assume("cluster"),
                      ModifierName.assume("cta"),
                      ModifierName.assume("gpu"),
                      ModifierName.assume("relaxed"),
                      ModifierName.assume("sys")
                    ),
                    true
                  )
                )
              )
            ).assume[MinLength[1]])
        )
    case CpReduceAsyncBulkTensor extends Definition(
          Opcode.assume("cp.reduce.async.bulk.tensor"),
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
            Metadata(Vec(
              Metadata.Entry(
                "data-movement-and-conversion-instructions-cp-reduce-async-bulk-tensor".assume[MinLength[1] & NoNul],
                "9.7.9.26.5.3. Data Movement and Conversion Instructions: cp.reduce.async.bulk.tensor".assume[MinLength[
                  1
                ] & NoNul],
                "cp.reduce.async.bulk.tensor".assume[MinLength[1] & NoNul],
                "Data Movement and Conversion Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "// shared::cta -> global:\ncp.reduce.async.bulk.tensor.dim.dst.src.redOp{.load_mode}.completion_mechanism{.level::cache_hint}\n                                          [tensorMap, tensorCoords], [srcMem] {,cache_policy}\n\n.dst =                  { .global }\n.src =                  { .shared::cta }\n.dim =                  { .1d, .2d, .3d, .4d, .5d }\n.completion_mechanism = { .bulk_group }\n.load_mode =            { .tile, .im2col_no_offs }\n.redOp =                { .add, .min, .max, .inc, .dec, .and, .or, .xor}"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(8, 0),
                Target(90),
                Vec(InstructionForm(
                  Opcode.assume("cp.reduce.async.bulk.tensor"),
                  Vec(OperandConstraint.Address, OperandConstraint.Address, OperandConstraint.Value),
                  Vec(
                    _root_.scala.collection.immutable.Set(ModifierName.assume("global")).assume[MinLength[1]],
                    _root_.scala.collection.immutable.Set(ModifierName.assume("shared::cta")).assume[MinLength[1]],
                    _root_.scala.collection.immutable.Set(
                      ModifierName.assume("add"),
                      ModifierName.assume("and"),
                      ModifierName.assume("dec"),
                      ModifierName.assume("inc"),
                      ModifierName.assume("max"),
                      ModifierName.assume("min"),
                      ModifierName.assume("or"),
                      ModifierName.assume("xor")
                    ).assume[MinLength[1]],
                    _root_.scala.collection.immutable.Set(ModifierName.assume("bulk_group")).assume[MinLength[1]]
                  ),
                  _root_.scala.collection.immutable.Set(
                    ModifierName.assume("im2col_no_offs"),
                    ModifierName.assume("level::cache_hint"),
                    ModifierName.assume("tile")
                  ),
                  true
                ))
              )
            ).assume[MinLength[1]])
        )
    case CreatePolicy extends Definition(
          Opcode.assume("createpolicy"),
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
            Metadata(Vec(
              Metadata.Entry(
                "data-movement-and-conversion-instructions-createpolicy".assume[MinLength[1] & NoNul],
                "9.7.9.19. Data Movement and Conversion Instructions: createpolicy".assume[MinLength[1] & NoNul],
                "createpolicy".assume[MinLength[1] & NoNul],
                "Data Movement and Conversion Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "// Range-based policy\ncreatepolicy.range{.global}.level::primary_priority{.level::secondary_priority}.b64\n                                   cache_policy, [a], primary-size, total-size;\n\n// Fraction-based policy\ncreatepolicy.fractional.level::primary_priority{.level::secondary_priority}.b64\n                                   cache_policy{, fraction};\n\n// Converting the access property from CUDA APIs\ncreatepolicy.cvt.L2.b64            cache_policy, access-property;\n\n.level::primary_priority =   { .L2::evict_last, .L2::evict_normal,\n                               .L2::evict_first, .L2::evict_unchanged };\n.level::secondary_priority = { .L2::evict_first, .L2::evict_unchanged };"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(7, 4),
                Target(80),
                Vec(
                  InstructionForm(
                    Opcode.assume("createpolicy"),
                    Vec(
                      OperandConstraint.Value,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("range")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("L2::evict_first"),
                        ModifierName.assume("L2::evict_last"),
                        ModifierName.assume("L2::evict_normal"),
                        ModifierName.assume("L2::evict_unchanged")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("b64")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName.assume("L2::evict_first"),
                      ModifierName.assume("L2::evict_unchanged"),
                      ModifierName.assume("global")
                    ),
                    true
                  ),
                  InstructionForm(
                    Opcode.assume("createpolicy"),
                    Vec(OperandConstraint.Value, OperandConstraint.Value),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("fractional")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("L2::evict_first"),
                        ModifierName.assume("L2::evict_last"),
                        ModifierName.assume("L2::evict_normal"),
                        ModifierName.assume("L2::evict_unchanged")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("b64")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName.assume("L2::evict_first"),
                      ModifierName.assume("L2::evict_unchanged")
                    ),
                    true
                  ),
                  InstructionForm(
                    Opcode.assume("createpolicy"),
                    Vec(OperandConstraint.Value, OperandConstraint.Value),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("cvt")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("L2")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("b64")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(),
                    true
                  )
                )
              )
            ).assume[MinLength[1]])
        )
    case Cvt extends Definition(
          Opcode.assume("cvt"),
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
            Metadata(Vec(
              Metadata.Entry(
                "data-movement-and-conversion-instructions-cvt".assume[MinLength[1] & NoNul],
                "9.7.9.22. Data Movement and Conversion Instructions: cvt".assume[MinLength[1] & NoNul],
                "cvt".assume[MinLength[1] & NoNul],
                "Data Movement and Conversion Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "cvt{.irnd}{.ftz}{.sat}.dtype.atype         d, a;  // integer rounding\ncvt{.frnd}{.ftz}{.sat}.dtype.atype         d, a;  // fp rounding\n\ncvt.frnd2{.relu}{.satfinite}.f16.f32       d, a;\ncvt.frnd2{.relu}{.satfinite}.f16x2.f32     d, a, b;\ncvt.rs{.relu}{.satfinite}.f16x2.f32        d, a, b, rbits;\n\ncvt.frnd2{.relu}{.satfinite}.bf16.f32      d, a;\ncvt.frnd2{.relu}{.satfinite}.bf16x2.f32    d, a, b;\ncvt.rs{.relu}{.satfinite}.bf16x2.f32       d, a, b, rbits;\n\ncvt.rna{.satfinite}.tf32.f32               d, a;\ncvt.frnd2{.satfinite}{.relu}.tf32.f32      d, a;\n\ncvt.rn.satfinite{.relu}.f8x2type.f32       d, a, b;\ncvt.rn.satfinite{.relu}.f8x2type.fp16x2    d, a;\ncvt.rn{.relu}.f16x2.f8x2type               d, a;\ncvt.rn{.relu}{.satfinite}{.scaled::n2::ue8m0}.bf16x2.f8x2type d, a{, scale-factor};\ncvt.rs{.relu}.satfinite.f8x4type.f32       d, {a, b, e, f}, rbits;\n\ncvt.rn.satfinite{.relu}.f4x2type.f32        d, a, b;\ncvt.rn.satfinite{.relu}.f4x2type.fp16x2type d, a;\ncvt.rn{.relu}.f16x2.f4x2type                d, a;\ncvt.rn{.relu}{.satfinite}{.scaled::n2::ue8m0}.bf16x2.f4x2type d, a{, scale-factor};\ncvt.rs{.relu}.satfinite.f4x4type.f32        d, {a, b, e, f}, rbits;\n\ncvt.rn.satfinite{.relu}.f6x2type.f32        d, a, b;\ncvt.rn.satfinite{.relu}.f6x2type.fp16x2type d, a;\ncvt.rn{.relu}.f16x2.f6x2type                d, a;\ncvt.rn{.relu}{.satfinite}{.scaled::n2::ue8m0}.bf16x2.f6x2type d, a{, scale-factor};\ncvt.rs{.relu}.satfinite.f6x4type.f32        d, {a, b, e, f}, rbits;\n\ncvt.frnd3{.satfinite}.ue8m0x2.f32          d, a, b;\ncvt.frnd3{.satfinite}.ue8m0x2.bf16x2       d, a;\ncvt.rn.bf16x2.ue8m0x2                      d, a;\n\ncvt.rn.satfinite{.relu}{.scaled::n2::ue8m0}.s2f6x2.f32      d, a, b{, scale-factor};\ncvt.rn.satfinite{.relu}{.scaled::n2::ue8m0}.s2f6x2.bf16x2   d, a{, scale-factor};\ncvt.rn{.satfinite}{.relu}{.scaled::n2::ue8m0}.bf16x2.s2f6x2 d, a{, scale-factor};\n\n.irnd   = { .rni, .rzi, .rmi, .rpi };\n.frnd   = { .rn,  .rz,  .rm,  .rp  };\n.frnd2  = { .rn,  .rz };\n.frnd3  = { .rz,  .rp };\n.dtype = .atype = { .u8,   .u16, .u32, .u64,\n                    .s8,   .s16, .s32, .s64,\n                    .bf16, .f16, .f32, .f64 };\n.f8x2type = { .e4m3x2, .e5m2x2 };\n.f4x2type = { .e2m1x2 };\n.f6x2type = { .e2m3x2, .e3m2x2 };\n.f4x4type = { .e2m1x4 };\n.f8x4type = { .e4m3x4, .e5m2x4 };\n.f6x4type = { .e2m3x4, .e3m2x4 };\n.fp16x2type = { .f16x2, .bf16x2 };"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(1, 0),
                Target(13),
                Vec(
                  InstructionForm(
                    Opcode.assume("cvt"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("dtype")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("bf16"),
                        ModifierName.assume("f16"),
                        ModifierName.assume("f32"),
                        ModifierName.assume("f64"),
                        ModifierName.assume("s16"),
                        ModifierName.assume("s32"),
                        ModifierName.assume("s64"),
                        ModifierName.assume("s8"),
                        ModifierName.assume("u16"),
                        ModifierName.assume("u32"),
                        ModifierName.assume("u64"),
                        ModifierName.assume("u8")
                      ).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName.assume("ftz"),
                      ModifierName.assume("rmi"),
                      ModifierName.assume("rni"),
                      ModifierName.assume("rpi"),
                      ModifierName.assume("rzi"),
                      ModifierName.assume("sat")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("cvt"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("dtype")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("bf16"),
                        ModifierName.assume("f16"),
                        ModifierName.assume("f32"),
                        ModifierName.assume("f64"),
                        ModifierName.assume("s16"),
                        ModifierName.assume("s32"),
                        ModifierName.assume("s64"),
                        ModifierName.assume("s8"),
                        ModifierName.assume("u16"),
                        ModifierName.assume("u32"),
                        ModifierName.assume("u64"),
                        ModifierName.assume("u8")
                      ).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName.assume("ftz"),
                      ModifierName.assume("rm"),
                      ModifierName.assume("rn"),
                      ModifierName.assume("rp"),
                      ModifierName.assume("rz"),
                      ModifierName.assume("sat")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("cvt"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("rn"),
                        ModifierName.assume("rz")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("f16")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("f32")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName.assume("relu"),
                      ModifierName.assume("satfinite")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("cvt"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("rn"),
                        ModifierName.assume("rz")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("f16x2")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("f32")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName.assume("relu"),
                      ModifierName.assume("satfinite")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("cvt"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("rs")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("f16x2")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("f32")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName.assume("relu"),
                      ModifierName.assume("satfinite")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("cvt"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("rn"),
                        ModifierName.assume("rz")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("bf16")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("f32")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName.assume("relu"),
                      ModifierName.assume("satfinite")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("cvt"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("rn"),
                        ModifierName.assume("rz")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("bf16x2")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("f32")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName.assume("relu"),
                      ModifierName.assume("satfinite")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("cvt"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("rs")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("bf16x2")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("f32")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName.assume("relu"),
                      ModifierName.assume("satfinite")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("cvt"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("rna")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("tf32")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("f32")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName.assume("satfinite")),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("cvt"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("rn"),
                        ModifierName.assume("rz")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("tf32")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("f32")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName.assume("relu"),
                      ModifierName.assume("satfinite")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("cvt"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("rn")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("satfinite")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("e4m3x2"),
                        ModifierName.assume("e5m2x2")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("f32")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName.assume("relu")),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("cvt"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("rn")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("satfinite")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("e4m3x2"),
                        ModifierName.assume("e5m2x2")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("fp16x2")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName.assume("relu")),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("cvt"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("rn")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("f16x2")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("e4m3x2"),
                        ModifierName.assume("e5m2x2")
                      ).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName.assume("relu")),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("cvt"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("rn")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("bf16x2")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("e4m3x2"),
                        ModifierName.assume("e5m2x2")
                      ).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName.assume("relu"),
                      ModifierName.assume("satfinite"),
                      ModifierName.assume("scaled::n2::ue8m0")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("cvt"),
                    Vec(OperandConstraint.Register, OperandConstraint.VectorOperand, OperandConstraint.Value),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("rs")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("satfinite")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("e4m3x4"),
                        ModifierName.assume("e5m2x4")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("f32")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName.assume("relu")),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("cvt"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("rn")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("satfinite")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("e2m1x2")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("f32")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName.assume("relu")),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("cvt"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("rn")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("satfinite")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("e2m1x2")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("bf16x2"),
                        ModifierName.assume("f16x2")
                      ).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName.assume("relu")),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("cvt"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("rn")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("f16x2")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("e2m1x2")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName.assume("relu")),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("cvt"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("rn")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("bf16x2")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("e2m1x2")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName.assume("relu"),
                      ModifierName.assume("satfinite"),
                      ModifierName.assume("scaled::n2::ue8m0")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("cvt"),
                    Vec(OperandConstraint.Register, OperandConstraint.VectorOperand, OperandConstraint.Value),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("rs")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("satfinite")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("e2m1x4")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("f32")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName.assume("relu")),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("cvt"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("rn")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("satfinite")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("e2m3x2"),
                        ModifierName.assume("e3m2x2")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("f32")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName.assume("relu")),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("cvt"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("rn")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("satfinite")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("e2m3x2"),
                        ModifierName.assume("e3m2x2")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("bf16x2"),
                        ModifierName.assume("f16x2")
                      ).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName.assume("relu")),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("cvt"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("rn")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("f16x2")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("e2m3x2"),
                        ModifierName.assume("e3m2x2")
                      ).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName.assume("relu")),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("cvt"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("rn")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("bf16x2")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("e2m3x2"),
                        ModifierName.assume("e3m2x2")
                      ).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName.assume("relu"),
                      ModifierName.assume("satfinite"),
                      ModifierName.assume("scaled::n2::ue8m0")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("cvt"),
                    Vec(OperandConstraint.Register, OperandConstraint.VectorOperand, OperandConstraint.Value),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("rs")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("satfinite")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("e2m3x4"),
                        ModifierName.assume("e3m2x4")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("f32")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName.assume("relu")),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("cvt"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("rp"),
                        ModifierName.assume("rz")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("ue8m0x2")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("f32")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName.assume("satfinite")),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("cvt"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("rp"),
                        ModifierName.assume("rz")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("ue8m0x2")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("bf16x2")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName.assume("satfinite")),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("cvt"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("rn")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("bf16x2")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("ue8m0x2")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("cvt"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("rn")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("satfinite")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("s2f6x2")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("f32")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName.assume("relu"),
                      ModifierName.assume("scaled::n2::ue8m0")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("cvt"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("rn")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("satfinite")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("s2f6x2")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("bf16x2")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName.assume("relu"),
                      ModifierName.assume("scaled::n2::ue8m0")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("cvt"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("rn")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("bf16x2")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("s2f6x2")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName.assume("relu"),
                      ModifierName.assume("satfinite"),
                      ModifierName.assume("scaled::n2::ue8m0")
                    ),
                    false
                  )
                )
              )
            ).assume[MinLength[1]])
        )
    case CvtPack extends Definition(
          Opcode.assume("cvt.pack"),
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
            Metadata(Vec(
              Metadata.Entry(
                "data-movement-and-conversion-instructions-cvt-pack".assume[MinLength[1] & NoNul],
                "9.7.9.23. Data Movement and Conversion Instructions: cvt.pack".assume[MinLength[1] & NoNul],
                "cvt.pack".assume[MinLength[1] & NoNul],
                "Data Movement and Conversion Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "cvt.pack.sat.convertType.abType  d, a, b;\n    .convertType  = { .u16, .s16 }\n    .abType       = { .s32 }\n\ncvt.pack.sat.convertType.abType.cType  d, a, b, c;\n    .convertType  = { .u2, .s2, .u4, .s4, .u8, .s8 }\n    .abType       = { .s32 }\n    .cType        = { .b32 }"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(6, 5),
                Target(72),
                Vec(
                  InstructionForm(
                    Opcode.assume("cvt.pack"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("sat")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("s2"),
                        ModifierName.assume("s4"),
                        ModifierName.assume("s8"),
                        ModifierName.assume("u2"),
                        ModifierName.assume("u4"),
                        ModifierName.assume("u8")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("s32")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("cvt.pack"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("sat")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("s2"),
                        ModifierName.assume("s4"),
                        ModifierName.assume("s8"),
                        ModifierName.assume("u2"),
                        ModifierName.assume("u4"),
                        ModifierName.assume("u8")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("s32")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("b32")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  )
                )
              )
            ).assume[MinLength[1]])
        )
    case Cvta extends Definition(
          Opcode.assume("cvta"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 2) Right(Instruction.Cvta(operands(0), operands(1), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("cvta", Vec(2), operands.size))
          },
          () =>
            Metadata(Vec(
              Metadata.Entry(
                "data-movement-and-conversion-instructions-cvta".assume[MinLength[1] & NoNul],
                "9.7.9.21. Data Movement and Conversion Instructions: cvta".assume[MinLength[1] & NoNul],
                "cvta".assume[MinLength[1] & NoNul],
                "Data Movement and Conversion Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "// convert const, global, local, or shared address to generic address\ncvta.space.size  p, a;        // source address in register a\ncvta.space.size  p, var;      // get generic address of var\ncvta.space.size  p, var+imm;  // generic address of var+offset\n\n// convert generic address to const, global, local, or shared address\ncvta.to.space.size  p, a;\n\n.space = { .const, .global, .local, .shared{::cta, ::cluster}, .param{::entry} };\n.size  = { .u32, .u64 };"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(2, 0),
                Target(20),
                Vec(
                  InstructionForm(
                    Opcode.assume("cvta"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("const"),
                        ModifierName.assume("global"),
                        ModifierName.assume("local")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("u32"),
                        ModifierName.assume("u64")
                      ).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("cvta"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("const"),
                        ModifierName.assume("global"),
                        ModifierName.assume("local")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("u32"),
                        ModifierName.assume("u64")
                      ).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("cvta"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("const"),
                        ModifierName.assume("global"),
                        ModifierName.assume("local")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("u32"),
                        ModifierName.assume("u64")
                      ).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("cvta"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("to")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("const"),
                        ModifierName.assume("global"),
                        ModifierName.assume("local")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("u32"),
                        ModifierName.assume("u64")
                      ).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  )
                )
              )
            ).assume[MinLength[1]])
        )
    case Discard extends Definition(
          Opcode.assume("discard"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 2) Right(Instruction.Discard(operands(0), operands(1), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("discard", Vec(2), operands.size))
          },
          () =>
            Metadata(Vec(
              Metadata.Entry(
                "data-movement-and-conversion-instructions-discard".assume[MinLength[1] & NoNul],
                "9.7.9.18. Data Movement and Conversion Instructions: discard".assume[MinLength[1] & NoNul],
                "discard".assume[MinLength[1] & NoNul],
                "Data Movement and Conversion Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "discard{.global}.level  [a], size;\n\n.level = { .L2 };"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(7, 4),
                Target(80),
                Vec(InstructionForm(
                  Opcode.assume("discard"),
                  Vec(OperandConstraint.Address, OperandConstraint.Value),
                  Vec(_root_.scala.collection.immutable.Set(ModifierName.assume("L2")).assume[MinLength[1]]),
                  _root_.scala.collection.immutable.Set(ModifierName.assume("global")),
                  true
                ))
              )
            ).assume[MinLength[1]])
        )
    case Div extends Definition(
          Opcode.assume("div"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 3) Right(Instruction.Div(operands(0), operands(1), operands(2), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("div", Vec(3), operands.size))
          },
          () =>
            Metadata(Vec(
              Metadata.Entry(
                "integer-arithmetic-instructions-div".assume[MinLength[1] & NoNul],
                "9.7.1.9. Integer Arithmetic Instructions: div".assume[MinLength[1] & NoNul],
                "div".assume[MinLength[1] & NoNul],
                "Integer Arithmetic Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "div.type  d, a, b;\n\n.type = { .u16, .u32, .u64,\n          .s16, .s32, .s64 };"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(1, 0),
                Target(10),
                Vec(InstructionForm(
                  Opcode.assume("div"),
                  Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                  Vec(_root_.scala.collection.immutable.Set(
                    ModifierName.assume("s16"),
                    ModifierName.assume("s32"),
                    ModifierName.assume("s64"),
                    ModifierName.assume("u16"),
                    ModifierName.assume("u32"),
                    ModifierName.assume("u64")
                  ).assume[MinLength[1]]),
                  _root_.scala.collection.immutable.Set(),
                  true
                ))
              ),
              Metadata.Entry(
                "floating-point-instructions-div".assume[MinLength[1] & NoNul],
                "9.7.3.8. Floating Point Instructions: div".assume[MinLength[1] & NoNul],
                "div".assume[MinLength[1] & NoNul],
                "Floating Point Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "div.approx{.ftz}.f32  d, a, b;  // fast, approximate divide\ndiv.full{.ftz}.f32    d, a, b;  // full-range approximate divide\ndiv.rnd{.ftz}.f32     d, a, b;  // IEEE 754 compliant rounding\ndiv.rnd.f64           d, a, b;  // IEEE 754 compliant rounding\n\n.rnd = { .rn, .rz, .rm, .rp };"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(1, 0),
                Target(13),
                Vec(
                  InstructionForm(
                    Opcode.assume("div"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("approx")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("f32")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName.assume("ftz")),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("div"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("full")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("f32")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName.assume("ftz")),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("div"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("rm"),
                        ModifierName.assume("rn"),
                        ModifierName.assume("rp"),
                        ModifierName.assume("rz")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("f32")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName.assume("ftz")),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("div"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("rm"),
                        ModifierName.assume("rn"),
                        ModifierName.assume("rp"),
                        ModifierName.assume("rz")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("f64")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  )
                )
              )
            ).assume[MinLength[1]])
        )
    case Dp2a extends Definition(
          Opcode.assume("dp2a"),
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
            Metadata(Vec(
              Metadata.Entry(
                "integer-arithmetic-instructions-dp2a".assume[MinLength[1] & NoNul],
                "9.7.1.25. Integer Arithmetic Instructions: dp2a".assume[MinLength[1] & NoNul],
                "dp2a".assume[MinLength[1] & NoNul],
                "Integer Arithmetic Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "dp2a.mode.atype.btype  d, a, b, c;\n\n.atype = .btype = { .u32, .s32 };\n.mode = { .lo, .hi };"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(5, 0),
                Target(61),
                Vec(InstructionForm(
                  Opcode.assume("dp2a"),
                  Vec(
                    OperandConstraint.Register,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Vec(
                    _root_.scala.collection.immutable.Set(
                      ModifierName.assume("hi"),
                      ModifierName.assume("lo")
                    ).assume[MinLength[1]],
                    _root_.scala.collection.immutable.Set(ModifierName.assume("atype")).assume[MinLength[1]],
                    _root_.scala.collection.immutable.Set(
                      ModifierName.assume("s32"),
                      ModifierName.assume("u32")
                    ).assume[MinLength[1]]
                  ),
                  _root_.scala.collection.immutable.Set(),
                  true
                ))
              )
            ).assume[MinLength[1]])
        )
    case Dp4a extends Definition(
          Opcode.assume("dp4a"),
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
            Metadata(Vec(
              Metadata.Entry(
                "integer-arithmetic-instructions-dp4a".assume[MinLength[1] & NoNul],
                "9.7.1.24. Integer Arithmetic Instructions: dp4a".assume[MinLength[1] & NoNul],
                "dp4a".assume[MinLength[1] & NoNul],
                "Integer Arithmetic Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "dp4a.atype.btype  d, a, b, c;\n\n.atype = .btype = { .u32, .s32 };"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(5, 0),
                Target(61),
                Vec(InstructionForm(
                  Opcode.assume("dp4a"),
                  Vec(
                    OperandConstraint.Register,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Vec(
                    _root_.scala.collection.immutable.Set(ModifierName.assume("atype")).assume[MinLength[1]],
                    _root_.scala.collection.immutable.Set(
                      ModifierName.assume("s32"),
                      ModifierName.assume("u32")
                    ).assume[MinLength[1]]
                  ),
                  _root_.scala.collection.immutable.Set(),
                  true
                ))
              )
            ).assume[MinLength[1]])
        )
    case ElectSync extends Definition(
          Opcode.assume("elect.sync"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 2) Right(Instruction.ElectSync(operands(0), operands(1), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("elect.sync", Vec(2), operands.size))
          },
          () =>
            Metadata(Vec(
              Metadata.Entry(
                "parallel-synchronization-and-communication-instructions-elect-sync".assume[MinLength[1] & NoNul],
                "9.7.14.15. Parallel Synchronization and Communication Instructions: elect.sync".assume[MinLength[
                  1
                ] & NoNul],
                "elect.sync".assume[MinLength[1] & NoNul],
                "Parallel Synchronization and Communication Instructions".assume[MinLength[1] & NoNul],
                Vec[String]("elect.sync d|p, membermask;").map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(8, 0),
                Target(90),
                Vec(InstructionForm(
                  Opcode.assume("elect.sync"),
                  Vec(OperandConstraint.Register, OperandConstraint.Value),
                  Vec(),
                  _root_.scala.collection.immutable.Set(),
                  true
                ))
              )
            ).assume[MinLength[1]])
        )
    case Ex2 extends Definition(
          Opcode.assume("ex2"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 2) Right(Instruction.Ex2(operands(0), operands(1), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("ex2", Vec(2), operands.size))
          },
          () =>
            Metadata(Vec(
              Metadata.Entry(
                "floating-point-instructions-ex2".assume[MinLength[1] & NoNul],
                "9.7.3.21. Floating Point Instructions: ex2".assume[MinLength[1] & NoNul],
                "ex2".assume[MinLength[1] & NoNul],
                "Floating Point Instructions".assume[MinLength[1] & NoNul],
                Vec[String]("ex2.approx{.ftz}.f32  d, a;").map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(1, 0),
                Target(10),
                Vec(InstructionForm(
                  Opcode.assume("ex2"),
                  Vec(OperandConstraint.Register, OperandConstraint.Value),
                  Vec(
                    _root_.scala.collection.immutable.Set(ModifierName.assume("approx")).assume[MinLength[1]],
                    _root_.scala.collection.immutable.Set(ModifierName.assume("f32")).assume[MinLength[1]]
                  ),
                  _root_.scala.collection.immutable.Set(ModifierName.assume("ftz")),
                  true
                ))
              ),
              Metadata.Entry(
                "half-precision-floating-point-instructions-ex2".assume[MinLength[1] & NoNul],
                "9.7.4.10. Half Precision Floating Point Instructions: ex2".assume[MinLength[1] & NoNul],
                "ex2".assume[MinLength[1] & NoNul],
                "Half Precision Floating Point Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "ex2.approx.atype     d, a;\nex2.approx.ftz.btype d, a;\n\n.atype = { .f16,  .f16x2}\n.btype = { .bf16, .bf16x2}"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(7, 0),
                Target(75),
                Vec(
                  InstructionForm(
                    Opcode.assume("ex2"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("approx")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("f16"),
                        ModifierName.assume("f16x2")
                      ).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("ex2"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("approx")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("ftz")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("bf16"),
                        ModifierName.assume("bf16x2")
                      ).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  )
                )
              )
            ).assume[MinLength[1]])
        )
    case Exit extends Definition(
          Opcode.assume("exit"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 0) Right(Instruction.Exit(mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("exit", Vec(0), operands.size))
          },
          () =>
            Metadata(Vec(
              Metadata.Entry(
                "control-flow-instructions-exit".assume[MinLength[1] & NoNul],
                "9.7.13.7. Control Flow Instructions: exit".assume[MinLength[1] & NoNul],
                "exit".assume[MinLength[1] & NoNul],
                "Control Flow Instructions".assume[MinLength[1] & NoNul],
                Vec[String]("exit;").map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(1, 0),
                Target(10),
                Vec(InstructionForm(Opcode.assume("exit"), Vec(), Vec(), _root_.scala.collection.immutable.Set(), true))
              )
            ).assume[MinLength[1]])
        )
    case FabricSubmit extends Definition(
          Opcode.assume("fabric.submit"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 0) Right(Instruction.FabricSubmit(mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("fabric.submit", Vec(0), operands.size))
          },
          () =>
            Metadata(Vec(
              Metadata.Entry(
                "fabric-instructions-submit".assume[MinLength[1] & NoNul],
                "9.7.10.5.5. Fabric Instructions: fabric.submit".assume[MinLength[1] & NoNul],
                "fabric.submit".assume[MinLength[1] & NoNul],
                "Fabric Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "fabric.submit{.submitop};\n\n.submitop = { .op_restrict::fetching }"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(9, 3),
                Target(100),
                Vec(InstructionForm(
                  Opcode.assume("fabric.submit"),
                  Vec(),
                  Vec(),
                  _root_.scala.collection.immutable.Set(ModifierName.assume("op_restrict::fetching")),
                  true
                ))
              )
            ).assume[MinLength[1]])
        )
    case FabricTryGet extends Definition(
          Opcode.assume("fabric.try_get"),
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
            Metadata(Vec(
              Metadata.Entry(
                "fabric-instructions-try-get".assume[MinLength[1] & NoNul],
                "9.7.10.5.1. Fabric Instructions: fabric.try_get".assume[MinLength[1] & NoNul],
                "fabric.try_get".assume[MinLength[1] & NoNul],
                "Fabric Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "fabric.try_get.async.dst.completion_mechanism.sem.scope.b128 [dst], [srcLeId, srcDataOff], size, [bar];\n\n.dst = { .shared::cta }\n.completion_mechanism = { .mbarrier::complete_tx::bytes.mbarrier::report::fabric }\n.sem = { .relaxed }\n.scope = { .sys }"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(9, 3),
                Target(100),
                Vec(InstructionForm(
                  Opcode.assume("fabric.try_get"),
                  Vec(
                    OperandConstraint.Address,
                    OperandConstraint.Address,
                    OperandConstraint.Value,
                    OperandConstraint.Address
                  ),
                  Vec(
                    _root_.scala.collection.immutable.Set(ModifierName.assume("async")).assume[MinLength[1]],
                    _root_.scala.collection.immutable.Set(ModifierName.assume("shared::cta")).assume[MinLength[1]],
                    _root_.scala.collection.immutable.Set(ModifierName.assume("relaxed")).assume[MinLength[1]],
                    _root_.scala.collection.immutable.Set(ModifierName.assume("sys")).assume[MinLength[1]],
                    _root_.scala.collection.immutable.Set(ModifierName.assume("b128")).assume[MinLength[1]]
                  ),
                  _root_.scala.collection.immutable.Set(),
                  true
                ))
              )
            ).assume[MinLength[1]])
        )
    case FabricTryPullred extends Definition(
          Opcode.assume("fabric.try_pullred"),
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
            Metadata(Vec(
              Metadata.Entry(
                "fabric-instructions-try-pullred".assume[MinLength[1] & NoNul],
                "9.7.10.5.4. Fabric Instructions: fabric.try_pullred".assume[MinLength[1] & NoNul],
                "fabric.try_pullred".assume[MinLength[1] & NoNul],
                "Fabric Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "fabric.try_pullred.async.multimem.dst.completion_mechanism.sem.scope.redOpBit.typeBit.sync [dst], [srcLeId, srcDataOff], size, [bar], imm-membermask;\n\nfabric.try_pullred.async.multimem.dst.completion_mechanism.sem.scope.redOpMinMax.typeMinMax.sync [dst], [srcLeId, srcDataOff], size, [bar], imm-membermask;\n\nfabric.try_pullred.async.multimem.dst.completion_mechanism.sem.scope.redOpAdd.typeAdd.sync [dst], [srcLeId, srcDataOff], size, [bar], imm-membermask;\n\nfabric.try_pullred.async.multimem.dst.completion_mechanism.sem.scope.redOpAddAcc16.typeAddAcc16.sync [dst], [srcLeId, srcDataOff], size, [bar], imm-membermask;\n\nfabric.try_pullred.async.multimem.dst.completion_mechanism.sem.scope.redOpAddAcc32.typeAddAcc32.sync [dst], [srcLeId, srcDataOff], size, [bar], imm-membermask;\n\n.dst = { .shared::cta }\n.completion_mechanism = { .mbarrier::complete_tx::bytes.mbarrier::report::fabric }\n.sem = { .relaxed }\n.scope = { .sys }\n.redOpBit = { .and, .xor, .or }\n.typeBit = { .b32, .b64 }\n.redOpMinMax = { .min, .max }\n.typeMinMax = { .u32, .s32, .u64, .s64, .bf16, .f16, .e4m3, .e5m2 }\n.redOpAdd = { .add }\n.typeAdd = { .u32, .u64, .f16, .bf16, .f32 }\n.redOpAddAcc16 = { .add.acc::f16 }\n.typeAddAcc16 = { .e4m3, .e5m2 }\n.redOpAddAcc32 = { .add.acc::f32 }\n.typeAddAcc32 = { .f16, .bf16 }"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(9, 3),
                Target(100),
                Vec(
                  InstructionForm(
                    Opcode.assume("fabric.try_pullred"),
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Address,
                      OperandConstraint.Value
                    ),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("async")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("multimem")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("shared::cta")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("relaxed")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("sys")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("and"),
                        ModifierName.assume("or"),
                        ModifierName.assume("xor")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("b32"),
                        ModifierName.assume("b64")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("sync")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("fabric.try_pullred"),
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Address,
                      OperandConstraint.Value
                    ),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("async")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("multimem")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("shared::cta")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("relaxed")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("sys")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("max"),
                        ModifierName.assume("min")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("bf16"),
                        ModifierName.assume("e4m3"),
                        ModifierName.assume("e5m2"),
                        ModifierName.assume("f16"),
                        ModifierName.assume("s32"),
                        ModifierName.assume("s64"),
                        ModifierName.assume("u32"),
                        ModifierName.assume("u64")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("sync")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("fabric.try_pullred"),
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Address,
                      OperandConstraint.Value
                    ),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("async")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("multimem")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("shared::cta")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("relaxed")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("sys")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("add")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("bf16"),
                        ModifierName.assume("f16"),
                        ModifierName.assume("f32"),
                        ModifierName.assume("u32"),
                        ModifierName.assume("u64")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("sync")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("fabric.try_pullred"),
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Address,
                      OperandConstraint.Value
                    ),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("async")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("multimem")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("shared::cta")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("relaxed")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("sys")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("e4m3"),
                        ModifierName.assume("e5m2")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("sync")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("fabric.try_pullred"),
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Address,
                      OperandConstraint.Value
                    ),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("async")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("multimem")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("shared::cta")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("relaxed")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("sys")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("bf16"),
                        ModifierName.assume("f16")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("sync")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  )
                )
              )
            ).assume[MinLength[1]])
        )
    case FabricTryPut extends Definition(
          Opcode.assume("fabric.try_put"),
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
            Metadata(Vec(
              Metadata.Entry(
                "fabric-instructions-try-put".assume[MinLength[1] & NoNul],
                "9.7.10.5.2. Fabric Instructions: fabric.try_put".assume[MinLength[1] & NoNul],
                "fabric.try_put".assume[MinLength[1] & NoNul],
                "Fabric Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "fabric.try_put.async{.multimem}.src.completion_mechanism0.sem.scope.b128 [dstLeId, dstDataOff], [src], size, [bar];\n\nfabric.try_put.async{.multimem}.src.completion_mechanism0.cp_mask.sem.scope.b128 [dstLeId, dstDataOff], [src], size, [bar], bytemask;\n\nfabric.try_put.async{.multimem}.src.completion_mechanism1.sem.scope.b128 [dstLeId, dstDataOff, dstCounterOff], [src], size, [bar];\n\n.src = { .shared::cta }\n.completion_mechanism0 = { .mbarrier::complete_tx::16B.mbarrier::report::fabric }\n.completion_mechanism1 = { .mbarrier::complete_tx::16B.mbarrier::report::fabric.counted::bytes }\n.sem = { .relaxed }\n.scope = { .sys }"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(9, 3),
                Target(100),
                Vec(
                  InstructionForm(
                    Opcode.assume("fabric.try_put"),
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Address
                    ),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("async")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("shared::cta")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("relaxed")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("sys")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("b128")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName.assume("multimem")),
                    true
                  ),
                  InstructionForm(
                    Opcode.assume("fabric.try_put"),
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Address,
                      OperandConstraint.Value
                    ),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("async")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("shared::cta")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("cp_mask")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("relaxed")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("sys")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("b128")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName.assume("multimem")),
                    true
                  ),
                  InstructionForm(
                    Opcode.assume("fabric.try_put"),
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Address
                    ),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("async")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("shared::cta")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("relaxed")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("sys")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("b128")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName.assume("multimem")),
                    true
                  )
                )
              )
            ).assume[MinLength[1]])
        )
    case FabricTryRed extends Definition(
          Opcode.assume("fabric.try_red"),
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
            Metadata(Vec(
              Metadata.Entry(
                "fabric-instructions-try-red".assume[MinLength[1] & NoNul],
                "9.7.10.5.3. Fabric Instructions: fabric.try_red".assume[MinLength[1] & NoNul],
                "fabric.try_red".assume[MinLength[1] & NoNul],
                "Fabric Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "fabric.try_red.async{.multimem}.src.completion_mechanism0.sem.scope.redOpBit.typeBit [dstLeId, dstDataOff], [src], size, [bar];\n\nfabric.try_red.async{.multimem}.src.completion_mechanism0.sem.scope.redOpMinMax.typeMinMax [dstLeId, dstDataOff], [src], size, [bar];\n\nfabric.try_red.async{.multimem}.src.completion_mechanism0.sem.scope.redOpArith.typeArith [dstLeId, dstDataOff], [src], size, [bar];\n\nfabric.try_red.async{.multimem}.src.completion_mechanism1.sem.scope.redOpBit.typeBit [dstLeId, dstDataOff, dstCounterOff], [src], size, [bar];\n\nfabric.try_red.async{.multimem}.src.completion_mechanism1.sem.scope.redOpMinMax.typeMinMax [dstLeId, dstDataOff, dstCounterOff], [src], size, [bar];\n\nfabric.try_red.async{.multimem}.src.completion_mechanism1.sem.scope.redOpAdd.typeAdd [dstLeId, dstDataOff, dstCounterOff], [src], size, [bar];\n\n.src = { .shared::cta }\n.completion_mechanism0 = { .mbarrier::complete_tx::16B.mbarrier::report::fabric }\n.completion_mechanism1 = { .mbarrier::complete_tx::16B.mbarrier::report::fabric.counted::bytes }\n.sem = { .relaxed }\n.scope = { .sys }\n.redOpBit = { .and, .xor, .or }\n.typeBit = { .b32, .b64 }\n.redOpMinMax = { .min, .max }\n.typeMinMax = { .u32, .s32, .u64, .s64, .f16, .bf16 }\n.redOpArith = { .add }\n.typeArith = { .u32, .u64, .f16, .bf16, .f32, .f64 }\n.redOpAdd = { .add }\n.typeAdd = { .u32, .u64, .f16, .bf16, .f32, .f64 }"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(9, 3),
                Target(100),
                Vec(
                  InstructionForm(
                    Opcode.assume("fabric.try_red"),
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Address
                    ),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("async")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("shared::cta")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("relaxed")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("sys")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("and"),
                        ModifierName.assume("or"),
                        ModifierName.assume("xor")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("b32"),
                        ModifierName.assume("b64")
                      ).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName.assume("multimem")),
                    true
                  ),
                  InstructionForm(
                    Opcode.assume("fabric.try_red"),
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Address
                    ),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("async")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("shared::cta")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("relaxed")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("sys")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("max"),
                        ModifierName.assume("min")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("bf16"),
                        ModifierName.assume("f16"),
                        ModifierName.assume("s32"),
                        ModifierName.assume("s64"),
                        ModifierName.assume("u32"),
                        ModifierName.assume("u64")
                      ).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName.assume("multimem")),
                    true
                  ),
                  InstructionForm(
                    Opcode.assume("fabric.try_red"),
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Address
                    ),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("async")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("shared::cta")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("relaxed")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("sys")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("add")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("bf16"),
                        ModifierName.assume("f16"),
                        ModifierName.assume("f32"),
                        ModifierName.assume("f64"),
                        ModifierName.assume("u32"),
                        ModifierName.assume("u64")
                      ).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName.assume("multimem")),
                    true
                  ),
                  InstructionForm(
                    Opcode.assume("fabric.try_red"),
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Address
                    ),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("async")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("shared::cta")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("relaxed")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("sys")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("and"),
                        ModifierName.assume("or"),
                        ModifierName.assume("xor")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("b32"),
                        ModifierName.assume("b64")
                      ).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName.assume("multimem")),
                    true
                  ),
                  InstructionForm(
                    Opcode.assume("fabric.try_red"),
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Address
                    ),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("async")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("shared::cta")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("relaxed")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("sys")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("max"),
                        ModifierName.assume("min")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("bf16"),
                        ModifierName.assume("f16"),
                        ModifierName.assume("s32"),
                        ModifierName.assume("s64"),
                        ModifierName.assume("u32"),
                        ModifierName.assume("u64")
                      ).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName.assume("multimem")),
                    true
                  ),
                  InstructionForm(
                    Opcode.assume("fabric.try_red"),
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Address
                    ),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("async")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("shared::cta")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("relaxed")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("sys")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("add")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("bf16"),
                        ModifierName.assume("f16"),
                        ModifierName.assume("f32"),
                        ModifierName.assume("f64"),
                        ModifierName.assume("u32"),
                        ModifierName.assume("u64")
                      ).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName.assume("multimem")),
                    true
                  )
                )
              )
            ).assume[MinLength[1]])
        )
    case FabricWait extends Definition(
          Opcode.assume("fabric.wait"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 0) Right(Instruction.FabricWait(mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("fabric.wait", Vec(0), operands.size))
          },
          () =>
            Metadata(Vec(
              Metadata.Entry(
                "fabric-instructions-wait".assume[MinLength[1] & NoNul],
                "9.7.10.5.6. Fabric Instructions: fabric.wait".assume[MinLength[1] & NoNul],
                "fabric.wait".assume[MinLength[1] & NoNul],
                "Fabric Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "fabric.wait.sync_restrict::reads;"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(9, 3),
                Target(100),
                Vec(InstructionForm(
                  Opcode.assume("fabric.wait"),
                  Vec(),
                  Vec(_root_.scala.collection.immutable.Set(ModifierName.assume(
                    "sync_restrict::reads"
                  )).assume[MinLength[1]]),
                  _root_.scala.collection.immutable.Set(),
                  true
                ))
              )
            ).assume[MinLength[1]])
        )
    case Fence extends Definition(
          Opcode.assume("fence"),
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
            Metadata(Vec(
              Metadata.Entry(
                "parallel-synchronization-and-communication-instructions-membar".assume[MinLength[1] & NoNul],
                "9.7.14.4. Parallel Synchronization and Communication Instructions: membar / fence".assume[MinLength[
                  1
                ] & NoNul],
                "membar / fence".assume[MinLength[1] & NoNul],
                "Parallel Synchronization and Communication Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "// Thread fence:\nfence{.sem}.scope;\n\n// Thread fence:\nfence.acquire.sync_restrict::shared::cluster.cluster;\nfence.release.sync_restrict::shared::cta.cluster;\n\n// Operation fence:\nfence.op_restrict.release.cluster;\n\n// Proxy fence (bi-directional):\nfence.proxy.proxykind;\n\n// Proxy fence (uni-directional):\nfence.proxy.to_proxykind::from_proxykind.release.scope;\nfence.proxy.to_proxykind::from_proxykind.acquire.scope  [addr], size;\nfence.proxy.async::generic.acquire.sync_restrict::shared::cluster.cluster;\nfence.proxy.async::generic.release.sync_restrict::shared::cta.cluster;\nfence.proxy.to_proxykind::from_proxykind_fabric.alias.sem_fabric.sys;\n\n// Old style membar:\nmembar.level;\nmembar.proxy.proxykind;\n\n.sem       = { .sc, .acq_rel, .acquire, .release };\n.scope     = { .cta, .cluster, .gpu, .sys };\n.level     = { .cta, .gl, .sys };\n.proxykind = { .alias, .async, .async.global, .async.shared::{cta, cluster} };\n.op_restrict = { .mbarrier_init };\n.to_proxykind::from_proxykind = {.tensormap::generic};\n.to_proxykind::from_proxykind_fabric = {.generic::fabric, .fabric::generic, .fabric::fabric };\n.sem_fabric = { .acquire, .release };"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(1, 0),
                Target(20),
                Vec(
                  InstructionForm(
                    Opcode.assume("fence"),
                    Vec(),
                    Vec(_root_.scala.collection.immutable.Set(
                      ModifierName.assume("cluster"),
                      ModifierName.assume("cta"),
                      ModifierName.assume("gpu"),
                      ModifierName.assume("sys")
                    ).assume[MinLength[1]]),
                    _root_.scala.collection.immutable.Set(
                      ModifierName.assume("acq_rel"),
                      ModifierName.assume("acquire"),
                      ModifierName.assume("release"),
                      ModifierName.assume("sc")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("fence"),
                    Vec(),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("acquire")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume(
                        "sync_restrict::shared::cluster"
                      )).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("cluster")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("fence"),
                    Vec(),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("release")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume(
                        "sync_restrict::shared::cta"
                      )).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("cluster")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("fence"),
                    Vec(),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("mbarrier_init")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("release")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("cluster")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("fence"),
                    Vec(),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("proxy")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("alias"),
                        ModifierName.assume("async"),
                        ModifierName.assume("cluster")
                      ).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("fence"),
                    Vec(),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("proxy")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume(
                        "tensormap::generic"
                      )).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("release")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("cluster"),
                        ModifierName.assume("cta"),
                        ModifierName.assume("gpu"),
                        ModifierName.assume("sys")
                      ).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("fence"),
                    Vec(OperandConstraint.Address, OperandConstraint.Value),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("proxy")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume(
                        "tensormap::generic"
                      )).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("acquire")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("cluster"),
                        ModifierName.assume("cta"),
                        ModifierName.assume("gpu"),
                        ModifierName.assume("sys")
                      ).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("fence"),
                    Vec(),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("proxy")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("async::generic")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("acquire")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume(
                        "sync_restrict::shared::cluster"
                      )).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("cluster")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("fence"),
                    Vec(),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("proxy")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("async::generic")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("release")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume(
                        "sync_restrict::shared::cta"
                      )).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("cluster")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("fence"),
                    Vec(),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("proxy")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("fabric::fabric"),
                        ModifierName.assume("fabric::generic"),
                        ModifierName.assume("generic::fabric")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("alias")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("acquire"),
                        ModifierName.assume("release")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("sys")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  )
                )
              )
            ).assume[MinLength[1]])
        )
    case Fma extends Definition(
          Opcode.assume("fma"),
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
            Metadata(Vec(
              Metadata.Entry(
                "floating-point-instructions-fma".assume[MinLength[1] & NoNul],
                "9.7.3.6. Floating Point Instructions: fma".assume[MinLength[1] & NoNul],
                "fma".assume[MinLength[1] & NoNul],
                "Floating Point Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "fma.rnd{.ftz}{.sat}.f32  d, a, b, c;\nfma.rnd{.ftz}.f32x2      d, a, b, c;\nfma.rnd.f64              d, a, b, c;\n\n.rnd = { .rn, .rz, .rm, .rp };"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(1, 0),
                Target(13),
                Vec(
                  InstructionForm(
                    Opcode.assume("fma"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("rm"),
                        ModifierName.assume("rn"),
                        ModifierName.assume("rp"),
                        ModifierName.assume("rz")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("f32")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName.assume("ftz"), ModifierName.assume("sat")),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("fma"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("rm"),
                        ModifierName.assume("rn"),
                        ModifierName.assume("rp"),
                        ModifierName.assume("rz")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("f32x2")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName.assume("ftz")),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("fma"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("rm"),
                        ModifierName.assume("rn"),
                        ModifierName.assume("rp"),
                        ModifierName.assume("rz")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("f64")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  )
                )
              ),
              Metadata.Entry(
                "half-precision-floating-point-instructions-fma".assume[MinLength[1] & NoNul],
                "9.7.4.4. Half Precision Floating Point Instructions: fma".assume[MinLength[1] & NoNul],
                "fma".assume[MinLength[1] & NoNul],
                "Half Precision Floating Point Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "fma.rnd{.ftz}{.sat}.f16     d, a, b, c;\nfma.rnd{.ftz}{.sat}.f16x2   d, a, b, c;\nfma.rnd{.ftz}.relu.f16      d, a, b, c;\nfma.rnd{.ftz}.relu.f16x2    d, a, b, c;\nfma.rnd{.relu}.bf16         d, a, b, c;\nfma.rnd{.relu}.bf16x2       d, a, b, c;\nfma.rnd.oob.{relu}.type     d, a, b, c;\n\n.rnd = { .rn };"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(4, 2),
                Target(53),
                Vec(
                  InstructionForm(
                    Opcode.assume("fma"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("rn")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("f16")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName.assume("ftz"), ModifierName.assume("sat")),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("fma"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("rn")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("f16x2")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName.assume("ftz"), ModifierName.assume("sat")),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("fma"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("rn")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("relu")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("f16")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName.assume("ftz")),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("fma"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("rn")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("relu")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("f16x2")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName.assume("ftz")),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("fma"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("rn")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("bf16")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName.assume("relu")),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("fma"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("rn")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("bf16x2")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName.assume("relu")),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("fma"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("rn")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("oob")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("type")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  )
                )
              ),
              Metadata.Entry(
                "mixed-precision-floating-point-instructions-fma".assume[MinLength[1] & NoNul],
                "9.7.5.3. Mixed Precision Floating Point Instructions: fma".assume[MinLength[1] & NoNul],
                "fma".assume[MinLength[1] & NoNul],
                "Mixed Precision Floating Point Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "fma.rnd{.sat}.f32.abtype  d, a, b, c;\n\n.abtype = { .f16, .bf16};\n.rnd    = { .rn, .rz, .rm, .rp };"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(1, 0),
                Target(100),
                Vec(InstructionForm(
                  Opcode.assume("fma"),
                  Vec(
                    OperandConstraint.Register,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Vec(
                    _root_.scala.collection.immutable.Set(
                      ModifierName.assume("rm"),
                      ModifierName.assume("rn"),
                      ModifierName.assume("rp"),
                      ModifierName.assume("rz")
                    ).assume[MinLength[1]],
                    _root_.scala.collection.immutable.Set(ModifierName.assume("f32")).assume[MinLength[1]],
                    _root_.scala.collection.immutable.Set(
                      ModifierName.assume("bf16"),
                      ModifierName.assume("f16")
                    ).assume[MinLength[1]]
                  ),
                  _root_.scala.collection.immutable.Set(ModifierName.assume("sat")),
                  true
                ))
              )
            ).assume[MinLength[1]])
        )
    case Fns extends Definition(
          Opcode.assume("fns"),
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
            Metadata(Vec(
              Metadata.Entry(
                "integer-arithmetic-instructions-fns".assume[MinLength[1] & NoNul],
                "9.7.1.18. Integer Arithmetic Instructions: fns".assume[MinLength[1] & NoNul],
                "fns".assume[MinLength[1] & NoNul],
                "Integer Arithmetic Instructions".assume[MinLength[1] & NoNul],
                Vec[String]("fns.b32 d, mask, base, offset;").map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(6, 0),
                Target(30),
                Vec(InstructionForm(
                  Opcode.assume("fns"),
                  Vec(
                    OperandConstraint.Register,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Vec(_root_.scala.collection.immutable.Set(ModifierName.assume("b32")).assume[MinLength[1]]),
                  _root_.scala.collection.immutable.Set(),
                  true
                ))
              )
            ).assume[MinLength[1]])
        )
    case GetCtaRank extends Definition(
          Opcode.assume("getctarank"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 2) Right(Instruction.GetCtaRank(operands(0), operands(1), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("getctarank", Vec(2), operands.size))
          },
          () =>
            Metadata(Vec(
              Metadata.Entry(
                "data-movement-and-conversion-instructions-getctarank".assume[MinLength[1] & NoNul],
                "9.7.9.25. Data Movement and Conversion Instructions: getctarank".assume[MinLength[1] & NoNul],
                "getctarank".assume[MinLength[1] & NoNul],
                "Data Movement and Conversion Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "getctarank{.space}.type d, a;\n\n// Get cta rank from source shared memory address in register a.\ngetctarank.shared::cluster.type d, a;\n\n// Get cta rank from shared memory variable.\ngetctarank.shared::cluster.type d, var;\n\n// Get cta rank from shared memory variable+offset.\ngetctarank.shared::cluster.type d, var + imm;\n\n// Get cta rank from generic address of shared memory variable in register a.\ngetctarank.type d, a;\n\n.space = { .shared::cluster }\n.type  = { .u32, .u64 }"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(7, 8),
                Target(90),
                Vec(
                  InstructionForm(
                    Opcode.assume("getctarank"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(_root_.scala.collection.immutable.Set(
                      ModifierName.assume("u32"),
                      ModifierName.assume("u64")
                    ).assume[MinLength[1]]),
                    _root_.scala.collection.immutable.Set(ModifierName.assume("shared::cluster")),
                    true
                  ),
                  InstructionForm(
                    Opcode.assume("getctarank"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume(
                        "shared::cluster"
                      )).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("u32"),
                        ModifierName.assume("u64")
                      ).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(),
                    true
                  ),
                  InstructionForm(
                    Opcode.assume("getctarank"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume(
                        "shared::cluster"
                      )).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("u32"),
                        ModifierName.assume("u64")
                      ).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(),
                    true
                  ),
                  InstructionForm(
                    Opcode.assume("getctarank"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume(
                        "shared::cluster"
                      )).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("u32"),
                        ModifierName.assume("u64")
                      ).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(),
                    true
                  ),
                  InstructionForm(
                    Opcode.assume("getctarank"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(_root_.scala.collection.immutable.Set(
                      ModifierName.assume("u32"),
                      ModifierName.assume("u64")
                    ).assume[MinLength[1]]),
                    _root_.scala.collection.immutable.Set(),
                    true
                  )
                )
              )
            ).assume[MinLength[1]])
        )
    case GridDependencyControl extends Definition(
          Opcode.assume("griddepcontrol"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 0) Right(Instruction.GridDependencyControl(mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("griddepcontrol", Vec(0), operands.size))
          },
          () =>
            Metadata(Vec(
              Metadata.Entry(
                "parallel-synchronization-and-communication-instructions-griddepcontrol".assume[MinLength[1] & NoNul],
                "9.7.14.14. Parallel Synchronization and Communication Instructions: griddepcontrol".assume[MinLength[
                  1
                ] & NoNul],
                "griddepcontrol".assume[MinLength[1] & NoNul],
                "Parallel Synchronization and Communication Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "griddepcontrol.action;\n\n.action   = { .launch_dependents, .wait }"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(7, 8),
                Target(90),
                Vec(InstructionForm(
                  Opcode.assume("griddepcontrol"),
                  Vec(),
                  Vec(_root_.scala.collection.immutable.Set(
                    ModifierName.assume("launch_dependents"),
                    ModifierName.assume("wait")
                  ).assume[MinLength[1]]),
                  _root_.scala.collection.immutable.Set(),
                  true
                ))
              )
            ).assume[MinLength[1]])
        )
    case IsSpacePredicate extends Definition(
          Opcode.assume("isspacep"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 2) Right(Instruction.IsSpacePredicate(operands(0), operands(1), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("isspacep", Vec(2), operands.size))
          },
          () =>
            Metadata(Vec(
              Metadata.Entry(
                "data-movement-and-conversion-instructions-isspacep".assume[MinLength[1] & NoNul],
                "9.7.9.20. Data Movement and Conversion Instructions: isspacep".assume[MinLength[1] & NoNul],
                "isspacep".assume[MinLength[1] & NoNul],
                "Data Movement and Conversion Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "isspacep.space  p, a;    // result is .pred\n\n.space = { const, .global, .local, .shared{::cta, ::cluster}, .param{::entry} };"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(2, 0),
                Target(20),
                Vec(InstructionForm(
                  Opcode.assume("isspacep"),
                  Vec(OperandConstraint.Register, OperandConstraint.Value),
                  Vec(_root_.scala.collection.immutable.Set(
                    ModifierName.assume("const"),
                    ModifierName.assume("global"),
                    ModifierName.assume("local")
                  ).assume[MinLength[1]]),
                  _root_.scala.collection.immutable.Set(),
                  false
                ))
              )
            ).assume[MinLength[1]])
        )
    case IsTypePredicate extends Definition(
          Opcode.assume("istypep"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 2) Right(Instruction.IsTypePredicate(operands(0), operands(1), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("istypep", Vec(2), operands.size))
          },
          () =>
            Metadata(Vec(
              Metadata.Entry(
                "texture-instructions-istypep".assume[MinLength[1] & NoNul],
                "9.7.11.6. Texture Instructions: istypep".assume[MinLength[1] & NoNul],
                "istypep".assume[MinLength[1] & NoNul],
                "Texture Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "istypep.type   p, a;  // result is .pred\n\n.type = { .texref, .samplerref, .surfref };"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(4, 0),
                Target(30),
                Vec(InstructionForm(
                  Opcode.assume("istypep"),
                  Vec(OperandConstraint.Register, OperandConstraint.Value),
                  Vec(_root_.scala.collection.immutable.Set(
                    ModifierName.assume("samplerref"),
                    ModifierName.assume("surfref"),
                    ModifierName.assume("texref")
                  ).assume[MinLength[1]]),
                  _root_.scala.collection.immutable.Set(),
                  true
                ))
              )
            ).assume[MinLength[1]])
        )
    case Ld extends Definition(
          Opcode.assume("ld"),
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
            Metadata(Vec(
              Metadata.Entry(
                "data-movement-and-conversion-instructions-ld".assume[MinLength[1] & NoNul],
                "9.7.9.8. Data Movement and Conversion Instructions: ld".assume[MinLength[1] & NoNul],
                "ld".assume[MinLength[1] & NoNul],
                "Data Movement and Conversion Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "ld{.weak}{.ss}{.cop}{.level::cache_hint}{.level::prefetch_size}{.vec}.type  d, [a]{.unified}{, cache_policy};\n\nld{.weak}{.ss}{.level1::eviction_priority}{.level2::eviction_priority}{.level::cache_hint}{.level::prefetch_size}{.vec}.type  d, [a]{.unified}{, cache_policy};\n\nld.volatile{.ss}{.level::prefetch_size}{.vec}.type  d, [a];\n\nld.relaxed.scope{.ss}{.level1::eviction_priority}{.level2::eviction_priority}{.level::cache_hint}{.level::prefetch_size}{.vec}.type  d, [a]{, cache_policy};\n\nld.acquire.scope{.ss}{.level1::eviction_priority}{.level2::eviction_priority}{.level::cache_hint}{.level::prefetch_size}{.vec}.type  d, [a]{, cache_policy};\n\nld.mmio.sem.sys{.global}.type  d, [a];\n\n.ss =                       { .const, .global, .local, .param{::entry, ::func}, .shared{::cta, ::cluster} };\n.cop =                      { .ca, .cg, .cs, .lu, .cv };\n.sem =                      { .acquire, .relaxed };\n.level1::eviction_priority = { .L1::evict_normal, .L1::evict_unchanged,\n                               .L1::evict_first, .L1::evict_last, .L1::no_allocate };\n.level2::eviction_priority = {.L2::evict_normal, .L2::evict_first, .L2::evict_last};\n.level::cache_hint =        { .L2::cache_hint };\n.level::prefetch_size =     { .L2::64B, .L2::128B, .L2::256B }\n.scope =                    { .cta, .cluster, .gpu, .sys };\n.vec =                      { .v2, .v4, .v8 };\n.type =                     { .b8, .b16, .b32, .b64, .b128,\n                              .u8, .u16, .u32, .u64,\n                              .s8, .s16, .s32, .s64,\n                              .f32, .f64 };"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(1, 0),
                Target(13),
                Vec(
                  InstructionForm(
                    Opcode.assume("ld"),
                    Vec(OperandConstraint.Register, OperandConstraint.Address, OperandConstraint.Value),
                    Vec(_root_.scala.collection.immutable.Set(
                      ModifierName.assume("b128"),
                      ModifierName.assume("b16"),
                      ModifierName.assume("b32"),
                      ModifierName.assume("b64"),
                      ModifierName.assume("b8"),
                      ModifierName.assume("f32"),
                      ModifierName.assume("f64"),
                      ModifierName.assume("s16"),
                      ModifierName.assume("s32"),
                      ModifierName.assume("s64"),
                      ModifierName.assume("s8"),
                      ModifierName.assume("u16"),
                      ModifierName.assume("u32"),
                      ModifierName.assume("u64"),
                      ModifierName.assume("u8")
                    ).assume[MinLength[1]]),
                    _root_.scala.collection.immutable.Set(
                      ModifierName.assume("L2::cache_hint"),
                      ModifierName.assume("ca"),
                      ModifierName.assume("cg"),
                      ModifierName.assume("const"),
                      ModifierName.assume("cs"),
                      ModifierName.assume("cv"),
                      ModifierName.assume("global"),
                      ModifierName.assume("local"),
                      ModifierName.assume("lu"),
                      ModifierName.assume("v2"),
                      ModifierName.assume("v4"),
                      ModifierName.assume("v8"),
                      ModifierName.assume("weak")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("ld"),
                    Vec(OperandConstraint.Register, OperandConstraint.Address, OperandConstraint.Value),
                    Vec(_root_.scala.collection.immutable.Set(
                      ModifierName.assume("b128"),
                      ModifierName.assume("b16"),
                      ModifierName.assume("b32"),
                      ModifierName.assume("b64"),
                      ModifierName.assume("b8"),
                      ModifierName.assume("f32"),
                      ModifierName.assume("f64"),
                      ModifierName.assume("s16"),
                      ModifierName.assume("s32"),
                      ModifierName.assume("s64"),
                      ModifierName.assume("s8"),
                      ModifierName.assume("u16"),
                      ModifierName.assume("u32"),
                      ModifierName.assume("u64"),
                      ModifierName.assume("u8")
                    ).assume[MinLength[1]]),
                    _root_.scala.collection.immutable.Set(
                      ModifierName.assume("L1::evict_first"),
                      ModifierName.assume("L1::evict_last"),
                      ModifierName.assume("L1::evict_normal"),
                      ModifierName.assume("L1::evict_unchanged"),
                      ModifierName.assume("L1::no_allocate"),
                      ModifierName.assume("L2::cache_hint"),
                      ModifierName.assume("L2::evict_first"),
                      ModifierName.assume("L2::evict_last"),
                      ModifierName.assume("L2::evict_normal"),
                      ModifierName.assume("const"),
                      ModifierName.assume("global"),
                      ModifierName.assume("local"),
                      ModifierName.assume("v2"),
                      ModifierName.assume("v4"),
                      ModifierName.assume("v8"),
                      ModifierName.assume("weak")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("ld"),
                    Vec(OperandConstraint.Register, OperandConstraint.Address),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("volatile")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("b128"),
                        ModifierName.assume("b16"),
                        ModifierName.assume("b32"),
                        ModifierName.assume("b64"),
                        ModifierName.assume("b8"),
                        ModifierName.assume("f32"),
                        ModifierName.assume("f64"),
                        ModifierName.assume("s16"),
                        ModifierName.assume("s32"),
                        ModifierName.assume("s64"),
                        ModifierName.assume("s8"),
                        ModifierName.assume("u16"),
                        ModifierName.assume("u32"),
                        ModifierName.assume("u64"),
                        ModifierName.assume("u8")
                      ).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName.assume("const"),
                      ModifierName.assume("global"),
                      ModifierName.assume("local"),
                      ModifierName.assume("v2"),
                      ModifierName.assume("v4"),
                      ModifierName.assume("v8")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("ld"),
                    Vec(OperandConstraint.Register, OperandConstraint.Address, OperandConstraint.Value),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("relaxed")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("cluster"),
                        ModifierName.assume("cta"),
                        ModifierName.assume("gpu"),
                        ModifierName.assume("sys")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("b128"),
                        ModifierName.assume("b16"),
                        ModifierName.assume("b32"),
                        ModifierName.assume("b64"),
                        ModifierName.assume("b8"),
                        ModifierName.assume("f32"),
                        ModifierName.assume("f64"),
                        ModifierName.assume("s16"),
                        ModifierName.assume("s32"),
                        ModifierName.assume("s64"),
                        ModifierName.assume("s8"),
                        ModifierName.assume("u16"),
                        ModifierName.assume("u32"),
                        ModifierName.assume("u64"),
                        ModifierName.assume("u8")
                      ).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName.assume("L1::evict_first"),
                      ModifierName.assume("L1::evict_last"),
                      ModifierName.assume("L1::evict_normal"),
                      ModifierName.assume("L1::evict_unchanged"),
                      ModifierName.assume("L1::no_allocate"),
                      ModifierName.assume("L2::cache_hint"),
                      ModifierName.assume("L2::evict_first"),
                      ModifierName.assume("L2::evict_last"),
                      ModifierName.assume("L2::evict_normal"),
                      ModifierName.assume("const"),
                      ModifierName.assume("global"),
                      ModifierName.assume("local"),
                      ModifierName.assume("v2"),
                      ModifierName.assume("v4"),
                      ModifierName.assume("v8")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("ld"),
                    Vec(OperandConstraint.Register, OperandConstraint.Address, OperandConstraint.Value),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("acquire")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("cluster"),
                        ModifierName.assume("cta"),
                        ModifierName.assume("gpu"),
                        ModifierName.assume("sys")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("b128"),
                        ModifierName.assume("b16"),
                        ModifierName.assume("b32"),
                        ModifierName.assume("b64"),
                        ModifierName.assume("b8"),
                        ModifierName.assume("f32"),
                        ModifierName.assume("f64"),
                        ModifierName.assume("s16"),
                        ModifierName.assume("s32"),
                        ModifierName.assume("s64"),
                        ModifierName.assume("s8"),
                        ModifierName.assume("u16"),
                        ModifierName.assume("u32"),
                        ModifierName.assume("u64"),
                        ModifierName.assume("u8")
                      ).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName.assume("L1::evict_first"),
                      ModifierName.assume("L1::evict_last"),
                      ModifierName.assume("L1::evict_normal"),
                      ModifierName.assume("L1::evict_unchanged"),
                      ModifierName.assume("L1::no_allocate"),
                      ModifierName.assume("L2::cache_hint"),
                      ModifierName.assume("L2::evict_first"),
                      ModifierName.assume("L2::evict_last"),
                      ModifierName.assume("L2::evict_normal"),
                      ModifierName.assume("const"),
                      ModifierName.assume("global"),
                      ModifierName.assume("local"),
                      ModifierName.assume("v2"),
                      ModifierName.assume("v4"),
                      ModifierName.assume("v8")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("ld"),
                    Vec(OperandConstraint.Register, OperandConstraint.Address),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("mmio")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("acquire"),
                        ModifierName.assume("relaxed")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("sys")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("b128"),
                        ModifierName.assume("b16"),
                        ModifierName.assume("b32"),
                        ModifierName.assume("b64"),
                        ModifierName.assume("b8"),
                        ModifierName.assume("f32"),
                        ModifierName.assume("f64"),
                        ModifierName.assume("s16"),
                        ModifierName.assume("s32"),
                        ModifierName.assume("s64"),
                        ModifierName.assume("s8"),
                        ModifierName.assume("u16"),
                        ModifierName.assume("u32"),
                        ModifierName.assume("u64"),
                        ModifierName.assume("u8")
                      ).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName.assume("global")),
                    false
                  )
                )
              )
            ).assume[MinLength[1]])
        )
    case LdGlobalNc extends Definition(
          Opcode.assume("ld.global.nc"),
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
            Metadata(Vec(
              Metadata.Entry(
                "data-movement-and-conversion-instructions-ld-global-nc".assume[MinLength[1] & NoNul],
                "9.7.9.9. Data Movement and Conversion Instructions: ld.global.nc".assume[MinLength[1] & NoNul],
                "ld.global.nc".assume[MinLength[1] & NoNul],
                "Data Movement and Conversion Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "ld.global{.cop}.nc{.level::cache_hint}{.level::prefetch_size}.type                 d, [a]{, cache_policy};\nld.global{.cop}.nc{.level::cache_hint}{.level::prefetch_size}.vec.type             d, [a]{, cache_policy};\n\nld.global.nc{.level1::eviction_priority}{.level2::eviction_priority}{.level::cache_hint}{.level::prefetch_size}.type      d, [a]{, cache_policy};\nld.global.nc{.level1::eviction_priority}{.level2::eviction_priority}{.level::cache_hint}{.level::prefetch_size}.vec.type  d, [a]{, cache_policy};\n\n.cop  =                     { .ca, .cg, .cs };     // cache operation\n.level1::eviction_priority = { .L1::evict_normal, .L1::evict_unchanged,\n                               .L1::evict_first, .L1::evict_last, .L1::no_allocate};\n.level2::eviction_priority = {.L2::evict_normal, .L2::evict_first, .L2::evict_last};\n.level::cache_hint =        { .L2::cache_hint };\n.level::prefetch_size =     { .L2::64B, .L2::128B, .L2::256B }\n.vec  =                     { .v2, .v4, .v8 };\n.type =                     { .b8, .b16, .b32, .b64, .b128,\n                              .u8, .u16, .u32, .u64,\n                              .s8, .s16, .s32, .s64,\n                              .f32, .f64 };"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(3, 1),
                Target(32),
                Vec(
                  InstructionForm(
                    Opcode.assume("ld.global.nc"),
                    Vec(OperandConstraint.Register, OperandConstraint.Address, OperandConstraint.Value),
                    Vec(_root_.scala.collection.immutable.Set(
                      ModifierName.assume("b128"),
                      ModifierName.assume("b16"),
                      ModifierName.assume("b32"),
                      ModifierName.assume("b64"),
                      ModifierName.assume("b8"),
                      ModifierName.assume("f32"),
                      ModifierName.assume("f64"),
                      ModifierName.assume("s16"),
                      ModifierName.assume("s32"),
                      ModifierName.assume("s64"),
                      ModifierName.assume("s8"),
                      ModifierName.assume("u16"),
                      ModifierName.assume("u32"),
                      ModifierName.assume("u64"),
                      ModifierName.assume("u8")
                    ).assume[MinLength[1]]),
                    _root_.scala.collection.immutable.Set(
                      ModifierName.assume("L2::cache_hint"),
                      ModifierName.assume("ca"),
                      ModifierName.assume("cg"),
                      ModifierName.assume("cs")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("ld.global.nc"),
                    Vec(OperandConstraint.Register, OperandConstraint.Address, OperandConstraint.Value),
                    Vec(
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("v2"),
                        ModifierName.assume("v4"),
                        ModifierName.assume("v8")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("b128"),
                        ModifierName.assume("b16"),
                        ModifierName.assume("b32"),
                        ModifierName.assume("b64"),
                        ModifierName.assume("b8"),
                        ModifierName.assume("f32"),
                        ModifierName.assume("f64"),
                        ModifierName.assume("s16"),
                        ModifierName.assume("s32"),
                        ModifierName.assume("s64"),
                        ModifierName.assume("s8"),
                        ModifierName.assume("u16"),
                        ModifierName.assume("u32"),
                        ModifierName.assume("u64"),
                        ModifierName.assume("u8")
                      ).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName.assume("L2::cache_hint"),
                      ModifierName.assume("ca"),
                      ModifierName.assume("cg"),
                      ModifierName.assume("cs")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("ld.global.nc"),
                    Vec(OperandConstraint.Register, OperandConstraint.Address, OperandConstraint.Value),
                    Vec(_root_.scala.collection.immutable.Set(
                      ModifierName.assume("b128"),
                      ModifierName.assume("b16"),
                      ModifierName.assume("b32"),
                      ModifierName.assume("b64"),
                      ModifierName.assume("b8"),
                      ModifierName.assume("f32"),
                      ModifierName.assume("f64"),
                      ModifierName.assume("s16"),
                      ModifierName.assume("s32"),
                      ModifierName.assume("s64"),
                      ModifierName.assume("s8"),
                      ModifierName.assume("u16"),
                      ModifierName.assume("u32"),
                      ModifierName.assume("u64"),
                      ModifierName.assume("u8")
                    ).assume[MinLength[1]]),
                    _root_.scala.collection.immutable.Set(
                      ModifierName.assume("L1::evict_first"),
                      ModifierName.assume("L1::evict_last"),
                      ModifierName.assume("L1::evict_normal"),
                      ModifierName.assume("L1::evict_unchanged"),
                      ModifierName.assume("L1::no_allocate"),
                      ModifierName.assume("L2::cache_hint"),
                      ModifierName.assume("L2::evict_first"),
                      ModifierName.assume("L2::evict_last"),
                      ModifierName.assume("L2::evict_normal")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("ld.global.nc"),
                    Vec(OperandConstraint.Register, OperandConstraint.Address, OperandConstraint.Value),
                    Vec(
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("v2"),
                        ModifierName.assume("v4"),
                        ModifierName.assume("v8")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("b128"),
                        ModifierName.assume("b16"),
                        ModifierName.assume("b32"),
                        ModifierName.assume("b64"),
                        ModifierName.assume("b8"),
                        ModifierName.assume("f32"),
                        ModifierName.assume("f64"),
                        ModifierName.assume("s16"),
                        ModifierName.assume("s32"),
                        ModifierName.assume("s64"),
                        ModifierName.assume("s8"),
                        ModifierName.assume("u16"),
                        ModifierName.assume("u32"),
                        ModifierName.assume("u64"),
                        ModifierName.assume("u8")
                      ).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName.assume("L1::evict_first"),
                      ModifierName.assume("L1::evict_last"),
                      ModifierName.assume("L1::evict_normal"),
                      ModifierName.assume("L1::evict_unchanged"),
                      ModifierName.assume("L1::no_allocate"),
                      ModifierName.assume("L2::cache_hint"),
                      ModifierName.assume("L2::evict_first"),
                      ModifierName.assume("L2::evict_last"),
                      ModifierName.assume("L2::evict_normal")
                    ),
                    false
                  )
                )
              )
            ).assume[MinLength[1]])
        )
    case Ldu extends Definition(
          Opcode.assume("ldu"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 2) Right(Instruction.Ldu(operands(0), operands(1), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("ldu", Vec(2), operands.size))
          },
          () =>
            Metadata(Vec(
              Metadata.Entry(
                "data-movement-and-conversion-instructions-ldu".assume[MinLength[1] & NoNul],
                "9.7.9.10. Data Movement and Conversion Instructions: ldu".assume[MinLength[1] & NoNul],
                "ldu".assume[MinLength[1] & NoNul],
                "Data Movement and Conversion Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "ldu{.ss}.type      d, [a];       // load from address\nldu{.ss}.vec.type  d, [a];       // vec load from address\n\n.ss   = { .global };             // state space\n.vec  = { .v2, .v4 };\n.type = { .b8, .b16, .b32, .b64, .b128,\n          .u8, .u16, .u32, .u64,\n          .s8, .s16, .s32, .s64,\n                     .f32, .f64 };"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(2, 0),
                Target(13),
                Vec(
                  InstructionForm(
                    Opcode.assume("ldu"),
                    Vec(OperandConstraint.Register, OperandConstraint.Address),
                    Vec(_root_.scala.collection.immutable.Set(
                      ModifierName.assume("b128"),
                      ModifierName.assume("b16"),
                      ModifierName.assume("b32"),
                      ModifierName.assume("b64"),
                      ModifierName.assume("b8"),
                      ModifierName.assume("f32"),
                      ModifierName.assume("f64"),
                      ModifierName.assume("s16"),
                      ModifierName.assume("s32"),
                      ModifierName.assume("s64"),
                      ModifierName.assume("s8"),
                      ModifierName.assume("u16"),
                      ModifierName.assume("u32"),
                      ModifierName.assume("u64"),
                      ModifierName.assume("u8")
                    ).assume[MinLength[1]]),
                    _root_.scala.collection.immutable.Set(ModifierName.assume("global")),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("ldu"),
                    Vec(OperandConstraint.Register, OperandConstraint.Address),
                    Vec(
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("v2"),
                        ModifierName.assume("v4")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("b128"),
                        ModifierName.assume("b16"),
                        ModifierName.assume("b32"),
                        ModifierName.assume("b64"),
                        ModifierName.assume("b8"),
                        ModifierName.assume("f32"),
                        ModifierName.assume("f64"),
                        ModifierName.assume("s16"),
                        ModifierName.assume("s32"),
                        ModifierName.assume("s64"),
                        ModifierName.assume("s8"),
                        ModifierName.assume("u16"),
                        ModifierName.assume("u32"),
                        ModifierName.assume("u64"),
                        ModifierName.assume("u8")
                      ).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName.assume("global")),
                    false
                  )
                )
              )
            ).assume[MinLength[1]])
        )
    case Lg2 extends Definition(
          Opcode.assume("lg2"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 2) Right(Instruction.Lg2(operands(0), operands(1), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("lg2", Vec(2), operands.size))
          },
          () =>
            Metadata(Vec(
              Metadata.Entry(
                "floating-point-instructions-lg2".assume[MinLength[1] & NoNul],
                "9.7.3.20. Floating Point Instructions: lg2".assume[MinLength[1] & NoNul],
                "lg2".assume[MinLength[1] & NoNul],
                "Floating Point Instructions".assume[MinLength[1] & NoNul],
                Vec[String]("lg2.approx{.ftz}.f32  d, a;").map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(1, 0),
                Target(10),
                Vec(InstructionForm(
                  Opcode.assume("lg2"),
                  Vec(OperandConstraint.Register, OperandConstraint.Value),
                  Vec(
                    _root_.scala.collection.immutable.Set(ModifierName.assume("approx")).assume[MinLength[1]],
                    _root_.scala.collection.immutable.Set(ModifierName.assume("f32")).assume[MinLength[1]]
                  ),
                  _root_.scala.collection.immutable.Set(ModifierName.assume("ftz")),
                  true
                ))
              )
            ).assume[MinLength[1]])
        )
    case Lop3 extends Definition(
          Opcode.assume("lop3"),
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
            Metadata(Vec(
              Metadata.Entry(
                "logic-and-shift-instructions-lop3".assume[MinLength[1] & NoNul],
                "9.7.8.6. Logic and Shift Instructions: lop3".assume[MinLength[1] & NoNul],
                "lop3".assume[MinLength[1] & NoNul],
                "Logic and Shift Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "lop3.b32 d, a, b, c, immLut;\nlop3.BoolOp.b32 d|p, a, b, c, immLut, q;\n\n.BoolOp   = { .or , .and };"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(4, 3),
                Target(50),
                Vec(
                  InstructionForm(
                    Opcode.assume("lop3"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(_root_.scala.collection.immutable.Set(ModifierName.assume("b32")).assume[MinLength[1]]),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("lop3"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Register
                    ),
                    Vec(
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("and"),
                        ModifierName.assume("or")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("b32")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  )
                )
              )
            ).assume[MinLength[1]])
        )
    case Mad extends Definition(
          Opcode.assume("mad"),
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
            Metadata(Vec(
              Metadata.Entry(
                "integer-arithmetic-instructions-mad".assume[MinLength[1] & NoNul],
                "9.7.1.4. Integer Arithmetic Instructions: mad".assume[MinLength[1] & NoNul],
                "mad".assume[MinLength[1] & NoNul],
                "Integer Arithmetic Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "mad.mode.type  d, a, b, c;\nmad.hi.sat.s32 d, a, b, c;\n\n.mode = { .hi, .lo, .wide };\n.type = { .u16, .u32, .u64,\n          .s16, .s32, .s64 };"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(1, 0),
                Target(10),
                Vec(
                  InstructionForm(
                    Opcode.assume("mad"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("hi"),
                        ModifierName.assume("lo"),
                        ModifierName.assume("wide")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("s16"),
                        ModifierName.assume("s32"),
                        ModifierName.assume("s64"),
                        ModifierName.assume("u16"),
                        ModifierName.assume("u32"),
                        ModifierName.assume("u64")
                      ).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(),
                    true
                  ),
                  InstructionForm(
                    Opcode.assume("mad"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("hi")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("sat")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("s32")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(),
                    true
                  )
                )
              ),
              Metadata.Entry(
                "floating-point-instructions-mad".assume[MinLength[1] & NoNul],
                "9.7.3.7. Floating Point Instructions: mad".assume[MinLength[1] & NoNul],
                "mad".assume[MinLength[1] & NoNul],
                "Floating Point Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "mad{.ftz}{.sat}.f32      d, a, b, c;    // .target sm_1x\nmad.rnd{.ftz}{.sat}.f32  d, a, b, c;    // .target sm_20\nmad.rnd.f64              d, a, b, c;    // .target sm_13 and higher\n\n.rnd = { .rn, .rz, .rm, .rp };"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(1, 0),
                Target(13),
                Vec(
                  InstructionForm(
                    Opcode.assume("mad"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(_root_.scala.collection.immutable.Set(ModifierName.assume("f32")).assume[MinLength[1]]),
                    _root_.scala.collection.immutable.Set(ModifierName.assume("ftz"), ModifierName.assume("sat")),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("mad"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("rm"),
                        ModifierName.assume("rn"),
                        ModifierName.assume("rp"),
                        ModifierName.assume("rz")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("f32")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName.assume("ftz"), ModifierName.assume("sat")),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("mad"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("rm"),
                        ModifierName.assume("rn"),
                        ModifierName.assume("rp"),
                        ModifierName.assume("rz")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("f64")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  )
                )
              )
            ).assume[MinLength[1]])
        )
    case MadCc extends Definition(
          Opcode.assume("mad.cc"),
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
            Metadata(Vec(
              Metadata.Entry(
                "extended-precision-arithmetic-instructions-mad-cc".assume[MinLength[1] & NoNul],
                "9.7.2.5. Extended-Precision Arithmetic Instructions: mad.cc".assume[MinLength[1] & NoNul],
                "mad.cc".assume[MinLength[1] & NoNul],
                "Extended-Precision Arithmetic Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "mad{.hi,.lo}.cc.type  d, a, b, c;\n\n.type = { .u32, .s32, .u64, .s64 };"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(1, 0),
                Target(20),
                Vec(InstructionForm(
                  Opcode.assume("mad.cc"),
                  Vec(
                    OperandConstraint.Register,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Vec(_root_.scala.collection.immutable.Set(
                    ModifierName.assume("s32"),
                    ModifierName.assume("s64"),
                    ModifierName.assume("u32"),
                    ModifierName.assume("u64")
                  ).assume[MinLength[1]]),
                  _root_.scala.collection.immutable.Set(ModifierName.assume("hi"), ModifierName.assume("lo")),
                  true
                ))
              )
            ).assume[MinLength[1]])
        )
    case Mad24 extends Definition(
          Opcode.assume("mad24"),
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
            Metadata(Vec(
              Metadata.Entry(
                "integer-arithmetic-instructions-mad24".assume[MinLength[1] & NoNul],
                "9.7.1.7. Integer Arithmetic Instructions: mad24".assume[MinLength[1] & NoNul],
                "mad24".assume[MinLength[1] & NoNul],
                "Integer Arithmetic Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "mad24.mode.type  d, a, b, c;\nmad24.hi.sat.s32 d, a, b, c;\n\n.mode = { .hi, .lo };\n.type = { .u32, .s32 };"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(1, 0),
                Target(10),
                Vec(
                  InstructionForm(
                    Opcode.assume("mad24"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("hi"),
                        ModifierName.assume("lo")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("s32"),
                        ModifierName.assume("u32")
                      ).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(),
                    true
                  ),
                  InstructionForm(
                    Opcode.assume("mad24"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("hi")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("sat")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("s32")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(),
                    true
                  )
                )
              )
            ).assume[MinLength[1]])
        )
    case Madc extends Definition(
          Opcode.assume("madc"),
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
            Metadata(Vec(
              Metadata.Entry(
                "extended-precision-arithmetic-instructions-madc".assume[MinLength[1] & NoNul],
                "9.7.2.6. Extended-Precision Arithmetic Instructions: madc".assume[MinLength[1] & NoNul],
                "madc".assume[MinLength[1] & NoNul],
                "Extended-Precision Arithmetic Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "madc{.hi,.lo}{.cc}.type  d, a, b, c;\n\n.type = { .u32, .s32, .u64, .s64 };"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(1, 0),
                Target(20),
                Vec(InstructionForm(
                  Opcode.assume("madc"),
                  Vec(
                    OperandConstraint.Register,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Vec(_root_.scala.collection.immutable.Set(
                    ModifierName.assume("s32"),
                    ModifierName.assume("s64"),
                    ModifierName.assume("u32"),
                    ModifierName.assume("u64")
                  ).assume[MinLength[1]]),
                  _root_.scala.collection.immutable.Set(
                    ModifierName.assume("cc"),
                    ModifierName.assume("hi"),
                    ModifierName.assume("lo")
                  ),
                  true
                ))
              )
            ).assume[MinLength[1]])
        )
    case Mapa extends Definition(
          Opcode.assume("mapa"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 3) Right(Instruction.Mapa(operands(0), operands(1), operands(2), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("mapa", Vec(3), operands.size))
          },
          () =>
            Metadata(Vec(
              Metadata.Entry(
                "data-movement-and-conversion-instructions-mapa".assume[MinLength[1] & NoNul],
                "9.7.9.24. Data Movement and Conversion Instructions: mapa".assume[MinLength[1] & NoNul],
                "mapa".assume[MinLength[1] & NoNul],
                "Data Movement and Conversion Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "mapa{.space}.type          d, a, b;\n\n// Maps shared memory address in register a into CTA b.\nmapa.shared::cluster.type  d, a, b;\n\n// Maps shared memory variable into CTA b.\nmapa.shared::cluster.type  d, sh, b;\n\n// Maps shared memory variable into CTA b.\nmapa.shared::cluster.type  d, sh + imm, b;\n\n// Maps generic address in register a into CTA b.\nmapa.type                  d, a, b;\n\n.space = { .shared::cluster }\n.type  = { .u32, .u64 }"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(7, 8),
                Target(90),
                Vec(
                  InstructionForm(
                    Opcode.assume("mapa"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(_root_.scala.collection.immutable.Set(
                      ModifierName.assume("u32"),
                      ModifierName.assume("u64")
                    ).assume[MinLength[1]]),
                    _root_.scala.collection.immutable.Set(ModifierName.assume("shared::cluster")),
                    true
                  ),
                  InstructionForm(
                    Opcode.assume("mapa"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume(
                        "shared::cluster"
                      )).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("u32"),
                        ModifierName.assume("u64")
                      ).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(),
                    true
                  ),
                  InstructionForm(
                    Opcode.assume("mapa"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume(
                        "shared::cluster"
                      )).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("u32"),
                        ModifierName.assume("u64")
                      ).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(),
                    true
                  ),
                  InstructionForm(
                    Opcode.assume("mapa"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume(
                        "shared::cluster"
                      )).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("u32"),
                        ModifierName.assume("u64")
                      ).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(),
                    true
                  ),
                  InstructionForm(
                    Opcode.assume("mapa"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(_root_.scala.collection.immutable.Set(
                      ModifierName.assume("u32"),
                      ModifierName.assume("u64")
                    ).assume[MinLength[1]]),
                    _root_.scala.collection.immutable.Set(),
                    true
                  )
                )
              )
            ).assume[MinLength[1]])
        )
    case MatchAllSync extends Definition(
          Opcode.assume("match.all.sync"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 3) Right(Instruction.MatchAllSync(operands(0), operands(1), operands(2), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("match.all.sync", Vec(3), operands.size))
          },
          () =>
            Metadata(Vec(
              Metadata.Entry(
                "parallel-synchronization-and-communication-instructions-match-sync".assume[MinLength[1] & NoNul],
                "9.7.14.11. Parallel Synchronization and Communication Instructions: match.sync".assume[MinLength[
                  1
                ] & NoNul],
                "match.sync".assume[MinLength[1] & NoNul],
                "Parallel Synchronization and Communication Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "match.any.sync.type  d, a, membermask;\nmatch.all.sync.type  d[|p], a, membermask;\n\n.type = { .b32, .b64 };"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(6, 0),
                Target(70),
                Vec(InstructionForm(
                  Opcode.assume("match.all.sync"),
                  Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                  Vec(_root_.scala.collection.immutable.Set(
                    ModifierName.assume("b32"),
                    ModifierName.assume("b64")
                  ).assume[MinLength[1]]),
                  _root_.scala.collection.immutable.Set(),
                  true
                ))
              )
            ).assume[MinLength[1]])
        )
    case MatchAnySync extends Definition(
          Opcode.assume("match.any.sync"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 3) Right(Instruction.MatchAnySync(operands(0), operands(1), operands(2), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("match.any.sync", Vec(3), operands.size))
          },
          () =>
            Metadata(Vec(
              Metadata.Entry(
                "parallel-synchronization-and-communication-instructions-match-sync".assume[MinLength[1] & NoNul],
                "9.7.14.11. Parallel Synchronization and Communication Instructions: match.sync".assume[MinLength[
                  1
                ] & NoNul],
                "match.sync".assume[MinLength[1] & NoNul],
                "Parallel Synchronization and Communication Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "match.any.sync.type  d, a, membermask;\nmatch.all.sync.type  d[|p], a, membermask;\n\n.type = { .b32, .b64 };"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(6, 0),
                Target(70),
                Vec(InstructionForm(
                  Opcode.assume("match.any.sync"),
                  Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                  Vec(_root_.scala.collection.immutable.Set(
                    ModifierName.assume("b32"),
                    ModifierName.assume("b64")
                  ).assume[MinLength[1]]),
                  _root_.scala.collection.immutable.Set(),
                  true
                ))
              )
            ).assume[MinLength[1]])
        )
    case Max extends Definition(
          Opcode.assume("max"),
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
            Metadata(Vec(
              Metadata.Entry(
                "integer-arithmetic-instructions-max".assume[MinLength[1] & NoNul],
                "9.7.1.14. Integer Arithmetic Instructions: max".assume[MinLength[1] & NoNul],
                "max".assume[MinLength[1] & NoNul],
                "Integer Arithmetic Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "max.type1         d, a, b;\nmax{.relu}.type2  d, a, b;\n\n.type1 = { .u16, .u32, .u64,\n           .u16x2, .u8x4, .s16, .s64 };\n.type2 = { .s16x2, .s32, .s8x4 };"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(1, 0),
                Target(10),
                Vec(
                  InstructionForm(
                    Opcode.assume("max"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(_root_.scala.collection.immutable.Set(
                      ModifierName.assume("s16"),
                      ModifierName.assume("s64"),
                      ModifierName.assume("u16"),
                      ModifierName.assume("u16x2"),
                      ModifierName.assume("u32"),
                      ModifierName.assume("u64"),
                      ModifierName.assume("u8x4")
                    ).assume[MinLength[1]]),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("max"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(_root_.scala.collection.immutable.Set(
                      ModifierName.assume("s16x2"),
                      ModifierName.assume("s32"),
                      ModifierName.assume("s8x4")
                    ).assume[MinLength[1]]),
                    _root_.scala.collection.immutable.Set(ModifierName.assume("relu")),
                    false
                  )
                )
              ),
              Metadata.Entry(
                "floating-point-instructions-max".assume[MinLength[1] & NoNul],
                "9.7.3.12. Floating Point Instructions: max".assume[MinLength[1] & NoNul],
                "max".assume[MinLength[1] & NoNul],
                "Floating Point Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "max{.ftz}{.NaN}{.xorsign.abs}.f32  d, a, b;\nmax{.ftz}{.NaN}{.abs}.f32          d, a, b, c;\nmax.f64                            d, a, b;"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(1, 0),
                Target(13),
                Vec(
                  InstructionForm(
                    Opcode.assume("max"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(_root_.scala.collection.immutable.Set(ModifierName.assume("f32")).assume[MinLength[1]]),
                    _root_.scala.collection.immutable.Set(
                      ModifierName.assume("NaN"),
                      ModifierName.assume("abs"),
                      ModifierName.assume("ftz"),
                      ModifierName.assume("xorsign")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("max"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(_root_.scala.collection.immutable.Set(ModifierName.assume("f32")).assume[MinLength[1]]),
                    _root_.scala.collection.immutable.Set(
                      ModifierName.assume("NaN"),
                      ModifierName.assume("abs"),
                      ModifierName.assume("ftz")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("max"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(_root_.scala.collection.immutable.Set(ModifierName.assume("f64")).assume[MinLength[1]]),
                    _root_.scala.collection.immutable.Set(),
                    false
                  )
                )
              ),
              Metadata.Entry(
                "half-precision-floating-point-instructions-max".assume[MinLength[1] & NoNul],
                "9.7.4.8. Half Precision Floating Point Instructions: max".assume[MinLength[1] & NoNul],
                "max".assume[MinLength[1] & NoNul],
                "Half Precision Floating Point Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "max{.ftz}{.NaN}{.xorsign.abs}.f16      d, a, b;\nmax{.ftz}{.NaN}{.xorsign.abs}.f16x2    d, a, b;\nmax{.NaN}{.xorsign.abs}.bf16           d, a, b;\nmax{.NaN}{.xorsign.abs}.bf16x2         d, a, b;"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(7, 0),
                Target(80),
                Vec(
                  InstructionForm(
                    Opcode.assume("max"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(_root_.scala.collection.immutable.Set(ModifierName.assume("f16")).assume[MinLength[1]]),
                    _root_.scala.collection.immutable.Set(
                      ModifierName.assume("NaN"),
                      ModifierName.assume("abs"),
                      ModifierName.assume("ftz"),
                      ModifierName.assume("xorsign")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("max"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(_root_.scala.collection.immutable.Set(ModifierName.assume("f16x2")).assume[MinLength[1]]),
                    _root_.scala.collection.immutable.Set(
                      ModifierName.assume("NaN"),
                      ModifierName.assume("abs"),
                      ModifierName.assume("ftz"),
                      ModifierName.assume("xorsign")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("max"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(_root_.scala.collection.immutable.Set(ModifierName.assume("bf16")).assume[MinLength[1]]),
                    _root_.scala.collection.immutable.Set(
                      ModifierName.assume("NaN"),
                      ModifierName.assume("abs"),
                      ModifierName.assume("xorsign")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("max"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(_root_.scala.collection.immutable.Set(ModifierName.assume("bf16x2")).assume[MinLength[1]]),
                    _root_.scala.collection.immutable.Set(
                      ModifierName.assume("NaN"),
                      ModifierName.assume("abs"),
                      ModifierName.assume("xorsign")
                    ),
                    false
                  )
                )
              )
            ).assume[MinLength[1]])
        )
    case MbarrierArrive extends Definition(
          Opcode.assume("mbarrier.arrive"),
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
            Metadata(Vec(
              Metadata.Entry(
                "parallel-synchronization-and-communication-instructions-mbarrier-arrive".assume[MinLength[1] & NoNul],
                "9.7.14.16.16. Parallel Synchronization and Communication Instructions: mbarrier.arrive".assume[
                  MinLength[1] & NoNul
                ],
                "mbarrier.arrive".assume[MinLength[1] & NoNul],
                "Parallel Synchronization and Communication Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "mbarrier.arrive{.sem.scope}{.shared{::cta}}.b64           state, [addr]{, count};\nmbarrier.arrive{.sem.scope}{.shared::cluster}.b64         _, [addr] {,count}\nmbarrier.arrive.expect_tx{.sem.scope}{.shared{::cta}}.b64 state, [addr], txCount;\nmbarrier.arrive.expect_tx{.sem.scope}{.shared::cluster}.b64   _, [addr], txCount;\nmbarrier.arrive.noComplete{.release.cta}{.shared{::cta}}.b64  state, [addr], count;\n\n.sem   = { .release, .relaxed }\n.scope = { .cta, .cluster }"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(7, 0),
                Target(80),
                Vec(
                  InstructionForm(
                    Opcode.assume("mbarrier.arrive"),
                    Vec(OperandConstraint.Value, OperandConstraint.Address, OperandConstraint.Value),
                    Vec(_root_.scala.collection.immutable.Set(ModifierName.assume("b64")).assume[MinLength[1]]),
                    _root_.scala.collection.immutable.Set(
                      ModifierName.assume("cluster"),
                      ModifierName.assume("cta"),
                      ModifierName.assume("relaxed"),
                      ModifierName.assume("release"),
                      ModifierName.assume("shared")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("mbarrier.arrive"),
                    Vec(OperandConstraint.Value, OperandConstraint.Address, OperandConstraint.Value),
                    Vec(_root_.scala.collection.immutable.Set(ModifierName.assume("b64")).assume[MinLength[1]]),
                    _root_.scala.collection.immutable.Set(
                      ModifierName.assume("cluster"),
                      ModifierName.assume("cta"),
                      ModifierName.assume("relaxed"),
                      ModifierName.assume("release"),
                      ModifierName.assume("shared::cluster")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("mbarrier.arrive"),
                    Vec(OperandConstraint.Value, OperandConstraint.Address, OperandConstraint.Value),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("expect_tx")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("b64")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName.assume("cluster"),
                      ModifierName.assume("cta"),
                      ModifierName.assume("relaxed"),
                      ModifierName.assume("release"),
                      ModifierName.assume("shared")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("mbarrier.arrive"),
                    Vec(OperandConstraint.Value, OperandConstraint.Address, OperandConstraint.Value),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("expect_tx")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("b64")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName.assume("cluster"),
                      ModifierName.assume("cta"),
                      ModifierName.assume("relaxed"),
                      ModifierName.assume("release"),
                      ModifierName.assume("shared::cluster")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("mbarrier.arrive"),
                    Vec(OperandConstraint.Value, OperandConstraint.Address, OperandConstraint.Value),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("noComplete")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("b64")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName.assume("cta"),
                      ModifierName.assume("release"),
                      ModifierName.assume("shared")
                    ),
                    false
                  )
                )
              )
            ).assume[MinLength[1]])
        )
    case MbarrierArriveDrop extends Definition(
          Opcode.assume("mbarrier.arrive_drop"),
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
            Metadata(Vec(
              Metadata.Entry(
                "parallel-synchronization-and-communication-instructions-mbarrier-arrive-drop".assume[MinLength[
                  1
                ] & NoNul],
                "9.7.14.16.17. Parallel Synchronization and Communication Instructions: mbarrier.arrive_drop".assume[
                  MinLength[1] & NoNul
                ],
                "mbarrier.arrive_drop".assume[MinLength[1] & NoNul],
                "Parallel Synchronization and Communication Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "mbarrier.arrive_drop{.sem.scope}{.shared{::cta}}.b64              state, [addr] {, count};\nmbarrier.arrive_drop{.sem.scope}{.shared::cluster}.b64            _,     [addr] {, count};\nmbarrier.arrive_drop.expect_tx{.sem.scope}{.shared{::cta}}.b64    state, [addr], tx_count;\nmbarrier.arrive_drop.expect_tx{.sem.scope}{.shared::cluster}.b64  _,     [addr], tx_count;\nmbarrier.arrive_drop.noComplete{.release.cta}{.shared{::cta}}.b64 state, [addr], count;\n\n.sem   = { .release, .relaxed }\n.scope = { .cta, .cluster }"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(7, 0),
                Target(80),
                Vec(
                  InstructionForm(
                    Opcode.assume("mbarrier.arrive_drop"),
                    Vec(OperandConstraint.Value, OperandConstraint.Address, OperandConstraint.Value),
                    Vec(_root_.scala.collection.immutable.Set(ModifierName.assume("b64")).assume[MinLength[1]]),
                    _root_.scala.collection.immutable.Set(
                      ModifierName.assume("cluster"),
                      ModifierName.assume("cta"),
                      ModifierName.assume("relaxed"),
                      ModifierName.assume("release"),
                      ModifierName.assume("shared")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("mbarrier.arrive_drop"),
                    Vec(OperandConstraint.Value, OperandConstraint.Address, OperandConstraint.Value),
                    Vec(_root_.scala.collection.immutable.Set(ModifierName.assume("b64")).assume[MinLength[1]]),
                    _root_.scala.collection.immutable.Set(
                      ModifierName.assume("cluster"),
                      ModifierName.assume("cta"),
                      ModifierName.assume("relaxed"),
                      ModifierName.assume("release"),
                      ModifierName.assume("shared::cluster")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("mbarrier.arrive_drop"),
                    Vec(OperandConstraint.Value, OperandConstraint.Address, OperandConstraint.Value),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("expect_tx")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("b64")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName.assume("cluster"),
                      ModifierName.assume("cta"),
                      ModifierName.assume("relaxed"),
                      ModifierName.assume("release"),
                      ModifierName.assume("shared")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("mbarrier.arrive_drop"),
                    Vec(OperandConstraint.Value, OperandConstraint.Address, OperandConstraint.Value),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("expect_tx")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("b64")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName.assume("cluster"),
                      ModifierName.assume("cta"),
                      ModifierName.assume("relaxed"),
                      ModifierName.assume("release"),
                      ModifierName.assume("shared::cluster")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("mbarrier.arrive_drop"),
                    Vec(OperandConstraint.Value, OperandConstraint.Address, OperandConstraint.Value),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("noComplete")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("b64")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName.assume("cta"),
                      ModifierName.assume("release"),
                      ModifierName.assume("shared")
                    ),
                    false
                  )
                )
              )
            ).assume[MinLength[1]])
        )
    case MbarrierCheckLayout extends Definition(
          Opcode.assume("mbarrier.check_layout"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 2) Right(Instruction.MbarrierCheckLayout(operands(0), operands(1), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("mbarrier.check_layout", Vec(2), operands.size))
          },
          () =>
            Metadata(Vec(
              Metadata.Entry(
                "parallel-synchronization-and-communication-instructions-mbarrier-check-layout".assume[MinLength[
                  1
                ] & NoNul],
                "9.7.14.16.21. Parallel Synchronization and Communication Instructions: mbarrier.check_layout".assume[
                  MinLength[1] & NoNul
                ],
                "mbarrier.check_layout".assume[MinLength[1] & NoNul],
                "Parallel Synchronization and Communication Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "mbarrier.check_layout.layout{.ss}.b64 p, [addr];\n\n.layout = { .layout::v0, .layout::v1 }\n.ss = { .shared::cta }"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(9, 3),
                Target(90),
                Vec(InstructionForm(
                  Opcode.assume("mbarrier.check_layout"),
                  Vec(OperandConstraint.Register, OperandConstraint.Address),
                  Vec(
                    _root_.scala.collection.immutable.Set(
                      ModifierName.assume("layout::v0"),
                      ModifierName.assume("layout::v1")
                    ).assume[MinLength[1]],
                    _root_.scala.collection.immutable.Set(ModifierName.assume("b64")).assume[MinLength[1]]
                  ),
                  _root_.scala.collection.immutable.Set(ModifierName.assume("shared::cta")),
                  true
                ))
              )
            ).assume[MinLength[1]])
        )
    case MbarrierCompleteTx extends Definition(
          Opcode.assume("mbarrier.complete_tx"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 2) Right(Instruction.MbarrierCompleteTx(operands(0), operands(1), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("mbarrier.complete_tx", Vec(2), operands.size))
          },
          () =>
            Metadata(Vec(
              Metadata.Entry(
                "parallel-synchronization-and-communication-instructions-mbarrier-complete-tx".assume[MinLength[
                  1
                ] & NoNul],
                "9.7.14.16.15. Parallel Synchronization and Communication Instructions: mbarrier.complete_tx".assume[
                  MinLength[1] & NoNul
                ],
                "mbarrier.complete_tx".assume[MinLength[1] & NoNul],
                "Parallel Synchronization and Communication Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "mbarrier.complete_tx{.sem.scope}{.space}.b64 [addr], txCount;\n\n.sem   = { .relaxed }\n.scope = { .cta, .cluster }\n.space = { .shared{::cta}, .shared::cluster }"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(8, 0),
                Target(90),
                Vec(InstructionForm(
                  Opcode.assume("mbarrier.complete_tx"),
                  Vec(OperandConstraint.Address, OperandConstraint.Value),
                  Vec(_root_.scala.collection.immutable.Set(ModifierName.assume("b64")).assume[MinLength[1]]),
                  _root_.scala.collection.immutable.Set(
                    ModifierName.assume("cluster"),
                    ModifierName.assume("cta"),
                    ModifierName.assume("relaxed")
                  ),
                  false
                ))
              )
            ).assume[MinLength[1]])
        )
    case MbarrierExpectTx extends Definition(
          Opcode.assume("mbarrier.expect_tx"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 2) Right(Instruction.MbarrierExpectTx(operands(0), operands(1), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("mbarrier.expect_tx", Vec(2), operands.size))
          },
          () =>
            Metadata(Vec(
              Metadata.Entry(
                "parallel-synchronization-and-communication-instructions-mbarrier-expect-tx".assume[MinLength[
                  1
                ] & NoNul],
                "9.7.14.16.14. Parallel Synchronization and Communication Instructions: mbarrier.expect_tx".assume[
                  MinLength[1] & NoNul
                ],
                "mbarrier.expect_tx".assume[MinLength[1] & NoNul],
                "Parallel Synchronization and Communication Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "mbarrier.expect_tx{.sem.scope}{.space}.b64 [addr], txCount;\n\n.sem   = { .relaxed }\n.scope = { .cta, .cluster }\n.space = { .shared{::cta}, .shared::cluster }"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(8, 0),
                Target(90),
                Vec(InstructionForm(
                  Opcode.assume("mbarrier.expect_tx"),
                  Vec(OperandConstraint.Address, OperandConstraint.Value),
                  Vec(_root_.scala.collection.immutable.Set(ModifierName.assume("b64")).assume[MinLength[1]]),
                  _root_.scala.collection.immutable.Set(
                    ModifierName.assume("cluster"),
                    ModifierName.assume("cta"),
                    ModifierName.assume("relaxed")
                  ),
                  false
                ))
              )
            ).assume[MinLength[1]])
        )
    case MbarrierInit extends Definition(
          Opcode.assume("mbarrier.init"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 2) Right(Instruction.MbarrierInit(operands(0), operands(1), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("mbarrier.init", Vec(2), operands.size))
          },
          () =>
            Metadata(Vec(
              Metadata.Entry(
                "parallel-synchronization-and-communication-instructions-mbarrier-init".assume[MinLength[1] & NoNul],
                "9.7.14.16.12. Parallel Synchronization and Communication Instructions: mbarrier.init".assume[MinLength[
                  1
                ] & NoNul],
                "mbarrier.init".assume[MinLength[1] & NoNul],
                "Parallel Synchronization and Communication Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "mbarrier.init{.layout}{.shared{::cta}}.b64 [addr], count;\n\n.layout = { .layout::v0, .layout::v1 }"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(7, 0),
                Target(80),
                Vec(InstructionForm(
                  Opcode.assume("mbarrier.init"),
                  Vec(OperandConstraint.Address, OperandConstraint.Value),
                  Vec(_root_.scala.collection.immutable.Set(ModifierName.assume("b64")).assume[MinLength[1]]),
                  _root_.scala.collection.immutable.Set(
                    ModifierName.assume("layout::v0"),
                    ModifierName.assume("layout::v1"),
                    ModifierName.assume("shared")
                  ),
                  false
                ))
              )
            ).assume[MinLength[1]])
        )
    case MbarrierInval extends Definition(
          Opcode.assume("mbarrier.inval"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 1) Right(Instruction.MbarrierInval(operands(0), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("mbarrier.inval", Vec(1), operands.size))
          },
          () =>
            Metadata(Vec(
              Metadata.Entry(
                "parallel-synchronization-and-communication-instructions-mbarrier-inval".assume[MinLength[1] & NoNul],
                "9.7.14.16.13. Parallel Synchronization and Communication Instructions: mbarrier.inval".assume[
                  MinLength[1] & NoNul
                ],
                "mbarrier.inval".assume[MinLength[1] & NoNul],
                "Parallel Synchronization and Communication Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "mbarrier.inval{.shared{::cta}}.b64 [addr];"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(7, 0),
                Target(80),
                Vec(InstructionForm(
                  Opcode.assume("mbarrier.inval"),
                  Vec(OperandConstraint.Address),
                  Vec(_root_.scala.collection.immutable.Set(ModifierName.assume("b64")).assume[MinLength[1]]),
                  _root_.scala.collection.immutable.Set(ModifierName.assume("shared")),
                  false
                ))
              )
            ).assume[MinLength[1]])
        )
    case MbarrierPendingCount extends Definition(
          Opcode.assume("mbarrier.pending_count"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 2) Right(Instruction.MbarrierPendingCount(operands(0), operands(1), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("mbarrier.pending_count", Vec(2), operands.size))
          },
          () =>
            Metadata(Vec(
              Metadata.Entry(
                "parallel-synchronization-and-communication-instructions-mbarrier-pending-count".assume[MinLength[
                  1
                ] & NoNul],
                "9.7.14.16.20. Parallel Synchronization and Communication Instructions: mbarrier.pending_count".assume[
                  MinLength[1] & NoNul
                ],
                "mbarrier.pending_count".assume[MinLength[1] & NoNul],
                "Parallel Synchronization and Communication Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "mbarrier.pending_count{.layout}.b64 count, state;\n\n.layout = { .layout::v0 }"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(7, 0),
                Target(80),
                Vec(InstructionForm(
                  Opcode.assume("mbarrier.pending_count"),
                  Vec(OperandConstraint.Value, OperandConstraint.Value),
                  Vec(_root_.scala.collection.immutable.Set(ModifierName.assume("b64")).assume[MinLength[1]]),
                  _root_.scala.collection.immutable.Set(ModifierName.assume("layout::v0")),
                  false
                ))
              )
            ).assume[MinLength[1]])
        )
    case MbarrierTestWait extends Definition(
          Opcode.assume("mbarrier.test_wait"),
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
            Metadata(Vec(
              Metadata.Entry(
                "parallel-synchronization-and-communication-instructions-mbarrier-test-wait-try-wait".assume[MinLength[
                  1
                ] & NoNul],
                "9.7.14.16.19. Parallel Synchronization and Communication Instructions: mbarrier.test_wait / mbarrier.try_wait".assume[
                  MinLength[1] & NoNul
                ],
                "mbarrier.test_wait / mbarrier.try_wait".assume[MinLength[1] & NoNul],
                "Parallel Synchronization and Communication Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "// without parity\nmbarrier.test_wait{.phase_type::primary}{.sem.scope}{.ss}.b64      waitComplete, [addr], state;\n\nmbarrier.test_wait.phase_type::primary{.sem}{.scope}{.ss}.b64      waitComplete|reportPredicate\n                                                                   {, reportValue}, [addr], state;\n\n// with parity\nmbarrier.test_wait.parity{.phase_type}{.sem.scope}{.ss}.b64        waitComplete, [addr], phaseParity;\n\nmbarrier.test_wait.parity.phase_type::primary{.sem}{.scope}{.ss}.b64  waitComplete|reportPredicate\n                                                                      {, reportValue}, [addr], phaseParity;\n\n// without parity\nmbarrier.try_wait{.phase_type::primary}{.sem.scope}{.ss}.b64      waitComplete, [addr], state {, timeHint};\n\nmbarrier.try_wait.phase_type::primary{.sem}{.scope}{.ss}.b64      waitComplete|reportPredicate {, reportValue},\n                                                                  [addr], state {, timeHint};\n\n// with parity\nmbarrier.try_wait.parity{.phase_type}{.sem.scope}{.ss}.b64            waitComplete, [addr], phaseParity {, timeHint};\n\nmbarrier.try_wait.parity.phase_type::primary{.sem}{.scope}{.ss}.b64   waitComplete|reportPredicate {, reportValue},\n                                                                      [addr], phaseParity {, timeHint};\n\n\n.ss   = { .shared{::cta} }\n.sem   = { .acquire, .relaxed }\n.scope = { .cta, .cluster }\n.phase_type = { .phase_type::primary, .phase_type::conditional }"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(1, 0),
                Target(80),
                Vec(
                  InstructionForm(
                    Opcode.assume("mbarrier.test_wait"),
                    Vec(OperandConstraint.Value, OperandConstraint.Address, OperandConstraint.Value),
                    Vec(_root_.scala.collection.immutable.Set(ModifierName.assume("b64")).assume[MinLength[1]]),
                    _root_.scala.collection.immutable.Set(
                      ModifierName.assume("acquire"),
                      ModifierName.assume("cluster"),
                      ModifierName.assume("cta"),
                      ModifierName.assume("phase_type::primary"),
                      ModifierName.assume("relaxed")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("mbarrier.test_wait"),
                    Vec(
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Address,
                      OperandConstraint.Value
                    ),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume(
                        "phase_type::primary"
                      )).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("b64")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName.assume("acquire"),
                      ModifierName.assume("cluster"),
                      ModifierName.assume("cta"),
                      ModifierName.assume("relaxed")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("mbarrier.test_wait"),
                    Vec(OperandConstraint.Value, OperandConstraint.Address, OperandConstraint.Value),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("parity")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("b64")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName.assume("acquire"),
                      ModifierName.assume("cluster"),
                      ModifierName.assume("cta"),
                      ModifierName.assume("phase_type::conditional"),
                      ModifierName.assume("phase_type::primary"),
                      ModifierName.assume("relaxed")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("mbarrier.test_wait"),
                    Vec(
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Address,
                      OperandConstraint.Value
                    ),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("parity")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume(
                        "phase_type::primary"
                      )).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("b64")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName.assume("acquire"),
                      ModifierName.assume("cluster"),
                      ModifierName.assume("cta"),
                      ModifierName.assume("relaxed")
                    ),
                    false
                  )
                )
              )
            ).assume[MinLength[1]])
        )
    case MbarrierTryWait extends Definition(
          Opcode.assume("mbarrier.try_wait"),
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
            Metadata(Vec(
              Metadata.Entry(
                "parallel-synchronization-and-communication-instructions-mbarrier-test-wait-try-wait".assume[MinLength[
                  1
                ] & NoNul],
                "9.7.14.16.19. Parallel Synchronization and Communication Instructions: mbarrier.test_wait / mbarrier.try_wait".assume[
                  MinLength[1] & NoNul
                ],
                "mbarrier.test_wait / mbarrier.try_wait".assume[MinLength[1] & NoNul],
                "Parallel Synchronization and Communication Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "// without parity\nmbarrier.test_wait{.phase_type::primary}{.sem.scope}{.ss}.b64      waitComplete, [addr], state;\n\nmbarrier.test_wait.phase_type::primary{.sem}{.scope}{.ss}.b64      waitComplete|reportPredicate\n                                                                   {, reportValue}, [addr], state;\n\n// with parity\nmbarrier.test_wait.parity{.phase_type}{.sem.scope}{.ss}.b64        waitComplete, [addr], phaseParity;\n\nmbarrier.test_wait.parity.phase_type::primary{.sem}{.scope}{.ss}.b64  waitComplete|reportPredicate\n                                                                      {, reportValue}, [addr], phaseParity;\n\n// without parity\nmbarrier.try_wait{.phase_type::primary}{.sem.scope}{.ss}.b64      waitComplete, [addr], state {, timeHint};\n\nmbarrier.try_wait.phase_type::primary{.sem}{.scope}{.ss}.b64      waitComplete|reportPredicate {, reportValue},\n                                                                  [addr], state {, timeHint};\n\n// with parity\nmbarrier.try_wait.parity{.phase_type}{.sem.scope}{.ss}.b64            waitComplete, [addr], phaseParity {, timeHint};\n\nmbarrier.try_wait.parity.phase_type::primary{.sem}{.scope}{.ss}.b64   waitComplete|reportPredicate {, reportValue},\n                                                                      [addr], phaseParity {, timeHint};\n\n\n.ss   = { .shared{::cta} }\n.sem   = { .acquire, .relaxed }\n.scope = { .cta, .cluster }\n.phase_type = { .phase_type::primary, .phase_type::conditional }"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(1, 0),
                Target(80),
                Vec(
                  InstructionForm(
                    Opcode.assume("mbarrier.try_wait"),
                    Vec(
                      OperandConstraint.Value,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(_root_.scala.collection.immutable.Set(ModifierName.assume("b64")).assume[MinLength[1]]),
                    _root_.scala.collection.immutable.Set(
                      ModifierName.assume("acquire"),
                      ModifierName.assume("cluster"),
                      ModifierName.assume("cta"),
                      ModifierName.assume("phase_type::primary"),
                      ModifierName.assume("relaxed")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("mbarrier.try_wait"),
                    Vec(
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume(
                        "phase_type::primary"
                      )).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("b64")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName.assume("acquire"),
                      ModifierName.assume("cluster"),
                      ModifierName.assume("cta"),
                      ModifierName.assume("relaxed")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("mbarrier.try_wait"),
                    Vec(
                      OperandConstraint.Value,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("parity")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("b64")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName.assume("acquire"),
                      ModifierName.assume("cluster"),
                      ModifierName.assume("cta"),
                      ModifierName.assume("phase_type::conditional"),
                      ModifierName.assume("phase_type::primary"),
                      ModifierName.assume("relaxed")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("mbarrier.try_wait"),
                    Vec(
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("parity")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume(
                        "phase_type::primary"
                      )).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("b64")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName.assume("acquire"),
                      ModifierName.assume("cluster"),
                      ModifierName.assume("cta"),
                      ModifierName.assume("relaxed")
                    ),
                    false
                  )
                )
              )
            ).assume[MinLength[1]])
        )
    case Membar extends Definition(
          Opcode.assume("membar"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 0) Right(Instruction.Membar(mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("membar", Vec(0), operands.size))
          },
          () =>
            Metadata(Vec(
              Metadata.Entry(
                "parallel-synchronization-and-communication-instructions-membar".assume[MinLength[1] & NoNul],
                "9.7.14.4. Parallel Synchronization and Communication Instructions: membar / fence".assume[MinLength[
                  1
                ] & NoNul],
                "membar / fence".assume[MinLength[1] & NoNul],
                "Parallel Synchronization and Communication Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "// Thread fence:\nfence{.sem}.scope;\n\n// Thread fence:\nfence.acquire.sync_restrict::shared::cluster.cluster;\nfence.release.sync_restrict::shared::cta.cluster;\n\n// Operation fence:\nfence.op_restrict.release.cluster;\n\n// Proxy fence (bi-directional):\nfence.proxy.proxykind;\n\n// Proxy fence (uni-directional):\nfence.proxy.to_proxykind::from_proxykind.release.scope;\nfence.proxy.to_proxykind::from_proxykind.acquire.scope  [addr], size;\nfence.proxy.async::generic.acquire.sync_restrict::shared::cluster.cluster;\nfence.proxy.async::generic.release.sync_restrict::shared::cta.cluster;\nfence.proxy.to_proxykind::from_proxykind_fabric.alias.sem_fabric.sys;\n\n// Old style membar:\nmembar.level;\nmembar.proxy.proxykind;\n\n.sem       = { .sc, .acq_rel, .acquire, .release };\n.scope     = { .cta, .cluster, .gpu, .sys };\n.level     = { .cta, .gl, .sys };\n.proxykind = { .alias, .async, .async.global, .async.shared::{cta, cluster} };\n.op_restrict = { .mbarrier_init };\n.to_proxykind::from_proxykind = {.tensormap::generic};\n.to_proxykind::from_proxykind_fabric = {.generic::fabric, .fabric::generic, .fabric::fabric };\n.sem_fabric = { .acquire, .release };"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(1, 0),
                Target(20),
                Vec(
                  InstructionForm(
                    Opcode.assume("membar"),
                    Vec(),
                    Vec(_root_.scala.collection.immutable.Set(
                      ModifierName.assume("cta"),
                      ModifierName.assume("gl"),
                      ModifierName.assume("sys")
                    ).assume[MinLength[1]]),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("membar"),
                    Vec(),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("proxy")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("alias"),
                        ModifierName.assume("async"),
                        ModifierName.assume("cluster")
                      ).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  )
                )
              )
            ).assume[MinLength[1]])
        )
    case Min extends Definition(
          Opcode.assume("min"),
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
            Metadata(Vec(
              Metadata.Entry(
                "integer-arithmetic-instructions-min".assume[MinLength[1] & NoNul],
                "9.7.1.13. Integer Arithmetic Instructions: min".assume[MinLength[1] & NoNul],
                "min".assume[MinLength[1] & NoNul],
                "Integer Arithmetic Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "min.type1         d, a, b;\nmin{.relu}.type2  d, a, b;\n\n.type1 = { .u16, .u32, .u64,\n           .u16x2, .u8x4, .s16, .s64 };\n.type2 = { .s16x2, .s32, .s8x4 };"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(1, 0),
                Target(10),
                Vec(
                  InstructionForm(
                    Opcode.assume("min"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(_root_.scala.collection.immutable.Set(
                      ModifierName.assume("s16"),
                      ModifierName.assume("s64"),
                      ModifierName.assume("u16"),
                      ModifierName.assume("u16x2"),
                      ModifierName.assume("u32"),
                      ModifierName.assume("u64"),
                      ModifierName.assume("u8x4")
                    ).assume[MinLength[1]]),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("min"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(_root_.scala.collection.immutable.Set(
                      ModifierName.assume("s16x2"),
                      ModifierName.assume("s32"),
                      ModifierName.assume("s8x4")
                    ).assume[MinLength[1]]),
                    _root_.scala.collection.immutable.Set(ModifierName.assume("relu")),
                    false
                  )
                )
              ),
              Metadata.Entry(
                "floating-point-instructions-min".assume[MinLength[1] & NoNul],
                "9.7.3.11. Floating Point Instructions: min".assume[MinLength[1] & NoNul],
                "min".assume[MinLength[1] & NoNul],
                "Floating Point Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "min{.ftz}{.NaN}{.xorsign.abs}.f32  d, a, b;\nmin{.ftz}{.NaN}{.abs}.f32          d, a, b, c;\nmin.f64                            d, a, b;"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(1, 0),
                Target(13),
                Vec(
                  InstructionForm(
                    Opcode.assume("min"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(_root_.scala.collection.immutable.Set(ModifierName.assume("f32")).assume[MinLength[1]]),
                    _root_.scala.collection.immutable.Set(
                      ModifierName.assume("NaN"),
                      ModifierName.assume("abs"),
                      ModifierName.assume("ftz"),
                      ModifierName.assume("xorsign")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("min"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(_root_.scala.collection.immutable.Set(ModifierName.assume("f32")).assume[MinLength[1]]),
                    _root_.scala.collection.immutable.Set(
                      ModifierName.assume("NaN"),
                      ModifierName.assume("abs"),
                      ModifierName.assume("ftz")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("min"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(_root_.scala.collection.immutable.Set(ModifierName.assume("f64")).assume[MinLength[1]]),
                    _root_.scala.collection.immutable.Set(),
                    false
                  )
                )
              ),
              Metadata.Entry(
                "half-precision-floating-point-instructions-min".assume[MinLength[1] & NoNul],
                "9.7.4.7. Half Precision Floating Point Instructions: min".assume[MinLength[1] & NoNul],
                "min".assume[MinLength[1] & NoNul],
                "Half Precision Floating Point Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "min{.ftz}{.NaN}{.xorsign.abs}.f16      d, a, b;\nmin{.ftz}{.NaN}{.xorsign.abs}.f16x2    d, a, b;\nmin{.NaN}{.xorsign.abs}.bf16           d, a, b;\nmin{.NaN}{.xorsign.abs}.bf16x2         d, a, b;"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(7, 0),
                Target(80),
                Vec(
                  InstructionForm(
                    Opcode.assume("min"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(_root_.scala.collection.immutable.Set(ModifierName.assume("f16")).assume[MinLength[1]]),
                    _root_.scala.collection.immutable.Set(
                      ModifierName.assume("NaN"),
                      ModifierName.assume("abs"),
                      ModifierName.assume("ftz"),
                      ModifierName.assume("xorsign")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("min"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(_root_.scala.collection.immutable.Set(ModifierName.assume("f16x2")).assume[MinLength[1]]),
                    _root_.scala.collection.immutable.Set(
                      ModifierName.assume("NaN"),
                      ModifierName.assume("abs"),
                      ModifierName.assume("ftz"),
                      ModifierName.assume("xorsign")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("min"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(_root_.scala.collection.immutable.Set(ModifierName.assume("bf16")).assume[MinLength[1]]),
                    _root_.scala.collection.immutable.Set(
                      ModifierName.assume("NaN"),
                      ModifierName.assume("abs"),
                      ModifierName.assume("xorsign")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("min"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(_root_.scala.collection.immutable.Set(ModifierName.assume("bf16x2")).assume[MinLength[1]]),
                    _root_.scala.collection.immutable.Set(
                      ModifierName.assume("NaN"),
                      ModifierName.assume("abs"),
                      ModifierName.assume("xorsign")
                    ),
                    false
                  )
                )
              )
            ).assume[MinLength[1]])
        )
    case Mov extends Definition(
          Opcode.assume("mov"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 2) Right(Instruction.Mov(operands(0), operands(1), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("mov", Vec(2), operands.size))
          },
          () =>
            Metadata(Vec(
              Metadata.Entry(
                "data-movement-and-conversion-instructions-mov".assume[MinLength[1] & NoNul],
                "9.7.9.3. Data Movement and Conversion Instructions: mov".assume[MinLength[1] & NoNul],
                "mov".assume[MinLength[1] & NoNul],
                "Data Movement and Conversion Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "mov.type  d, a;\nmov.type  d, sreg;\nmov.type  d, avar;       // get address of variable\nmov.type  d, avar+imm;   // get address of variable with offset\nmov.u32   d, fname;      // get address of device function\nmov.u64   d, fname;      // get address of device function\nmov.u32   d, kernel;     // get address of entry function\nmov.u64   d, kernel;     // get address of entry function\n\n.type = { .pred,\n          .b16, .b32, .b64,\n          .u16, .u32, .u64,\n          .s16, .s32, .s64,\n                .f32, .f64 };"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(1, 0),
                Target(13),
                Vec(
                  InstructionForm(
                    Opcode.assume("mov"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(_root_.scala.collection.immutable.Set(
                      ModifierName.assume("b16"),
                      ModifierName.assume("b32"),
                      ModifierName.assume("b64"),
                      ModifierName.assume("f32"),
                      ModifierName.assume("f64"),
                      ModifierName.assume("pred"),
                      ModifierName.assume("s16"),
                      ModifierName.assume("s32"),
                      ModifierName.assume("s64"),
                      ModifierName.assume("u16"),
                      ModifierName.assume("u32"),
                      ModifierName.assume("u64")
                    ).assume[MinLength[1]]),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("mov"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(_root_.scala.collection.immutable.Set(
                      ModifierName.assume("b16"),
                      ModifierName.assume("b32"),
                      ModifierName.assume("b64"),
                      ModifierName.assume("f32"),
                      ModifierName.assume("f64"),
                      ModifierName.assume("pred"),
                      ModifierName.assume("s16"),
                      ModifierName.assume("s32"),
                      ModifierName.assume("s64"),
                      ModifierName.assume("u16"),
                      ModifierName.assume("u32"),
                      ModifierName.assume("u64")
                    ).assume[MinLength[1]]),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("mov"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(_root_.scala.collection.immutable.Set(
                      ModifierName.assume("b16"),
                      ModifierName.assume("b32"),
                      ModifierName.assume("b64"),
                      ModifierName.assume("f32"),
                      ModifierName.assume("f64"),
                      ModifierName.assume("pred"),
                      ModifierName.assume("s16"),
                      ModifierName.assume("s32"),
                      ModifierName.assume("s64"),
                      ModifierName.assume("u16"),
                      ModifierName.assume("u32"),
                      ModifierName.assume("u64")
                    ).assume[MinLength[1]]),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("mov"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(_root_.scala.collection.immutable.Set(
                      ModifierName.assume("b16"),
                      ModifierName.assume("b32"),
                      ModifierName.assume("b64"),
                      ModifierName.assume("f32"),
                      ModifierName.assume("f64"),
                      ModifierName.assume("pred"),
                      ModifierName.assume("s16"),
                      ModifierName.assume("s32"),
                      ModifierName.assume("s64"),
                      ModifierName.assume("u16"),
                      ModifierName.assume("u32"),
                      ModifierName.assume("u64")
                    ).assume[MinLength[1]]),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("mov"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(_root_.scala.collection.immutable.Set(ModifierName.assume("u32")).assume[MinLength[1]]),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("mov"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(_root_.scala.collection.immutable.Set(ModifierName.assume("u64")).assume[MinLength[1]]),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("mov"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(_root_.scala.collection.immutable.Set(ModifierName.assume("u32")).assume[MinLength[1]]),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("mov"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(_root_.scala.collection.immutable.Set(ModifierName.assume("u64")).assume[MinLength[1]]),
                    _root_.scala.collection.immutable.Set(),
                    false
                  )
                )
              ),
              Metadata.Entry(
                "data-movement-and-conversion-instructions-mov-2".assume[MinLength[1] & NoNul],
                "9.7.9.4. Data Movement and Conversion Instructions: mov".assume[MinLength[1] & NoNul],
                "mov".assume[MinLength[1] & NoNul],
                "Data Movement and Conversion Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "mov.type  d, a;\n\n.type = { .b16, .b32, .b64, .b128 };"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(1, 0),
                Target(10),
                Vec(InstructionForm(
                  Opcode.assume("mov"),
                  Vec(OperandConstraint.Register, OperandConstraint.Value),
                  Vec(_root_.scala.collection.immutable.Set(
                    ModifierName.assume("b128"),
                    ModifierName.assume("b16"),
                    ModifierName.assume("b32"),
                    ModifierName.assume("b64")
                  ).assume[MinLength[1]]),
                  _root_.scala.collection.immutable.Set(),
                  false
                ))
              )
            ).assume[MinLength[1]])
        )
    case Mul extends Definition(
          Opcode.assume("mul"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 3) Right(Instruction.Mul(operands(0), operands(1), operands(2), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("mul", Vec(3), operands.size))
          },
          () =>
            Metadata(Vec(
              Metadata.Entry(
                "integer-arithmetic-instructions-mul".assume[MinLength[1] & NoNul],
                "9.7.1.3. Integer Arithmetic Instructions: mul".assume[MinLength[1] & NoNul],
                "mul".assume[MinLength[1] & NoNul],
                "Integer Arithmetic Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "mul.mode.type  d, a, b;\n\n.mode = { .hi, .lo, .wide };\n.type = { .u16, .u32, .u64,\n          .s16, .s32, .s64 };"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(1, 0),
                Target(10),
                Vec(InstructionForm(
                  Opcode.assume("mul"),
                  Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                  Vec(
                    _root_.scala.collection.immutable.Set(
                      ModifierName.assume("hi"),
                      ModifierName.assume("lo"),
                      ModifierName.assume("wide")
                    ).assume[MinLength[1]],
                    _root_.scala.collection.immutable.Set(
                      ModifierName.assume("s16"),
                      ModifierName.assume("s32"),
                      ModifierName.assume("s64"),
                      ModifierName.assume("u16"),
                      ModifierName.assume("u32"),
                      ModifierName.assume("u64")
                    ).assume[MinLength[1]]
                  ),
                  _root_.scala.collection.immutable.Set(),
                  true
                ))
              ),
              Metadata.Entry(
                "floating-point-instructions-mul".assume[MinLength[1] & NoNul],
                "9.7.3.5. Floating Point Instructions: mul".assume[MinLength[1] & NoNul],
                "mul".assume[MinLength[1] & NoNul],
                "Floating Point Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "mul{.rnd}{.ftz}{.sat}.f32  d, a, b;\nmul{.rnd}{.ftz}.f32x2      d, a, b;\nmul{.rnd}.f64              d, a, b;\n\n.rnd = { .rn, .rz, .rm, .rp };"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(1, 0),
                Target(13),
                Vec(
                  InstructionForm(
                    Opcode.assume("mul"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(_root_.scala.collection.immutable.Set(ModifierName.assume("f32")).assume[MinLength[1]]),
                    _root_.scala.collection.immutable.Set(
                      ModifierName.assume("ftz"),
                      ModifierName.assume("rm"),
                      ModifierName.assume("rn"),
                      ModifierName.assume("rp"),
                      ModifierName.assume("rz"),
                      ModifierName.assume("sat")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("mul"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(_root_.scala.collection.immutable.Set(ModifierName.assume("f32x2")).assume[MinLength[1]]),
                    _root_.scala.collection.immutable.Set(
                      ModifierName.assume("ftz"),
                      ModifierName.assume("rm"),
                      ModifierName.assume("rn"),
                      ModifierName.assume("rp"),
                      ModifierName.assume("rz")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("mul"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(_root_.scala.collection.immutable.Set(ModifierName.assume("f64")).assume[MinLength[1]]),
                    _root_.scala.collection.immutable.Set(
                      ModifierName.assume("rm"),
                      ModifierName.assume("rn"),
                      ModifierName.assume("rp"),
                      ModifierName.assume("rz")
                    ),
                    false
                  )
                )
              ),
              Metadata.Entry(
                "half-precision-floating-point-instructions-mul".assume[MinLength[1] & NoNul],
                "9.7.4.3. Half Precision Floating Point Instructions: mul".assume[MinLength[1] & NoNul],
                "mul".assume[MinLength[1] & NoNul],
                "Half Precision Floating Point Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "mul{.rnd}{.ftz}{.sat}.f16   d, a, b;\nmul{.rnd}{.ftz}{.sat}.f16x2 d, a, b;\n\nmul{.rnd}.bf16   d, a, b;\nmul{.rnd}.bf16x2 d, a, b;\n\n.rnd = { .rn };"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(4, 2),
                Target(53),
                Vec(
                  InstructionForm(
                    Opcode.assume("mul"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(_root_.scala.collection.immutable.Set(ModifierName.assume("f16")).assume[MinLength[1]]),
                    _root_.scala.collection.immutable.Set(
                      ModifierName.assume("ftz"),
                      ModifierName.assume("rn"),
                      ModifierName.assume("sat")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("mul"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(_root_.scala.collection.immutable.Set(ModifierName.assume("f16x2")).assume[MinLength[1]]),
                    _root_.scala.collection.immutable.Set(
                      ModifierName.assume("ftz"),
                      ModifierName.assume("rn"),
                      ModifierName.assume("sat")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("mul"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(_root_.scala.collection.immutable.Set(ModifierName.assume("bf16")).assume[MinLength[1]]),
                    _root_.scala.collection.immutable.Set(ModifierName.assume("rn")),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("mul"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(_root_.scala.collection.immutable.Set(ModifierName.assume("bf16x2")).assume[MinLength[1]]),
                    _root_.scala.collection.immutable.Set(ModifierName.assume("rn")),
                    false
                  )
                )
              )
            ).assume[MinLength[1]])
        )
    case Mul24 extends Definition(
          Opcode.assume("mul24"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 3) Right(Instruction.Mul24(operands(0), operands(1), operands(2), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("mul24", Vec(3), operands.size))
          },
          () =>
            Metadata(Vec(
              Metadata.Entry(
                "integer-arithmetic-instructions-mul24".assume[MinLength[1] & NoNul],
                "9.7.1.6. Integer Arithmetic Instructions: mul24".assume[MinLength[1] & NoNul],
                "mul24".assume[MinLength[1] & NoNul],
                "Integer Arithmetic Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "mul24.mode.type  d, a, b;\n\n.mode = { .hi, .lo };\n.type = { .u32, .s32 };"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(1, 0),
                Target(10),
                Vec(InstructionForm(
                  Opcode.assume("mul24"),
                  Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                  Vec(
                    _root_.scala.collection.immutable.Set(
                      ModifierName.assume("hi"),
                      ModifierName.assume("lo")
                    ).assume[MinLength[1]],
                    _root_.scala.collection.immutable.Set(
                      ModifierName.assume("s32"),
                      ModifierName.assume("u32")
                    ).assume[MinLength[1]]
                  ),
                  _root_.scala.collection.immutable.Set(),
                  true
                ))
              )
            ).assume[MinLength[1]])
        )
    case MultimemCpAsyncBulk extends Definition(
          Opcode.assume("multimem.cp.async.bulk"),
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
            Metadata(Vec(
              Metadata.Entry(
                "data-movement-and-conversion-instructions-multimem-cp-async-bulk".assume[MinLength[1] & NoNul],
                "9.7.9.26.4.4. Data Movement and Conversion Instructions: multimem.cp.async.bulk".assume[MinLength[
                  1
                ] & NoNul],
                "multimem.cp.async.bulk".assume[MinLength[1] & NoNul],
                "Data Movement and Conversion Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "multimem.cp.async.bulk{.sem}.dst.src.completion_mechanism{.cp_mask}\n    [dstMem], [srcMem], size{, byteMask};\n\n    .sem                  = { .weak }\n    .dst                  = { .global }\n    .src                  = { .shared::cta }\n    .completion_mechanism = { .bulk_group }\n\nmultimem.cp.async.bulk.sem.scope.dst.src.completion_mechanism{.cp_mask}.type\n    [dstMem], [srcMem], size{, byteMask};\n\n    .sem                  = { .relaxed }\n    .scope                = { .cta, .cluster, .gpu, .sys }\n    .dst                  = { .global }\n    .src                  = { .shared::cta }\n    .completion_mechanism = { .bulk_group }\n    .type                 = { .b128 }"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(9, 1),
                Target(90),
                Vec(
                  InstructionForm(
                    Opcode.assume("multimem.cp.async.bulk"),
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("global")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("shared::cta")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("bulk_group")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName.assume("cp_mask"),
                      ModifierName.assume("relaxed")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("multimem.cp.async.bulk"),
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("relaxed")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("cluster"),
                        ModifierName.assume("cta"),
                        ModifierName.assume("gpu"),
                        ModifierName.assume("sys")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("global")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("shared::cta")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("bulk_group")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("b128")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName.assume("cp_mask")),
                    false
                  )
                )
              )
            ).assume[MinLength[1]])
        )
    case MultimemCpReduceAsyncBulk extends Definition(
          Opcode.assume("multimem.cp.reduce.async.bulk"),
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
            Metadata(Vec(
              Metadata.Entry(
                "data-movement-and-conversion-instructions-multimem-cp-reduce-async-bulk".assume[MinLength[1] & NoNul],
                "9.7.9.26.4.5. Data Movement and Conversion Instructions: multimem.cp.reduce.async.bulk".assume[
                  MinLength[1] & NoNul
                ],
                "multimem.cp.reduce.async.bulk".assume[MinLength[1] & NoNul],
                "Data Movement and Conversion Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "multimem.cp.reduce.async.bulk{.sem.scope}.dst.src.completion_mechanism.redOp.type  [dstMem], [srcMem], size;\n\n    .sem                  =  { .relaxed }\n    .scope                = { .cta, .cluster, .gpu, .sys }\n    .dst                  = { .global }\n    .src                  = { .shared::cta }\n    .completion_mechanism = { .bulk_group }\n    .redOp                = { .and, .or, .xor,\n                              .add, .inc, .dec,\n                              .min, .max }\n    .type                 = { .f16, .bf16,\n                              .b32, .u32, .s32,\n                              .b64, .u64, .s64,\n                              .f32, .f64 }\n\nmultimem.cp.reduce.async.bulk{.sem.scope}.dst.src.completion_mechanism.add.noftz.type  [dstMem], [srcMem], size;\n\n    .sem                  = { .relaxed }\n    .scope                = { .cta, .cluster, .gpu, .sys }\n    .dst                  = { .global }\n    .src                  = { .shared::cta }\n    .completion_mechanism = { .bulk_group }\n    .type                 = { .f16, .bf16 }"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(9, 1),
                Target(90),
                Vec(
                  InstructionForm(
                    Opcode.assume("multimem.cp.reduce.async.bulk"),
                    Vec(OperandConstraint.Address, OperandConstraint.Address, OperandConstraint.Value),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("global")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("shared::cta")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("bulk_group")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("add"),
                        ModifierName.assume("and"),
                        ModifierName.assume("dec"),
                        ModifierName.assume("inc"),
                        ModifierName.assume("max"),
                        ModifierName.assume("min"),
                        ModifierName.assume("or"),
                        ModifierName.assume("xor")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("bf16"),
                        ModifierName.assume("f16")
                      ).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName.assume("cluster"),
                      ModifierName.assume("cta"),
                      ModifierName.assume("gpu"),
                      ModifierName.assume("relaxed"),
                      ModifierName.assume("sys")
                    ),
                    true
                  ),
                  InstructionForm(
                    Opcode.assume("multimem.cp.reduce.async.bulk"),
                    Vec(OperandConstraint.Address, OperandConstraint.Address, OperandConstraint.Value),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("global")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("shared::cta")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("bulk_group")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("add")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("noftz")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("bf16"),
                        ModifierName.assume("f16")
                      ).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName.assume("cluster"),
                      ModifierName.assume("cta"),
                      ModifierName.assume("gpu"),
                      ModifierName.assume("relaxed"),
                      ModifierName.assume("sys")
                    ),
                    true
                  )
                )
              )
            ).assume[MinLength[1]])
        )
    case MultimemLdReduce extends Definition(
          Opcode.assume("multimem.ld_reduce"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 2) Right(Instruction.MultimemLdReduce(operands(0), operands(1), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("multimem.ld_reduce", Vec(2), operands.size))
          },
          () =>
            Metadata(Vec(
              Metadata.Entry(
                "data-movement-and-conversion-instructions-multimem".assume[MinLength[1] & NoNul],
                "9.7.9.15. Data Movement and Conversion Instructions: multimem.ld_reduce, multimem.st, multimem.red".assume[
                  MinLength[1] & NoNul
                ],
                "multimem.ld_reduce, multimem.st, multimem.red".assume[MinLength[1] & NoNul],
                "Data Movement and Conversion Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "// Integer type:\n\nmultimem.ld_reduce{.ldsem}{.scope}{.ss}.op.type      d, [a];\nmultimem.ld_reduce.weak{.ss}.op.type                 d, [a];\n\nmultimem.st{.stsem}{.scope}{.ss}.type                [a], b;\nmultimem.st.weak{.ss}.type                           [a], b;\n\nmultimem.red{.redsem}{.scope}{.ss}.op.type           [a], b;\n\n.ss =       { .global }\n.ldsem =    { .relaxed, .acquire }\n.stsem =    { .relaxed, .release }\n.redsem =   { .relaxed, .release }\n.scope =    { .cta, .cluster, .gpu, .sys }\n.op  =      { .min, .max, .add, .and, .or, .xor }\n.type =     { .b32, .b64,  .u32, .u64, .s32, .s64 }\n\n// Floating point type:\n\nmultimem.ld_reduce{.ldsem}{.scope}{.ss}.op{.acc_prec}{.vec}.type    d, [a];\nmultimem.ld_reduce.weak{.ss}.op{.acc_prec}{.vec}.type               d, [a];\n\nmultimem.st{.stsem}{.scope}{.ss}{.vec}.type                         [a], b;\nmultimem.st.weak{.ss}{.vec}.type                                    [a], b;\n\nmultimem.red{.redsem}{.scope}{.ss}.redop{.vec}.redtype              [a], b;\n\n.ss =       { .global }\n.ldsem =    { .relaxed, .acquire }\n.stsem =    { .relaxed, .release }\n.redsem =   { .relaxed, .release }\n.scope =    { .cta, .cluster, .gpu, .sys }\n.op  =      { .min, .max, .add }\n.redop  =   { .add }\n.acc_prec = { .acc::f32, .acc::f16 }\n.vec =      { .v2, .v4, .v8 }\n.type=      { .f16, .f16x2, .bf16, .bf16x2, .f32, .f64, .e5m2, .e5m2x2, .e5m2x4, .e4m3, .e4m3x2, .e4m3x4 }\n.redtype =  { .f16, .f16x2, .bf16, .bf16x2, .f32, .f64 }"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(8, 1),
                Target(90),
                Vec(
                  InstructionForm(
                    Opcode.assume("multimem.ld_reduce"),
                    Vec(OperandConstraint.Register, OperandConstraint.Address),
                    Vec(
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("add"),
                        ModifierName.assume("max"),
                        ModifierName.assume("min")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("bf16"),
                        ModifierName.assume("bf16x2"),
                        ModifierName.assume("e4m3"),
                        ModifierName.assume("e4m3x2"),
                        ModifierName.assume("e4m3x4"),
                        ModifierName.assume("e5m2"),
                        ModifierName.assume("e5m2x2"),
                        ModifierName.assume("e5m2x4"),
                        ModifierName.assume("f16"),
                        ModifierName.assume("f16x2"),
                        ModifierName.assume("f32"),
                        ModifierName.assume("f64")
                      ).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName.assume("acquire"),
                      ModifierName.assume("cluster"),
                      ModifierName.assume("cta"),
                      ModifierName.assume("global"),
                      ModifierName.assume("gpu"),
                      ModifierName.assume("relaxed"),
                      ModifierName.assume("sys")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("multimem.ld_reduce"),
                    Vec(OperandConstraint.Register, OperandConstraint.Address),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("weak")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("add"),
                        ModifierName.assume("max"),
                        ModifierName.assume("min")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("bf16"),
                        ModifierName.assume("bf16x2"),
                        ModifierName.assume("e4m3"),
                        ModifierName.assume("e4m3x2"),
                        ModifierName.assume("e4m3x4"),
                        ModifierName.assume("e5m2"),
                        ModifierName.assume("e5m2x2"),
                        ModifierName.assume("e5m2x4"),
                        ModifierName.assume("f16"),
                        ModifierName.assume("f16x2"),
                        ModifierName.assume("f32"),
                        ModifierName.assume("f64")
                      ).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName.assume("global")),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("multimem.ld_reduce"),
                    Vec(OperandConstraint.Register, OperandConstraint.Address),
                    Vec(
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("add"),
                        ModifierName.assume("max"),
                        ModifierName.assume("min")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("bf16"),
                        ModifierName.assume("bf16x2"),
                        ModifierName.assume("e4m3"),
                        ModifierName.assume("e4m3x2"),
                        ModifierName.assume("e4m3x4"),
                        ModifierName.assume("e5m2"),
                        ModifierName.assume("e5m2x2"),
                        ModifierName.assume("e5m2x4"),
                        ModifierName.assume("f16"),
                        ModifierName.assume("f16x2"),
                        ModifierName.assume("f32"),
                        ModifierName.assume("f64")
                      ).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName.assume("acc::f16"),
                      ModifierName.assume("acc::f32"),
                      ModifierName.assume("acquire"),
                      ModifierName.assume("cluster"),
                      ModifierName.assume("cta"),
                      ModifierName.assume("global"),
                      ModifierName.assume("gpu"),
                      ModifierName.assume("relaxed"),
                      ModifierName.assume("sys"),
                      ModifierName.assume("v2"),
                      ModifierName.assume("v4"),
                      ModifierName.assume("v8")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("multimem.ld_reduce"),
                    Vec(OperandConstraint.Register, OperandConstraint.Address),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("weak")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("add"),
                        ModifierName.assume("max"),
                        ModifierName.assume("min")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("bf16"),
                        ModifierName.assume("bf16x2"),
                        ModifierName.assume("e4m3"),
                        ModifierName.assume("e4m3x2"),
                        ModifierName.assume("e4m3x4"),
                        ModifierName.assume("e5m2"),
                        ModifierName.assume("e5m2x2"),
                        ModifierName.assume("e5m2x4"),
                        ModifierName.assume("f16"),
                        ModifierName.assume("f16x2"),
                        ModifierName.assume("f32"),
                        ModifierName.assume("f64")
                      ).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName.assume("acc::f16"),
                      ModifierName.assume("acc::f32"),
                      ModifierName.assume("global"),
                      ModifierName.assume("v2"),
                      ModifierName.assume("v4"),
                      ModifierName.assume("v8")
                    ),
                    false
                  )
                )
              )
            ).assume[MinLength[1]])
        )
    case MultimemRed extends Definition(
          Opcode.assume("multimem.red"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 2) Right(Instruction.MultimemRed(operands(0), operands(1), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("multimem.red", Vec(2), operands.size))
          },
          () =>
            Metadata(Vec(
              Metadata.Entry(
                "data-movement-and-conversion-instructions-multimem".assume[MinLength[1] & NoNul],
                "9.7.9.15. Data Movement and Conversion Instructions: multimem.ld_reduce, multimem.st, multimem.red".assume[
                  MinLength[1] & NoNul
                ],
                "multimem.ld_reduce, multimem.st, multimem.red".assume[MinLength[1] & NoNul],
                "Data Movement and Conversion Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "// Integer type:\n\nmultimem.ld_reduce{.ldsem}{.scope}{.ss}.op.type      d, [a];\nmultimem.ld_reduce.weak{.ss}.op.type                 d, [a];\n\nmultimem.st{.stsem}{.scope}{.ss}.type                [a], b;\nmultimem.st.weak{.ss}.type                           [a], b;\n\nmultimem.red{.redsem}{.scope}{.ss}.op.type           [a], b;\n\n.ss =       { .global }\n.ldsem =    { .relaxed, .acquire }\n.stsem =    { .relaxed, .release }\n.redsem =   { .relaxed, .release }\n.scope =    { .cta, .cluster, .gpu, .sys }\n.op  =      { .min, .max, .add, .and, .or, .xor }\n.type =     { .b32, .b64,  .u32, .u64, .s32, .s64 }\n\n// Floating point type:\n\nmultimem.ld_reduce{.ldsem}{.scope}{.ss}.op{.acc_prec}{.vec}.type    d, [a];\nmultimem.ld_reduce.weak{.ss}.op{.acc_prec}{.vec}.type               d, [a];\n\nmultimem.st{.stsem}{.scope}{.ss}{.vec}.type                         [a], b;\nmultimem.st.weak{.ss}{.vec}.type                                    [a], b;\n\nmultimem.red{.redsem}{.scope}{.ss}.redop{.vec}.redtype              [a], b;\n\n.ss =       { .global }\n.ldsem =    { .relaxed, .acquire }\n.stsem =    { .relaxed, .release }\n.redsem =   { .relaxed, .release }\n.scope =    { .cta, .cluster, .gpu, .sys }\n.op  =      { .min, .max, .add }\n.redop  =   { .add }\n.acc_prec = { .acc::f32, .acc::f16 }\n.vec =      { .v2, .v4, .v8 }\n.type=      { .f16, .f16x2, .bf16, .bf16x2, .f32, .f64, .e5m2, .e5m2x2, .e5m2x4, .e4m3, .e4m3x2, .e4m3x4 }\n.redtype =  { .f16, .f16x2, .bf16, .bf16x2, .f32, .f64 }"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(8, 1),
                Target(90),
                Vec(
                  InstructionForm(
                    Opcode.assume("multimem.red"),
                    Vec(OperandConstraint.Address, OperandConstraint.Value),
                    Vec(
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("add"),
                        ModifierName.assume("max"),
                        ModifierName.assume("min")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("bf16"),
                        ModifierName.assume("bf16x2"),
                        ModifierName.assume("e4m3"),
                        ModifierName.assume("e4m3x2"),
                        ModifierName.assume("e4m3x4"),
                        ModifierName.assume("e5m2"),
                        ModifierName.assume("e5m2x2"),
                        ModifierName.assume("e5m2x4"),
                        ModifierName.assume("f16"),
                        ModifierName.assume("f16x2"),
                        ModifierName.assume("f32"),
                        ModifierName.assume("f64")
                      ).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName.assume("cluster"),
                      ModifierName.assume("cta"),
                      ModifierName.assume("global"),
                      ModifierName.assume("gpu"),
                      ModifierName.assume("relaxed"),
                      ModifierName.assume("release"),
                      ModifierName.assume("sys")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("multimem.red"),
                    Vec(OperandConstraint.Address, OperandConstraint.Value),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("add")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("bf16"),
                        ModifierName.assume("bf16x2"),
                        ModifierName.assume("f16"),
                        ModifierName.assume("f16x2"),
                        ModifierName.assume("f32"),
                        ModifierName.assume("f64")
                      ).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName.assume("cluster"),
                      ModifierName.assume("cta"),
                      ModifierName.assume("global"),
                      ModifierName.assume("gpu"),
                      ModifierName.assume("relaxed"),
                      ModifierName.assume("release"),
                      ModifierName.assume("sys"),
                      ModifierName.assume("v2"),
                      ModifierName.assume("v4"),
                      ModifierName.assume("v8")
                    ),
                    false
                  )
                )
              )
            ).assume[MinLength[1]])
        )
    case MultimemRedAsync extends Definition(
          Opcode.assume("multimem.red.async"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 2) Right(Instruction.MultimemRedAsync(operands(0), operands(1), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("multimem.red.async", Vec(2), operands.size))
          },
          () =>
            Metadata(Vec(
              Metadata.Entry(
                "parallel-synchronization-and-communication-instructions-multimem-red-async".assume[MinLength[
                  1
                ] & NoNul],
                "9.7.14.8. Parallel Synchronization and Communication Instructions: multimem.red.async".assume[
                  MinLength[1] & NoNul
                ],
                "multimem.red.async".assume[MinLength[1] & NoNul],
                "Parallel Synchronization and Communication Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "multimem.red.async.sem.scope{.ss}.op.type [a], b;\n\n.sem   = { .release };\n.scope = { .gpu, .sys };\n.ss    = { .global };\n.op    = { .add };\n.type  = { .u32, .s32, .u64 };"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(9, 3),
                Target(100),
                Vec(InstructionForm(
                  Opcode.assume("multimem.red.async"),
                  Vec(OperandConstraint.Address, OperandConstraint.Value),
                  Vec(
                    _root_.scala.collection.immutable.Set(ModifierName.assume("release")).assume[MinLength[1]],
                    _root_.scala.collection.immutable.Set(
                      ModifierName.assume("gpu"),
                      ModifierName.assume("sys")
                    ).assume[MinLength[1]],
                    _root_.scala.collection.immutable.Set(ModifierName.assume("add")).assume[MinLength[1]],
                    _root_.scala.collection.immutable.Set(
                      ModifierName.assume("s32"),
                      ModifierName.assume("u32"),
                      ModifierName.assume("u64")
                    ).assume[MinLength[1]]
                  ),
                  _root_.scala.collection.immutable.Set(ModifierName.assume("global")),
                  true
                ))
              )
            ).assume[MinLength[1]])
        )
    case MultimemSt extends Definition(
          Opcode.assume("multimem.st"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 2) Right(Instruction.MultimemSt(operands(0), operands(1), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("multimem.st", Vec(2), operands.size))
          },
          () =>
            Metadata(Vec(
              Metadata.Entry(
                "data-movement-and-conversion-instructions-multimem".assume[MinLength[1] & NoNul],
                "9.7.9.15. Data Movement and Conversion Instructions: multimem.ld_reduce, multimem.st, multimem.red".assume[
                  MinLength[1] & NoNul
                ],
                "multimem.ld_reduce, multimem.st, multimem.red".assume[MinLength[1] & NoNul],
                "Data Movement and Conversion Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "// Integer type:\n\nmultimem.ld_reduce{.ldsem}{.scope}{.ss}.op.type      d, [a];\nmultimem.ld_reduce.weak{.ss}.op.type                 d, [a];\n\nmultimem.st{.stsem}{.scope}{.ss}.type                [a], b;\nmultimem.st.weak{.ss}.type                           [a], b;\n\nmultimem.red{.redsem}{.scope}{.ss}.op.type           [a], b;\n\n.ss =       { .global }\n.ldsem =    { .relaxed, .acquire }\n.stsem =    { .relaxed, .release }\n.redsem =   { .relaxed, .release }\n.scope =    { .cta, .cluster, .gpu, .sys }\n.op  =      { .min, .max, .add, .and, .or, .xor }\n.type =     { .b32, .b64,  .u32, .u64, .s32, .s64 }\n\n// Floating point type:\n\nmultimem.ld_reduce{.ldsem}{.scope}{.ss}.op{.acc_prec}{.vec}.type    d, [a];\nmultimem.ld_reduce.weak{.ss}.op{.acc_prec}{.vec}.type               d, [a];\n\nmultimem.st{.stsem}{.scope}{.ss}{.vec}.type                         [a], b;\nmultimem.st.weak{.ss}{.vec}.type                                    [a], b;\n\nmultimem.red{.redsem}{.scope}{.ss}.redop{.vec}.redtype              [a], b;\n\n.ss =       { .global }\n.ldsem =    { .relaxed, .acquire }\n.stsem =    { .relaxed, .release }\n.redsem =   { .relaxed, .release }\n.scope =    { .cta, .cluster, .gpu, .sys }\n.op  =      { .min, .max, .add }\n.redop  =   { .add }\n.acc_prec = { .acc::f32, .acc::f16 }\n.vec =      { .v2, .v4, .v8 }\n.type=      { .f16, .f16x2, .bf16, .bf16x2, .f32, .f64, .e5m2, .e5m2x2, .e5m2x4, .e4m3, .e4m3x2, .e4m3x4 }\n.redtype =  { .f16, .f16x2, .bf16, .bf16x2, .f32, .f64 }"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(8, 1),
                Target(90),
                Vec(
                  InstructionForm(
                    Opcode.assume("multimem.st"),
                    Vec(OperandConstraint.Address, OperandConstraint.Value),
                    Vec(_root_.scala.collection.immutable.Set(
                      ModifierName.assume("bf16"),
                      ModifierName.assume("bf16x2"),
                      ModifierName.assume("e4m3"),
                      ModifierName.assume("e4m3x2"),
                      ModifierName.assume("e4m3x4"),
                      ModifierName.assume("e5m2"),
                      ModifierName.assume("e5m2x2"),
                      ModifierName.assume("e5m2x4"),
                      ModifierName.assume("f16"),
                      ModifierName.assume("f16x2"),
                      ModifierName.assume("f32"),
                      ModifierName.assume("f64")
                    ).assume[MinLength[1]]),
                    _root_.scala.collection.immutable.Set(
                      ModifierName.assume("cluster"),
                      ModifierName.assume("cta"),
                      ModifierName.assume("global"),
                      ModifierName.assume("gpu"),
                      ModifierName.assume("relaxed"),
                      ModifierName.assume("release"),
                      ModifierName.assume("sys")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("multimem.st"),
                    Vec(OperandConstraint.Address, OperandConstraint.Value),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("weak")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("bf16"),
                        ModifierName.assume("bf16x2"),
                        ModifierName.assume("e4m3"),
                        ModifierName.assume("e4m3x2"),
                        ModifierName.assume("e4m3x4"),
                        ModifierName.assume("e5m2"),
                        ModifierName.assume("e5m2x2"),
                        ModifierName.assume("e5m2x4"),
                        ModifierName.assume("f16"),
                        ModifierName.assume("f16x2"),
                        ModifierName.assume("f32"),
                        ModifierName.assume("f64")
                      ).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName.assume("global")),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("multimem.st"),
                    Vec(OperandConstraint.Address, OperandConstraint.Value),
                    Vec(_root_.scala.collection.immutable.Set(
                      ModifierName.assume("bf16"),
                      ModifierName.assume("bf16x2"),
                      ModifierName.assume("e4m3"),
                      ModifierName.assume("e4m3x2"),
                      ModifierName.assume("e4m3x4"),
                      ModifierName.assume("e5m2"),
                      ModifierName.assume("e5m2x2"),
                      ModifierName.assume("e5m2x4"),
                      ModifierName.assume("f16"),
                      ModifierName.assume("f16x2"),
                      ModifierName.assume("f32"),
                      ModifierName.assume("f64")
                    ).assume[MinLength[1]]),
                    _root_.scala.collection.immutable.Set(
                      ModifierName.assume("cluster"),
                      ModifierName.assume("cta"),
                      ModifierName.assume("global"),
                      ModifierName.assume("gpu"),
                      ModifierName.assume("relaxed"),
                      ModifierName.assume("release"),
                      ModifierName.assume("sys"),
                      ModifierName.assume("v2"),
                      ModifierName.assume("v4"),
                      ModifierName.assume("v8")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("multimem.st"),
                    Vec(OperandConstraint.Address, OperandConstraint.Value),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("weak")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("bf16"),
                        ModifierName.assume("bf16x2"),
                        ModifierName.assume("e4m3"),
                        ModifierName.assume("e4m3x2"),
                        ModifierName.assume("e4m3x4"),
                        ModifierName.assume("e5m2"),
                        ModifierName.assume("e5m2x2"),
                        ModifierName.assume("e5m2x4"),
                        ModifierName.assume("f16"),
                        ModifierName.assume("f16x2"),
                        ModifierName.assume("f32"),
                        ModifierName.assume("f64")
                      ).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName.assume("global"),
                      ModifierName.assume("v2"),
                      ModifierName.assume("v4"),
                      ModifierName.assume("v8")
                    ),
                    false
                  )
                )
              )
            ).assume[MinLength[1]])
        )
    case MultimemStAsync extends Definition(
          Opcode.assume("multimem.st.async"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 2) Right(Instruction.MultimemStAsync(operands(0), operands(1), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("multimem.st.async", Vec(2), operands.size))
          },
          () =>
            Metadata(Vec(
              Metadata.Entry(
                "data-movement-and-conversion-instructions-multimem-st-async".assume[MinLength[1] & NoNul],
                "9.7.9.13. Data Movement and Conversion Instructions: multimem.st.async".assume[MinLength[1] & NoNul],
                "multimem.st.async".assume[MinLength[1] & NoNul],
                "Data Movement and Conversion Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "multimem.st.async.sem.scope{.ss}.type [a], b;\n\n.sem   = { .release };\n.scope = { .gpu, .sys };\n.ss    = { .global };\n.type  = { .b8, .b16, .b32, .b64,\n            .u8, .u16, .u32, .u64,\n            .s8, .s16, .s32, .s64,\n            .f32, .f64 };"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(9, 3),
                Target(100),
                Vec(InstructionForm(
                  Opcode.assume("multimem.st.async"),
                  Vec(OperandConstraint.Address, OperandConstraint.Value),
                  Vec(
                    _root_.scala.collection.immutable.Set(ModifierName.assume("release")).assume[MinLength[1]],
                    _root_.scala.collection.immutable.Set(
                      ModifierName.assume("gpu"),
                      ModifierName.assume("sys")
                    ).assume[MinLength[1]],
                    _root_.scala.collection.immutable.Set(
                      ModifierName.assume("b16"),
                      ModifierName.assume("b32"),
                      ModifierName.assume("b64"),
                      ModifierName.assume("b8"),
                      ModifierName.assume("f32"),
                      ModifierName.assume("f64"),
                      ModifierName.assume("s16"),
                      ModifierName.assume("s32"),
                      ModifierName.assume("s64"),
                      ModifierName.assume("s8"),
                      ModifierName.assume("u16"),
                      ModifierName.assume("u32"),
                      ModifierName.assume("u64"),
                      ModifierName.assume("u8")
                    ).assume[MinLength[1]]
                  ),
                  _root_.scala.collection.immutable.Set(ModifierName.assume("global")),
                  true
                ))
              )
            ).assume[MinLength[1]])
        )
    case NanoSleep extends Definition(
          Opcode.assume("nanosleep"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 1) Right(Instruction.NanoSleep(operands(0), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("nanosleep", Vec(1), operands.size))
          },
          () =>
            Metadata(Vec(
              Metadata.Entry(
                "miscellaneous-instructions-nanosleep".assume[MinLength[1] & NoNul],
                "9.7.20.2. Miscellaneous Instructions: nanosleep".assume[MinLength[1] & NoNul],
                "nanosleep".assume[MinLength[1] & NoNul],
                "Miscellaneous Instructions".assume[MinLength[1] & NoNul],
                Vec[String]("nanosleep.u32 t;").map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(1, 0),
                Target(70),
                Vec(InstructionForm(
                  Opcode.assume("nanosleep"),
                  Vec(OperandConstraint.Value),
                  Vec(_root_.scala.collection.immutable.Set(ModifierName.assume("u32")).assume[MinLength[1]]),
                  _root_.scala.collection.immutable.Set(),
                  true
                ))
              )
            ).assume[MinLength[1]])
        )
    case Neg extends Definition(
          Opcode.assume("neg"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 2) Right(Instruction.Neg(operands(0), operands(1), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("neg", Vec(2), operands.size))
          },
          () =>
            Metadata(Vec(
              Metadata.Entry(
                "integer-arithmetic-instructions-neg".assume[MinLength[1] & NoNul],
                "9.7.1.12. Integer Arithmetic Instructions: neg".assume[MinLength[1] & NoNul],
                "neg".assume[MinLength[1] & NoNul],
                "Integer Arithmetic Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "neg.type  d, a;\n\n.type = { .s8x4, .s16, .s32, .s64 };"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(1, 0),
                Target(10),
                Vec(InstructionForm(
                  Opcode.assume("neg"),
                  Vec(OperandConstraint.Register, OperandConstraint.Value),
                  Vec(_root_.scala.collection.immutable.Set(
                    ModifierName.assume("s16"),
                    ModifierName.assume("s32"),
                    ModifierName.assume("s64"),
                    ModifierName.assume("s8x4")
                  ).assume[MinLength[1]]),
                  _root_.scala.collection.immutable.Set(),
                  false
                ))
              ),
              Metadata.Entry(
                "floating-point-instructions-neg".assume[MinLength[1] & NoNul],
                "9.7.3.10. Floating Point Instructions: neg".assume[MinLength[1] & NoNul],
                "neg".assume[MinLength[1] & NoNul],
                "Floating Point Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "neg{.ftz}.f32  d, a;\nneg.f64        d, a;"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(1, 0),
                Target(13),
                Vec(
                  InstructionForm(
                    Opcode.assume("neg"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(_root_.scala.collection.immutable.Set(ModifierName.assume("f32")).assume[MinLength[1]]),
                    _root_.scala.collection.immutable.Set(ModifierName.assume("ftz")),
                    true
                  ),
                  InstructionForm(
                    Opcode.assume("neg"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(_root_.scala.collection.immutable.Set(ModifierName.assume("f64")).assume[MinLength[1]]),
                    _root_.scala.collection.immutable.Set(),
                    true
                  )
                )
              ),
              Metadata.Entry(
                "half-precision-floating-point-instructions-neg".assume[MinLength[1] & NoNul],
                "9.7.4.5. Half Precision Floating Point Instructions: neg".assume[MinLength[1] & NoNul],
                "neg".assume[MinLength[1] & NoNul],
                "Half Precision Floating Point Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "neg{.ftz}.f16    d, a;\nneg{.ftz}.f16x2  d, a;\nneg.bf16         d, a;\nneg.bf16x2       d, a;"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(6, 0),
                Target(53),
                Vec(
                  InstructionForm(
                    Opcode.assume("neg"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(_root_.scala.collection.immutable.Set(ModifierName.assume("f16")).assume[MinLength[1]]),
                    _root_.scala.collection.immutable.Set(ModifierName.assume("ftz")),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("neg"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(_root_.scala.collection.immutable.Set(ModifierName.assume("f16x2")).assume[MinLength[1]]),
                    _root_.scala.collection.immutable.Set(ModifierName.assume("ftz")),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("neg"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(_root_.scala.collection.immutable.Set(ModifierName.assume("bf16")).assume[MinLength[1]]),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("neg"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(_root_.scala.collection.immutable.Set(ModifierName.assume("bf16x2")).assume[MinLength[1]]),
                    _root_.scala.collection.immutable.Set(),
                    false
                  )
                )
              )
            ).assume[MinLength[1]])
        )
    case Not extends Definition(
          Opcode.assume("not"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 2) Right(Instruction.Not(operands(0), operands(1), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("not", Vec(2), operands.size))
          },
          () =>
            Metadata(Vec(
              Metadata.Entry(
                "logic-and-shift-instructions-not".assume[MinLength[1] & NoNul],
                "9.7.8.4. Logic and Shift Instructions: not".assume[MinLength[1] & NoNul],
                "not".assume[MinLength[1] & NoNul],
                "Logic and Shift Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "not.type d, a;\n\n.type = { .pred, .b16, .b32, .b64 };"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(1, 0),
                Target(10),
                Vec(InstructionForm(
                  Opcode.assume("not"),
                  Vec(OperandConstraint.Register, OperandConstraint.Value),
                  Vec(_root_.scala.collection.immutable.Set(
                    ModifierName.assume("b16"),
                    ModifierName.assume("b32"),
                    ModifierName.assume("b64"),
                    ModifierName.assume("pred")
                  ).assume[MinLength[1]]),
                  _root_.scala.collection.immutable.Set(),
                  true
                ))
              )
            ).assume[MinLength[1]])
        )
    case Or extends Definition(
          Opcode.assume("or"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 3) Right(Instruction.Or(operands(0), operands(1), operands(2), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("or", Vec(3), operands.size))
          },
          () =>
            Metadata(Vec(
              Metadata.Entry(
                "logic-and-shift-instructions-or".assume[MinLength[1] & NoNul],
                "9.7.8.2. Logic and Shift Instructions: or".assume[MinLength[1] & NoNul],
                "or".assume[MinLength[1] & NoNul],
                "Logic and Shift Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "or.type d, a, b;\n\n.type = { .pred, .b16, .b32, .b64 };"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(1, 0),
                Target(10),
                Vec(InstructionForm(
                  Opcode.assume("or"),
                  Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                  Vec(_root_.scala.collection.immutable.Set(
                    ModifierName.assume("b16"),
                    ModifierName.assume("b32"),
                    ModifierName.assume("b64"),
                    ModifierName.assume("pred")
                  ).assume[MinLength[1]]),
                  _root_.scala.collection.immutable.Set(),
                  true
                ))
              )
            ).assume[MinLength[1]])
        )
    case PmEvent extends Definition(
          Opcode.assume("pmevent"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 1) Right(Instruction.PmEvent(operands(0), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("pmevent", Vec(1), operands.size))
          },
          () =>
            Metadata(Vec(
              Metadata.Entry(
                "miscellaneous-instructions-pmevent".assume[MinLength[1] & NoNul],
                "9.7.20.3. Miscellaneous Instructions: pmevent".assume[MinLength[1] & NoNul],
                "pmevent".assume[MinLength[1] & NoNul],
                "Miscellaneous Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "pmevent       a;    // trigger a single performance monitor event\npmevent.mask  a;    // trigger one or more performance monitor events"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(1, 0),
                Target(20),
                Vec(
                  InstructionForm(
                    Opcode.assume("pmevent"),
                    Vec(OperandConstraint.Value),
                    Vec(),
                    _root_.scala.collection.immutable.Set(),
                    true
                  ),
                  InstructionForm(
                    Opcode.assume("pmevent"),
                    Vec(OperandConstraint.Value),
                    Vec(_root_.scala.collection.immutable.Set(ModifierName.assume("mask")).assume[MinLength[1]]),
                    _root_.scala.collection.immutable.Set(),
                    true
                  )
                )
              )
            ).assume[MinLength[1]])
        )
    case Popc extends Definition(
          Opcode.assume("popc"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 2) Right(Instruction.Popc(operands(0), operands(1), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("popc", Vec(2), operands.size))
          },
          () =>
            Metadata(Vec(
              Metadata.Entry(
                "integer-arithmetic-instructions-popc".assume[MinLength[1] & NoNul],
                "9.7.1.15. Integer Arithmetic Instructions: popc".assume[MinLength[1] & NoNul],
                "popc".assume[MinLength[1] & NoNul],
                "Integer Arithmetic Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "popc.type  d, a;\n\n.type = { .b32, .b64 };"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(2, 0),
                Target(20),
                Vec(InstructionForm(
                  Opcode.assume("popc"),
                  Vec(OperandConstraint.Register, OperandConstraint.Value),
                  Vec(_root_.scala.collection.immutable.Set(
                    ModifierName.assume("b32"),
                    ModifierName.assume("b64")
                  ).assume[MinLength[1]]),
                  _root_.scala.collection.immutable.Set(),
                  true
                ))
              )
            ).assume[MinLength[1]])
        )
    case Prefetch extends Definition(
          Opcode.assume("prefetch"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 1) Right(Instruction.Prefetch(operands(0), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("prefetch", Vec(1), operands.size))
          },
          () =>
            Metadata(Vec(
              Metadata.Entry(
                "data-movement-and-conversion-instructions-prefetch-prefetchu".assume[MinLength[1] & NoNul],
                "9.7.9.16. Data Movement and Conversion Instructions: prefetch, prefetchu".assume[MinLength[1] & NoNul],
                "prefetch, prefetchu".assume[MinLength[1] & NoNul],
                "Data Movement and Conversion Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "prefetch{.space}.level                    [a];   // prefetch to data cache\nprefetch.global.level::eviction_priority  [a];   // prefetch to data cache\n\nprefetchu.L1  [a];             // prefetch to uniform cache\n\nprefetch{.tensormap_space}.tensormap [a];  // prefetch the tensormap\n\n.space =                    { .global, .local };\n.level =                    { .L1, .L2 };\n.level::eviction_priority = { .L2::evict_last, .L2::evict_normal };\n.tensormap_space =          { .const, .param };"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(2, 0),
                Target(20),
                Vec(
                  InstructionForm(
                    Opcode.assume("prefetch"),
                    Vec(OperandConstraint.Address),
                    Vec(_root_.scala.collection.immutable.Set(
                      ModifierName.assume("L1"),
                      ModifierName.assume("L2")
                    ).assume[MinLength[1]]),
                    _root_.scala.collection.immutable.Set(ModifierName.assume("global"), ModifierName.assume("local")),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("prefetch"),
                    Vec(OperandConstraint.Address),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("global")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("L2::evict_last"),
                        ModifierName.assume("L2::evict_normal")
                      ).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("prefetch"),
                    Vec(OperandConstraint.Address),
                    Vec(_root_.scala.collection.immutable.Set(ModifierName.assume("tensormap")).assume[MinLength[1]]),
                    _root_.scala.collection.immutable.Set(ModifierName.assume("const"), ModifierName.assume("param")),
                    false
                  )
                )
              )
            ).assume[MinLength[1]])
        )
    case Prefetchu extends Definition(
          Opcode.assume("prefetchu"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 1) Right(Instruction.Prefetchu(operands(0), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("prefetchu", Vec(1), operands.size))
          },
          () =>
            Metadata(Vec(
              Metadata.Entry(
                "data-movement-and-conversion-instructions-prefetch-prefetchu".assume[MinLength[1] & NoNul],
                "9.7.9.16. Data Movement and Conversion Instructions: prefetch, prefetchu".assume[MinLength[1] & NoNul],
                "prefetch, prefetchu".assume[MinLength[1] & NoNul],
                "Data Movement and Conversion Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "prefetch{.space}.level                    [a];   // prefetch to data cache\nprefetch.global.level::eviction_priority  [a];   // prefetch to data cache\n\nprefetchu.L1  [a];             // prefetch to uniform cache\n\nprefetch{.tensormap_space}.tensormap [a];  // prefetch the tensormap\n\n.space =                    { .global, .local };\n.level =                    { .L1, .L2 };\n.level::eviction_priority = { .L2::evict_last, .L2::evict_normal };\n.tensormap_space =          { .const, .param };"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(2, 0),
                Target(20),
                Vec(InstructionForm(
                  Opcode.assume("prefetchu"),
                  Vec(OperandConstraint.Address),
                  Vec(_root_.scala.collection.immutable.Set(ModifierName.assume("L1")).assume[MinLength[1]]),
                  _root_.scala.collection.immutable.Set(),
                  false
                ))
              )
            ).assume[MinLength[1]])
        )
    case Prmt extends Definition(
          Opcode.assume("prmt"),
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
            Metadata(Vec(
              Metadata.Entry(
                "data-movement-and-conversion-instructions-prmt".assume[MinLength[1] & NoNul],
                "9.7.9.7. Data Movement and Conversion Instructions: prmt".assume[MinLength[1] & NoNul],
                "prmt".assume[MinLength[1] & NoNul],
                "Data Movement and Conversion Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "prmt.b32{.mode}  d, a, b, c;\n\n.mode = { .f4e, .b4e, .rc8, .ecl, .ecr, .rc16 };"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(2, 0),
                Target(20),
                Vec(InstructionForm(
                  Opcode.assume("prmt"),
                  Vec(
                    OperandConstraint.Register,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Vec(_root_.scala.collection.immutable.Set(ModifierName.assume("b32")).assume[MinLength[1]]),
                  _root_.scala.collection.immutable.Set(
                    ModifierName.assume("b4e"),
                    ModifierName.assume("ecl"),
                    ModifierName.assume("ecr"),
                    ModifierName.assume("f4e"),
                    ModifierName.assume("rc16"),
                    ModifierName.assume("rc8")
                  ),
                  true
                ))
              )
            ).assume[MinLength[1]])
        )
    case Rcp extends Definition(
          Opcode.assume("rcp"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 2) Right(Instruction.Rcp(operands(0), operands(1), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("rcp", Vec(2), operands.size))
          },
          () =>
            Metadata(Vec(
              Metadata.Entry(
                "floating-point-instructions-rcp".assume[MinLength[1] & NoNul],
                "9.7.3.13. Floating Point Instructions: rcp".assume[MinLength[1] & NoNul],
                "rcp".assume[MinLength[1] & NoNul],
                "Floating Point Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "rcp.approx{.ftz}.f32  d, a;  // fast, approximate reciprocal\nrcp.rnd{.ftz}.f32     d, a;  // IEEE 754 compliant rounding\nrcp.rnd.f64           d, a;  // IEEE 754 compliant rounding\n\n.rnd = { .rn, .rz, .rm, .rp };"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(1, 0),
                Target(13),
                Vec(
                  InstructionForm(
                    Opcode.assume("rcp"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("approx")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("f32")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName.assume("ftz")),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("rcp"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("rm"),
                        ModifierName.assume("rn"),
                        ModifierName.assume("rp"),
                        ModifierName.assume("rz")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("f32")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName.assume("ftz")),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("rcp"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("rm"),
                        ModifierName.assume("rn"),
                        ModifierName.assume("rp"),
                        ModifierName.assume("rz")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("f64")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  )
                )
              )
            ).assume[MinLength[1]])
        )
    case RcpApproxFtzF64 extends Definition(
          Opcode.assume("rcp.approx.ftz.f64"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 2) Right(Instruction.RcpApproxFtzF64(operands(0), operands(1), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("rcp.approx.ftz.f64", Vec(2), operands.size))
          },
          () =>
            Metadata(Vec(
              Metadata.Entry(
                "floating-point-instructions-rcp-approx-ftz-f64".assume[MinLength[1] & NoNul],
                "9.7.3.14. Floating Point Instructions: rcp.approx.ftz.f64".assume[MinLength[1] & NoNul],
                "rcp.approx.ftz.f64".assume[MinLength[1] & NoNul],
                "Floating Point Instructions".assume[MinLength[1] & NoNul],
                Vec[String]("rcp.approx.ftz.f64  d, a;").map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(1, 0),
                Target(20),
                Vec(InstructionForm(
                  Opcode.assume("rcp.approx.ftz.f64"),
                  Vec(OperandConstraint.Register, OperandConstraint.Value),
                  Vec(),
                  _root_.scala.collection.immutable.Set(),
                  true
                ))
              )
            ).assume[MinLength[1]])
        )
    case Red extends Definition(
          Opcode.assume("red"),
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
            Metadata(Vec(
              Metadata.Entry(
                "parallel-synchronization-and-communication-instructions-red".assume[MinLength[1] & NoNul],
                "9.7.14.6. Parallel Synchronization and Communication Instructions: red".assume[MinLength[1] & NoNul],
                "red".assume[MinLength[1] & NoNul],
                "Parallel Synchronization and Communication Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "red{.sem}{.scope}{.space}.op{.level::cache_hint}.type          [a], b{, cache_policy};\n\nred{.sem}{.scope}{.space}.add.noftz{.level::cache_hint}.f16    [a], b{, cache_policy};\n\nred{.sem}{.scope}{.space}.add.noftz{.level::cache_hint}.f16x2  [a], b{, cache_policy};\n\nred{.sem}{.scope}{.space}.add.noftz{.level::cache_hint}.bf16\n                                                      [a], b {, cache_policy};\n\nred{.sem}{.scope}{.space}.add.noftz{.level::cache_hint}.bf16x2\n                                                      [a], b {, cache_policy};\n\n.space =              { .global, .shared{::cta, ::cluster} };\n.sem =                { .relaxed, .release };\n.scope =              { .cta, .cluster, .gpu, .sys };\n\n.op =                 { .and, .or, .xor,\n                        .add, .inc, .dec,\n                        .min, .max };\n.level::cache_hint =  { .L2::cache_hint };\n.type =               { .b32, .b64, .u32, .u64, .s32, .s64, .f32, .f64 };",
                  "red{.sem}{.scope}{.global}.add{.level::cache_hint}.vec_32_bit.f32 [a], b{, cache_policy};\nred{.sem}{.scope}{.global}.op.noftz{.level::cache_hint}. vec_16_bit.half_word_type [a], b{, cache_policy};\nred{.sem}{.scope}{.global}.op.noftz{.level::cache_hint}.vec_32_bit.packed_type [a], b {, cache_policy};\n\n.sem =                { .relaxed, .release };\n.scope =              { .cta, .cluster, .gpu, .sys };\n.op =                 { .add, .min, .max };\n.half_word_type =     { .f16, .bf16 };\n.packed_type =        { .f16x2,.bf16x2 };\n.vec_16_bit =         { .v2, .v4, .v8 }\n.vec_32_bit =         { .v2, .v4 };\n.level::cache_hint =  { .L2::cache_hint }"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(1, 2),
                Target(11),
                Vec(
                  InstructionForm(
                    Opcode.assume("red"),
                    Vec(OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("add"),
                        ModifierName.assume("and"),
                        ModifierName.assume("dec"),
                        ModifierName.assume("inc"),
                        ModifierName.assume("max"),
                        ModifierName.assume("min"),
                        ModifierName.assume("or"),
                        ModifierName.assume("xor")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("b32"),
                        ModifierName.assume("b64"),
                        ModifierName.assume("f32"),
                        ModifierName.assume("f64"),
                        ModifierName.assume("s32"),
                        ModifierName.assume("s64"),
                        ModifierName.assume("u32"),
                        ModifierName.assume("u64")
                      ).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName.assume("L2::cache_hint"),
                      ModifierName.assume("cluster"),
                      ModifierName.assume("cta"),
                      ModifierName.assume("global"),
                      ModifierName.assume("gpu"),
                      ModifierName.assume("relaxed"),
                      ModifierName.assume("release"),
                      ModifierName.assume("sys")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("red"),
                    Vec(OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("add")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("noftz")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("f16")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName.assume("L2::cache_hint"),
                      ModifierName.assume("cluster"),
                      ModifierName.assume("cta"),
                      ModifierName.assume("global"),
                      ModifierName.assume("gpu"),
                      ModifierName.assume("relaxed"),
                      ModifierName.assume("release"),
                      ModifierName.assume("sys")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("red"),
                    Vec(OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("add")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("noftz")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("f16x2")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName.assume("L2::cache_hint"),
                      ModifierName.assume("cluster"),
                      ModifierName.assume("cta"),
                      ModifierName.assume("global"),
                      ModifierName.assume("gpu"),
                      ModifierName.assume("relaxed"),
                      ModifierName.assume("release"),
                      ModifierName.assume("sys")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("red"),
                    Vec(OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("add")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("noftz")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("bf16")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName.assume("L2::cache_hint"),
                      ModifierName.assume("cluster"),
                      ModifierName.assume("cta"),
                      ModifierName.assume("global"),
                      ModifierName.assume("gpu"),
                      ModifierName.assume("relaxed"),
                      ModifierName.assume("release"),
                      ModifierName.assume("sys")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("red"),
                    Vec(OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("add")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("noftz")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("bf16x2")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName.assume("L2::cache_hint"),
                      ModifierName.assume("cluster"),
                      ModifierName.assume("cta"),
                      ModifierName.assume("global"),
                      ModifierName.assume("gpu"),
                      ModifierName.assume("relaxed"),
                      ModifierName.assume("release"),
                      ModifierName.assume("sys")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("red"),
                    Vec(OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("add")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("v2"),
                        ModifierName.assume("v4")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("f32")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName.assume("L2::cache_hint"),
                      ModifierName.assume("cluster"),
                      ModifierName.assume("cta"),
                      ModifierName.assume("global"),
                      ModifierName.assume("gpu"),
                      ModifierName.assume("relaxed"),
                      ModifierName.assume("release"),
                      ModifierName.assume("sys")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("red"),
                    Vec(OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("add"),
                        ModifierName.assume("max"),
                        ModifierName.assume("min")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("noftz")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName.assume("L2::cache_hint"),
                      ModifierName.assume("cluster"),
                      ModifierName.assume("cta"),
                      ModifierName.assume("global"),
                      ModifierName.assume("gpu"),
                      ModifierName.assume("relaxed"),
                      ModifierName.assume("release"),
                      ModifierName.assume("sys")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("red"),
                    Vec(OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("add"),
                        ModifierName.assume("max"),
                        ModifierName.assume("min")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("noftz")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("v2"),
                        ModifierName.assume("v4")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("bf16x2"),
                        ModifierName.assume("f16x2")
                      ).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName.assume("L2::cache_hint"),
                      ModifierName.assume("cluster"),
                      ModifierName.assume("cta"),
                      ModifierName.assume("global"),
                      ModifierName.assume("gpu"),
                      ModifierName.assume("relaxed"),
                      ModifierName.assume("release"),
                      ModifierName.assume("sys")
                    ),
                    false
                  )
                )
              )
            ).assume[MinLength[1]])
        )
    case RedAsync extends Definition(
          Opcode.assume("red.async"),
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
            Metadata(Vec(
              Metadata.Entry(
                "parallel-synchronization-and-communication-instructions-red-async".assume[MinLength[1] & NoNul],
                "9.7.14.7. Parallel Synchronization and Communication Instructions: red.async".assume[MinLength[
                  1
                ] & NoNul],
                "red.async".assume[MinLength[1] & NoNul],
                "Parallel Synchronization and Communication Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "// Increment and Decrement reductions\nred.async.sem.scope{.ss}.completion_mechanism.op.type [a], b, [mbar];\n\n.sem  =                 { .relaxed };\n.scope =                { .cluster };\n.ss   =                 { .shared::cluster };\n.op   =                 { .inc, .dec };\n.type =                 { .u32 };\n.completion_mechanism = { .mbarrier::complete_tx::bytes };\n\n\n// MIN and MAX reductions\nred.async.sem.scope{.ss}.completion_mechanism.op.type [a], b, [mbar];\n\n.sem  = { .relaxed };\n.scope = { .cluster };\n.ss   = { .shared::cluster };\n.op   = { .min, .max };\n.type = { .u32, .s32 };\n.completion_mechanism = { .mbarrier::complete_tx::bytes };\n\n// Bitwise AND, OR and XOR reductions\nred.async.sem.scope{.ss}.completion_mechanism.op.type [a], b, [mbar];\n\n.sem  = { .relaxed };\n.scope = { .cluster };\n.ss   = { .shared::cluster };\n.op   = { .and, .or, .xor };\n.type = { .b32 };\n.completion_mechanism = { .mbarrier::complete_tx::bytes };\n\n// ADD reductions\nred.async.sem.scope{.ss}.completion_mechanism.add.type [a], b, [mbar];\n\n.sem  = { .relaxed };\n.scope = { .cluster };\n.ss   = { .shared::cluster };\n.type = { .u32, .s32, .u64 };\n.completion_mechanism = { .mbarrier::complete_tx::bytes };\n\nred.async{.mmio}.sem.scope{.ss}.add.type [a], b;\n\n.sem  = { .release };\n.scope = { .gpu, .sys };\n.ss   = { .global };\n.type = { .u32, .s32, .u64, .s64 };"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(8, 1),
                Target(90),
                Vec(
                  InstructionForm(
                    Opcode.assume("red.async"),
                    Vec(OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Address),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("release")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("gpu"),
                        ModifierName.assume("sys")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume(
                        "mbarrier::complete_tx::bytes"
                      )).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("and"),
                        ModifierName.assume("or"),
                        ModifierName.assume("xor")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("s32"),
                        ModifierName.assume("s64"),
                        ModifierName.assume("u32"),
                        ModifierName.assume("u64")
                      ).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName.assume("global")),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("red.async"),
                    Vec(OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Address),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("release")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("gpu"),
                        ModifierName.assume("sys")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume(
                        "mbarrier::complete_tx::bytes"
                      )).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("add")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("s32"),
                        ModifierName.assume("s64"),
                        ModifierName.assume("u32"),
                        ModifierName.assume("u64")
                      ).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName.assume("global")),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("red.async"),
                    Vec(OperandConstraint.Address, OperandConstraint.Value),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("release")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("gpu"),
                        ModifierName.assume("sys")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("add")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("s32"),
                        ModifierName.assume("s64"),
                        ModifierName.assume("u32"),
                        ModifierName.assume("u64")
                      ).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName.assume("global"), ModifierName.assume("mmio")),
                    false
                  )
                )
              )
            ).assume[MinLength[1]])
        )
    case ReduxSync extends Definition(
          Opcode.assume("redux.sync"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 3) Right(Instruction.ReduxSync(operands(0), operands(1), operands(2), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("redux.sync", Vec(3), operands.size))
          },
          () =>
            Metadata(Vec(
              Metadata.Entry(
                "parallel-synchronization-and-communication-instructions-redux-sync".assume[MinLength[1] & NoNul],
                "9.7.14.13. Parallel Synchronization and Communication Instructions: redux.sync".assume[MinLength[
                  1
                ] & NoNul],
                "redux.sync".assume[MinLength[1] & NoNul],
                "Parallel Synchronization and Communication Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "redux.sync.op.type dst, src, membermask;\n.op   = {.add, .min, .max}\n.type = {.u32, .s32}\n\nredux.sync.op.b32 dst, src, membermask;\n.op   = {.and, .or, .xor}\n\nredux.sync.op{.abs.}{.NaN}.f32 dst, src, membermask;\n.op   = { .min, .max }"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(7, 0),
                Target(80),
                Vec(
                  InstructionForm(
                    Opcode.assume("redux.sync"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("max"),
                        ModifierName.assume("min")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("s32"),
                        ModifierName.assume("u32")
                      ).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("redux.sync"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("max"),
                        ModifierName.assume("min")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("b32")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("redux.sync"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("max"),
                        ModifierName.assume("min")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("f32")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName.assume("NaN"), ModifierName.assume("abs")),
                    false
                  )
                )
              )
            ).assume[MinLength[1]])
        )
    case Rem extends Definition(
          Opcode.assume("rem"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 3) Right(Instruction.Rem(operands(0), operands(1), operands(2), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("rem", Vec(3), operands.size))
          },
          () =>
            Metadata(Vec(
              Metadata.Entry(
                "integer-arithmetic-instructions-rem".assume[MinLength[1] & NoNul],
                "9.7.1.10. Integer Arithmetic Instructions: rem".assume[MinLength[1] & NoNul],
                "rem".assume[MinLength[1] & NoNul],
                "Integer Arithmetic Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "rem.type  d, a, b;\n\n.type = { .u16, .u32, .u64,\n          .s16, .s32, .s64 };"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(1, 0),
                Target(10),
                Vec(InstructionForm(
                  Opcode.assume("rem"),
                  Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                  Vec(_root_.scala.collection.immutable.Set(
                    ModifierName.assume("s16"),
                    ModifierName.assume("s32"),
                    ModifierName.assume("s64"),
                    ModifierName.assume("u16"),
                    ModifierName.assume("u32"),
                    ModifierName.assume("u64")
                  ).assume[MinLength[1]]),
                  _root_.scala.collection.immutable.Set(),
                  true
                ))
              )
            ).assume[MinLength[1]])
        )
    case Ret extends Definition(
          Opcode.assume("ret"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 0) Right(Instruction.Ret(mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("ret", Vec(0), operands.size))
          },
          () =>
            Metadata(Vec(
              Metadata.Entry(
                "control-flow-instructions-ret".assume[MinLength[1] & NoNul],
                "9.7.13.6. Control Flow Instructions: ret".assume[MinLength[1] & NoNul],
                "ret".assume[MinLength[1] & NoNul],
                "Control Flow Instructions".assume[MinLength[1] & NoNul],
                Vec[String]("ret{.uni};").map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(1, 0),
                Target(10),
                Vec(InstructionForm(
                  Opcode.assume("ret"),
                  Vec(),
                  Vec(),
                  _root_.scala.collection.immutable.Set(ModifierName.assume("uni")),
                  true
                ))
              )
            ).assume[MinLength[1]])
        )
    case Rsqrt extends Definition(
          Opcode.assume("rsqrt"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 2) Right(Instruction.Rsqrt(operands(0), operands(1), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("rsqrt", Vec(2), operands.size))
          },
          () =>
            Metadata(Vec(
              Metadata.Entry(
                "floating-point-instructions-rsqrt".assume[MinLength[1] & NoNul],
                "9.7.3.16. Floating Point Instructions: rsqrt".assume[MinLength[1] & NoNul],
                "rsqrt".assume[MinLength[1] & NoNul],
                "Floating Point Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "rsqrt.approx{.ftz}.f32  d, a;\nrsqrt.approx.f64        d, a;"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(1, 0),
                Target(13),
                Vec(
                  InstructionForm(
                    Opcode.assume("rsqrt"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("approx")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("f32")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName.assume("ftz")),
                    true
                  ),
                  InstructionForm(
                    Opcode.assume("rsqrt"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("approx")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("f64")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(),
                    true
                  )
                )
              )
            ).assume[MinLength[1]])
        )
    case RsqrtApproxFtzF64 extends Definition(
          Opcode.assume("rsqrt.approx.ftz.f64"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 2) Right(Instruction.RsqrtApproxFtzF64(operands(0), operands(1), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("rsqrt.approx.ftz.f64", Vec(2), operands.size))
          },
          () =>
            Metadata(Vec(
              Metadata.Entry(
                "floating-point-instructions-rsqrt-approx-ftz-f64".assume[MinLength[1] & NoNul],
                "9.7.3.17. Floating Point Instructions: rsqrt.approx.ftz.f64".assume[MinLength[1] & NoNul],
                "rsqrt.approx.ftz.f64".assume[MinLength[1] & NoNul],
                "Floating Point Instructions".assume[MinLength[1] & NoNul],
                Vec[String]("rsqrt.approx.ftz.f64 d, a;").map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(1, 0),
                Target(20),
                Vec(InstructionForm(
                  Opcode.assume("rsqrt.approx.ftz.f64"),
                  Vec(OperandConstraint.Register, OperandConstraint.Value),
                  Vec(),
                  _root_.scala.collection.immutable.Set(),
                  true
                ))
              )
            ).assume[MinLength[1]])
        )
    case Sad extends Definition(
          Opcode.assume("sad"),
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
            Metadata(Vec(
              Metadata.Entry(
                "integer-arithmetic-instructions-sad".assume[MinLength[1] & NoNul],
                "9.7.1.8. Integer Arithmetic Instructions: sad".assume[MinLength[1] & NoNul],
                "sad".assume[MinLength[1] & NoNul],
                "Integer Arithmetic Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "sad.type  d, a, b, c;\n\n.type = { .u16, .u32, .u64,\n          .s16, .s32, .s64 };"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(1, 0),
                Target(10),
                Vec(InstructionForm(
                  Opcode.assume("sad"),
                  Vec(
                    OperandConstraint.Register,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Vec(_root_.scala.collection.immutable.Set(
                    ModifierName.assume("s16"),
                    ModifierName.assume("s32"),
                    ModifierName.assume("s64"),
                    ModifierName.assume("u16"),
                    ModifierName.assume("u32"),
                    ModifierName.assume("u64")
                  ).assume[MinLength[1]]),
                  _root_.scala.collection.immutable.Set(),
                  true
                ))
              )
            ).assume[MinLength[1]])
        )
    case Selp extends Definition(
          Opcode.assume("selp"),
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
            Metadata(Vec(
              Metadata.Entry(
                "comparison-and-selection-instructions-selp".assume[MinLength[1] & NoNul],
                "9.7.6.3. Comparison and Selection Instructions: selp".assume[MinLength[1] & NoNul],
                "selp".assume[MinLength[1] & NoNul],
                "Comparison and Selection Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "selp.type d, a, b, c;\n\n.type = { .b16, .b32, .b64,\n          .u16, .u32, .u64,\n          .s16, .s32, .s64,\n                .f32, .f64 };"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(1, 0),
                Target(13),
                Vec(InstructionForm(
                  Opcode.assume("selp"),
                  Vec(
                    OperandConstraint.Register,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Vec(_root_.scala.collection.immutable.Set(
                    ModifierName.assume("b16"),
                    ModifierName.assume("b32"),
                    ModifierName.assume("b64"),
                    ModifierName.assume("f32"),
                    ModifierName.assume("f64"),
                    ModifierName.assume("s16"),
                    ModifierName.assume("s32"),
                    ModifierName.assume("s64"),
                    ModifierName.assume("u16"),
                    ModifierName.assume("u32"),
                    ModifierName.assume("u64")
                  ).assume[MinLength[1]]),
                  _root_.scala.collection.immutable.Set(),
                  true
                ))
              )
            ).assume[MinLength[1]])
        )
    case Set extends Definition(
          Opcode.assume("set"),
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
            Metadata(Vec(
              Metadata.Entry(
                "comparison-and-selection-instructions-set".assume[MinLength[1] & NoNul],
                "9.7.6.1. Comparison and Selection Instructions: set".assume[MinLength[1] & NoNul],
                "set".assume[MinLength[1] & NoNul],
                "Comparison and Selection Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "set.CmpOp{.ftz}.dtype.stype         d, a, b;\nset.CmpOp.BoolOp{.ftz}.dtype.stype  d, a, b, {!}c;\n\n.CmpOp  = { eq, ne, lt, le, gt, ge, lo, ls, hi, hs,\n            equ, neu, ltu, leu, gtu, geu, num, nan };\n.BoolOp = { and, or, xor };\n.dtype  = { .u32, .s32, .f32 };\n.stype  = { .b16, .b32, .b64,\n            .u16, .u32, .u64,\n            .s16, .s32, .s64,\n                  .f32, .f64 };"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(1, 0),
                Target(13),
                Vec(
                  InstructionForm(
                    Opcode.assume("set"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("eq"),
                        ModifierName.assume("equ"),
                        ModifierName.assume("ge"),
                        ModifierName.assume("geu"),
                        ModifierName.assume("gt"),
                        ModifierName.assume("gtu"),
                        ModifierName.assume("hi"),
                        ModifierName.assume("hs"),
                        ModifierName.assume("le"),
                        ModifierName.assume("leu"),
                        ModifierName.assume("lo"),
                        ModifierName.assume("ls"),
                        ModifierName.assume("lt"),
                        ModifierName.assume("ltu"),
                        ModifierName.assume("nan"),
                        ModifierName.assume("ne"),
                        ModifierName.assume("neu"),
                        ModifierName.assume("num")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("f32"),
                        ModifierName.assume("s32"),
                        ModifierName.assume("u32")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("b16"),
                        ModifierName.assume("b32"),
                        ModifierName.assume("b64"),
                        ModifierName.assume("f32"),
                        ModifierName.assume("f64"),
                        ModifierName.assume("s16"),
                        ModifierName.assume("s32"),
                        ModifierName.assume("s64"),
                        ModifierName.assume("u16"),
                        ModifierName.assume("u32"),
                        ModifierName.assume("u64")
                      ).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName.assume("ftz")),
                    true
                  ),
                  InstructionForm(
                    Opcode.assume("set"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("eq"),
                        ModifierName.assume("equ"),
                        ModifierName.assume("ge"),
                        ModifierName.assume("geu"),
                        ModifierName.assume("gt"),
                        ModifierName.assume("gtu"),
                        ModifierName.assume("hi"),
                        ModifierName.assume("hs"),
                        ModifierName.assume("le"),
                        ModifierName.assume("leu"),
                        ModifierName.assume("lo"),
                        ModifierName.assume("ls"),
                        ModifierName.assume("lt"),
                        ModifierName.assume("ltu"),
                        ModifierName.assume("nan"),
                        ModifierName.assume("ne"),
                        ModifierName.assume("neu"),
                        ModifierName.assume("num")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("and"),
                        ModifierName.assume("or"),
                        ModifierName.assume("xor")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("f32"),
                        ModifierName.assume("s32"),
                        ModifierName.assume("u32")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("b16"),
                        ModifierName.assume("b32"),
                        ModifierName.assume("b64"),
                        ModifierName.assume("f32"),
                        ModifierName.assume("f64"),
                        ModifierName.assume("s16"),
                        ModifierName.assume("s32"),
                        ModifierName.assume("s64"),
                        ModifierName.assume("u16"),
                        ModifierName.assume("u32"),
                        ModifierName.assume("u64")
                      ).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName.assume("ftz")),
                    true
                  )
                )
              ),
              Metadata.Entry(
                "half-precision-comparison-instructions-set".assume[MinLength[1] & NoNul],
                "9.7.7.1. Half Precision Comparison Instructions: set".assume[MinLength[1] & NoNul],
                "set".assume[MinLength[1] & NoNul],
                "Half Precision Comparison Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "set.CmpOp{.ftz}.f16.stype            d, a, b;\nset.CmpOp.BoolOp{.ftz}.f16.stype     d, a, b, {!}c;\n\nset.CmpOp.bf16.stype                 d, a, b;\nset.CmpOp.BoolOp.bf16.stype          d, a, b, {!}c;\n\nset.CmpOp{.ftz}.dtype.f16            d, a, b;\nset.CmpOp.BoolOp{.ftz}.dtype.f16     d, a, b, {!}c;\n.dtype  = { .u16, .s16, .u32, .s32}\n\nset.CmpOp.dtype.bf16                 d, a, b;\nset.CmpOp.BoolOp.dtype.bf16          d, a, b, {!}c;\n.dtype  = { .u16, .s16, .u32, .s32}\n\nset.CmpOp{.ftz}.dtype.f16x2          d, a, b;\nset.CmpOp.BoolOp{.ftz}.dtype.f16x2   d, a, b, {!}c;\n.dtype  = { .f16x2, .u32, .s32}\n\nset.CmpOp.dtype.bf16x2               d, a, b;\nset.CmpOp.BoolOp.dtype.bf16x2        d, a, b, {!}c;\n.dtype  = { .bf16x2, .u32, .s32}\n\n.CmpOp  = { eq, ne, lt, le, gt, ge,\n            equ, neu, ltu, leu, gtu, geu, num, nan };\n.BoolOp = { and, or, xor };\n.stype  = { .b16, .b32, .b64,\n            .u16, .u32, .u64,\n            .s16, .s32, .s64,\n            .f16, .f32, .f64};"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(4, 2),
                Target(53),
                Vec(
                  InstructionForm(
                    Opcode.assume("set"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("eq"),
                        ModifierName.assume("equ"),
                        ModifierName.assume("ge"),
                        ModifierName.assume("geu"),
                        ModifierName.assume("gt"),
                        ModifierName.assume("gtu"),
                        ModifierName.assume("le"),
                        ModifierName.assume("leu"),
                        ModifierName.assume("lt"),
                        ModifierName.assume("ltu"),
                        ModifierName.assume("nan"),
                        ModifierName.assume("ne"),
                        ModifierName.assume("neu"),
                        ModifierName.assume("num")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("f16")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("b16"),
                        ModifierName.assume("b32"),
                        ModifierName.assume("b64"),
                        ModifierName.assume("f16"),
                        ModifierName.assume("f32"),
                        ModifierName.assume("f64"),
                        ModifierName.assume("s16"),
                        ModifierName.assume("s32"),
                        ModifierName.assume("s64"),
                        ModifierName.assume("u16"),
                        ModifierName.assume("u32"),
                        ModifierName.assume("u64")
                      ).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName.assume("ftz")),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("set"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("eq"),
                        ModifierName.assume("equ"),
                        ModifierName.assume("ge"),
                        ModifierName.assume("geu"),
                        ModifierName.assume("gt"),
                        ModifierName.assume("gtu"),
                        ModifierName.assume("le"),
                        ModifierName.assume("leu"),
                        ModifierName.assume("lt"),
                        ModifierName.assume("ltu"),
                        ModifierName.assume("nan"),
                        ModifierName.assume("ne"),
                        ModifierName.assume("neu"),
                        ModifierName.assume("num")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("and"),
                        ModifierName.assume("or"),
                        ModifierName.assume("xor")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("f16")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("b16"),
                        ModifierName.assume("b32"),
                        ModifierName.assume("b64"),
                        ModifierName.assume("f16"),
                        ModifierName.assume("f32"),
                        ModifierName.assume("f64"),
                        ModifierName.assume("s16"),
                        ModifierName.assume("s32"),
                        ModifierName.assume("s64"),
                        ModifierName.assume("u16"),
                        ModifierName.assume("u32"),
                        ModifierName.assume("u64")
                      ).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName.assume("ftz")),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("set"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("eq"),
                        ModifierName.assume("equ"),
                        ModifierName.assume("ge"),
                        ModifierName.assume("geu"),
                        ModifierName.assume("gt"),
                        ModifierName.assume("gtu"),
                        ModifierName.assume("le"),
                        ModifierName.assume("leu"),
                        ModifierName.assume("lt"),
                        ModifierName.assume("ltu"),
                        ModifierName.assume("nan"),
                        ModifierName.assume("ne"),
                        ModifierName.assume("neu"),
                        ModifierName.assume("num")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("bf16")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("b16"),
                        ModifierName.assume("b32"),
                        ModifierName.assume("b64"),
                        ModifierName.assume("f16"),
                        ModifierName.assume("f32"),
                        ModifierName.assume("f64"),
                        ModifierName.assume("s16"),
                        ModifierName.assume("s32"),
                        ModifierName.assume("s64"),
                        ModifierName.assume("u16"),
                        ModifierName.assume("u32"),
                        ModifierName.assume("u64")
                      ).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("set"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("eq"),
                        ModifierName.assume("equ"),
                        ModifierName.assume("ge"),
                        ModifierName.assume("geu"),
                        ModifierName.assume("gt"),
                        ModifierName.assume("gtu"),
                        ModifierName.assume("le"),
                        ModifierName.assume("leu"),
                        ModifierName.assume("lt"),
                        ModifierName.assume("ltu"),
                        ModifierName.assume("nan"),
                        ModifierName.assume("ne"),
                        ModifierName.assume("neu"),
                        ModifierName.assume("num")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("and"),
                        ModifierName.assume("or"),
                        ModifierName.assume("xor")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("bf16")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("b16"),
                        ModifierName.assume("b32"),
                        ModifierName.assume("b64"),
                        ModifierName.assume("f16"),
                        ModifierName.assume("f32"),
                        ModifierName.assume("f64"),
                        ModifierName.assume("s16"),
                        ModifierName.assume("s32"),
                        ModifierName.assume("s64"),
                        ModifierName.assume("u16"),
                        ModifierName.assume("u32"),
                        ModifierName.assume("u64")
                      ).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("set"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("eq"),
                        ModifierName.assume("equ"),
                        ModifierName.assume("ge"),
                        ModifierName.assume("geu"),
                        ModifierName.assume("gt"),
                        ModifierName.assume("gtu"),
                        ModifierName.assume("le"),
                        ModifierName.assume("leu"),
                        ModifierName.assume("lt"),
                        ModifierName.assume("ltu"),
                        ModifierName.assume("nan"),
                        ModifierName.assume("ne"),
                        ModifierName.assume("neu"),
                        ModifierName.assume("num")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("bf16x2"),
                        ModifierName.assume("s32"),
                        ModifierName.assume("u32")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("f16")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName.assume("ftz")),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("set"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("eq"),
                        ModifierName.assume("equ"),
                        ModifierName.assume("ge"),
                        ModifierName.assume("geu"),
                        ModifierName.assume("gt"),
                        ModifierName.assume("gtu"),
                        ModifierName.assume("le"),
                        ModifierName.assume("leu"),
                        ModifierName.assume("lt"),
                        ModifierName.assume("ltu"),
                        ModifierName.assume("nan"),
                        ModifierName.assume("ne"),
                        ModifierName.assume("neu"),
                        ModifierName.assume("num")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("and"),
                        ModifierName.assume("or"),
                        ModifierName.assume("xor")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("bf16x2"),
                        ModifierName.assume("s32"),
                        ModifierName.assume("u32")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("f16")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName.assume("ftz")),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("set"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("eq"),
                        ModifierName.assume("equ"),
                        ModifierName.assume("ge"),
                        ModifierName.assume("geu"),
                        ModifierName.assume("gt"),
                        ModifierName.assume("gtu"),
                        ModifierName.assume("le"),
                        ModifierName.assume("leu"),
                        ModifierName.assume("lt"),
                        ModifierName.assume("ltu"),
                        ModifierName.assume("nan"),
                        ModifierName.assume("ne"),
                        ModifierName.assume("neu"),
                        ModifierName.assume("num")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("bf16x2"),
                        ModifierName.assume("s32"),
                        ModifierName.assume("u32")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("bf16")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("set"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("eq"),
                        ModifierName.assume("equ"),
                        ModifierName.assume("ge"),
                        ModifierName.assume("geu"),
                        ModifierName.assume("gt"),
                        ModifierName.assume("gtu"),
                        ModifierName.assume("le"),
                        ModifierName.assume("leu"),
                        ModifierName.assume("lt"),
                        ModifierName.assume("ltu"),
                        ModifierName.assume("nan"),
                        ModifierName.assume("ne"),
                        ModifierName.assume("neu"),
                        ModifierName.assume("num")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("and"),
                        ModifierName.assume("or"),
                        ModifierName.assume("xor")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("bf16x2"),
                        ModifierName.assume("s32"),
                        ModifierName.assume("u32")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("bf16")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("set"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("eq"),
                        ModifierName.assume("equ"),
                        ModifierName.assume("ge"),
                        ModifierName.assume("geu"),
                        ModifierName.assume("gt"),
                        ModifierName.assume("gtu"),
                        ModifierName.assume("le"),
                        ModifierName.assume("leu"),
                        ModifierName.assume("lt"),
                        ModifierName.assume("ltu"),
                        ModifierName.assume("nan"),
                        ModifierName.assume("ne"),
                        ModifierName.assume("neu"),
                        ModifierName.assume("num")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("bf16x2"),
                        ModifierName.assume("s32"),
                        ModifierName.assume("u32")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("f16x2")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName.assume("ftz")),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("set"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("eq"),
                        ModifierName.assume("equ"),
                        ModifierName.assume("ge"),
                        ModifierName.assume("geu"),
                        ModifierName.assume("gt"),
                        ModifierName.assume("gtu"),
                        ModifierName.assume("le"),
                        ModifierName.assume("leu"),
                        ModifierName.assume("lt"),
                        ModifierName.assume("ltu"),
                        ModifierName.assume("nan"),
                        ModifierName.assume("ne"),
                        ModifierName.assume("neu"),
                        ModifierName.assume("num")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("and"),
                        ModifierName.assume("or"),
                        ModifierName.assume("xor")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("bf16x2"),
                        ModifierName.assume("s32"),
                        ModifierName.assume("u32")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("f16x2")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName.assume("ftz")),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("set"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("eq"),
                        ModifierName.assume("equ"),
                        ModifierName.assume("ge"),
                        ModifierName.assume("geu"),
                        ModifierName.assume("gt"),
                        ModifierName.assume("gtu"),
                        ModifierName.assume("le"),
                        ModifierName.assume("leu"),
                        ModifierName.assume("lt"),
                        ModifierName.assume("ltu"),
                        ModifierName.assume("nan"),
                        ModifierName.assume("ne"),
                        ModifierName.assume("neu"),
                        ModifierName.assume("num")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("bf16x2"),
                        ModifierName.assume("s32"),
                        ModifierName.assume("u32")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("bf16x2")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("set"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("eq"),
                        ModifierName.assume("equ"),
                        ModifierName.assume("ge"),
                        ModifierName.assume("geu"),
                        ModifierName.assume("gt"),
                        ModifierName.assume("gtu"),
                        ModifierName.assume("le"),
                        ModifierName.assume("leu"),
                        ModifierName.assume("lt"),
                        ModifierName.assume("ltu"),
                        ModifierName.assume("nan"),
                        ModifierName.assume("ne"),
                        ModifierName.assume("neu"),
                        ModifierName.assume("num")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("and"),
                        ModifierName.assume("or"),
                        ModifierName.assume("xor")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("bf16x2"),
                        ModifierName.assume("s32"),
                        ModifierName.assume("u32")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("bf16x2")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  )
                )
              )
            ).assume[MinLength[1]])
        )
    case SetMaxRegisters extends Definition(
          Opcode.assume("setmaxnreg"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 1) Right(Instruction.SetMaxRegisters(operands(0), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("setmaxnreg", Vec(1), operands.size))
          },
          () =>
            Metadata(Vec(
              Metadata.Entry(
                "miscellaneous-instructions-setmaxnreg".assume[MinLength[1] & NoNul],
                "9.7.20.5. Miscellaneous Instructions: setmaxnreg".assume[MinLength[1] & NoNul],
                "setmaxnreg".assume[MinLength[1] & NoNul],
                "Miscellaneous Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "setmaxnreg.action.sync.aligned.u32 imm-reg-count;\n\n.action = { .inc, .dec };"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(8, 0),
                Target(100),
                Vec(InstructionForm(
                  Opcode.assume("setmaxnreg"),
                  Vec(OperandConstraint.Value),
                  Vec(
                    _root_.scala.collection.immutable.Set(
                      ModifierName.assume("dec"),
                      ModifierName.assume("inc")
                    ).assume[MinLength[1]],
                    _root_.scala.collection.immutable.Set(ModifierName.assume("sync")).assume[MinLength[1]],
                    _root_.scala.collection.immutable.Set(ModifierName.assume("aligned")).assume[MinLength[1]],
                    _root_.scala.collection.immutable.Set(ModifierName.assume("u32")).assume[MinLength[1]]
                  ),
                  _root_.scala.collection.immutable.Set(),
                  false
                ))
              )
            ).assume[MinLength[1]])
        )
    case Setp extends Definition(
          Opcode.assume("setp"),
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
            Metadata(Vec(
              Metadata.Entry(
                "comparison-and-selection-instructions-setp".assume[MinLength[1] & NoNul],
                "9.7.6.2. Comparison and Selection Instructions: setp".assume[MinLength[1] & NoNul],
                "setp".assume[MinLength[1] & NoNul],
                "Comparison and Selection Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "setp.CmpOp{.ftz}.type         p[|q], a, b;\nsetp.CmpOp.BoolOp{.ftz}.type  p[|q], a, b, {!}c;\n\n.CmpOp  = { eq, ne, lt, le, gt, ge, lo, ls, hi, hs,\n            equ, neu, ltu, leu, gtu, geu, num, nan };\n.BoolOp = { and, or, xor };\n.type   = { .b16, .b32, .b64,\n            .u16, .u32, .u64,\n            .s16, .s32, .s64,\n                  .f32, .f64 };"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(1, 0),
                Target(13),
                Vec(
                  InstructionForm(
                    Opcode.assume("setp"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("eq"),
                        ModifierName.assume("equ"),
                        ModifierName.assume("ge"),
                        ModifierName.assume("geu"),
                        ModifierName.assume("gt"),
                        ModifierName.assume("gtu"),
                        ModifierName.assume("hi"),
                        ModifierName.assume("hs"),
                        ModifierName.assume("le"),
                        ModifierName.assume("leu"),
                        ModifierName.assume("lo"),
                        ModifierName.assume("ls"),
                        ModifierName.assume("lt"),
                        ModifierName.assume("ltu"),
                        ModifierName.assume("nan"),
                        ModifierName.assume("ne"),
                        ModifierName.assume("neu"),
                        ModifierName.assume("num")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("b16"),
                        ModifierName.assume("b32"),
                        ModifierName.assume("b64"),
                        ModifierName.assume("f32"),
                        ModifierName.assume("f64"),
                        ModifierName.assume("s16"),
                        ModifierName.assume("s32"),
                        ModifierName.assume("s64"),
                        ModifierName.assume("u16"),
                        ModifierName.assume("u32"),
                        ModifierName.assume("u64")
                      ).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName.assume("ftz")),
                    true
                  ),
                  InstructionForm(
                    Opcode.assume("setp"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("eq"),
                        ModifierName.assume("equ"),
                        ModifierName.assume("ge"),
                        ModifierName.assume("geu"),
                        ModifierName.assume("gt"),
                        ModifierName.assume("gtu"),
                        ModifierName.assume("hi"),
                        ModifierName.assume("hs"),
                        ModifierName.assume("le"),
                        ModifierName.assume("leu"),
                        ModifierName.assume("lo"),
                        ModifierName.assume("ls"),
                        ModifierName.assume("lt"),
                        ModifierName.assume("ltu"),
                        ModifierName.assume("nan"),
                        ModifierName.assume("ne"),
                        ModifierName.assume("neu"),
                        ModifierName.assume("num")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("and"),
                        ModifierName.assume("or"),
                        ModifierName.assume("xor")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("b16"),
                        ModifierName.assume("b32"),
                        ModifierName.assume("b64"),
                        ModifierName.assume("f32"),
                        ModifierName.assume("f64"),
                        ModifierName.assume("s16"),
                        ModifierName.assume("s32"),
                        ModifierName.assume("s64"),
                        ModifierName.assume("u16"),
                        ModifierName.assume("u32"),
                        ModifierName.assume("u64")
                      ).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName.assume("ftz")),
                    true
                  )
                )
              ),
              Metadata.Entry(
                "half-precision-comparison-instructions-setp".assume[MinLength[1] & NoNul],
                "9.7.7.2. Half Precision Comparison Instructions: setp".assume[MinLength[1] & NoNul],
                "setp".assume[MinLength[1] & NoNul],
                "Half Precision Comparison Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "setp.CmpOp{.ftz}.f16           p, a, b;\nsetp.CmpOp.BoolOp{.ftz}.f16    p, a, b, {!}c;\n\nsetp.CmpOp{.ftz}.f16x2         p|q, a, b;\nsetp.CmpOp.BoolOp{.ftz}.f16x2  p|q, a, b, {!}c;\n\nsetp.CmpOp.bf16                p, a, b;\nsetp.CmpOp.BoolOp.bf16         p, a, b, {!}c;\n\nsetp.CmpOp.bf16x2              p|q, a, b;\nsetp.CmpOp.BoolOp.bf16x2       p|q, a, b, {!}c;\n\n.CmpOp  = { eq, ne, lt, le, gt, ge,\n            equ, neu, ltu, leu, gtu, geu, num, nan };\n.BoolOp = { and, or, xor };"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(4, 2),
                Target(53),
                Vec(
                  InstructionForm(
                    Opcode.assume("setp"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("eq"),
                        ModifierName.assume("equ"),
                        ModifierName.assume("ge"),
                        ModifierName.assume("geu"),
                        ModifierName.assume("gt"),
                        ModifierName.assume("gtu"),
                        ModifierName.assume("le"),
                        ModifierName.assume("leu"),
                        ModifierName.assume("lt"),
                        ModifierName.assume("ltu"),
                        ModifierName.assume("nan"),
                        ModifierName.assume("ne"),
                        ModifierName.assume("neu"),
                        ModifierName.assume("num")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("f16")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName.assume("ftz")),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("setp"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("eq"),
                        ModifierName.assume("equ"),
                        ModifierName.assume("ge"),
                        ModifierName.assume("geu"),
                        ModifierName.assume("gt"),
                        ModifierName.assume("gtu"),
                        ModifierName.assume("le"),
                        ModifierName.assume("leu"),
                        ModifierName.assume("lt"),
                        ModifierName.assume("ltu"),
                        ModifierName.assume("nan"),
                        ModifierName.assume("ne"),
                        ModifierName.assume("neu"),
                        ModifierName.assume("num")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("and"),
                        ModifierName.assume("or"),
                        ModifierName.assume("xor")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("f16")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName.assume("ftz")),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("setp"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("eq"),
                        ModifierName.assume("equ"),
                        ModifierName.assume("ge"),
                        ModifierName.assume("geu"),
                        ModifierName.assume("gt"),
                        ModifierName.assume("gtu"),
                        ModifierName.assume("le"),
                        ModifierName.assume("leu"),
                        ModifierName.assume("lt"),
                        ModifierName.assume("ltu"),
                        ModifierName.assume("nan"),
                        ModifierName.assume("ne"),
                        ModifierName.assume("neu"),
                        ModifierName.assume("num")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("f16x2")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName.assume("ftz")),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("setp"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("eq"),
                        ModifierName.assume("equ"),
                        ModifierName.assume("ge"),
                        ModifierName.assume("geu"),
                        ModifierName.assume("gt"),
                        ModifierName.assume("gtu"),
                        ModifierName.assume("le"),
                        ModifierName.assume("leu"),
                        ModifierName.assume("lt"),
                        ModifierName.assume("ltu"),
                        ModifierName.assume("nan"),
                        ModifierName.assume("ne"),
                        ModifierName.assume("neu"),
                        ModifierName.assume("num")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("and"),
                        ModifierName.assume("or"),
                        ModifierName.assume("xor")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("f16x2")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName.assume("ftz")),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("setp"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("eq"),
                        ModifierName.assume("equ"),
                        ModifierName.assume("ge"),
                        ModifierName.assume("geu"),
                        ModifierName.assume("gt"),
                        ModifierName.assume("gtu"),
                        ModifierName.assume("le"),
                        ModifierName.assume("leu"),
                        ModifierName.assume("lt"),
                        ModifierName.assume("ltu"),
                        ModifierName.assume("nan"),
                        ModifierName.assume("ne"),
                        ModifierName.assume("neu"),
                        ModifierName.assume("num")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("bf16")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("setp"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("eq"),
                        ModifierName.assume("equ"),
                        ModifierName.assume("ge"),
                        ModifierName.assume("geu"),
                        ModifierName.assume("gt"),
                        ModifierName.assume("gtu"),
                        ModifierName.assume("le"),
                        ModifierName.assume("leu"),
                        ModifierName.assume("lt"),
                        ModifierName.assume("ltu"),
                        ModifierName.assume("nan"),
                        ModifierName.assume("ne"),
                        ModifierName.assume("neu"),
                        ModifierName.assume("num")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("and"),
                        ModifierName.assume("or"),
                        ModifierName.assume("xor")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("bf16")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("setp"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("eq"),
                        ModifierName.assume("equ"),
                        ModifierName.assume("ge"),
                        ModifierName.assume("geu"),
                        ModifierName.assume("gt"),
                        ModifierName.assume("gtu"),
                        ModifierName.assume("le"),
                        ModifierName.assume("leu"),
                        ModifierName.assume("lt"),
                        ModifierName.assume("ltu"),
                        ModifierName.assume("nan"),
                        ModifierName.assume("ne"),
                        ModifierName.assume("neu"),
                        ModifierName.assume("num")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("bf16x2")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("setp"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("eq"),
                        ModifierName.assume("equ"),
                        ModifierName.assume("ge"),
                        ModifierName.assume("geu"),
                        ModifierName.assume("gt"),
                        ModifierName.assume("gtu"),
                        ModifierName.assume("le"),
                        ModifierName.assume("leu"),
                        ModifierName.assume("lt"),
                        ModifierName.assume("ltu"),
                        ModifierName.assume("nan"),
                        ModifierName.assume("ne"),
                        ModifierName.assume("neu"),
                        ModifierName.assume("num")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("and"),
                        ModifierName.assume("or"),
                        ModifierName.assume("xor")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("bf16x2")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  )
                )
              )
            ).assume[MinLength[1]])
        )
    case Shf extends Definition(
          Opcode.assume("shf"),
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
            Metadata(Vec(
              Metadata.Entry(
                "logic-and-shift-instructions-shf".assume[MinLength[1] & NoNul],
                "9.7.8.7. Logic and Shift Instructions: shf".assume[MinLength[1] & NoNul],
                "shf".assume[MinLength[1] & NoNul],
                "Logic and Shift Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "shf.l.mode.b32  d, a, b, c;  // left shift\nshf.r.mode.b32  d, a, b, c;  // right shift\n\n.mode = { .clamp, .wrap };"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(3, 1),
                Target(32),
                Vec(
                  InstructionForm(
                    Opcode.assume("shf"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("l")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("clamp"),
                        ModifierName.assume("wrap")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("b32")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(),
                    true
                  ),
                  InstructionForm(
                    Opcode.assume("shf"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("r")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("clamp"),
                        ModifierName.assume("wrap")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("b32")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(),
                    true
                  )
                )
              )
            ).assume[MinLength[1]])
        )
    case Shfl extends Definition(
          Opcode.assume("shfl"),
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
            Metadata(Vec(
              Metadata.Entry(
                "data-movement-and-conversion-instructions-shfl".assume[MinLength[1] & NoNul],
                "9.7.9.5. Data Movement and Conversion Instructions: shfl (deprecated)".assume[MinLength[1] & NoNul],
                "shfl (deprecated)".assume[MinLength[1] & NoNul],
                "Data Movement and Conversion Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "shfl.mode.b32  d[|p], a, b, c;\n\n.mode = { .up, .down, .bfly, .idx };"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(3, 0),
                Target(30),
                Vec(InstructionForm(
                  Opcode.assume("shfl"),
                  Vec(
                    OperandConstraint.Register,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Vec(
                    _root_.scala.collection.immutable.Set(
                      ModifierName.assume("bfly"),
                      ModifierName.assume("down"),
                      ModifierName.assume("idx"),
                      ModifierName.assume("up")
                    ).assume[MinLength[1]],
                    _root_.scala.collection.immutable.Set(ModifierName.assume("b32")).assume[MinLength[1]]
                  ),
                  _root_.scala.collection.immutable.Set(),
                  false
                ))
              )
            ).assume[MinLength[1]])
        )
    case ShflSync extends Definition(
          Opcode.assume("shfl.sync"),
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
            Metadata(Vec(
              Metadata.Entry(
                "data-movement-and-conversion-instructions-shfl-sync".assume[MinLength[1] & NoNul],
                "9.7.9.6. Data Movement and Conversion Instructions: shfl.sync".assume[MinLength[1] & NoNul],
                "shfl.sync".assume[MinLength[1] & NoNul],
                "Data Movement and Conversion Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "shfl.sync.mode.b32  d[|p], a, b, c, membermask;\n\n.mode = { .up, .down, .bfly, .idx };"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(6, 0),
                Target(30),
                Vec(InstructionForm(
                  Opcode.assume("shfl.sync"),
                  Vec(
                    OperandConstraint.Register,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Vec(
                    _root_.scala.collection.immutable.Set(
                      ModifierName.assume("bfly"),
                      ModifierName.assume("down"),
                      ModifierName.assume("idx"),
                      ModifierName.assume("up")
                    ).assume[MinLength[1]],
                    _root_.scala.collection.immutable.Set(ModifierName.assume("b32")).assume[MinLength[1]]
                  ),
                  _root_.scala.collection.immutable.Set(),
                  true
                ))
              )
            ).assume[MinLength[1]])
        )
    case Shl extends Definition(
          Opcode.assume("shl"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 3) Right(Instruction.Shl(operands(0), operands(1), operands(2), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("shl", Vec(3), operands.size))
          },
          () =>
            Metadata(Vec(
              Metadata.Entry(
                "logic-and-shift-instructions-shl".assume[MinLength[1] & NoNul],
                "9.7.8.8. Logic and Shift Instructions: shl".assume[MinLength[1] & NoNul],
                "shl".assume[MinLength[1] & NoNul],
                "Logic and Shift Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "shl.type d, a, b;\n\n.type = { .b16, .b32, .b64 };"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(1, 0),
                Target(10),
                Vec(InstructionForm(
                  Opcode.assume("shl"),
                  Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                  Vec(_root_.scala.collection.immutable.Set(
                    ModifierName.assume("b16"),
                    ModifierName.assume("b32"),
                    ModifierName.assume("b64")
                  ).assume[MinLength[1]]),
                  _root_.scala.collection.immutable.Set(),
                  true
                ))
              )
            ).assume[MinLength[1]])
        )
    case Shr extends Definition(
          Opcode.assume("shr"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 3) Right(Instruction.Shr(operands(0), operands(1), operands(2), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("shr", Vec(3), operands.size))
          },
          () =>
            Metadata(Vec(
              Metadata.Entry(
                "logic-and-shift-instructions-shr".assume[MinLength[1] & NoNul],
                "9.7.8.9. Logic and Shift Instructions: shr".assume[MinLength[1] & NoNul],
                "shr".assume[MinLength[1] & NoNul],
                "Logic and Shift Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "shr.type d, a, b;\n\n.type = { .b16, .b32, .b64,\n          .u16, .u32, .u64,\n          .s16, .s32, .s64 };"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(1, 0),
                Target(10),
                Vec(InstructionForm(
                  Opcode.assume("shr"),
                  Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                  Vec(_root_.scala.collection.immutable.Set(
                    ModifierName.assume("b16"),
                    ModifierName.assume("b32"),
                    ModifierName.assume("b64"),
                    ModifierName.assume("s16"),
                    ModifierName.assume("s32"),
                    ModifierName.assume("s64"),
                    ModifierName.assume("u16"),
                    ModifierName.assume("u32"),
                    ModifierName.assume("u64")
                  ).assume[MinLength[1]]),
                  _root_.scala.collection.immutable.Set(),
                  true
                ))
              )
            ).assume[MinLength[1]])
        )
    case Sin extends Definition(
          Opcode.assume("sin"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 2) Right(Instruction.Sin(operands(0), operands(1), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("sin", Vec(2), operands.size))
          },
          () =>
            Metadata(Vec(
              Metadata.Entry(
                "floating-point-instructions-sin".assume[MinLength[1] & NoNul],
                "9.7.3.18. Floating Point Instructions: sin".assume[MinLength[1] & NoNul],
                "sin".assume[MinLength[1] & NoNul],
                "Floating Point Instructions".assume[MinLength[1] & NoNul],
                Vec[String]("sin.approx{.ftz}.f32  d, a;").map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(1, 0),
                Target(10),
                Vec(InstructionForm(
                  Opcode.assume("sin"),
                  Vec(OperandConstraint.Register, OperandConstraint.Value),
                  Vec(
                    _root_.scala.collection.immutable.Set(ModifierName.assume("approx")).assume[MinLength[1]],
                    _root_.scala.collection.immutable.Set(ModifierName.assume("f32")).assume[MinLength[1]]
                  ),
                  _root_.scala.collection.immutable.Set(ModifierName.assume("ftz")),
                  true
                ))
              )
            ).assume[MinLength[1]])
        )
    case Slct extends Definition(
          Opcode.assume("slct"),
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
            Metadata(Vec(
              Metadata.Entry(
                "comparison-and-selection-instructions-slct".assume[MinLength[1] & NoNul],
                "9.7.6.4. Comparison and Selection Instructions: slct".assume[MinLength[1] & NoNul],
                "slct".assume[MinLength[1] & NoNul],
                "Comparison and Selection Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "slct.dtype.s32        d, a, b, c;\nslct{.ftz}.dtype.f32  d, a, b, c;\n\n.dtype = { .b16, .b32, .b64,\n           .u16, .u32, .u64,\n           .s16, .s32, .s64,\n                 .f32, .f64 };"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(1, 0),
                Target(13),
                Vec(
                  InstructionForm(
                    Opcode.assume("slct"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("b16"),
                        ModifierName.assume("b32"),
                        ModifierName.assume("b64"),
                        ModifierName.assume("f32"),
                        ModifierName.assume("f64"),
                        ModifierName.assume("s16"),
                        ModifierName.assume("s32"),
                        ModifierName.assume("s64"),
                        ModifierName.assume("u16"),
                        ModifierName.assume("u32"),
                        ModifierName.assume("u64")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("s32")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(),
                    true
                  ),
                  InstructionForm(
                    Opcode.assume("slct"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("b16"),
                        ModifierName.assume("b32"),
                        ModifierName.assume("b64"),
                        ModifierName.assume("f32"),
                        ModifierName.assume("f64"),
                        ModifierName.assume("s16"),
                        ModifierName.assume("s32"),
                        ModifierName.assume("s64"),
                        ModifierName.assume("u16"),
                        ModifierName.assume("u32"),
                        ModifierName.assume("u64")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("f32")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName.assume("ftz")),
                    true
                  )
                )
              )
            ).assume[MinLength[1]])
        )
    case Sqrt extends Definition(
          Opcode.assume("sqrt"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 2) Right(Instruction.Sqrt(operands(0), operands(1), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("sqrt", Vec(2), operands.size))
          },
          () =>
            Metadata(Vec(
              Metadata.Entry(
                "floating-point-instructions-sqrt".assume[MinLength[1] & NoNul],
                "9.7.3.15. Floating Point Instructions: sqrt".assume[MinLength[1] & NoNul],
                "sqrt".assume[MinLength[1] & NoNul],
                "Floating Point Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "sqrt.approx{.ftz}.f32  d, a; // fast, approximate square root\nsqrt.rnd{.ftz}.f32     d, a; // IEEE 754 compliant rounding\nsqrt.rnd.f64           d, a; // IEEE 754 compliant rounding\n\n.rnd = { .rn, .rz, .rm, .rp };"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(1, 0),
                Target(13),
                Vec(
                  InstructionForm(
                    Opcode.assume("sqrt"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("approx")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("f32")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName.assume("ftz")),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("sqrt"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("rm"),
                        ModifierName.assume("rn"),
                        ModifierName.assume("rp"),
                        ModifierName.assume("rz")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("f32")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName.assume("ftz")),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("sqrt"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("rm"),
                        ModifierName.assume("rn"),
                        ModifierName.assume("rp"),
                        ModifierName.assume("rz")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("f64")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  )
                )
              )
            ).assume[MinLength[1]])
        )
    case St extends Definition(
          Opcode.assume("st"),
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
            Metadata(Vec(
              Metadata.Entry(
                "data-movement-and-conversion-instructions-st".assume[MinLength[1] & NoNul],
                "9.7.9.11. Data Movement and Conversion Instructions: st".assume[MinLength[1] & NoNul],
                "st".assume[MinLength[1] & NoNul],
                "Data Movement and Conversion Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "st{.weak}{.ss}{.cop}{.level::cache_hint}{.vec}.type   [a], b{, cache_policy};\nst{.weak}{.ss}{.level1::eviction_priority}{.level2::eviction_priority}{.level::cache_hint}{.vec}.type\n                                                      [a], b{, cache_policy};\nst.volatile{.ss}{.vec}.type                           [a], b;\nst.relaxed.scope{.ss}{.level1::eviction_priority}{.level2::eviction_priority}{.level::cache_hint}{.vec}.type\n                                                      [a], b{, cache_policy};\nst.release.scope{.ss}{.level1::eviction_priority}{.level2::eviction_priority}{.level::cache_hint}{.vec}.type\n                                                      [a], b{, cache_policy};\nst.mmio.sem.sys{.global}.type         [a], b;\n\n.ss =                       { .global, .local, .param{::func}, .shared{::cta, ::cluster} };\n.sem =                      { .release, .relaxed };\n.level1::eviction_priority = { .L1::evict_normal, .L1::evict_unchanged,\n                               .L1::evict_first, .L1::evict_last, .L1::no_allocate };\n.level2::eviction_priority = { .L2::evict_normal, .L2::evict_first, .L2::evict_last };\n.level::cache_hint =        { .L2::cache_hint };\n.cop =                      { .wb, .cg, .cs, .wt };\n.sem =                      { .relaxed, .release };\n.scope =                    { .cta, .cluster, .gpu, .sys };\n.vec =                      { .v2, .v4, .v8 };\n.type =                     { .b8, .b16, .b32, .b64, .b128,\n                              .u8, .u16, .u32, .u64,\n                              .s8, .s16, .s32, .s64,\n                              .f32, .f64 };"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(1, 0),
                Target(13),
                Vec(
                  InstructionForm(
                    Opcode.assume("st"),
                    Vec(OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(_root_.scala.collection.immutable.Set(
                      ModifierName.assume("b128"),
                      ModifierName.assume("b16"),
                      ModifierName.assume("b32"),
                      ModifierName.assume("b64"),
                      ModifierName.assume("b8"),
                      ModifierName.assume("f32"),
                      ModifierName.assume("f64"),
                      ModifierName.assume("s16"),
                      ModifierName.assume("s32"),
                      ModifierName.assume("s64"),
                      ModifierName.assume("s8"),
                      ModifierName.assume("u16"),
                      ModifierName.assume("u32"),
                      ModifierName.assume("u64"),
                      ModifierName.assume("u8")
                    ).assume[MinLength[1]]),
                    _root_.scala.collection.immutable.Set(
                      ModifierName.assume("L2::cache_hint"),
                      ModifierName.assume("cg"),
                      ModifierName.assume("cs"),
                      ModifierName.assume("global"),
                      ModifierName.assume("local"),
                      ModifierName.assume("v2"),
                      ModifierName.assume("v4"),
                      ModifierName.assume("v8"),
                      ModifierName.assume("wb"),
                      ModifierName.assume("weak"),
                      ModifierName.assume("wt")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("st"),
                    Vec(OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(_root_.scala.collection.immutable.Set(
                      ModifierName.assume("b128"),
                      ModifierName.assume("b16"),
                      ModifierName.assume("b32"),
                      ModifierName.assume("b64"),
                      ModifierName.assume("b8"),
                      ModifierName.assume("f32"),
                      ModifierName.assume("f64"),
                      ModifierName.assume("s16"),
                      ModifierName.assume("s32"),
                      ModifierName.assume("s64"),
                      ModifierName.assume("s8"),
                      ModifierName.assume("u16"),
                      ModifierName.assume("u32"),
                      ModifierName.assume("u64"),
                      ModifierName.assume("u8")
                    ).assume[MinLength[1]]),
                    _root_.scala.collection.immutable.Set(
                      ModifierName.assume("L1::evict_first"),
                      ModifierName.assume("L1::evict_last"),
                      ModifierName.assume("L1::evict_normal"),
                      ModifierName.assume("L1::evict_unchanged"),
                      ModifierName.assume("L1::no_allocate"),
                      ModifierName.assume("L2::cache_hint"),
                      ModifierName.assume("L2::evict_first"),
                      ModifierName.assume("L2::evict_last"),
                      ModifierName.assume("L2::evict_normal"),
                      ModifierName.assume("global"),
                      ModifierName.assume("local"),
                      ModifierName.assume("v2"),
                      ModifierName.assume("v4"),
                      ModifierName.assume("v8"),
                      ModifierName.assume("weak")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("st"),
                    Vec(OperandConstraint.Address, OperandConstraint.Value),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("volatile")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("b128"),
                        ModifierName.assume("b16"),
                        ModifierName.assume("b32"),
                        ModifierName.assume("b64"),
                        ModifierName.assume("b8"),
                        ModifierName.assume("f32"),
                        ModifierName.assume("f64"),
                        ModifierName.assume("s16"),
                        ModifierName.assume("s32"),
                        ModifierName.assume("s64"),
                        ModifierName.assume("s8"),
                        ModifierName.assume("u16"),
                        ModifierName.assume("u32"),
                        ModifierName.assume("u64"),
                        ModifierName.assume("u8")
                      ).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName.assume("global"),
                      ModifierName.assume("local"),
                      ModifierName.assume("v2"),
                      ModifierName.assume("v4"),
                      ModifierName.assume("v8")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("st"),
                    Vec(OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("relaxed")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("cluster"),
                        ModifierName.assume("cta"),
                        ModifierName.assume("gpu"),
                        ModifierName.assume("sys")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("b128"),
                        ModifierName.assume("b16"),
                        ModifierName.assume("b32"),
                        ModifierName.assume("b64"),
                        ModifierName.assume("b8"),
                        ModifierName.assume("f32"),
                        ModifierName.assume("f64"),
                        ModifierName.assume("s16"),
                        ModifierName.assume("s32"),
                        ModifierName.assume("s64"),
                        ModifierName.assume("s8"),
                        ModifierName.assume("u16"),
                        ModifierName.assume("u32"),
                        ModifierName.assume("u64"),
                        ModifierName.assume("u8")
                      ).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName.assume("L1::evict_first"),
                      ModifierName.assume("L1::evict_last"),
                      ModifierName.assume("L1::evict_normal"),
                      ModifierName.assume("L1::evict_unchanged"),
                      ModifierName.assume("L1::no_allocate"),
                      ModifierName.assume("L2::cache_hint"),
                      ModifierName.assume("L2::evict_first"),
                      ModifierName.assume("L2::evict_last"),
                      ModifierName.assume("L2::evict_normal"),
                      ModifierName.assume("global"),
                      ModifierName.assume("local"),
                      ModifierName.assume("v2"),
                      ModifierName.assume("v4"),
                      ModifierName.assume("v8")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("st"),
                    Vec(OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("release")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("cluster"),
                        ModifierName.assume("cta"),
                        ModifierName.assume("gpu"),
                        ModifierName.assume("sys")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("b128"),
                        ModifierName.assume("b16"),
                        ModifierName.assume("b32"),
                        ModifierName.assume("b64"),
                        ModifierName.assume("b8"),
                        ModifierName.assume("f32"),
                        ModifierName.assume("f64"),
                        ModifierName.assume("s16"),
                        ModifierName.assume("s32"),
                        ModifierName.assume("s64"),
                        ModifierName.assume("s8"),
                        ModifierName.assume("u16"),
                        ModifierName.assume("u32"),
                        ModifierName.assume("u64"),
                        ModifierName.assume("u8")
                      ).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName.assume("L1::evict_first"),
                      ModifierName.assume("L1::evict_last"),
                      ModifierName.assume("L1::evict_normal"),
                      ModifierName.assume("L1::evict_unchanged"),
                      ModifierName.assume("L1::no_allocate"),
                      ModifierName.assume("L2::cache_hint"),
                      ModifierName.assume("L2::evict_first"),
                      ModifierName.assume("L2::evict_last"),
                      ModifierName.assume("L2::evict_normal"),
                      ModifierName.assume("global"),
                      ModifierName.assume("local"),
                      ModifierName.assume("v2"),
                      ModifierName.assume("v4"),
                      ModifierName.assume("v8")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("st"),
                    Vec(OperandConstraint.Address, OperandConstraint.Value),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("mmio")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("relaxed"),
                        ModifierName.assume("release")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("sys")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("b128"),
                        ModifierName.assume("b16"),
                        ModifierName.assume("b32"),
                        ModifierName.assume("b64"),
                        ModifierName.assume("b8"),
                        ModifierName.assume("f32"),
                        ModifierName.assume("f64"),
                        ModifierName.assume("s16"),
                        ModifierName.assume("s32"),
                        ModifierName.assume("s64"),
                        ModifierName.assume("s8"),
                        ModifierName.assume("u16"),
                        ModifierName.assume("u32"),
                        ModifierName.assume("u64"),
                        ModifierName.assume("u8")
                      ).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName.assume("global")),
                    false
                  )
                )
              )
            ).assume[MinLength[1]])
        )
    case StAsync extends Definition(
          Opcode.assume("st.async"),
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
            Metadata(Vec(
              Metadata.Entry(
                "data-movement-and-conversion-instructions-st-async".assume[MinLength[1] & NoNul],
                "9.7.9.12. Data Movement and Conversion Instructions: st.async".assume[MinLength[1] & NoNul],
                "st.async".assume[MinLength[1] & NoNul],
                "Data Movement and Conversion Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "st.async{.weak}{.ss}.completion_mechanism{.vec}.type [a], b, [mbar];\n\n.ss   =                 { .shared::cluster };\n.type =                 { .b32, .b64, .b128,\n                          .u32, .u64,\n                          .s32, .s64,\n                          .f32, .f64 };\n.vec  =                 { .v2, .v4 };\n.completion_mechanism = { .mbarrier::complete_tx::bytes };\n\nst.async{.mmio}.sem.scope{.ss}.type [a], b;\n\n.sem =                  { .release };\n.scope =                { .gpu, .sys };\n.ss =                   { .global };\n.type =                 { .b8, .b16, .b32, .b64,\n                          .u8, .u16, .u32, .u64,\n                          .s8, .s16, .s32, .s64,\n                                     .f32, .f64 };"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(8, 1),
                Target(90),
                Vec(
                  InstructionForm(
                    Opcode.assume("st.async"),
                    Vec(OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Address),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume(
                        "mbarrier::complete_tx::bytes"
                      )).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("b16"),
                        ModifierName.assume("b32"),
                        ModifierName.assume("b64"),
                        ModifierName.assume("b8"),
                        ModifierName.assume("f32"),
                        ModifierName.assume("f64"),
                        ModifierName.assume("s16"),
                        ModifierName.assume("s32"),
                        ModifierName.assume("s64"),
                        ModifierName.assume("s8"),
                        ModifierName.assume("u16"),
                        ModifierName.assume("u32"),
                        ModifierName.assume("u64"),
                        ModifierName.assume("u8")
                      ).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName.assume("global"),
                      ModifierName.assume("v2"),
                      ModifierName.assume("v4"),
                      ModifierName.assume("weak")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("st.async"),
                    Vec(OperandConstraint.Address, OperandConstraint.Value),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("release")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("gpu"),
                        ModifierName.assume("sys")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("b16"),
                        ModifierName.assume("b32"),
                        ModifierName.assume("b64"),
                        ModifierName.assume("b8"),
                        ModifierName.assume("f32"),
                        ModifierName.assume("f64"),
                        ModifierName.assume("s16"),
                        ModifierName.assume("s32"),
                        ModifierName.assume("s64"),
                        ModifierName.assume("s8"),
                        ModifierName.assume("u16"),
                        ModifierName.assume("u32"),
                        ModifierName.assume("u64"),
                        ModifierName.assume("u8")
                      ).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName.assume("global"), ModifierName.assume("mmio")),
                    false
                  )
                )
              )
            ).assume[MinLength[1]])
        )
    case StBulk extends Definition(
          Opcode.assume("st.bulk"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 3) Right(Instruction.StBulk(operands(0), operands(1), operands(2), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("st.bulk", Vec(3), operands.size))
          },
          () =>
            Metadata(Vec(
              Metadata.Entry(
                "data-movement-and-conversion-instructions-st-bulk".assume[MinLength[1] & NoNul],
                "9.7.9.14. Data Movement and Conversion Instructions: st.bulk".assume[MinLength[1] & NoNul],
                "st.bulk".assume[MinLength[1] & NoNul],
                "Data Movement and Conversion Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "st.bulk{.weak}{.shared::cta}  [a], size, initval; // initval must be zero"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(8, 6),
                Target(100),
                Vec(InstructionForm(
                  Opcode.assume("st.bulk"),
                  Vec(OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value),
                  Vec(),
                  _root_.scala.collection.immutable.Set(
                    ModifierName.assume("shared::cta"),
                    ModifierName.assume("weak")
                  ),
                  true
                ))
              )
            ).assume[MinLength[1]])
        )
    case StackRestore extends Definition(
          Opcode.assume("stackrestore"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 1) Right(Instruction.StackRestore(operands(0), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("stackrestore", Vec(1), operands.size))
          },
          () =>
            Metadata(Vec(
              Metadata.Entry(
                "stack-manipulation-instructions-stackrestore".assume[MinLength[1] & NoNul],
                "9.7.18.2. Stack Manipulation Instructions: stackrestore".assume[MinLength[1] & NoNul],
                "stackrestore".assume[MinLength[1] & NoNul],
                "Stack Manipulation Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "stackrestore.type  a;\n\n.type = { .u32, .u64 };"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(7, 3),
                Target(52),
                Vec(InstructionForm(
                  Opcode.assume("stackrestore"),
                  Vec(OperandConstraint.Value),
                  Vec(_root_.scala.collection.immutable.Set(
                    ModifierName.assume("u32"),
                    ModifierName.assume("u64")
                  ).assume[MinLength[1]]),
                  _root_.scala.collection.immutable.Set(),
                  true
                ))
              )
            ).assume[MinLength[1]])
        )
    case StackSave extends Definition(
          Opcode.assume("stacksave"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 1) Right(Instruction.StackSave(operands(0), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("stacksave", Vec(1), operands.size))
          },
          () =>
            Metadata(Vec(
              Metadata.Entry(
                "stack-manipulation-instructions-stacksave".assume[MinLength[1] & NoNul],
                "9.7.18.1. Stack Manipulation Instructions: stacksave".assume[MinLength[1] & NoNul],
                "stacksave".assume[MinLength[1] & NoNul],
                "Stack Manipulation Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "stacksave.type  d;\n\n.type = { .u32, .u64 };"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(7, 3),
                Target(52),
                Vec(InstructionForm(
                  Opcode.assume("stacksave"),
                  Vec(OperandConstraint.Register),
                  Vec(_root_.scala.collection.immutable.Set(
                    ModifierName.assume("u32"),
                    ModifierName.assume("u64")
                  ).assume[MinLength[1]]),
                  _root_.scala.collection.immutable.Set(),
                  true
                ))
              )
            ).assume[MinLength[1]])
        )
    case Sub extends Definition(
          Opcode.assume("sub"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 3) Right(Instruction.Sub(operands(0), operands(1), operands(2), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("sub", Vec(3), operands.size))
          },
          () =>
            Metadata(Vec(
              Metadata.Entry(
                "integer-arithmetic-instructions-sub".assume[MinLength[1] & NoNul],
                "9.7.1.2. Integer Arithmetic Instructions: sub".assume[MinLength[1] & NoNul],
                "sub".assume[MinLength[1] & NoNul],
                "Integer Arithmetic Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "sub.type1        d, a, b;\nsub{.sat}.type2  d, a, b;\n\n.type1 = { .u16, .u32, .u64,\n           .s16, .s64 };\n\n.type2 = { .s32, .u8x4, .s8x4 };"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(1, 0),
                Target(10),
                Vec(
                  InstructionForm(
                    Opcode.assume("sub"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(_root_.scala.collection.immutable.Set(
                      ModifierName.assume("s16"),
                      ModifierName.assume("s64"),
                      ModifierName.assume("u16"),
                      ModifierName.assume("u32"),
                      ModifierName.assume("u64")
                    ).assume[MinLength[1]]),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("sub"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(_root_.scala.collection.immutable.Set(
                      ModifierName.assume("s32"),
                      ModifierName.assume("s8x4"),
                      ModifierName.assume("u8x4")
                    ).assume[MinLength[1]]),
                    _root_.scala.collection.immutable.Set(ModifierName.assume("sat")),
                    false
                  )
                )
              ),
              Metadata.Entry(
                "floating-point-instructions-sub".assume[MinLength[1] & NoNul],
                "9.7.3.4. Floating Point Instructions: sub".assume[MinLength[1] & NoNul],
                "sub".assume[MinLength[1] & NoNul],
                "Floating Point Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "sub{.rnd}{.ftz}{.sat}.f32  d, a, b;\nsub{.rnd}{.ftz}.f32x2      d, a, b;\nsub{.rnd}.f64              d, a, b;\n\n.rnd = { .rn, .rz, .rm, .rp };"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(1, 0),
                Target(13),
                Vec(
                  InstructionForm(
                    Opcode.assume("sub"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(_root_.scala.collection.immutable.Set(ModifierName.assume("f32")).assume[MinLength[1]]),
                    _root_.scala.collection.immutable.Set(
                      ModifierName.assume("ftz"),
                      ModifierName.assume("rm"),
                      ModifierName.assume("rn"),
                      ModifierName.assume("rp"),
                      ModifierName.assume("rz"),
                      ModifierName.assume("sat")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("sub"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(_root_.scala.collection.immutable.Set(ModifierName.assume("f32x2")).assume[MinLength[1]]),
                    _root_.scala.collection.immutable.Set(
                      ModifierName.assume("ftz"),
                      ModifierName.assume("rm"),
                      ModifierName.assume("rn"),
                      ModifierName.assume("rp"),
                      ModifierName.assume("rz")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("sub"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(_root_.scala.collection.immutable.Set(ModifierName.assume("f64")).assume[MinLength[1]]),
                    _root_.scala.collection.immutable.Set(
                      ModifierName.assume("rm"),
                      ModifierName.assume("rn"),
                      ModifierName.assume("rp"),
                      ModifierName.assume("rz")
                    ),
                    false
                  )
                )
              ),
              Metadata.Entry(
                "half-precision-floating-point-instructions-sub".assume[MinLength[1] & NoNul],
                "9.7.4.2. Half Precision Floating Point Instructions: sub".assume[MinLength[1] & NoNul],
                "sub".assume[MinLength[1] & NoNul],
                "Half Precision Floating Point Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "sub{.rnd}{.ftz}{.sat}.f16   d, a, b;\nsub{.rnd}{.ftz}{.sat}.f16x2 d, a, b;\n\nsub{.rnd}.bf16   d, a, b;\nsub{.rnd}.bf16x2 d, a, b;\n\n.rnd = { .rn };"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(4, 2),
                Target(53),
                Vec(
                  InstructionForm(
                    Opcode.assume("sub"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(_root_.scala.collection.immutable.Set(ModifierName.assume("f16")).assume[MinLength[1]]),
                    _root_.scala.collection.immutable.Set(
                      ModifierName.assume("ftz"),
                      ModifierName.assume("rn"),
                      ModifierName.assume("sat")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("sub"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(_root_.scala.collection.immutable.Set(ModifierName.assume("f16x2")).assume[MinLength[1]]),
                    _root_.scala.collection.immutable.Set(
                      ModifierName.assume("ftz"),
                      ModifierName.assume("rn"),
                      ModifierName.assume("sat")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("sub"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(_root_.scala.collection.immutable.Set(ModifierName.assume("bf16")).assume[MinLength[1]]),
                    _root_.scala.collection.immutable.Set(ModifierName.assume("rn")),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("sub"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(_root_.scala.collection.immutable.Set(ModifierName.assume("bf16x2")).assume[MinLength[1]]),
                    _root_.scala.collection.immutable.Set(ModifierName.assume("rn")),
                    false
                  )
                )
              ),
              Metadata.Entry(
                "mixed-precision-floating-point-instructions-sub".assume[MinLength[1] & NoNul],
                "9.7.5.2. Mixed Precision Floating Point Instructions: sub".assume[MinLength[1] & NoNul],
                "sub".assume[MinLength[1] & NoNul],
                "Mixed Precision Floating Point Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "sub{.rnd}{.sat}.f32.atype  d, a, c;\n\n.atype = { .f16, .bf16};\n.rnd   = { .rn, .rz, .rm, .rp };"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(1, 0),
                Target(100),
                Vec(InstructionForm(
                  Opcode.assume("sub"),
                  Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                  Vec(
                    _root_.scala.collection.immutable.Set(ModifierName.assume("f32")).assume[MinLength[1]],
                    _root_.scala.collection.immutable.Set(
                      ModifierName.assume("bf16"),
                      ModifierName.assume("f16")
                    ).assume[MinLength[1]]
                  ),
                  _root_.scala.collection.immutable.Set(
                    ModifierName.assume("rm"),
                    ModifierName.assume("rn"),
                    ModifierName.assume("rp"),
                    ModifierName.assume("rz"),
                    ModifierName.assume("sat")
                  ),
                  true
                ))
              )
            ).assume[MinLength[1]])
        )
    case SubCc extends Definition(
          Opcode.assume("sub.cc"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 3) Right(Instruction.SubCc(operands(0), operands(1), operands(2), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("sub.cc", Vec(3), operands.size))
          },
          () =>
            Metadata(Vec(
              Metadata.Entry(
                "extended-precision-arithmetic-instructions-sub-cc".assume[MinLength[1] & NoNul],
                "9.7.2.3. Extended-Precision Arithmetic Instructions: sub.cc".assume[MinLength[1] & NoNul],
                "sub.cc".assume[MinLength[1] & NoNul],
                "Extended-Precision Arithmetic Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "sub.cc.type  d, a, b;\n\n.type = { .u32, .s32, .u64, .s64 };"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(1, 0),
                Target(20),
                Vec(InstructionForm(
                  Opcode.assume("sub.cc"),
                  Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                  Vec(_root_.scala.collection.immutable.Set(
                    ModifierName.assume("s32"),
                    ModifierName.assume("s64"),
                    ModifierName.assume("u32"),
                    ModifierName.assume("u64")
                  ).assume[MinLength[1]]),
                  _root_.scala.collection.immutable.Set(),
                  true
                ))
              )
            ).assume[MinLength[1]])
        )
    case Subc extends Definition(
          Opcode.assume("subc"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 3) Right(Instruction.Subc(operands(0), operands(1), operands(2), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("subc", Vec(3), operands.size))
          },
          () =>
            Metadata(Vec(
              Metadata.Entry(
                "extended-precision-arithmetic-instructions-subc".assume[MinLength[1] & NoNul],
                "9.7.2.4. Extended-Precision Arithmetic Instructions: subc".assume[MinLength[1] & NoNul],
                "subc".assume[MinLength[1] & NoNul],
                "Extended-Precision Arithmetic Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "subc{.cc}.type  d, a, b;\n\n.type = { .u32, .s32, .u64, .s64 };"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(1, 0),
                Target(20),
                Vec(InstructionForm(
                  Opcode.assume("subc"),
                  Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                  Vec(_root_.scala.collection.immutable.Set(
                    ModifierName.assume("s32"),
                    ModifierName.assume("s64"),
                    ModifierName.assume("u32"),
                    ModifierName.assume("u64")
                  ).assume[MinLength[1]]),
                  _root_.scala.collection.immutable.Set(ModifierName.assume("cc")),
                  true
                ))
              )
            ).assume[MinLength[1]])
        )
    case Suld extends Definition(
          Opcode.assume("suld"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 2) Right(Instruction.Suld(operands(0), operands(1), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("suld", Vec(2), operands.size))
          },
          () =>
            Metadata(Vec(
              Metadata.Entry(
                "surface-instructions-suld".assume[MinLength[1] & NoNul],
                "9.7.12.1. Surface Instructions: suld".assume[MinLength[1] & NoNul],
                "suld".assume[MinLength[1] & NoNul],
                "Surface Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "suld.b.geom{.cop}.vec.dtype.clamp  d, [a, b];  // unformatted\n\n.geom  = { .1d, .2d, .3d, .a1d, .a2d };\n.cop   = { .ca, .cg, .cs, .cv };               // cache operation\n.vec   = { none, .v2, .v4 };\n.dtype = { .b8 , .b16, .b32, .b64 };\n.clamp = { .trap, .clamp, .zero };"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(1, 0),
                Target(20),
                Vec(InstructionForm(
                  Opcode.assume("suld"),
                  Vec(OperandConstraint.Register, OperandConstraint.Address),
                  Vec(
                    _root_.scala.collection.immutable.Set(ModifierName.assume("b")).assume[MinLength[1]],
                    _root_.scala.collection.immutable.Set(
                      ModifierName.assume("a1d"),
                      ModifierName.assume("a2d")
                    ).assume[MinLength[1]],
                    _root_.scala.collection.immutable.Set(
                      ModifierName.assume("none"),
                      ModifierName.assume("v2"),
                      ModifierName.assume("v4")
                    ).assume[MinLength[1]],
                    _root_.scala.collection.immutable.Set(
                      ModifierName.assume("b16"),
                      ModifierName.assume("b32"),
                      ModifierName.assume("b64"),
                      ModifierName.assume("b8")
                    ).assume[MinLength[1]],
                    _root_.scala.collection.immutable.Set(
                      ModifierName.assume("clamp"),
                      ModifierName.assume("trap"),
                      ModifierName.assume("zero")
                    ).assume[MinLength[1]]
                  ),
                  _root_.scala.collection.immutable.Set(
                    ModifierName.assume("ca"),
                    ModifierName.assume("cg"),
                    ModifierName.assume("cs"),
                    ModifierName.assume("cv")
                  ),
                  true
                ))
              )
            ).assume[MinLength[1]])
        )
    case Suq extends Definition(
          Opcode.assume("suq"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 2) Right(Instruction.Suq(operands(0), operands(1), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("suq", Vec(2), operands.size))
          },
          () =>
            Metadata(Vec(
              Metadata.Entry(
                "surface-instructions-suq".assume[MinLength[1] & NoNul],
                "9.7.12.4. Surface Instructions: suq".assume[MinLength[1] & NoNul],
                "suq".assume[MinLength[1] & NoNul],
                "Surface Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "suq.query.b32   d, [a];\n\n.query = { .width, .height, .depth,\n           .channel_data_type, .channel_order,\n           .array_size, .memory_layout };"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(1, 5),
                Target(10),
                Vec(InstructionForm(
                  Opcode.assume("suq"),
                  Vec(OperandConstraint.Register, OperandConstraint.Address),
                  Vec(
                    _root_.scala.collection.immutable.Set(
                      ModifierName.assume("array_size"),
                      ModifierName.assume("channel_data_type"),
                      ModifierName.assume("channel_order"),
                      ModifierName.assume("depth"),
                      ModifierName.assume("height"),
                      ModifierName.assume("memory_layout"),
                      ModifierName.assume("width")
                    ).assume[MinLength[1]],
                    _root_.scala.collection.immutable.Set(ModifierName.assume("b32")).assume[MinLength[1]]
                  ),
                  _root_.scala.collection.immutable.Set(),
                  false
                ))
              )
            ).assume[MinLength[1]])
        )
    case Sured extends Definition(
          Opcode.assume("sured"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 2) Right(Instruction.Sured(operands(0), operands(1), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("sured", Vec(2), operands.size))
          },
          () =>
            Metadata(Vec(
              Metadata.Entry(
                "surface-instructions-sured".assume[MinLength[1] & NoNul],
                "9.7.12.3. Surface Instructions: sured".assume[MinLength[1] & NoNul],
                "sured".assume[MinLength[1] & NoNul],
                "Surface Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "sured.b.op.geom.ctype.clamp  [a,b],c; // byte addressing\nsured.p.op.geom.ctype.clamp  [a,b],c; // sample addressing\n\n.op    = { .add, .min, .max, .and, .or };\n.geom  = { .1d, .2d, .3d };\n.ctype = { .u32, .u64, .s32, .b32, .s64 };  // for sured.b\n.ctype = { .b32, .b64 };                    // for sured.p\n.clamp = { .trap, .clamp, .zero };"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(2, 0),
                Target(20),
                Vec(
                  InstructionForm(
                    Opcode.assume("sured"),
                    Vec(OperandConstraint.Address, OperandConstraint.Value),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("b")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("add"),
                        ModifierName.assume("and"),
                        ModifierName.assume("max"),
                        ModifierName.assume("min"),
                        ModifierName.assume("or")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("b32"),
                        ModifierName.assume("b64")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("clamp"),
                        ModifierName.assume("trap"),
                        ModifierName.assume("zero")
                      ).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("sured"),
                    Vec(OperandConstraint.Address, OperandConstraint.Value),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("p")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("add"),
                        ModifierName.assume("and"),
                        ModifierName.assume("max"),
                        ModifierName.assume("min"),
                        ModifierName.assume("or")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("b32"),
                        ModifierName.assume("b64")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("clamp"),
                        ModifierName.assume("trap"),
                        ModifierName.assume("zero")
                      ).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  )
                )
              )
            ).assume[MinLength[1]])
        )
    case Sust extends Definition(
          Opcode.assume("sust"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 2) Right(Instruction.Sust(operands(0), operands(1), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("sust", Vec(2), operands.size))
          },
          () =>
            Metadata(Vec(
              Metadata.Entry(
                "surface-instructions-sust".assume[MinLength[1] & NoNul],
                "9.7.12.2. Surface Instructions: sust".assume[MinLength[1] & NoNul],
                "sust".assume[MinLength[1] & NoNul],
                "Surface Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "sust.b.{1d,2d,3d}{.cop}.vec.ctype.clamp  [a, b], c;  // unformatted\nsust.p.{1d,2d,3d}.vec.b32.clamp          [a, b], c;  // formatted\n\nsust.b.{a1d,a2d}{.cop}.vec.ctype.clamp   [a, b], c;  // unformatted\n\n.cop   = { .wb, .cg, .cs, .wt };                     // cache operation\n.vec   = { none, .v2, .v4 };\n.ctype = { .b8 , .b16, .b32, .b64 };\n.clamp = { .trap, .clamp, .zero };"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(1, 0),
                Target(20),
                Vec(
                  InstructionForm(
                    Opcode.assume("sust"),
                    Vec(OperandConstraint.Address, OperandConstraint.Value),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("b")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("none"),
                        ModifierName.assume("v2"),
                        ModifierName.assume("v4")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("b16"),
                        ModifierName.assume("b32"),
                        ModifierName.assume("b64"),
                        ModifierName.assume("b8")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("clamp"),
                        ModifierName.assume("trap"),
                        ModifierName.assume("zero")
                      ).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName.assume("cg"),
                      ModifierName.assume("cs"),
                      ModifierName.assume("wb"),
                      ModifierName.assume("wt")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("sust"),
                    Vec(OperandConstraint.Address, OperandConstraint.Value),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("p")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("none"),
                        ModifierName.assume("v2"),
                        ModifierName.assume("v4")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("b32")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("clamp"),
                        ModifierName.assume("trap"),
                        ModifierName.assume("zero")
                      ).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  )
                )
              )
            ).assume[MinLength[1]])
        )
    case Szext extends Definition(
          Opcode.assume("szext"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 3) Right(Instruction.Szext(operands(0), operands(1), operands(2), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("szext", Vec(3), operands.size))
          },
          () =>
            Metadata(Vec(
              Metadata.Entry(
                "integer-arithmetic-instructions-szext".assume[MinLength[1] & NoNul],
                "9.7.1.22. Integer Arithmetic Instructions: szext".assume[MinLength[1] & NoNul],
                "szext".assume[MinLength[1] & NoNul],
                "Integer Arithmetic Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "szext.mode.type  d, a, b;\n\n.mode = { .clamp, .wrap };\n.type = { .u32, .s32 };"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(7, 6),
                Target(70),
                Vec(InstructionForm(
                  Opcode.assume("szext"),
                  Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                  Vec(
                    _root_.scala.collection.immutable.Set(
                      ModifierName.assume("clamp"),
                      ModifierName.assume("wrap")
                    ).assume[MinLength[1]],
                    _root_.scala.collection.immutable.Set(
                      ModifierName.assume("s32"),
                      ModifierName.assume("u32")
                    ).assume[MinLength[1]]
                  ),
                  _root_.scala.collection.immutable.Set(),
                  true
                ))
              )
            ).assume[MinLength[1]])
        )
    case Tanh extends Definition(
          Opcode.assume("tanh"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 2) Right(Instruction.Tanh(operands(0), operands(1), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("tanh", Vec(2), operands.size))
          },
          () =>
            Metadata(Vec(
              Metadata.Entry(
                "floating-point-instructions-tanh".assume[MinLength[1] & NoNul],
                "9.7.3.22. Floating Point Instructions: tanh".assume[MinLength[1] & NoNul],
                "tanh".assume[MinLength[1] & NoNul],
                "Floating Point Instructions".assume[MinLength[1] & NoNul],
                Vec[String]("tanh.approx.f32 d, a;").map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(7, 0),
                Target(75),
                Vec(InstructionForm(
                  Opcode.assume("tanh"),
                  Vec(OperandConstraint.Register, OperandConstraint.Value),
                  Vec(
                    _root_.scala.collection.immutable.Set(ModifierName.assume("approx")).assume[MinLength[1]],
                    _root_.scala.collection.immutable.Set(ModifierName.assume("f32")).assume[MinLength[1]]
                  ),
                  _root_.scala.collection.immutable.Set(),
                  true
                ))
              ),
              Metadata.Entry(
                "half-precision-floating-point-instructions-tanh".assume[MinLength[1] & NoNul],
                "9.7.4.9. Half Precision Floating Point Instructions: tanh".assume[MinLength[1] & NoNul],
                "tanh".assume[MinLength[1] & NoNul],
                "Half Precision Floating Point Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "tanh.approx.type d, a;\n\n.type = {.f16, .f16x2, .bf16, .bf16x2}"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(7, 0),
                Target(75),
                Vec(InstructionForm(
                  Opcode.assume("tanh"),
                  Vec(OperandConstraint.Register, OperandConstraint.Value),
                  Vec(
                    _root_.scala.collection.immutable.Set(ModifierName.assume("approx")).assume[MinLength[1]],
                    _root_.scala.collection.immutable.Set(
                      ModifierName.assume("bf16"),
                      ModifierName.assume("bf16x2"),
                      ModifierName.assume("f16"),
                      ModifierName.assume("f16x2")
                    ).assume[MinLength[1]]
                  ),
                  _root_.scala.collection.immutable.Set(),
                  false
                ))
              )
            ).assume[MinLength[1]])
        )
    case Tcgen05Alloc extends Definition(
          Opcode.assume("tcgen05.alloc"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 2) Right(Instruction.Tcgen05Alloc(operands(0), operands(1), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("tcgen05.alloc", Vec(2), operands.size))
          },
          () =>
            Metadata(Vec(
              Metadata.Entry(
                "tcgen05-instructions-tcgen05-alloc-dealloc-relinquish-alloc-permit".assume[MinLength[1] & NoNul],
                "9.7.17.7.1. Tensorcore 5th Generation Instructions: tcgen05.alloc, tcgen05.dealloc, tcgen05.relinquish_alloc_permit".assume[
                  MinLength[1] & NoNul
                ],
                "tcgen05.alloc, tcgen05.dealloc, tcgen05.relinquish_alloc_permit".assume[MinLength[1] & NoNul],
                "Tensorcore 5th Generation Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "tcgen05.alloc.cta_group.sync.aligned{.shared::cta}.b32  [dst], nCols;\n\ntcgen05.dealloc.cta_group.sync.aligned.b32              taddr, nCols;\n\ntcgen05.relinquish_alloc_permit.cta_group.sync.aligned;\n\n.cta_group = { .cta_group::1, .cta_group::2 }"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(8, 6),
                Target(100),
                Vec(InstructionForm(
                  Opcode.assume("tcgen05.alloc"),
                  Vec(OperandConstraint.Address, OperandConstraint.Value),
                  Vec(
                    _root_.scala.collection.immutable.Set(ModifierName.assume("sync")).assume[MinLength[1]],
                    _root_.scala.collection.immutable.Set(ModifierName.assume("aligned")).assume[MinLength[1]],
                    _root_.scala.collection.immutable.Set(ModifierName.assume("b32")).assume[MinLength[1]]
                  ),
                  _root_.scala.collection.immutable.Set(ModifierName.assume("shared::cta")),
                  false
                ))
              )
            ).assume[MinLength[1]])
        )
    case Tcgen05Commit extends Definition(
          Opcode.assume("tcgen05.commit"),
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
            Metadata(Vec(
              Metadata.Entry(
                "tcgen-async-sync-operations-commit".assume[MinLength[1] & NoNul],
                "9.7.17.12.1. TensorCore 5th Generation Instructions: tcgen05.commit".assume[MinLength[1] & NoNul],
                "tcgen05.commit".assume[MinLength[1] & NoNul],
                "TensorCore 5th Generation Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "tcgen05.commit.cta_group.completion_mechanism{.shared::cluster}{.multicast}.b64\n                                                            [mbar] {, ctaMask};\n\n.completion_mechanism = { .mbarrier::arrive::one }\n.cta_group            = { .cta_group::1, .cta_group::2 }\n.multicast            = { .multicast::cluster }"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(8, 6),
                Target(100),
                Vec(InstructionForm(
                  Opcode.assume("tcgen05.commit"),
                  Vec(OperandConstraint.Address, OperandConstraint.Value),
                  Vec(
                    _root_.scala.collection.immutable.Set(ModifierName.assume(
                      "mbarrier::arrive::one"
                    )).assume[MinLength[1]],
                    _root_.scala.collection.immutable.Set(ModifierName.assume("b64")).assume[MinLength[1]]
                  ),
                  _root_.scala.collection.immutable.Set(
                    ModifierName.assume("multicast::cluster"),
                    ModifierName.assume("shared::cluster")
                  ),
                  false
                ))
              )
            ).assume[MinLength[1]])
        )
    case Tcgen05Cp extends Definition(
          Opcode.assume("tcgen05.cp"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 2) Right(Instruction.Tcgen05Cp(operands(0), operands(1), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("tcgen05.cp", Vec(2), operands.size))
          },
          () =>
            Metadata(Vec(
              Metadata.Entry(
                "tcgen05-instructions-tcgen05-cp".assume[MinLength[1] & NoNul],
                "9.7.17.9.2. Tensorcore 5th Generation Instructions: tcgen05.cp".assume[MinLength[1] & NoNul],
                "tcgen05.cp".assume[MinLength[1] & NoNul],
                "Tensorcore 5th Generation Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "tcgen05.cp.cta_group.shape{.multicast}{.dst_fmt.src_fmt} [taddr], s-desc;\n\n.cta_group = { .cta_group::1, .cta_group::2 }\n.src_fmt   = { .b6x16_p32 , .b4x16_p64 }\n.dst_fmt   = { .b8x16 }\n.shape     = { .128x256b, .4x256b, .128x128b, .64x128b**, .32x128b*** }\n.multicast = { .warpx2::02_13** , .warpx2::01_23**, .warpx4*** }"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(8, 6),
                Target(100),
                Vec(InstructionForm(
                  Opcode.assume("tcgen05.cp"),
                  Vec(OperandConstraint.Address, OperandConstraint.Value),
                  Vec(),
                  _root_.scala.collection.immutable.Set(
                    ModifierName.assume("b4x16_p64"),
                    ModifierName.assume("b6x16_p32"),
                    ModifierName.assume("b8x16")
                  ),
                  false
                ))
              )
            ).assume[MinLength[1]])
        )
    case Tcgen05Dealloc extends Definition(
          Opcode.assume("tcgen05.dealloc"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 2) Right(Instruction.Tcgen05Dealloc(operands(0), operands(1), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("tcgen05.dealloc", Vec(2), operands.size))
          },
          () =>
            Metadata(Vec(
              Metadata.Entry(
                "tcgen05-instructions-tcgen05-alloc-dealloc-relinquish-alloc-permit".assume[MinLength[1] & NoNul],
                "9.7.17.7.1. Tensorcore 5th Generation Instructions: tcgen05.alloc, tcgen05.dealloc, tcgen05.relinquish_alloc_permit".assume[
                  MinLength[1] & NoNul
                ],
                "tcgen05.alloc, tcgen05.dealloc, tcgen05.relinquish_alloc_permit".assume[MinLength[1] & NoNul],
                "Tensorcore 5th Generation Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "tcgen05.alloc.cta_group.sync.aligned{.shared::cta}.b32  [dst], nCols;\n\ntcgen05.dealloc.cta_group.sync.aligned.b32              taddr, nCols;\n\ntcgen05.relinquish_alloc_permit.cta_group.sync.aligned;\n\n.cta_group = { .cta_group::1, .cta_group::2 }"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(8, 6),
                Target(100),
                Vec(InstructionForm(
                  Opcode.assume("tcgen05.dealloc"),
                  Vec(OperandConstraint.Value, OperandConstraint.Value),
                  Vec(
                    _root_.scala.collection.immutable.Set(ModifierName.assume("sync")).assume[MinLength[1]],
                    _root_.scala.collection.immutable.Set(ModifierName.assume("aligned")).assume[MinLength[1]],
                    _root_.scala.collection.immutable.Set(ModifierName.assume("b32")).assume[MinLength[1]]
                  ),
                  _root_.scala.collection.immutable.Set(),
                  false
                ))
              )
            ).assume[MinLength[1]])
        )
    case Tcgen05FenceAfterThreadSync extends Definition(
          Opcode.assume("tcgen05.fence::after_thread_sync"),
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
            Metadata(Vec(
              Metadata.Entry(
                "tcgen05-special-sync-operations-fence".assume[MinLength[1] & NoNul],
                "9.7.17.11.1. TensorCore 5th Generation Instructions: tcgen05.fence".assume[MinLength[1] & NoNul],
                "tcgen05.fence".assume[MinLength[1] & NoNul],
                "TensorCore 5th Generation Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "tcgen05.fence::before_thread_sync ;\ntcgen05.fence::after_thread_sync  ;"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(8, 6),
                Target(100),
                Vec(InstructionForm(
                  Opcode.assume("tcgen05.fence::after_thread_sync"),
                  Vec(),
                  Vec(),
                  _root_.scala.collection.immutable.Set(),
                  false
                ))
              )
            ).assume[MinLength[1]])
        )
    case Tcgen05FenceBeforeThreadSync extends Definition(
          Opcode.assume("tcgen05.fence::before_thread_sync"),
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
            Metadata(Vec(
              Metadata.Entry(
                "tcgen05-special-sync-operations-fence".assume[MinLength[1] & NoNul],
                "9.7.17.11.1. TensorCore 5th Generation Instructions: tcgen05.fence".assume[MinLength[1] & NoNul],
                "tcgen05.fence".assume[MinLength[1] & NoNul],
                "TensorCore 5th Generation Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "tcgen05.fence::before_thread_sync ;\ntcgen05.fence::after_thread_sync  ;"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(8, 6),
                Target(100),
                Vec(InstructionForm(
                  Opcode.assume("tcgen05.fence::before_thread_sync"),
                  Vec(),
                  Vec(),
                  _root_.scala.collection.immutable.Set(),
                  false
                ))
              )
            ).assume[MinLength[1]])
        )
    case Tcgen05Ld extends Definition(
          Opcode.assume("tcgen05.ld"),
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
            Metadata(Vec(
              Metadata.Entry(
                "tcgen05-instructions-tcgen05-ld".assume[MinLength[1] & NoNul],
                "9.7.17.8.3. Tensorcore 5th Generation Instructions: tcgen05.ld".assume[MinLength[1] & NoNul],
                "tcgen05.ld".assume[MinLength[1] & NoNul],
                "Tensorcore 5th Generation Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "// Base load instruction:\n\ntcgen05.ld.sync.aligned.shape1.num{.pack}.b32    r, [taddr];\n\ntcgen05.ld.sync.aligned.shape2.num{.pack}.b32    r, [taddr], immHalfSplitoff;\n\n.shape1 = { .16x64b, .16x128b, .16x256b, .32x32b }\n.shape2 = { .16x32bx2 }\n.num    = { .x1, .x2, .x4, .x8, .x16, .x32, .x64, .x128 }\n.pack   = { .pack::16b }\n\n// Floating point type load along with reduction :\n\ntcgen05.ld.red.sync.aligned.shape3.num.redOp{.abs}{.NaN}.f32 r, redval, [taddr];\n\ntcgen05.ld.red.sync.aligned.shape4.num.redOp{.abs}{.NaN}.f32 r, redval, [taddr], immHalfSplitoff;\n\n// Integer type load along with reduction :\n\ntcgen05.ld.red.sync.aligned.shape3.num.redOp.type r, redval, [taddr];\n\ntcgen05.ld.red.sync.aligned.shape4.num.redOp.type r, redval, [taddr], immHalfSplitoff;\n\n.shape3 = { .32x32b   }\n.shape4 = { .16x32bx2 }\n.redOp  = { .min, .max }\n.type   = { .u32, .s32 }"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(8, 6),
                Target(100),
                Vec(
                  InstructionForm(
                    Opcode.assume("tcgen05.ld"),
                    Vec(OperandConstraint.Register, OperandConstraint.Address),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("sync")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("aligned")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("x1"),
                        ModifierName.assume("x128"),
                        ModifierName.assume("x16"),
                        ModifierName.assume("x2"),
                        ModifierName.assume("x32"),
                        ModifierName.assume("x4"),
                        ModifierName.assume("x64"),
                        ModifierName.assume("x8")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("b32")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("tcgen05.ld"),
                    Vec(OperandConstraint.Register, OperandConstraint.Address, OperandConstraint.Value),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("sync")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("aligned")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("x1"),
                        ModifierName.assume("x128"),
                        ModifierName.assume("x16"),
                        ModifierName.assume("x2"),
                        ModifierName.assume("x32"),
                        ModifierName.assume("x4"),
                        ModifierName.assume("x64"),
                        ModifierName.assume("x8")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("b32")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("tcgen05.ld"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Address),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("red")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("sync")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("aligned")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("x1"),
                        ModifierName.assume("x128"),
                        ModifierName.assume("x16"),
                        ModifierName.assume("x2"),
                        ModifierName.assume("x32"),
                        ModifierName.assume("x4"),
                        ModifierName.assume("x64"),
                        ModifierName.assume("x8")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("max"),
                        ModifierName.assume("min")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("f32")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName.assume("NaN"), ModifierName.assume("abs")),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("tcgen05.ld"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Address,
                      OperandConstraint.Value
                    ),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("red")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("sync")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("aligned")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("x1"),
                        ModifierName.assume("x128"),
                        ModifierName.assume("x16"),
                        ModifierName.assume("x2"),
                        ModifierName.assume("x32"),
                        ModifierName.assume("x4"),
                        ModifierName.assume("x64"),
                        ModifierName.assume("x8")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("max"),
                        ModifierName.assume("min")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("f32")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName.assume("NaN"), ModifierName.assume("abs")),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("tcgen05.ld"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Address),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("red")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("sync")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("aligned")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("x1"),
                        ModifierName.assume("x128"),
                        ModifierName.assume("x16"),
                        ModifierName.assume("x2"),
                        ModifierName.assume("x32"),
                        ModifierName.assume("x4"),
                        ModifierName.assume("x64"),
                        ModifierName.assume("x8")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("max"),
                        ModifierName.assume("min")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("s32"),
                        ModifierName.assume("u32")
                      ).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("tcgen05.ld"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Address,
                      OperandConstraint.Value
                    ),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("red")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("sync")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("aligned")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("x1"),
                        ModifierName.assume("x128"),
                        ModifierName.assume("x16"),
                        ModifierName.assume("x2"),
                        ModifierName.assume("x32"),
                        ModifierName.assume("x4"),
                        ModifierName.assume("x64"),
                        ModifierName.assume("x8")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("max"),
                        ModifierName.assume("min")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("s32"),
                        ModifierName.assume("u32")
                      ).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  )
                )
              )
            ).assume[MinLength[1]])
        )
    case Tcgen05Mma extends Definition(
          Opcode.assume("tcgen05.mma"),
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
            Metadata(Vec(
              Metadata.Entry(
                "tcgen05-mma-instructions-mma".assume[MinLength[1] & NoNul],
                "9.7.17.10.9.1. TensorCore 5th Generation Instructions: tcgen05.mma".assume[MinLength[1] & NoNul],
                "tcgen05.mma".assume[MinLength[1] & NoNul],
                "TensorCore 5th Generation Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "// 1. Floating-point type without block scaling:\n\ntcgen05.mma.cta_group.kind   [d-tmem],  a-desc,  b-desc, idesc,\n                             { disable-output-lane }, enable-input-d {, scale-input-d};\n\ntcgen05.mma.cta_group.kind   [d-tmem], [a-tmem], b-desc, idesc,\n                             { disable-output-lane }, enable-input-d {, scale-input-d};\n\n.kind      = { .kind::f16, .kind::tf32, .kind::f8f6f4 }\n.cta_group = { .cta_group::1, .cta_group::2 }\n\n----------------------------------------------------------------------------------\n\n// 2. Floating-point type with block scaling:\n\ntcgen05.mma.cta_group.kind.block_scale{.scale_vectorsize}\n                                        [d-tmem],  a-desc,  b-desc, idesc,\n                                        [scale-A-tmem], [scale-B-tmem], enable-input-d;\n\ntcgen05.mma.cta_group.kind.block_scale{.scale_vectorsize}\n                                        [d-tmem], [a-tmem], b-desc, idesc,\n                                        [scale-A-tmem], [scale-B-tmem], enable-input-d;\n\n.kind = { .kind::mxf8f6f4, .kind::mxf4, .kind::mxf4nvf4 }\n.cta_group      = { .cta_group::1,   .cta_group::2 }\n.scale_vectorsize = { .scale_vec::1X, .scale_vec::2X, .scale_vec::4X, .block16, .block32 }\n\n----------------------------------------------------------------------------------\n\n// 3. Convolution MMA for floating-point type without block scaling:\n\ntcgen05.mma.cta_group.kind.collector_usage [d-tmem],  a-desc,  b-desc, idesc,\n                                           { disable-output-lane }, enable-input-d {, scale-input-d};\n\ntcgen05.mma.cta_group.kind{.ashift}.collector_usage [d-tmem], [a-tmem], b-desc, idesc,\n                                                    { disable-output-lane }, enable-input-d {, scale-input-d};\n\ntcgen05.mma.cta_group.kind.ashift{.collector_usage} [d-tmem], [a-tmem], b-desc, idesc,\n                                                    { disable-output-lane }, enable-input-d {, scale-input-d};\n\n.kind      = { .kind::f16, .kind::tf32, .kind::f8f6f4 }\n.cta_group = { .cta_group::1,   .cta_group::2 }\n.collector_usage = { .collector::buffer::op }\n::buffer         = { ::a }\n::op             = { ::fill, ::use, ::lastuse, ::discard* }\n\n----------------------------------------------------------------------------------\n\n// 4. Activation Stationary MMA for floating-point type with block scaling:\n\ntcgen05.mma.cta_group.kind.block_scale{.scale_vectorsize}.collector_usage\n                                            [d-tmem],  a-desc,  b-desc, idesc,\n                                            [scale-A-tmem], [scale-B-tmem], enable-input-d;\n\ntcgen05.mma.cta_group.kind.block_scale{.scale_vectorsize}.collector_usage\n                                            [d-tmem], [a-tmem], b-desc, idesc,\n                                            [scale-A-tmem], [scale-B-tmem], enable-input-d;\n\n.cta_group       = { .cta_group::1,   .cta_group::2 }\n.scale_vectorsize  = { .scale_vec::1X, .scale_vec::2X, .scale_vec::4X, .block16, .block32 }\n.kind            = { .kind::mxf8f6f4, .kind::mxf4, .kind::mxf4nvf4 }\n.collector_usage = { .collector::buffer::op }\n::buffer         = { ::a }\n::op             = { ::fill, ::use, ::lastuse, ::discard* }\n\n----------------------------------------------------------------------------------\n\n// 5. Integer type:\n\ntcgen05.mma.cta_group.kind::i8  [d-tmem],  a-desc,  b-desc, idesc,\n                                { disable-output-lane }, enable-input-d;\n\ntcgen05.mma.cta_group.kind::i8  [d-tmem], [a-tmem], b-desc, idesc,\n                                { disable-output-lane }, enable-input-d;\n\n.cta_group = { .cta_group::1,   .cta_group::2  }\n\n----------------------------------------------------------------------------------\n\n// 6. Convolution MMA for integer type:\n\ntcgen05.mma.cta_group.kind::i8.collector_usage          [d-tmem],  a-desc,  b-desc, idesc,\n                                                        { disable-output-lane }, enable-input-d;\n\ntcgen05.mma.cta_group.kind::i8.ashift{.collector_usage} [d-tmem], [a-tmem], b-desc, idesc,\n                                                        { disable-output-lane }, enable-input-d;\n\ntcgen05.mma.cta_group.kind::i8{.ashift}.collector_usage [d-tmem], [a-tmem], b-desc, idesc,\n                                                        { disable-output-lane }, enable-input-d;\n\n.cta_group       = { .cta_group::1,   .cta_group::2  }\n.collector_usage = { .collector::buffer::op }\n::buffer         = { ::a }\n::op             = { ::fill, ::use, ::lastuse, ::discard* }"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(8, 6),
                Target(100),
                Vec(
                  InstructionForm(
                    Opcode.assume("tcgen05.mma"),
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.VectorOperand,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(_root_.scala.collection.immutable.Set(
                      ModifierName.assume("kind::mxf4"),
                      ModifierName.assume("kind::mxf4nvf4"),
                      ModifierName.assume("kind::mxf8f6f4")
                    ).assume[MinLength[1]]),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("tcgen05.mma"),
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.VectorOperand,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(_root_.scala.collection.immutable.Set(
                      ModifierName.assume("kind::mxf4"),
                      ModifierName.assume("kind::mxf4nvf4"),
                      ModifierName.assume("kind::mxf8f6f4")
                    ).assume[MinLength[1]]),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("tcgen05.mma"),
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
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("kind::mxf4"),
                        ModifierName.assume("kind::mxf4nvf4"),
                        ModifierName.assume("kind::mxf8f6f4")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("block_scale")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName.assume("block16"),
                      ModifierName.assume("block32")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("tcgen05.mma"),
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
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("kind::mxf4"),
                        ModifierName.assume("kind::mxf4nvf4"),
                        ModifierName.assume("kind::mxf8f6f4")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("block_scale")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName.assume("block16"),
                      ModifierName.assume("block32")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("tcgen05.mma"),
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
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("kind::mxf4"),
                        ModifierName.assume("kind::mxf4nvf4"),
                        ModifierName.assume("kind::mxf8f6f4")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume(
                        "collector::buffer::op"
                      )).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("tcgen05.mma"),
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
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("kind::mxf4"),
                        ModifierName.assume("kind::mxf4nvf4"),
                        ModifierName.assume("kind::mxf8f6f4")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume(
                        "collector::buffer::op"
                      )).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName.assume("ashift")),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("tcgen05.mma"),
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
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("kind::mxf4"),
                        ModifierName.assume("kind::mxf4nvf4"),
                        ModifierName.assume("kind::mxf8f6f4")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("ashift")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName.assume("collector::buffer::op")),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("tcgen05.mma"),
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
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("kind::mxf4"),
                        ModifierName.assume("kind::mxf4nvf4"),
                        ModifierName.assume("kind::mxf8f6f4")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("block_scale")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume(
                        "collector::buffer::op"
                      )).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName.assume("block16"),
                      ModifierName.assume("block32")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("tcgen05.mma"),
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
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("kind::mxf4"),
                        ModifierName.assume("kind::mxf4nvf4"),
                        ModifierName.assume("kind::mxf8f6f4")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("block_scale")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume(
                        "collector::buffer::op"
                      )).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName.assume("block16"),
                      ModifierName.assume("block32")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("tcgen05.mma"),
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.VectorOperand,
                      OperandConstraint.Value
                    ),
                    Vec(_root_.scala.collection.immutable.Set(ModifierName.assume("kind::i8")).assume[MinLength[1]]),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("tcgen05.mma"),
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.VectorOperand,
                      OperandConstraint.Value
                    ),
                    Vec(_root_.scala.collection.immutable.Set(ModifierName.assume("kind::i8")).assume[MinLength[1]]),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("tcgen05.mma"),
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.VectorOperand,
                      OperandConstraint.Value
                    ),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("kind::i8")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume(
                        "collector::buffer::op"
                      )).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("tcgen05.mma"),
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.VectorOperand,
                      OperandConstraint.Value
                    ),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("kind::i8")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("ashift")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName.assume("collector::buffer::op")),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("tcgen05.mma"),
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.VectorOperand,
                      OperandConstraint.Value
                    ),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("kind::i8")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume(
                        "collector::buffer::op"
                      )).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName.assume("ashift")),
                    false
                  )
                )
              )
            ).assume[MinLength[1]])
        )
    case Tcgen05MmaSp extends Definition(
          Opcode.assume("tcgen05.mma.sp"),
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
            Metadata(Vec(
              Metadata.Entry(
                "tcgen05-mma-instructions-mma-sp".assume[MinLength[1] & NoNul],
                "9.7.17.10.9.2. TensorCore 5th Generation Instructions: tcgen05.mma.sp".assume[MinLength[1] & NoNul],
                "tcgen05.mma.sp".assume[MinLength[1] & NoNul],
                "TensorCore 5th Generation Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "// 1. Floating-point type without block scaling:\n\ntcgen05.mma.sp.cta_group.kind  [d-tmem],  a-desc,  b-desc, [sp-meta-tmem] ,  idesc,\n                               { disable-output-lane }, enable-input-d{, scale-input-d};\n\ntcgen05.mma.sp.cta_group.kind  [d-tmem], [a-tmem], b-desc, [sp-meta-tmem] , idesc,\n                               { disable-output-lane }, enable-input-d{, scale-input-d};\n\n.kind       = { .kind::f16, , .kind::tf32, .kind::f8f6f4 }\n.cta_group  = { .cta_group::1,  .cta_group::2 }\n\n----------------------------------------------------------------------------------\n\n// 2. Floating-point type with block scaling:\n\ntcgen05.mma.sp.cta_group.kind.block_scale{.scale_vectorsize}\n                                         [d-tmem],  a-desc,  b-desc , [sp-meta-tmem] , idesc,\n                                         [scale-A-tmem], [scale-B-tmem], enable-input-d;\n\ntcgen05.mma.sp.cta_group.kind.block_scale{.scale_vectorsize}\n                                         [d-tmem], [a-tmem], b-desc , [sp-meta-tmem] , idesc,\n                                         [scale-A-tmem], [scale-B-tmem], enable-input-d;\n\n.scale_vectorsize = { .scale_vec::1X, .scale_vec::2X, .scale_vec::4X, .block16, .block32 }\n.cta_group      = { .cta_group::1,  .cta_group::2 }\n.kind = { .kind::mxf8f6f4, .kind::mxf4, .kind::mxf4nvf4 }\n\n----------------------------------------------------------------------------------\n\n// 3. Convolution MMA with floating-point type without block scaling:\n\ntcgen05.mma.sp.cta_group.kind.collector_usage           [d-tmem],  a-desc,  b-desc,\n                                                        [sp-meta-tmem] ,  idesc,\n                                                        { disable-output-lane }, enable-input-d\n                                                        {, scale-input-d};\n\ntcgen05.mma.sp.cta_group.kind.ashift{.collector_usage}  [d-tmem], [a-tmem], b-desc,\n                                                        [sp-meta-tmem] , idesc,\n                                                        { disable-output-lane }, enable-input-d\n                                                        {, scale-input-d};\n\ntcgen05.mma.sp.cta_group.kind{.ashift}.collector_usage  [d-tmem], [a-tmem], b-desc,\n                                                        [sp-meta-tmem] , idesc,\n                                                        { disable-output-lane }, enable-input-d\n                                                        {, scale-input-d};\n\n.kind            = { .kind::f16, .kind::tf32, .kind::f8f6f4 }\n.collector_usage = { .collector::buffer::op }\n::buffer         = { ::a }\n::op             = { ::fill, ::use, ::lastuse, ::discard* }\n\n----------------------------------------------------------------------------------\n\n// 4. Activation Stationary MMA with floating-point type with block scaling:\n\ntcgen05.mma.sp.cta_group.kind.block_scale{.scale_vectorsize}.collector_usage\n                                         [d-tmem],  a-desc,  b-desc , [sp-meta-tmem] , idesc,\n                                         [scale-A-tmem], [scale-B-tmem], enable-input-d;\n\ntcgen05.mma.sp.cta_group.kind.block_scale{.scale_vectorsize}.collector_usage\n                                         [d-tmem], [a-tmem], b-desc , [sp-meta-tmem] , idesc,\n                                         [scale-A-tmem], [scale-B-tmem], enable-input-d;\n\n.kind = { .kind::mxf8f6f4, .kind::mxf4, .kind::mxf4nvf4 }\n.scale_vectorsize = { .scale_vec::1X, .scale_vec::2X, .scale_vec::4X, .block16, .block32 }\n.collector_usage = { .collector::buffer::op }\n::buffer         = { ::a }\n::op             = { ::fill, ::use, ::lastuse, ::discard* }\n\n----------------------------------------------------------------------------------\n\n// 5. Integer type:\n\ntcgen05.mma.sp.cta_group.kind::i8 [d-tmem],  a-desc,  b-desc, [sp-meta-tmem] , idesc,\n                                  { disable-output-lane }, enable-input-d;\n\ntcgen05.mma.sp.cta_group.kind::i8 [d-tmem], [a-tmem], b-desc, [sp-meta-tmem] , idesc,\n                                  { disable-output-lane }, enable-input-d;\n\n.cta_group      = { .cta_group::1,  .cta_group::2 }\n\n----------------------------------------------------------------------------------\n\n// 6. Convolution MMA with Integer type:\n\ntcgen05.mma.sp.cta_group.kind::i8.collector_usage          [d-tmem],  a-desc,  b-desc,\n                                                           [sp-meta-tmem] , idesc,\n                                                           { disable-output-lane }, enable-input-d;\n\ntcgen05.mma.sp.cta_group.kind::i8.ashift{.collector_usage} [d-tmem], [a-tmem], b-desc,\n                                                           [sp-meta-tmem], idesc ,\n                                                           { disable-output-lane }, enable-input-d;\n\ntcgen05.mma.sp.cta_group.kind::i8{.ashift}.collector_usage [d-tmem], [a-tmem], b-desc,\n                                                           [sp-meta-tmem], idesc ,\n                                                           { disable-output-lane }, enable-input-d;\n\n.collector_usage = { .collector::buffer::op }\n::buffer         = { ::a }\n::op             = { ::fill, ::use, ::lastuse, ::discard* }"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(8, 6),
                Target(100),
                Vec(
                  InstructionForm(
                    Opcode.assume("tcgen05.mma.sp"),
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
                    Vec(_root_.scala.collection.immutable.Set(
                      ModifierName.assume("kind::mxf4"),
                      ModifierName.assume("kind::mxf4nvf4"),
                      ModifierName.assume("kind::mxf8f6f4")
                    ).assume[MinLength[1]]),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("tcgen05.mma.sp"),
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
                    Vec(_root_.scala.collection.immutable.Set(
                      ModifierName.assume("kind::mxf4"),
                      ModifierName.assume("kind::mxf4nvf4"),
                      ModifierName.assume("kind::mxf8f6f4")
                    ).assume[MinLength[1]]),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("tcgen05.mma.sp"),
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
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("kind::mxf4"),
                        ModifierName.assume("kind::mxf4nvf4"),
                        ModifierName.assume("kind::mxf8f6f4")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("block_scale")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName.assume("block16"),
                      ModifierName.assume("block32")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("tcgen05.mma.sp"),
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
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("kind::mxf4"),
                        ModifierName.assume("kind::mxf4nvf4"),
                        ModifierName.assume("kind::mxf8f6f4")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("block_scale")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName.assume("block16"),
                      ModifierName.assume("block32")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("tcgen05.mma.sp"),
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
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("kind::mxf4"),
                        ModifierName.assume("kind::mxf4nvf4"),
                        ModifierName.assume("kind::mxf8f6f4")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume(
                        "collector::buffer::op"
                      )).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("tcgen05.mma.sp"),
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
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("kind::mxf4"),
                        ModifierName.assume("kind::mxf4nvf4"),
                        ModifierName.assume("kind::mxf8f6f4")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("ashift")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName.assume("collector::buffer::op")),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("tcgen05.mma.sp"),
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
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("kind::mxf4"),
                        ModifierName.assume("kind::mxf4nvf4"),
                        ModifierName.assume("kind::mxf8f6f4")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume(
                        "collector::buffer::op"
                      )).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName.assume("ashift")),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("tcgen05.mma.sp"),
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
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("kind::mxf4"),
                        ModifierName.assume("kind::mxf4nvf4"),
                        ModifierName.assume("kind::mxf8f6f4")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("block_scale")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume(
                        "collector::buffer::op"
                      )).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName.assume("block16"),
                      ModifierName.assume("block32")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("tcgen05.mma.sp"),
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
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("kind::mxf4"),
                        ModifierName.assume("kind::mxf4nvf4"),
                        ModifierName.assume("kind::mxf8f6f4")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("block_scale")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume(
                        "collector::buffer::op"
                      )).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName.assume("block16"),
                      ModifierName.assume("block32")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("tcgen05.mma.sp"),
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.VectorOperand,
                      OperandConstraint.Value
                    ),
                    Vec(_root_.scala.collection.immutable.Set(ModifierName.assume("kind::i8")).assume[MinLength[1]]),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("tcgen05.mma.sp"),
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.VectorOperand,
                      OperandConstraint.Value
                    ),
                    Vec(_root_.scala.collection.immutable.Set(ModifierName.assume("kind::i8")).assume[MinLength[1]]),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("tcgen05.mma.sp"),
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
                      _root_.scala.collection.immutable.Set(ModifierName.assume("kind::i8")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume(
                        "collector::buffer::op"
                      )).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("tcgen05.mma.sp"),
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
                      _root_.scala.collection.immutable.Set(ModifierName.assume("kind::i8")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("ashift")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName.assume("collector::buffer::op")),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("tcgen05.mma.sp"),
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
                      _root_.scala.collection.immutable.Set(ModifierName.assume("kind::i8")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume(
                        "collector::buffer::op"
                      )).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName.assume("ashift")),
                    false
                  )
                )
              )
            ).assume[MinLength[1]])
        )
    case Tcgen05MmaWs extends Definition(
          Opcode.assume("tcgen05.mma.ws"),
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
            Metadata(Vec(
              Metadata.Entry(
                "tcgen05-mma-instructions-mma-ws".assume[MinLength[1] & NoNul],
                "9.7.17.10.9.3. TensorCore 5th Generation Instructions: tcgen05.mma.ws".assume[MinLength[1] & NoNul],
                "tcgen05.mma.ws".assume[MinLength[1] & NoNul],
                "TensorCore 5th Generation Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "// 1. Floating-point type without block scaling:\n\ntcgen05.mma.ws.cta_group::1.kind{.collector_usage}    [d-tmem],  a-desc,  b-desc,  idesc,\n                                                      enable-input-d {, zero-column-mask-desc };\n\ntcgen05.mma.ws.cta_group::1.kind{.collector_usage}    [d-tmem], [a-tmem], b-desc, idesc,\n                                                      enable-input-d {, zero-column-mask-desc };\n\n.kind = { .kind::f16, .kind::tf32, .kind::f8f6f4 }\n\n----------------------------------------------------------------------------------\n\n// 2. Integer type:\n\ntcgen05.mma.ws.cta_group::1.kind::i8{.collector_usage} [d-tmem],  a-desc,  b-desc, idesc,\n                                                       enable-input-d {, zero-column-mask-desc};\n\ntcgen05.mma.ws.cta_group::1.kind::i8{.collector_usage} [d-tmem], [a-tmem], b-desc, idesc,\n                                                       enable-input-d {, zero-column-mask-desc};\n\n.collector_usage = { .collector::buffer::op }\n::buffer = { ::b0, ::b1, ::b2, ::b3 }\n::op   = { ::fill, ::use, ::lastuse, ::discard}"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(8, 6),
                Target(100),
                Vec(
                  InstructionForm(
                    Opcode.assume("tcgen05.mma.ws"),
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("cta_group")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("kind::f16"),
                        ModifierName.assume("kind::f8f6f4"),
                        ModifierName.assume("kind::tf32")
                      ).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName.assume("collector::buffer::op")),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("tcgen05.mma.ws"),
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("cta_group")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("kind::f16"),
                        ModifierName.assume("kind::f8f6f4"),
                        ModifierName.assume("kind::tf32")
                      ).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName.assume("collector::buffer::op")),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("tcgen05.mma.ws"),
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("cta_group")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("kind::i8")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName.assume("collector::buffer::op")),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("tcgen05.mma.ws"),
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("cta_group")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("kind::i8")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName.assume("collector::buffer::op")),
                    false
                  )
                )
              )
            ).assume[MinLength[1]])
        )
    case Tcgen05MmaWsSp extends Definition(
          Opcode.assume("tcgen05.mma.ws.sp"),
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
            Metadata(Vec(
              Metadata.Entry(
                "tcgen05-mma-instructions-mma-ws-sp".assume[MinLength[1] & NoNul],
                "9.7.17.10.9.4. TensorCore 5th Generation Instructions: tcgen05.mma.ws.sp".assume[MinLength[1] & NoNul],
                "tcgen05.mma.ws.sp".assume[MinLength[1] & NoNul],
                "TensorCore 5th Generation Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "// 1. Floating-point type without block scaling:\n\ntcgen05.mma.ws.sp.cta_group::1.kind{.collector_usage} [d-tmem],  a-desc,  b-desc,\n                                                      [sp-meta-tmem] ,  idesc,\n                                                      enable-input-d {, zero-column-mask-desc};\n\ntcgen05.mma.ws.sp.cta_group::1.kind{.collector_usage} [d-tmem], [a-tmem], b-desc,\n                                                      [sp-meta-tmem] , idesc,\n                                                      enable-input-d {, zero-column-mask-desc};\n\n.kind = { .kind::f16, .kind::tf32, .kind::f8f6f4 }\n\n----------------------------------------------------------------------------------\n\n// 2. Integer type:\n\ntcgen05.mma.ws.sp.cta_group::1.kind::i8{.collector_usage} [d-tmem], a-desc, b-desc,\n                                                          [sp-meta-tmem] , idesc,\n                                                          enable-input-d {, zero-column-mask-desc};\n\ntcgen05.mma.ws.sp.cta_group::1.kind::i8{.collector_usage} [d-tmem], [a-tmem], b-desc,\n                                                          [sp-meta-tmem] , idesc,\n                                                          enable-input-d {, zero-column-mask-desc};\n\n.collector_usage = { .collector::buffer::op }\n::buffer = { ::b0, ::b1, ::b2, ::b3 }\n::op   = { ::fill, ::use, ::lastuse, ::discard }"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(8, 6),
                Target(100),
                Vec(
                  InstructionForm(
                    Opcode.assume("tcgen05.mma.ws.sp"),
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
                      _root_.scala.collection.immutable.Set(ModifierName.assume("cta_group")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("kind::f16"),
                        ModifierName.assume("kind::f8f6f4"),
                        ModifierName.assume("kind::tf32")
                      ).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName.assume("collector::buffer::op")),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("tcgen05.mma.ws.sp"),
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
                      _root_.scala.collection.immutable.Set(ModifierName.assume("cta_group")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("kind::f16"),
                        ModifierName.assume("kind::f8f6f4"),
                        ModifierName.assume("kind::tf32")
                      ).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName.assume("collector::buffer::op")),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("tcgen05.mma.ws.sp"),
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
                      _root_.scala.collection.immutable.Set(ModifierName.assume("cta_group")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("kind::i8")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName.assume("collector::buffer::op")),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("tcgen05.mma.ws.sp"),
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
                      _root_.scala.collection.immutable.Set(ModifierName.assume("cta_group")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("kind::i8")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName.assume("collector::buffer::op")),
                    false
                  )
                )
              )
            ).assume[MinLength[1]])
        )
    case Tcgen05RelinquishAllocPermit extends Definition(
          Opcode.assume("tcgen05.relinquish_alloc_permit"),
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
            Metadata(Vec(
              Metadata.Entry(
                "tcgen05-instructions-tcgen05-alloc-dealloc-relinquish-alloc-permit".assume[MinLength[1] & NoNul],
                "9.7.17.7.1. Tensorcore 5th Generation Instructions: tcgen05.alloc, tcgen05.dealloc, tcgen05.relinquish_alloc_permit".assume[
                  MinLength[1] & NoNul
                ],
                "tcgen05.alloc, tcgen05.dealloc, tcgen05.relinquish_alloc_permit".assume[MinLength[1] & NoNul],
                "Tensorcore 5th Generation Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "tcgen05.alloc.cta_group.sync.aligned{.shared::cta}.b32  [dst], nCols;\n\ntcgen05.dealloc.cta_group.sync.aligned.b32              taddr, nCols;\n\ntcgen05.relinquish_alloc_permit.cta_group.sync.aligned;\n\n.cta_group = { .cta_group::1, .cta_group::2 }"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(8, 6),
                Target(100),
                Vec(InstructionForm(
                  Opcode.assume("tcgen05.relinquish_alloc_permit"),
                  Vec(),
                  Vec(
                    _root_.scala.collection.immutable.Set(ModifierName.assume("sync")).assume[MinLength[1]],
                    _root_.scala.collection.immutable.Set(ModifierName.assume("aligned")).assume[MinLength[1]]
                  ),
                  _root_.scala.collection.immutable.Set(),
                  false
                ))
              )
            ).assume[MinLength[1]])
        )
    case Tcgen05Shift extends Definition(
          Opcode.assume("tcgen05.shift"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 1) Right(Instruction.Tcgen05Shift(operands(0), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("tcgen05.shift", Vec(1), operands.size))
          },
          () =>
            Metadata(Vec(
              Metadata.Entry(
                "tcgen05-instructions-tcgen05-shift".assume[MinLength[1] & NoNul],
                "9.7.17.9.3. Tensorcore 5th Generation Instructions: tcgen05.shift".assume[MinLength[1] & NoNul],
                "tcgen05.shift".assume[MinLength[1] & NoNul],
                "Tensorcore 5th Generation Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "tcgen05.shift.cta_group.down  [taddr];\n\n.cta_group = { .cta_group::1, .cta_group::2 }"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(8, 6),
                Target(10),
                Vec(InstructionForm(
                  Opcode.assume("tcgen05.shift"),
                  Vec(OperandConstraint.Address),
                  Vec(_root_.scala.collection.immutable.Set(ModifierName.assume("down")).assume[MinLength[1]]),
                  _root_.scala.collection.immutable.Set(),
                  true
                ))
              )
            ).assume[MinLength[1]])
        )
    case Tcgen05St extends Definition(
          Opcode.assume("tcgen05.st"),
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
            Metadata(Vec(
              Metadata.Entry(
                "tcgen05-instructions-tcgen05-st".assume[MinLength[1] & NoNul],
                "9.7.17.8.4. Tensorcore 5th Generation Instructions: tcgen05.st".assume[MinLength[1] & NoNul],
                "tcgen05.st".assume[MinLength[1] & NoNul],
                "Tensorcore 5th Generation Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "tcgen05.st.sync.aligned.shape1.num{.unpack}.b32    [taddr], r;\n\ntcgen05.st.sync.aligned.shape2.num{.unpack}.b32    [taddr], immHalfSplitoff, r;\n\n.shape1 = { .16x64b, .16x128b, .16x256b, .32x32b }\n.shape2 = { .16x32bx2 }\n.num    = { .x1, .x2, .x4, .x8, .x16, .x32, .x64, .x128 }\n.unpack = { .unpack::16b }"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(8, 6),
                Target(100),
                Vec(
                  InstructionForm(
                    Opcode.assume("tcgen05.st"),
                    Vec(OperandConstraint.Address, OperandConstraint.Register),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("sync")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("aligned")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("x1"),
                        ModifierName.assume("x128"),
                        ModifierName.assume("x16"),
                        ModifierName.assume("x2"),
                        ModifierName.assume("x32"),
                        ModifierName.assume("x4"),
                        ModifierName.assume("x64"),
                        ModifierName.assume("x8")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("b32")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("tcgen05.st"),
                    Vec(OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Register),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("sync")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("aligned")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("x1"),
                        ModifierName.assume("x128"),
                        ModifierName.assume("x16"),
                        ModifierName.assume("x2"),
                        ModifierName.assume("x32"),
                        ModifierName.assume("x4"),
                        ModifierName.assume("x64"),
                        ModifierName.assume("x8")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("b32")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  )
                )
              )
            ).assume[MinLength[1]])
        )
    case Tcgen05WaitLd extends Definition(
          Opcode.assume("tcgen05.wait::ld"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 0) Right(Instruction.Tcgen05WaitLd(mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("tcgen05.wait::ld", Vec(0), operands.size))
          },
          () =>
            Metadata(Vec(
              Metadata.Entry(
                "tcgen05-instructions-tcgen05-wait".assume[MinLength[1] & NoNul],
                "9.7.17.8.5. Tensorcore 5th Generation Instructions: tcgen05.wait".assume[MinLength[1] & NoNul],
                "tcgen05.wait".assume[MinLength[1] & NoNul],
                "Tensorcore 5th Generation Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "tcgen05.wait_operation.sync.aligned;\n\n.wait_operation = { .wait::ld, .wait::st }"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(8, 6),
                Target(100),
                Vec(InstructionForm(
                  Opcode.assume("tcgen05.wait::ld"),
                  Vec(),
                  Vec(
                    _root_.scala.collection.immutable.Set(ModifierName.assume("sync")).assume[MinLength[1]],
                    _root_.scala.collection.immutable.Set(ModifierName.assume("aligned")).assume[MinLength[1]]
                  ),
                  _root_.scala.collection.immutable.Set(),
                  false
                ))
              )
            ).assume[MinLength[1]])
        )
    case Tcgen05WaitSt extends Definition(
          Opcode.assume("tcgen05.wait::st"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 0) Right(Instruction.Tcgen05WaitSt(mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("tcgen05.wait::st", Vec(0), operands.size))
          },
          () =>
            Metadata(Vec(
              Metadata.Entry(
                "tcgen05-instructions-tcgen05-wait".assume[MinLength[1] & NoNul],
                "9.7.17.8.5. Tensorcore 5th Generation Instructions: tcgen05.wait".assume[MinLength[1] & NoNul],
                "tcgen05.wait".assume[MinLength[1] & NoNul],
                "Tensorcore 5th Generation Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "tcgen05.wait_operation.sync.aligned;\n\n.wait_operation = { .wait::ld, .wait::st }"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(8, 6),
                Target(100),
                Vec(InstructionForm(
                  Opcode.assume("tcgen05.wait::st"),
                  Vec(),
                  Vec(
                    _root_.scala.collection.immutable.Set(ModifierName.assume("sync")).assume[MinLength[1]],
                    _root_.scala.collection.immutable.Set(ModifierName.assume("aligned")).assume[MinLength[1]]
                  ),
                  _root_.scala.collection.immutable.Set(),
                  false
                ))
              )
            ).assume[MinLength[1]])
        )
    case TensorMapCopyFenceProxy extends Definition(
          Opcode.assume("tensormap.cp_fenceproxy"),
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
            Metadata(Vec(
              Metadata.Entry(
                "parallel-synchronization-and-communication-instructions-tensormap-cp-fenceproxy".assume[MinLength[
                  1
                ] & NoNul],
                "9.7.14.17. Parallel Synchronization and Communication Instructions: tensormap.cp_fenceproxy".assume[
                  MinLength[1] & NoNul
                ],
                "tensormap.cp_fenceproxy".assume[MinLength[1] & NoNul],
                "Parallel Synchronization and Communication Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "tensormap.cp_fenceproxy.cp_qualifiers.fence_qualifiers.sync.aligned  [dst], [src], size;\n\n.cp_qualifiers    = { .global.shared::cta }\n.fence_qualifiers = { .to_proxy::from_proxy.release.scope }\n.to_proxy::from_proxy  = { .tensormap::generic }\n.scope            = { .cta, .cluster, .gpu , .sys }"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(8, 3),
                Target(90),
                Vec(InstructionForm(
                  Opcode.assume("tensormap.cp_fenceproxy"),
                  Vec(OperandConstraint.Address, OperandConstraint.Address, OperandConstraint.Value),
                  Vec(
                    _root_.scala.collection.immutable.Set(ModifierName.assume("sync")).assume[MinLength[1]],
                    _root_.scala.collection.immutable.Set(ModifierName.assume("aligned")).assume[MinLength[1]]
                  ),
                  _root_.scala.collection.immutable.Set(),
                  true
                ))
              )
            ).assume[MinLength[1]])
        )
    case TensorMapReplace extends Definition(
          Opcode.assume("tensormap.replace"),
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
            Metadata(Vec(
              Metadata.Entry(
                "data-movement-and-conversion-instructions-tensormap-replace".assume[MinLength[1] & NoNul],
                "9.7.9.27. Data Movement and Conversion Instructions: tensormap.replace".assume[MinLength[1] & NoNul],
                "tensormap.replace".assume[MinLength[1] & NoNul],
                "Data Movement and Conversion Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "tensormap.replace.mode.field1{.ss}.b1024.type  [addr], new_val;\ntensormap.replace.mode.field2{.ss}.b1024.type  [addr], ord, new_val;\ntensormap.replace.mode.field3{.ss}.b1024.type  [addr], new_val;\n\n.mode    = { .tile }\n.field1  = { .global_address, .rank }\n.field2  = { .box_dim, .global_dim, .global_stride, .element_stride  }\n.field3  = { .elemtype,  .interleave_layout, .swizzle_mode, .swizzle_atomicity, .fill_mode }\n.ss      = { .global, .shared::cta }\n.type    = { .b32, .b64 }"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(8, 3),
                Target(100),
                Vec(
                  InstructionForm(
                    Opcode.assume("tensormap.replace"),
                    Vec(OperandConstraint.Address, OperandConstraint.Value),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("tile")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("global_address"),
                        ModifierName.assume("rank")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("b1024")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("b32"),
                        ModifierName.assume("b64")
                      ).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName.assume("global"),
                      ModifierName.assume("shared::cta")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("tensormap.replace"),
                    Vec(OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("tile")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("box_dim"),
                        ModifierName.assume("element_stride"),
                        ModifierName.assume("global_dim"),
                        ModifierName.assume("global_stride")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("b1024")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("b32"),
                        ModifierName.assume("b64")
                      ).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName.assume("global"),
                      ModifierName.assume("shared::cta")
                    ),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("tensormap.replace"),
                    Vec(OperandConstraint.Address, OperandConstraint.Value),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("tile")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("elemtype"),
                        ModifierName.assume("fill_mode"),
                        ModifierName.assume("interleave_layout"),
                        ModifierName.assume("swizzle_atomicity"),
                        ModifierName.assume("swizzle_mode")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("b1024")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("b32"),
                        ModifierName.assume("b64")
                      ).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName.assume("global"),
                      ModifierName.assume("shared::cta")
                    ),
                    false
                  )
                )
              )
            ).assume[MinLength[1]])
        )
    case TestPredicate extends Definition(
          Opcode.assume("testp"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 2) Right(Instruction.TestPredicate(operands(0), operands(1), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("testp", Vec(2), operands.size))
          },
          () =>
            Metadata(Vec(
              Metadata.Entry(
                "floating-point-instructions-testp".assume[MinLength[1] & NoNul],
                "9.7.3.1. Floating Point Instructions: testp".assume[MinLength[1] & NoNul],
                "testp".assume[MinLength[1] & NoNul],
                "Floating Point Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "testp.op.type  p, a;  // result is .pred\n\n.op   = { .finite, .infinite,\n          .number, .notanumber,\n          .normal, .subnormal };\n.type = { .f32, .f64 };"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(2, 0),
                Target(20),
                Vec(InstructionForm(
                  Opcode.assume("testp"),
                  Vec(OperandConstraint.Register, OperandConstraint.Value),
                  Vec(
                    _root_.scala.collection.immutable.Set(
                      ModifierName.assume("finite"),
                      ModifierName.assume("infinite"),
                      ModifierName.assume("normal"),
                      ModifierName.assume("notanumber"),
                      ModifierName.assume("number"),
                      ModifierName.assume("subnormal")
                    ).assume[MinLength[1]],
                    _root_.scala.collection.immutable.Set(
                      ModifierName.assume("f32"),
                      ModifierName.assume("f64")
                    ).assume[MinLength[1]]
                  ),
                  _root_.scala.collection.immutable.Set(),
                  true
                ))
              )
            ).assume[MinLength[1]])
        )
    case Tex extends Definition(
          Opcode.assume("tex"),
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
            Metadata(Vec(
              Metadata.Entry(
                "texture-instructions-tex".assume[MinLength[1] & NoNul],
                "9.7.11.3. Texture Instructions: tex".assume[MinLength[1] & NoNul],
                "tex".assume[MinLength[1] & NoNul],
                "Texture Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "tex.geom.v4.dtype.ctype  d, [a, c] {, e} {, f};\ntex.geom.v4.dtype.ctype  d[|p], [a, b, c] {, e} {, f};  // explicit sampler\n\ntex.geom.v2.f16x2.ctype  d[|p], [a, c] {, e} {, f};\ntex.geom.v2.f16x2.ctype  d[|p], [a, b, c] {, e} {, f};  // explicit sampler\n\n// mipmaps\ntex.base.geom.v4.dtype.ctype   d[|p], [a, {b,} c] {, e} {, f};\ntex.level.geom.v4.dtype.ctype  d[|p], [a, {b,} c], lod {, e} {, f};\ntex.grad.geom.v4.dtype.ctype   d[|p], [a, {b,} c], dPdx, dPdy {, e} {, f};\n\ntex.base.geom.v2.f16x2.ctype   d[|p], [a, {b,} c] {, e} {, f};\ntex.level.geom.v2.f16x2.ctype  d[|p], [a, {b,} c], lod {, e} {, f};\ntex.grad.geom.v2.f16x2.ctype   d[|p], [a, {b,} c], dPdx, dPdy {, e} {, f};\n\n.geom  = { .1d, .2d, .3d, .a1d, .a2d, .cube, .acube, .2dms, .a2dms };\n.dtype = { .u32, .s32, .f16,  .f32 };\n.ctype = {       .s32, .f32 };          // .cube, .acube require .f32\n                                        // .2dms, .a2dms require .s32"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(1, 0),
                Target(10),
                Vec(
                  InstructionForm(
                    Opcode.assume("tex"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("a1d"),
                        ModifierName.assume("a2d"),
                        ModifierName.assume("a2dms"),
                        ModifierName.assume("acube"),
                        ModifierName.assume("cube")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("v4")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("f16"),
                        ModifierName.assume("f32"),
                        ModifierName.assume("s32"),
                        ModifierName.assume("u32")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("f32"),
                        ModifierName.assume("s32")
                      ).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("tex"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("a1d"),
                        ModifierName.assume("a2d"),
                        ModifierName.assume("a2dms"),
                        ModifierName.assume("acube"),
                        ModifierName.assume("cube")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("v4")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("f16"),
                        ModifierName.assume("f32"),
                        ModifierName.assume("s32"),
                        ModifierName.assume("u32")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("f32"),
                        ModifierName.assume("s32")
                      ).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("tex"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("a1d"),
                        ModifierName.assume("a2d"),
                        ModifierName.assume("a2dms"),
                        ModifierName.assume("acube"),
                        ModifierName.assume("cube")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("v2")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("f16x2")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("f32"),
                        ModifierName.assume("s32")
                      ).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("tex"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("a1d"),
                        ModifierName.assume("a2d"),
                        ModifierName.assume("a2dms"),
                        ModifierName.assume("acube"),
                        ModifierName.assume("cube")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("v2")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("f16x2")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("f32"),
                        ModifierName.assume("s32")
                      ).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("tex"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("base")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("a1d"),
                        ModifierName.assume("a2d"),
                        ModifierName.assume("a2dms"),
                        ModifierName.assume("acube"),
                        ModifierName.assume("cube")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("v4")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("f16"),
                        ModifierName.assume("f32"),
                        ModifierName.assume("s32"),
                        ModifierName.assume("u32")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("f32"),
                        ModifierName.assume("s32")
                      ).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("tex"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("level")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("a1d"),
                        ModifierName.assume("a2d"),
                        ModifierName.assume("a2dms"),
                        ModifierName.assume("acube"),
                        ModifierName.assume("cube")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("v4")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("f16"),
                        ModifierName.assume("f32"),
                        ModifierName.assume("s32"),
                        ModifierName.assume("u32")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("f32"),
                        ModifierName.assume("s32")
                      ).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("tex"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("grad")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("a1d"),
                        ModifierName.assume("a2d"),
                        ModifierName.assume("a2dms"),
                        ModifierName.assume("acube"),
                        ModifierName.assume("cube")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("v4")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("f16"),
                        ModifierName.assume("f32"),
                        ModifierName.assume("s32"),
                        ModifierName.assume("u32")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("f32"),
                        ModifierName.assume("s32")
                      ).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("tex"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("base")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("a1d"),
                        ModifierName.assume("a2d"),
                        ModifierName.assume("a2dms"),
                        ModifierName.assume("acube"),
                        ModifierName.assume("cube")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("v2")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("f16x2")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("f32"),
                        ModifierName.assume("s32")
                      ).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("tex"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("level")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("a1d"),
                        ModifierName.assume("a2d"),
                        ModifierName.assume("a2dms"),
                        ModifierName.assume("acube"),
                        ModifierName.assume("cube")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("v2")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("f16x2")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("f32"),
                        ModifierName.assume("s32")
                      ).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("tex"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("grad")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("a1d"),
                        ModifierName.assume("a2d"),
                        ModifierName.assume("a2dms"),
                        ModifierName.assume("acube"),
                        ModifierName.assume("cube")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("v2")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("f16x2")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("f32"),
                        ModifierName.assume("s32")
                      ).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  )
                )
              )
            ).assume[MinLength[1]])
        )
    case Tld4 extends Definition(
          Opcode.assume("tld4"),
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
            Metadata(Vec(
              Metadata.Entry(
                "texture-instructions-tld4".assume[MinLength[1] & NoNul],
                "9.7.11.4. Texture Instructions: tld4".assume[MinLength[1] & NoNul],
                "tld4".assume[MinLength[1] & NoNul],
                "Texture Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "tld4.comp.2d.v4.dtype.f32    d[|p], [a, c] {, e} {, f};\ntld4.comp.geom.v4.dtype.f32  d[|p], [a, b, c] {, e} {, f};  // explicit sampler\n\n.comp  = { .r, .g, .b, .a };\n.geom  = { .2d, .a2d, .cube, .acube };\n.dtype = { .u32, .s32, .f32 };"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(2, 2),
                Target(20),
                Vec(
                  InstructionForm(
                    Opcode.assume("tld4"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("a"),
                        ModifierName.assume("b"),
                        ModifierName.assume("g"),
                        ModifierName.assume("r")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("v4")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("f32"),
                        ModifierName.assume("s32"),
                        ModifierName.assume("u32")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("f32")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("tld4"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("a"),
                        ModifierName.assume("b"),
                        ModifierName.assume("g"),
                        ModifierName.assume("r")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("a2d"),
                        ModifierName.assume("acube"),
                        ModifierName.assume("cube")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("v4")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("f32"),
                        ModifierName.assume("s32"),
                        ModifierName.assume("u32")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("f32")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  )
                )
              )
            ).assume[MinLength[1]])
        )
    case Trap extends Definition(
          Opcode.assume("trap"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 0) Right(Instruction.Trap(mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("trap", Vec(0), operands.size))
          },
          () =>
            Metadata(Vec(
              Metadata.Entry(
                "miscellaneous-instructions-trap".assume[MinLength[1] & NoNul],
                "9.7.20.4. Miscellaneous Instructions: trap".assume[MinLength[1] & NoNul],
                "trap".assume[MinLength[1] & NoNul],
                "Miscellaneous Instructions".assume[MinLength[1] & NoNul],
                Vec[String]("trap;").map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(1, 0),
                Target(10),
                Vec(InstructionForm(Opcode.assume("trap"), Vec(), Vec(), _root_.scala.collection.immutable.Set(), true))
              )
            ).assume[MinLength[1]])
        )
    case Txq extends Definition(
          Opcode.assume("txq"),
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
            Metadata(Vec(
              Metadata.Entry(
                "texture-instructions-txq".assume[MinLength[1] & NoNul],
                "9.7.11.5. Texture Instructions: txq".assume[MinLength[1] & NoNul],
                "txq".assume[MinLength[1] & NoNul],
                "Texture Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "txq.tquery.b32         d, [a];       // texture attributes\ntxq.level.tlquery.b32  d, [a], lod;  // texture attributes\ntxq.squery.b32         d, [a];       // sampler attributes\n\n.tquery  = { .width, .height, .depth,\n             .channel_data_type, .channel_order,\n             .normalized_coords, .array_size,\n             .num_mipmap_levels, .num_samples};\n\n.tlquery = { .width, .height, .depth };\n\n.squery  = { .force_unnormalized_coords, .filter_mode,\n             .addr_mode_0, addr_mode_1, addr_mode_2 };"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(1, 5),
                Target(10),
                Vec(
                  InstructionForm(
                    Opcode.assume("txq"),
                    Vec(OperandConstraint.Register, OperandConstraint.Address),
                    Vec(
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("array_size"),
                        ModifierName.assume("channel_data_type"),
                        ModifierName.assume("channel_order"),
                        ModifierName.assume("depth"),
                        ModifierName.assume("height"),
                        ModifierName.assume("normalized_coords"),
                        ModifierName.assume("num_mipmap_levels"),
                        ModifierName.assume("num_samples"),
                        ModifierName.assume("width")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("b32")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("txq"),
                    Vec(OperandConstraint.Register, OperandConstraint.Address, OperandConstraint.Value),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("level")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("depth"),
                        ModifierName.assume("height"),
                        ModifierName.assume("width")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("b32")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("txq"),
                    Vec(OperandConstraint.Register, OperandConstraint.Address),
                    Vec(
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("addr_mode_0"),
                        ModifierName.assume("addr_mode_1"),
                        ModifierName.assume("addr_mode_2"),
                        ModifierName.assume("filter_mode"),
                        ModifierName.assume("force_unnormalized_coords")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("b32")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  )
                )
              )
            ).assume[MinLength[1]])
        )
    case Vabsdiff extends Definition(
          Opcode.assume("vabsdiff"),
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
            Metadata(Vec(
              Metadata.Entry(
                "scalar-video-instructions-vadd-vsub-vabsdiff-vmin-vmax".assume[MinLength[1] & NoNul],
                "9.7.19.1.1. Scalar Video Instructions: vadd, vsub, vabsdiff, vmin, vmax".assume[MinLength[1] & NoNul],
                "vadd, vsub, vabsdiff, vmin, vmax".assume[MinLength[1] & NoNul],
                "Scalar Video Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "// 32-bit scalar operation, with optional secondary operation\nvop.dtype.atype.btype{.sat}       d, a{.asel}, b{.bsel};\nvop.dtype.atype.btype{.sat}.op2   d, a{.asel}, b{.bsel}, c;\n\n// 32-bit scalar operation, with optional data merge\nvop.dtype.atype.btype{.sat}  d.dsel, a{.asel}, b{.bsel}, c;\n\n vop   = { vadd, vsub, vabsdiff, vmin, vmax };\n.dtype = .atype = .btype = { .u32, .s32 };\n.dsel  = .asel  = .bsel  = { .b0, .b1, .b2, .b3, .h0, .h1 };\n.op2   = { .add, .min, .max };"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(2, 0),
                Target(20),
                Vec(
                  InstructionForm(
                    Opcode.assume("vabsdiff"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("dtype")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("atype")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("s32"),
                        ModifierName.assume("u32")
                      ).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName.assume("sat")),
                    true
                  ),
                  InstructionForm(
                    Opcode.assume("vabsdiff"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("dtype")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("atype")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("s32"),
                        ModifierName.assume("u32")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("add"),
                        ModifierName.assume("max"),
                        ModifierName.assume("min")
                      ).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName.assume("sat")),
                    true
                  ),
                  InstructionForm(
                    Opcode.assume("vabsdiff"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("dtype")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("atype")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("s32"),
                        ModifierName.assume("u32")
                      ).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName.assume("sat")),
                    true
                  )
                )
              )
            ).assume[MinLength[1]])
        )
    case Vabsdiff2 extends Definition(
          Opcode.assume("vabsdiff2"),
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
            Metadata(Vec(
              Metadata.Entry(
                "simd-video-instructions-vadd2-vsub2-vavrg2-vabsdiff2-vmin2-vmax2".assume[MinLength[1] & NoNul],
                "9.7.19.2.1. SIMD Video Instructions: vadd2, vsub2, vavrg2, vabsdiff2, vmin2, vmax2".assume[MinLength[
                  1
                ] & NoNul],
                "vadd2, vsub2, vavrg2, vabsdiff2, vmin2, vmax2".assume[MinLength[1] & NoNul],
                "SIMD Video Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "// SIMD instruction with secondary SIMD merge operation\nvop2.dtype.atype.btype{.sat}  d{.mask}, a{.asel}, b{.bsel}, c;\n\n// SIMD instruction with secondary accumulate operation\nvop2.dtype.atype.btype.add  d{.mask}, a{.asel}, b{.bsel}, c;\n\n vop2  = { vadd2, vsub2, vavrg2, vabsdiff2, vmin2, vmax2 };\n.dtype = .atype = .btype = { .u32, .s32 };\n.mask  = { .h0, .h1, .h10 };  // defaults to .h10\n.asel  = .bsel  = { .hxy, where x,y are from { 0, 1, 2, 3 } };\n   .asel defaults to .h10\n   .bsel defaults to .h32"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(3, 0),
                Target(30),
                Vec(
                  InstructionForm(
                    Opcode.assume("vabsdiff2"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("dtype")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("atype")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("s32"),
                        ModifierName.assume("u32")
                      ).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName.assume("sat")),
                    true
                  ),
                  InstructionForm(
                    Opcode.assume("vabsdiff2"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("dtype")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("atype")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("s32"),
                        ModifierName.assume("u32")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("add")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(),
                    true
                  )
                )
              )
            ).assume[MinLength[1]])
        )
    case Vabsdiff4 extends Definition(
          Opcode.assume("vabsdiff4"),
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
            Metadata(Vec(
              Metadata.Entry(
                "simd-video-instructions-vadd4-vsub4-vavrg4-vabsdiff4-vmin4-vmax4".assume[MinLength[1] & NoNul],
                "9.7.19.2.3. SIMD Video Instructions: vadd4, vsub4, vavrg4, vabsdiff4, vmin4, vmax4".assume[MinLength[
                  1
                ] & NoNul],
                "vadd4, vsub4, vavrg4, vabsdiff4, vmin4, vmax4".assume[MinLength[1] & NoNul],
                "SIMD Video Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "// SIMD instruction with secondary SIMD merge operation\nvop4.dtype.atype.btype{.sat}  d{.mask}, a{.asel}, b{.bsel}, c;\n\n// SIMD instruction with secondary accumulate operation\nvop4.dtype.atype.btype.add  d{.mask}, a{.asel}, b{.bsel}, c;\nvop4  = { vadd4, vsub4, vavrg4, vabsdiff4, vmin4, vmax4 };\n\n.dtype = .atype = .btype = { .u32, .s32 };\n.mask  = { .b0,\n           .b1, .b10\n           .b2, .b20, .b21, .b210,\n           .b3, .b30, .b31, .b310, .b32, .b320, .b321, .b3210 };\n    defaults to .b3210\n.asel = .bsel = .bxyzw, where x,y,z,w are from { 0, ..., 7 };\n   .asel defaults to .b3210\n   .bsel defaults to .b7654"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(3, 0),
                Target(30),
                Vec(
                  InstructionForm(
                    Opcode.assume("vabsdiff4"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("dtype")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("atype")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("s32"),
                        ModifierName.assume("u32")
                      ).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName.assume("sat")),
                    true
                  ),
                  InstructionForm(
                    Opcode.assume("vabsdiff4"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("dtype")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("atype")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("s32"),
                        ModifierName.assume("u32")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("add")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(),
                    true
                  )
                )
              )
            ).assume[MinLength[1]])
        )
    case Vadd extends Definition(
          Opcode.assume("vadd"),
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
            Metadata(Vec(
              Metadata.Entry(
                "scalar-video-instructions-vadd-vsub-vabsdiff-vmin-vmax".assume[MinLength[1] & NoNul],
                "9.7.19.1.1. Scalar Video Instructions: vadd, vsub, vabsdiff, vmin, vmax".assume[MinLength[1] & NoNul],
                "vadd, vsub, vabsdiff, vmin, vmax".assume[MinLength[1] & NoNul],
                "Scalar Video Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "// 32-bit scalar operation, with optional secondary operation\nvop.dtype.atype.btype{.sat}       d, a{.asel}, b{.bsel};\nvop.dtype.atype.btype{.sat}.op2   d, a{.asel}, b{.bsel}, c;\n\n// 32-bit scalar operation, with optional data merge\nvop.dtype.atype.btype{.sat}  d.dsel, a{.asel}, b{.bsel}, c;\n\n vop   = { vadd, vsub, vabsdiff, vmin, vmax };\n.dtype = .atype = .btype = { .u32, .s32 };\n.dsel  = .asel  = .bsel  = { .b0, .b1, .b2, .b3, .h0, .h1 };\n.op2   = { .add, .min, .max };"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(2, 0),
                Target(20),
                Vec(
                  InstructionForm(
                    Opcode.assume("vadd"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("dtype")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("atype")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("s32"),
                        ModifierName.assume("u32")
                      ).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName.assume("sat")),
                    true
                  ),
                  InstructionForm(
                    Opcode.assume("vadd"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("dtype")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("atype")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("s32"),
                        ModifierName.assume("u32")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("add"),
                        ModifierName.assume("max"),
                        ModifierName.assume("min")
                      ).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName.assume("sat")),
                    true
                  ),
                  InstructionForm(
                    Opcode.assume("vadd"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("dtype")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("atype")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("s32"),
                        ModifierName.assume("u32")
                      ).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName.assume("sat")),
                    true
                  )
                )
              )
            ).assume[MinLength[1]])
        )
    case Vadd2 extends Definition(
          Opcode.assume("vadd2"),
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
            Metadata(Vec(
              Metadata.Entry(
                "simd-video-instructions-vadd2-vsub2-vavrg2-vabsdiff2-vmin2-vmax2".assume[MinLength[1] & NoNul],
                "9.7.19.2.1. SIMD Video Instructions: vadd2, vsub2, vavrg2, vabsdiff2, vmin2, vmax2".assume[MinLength[
                  1
                ] & NoNul],
                "vadd2, vsub2, vavrg2, vabsdiff2, vmin2, vmax2".assume[MinLength[1] & NoNul],
                "SIMD Video Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "// SIMD instruction with secondary SIMD merge operation\nvop2.dtype.atype.btype{.sat}  d{.mask}, a{.asel}, b{.bsel}, c;\n\n// SIMD instruction with secondary accumulate operation\nvop2.dtype.atype.btype.add  d{.mask}, a{.asel}, b{.bsel}, c;\n\n vop2  = { vadd2, vsub2, vavrg2, vabsdiff2, vmin2, vmax2 };\n.dtype = .atype = .btype = { .u32, .s32 };\n.mask  = { .h0, .h1, .h10 };  // defaults to .h10\n.asel  = .bsel  = { .hxy, where x,y are from { 0, 1, 2, 3 } };\n   .asel defaults to .h10\n   .bsel defaults to .h32"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(3, 0),
                Target(30),
                Vec(
                  InstructionForm(
                    Opcode.assume("vadd2"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("dtype")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("atype")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("s32"),
                        ModifierName.assume("u32")
                      ).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName.assume("sat")),
                    true
                  ),
                  InstructionForm(
                    Opcode.assume("vadd2"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("dtype")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("atype")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("s32"),
                        ModifierName.assume("u32")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("add")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(),
                    true
                  )
                )
              )
            ).assume[MinLength[1]])
        )
    case Vadd4 extends Definition(
          Opcode.assume("vadd4"),
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
            Metadata(Vec(
              Metadata.Entry(
                "simd-video-instructions-vadd4-vsub4-vavrg4-vabsdiff4-vmin4-vmax4".assume[MinLength[1] & NoNul],
                "9.7.19.2.3. SIMD Video Instructions: vadd4, vsub4, vavrg4, vabsdiff4, vmin4, vmax4".assume[MinLength[
                  1
                ] & NoNul],
                "vadd4, vsub4, vavrg4, vabsdiff4, vmin4, vmax4".assume[MinLength[1] & NoNul],
                "SIMD Video Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "// SIMD instruction with secondary SIMD merge operation\nvop4.dtype.atype.btype{.sat}  d{.mask}, a{.asel}, b{.bsel}, c;\n\n// SIMD instruction with secondary accumulate operation\nvop4.dtype.atype.btype.add  d{.mask}, a{.asel}, b{.bsel}, c;\nvop4  = { vadd4, vsub4, vavrg4, vabsdiff4, vmin4, vmax4 };\n\n.dtype = .atype = .btype = { .u32, .s32 };\n.mask  = { .b0,\n           .b1, .b10\n           .b2, .b20, .b21, .b210,\n           .b3, .b30, .b31, .b310, .b32, .b320, .b321, .b3210 };\n    defaults to .b3210\n.asel = .bsel = .bxyzw, where x,y,z,w are from { 0, ..., 7 };\n   .asel defaults to .b3210\n   .bsel defaults to .b7654"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(3, 0),
                Target(30),
                Vec(
                  InstructionForm(
                    Opcode.assume("vadd4"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("dtype")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("atype")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("s32"),
                        ModifierName.assume("u32")
                      ).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName.assume("sat")),
                    true
                  ),
                  InstructionForm(
                    Opcode.assume("vadd4"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("dtype")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("atype")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("s32"),
                        ModifierName.assume("u32")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("add")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(),
                    true
                  )
                )
              )
            ).assume[MinLength[1]])
        )
    case Vavrg2 extends Definition(
          Opcode.assume("vavrg2"),
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
            Metadata(Vec(
              Metadata.Entry(
                "simd-video-instructions-vadd2-vsub2-vavrg2-vabsdiff2-vmin2-vmax2".assume[MinLength[1] & NoNul],
                "9.7.19.2.1. SIMD Video Instructions: vadd2, vsub2, vavrg2, vabsdiff2, vmin2, vmax2".assume[MinLength[
                  1
                ] & NoNul],
                "vadd2, vsub2, vavrg2, vabsdiff2, vmin2, vmax2".assume[MinLength[1] & NoNul],
                "SIMD Video Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "// SIMD instruction with secondary SIMD merge operation\nvop2.dtype.atype.btype{.sat}  d{.mask}, a{.asel}, b{.bsel}, c;\n\n// SIMD instruction with secondary accumulate operation\nvop2.dtype.atype.btype.add  d{.mask}, a{.asel}, b{.bsel}, c;\n\n vop2  = { vadd2, vsub2, vavrg2, vabsdiff2, vmin2, vmax2 };\n.dtype = .atype = .btype = { .u32, .s32 };\n.mask  = { .h0, .h1, .h10 };  // defaults to .h10\n.asel  = .bsel  = { .hxy, where x,y are from { 0, 1, 2, 3 } };\n   .asel defaults to .h10\n   .bsel defaults to .h32"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(3, 0),
                Target(30),
                Vec(
                  InstructionForm(
                    Opcode.assume("vavrg2"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("dtype")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("atype")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("s32"),
                        ModifierName.assume("u32")
                      ).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName.assume("sat")),
                    true
                  ),
                  InstructionForm(
                    Opcode.assume("vavrg2"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("dtype")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("atype")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("s32"),
                        ModifierName.assume("u32")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("add")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(),
                    true
                  )
                )
              )
            ).assume[MinLength[1]])
        )
    case Vavrg4 extends Definition(
          Opcode.assume("vavrg4"),
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
            Metadata(Vec(
              Metadata.Entry(
                "simd-video-instructions-vadd4-vsub4-vavrg4-vabsdiff4-vmin4-vmax4".assume[MinLength[1] & NoNul],
                "9.7.19.2.3. SIMD Video Instructions: vadd4, vsub4, vavrg4, vabsdiff4, vmin4, vmax4".assume[MinLength[
                  1
                ] & NoNul],
                "vadd4, vsub4, vavrg4, vabsdiff4, vmin4, vmax4".assume[MinLength[1] & NoNul],
                "SIMD Video Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "// SIMD instruction with secondary SIMD merge operation\nvop4.dtype.atype.btype{.sat}  d{.mask}, a{.asel}, b{.bsel}, c;\n\n// SIMD instruction with secondary accumulate operation\nvop4.dtype.atype.btype.add  d{.mask}, a{.asel}, b{.bsel}, c;\nvop4  = { vadd4, vsub4, vavrg4, vabsdiff4, vmin4, vmax4 };\n\n.dtype = .atype = .btype = { .u32, .s32 };\n.mask  = { .b0,\n           .b1, .b10\n           .b2, .b20, .b21, .b210,\n           .b3, .b30, .b31, .b310, .b32, .b320, .b321, .b3210 };\n    defaults to .b3210\n.asel = .bsel = .bxyzw, where x,y,z,w are from { 0, ..., 7 };\n   .asel defaults to .b3210\n   .bsel defaults to .b7654"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(3, 0),
                Target(30),
                Vec(
                  InstructionForm(
                    Opcode.assume("vavrg4"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("dtype")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("atype")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("s32"),
                        ModifierName.assume("u32")
                      ).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName.assume("sat")),
                    true
                  ),
                  InstructionForm(
                    Opcode.assume("vavrg4"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("dtype")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("atype")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("s32"),
                        ModifierName.assume("u32")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("add")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(),
                    true
                  )
                )
              )
            ).assume[MinLength[1]])
        )
    case Vmad extends Definition(
          Opcode.assume("vmad"),
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
            Metadata(Vec(
              Metadata.Entry(
                "scalar-video-instructions-vmad".assume[MinLength[1] & NoNul],
                "9.7.19.1.3. Scalar Video Instructions: vmad".assume[MinLength[1] & NoNul],
                "vmad".assume[MinLength[1] & NoNul],
                "Scalar Video Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "// 32-bit scalar operation\nvmad.dtype.atype.btype{.sat}{.scale}     d, {-}a{.asel}, {-}b{.bsel},\n                                         {-}c;\nvmad.dtype.atype.btype.po{.sat}{.scale}  d, a{.asel}, b{.bsel}, c;\n\n.dtype = .atype = .btype = { .u32, .s32 };\n.asel  = .bsel  = { .b0, .b1, .b2, .b3, .h0, .h1 };\n.scale = { .shr7, .shr15 };"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(2, 0),
                Target(20),
                Vec(
                  InstructionForm(
                    Opcode.assume("vmad"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("dtype")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("atype")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("s32"),
                        ModifierName.assume("u32")
                      ).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName.assume("sat"),
                      ModifierName.assume("shr15"),
                      ModifierName.assume("shr7")
                    ),
                    true
                  ),
                  InstructionForm(
                    Opcode.assume("vmad"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("dtype")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("atype")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("s32"),
                        ModifierName.assume("u32")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("po")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(
                      ModifierName.assume("sat"),
                      ModifierName.assume("shr15"),
                      ModifierName.assume("shr7")
                    ),
                    true
                  )
                )
              )
            ).assume[MinLength[1]])
        )
    case Vmax extends Definition(
          Opcode.assume("vmax"),
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
            Metadata(Vec(
              Metadata.Entry(
                "scalar-video-instructions-vadd-vsub-vabsdiff-vmin-vmax".assume[MinLength[1] & NoNul],
                "9.7.19.1.1. Scalar Video Instructions: vadd, vsub, vabsdiff, vmin, vmax".assume[MinLength[1] & NoNul],
                "vadd, vsub, vabsdiff, vmin, vmax".assume[MinLength[1] & NoNul],
                "Scalar Video Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "// 32-bit scalar operation, with optional secondary operation\nvop.dtype.atype.btype{.sat}       d, a{.asel}, b{.bsel};\nvop.dtype.atype.btype{.sat}.op2   d, a{.asel}, b{.bsel}, c;\n\n// 32-bit scalar operation, with optional data merge\nvop.dtype.atype.btype{.sat}  d.dsel, a{.asel}, b{.bsel}, c;\n\n vop   = { vadd, vsub, vabsdiff, vmin, vmax };\n.dtype = .atype = .btype = { .u32, .s32 };\n.dsel  = .asel  = .bsel  = { .b0, .b1, .b2, .b3, .h0, .h1 };\n.op2   = { .add, .min, .max };"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(2, 0),
                Target(20),
                Vec(
                  InstructionForm(
                    Opcode.assume("vmax"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("dtype")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("atype")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("s32"),
                        ModifierName.assume("u32")
                      ).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName.assume("sat")),
                    true
                  ),
                  InstructionForm(
                    Opcode.assume("vmax"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("dtype")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("atype")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("s32"),
                        ModifierName.assume("u32")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("add"),
                        ModifierName.assume("max"),
                        ModifierName.assume("min")
                      ).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName.assume("sat")),
                    true
                  ),
                  InstructionForm(
                    Opcode.assume("vmax"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("dtype")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("atype")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("s32"),
                        ModifierName.assume("u32")
                      ).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName.assume("sat")),
                    true
                  )
                )
              )
            ).assume[MinLength[1]])
        )
    case Vmax2 extends Definition(
          Opcode.assume("vmax2"),
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
            Metadata(Vec(
              Metadata.Entry(
                "simd-video-instructions-vadd2-vsub2-vavrg2-vabsdiff2-vmin2-vmax2".assume[MinLength[1] & NoNul],
                "9.7.19.2.1. SIMD Video Instructions: vadd2, vsub2, vavrg2, vabsdiff2, vmin2, vmax2".assume[MinLength[
                  1
                ] & NoNul],
                "vadd2, vsub2, vavrg2, vabsdiff2, vmin2, vmax2".assume[MinLength[1] & NoNul],
                "SIMD Video Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "// SIMD instruction with secondary SIMD merge operation\nvop2.dtype.atype.btype{.sat}  d{.mask}, a{.asel}, b{.bsel}, c;\n\n// SIMD instruction with secondary accumulate operation\nvop2.dtype.atype.btype.add  d{.mask}, a{.asel}, b{.bsel}, c;\n\n vop2  = { vadd2, vsub2, vavrg2, vabsdiff2, vmin2, vmax2 };\n.dtype = .atype = .btype = { .u32, .s32 };\n.mask  = { .h0, .h1, .h10 };  // defaults to .h10\n.asel  = .bsel  = { .hxy, where x,y are from { 0, 1, 2, 3 } };\n   .asel defaults to .h10\n   .bsel defaults to .h32"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(3, 0),
                Target(30),
                Vec(
                  InstructionForm(
                    Opcode.assume("vmax2"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("dtype")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("atype")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("s32"),
                        ModifierName.assume("u32")
                      ).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName.assume("sat")),
                    true
                  ),
                  InstructionForm(
                    Opcode.assume("vmax2"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("dtype")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("atype")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("s32"),
                        ModifierName.assume("u32")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("add")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(),
                    true
                  )
                )
              )
            ).assume[MinLength[1]])
        )
    case Vmax4 extends Definition(
          Opcode.assume("vmax4"),
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
            Metadata(Vec(
              Metadata.Entry(
                "simd-video-instructions-vadd4-vsub4-vavrg4-vabsdiff4-vmin4-vmax4".assume[MinLength[1] & NoNul],
                "9.7.19.2.3. SIMD Video Instructions: vadd4, vsub4, vavrg4, vabsdiff4, vmin4, vmax4".assume[MinLength[
                  1
                ] & NoNul],
                "vadd4, vsub4, vavrg4, vabsdiff4, vmin4, vmax4".assume[MinLength[1] & NoNul],
                "SIMD Video Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "// SIMD instruction with secondary SIMD merge operation\nvop4.dtype.atype.btype{.sat}  d{.mask}, a{.asel}, b{.bsel}, c;\n\n// SIMD instruction with secondary accumulate operation\nvop4.dtype.atype.btype.add  d{.mask}, a{.asel}, b{.bsel}, c;\nvop4  = { vadd4, vsub4, vavrg4, vabsdiff4, vmin4, vmax4 };\n\n.dtype = .atype = .btype = { .u32, .s32 };\n.mask  = { .b0,\n           .b1, .b10\n           .b2, .b20, .b21, .b210,\n           .b3, .b30, .b31, .b310, .b32, .b320, .b321, .b3210 };\n    defaults to .b3210\n.asel = .bsel = .bxyzw, where x,y,z,w are from { 0, ..., 7 };\n   .asel defaults to .b3210\n   .bsel defaults to .b7654"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(3, 0),
                Target(30),
                Vec(
                  InstructionForm(
                    Opcode.assume("vmax4"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("dtype")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("atype")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("s32"),
                        ModifierName.assume("u32")
                      ).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName.assume("sat")),
                    true
                  ),
                  InstructionForm(
                    Opcode.assume("vmax4"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("dtype")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("atype")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("s32"),
                        ModifierName.assume("u32")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("add")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(),
                    true
                  )
                )
              )
            ).assume[MinLength[1]])
        )
    case Vmin extends Definition(
          Opcode.assume("vmin"),
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
            Metadata(Vec(
              Metadata.Entry(
                "scalar-video-instructions-vadd-vsub-vabsdiff-vmin-vmax".assume[MinLength[1] & NoNul],
                "9.7.19.1.1. Scalar Video Instructions: vadd, vsub, vabsdiff, vmin, vmax".assume[MinLength[1] & NoNul],
                "vadd, vsub, vabsdiff, vmin, vmax".assume[MinLength[1] & NoNul],
                "Scalar Video Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "// 32-bit scalar operation, with optional secondary operation\nvop.dtype.atype.btype{.sat}       d, a{.asel}, b{.bsel};\nvop.dtype.atype.btype{.sat}.op2   d, a{.asel}, b{.bsel}, c;\n\n// 32-bit scalar operation, with optional data merge\nvop.dtype.atype.btype{.sat}  d.dsel, a{.asel}, b{.bsel}, c;\n\n vop   = { vadd, vsub, vabsdiff, vmin, vmax };\n.dtype = .atype = .btype = { .u32, .s32 };\n.dsel  = .asel  = .bsel  = { .b0, .b1, .b2, .b3, .h0, .h1 };\n.op2   = { .add, .min, .max };"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(2, 0),
                Target(20),
                Vec(
                  InstructionForm(
                    Opcode.assume("vmin"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("dtype")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("atype")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("s32"),
                        ModifierName.assume("u32")
                      ).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName.assume("sat")),
                    true
                  ),
                  InstructionForm(
                    Opcode.assume("vmin"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("dtype")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("atype")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("s32"),
                        ModifierName.assume("u32")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("add"),
                        ModifierName.assume("max"),
                        ModifierName.assume("min")
                      ).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName.assume("sat")),
                    true
                  ),
                  InstructionForm(
                    Opcode.assume("vmin"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("dtype")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("atype")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("s32"),
                        ModifierName.assume("u32")
                      ).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName.assume("sat")),
                    true
                  )
                )
              )
            ).assume[MinLength[1]])
        )
    case Vmin2 extends Definition(
          Opcode.assume("vmin2"),
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
            Metadata(Vec(
              Metadata.Entry(
                "simd-video-instructions-vadd2-vsub2-vavrg2-vabsdiff2-vmin2-vmax2".assume[MinLength[1] & NoNul],
                "9.7.19.2.1. SIMD Video Instructions: vadd2, vsub2, vavrg2, vabsdiff2, vmin2, vmax2".assume[MinLength[
                  1
                ] & NoNul],
                "vadd2, vsub2, vavrg2, vabsdiff2, vmin2, vmax2".assume[MinLength[1] & NoNul],
                "SIMD Video Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "// SIMD instruction with secondary SIMD merge operation\nvop2.dtype.atype.btype{.sat}  d{.mask}, a{.asel}, b{.bsel}, c;\n\n// SIMD instruction with secondary accumulate operation\nvop2.dtype.atype.btype.add  d{.mask}, a{.asel}, b{.bsel}, c;\n\n vop2  = { vadd2, vsub2, vavrg2, vabsdiff2, vmin2, vmax2 };\n.dtype = .atype = .btype = { .u32, .s32 };\n.mask  = { .h0, .h1, .h10 };  // defaults to .h10\n.asel  = .bsel  = { .hxy, where x,y are from { 0, 1, 2, 3 } };\n   .asel defaults to .h10\n   .bsel defaults to .h32"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(3, 0),
                Target(30),
                Vec(
                  InstructionForm(
                    Opcode.assume("vmin2"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("dtype")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("atype")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("s32"),
                        ModifierName.assume("u32")
                      ).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName.assume("sat")),
                    true
                  ),
                  InstructionForm(
                    Opcode.assume("vmin2"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("dtype")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("atype")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("s32"),
                        ModifierName.assume("u32")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("add")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(),
                    true
                  )
                )
              )
            ).assume[MinLength[1]])
        )
    case Vmin4 extends Definition(
          Opcode.assume("vmin4"),
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
            Metadata(Vec(
              Metadata.Entry(
                "simd-video-instructions-vadd4-vsub4-vavrg4-vabsdiff4-vmin4-vmax4".assume[MinLength[1] & NoNul],
                "9.7.19.2.3. SIMD Video Instructions: vadd4, vsub4, vavrg4, vabsdiff4, vmin4, vmax4".assume[MinLength[
                  1
                ] & NoNul],
                "vadd4, vsub4, vavrg4, vabsdiff4, vmin4, vmax4".assume[MinLength[1] & NoNul],
                "SIMD Video Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "// SIMD instruction with secondary SIMD merge operation\nvop4.dtype.atype.btype{.sat}  d{.mask}, a{.asel}, b{.bsel}, c;\n\n// SIMD instruction with secondary accumulate operation\nvop4.dtype.atype.btype.add  d{.mask}, a{.asel}, b{.bsel}, c;\nvop4  = { vadd4, vsub4, vavrg4, vabsdiff4, vmin4, vmax4 };\n\n.dtype = .atype = .btype = { .u32, .s32 };\n.mask  = { .b0,\n           .b1, .b10\n           .b2, .b20, .b21, .b210,\n           .b3, .b30, .b31, .b310, .b32, .b320, .b321, .b3210 };\n    defaults to .b3210\n.asel = .bsel = .bxyzw, where x,y,z,w are from { 0, ..., 7 };\n   .asel defaults to .b3210\n   .bsel defaults to .b7654"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(3, 0),
                Target(30),
                Vec(
                  InstructionForm(
                    Opcode.assume("vmin4"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("dtype")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("atype")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("s32"),
                        ModifierName.assume("u32")
                      ).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName.assume("sat")),
                    true
                  ),
                  InstructionForm(
                    Opcode.assume("vmin4"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("dtype")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("atype")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("s32"),
                        ModifierName.assume("u32")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("add")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(),
                    true
                  )
                )
              )
            ).assume[MinLength[1]])
        )
    case Vote extends Definition(
          Opcode.assume("vote"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 2) Right(Instruction.Vote(operands(0), operands(1), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("vote", Vec(2), operands.size))
          },
          () =>
            Metadata(Vec(
              Metadata.Entry(
                "parallel-synchronization-and-communication-instructions-vote".assume[MinLength[1] & NoNul],
                "9.7.14.9. Parallel Synchronization and Communication Instructions: vote (deprecated)".assume[MinLength[
                  1
                ] & NoNul],
                "vote (deprecated)".assume[MinLength[1] & NoNul],
                "Parallel Synchronization and Communication Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "vote.mode.pred  d, {!}a;\nvote.ballot.b32 d, {!}a;  // 'ballot' form, returns bitmask\n\n.mode = { .all, .any, .uni };"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(1, 2),
                Target(12),
                Vec(
                  InstructionForm(
                    Opcode.assume("vote"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("all"),
                        ModifierName.assume("any"),
                        ModifierName.assume("uni")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("pred")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Opcode.assume("vote"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("ballot")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("b32")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  )
                )
              )
            ).assume[MinLength[1]])
        )
    case VoteSync extends Definition(
          Opcode.assume("vote.sync"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 3) Right(Instruction.VoteSync(operands(0), operands(1), operands(2), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("vote.sync", Vec(3), operands.size))
          },
          () =>
            Metadata(Vec(
              Metadata.Entry(
                "parallel-synchronization-and-communication-instructions-vote-sync".assume[MinLength[1] & NoNul],
                "9.7.14.10. Parallel Synchronization and Communication Instructions: vote.sync".assume[MinLength[
                  1
                ] & NoNul],
                "vote.sync".assume[MinLength[1] & NoNul],
                "Parallel Synchronization and Communication Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "vote.sync.mode.pred  d, {!}a, membermask;\nvote.sync.ballot.b32 d, {!}a, membermask;  // 'ballot' form, returns bitmask\n\n.mode = { .all, .any, .uni };"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(6, 0),
                Target(30),
                Vec(
                  InstructionForm(
                    Opcode.assume("vote.sync"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("all"),
                        ModifierName.assume("any"),
                        ModifierName.assume("uni")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("pred")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(),
                    true
                  ),
                  InstructionForm(
                    Opcode.assume("vote.sync"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("ballot")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("b32")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(),
                    true
                  )
                )
              )
            ).assume[MinLength[1]])
        )
    case Vset extends Definition(
          Opcode.assume("vset"),
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
            Metadata(Vec(
              Metadata.Entry(
                "scalar-video-instructions-vset".assume[MinLength[1] & NoNul],
                "9.7.19.1.4. Scalar Video Instructions: vset".assume[MinLength[1] & NoNul],
                "vset".assume[MinLength[1] & NoNul],
                "Scalar Video Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "// 32-bit scalar operation, with optional secondary operation\nvset.atype.btype.cmp       d, a{.asel}, b{.bsel};\nvset.atype.btype.cmp.op2   d, a{.asel}, b{.bsel}, c;\n\n// 32-bit scalar operation, with optional data merge\nvset.atype.btype.cmp  d.dsel, a{.asel}, b{.bsel}, c;\n\n.atype = .btype = { .u32, .s32 };\n.cmp   = { .eq, .ne, .lt, .le, .gt, .ge };\n.dsel  = .asel  = .bsel  = { .b0, .b1, .b2, .b3, .h0, .h1 };\n.op2   = { .add, .min, .max };"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(2, 0),
                Target(20),
                Vec(
                  InstructionForm(
                    Opcode.assume("vset"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("atype")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("s32"),
                        ModifierName.assume("u32")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("eq"),
                        ModifierName.assume("ge"),
                        ModifierName.assume("gt"),
                        ModifierName.assume("le"),
                        ModifierName.assume("lt"),
                        ModifierName.assume("ne")
                      ).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(),
                    true
                  ),
                  InstructionForm(
                    Opcode.assume("vset"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("atype")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("s32"),
                        ModifierName.assume("u32")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("eq"),
                        ModifierName.assume("ge"),
                        ModifierName.assume("gt"),
                        ModifierName.assume("le"),
                        ModifierName.assume("lt"),
                        ModifierName.assume("ne")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("add"),
                        ModifierName.assume("max"),
                        ModifierName.assume("min")
                      ).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(),
                    true
                  ),
                  InstructionForm(
                    Opcode.assume("vset"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("atype")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("s32"),
                        ModifierName.assume("u32")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("eq"),
                        ModifierName.assume("ge"),
                        ModifierName.assume("gt"),
                        ModifierName.assume("le"),
                        ModifierName.assume("lt"),
                        ModifierName.assume("ne")
                      ).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(),
                    true
                  )
                )
              )
            ).assume[MinLength[1]])
        )
    case Vset2 extends Definition(
          Opcode.assume("vset2"),
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
            Metadata(Vec(
              Metadata.Entry(
                "simd-video-instructions-vset2".assume[MinLength[1] & NoNul],
                "9.7.19.2.2. SIMD Video Instructions: vset2".assume[MinLength[1] & NoNul],
                "vset2".assume[MinLength[1] & NoNul],
                "SIMD Video Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "// SIMD instruction with secondary SIMD merge operation\nvset2.atype.btype.cmp  d{.mask}, a{.asel}, b{.bsel}, c;\n\n// SIMD instruction with secondary accumulate operation\nvset2.atype.btype.cmp.add  d{.mask}, a{.asel}, b{.bsel}, c;\n\n.atype = .btype = { .u32, .s32 };\n.cmp   = { .eq, .ne, .lt, .le, .gt, .ge };\n.mask  = { .h0, .h1, .h10 };  // defaults to .h10\n.asel  = .bsel  = { .hxy, where x,y are from { 0, 1, 2, 3 } };\n   .asel defaults to .h10\n   .bsel defaults to .h32"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(3, 0),
                Target(30),
                Vec(
                  InstructionForm(
                    Opcode.assume("vset2"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("atype")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("s32"),
                        ModifierName.assume("u32")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("eq"),
                        ModifierName.assume("ge"),
                        ModifierName.assume("gt"),
                        ModifierName.assume("le"),
                        ModifierName.assume("lt"),
                        ModifierName.assume("ne")
                      ).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(),
                    true
                  ),
                  InstructionForm(
                    Opcode.assume("vset2"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("atype")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("s32"),
                        ModifierName.assume("u32")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("eq"),
                        ModifierName.assume("ge"),
                        ModifierName.assume("gt"),
                        ModifierName.assume("le"),
                        ModifierName.assume("lt"),
                        ModifierName.assume("ne")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("add")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(),
                    true
                  )
                )
              )
            ).assume[MinLength[1]])
        )
    case Vset4 extends Definition(
          Opcode.assume("vset4"),
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
            Metadata(Vec(
              Metadata.Entry(
                "simd-video-instructions-vset4".assume[MinLength[1] & NoNul],
                "9.7.19.2.4. SIMD Video Instructions: vset4".assume[MinLength[1] & NoNul],
                "vset4".assume[MinLength[1] & NoNul],
                "SIMD Video Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "// SIMD instruction with secondary SIMD merge operation\nvset4.atype.btype.cmp  d{.mask}, a{.asel}, b{.bsel}, c;\n\n// SIMD instruction with secondary accumulate operation\nvset4.atype.btype.cmp.add  d{.mask}, a{.asel}, b{.bsel}, c;\n\n.atype = .btype = { .u32, .s32 };\n.cmp   = { .eq, .ne, .lt, .le, .gt, .ge };\n.mask  = { .b0,\n           .b1, .b10\n           .b2, .b20, .b21, .b210,\n           .b3, .b30, .b31, .b310, .b32, .b320, .b321, .b3210 };\n    defaults to .b3210\n.asel = .bsel = .bxyzw, where x,y,z,w are from { 0, ..., 7 };\n   .asel defaults to .b3210\n   .bsel defaults to .b7654"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(3, 0),
                Target(30),
                Vec(
                  InstructionForm(
                    Opcode.assume("vset4"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("atype")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("s32"),
                        ModifierName.assume("u32")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("eq"),
                        ModifierName.assume("ge"),
                        ModifierName.assume("gt"),
                        ModifierName.assume("le"),
                        ModifierName.assume("lt"),
                        ModifierName.assume("ne")
                      ).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(),
                    true
                  ),
                  InstructionForm(
                    Opcode.assume("vset4"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("atype")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("s32"),
                        ModifierName.assume("u32")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("eq"),
                        ModifierName.assume("ge"),
                        ModifierName.assume("gt"),
                        ModifierName.assume("le"),
                        ModifierName.assume("lt"),
                        ModifierName.assume("ne")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("add")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(),
                    true
                  )
                )
              )
            ).assume[MinLength[1]])
        )
    case Vshl extends Definition(
          Opcode.assume("vshl"),
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
            Metadata(Vec(
              Metadata.Entry(
                "scalar-video-instructions-vshl-vshr".assume[MinLength[1] & NoNul],
                "9.7.19.1.2. Scalar Video Instructions: vshl, vshr".assume[MinLength[1] & NoNul],
                "vshl, vshr".assume[MinLength[1] & NoNul],
                "Scalar Video Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "// 32-bit scalar operation, with optional secondary operation\nvop.dtype.atype.u32{.sat}.mode       d, a{.asel}, b{.bsel};\nvop.dtype.atype.u32{.sat}.mode.op2   d, a{.asel}, b{.bsel}, c;\n\n// 32-bit scalar operation, with optional data merge\nvop.dtype.atype.u32{.sat}.mode  d.dsel, a{.asel}, b{.bsel}, c;\n\n vop   = { vshl, vshr };\n.dtype = .atype = { .u32, .s32 };\n.mode  = { .clamp, .wrap };\n.dsel  = .asel  = .bsel  = { .b0, .b1, .b2, .b3, .h0, .h1 };\n.op2   = { .add, .min, .max };"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(2, 0),
                Target(20),
                Vec(
                  InstructionForm(
                    Opcode.assume("vshl"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("dtype")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("s32"),
                        ModifierName.assume("u32")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("u32")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("clamp"),
                        ModifierName.assume("wrap")
                      ).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName.assume("sat")),
                    true
                  ),
                  InstructionForm(
                    Opcode.assume("vshl"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("dtype")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("s32"),
                        ModifierName.assume("u32")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("u32")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("clamp"),
                        ModifierName.assume("wrap")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("add"),
                        ModifierName.assume("max"),
                        ModifierName.assume("min")
                      ).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName.assume("sat")),
                    true
                  ),
                  InstructionForm(
                    Opcode.assume("vshl"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("dtype")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("s32"),
                        ModifierName.assume("u32")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("u32")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("clamp"),
                        ModifierName.assume("wrap")
                      ).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName.assume("sat")),
                    true
                  )
                )
              )
            ).assume[MinLength[1]])
        )
    case Vshr extends Definition(
          Opcode.assume("vshr"),
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
            Metadata(Vec(
              Metadata.Entry(
                "scalar-video-instructions-vshl-vshr".assume[MinLength[1] & NoNul],
                "9.7.19.1.2. Scalar Video Instructions: vshl, vshr".assume[MinLength[1] & NoNul],
                "vshl, vshr".assume[MinLength[1] & NoNul],
                "Scalar Video Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "// 32-bit scalar operation, with optional secondary operation\nvop.dtype.atype.u32{.sat}.mode       d, a{.asel}, b{.bsel};\nvop.dtype.atype.u32{.sat}.mode.op2   d, a{.asel}, b{.bsel}, c;\n\n// 32-bit scalar operation, with optional data merge\nvop.dtype.atype.u32{.sat}.mode  d.dsel, a{.asel}, b{.bsel}, c;\n\n vop   = { vshl, vshr };\n.dtype = .atype = { .u32, .s32 };\n.mode  = { .clamp, .wrap };\n.dsel  = .asel  = .bsel  = { .b0, .b1, .b2, .b3, .h0, .h1 };\n.op2   = { .add, .min, .max };"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(2, 0),
                Target(20),
                Vec(
                  InstructionForm(
                    Opcode.assume("vshr"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("dtype")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("s32"),
                        ModifierName.assume("u32")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("u32")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("clamp"),
                        ModifierName.assume("wrap")
                      ).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName.assume("sat")),
                    true
                  ),
                  InstructionForm(
                    Opcode.assume("vshr"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("dtype")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("s32"),
                        ModifierName.assume("u32")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("u32")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("clamp"),
                        ModifierName.assume("wrap")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("add"),
                        ModifierName.assume("max"),
                        ModifierName.assume("min")
                      ).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName.assume("sat")),
                    true
                  ),
                  InstructionForm(
                    Opcode.assume("vshr"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("dtype")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("s32"),
                        ModifierName.assume("u32")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("u32")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("clamp"),
                        ModifierName.assume("wrap")
                      ).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName.assume("sat")),
                    true
                  )
                )
              )
            ).assume[MinLength[1]])
        )
    case Vsub extends Definition(
          Opcode.assume("vsub"),
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
            Metadata(Vec(
              Metadata.Entry(
                "scalar-video-instructions-vadd-vsub-vabsdiff-vmin-vmax".assume[MinLength[1] & NoNul],
                "9.7.19.1.1. Scalar Video Instructions: vadd, vsub, vabsdiff, vmin, vmax".assume[MinLength[1] & NoNul],
                "vadd, vsub, vabsdiff, vmin, vmax".assume[MinLength[1] & NoNul],
                "Scalar Video Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "// 32-bit scalar operation, with optional secondary operation\nvop.dtype.atype.btype{.sat}       d, a{.asel}, b{.bsel};\nvop.dtype.atype.btype{.sat}.op2   d, a{.asel}, b{.bsel}, c;\n\n// 32-bit scalar operation, with optional data merge\nvop.dtype.atype.btype{.sat}  d.dsel, a{.asel}, b{.bsel}, c;\n\n vop   = { vadd, vsub, vabsdiff, vmin, vmax };\n.dtype = .atype = .btype = { .u32, .s32 };\n.dsel  = .asel  = .bsel  = { .b0, .b1, .b2, .b3, .h0, .h1 };\n.op2   = { .add, .min, .max };"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(2, 0),
                Target(20),
                Vec(
                  InstructionForm(
                    Opcode.assume("vsub"),
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("dtype")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("atype")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("s32"),
                        ModifierName.assume("u32")
                      ).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName.assume("sat")),
                    true
                  ),
                  InstructionForm(
                    Opcode.assume("vsub"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("dtype")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("atype")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("s32"),
                        ModifierName.assume("u32")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("add"),
                        ModifierName.assume("max"),
                        ModifierName.assume("min")
                      ).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName.assume("sat")),
                    true
                  ),
                  InstructionForm(
                    Opcode.assume("vsub"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("dtype")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("atype")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("s32"),
                        ModifierName.assume("u32")
                      ).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName.assume("sat")),
                    true
                  )
                )
              )
            ).assume[MinLength[1]])
        )
    case Vsub2 extends Definition(
          Opcode.assume("vsub2"),
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
            Metadata(Vec(
              Metadata.Entry(
                "simd-video-instructions-vadd2-vsub2-vavrg2-vabsdiff2-vmin2-vmax2".assume[MinLength[1] & NoNul],
                "9.7.19.2.1. SIMD Video Instructions: vadd2, vsub2, vavrg2, vabsdiff2, vmin2, vmax2".assume[MinLength[
                  1
                ] & NoNul],
                "vadd2, vsub2, vavrg2, vabsdiff2, vmin2, vmax2".assume[MinLength[1] & NoNul],
                "SIMD Video Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "// SIMD instruction with secondary SIMD merge operation\nvop2.dtype.atype.btype{.sat}  d{.mask}, a{.asel}, b{.bsel}, c;\n\n// SIMD instruction with secondary accumulate operation\nvop2.dtype.atype.btype.add  d{.mask}, a{.asel}, b{.bsel}, c;\n\n vop2  = { vadd2, vsub2, vavrg2, vabsdiff2, vmin2, vmax2 };\n.dtype = .atype = .btype = { .u32, .s32 };\n.mask  = { .h0, .h1, .h10 };  // defaults to .h10\n.asel  = .bsel  = { .hxy, where x,y are from { 0, 1, 2, 3 } };\n   .asel defaults to .h10\n   .bsel defaults to .h32"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(3, 0),
                Target(30),
                Vec(
                  InstructionForm(
                    Opcode.assume("vsub2"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("dtype")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("atype")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("s32"),
                        ModifierName.assume("u32")
                      ).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName.assume("sat")),
                    true
                  ),
                  InstructionForm(
                    Opcode.assume("vsub2"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("dtype")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("atype")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("s32"),
                        ModifierName.assume("u32")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("add")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(),
                    true
                  )
                )
              )
            ).assume[MinLength[1]])
        )
    case Vsub4 extends Definition(
          Opcode.assume("vsub4"),
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
            Metadata(Vec(
              Metadata.Entry(
                "simd-video-instructions-vadd4-vsub4-vavrg4-vabsdiff4-vmin4-vmax4".assume[MinLength[1] & NoNul],
                "9.7.19.2.3. SIMD Video Instructions: vadd4, vsub4, vavrg4, vabsdiff4, vmin4, vmax4".assume[MinLength[
                  1
                ] & NoNul],
                "vadd4, vsub4, vavrg4, vabsdiff4, vmin4, vmax4".assume[MinLength[1] & NoNul],
                "SIMD Video Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "// SIMD instruction with secondary SIMD merge operation\nvop4.dtype.atype.btype{.sat}  d{.mask}, a{.asel}, b{.bsel}, c;\n\n// SIMD instruction with secondary accumulate operation\nvop4.dtype.atype.btype.add  d{.mask}, a{.asel}, b{.bsel}, c;\nvop4  = { vadd4, vsub4, vavrg4, vabsdiff4, vmin4, vmax4 };\n\n.dtype = .atype = .btype = { .u32, .s32 };\n.mask  = { .b0,\n           .b1, .b10\n           .b2, .b20, .b21, .b210,\n           .b3, .b30, .b31, .b310, .b32, .b320, .b321, .b3210 };\n    defaults to .b3210\n.asel = .bsel = .bxyzw, where x,y,z,w are from { 0, ..., 7 };\n   .asel defaults to .b3210\n   .bsel defaults to .b7654"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(3, 0),
                Target(30),
                Vec(
                  InstructionForm(
                    Opcode.assume("vsub4"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("dtype")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("atype")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("s32"),
                        ModifierName.assume("u32")
                      ).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(ModifierName.assume("sat")),
                    true
                  ),
                  InstructionForm(
                    Opcode.assume("vsub4"),
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      _root_.scala.collection.immutable.Set(ModifierName.assume("dtype")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("atype")).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(
                        ModifierName.assume("s32"),
                        ModifierName.assume("u32")
                      ).assume[MinLength[1]],
                      _root_.scala.collection.immutable.Set(ModifierName.assume("add")).assume[MinLength[1]]
                    ),
                    _root_.scala.collection.immutable.Set(),
                    true
                  )
                )
              )
            ).assume[MinLength[1]])
        )
    case Xor extends Definition(
          Opcode.assume("xor"),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 3) Right(Instruction.Xor(operands(0), operands(1), operands(2), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("xor", Vec(3), operands.size))
          },
          () =>
            Metadata(Vec(
              Metadata.Entry(
                "logic-and-shift-instructions-xor".assume[MinLength[1] & NoNul],
                "9.7.8.3. Logic and Shift Instructions: xor".assume[MinLength[1] & NoNul],
                "xor".assume[MinLength[1] & NoNul],
                "Logic and Shift Instructions".assume[MinLength[1] & NoNul],
                Vec[String](
                  "xor.type d, a, b;\n\n.type = { .pred, .b16, .b32, .b64 };"
                ).map(_.assume[MinLength[1] & NoNul]).assume[MinLength[1]],
                Version(1, 0),
                Target(10),
                Vec(InstructionForm(
                  Opcode.assume("xor"),
                  Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                  Vec(_root_.scala.collection.immutable.Set(
                    ModifierName.assume("b16"),
                    ModifierName.assume("b32"),
                    ModifierName.assume("b64"),
                    ModifierName.assume("pred")
                  ).assume[MinLength[1]]),
                  _root_.scala.collection.immutable.Set(),
                  true
                ))
              )
            ).assume[MinLength[1]])
        )

    final lazy val meta: Metadata = metadata()

    private[v9_3] final val fixedMods: Vec[String] = Vec.from(opcode.value.split('.')).drop(1)
  }

  val version: Version = Version(9, 3)

  val definitions: Vec[Definition] = Definition.values.toVec

  private def normalizeOpcode(value: String): String = value.toLowerCase(java.util.Locale.ROOT)

  private lazy val DefinitionsByMnemonic: Map[String, Vec[Definition]] =
    definitions.groupMap(_.opcode.value.takeWhile(_ != '.'))(value => value)

  def decode(
      opcode: String,
      operands: Vec[Operand],
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Predication] = None
  ): Either[DecodeError, Instruction] = {
    val opcodeParts = Vec.from(normalizeOpcode(opcode).split('.')).filter(_.nonEmpty)
    val name = opcodeParts.headOption.getOrElse("")
    val allModifiers = opcodeParts.drop(1).map(value => Modifier(ModifierName.assume(value))) ++ mods
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
    fixed.size <= modifiers.size && fixed.indices.forall(index => modifiers(index).name.value == fixed(index))

  private def qualifiedOpcode(name: String, modifiers: Vec[Modifier]): String =
    (name +: modifiers.map(_.name)).mkString(".")
}
