package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.rpc.ExecutionProtocol.*
import net.kurobako.gemstone.vm.*

abstract class SyntheticSASSExecutionSuiteBase extends munit.FunSuite {
  protected def executeSynthetic(
      body: Vec[(String, String, Option[String])],
      expected: NonEmpty[Vec[Int]],
      shared: Int >= 0 = 0,
      sm: Int = 86,
      rewrite: DecodedInstruction => DecodedInstruction = instruction => instruction
  ): Unit = {
    val original = CubinSuiteFixtures.module
    val m        = original.copy(
      sm = sm.refine[Int >= 35].fold(error => fail(error.raw), identity),
      sections =
        original.sections :+ Cubin.Section(
          original.sections.nonNegativeSize,
          ".nv.shared.saxpy",
          8,
          3,
          shared,
          0,
          0,
          Vec.empty
        )
    )
    val instructions = body.zipWithIndex.map { case ((op, operands, pred), index) =>
      SASSFixture.instruction(index * 16, op, operands.split(',').toVec.map(_.trim).filter(_.nonEmpty), pred)
    }
    val source = SASSFixture(m, m.kernels.head, Vec(DecodedFunction("saxpy", instructions.map(rewrite))))
    locally {
      val service = SASSFixture.Execution()
      val output  =
        service.allocate(expected.positiveSize.toLong.widen[Long > 0] * 4L)
      val program = service.compile(source).fold(e => fail(e.message), identity)
      val task    = SASSFixture.task(
        "saxpy": NonEmpty[String],
        LaunchGeometry.Dimensions(expected.positiveSize, 1, 1),
        LaunchGeometry.Dimensions.One,
        Vec(
          KernelTask.Argument.Memory(output, 0L),
          KernelTask.Argument.Memory(output, 0L),
          KernelTask.Argument.Scalar(LittleEndian.toVec(0L, 4)),
          KernelTask.Argument.Scalar(LittleEndian.toVec(expected.size.toLong, 4))
        )
      )
      service.run(program, task).fold(e => fail(e.message), identity)
      assertEquals(service.readWords(output, expected.nonNegativeSize).map(_.toInt), expected)
    }
  }

}
