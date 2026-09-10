package net.kurobako.gemstone.server

import net.kurobako.gemstone.Col
import net.kurobako.gemstone.execution.*
import net.kurobako.gemstone.launcher.UsageSnapshot

import java.nio.file.Files
import java.nio.file.Path
import java.io.DataInputStream
import java.io.DataOutputStream
import java.net.InetAddress
import java.net.ServerSocket
import java.net.SocketTimeoutException
import java.security.SecureRandom
import java.util.HexFormat
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference
import java.util.concurrent.ConcurrentHashMap

class ExecutionRPCConformanceSuite extends munit.FunSuite {
  private object TestBackend extends ProgramExecutor {
    override def load(id: ProgramId, format: ProgramFormat, image: Col[Byte]) =
      Either.cond(
        format == ProgramFormat.SPIRV && image == Col[Byte](3, 2, 35, 7),
        (),
        ExecutionFailure.InvalidRequest("unexpected module")
      )
    override def unload(id: ProgramId)                                                                = Right(())
    override def launch(dispatch: KernelDispatch, memory: ExecutionMemory, control: ExecutionControl) =
      dispatch.arguments match {
        case Col(
              KernelArgument.Allocation(id, offset),
              KernelArgument.Scalar(bytes),
              KernelArgument.Local(64L)
            ) => memory.write(id, offset, bytes)
        case _ => Left(ExecutionFailure.InvalidRequest("unexpected arguments"))
      }
  }

  test("native execution client round-trips program, allocation, dispatch, and completion") {
    val root   = findRoot(Path.of(sys.props("user.dir")))
    val output = compileClient(root)

    val token  = randomToken()
    val server = GemstoneRPCServer.start(token, TestBackend)
    try {
      assertEquals(server.usage, UsageSnapshot(0, 0))
      val run = ProcessBuilder(output.toString).inheritIO()
      run.environment().put("GEMSTONE_RPC_PORT", server.port.toString)
      run.environment().put("GEMSTONE_RPC_TOKEN", token)
      assertEquals(run.start().waitFor(), 0)
      assert(server.usage.executionRequests > 0)
      assertEquals(server.usage.cudaRequests, 0L)
    } finally {
      server.close()
      val _ = Files.deleteIfExists(output)
    }
  }

  private final case class ProfileBackend(observed: AtomicReference[Option[ExecutionLimits]]) extends ProgramExecutor {
    override def load(id: ProgramId, format: ProgramFormat, image: Col[Byte]) =
      TestBackend.load(id, format, image)
    override def unload(id: ProgramId) = TestBackend.unload(id)
    override def launch(dispatch: KernelDispatch, memory: ExecutionMemory, control: ExecutionControl) = {
      observed.set(Some(control.limits))
      TestBackend.launch(dispatch, memory, control)
    }
  }

  test("an explicit server execution profile is applied to each dispatch") {
    val root     = findRoot(Path.of(sys.props("user.dir")))
    val output   = compileClient(root)
    val token    = randomToken()
    val limits   = ExecutionLimits(maxInstructions = 17L, quantum = 3)
    val observed = AtomicReference(Option.empty[ExecutionLimits])
    val backend  = ProfileBackend(observed)
    val server   = GemstoneRPCServer.start(token, backend, limits)
    try {
      val run = ProcessBuilder(output.toString).inheritIO()
      run.environment().put("GEMSTONE_RPC_PORT", server.port.toString)
      run.environment().put("GEMSTONE_RPC_TOKEN", token)
      assertEquals(run.start().waitFor(), 0)
      assertEquals(observed.get(), Some(limits))
    } finally {
      server.close()
      val _ = Files.deleteIfExists(output)
    }
  }

  test("execution connections survive an idle memory connection during a long dispatch") {
    val root   = findRoot(Path.of(sys.props("user.dir")))
    val output = compileClient(root)
    val token  = randomToken()
    val server = GemstoneRPCServer.start(token, TestBackend, ExecutionLimits(maxInstructions = 1L, quantum = 1), 50)
    try {
      val run = ProcessBuilder(output.toString).inheritIO()
      run.environment().put("GEMSTONE_RPC_PORT", server.port.toString)
      run.environment().put("GEMSTONE_RPC_TOKEN", token)
      run.environment().put("GEMSTONE_EXECUTION_HOLD_ALLOCATION", "1")
      assertEquals(run.start().waitFor(), 0)
    } finally {
      server.close()
      val _ = Files.deleteIfExists(output)
    }
  }

  test("independent native processes have isolated execution capability namespaces") {
    val root   = findRoot(Path.of(sys.props("user.dir")))
    val output = compileClient(root)
    val token  = randomToken()
    val server = GemstoneRPCServer.start(token, TestBackend)
    try {
      def start() = {
        val run = ProcessBuilder(output.toString).inheritIO()
        run.environment().put("GEMSTONE_RPC_PORT", server.port.toString)
        run.environment().put("GEMSTONE_RPC_TOKEN", token)
        run.environment().put("GEMSTONE_EXECUTION_HOLD_ALLOCATION", "1")
        run.start()
      }
      val first  = start()
      val second = start()
      assertEquals(first.waitFor(), 0)
      assertEquals(second.waitFor(), 0)
    } finally {
      server.close()
      val _ = Files.deleteIfExists(output)
    }
  }

  test("a lost execution session is never silently reconnected") {
    val root        = findRoot(Path.of(sys.props("user.dir")))
    val output      = compileClient(root, "execution_rpc_session_loss.cpp")
    val token       = randomToken()
    val listener    = ServerSocket(0, 1, InetAddress.getLoopbackAddress)
    val reconnected = AtomicBoolean(false)
    val server      = Thread { () =>
      def serveOne(): Unit = {
        val socket = listener.accept()
        try {
          val input  = DataInputStream(socket.getInputStream)
          val output = DataOutputStream(socket.getOutputStream)
          val header = input.readNBytes(96)
          assertEquals(header.length, 96)
          val payloadSize = java.nio.ByteBuffer.wrap(header, 92, 4).getInt()
          assertEquals(input.readNBytes(payloadSize).length, payloadSize)
          output.writeInt(0x474d4631)
          output.writeInt(3)
          output.writeInt(0)
          output.writeInt(0)
          output.writeInt(0)
          output.flush()
        } finally socket.close()
      }
      serveOne()
      listener.setSoTimeout(500)
      try {
        serveOne()
        reconnected.set(true)
      } catch {
        case _: SocketTimeoutException => ()
      }
    }
    server.start()
    try {
      val run = ProcessBuilder(output.toString).inheritIO()
      run.environment().put("GEMSTONE_RPC_PORT", listener.getLocalPort.toString)
      run.environment().put("GEMSTONE_RPC_TOKEN", token)
      assertEquals(run.start().waitFor(), 0)
      server.join()
      assert(!reconnected.get())
    } finally {
      listener.close()
      server.join()
      val _ = Files.deleteIfExists(output)
    }
  }

  test("work-group execution uses a bounded shared pool") {
    val pool    = BoundedExecutionPool(4)
    val running = AtomicInteger(0)
    val maximum = AtomicInteger(0)
    val visited = AtomicInteger(0)
    val threads = ConcurrentHashMap.newKeySet[String]()
    try {
      val result = pool.parallelFor(1024L) { _ =>
        val active = running.incrementAndGet()
        maximum.accumulateAndGet(active, java.lang.Integer.max)
        threads.add(Thread.currentThread().getName)
        Thread.`yield`()
        visited.incrementAndGet()
        running.decrementAndGet()
        Right(())
      }
      assert(result == Right(()), s"bounded pool returned $result")
      assertEquals(visited.get(), 1024)
      assert(maximum.get() <= 4, s"bounded pool ran ${maximum.get()} workers")
      assert(threads.size() <= 4, s"bounded pool created ${threads.size()} worker threads")
    } finally pool.close()
  }

  test("nested dispatches can use every worker without exceeding the pool") {
    val pool    = BoundedExecutionPool(4)
    val arrived = java.util.concurrent.CountDownLatch(4)
    val result  = java.util.concurrent.CompletableFuture[Either[ExecutionFailure, Unit]]()
    try {
      pool.schedule { () =>
        val completed = pool.parallelFor(4L) { _ =>
          arrived.countDown()
          Either.cond(
            arrived.await(5L, java.util.concurrent.TimeUnit.SECONDS),
            (),
            ExecutionFailure.Failed("a dispatch worker was left idle")
          )
        }
        val _ = result.complete(completed)
      }
      assertEquals(result.get(10L, java.util.concurrent.TimeUnit.SECONDS), Right(()))
    } finally pool.close()
  }

  test("a later job runs before a large grid drains") {
    val pool        = BoundedExecutionPool(2)
    val firstGroups = java.util.concurrent.CountDownLatch(2)
    val release     = java.util.concurrent.CountDownLatch(1)
    val completed   = AtomicInteger(0)
    val heavy       = java.util.concurrent.CompletableFuture[Either[ExecutionFailure, Unit]]()
    val small       = java.util.concurrent.CompletableFuture[Int]()
    val caller      = Thread { () =>
      val result = pool.parallelFor(100L) { index =>
        if (index < 2L) {
          firstGroups.countDown()
          release.await()
        }
        completed.incrementAndGet()
        Right(())
      }
      val _ = heavy.complete(result)
    }
    caller.setDaemon(true)
    try {
      caller.start()
      assert(firstGroups.await(5L, java.util.concurrent.TimeUnit.SECONDS))
      pool.schedule { () =>
        val _ = small.complete(completed.get())
      }
      release.countDown()
      assert(small.get(5L, java.util.concurrent.TimeUnit.SECONDS) < 100)
      assertEquals(heavy.get(5L, java.util.concurrent.TimeUnit.SECONDS), Right(()))
    } finally {
      release.countDown()
      pool.close()
      caller.join(5000L)
    }
  }

  test("execution parallelism has one shared default and rejects host oversubscription") {
    val defaultPool = BoundedExecutionPool.fromEnvironment(Map.empty)
    val hostBound   = math.min(64, math.max(1, Runtime.getRuntime.availableProcessors()))
    try assertEquals(defaultPool.parallelism, math.max(1, hostBound - 2))
    finally defaultPool.close()

    val explicitPool =
      BoundedExecutionPool.fromEnvironment(Map(BoundedExecutionPool.EnvironmentName -> hostBound.toString))
    try assertEquals(explicitPool.parallelism, hostBound)
    finally explicitPool.close()

    val invalidZero = intercept[IllegalArgumentException](BoundedExecutionPool.fromEnvironment(Map(
      BoundedExecutionPool.EnvironmentName -> "0"
    )))
    assert(invalidZero.getMessage.contains("must be positive"))
    val invalidOversubscription = intercept[IllegalArgumentException](BoundedExecutionPool.fromEnvironment(Map(
      BoundedExecutionPool.EnvironmentName -> (hostBound + 1).toString
    )))
    assert(invalidOversubscription.getMessage.contains("exceeds host bound"))
  }

  private def compileClient(root: Path, source: String = "execution_rpc_smoke.cpp"): Path = {
    val output  = Files.createTempFile("gemstone-execution-rpc-", ".smoke")
    val compile = ProcessBuilder(
      "c++",
      "-std=c++17",
      "-Wall",
      "-Wextra",
      "-Wpedantic",
      "-Werror",
      "-pthread",
      "-I",
      root.resolve("native/execution").toString,
      root.resolve("native/execution/gemstone_execution_rpc.cpp").toString,
      root.resolve(s"native/execution/test/$source").toString,
      "-o",
      output.toString
    ).inheritIO().start()
    assertEquals(compile.waitFor(), 0)
    output
  }

  private def randomToken(): String = {
    val bytes = Array.ofDim[Byte](32)
    SecureRandom().nextBytes(bytes)
    HexFormat.of().formatHex(bytes)
  }

  private def findRoot(start: Path): Path =
    Iterator.iterate(start.toAbsolutePath.normalize)(_.getParent)
      .takeWhile(_ != null)
      .find(path => Files.isRegularFile(path.resolve("build.sbt")))
      .getOrElse(throw IllegalStateException(s"cannot find repository root from $start"))
}
