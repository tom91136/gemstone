package net.kurobako.gemstone.vm

import net.kurobako.gemstone.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.memory.*
import scala.annotation.tailrec

/** The device-side `malloc` and `free` of a context. The allocator keeps all of its state in the region itself, so the
  * heap lives exactly as long as the memory the context owns and holds across launches.
  *
  * The region starts with a header of four words: the end of the carved blocks, a lock, the head of the free list and
  * the size of the last block carved. The first word is zero until a `malloc` or `free` first reaches the region, which
  * is how a driver tells whether the heap has been used. A block is a 16-byte header, its size with the low bit set
  * while allocated and the size of the physically previous block, followed by the payload. A free block stores the
  * links of the free list in its payload. Free blocks never end at the top: the top is lowered instead, so a free block
  * always has a successor.
  */
private[gemstone] object ContextHeap {
  val HeaderBytes: Long = 64L

  private val TopWord            = 0L
  private val LockWord           = 8L
  private val FreeWord           = 16L
  private val LastWord           = 24L
  private val BlockHeader        = 16L
  private val MinimumBlock       = 32L
  private val Alignment          = 16L
  private val Word: Int in 1 ~ 8 = 8
  private val Flag: Long         = 1L

  /** The address of a new block of `size` bytes, or 0 when the heap cannot supply it. */
  def allocate(memory: AddressSpace, region: HeapRegion, size: Long): Either[Fault, Long] =
    if (size <= 0L || size > region.size) Right(0L)
    else Heap(memory, region).locked(_.allocate(size))

  def free(memory: AddressSpace, region: HeapRegion, address: Long): Either[Fault, Unit] =
    if (address == 0L) Right(())
    else Heap(memory, region).locked(_.free(address))

  private class Heap(memory: AddressSpace, region: HeapRegion) {
    private def at(offset: Long): GuestAddress                      = GuestAddress(region.base.raw + offset)
    private def get(offset: Long): Either[Fault, Long]              = memory.readValue(at(offset), Word)
    private def put(offset: Long, value: Long): Either[Fault, Unit] = memory.writeValue(at(offset), value, Word)
    private def sizeOf(header: Long): Long                          = header & ~Flag

    def locked[A](body: Heap => Either[Fault, A]): Either[Fault, A] = {
      @tailrec def acquire(): Either[Fault, Unit] =
        memory.atomicModifyValue(at(LockWord), 4: Int in 1 ~ 8)(held => if (held == 0L) 1L else held) match {
          case Left(fault) => Left(fault)
          case Right(0L)   => Right(())
          case Right(_)    => PlatformConcurrency.yieldThread(); acquire()
        }
      acquire().flatMap { _ =>
        val result = try body(this)
        catch { case error: Throwable => val _ = release(); throw error }
        release().flatMap(_ => result)
      }
    }

    private def release(): Either[Fault, Unit] = memory.writeValue(at(LockWord), 0L, 4: Int in 1 ~ 8)

    private def top(): Either[Fault, Long] =
      get(TopWord).flatMap(top => if (top != 0L) Right(top) else put(TopWord, HeaderBytes).map(_ => HeaderBytes))

    private def link(block: Long): Either[Fault, Unit] = for {
      head <- get(FreeWord)
      _    <- put(block + BlockHeader, head)
      _    <- put(block + BlockHeader + 8L, 0L)
      _    <- if (head == 0L) Right(()) else put(head + BlockHeader + 8L, block)
      _    <- put(FreeWord, block)
    } yield ()

    private def unlink(block: Long): Either[Fault, Unit] = for {
      next     <- get(block + BlockHeader)
      previous <- get(block + BlockHeader + 8L)
      _        <- if (previous == 0L) put(FreeWord, next) else put(previous + BlockHeader, next)
      _        <- if (next == 0L) Right(()) else put(next + BlockHeader + 8L, previous)
    } yield ()

    @tailrec private def firstFit(block: Long, need: Long): Either[Fault, Long] =
      if (block == 0L) Right(0L)
      else
        get(block) match {
          case Left(fault)                             => Left(fault)
          case Right(header) if sizeOf(header) >= need => Right(block)
          case Right(_)                                =>
            get(block + BlockHeader) match {
              case Left(fault) => Left(fault)
              case Right(next) => firstFit(next, need)
            }
        }

    private def split(block: Long, need: Long, top: Long): Either[Fault, Unit] =
      get(block).flatMap { header =>
        val total     = sizeOf(header)
        val remainder = total - need
        if (remainder < MinimumBlock) put(block, total | Flag)
        else {
          val tail = block + need
          for {
            _ <- put(block, need | Flag)
            _ <- put(tail, remainder)
            _ <- put(tail + 8L, need)
            _ <- if (tail + remainder < top) put(tail + remainder + 8L, remainder) else Right(())
            _ <- link(tail)
          } yield ()
        }
      }

    private def carve(top: Long, need: Long): Either[Fault, Long] =
      if (need > region.size - top) Right(0L)
      else
        for {
          last <- get(LastWord)
          _    <- put(top, need | Flag)
          _    <- put(top + 8L, last)
          _    <- put(TopWord, top + need)
          _    <- put(LastWord, need)
        } yield top

    def allocate(size: Long): Either[Fault, Long] = {
      val need = Math.max(MinimumBlock, ((size + Alignment - 1L) & ~(Alignment - 1L)) + BlockHeader)
      for {
        top   <- top()
        found <- get(FreeWord).flatMap(firstFit(_, need))
        block <-
          if (found == 0L) carve(top, need)
          else unlink(found).flatMap(_ => split(found, need, top)).map(_ => found)
      } yield if (block == 0L) 0L else region.base.raw + block + BlockHeader
    }

    def free(address: Long): Either[Fault, Unit] = {
      val offset = address - region.base.raw - BlockHeader
      top().flatMap { top =>
        if (offset < HeaderBytes || offset >= top || (offset & (Alignment - 1L)) != 0L) Right(())
        else
          get(offset).flatMap { header =>
            if ((header & Flag) == 0L) Right(())
            else {
              val size = sizeOf(header)
              for {
                following <- if (offset + size < top) get(offset + size) else Right(Flag)
                grown     <-
                  if ((following & Flag) != 0L) Right(size)
                  else unlink(offset + size).map(_ => size + sizeOf(following))
                previousSize <- get(offset + 8L)
                previous     <- if (previousSize == 0L) Right(Flag) else get(offset - previousSize)
                merge = (previous & Flag) == 0L
                start = if (merge) offset - previousSize else offset
                total = if (merge) grown + previousSize else grown
                _ <- if (merge) unlink(start) else Right(())
                _ <- release(start, total, top)
              } yield ()
            }
          }
      }
    }

    private def release(start: Long, total: Long, top: Long): Either[Fault, Unit] =
      if (start + total == top)
        for {
          before <- get(start + 8L)
          _      <- put(TopWord, start)
          _      <- put(LastWord, before)
        } yield ()
      else
        for {
          _ <- put(start, total)
          _ <- put(start + total + 8L, total)
          _ <- link(start)
        } yield ()
  }
}
