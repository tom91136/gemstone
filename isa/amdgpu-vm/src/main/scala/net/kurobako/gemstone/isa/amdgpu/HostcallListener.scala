package net.kurobako.gemstone.isa.amdgpu

import java.util.concurrent.locks.ReentrantLock
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.concurrent.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.constraint.auto.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.vm.*
import scala.annotation.tailrec
import scala.collection.mutable

private object HostcallListener {
  val ServiceFunctionCall = 1
  val ServicePrintf       = 2
  val ServiceDeviceMemory = 3

  // hostcall_buffer_t, the prefix the device library reads: packet headers, packet payloads, doorbell, free stack,
  // ready stack and the mask that extracts a packet index from a tagged pointer
  private val HeadersWord   = 0L
  private val PayloadsWord  = 8L
  private val ReadyStack    = 32L
  private val IndexMaskWord = 40L

  // packet header: next, active lane mask, service, control; a payload is 64 slots of 8 words
  private val HeaderBytes: Long   = 24L
  private val ActiveMask: Long    = 8L
  private val ServiceField        = 16L
  private val ControlField        = 20L
  private val SlotBytes: Int >= 0 = 64
  private val PayloadBytes        = 64L * SlotBytes

  private val BeginFlag          = 1L
  private val EndFlag            = 2L
  private val Word: Int in 1 ~ 8 = 8
  private val Half: Int in 1 ~ 8 = 4

  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private class Message(val id: Int) {
    val words: mutable.ArrayBuffer[Long] = mutable.ArrayBuffer.empty
    var live                             = true
  }
}

/** Services a queue's hostcall buffer on each `s_sendmsg MSG_INTERRUPT` doorbell. */
private class HostcallListener(handler: Event => Either[Fault, Unit]) {
  import HostcallListener.*

  private val lock     = ReentrantLock()
  private val messages = mutable.ArrayBuffer.empty[Message]
  private val free     = mutable.ArrayBuffer.empty[Int]

  private def readAt(memory: AddressSpace, address: Long, size: Int in 1 ~ 8): Either[Fault, Long] =
    memory.readValue(GuestAddress(address), size)

  def service(memory: AddressSpace, buffer: Long): Either[Fault, Unit] = lock.withLock {
    for {
      headers  <- readAt(memory, buffer + HeadersWord, Word)
      payloads <- readAt(memory, buffer + PayloadsWord, Word)
      mask     <- readAt(memory, buffer + IndexMaskWord, Word)
      ready    <- memory.atomicModifyValue(GuestAddress(buffer + ReadyStack), Word)(_ => 0L)
      _        <- packets(memory, headers, payloads, mask, ready)
    } yield ()
  }

  @tailrec
  private def packets(memory: AddressSpace, headers: Long, payloads: Long, mask: Long, top: Long): Either[Fault, Unit] =
    if (top == 0L) Right(())
    else {
      val index  = top & mask
      val header = headers + index * HeaderBytes
      val served = for {
        next    <- readAt(memory, header, Word)
        active  <- readAt(memory, header + ActiveMask, Word)
        service <- readAt(memory, header + ServiceField, Half)
        _       <- lanes(memory, payloads + index * PayloadBytes, service.toInt, active)
        control <- readAt(memory, header + ControlField, Half)
        _       <- memory.writeValue(GuestAddress(header + ControlField), control & ~1L, Half)
      } yield next
      served match {
        case Left(fault) => Left(fault)
        case Right(next) => packets(memory, headers, payloads, mask, next)
      }
    }

  @tailrec
  private def lanes(memory: AddressSpace, payload: Long, service: Int, active: Long): Either[Fault, Unit] =
    if (active == 0L) Right(())
    else {
      val lane = java.lang.Long.numberOfTrailingZeros(active)
      val slot = payload + lane * SlotBytes
      val done = for {
        bytes <- memory.read(GuestAddress(slot), SlotBytes)
        words = Array.tabulate(SlotBytes / Word)(word => LittleEndian.decode(bytes, word * Word, Word))
        result <- handle(service, words)
        _      <- memory.writeValue(GuestAddress(slot), result, Word)
      } yield ()
      done match {
        case Left(fault) => Left(fault)
        case Right(_)    => lanes(memory, payload, service, active & (active - 1L))
      }
    }

  private def handle(service: Int, slot: Array[Long]): Either[Fault, Long] = service match {
    case ServicePrintf       => message(slot)
    case ServiceFunctionCall =>
      Left(
        Fault.Unsupported("hostcall function-call service calls a host address, which the emulated device cannot reach")
      )
    case ServiceDeviceMemory =>
      Left(Fault.Unsupported(
        "hostcall device-memory service allocates while the launch runs, which the emulated device cannot"
      ))
    case other => Left(Fault.Backend(s"hostcall names service $other, which no handler serves"))
  }

  private def message(slot: Array[Long]): Either[Fault, Long] = {
    val descriptor = slot(0)
    val begin      = (descriptor & BeginFlag) != 0L
    val found      =
      if (begin) Some(open())
      else messages.lift(((descriptor >>> 8) & 0xffffffffL).toInt).filter(_.live)
    found match {
      case None          => Left(Fault.Backend("hostcall message has no live message to append to"))
      case Some(current) =>
        val length = ((descriptor >>> 5) & 7L).toInt
        (1 to length).foreach(index => current.words += slot(index))
        val tagged = if (begin) ((descriptor & ~BeginFlag) & 0xffL) | (current.id.toLong << 8) else descriptor
        if ((descriptor & EndFlag) == 0L) Right(tagged)
        else {
          val result = printf(current.words.toArray)
          current.live = false
          current.words.clear()
          free += current.id
          result
        }
    }
  }

  private def open(): Message =
    if (free.nonEmpty) {
      val reused = messages(free.remove(free.size - 1))
      reused.live = true
      reused
    } else {
      val created = Message(messages.size)
      messages += created
      created
    }

  private def printf(words: Array[Long]): Either[Fault, Long] = {
    val control = words(0)
    if ((control & ~1L) != 0L) Right(-1L)
    else {
      val stream        = if (control == 1L) Event.PrintStream.Stderr else Event.PrintStream.Stdout
      val (text, count) = HostcallPrintf.format(words)
      handler(Event.Print("%s", Vec(Event.PrintArgument.Text(text)), stream)).map(_ => count.toLong)
    }
  }
}
