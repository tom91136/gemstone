package net.kurobako.gemstone.isa.sass

import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.charset.StandardCharsets
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.sass.Harness.*
import net.kurobako.gemstone.rpc.*
import net.kurobako.gemstone.rpc.ExecutionRPC.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.testing.either.*
import net.kurobako.gemstone.vm.*
import scala.util.Using

class DeviceLaunchSuite extends Suite {

  private val Width = 16

  private val launcher =
    """UMOV UR4, 0
      |UMOV UR5, 0
      |MOV R6, 0x1
      |MOV R7, 0x1
      |MOV R8, 0x1
      |MOV R9, 0x1
      |MOV R10, 0x1
      |MOV R11, 0x1
      |MOV R12, RZ
      |MOV R5, UR5
      |MOV R4, UR4
      |CALL.ABS.NOINC 0x0
      |LDC.64 R2, c[0x0][0x160]
      |ST.E.64 [R4.64], R2
      |MOV R6, RZ
      |MOV R7, RZ
      |CALL.ABS.NOINC 0x0
      |EXIT""".stripMargin

  private val streams =
    """LDC.64 R4, c[0x0][0x160]
      |MOV R6, 0x1
      |CALL.ABS.NOINC 0x0
      |LDC.64 R2, c[0x0][0x160]
      |STG.E [R2.64+0x8], R4
      |LDG.E.64 R4, [R2.64]
      |CALL.ABS.NOINC 0x0
      |STG.E [R2.64+0xc], R4
      |EXIT""".stripMargin

  private val synchronize =
    """LDC.64 R2, c[0x0][0x160]
      |MOV R4, 0x1
      |CALL.ABS.NOINC 0x0
      |STG.E [R2.64], R4
      |EXIT""".stripMargin

  private val child = "LDC.64 R2, c[0x0][0x160]; MOV R5, 0x2a; STG.E [R2.64], R5; EXIT"

  // libcudadevrt's wrappers call these driver functions, and a kernel loads a child's function descriptor into UR4:UR5
  // with an R_CUDA_FUNC_DESC32 pair
  private def module(parent: String, relocations: Vec[(Int, String, Long)]): Cubin.Module = {
    val base = Harness.module(
      89,
      Vec(Entry("child", 0x160, Vec(8), registers = 8), Entry("parent", 0x160, Vec(8), registers = 16))
    )
    val count  = parent.linesIterator.size
    val text   = base.sections.find(_.name == ".text.parent").get
    val sized  = text.copy(size = (count * Width).unsafe[Int >= 0], bytes = Vec.fill(count * Width)(0.toByte))
    val names  = relocations.map(_._2).distinct
    val strtab = Vec.from(names.map(name => s"$name\u0000").mkString("\u0000", "", "").getBytes(StandardCharsets.UTF_8))
    val strings = Cubin.Section(base.sections.nonNegativeSize, ".strtab", 3, 0, strtab.nonNegativeSize, 0, 0, strtab)
    val symbol  = ByteBuffer.allocate(24 * (names.size + 1)).order(ByteOrder.LITTLE_ENDIAN)
    val _       = names.zipWithIndex.foldLeft(1) { case (offset, (name, index)) =>
      symbol.putInt(24 * (index + 1), offset).put(24 * (index + 1) + 4, 0x12.toByte)
      offset + name.length + 1
    }
    val bytes   = Vec.from(symbol.array())
    val symbols = Cubin.Section(strings.index + 1, ".symtab", 2, 0, bytes.nonNegativeSize, strings.index, 0, bytes)
    val entries = relocations.flatMap { (offset, name, kind) =>
      LittleEndian.toVec(offset.toLong * Width, 8) ++ LittleEndian.toVec(((names.indexOf(name) + 1L) << 32) | kind, 8)
    }
    val table = Cubin.Section(
      symbols.index + 1,
      ".rel.text.parent",
      9,
      0,
      entries.nonNegativeSize,
      symbols.index,
      sized.index,
      entries
    )
    base.copy(sections = base.sections.updated(sized.index.raw, sized) ++ Vec(strings, symbols, table))
  }

  private def functions(text: String, name: String): Vec[DecodedFunction] =
    Vec(DecodedFunction(
      name,
      Listing.program(text, name).value.instructions.zipWithIndex.map((instruction, index) =>
        instruction.copy(address = (index * Width).unsafe)
      )
    ))

  private class Images(module: Cubin.Module, parent: String) extends Backend {
    override val format: ProgramFormat                                  = ProgramFormat.CUDACubin
    override def load(bytes: Vec[Byte]): Either[Fault, Backend.Program] =
      Right(LoadedProgram(
        module,
        Map[NonEmpty[String], Vec[DecodedFunction]](
          ("parent": NonEmpty[String]) -> functions(parent, "parent"),
          ("child": NonEmpty[String])  -> functions(child, "child")
        ),
        Workgroups.Sequential,
        Harness.text
      ))
  }

  private def launch(
      parent: String,
      relocations: Vec[(Int, String, Long)],
      size: Int in 1 ~ 64
  ): (Either[ExecutionFailure, CompletionStatus], Vec[Byte]) =
    Using.resource(ExecutionSession(List(Images(module(parent, relocations), parent)))) { service =>
      val program =
        service.load(ProgramEncoding.CUDACubin, LittleEndian.toVec(0L, 4)).fold(e => fail(e.message), identity)
      val output = service.registerAllocation(size.toLong.unsafe[Long > 0]).value
      val heap   = service.registerAllocation(4096).value
      val task   = KernelTask.validated(
        "parent": NonEmpty[String],
        LaunchGeometry.Dimensions.One,
        LaunchGeometry.Dimensions.One,
        Vec(KernelTask.Argument.Memory(output, 0L)),
        deviceHeap = Some(KernelTask.DeviceHeap(heap, 0L, 4096L))
      ).value
      val completion = service.launch(program, task).fold(e => fail(e.message), identity)
      (service.completion(completion), service.read(AllocationRange(output, 0L, size.widen[Int >= 0])).value)
    }

  test("a kernel launches the child named by its function descriptor through the driver's parameter buffer") {
    val relocations = Vec(
      (0, "child", 0x3eL),
      (1, "child", 0x3fL),
      (11, "cnpGetParameterBufferV2", 0x3aL),
      (16, "cnpLaunchDeviceV2", 0x3aL)
    )
    assertEquals(launch(launcher, relocations, 4), (Right(CompletionStatus.Succeeded), LittleEndian.toVec(0x2aL, 4)))
  }

  test("device streams are created into the caller's handle and destroyed without error") {
    val (status, output) = launch(streams, Vec((2, "cnpStreamCreate", 0x3aL), (6, "cnpStreamDestroy", 0x3aL)), 16)
    assertEquals(status, Right(CompletionStatus.Succeeded))
    assertNotEquals(LittleEndian.decodeInt64(output, 0), 0L)
    assertEquals(output.drop(8), Vec.fill(8)(0.toByte))
  }

  test("cudaDeviceSynchronize's cnpCtxSynchronize call is linked and reports success") {
    val (status, output) = launch(synchronize, Vec((2, "cnpCtxSynchronize", 0x3aL)), 4)
    assertEquals(status, Right(CompletionStatus.Succeeded))
    assertEquals(LittleEndian.decodeInt32(output, 0), 0)
  }
}
