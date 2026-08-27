package net.kurobako.gemstone.codegen

import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import scala.jdk.CollectionConverters.*

object GenerateComputeAPIs {
  private val OpenCLFunction    = raw"(?s)CL_API_ENTRY\s+.*?\s+CL_API_CALL\s+(cl[A-Z][A-Za-z0-9_]*)\s*\(".r
  private val LevelZeroFunction = raw"(?s)ZE_APIEXPORT\s+.*?\s+ZE_APICALL\s+(ze[A-Z][A-Za-z0-9_]*)\s*\(".r

  def main(arguments: Array[String]): Unit = arguments.toVector match {
    case Vector(references, openCLOutput, levelZeroOutput, openCLVersion, levelZeroVersion) =>
      val root            = Paths.get(references)
      val openCLHeaders   = headerText(root.resolve("khronos/opencl/CL"))
      val levelZeroHeader = Files.readString(
        root.resolve("oneapi/level-zero/include/ze_api.h"),
        StandardCharsets.UTF_8
      )
      val openCLNames    = OpenCLFunction.findAllMatchIn(openCLHeaders).map(_.group(1)).toVector.distinct.sorted
      val levelZeroNames = LevelZeroFunction.findAllMatchIn(levelZeroHeader).map(_.group(1)).toVector.distinct.sorted
      require(openCLNames.size >= 100, s"unexpectedly small OpenCL surface: ${openCLNames.size}")
      require(levelZeroNames.size >= 100, s"unexpectedly small Level Zero surface: ${levelZeroNames.size}")
      write(Paths.get(openCLOutput), renderOpenCL(openCLNames, openCLVersion))
      write(Paths.get(levelZeroOutput), renderLevelZero(levelZeroNames, levelZeroVersion))
      println(s"generated ${openCLNames.size} OpenCL and ${levelZeroNames.size} Level Zero methods")
    case _ =>
      throw new IllegalArgumentException(
        "usage: GenerateComputeAPIs <references> <opencl-output> <level-zero-output> <opencl-version> <level-zero-version>"
      )
  }

  private def headerText(root: Path): String = {
    val stream = Files.walk(root)
    try
      stream.iterator().asScala.filter(path => Files.isRegularFile(path) && path.toString.endsWith(".h")).toVector
        .sorted
        .map(path => Files.readString(path, StandardCharsets.UTF_8))
        .mkString("\n")
    finally stream.close()
  }

  private def renderOpenCL(names: Vector[String], version: String): String =
    render(
      packageName = "net.kurobako.gemstone.opencl",
      traitName = "OpenCL",
      resultName = "OpenCLResult",
      errorName = "OpenCLError",
      version = version,
      methods = names
    )

  private def renderLevelZero(names: Vector[String], version: String): String =
    render(
      packageName = "net.kurobako.gemstone.levelzero",
      traitName = "LevelZero",
      resultName = "LevelZeroResult",
      errorName = "LevelZeroError",
      version = version,
      methods = names
    )

  private def render(
      packageName: String,
      traitName: String,
      resultName: String,
      errorName: String,
      version: String,
      methods: Vector[String]
  ): String = {
    val declarations =
      methods.map(name => s"  def $name(): $resultName[Nothing] = unsupported(\"$name\")").mkString("\n")
    s"""|// Generated from the pinned authoritative headers. Do not edit by hand.
        |package $packageName
        |
        |final case class $errorName(code: Int, message: String)
        |
        |enum $resultName[+A] {
        |  case Success(value: A)
        |  case Failure(error: $errorName)
        |}
        |
        |/** Complete exported C-name surface. Signatures remain fail-closed until given a typed wire contract. */
        |trait $traitName {
        |$declarations
        |
        |  private def unsupported(name: String): $resultName[Nothing] =
        |    $resultName.Failure($errorName(-1, s"$$name is not implemented"))
        |}
        |
        |object $traitName {
        |  val HeaderVersion: String = "$version"
        |  val SurfaceSize: Int = ${methods.size}
        |}
        |""".stripMargin
  }

  private def write(path: Path, content: String): Unit = {
    val _ = Files.createDirectories(path.getParent)
    val _ = Files.writeString(path, content, StandardCharsets.UTF_8)
  }
}
