package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.sass.fixtures.*
import net.kurobako.gemstone.rpc.*
import net.kurobako.gemstone.rpc.ExecutionRPC.*
import net.kurobako.gemstone.testing.either.*
import net.kurobako.gemstone.vm.*
import scala.util.Using

class BinaryExecutionSuite extends munit.FunSuite {
  test("unsupported binary instructions report the kernel and offset directly") {
    val image   = Fixture.copyAddCubin.toArray
    val module  = Cubin.decode(Vec.unsafeWrapArray(image)).value
    val kernel  = module.kernels.find(_.name == "copy_add").getOrElse(fail("missing copy_add"))
    val section = elf.Image.parse(Vec.unsafeWrapArray(image)).value.sections(kernel.section.raw)
    java.util.Arrays.fill(image, section.offset.raw, (section.offset + 16).raw, 0.toByte)
    Using.resource(ExecutionSession(List(CubinBackend()))) { service =>
      val memory  = service.registerAllocation(128).value
      val program = service.load(ProgramEncoding.CUDACubin, Vec.from(image)).value
      val task    = KernelTask.validated(
        "copy_add": NonEmpty[String],
        LaunchGeometry.Dimensions.One,
        LaunchGeometry.Dimensions.One,
        Vec(KernelTask.Argument.Memory(memory, 0L), KernelTask.Argument.Memory(memory, 0L))
      ).value
      val completion = service.launch(program, task).value
      service.completion(completion) match {
        case Right(CompletionStatus.Failed(error: ExecutionFailure.Unsupported)) =>
          assert(error.message.startsWith("copy_add sm_89 at 0x0:"), error.message)
        case other => fail(s"expected unsupported instruction failure, got $other")
      }
    }
  }

  test("SM89 compiler-produced copy-add executes through the cubin backend without external decoding") {
    val bytes  = Fixture.copyAddCubin.toArray
    val values =
      Vec.tabulate(32)(index => if (index % 3 == 0) Int.MaxValue else if (index % 3 == 1) -1 else index * 31)
    def integers(values: Vec[Int]): Vec[Byte] =
      Vec.from(values).flatMap(value => LittleEndian.toVec(value.toLong, 4))
    Using.resource(ExecutionSession(List(CubinBackend()))) { service =>
      val input  = service.registerAllocation(128).value
      val output = service.registerAllocation(128).value
      assert(service.write(input, 0L, integers(values)).isRight)
      assert(service.write(output, 0L, integers(Vec.fill(32)(0xdeadbeef))).isRight)
      val program = service.load(ProgramEncoding.CUDACubin, Vec.from(bytes)).value
      val task    = KernelTask.validated(
        "copy_add": NonEmpty[String],
        LaunchGeometry.Dimensions(32, 1, 1),
        LaunchGeometry.Dimensions.One,
        Vec(KernelTask.Argument.Memory(output, 0L), KernelTask.Argument.Memory(input, 0L))
      ).value
      val completion = service.launch(program, task).value
      assertEquals(service.completion(completion), Right(CompletionStatus.Succeeded))
      assertEquals(service.read(output, 0L, 128), Right(integers(values.map(_ + 1))))
      assertEquals(service.read(input, 0L, 128), Right(integers(values)))
    }
  }

}
