package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.sass.asm.*
import net.kurobako.gemstone.isa.sass.fixtures.*
import net.kurobako.gemstone.rpc.ExecutionProtocol.*
import net.kurobako.gemstone.vm.*

class SgxtLoweringSuite extends munit.FunSuite {
  private def fixtureModule: Cubin.Module =
    SASSFixture.module(70, Vec(SASSFixture.Kernel("sgxt_probe", 352, Vec((0, 8), (8, 4), (12, 4)), registers = 14)))

  private def scalar(value: Int): KernelTask.Argument.Scalar =
    KernelTask.Argument.Scalar(LittleEndian.toVec(value.toLong, 4))

  private def extract(value: Int, rawWidth: Int, signed: Boolean, rawSgxtWidth: Boolean): Int = {
    val width = if (rawSgxtWidth) {
      if (java.lang.Integer.compareUnsigned(rawWidth, 32) >= 0) 32 else rawWidth
    } else rawWidth & 0xff
    if (width == 0) 0
    else if (width >= 32) value
    else {
      val mask = (1 << width) - 1
      val bits = value & mask
      if (signed && (bits & (1 << (width - 1))) != 0) bits | ~mask else bits
    }
  }

  private val inputs = Vec(
    (0x81234567, 0),
    (0xffffffff, 1),
    (0x7fffffff, 2),
    (0x80000000, 7),
    (0x87654321, 8),
    (0xffffffff, 15),
    (0x80000001, 16),
    (0xdeadbeef, 31),
    (0x80000000, 32),
    (0x87654321, 33),
    (0xffffffff, 63),
    (0x12345678, 64),
    (0xfedcba98, 255),
    (0x87654321, 256),
    (0x80000001, 257),
    (0x12345678, 0x80000000),
    (0xffffffff, -1)
  )

  private def runFixture(
      sassText: String,
      rawSgxtWidth: Boolean,
      immediateWidth: Option[Int] = None
  ): Unit = {
    val module = fixtureModule
    assertEquals(module.sm.raw, 70)
    val parsed   = ListingDecoder.parse(sassText, "sgxt_probe", 70).fold(fail(_), _.head)
    val function = immediateWidth.fold(parsed) { width =>
      parsed.copy(instructions = parsed.instructions.map { instruction =>
        val immediate = (instruction.operation match { case _: Instruction.Sgxt => true; case _ => false }) && (
          (instruction.modifiers == Vec(Instruction.Modifier.U32) &&
            instruction.operands(2) == SASSFixture.operand("0x2")) ||
            (instruction.modifiers.isEmpty && instruction.operands(2) == SASSFixture.operand("0x3"))
        )
        if (immediate) instruction.rewrite(operands =
          instruction.operands.updated(2, Operand.Bits(BigInt(java.lang.Integer.toUnsignedLong(width)), 0))
        )
        else instruction
      })
    }
    val lowered = SASSFixture(module, module.kernels.head, Vec(function))

    locally {
      val service = SASSFixture.Execution()
      val output  = service.allocate(20L)
      val program = service.compile(lowered)
        .fold(e => fail(e.message), identity)

      inputs.foreach { case (value, width) =>
        val task = SASSFixture.task(
          "sgxt_probe": NonEmpty[String],
          LaunchGeometry.Dimensions.One,
          LaunchGeometry.Dimensions.One,
          Vec(
            KernelTask.Argument.Memory(output, 0L),
            scalar(value),
            scalar(width)
          )
        )
        service.run(program, task).fold(e => fail(e.message), identity)
        val bytes                   = service.read(output, 0L, 20).toOption.get
        def intAt(offset: Int): Int = (bytes(offset) & 255) | ((bytes(offset + 1) & 255) << 8) |
          ((bytes(offset + 2) & 255) << 16) | (bytes(offset + 3) << 24)
        assertEquals(intAt(0), extract(value, width, signed = false, rawSgxtWidth))
        assertEquals(intAt(4), extract(value, width, signed = false, rawSgxtWidth))
        assertEquals(intAt(8), extract(value, width, signed = true, rawSgxtWidth))
        assertEquals(intAt(12), extract(value, immediateWidth.getOrElse(2), signed = false, rawSgxtWidth = true))
        assertEquals(intAt(16), extract(value, immediateWidth.getOrElse(3), signed = true, rawSgxtWidth = true))
      }
    }
  }

  test("compiler-produced sm_70 SGXT forms preserve PTX BFE descriptor behavior") {
    val sassText = SgxtFixtures.sgxtSm70ProbeSm70
    assert(sassText.contains("SGXT.U32"))
    assert(sassText.contains("SGXT "))
    assert(sassText.contains("SGXT.U32 R5, R4, R5"))
    runFixture(SgxtFixtures.sgxtSm70ProbeSm70, rawSgxtWidth = false)
  }

  test("raw sm_70 SGXT widths clamp at 32 bits, including large register values") {
    val sassText = SgxtFixtures.sgxtSm70ProbeRawWidthSm70
    assert(sassText.contains("SGXT.U32 R5, R4, R12"))
    assert(sassText.contains("SGXT R7, R6, R12"))
    runFixture(SgxtFixtures.sgxtSm70ProbeRawWidthSm70, rawSgxtWidth = true)
  }

  test("SGXT immediate widths use the measured 9-bit encoding and clamp at 32") {
    runFixture(SgxtFixtures.sgxtSm70ProbeSm70, rawSgxtWidth = false, immediateWidth = Some(256))
  }

  test("SGXT rejects forms outside its measured operands and architectures") {
    val module   = fixtureModule
    val text     = SgxtFixtures.sgxtSm70ProbeSm70
    val function = ListingDecoder.parse(text, "sgxt_probe", 70).fold(fail(_), _.head)
    val original = function.instructions.find(instruction =>
      instruction.mnemonic.raw == "SGXT" && instruction.modifiers == Vec(Instruction.Modifier.`U32`)
    ).get
    Vec(
      original.rewrite(guard = Some(Instruction.Guard(Operand.Predicate(Some(0), false, false)))),
      original.rewrite(modifiers = Vec(Instruction.Modifier.U16)),
      original.rewrite(operands = original.operands.updated(1, SASSFixture.operand("R4.H0"))),
      original.rewrite(operands = original.operands.updated(2, SASSFixture.operand("-1"))),
      original.rewrite(operands = original.operands.updated(2, SASSFixture.operand("0x200")))
    ).foreach { invalid =>
      val changed = function.copy(instructions = function.instructions.map(i => if (i == original) invalid else i))
      assert(SASSFixture.lower(module, module.kernels.head, Vec(changed)).isLeft)
    }
    assert(SASSFixture.lower(module.copy(sm = 89), module.kernels.head, Vec(function)).isLeft)
  }

}
