package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.sass.asm.*
import net.kurobako.gemstone.isa.sass.fixtures.*
import net.kurobako.gemstone.rpc.ExecutionProtocol.*
import net.kurobako.gemstone.vm.*

class LegacyArithmeticSuite extends munit.FunSuite {
  private def fixtureModule: Cubin.Module = SASSFixture.module(
    35,
    Vec(SASSFixture.Kernel("arith_probe", 320, Vec(0 -> 8, 8 -> 8, 16 -> 8, 24 -> 4), registers = 24))
  )

  test("CUDA 11.8 sm_35 64-bit add/sub and signed/unsigned right shifts match arithmetic semantics") {
    val module   = fixtureModule
    val sassText = LegacyArithmeticFixtures.legacyArithmeticSm35
    assert(sassText.contains("IADD R4.CC"))
    assert(sassText.contains("IADD.X R5"))
    assert(sassText.contains("ISUB R6.CC"))
    assert(sassText.contains("ISUB.X R7"))
    assert(sassText.contains("SHF.R.S64"))
    assert(sassText.contains("SHF.R.U64"))
    val function = ListingDecoder.parse(sassText, "arith_probe", 35).fold(fail(_), _.head)
    val kernel   = module.kernels.find(_.name == "arith_probe").get
    val sass     = SASSFixture(module, kernel, Vec(function))
    val cases    = Vec(
      (0L, 0L, 0),
      (0x00000000ffffffffL, 1L, 1),
      (Long.MaxValue, 1L, 1),
      (Long.MinValue, -1L, 31),
      (-1L, Long.MinValue, 32),
      (0x0123456789abcdefL, 0xfedcba9876543210L, 63),
      (0x80000000ffffffffL, 0x7fffffff00000001L, 17)
    )
    locally {
      val service = SASSFixture.Execution()
      val output  = service.allocate(64L.unsafe[Long > 0])
      val program = service.compile(sass)
        .fold(e => fail(e.message), identity)
      cases.foreach { case (a, b, shift) =>
        val task = SASSFixture.task(
          "arith_probe": NonEmpty[String],
          LaunchGeometry.Dimensions.One,
          LaunchGeometry.Dimensions.One,
          Vec(
            KernelTask.Argument.Memory(output, 0L),
            KernelTask.Argument.Scalar(LittleEndian.toVec(a, 8)),
            KernelTask.Argument.Scalar(LittleEndian.toVec(b, 8)),
            KernelTask.Argument.Scalar(LittleEndian.toVec(shift.toLong, 4))
          )
        )
        service.run(program, task).fold(e => fail(e.message), identity)
        val bytes  = service.read(output, 0L, 64).toOption.get
        val actual = (0 until 8).map { i =>
          LittleEndian.decode(bytes, i * 8, 8)
        }.toVec
        assertEquals(
          actual,
          Vec(a + b, a - b, a >> shift, a >>> shift, b >> shift, b >>> shift, -a, -b)
        )
      }
    }
  }

  test("sm_35 SHF.L.U64 and IMUL32I preserve aliased sources across boundary shifts") {
    val module = fixtureModule
    val kernel = module.kernels.find(_.name == "arith_probe").get
    val body   = Vec(
      ("LDC.64", "R4,c[0x0][0x140]"),
      ("MOV", "R0,c[0x0][0x148]"),
      ("MOV", "R1,c[0x0][0x150]"),
      ("MOV", "R2,c[0x0][0x158]"),
      // The wide source is {R1,R0}; exercise both destination/source aliases.
      ("SHF.L.U64", "R0,R0,R2,R1"),
      ("ST.E", "[R4],R0"),
      ("MOV", "R0,c[0x0][0x148]"),
      ("SHF.L.U64", "R1,R0,R2,R1"),
      ("ST.E", "[R4+0x4],R1"),
      ("IMUL32I", "R0,R0,0x500"),
      ("ST.E", "[R4+0x8],R0"),
      ("MOV", "R1,c[0x0][0x148]"),
      ("IMUL32I.U32.HI", "R1,R1,0x500"),
      ("ST.E", "[R4+0xc],R1"),
      ("EXIT", "")
    ).zipWithIndex.map { case ((op, args), index) =>
      SASSFixture.instruction(index * 16, op, args.split(',').filter(_.nonEmpty).toVec, None)
    }
    val sass  = SASSFixture(module, kernel, Vec(DecodedFunction("arith_probe", body)))
    val cases = Vec(
      0x00000000L -> Vec(0, 1, 31, 32, 33, 63, 64, 79),
      0xffffffffL -> Vec(0, 1, 31, 32, 33, 63, 64, 79),
      0x80000000L -> Vec(0, 1, 31, 32, 33, 63, 64, 79),
      0x81234567L -> Vec(0, 1, 31, 32, 33, 63, 64, 79)
    )
    locally {
      val service = SASSFixture.Execution()
      val output  = service.allocate(16L.unsafe[Long > 0])
      val program = service.compile(sass)
        .fold(e => fail(e.message), identity)
      cases.foreach { case (word, shifts) =>
        shifts.foreach { shift =>
          val task = SASSFixture.task(
            "arith_probe": NonEmpty[String],
            LaunchGeometry.Dimensions.One,
            LaunchGeometry.Dimensions.One,
            Vec(
              KernelTask.Argument.Memory(output, 0L),
              KernelTask.Argument.Scalar(LittleEndian.toVec(word, 8)),
              KernelTask.Argument.Scalar(LittleEndian.toVec((~word) & 0xffffffffL, 8)),
              KernelTask.Argument.Scalar(LittleEndian.toVec(shift.toLong, 4))
            )
          )
          service.run(program, task).fold(e => fail(e.message), identity)
          val bytes  = service.read(output, 0L, 16).toOption.get
          val actual = (0 until 4).map(i =>
            LittleEndian.decode(bytes, i * 4, 4)
          ).toVec
          val lo               = word & 0xffffffffL
          val hi               = (~word) & 0xffffffffL
          val wide             = (hi << 32) | lo
          val effectiveShift   = shift.min(64)
          val shiftedHigh      = if (effectiveShift == 64) 0L else (wide << effectiveShift) >>> 32
          val shiftedHighAlias = shiftedHigh
          val productLow       = (lo * 0x500L) & 0xffffffffL
          val productHigh      = ((lo * 0x500L) >>> 32) & 0xffffffffL
          assertEquals(
            actual,
            Vec(shiftedHigh, shiftedHighAlias, productLow, productHigh),
            s"word=0x${word.toHexString}, shift=$shift"
          )
        }
      }
    }
  }

  test("sm_35 ISUB.X consumes the no-borrow carry from a negated IADD.CC") {
    val module       = fixtureModule
    val kernel       = module.kernels.find(_.name == "arith_probe").get
    val instructions = Vec(
      SASSFixture.instruction(0x08, "LDC.64", Vec("R4", "c[0x0][0x140]"), None),
      SASSFixture.instruction(0x10, "MOV", Vec("R10", "c[0x0][0x148]"), None),
      SASSFixture.instruction(0x18, "MOV", Vec("R11", "c[0x0][0x150]"), None),
      SASSFixture.instruction(0x20, "MOV", Vec("R12", "c[0x0][0x158]"), None),
      SASSFixture.instruction(0x28, "IADD", Vec("R13.CC", "-R12", "R10"), None),
      SASSFixture.instruction(0x30, "ISUB.X", Vec("R14", "R11", "R10"), None),
      SASSFixture.instruction(0x38, "ST.E", Vec("[R4]", "R13"), None),
      SASSFixture.instruction(0x40, "ST.E", Vec("[R4+0x4]", "R14"), None),
      SASSFixture.instruction(0x48, "EXIT", Vec.empty, None)
    )
    val sass  = SASSFixture(module, kernel, Vec(DecodedFunction("arith_probe", instructions)))
    val cases = Vec(
      (0L, 0L, 0L),
      (0L, 0L, 1L),
      (1L, 0L, 0L),
      (0L, 1L, 0L),
      (0xffffffffL, 0L, 0xffffffffL),
      (0x7fffffffL, 0x80000000L, 0x80000001L)
    )
    locally {
      val service = SASSFixture.Execution()
      val output  = service.allocate(8L.unsafe[Long > 0])
      val program = service.compile(sass)
        .fold(e => fail(e.message), identity)
      cases.foreach { case (a, b, c) =>
        val task = SASSFixture.task(
          "arith_probe": NonEmpty[String],
          LaunchGeometry.Dimensions.One,
          LaunchGeometry.Dimensions.One,
          Vec(
            KernelTask.Argument.Memory(output, 0L),
            KernelTask.Argument.Scalar(LittleEndian.toVec(b, 8)),
            KernelTask.Argument.Scalar(LittleEndian.toVec(c, 8)),
            KernelTask.Argument.Scalar(LittleEndian.toVec(a, 4))
          )
        )
        service.run(program, task).fold(e => fail(e.message), identity)
        val bytes = service.read(output, 0L, 8).toOption.get
        val words = (0 until 2).map(i =>
          (0 until 4).foldLeft(0L)((value, byte) =>
            value | ((bytes(i * 4 + byte).toLong & 255L) << (byte * 8))
          )
        ).toVec
        val low  = (b - a) & 0xffffffffL
        val high = (c - b - (if (b < a) 1L else 0L)) & 0xffffffffL
        assertEquals(words, Vec(low, high), s"a=$a b=$b c=$c")
      }
    }
  }
}
