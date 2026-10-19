package net.kurobako.gemstone.isa.ptx

import execution.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.nvidia.*
import net.kurobako.gemstone.isa.nvidia.VMIR.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchGeometry.*
import scala.collection.mutable

class WideDescriptorSuite extends munit.FunSuite {
  private val values = (0 until 4096).map { index =>
    val x = (index * 17 + 11) % 23 - 11; x * x
  }
  private val first     = values.take(896).sum.toDouble
  private val partial   = values.slice(896, 1792).sum.toDouble
  private val inclusive = first + partial
  private val current   = values.slice(1792, 2688).sum.toDouble
  private val expected  = values.take(2688).sum.toDouble
  private val geometry  = LaunchGeometry.forLaunch(
    Dimensions.One,
    Dimensions(3, 1, 1)
  ).toOption.get
  private def bits(value: Double): Long      = java.lang.Double.doubleToRawLongBits(value)
  private def literal(value: Double): String = "0d" + java.lang.Long.toHexString(bits(value))

  // Interleaving controls and observations are confined to this final test fixture.
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private class DescriptorMemory extends DeviceMemory(64, GuestAddress(0x10000000000L)) {
    val base                                          = start
    var transition                                    = false
    val observed: mutable.ArrayBuffer[(Long, Double)] = mutable.ArrayBuffer.empty[(Long, Double)]
    def set(status: Long, payload: Double): Unit      = {
      writeLittleEndian(base, status, 8).toOption.get
      writeLittleEndian(base.offset(8), bits(payload), 8).toOption.get
    }
    private def afterRead(address: GuestAddress): Unit = if (transition && address == base) {
      transition = false
      set(101, inclusive)
    }
    private def observe(): Unit = observed += (
      readLittleEndian(base, 8).toOption.get ->
        java.lang.Double.longBitsToDouble(readLittleEndian(base.offset(8), 8).toOption.get)
    )
    override def readValue(address: GuestAddress, size: Int in 1 ~ 8): Either[Fault, Long] = {
      val result = readLittleEndian(address, size)
      afterRead(address)
      result
    }
    override def writeValue(address: GuestAddress, value: Long, size: Int in 1 ~ 8): Either[Fault, Unit] = {
      val result = writeLittleEndian(address, value, size)
      observe()
      result
    }
    override def read(address: GuestAddress, size: Int >= 0): Either[Fault, Vec[Byte]] = {
      val result = readBytes(address, size)
      afterRead(address)
      result
    }
    override def write(address: GuestAddress, bytes: Vec[Byte]): Either[Fault, Unit] = {
      val result = writeBytes(address, bytes)
      observe()
      result
    }
  }

  test("three observers never combine PARTIAL status with an INCLUSIVE double prefix") {
    assertEquals(first, 39432.0)
    assertEquals(expected, 118222.0)
    val memory = DescriptorMemory()
    memory.set(100, partial)
    memory.transition = true
    val source = s""".version 8.0
      .target sm_80
      .address_size 64
      .visible .entry observe(.param .u64 memory) {
        .reg .pred %p;
        .reg .u32 %r;
        .reg .u64 %rd<5>;
        .reg .f64 %fd;
        ld.param.u64 %rd0, [memory];
        ld.relaxed.gpu.v2.u64 {%rd1, %rd2}, [%rd0];
        mov.b64 %fd, %rd2;
        setp.eq.u64 %p, %rd1, 100;
        @%p add.f64 %fd, %fd, ${literal(first)};
        add.f64 %fd, %fd, ${literal(current)};
        mov.u32 %r, %ctaid.x;
        mul.wide.u32 %rd3, %r, 8;
        add.u64 %rd4, %rd0, %rd3;
        st.global.f64 [%rd4+32], %fd;
        ret;
      }"""
    val kernel = compileKernel(source)
    val result = VMInterpreter.execute(
      kernel,
      geometry,
      Map(("memory": NonEmpty[String]) -> Argument.fromLong(memory.base.raw)),
      memory
    )
    assert(result.isRight, result.toString)
    (0 until 3).foreach { group =>
      val actual = java.lang.Double.longBitsToDouble(memory.readLittleEndian(
        memory.base.offset(32 + group * 8L),
        8
      ).toOption.get)
      assertEquals(actual, expected, s"group $group must not double-count first tile $first")
    }
  }

  test("three publications preserve both words of a double scan descriptor") {
    val memory = DescriptorMemory()
    memory.set(100, partial)
    val source = s""".version 8.0
      .target sm_80
      .address_size 64
      .visible .entry publish(.param .u64 memory) {
        .reg .u64 %rd<3>;
        ld.param.u64 %rd0, [memory];
        mov.u64 %rd1, 101;
        mov.u64 %rd2, ${bits(inclusive)};
        st.relaxed.gpu.v2.u64 [%rd0], {%rd1, %rd2};
        ret;
      }"""
    val kernel = compileKernel(source)
    val result = VMInterpreter.execute(
      kernel,
      geometry,
      Map(("memory": NonEmpty[String]) -> Argument.fromLong(memory.base.raw)),
      memory
    )
    assert(result.isRight, result.toString)
    assert(memory.observed.nonEmpty)
    assert(
      memory.observed.forall(value => value == (100L -> partial) || value == (101L -> inclusive)),
      memory.observed.toString
    )
  }
}
