package net.kurobako.gemstone.server.backend

import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.charset.StandardCharsets
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.memory.AllocationId.*
import net.kurobako.gemstone.rpc.*
import net.kurobako.gemstone.rpc.ExecutionRPC.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.vm.*
import scala.util.Using

class ProgramGlobalBindingSuite extends munit.FunSuite {
  private val Booleans = List(false, true)

  // gfx900: load through linked 0x3000, increment/store through kernarg pointer, reload linked global,
  // write value and pointer-equality flag to output. Assembly recorded in the bring-up report.
  private val code = Vec[Long](
    0xc00a0100L, 0x00000000L, 0xbf8cc07fL, 0xbe8800ffL, 0x00003000L, 0xbe890080L, 0xc0020284L, 0x00000000L, 0xbf8cc07fL,
    0x800a810aL, 0x7e000280L, 0x7e02020aL, 0xdc708000L, 0x00040100L, 0xbf8c0f70L, 0xc0020284L, 0x00000000L, 0xbf8cc07fL,
    0x7e02020aL, 0xdc708000L, 0x00060100L, 0x880a0804L, 0x880b0905L, 0x870a0b0aL, 0xbf06800aL, 0x850a8081L, 0x7e02020aL,
    0x7e000284L, 0xdc708000L, 0x00060100L, 0xbf810000L
  ).flatMap(value => LittleEndian.toVec(value, 4))
  private val global                                      = AllocationId(1L)
  private val output                                      = AllocationId(2L)
  private def service()                                   = ExecutionSession(Backends.default().programs)
  private def setup(service: ExecutionSession): ProgramId = {
    assertEquals(service.registerAllocation(32), Right(global))
    assertEquals(service.registerAllocation(16), Right(output))
    service.load(ProgramEncoding.HSACodeObject, Vec.from(image())).toOption.get
  }
  private def read(
      service: ExecutionSession,
      id: AllocationId,
      offset: Long,
      size: Int in 0 ~ 8 = 4
  ): Long =
    service.read(id, offset.unsafe[Long >= 0], size.widen[Int >= 0])
      .map(bytes => LittleEndian.decode(bytes, 0, size)).toOption.get
  private def launch(service: ExecutionSession, program: ProgramId, pointer: KernelTask.Argument): CompletionId = {
    val task = KernelTask.validated(
      "kernel",
      LaunchGeometry.Dimensions.One,
      LaunchGeometry.Dimensions.One,
      Vec(pointer, KernelTask.Argument.Memory(output, 0L))
    ).toOption.get
    val completion = service.launch(program, task).toOption.get
    assertEquals(service.completion(completion), Right(CompletionStatus.Succeeded))
    completion
  }

  test("linked globals and synthetic allocation addresses share writes and pointer identity") {
    Using.resource(service()) { s =>
      val program = setup(s)
      assertEquals(
        s.writeGlobal(
          program,
          "device_global",
          0L,
          LittleEndian.toVec(41, 4)
        ),
        Right(())
      )
      assertEquals(s.bindGlobal(program, "device_global", global, 8L), Right(()))
      assertEquals(read(s, global, 8), 41L)
      val first = launch(s, program, KernelTask.Argument.Memory(global, 8L))
      assertEquals(read(s, global, 8), 42L)
      assertEquals(read(s, output, 0), 42L)
      assertEquals(read(s, output, 4), 1L)
      assertEquals(s.releaseCompletion(first), Right(()))
      assertEquals(
        s.write(global, 8L, LittleEndian.toVec(70, 4)),
        Right(())
      )
      val synthetic = 0x400000000000L + 8L
      val second    =
        launch(s, program, KernelTask.Argument.Scalar(LittleEndian.toVec(synthetic, 8).unsafe))
      assertEquals(read(s, global, 8), 71L)
      assertEquals(read(s, output, 0), 71L)
      assertEquals(s.releaseCompletion(second), Right(()))
      val third = launch(s, program, KernelTask.Argument.Scalar(LittleEndian.toVec(0x3000, 8).unsafe))
      assertEquals(read(s, global, 8), 72L)
      assertEquals(read(s, output, 4), 1L)
      assert(s.completionDirtyRanges(
        id = third,
        offset = 0,
        limit = ExecutionRPC.MaximumElements
      ).toOption.get.exists(_.allocation == global))
      assert(s.releaseAllocation(global).isLeft)
      assertEquals(s.unload(program), Right(()))
      assert(s.releaseAllocation(global).isLeft, "completion must retain implicit bound globals after unload")
      assertEquals(s.releaseCompletion(third), Right(()))
      assertEquals(s.releaseAllocation(global), Right(()))
    }
  }

  test("linked initialiser relocations, overlapping aliases and cached programs remain session-private") {
    Using.Manager { use =>
      val a  = use(service())
      val b  = use(service())
      val pa = setup(a); val pb = setup(b)
      assertEquals(a.bindGlobal(pa, "device_global", global, 8L), Right(()))
      assertEquals(
        a.write(global, 8L, LittleEndian.toVec(99, 4)),
        Right(())
      )
      assertEquals(a.bindGlobal(pa, "device_alias", global, 8L), Right(()))
      assertEquals(a.bindGlobal(pa, "device_global", global, 8L), Right(()))
      assertEquals(read(a, global, 8), 99L, "alias/idempotent bind must not reset live storage")
      assertEquals(
        a.writeGlobal(
          pa,
          "device_alias",
          0L,
          LittleEndian.toVec(123, 4)
        ),
        Right(())
      )
      assertEquals(read(a, global, 8), 123L)
      assertEquals(a.bindGlobal(pa, "device_pointer", output, 0L), Right(()))
      assertEquals(
        read(a, output, 0, 8),
        0x3000L,
        "initialiser must contain relocated ELF global address"
      )
      assertEquals(b.bindGlobal(pb, "device_global", global, 8L), Right(()))
      assertEquals(read(b, global, 8), 0L)
      assertEquals(read(a, global, 8), 123L)
      assert(a.bindGlobal(pa, "device_global", output, 0L).isLeft)
      assert(a.bindGlobal(pa, "device_alias", global, 9L).isLeft)
      assert(a.bindGlobal(pa, "missing", global, 0L).isLeft)
      assert(a.bindGlobal(pa, "device_global", global, 32L).isLeft)
    }.get
  }

  test("unsupported and failed bindings roll back allocation retention") {
    Using.resource(service()) { s =>
      val p = setup(s)
      assert(s.bindGlobal(p, "missing", output, 0L).isLeft)
      assertEquals(s.releaseAllocation(output), Right(()))
      val ptx = s.load(
        ProgramEncoding.PTXText,
        Vec.from(
          ".version 8.0\n.target sm_80\n.address_size 64\n.visible .entry k(){ret;}".getBytes(StandardCharsets.UTF_8)
        )
      ).toOption.get
      assert(s.bindGlobal(ptx, "global", global, 0L).left.exists {
        case ExecutionFailure.NotFound(_) => true
        case _                            => false
      })
      assertEquals(s.releaseAllocation(global), Right(()))
    }
  }

  test("allocated custom ELF data sections expose globals while nonallocated sections do not") {
    for {
      kind      <- List(1, 8)
      allocated <- Booleans
    } {
      val bytes  = image()
      val buffer = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN)
      val table  = buffer.getLong(40).toInt
      // Rename .bss to .env without moving section names or symbol records.
      val names = buffer.getLong(table + 64 + 24).toInt
      Array.copy(".env".getBytes(StandardCharsets.US_ASCII), 0, bytes, names + 41, 4)
      buffer.putInt(table + 6 * 64 + 4, kind)
      buffer.putLong(table + 6 * 64 + 8, if (allocated) 2L else 0L)
      // PROGBITS uses an existing zero-filled area; NOBITS ignores its file offset.
      buffer.putLong(table + 6 * 64 + 24, table.toLong)
      Using.resource(service()) { s =>
        assertEquals(s.registerAllocation(32), Right(global))
        val p = s.load(ProgramEncoding.HSACodeObject, Vec.from(bytes)).toOption.get
        assertEquals(
          s.bindGlobal(p, "device_global", global, 8L).isRight,
          allocated
        )
      }
    }
  }

  private def image(): Array[Byte] = elfImage.amdgpu(
    Vec.toArray(code),
    kernargSize = 16,
    kernelCodeProperties = 0x08,
    globalPointer = true,
    extraSymbols = Seq(
      elfImage.Symbol("device_pointer", 1, 7, 0x4000L, 8L),
      elfImage.Symbol("device_alias", 1, 6, 0x3000L, 8L)
    )
  )
}
