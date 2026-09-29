package net.kurobako.gemstone

import java.util.concurrent.locks.Lock

object concurrent {
  extension (lock: Lock) {
    inline def withLock[A](body: => A): A = {
      lock.lock()
      try body
      finally lock.unlock()
    }
  }
}
