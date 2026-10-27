package net.kurobako.gemstone.isa.spirv

import VMMemory.*
import net.kurobako.gemstone.semantics.*
import net.kurobako.gemstone.semantics.prelude.*

/** How an address names its storage class, as generic casts read it. */
private[isa] object Addresses {
  val castable: Set[Int] = Set(StorageClass.Workgroup, StorageClass.CrossWorkgroup, StorageClass.Function)

  /** Whether a cast from Generic to `storageClass` keeps `address`: it must lie in that class, and be non-null unless
    * the cast names the class.
    */
  def keeps(address: Long, storageClass: Int, explicit: Boolean): Boolean = {
    val named = storageClassOf(address) == storageClass
    if (explicit) address == 0L || named else address != 0L && named
  }

  private def retainedOf(address: Long, storageClass: Long, explicit: Long): Long =
    if (keeps(address, storageClass.toInt, explicit != 0L)) address else 0L

  val Retained: Primitive[Primitive.Ternary] =
    Primitive("net.kurobako.gemstone.isa.spirv.Addresses.retained", retainedOf(_, _, _))

  def retained(address: UInt[64], storageClass: UInt[64], explicit: UInt[64]): UInt[64] =
    fromRaw[UInt[64]](retainedOf(toRaw(address), toRaw(storageClass), toRaw(explicit)))
}
