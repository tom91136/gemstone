package net.kurobako.gemstone.server.backend

import java.nio.charset.StandardCharsets
import net.kurobako.gemstone.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.binary.Widths.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.constraint.auto.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.rpc.*
import net.kurobako.gemstone.rpc.ExecutionRPC.*
import net.kurobako.gemstone.testing.either.*
import net.kurobako.gemstone.vm.*
import scala.collection.immutable.LongMap
import scala.collection.mutable
import scala.util.Using

class HostMemoryExecutionSuite extends munit.FunSuite {
  private val Booleans       = List(false, true)
  private val PointerOffsets = List(0L, 8L)

  private def encoded(value: Long, size: Int in 0 ~ 8): Vec[Byte] =
    LittleEndian.toVec(value, size)
  private def scalar(value: Long, size: Int in 0 ~ 8 = 8): KernelTask.Argument =
    KernelTask.Argument.Scalar(encoded(value, size).unsafe)
  private def bytes(address: Long, value: Long, size: Int in 0 ~ 8): Map[Long, Byte] =
    encoded(value, size).zipWithIndex.map((byte, index) => (address + index.toLong) -> byte).toMap

  private object Async extends scala.concurrent.ExecutionContext {
    override def execute(runnable: Runnable): Unit = {
      val thread = Thread(runnable); thread.setDaemon(true); thread.start()
    }
    override def reportFailure(error: Throwable): Unit = throw error
  }

  private def run(source: String, lanes: Int, arguments: Vec[KernelTask.Argument], host: Map[Long, Byte]) =
    runImage(ProgramEncoding.PTXText, Vec.from(source.getBytes(StandardCharsets.UTF_8)), lanes, arguments, host)

  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private def runImage(
      encoding: ProgramEncoding,
      image: Vec[Byte],
      lanes: Int,
      arguments: Vec[KernelTask.Argument],
      host: Map[Long, Byte],
      fragments: List[(Long, Vec[Byte])] = Nil,
      hostBacked: Boolean = false,
      beforeSync: (HostMemorySync, LongMap[Byte]) => Map[Long, Byte] = (_, _) => Map.empty,
      beforeRead: (HostMemoryRange, mutable.LongMap[Byte]) => Map[Long, Byte] = (_, _) => Map.empty,
      relocations: Vec[KernelTask.PointerRelocation] = Vec.empty,
      afterCompletion: Vec[KernelTask.PointerRelocation] => Unit = _ => (),
      afterExecution: (ExecutionSession, Map[Long, AllocationId]) => Unit = (_, _) => ()
  ) =
    Using.resource(ExecutionSession(Backends.default().programs, executionContext = Async)) { service =>
      assertEquals(service.enableHostMemory(), Right(()))
      val allocations = mutable.LongMap.empty[AllocationId]
      fragments.foreach { (address, contents) =>
        val allocation =
          service.registerAllocation(contents.size.unsafe[Int > 0]).toOption.get
        assertEquals(service.registerAllocationAddress(allocation, GuestAddress(address), hostBacked), Right(()))
        assertEquals(service.write(allocation, 0L, contents), Right(()))
        allocations.update(address, allocation)
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
      val writes     = mutable.LongMap.empty[Byte]
      val native     = mutable.LongMap.from(host)
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
          native ++= beforeSync(sync, LongMap.from(native))
          val observed = sync.atomic.fold(0L) { request =>
            val size     = request.size
            val observed = (0 until size).foldLeft(0L)((value, index) =>
              value | ((native(request.address.raw + index).toLong & 255L) << (index * 8))
            )
            if (request.operation == HostMemoryAtomicOperation.Load) observed
            else if (request.operation == HostMemoryAtomicOperation.Store || observed == request.expected) {
              val updated = bytes(request.address.raw, request.desired, size)
              native ++= updated
              writes ++= updated
              observed
            } else {
              observed
            }
          }
          sync.atomicStores.foreach { request =>
            val size    = request.size
            val updated = bytes(request.address.raw, request.desired, size)
            native ++= updated
            writes ++= updated
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
              Vec.from((0 until range.size).map { index =>
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
      afterExecution(service, allocations.toMap)
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
      ProgramEncoding.PTXText,
      Vec.from(source.getBytes(StandardCharsets.UTF_8)),
      1,
      Vec(scalar(0x1000), scalar(0x8000)),
      Map.empty,
      List(0x1000L -> encoded(7, 4), 0x1004L -> encoded(9, 4))
    )
    assertEquals(batches, 0)
    assertEquals(reads.size, 0)
    assertEquals(writes, bytes(address = 0x8000, value = 7, 4) ++ bytes(address = 0x8004, value = 9, 4))
  }

  test("relaxed system-scope access to device-backed allocations stays on device memory") {
    val source                   = header + """
      .visible .entry probe() {
        .reg .u64 %input, %output; .reg .u32 %value;
        mov.u64 %input, 4096; mov.u64 %output, 8192;
        ld.relaxed.sys.global.u32 %value, [%input];
        st.relaxed.sys.global.u32 [%output], %value;
        ret;
      }"""
    val syncs                    = mutable.ArrayBuffer.empty[HostMemorySync]
    val (reads, writes, batches) = runImage(
      ProgramEncoding.PTXText,
      Vec.from(source.getBytes(StandardCharsets.UTF_8)),
      1,
      Vec.empty,
      bytes(0x1000, 999, 4) ++ bytes(0x2000, 0, 4),
      List(0x1000L -> encoded(73, 4), 0x2000L -> encoded(0, 4)),
      beforeSync = (sync, _) => { syncs += sync; Map.empty },
      afterExecution = (service, allocations) =>
        assertEquals(service.read(allocations(0x2000L), 0L, 4).map(_.toList), Right(encoded(73, 4).toList))
    )
    assertEquals(reads, Vector.empty)
    assertEquals(writes, Map.empty)
    assertEquals(syncs.toVector, Vector.empty)
    assertEquals(batches, 0)
  }

  test("PTX device atomics linearise at native storage across lanes") {
    val source                   = header + """
      .visible .entry probe(.param .u64 counter) {
        .reg .u64 %p; .reg .u32 %old;
        ld.param.u64 %p, [counter]; atom.global.add.u32 %old, [%p], 1; ret;
      }"""
    val (reads, writes, batches) = run(source, 32, Vec(scalar(0x1000)), bytes(0x1000, 7, 4))
    assertEquals(batches, 0)
    assertEquals(reads.map(_.size.raw).sum, 0)
    assertEquals(writes, bytes(address = 0x1000, value = 39, 4))
  }

  for {
    width   <- List[Int in (16, 32, 64)](32, 64)
    changed <- Booleans
  } test(s"host atomic $width-bit hints avoid redundant RPCs and retry native changes, changed=$changed") {
    val initial     = if (width == 32) 0xfffffffeL else Long.MinValue + 7L
    val replacement = if (width == 32) 20L else 0x678900000014L
    val size        = width.toFloatByteWidth
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
      ProgramEncoding.PTXText,
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
      bytes(0x1000, second + 1, size) ++ bytes(0x8000, initial, size) ++ bytes(0x8000 + size, second, size)
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
        ProgramEncoding.PTXText,
        Vec.from(source.getBytes(StandardCharsets.UTF_8)),
        1,
        Vec(scalar(0x1000), scalar(0x8000)),
        bytes(0x1000, 41, width.toFloatByteWidth),
        List(0x1000L -> encoded(0, 8), 0x8000L -> encoded(0, 8)),
        hostBacked = true
      )
      assertEquals(
        writes,
        bytes(address = 0x1000, value = 42, width.toFloatByteWidth) ++
          bytes(address = 0x8000, value = 42, width.toFloatByteWidth)
      )
    }

  for {
    width   <- List[Int in (16, 32, 64)](32, 64)
    release <- Booleans
    rmw     <- Booleans
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
      ProgramEncoding.PTXText,
      Vec.from(source.getBytes(StandardCharsets.UTF_8)),
      1,
      Vec.empty,
      bytes(0x1000, 7, width.toFloatByteWidth) ++ bytes(0x3000, 0, 4),
      List(0x2000L -> encoded(1, width.toFloatByteWidth)),
      beforeRead = (range, native) =>
        if (range.address.raw == 0x3000) {
          if (release) {
            assertEquals(
              (0 until width.toFloatByteWidth).map(i => native(0x1000L + i)),
              encoded(73, width.toFloatByteWidth)
            )
            Map.empty
          } else bytes(address = 0x1000, value = 73, width.toFloatByteWidth)
        } else Map.empty
    )
    assertEquals(
      writes.filter((address, _) => address >= 0x8000),
      bytes(address = 0x8000, value = 73, width.toFloatByteWidth)
    )
  }

  test("registered host memory fetches exact bytes, without reading poisoned allocation holes") {
    val source             = header + """
      .visible .entry probe(.param .u64 input, .param .u64 output) {
        .reg .u64 %p, %q; .reg .u32 %v;
        ld.param.u64 %p, [input]; ld.param.u64 %q, [output];
        ld.global.u32 %v, [%p+12]; st.global.u32 [%q+4], %v; ret;
      }"""
    val (reads, writes, _) = runImage(
      ProgramEncoding.PTXText,
      Vec.from(source.getBytes(StandardCharsets.UTF_8)),
      1,
      Vec(scalar(0x1000), scalar(0x8000)),
      bytes(0x100c, 73, 4),
      List(0x1000L -> Vec.fill(64)(0.toByte), 0x8000L -> Vec.fill(64)(0.toByte)),
      hostBacked = true
    )
    assertEquals(reads, Vector(HostMemoryRange(GuestAddress(0x100cL), 4)))
    assertEquals(writes, bytes(address = 0x8004, value = 73, 4))
  }

  test("relaxed.sys store publishes its target without flushing unrelated host writes") {
    val source         = header + """
      .visible .entry probe() {
        .reg .u64 %ordinary, %system; .reg .u32 %value;
        mov.u64 %ordinary, 4096; mov.u64 %system, 8192; mov.u32 %value, 17;
        st.global.u32 [%ordinary], %value;
        mov.u32 %value, 29; st.relaxed.sys.global.u32 [%system], %value; ret;
      }"""
    val syncs          = mutable.ArrayBuffer.empty[HostMemorySync]
    val (_, writes, _) = runImage(
      ProgramEncoding.PTXText,
      Vec.from(source.getBytes(StandardCharsets.UTF_8)),
      1,
      Vec.empty,
      bytes(0x1000, 0, 4) ++ bytes(0x2000, 0, 4),
      beforeSync = (sync, native) => {
        syncs += sync
        assertEquals(sync.atomic, None)
        assertEquals(sync.relaxed, true)
        assertEquals(sync.publish, Vec(HostMemoryRange(GuestAddress(0x2000L), 4)))
        assertEquals((0 until 4).map(index => native(0x1000L + index)), encoded(0, 4))
        assertEquals(sync.atomicStores.map(_.operation), Vec.fill(1)(HostMemoryAtomicOperation.Store))
        assertEquals(sync.atomicStores.map(_.desired), Vec(29L))
        Map.empty
      }
    )
    assertEquals(syncs.size, 1)
    assertEquals(writes, bytes(address = 0x1000, value = 17, 4) ++ bytes(address = 0x2000, value = 29, 4))
  }

  test("relaxed.sys load refreshes only its requested range after a cached weak load") {
    val source         = header + """
      .visible .entry probe() {
        .reg .u64 %input, %output; .reg .u32 %weak, %system;
        mov.u64 %input, 4096; mov.u64 %output, 32768;
        ld.global.u32 %weak, [%input];
        ld.relaxed.sys.global.u32 %system, [%input];
        st.global.u32 [%output], %weak; st.global.u32 [%output+4], %system; ret;
      }"""
    val reads          = mutable.ArrayBuffer.empty[HostMemoryRange]
    val syncs          = mutable.ArrayBuffer.empty[HostMemorySync]
    val (_, writes, _) = runImage(
      ProgramEncoding.PTXText,
      Vec.from(source.getBytes(StandardCharsets.UTF_8)),
      1,
      Vec.empty,
      bytes(0x1000, 1, 4) ++ bytes(0x8000, 0, 8),
      beforeRead = (range, _) =>
        if (range.address.raw == 0x1000L) {
          reads += range
          bytes(address = 0x1000, value = 11, 4)
        } else Map.empty,
      beforeSync = (sync, _) => {
        syncs += sync
        assertEquals(sync.atomic.map(_.operation), Some(HostMemoryAtomicOperation.Load))
        assertEquals(sync.relaxed, true)
        bytes(address = 0x1000, value = 23, 4)
      }
    )
    assertEquals(reads.toVector, Vector(HostMemoryRange(GuestAddress(0x1000L), 4)))
    assertEquals(syncs.size, 1)
    assertEquals(writes, bytes(address = 0x8000, value = 11, 4) ++ bytes(address = 0x8004, value = 23, 4))
  }

  test("generic relaxed.sys accesses classify host-backed addresses and publish the exact store") {
    val source         = header + """
      .visible .entry probe(.param .u64 input, .param .u64 output) {
        .reg .u64 %p, %q; .reg .u32 %v;
        ld.param.u64 %p, [input]; ld.param.u64 %q, [output];
        ld.relaxed.sys.u32 %v, [%p]; st.relaxed.sys.u32 [%q], %v; ret;
      }"""
    val reads          = mutable.ArrayBuffer.empty[HostMemoryRange]
    val (_, writes, _) = runImage(
      ProgramEncoding.PTXText,
      Vec.from(source.getBytes(StandardCharsets.UTF_8)),
      1,
      Vec(scalar(0x100001000L), scalar(0x100002000L)),
      bytes(0x100001000L, 41, 4) ++ bytes(0x100002000L, 0, 4),
      beforeRead = (range, _) => { reads += range; Map.empty },
      beforeSync = (sync, _) => {
        assertEquals(sync.relaxed, true)
        sync.atomic match {
          case Some(request) if request.operation == HostMemoryAtomicOperation.Load =>
            assertEquals(sync.publish, Vec(HostMemoryRange(GuestAddress(0x100001000L), 4)))
          case None =>
            assertEquals(sync.publish, Vec(HostMemoryRange(GuestAddress(0x100002000L), 4)))
            assertEquals(sync.atomicStores.map(_.desired), Vec(41L))
          case other => fail(s"unexpected relaxed.sys sync: $other")
        }
        Map.empty
      }
    )
    assertEquals(reads.toVector, Vector.empty)
    assertEquals(writes, bytes(address = 0x100002000L, value = 41, 4))
  }

  test("relaxed.sys host scalars use native atomic operations at 8- and 16-bit widths") {
    val source             = header + """
      .visible .entry probe(.param .u64 input, .param .u64 output) {
        .reg .u64 %p, %q; .reg .u32 %a, %b;
        ld.param.u64 %p, [input]; ld.param.u64 %q, [output];
        ld.relaxed.sys.u8 %a, [%p]; ld.relaxed.sys.u16 %b, [%p+2];
        st.relaxed.sys.u8 [%q], %a; st.relaxed.sys.u16 [%q+2], %b; ret;
      }"""
    val syncs              = mutable.ArrayBuffer.empty[HostMemorySync]
    val (reads, writes, _) = runImage(
      ProgramEncoding.PTXText,
      Vec.from(source.getBytes(StandardCharsets.UTF_8)),
      1,
      Vec(scalar(0x100001000L), scalar(0x100002000L)),
      bytes(0x100001000L, 0x66554433L, 4) ++ bytes(0x100002000L, 0, 4),
      beforeSync = (sync, _) => { syncs += sync; Map.empty }
    )
    assertEquals(reads, Vector.empty)
    assertEquals(syncs.flatMap(_.atomic.map(_.size)).toVector, Vector[Int in (1, 2, 4, 8)](1, 2))
    assertEquals(syncs.flatMap(_.atomicStores.map(_.size)).toVector, Vector[Int in (1, 2, 4, 8)](1, 2))
    assert(syncs.forall(_.relaxed))
    assertEquals(
      writes,
      bytes(address = 0x100002000L, value = 0x33L, 1) ++ bytes(address = 0x100002002L, value = 0x6655L, 2)
    )
  }

  test("relaxed.sys host load at a misaligned address faults as misaligned") {
    val source = header + """
      .visible .entry probe(.param .u64 input) {
        .reg .u64 %p; .reg .u32 %value;
        ld.param.u64 %p, [input];
        ld.relaxed.sys.global.u32 %value, [%p];
        ret;
      }"""
    Using.resource(ExecutionSession(Backends.default().programs, executionContext = Async)) { service =>
      assertEquals(service.enableHostMemory(), Right(()))
      val program =
        service.load(ProgramEncoding.PTXText, Vec.from(source.getBytes(StandardCharsets.UTF_8))).toOption.get
      val task = KernelTask.validated(
        "probe",
        LaunchGeometry.Dimensions.One,
        LaunchGeometry.Dimensions.One,
        Vec(scalar(0x1001L))
      ).toOption.get
      val completion = service.launch(program, task).toOption.get
      service.pollHostMemory(completion) match {
        case Left(ExecutionFailure.MisalignedAddress(_)) => ()
        case other => fail(s"misaligned relaxed.sys load did not fault as misaligned: $other")
      }
    }
  }

  test("generic relaxed.gpu accesses retain shared-memory routing") {
    val source         = header + """
      .shared .align 4 .b8 scratch[4];
      .visible .entry probe(.param .u64 output) {
        .reg .u64 %q, %shared; .reg .u32 %value, %result;
        ld.param.u64 %q, [output]; cvta.shared.u64 %shared, scratch;
        mov.u32 %value, 73;
        st.relaxed.gpu.u32 [%shared], %value;
        ld.relaxed.gpu.u32 %result, [%shared];
        st.global.u32 [%q], %result; ret;
      }"""
    val (_, writes, _) = runImage(
      ProgramEncoding.PTXText,
      Vec.from(source.getBytes(StandardCharsets.UTF_8)),
      1,
      Vec(scalar(0x8000)),
      bytes(0x8000, 0, 4)
    )
    assertEquals(writes, bytes(address = 0x8000, value = 73, 4))
  }

  // This oracle checks the interpreter's publication/cache boundary, independently of
  // atomics (whose stronger implementation could otherwise hide an ignored fence).
  private def checkFence(encoding: ProgramEncoding, image: Vec[Byte], writeBefore: Boolean): Unit = {
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
        bytes(address = 0x1000, value = 9, 4)
      }
    )
    assertEquals(fences.size, 1, "fence must publish preceding writes before execution proceeds")
    assertEquals(reads.count(_.address.raw == 0x1000L), 2, "fence must invalidate cached host reads")
    assertEquals(
      writes,
      (if (writeBefore) bytes(address = 0x8000, value = 7, 4) else Map.empty[Long, Byte]) ++
        bytes(address = 0x8004, value = 9, 4)
    )
  }

  for {
    fence       <- List("membar.sys", "fence.sc.sys", "fence.acq_rel.sys", "fence.acquire.sys", "fence.release.sys")
    writeBefore <- Booleans
  } test(s"PTX $fence refreshes host data, pending writes=$writeBefore") {
    // fence.acquire and fence.release need PTX ISA 8.6 and sm_90
    val target = if (fence.startsWith("fence.acquire") || fence.startsWith("fence.release"))
      ".version 8.6\n.target sm_90\n.address_size 64\n"
    else header
    val source = target + s"""
        .visible .entry probe(.param .u64 input, .param .u64 output) {
          .reg .u64 %p, %q; .reg .u32 %v;
          ld.param.u64 %p, [input]; ld.param.u64 %q, [output];
          ld.global.u32 %v, [%p]; ${if (writeBefore) "st.global.u32 [%q], %v;" else ""}
          $fence;
          ld.global.u32 %v, [%p]; st.global.u32 [%q+4], %v; ret;
        }"""
    checkFence(ProgramEncoding.PTXText, Vec.from(source.getBytes(StandardCharsets.UTF_8)), writeBefore)
  }

  for (writeBefore <- Booleans)
    test(s"SPIR-V memory barrier refreshes host data, pending writes=$writeBefore") {
      val image = spirvAssembly.image(s"""
        ${spirvAssembly.openCL}
        OpEntryPoint Kernel %main "probe"
        %void = OpTypeVoid
        %uint = OpTypeInt 32 0
        %uint_ptr = OpTypePointer CrossWorkgroup %uint
        %fn = OpTypeFunction %void %uint_ptr %uint_ptr
        %cross_device = OpConstant %uint 0
        %acquire_release_cross_workgroup = OpConstant %uint 0x208
        %one = OpConstant %uint 1
        %main = OpFunction %void None %fn
        %input = OpFunctionParameter %uint_ptr
        %output = OpFunctionParameter %uint_ptr
        %entry = OpLabel
        %before = OpLoad %uint %input
        ${if (writeBefore) "OpStore %output %before" else ""}
        OpMemoryBarrier %cross_device %acquire_release_cross_workgroup
        %after = OpLoad %uint %input
        %second = OpPtrAccessChain %uint_ptr %output %one
        OpStore %second %after
        OpReturn
        OpFunctionEnd
      """)
      checkFence(ProgramEncoding.SPIRVOpenCLCompute, image, writeBefore)
    }

  for {
    reload    <- Booleans
    relocated <- Booleans
  } test(s"SPIR-V synchronised host pointer replacement stays authoritative, reload=$reload relocated=$relocated") {
    val reloading = "%reloaded = OpLoad %uint_ptr %slot\n%value = OpLoad %uint %reloaded\nOpStore %output %value"
    val image     = spirvAssembly.image(s"""
      ${spirvAssembly.openCL}
      OpEntryPoint Kernel %main "probe"
      %void = OpTypeVoid
      %uint = OpTypeInt 32 0
      %uint_ptr = OpTypePointer CrossWorkgroup %uint
      %uint_ptr_ptr = OpTypePointer CrossWorkgroup %uint_ptr
      %fn = OpTypeFunction %void %uint_ptr_ptr %uint_ptr %uint_ptr
      %cross_device = OpConstant %uint 0
      %acquire_release_cross_workgroup = OpConstant %uint 0x208
      %main = OpFunction %void None %fn
      %slot = OpFunctionParameter %uint_ptr_ptr
      %replacement = OpFunctionParameter %uint_ptr
      %output = OpFunctionParameter %uint_ptr
      %entry = OpLabel
      OpStore %slot %replacement
      OpMemoryBarrier %cross_device %acquire_release_cross_workgroup
      ${if (reload) reloading else ""}
      OpReturn
      OpFunctionEnd
    """)
    val (_, writes, _) = runImage(
      ProgramEncoding.SPIRVOpenCLCompute,
      image,
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
        bytes(address = 0x1008, value = 0x5000, 8)
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
          "native pointer stores must not be replayed after host synchronisation"
        )
    )
    if (reload) assertEquals(writes.filter((address, _) => address >= 0x8000), bytes(address = 0x8000, value = 29, 4))
  }

  for (registered <- Booleans)
    test(s"SPIR-V ByVal aggregate preserves native pointer bytes without relocation, registered=$registered") {
      val image = spirvAssembly.image(s"""
        ${spirvAssembly.openCL}
        OpEntryPoint Kernel %main "probe"
        OpDecorate %aggregate FuncParamAttr ByVal
        %void = OpTypeVoid
        %uint = OpTypeInt 32 0
        %ulong = OpTypeInt 64 0
        %uint_ptr = OpTypePointer CrossWorkgroup %uint
        %pair = OpTypeStruct %ulong %uint_ptr
        %pair_ptr = OpTypePointer Function %pair
        %field_ptr = OpTypePointer Function %uint_ptr
        %fn = OpTypeFunction %void %pair_ptr
        %one = OpConstant %uint 1
        %value = OpConstant %uint 97
        %main = OpFunction %void None %fn
        %aggregate = OpFunctionParameter %pair_ptr
        %entry = OpLabel
        %field = OpAccessChain %field_ptr %aggregate %one
        %target = OpLoad %uint_ptr %field
        OpStore %target %value
        OpReturn
        OpFunctionEnd
      """)
      val (_, writes, _) = runImage(
        ProgramEncoding.SPIRVOpenCLCompute,
        image,
        1,
        Vec(KernelTask.Argument.Scalar((encoded(123, 8) ++ encoded(0x4008, 8)).refine[NonEmpty[Vec[Byte]]].value)),
        Map.empty,
        fragments = if (registered) List(0x4000L -> Vec.fill(16)(0.toByte)) else Nil,
        hostBacked = registered
      )
      assertEquals(writes, bytes(address = 0x4008, value = 97, 4))
    }

  for {
    structure <- Booleans
    offset    <- PointerOffsets
  } test(s"SPIR-V typed external pointer argument reads native bytes, structure=$structure offset=$offset") {
    val argument = if (structure) "%holder_ptr" else "%uint_ptr_ptr"
    val slot     =
      if (structure) "OpAccessChain %uint_ptr_ptr %argument %zero" else "OpCopyObject %uint_ptr_ptr %argument"
    val image = spirvAssembly.image(s"""
      ${spirvAssembly.openCL}
      OpEntryPoint Kernel %main "probe"
      %void = OpTypeVoid
      %uint = OpTypeInt 32 0
      %uint_ptr = OpTypePointer CrossWorkgroup %uint
      %uint_ptr_ptr = OpTypePointer CrossWorkgroup %uint_ptr
      %holder = OpTypeStruct %uint_ptr
      %holder_ptr = OpTypePointer CrossWorkgroup %holder
      %fn = OpTypeFunction %void $argument
      %zero = OpConstant %uint 0
      %value = OpConstant %uint 97
      %main = OpFunction %void None %fn
      %argument = OpFunctionParameter $argument
      %entry = OpLabel
      %slot = $slot
      %target = OpLoad %uint_ptr %slot
      OpStore %target %value
      OpReturn
      OpFunctionEnd
    """)
    val (reads, writes, _) = runImage(
      ProgramEncoding.SPIRVOpenCLCompute,
      image,
      1,
      Vec(KernelTask.Argument.Memory(AllocationId(1), offset.refine[Long >= 0].value)),
      bytes(0x1000 + offset, 0x8000, 8),
      List(0x1000L -> Vec.fill(24)(0.toByte)),
      hostBacked = true
    )
    assertEquals(reads.map(_.address.raw), Vector(0x1000 + offset))
    assertEquals(reads.map(_.size.raw), Vector(8))
    assertEquals(writes, bytes(address = 0x8000, value = 97, 4))
  }

  for (stride <- List(1, 3)) test(s"SPIR-V suspends active invocations and batches stride $stride") {
    val image = spirvAssembly.image(s"""
      ${spirvAssembly.openCL}
      OpEntryPoint Kernel %main "probe" %global_id
      OpDecorate %global_id BuiltIn GlobalInvocationId
      %void = OpTypeVoid
      %uint = OpTypeInt 32 0
      %ulong = OpTypeInt 64 0
      %ulong3 = OpTypeVector %ulong 3
      %ulong3_ptr = OpTypePointer Input %ulong3
      %uint_ptr = OpTypePointer CrossWorkgroup %uint
      %fn = OpTypeFunction %void %uint_ptr %uint_ptr
      %stride = OpConstant %ulong $stride
      %global_id = OpVariable %ulong3_ptr Input
      %main = OpFunction %void None %fn
      %input = OpFunctionParameter %uint_ptr
      %output = OpFunctionParameter %uint_ptr
      %entry = OpLabel
      %id = OpLoad %ulong3 %global_id
      %x = OpCompositeExtract %ulong %id 0
      %index = OpIMul %ulong %x %stride
      %source = OpPtrAccessChain %uint_ptr %input %index
      %target = OpPtrAccessChain %uint_ptr %output %index
      %value = OpLoad %uint %source
      OpStore %target %value
      OpReturn
      OpFunctionEnd
    """)
    val host                     = (0 until 32).flatMap(i => bytes(0x1000L + i * stride * 4, i + 17, 4)).toMap
    val (reads, writes, batches) =
      runImage(ProgramEncoding.SPIRVOpenCLCompute, image, 32, Vec(scalar(0x1000), scalar(0x8000)), host)
    assertEquals(batches, 1)
    assertEquals(reads.size, if (stride == 1) 1 else 32)
    assertEquals(reads.map(_.size.raw).sum, 128)
    assertEquals(writes, (0 until 32).flatMap(i => bytes(0x8000L + i * stride * 4, i + 17, 4)).toMap)
  }
}
