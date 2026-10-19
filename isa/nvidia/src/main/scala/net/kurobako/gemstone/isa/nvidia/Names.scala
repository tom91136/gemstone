package net.kurobako.gemstone.isa.nvidia

import VMIR.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.semantics.render.*
import scala.collection.mutable

/** The names rendered NVIDIA semantics use. Registers and predicates take the name of what they stand for, such as the
  * source register or instruction operand; the rest are temporaries `tN`, numbered in the order they first appear. Each
  * name that stands for an operand or for state is declared with its type, in the order it first appears.
  */
private[isa] class Names(naming: SlotNaming) extends Scope[Slot](naming) {
  private val registerTemporaries  = mutable.HashMap.empty[Register, String]
  private val predicateTemporaries = mutable.HashMap.empty[Predicate, String]

  def register(slot: Register): String =
    operand(Slot.Gpr(slot)).fold(registerTemporaries.getOrElseUpdate(slot, temporary()))(_.name)

  /** The predicate written; a constant one discards what it is written. */
  def predicate(slot: Predicate): String = naming.constant(slot) match {
    case Some(_) => temporary()
    case None    =>
      operand(Slot.Pred(slot)) match {
        case Some(named) => named.name
        case None        => predicateTemporaries.getOrElseUpdate(slot, temporary())
      }
  }

  def read(slot: Predicate): Term = naming.constant(slot) match {
    case Some(value) => Term.Literal(if (value) 1L else 0L)
    case None        => Term.Variable(predicate(slot))
  }

  /** The type of `value` where it has one, such as a register operand's; 64-bit for the rest. */
  def tpe(value: Value): Type = value match {
    case Value.Register(slot) => naming.resolve(Slot.Gpr(slot)).headOption.fold(Type.unsigned(64))(_.tpe.unranged)
    case Value.Predicate(_)   => Type.Bool
    case Value.Immediate(_) | Value.Address(_, _) | Value.LocalBase | Value.SharedWindow => Type.unsigned(64)
    case Value.Clock(CounterPart.Whole) | Value.GlobalTimer(CounterPart.Whole)           => Type.unsigned(64)
    case _                                                                               => Type.unsigned(32)
  }

  /** Immediates as literals, addresses as their base plus offset and special registers as reads of the lane's contexts.
    */
  def value(value: Value): Term = value match {
    case Value.Register(slot)        => Term.Variable(register(slot))
    case Value.Predicate(slot)       => read(slot)
    case Value.Immediate(immediate)  => Term.Literal(immediate)
    case Value.Address(base, 0L)     => this.value(base)
    case Value.Address(base, offset) => Term.Binary("+", this.value(base), Term.Literal(offset))
    case Value.LocalBase             => Machine.localBase
    case Value.SharedWindow          => Machine.sharedWindow
    case Value.LaneId                => Machine.lane
    case Value.DynamicSharedBytes    => Machine.dynamicSharedBytes
    case Value.ActiveMask            => Machine.activeMask
    case Value.LiveMask              => Machine.liveMask
    case Value.LaneMaskGreaterEqual  => Machine.laneMaskGreaterEqual
    case Value.LaneMaskGreater       => Machine.laneMaskGreater
    case Value.LaneMaskLessEqual     => Machine.laneMaskLessEqual
    case Value.LaneMaskLess          => Machine.laneMaskLess
    case Value.LaneMaskEqual         => Machine.laneMaskEqual
    case Value.ThreadId(axis)        => Machine.threadId(axis)
    case Value.BlockId(axis)         => Machine.blockId(axis)
    case Value.BlockDim(axis)        => Machine.blockDim(axis)
    case Value.GridDim(axis)         => Machine.gridDim(axis)
    case Value.Clock(part)           => Machine.counter(Machine.clock, part)
    case Value.GlobalTimer(part)     => Machine.counter(Machine.globalTimer, part)
    case Value.MultiprocessorId      => Machine.multiprocessorId
    case Value.WarpId                => Machine.warpId
    case Value.ClusterCtaRank        => Machine.clusterCtaRank
    case Value.ClusterCtaCount       => Machine.clusterCtaCount
    case Value.ClusterCtaId(axis)    => Machine.clusterCtaId(axis)
    case Value.ClusterShape(axis)    => Machine.clusterShape(axis)
    case Value.ClusterId(axis)       => Machine.clusterId(axis)
    case Value.ClusterCount(axis)    => Machine.clusterCount(axis)
    case Value.ExplicitCluster       => Machine.explicitCluster
  }

  /** A load at `address` as a read of the constant bank, where it reads a constant operand. */
  def constant(address: Value, tpe: Type): Option[Term] = address match {
    case Value.Immediate(immediate) =>
      naming.constantBank(immediate).map { (bank, field, offset) =>
        Machine.constant(
          tpe,
          bank,
          field.fold(Term.Literal(offset)) { (name, declared) =>
            val base = Term.Variable(declare(name, declared))
            if (offset == 0L) base else Term.Binary("+", base, Term.Literal(offset))
          }
        )
      }
    case _ => None
  }
}
