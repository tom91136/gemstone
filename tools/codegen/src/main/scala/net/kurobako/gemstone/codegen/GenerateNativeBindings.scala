package net.kurobako.gemstone.codegen

import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import cats.syntax.all.*
import com.monovore.decline.Command
import com.monovore.decline.Opts
import GenerateOfficialAPIManifest.CIdentifier.raw
import GenerateOfficialAPIManifest.CType.raw
import GenerateOfficialAPIManifest.EnumValue.raw
import GenerateOfficialAPIManifest.SurfaceName.raw
import upickle.default.read

/** Generates native ABI declarations and implementation checks. */
object GenerateNativeBindings {
  private case class Request(output: Path, manifest: Path)
  private type GeneratedFile = (path: Path, content: String)
  private case class Platform(
      name: String,
      surfaces: Vector[String],
      preamble: Vector[String],
      layout: Option[LayoutSpec] = None
  )
  private case class FieldLayout(mirror: String, vendor: String)
  private case class StructLayout(
      mirror: String,
      vendor: String,
      fields: Vector[FieldLayout],
      prefix: Boolean = false
  )
  private case class LayoutSpec(header: String, defines: Vector[String], structs: Vector[StructLayout])

  private def layoutFields(pairs: (String, String)*): Vector[FieldLayout] =
    pairs.toVector.map { case (mirror, vendor) => FieldLayout(mirror, vendor) }

  // The CUDA facade mirrors the driver ABI without including the vendor headers,
  // so every mirrored field is asserted against the pinned header at build time.
  private val CudaLayout = LayoutSpec(
    header = "gemstone/cuda/abi_layout.hpp",
    defines = Vector("__CUDA_API_VERSION_INTERNAL 1"),
    structs = Vector(
      StructLayout(
        "gemstone::cuda::CudaArrayDescriptor",
        "CUDA_ARRAY_DESCRIPTOR",
        layoutFields("width" -> "Width", "height" -> "Height", "format" -> "Format", "channels" -> "NumChannels")
      ),
      StructLayout(
        "gemstone::cuda::CudaArray3DDescriptor",
        "CUDA_ARRAY3D_DESCRIPTOR",
        layoutFields(
          "width"    -> "Width",
          "height"   -> "Height",
          "depth"    -> "Depth",
          "format"   -> "Format",
          "channels" -> "NumChannels",
          "flags"    -> "Flags"
        )
      ),
      StructLayout(
        "gemstone::cuda::CudaMemcpy2D",
        "CUDA_MEMCPY2D",
        layoutFields(
          "src_x_in_bytes"  -> "srcXInBytes",
          "src_y"           -> "srcY",
          "src_memory_type" -> "srcMemoryType",
          "src_host"        -> "srcHost",
          "src_device"      -> "srcDevice",
          "src_array"       -> "srcArray",
          "src_pitch"       -> "srcPitch",
          "dst_x_in_bytes"  -> "dstXInBytes",
          "dst_y"           -> "dstY",
          "dst_memory_type" -> "dstMemoryType",
          "dst_host"        -> "dstHost",
          "dst_device"      -> "dstDevice",
          "dst_array"       -> "dstArray",
          "dst_pitch"       -> "dstPitch",
          "width_in_bytes"  -> "WidthInBytes",
          "height"          -> "Height"
        )
      ),
      StructLayout(
        "gemstone::cuda::CudaMemcpy3D",
        "CUDA_MEMCPY3D",
        layoutFields(
          "src_x_in_bytes"  -> "srcXInBytes",
          "src_y"           -> "srcY",
          "src_z"           -> "srcZ",
          "src_lod"         -> "srcLOD",
          "src_memory_type" -> "srcMemoryType",
          "src_host"        -> "srcHost",
          "src_device"      -> "srcDevice",
          "src_array"       -> "srcArray",
          "src_reserved"    -> "reserved0",
          "src_pitch"       -> "srcPitch",
          "src_height"      -> "srcHeight",
          "dst_x_in_bytes"  -> "dstXInBytes",
          "dst_y"           -> "dstY",
          "dst_z"           -> "dstZ",
          "dst_lod"         -> "dstLOD",
          "dst_memory_type" -> "dstMemoryType",
          "dst_host"        -> "dstHost",
          "dst_device"      -> "dstDevice",
          "dst_array"       -> "dstArray",
          "dst_reserved"    -> "reserved1",
          "dst_pitch"       -> "dstPitch",
          "dst_height"      -> "dstHeight",
          "width_in_bytes"  -> "WidthInBytes",
          "height"          -> "Height",
          "depth"           -> "Depth"
        )
      ),
      StructLayout(
        "gemstone::cuda::CudaArrayDescriptorV1",
        "CUDA_ARRAY_DESCRIPTOR_v1",
        layoutFields("width" -> "Width", "height" -> "Height", "format" -> "Format", "channels" -> "NumChannels")
      ),
      StructLayout(
        "gemstone::cuda::CudaArray3DDescriptorV1",
        "CUDA_ARRAY3D_DESCRIPTOR_v1",
        layoutFields(
          "width"    -> "Width",
          "height"   -> "Height",
          "depth"    -> "Depth",
          "format"   -> "Format",
          "channels" -> "NumChannels",
          "flags"    -> "Flags"
        )
      ),
      StructLayout(
        "gemstone::cuda::CudaMemcpy2DV1",
        "CUDA_MEMCPY2D_v1",
        layoutFields(
          "src_x_in_bytes"  -> "srcXInBytes",
          "src_y"           -> "srcY",
          "src_memory_type" -> "srcMemoryType",
          "src_host"        -> "srcHost",
          "src_device"      -> "srcDevice",
          "src_array"       -> "srcArray",
          "src_pitch"       -> "srcPitch",
          "dst_x_in_bytes"  -> "dstXInBytes",
          "dst_y"           -> "dstY",
          "dst_memory_type" -> "dstMemoryType",
          "dst_host"        -> "dstHost",
          "dst_device"      -> "dstDevice",
          "dst_array"       -> "dstArray",
          "dst_pitch"       -> "dstPitch",
          "width_in_bytes"  -> "WidthInBytes",
          "height"          -> "Height"
        )
      ),
      StructLayout(
        "gemstone::cuda::CudaMemcpy3DV1",
        "CUDA_MEMCPY3D_v1",
        layoutFields(
          "src_x_in_bytes"  -> "srcXInBytes",
          "src_y"           -> "srcY",
          "src_z"           -> "srcZ",
          "src_lod"         -> "srcLOD",
          "src_memory_type" -> "srcMemoryType",
          "src_host"        -> "srcHost",
          "src_device"      -> "srcDevice",
          "src_array"       -> "srcArray",
          "src_reserved"    -> "reserved0",
          "src_pitch"       -> "srcPitch",
          "src_height"      -> "srcHeight",
          "dst_x_in_bytes"  -> "dstXInBytes",
          "dst_y"           -> "dstY",
          "dst_z"           -> "dstZ",
          "dst_lod"         -> "dstLOD",
          "dst_memory_type" -> "dstMemoryType",
          "dst_host"        -> "dstHost",
          "dst_device"      -> "dstDevice",
          "dst_array"       -> "dstArray",
          "dst_reserved"    -> "reserved1",
          "dst_pitch"       -> "dstPitch",
          "dst_height"      -> "dstHeight",
          "width_in_bytes"  -> "WidthInBytes",
          "height"          -> "Height",
          "depth"           -> "Depth"
        )
      ),
      StructLayout(
        "gemstone::cuda::CudaLaunchConfig",
        "CUlaunchConfig",
        layoutFields(
          "grid_x"              -> "gridDimX",
          "grid_y"              -> "gridDimY",
          "grid_z"              -> "gridDimZ",
          "block_x"             -> "blockDimX",
          "block_y"             -> "blockDimY",
          "block_z"             -> "blockDimZ",
          "shared_memory_bytes" -> "sharedMemBytes",
          "stream"              -> "hStream",
          "attributes"          -> "attrs",
          "attribute_count"     -> "numAttrs"
        )
      ),
      StructLayout("gemstone::cuda::Dim3", "dim3", layoutFields("x" -> "x", "y" -> "y", "z" -> "z")),
      StructLayout(
        "gemstone::cuda::DevicePropertiesPrefix",
        "cudaDeviceProp",
        layoutFields(
          "name"                    -> "name",
          "uuid"                    -> "uuid",
          "luid"                    -> "luid",
          "luid_device_node_mask"   -> "luidDeviceNodeMask",
          "total_global_memory"     -> "totalGlobalMem",
          "shared_memory_per_block" -> "sharedMemPerBlock",
          "registers_per_block"     -> "regsPerBlock",
          "warp_size"               -> "warpSize",
          "memory_pitch"            -> "memPitch",
          "max_threads_per_block"   -> "maxThreadsPerBlock",
          "max_threads_dimension"   -> "maxThreadsDim",
          "max_grid_size"           -> "maxGridSize",
          "total_constant_memory"   -> "totalConstMem",
          "major"                   -> "major",
          "minor"                   -> "minor",
          "texture_alignment"       -> "textureAlignment",
          "texture_pitch_alignment" -> "texturePitchAlignment",
          "multiprocessor_count"    -> "multiProcessorCount"
        ),
        prefix = true
      ),
      StructLayout(
        "gemstone::cuda::FunctionAttributesPrefix",
        "cudaFuncAttributes",
        layoutFields(
          "shared_size_bytes"                -> "sharedSizeBytes",
          "constant_size_bytes"              -> "constSizeBytes",
          "local_size_bytes"                 -> "localSizeBytes",
          "max_threads_per_block"            -> "maxThreadsPerBlock",
          "register_count"                   -> "numRegs",
          "ptx_version"                      -> "ptxVersion",
          "binary_version"                   -> "binaryVersion",
          "cache_mode_ca"                    -> "cacheModeCA",
          "max_dynamic_shared_size_bytes"    -> "maxDynamicSharedSizeBytes",
          "preferred_shared_memory_carveout" -> "preferredShmemCarveout"
        ),
        prefix = true
      ),
      StructLayout(
        "gemstone::cuda::LegacyDeviceProperties",
        "CUdevprop",
        layoutFields(
          "max_threads_per_block"   -> "maxThreadsPerBlock",
          "max_threads_dim"         -> "maxThreadsDim",
          "max_grid_size"           -> "maxGridSize",
          "shared_memory_per_block" -> "sharedMemPerBlock",
          "total_constant_memory"   -> "totalConstantMemory",
          "simd_width"              -> "SIMDWidth",
          "memory_pitch"            -> "memPitch",
          "registers_per_block"     -> "regsPerBlock",
          "clock_rate"              -> "clockRate",
          "texture_alignment"       -> "textureAlign"
        )
      )
    )
  )

  private val platforms = Vector(
    Platform(
      "cuda",
      Vector("cuda-driver", "cuda-runtime", "cuda-cublas"),
      Vector("#include <cuda.h>", "#include <cuda_runtime_api.h>", "#include <cublas_api.h>"),
      layout = Some(CudaLayout)
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
    ),
    Platform("vulkan", Vector("vulkan"), Vector("#include <vulkan/vulkan.h>"))
  )

  private val command =
    Command("GenerateNativeBindings", "Generate native ABI declarations and implementation checks")((
      Opts.argument[String]("output-directory").map(Path.of(_)),
      Opts.argument[String]("official-header-manifest").map(Path.of(_))
    ).mapN(Request(_, _)))

  def main(arguments: Array[String]): Unit = CodegenCommand.run(command, arguments) { request =>
    val manifest =
      read[GenerateOfficialAPIManifest.Manifest](Files.readString(request.manifest, StandardCharsets.UTF_8))
    write(request.output, files(request.output, manifest))
  }

  private def files(
      output: Path,
      manifest: GenerateOfficialAPIManifest.Manifest
  ): Vector[GeneratedFile] = {
    val byName: Map[String, GenerateOfficialAPIManifest.Surface] =
      manifest.surfaces.map(surface => surface.name.raw -> surface).toMap
    val platformFiles: Vector[GeneratedFile] = platforms.flatMap { platform =>
      val functions = platform.surfaces.flatMap(name => byName.getOrElse(name, missingSurface(name)).functions)
        .distinctBy(_.name).sortBy(_.name.raw)
      val enumConstants = platform.surfaces.flatMap(name => byName.getOrElse(name, missingSurface(name)).enumConstants)
      val conflicts     = enumConstants.groupBy(_.name).collect {
        case (name, values) if values.map(_.value).distinct.size > 1 =>
          s"${name.raw}: ${values.map(_.value.raw).distinct.mkString(" versus ")}"
      }
      if (conflicts.nonEmpty)
        throw IllegalStateException(s"${platform.name}: conflicting enum constants: ${conflicts.mkString("; ")}")
      val directory = output.resolve(platform.name)
      val constants = enumConstants.distinctBy(_.name).sortBy(_.name.raw)
      val layout    = platform.layout.map { spec =>
        directory.resolve(s"gemstone_${platform.name}_layout.cpp") ->
          attributed(renderLayout(platform, spec), manifest)
      }
      Vector(
        directory.resolve(s"gemstone_${platform.name}_api.h") ->
          attributed(renderPublicHeader(platform, functions), manifest),
        directory.resolve(s"gemstone_${platform.name}_constants.hpp") ->
          attributed(renderConstants(platform.name, constants), manifest),
        directory.resolve(s"gemstone_${platform.name}_impl.cpp") ->
          attributed(renderImplementationCheck(platform.name, functions, constants), manifest)
      ) ++ layout
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
    GeneratedHeader.cpp(s"by GenerateNativeBindings from $source") + content

  private def renderPublicHeader(
      platform: Platform,
      functions: Vector[GenerateOfficialAPIManifest.Function]
  ): String = {
    val guard        = s"GEMSTONE_${platform.name.toUpperCase(java.util.Locale.ROOT)}_API_H"
    val declarations = functions.map { function =>
      val functionType = function.functionType.raw
      val open         = functionType.indexOf('(')
      val signature    = functionType.substring(0, open) + function.name.raw + functionType.substring(open)
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
      functions: Vector[GenerateOfficialAPIManifest.Function],
      constants: Vector[GenerateOfficialAPIManifest.EnumConstant]
  ): String = {
    val references = functions.map { function =>
      val functionType = function.functionType.raw
      val functionName = function.name.raw
      val open         = functionType.indexOf('(')
      val pointerType  = functionType.substring(0, open).trim + " (*)" + functionType.substring(open)
      s"using signature_$functionName = $pointerType;\n" +
        s"GEMSTONE_USED static const auto require_$functionName = " +
        s"static_cast<signature_$functionName>(&$functionName);"
    }.mkString("\n")
    // Generated literals mirror the pinned headers, so assert each one back against
    // the vendor enumerator to catch drift at compile time.
    val constantChecks = constants.map { constant =>
      val name = constant.name.raw
      s"static_assert(static_cast<long long>(gemstone::$platform::api::$name) == static_cast<long long>($name));"
    }.mkString("\n")
    s"""#include "gemstone_${platform}_api.h"
       |#include "gemstone_${platform}_constants.hpp"
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
       |
       |$constantChecks
       |""".stripMargin
  }

  private def renderLayout(platform: Platform, spec: LayoutSpec): String = {
    val defines    = spec.defines.map(define => s"#define $define").mkString("\n")
    val assertions = spec.structs.flatMap { layout =>
      val size =
        if (layout.prefix)
          s"static_assert(sizeof(${layout.mirror}) <= sizeof(${layout.vendor}), \"${layout.vendor} prefix exceeds the vendor size\");"
        else s"static_assert(sizeof(${layout.mirror}) == sizeof(${layout.vendor}), \"${layout.vendor} size drift\");"
      size +: layout.fields.map { field =>
        s"static_assert(offsetof(${layout.mirror}, ${field.mirror}) == offsetof(${layout.vendor}, ${field.vendor}), " +
          s"\"${layout.vendor}.${field.vendor} offset drift\");"
      }
    }.mkString("\n")
    s"""$defines
       |${platform.preamble.mkString("\n")}
       |#include "${spec.header}"
       |
       |$assertions
       |""".stripMargin
  }

  private def renderConstants(
      platform: String,
      constants: Vector[GenerateOfficialAPIManifest.EnumConstant]
  ): String = {
    val guard  = s"GEMSTONE_${platform.toUpperCase(java.util.Locale.ROOT)}_CONSTANTS_HPP"
    val values =
      constants.map(constant => s"inline constexpr auto ${constant.name.raw} = ${constant.value.raw};").mkString("\n")
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
    val functions   = surface.functions.map(_.name.raw).toSet
    val assignments = table.fields.map { field =>
      val fieldName = field.name.raw
      val value     = if (functions(fieldName)) s"&$fieldName" else "nullptr"
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
      val assignments = table.fields.map(field => s"  table->${field.name.raw} = nullptr;").mkString("\n")
      s"""inline void clear(struct ${table.name.raw} *table) {
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
}
