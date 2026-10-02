package net.kurobako.gemstone.isa.sass

import cats.syntax.all.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.sass.asm.*
import net.kurobako.gemstone.isa.sass.fixtures.*
import net.kurobako.gemstone.rpc.ExecutionProtocol.*
import net.kurobako.gemstone.vm.*

class CubinSuite extends munit.FunSuite {
  for {
    sm        <- SaxpyFixtures.code.keys.toVec.sorted
    generated <- Vec(false, true)
  }
    test(
      s"compiler-produced sm_$sm SAXPY executes predicated tails and aliased register pairs (generated=$generated)"
    ) {
      val m         = CubinSuiteFixtures.saxpyModule(sm)
      val functions = if (generated)
        SASSCodeWords.words(FixtureBytes.readHex(SaxpyFixtures.code(sm)), sm).flatMap(_.traverse {
          case (address, bytes) =>
            InstructionDecoder.decodeTyped(address, bytes, s"SM$sm")
        }).map(instructions => Vec(DecodedFunction("saxpy", instructions))).fold(fail(_), identity)
      else ListingDecoder.parse(
        SaxpyFixtures.listings(sm),
        "saxpy",
        sm
      ).fold(fail(_), identity)
      val source = SASSFixture(m, m.kernels.head, functions)
      val count  = 97
      locally {
        val service                               = SASSFixture.Execution()
        def floats(values: Vec[Float]): Vec[Byte] = Vec.from(values).flatMap(f =>
          LittleEndian.toVec(java.lang.Float.floatToRawIntBits(f).toLong, 4)
        )
        val x       = service.allocate(floats(Vec.tabulate(count)(i => (i - 40).toFloat)))
        val y       = service.allocate(floats(Vec.tabulate(count)(i => (i % 7).toFloat)))
        val program = service.compile(source).fold(e => fail(e.message), identity)
        val task    = SASSFixture.task(
          "saxpy": NonEmpty[String],
          LaunchGeometry.Dimensions(32, 1, 1),
          LaunchGeometry.Dimensions(4, 1, 1),
          Vec(
            KernelTask.Argument.Memory(y, 0L),
            KernelTask.Argument.Memory(x, 0L),
            KernelTask.Argument.Scalar(floats(Vec(2f))),
            KernelTask.Argument.Scalar(LittleEndian.toVec(count.toLong, 4))
          )
        )
        service.run(program, task).fold(e => fail(e.message), identity)
        val expected = (0 until count).map(i => (2 * (i - 40) + i % 7).toFloat)
          .map(f => java.lang.Float.floatToRawIntBits(f).toLong & 0xffffffffL).toVec
        assertEquals(service.readWords(y, count.unsafe[Int >= 0]), expected)
      }
    }

  test("compiler-emitted HMMA GEMM uses real SASS fragments across blocks and warps") {
    val m = SASSFixture.module(
      86,
      Vec(SASSFixture.Kernel(
        "_ZN11cuda_tensor11tensor_gemmEPK6__halfS2_Pf",
        352,
        Vec(0 -> 8, 8 -> 8, 16 -> 8),
        registers = 26,
        shared = 0
      ))
    )
    val k            = m.kernels.head
    val instructions = ListingDecoder.parse(
      TensorFixtures.tensorSm86,
      k.name
    ).fold(fail(_), identity)
    val source                                  = SASSFixture(m, k, instructions)
    def a(tile: Int, row: Int, col: Int): Float = ((row + 3 * col + tile) % 7 - 3).toFloat
    def b(row: Int, col: Int): Float            = ((2 * row + col)        % 5 - 2).toFloat
    def floats(values: Vec[Float]): Vec[Byte]   = Vec.from(values).flatMap(v =>
      LittleEndian.toVec(java.lang.Float.floatToRawIntBits(v).toLong, 4)
    )
    def floatWords(values: Vec[Float]): Vec[Long] = Vec.from(values).map(v =>
      java.lang.Float.floatToRawIntBits(v).toLong & 0xffffffffL
    )
    val av       = (0 until 2048).map(i => if (i % 32 < 16) a(i / 512, (i % 512) / 32, i % 32) else 0f)
    val bv       = (0 until 2048).map(i => if (i % 32 < 16) b(i % 32, (i % 512) / 32) else 0f)
    val cv       = (0 until 2048).map(i => if (i % 32 < 16) ((i % 512) / 32 - i % 32).toFloat else -1234f).toVec
    val expected = cv.zipWithIndex.map { (initial, i) =>
      val row = i % 512 / 32
      val col = i % 32
      if (col >= 16) initial
      else 2f * (0 until 16).foldLeft(initial)((sum, n) => sum + a(i / 512, row, n) * b(n, col))
    }
    locally {
      val service     = SASSFixture.Execution()
      val aBytes      = Vec.from(av).flatMap(v => LittleEndian.toVec(IEEE754.floatToBinary16(v).raw.toLong, 2))
      val bBytes      = Vec.from(bv).flatMap(v => LittleEndian.toVec(IEEE754.floatToBinary16(v).raw.toLong, 2))
      val allocations = Vec(
        service.allocate(aBytes),
        service.allocate(bBytes),
        service.allocate(floats(cv))
      )
      val program = service.compile(source).fold(e => fail(e.message), identity)
      val task    = SASSFixture.task(
        k.name.unsafe[NonEmpty[String]],
        LaunchGeometry.Dimensions(64, 1, 1),
        LaunchGeometry.Dimensions(2, 1, 1),
        Vec.from(allocations.map(a => KernelTask.Argument.Memory(a, 0L)))
      )
      service.run(program, task).fold(e => fail(e.message), identity)
      assertEquals(service.readWords(allocations(2), expected.size.unsafe[Int >= 0]), floatWords(expected))
    }
  }
}
