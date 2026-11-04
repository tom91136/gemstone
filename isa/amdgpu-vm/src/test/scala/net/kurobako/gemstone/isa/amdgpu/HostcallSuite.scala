package net.kurobako.gemstone.isa.amdgpu

import java.nio.charset.StandardCharsets
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.testing.either.*
import net.kurobako.gemstone.vm.*
import scala.collection.mutable

class HostcallSuite extends Suite {

  private val Packets            = 4
  private val Headers            = 64L
  private val Payloads           = Headers + 24L * Packets
  private val Bytes              = Payloads + 4096L * Packets
  private val Begin              = 1L
  private val End                = 2L
  private val bufferId           = AllocationId(1)
  private val Word: Int in 1 ~ 8 = 8
  private val Half: Int in 1 ~ 8 = 4
  private val Full: Int in 0 ~ 8 = 8

  private def words(text: String): Array[Long] = {
    val bytes = Array.copyOf(text.getBytes(StandardCharsets.ISO_8859_1), ((text.length + 1) + 7) / 8 * 8)
    Array.tabulate(bytes.length / 8)(index => LittleEndian.decode(bytes, index * 8, Full))
  }

  private def message(format: String, arguments: Long*): Array[Long] = Array(0L) ++ words(format) ++ arguments

  private def rendered(format: String, arguments: Long*): String =
    HostcallPrintf.format(message(format, arguments*))._1

  private def bits(value: Double): Long = java.lang.Double.doubleToRawLongBits(value)

  private class Buffer {
    val memory: RegionMemory = RegionMemory()
    memory.allocate(bufferId, Bytes.toInt)
    val base: Long                 = memory.address(bufferId, 0L).value.raw
    private val events             = mutable.ArrayBuffer.empty[Event]
    val listener: HostcallListener = HostcallListener { event => events += event; Right(()) }

    private def put(address: Long, value: Long, size: Int in 1 ~ 8): Unit =
      memory.writeValue(GuestAddress(address), value, size).value

    private def get(address: Long, size: Int in 1 ~ 8): Long = memory.readValue(GuestAddress(address), size).value

    put(base, base + Headers, Word)
    put(base + 8, base + Payloads, Word)
    put(base + 40, Packets - 1L, Word)

    def submit(index: Int, service: Int, lanes: Map[Int, Array[Long]]): Unit = {
      val header = base + Headers + 24L * index
      put(header, get(base + 32, Word), Word)
      put(header + 8, lanes.keys.foldLeft(0L)((mask, lane) => mask | (1L << lane)), Word)
      put(header + 16, service.toLong, Half)
      put(header + 20, 1L, Half)
      lanes.foreach { case (lane, slot) =>
        slot.zipWithIndex.foreach { case (value, word) =>
          put(base + Payloads + 4096L * index + 64L * lane + 8L * word, value, Word)
        }
      }
      put(base + 32, index.toLong, Word)
    }

    def service(): Either[Fault, Unit]      = listener.service(memory, base)
    def result(index: Int, lane: Int): Long = get(base + Payloads + 4096L * index + 64L * lane, Word)
    def ready(index: Int): Boolean          = (get(base + Headers + 24L * index + 20, Half) & 1L) != 0L
    def stack: Long                         = get(base + 32, Word)
    def printed: Vec[String]                = Vec.from(events.collect {
      case Event.Print(_, Vec(Event.PrintArgument.Text(text)), Event.PrintStream.Stdout) => text
    })
    def printedToStderr: Vec[String] = Vec.from(events.collect {
      case Event.Print(_, Vec(Event.PrintArgument.Text(text)), Event.PrintStream.Stderr) => text
    })
  }

  private def descriptor(flags: Long, length: Int): Long = flags | (length.toLong << 5)

  test("a single-packet message prints each active lane in lane order and releases the packet") {
    val buffer = Buffer()
    val format = message("n=%d\n", 7L)
    buffer.submit(
      2,
      HostcallListener.ServicePrintf,
      Map(
        2 -> (Array(descriptor(Begin | End, format.length)) ++ message("n=%d\n", 9L)),
        0 -> (Array(descriptor(Begin | End, format.length)) ++ format)
      )
    )
    assertEquals(buffer.service(), Right(()))
    assertEquals(buffer.printed, Vec("n=7\n", "n=9\n"))
    assertEquals(buffer.result(index = 2, lane = 0), 4L)
    assertEquals(buffer.result(index = 2, lane = 2), 4L)
    assert(!buffer.ready(2))
    assertEquals(buffer.stack, 0L)
  }

  test("a message split across packets accumulates under the id the first packet returns") {
    val buffer = Buffer()
    val all    = message("%s-%d", 0L).take(1 + words("%s-%d").length)
    val text   = words("hello") ++ words("%s-%d")
    val first  = Array(0L) ++ words("%s-%d")
    buffer.submit(1, HostcallListener.ServicePrintf, Map(0 -> (Array(descriptor(Begin, first.length)) ++ first)))
    assertEquals(buffer.service(), Right(()))
    val issued = buffer.result(index = 1, lane = 0)
    assertEquals(issued & 3L, 0L)
    val rest = words("hello") ++ Array(5L)
    buffer.submit(
      3,
      HostcallListener.ServicePrintf,
      Map(0 -> (Array((issued & ~255L) | descriptor(End, rest.length)) ++ rest))
    )
    assertEquals(buffer.service(), Right(()))
    assertEquals(buffer.printed, Vec("hello-5"))
    assert(all.nonEmpty && text.nonEmpty)
  }

  test("a control word with bit 0 set prints to standard error") {
    val buffer = Buffer()
    val stderr = Array(1L) ++ words("e=%d\n") ++ Array(3L)
    buffer.submit(
      1,
      HostcallListener.ServicePrintf,
      Map(0 -> (Array(descriptor(Begin | End, stderr.length)) ++ stderr))
    )
    assertEquals(buffer.service(), Right(()))
    assertEquals(buffer.printedToStderr, Vec("e=3\n"))
    assertEquals(buffer.printed, Vec.empty[String])
  }

  test("the control word's reserved bits make printf return -1 and print nothing") {
    val buffer = Buffer()
    val bad    = Array(4L) ++ words("x")
    buffer.submit(1, HostcallListener.ServicePrintf, Map(0 -> (Array(descriptor(Begin | End, bad.length)) ++ bad)))
    assertEquals(buffer.service(), Right(()))
    assertEquals(buffer.result(index = 1, lane = 0), -1L)
    assertEquals(buffer.printed, Vec.empty[String])
  }

  test("a service without a handler faults") {
    val buffer = Buffer()
    buffer.submit(1, HostcallListener.ServiceDeviceMemory, Map(0 -> Array.fill(8)(0L)))
    assert(buffer.service().isLeft)
  }

  test("conversions follow C printf") {
    assertEquals(rendered("%5.2f|%-4d|", bits(3.14159), 42L), " 3.14|42  |")
    assertEquals(
      rendered("%lld %llx %lu", -5000000000L, 0xdeadbeefcafeL, -1L),
      "-5000000000 deadbeefcafe 18446744073709551615"
    )
    assertEquals(rendered("%x %u %hhd %hd", 255L, -1L, 300L, 70000L), "ff 4294967295 44 4464")
    assertEquals(rendered("%c%c", 'o'.toLong, 'k'.toLong), "ok")
    assertEquals(rendered("%e %g %g", bits(12345.678), bits(0.0001), bits(1e20)), "1.234568e+04 0.0001 1e+20")
    assertEquals(rendered("%08.3f %+d % d", bits(-3.14159), 5L, 5L), "-003.142 +5  5")
    assertEquals(rendered("%p %p", 0L, 0x1000L), "(nil) 0x1000")
    assertEquals(rendered("%*d|%-*d|%.*f", 6L, 42L, 4L, 7L, 2L, bits(2.5)), "    42|7   |2.50")
    assertEquals(rendered("100%% sure"), "100% sure")
  }

  test("a string argument sits inline, padded to eight bytes") {
    val message = Array(0L) ++ words("[%s] %d") ++ words("abc") ++ Array(9L)
    assertEquals(HostcallPrintf.format(message), ("[abc] 9", 7))
  }

  test("text after a padded string is still printed, as the runtime advances past the string by its printed width") {
    val message = Array(0L) ++ words("[%10s]") ++ words("ab")
    assertEquals(HostcallPrintf.format(message), ("[        ab]", 12))
  }
}
