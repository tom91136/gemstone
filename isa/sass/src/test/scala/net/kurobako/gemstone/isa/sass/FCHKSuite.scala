package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.constraint.auto.*
import net.kurobako.gemstone.isa.sass.asm.*
import net.kurobako.gemstone.isa.sass.fixtures.*
import net.kurobako.gemstone.rpc.ExecutionProtocol.*
import net.kurobako.gemstone.vm.*

class FCHKSuite extends munit.FunSuite {
  private val registerCounts                       = Map(70 -> 12, 75 -> 8, 80 -> 12, 86 -> 12, 89 -> 12)
  private def fixtureModule(sm: Int): Cubin.Module =
    SASSFixture.module(
      sm,
      Vec(SASSFixture.Kernel(
        "probe",
        352,
        Vec((0, 8), (8, 8), (16, 8)),
        registers = registerCounts.getOrElse(sm, fail(s"unsupported FCHK fixture sm_$sm"))
      ))
    )

  private val listing = Map(
    70 -> FchkFixtures.fchkFchkSm70,
    75 -> FchkFixtures.fchkFchkSm75,
    80 -> FchkFixtures.fchkFchkSm80,
    86 -> FchkFixtures.fchkFchkSm86,
    89 -> FchkFixtures.fchkFchkSm89
  )

  private def bits(exponent: Int, mantissa: Int = 0, negative: Boolean = false): Int =
    (if (negative) 0x80000000 else 0) | (exponent << 23) | mantissa

  // The hardware probe identifies this as the slow-path predicate. Exponents
  // are IEEE-754 biased exponents; sign and fraction bits do not participate.
  private def slowPath(a: Int, b: Int): Int = {
    val ea = (a >>> 23) & 0xff
    val eb = (b >>> 23) & 0xff
    if (ea < 25 || ea > 254 || eb < 2 || eb > 251 || ea - eb < -124 || ea - eb > 126) 1 else 0
  }

  private def probeInstructions(guardWithIndex: Boolean): Vec[DecodedInstruction] = {
    val body = Vec(
      ("LDC.64", "R10,c[0x0][0x160]", None),
      ("S2R", "R0,SR_TID.X", None),
      ("IMAD.WIDE.U32", "R10,R0,4,R10", None),
      ("LDG.E", "R4,[R10.64]", None),
      ("LDC.64", "R10,c[0x0][0x168]", None),
      ("IMAD.WIDE.U32", "R10,R0,4,R10", None),
      ("LDG.E", "R5,[R10.64]", None)
    ) ++
      (if (guardWithIndex) Vec(
         ("ISETP.NE.U32.AND", "P0,PT,R0,RZ,PT", None),
         ("FCHK", "P0,R4,R5", Some("@P0"))
       )
       else Vec(("FCHK", "P0,R4,R5", None))) ++ Vec(
        ("MOV", "R7,1", None),
        ("SEL", "R6,R7,0,P0", None),
        ("LDC.64", "R10,c[0x0][0x170]", None),
        ("IMAD.WIDE.U32", "R10,R0,4,R10", None),
        ("STG.E", "[R10.64],R6", None),
        ("EXIT", "", None)
      )
    body.zipWithIndex.map { case ((opcode, operands, pred), index) =>
      SASSFixture.instruction(
        index * 16,
        opcode,
        operands.split(',').filter(_.nonEmpty).map(_.trim).toVec,
        pred
      )
    }
  }

  private def execute(sm: Int, pairs: NonEmpty[Vec[(Int, Int)]], guarded: Boolean = false): Vec[Int] = {
    require(pairs.size <= 256)
    val module   = fixtureModule(sm)
    val kernel   = module.kernels.find(_.name == "probe").getOrElse(fail("probe kernel is missing"))
    val function = if (guarded) DecodedFunction("probe", probeInstructions(guarded))
    else
      ListingDecoder.parse(
        listing(sm),
        "probe",
        sm
      ).fold(fail(_), _.find(_.name == "probe").getOrElse(fail("probe SASS function is missing")))
    val sass                               = SASSFixture(module, kernel, Vec(function))
    val bytes                              = pairs.positiveSize * 4
    def words(values: Vec[Int]): Vec[Byte] =
      Vec.from(values).flatMap(v => LittleEndian.toVec(v.toLong & 0xffffffffL, 4))
    locally {
      val service = SASSFixture.Execution()
      val a       = service.allocate(bytes.toLong)
      val b       = service.allocate(bytes.toLong)
      val out     = service.allocate(bytes.toLong)
      assert(service.write(a, 0L, words(pairs.map(_._1))).isRight)
      assert(service.write(b, 0L, words(pairs.map(_._2))).isRight)
      val program = service.compile(sass)
        .fold(e => fail(e.message), identity)
      val task = SASSFixture.task(
        "probe": NonEmpty[String],
        LaunchGeometry.Dimensions(pairs.positiveSize, 1, 1),
        LaunchGeometry.Dimensions.One,
        Vec(
          KernelTask.Argument.Memory(a, 0L),
          KernelTask.Argument.Memory(b, 0L),
          KernelTask.Argument.Memory(out, 0L)
        )
      )
      service.run(program, task).fold(e => fail(e.message), identity)
      val data   = service.read(out, 0L, bytes).toOption.get
      val stored = pairs.indices.map { i =>
        (0 until 4).foldLeft(0)((value, byte) => value | ((data(i * 4 + byte).toInt & 255) << (8 * byte)))
      }.toVec
      // The compiler's SEL selects 1 when P0 is false, so invert its output
      // to compare the measured/raw FCHK predicate with the boundary equation.
      if (guarded) stored else stored.map(value => 1 - value)
    }
  }

  private val boundaryPairs: NonEmpty[Vec[(Int, Int)]] = {
    val exponents = Vec.nonEmpty(0, 1, 2, 24, 25, 26, 127, 250, 251, 252, 253, 254, 255)
    val matrix    = Vec.tabulateNonEmpty(exponents.positiveSize) { index =>
      (exponents(index.raw), index.raw)
    }.flatMapNonEmpty { (ea, i) =>
      Vec.tabulateNonEmpty(exponents.positiveSize) { index =>
        val j  = index.raw
        val eb = exponents(j)
        (
          bits(ea, (i * 0x10203 + j * 0x301) & 0x7fffff, (i & 1) != 0),
          bits(eb, (j * 0x507 + i * 0x10001) & 0x7fffff, (j & 1) != 0)
        )
      }
    }
    val special = Vec(
      bits(0),
      bits(0, negative = true), // signed zeros
      bits(0, 1),
      bits(0, 0x7fffff, negative = true), // subnormals
      bits(127, 0x400000),
      bits(127, 0x7fffff, negative = true),
      bits(255),
      bits(255, 1),
      bits(255, 0x400000, negative = true),
      bits(255, 0x7fffff),
      bits(128),
      bits(2),
      bits(129),
      bits(2),
      bits(2),
      bits(126),
      bits(2),
      bits(127)
    ).grouped(2).map {
      case Vec(a, b) => (a, b); case Vec(a) => (a, 0); case _ => fail("bad special-value fixture")
    }.toVec
    matrix.concatNonEmpty(special)
  }

  for (sm <- Vec(70, 75, 80, 86, 89))
    test(s"FCHK exponent boundaries, signs, zeros and NaNs for sm_$sm cubin") {
      val actual   = execute(sm, boundaryPairs)
      val expected = boundaryPairs.map((a, b) => slowPath(a, b))
      assertEquals(actual, expected)
    }

  test("sm70 and sm75 FCHK reject forms outside the measured register forms") {
    Vec(70, 75).foreach { sm =>
      val module   = fixtureModule(sm)
      val kernel   = module.kernels.find(_.name == "probe").get
      val function = ListingDecoder.parse(
        listing(sm),
        "probe",
        sm
      ).fold(fail(_), _.head)
      val original = function.instructions.find(_.mnemonic.raw == "FCHK").get
      Vec(
        original.rewrite(guard = Some(Instruction.Guard(Operand.Predicate(Some(0), uniform = false, negated = false)))),
        original.rewrite(modifiers = original.modifiers :+ Instruction.Modifier("DIVIDE"))
      ).foreach { invalid =>
        val changed = function.copy(instructions = function.instructions.map(i => if (i == original) invalid else i))
        assert(SASSFixture.lower(module, kernel, Vec(changed)).isLeft)
      }
      assert(Parser.parse("FCHK P0, 1, R5;").isLeft)
    }
  }

  test("predicated FCHK may read and write the same predicate without changing the guard") {
    val pairs = Vec.nonEmpty(
      bits(127)                  -> bits(127), // lane 0 has a false guard and must retain it
      bits(24)                   -> bits(127),
      bits(127, negative = true) -> bits(2),
      bits(127, 0x7fffff)        -> bits(127, 0x400000),
      bits(255, 0x400000)        -> bits(127)
    )
    val actual   = execute(89, pairs, guarded = true)
    val expected = Vec(0) ++ pairs.drop(1).map((a, b) => slowPath(a, b))
    assertEquals(actual, expected)
  }
}
