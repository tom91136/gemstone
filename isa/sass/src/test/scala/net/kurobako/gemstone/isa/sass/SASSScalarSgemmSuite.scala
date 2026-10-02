package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.sass.fixtures.*
import net.kurobako.gemstone.rpc.ExecutionProtocol.*
import net.kurobako.gemstone.vm.*

class SASSScalarSgemmSuite extends munit.FunSuite {
  for (inner <- Vec(0, 1, 3, 4, 5, 9))
    test(s"generated SM89 scalar SGEMM executes K=$inner without NVIDIA decoding") {
      val text         = ScalarSgemmFixtures.scalarSgemm
      val instructions = SASSOracleWords.read(text).map { case (address, bytes) =>
        InstructionDecoder.decodeTyped(address, bytes, "SM89").fold(fail(_), identity)
      }
      val functions = Vec(DecodedFunction("scalar_sgemm", instructions))
      assert(functions.head.instructions.exists(_.mnemonic.raw == "FFMA"))
      val module = SASSFixture.module(
        89,
        Vec(SASSFixture.Kernel(
          "scalar_sgemm",
          0x160,
          Vec(0 -> 8, 8 -> 8, 16 -> 8, 24 -> 4),
          registers = 25
        ))
      )
      val fixture                               = SASSFixture(module, module.kernels.head, functions)
      def floats(values: Vec[Float]): Vec[Byte] = Vec.from(values).flatMap(value =>
        LittleEndian.toVec(java.lang.Float.floatToRawIntBits(value).toLong, 4)
      )
      val a        = Vec.tabulate(8 * inner)(index => 0.5f * (index / inner + 1) + 0.125f * (index % inner + 1))
      val b        = Vec.tabulate(inner * 4)(index => 0.25f * (index / 4 + 1) * (index % 4 + 1))
      val expected = Vec.tabulate(32)(index =>
        (0 until inner).foldLeft(0f) { (sum, k) =>
          sum + a((index / 4) * inner + k) * b(k * 4 + index % 4)
        }
      )
      locally {
        val service = SASSFixture.Execution()
        val left    = service.allocate((a.size.max(1) * 4L).unsafe[Long > 0])
        val right   =
          service.allocate((b.size.max(1) * 4L).unsafe[Long > 0])
        val output = service.allocate(128L.unsafe[Long > 0])
        assert(service.write(left, 0L, floats(a)).isRight)
        assert(service.write(right, 0L, floats(b)).isRight)
        assert(service.write(output, 0L, floats(Vec.fill(32)(-999f))).isRight)
        val program = service.compile(fixture)
          .fold(error => fail(error.message), identity)
        val task = SASSFixture.task(
          "scalar_sgemm": NonEmpty[String],
          LaunchGeometry.Dimensions(32, 1, 1),
          LaunchGeometry.Dimensions.One,
          Vec(
            KernelTask.Argument.Memory(output, 0L),
            KernelTask.Argument.Memory(left, 0L),
            KernelTask.Argument.Memory(right, 0L),
            KernelTask.Argument.Scalar(LittleEndian.toVec(inner.toLong, 4))
          )
        )
        service.run(program, task).fold(error => fail(error.message), identity)
        assertEquals(service.read(output, 0L, 128.unsafe[Int >= 0]), Right(floats(expected)))
        assertEquals(service.read(left, 0L, (a.size * 4).unsafe[Int >= 0]), Right(floats(a)))
        assertEquals(service.read(right, 0L, (b.size * 4).unsafe[Int >= 0]), Right(floats(b)))
      }
    }

}
