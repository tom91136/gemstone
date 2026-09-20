package net.kurobako.gemstone.codegen

import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import cats.syntax.all.*
import com.monovore.decline.Command
import com.monovore.decline.Opts
import GenerateOfficialAPIManifest.CIdentifier.text
import GenerateOfficialAPIManifest.CType.text
import GenerateOfficialAPIManifest.EnumValue.text
import GenerateOfficialAPIManifest.SurfaceName.text
import upickle.default.read

/** Generates native ABI declarations and implementation checks. */
object GenerateNativeBindings {
  private final case class Request(check: Boolean, output: Path, manifest: Path)
  private type GeneratedFile = (path: Path, content: String)
  private final case class Platform(
      name: String,
      surfaces: Vector[String],
      preamble: Vector[String]
  )

  private val platforms = Vector(
    Platform(
      "cuda",
      Vector("cuda-driver", "cuda-runtime", "cuda-cublas"),
      Vector("#include <cuda.h>", "#include <cuda_runtime_api.h>")
    ),
    Platform(
      "hip",
      Vector("hip"),
      Vector("#define __HIP_PLATFORM_AMD__ 1", "#include <hip/hip_runtime_api.h>")
    ),
    Platform("hsa", Vector("hsa"), Vector("#include <hsa.h>", "#include <hsa_ext_amd.h>")),
    Platform("hsakmt", Vector("hsakmt"), Vector("#include <hsakmt/hsakmt.h>")),
    Platform(
      "levelzero",
      Vector("level-zero-ddi"),
      Vector("#include <ze_ddi.h>", "#include <zes_ddi.h>", "#include <zet_ddi.h>")
    ),
    Platform(
      "opencl",
      Vector("opencl"),
      Vector(
        "#define CL_TARGET_OPENCL_VERSION 300",
        "#define CL_USE_DEPRECATED_OPENCL_1_0_APIS 1",
        "#define CL_USE_DEPRECATED_OPENCL_1_1_APIS 1",
        "#define CL_USE_DEPRECATED_OPENCL_1_2_APIS 1",
        "#define CL_USE_DEPRECATED_OPENCL_2_0_APIS 1",
        "#define CL_USE_DEPRECATED_OPENCL_2_1_APIS 1",
        "#define CL_USE_DEPRECATED_OPENCL_2_2_APIS 1",
        "#include <CL/cl_icd.h>",
        "#include <CL/cl_ext.h>",
        "#include <CL/cl_gl.h>"
      )
    )
  )

  private val command =
    Command("GenerateNativeBindings", "Generate native ABI declarations and implementation checks")((
      Opts.flag("check", help = "Verify existing generated bindings").orFalse,
      Opts.argument[String]("output-directory").map(Path.of(_)),
      Opts.argument[String]("official-header-manifest").map(Path.of(_))
    ).mapN(Request(_, _, _)))

  def main(arguments: Array[String]): Unit = command.parse(arguments.toSeq).fold(
    help => throw IllegalArgumentException(help.toString),
    request => {
      val manifest =
        read[GenerateOfficialAPIManifest.Manifest](Files.readString(request.manifest, StandardCharsets.UTF_8))
      val generated = files(request.output, manifest)
      if request.check then verify(request.output, generated) else write(request.output, generated)
    }
  )

  private def files(
      output: Path,
      manifest: GenerateOfficialAPIManifest.Manifest
  ): Vector[GeneratedFile] = {
    val byName: Map[String, GenerateOfficialAPIManifest.Surface] =
      manifest.surfaces.map(surface => surface.name.text -> surface).toMap
    val platformFiles: Vector[GeneratedFile] = platforms.flatMap { platform =>
      val functions = platform.surfaces.flatMap(name => byName.getOrElse(name, missingSurface(name)).functions)
        .distinctBy(_.name).sortBy(_.name.text)
      val enumConstants = platform.surfaces.flatMap(name => byName.getOrElse(name, missingSurface(name)).enumConstants)
      val conflicts     = enumConstants.groupBy(_.name).collect {
        case (name, values) if values.map(_.value).distinct.size > 1 =>
          s"${name.text}: ${values.map(_.value.text).distinct.mkString(" versus ")}"
      }
      if (conflicts.nonEmpty)
        throw IllegalStateException(s"${platform.name}: conflicting enum constants: ${conflicts.mkString("; ")}")
      val directory = output.resolve(platform.name)
      Vector(
        directory.resolve(s"gemstone_${platform.name}_api.h") ->
          attributed(renderPublicHeader(platform, functions), manifest),
        directory.resolve(s"gemstone_${platform.name}_constants.hpp") ->
          attributed(renderConstants(platform.name, enumConstants.distinctBy(_.name).sortBy(_.name.text)), manifest),
        directory.resolve(s"gemstone_${platform.name}_impl.cpp") ->
          attributed(renderImplementationCheck(platform.name, functions), manifest)
      )
    }
    val openCL                        = byName.getOrElse("opencl", missingSurface("opencl"))
    val openCLDispatch: GeneratedFile = output.resolve("opencl/gemstone_opencl_dispatch.hpp") ->
      attributed(renderOpenCLDispatch(openCL), manifest)
    val levelZero                   = byName.getOrElse("level-zero-ddi", missingSurface("level-zero-ddi"))
    val levelZeroDDI: GeneratedFile = output.resolve("levelzero/gemstone_levelzero_ddi.hpp") ->
      attributed(renderLevelZeroDDI(levelZero), manifest)
    platformFiles ++ Vector(openCLDispatch, levelZeroDDI)
  }

  private def missingSurface(name: String): Nothing =
    throw IllegalArgumentException(s"official API manifest is missing $name")

  private def attributed(content: String, manifest: GenerateOfficialAPIManifest.Manifest): String =
    generated(content, s"official API manifest format ${manifest.format}")

  private def generated(content: String, source: String): String =
    s"/* Generated by GenerateNativeBindings from $source. Do not edit. */\n$content"

  private def renderPublicHeader(
      platform: Platform,
      functions: Vector[GenerateOfficialAPIManifest.Function]
  ): String = {
    val guard        = s"GEMSTONE_${platform.name.toUpperCase}_API_H"
    val declarations = functions.map { function =>
      val functionType = function.functionType.text
      val open         = functionType.indexOf('(')
      val signature    = functionType.substring(0, open) + function.name.text + functionType.substring(open)
      s"extern $signature GEMSTONE_PUBLIC_API;"
    }.mkString("\n")
    s"""#ifndef $guard
       |#define $guard
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
       |
       |#endif
       |""".stripMargin
  }

  private def renderImplementationCheck(
      platform: String,
      functions: Vector[GenerateOfficialAPIManifest.Function]
  ): String = {
    val references = functions.map { function =>
      val functionType = function.functionType.text
      val functionName = function.name.text
      val open         = functionType.indexOf('(')
      val pointerType  = functionType.substring(0, open).trim + " (*)" + functionType.substring(open)
      s"using signature_$functionName = $pointerType;\n" +
        s"GEMSTONE_USED static const auto require_$functionName = " +
        s"static_cast<signature_$functionName>(&$functionName);"
    }.mkString("\n")
    s"""#include "gemstone_${platform}_api.h"
       |
       |#if defined(__GNUC__) || defined(__clang__)
       |#define GEMSTONE_USED __attribute__((used))
       |#else
       |#define GEMSTONE_USED
       |#endif
       |
       |namespace {
       |$references
       |}
       |""".stripMargin
  }

  private def renderConstants(
      platform: String,
      constants: Vector[GenerateOfficialAPIManifest.EnumConstant]
  ): String = {
    val guard  = s"GEMSTONE_${platform.toUpperCase}_CONSTANTS_HPP"
    val values =
      constants.map(constant => s"inline constexpr auto ${constant.name.text} = ${constant.value.text};").mkString("\n")
    s"""#ifndef $guard
       |#define $guard
       |
       |namespace gemstone::$platform::api {
       |$values
       |}
       |
       |#endif
       |""".stripMargin
  }

  private def renderOpenCLDispatch(surface: GenerateOfficialAPIManifest.Surface): String = {
    val table = surface.dispatchTables match {
      case Vector(value) => value
      case values        => throw IllegalArgumentException(s"opencl: expected one dispatch table, found ${values.size}")
    }
    val functions   = surface.functions.map(_.name.text).toSet
    val assignments = table.fields.map { field =>
      val fieldName = field.name.text
      val value     = if functions(fieldName) then s"&$fieldName" else "nullptr"
      s"  dispatch.$fieldName = $value;"
    }.mkString("\n")
    s"""#ifndef GEMSTONE_OPENCL_DISPATCH_HPP
       |#define GEMSTONE_OPENCL_DISPATCH_HPP
       |
       |#include "gemstone_opencl_api.h"
       |
       |namespace gemstone::opencl {
       |inline void populateDispatch(cl_icd_dispatch &dispatch) {
       |$assignments
       |}
       |}
       |
       |#endif
       |""".stripMargin
  }

  private def renderLevelZeroDDI(surface: GenerateOfficialAPIManifest.Surface): String = {
    if (surface.dispatchTables.isEmpty)
      throw IllegalStateException("level-zero-ddi: no DDI table records found")
    val clearers = surface.dispatchTables.map { table =>
      val assignments = table.fields.map(field => s"  table->${field.name.text} = nullptr;").mkString("\n")
      s"""inline void clear(struct ${table.name.text} *table) {
         |$assignments
         |}""".stripMargin
    }.mkString("\n\n")
    s"""#ifndef GEMSTONE_LEVELZERO_DDI_HPP
       |#define GEMSTONE_LEVELZERO_DDI_HPP
       |
       |#include <ze_ddi.h>
       |#include <zes_ddi.h>
       |#include <zet_ddi.h>
       |
       |namespace gemstone_levelzero_ddi {
       |$clearers
       |}
       |
       |#endif
       |""".stripMargin
  }

  private def write(output: Path, files: Vector[GeneratedFile]): Unit = {
    files.foreach { file =>
      val _ = GeneratedFiles.write(file.path, file.content)
      Console.out.println(s"generated ${file.path}")
    }
    GeneratedFiles.removeUnexpected(output, files.map(_.path).toSet, path => !path.startsWith(output.resolve("rpc")))
  }

  private def verify(output: Path, files: Vector[GeneratedFile]): Unit = {
    val stale = files.collect {
      case file
          if !Files.exists(file.path) || Files.readString(file.path, StandardCharsets.UTF_8) != file.content =>
        file.path
    }
    if stale.nonEmpty then throw IllegalStateException(s"generated native ABI files are stale: ${stale.mkString(", ")}")
    GeneratedFiles.requireOnly(output, files.map(_.path).toSet, path => !path.startsWith(output.resolve("rpc")))
    Console.out.println(s"verified ${files.size} generated native ABI files")
  }
}
