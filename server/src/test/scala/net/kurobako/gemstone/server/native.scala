package net.kurobako.gemstone.server

import java.nio.file.Path
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.environment as env
import net.kurobako.gemstone.testing.*
import scala.concurrent.duration.FiniteDuration

private object native {
  lazy val directory: Path = Path.of(
    sys.env.getOrElse(
      env.NativeDirectory,
      throw IllegalStateException(s"${env.NativeDirectory} was not supplied by the build")
    )
  )

  /** Use only the providers and optional loader layers built for these tests. */
  def providerEnvironment: Map[String, String] = Map(
    "LD_LIBRARY_PATH"              -> directory.toString,
    "ZE_ENABLE_ALT_DRIVERS"        -> directory.resolve(API.LevelZero.lib).toString,
    "ZEL_LIBRARY_PATH"             -> directory.toString,
    "ZE_ENABLE_LOADER_DEBUG_TRACE" -> "0",
    "OCL_ICD_FILENAMES"            -> directory.resolve(API.OpenCL.lib).toString
  )

  /** A child environment that connects to the server at `endpoint` as `client`. */
  def serverEnvironment(
      endpoint: Transport.Endpoint,
      client: protocol.ClientId,
      set: Map[String, String] = Map.empty,
      unset: Set[String] = Set.empty
  ): Launch.Environment =
    Launch.Environment(Map(env.RpcEndpoint -> endpoint.toString, env.RpcClient -> client.raw.toString) ++ set, unset)

  def run(
      command: NonEmpty[Vec[String]],
      directory: Path,
      changes: Launch.Environment,
      timeout: FiniteDuration
  ): process.Result = {
    val inheritedOverrides = sys.env.keySet.filter(name =>
      name.startsWith("GEMSTONE_") || name.startsWith("ZE_") || name.startsWith("ZEL_") ||
        name.startsWith("ZET_") || name.startsWith("OCL_ICD_") || name == "UR_ADAPTERS_FORCE_LOAD" ||
        name == "LD_PRELOAD"
    )
    process.run(command.toSeq, directory, changes.set, inheritedOverrides ++ changes.unset, timeout)
  }
}
