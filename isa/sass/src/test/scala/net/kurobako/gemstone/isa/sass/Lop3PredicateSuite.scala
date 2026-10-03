package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.constraint.auto.*
import net.kurobako.gemstone.isa.sass.asm.*
import net.kurobako.gemstone.isa.sass.fixtures.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.rpc.ExecutionProtocol.*
import net.kurobako.gemstone.vm.*

class Lop3PredicateSuite extends munit.FunSuite {
  private def fixtureModule(sm: Int): Cubin.Module =
    SASSFixture.module(sm, Vec(SASSFixture.Kernel("probe", 352, Vec((0, 8), (8, 8)), registers = 14)))

  private val rows = Vec.nonEmpty(0L, 1L, 0x80000000L, 0xffffffffL).flatMapNonEmpty { a =>
    Vec.nonEmpty(0L, 1L, 0x80000000L, 0xffffffffL).flatMapNonEmpty { b =>
      Vec.nonEmpty(0L, 0xffffffffL).mapNonEmpty(q => Vec(a, b, 0x12345678L, q))
    }
  }
  private def bytes(values: Vec[Long]): Vec[Byte] =
    Vec.from(values).flatMap(LittleEndian.toVec(_, 4))
  private val expected = rows.flatMap { row =>
    val result = row(0) & row(1)
    val p      = result != 0
    val q      = row(3) != 0
    Vec(result, if (p || q) 1L else 0L, if (p && q) 1L else 0L, if (p || !q) 1L else 0L)
  }

  for (sm <- Vec(70, 75, 80, 86, 89)) test(s"sm_$sm LOP3 predicate OR/AND preserves data and aliased input") {
    val module    = fixtureModule(sm)
    val kernel    = module.kernels.head
    val functions =
      ListingDecoder.parse(
        sm match {
          case 70    => Lop3PredicateFixtures.lop3PredicateProbeSm70;
          case 75    => Lop3PredicateFixtures.lop3PredicateProbeSm75;
          case 80    => Lop3PredicateFixtures.lop3PredicateProbeSm80;
          case 86    => Lop3PredicateFixtures.lop3PredicateProbeSm86;
          case 89    => Lop3PredicateFixtures.lop3PredicateProbeSm89;
          case other => fail(s"unknown fixture architecture $other")
        },
        kernel.name,
        sm
      )
        .fold(fail(_), identity)
    assert(functions.head.instructions.exists(instruction =>
      instruction.mnemonic.raw == "LOP3" &&
        instruction.modifiers == Vec(Instruction.Modifier.`LUT`, Instruction.Modifier.`PAND`)
    ))
    assert(functions.head.instructions.exists(i =>
      i.mnemonic.raw == "LOP3" && i.modifiers == Vec(Instruction.Modifier.`LUT`) &&
        i.operands.head == SASSFixture.operand("P0") && i.operands.last == SASSFixture.operand("!P0")
    ))
    val source = SASSFixture(module, kernel, functions)
    locally {
      val service = SASSFixture.Execution()
      val size    = rows.positiveSize * 16
      val input   = service.allocate(size.toLong)
      val output  = service.allocate(size.toLong)
      assert(service.write(input, 0L, bytes(rows.flatten)).isRight)
      val program = service.compile(source)
        .fold(e => fail(e.message), identity)
      val task = SASSFixture.task(
        kernel.name,
        LaunchGeometry.Dimensions(rows.positiveSize, 1, 1),
        LaunchGeometry.Dimensions.One,
        Vec(output, input).map(id => KernelTask.Argument.Memory(id, 0L))
      )
      service.run(program, task).fold(e => fail(e.message), identity)
      assertEquals(service.read(output, 0L, size), Right(bytes(expected)))
    }
  }

  test("LOP3 dynamic predicate input remains bounded to verified architectures and operand forms") {
    val module   = fixtureModule(70)
    val kernel   = module.kernels.head
    val function = ListingDecoder.parse(Lop3PredicateFixtures.lop3PredicateProbeSm70, kernel.name, 70)
      .fold(fail(_), _.head)
    val lop = function.instructions.find(i =>
      i.mnemonic.raw == "LOP3" && i.modifiers == Vec(Instruction.Modifier.`LUT`) &&
        i.operands.head == SASSFixture.operand("P0")
    ).get
    def rejected(sm: Int >= 35, opcode: String, input: String): Boolean = {
      val parts   = opcode.split('.').toVec
      val changed = lop.rewrite(
        mnemonic = parts.head.refine[NonEmpty[String]].fold(fail(_), identity),
        modifiers = parts.tail.map(Instruction.Modifier(_)),
        operands = lop.operands.updated(lop.operands.size - 1, SASSFixture.operand(input))
      )
      SASSFixture.lower(module.copy(sm = sm), kernel, Vec(function.copy(instructions = Vec(changed)))).isLeft
    }
    assert(rejected(35, "LOP3.LUT", "P0"))
    assert(rejected(70, "ULOP3.LUT", "UP0"))
    val invalidPredicate = Instruction.decode(
      lop.mnemonic.raw,
      lop.modifiers,
      lop.operands.updated(lop.operands.size - 1, Operand.Register(RegisterBank.R, 0)),
      lop.guard
    )
    assert(invalidPredicate.left.exists(_.startsWith("invalid operands for LOP3:")))
  }
}
