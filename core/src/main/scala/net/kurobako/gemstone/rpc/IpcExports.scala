package net.kurobako.gemstone.rpc

import java.util.concurrent.locks.ReentrantLock
import net.kurobako.gemstone.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.concurrent.*
import net.kurobako.gemstone.rpc.ExecutionRPC.*
import scala.collection.mutable

/** Allocations exported for other sessions to open. A handle is 64 bytes: a magic, the export id, the size and a token
  * keyed by the epoch, so handles from another server run or a forged id are refused. An export lives until its owner
  * frees the allocation or closes its session; imports keep the storage alive past that.
  */
class IpcExports(epoch: Long = scala.util.Random.nextLong()) {
  private class Record(val id: Long, val storage: StoredAllocation.Storage)

  private val lock    = ReentrantLock()
  private val records = mutable.Map.empty[Long, Record]
  private val ids     = java.util.IdentityHashMap[StoredAllocation.Storage, Record]()
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private var next = 1L

  private def mix(value: Long): Long = {
    val a = value + 0x9e3779b97f4a7c15L
    val b = (a ^ (a >>> 30)) * 0xbf58476d1ce4e5b9L
    val c = (b ^ (b >>> 27)) * 0x94d049bb133111ebL
    c ^ (c >>> 31)
  }

  private def token(id: Long, size: Long): Long = mix(mix(epoch ^ id) ^ size)

  private def put(bytes: Array[Byte], at: Int, value: Long): Unit =
    for (index <- 0 until 8) bytes(at + index) = (value >>> (8 * index)).toByte

  private def get(bytes: Vec[Byte], at: Int): Long = (0 until 8).foldLeft(0L)((acc, index) =>
    acc | ((bytes(at + index) & 0xffL) << (8 * index))
  )

  private[rpc] def exported(storage: StoredAllocation.Storage): Vec[Byte] = {
    val record = lock.withLock {
      Option(ids.get(storage)).getOrElse {
        val created = Record(next, storage)
        next += 1L
        records.update(created.id, created)
        ids.put(storage, created)
        created
      }
    }
    val bytes = Array.ofDim[Byte](IpcExports.HandleBytes)
    put(bytes, 0, IpcExports.Magic)
    put(bytes, 8, record.id)
    put(bytes, 16, storage.size)
    put(bytes, 24, token(record.id, storage.size))
    Vec.unsafeWrapArray(bytes)
  }

  private[rpc] def revoke(storage: StoredAllocation.Storage): Unit = lock.withLock {
    Option(ids.remove(storage)).foreach(record => records.remove(record.id))
  }

  /** The storage a handle names, retained for the caller. */
  private[rpc] def opened(handle: Vec[Byte]): Either[ExecutionFailure, StoredAllocation.Storage] =
    if (handle.length != IpcExports.HandleBytes || get(handle, 0) != IpcExports.Magic)
      Left(ExecutionFailure.InvalidRequest("malformed IPC memory handle"))
    else
      lock.withLock {
        records.get(get(handle, 8)).filter { record =>
          get(handle, 16) == (record.storage.size: Long) && get(handle, 24) == token(record.id, record.storage.size)
        } match {
          case Some(record) => record.storage.retain(); Right(record.storage)
          case None         => Left(ExecutionFailure.NotFound("IPC memory handle is not valid"))
        }
      }
}

object IpcExports {
  val HandleBytes: Int = 64
  private val Magic    = 0x31304350494d4547L
}
