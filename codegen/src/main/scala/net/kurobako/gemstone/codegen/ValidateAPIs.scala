package net.kurobako.gemstone.codegen

import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import scala.jdk.CollectionConverters.*
import scala.sys.process.Process
import scala.sys.process.ProcessLogger
import upickle.default.ReadWriter
import upickle.default.read
import upickle.default.write

object ValidateAPIs {
  final case class Parameter(name: String, cType: String) derives ReadWriter
  final case class Function(name: String, returnType: String, functionType: String, parameters: Vector[Parameter])
      derives ReadWriter
  final case class Surface(name: String, functions: Vector[Function]) derives ReadWriter
  final case class Manifest(format: Int, surfaces: Vector[Surface]) derives ReadWriter

  private final case class TypeInfo(qualType: String) derives ReadWriter
  private final case class Node(
      kind: Option[String] = None,
      name: Option[String] = None,
      `type`: Option[TypeInfo] = None,
      isImplicit: Option[Boolean] = None,
      inner: Option[Vector[Node]] = None
  ) derives ReadWriter

  private final case class Config(
      name: String,
      language: String,
      headers: Vector[String],
      includeDirectories: Vector[Path],
      defines: Vector[String],
      acceptedName: String => Boolean
  )

  def main(arguments: Array[String]): Unit = {
    val (check, references, output) = arguments.toVector match {
      case Vector(referenceRoot, outputPath)            => (false, Paths.get(referenceRoot), Paths.get(outputPath))
      case Vector("--check", referenceRoot, outputPath) => (true, Paths.get(referenceRoot), Paths.get(outputPath))
      case _ => throw new IllegalArgumentException("usage: ValidateAPIs [--check] <references> <manifest>")
    }
    val manifest = Manifest(format = 1, surfaces = configurations(references).map(probe))
    val rendered = write(manifest, indent = 2) + "\n"
    if (check) {
      require(Files.isRegularFile(output), s"API validation manifest is absent: $output")
      require(
        Files.readString(output, StandardCharsets.UTF_8) == rendered,
        s"API validation manifest is stale: $output"
      )
      println(s"validated ${manifest.surfaces.map(_.functions.size).sum} official API declarations")
    } else {
      val _ = Files.createDirectories(output.getParent)
      val _ = Files.writeString(output, rendered, StandardCharsets.UTF_8)
      manifest.surfaces.foreach(surface => println(s"probed ${surface.functions.size} ${surface.name} declarations"))
    }
  }

  private def configurations(root: Path): Vector[Config] = {
    val cudaInclude = findParent(root.resolve("nvidia/cuda"), "cuda.h")
    Vector(
      Config(
        "cuda-driver",
        "c",
        Vector("cuda.h"),
        Vector(cudaInclude),
        Vector.empty,
        name => name.matches("cu[A-Z][A-Za-z0-9_]*")
      ),
      Config(
        "cuda-runtime",
        "c",
        Vector("cuda_runtime_api.h"),
        Vector(cudaInclude),
        Vector.empty,
        name => name.matches("cuda[A-Z][A-Za-z0-9_]*")
      ),
      Config(
        "hip",
        "c",
        Vector("hip/hip_runtime_api.h"),
        Vector(root.resolve("amd/hip/include")),
        Vector("__HIP_PLATFORM_AMD__=1"),
        name => name.matches("hip[A-Z][A-Za-z0-9_]*")
      ),
      Config(
        "hsa",
        "c",
        Vector("hsa.h", "hsa_ext_amd.h"),
        Vector(root.resolve("amd/hsa/runtime/hsa-runtime/inc")),
        Vector.empty,
        name => name.matches("hsa_[a-z0-9_]+")
      ),
      Config(
        "level-zero",
        "c",
        Vector("ze_api.h", "zes_api.h", "zet_api.h"),
        Vector(root.resolve("oneapi/level-zero/include")),
        Vector.empty,
        name => name.matches("(?:ze|zes|zet)[A-Z][A-Za-z0-9_]*")
      ),
      Config(
        "opencl",
        "c",
        Vector("CL/cl.h", "CL/cl_ext.h", "CL/cl_gl.h"),
        Vector(root.resolve("khronos/opencl")),
        Vector("CL_TARGET_OPENCL_VERSION=300"),
        name => name.matches("cl[A-Z][A-Za-z0-9_]*")
      )
    )
  }

  private def findParent(root: Path, name: String): Path = {
    val stream = Files.walk(root)
    try
      stream.iterator().asScala.find(path => Files.isRegularFile(path) && path.getFileName.toString == name)
        .map(_.getParent)
        .getOrElse(throw new IllegalArgumentException(s"$name is absent below $root"))
    finally stream.close()
  }

  private def probe(config: Config): Surface = {
    val command = Vector("clang", "-x", config.language, "-Xclang", "-ast-dump=json", "-fsyntax-only") ++
      Option.when(config.language == "c++")("-std=c++17") ++
      config.includeDirectories.map(directory => s"-I$directory") ++
      config.defines.map(define => s"-D$define") ++
      config.headers.flatMap(header => Vector("-include", header)) ++ Vector("/dev/null")
    val output = new StringBuilder
    val errors = new StringBuilder
    val exit   = Process(command) ! ProcessLogger(
      line => { val _ = output.append(line).append('\n') },
      line => { val _ = errors.append(line).append('\n') }
    )
    require(exit == 0, s"${config.name}: clang failed ($exit):\n$errors")
    val root = read[Node](output.result())
    validateCallbackTypedefs(config.name, root)
    val functions = collect(root).filter(function => config.acceptedName(function.name))
    val conflicts = functions.groupBy(_.name).collect {
      case (name, entries) if entries.map(_.functionType).distinct.size > 1 =>
        s"$name: ${entries.map(_.functionType).distinct.mkString(" versus ")}"
    }.toVector
    require(conflicts.isEmpty, s"${config.name}: conflicting declarations: ${conflicts.mkString("; ")}")
    Surface(config.name, functions.distinctBy(_.name).sortBy(_.name))
  }

  private def validateCallbackTypedefs(surface: String, root: Node): Unit = {
    val expected = CallbackTypedefCatalog.forSurface(surface)
    val names    = expected.map(_.name).toSet
    val actual   = collectTypedefs(root)
      .filter(value => names(value._1))
      .groupMap(_._1)(_._2)
      .view
      .mapValues(_.distinct)
      .toMap
    expected.foreach { definition =>
      val signatures = actual.getOrElse(definition.name, Vector.empty)
      require(
        signatures == Vector(definition.signature),
        s"$surface: callback typedef ${definition.name} differs: expected ${definition.signature}, found ${signatures.mkString(" or ")}"
      )
    }
  }

  private def collectTypedefs(root: Node): Vector[(String, String)] = {
    val here = root match {
      case Node(Some("TypedefDecl"), Some(name), Some(TypeInfo(underlying)), isImplicit, _)
          if !isImplicit.getOrElse(false) => Vector(name -> underlying)
      case _ => Vector.empty
    }
    here ++ root.inner.getOrElse(Vector.empty).flatMap(collectTypedefs)
  }

  private def collect(root: Node): Vector[Function] = {
    val here = root match {
      case Node(Some("FunctionDecl"), Some(name), Some(TypeInfo(functionType)), isImplicit, inner)
          if !isImplicit.getOrElse(false) =>
        val parameters = inner.getOrElse(Vector.empty).collect {
          case Node(Some("ParmVarDecl"), parameterName, Some(TypeInfo(cType)), _, _) =>
            Parameter(parameterName.getOrElse(""), cType)
        }
        Vector(Function(name, returnType(functionType), functionType, parameters))
      case _ => Vector.empty
    }
    here ++ root.inner.getOrElse(Vector.empty).flatMap(collect)
  }

  private def returnType(functionType: String): String = {
    val boundary = functionType.indexOf('(')
    require(boundary > 0, s"cannot split C function type: $functionType")
    functionType.substring(0, boundary).trim
  }
}
