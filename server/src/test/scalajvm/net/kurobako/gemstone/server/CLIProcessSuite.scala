package net.kurobako.gemstone.server

import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.attribute.PosixFileAttributeView
import java.nio.file.attribute.PosixFilePermissions
import java.util.concurrent.TimeUnit
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.environment as env
import net.kurobako.gemstone.platform.*
import net.kurobako.gemstone.rpc.*
import net.kurobako.gemstone.server.*
import net.kurobako.gemstone.server.Transport.*
import net.kurobako.gemstone.server.protocol.*
import net.kurobako.gemstone.server.recording.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.vm.*
import scala.concurrent.duration.*
import scala.util.Using

class CLIProcessSuite extends Suite {
  // The same process checks can target a freshly linked Native CLI from the JVM test runner.
  private val executable = sys.props.get("gemstone.cli.executable").fold {
    val classpath = sys.props("gemstone.cli.classpath")
    Seq(
      Path.of(sys.props("java.home"), "bin", "java").toString,
      "-cp",
      classpath,
      "net.kurobako.gemstone.server.CLI"
    )
  }(value => Seq(Path.of(value).toAbsolutePath.toString))

  private class Child(directory: Path, arguments: Seq[String], environment: Map[String, String] = Map.empty)
      extends AutoCloseable {
    private val output  = Files.createTempFile(directory, "cli-", ".log")
    private val builder = ProcessBuilder((executable ++ arguments)*)
      .directory(directory.toFile).redirectErrorStream(true).redirectOutput(output.toFile)
    // Child behaviour should not depend on a developer's recording or worker settings.
    builder.environment().keySet().removeIf(_.startsWith("GEMSTONE_"))
    environment.foreach((key, value) => builder.environment().put(key, value))
    private val process   = builder.start()
    def text: String      = Files.readString(output)
    def terminate(): Unit = { val _ = process.destroy() }
    def awaitExit(): Int  = {
      assert(process.waitFor(20, TimeUnit.SECONDS), s"CLI timed out: $text")
      process.exitValue()
    }
    def awaitServing(): Unit = {
      val deadline = monotonicNanos + TimeUnit.SECONDS.toNanos(20)
      while (!text.contains("gemstone RPC server listening") && process.isAlive && monotonicNanos < deadline)
        Thread.sleep(10)
      assert(text.contains("gemstone RPC server listening"), s"CLI failed to start: $text")
    }
    override def close(): Unit =
      if (process.isAlive) {
        val _ = process.destroyForcibly()
        assert(process.waitFor(10, TimeUnit.SECONDS), "CLI failed to terminate")
      }
  }

  test("CLI help and malformed arguments have distinct exit statuses") {
    files.temporary { directory =>
      Using.resource(Child(directory, Seq("--help"))) { child =>
        assertEquals(child.awaitExit(), 0)
        assert(child.text.contains("serve"))
      }
      Using.resource(Child(directory, Seq("shutdown", "--server", "tcp://127.0.0.1:0"))) { child =>
        assertEquals(child.awaitExit(), 2)
        assert(child.text.contains("server"), child.text)
      }
    }
  }

  test("CLI serves from an unrelated directory, publishes private endpoint files, records, replays, and shuts down") {
    files.temporary { directory =>
      val endpointFile = directory.resolve("endpoint.json")
      val archive      = directory.resolve("capture.rpc")
      Using.resource(Child(
        directory,
        Seq(
          "serve",
          "--endpoint-file",
          endpointFile.toString,
          "--record",
          archive.toString
        )
      )) { server =>
        server.awaitServing()
        val endpoint = ujson.read(Files.readString(endpointFile))
        val address  = Endpoint.parse(endpoint("endpoint").str).toOption.get
        val client   = endpoint("client").num.toLong.refine[ClientId].toOption.get
        Seq(endpointFile).foreach { file =>
          if (Option(Files.getFileAttributeView(file, classOf[PosixFileAttributeView])).isDefined)
            assertEquals(Files.getPosixFilePermissions(file), PosixFilePermissions.fromString("rw-------"))
        }
        Using.resource(connections.Client(address, client)) { connection =>
          assertEquals(connection.call(ExecutionRPC.Operation.Ping), Right(()))
        }
        Using.resource(Child(directory, Seq("shutdown", "--server", address.toString))) { shutdown =>
          assertEquals(shutdown.awaitExit(), 0, shutdown.text)
        }
        assertEquals(server.awaitExit(), 0, server.text)
      }
      val summary = Journal.inspect(archive)
      assert(summary.replayable)
      assertEquals(summary.frames.raw, 2)
      Using.resource(Child(directory, Seq("inspect", archive.toString))) { inspect =>
        assertEquals(inspect.awaitExit(), 0, inspect.text)
        assert(inspect.text.contains("ExecutionProfile"), inspect.text)
      }
      Using.resource(Child(directory, Seq("replay", archive.toString))) { replay =>
        assertEquals(replay.awaitExit(), 0, replay.text)
        assert(replay.text.contains(s"Report(${summary.events},1,0)"), replay.text)
      }
    }
  }
  test("terminating a launcher revokes its client and releases its shared-server worker") {
    files.temporary { directory =>
      Using.Manager { use =>
        val server = use(Server.start(Server.Settings(
          limits = TaskControl.Limits(1000000000000L),
          parallelism = 1
        )))
        val child = use(Child(
          directory = directory,
          arguments = Seq(
            "run",
            "--apis",
            "hsa",
            "--server",
            server.endpoint.toString,
            "--",
            "/bin/sh",
            "-c",
            "printf '%s' \"$GEMSTONE_RPC_CLIENT_ID\" > client-id; exec sleep 60"
          ),
          environment = Map(env.NativeDir -> native.directory.toString)
        ))
        val clientFile = directory.resolve("client-id")
        val deadline   = monotonicNanos + TimeUnit.SECONDS.toNanos(10)
        while (!Files.exists(clientFile) && monotonicNanos < deadline) Thread.sleep(10)
        assert(Files.exists(clientFile), child.text)
        val clientId   = Files.readString(clientFile).toLong.refine[ClientId].toOption.get
        val connection = use(connections.Client(server.endpoint, clientId))
        def launch(client: connections.Client, body: String): CompletionId = {
          val source  = s".version 8.0\n.target sm_80\n.address_size 64\n.visible .entry kernel() { $body }"
          val program = client.call(ExecutionRPC.Operation.LoadProgram(
            ProgramEncoding.PTXText,
            Vec.from(source.getBytes(StandardCharsets.UTF_8))
          )).toOption.get
          val one = LaunchGeometry.Dimensions.One
          client.call(ExecutionRPC.Operation.Launch(
            program,
            "kernel",
            one,
            one,
            one.toGlobalDimensions,
            1,
            LaunchGeometry.Coordinates.origin,
            0,
            0,
            Vec.empty,
            Vec.empty
          )).toOption.get
        }
        val pending = launch(connection, "LOOP: bra LOOP;")
        assertEquals(
          connection.call(ExecutionRPC.Operation.QueryCompletion(pending)),
          Right(ExecutionRPC.CompletionStatus.Pending)
        )
        child.terminate()
        assert(child.awaitExit() != 0, child.text)
        assert(connection.call(ExecutionRPC.Operation.Ping).isLeft, "terminated launcher's client remained live")
        val next       = use(connections.Client(server.endpoint, server.client))
        val completion = launch(next, "ret;")
        assertEquals(next.awaitCompletion(completion), Right(ExecutionRPC.CompletionStatus.Succeeded))
      }.get
    }
  }

  test("packaged CLI loads AMDGPU metadata and executes an HSA kernel from an unrelated directory") {
    files.temporary { directory =>
      val endpointFile = directory.resolve("endpoint.json")
      Using.resource(Child(
        directory,
        Seq(
          "serve",
          "--listen",
          Endpoint.Unix(directory.resolve("server.sock")).toString,
          "--endpoint-file",
          endpointFile.toString
        )
      )) { server =>
        server.awaitServing()
        val endpoint        = ujson.read(Files.readString(endpointFile))
        val nativeDirectory = native.directory
        val result          = native.run(
          Vec.nonEmpty(nativeDirectory.resolve("gemstone_memory_hsa").toString),
          directory,
          native.serverEnvironment(
            Endpoint.parse(endpoint("endpoint").str).fold(fail(_), identity),
            endpoint("client").num.toLong.refine[ClientId].fold(fail(_), identity),
            Map("LD_LIBRARY_PATH" -> nativeDirectory.toString, env.TestMemoryProbeList -> "rpc_only"),
            Set(env.SystemPagingOn, env.TestMemoryConcurrencyDir, env.RecordFile)
          ),
          20.seconds
        )
        assertEquals(result.exit, 0, result.diagnostic)
        assert(result.stdout.contains("verified host-memory transport pass=1"), result.diagnostic)
        Using.resource(Child(directory, Seq("shutdown", "--server", endpoint("endpoint").str))) { shutdown =>
          assertEquals(shutdown.awaitExit(), 0, shutdown.text)
        }
        assertEquals(server.awaitExit(), 0, server.text)
      }
    }
  }

}
