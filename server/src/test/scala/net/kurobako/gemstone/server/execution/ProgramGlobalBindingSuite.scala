package net.kurobako.gemstone.server.execution
import net.kurobako.gemstone.Vec
import net.kurobako.gemstone.isa.KernelName as ISAKernelName
import net.kurobako.gemstone.memory.AllocationKind
import net.kurobako.gemstone.binary.{LittleEndian, ScalarByteWidth}
import net.kurobako.gemstone.rpc.ExecutionProtocol.*
import net.kurobako.gemstone.vm.{KernelTask, LaunchGeometry, MemoryRegion}
import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*
import java.nio.{ByteBuffer, ByteOrder}
import java.nio.charset.StandardCharsets
import scala.util.Using

final class ProgramGlobalBindingSuite extends munit.FunSuite {
  // gfx900: load through linked 0x3000, increment/store through kernarg pointer, reload linked global,
  // write value and pointer-equality flag to output. Assembly recorded in the bring-up report.
  private val code = Vec[Long](
    0xc00a0100L, 0x00000000L, 0xbf8cc07fL, 0xbe8800ffL, 0x00003000L, 0xbe890080L, 0xc0020284L, 0x00000000L, 0xbf8cc07fL,
    0x800a810aL, 0x7e000280L, 0x7e02020aL, 0xdc708000L, 0x00040100L, 0xbf8c0f70L, 0xc0020284L, 0x00000000L, 0xbf8cc07fL,
    0x7e02020aL, 0xdc708000L, 0x00060100L, 0x880a0804L, 0x880b0905L, 0x870a0b0aL, 0xbf06800aL, 0x850a8081L, 0x7e02020aL,
    0x7e000284L, 0xdc708000L, 0x00060100L, 0xbf810000L
  ).flatMap(value => LittleEndian.toVec(value, ScalarByteWidth(4)))
  private val global                                      = AllocationId(1)
  private val output                                      = AllocationId(2)
  private def service()                                   = ExecutionService(GemstoneBackendCatalog.default, _.run())
  private def setup(service: ExecutionService): ProgramId = {
    assertEquals(service.registerAllocation(32, AllocationKind.Shared), Right(global))
    assertEquals(service.registerAllocation(16, AllocationKind.Shared), Right(output))
    service.load(HSARuntimeBackend.encoding, Vec.from(image())).toOption.get
  }
  private def read(
      service: ExecutionService,
      id: AllocationId,
      offset: Long :| Positive0,
      size: ScalarByteWidth = ScalarByteWidth(4)
  ): Long =
    service.read(id, offset, size.nonNegative)
      .map(bytes => LittleEndian.decode(bytes, 0, size)).toOption.get
  private def launch(service: ExecutionService, program: ProgramId, pointer: KernelTask.Argument): CompletionId = {
    val task = KernelTask.validated(
      ISAKernelName("kernel"),
      LaunchGeometry.Dimensions.One,
      LaunchGeometry.Dimensions.One,
      Vec(pointer, KernelTask.Argument.Memory(MemoryRegion(output.value), 0))
    ).toOption.get
    val completion = service.launch(program, task).toOption.get
    assertEquals(service.completion(completion), Right(CompletionStatus.Succeeded))
    completion
  }

  test("linked globals and synthetic allocation addresses share writes and pointer identity") {
    Using.resource(service()) { s =>
      val program = setup(s)
      assertEquals(
        s.writeGlobal(program, GlobalName("device_global"), 0, LittleEndian.toVec(41, ScalarByteWidth(4))),
        Right(())
      )
      assertEquals(s.bindGlobal(program, GlobalName("device_global"), global, 8), Right(()))
      assertEquals(read(s, global, 8), 41L)
      val first = launch(s, program, KernelTask.Argument.Memory(MemoryRegion(global.value), 8))
      assertEquals(read(s, global, 8), 42L)
      assertEquals(read(s, output, 0), 42L)
      assertEquals(read(s, output, 4), 1L)
      assertEquals(s.releaseCompletion(first), Right(()))
      assertEquals(s.write(global, 8, LittleEndian.toVec(70, ScalarByteWidth(4))), Right(()))
      val synthetic = 0x400000000000L + 8L
      val second    = launch(s, program, KernelTask.Argument.Scalar(LittleEndian.toVec(synthetic, ScalarByteWidth(8))))
      assertEquals(read(s, global, 8), 71L)
      assertEquals(read(s, output, 0), 71L)
      assertEquals(s.releaseCompletion(second), Right(()))
      val third = launch(s, program, KernelTask.Argument.Scalar(LittleEndian.toVec(0x3000, ScalarByteWidth(8))))
      assertEquals(read(s, global, 8), 72L)
      assertEquals(read(s, output, 4), 1L)
      assert(s.completionDirtyRanges(third).toOption.get.exists(_.allocation == global))
      assert(s.releaseAllocation(global).isLeft)
      assertEquals(s.unload(program), Right(()))
      assert(s.releaseAllocation(global).isLeft, "completion must retain implicit bound globals after unload")
      assertEquals(s.releaseCompletion(third), Right(()))
      assertEquals(s.releaseAllocation(global), Right(()))
    }
  }

  test("linked initializer relocations, overlapping aliases and cached programs remain session-private") {
    Using.Manager { use =>
      val a  = use(service())
      val b  = use(service())
      val pa = setup(a); val pb = setup(b)
      assertEquals(a.bindGlobal(pa, GlobalName("device_global"), global, 8), Right(()))
      assertEquals(a.write(global, 8, LittleEndian.toVec(99, ScalarByteWidth(4))), Right(()))
      assertEquals(a.bindGlobal(pa, GlobalName("device_alias"), global, 8), Right(()))
      assertEquals(a.bindGlobal(pa, GlobalName("device_global"), global, 8), Right(()))
      assertEquals(read(a, global, 8), 99L, "alias/idempotent bind must not reset live storage")
      assertEquals(
        a.writeGlobal(pa, GlobalName("device_alias"), 0, LittleEndian.toVec(123, ScalarByteWidth(4))),
        Right(())
      )
      assertEquals(read(a, global, 8), 123L)
      assertEquals(a.bindGlobal(pa, GlobalName("device_pointer"), output, 0), Right(()))
      assertEquals(
        read(a, output, 0, ScalarByteWidth(8)),
        0x3000L,
        "initializer must contain relocated ELF global address"
      )
      assertEquals(b.bindGlobal(pb, GlobalName("device_global"), global, 8), Right(()))
      assertEquals(read(b, global, 8), 0L)
      assertEquals(read(a, global, 8), 123L)
      assert(a.bindGlobal(pa, GlobalName("device_global"), output, 0).isLeft)
      assert(a.bindGlobal(pa, GlobalName("device_alias"), global, 9).isLeft)
      assert(a.bindGlobal(pa, GlobalName("missing"), global, 0).isLeft)
      assert(a.bindGlobal(pa, GlobalName("device_global"), global, 32).isLeft)
    }.get
  }

  test("unsupported and failed bindings roll back allocation retention") {
    Using.resource(service()) { s =>
      val p = setup(s)
      assert(s.bindGlobal(p, GlobalName("missing"), output, 0).isLeft)
      assertEquals(s.releaseAllocation(output), Right(()))
      val ptx = s.load(
        PTXRuntimeBackend.encoding,
        Vec.from(
          ".version 8.0\n.target sm_80\n.address_size 64\n.visible .entry k(){ret;}".getBytes(StandardCharsets.UTF_8)
        )
      ).toOption.get
      assert(s.bindGlobal(ptx, GlobalName("global"), global, 0).left.exists {
        case ExecutionFailure.NotFound(_) => true
        case _                            => false
      })
      assertEquals(s.releaseAllocation(global), Right(()))
    }
  }

  test("allocated custom ELF data sections expose globals while nonallocated sections do not") {
    for {
      kind      <- List(1, 8)
      allocated <- List(false, true)
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
        assertEquals(s.registerAllocation(32, AllocationKind.Shared), Right(global))
        val p = s.load(HSARuntimeBackend.encoding, Vec.from(bytes)).toOption.get
        assertEquals(s.bindGlobal(p, GlobalName("device_global"), global, 8).isRight, allocated)
      }
    }
  }

  private def image(): Array[Byte] = {
    def aligned(value: Int): Int = Math.addExact(value, 7) & ~7
    val sectionNames             =
      "\u0000.shstrtab\u0000.strtab\u0000.symtab\u0000.text\u0000.rodata\u0000.bss\u0000.got\u0000.rela.dyn\u0000"
        .getBytes(StandardCharsets.US_ASCII)
    val symbolNames =
      "\u0000kernel\u0000kernel.kd\u0000device_global\u0000device_pointer\u0000device_alias\u0000".getBytes(
        StandardCharsets.US_ASCII
      )
    val target            = "amdgcn-amd-amdhsa--gfx900\u0000".getBytes(StandardCharsets.US_ASCII)
    val sectionNameOffset = 64
    val symbolNameOffset  = aligned(sectionNameOffset + sectionNames.length)
    val symbolTableOffset = aligned(symbolNameOffset + symbolNames.length)
    val textOffset        = aligned(symbolTableOffset + 6 * 24)
    val readOnlyOffset    = aligned(textOffset + code.size)
    val readOnlySize      = 64 + target.length
    val sectionTable      = aligned(readOnlyOffset + readOnlySize)
    val sectionCount      = 9
    val gotOffset         = aligned(sectionTable + sectionCount * 64)
    val relocationOffset  = aligned(gotOffset + 8)
    val bytes             = Array.ofDim[Byte](relocationOffset + 24)
    val buffer            = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN)

    buffer.put(0, 0x7f.toByte)
    buffer.put(1, 'E'.toByte)
    buffer.put(2, 'L'.toByte)
    buffer.put(3, 'F'.toByte)
    buffer.put(4, 2.toByte)
    buffer.put(5, 1.toByte)
    buffer.putShort(16, 3.toShort)
    buffer.putShort(18, 224.toShort)
    buffer.putInt(20, 1)
    buffer.putLong(40, sectionTable.toLong)
    buffer.putShort(52, 64.toShort)
    buffer.putShort(58, 64.toShort)
    buffer.putShort(60, sectionCount.toShort)
    buffer.putShort(62, 1.toShort)
    Array.copy(sectionNames, 0, bytes, sectionNameOffset, sectionNames.length)
    Array.copy(symbolNames, 0, bytes, symbolNameOffset, symbolNames.length)
    val _ = code.copyToArray(bytes, textOffset, code.size)
    LittleEndian.write(16L, bytes, readOnlyOffset, ScalarByteWidth(4))
    LittleEndian.write(8L, bytes, readOnlyOffset + 4, ScalarByteWidth(4))
    LittleEndian.write(16L, bytes, readOnlyOffset + 8, ScalarByteWidth(4))
    LittleEndian.write(0x80L, bytes, readOnlyOffset + 52, ScalarByteWidth(4))
    LittleEndian.write(0x08L, bytes, readOnlyOffset + 56, ScalarByteWidth(2))
    Array.copy(target, 0, bytes, readOnlyOffset + 64, target.length)

    def section(
        index: Int,
        name: Int,
        kind: Int,
        address: Long,
        offset: Int,
        size: Int,
        link: Int = 0,
        alignment: Int = 1,
        entrySize: Int = 0
    ): Unit = {
      val start = sectionTable + index * 64
      buffer.putInt(start, name)
      buffer.putInt(start + 4, kind)
      buffer.putLong(start + 16, address)
      buffer.putLong(start + 24, offset.toLong)
      buffer.putLong(start + 32, size.toLong)
      buffer.putInt(start + 40, link)
      buffer.putLong(start + 48, alignment.toLong)
      val _ = buffer.putLong(start + 56, entrySize.toLong)
    }
    section(1, 1, 3, 0L, sectionNameOffset, sectionNames.length)
    section(2, 11, 3, 0L, symbolNameOffset, symbolNames.length)
    section(3, 19, 2, 0L, symbolTableOffset, 6 * 24, link = 2, alignment = 8, entrySize = 24)
    section(4, 27, 1, 0x1000L, textOffset, code.size, alignment = 4)
    section(5, 33, 1, 0x2000L, readOnlyOffset, readOnlySize, alignment = 8)
    section(6, 41, 8, 0x3000L, 0, 16, alignment = 8)

    def symbol(index: Int, name: Int, kind: Int, section: Int, value: Long, size: Long): Unit = {
      val start = symbolTableOffset + index * 24
      buffer.putInt(start, name)
      buffer.put(start + 4, kind.toByte)
      buffer.putShort(start + 6, section.toShort)
      buffer.putLong(start + 8, value)
      val _ = buffer.putLong(start + 16, size)
    }
    symbol(1, 1, 2, 4, 0x1000L, code.size.toLong)
    symbol(2, 8, 1, 5, 0x2000L, 64L)
    symbol(3, 18, 1, 6, 0x3000L, 16L)
    symbol(4, 32, 1, 7, 0x4000L, 8L)
    symbol(5, 47, 1, 6, 0x3000L, 8L)
    section(7, 46, 1, 0x4000L, gotOffset, 8, alignment = 8)
    section(8, 51, 4, 0x5000L, relocationOffset, 24, link = 3, alignment = 8, entrySize = 24)
    // R_AMDGPU_ABS64 for device_global (symbol 3) stored into the .got slot.
    LittleEndian.write(0x4000L, bytes, relocationOffset, ScalarByteWidth(8))
    LittleEndian.write((3L << 32) | 3L, bytes, relocationOffset + 8, ScalarByteWidth(8))
    LittleEndian.write(0L, bytes, relocationOffset + 16, ScalarByteWidth(8))
    bytes
  }
}
