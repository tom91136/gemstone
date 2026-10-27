package net.kurobako.gemstone.isa.amdgpu.domain

import net.kurobako.gemstone.isa.amdgpu.*

/** The typed face of one generated operand domain: values convert to and from the codes of each ISA family. */
trait Domain[V <: DomainValue] {
  def id: String

  def of(slot: Slot): Option[V]

  /** The domain on `isa`; empty when the family lacks it. */
  final def table(isa: ISA): DomainTable =
    DomainCatalogue.table(isa, id).getOrElse(DomainTable(id, Vector.empty, Vector.empty))

  final def encode(isa: ISA, value: V): Option[BigInt] = table(isa).encode(value.slot)

  final def decode(isa: ISA, code: Long): Option[V] = table(isa).decode(code).flatMap(of(_))

  /** Every value of `isa`; an immediate contributes its two bounds. */
  final def values(isa: ISA): Vector[V] = table(isa).slots.flatMap(of(_))
}
