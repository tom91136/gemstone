import java.util.Locale
import sbt.internal.util.MessageOnlyException

/** The build's reads of registered names; the grammar mirrors `configuration` in core. */
object BuildEnvironment {
  private def value(name: String): Option[String] = sys.env.get(name).filter(_.nonEmpty)

  private def enabled(name: String): Boolean = value(name).exists(_.toLowerCase(Locale.ROOT) match {
    case "1" | "true" | "on" | "y" | "yes"  => true
    case "0" | "false" | "off" | "n" | "no" => false
    case raw => throw MessageOnlyException(s"$name must be one of 1 true on y yes 0 false off n no, not '$raw'")
  })

  private def positive(name: String, default: => Int): Int = value(name).fold(default) { raw =>
    Option.when(raw.forall(c => c >= '0' && c <= '9'))(raw).flatMap(_.toIntOption).filter(_ > 0)
      .getOrElse(throw MessageOnlyException(s"$name must be a positive integer, not '$raw'"))
  }

  lazy val offline: Boolean            = enabled("GEMSTONE_OFFLINE_ON")
  lazy val threadCount: Int            = positive("GEMSTONE_THREAD_COUNT", sys.runtime.availableProcessors())
  lazy val containerExecutable: String = value("GEMSTONE_CONTAINER_EXE").getOrElse("podman")
  lazy val buildType: String           = value("CMAKE_BUILD_TYPE").getOrElse("Release")
  lazy val syclShardIndex: String      = value("CONFORMANCE_SYCL_SHARD_INDEX").getOrElse("")
  lazy val shardRouteList: String      = value("CONFORMANCE_SHARD_ROUTE_LIST").getOrElse("")
}
