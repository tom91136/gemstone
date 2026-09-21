package net.kurobako.gemstone.rpc

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

type ProgramId = ProgramId.T
object ProgramId extends RefinedType[Long, Positive] {
  extension (id: ProgramId) def next: ProgramId = ProgramId.applyUnsafe(Math.addExact(id.value, 1L))
}
