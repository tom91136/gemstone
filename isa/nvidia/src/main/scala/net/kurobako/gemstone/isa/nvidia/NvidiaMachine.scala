package net.kurobako.gemstone.isa.nvidia

import VMExecution.*
import VMIR.*
import net.kurobako.gemstone.binary.Widths.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.semantics.*
import net.kurobako.gemstone.vm.*

/** The machine operations NVIDIA semantics request: those of [[MachineAlgebra]] over NVIDIA operands, locations and
  * steps, those of [[UnitAlgebra]] over warps, and those of clusters, mbarriers and calls. `C` names a CTA of the
  * cluster, which a shared address names the holder of.
  */
private[isa] trait NvidiaMachine[V, L, D, C, F[_]] extends Any
    with MachineAlgebra[V, Value, Register, L, Pc, D, Step, F]
    with UnitAlgebra[V, Value, Register, L, F] {

  /** 1 when the launch has an explicit cluster, otherwise 0. */
  inline def explicitCluster: V

  /** The number of CTAs in the issuing CTA's cluster. */
  inline def clusterSize: V

  /** The rank of the issuing CTA within its cluster. */
  inline def ctaRank: V

  /** The rank of the CTA whose window holds the shared address `address`, or -1 for the issuing CTA's own window. */
  inline def rankOf(address: V): V

  /** The shared address `address` names within the CTA of rank `rank`. */
  inline def clusterAddress(address: V, rank: V): V

  /** 1 unless the `bytes` at the generic `address` are shared and name a CTA outside the cluster. */
  inline def inCluster(address: V, bytes: V): V

  /** `each` over the ranks of the cluster in ascending order, stopping at the first failure. */
  inline def eachRank(inline each: V => F[Unit]): F[Unit]

  /** `value` of `width` written to the shared address `address`, which may name a CTA of the cluster, then `next`. */
  inline def storeShared(address: V, value: V, width: ScalarBitWidth)(inline next: => F[Unit]): F[Unit]

  /** `data` at the shared address `address`, with zeros past its end up to `size` bytes, then `next`. */
  inline def storeSharedBytes(address: V, data: D, size: V)(inline next: => F[Unit]): F[Unit]

  /** `next` if the shared address `address` lies in shared memory, otherwise a fault naming `what` and the address. */
  inline def requireShared(address: V, what: String)(inline next: => F[Unit]): F[Unit]

  /** `next` over the CTA that holds the `bytes` at a shared `address` and their offset within its shared window; `what`
    * names the access if the address is not shared or names a CTA outside the cluster.
    */
  inline def sharedWindow(lane: L, address: V, bytes: V, what: String)(inline next: (C, V) => F[Unit]): F[Unit]

  /** `each` over every CTA a multicast mask names, or over `cta` alone without one, then `next`. */
  inline def eachDestination(lane: L, cta: C, multicast: Option[Value])(inline each: C => F[Unit])(
      inline next: => F[Unit]
  ): F[Unit]

  /** The lanes arrive at the cluster barrier if `arrive`, and otherwise wait at it. */
  inline def clusterBarrier(arrive: Boolean): F[Step]

  /** `data` stored at `offset` in the shared window of `cta`, then `next`. */
  inline def storeCluster(cta: C, offset: V, data: D)(inline next: => F[Unit]): F[Unit]

  /** `bytes` of transaction count completed on the mbarrier at `barrier` in the shared window of `cta`, then `next`. */
  inline def completeTx(cta: C, barrier: V, bytes: V)(inline next: => F[Unit]): F[Unit]

  inline def sameCta(first: C, second: C): V

  inline def clearCluster(cta: C, offset: V, bytes: V)(inline next: => F[Unit]): F[Unit]

  inline def storeWord(cta: C, offset: V, value: V, width: ScalarBitWidth)(inline next: => F[Unit]): F[Unit]

  inline def reduceWord(cta: C, offset: V, operand: V, width: ScalarBitWidth, operation: AtomicOperation)(
      inline next: => F[Unit]
  ): F[Unit]

  /** faults where element has no form of operator */
  inline def reduceBytes(cta: C, offset: V, data: D, copy: Instruction.AsyncBulkReduce)(
      inline next: => F[Unit]
  ): F[Unit]

  /** each element combined atomically */
  inline def reduceGlobal(lane: L, address: V, data: D, copy: Instruction.AsyncBulkReduce)(
      inline next: => F[Unit]
  ): F[Unit]

  /** byte i is selected by bit i & 15 of mask */
  inline def storeGlobalMasked(lane: L, destination: V, source: V, bytes: V, mask: V)(inline next: => F[Unit]): F[Unit]

  /** `next` over the transfer of the tensor copy `copy` makes through the tensor map `map`, whose coordinates and
    * offsets are `coordinates` and `offsets`: the facts [[tensorFact]] reads, then the runs of box elements and the box
    * that [[loadRun]], [[storeRun]], [[reduceRun]], [[placeBox]] and [[fetchBox]] work on.
    */
  inline def tensorTransfer(map: D, coordinates: Vec[V], offsets: Vec[V], copy: Instruction.AsyncTensor)(
      inline next: D => F[Unit]
  ): F[Unit]

  /** What the transfer `transfer` records as `fact`. */
  inline def tensorFact(transfer: D, fact: TensorLayout.Fact): V

  /** The field `kind` at `axis` of the tensor map `map`, read from its bytes. */
  def tensorField(map: D, kind: TensorMapping.Field, axis: Int): V

  /** The name of the mode or element type the transfer `transfer` records, for a fault. */
  inline def tensorText(transfer: D, text: TensorLayout.Text): String

  /** `each` over the indices below `count` in ascending order, stopping at the first failure, then `next`. */
  inline def eachIndex(count: V)(inline each: V => F[Unit])(inline next: => F[Unit]): F[Unit]

  /** `each` over the runs of box elements of `transfer`, each named by a `run` that [[loadRun]], [[storeRun]] and
    * [[reduceRun]] work on, stopping at the first failure, then `next`.
    */
  inline def eachRun(transfer: D)(inline each: V => F[Unit])(inline next: => F[Unit]): F[Unit]

  /** Run `run` of the box of `transfer` read from global memory, or filled where it lies outside the tensor, then
    * `next`.
    */
  inline def loadRun(lane: L, transfer: D, run: V)(inline next: => F[Unit]): F[Unit]

  /** Run `run` of the box of `transfer` written to global memory unless it lies outside the tensor, then `next`. */
  inline def storeRun(lane: L, transfer: D, run: V)(inline next: => F[Unit]): F[Unit]

  /** Run `run` of the box of `transfer` reduced into global memory by `operator` unless it lies outside the tensor,
    * then `next`.
    */
  inline def reduceRun(lane: L, transfer: D, run: V, operator: TensorMap.Reduction)(inline next: => F[Unit]): F[Unit]

  /** The box of `transfer` stored in the shared window of `cta`, swizzled from the offset `start`, then `next`. */
  inline def placeBox(cta: C, start: V, transfer: D)(inline next: => F[Unit]): F[Unit]

  /** The box of `transfer` read from the shared window of `cta`, swizzled from the offset `start`, then `next`. */
  inline def fetchBox(cta: C, start: V, transfer: D)(inline next: => F[Unit]): F[Unit]

  /** The rank of the CTA of the pair of `cta` whose rank has the parity of `signalled`'s. */
  inline def peerRank(cta: C, signalled: C): V

  /** 1 where the cluster has a CTA of rank `rank`, otherwise 0. */
  inline def hasCta(rank: V): V

  /** The CTA of rank `rank` in the cluster. */
  inline def ctaAt(rank: V): C

  /** `next` over `map` with the value `value` replacing its field `field`, at `index` for the fields that repeat. */
  inline def replaceTensorMap(map: D, field: TensorMap.Field, index: Int, value: V)(inline next: D => F[Unit]): F[Unit]

  /** Initialises the mbarrier at the shared address `barrier` to expect `count` arrivals, then `next`. */
  inline def mbarrierInit(lane: L, barrier: V, count: V)(inline next: => F[Unit]): F[Unit]

  /** Invalidates the mbarrier at the shared address `barrier`, then `next`. */
  inline def mbarrierInvalidate(lane: L, barrier: V)(inline next: => F[Unit]): F[Unit]

  /** Loads the mbarrier at the shared address `barrier` from its stored initial state, then `next`. */
  inline def mbarrierLoad(lane: L, barrier: V)(inline next: => F[Unit]): F[Unit]

  /** Arrives `count` times at the mbarrier at the shared address `barrier`, expecting `bytes` transaction bytes, then
    * `next` over the token of the phase arrived in. `drop` also lowers the arrivals later phases expect, and without
    * `complete` the arrivals must leave the phase incomplete.
    */
  inline def mbarrierArrive(lane: L, barrier: V, count: V, bytes: V, drop: Boolean, complete: Boolean)(
      inline next: V => F[Unit]
  ): F[Unit]

  /** Expects `bytes` transaction bytes on the mbarrier at the shared address `barrier`, or completes them if
    * `complete`, then `next`.
    */
  inline def mbarrierTransaction(lane: L, barrier: V, bytes: V, complete: Boolean)(inline next: => F[Unit]): F[Unit]

  /** Whether the phase of the mbarrier at the shared address `barrier` that `phase` names has completed, then `next`.
    * Where `parity` holds, `phase` is a parity rather than a token.
    */
  inline def mbarrierTest(lane: L, barrier: V, phase: V, parity: Boolean)(inline next: V => F[Unit]): F[Unit]

  /** Writes `value` to the predicate `target` on `lane`, then `next`. */
  inline def writePredicate(lane: L, target: Predicate, value: V)(inline next: => F[Unit]): F[Unit]

  /** The lanes call the functions `function` names, passing `parameters` and receiving `returns`. */
  inline def call(function: Value, returns: Vec[Parameter], parameters: Vec[Parameter]): F[Step]

  /** The lanes return from the function they are in. */
  inline def returnControl: F[Step]

  /** Each active lane continues at the `targets` entry its unsigned 32-bit index selects, and falls through where the
    * index is past the table.
    */
  inline def branchTable(index: Value, targets: Vec[Pc]): F[Step]

  /** The lanes trap. */
  inline def trap: F[Step]

  /** The writes the kernel makes to host-mapped memory become visible to the host. */
  inline def fenceHost: F[Step]

  /** The lanes call the device builtin `builtin`, passing `parameters` and receiving `returns`, then `next`. */
  inline def callBuiltin(builtin: Builtin, returns: Vec[Parameter], parameters: Vec[Parameter])(
      inline next: => F[Step]
  ): F[Step]

  /** Opens a convergence barrier over the active lanes of each warp that has one, writes each lane's barrier to
    * `token`, then `next`.
    */
  inline def setupConvergence(token: Register)(inline next: => F[Step]): F[Step]

  /** The lanes wait at the convergence barrier their `token` names. */
  inline def synchroniseConvergence(token: Register): F[Step]

  /** The active lanes leave the convergence barrier their `token` names, then `next`. */
  inline def leaveConvergence(token: Register)(inline next: => F[Step]): F[Step]

  /** The lanes arrive at the workgroup barrier `barrier`, which expects `count` threads, and reduce the predicates of
    * those threads by `reduction` when it completes.
    */
  inline def parkReduce(barrier: V, count: V, reduction: BarrierReduction): F[Step]

  /** Each lane arrives at the workgroup barrier its `barrier` names, which expects the threads its `count` names, and
    * waits for it if `blocking`. The predicates of the barrier's threads are reduced by `reduction`, where given, when
    * it completes.
    */
  inline def parkEach(
      inline barrier: L => V,
      inline count: L => V,
      inline blocking: Boolean,
      reduction: Option[BarrierReduction]
  ): F[Step]

  /** Each warp of the active lanes synchronises on `memberMask`, which its active lanes must agree on. */
  inline def warpSync(memberMask: Value): F[Step]

  /** The frame-local address of the lane's stack pointer. */
  inline def stackPointer(lane: L): V

  /** `size` bytes aligned to `alignment` allocated on the lane's stack, then `next` over their frame-local address.
    * Faults where the allocation leaves the frame's stack.
    */
  inline def allocate(lane: L, size: V, alignment: V)(inline next: V => F[Unit]): F[Unit]

  /** The lane's stack pointer set to the frame-local address `pointer`, then `next`. Faults where `pointer` lies
    * outside the frame's stack.
    */
  inline def restoreStack(lane: L, pointer: V)(inline next: => F[Unit]): F[Unit]

  /** Pushes the return address `address` on the lane's return stack, then `next`. */
  inline def pushReturn(lane: L, address: V)(inline next: => F[Unit]): F[Unit]

  /** 1 where every lane of the unit that holds `lane` holds the same value in `view`, otherwise 0. */
  inline def agreed(lane: L, view: V): V

  /** The index of the unit that holds `lane` among the units of the workgroup. */
  inline def unitIndex(lane: L): V

  /** `body` where `condition` holds, then `next`. */
  inline def when(condition: V)(inline body: => F[Unit])(inline next: => F[Unit]): F[Unit]

  /** The text of `value` as `text` shows it, for a fault. */
  inline def valueText(text: TensorMemoryLayout.Text, value: V): String

  /** 1 unless the CTA `cta` relinquished its tensor memory allocation permit. */
  inline def tensorMemoryPermitted(cta: C): V

  /** `next` over the CTA of rank `rank` in the cluster. */
  inline def withCta[A](rank: V)(inline next: C => F[A]): F[A]

  /** The tensor memory granules of `cta` that are allocated, a bit each. */
  inline def tensorMemoryTaken(cta: C): V

  /** The allocation permit of `cta` relinquished, then `next`. */
  inline def relinquishTensorMemory(cta: C)(inline next: => F[Step]): F[Step]

  /** `next` over the first column of the lowest free run of `granules` granules of `cta`, aligned to its size and now
    * allocated, or over the column count where there is none.
    */
  inline def allocateTensorMemory(cta: C, granules: V)(inline next: V => F[Unit]): F[Unit]

  /** `next` over 1 where every granule of `mask` was allocated in `cta` and is now free, otherwise over 0. */
  inline def freeTensorMemory(cta: C, mask: V)(inline next: V => F[Unit]): F[Unit]

  /** The 32-bit cell of the tensor memory of `cta` at lane `lane` and column `column`. */
  inline def tensorMemoryCell(cta: C, lane: V, column: V): V

  /** The low 32 bits of `value` stored in the cell of the tensor memory of `cta` at `lane` and `column`, then `next`.
    */
  inline def storeTensorMemory(cta: C, lane: V, column: V, value: V)(inline next: => F[Unit]): F[Unit]

  /** The four words of `data` stored in the cells from `column` on at `lane` in the tensor memory of `cta`, then
    * `next`.
    */
  inline def storeTensorWords(cta: C, lane: V, column: V, data: D)(inline next: => F[Unit]): F[Unit]

  /** The first 31 of 32 rows of 8 cells from `lane` and `column` on in the tensor memory of `cta` moved down by a row,
    * then `next`.
    */
  inline def shiftTensorMemory(cta: C, lane: V, column: V)(inline next: => F[Unit]): F[Unit]

  /** A tcgen05.mma whose instruction descriptor is decoded and whose operands are read from shared and tensor memory.
    */
  type Gathered

  /** `next` over the product the tcgen05.mma `operation` describes on `lane`, once its descriptor decodes to a valid
    * product and its operands lie in shared and tensor memory.
    */
  inline def gatherProduct(lane: L, operation: Tcgen05.Multiply)(inline next: Gathered => F[Unit]): F[Unit]

  /** The number of elements of D in `product`. */
  inline def elementCount(product: Gathered): V

  /** `next` over element `index` of D in `product`, row by row, from its operands. */
  inline def computeElement(product: Gathered, index: V)(inline next: V => F[Unit]): F[Unit]

  /** `value` stored as element `index` of D in `product`, where the datapath layout places it and its lane is not
    * disabled, then `next`; faults where it falls outside tensor memory.
    */
  inline def storeElement(lane: L, product: Gathered, index: V, value: V)(inline next: => F[Unit]): F[Unit]

  /** `body` over the first active lane of each unit with an active lane, then `next`. */
  inline def eachUnit(inline body: L => F[Unit])(inline next: => F[Step]): F[Step]

  /** `value` written to the register `target` on every active lane of the unit that holds `lane`, then `next`. */
  inline def writeUnit(lane: L, target: Register, value: V)(inline next: => F[Unit]): F[Unit]

  /** `value` written to the predicate `target` on every active lane of the unit that holds `lane`, then `next`. */
  inline def writeUnitPredicate(lane: L, target: Predicate, value: V)(inline next: => F[Unit]): F[Unit]

  /** The granules of `cta` that `mask` leaves allocated kept and the rest freed, then `next`. */
  inline def retainTensorMemory(cta: C, mask: V)(inline next: => F[Unit]): F[Unit]

  /** `next` over 1 where the lock on the shared word at `offset` in the window of `cta` is free or held by `holder`,
    * which now holds it, otherwise over 0.
    */
  inline def acquireLock(cta: C, offset: V, holder: V)(inline next: V => F[Unit]): F[Unit]

  /** `next` over 1 where `holder` held the lock on the shared word at `offset` in the window of `cta`, which is now
    * free, otherwise over 0.
    */
  inline def releaseLock(cta: C, offset: V, holder: V)(inline next: V => F[Unit]): F[Unit]

  /** `next` over the 16 bytes at `offset` in the shared window of `cta`, widened as `packing` says. */
  inline def loadChunk(cta: C, offset: V, packing: Matrix.Packing)(inline next: D => F[Unit]): F[Unit]
}
