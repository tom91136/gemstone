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

class Hsetp2DualOutputSuite extends munit.FunSuite {
  {
    def fixtureModule(sm: Int): Cubin.Module =
      SASSFixture.module(sm, Vec(SASSFixture.Kernel("hsetp2_two", 352, Vec(0 -> 8, 8 -> 8, 16 -> 8), registers = 14)))

    val comparisonCases = Vec(
      (0x38003c00, 0x3c003800), // low true, high false
      (0x3c003800, 0x38003c00), // low false, high true
      (0x00007e01, 0x3c000000), // low NaN gives unordered GEU true
      (0x3c000000, 0x7e010000), // high NaN gives unordered GEU true
      (0x7e017e01, 0x3c003c00), // both NaN
      (0x80000000, 0x00008000), // signed zeros compare equal
      (0x00010000, 0x00000001), // low zero < minimum subnormal; high subnormal > zero
      (0x7c003c00, 0x7e017c00), // low finite < infinity; high infinity vs NaN
      (0x00003c00, 0x00003c00)  // exact equality
    )

    def expected(left: Int, right: Int, duplicateLeftLow: Boolean): (Long, Long) = {
      def geu(a: Int, b: Int): Boolean = {
        def decode(bits: Int): Float = IEEE754.binary16ToFloat(Binary16.low(bits.toLong))
        val av                       = decode(a)
        val bv                       = decode(b)
        av.isNaN || bv.isNaN || av >= bv
      }
      val lhsLow  = left & 0xffff
      val lhsHigh = if (duplicateLeftLow) lhsLow else (left >>> 16) & 0xffff
      val rhsLow  = right & 0xffff
      val rhsHigh = (right >>> 16) & 0xffff
      (if (geu(lhsLow, rhsLow)) 1L else 0L, if (geu(lhsHigh, rhsHigh)) 1L else 0L)
    }

    def run(sm: Int, duplicateLeftLow: Boolean, rejectPin: Boolean = false): Either[String, Vec[Long]] = {
      val module   = fixtureModule(sm)
      val sassText = sm match {
        case 70    => Hsetp2Fixtures.hsetp2TwoHsetp2TwoSm70
        case 89    => Hsetp2Fixtures.hsetp2TwoHsetp2TwoSm89
        case other => fail(s"unsupported HSETP2 fixture sm_$other")
      }
      val parsed   = ListingDecoder.parse(sassText, "hsetp2_two", sm).fold(fail(_), _.head)
      val function = parsed.copy(instructions = parsed.instructions.map { instruction =>
        if (instruction.mnemonic.raw == "HSETP2") {
          val operands = instruction.operands.updated(
            2,
            SASSOperandParser.parse(if (duplicateLeftLow) "R2.H0_H0" else "R2").fold(fail(_), identity)
          ).updated(4, SASSOperandParser.parse(if (rejectPin) "P2" else "PT").fold(fail(_), identity))
          instruction.rewrite(operands = operands)
        } else instruction
      })
      val lowered = SASSFixture.lower(module, module.kernels.find(_.name == "hsetp2_two").get, Vec(function))
      if (rejectPin) lowered.map(_ => Vec.empty)
      else {
        val sass   = lowered.fold(fail(_), identity)
        val left   = comparisonCases.map(_._1.toLong)
        val right  = comparisonCases.map(_._2.toLong)
        val inputs =
          Vec(left, right).map(values => Vec.from(values).flatMap(LittleEndian.toVec(_, 4)))

        locally {
          val service     = SASSFixture.Execution()
          val output      = service.allocate((64 * 4L).unsafe[Long > 0])
          val allocations = inputs.map(bytes =>
            service.allocate(bytes.size.toLong.unsafe[Long > 0])
          )
          service.write(
            output,
            0L,
            Vec.fill(64 * 4)(0.toByte)
          ).fold(error => fail(error.message), identity)
          allocations.zip(inputs).foreach { case (allocation, bytes) =>
            service.write(allocation, 0L, bytes).fold(error => fail(error.message), identity)
          }
          val program = service.compile(sass)
            .fold(error => fail(error.message), identity)
          val task = SASSFixture.task(
            "hsetp2_two": NonEmpty[String],
            LaunchGeometry.Dimensions(comparisonCases.size.unsafe[Int > 0], 1, 1),
            LaunchGeometry.Dimensions.One,
            Vec(
              KernelTask.Argument.Memory(output, 0L),
              KernelTask.Argument.Memory(allocations(0), 0L),
              KernelTask.Argument.Memory(allocations(1), 0L)
            )
          )
          service.run(program, task).fold(error => fail(error.message), identity)
          val actual = service.readWords(output, 64.unsafe[Int >= 0])
          Right(actual)
        }
      }
    }

    test("dual-output HSETP2.GEU maps low and high half comparisons independently") {
      val actual        = run(sm = 89, duplicateLeftLow = false).fold(fail(_), identity)
      val expectedWords = Array.fill[Long](64)(0L)
      comparisonCases.zipWithIndex.foreach { case ((left, right), lane) =>
        val (low, high) = expected(left, right, duplicateLeftLow = false)
        expectedWords(lane) = low
        expectedWords(32 + lane) = high
      }
      assertEquals(actual, expectedWords.toVec)
    }

    test("dual-output HSETP2.GEU H0_H0 duplicates the left low half into both comparisons") {
      val actual        = run(sm = 89, duplicateLeftLow = true).fold(fail(_), identity)
      val expectedWords = Array.fill[Long](64)(0L)
      comparisonCases.zipWithIndex.foreach { case ((left, right), lane) =>
        val (low, high) = expected(left, right, duplicateLeftLow = true)
        expectedWords(lane) = low
        expectedWords(32 + lane) = high
      }
      assertEquals(actual, expectedWords.toVec)
    }

    test("dual-output HSETP2 rejects an unverified predicate input") {
      assert(run(sm = 89, duplicateLeftLow = false, rejectPin = true).isLeft)
    }

    test("CUDA 11.8 sm_70 compiler form preserves both outputs in the shared NVIDIA VM") {
      val actual        = run(sm = 70, duplicateLeftLow = false).fold(fail(_), identity)
      val expectedWords = Array.fill[Long](64)(0L)
      comparisonCases.zipWithIndex.foreach { case ((left, right), lane) =>
        val (low, high) = expected(left, right, duplicateLeftLow = false)
        expectedWords(lane) = low
        expectedWords(32 + lane) = high
      }
      assertEquals(actual, expectedWords.toVec)
    }
  }

}
