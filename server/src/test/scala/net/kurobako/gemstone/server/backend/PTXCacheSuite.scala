package net.kurobako.gemstone.server.backend

import java.nio.charset.StandardCharsets
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.FutureTask
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.ptx.v9_3.ISA as PTXISA
import net.kurobako.gemstone.isa.ptx.v9_3.Program as PTXProgram
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.rpc.*
import net.kurobako.gemstone.rpc.ExecutionProtocol.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.vm.*
import scala.util.Using

class PTXCacheSuite extends munit.FunSuite {
  private val source =
    """|.version 8.0
       |.target sm_80
       |.address_size 64
       |.global .align 4 .u32 shared_value;
       |.visible .entry read_global(.param .u64 output) {
       |  .reg .b64 %rd;
       |  .reg .b32 %r;
       |  ld.param.u64 %rd, [output];
       |  ld.global.u32 %r, [shared_value];
       |  st.global.u32 [%rd], %r;
       |  ret;
       |}
       |""".stripMargin
  private val image = Vec.from(source.getBytes(StandardCharsets.UTF_8))

  private class DecodingPTX(decoder: Vec[Byte] => Either[PTXISA.DecodeError, PTXProgram]) extends PTX() {
    override protected def decode(bytes: Vec[Byte]): Either[PTXISA.DecodeError, PTXProgram] = decoder(bytes)
  }

  test("images with colliding CRC32 hashes retain independent cache entries and lifetimes") {
    val firstImage  = Vec.from(java.util.HexFormat.of().parseHex("58fbb7a9b2680392"))
    val secondImage = Vec.from(java.util.HexFormat.of().parseHex("69176c4aa471a321"))
    val calls       = AtomicInteger()
    val backend     = DecodingPTX { bytes =>
      val _      = calls.incrementAndGet()
      val kernel = if (bytes == firstImage) "first" else "second"
      PTXISA.decode(Vec.from(s".version 8.0\n.target sm_80\n.address_size 64\n.entry $kernel() { ret; }"
        .getBytes(StandardCharsets.UTF_8)))
    }
    Using.Manager { use =>
      val first  = use(backend.load(firstImage).toOption.get)
      val second = use(backend.load(secondImage).toOption.get)
      assertEquals(first.symbols.toOption.get.kernels.map(_.name.raw), Vec("first"))
      assertEquals(second.symbols.toOption.get.kernels.map(_.name.raw), Vec("second"))
      first.close()
      val retained = use(backend.load(Vec.from(Vec.toArray(secondImage))).toOption.get)
      assertEquals(retained.symbols, second.symbols)
      assertEquals(calls.get(), 2)
    }.get
    backend.load(firstImage).toOption.get.close()
    assertEquals(calls.get(), 3)
  }

  test("a blocked PTX parse does not block a different image or an existing program's release") {
    val entered   = CountDownLatch(1)
    val release   = CountDownLatch(1)
    val slowImage = Vec.from(s"$source\n// slow".getBytes(StandardCharsets.UTF_8))
    val backend   = DecodingPTX { bytes =>
      if (bytes == slowImage) {
        entered.countDown()
        if (!release.await(10, TimeUnit.SECONDS)) throw IllegalStateException("parse was not released")
      }
      PTXISA.decode(bytes)
    }
    Using.resource(threads.Pool(2)) { pool =>
      val existing = backend.load(image).toOption.get
      val slow     = pool.executor.submit(() => backend.load(slowImage))
      try {
        assert(entered.await(5, TimeUnit.SECONDS))
        val independent = pool.executor.submit { () =>
          existing.close()
          backend.load(image).map { program =>
            program.close()
            ()
          }
        }
        assertEquals(independent.get(2, TimeUnit.SECONDS), Right(()))
      } finally {
        release.countDown()
        existing.close()
      }
      slow.get(5, TimeUnit.SECONDS).toOption.get.close()
    }
  }

  test("equal-content concurrent loads share parsing until the last reference closes") {
    val calls   = AtomicInteger()
    val entered = CountDownLatch(1)
    val release = CountDownLatch(1)
    val backend = DecodingPTX { bytes =>
      val _ = calls.incrementAndGet()
      entered.countDown()
      if (!release.await(5, TimeUnit.SECONDS)) throw IllegalStateException("parse was not released")
      PTXISA.decode(bytes)
    }
    Using.resource(threads.Pool(8)) { pool =>
      val loads = Vector.fill(8)(pool.executor.submit(() =>
        backend.load(Vec.from(source.getBytes(StandardCharsets.UTF_8)))
      ))
      try assert(entered.await(5, TimeUnit.SECONDS))
      finally release.countDown()
      val programs = loads.map(_.get(5, TimeUnit.SECONDS).toOption.get)
      assertEquals(calls.get(), 1)
      programs.dropRight(1).foreach(_.close())
      val retained = backend.load(image).toOption.get
      assertEquals(calls.get(), 1)
      programs.last.close()
      retained.close()
      retained.close()
      backend.load(image).toOption.get.close()
      assertEquals(calls.get(), 2)
    }
  }

  test("decode errors and thrown failures release the cache reservation and allow retry") {
    val calls       = AtomicInteger()
    val interrupted = InterruptedException("decode interrupted")
    val backend     = DecodingPTX { bytes =>
      calls.incrementAndGet() match {
        case 1 => Left(PTXISA.DecodeError.InvalidUTF8(0))
        case 2 => throw interrupted
        case _ => PTXISA.decode(bytes)
      }
    }
    assert(backend.load(image).isLeft)
    val observed = try { val _ = backend.load(image); None }
    catch { case error: InterruptedException => Some(error) }
    assert(observed.contains(interrupted))
    backend.load(image).toOption.get.close()
    backend.load(image).toOption.get.close()
    assertEquals(calls.get(), 4)
  }

  test("interrupting a parse waiter leaves the owner usable and does not leak a reference") {
    val entered = CountDownLatch(1)
    val release = CountDownLatch(1)
    val calls   = AtomicInteger()
    val backend = DecodingPTX { bytes =>
      val _ = calls.incrementAndGet()
      entered.countDown()
      if (!release.await(5, TimeUnit.SECONDS)) throw IllegalStateException("parse was not released")
      PTXISA.decode(bytes)
    }
    Using.resource(threads.Pool(1)) { pool =>
      val owner  = pool.executor.submit(() => backend.load(image))
      val waiter = FutureTask(() => backend.load(image))
      val thread = Thread(waiter)
      try {
        assert(entered.await(5, TimeUnit.SECONDS))
        thread.start()
        thread.interrupt()
        val error = intercept[java.util.concurrent.ExecutionException](waiter.get(2, TimeUnit.SECONDS))
        error.getCause match {
          case _: InterruptedException => ()
          case other                   => fail(s"expected interrupted waiter, got $other")
        }
      } finally {
        release.countDown()
        thread.join(5000)
      }
      owner.get(5, TimeUnit.SECONDS).toOption.get.close()
      backend.load(image).toOption.get.close()
      assertEquals(calls.get(), 2)
    }
  }

  test("concurrent identical PTX loads keep bindings and runtime lifetime independent") {
    val backend  = PTX()
    val executor = Executors.newFixedThreadPool(2)
    try
      Using.Manager { use =>
        val firstSession  = use(ExecutionSession(List(backend)))
        val secondSession = use(ExecutionSession(List(backend)))
        val firstLoad     = executor.submit(() => firstSession.load(PTX.encoding, image))
        val secondLoad    = executor.submit(() => secondSession.load(PTX.encoding, image))
        val first         = firstLoad.get().fold(error => fail(error.message), identity)
        val second        = secondLoad.get().fold(error => fail(error.message), identity)

        assert(firstSession.load(PTX.encoding, Vec.from(".entry {".getBytes(StandardCharsets.UTF_8))).isLeft)
        val oversizedGlobal =
          ".version 8.0\n.target sm_80\n.address_size 64\n.global .u32 huge[2147483647];"
        assert(firstSession.load(
          PTX.encoding,
          Vec.from(oversizedGlobal.getBytes(StandardCharsets.UTF_8))
        ).isLeft)

        val firstGlobal  = firstSession.registerAllocation(4L, AllocationKind.Shared).toOption.get
        val secondGlobal = secondSession.registerAllocation(4L, AllocationKind.Shared).toOption.get
        val firstOutput  = firstSession.registerAllocation(4L, AllocationKind.Shared).toOption.get
        val secondOutput = secondSession.registerAllocation(4L, AllocationKind.Shared).toOption.get
        assertEquals(firstSession.write(firstGlobal, 0L, LittleEndian.toVec(17, 4)), Right(()))
        assertEquals(secondSession.write(secondGlobal, 0L, LittleEndian.toVec(29, 4)), Right(()))
        assertEquals(firstSession.bindGlobal(first, "shared_value", firstGlobal, 0L), Right(()))
        assertEquals(secondSession.bindGlobal(second, "shared_value", secondGlobal, 0L), Right(()))

        def launch(session: ExecutionSession, program: ProgramId, output: AllocationId): Unit = {
          val task = KernelTask.validated(
            "read_global",
            LaunchGeometry.Dimensions.One,
            LaunchGeometry.Dimensions.One,
            Vec(KernelTask.Argument.Memory(output, 0L))
          ).fold(message => fail(message), identity)
          val completion = session.launch(program, task).toOption.get
          assertEquals(session.completion(completion), Right(CompletionStatus.Succeeded))
        }

        launch(firstSession, first, firstOutput)
        assertEquals(firstSession.read(firstOutput, 0L, 4).map(LittleEndian.decode(_, 0, 4)), Right(17L))
        assertEquals(firstSession.unload(first), Right(()))
        launch(secondSession, second, secondOutput)
        assertEquals(secondSession.read(secondOutput, 0L, 4).map(LittleEndian.decode(_, 0, 4)), Right(29L))
      }.get
    finally {
      val _ = executor.shutdownNow()
    }
  }
}
