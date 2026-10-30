package net.kurobako.gemstone.toolchain

import java.lang.ref.WeakReference
import java.util.concurrent.atomic.AtomicBoolean
import scala.scalanative.meta.LinktimeInfo
import scala.scalanative.unsafe.*

@extern private object collector {
  @name("scalanative_GC_set_weak_references_collected_callback")
  def setWeakReferencesCollectedCallback(callback: CFuncPtr0[Unit]): Unit = extern

  // the callback WeakReferenceRegistry registers: it starts the handler thread on first use, then wakes it
  @name("_SM46java.lang.ref.WeakReferenceRegistry$$$Lambda$1G17$extern$forwarder")
  def wakeReferenceHandler(): Unit = extern
}

/** Scala Native 0.5.12 wakes its weak reference handler from whichever thread collected, or under Boehm from whichever
  * thread runs finalizers. `park` and `unpark` allocate while holding the handler's lock, so a thread inside either can
  * re-enter `unpark` and deadlock on that lock; under Boehm the wake-up can also run on a thread that is still starting
  * and abort. Installing this replaces the wake-up with a flag, and a relay thread makes the wake-up instead.
  */
private[toolchain] object ReferenceHandlerRelay {
  private val pending = AtomicBoolean()

  private lazy val installed: Unit =
    if (LinktimeInfo.isWeakReferenceSupported && LinktimeInfo.isMultithreadingEnabled) {
      // registers WeakReferenceRegistry's callback, so the one below replaces it rather than being replaced
      val _ = WeakReference(Object())
      collector.setWeakReferencesCollectedCallback(CFuncPtr0.fromScalaFunction(() => pending.set(true)))
      // the relay polls because parking it would deadlock the same way
      val relay = Thread(() =>
        while (true) {
          if (pending.getAndSet(false)) collector.wakeReferenceHandler()
          Thread.sleep(10)
        }
      )
      relay.setName("gemstone-reference-relay")
      relay.setDaemon(true)
      relay.start()
    }

  def install(): Unit = installed
}
