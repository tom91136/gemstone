// Generated PTX instruction forms from the pinned NVIDIA ISA 9.3 specification. Regenerate with: sbt codegen
package net.kurobako.gemstone.isa.ptx

import javax.annotation.processing.Generated

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*

@Generated(Array("sbt codegen"))
enum Form(operandValues: Operand*) {
  case Abs(dest: Operand, srcA: Operand) extends Form(dest, srcA)
  case Activemask(dest: Operand) extends Form(dest)
  case Add(dest: Operand, srcA: Operand, srcB: Operand) extends Form(dest, srcA, srcB)
  case AddCc(dest: Operand, srcA: Operand, srcB: Operand) extends Form(dest, srcA, srcB)
  case Addc(dest: Operand, srcA: Operand, srcB: Operand) extends Form(dest, srcA, srcB)
  case Alloca(pointer: Operand, size: Operand, alignment: Option[Operand])
      extends Form((Vec(pointer, size) ++ alignment.toVec)*)
  case And(dest: Operand, srcA: Operand, srcB: Operand) extends Form(dest, srcA, srcB)
  case ApplyPriority(address: Operand, size: Operand) extends Form(address, size)
  case Atom(dest: Operand, address: Operand, value: Operand, compareOrCachePolicy: Option[Operand])
      extends Form((Vec(dest, address, value) ++ compareOrCachePolicy.toVec)*)
  case Bar(barrier: Operand, threadCount: Option[Operand], predicate: Option[Operand], value: Option[Operand])
      extends Form((Vec(barrier) ++ threadCount.toVec ++ predicate.toVec ++ value.toVec)*)
  case BarWarpSync(barrier: Operand) extends Form(barrier)
  case Barrier(barrier: Operand, threadCount: Option[Operand], predicate: Option[Operand], value: Option[Operand])
      extends Form((Vec(barrier) ++ threadCount.toVec ++ predicate.toVec ++ value.toVec)*)
  case BarrierCluster() extends Form()
  case Bfe(dest: Operand, srcA: Operand, srcB: Operand, srcC: Operand) extends Form(dest, srcA, srcB, srcC)
  case Bfi(dest: Operand, insertValue: Operand, baseValue: Operand, bitPosition: Operand, bitLength: Operand)
      extends Form(dest, insertValue, baseValue, bitPosition, bitLength)
  case Bfind(dest: Operand, srcA: Operand) extends Form(dest, srcA)
  case Bmsk(dest: Operand, srcA: Operand, srcB: Operand) extends Form(dest, srcA, srcB)
  case Bra(target: Operand) extends Form(target)
  case Brev(dest: Operand, srcA: Operand) extends Form(dest, srcA)
  case Brkpt() extends Form()
  case BrxIdx(index: Operand, tlist: Operand) extends Form(index, tlist)
  case Call(returnParams: Option[Operand], function: Operand, params: Option[Operand], targets: Option[Operand])
      extends Form((returnParams.toVec ++ Vec(function) ++ params.toVec ++ targets.toVec)*)
  case Clmad(dest: Operand, srcA: Operand, srcB: Operand, srcC: Operand) extends Form(dest, srcA, srcB, srcC)
  case ClusterLaunchControlQueryCancel(predicate: Operand, tryCancelResponse: Operand)
      extends Form(predicate, tryCancelResponse)
  case ClusterLaunchControlTryCancel(address: Operand, barrierAddr: Operand) extends Form(address, barrierAddr)
  case Clz(dest: Operand, srcA: Operand) extends Form(dest, srcA)
  case Cnot(dest: Operand, srcA: Operand) extends Form(dest, srcA)
  case CopySign(dest: Operand, srcA: Operand, srcB: Operand) extends Form(dest, srcA, srcB)
  case Cos(dest: Operand, srcA: Operand) extends Form(dest, srcA)
  case CpAsync(
      destAddr: Operand,
      srcAddr: Operand,
      copySize: Operand,
      srcSizeOrIgnore: Option[Operand],
      cachePolicy: Option[Operand]
  ) extends Form((Vec(destAddr, srcAddr, copySize) ++ srcSizeOrIgnore.toVec ++ cachePolicy.toVec)*)
  case CpAsyncBulkMBarrier(
      destAddr: Operand,
      srcAddr: Operand,
      size: Operand,
      barrierAddr: Operand,
      ignoredBytesLeft: Option[Operand],
      ignoredBytesRight: Option[Operand],
      ctaMask: Option[Operand],
      cachePolicy: Option[Operand]
  ) extends Form((Vec(destAddr, srcAddr, size) ++ ignoredBytesLeft.toVec ++ ignoredBytesRight.toVec ++
        Vec(barrierAddr) ++ ctaMask.toVec ++ cachePolicy.toVec)*)
  case CpAsyncBulkGroup(
      destAddr: Operand,
      srcAddr: Operand,
      size: Operand,
      cachePolicy: Option[Operand],
      byteMask: Option[Operand]
  ) extends Form((Vec(destAddr, srcAddr, size) ++ cachePolicy.toVec ++ byteMask.toVec)*)
  case CpAsyncBulkCommitGroup() extends Form()
  case CpAsyncBulkPrefetch(srcAddr: Operand, size: Operand, cachePolicy: Option[Operand])
      extends Form((Vec(srcAddr, size) ++ cachePolicy.toVec)*)
  case CpAsyncBulkPrefetchTensor(
      tensorMapAndCoordinates: Operand,
      im2colInfo: Option[Operand],
      cachePolicy: Option[Operand]
  ) extends Form((Vec(tensorMapAndCoordinates) ++ im2colInfo.toVec ++ cachePolicy.toVec)*)
  case CpAsyncBulkTensorMBarrier(
      destAddr: Operand,
      tensorMapAndCoordinates: Operand,
      barrierAddr: Operand,
      im2colInfo: Option[Operand],
      ctaMask: Option[Operand],
      cachePolicy: Option[Operand]
  ) extends Form((Vec(destAddr, tensorMapAndCoordinates, barrierAddr) ++ im2colInfo.toVec ++ ctaMask.toVec ++
        cachePolicy.toVec)*)
  case CpAsyncBulkTensorGroup(tensorMapAndCoordinates: Operand, srcAddr: Operand, cachePolicy: Option[Operand])
      extends Form((Vec(tensorMapAndCoordinates, srcAddr) ++ cachePolicy.toVec)*)
  case CpAsyncBulkWaitGroup(pendingGroupCount: Operand) extends Form(pendingGroupCount)
  case CpAsyncCommitGroup() extends Form()
  case CpAsyncMbarrierArrive(address: Operand) extends Form(address)
  case CpAsyncWaitAll() extends Form()
  case CpAsyncWaitGroup(pendingGroupCount: Operand) extends Form(pendingGroupCount)
  case CpReduceAsyncBulk(destAddr: Operand, srcAddr: Operand, size: Operand, cachePolicy: Option[Operand])
      extends Form((Vec(destAddr, srcAddr, size) ++ cachePolicy.toVec)*)
  case CpReduceAsyncBulkTensor(address: Operand, srcAddr: Operand, cachePolicy: Option[Operand])
      extends Form((Vec(address, srcAddr) ++ cachePolicy.toVec)*)
  case CreatePolicy(
      cachePolicy: Operand,
      accessProperty: Option[Operand],
      primarySize: Option[Operand],
      totalSize: Option[Operand]
  ) extends Form((Vec(cachePolicy) ++ accessProperty.toVec ++ primarySize.toVec ++ totalSize.toVec)*)
  case Cvt(dest: Operand, src: Operand, secondarySrc: Option[Operand], roundingBits: Option[Operand])
      extends Form((Vec(dest, src) ++ secondarySrc.toVec ++ roundingBits.toVec)*)
  case CvtPack(dest: Operand, src: Operand, secondarySrc: Operand, roundingBits: Option[Operand])
      extends Form((Vec(dest, src, secondarySrc) ++ roundingBits.toVec)*)
  case Cvta(dest: Operand, src: Operand) extends Form(dest, src)
  case Discard(address: Operand, size: Operand) extends Form(address, size)
  case Div(dest: Operand, srcA: Operand, srcB: Operand) extends Form(dest, srcA, srcB)
  case Dp2a(dest: Operand, srcA: Operand, srcB: Operand, srcC: Operand) extends Form(dest, srcA, srcB, srcC)
  case Dp4a(dest: Operand, srcA: Operand, srcB: Operand, srcC: Operand) extends Form(dest, srcA, srcB, srcC)
  case ElectSync(dest: Operand, memberMask: Operand) extends Form(dest, memberMask)
  case Ex2(dest: Operand, srcA: Operand) extends Form(dest, srcA)
  case Exit() extends Form()
  case FabricSubmit() extends Form()
  case FabricTryGet(destAddr: Operand, srcAddr: Operand, size: Operand, barrierAddr: Operand)
      extends Form(destAddr, srcAddr, size, barrierAddr)
  case FabricTryPullred(destAddr: Operand, srcAddr: Operand, size: Operand, barrierAddr: Operand, memberMask: Operand)
      extends Form(destAddr, srcAddr, size, barrierAddr, memberMask)
  case FabricTryPut(destAddr: Operand, srcAddr: Operand, size: Operand, barrierAddr: Operand, bytemask: Option[Operand])
      extends Form((Vec(destAddr, srcAddr, size, barrierAddr) ++ bytemask.toVec)*)
  case FabricTryRed(destAddr: Operand, srcAddr: Operand, size: Operand, barrierAddr: Operand)
      extends Form(destAddr, srcAddr, size, barrierAddr)
  case FabricWait() extends Form()
  case Fence(address: Option[Operand], size: Option[Operand]) extends Form((address.toVec ++ size.toVec)*)
  case Fma(dest: Operand, srcA: Operand, srcB: Operand, srcC: Operand) extends Form(dest, srcA, srcB, srcC)
  case Fns(dest: Operand, mask: Operand, base: Operand, offset: Operand) extends Form(dest, mask, base, offset)
  case GetCtaRank(dest: Operand, srcA: Operand) extends Form(dest, srcA)
  case GridDependencyControl() extends Form()
  case IsSpacePredicate(predicate: Operand, srcA: Operand) extends Form(predicate, srcA)
  case IsTypePredicate(predicate: Operand, srcA: Operand) extends Form(predicate, srcA)
  case Ld(dest: Operand, address: Operand, cachePolicy: Option[Operand])
      extends Form((Vec(dest, address) ++ cachePolicy.toVec)*)
  case LdGlobalNc(dest: Operand, address: Operand, cachePolicy: Option[Operand])
      extends Form((Vec(dest, address) ++ cachePolicy.toVec)*)
  case Ldmatrix(r: Operand, address: Operand) extends Form(r, address)
  case Ldu(dest: Operand, address: Operand) extends Form(dest, address)
  case Lg2(dest: Operand, srcA: Operand) extends Form(dest, srcA)
  case Lop3(dest: Operand, srcA: Operand, srcB: Operand, srcC: Operand, immLut: Operand, predicateQ: Option[Operand])
      extends Form((Vec(dest, srcA, srcB, srcC, immLut) ++ predicateQ.toVec)*)
  case Mad(dest: Operand, srcA: Operand, srcB: Operand, srcC: Operand) extends Form(dest, srcA, srcB, srcC)
  case MadCc(dest: Operand, srcA: Operand, srcB: Operand, srcC: Operand) extends Form(dest, srcA, srcB, srcC)
  case Mad24(dest: Operand, srcA: Operand, srcB: Operand, srcC: Operand) extends Form(dest, srcA, srcB, srcC)
  case Madc(dest: Operand, srcA: Operand, srcB: Operand, srcC: Operand) extends Form(dest, srcA, srcB, srcC)
  case Mapa(dest: Operand, srcA: Operand, srcB: Operand) extends Form(dest, srcA, srcB)
  case MatchAllSync(dest: Operand, srcA: Operand, memberMask: Operand) extends Form(dest, srcA, memberMask)
  case MatchAnySync(dest: Operand, srcA: Operand, memberMask: Operand) extends Form(dest, srcA, memberMask)
  case Max(dest: Operand, srcA: Operand, srcB: Operand, srcC: Option[Operand])
      extends Form((Vec(dest, srcA, srcB) ++ srcC.toVec)*)
  case MbarrierArrive(state: Operand, barrierAddr: Operand, countOrTransactionCount: Option[Operand])
      extends Form((Vec(state, barrierAddr) ++ countOrTransactionCount.toVec)*)
  case MbarrierArriveDrop(state: Operand, barrierAddr: Operand, countOrTransactionCount: Option[Operand])
      extends Form((Vec(state, barrierAddr) ++ countOrTransactionCount.toVec)*)
  case MbarrierCheckLayout(predicate: Operand, barrierAddr: Operand) extends Form(predicate, barrierAddr)
  case MbarrierCompleteTx(barrierAddr: Operand, transactionCount: Operand) extends Form(barrierAddr, transactionCount)
  case MbarrierExpectTx(barrierAddr: Operand, transactionCount: Operand) extends Form(barrierAddr, transactionCount)
  case MbarrierInit(barrierAddr: Operand, count: Operand) extends Form(barrierAddr, count)
  case MbarrierInval(barrierAddr: Operand) extends Form(barrierAddr)
  case MbarrierPendingCount(dest: Operand, state: Operand) extends Form(dest, state)
  case MbarrierTestWait(completion: Operand, reportValue: Option[Operand], barrierAddr: Operand, stateOrPhase: Operand)
      extends Form((Vec(completion) ++ reportValue.toVec ++ Vec(barrierAddr, stateOrPhase))*)
  case MbarrierTryWait(
      completion: Operand,
      reportValue: Option[Operand],
      barrierAddr: Operand,
      stateOrPhase: Operand,
      timeHint: Option[Operand]
  ) extends Form((Vec(completion) ++ reportValue.toVec ++ Vec(barrierAddr, stateOrPhase) ++ timeHint.toVec)*)
  case Membar() extends Form()
  case Min(dest: Operand, srcA: Operand, srcB: Operand, srcC: Option[Operand])
      extends Form((Vec(dest, srcA, srcB) ++ srcC.toVec)*)
  case Mma(
      dest: Operand,
      srcA: Operand,
      srcB: Operand,
      srcC: Operand,
      scaleAData: Option[Operand],
      scaleASelector: Option[Operand],
      scaleBData: Option[Operand],
      scaleBSelector: Option[Operand]
  ) extends Form((Vec(dest, srcA, srcB, srcC) ++ scaleAData.toVec ++ scaleASelector.toVec ++ scaleBData.toVec ++
        scaleBSelector.toVec)*)
  case MmaSp(dest: Operand, srcA: Operand, srcB: Operand, srcC: Operand, e: Operand, f: Operand)
      extends Form(dest, srcA, srcB, srcC, e, f)
  case MmaSpOrderedMetadata(
      dest: Operand,
      srcA: Operand,
      srcB: Operand,
      srcC: Operand,
      e: Operand,
      f: Operand,
      scaleAData: Option[Operand],
      scaleASelector: Option[Operand],
      scaleBData: Option[Operand],
      scaleBSelector: Option[Operand]
  ) extends Form((Vec(dest, srcA, srcB, srcC, e, f) ++ scaleAData.toVec ++ scaleASelector.toVec ++ scaleBData.toVec ++
        scaleBSelector.toVec)*)
  case Mov(dest: Operand, src: Operand) extends Form(dest, src)
  case Movmatrix(dest: Operand, srcA: Operand) extends Form(dest, srcA)
  case Mul(dest: Operand, srcA: Operand, srcB: Operand) extends Form(dest, srcA, srcB)
  case Mul24(dest: Operand, srcA: Operand, srcB: Operand) extends Form(dest, srcA, srcB)
  case MultimemCpAsyncBulk(destAddr: Operand, srcAddr: Operand, size: Operand, byteMask: Option[Operand])
      extends Form((Vec(destAddr, srcAddr, size) ++ byteMask.toVec)*)
  case MultimemCpReduceAsyncBulk(destAddr: Operand, srcAddr: Operand, size: Operand)
      extends Form(destAddr, srcAddr, size)
  case MultimemLdReduce(dest: Operand, address: Operand) extends Form(dest, address)
  case MultimemRed(address: Operand, value: Operand) extends Form(address, value)
  case MultimemRedAsync(address: Operand, value: Operand) extends Form(address, value)
  case MultimemSt(address: Operand, value: Operand) extends Form(address, value)
  case MultimemStAsync(address: Operand, value: Operand) extends Form(address, value)
  case NanoSleep(nanoseconds: Operand) extends Form(nanoseconds)
  case Neg(dest: Operand, srcA: Operand) extends Form(dest, srcA)
  case Not(dest: Operand, srcA: Operand) extends Form(dest, srcA)
  case Or(dest: Operand, srcA: Operand, srcB: Operand) extends Form(dest, srcA, srcB)
  case PmEvent(event: Operand) extends Form(event)
  case Popc(dest: Operand, srcA: Operand) extends Form(dest, srcA)
  case Prefetch(address: Operand) extends Form(address)
  case Prefetchu(address: Operand) extends Form(address)
  case Prmt(dest: Operand, srcA: Operand, srcB: Operand, srcC: Operand) extends Form(dest, srcA, srcB, srcC)
  case Rcp(dest: Operand, srcA: Operand) extends Form(dest, srcA)
  case RcpApproxFtzF64(dest: Operand, srcA: Operand) extends Form(dest, srcA)
  case Red(address: Operand, value: Operand, cachePolicy: Option[Operand])
      extends Form((Vec(address, value) ++ cachePolicy.toVec)*)
  case RedAsync(address: Operand, value: Operand, cachePolicy: Option[Operand])
      extends Form((Vec(address, value) ++ cachePolicy.toVec)*)
  case ReduxSync(dest: Operand, src: Operand, memberMask: Operand) extends Form(dest, src, memberMask)
  case Rem(dest: Operand, srcA: Operand, srcB: Operand) extends Form(dest, srcA, srcB)
  case Ret() extends Form()
  case Rsqrt(dest: Operand, srcA: Operand) extends Form(dest, srcA)
  case RsqrtApproxFtzF64(dest: Operand, srcA: Operand) extends Form(dest, srcA)
  case Sad(dest: Operand, srcA: Operand, srcB: Operand, srcC: Operand) extends Form(dest, srcA, srcB, srcC)
  case Selp(dest: Operand, srcA: Operand, srcB: Operand, srcC: Operand) extends Form(dest, srcA, srcB, srcC)
  case Set(dest: Operand, left: Operand, right: Operand, booleanSrc: Option[Operand])
      extends Form((Vec(dest, left, right) ++ booleanSrc.toVec)*)
  case SetMaxRegisters(immRegCount: Operand) extends Form(immRegCount)
  case Setp(dest: Operand, left: Operand, right: Operand, booleanSrc: Option[Operand])
      extends Form((Vec(dest, left, right) ++ booleanSrc.toVec)*)
  case Shf(dest: Operand, srcA: Operand, srcB: Operand, srcC: Operand) extends Form(dest, srcA, srcB, srcC)
  case Shfl(dest: Operand, srcA: Operand, srcB: Operand, srcC: Operand) extends Form(dest, srcA, srcB, srcC)
  case ShflSync(dest: Operand, srcA: Operand, srcB: Operand, srcC: Operand, memberMask: Operand)
      extends Form(dest, srcA, srcB, srcC, memberMask)
  case Shl(dest: Operand, srcA: Operand, srcB: Operand) extends Form(dest, srcA, srcB)
  case Shr(dest: Operand, srcA: Operand, srcB: Operand) extends Form(dest, srcA, srcB)
  case Sin(dest: Operand, srcA: Operand) extends Form(dest, srcA)
  case Slct(dest: Operand, srcA: Operand, srcB: Operand, srcC: Operand) extends Form(dest, srcA, srcB, srcC)
  case Sqrt(dest: Operand, srcA: Operand) extends Form(dest, srcA)
  case St(address: Operand, value: Operand, cachePolicy: Option[Operand])
      extends Form((Vec(address, value) ++ cachePolicy.toVec)*)
  case StAsync(address: Operand, value: Operand, cachePolicy: Option[Operand])
      extends Form((Vec(address, value) ++ cachePolicy.toVec)*)
  case StBulk(address: Operand, value: Operand, cachePolicy: Operand) extends Form(address, value, cachePolicy)
  case StackRestore(srcA: Operand) extends Form(srcA)
  case StackSave(dest: Operand) extends Form(dest)
  case Stmatrix(address: Operand, r: Operand) extends Form(address, r)
  case Sub(dest: Operand, srcA: Operand, srcB: Operand) extends Form(dest, srcA, srcB)
  case SubCc(dest: Operand, srcA: Operand, srcB: Operand) extends Form(dest, srcA, srcB)
  case Subc(dest: Operand, srcA: Operand, srcB: Operand) extends Form(dest, srcA, srcB)
  case Suld(dest: Operand, address: Operand) extends Form(dest, address)
  case Suq(dest: Operand, address: Operand) extends Form(dest, address)
  case Sured(address: Operand, srcC: Operand) extends Form(address, srcC)
  case Sust(address: Operand, srcC: Operand) extends Form(address, srcC)
  case Szext(dest: Operand, srcA: Operand, srcB: Operand) extends Form(dest, srcA, srcB)
  case Tanh(dest: Operand, srcA: Operand) extends Form(dest, srcA)
  case Tcgen05Alloc(destAddr: Operand, columnCount: Operand) extends Form(destAddr, columnCount)
  case Tcgen05Commit(barrierAddr: Operand, ctaMask: Option[Operand]) extends Form((Vec(barrierAddr) ++ ctaMask.toVec)*)
  case Tcgen05Cp(destAddr: Operand, srcDesc: Operand) extends Form(destAddr, srcDesc)
  case Tcgen05Dealloc(tensorMemoryAddr: Operand, columnCount: Operand) extends Form(tensorMemoryAddr, columnCount)
  case Tcgen05FenceAfterThreadSync() extends Form()
  case Tcgen05FenceBeforeThreadSync() extends Form()
  case Tcgen05Ld(destRegs: Operand, reductionValue: Operand, address: Option[Operand], halfSplitOffset: Option[Operand])
      extends Form((Vec(destRegs, reductionValue) ++ address.toVec ++ halfSplitOffset.toVec)*)
  case Tcgen05Mma(
      destAddr: Operand,
      srcA: Operand,
      srcBDesc: Operand,
      instDesc: Operand,
      scaleAAddr: Option[Operand],
      scaleBAddr: Option[Operand],
      disabledOutputLanes: Option[Operand],
      enableInputD: Operand,
      scaleInputD: Option[Operand]
  ) extends Form((Vec(destAddr, srcA, srcBDesc, instDesc) ++ scaleAAddr.toVec ++ scaleBAddr.toVec ++
        disabledOutputLanes.toVec ++ Vec(enableInputD) ++ scaleInputD.toVec)*)
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
      scaleInputD: Option[Operand]
  ) extends Form((Vec(destAddr, srcA, srcBDesc, sparseMetadataAddr, instDesc) ++ scaleAAddr.toVec ++ scaleBAddr.toVec ++
        disabledOutputLanes.toVec ++ Vec(enableInputD) ++ scaleInputD.toVec)*)
  case Tcgen05MmaWs(
      destAddr: Operand,
      srcA: Operand,
      srcBDesc: Operand,
      instDesc: Operand,
      enableInputD: Operand,
      zeroColumnMaskDesc: Option[Operand]
  ) extends Form((Vec(destAddr, srcA, srcBDesc, instDesc, enableInputD) ++ zeroColumnMaskDesc.toVec)*)
  case Tcgen05MmaWsSp(
      destAddr: Operand,
      srcA: Operand,
      srcBDesc: Operand,
      sparseMetadataAddr: Operand,
      instDesc: Operand,
      enableInputD: Operand,
      zeroColumnMaskDesc: Option[Operand]
  ) extends Form((Vec(destAddr, srcA, srcBDesc, sparseMetadataAddr, instDesc, enableInputD) ++
        zeroColumnMaskDesc.toVec)*)
  case Tcgen05RelinquishAllocPermit() extends Form()
  case Tcgen05Shift(tensorMemoryAddr: Operand) extends Form(tensorMemoryAddr)
  case Tcgen05St(address: Operand, halfSplitOffset: Operand, srcRegs: Option[Operand])
      extends Form((Vec(address, halfSplitOffset) ++ srcRegs.toVec)*)
  case Tcgen05WaitLd() extends Form()
  case Tcgen05WaitSt() extends Form()
  case TensorMapCopyFenceProxy(dest: Operand, srcOrOrdinal: Operand, valueOrSize: Operand)
      extends Form(dest, srcOrOrdinal, valueOrSize)
  case TensorMapReplace(dest: Operand, srcOrOrdinal: Operand, valueOrSize: Option[Operand])
      extends Form((Vec(dest, srcOrOrdinal) ++ valueOrSize.toVec)*)
  case TestPredicate(predicate: Operand, srcA: Operand) extends Form(predicate, srcA)
  case Tex(
      dest: Operand,
      textureAndCoordinates: Operand,
      lodOffsetOrDerivativeX: Option[Operand],
      depthCompareOrDerivativeY: Option[Operand],
      offset: Option[Operand],
      depthCompare: Option[Operand]
  ) extends Form((Vec(dest, textureAndCoordinates) ++ lodOffsetOrDerivativeX.toVec ++ depthCompareOrDerivativeY.toVec ++
        offset.toVec ++ depthCompare.toVec)*)
  case Tld4(dest: Operand, textureAndCoordinates: Operand, offset: Option[Operand], depthCompare: Option[Operand])
      extends Form((Vec(dest, textureAndCoordinates) ++ offset.toVec ++ depthCompare.toVec)*)
  case Trap() extends Form()
  case Txq(dest: Operand, texture: Operand, lod: Option[Operand]) extends Form((Vec(dest, texture) ++ lod.toVec)*)
  case Vabsdiff(dest: Operand, srcA: Operand, srcB: Operand, srcC: Option[Operand])
      extends Form((Vec(dest, srcA, srcB) ++ srcC.toVec)*)
  case Vabsdiff2(dest: Operand, srcA: Operand, srcB: Operand, srcC: Operand) extends Form(dest, srcA, srcB, srcC)
  case Vabsdiff4(dest: Operand, srcA: Operand, srcB: Operand, srcC: Operand) extends Form(dest, srcA, srcB, srcC)
  case Vadd(dest: Operand, srcA: Operand, srcB: Operand, srcC: Option[Operand])
      extends Form((Vec(dest, srcA, srcB) ++ srcC.toVec)*)
  case Vadd2(dest: Operand, srcA: Operand, srcB: Operand, srcC: Operand) extends Form(dest, srcA, srcB, srcC)
  case Vadd4(dest: Operand, srcA: Operand, srcB: Operand, srcC: Operand) extends Form(dest, srcA, srcB, srcC)
  case Vavrg2(dest: Operand, srcA: Operand, srcB: Operand, srcC: Operand) extends Form(dest, srcA, srcB, srcC)
  case Vavrg4(dest: Operand, srcA: Operand, srcB: Operand, srcC: Operand) extends Form(dest, srcA, srcB, srcC)
  case Vmad(dest: Operand, srcA: Operand, srcB: Operand, srcC: Operand) extends Form(dest, srcA, srcB, srcC)
  case Vmax(dest: Operand, srcA: Operand, srcB: Operand, srcC: Option[Operand])
      extends Form((Vec(dest, srcA, srcB) ++ srcC.toVec)*)
  case Vmax2(dest: Operand, srcA: Operand, srcB: Operand, srcC: Operand) extends Form(dest, srcA, srcB, srcC)
  case Vmax4(dest: Operand, srcA: Operand, srcB: Operand, srcC: Operand) extends Form(dest, srcA, srcB, srcC)
  case Vmin(dest: Operand, srcA: Operand, srcB: Operand, srcC: Option[Operand])
      extends Form((Vec(dest, srcA, srcB) ++ srcC.toVec)*)
  case Vmin2(dest: Operand, srcA: Operand, srcB: Operand, srcC: Operand) extends Form(dest, srcA, srcB, srcC)
  case Vmin4(dest: Operand, srcA: Operand, srcB: Operand, srcC: Operand) extends Form(dest, srcA, srcB, srcC)
  case Vote(dest: Operand, srcA: Operand) extends Form(dest, srcA)
  case VoteSync(dest: Operand, srcA: Operand, memberMask: Operand) extends Form(dest, srcA, memberMask)
  case Vset(dest: Operand, srcA: Operand, srcB: Operand, srcC: Option[Operand])
      extends Form((Vec(dest, srcA, srcB) ++ srcC.toVec)*)
  case Vset2(dest: Operand, srcA: Operand, srcB: Operand, srcC: Operand) extends Form(dest, srcA, srcB, srcC)
  case Vset4(dest: Operand, srcA: Operand, srcB: Operand, srcC: Operand) extends Form(dest, srcA, srcB, srcC)
  case Vshl(dest: Operand, srcA: Operand, srcB: Operand, srcC: Option[Operand])
      extends Form((Vec(dest, srcA, srcB) ++ srcC.toVec)*)
  case Vshr(dest: Operand, srcA: Operand, srcB: Operand, srcC: Option[Operand])
      extends Form((Vec(dest, srcA, srcB) ++ srcC.toVec)*)
  case Vsub(dest: Operand, srcA: Operand, srcB: Operand, srcC: Option[Operand])
      extends Form((Vec(dest, srcA, srcB) ++ srcC.toVec)*)
  case Vsub2(dest: Operand, srcA: Operand, srcB: Operand, srcC: Operand) extends Form(dest, srcA, srcB, srcC)
  case Vsub4(dest: Operand, srcA: Operand, srcB: Operand, srcC: Operand) extends Form(dest, srcA, srcB, srcC)
  case WgmmaCommitGroup() extends Form()
  case WgmmaFence() extends Form()
  case WgmmaMmaAsync(
      dest: Operand,
      aDesc: Operand,
      bDesc: Operand,
      scaleD: Operand,
      immScaleA: Option[Operand],
      immScaleB: Option[Operand],
      immTransA: Option[Operand],
      immTransB: Option[Operand]
  ) extends Form((Vec(dest, aDesc, bDesc, scaleD) ++ immScaleA.toVec ++ immScaleB.toVec ++ immTransA.toVec ++
        immTransB.toVec)*)
  case WgmmaMmaAsyncSp(
      dest: Operand,
      aDesc: Operand,
      bDesc: Operand,
      spMeta: Operand,
      spSel: Operand,
      scaleD: Operand,
      immScaleA: Option[Operand],
      immScaleB: Option[Operand],
      immTransA: Option[Operand],
      immTransB: Option[Operand]
  ) extends Form((Vec(dest, aDesc, bDesc, spMeta, spSel, scaleD) ++ immScaleA.toVec ++ immScaleB.toVec ++
        immTransA.toVec ++ immTransB.toVec)*)
  case WgmmaWaitGroup(n: Operand) extends Form(n)
  case WmmaLoad(r: Operand, address: Operand, stride: Option[Operand]) extends Form((Vec(r, address) ++ stride.toVec)*)
  case WmmaMma(dest: Operand, srcA: Operand, srcB: Operand, srcC: Operand) extends Form(dest, srcA, srcB, srcC)
  case WmmaStore(address: Operand, r: Operand, stride: Option[Operand]) extends Form((Vec(address, r) ++ stride.toVec)*)
  case Xor(dest: Operand, srcA: Operand, srcB: Operand) extends Form(dest, srcA, srcB)

  val operands: Vec[Operand] = operandValues.toVec
}
