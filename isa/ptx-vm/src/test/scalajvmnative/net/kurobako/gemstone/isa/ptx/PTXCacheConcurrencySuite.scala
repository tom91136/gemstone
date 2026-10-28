package net.kurobako.gemstone.isa.ptx

import java.nio.charset.StandardCharsets
import java.util.concurrent.CountDownLatch
import java.util.concurrent.ExecutionException
import java.util.concurrent.FutureTask
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.rpc.*
import net.kurobako.gemstone.rpc.ExecutionRPC.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.testing.either.*
import net.kurobako.gemstone.vm.*
import scala.util.Using

class PTXCacheConcurrencySuite extends munit.FunSuite {
  import PTXCacheSuite.*

  test("a blocked PTX parse does not block a different image or an existing program's release") {
    val entered   = CountDownLatch(1)
    val release   = CountDownLatch(1)
    val slowImage = Vec.from(s"$source\n// slow".getBytes(StandardCharsets.UTF_8))
    val backend   = PTXBackend(decode = { bytes =>
      if (bytes == slowImage) {
        entered.countDown()
        if (!release.await(10, TimeUnit.SECONDS)) throw IllegalStateException("parse was not released")
      }
      ISA.decode(bytes)
    })
    Using.resource(threads.Pool(2)) { pool =>
      val existing = backend.load(image).value
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
      } finally release.countDown()
      slow.get(5, TimeUnit.SECONDS).value.close()
    }
  }

  test("equal-content concurrent loads share parsing until the last reference closes") {
    val calls   = AtomicInteger()
    val entered = CountDownLatch(1)
    val release = CountDownLatch(1)
    val backend = PTXBackend(
      decode = { bytes =>
        val _ = calls.incrementAndGet()
        entered.countDown()
        if (!release.await(5, TimeUnit.SECONDS)) throw IllegalStateException("parse was not released")
        ISA.decode(bytes)
      },
      retainReleased = false
    )
    Using.resource(threads.Pool(8)) { pool =>
      val loads = Vector.fill(8)(pool.executor.submit(() =>
        backend.load(Vec.from(source.getBytes(StandardCharsets.UTF_8)))
      ))
      try assert(entered.await(5, TimeUnit.SECONDS))
      finally release.countDown()
      val programs = loads.map(_.get(5, TimeUnit.SECONDS).value)
      assertEquals(calls.get(), 1)
      programs.dropRight(1).foreach(_.close())
      val retained = backend.load(image).value
      try {
        assertEquals(calls.get(), 1)
        programs.last.close()
      } finally retained.close()
      backend.load(image).value.close()
      assertEquals(calls.get(), 2)
    }
  }

  test("interrupting a parse waiter leaves the owner usable and does not leak a reference") {
    val entered = CountDownLatch(1)
    val release = CountDownLatch(1)
    val calls   = AtomicInteger()
    val backend = PTXBackend(
      decode = { bytes =>
        val _ = calls.incrementAndGet()
        entered.countDown()
        if (!release.await(5, TimeUnit.SECONDS)) throw IllegalStateException("parse was not released")
        ISA.decode(bytes)
      },
      retainReleased = false
    )
    Using.resource(threads.Pool(1)) { pool =>
      val owner  = pool.executor.submit(() => backend.load(image))
      val waiter = FutureTask(() => backend.load(image))
      val thread = Thread(waiter)
      try {
        assert(entered.await(5, TimeUnit.SECONDS))
        thread.start()
        thread.interrupt()
        val error = intercept[ExecutionException](waiter.get(2, TimeUnit.SECONDS))
        error.getCause match {
          case _: InterruptedException => ()
          case other                   => fail(s"expected interrupted waiter, got $other")
        }
      } finally {
        release.countDown()
        thread.join(5000)
      }
      owner.get(5, TimeUnit.SECONDS).value.close()
      backend.load(image).value.close()
      assertEquals(calls.get(), 2)
    }
  }

  test("concurrent identical PTX loads keep bindings and runtime lifetime independent") {
    val backend = PTXBackend()
    Using.Manager { use =>
      val pool          = use(threads.Pool(2))
      val firstSession  = use(ExecutionSession(List(backend)))
      val secondSession = use(ExecutionSession(List(backend)))
      val firstLoad     = pool.executor.submit(() => firstSession.load(ProgramEncoding.PTXText, image))
      val secondLoad    = pool.executor.submit(() => secondSession.load(ProgramEncoding.PTXText, image))
      val first         = firstLoad.get(30, TimeUnit.SECONDS).value
      val second        = secondLoad.get(30, TimeUnit.SECONDS).value

      assert(firstSession.load(ProgramEncoding.PTXText, Vec.from(".entry {".getBytes(StandardCharsets.UTF_8))).isLeft)
      val oversizedGlobal =
        ".version 8.0\n.target sm_80\n.address_size 64\n.global .u32 huge[2147483647];"
      assert(firstSession.load(
        ProgramEncoding.PTXText,
        Vec.from(oversizedGlobal.getBytes(StandardCharsets.UTF_8))
      ).isLeft)

      val firstGlobal  = firstSession.registerAllocation(4).value
      val secondGlobal = secondSession.registerAllocation(4).value
      val firstOutput  = firstSession.registerAllocation(4).value
      val secondOutput = secondSession.registerAllocation(4).value
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
        ).value
        val completion = session.launch(program, task).value
        assertEquals(session.completion(completion), Right(CompletionStatus.Succeeded))
      }

      launch(firstSession, first, firstOutput)
      assertEquals(firstSession.read(AllocationRange(firstOutput, 0L, 4)).map(LittleEndian.decode(_, 0, 4)), Right(17L))
      assertEquals(firstSession.unload(first), Right(()))
      launch(secondSession, second, secondOutput)
      assertEquals(
        secondSession.read(AllocationRange(secondOutput, 0L, 4)).map(LittleEndian.decode(_, 0, 4)),
        Right(29L)
      )
    }.get
  }
}
