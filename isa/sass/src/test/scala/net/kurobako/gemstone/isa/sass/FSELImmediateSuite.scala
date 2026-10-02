package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.nvidia.VMIR.*
import net.kurobako.gemstone.isa.nvidia.VMIR.Instruction as VMInstruction
import net.kurobako.gemstone.isa.sass.asm.*
import net.kurobako.gemstone.isa.sass.fixtures.*
import net.kurobako.gemstone.rpc.ExecutionProtocol.*
import net.kurobako.gemstone.vm.*

class FSELImmediateSuite extends munit.FunSuite {
  private def probeModule: Cubin.Module =
    SASSFixture.module(86, Vec(SASSFixture.Kernel("probe", 352, Vec(0 -> 8), registers = 8)))
  private def saxpyModule: Cubin.Module = SASSFixture.module(
    86,
    Vec(SASSFixture.Kernel("saxpy", 352, Vec(0 -> 8, 8 -> 8, 16 -> 4, 20 -> 4), registers = 10))
  )

  private def lower(
      encodingWord: Option[Long],
      sourceA: String = "RZ",
      sourceB: String = "+QNAN"
  ): Either[String, SASSFixture] = {
    val module   = saxpyModule
    val function = DecodedFunction(
      "saxpy",
      Vec(
        SASSFixture.instruction(0, "FSEL", Vec("R3", sourceA, sourceB, "P0"), None, encodingWord),
        SASSFixture.instruction(16, "EXIT", Vec.empty, None)
      )
    )
    SASSFixture.lower(module, module.kernels.head, Vec(function))
  }

  test("nvdisasm hex output preserves the raw FSEL immediate bits") {
    val disassembly =
      """
        |.visible .entry probe
        |probe:
        |        /*0000*/                   FSEL R13, RZ, +QNAN , P0 ; /* 0x7ff80000ff0d7808 */
        |                                                   /* 0x000fc60001000000 */
        |        /*0010*/                   EXIT ; /* 0x000000000000794d */
        |                                                   /* 0x000fea0003800000 */
        |""".stripMargin
    val instruction = ListingDecoder.parse(disassembly, "probe", 86).fold(fail(_), _.head.instructions.head)
    assertEquals(instruction.encodingWord, Some(BigInt("7ff80000ff0d7808", 16)))
  }

  test("FSEL carries encoded NaN payload bits through its raw register select") {
    val canonical = lower(Some(0x7fc00000ff037808L)).fold(fail(_), identity)
    val vendor    = lower(Some(0x7ff80000ff037808L)).fold(fail(_), identity)
    def selectedImmediateValues(fixture: SASSFixture): Set[Long] = fixture.instructions.collect {
      case VMInstruction.Select(_, a, b, _, _) => Vec(a, b)
    }.flatten.collect {
      case Value.Immediate(value) => value
    }.toSet
    assert(selectedImmediateValues(canonical)(0x7fc00000L))
    assert(selectedImmediateValues(vendor)(0x7ff80000L))
  }

  test("FSEL NaN immediates fail closed when disassembly did not provide instruction bits") {
    val error = lower(None).swap.toOption.getOrElse(fail("expected missing encoding to be rejected"))
    assert(error.contains("FSEL NaN immediate requires raw instruction encoding"), error)
  }

  test(
    "FSEL NaN immediate decoding rejects register encodings, mismatched payloads, and unverified operand placement"
  ) {
    val registerForm =
      lower(Some(0x7ff80000ff037208L)).swap.toOption.getOrElse(fail("expected register form to be rejected"))
    assert(registerForm.contains("FSEL NaN source is not encoded as an immediate"), registerForm)

    val mismatchedPayload =
      lower(Some(0x3f800000ff037808L)).swap.toOption.getOrElse(fail("expected mismatched bits to be rejected"))
    assert(mismatchedPayload.contains("FSEL NaN spelling does not match encoded source bits"), mismatchedPayload)

    val invalidPlacement = Parser.parse("FSEL R3, +QNAN, RZ, P0;")
    assert(invalidPlacement.isLeft)
    assert(invalidPlacement.left.toOption.exists(_.contains("invalid operands for FSEL")), invalidPlacement)
  }

  test("compiler-produced sm_86 FSEL immediates preserve distinct NaN payloads in VM execution") {
    val module          = probeModule
    val sass            = FselImmediateFixtures.fselImmediateSm86Probe
    val function        = ListingDecoder.parse(sass, "probe", 86).fold(fail(_), _.head)
    val lowered         = SASSFixture(module, module.kernels.head, Vec(function))
    val immediateValues = lowered.instructions.collect {
      case VMInstruction.Select(_, a, b, _, _) => Vec(a, b)
    }.flatten.collect {
      case Value.Immediate(value) => value
    }.toSet
    assert(immediateValues(0x7ff80000L))
    assert(immediateValues(0x7fc00000L))

    locally {
      val service = SASSFixture.Execution()
      val output  = service.allocate(16L.unsafe[Long > 0])
      val program = service.compile(lowered)
        .fold(e => fail(e.message), identity)
      val task = SASSFixture.task(
        "probe": NonEmpty[String],
        LaunchGeometry.Dimensions(2, 1, 1),
        LaunchGeometry.Dimensions.One,
        Vec(KernelTask.Argument.Memory(output, 0L))
      )
      service.run(program, task).fold(e => fail(e.message), identity)
      val bytes  = service.read(output, 0L, 16).toOption.get
      val actual = Vec.tabulate(4)(index =>
        LittleEndian.decode(bytes, index * 4, 4)
      )
      assertEquals(actual, Vec(0x3f800000L, 0x3f800000L, 0x7ff80000L, 0x7fc00000L))
    }
  }
}
