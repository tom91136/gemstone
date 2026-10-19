package net.kurobako.gemstone.isa.nvidia

import net.kurobako.gemstone.semantics.prelude.*

/** The launch a lane belongs to: its thread's place in its block, and the block's in the grid. */
private[isa] trait Launch {
  def threadIdX(): UInt[32]
  def threadIdY(): UInt[32]
  def threadIdZ(): UInt[32]
  def blockIdX(): UInt[32]
  def blockIdY(): UInt[32]
  def blockIdZ(): UInt[32]
  def blockDimX(): UInt[32]
  def blockDimY(): UInt[32]
  def blockDimZ(): UInt[32]
  def gridDimX(): UInt[32]
  def gridDimY(): UInt[32]
  def gridDimZ(): UInt[32]
  def dynamicSharedBytes(): UInt[32]
  def multiprocessorId(): UInt[32]

  /** The block's rank in its cluster, its cluster's block count and the block's place in the cluster. */
  def clusterCtaRank(): UInt[32]
  def clusterCtaCount(): UInt[32]
  def clusterCtaIdX(): UInt[32]
  def clusterCtaIdY(): UInt[32]
  def clusterCtaIdZ(): UInt[32]
  def clusterShapeX(): UInt[32]
  def clusterShapeY(): UInt[32]
  def clusterShapeZ(): UInt[32]

  /** The cluster's place in the grid of clusters. */
  def clusterIdX(): UInt[32]
  def clusterIdY(): UInt[32]
  def clusterIdZ(): UInt[32]
  def clusterCountX(): UInt[32]
  def clusterCountY(): UInt[32]
  def clusterCountZ(): UInt[32]

  /** 1 where the launch named a cluster shape, otherwise 0. */
  def explicitCluster(): UInt[32]

  /** `value`, read from a cluster cancellation response whose high word is `high`; the response must report success. */
  def cancelledCta(value: UInt[64], high: UInt[64]): UInt[64]

  /** The multiprocessor's cycle counter. */
  def clock(): UInt[64]

  /** A nanosecond timer every multiprocessor shares. */
  def globalTimer(): UInt[64]
}
