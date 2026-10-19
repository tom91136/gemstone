package net.kurobako.gemstone.isa.nvidia

import VMIR.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.semantics.render.*
import net.kurobako.gemstone.string.*
import net.kurobako.gemstone.vm.LaunchGeometry.*

/** The architectural state NVIDIA instructions read and write implicitly, and the machine operations rendered semantics
  * perform through the contexts of a lane.
  */
private[isa] object Machine {
  def flag(flag: VMConditionCodes.Flag): String = s"cc.${flag.toString.toLowerCaseRoot}"

  /** The carry flag of `add.cc` and its kin. */
  val carry: String = "CC.CF"

  /** IADD3's carry count. */
  val carries: String = "iadd3.carry"

  val carriesType: Type = Type.Ranged(Type.unsigned(2), Vec((0L, 2L)))

  val memory: String = "memory"

  private val warpContext     = ("net.kurobako.gemstone.isa.nvidia.Warp", "warp")
  private val launchContext   = ("net.kurobako.gemstone.isa.nvidia.Launch", "launch")
  private val memoryContext   = ("net.kurobako.gemstone.isa.nvidia.Memory", "memory")
  private val constantContext = ("net.kurobako.gemstone.isa.nvidia.Constants", "constants")

  private val u32 = Type.unsigned(32)
  private val u64 = Type.unsigned(64)

  private def warp(method: String): Term = Term.context(warpContext, method, Vec.empty, Vec.empty, Some(u32))

  /** A lane's index in its warp, and the number of lanes from its warp's first. */
  val lane: Term      = warp("laneId")
  val warpLanes: Term = warp("lanes")

  def members(mask: Term): Term =
    Term.context(warpContext, "members", Vec.empty, Vec((mask, Some(u32))), Some(u32))

  def participants(mask: Term): Term =
    Term.context(warpContext, "participants", Vec.empty, Vec((mask, Some(u32))), Some(u32))

  def ballot(voter: Term, mask: Term): Term =
    Term.context(warpContext, "ballot", Vec.empty, Vec((voter, Some(Type.Bool)), (mask, Some(u32))), Some(u32))

  def shuffle(operand: Term, tpe: Type, lane: Term): Term =
    Term.context(warpContext, "shuffle", Vec(tpe), Vec((operand, Some(tpe)), (lane, Some(u32))), Some(tpe))

  /** The binary32 operands of the lanes of `members` reduced to their minimum or maximum, ignoring NaN operands unless
    * `propagateNaN`.
    */
  def reduceFloat(maximum: Boolean, propagateNaN: Boolean, value: Term, members: Term): Term = {
    val tpe = Type.Float(32)
    Term.context(
      warpContext,
      s"reduce${if (maximum) "Maximum" else "Minimum"}${if (propagateNaN) "" else "Number"}",
      Vec(tpe),
      Vec((value, Some(tpe)), (members, Some(u32))),
      Some(tpe)
    )
  }

  /** The lanes of `members` whose `value` equals this lane's; under `all`, all of them where they all match and none
    * otherwise.
    */
  def matching(all: Boolean, value: Term, tpe: Type, members: Term): Term =
    Term.context(
      warpContext,
      if (all) "matchAll" else "matchAny",
      Vec(tpe),
      Vec((value, Some(tpe)), (members, Some(u32))),
      Some(u32)
    )

  /** The value of the warp's lowest active lane. */
  def leader(value: Term, tpe: Type): Term =
    Term.context(warpContext, "leader", Vec(tpe), Vec((value, Some(tpe))), Some(tpe))

  /** The operands of the lanes of `members` combined by `operation` at `tpe`, or `None` for one no warp reduces by. */
  def reduce(operation: IntegerOperation, tpe: Type, value: Term, members: Term): Option[Term] = (operation match {
    case IntegerOperation.Add        => Some("reduceAdd")
    case IntegerOperation.BitwiseAnd => Some("reduceAnd")
    case IntegerOperation.BitwiseOr  => Some("reduceOr")
    case IntegerOperation.BitwiseXor => Some("reduceXor")
    case IntegerOperation.Minimum(_) => Some("reduceMinimum")
    case IntegerOperation.Maximum(_) => Some("reduceMaximum")
    case _                           => None
  }).map(method =>
    Term.context(warpContext, method, Vec(tpe), Vec((value, Some(tpe)), (members, Some(u32))), Some(tpe))
  )

  /** `value`, read from a cluster cancellation response whose high word is `high`. */
  def cancelledCta(value: Term, high: Term): Term =
    Term.context(launchContext, "cancelledCta", Vec.empty, Vec((value, Some(u64)), (high, Some(u64))), Some(u64))

  private def launch(method: String): Term = Term.context(launchContext, method, Vec.empty, Vec.empty, Some(u32))

  /** The method of each state space's loads. */
  def loadMethod(access: MemoryAccess): String = "load" + spaceSuffix(access)

  def loadMethod(space: MemorySpace): String = "load" + spaceSuffix(space)

  /** The method of each state space's stores. */
  def storeMethod(access: MemoryAccess): String = "store" + spaceSuffix(access)

  def storeMethod(space: MemorySpace): String = "store" + spaceSuffix(space)

  private def spaceSuffix(access: MemoryAccess): String = access match {
    case MemoryAccess.Global(_)  => "Global"
    case MemoryAccess.Generic(_) => ""
    case MemoryAccess.Shared     => "Shared"
    case MemoryAccess.Local      => "Local"
  }

  private def spaceSuffix(space: MemorySpace): String = space match {
    case MemorySpace.Generic => ""
    case MemorySpace.Global  => "Global"
    case MemorySpace.Shared  => "Shared"
  }

  def load(method: String, tpe: Type, address: Term): Term =
    Term.context(memoryContext, method, Vec(tpe), Vec((address, Some(u64))), Some(tpe))

  def store(method: String, tpe: Type, address: Term, value: Term): Term =
    Term.context(memoryContext, method, Vec(tpe), Vec((address, Some(u64)), (value, Some(tpe))), None, effect = true)

  def loadParameter(parameter: Parameter, tpe: Type, offset: Term): Term = Term.context(
    memoryContext,
    "loadParameter",
    Vec(tpe),
    Vec((Term.Constant(parameter.raw.toString), None), (offset, Some(u64))),
    Some(tpe)
  )

  def storeParameter(parameter: Parameter, tpe: Type, offset: Term, value: Term): Term = Term.context(
    memoryContext,
    "storeParameter",
    Vec(tpe),
    Vec((Term.Constant(parameter.raw.toString), None), (offset, Some(u64)), (value, Some(tpe))),
    None,
    effect = true
  )

  /** A read of `tpe` at `offset` in constant bank `bank`. */
  def constant(tpe: Type, bank: Long, offset: Term): Term = Term.context(
    constantContext,
    "load",
    Vec(tpe),
    Vec((Term.Literal(bank), Some(u32)), (offset, Some(u32))),
    Some(tpe)
  )

  /** The unsigned type of an access of `width` bits. */
  def bits(width: Int in 1 ~ 64): Type = Type.unsigned(width)

  val localBase: Term    = Term.context(memoryContext, "localBase", Vec.empty, Vec.empty, Some(u64))
  val sharedWindow: Term = Term.context(memoryContext, "sharedWindow", Vec.empty, Vec.empty, Some(u64))

  def threadId(axis: Axis): Term     = launch(s"threadId$axis")
  def blockId(axis: Axis): Term      = launch(s"blockId$axis")
  def blockDim(axis: Axis): Term     = launch(s"blockDim$axis")
  def gridDim(axis: Axis): Term      = launch(s"gridDim$axis")
  val dynamicSharedBytes: Term       = launch("dynamicSharedBytes")
  val activeMask: Term               = warp("activeMask")
  val liveMask: Term                 = warp("liveMask")
  val laneMaskGreaterEqual: Term     = warp("laneMaskGreaterEqual")
  val laneMaskGreater: Term          = warp("laneMaskGreater")
  val laneMaskLessEqual: Term        = warp("laneMaskLessEqual")
  val laneMaskLess: Term             = warp("laneMaskLess")
  val laneMaskEqual: Term            = warp("laneMaskEqual")
  val warpId: Term                   = warp("warpId")
  val multiprocessorId: Term         = launch("multiprocessorId")
  val clusterCtaRank: Term           = launch("clusterCtaRank")
  val clusterCtaCount: Term          = launch("clusterCtaCount")
  def clusterCtaId(axis: Axis): Term = launch(s"clusterCtaId$axis")
  def clusterShape(axis: Axis): Term = launch(s"clusterShape$axis")
  def clusterId(axis: Axis): Term    = launch(s"clusterId$axis")
  def clusterCount(axis: Axis): Term = launch(s"clusterCount$axis")
  val explicitCluster: Term          = launch("explicitCluster")
  val clock: Term                    = Term.context(launchContext, "clock", Vec.empty, Vec.empty, Some(u64))
  val globalTimer: Term              = Term.context(launchContext, "globalTimer", Vec.empty, Vec.empty, Some(u64))

  def counter(counter: Term, part: CounterPart): Term = part match {
    case CounterPart.Whole => counter
    case CounterPart.Low   => Term.truncate(counter, 32)
    case CounterPart.High  => Term.truncate(Term.Binary(">>", counter, Term.Literal(32L)), 32)
  }
}
