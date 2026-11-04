package net.kurobako.gemstone.isa.amdgpu

import VMDispatchMemory.*
import java.util.concurrent.ConcurrentHashMap
import net.kurobako.gemstone.vm.*

/** A dispatch's code: the dispatched program, and the programs its waves have called into. */
private class CodeSlices(val origin: CodeSlice, peers: PeerCode, mappings: Array[GlobalMapping]) {
  private val resolved = ConcurrentHashMap[Long, Either[Fault, Option[CodeSlice]]]()

  private def resolve(id: ProgramId): Either[Fault, Option[CodeSlice]] =
    peers.resolve(id, origin.program.source).map(_.map { peer =>
      CodeSlice(peer.kernel, peer.kernel.jumpTable, origin.memory.inCode(peer.kernel.source, peer.mappings ++ mappings))
    })

  /** The code holding `address`. */
  def at(address: Long): Either[Fault, Option[CodeSlice]] =
    if (origin.contains(address)) Right(Some(origin))
    else CodeWindows.program(address) match {
      case None     => Right(None)
      case Some(id) => resolved.computeIfAbsent(id.raw, _ => resolve(id)).map(_.filter(_.contains(address)))
    }
}
