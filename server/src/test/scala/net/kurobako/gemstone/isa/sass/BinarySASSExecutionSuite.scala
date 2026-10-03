package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.rpc.*
import net.kurobako.gemstone.rpc.ExecutionProtocol.*
import net.kurobako.gemstone.server.backend.{Cubin as CubinBackend, *}
import net.kurobako.gemstone.vm.*
import scala.util.Using

class BinarySASSExecutionSuite extends munit.FunSuite {
  test("unsupported binary instructions report the kernel and offset directly") {
    val image   = CubinImageSuite.image.clone()
    val module  = Cubin.decode(image).fold(fail(_), identity(_))
    val kernel  = module.kernels.find(_.name == "copy_add").getOrElse(fail("missing copy_add"))
    val section = elf.sections64(image).fold(message => fail(message.raw), identity(_))(kernel.section.raw)
    java.util.Arrays.fill(image, section.offset.raw, (section.offset + 16).raw, 0.toByte)
    Using.resource(ExecutionSession(Backends.default().programs)) { service =>
      val memory  = service.registerAllocation(128L, AllocationKind.Shared).toOption.get
      val program = service.load(CubinBackend.encoding, Vec.from(image)).fold(error => fail(error.message), identity)
      val task    = KernelTask.validated(
        "copy_add": NonEmpty[String],
        LaunchGeometry.Dimensions.One,
        LaunchGeometry.Dimensions.One,
        Vec(KernelTask.Argument.Memory(memory, 0L), KernelTask.Argument.Memory(memory, 0L))
      ).toOption.get
      val completion = service.launch(program, task).fold(error => fail(error.message), identity)
      service.completion(completion) match {
        case Right(CompletionStatus.Failed(error: ExecutionFailure.Unsupported)) =>
          assert(error.message.startsWith("copy_add sm_89 at 0x0:"), error.message)
        case other => fail(s"expected unsupported instruction failure, got $other")
      }
    }
  }

  test("SM89 compiler-produced copy-add executes through the cubin backend without external decoding") {
    val bytes  = CubinImageSuite.image
    val values =
      Vec.tabulate(32)(index => if (index % 3 == 0) Int.MaxValue else if (index % 3 == 1) -1 else index * 31)
    def integers(values: Vec[Int]): Vec[Byte] =
      Vec.from(values).flatMap(value => LittleEndian.toVec(value.toLong, 4))
    Using.resource(ExecutionSession(Backends.default().programs)) { service =>
      val input  = service.registerAllocation(128L, AllocationKind.Shared).toOption.get
      val output = service.registerAllocation(128L, AllocationKind.Shared).toOption.get
      assert(service.write(input, 0L, integers(values)).isRight)
      assert(service.write(output, 0L, integers(Vec.fill(32)(0xdeadbeef))).isRight)
      val program = service.load(CubinBackend.encoding, Vec.from(bytes)).fold(error => fail(error.message), identity)
      val task    = KernelTask.validated(
        "copy_add": NonEmpty[String],
        LaunchGeometry.Dimensions(32, 1, 1),
        LaunchGeometry.Dimensions.One,
        Vec(KernelTask.Argument.Memory(output, 0L), KernelTask.Argument.Memory(input, 0L))
      ).toOption.get
      val completion = service.launch(program, task).fold(error => fail(error.message), identity)
      assertEquals(service.completion(completion), Right(CompletionStatus.Succeeded))
      assertEquals(service.read(output, 0L, 128), Right(integers(values.map(_ + 1))))
      assertEquals(service.read(input, 0L, 128), Right(integers(values)))
    }
  }

}
