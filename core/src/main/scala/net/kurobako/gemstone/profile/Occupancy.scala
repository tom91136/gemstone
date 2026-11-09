package net.kurobako.gemstone.profile

import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.vm.*

object Occupancy {

  /** A resource that bounds how many workgroups a multiprocessor holds. */
  enum Limiter derives CanEqual {
    case Subgroups, Workgroups, Registers, Shared
  }

  /** How many of `resources`' workgroups one multiprocessor of `limits` holds, as the CUDA occupancy calculator counts
    * them: registers per scheduler partition in granules, warps and workgroups per multiprocessor, and shared memory in
    * granules after the per-workgroup reservation. A workgroup that does not fit at all bounds it to zero.
    */
  def of(limits: Architecture.Limits, resources: KernelResources): Occupancy = {
    val subgroupSize = resources.subgroupSize.raw.toLong
    val perWorkgroup = resources.workgroupLanes.ceilDiv(resources.subgroupSize).raw.toLong
    val fits         = resources.workgroupLanes.raw <= limits.workgroupLanes.value

    val bySubgroups = if (fits) limits.subgroups.value / perWorkgroup else 0L

    val byRegisters = resources.registersPerLane.fold(Long.MaxValue) { registers =>
      val perSubgroup = (registers.raw.toLong * subgroupSize).alignUp(limits.registerGranule.value.toLong)
      val tooMany     = limits.workgroupRegisters.exists { limit =>
        perSubgroup * perWorkgroup.alignUp(limits.partitions.value.toLong) > limit.value ||
        perSubgroup * perWorkgroup > limit.value
      }
      if (registers.raw > limits.laneRegisters.value || tooMany) 0L
      else if (perSubgroup == 0L) Long.MaxValue
      else limits.partitionRegisters.value / perSubgroup * limits.partitions.value / perWorkgroup
    }

    val reserved  = limits.reservedShared.value.toLong
    val allocated = (resources.sharedBytes.raw + reserved).alignUp(limits.sharedGranule.value.toLong)
    val byShared  =
      if (allocated > limits.workgroupShared.value + reserved) 0L
      else if (allocated == 0L) Long.MaxValue
      else limits.shared.value / allocated

    val barrierCapped = limits.workgroupCap == Architecture.WorkgroupCap.AllWorkgroups || perWorkgroup > 1

    val bounds = Map(
      Limiter.Subgroups  -> bySubgroups,
      Limiter.Workgroups -> (if (barrierCapped) limits.workgroups.value.toLong else Long.MaxValue),
      Limiter.Registers  -> byRegisters,
      Limiter.Shared     -> byShared
    )
    val workgroups = bounds.values.min
    Occupancy(
      workgroups.toInt,
      (workgroups * perWorkgroup).toInt,
      limits.subgroups.value,
      bounds.collect { case (limiter, bound) if bound != Long.MaxValue => limiter -> bound.toInt }
    )
  }
}

/** How many workgroups and subgroups a multiprocessor holds at once, out of the `maxSubgroups` it could, and what each
  * resource alone would allow.
  */
case class Occupancy(workgroups: Int, subgroups: Int, maxSubgroups: Int, bounds: Map[Occupancy.Limiter, Int])
    derives CanEqual {

  def fraction: Double = subgroups.toDouble / maxSubgroups.toDouble

  /** The resources that bound the workgroup count. */
  def limiters: Set[Occupancy.Limiter] =
    bounds.collect { case (limiter, bound) if bound == workgroups => limiter }.toSet
}
