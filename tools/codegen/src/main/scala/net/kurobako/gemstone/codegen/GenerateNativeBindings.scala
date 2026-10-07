package net.kurobako.gemstone.codegen

import GenerateOfficialAPIManifest.*
import NativeAPIs.*
import cats.syntax.all.*
import com.monovore.decline.Command
import com.monovore.decline.Opts
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import upickle.default.read

/** Generates native ABI declarations, implementation checks, and the inventories of unimplemented functions. */
object GenerateNativeBindings {
  private type GeneratedFile = (path: Path, content: String)

  private val command =
    Command("GenerateNativeBindings", "Generate native ABI declarations and implementation checks")((
      Opts.argument[String]("output-directory").map(Path.of(_)),
      Opts.argument[String]("official-header-manifest").map(Path.of(_))
    ).tupled)

  def main(arguments: Array[String]): Unit = CodegenCommand.run(command, arguments) { (output, manifest) =>
    generate(output, manifest, NativeAPIs.platforms)
  }

  def generate(output: Path, manifestPath: Path, platforms: Vector[Platform]): Unit = {
    val manifest = read[Manifest](Files.readString(manifestPath, StandardCharsets.UTF_8))
    val files    = platforms.flatMap(platformFiles(output, manifest, _))
    files.foreach(file => GeneratedFiles.write(file.path, file.content))
    val owned = files.map(_.path.toAbsolutePath.normalize.getParent).toSet
    GeneratedFiles.removeUnexpected(output, files.map(_.path).toSet, path => owned(path.getParent))
    Console.out.println(s"generated ${files.size} native binding files under $output")
  }

  private def platformFiles(output: Path, manifest: Manifest, platform: Platform): Vector[GeneratedFile] = {
    val surfaces = platform.surfaces.map { surface =>
      val found = manifest.surfaces.find(_.name.raw == surface.name)
        .getOrElse(throw IllegalArgumentException(s"official API manifest is missing ${surface.name}"))
      surface.dispatchTable.foreach(table =>
        if (!found.dispatchTable.exists(_.name.raw == table))
          throw IllegalArgumentException(s"official API manifest ${surface.name} is missing dispatch table $table")
      )
      found
    }
    val functions = Function.merge(surfaces.flatMap(_.functions)).sortBy(_.name.raw)
    val prefix    = s"gemstone_${platform.name}"
    val coverage  = platform.coverage match {
      case Coverage.Complete         => Vector.empty
      case partial: Coverage.Partial =>
        Vector(s"${prefix}_unsupported.cpp" -> unsupported(platform, functions, partial))
      case Coverage.Vulkan => VulkanAdapter.render(functions)
    }
    val dispatch =
      surfaces.flatMap(_.dispatchTable).map(s"${prefix}_dispatch.hpp" -> dispatchTable(platform, functions, _))
    val entryPoints = surfaces.flatMap(_.entryPoints).sortBy(_.name.raw)
    val history     =
      Option.when(entryPoints.nonEmpty)(s"${prefix}_entry_points.hpp" -> entryPointHistory(platform, entryPoints))
    val sources = Vector(
      s"${prefix}_api.h"    -> publicHeader(platform, functions),
      s"${prefix}_impl.cpp" -> implementationCheck(platform, functions)
    ) ++ coverage ++ dispatch ++ history
    val directory = output.resolve(platform.name)
    val banner    = GeneratedHeader.cpp(s"from the official API manifest format ${manifest.format}")
    sources.map((name, content) => directory.resolve(name) -> (banner + content)) ++
      Option.when(platform.coverage == Coverage.Vulkan)(
        directory.resolve(s"${prefix}_graphics.hpp") ->
          (GeneratedHeader.cpp("from GraphicsProtocol enums") + GraphicsEnums.nativeSource)
      )
  }

  private def publicHeader(platform: Platform, functions: Vector[Function]): String = {
    val declarations = functions.map { function =>
      val open = function.functionType.indexOf('(')
      s"extern ${function.functionType.substring(0, open)}${function.name}${function.functionType.substring(open)} " +
        "GEMSTONE_PUBLIC_API;"
    }.mkString("\n")
    s"""#pragma once
       |
       |#include <gemstone/export.h>
       |${platform.preamble.mkString("\n")}
       |
       |#ifdef __cplusplus
       |extern "C" {
       |#endif
       |
       |$declarations
       |
       |#ifdef __cplusplus
       |}
       |#endif
       |""".stripMargin
  }

  private def implementationCheck(platform: Platform, functions: Vector[Function]): String = {
    val references = functions.map { function =>
      val open        = function.functionType.indexOf('(')
      val pointerType = function.functionType.substring(0, open).trim + " (*)" + function.functionType.substring(open)
      s"  reinterpret_cast<void (*)()>(static_cast<$pointerType>(&${function.name})),"
    }.mkString("\n")
    s"""#include "gemstone_${platform.name}_api.h"
       |
       |#if defined(__GNUC__) || defined(__clang__)
       |#define GEMSTONE_USED __attribute__((used))
       |#else
       |#define GEMSTONE_USED
       |#endif
       |
       |namespace {
       |GEMSTONE_USED void (*const RequiredFunctions[])() = {
       |$references
       |};
       |}
       |""".stripMargin
  }

  private def unsupported(platform: Platform, functions: Vector[Function], coverage: Coverage.Partial): String = {
    val names = functions.map(_.name.raw).toSet
    val stale = coverage.implemented -- names
    require(
      stale.isEmpty,
      s"${platform.name}: implemented functions absent from the manifest: ${stale.toVector.sorted}"
    )
    val declared = coverage.legacy.map(_._1).filter(names)
    require(declared.isEmpty, s"${platform.name}: legacy names declared by the manifest: $declared")
    val statuses             = coverage.statuses.map(status => status.cType -> status).toMap
    val missing              = functions.filterNot(function => coverage.implemented(function.name.raw))
    val (aliased, defaulted) = missing.partition(function => statuses.contains(function.returnType.raw))
    val stubs                = coverage.statuses.map { status =>
      s"GEMSTONE_INTERNAL_API ${status.cType} ${status.stub}() { return ${status.value}; }"
    }
    val definitions = defaulted.map { function =>
      require(
        !function.functionType.contains("..."),
        s"${platform.name}: no default body for variadic ${function.name}"
      )
      val error =
        function.parameters.lastOption.flatMap(last => coverage.statuses.find(status => last == s"${status.cType} *"))
      val parameters = function.parameters.zipWithIndex.map { (cType, index) =>
        if (error.isDefined && index == function.parameters.size - 1) s"${cType}error" else cType.raw
      }
      val assignment = error.map(status => s"if (error) *error = ${status.value}; ").getOrElse("")
      val result     = function.returnType.raw match {
        case "void"         => ""
        case "const char *" => "return \"unsupported\"; "
        case _              => "return {}; "
      }
      s"${function.returnType} ${function.name}(${parameters.mkString(", ")}) { $assignment$result}"
    }
    val aliases = aliased.map { function =>
      val status = statuses(function.returnType.raw)
      if (coverage.erased) s"GEMSTONE_UNSUPPORTED_AS(${status.cType}(), ${function.name}, ${status.stub})"
      else s"GEMSTONE_UNSUPPORTED(${function.name}, ${status.stub})"
    } ++ coverage.legacy.map((name, aliasType) =>
      s"GEMSTONE_UNSUPPORTED_AS($aliasType, $name, ${coverage.statuses.head.stub})"
    )
    s"""#include "gemstone_${platform.name}_api.h"
       |#include <gemstone/unsupported.hpp>
       |
       |extern "C" {
       |${(stubs ++ definitions).mkString("\n")}
       |}
       |
       |${aliases.mkString("\n")}
       |""".stripMargin
  }

  private def entryPointHistory(platform: Platform, entryPoints: Vector[EntryPoint]): String = {
    val width   = entryPoints.map(_.versions.size).max
    val entries = entryPoints.map(entry =>
      s"""    EntryPointHistory{"${entry.name}", {${entry.versions.mkString(", ")}}},"""
    ).mkString("\n")
    s"""#pragma once
       |
       |#include <array>
       |#include <string_view>
       |
       |namespace gemstone::${platform.name} {
       |
       |/** The driver version introducing each revision in order; zero pads absent revisions. */
       |struct EntryPointHistory {
       |  std::string_view name;
       |  std::array<int, $width> revisions;
       |};
       |
       |/** Sorted by name. */
       |inline constexpr std::array EntryPointHistories{
       |$entries
       |};
       |
       |} // namespace gemstone::${platform.name}
       |""".stripMargin
  }

  private def dispatchTable(platform: Platform, functions: Vector[Function], table: DispatchTable): String = {
    val names       = functions.map(_.name.raw).toSet
    val assignments = table.fields.map { field =>
      s"  dispatch.${field.name} = ${if (names(field.name)) s"&${field.name}" else "nullptr"};"
    }.mkString("\n")
    s"""#pragma once
       |
       |#include "gemstone_${platform.name}_api.h"
       |
       |namespace gemstone::${platform.name} {
       |inline void populateDispatch(${table.name} &dispatch) {
       |$assignments
       |}
       |}
       |""".stripMargin
  }
}
