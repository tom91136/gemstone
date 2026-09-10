package net.kurobako.gemstone.launcher

import java.nio.file.Path
import java.net.Socket
import java.io.{DataInputStream, DataOutputStream}
import java.nio.charset.StandardCharsets

class ProcessExitSuite extends munit.FunSuite {
  private val layout      = RuntimeLayout(hsaRuntime = Some(Path.of("/unused/libhsa-runtime64.so.1")))
  private val environment = Map("GEMSTONE_EXECUTION_PARALLELISM" -> "1")
  test("process failure before initialization retains its original exit status") {
    assertEquals(GemstoneProcess.attestedExit(ComputeAPI.HSA, layout, environment)(_ => 7), Right(7))
  }
  test("successful processes still require authenticated evidence") {
    assert(GemstoneProcess.attestedExit(ComputeAPI.HSA, layout, environment)(_ => 0).isLeft)
    assert(GemstoneProcess.attested(ComputeAPI.HSA, layout, environment)(_ => 7).isLeft)
  }
  test("authenticated successful process remains successful") {
    val result = GemstoneProcess.attestedExit(ComputeAPI.HSA, layout, environment) { env =>
      val socket = Socket("127.0.0.1", env.set("GEMSTONE_RPC_PORT").toInt)
      socket.setSoTimeout(2000)
      try {
        val output = DataOutputStream(socket.getOutputStream)
        output.writeInt(0x474d4531); output.writeInt(3)
        output.write(env.set("GEMSTONE_RPC_TOKEN").getBytes(StandardCharsets.US_ASCII))
        output.writeLong(1); output.writeLong(1); output.writeInt(0); output.writeInt(0); output.flush()
        val input = DataInputStream(socket.getInputStream)
        assertEquals(input.readInt(), 0x474d4631); assertEquals(input.readInt(), 3)
        assertEquals(input.readInt(), 0); assertEquals(input.readInt(), 0); assertEquals(input.readInt(), 0)
        0
      } finally socket.close()
    }
    assertEquals(result, Right(0))
  }
}
