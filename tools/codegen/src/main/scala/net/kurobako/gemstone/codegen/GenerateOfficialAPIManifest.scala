package net.kurobako.gemstone.codegen

import java.nio.file.Files
import java.nio.file.Path
import cats.syntax.all.*
import com.monovore.decline.Command
import com.monovore.decline.Opts
import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*
import net.kurobako.gemstone.NoNul
import scala.jdk.CollectionConverters.*
import scala.sys.process.Process
import scala.util.Using
import upickle.default.ReadWriter
import upickle.default.read
import upickle.default.readwriter
import upickle.default.write

object GenerateOfficialAPIManifest {
  type CIdentifier = CIdentifier.T
  object CIdentifier extends RefinedType[String, MinLength[1] & NoNul] {
    given ReadWriter[CIdentifier] = readwriter[String].bimap(
      _.value,
      value => either(value).fold(message => throw IllegalArgumentException(message), valid => valid)
    )
    extension (identifier: CIdentifier) def text: String = identifier.value
  }

  type CType = CType.T
  object CType extends RefinedType[String, MinLength[1] & NoNul] {
    given ReadWriter[CType] = readwriter[String].bimap(
      _.value,
      value => either(value).fold(message => throw IllegalArgumentException(message), valid => valid)
    )
    extension (cType: CType) def text: String = cType.value
  }

  type EnumValue = EnumValue.T
  object EnumValue extends RefinedType[String, MinLength[1] & NoNul] {
    given ReadWriter[EnumValue] = readwriter[String].bimap(
      _.value,
      value => either(value).fold(message => throw IllegalArgumentException(message), valid => valid)
    )
    extension (enumValue: EnumValue) def text: String = enumValue.value
  }

  type SurfaceName = SurfaceName.T
  object SurfaceName extends RefinedType[String, MinLength[1] & NoNul] {
    given ReadWriter[SurfaceName] = readwriter[String].bimap(
      _.value,
      value => either(value).fold(message => throw IllegalArgumentException(message), valid => valid)
    )
    extension (surfaceName: SurfaceName) def text: String = surfaceName.value
  }

  private final case class Request(clang: String, references: Path, output: Path)
  final case class Parameter(name: Option[CIdentifier], cType: CType) derives ReadWriter
  final case class Function(
      name: CIdentifier,
      returnType: CType,
      functionType: CType,
      parameters: Vector[Parameter]
  ) derives ReadWriter
  final case class CallbackTypedef(name: CIdentifier, cType: CType) derives ReadWriter
  final case class EnumConstant(name: CIdentifier, value: EnumValue) derives ReadWriter
  final case class DispatchField(name: CIdentifier, cType: CType) derives ReadWriter
  final case class DispatchTable(name: CIdentifier, fields: Vector[DispatchField]) derives ReadWriter
  final case class Surface(
      name: SurfaceName,
      functions: Vector[Function],
      callbackTypedefs: Vector[CallbackTypedef],
      enumConstants: Vector[EnumConstant],
      dispatchTables: Vector[DispatchTable]
  ) derives ReadWriter
  final case class Manifest(format: Int, surfaces: Vector[Surface]) derives ReadWriter

  private final case class TypeInfo(qualType: String) derives ReadWriter
  private final case class Node(
      kind: Option[String] = None,
      name: Option[String] = None,
      `type`: Option[TypeInfo] = None,
      isImplicit: Option[Boolean] = None,
      storageClass: Option[String] = None,
      value: Option[String] = None,
      inner: Option[Vector[Node]] = None
  ) derives ReadWriter

  private final case class Config(
      name: String,
      language: String,
      headers: Vector[String],
      includeDirectories: Vector[Path],
      defines: Vector[String],
      acceptedName: String => Boolean,
      acceptedEnumConstant: String => Boolean,
      dispatchTables: Vector[String] = Vector.empty,
      acceptedDispatchTable: String => Boolean = _ => false
  )

  private val command = Command("GenerateOfficialAPIManifest", "Generate the official API manifest")((
    Opts.option[String]("clang", help = "Clang executable").withDefault("clang"),
    Opts.argument[String]("references").map(Path.of(_)),
    Opts.argument[String]("manifest").map(Path.of(_))
  ).mapN(Request(_, _, _)))

  def main(arguments: Array[String]): Unit = CodegenCommand.run(command, arguments)(generate(_))

  private def generate(request: Request): Unit = {
    val manifest = Manifest(
      format = 4,
      surfaces = configurations(request.references).map(config => probe(config, request.clang))
    )
    val rendered = write(manifest, indent = 2) + "\n"
    val _        = Files.createDirectories(request.output.getParent)
    val _        = GeneratedFiles.write(request.output, rendered)
    manifest.surfaces.foreach(surface =>
      Console.out.println(s"probed ${surface.functions.size} ${surface.name} declarations")
    )
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
        Identifiers.isUpperCamelAfter(_, "cu"),
        name => name.startsWith("CU_") || name.startsWith("CUDA_")
      ),
      Config(
        "cuda-runtime",
        "c",
        Vector("cuda_runtime_api.h"),
        Vector(cudaInclude),
        Vector.empty,
        Identifiers.isUpperCamelAfter(_, "cuda"),
        _.startsWith("cuda")
      ),
      Config(
        "cuda-cublas",
        "c",
        Vector("cublas_api.h"),
        Vector(cudaInclude),
        Vector.empty,
        _ => false,
        _.startsWith("CUBLAS_")
      ),
      Config(
        "hip",
        "c",
        Vector("hip/hip_runtime_api.h"),
        Vector(root.resolve("amd/hip/include")),
        Vector("__HIP_PLATFORM_AMD__=1"),
        Identifiers.isUpperCamelAfter(_, "hip"),
        _.startsWith("hip")
      ),
      Config(
        "hsa",
        "c",
        Vector("hsa.h", "hsa_ext_amd.h"),
        Vector(root.resolve("amd/hsa/runtime/hsa-runtime/inc")),
        Vector.empty,
        Identifiers.isLowerSnake(_, "hsa_"),
        _.startsWith("HSA_")
      ),
      Config(
        "hsakmt",
        "c",
        Vector("hsakmt/hsakmt.h"),
        Vector(root.resolve("amd/hsa/libhsakmt/include")),
        Vector.empty,
        Identifiers.isUpperCamelAfter(_, "hsaKmt"),
        _.startsWith("HSA")
      ),
      Config(
        "level-zero",
        "c",
        Vector("ze_api.h", "zes_api.h", "zet_api.h"),
        Vector(root.resolve("oneapi/level-zero/include")),
        Vector.empty,
        name => Vector("ze", "zes", "zet").exists(Identifiers.isUpperCamelAfter(name, _)),
        name => Vector("ZE_", "ZES_", "ZET_").exists(name.startsWith(_))
      ),
      Config(
        "level-zero-ddi",
        "c",
        Vector("ze_ddi.h", "zes_ddi.h", "zet_ddi.h"),
        Vector(root.resolve("oneapi/level-zero/include")),
        Vector.empty,
        name =>
          name.endsWith("ProcAddrTable") && Vector("zeGet", "zesGet", "zetGet").exists(name.startsWith(_)),
        name => Vector("ZE_", "ZES_", "ZET_").exists(name.startsWith(_)),
        acceptedDispatchTable = name =>
          name.endsWith("_dditable_t") && Vector("_ze_", "_zes_", "_zet_").exists(name.startsWith(_))
      ),
      Config(
        "opencl",
        "c",
        Vector("CL/cl_icd.h", "CL/cl_ext.h", "CL/cl_gl.h"),
        Vector(root.resolve("khronos/opencl")),
        Vector(
          "CL_TARGET_OPENCL_VERSION=300",
          "CL_USE_DEPRECATED_OPENCL_1_0_APIS=1",
          "CL_USE_DEPRECATED_OPENCL_1_1_APIS=1",
          "CL_USE_DEPRECATED_OPENCL_1_2_APIS=1",
          "CL_USE_DEPRECATED_OPENCL_2_0_APIS=1",
          "CL_USE_DEPRECATED_OPENCL_2_1_APIS=1",
          "CL_USE_DEPRECATED_OPENCL_2_2_APIS=1"
        ),
        Identifiers.isUpperCamelAfter(_, "cl"),
        _.startsWith("CL_"),
        Vector("_cl_icd_dispatch")
      ),
      Config(
        "vulkan",
        "c",
        Vector("vulkan/vulkan.h"),
        Vector(root.resolve("khronos/vulkan/include")),
        Vector.empty,
        Identifiers.isUpperCamelAfter(_, "vk"),
        _.startsWith("VK_")
      )
    )
  }

  private def findParent(root: Path, name: String): Path =
    Using.resource(Files.walk(root)) { stream =>
      stream.toList.asScala.view.find(path => Files.isRegularFile(path) && path.getFileName.toString == name)
        .map(_.getParent)
        .getOrElse(throw IllegalArgumentException(s"$name is absent below $root"))
    }

  private def probe(config: Config, clang: String): Surface = {
    val command = Vector(
      clang,
      "-x",
      config.language,
      "-Xclang",
      "-ast-dump=json",
      "-fsyntax-only",
      "-Wno-visibility"
    ) ++
      Option.when(config.language == "c++")("-std=c++17") ++
      config.includeDirectories.map(directory => s"-I$directory") ++
      config.defines.map(define => s"-D$define") ++
      config.headers.flatMap(header => Vector("-include", header)) ++ Vector("-")
    val output    = Process(command).#<(java.io.ByteArrayInputStream(Array.emptyByteArray)).!!
    val root      = read[Node](output)
    val functions = collect(root).filter(function => config.acceptedName(function.name.value))
    val conflicts = functions.groupBy(_.name).collect {
      case (name, entries) if entries.map(_.functionType).distinct.size > 1 =>
        s"${name.value}: ${entries.map(_.functionType.value).distinct.mkString(" versus ")}"
    }.toVector
    if (conflicts.nonEmpty)
      throw IllegalStateException(s"${config.name}: conflicting declarations: ${conflicts.mkString("; ")}")
    val allRecords          = collectRecords(root)
    val namedDispatchTables = config.dispatchTables.map { name =>
      val records = allRecords.filter(_.name.value == name)
      if (records.size != 1)
        throw IllegalStateException(s"${config.name}: expected one complete $name record, found ${records.size}")
      records.head
    }
    val selectedDispatchTables = allRecords.filter(table => config.acceptedDispatchTable(table.name.value))
    val dispatchTables         = (namedDispatchTables ++ selectedDispatchTables).distinctBy(_.name).sortBy(_.name.value)
    val callbackTypedefs       = collectTypedefs(root).collect {
      case (name, cType) if cType.value.contains("(*") => CallbackTypedef(name, cType)
    }.distinct.sortBy(_.name.value)
    val enumConstants = collectEnumConstants(root).filter(constant => config.acceptedEnumConstant(constant.name.value))
    Surface(
      surfaceName(config.name),
      functions.distinctBy(_.name).sortBy(_.name.value),
      callbackTypedefs,
      enumConstants,
      dispatchTables
    )
  }

  private def collectEnumConstants(root: Node): Vector[EnumConstant] = {
    val here = root match {
      case Node(Some("EnumDecl"), _, _, isImplicit, _, _, inner) if !isImplicit.getOrElse(false) =>
        inner.toVector.flatten
          .collect { case constant @ Node(Some("EnumConstantDecl"), Some(name), _, _, _, _, _) => name -> constant }
          .foldLeft(BigInt(-1) -> Vector.empty[EnumConstant]) { case ((previous, constants), (name, constant)) =>
            val explicit = constant.inner.toVector.flatten.flatMap(findValue(_)).headOption.map(BigInt(_))
            val value    = explicit.getOrElse(previous + 1)
            value -> (constants :+ EnumConstant(identifier(name, "enum constant"), enumValue(value.toString)))
          }
          ._2
      case _ => Vector.empty
    }
    here ++ root.inner.getOrElse(Vector.empty).flatMap(collectEnumConstants(_))
  }

  private def findValue(node: Node): Vector[String] =
    node.value.toVector ++ node.inner.getOrElse(Vector.empty).flatMap(findValue(_))

  private def collectRecords(root: Node): Vector[DispatchTable] = {
    val here = root match {
      case Node(Some("RecordDecl"), Some(name), _, isImplicit, _, _, inner)
          if !isImplicit.getOrElse(false) && inner.exists(_.nonEmpty) =>
        val fields = inner.toVector.flatten.flatMap(collectFields(_))
          .filter(field => field.cType.value.endsWith("_t *"))
          .distinctBy(_.name)
        Vector(DispatchTable(identifier(name, "dispatch table"), fields))
      case _ => Vector.empty
    }
    here ++ root.inner.getOrElse(Vector.empty).flatMap(collectRecords(_))
  }

  private def collectFields(root: Node): Vector[DispatchField] = {
    val here = root match {
      case Node(Some("FieldDecl"), Some(name), Some(TypeInfo(cType)), isImplicit, _, _, _)
          if !isImplicit.getOrElse(false) =>
        Vector(DispatchField(identifier(name, "dispatch field"), cTypeValue(cType, "dispatch field type")))
      case _ => Vector.empty
    }
    here ++ root.inner.getOrElse(Vector.empty).flatMap(collectFields(_))
  }

  private def collectTypedefs(root: Node): Vector[(CIdentifier, CType)] = {
    val here = root match {
      case Node(Some("TypedefDecl"), Some(name), Some(TypeInfo(underlying)), isImplicit, _, _, _)
          if !isImplicit.getOrElse(false) =>
        Vector(identifier(name, "callback typedef") -> cTypeValue(underlying, "callback typedef type"))
      case _ => Vector.empty
    }
    here ++ root.inner.getOrElse(Vector.empty).flatMap(collectTypedefs(_))
  }

  private def collect(root: Node): Vector[Function] = {
    val here = root match {
      case Node(Some("FunctionDecl"), Some(name), Some(TypeInfo(functionType)), isImplicit, storageClass, _, inner)
          if !isImplicit.getOrElse(false) && !storageClass.contains("static") =>
        val parameters = inner.getOrElse(Vector.empty).collect {
          case Node(Some("ParmVarDecl"), parameterName, Some(TypeInfo(cType)), _, _, _, _) =>
            Parameter(
              parameterName.filter(_.nonEmpty).map(identifier(_, "parameter")),
              cTypeValue(cType, "parameter type")
            )
        }
        Vector(
          Function(
            identifier(name, "function"),
            returnType(functionType),
            cTypeValue(functionType, "function type"),
            parameters
          )
        )
      case _ => Vector.empty
    }
    here ++ root.inner.getOrElse(Vector.empty).flatMap(collect(_))
  }

  private def returnType(functionType: String): CType = {
    val boundary = functionType.indexOf('(')
    if (boundary <= 0) throw IllegalArgumentException(s"cannot split C function type: $functionType")
    cTypeValue(functionType.substring(0, boundary).trim, "function return type")
  }

  private def identifier(value: String, description: String): CIdentifier =
    CIdentifier.either(value).fold(
      message => throw IllegalArgumentException(s"invalid $description '$value': $message"),
      valid => valid
    )

  private def cTypeValue(value: String, description: String): CType =
    CType.either(value).fold(
      message => throw IllegalArgumentException(s"invalid $description '$value': $message"),
      valid => valid
    )

  private def enumValue(value: String): EnumValue =
    EnumValue.either(value).fold(
      message => throw IllegalArgumentException(s"invalid enum value '$value': $message"),
      valid => valid
    )

  private def surfaceName(value: String): SurfaceName =
    SurfaceName.either(value).fold(
      message => throw IllegalArgumentException(s"invalid surface name '$value': $message"),
      valid => valid
    )
}
