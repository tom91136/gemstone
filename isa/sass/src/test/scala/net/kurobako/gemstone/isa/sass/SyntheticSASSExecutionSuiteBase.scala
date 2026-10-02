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
      expected: Vec[Int],
      shared: Int = 0,
      sm: Int = 86,
      rewrite: DecodedInstruction => DecodedInstruction = instruction => instruction
  ): Unit = {
    val original = CubinSuiteFixtures.module
    val m        = original.copy(
      sm = sm,
      sections =
        original.sections :+ Cubin.Section(
          original.sections.size,
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
        service.allocate((expected.size * 4L).unsafe[Long > 0])
      val program = service.compile(source).fold(e => fail(e.message), identity)
      val task    = SASSFixture.task(
        "saxpy": NonEmpty[String],
        LaunchGeometry.Dimensions(expected.size.unsafe[Int > 0], 1, 1),
        LaunchGeometry.Dimensions.One,
        Vec(
          KernelTask.Argument.Memory(output, 0L),
          KernelTask.Argument.Memory(output, 0L),
          KernelTask.Argument.Scalar(LittleEndian.toVec(0L, 4)),
          KernelTask.Argument.Scalar(LittleEndian.toVec(expected.size.toLong, 4))
        )
      )
      service.run(program, task).fold(e => fail(e.message), identity)
      assertEquals(service.readWords(output, expected.size.unsafe[Int >= 0]).map(_.toInt), expected)
    }
  }

}
