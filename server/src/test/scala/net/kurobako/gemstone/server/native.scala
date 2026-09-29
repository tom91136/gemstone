package net.kurobako.gemstone.server

import java.nio.file.Files
import java.nio.file.Path
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.environment as env
import net.kurobako.gemstone.testing.*
import scala.concurrent.duration.FiniteDuration

private object native {
  val root: Path = Iterator.unfold(Option(Path.of(sys.props("user.dir")).toAbsolutePath.normalize))(
    _.map(path => path -> Option(path.getParent))
  )
    .find(path => Files.isRegularFile(path.resolve("build.sbt")))
    .getOrElse(throw IllegalStateException("cannot locate Gemstone repository root"))

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
