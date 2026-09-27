package net.kurobako.gemstone.codegen

import net.kurobako.gemstone.Constraint.Positive

import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import cats.data.State
import cats.syntax.all.*
import com.monovore.decline.Command
import com.monovore.decline.Opts
import net.kurobako.gemstone.Constraint.NonEmptyString
import upickle.default.read

/** Materializes the pinned Clang declarations as checked-in Scala API traits. The generated source is deliberately
  * boring: exact C names, nominal C types, and fail-closed method bodies.
  */
object GenerateAPISurfaces {
  private case class Request(
      manifest: Path,
      sourceRoot: Path,
      cuda: String,
      hip: String,
      hsa: String,
      levelZero: String,
      openCL: String,
      vulkan: String
  )
  private case class Aliases(entries: Vector[(String, String)]) {
    def get(name: String): Option[String] = entries.collectFirst { case (`name`, definition) => definition }
    def put(name: String, definition: String): Aliases =
      if (get(name).nonEmpty) this else copy(entries = entries :+ (name -> definition))
  }

  private object Aliases {
    val empty: Aliases = Aliases(Vector.empty)
  }

  private type Render[A] = State[Aliases, A]

  private case class Target(
      manifestNames: Vector[String],
      packageName: String,
      traitName: String,
      output: Path,
      versions: Vector[(String, NonEmptyString)]
  )

  private val command = Command("GenerateAPISurfaces", "Generate Scala API surfaces")((
    Opts.argument[String]("manifest").map(Path.of(_)),
    Opts.argument[String]("api-source-root").map(Path.of(_)),
    Opts.argument[String]("cuda-version"),
    Opts.argument[String]("hip-version"),
    Opts.argument[String]("hsa-version"),
    Opts.argument[String]("level-zero-version"),
    Opts.argument[String]("opencl-version"),
    Opts.argument[String]("vulkan-version")
  ).mapN(Request(_, _, _, _, _, _, _, _)))

  def main(arguments: Array[String]): Unit = CodegenCommand.run(command, arguments)(generate(_))

  private def generate(request: Request): Unit = {
    val manifest = read[GenerateOfficialAPIManifest.Manifest](
      Files.readString(request.manifest, StandardCharsets.UTF_8)
    )
    val versions =
      Vector(request.cuda, request.hip, request.hsa, request.levelZero, request.openCL, request.vulkan).map { version =>
        NonEmptyString.either(version).fold(
          message => throw IllegalArgumentException(s"invalid header version: $message"),
          value => value
        )
      }
    val targets = Vector(
      Target(
        Vector("cuda-driver", "cuda-runtime"),
        "net.kurobako.gemstone.cuda",
        "CUDA",
        request.sourceRoot.resolve("net/kurobako/gemstone/cuda/CUDA.scala"),
        Vector("HeaderVersion" -> versions(0))
      ),
      Target(
        Vector("hip"),
        "net.kurobako.gemstone.hip",
        "HIP",
        request.sourceRoot.resolve("net/kurobako/gemstone/hip/HIP.scala"),
        Vector("HeaderVersion" -> versions(1))
      ),
      Target(
        Vector("hsa"),
        "net.kurobako.gemstone.hsa",
        "HSA",
        request.sourceRoot.resolve("net/kurobako/gemstone/hsa/HSA.scala"),
        Vector("HeaderVersion" -> versions(2))
      ),
      Target(
        Vector("level-zero"),
        "net.kurobako.gemstone.levelzero",
        "LevelZero",
        request.sourceRoot.resolve("net/kurobako/gemstone/levelzero/LevelZero.scala"),
        Vector("HeaderVersion" -> versions(3))
      ),
      Target(
        Vector("opencl"),
        "net.kurobako.gemstone.opencl",
        "OpenCL",
        request.sourceRoot.resolve("net/kurobako/gemstone/opencl/OpenCL.scala"),
        Vector("HeaderVersion" -> versions(4))
      ),
      Target(
        Vector("vulkan"),
        "net.kurobako.gemstone.vulkan",
        "Vulkan",
        request.sourceRoot.resolve("net/kurobako/gemstone/vulkan/Vulkan.scala"),
        Vector("HeaderVersion" -> versions(5))
      )
    )
    val byName: Map[String, GenerateOfficialAPIManifest.Surface] =
      manifest.surfaces.map(surface => (surface.name.raw: String) -> surface).toMap
    targets.foreach { target =>
      val surfaces  = target.manifestNames.map(name => byName.getOrElse(name, missing(name)))
      val functions = surfaces.flatMap(_.functions)
      val typedefs  = surfaces.flatMap(_.callbackTypedefs).groupBy(_.name.raw)
      val conflicts = typedefs.collect {
        case (name, definitions) if definitions.map(_.cType).distinct.size > 1 => name
      }
      if (conflicts.nonEmpty)
        throw IllegalArgumentException(s"conflicting callback typedefs: ${conflicts.toVector.sorted.mkString(", ")}")
      val callbackTypedefs: Map[String, String] = typedefs.view.map { case (name, definitions) =>
        (name: String) -> (definitions.head.cType.raw: String)
      }.toMap
      val renderedFunctions = functions
      val functionCount     = Positive.either(renderedFunctions.size).fold(
        message => throw IllegalArgumentException(s"${target.traitName} has no declarations: $message"),
        value => value
      )
      val content = GeneratedScalaFormatter(
        target.output,
        render(
          target,
          renderedFunctions,
          callbackTypedefs,
          functionCount
        )
      )
      val _ = Files.createDirectories(target.output.getParent)
      val _ = GeneratedFiles.write(target.output, content)
      Console.out.println(s"generated ${renderedFunctions.size} ${target.traitName} declarations at ${target.output}")
    }
    val include = (path: Path) => path.getFileName.toString.endsWith(".scala")
    GeneratedFiles.removeUnexpected(
      request.sourceRoot,
      targets.map(_.output).toSet,
      include
    )
  }

  private def missing(name: String): Nothing = throw IllegalArgumentException(s"manifest surface is absent: $name")

  private def render(
      target: Target,
      functions: Vector[GenerateOfficialAPIManifest.Function],
      callbackTypedefs: Map[String, String],
      surfaceSize: Positive[Int]
  ): String = {
    val rendered = functions.traverse { function =>
      val (_, namedParameters) = function.parameters.zipWithIndex.foldLeft(
        (Set.empty[String], Vector.empty[(String, String)])
      ) { case ((used, result), (parameter, index)) =>
        val base = identifier(parameter.name.map(_.raw).getOrElse(s"arg$index"))
        val name = Iterator.from(1).map(i => if (i == 1) base else s"${base}_$i").find(name => !used(name))
          .getOrElse(throw IllegalStateException(s"cannot allocate a unique Scala name for $base"))
        (used + name, result :+ (name -> parameter.cType.raw))
      }
      for {
        parameters <- namedParameters.traverse { case (name, cType) =>
          mapType(cType.trim, callbackTypedefs).map(scalaType => s"$name: $scalaType")
        }
        returnType <- mapType(function.returnType.raw.trim, callbackTypedefs)
      } yield s"  def ${identifier(function.name.raw)}(${parameters.mkString(", ")}): $returnType = unsupported"
    }
    val (aliases, declarations) = rendered.run(Aliases.empty).value
    val aliasLines              = aliases.entries.sortBy(_._1).map {
      case (name, definition) =>
        s"  type ${identifier(name)} = $definition"
    }
    val versionLines = target.versions.map { case (name, value) =>
      s"  val $name: NonEmptyString = NonEmptyString(${literal(value.raw)})"
    }
    GeneratedHeader.scala("from pinned official headers by GenerateAPISurfaces") +
      s"""|package ${target.packageName}
        |
        |import net.kurobako.gemstone.Constraint.Positive
        |import net.kurobako.gemstone.Constraint.NonEmptyString
        |import net.kurobako.gemstone.abi.ABI.*
        |
        |${GeneratedHeader.annotation()}object ${target.traitName} {
        |${aliasLines.mkString("\n")}
        |
        |${versionLines.mkString("\n")}
        |  val FunctionCount: Positive[Int] = Positive.unsafe($surfaceSize)
        |}
        |
        |import ${target.traitName}.*
        |
        |trait ${target.traitName} {
        |${declarations.mkString("\n")}
        |}
        |""".stripMargin
  }

  private def mapType(raw: String, callbackTypedefs: Map[String, String]): Render[String] = {
    val value = Text.normalizeWhitespace(raw)
    functionPointer(value) match {
      case Some((result, parameters)) =>
        for {
          parameterTypes <- parameters.traverse(mapType(_, callbackTypedefs))
          resultType     <- mapType(result, callbackTypedefs)
        } yield s"Callback[${callbackArguments(parameterTypes)}, $resultType]"
      case None if value.endsWith("*") =>
        // Clang spells a const pointer as `*const`. When the element is itself a
        // pointer, a leading `const` qualifies its pointee and is preserved for
        // the recursive call; only the trailing `const` marks the pointer itself
        // read-only. For a scalar element the leading `const` is the read-only
        // qualifier.
        val pointee0       = value.dropRight(1).trim.replace("*const", "* const")
        val pointerElement = pointee0.contains("*")
        val trailingConst  = pointee0.endsWith(" const")
        val readOnly       = if (pointerElement) trailingConst else pointee0.startsWith("const ") || trailingConst
        val pointee        =
          if (pointerElement) if (trailingConst) pointee0.stripSuffix(" const").trim else pointee0
          else pointee0.stripPrefix("const ").stripSuffix(" const").trim
        if (readOnly && pointee == "char") State.pure("CString")
        else if (!readOnly && pointee == "void") State.pure("OpaquePointer")
        else {
          val access  = if (readOnly) "Read" else "ReadWrite"
          val element =
            if (pointee == "void") State.pure[Aliases, String]("CVoid") else mapType(pointee, callbackTypedefs)
          element.map(value => s"Pointer[$value, $access, Opaque]")
        }
      case None =>
        callbackTypedefs.get(value) match {
          case Some(signature) =>
            val (result, parameters) = functionPointer(signature)
              .getOrElse(throw IllegalStateException(s"invalid callback typedef signature: $value = $signature"))
            for {
              parameterTypes <- parameters.traverse(mapType(_, callbackTypedefs))
              resultType     <- mapType(result, callbackTypedefs)
              definition = s"CallbackTypedef[${literal(value)}, ${callbackArguments(parameterTypes)}, $resultType]"
              _ <- State.modify[Aliases](_.put(value, definition))
            } yield typeIdentifier(value)
          case None =>
            fixedArray(value) match {
              case Some((element, count)) =>
                mapType(element, callbackTypedefs).map(value => s"CArray[$value, $count]")
              case None => mapValue(value)
            }
        }
    }
  }

  private def callbackArguments(parameters: Vector[String]): String = parameters match {
    case Vector()      => "EmptyTuple"
    case Vector(value) => s"Tuple1[$value]"
    case values        => values.mkString("(", ", ", ")")
  }

  private def mapValue(raw: String): Render[String] =
    raw match {
      case "void"                                                      => State.pure("Unit")
      case "bool"                                                      => State.pure("Boolean")
      case "char"                                                      => State.pure("Byte")
      case "signed char"                                               => State.pure("Byte")
      case "short" | "short int" | "signed short" | "signed short int" => State.pure("Short")
      case "int" | "signed" | "signed int"                             => State.pure("Int")
      case "float"                                                     => State.pure("Float")
      case "double"                                                    => State.pure("Double")
      case other                                                       =>
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
    val cleaned = Identifiers.sanitized(cType)
    if (cleaned.headOption.exists(_.isDigit)) s"C_$cleaned" else cleaned
  }

  private def typeIdentifier(value: String): String = identifier(identifierName(value))

  private def identifier(value: String): String = if (ScalaKeywords(value)) s"`$value`" else value

  private def literal(value: String): String = Emission.literal(value)
}
