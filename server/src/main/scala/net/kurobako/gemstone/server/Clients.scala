package net.kurobako.gemstone.server

import java.util.concurrent.locks.ReentrantLock
import net.kurobako.gemstone.*
import net.kurobako.gemstone.concurrent.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.rpc.*
import net.kurobako.gemstone.server.protocol.*
import net.kurobako.gemstone.vm.*
import scala.collection.mutable

object Clients {
  val Root: ClientId = ClientId(1L)
}

/** A single lock protects registration, session membership and leases. Cleanup always runs outside it. */
class Clients(createSession: () => ExecutionSession, clientClosed: AnyRef => Unit = _ => ()) extends AutoCloseable {
  private class Entry(val service: ExecutionSession) {
    @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
    var references: Int >= 0 = 1
  }
  private class Client {
    val sessions: scala.collection.mutable.Map[SessionId.Type, Entry] = mutable.Map.empty[SessionId, Entry]
    @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
    var usage: Long >= 0 = 0L
  }
  private val lock    = ReentrantLock()
  private val clients = mutable.Map(Clients.Root -> Client())
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private var next: Long > 0 = 2L
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private var closed = false

  class Lease private[Clients] (id: ClientId, session: SessionId, client: Client, entry: Entry) extends AutoCloseable {
    @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
    private var released = false

    def service: Either[ExecutionFailure, ExecutionSession] = lock.withLock {
      if (released || !clients.get(id).contains(client) || !client.sessions.get(session).contains(entry))
        Left(ExecutionFailure.NotFound("process session was closed"))
      else Right(entry.service)
    }

    override def close(): Unit = {
      val detached = lock.withLock {
        if (released) None
        else {
          released = true
          if (!client.sessions.get(session).contains(entry)) None
          else {
            entry.references -= 1
            if (entry.references.raw == 0) {
              val _ = client.sessions.remove(session)
              Some(entry.service)
            } else None
          }
        }
      }
      detached.foreach(_.close())
    }
  }

  def register(): ClientId = lock.withLock {
    checkOpen()
    val id = ClientId(next)
    next = next + 1L
    clients.update(id, Client())
    id
  }

  def acquire(id: ClientId, session: SessionId): Either[ExecutionFailure, Lease] = lock.withLock {
    clients.get(id).toRight(ExecutionFailure.NotFound("client session was closed")).map { client =>
      val entry = client.sessions.get(session) match {
        case Some(entry) => entry.references = entry.references + 1; entry
        case None        =>
          val entry = Entry(createSession())
          client.sessions.update(session, entry)
          entry
      }
      Lease(id, session, client, entry)
    }
  }

  def accept(id: ClientId): Either[ExecutionFailure, AnyRef] = lock.withLock {
    clients.get(id).toRight(ExecutionFailure.NotFound("client session was closed")).map { client =>
      client.usage = client.usage + 1L
      client
    }
  }
  def usage(id: ClientId): Long >= 0 = lock.withLock(clients.get(id).fold[Long >= 0](0L)(_.usage))

  private def checkOpen(): Unit = require(!closed, "server clients are closed")

  // Detach every session before attempting cleanup. Include the client notification even if a session fails.
  private def detach(client: Client): List[() => Unit] = {
    val sessions = client.sessions.values.toList
    client.sessions.clear()
    sessions.map(entry => () => entry.service.close()) :+ (() => clientClosed(client))
  }

  def release(id: ClientId): Unit = {
    val actions = lock.withLock {
      if (id == Clients.Root) Nil else clients.remove(id).toList.flatMap(detach(_))
    }
    throwables.closeAll(actions)
  }

  override def close(): Unit = {
    val actions = lock.withLock {
      closed = true
      val actions = clients.values.toList.flatMap(detach(_))
      clients.clear()
      actions
    }
    throwables.closeAll(actions)
  }
}
