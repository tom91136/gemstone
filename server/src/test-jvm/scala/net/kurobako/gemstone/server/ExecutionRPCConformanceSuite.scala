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
