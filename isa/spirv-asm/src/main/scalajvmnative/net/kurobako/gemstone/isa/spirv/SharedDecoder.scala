package net.kurobako.gemstone.isa.spirv

import java.lang.ref.ReferenceQueue
import java.lang.ref.WeakReference
import java.util.concurrent.locks.ReentrantLock
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.concurrent.*
import net.kurobako.gemstone.isa.*
import scala.annotation.tailrec
import scala.collection.mutable

/** Share immutable instruction graphs between live modules, including specialized variants. The pool owns only weak
  * references to instructions; compilation and execution state remain private to each loaded program.
  */
class SharedDecoder {
  private val lock         = ReentrantLock()
  private val released     = ReferenceQueue[Instruction]()
  private val instructions = mutable.HashMap.empty[Int, List[Entry]]

  // Use explicit references: WeakHashMap does not provide weak keys on all supported runtimes.
  private class Entry(instruction: Instruction, val hash: Int)
      extends WeakReference[Instruction](instruction, released)

  @tailrec private def reclaim(): Unit = Option(released.poll()) match {
    case None               => ()
    case Some(entry: Entry) =>
      instructions.get(entry.hash).foreach { bucket =>
        val remaining = bucket.filterNot(_ eq entry)
        if (remaining.isEmpty) {
          val _ = instructions.remove(entry.hash)
        } else instructions.update(entry.hash, remaining)
      }
      reclaim()
    case Some(_) => reclaim()
  }

  private def intern(instruction: Instruction): Instruction = {
    val hash   = instruction.hashCode()
    val bucket = instructions.getOrElse(hash, Nil)
    bucket.view.flatMap(reference => Option(reference.get())).find(_ == instruction) match {
      case Some(shared) => shared
      case None         =>
        instructions.update(hash, Entry(instruction, hash) :: bucket)
        instruction
    }
  }

  def decode(bytes: Vec[Byte]): Either[ISA.DecodeError, Module] = ISA.decode(bytes).map { module =>
    lock.withLock {
      reclaim()
      module.copy(instructions = module.instructions.map(intern))
    }
  }
}
