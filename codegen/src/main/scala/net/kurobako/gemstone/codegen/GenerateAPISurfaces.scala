package net.kurobako.gemstone.codegen

import net.kurobako.gemstone.abi.ABIDerivation
import net.kurobako.gemstone.cuda.CUDACustomSurface

import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import cats.data.State
import cats.syntax.all.*
import upickle.default.read

/** Materializes the pinned Clang declarations as checked-in Scala API surface bases. The generated source is
  * deliberately boring: exact C names, nominal C types, and fail-closed method bodies. Hand-written API traits refine
  * selected methods as RPC endpoints or native delegates without editing this output.
  */
object GenerateAPISurfaces {
  private final case class Aliases(entries: Vector[(String, String)]) {
    def get(name: String): Option[String] = entries.collectFirst { case (`name`, definition) => definition }
    def put(name: String, definition: String): Aliases =
      if (get(name).nonEmpty) this else copy(entries = entries :+ (name -> definition))
  }

  private object Aliases {
    val empty: Aliases = Aliases(Vector.empty)
  }

  private type Render[A] = State[Aliases, A]

  private final case class Target(
      manifestNames: Vector[String],
      packageName: String,
      traitName: String,
      output: Path,
      versions: Vector[(String, String)],
      excludedFunctions: Set[String] = Set.empty,
      excludedTypes: Set[String] = Set.empty
  )

  def main(arguments: Array[String]): Unit = {
    val (check, manifestPath, sourceRoot, versions) = arguments.toVector match {
      case Vector(manifest, root, cuda, hip, hsa, levelZero, openCL) =>
        (false, Paths.get(manifest), Paths.get(root), Vector(cuda, hip, hsa, levelZero, openCL))
      case Vector("--check", manifest, root, cuda, hip, hsa, levelZero, openCL) =>
        (true, Paths.get(manifest), Paths.get(root), Vector(cuda, hip, hsa, levelZero, openCL))
      case _ =>
        throw IllegalArgumentException(
          "usage: GenerateAPISurfaces [--check] <manifest> <api-source-root> <cuda> <hip> <hsa> <level-zero> <opencl>"
        )
    }
    val manifest = read[GenerateOfficialAPIManifest.Manifest](Files.readString(manifestPath, StandardCharsets.UTF_8))
    val cudaCustomFunctions = ABIDerivation.methods[CUDACustomSurface].iterator.map(_.name).toSet
    val targets             = Vector(
      Target(
        Vector("cuda-driver", "cuda-runtime"),
        "net.kurobako.gemstone.cuda",
        "CUDASurface",
        sourceRoot.resolve("net/kurobako/gemstone/cuda/CUDASurface.scala"),
        Vector("HeaderVersion" -> versions(0)),
        cudaCustomFunctions,
        Set("CUctxCreateParams", "CUhostFn", "CUstreamCallback", "CUuuid", "cuuint64_t")
      ),
      Target(
        Vector("hip"),
        "net.kurobako.gemstone.hip",
        "HIPSurface",
        sourceRoot.resolve("net/kurobako/gemstone/hip/HIPSurface.scala"),
        Vector("HeaderVersion" -> versions(1))
      ),
      Target(
        Vector("hsa"),
        "net.kurobako.gemstone.hsa",
        "HSASurface",
        sourceRoot.resolve("net/kurobako/gemstone/hsa/HSASurface.scala"),
        Vector("HeaderVersion" -> versions(2))
      ),
      Target(
        Vector("level-zero"),
        "net.kurobako.gemstone.levelzero",
        "LevelZeroSurface",
        sourceRoot.resolve("net/kurobako/gemstone/levelzero/LevelZeroSurface.scala"),
        Vector("HeaderVersion" -> versions(3))
      ),
      Target(
        Vector("opencl"),
        "net.kurobako.gemstone.opencl",
        "OpenCLSurface",
        sourceRoot.resolve("net/kurobako/gemstone/opencl/OpenCLSurface.scala"),
        Vector("HeaderVersion" -> versions(4))
      )
    )
    val byName = manifest.surfaces.map(surface => surface.name -> surface).toMap
    targets.foreach { target =>
      val functions = target.manifestNames.flatMap(name => byName.getOrElse(name, missing(name)).functions)
      val content   = GeneratedScalaFormatter(
        target.output,
        render(target, functions.filterNot(function => target.excludedFunctions(function.name)), functions.size)
      )
      if (check) {
        require(Files.isRegularFile(target.output), s"API surface is absent: ${target.output}")
        require(
          Files.readString(target.output, StandardCharsets.UTF_8) == content,
          s"API surface is stale: ${target.output}"
        )
        println(s"validated ${functions.size} ${target.traitName} declarations")
      } else {
        val _ = Files.createDirectories(target.output.getParent)
        val _ = GeneratedFiles.write(target.output, content)
        println(s"generated ${functions.size} ${target.traitName} declarations at ${target.output}")
      }
    }
  }

  private def missing(name: String): Nothing = throw IllegalArgumentException(s"manifest surface is absent: $name")

  private def render(
      target: Target,
      functions: Vector[GenerateOfficialAPIManifest.Function],
      surfaceSize: Int
  ): String = {
    val api      = target.traitName.stripSuffix("Surface")
    val rendered = functions.traverse { function =>
      val (_, namedParameters) = function.parameters.zipWithIndex.foldLeft(
        (Set.empty[String], Vector.empty[(String, String)])
      ) { case ((used, result), (parameter, index)) =>
        val base = identifier(Option(parameter.name).filter(_.nonEmpty).getOrElse(s"arg$index"))
        val name = Iterator.from(1).map(i => if (i == 1) base else s"${base}_$i").find(!used(_)).get
        (used + name, result :+ (name -> parameter.cType))
      }
      for {
        parameters <- namedParameters.traverse { case (name, cType) =>
          mapType(cType.trim, api).map(scalaType => s"$name: $scalaType")
        }
        returnType <- mapType(function.returnType.trim, api)
      } yield s"  def ${identifier(function.name)}(${parameters.mkString(", ")}): $returnType = unsupported"
    }
    val (aliases, declarations) = rendered.run(Aliases.empty).value
    val aliasLines              = aliases.entries.filterNot(entry => target.excludedTypes(entry._1)).sortBy(_._1).map {
      case (name, definition) =>
        s"type ${identifier(name)} = $definition"
    }
    val versionLines = target.versions.map { case (name, value) => s"  val $name: String = ${literal(value)}" }
    s"""|// Generated from pinned official headers by GenerateAPISurfaces. Do not edit mechanically.
        |package ${target.packageName}
        |
        |import net.kurobako.gemstone.abi.*
        |
        |${aliasLines.mkString("\n")}
        |
        |trait ${target.traitName} {
        |${declarations.mkString("\n")}
        |}
        |
        |object ${target.traitName} {
        |${versionLines.mkString("\n")}
        |  val SurfaceSize: Int = $surfaceSize
        |}
        |""".stripMargin
  }

  private def mapType(raw: String, api: String): Render[String] = {
    val value = raw.replaceAll("\\s+", " ").trim
    functionPointer(value) match {
      case Some((result, parameters)) =>
        for {
          parameterTypes <- parameters.traverse(mapType(_, api))
          resultType     <- mapType(result, api)
        } yield s"Callback[${callbackArguments(parameterTypes)}, $resultType]"
      case None if value.endsWith("*") =>
        val pointee0 = value.dropRight(1).trim
        val readOnly = pointee0.startsWith("const ") || pointee0.endsWith(" const")
        val pointee  = pointee0.stripPrefix("const ").stripSuffix(" const").trim
        if (readOnly && pointee == "char") State.pure("CString")
        else if (!readOnly && pointee == "void") State.pure("OpaquePointer")
        else {
          val access  = if (readOnly) "Read" else "ReadWrite"
          val element = if (pointee == "void") State.pure[Aliases, String]("CVoid") else mapType(pointee, api)
          element.map(value => s"Pointer[$value, $access, Opaque]")
        }
      case None =>
        CallbackTypedefCatalog.find(api, value) match {
          case Some(signature) =>
            val (result, parameters) = functionPointer(signature)
              .getOrElse(throw IllegalStateException(s"invalid callback typedef signature: $value = $signature"))
            for {
              parameterTypes <- parameters.traverse(mapType(_, api))
              resultType     <- mapType(result, api)
              definition = s"CallbackTypedef[${literal(value)}, ${callbackArguments(parameterTypes)}, $resultType]"
              _ <- State.modify[Aliases](_.put(value, definition))
            } yield typeIdentifier(value)
          case None =>
            fixedArray(value) match {
              case Some((element, count)) => mapType(element, api).map(value => s"CArray[$value, $count]")
              case None                   => mapValue(value, api)
            }
        }
    }
  }

  private def callbackArguments(parameters: Vector[String]): String = parameters match {
    case Vector()      => "EmptyTuple"
    case Vector(value) => s"Tuple1[$value]"
    case values        => values.mkString("(", ", ", ")")
  }

  private def mapValue(raw: String, api: String): Render[String] =
    (api, raw) match {
      case ("CUDA", "CUresult" | "cudaError_t")                             => State.pure("CUDAStatus")
      case ("CUDA", "CUdevice")                                             => State.pure("Device")
      case ("CUDA", "CUdeviceptr")                                          => State.pure("DevicePtr")
      case ("CUDA", "CUcontext")                                            => State.pure("Context")
      case ("CUDA", "CUmodule")                                             => State.pure("CUDAModule")
      case ("CUDA", "CUfunction")                                           => State.pure("CUDAFunction")
      case ("CUDA", "size_t")                                               => State.pure("SizeT")
      case ("CUDA", "unsigned int")                                         => State.pure("UInt32")
      case ("CUDA", "unsigned char")                                        => State.pure("UInt8")
      case (_, "void")                                                      => State.pure("Unit")
      case (_, "bool")                                                      => State.pure("Boolean")
      case (_, "char")                                                      => State.pure("Byte")
      case (_, "signed char")                                               => State.pure("Byte")
      case (_, "short" | "short int" | "signed short" | "signed short int") => State.pure("Short")
      case (_, "int" | "signed" | "signed int")                             => State.pure("Int")
      case (_, "float")                                                     => State.pure("Float")
      case (_, "double")                                                    => State.pure("Double")
      case (_, other)                                                       =>
        val name = identifierName(other)
        State.modify[Aliases](_.put(name, s"CValue[${literal(other)}]")).as(identifier(name))
    }

  private def functionPointer(value: String): Option[(String, Vector[String])] = {
    val pattern = raw"(?s)^(.+?)\(\s*\*[^)]*\)\s*\((.*)\)$$".r
    value match {
      case pattern(result, rawParameters) =>
        val parameters = splitParameters(rawParameters.trim).filterNot(_ == "void")
        Some(result.trim -> parameters)
      case _ => None
    }
  }

  private def splitParameters(value: String): Vector[String] = {
    val (start, _, result) = value.indices.foldLeft((0, 0, Vector.empty[String])) {
      case ((start, depth, result), index) =>
        value(index) match {
          case '(' | '['         => (start, depth + 1, result)
          case ')' | ']'         => (start, depth - 1, result)
          case ',' if depth == 0 => (index + 1, depth, result :+ value.substring(start, index).trim)
          case _                 => (start, depth, result)
        }
    }
    val finalValue = value.substring(start).trim
    if (finalValue.nonEmpty) result :+ finalValue else result
  }

  private def fixedArray(value: String): Option[(String, Int)] =
    value match {
      case s"$element[$count]" if element.nonEmpty && count.nonEmpty && count.forall(_.isDigit) =>
        Some(element.trim -> count.toInt)
      case _ => None
    }

  private def identifierName(cType: String): String = {
    val cleaned = cType.replaceAll("[^A-Za-z0-9_]", "_").replaceAll("_+", "_").stripSuffix("_")
    if (cleaned.headOption.exists(_.isDigit)) s"C_$cleaned" else cleaned
  }

  private def typeIdentifier(value: String): String = identifier(identifierName(value))

  private val keywords = Set(
    "abstract",
    "case",
    "catch",
    "class",
    "def",
    "do",
    "else",
    "enum",
    "export",
    "extends",
    "false",
    "final",
    "finally",
    "for",
    "forSome",
    "given",
    "if",
    "implicit",
    "import",
    "inline",
    "lazy",
    "match",
    "new",
    "null",
    "object",
    "opaque",
    "open",
    "override",
    "package",
    "private",
    "protected",
    "return",
    "sealed",
    "super",
    "then",
    "this",
    "throw",
    "trait",
    "transparent",
    "true",
    "try",
    "type",
    "using",
    "val",
    "var",
    "while",
    "with",
    "yield"
  )

  private def identifier(value: String): String = if (keywords(value)) s"`$value`" else value

  private def literal(value: String): String =
    "\"" + value.flatMap {
      case '\\' => "\\\\"
      case '\"' => "\\\""
      case '\n' => "\\n"
      case '\r' => "\\r"
      case '\t' => "\\t"
      case c    => c.toString
    } + "\""
}
