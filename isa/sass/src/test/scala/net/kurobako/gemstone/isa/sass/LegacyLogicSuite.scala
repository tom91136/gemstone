package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.sass.asm.*
import net.kurobako.gemstone.isa.sass.fixtures.*
import net.kurobako.gemstone.rpc.ExecutionProtocol.*
import net.kurobako.gemstone.vm.*

class LegacyLogicSuite extends munit.FunSuite {
  private def fixtureModule(sm: Int): Cubin.Module =
    SASSFixture.module(
      sm,
      Vec(
        SASSFixture.Kernel("predicate_probe", 320, Vec(0 -> 8, 8 -> 4, 12 -> 4), registers = 6),
        SASSFixture.Kernel("logic_probe", 320, Vec(0 -> 8, 8 -> 4, 12 -> 4, 16 -> 4), registers = 11)
      )
    )

  private val listings = Map(
    ("probe", 50, "logic_probe")     -> LogicFixtures.legacyLogicProbeSm50LogicProbe,
    ("probe", 50, "predicate_probe") -> LogicFixtures.legacyLogicProbeSm50PredicateProbe,
    ("probe", 60, "logic_probe")     -> LogicFixtures.legacyLogicProbeSm60LogicProbe,
    ("probe", 60, "predicate_probe") -> LogicFixtures.legacyLogicProbeSm60PredicateProbe,
    ("zero", 50, "predicate_probe")  -> LogicFixtures.legacyLogicZeroSm50PredicateProbe,
    ("zero", 60, "predicate_probe")  -> LogicFixtures.legacyLogicZeroSm60PredicateProbe
  )

  private val patterns = Vec(0, 1, 2, 3, 31, 32, 0x80000000, -1, 0x55555555, 0xaaaaaaaa)
  private val inputs   = for {
    a <- patterns
    b <- patterns
  } yield (a, b, a ^ 0x12345678)

  private def verify(sm: Int, name: String, zeroVariant: Boolean = false): Unit = {
    val prefix   = if (zeroVariant) "zero" else "probe"
    val module   = fixtureModule(sm)
    val kernel   = module.kernels.find(_.name == name).get
    val text     = listings((prefix, sm, name))
    val function = ListingDecoder.parse(text, name, sm).fold(fail(_), _.find(_.name == name).get)
    if (name == "logic_probe") assert(function.instructions.exists(i =>
      i.mnemonic.raw == "LOP3" && i.modifiers == Vec(Instruction.Modifier.LUT) && i.operands.size == 5
    ))
    else {
      assert(function.instructions.exists(i =>
        i.mnemonic.raw == "LOP" && i.modifiers == Vec(Instruction.Modifier.AND, Instruction.Modifier.NZ)
      ))
      assert(function.instructions.exists(i =>
        i.mnemonic.raw == "LOP" && i.modifiers == Vec(Instruction.Modifier.OR, Instruction.Modifier.NZ)
      ))
      if (zeroVariant) assert(function.instructions.exists(i =>
        i.mnemonic.raw == "LOP" &&
          i.modifiers == Vec(Instruction.Modifier.AND, Instruction.Modifier.Z) &&
          i.operands(1) != Operand.Register(RegisterBank.R, 255)
      ))
    }
    val lowered = SASSFixture(module, kernel, Vec(function))
    locally {
      val service = SASSFixture.Execution()
      val output  = service.allocate(24L.unsafe[Long > 0])
      val program = service.compile(lowered)
        .fold(e => fail(e.message), identity)
      inputs.foreach { case (a, b, c) =>
        assert(service.write(output, 0L, Vec.fill(24)(0.toByte)).isRight)
        def scalar(value: Int): KernelTask.Argument =
          KernelTask.Argument.Scalar(LittleEndian.toVec(value.toLong & 0xffffffffL, 4))
        val arguments =
          Vec(KernelTask.Argument.Memory(output, 0L), scalar(a), scalar(b))
        val task = SASSFixture.task(
          name.unsafe[NonEmpty[String]],
          LaunchGeometry.Dimensions.One,
          LaunchGeometry.Dimensions.One,
          if (name == "logic_probe") arguments :+ scalar(c) else arguments
        )
        service.run(program, task).fold(e => fail(e.message), identity)
        val bytes  = service.read(output, 0L, 24).toOption.get
        val actual =
          Vec.tabulate(6)(i => (0 until 4).foldLeft(0)((v, j) => v | ((bytes(i * 4 + j) & 255) << (j * 8))))
        val expected = if (name == "logic_probe")
          Vec(
            a & b,
            if ((a & b) != 0) 17 else 29,
            if ((a & b) == 0) 37 else 41,
            a | b,
            if ((a | b) != 0) 43 else 47,
            (a & b) | (~a & c)
          )
        else Vec(
          if ((a & 3) != 0) a else 0,
          if ((a & 31) == 0) b else 0,
          if (zeroVariant) a & 31 else 0,
          if ((a | b) != 0) b else 0,
          0,
          0
        )
        assertEquals(actual, expected, s"sm_$sm $name zero=$zeroVariant a=$a b=$b c=$c")
      }
    }
  }

  for (sm <- Vec(50, 60)) {
    test(s"sm_$sm compiler LOP3 uses five operands and preserves its permuted truth table") {
      verify(sm, "logic_probe")
    }
    test(s"sm_$sm compiler LOP predicates preserve independent AND and OR guards") {
      verify(sm, "predicate_probe")
    }
    test(s"sm_$sm decoded LOP.Z variant preserves zero predicate and data outputs") {
      verify(sm, "predicate_probe", zeroVariant = true)
    }
  }
}
