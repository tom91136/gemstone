package net.kurobako.gemstone.rpc

import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.locks.ReentrantLock
import net.kurobako.gemstone.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.rpc.ExecutionRPC.*
import scala.collection.mutable

/** Sparse host bytes shared by aliases within one dispatch. Pages are spread over 64 shards, each behind its own lock,
  * so lanes working on different pages do not contend. An operation takes the locks of the shards it touches in
  * ascending order; one that fetches ahead, or looks at every page, takes them all. A read that misses requests the
  * invalid bytes of every page it touches, narrowed to the registered allocation holding its address. A miss that
  * continues a sequential scan requests a window that doubles per step, up to the batch budget, so a scan costs a round
  * trip per window instead of per page. The client may reply with less than was requested (it stops at memory it cannot
  * read): bytes a read was not waiting on are forgotten, and bytes it was waiting on become unreadable.
  */
private class HostMemoryMirror(maxBatchBytes: Int >= 8 = 1048576, maxBatchRanges: Int > 0 = 1024) {
  private val pageSize = 4096

  private def number(address: Long): Long = address >>> 12
  private def offset(address: Long): Int  = (address & 4095L).toInt

  // validity is byte-exact so that a fetch never replaces a byte another lane has written
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private class Page {
    // whether any store has recorded an atomic width, which plain stores must then clear
    var atomics                  = false
    val bytes: Array[Byte]       = Array.ofDim[Byte](pageSize)
    val valid: java.util.BitSet  = java.util.BitSet(pageSize)
    val words1: java.util.BitSet = java.util.BitSet(pageSize)
    val words2: java.util.BitSet = java.util.BitSet(pageSize / 2)
    val words4: java.util.BitSet = java.util.BitSet(pageSize / 4)
    val words8: java.util.BitSet = java.util.BitSet(pageSize / 8)

    def wordsOf(width: Int): java.util.BitSet = width match {
      case 8 => words8
      case 4 => words4
      case 2 => words2
      case _ => words1
    }

    def atomicWidth(offset: Int): Int in (0, 1, 2, 4, 8) =
      if (words8.get(offset / 8)) 8
      else if (words4.get(offset / 4)) 4
      else if (words2.get(offset / 2)) 2
      else if (words1.get(offset)) 1
      else 0
  }

  private class Addresses {
    val pages: mutable.LongMap[java.util.BitSet] = mutable.LongMap.empty[java.util.BitSet]
    // page numbers in ascending order; only removals leave it valid, and then it may name pages that are gone
    @SuppressWarnings(Array("scalafix:DisableSyntax.var", "scalafix:DisableSyntax.null"))
    private var ordered: Array[Long] | Null = null
    @SuppressWarnings(Array("scalafix:DisableSyntax.null"))
    def sortedPages(): Array[Long] = {
      val cached = ordered
      if (cached != null && cached.length <= 2 * pages.size + 16) cached
      else {
        val numbers = pages.keys.toArray
        java.util.Arrays.sort(numbers)
        ordered = numbers
        numbers
      }
    }
    def nonEmpty: Boolean                = pages.nonEmpty
    def contains(address: Long): Boolean = pages.get(number(address)).exists(_.get(offset(address)))
    @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
    def containsAny(address: Long, size: Int): Boolean = {
      var any = false
      spans(address, size) { (number, offset, length, _) =>
        any = any || pages.get(number).exists { bits =>
          val next = bits.nextSetBit(offset)
          next >= 0 && next < offset + length
        }
      }
      any
    }
    @SuppressWarnings(Array("scalafix:DisableSyntax.null"))
    def add(address: Long, size: Int = 1): Unit = spans(address, size) { (number, offset, length, _) =>
      pages.getOrNull(number) match {
        case null =>
          ordered = null
          val bits = java.util.BitSet(pageSize)
          bits.set(offset, offset + length)
          pages.update(number, bits)
        case bits => bits.set(offset, offset + length)
      }
    }
    def remove(address: Long, size: Int = 1): Unit = spans(address, size) { (number, offset, length, _) =>
      pages.get(number).foreach { bits =>
        bits.clear(offset, offset + length)
        if (bits.isEmpty) { val _ = pages.subtractOne(number) }
      }
    }
    @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
    def addMatching(address: Long, size: Int, into: Addresses): Unit = spans(address, size) {
      (number, offset, length, _) =>
        pages.get(number).foreach { bits =>
          var index = bits.nextSetBit(offset)
          while (index >= 0 && index < offset + length) {
            into.add((number << 12) + index.toLong)
            index = bits.nextSetBit(index + 1)
          }
        }
    }
  }

  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private inline def spans(address: Long, size: Int)(inline f: (Long, Int, Int, Int) => Unit): Unit = {
    var consumed = 0
    while (consumed < size) {
      val current = address + consumed.toLong
      val length  = math.min(pageSize - offset(current), size - consumed)
      f(number(current), offset(current), length, consumed)
      consumed += length
    }
  }

  // the number of shards whose set is occupied; drained counts the times it fell to zero
  private class Census {
    private val holders   = AtomicInteger()
    private val emptied   = AtomicLong()
    def occupied: Boolean = holders.get > 0
    def drained: Long     = emptied.get
    def join(): Unit      = { val _ = holders.incrementAndGet() }
    def leave(): Unit     = if (holders.decrementAndGet() == 0) { val _ = emptied.incrementAndGet() }
  }

  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private class Watch(occupied: () => Boolean, census: Census) {
    private var held   = false
    def settle(): Unit = {
      val now = occupied()
      if (now != held) { held = now; if (now) census.join() else census.leave() }
    }
  }

  private val missingCensus    = Census()
  private val demandedCensus   = Census()
  private val dirtyCensus      = Census()
  private val unreadableCensus = Census()
  // shards holding pages or unreadable bytes, so that an acquire visits only those
  private val populatedShards = AtomicLong()
  private val invalidations   = AtomicLong()

  private val shardCount    = 64
  private val EveryShard    = -1L
  private val SpinLimit     = 512
  private val NoBytes       = Array.emptyByteArray
  private inline val Hit    = 0
  private inline val Fresh  = 1
  private inline val Missed = 2

  /** The page-addressed state of one slice of the address space. Callers hold the slot's lock. */
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private class Shard(val slot: Int) {
    val pages: mutable.LongMap[Page] = mutable.LongMap.empty[Page]
    val missing: Addresses           = Addresses()
    // requested bytes that the client could not read
    val unreadable: Addresses = Addresses()
    val issued: Addresses     = Addresses()
    // the part of missing that a read is waiting on, as opposed to the rest of its page
    val demanded: Addresses = Addresses()
    val changed: Addresses  = Addresses()
    // in-flight read bytes whose reply was fetched before a system invalidation and so must not be accepted
    val stale: Addresses = Addresses()

    private val watches = Array(
      Watch(() => missing.nonEmpty, missingCensus),
      Watch(() => demanded.nonEmpty, demandedCensus),
      Watch(() => changed.nonEmpty, dirtyCensus),
      Watch(() => unreadable.nonEmpty, unreadableCensus)
    )
    private var populated = false

    def settle(): Unit = {
      var at = 0
      while (at < watches.length) { watches(at).settle(); at += 1 }
      val now = pages.nonEmpty || unreadable.nonEmpty
      if (now != populated) {
        populated = now
        val bit = 1L << slot
        val _   = if (now) populatedShards.updateAndGet(_ | bit) else populatedShards.updateAndGet(_ & ~bit)
      }
    }

    def atomicWidth(address: Long): Int in (0, 1, 2, 4, 8) =
      pages.get(number(address)).fold[Int in (0, 1, 2, 4, 8)](0)(_.atomicWidth(offset(address)))

    @SuppressWarnings(Array("scalafix:DisableSyntax.null"))
    def present(number: Long, offset: Int, length: Int): Boolean = {
      val page = pages.getOrNull(number)
      page != null && page.valid.nextClearBit(offset) >= offset + length
    }

    def copy(number: Long, offset: Int, length: Int, into: Array[Byte], at: Int): Unit =
      Array.copy(pages(number).bytes, offset, into, at, length)

    def unrequested(number: Long, offset: Int, length: Int): Boolean = {
      val valid     = pages.get(number).map(_.valid)
      val requested = missing.pages.get(number)
      (offset until offset + length).exists(at => !valid.exists(_.get(at)) && !requested.exists(_.get(at)))
    }

    def retire(address: Long, size: Int = 1): Unit = {
      if (missing.nonEmpty) missing.remove(address, size)
      if (demanded.nonEmpty) demanded.remove(address, size)
    }

    def requestPage(number: Long, from: Int, until: Int, waiting: Boolean): Unit = {
      val base                                = number << 12
      def request(start: Int, end: Int): Unit = {
        missing.add(base + start.toLong, end - start)
        if (waiting) demanded.add(base + start.toLong, end - start)
      }
      pages.get(number) match {
        case None       => request(from, until)
        case Some(page) =>
          var gap = page.valid.nextClearBit(from)
          while (gap < until) {
            val filled = page.valid.nextSetBit(gap) match { case -1 => until; case next => math.min(next, until) }
            request(gap, filled)
            gap = page.valid.nextClearBit(filled)
          }
      }
    }

    def requestSpeculative(number: Long, allocation: Option[HostMemoryRange]): Unit =
      allocation match {
        case None        => requestPage(number, 0, pageSize, waiting = false)
        case Some(range) =>
          val base  = number << 12
          val from  = math.max(range.address.raw, base) - base
          val until = math.min(range.address.raw + range.size.toLong, base + pageSize) - base
          if (from < until) requestPage(number, from.toInt, until.toInt, waiting = false)
      }

    def write(number: Long, offset: Int, length: Int, values: Vec[Byte], consumed: Int, word: Int): Unit = {
      val page = pages.getOrElseUpdate(number, Page())
      if (page.atomics) {
        page.words1.clear(offset, offset + length)
        page.words2.clear(offset / 2, (offset + length + 1) / 2)
        page.words4.clear(offset / 4, (offset + length + 3) / 4)
        page.words8.clear(offset / 8, (offset + length + 7) / 8)
      }
      var at = 0
      while (at < length) { page.bytes(offset + at) = values(consumed + at); at += 1 }
      page.valid.set(offset, offset + length)
      if (word != 0) {
        page.atomics = true
        if (word == 8) page.words8.set(offset / 8)
        else if (word == 4) page.words4.set(offset / 4)
        else if (word == 2) page.words2.set(offset / 2)
        else page.words1.set(offset)
      }
      val address = (number << 12) + offset.toLong
      changed.add(address, length)
      if (unreadable.nonEmpty) unreadable.remove(address, length)
      retire(address, length)
    }

    def observe(address: Long, value: Byte): Unit =
      if (!changed.contains(address)) {
        val page  = pages.getOrElseUpdate(number(address), Page())
        val index = offset(address)
        page.bytes(index) = value
        page.valid.set(index)
        retire(address)
      }

    private def storeIfAbsent(address: Long, value: Byte): Unit = {
      val page  = pages.getOrElseUpdate(number(address), Page())
      val index = offset(address)
      if (!page.valid.get(index)) { page.bytes(index) = value; page.valid.set(index) }
    }

    private def storeAbsent(number: Long, offset: Int, length: Int, values: Vec[Byte], consumed: Int): Unit = {
      val page = pages.getOrElseUpdate(number, Page())
      val end  = offset + length
      var from = page.valid.nextClearBit(offset)
      while (from < end) {
        val until = math.min(page.valid.nextSetBit(from) match { case -1 => end; case next => next }, end)
        var index = from
        while (index < until) { page.bytes(index) = values(consumed + index - offset); index += 1 }
        page.valid.set(from, until)
        from = page.valid.nextClearBit(until)
      }
    }

    def accept(number: Long, offset: Int, length: Int, values: Vec[Byte], consumed: Int, outdated: Boolean): Unit = {
      val base = (number << 12) + offset.toLong
      if (outdated) {
        var at = 0
        while (at < length) {
          val address = base + at.toLong
          if (!stale.contains(address)) { storeIfAbsent(address, values(consumed + at)); retire(address) }
          at += 1
        }
      } else {
        storeAbsent(number, offset, length, values, consumed)
        retire(base, length)
      }
      issued.remove(base, length)
      if (outdated) stale.remove(base, length)
    }

    @SuppressWarnings(Array("scalafix:DisableSyntax.null"))
    def requested(number: Long, offset: Int, length: Int): Boolean = {
      val asked = missing.pages.getOrNull(number)
      val sent  = issued.pages.getOrNull(number)
      if (asked == null) sent != null && sent.nextClearBit(offset) >= offset + length
      else if (sent == null) asked.nextClearBit(offset) >= offset + length
      else {
        val both = java.util.BitSet.valueOf(asked.toLongArray)
        both.or(sent)
        both.nextClearBit(offset) >= offset + length
      }
    }

    // requested bytes that no reply carried: the client stopped short of memory it cannot read
    def forgetUnanswered(): Unit = {
      issued.pages.foreach { (number, unanswered) =>
        demanded.pages.get(number).foreach { waiting =>
          val lost = java.util.BitSet.valueOf(unanswered.toLongArray)
          lost.and(waiting)
          if (!lost.isEmpty) {
            unreadable.pages.getOrElseUpdate(number, java.util.BitSet(pageSize)).or(lost)
            waiting.andNot(lost)
            if (waiting.isEmpty) { val _ = demanded.pages.subtractOne(number) }
          }
        }
        missing.pages.get(number).foreach { requested =>
          requested.andNot(unanswered)
          if (requested.isEmpty) { val _ = missing.pages.subtractOne(number) }
        }
      }
      issued.pages.clear()
      stale.pages.clear()
    }

    private def retainDirty(number: Long, page: Page, from: Int, until: Int): Unit =
      changed.pages.get(number) match {
        case None                                          => page.valid.clear(from, until)
        case Some(dirty) if from == 0 && until == pageSize => page.valid.and(dirty)
        case Some(dirty)                                   =>
          var index = page.valid.nextSetBit(from)
          while (index >= 0 && index < until) {
            if (!dirty.get(index)) page.valid.clear(index)
            index = page.valid.nextSetBit(index + 1)
          }
      }

    def invalidate(number: Long, offset: Int, length: Int): Unit = {
      val address = (number << 12) + offset.toLong
      unreadable.remove(address, length)
      issued.addMatching(address, length, stale)
      pages.get(number).foreach(retainDirty(number, _, offset, offset + length))
    }

    def invalidateAll(): Unit = {
      unreadable.pages.clear()
      pages.filterInPlace { (number, page) =>
        retainDirty(number, page, 0, pageSize)
        !page.valid.isEmpty
      }
    }
  }

  @SuppressWarnings(Array("scalafix:DisableSyntax.var", "scalafix:DisableSyntax.null"))
  private class Slot(index: Int) {
    val lock: ReentrantLock           = ReentrantLock()
    private var created: Shard | Null = null
    def shard: Shard                  = {
      val existing = created
      if (existing != null) existing
      else {
        val fresh = Shard(index)
        created = fresh
        fresh
      }
    }
    def existing: Shard | Null = created
  }

  private val slots = Array.tabulate(shardCount)(Slot(_))

  private def shardAt(number: Long): Shard = slots(shardIndex(number)).shard

  private def shardIndex(number: Long): Int = (number & 63L).toInt

  @SuppressWarnings(Array("scalafix:DisableSyntax.null"))
  private def populated: Array[Shard] = {
    val found = mutable.ArrayBuffer.empty[Shard]
    slots.foreach { slot =>
      val shard = slot.existing
      if (shard != null) found += shard
    }
    found.toArray
  }

  // the shards holding pages `first` to `last`
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private def touching(first: Long, last: Long): Long =
    if (last - first >= 63L) EveryShard
    else {
      var mask    = 0L
      var current = first
      while (current <= last) { mask |= 1L << shardIndex(current); current += 1 }
      mask
    }

  private def touching(ranges: Vec[HostMemoryRange]): Long =
    ranges.foldLeft(0L) { (mask, range) =>
      if (mask == EveryShard) mask
      else mask | touching(number(range.address.raw), number(range.address.raw + range.size.toLong - 1))
    }

  // holds are short, so a brief spin avoids parking behind a lane that is about to release
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private def acquire(lock: ReentrantLock): Unit = {
    var spins = 0
    while (spins < SpinLimit && !lock.tryLock()) { PlatformConcurrency.onSpinWait(); spins += 1 }
    if (spins == SpinLimit) lock.lock()
  }

  /** Runs `body` holding the locks of the shards in `mask` in ascending order, which makes them deadlock free. */
  @SuppressWarnings(Array("scalafix:DisableSyntax.var", "scalafix:DisableSyntax.null"))
  private inline def locked[A](mask: Long)(inline body: => A): A = {
    var pending = mask
    while (pending != 0L) {
      acquire(slots(java.lang.Long.numberOfTrailingZeros(pending)).lock)
      pending &= pending - 1L
    }
    try body
    finally {
      var held = mask
      while (held != 0L) {
        val slot  = slots(java.lang.Long.numberOfTrailingZeros(held))
        val shard = slot.existing
        if (shard != null) shard.settle()
        slot.lock.unlock()
        held &= held - 1L
      }
    }
  }

  @SuppressWarnings(Array("scalafix:DisableSyntax.var", "scalafix:DisableSyntax.null"))
  private inline def inShards(mask: Long)(inline f: Shard => Unit): Unit = {
    var pending = mask
    while (pending != 0L) {
      val shard = slots(java.lang.Long.numberOfTrailingZeros(pending)).existing
      if (shard != null) f(shard)
      pending &= pending - 1L
    }
  }

  private def bytes(address: Long, size: Int): Vec[Byte] = {
    val result = Array.ofDim[Byte](size)
    spans(address, size)((number, offset, length, consumed) =>
      shardAt(number).copy(number, offset, length, result, consumed)
    )
    Vec.unsafeWrapArray(result)
  }

  private def atomicWidth(address: Long): Int in (0, 1, 2, 4, 8) = shardAt(number(address)).atomicWidth(address)

  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private def selectedDirty(selected: Vec[HostMemoryRange]): Addresses = {
    val result = Addresses()
    val masks  = mutable.LongMap.empty[java.util.BitSet]
    selected.foreach { range =>
      val last    = range.address.raw + range.size.toLong
      var address = range.address.raw
      while (address < last) {
        val number = address >> 12
        val base   = number << 12
        val end    = math.min(base + pageSize, last)
        if (shardAt(number).changed.pages.contains(number))
          masks.getOrElseUpdate(number, java.util.BitSet(pageSize)).set((address - base).toInt, (end - base).toInt)
        address = base + pageSize
      }
    }
    masks.foreach { (number, mask) =>
      val shard = shardAt(number)
      shard.changed.pages.get(number).foreach { dirty =>
        shard.pages.get(number).foreach { page =>
          val base                                                              = number << 12
          def expand(words: java.util.BitSet, width: Int in (1, 2, 4, 8)): Unit = {
            var index = words.nextSetBit(0)
            while (index >= 0) {
              val start = index * width
              val next  = mask.nextSetBit(start)
              if (next >= start && next < start + width) mask.set(start, start + width)
              index = words.nextSetBit(index + 1)
            }
          }
          expand(page.words8, 8)
          expand(page.words4, 4)
          expand(page.words2, 2)
          expand(page.words1, 1)
          mask.and(dirty)
          var offset = mask.nextSetBit(0)
          while (offset >= 0) {
            val end = math.min(mask.nextClearBit(offset), pageSize)
            result.add(base + offset.toLong, end - offset)
            offset = mask.nextSetBit(end)
          }
        }
      }
    }
    result
  }

  private val maxWindow = math.max(1, maxBatchBytes.raw / pageSize)
  // end page of a scan's latest window -> that window's length in pages
  private val scans = mutable.LongMap.empty[Int]

  // the pages [start, end) to fetch for a first miss on pages [first, last]; a miss within one window of the end of an
  // earlier window continues that scan with a window twice as long
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private def window(first: Long, last: Long): (Long, Long) = {
    var prior = -1L
    scans.foreach((end, length) => if (end <= first && first - end <= length && end > prior) prior = end)
    val start = if (prior < 0) first else prior
    val count = if (prior < 0) 1 else math.min(maxWindow, 2 * scans(prior))
    val end   = math.max(start + count, last + 1)
    if (prior >= 0) scans.subtractOne(prior)
    else if (scans.size >= 64) scans.clear()
    scans.update(end, (end - start).toInt)
    (start, end)
  }

  // copies the bytes if all are valid. Otherwise demands the invalid ones, and with `windowed` also fetches ahead; a
  // first miss reports Fresh instead when it needs `windowed`, so the caller can take every shard for the window
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private def attempt(
      begin: Long,
      size: Int,
      result: Array[Byte],
      allocation: => Option[HostMemoryRange],
      windowed: Boolean
  ): Int = {
    var absent = false
    var fresh  = false
    spans(begin, size) { (number, offset, length, consumed) =>
      val shard = shardAt(number)
      if (!shard.present(number, offset, length)) {
        absent = true
        fresh = fresh || shard.unrequested(number, offset, length)
      } else if (!absent && result.nonEmpty) shard.copy(number, offset, length, result, consumed)
    }
    if (!absent) Hit
    else if (fresh && !windowed) Fresh
    else {
      if (fresh) {
        val bounds       = allocation
        val (start, end) = window(number(begin), number(begin + size - 1))
        var current      = start
        while (current < end) { shardAt(current).requestSpeculative(current, bounds); current += 1 }
      }
      spans(begin, size)((number, offset, length, _) =>
        shardAt(number).requestPage(number, offset, offset + length, waiting = true)
      )
      Missed
    }
  }

  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  def read(address: GuestAddress, size: Int >= 0, allocation: => Option[HostMemoryRange]): Option[Vec[Byte]] =
    if (size == 0) Some(Vec.empty[Byte])
    else {
      val begin  = address.raw
      val first  = number(begin)
      val result = Array.ofDim[Byte](size)
      var code   = locked(touching(first, number(begin + size - 1)))(attempt(begin, size, result, allocation, false))
      if (code == Fresh) code = locked(EveryShard)(attempt(begin, size, result, allocation, true))
      if (code == Hit) Some(Vec.unsafeWrapArray(result)) else None
    }

  /** Demands the invalid bytes of `count` reads in one pass over the shards they touch, without copying. Returns
    * whether any read has bytes still to arrive.
    */
  // one call per pointer argument of a launch, beside a locked update of the mirror
  @SuppressWarnings(Array("scalafix:DisableSyntax.var", "bonsai:unspecialised-function"))
  def stage(
      addresses: Array[Long],
      sizes: Array[Int],
      count: Int,
      allocation: Long => Option[HostMemoryRange]
  ): Boolean = {
    var mask = 0L
    var at   = 0
    while (at < count) {
      if (sizes(at) > 0) mask |= touching(number(addresses(at)), number(addresses(at) + sizes(at) - 1))
      at += 1
    }
    var missed = false
    var fresh  = List.empty[Int]
    locked(mask) {
      at = 0
      while (at < count) {
        if (sizes(at) > 0) {
          val begin = addresses(at)
          attempt(begin, sizes(at), NoBytes, allocation(begin), windowed = false) match {
            case Hit   => ()
            case Fresh => missed = true; fresh = at :: fresh
            case _     => missed = true
          }
        }
        at += 1
      }
    }
    if (fresh.nonEmpty)
      locked(EveryShard)(fresh.foreach { index =>
        val begin = addresses(index)
        val _     = attempt(begin, sizes(index), NoBytes, allocation(begin), windowed = true)
      })
    missed
  }

  /** Applies `update` to the aligned word at `address` in one step against every other access to the mirror, returning
    * the previous value. The word is fetched like a read when absent, and left dirty with an atomic width.
    */
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  def modify(address: GuestAddress, size: Int in (1, 2, 4, 8), allocation: => Option[HostMemoryRange])(
      update: Long => Long
  ): Option[Long] = {
    val begin                                   = address.raw
    val first                                   = number(begin)
    val buffer                                  = Array.ofDim[Byte](size)
    var previous                                = 0L
    def run(mask: Long, windowed: Boolean): Int = locked(mask) {
      val code = attempt(begin, size, buffer, allocation, windowed)
      if (code == Hit) {
        val width = size.widen[Int in 0 ~ 8]
        previous = LittleEndian.decode(buffer, 0, width)
        LittleEndian.write(update(previous), buffer, 0, width)
        shardAt(first).write(first, offset(begin), size, Vec.unsafeWrapArray(buffer), 0, size)
      }
      code
    }
    var code = run(1L << shardIndex(first), windowed = false)
    if (code == Fresh) code = run(EveryShard, windowed = true)
    if (code == Hit) Some(previous) else None
  }

  def write(at: GuestAddress, values: Vec[Byte], atomicWidth: Int in (0, 1, 2, 4, 8) = 0): Unit =
    if (values.nonEmpty) {
      val address = at.raw
      // Preserve the width of naturally aligned stores when publishing a batch.
      // Mixed-width overlapping accesses do not have a cross-agent atomicity guarantee.
      val word: Int = if (atomicWidth != 0 && address % atomicWidth == 0) atomicWidth else 0
      locked(touching(number(address), number(address + values.size - 1))) {
        spans(address, values.size)((number, offset, length, consumed) =>
          shardAt(number).write(number, offset, length, values, consumed, if (consumed == 0) word else 0)
        )
      }
    }

  /** Counts system invalidations, so an observation made earlier can be recognised as possibly out of date. */
  def generation: Long = invalidations.get

  /** Record a value returned by a native atomic operation without making it dirty for publication. An observation that
    * began at `since` is dropped once a system invalidation has happened after it, because the host may have changed
    * the word in between.
    */
  def observeAtomic(address: GuestAddress, values: Vec[Byte], since: Long): Unit =
    if (values.nonEmpty)
      locked(touching(number(address.raw), number(address.raw + values.size - 1))) {
        if (since == invalidations.get)
          values.zipWithIndex.foreach { case (value, position) =>
            val target = address.raw + position.toLong
            shardAt(number(target)).observe(target, value)
          }
      }

  // the first byte at or after `from` in the page that belongs to a naturally aligned atomic word of any width
  private def nextAtomicByte(page: Page, from: Int): Int = {
    def next(words: java.util.BitSet, width: Int): Int = words.nextSetBit(from / width) match {
      case -1    => Int.MaxValue
      case index => index * width
    }
    math.min(math.min(next(page.words8, 8), next(page.words4, 4)), math.min(next(page.words2, 2), next(page.words1, 1)))
  }

  private val wordWidths = List(2, 4, 8)

  // the length of the leading part of `[from, until)` over which the page's atomic width stays that of `from`
  private def uniformWidth(page: Page, from: Int, until: Int): Int = {
    val width = page.atomicWidth(from)
    if (width == 0) math.min(until, nextAtomicByte(page, from)) - from
    else {
      val first = from / width
      val last  = math.min(page.wordsOf(width).nextClearBit(first), (until + width - 1) / width)
      val wider = wordWidths.filter(_ > width).map(w =>
        page.wordsOf(w).nextSetBit(from / w) match {
          case -1    => Int.MaxValue
          case index => math.max(index * w, from + 1)
        }
      )
      math.min(math.min(until, last * width), wider.foldLeft(Int.MaxValue)(math.min)) - from
    }
  }

  private def flushRun(chunks: mutable.ArrayBuffer[HostMemoryRange], start: Long, size: Int): Unit =
    if (size > 0) chunks += HostMemoryRange(GuestAddress(start), size.unsafe[Int > 0])

  /** Visits the pages of several disjoint sets in ascending order. */
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private class Cursor(sources: Array[Addresses]) {
    private val ordered   = sources.map(_.sortedPages())
    private val positions = Array.ofDim[Int](sources.length)
    private var current   = 0

    /** The next page number, or -1 once every page has been visited. */
    def next(): Long = {
      var best  = -1
      var least = Long.MaxValue
      var at    = 0
      while (at < ordered.length) {
        val position = positions(at)
        if (position < ordered(at).length && (best < 0 || ordered(at)(position) < least)) {
          best = at
          least = ordered(at)(position)
        }
        at += 1
      }
      if (best < 0) -1L
      else {
        positions(best) += 1
        current = best
        least
      }
    }

    /** The bits of the page last returned, null if it has left its set since the order was taken. */
    def bits(number: Long): java.util.BitSet | Null = sources(current).pages.getOrNull(number)
  }

  @SuppressWarnings(Array("scalafix:DisableSyntax.var", "scalafix:DisableSyntax.null"))
  private def storeRanges(cursor: Cursor): Vec[HostMemoryRange] = {
    val chunks        = mutable.ArrayBuffer.empty[HostMemoryRange]
    var remaining     = maxBatchBytes.raw
    var start         = 0L
    var size          = 0
    var width         = 0
    var capacity      = 0
    var full          = false
    def close(): Unit = { flushRun(chunks, start, size); size = 0 }
    var number        = cursor.next()
    while (number >= 0 && !full) {
      val bits = cursor.bits(number)
      if (bits != null) {
        val page = shardAt(number).pages(number)
        val base = number << 12
        var from = bits.nextSetBit(0)
        while (from >= 0 && !full) {
          val until = bits.nextClearBit(from)
          var at    = from
          while (at < until && !full) {
            val sw: Int = page.atomicWidth(at)
            val length  = math.max(1, uniformWidth(page, at, until))
            val address = base + at.toLong
            if (size > 0 && start + size == address && width == sw && size < capacity) {
              val take = math.min(length, capacity - size)
              size += take
              remaining -= take
              at += take
            } else {
              close()
              val unit = math.max(1, sw)
              if (chunks.size >= maxBatchRanges || remaining < unit) full = true
              else {
                capacity = math.min(65536, remaining / unit * unit)
                val take = math.min(length, capacity)
                start = address
                size = take
                width = sw
                remaining -= take
                at += take
              }
            }
          }
          from = bits.nextSetBit(until)
        }
      }
      number = cursor.next()
    }
    close()
    Vec.from(chunks)
  }

  @SuppressWarnings(Array("scalafix:DisableSyntax.var", "scalafix:DisableSyntax.null"))
  private def readRanges(cursor: Cursor): Vec[HostMemoryRange] = {
    val chunks        = mutable.ArrayBuffer.empty[HostMemoryRange]
    var remaining     = maxBatchBytes.raw
    var full          = false
    var start         = 0L
    var size          = 0
    def close(): Unit = { flushRun(chunks, start, size); size = 0 }
    var number        = cursor.next()
    while (number >= 0 && !full && remaining > 0) {
      val base = number << 12
      val bits = cursor.bits(number)
      if (bits != null) {
        var from = bits.nextSetBit(0)
        while (from >= 0 && !full && remaining > 0) {
          val end = bits.nextClearBit(from)
          var at  = from
          while (at < end && !full && remaining > 0) {
            val address = base + at.toLong
            if (size == 0 || start + size != address || size >= 65536) {
              close()
              full = chunks.size >= maxBatchRanges
              start = address
            }
            if (!full) {
              val take = math.min(math.min(end - at, remaining), 65536 - size)
              size += take
              at += take
              remaining -= take
            }
          }
          from = bits.nextSetBit(end)
        }
      }
      number = cursor.next()
    }
    close()
    Vec.from(chunks)
  }

  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  def isUnreadable(address: GuestAddress, size: Int >= 0): Boolean =
    unreadableCensus.occupied && size > 0 && {
      val begin = address.raw
      locked(touching(number(begin), number(begin + size - 1))) {
        var any = false
        spans(begin, size)((number, offset, length, _) =>
          any = any || shardAt(number).unreadable.containsAny((number << 12) + offset.toLong, length)
        )
        any
      }
    }

  def hasPending: Boolean = demandedCensus.occupied
  def hasDirty: Boolean   = dirtyCensus.occupied

  def hasDirty(selected: Vec[HostMemoryRange]): Boolean = dirtyCensus.occupied &&
    locked(touching(selected))(selectedDirty(selected).nonEmpty)

  /** Counts transitions of the demanded bytes from some to none, which release the waiting readers. */
  def drains: Long = demandedCensus.drained

  def requests(): Vec[HostMemoryRange] =
    if (!missingCensus.occupied) Vec.empty[HostMemoryRange]
    else
      locked(EveryShard) {
        val result = readRanges(Cursor(populated.map(_.missing)))
        result.foreach(range =>
          spans(range.address.raw, range.size)((number, offset, length, _) =>
            shardAt(number).issued.add((number << 12) + offset.toLong, length)
          )
        )
        result
      }

  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  def supply(values: Vec[HostMemoryBytes]): Either[NonEmpty[String], Unit] = locked(EveryShard) {
    val valid = values.forall { value =>
      var all = true
      spans(value.address.raw, value.bytes.size)((number, offset, length, _) =>
        all = all && shardAt(number).requested(number, offset, length)
      )
      all
    }
    if (!valid) Left("host memory reply includes bytes that were not requested")
    else {
      val outdated = Array.ofDim[Boolean](shardCount)
      populated.foreach(shard => outdated(shard.slot) = shard.stale.nonEmpty)
      values.foreach { value =>
        spans(value.address.raw, value.bytes.size)((number, offset, length, consumed) =>
          shardAt(number).accept(number, offset, length, value.bytes, consumed, outdated(shardIndex(number)))
        )
      }
      populated.foreach(_.forgetUnanswered())
      Right(())
    }
  }

  private def take(sources: Array[Addresses]): Vec[HostMemoryBytes] = {
    val result = storeRanges(Cursor(sources)).map { range =>
      HostMemoryBytes(range.address, bytes(range.address.raw, range.size), atomicWidth(range.address.raw))
    }
    result.foreach(range =>
      spans(range.address.raw, range.bytes.size)((number, offset, length, _) =>
        shardAt(number).changed.remove((number << 12) + offset.toLong, length)
      )
    )
    result
  }

  def takeDirty(): Vec[HostMemoryBytes] =
    if (!dirtyCensus.occupied) Vec.empty[HostMemoryBytes]
    else locked(EveryShard)(take(populated.map(_.changed)))

  def takeDirty(selected: Vec[HostMemoryRange]): Vec[HostMemoryBytes] =
    if (!dirtyCensus.occupied) Vec.empty[HostMemoryBytes]
    else locked(touching(selected))(take(Array(selectedDirty(selected))))

  /** Discards host reads within `selected` for a system-scope load, retaining unpublished writes. Reads of those bytes
    * already in flight were issued before this call, so their replies are refused and the bytes fetched again.
    */
  def invalidateClean(selected: Vec[HostMemoryRange]): Unit = {
    val _ = invalidations.incrementAndGet()
    locked(touching(selected)) {
      selected.foreach { range =>
        spans(range.address.raw, range.size)((number, offset, length, _) =>
          shardAt(number).invalidate(number, offset, length)
        )
      }
    }
  }

  /** Acquire discards stale host reads, retaining unpublished writes from other lanes. */
  def invalidateClean(): Unit = {
    val _    = invalidations.incrementAndGet()
    val mask = populatedShards.get
    locked(mask)(inShards(mask)(_.invalidateAll()))
  }
}
