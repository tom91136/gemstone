package net.kurobako.gemstone.server.backend

import java.nio.charset.StandardCharsets
import net.kurobako.gemstone.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.binary.Widths.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.constraint.auto.*
import net.kurobako.gemstone.isa.spirv.v1_6.{ISA as SPIRVISA, SPIRV as _, *}
import net.kurobako.gemstone.isa.spirv.v1_6 as spv
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.rpc.*
import net.kurobako.gemstone.rpc.ExecutionProtocol.*
import net.kurobako.gemstone.vm.*
import scala.collection.mutable
import scala.util.Using

class HostMemoryExecutionSuite extends munit.FunSuite {
  private def encoded(value: Long, size: Int where Between[0, 8]): Vec[Byte] =
    LittleEndian.toVec(value, size)
  private def scalar(value: Long, size: Int where Between[0, 8] = 8): KernelTask.Argument =
    KernelTask.Argument.Scalar(encoded(value, size))
  private def bytes(address: Long, value: Long, size: Int where Between[0, 8]): Map[Long, Byte] =
    encoded(value, size).zipWithIndex.map((byte, index) => (address + index.toLong) -> byte).toMap

  private object Async extends scala.concurrent.ExecutionContext {
    override def execute(runnable: Runnable): Unit = {
      val thread = Thread(runnable); thread.setDaemon(true); thread.start()
    }
    override def reportFailure(error: Throwable): Unit = throw error
  }

  private def run(source: String, lanes: Int, arguments: Vec[KernelTask.Argument], host: Map[Long, Byte]) =
    runImage(PTX.encoding, Vec.from(source.getBytes(StandardCharsets.UTF_8)), lanes, arguments, host)

  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private def runImage(
      encoding: String,
      image: Vec[Byte],
      lanes: Int,
      arguments: Vec[KernelTask.Argument],
      host: Map[Long, Byte],
      fragments: List[(Long, Vec[Byte])] = Nil,
      hostBacked: Boolean = false,
      beforeSync: (HostMemorySync, Map[Long, Byte]) => Map[Long, Byte] = (_, _) => Map.empty,
      beforeRead: (HostMemoryRange, scala.collection.Map[Long, Byte]) => Map[Long, Byte] = (_, _) => Map.empty,
      relocations: Vec[KernelTask.PointerRelocation] = Vec.empty,
      afterCompletion: Vec[KernelTask.PointerRelocation] => Unit = _ => ()
  ) =
    Using.resource(ExecutionSession(Backends.default().programs, executionContext = Async)) { service =>
      assertEquals(service.enableHostMemory(), Right(()))
      fragments.foreach { (address, contents) =>
        val allocation =
          service.registerAllocation(
            contents.size.toLong.unsafe[Long > 0],
            AllocationKind.Shared
          ).toOption.get
        assertEquals(service.registerAllocationAddress(allocation, GuestAddress(address), hostBacked), Right(()))
        assertEquals(service.write(allocation, 0L, contents), Right(()))
      }
      val program = service.load(encoding, image).toOption.get
      val task    = KernelTask.validated(
        "probe",
        LaunchGeometry.Dimensions(lanes.unsafe[Int > 0], 1, 1),
        LaunchGeometry.Dimensions.One,
        arguments,
        pointerRelocations = relocations
      ).toOption.get
      val completion = service.launch(program, task).toOption.get
      val reads      = mutable.ArrayBuffer.empty[HostMemoryRange]
      val writes     = mutable.Map.empty[Long, Byte]
      val native     = mutable.Map.from(host)
      var done       = false
      var batches    = 0
      while (!done && batches < 65536) {
        val batch = service.pollHostMemory(completion).fold(error => fail(error.message), identity)
        reads ++= batch.reads
        batch.writes.foreach(range =>
          range.bytes.zipWithIndex.foreach((byte, index) => writes.update(range.address.raw + index, byte))
        )
        batch.writes.foreach(range =>
          range.bytes.zipWithIndex.foreach((byte, index) => native.update(range.address.raw + index, byte))
        )
        batch.sync.foreach { sync =>
          native ++= beforeSync(sync, native.toMap)
          val observed = sync.atomic.fold(0L) { request =>
            val size     = request.size.unsafe[Int where Between[1, 8]]
            val observed = (0 until size.raw).foldLeft(0L)((value, index) =>
              value | ((native(request.address.raw + index).toLong & 255L) << (index * 8))
            )
            if (observed == request.expected) {
              val updated = bytes(request.address.raw, request.desired, size)
              native ++= updated
              writes ++= updated
            }
            observed
          }
          assertEquals(service.supplyHostSync(completion, sync.ticket, observed), Right(()))
          assert(service.supplyHostSync(completion, sync.ticket, observed).isLeft)
        }
        done = batch.completed
        if (batch.reads.nonEmpty) {
          batches += 1
          val replies = batch.reads.map { range =>
            native ++= beforeRead(range, native)
            HostMemoryBytes(
              range.address,
              Vec.from((0 until range.size.raw).map { index =>
                val address = range.address.raw + index
                native.getOrElse(address, fail(s"speculative host read at 0x${address.toHexString}"))
              })
            )
          }
          assertEquals(service.supplyHostMemory(completion, replies), Right(()))
        }
      }
      assert(done)
      assertEquals(service.awaitCompletion(completion), Right(()))
      afterCompletion(service.completionPointerRelocations(
        id = completion,
        offset = 0,
        limit = ExecutionRPC.MaximumElements
      ).toOption.get)
      assertEquals(service.releaseCompletion(completion), Right(()))
      (reads.toVector, writes.toMap, batches)
    }

  private val header = ".version 8.0\n.target sm_80\n.address_size 64\n"

  for (stride <- List(1, 3)) test(s"PTX batches exact active-lane reads with stride $stride") {
    val source                   = header + """
      .visible .entry probe(.param .u64 input, .param .u64 output, .param .u32 stride) {
        .reg .u64 %p, %q, %offset; .reg .u32 %i, %s, %v;
        ld.param.u64 %p, [input]; ld.param.u64 %q, [output]; ld.param.u32 %s, [stride];
        mov.u32 %i, %tid.x; mul.lo.u32 %i, %i, %s; mul.wide.u32 %offset, %i, 4;
        add.u64 %p, %p, %offset; add.u64 %q, %q, %offset;
        ld.global.u32 %v, [%p]; add.u32 %v, %v, 1; st.global.u32 [%q], %v; ret;
      }"""
    val host                     = (0 until 32).flatMap(i => bytes(0x1000L + i * stride * 4, i, 4)).toMap
    val (reads, writes, batches) = run(source, 32, Vec(scalar(0x1000), scalar(0x8000), scalar(stride, 4)), host)
    assertEquals(batches, 1)
    assertEquals(reads.size, if (stride == 1) 1 else 32)
    assertEquals(reads.map(_.size.raw).sum, 128)
    assertEquals(writes, (0 until 32).flatMap(i => bytes(0x8000L + i * stride * 4, i + 1, 4)).toMap)
  }

  test("PTX gather fetches indices then distinct targets without reading gaps") {
    val source  = header + """
      .visible .entry probe(.param .u64 indices, .param .u64 input, .param .u64 output) {
        .reg .u64 %p, %q, %r, %offset; .reg .u32 %i, %v;
        ld.param.u64 %p, [indices]; ld.param.u64 %q, [input]; ld.param.u64 %r, [output];
        mov.u32 %i, %tid.x; mul.wide.u32 %offset, %i, 4;
        add.u64 %p, %p, %offset; add.u64 %r, %r, %offset;
        ld.global.u32 %i, [%p]; mul.wide.u32 %offset, %i, 4; add.u64 %q, %q, %offset;
        ld.global.u32 %v, [%q]; st.global.u32 [%r], %v; ret;
      }"""
    val indices = (0 until 32).map(i => ((i * 13) % 7) * 17)
    val host    = indices.zipWithIndex.flatMap((value, i) => bytes(0x1000L + i * 4, value, 4)).toMap ++
      indices.distinct.flatMap(i => bytes(0x4000L + i * 4, i + 9, 4)).toMap
    val (reads, writes, batches) = run(source, 32, Vec(scalar(0x1000), scalar(0x4000), scalar(0x8000)), host)
    assertEquals(batches, 2)
    assertEquals(reads.map(_.size.raw).sum, 128 + 7 * 4)
    assertEquals(writes, indices.zipWithIndex.flatMap((value, i) => bytes(0x8000L + i * 4, value + 9, 4)).toMap)
  }

  test("PTX linked traversal discovers 4096 irregular nodes incrementally") {
    val source = header + """
      .visible .entry probe(.param .u64 root, .param .u64 output) {
        .reg .u64 %p, %q; .reg .u32 %sum, %v; .reg .pred %done;
        ld.param.u64 %p, [root]; ld.param.u64 %q, [output]; mov.u32 %sum, 0;
      again:
        setp.eq.u64 %done, %p, 0; @%done bra finish;
        ld.global.u32 %v, [%p+8]; add.u32 %sum, %sum, %v;
        ld.global.u64 %p, [%p]; bra again;
      finish:
        st.global.u32 [%q], %sum; ret;
      }"""
    val nodes  = (0 until 4096).map(index => 0x100000L + ((index * 2053) % 4096) * 64L).toList
    val host   = nodes.zipWithIndex.flatMap { (address, i) =>
      bytes(address, nodes.lift(i + 1).getOrElse(0L), 8) ++ bytes(address + 8, i + 1, 4)
    }.toMap
    val (reads, writes, batches) = run(source, 1, Vec(scalar(nodes.head), scalar(0x8000)), host)
    assertEquals(reads.map(_.size.raw).sum, 4096 * 12)
    assertEquals(batches, 4096 * 2)
    assertEquals(writes, bytes(0x8000, 4096L * 4097 / 2, 4))
  }
  test("PTX load crossing registered fragments uses device bytes without fetching stale host bytes") {
    val source                   = header + """
      .visible .entry probe(.param .u64 input, .param .u64 output) {
        .reg .u64 %p, %q, %v;
        ld.param.u64 %p, [input]; ld.param.u64 %q, [output];
        ld.global.u64 %v, [%p]; st.global.u64 [%q], %v; ret;
      }"""
    val (reads, writes, batches) = runImage(
      PTX.encoding,
      Vec.from(source.getBytes(StandardCharsets.UTF_8)),
      1,
      Vec(scalar(0x1000), scalar(0x8000)),
      Map.empty,
      List(0x1000L -> encoded(7, 4), 0x1004L -> encoded(9, 4))
    )
    assertEquals(batches, 0)
    assertEquals(reads.size, 0)
    assertEquals(writes, bytes(0x8000, 7, 4) ++ bytes(0x8004, 9, 4))
  }

  test("PTX device atomics linearize at native storage across lanes") {
    val source                   = header + """
      .visible .entry probe(.param .u64 counter) {
        .reg .u64 %p; .reg .u32 %old;
        ld.param.u64 %p, [counter]; atom.global.add.u32 %old, [%p], 1; ret;
      }"""
    val (reads, writes, batches) = run(source, 32, Vec(scalar(0x1000)), bytes(0x1000, 7, 4))
    assertEquals(batches, 0)
    assertEquals(reads.map(_.size.raw).sum, 0)
    assertEquals(writes, bytes(0x1000, 39, 4))
  }

  for {
    width   <- List[Int in (16, 32, 64)](32, 64)
    changed <- List(false, true)
  } test(s"host atomic $width-bit hints avoid redundant RPCs and retry native changes, changed=$changed") {
    val initial     = if (width.raw == 32) 0xfffffffeL else Long.MinValue + 7L
    val replacement = if (width.raw == 32) 20L else 0x678900000014L
    val size        = width.scalarByteWidth
    val source      = header + s"""
      .visible .entry probe(.param .u64 counter, .param .u64 output) {
        .reg .u64 %p, %q; .reg .u$width %first, %second;
        ld.param.u64 %p, [counter]; ld.param.u64 %q, [output];
        atom.global.add.u$width %first, [%p], 1;
        atom.global.add.u$width %second, [%p], 1;
        st.global.u$width [%q], %first;
        st.global.u$width [%q+$size], %second;
        ret;
      }"""
    val requests       = mutable.ArrayBuffer.empty[HostMemoryAtomic]
    val replaced       = java.util.concurrent.atomic.AtomicBoolean(false)
    val (_, writes, _) = runImage(
      PTX.encoding,
      Vec.from(source.getBytes(StandardCharsets.UTF_8)),
      1,
      Vec(scalar(0x1000), scalar(0x8000)),
      bytes(0x1000, initial, size),
      beforeSync = (sync, native) => {
        sync.atomic.foreach { request =>
          val _ = requests.addOne(request)
        }
        val afterFirst = bytes(0x1000, initial + 1, size).forall((address, byte) => native.get(address).contains(byte))
        if (changed && sync.atomic.nonEmpty && afterFirst && replaced.compareAndSet(false, true))
          bytes(0x1000, replacement, size)
        else Map.empty
      }
    )
    val second = if (changed) replacement else initial + 1
    assertEquals(
      writes,
      bytes(0x1000, second + 1, size) ++ bytes(0x8000, initial, size) ++ bytes(0x8000 + size.raw, second, size)
    )
    assert(requests.size <= (if (changed) 4 else 3), s"redundant native atomic RPCs: $requests")
  }

  for (width <- List[Int in (16, 32, 64)](32, 64))
    test(s"PTX scoped $width-bit atomics publish writes and invalidate earlier cached reads") {
      val source = header + s"""
      .visible .entry probe(.param .u64 input, .param .u64 output) {
        .reg .u64 %p, %q; .reg .u$width %old, %v;
        ld.param.u64 %p, [input]; ld.param.u64 %q, [output];
        ld.global.u$width %v, [%p];
        atom.acq_rel.sys.global.add.u$width %old, [%p], 1;
        ld.global.u$width %v, [%p]; st.global.u$width [%q], %v;
        ret;
      }"""
      val (_, writes, _) = runImage(
        PTX.encoding,
        Vec.from(source.getBytes(StandardCharsets.UTF_8)),
        1,
        Vec(scalar(0x1000), scalar(0x8000)),
        bytes(0x1000, 41, width.scalarByteWidth),
        List(0x1000L -> encoded(0, 8), 0x8000L -> encoded(0, 8)),
        hostBacked = true
      )
      assertEquals(writes, bytes(0x1000, 42, width.scalarByteWidth) ++ bytes(0x8000, 42, width.scalarByteWidth))
    }

  for {
    width   <- List[Int in (16, 32, 64)](32, 64)
    release <- List(false, true)
    rmw     <- List(false, true)
  } test(s"PTX device-backed flag orders host payload, width=$width release=$release rmw=$rmw") {
    val atomic = if (release) {
      if (rmw) s"atom.release.gpu.global.exch.b$width %v, [%flag], 1;"
      else s"st.release.gpu.global.u$width [%flag], 1;"
    } else {
      if (rmw) s"atom.acquire.gpu.global.add.u$width %v, [%flag], 0;"
      else s"ld.acquire.gpu.global.u$width %v, [%flag];"
    }
    val operations = if (release)
      s"st.global.u$width [%payload], 73; $atomic ld.global.u32 %marker, [%gate];"
    else
      s"ld.global.u$width %v, [%payload]; ld.global.u32 %marker, [%gate]; $atomic"
    val source = header + s"""
      .visible .entry probe() {
        .reg .u64 %payload, %flag, %gate, %output; .reg .u$width %v; .reg .u32 %marker;
        mov.u64 %payload, 4096; mov.u64 %flag, 8192; mov.u64 %gate, 12288; mov.u64 %output, 32768;
        $operations
        ld.global.u$width %v, [%payload]; st.global.u$width [%output], %v; ret;
      }"""
    val (_, writes, _) = runImage(
      PTX.encoding,
      Vec.from(source.getBytes(StandardCharsets.UTF_8)),
      1,
      Vec.empty,
      bytes(0x1000, 7, width.scalarByteWidth) ++ bytes(0x3000, 0, 4),
      List(0x2000L -> encoded(1, width.scalarByteWidth)),
      beforeRead = (range, native) =>
        if (range.address.raw == 0x3000) {
          if (release) {
            assertEquals(
              (0 until width.scalarByteWidth.raw).map(i => native(0x1000L + i)),
              encoded(73, width.scalarByteWidth)
            )
            Map.empty
          } else bytes(0x1000, 73, width.scalarByteWidth)
        } else Map.empty
    )
    assertEquals(writes.filter((address, _) => address >= 0x8000), bytes(0x8000, 73, width.scalarByteWidth))
  }

  test("registered host memory fetches exact bytes, without reading poisoned allocation holes") {
    val source             = header + """
      .visible .entry probe(.param .u64 input, .param .u64 output) {
        .reg .u64 %p, %q; .reg .u32 %v;
        ld.param.u64 %p, [input]; ld.param.u64 %q, [output];
        ld.global.u32 %v, [%p+12]; st.global.u32 [%q+4], %v; ret;
      }"""
    val (reads, writes, _) = runImage(
      PTX.encoding,
      Vec.from(source.getBytes(StandardCharsets.UTF_8)),
      1,
      Vec(scalar(0x1000), scalar(0x8000)),
      bytes(0x100c, 73, 4),
      List(0x1000L -> Vec.fill(64)(0.toByte), 0x8000L -> Vec.fill(64)(0.toByte)),
      hostBacked = true
    )
    assertEquals(reads, Vector(HostMemoryRange(GuestAddress(0x100cL), 4)))
    assertEquals(writes, bytes(0x8004, 73, 4))
  }

  private def operand(kind: OperandKind, words: Int*): Operand =
    Operand(kind, Vec.from(words.map(Word(_))).unsafe)
  private def id(value: Int): Operand = operand(OperandKind.IdRef, value)

  // This oracle checks the interpreter's publication/cache boundary, independently of
  // atomics (whose stronger implementation could otherwise hide an ignored fence).
  private def checkFence(encoding: String, image: Vec[Byte], writeBefore: Boolean): Unit = {
    val fences             = mutable.ArrayBuffer.empty[Long]
    val (reads, writes, _) = runImage(
      encoding,
      image,
      1,
      Vec(scalar(0x1000), scalar(0x8000)),
      bytes(0x1000, 7, 4),
      beforeSync = (sync, native) => {
        assertEquals(sync.atomic, None)
        if (writeBefore)
          assertEquals((0 until 4).map(i => native.get(0x8000L + i)), encoded(7, 4).map(Some(_)))
        fences += sync.ticket
        bytes(0x1000, 9, 4)
      }
    )
    assertEquals(fences.size, 1, "fence must publish preceding writes before execution proceeds")
    assertEquals(reads.count(_.address.raw == 0x1000L), 2, "fence must invalidate cached host reads")
    assertEquals(writes, (if (writeBefore) bytes(0x8000, 7, 4) else Map.empty[Long, Byte]) ++ bytes(0x8004, 9, 4))
  }

  for {
    fence       <- List("membar.sys", "fence.sc.sys", "fence.acq_rel.sys", "fence.acquire.sys", "fence.release.sys")
    writeBefore <- List(false, true)
  } test(s"PTX $fence refreshes host data, pending writes=$writeBefore") {
    val source = header + s"""
        .visible .entry probe(.param .u64 input, .param .u64 output) {
          .reg .u64 %p, %q; .reg .u32 %v;
          ld.param.u64 %p, [input]; ld.param.u64 %q, [output];
          ld.global.u32 %v, [%p]; ${if (writeBefore) "st.global.u32 [%q], %v;" else ""}
          $fence;
          ld.global.u32 %v, [%p]; st.global.u32 [%q+4], %v; ret;
        }"""
    checkFence(PTX.encoding, Vec.from(source.getBytes(StandardCharsets.UTF_8)), writeBefore)
  }

  for (writeBefore <- List(false, true))
    test(s"SPIR-V memory barrier refreshes host data, pending writes=$writeBefore") {
      import Instruction.*
      val module = Module(
        Header(SPIRVISA.version, Word(0), Word(40), Word(0)),
        Vec[Instruction](
          Capability(operand(OperandKind.Capability, spv.Capability.Kernel)),
          MemoryModel(
            operand(OperandKind.AddressingModel, spv.AddressingModel.Physical64),
            operand(OperandKind.MemoryModel, spv.MemoryModel.OpenCL)
          ),
          TypeVoid(id(1)),
          TypeInt(id(2), operand(OperandKind.LiteralInteger, 32), operand(OperandKind.LiteralInteger, 0)),
          TypePointer(id(7), operand(OperandKind.StorageClass, spv.StorageClass.CrossWorkgroup), id(2)),
          TypeFunction(id(9), id(1), Vec(id(7), id(7))),
          Constant(id(2), id(10), operand(OperandKind.LiteralContextDependentNumber, 0)), // CrossDevice
          Constant(
            id(2),
            id(11),
            operand(
              OperandKind.LiteralContextDependentNumber,
              spv.MemorySemantics.CrossWorkgroupMemory | spv.MemorySemantics.AcquireRelease
            )
          ),
          Constant(id(2), id(12), operand(OperandKind.LiteralContextDependentNumber, 1)),
          EntryPoint(
            operand(OperandKind.ExecutionModel, spv.ExecutionModel.Kernel),
            id(20),
            operand(OperandKind.LiteralString, 0x626f7270, 0x65),
            Vec.empty
          ),
          Function(id(1), id(20), operand(OperandKind.FunctionControl, spv.FunctionControl.None), id(9)),
          FunctionParameter(id(7), id(21)),
          FunctionParameter(id(7), id(22)),
          Label(id(24)),
          Load(id(2), id(25), id(21), None),
          if (writeBefore) Store(id(22), id(25), None) else CopyObject(id(2), id(28), id(25)),
          MemoryBarrier(id(10), id(11)),
          Load(id(2), id(26), id(21), None),
          PtrAccessChain(id(7), id(27), id(22), id(12), Vec.empty),
          Store(id(27), id(26), None),
          Return(),
          FunctionEnd()
        ),
        Endianness.Little
      )
      checkFence(
        SPIRV(Validation.Environment.OpenCLCompute).encoding,
        SPIRVISA.encode(module).toOption.get,
        writeBefore
      )
    }

  for {
    reload    <- List(false, true)
    relocated <- List(false, true)
  } test(s"SPIR-V synchronized host pointer replacement stays authoritative, reload=$reload relocated=$relocated") {
    import Instruction.*
    val body: Vec[Instruction] = if (reload) Vec(
      Load(id(7), id(26), id(21), None),
      Load(id(2), id(27), id(26), None),
      Store(id(23), id(27), None)
    )
    else Vec.empty
    val module = Module(
      Header(SPIRVISA.version, Word(0), Word(40), Word(0)),
      Vec[Instruction](
        Capability(operand(OperandKind.Capability, spv.Capability.Kernel)),
        MemoryModel(
          operand(OperandKind.AddressingModel, spv.AddressingModel.Physical64),
          operand(OperandKind.MemoryModel, spv.MemoryModel.OpenCL)
        ),
        TypeVoid(id(1)),
        TypeInt(id(2), operand(OperandKind.LiteralInteger, 32), operand(OperandKind.LiteralInteger, 0)),
        TypePointer(id(7), operand(OperandKind.StorageClass, spv.StorageClass.CrossWorkgroup), id(2)),
        TypePointer(id(8), operand(OperandKind.StorageClass, spv.StorageClass.CrossWorkgroup), id(7)),
        TypeFunction(id(9), id(1), Vec(id(8), id(7), id(7))),
        Constant(id(2), id(10), operand(OperandKind.LiteralContextDependentNumber, 0)),
        Constant(id(2), id(11), operand(OperandKind.LiteralContextDependentNumber, 0x208)),
        EntryPoint(
          operand(OperandKind.ExecutionModel, spv.ExecutionModel.Kernel),
          id(20),
          operand(OperandKind.LiteralString, 0x626f7270, 0x65),
          Vec.empty
        ),
        Function(id(1), id(20), operand(OperandKind.FunctionControl, spv.FunctionControl.None), id(9)),
        FunctionParameter(id(8), id(21)),
        FunctionParameter(id(7), id(22)),
        FunctionParameter(id(7), id(23)),
        Label(id(24)),
        Store(id(21), id(22), None),
        MemoryBarrier(id(10), id(11))
      ) ++ body ++ Vec[Instruction](Return(), FunctionEnd()),
      Endianness.Little
    )
    val (_, writes, _) = runImage(
      SPIRV(Validation.Environment.OpenCLCompute).encoding,
      SPIRVISA.encode(module).toOption.get,
      1,
      Vec(
        KernelTask.Argument.Memory(AllocationId(1), 8L),
        KernelTask.Argument.Memory(AllocationId(2), 0L),
        scalar(0x8000)
      ),
      bytes(0x1008, 0x4000, 8) ++ bytes(0x4000, 11, 4) ++ bytes(0x5000, 29, 4),
      List(0x1000L -> Vec.fill(24)(0.toByte), 0x4000L -> encoded(0, 4), 0x5000L -> encoded(0, 4)),
      hostBacked = true,
      beforeSync = (_, native) => {
        assertEquals((0 until 8).map(i => native(0x1008L + i)), encoded(0x4000, 8))
        bytes(0x1008, 0x5000, 8)
      },
      relocations = if (relocated)
        Vec(KernelTask.PointerRelocation(
          KernelTask.RelocationSource.Memory(AllocationId(1)),
          8L,
          AllocationId(2),
          0L
        ))
      else Vec.empty,
      afterCompletion = values =>
        assertEquals(
          values,
          Vec.empty[KernelTask.PointerRelocation],
          "native pointer stores must not be replayed after host synchronization"
        )
    )
    if (reload) assertEquals(writes.filter((address, _) => address >= 0x8000), bytes(0x8000, 29, 4))
  }

  for (registered <- List(false, true))
    test(s"SPIR-V ByVal aggregate preserves native pointer bytes without relocation, registered=$registered") {
      import Instruction.*
      val module = Module(
        Header(SPIRVISA.version, Word(0), Word(40), Word(0)),
        Vec[Instruction](
          Capability(operand(OperandKind.Capability, spv.Capability.Kernel)),
          MemoryModel(
            operand(OperandKind.AddressingModel, spv.AddressingModel.Physical64),
            operand(OperandKind.MemoryModel, spv.MemoryModel.OpenCL)
          ),
          TypeVoid(id(1)),
          TypeInt(id(2), operand(OperandKind.LiteralInteger, 32), operand(OperandKind.LiteralInteger, 0)),
          TypeInt(id(3), operand(OperandKind.LiteralInteger, 64), operand(OperandKind.LiteralInteger, 0)),
          TypePointer(id(7), operand(OperandKind.StorageClass, spv.StorageClass.CrossWorkgroup), id(2)),
          TypeStruct(id(4), Vec(id(3), id(7))),
          TypePointer(id(6), operand(OperandKind.StorageClass, spv.StorageClass.Function), id(4)),
          TypePointer(id(8), operand(OperandKind.StorageClass, spv.StorageClass.Function), id(7)),
          TypeFunction(id(9), id(1), Vec(id(6))),
          Constant(id(2), id(10), operand(OperandKind.LiteralContextDependentNumber, 1)),
          Constant(id(2), id(12), operand(OperandKind.LiteralContextDependentNumber, 97)),
          Decorate(id(21), operand(OperandKind.Decoration, spv.Decoration.FuncParamAttr, 2)), // FuncParamAttr ByVal
          EntryPoint(
            operand(OperandKind.ExecutionModel, spv.ExecutionModel.Kernel),
            id(20),
            operand(OperandKind.LiteralString, 0x626f7270, 0x65),
            Vec.empty
          ),
          Function(id(1), id(20), operand(OperandKind.FunctionControl, spv.FunctionControl.None), id(9)),
          FunctionParameter(id(6), id(21)),
          Label(id(24)),
          AccessChain(id(8), id(25), id(21), Vec(id(10))),
          Load(id(7), id(26), id(25), None),
          Store(id(26), id(12), None),
          Return(),
          FunctionEnd()
        ),
        Endianness.Little
      )
      val (_, writes, _) = runImage(
        SPIRV(Validation.Environment.OpenCLCompute).encoding,
        SPIRVISA.encode(module).toOption.get,
        1,
        Vec(KernelTask.Argument.Scalar(encoded(123, 8) ++ encoded(0x4008, 8))),
        Map.empty,
        fragments = if (registered) List(0x4000L -> Vec.fill(16)(0.toByte)) else Nil,
        hostBacked = registered
      )
      assertEquals(writes, bytes(0x4008, 97, 4))
    }

  for {
    structure <- List(false, true)
    offset    <- List(0L, 8L)
  } test(s"SPIR-V typed external pointer argument reads native bytes, structure=$structure offset=$offset") {
    import Instruction.*
    val argumentType = if (structure) 6 else 8
    val module       = Module(
      Header(SPIRVISA.version, Word(0), Word(40), Word(0)),
      Vec[Instruction](
        Capability(operand(OperandKind.Capability, spv.Capability.Kernel)),
        MemoryModel(
          operand(OperandKind.AddressingModel, spv.AddressingModel.Physical64),
          operand(OperandKind.MemoryModel, spv.MemoryModel.OpenCL)
        ),
        TypeVoid(id(1)),
        TypeInt(id(2), operand(OperandKind.LiteralInteger, 32), operand(OperandKind.LiteralInteger, 0)),
        TypePointer(id(7), operand(OperandKind.StorageClass, spv.StorageClass.CrossWorkgroup), id(2)),
        TypePointer(id(8), operand(OperandKind.StorageClass, spv.StorageClass.CrossWorkgroup), id(7)),
        TypeStruct(id(4), Vec(id(7))),
        TypePointer(id(6), operand(OperandKind.StorageClass, spv.StorageClass.CrossWorkgroup), id(4)),
        TypeFunction(id(9), id(1), Vec(id(argumentType))),
        Constant(id(2), id(10), operand(OperandKind.LiteralContextDependentNumber, 0)),
        Constant(id(2), id(12), operand(OperandKind.LiteralContextDependentNumber, 97)),
        EntryPoint(
          operand(OperandKind.ExecutionModel, spv.ExecutionModel.Kernel),
          id(20),
          operand(OperandKind.LiteralString, 0x626f7270, 0x65),
          Vec.empty
        ),
        Function(id(1), id(20), operand(OperandKind.FunctionControl, spv.FunctionControl.None), id(9)),
        FunctionParameter(id(argumentType), id(21)),
        Label(id(24)),
        if (structure) AccessChain(id(8), id(25), id(21), Vec(id(10))) else CopyObject(id(8), id(25), id(21)),
        Load(id(7), id(26), id(25), None),
        Store(id(26), id(12), None),
        Return(),
        FunctionEnd()
      ),
      Endianness.Little
    )
    val (reads, writes, _) = runImage(
      SPIRV(Validation.Environment.OpenCLCompute).encoding,
      SPIRVISA.encode(module).toOption.get,
      1,
      Vec(KernelTask.Argument.Memory(AllocationId(1), offset.unsafe[Long >= 0])),
      bytes(0x1000 + offset, 0x8000, 8),
      List(0x1000L -> Vec.fill(24)(0.toByte)),
      hostBacked = true
    )
    assertEquals(reads.map(_.address.raw), Vector(0x1000 + offset))
    assertEquals(reads.map(_.size.raw), Vector(8))
    assertEquals(writes, bytes(0x8000, 97, 4))
  }

  for (stride <- List(1, 3)) test(s"SPIR-V suspends active invocations and batches stride $stride") {
    import Instruction.*
    val module = Module(
      Header(SPIRVISA.version, Word(0), Word(48), Word(0)),
      Vec[Instruction](
        Capability(operand(OperandKind.Capability, spv.Capability.Kernel)),
        MemoryModel(
          operand(OperandKind.AddressingModel, spv.AddressingModel.Physical64),
          operand(OperandKind.MemoryModel, spv.MemoryModel.OpenCL)
        ),
        TypeVoid(id(1)),
        TypeInt(id(2), operand(OperandKind.LiteralInteger, 32), operand(OperandKind.LiteralInteger, 0)),
        TypeInt(id(3), operand(OperandKind.LiteralInteger, 64), operand(OperandKind.LiteralInteger, 0)),
        TypeVector(id(5), id(3), operand(OperandKind.LiteralInteger, 3)),
        TypePointer(id(6), operand(OperandKind.StorageClass, spv.StorageClass.Input), id(5)),
        TypePointer(id(7), operand(OperandKind.StorageClass, spv.StorageClass.CrossWorkgroup), id(2)),
        TypeFunction(id(9), id(1), Vec(id(7), id(7))),
        Constant(id(3), id(10), operand(OperandKind.LiteralContextDependentNumber, stride, 0)),
        Variable(id(6), id(15), operand(OperandKind.StorageClass, spv.StorageClass.Input), None),
        Decorate(id(15), operand(OperandKind.Decoration, spv.Decoration.BuiltIn, spv.BuiltIn.GlobalInvocationId)),
        EntryPoint(
          operand(OperandKind.ExecutionModel, spv.ExecutionModel.Kernel),
          id(20),
          operand(OperandKind.LiteralString, 0x626f7270, 0x65),
          Vec(id(15))
        ),
        Function(id(1), id(20), operand(OperandKind.FunctionControl, spv.FunctionControl.None), id(9)),
        FunctionParameter(id(7), id(21)),
        FunctionParameter(id(7), id(22)),
        Label(id(24)),
        Load(id(5), id(25), id(15), None),
        CompositeExtract(id(3), id(28), id(25), Vec(operand(OperandKind.LiteralInteger, 0))),
        IMul(id(3), id(29), id(28), id(10)),
        PtrAccessChain(id(7), id(30), id(21), id(29), Vec.empty),
        PtrAccessChain(id(7), id(31), id(22), id(29), Vec.empty),
        Load(id(2), id(32), id(30), None),
        Store(id(31), id(32), None),
        Return(),
        FunctionEnd()
      ),
      Endianness.Little
    )
    val host                     = (0 until 32).flatMap(i => bytes(0x1000L + i * stride * 4, i + 17, 4)).toMap
    val encoding                 = SPIRV(Validation.Environment.OpenCLCompute).encoding
    val (reads, writes, batches) =
      runImage(encoding, SPIRVISA.encode(module).toOption.get, 32, Vec(scalar(0x1000), scalar(0x8000)), host)
    assertEquals(batches, 1)
    assertEquals(reads.size, if (stride == 1) 1 else 32)
    assertEquals(reads.map(_.size.raw).sum, 128)
    assertEquals(writes, (0 until 32).flatMap(i => bytes(0x8000L + i * stride * 4, i + 17, 4)).toMap)
  }

}
