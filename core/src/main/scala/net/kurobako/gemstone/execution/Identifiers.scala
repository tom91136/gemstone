package net.kurobako.gemstone.execution

import net.kurobako.gemstone.NoNul
import net.kurobako.gemstone.rpc.Wire.Codec

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

type AllocationId = AllocationId.T
object AllocationId extends RefinedType[Long, Positive] {
  extension (id: AllocationId) def next: AllocationId = AllocationId.applyUnsafe(Math.addExact(id.value, 1L))
}

type ProgramId = ProgramId.T
object ProgramId extends RefinedType[Long, Positive] {
  extension (id: ProgramId) def next: ProgramId = ProgramId.applyUnsafe(Math.addExact(id.value, 1L))
}

type CompletionId = CompletionId.T
object CompletionId extends RefinedType[Long, Positive] {
  extension (id: CompletionId) def next: CompletionId = CompletionId.applyUnsafe(Math.addExact(id.value, 1L))
}

type KernelName = KernelName.T
object KernelName extends RefinedType[String, MinLength[1] & NoNul]

type GlobalName = GlobalName.T
object GlobalName extends RefinedType[String, MinLength[1] & NoNul]

type LinkageName = LinkageName.T
object LinkageName extends RefinedType[String, MinLength[1] & NoNul]

enum ProgramFormat derives Codec {
  case PTXText
  case HSACodeObjectBinary
  case SPIRVBinary
}
