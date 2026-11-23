package net.kurobako.gemstone.isa.sass

import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.charset.StandardCharsets
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.nvidia.VMIR
import net.kurobako.gemstone.isa.nvidia.VMKernelLaunch
import net.kurobako.gemstone.isa.sass.Harness.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.testing.either.*
import net.kurobako.gemstone.vm.*

class GridSyncSuite extends Suite {

  private val Handle = "__cuda_syscall_CGS_get_intrinsic_handle"
  private val Sync   = "__cuda_syscall_CGS_sync"
  private val Width  = 16
  private val Block  = 32

  // the device runtime's wrappers call the syscalls as CALL.ABS.NOINC relocated to an undefined function: sm_80 and
  // sm_89 use R_CUDA_ABS47_34 (0x3a), sm_90 and later use R_CUDA_ABS55_16_34 (0x4b)
  private def compile(text: String, calls: Vec[(Int, String)], sm: Int = 80, kind: Long = 0x3aL): VMIR.Kernel = {
    val listing = Listing.program(text, "grid").value.instructions.zipWithIndex
      .map((instruction, index) => instruction.copy(address = (index * Width).unsafe))
    val base    = Harness.module(sm, Vec(Entry("grid", 0x160, Vec(8, 8, 8))))
    val size    = listing.size * Width
    val code    = base.sections.head.copy(size = size.unsafe[Int >= 0], bytes = Vec.fill(size)(0.toByte))
    val names   = calls.map(_._2).distinct
    val strings = Vec.from(("\u0000" + names.mkString("\u0000") + "\u0000").getBytes(StandardCharsets.UTF_8))
    val offsets = names.scanLeft(1)((offset, name) => offset + name.length + 1)
    val section = Cubin.Section(base.sections.nonNegativeSize, ".strtab", 3, 0, strings.nonNegativeSize, 0, 0, strings)
    val symbol  = ByteBuffer.allocate(24 * (names.size + 1)).order(ByteOrder.LITTLE_ENDIAN)
    names.indices.foreach(index =>
      symbol.putInt(24 * (index + 1), offsets(index)).put(24 * (index + 1) + 4, 0x12.toByte)
    )
    val symbols = Cubin.Section(
      section.index + 1,
      ".symtab",
      2,
      0,
      symbol.capacity.unsafe[Int >= 0],
      section.index,
      0,
      Vec.from(symbol.array())
    )
    val entries = calls.flatMap((index, name) =>
      LittleEndian.toVec((index * Width).toLong, 8) ++
        LittleEndian.toVec(((names.indexOf(name) + 1).toLong << 32) | kind, 8)
    )
    val table = Cubin.Section(
      symbols.index + 1,
      ".rel.text.grid",
      9,
      0,
      entries.nonNegativeSize,
      symbols.index,
      code.index,
      entries
    )
    val module = base.copy(sections = base.sections.updated(0, code) ++ Vec(section, symbols, table))
    VMCompiler.compile(module, module.kernels.head, Vec(DecodedFunction("grid", listing))).map(_.kernel)
      .fold(error => fail(error.raw), identity)
  }

  private def launch(
      kernel: VMIR.Kernel,
      blocks: Int,
      cooperative: Boolean,
      memory: Memory,
      outputs: Vec[AllocationId],
      schedule: ConcurrencySchedule = ConcurrencySchedule.Fixed
  ): Either[Fault, Unit] = {
    val task = KernelTask.validated(
      "grid",
      LaunchGeometry.Dimensions(Block.unsafe[Int > 0], 1, 1),
      LaunchGeometry.Dimensions(blocks.unsafe[Int > 0], 1, 1),
      (outputs ++ Vec.fill(3 - outputs.size)(memory.allocate(8L))).map(KernelTask.Argument.Memory(_, 0L)),
      cooperative = cooperative
    ).value
    VMKernelLaunch.execute(
      kernel,
      KernelLaunch(task, TaskContext(memory, TaskControl(), _ => Right(()), schedule = schedule), Workgroups.Sequential)
    )
  }

  private def handle(scope: Int, cooperative: Boolean, sm: Int = 80, kind: Long = 0x3aL): (Long, Long) = {
    val kernel = compile(
      s"MOV R4, 0x$scope; CALL.ABS.NOINC 0x0; LDC.64 R2, c[0x0][0x160]; STG.E [R2.64], R4; STG.E [R2.64+0x4], R5; EXIT",
      Vec(1 -> Handle),
      sm,
      kind
    )
    val memory = Memory()
    val output = memory.allocate(Vec.fill(8)(0x55.toByte))
    launch(kernel, 1, cooperative, memory, Vec(output)).fold(error => fail(error.message), identity)
    val words = memory.readWords(output, 2)
    (words(0), words(1))
  }

  test("the grid scope's handle is the hardware constant in a cooperative launch") {
    assertEquals(handle(1, cooperative = true), (0xffffffffL, 0x0007ffffL))
  }

  test("the grid scope's handle is zero when the launch is not cooperative") {
    assertEquals(handle(1, cooperative = false), (0L, 0L))
  }

  test("a multi-grid scope has no handle") {
    assertEquals(handle(2, cooperative = true), (0L, 0L))
  }

  test("sm_90 binds both syscalls through the Hopper call relocation") {
    assertEquals(handle(1, cooperative = true, sm = 90, kind = 0x4bL), (0xffffffffL, 0x0007ffffL))
    val (result, read, status) = synced(2, sm = 90, kind = 0x4bL)
    assertEquals(result, Right(()))
    assertEquals(read, Vec.tabulate(2 * Block)(gid => ((gid ^ Block) + 1).toLong))
    assertEquals(status, Vec.fill(2 * Block)(0L))
  }

  // each thread stores its index, syncs, then reads the thread a block over and stores what it read and the result
  private val syncText =
    "S2R R0, SR_TID.X; S2R R1, SR_CTAID.X; IMAD R0, R1, c[0x0][0x0], R0; LDC.64 R2, c[0x0][0x160]; " +
      "IMAD.WIDE.U32 R8, R0, 0x4, R2; IADD3 R10, R0, 0x1, RZ; STG.E [R8.64], R10; " +
      "MOV R4, 0x1; CALL.ABS.NOINC 0x0; MOV R6, RZ; CALL.ABS.NOINC 0x0; MOV R18, R4; " +
      "LOP3.LUT R11, R0, c[0x0][0x0], RZ, 0x3c, !PT; IMAD.WIDE.U32 R12, R11, 0x4, R2; LDG.E R14, [R12.64]; " +
      "LDC.64 R16, c[0x0][0x168]; IMAD.WIDE.U32 R16, R0, 0x4, R16; STG.E [R16.64], R14; " +
      "LDC.64 R20, c[0x0][0x170]; IMAD.WIDE.U32 R20, R0, 0x4, R20; STG.E [R20.64], R18; EXIT"

  private def synced(
      blocks: Int,
      schedule: ConcurrencySchedule = ConcurrencySchedule.Fixed,
      sm: Int = 80,
      kind: Long = 0x3aL
  ): (Either[Fault, Unit], Vec[Long], Vec[Long]) = {
    val kernel = compile(syncText, Vec(8 -> Handle, 10 -> Sync), sm, kind)
    val memory = Memory()
    val count  = blocks * Block
    val data   = memory.allocate(Vec.fill(count * 4)(0.toByte))
    val read   = memory.allocate(Vec.fill(count * 4)(0.toByte))
    val status = memory.allocate(Vec.fill(count * 4)(0x7f.toByte))
    val result = launch(kernel, blocks, cooperative = true, memory, Vec(data, read, status), schedule)
    (result, memory.readWords(read, count.unsafe[Int >= 0]), memory.readWords(status, count.unsafe[Int >= 0]))
  }

  for (blocks <- Vec(2, 4, 6))
    test(s"a grid sync of $blocks cooperative workgroups publishes every workgroup's stores") {
      val (result, read, status) = synced(blocks)
      assertEquals(result, Right(()))
      assertEquals(read, Vec.tabulate(blocks * Block)(gid => ((gid ^ Block) + 1).toLong))
      assertEquals(status, Vec.fill(blocks * Block)(0L))
    }

  test("a grid sync completes under several schedule seeds") {
    Vec(1L, 2L, 3L, 7L).foreach { seed =>
      val (result, read, _) = synced(4, ConcurrencySchedule.Random(seed))
      assertEquals(result, Right(()))
      assertEquals(read, Vec.tabulate(4 * Block)(gid => ((gid ^ Block) + 1).toLong))
    }
  }

  test("a grid sync in a launch that is not cooperative faults") {
    val kernel = compile("MOV R4, 0x1; MOV R5, RZ; MOV R6, RZ; CALL.ABS.NOINC 0x0; EXIT", Vec(3 -> Sync))
    val memory = Memory()
    val result = launch(kernel, 2, cooperative = false, memory, Vec.empty[AllocationId])
    assert(result.left.exists(_.message.contains("not cooperative")), result.toString)
  }

  test("a workgroup that exits before the grid sync counts as arrived") {
    val kernel = compile(
      "S2R R1, SR_CTAID.X; ISETP.NE.AND P0, PT, R1, RZ, PT; @P0 EXIT; MOV R4, 0x1; MOV R5, RZ; MOV R6, RZ; " +
        "CALL.ABS.NOINC 0x0; EXIT",
      Vec(6 -> Sync)
    )
    val result = launch(kernel, 2, cooperative = true, Memory(), Vec.empty[AllocationId])
    assertEquals(result, Right(()))
  }

  test("a grid sync that lanes of another workgroup cannot reach reports the grid phase") {
    val kernel = compile(
      "S2R R0, SR_TID.X; S2R R1, SR_CTAID.X; ISETP.NE.AND P0, PT, R1, RZ, PT; ISETP.LT.U32.AND P0, PT, R0, 0x10, P0; " +
        "@P0 BRA `(wait); MOV R4, 0x1; MOV R5, RZ; MOV R6, RZ; CALL.ABS.NOINC 0x0; EXIT; wait: BAR.SYNC 0x0; EXIT",
      Vec(8 -> Sync)
    )
    val result = launch(kernel, 2, cooperative = true, Memory(), Vec.empty[AllocationId])
    assert(result.left.exists(_.message.contains("grid phase")), result.toString)
  }

  test("a grid sync with the block-leader flag is unsupported") {
    val kernel = compile("MOV R4, 0x1; MOV R5, RZ; MOV R6, 0x1; CALL.ABS.NOINC 0x0; EXIT", Vec(3 -> Sync))
    val memory = Memory()
    val result = launch(kernel, 2, cooperative = true, memory, Vec.empty[AllocationId])
    assert(result.left.exists(_.message.contains("flag")), result.toString)
  }
}
