package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.sass.asm.*
import net.kurobako.gemstone.isa.sass.fixtures.*
import net.kurobako.gemstone.rpc.ExecutionProtocol.*
import net.kurobako.gemstone.vm.*
import scala.util.Random

class LegacyIscaddSuite extends munit.FunSuite {
  private val kernels = Vec(
    SASSFixture.Kernel("guarded_index_scaled_minus_base", 320, Vec(0 -> 8, 8 -> 8, 16 -> 8), registers = 10),
    SASSFixture.Kernel("index_scaled9_minus_base", 320, Vec(0 -> 8, 8 -> 8, 16 -> 8), registers = 8),
    SASSFixture.Kernel("index_scaled7_minus_base", 320, Vec(0 -> 8, 8 -> 8, 16 -> 8), registers = 8),
    SASSFixture.Kernel("index_scaled_minus_base", 320, Vec(0 -> 8, 8 -> 8, 16 -> 8), registers = 8)
  )
  private def fixtureModule(sm: Int): Cubin.Module = SASSFixture.module(sm, kernels)

  private val listings = Map(
    (50, "guarded_index_scaled_minus_base") -> IscaddNegativeFixtures.iscaddNegativeGuardedIndexScaledMinusBaseSm50,
    (60, "guarded_index_scaled_minus_base") -> IscaddNegativeFixtures.iscaddNegativeGuardedIndexScaledMinusBaseSm60,
    (50, "index_scaled9_minus_base")        -> IscaddNegativeFixtures.iscaddNegativeIndexScaled9MinusBaseSm50,
    (60, "index_scaled9_minus_base")        -> IscaddNegativeFixtures.iscaddNegativeIndexScaled9MinusBaseSm60,
    (50, "index_scaled7_minus_base")        -> IscaddNegativeFixtures.iscaddNegativeIndexScaled7MinusBaseSm50,
    (60, "index_scaled7_minus_base")        -> IscaddNegativeFixtures.iscaddNegativeIndexScaled7MinusBaseSm60,
    (50, "index_scaled_minus_base")         -> IscaddNegativeFixtures.iscaddNegativeIndexScaledMinusBaseSm50,
    (60, "index_scaled_minus_base")         -> IscaddNegativeFixtures.iscaddNegativeIndexScaledMinusBaseSm60
  )

  private def dwords(values: Vec[Int]): Vec[Byte] =
    Vec.from(values.flatMap(value => LittleEndian.toVec(value.toLong, 4)))

  private def readDwords(bytes: Vec[Byte]): Vec[Int] =
    bytes.indices.grouped(4).map(_.zipWithIndex.foldLeft(0) { case (value, (index, byte)) =>
      value | ((bytes(index).toInt & 255) << (byte * 8))
    }).toVec

  private def execute(
      sm: Int,
      kernelName: String,
      shift: Int,
      indices: Vec[Int],
      base: Vec[Int],
      testFalseGuard: Boolean = false
  ): Unit = {
    val module = fixtureModule(sm)
    assertEquals(module.sm, sm)
    val function = ListingDecoder.parse(
      listings((sm, kernelName)),
      kernelName,
      sm
    ).fold(fail(_), _.head)
    val iscadd = function.instructions.filter(_.mnemonic.raw == "ISCADD")
    assertEquals(iscadd.size, 1)
    assertEquals(
      iscadd.head.operands.drop(1),
      Vec(
        Operand.Unary(Operand.Register(RegisterBank.R, 4), negative = true, not = false, absolute = false),
        Operand.Register(RegisterBank.R, 2),
        Operand.Bits(BigInt(shift), 0)
      )
    )
    val executableFunction = if (testFalseGuard) falseGuarded(function) else function
    val sass               = SASSFixture(
      module,
      module.kernels.find(_.name == kernelName).get,
      Vec(executableFunction)
    )
    locally {
      val service = SASSFixture.Execution()
      val program = service.compile(sass)
        .fold(error => fail(error.message), identity)
      val indexInput = service.allocate((indices.size.toLong * 4L).unsafe[Long > 0])
      val baseInput  = service.allocate((base.size.toLong * 4L).unsafe[Long > 0])
      val output     = service.allocate((base.size.toLong * 4L).unsafe[Long > 0])
      assert(service.write(indexInput, 0L, dwords(indices)).isRight)
      assert(service.write(baseInput, 0L, dwords(base)).isRight)
      val arguments = Vec(
        KernelTask.Argument.Memory(indexInput, 0L),
        KernelTask.Argument.Memory(baseInput, 0L),
        KernelTask.Argument.Memory(output, 0L)
      )
      val task = SASSFixture.task(
        kernelName.unsafe[NonEmpty[String]],
        LaunchGeometry.Dimensions.One,
        LaunchGeometry.Dimensions(base.size.unsafe[Int > 0], 1, 1),
        Vec.from(arguments)
      )
      service.run(program, task).fold(error => fail(error.message), identity)
      val bytes = service.read(
        output,
        0L,
        (base.size * 4).unsafe[Int >= 0]
      ).fold(error => fail(error.message), identity)
      val expected = if (testFalseGuard) Vec.fill(base.size)(0x5a5a5a5a)
      else indices.indices.map(index => (base(index).toLong - (indices(index).toLong << shift)).toInt).toVec
      assertEquals(readDwords(bytes), expected, s"sm_$sm $kernelName shift=$shift")
    }
  }

  private def falseGuarded(function: DecodedFunction): DecodedFunction = {
    val index = function.instructions.indexWhere(_.mnemonic.raw == "ISCADD")
    assert(index >= 0)
    val instruction                                                       = function.instructions(index)
    def decoded(address: Int, operation: Instruction): DecodedInstruction =
      DecodedInstruction(address.unsafe[Int >= 0], operation, None, None)

    val init = decoded(
      instruction.address.raw - 2,
      Instruction.decode(
        "MOV32I",
        Vec.empty,
        Vec(instruction.operands.head, Operand.Bits(BigInt("5a5a5a5a", 16), 0)),
        None
      ).fold(error => fail(error), identity)
    )
    val setTrue = decoded(
      instruction.address.raw - 1,
      Instruction.decode(
        "ISETP",
        Vec(Instruction.Modifier.EQ, Instruction.Modifier.AND),
        Vec(
          Operand.Predicate(Some(0), uniform = false, negated = false),
          Operand.Predicate(None, uniform = false, negated = false),
          Operand.Register(RegisterBank.R, 255),
          Operand.Register(RegisterBank.R, 255),
          Operand.Predicate(None, uniform = false, negated = false)
        ),
        None
      ).fold(error => fail(error), identity)
    )
    function.copy(instructions =
      function.instructions.patch(
        index,
        Vec(
          init,
          setTrue,
          instruction.rewrite(guard =
            Some(Instruction.Guard(Operand.Predicate(Some(0), uniform = false, negated = true)))
          )
        ),
        1
      )
    )
  }

  test("CUDA 11.8 sm_50/sm_60 negated ISCADD wraps shifted index at 32 bits") {
    val boundary = Vec(0, 1, -1, Int.MinValue, Int.MaxValue, 0x100, -0x100, 0x01000000, 0x7fffffff)
    val random   = Random(0x1caddL)
    val indices  = boundary ++ Vec.fill(64)(random.nextInt())
    val base     = Vec.fill(indices.size)(random.nextInt())
    Vec(50, 60).foreach { sm =>
      execute(sm, "index_scaled7_minus_base", 7, indices, base)
      execute(sm, "index_scaled_minus_base", 8, indices, base)
      execute(sm, "index_scaled9_minus_base", 9, indices, base)
      execute(sm, "index_scaled_minus_base", 8, indices, base, testFalseGuard = true)
    }
  }
}
