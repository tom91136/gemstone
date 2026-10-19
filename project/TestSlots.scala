import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Semaphore

// A Scala.js or Scala Native test project keeps a node or binary process alive from loading its frameworks until the
// adapter that owns it closes, which the plugins do only when a whole evaluation completes. Loading every project at
// once holds one process per project, so a project takes a slot when it loads, owns its adapter, and gives the slot
// back after closing it.
object TestSlots {

  val native = new Semaphore(1)
  val js     = new Semaphore(1)

  private val held     = new ConcurrentHashMap[String, Semaphore]()
  private val adapters = new ConcurrentHashMap[String, () => Unit]()

  def acquire(owner: String, slots: Semaphore): Unit = if (!held.containsKey(owner)) {
    slots.acquire()
    held.put(owner, slots)
  }

  def adopt(owner: String, close: () => Unit): Unit = Option(adapters.put(owner, close)).foreach(_())

  def release(owner: String): Unit =
    try Option(adapters.remove(owner)).foreach(_())
    finally Option(held.remove(owner)).foreach(_.release())

  def releaseAll(): Unit = {
    adapters.keySet.forEach(release(_))
    held.keySet.forEach(release(_))
  }
}
