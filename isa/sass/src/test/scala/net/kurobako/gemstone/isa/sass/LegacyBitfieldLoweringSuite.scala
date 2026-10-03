package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.sass.asm.*
import net.kurobako.gemstone.isa.sass.fixtures.*
import net.kurobako.gemstone.rpc.ExecutionProtocol.*
import net.kurobako.gemstone.vm.*

class LegacyBitfieldLoweringSuite extends munit.FunSuite {
  private def fixtureModule: Cubin.Module = SASSFixture.module(
    35,
    Vec(SASSFixture.Kernel(
      "bitfield_probe",
      320,
      Vec((0, 8), (8, 4), (12, 4), (16, 4), (20, 4), (24, 4)),
      registers = 8
    ))
  )

  private def extract(value: Int, position: Int, width: Int, signed: Boolean): Int =
    if (width == 0) 0
    else if (position >= 32) { if (signed && value < 0) -1 else 0 }
    else {
      val actualWidth = math.min(width, 32 - position)
      val mask        = if (actualWidth == 32) -1 else (1 << actualWidth) - 1
      val bits        = (value >>> position) & mask
      if (signed && actualWidth < 32 && (bits & (1 << (actualWidth - 1))) != 0)
        bits | ~mask
      else bits
    }

  private def insert(base: Int, value: Int, position: Int, width: Int): Int = {
    val actualWidth = if (position >= 32) 0 else math.min(width, 32 - position)
    if (actualWidth == 0) base
    else {
      val mask = if (actualWidth == 32) -1 else ((1 << actualWidth) - 1) << position
      (base & ~mask) | ((value << position) & mask)
    }
  }

  test("compiler-produced sm_35 BFE.BREV, BFE and BFI match bit-field edge semantics") {
    val module   = fixtureModule
    val sassText = BitfieldFixtures.bitfieldSm35
    assert(sassText.contains("BFE.U32.BREV"))
    assert(sassText.contains("BFI R6, R6, R0"))
    val function = ListingDecoder.parse(sassText, "bitfield_probe", 35).fold(fail(_), _.head)
    val lowered  = SASSFixture(module, module.kernels.head, Vec(function))
    val scalar   = (value: Int) =>
      KernelTask.Argument.Scalar(
        LittleEndian.toVec(value.toLong, 4)
      )

    locally {
      val service = SASSFixture.Execution()
      val output  = service.allocate(16L)
      val program = service.compile(lowered)
        .fold(e => fail(e.message), identity)
      val inputs = Vec(
        (0xe1234567, 0x89abcdef, 0x35, 0, 32),
        (0xe1234567, 0x89abcdef, 0x35, 1, 1),
        (0xe1234567, 0x89abcdef, 0x35, 29, 8),
        (0xe1234567, 0x89abcdef, 0x35, 31, 8),
        (0xe1234567, 0x89abcdef, 0x35, 8, 0),
        (0xe1234567, 0x89abcdef, 0x35, 32, 1),
        (0xe1234567, 0x89abcdef, 0x35, 255, 8),
        (0x61234567, 0x89abcdef, 0x35, 32, 8)
      )
      inputs.foreach { case (x, base, inserted, position, width) =>
        val task = SASSFixture.task(
          "bitfield_probe": NonEmpty[String],
          LaunchGeometry.Dimensions.One,
          LaunchGeometry.Dimensions.One,
          Vec(
            KernelTask.Argument.Memory(output, 0L),
            scalar(x),
            scalar(base),
            scalar(inserted),
            scalar(position),
            scalar(width)
          )
        )
        service.run(program, task).fold(e => fail(e.message), identity)
        val bytes                   = service.read(output, 0L, 16).toOption.get
        def intAt(offset: Int): Int = (bytes(offset) & 255) | ((bytes(offset + 1) & 255) << 8) |
          ((bytes(offset + 2) & 255) << 16) | (bytes(offset + 3) << 24)
        assertEquals(intAt(0), extract(x, position, width, signed = false))
        assertEquals(intAt(4), extract(x, position, width, signed = true))
        assertEquals(intAt(8), insert(base, inserted, position, width))
        assertEquals(intAt(12), java.lang.Integer.reverse(x))
      }
    }
  }
}
