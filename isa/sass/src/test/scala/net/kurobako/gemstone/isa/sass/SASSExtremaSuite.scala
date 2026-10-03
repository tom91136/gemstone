package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.sass.asm.*
import net.kurobako.gemstone.isa.sass.fixtures.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.rpc.ExecutionProtocol.*
import net.kurobako.gemstone.vm.*

class SASSExtremaSuite extends munit.FunSuite {
  private def fixtureModule: Cubin.Module = SASSFixture.module(
    89,
    Vec(SASSFixture.Kernel("extrema", 352, Vec((0, 8), (8, 8), (16, 8), (24, 8)), registers = 17))
  )

  test("compiler-produced sm_89 DSETP.MAX/MIN preserve NaN and signed-zero selection") {
    val module = fixtureModule
    val sass   = ExtremaFixtures.extremaSm89
    assert(sass.contains("DSETP.MAX.AND"))
    assert(sass.contains("DSETP.MIN.AND"))
    val functions = ListingDecoder.parse(sass, "extrema", 89).fold(fail(_), identity)
    val source    = SASSFixture(module, module.kernels.head, functions)

    val quietA         = 0x7ff8000000001234L
    val quietB         = 0x7ff8000000004321L
    val quietBothA     = 0x7ff80000000000a5L
    val signalingBothB = 0xfff00000000000bcL
    val cases          = Vec(
      (3.0, 2.0, 3.0, 2.0),
      (-2.0, -3.0, -2.0, -3.0),
      (0.0, -0.0, 0.0, -0.0),
      (-0.0, 0.0, 0.0, -0.0),
      (java.lang.Double.longBitsToDouble(quietA), 5.0, 5.0, 5.0),
      (5.0, java.lang.Double.longBitsToDouble(quietB), 5.0, 5.0),
      (Double.PositiveInfinity, 42.0, Double.PositiveInfinity, 42.0),
      (Double.NegativeInfinity, 42.0, 42.0, Double.NegativeInfinity),
      (
        java.lang.Double.longBitsToDouble(quietBothA),
        java.lang.Double.longBitsToDouble(signalingBothB),
        java.lang.Double.longBitsToDouble(signalingBothB | 0x0008000000000000L),
        java.lang.Double.longBitsToDouble(signalingBothB | 0x0008000000000000L)
      )
    )
    def bits(value: Double): Long = java.lang.Double.doubleToRawLongBits(value)
    val aData                     = Vec.from(cases).flatMap(c => LittleEndian.toVec(bits(c._1), 8))
    val bData                     = Vec.from(cases).flatMap(c => LittleEndian.toVec(bits(c._2), 8))
    val expectedMax               = cases.map(c => bits(c._3))
    val expectedMin               = cases.map(c => bits(c._4))

    locally {
      val service      = SASSFixture.Execution()
      val bytes        = cases.size * 8
      def allocation() =
        service.allocate(bytes.toLong.unsafe[Long > 0])
      val a = allocation(); val b = allocation(); val max = allocation(); val min = allocation()
      assert(service.write(a, 0L, aData).isRight)
      assert(service.write(b, 0L, bData).isRight)
      val program = service.compile(source)
        .fold(e => fail(e.message), identity)
      def pointer(allocation: AllocationId) =
        KernelTask.Argument.Memory(allocation, 0L)
      val task = SASSFixture.task(
        "extrema": NonEmpty[String],
        LaunchGeometry.Dimensions(16, 1, 1),
        LaunchGeometry.Dimensions.One,
        Vec(pointer(a), pointer(b), pointer(max), pointer(min))
      )
      service.run(program, task).fold(e => fail(e.message), identity)
      def read(allocation: AllocationId): Vec[Long] = {
        val data = service.read(allocation, 0L, bytes.unsafe[Int >= 0]).toOption.get
        cases.indices.map { i =>
          (0 until 8).foldLeft(0L)((value, byte) => value | ((data(i * 8 + byte) & 255L) << (byte * 8)))
        }.toVec
      }
      assertEquals(read(max), expectedMax)
      assertEquals(read(min), expectedMin)
    }
  }
}
