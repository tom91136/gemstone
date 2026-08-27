package net.kurobako.gemstone.codegen

import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import scala.collection.mutable
import upickle.default.read

/** Materializes the pinned Clang declarations as checked-in Scala API contracts. The generated source is deliberately
  * boring: exact C names, nominal C types, and fail-closed method bodies. Hand-written API work consists of changing a
  * selected method to abstract (RPC) or giving it a `delegate[...]` body; validation derives its declaration from the
  * Scala ABI types and compares it with the normalized header manifest.
  */
object GenerateAPISurfaces {
  private final case class Target(
      manifestNames: Vector[String],
      packageName: String,
      traitName: String,
      output: Path,
      versions: Vector[(String, String)]
  )

  def main(arguments: Array[String]): Unit = {
    val (check, manifestPath, sourceRoot, versions) = arguments.toVector match {
      case Vector(manifest, root, cuda, hip, hsa, levelZero, openCL) =>
        (false, Paths.get(manifest), Paths.get(root), Vector(cuda, hip, hsa, levelZero, openCL))
      case Vector("--check", manifest, root, cuda, hip, hsa, levelZero, openCL) =>
        (true, Paths.get(manifest), Paths.get(root), Vector(cuda, hip, hsa, levelZero, openCL))
      case _ =>
        throw new IllegalArgumentException(
          "usage: GenerateAPISurfaces [--check] <manifest> <api-source-root> <cuda> <hip> <hsa> <level-zero> <opencl>"
        )
    }
    val manifest = read[ValidateAPIs.Manifest](Files.readString(manifestPath, StandardCharsets.UTF_8))
    val targets  = Vector(
      Target(
        Vector("cuda-driver", "cuda-runtime"),
        "net.kurobako.gemstone.cuda",
        "CUDA",
        sourceRoot.resolve("net/kurobako/gemstone/cuda/CUDA.scala"),
        Vector("HeaderVersion" -> versions(0))
      ),
      Target(
        Vector("hip"),
        "net.kurobako.gemstone.hip",
        "HIP",
        sourceRoot.resolve("net/kurobako/gemstone/hip/HIP.scala"),
        Vector("HeaderVersion" -> versions(1))
      ),
      Target(
        Vector("hsa"),
        "net.kurobako.gemstone.hsa",
        "HSA",
        sourceRoot.resolve("net/kurobako/gemstone/hsa/HSA.scala"),
        Vector("HeaderVersion" -> versions(2))
      ),
      Target(
        Vector("level-zero"),
        "net.kurobako.gemstone.levelzero",
        "LevelZero",
        sourceRoot.resolve("net/kurobako/gemstone/levelzero/LevelZero.scala"),
        Vector("HeaderVersion" -> versions(3))
      ),
      Target(
        Vector("opencl"),
        "net.kurobako.gemstone.opencl",
        "OpenCL",
        sourceRoot.resolve("net/kurobako/gemstone/opencl/OpenCL.scala"),
        Vector("HeaderVersion" -> versions(4))
      )
    )
    val byName = manifest.surfaces.map(surface => surface.name -> surface).toMap
    targets.foreach { target =>
      val functions = target.manifestNames.flatMap(name => byName.getOrElse(name, missing(name)).functions)
      val content   = render(target, functions)
      if (check) {
        require(Files.isRegularFile(target.output), s"API surface is absent: ${target.output}")
        require(
          Files.readString(target.output, StandardCharsets.UTF_8) == content,
          s"API surface is stale: ${target.output}"
        )
        println(s"validated ${functions.size} ${target.traitName} declarations")
      } else {
        val _ = Files.createDirectories(target.output.getParent)
        val _ = Files.writeString(target.output, content, StandardCharsets.UTF_8)
        println(s"generated ${functions.size} ${target.traitName} declarations at ${target.output}")
      }
    }
  }

  private def missing(name: String): Nothing = throw new IllegalArgumentException(s"manifest surface is absent: $name")

  private def render(target: Target, functions: Vector[ValidateAPIs.Function]): String = {
    val aliases                          = mutable.LinkedHashMap.empty[String, String]
    def scalaType(cType: String): String = mapType(cType.trim, aliases, target.traitName)
    val declarations                     = functions.map { function =>
      val used       = mutable.Set.empty[String]
      val parameters = function.parameters.zipWithIndex.map { case (parameter, index) =>
        val base = identifier(Option(parameter.name).filter(_.nonEmpty).getOrElse(s"arg$index"))
        val name = Iterator.from(1).map(i => if (i == 1) base else s"${base}_$i").find(used.add).get
        s"$name: ${scalaType(parameter.cType)}"
      }
      s"  def ${identifier(function.name)}(${parameters.mkString(", ")}): ${scalaType(function.returnType)} = unsupported"
    }
    val aliasLines = aliases.toVector.sortBy(_._1).map { case (name, definition) =>
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
        |  val SurfaceSize: Int = ${functions.size}
        |}
        |""".stripMargin
  }

  private def mapType(raw: String, aliases: mutable.LinkedHashMap[String, String], api: String): String = {
    val value = raw.replaceAll("\\s+", " ").trim
    functionPointer(value) match {
      case Some((result, parameters)) =>
        val arguments = callbackArguments(parameters.map(mapType(_, aliases, api)))
        s"Callback[$arguments, ${mapType(result, aliases, api)}]"
      case None if value.endsWith("*") =>
        val pointee0 = value.dropRight(1).trim
        val readOnly = pointee0.startsWith("const ") || pointee0.endsWith(" const")
        val pointee  = pointee0.stripPrefix("const ").stripSuffix(" const").trim
        if (readOnly && pointee == "char") "CString"
        else {
          val access  = if (readOnly) "Read" else "ReadWrite"
          val element = if (pointee == "void") "CVoid" else mapType(pointee, aliases, api)
          s"Pointer[$element, $access, Opaque]"
        }
      case None =>
        CallbackTypedefCatalog.find(api, value) match {
          case Some(signature) =>
            val (result, parameters) = functionPointer(signature)
              .getOrElse(throw new IllegalStateException(s"invalid callback typedef signature: $value = $signature"))
            val arguments = callbackArguments(parameters.map(mapType(_, aliases, api)))
            aliases(value) = s"CallbackTypedef[${literal(value)}, $arguments, ${mapType(result, aliases, api)}]"
            identifier(value)
          case None =>
            fixedArray(value) match {
              case Some((element, count)) => s"CArray[${mapType(element, aliases, api)}, $count]"
              case None                   => mapValue(value, aliases, api)
            }
        }
    }
  }

  private def callbackArguments(parameters: Vector[String]): String = parameters match {
    case Vector()      => "EmptyTuple"
    case Vector(value) => s"Tuple1[$value]"
    case values        => values.mkString("(", ", ", ")")
  }

  private def mapValue(raw: String, aliases: mutable.LinkedHashMap[String, String], api: String): String =
    (api, raw) match {
      case ("CUDA", "CUresult" | "cudaError_t")                             => "CUDAStatus"
      case ("CUDA", "CUdevice")                                             => "Device"
      case ("CUDA", "CUdeviceptr")                                          => "DevicePtr"
      case ("CUDA", "CUcontext")                                            => "Context"
      case ("CUDA", "CUmodule")                                             => "CUDAModule"
      case ("CUDA", "CUfunction")                                           => "CUDAFunction"
      case ("CUDA", "size_t")                                               => "SizeT"
      case ("CUDA", "unsigned int")                                         => "UInt32"
      case ("CUDA", "unsigned char")                                        => "UInt8"
      case (_, "void")                                                      => "Unit"
      case (_, "bool")                                                      => "Boolean"
      case (_, "char")                                                      => "Byte"
      case (_, "signed char")                                               => "Byte"
      case (_, "short" | "short int" | "signed short" | "signed short int") => "Short"
      case (_, "int" | "signed" | "signed int")                             => "Int"
      case (_, "float")                                                     => "Float"
      case (_, "double")                                                    => "Double"
      case (_, other)                                                       =>
        val name = identifierName(other)
        aliases.get(name) match {
          case Some(_) => ()
          case None    => aliases(name) = s"CValue[${literal(other)}]"
        }
        identifier(name)
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
    val result = Vector.newBuilder[String]
    var depth  = 0
    var start  = 0
    value.indices.foreach { index =>
      value(index) match {
        case '(' | '['         => depth += 1
        case ')' | ']'         => depth -= 1
        case ',' if depth == 0 =>
          result += value.substring(start, index).trim
          start = index + 1
        case _ => ()
      }
    }
    val finalValue = value.substring(start).trim
    if (finalValue.nonEmpty) result += finalValue
    result.result()
  }

  private def fixedArray(value: String): Option[(String, Int)] = {
    val pattern = raw"(.+)\[([0-9]+)\]".r
    value match {
      case pattern(element, count) => Some(element.trim -> count.toInt)
      case _                       => None
    }
  }

  private def identifierName(cType: String): String = {
    val cleaned = cType.replaceAll("[^A-Za-z0-9_]", "_").replaceAll("_+", "_").stripSuffix("_")
    if (cleaned.headOption.exists(_.isDigit)) s"C_$cleaned" else cleaned
  }

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
