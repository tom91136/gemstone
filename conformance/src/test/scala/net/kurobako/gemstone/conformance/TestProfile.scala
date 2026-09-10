package net.kurobako.gemstone.conformance

import java.nio.file.Files
import java.nio.file.Path

/** Profile precedence and env-file conventions adapted from Polyregion's polytest/profile.hpp. */
private[conformance] object TestProfile {
  final case class Oracle(provider: String, target: String, resource: String)
  final case class Profile(file: Path, environment: Map[String, String], oracles: Vector[Oracle]) {
    require(oracles.map(_.provider).distinct.size == oracles.size, s"duplicate oracle providers in $file")
    def oracle(provider: String): Option[Oracle] = oracles.find(_.provider == provider)
    def buildEnvironment: Map[String, String]    =
      oracle("cuda").filter(_.target.nonEmpty).map(x => "GEMSTONE_CUDA_ARCH" -> x.target).toMap ++
        oracle("hip").orElse(oracle("hsa")).filter(_.target.nonEmpty)
          .map(x => "GEMSTONE_AMDGPU_TARGETS" -> x.target).toMap
  }

  def load(
      directory: Path,
      environment: Map[String, String],
      hostname: String,
      os: String
  ): Profile = {
    val bases = environment.get("GEMSTONE_TEST_PROFILE").filter(_.nonEmpty).toVector :+ hostname.takeWhile(_ != '.')
    require(bases.forall(_.matches("[A-Za-z0-9_.-]+")), "invalid test profile or hostname")
    val candidates = bases.distinct.flatMap(base => Vector(s"$base.$os.env", s"$base.env")) :+ "default.env"
    val file       = candidates.map(directory.resolve).find(Files.isRegularFile(_))
      .getOrElse(throw IllegalArgumentException(s"no test profile or default.env in $directory"))
    val assignments = Files.readString(file).linesIterator.flatMap { raw =>
      val line = raw.trim.stripPrefix("export ")
      if line.isEmpty || line.startsWith("#") then None
      else
        line.split("=", 2) match {
          case Array(key, value) if key.trim.stripSuffix("+").matches("[A-Za-z_][A-Za-z_0-9]*") =>
            Some((key.trim.stripSuffix("+"), key.trim.endsWith("+"), value.trim))
          case _ => throw IllegalArgumentException(s"invalid profile assignment in $file: $raw")
        }
    }.toVector
    val values = assignments.foldLeft(Map.empty[String, String]) { case (current, (key, append, value)) =>
      current.updated(key, if append then current.getOrElse(key, "") + value else value)
    } ++ environment.filter { case (key, _) => key == "GEMSTONE_TEST_ORACLES" }
    val oracles = values.getOrElse("GEMSTONE_TEST_ORACLES", "").split("[;:]").iterator.map(_.trim)
      .filter(_.nonEmpty).map { token =>
        token.split("!", 2) match {
          case Array(specification, resource) if resource.matches("[A-Za-z0-9_-]+") =>
            specification.split("@", 2) match {
              case Array(provider, target)
                  if Set("cuda", "hip", "hsa", "opencl", "sycl-opencl", "sycl-levelzero").contains(provider) =>
                Oracle(provider, target, "gpu-" + resource)
              case _ => throw IllegalArgumentException(s"invalid oracle target: $token")
            }
          case _ => throw IllegalArgumentException(s"oracle must specify a physical GPU resource: $token")
        }
      }.toVector
    Profile(file, values - "GEMSTONE_TEST_ORACLES", oracles)
  }

  lazy val current: Profile = load(
    ConformanceSupport.root.resolve("conformance/profiles"),
    sys.env,
    sys.env.get(
      "HOSTNAME"
    ).filter(_.nonEmpty).getOrElse(scala.util.Try(java.net.InetAddress.getLocalHost.getHostName).getOrElse("unknown")),
    sys.props("os.name").toLowerCase match {
      case value if value.contains("linux")   => "linux"
      case value if value.contains("windows") => "windows"
      case _                                  => "darwin"
    }
  )
  def environment: Map[String, String] = current.environment ++ current.buildEnvironment ++ sys.env
}
