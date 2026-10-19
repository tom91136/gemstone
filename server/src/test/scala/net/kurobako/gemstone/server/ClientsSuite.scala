package net.kurobako.gemstone.server

import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import net.kurobako.gemstone.rpc.*
import net.kurobako.gemstone.server.protocol.*
import scala.util.Using

class ClientsSuite extends munit.FunSuite {
  test("released clients revoke existing leases and reject delayed acquisition") {
    Using.resource(Clients(() => ExecutionSession(Nil))) { clients =>
      val id       = clients.register()
      val existing = clients.acquire(id, SessionId(process = 1, incarnation = 1)).toOption.get
      assert(existing.service.isRight)
      clients.release(id)
      assert(existing.service.isLeft)
      assert(clients.acquire(id, SessionId(process = 2, incarnation = 1)).isLeft)
      // closing the Clients revokes every lease
      existing.close(): @scala.annotation.nowarn("msg=baumkuchen:unguarded-close")
    }
  }

  test("connections share a process session until its last lease closes") {
    Using.resource(Clients(() => ExecutionSession(Nil))) { clients =>
      val first   = clients.acquire(Clients.Root, SessionId(process = 1, incarnation = 1)).toOption.get
      val second  = clients.acquire(Clients.Root, SessionId(process = 1, incarnation = 1)).toOption.get
      val service = first.service.toOption.get
      assert(second.service.toOption.get eq service)
      // closing the Clients revokes every lease
      first.close(): @scala.annotation.nowarn("msg=baumkuchen:unguarded-close")
      first.close()
      assert(first.service.isLeft)
      assert(second.service.toOption.get eq service)
      second.close(): @scala.annotation.nowarn("msg=baumkuchen:unguarded-close")
      val next = clients.acquire(Clients.Root, SessionId(process = 1, incarnation = 1)).toOption.get
      assert(!(next.service.toOption.get eq service))
      next.close(): @scala.annotation.nowarn("msg=baumkuchen:unguarded-close")
    }
  }

  test("failure notifying one closed client does not skip other clients or leave leases live") {
    val notifications = java.util.concurrent.atomic.AtomicInteger()
    val clients       = Clients(
      () => ExecutionSession(Nil),
      _ => {
        notifications.incrementAndGet()
        throw IllegalStateException("notification failed")
      }
    )
    val ids     = List(Clients.Root, clients.register(), clients.register())
    val leases  = ids.map(id => clients.acquire(id, SessionId(process = 1, incarnation = 1)).toOption.get)
    val failure = intercept[IllegalStateException](clients.close())
    assertEquals(notifications.get(), 3)
    assertEquals(failure.getSuppressed.length, 2)
    assert(leases.forall(_.service.isLeft))
    assert(ids.forall(id => clients.acquire(id, SessionId(process = 2, incarnation = 1)).isLeft))
    leases.foreach(_.close())
    clients.close()
  }

  test("a slow client cleanup does not hold the registry lock") {
    val entered = CountDownLatch(1)
    val finish  = CountDownLatch(1)
    val clients = Clients(
      () => ExecutionSession(Nil),
      _ => {
        entered.countDown()
        if (!finish.await(5, TimeUnit.SECONDS)) throw IllegalStateException("cleanup was not released")
      }
    )
    val id      = clients.register()
    val cleanup = Thread(() => clients.release(id))
    cleanup.start()
    try {
      assert(entered.await(2, TimeUnit.SECONDS))
      val acquired = java.util.concurrent.atomic.AtomicBoolean()
      val other    = Thread { () =>
        val _ = clients.register(); acquired.set(true)
      }
      other.start()
      other.join(1000)
      assert(acquired.get(), "registry access waited for resource cleanup")
    } finally {
      finish.countDown()
      cleanup.join(5000)
      clients.close()
    }
  }
}
