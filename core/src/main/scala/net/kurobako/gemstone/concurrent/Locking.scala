package net.kurobako.gemstone.concurrent

import java.util.concurrent.locks.Lock

extension (lock: Lock) {
  private[gemstone] inline def withLock[A](body: => A): A = {
    lock.lock()
    try body
    finally lock.unlock()
  }
}
