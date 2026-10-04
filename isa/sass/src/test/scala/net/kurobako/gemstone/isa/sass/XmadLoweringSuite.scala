package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.sass.asm.*
import net.kurobako.gemstone.isa.sass.fixtures.*
import net.kurobako.gemstone.rpc.ExecutionProtocol.*
import net.kurobako.gemstone.vm.*

class XmadLoweringSuite extends munit.FunSuite {
  private def fixtureModule(sm: Int, signedHi: Boolean = false): Cubin.Module = {
    val name: NonEmpty[String] = if (signedHi) "signed_hi" else "xmad_patterns"
    val registers              = if (signedHi) 9 else 14
    SASSFixture.module(
      sm,
      Vec.nonEmpty(SASSFixture.Kernel(name, 320, Vec.nonEmpty((0, 8), (8, 8)), registers = registers))
    )
  }

  private def words(values: Vec[Long]): Vec[Byte] =
    Vec.from(values.flatMap(value => LittleEndian.toVec(value, 4)))

  private def instruction(address: Int, opcode: String, args: String): DecodedInstruction =
    SASSFixture.instruction(address, opcode, args.split(',').filter(_.nonEmpty).toVec, None)

  private def synthetic(): DecodedFunction = {
    val body = Vec.nonEmpty(
      instruction(0x00, "MOV", "R2,c[0x0][0x148]"),
      instruction(0x08, "MOV", "R3,c[0x0][0x14c]"),
      instruction(0x10, "MOV", "R4,c[0x0][0x140]"),
      instruction(0x18, "MOV", "R5,c[0x0][0x144]"),
      instruction(0x20, "LDG", "R0,[R2]"),
      instruction(0x28, "LDG", "R6,[R2+0x4]"),
      instruction(0x30, "LDG", "R7,[R2+0x8]"),
      instruction(0x38, "XMAD", "R8,R0.H0.reuse,R6.H0,R7.reuse"),
      instruction(0x40, "STG", "[R4],R8"),
      instruction(0x48, "XMAD.PSL", "R8,R0.H1.reuse,R6.H0,R7"),
      instruction(0x50, "STG", "[R4+0x4],R8"),
      instruction(0x58, "XMAD.PSL.CLO", "R8,R0.H1,0x1100,R7"),
      instruction(0x60, "STG", "[R4+0x8],R8"),
      instruction(0x68, "XMAD.PSL.CBCC", "R8,R0.H1.reuse,R6.H1,R7"),
      instruction(0x70, "STG", "[R4+0xc],R8"),
      instruction(0x78, "XMAD.CHI", "R8,R0.H1.reuse,0xab,R7"),
      instruction(0x80, "STG", "[R4+0x10],R8"),
      instruction(0x88, "XMAD.MRG", "R8,R0.H1.reuse,R6.H1.reuse,R7"),
      instruction(0x90, "STG", "[R4+0x14],R8"),
      instruction(0x98, "XMAD.S16.U16", "R8,R0.H1,R6.H0,R7"),
      instruction(0xa0, "STG", "[R4+0x18],R8"),
      instruction(0xa8, "XMAD.U16.S16", "R8,R0.H0,R6.H1,R7"),
      instruction(0xb0, "STG", "[R4+0x1c],R8"),
      // Destination aliases C: the original C value must be read before the store.
      instruction(0xb8, "XMAD.PSL.CBCC", "R7,R0.H1,R6.H1,R7"),
      instruction(0xc0, "STG", "[R4+0x20],R7"),
      // c0[0x8] is the compiler-defined ntid.x value for these legacy targets.
      // Its selected H1 product half is zero; MRG still takes its low 16 bits for the merged high word.
      instruction(0xc8, "XMAD.MRG", "R8,R0.H1,c[0x0][0x8].H1,R7"),
      instruction(0xd0, "STG", "[R4+0x24],R8"),
      instruction(0xd8, "XMAD.S16.S16.CSFU", "R8,R0.H1,R6.H1,RZ"),
      instruction(0xe0, "STG", "[R4+0x28],R8"),
      // The zero register supplies zero to both the multiply half and merged high half.
      instruction(0xe8, "XMAD.MRG", "R8,R0.reuse,RZ.H1,RZ"),
      instruction(0xf0, "STG", "[R4+0x2c],R8"),
      instruction(0xf8, "XMAD.MRG", "R8,R0.H1,RZ,R7"),
      instruction(0x100, "STG", "[R4+0x30],R8"),
      instruction(0x108, "EXIT", "")
    )
    DecodedFunction("xmad_patterns", body)
  }

  private def u32(value: Long): Long                 = value & 0xffffffffL
  private def half(value: Long, high: Boolean): Long = (value >>> (if (high) 16 else 0)) & 0xffffL
  private def signed16(value: Long): Long            = if ((value & 0x8000L) != 0L) value - 0x10000L else value
  private def signed16Big(value: Long): BigInt       =
    if ((value & 0x8000L) != 0L) BigInt(value - 0x10000L) else BigInt(value)
  private def xmadCsfuExpected(a: Long, b: Long): Long = {
    val ah         = signed16Big(half(a, high = true))
    val bh         = signed16Big(half(b, high = true))
    val al         = half(a, high = false)
    val bl         = half(b, high = false)
    val correction = BigInt(65536) *
      ((if (ah < 0 && bl != 0) 1 else 0) + (if (bh < 0 && al != 0) 1 else 0))
    ((ah * bh - correction) & BigInt("ffffffff", 16)).toLong
  }

  private def expected(a: Long, b: Long, c: Long): Vec[Long] = {
    val al = half(a, high = false)
    val ah = half(a, high = true)
    val bl = half(b, high = false)
    val bh = half(b, high = true)
    val p0 = al * bl
    val p1 = ah * bl
    val p2 = ah * bh
    Vec.nonEmpty(
      u32(p0 + c),
      u32((p1 << 16) + c),
      u32((ah * 0x1100L << 16) + (c & 0xffffL)),
      u32((p2 << 16) + c + (b << 16)),
      u32(ah * 0xabL + (c >>> 16)),
      u32((u32(p2 + c) & 0xffffL) | (b << 16)),
      u32(signed16(ah) * bl + c),
      u32(al * signed16(bh) + c),
      u32((p2 << 16) + c + (b << 16)),
      u32((c & 0xffffL) | (1L << 16)),
      xmadCsfuExpected(a, b),
      0L,
      c & 0xffffL
    )
  }

  private def expectedSource(a: Long, b: Long, c: Long): Vec[Long] = {
    val al      = half(a, high = false)
    val bl      = half(b, high = false)
    val product = al * bl
    Vec.nonEmpty(
      u32(product + c),
      u32((product << 16) + c),
      u32((product << 16) + (c & 0xffffL)),
      u32((product << 16) + c + (b << 16)),
      u32(product + (c >>> 16)),
      u32((product + c) & 0xffffL) | u32(b << 16),
      u32(signed16(al) * bl + c)
    )
  }

  test("CUDA 11.8 compiler fixtures cover Maxwell/Pascal XMAD families; VM arithmetic matches independent formulas") {
    val cases = Vec.nonEmpty(
      (0x8001ffffL, 0xffff8001L, 0xffff0003L),
      (0x1234abcdL, 0x5678fedcL, 0x76543210L),
      (0x00010001L, 0x00020003L, 0x0000ffffL),
      (0x0000ffffL, 0x80000001L, 0x76543210L),
      (0xffff1234L, 0x0000ffffL, 0x80000000L),
      (0x80018001L, 0x0001ffffL, 0x00000000L),
      (0xffff0000L, 0x00010000L, 0x00000000L),
      (0xffffffffL, 0x00000001L, 0x00000000L)
    )

    Vec.nonEmpty(50, 60).foreach { sm =>
      val module = fixtureModule(sm)
      assertEquals(module.sm.raw, sm)
      val listing = Map(50 -> XmadFixtures.xmadCompilerFormsSm50, 60 -> XmadFixtures.xmadArithmeticSm60)(sm)
      Vec.nonEmpty("XMAD.MRG", "XMAD.CHI", "XMAD.PSL.CBCC", "XMAD.S16.U16").foreach { form =>
        assert(listing.contains(form), s"sm_$sm compiler fixture did not contain $form")
      }
      val kernel = module.kernels.find(_.name == "xmad_patterns").get
      val sass   = SASSFixture(module, kernel, Vec.nonEmpty(synthetic()))

      locally {
        val service = SASSFixture.Execution()
        val output  = service.allocate(52L)
        val input   = service.allocate(12L)
        val program = service.compile(sass)
          .fold(e => fail(e.message), identity)
        cases.foreach { case (a, b, c) =>
          assert(service.write(input, 0L, words(Vec.nonEmpty(a, b, c))).isRight)
          val task = SASSFixture.task(
            "xmad_patterns": NonEmpty[String],
            LaunchGeometry.Dimensions.One,
            LaunchGeometry.Dimensions.One,
            Vec.nonEmpty(
              KernelTask.Argument.Memory(output, 0L),
              KernelTask.Argument.Memory(input, 0L)
            )
          )
          service.run(program, task).fold(e => fail(e.message), identity)
          val bytes  = service.read(output, 0L, 52).toOption.get
          val actual = (0 until 13).map(i =>
            LittleEndian.decode(bytes, i * 4, 4)
          ).toVec
          assertEquals(
            actual,
            expected(a, b, c),
            s"sm_$sm a=0x${a.toHexString}, b=0x${b.toHexString}, c=0x${c.toHexString}"
          )
        }
      }
    }
  }

  test("XMAD rejects unsupported modifiers and condition-code modifier combinations") {
    val module = fixtureModule(50)
    val kernel = module.kernels.find(_.name == "xmad_patterns").get
    val base   = synthetic()
    val badMod = base.copy(instructions = base.instructions.updated(7, instruction(0x38, "XMAD.SAT", "R8,R0,R6,R7")))
    val badCC  = base.copy(instructions = base.instructions.updated(7, instruction(0x38, "XMAD.PSL", "R8.CC,R0,R6,R7")))
    assert(SASSFixture.lower(module, kernel, Vec.nonEmpty(badMod)).isLeft)
    assert(SASSFixture.lower(module, kernel, Vec.nonEmpty(badCC)).isLeft)
  }

  test("CUDA 11.8 signed mul.hi.s32 compiler chains match independent BigInt arithmetic") {
    val boundaries = Vec.nonEmpty(0L, 1L, 0x7fffL, 0x8000L, 0xffffL, 0x10000L, 0x7fff0000L,
      0x7fffffffL, 0x80000000L, 0x80000001L, 0xffff0000L, 0xffffffffL)
    val cases =
      (for {
        a <- boundaries
        b <- boundaries
      } yield (a, b)) ++ Vec.nonEmpty(
        (0x80000000L, 0x80000000L),
        (0x7fffffffL, 0xffffffffL),
        (0xffff0001L, 0x0001ffffL),
        (0x12345678L, 0x87654321L),
        (0xffffffffL, 0xffffffffL),
        (0x00000000L, 0x80000000L),
        (0xffff0000L, 0x00010000L),
        (0xffffffffL, 0x00000001L)
      )
    Vec.nonEmpty(50, 60).foreach { sm =>
      val module = fixtureModule(sm, signedHi = true)
      assertEquals(module.sm.raw, sm)
      val listing = Map(50 -> XmadFixtures.xmadSignedHiSm50, 60 -> XmadFixtures.xmadSignedHiSm60)(sm)
      assert(listing.contains("XMAD.S16.S16.CSFU"), s"sm_$sm mul.hi.s32 did not emit CSFU")
      assert(listing.contains("IADD3.RS"), s"sm_$sm mul.hi.s32 fixture lost the final partial-product combine")
      val function = ListingDecoder.parse(listing, "signed_hi", sm).fold(fail(_), _.head)
      val kernel   = module.kernels.find(_.name == "signed_hi").get
      val sass     = SASSFixture(module, kernel, Vec.nonEmpty(function))

      locally {
        val service = SASSFixture.Execution()
        val output  = service.allocate(4L)
        val input   = service.allocate(8L)
        val program = service.compile(sass)
          .fold(e => fail(e.message), identity)
        cases.foreach { case (a, b) =>
          assert(service.write(input, 0L, words(Vec.nonEmpty(a, b))).isRight)
          val task = SASSFixture.task(
            "signed_hi": NonEmpty[String],
            LaunchGeometry.Dimensions.One,
            LaunchGeometry.Dimensions.One,
            Vec.nonEmpty(
              KernelTask.Argument.Memory(output, 0L),
              KernelTask.Argument.Memory(input, 0L)
            )
          )
          service.run(program, task).fold(e => fail(e.message), identity)
          val bytes  = service.read(output, 0L, 4).toOption.get
          val actual = (0 until 4).foldLeft(0L)((value, byte) =>
            value | ((bytes(byte).toLong & 0xffL) << (byte * 8))
          )
          val expected = ((BigInt(a.toInt) * BigInt(b.toInt)) >> 32) & BigInt("ffffffff", 16)
          assertEquals(actual, expected.toLong, s"sm_$sm a=0x${a.toHexString}, b=0x${b.toHexString}")
        }
      }
    }
  }

  test("CUDA 11.8 compiled PTX multiply-add chains match their source arithmetic in the VM") {
    val cases = Vec.nonEmpty(
      (0x8001ffffL, 0xffff8001L, 0xffff0003L),
      (0x1234abcdL, 0x5678fedcL, 0x76543210L),
      (0x00010001L, 0x00020003L, 0x0000ffffL)
    )
    Vec.nonEmpty(50, 60).foreach { sm =>
      val module = fixtureModule(sm)
      assertEquals(module.sm.raw, sm)
      val listing  = Map(50 -> XmadFixtures.xmadArithmeticSm50, 60 -> XmadFixtures.xmadArithmeticSm60)(sm)
      val function = ListingDecoder.parse(listing, "xmad_patterns", sm).fold(fail(_), _.head)
      val kernel   = module.kernels.find(_.name == "xmad_patterns").get
      val sass     = SASSFixture(module, kernel, Vec.nonEmpty(function))

      locally {
        val service = SASSFixture.Execution()
        val output  = service.allocate(28L)
        val input   = service.allocate(12L)
        val program = service.compile(sass)
          .fold(e => fail(e.message), identity)
        cases.foreach { case (a, b, c) =>
          assert(service.write(input, 0L, words(Vec.nonEmpty(a, b, c))).isRight)
          val task = SASSFixture.task(
            "xmad_patterns": NonEmpty[String],
            LaunchGeometry.Dimensions.One,
            LaunchGeometry.Dimensions.One,
            Vec.nonEmpty(
              KernelTask.Argument.Memory(output, 0L),
              KernelTask.Argument.Memory(input, 0L)
            )
          )
          service.run(program, task).fold(e => fail(e.message), identity)
          val bytes  = service.read(output, 0L, 28).toOption.get
          val actual = (0 until 7).map(i =>
            LittleEndian.decode(bytes, i * 4, 4)
          ).toVec
          assertEquals(actual, expectedSource(a, b, c), s"sm_$sm compiled PTX inputs")
        }
      }
    }
  }
}
