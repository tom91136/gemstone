package net.kurobako.gemstone.isa.nvidia

import net.kurobako.gemstone.semantics.prelude.*
import net.kurobako.gemstone.semantics.render.*
import net.kurobako.gemstone.vm.*

/** The CTAs of a cluster, which rendered machine definitions address by the shared addresses that name them. */
// rendered definitions compile against this prelude trait
@SuppressWarnings(Array("baumkuchen:data-clump"))
private[isa] trait Cluster {
  type Cta

  def shared(address: UInt[64], bytes: UInt[64]): Bool
  def owner(address: UInt[64]): Cta
  def offset(address: UInt[64]): UInt[64]
  def destinations(mask: UInt[64]): Iterable[Cta]
  def clusterSize(): UInt[64]
  def ctaRank(): UInt[64]
  def ranks(): Iterable[UInt[64]]
  def rankOf(address: UInt[64]): UInt[64]
  def clusterAddress(address: UInt[64], rank: UInt[64]): UInt[64]
  def inCluster(address: UInt[64], bytes: UInt[64]): Bool
  def same(first: Cta, second: Cta): Bool
  def clear(cta: Cta, offset: UInt[64], bytes: UInt[64]): Unit
  def store(cta: Cta, offset: UInt[64], data: Array[Byte]): Unit
  def storeShared[T](address: UInt[64], value: T)(using Type.Reified[T]): Unit

  /** `data` at the shared address `address`, with zeros past its end up to `size` bytes. */
  def storeSharedBytes(address: UInt[64], data: Array[Byte], size: UInt[64]): Unit
  def storeWord[T](cta: Cta, offset: UInt[64], value: T)(using Type.Reified[T]): Unit
  def reduceWord[T](cta: Cta, offset: UInt[64], operand: T, operation: AtomicOperation)(using Type.Reified[T]): Unit
  def reduceBytes(
      cta: Cta,
      offset: UInt[64],
      data: Array[Byte],
      operator: TensorMap.Reduction,
      element: TensorMap.Element
  ): Unit
  def completeTx(cta: Cta, barrier: UInt[64], bytes: UInt[64]): Unit

  /** The rank of the CTA of the pair of `cta` whose rank has the parity of `signalled`'s. */
  def peerRank(cta: Cta, signalled: Cta): UInt[64]
  def present(rank: UInt[64]): Bool
  def at(rank: UInt[64]): Cta

  /** The `count` bytes at `at` in the box of `transfer` stored in, or read from, the shared window of `cta` at
    * `offset`.
    */
  def placeChunk(cta: Cta, offset: UInt[64], transfer: Array[Byte], at: UInt[64], count: UInt[64]): Unit
  def fetchChunk(cta: Cta, offset: UInt[64], transfer: Array[Byte], at: UInt[64], count: UInt[64]): Unit
  def barrier(arrive: Boolean): Unit

  /** Whether `cta` may still allocate tensor memory, and the granules of its tensor memory that are allocated. */
  def tensorMemoryPermitted(cta: Cta): Bool
  def tensorMemoryTaken(cta: Cta): UInt[64]
  def relinquishTensorMemory(cta: Cta): Unit

  /** The first column of the granules of tensor memory allocated, or the column count where there are none free. */
  def allocateTensorMemory(cta: Cta, granules: UInt[64]): UInt[64]

  /** Whether every granule of `mask` was allocated, which are now free. */
  def freeTensorMemory(cta: Cta, mask: UInt[64]): Bool
  def tensorMemoryCell(cta: Cta, lane: UInt[64], column: UInt[64]): UInt[64]
  def storeTensorMemory(cta: Cta, lane: UInt[64], column: UInt[64], value: UInt[64]): Unit
  def storeTensorWords(cta: Cta, lane: UInt[64], column: UInt[64], data: Array[Byte]): Unit

  /** The granules of `cta` that `mask` leaves allocated kept and the rest freed. */
  def retainTensorMemory(cta: Cta, mask: UInt[64]): Unit

  /** Whether the lock on the shared word at `offset` of `cta` is free or held by `holder`, which now holds it. */
  def acquireLock(cta: Cta, offset: UInt[64], holder: UInt[64]): Bool

  /** Whether `holder` held the lock on the shared word at `offset` of `cta`, which is now free. */
  def releaseLock(cta: Cta, offset: UInt[64], holder: UInt[64]): Bool

  /** The rows of `rows` by `columns` cells but the last moved down by one. */
  def shiftTensorMemory(cta: Cta, lane: UInt[64], column: UInt[64], rows: Int, columns: Int): Unit

  /** The 16 bytes at `offset` in the shared window of `cta`, widened as `packing` says. */
  def loadChunk(cta: Cta, offset: UInt[64], packing: VMIR.Matrix.Packing): Array[Byte]
}
