package net.kurobako.gemstone.server

import com.monovore.decline.Command
import java.nio.file.Files
import java.nio.file.Path
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.environment as env
import net.kurobako.gemstone.server.*
import net.kurobako.gemstone.server.Transport.*
import net.kurobako.gemstone.server.protocol.*
import net.kurobako.gemstone.testing.Suite
import scala.util.Using

class LaunchSuite extends Suite {
  private val endpoint = Endpoint.Tcp("127.0.0.1", Port(43123))
  private val client   = ClientId(7L)
  private val root     = Path.of("/opt/gemstone")

  private def parse(arguments: String*): Either[String, Launch.Options] =
    Command("run", "test launcher options")(CLI.launchOptions).parse(arguments).left.map(help => help.toString)

  private def library(api: API): Path = root.resolve(api.lib)

  private def selected(apis: API*): NonEmpty[Set[API]] = apis.toSet.unsafe

  test("API lists are comma-separated sets") {
    def apis(value: String) = parse("--apis", value, "--", "application").map(_.apis)
    assertEquals(apis("hip,hsa,cuda,opencl,vulkan,levelzero"), Right(selected(API.values.toSeq*)))
    assert(apis("sycl").isLeft)
    assert(apis("cuda,").isLeft)
  }

  test("launcher accepts the public run syntax") {
    val parsed = parse("--apis", "hip,hsa", "--", "application", "argument")
      .fold(help => fail(help), value => value)

    assertEquals(parsed.apis, selected(API.HIP, API.HSA))
    assertEquals(parsed.serverEndpoint, None)
    assertEquals(parsed.executable.raw, "application")
    assertEquals(parsed.arguments.map(argument => s"$argument").toVector, Vector("argument"))
  }

  test("process arguments allow empty strings and reject embedded NUL at the boundary") {
    assertEquals(parse("--", "application", "").map(_.arguments.map(_.raw).toVector), Right(Vector("")))
    assert(parse("--", "bad\u0000application").isLeft)
    assert(parse("--", "application", "bad\u0000argument").isLeft)
  }

  test("launcher accepts a shared server endpoint") {
    val parsed = parse("--server", "tcp://127.0.0.1:43123", "--", "application")
      .fold(help => fail(help), value => value)

    assertEquals(parsed.serverEndpoint, Some(Endpoint.Tcp("127.0.0.1", Port(43123))))
  }

  test("all selected APIs coexist in one environment") {
    val environment = Launch.prepareEnvironment(
      selected(API.values.toSeq*),
      root,
      endpoint,
      client,
      Map("LD_PRELOAD" -> "/existing.so", "LD_LIBRARY_PATH" -> "/vendor/lib")
    )

    val preload = Vector(API.CUDA, API.HIP, API.HSA, API.OpenCL)
      .map(api => library(api).toString) :+ "/existing.so"
    assertEquals(environment.set("LD_PRELOAD"), preload.mkString(":"))
    assertEquals(environment.set("LD_LIBRARY_PATH"), s"$root:${root.resolve("vulkan")}:/vendor/lib")
    assertEquals(environment.set("OCL_ICD_FILENAMES"), library(API.OpenCL).toString)
    assertEquals(environment.set("OCL_ICD_VENDORS"), library(API.OpenCL).toString)
    assertEquals(environment.set("ZE_ENABLE_ALT_DRIVERS"), library(API.LevelZero).toString)
    assertEquals(environment.set(env.RpcEndpoint), s"$endpoint")
    assertEquals(environment.set(env.RpcClientId), s"$client")
  }

  test("Vulkan loader directory is absent when Vulkan is not selected") {
    val environment = Launch.prepareEnvironment(selected(API.CUDA), root, endpoint, client, Map.empty)
    assertEquals(environment.set("LD_LIBRARY_PATH"), s"$root")
    assert(!environment.set("LD_LIBRARY_PATH").contains(s"${root.resolve("vulkan")}"))
  }

  test("process-scoped execution profiles reject malformed values before launch") {
    val result = Launch.launch(
      selected(API.CUDA),
      root,
      Map(env.ExecutionMaxInstructionCount -> "not-a-number")
    )(_ => 0)
    assert(result.isLeft)
    assert(result.swap.exists(_.contains("invalid execution profile")))
  }

  private val directory = Path.of("/unused")
  private val apis      = selected(API.HSA)

  test("child exit status is preserved") {
    assertEquals(Launch.launch(apis, directory, Map.empty)(_ => 7), Right(7))
  }

  test("child receives the selected provider environment") {
    val result = Launch.launch(apis, directory, Map.empty) { environment =>
      environment.set("LD_PRELOAD")
    }
    assertEquals(result, Right(directory.toAbsolutePath.normalize().resolve(API.HSA.lib).toString))
  }

  test("child receives an isolated client from a shared server") {
    Using.resource(Server.start(Server.Settings(parallelism = 2))) { server =>
      val result = Launch.launch(apis, directory, Map.empty, Some(server.endpoint)) { environment =>
        val first  = environment.set(env.RpcClientId)
        val second = Launch.launch(apis, directory, Map.empty, Some(server.endpoint)) { other =>
          assertEquals(other.set(env.RpcEndpoint), server.endpoint.toString)
          other.set(env.RpcClientId)
        }
        first -> second
      }
      assert(result.exists { case (first, second) =>
        first != server.client.raw.toString && second.exists(_ != first)
      })
    }
  }

  test("launcher library names match the native build outputs") {
    API.values.foreach { api =>
      val library = native.directory.resolve(api.lib)
      assert(Files.isRegularFile(library), s"missing native library for $api: $library")
    }
  }

}
