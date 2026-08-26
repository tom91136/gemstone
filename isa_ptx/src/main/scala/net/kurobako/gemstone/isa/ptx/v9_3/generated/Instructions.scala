// Generated from the pinned NVIDIA PTX ISA 9.3 specification. Do not edit by hand.
package net.kurobako.gemstone.isa.ptx.v9_3

enum InstructionDecodeError {
  case UnknownOpcode(opcode: String)
  case InvalidOperandCount(opcode: String, expected: Vector[Int], actual: Int)
}

enum Instruction {
  case Abs(destination: Operand, sourceA: Operand, modifiers: Vector[Modifier], predication: Option[Predication])
  case Activemask(destination: Operand, modifiers: Vector[Modifier], predication: Option[Predication])
  case Add(destination: Operand, sourceA: Operand, sourceB: Operand, modifiers: Vector[Modifier], predication: Option[Predication])
  case AddCc(destination: Operand, sourceA: Operand, sourceB: Operand, modifiers: Vector[Modifier], predication: Option[Predication])
  case Addc(destination: Operand, sourceA: Operand, sourceB: Operand, modifiers: Vector[Modifier], predication: Option[Predication])
  case Alloca(pointer: Operand, size: Operand, alignment: Option[Operand], modifiers: Vector[Modifier], predication: Option[Predication])
  case And(destination: Operand, sourceA: Operand, sourceB: Operand, modifiers: Vector[Modifier], predication: Option[Predication])
  case ApplyPriority(address: Operand, size: Operand, modifiers: Vector[Modifier], predication: Option[Predication])
  case Atom(destination: Operand, address: Operand, value: Operand, compareOrCachePolicy: Option[Operand], modifiers: Vector[Modifier], predication: Option[Predication])
  case Bar(barrier: Operand, threadCount: Option[Operand], predicate: Option[Operand], value: Option[Operand], modifiers: Vector[Modifier], predication: Option[Predication])
  case BarWarpSync(barrier: Operand, modifiers: Vector[Modifier], predication: Option[Predication])
  case Barrier(barrier: Operand, threadCount: Option[Operand], predicate: Option[Operand], value: Option[Operand], modifiers: Vector[Modifier], predication: Option[Predication])
  case BarrierCluster(modifiers: Vector[Modifier], predication: Option[Predication])
  case Bfe(destination: Operand, sourceA: Operand, sourceB: Operand, sourceC: Operand, modifiers: Vector[Modifier], predication: Option[Predication])
  case Bfi(destination: Operand, insertValue: Operand, baseValue: Operand, bitPosition: Operand, bitLength: Operand, modifiers: Vector[Modifier], predication: Option[Predication])
  case Bfind(destination: Operand, sourceA: Operand, modifiers: Vector[Modifier], predication: Option[Predication])
  case Bmsk(destination: Operand, sourceA: Operand, sourceB: Operand, modifiers: Vector[Modifier], predication: Option[Predication])
  case Bra(target: Operand, modifiers: Vector[Modifier], predication: Option[Predication])
  case Brev(destination: Operand, sourceA: Operand, modifiers: Vector[Modifier], predication: Option[Predication])
  case Brkpt(modifiers: Vector[Modifier], predication: Option[Predication])
  case BrxIdx(index: Operand, tlist: Operand, modifiers: Vector[Modifier], predication: Option[Predication])
  case Call(function: Operand, returnParameters: Option[Operand], parameters: Option[Operand], targets: Option[Operand], modifiers: Vector[Modifier], predication: Option[Predication])
  case Clmad(destination: Operand, sourceA: Operand, sourceB: Operand, sourceC: Operand, modifiers: Vector[Modifier], predication: Option[Predication])
  case ClusterLaunchControlQueryCancel(predicate: Operand, tryCancelResponse: Operand, modifiers: Vector[Modifier], predication: Option[Predication])
  case ClusterLaunchControlTryCancel(address: Operand, barrierAddress: Operand, modifiers: Vector[Modifier], predication: Option[Predication])
  case Clz(destination: Operand, sourceA: Operand, modifiers: Vector[Modifier], predication: Option[Predication])
  case Cnot(destination: Operand, sourceA: Operand, modifiers: Vector[Modifier], predication: Option[Predication])
  case CopySign(destination: Operand, sourceA: Operand, sourceB: Operand, modifiers: Vector[Modifier], predication: Option[Predication])
  case Cos(destination: Operand, sourceA: Operand, modifiers: Vector[Modifier], predication: Option[Predication])
  case CpAsync(destinationAddress: Operand, sourceAddress: Operand, copySize: Operand, sourceSizeOrIgnore: Option[Operand], cachePolicy: Option[Operand], modifiers: Vector[Modifier], predication: Option[Predication])
  case CpAsyncBulkMBarrier(destinationAddress: Operand, sourceAddress: Operand, size: Operand, barrierAddress: Operand, ignoredBytesLeft: Option[Operand], ignoredBytesRight: Option[Operand], ctaMask: Option[Operand], cachePolicy: Option[Operand], modifiers: Vector[Modifier], predication: Option[Predication])
  case CpAsyncBulkGroup(destinationAddress: Operand, sourceAddress: Operand, size: Operand, cachePolicy: Option[Operand], byteMask: Option[Operand], modifiers: Vector[Modifier], predication: Option[Predication])
  case CpAsyncBulkCommitGroup(modifiers: Vector[Modifier], predication: Option[Predication])
  case CpAsyncBulkPrefetch(sourceAddress: Operand, size: Operand, cachePolicy: Option[Operand], modifiers: Vector[Modifier], predication: Option[Predication])
  case CpAsyncBulkPrefetchTensor(tensorMapAndCoordinates: Operand, im2colInfo: Option[Operand], cachePolicy: Option[Operand], modifiers: Vector[Modifier], predication: Option[Predication])
  case CpAsyncBulkTensorMBarrier(destinationAddress: Operand, tensorMapAndCoordinates: Operand, barrierAddress: Operand, im2colInfo: Option[Operand], ctaMask: Option[Operand], cachePolicy: Option[Operand], modifiers: Vector[Modifier], predication: Option[Predication])
  case CpAsyncBulkTensorGroup(tensorMapAndCoordinates: Operand, sourceAddress: Operand, cachePolicy: Option[Operand], modifiers: Vector[Modifier], predication: Option[Predication])
  case CpAsyncBulkWaitGroup(pendingGroupCount: Operand, modifiers: Vector[Modifier], predication: Option[Predication])
  case CpAsyncCommitGroup(modifiers: Vector[Modifier], predication: Option[Predication])
  case CpAsyncMbarrierArrive(address: Operand, modifiers: Vector[Modifier], predication: Option[Predication])
  case CpAsyncWaitAll(modifiers: Vector[Modifier], predication: Option[Predication])
  case CpAsyncWaitGroup(pendingGroupCount: Operand, modifiers: Vector[Modifier], predication: Option[Predication])
  case CpReduceAsyncBulk(destinationAddress: Operand, sourceAddress: Operand, size: Operand, cachePolicy: Option[Operand], modifiers: Vector[Modifier], predication: Option[Predication])
  case CpReduceAsyncBulkTensor(address: Operand, sourceAddress: Operand, cachePolicy: Option[Operand], modifiers: Vector[Modifier], predication: Option[Predication])
  case CreatePolicy(cachePolicy: Operand, accessProperty: Option[Operand], primarySize: Option[Operand], totalSize: Option[Operand], modifiers: Vector[Modifier], predication: Option[Predication])
  case Cvt(destination: Operand, source: Operand, secondarySource: Option[Operand], roundingBits: Option[Operand], modifiers: Vector[Modifier], predication: Option[Predication])
  case CvtPack(destination: Operand, source: Operand, secondarySource: Operand, roundingBits: Option[Operand], modifiers: Vector[Modifier], predication: Option[Predication])
  case Cvta(destination: Operand, source: Operand, modifiers: Vector[Modifier], predication: Option[Predication])
  case Discard(address: Operand, size: Operand, modifiers: Vector[Modifier], predication: Option[Predication])
  case Div(destination: Operand, sourceA: Operand, sourceB: Operand, modifiers: Vector[Modifier], predication: Option[Predication])
  case Dp2a(destination: Operand, sourceA: Operand, sourceB: Operand, sourceC: Operand, modifiers: Vector[Modifier], predication: Option[Predication])
  case Dp4a(destination: Operand, sourceA: Operand, sourceB: Operand, sourceC: Operand, modifiers: Vector[Modifier], predication: Option[Predication])
  case ElectSync(destination: Operand, memberMask: Operand, modifiers: Vector[Modifier], predication: Option[Predication])
  case Ex2(destination: Operand, sourceA: Operand, modifiers: Vector[Modifier], predication: Option[Predication])
  case Exit(modifiers: Vector[Modifier], predication: Option[Predication])
  case FabricSubmit(modifiers: Vector[Modifier], predication: Option[Predication])
  case FabricTryGet(destinationAddress: Operand, sourceAddress: Operand, size: Operand, barrierAddress: Operand, modifiers: Vector[Modifier], predication: Option[Predication])
  case FabricTryPullred(destinationAddress: Operand, sourceAddress: Operand, size: Operand, barrierAddress: Operand, memberMask: Operand, modifiers: Vector[Modifier], predication: Option[Predication])
  case FabricTryPut(destinationAddress: Operand, sourceAddress: Operand, size: Operand, barrierAddress: Operand, bytemask: Option[Operand], modifiers: Vector[Modifier], predication: Option[Predication])
  case FabricTryRed(destinationAddress: Operand, sourceAddress: Operand, size: Operand, barrierAddress: Operand, modifiers: Vector[Modifier], predication: Option[Predication])
  case FabricWait(modifiers: Vector[Modifier], predication: Option[Predication])
  case Fence(address: Option[Operand], size: Option[Operand], modifiers: Vector[Modifier], predication: Option[Predication])
  case Fma(destination: Operand, sourceA: Operand, sourceB: Operand, sourceC: Operand, modifiers: Vector[Modifier], predication: Option[Predication])
  case Fns(destination: Operand, mask: Operand, base: Operand, offset: Operand, modifiers: Vector[Modifier], predication: Option[Predication])
  case GetCtaRank(destination: Operand, sourceA: Operand, modifiers: Vector[Modifier], predication: Option[Predication])
  case GridDependencyControl(modifiers: Vector[Modifier], predication: Option[Predication])
  case IsSpacePredicate(predicate: Operand, sourceA: Operand, modifiers: Vector[Modifier], predication: Option[Predication])
  case IsTypePredicate(predicate: Operand, sourceA: Operand, modifiers: Vector[Modifier], predication: Option[Predication])
  case Ld(destination: Operand, address: Operand, cachePolicy: Option[Operand], modifiers: Vector[Modifier], predication: Option[Predication])
  case LdGlobalNc(destination: Operand, address: Operand, cachePolicy: Option[Operand], modifiers: Vector[Modifier], predication: Option[Predication])
  case Ldu(destination: Operand, address: Operand, modifiers: Vector[Modifier], predication: Option[Predication])
  case Lg2(destination: Operand, sourceA: Operand, modifiers: Vector[Modifier], predication: Option[Predication])
  case Lop3(destination: Operand, sourceA: Operand, sourceB: Operand, sourceC: Operand, immLut: Operand, predicateQ: Option[Operand], modifiers: Vector[Modifier], predication: Option[Predication])
  case Mad(destination: Operand, sourceA: Operand, sourceB: Operand, sourceC: Operand, modifiers: Vector[Modifier], predication: Option[Predication])
  case MadCc(destination: Operand, sourceA: Operand, sourceB: Operand, sourceC: Operand, modifiers: Vector[Modifier], predication: Option[Predication])
  case Mad24(destination: Operand, sourceA: Operand, sourceB: Operand, sourceC: Operand, modifiers: Vector[Modifier], predication: Option[Predication])
  case Madc(destination: Operand, sourceA: Operand, sourceB: Operand, sourceC: Operand, modifiers: Vector[Modifier], predication: Option[Predication])
  case Mapa(destination: Operand, sourceA: Operand, sourceB: Operand, modifiers: Vector[Modifier], predication: Option[Predication])
  case MatchAllSync(destination: Operand, sourceA: Operand, memberMask: Operand, modifiers: Vector[Modifier], predication: Option[Predication])
  case MatchAnySync(destination: Operand, sourceA: Operand, memberMask: Operand, modifiers: Vector[Modifier], predication: Option[Predication])
  case Max(destination: Operand, sourceA: Operand, sourceB: Operand, sourceC: Option[Operand], modifiers: Vector[Modifier], predication: Option[Predication])
  case MbarrierArrive(state: Operand, barrierAddress: Operand, countOrTransactionCount: Option[Operand], modifiers: Vector[Modifier], predication: Option[Predication])
  case MbarrierArriveDrop(state: Operand, barrierAddress: Operand, countOrTransactionCount: Option[Operand], modifiers: Vector[Modifier], predication: Option[Predication])
  case MbarrierCheckLayout(predicate: Operand, barrierAddress: Operand, modifiers: Vector[Modifier], predication: Option[Predication])
  case MbarrierCompleteTx(barrierAddress: Operand, transactionCount: Operand, modifiers: Vector[Modifier], predication: Option[Predication])
  case MbarrierExpectTx(barrierAddress: Operand, transactionCount: Operand, modifiers: Vector[Modifier], predication: Option[Predication])
  case MbarrierInit(barrierAddress: Operand, count: Operand, modifiers: Vector[Modifier], predication: Option[Predication])
  case MbarrierInval(barrierAddress: Operand, modifiers: Vector[Modifier], predication: Option[Predication])
  case MbarrierPendingCount(destination: Operand, state: Operand, modifiers: Vector[Modifier], predication: Option[Predication])
  case MbarrierTestWait(completion: Operand, reportValue: Option[Operand], barrierAddress: Operand, stateOrPhase: Operand, modifiers: Vector[Modifier], predication: Option[Predication])
  case MbarrierTryWait(completion: Operand, reportValue: Option[Operand], barrierAddress: Operand, stateOrPhase: Operand, timeHint: Option[Operand], modifiers: Vector[Modifier], predication: Option[Predication])
  case Membar(modifiers: Vector[Modifier], predication: Option[Predication])
  case Min(destination: Operand, sourceA: Operand, sourceB: Operand, sourceC: Option[Operand], modifiers: Vector[Modifier], predication: Option[Predication])
  case Mov(destination: Operand, source: Operand, modifiers: Vector[Modifier], predication: Option[Predication])
  case Mul(destination: Operand, sourceA: Operand, sourceB: Operand, modifiers: Vector[Modifier], predication: Option[Predication])
  case Mul24(destination: Operand, sourceA: Operand, sourceB: Operand, modifiers: Vector[Modifier], predication: Option[Predication])
  case MultimemCpAsyncBulk(destinationAddress: Operand, sourceAddress: Operand, size: Operand, byteMask: Option[Operand], modifiers: Vector[Modifier], predication: Option[Predication])
  case MultimemCpReduceAsyncBulk(destinationAddress: Operand, sourceAddress: Operand, size: Operand, modifiers: Vector[Modifier], predication: Option[Predication])
  case MultimemLdReduce(destination: Operand, address: Operand, modifiers: Vector[Modifier], predication: Option[Predication])
  case MultimemRed(address: Operand, value: Operand, modifiers: Vector[Modifier], predication: Option[Predication])
  case MultimemRedAsync(address: Operand, value: Operand, modifiers: Vector[Modifier], predication: Option[Predication])
  case MultimemSt(address: Operand, value: Operand, modifiers: Vector[Modifier], predication: Option[Predication])
  case MultimemStAsync(address: Operand, value: Operand, modifiers: Vector[Modifier], predication: Option[Predication])
  case NanoSleep(nanoseconds: Operand, modifiers: Vector[Modifier], predication: Option[Predication])
  case Neg(destination: Operand, sourceA: Operand, modifiers: Vector[Modifier], predication: Option[Predication])
  case Not(destination: Operand, sourceA: Operand, modifiers: Vector[Modifier], predication: Option[Predication])
  case Or(destination: Operand, sourceA: Operand, sourceB: Operand, modifiers: Vector[Modifier], predication: Option[Predication])
  case PmEvent(event: Operand, modifiers: Vector[Modifier], predication: Option[Predication])
  case Popc(destination: Operand, sourceA: Operand, modifiers: Vector[Modifier], predication: Option[Predication])
  case Prefetch(address: Operand, modifiers: Vector[Modifier], predication: Option[Predication])
  case Prefetchu(address: Operand, modifiers: Vector[Modifier], predication: Option[Predication])
  case Prmt(destination: Operand, sourceA: Operand, sourceB: Operand, sourceC: Operand, modifiers: Vector[Modifier], predication: Option[Predication])
  case Rcp(destination: Operand, sourceA: Operand, modifiers: Vector[Modifier], predication: Option[Predication])
  case RcpApproxFtzF64(destination: Operand, sourceA: Operand, modifiers: Vector[Modifier], predication: Option[Predication])
  case Red(address: Operand, value: Operand, cachePolicy: Option[Operand], modifiers: Vector[Modifier], predication: Option[Predication])
  case RedAsync(address: Operand, value: Operand, cachePolicy: Option[Operand], modifiers: Vector[Modifier], predication: Option[Predication])
  case ReduxSync(destination: Operand, src: Operand, memberMask: Operand, modifiers: Vector[Modifier], predication: Option[Predication])
  case Rem(destination: Operand, sourceA: Operand, sourceB: Operand, modifiers: Vector[Modifier], predication: Option[Predication])
  case Ret(modifiers: Vector[Modifier], predication: Option[Predication])
  case Rsqrt(destination: Operand, sourceA: Operand, modifiers: Vector[Modifier], predication: Option[Predication])
  case RsqrtApproxFtzF64(destination: Operand, sourceA: Operand, modifiers: Vector[Modifier], predication: Option[Predication])
  case Sad(destination: Operand, sourceA: Operand, sourceB: Operand, sourceC: Operand, modifiers: Vector[Modifier], predication: Option[Predication])
  case Selp(destination: Operand, sourceA: Operand, sourceB: Operand, sourceC: Operand, modifiers: Vector[Modifier], predication: Option[Predication])
  case Set(destination: Operand, left: Operand, right: Operand, booleanSource: Option[Operand], modifiers: Vector[Modifier], predication: Option[Predication])
  case SetMaxRegisters(immRegCount: Operand, modifiers: Vector[Modifier], predication: Option[Predication])
  case Setp(destination: Operand, left: Operand, right: Operand, booleanSource: Option[Operand], modifiers: Vector[Modifier], predication: Option[Predication])
  case Shf(destination: Operand, sourceA: Operand, sourceB: Operand, sourceC: Operand, modifiers: Vector[Modifier], predication: Option[Predication])
  case Shfl(destination: Operand, sourceA: Operand, sourceB: Operand, sourceC: Operand, modifiers: Vector[Modifier], predication: Option[Predication])
  case ShflSync(destination: Operand, sourceA: Operand, sourceB: Operand, sourceC: Operand, memberMask: Operand, modifiers: Vector[Modifier], predication: Option[Predication])
  case Shl(destination: Operand, sourceA: Operand, sourceB: Operand, modifiers: Vector[Modifier], predication: Option[Predication])
  case Shr(destination: Operand, sourceA: Operand, sourceB: Operand, modifiers: Vector[Modifier], predication: Option[Predication])
  case Sin(destination: Operand, sourceA: Operand, modifiers: Vector[Modifier], predication: Option[Predication])
  case Slct(destination: Operand, sourceA: Operand, sourceB: Operand, sourceC: Operand, modifiers: Vector[Modifier], predication: Option[Predication])
  case Sqrt(destination: Operand, sourceA: Operand, modifiers: Vector[Modifier], predication: Option[Predication])
  case St(address: Operand, value: Operand, cachePolicy: Option[Operand], modifiers: Vector[Modifier], predication: Option[Predication])
  case StAsync(address: Operand, value: Operand, cachePolicy: Option[Operand], modifiers: Vector[Modifier], predication: Option[Predication])
  case StBulk(address: Operand, value: Operand, cachePolicy: Operand, modifiers: Vector[Modifier], predication: Option[Predication])
  case StackRestore(sourceA: Operand, modifiers: Vector[Modifier], predication: Option[Predication])
  case StackSave(destination: Operand, modifiers: Vector[Modifier], predication: Option[Predication])
  case Sub(destination: Operand, sourceA: Operand, sourceB: Operand, modifiers: Vector[Modifier], predication: Option[Predication])
  case SubCc(destination: Operand, sourceA: Operand, sourceB: Operand, modifiers: Vector[Modifier], predication: Option[Predication])
  case Subc(destination: Operand, sourceA: Operand, sourceB: Operand, modifiers: Vector[Modifier], predication: Option[Predication])
  case Suld(destination: Operand, address: Operand, modifiers: Vector[Modifier], predication: Option[Predication])
  case Suq(destination: Operand, address: Operand, modifiers: Vector[Modifier], predication: Option[Predication])
  case Sured(address: Operand, sourceC: Operand, modifiers: Vector[Modifier], predication: Option[Predication])
  case Sust(address: Operand, sourceC: Operand, modifiers: Vector[Modifier], predication: Option[Predication])
  case Szext(destination: Operand, sourceA: Operand, sourceB: Operand, modifiers: Vector[Modifier], predication: Option[Predication])
  case Tanh(destination: Operand, sourceA: Operand, modifiers: Vector[Modifier], predication: Option[Predication])
  case Tcgen05Alloc(destinationAddress: Operand, columnCount: Operand, modifiers: Vector[Modifier], predication: Option[Predication])
  case Tcgen05Commit(barrierAddress: Operand, ctaMask: Option[Operand], modifiers: Vector[Modifier], predication: Option[Predication])
  case Tcgen05Cp(destinationAddress: Operand, sourceDescriptor: Operand, modifiers: Vector[Modifier], predication: Option[Predication])
  case Tcgen05Dealloc(tensorMemoryAddress: Operand, columnCount: Operand, modifiers: Vector[Modifier], predication: Option[Predication])
  case Tcgen05FenceAfterThreadSync(modifiers: Vector[Modifier], predication: Option[Predication])
  case Tcgen05FenceBeforeThreadSync(modifiers: Vector[Modifier], predication: Option[Predication])
  case Tcgen05Ld(destinationRegisters: Operand, reductionValue: Operand, address: Option[Operand], halfSplitOffset: Option[Operand], modifiers: Vector[Modifier], predication: Option[Predication])
  case Tcgen05Mma(destinationAddress: Operand, sourceA: Operand, sourceBDescriptor: Operand, instructionDescriptor: Operand, scaleAAddress: Option[Operand], scaleBAddress: Option[Operand], disabledOutputLanes: Option[Operand], enableInputD: Operand, scaleInputD: Option[Operand], modifiers: Vector[Modifier], predication: Option[Predication])
  case Tcgen05MmaSp(destinationAddress: Operand, sourceA: Operand, sourceBDescriptor: Operand, sparseMetadataAddress: Operand, instructionDescriptor: Operand, scaleAAddress: Option[Operand], scaleBAddress: Option[Operand], disabledOutputLanes: Option[Operand], enableInputD: Operand, scaleInputD: Option[Operand], modifiers: Vector[Modifier], predication: Option[Predication])
  case Tcgen05MmaWs(destinationAddress: Operand, sourceA: Operand, sourceBDescriptor: Operand, instructionDescriptor: Operand, enableInputD: Operand, zeroColumnMaskDescriptor: Option[Operand], modifiers: Vector[Modifier], predication: Option[Predication])
  case Tcgen05MmaWsSp(destinationAddress: Operand, sourceA: Operand, sourceBDescriptor: Operand, sparseMetadataAddress: Operand, instructionDescriptor: Operand, enableInputD: Operand, zeroColumnMaskDescriptor: Option[Operand], modifiers: Vector[Modifier], predication: Option[Predication])
  case Tcgen05RelinquishAllocPermit(modifiers: Vector[Modifier], predication: Option[Predication])
  case Tcgen05Shift(tensorMemoryAddress: Operand, modifiers: Vector[Modifier], predication: Option[Predication])
  case Tcgen05St(address: Operand, halfSplitOffset: Operand, sourceRegisters: Option[Operand], modifiers: Vector[Modifier], predication: Option[Predication])
  case Tcgen05WaitLd(modifiers: Vector[Modifier], predication: Option[Predication])
  case Tcgen05WaitSt(modifiers: Vector[Modifier], predication: Option[Predication])
  case TensorMapCopyFenceProxy(destination: Operand, sourceOrOrdinal: Operand, valueOrSize: Operand, modifiers: Vector[Modifier], predication: Option[Predication])
  case TensorMapReplace(destination: Operand, sourceOrOrdinal: Operand, valueOrSize: Option[Operand], modifiers: Vector[Modifier], predication: Option[Predication])
  case TestPredicate(predicate: Operand, sourceA: Operand, modifiers: Vector[Modifier], predication: Option[Predication])
  case Tex(destination: Operand, textureAndCoordinates: Operand, lodOffsetOrDerivativeX: Option[Operand], depthCompareOrDerivativeY: Option[Operand], offset: Option[Operand], depthCompare: Option[Operand], modifiers: Vector[Modifier], predication: Option[Predication])
  case Tld4(destination: Operand, textureAndCoordinates: Operand, offset: Option[Operand], depthCompare: Option[Operand], modifiers: Vector[Modifier], predication: Option[Predication])
  case Trap(modifiers: Vector[Modifier], predication: Option[Predication])
  case Txq(destination: Operand, texture: Operand, lod: Option[Operand], modifiers: Vector[Modifier], predication: Option[Predication])
  case Vabsdiff(destination: Operand, sourceA: Operand, sourceB: Operand, sourceC: Option[Operand], modifiers: Vector[Modifier], predication: Option[Predication])
  case Vabsdiff2(destination: Operand, sourceA: Operand, sourceB: Operand, sourceC: Operand, modifiers: Vector[Modifier], predication: Option[Predication])
  case Vabsdiff4(destination: Operand, sourceA: Operand, sourceB: Operand, sourceC: Operand, modifiers: Vector[Modifier], predication: Option[Predication])
  case Vadd(destination: Operand, sourceA: Operand, sourceB: Operand, sourceC: Option[Operand], modifiers: Vector[Modifier], predication: Option[Predication])
  case Vadd2(destination: Operand, sourceA: Operand, sourceB: Operand, sourceC: Operand, modifiers: Vector[Modifier], predication: Option[Predication])
  case Vadd4(destination: Operand, sourceA: Operand, sourceB: Operand, sourceC: Operand, modifiers: Vector[Modifier], predication: Option[Predication])
  case Vavrg2(destination: Operand, sourceA: Operand, sourceB: Operand, sourceC: Operand, modifiers: Vector[Modifier], predication: Option[Predication])
  case Vavrg4(destination: Operand, sourceA: Operand, sourceB: Operand, sourceC: Operand, modifiers: Vector[Modifier], predication: Option[Predication])
  case Vmad(destination: Operand, sourceA: Operand, sourceB: Operand, sourceC: Operand, modifiers: Vector[Modifier], predication: Option[Predication])
  case Vmax(destination: Operand, sourceA: Operand, sourceB: Operand, sourceC: Option[Operand], modifiers: Vector[Modifier], predication: Option[Predication])
  case Vmax2(destination: Operand, sourceA: Operand, sourceB: Operand, sourceC: Operand, modifiers: Vector[Modifier], predication: Option[Predication])
  case Vmax4(destination: Operand, sourceA: Operand, sourceB: Operand, sourceC: Operand, modifiers: Vector[Modifier], predication: Option[Predication])
  case Vmin(destination: Operand, sourceA: Operand, sourceB: Operand, sourceC: Option[Operand], modifiers: Vector[Modifier], predication: Option[Predication])
  case Vmin2(destination: Operand, sourceA: Operand, sourceB: Operand, sourceC: Operand, modifiers: Vector[Modifier], predication: Option[Predication])
  case Vmin4(destination: Operand, sourceA: Operand, sourceB: Operand, sourceC: Operand, modifiers: Vector[Modifier], predication: Option[Predication])
  case Vote(destination: Operand, sourceA: Operand, modifiers: Vector[Modifier], predication: Option[Predication])
  case VoteSync(destination: Operand, sourceA: Operand, memberMask: Operand, modifiers: Vector[Modifier], predication: Option[Predication])
  case Vset(destination: Operand, sourceA: Operand, sourceB: Operand, sourceC: Option[Operand], modifiers: Vector[Modifier], predication: Option[Predication])
  case Vset2(destination: Operand, sourceA: Operand, sourceB: Operand, sourceC: Operand, modifiers: Vector[Modifier], predication: Option[Predication])
  case Vset4(destination: Operand, sourceA: Operand, sourceB: Operand, sourceC: Operand, modifiers: Vector[Modifier], predication: Option[Predication])
  case Vshl(destination: Operand, sourceA: Operand, sourceB: Operand, sourceC: Option[Operand], modifiers: Vector[Modifier], predication: Option[Predication])
  case Vshr(destination: Operand, sourceA: Operand, sourceB: Operand, sourceC: Option[Operand], modifiers: Vector[Modifier], predication: Option[Predication])
  case Vsub(destination: Operand, sourceA: Operand, sourceB: Operand, sourceC: Option[Operand], modifiers: Vector[Modifier], predication: Option[Predication])
  case Vsub2(destination: Operand, sourceA: Operand, sourceB: Operand, sourceC: Operand, modifiers: Vector[Modifier], predication: Option[Predication])
  case Vsub4(destination: Operand, sourceA: Operand, sourceB: Operand, sourceC: Operand, modifiers: Vector[Modifier], predication: Option[Predication])
  case Xor(destination: Operand, sourceA: Operand, sourceB: Operand, modifiers: Vector[Modifier], predication: Option[Predication])

  final def opcode: String = this match {
    case Abs(_, _, _, _) => "abs"
    case Activemask(_, _, _) => "activemask"
    case Add(_, _, _, _, _) => "add"
    case AddCc(_, _, _, _, _) => "add.cc"
    case Addc(_, _, _, _, _) => "addc"
    case Alloca(_, _, _, _, _) => "alloca"
    case And(_, _, _, _, _) => "and"
    case ApplyPriority(_, _, _, _) => "applypriority"
    case Atom(_, _, _, _, _, _) => "atom"
    case Bar(_, _, _, _, _, _) => "bar"
    case BarWarpSync(_, _, _) => "bar.warp.sync"
    case Barrier(_, _, _, _, _, _) => "barrier"
    case BarrierCluster(_, _) => "barrier.cluster"
    case Bfe(_, _, _, _, _, _) => "bfe"
    case Bfi(_, _, _, _, _, _, _) => "bfi"
    case Bfind(_, _, _, _) => "bfind"
    case Bmsk(_, _, _, _, _) => "bmsk"
    case Bra(_, _, _) => "bra"
    case Brev(_, _, _, _) => "brev"
    case Brkpt(_, _) => "brkpt"
    case BrxIdx(_, _, _, _) => "brx.idx"
    case Call(_, _, _, _, _, _) => "call"
    case Clmad(_, _, _, _, _, _) => "clmad"
    case ClusterLaunchControlQueryCancel(_, _, _, _) => "clusterlaunchcontrol.query_cancel"
    case ClusterLaunchControlTryCancel(_, _, _, _) => "clusterlaunchcontrol.try_cancel"
    case Clz(_, _, _, _) => "clz"
    case Cnot(_, _, _, _) => "cnot"
    case CopySign(_, _, _, _, _) => "copysign"
    case Cos(_, _, _, _) => "cos"
    case CpAsync(_, _, _, _, _, _, _) => "cp.async"
    case CpAsyncBulkMBarrier(_, _, _, _, _, _, _, _, _, _) => "cp.async.bulk"
    case CpAsyncBulkGroup(_, _, _, _, _, _, _) => "cp.async.bulk"
    case CpAsyncBulkCommitGroup(_, _) => "cp.async.bulk.commit_group"
    case CpAsyncBulkPrefetch(_, _, _, _, _) => "cp.async.bulk.prefetch"
    case CpAsyncBulkPrefetchTensor(_, _, _, _, _) => "cp.async.bulk.prefetch.tensor"
    case CpAsyncBulkTensorMBarrier(_, _, _, _, _, _, _, _) => "cp.async.bulk.tensor"
    case CpAsyncBulkTensorGroup(_, _, _, _, _) => "cp.async.bulk.tensor"
    case CpAsyncBulkWaitGroup(_, _, _) => "cp.async.bulk.wait_group"
    case CpAsyncCommitGroup(_, _) => "cp.async.commit_group"
    case CpAsyncMbarrierArrive(_, _, _) => "cp.async.mbarrier.arrive"
    case CpAsyncWaitAll(_, _) => "cp.async.wait_all"
    case CpAsyncWaitGroup(_, _, _) => "cp.async.wait_group"
    case CpReduceAsyncBulk(_, _, _, _, _, _) => "cp.reduce.async.bulk"
    case CpReduceAsyncBulkTensor(_, _, _, _, _) => "cp.reduce.async.bulk.tensor"
    case CreatePolicy(_, _, _, _, _, _) => "createpolicy"
    case Cvt(_, _, _, _, _, _) => "cvt"
    case CvtPack(_, _, _, _, _, _) => "cvt.pack"
    case Cvta(_, _, _, _) => "cvta"
    case Discard(_, _, _, _) => "discard"
    case Div(_, _, _, _, _) => "div"
    case Dp2a(_, _, _, _, _, _) => "dp2a"
    case Dp4a(_, _, _, _, _, _) => "dp4a"
    case ElectSync(_, _, _, _) => "elect.sync"
    case Ex2(_, _, _, _) => "ex2"
    case Exit(_, _) => "exit"
    case FabricSubmit(_, _) => "fabric.submit"
    case FabricTryGet(_, _, _, _, _, _) => "fabric.try_get"
    case FabricTryPullred(_, _, _, _, _, _, _) => "fabric.try_pullred"
    case FabricTryPut(_, _, _, _, _, _, _) => "fabric.try_put"
    case FabricTryRed(_, _, _, _, _, _) => "fabric.try_red"
    case FabricWait(_, _) => "fabric.wait"
    case Fence(_, _, _, _) => "fence"
    case Fma(_, _, _, _, _, _) => "fma"
    case Fns(_, _, _, _, _, _) => "fns"
    case GetCtaRank(_, _, _, _) => "getctarank"
    case GridDependencyControl(_, _) => "griddepcontrol"
    case IsSpacePredicate(_, _, _, _) => "isspacep"
    case IsTypePredicate(_, _, _, _) => "istypep"
    case Ld(_, _, _, _, _) => "ld"
    case LdGlobalNc(_, _, _, _, _) => "ld.global.nc"
    case Ldu(_, _, _, _) => "ldu"
    case Lg2(_, _, _, _) => "lg2"
    case Lop3(_, _, _, _, _, _, _, _) => "lop3"
    case Mad(_, _, _, _, _, _) => "mad"
    case MadCc(_, _, _, _, _, _) => "mad.cc"
    case Mad24(_, _, _, _, _, _) => "mad24"
    case Madc(_, _, _, _, _, _) => "madc"
    case Mapa(_, _, _, _, _) => "mapa"
    case MatchAllSync(_, _, _, _, _) => "match.all.sync"
    case MatchAnySync(_, _, _, _, _) => "match.any.sync"
    case Max(_, _, _, _, _, _) => "max"
    case MbarrierArrive(_, _, _, _, _) => "mbarrier.arrive"
    case MbarrierArriveDrop(_, _, _, _, _) => "mbarrier.arrive_drop"
    case MbarrierCheckLayout(_, _, _, _) => "mbarrier.check_layout"
    case MbarrierCompleteTx(_, _, _, _) => "mbarrier.complete_tx"
    case MbarrierExpectTx(_, _, _, _) => "mbarrier.expect_tx"
    case MbarrierInit(_, _, _, _) => "mbarrier.init"
    case MbarrierInval(_, _, _) => "mbarrier.inval"
    case MbarrierPendingCount(_, _, _, _) => "mbarrier.pending_count"
    case MbarrierTestWait(_, _, _, _, _, _) => "mbarrier.test_wait"
    case MbarrierTryWait(_, _, _, _, _, _, _) => "mbarrier.try_wait"
    case Membar(_, _) => "membar"
    case Min(_, _, _, _, _, _) => "min"
    case Mov(_, _, _, _) => "mov"
    case Mul(_, _, _, _, _) => "mul"
    case Mul24(_, _, _, _, _) => "mul24"
    case MultimemCpAsyncBulk(_, _, _, _, _, _) => "multimem.cp.async.bulk"
    case MultimemCpReduceAsyncBulk(_, _, _, _, _) => "multimem.cp.reduce.async.bulk"
    case MultimemLdReduce(_, _, _, _) => "multimem.ld_reduce"
    case MultimemRed(_, _, _, _) => "multimem.red"
    case MultimemRedAsync(_, _, _, _) => "multimem.red.async"
    case MultimemSt(_, _, _, _) => "multimem.st"
    case MultimemStAsync(_, _, _, _) => "multimem.st.async"
    case NanoSleep(_, _, _) => "nanosleep"
    case Neg(_, _, _, _) => "neg"
    case Not(_, _, _, _) => "not"
    case Or(_, _, _, _, _) => "or"
    case PmEvent(_, _, _) => "pmevent"
    case Popc(_, _, _, _) => "popc"
    case Prefetch(_, _, _) => "prefetch"
    case Prefetchu(_, _, _) => "prefetchu"
    case Prmt(_, _, _, _, _, _) => "prmt"
    case Rcp(_, _, _, _) => "rcp"
    case RcpApproxFtzF64(_, _, _, _) => "rcp.approx.ftz.f64"
    case Red(_, _, _, _, _) => "red"
    case RedAsync(_, _, _, _, _) => "red.async"
    case ReduxSync(_, _, _, _, _) => "redux.sync"
    case Rem(_, _, _, _, _) => "rem"
    case Ret(_, _) => "ret"
    case Rsqrt(_, _, _, _) => "rsqrt"
    case RsqrtApproxFtzF64(_, _, _, _) => "rsqrt.approx.ftz.f64"
    case Sad(_, _, _, _, _, _) => "sad"
    case Selp(_, _, _, _, _, _) => "selp"
    case Set(_, _, _, _, _, _) => "set"
    case SetMaxRegisters(_, _, _) => "setmaxnreg"
    case Setp(_, _, _, _, _, _) => "setp"
    case Shf(_, _, _, _, _, _) => "shf"
    case Shfl(_, _, _, _, _, _) => "shfl"
    case ShflSync(_, _, _, _, _, _, _) => "shfl.sync"
    case Shl(_, _, _, _, _) => "shl"
    case Shr(_, _, _, _, _) => "shr"
    case Sin(_, _, _, _) => "sin"
    case Slct(_, _, _, _, _, _) => "slct"
    case Sqrt(_, _, _, _) => "sqrt"
    case St(_, _, _, _, _) => "st"
    case StAsync(_, _, _, _, _) => "st.async"
    case StBulk(_, _, _, _, _) => "st.bulk"
    case StackRestore(_, _, _) => "stackrestore"
    case StackSave(_, _, _) => "stacksave"
    case Sub(_, _, _, _, _) => "sub"
    case SubCc(_, _, _, _, _) => "sub.cc"
    case Subc(_, _, _, _, _) => "subc"
    case Suld(_, _, _, _) => "suld"
    case Suq(_, _, _, _) => "suq"
    case Sured(_, _, _, _) => "sured"
    case Sust(_, _, _, _) => "sust"
    case Szext(_, _, _, _, _) => "szext"
    case Tanh(_, _, _, _) => "tanh"
    case Tcgen05Alloc(_, _, _, _) => "tcgen05.alloc"
    case Tcgen05Commit(_, _, _, _) => "tcgen05.commit"
    case Tcgen05Cp(_, _, _, _) => "tcgen05.cp"
    case Tcgen05Dealloc(_, _, _, _) => "tcgen05.dealloc"
    case Tcgen05FenceAfterThreadSync(_, _) => "tcgen05.fence::after_thread_sync"
    case Tcgen05FenceBeforeThreadSync(_, _) => "tcgen05.fence::before_thread_sync"
    case Tcgen05Ld(_, _, _, _, _, _) => "tcgen05.ld"
    case Tcgen05Mma(_, _, _, _, _, _, _, _, _, _, _) => "tcgen05.mma"
    case Tcgen05MmaSp(_, _, _, _, _, _, _, _, _, _, _, _) => "tcgen05.mma.sp"
    case Tcgen05MmaWs(_, _, _, _, _, _, _, _) => "tcgen05.mma.ws"
    case Tcgen05MmaWsSp(_, _, _, _, _, _, _, _, _) => "tcgen05.mma.ws.sp"
    case Tcgen05RelinquishAllocPermit(_, _) => "tcgen05.relinquish_alloc_permit"
    case Tcgen05Shift(_, _, _) => "tcgen05.shift"
    case Tcgen05St(_, _, _, _, _) => "tcgen05.st"
    case Tcgen05WaitLd(_, _) => "tcgen05.wait::ld"
    case Tcgen05WaitSt(_, _) => "tcgen05.wait::st"
    case TensorMapCopyFenceProxy(_, _, _, _, _) => "tensormap.cp_fenceproxy"
    case TensorMapReplace(_, _, _, _, _) => "tensormap.replace"
    case TestPredicate(_, _, _, _) => "testp"
    case Tex(_, _, _, _, _, _, _, _) => "tex"
    case Tld4(_, _, _, _, _, _) => "tld4"
    case Trap(_, _) => "trap"
    case Txq(_, _, _, _, _) => "txq"
    case Vabsdiff(_, _, _, _, _, _) => "vabsdiff"
    case Vabsdiff2(_, _, _, _, _, _) => "vabsdiff2"
    case Vabsdiff4(_, _, _, _, _, _) => "vabsdiff4"
    case Vadd(_, _, _, _, _, _) => "vadd"
    case Vadd2(_, _, _, _, _, _) => "vadd2"
    case Vadd4(_, _, _, _, _, _) => "vadd4"
    case Vavrg2(_, _, _, _, _, _) => "vavrg2"
    case Vavrg4(_, _, _, _, _, _) => "vavrg4"
    case Vmad(_, _, _, _, _, _) => "vmad"
    case Vmax(_, _, _, _, _, _) => "vmax"
    case Vmax2(_, _, _, _, _, _) => "vmax2"
    case Vmax4(_, _, _, _, _, _) => "vmax4"
    case Vmin(_, _, _, _, _, _) => "vmin"
    case Vmin2(_, _, _, _, _, _) => "vmin2"
    case Vmin4(_, _, _, _, _, _) => "vmin4"
    case Vote(_, _, _, _) => "vote"
    case VoteSync(_, _, _, _, _) => "vote.sync"
    case Vset(_, _, _, _, _, _) => "vset"
    case Vset2(_, _, _, _, _, _) => "vset2"
    case Vset4(_, _, _, _, _, _) => "vset4"
    case Vshl(_, _, _, _, _, _) => "vshl"
    case Vshr(_, _, _, _, _, _) => "vshr"
    case Vsub(_, _, _, _, _, _) => "vsub"
    case Vsub2(_, _, _, _, _, _) => "vsub2"
    case Vsub4(_, _, _, _, _, _) => "vsub4"
    case Xor(_, _, _, _, _) => "xor"
  }

  final def operands: Vector[Operand] = this match {
    case Abs(destination, sourceA, _, _) => Vector(destination) ++ Vector(sourceA)
    case Activemask(destination, _, _) => Vector(destination)
    case Add(destination, sourceA, sourceB, _, _) => Vector(destination) ++ Vector(sourceA) ++ Vector(sourceB)
    case AddCc(destination, sourceA, sourceB, _, _) => Vector(destination) ++ Vector(sourceA) ++ Vector(sourceB)
    case Addc(destination, sourceA, sourceB, _, _) => Vector(destination) ++ Vector(sourceA) ++ Vector(sourceB)
    case Alloca(pointer, size, alignment, _, _) => Vector(pointer) ++ Vector(size) ++ alignment.toVector
    case And(destination, sourceA, sourceB, _, _) => Vector(destination) ++ Vector(sourceA) ++ Vector(sourceB)
    case ApplyPriority(address, size, _, _) => Vector(address) ++ Vector(size)
    case Atom(destination, address, value, compareOrCachePolicy, _, _) => Vector(destination) ++ Vector(address) ++ Vector(value) ++ compareOrCachePolicy.toVector
    case Bar(barrier, threadCount, predicate, value, _, _) => Vector(barrier) ++ threadCount.toVector ++ predicate.toVector ++ value.toVector
    case BarWarpSync(barrier, _, _) => Vector(barrier)
    case Barrier(barrier, threadCount, predicate, value, _, _) => Vector(barrier) ++ threadCount.toVector ++ predicate.toVector ++ value.toVector
    case BarrierCluster(_, _) => Vector.empty
    case Bfe(destination, sourceA, sourceB, sourceC, _, _) => Vector(destination) ++ Vector(sourceA) ++ Vector(sourceB) ++ Vector(sourceC)
    case Bfi(destination, insertValue, baseValue, bitPosition, bitLength, _, _) => Vector(destination) ++ Vector(insertValue) ++ Vector(baseValue) ++ Vector(bitPosition) ++ Vector(bitLength)
    case Bfind(destination, sourceA, _, _) => Vector(destination) ++ Vector(sourceA)
    case Bmsk(destination, sourceA, sourceB, _, _) => Vector(destination) ++ Vector(sourceA) ++ Vector(sourceB)
    case Bra(target, _, _) => Vector(target)
    case Brev(destination, sourceA, _, _) => Vector(destination) ++ Vector(sourceA)
    case Brkpt(_, _) => Vector.empty
    case BrxIdx(index, tlist, _, _) => Vector(index) ++ Vector(tlist)
    case Call(function, returnParameters, parameters, targets, _, _) => Vector(function) ++ returnParameters.toVector ++ parameters.toVector ++ targets.toVector
    case Clmad(destination, sourceA, sourceB, sourceC, _, _) => Vector(destination) ++ Vector(sourceA) ++ Vector(sourceB) ++ Vector(sourceC)
    case ClusterLaunchControlQueryCancel(predicate, tryCancelResponse, _, _) => Vector(predicate) ++ Vector(tryCancelResponse)
    case ClusterLaunchControlTryCancel(address, barrierAddress, _, _) => Vector(address) ++ Vector(barrierAddress)
    case Clz(destination, sourceA, _, _) => Vector(destination) ++ Vector(sourceA)
    case Cnot(destination, sourceA, _, _) => Vector(destination) ++ Vector(sourceA)
    case CopySign(destination, sourceA, sourceB, _, _) => Vector(destination) ++ Vector(sourceA) ++ Vector(sourceB)
    case Cos(destination, sourceA, _, _) => Vector(destination) ++ Vector(sourceA)
    case CpAsync(destinationAddress, sourceAddress, copySize, sourceSizeOrIgnore, cachePolicy, _, _) => Vector(destinationAddress) ++ Vector(sourceAddress) ++ Vector(copySize) ++ sourceSizeOrIgnore.toVector ++ cachePolicy.toVector
    case CpAsyncBulkMBarrier(destinationAddress, sourceAddress, size, barrierAddress, ignoredBytesLeft, ignoredBytesRight, ctaMask, cachePolicy, _, _) => Vector(destinationAddress, sourceAddress, size) ++ ignoredBytesLeft.toVector ++ ignoredBytesRight.toVector ++ Vector(barrierAddress) ++ ctaMask.toVector ++ cachePolicy.toVector
    case CpAsyncBulkGroup(destinationAddress, sourceAddress, size, cachePolicy, byteMask, _, _) => Vector(destinationAddress, sourceAddress, size) ++ cachePolicy.toVector ++ byteMask.toVector
    case CpAsyncBulkCommitGroup(_, _) => Vector.empty
    case CpAsyncBulkPrefetch(sourceAddress, size, cachePolicy, _, _) => Vector(sourceAddress) ++ Vector(size) ++ cachePolicy.toVector
    case CpAsyncBulkPrefetchTensor(tensorMapAndCoordinates, im2colInfo, cachePolicy, _, _) => Vector(tensorMapAndCoordinates) ++ im2colInfo.toVector ++ cachePolicy.toVector
    case CpAsyncBulkTensorMBarrier(destinationAddress, tensorMapAndCoordinates, barrierAddress, im2colInfo, ctaMask, cachePolicy, _, _) => Vector(destinationAddress, tensorMapAndCoordinates, barrierAddress) ++ im2colInfo.toVector ++ ctaMask.toVector ++ cachePolicy.toVector
    case CpAsyncBulkTensorGroup(tensorMapAndCoordinates, sourceAddress, cachePolicy, _, _) => Vector(tensorMapAndCoordinates, sourceAddress) ++ cachePolicy.toVector
    case CpAsyncBulkWaitGroup(pendingGroupCount, _, _) => Vector(pendingGroupCount)
    case CpAsyncCommitGroup(_, _) => Vector.empty
    case CpAsyncMbarrierArrive(address, _, _) => Vector(address)
    case CpAsyncWaitAll(_, _) => Vector.empty
    case CpAsyncWaitGroup(pendingGroupCount, _, _) => Vector(pendingGroupCount)
    case CpReduceAsyncBulk(destinationAddress, sourceAddress, size, cachePolicy, _, _) => Vector(destinationAddress) ++ Vector(sourceAddress) ++ Vector(size) ++ cachePolicy.toVector
    case CpReduceAsyncBulkTensor(address, sourceAddress, cachePolicy, _, _) => Vector(address) ++ Vector(sourceAddress) ++ cachePolicy.toVector
    case CreatePolicy(cachePolicy, accessProperty, primarySize, totalSize, _, _) => Vector(cachePolicy) ++ accessProperty.toVector ++ primarySize.toVector ++ totalSize.toVector
    case Cvt(destination, source, secondarySource, roundingBits, _, _) => Vector(destination) ++ Vector(source) ++ secondarySource.toVector ++ roundingBits.toVector
    case CvtPack(destination, source, secondarySource, roundingBits, _, _) => Vector(destination) ++ Vector(source) ++ Vector(secondarySource) ++ roundingBits.toVector
    case Cvta(destination, source, _, _) => Vector(destination) ++ Vector(source)
    case Discard(address, size, _, _) => Vector(address) ++ Vector(size)
    case Div(destination, sourceA, sourceB, _, _) => Vector(destination) ++ Vector(sourceA) ++ Vector(sourceB)
    case Dp2a(destination, sourceA, sourceB, sourceC, _, _) => Vector(destination) ++ Vector(sourceA) ++ Vector(sourceB) ++ Vector(sourceC)
    case Dp4a(destination, sourceA, sourceB, sourceC, _, _) => Vector(destination) ++ Vector(sourceA) ++ Vector(sourceB) ++ Vector(sourceC)
    case ElectSync(destination, memberMask, _, _) => Vector(destination) ++ Vector(memberMask)
    case Ex2(destination, sourceA, _, _) => Vector(destination) ++ Vector(sourceA)
    case Exit(_, _) => Vector.empty
    case FabricSubmit(_, _) => Vector.empty
    case FabricTryGet(destinationAddress, sourceAddress, size, barrierAddress, _, _) => Vector(destinationAddress) ++ Vector(sourceAddress) ++ Vector(size) ++ Vector(barrierAddress)
    case FabricTryPullred(destinationAddress, sourceAddress, size, barrierAddress, memberMask, _, _) => Vector(destinationAddress) ++ Vector(sourceAddress) ++ Vector(size) ++ Vector(barrierAddress) ++ Vector(memberMask)
    case FabricTryPut(destinationAddress, sourceAddress, size, barrierAddress, bytemask, _, _) => Vector(destinationAddress) ++ Vector(sourceAddress) ++ Vector(size) ++ Vector(barrierAddress) ++ bytemask.toVector
    case FabricTryRed(destinationAddress, sourceAddress, size, barrierAddress, _, _) => Vector(destinationAddress) ++ Vector(sourceAddress) ++ Vector(size) ++ Vector(barrierAddress)
    case FabricWait(_, _) => Vector.empty
    case Fence(address, size, _, _) => address.toVector ++ size.toVector
    case Fma(destination, sourceA, sourceB, sourceC, _, _) => Vector(destination) ++ Vector(sourceA) ++ Vector(sourceB) ++ Vector(sourceC)
    case Fns(destination, mask, base, offset, _, _) => Vector(destination) ++ Vector(mask) ++ Vector(base) ++ Vector(offset)
    case GetCtaRank(destination, sourceA, _, _) => Vector(destination) ++ Vector(sourceA)
    case GridDependencyControl(_, _) => Vector.empty
    case IsSpacePredicate(predicate, sourceA, _, _) => Vector(predicate) ++ Vector(sourceA)
    case IsTypePredicate(predicate, sourceA, _, _) => Vector(predicate) ++ Vector(sourceA)
    case Ld(destination, address, cachePolicy, _, _) => Vector(destination) ++ Vector(address) ++ cachePolicy.toVector
    case LdGlobalNc(destination, address, cachePolicy, _, _) => Vector(destination) ++ Vector(address) ++ cachePolicy.toVector
    case Ldu(destination, address, _, _) => Vector(destination) ++ Vector(address)
    case Lg2(destination, sourceA, _, _) => Vector(destination) ++ Vector(sourceA)
    case Lop3(destination, sourceA, sourceB, sourceC, immLut, predicateQ, _, _) => Vector(destination) ++ Vector(sourceA) ++ Vector(sourceB) ++ Vector(sourceC) ++ Vector(immLut) ++ predicateQ.toVector
    case Mad(destination, sourceA, sourceB, sourceC, _, _) => Vector(destination) ++ Vector(sourceA) ++ Vector(sourceB) ++ Vector(sourceC)
    case MadCc(destination, sourceA, sourceB, sourceC, _, _) => Vector(destination) ++ Vector(sourceA) ++ Vector(sourceB) ++ Vector(sourceC)
    case Mad24(destination, sourceA, sourceB, sourceC, _, _) => Vector(destination) ++ Vector(sourceA) ++ Vector(sourceB) ++ Vector(sourceC)
    case Madc(destination, sourceA, sourceB, sourceC, _, _) => Vector(destination) ++ Vector(sourceA) ++ Vector(sourceB) ++ Vector(sourceC)
    case Mapa(destination, sourceA, sourceB, _, _) => Vector(destination) ++ Vector(sourceA) ++ Vector(sourceB)
    case MatchAllSync(destination, sourceA, memberMask, _, _) => Vector(destination) ++ Vector(sourceA) ++ Vector(memberMask)
    case MatchAnySync(destination, sourceA, memberMask, _, _) => Vector(destination) ++ Vector(sourceA) ++ Vector(memberMask)
    case Max(destination, sourceA, sourceB, sourceC, _, _) => Vector(destination) ++ Vector(sourceA) ++ Vector(sourceB) ++ sourceC.toVector
    case MbarrierArrive(state, barrierAddress, countOrTransactionCount, _, _) => Vector(state) ++ Vector(barrierAddress) ++ countOrTransactionCount.toVector
    case MbarrierArriveDrop(state, barrierAddress, countOrTransactionCount, _, _) => Vector(state) ++ Vector(barrierAddress) ++ countOrTransactionCount.toVector
    case MbarrierCheckLayout(predicate, barrierAddress, _, _) => Vector(predicate) ++ Vector(barrierAddress)
    case MbarrierCompleteTx(barrierAddress, transactionCount, _, _) => Vector(barrierAddress) ++ Vector(transactionCount)
    case MbarrierExpectTx(barrierAddress, transactionCount, _, _) => Vector(barrierAddress) ++ Vector(transactionCount)
    case MbarrierInit(barrierAddress, count, _, _) => Vector(barrierAddress) ++ Vector(count)
    case MbarrierInval(barrierAddress, _, _) => Vector(barrierAddress)
    case MbarrierPendingCount(destination, state, _, _) => Vector(destination) ++ Vector(state)
    case MbarrierTestWait(completion, reportValue, barrierAddress, stateOrPhase, _, _) => Vector(completion) ++ reportValue.toVector ++ Vector(barrierAddress, stateOrPhase)
    case MbarrierTryWait(completion, reportValue, barrierAddress, stateOrPhase, timeHint, _, _) => Vector(completion) ++ reportValue.toVector ++ Vector(barrierAddress, stateOrPhase) ++ timeHint.toVector
    case Membar(_, _) => Vector.empty
    case Min(destination, sourceA, sourceB, sourceC, _, _) => Vector(destination) ++ Vector(sourceA) ++ Vector(sourceB) ++ sourceC.toVector
    case Mov(destination, source, _, _) => Vector(destination) ++ Vector(source)
    case Mul(destination, sourceA, sourceB, _, _) => Vector(destination) ++ Vector(sourceA) ++ Vector(sourceB)
    case Mul24(destination, sourceA, sourceB, _, _) => Vector(destination) ++ Vector(sourceA) ++ Vector(sourceB)
    case MultimemCpAsyncBulk(destinationAddress, sourceAddress, size, byteMask, _, _) => Vector(destinationAddress) ++ Vector(sourceAddress) ++ Vector(size) ++ byteMask.toVector
    case MultimemCpReduceAsyncBulk(destinationAddress, sourceAddress, size, _, _) => Vector(destinationAddress) ++ Vector(sourceAddress) ++ Vector(size)
    case MultimemLdReduce(destination, address, _, _) => Vector(destination) ++ Vector(address)
    case MultimemRed(address, value, _, _) => Vector(address) ++ Vector(value)
    case MultimemRedAsync(address, value, _, _) => Vector(address) ++ Vector(value)
    case MultimemSt(address, value, _, _) => Vector(address) ++ Vector(value)
    case MultimemStAsync(address, value, _, _) => Vector(address) ++ Vector(value)
    case NanoSleep(nanoseconds, _, _) => Vector(nanoseconds)
    case Neg(destination, sourceA, _, _) => Vector(destination) ++ Vector(sourceA)
    case Not(destination, sourceA, _, _) => Vector(destination) ++ Vector(sourceA)
    case Or(destination, sourceA, sourceB, _, _) => Vector(destination) ++ Vector(sourceA) ++ Vector(sourceB)
    case PmEvent(event, _, _) => Vector(event)
    case Popc(destination, sourceA, _, _) => Vector(destination) ++ Vector(sourceA)
    case Prefetch(address, _, _) => Vector(address)
    case Prefetchu(address, _, _) => Vector(address)
    case Prmt(destination, sourceA, sourceB, sourceC, _, _) => Vector(destination) ++ Vector(sourceA) ++ Vector(sourceB) ++ Vector(sourceC)
    case Rcp(destination, sourceA, _, _) => Vector(destination) ++ Vector(sourceA)
    case RcpApproxFtzF64(destination, sourceA, _, _) => Vector(destination) ++ Vector(sourceA)
    case Red(address, value, cachePolicy, _, _) => Vector(address) ++ Vector(value) ++ cachePolicy.toVector
    case RedAsync(address, value, cachePolicy, _, _) => Vector(address) ++ Vector(value) ++ cachePolicy.toVector
    case ReduxSync(destination, src, memberMask, _, _) => Vector(destination) ++ Vector(src) ++ Vector(memberMask)
    case Rem(destination, sourceA, sourceB, _, _) => Vector(destination) ++ Vector(sourceA) ++ Vector(sourceB)
    case Ret(_, _) => Vector.empty
    case Rsqrt(destination, sourceA, _, _) => Vector(destination) ++ Vector(sourceA)
    case RsqrtApproxFtzF64(destination, sourceA, _, _) => Vector(destination) ++ Vector(sourceA)
    case Sad(destination, sourceA, sourceB, sourceC, _, _) => Vector(destination) ++ Vector(sourceA) ++ Vector(sourceB) ++ Vector(sourceC)
    case Selp(destination, sourceA, sourceB, sourceC, _, _) => Vector(destination) ++ Vector(sourceA) ++ Vector(sourceB) ++ Vector(sourceC)
    case Set(destination, left, right, booleanSource, _, _) => Vector(destination) ++ Vector(left) ++ Vector(right) ++ booleanSource.toVector
    case SetMaxRegisters(immRegCount, _, _) => Vector(immRegCount)
    case Setp(destination, left, right, booleanSource, _, _) => Vector(destination) ++ Vector(left) ++ Vector(right) ++ booleanSource.toVector
    case Shf(destination, sourceA, sourceB, sourceC, _, _) => Vector(destination) ++ Vector(sourceA) ++ Vector(sourceB) ++ Vector(sourceC)
    case Shfl(destination, sourceA, sourceB, sourceC, _, _) => Vector(destination) ++ Vector(sourceA) ++ Vector(sourceB) ++ Vector(sourceC)
    case ShflSync(destination, sourceA, sourceB, sourceC, memberMask, _, _) => Vector(destination) ++ Vector(sourceA) ++ Vector(sourceB) ++ Vector(sourceC) ++ Vector(memberMask)
    case Shl(destination, sourceA, sourceB, _, _) => Vector(destination) ++ Vector(sourceA) ++ Vector(sourceB)
    case Shr(destination, sourceA, sourceB, _, _) => Vector(destination) ++ Vector(sourceA) ++ Vector(sourceB)
    case Sin(destination, sourceA, _, _) => Vector(destination) ++ Vector(sourceA)
    case Slct(destination, sourceA, sourceB, sourceC, _, _) => Vector(destination) ++ Vector(sourceA) ++ Vector(sourceB) ++ Vector(sourceC)
    case Sqrt(destination, sourceA, _, _) => Vector(destination) ++ Vector(sourceA)
    case St(address, value, cachePolicy, _, _) => Vector(address) ++ Vector(value) ++ cachePolicy.toVector
    case StAsync(address, value, cachePolicy, _, _) => Vector(address) ++ Vector(value) ++ cachePolicy.toVector
    case StBulk(address, value, cachePolicy, _, _) => Vector(address) ++ Vector(value) ++ Vector(cachePolicy)
    case StackRestore(sourceA, _, _) => Vector(sourceA)
    case StackSave(destination, _, _) => Vector(destination)
    case Sub(destination, sourceA, sourceB, _, _) => Vector(destination) ++ Vector(sourceA) ++ Vector(sourceB)
    case SubCc(destination, sourceA, sourceB, _, _) => Vector(destination) ++ Vector(sourceA) ++ Vector(sourceB)
    case Subc(destination, sourceA, sourceB, _, _) => Vector(destination) ++ Vector(sourceA) ++ Vector(sourceB)
    case Suld(destination, address, _, _) => Vector(destination) ++ Vector(address)
    case Suq(destination, address, _, _) => Vector(destination) ++ Vector(address)
    case Sured(address, sourceC, _, _) => Vector(address) ++ Vector(sourceC)
    case Sust(address, sourceC, _, _) => Vector(address) ++ Vector(sourceC)
    case Szext(destination, sourceA, sourceB, _, _) => Vector(destination) ++ Vector(sourceA) ++ Vector(sourceB)
    case Tanh(destination, sourceA, _, _) => Vector(destination) ++ Vector(sourceA)
    case Tcgen05Alloc(destinationAddress, columnCount, _, _) => Vector(destinationAddress) ++ Vector(columnCount)
    case Tcgen05Commit(barrierAddress, ctaMask, _, _) => Vector(barrierAddress) ++ ctaMask.toVector
    case Tcgen05Cp(destinationAddress, sourceDescriptor, _, _) => Vector(destinationAddress) ++ Vector(sourceDescriptor)
    case Tcgen05Dealloc(tensorMemoryAddress, columnCount, _, _) => Vector(tensorMemoryAddress) ++ Vector(columnCount)
    case Tcgen05FenceAfterThreadSync(_, _) => Vector.empty
    case Tcgen05FenceBeforeThreadSync(_, _) => Vector.empty
    case Tcgen05Ld(destinationRegisters, reductionValue, address, halfSplitOffset, _, _) => Vector(destinationRegisters) ++ Vector(reductionValue) ++ address.toVector ++ halfSplitOffset.toVector
    case Tcgen05Mma(destinationAddress, sourceA, sourceBDescriptor, instructionDescriptor, scaleAAddress, scaleBAddress, disabledOutputLanes, enableInputD, scaleInputD, _, _) => Vector(destinationAddress, sourceA, sourceBDescriptor, instructionDescriptor) ++ scaleAAddress.toVector ++ scaleBAddress.toVector ++ disabledOutputLanes.toVector ++ Vector(enableInputD) ++ scaleInputD.toVector
    case Tcgen05MmaSp(destinationAddress, sourceA, sourceBDescriptor, sparseMetadataAddress, instructionDescriptor, scaleAAddress, scaleBAddress, disabledOutputLanes, enableInputD, scaleInputD, _, _) => Vector(destinationAddress, sourceA, sourceBDescriptor, sparseMetadataAddress, instructionDescriptor) ++ scaleAAddress.toVector ++ scaleBAddress.toVector ++ disabledOutputLanes.toVector ++ Vector(enableInputD) ++ scaleInputD.toVector
    case Tcgen05MmaWs(destinationAddress, sourceA, sourceBDescriptor, instructionDescriptor, enableInputD, zeroColumnMaskDescriptor, _, _) => Vector(destinationAddress) ++ Vector(sourceA) ++ Vector(sourceBDescriptor) ++ Vector(instructionDescriptor) ++ Vector(enableInputD) ++ zeroColumnMaskDescriptor.toVector
    case Tcgen05MmaWsSp(destinationAddress, sourceA, sourceBDescriptor, sparseMetadataAddress, instructionDescriptor, enableInputD, zeroColumnMaskDescriptor, _, _) => Vector(destinationAddress) ++ Vector(sourceA) ++ Vector(sourceBDescriptor) ++ Vector(sparseMetadataAddress) ++ Vector(instructionDescriptor) ++ Vector(enableInputD) ++ zeroColumnMaskDescriptor.toVector
    case Tcgen05RelinquishAllocPermit(_, _) => Vector.empty
    case Tcgen05Shift(tensorMemoryAddress, _, _) => Vector(tensorMemoryAddress)
    case Tcgen05St(address, halfSplitOffset, sourceRegisters, _, _) => Vector(address) ++ Vector(halfSplitOffset) ++ sourceRegisters.toVector
    case Tcgen05WaitLd(_, _) => Vector.empty
    case Tcgen05WaitSt(_, _) => Vector.empty
    case TensorMapCopyFenceProxy(destination, sourceOrOrdinal, valueOrSize, _, _) => Vector(destination) ++ Vector(sourceOrOrdinal) ++ Vector(valueOrSize)
    case TensorMapReplace(destination, sourceOrOrdinal, valueOrSize, _, _) => Vector(destination) ++ Vector(sourceOrOrdinal) ++ valueOrSize.toVector
    case TestPredicate(predicate, sourceA, _, _) => Vector(predicate) ++ Vector(sourceA)
    case Tex(destination, textureAndCoordinates, lodOffsetOrDerivativeX, depthCompareOrDerivativeY, offset, depthCompare, _, _) => Vector(destination) ++ Vector(textureAndCoordinates) ++ lodOffsetOrDerivativeX.toVector ++ depthCompareOrDerivativeY.toVector ++ offset.toVector ++ depthCompare.toVector
    case Tld4(destination, textureAndCoordinates, offset, depthCompare, _, _) => Vector(destination) ++ Vector(textureAndCoordinates) ++ offset.toVector ++ depthCompare.toVector
    case Trap(_, _) => Vector.empty
    case Txq(destination, texture, lod, _, _) => Vector(destination) ++ Vector(texture) ++ lod.toVector
    case Vabsdiff(destination, sourceA, sourceB, sourceC, _, _) => Vector(destination) ++ Vector(sourceA) ++ Vector(sourceB) ++ sourceC.toVector
    case Vabsdiff2(destination, sourceA, sourceB, sourceC, _, _) => Vector(destination) ++ Vector(sourceA) ++ Vector(sourceB) ++ Vector(sourceC)
    case Vabsdiff4(destination, sourceA, sourceB, sourceC, _, _) => Vector(destination) ++ Vector(sourceA) ++ Vector(sourceB) ++ Vector(sourceC)
    case Vadd(destination, sourceA, sourceB, sourceC, _, _) => Vector(destination) ++ Vector(sourceA) ++ Vector(sourceB) ++ sourceC.toVector
    case Vadd2(destination, sourceA, sourceB, sourceC, _, _) => Vector(destination) ++ Vector(sourceA) ++ Vector(sourceB) ++ Vector(sourceC)
    case Vadd4(destination, sourceA, sourceB, sourceC, _, _) => Vector(destination) ++ Vector(sourceA) ++ Vector(sourceB) ++ Vector(sourceC)
    case Vavrg2(destination, sourceA, sourceB, sourceC, _, _) => Vector(destination) ++ Vector(sourceA) ++ Vector(sourceB) ++ Vector(sourceC)
    case Vavrg4(destination, sourceA, sourceB, sourceC, _, _) => Vector(destination) ++ Vector(sourceA) ++ Vector(sourceB) ++ Vector(sourceC)
    case Vmad(destination, sourceA, sourceB, sourceC, _, _) => Vector(destination) ++ Vector(sourceA) ++ Vector(sourceB) ++ Vector(sourceC)
    case Vmax(destination, sourceA, sourceB, sourceC, _, _) => Vector(destination) ++ Vector(sourceA) ++ Vector(sourceB) ++ sourceC.toVector
    case Vmax2(destination, sourceA, sourceB, sourceC, _, _) => Vector(destination) ++ Vector(sourceA) ++ Vector(sourceB) ++ Vector(sourceC)
    case Vmax4(destination, sourceA, sourceB, sourceC, _, _) => Vector(destination) ++ Vector(sourceA) ++ Vector(sourceB) ++ Vector(sourceC)
    case Vmin(destination, sourceA, sourceB, sourceC, _, _) => Vector(destination) ++ Vector(sourceA) ++ Vector(sourceB) ++ sourceC.toVector
    case Vmin2(destination, sourceA, sourceB, sourceC, _, _) => Vector(destination) ++ Vector(sourceA) ++ Vector(sourceB) ++ Vector(sourceC)
    case Vmin4(destination, sourceA, sourceB, sourceC, _, _) => Vector(destination) ++ Vector(sourceA) ++ Vector(sourceB) ++ Vector(sourceC)
    case Vote(destination, sourceA, _, _) => Vector(destination) ++ Vector(sourceA)
    case VoteSync(destination, sourceA, memberMask, _, _) => Vector(destination) ++ Vector(sourceA) ++ Vector(memberMask)
    case Vset(destination, sourceA, sourceB, sourceC, _, _) => Vector(destination) ++ Vector(sourceA) ++ Vector(sourceB) ++ sourceC.toVector
    case Vset2(destination, sourceA, sourceB, sourceC, _, _) => Vector(destination) ++ Vector(sourceA) ++ Vector(sourceB) ++ Vector(sourceC)
    case Vset4(destination, sourceA, sourceB, sourceC, _, _) => Vector(destination) ++ Vector(sourceA) ++ Vector(sourceB) ++ Vector(sourceC)
    case Vshl(destination, sourceA, sourceB, sourceC, _, _) => Vector(destination) ++ Vector(sourceA) ++ Vector(sourceB) ++ sourceC.toVector
    case Vshr(destination, sourceA, sourceB, sourceC, _, _) => Vector(destination) ++ Vector(sourceA) ++ Vector(sourceB) ++ sourceC.toVector
    case Vsub(destination, sourceA, sourceB, sourceC, _, _) => Vector(destination) ++ Vector(sourceA) ++ Vector(sourceB) ++ sourceC.toVector
    case Vsub2(destination, sourceA, sourceB, sourceC, _, _) => Vector(destination) ++ Vector(sourceA) ++ Vector(sourceB) ++ Vector(sourceC)
    case Vsub4(destination, sourceA, sourceB, sourceC, _, _) => Vector(destination) ++ Vector(sourceA) ++ Vector(sourceB) ++ Vector(sourceC)
    case Xor(destination, sourceA, sourceB, _, _) => Vector(destination) ++ Vector(sourceA) ++ Vector(sourceB)
  }

  def modifiers: Vector[Modifier]

  def predication: Option[Predication]

  final def qualifiedOpcode: String = (opcode +: modifiers.map(_.name)).mkString(".")

}

object Instruction {
  private val ChunkCount: Int = 7

  def decode(
      opcode: String,
      operands: Vector[Operand],
      modifiers: Vector[Modifier] = Vector.empty,
      predication: Option[Predication] = None
  ): Either[InstructionDecodeError, Instruction] = {
    val opcodeParts = normalizeOpcode(opcode).split('.').toVector.filter(_.nonEmpty)
    val name = opcodeParts.headOption.getOrElse("")
    val allModifiers = opcodeParts.drop(1).map(Modifier.apply) ++ modifiers
    Math.floorMod(name.hashCode, ChunkCount) match {
      case 0 => decode0(name, operands, allModifiers, predication)
      case 1 => decode1(name, operands, allModifiers, predication)
      case 2 => decode2(name, operands, allModifiers, predication)
      case 3 => decode3(name, operands, allModifiers, predication)
      case 4 => decode4(name, operands, allModifiers, predication)
      case 5 => decode5(name, operands, allModifiers, predication)
      case 6 => decode6(name, operands, allModifiers, predication)
    }
}

  private def decode0(
      name: String,
      operands: Vector[Operand],
      modifiers: Vector[Modifier],
      predication: Option[Predication]
  ): Either[InstructionDecodeError, Instruction] =
    name match {
      case "activemask" =>
        val modifierNames = modifiers.map(_.name)
        if (true && (operands.size == 1)) Right(Activemask(operands(0), modifiers.drop(0), predication))
        else if (true) Left(InstructionDecodeError.InvalidOperandCount("activemask", Vector(1), operands.size))
        else Left(InstructionDecodeError.UnknownOpcode((name +: modifierNames).mkString(".")))
      case "alloca" =>
        val modifierNames = modifiers.map(_.name)
        if (true && (operands.size == 2 || operands.size == 3)) Right(Alloca(operands(0), operands(1), operands.lift(2), modifiers.drop(0), predication))
        else if (true) Left(InstructionDecodeError.InvalidOperandCount("alloca", Vector(2, 3), operands.size))
        else Left(InstructionDecodeError.UnknownOpcode((name +: modifierNames).mkString(".")))
      case "brx" =>
        val modifierNames = modifiers.map(_.name)
        if (modifierNames.startsWith(Vector("idx")) && (operands.size == 2)) Right(BrxIdx(operands(0), operands(1), modifiers.drop(1), predication))
        else if (modifierNames.startsWith(Vector("idx"))) Left(InstructionDecodeError.InvalidOperandCount("brx.idx", Vector(2), operands.size))
        else Left(InstructionDecodeError.UnknownOpcode((name +: modifierNames).mkString(".")))
      case "clz" =>
        val modifierNames = modifiers.map(_.name)
        if (true && (operands.size == 2)) Right(Clz(operands(0), operands(1), modifiers.drop(0), predication))
        else if (true) Left(InstructionDecodeError.InvalidOperandCount("clz", Vector(2), operands.size))
        else Left(InstructionDecodeError.UnknownOpcode((name +: modifierNames).mkString(".")))
      case "dp2a" =>
        val modifierNames = modifiers.map(_.name)
        if (true && (operands.size == 4)) Right(Dp2a(operands(0), operands(1), operands(2), operands(3), modifiers.drop(0), predication))
        else if (true) Left(InstructionDecodeError.InvalidOperandCount("dp2a", Vector(4), operands.size))
        else Left(InstructionDecodeError.UnknownOpcode((name +: modifierNames).mkString(".")))
      case "isspacep" =>
        val modifierNames = modifiers.map(_.name)
        if (true && (operands.size == 2)) Right(IsSpacePredicate(operands(0), operands(1), modifiers.drop(0), predication))
        else if (true) Left(InstructionDecodeError.InvalidOperandCount("isspacep", Vector(2), operands.size))
        else Left(InstructionDecodeError.UnknownOpcode((name +: modifierNames).mkString(".")))
      case "mad" =>
        val modifierNames = modifiers.map(_.name)
        if (modifierNames.startsWith(Vector("cc")) && (operands.size == 4)) Right(MadCc(operands(0), operands(1), operands(2), operands(3), modifiers.drop(1), predication))
        else if (modifierNames.startsWith(Vector("cc"))) Left(InstructionDecodeError.InvalidOperandCount("mad.cc", Vector(4), operands.size))
        else if (true && (operands.size == 4)) Right(Mad(operands(0), operands(1), operands(2), operands(3), modifiers.drop(0), predication))
        else if (true) Left(InstructionDecodeError.InvalidOperandCount("mad", Vector(4), operands.size))
        else Left(InstructionDecodeError.UnknownOpcode((name +: modifierNames).mkString(".")))
      case "mapa" =>
        val modifierNames = modifiers.map(_.name)
        if (true && (operands.size == 3)) Right(Mapa(operands(0), operands(1), operands(2), modifiers.drop(0), predication))
        else if (true) Left(InstructionDecodeError.InvalidOperandCount("mapa", Vector(3), operands.size))
        else Left(InstructionDecodeError.UnknownOpcode((name +: modifierNames).mkString(".")))
      case "prefetchu" =>
        val modifierNames = modifiers.map(_.name)
        if (true && (operands.size == 1)) Right(Prefetchu(operands(0), modifiers.drop(0), predication))
        else if (true) Left(InstructionDecodeError.InvalidOperandCount("prefetchu", Vector(1), operands.size))
        else Left(InstructionDecodeError.UnknownOpcode((name +: modifierNames).mkString(".")))
      case "rcp" =>
        val modifierNames = modifiers.map(_.name)
        if (modifierNames.startsWith(Vector("approx", "ftz", "f64")) && (operands.size == 2)) Right(RcpApproxFtzF64(operands(0), operands(1), modifiers.drop(3), predication))
        else if (modifierNames.startsWith(Vector("approx", "ftz", "f64"))) Left(InstructionDecodeError.InvalidOperandCount("rcp.approx.ftz.f64", Vector(2), operands.size))
        else if (true && (operands.size == 2)) Right(Rcp(operands(0), operands(1), modifiers.drop(0), predication))
        else if (true) Left(InstructionDecodeError.InvalidOperandCount("rcp", Vector(2), operands.size))
        else Left(InstructionDecodeError.UnknownOpcode((name +: modifierNames).mkString(".")))
      case "shf" =>
        val modifierNames = modifiers.map(_.name)
        if (true && (operands.size == 4)) Right(Shf(operands(0), operands(1), operands(2), operands(3), modifiers.drop(0), predication))
        else if (true) Left(InstructionDecodeError.InvalidOperandCount("shf", Vector(4), operands.size))
        else Left(InstructionDecodeError.UnknownOpcode((name +: modifierNames).mkString(".")))
      case "stackrestore" =>
        val modifierNames = modifiers.map(_.name)
        if (true && (operands.size == 1)) Right(StackRestore(operands(0), modifiers.drop(0), predication))
        else if (true) Left(InstructionDecodeError.InvalidOperandCount("stackrestore", Vector(1), operands.size))
        else Left(InstructionDecodeError.UnknownOpcode((name +: modifierNames).mkString(".")))
      case "sub" =>
        val modifierNames = modifiers.map(_.name)
        if (modifierNames.startsWith(Vector("cc")) && (operands.size == 3)) Right(SubCc(operands(0), operands(1), operands(2), modifiers.drop(1), predication))
        else if (modifierNames.startsWith(Vector("cc"))) Left(InstructionDecodeError.InvalidOperandCount("sub.cc", Vector(3), operands.size))
        else if (true && (operands.size == 3)) Right(Sub(operands(0), operands(1), operands(2), modifiers.drop(0), predication))
        else if (true) Left(InstructionDecodeError.InvalidOperandCount("sub", Vector(3), operands.size))
        else Left(InstructionDecodeError.UnknownOpcode((name +: modifierNames).mkString(".")))
      case "tcgen05" =>
        val modifierNames = modifiers.map(_.name)
        if (modifierNames.startsWith(Vector("mma", "ws", "sp")) && (operands.size == 6 || operands.size == 7)) Right(Tcgen05MmaWsSp(operands(0), operands(1), operands(2), operands(3), operands(4), operands(5), operands.lift(6), modifiers.drop(3), predication))
        else if (modifierNames.startsWith(Vector("mma", "ws", "sp"))) Left(InstructionDecodeError.InvalidOperandCount("tcgen05.mma.ws.sp", Vector(6, 7), operands.size))
        else if (modifierNames.startsWith(Vector("mma", "sp")) && (operands.size == 6 || operands.size == 7 || operands.size == 8)) Right(if (operands.size == 6) Tcgen05MmaSp(operands(0), operands(1), operands(2), operands(3), operands(4), None, None, None, operands(5), None, modifiers.drop(2), predication) else if (operands(5).isInstanceOf[Operand.Address]) Tcgen05MmaSp(operands(0), operands(1), operands(2), operands(3), operands(4), Some(operands(5)), Some(operands(6)), None, operands(7), None, modifiers.drop(2), predication) else Tcgen05MmaSp(operands(0), operands(1), operands(2), operands(3), operands(4), None, None, Some(operands(5)), operands(6), operands.lift(7), modifiers.drop(2), predication))
        else if (modifierNames.startsWith(Vector("mma", "sp"))) Left(InstructionDecodeError.InvalidOperandCount("tcgen05.mma.sp", Vector(6, 7, 8), operands.size))
        else if (modifierNames.startsWith(Vector("mma", "ws")) && (operands.size == 5 || operands.size == 6)) Right(Tcgen05MmaWs(operands(0), operands(1), operands(2), operands(3), operands(4), operands.lift(5), modifiers.drop(2), predication))
        else if (modifierNames.startsWith(Vector("mma", "ws"))) Left(InstructionDecodeError.InvalidOperandCount("tcgen05.mma.ws", Vector(5, 6), operands.size))
        else if (modifierNames.startsWith(Vector("alloc")) && (operands.size == 2)) Right(Tcgen05Alloc(operands(0), operands(1), modifiers.drop(1), predication))
        else if (modifierNames.startsWith(Vector("alloc"))) Left(InstructionDecodeError.InvalidOperandCount("tcgen05.alloc", Vector(2), operands.size))
        else if (modifierNames.startsWith(Vector("commit")) && (operands.size == 1 || operands.size == 2)) Right(Tcgen05Commit(operands(0), operands.lift(1), modifiers.drop(1), predication))
        else if (modifierNames.startsWith(Vector("commit"))) Left(InstructionDecodeError.InvalidOperandCount("tcgen05.commit", Vector(1, 2), operands.size))
        else if (modifierNames.startsWith(Vector("cp")) && (operands.size == 2)) Right(Tcgen05Cp(operands(0), operands(1), modifiers.drop(1), predication))
        else if (modifierNames.startsWith(Vector("cp"))) Left(InstructionDecodeError.InvalidOperandCount("tcgen05.cp", Vector(2), operands.size))
        else if (modifierNames.startsWith(Vector("dealloc")) && (operands.size == 2)) Right(Tcgen05Dealloc(operands(0), operands(1), modifiers.drop(1), predication))
        else if (modifierNames.startsWith(Vector("dealloc"))) Left(InstructionDecodeError.InvalidOperandCount("tcgen05.dealloc", Vector(2), operands.size))
        else if (modifierNames.startsWith(Vector("fence::after_thread_sync")) && (operands.size == 0)) Right(Tcgen05FenceAfterThreadSync(modifiers.drop(1), predication))
        else if (modifierNames.startsWith(Vector("fence::after_thread_sync"))) Left(InstructionDecodeError.InvalidOperandCount("tcgen05.fence::after_thread_sync", Vector(0), operands.size))
        else if (modifierNames.startsWith(Vector("fence::before_thread_sync")) && (operands.size == 0)) Right(Tcgen05FenceBeforeThreadSync(modifiers.drop(1), predication))
        else if (modifierNames.startsWith(Vector("fence::before_thread_sync"))) Left(InstructionDecodeError.InvalidOperandCount("tcgen05.fence::before_thread_sync", Vector(0), operands.size))
        else if (modifierNames.startsWith(Vector("ld")) && (operands.size == 2 || operands.size == 3 || operands.size == 4)) Right(Tcgen05Ld(operands(0), operands(1), operands.lift(2), operands.lift(3), modifiers.drop(1), predication))
        else if (modifierNames.startsWith(Vector("ld"))) Left(InstructionDecodeError.InvalidOperandCount("tcgen05.ld", Vector(2, 3, 4), operands.size))
        else if (modifierNames.startsWith(Vector("mma")) && (operands.size == 5 || operands.size == 6 || operands.size == 7)) Right(if (operands.size == 5) Tcgen05Mma(operands(0), operands(1), operands(2), operands(3), None, None, None, operands(4), None, modifiers.drop(1), predication) else if (operands(4).isInstanceOf[Operand.Address]) Tcgen05Mma(operands(0), operands(1), operands(2), operands(3), Some(operands(4)), Some(operands(5)), None, operands(6), None, modifiers.drop(1), predication) else Tcgen05Mma(operands(0), operands(1), operands(2), operands(3), None, None, Some(operands(4)), operands(5), operands.lift(6), modifiers.drop(1), predication))
        else if (modifierNames.startsWith(Vector("mma"))) Left(InstructionDecodeError.InvalidOperandCount("tcgen05.mma", Vector(5, 6, 7), operands.size))
        else if (modifierNames.startsWith(Vector("relinquish_alloc_permit")) && (operands.size == 0)) Right(Tcgen05RelinquishAllocPermit(modifiers.drop(1), predication))
        else if (modifierNames.startsWith(Vector("relinquish_alloc_permit"))) Left(InstructionDecodeError.InvalidOperandCount("tcgen05.relinquish_alloc_permit", Vector(0), operands.size))
        else if (modifierNames.startsWith(Vector("shift")) && (operands.size == 1)) Right(Tcgen05Shift(operands(0), modifiers.drop(1), predication))
        else if (modifierNames.startsWith(Vector("shift"))) Left(InstructionDecodeError.InvalidOperandCount("tcgen05.shift", Vector(1), operands.size))
        else if (modifierNames.startsWith(Vector("st")) && (operands.size == 2 || operands.size == 3)) Right(Tcgen05St(operands(0), operands(1), operands.lift(2), modifiers.drop(1), predication))
        else if (modifierNames.startsWith(Vector("st"))) Left(InstructionDecodeError.InvalidOperandCount("tcgen05.st", Vector(2, 3), operands.size))
        else if (modifierNames.startsWith(Vector("wait::ld")) && (operands.size == 0)) Right(Tcgen05WaitLd(modifiers.drop(1), predication))
        else if (modifierNames.startsWith(Vector("wait::ld"))) Left(InstructionDecodeError.InvalidOperandCount("tcgen05.wait::ld", Vector(0), operands.size))
        else if (modifierNames.startsWith(Vector("wait::st")) && (operands.size == 0)) Right(Tcgen05WaitSt(modifiers.drop(1), predication))
        else if (modifierNames.startsWith(Vector("wait::st"))) Left(InstructionDecodeError.InvalidOperandCount("tcgen05.wait::st", Vector(0), operands.size))
        else Left(InstructionDecodeError.UnknownOpcode((name +: modifierNames).mkString(".")))
      case "vadd" =>
        val modifierNames = modifiers.map(_.name)
        if (true && (operands.size == 3 || operands.size == 4)) Right(Vadd(operands(0), operands(1), operands(2), operands.lift(3), modifiers.drop(0), predication))
        else if (true) Left(InstructionDecodeError.InvalidOperandCount("vadd", Vector(3, 4), operands.size))
        else Left(InstructionDecodeError.UnknownOpcode((name +: modifierNames).mkString(".")))
      case "vmax" =>
        val modifierNames = modifiers.map(_.name)
        if (true && (operands.size == 3 || operands.size == 4)) Right(Vmax(operands(0), operands(1), operands(2), operands.lift(3), modifiers.drop(0), predication))
        else if (true) Left(InstructionDecodeError.InvalidOperandCount("vmax", Vector(3, 4), operands.size))
        else Left(InstructionDecodeError.UnknownOpcode((name +: modifierNames).mkString(".")))
      case "vmin" =>
        val modifierNames = modifiers.map(_.name)
        if (true && (operands.size == 3 || operands.size == 4)) Right(Vmin(operands(0), operands(1), operands(2), operands.lift(3), modifiers.drop(0), predication))
        else if (true) Left(InstructionDecodeError.InvalidOperandCount("vmin", Vector(3, 4), operands.size))
        else Left(InstructionDecodeError.UnknownOpcode((name +: modifierNames).mkString(".")))
      case "vote" =>
        val modifierNames = modifiers.map(_.name)
        if (modifierNames.startsWith(Vector("sync")) && (operands.size == 3)) Right(VoteSync(operands(0), operands(1), operands(2), modifiers.drop(1), predication))
        else if (modifierNames.startsWith(Vector("sync"))) Left(InstructionDecodeError.InvalidOperandCount("vote.sync", Vector(3), operands.size))
        else if (true && (operands.size == 2)) Right(Vote(operands(0), operands(1), modifiers.drop(0), predication))
        else if (true) Left(InstructionDecodeError.InvalidOperandCount("vote", Vector(2), operands.size))
        else Left(InstructionDecodeError.UnknownOpcode((name +: modifierNames).mkString(".")))
      case "vset4" =>
        val modifierNames = modifiers.map(_.name)
        if (true && (operands.size == 4)) Right(Vset4(operands(0), operands(1), operands(2), operands(3), modifiers.drop(0), predication))
        else if (true) Left(InstructionDecodeError.InvalidOperandCount("vset4", Vector(4), operands.size))
        else Left(InstructionDecodeError.UnknownOpcode((name +: modifierNames).mkString(".")))
      case "vshl" =>
        val modifierNames = modifiers.map(_.name)
        if (true && (operands.size == 3 || operands.size == 4)) Right(Vshl(operands(0), operands(1), operands(2), operands.lift(3), modifiers.drop(0), predication))
        else if (true) Left(InstructionDecodeError.InvalidOperandCount("vshl", Vector(3, 4), operands.size))
        else Left(InstructionDecodeError.UnknownOpcode((name +: modifierNames).mkString(".")))
      case _ => Left(InstructionDecodeError.UnknownOpcode(name))
    }

  private def decode1(
      name: String,
      operands: Vector[Operand],
      modifiers: Vector[Modifier],
      predication: Option[Predication]
  ): Either[InstructionDecodeError, Instruction] =
    name match {
      case "abs" =>
        val modifierNames = modifiers.map(_.name)
        if (true && (operands.size == 2)) Right(Abs(operands(0), operands(1), modifiers.drop(0), predication))
        else if (true) Left(InstructionDecodeError.InvalidOperandCount("abs", Vector(2), operands.size))
        else Left(InstructionDecodeError.UnknownOpcode((name +: modifierNames).mkString(".")))
      case "and" =>
        val modifierNames = modifiers.map(_.name)
        if (true && (operands.size == 3)) Right(And(operands(0), operands(1), operands(2), modifiers.drop(0), predication))
        else if (true) Left(InstructionDecodeError.InvalidOperandCount("and", Vector(3), operands.size))
        else Left(InstructionDecodeError.UnknownOpcode((name +: modifierNames).mkString(".")))
      case "bfe" =>
        val modifierNames = modifiers.map(_.name)
        if (true && (operands.size == 4)) Right(Bfe(operands(0), operands(1), operands(2), operands(3), modifiers.drop(0), predication))
        else if (true) Left(InstructionDecodeError.InvalidOperandCount("bfe", Vector(4), operands.size))
        else Left(InstructionDecodeError.UnknownOpcode((name +: modifierNames).mkString(".")))
      case "clmad" =>
        val modifierNames = modifiers.map(_.name)
        if (true && (operands.size == 4)) Right(Clmad(operands(0), operands(1), operands(2), operands(3), modifiers.drop(0), predication))
        else if (true) Left(InstructionDecodeError.InvalidOperandCount("clmad", Vector(4), operands.size))
        else Left(InstructionDecodeError.UnknownOpcode((name +: modifierNames).mkString(".")))
      case "cvta" =>
        val modifierNames = modifiers.map(_.name)
        if (true && (operands.size == 2)) Right(Cvta(operands(0), operands(1), modifiers.drop(0), predication))
        else if (true) Left(InstructionDecodeError.InvalidOperandCount("cvta", Vector(2), operands.size))
        else Left(InstructionDecodeError.UnknownOpcode((name +: modifierNames).mkString(".")))
      case "elect" =>
        val modifierNames = modifiers.map(_.name)
        if (modifierNames.startsWith(Vector("sync")) && (operands.size == 2)) Right(ElectSync(operands(0), operands(1), modifiers.drop(1), predication))
        else if (modifierNames.startsWith(Vector("sync"))) Left(InstructionDecodeError.InvalidOperandCount("elect.sync", Vector(2), operands.size))
        else Left(InstructionDecodeError.UnknownOpcode((name +: modifierNames).mkString(".")))
      case "fence" =>
        val modifierNames = modifiers.map(_.name)
        if (true && (operands.size == 0 || operands.size == 2)) Right(Fence(operands.lift(0), operands.lift(1), modifiers.drop(0), predication))
        else if (true) Left(InstructionDecodeError.InvalidOperandCount("fence", Vector(0, 2), operands.size))
        else Left(InstructionDecodeError.UnknownOpcode((name +: modifierNames).mkString(".")))
      case "lg2" =>
        val modifierNames = modifiers.map(_.name)
        if (true && (operands.size == 2)) Right(Lg2(operands(0), operands(1), modifiers.drop(0), predication))
        else if (true) Left(InstructionDecodeError.InvalidOperandCount("lg2", Vector(2), operands.size))
        else Left(InstructionDecodeError.UnknownOpcode((name +: modifierNames).mkString(".")))
      case "madc" =>
        val modifierNames = modifiers.map(_.name)
        if (true && (operands.size == 4)) Right(Madc(operands(0), operands(1), operands(2), operands(3), modifiers.drop(0), predication))
        else if (true) Left(InstructionDecodeError.InvalidOperandCount("madc", Vector(4), operands.size))
        else Left(InstructionDecodeError.UnknownOpcode((name +: modifierNames).mkString(".")))
      case "red" =>
        val modifierNames = modifiers.map(_.name)
        if (modifierNames.startsWith(Vector("async")) && (operands.size == 2 || operands.size == 3)) Right(RedAsync(operands(0), operands(1), operands.lift(2), modifiers.drop(1), predication))
        else if (modifierNames.startsWith(Vector("async"))) Left(InstructionDecodeError.InvalidOperandCount("red.async", Vector(2, 3), operands.size))
        else if (true && (operands.size == 2 || operands.size == 3)) Right(Red(operands(0), operands(1), operands.lift(2), modifiers.drop(0), predication))
        else if (true) Left(InstructionDecodeError.InvalidOperandCount("red", Vector(2, 3), operands.size))
        else Left(InstructionDecodeError.UnknownOpcode((name +: modifierNames).mkString(".")))
      case "setp" =>
        val modifierNames = modifiers.map(_.name)
        if (true && (operands.size == 3 || operands.size == 4)) Right(Setp(operands(0), operands(1), operands(2), operands.lift(3), modifiers.drop(0), predication))
        else if (true) Left(InstructionDecodeError.InvalidOperandCount("setp", Vector(3, 4), operands.size))
        else Left(InstructionDecodeError.UnknownOpcode((name +: modifierNames).mkString(".")))
      case "subc" =>
        val modifierNames = modifiers.map(_.name)
        if (true && (operands.size == 3)) Right(Subc(operands(0), operands(1), operands(2), modifiers.drop(0), predication))
        else if (true) Left(InstructionDecodeError.InvalidOperandCount("subc", Vector(3), operands.size))
        else Left(InstructionDecodeError.UnknownOpcode((name +: modifierNames).mkString(".")))
      case "suq" =>
        val modifierNames = modifiers.map(_.name)
        if (true && (operands.size == 2)) Right(Suq(operands(0), operands(1), modifiers.drop(0), predication))
        else if (true) Left(InstructionDecodeError.InvalidOperandCount("suq", Vector(2), operands.size))
        else Left(InstructionDecodeError.UnknownOpcode((name +: modifierNames).mkString(".")))
      case "sured" =>
        val modifierNames = modifiers.map(_.name)
        if (true && (operands.size == 2)) Right(Sured(operands(0), operands(1), modifiers.drop(0), predication))
        else if (true) Left(InstructionDecodeError.InvalidOperandCount("sured", Vector(2), operands.size))
        else Left(InstructionDecodeError.UnknownOpcode((name +: modifierNames).mkString(".")))
      case "szext" =>
        val modifierNames = modifiers.map(_.name)
        if (true && (operands.size == 3)) Right(Szext(operands(0), operands(1), operands(2), modifiers.drop(0), predication))
        else if (true) Left(InstructionDecodeError.InvalidOperandCount("szext", Vector(3), operands.size))
        else Left(InstructionDecodeError.UnknownOpcode((name +: modifierNames).mkString(".")))
      case "tanh" =>
        val modifierNames = modifiers.map(_.name)
        if (true && (operands.size == 2)) Right(Tanh(operands(0), operands(1), modifiers.drop(0), predication))
        else if (true) Left(InstructionDecodeError.InvalidOperandCount("tanh", Vector(2), operands.size))
        else Left(InstructionDecodeError.UnknownOpcode((name +: modifierNames).mkString(".")))
      case "vabsdiff" =>
        val modifierNames = modifiers.map(_.name)
        if (true && (operands.size == 3 || operands.size == 4)) Right(Vabsdiff(operands(0), operands(1), operands(2), operands.lift(3), modifiers.drop(0), predication))
        else if (true) Left(InstructionDecodeError.InvalidOperandCount("vabsdiff", Vector(3, 4), operands.size))
        else Left(InstructionDecodeError.UnknownOpcode((name +: modifierNames).mkString(".")))
      case "vadd2" =>
        val modifierNames = modifiers.map(_.name)
        if (true && (operands.size == 4)) Right(Vadd2(operands(0), operands(1), operands(2), operands(3), modifiers.drop(0), predication))
        else if (true) Left(InstructionDecodeError.InvalidOperandCount("vadd2", Vector(4), operands.size))
        else Left(InstructionDecodeError.UnknownOpcode((name +: modifierNames).mkString(".")))
      case "vavrg2" =>
        val modifierNames = modifiers.map(_.name)
        if (true && (operands.size == 4)) Right(Vavrg2(operands(0), operands(1), operands(2), operands(3), modifiers.drop(0), predication))
        else if (true) Left(InstructionDecodeError.InvalidOperandCount("vavrg2", Vector(4), operands.size))
        else Left(InstructionDecodeError.UnknownOpcode((name +: modifierNames).mkString(".")))
      case "vmad" =>
        val modifierNames = modifiers.map(_.name)
        if (true && (operands.size == 4)) Right(Vmad(operands(0), operands(1), operands(2), operands(3), modifiers.drop(0), predication))
        else if (true) Left(InstructionDecodeError.InvalidOperandCount("vmad", Vector(4), operands.size))
        else Left(InstructionDecodeError.UnknownOpcode((name +: modifierNames).mkString(".")))
      case "vmax2" =>
        val modifierNames = modifiers.map(_.name)
        if (true && (operands.size == 4)) Right(Vmax2(operands(0), operands(1), operands(2), operands(3), modifiers.drop(0), predication))
        else if (true) Left(InstructionDecodeError.InvalidOperandCount("vmax2", Vector(4), operands.size))
        else Left(InstructionDecodeError.UnknownOpcode((name +: modifierNames).mkString(".")))
      case "vmin2" =>
        val modifierNames = modifiers.map(_.name)
        if (true && (operands.size == 4)) Right(Vmin2(operands(0), operands(1), operands(2), operands(3), modifiers.drop(0), predication))
        else if (true) Left(InstructionDecodeError.InvalidOperandCount("vmin2", Vector(4), operands.size))
        else Left(InstructionDecodeError.UnknownOpcode((name +: modifierNames).mkString(".")))
      case "vsub" =>
        val modifierNames = modifiers.map(_.name)
        if (true && (operands.size == 3 || operands.size == 4)) Right(Vsub(operands(0), operands(1), operands(2), operands.lift(3), modifiers.drop(0), predication))
        else if (true) Left(InstructionDecodeError.InvalidOperandCount("vsub", Vector(3, 4), operands.size))
        else Left(InstructionDecodeError.UnknownOpcode((name +: modifierNames).mkString(".")))
      case "xor" =>
        val modifierNames = modifiers.map(_.name)
        if (true && (operands.size == 3)) Right(Xor(operands(0), operands(1), operands(2), modifiers.drop(0), predication))
        else if (true) Left(InstructionDecodeError.InvalidOperandCount("xor", Vector(3), operands.size))
        else Left(InstructionDecodeError.UnknownOpcode((name +: modifierNames).mkString(".")))
      case _ => Left(InstructionDecodeError.UnknownOpcode(name))
    }

  private def decode2(
      name: String,
      operands: Vector[Operand],
      modifiers: Vector[Modifier],
      predication: Option[Predication]
  ): Either[InstructionDecodeError, Instruction] =
    name match {
      case "call" =>
        val modifierNames = modifiers.map(_.name)
        if (true && (operands.size == 1 || operands.size == 2 || operands.size == 3 || operands.size == 4)) Right(Call(operands(0), operands.lift(1), operands.lift(2), operands.lift(3), modifiers.drop(0), predication))
        else if (true) Left(InstructionDecodeError.InvalidOperandCount("call", Vector(1, 2, 3, 4), operands.size))
        else Left(InstructionDecodeError.UnknownOpcode((name +: modifierNames).mkString(".")))
      case "cos" =>
        val modifierNames = modifiers.map(_.name)
        if (true && (operands.size == 2)) Right(Cos(operands(0), operands(1), modifiers.drop(0), predication))
        else if (true) Left(InstructionDecodeError.InvalidOperandCount("cos", Vector(2), operands.size))
        else Left(InstructionDecodeError.UnknownOpcode((name +: modifierNames).mkString(".")))
      case "membar" =>
        val modifierNames = modifiers.map(_.name)
        if (true && (operands.size == 0)) Right(Membar(modifiers.drop(0), predication))
        else if (true) Left(InstructionDecodeError.InvalidOperandCount("membar", Vector(0), operands.size))
        else Left(InstructionDecodeError.UnknownOpcode((name +: modifierNames).mkString(".")))
      case "mul24" =>
        val modifierNames = modifiers.map(_.name)
        if (true && (operands.size == 3)) Right(Mul24(operands(0), operands(1), operands(2), modifiers.drop(0), predication))
        else if (true) Left(InstructionDecodeError.InvalidOperandCount("mul24", Vector(3), operands.size))
        else Left(InstructionDecodeError.UnknownOpcode((name +: modifierNames).mkString(".")))
      case "setmaxnreg" =>
        val modifierNames = modifiers.map(_.name)
        if (true && (operands.size == 1)) Right(SetMaxRegisters(operands(0), modifiers.drop(0), predication))
        else if (true) Left(InstructionDecodeError.InvalidOperandCount("setmaxnreg", Vector(1), operands.size))
        else Left(InstructionDecodeError.UnknownOpcode((name +: modifierNames).mkString(".")))
      case "sqrt" =>
        val modifierNames = modifiers.map(_.name)
        if (true && (operands.size == 2)) Right(Sqrt(operands(0), operands(1), modifiers.drop(0), predication))
        else if (true) Left(InstructionDecodeError.InvalidOperandCount("sqrt", Vector(2), operands.size))
        else Left(InstructionDecodeError.UnknownOpcode((name +: modifierNames).mkString(".")))
      case _ => Left(InstructionDecodeError.UnknownOpcode(name))
    }

  private def decode3(
      name: String,
      operands: Vector[Operand],
      modifiers: Vector[Modifier],
      predication: Option[Predication]
  ): Either[InstructionDecodeError, Instruction] =
    name match {
      case "atom" =>
        val modifierNames = modifiers.map(_.name)
        if (true && (operands.size == 3 || operands.size == 4)) Right(Atom(operands(0), operands(1), operands(2), operands.lift(3), modifiers.drop(0), predication))
        else if (true) Left(InstructionDecodeError.InvalidOperandCount("atom", Vector(3, 4), operands.size))
        else Left(InstructionDecodeError.UnknownOpcode((name +: modifierNames).mkString(".")))
      case "cnot" =>
        val modifierNames = modifiers.map(_.name)
        if (true && (operands.size == 2)) Right(Cnot(operands(0), operands(1), modifiers.drop(0), predication))
        else if (true) Left(InstructionDecodeError.InvalidOperandCount("cnot", Vector(2), operands.size))
        else Left(InstructionDecodeError.UnknownOpcode((name +: modifierNames).mkString(".")))
      case "cp" =>
        val modifierNames = modifiers.map(_.name)
        if (modifierNames.startsWith(Vector("async", "bulk", "prefetch", "tensor")) && (operands.size == 1 || operands.size == 2 || operands.size == 3)) Right(CpAsyncBulkPrefetchTensor(operands(0), operands.lift(1), operands.lift(2), modifiers.drop(4), predication))
        else if (modifierNames.startsWith(Vector("async", "bulk", "prefetch", "tensor"))) Left(InstructionDecodeError.InvalidOperandCount("cp.async.bulk.prefetch.tensor", Vector(1, 2, 3), operands.size))
        else if (modifierNames.startsWith(Vector("reduce", "async", "bulk", "tensor")) && (operands.size == 2 || operands.size == 3)) Right(CpReduceAsyncBulkTensor(operands(0), operands(1), operands.lift(2), modifiers.drop(4), predication))
        else if (modifierNames.startsWith(Vector("reduce", "async", "bulk", "tensor"))) Left(InstructionDecodeError.InvalidOperandCount("cp.reduce.async.bulk.tensor", Vector(2, 3), operands.size))
        else if (modifierNames.startsWith(Vector("async", "bulk", "commit_group")) && (operands.size == 0)) Right(CpAsyncBulkCommitGroup(modifiers.drop(3), predication))
        else if (modifierNames.startsWith(Vector("async", "bulk", "commit_group"))) Left(InstructionDecodeError.InvalidOperandCount("cp.async.bulk.commit_group", Vector(0), operands.size))
        else if (modifierNames.startsWith(Vector("async", "bulk", "prefetch")) && (operands.size == 2 || operands.size == 3)) Right(CpAsyncBulkPrefetch(operands(0), operands(1), operands.lift(2), modifiers.drop(3), predication))
        else if (modifierNames.startsWith(Vector("async", "bulk", "prefetch"))) Left(InstructionDecodeError.InvalidOperandCount("cp.async.bulk.prefetch", Vector(2, 3), operands.size))
        else if ((modifierNames.startsWith(Vector("async", "bulk", "tensor"))) && (!modifierNames.contains("bulk_group")) && (if (modifierNames.exists(_.startsWith("im2col")) && modifierNames.contains("multicast::cluster")) operands.size >= 5 && operands.size <= 6 else if (modifierNames.exists(_.startsWith("im2col")) || modifierNames.contains("multicast::cluster")) operands.size >= 4 && operands.size <= 5 else operands.size == 3 || operands.size == 4)) Right(if (modifierNames.exists(_.startsWith("im2col")) && modifierNames.contains("multicast::cluster")) CpAsyncBulkTensorMBarrier(operands(0), operands(1), operands(2), Some(operands(3)), operands.lift(4), operands.lift(5), modifiers.drop(3), predication) else if (modifierNames.exists(_.startsWith("im2col"))) CpAsyncBulkTensorMBarrier(operands(0), operands(1), operands(2), Some(operands(3)), None, operands.lift(4), modifiers.drop(3), predication) else if (modifierNames.contains("multicast::cluster")) CpAsyncBulkTensorMBarrier(operands(0), operands(1), operands(2), None, operands.lift(3), operands.lift(4), modifiers.drop(3), predication) else CpAsyncBulkTensorMBarrier(operands(0), operands(1), operands(2), None, None, operands.lift(3), modifiers.drop(3), predication))
        else if ((modifierNames.startsWith(Vector("async", "bulk", "tensor"))) && (!modifierNames.contains("bulk_group"))) Left(InstructionDecodeError.InvalidOperandCount("cp.async.bulk.tensor", Vector(3, 4, 5, 6), operands.size))
        else if ((modifierNames.startsWith(Vector("async", "bulk", "tensor"))) && (modifierNames.contains("bulk_group")) && (operands.size == 2 || operands.size == 3)) Right(CpAsyncBulkTensorGroup(operands(0), operands(1), operands.lift(2), modifiers.drop(3), predication))
        else if ((modifierNames.startsWith(Vector("async", "bulk", "tensor"))) && (modifierNames.contains("bulk_group"))) Left(InstructionDecodeError.InvalidOperandCount("cp.async.bulk.tensor", Vector(2, 3), operands.size))
        else if (modifierNames.startsWith(Vector("async", "bulk", "wait_group")) && (operands.size == 1)) Right(CpAsyncBulkWaitGroup(operands(0), modifiers.drop(3), predication))
        else if (modifierNames.startsWith(Vector("async", "bulk", "wait_group"))) Left(InstructionDecodeError.InvalidOperandCount("cp.async.bulk.wait_group", Vector(1), operands.size))
        else if (modifierNames.startsWith(Vector("async", "mbarrier", "arrive")) && (operands.size == 1)) Right(CpAsyncMbarrierArrive(operands(0), modifiers.drop(3), predication))
        else if (modifierNames.startsWith(Vector("async", "mbarrier", "arrive"))) Left(InstructionDecodeError.InvalidOperandCount("cp.async.mbarrier.arrive", Vector(1), operands.size))
        else if (modifierNames.startsWith(Vector("reduce", "async", "bulk")) && (operands.size == 3 || operands.size == 4)) Right(CpReduceAsyncBulk(operands(0), operands(1), operands(2), operands.lift(3), modifiers.drop(3), predication))
        else if (modifierNames.startsWith(Vector("reduce", "async", "bulk"))) Left(InstructionDecodeError.InvalidOperandCount("cp.reduce.async.bulk", Vector(3, 4), operands.size))
        else if ((modifierNames.startsWith(Vector("async", "bulk"))) && (!modifierNames.contains("bulk_group")) && (if (modifierNames.contains("ignore_oob")) operands.size == 6 || operands.size == 7 else if (modifierNames.contains("multicast::cluster")) operands.size >= 4 && operands.size <= 6 else operands.size == 4 || operands.size == 5)) Right(if (modifierNames.contains("ignore_oob")) CpAsyncBulkMBarrier(operands(0), operands(1), operands(2), operands(5), Some(operands(3)), Some(operands(4)), None, operands.lift(6), modifiers.drop(2), predication) else if (modifierNames.contains("multicast::cluster")) CpAsyncBulkMBarrier(operands(0), operands(1), operands(2), operands(3), None, None, operands.lift(4), operands.lift(5), modifiers.drop(2), predication) else CpAsyncBulkMBarrier(operands(0), operands(1), operands(2), operands(3), None, None, None, operands.lift(4), modifiers.drop(2), predication))
        else if ((modifierNames.startsWith(Vector("async", "bulk"))) && (!modifierNames.contains("bulk_group"))) Left(InstructionDecodeError.InvalidOperandCount("cp.async.bulk", Vector(4, 5, 6, 7), operands.size))
        else if ((modifierNames.startsWith(Vector("async", "bulk"))) && (modifierNames.contains("bulk_group")) && (operands.size == 3 || operands.size == 4 || operands.size == 5)) Right(if (operands.size == 3) CpAsyncBulkGroup(operands(0), operands(1), operands(2), None, None, modifiers.drop(2), predication) else if (operands.size == 4 && modifierNames.contains("cp_mask")) CpAsyncBulkGroup(operands(0), operands(1), operands(2), None, Some(operands(3)), modifiers.drop(2), predication) else CpAsyncBulkGroup(operands(0), operands(1), operands(2), Some(operands(3)), operands.lift(4), modifiers.drop(2), predication))
        else if ((modifierNames.startsWith(Vector("async", "bulk"))) && (modifierNames.contains("bulk_group"))) Left(InstructionDecodeError.InvalidOperandCount("cp.async.bulk", Vector(3, 4, 5), operands.size))
        else if (modifierNames.startsWith(Vector("async", "commit_group")) && (operands.size == 0)) Right(CpAsyncCommitGroup(modifiers.drop(2), predication))
        else if (modifierNames.startsWith(Vector("async", "commit_group"))) Left(InstructionDecodeError.InvalidOperandCount("cp.async.commit_group", Vector(0), operands.size))
        else if (modifierNames.startsWith(Vector("async", "wait_all")) && (operands.size == 0)) Right(CpAsyncWaitAll(modifiers.drop(2), predication))
        else if (modifierNames.startsWith(Vector("async", "wait_all"))) Left(InstructionDecodeError.InvalidOperandCount("cp.async.wait_all", Vector(0), operands.size))
        else if (modifierNames.startsWith(Vector("async", "wait_group")) && (operands.size == 1)) Right(CpAsyncWaitGroup(operands(0), modifiers.drop(2), predication))
        else if (modifierNames.startsWith(Vector("async", "wait_group"))) Left(InstructionDecodeError.InvalidOperandCount("cp.async.wait_group", Vector(1), operands.size))
        else if (modifierNames.startsWith(Vector("async")) && (operands.size == 3 || operands.size == 4 || operands.size == 5)) Right(CpAsync(operands(0), operands(1), operands(2), operands.lift(3), operands.lift(4), modifiers.drop(1), predication))
        else if (modifierNames.startsWith(Vector("async"))) Left(InstructionDecodeError.InvalidOperandCount("cp.async", Vector(3, 4, 5), operands.size))
        else Left(InstructionDecodeError.UnknownOpcode((name +: modifierNames).mkString(".")))
      case "createpolicy" =>
        val modifierNames = modifiers.map(_.name)
        if (true && (operands.size == 1 || operands.size == 2 || operands.size == 4)) Right(CreatePolicy(operands(0), operands.lift(1), operands.lift(2), operands.lift(3), modifiers.drop(0), predication))
        else if (true) Left(InstructionDecodeError.InvalidOperandCount("createpolicy", Vector(1, 2, 4), operands.size))
        else Left(InstructionDecodeError.UnknownOpcode((name +: modifierNames).mkString(".")))
      case "cvt" =>
        val modifierNames = modifiers.map(_.name)
        if (modifierNames.startsWith(Vector("pack")) && (operands.size == 3 || operands.size == 4)) Right(CvtPack(operands(0), operands(1), operands(2), operands.lift(3), modifiers.drop(1), predication))
        else if (modifierNames.startsWith(Vector("pack"))) Left(InstructionDecodeError.InvalidOperandCount("cvt.pack", Vector(3, 4), operands.size))
        else if (true && (operands.size == 2 || operands.size == 3 || operands.size == 4)) Right(Cvt(operands(0), operands(1), operands.lift(2), operands.lift(3), modifiers.drop(0), predication))
        else if (true) Left(InstructionDecodeError.InvalidOperandCount("cvt", Vector(2, 3, 4), operands.size))
        else Left(InstructionDecodeError.UnknownOpcode((name +: modifierNames).mkString(".")))
      case "div" =>
        val modifierNames = modifiers.map(_.name)
        if (true && (operands.size == 3)) Right(Div(operands(0), operands(1), operands(2), modifiers.drop(0), predication))
        else if (true) Left(InstructionDecodeError.InvalidOperandCount("div", Vector(3), operands.size))
        else Left(InstructionDecodeError.UnknownOpcode((name +: modifierNames).mkString(".")))
      case "ex2" =>
        val modifierNames = modifiers.map(_.name)
        if (true && (operands.size == 2)) Right(Ex2(operands(0), operands(1), modifiers.drop(0), predication))
        else if (true) Left(InstructionDecodeError.InvalidOperandCount("ex2", Vector(2), operands.size))
        else Left(InstructionDecodeError.UnknownOpcode((name +: modifierNames).mkString(".")))
      case "exit" =>
        val modifierNames = modifiers.map(_.name)
        if (true && (operands.size == 0)) Right(Exit(modifiers.drop(0), predication))
        else if (true) Left(InstructionDecodeError.InvalidOperandCount("exit", Vector(0), operands.size))
        else Left(InstructionDecodeError.UnknownOpcode((name +: modifierNames).mkString(".")))
      case "fabric" =>
        val modifierNames = modifiers.map(_.name)
        if (modifierNames.startsWith(Vector("submit")) && (operands.size == 0)) Right(FabricSubmit(modifiers.drop(1), predication))
        else if (modifierNames.startsWith(Vector("submit"))) Left(InstructionDecodeError.InvalidOperandCount("fabric.submit", Vector(0), operands.size))
        else if (modifierNames.startsWith(Vector("try_get")) && (operands.size == 4)) Right(FabricTryGet(operands(0), operands(1), operands(2), operands(3), modifiers.drop(1), predication))
        else if (modifierNames.startsWith(Vector("try_get"))) Left(InstructionDecodeError.InvalidOperandCount("fabric.try_get", Vector(4), operands.size))
        else if (modifierNames.startsWith(Vector("try_pullred")) && (operands.size == 5)) Right(FabricTryPullred(operands(0), operands(1), operands(2), operands(3), operands(4), modifiers.drop(1), predication))
        else if (modifierNames.startsWith(Vector("try_pullred"))) Left(InstructionDecodeError.InvalidOperandCount("fabric.try_pullred", Vector(5), operands.size))
        else if (modifierNames.startsWith(Vector("try_put")) && (operands.size == 4 || operands.size == 5)) Right(FabricTryPut(operands(0), operands(1), operands(2), operands(3), operands.lift(4), modifiers.drop(1), predication))
        else if (modifierNames.startsWith(Vector("try_put"))) Left(InstructionDecodeError.InvalidOperandCount("fabric.try_put", Vector(4, 5), operands.size))
        else if (modifierNames.startsWith(Vector("try_red")) && (operands.size == 4)) Right(FabricTryRed(operands(0), operands(1), operands(2), operands(3), modifiers.drop(1), predication))
        else if (modifierNames.startsWith(Vector("try_red"))) Left(InstructionDecodeError.InvalidOperandCount("fabric.try_red", Vector(4), operands.size))
        else if (modifierNames.startsWith(Vector("wait")) && (operands.size == 0)) Right(FabricWait(modifiers.drop(1), predication))
        else if (modifierNames.startsWith(Vector("wait"))) Left(InstructionDecodeError.InvalidOperandCount("fabric.wait", Vector(0), operands.size))
        else Left(InstructionDecodeError.UnknownOpcode((name +: modifierNames).mkString(".")))
      case "griddepcontrol" =>
        val modifierNames = modifiers.map(_.name)
        if (true && (operands.size == 0)) Right(GridDependencyControl(modifiers.drop(0), predication))
        else if (true) Left(InstructionDecodeError.InvalidOperandCount("griddepcontrol", Vector(0), operands.size))
        else Left(InstructionDecodeError.UnknownOpcode((name +: modifierNames).mkString(".")))
      case "ldu" =>
        val modifierNames = modifiers.map(_.name)
        if (true && (operands.size == 2)) Right(Ldu(operands(0), operands(1), modifiers.drop(0), predication))
        else if (true) Left(InstructionDecodeError.InvalidOperandCount("ldu", Vector(2), operands.size))
        else Left(InstructionDecodeError.UnknownOpcode((name +: modifierNames).mkString(".")))
      case "neg" =>
        val modifierNames = modifiers.map(_.name)
        if (true && (operands.size == 2)) Right(Neg(operands(0), operands(1), modifiers.drop(0), predication))
        else if (true) Left(InstructionDecodeError.InvalidOperandCount("neg", Vector(2), operands.size))
        else Left(InstructionDecodeError.UnknownOpcode((name +: modifierNames).mkString(".")))
      case "rem" =>
        val modifierNames = modifiers.map(_.name)
        if (true && (operands.size == 3)) Right(Rem(operands(0), operands(1), operands(2), modifiers.drop(0), predication))
        else if (true) Left(InstructionDecodeError.InvalidOperandCount("rem", Vector(3), operands.size))
        else Left(InstructionDecodeError.UnknownOpcode((name +: modifierNames).mkString(".")))
      case "ret" =>
        val modifierNames = modifiers.map(_.name)
        if (true && (operands.size == 0)) Right(Ret(modifiers.drop(0), predication))
        else if (true) Left(InstructionDecodeError.InvalidOperandCount("ret", Vector(0), operands.size))
        else Left(InstructionDecodeError.UnknownOpcode((name +: modifierNames).mkString(".")))
      case "rsqrt" =>
        val modifierNames = modifiers.map(_.name)
        if (modifierNames.startsWith(Vector("approx", "ftz", "f64")) && (operands.size == 2)) Right(RsqrtApproxFtzF64(operands(0), operands(1), modifiers.drop(3), predication))
        else if (modifierNames.startsWith(Vector("approx", "ftz", "f64"))) Left(InstructionDecodeError.InvalidOperandCount("rsqrt.approx.ftz.f64", Vector(2), operands.size))
        else if (true && (operands.size == 2)) Right(Rsqrt(operands(0), operands(1), modifiers.drop(0), predication))
        else if (true) Left(InstructionDecodeError.InvalidOperandCount("rsqrt", Vector(2), operands.size))
        else Left(InstructionDecodeError.UnknownOpcode((name +: modifierNames).mkString(".")))
      case "shfl" =>
        val modifierNames = modifiers.map(_.name)
        if (modifierNames.startsWith(Vector("sync")) && (operands.size == 5)) Right(ShflSync(operands(0), operands(1), operands(2), operands(3), operands(4), modifiers.drop(1), predication))
        else if (modifierNames.startsWith(Vector("sync"))) Left(InstructionDecodeError.InvalidOperandCount("shfl.sync", Vector(5), operands.size))
        else if (true && (operands.size == 4)) Right(Shfl(operands(0), operands(1), operands(2), operands(3), modifiers.drop(0), predication))
        else if (true) Left(InstructionDecodeError.InvalidOperandCount("shfl", Vector(4), operands.size))
        else Left(InstructionDecodeError.UnknownOpcode((name +: modifierNames).mkString(".")))
      case "slct" =>
        val modifierNames = modifiers.map(_.name)
        if (true && (operands.size == 4)) Right(Slct(operands(0), operands(1), operands(2), operands(3), modifiers.drop(0), predication))
        else if (true) Left(InstructionDecodeError.InvalidOperandCount("slct", Vector(4), operands.size))
        else Left(InstructionDecodeError.UnknownOpcode((name +: modifierNames).mkString(".")))
      case "testp" =>
        val modifierNames = modifiers.map(_.name)
        if (true && (operands.size == 2)) Right(TestPredicate(operands(0), operands(1), modifiers.drop(0), predication))
        else if (true) Left(InstructionDecodeError.InvalidOperandCount("testp", Vector(2), operands.size))
        else Left(InstructionDecodeError.UnknownOpcode((name +: modifierNames).mkString(".")))
      case "vabsdiff2" =>
        val modifierNames = modifiers.map(_.name)
        if (true && (operands.size == 4)) Right(Vabsdiff2(operands(0), operands(1), operands(2), operands(3), modifiers.drop(0), predication))
        else if (true) Left(InstructionDecodeError.InvalidOperandCount("vabsdiff2", Vector(4), operands.size))
        else Left(InstructionDecodeError.UnknownOpcode((name +: modifierNames).mkString(".")))
      case "vadd4" =>
        val modifierNames = modifiers.map(_.name)
        if (true && (operands.size == 4)) Right(Vadd4(operands(0), operands(1), operands(2), operands(3), modifiers.drop(0), predication))
        else if (true) Left(InstructionDecodeError.InvalidOperandCount("vadd4", Vector(4), operands.size))
        else Left(InstructionDecodeError.UnknownOpcode((name +: modifierNames).mkString(".")))
      case "vavrg4" =>
        val modifierNames = modifiers.map(_.name)
        if (true && (operands.size == 4)) Right(Vavrg4(operands(0), operands(1), operands(2), operands(3), modifiers.drop(0), predication))
        else if (true) Left(InstructionDecodeError.InvalidOperandCount("vavrg4", Vector(4), operands.size))
        else Left(InstructionDecodeError.UnknownOpcode((name +: modifierNames).mkString(".")))
      case "vmax4" =>
        val modifierNames = modifiers.map(_.name)
        if (true && (operands.size == 4)) Right(Vmax4(operands(0), operands(1), operands(2), operands(3), modifiers.drop(0), predication))
        else if (true) Left(InstructionDecodeError.InvalidOperandCount("vmax4", Vector(4), operands.size))
        else Left(InstructionDecodeError.UnknownOpcode((name +: modifierNames).mkString(".")))
      case "vmin4" =>
        val modifierNames = modifiers.map(_.name)
        if (true && (operands.size == 4)) Right(Vmin4(operands(0), operands(1), operands(2), operands(3), modifiers.drop(0), predication))
        else if (true) Left(InstructionDecodeError.InvalidOperandCount("vmin4", Vector(4), operands.size))
        else Left(InstructionDecodeError.UnknownOpcode((name +: modifierNames).mkString(".")))
      case _ => Left(InstructionDecodeError.UnknownOpcode(name))
    }

  private def decode4(
      name: String,
      operands: Vector[Operand],
      modifiers: Vector[Modifier],
      predication: Option[Predication]
  ): Either[InstructionDecodeError, Instruction] =
    name match {
      case "ld" =>
        val modifierNames = modifiers.map(_.name)
        if (modifierNames.startsWith(Vector("global", "nc")) && (operands.size == 2 || operands.size == 3)) Right(LdGlobalNc(operands(0), operands(1), operands.lift(2), modifiers.drop(2), predication))
        else if (modifierNames.startsWith(Vector("global", "nc"))) Left(InstructionDecodeError.InvalidOperandCount("ld.global.nc", Vector(2, 3), operands.size))
        else if (true && (operands.size == 2 || operands.size == 3)) Right(Ld(operands(0), operands(1), operands.lift(2), modifiers.drop(0), predication))
        else if (true) Left(InstructionDecodeError.InvalidOperandCount("ld", Vector(2, 3), operands.size))
        else Left(InstructionDecodeError.UnknownOpcode((name +: modifierNames).mkString(".")))
      case "lop3" =>
        val modifierNames = modifiers.map(_.name)
        if (true && (operands.size == 5 || operands.size == 6)) Right(Lop3(operands(0), operands(1), operands(2), operands(3), operands(4), operands.lift(5), modifiers.drop(0), predication))
        else if (true) Left(InstructionDecodeError.InvalidOperandCount("lop3", Vector(5, 6), operands.size))
        else Left(InstructionDecodeError.UnknownOpcode((name +: modifierNames).mkString(".")))
      case "mbarrier" =>
        val modifierNames = modifiers.map(_.name)
        if (modifierNames.startsWith(Vector("arrive")) && (operands.size == 2 || operands.size == 3)) Right(MbarrierArrive(operands(0), operands(1), operands.lift(2), modifiers.drop(1), predication))
        else if (modifierNames.startsWith(Vector("arrive"))) Left(InstructionDecodeError.InvalidOperandCount("mbarrier.arrive", Vector(2, 3), operands.size))
        else if (modifierNames.startsWith(Vector("arrive_drop")) && (operands.size == 2 || operands.size == 3)) Right(MbarrierArriveDrop(operands(0), operands(1), operands.lift(2), modifiers.drop(1), predication))
        else if (modifierNames.startsWith(Vector("arrive_drop"))) Left(InstructionDecodeError.InvalidOperandCount("mbarrier.arrive_drop", Vector(2, 3), operands.size))
        else if (modifierNames.startsWith(Vector("check_layout")) && (operands.size == 2)) Right(MbarrierCheckLayout(operands(0), operands(1), modifiers.drop(1), predication))
        else if (modifierNames.startsWith(Vector("check_layout"))) Left(InstructionDecodeError.InvalidOperandCount("mbarrier.check_layout", Vector(2), operands.size))
        else if (modifierNames.startsWith(Vector("complete_tx")) && (operands.size == 2)) Right(MbarrierCompleteTx(operands(0), operands(1), modifiers.drop(1), predication))
        else if (modifierNames.startsWith(Vector("complete_tx"))) Left(InstructionDecodeError.InvalidOperandCount("mbarrier.complete_tx", Vector(2), operands.size))
        else if (modifierNames.startsWith(Vector("expect_tx")) && (operands.size == 2)) Right(MbarrierExpectTx(operands(0), operands(1), modifiers.drop(1), predication))
        else if (modifierNames.startsWith(Vector("expect_tx"))) Left(InstructionDecodeError.InvalidOperandCount("mbarrier.expect_tx", Vector(2), operands.size))
        else if (modifierNames.startsWith(Vector("init")) && (operands.size == 2)) Right(MbarrierInit(operands(0), operands(1), modifiers.drop(1), predication))
        else if (modifierNames.startsWith(Vector("init"))) Left(InstructionDecodeError.InvalidOperandCount("mbarrier.init", Vector(2), operands.size))
        else if (modifierNames.startsWith(Vector("inval")) && (operands.size == 1)) Right(MbarrierInval(operands(0), modifiers.drop(1), predication))
        else if (modifierNames.startsWith(Vector("inval"))) Left(InstructionDecodeError.InvalidOperandCount("mbarrier.inval", Vector(1), operands.size))
        else if (modifierNames.startsWith(Vector("pending_count")) && (operands.size == 2)) Right(MbarrierPendingCount(operands(0), operands(1), modifiers.drop(1), predication))
        else if (modifierNames.startsWith(Vector("pending_count"))) Left(InstructionDecodeError.InvalidOperandCount("mbarrier.pending_count", Vector(2), operands.size))
        else if (modifierNames.startsWith(Vector("test_wait")) && (operands.size == 3 || operands.size == 4)) Right(if (operands.size == 3) MbarrierTestWait(operands(0), None, operands(1), operands(2), modifiers.drop(1), predication) else MbarrierTestWait(operands(0), Some(operands(1)), operands(2), operands(3), modifiers.drop(1), predication))
        else if (modifierNames.startsWith(Vector("test_wait"))) Left(InstructionDecodeError.InvalidOperandCount("mbarrier.test_wait", Vector(3, 4), operands.size))
        else if (modifierNames.startsWith(Vector("try_wait")) && (operands.size == 3 || operands.size == 4 || operands.size == 5)) Right(if (operands.size == 3) MbarrierTryWait(operands(0), None, operands(1), operands(2), None, modifiers.drop(1), predication) else if (operands.size == 4 && !operands(0).isInstanceOf[Operand.WithPredicate]) MbarrierTryWait(operands(0), None, operands(1), operands(2), Some(operands(3)), modifiers.drop(1), predication) else MbarrierTryWait(operands(0), Some(operands(1)), operands(2), operands(3), operands.lift(4), modifiers.drop(1), predication))
        else if (modifierNames.startsWith(Vector("try_wait"))) Left(InstructionDecodeError.InvalidOperandCount("mbarrier.try_wait", Vector(3, 4, 5), operands.size))
        else Left(InstructionDecodeError.UnknownOpcode((name +: modifierNames).mkString(".")))
      case "mov" =>
        val modifierNames = modifiers.map(_.name)
        if (true && (operands.size == 2)) Right(Mov(operands(0), operands(1), modifiers.drop(0), predication))
        else if (true) Left(InstructionDecodeError.InvalidOperandCount("mov", Vector(2), operands.size))
        else Left(InstructionDecodeError.UnknownOpcode((name +: modifierNames).mkString(".")))
      case "nanosleep" =>
        val modifierNames = modifiers.map(_.name)
        if (true && (operands.size == 1)) Right(NanoSleep(operands(0), modifiers.drop(0), predication))
        else if (true) Left(InstructionDecodeError.InvalidOperandCount("nanosleep", Vector(1), operands.size))
        else Left(InstructionDecodeError.UnknownOpcode((name +: modifierNames).mkString(".")))
      case "not" =>
        val modifierNames = modifiers.map(_.name)
        if (true && (operands.size == 2)) Right(Not(operands(0), operands(1), modifiers.drop(0), predication))
        else if (true) Left(InstructionDecodeError.InvalidOperandCount("not", Vector(2), operands.size))
        else Left(InstructionDecodeError.UnknownOpcode((name +: modifierNames).mkString(".")))
      case "pmevent" =>
        val modifierNames = modifiers.map(_.name)
        if (true && (operands.size == 1)) Right(PmEvent(operands(0), modifiers.drop(0), predication))
        else if (true) Left(InstructionDecodeError.InvalidOperandCount("pmevent", Vector(1), operands.size))
        else Left(InstructionDecodeError.UnknownOpcode((name +: modifierNames).mkString(".")))
      case "redux" =>
        val modifierNames = modifiers.map(_.name)
        if (modifierNames.startsWith(Vector("sync")) && (operands.size == 3)) Right(ReduxSync(operands(0), operands(1), operands(2), modifiers.drop(1), predication))
        else if (modifierNames.startsWith(Vector("sync"))) Left(InstructionDecodeError.InvalidOperandCount("redux.sync", Vector(3), operands.size))
        else Left(InstructionDecodeError.UnknownOpcode((name +: modifierNames).mkString(".")))
      case "sin" =>
        val modifierNames = modifiers.map(_.name)
        if (true && (operands.size == 2)) Right(Sin(operands(0), operands(1), modifiers.drop(0), predication))
        else if (true) Left(InstructionDecodeError.InvalidOperandCount("sin", Vector(2), operands.size))
        else Left(InstructionDecodeError.UnknownOpcode((name +: modifierNames).mkString(".")))
      case "suld" =>
        val modifierNames = modifiers.map(_.name)
        if (true && (operands.size == 2)) Right(Suld(operands(0), operands(1), modifiers.drop(0), predication))
        else if (true) Left(InstructionDecodeError.InvalidOperandCount("suld", Vector(2), operands.size))
        else Left(InstructionDecodeError.UnknownOpcode((name +: modifierNames).mkString(".")))
      case "tex" =>
        val modifierNames = modifiers.map(_.name)
        if (true && (operands.size == 2 || operands.size == 3 || operands.size == 4 || operands.size == 5 || operands.size == 6)) Right(Tex(operands(0), operands(1), operands.lift(2), operands.lift(3), operands.lift(4), operands.lift(5), modifiers.drop(0), predication))
        else if (true) Left(InstructionDecodeError.InvalidOperandCount("tex", Vector(2, 3, 4, 5, 6), operands.size))
        else Left(InstructionDecodeError.UnknownOpcode((name +: modifierNames).mkString(".")))
      case "tld4" =>
        val modifierNames = modifiers.map(_.name)
        if (true && (operands.size == 2 || operands.size == 3 || operands.size == 4)) Right(Tld4(operands(0), operands(1), operands.lift(2), operands.lift(3), modifiers.drop(0), predication))
        else if (true) Left(InstructionDecodeError.InvalidOperandCount("tld4", Vector(2, 3, 4), operands.size))
        else Left(InstructionDecodeError.UnknownOpcode((name +: modifierNames).mkString(".")))
      case "trap" =>
        val modifierNames = modifiers.map(_.name)
        if (true && (operands.size == 0)) Right(Trap(modifiers.drop(0), predication))
        else if (true) Left(InstructionDecodeError.InvalidOperandCount("trap", Vector(0), operands.size))
        else Left(InstructionDecodeError.UnknownOpcode((name +: modifierNames).mkString(".")))
      case "vsub2" =>
        val modifierNames = modifiers.map(_.name)
        if (true && (operands.size == 4)) Right(Vsub2(operands(0), operands(1), operands(2), operands(3), modifiers.drop(0), predication))
        else if (true) Left(InstructionDecodeError.InvalidOperandCount("vsub2", Vector(4), operands.size))
        else Left(InstructionDecodeError.UnknownOpcode((name +: modifierNames).mkString(".")))
      case _ => Left(InstructionDecodeError.UnknownOpcode(name))
    }

  private def decode5(
      name: String,
      operands: Vector[Operand],
      modifiers: Vector[Modifier],
      predication: Option[Predication]
  ): Either[InstructionDecodeError, Instruction] =
    name match {
      case "addc" =>
        val modifierNames = modifiers.map(_.name)
        if (true && (operands.size == 3)) Right(Addc(operands(0), operands(1), operands(2), modifiers.drop(0), predication))
        else if (true) Left(InstructionDecodeError.InvalidOperandCount("addc", Vector(3), operands.size))
        else Left(InstructionDecodeError.UnknownOpcode((name +: modifierNames).mkString(".")))
      case "barrier" =>
        val modifierNames = modifiers.map(_.name)
        if (modifierNames.startsWith(Vector("cluster")) && (operands.size == 0)) Right(BarrierCluster(modifiers.drop(1), predication))
        else if (modifierNames.startsWith(Vector("cluster"))) Left(InstructionDecodeError.InvalidOperandCount("barrier.cluster", Vector(0), operands.size))
        else if (true && (operands.size == 1 || operands.size == 2 || operands.size == 3 || operands.size == 4)) Right(Barrier(operands(0), operands.lift(1), operands.lift(2), operands.lift(3), modifiers.drop(0), predication))
        else if (true) Left(InstructionDecodeError.InvalidOperandCount("barrier", Vector(1, 2, 3, 4), operands.size))
        else Left(InstructionDecodeError.UnknownOpcode((name +: modifierNames).mkString(".")))
      case "bfi" =>
        val modifierNames = modifiers.map(_.name)
        if (true && (operands.size == 5)) Right(Bfi(operands(0), operands(1), operands(2), operands(3), operands(4), modifiers.drop(0), predication))
        else if (true) Left(InstructionDecodeError.InvalidOperandCount("bfi", Vector(5), operands.size))
        else Left(InstructionDecodeError.UnknownOpcode((name +: modifierNames).mkString(".")))
      case "bmsk" =>
        val modifierNames = modifiers.map(_.name)
        if (true && (operands.size == 3)) Right(Bmsk(operands(0), operands(1), operands(2), modifiers.drop(0), predication))
        else if (true) Left(InstructionDecodeError.InvalidOperandCount("bmsk", Vector(3), operands.size))
        else Left(InstructionDecodeError.UnknownOpcode((name +: modifierNames).mkString(".")))
      case "bra" =>
        val modifierNames = modifiers.map(_.name)
        if (true && (operands.size == 1)) Right(Bra(operands(0), modifiers.drop(0), predication))
        else if (true) Left(InstructionDecodeError.InvalidOperandCount("bra", Vector(1), operands.size))
        else Left(InstructionDecodeError.UnknownOpcode((name +: modifierNames).mkString(".")))
      case "brev" =>
        val modifierNames = modifiers.map(_.name)
        if (true && (operands.size == 2)) Right(Brev(operands(0), operands(1), modifiers.drop(0), predication))
        else if (true) Left(InstructionDecodeError.InvalidOperandCount("brev", Vector(2), operands.size))
        else Left(InstructionDecodeError.UnknownOpcode((name +: modifierNames).mkString(".")))
      case "discard" =>
        val modifierNames = modifiers.map(_.name)
        if (true && (operands.size == 2)) Right(Discard(operands(0), operands(1), modifiers.drop(0), predication))
        else if (true) Left(InstructionDecodeError.InvalidOperandCount("discard", Vector(2), operands.size))
        else Left(InstructionDecodeError.UnknownOpcode((name +: modifierNames).mkString(".")))
      case "fma" =>
        val modifierNames = modifiers.map(_.name)
        if (true && (operands.size == 4)) Right(Fma(operands(0), operands(1), operands(2), operands(3), modifiers.drop(0), predication))
        else if (true) Left(InstructionDecodeError.InvalidOperandCount("fma", Vector(4), operands.size))
        else Left(InstructionDecodeError.UnknownOpcode((name +: modifierNames).mkString(".")))
      case "fns" =>
        val modifierNames = modifiers.map(_.name)
        if (true && (operands.size == 4)) Right(Fns(operands(0), operands(1), operands(2), operands(3), modifiers.drop(0), predication))
        else if (true) Left(InstructionDecodeError.InvalidOperandCount("fns", Vector(4), operands.size))
        else Left(InstructionDecodeError.UnknownOpcode((name +: modifierNames).mkString(".")))
      case "getctarank" =>
        val modifierNames = modifiers.map(_.name)
        if (true && (operands.size == 2)) Right(GetCtaRank(operands(0), operands(1), modifiers.drop(0), predication))
        else if (true) Left(InstructionDecodeError.InvalidOperandCount("getctarank", Vector(2), operands.size))
        else Left(InstructionDecodeError.UnknownOpcode((name +: modifierNames).mkString(".")))
      case "mul" =>
        val modifierNames = modifiers.map(_.name)
        if (true && (operands.size == 3)) Right(Mul(operands(0), operands(1), operands(2), modifiers.drop(0), predication))
        else if (true) Left(InstructionDecodeError.InvalidOperandCount("mul", Vector(3), operands.size))
        else Left(InstructionDecodeError.UnknownOpcode((name +: modifierNames).mkString(".")))
      case "multimem" =>
        val modifierNames = modifiers.map(_.name)
        if (modifierNames.startsWith(Vector("cp", "reduce", "async", "bulk")) && (operands.size == 3)) Right(MultimemCpReduceAsyncBulk(operands(0), operands(1), operands(2), modifiers.drop(4), predication))
        else if (modifierNames.startsWith(Vector("cp", "reduce", "async", "bulk"))) Left(InstructionDecodeError.InvalidOperandCount("multimem.cp.reduce.async.bulk", Vector(3), operands.size))
        else if (modifierNames.startsWith(Vector("cp", "async", "bulk")) && (operands.size == 3 || operands.size == 4)) Right(MultimemCpAsyncBulk(operands(0), operands(1), operands(2), operands.lift(3), modifiers.drop(3), predication))
        else if (modifierNames.startsWith(Vector("cp", "async", "bulk"))) Left(InstructionDecodeError.InvalidOperandCount("multimem.cp.async.bulk", Vector(3, 4), operands.size))
        else if (modifierNames.startsWith(Vector("red", "async")) && (operands.size == 2)) Right(MultimemRedAsync(operands(0), operands(1), modifiers.drop(2), predication))
        else if (modifierNames.startsWith(Vector("red", "async"))) Left(InstructionDecodeError.InvalidOperandCount("multimem.red.async", Vector(2), operands.size))
        else if (modifierNames.startsWith(Vector("st", "async")) && (operands.size == 2)) Right(MultimemStAsync(operands(0), operands(1), modifiers.drop(2), predication))
        else if (modifierNames.startsWith(Vector("st", "async"))) Left(InstructionDecodeError.InvalidOperandCount("multimem.st.async", Vector(2), operands.size))
        else if (modifierNames.startsWith(Vector("ld_reduce")) && (operands.size == 2)) Right(MultimemLdReduce(operands(0), operands(1), modifiers.drop(1), predication))
        else if (modifierNames.startsWith(Vector("ld_reduce"))) Left(InstructionDecodeError.InvalidOperandCount("multimem.ld_reduce", Vector(2), operands.size))
        else if (modifierNames.startsWith(Vector("red")) && (operands.size == 2)) Right(MultimemRed(operands(0), operands(1), modifiers.drop(1), predication))
        else if (modifierNames.startsWith(Vector("red"))) Left(InstructionDecodeError.InvalidOperandCount("multimem.red", Vector(2), operands.size))
        else if (modifierNames.startsWith(Vector("st")) && (operands.size == 2)) Right(MultimemSt(operands(0), operands(1), modifiers.drop(1), predication))
        else if (modifierNames.startsWith(Vector("st"))) Left(InstructionDecodeError.InvalidOperandCount("multimem.st", Vector(2), operands.size))
        else Left(InstructionDecodeError.UnknownOpcode((name +: modifierNames).mkString(".")))
      case "prefetch" =>
        val modifierNames = modifiers.map(_.name)
        if (true && (operands.size == 1)) Right(Prefetch(operands(0), modifiers.drop(0), predication))
        else if (true) Left(InstructionDecodeError.InvalidOperandCount("prefetch", Vector(1), operands.size))
        else Left(InstructionDecodeError.UnknownOpcode((name +: modifierNames).mkString(".")))
      case "sad" =>
        val modifierNames = modifiers.map(_.name)
        if (true && (operands.size == 4)) Right(Sad(operands(0), operands(1), operands(2), operands(3), modifiers.drop(0), predication))
        else if (true) Left(InstructionDecodeError.InvalidOperandCount("sad", Vector(4), operands.size))
        else Left(InstructionDecodeError.UnknownOpcode((name +: modifierNames).mkString(".")))
      case "selp" =>
        val modifierNames = modifiers.map(_.name)
        if (true && (operands.size == 4)) Right(Selp(operands(0), operands(1), operands(2), operands(3), modifiers.drop(0), predication))
        else if (true) Left(InstructionDecodeError.InvalidOperandCount("selp", Vector(4), operands.size))
        else Left(InstructionDecodeError.UnknownOpcode((name +: modifierNames).mkString(".")))
      case "set" =>
        val modifierNames = modifiers.map(_.name)
        if (true && (operands.size == 3 || operands.size == 4)) Right(Set(operands(0), operands(1), operands(2), operands.lift(3), modifiers.drop(0), predication))
        else if (true) Left(InstructionDecodeError.InvalidOperandCount("set", Vector(3, 4), operands.size))
        else Left(InstructionDecodeError.UnknownOpcode((name +: modifierNames).mkString(".")))
      case "shr" =>
        val modifierNames = modifiers.map(_.name)
        if (true && (operands.size == 3)) Right(Shr(operands(0), operands(1), operands(2), modifiers.drop(0), predication))
        else if (true) Left(InstructionDecodeError.InvalidOperandCount("shr", Vector(3), operands.size))
        else Left(InstructionDecodeError.UnknownOpcode((name +: modifierNames).mkString(".")))
      case "txq" =>
        val modifierNames = modifiers.map(_.name)
        if (true && (operands.size == 2 || operands.size == 3)) Right(Txq(operands(0), operands(1), operands.lift(2), modifiers.drop(0), predication))
        else if (true) Left(InstructionDecodeError.InvalidOperandCount("txq", Vector(2, 3), operands.size))
        else Left(InstructionDecodeError.UnknownOpcode((name +: modifierNames).mkString(".")))
      case "vabsdiff4" =>
        val modifierNames = modifiers.map(_.name)
        if (true && (operands.size == 4)) Right(Vabsdiff4(operands(0), operands(1), operands(2), operands(3), modifiers.drop(0), predication))
        else if (true) Left(InstructionDecodeError.InvalidOperandCount("vabsdiff4", Vector(4), operands.size))
        else Left(InstructionDecodeError.UnknownOpcode((name +: modifierNames).mkString(".")))
      case "vset2" =>
        val modifierNames = modifiers.map(_.name)
        if (true && (operands.size == 4)) Right(Vset2(operands(0), operands(1), operands(2), operands(3), modifiers.drop(0), predication))
        else if (true) Left(InstructionDecodeError.InvalidOperandCount("vset2", Vector(4), operands.size))
        else Left(InstructionDecodeError.UnknownOpcode((name +: modifierNames).mkString(".")))
      case _ => Left(InstructionDecodeError.UnknownOpcode(name))
    }

  private def decode6(
      name: String,
      operands: Vector[Operand],
      modifiers: Vector[Modifier],
      predication: Option[Predication]
  ): Either[InstructionDecodeError, Instruction] =
    name match {
      case "add" =>
        val modifierNames = modifiers.map(_.name)
        if (modifierNames.startsWith(Vector("cc")) && (operands.size == 3)) Right(AddCc(operands(0), operands(1), operands(2), modifiers.drop(1), predication))
        else if (modifierNames.startsWith(Vector("cc"))) Left(InstructionDecodeError.InvalidOperandCount("add.cc", Vector(3), operands.size))
        else if (true && (operands.size == 3)) Right(Add(operands(0), operands(1), operands(2), modifiers.drop(0), predication))
        else if (true) Left(InstructionDecodeError.InvalidOperandCount("add", Vector(3), operands.size))
        else Left(InstructionDecodeError.UnknownOpcode((name +: modifierNames).mkString(".")))
      case "applypriority" =>
        val modifierNames = modifiers.map(_.name)
        if (true && (operands.size == 2)) Right(ApplyPriority(operands(0), operands(1), modifiers.drop(0), predication))
        else if (true) Left(InstructionDecodeError.InvalidOperandCount("applypriority", Vector(2), operands.size))
        else Left(InstructionDecodeError.UnknownOpcode((name +: modifierNames).mkString(".")))
      case "bar" =>
        val modifierNames = modifiers.map(_.name)
        if (modifierNames.startsWith(Vector("warp", "sync")) && (operands.size == 1)) Right(BarWarpSync(operands(0), modifiers.drop(2), predication))
        else if (modifierNames.startsWith(Vector("warp", "sync"))) Left(InstructionDecodeError.InvalidOperandCount("bar.warp.sync", Vector(1), operands.size))
        else if (true && (operands.size == 1 || operands.size == 2 || operands.size == 3 || operands.size == 4)) Right(Bar(operands(0), operands.lift(1), operands.lift(2), operands.lift(3), modifiers.drop(0), predication))
        else if (true) Left(InstructionDecodeError.InvalidOperandCount("bar", Vector(1, 2, 3, 4), operands.size))
        else Left(InstructionDecodeError.UnknownOpcode((name +: modifierNames).mkString(".")))
      case "bfind" =>
        val modifierNames = modifiers.map(_.name)
        if (true && (operands.size == 2)) Right(Bfind(operands(0), operands(1), modifiers.drop(0), predication))
        else if (true) Left(InstructionDecodeError.InvalidOperandCount("bfind", Vector(2), operands.size))
        else Left(InstructionDecodeError.UnknownOpcode((name +: modifierNames).mkString(".")))
      case "brkpt" =>
        val modifierNames = modifiers.map(_.name)
        if (true && (operands.size == 0)) Right(Brkpt(modifiers.drop(0), predication))
        else if (true) Left(InstructionDecodeError.InvalidOperandCount("brkpt", Vector(0), operands.size))
        else Left(InstructionDecodeError.UnknownOpcode((name +: modifierNames).mkString(".")))
      case "clusterlaunchcontrol" =>
        val modifierNames = modifiers.map(_.name)
        if (modifierNames.startsWith(Vector("query_cancel")) && (operands.size == 2)) Right(ClusterLaunchControlQueryCancel(operands(0), operands(1), modifiers.drop(1), predication))
        else if (modifierNames.startsWith(Vector("query_cancel"))) Left(InstructionDecodeError.InvalidOperandCount("clusterlaunchcontrol.query_cancel", Vector(2), operands.size))
        else if (modifierNames.startsWith(Vector("try_cancel")) && (operands.size == 2)) Right(ClusterLaunchControlTryCancel(operands(0), operands(1), modifiers.drop(1), predication))
        else if (modifierNames.startsWith(Vector("try_cancel"))) Left(InstructionDecodeError.InvalidOperandCount("clusterlaunchcontrol.try_cancel", Vector(2), operands.size))
        else Left(InstructionDecodeError.UnknownOpcode((name +: modifierNames).mkString(".")))
      case "copysign" =>
        val modifierNames = modifiers.map(_.name)
        if (true && (operands.size == 3)) Right(CopySign(operands(0), operands(1), operands(2), modifiers.drop(0), predication))
        else if (true) Left(InstructionDecodeError.InvalidOperandCount("copysign", Vector(3), operands.size))
        else Left(InstructionDecodeError.UnknownOpcode((name +: modifierNames).mkString(".")))
      case "dp4a" =>
        val modifierNames = modifiers.map(_.name)
        if (true && (operands.size == 4)) Right(Dp4a(operands(0), operands(1), operands(2), operands(3), modifiers.drop(0), predication))
        else if (true) Left(InstructionDecodeError.InvalidOperandCount("dp4a", Vector(4), operands.size))
        else Left(InstructionDecodeError.UnknownOpcode((name +: modifierNames).mkString(".")))
      case "istypep" =>
        val modifierNames = modifiers.map(_.name)
        if (true && (operands.size == 2)) Right(IsTypePredicate(operands(0), operands(1), modifiers.drop(0), predication))
        else if (true) Left(InstructionDecodeError.InvalidOperandCount("istypep", Vector(2), operands.size))
        else Left(InstructionDecodeError.UnknownOpcode((name +: modifierNames).mkString(".")))
      case "mad24" =>
        val modifierNames = modifiers.map(_.name)
        if (true && (operands.size == 4)) Right(Mad24(operands(0), operands(1), operands(2), operands(3), modifiers.drop(0), predication))
        else if (true) Left(InstructionDecodeError.InvalidOperandCount("mad24", Vector(4), operands.size))
        else Left(InstructionDecodeError.UnknownOpcode((name +: modifierNames).mkString(".")))
      case "match" =>
        val modifierNames = modifiers.map(_.name)
        if (modifierNames.startsWith(Vector("all", "sync")) && (operands.size == 3)) Right(MatchAllSync(operands(0), operands(1), operands(2), modifiers.drop(2), predication))
        else if (modifierNames.startsWith(Vector("all", "sync"))) Left(InstructionDecodeError.InvalidOperandCount("match.all.sync", Vector(3), operands.size))
        else if (modifierNames.startsWith(Vector("any", "sync")) && (operands.size == 3)) Right(MatchAnySync(operands(0), operands(1), operands(2), modifiers.drop(2), predication))
        else if (modifierNames.startsWith(Vector("any", "sync"))) Left(InstructionDecodeError.InvalidOperandCount("match.any.sync", Vector(3), operands.size))
        else Left(InstructionDecodeError.UnknownOpcode((name +: modifierNames).mkString(".")))
      case "max" =>
        val modifierNames = modifiers.map(_.name)
        if (true && (operands.size == 3 || operands.size == 4)) Right(Max(operands(0), operands(1), operands(2), operands.lift(3), modifiers.drop(0), predication))
        else if (true) Left(InstructionDecodeError.InvalidOperandCount("max", Vector(3, 4), operands.size))
        else Left(InstructionDecodeError.UnknownOpcode((name +: modifierNames).mkString(".")))
      case "min" =>
        val modifierNames = modifiers.map(_.name)
        if (true && (operands.size == 3 || operands.size == 4)) Right(Min(operands(0), operands(1), operands(2), operands.lift(3), modifiers.drop(0), predication))
        else if (true) Left(InstructionDecodeError.InvalidOperandCount("min", Vector(3, 4), operands.size))
        else Left(InstructionDecodeError.UnknownOpcode((name +: modifierNames).mkString(".")))
      case "or" =>
        val modifierNames = modifiers.map(_.name)
        if (true && (operands.size == 3)) Right(Or(operands(0), operands(1), operands(2), modifiers.drop(0), predication))
        else if (true) Left(InstructionDecodeError.InvalidOperandCount("or", Vector(3), operands.size))
        else Left(InstructionDecodeError.UnknownOpcode((name +: modifierNames).mkString(".")))
      case "popc" =>
        val modifierNames = modifiers.map(_.name)
        if (true && (operands.size == 2)) Right(Popc(operands(0), operands(1), modifiers.drop(0), predication))
        else if (true) Left(InstructionDecodeError.InvalidOperandCount("popc", Vector(2), operands.size))
        else Left(InstructionDecodeError.UnknownOpcode((name +: modifierNames).mkString(".")))
      case "prmt" =>
        val modifierNames = modifiers.map(_.name)
        if (true && (operands.size == 4)) Right(Prmt(operands(0), operands(1), operands(2), operands(3), modifiers.drop(0), predication))
        else if (true) Left(InstructionDecodeError.InvalidOperandCount("prmt", Vector(4), operands.size))
        else Left(InstructionDecodeError.UnknownOpcode((name +: modifierNames).mkString(".")))
      case "shl" =>
        val modifierNames = modifiers.map(_.name)
        if (true && (operands.size == 3)) Right(Shl(operands(0), operands(1), operands(2), modifiers.drop(0), predication))
        else if (true) Left(InstructionDecodeError.InvalidOperandCount("shl", Vector(3), operands.size))
        else Left(InstructionDecodeError.UnknownOpcode((name +: modifierNames).mkString(".")))
      case "st" =>
        val modifierNames = modifiers.map(_.name)
        if (modifierNames.startsWith(Vector("async")) && (operands.size == 2 || operands.size == 3)) Right(StAsync(operands(0), operands(1), operands.lift(2), modifiers.drop(1), predication))
        else if (modifierNames.startsWith(Vector("async"))) Left(InstructionDecodeError.InvalidOperandCount("st.async", Vector(2, 3), operands.size))
        else if (modifierNames.startsWith(Vector("bulk")) && (operands.size == 3)) Right(StBulk(operands(0), operands(1), operands(2), modifiers.drop(1), predication))
        else if (modifierNames.startsWith(Vector("bulk"))) Left(InstructionDecodeError.InvalidOperandCount("st.bulk", Vector(3), operands.size))
        else if (true && (operands.size == 2 || operands.size == 3)) Right(St(operands(0), operands(1), operands.lift(2), modifiers.drop(0), predication))
        else if (true) Left(InstructionDecodeError.InvalidOperandCount("st", Vector(2, 3), operands.size))
        else Left(InstructionDecodeError.UnknownOpcode((name +: modifierNames).mkString(".")))
      case "stacksave" =>
        val modifierNames = modifiers.map(_.name)
        if (true && (operands.size == 1)) Right(StackSave(operands(0), modifiers.drop(0), predication))
        else if (true) Left(InstructionDecodeError.InvalidOperandCount("stacksave", Vector(1), operands.size))
        else Left(InstructionDecodeError.UnknownOpcode((name +: modifierNames).mkString(".")))
      case "sust" =>
        val modifierNames = modifiers.map(_.name)
        if (true && (operands.size == 2)) Right(Sust(operands(0), operands(1), modifiers.drop(0), predication))
        else if (true) Left(InstructionDecodeError.InvalidOperandCount("sust", Vector(2), operands.size))
        else Left(InstructionDecodeError.UnknownOpcode((name +: modifierNames).mkString(".")))
      case "tensormap" =>
        val modifierNames = modifiers.map(_.name)
        if (modifierNames.startsWith(Vector("cp_fenceproxy")) && (operands.size == 3)) Right(TensorMapCopyFenceProxy(operands(0), operands(1), operands(2), modifiers.drop(1), predication))
        else if (modifierNames.startsWith(Vector("cp_fenceproxy"))) Left(InstructionDecodeError.InvalidOperandCount("tensormap.cp_fenceproxy", Vector(3), operands.size))
        else if (modifierNames.startsWith(Vector("replace")) && (operands.size == 2 || operands.size == 3)) Right(TensorMapReplace(operands(0), operands(1), operands.lift(2), modifiers.drop(1), predication))
        else if (modifierNames.startsWith(Vector("replace"))) Left(InstructionDecodeError.InvalidOperandCount("tensormap.replace", Vector(2, 3), operands.size))
        else Left(InstructionDecodeError.UnknownOpcode((name +: modifierNames).mkString(".")))
      case "vset" =>
        val modifierNames = modifiers.map(_.name)
        if (true && (operands.size == 3 || operands.size == 4)) Right(Vset(operands(0), operands(1), operands(2), operands.lift(3), modifiers.drop(0), predication))
        else if (true) Left(InstructionDecodeError.InvalidOperandCount("vset", Vector(3, 4), operands.size))
        else Left(InstructionDecodeError.UnknownOpcode((name +: modifierNames).mkString(".")))
      case "vshr" =>
        val modifierNames = modifiers.map(_.name)
        if (true && (operands.size == 3 || operands.size == 4)) Right(Vshr(operands(0), operands(1), operands(2), operands.lift(3), modifiers.drop(0), predication))
        else if (true) Left(InstructionDecodeError.InvalidOperandCount("vshr", Vector(3, 4), operands.size))
        else Left(InstructionDecodeError.UnknownOpcode((name +: modifierNames).mkString(".")))
      case "vsub4" =>
        val modifierNames = modifiers.map(_.name)
        if (true && (operands.size == 4)) Right(Vsub4(operands(0), operands(1), operands(2), operands(3), modifiers.drop(0), predication))
        else if (true) Left(InstructionDecodeError.InvalidOperandCount("vsub4", Vector(4), operands.size))
        else Left(InstructionDecodeError.UnknownOpcode((name +: modifierNames).mkString(".")))
      case _ => Left(InstructionDecodeError.UnknownOpcode(name))
    }
}

val Version: PtxVersion = PtxVersion(9, 3)

private def instructionSpecChunk0: Vector[InstructionSpec] = Vector(
    InstructionSpec(
      "integer-arithmetic-instructions-add",
      "9.7.1.1. Integer Arithmetic Instructions: add",
      "add",
      "Integer Arithmetic Instructions",
      Vector("add"),
      Vector("add.type1        d, a, b;\nadd{.sat}.type2  d, a, b;\n\n.type1 = { .u16, .u64,\n           .s16, .s64 };\n\n.type2 = { .u32, .u16x2, .u8x4,\n           .s32, .s16x2, .s8x4 };"),
      PtxVersion(1, 0),
      SmTarget(10),
      Vector(InstructionForm("add", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("s16", "s64", "u16", "u64")), _root_.scala.collection.immutable.Set(), false), InstructionForm("add", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("s16x2", "s32", "s8x4", "u16x2", "u32", "u8x4")), _root_.scala.collection.immutable.Set("sat"), false))
    ),
    InstructionSpec(
      "integer-arithmetic-instructions-sub",
      "9.7.1.2. Integer Arithmetic Instructions: sub",
      "sub",
      "Integer Arithmetic Instructions",
      Vector("sub"),
      Vector("sub.type1        d, a, b;\nsub{.sat}.type2  d, a, b;\n\n.type1 = { .u16, .u32, .u64,\n           .s16, .s64 };\n\n.type2 = { .s32, .u8x4, .s8x4 };"),
      PtxVersion(1, 0),
      SmTarget(10),
      Vector(InstructionForm("sub", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("s16", "s64", "u16", "u32", "u64")), _root_.scala.collection.immutable.Set(), false), InstructionForm("sub", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("s32", "s8x4", "u8x4")), _root_.scala.collection.immutable.Set("sat"), false))
    ),
    InstructionSpec(
      "integer-arithmetic-instructions-mul",
      "9.7.1.3. Integer Arithmetic Instructions: mul",
      "mul",
      "Integer Arithmetic Instructions",
      Vector("mul"),
      Vector("mul.mode.type  d, a, b;\n\n.mode = { .hi, .lo, .wide };\n.type = { .u16, .u32, .u64,\n          .s16, .s32, .s64 };"),
      PtxVersion(1, 0),
      SmTarget(10),
      Vector(InstructionForm("mul", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("hi", "lo", "wide"), _root_.scala.collection.immutable.Set("s16", "s32", "s64", "u16", "u32", "u64")), _root_.scala.collection.immutable.Set(), true))
    ),
    InstructionSpec(
      "integer-arithmetic-instructions-mad",
      "9.7.1.4. Integer Arithmetic Instructions: mad",
      "mad",
      "Integer Arithmetic Instructions",
      Vector("mad"),
      Vector("mad.mode.type  d, a, b, c;\nmad.hi.sat.s32 d, a, b, c;\n\n.mode = { .hi, .lo, .wide };\n.type = { .u16, .u32, .u64,\n          .s16, .s32, .s64 };"),
      PtxVersion(1, 0),
      SmTarget(10),
      Vector(InstructionForm("mad", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("hi", "lo", "wide"), _root_.scala.collection.immutable.Set("s16", "s32", "s64", "u16", "u32", "u64")), _root_.scala.collection.immutable.Set(), true), InstructionForm("mad", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("hi"), _root_.scala.collection.immutable.Set("sat"), _root_.scala.collection.immutable.Set("s32")), _root_.scala.collection.immutable.Set(), true))
    ),
    InstructionSpec(
      "integer-arithmetic-instructions-clmad",
      "9.7.1.5. Integer Arithmetic Instructions: clmad",
      "clmad",
      "Integer Arithmetic Instructions",
      Vector("clmad"),
      Vector("clmad.mode.u64 d, a, b, c;\n\n.mode = { .hi, .lo };"),
      PtxVersion(9, 3),
      SmTarget(80),
      Vector(InstructionForm("clmad", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("hi", "lo"), _root_.scala.collection.immutable.Set("u64")), _root_.scala.collection.immutable.Set(), true))
    ),
    InstructionSpec(
      "integer-arithmetic-instructions-mul24",
      "9.7.1.6. Integer Arithmetic Instructions: mul24",
      "mul24",
      "Integer Arithmetic Instructions",
      Vector("mul24"),
      Vector("mul24.mode.type  d, a, b;\n\n.mode = { .hi, .lo };\n.type = { .u32, .s32 };"),
      PtxVersion(1, 0),
      SmTarget(10),
      Vector(InstructionForm("mul24", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("hi", "lo"), _root_.scala.collection.immutable.Set("s32", "u32")), _root_.scala.collection.immutable.Set(), true))
    ),
    InstructionSpec(
      "integer-arithmetic-instructions-mad24",
      "9.7.1.7. Integer Arithmetic Instructions: mad24",
      "mad24",
      "Integer Arithmetic Instructions",
      Vector("mad24"),
      Vector("mad24.mode.type  d, a, b, c;\nmad24.hi.sat.s32 d, a, b, c;\n\n.mode = { .hi, .lo };\n.type = { .u32, .s32 };"),
      PtxVersion(1, 0),
      SmTarget(10),
      Vector(InstructionForm("mad24", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("hi", "lo"), _root_.scala.collection.immutable.Set("s32", "u32")), _root_.scala.collection.immutable.Set(), true), InstructionForm("mad24", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("hi"), _root_.scala.collection.immutable.Set("sat"), _root_.scala.collection.immutable.Set("s32")), _root_.scala.collection.immutable.Set(), true))
    ),
    InstructionSpec(
      "integer-arithmetic-instructions-sad",
      "9.7.1.8. Integer Arithmetic Instructions: sad",
      "sad",
      "Integer Arithmetic Instructions",
      Vector("sad"),
      Vector("sad.type  d, a, b, c;\n\n.type = { .u16, .u32, .u64,\n          .s16, .s32, .s64 };"),
      PtxVersion(1, 0),
      SmTarget(10),
      Vector(InstructionForm("sad", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("s16", "s32", "s64", "u16", "u32", "u64")), _root_.scala.collection.immutable.Set(), true))
    ),
    InstructionSpec(
      "integer-arithmetic-instructions-div",
      "9.7.1.9. Integer Arithmetic Instructions: div",
      "div",
      "Integer Arithmetic Instructions",
      Vector("div"),
      Vector("div.type  d, a, b;\n\n.type = { .u16, .u32, .u64,\n          .s16, .s32, .s64 };"),
      PtxVersion(1, 0),
      SmTarget(10),
      Vector(InstructionForm("div", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("s16", "s32", "s64", "u16", "u32", "u64")), _root_.scala.collection.immutable.Set(), true))
    ),
    InstructionSpec(
      "integer-arithmetic-instructions-rem",
      "9.7.1.10. Integer Arithmetic Instructions: rem",
      "rem",
      "Integer Arithmetic Instructions",
      Vector("rem"),
      Vector("rem.type  d, a, b;\n\n.type = { .u16, .u32, .u64,\n          .s16, .s32, .s64 };"),
      PtxVersion(1, 0),
      SmTarget(10),
      Vector(InstructionForm("rem", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("s16", "s32", "s64", "u16", "u32", "u64")), _root_.scala.collection.immutable.Set(), true))
    ),
    InstructionSpec(
      "integer-arithmetic-instructions-abs",
      "9.7.1.11. Integer Arithmetic Instructions: abs",
      "abs",
      "Integer Arithmetic Instructions",
      Vector("abs"),
      Vector("abs.type  d, a;\n\n.type = { .s16, .s32, .s64 };"),
      PtxVersion(1, 0),
      SmTarget(10),
      Vector(InstructionForm("abs", Vector(OperandConstraint.Register, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("s16", "s32", "s64")), _root_.scala.collection.immutable.Set(), true))
    ),
    InstructionSpec(
      "integer-arithmetic-instructions-neg",
      "9.7.1.12. Integer Arithmetic Instructions: neg",
      "neg",
      "Integer Arithmetic Instructions",
      Vector("neg"),
      Vector("neg.type  d, a;\n\n.type = { .s8x4, .s16, .s32, .s64 };"),
      PtxVersion(1, 0),
      SmTarget(10),
      Vector(InstructionForm("neg", Vector(OperandConstraint.Register, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("s16", "s32", "s64", "s8x4")), _root_.scala.collection.immutable.Set(), false))
    )
)

private def instructionSpecChunk1: Vector[InstructionSpec] = Vector(
    InstructionSpec(
      "integer-arithmetic-instructions-min",
      "9.7.1.13. Integer Arithmetic Instructions: min",
      "min",
      "Integer Arithmetic Instructions",
      Vector("min"),
      Vector("min.type1         d, a, b;\nmin{.relu}.type2  d, a, b;\n\n.type1 = { .u16, .u32, .u64,\n           .u16x2, .u8x4, .s16, .s64 };\n.type2 = { .s16x2, .s32, .s8x4 };"),
      PtxVersion(1, 0),
      SmTarget(10),
      Vector(InstructionForm("min", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("s16", "s64", "u16", "u16x2", "u32", "u64", "u8x4")), _root_.scala.collection.immutable.Set(), false), InstructionForm("min", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("s16x2", "s32", "s8x4")), _root_.scala.collection.immutable.Set("relu"), false))
    ),
    InstructionSpec(
      "integer-arithmetic-instructions-max",
      "9.7.1.14. Integer Arithmetic Instructions: max",
      "max",
      "Integer Arithmetic Instructions",
      Vector("max"),
      Vector("max.type1         d, a, b;\nmax{.relu}.type2  d, a, b;\n\n.type1 = { .u16, .u32, .u64,\n           .u16x2, .u8x4, .s16, .s64 };\n.type2 = { .s16x2, .s32, .s8x4 };"),
      PtxVersion(1, 0),
      SmTarget(10),
      Vector(InstructionForm("max", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("s16", "s64", "u16", "u16x2", "u32", "u64", "u8x4")), _root_.scala.collection.immutable.Set(), false), InstructionForm("max", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("s16x2", "s32", "s8x4")), _root_.scala.collection.immutable.Set("relu"), false))
    ),
    InstructionSpec(
      "integer-arithmetic-instructions-popc",
      "9.7.1.15. Integer Arithmetic Instructions: popc",
      "popc",
      "Integer Arithmetic Instructions",
      Vector("popc"),
      Vector("popc.type  d, a;\n\n.type = { .b32, .b64 };"),
      PtxVersion(2, 0),
      SmTarget(20),
      Vector(InstructionForm("popc", Vector(OperandConstraint.Register, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("b32", "b64")), _root_.scala.collection.immutable.Set(), true))
    ),
    InstructionSpec(
      "integer-arithmetic-instructions-clz",
      "9.7.1.16. Integer Arithmetic Instructions: clz",
      "clz",
      "Integer Arithmetic Instructions",
      Vector("clz"),
      Vector("clz.type  d, a;\n\n.type = { .b32, .b64 };"),
      PtxVersion(2, 0),
      SmTarget(20),
      Vector(InstructionForm("clz", Vector(OperandConstraint.Register, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("b32", "b64")), _root_.scala.collection.immutable.Set(), true))
    ),
    InstructionSpec(
      "integer-arithmetic-instructions-bfind",
      "9.7.1.17. Integer Arithmetic Instructions: bfind",
      "bfind",
      "Integer Arithmetic Instructions",
      Vector("bfind"),
      Vector("bfind.type           d, a;\nbfind.shiftamt.type  d, a;\n\n.type = { .u32, .u64,\n          .s32, .s64 };"),
      PtxVersion(2, 0),
      SmTarget(20),
      Vector(InstructionForm("bfind", Vector(OperandConstraint.Register, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("s32", "s64", "u32", "u64")), _root_.scala.collection.immutable.Set(), true), InstructionForm("bfind", Vector(OperandConstraint.Register, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("shiftamt"), _root_.scala.collection.immutable.Set("s32", "s64", "u32", "u64")), _root_.scala.collection.immutable.Set(), true))
    ),
    InstructionSpec(
      "integer-arithmetic-instructions-fns",
      "9.7.1.18. Integer Arithmetic Instructions: fns",
      "fns",
      "Integer Arithmetic Instructions",
      Vector("fns"),
      Vector("fns.b32 d, mask, base, offset;"),
      PtxVersion(6, 0),
      SmTarget(30),
      Vector(InstructionForm("fns", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("b32")), _root_.scala.collection.immutable.Set(), true))
    ),
    InstructionSpec(
      "integer-arithmetic-instructions-brev",
      "9.7.1.19. Integer Arithmetic Instructions: brev",
      "brev",
      "Integer Arithmetic Instructions",
      Vector("brev"),
      Vector("brev.type  d, a;\n\n.type = { .b32, .b64 };"),
      PtxVersion(2, 0),
      SmTarget(20),
      Vector(InstructionForm("brev", Vector(OperandConstraint.Register, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("b32", "b64")), _root_.scala.collection.immutable.Set(), true))
    ),
    InstructionSpec(
      "integer-arithmetic-instructions-bfe",
      "9.7.1.20. Integer Arithmetic Instructions: bfe",
      "bfe",
      "Integer Arithmetic Instructions",
      Vector("bfe"),
      Vector("bfe.type  d, a, b, c;\n\n.type = { .u32, .u64,\n          .s32, .s64 };"),
      PtxVersion(2, 0),
      SmTarget(20),
      Vector(InstructionForm("bfe", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("s32", "s64", "u32", "u64")), _root_.scala.collection.immutable.Set(), true))
    ),
    InstructionSpec(
      "integer-arithmetic-instructions-bfi",
      "9.7.1.21. Integer Arithmetic Instructions: bfi",
      "bfi",
      "Integer Arithmetic Instructions",
      Vector("bfi"),
      Vector("bfi.type  f, a, b, c, d;\n\n.type = { .b32, .b64 };"),
      PtxVersion(2, 0),
      SmTarget(20),
      Vector(InstructionForm("bfi", Vector(OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Register), Vector(_root_.scala.collection.immutable.Set("b32", "b64")), _root_.scala.collection.immutable.Set(), true))
    ),
    InstructionSpec(
      "integer-arithmetic-instructions-szext",
      "9.7.1.22. Integer Arithmetic Instructions: szext",
      "szext",
      "Integer Arithmetic Instructions",
      Vector("szext"),
      Vector("szext.mode.type  d, a, b;\n\n.mode = { .clamp, .wrap };\n.type = { .u32, .s32 };"),
      PtxVersion(7, 6),
      SmTarget(70),
      Vector(InstructionForm("szext", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("clamp", "wrap"), _root_.scala.collection.immutable.Set("s32", "u32")), _root_.scala.collection.immutable.Set(), true))
    ),
    InstructionSpec(
      "integer-arithmetic-instructions-bmsk",
      "9.7.1.23. Integer Arithmetic Instructions: bmsk",
      "bmsk",
      "Integer Arithmetic Instructions",
      Vector("bmsk"),
      Vector("bmsk.mode.b32  d, a, b;\n\n.mode = { .clamp, .wrap };"),
      PtxVersion(7, 6),
      SmTarget(70),
      Vector(InstructionForm("bmsk", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("clamp", "wrap"), _root_.scala.collection.immutable.Set("b32")), _root_.scala.collection.immutable.Set(), true))
    ),
    InstructionSpec(
      "integer-arithmetic-instructions-dp4a",
      "9.7.1.24. Integer Arithmetic Instructions: dp4a",
      "dp4a",
      "Integer Arithmetic Instructions",
      Vector("dp4a"),
      Vector("dp4a.atype.btype  d, a, b, c;\n\n.atype = .btype = { .u32, .s32 };"),
      PtxVersion(5, 0),
      SmTarget(61),
      Vector(InstructionForm("dp4a", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("atype"), _root_.scala.collection.immutable.Set("s32", "u32")), _root_.scala.collection.immutable.Set(), true))
    )
)

private def instructionSpecChunk2: Vector[InstructionSpec] = Vector(
    InstructionSpec(
      "integer-arithmetic-instructions-dp2a",
      "9.7.1.25. Integer Arithmetic Instructions: dp2a",
      "dp2a",
      "Integer Arithmetic Instructions",
      Vector("dp2a"),
      Vector("dp2a.mode.atype.btype  d, a, b, c;\n\n.atype = .btype = { .u32, .s32 };\n.mode = { .lo, .hi };"),
      PtxVersion(5, 0),
      SmTarget(61),
      Vector(InstructionForm("dp2a", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("hi", "lo"), _root_.scala.collection.immutable.Set("atype"), _root_.scala.collection.immutable.Set("s32", "u32")), _root_.scala.collection.immutable.Set(), true))
    ),
    InstructionSpec(
      "extended-precision-arithmetic-instructions-add-cc",
      "9.7.2.1. Extended-Precision Arithmetic Instructions: add.cc",
      "add.cc",
      "Extended-Precision Arithmetic Instructions",
      Vector("add.cc"),
      Vector("add.cc.type  d, a, b;\n\n.type = { .u32, .s32, .u64, .s64 };"),
      PtxVersion(1, 0),
      SmTarget(20),
      Vector(InstructionForm("add.cc", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("s32", "s64", "u32", "u64")), _root_.scala.collection.immutable.Set(), true))
    ),
    InstructionSpec(
      "extended-precision-arithmetic-instructions-addc",
      "9.7.2.2. Extended-Precision Arithmetic Instructions: addc",
      "addc",
      "Extended-Precision Arithmetic Instructions",
      Vector("addc"),
      Vector("addc{.cc}.type  d, a, b;\n\n.type = { .u32, .s32, .u64, .s64 };"),
      PtxVersion(1, 0),
      SmTarget(20),
      Vector(InstructionForm("addc", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("s32", "s64", "u32", "u64")), _root_.scala.collection.immutable.Set("cc"), true))
    ),
    InstructionSpec(
      "extended-precision-arithmetic-instructions-sub-cc",
      "9.7.2.3. Extended-Precision Arithmetic Instructions: sub.cc",
      "sub.cc",
      "Extended-Precision Arithmetic Instructions",
      Vector("sub.cc"),
      Vector("sub.cc.type  d, a, b;\n\n.type = { .u32, .s32, .u64, .s64 };"),
      PtxVersion(1, 0),
      SmTarget(20),
      Vector(InstructionForm("sub.cc", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("s32", "s64", "u32", "u64")), _root_.scala.collection.immutable.Set(), true))
    ),
    InstructionSpec(
      "extended-precision-arithmetic-instructions-subc",
      "9.7.2.4. Extended-Precision Arithmetic Instructions: subc",
      "subc",
      "Extended-Precision Arithmetic Instructions",
      Vector("subc"),
      Vector("subc{.cc}.type  d, a, b;\n\n.type = { .u32, .s32, .u64, .s64 };"),
      PtxVersion(1, 0),
      SmTarget(20),
      Vector(InstructionForm("subc", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("s32", "s64", "u32", "u64")), _root_.scala.collection.immutable.Set("cc"), true))
    ),
    InstructionSpec(
      "extended-precision-arithmetic-instructions-mad-cc",
      "9.7.2.5. Extended-Precision Arithmetic Instructions: mad.cc",
      "mad.cc",
      "Extended-Precision Arithmetic Instructions",
      Vector("mad.cc"),
      Vector("mad{.hi,.lo}.cc.type  d, a, b, c;\n\n.type = { .u32, .s32, .u64, .s64 };"),
      PtxVersion(1, 0),
      SmTarget(20),
      Vector(InstructionForm("mad.cc", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("s32", "s64", "u32", "u64")), _root_.scala.collection.immutable.Set("hi", "lo"), true))
    ),
    InstructionSpec(
      "extended-precision-arithmetic-instructions-madc",
      "9.7.2.6. Extended-Precision Arithmetic Instructions: madc",
      "madc",
      "Extended-Precision Arithmetic Instructions",
      Vector("madc"),
      Vector("madc{.hi,.lo}{.cc}.type  d, a, b, c;\n\n.type = { .u32, .s32, .u64, .s64 };"),
      PtxVersion(1, 0),
      SmTarget(20),
      Vector(InstructionForm("madc", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("s32", "s64", "u32", "u64")), _root_.scala.collection.immutable.Set("cc", "hi", "lo"), true))
    ),
    InstructionSpec(
      "floating-point-instructions-testp",
      "9.7.3.1. Floating Point Instructions: testp",
      "testp",
      "Floating Point Instructions",
      Vector("testp"),
      Vector("testp.op.type  p, a;  // result is .pred\n\n.op   = { .finite, .infinite,\n          .number, .notanumber,\n          .normal, .subnormal };\n.type = { .f32, .f64 };"),
      PtxVersion(2, 0),
      SmTarget(20),
      Vector(InstructionForm("testp", Vector(OperandConstraint.Register, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("finite", "infinite", "normal", "notanumber", "number", "subnormal"), _root_.scala.collection.immutable.Set("f32", "f64")), _root_.scala.collection.immutable.Set(), true))
    ),
    InstructionSpec(
      "floating-point-instructions-copysign",
      "9.7.3.2. Floating Point Instructions: copysign",
      "copysign",
      "Floating Point Instructions",
      Vector("copysign"),
      Vector("copysign.type  d, a, b;\n\n.type = { .f32, .f64 };"),
      PtxVersion(2, 0),
      SmTarget(20),
      Vector(InstructionForm("copysign", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("f32", "f64")), _root_.scala.collection.immutable.Set(), true))
    ),
    InstructionSpec(
      "floating-point-instructions-add",
      "9.7.3.3. Floating Point Instructions: add",
      "add",
      "Floating Point Instructions",
      Vector("add"),
      Vector("add{.rnd}{.ftz}{.sat}.f32  d, a, b;\nadd{.rnd}{.ftz}.f32x2      d, a, b;\nadd{.rnd}.f64              d, a, b;\n\n.rnd = { .rn, .rz, .rm, .rp };"),
      PtxVersion(1, 0),
      SmTarget(13),
      Vector(InstructionForm("add", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("f32")), _root_.scala.collection.immutable.Set("ftz", "rm", "rn", "rp", "rz", "sat"), false), InstructionForm("add", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("f32x2")), _root_.scala.collection.immutable.Set("ftz", "rm", "rn", "rp", "rz"), false), InstructionForm("add", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("f64")), _root_.scala.collection.immutable.Set("rm", "rn", "rp", "rz"), false))
    ),
    InstructionSpec(
      "floating-point-instructions-sub",
      "9.7.3.4. Floating Point Instructions: sub",
      "sub",
      "Floating Point Instructions",
      Vector("sub"),
      Vector("sub{.rnd}{.ftz}{.sat}.f32  d, a, b;\nsub{.rnd}{.ftz}.f32x2      d, a, b;\nsub{.rnd}.f64              d, a, b;\n\n.rnd = { .rn, .rz, .rm, .rp };"),
      PtxVersion(1, 0),
      SmTarget(13),
      Vector(InstructionForm("sub", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("f32")), _root_.scala.collection.immutable.Set("ftz", "rm", "rn", "rp", "rz", "sat"), false), InstructionForm("sub", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("f32x2")), _root_.scala.collection.immutable.Set("ftz", "rm", "rn", "rp", "rz"), false), InstructionForm("sub", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("f64")), _root_.scala.collection.immutable.Set("rm", "rn", "rp", "rz"), false))
    ),
    InstructionSpec(
      "floating-point-instructions-mul",
      "9.7.3.5. Floating Point Instructions: mul",
      "mul",
      "Floating Point Instructions",
      Vector("mul"),
      Vector("mul{.rnd}{.ftz}{.sat}.f32  d, a, b;\nmul{.rnd}{.ftz}.f32x2      d, a, b;\nmul{.rnd}.f64              d, a, b;\n\n.rnd = { .rn, .rz, .rm, .rp };"),
      PtxVersion(1, 0),
      SmTarget(13),
      Vector(InstructionForm("mul", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("f32")), _root_.scala.collection.immutable.Set("ftz", "rm", "rn", "rp", "rz", "sat"), false), InstructionForm("mul", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("f32x2")), _root_.scala.collection.immutable.Set("ftz", "rm", "rn", "rp", "rz"), false), InstructionForm("mul", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("f64")), _root_.scala.collection.immutable.Set("rm", "rn", "rp", "rz"), false))
    )
)

private def instructionSpecChunk3: Vector[InstructionSpec] = Vector(
    InstructionSpec(
      "floating-point-instructions-fma",
      "9.7.3.6. Floating Point Instructions: fma",
      "fma",
      "Floating Point Instructions",
      Vector("fma"),
      Vector("fma.rnd{.ftz}{.sat}.f32  d, a, b, c;\nfma.rnd{.ftz}.f32x2      d, a, b, c;\nfma.rnd.f64              d, a, b, c;\n\n.rnd = { .rn, .rz, .rm, .rp };"),
      PtxVersion(1, 0),
      SmTarget(13),
      Vector(InstructionForm("fma", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("rm", "rn", "rp", "rz"), _root_.scala.collection.immutable.Set("f32")), _root_.scala.collection.immutable.Set("ftz", "sat"), false), InstructionForm("fma", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("rm", "rn", "rp", "rz"), _root_.scala.collection.immutable.Set("f32x2")), _root_.scala.collection.immutable.Set("ftz"), false), InstructionForm("fma", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("rm", "rn", "rp", "rz"), _root_.scala.collection.immutable.Set("f64")), _root_.scala.collection.immutable.Set(), false))
    ),
    InstructionSpec(
      "floating-point-instructions-mad",
      "9.7.3.7. Floating Point Instructions: mad",
      "mad",
      "Floating Point Instructions",
      Vector("mad"),
      Vector("mad{.ftz}{.sat}.f32      d, a, b, c;    // .target sm_1x\nmad.rnd{.ftz}{.sat}.f32  d, a, b, c;    // .target sm_20\nmad.rnd.f64              d, a, b, c;    // .target sm_13 and higher\n\n.rnd = { .rn, .rz, .rm, .rp };"),
      PtxVersion(1, 0),
      SmTarget(13),
      Vector(InstructionForm("mad", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("f32")), _root_.scala.collection.immutable.Set("ftz", "sat"), false), InstructionForm("mad", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("rm", "rn", "rp", "rz"), _root_.scala.collection.immutable.Set("f32")), _root_.scala.collection.immutable.Set("ftz", "sat"), false), InstructionForm("mad", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("rm", "rn", "rp", "rz"), _root_.scala.collection.immutable.Set("f64")), _root_.scala.collection.immutable.Set(), false))
    ),
    InstructionSpec(
      "floating-point-instructions-div",
      "9.7.3.8. Floating Point Instructions: div",
      "div",
      "Floating Point Instructions",
      Vector("div"),
      Vector("div.approx{.ftz}.f32  d, a, b;  // fast, approximate divide\ndiv.full{.ftz}.f32    d, a, b;  // full-range approximate divide\ndiv.rnd{.ftz}.f32     d, a, b;  // IEEE 754 compliant rounding\ndiv.rnd.f64           d, a, b;  // IEEE 754 compliant rounding\n\n.rnd = { .rn, .rz, .rm, .rp };"),
      PtxVersion(1, 0),
      SmTarget(13),
      Vector(InstructionForm("div", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("approx"), _root_.scala.collection.immutable.Set("f32")), _root_.scala.collection.immutable.Set("ftz"), false), InstructionForm("div", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("full"), _root_.scala.collection.immutable.Set("f32")), _root_.scala.collection.immutable.Set("ftz"), false), InstructionForm("div", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("rm", "rn", "rp", "rz"), _root_.scala.collection.immutable.Set("f32")), _root_.scala.collection.immutable.Set("ftz"), false), InstructionForm("div", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("rm", "rn", "rp", "rz"), _root_.scala.collection.immutable.Set("f64")), _root_.scala.collection.immutable.Set(), false))
    ),
    InstructionSpec(
      "floating-point-instructions-abs",
      "9.7.3.9. Floating Point Instructions: abs",
      "abs",
      "Floating Point Instructions",
      Vector("abs"),
      Vector("abs{.ftz}.f32  d, a;\nabs.f64        d, a;"),
      PtxVersion(1, 0),
      SmTarget(13),
      Vector(InstructionForm("abs", Vector(OperandConstraint.Register, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("f32")), _root_.scala.collection.immutable.Set("ftz"), true), InstructionForm("abs", Vector(OperandConstraint.Register, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("f64")), _root_.scala.collection.immutable.Set(), true))
    ),
    InstructionSpec(
      "floating-point-instructions-neg",
      "9.7.3.10. Floating Point Instructions: neg",
      "neg",
      "Floating Point Instructions",
      Vector("neg"),
      Vector("neg{.ftz}.f32  d, a;\nneg.f64        d, a;"),
      PtxVersion(1, 0),
      SmTarget(13),
      Vector(InstructionForm("neg", Vector(OperandConstraint.Register, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("f32")), _root_.scala.collection.immutable.Set("ftz"), true), InstructionForm("neg", Vector(OperandConstraint.Register, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("f64")), _root_.scala.collection.immutable.Set(), true))
    ),
    InstructionSpec(
      "floating-point-instructions-min",
      "9.7.3.11. Floating Point Instructions: min",
      "min",
      "Floating Point Instructions",
      Vector("min"),
      Vector("min{.ftz}{.NaN}{.xorsign.abs}.f32  d, a, b;\nmin{.ftz}{.NaN}{.abs}.f32          d, a, b, c;\nmin.f64                            d, a, b;"),
      PtxVersion(1, 0),
      SmTarget(13),
      Vector(InstructionForm("min", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("f32")), _root_.scala.collection.immutable.Set("NaN", "abs", "ftz", "xorsign"), false), InstructionForm("min", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("f32")), _root_.scala.collection.immutable.Set("NaN", "abs", "ftz"), false), InstructionForm("min", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("f64")), _root_.scala.collection.immutable.Set(), false))
    ),
    InstructionSpec(
      "floating-point-instructions-max",
      "9.7.3.12. Floating Point Instructions: max",
      "max",
      "Floating Point Instructions",
      Vector("max"),
      Vector("max{.ftz}{.NaN}{.xorsign.abs}.f32  d, a, b;\nmax{.ftz}{.NaN}{.abs}.f32          d, a, b, c;\nmax.f64                            d, a, b;"),
      PtxVersion(1, 0),
      SmTarget(13),
      Vector(InstructionForm("max", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("f32")), _root_.scala.collection.immutable.Set("NaN", "abs", "ftz", "xorsign"), false), InstructionForm("max", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("f32")), _root_.scala.collection.immutable.Set("NaN", "abs", "ftz"), false), InstructionForm("max", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("f64")), _root_.scala.collection.immutable.Set(), false))
    ),
    InstructionSpec(
      "floating-point-instructions-rcp",
      "9.7.3.13. Floating Point Instructions: rcp",
      "rcp",
      "Floating Point Instructions",
      Vector("rcp"),
      Vector("rcp.approx{.ftz}.f32  d, a;  // fast, approximate reciprocal\nrcp.rnd{.ftz}.f32     d, a;  // IEEE 754 compliant rounding\nrcp.rnd.f64           d, a;  // IEEE 754 compliant rounding\n\n.rnd = { .rn, .rz, .rm, .rp };"),
      PtxVersion(1, 0),
      SmTarget(13),
      Vector(InstructionForm("rcp", Vector(OperandConstraint.Register, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("approx"), _root_.scala.collection.immutable.Set("f32")), _root_.scala.collection.immutable.Set("ftz"), false), InstructionForm("rcp", Vector(OperandConstraint.Register, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("rm", "rn", "rp", "rz"), _root_.scala.collection.immutable.Set("f32")), _root_.scala.collection.immutable.Set("ftz"), false), InstructionForm("rcp", Vector(OperandConstraint.Register, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("rm", "rn", "rp", "rz"), _root_.scala.collection.immutable.Set("f64")), _root_.scala.collection.immutable.Set(), false))
    ),
    InstructionSpec(
      "floating-point-instructions-rcp-approx-ftz-f64",
      "9.7.3.14. Floating Point Instructions: rcp.approx.ftz.f64",
      "rcp.approx.ftz.f64",
      "Floating Point Instructions",
      Vector("rcp.approx.ftz.f64"),
      Vector("rcp.approx.ftz.f64  d, a;"),
      PtxVersion(1, 0),
      SmTarget(20),
      Vector(InstructionForm("rcp.approx.ftz.f64", Vector(OperandConstraint.Register, OperandConstraint.Value), Vector(), _root_.scala.collection.immutable.Set(), true))
    ),
    InstructionSpec(
      "floating-point-instructions-sqrt",
      "9.7.3.15. Floating Point Instructions: sqrt",
      "sqrt",
      "Floating Point Instructions",
      Vector("sqrt"),
      Vector("sqrt.approx{.ftz}.f32  d, a; // fast, approximate square root\nsqrt.rnd{.ftz}.f32     d, a; // IEEE 754 compliant rounding\nsqrt.rnd.f64           d, a; // IEEE 754 compliant rounding\n\n.rnd = { .rn, .rz, .rm, .rp };"),
      PtxVersion(1, 0),
      SmTarget(13),
      Vector(InstructionForm("sqrt", Vector(OperandConstraint.Register, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("approx"), _root_.scala.collection.immutable.Set("f32")), _root_.scala.collection.immutable.Set("ftz"), false), InstructionForm("sqrt", Vector(OperandConstraint.Register, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("rm", "rn", "rp", "rz"), _root_.scala.collection.immutable.Set("f32")), _root_.scala.collection.immutable.Set("ftz"), false), InstructionForm("sqrt", Vector(OperandConstraint.Register, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("rm", "rn", "rp", "rz"), _root_.scala.collection.immutable.Set("f64")), _root_.scala.collection.immutable.Set(), false))
    ),
    InstructionSpec(
      "floating-point-instructions-rsqrt",
      "9.7.3.16. Floating Point Instructions: rsqrt",
      "rsqrt",
      "Floating Point Instructions",
      Vector("rsqrt"),
      Vector("rsqrt.approx{.ftz}.f32  d, a;\nrsqrt.approx.f64        d, a;"),
      PtxVersion(1, 0),
      SmTarget(13),
      Vector(InstructionForm("rsqrt", Vector(OperandConstraint.Register, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("approx"), _root_.scala.collection.immutable.Set("f32")), _root_.scala.collection.immutable.Set("ftz"), true), InstructionForm("rsqrt", Vector(OperandConstraint.Register, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("approx"), _root_.scala.collection.immutable.Set("f64")), _root_.scala.collection.immutable.Set(), true))
    ),
    InstructionSpec(
      "floating-point-instructions-rsqrt-approx-ftz-f64",
      "9.7.3.17. Floating Point Instructions: rsqrt.approx.ftz.f64",
      "rsqrt.approx.ftz.f64",
      "Floating Point Instructions",
      Vector("rsqrt.approx.ftz.f64"),
      Vector("rsqrt.approx.ftz.f64 d, a;"),
      PtxVersion(1, 0),
      SmTarget(20),
      Vector(InstructionForm("rsqrt.approx.ftz.f64", Vector(OperandConstraint.Register, OperandConstraint.Value), Vector(), _root_.scala.collection.immutable.Set(), true))
    )
)

private def instructionSpecChunk4: Vector[InstructionSpec] = Vector(
    InstructionSpec(
      "floating-point-instructions-sin",
      "9.7.3.18. Floating Point Instructions: sin",
      "sin",
      "Floating Point Instructions",
      Vector("sin"),
      Vector("sin.approx{.ftz}.f32  d, a;"),
      PtxVersion(1, 0),
      SmTarget(10),
      Vector(InstructionForm("sin", Vector(OperandConstraint.Register, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("approx"), _root_.scala.collection.immutable.Set("f32")), _root_.scala.collection.immutable.Set("ftz"), true))
    ),
    InstructionSpec(
      "floating-point-instructions-cos",
      "9.7.3.19. Floating Point Instructions: cos",
      "cos",
      "Floating Point Instructions",
      Vector("cos"),
      Vector("cos.approx{.ftz}.f32  d, a;"),
      PtxVersion(1, 0),
      SmTarget(10),
      Vector(InstructionForm("cos", Vector(OperandConstraint.Register, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("approx"), _root_.scala.collection.immutable.Set("f32")), _root_.scala.collection.immutable.Set("ftz"), true))
    ),
    InstructionSpec(
      "floating-point-instructions-lg2",
      "9.7.3.20. Floating Point Instructions: lg2",
      "lg2",
      "Floating Point Instructions",
      Vector("lg2"),
      Vector("lg2.approx{.ftz}.f32  d, a;"),
      PtxVersion(1, 0),
      SmTarget(10),
      Vector(InstructionForm("lg2", Vector(OperandConstraint.Register, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("approx"), _root_.scala.collection.immutable.Set("f32")), _root_.scala.collection.immutable.Set("ftz"), true))
    ),
    InstructionSpec(
      "floating-point-instructions-ex2",
      "9.7.3.21. Floating Point Instructions: ex2",
      "ex2",
      "Floating Point Instructions",
      Vector("ex2"),
      Vector("ex2.approx{.ftz}.f32  d, a;"),
      PtxVersion(1, 0),
      SmTarget(10),
      Vector(InstructionForm("ex2", Vector(OperandConstraint.Register, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("approx"), _root_.scala.collection.immutable.Set("f32")), _root_.scala.collection.immutable.Set("ftz"), true))
    ),
    InstructionSpec(
      "floating-point-instructions-tanh",
      "9.7.3.22. Floating Point Instructions: tanh",
      "tanh",
      "Floating Point Instructions",
      Vector("tanh"),
      Vector("tanh.approx.f32 d, a;"),
      PtxVersion(7, 0),
      SmTarget(75),
      Vector(InstructionForm("tanh", Vector(OperandConstraint.Register, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("approx"), _root_.scala.collection.immutable.Set("f32")), _root_.scala.collection.immutable.Set(), true))
    ),
    InstructionSpec(
      "half-precision-floating-point-instructions-add",
      "9.7.4.1. Half Precision Floating Point Instructions: add",
      "add",
      "Half Precision Floating Point Instructions",
      Vector("add"),
      Vector("add{.rnd}{.ftz}{.sat}.f16   d, a, b;\nadd{.rnd}{.ftz}{.sat}.f16x2 d, a, b;\n\nadd{.rnd}.bf16   d, a, b;\nadd{.rnd}.bf16x2 d, a, b;\n\n.rnd = { .rn };"),
      PtxVersion(4, 2),
      SmTarget(53),
      Vector(InstructionForm("add", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("f16")), _root_.scala.collection.immutable.Set("ftz", "rn", "sat"), false), InstructionForm("add", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("f16x2")), _root_.scala.collection.immutable.Set("ftz", "rn", "sat"), false), InstructionForm("add", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("bf16")), _root_.scala.collection.immutable.Set("rn"), false), InstructionForm("add", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("bf16x2")), _root_.scala.collection.immutable.Set("rn"), false))
    ),
    InstructionSpec(
      "half-precision-floating-point-instructions-sub",
      "9.7.4.2. Half Precision Floating Point Instructions: sub",
      "sub",
      "Half Precision Floating Point Instructions",
      Vector("sub"),
      Vector("sub{.rnd}{.ftz}{.sat}.f16   d, a, b;\nsub{.rnd}{.ftz}{.sat}.f16x2 d, a, b;\n\nsub{.rnd}.bf16   d, a, b;\nsub{.rnd}.bf16x2 d, a, b;\n\n.rnd = { .rn };"),
      PtxVersion(4, 2),
      SmTarget(53),
      Vector(InstructionForm("sub", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("f16")), _root_.scala.collection.immutable.Set("ftz", "rn", "sat"), false), InstructionForm("sub", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("f16x2")), _root_.scala.collection.immutable.Set("ftz", "rn", "sat"), false), InstructionForm("sub", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("bf16")), _root_.scala.collection.immutable.Set("rn"), false), InstructionForm("sub", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("bf16x2")), _root_.scala.collection.immutable.Set("rn"), false))
    ),
    InstructionSpec(
      "half-precision-floating-point-instructions-mul",
      "9.7.4.3. Half Precision Floating Point Instructions: mul",
      "mul",
      "Half Precision Floating Point Instructions",
      Vector("mul"),
      Vector("mul{.rnd}{.ftz}{.sat}.f16   d, a, b;\nmul{.rnd}{.ftz}{.sat}.f16x2 d, a, b;\n\nmul{.rnd}.bf16   d, a, b;\nmul{.rnd}.bf16x2 d, a, b;\n\n.rnd = { .rn };"),
      PtxVersion(4, 2),
      SmTarget(53),
      Vector(InstructionForm("mul", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("f16")), _root_.scala.collection.immutable.Set("ftz", "rn", "sat"), false), InstructionForm("mul", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("f16x2")), _root_.scala.collection.immutable.Set("ftz", "rn", "sat"), false), InstructionForm("mul", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("bf16")), _root_.scala.collection.immutable.Set("rn"), false), InstructionForm("mul", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("bf16x2")), _root_.scala.collection.immutable.Set("rn"), false))
    ),
    InstructionSpec(
      "half-precision-floating-point-instructions-fma",
      "9.7.4.4. Half Precision Floating Point Instructions: fma",
      "fma",
      "Half Precision Floating Point Instructions",
      Vector("fma"),
      Vector("fma.rnd{.ftz}{.sat}.f16     d, a, b, c;\nfma.rnd{.ftz}{.sat}.f16x2   d, a, b, c;\nfma.rnd{.ftz}.relu.f16      d, a, b, c;\nfma.rnd{.ftz}.relu.f16x2    d, a, b, c;\nfma.rnd{.relu}.bf16         d, a, b, c;\nfma.rnd{.relu}.bf16x2       d, a, b, c;\nfma.rnd.oob.{relu}.type     d, a, b, c;\n\n.rnd = { .rn };"),
      PtxVersion(4, 2),
      SmTarget(53),
      Vector(InstructionForm("fma", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("rn"), _root_.scala.collection.immutable.Set("f16")), _root_.scala.collection.immutable.Set("ftz", "sat"), false), InstructionForm("fma", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("rn"), _root_.scala.collection.immutable.Set("f16x2")), _root_.scala.collection.immutable.Set("ftz", "sat"), false), InstructionForm("fma", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("rn"), _root_.scala.collection.immutable.Set("relu"), _root_.scala.collection.immutable.Set("f16")), _root_.scala.collection.immutable.Set("ftz"), false), InstructionForm("fma", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("rn"), _root_.scala.collection.immutable.Set("relu"), _root_.scala.collection.immutable.Set("f16x2")), _root_.scala.collection.immutable.Set("ftz"), false), InstructionForm("fma", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("rn"), _root_.scala.collection.immutable.Set("bf16")), _root_.scala.collection.immutable.Set("relu"), false), InstructionForm("fma", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("rn"), _root_.scala.collection.immutable.Set("bf16x2")), _root_.scala.collection.immutable.Set("relu"), false), InstructionForm("fma", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("rn"), _root_.scala.collection.immutable.Set("oob"), _root_.scala.collection.immutable.Set("type")), _root_.scala.collection.immutable.Set(), false))
    ),
    InstructionSpec(
      "half-precision-floating-point-instructions-neg",
      "9.7.4.5. Half Precision Floating Point Instructions: neg",
      "neg",
      "Half Precision Floating Point Instructions",
      Vector("neg"),
      Vector("neg{.ftz}.f16    d, a;\nneg{.ftz}.f16x2  d, a;\nneg.bf16         d, a;\nneg.bf16x2       d, a;"),
      PtxVersion(6, 0),
      SmTarget(53),
      Vector(InstructionForm("neg", Vector(OperandConstraint.Register, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("f16")), _root_.scala.collection.immutable.Set("ftz"), false), InstructionForm("neg", Vector(OperandConstraint.Register, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("f16x2")), _root_.scala.collection.immutable.Set("ftz"), false), InstructionForm("neg", Vector(OperandConstraint.Register, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("bf16")), _root_.scala.collection.immutable.Set(), false), InstructionForm("neg", Vector(OperandConstraint.Register, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("bf16x2")), _root_.scala.collection.immutable.Set(), false))
    ),
    InstructionSpec(
      "half-precision-floating-point-instructions-abs",
      "9.7.4.6. Half Precision Floating Point Instructions: abs",
      "abs",
      "Half Precision Floating Point Instructions",
      Vector("abs"),
      Vector("abs{.ftz}.f16    d, a;\nabs{.ftz}.f16x2  d, a;\nabs.bf16         d, a;\nabs.bf16x2       d, a;"),
      PtxVersion(6, 5),
      SmTarget(53),
      Vector(InstructionForm("abs", Vector(OperandConstraint.Register, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("f16")), _root_.scala.collection.immutable.Set("ftz"), false), InstructionForm("abs", Vector(OperandConstraint.Register, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("f16x2")), _root_.scala.collection.immutable.Set("ftz"), false), InstructionForm("abs", Vector(OperandConstraint.Register, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("bf16")), _root_.scala.collection.immutable.Set(), false), InstructionForm("abs", Vector(OperandConstraint.Register, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("bf16x2")), _root_.scala.collection.immutable.Set(), false))
    ),
    InstructionSpec(
      "half-precision-floating-point-instructions-min",
      "9.7.4.7. Half Precision Floating Point Instructions: min",
      "min",
      "Half Precision Floating Point Instructions",
      Vector("min"),
      Vector("min{.ftz}{.NaN}{.xorsign.abs}.f16      d, a, b;\nmin{.ftz}{.NaN}{.xorsign.abs}.f16x2    d, a, b;\nmin{.NaN}{.xorsign.abs}.bf16           d, a, b;\nmin{.NaN}{.xorsign.abs}.bf16x2         d, a, b;"),
      PtxVersion(7, 0),
      SmTarget(80),
      Vector(InstructionForm("min", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("f16")), _root_.scala.collection.immutable.Set("NaN", "abs", "ftz", "xorsign"), false), InstructionForm("min", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("f16x2")), _root_.scala.collection.immutable.Set("NaN", "abs", "ftz", "xorsign"), false), InstructionForm("min", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("bf16")), _root_.scala.collection.immutable.Set("NaN", "abs", "xorsign"), false), InstructionForm("min", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("bf16x2")), _root_.scala.collection.immutable.Set("NaN", "abs", "xorsign"), false))
    )
)

private def instructionSpecChunk5: Vector[InstructionSpec] = Vector(
    InstructionSpec(
      "half-precision-floating-point-instructions-max",
      "9.7.4.8. Half Precision Floating Point Instructions: max",
      "max",
      "Half Precision Floating Point Instructions",
      Vector("max"),
      Vector("max{.ftz}{.NaN}{.xorsign.abs}.f16      d, a, b;\nmax{.ftz}{.NaN}{.xorsign.abs}.f16x2    d, a, b;\nmax{.NaN}{.xorsign.abs}.bf16           d, a, b;\nmax{.NaN}{.xorsign.abs}.bf16x2         d, a, b;"),
      PtxVersion(7, 0),
      SmTarget(80),
      Vector(InstructionForm("max", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("f16")), _root_.scala.collection.immutable.Set("NaN", "abs", "ftz", "xorsign"), false), InstructionForm("max", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("f16x2")), _root_.scala.collection.immutable.Set("NaN", "abs", "ftz", "xorsign"), false), InstructionForm("max", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("bf16")), _root_.scala.collection.immutable.Set("NaN", "abs", "xorsign"), false), InstructionForm("max", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("bf16x2")), _root_.scala.collection.immutable.Set("NaN", "abs", "xorsign"), false))
    ),
    InstructionSpec(
      "half-precision-floating-point-instructions-tanh",
      "9.7.4.9. Half Precision Floating Point Instructions: tanh",
      "tanh",
      "Half Precision Floating Point Instructions",
      Vector("tanh"),
      Vector("tanh.approx.type d, a;\n\n.type = {.f16, .f16x2, .bf16, .bf16x2}"),
      PtxVersion(7, 0),
      SmTarget(75),
      Vector(InstructionForm("tanh", Vector(OperandConstraint.Register, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("approx"), _root_.scala.collection.immutable.Set("bf16", "bf16x2", "f16", "f16x2")), _root_.scala.collection.immutable.Set(), false))
    ),
    InstructionSpec(
      "half-precision-floating-point-instructions-ex2",
      "9.7.4.10. Half Precision Floating Point Instructions: ex2",
      "ex2",
      "Half Precision Floating Point Instructions",
      Vector("ex2"),
      Vector("ex2.approx.atype     d, a;\nex2.approx.ftz.btype d, a;\n\n.atype = { .f16,  .f16x2}\n.btype = { .bf16, .bf16x2}"),
      PtxVersion(7, 0),
      SmTarget(75),
      Vector(InstructionForm("ex2", Vector(OperandConstraint.Register, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("approx"), _root_.scala.collection.immutable.Set("f16", "f16x2")), _root_.scala.collection.immutable.Set(), false), InstructionForm("ex2", Vector(OperandConstraint.Register, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("approx"), _root_.scala.collection.immutable.Set("ftz"), _root_.scala.collection.immutable.Set("bf16", "bf16x2")), _root_.scala.collection.immutable.Set(), false))
    ),
    InstructionSpec(
      "mixed-precision-floating-point-instructions-add",
      "9.7.5.1. Mixed Precision Floating Point Instructions: add",
      "add",
      "Mixed Precision Floating Point Instructions",
      Vector("add"),
      Vector("add{.rnd}{.sat}.f32.atype  d, a, c;\n\n.atype = { .f16, .bf16};\n.rnd   = { .rn, .rz, .rm, .rp };"),
      PtxVersion(1, 0),
      SmTarget(100),
      Vector(InstructionForm("add", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("f32"), _root_.scala.collection.immutable.Set("bf16", "f16")), _root_.scala.collection.immutable.Set("rm", "rn", "rp", "rz", "sat"), true))
    ),
    InstructionSpec(
      "mixed-precision-floating-point-instructions-sub",
      "9.7.5.2. Mixed Precision Floating Point Instructions: sub",
      "sub",
      "Mixed Precision Floating Point Instructions",
      Vector("sub"),
      Vector("sub{.rnd}{.sat}.f32.atype  d, a, c;\n\n.atype = { .f16, .bf16};\n.rnd   = { .rn, .rz, .rm, .rp };"),
      PtxVersion(1, 0),
      SmTarget(100),
      Vector(InstructionForm("sub", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("f32"), _root_.scala.collection.immutable.Set("bf16", "f16")), _root_.scala.collection.immutable.Set("rm", "rn", "rp", "rz", "sat"), true))
    ),
    InstructionSpec(
      "mixed-precision-floating-point-instructions-fma",
      "9.7.5.3. Mixed Precision Floating Point Instructions: fma",
      "fma",
      "Mixed Precision Floating Point Instructions",
      Vector("fma"),
      Vector("fma.rnd{.sat}.f32.abtype  d, a, b, c;\n\n.abtype = { .f16, .bf16};\n.rnd    = { .rn, .rz, .rm, .rp };"),
      PtxVersion(1, 0),
      SmTarget(100),
      Vector(InstructionForm("fma", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("rm", "rn", "rp", "rz"), _root_.scala.collection.immutable.Set("f32"), _root_.scala.collection.immutable.Set("bf16", "f16")), _root_.scala.collection.immutable.Set("sat"), true))
    ),
    InstructionSpec(
      "comparison-and-selection-instructions-set",
      "9.7.6.1. Comparison and Selection Instructions: set",
      "set",
      "Comparison and Selection Instructions",
      Vector("set"),
      Vector("set.CmpOp{.ftz}.dtype.stype         d, a, b;\nset.CmpOp.BoolOp{.ftz}.dtype.stype  d, a, b, {!}c;\n\n.CmpOp  = { eq, ne, lt, le, gt, ge, lo, ls, hi, hs,\n            equ, neu, ltu, leu, gtu, geu, num, nan };\n.BoolOp = { and, or, xor };\n.dtype  = { .u32, .s32, .f32 };\n.stype  = { .b16, .b32, .b64,\n            .u16, .u32, .u64,\n            .s16, .s32, .s64,\n                  .f32, .f64 };"),
      PtxVersion(1, 0),
      SmTarget(13),
      Vector(InstructionForm("set", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("eq", "equ", "ge", "geu", "gt", "gtu", "hi", "hs", "le", "leu", "lo", "ls", "lt", "ltu", "nan", "ne", "neu", "num"), _root_.scala.collection.immutable.Set("f32", "s32", "u32"), _root_.scala.collection.immutable.Set("b16", "b32", "b64", "f32", "f64", "s16", "s32", "s64", "u16", "u32", "u64")), _root_.scala.collection.immutable.Set("ftz"), true), InstructionForm("set", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("eq", "equ", "ge", "geu", "gt", "gtu", "hi", "hs", "le", "leu", "lo", "ls", "lt", "ltu", "nan", "ne", "neu", "num"), _root_.scala.collection.immutable.Set("and", "or", "xor"), _root_.scala.collection.immutable.Set("f32", "s32", "u32"), _root_.scala.collection.immutable.Set("b16", "b32", "b64", "f32", "f64", "s16", "s32", "s64", "u16", "u32", "u64")), _root_.scala.collection.immutable.Set("ftz"), true))
    ),
    InstructionSpec(
      "comparison-and-selection-instructions-setp",
      "9.7.6.2. Comparison and Selection Instructions: setp",
      "setp",
      "Comparison and Selection Instructions",
      Vector("setp"),
      Vector("setp.CmpOp{.ftz}.type         p[|q], a, b;\nsetp.CmpOp.BoolOp{.ftz}.type  p[|q], a, b, {!}c;\n\n.CmpOp  = { eq, ne, lt, le, gt, ge, lo, ls, hi, hs,\n            equ, neu, ltu, leu, gtu, geu, num, nan };\n.BoolOp = { and, or, xor };\n.type   = { .b16, .b32, .b64,\n            .u16, .u32, .u64,\n            .s16, .s32, .s64,\n                  .f32, .f64 };"),
      PtxVersion(1, 0),
      SmTarget(13),
      Vector(InstructionForm("setp", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("eq", "equ", "ge", "geu", "gt", "gtu", "hi", "hs", "le", "leu", "lo", "ls", "lt", "ltu", "nan", "ne", "neu", "num"), _root_.scala.collection.immutable.Set("b16", "b32", "b64", "f32", "f64", "s16", "s32", "s64", "u16", "u32", "u64")), _root_.scala.collection.immutable.Set("ftz"), true), InstructionForm("setp", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("eq", "equ", "ge", "geu", "gt", "gtu", "hi", "hs", "le", "leu", "lo", "ls", "lt", "ltu", "nan", "ne", "neu", "num"), _root_.scala.collection.immutable.Set("and", "or", "xor"), _root_.scala.collection.immutable.Set("b16", "b32", "b64", "f32", "f64", "s16", "s32", "s64", "u16", "u32", "u64")), _root_.scala.collection.immutable.Set("ftz"), true))
    ),
    InstructionSpec(
      "comparison-and-selection-instructions-selp",
      "9.7.6.3. Comparison and Selection Instructions: selp",
      "selp",
      "Comparison and Selection Instructions",
      Vector("selp"),
      Vector("selp.type d, a, b, c;\n\n.type = { .b16, .b32, .b64,\n          .u16, .u32, .u64,\n          .s16, .s32, .s64,\n                .f32, .f64 };"),
      PtxVersion(1, 0),
      SmTarget(13),
      Vector(InstructionForm("selp", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("b16", "b32", "b64", "f32", "f64", "s16", "s32", "s64", "u16", "u32", "u64")), _root_.scala.collection.immutable.Set(), true))
    ),
    InstructionSpec(
      "comparison-and-selection-instructions-slct",
      "9.7.6.4. Comparison and Selection Instructions: slct",
      "slct",
      "Comparison and Selection Instructions",
      Vector("slct"),
      Vector("slct.dtype.s32        d, a, b, c;\nslct{.ftz}.dtype.f32  d, a, b, c;\n\n.dtype = { .b16, .b32, .b64,\n           .u16, .u32, .u64,\n           .s16, .s32, .s64,\n                 .f32, .f64 };"),
      PtxVersion(1, 0),
      SmTarget(13),
      Vector(InstructionForm("slct", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("b16", "b32", "b64", "f32", "f64", "s16", "s32", "s64", "u16", "u32", "u64"), _root_.scala.collection.immutable.Set("s32")), _root_.scala.collection.immutable.Set(), true), InstructionForm("slct", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("b16", "b32", "b64", "f32", "f64", "s16", "s32", "s64", "u16", "u32", "u64"), _root_.scala.collection.immutable.Set("f32")), _root_.scala.collection.immutable.Set("ftz"), true))
    ),
    InstructionSpec(
      "half-precision-comparison-instructions-set",
      "9.7.7.1. Half Precision Comparison Instructions: set",
      "set",
      "Half Precision Comparison Instructions",
      Vector("set"),
      Vector("set.CmpOp{.ftz}.f16.stype            d, a, b;\nset.CmpOp.BoolOp{.ftz}.f16.stype     d, a, b, {!}c;\n\nset.CmpOp.bf16.stype                 d, a, b;\nset.CmpOp.BoolOp.bf16.stype          d, a, b, {!}c;\n\nset.CmpOp{.ftz}.dtype.f16            d, a, b;\nset.CmpOp.BoolOp{.ftz}.dtype.f16     d, a, b, {!}c;\n.dtype  = { .u16, .s16, .u32, .s32}\n\nset.CmpOp.dtype.bf16                 d, a, b;\nset.CmpOp.BoolOp.dtype.bf16          d, a, b, {!}c;\n.dtype  = { .u16, .s16, .u32, .s32}\n\nset.CmpOp{.ftz}.dtype.f16x2          d, a, b;\nset.CmpOp.BoolOp{.ftz}.dtype.f16x2   d, a, b, {!}c;\n.dtype  = { .f16x2, .u32, .s32}\n\nset.CmpOp.dtype.bf16x2               d, a, b;\nset.CmpOp.BoolOp.dtype.bf16x2        d, a, b, {!}c;\n.dtype  = { .bf16x2, .u32, .s32}\n\n.CmpOp  = { eq, ne, lt, le, gt, ge,\n            equ, neu, ltu, leu, gtu, geu, num, nan };\n.BoolOp = { and, or, xor };\n.stype  = { .b16, .b32, .b64,\n            .u16, .u32, .u64,\n            .s16, .s32, .s64,\n            .f16, .f32, .f64};"),
      PtxVersion(4, 2),
      SmTarget(53),
      Vector(InstructionForm("set", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("eq", "equ", "ge", "geu", "gt", "gtu", "le", "leu", "lt", "ltu", "nan", "ne", "neu", "num"), _root_.scala.collection.immutable.Set("f16"), _root_.scala.collection.immutable.Set("b16", "b32", "b64", "f16", "f32", "f64", "s16", "s32", "s64", "u16", "u32", "u64")), _root_.scala.collection.immutable.Set("ftz"), false), InstructionForm("set", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("eq", "equ", "ge", "geu", "gt", "gtu", "le", "leu", "lt", "ltu", "nan", "ne", "neu", "num"), _root_.scala.collection.immutable.Set("and", "or", "xor"), _root_.scala.collection.immutable.Set("f16"), _root_.scala.collection.immutable.Set("b16", "b32", "b64", "f16", "f32", "f64", "s16", "s32", "s64", "u16", "u32", "u64")), _root_.scala.collection.immutable.Set("ftz"), false), InstructionForm("set", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("eq", "equ", "ge", "geu", "gt", "gtu", "le", "leu", "lt", "ltu", "nan", "ne", "neu", "num"), _root_.scala.collection.immutable.Set("bf16"), _root_.scala.collection.immutable.Set("b16", "b32", "b64", "f16", "f32", "f64", "s16", "s32", "s64", "u16", "u32", "u64")), _root_.scala.collection.immutable.Set(), false), InstructionForm("set", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("eq", "equ", "ge", "geu", "gt", "gtu", "le", "leu", "lt", "ltu", "nan", "ne", "neu", "num"), _root_.scala.collection.immutable.Set("and", "or", "xor"), _root_.scala.collection.immutable.Set("bf16"), _root_.scala.collection.immutable.Set("b16", "b32", "b64", "f16", "f32", "f64", "s16", "s32", "s64", "u16", "u32", "u64")), _root_.scala.collection.immutable.Set(), false), InstructionForm("set", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("eq", "equ", "ge", "geu", "gt", "gtu", "le", "leu", "lt", "ltu", "nan", "ne", "neu", "num"), _root_.scala.collection.immutable.Set("bf16x2", "s32", "u32"), _root_.scala.collection.immutable.Set("f16")), _root_.scala.collection.immutable.Set("ftz"), false), InstructionForm("set", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("eq", "equ", "ge", "geu", "gt", "gtu", "le", "leu", "lt", "ltu", "nan", "ne", "neu", "num"), _root_.scala.collection.immutable.Set("and", "or", "xor"), _root_.scala.collection.immutable.Set("bf16x2", "s32", "u32"), _root_.scala.collection.immutable.Set("f16")), _root_.scala.collection.immutable.Set("ftz"), false), InstructionForm("set", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("eq", "equ", "ge", "geu", "gt", "gtu", "le", "leu", "lt", "ltu", "nan", "ne", "neu", "num"), _root_.scala.collection.immutable.Set("bf16x2", "s32", "u32"), _root_.scala.collection.immutable.Set("bf16")), _root_.scala.collection.immutable.Set(), false), InstructionForm("set", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("eq", "equ", "ge", "geu", "gt", "gtu", "le", "leu", "lt", "ltu", "nan", "ne", "neu", "num"), _root_.scala.collection.immutable.Set("and", "or", "xor"), _root_.scala.collection.immutable.Set("bf16x2", "s32", "u32"), _root_.scala.collection.immutable.Set("bf16")), _root_.scala.collection.immutable.Set(), false), InstructionForm("set", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("eq", "equ", "ge", "geu", "gt", "gtu", "le", "leu", "lt", "ltu", "nan", "ne", "neu", "num"), _root_.scala.collection.immutable.Set("bf16x2", "s32", "u32"), _root_.scala.collection.immutable.Set("f16x2")), _root_.scala.collection.immutable.Set("ftz"), false), InstructionForm("set", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("eq", "equ", "ge", "geu", "gt", "gtu", "le", "leu", "lt", "ltu", "nan", "ne", "neu", "num"), _root_.scala.collection.immutable.Set("and", "or", "xor"), _root_.scala.collection.immutable.Set("bf16x2", "s32", "u32"), _root_.scala.collection.immutable.Set("f16x2")), _root_.scala.collection.immutable.Set("ftz"), false), InstructionForm("set", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("eq", "equ", "ge", "geu", "gt", "gtu", "le", "leu", "lt", "ltu", "nan", "ne", "neu", "num"), _root_.scala.collection.immutable.Set("bf16x2", "s32", "u32"), _root_.scala.collection.immutable.Set("bf16x2")), _root_.scala.collection.immutable.Set(), false), InstructionForm("set", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("eq", "equ", "ge", "geu", "gt", "gtu", "le", "leu", "lt", "ltu", "nan", "ne", "neu", "num"), _root_.scala.collection.immutable.Set("and", "or", "xor"), _root_.scala.collection.immutable.Set("bf16x2", "s32", "u32"), _root_.scala.collection.immutable.Set("bf16x2")), _root_.scala.collection.immutable.Set(), false))
    ),
    InstructionSpec(
      "half-precision-comparison-instructions-setp",
      "9.7.7.2. Half Precision Comparison Instructions: setp",
      "setp",
      "Half Precision Comparison Instructions",
      Vector("setp"),
      Vector("setp.CmpOp{.ftz}.f16           p, a, b;\nsetp.CmpOp.BoolOp{.ftz}.f16    p, a, b, {!}c;\n\nsetp.CmpOp{.ftz}.f16x2         p|q, a, b;\nsetp.CmpOp.BoolOp{.ftz}.f16x2  p|q, a, b, {!}c;\n\nsetp.CmpOp.bf16                p, a, b;\nsetp.CmpOp.BoolOp.bf16         p, a, b, {!}c;\n\nsetp.CmpOp.bf16x2              p|q, a, b;\nsetp.CmpOp.BoolOp.bf16x2       p|q, a, b, {!}c;\n\n.CmpOp  = { eq, ne, lt, le, gt, ge,\n            equ, neu, ltu, leu, gtu, geu, num, nan };\n.BoolOp = { and, or, xor };"),
      PtxVersion(4, 2),
      SmTarget(53),
      Vector(InstructionForm("setp", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("eq", "equ", "ge", "geu", "gt", "gtu", "le", "leu", "lt", "ltu", "nan", "ne", "neu", "num"), _root_.scala.collection.immutable.Set("f16")), _root_.scala.collection.immutable.Set("ftz"), false), InstructionForm("setp", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("eq", "equ", "ge", "geu", "gt", "gtu", "le", "leu", "lt", "ltu", "nan", "ne", "neu", "num"), _root_.scala.collection.immutable.Set("and", "or", "xor"), _root_.scala.collection.immutable.Set("f16")), _root_.scala.collection.immutable.Set("ftz"), false), InstructionForm("setp", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("eq", "equ", "ge", "geu", "gt", "gtu", "le", "leu", "lt", "ltu", "nan", "ne", "neu", "num"), _root_.scala.collection.immutable.Set("f16x2")), _root_.scala.collection.immutable.Set("ftz"), false), InstructionForm("setp", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("eq", "equ", "ge", "geu", "gt", "gtu", "le", "leu", "lt", "ltu", "nan", "ne", "neu", "num"), _root_.scala.collection.immutable.Set("and", "or", "xor"), _root_.scala.collection.immutable.Set("f16x2")), _root_.scala.collection.immutable.Set("ftz"), false), InstructionForm("setp", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("eq", "equ", "ge", "geu", "gt", "gtu", "le", "leu", "lt", "ltu", "nan", "ne", "neu", "num"), _root_.scala.collection.immutable.Set("bf16")), _root_.scala.collection.immutable.Set(), false), InstructionForm("setp", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("eq", "equ", "ge", "geu", "gt", "gtu", "le", "leu", "lt", "ltu", "nan", "ne", "neu", "num"), _root_.scala.collection.immutable.Set("and", "or", "xor"), _root_.scala.collection.immutable.Set("bf16")), _root_.scala.collection.immutable.Set(), false), InstructionForm("setp", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("eq", "equ", "ge", "geu", "gt", "gtu", "le", "leu", "lt", "ltu", "nan", "ne", "neu", "num"), _root_.scala.collection.immutable.Set("bf16x2")), _root_.scala.collection.immutable.Set(), false), InstructionForm("setp", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("eq", "equ", "ge", "geu", "gt", "gtu", "le", "leu", "lt", "ltu", "nan", "ne", "neu", "num"), _root_.scala.collection.immutable.Set("and", "or", "xor"), _root_.scala.collection.immutable.Set("bf16x2")), _root_.scala.collection.immutable.Set(), false))
    )
)

private def instructionSpecChunk6: Vector[InstructionSpec] = Vector(
    InstructionSpec(
      "logic-and-shift-instructions-and",
      "9.7.8.1. Logic and Shift Instructions: and",
      "and",
      "Logic and Shift Instructions",
      Vector("and"),
      Vector("and.type d, a, b;\n\n.type = { .pred, .b16, .b32, .b64 };"),
      PtxVersion(1, 0),
      SmTarget(10),
      Vector(InstructionForm("and", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("b16", "b32", "b64", "pred")), _root_.scala.collection.immutable.Set(), true))
    ),
    InstructionSpec(
      "logic-and-shift-instructions-or",
      "9.7.8.2. Logic and Shift Instructions: or",
      "or",
      "Logic and Shift Instructions",
      Vector("or"),
      Vector("or.type d, a, b;\n\n.type = { .pred, .b16, .b32, .b64 };"),
      PtxVersion(1, 0),
      SmTarget(10),
      Vector(InstructionForm("or", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("b16", "b32", "b64", "pred")), _root_.scala.collection.immutable.Set(), true))
    ),
    InstructionSpec(
      "logic-and-shift-instructions-xor",
      "9.7.8.3. Logic and Shift Instructions: xor",
      "xor",
      "Logic and Shift Instructions",
      Vector("xor"),
      Vector("xor.type d, a, b;\n\n.type = { .pred, .b16, .b32, .b64 };"),
      PtxVersion(1, 0),
      SmTarget(10),
      Vector(InstructionForm("xor", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("b16", "b32", "b64", "pred")), _root_.scala.collection.immutable.Set(), true))
    ),
    InstructionSpec(
      "logic-and-shift-instructions-not",
      "9.7.8.4. Logic and Shift Instructions: not",
      "not",
      "Logic and Shift Instructions",
      Vector("not"),
      Vector("not.type d, a;\n\n.type = { .pred, .b16, .b32, .b64 };"),
      PtxVersion(1, 0),
      SmTarget(10),
      Vector(InstructionForm("not", Vector(OperandConstraint.Register, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("b16", "b32", "b64", "pred")), _root_.scala.collection.immutable.Set(), true))
    ),
    InstructionSpec(
      "logic-and-shift-instructions-cnot",
      "9.7.8.5. Logic and Shift Instructions: cnot",
      "cnot",
      "Logic and Shift Instructions",
      Vector("cnot"),
      Vector("cnot.type d, a;\n\n.type = { .b16, .b32, .b64 };"),
      PtxVersion(1, 0),
      SmTarget(10),
      Vector(InstructionForm("cnot", Vector(OperandConstraint.Register, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("b16", "b32", "b64")), _root_.scala.collection.immutable.Set(), true))
    ),
    InstructionSpec(
      "logic-and-shift-instructions-lop3",
      "9.7.8.6. Logic and Shift Instructions: lop3",
      "lop3",
      "Logic and Shift Instructions",
      Vector("lop3"),
      Vector("lop3.b32 d, a, b, c, immLut;\nlop3.BoolOp.b32 d|p, a, b, c, immLut, q;\n\n.BoolOp   = { .or , .and };"),
      PtxVersion(4, 3),
      SmTarget(50),
      Vector(InstructionForm("lop3", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("b32")), _root_.scala.collection.immutable.Set(), false), InstructionForm("lop3", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Register), Vector(_root_.scala.collection.immutable.Set("and", "or"), _root_.scala.collection.immutable.Set("b32")), _root_.scala.collection.immutable.Set(), false))
    ),
    InstructionSpec(
      "logic-and-shift-instructions-shf",
      "9.7.8.7. Logic and Shift Instructions: shf",
      "shf",
      "Logic and Shift Instructions",
      Vector("shf"),
      Vector("shf.l.mode.b32  d, a, b, c;  // left shift\nshf.r.mode.b32  d, a, b, c;  // right shift\n\n.mode = { .clamp, .wrap };"),
      PtxVersion(3, 1),
      SmTarget(32),
      Vector(InstructionForm("shf", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("l"), _root_.scala.collection.immutable.Set("clamp", "wrap"), _root_.scala.collection.immutable.Set("b32")), _root_.scala.collection.immutable.Set(), true), InstructionForm("shf", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("r"), _root_.scala.collection.immutable.Set("clamp", "wrap"), _root_.scala.collection.immutable.Set("b32")), _root_.scala.collection.immutable.Set(), true))
    ),
    InstructionSpec(
      "logic-and-shift-instructions-shl",
      "9.7.8.8. Logic and Shift Instructions: shl",
      "shl",
      "Logic and Shift Instructions",
      Vector("shl"),
      Vector("shl.type d, a, b;\n\n.type = { .b16, .b32, .b64 };"),
      PtxVersion(1, 0),
      SmTarget(10),
      Vector(InstructionForm("shl", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("b16", "b32", "b64")), _root_.scala.collection.immutable.Set(), true))
    ),
    InstructionSpec(
      "logic-and-shift-instructions-shr",
      "9.7.8.9. Logic and Shift Instructions: shr",
      "shr",
      "Logic and Shift Instructions",
      Vector("shr"),
      Vector("shr.type d, a, b;\n\n.type = { .b16, .b32, .b64,\n          .u16, .u32, .u64,\n          .s16, .s32, .s64 };"),
      PtxVersion(1, 0),
      SmTarget(10),
      Vector(InstructionForm("shr", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("b16", "b32", "b64", "s16", "s32", "s64", "u16", "u32", "u64")), _root_.scala.collection.immutable.Set(), true))
    ),
    InstructionSpec(
      "data-movement-and-conversion-instructions-mov",
      "9.7.9.3. Data Movement and Conversion Instructions: mov",
      "mov",
      "Data Movement and Conversion Instructions",
      Vector("mov"),
      Vector("mov.type  d, a;\nmov.type  d, sreg;\nmov.type  d, avar;       // get address of variable\nmov.type  d, avar+imm;   // get address of variable with offset\nmov.u32   d, fname;      // get address of device function\nmov.u64   d, fname;      // get address of device function\nmov.u32   d, kernel;     // get address of entry function\nmov.u64   d, kernel;     // get address of entry function\n\n.type = { .pred,\n          .b16, .b32, .b64,\n          .u16, .u32, .u64,\n          .s16, .s32, .s64,\n                .f32, .f64 };"),
      PtxVersion(1, 0),
      SmTarget(13),
      Vector(InstructionForm("mov", Vector(OperandConstraint.Register, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("b16", "b32", "b64", "f32", "f64", "pred", "s16", "s32", "s64", "u16", "u32", "u64")), _root_.scala.collection.immutable.Set(), false), InstructionForm("mov", Vector(OperandConstraint.Register, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("b16", "b32", "b64", "f32", "f64", "pred", "s16", "s32", "s64", "u16", "u32", "u64")), _root_.scala.collection.immutable.Set(), false), InstructionForm("mov", Vector(OperandConstraint.Register, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("b16", "b32", "b64", "f32", "f64", "pred", "s16", "s32", "s64", "u16", "u32", "u64")), _root_.scala.collection.immutable.Set(), false), InstructionForm("mov", Vector(OperandConstraint.Register, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("b16", "b32", "b64", "f32", "f64", "pred", "s16", "s32", "s64", "u16", "u32", "u64")), _root_.scala.collection.immutable.Set(), false), InstructionForm("mov", Vector(OperandConstraint.Register, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("u32")), _root_.scala.collection.immutable.Set(), false), InstructionForm("mov", Vector(OperandConstraint.Register, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("u64")), _root_.scala.collection.immutable.Set(), false), InstructionForm("mov", Vector(OperandConstraint.Register, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("u32")), _root_.scala.collection.immutable.Set(), false), InstructionForm("mov", Vector(OperandConstraint.Register, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("u64")), _root_.scala.collection.immutable.Set(), false))
    ),
    InstructionSpec(
      "data-movement-and-conversion-instructions-mov-2",
      "9.7.9.4. Data Movement and Conversion Instructions: mov",
      "mov",
      "Data Movement and Conversion Instructions",
      Vector("mov"),
      Vector("mov.type  d, a;\n\n.type = { .b16, .b32, .b64, .b128 };"),
      PtxVersion(1, 0),
      SmTarget(10),
      Vector(InstructionForm("mov", Vector(OperandConstraint.Register, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("b128", "b16", "b32", "b64")), _root_.scala.collection.immutable.Set(), false))
    ),
    InstructionSpec(
      "data-movement-and-conversion-instructions-shfl",
      "9.7.9.5. Data Movement and Conversion Instructions: shfl (deprecated)",
      "shfl (deprecated)",
      "Data Movement and Conversion Instructions",
      Vector("shfl"),
      Vector("shfl.mode.b32  d[|p], a, b, c;\n\n.mode = { .up, .down, .bfly, .idx };"),
      PtxVersion(3, 0),
      SmTarget(30),
      Vector(InstructionForm("shfl", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("bfly", "down", "idx", "up"), _root_.scala.collection.immutable.Set("b32")), _root_.scala.collection.immutable.Set(), false))
    )
)

private def instructionSpecChunk7: Vector[InstructionSpec] = Vector(
    InstructionSpec(
      "data-movement-and-conversion-instructions-shfl-sync",
      "9.7.9.6. Data Movement and Conversion Instructions: shfl.sync",
      "shfl.sync",
      "Data Movement and Conversion Instructions",
      Vector("shfl.sync"),
      Vector("shfl.sync.mode.b32  d[|p], a, b, c, membermask;\n\n.mode = { .up, .down, .bfly, .idx };"),
      PtxVersion(6, 0),
      SmTarget(30),
      Vector(InstructionForm("shfl.sync", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("bfly", "down", "idx", "up"), _root_.scala.collection.immutable.Set("b32")), _root_.scala.collection.immutable.Set(), true))
    ),
    InstructionSpec(
      "data-movement-and-conversion-instructions-prmt",
      "9.7.9.7. Data Movement and Conversion Instructions: prmt",
      "prmt",
      "Data Movement and Conversion Instructions",
      Vector("prmt"),
      Vector("prmt.b32{.mode}  d, a, b, c;\n\n.mode = { .f4e, .b4e, .rc8, .ecl, .ecr, .rc16 };"),
      PtxVersion(2, 0),
      SmTarget(20),
      Vector(InstructionForm("prmt", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("b32")), _root_.scala.collection.immutable.Set("b4e", "ecl", "ecr", "f4e", "rc16", "rc8"), true))
    ),
    InstructionSpec(
      "data-movement-and-conversion-instructions-ld",
      "9.7.9.8. Data Movement and Conversion Instructions: ld",
      "ld",
      "Data Movement and Conversion Instructions",
      Vector("ld"),
      Vector("ld{.weak}{.ss}{.cop}{.level::cache_hint}{.level::prefetch_size}{.vec}.type  d, [a]{.unified}{, cache_policy};\n\nld{.weak}{.ss}{.level1::eviction_priority}{.level2::eviction_priority}{.level::cache_hint}{.level::prefetch_size}{.vec}.type  d, [a]{.unified}{, cache_policy};\n\nld.volatile{.ss}{.level::prefetch_size}{.vec}.type  d, [a];\n\nld.relaxed.scope{.ss}{.level1::eviction_priority}{.level2::eviction_priority}{.level::cache_hint}{.level::prefetch_size}{.vec}.type  d, [a]{, cache_policy};\n\nld.acquire.scope{.ss}{.level1::eviction_priority}{.level2::eviction_priority}{.level::cache_hint}{.level::prefetch_size}{.vec}.type  d, [a]{, cache_policy};\n\nld.mmio.sem.sys{.global}.type  d, [a];\n\n.ss =                       { .const, .global, .local, .param{::entry, ::func}, .shared{::cta, ::cluster} };\n.cop =                      { .ca, .cg, .cs, .lu, .cv };\n.sem =                      { .acquire, .relaxed };\n.level1::eviction_priority = { .L1::evict_normal, .L1::evict_unchanged,\n                               .L1::evict_first, .L1::evict_last, .L1::no_allocate };\n.level2::eviction_priority = {.L2::evict_normal, .L2::evict_first, .L2::evict_last};\n.level::cache_hint =        { .L2::cache_hint };\n.level::prefetch_size =     { .L2::64B, .L2::128B, .L2::256B }\n.scope =                    { .cta, .cluster, .gpu, .sys };\n.vec =                      { .v2, .v4, .v8 };\n.type =                     { .b8, .b16, .b32, .b64, .b128,\n                              .u8, .u16, .u32, .u64,\n                              .s8, .s16, .s32, .s64,\n                              .f32, .f64 };"),
      PtxVersion(1, 0),
      SmTarget(13),
      Vector(InstructionForm("ld", Vector(OperandConstraint.Register, OperandConstraint.Address, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("b128", "b16", "b32", "b64", "b8", "f32", "f64", "s16", "s32", "s64", "s8", "u16", "u32", "u64", "u8")), _root_.scala.collection.immutable.Set("L2::cache_hint", "ca", "cg", "const", "cs", "cv", "global", "local", "lu", "v2", "v4", "v8", "weak"), false), InstructionForm("ld", Vector(OperandConstraint.Register, OperandConstraint.Address, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("b128", "b16", "b32", "b64", "b8", "f32", "f64", "s16", "s32", "s64", "s8", "u16", "u32", "u64", "u8")), _root_.scala.collection.immutable.Set("L1::evict_first", "L1::evict_last", "L1::evict_normal", "L1::evict_unchanged", "L1::no_allocate", "L2::cache_hint", "L2::evict_first", "L2::evict_last", "L2::evict_normal", "const", "global", "local", "v2", "v4", "v8", "weak"), false), InstructionForm("ld", Vector(OperandConstraint.Register, OperandConstraint.Address), Vector(_root_.scala.collection.immutable.Set("volatile"), _root_.scala.collection.immutable.Set("b128", "b16", "b32", "b64", "b8", "f32", "f64", "s16", "s32", "s64", "s8", "u16", "u32", "u64", "u8")), _root_.scala.collection.immutable.Set("const", "global", "local", "v2", "v4", "v8"), false), InstructionForm("ld", Vector(OperandConstraint.Register, OperandConstraint.Address, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("relaxed"), _root_.scala.collection.immutable.Set("cluster", "cta", "gpu", "sys"), _root_.scala.collection.immutable.Set("b128", "b16", "b32", "b64", "b8", "f32", "f64", "s16", "s32", "s64", "s8", "u16", "u32", "u64", "u8")), _root_.scala.collection.immutable.Set("L1::evict_first", "L1::evict_last", "L1::evict_normal", "L1::evict_unchanged", "L1::no_allocate", "L2::cache_hint", "L2::evict_first", "L2::evict_last", "L2::evict_normal", "const", "global", "local", "v2", "v4", "v8"), false), InstructionForm("ld", Vector(OperandConstraint.Register, OperandConstraint.Address, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("acquire"), _root_.scala.collection.immutable.Set("cluster", "cta", "gpu", "sys"), _root_.scala.collection.immutable.Set("b128", "b16", "b32", "b64", "b8", "f32", "f64", "s16", "s32", "s64", "s8", "u16", "u32", "u64", "u8")), _root_.scala.collection.immutable.Set("L1::evict_first", "L1::evict_last", "L1::evict_normal", "L1::evict_unchanged", "L1::no_allocate", "L2::cache_hint", "L2::evict_first", "L2::evict_last", "L2::evict_normal", "const", "global", "local", "v2", "v4", "v8"), false), InstructionForm("ld", Vector(OperandConstraint.Register, OperandConstraint.Address), Vector(_root_.scala.collection.immutable.Set("mmio"), _root_.scala.collection.immutable.Set("acquire", "relaxed"), _root_.scala.collection.immutable.Set("sys"), _root_.scala.collection.immutable.Set("b128", "b16", "b32", "b64", "b8", "f32", "f64", "s16", "s32", "s64", "s8", "u16", "u32", "u64", "u8")), _root_.scala.collection.immutable.Set("global"), false))
    ),
    InstructionSpec(
      "data-movement-and-conversion-instructions-ld-global-nc",
      "9.7.9.9. Data Movement and Conversion Instructions: ld.global.nc",
      "ld.global.nc",
      "Data Movement and Conversion Instructions",
      Vector("ld.global.nc"),
      Vector("ld.global{.cop}.nc{.level::cache_hint}{.level::prefetch_size}.type                 d, [a]{, cache_policy};\nld.global{.cop}.nc{.level::cache_hint}{.level::prefetch_size}.vec.type             d, [a]{, cache_policy};\n\nld.global.nc{.level1::eviction_priority}{.level2::eviction_priority}{.level::cache_hint}{.level::prefetch_size}.type      d, [a]{, cache_policy};\nld.global.nc{.level1::eviction_priority}{.level2::eviction_priority}{.level::cache_hint}{.level::prefetch_size}.vec.type  d, [a]{, cache_policy};\n\n.cop  =                     { .ca, .cg, .cs };     // cache operation\n.level1::eviction_priority = { .L1::evict_normal, .L1::evict_unchanged,\n                               .L1::evict_first, .L1::evict_last, .L1::no_allocate};\n.level2::eviction_priority = {.L2::evict_normal, .L2::evict_first, .L2::evict_last};\n.level::cache_hint =        { .L2::cache_hint };\n.level::prefetch_size =     { .L2::64B, .L2::128B, .L2::256B }\n.vec  =                     { .v2, .v4, .v8 };\n.type =                     { .b8, .b16, .b32, .b64, .b128,\n                              .u8, .u16, .u32, .u64,\n                              .s8, .s16, .s32, .s64,\n                              .f32, .f64 };"),
      PtxVersion(3, 1),
      SmTarget(32),
      Vector(InstructionForm("ld.global.nc", Vector(OperandConstraint.Register, OperandConstraint.Address, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("b128", "b16", "b32", "b64", "b8", "f32", "f64", "s16", "s32", "s64", "s8", "u16", "u32", "u64", "u8")), _root_.scala.collection.immutable.Set("L2::cache_hint", "ca", "cg", "cs"), false), InstructionForm("ld.global.nc", Vector(OperandConstraint.Register, OperandConstraint.Address, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("v2", "v4", "v8"), _root_.scala.collection.immutable.Set("b128", "b16", "b32", "b64", "b8", "f32", "f64", "s16", "s32", "s64", "s8", "u16", "u32", "u64", "u8")), _root_.scala.collection.immutable.Set("L2::cache_hint", "ca", "cg", "cs"), false), InstructionForm("ld.global.nc", Vector(OperandConstraint.Register, OperandConstraint.Address, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("b128", "b16", "b32", "b64", "b8", "f32", "f64", "s16", "s32", "s64", "s8", "u16", "u32", "u64", "u8")), _root_.scala.collection.immutable.Set("L1::evict_first", "L1::evict_last", "L1::evict_normal", "L1::evict_unchanged", "L1::no_allocate", "L2::cache_hint", "L2::evict_first", "L2::evict_last", "L2::evict_normal"), false), InstructionForm("ld.global.nc", Vector(OperandConstraint.Register, OperandConstraint.Address, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("v2", "v4", "v8"), _root_.scala.collection.immutable.Set("b128", "b16", "b32", "b64", "b8", "f32", "f64", "s16", "s32", "s64", "s8", "u16", "u32", "u64", "u8")), _root_.scala.collection.immutable.Set("L1::evict_first", "L1::evict_last", "L1::evict_normal", "L1::evict_unchanged", "L1::no_allocate", "L2::cache_hint", "L2::evict_first", "L2::evict_last", "L2::evict_normal"), false))
    ),
    InstructionSpec(
      "data-movement-and-conversion-instructions-ldu",
      "9.7.9.10. Data Movement and Conversion Instructions: ldu",
      "ldu",
      "Data Movement and Conversion Instructions",
      Vector("ldu"),
      Vector("ldu{.ss}.type      d, [a];       // load from address\nldu{.ss}.vec.type  d, [a];       // vec load from address\n\n.ss   = { .global };             // state space\n.vec  = { .v2, .v4 };\n.type = { .b8, .b16, .b32, .b64, .b128,\n          .u8, .u16, .u32, .u64,\n          .s8, .s16, .s32, .s64,\n                     .f32, .f64 };"),
      PtxVersion(2, 0),
      SmTarget(13),
      Vector(InstructionForm("ldu", Vector(OperandConstraint.Register, OperandConstraint.Address), Vector(_root_.scala.collection.immutable.Set("b128", "b16", "b32", "b64", "b8", "f32", "f64", "s16", "s32", "s64", "s8", "u16", "u32", "u64", "u8")), _root_.scala.collection.immutable.Set("global"), false), InstructionForm("ldu", Vector(OperandConstraint.Register, OperandConstraint.Address), Vector(_root_.scala.collection.immutable.Set("v2", "v4"), _root_.scala.collection.immutable.Set("b128", "b16", "b32", "b64", "b8", "f32", "f64", "s16", "s32", "s64", "s8", "u16", "u32", "u64", "u8")), _root_.scala.collection.immutable.Set("global"), false))
    ),
    InstructionSpec(
      "data-movement-and-conversion-instructions-st",
      "9.7.9.11. Data Movement and Conversion Instructions: st",
      "st",
      "Data Movement and Conversion Instructions",
      Vector("st"),
      Vector("st{.weak}{.ss}{.cop}{.level::cache_hint}{.vec}.type   [a], b{, cache_policy};\nst{.weak}{.ss}{.level1::eviction_priority}{.level2::eviction_priority}{.level::cache_hint}{.vec}.type\n                                                      [a], b{, cache_policy};\nst.volatile{.ss}{.vec}.type                           [a], b;\nst.relaxed.scope{.ss}{.level1::eviction_priority}{.level2::eviction_priority}{.level::cache_hint}{.vec}.type\n                                                      [a], b{, cache_policy};\nst.release.scope{.ss}{.level1::eviction_priority}{.level2::eviction_priority}{.level::cache_hint}{.vec}.type\n                                                      [a], b{, cache_policy};\nst.mmio.sem.sys{.global}.type         [a], b;\n\n.ss =                       { .global, .local, .param{::func}, .shared{::cta, ::cluster} };\n.sem =                      { .release, .relaxed };\n.level1::eviction_priority = { .L1::evict_normal, .L1::evict_unchanged,\n                               .L1::evict_first, .L1::evict_last, .L1::no_allocate };\n.level2::eviction_priority = { .L2::evict_normal, .L2::evict_first, .L2::evict_last };\n.level::cache_hint =        { .L2::cache_hint };\n.cop =                      { .wb, .cg, .cs, .wt };\n.sem =                      { .relaxed, .release };\n.scope =                    { .cta, .cluster, .gpu, .sys };\n.vec =                      { .v2, .v4, .v8 };\n.type =                     { .b8, .b16, .b32, .b64, .b128,\n                              .u8, .u16, .u32, .u64,\n                              .s8, .s16, .s32, .s64,\n                              .f32, .f64 };"),
      PtxVersion(1, 0),
      SmTarget(13),
      Vector(InstructionForm("st", Vector(OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("b128", "b16", "b32", "b64", "b8", "f32", "f64", "s16", "s32", "s64", "s8", "u16", "u32", "u64", "u8")), _root_.scala.collection.immutable.Set("L2::cache_hint", "cg", "cs", "global", "local", "v2", "v4", "v8", "wb", "weak", "wt"), false), InstructionForm("st", Vector(OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("b128", "b16", "b32", "b64", "b8", "f32", "f64", "s16", "s32", "s64", "s8", "u16", "u32", "u64", "u8")), _root_.scala.collection.immutable.Set("L1::evict_first", "L1::evict_last", "L1::evict_normal", "L1::evict_unchanged", "L1::no_allocate", "L2::cache_hint", "L2::evict_first", "L2::evict_last", "L2::evict_normal", "global", "local", "v2", "v4", "v8", "weak"), false), InstructionForm("st", Vector(OperandConstraint.Address, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("volatile"), _root_.scala.collection.immutable.Set("b128", "b16", "b32", "b64", "b8", "f32", "f64", "s16", "s32", "s64", "s8", "u16", "u32", "u64", "u8")), _root_.scala.collection.immutable.Set("global", "local", "v2", "v4", "v8"), false), InstructionForm("st", Vector(OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("relaxed"), _root_.scala.collection.immutable.Set("cluster", "cta", "gpu", "sys"), _root_.scala.collection.immutable.Set("b128", "b16", "b32", "b64", "b8", "f32", "f64", "s16", "s32", "s64", "s8", "u16", "u32", "u64", "u8")), _root_.scala.collection.immutable.Set("L1::evict_first", "L1::evict_last", "L1::evict_normal", "L1::evict_unchanged", "L1::no_allocate", "L2::cache_hint", "L2::evict_first", "L2::evict_last", "L2::evict_normal", "global", "local", "v2", "v4", "v8"), false), InstructionForm("st", Vector(OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("release"), _root_.scala.collection.immutable.Set("cluster", "cta", "gpu", "sys"), _root_.scala.collection.immutable.Set("b128", "b16", "b32", "b64", "b8", "f32", "f64", "s16", "s32", "s64", "s8", "u16", "u32", "u64", "u8")), _root_.scala.collection.immutable.Set("L1::evict_first", "L1::evict_last", "L1::evict_normal", "L1::evict_unchanged", "L1::no_allocate", "L2::cache_hint", "L2::evict_first", "L2::evict_last", "L2::evict_normal", "global", "local", "v2", "v4", "v8"), false), InstructionForm("st", Vector(OperandConstraint.Address, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("mmio"), _root_.scala.collection.immutable.Set("relaxed", "release"), _root_.scala.collection.immutable.Set("sys"), _root_.scala.collection.immutable.Set("b128", "b16", "b32", "b64", "b8", "f32", "f64", "s16", "s32", "s64", "s8", "u16", "u32", "u64", "u8")), _root_.scala.collection.immutable.Set("global"), false))
    ),
    InstructionSpec(
      "data-movement-and-conversion-instructions-st-async",
      "9.7.9.12. Data Movement and Conversion Instructions: st.async",
      "st.async",
      "Data Movement and Conversion Instructions",
      Vector("st.async"),
      Vector("st.async{.weak}{.ss}.completion_mechanism{.vec}.type [a], b, [mbar];\n\n.ss   =                 { .shared::cluster };\n.type =                 { .b32, .b64, .b128,\n                          .u32, .u64,\n                          .s32, .s64,\n                          .f32, .f64 };\n.vec  =                 { .v2, .v4 };\n.completion_mechanism = { .mbarrier::complete_tx::bytes };\n\nst.async{.mmio}.sem.scope{.ss}.type [a], b;\n\n.sem =                  { .release };\n.scope =                { .gpu, .sys };\n.ss =                   { .global };\n.type =                 { .b8, .b16, .b32, .b64,\n                          .u8, .u16, .u32, .u64,\n                          .s8, .s16, .s32, .s64,\n                                     .f32, .f64 };"),
      PtxVersion(8, 1),
      SmTarget(90),
      Vector(InstructionForm("st.async", Vector(OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Address), Vector(_root_.scala.collection.immutable.Set("mbarrier::complete_tx::bytes"), _root_.scala.collection.immutable.Set("b16", "b32", "b64", "b8", "f32", "f64", "s16", "s32", "s64", "s8", "u16", "u32", "u64", "u8")), _root_.scala.collection.immutable.Set("global", "v2", "v4", "weak"), false), InstructionForm("st.async", Vector(OperandConstraint.Address, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("release"), _root_.scala.collection.immutable.Set("gpu", "sys"), _root_.scala.collection.immutable.Set("b16", "b32", "b64", "b8", "f32", "f64", "s16", "s32", "s64", "s8", "u16", "u32", "u64", "u8")), _root_.scala.collection.immutable.Set("global", "mmio"), false))
    ),
    InstructionSpec(
      "data-movement-and-conversion-instructions-multimem-st-async",
      "9.7.9.13. Data Movement and Conversion Instructions: multimem.st.async",
      "multimem.st.async",
      "Data Movement and Conversion Instructions",
      Vector("multimem.st.async"),
      Vector("multimem.st.async.sem.scope{.ss}.type [a], b;\n\n.sem   = { .release };\n.scope = { .gpu, .sys };\n.ss    = { .global };\n.type  = { .b8, .b16, .b32, .b64,\n            .u8, .u16, .u32, .u64,\n            .s8, .s16, .s32, .s64,\n            .f32, .f64 };"),
      PtxVersion(9, 3),
      SmTarget(100),
      Vector(InstructionForm("multimem.st.async", Vector(OperandConstraint.Address, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("release"), _root_.scala.collection.immutable.Set("gpu", "sys"), _root_.scala.collection.immutable.Set("b16", "b32", "b64", "b8", "f32", "f64", "s16", "s32", "s64", "s8", "u16", "u32", "u64", "u8")), _root_.scala.collection.immutable.Set("global"), true))
    ),
    InstructionSpec(
      "data-movement-and-conversion-instructions-st-bulk",
      "9.7.9.14. Data Movement and Conversion Instructions: st.bulk",
      "st.bulk",
      "Data Movement and Conversion Instructions",
      Vector("st.bulk"),
      Vector("st.bulk{.weak}{.shared::cta}  [a], size, initval; // initval must be zero"),
      PtxVersion(8, 6),
      SmTarget(100),
      Vector(InstructionForm("st.bulk", Vector(OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value), Vector(), _root_.scala.collection.immutable.Set("shared::cta", "weak"), true))
    ),
    InstructionSpec(
      "data-movement-and-conversion-instructions-multimem",
      "9.7.9.15. Data Movement and Conversion Instructions: multimem.ld_reduce, multimem.st, multimem.red",
      "multimem.ld_reduce, multimem.st, multimem.red",
      "Data Movement and Conversion Instructions",
      Vector("multimem.ld_reduce", "multimem.st", "multimem.red"),
      Vector("// Integer type:\n\nmultimem.ld_reduce{.ldsem}{.scope}{.ss}.op.type      d, [a];\nmultimem.ld_reduce.weak{.ss}.op.type                 d, [a];\n\nmultimem.st{.stsem}{.scope}{.ss}.type                [a], b;\nmultimem.st.weak{.ss}.type                           [a], b;\n\nmultimem.red{.redsem}{.scope}{.ss}.op.type           [a], b;\n\n.ss =       { .global }\n.ldsem =    { .relaxed, .acquire }\n.stsem =    { .relaxed, .release }\n.redsem =   { .relaxed, .release }\n.scope =    { .cta, .cluster, .gpu, .sys }\n.op  =      { .min, .max, .add, .and, .or, .xor }\n.type =     { .b32, .b64,  .u32, .u64, .s32, .s64 }\n\n// Floating point type:\n\nmultimem.ld_reduce{.ldsem}{.scope}{.ss}.op{.acc_prec}{.vec}.type    d, [a];\nmultimem.ld_reduce.weak{.ss}.op{.acc_prec}{.vec}.type               d, [a];\n\nmultimem.st{.stsem}{.scope}{.ss}{.vec}.type                         [a], b;\nmultimem.st.weak{.ss}{.vec}.type                                    [a], b;\n\nmultimem.red{.redsem}{.scope}{.ss}.redop{.vec}.redtype              [a], b;\n\n.ss =       { .global }\n.ldsem =    { .relaxed, .acquire }\n.stsem =    { .relaxed, .release }\n.redsem =   { .relaxed, .release }\n.scope =    { .cta, .cluster, .gpu, .sys }\n.op  =      { .min, .max, .add }\n.redop  =   { .add }\n.acc_prec = { .acc::f32, .acc::f16 }\n.vec =      { .v2, .v4, .v8 }\n.type=      { .f16, .f16x2, .bf16, .bf16x2, .f32, .f64, .e5m2, .e5m2x2, .e5m2x4, .e4m3, .e4m3x2, .e4m3x4 }\n.redtype =  { .f16, .f16x2, .bf16, .bf16x2, .f32, .f64 }"),
      PtxVersion(8, 1),
      SmTarget(90),
      Vector(InstructionForm("multimem.ld_reduce", Vector(OperandConstraint.Register, OperandConstraint.Address), Vector(_root_.scala.collection.immutable.Set("add", "max", "min"), _root_.scala.collection.immutable.Set("bf16", "bf16x2", "e4m3", "e4m3x2", "e4m3x4", "e5m2", "e5m2x2", "e5m2x4", "f16", "f16x2", "f32", "f64")), _root_.scala.collection.immutable.Set("acquire", "cluster", "cta", "global", "gpu", "relaxed", "sys"), false), InstructionForm("multimem.ld_reduce", Vector(OperandConstraint.Register, OperandConstraint.Address), Vector(_root_.scala.collection.immutable.Set("weak"), _root_.scala.collection.immutable.Set("add", "max", "min"), _root_.scala.collection.immutable.Set("bf16", "bf16x2", "e4m3", "e4m3x2", "e4m3x4", "e5m2", "e5m2x2", "e5m2x4", "f16", "f16x2", "f32", "f64")), _root_.scala.collection.immutable.Set("global"), false), InstructionForm("multimem.st", Vector(OperandConstraint.Address, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("bf16", "bf16x2", "e4m3", "e4m3x2", "e4m3x4", "e5m2", "e5m2x2", "e5m2x4", "f16", "f16x2", "f32", "f64")), _root_.scala.collection.immutable.Set("cluster", "cta", "global", "gpu", "relaxed", "release", "sys"), false), InstructionForm("multimem.st", Vector(OperandConstraint.Address, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("weak"), _root_.scala.collection.immutable.Set("bf16", "bf16x2", "e4m3", "e4m3x2", "e4m3x4", "e5m2", "e5m2x2", "e5m2x4", "f16", "f16x2", "f32", "f64")), _root_.scala.collection.immutable.Set("global"), false), InstructionForm("multimem.red", Vector(OperandConstraint.Address, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("add", "max", "min"), _root_.scala.collection.immutable.Set("bf16", "bf16x2", "e4m3", "e4m3x2", "e4m3x4", "e5m2", "e5m2x2", "e5m2x4", "f16", "f16x2", "f32", "f64")), _root_.scala.collection.immutable.Set("cluster", "cta", "global", "gpu", "relaxed", "release", "sys"), false), InstructionForm("multimem.ld_reduce", Vector(OperandConstraint.Register, OperandConstraint.Address), Vector(_root_.scala.collection.immutable.Set("add", "max", "min"), _root_.scala.collection.immutable.Set("bf16", "bf16x2", "e4m3", "e4m3x2", "e4m3x4", "e5m2", "e5m2x2", "e5m2x4", "f16", "f16x2", "f32", "f64")), _root_.scala.collection.immutable.Set("acc::f16", "acc::f32", "acquire", "cluster", "cta", "global", "gpu", "relaxed", "sys", "v2", "v4", "v8"), false), InstructionForm("multimem.ld_reduce", Vector(OperandConstraint.Register, OperandConstraint.Address), Vector(_root_.scala.collection.immutable.Set("weak"), _root_.scala.collection.immutable.Set("add", "max", "min"), _root_.scala.collection.immutable.Set("bf16", "bf16x2", "e4m3", "e4m3x2", "e4m3x4", "e5m2", "e5m2x2", "e5m2x4", "f16", "f16x2", "f32", "f64")), _root_.scala.collection.immutable.Set("acc::f16", "acc::f32", "global", "v2", "v4", "v8"), false), InstructionForm("multimem.st", Vector(OperandConstraint.Address, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("bf16", "bf16x2", "e4m3", "e4m3x2", "e4m3x4", "e5m2", "e5m2x2", "e5m2x4", "f16", "f16x2", "f32", "f64")), _root_.scala.collection.immutable.Set("cluster", "cta", "global", "gpu", "relaxed", "release", "sys", "v2", "v4", "v8"), false), InstructionForm("multimem.st", Vector(OperandConstraint.Address, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("weak"), _root_.scala.collection.immutable.Set("bf16", "bf16x2", "e4m3", "e4m3x2", "e4m3x4", "e5m2", "e5m2x2", "e5m2x4", "f16", "f16x2", "f32", "f64")), _root_.scala.collection.immutable.Set("global", "v2", "v4", "v8"), false), InstructionForm("multimem.red", Vector(OperandConstraint.Address, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("add"), _root_.scala.collection.immutable.Set("bf16", "bf16x2", "f16", "f16x2", "f32", "f64")), _root_.scala.collection.immutable.Set("cluster", "cta", "global", "gpu", "relaxed", "release", "sys", "v2", "v4", "v8"), false))
    ),
    InstructionSpec(
      "data-movement-and-conversion-instructions-prefetch-prefetchu",
      "9.7.9.16. Data Movement and Conversion Instructions: prefetch, prefetchu",
      "prefetch, prefetchu",
      "Data Movement and Conversion Instructions",
      Vector("prefetch", "prefetchu"),
      Vector("prefetch{.space}.level                    [a];   // prefetch to data cache\nprefetch.global.level::eviction_priority  [a];   // prefetch to data cache\n\nprefetchu.L1  [a];             // prefetch to uniform cache\n\nprefetch{.tensormap_space}.tensormap [a];  // prefetch the tensormap\n\n.space =                    { .global, .local };\n.level =                    { .L1, .L2 };\n.level::eviction_priority = { .L2::evict_last, .L2::evict_normal };\n.tensormap_space =          { .const, .param };"),
      PtxVersion(2, 0),
      SmTarget(20),
      Vector(InstructionForm("prefetch", Vector(OperandConstraint.Address), Vector(_root_.scala.collection.immutable.Set("L1", "L2")), _root_.scala.collection.immutable.Set("global", "local"), false), InstructionForm("prefetch", Vector(OperandConstraint.Address), Vector(_root_.scala.collection.immutable.Set("global"), _root_.scala.collection.immutable.Set("L2::evict_last", "L2::evict_normal")), _root_.scala.collection.immutable.Set(), false), InstructionForm("prefetchu", Vector(OperandConstraint.Address), Vector(_root_.scala.collection.immutable.Set("L1")), _root_.scala.collection.immutable.Set(), false), InstructionForm("prefetch", Vector(OperandConstraint.Address), Vector(_root_.scala.collection.immutable.Set("tensormap")), _root_.scala.collection.immutable.Set("const", "param"), false))
    ),
    InstructionSpec(
      "data-movement-and-conversion-instructions-applypriority",
      "9.7.9.17. Data Movement and Conversion Instructions: applypriority",
      "applypriority",
      "Data Movement and Conversion Instructions",
      Vector("applypriority"),
      Vector("applypriority{.global}.level::eviction_priority  [a], size;\n\n.level::eviction_priority = { .L2::evict_normal };"),
      PtxVersion(7, 4),
      SmTarget(80),
      Vector(InstructionForm("applypriority", Vector(OperandConstraint.Address, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("L2::evict_normal")), _root_.scala.collection.immutable.Set("global"), true))
    )
)

private def instructionSpecChunk8: Vector[InstructionSpec] = Vector(
    InstructionSpec(
      "data-movement-and-conversion-instructions-discard",
      "9.7.9.18. Data Movement and Conversion Instructions: discard",
      "discard",
      "Data Movement and Conversion Instructions",
      Vector("discard"),
      Vector("discard{.global}.level  [a], size;\n\n.level = { .L2 };"),
      PtxVersion(7, 4),
      SmTarget(80),
      Vector(InstructionForm("discard", Vector(OperandConstraint.Address, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("L2")), _root_.scala.collection.immutable.Set("global"), true))
    ),
    InstructionSpec(
      "data-movement-and-conversion-instructions-createpolicy",
      "9.7.9.19. Data Movement and Conversion Instructions: createpolicy",
      "createpolicy",
      "Data Movement and Conversion Instructions",
      Vector("createpolicy"),
      Vector("// Range-based policy\ncreatepolicy.range{.global}.level::primary_priority{.level::secondary_priority}.b64\n                                   cache_policy, [a], primary-size, total-size;\n\n// Fraction-based policy\ncreatepolicy.fractional.level::primary_priority{.level::secondary_priority}.b64\n                                   cache_policy{, fraction};\n\n// Converting the access property from CUDA APIs\ncreatepolicy.cvt.L2.b64            cache_policy, access-property;\n\n.level::primary_priority =   { .L2::evict_last, .L2::evict_normal,\n                               .L2::evict_first, .L2::evict_unchanged };\n.level::secondary_priority = { .L2::evict_first, .L2::evict_unchanged };"),
      PtxVersion(7, 4),
      SmTarget(80),
      Vector(InstructionForm("createpolicy", Vector(OperandConstraint.Value, OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("range"), _root_.scala.collection.immutable.Set("L2::evict_first", "L2::evict_last", "L2::evict_normal", "L2::evict_unchanged"), _root_.scala.collection.immutable.Set("b64")), _root_.scala.collection.immutable.Set("L2::evict_first", "L2::evict_unchanged", "global"), true), InstructionForm("createpolicy", Vector(OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("fractional"), _root_.scala.collection.immutable.Set("L2::evict_first", "L2::evict_last", "L2::evict_normal", "L2::evict_unchanged"), _root_.scala.collection.immutable.Set("b64")), _root_.scala.collection.immutable.Set("L2::evict_first", "L2::evict_unchanged"), true), InstructionForm("createpolicy", Vector(OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("cvt"), _root_.scala.collection.immutable.Set("L2"), _root_.scala.collection.immutable.Set("b64")), _root_.scala.collection.immutable.Set(), true))
    ),
    InstructionSpec(
      "data-movement-and-conversion-instructions-isspacep",
      "9.7.9.20. Data Movement and Conversion Instructions: isspacep",
      "isspacep",
      "Data Movement and Conversion Instructions",
      Vector("isspacep"),
      Vector("isspacep.space  p, a;    // result is .pred\n\n.space = { const, .global, .local, .shared{::cta, ::cluster}, .param{::entry} };"),
      PtxVersion(2, 0),
      SmTarget(20),
      Vector(InstructionForm("isspacep", Vector(OperandConstraint.Register, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("const", "global", "local")), _root_.scala.collection.immutable.Set(), false))
    ),
    InstructionSpec(
      "data-movement-and-conversion-instructions-cvta",
      "9.7.9.21. Data Movement and Conversion Instructions: cvta",
      "cvta",
      "Data Movement and Conversion Instructions",
      Vector("cvta"),
      Vector("// convert const, global, local, or shared address to generic address\ncvta.space.size  p, a;        // source address in register a\ncvta.space.size  p, var;      // get generic address of var\ncvta.space.size  p, var+imm;  // generic address of var+offset\n\n// convert generic address to const, global, local, or shared address\ncvta.to.space.size  p, a;\n\n.space = { .const, .global, .local, .shared{::cta, ::cluster}, .param{::entry} };\n.size  = { .u32, .u64 };"),
      PtxVersion(2, 0),
      SmTarget(20),
      Vector(InstructionForm("cvta", Vector(OperandConstraint.Register, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("const", "global", "local"), _root_.scala.collection.immutable.Set("u32", "u64")), _root_.scala.collection.immutable.Set(), false), InstructionForm("cvta", Vector(OperandConstraint.Register, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("const", "global", "local"), _root_.scala.collection.immutable.Set("u32", "u64")), _root_.scala.collection.immutable.Set(), false), InstructionForm("cvta", Vector(OperandConstraint.Register, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("const", "global", "local"), _root_.scala.collection.immutable.Set("u32", "u64")), _root_.scala.collection.immutable.Set(), false), InstructionForm("cvta", Vector(OperandConstraint.Register, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("to"), _root_.scala.collection.immutable.Set("const", "global", "local"), _root_.scala.collection.immutable.Set("u32", "u64")), _root_.scala.collection.immutable.Set(), false))
    ),
    InstructionSpec(
      "data-movement-and-conversion-instructions-cvt",
      "9.7.9.22. Data Movement and Conversion Instructions: cvt",
      "cvt",
      "Data Movement and Conversion Instructions",
      Vector("cvt"),
      Vector("cvt{.irnd}{.ftz}{.sat}.dtype.atype         d, a;  // integer rounding\ncvt{.frnd}{.ftz}{.sat}.dtype.atype         d, a;  // fp rounding\n\ncvt.frnd2{.relu}{.satfinite}.f16.f32       d, a;\ncvt.frnd2{.relu}{.satfinite}.f16x2.f32     d, a, b;\ncvt.rs{.relu}{.satfinite}.f16x2.f32        d, a, b, rbits;\n\ncvt.frnd2{.relu}{.satfinite}.bf16.f32      d, a;\ncvt.frnd2{.relu}{.satfinite}.bf16x2.f32    d, a, b;\ncvt.rs{.relu}{.satfinite}.bf16x2.f32       d, a, b, rbits;\n\ncvt.rna{.satfinite}.tf32.f32               d, a;\ncvt.frnd2{.satfinite}{.relu}.tf32.f32      d, a;\n\ncvt.rn.satfinite{.relu}.f8x2type.f32       d, a, b;\ncvt.rn.satfinite{.relu}.f8x2type.fp16x2    d, a;\ncvt.rn{.relu}.f16x2.f8x2type               d, a;\ncvt.rn{.relu}{.satfinite}{.scaled::n2::ue8m0}.bf16x2.f8x2type d, a{, scale-factor};\ncvt.rs{.relu}.satfinite.f8x4type.f32       d, {a, b, e, f}, rbits;\n\ncvt.rn.satfinite{.relu}.f4x2type.f32        d, a, b;\ncvt.rn.satfinite{.relu}.f4x2type.fp16x2type d, a;\ncvt.rn{.relu}.f16x2.f4x2type                d, a;\ncvt.rn{.relu}{.satfinite}{.scaled::n2::ue8m0}.bf16x2.f4x2type d, a{, scale-factor};\ncvt.rs{.relu}.satfinite.f4x4type.f32        d, {a, b, e, f}, rbits;\n\ncvt.rn.satfinite{.relu}.f6x2type.f32        d, a, b;\ncvt.rn.satfinite{.relu}.f6x2type.fp16x2type d, a;\ncvt.rn{.relu}.f16x2.f6x2type                d, a;\ncvt.rn{.relu}{.satfinite}{.scaled::n2::ue8m0}.bf16x2.f6x2type d, a{, scale-factor};\ncvt.rs{.relu}.satfinite.f6x4type.f32        d, {a, b, e, f}, rbits;\n\ncvt.frnd3{.satfinite}.ue8m0x2.f32          d, a, b;\ncvt.frnd3{.satfinite}.ue8m0x2.bf16x2       d, a;\ncvt.rn.bf16x2.ue8m0x2                      d, a;\n\ncvt.rn.satfinite{.relu}{.scaled::n2::ue8m0}.s2f6x2.f32      d, a, b{, scale-factor};\ncvt.rn.satfinite{.relu}{.scaled::n2::ue8m0}.s2f6x2.bf16x2   d, a{, scale-factor};\ncvt.rn{.satfinite}{.relu}{.scaled::n2::ue8m0}.bf16x2.s2f6x2 d, a{, scale-factor};\n\n.irnd   = { .rni, .rzi, .rmi, .rpi };\n.frnd   = { .rn,  .rz,  .rm,  .rp  };\n.frnd2  = { .rn,  .rz };\n.frnd3  = { .rz,  .rp };\n.dtype = .atype = { .u8,   .u16, .u32, .u64,\n                    .s8,   .s16, .s32, .s64,\n                    .bf16, .f16, .f32, .f64 };\n.f8x2type = { .e4m3x2, .e5m2x2 };\n.f4x2type = { .e2m1x2 };\n.f6x2type = { .e2m3x2, .e3m2x2 };\n.f4x4type = { .e2m1x4 };\n.f8x4type = { .e4m3x4, .e5m2x4 };\n.f6x4type = { .e2m3x4, .e3m2x4 };\n.fp16x2type = { .f16x2, .bf16x2 };"),
      PtxVersion(1, 0),
      SmTarget(13),
      Vector(InstructionForm("cvt", Vector(OperandConstraint.Register, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("dtype"), _root_.scala.collection.immutable.Set("bf16", "f16", "f32", "f64", "s16", "s32", "s64", "s8", "u16", "u32", "u64", "u8")), _root_.scala.collection.immutable.Set("ftz", "rmi", "rni", "rpi", "rzi", "sat"), false), InstructionForm("cvt", Vector(OperandConstraint.Register, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("dtype"), _root_.scala.collection.immutable.Set("bf16", "f16", "f32", "f64", "s16", "s32", "s64", "s8", "u16", "u32", "u64", "u8")), _root_.scala.collection.immutable.Set("ftz", "rm", "rn", "rp", "rz", "sat"), false), InstructionForm("cvt", Vector(OperandConstraint.Register, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("rn", "rz"), _root_.scala.collection.immutable.Set("f16"), _root_.scala.collection.immutable.Set("f32")), _root_.scala.collection.immutable.Set("relu", "satfinite"), false), InstructionForm("cvt", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("rn", "rz"), _root_.scala.collection.immutable.Set("f16x2"), _root_.scala.collection.immutable.Set("f32")), _root_.scala.collection.immutable.Set("relu", "satfinite"), false), InstructionForm("cvt", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("rs"), _root_.scala.collection.immutable.Set("f16x2"), _root_.scala.collection.immutable.Set("f32")), _root_.scala.collection.immutable.Set("relu", "satfinite"), false), InstructionForm("cvt", Vector(OperandConstraint.Register, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("rn", "rz"), _root_.scala.collection.immutable.Set("bf16"), _root_.scala.collection.immutable.Set("f32")), _root_.scala.collection.immutable.Set("relu", "satfinite"), false), InstructionForm("cvt", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("rn", "rz"), _root_.scala.collection.immutable.Set("bf16x2"), _root_.scala.collection.immutable.Set("f32")), _root_.scala.collection.immutable.Set("relu", "satfinite"), false), InstructionForm("cvt", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("rs"), _root_.scala.collection.immutable.Set("bf16x2"), _root_.scala.collection.immutable.Set("f32")), _root_.scala.collection.immutable.Set("relu", "satfinite"), false), InstructionForm("cvt", Vector(OperandConstraint.Register, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("rna"), _root_.scala.collection.immutable.Set("tf32"), _root_.scala.collection.immutable.Set("f32")), _root_.scala.collection.immutable.Set("satfinite"), false), InstructionForm("cvt", Vector(OperandConstraint.Register, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("rn", "rz"), _root_.scala.collection.immutable.Set("tf32"), _root_.scala.collection.immutable.Set("f32")), _root_.scala.collection.immutable.Set("relu", "satfinite"), false), InstructionForm("cvt", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("rn"), _root_.scala.collection.immutable.Set("satfinite"), _root_.scala.collection.immutable.Set("e4m3x2", "e5m2x2"), _root_.scala.collection.immutable.Set("f32")), _root_.scala.collection.immutable.Set("relu"), false), InstructionForm("cvt", Vector(OperandConstraint.Register, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("rn"), _root_.scala.collection.immutable.Set("satfinite"), _root_.scala.collection.immutable.Set("e4m3x2", "e5m2x2"), _root_.scala.collection.immutable.Set("fp16x2")), _root_.scala.collection.immutable.Set("relu"), false), InstructionForm("cvt", Vector(OperandConstraint.Register, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("rn"), _root_.scala.collection.immutable.Set("f16x2"), _root_.scala.collection.immutable.Set("e4m3x2", "e5m2x2")), _root_.scala.collection.immutable.Set("relu"), false), InstructionForm("cvt", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("rn"), _root_.scala.collection.immutable.Set("bf16x2"), _root_.scala.collection.immutable.Set("e4m3x2", "e5m2x2")), _root_.scala.collection.immutable.Set("relu", "satfinite", "scaled::n2::ue8m0"), false), InstructionForm("cvt", Vector(OperandConstraint.Register, OperandConstraint.Vector, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("rs"), _root_.scala.collection.immutable.Set("satfinite"), _root_.scala.collection.immutable.Set("e4m3x4", "e5m2x4"), _root_.scala.collection.immutable.Set("f32")), _root_.scala.collection.immutable.Set("relu"), false), InstructionForm("cvt", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("rn"), _root_.scala.collection.immutable.Set("satfinite"), _root_.scala.collection.immutable.Set("e2m1x2"), _root_.scala.collection.immutable.Set("f32")), _root_.scala.collection.immutable.Set("relu"), false), InstructionForm("cvt", Vector(OperandConstraint.Register, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("rn"), _root_.scala.collection.immutable.Set("satfinite"), _root_.scala.collection.immutable.Set("e2m1x2"), _root_.scala.collection.immutable.Set("bf16x2", "f16x2")), _root_.scala.collection.immutable.Set("relu"), false), InstructionForm("cvt", Vector(OperandConstraint.Register, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("rn"), _root_.scala.collection.immutable.Set("f16x2"), _root_.scala.collection.immutable.Set("e2m1x2")), _root_.scala.collection.immutable.Set("relu"), false), InstructionForm("cvt", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("rn"), _root_.scala.collection.immutable.Set("bf16x2"), _root_.scala.collection.immutable.Set("e2m1x2")), _root_.scala.collection.immutable.Set("relu", "satfinite", "scaled::n2::ue8m0"), false), InstructionForm("cvt", Vector(OperandConstraint.Register, OperandConstraint.Vector, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("rs"), _root_.scala.collection.immutable.Set("satfinite"), _root_.scala.collection.immutable.Set("e2m1x4"), _root_.scala.collection.immutable.Set("f32")), _root_.scala.collection.immutable.Set("relu"), false), InstructionForm("cvt", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("rn"), _root_.scala.collection.immutable.Set("satfinite"), _root_.scala.collection.immutable.Set("e2m3x2", "e3m2x2"), _root_.scala.collection.immutable.Set("f32")), _root_.scala.collection.immutable.Set("relu"), false), InstructionForm("cvt", Vector(OperandConstraint.Register, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("rn"), _root_.scala.collection.immutable.Set("satfinite"), _root_.scala.collection.immutable.Set("e2m3x2", "e3m2x2"), _root_.scala.collection.immutable.Set("bf16x2", "f16x2")), _root_.scala.collection.immutable.Set("relu"), false), InstructionForm("cvt", Vector(OperandConstraint.Register, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("rn"), _root_.scala.collection.immutable.Set("f16x2"), _root_.scala.collection.immutable.Set("e2m3x2", "e3m2x2")), _root_.scala.collection.immutable.Set("relu"), false), InstructionForm("cvt", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("rn"), _root_.scala.collection.immutable.Set("bf16x2"), _root_.scala.collection.immutable.Set("e2m3x2", "e3m2x2")), _root_.scala.collection.immutable.Set("relu", "satfinite", "scaled::n2::ue8m0"), false), InstructionForm("cvt", Vector(OperandConstraint.Register, OperandConstraint.Vector, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("rs"), _root_.scala.collection.immutable.Set("satfinite"), _root_.scala.collection.immutable.Set("e2m3x4", "e3m2x4"), _root_.scala.collection.immutable.Set("f32")), _root_.scala.collection.immutable.Set("relu"), false), InstructionForm("cvt", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("rp", "rz"), _root_.scala.collection.immutable.Set("ue8m0x2"), _root_.scala.collection.immutable.Set("f32")), _root_.scala.collection.immutable.Set("satfinite"), false), InstructionForm("cvt", Vector(OperandConstraint.Register, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("rp", "rz"), _root_.scala.collection.immutable.Set("ue8m0x2"), _root_.scala.collection.immutable.Set("bf16x2")), _root_.scala.collection.immutable.Set("satfinite"), false), InstructionForm("cvt", Vector(OperandConstraint.Register, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("rn"), _root_.scala.collection.immutable.Set("bf16x2"), _root_.scala.collection.immutable.Set("ue8m0x2")), _root_.scala.collection.immutable.Set(), false), InstructionForm("cvt", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("rn"), _root_.scala.collection.immutable.Set("satfinite"), _root_.scala.collection.immutable.Set("s2f6x2"), _root_.scala.collection.immutable.Set("f32")), _root_.scala.collection.immutable.Set("relu", "scaled::n2::ue8m0"), false), InstructionForm("cvt", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("rn"), _root_.scala.collection.immutable.Set("satfinite"), _root_.scala.collection.immutable.Set("s2f6x2"), _root_.scala.collection.immutable.Set("bf16x2")), _root_.scala.collection.immutable.Set("relu", "scaled::n2::ue8m0"), false), InstructionForm("cvt", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("rn"), _root_.scala.collection.immutable.Set("bf16x2"), _root_.scala.collection.immutable.Set("s2f6x2")), _root_.scala.collection.immutable.Set("relu", "satfinite", "scaled::n2::ue8m0"), false))
    ),
    InstructionSpec(
      "data-movement-and-conversion-instructions-cvt-pack",
      "9.7.9.23. Data Movement and Conversion Instructions: cvt.pack",
      "cvt.pack",
      "Data Movement and Conversion Instructions",
      Vector("cvt.pack"),
      Vector("cvt.pack.sat.convertType.abType  d, a, b;\n    .convertType  = { .u16, .s16 }\n    .abType       = { .s32 }\n\ncvt.pack.sat.convertType.abType.cType  d, a, b, c;\n    .convertType  = { .u2, .s2, .u4, .s4, .u8, .s8 }\n    .abType       = { .s32 }\n    .cType        = { .b32 }"),
      PtxVersion(6, 5),
      SmTarget(72),
      Vector(InstructionForm("cvt.pack", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("sat"), _root_.scala.collection.immutable.Set("s2", "s4", "s8", "u2", "u4", "u8"), _root_.scala.collection.immutable.Set("s32")), _root_.scala.collection.immutable.Set(), false), InstructionForm("cvt.pack", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("sat"), _root_.scala.collection.immutable.Set("s2", "s4", "s8", "u2", "u4", "u8"), _root_.scala.collection.immutable.Set("s32"), _root_.scala.collection.immutable.Set("b32")), _root_.scala.collection.immutable.Set(), false))
    ),
    InstructionSpec(
      "data-movement-and-conversion-instructions-mapa",
      "9.7.9.24. Data Movement and Conversion Instructions: mapa",
      "mapa",
      "Data Movement and Conversion Instructions",
      Vector("mapa"),
      Vector("mapa{.space}.type          d, a, b;\n\n// Maps shared memory address in register a into CTA b.\nmapa.shared::cluster.type  d, a, b;\n\n// Maps shared memory variable into CTA b.\nmapa.shared::cluster.type  d, sh, b;\n\n// Maps shared memory variable into CTA b.\nmapa.shared::cluster.type  d, sh + imm, b;\n\n// Maps generic address in register a into CTA b.\nmapa.type                  d, a, b;\n\n.space = { .shared::cluster }\n.type  = { .u32, .u64 }"),
      PtxVersion(7, 8),
      SmTarget(90),
      Vector(InstructionForm("mapa", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("u32", "u64")), _root_.scala.collection.immutable.Set("shared::cluster"), true), InstructionForm("mapa", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("shared::cluster"), _root_.scala.collection.immutable.Set("u32", "u64")), _root_.scala.collection.immutable.Set(), true), InstructionForm("mapa", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("shared::cluster"), _root_.scala.collection.immutable.Set("u32", "u64")), _root_.scala.collection.immutable.Set(), true), InstructionForm("mapa", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("shared::cluster"), _root_.scala.collection.immutable.Set("u32", "u64")), _root_.scala.collection.immutable.Set(), true), InstructionForm("mapa", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("u32", "u64")), _root_.scala.collection.immutable.Set(), true))
    ),
    InstructionSpec(
      "data-movement-and-conversion-instructions-getctarank",
      "9.7.9.25. Data Movement and Conversion Instructions: getctarank",
      "getctarank",
      "Data Movement and Conversion Instructions",
      Vector("getctarank"),
      Vector("getctarank{.space}.type d, a;\n\n// Get cta rank from source shared memory address in register a.\ngetctarank.shared::cluster.type d, a;\n\n// Get cta rank from shared memory variable.\ngetctarank.shared::cluster.type d, var;\n\n// Get cta rank from shared memory variable+offset.\ngetctarank.shared::cluster.type d, var + imm;\n\n// Get cta rank from generic address of shared memory variable in register a.\ngetctarank.type d, a;\n\n.space = { .shared::cluster }\n.type  = { .u32, .u64 }"),
      PtxVersion(7, 8),
      SmTarget(90),
      Vector(InstructionForm("getctarank", Vector(OperandConstraint.Register, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("u32", "u64")), _root_.scala.collection.immutable.Set("shared::cluster"), true), InstructionForm("getctarank", Vector(OperandConstraint.Register, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("shared::cluster"), _root_.scala.collection.immutable.Set("u32", "u64")), _root_.scala.collection.immutable.Set(), true), InstructionForm("getctarank", Vector(OperandConstraint.Register, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("shared::cluster"), _root_.scala.collection.immutable.Set("u32", "u64")), _root_.scala.collection.immutable.Set(), true), InstructionForm("getctarank", Vector(OperandConstraint.Register, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("shared::cluster"), _root_.scala.collection.immutable.Set("u32", "u64")), _root_.scala.collection.immutable.Set(), true), InstructionForm("getctarank", Vector(OperandConstraint.Register, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("u32", "u64")), _root_.scala.collection.immutable.Set(), true))
    ),
    InstructionSpec(
      "data-movement-and-conversion-instructions-cp-async",
      "9.7.9.26.3.1. Data Movement and Conversion Instructions: cp.async",
      "cp.async",
      "Data Movement and Conversion Instructions",
      Vector("cp.async"),
      Vector("cp.async.ca.shared{::cta}.global{.level::cache_hint}{.level::prefetch_size}\n                         [dst], [src], cp-size{, src-size}{, cache_policy} ;\ncp.async.cg.shared{::cta}.global{.level::cache_hint}{.level::prefetch_size}\n                         [dst], [src], 16{, src-size}{, cache_policy} ;\ncp.async.ca.shared{::cta}.global{.level::cache_hint}{.level::prefetch_size}\n                         [dst], [src], cp-size{, ignore-src}{, cache_policy} ;\ncp.async.cg.shared{::cta}.global{.level::cache_hint}{.level::prefetch_size}\n                         [dst], [src], 16{, ignore-src}{, cache_policy} ;\n\n.level::cache_hint =     { .L2::cache_hint }\n.level::prefetch_size =  { .L2::64B, .L2::128B, .L2::256B }\ncp-size =                { 4, 8, 16 }"),
      PtxVersion(7, 0),
      SmTarget(30),
      Vector(InstructionForm("cp.async", Vector(OperandConstraint.Address, OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("ca"), _root_.scala.collection.immutable.Set("shared"), _root_.scala.collection.immutable.Set("global")), _root_.scala.collection.immutable.Set("L2::cache_hint"), false), InstructionForm("cp.async", Vector(OperandConstraint.Address, OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("cg"), _root_.scala.collection.immutable.Set("shared"), _root_.scala.collection.immutable.Set("global")), _root_.scala.collection.immutable.Set("L2::cache_hint"), false), InstructionForm("cp.async", Vector(OperandConstraint.Address, OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("ca"), _root_.scala.collection.immutable.Set("shared"), _root_.scala.collection.immutable.Set("global")), _root_.scala.collection.immutable.Set("L2::cache_hint"), false), InstructionForm("cp.async", Vector(OperandConstraint.Address, OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("cg"), _root_.scala.collection.immutable.Set("shared"), _root_.scala.collection.immutable.Set("global")), _root_.scala.collection.immutable.Set("L2::cache_hint"), false))
    ),
    InstructionSpec(
      "data-movement-and-conversion-instructions-cp-async-commit-group",
      "9.7.9.26.3.2. Data Movement and Conversion Instructions: cp.async.commit_group",
      "cp.async.commit_group",
      "Data Movement and Conversion Instructions",
      Vector("cp.async.commit_group"),
      Vector("cp.async.commit_group ;"),
      PtxVersion(7, 0),
      SmTarget(80),
      Vector(InstructionForm("cp.async.commit_group", Vector(), Vector(), _root_.scala.collection.immutable.Set(), true))
    ),
    InstructionSpec(
      "data-movement-and-conversion-instructions-cp-async-wait-group",
      "9.7.9.26.3.3. Data Movement and Conversion Instructions: cp.async.wait_group / cp.async.wait_all",
      "cp.async.wait_group / cp.async.wait_all",
      "Data Movement and Conversion Instructions",
      Vector("cp.async.wait_group", "cp.async.wait_all"),
      Vector("cp.async.wait_group N;\ncp.async.wait_all ;"),
      PtxVersion(7, 0),
      SmTarget(80),
      Vector(InstructionForm("cp.async.wait_group", Vector(OperandConstraint.Value), Vector(), _root_.scala.collection.immutable.Set(), true), InstructionForm("cp.async.wait_all", Vector(), Vector(), _root_.scala.collection.immutable.Set(), true))
    ),
    InstructionSpec(
      "data-movement-and-conversion-instructions-cp-async-bulk",
      "9.7.9.26.4.1. Data Movement and Conversion Instructions: cp.async.bulk",
      "cp.async.bulk",
      "Data Movement and Conversion Instructions",
      Vector("cp.async.bulk"),
      Vector("// global -> shared::cta\ncp.async.bulk{.sem}.dst.src.completion_mechanism{.level::cache_hint}{.ignore_oob}\n                      [dstMem], [srcMem], size{, ignoreBytesLeft, ignoreBytesRight}, [mbar] {, cache_policy};\n\n.sem =                  { .weak }\n.dst =                  { .shared::cta }\n.src =                  { .global }\n.completion_mechanism = { .mbarrier::complete_tx::bytes }\n.level::cache_hint =    { .L2::cache_hint }\n\ncp.async.bulk.sem.scope.dst.src.completion_mechanism{.level::cache_hint}{.ignore_oob}.type\n                      [dstMem], [srcMem], size{, ignoreBytesLeft, ignoreBytesRight}, [mbar] {, cache_policy};\n\n.sem =                  { .relaxed }\n.scope =                { .cta, .cluster, .gpu, .sys }\n.dst =                  { .shared::cta }\n.src =                  { .global }\n.completion_mechanism = { .mbarrier::complete_tx::bytes }\n.level::cache_hint =    { .L2::cache_hint }\n.type =                 { .b128 }\n\n\n// global -> shared::cluster\ncp.async.bulk{.sem}.dst.src.completion_mechanism{.multicast}{.level::cache_hint}\n                      [dstMem], [srcMem], size, [mbar] {, ctaMask} {, cache_policy};\n\n.sem =                  { .weak }\n.dst =                  { .shared::cluster }\n.src =                  { .global }\n.completion_mechanism = { .mbarrier::complete_tx::bytes }\n.level::cache_hint =    { .L2::cache_hint }\n.multicast =            { .multicast::cluster }\n\ncp.async.bulk.sem.scope.dst.src.completion_mechanism{.multicast}{.level::cache_hint}.type\n                      [dstMem], [srcMem], size, [mbar] {, ctaMask} {, cache_policy};\n\n.sem =                  { .relaxed }\n.scope =                { .cta, .cluster, .gpu, .sys }\n.dst =                  { .shared::cluster }\n.src =                  { .global }\n.completion_mechanism = { .mbarrier::complete_tx::bytes }\n.level::cache_hint =    { .L2::cache_hint }\n.multicast =            { .multicast::cluster }\n.type =                 { .b128 }\n\n\n// shared::cta -> shared::cluster\ncp.async.bulk{.sem}.dst.src.completion_mechanism [dstMem], [srcMem], size, [mbar];\n\n.sem =                  { .weak }\n.dst =                  { .shared::cluster }\n.src =                  { .shared::cta }\n.completion_mechanism = { .mbarrier::complete_tx::bytes }\n\ncp.async.bulk.sem.scope.dst.src.completion_mechanism.type [dstMem], [srcMem], size, [mbar];\n\n.sem =                  { .relaxed }\n.scope =                { .cta, .cluster }\n.dst =                  { .shared::cluster }\n.src =                  { .shared::cta }\n.completion_mechanism = { .mbarrier::complete_tx::bytes }\n.type =                 { .b128 }\n\n\n// shared::cta -> global\ncp.async.bulk{.sem}.dst.src.completion_mechanism{.level::cache_hint}{.cp_mask}\n                      [dstMem], [srcMem], size {, cache_policy} {, byteMask};\n\n.sem =                  { .weak }\n.dst =                  { .global }\n.src =                  { .shared::cta }\n.completion_mechanism = { .bulk_group }\n.level::cache_hint =    { .L2::cache_hint }\n\ncp.async.bulk.sem.scope.dst.src.completion_mechanism{.level::cache_hint}{.cp_mask}.type\n                      [dstMem], [srcMem], size {, cache_policy} {, byteMask};\n\n.sem =                  { .relaxed }\n.scope =                { .cta, .cluster, .gpu, .sys }\n.dst =                  { .global }\n.src =                  { .shared::cta }\n.completion_mechanism = { .bulk_group }\n.level::cache_hint =    { .L2::cache_hint }\n.type =                 { .b128 }"),
      PtxVersion(8, 0),
      SmTarget(90),
      Vector(InstructionForm("cp.async.bulk", Vector(OperandConstraint.Address, OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Address, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("global"), _root_.scala.collection.immutable.Set("shared::cta"), _root_.scala.collection.immutable.Set("bulk_group")), _root_.scala.collection.immutable.Set("L2::cache_hint", "ignore_oob", "relaxed"), false), InstructionForm("cp.async.bulk", Vector(OperandConstraint.Address, OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Address, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("relaxed"), _root_.scala.collection.immutable.Set("cluster", "cta", "gpu", "sys"), _root_.scala.collection.immutable.Set("global"), _root_.scala.collection.immutable.Set("shared::cta"), _root_.scala.collection.immutable.Set("bulk_group"), _root_.scala.collection.immutable.Set("b128")), _root_.scala.collection.immutable.Set("L2::cache_hint", "ignore_oob"), false), InstructionForm("cp.async.bulk", Vector(OperandConstraint.Address, OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("global"), _root_.scala.collection.immutable.Set("shared::cta"), _root_.scala.collection.immutable.Set("bulk_group")), _root_.scala.collection.immutable.Set("L2::cache_hint", "multicast::cluster", "relaxed"), false), InstructionForm("cp.async.bulk", Vector(OperandConstraint.Address, OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("relaxed"), _root_.scala.collection.immutable.Set("cluster", "cta", "gpu", "sys"), _root_.scala.collection.immutable.Set("global"), _root_.scala.collection.immutable.Set("shared::cta"), _root_.scala.collection.immutable.Set("bulk_group"), _root_.scala.collection.immutable.Set("b128")), _root_.scala.collection.immutable.Set("L2::cache_hint", "multicast::cluster"), false), InstructionForm("cp.async.bulk", Vector(OperandConstraint.Address, OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Address), Vector(_root_.scala.collection.immutable.Set("global"), _root_.scala.collection.immutable.Set("shared::cta"), _root_.scala.collection.immutable.Set("bulk_group")), _root_.scala.collection.immutable.Set("relaxed"), false), InstructionForm("cp.async.bulk", Vector(OperandConstraint.Address, OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Address), Vector(_root_.scala.collection.immutable.Set("relaxed"), _root_.scala.collection.immutable.Set("cluster", "cta", "gpu", "sys"), _root_.scala.collection.immutable.Set("global"), _root_.scala.collection.immutable.Set("shared::cta"), _root_.scala.collection.immutable.Set("bulk_group"), _root_.scala.collection.immutable.Set("b128")), _root_.scala.collection.immutable.Set(), false), InstructionForm("cp.async.bulk", Vector(OperandConstraint.Address, OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("global"), _root_.scala.collection.immutable.Set("shared::cta"), _root_.scala.collection.immutable.Set("bulk_group")), _root_.scala.collection.immutable.Set("L2::cache_hint", "cp_mask", "relaxed"), false), InstructionForm("cp.async.bulk", Vector(OperandConstraint.Address, OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("relaxed"), _root_.scala.collection.immutable.Set("cluster", "cta", "gpu", "sys"), _root_.scala.collection.immutable.Set("global"), _root_.scala.collection.immutable.Set("shared::cta"), _root_.scala.collection.immutable.Set("bulk_group"), _root_.scala.collection.immutable.Set("b128")), _root_.scala.collection.immutable.Set("L2::cache_hint", "cp_mask"), false))
    )
)

private def instructionSpecChunk9: Vector[InstructionSpec] = Vector(
    InstructionSpec(
      "data-movement-and-conversion-instructions-cp-reduce-async-bulk",
      "9.7.9.26.4.2. Data Movement and Conversion Instructions: cp.reduce.async.bulk",
      "cp.reduce.async.bulk",
      "Data Movement and Conversion Instructions",
      Vector("cp.reduce.async.bulk"),
      Vector("cp.reduce.async.bulk{.sem.scope}.dst.src.completion_mechanism.redOp.type\n              [dstMem], [srcMem], size, [mbar]\n\n.sem =                  { .relaxed }\n.scope =                { .cta, .cluster }\n.dst =                  { .shared::cluster }\n.src =                  { .shared::cta }\n.completion_mechanism = { .mbarrier::complete_tx::bytes }\n.redOp=                 { .and, .or, .xor,\n                          .add, .inc, .dec,\n                          .min, .max }\n.type =                 { .b32, .u32, .s32, .b64, .u64 }\n\n\ncp.reduce.async.bulk{.sem.scope}.dst.src.completion_mechanism{.level::cache_hint}.redOp.type\n               [dstMem], [srcMem], size{, cache_policy}\n\n.sem =                  { .relaxed }\n.scope =                { .cta, .cluster, .gpu, .sys }\n.dst =                  { .global      }\n.src =                  { .shared::cta }\n.completion_mechanism = { .bulk_group }\n.level::cache_hint    = { .L2::cache_hint }\n.redOp=                 { .and, .or, .xor,\n                          .add, .inc, .dec,\n                          .min, .max }\n.type =                 { .f16, .bf16, .b32, .u32, .s32, .b64, .u64, .s64, .f32, .f64 }\n\n\ncp.reduce.async.bulk{.sem.scope}.dst.src.completion_mechanism{.level::cache_hint}.add.noftz.type\n               [dstMem], [srcMem], size{, cache_policy}\n\n.sem =                  { .relaxed }\n.scope =                { .cta, .cluster, .gpu, .sys }\n.dst  =                 { .global }\n.src  =                 { .shared::cta }\n.completion_mechanism = { .bulk_group }\n.type =                 { .f16, .bf16 }"),
      PtxVersion(8, 0),
      SmTarget(90),
      Vector(InstructionForm("cp.reduce.async.bulk", Vector(OperandConstraint.Address, OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Address), Vector(_root_.scala.collection.immutable.Set("global"), _root_.scala.collection.immutable.Set("shared::cta"), _root_.scala.collection.immutable.Set("bulk_group"), _root_.scala.collection.immutable.Set("add", "and", "dec", "inc", "max", "min", "or", "xor"), _root_.scala.collection.immutable.Set("bf16", "f16")), _root_.scala.collection.immutable.Set("cluster", "cta", "gpu", "relaxed", "sys"), true), InstructionForm("cp.reduce.async.bulk", Vector(OperandConstraint.Address, OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("global"), _root_.scala.collection.immutable.Set("shared::cta"), _root_.scala.collection.immutable.Set("bulk_group"), _root_.scala.collection.immutable.Set("add", "and", "dec", "inc", "max", "min", "or", "xor"), _root_.scala.collection.immutable.Set("bf16", "f16")), _root_.scala.collection.immutable.Set("L2::cache_hint", "cluster", "cta", "gpu", "relaxed", "sys"), true), InstructionForm("cp.reduce.async.bulk", Vector(OperandConstraint.Address, OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("global"), _root_.scala.collection.immutable.Set("shared::cta"), _root_.scala.collection.immutable.Set("bulk_group"), _root_.scala.collection.immutable.Set("add"), _root_.scala.collection.immutable.Set("noftz"), _root_.scala.collection.immutable.Set("bf16", "f16")), _root_.scala.collection.immutable.Set("L2::cache_hint", "cluster", "cta", "gpu", "relaxed", "sys"), true))
    ),
    InstructionSpec(
      "data-movement-and-conversion-instructions-cp-async-bulk-prefetch",
      "9.7.9.26.4.3. Data Movement and Conversion Instructions: cp.async.bulk.prefetch",
      "cp.async.bulk.prefetch",
      "Data Movement and Conversion Instructions",
      Vector("cp.async.bulk.prefetch"),
      Vector("cp.async.bulk.prefetch.L2.src{.level::cache_hint}   [srcMem], size {, cache_policy};\n\n.src =                { .global }\n.level::cache_hint =  { .L2::cache_hint }"),
      PtxVersion(8, 0),
      SmTarget(90),
      Vector(InstructionForm("cp.async.bulk.prefetch", Vector(OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("L2"), _root_.scala.collection.immutable.Set("global")), _root_.scala.collection.immutable.Set("L2::cache_hint"), true))
    ),
    InstructionSpec(
      "data-movement-and-conversion-instructions-multimem-cp-async-bulk",
      "9.7.9.26.4.4. Data Movement and Conversion Instructions: multimem.cp.async.bulk",
      "multimem.cp.async.bulk",
      "Data Movement and Conversion Instructions",
      Vector("multimem.cp.async.bulk"),
      Vector("multimem.cp.async.bulk{.sem}.dst.src.completion_mechanism{.cp_mask}\n    [dstMem], [srcMem], size{, byteMask};\n\n    .sem                  = { .weak }\n    .dst                  = { .global }\n    .src                  = { .shared::cta }\n    .completion_mechanism = { .bulk_group }\n\nmultimem.cp.async.bulk.sem.scope.dst.src.completion_mechanism{.cp_mask}.type\n    [dstMem], [srcMem], size{, byteMask};\n\n    .sem                  = { .relaxed }\n    .scope                = { .cta, .cluster, .gpu, .sys }\n    .dst                  = { .global }\n    .src                  = { .shared::cta }\n    .completion_mechanism = { .bulk_group }\n    .type                 = { .b128 }"),
      PtxVersion(9, 1),
      SmTarget(90),
      Vector(InstructionForm("multimem.cp.async.bulk", Vector(OperandConstraint.Address, OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("global"), _root_.scala.collection.immutable.Set("shared::cta"), _root_.scala.collection.immutable.Set("bulk_group")), _root_.scala.collection.immutable.Set("cp_mask", "relaxed"), false), InstructionForm("multimem.cp.async.bulk", Vector(OperandConstraint.Address, OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("relaxed"), _root_.scala.collection.immutable.Set("cluster", "cta", "gpu", "sys"), _root_.scala.collection.immutable.Set("global"), _root_.scala.collection.immutable.Set("shared::cta"), _root_.scala.collection.immutable.Set("bulk_group"), _root_.scala.collection.immutable.Set("b128")), _root_.scala.collection.immutable.Set("cp_mask"), false))
    ),
    InstructionSpec(
      "data-movement-and-conversion-instructions-multimem-cp-reduce-async-bulk",
      "9.7.9.26.4.5. Data Movement and Conversion Instructions: multimem.cp.reduce.async.bulk",
      "multimem.cp.reduce.async.bulk",
      "Data Movement and Conversion Instructions",
      Vector("multimem.cp.reduce.async.bulk"),
      Vector("multimem.cp.reduce.async.bulk{.sem.scope}.dst.src.completion_mechanism.redOp.type  [dstMem], [srcMem], size;\n\n    .sem                  =  { .relaxed }\n    .scope                = { .cta, .cluster, .gpu, .sys }\n    .dst                  = { .global }\n    .src                  = { .shared::cta }\n    .completion_mechanism = { .bulk_group }\n    .redOp                = { .and, .or, .xor,\n                              .add, .inc, .dec,\n                              .min, .max }\n    .type                 = { .f16, .bf16,\n                              .b32, .u32, .s32,\n                              .b64, .u64, .s64,\n                              .f32, .f64 }\n\nmultimem.cp.reduce.async.bulk{.sem.scope}.dst.src.completion_mechanism.add.noftz.type  [dstMem], [srcMem], size;\n\n    .sem                  = { .relaxed }\n    .scope                = { .cta, .cluster, .gpu, .sys }\n    .dst                  = { .global }\n    .src                  = { .shared::cta }\n    .completion_mechanism = { .bulk_group }\n    .type                 = { .f16, .bf16 }"),
      PtxVersion(9, 1),
      SmTarget(90),
      Vector(InstructionForm("multimem.cp.reduce.async.bulk", Vector(OperandConstraint.Address, OperandConstraint.Address, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("global"), _root_.scala.collection.immutable.Set("shared::cta"), _root_.scala.collection.immutable.Set("bulk_group"), _root_.scala.collection.immutable.Set("add", "and", "dec", "inc", "max", "min", "or", "xor"), _root_.scala.collection.immutable.Set("bf16", "f16")), _root_.scala.collection.immutable.Set("cluster", "cta", "gpu", "relaxed", "sys"), true), InstructionForm("multimem.cp.reduce.async.bulk", Vector(OperandConstraint.Address, OperandConstraint.Address, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("global"), _root_.scala.collection.immutable.Set("shared::cta"), _root_.scala.collection.immutable.Set("bulk_group"), _root_.scala.collection.immutable.Set("add"), _root_.scala.collection.immutable.Set("noftz"), _root_.scala.collection.immutable.Set("bf16", "f16")), _root_.scala.collection.immutable.Set("cluster", "cta", "gpu", "relaxed", "sys"), true))
    ),
    InstructionSpec(
      "data-movement-and-conversion-instructions-cp-async-bulk-tensor",
      "9.7.9.26.5.2. Data Movement and Conversion Instructions: cp.async.bulk.tensor",
      "cp.async.bulk.tensor",
      "Data Movement and Conversion Instructions",
      Vector("cp.async.bulk.tensor"),
      Vector("// global -> shared::cta\ncp.async.bulk.tensor.dim.dst.src{.load_mode}.completion_mechanism{.cta_group}{.level::cache_hint}\n                                   [dstMem], [tensorMap, tensorCoords], [mbar]{, im2colInfo} {, cache_policy}\n\n.dst =                  { .shared::cta }\n.src =                  { .global }\n.dim =                  { .1d, .2d, .3d, .4d, .5d }\n.completion_mechanism = { .mbarrier::complete_tx::bytes }\n.cta_group =            { .cta_group::1, .cta_group::2 }\n.load_mode =            { .tile, .tile::gather4, .im2col, .im2col::w, .im2col::w::128 }\n.level::cache_hint =    { .L2::cache_hint }\n\n\n// global -> shared::cluster\ncp.async.bulk.tensor.dim.dst.src{.load_mode}.completion_mechanism{.multicast}{.cta_group}{.level::cache_hint}\n                                   [dstMem], [tensorMap, tensorCoords], [mbar]{, im2colInfo}\n                                   {, ctaMask} {, cache_policy}\n\n.dst =                  { .shared::cluster }\n.src =                  { .global }\n.dim =                  { .1d, .2d, .3d, .4d, .5d }\n.completion_mechanism = { .mbarrier::complete_tx::bytes }\n.cta_group =            { .cta_group::1, .cta_group::2 }\n.load_mode =            { .tile, .tile::gather4, .im2col, .im2col::w, .im2col::w::128 }\n.level::cache_hint =    { .L2::cache_hint }\n.multicast =            { .multicast::cluster  }\n\n\n// shared::cta -> global\ncp.async.bulk.tensor.dim.dst.src{.load_mode}.completion_mechanism{.level::cache_hint}\n                                   [tensorMap, tensorCoords], [srcMem] {, cache_policy}\n\n.dst =                  { .global }\n.src =                  { .shared::cta }\n.dim =                  { .1d, .2d, .3d, .4d, .5d }\n.completion_mechanism = { .bulk_group }\n.load_mode =            { .tile, .tile::scatter4, .im2col_no_offs }\n.level::cache_hint =    { .L2::cache_hint }"),
      PtxVersion(8, 0),
      SmTarget(90),
      Vector(InstructionForm("cp.async.bulk.tensor", Vector(OperandConstraint.Address, OperandConstraint.Address, OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("global"), _root_.scala.collection.immutable.Set("shared::cta"), _root_.scala.collection.immutable.Set("bulk_group")), _root_.scala.collection.immutable.Set("L2::cache_hint", "im2col_no_offs", "tile", "tile::scatter4"), false), InstructionForm("cp.async.bulk.tensor", Vector(OperandConstraint.Address, OperandConstraint.Address, OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("global"), _root_.scala.collection.immutable.Set("shared::cta"), _root_.scala.collection.immutable.Set("bulk_group")), _root_.scala.collection.immutable.Set("L2::cache_hint", "im2col_no_offs", "multicast::cluster", "tile", "tile::scatter4"), false), InstructionForm("cp.async.bulk.tensor", Vector(OperandConstraint.Address, OperandConstraint.Address, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("global"), _root_.scala.collection.immutable.Set("shared::cta"), _root_.scala.collection.immutable.Set("bulk_group")), _root_.scala.collection.immutable.Set("L2::cache_hint", "im2col_no_offs", "tile", "tile::scatter4"), false))
    ),
    InstructionSpec(
      "data-movement-and-conversion-instructions-cp-reduce-async-bulk-tensor",
      "9.7.9.26.5.3. Data Movement and Conversion Instructions: cp.reduce.async.bulk.tensor",
      "cp.reduce.async.bulk.tensor",
      "Data Movement and Conversion Instructions",
      Vector("cp.reduce.async.bulk.tensor"),
      Vector("// shared::cta -> global:\ncp.reduce.async.bulk.tensor.dim.dst.src.redOp{.load_mode}.completion_mechanism{.level::cache_hint}\n                                          [tensorMap, tensorCoords], [srcMem] {,cache_policy}\n\n.dst =                  { .global }\n.src =                  { .shared::cta }\n.dim =                  { .1d, .2d, .3d, .4d, .5d }\n.completion_mechanism = { .bulk_group }\n.load_mode =            { .tile, .im2col_no_offs }\n.redOp =                { .add, .min, .max, .inc, .dec, .and, .or, .xor}"),
      PtxVersion(8, 0),
      SmTarget(90),
      Vector(InstructionForm("cp.reduce.async.bulk.tensor", Vector(OperandConstraint.Address, OperandConstraint.Address, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("global"), _root_.scala.collection.immutable.Set("shared::cta"), _root_.scala.collection.immutable.Set("add", "and", "dec", "inc", "max", "min", "or", "xor"), _root_.scala.collection.immutable.Set("bulk_group")), _root_.scala.collection.immutable.Set("im2col_no_offs", "level::cache_hint", "tile"), true))
    ),
    InstructionSpec(
      "data-movement-and-conversion-instructions-cp-async-bulk-prefetch-tensor",
      "9.7.9.26.5.4. Data Movement and Conversion Instructions: cp.async.bulk.prefetch.tensor",
      "cp.async.bulk.prefetch.tensor",
      "Data Movement and Conversion Instructions",
      Vector("cp.async.bulk.prefetch.tensor"),
      Vector("// global -> L2:\ncp.async.bulk.prefetch.tensor.dim.L2.src{.load_mode}{.level::cache_hint} [tensorMap, tensorCoords]\n                                                             {, im2colInfo } {, cache_policy}\n\n.src =                { .global }\n.dim =                { .1d, .2d, .3d, .4d, .5d }\n.load_mode =          { .tile, .tile::gather4, .im2col, .im2col::w, .im2col::w::128 }\n.level::cache_hint =  { .L2::cache_hint }"),
      PtxVersion(8, 0),
      SmTarget(90),
      Vector(InstructionForm("cp.async.bulk.prefetch.tensor", Vector(OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("L2"), _root_.scala.collection.immutable.Set("global")), _root_.scala.collection.immutable.Set("L2::cache_hint", "im2col", "im2col::w", "tile", "tile::gather4"), false))
    ),
    InstructionSpec(
      "data-movement-and-conversion-instructions-cp-async-bulk-commit-group",
      "9.7.9.26.6.1. Data Movement and Conversion Instructions: cp.async.bulk.commit_group",
      "cp.async.bulk.commit_group",
      "Data Movement and Conversion Instructions",
      Vector("cp.async.bulk.commit_group"),
      Vector("cp.async.bulk.commit_group;"),
      PtxVersion(8, 0),
      SmTarget(90),
      Vector(InstructionForm("cp.async.bulk.commit_group", Vector(), Vector(), _root_.scala.collection.immutable.Set(), true))
    ),
    InstructionSpec(
      "data-movement-and-conversion-instructions-cp-async-bulk-wait-group",
      "9.7.9.26.6.2. Data Movement and Conversion Instructions: cp.async.bulk.wait_group",
      "cp.async.bulk.wait_group",
      "Data Movement and Conversion Instructions",
      Vector("cp.async.bulk.wait_group"),
      Vector("cp.async.bulk.wait_group{.read} N;"),
      PtxVersion(8, 0),
      SmTarget(90),
      Vector(InstructionForm("cp.async.bulk.wait_group", Vector(OperandConstraint.Value), Vector(), _root_.scala.collection.immutable.Set("read"), true))
    ),
    InstructionSpec(
      "data-movement-and-conversion-instructions-tensormap-replace",
      "9.7.9.27. Data Movement and Conversion Instructions: tensormap.replace",
      "tensormap.replace",
      "Data Movement and Conversion Instructions",
      Vector("tensormap.replace"),
      Vector("tensormap.replace.mode.field1{.ss}.b1024.type  [addr], new_val;\ntensormap.replace.mode.field2{.ss}.b1024.type  [addr], ord, new_val;\ntensormap.replace.mode.field3{.ss}.b1024.type  [addr], new_val;\n\n.mode    = { .tile }\n.field1  = { .global_address, .rank }\n.field2  = { .box_dim, .global_dim, .global_stride, .element_stride  }\n.field3  = { .elemtype,  .interleave_layout, .swizzle_mode, .swizzle_atomicity, .fill_mode }\n.ss      = { .global, .shared::cta }\n.type    = { .b32, .b64 }"),
      PtxVersion(8, 3),
      SmTarget(100),
      Vector(InstructionForm("tensormap.replace", Vector(OperandConstraint.Address, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("tile"), _root_.scala.collection.immutable.Set("global_address", "rank"), _root_.scala.collection.immutable.Set("b1024"), _root_.scala.collection.immutable.Set("b32", "b64")), _root_.scala.collection.immutable.Set("global", "shared::cta"), false), InstructionForm("tensormap.replace", Vector(OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("tile"), _root_.scala.collection.immutable.Set("box_dim", "element_stride", "global_dim", "global_stride"), _root_.scala.collection.immutable.Set("b1024"), _root_.scala.collection.immutable.Set("b32", "b64")), _root_.scala.collection.immutable.Set("global", "shared::cta"), false), InstructionForm("tensormap.replace", Vector(OperandConstraint.Address, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("tile"), _root_.scala.collection.immutable.Set("elemtype", "fill_mode", "interleave_layout", "swizzle_atomicity", "swizzle_mode"), _root_.scala.collection.immutable.Set("b1024"), _root_.scala.collection.immutable.Set("b32", "b64")), _root_.scala.collection.immutable.Set("global", "shared::cta"), false))
    ),
    InstructionSpec(
      "fabric-instructions-try-get",
      "9.7.10.5.1. Fabric Instructions: fabric.try_get",
      "fabric.try_get",
      "Fabric Instructions",
      Vector("fabric.try_get"),
      Vector("fabric.try_get.async.dst.completion_mechanism.sem.scope.b128 [dst], [srcLeId, srcDataOff], size, [bar];\n\n.dst = { .shared::cta }\n.completion_mechanism = { .mbarrier::complete_tx::bytes.mbarrier::report::fabric }\n.sem = { .relaxed }\n.scope = { .sys }"),
      PtxVersion(9, 3),
      SmTarget(100),
      Vector(InstructionForm("fabric.try_get", Vector(OperandConstraint.Address, OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Address), Vector(_root_.scala.collection.immutable.Set("async"), _root_.scala.collection.immutable.Set("shared::cta"), _root_.scala.collection.immutable.Set("relaxed"), _root_.scala.collection.immutable.Set("sys"), _root_.scala.collection.immutable.Set("b128")), _root_.scala.collection.immutable.Set(), true))
    ),
    InstructionSpec(
      "fabric-instructions-try-put",
      "9.7.10.5.2. Fabric Instructions: fabric.try_put",
      "fabric.try_put",
      "Fabric Instructions",
      Vector("fabric.try_put"),
      Vector("fabric.try_put.async{.multimem}.src.completion_mechanism0.sem.scope.b128 [dstLeId, dstDataOff], [src], size, [bar];\n\nfabric.try_put.async{.multimem}.src.completion_mechanism0.cp_mask.sem.scope.b128 [dstLeId, dstDataOff], [src], size, [bar], bytemask;\n\nfabric.try_put.async{.multimem}.src.completion_mechanism1.sem.scope.b128 [dstLeId, dstDataOff, dstCounterOff], [src], size, [bar];\n\n.src = { .shared::cta }\n.completion_mechanism0 = { .mbarrier::complete_tx::16B.mbarrier::report::fabric }\n.completion_mechanism1 = { .mbarrier::complete_tx::16B.mbarrier::report::fabric.counted::bytes }\n.sem = { .relaxed }\n.scope = { .sys }"),
      PtxVersion(9, 3),
      SmTarget(100),
      Vector(InstructionForm("fabric.try_put", Vector(OperandConstraint.Address, OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Address), Vector(_root_.scala.collection.immutable.Set("async"), _root_.scala.collection.immutable.Set("shared::cta"), _root_.scala.collection.immutable.Set("relaxed"), _root_.scala.collection.immutable.Set("sys"), _root_.scala.collection.immutable.Set("b128")), _root_.scala.collection.immutable.Set("multimem"), true), InstructionForm("fabric.try_put", Vector(OperandConstraint.Address, OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Address, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("async"), _root_.scala.collection.immutable.Set("shared::cta"), _root_.scala.collection.immutable.Set("cp_mask"), _root_.scala.collection.immutable.Set("relaxed"), _root_.scala.collection.immutable.Set("sys"), _root_.scala.collection.immutable.Set("b128")), _root_.scala.collection.immutable.Set("multimem"), true), InstructionForm("fabric.try_put", Vector(OperandConstraint.Address, OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Address), Vector(_root_.scala.collection.immutable.Set("async"), _root_.scala.collection.immutable.Set("shared::cta"), _root_.scala.collection.immutable.Set("relaxed"), _root_.scala.collection.immutable.Set("sys"), _root_.scala.collection.immutable.Set("b128")), _root_.scala.collection.immutable.Set("multimem"), true))
    )
)

private def instructionSpecChunk10: Vector[InstructionSpec] = Vector(
    InstructionSpec(
      "fabric-instructions-try-red",
      "9.7.10.5.3. Fabric Instructions: fabric.try_red",
      "fabric.try_red",
      "Fabric Instructions",
      Vector("fabric.try_red"),
      Vector("fabric.try_red.async{.multimem}.src.completion_mechanism0.sem.scope.redOpBit.typeBit [dstLeId, dstDataOff], [src], size, [bar];\n\nfabric.try_red.async{.multimem}.src.completion_mechanism0.sem.scope.redOpMinMax.typeMinMax [dstLeId, dstDataOff], [src], size, [bar];\n\nfabric.try_red.async{.multimem}.src.completion_mechanism0.sem.scope.redOpArith.typeArith [dstLeId, dstDataOff], [src], size, [bar];\n\nfabric.try_red.async{.multimem}.src.completion_mechanism1.sem.scope.redOpBit.typeBit [dstLeId, dstDataOff, dstCounterOff], [src], size, [bar];\n\nfabric.try_red.async{.multimem}.src.completion_mechanism1.sem.scope.redOpMinMax.typeMinMax [dstLeId, dstDataOff, dstCounterOff], [src], size, [bar];\n\nfabric.try_red.async{.multimem}.src.completion_mechanism1.sem.scope.redOpAdd.typeAdd [dstLeId, dstDataOff, dstCounterOff], [src], size, [bar];\n\n.src = { .shared::cta }\n.completion_mechanism0 = { .mbarrier::complete_tx::16B.mbarrier::report::fabric }\n.completion_mechanism1 = { .mbarrier::complete_tx::16B.mbarrier::report::fabric.counted::bytes }\n.sem = { .relaxed }\n.scope = { .sys }\n.redOpBit = { .and, .xor, .or }\n.typeBit = { .b32, .b64 }\n.redOpMinMax = { .min, .max }\n.typeMinMax = { .u32, .s32, .u64, .s64, .f16, .bf16 }\n.redOpArith = { .add }\n.typeArith = { .u32, .u64, .f16, .bf16, .f32, .f64 }\n.redOpAdd = { .add }\n.typeAdd = { .u32, .u64, .f16, .bf16, .f32, .f64 }"),
      PtxVersion(9, 3),
      SmTarget(100),
      Vector(InstructionForm("fabric.try_red", Vector(OperandConstraint.Address, OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Address), Vector(_root_.scala.collection.immutable.Set("async"), _root_.scala.collection.immutable.Set("shared::cta"), _root_.scala.collection.immutable.Set("relaxed"), _root_.scala.collection.immutable.Set("sys"), _root_.scala.collection.immutable.Set("and", "or", "xor"), _root_.scala.collection.immutable.Set("b32", "b64")), _root_.scala.collection.immutable.Set("multimem"), true), InstructionForm("fabric.try_red", Vector(OperandConstraint.Address, OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Address), Vector(_root_.scala.collection.immutable.Set("async"), _root_.scala.collection.immutable.Set("shared::cta"), _root_.scala.collection.immutable.Set("relaxed"), _root_.scala.collection.immutable.Set("sys"), _root_.scala.collection.immutable.Set("max", "min"), _root_.scala.collection.immutable.Set("bf16", "f16", "s32", "s64", "u32", "u64")), _root_.scala.collection.immutable.Set("multimem"), true), InstructionForm("fabric.try_red", Vector(OperandConstraint.Address, OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Address), Vector(_root_.scala.collection.immutable.Set("async"), _root_.scala.collection.immutable.Set("shared::cta"), _root_.scala.collection.immutable.Set("relaxed"), _root_.scala.collection.immutable.Set("sys"), _root_.scala.collection.immutable.Set("add"), _root_.scala.collection.immutable.Set("bf16", "f16", "f32", "f64", "u32", "u64")), _root_.scala.collection.immutable.Set("multimem"), true), InstructionForm("fabric.try_red", Vector(OperandConstraint.Address, OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Address), Vector(_root_.scala.collection.immutable.Set("async"), _root_.scala.collection.immutable.Set("shared::cta"), _root_.scala.collection.immutable.Set("relaxed"), _root_.scala.collection.immutable.Set("sys"), _root_.scala.collection.immutable.Set("and", "or", "xor"), _root_.scala.collection.immutable.Set("b32", "b64")), _root_.scala.collection.immutable.Set("multimem"), true), InstructionForm("fabric.try_red", Vector(OperandConstraint.Address, OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Address), Vector(_root_.scala.collection.immutable.Set("async"), _root_.scala.collection.immutable.Set("shared::cta"), _root_.scala.collection.immutable.Set("relaxed"), _root_.scala.collection.immutable.Set("sys"), _root_.scala.collection.immutable.Set("max", "min"), _root_.scala.collection.immutable.Set("bf16", "f16", "s32", "s64", "u32", "u64")), _root_.scala.collection.immutable.Set("multimem"), true), InstructionForm("fabric.try_red", Vector(OperandConstraint.Address, OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Address), Vector(_root_.scala.collection.immutable.Set("async"), _root_.scala.collection.immutable.Set("shared::cta"), _root_.scala.collection.immutable.Set("relaxed"), _root_.scala.collection.immutable.Set("sys"), _root_.scala.collection.immutable.Set("add"), _root_.scala.collection.immutable.Set("bf16", "f16", "f32", "f64", "u32", "u64")), _root_.scala.collection.immutable.Set("multimem"), true))
    ),
    InstructionSpec(
      "fabric-instructions-try-pullred",
      "9.7.10.5.4. Fabric Instructions: fabric.try_pullred",
      "fabric.try_pullred",
      "Fabric Instructions",
      Vector("fabric.try_pullred"),
      Vector("fabric.try_pullred.async.multimem.dst.completion_mechanism.sem.scope.redOpBit.typeBit.sync [dst], [srcLeId, srcDataOff], size, [bar], imm-membermask;\n\nfabric.try_pullred.async.multimem.dst.completion_mechanism.sem.scope.redOpMinMax.typeMinMax.sync [dst], [srcLeId, srcDataOff], size, [bar], imm-membermask;\n\nfabric.try_pullred.async.multimem.dst.completion_mechanism.sem.scope.redOpAdd.typeAdd.sync [dst], [srcLeId, srcDataOff], size, [bar], imm-membermask;\n\nfabric.try_pullred.async.multimem.dst.completion_mechanism.sem.scope.redOpAddAcc16.typeAddAcc16.sync [dst], [srcLeId, srcDataOff], size, [bar], imm-membermask;\n\nfabric.try_pullred.async.multimem.dst.completion_mechanism.sem.scope.redOpAddAcc32.typeAddAcc32.sync [dst], [srcLeId, srcDataOff], size, [bar], imm-membermask;\n\n.dst = { .shared::cta }\n.completion_mechanism = { .mbarrier::complete_tx::bytes.mbarrier::report::fabric }\n.sem = { .relaxed }\n.scope = { .sys }\n.redOpBit = { .and, .xor, .or }\n.typeBit = { .b32, .b64 }\n.redOpMinMax = { .min, .max }\n.typeMinMax = { .u32, .s32, .u64, .s64, .bf16, .f16, .e4m3, .e5m2 }\n.redOpAdd = { .add }\n.typeAdd = { .u32, .u64, .f16, .bf16, .f32 }\n.redOpAddAcc16 = { .add.acc::f16 }\n.typeAddAcc16 = { .e4m3, .e5m2 }\n.redOpAddAcc32 = { .add.acc::f32 }\n.typeAddAcc32 = { .f16, .bf16 }"),
      PtxVersion(9, 3),
      SmTarget(100),
      Vector(InstructionForm("fabric.try_pullred", Vector(OperandConstraint.Address, OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Address, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("async"), _root_.scala.collection.immutable.Set("multimem"), _root_.scala.collection.immutable.Set("shared::cta"), _root_.scala.collection.immutable.Set("relaxed"), _root_.scala.collection.immutable.Set("sys"), _root_.scala.collection.immutable.Set("and", "or", "xor"), _root_.scala.collection.immutable.Set("b32", "b64"), _root_.scala.collection.immutable.Set("sync")), _root_.scala.collection.immutable.Set(), false), InstructionForm("fabric.try_pullred", Vector(OperandConstraint.Address, OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Address, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("async"), _root_.scala.collection.immutable.Set("multimem"), _root_.scala.collection.immutable.Set("shared::cta"), _root_.scala.collection.immutable.Set("relaxed"), _root_.scala.collection.immutable.Set("sys"), _root_.scala.collection.immutable.Set("max", "min"), _root_.scala.collection.immutable.Set("bf16", "e4m3", "e5m2", "f16", "s32", "s64", "u32", "u64"), _root_.scala.collection.immutable.Set("sync")), _root_.scala.collection.immutable.Set(), false), InstructionForm("fabric.try_pullred", Vector(OperandConstraint.Address, OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Address, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("async"), _root_.scala.collection.immutable.Set("multimem"), _root_.scala.collection.immutable.Set("shared::cta"), _root_.scala.collection.immutable.Set("relaxed"), _root_.scala.collection.immutable.Set("sys"), _root_.scala.collection.immutable.Set("add"), _root_.scala.collection.immutable.Set("bf16", "f16", "f32", "u32", "u64"), _root_.scala.collection.immutable.Set("sync")), _root_.scala.collection.immutable.Set(), false), InstructionForm("fabric.try_pullred", Vector(OperandConstraint.Address, OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Address, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("async"), _root_.scala.collection.immutable.Set("multimem"), _root_.scala.collection.immutable.Set("shared::cta"), _root_.scala.collection.immutable.Set("relaxed"), _root_.scala.collection.immutable.Set("sys"), _root_.scala.collection.immutable.Set("e4m3", "e5m2"), _root_.scala.collection.immutable.Set("sync")), _root_.scala.collection.immutable.Set(), false), InstructionForm("fabric.try_pullred", Vector(OperandConstraint.Address, OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Address, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("async"), _root_.scala.collection.immutable.Set("multimem"), _root_.scala.collection.immutable.Set("shared::cta"), _root_.scala.collection.immutable.Set("relaxed"), _root_.scala.collection.immutable.Set("sys"), _root_.scala.collection.immutable.Set("bf16", "f16"), _root_.scala.collection.immutable.Set("sync")), _root_.scala.collection.immutable.Set(), false))
    ),
    InstructionSpec(
      "fabric-instructions-submit",
      "9.7.10.5.5. Fabric Instructions: fabric.submit",
      "fabric.submit",
      "Fabric Instructions",
      Vector("fabric.submit"),
      Vector("fabric.submit{.submitop};\n\n.submitop = { .op_restrict::fetching }"),
      PtxVersion(9, 3),
      SmTarget(100),
      Vector(InstructionForm("fabric.submit", Vector(), Vector(), _root_.scala.collection.immutable.Set("op_restrict::fetching"), true))
    ),
    InstructionSpec(
      "fabric-instructions-wait",
      "9.7.10.5.6. Fabric Instructions: fabric.wait",
      "fabric.wait",
      "Fabric Instructions",
      Vector("fabric.wait"),
      Vector("fabric.wait.sync_restrict::reads;"),
      PtxVersion(9, 3),
      SmTarget(100),
      Vector(InstructionForm("fabric.wait", Vector(), Vector(_root_.scala.collection.immutable.Set("sync_restrict::reads")), _root_.scala.collection.immutable.Set(), true))
    ),
    InstructionSpec(
      "texture-instructions-tex",
      "9.7.11.3. Texture Instructions: tex",
      "tex",
      "Texture Instructions",
      Vector("tex"),
      Vector("tex.geom.v4.dtype.ctype  d, [a, c] {, e} {, f};\ntex.geom.v4.dtype.ctype  d[|p], [a, b, c] {, e} {, f};  // explicit sampler\n\ntex.geom.v2.f16x2.ctype  d[|p], [a, c] {, e} {, f};\ntex.geom.v2.f16x2.ctype  d[|p], [a, b, c] {, e} {, f};  // explicit sampler\n\n// mipmaps\ntex.base.geom.v4.dtype.ctype   d[|p], [a, {b,} c] {, e} {, f};\ntex.level.geom.v4.dtype.ctype  d[|p], [a, {b,} c], lod {, e} {, f};\ntex.grad.geom.v4.dtype.ctype   d[|p], [a, {b,} c], dPdx, dPdy {, e} {, f};\n\ntex.base.geom.v2.f16x2.ctype   d[|p], [a, {b,} c] {, e} {, f};\ntex.level.geom.v2.f16x2.ctype  d[|p], [a, {b,} c], lod {, e} {, f};\ntex.grad.geom.v2.f16x2.ctype   d[|p], [a, {b,} c], dPdx, dPdy {, e} {, f};\n\n.geom  = { .1d, .2d, .3d, .a1d, .a2d, .cube, .acube, .2dms, .a2dms };\n.dtype = { .u32, .s32, .f16,  .f32 };\n.ctype = {       .s32, .f32 };          // .cube, .acube require .f32\n                                        // .2dms, .a2dms require .s32"),
      PtxVersion(1, 0),
      SmTarget(10),
      Vector(InstructionForm("tex", Vector(OperandConstraint.Register, OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("a1d", "a2d", "a2dms", "acube", "cube"), _root_.scala.collection.immutable.Set("v4"), _root_.scala.collection.immutable.Set("f16", "f32", "s32", "u32"), _root_.scala.collection.immutable.Set("f32", "s32")), _root_.scala.collection.immutable.Set(), false), InstructionForm("tex", Vector(OperandConstraint.Register, OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("a1d", "a2d", "a2dms", "acube", "cube"), _root_.scala.collection.immutable.Set("v4"), _root_.scala.collection.immutable.Set("f16", "f32", "s32", "u32"), _root_.scala.collection.immutable.Set("f32", "s32")), _root_.scala.collection.immutable.Set(), false), InstructionForm("tex", Vector(OperandConstraint.Register, OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("a1d", "a2d", "a2dms", "acube", "cube"), _root_.scala.collection.immutable.Set("v2"), _root_.scala.collection.immutable.Set("f16x2"), _root_.scala.collection.immutable.Set("f32", "s32")), _root_.scala.collection.immutable.Set(), false), InstructionForm("tex", Vector(OperandConstraint.Register, OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("a1d", "a2d", "a2dms", "acube", "cube"), _root_.scala.collection.immutable.Set("v2"), _root_.scala.collection.immutable.Set("f16x2"), _root_.scala.collection.immutable.Set("f32", "s32")), _root_.scala.collection.immutable.Set(), false), InstructionForm("tex", Vector(OperandConstraint.Register, OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("base"), _root_.scala.collection.immutable.Set("a1d", "a2d", "a2dms", "acube", "cube"), _root_.scala.collection.immutable.Set("v4"), _root_.scala.collection.immutable.Set("f16", "f32", "s32", "u32"), _root_.scala.collection.immutable.Set("f32", "s32")), _root_.scala.collection.immutable.Set(), false), InstructionForm("tex", Vector(OperandConstraint.Register, OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("level"), _root_.scala.collection.immutable.Set("a1d", "a2d", "a2dms", "acube", "cube"), _root_.scala.collection.immutable.Set("v4"), _root_.scala.collection.immutable.Set("f16", "f32", "s32", "u32"), _root_.scala.collection.immutable.Set("f32", "s32")), _root_.scala.collection.immutable.Set(), false), InstructionForm("tex", Vector(OperandConstraint.Register, OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("grad"), _root_.scala.collection.immutable.Set("a1d", "a2d", "a2dms", "acube", "cube"), _root_.scala.collection.immutable.Set("v4"), _root_.scala.collection.immutable.Set("f16", "f32", "s32", "u32"), _root_.scala.collection.immutable.Set("f32", "s32")), _root_.scala.collection.immutable.Set(), false), InstructionForm("tex", Vector(OperandConstraint.Register, OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("base"), _root_.scala.collection.immutable.Set("a1d", "a2d", "a2dms", "acube", "cube"), _root_.scala.collection.immutable.Set("v2"), _root_.scala.collection.immutable.Set("f16x2"), _root_.scala.collection.immutable.Set("f32", "s32")), _root_.scala.collection.immutable.Set(), false), InstructionForm("tex", Vector(OperandConstraint.Register, OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("level"), _root_.scala.collection.immutable.Set("a1d", "a2d", "a2dms", "acube", "cube"), _root_.scala.collection.immutable.Set("v2"), _root_.scala.collection.immutable.Set("f16x2"), _root_.scala.collection.immutable.Set("f32", "s32")), _root_.scala.collection.immutable.Set(), false), InstructionForm("tex", Vector(OperandConstraint.Register, OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("grad"), _root_.scala.collection.immutable.Set("a1d", "a2d", "a2dms", "acube", "cube"), _root_.scala.collection.immutable.Set("v2"), _root_.scala.collection.immutable.Set("f16x2"), _root_.scala.collection.immutable.Set("f32", "s32")), _root_.scala.collection.immutable.Set(), false))
    ),
    InstructionSpec(
      "texture-instructions-tld4",
      "9.7.11.4. Texture Instructions: tld4",
      "tld4",
      "Texture Instructions",
      Vector("tld4"),
      Vector("tld4.comp.2d.v4.dtype.f32    d[|p], [a, c] {, e} {, f};\ntld4.comp.geom.v4.dtype.f32  d[|p], [a, b, c] {, e} {, f};  // explicit sampler\n\n.comp  = { .r, .g, .b, .a };\n.geom  = { .2d, .a2d, .cube, .acube };\n.dtype = { .u32, .s32, .f32 };"),
      PtxVersion(2, 2),
      SmTarget(20),
      Vector(InstructionForm("tld4", Vector(OperandConstraint.Register, OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("a", "b", "g", "r"), _root_.scala.collection.immutable.Set("v4"), _root_.scala.collection.immutable.Set("f32", "s32", "u32"), _root_.scala.collection.immutable.Set("f32")), _root_.scala.collection.immutable.Set(), false), InstructionForm("tld4", Vector(OperandConstraint.Register, OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("a", "b", "g", "r"), _root_.scala.collection.immutable.Set("a2d", "acube", "cube"), _root_.scala.collection.immutable.Set("v4"), _root_.scala.collection.immutable.Set("f32", "s32", "u32"), _root_.scala.collection.immutable.Set("f32")), _root_.scala.collection.immutable.Set(), false))
    ),
    InstructionSpec(
      "texture-instructions-txq",
      "9.7.11.5. Texture Instructions: txq",
      "txq",
      "Texture Instructions",
      Vector("txq"),
      Vector("txq.tquery.b32         d, [a];       // texture attributes\ntxq.level.tlquery.b32  d, [a], lod;  // texture attributes\ntxq.squery.b32         d, [a];       // sampler attributes\n\n.tquery  = { .width, .height, .depth,\n             .channel_data_type, .channel_order,\n             .normalized_coords, .array_size,\n             .num_mipmap_levels, .num_samples};\n\n.tlquery = { .width, .height, .depth };\n\n.squery  = { .force_unnormalized_coords, .filter_mode,\n             .addr_mode_0, addr_mode_1, addr_mode_2 };"),
      PtxVersion(1, 5),
      SmTarget(10),
      Vector(InstructionForm("txq", Vector(OperandConstraint.Register, OperandConstraint.Address), Vector(_root_.scala.collection.immutable.Set("array_size", "channel_data_type", "channel_order", "depth", "height", "normalized_coords", "num_mipmap_levels", "num_samples", "width"), _root_.scala.collection.immutable.Set("b32")), _root_.scala.collection.immutable.Set(), false), InstructionForm("txq", Vector(OperandConstraint.Register, OperandConstraint.Address, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("level"), _root_.scala.collection.immutable.Set("depth", "height", "width"), _root_.scala.collection.immutable.Set("b32")), _root_.scala.collection.immutable.Set(), false), InstructionForm("txq", Vector(OperandConstraint.Register, OperandConstraint.Address), Vector(_root_.scala.collection.immutable.Set("addr_mode_0", "addr_mode_1", "addr_mode_2", "filter_mode", "force_unnormalized_coords"), _root_.scala.collection.immutable.Set("b32")), _root_.scala.collection.immutable.Set(), false))
    ),
    InstructionSpec(
      "texture-instructions-istypep",
      "9.7.11.6. Texture Instructions: istypep",
      "istypep",
      "Texture Instructions",
      Vector("istypep"),
      Vector("istypep.type   p, a;  // result is .pred\n\n.type = { .texref, .samplerref, .surfref };"),
      PtxVersion(4, 0),
      SmTarget(30),
      Vector(InstructionForm("istypep", Vector(OperandConstraint.Register, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("samplerref", "surfref", "texref")), _root_.scala.collection.immutable.Set(), true))
    ),
    InstructionSpec(
      "surface-instructions-suld",
      "9.7.12.1. Surface Instructions: suld",
      "suld",
      "Surface Instructions",
      Vector("suld"),
      Vector("suld.b.geom{.cop}.vec.dtype.clamp  d, [a, b];  // unformatted\n\n.geom  = { .1d, .2d, .3d, .a1d, .a2d };\n.cop   = { .ca, .cg, .cs, .cv };               // cache operation\n.vec   = { none, .v2, .v4 };\n.dtype = { .b8 , .b16, .b32, .b64 };\n.clamp = { .trap, .clamp, .zero };"),
      PtxVersion(1, 0),
      SmTarget(20),
      Vector(InstructionForm("suld", Vector(OperandConstraint.Register, OperandConstraint.Address), Vector(_root_.scala.collection.immutable.Set("b"), _root_.scala.collection.immutable.Set("a1d", "a2d"), _root_.scala.collection.immutable.Set("none", "v2", "v4"), _root_.scala.collection.immutable.Set("b16", "b32", "b64", "b8"), _root_.scala.collection.immutable.Set("clamp", "trap", "zero")), _root_.scala.collection.immutable.Set("ca", "cg", "cs", "cv"), true))
    ),
    InstructionSpec(
      "surface-instructions-sust",
      "9.7.12.2. Surface Instructions: sust",
      "sust",
      "Surface Instructions",
      Vector("sust"),
      Vector("sust.b.{1d,2d,3d}{.cop}.vec.ctype.clamp  [a, b], c;  // unformatted\nsust.p.{1d,2d,3d}.vec.b32.clamp          [a, b], c;  // formatted\n\nsust.b.{a1d,a2d}{.cop}.vec.ctype.clamp   [a, b], c;  // unformatted\n\n.cop   = { .wb, .cg, .cs, .wt };                     // cache operation\n.vec   = { none, .v2, .v4 };\n.ctype = { .b8 , .b16, .b32, .b64 };\n.clamp = { .trap, .clamp, .zero };"),
      PtxVersion(1, 0),
      SmTarget(20),
      Vector(InstructionForm("sust", Vector(OperandConstraint.Address, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("b"), _root_.scala.collection.immutable.Set("none", "v2", "v4"), _root_.scala.collection.immutable.Set("b16", "b32", "b64", "b8"), _root_.scala.collection.immutable.Set("clamp", "trap", "zero")), _root_.scala.collection.immutable.Set("cg", "cs", "wb", "wt"), false), InstructionForm("sust", Vector(OperandConstraint.Address, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("p"), _root_.scala.collection.immutable.Set("none", "v2", "v4"), _root_.scala.collection.immutable.Set("b32"), _root_.scala.collection.immutable.Set("clamp", "trap", "zero")), _root_.scala.collection.immutable.Set(), false))
    ),
    InstructionSpec(
      "surface-instructions-sured",
      "9.7.12.3. Surface Instructions: sured",
      "sured",
      "Surface Instructions",
      Vector("sured"),
      Vector("sured.b.op.geom.ctype.clamp  [a,b],c; // byte addressing\nsured.p.op.geom.ctype.clamp  [a,b],c; // sample addressing\n\n.op    = { .add, .min, .max, .and, .or };\n.geom  = { .1d, .2d, .3d };\n.ctype = { .u32, .u64, .s32, .b32, .s64 };  // for sured.b\n.ctype = { .b32, .b64 };                    // for sured.p\n.clamp = { .trap, .clamp, .zero };"),
      PtxVersion(2, 0),
      SmTarget(20),
      Vector(InstructionForm("sured", Vector(OperandConstraint.Address, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("b"), _root_.scala.collection.immutable.Set("add", "and", "max", "min", "or"), _root_.scala.collection.immutable.Set("b32", "b64"), _root_.scala.collection.immutable.Set("clamp", "trap", "zero")), _root_.scala.collection.immutable.Set(), false), InstructionForm("sured", Vector(OperandConstraint.Address, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("p"), _root_.scala.collection.immutable.Set("add", "and", "max", "min", "or"), _root_.scala.collection.immutable.Set("b32", "b64"), _root_.scala.collection.immutable.Set("clamp", "trap", "zero")), _root_.scala.collection.immutable.Set(), false))
    ),
    InstructionSpec(
      "surface-instructions-suq",
      "9.7.12.4. Surface Instructions: suq",
      "suq",
      "Surface Instructions",
      Vector("suq"),
      Vector("suq.query.b32   d, [a];\n\n.query = { .width, .height, .depth,\n           .channel_data_type, .channel_order,\n           .array_size, .memory_layout };"),
      PtxVersion(1, 5),
      SmTarget(10),
      Vector(InstructionForm("suq", Vector(OperandConstraint.Register, OperandConstraint.Address), Vector(_root_.scala.collection.immutable.Set("array_size", "channel_data_type", "channel_order", "depth", "height", "memory_layout", "width"), _root_.scala.collection.immutable.Set("b32")), _root_.scala.collection.immutable.Set(), false))
    )
)

private def instructionSpecChunk11: Vector[InstructionSpec] = Vector(
    InstructionSpec(
      "control-flow-instructions-curly-braces",
      "9.7.13.1. Control Flow Instructions: {}",
      "{}",
      "Control Flow Instructions",
      Vector(),
      Vector("{ instructionList }"),
      PtxVersion(1, 0),
      SmTarget(10),
      Vector()
    ),
    InstructionSpec(
      "control-flow-instructions-at",
      "9.7.13.2. Control Flow Instructions: @",
      "@",
      "Control Flow Instructions",
      Vector(),
      Vector("@{!}p    instruction;"),
      PtxVersion(1, 0),
      SmTarget(10),
      Vector()
    ),
    InstructionSpec(
      "control-flow-instructions-bra",
      "9.7.13.3. Control Flow Instructions: bra",
      "bra",
      "Control Flow Instructions",
      Vector("bra"),
      Vector("@p   bra{.uni}  tgt;           // tgt is a label\n     bra{.uni}  tgt;           // unconditional branch"),
      PtxVersion(1, 0),
      SmTarget(10),
      Vector(InstructionForm("bra", Vector(OperandConstraint.Value), Vector(), _root_.scala.collection.immutable.Set("uni"), true))
    ),
    InstructionSpec(
      "control-flow-instructions-brx-idx",
      "9.7.13.4. Control Flow Instructions: brx.idx",
      "brx.idx",
      "Control Flow Instructions",
      Vector("brx.idx"),
      Vector("@p    brx.idx{.uni} index, tlist;\n      brx.idx{.uni} index, tlist;"),
      PtxVersion(6, 0),
      SmTarget(30),
      Vector(InstructionForm("brx.idx", Vector(OperandConstraint.Value, OperandConstraint.Value), Vector(), _root_.scala.collection.immutable.Set("uni"), true))
    ),
    InstructionSpec(
      "control-flow-instructions-call",
      "9.7.13.5. Control Flow Instructions: call",
      "call",
      "Control Flow Instructions",
      Vector("call"),
      Vector("// direct call to named function, func is a symbol\ncall{.uni} (ret-param), func, (param-list);\ncall{.uni} func, (param-list);\ncall{.uni} func;\n\n// indirect call via pointer, with full list of call targets\ncall{.uni} (ret-param), fptr, (param-list), flist;\ncall{.uni} fptr, (param-list), flist;\ncall{.uni} fptr, flist;\n\n// indirect call via pointer, with no knowledge of call targets\ncall{.uni} (ret-param), fptr, (param-list), fproto;\ncall{.uni} fptr, (param-list), fproto;\ncall{.uni} fptr, fproto;"),
      PtxVersion(1, 0),
      SmTarget(20),
      Vector(InstructionForm("call", Vector(OperandConstraint.Unknown("unparsed"), OperandConstraint.Symbol, OperandConstraint.Unknown("unparsed")), Vector(), _root_.scala.collection.immutable.Set("uni"), false), InstructionForm("call", Vector(OperandConstraint.Symbol, OperandConstraint.Unknown("unparsed")), Vector(), _root_.scala.collection.immutable.Set("uni"), false), InstructionForm("call", Vector(OperandConstraint.Symbol), Vector(), _root_.scala.collection.immutable.Set("uni"), true), InstructionForm("call", Vector(OperandConstraint.Unknown("unparsed"), OperandConstraint.Value, OperandConstraint.Unknown("unparsed"), OperandConstraint.Value), Vector(), _root_.scala.collection.immutable.Set("uni"), false), InstructionForm("call", Vector(OperandConstraint.Value, OperandConstraint.Unknown("unparsed"), OperandConstraint.Value), Vector(), _root_.scala.collection.immutable.Set("uni"), false), InstructionForm("call", Vector(OperandConstraint.Value, OperandConstraint.Value), Vector(), _root_.scala.collection.immutable.Set("uni"), true), InstructionForm("call", Vector(OperandConstraint.Unknown("unparsed"), OperandConstraint.Value, OperandConstraint.Unknown("unparsed"), OperandConstraint.Value), Vector(), _root_.scala.collection.immutable.Set("uni"), false), InstructionForm("call", Vector(OperandConstraint.Value, OperandConstraint.Unknown("unparsed"), OperandConstraint.Value), Vector(), _root_.scala.collection.immutable.Set("uni"), false), InstructionForm("call", Vector(OperandConstraint.Value, OperandConstraint.Value), Vector(), _root_.scala.collection.immutable.Set("uni"), true))
    ),
    InstructionSpec(
      "control-flow-instructions-ret",
      "9.7.13.6. Control Flow Instructions: ret",
      "ret",
      "Control Flow Instructions",
      Vector("ret"),
      Vector("ret{.uni};"),
      PtxVersion(1, 0),
      SmTarget(10),
      Vector(InstructionForm("ret", Vector(), Vector(), _root_.scala.collection.immutable.Set("uni"), true))
    ),
    InstructionSpec(
      "control-flow-instructions-exit",
      "9.7.13.7. Control Flow Instructions: exit",
      "exit",
      "Control Flow Instructions",
      Vector("exit"),
      Vector("exit;"),
      PtxVersion(1, 0),
      SmTarget(10),
      Vector(InstructionForm("exit", Vector(), Vector(), _root_.scala.collection.immutable.Set(), true))
    ),
    InstructionSpec(
      "parallel-synchronization-and-communication-instructions-bar",
      "9.7.14.1. Parallel Synchronization and Communication Instructions: bar, barrier",
      "bar, barrier",
      "Parallel Synchronization and Communication Instructions",
      Vector("barrier", "bar"),
      Vector("barrier{.cta}.sync{.aligned}      a{, b};\nbarrier{.cta}.arrive{.aligned}    a, b;\n\nbarrier{.cta}.red.popc{.aligned}.u32  d, a{, b}, {!}c;\nbarrier{.cta}.red.op{.aligned}.pred   p, a{, b}, {!}c;\n\nbar{.cta}.sync      a{, b};\nbar{.cta}.arrive    a, b;\n\nbar{.cta}.red.popc.u32  d, a{, b}, {!}c;\nbar{.cta}.red.op.pred   p, a{, b}, {!}c;\n\n.op = { .and, .or };"),
      PtxVersion(1, 0),
      SmTarget(20),
      Vector(InstructionForm("barrier", Vector(OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("sync")), _root_.scala.collection.immutable.Set("aligned", "cta"), false), InstructionForm("barrier", Vector(OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("arrive")), _root_.scala.collection.immutable.Set("aligned", "cta"), false), InstructionForm("barrier", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("red"), _root_.scala.collection.immutable.Set("popc"), _root_.scala.collection.immutable.Set("u32")), _root_.scala.collection.immutable.Set("aligned", "cta"), false), InstructionForm("barrier", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("red"), _root_.scala.collection.immutable.Set("and", "or"), _root_.scala.collection.immutable.Set("pred")), _root_.scala.collection.immutable.Set("aligned", "cta"), false), InstructionForm("bar", Vector(OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("sync")), _root_.scala.collection.immutable.Set("cta"), false), InstructionForm("bar", Vector(OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("arrive")), _root_.scala.collection.immutable.Set("cta"), false), InstructionForm("bar", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("red"), _root_.scala.collection.immutable.Set("popc"), _root_.scala.collection.immutable.Set("u32")), _root_.scala.collection.immutable.Set("cta"), false), InstructionForm("bar", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("red"), _root_.scala.collection.immutable.Set("and", "or"), _root_.scala.collection.immutable.Set("pred")), _root_.scala.collection.immutable.Set("cta"), false))
    ),
    InstructionSpec(
      "parallel-synchronization-and-communication-instructions-bar-warp-sync",
      "9.7.14.2. Parallel Synchronization and Communication Instructions: bar.warp.sync",
      "bar.warp.sync",
      "Parallel Synchronization and Communication Instructions",
      Vector("bar.warp.sync"),
      Vector("bar.warp.sync      membermask;"),
      PtxVersion(6, 0),
      SmTarget(30),
      Vector(InstructionForm("bar.warp.sync", Vector(OperandConstraint.Value), Vector(), _root_.scala.collection.immutable.Set(), true))
    ),
    InstructionSpec(
      "parallel-synchronization-and-communication-instructions-barrier-cluster",
      "9.7.14.3. Parallel Synchronization and Communication Instructions: barrier.cluster",
      "barrier.cluster",
      "Parallel Synchronization and Communication Instructions",
      Vector("barrier.cluster"),
      Vector("barrier.cluster.arrive{.sem}{.aligned};\nbarrier.cluster.wait{.acquire}{.aligned};\n\n.sem = {.release, .relaxed}"),
      PtxVersion(7, 8),
      SmTarget(90),
      Vector(InstructionForm("barrier.cluster", Vector(), Vector(_root_.scala.collection.immutable.Set("arrive")), _root_.scala.collection.immutable.Set("aligned", "relaxed", "release"), true), InstructionForm("barrier.cluster", Vector(), Vector(_root_.scala.collection.immutable.Set("wait")), _root_.scala.collection.immutable.Set("acquire", "aligned"), true))
    ),
    InstructionSpec(
      "parallel-synchronization-and-communication-instructions-membar",
      "9.7.14.4. Parallel Synchronization and Communication Instructions: membar / fence",
      "membar / fence",
      "Parallel Synchronization and Communication Instructions",
      Vector("fence", "membar"),
      Vector("// Thread fence:\nfence{.sem}.scope;\n\n// Thread fence:\nfence.acquire.sync_restrict::shared::cluster.cluster;\nfence.release.sync_restrict::shared::cta.cluster;\n\n// Operation fence:\nfence.op_restrict.release.cluster;\n\n// Proxy fence (bi-directional):\nfence.proxy.proxykind;\n\n// Proxy fence (uni-directional):\nfence.proxy.to_proxykind::from_proxykind.release.scope;\nfence.proxy.to_proxykind::from_proxykind.acquire.scope  [addr], size;\nfence.proxy.async::generic.acquire.sync_restrict::shared::cluster.cluster;\nfence.proxy.async::generic.release.sync_restrict::shared::cta.cluster;\nfence.proxy.to_proxykind::from_proxykind_fabric.alias.sem_fabric.sys;\n\n// Old style membar:\nmembar.level;\nmembar.proxy.proxykind;\n\n.sem       = { .sc, .acq_rel, .acquire, .release };\n.scope     = { .cta, .cluster, .gpu, .sys };\n.level     = { .cta, .gl, .sys };\n.proxykind = { .alias, .async, .async.global, .async.shared::{cta, cluster} };\n.op_restrict = { .mbarrier_init };\n.to_proxykind::from_proxykind = {.tensormap::generic};\n.to_proxykind::from_proxykind_fabric = {.generic::fabric, .fabric::generic, .fabric::fabric };\n.sem_fabric = { .acquire, .release };"),
      PtxVersion(1, 0),
      SmTarget(20),
      Vector(InstructionForm("fence", Vector(), Vector(_root_.scala.collection.immutable.Set("cluster", "cta", "gpu", "sys")), _root_.scala.collection.immutable.Set("acq_rel", "acquire", "release", "sc"), false), InstructionForm("fence", Vector(), Vector(_root_.scala.collection.immutable.Set("acquire"), _root_.scala.collection.immutable.Set("sync_restrict::shared::cluster"), _root_.scala.collection.immutable.Set("cluster")), _root_.scala.collection.immutable.Set(), false), InstructionForm("fence", Vector(), Vector(_root_.scala.collection.immutable.Set("release"), _root_.scala.collection.immutable.Set("sync_restrict::shared::cta"), _root_.scala.collection.immutable.Set("cluster")), _root_.scala.collection.immutable.Set(), false), InstructionForm("fence", Vector(), Vector(_root_.scala.collection.immutable.Set("mbarrier_init"), _root_.scala.collection.immutable.Set("release"), _root_.scala.collection.immutable.Set("cluster")), _root_.scala.collection.immutable.Set(), false), InstructionForm("fence", Vector(), Vector(_root_.scala.collection.immutable.Set("proxy"), _root_.scala.collection.immutable.Set("alias", "async", "cluster")), _root_.scala.collection.immutable.Set(), false), InstructionForm("fence", Vector(), Vector(_root_.scala.collection.immutable.Set("proxy"), _root_.scala.collection.immutable.Set("tensormap::generic"), _root_.scala.collection.immutable.Set("release"), _root_.scala.collection.immutable.Set("cluster", "cta", "gpu", "sys")), _root_.scala.collection.immutable.Set(), false), InstructionForm("fence", Vector(OperandConstraint.Address, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("proxy"), _root_.scala.collection.immutable.Set("tensormap::generic"), _root_.scala.collection.immutable.Set("acquire"), _root_.scala.collection.immutable.Set("cluster", "cta", "gpu", "sys")), _root_.scala.collection.immutable.Set(), false), InstructionForm("fence", Vector(), Vector(_root_.scala.collection.immutable.Set("proxy"), _root_.scala.collection.immutable.Set("async::generic"), _root_.scala.collection.immutable.Set("acquire"), _root_.scala.collection.immutable.Set("sync_restrict::shared::cluster"), _root_.scala.collection.immutable.Set("cluster")), _root_.scala.collection.immutable.Set(), false), InstructionForm("fence", Vector(), Vector(_root_.scala.collection.immutable.Set("proxy"), _root_.scala.collection.immutable.Set("async::generic"), _root_.scala.collection.immutable.Set("release"), _root_.scala.collection.immutable.Set("sync_restrict::shared::cta"), _root_.scala.collection.immutable.Set("cluster")), _root_.scala.collection.immutable.Set(), false), InstructionForm("fence", Vector(), Vector(_root_.scala.collection.immutable.Set("proxy"), _root_.scala.collection.immutable.Set("fabric::fabric", "fabric::generic", "generic::fabric"), _root_.scala.collection.immutable.Set("alias"), _root_.scala.collection.immutable.Set("acquire", "release"), _root_.scala.collection.immutable.Set("sys")), _root_.scala.collection.immutable.Set(), false), InstructionForm("membar", Vector(), Vector(_root_.scala.collection.immutable.Set("cta", "gl", "sys")), _root_.scala.collection.immutable.Set(), false), InstructionForm("membar", Vector(), Vector(_root_.scala.collection.immutable.Set("proxy"), _root_.scala.collection.immutable.Set("alias", "async", "cluster")), _root_.scala.collection.immutable.Set(), false))
    ),
    InstructionSpec(
      "parallel-synchronization-and-communication-instructions-atom",
      "9.7.14.5. Parallel Synchronization and Communication Instructions: atom",
      "atom",
      "Parallel Synchronization and Communication Instructions",
      Vector("atom"),
      Vector("atom{.sem}{.scope}{.space}.op{.level::cache_hint}.type d, [a], b{, cache_policy};\n\natom{.sem}{.scope}{.space}.cas.type1 d, [a], b, c;\n\natom{.sem}{.scope}{.space}.exch{.level::cache_hint}.type2 d, [a], b {, cache_policy};\n\natom{.sem}{.scope}{.space}.add.noftz{.level::cache_hint}.f16     d, [a], b{, cache_policy};\natom{.sem}{.scope}{.space}.add.noftz{.level::cache_hint}.f16x2   d, [a], b{, cache_policy};\n\natom{.sem}{.scope}{.space}.add.noftz{.level::cache_hint}.bf16    d, [a], b{, cache_policy};\natom{.sem}{.scope}{.space}.add.noftz{.level::cache_hint}.bf16x2  d, [a], b{, cache_policy};\n\n.space =              { .global, .shared{::cta, ::cluster} };\n.sem =                { .relaxed, .acquire, .release, .acq_rel };\n.scope =              { .cta, .cluster, .gpu, .sys };\n\n.op =                 { .and, .or, .xor,\n                        .add, .inc, .dec,\n                        .min, .max };\n.level::cache_hint =  { .L2::cache_hint };\n.type =               { .b32, .b64, .u32, .u64, .s32, .s64, .f32, .f64 };\n.type1 =              { .b16, .b32, .b64, .b128 };\n.type2 =              { .b32, .b64, .b128 };", "atom{.sem}{.scope}{.global}.add{.level::cache_hint}.vec_32_bit.f32                  d, [a], b{, cache_policy};\natom{.sem}{.scope}{.global}.op.noftz{.level::cache_hint}.vec_16_bit.half_word_type  d, [a], b{, cache_policy};\natom{.sem}{.scope}{.global}.op.noftz{.level::cache_hint}.vec_32_bit.packed_type     d, [a], b{, cache_policy};\n\n.sem =               { .relaxed, .acquire, .release, .acq_rel };\n.scope =             { .cta, .cluster, .gpu, .sys };\n.op =                { .add, .min, .max };\n.half_word_type =    { .f16, .bf16 };\n.packed_type =       { .f16x2, .bf16x2 };\n.vec_16_bit =        { .v2, .v4, .v8 }\n.vec_32_bit =        { .v2, .v4 };\n.level::cache_hint = { .L2::cache_hint }"),
      PtxVersion(1, 0),
      SmTarget(11),
      Vector(InstructionForm("atom", Vector(OperandConstraint.Register, OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("add", "and", "dec", "inc", "max", "min", "or", "xor"), _root_.scala.collection.immutable.Set("b32", "b64", "f32", "f64", "s32", "s64", "u32", "u64")), _root_.scala.collection.immutable.Set("L2::cache_hint", "acq_rel", "acquire", "cluster", "cta", "global", "gpu", "relaxed", "release", "sys"), false), InstructionForm("atom", Vector(OperandConstraint.Register, OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("cas"), _root_.scala.collection.immutable.Set("b128", "b16", "b32", "b64")), _root_.scala.collection.immutable.Set("acq_rel", "acquire", "cluster", "cta", "global", "gpu", "relaxed", "release", "sys"), false), InstructionForm("atom", Vector(OperandConstraint.Register, OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("exch"), _root_.scala.collection.immutable.Set("b128", "b32", "b64")), _root_.scala.collection.immutable.Set("L2::cache_hint", "acq_rel", "acquire", "cluster", "cta", "global", "gpu", "relaxed", "release", "sys"), false), InstructionForm("atom", Vector(OperandConstraint.Register, OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("add"), _root_.scala.collection.immutable.Set("noftz"), _root_.scala.collection.immutable.Set("f16")), _root_.scala.collection.immutable.Set("L2::cache_hint", "acq_rel", "acquire", "cluster", "cta", "global", "gpu", "relaxed", "release", "sys"), false), InstructionForm("atom", Vector(OperandConstraint.Register, OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("add"), _root_.scala.collection.immutable.Set("noftz"), _root_.scala.collection.immutable.Set("f16x2")), _root_.scala.collection.immutable.Set("L2::cache_hint", "acq_rel", "acquire", "cluster", "cta", "global", "gpu", "relaxed", "release", "sys"), false), InstructionForm("atom", Vector(OperandConstraint.Register, OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("add"), _root_.scala.collection.immutable.Set("noftz"), _root_.scala.collection.immutable.Set("bf16")), _root_.scala.collection.immutable.Set("L2::cache_hint", "acq_rel", "acquire", "cluster", "cta", "global", "gpu", "relaxed", "release", "sys"), false), InstructionForm("atom", Vector(OperandConstraint.Register, OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("add"), _root_.scala.collection.immutable.Set("noftz"), _root_.scala.collection.immutable.Set("bf16x2")), _root_.scala.collection.immutable.Set("L2::cache_hint", "acq_rel", "acquire", "cluster", "cta", "global", "gpu", "relaxed", "release", "sys"), false), InstructionForm("atom", Vector(OperandConstraint.Register, OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("add"), _root_.scala.collection.immutable.Set("v2", "v4"), _root_.scala.collection.immutable.Set("f32")), _root_.scala.collection.immutable.Set("L2::cache_hint", "acq_rel", "acquire", "cluster", "cta", "global", "gpu", "relaxed", "release", "sys"), false), InstructionForm("atom", Vector(OperandConstraint.Register, OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("add", "max", "min"), _root_.scala.collection.immutable.Set("noftz"), _root_.scala.collection.immutable.Set("v2", "v4", "v8"), _root_.scala.collection.immutable.Set("bf16", "f16")), _root_.scala.collection.immutable.Set("L2::cache_hint", "acq_rel", "acquire", "cluster", "cta", "global", "gpu", "relaxed", "release", "sys"), false), InstructionForm("atom", Vector(OperandConstraint.Register, OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("add", "max", "min"), _root_.scala.collection.immutable.Set("noftz"), _root_.scala.collection.immutable.Set("v2", "v4"), _root_.scala.collection.immutable.Set("bf16x2", "f16x2")), _root_.scala.collection.immutable.Set("L2::cache_hint", "acq_rel", "acquire", "cluster", "cta", "global", "gpu", "relaxed", "release", "sys"), false))
    )
)

private def instructionSpecChunk12: Vector[InstructionSpec] = Vector(
    InstructionSpec(
      "parallel-synchronization-and-communication-instructions-red",
      "9.7.14.6. Parallel Synchronization and Communication Instructions: red",
      "red",
      "Parallel Synchronization and Communication Instructions",
      Vector("red"),
      Vector("red{.sem}{.scope}{.space}.op{.level::cache_hint}.type          [a], b{, cache_policy};\n\nred{.sem}{.scope}{.space}.add.noftz{.level::cache_hint}.f16    [a], b{, cache_policy};\n\nred{.sem}{.scope}{.space}.add.noftz{.level::cache_hint}.f16x2  [a], b{, cache_policy};\n\nred{.sem}{.scope}{.space}.add.noftz{.level::cache_hint}.bf16\n                                                      [a], b {, cache_policy};\n\nred{.sem}{.scope}{.space}.add.noftz{.level::cache_hint}.bf16x2\n                                                      [a], b {, cache_policy};\n\n.space =              { .global, .shared{::cta, ::cluster} };\n.sem =                { .relaxed, .release };\n.scope =              { .cta, .cluster, .gpu, .sys };\n\n.op =                 { .and, .or, .xor,\n                        .add, .inc, .dec,\n                        .min, .max };\n.level::cache_hint =  { .L2::cache_hint };\n.type =               { .b32, .b64, .u32, .u64, .s32, .s64, .f32, .f64 };", "red{.sem}{.scope}{.global}.add{.level::cache_hint}.vec_32_bit.f32 [a], b{, cache_policy};\nred{.sem}{.scope}{.global}.op.noftz{.level::cache_hint}. vec_16_bit.half_word_type [a], b{, cache_policy};\nred{.sem}{.scope}{.global}.op.noftz{.level::cache_hint}.vec_32_bit.packed_type [a], b {, cache_policy};\n\n.sem =                { .relaxed, .release };\n.scope =              { .cta, .cluster, .gpu, .sys };\n.op =                 { .add, .min, .max };\n.half_word_type =     { .f16, .bf16 };\n.packed_type =        { .f16x2,.bf16x2 };\n.vec_16_bit =         { .v2, .v4, .v8 }\n.vec_32_bit =         { .v2, .v4 };\n.level::cache_hint =  { .L2::cache_hint }"),
      PtxVersion(1, 2),
      SmTarget(11),
      Vector(InstructionForm("red", Vector(OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("add", "and", "dec", "inc", "max", "min", "or", "xor"), _root_.scala.collection.immutable.Set("b32", "b64", "f32", "f64", "s32", "s64", "u32", "u64")), _root_.scala.collection.immutable.Set("L2::cache_hint", "cluster", "cta", "global", "gpu", "relaxed", "release", "sys"), false), InstructionForm("red", Vector(OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("add"), _root_.scala.collection.immutable.Set("noftz"), _root_.scala.collection.immutable.Set("f16")), _root_.scala.collection.immutable.Set("L2::cache_hint", "cluster", "cta", "global", "gpu", "relaxed", "release", "sys"), false), InstructionForm("red", Vector(OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("add"), _root_.scala.collection.immutable.Set("noftz"), _root_.scala.collection.immutable.Set("f16x2")), _root_.scala.collection.immutable.Set("L2::cache_hint", "cluster", "cta", "global", "gpu", "relaxed", "release", "sys"), false), InstructionForm("red", Vector(OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("add"), _root_.scala.collection.immutable.Set("noftz"), _root_.scala.collection.immutable.Set("bf16")), _root_.scala.collection.immutable.Set("L2::cache_hint", "cluster", "cta", "global", "gpu", "relaxed", "release", "sys"), false), InstructionForm("red", Vector(OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("add"), _root_.scala.collection.immutable.Set("noftz"), _root_.scala.collection.immutable.Set("bf16x2")), _root_.scala.collection.immutable.Set("L2::cache_hint", "cluster", "cta", "global", "gpu", "relaxed", "release", "sys"), false), InstructionForm("red", Vector(OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("add"), _root_.scala.collection.immutable.Set("v2", "v4"), _root_.scala.collection.immutable.Set("f32")), _root_.scala.collection.immutable.Set("L2::cache_hint", "cluster", "cta", "global", "gpu", "relaxed", "release", "sys"), false), InstructionForm("red", Vector(OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("add", "max", "min"), _root_.scala.collection.immutable.Set("noftz")), _root_.scala.collection.immutable.Set("L2::cache_hint", "cluster", "cta", "global", "gpu", "relaxed", "release", "sys"), false), InstructionForm("red", Vector(OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("add", "max", "min"), _root_.scala.collection.immutable.Set("noftz"), _root_.scala.collection.immutable.Set("v2", "v4"), _root_.scala.collection.immutable.Set("bf16x2", "f16x2")), _root_.scala.collection.immutable.Set("L2::cache_hint", "cluster", "cta", "global", "gpu", "relaxed", "release", "sys"), false))
    ),
    InstructionSpec(
      "parallel-synchronization-and-communication-instructions-red-async",
      "9.7.14.7. Parallel Synchronization and Communication Instructions: red.async",
      "red.async",
      "Parallel Synchronization and Communication Instructions",
      Vector("red.async"),
      Vector("// Increment and Decrement reductions\nred.async.sem.scope{.ss}.completion_mechanism.op.type [a], b, [mbar];\n\n.sem  =                 { .relaxed };\n.scope =                { .cluster };\n.ss   =                 { .shared::cluster };\n.op   =                 { .inc, .dec };\n.type =                 { .u32 };\n.completion_mechanism = { .mbarrier::complete_tx::bytes };\n\n\n// MIN and MAX reductions\nred.async.sem.scope{.ss}.completion_mechanism.op.type [a], b, [mbar];\n\n.sem  = { .relaxed };\n.scope = { .cluster };\n.ss   = { .shared::cluster };\n.op   = { .min, .max };\n.type = { .u32, .s32 };\n.completion_mechanism = { .mbarrier::complete_tx::bytes };\n\n// Bitwise AND, OR and XOR reductions\nred.async.sem.scope{.ss}.completion_mechanism.op.type [a], b, [mbar];\n\n.sem  = { .relaxed };\n.scope = { .cluster };\n.ss   = { .shared::cluster };\n.op   = { .and, .or, .xor };\n.type = { .b32 };\n.completion_mechanism = { .mbarrier::complete_tx::bytes };\n\n// ADD reductions\nred.async.sem.scope{.ss}.completion_mechanism.add.type [a], b, [mbar];\n\n.sem  = { .relaxed };\n.scope = { .cluster };\n.ss   = { .shared::cluster };\n.type = { .u32, .s32, .u64 };\n.completion_mechanism = { .mbarrier::complete_tx::bytes };\n\nred.async{.mmio}.sem.scope{.ss}.add.type [a], b;\n\n.sem  = { .release };\n.scope = { .gpu, .sys };\n.ss   = { .global };\n.type = { .u32, .s32, .u64, .s64 };"),
      PtxVersion(8, 1),
      SmTarget(90),
      Vector(InstructionForm("red.async", Vector(OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Address), Vector(_root_.scala.collection.immutable.Set("release"), _root_.scala.collection.immutable.Set("gpu", "sys"), _root_.scala.collection.immutable.Set("mbarrier::complete_tx::bytes"), _root_.scala.collection.immutable.Set("and", "or", "xor"), _root_.scala.collection.immutable.Set("s32", "s64", "u32", "u64")), _root_.scala.collection.immutable.Set("global"), false), InstructionForm("red.async", Vector(OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Address), Vector(_root_.scala.collection.immutable.Set("release"), _root_.scala.collection.immutable.Set("gpu", "sys"), _root_.scala.collection.immutable.Set("mbarrier::complete_tx::bytes"), _root_.scala.collection.immutable.Set("add"), _root_.scala.collection.immutable.Set("s32", "s64", "u32", "u64")), _root_.scala.collection.immutable.Set("global"), false), InstructionForm("red.async", Vector(OperandConstraint.Address, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("release"), _root_.scala.collection.immutable.Set("gpu", "sys"), _root_.scala.collection.immutable.Set("add"), _root_.scala.collection.immutable.Set("s32", "s64", "u32", "u64")), _root_.scala.collection.immutable.Set("global", "mmio"), false))
    ),
    InstructionSpec(
      "parallel-synchronization-and-communication-instructions-multimem-red-async",
      "9.7.14.8. Parallel Synchronization and Communication Instructions: multimem.red.async",
      "multimem.red.async",
      "Parallel Synchronization and Communication Instructions",
      Vector("multimem.red.async"),
      Vector("multimem.red.async.sem.scope{.ss}.op.type [a], b;\n\n.sem   = { .release };\n.scope = { .gpu, .sys };\n.ss    = { .global };\n.op    = { .add };\n.type  = { .u32, .s32, .u64 };"),
      PtxVersion(9, 3),
      SmTarget(100),
      Vector(InstructionForm("multimem.red.async", Vector(OperandConstraint.Address, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("release"), _root_.scala.collection.immutable.Set("gpu", "sys"), _root_.scala.collection.immutable.Set("add"), _root_.scala.collection.immutable.Set("s32", "u32", "u64")), _root_.scala.collection.immutable.Set("global"), true))
    ),
    InstructionSpec(
      "parallel-synchronization-and-communication-instructions-vote",
      "9.7.14.9. Parallel Synchronization and Communication Instructions: vote (deprecated)",
      "vote (deprecated)",
      "Parallel Synchronization and Communication Instructions",
      Vector("vote"),
      Vector("vote.mode.pred  d, {!}a;\nvote.ballot.b32 d, {!}a;  // 'ballot' form, returns bitmask\n\n.mode = { .all, .any, .uni };"),
      PtxVersion(1, 2),
      SmTarget(12),
      Vector(InstructionForm("vote", Vector(OperandConstraint.Register, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("all", "any", "uni"), _root_.scala.collection.immutable.Set("pred")), _root_.scala.collection.immutable.Set(), false), InstructionForm("vote", Vector(OperandConstraint.Register, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("ballot"), _root_.scala.collection.immutable.Set("b32")), _root_.scala.collection.immutable.Set(), false))
    ),
    InstructionSpec(
      "parallel-synchronization-and-communication-instructions-vote-sync",
      "9.7.14.10. Parallel Synchronization and Communication Instructions: vote.sync",
      "vote.sync",
      "Parallel Synchronization and Communication Instructions",
      Vector("vote.sync"),
      Vector("vote.sync.mode.pred  d, {!}a, membermask;\nvote.sync.ballot.b32 d, {!}a, membermask;  // 'ballot' form, returns bitmask\n\n.mode = { .all, .any, .uni };"),
      PtxVersion(6, 0),
      SmTarget(30),
      Vector(InstructionForm("vote.sync", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("all", "any", "uni"), _root_.scala.collection.immutable.Set("pred")), _root_.scala.collection.immutable.Set(), true), InstructionForm("vote.sync", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("ballot"), _root_.scala.collection.immutable.Set("b32")), _root_.scala.collection.immutable.Set(), true))
    ),
    InstructionSpec(
      "parallel-synchronization-and-communication-instructions-match-sync",
      "9.7.14.11. Parallel Synchronization and Communication Instructions: match.sync",
      "match.sync",
      "Parallel Synchronization and Communication Instructions",
      Vector("match.any.sync", "match.all.sync"),
      Vector("match.any.sync.type  d, a, membermask;\nmatch.all.sync.type  d[|p], a, membermask;\n\n.type = { .b32, .b64 };"),
      PtxVersion(6, 0),
      SmTarget(70),
      Vector(InstructionForm("match.any.sync", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("b32", "b64")), _root_.scala.collection.immutable.Set(), true), InstructionForm("match.all.sync", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("b32", "b64")), _root_.scala.collection.immutable.Set(), true))
    ),
    InstructionSpec(
      "parallel-synchronization-and-communication-instructions-activemask",
      "9.7.14.12. Parallel Synchronization and Communication Instructions: activemask",
      "activemask",
      "Parallel Synchronization and Communication Instructions",
      Vector("activemask"),
      Vector("activemask.b32 d;"),
      PtxVersion(6, 2),
      SmTarget(30),
      Vector(InstructionForm("activemask", Vector(OperandConstraint.Register), Vector(_root_.scala.collection.immutable.Set("b32")), _root_.scala.collection.immutable.Set(), true))
    ),
    InstructionSpec(
      "parallel-synchronization-and-communication-instructions-redux-sync",
      "9.7.14.13. Parallel Synchronization and Communication Instructions: redux.sync",
      "redux.sync",
      "Parallel Synchronization and Communication Instructions",
      Vector("redux.sync"),
      Vector("redux.sync.op.type dst, src, membermask;\n.op   = {.add, .min, .max}\n.type = {.u32, .s32}\n\nredux.sync.op.b32 dst, src, membermask;\n.op   = {.and, .or, .xor}\n\nredux.sync.op{.abs.}{.NaN}.f32 dst, src, membermask;\n.op   = { .min, .max }"),
      PtxVersion(7, 0),
      SmTarget(80),
      Vector(InstructionForm("redux.sync", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("max", "min"), _root_.scala.collection.immutable.Set("s32", "u32")), _root_.scala.collection.immutable.Set(), false), InstructionForm("redux.sync", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("max", "min"), _root_.scala.collection.immutable.Set("b32")), _root_.scala.collection.immutable.Set(), false), InstructionForm("redux.sync", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("max", "min"), _root_.scala.collection.immutable.Set("f32")), _root_.scala.collection.immutable.Set("NaN", "abs"), false))
    ),
    InstructionSpec(
      "parallel-synchronization-and-communication-instructions-griddepcontrol",
      "9.7.14.14. Parallel Synchronization and Communication Instructions: griddepcontrol",
      "griddepcontrol",
      "Parallel Synchronization and Communication Instructions",
      Vector("griddepcontrol"),
      Vector("griddepcontrol.action;\n\n.action   = { .launch_dependents, .wait }"),
      PtxVersion(7, 8),
      SmTarget(90),
      Vector(InstructionForm("griddepcontrol", Vector(), Vector(_root_.scala.collection.immutable.Set("launch_dependents", "wait")), _root_.scala.collection.immutable.Set(), true))
    ),
    InstructionSpec(
      "parallel-synchronization-and-communication-instructions-elect-sync",
      "9.7.14.15. Parallel Synchronization and Communication Instructions: elect.sync",
      "elect.sync",
      "Parallel Synchronization and Communication Instructions",
      Vector("elect.sync"),
      Vector("elect.sync d|p, membermask;"),
      PtxVersion(8, 0),
      SmTarget(90),
      Vector(InstructionForm("elect.sync", Vector(OperandConstraint.Register, OperandConstraint.Value), Vector(), _root_.scala.collection.immutable.Set(), true))
    ),
    InstructionSpec(
      "parallel-synchronization-and-communication-instructions-mbarrier-init",
      "9.7.14.16.12. Parallel Synchronization and Communication Instructions: mbarrier.init",
      "mbarrier.init",
      "Parallel Synchronization and Communication Instructions",
      Vector("mbarrier.init"),
      Vector("mbarrier.init{.layout}{.shared{::cta}}.b64 [addr], count;\n\n.layout = { .layout::v0, .layout::v1 }"),
      PtxVersion(7, 0),
      SmTarget(80),
      Vector(InstructionForm("mbarrier.init", Vector(OperandConstraint.Address, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("b64")), _root_.scala.collection.immutable.Set("layout::v0", "layout::v1", "shared"), false))
    ),
    InstructionSpec(
      "parallel-synchronization-and-communication-instructions-mbarrier-inval",
      "9.7.14.16.13. Parallel Synchronization and Communication Instructions: mbarrier.inval",
      "mbarrier.inval",
      "Parallel Synchronization and Communication Instructions",
      Vector("mbarrier.inval"),
      Vector("mbarrier.inval{.shared{::cta}}.b64 [addr];"),
      PtxVersion(7, 0),
      SmTarget(80),
      Vector(InstructionForm("mbarrier.inval", Vector(OperandConstraint.Address), Vector(_root_.scala.collection.immutable.Set("b64")), _root_.scala.collection.immutable.Set("shared"), false))
    )
)

private def instructionSpecChunk13: Vector[InstructionSpec] = Vector(
    InstructionSpec(
      "parallel-synchronization-and-communication-instructions-mbarrier-expect-tx",
      "9.7.14.16.14. Parallel Synchronization and Communication Instructions: mbarrier.expect_tx",
      "mbarrier.expect_tx",
      "Parallel Synchronization and Communication Instructions",
      Vector("mbarrier.expect_tx"),
      Vector("mbarrier.expect_tx{.sem.scope}{.space}.b64 [addr], txCount;\n\n.sem   = { .relaxed }\n.scope = { .cta, .cluster }\n.space = { .shared{::cta}, .shared::cluster }"),
      PtxVersion(8, 0),
      SmTarget(90),
      Vector(InstructionForm("mbarrier.expect_tx", Vector(OperandConstraint.Address, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("b64")), _root_.scala.collection.immutable.Set("cluster", "cta", "relaxed"), false))
    ),
    InstructionSpec(
      "parallel-synchronization-and-communication-instructions-mbarrier-complete-tx",
      "9.7.14.16.15. Parallel Synchronization and Communication Instructions: mbarrier.complete_tx",
      "mbarrier.complete_tx",
      "Parallel Synchronization and Communication Instructions",
      Vector("mbarrier.complete_tx"),
      Vector("mbarrier.complete_tx{.sem.scope}{.space}.b64 [addr], txCount;\n\n.sem   = { .relaxed }\n.scope = { .cta, .cluster }\n.space = { .shared{::cta}, .shared::cluster }"),
      PtxVersion(8, 0),
      SmTarget(90),
      Vector(InstructionForm("mbarrier.complete_tx", Vector(OperandConstraint.Address, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("b64")), _root_.scala.collection.immutable.Set("cluster", "cta", "relaxed"), false))
    ),
    InstructionSpec(
      "parallel-synchronization-and-communication-instructions-mbarrier-arrive",
      "9.7.14.16.16. Parallel Synchronization and Communication Instructions: mbarrier.arrive",
      "mbarrier.arrive",
      "Parallel Synchronization and Communication Instructions",
      Vector("mbarrier.arrive"),
      Vector("mbarrier.arrive{.sem.scope}{.shared{::cta}}.b64           state, [addr]{, count};\nmbarrier.arrive{.sem.scope}{.shared::cluster}.b64         _, [addr] {,count}\nmbarrier.arrive.expect_tx{.sem.scope}{.shared{::cta}}.b64 state, [addr], txCount;\nmbarrier.arrive.expect_tx{.sem.scope}{.shared::cluster}.b64   _, [addr], txCount;\nmbarrier.arrive.noComplete{.release.cta}{.shared{::cta}}.b64  state, [addr], count;\n\n.sem   = { .release, .relaxed }\n.scope = { .cta, .cluster }"),
      PtxVersion(7, 0),
      SmTarget(80),
      Vector(InstructionForm("mbarrier.arrive", Vector(OperandConstraint.Value, OperandConstraint.Address, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("b64")), _root_.scala.collection.immutable.Set("cluster", "cta", "relaxed", "release", "shared"), false), InstructionForm("mbarrier.arrive", Vector(OperandConstraint.Value, OperandConstraint.Address, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("b64")), _root_.scala.collection.immutable.Set("cluster", "cta", "relaxed", "release", "shared::cluster"), false), InstructionForm("mbarrier.arrive", Vector(OperandConstraint.Value, OperandConstraint.Address, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("expect_tx"), _root_.scala.collection.immutable.Set("b64")), _root_.scala.collection.immutable.Set("cluster", "cta", "relaxed", "release", "shared"), false), InstructionForm("mbarrier.arrive", Vector(OperandConstraint.Value, OperandConstraint.Address, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("expect_tx"), _root_.scala.collection.immutable.Set("b64")), _root_.scala.collection.immutable.Set("cluster", "cta", "relaxed", "release", "shared::cluster"), false), InstructionForm("mbarrier.arrive", Vector(OperandConstraint.Value, OperandConstraint.Address, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("noComplete"), _root_.scala.collection.immutable.Set("b64")), _root_.scala.collection.immutable.Set("cta", "release", "shared"), false))
    ),
    InstructionSpec(
      "parallel-synchronization-and-communication-instructions-mbarrier-arrive-drop",
      "9.7.14.16.17. Parallel Synchronization and Communication Instructions: mbarrier.arrive_drop",
      "mbarrier.arrive_drop",
      "Parallel Synchronization and Communication Instructions",
      Vector("mbarrier.arrive_drop"),
      Vector("mbarrier.arrive_drop{.sem.scope}{.shared{::cta}}.b64              state, [addr] {, count};\nmbarrier.arrive_drop{.sem.scope}{.shared::cluster}.b64            _,     [addr] {, count};\nmbarrier.arrive_drop.expect_tx{.sem.scope}{.shared{::cta}}.b64    state, [addr], tx_count;\nmbarrier.arrive_drop.expect_tx{.sem.scope}{.shared::cluster}.b64  _,     [addr], tx_count;\nmbarrier.arrive_drop.noComplete{.release.cta}{.shared{::cta}}.b64 state, [addr], count;\n\n.sem   = { .release, .relaxed }\n.scope = { .cta, .cluster }"),
      PtxVersion(7, 0),
      SmTarget(80),
      Vector(InstructionForm("mbarrier.arrive_drop", Vector(OperandConstraint.Value, OperandConstraint.Address, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("b64")), _root_.scala.collection.immutable.Set("cluster", "cta", "relaxed", "release", "shared"), false), InstructionForm("mbarrier.arrive_drop", Vector(OperandConstraint.Value, OperandConstraint.Address, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("b64")), _root_.scala.collection.immutable.Set("cluster", "cta", "relaxed", "release", "shared::cluster"), false), InstructionForm("mbarrier.arrive_drop", Vector(OperandConstraint.Value, OperandConstraint.Address, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("expect_tx"), _root_.scala.collection.immutable.Set("b64")), _root_.scala.collection.immutable.Set("cluster", "cta", "relaxed", "release", "shared"), false), InstructionForm("mbarrier.arrive_drop", Vector(OperandConstraint.Value, OperandConstraint.Address, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("expect_tx"), _root_.scala.collection.immutable.Set("b64")), _root_.scala.collection.immutable.Set("cluster", "cta", "relaxed", "release", "shared::cluster"), false), InstructionForm("mbarrier.arrive_drop", Vector(OperandConstraint.Value, OperandConstraint.Address, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("noComplete"), _root_.scala.collection.immutable.Set("b64")), _root_.scala.collection.immutable.Set("cta", "release", "shared"), false))
    ),
    InstructionSpec(
      "parallel-synchronization-and-communication-instructions-cp-async-mbarrier-arrive",
      "9.7.14.16.18. Parallel Synchronization and Communication Instructions: cp.async.mbarrier.arrive",
      "cp.async.mbarrier.arrive",
      "Parallel Synchronization and Communication Instructions",
      Vector("cp.async.mbarrier.arrive"),
      Vector("cp.async.mbarrier.arrive{.noinc}{.shared{::cta}}.b64 [addr];"),
      PtxVersion(7, 0),
      SmTarget(80),
      Vector(InstructionForm("cp.async.mbarrier.arrive", Vector(OperandConstraint.Address), Vector(_root_.scala.collection.immutable.Set("b64")), _root_.scala.collection.immutable.Set("noinc", "shared"), false))
    ),
    InstructionSpec(
      "parallel-synchronization-and-communication-instructions-mbarrier-test-wait-try-wait",
      "9.7.14.16.19. Parallel Synchronization and Communication Instructions: mbarrier.test_wait / mbarrier.try_wait",
      "mbarrier.test_wait / mbarrier.try_wait",
      "Parallel Synchronization and Communication Instructions",
      Vector("mbarrier.test_wait", "mbarrier.try_wait"),
      Vector("// without parity\nmbarrier.test_wait{.phase_type::primary}{.sem.scope}{.ss}.b64      waitComplete, [addr], state;\n\nmbarrier.test_wait.phase_type::primary{.sem}{.scope}{.ss}.b64      waitComplete|reportPredicate\n                                                                   {, reportValue}, [addr], state;\n\n// with parity\nmbarrier.test_wait.parity{.phase_type}{.sem.scope}{.ss}.b64        waitComplete, [addr], phaseParity;\n\nmbarrier.test_wait.parity.phase_type::primary{.sem}{.scope}{.ss}.b64  waitComplete|reportPredicate\n                                                                      {, reportValue}, [addr], phaseParity;\n\n// without parity\nmbarrier.try_wait{.phase_type::primary}{.sem.scope}{.ss}.b64      waitComplete, [addr], state {, timeHint};\n\nmbarrier.try_wait.phase_type::primary{.sem}{.scope}{.ss}.b64      waitComplete|reportPredicate {, reportValue},\n                                                                  [addr], state {, timeHint};\n\n// with parity\nmbarrier.try_wait.parity{.phase_type}{.sem.scope}{.ss}.b64            waitComplete, [addr], phaseParity {, timeHint};\n\nmbarrier.try_wait.parity.phase_type::primary{.sem}{.scope}{.ss}.b64   waitComplete|reportPredicate {, reportValue},\n                                                                      [addr], phaseParity {, timeHint};\n\n\n.ss   = { .shared{::cta} }\n.sem   = { .acquire, .relaxed }\n.scope = { .cta, .cluster }\n.phase_type = { .phase_type::primary, .phase_type::conditional }"),
      PtxVersion(1, 0),
      SmTarget(80),
      Vector(InstructionForm("mbarrier.test_wait", Vector(OperandConstraint.Value, OperandConstraint.Address, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("b64")), _root_.scala.collection.immutable.Set("acquire", "cluster", "cta", "phase_type::primary", "relaxed"), false), InstructionForm("mbarrier.test_wait", Vector(OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Address, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("phase_type::primary"), _root_.scala.collection.immutable.Set("b64")), _root_.scala.collection.immutable.Set("acquire", "cluster", "cta", "relaxed"), false), InstructionForm("mbarrier.test_wait", Vector(OperandConstraint.Value, OperandConstraint.Address, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("parity"), _root_.scala.collection.immutable.Set("b64")), _root_.scala.collection.immutable.Set("acquire", "cluster", "cta", "phase_type::conditional", "phase_type::primary", "relaxed"), false), InstructionForm("mbarrier.test_wait", Vector(OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Address, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("parity"), _root_.scala.collection.immutable.Set("phase_type::primary"), _root_.scala.collection.immutable.Set("b64")), _root_.scala.collection.immutable.Set("acquire", "cluster", "cta", "relaxed"), false), InstructionForm("mbarrier.try_wait", Vector(OperandConstraint.Value, OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("b64")), _root_.scala.collection.immutable.Set("acquire", "cluster", "cta", "phase_type::primary", "relaxed"), false), InstructionForm("mbarrier.try_wait", Vector(OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("phase_type::primary"), _root_.scala.collection.immutable.Set("b64")), _root_.scala.collection.immutable.Set("acquire", "cluster", "cta", "relaxed"), false), InstructionForm("mbarrier.try_wait", Vector(OperandConstraint.Value, OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("parity"), _root_.scala.collection.immutable.Set("b64")), _root_.scala.collection.immutable.Set("acquire", "cluster", "cta", "phase_type::conditional", "phase_type::primary", "relaxed"), false), InstructionForm("mbarrier.try_wait", Vector(OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("parity"), _root_.scala.collection.immutable.Set("phase_type::primary"), _root_.scala.collection.immutable.Set("b64")), _root_.scala.collection.immutable.Set("acquire", "cluster", "cta", "relaxed"), false))
    ),
    InstructionSpec(
      "parallel-synchronization-and-communication-instructions-mbarrier-pending-count",
      "9.7.14.16.20. Parallel Synchronization and Communication Instructions: mbarrier.pending_count",
      "mbarrier.pending_count",
      "Parallel Synchronization and Communication Instructions",
      Vector("mbarrier.pending_count"),
      Vector("mbarrier.pending_count{.layout}.b64 count, state;\n\n.layout = { .layout::v0 }"),
      PtxVersion(7, 0),
      SmTarget(80),
      Vector(InstructionForm("mbarrier.pending_count", Vector(OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("b64")), _root_.scala.collection.immutable.Set("layout::v0"), false))
    ),
    InstructionSpec(
      "parallel-synchronization-and-communication-instructions-mbarrier-check-layout",
      "9.7.14.16.21. Parallel Synchronization and Communication Instructions: mbarrier.check_layout",
      "mbarrier.check_layout",
      "Parallel Synchronization and Communication Instructions",
      Vector("mbarrier.check_layout"),
      Vector("mbarrier.check_layout.layout{.ss}.b64 p, [addr];\n\n.layout = { .layout::v0, .layout::v1 }\n.ss = { .shared::cta }"),
      PtxVersion(9, 3),
      SmTarget(90),
      Vector(InstructionForm("mbarrier.check_layout", Vector(OperandConstraint.Register, OperandConstraint.Address), Vector(_root_.scala.collection.immutable.Set("layout::v0", "layout::v1"), _root_.scala.collection.immutable.Set("b64")), _root_.scala.collection.immutable.Set("shared::cta"), true))
    ),
    InstructionSpec(
      "parallel-synchronization-and-communication-instructions-tensormap-cp-fenceproxy",
      "9.7.14.17. Parallel Synchronization and Communication Instructions: tensormap.cp_fenceproxy",
      "tensormap.cp_fenceproxy",
      "Parallel Synchronization and Communication Instructions",
      Vector("tensormap.cp_fenceproxy"),
      Vector("tensormap.cp_fenceproxy.cp_qualifiers.fence_qualifiers.sync.aligned  [dst], [src], size;\n\n.cp_qualifiers    = { .global.shared::cta }\n.fence_qualifiers = { .to_proxy::from_proxy.release.scope }\n.to_proxy::from_proxy  = { .tensormap::generic }\n.scope            = { .cta, .cluster, .gpu , .sys }"),
      PtxVersion(8, 3),
      SmTarget(90),
      Vector(InstructionForm("tensormap.cp_fenceproxy", Vector(OperandConstraint.Address, OperandConstraint.Address, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("sync"), _root_.scala.collection.immutable.Set("aligned")), _root_.scala.collection.immutable.Set(), true))
    ),
    InstructionSpec(
      "parallel-synchronization-and-communication-instructions-clusterlaunchcontrol-try-cancel",
      "9.7.14.18. Parallel Synchronization and Communication Instructions: clusterlaunchcontrol.try_cancel",
      "clusterlaunchcontrol.try_cancel",
      "Parallel Synchronization and Communication Instructions",
      Vector("clusterlaunchcontrol.try_cancel"),
      Vector("clusterlaunchcontrol.try_cancel.async{.space}.completion_mechanism{.multicast::cluster::all}.b128 [addr], [mbar];\n\n.completion_mechanism = { .mbarrier::complete_tx::bytes };\n.space = { .shared::cta };"),
      PtxVersion(8, 6),
      SmTarget(100),
      Vector(InstructionForm("clusterlaunchcontrol.try_cancel", Vector(OperandConstraint.Address, OperandConstraint.Address), Vector(_root_.scala.collection.immutable.Set("async"), _root_.scala.collection.immutable.Set("mbarrier::complete_tx::bytes"), _root_.scala.collection.immutable.Set("b128")), _root_.scala.collection.immutable.Set("multicast::cluster::all", "shared::cta"), false))
    ),
    InstructionSpec(
      "parallel-synchronization-and-communication-instructions-clusterlaunchcontrol-query-cancel",
      "9.7.14.19. Parallel Synchronization and Communication Instructions: clusterlaunchcontrol.query_cancel",
      "clusterlaunchcontrol.query_cancel",
      "Parallel Synchronization and Communication Instructions",
      Vector("clusterlaunchcontrol.query_cancel"),
      Vector("clusterlaunchcontrol.query_cancel.is_canceled.pred.b128 pred, try_cancel_response;\n\nclusterlaunchcontrol.query_cancel.get_first_ctaid.v4.b32.b128 {xdim, ydim, zdim, _},  try_cancel_response;\n\nclusterlaunchcontrol.query_cancel.get_first_ctaid{::dimension}.b32.b128 reg, try_cancel_response;\n\n::dimension = { ::x, ::y, ::z };"),
      PtxVersion(8, 6),
      SmTarget(100),
      Vector(InstructionForm("clusterlaunchcontrol.query_cancel", Vector(OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("is_canceled"), _root_.scala.collection.immutable.Set("pred"), _root_.scala.collection.immutable.Set("b128")), _root_.scala.collection.immutable.Set(), true), InstructionForm("clusterlaunchcontrol.query_cancel", Vector(OperandConstraint.Vector, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("get_first_ctaid"), _root_.scala.collection.immutable.Set("v4"), _root_.scala.collection.immutable.Set("b32"), _root_.scala.collection.immutable.Set("b128")), _root_.scala.collection.immutable.Set(), true), InstructionForm("clusterlaunchcontrol.query_cancel", Vector(OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("get_first_ctaid"), _root_.scala.collection.immutable.Set("b32"), _root_.scala.collection.immutable.Set("b128")), _root_.scala.collection.immutable.Set(), false))
    ),
    InstructionSpec(
      "tcgen05-instructions-tcgen05-alloc-dealloc-relinquish-alloc-permit",
      "9.7.17.7.1. Tensorcore 5th Generation Instructions: tcgen05.alloc, tcgen05.dealloc, tcgen05.relinquish_alloc_permit",
      "tcgen05.alloc, tcgen05.dealloc, tcgen05.relinquish_alloc_permit",
      "Tensorcore 5th Generation Instructions",
      Vector("tcgen05.alloc", "tcgen05.dealloc", "tcgen05.relinquish_alloc_permit"),
      Vector("tcgen05.alloc.cta_group.sync.aligned{.shared::cta}.b32  [dst], nCols;\n\ntcgen05.dealloc.cta_group.sync.aligned.b32              taddr, nCols;\n\ntcgen05.relinquish_alloc_permit.cta_group.sync.aligned;\n\n.cta_group = { .cta_group::1, .cta_group::2 }"),
      PtxVersion(8, 6),
      SmTarget(100),
      Vector(InstructionForm("tcgen05.alloc", Vector(OperandConstraint.Address, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("sync"), _root_.scala.collection.immutable.Set("aligned"), _root_.scala.collection.immutable.Set("b32")), _root_.scala.collection.immutable.Set("shared::cta"), false), InstructionForm("tcgen05.dealloc", Vector(OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("sync"), _root_.scala.collection.immutable.Set("aligned"), _root_.scala.collection.immutable.Set("b32")), _root_.scala.collection.immutable.Set(), false), InstructionForm("tcgen05.relinquish_alloc_permit", Vector(), Vector(_root_.scala.collection.immutable.Set("sync"), _root_.scala.collection.immutable.Set("aligned")), _root_.scala.collection.immutable.Set(), false))
    )
)

private def instructionSpecChunk14: Vector[InstructionSpec] = Vector(
    InstructionSpec(
      "tcgen05-instructions-tcgen05-ld",
      "9.7.17.8.3. Tensorcore 5th Generation Instructions: tcgen05.ld",
      "tcgen05.ld",
      "Tensorcore 5th Generation Instructions",
      Vector("tcgen05.ld"),
      Vector("// Base load instruction:\n\ntcgen05.ld.sync.aligned.shape1.num{.pack}.b32    r, [taddr];\n\ntcgen05.ld.sync.aligned.shape2.num{.pack}.b32    r, [taddr], immHalfSplitoff;\n\n.shape1 = { .16x64b, .16x128b, .16x256b, .32x32b }\n.shape2 = { .16x32bx2 }\n.num    = { .x1, .x2, .x4, .x8, .x16, .x32, .x64, .x128 }\n.pack   = { .pack::16b }\n\n// Floating point type load along with reduction :\n\ntcgen05.ld.red.sync.aligned.shape3.num.redOp{.abs}{.NaN}.f32 r, redval, [taddr];\n\ntcgen05.ld.red.sync.aligned.shape4.num.redOp{.abs}{.NaN}.f32 r, redval, [taddr], immHalfSplitoff;\n\n// Integer type load along with reduction :\n\ntcgen05.ld.red.sync.aligned.shape3.num.redOp.type r, redval, [taddr];\n\ntcgen05.ld.red.sync.aligned.shape4.num.redOp.type r, redval, [taddr], immHalfSplitoff;\n\n.shape3 = { .32x32b   }\n.shape4 = { .16x32bx2 }\n.redOp  = { .min, .max }\n.type   = { .u32, .s32 }"),
      PtxVersion(8, 6),
      SmTarget(100),
      Vector(InstructionForm("tcgen05.ld", Vector(OperandConstraint.Register, OperandConstraint.Address), Vector(_root_.scala.collection.immutable.Set("sync"), _root_.scala.collection.immutable.Set("aligned"), _root_.scala.collection.immutable.Set("x1", "x128", "x16", "x2", "x32", "x4", "x64", "x8"), _root_.scala.collection.immutable.Set("b32")), _root_.scala.collection.immutable.Set(), false), InstructionForm("tcgen05.ld", Vector(OperandConstraint.Register, OperandConstraint.Address, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("sync"), _root_.scala.collection.immutable.Set("aligned"), _root_.scala.collection.immutable.Set("x1", "x128", "x16", "x2", "x32", "x4", "x64", "x8"), _root_.scala.collection.immutable.Set("b32")), _root_.scala.collection.immutable.Set(), false), InstructionForm("tcgen05.ld", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Address), Vector(_root_.scala.collection.immutable.Set("red"), _root_.scala.collection.immutable.Set("sync"), _root_.scala.collection.immutable.Set("aligned"), _root_.scala.collection.immutable.Set("x1", "x128", "x16", "x2", "x32", "x4", "x64", "x8"), _root_.scala.collection.immutable.Set("max", "min"), _root_.scala.collection.immutable.Set("f32")), _root_.scala.collection.immutable.Set("NaN", "abs"), false), InstructionForm("tcgen05.ld", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Address, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("red"), _root_.scala.collection.immutable.Set("sync"), _root_.scala.collection.immutable.Set("aligned"), _root_.scala.collection.immutable.Set("x1", "x128", "x16", "x2", "x32", "x4", "x64", "x8"), _root_.scala.collection.immutable.Set("max", "min"), _root_.scala.collection.immutable.Set("f32")), _root_.scala.collection.immutable.Set("NaN", "abs"), false), InstructionForm("tcgen05.ld", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Address), Vector(_root_.scala.collection.immutable.Set("red"), _root_.scala.collection.immutable.Set("sync"), _root_.scala.collection.immutable.Set("aligned"), _root_.scala.collection.immutable.Set("x1", "x128", "x16", "x2", "x32", "x4", "x64", "x8"), _root_.scala.collection.immutable.Set("max", "min"), _root_.scala.collection.immutable.Set("s32", "u32")), _root_.scala.collection.immutable.Set(), false), InstructionForm("tcgen05.ld", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Address, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("red"), _root_.scala.collection.immutable.Set("sync"), _root_.scala.collection.immutable.Set("aligned"), _root_.scala.collection.immutable.Set("x1", "x128", "x16", "x2", "x32", "x4", "x64", "x8"), _root_.scala.collection.immutable.Set("max", "min"), _root_.scala.collection.immutable.Set("s32", "u32")), _root_.scala.collection.immutable.Set(), false))
    ),
    InstructionSpec(
      "tcgen05-instructions-tcgen05-st",
      "9.7.17.8.4. Tensorcore 5th Generation Instructions: tcgen05.st",
      "tcgen05.st",
      "Tensorcore 5th Generation Instructions",
      Vector("tcgen05.st"),
      Vector("tcgen05.st.sync.aligned.shape1.num{.unpack}.b32    [taddr], r;\n\ntcgen05.st.sync.aligned.shape2.num{.unpack}.b32    [taddr], immHalfSplitoff, r;\n\n.shape1 = { .16x64b, .16x128b, .16x256b, .32x32b }\n.shape2 = { .16x32bx2 }\n.num    = { .x1, .x2, .x4, .x8, .x16, .x32, .x64, .x128 }\n.unpack = { .unpack::16b }"),
      PtxVersion(8, 6),
      SmTarget(100),
      Vector(InstructionForm("tcgen05.st", Vector(OperandConstraint.Address, OperandConstraint.Register), Vector(_root_.scala.collection.immutable.Set("sync"), _root_.scala.collection.immutable.Set("aligned"), _root_.scala.collection.immutable.Set("x1", "x128", "x16", "x2", "x32", "x4", "x64", "x8"), _root_.scala.collection.immutable.Set("b32")), _root_.scala.collection.immutable.Set(), false), InstructionForm("tcgen05.st", Vector(OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Register), Vector(_root_.scala.collection.immutable.Set("sync"), _root_.scala.collection.immutable.Set("aligned"), _root_.scala.collection.immutable.Set("x1", "x128", "x16", "x2", "x32", "x4", "x64", "x8"), _root_.scala.collection.immutable.Set("b32")), _root_.scala.collection.immutable.Set(), false))
    ),
    InstructionSpec(
      "tcgen05-instructions-tcgen05-wait",
      "9.7.17.8.5. Tensorcore 5th Generation Instructions: tcgen05.wait",
      "tcgen05.wait",
      "Tensorcore 5th Generation Instructions",
      Vector("tcgen05.wait::ld", "tcgen05.wait::st"),
      Vector("tcgen05.wait_operation.sync.aligned;\n\n.wait_operation = { .wait::ld, .wait::st }"),
      PtxVersion(8, 6),
      SmTarget(100),
      Vector(InstructionForm("tcgen05.wait::ld", Vector(), Vector(_root_.scala.collection.immutable.Set("sync"), _root_.scala.collection.immutable.Set("aligned")), _root_.scala.collection.immutable.Set(), false), InstructionForm("tcgen05.wait::st", Vector(), Vector(_root_.scala.collection.immutable.Set("sync"), _root_.scala.collection.immutable.Set("aligned")), _root_.scala.collection.immutable.Set(), false))
    ),
    InstructionSpec(
      "tcgen05-instructions-tcgen05-cp",
      "9.7.17.9.2. Tensorcore 5th Generation Instructions: tcgen05.cp",
      "tcgen05.cp",
      "Tensorcore 5th Generation Instructions",
      Vector("tcgen05.cp"),
      Vector("tcgen05.cp.cta_group.shape{.multicast}{.dst_fmt.src_fmt} [taddr], s-desc;\n\n.cta_group = { .cta_group::1, .cta_group::2 }\n.src_fmt   = { .b6x16_p32 , .b4x16_p64 }\n.dst_fmt   = { .b8x16 }\n.shape     = { .128x256b, .4x256b, .128x128b, .64x128b**, .32x128b*** }\n.multicast = { .warpx2::02_13** , .warpx2::01_23**, .warpx4*** }"),
      PtxVersion(8, 6),
      SmTarget(100),
      Vector(InstructionForm("tcgen05.cp", Vector(OperandConstraint.Address, OperandConstraint.Value), Vector(), _root_.scala.collection.immutable.Set("b4x16_p64", "b6x16_p32", "b8x16"), false))
    ),
    InstructionSpec(
      "tcgen05-instructions-tcgen05-shift",
      "9.7.17.9.3. Tensorcore 5th Generation Instructions: tcgen05.shift",
      "tcgen05.shift",
      "Tensorcore 5th Generation Instructions",
      Vector("tcgen05.shift"),
      Vector("tcgen05.shift.cta_group.down  [taddr];\n\n.cta_group = { .cta_group::1, .cta_group::2 }"),
      PtxVersion(8, 6),
      SmTarget(10),
      Vector(InstructionForm("tcgen05.shift", Vector(OperandConstraint.Address), Vector(_root_.scala.collection.immutable.Set("down")), _root_.scala.collection.immutable.Set(), true))
    ),
    InstructionSpec(
      "tcgen05-mma-instructions-mma",
      "9.7.17.10.9.1. TensorCore 5th Generation Instructions: tcgen05.mma",
      "tcgen05.mma",
      "TensorCore 5th Generation Instructions",
      Vector("tcgen05.mma"),
      Vector("// 1. Floating-point type without block scaling:\n\ntcgen05.mma.cta_group.kind   [d-tmem],  a-desc,  b-desc, idesc,\n                             { disable-output-lane }, enable-input-d {, scale-input-d};\n\ntcgen05.mma.cta_group.kind   [d-tmem], [a-tmem], b-desc, idesc,\n                             { disable-output-lane }, enable-input-d {, scale-input-d};\n\n.kind      = { .kind::f16, .kind::tf32, .kind::f8f6f4 }\n.cta_group = { .cta_group::1, .cta_group::2 }\n\n----------------------------------------------------------------------------------\n\n// 2. Floating-point type with block scaling:\n\ntcgen05.mma.cta_group.kind.block_scale{.scale_vectorsize}\n                                        [d-tmem],  a-desc,  b-desc, idesc,\n                                        [scale-A-tmem], [scale-B-tmem], enable-input-d;\n\ntcgen05.mma.cta_group.kind.block_scale{.scale_vectorsize}\n                                        [d-tmem], [a-tmem], b-desc, idesc,\n                                        [scale-A-tmem], [scale-B-tmem], enable-input-d;\n\n.kind = { .kind::mxf8f6f4, .kind::mxf4, .kind::mxf4nvf4 }\n.cta_group      = { .cta_group::1,   .cta_group::2 }\n.scale_vectorsize = { .scale_vec::1X, .scale_vec::2X, .scale_vec::4X, .block16, .block32 }\n\n----------------------------------------------------------------------------------\n\n// 3. Convolution MMA for floating-point type without block scaling:\n\ntcgen05.mma.cta_group.kind.collector_usage [d-tmem],  a-desc,  b-desc, idesc,\n                                           { disable-output-lane }, enable-input-d {, scale-input-d};\n\ntcgen05.mma.cta_group.kind{.ashift}.collector_usage [d-tmem], [a-tmem], b-desc, idesc,\n                                                    { disable-output-lane }, enable-input-d {, scale-input-d};\n\ntcgen05.mma.cta_group.kind.ashift{.collector_usage} [d-tmem], [a-tmem], b-desc, idesc,\n                                                    { disable-output-lane }, enable-input-d {, scale-input-d};\n\n.kind      = { .kind::f16, .kind::tf32, .kind::f8f6f4 }\n.cta_group = { .cta_group::1,   .cta_group::2 }\n.collector_usage = { .collector::buffer::op }\n::buffer         = { ::a }\n::op             = { ::fill, ::use, ::lastuse, ::discard* }\n\n----------------------------------------------------------------------------------\n\n// 4. Activation Stationary MMA for floating-point type with block scaling:\n\ntcgen05.mma.cta_group.kind.block_scale{.scale_vectorsize}.collector_usage\n                                            [d-tmem],  a-desc,  b-desc, idesc,\n                                            [scale-A-tmem], [scale-B-tmem], enable-input-d;\n\ntcgen05.mma.cta_group.kind.block_scale{.scale_vectorsize}.collector_usage\n                                            [d-tmem], [a-tmem], b-desc, idesc,\n                                            [scale-A-tmem], [scale-B-tmem], enable-input-d;\n\n.cta_group       = { .cta_group::1,   .cta_group::2 }\n.scale_vectorsize  = { .scale_vec::1X, .scale_vec::2X, .scale_vec::4X, .block16, .block32 }\n.kind            = { .kind::mxf8f6f4, .kind::mxf4, .kind::mxf4nvf4 }\n.collector_usage = { .collector::buffer::op }\n::buffer         = { ::a }\n::op             = { ::fill, ::use, ::lastuse, ::discard* }\n\n----------------------------------------------------------------------------------\n\n// 5. Integer type:\n\ntcgen05.mma.cta_group.kind::i8  [d-tmem],  a-desc,  b-desc, idesc,\n                                { disable-output-lane }, enable-input-d;\n\ntcgen05.mma.cta_group.kind::i8  [d-tmem], [a-tmem], b-desc, idesc,\n                                { disable-output-lane }, enable-input-d;\n\n.cta_group = { .cta_group::1,   .cta_group::2  }\n\n----------------------------------------------------------------------------------\n\n// 6. Convolution MMA for integer type:\n\ntcgen05.mma.cta_group.kind::i8.collector_usage          [d-tmem],  a-desc,  b-desc, idesc,\n                                                        { disable-output-lane }, enable-input-d;\n\ntcgen05.mma.cta_group.kind::i8.ashift{.collector_usage} [d-tmem], [a-tmem], b-desc, idesc,\n                                                        { disable-output-lane }, enable-input-d;\n\ntcgen05.mma.cta_group.kind::i8{.ashift}.collector_usage [d-tmem], [a-tmem], b-desc, idesc,\n                                                        { disable-output-lane }, enable-input-d;\n\n.cta_group       = { .cta_group::1,   .cta_group::2  }\n.collector_usage = { .collector::buffer::op }\n::buffer         = { ::a }\n::op             = { ::fill, ::use, ::lastuse, ::discard* }"),
      PtxVersion(8, 6),
      SmTarget(100),
      Vector(InstructionForm("tcgen05.mma", Vector(OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Vector, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("kind::mxf4", "kind::mxf4nvf4", "kind::mxf8f6f4")), _root_.scala.collection.immutable.Set(), false), InstructionForm("tcgen05.mma", Vector(OperandConstraint.Address, OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Vector, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("kind::mxf4", "kind::mxf4nvf4", "kind::mxf8f6f4")), _root_.scala.collection.immutable.Set(), false), InstructionForm("tcgen05.mma", Vector(OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Address, OperandConstraint.Address, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("kind::mxf4", "kind::mxf4nvf4", "kind::mxf8f6f4"), _root_.scala.collection.immutable.Set("block_scale")), _root_.scala.collection.immutable.Set("block16", "block32"), false), InstructionForm("tcgen05.mma", Vector(OperandConstraint.Address, OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Address, OperandConstraint.Address, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("kind::mxf4", "kind::mxf4nvf4", "kind::mxf8f6f4"), _root_.scala.collection.immutable.Set("block_scale")), _root_.scala.collection.immutable.Set("block16", "block32"), false), InstructionForm("tcgen05.mma", Vector(OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Vector, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("kind::mxf4", "kind::mxf4nvf4", "kind::mxf8f6f4"), _root_.scala.collection.immutable.Set("collector::buffer::op")), _root_.scala.collection.immutable.Set(), false), InstructionForm("tcgen05.mma", Vector(OperandConstraint.Address, OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Vector, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("kind::mxf4", "kind::mxf4nvf4", "kind::mxf8f6f4"), _root_.scala.collection.immutable.Set("collector::buffer::op")), _root_.scala.collection.immutable.Set("ashift"), false), InstructionForm("tcgen05.mma", Vector(OperandConstraint.Address, OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Vector, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("kind::mxf4", "kind::mxf4nvf4", "kind::mxf8f6f4"), _root_.scala.collection.immutable.Set("ashift")), _root_.scala.collection.immutable.Set("collector::buffer::op"), false), InstructionForm("tcgen05.mma", Vector(OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Address, OperandConstraint.Address, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("kind::mxf4", "kind::mxf4nvf4", "kind::mxf8f6f4"), _root_.scala.collection.immutable.Set("block_scale"), _root_.scala.collection.immutable.Set("collector::buffer::op")), _root_.scala.collection.immutable.Set("block16", "block32"), false), InstructionForm("tcgen05.mma", Vector(OperandConstraint.Address, OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Address, OperandConstraint.Address, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("kind::mxf4", "kind::mxf4nvf4", "kind::mxf8f6f4"), _root_.scala.collection.immutable.Set("block_scale"), _root_.scala.collection.immutable.Set("collector::buffer::op")), _root_.scala.collection.immutable.Set("block16", "block32"), false), InstructionForm("tcgen05.mma", Vector(OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Vector, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("kind::i8")), _root_.scala.collection.immutable.Set(), false), InstructionForm("tcgen05.mma", Vector(OperandConstraint.Address, OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Vector, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("kind::i8")), _root_.scala.collection.immutable.Set(), false), InstructionForm("tcgen05.mma", Vector(OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Vector, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("kind::i8"), _root_.scala.collection.immutable.Set("collector::buffer::op")), _root_.scala.collection.immutable.Set(), false), InstructionForm("tcgen05.mma", Vector(OperandConstraint.Address, OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Vector, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("kind::i8"), _root_.scala.collection.immutable.Set("ashift")), _root_.scala.collection.immutable.Set("collector::buffer::op"), false), InstructionForm("tcgen05.mma", Vector(OperandConstraint.Address, OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Vector, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("kind::i8"), _root_.scala.collection.immutable.Set("collector::buffer::op")), _root_.scala.collection.immutable.Set("ashift"), false))
    ),
    InstructionSpec(
      "tcgen05-mma-instructions-mma-sp",
      "9.7.17.10.9.2. TensorCore 5th Generation Instructions: tcgen05.mma.sp",
      "tcgen05.mma.sp",
      "TensorCore 5th Generation Instructions",
      Vector("tcgen05.mma.sp"),
      Vector("// 1. Floating-point type without block scaling:\n\ntcgen05.mma.sp.cta_group.kind  [d-tmem],  a-desc,  b-desc, [sp-meta-tmem] ,  idesc,\n                               { disable-output-lane }, enable-input-d{, scale-input-d};\n\ntcgen05.mma.sp.cta_group.kind  [d-tmem], [a-tmem], b-desc, [sp-meta-tmem] , idesc,\n                               { disable-output-lane }, enable-input-d{, scale-input-d};\n\n.kind       = { .kind::f16, , .kind::tf32, .kind::f8f6f4 }\n.cta_group  = { .cta_group::1,  .cta_group::2 }\n\n----------------------------------------------------------------------------------\n\n// 2. Floating-point type with block scaling:\n\ntcgen05.mma.sp.cta_group.kind.block_scale{.scale_vectorsize}\n                                         [d-tmem],  a-desc,  b-desc , [sp-meta-tmem] , idesc,\n                                         [scale-A-tmem], [scale-B-tmem], enable-input-d;\n\ntcgen05.mma.sp.cta_group.kind.block_scale{.scale_vectorsize}\n                                         [d-tmem], [a-tmem], b-desc , [sp-meta-tmem] , idesc,\n                                         [scale-A-tmem], [scale-B-tmem], enable-input-d;\n\n.scale_vectorsize = { .scale_vec::1X, .scale_vec::2X, .scale_vec::4X, .block16, .block32 }\n.cta_group      = { .cta_group::1,  .cta_group::2 }\n.kind = { .kind::mxf8f6f4, .kind::mxf4, .kind::mxf4nvf4 }\n\n----------------------------------------------------------------------------------\n\n// 3. Convolution MMA with floating-point type without block scaling:\n\ntcgen05.mma.sp.cta_group.kind.collector_usage           [d-tmem],  a-desc,  b-desc,\n                                                        [sp-meta-tmem] ,  idesc,\n                                                        { disable-output-lane }, enable-input-d\n                                                        {, scale-input-d};\n\ntcgen05.mma.sp.cta_group.kind.ashift{.collector_usage}  [d-tmem], [a-tmem], b-desc,\n                                                        [sp-meta-tmem] , idesc,\n                                                        { disable-output-lane }, enable-input-d\n                                                        {, scale-input-d};\n\ntcgen05.mma.sp.cta_group.kind{.ashift}.collector_usage  [d-tmem], [a-tmem], b-desc,\n                                                        [sp-meta-tmem] , idesc,\n                                                        { disable-output-lane }, enable-input-d\n                                                        {, scale-input-d};\n\n.kind            = { .kind::f16, .kind::tf32, .kind::f8f6f4 }\n.collector_usage = { .collector::buffer::op }\n::buffer         = { ::a }\n::op             = { ::fill, ::use, ::lastuse, ::discard* }\n\n----------------------------------------------------------------------------------\n\n// 4. Activation Stationary MMA with floating-point type with block scaling:\n\ntcgen05.mma.sp.cta_group.kind.block_scale{.scale_vectorsize}.collector_usage\n                                         [d-tmem],  a-desc,  b-desc , [sp-meta-tmem] , idesc,\n                                         [scale-A-tmem], [scale-B-tmem], enable-input-d;\n\ntcgen05.mma.sp.cta_group.kind.block_scale{.scale_vectorsize}.collector_usage\n                                         [d-tmem], [a-tmem], b-desc , [sp-meta-tmem] , idesc,\n                                         [scale-A-tmem], [scale-B-tmem], enable-input-d;\n\n.kind = { .kind::mxf8f6f4, .kind::mxf4, .kind::mxf4nvf4 }\n.scale_vectorsize = { .scale_vec::1X, .scale_vec::2X, .scale_vec::4X, .block16, .block32 }\n.collector_usage = { .collector::buffer::op }\n::buffer         = { ::a }\n::op             = { ::fill, ::use, ::lastuse, ::discard* }\n\n----------------------------------------------------------------------------------\n\n// 5. Integer type:\n\ntcgen05.mma.sp.cta_group.kind::i8 [d-tmem],  a-desc,  b-desc, [sp-meta-tmem] , idesc,\n                                  { disable-output-lane }, enable-input-d;\n\ntcgen05.mma.sp.cta_group.kind::i8 [d-tmem], [a-tmem], b-desc, [sp-meta-tmem] , idesc,\n                                  { disable-output-lane }, enable-input-d;\n\n.cta_group      = { .cta_group::1,  .cta_group::2 }\n\n----------------------------------------------------------------------------------\n\n// 6. Convolution MMA with Integer type:\n\ntcgen05.mma.sp.cta_group.kind::i8.collector_usage          [d-tmem],  a-desc,  b-desc,\n                                                           [sp-meta-tmem] , idesc,\n                                                           { disable-output-lane }, enable-input-d;\n\ntcgen05.mma.sp.cta_group.kind::i8.ashift{.collector_usage} [d-tmem], [a-tmem], b-desc,\n                                                           [sp-meta-tmem], idesc ,\n                                                           { disable-output-lane }, enable-input-d;\n\ntcgen05.mma.sp.cta_group.kind::i8{.ashift}.collector_usage [d-tmem], [a-tmem], b-desc,\n                                                           [sp-meta-tmem], idesc ,\n                                                           { disable-output-lane }, enable-input-d;\n\n.collector_usage = { .collector::buffer::op }\n::buffer         = { ::a }\n::op             = { ::fill, ::use, ::lastuse, ::discard* }"),
      PtxVersion(8, 6),
      SmTarget(100),
      Vector(InstructionForm("tcgen05.mma.sp", Vector(OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Vector, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("kind::mxf4", "kind::mxf4nvf4", "kind::mxf8f6f4")), _root_.scala.collection.immutable.Set(), false), InstructionForm("tcgen05.mma.sp", Vector(OperandConstraint.Address, OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Vector, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("kind::mxf4", "kind::mxf4nvf4", "kind::mxf8f6f4")), _root_.scala.collection.immutable.Set(), false), InstructionForm("tcgen05.mma.sp", Vector(OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Address, OperandConstraint.Address, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("kind::mxf4", "kind::mxf4nvf4", "kind::mxf8f6f4"), _root_.scala.collection.immutable.Set("block_scale")), _root_.scala.collection.immutable.Set("block16", "block32"), false), InstructionForm("tcgen05.mma.sp", Vector(OperandConstraint.Address, OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Address, OperandConstraint.Address, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("kind::mxf4", "kind::mxf4nvf4", "kind::mxf8f6f4"), _root_.scala.collection.immutable.Set("block_scale")), _root_.scala.collection.immutable.Set("block16", "block32"), false), InstructionForm("tcgen05.mma.sp", Vector(OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Vector, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("kind::mxf4", "kind::mxf4nvf4", "kind::mxf8f6f4"), _root_.scala.collection.immutable.Set("collector::buffer::op")), _root_.scala.collection.immutable.Set(), false), InstructionForm("tcgen05.mma.sp", Vector(OperandConstraint.Address, OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Vector, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("kind::mxf4", "kind::mxf4nvf4", "kind::mxf8f6f4"), _root_.scala.collection.immutable.Set("ashift")), _root_.scala.collection.immutable.Set("collector::buffer::op"), false), InstructionForm("tcgen05.mma.sp", Vector(OperandConstraint.Address, OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Vector, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("kind::mxf4", "kind::mxf4nvf4", "kind::mxf8f6f4"), _root_.scala.collection.immutable.Set("collector::buffer::op")), _root_.scala.collection.immutable.Set("ashift"), false), InstructionForm("tcgen05.mma.sp", Vector(OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Address, OperandConstraint.Address, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("kind::mxf4", "kind::mxf4nvf4", "kind::mxf8f6f4"), _root_.scala.collection.immutable.Set("block_scale"), _root_.scala.collection.immutable.Set("collector::buffer::op")), _root_.scala.collection.immutable.Set("block16", "block32"), false), InstructionForm("tcgen05.mma.sp", Vector(OperandConstraint.Address, OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Address, OperandConstraint.Address, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("kind::mxf4", "kind::mxf4nvf4", "kind::mxf8f6f4"), _root_.scala.collection.immutable.Set("block_scale"), _root_.scala.collection.immutable.Set("collector::buffer::op")), _root_.scala.collection.immutable.Set("block16", "block32"), false), InstructionForm("tcgen05.mma.sp", Vector(OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Vector, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("kind::i8")), _root_.scala.collection.immutable.Set(), false), InstructionForm("tcgen05.mma.sp", Vector(OperandConstraint.Address, OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Vector, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("kind::i8")), _root_.scala.collection.immutable.Set(), false), InstructionForm("tcgen05.mma.sp", Vector(OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Vector, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("kind::i8"), _root_.scala.collection.immutable.Set("collector::buffer::op")), _root_.scala.collection.immutable.Set(), false), InstructionForm("tcgen05.mma.sp", Vector(OperandConstraint.Address, OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Vector, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("kind::i8"), _root_.scala.collection.immutable.Set("ashift")), _root_.scala.collection.immutable.Set("collector::buffer::op"), false), InstructionForm("tcgen05.mma.sp", Vector(OperandConstraint.Address, OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Vector, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("kind::i8"), _root_.scala.collection.immutable.Set("collector::buffer::op")), _root_.scala.collection.immutable.Set("ashift"), false))
    ),
    InstructionSpec(
      "tcgen05-mma-instructions-mma-ws",
      "9.7.17.10.9.3. TensorCore 5th Generation Instructions: tcgen05.mma.ws",
      "tcgen05.mma.ws",
      "TensorCore 5th Generation Instructions",
      Vector("tcgen05.mma.ws"),
      Vector("// 1. Floating-point type without block scaling:\n\ntcgen05.mma.ws.cta_group::1.kind{.collector_usage}    [d-tmem],  a-desc,  b-desc,  idesc,\n                                                      enable-input-d {, zero-column-mask-desc };\n\ntcgen05.mma.ws.cta_group::1.kind{.collector_usage}    [d-tmem], [a-tmem], b-desc, idesc,\n                                                      enable-input-d {, zero-column-mask-desc };\n\n.kind = { .kind::f16, .kind::tf32, .kind::f8f6f4 }\n\n----------------------------------------------------------------------------------\n\n// 2. Integer type:\n\ntcgen05.mma.ws.cta_group::1.kind::i8{.collector_usage} [d-tmem],  a-desc,  b-desc, idesc,\n                                                       enable-input-d {, zero-column-mask-desc};\n\ntcgen05.mma.ws.cta_group::1.kind::i8{.collector_usage} [d-tmem], [a-tmem], b-desc, idesc,\n                                                       enable-input-d {, zero-column-mask-desc};\n\n.collector_usage = { .collector::buffer::op }\n::buffer = { ::b0, ::b1, ::b2, ::b3 }\n::op   = { ::fill, ::use, ::lastuse, ::discard}"),
      PtxVersion(8, 6),
      SmTarget(100),
      Vector(InstructionForm("tcgen05.mma.ws", Vector(OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("cta_group"), _root_.scala.collection.immutable.Set("kind::f16", "kind::f8f6f4", "kind::tf32")), _root_.scala.collection.immutable.Set("collector::buffer::op"), false), InstructionForm("tcgen05.mma.ws", Vector(OperandConstraint.Address, OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("cta_group"), _root_.scala.collection.immutable.Set("kind::f16", "kind::f8f6f4", "kind::tf32")), _root_.scala.collection.immutable.Set("collector::buffer::op"), false), InstructionForm("tcgen05.mma.ws", Vector(OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("cta_group"), _root_.scala.collection.immutable.Set("kind::i8")), _root_.scala.collection.immutable.Set("collector::buffer::op"), false), InstructionForm("tcgen05.mma.ws", Vector(OperandConstraint.Address, OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("cta_group"), _root_.scala.collection.immutable.Set("kind::i8")), _root_.scala.collection.immutable.Set("collector::buffer::op"), false))
    ),
    InstructionSpec(
      "tcgen05-mma-instructions-mma-ws-sp",
      "9.7.17.10.9.4. TensorCore 5th Generation Instructions: tcgen05.mma.ws.sp",
      "tcgen05.mma.ws.sp",
      "TensorCore 5th Generation Instructions",
      Vector("tcgen05.mma.ws.sp"),
      Vector("// 1. Floating-point type without block scaling:\n\ntcgen05.mma.ws.sp.cta_group::1.kind{.collector_usage} [d-tmem],  a-desc,  b-desc,\n                                                      [sp-meta-tmem] ,  idesc,\n                                                      enable-input-d {, zero-column-mask-desc};\n\ntcgen05.mma.ws.sp.cta_group::1.kind{.collector_usage} [d-tmem], [a-tmem], b-desc,\n                                                      [sp-meta-tmem] , idesc,\n                                                      enable-input-d {, zero-column-mask-desc};\n\n.kind = { .kind::f16, .kind::tf32, .kind::f8f6f4 }\n\n----------------------------------------------------------------------------------\n\n// 2. Integer type:\n\ntcgen05.mma.ws.sp.cta_group::1.kind::i8{.collector_usage} [d-tmem], a-desc, b-desc,\n                                                          [sp-meta-tmem] , idesc,\n                                                          enable-input-d {, zero-column-mask-desc};\n\ntcgen05.mma.ws.sp.cta_group::1.kind::i8{.collector_usage} [d-tmem], [a-tmem], b-desc,\n                                                          [sp-meta-tmem] , idesc,\n                                                          enable-input-d {, zero-column-mask-desc};\n\n.collector_usage = { .collector::buffer::op }\n::buffer = { ::b0, ::b1, ::b2, ::b3 }\n::op   = { ::fill, ::use, ::lastuse, ::discard }"),
      PtxVersion(8, 6),
      SmTarget(100),
      Vector(InstructionForm("tcgen05.mma.ws.sp", Vector(OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("cta_group"), _root_.scala.collection.immutable.Set("kind::f16", "kind::f8f6f4", "kind::tf32")), _root_.scala.collection.immutable.Set("collector::buffer::op"), false), InstructionForm("tcgen05.mma.ws.sp", Vector(OperandConstraint.Address, OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("cta_group"), _root_.scala.collection.immutable.Set("kind::f16", "kind::f8f6f4", "kind::tf32")), _root_.scala.collection.immutable.Set("collector::buffer::op"), false), InstructionForm("tcgen05.mma.ws.sp", Vector(OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("cta_group"), _root_.scala.collection.immutable.Set("kind::i8")), _root_.scala.collection.immutable.Set("collector::buffer::op"), false), InstructionForm("tcgen05.mma.ws.sp", Vector(OperandConstraint.Address, OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("cta_group"), _root_.scala.collection.immutable.Set("kind::i8")), _root_.scala.collection.immutable.Set("collector::buffer::op"), false))
    ),
    InstructionSpec(
      "tcgen05-special-sync-operations-fence",
      "9.7.17.11.1. TensorCore 5th Generation Instructions: tcgen05.fence",
      "tcgen05.fence",
      "TensorCore 5th Generation Instructions",
      Vector("tcgen05.fence::before_thread_sync", "tcgen05.fence::after_thread_sync"),
      Vector("tcgen05.fence::before_thread_sync ;\ntcgen05.fence::after_thread_sync  ;"),
      PtxVersion(8, 6),
      SmTarget(100),
      Vector(InstructionForm("tcgen05.fence::before_thread_sync", Vector(), Vector(), _root_.scala.collection.immutable.Set(), false), InstructionForm("tcgen05.fence::after_thread_sync", Vector(), Vector(), _root_.scala.collection.immutable.Set(), false))
    ),
    InstructionSpec(
      "tcgen-async-sync-operations-commit",
      "9.7.17.12.1. TensorCore 5th Generation Instructions: tcgen05.commit",
      "tcgen05.commit",
      "TensorCore 5th Generation Instructions",
      Vector("tcgen05.commit"),
      Vector("tcgen05.commit.cta_group.completion_mechanism{.shared::cluster}{.multicast}.b64\n                                                            [mbar] {, ctaMask};\n\n.completion_mechanism = { .mbarrier::arrive::one }\n.cta_group            = { .cta_group::1, .cta_group::2 }\n.multicast            = { .multicast::cluster }"),
      PtxVersion(8, 6),
      SmTarget(100),
      Vector(InstructionForm("tcgen05.commit", Vector(OperandConstraint.Address, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("mbarrier::arrive::one"), _root_.scala.collection.immutable.Set("b64")), _root_.scala.collection.immutable.Set("multicast::cluster", "shared::cluster"), false))
    ),
    InstructionSpec(
      "stack-manipulation-instructions-stacksave",
      "9.7.18.1. Stack Manipulation Instructions: stacksave",
      "stacksave",
      "Stack Manipulation Instructions",
      Vector("stacksave"),
      Vector("stacksave.type  d;\n\n.type = { .u32, .u64 };"),
      PtxVersion(7, 3),
      SmTarget(52),
      Vector(InstructionForm("stacksave", Vector(OperandConstraint.Register), Vector(_root_.scala.collection.immutable.Set("u32", "u64")), _root_.scala.collection.immutable.Set(), true))
    )
)

private def instructionSpecChunk15: Vector[InstructionSpec] = Vector(
    InstructionSpec(
      "stack-manipulation-instructions-stackrestore",
      "9.7.18.2. Stack Manipulation Instructions: stackrestore",
      "stackrestore",
      "Stack Manipulation Instructions",
      Vector("stackrestore"),
      Vector("stackrestore.type  a;\n\n.type = { .u32, .u64 };"),
      PtxVersion(7, 3),
      SmTarget(52),
      Vector(InstructionForm("stackrestore", Vector(OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("u32", "u64")), _root_.scala.collection.immutable.Set(), true))
    ),
    InstructionSpec(
      "stack-manipulation-instructions-alloca",
      "9.7.18.3. Stack Manipulation Instructions: alloca",
      "alloca",
      "Stack Manipulation Instructions",
      Vector("alloca"),
      Vector("alloca.type  ptr, size{, immAlign};\n\n.type = { .u32, .u64 };"),
      PtxVersion(7, 3),
      SmTarget(52),
      Vector(InstructionForm("alloca", Vector(OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("u32", "u64")), _root_.scala.collection.immutable.Set(), true))
    ),
    InstructionSpec(
      "scalar-video-instructions-vadd-vsub-vabsdiff-vmin-vmax",
      "9.7.19.1.1. Scalar Video Instructions: vadd, vsub, vabsdiff, vmin, vmax",
      "vadd, vsub, vabsdiff, vmin, vmax",
      "Scalar Video Instructions",
      Vector("vadd", "vsub", "vabsdiff", "vmin", "vmax"),
      Vector("// 32-bit scalar operation, with optional secondary operation\nvop.dtype.atype.btype{.sat}       d, a{.asel}, b{.bsel};\nvop.dtype.atype.btype{.sat}.op2   d, a{.asel}, b{.bsel}, c;\n\n// 32-bit scalar operation, with optional data merge\nvop.dtype.atype.btype{.sat}  d.dsel, a{.asel}, b{.bsel}, c;\n\n vop   = { vadd, vsub, vabsdiff, vmin, vmax };\n.dtype = .atype = .btype = { .u32, .s32 };\n.dsel  = .asel  = .bsel  = { .b0, .b1, .b2, .b3, .h0, .h1 };\n.op2   = { .add, .min, .max };"),
      PtxVersion(2, 0),
      SmTarget(20),
      Vector(InstructionForm("vadd", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("dtype"), _root_.scala.collection.immutable.Set("atype"), _root_.scala.collection.immutable.Set("s32", "u32")), _root_.scala.collection.immutable.Set("sat"), true), InstructionForm("vsub", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("dtype"), _root_.scala.collection.immutable.Set("atype"), _root_.scala.collection.immutable.Set("s32", "u32")), _root_.scala.collection.immutable.Set("sat"), true), InstructionForm("vabsdiff", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("dtype"), _root_.scala.collection.immutable.Set("atype"), _root_.scala.collection.immutable.Set("s32", "u32")), _root_.scala.collection.immutable.Set("sat"), true), InstructionForm("vmin", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("dtype"), _root_.scala.collection.immutable.Set("atype"), _root_.scala.collection.immutable.Set("s32", "u32")), _root_.scala.collection.immutable.Set("sat"), true), InstructionForm("vmax", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("dtype"), _root_.scala.collection.immutable.Set("atype"), _root_.scala.collection.immutable.Set("s32", "u32")), _root_.scala.collection.immutable.Set("sat"), true), InstructionForm("vadd", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("dtype"), _root_.scala.collection.immutable.Set("atype"), _root_.scala.collection.immutable.Set("s32", "u32"), _root_.scala.collection.immutable.Set("add", "max", "min")), _root_.scala.collection.immutable.Set("sat"), true), InstructionForm("vsub", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("dtype"), _root_.scala.collection.immutable.Set("atype"), _root_.scala.collection.immutable.Set("s32", "u32"), _root_.scala.collection.immutable.Set("add", "max", "min")), _root_.scala.collection.immutable.Set("sat"), true), InstructionForm("vabsdiff", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("dtype"), _root_.scala.collection.immutable.Set("atype"), _root_.scala.collection.immutable.Set("s32", "u32"), _root_.scala.collection.immutable.Set("add", "max", "min")), _root_.scala.collection.immutable.Set("sat"), true), InstructionForm("vmin", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("dtype"), _root_.scala.collection.immutable.Set("atype"), _root_.scala.collection.immutable.Set("s32", "u32"), _root_.scala.collection.immutable.Set("add", "max", "min")), _root_.scala.collection.immutable.Set("sat"), true), InstructionForm("vmax", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("dtype"), _root_.scala.collection.immutable.Set("atype"), _root_.scala.collection.immutable.Set("s32", "u32"), _root_.scala.collection.immutable.Set("add", "max", "min")), _root_.scala.collection.immutable.Set("sat"), true), InstructionForm("vadd", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("dtype"), _root_.scala.collection.immutable.Set("atype"), _root_.scala.collection.immutable.Set("s32", "u32")), _root_.scala.collection.immutable.Set("sat"), true), InstructionForm("vsub", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("dtype"), _root_.scala.collection.immutable.Set("atype"), _root_.scala.collection.immutable.Set("s32", "u32")), _root_.scala.collection.immutable.Set("sat"), true), InstructionForm("vabsdiff", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("dtype"), _root_.scala.collection.immutable.Set("atype"), _root_.scala.collection.immutable.Set("s32", "u32")), _root_.scala.collection.immutable.Set("sat"), true), InstructionForm("vmin", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("dtype"), _root_.scala.collection.immutable.Set("atype"), _root_.scala.collection.immutable.Set("s32", "u32")), _root_.scala.collection.immutable.Set("sat"), true), InstructionForm("vmax", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("dtype"), _root_.scala.collection.immutable.Set("atype"), _root_.scala.collection.immutable.Set("s32", "u32")), _root_.scala.collection.immutable.Set("sat"), true))
    ),
    InstructionSpec(
      "scalar-video-instructions-vshl-vshr",
      "9.7.19.1.2. Scalar Video Instructions: vshl, vshr",
      "vshl, vshr",
      "Scalar Video Instructions",
      Vector("vshl", "vshr"),
      Vector("// 32-bit scalar operation, with optional secondary operation\nvop.dtype.atype.u32{.sat}.mode       d, a{.asel}, b{.bsel};\nvop.dtype.atype.u32{.sat}.mode.op2   d, a{.asel}, b{.bsel}, c;\n\n// 32-bit scalar operation, with optional data merge\nvop.dtype.atype.u32{.sat}.mode  d.dsel, a{.asel}, b{.bsel}, c;\n\n vop   = { vshl, vshr };\n.dtype = .atype = { .u32, .s32 };\n.mode  = { .clamp, .wrap };\n.dsel  = .asel  = .bsel  = { .b0, .b1, .b2, .b3, .h0, .h1 };\n.op2   = { .add, .min, .max };"),
      PtxVersion(2, 0),
      SmTarget(20),
      Vector(InstructionForm("vshl", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("dtype"), _root_.scala.collection.immutable.Set("s32", "u32"), _root_.scala.collection.immutable.Set("u32"), _root_.scala.collection.immutable.Set("clamp", "wrap")), _root_.scala.collection.immutable.Set("sat"), true), InstructionForm("vshr", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("dtype"), _root_.scala.collection.immutable.Set("s32", "u32"), _root_.scala.collection.immutable.Set("u32"), _root_.scala.collection.immutable.Set("clamp", "wrap")), _root_.scala.collection.immutable.Set("sat"), true), InstructionForm("vshl", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("dtype"), _root_.scala.collection.immutable.Set("s32", "u32"), _root_.scala.collection.immutable.Set("u32"), _root_.scala.collection.immutable.Set("clamp", "wrap"), _root_.scala.collection.immutable.Set("add", "max", "min")), _root_.scala.collection.immutable.Set("sat"), true), InstructionForm("vshr", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("dtype"), _root_.scala.collection.immutable.Set("s32", "u32"), _root_.scala.collection.immutable.Set("u32"), _root_.scala.collection.immutable.Set("clamp", "wrap"), _root_.scala.collection.immutable.Set("add", "max", "min")), _root_.scala.collection.immutable.Set("sat"), true), InstructionForm("vshl", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("dtype"), _root_.scala.collection.immutable.Set("s32", "u32"), _root_.scala.collection.immutable.Set("u32"), _root_.scala.collection.immutable.Set("clamp", "wrap")), _root_.scala.collection.immutable.Set("sat"), true), InstructionForm("vshr", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("dtype"), _root_.scala.collection.immutable.Set("s32", "u32"), _root_.scala.collection.immutable.Set("u32"), _root_.scala.collection.immutable.Set("clamp", "wrap")), _root_.scala.collection.immutable.Set("sat"), true))
    ),
    InstructionSpec(
      "scalar-video-instructions-vmad",
      "9.7.19.1.3. Scalar Video Instructions: vmad",
      "vmad",
      "Scalar Video Instructions",
      Vector("vmad"),
      Vector("// 32-bit scalar operation\nvmad.dtype.atype.btype{.sat}{.scale}     d, {-}a{.asel}, {-}b{.bsel},\n                                         {-}c;\nvmad.dtype.atype.btype.po{.sat}{.scale}  d, a{.asel}, b{.bsel}, c;\n\n.dtype = .atype = .btype = { .u32, .s32 };\n.asel  = .bsel  = { .b0, .b1, .b2, .b3, .h0, .h1 };\n.scale = { .shr7, .shr15 };"),
      PtxVersion(2, 0),
      SmTarget(20),
      Vector(InstructionForm("vmad", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("dtype"), _root_.scala.collection.immutable.Set("atype"), _root_.scala.collection.immutable.Set("s32", "u32")), _root_.scala.collection.immutable.Set("sat", "shr15", "shr7"), true), InstructionForm("vmad", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("dtype"), _root_.scala.collection.immutable.Set("atype"), _root_.scala.collection.immutable.Set("s32", "u32"), _root_.scala.collection.immutable.Set("po")), _root_.scala.collection.immutable.Set("sat", "shr15", "shr7"), true))
    ),
    InstructionSpec(
      "scalar-video-instructions-vset",
      "9.7.19.1.4. Scalar Video Instructions: vset",
      "vset",
      "Scalar Video Instructions",
      Vector("vset"),
      Vector("// 32-bit scalar operation, with optional secondary operation\nvset.atype.btype.cmp       d, a{.asel}, b{.bsel};\nvset.atype.btype.cmp.op2   d, a{.asel}, b{.bsel}, c;\n\n// 32-bit scalar operation, with optional data merge\nvset.atype.btype.cmp  d.dsel, a{.asel}, b{.bsel}, c;\n\n.atype = .btype = { .u32, .s32 };\n.cmp   = { .eq, .ne, .lt, .le, .gt, .ge };\n.dsel  = .asel  = .bsel  = { .b0, .b1, .b2, .b3, .h0, .h1 };\n.op2   = { .add, .min, .max };"),
      PtxVersion(2, 0),
      SmTarget(20),
      Vector(InstructionForm("vset", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("atype"), _root_.scala.collection.immutable.Set("s32", "u32"), _root_.scala.collection.immutable.Set("eq", "ge", "gt", "le", "lt", "ne")), _root_.scala.collection.immutable.Set(), true), InstructionForm("vset", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("atype"), _root_.scala.collection.immutable.Set("s32", "u32"), _root_.scala.collection.immutable.Set("eq", "ge", "gt", "le", "lt", "ne"), _root_.scala.collection.immutable.Set("add", "max", "min")), _root_.scala.collection.immutable.Set(), true), InstructionForm("vset", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("atype"), _root_.scala.collection.immutable.Set("s32", "u32"), _root_.scala.collection.immutable.Set("eq", "ge", "gt", "le", "lt", "ne")), _root_.scala.collection.immutable.Set(), true))
    ),
    InstructionSpec(
      "simd-video-instructions-vadd2-vsub2-vavrg2-vabsdiff2-vmin2-vmax2",
      "9.7.19.2.1. SIMD Video Instructions: vadd2, vsub2, vavrg2, vabsdiff2, vmin2, vmax2",
      "vadd2, vsub2, vavrg2, vabsdiff2, vmin2, vmax2",
      "SIMD Video Instructions",
      Vector("vadd2", "vsub2", "vavrg2", "vabsdiff2", "vmin2", "vmax2"),
      Vector("// SIMD instruction with secondary SIMD merge operation\nvop2.dtype.atype.btype{.sat}  d{.mask}, a{.asel}, b{.bsel}, c;\n\n// SIMD instruction with secondary accumulate operation\nvop2.dtype.atype.btype.add  d{.mask}, a{.asel}, b{.bsel}, c;\n\n vop2  = { vadd2, vsub2, vavrg2, vabsdiff2, vmin2, vmax2 };\n.dtype = .atype = .btype = { .u32, .s32 };\n.mask  = { .h0, .h1, .h10 };  // defaults to .h10\n.asel  = .bsel  = { .hxy, where x,y are from { 0, 1, 2, 3 } };\n   .asel defaults to .h10\n   .bsel defaults to .h32"),
      PtxVersion(3, 0),
      SmTarget(30),
      Vector(InstructionForm("vadd2", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("dtype"), _root_.scala.collection.immutable.Set("atype"), _root_.scala.collection.immutable.Set("s32", "u32")), _root_.scala.collection.immutable.Set("sat"), true), InstructionForm("vsub2", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("dtype"), _root_.scala.collection.immutable.Set("atype"), _root_.scala.collection.immutable.Set("s32", "u32")), _root_.scala.collection.immutable.Set("sat"), true), InstructionForm("vavrg2", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("dtype"), _root_.scala.collection.immutable.Set("atype"), _root_.scala.collection.immutable.Set("s32", "u32")), _root_.scala.collection.immutable.Set("sat"), true), InstructionForm("vabsdiff2", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("dtype"), _root_.scala.collection.immutable.Set("atype"), _root_.scala.collection.immutable.Set("s32", "u32")), _root_.scala.collection.immutable.Set("sat"), true), InstructionForm("vmin2", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("dtype"), _root_.scala.collection.immutable.Set("atype"), _root_.scala.collection.immutable.Set("s32", "u32")), _root_.scala.collection.immutable.Set("sat"), true), InstructionForm("vmax2", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("dtype"), _root_.scala.collection.immutable.Set("atype"), _root_.scala.collection.immutable.Set("s32", "u32")), _root_.scala.collection.immutable.Set("sat"), true), InstructionForm("vadd2", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("dtype"), _root_.scala.collection.immutable.Set("atype"), _root_.scala.collection.immutable.Set("s32", "u32"), _root_.scala.collection.immutable.Set("add")), _root_.scala.collection.immutable.Set(), true), InstructionForm("vsub2", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("dtype"), _root_.scala.collection.immutable.Set("atype"), _root_.scala.collection.immutable.Set("s32", "u32"), _root_.scala.collection.immutable.Set("add")), _root_.scala.collection.immutable.Set(), true), InstructionForm("vavrg2", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("dtype"), _root_.scala.collection.immutable.Set("atype"), _root_.scala.collection.immutable.Set("s32", "u32"), _root_.scala.collection.immutable.Set("add")), _root_.scala.collection.immutable.Set(), true), InstructionForm("vabsdiff2", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("dtype"), _root_.scala.collection.immutable.Set("atype"), _root_.scala.collection.immutable.Set("s32", "u32"), _root_.scala.collection.immutable.Set("add")), _root_.scala.collection.immutable.Set(), true), InstructionForm("vmin2", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("dtype"), _root_.scala.collection.immutable.Set("atype"), _root_.scala.collection.immutable.Set("s32", "u32"), _root_.scala.collection.immutable.Set("add")), _root_.scala.collection.immutable.Set(), true), InstructionForm("vmax2", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("dtype"), _root_.scala.collection.immutable.Set("atype"), _root_.scala.collection.immutable.Set("s32", "u32"), _root_.scala.collection.immutable.Set("add")), _root_.scala.collection.immutable.Set(), true))
    ),
    InstructionSpec(
      "simd-video-instructions-vset2",
      "9.7.19.2.2. SIMD Video Instructions: vset2",
      "vset2",
      "SIMD Video Instructions",
      Vector("vset2"),
      Vector("// SIMD instruction with secondary SIMD merge operation\nvset2.atype.btype.cmp  d{.mask}, a{.asel}, b{.bsel}, c;\n\n// SIMD instruction with secondary accumulate operation\nvset2.atype.btype.cmp.add  d{.mask}, a{.asel}, b{.bsel}, c;\n\n.atype = .btype = { .u32, .s32 };\n.cmp   = { .eq, .ne, .lt, .le, .gt, .ge };\n.mask  = { .h0, .h1, .h10 };  // defaults to .h10\n.asel  = .bsel  = { .hxy, where x,y are from { 0, 1, 2, 3 } };\n   .asel defaults to .h10\n   .bsel defaults to .h32"),
      PtxVersion(3, 0),
      SmTarget(30),
      Vector(InstructionForm("vset2", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("atype"), _root_.scala.collection.immutable.Set("s32", "u32"), _root_.scala.collection.immutable.Set("eq", "ge", "gt", "le", "lt", "ne")), _root_.scala.collection.immutable.Set(), true), InstructionForm("vset2", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("atype"), _root_.scala.collection.immutable.Set("s32", "u32"), _root_.scala.collection.immutable.Set("eq", "ge", "gt", "le", "lt", "ne"), _root_.scala.collection.immutable.Set("add")), _root_.scala.collection.immutable.Set(), true))
    ),
    InstructionSpec(
      "simd-video-instructions-vadd4-vsub4-vavrg4-vabsdiff4-vmin4-vmax4",
      "9.7.19.2.3. SIMD Video Instructions: vadd4, vsub4, vavrg4, vabsdiff4, vmin4, vmax4",
      "vadd4, vsub4, vavrg4, vabsdiff4, vmin4, vmax4",
      "SIMD Video Instructions",
      Vector("vadd4", "vsub4", "vavrg4", "vabsdiff4", "vmin4", "vmax4"),
      Vector("// SIMD instruction with secondary SIMD merge operation\nvop4.dtype.atype.btype{.sat}  d{.mask}, a{.asel}, b{.bsel}, c;\n\n// SIMD instruction with secondary accumulate operation\nvop4.dtype.atype.btype.add  d{.mask}, a{.asel}, b{.bsel}, c;\nvop4  = { vadd4, vsub4, vavrg4, vabsdiff4, vmin4, vmax4 };\n\n.dtype = .atype = .btype = { .u32, .s32 };\n.mask  = { .b0,\n           .b1, .b10\n           .b2, .b20, .b21, .b210,\n           .b3, .b30, .b31, .b310, .b32, .b320, .b321, .b3210 };\n    defaults to .b3210\n.asel = .bsel = .bxyzw, where x,y,z,w are from { 0, ..., 7 };\n   .asel defaults to .b3210\n   .bsel defaults to .b7654"),
      PtxVersion(3, 0),
      SmTarget(30),
      Vector(InstructionForm("vadd4", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("dtype"), _root_.scala.collection.immutable.Set("atype"), _root_.scala.collection.immutable.Set("s32", "u32")), _root_.scala.collection.immutable.Set("sat"), true), InstructionForm("vsub4", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("dtype"), _root_.scala.collection.immutable.Set("atype"), _root_.scala.collection.immutable.Set("s32", "u32")), _root_.scala.collection.immutable.Set("sat"), true), InstructionForm("vavrg4", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("dtype"), _root_.scala.collection.immutable.Set("atype"), _root_.scala.collection.immutable.Set("s32", "u32")), _root_.scala.collection.immutable.Set("sat"), true), InstructionForm("vabsdiff4", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("dtype"), _root_.scala.collection.immutable.Set("atype"), _root_.scala.collection.immutable.Set("s32", "u32")), _root_.scala.collection.immutable.Set("sat"), true), InstructionForm("vmin4", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("dtype"), _root_.scala.collection.immutable.Set("atype"), _root_.scala.collection.immutable.Set("s32", "u32")), _root_.scala.collection.immutable.Set("sat"), true), InstructionForm("vmax4", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("dtype"), _root_.scala.collection.immutable.Set("atype"), _root_.scala.collection.immutable.Set("s32", "u32")), _root_.scala.collection.immutable.Set("sat"), true), InstructionForm("vadd4", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("dtype"), _root_.scala.collection.immutable.Set("atype"), _root_.scala.collection.immutable.Set("s32", "u32"), _root_.scala.collection.immutable.Set("add")), _root_.scala.collection.immutable.Set(), true), InstructionForm("vsub4", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("dtype"), _root_.scala.collection.immutable.Set("atype"), _root_.scala.collection.immutable.Set("s32", "u32"), _root_.scala.collection.immutable.Set("add")), _root_.scala.collection.immutable.Set(), true), InstructionForm("vavrg4", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("dtype"), _root_.scala.collection.immutable.Set("atype"), _root_.scala.collection.immutable.Set("s32", "u32"), _root_.scala.collection.immutable.Set("add")), _root_.scala.collection.immutable.Set(), true), InstructionForm("vabsdiff4", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("dtype"), _root_.scala.collection.immutable.Set("atype"), _root_.scala.collection.immutable.Set("s32", "u32"), _root_.scala.collection.immutable.Set("add")), _root_.scala.collection.immutable.Set(), true), InstructionForm("vmin4", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("dtype"), _root_.scala.collection.immutable.Set("atype"), _root_.scala.collection.immutable.Set("s32", "u32"), _root_.scala.collection.immutable.Set("add")), _root_.scala.collection.immutable.Set(), true), InstructionForm("vmax4", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("dtype"), _root_.scala.collection.immutable.Set("atype"), _root_.scala.collection.immutable.Set("s32", "u32"), _root_.scala.collection.immutable.Set("add")), _root_.scala.collection.immutable.Set(), true))
    ),
    InstructionSpec(
      "simd-video-instructions-vset4",
      "9.7.19.2.4. SIMD Video Instructions: vset4",
      "vset4",
      "SIMD Video Instructions",
      Vector("vset4"),
      Vector("// SIMD instruction with secondary SIMD merge operation\nvset4.atype.btype.cmp  d{.mask}, a{.asel}, b{.bsel}, c;\n\n// SIMD instruction with secondary accumulate operation\nvset4.atype.btype.cmp.add  d{.mask}, a{.asel}, b{.bsel}, c;\n\n.atype = .btype = { .u32, .s32 };\n.cmp   = { .eq, .ne, .lt, .le, .gt, .ge };\n.mask  = { .b0,\n           .b1, .b10\n           .b2, .b20, .b21, .b210,\n           .b3, .b30, .b31, .b310, .b32, .b320, .b321, .b3210 };\n    defaults to .b3210\n.asel = .bsel = .bxyzw, where x,y,z,w are from { 0, ..., 7 };\n   .asel defaults to .b3210\n   .bsel defaults to .b7654"),
      PtxVersion(3, 0),
      SmTarget(30),
      Vector(InstructionForm("vset4", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("atype"), _root_.scala.collection.immutable.Set("s32", "u32"), _root_.scala.collection.immutable.Set("eq", "ge", "gt", "le", "lt", "ne")), _root_.scala.collection.immutable.Set(), true), InstructionForm("vset4", Vector(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("atype"), _root_.scala.collection.immutable.Set("s32", "u32"), _root_.scala.collection.immutable.Set("eq", "ge", "gt", "le", "lt", "ne"), _root_.scala.collection.immutable.Set("add")), _root_.scala.collection.immutable.Set(), true))
    ),
    InstructionSpec(
      "miscellaneous-instructions-brkpt",
      "9.7.20.1. Miscellaneous Instructions: brkpt",
      "brkpt",
      "Miscellaneous Instructions",
      Vector("brkpt"),
      Vector("brkpt;"),
      PtxVersion(1, 0),
      SmTarget(11),
      Vector(InstructionForm("brkpt", Vector(), Vector(), _root_.scala.collection.immutable.Set(), true))
    ),
    InstructionSpec(
      "miscellaneous-instructions-nanosleep",
      "9.7.20.2. Miscellaneous Instructions: nanosleep",
      "nanosleep",
      "Miscellaneous Instructions",
      Vector("nanosleep"),
      Vector("nanosleep.u32 t;"),
      PtxVersion(1, 0),
      SmTarget(70),
      Vector(InstructionForm("nanosleep", Vector(OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("u32")), _root_.scala.collection.immutable.Set(), true))
    )
)

private def instructionSpecChunk16: Vector[InstructionSpec] = Vector(
    InstructionSpec(
      "miscellaneous-instructions-pmevent",
      "9.7.20.3. Miscellaneous Instructions: pmevent",
      "pmevent",
      "Miscellaneous Instructions",
      Vector("pmevent"),
      Vector("pmevent       a;    // trigger a single performance monitor event\npmevent.mask  a;    // trigger one or more performance monitor events"),
      PtxVersion(1, 0),
      SmTarget(20),
      Vector(InstructionForm("pmevent", Vector(OperandConstraint.Value), Vector(), _root_.scala.collection.immutable.Set(), true), InstructionForm("pmevent", Vector(OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("mask")), _root_.scala.collection.immutable.Set(), true))
    ),
    InstructionSpec(
      "miscellaneous-instructions-trap",
      "9.7.20.4. Miscellaneous Instructions: trap",
      "trap",
      "Miscellaneous Instructions",
      Vector("trap"),
      Vector("trap;"),
      PtxVersion(1, 0),
      SmTarget(10),
      Vector(InstructionForm("trap", Vector(), Vector(), _root_.scala.collection.immutable.Set(), true))
    ),
    InstructionSpec(
      "miscellaneous-instructions-setmaxnreg",
      "9.7.20.5. Miscellaneous Instructions: setmaxnreg",
      "setmaxnreg",
      "Miscellaneous Instructions",
      Vector("setmaxnreg"),
      Vector("setmaxnreg.action.sync.aligned.u32 imm-reg-count;\n\n.action = { .inc, .dec };"),
      PtxVersion(8, 0),
      SmTarget(100),
      Vector(InstructionForm("setmaxnreg", Vector(OperandConstraint.Value), Vector(_root_.scala.collection.immutable.Set("dec", "inc"), _root_.scala.collection.immutable.Set("sync"), _root_.scala.collection.immutable.Set("aligned"), _root_.scala.collection.immutable.Set("u32")), _root_.scala.collection.immutable.Set(), false))
    )
)

val Instructions: Vector[InstructionSpec] = Vector(instructionSpecChunk0, instructionSpecChunk1, instructionSpecChunk2, instructionSpecChunk3, instructionSpecChunk4, instructionSpecChunk5, instructionSpecChunk6, instructionSpecChunk7, instructionSpecChunk8, instructionSpecChunk9, instructionSpecChunk10, instructionSpecChunk11, instructionSpecChunk12, instructionSpecChunk13, instructionSpecChunk14, instructionSpecChunk15, instructionSpecChunk16).flatten

private[v9_3] val InstructionsByOpcode: Map[String, Vector[InstructionSpec]] =
  Instructions.flatMap(specification => specification.opcodes.map(_ -> specification)).groupMap(_._1)(_._2)
