package net.kurobako.gemstone.server
import net.kurobako.gemstone.Col
import net.kurobako.gemstone.execution.*
import net.kurobako.gemstone.memory.AllocationKind
import net.kurobako.gemstone.binary.LittleEndian
import java.nio.{ByteBuffer, ByteOrder}
import java.nio.charset.StandardCharsets
import java.util.concurrent.{CountDownLatch, TimeUnit}
import java.util.concurrent.atomic.{AtomicInteger, AtomicReference}

final class ProgramGlobalBindingSuite extends munit.FunSuite {
  // gfx900: load through linked 0x3000, increment/store through kernarg pointer, reload linked global,
  // write value and pointer-equality flag to output. Assembly recorded in the bring-up report.
  private val code = Col[Long](
    0xc00a0100L, 0x00000000L, 0xbf8cc07fL, 0xbe8800ffL, 0x00003000L, 0xbe890080L, 0xc0020284L, 0x00000000L, 0xbf8cc07fL,
    0x800a810aL, 0x7e000280L, 0x7e02020aL, 0xdc708000L, 0x00040100L, 0xbf8c0f70L, 0xc0020284L, 0x00000000L, 0xbf8cc07fL,
    0x7e02020aL, 0xdc708000L, 0x00060100L, 0x880a0804L, 0x880b0905L, 0x870a0b0aL, 0xbf06800aL, 0x850a8081L, 0x7e02020aL,
    0x7e000284L, 0xdc708000L, 0x00060100L, 0xbf810000L
  ).flatMap(value => LittleEndian.toCol(value, 4))
  private val global                                      = AllocationId.fromLong(1).toOption.get
  private val output                                      = AllocationId.fromLong(2).toOption.get
  private def service()                                   = ExecutionService(RegisteredProgramExecutor.default, _.run())
  private def setup(service: ExecutionService): ProgramId = {
    assertEquals(service.registerAllocation(global, 32, AllocationKind.Shared), Right(()))
    assertEquals(service.registerAllocation(output, 16, AllocationKind.Shared), Right(()))
    service.load(ProgramFormat.AMDGPUCodeObject, Col.from(image())).toOption.get
  }
  private def read(service: ExecutionService, id: AllocationId, offset: Long, size: Int = 4): Long =
    service.read(id, offset, size).map(bytes => LittleEndian.decode(bytes, 0, size)).toOption.get
  private def launch(service: ExecutionService, program: ProgramId, pointer: KernelArgument): CompletionId = {
    val dispatch = KernelDispatch.create(
      program,
      "kernel",
      Dimensions.One,
      Dimensions.One,
      Col(pointer, KernelArgument.Allocation(output, 0))
    ).toOption.get
    val completion = service.launch(dispatch).toOption.get
    assertEquals(service.completion(completion), Right(CompletionStatus.Succeeded))
    completion
  }

  test("linked globals and typed/legacy allocation aliases share writes and pointer identity") {
    val s = service()
    try {
      val program = setup(s)
      assertEquals(s.writeGlobal(program, "device_global", 0, LittleEndian.toCol(41, 4)), Right(()))
      assertEquals(s.bindGlobal(program, "device_global", global, 8), Right(()))
      assertEquals(read(s, global, 8), 41L)
      val first = launch(s, program, KernelArgument.Allocation(global, 8))
      assertEquals(read(s, global, 8), 42L)
      assertEquals(read(s, output, 0), 42L)
      assertEquals(read(s, output, 4), 1L)
      assertEquals(s.releaseCompletion(first), Right(()))
      assertEquals(s.write(global, 8, LittleEndian.toCol(70, 4)), Right(()))
      // The old process-neutral allocation encoding remains a storage alias after canonical binding.
      val legacy = 0x400000000000L + 8L
      val second = launch(s, program, KernelArgument.Scalar(LittleEndian.toCol(legacy, 8)))
      assertEquals(read(s, global, 8), 71L)
      assertEquals(read(s, output, 0), 71L)
      assertEquals(s.releaseCompletion(second), Right(()))
      val third = launch(s, program, KernelArgument.Scalar(LittleEndian.toCol(0x3000, 8)))
      assertEquals(read(s, global, 8), 72L)
      assertEquals(read(s, output, 4), 1L)
      assert(s.completionDirtyRanges(third).toOption.get.exists(_.allocation == global))
      assert(s.releaseAllocation(global).isLeft)
      assertEquals(s.unload(program), Right(()))
      assert(s.releaseAllocation(global).isLeft, "completion must retain implicit bound globals after unload")
      assertEquals(s.releaseCompletion(third), Right(()))
      assertEquals(s.releaseAllocation(global), Right(()))
    } finally s.close()
  }

  test("linked initializer relocations, overlapping aliases and cached programs remain session-private") {
    val a = service(); val b = service()
    try {
      val pa = setup(a); val pb = setup(b)
      assertEquals(a.bindGlobal(pa, "device_global", global, 8), Right(()))
      assertEquals(a.write(global, 8, LittleEndian.toCol(99, 4)), Right(()))
      assertEquals(a.bindGlobal(pa, "device_alias", global, 8), Right(()))
      assertEquals(a.bindGlobal(pa, "device_global", global, 8), Right(()))
      assertEquals(read(a, global, 8), 99L, "alias/idempotent bind must not reset live storage")
      assertEquals(a.writeGlobal(pa, "device_alias", 0, LittleEndian.toCol(123, 4)), Right(()))
      assertEquals(read(a, global, 8), 123L)
      assertEquals(a.bindGlobal(pa, "device_pointer", output, 0), Right(()))
      assertEquals(read(a, output, 0, 8), 0x3000L, "initializer must contain relocated ELF global address")
      assertEquals(b.bindGlobal(pb, "device_global", global, 8), Right(()))
      assertEquals(read(b, global, 8), 0L)
      assertEquals(read(a, global, 8), 123L)
      assert(a.bindGlobal(pa, "device_global", output, 0).isLeft)
      assert(a.bindGlobal(pa, "device_alias", global, 9).isLeft)
      assert(a.bindGlobal(pa, "missing", global, 0).isLeft)
      assert(a.bindGlobal(pa, "device_global", global, 32).isLeft)
      assert(a.bindGlobal(pa, "", global, 0).isLeft)
      assert(a.bindGlobal(pa, "device_global", global, -1).isLeft)
    } finally { a.close(); b.close() }
  }

  test("unsupported and failed bindings roll back allocation retention") {
    val s = service()
    try {
      val p = setup(s)
      assert(s.bindGlobal(p, "missing", output, 0).isLeft)
      assertEquals(s.releaseAllocation(output), Right(()))
      val ptx = s.load(
        ProgramFormat.PTX,
        Col.from(
          ".version 8.0\n.target sm_80\n.address_size 64\n.visible .entry k(){ret;}".getBytes(StandardCharsets.UTF_8)
        )
      ).toOption.get
      assert(s.bindGlobal(ptx, "global", global, 0).left.exists(_.isInstanceOf[ExecutionFailure.Unsupported]))
      assertEquals(s.releaseAllocation(global), Right(()))
    } finally s.close()
  }

  test("in-progress binding reserves ownership and close defers backend unload") {
    val entered     = CountDownLatch(1)
    val resume      = CountDownLatch(1)
    val unloads     = AtomicInteger()
    val initialized = AtomicReference(Option.empty[Either[ExecutionFailure, Unit]])
    final class GatedExecutor extends ProgramExecutor {
      def load(id: ProgramId, format: ProgramFormat, image: Col[Byte]) = Right(())
      def unload(id: ProgramId) = { val _ = unloads.incrementAndGet(); Right(()) }
      def launch(dispatch: KernelDispatch, memory: ExecutionMemory, control: ExecutionControl) = Right(())
      override def bindGlobal(
          id: ProgramId,
          name: String,
          allocation: AllocationId,
          offset: Long,
          allocationSize: Long,
          memory: ExecutionMemory
      ): Either[ExecutionFailure, Unit] = {
        entered.countDown()
        if (!resume.await(5L, TimeUnit.SECONDS)) Left(ExecutionFailure.Failed("binding gate timed out"))
        else {
          val result = memory.write(allocation, offset, Col[Byte](42))
          initialized.set(Some(memory.read(allocation, offset, 1).map(_ => ())))
          result
        }
      }
    }
    val s      = ExecutionService(GatedExecutor(), _.run())
    val p      = setup(s)
    val result = AtomicReference(Option.empty[Either[ExecutionFailure, Unit]])
    val worker = Thread(() => result.set(Some(s.bindGlobal(p, "global", global, 0))))
    worker.start()
    try {
      assert(entered.await(5L, TimeUnit.SECONDS), "backend bind did not enter")
      assert(s.releaseAllocation(global).isLeft)
      assert(s.unload(p).isLeft)
      val dispatch = KernelDispatch.create(p, "kernel", Dimensions.One, Dimensions.One, Col.empty).toOption.get
      assert(s.launch(dispatch).isLeft)
      s.close()
      assertEquals(unloads.get(), 0, "close unloaded backend while initialization still used its state")
    } finally {
      resume.countDown()
      worker.join(5000L)
      s.close()
    }
    assert(!worker.isAlive, "binding did not finish")
    assertEquals(initialized.get(), Some(Right(())), "close reclaimed reserved allocation too early")
    assertEquals(result.get().map(_.isLeft), Some(true), "closed session cannot report a successful binding")
    assertEquals(unloads.get(), 1)
    assert(s.read(global, 0, 1).isLeft)
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
      System.arraycopy(".env".getBytes(StandardCharsets.US_ASCII), 0, bytes, names + 41, 4)
      buffer.putInt(table + 6 * 64 + 4, kind)
      buffer.putLong(table + 6 * 64 + 8, if (allocated) 2L else 0L)
      // PROGBITS uses an existing zero-filled area; NOBITS ignores its file offset.
      buffer.putLong(table + 6 * 64 + 24, table.toLong)
      val s = service()
      try {
        assertEquals(s.registerAllocation(global, 32, AllocationKind.Shared), Right(()))
        val p = s.load(ProgramFormat.AMDGPUCodeObject, Col.from(bytes)).toOption.get
        assertEquals(s.bindGlobal(p, "device_global", global, 8).isRight, allocated)
      } finally s.close()
    }
  }

  private def image(): Array[Byte] = {
    def aligned(value: Int): Int = (value + 7) & ~7
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
    System.arraycopy(sectionNames, 0, bytes, sectionNameOffset, sectionNames.length)
    System.arraycopy(symbolNames, 0, bytes, symbolNameOffset, symbolNames.length)
    val _ = code.copyToArray(bytes, textOffset, code.size)
    LittleEndian.write(16L, bytes, readOnlyOffset, 4)
    LittleEndian.write(8L, bytes, readOnlyOffset + 4, 4)
    LittleEndian.write(16L, bytes, readOnlyOffset + 8, 4)
    LittleEndian.write(0x80L, bytes, readOnlyOffset + 52, 4)
    LittleEndian.write(0x08L, bytes, readOnlyOffset + 56, 2)
    System.arraycopy(target, 0, bytes, readOnlyOffset + 64, target.length)

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
    LittleEndian.write(0x4000L, bytes, relocationOffset, 8)
    LittleEndian.write((3L << 32) | 3L, bytes, relocationOffset + 8, 8)
    LittleEndian.write(0L, bytes, relocationOffset + 16, 8)
    bytes
  }
}
