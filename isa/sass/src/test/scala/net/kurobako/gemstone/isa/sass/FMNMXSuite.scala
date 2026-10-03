package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.*
import net.kurobako.gemstone.isa.nvidia.*
import net.kurobako.gemstone.isa.sass.asm.*
import net.kurobako.gemstone.isa.sass.fixtures.*
import net.kurobako.gemstone.rpc.ExecutionProtocol.*
import net.kurobako.gemstone.vm.*

class FMNMXSuite extends munit.FunSuite {
  private def fixtureModule: Cubin.Module =
    SASSFixture.module(86, Vec(SASSFixture.Kernel("probe", 352, Vec((0, 8), (8, 8), (16, 8)), registers = 14)))

  private def parsedFunction(): (Cubin.Module, DecodedFunction) = {
    val module   = fixtureModule
    val text     = FmnmxFixtures.fmnmxSm86Probe
    val function = ListingDecoder.parse(text, "probe", 86).fold(fail(_), _.head)
    (module, function)
  }

  private def lower(function: DecodedFunction): Either[String, SASSFixture] = {
    val module = fixtureModule
    SASSFixture.lower(module, module.kernels.head, Vec(function))
  }

  private val cases = NumericCases.f32Extrema

  test("compiler-produced sm_86 FMNMX implements min/max, zero ties, and NaN selection") {
    val text = FmnmxFixtures.fmnmxSm86Probe
    assert(text.contains("FMNMX R9, R4.reuse, R7.reuse, PT"))
    assert(text.contains("FMNMX R11, R4, R7, !PT"))
    val (module, function) = parsedFunction()
    val lowered            = SASSFixture(module, module.kernels.head, Vec(function))

    val inputs = Vec(cases.map(_._1), cases.map(_._2)).map(values =>
      Vec.from(values).flatMap(LittleEndian.toVec(_, 4))
    )
    locally {
      val service     = SASSFixture.Execution()
      val output      = service.allocate(cases.positiveSize.toLong.widen[Long > 0] * 8L)
      val allocations = inputs.map(service.allocate(_))
      val program     = service.compile(lowered)
        .fold(error => fail(error.message), identity)
      val task = SASSFixture.task(
        "probe": NonEmpty[String],
        LaunchGeometry.Dimensions(cases.positiveSize, 1, 1),
        LaunchGeometry.Dimensions.One,
        Vec(
          KernelTask.Argument.Memory(output, 0L),
          KernelTask.Argument.Memory(allocations(0), 0L),
          KernelTask.Argument.Memory(allocations(1), 0L)
        )
      )
      service.run(program, task).fold(error => fail(error.message), identity)
      val actual   = service.readWords(output, cases.nonNegativeSize * 2)
      val expected = cases.flatMap { case (_, _, minimum, maximum) => Vec(minimum, maximum) }
      assertEquals(actual, expected)
    }
  }

  test("FMNMX preserves read-before-write when the destination aliases a source") {
    val (module, original) = parsedFunction()
    val aliased            = original.copy(instructions = original.instructions.map { instruction =>
      val opcode   = instruction.mnemonic.raw
      val lastIsPT = instruction.operands.lastOption.contains(Operand.Predicate(None, uniform = false, negated = false))
      if (opcode == "FMNMX" && lastIsPT)
        instruction.rewrite(operands =
          instruction.operands.updated(0, SASSOperandParser.parse("R4").fold(fail(_), identity))
        )
      else if (opcode == "FMNMX") instruction.rewrite(Parser.parse("NOP;").fold(fail(_), identity))
      else if (opcode == "STG" && instruction.address.raw == 0xf0)
        instruction.rewrite(operands = SASSOperandParser.parseMany("[R2.64], R4").fold(fail(_), identity))
      else if (opcode == "STG") instruction.rewrite(Parser.parse("NOP;").fold(fail(_), identity))
      else instruction
    })
    val lowered = SASSFixture(module, module.kernels.head, Vec(aliased))
    val inputA  = Vec.from(Vec(0x40000000L)).flatMap(LittleEndian.toVec(_, 4))
    val inputB  = Vec.from(Vec(0x3f800000L)).flatMap(LittleEndian.toVec(_, 4))
    locally {
      val service = SASSFixture.Execution()
      val output  = service.allocate(8L)
      val a       = service.allocate(inputA)
      val b       = service.allocate(inputB)
      val program = service.compile(lowered)
        .fold(error => fail(error.message), identity)
      val task = SASSFixture.task(
        "probe": NonEmpty[String],
        LaunchGeometry.Dimensions.One,
        LaunchGeometry.Dimensions.One,
        Vec(
          KernelTask.Argument.Memory(output, 0L),
          KernelTask.Argument.Memory(a, 0L),
          KernelTask.Argument.Memory(b, 0L)
        )
      )
      service.run(program, task).fold(error => fail(error.message), identity)
      val bytes = service.read(output, 0L, 8).toOption.get
      assertEquals(LittleEndian.decode(bytes, 0, 4), 0x3f800000L)
    }
  }

  test("FMNMX fails closed for unmeasured modifier and selector forms") {
    val (_, function) = parsedFunction()
    val decorated     = function.copy(instructions = function.instructions.map { instruction =>
      if (
        instruction.mnemonic.raw == "FMNMX" &&
        instruction.operands.lastOption.contains(Operand.Predicate(None, uniform = false, negated = false))
      )
        instruction.rewrite(modifiers = instruction.modifiers :+ Instruction.Modifier("FTZ"))
      else instruction
    })
    assert(lower(decorated).swap.toOption.getOrElse(fail("expected unsupported modifier rejection"))
      .contains("unsupported modifiers"))
    val selector = function.copy(instructions = function.instructions.map { instruction =>
      if (
        instruction.mnemonic.raw == "FMNMX" &&
        instruction.operands.lastOption.contains(Operand.Predicate(None, uniform = false, negated = false))
      )
        instruction.rewrite(operands =
          instruction.operands.updated(3, Operand.Predicate(Some(0), uniform = false, negated = false))
        )
      else instruction
    })
    assert(lower(selector).swap.toOption.getOrElse(fail("expected unsupported selector rejection"))
      .contains("FMNMX selector must be PT or !PT"))
  }
}
