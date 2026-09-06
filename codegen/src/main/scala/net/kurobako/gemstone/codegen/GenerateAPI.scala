package net.kurobako.gemstone.codegen

import net.kurobako.gemstone.Col
import net.kurobako.gemstone.abi.ABIDeclaration
import net.kurobako.gemstone.abi.ABIDerivation
import net.kurobako.gemstone.abi.MethodImplementation
import net.kurobako.gemstone.cuda.CUDA
import net.kurobako.gemstone.rpc.RPCDerivation
import net.kurobako.gemstone.rpc.RPCParameter
import net.kurobako.gemstone.rpc.RPCSchema
import net.kurobako.gemstone.rpc.WireDirection
import net.kurobako.gemstone.rpc.WireExtent
import net.kurobako.gemstone.rpc.WireType

import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

object GenerateAPI {
  // C entry-point spelling is protocol identity and must never be acronym-normalized.
  // Internal Gemstone methods share this schema so native glue stays a thin RPC adapter.
  private val cudaRPC: RPCSchema                          = RPCDerivation.schema[CUDA]
  private val cudaNativeDelegates: Vector[ABIDeclaration] = ABIDerivation.nativeDelegates[CUDA].toVector
  private val cudaDriverSurface                           = RPCDerivation.methodNames[CUDA].filter(name =>
    name.startsWith("cu") && !name.startsWith("cuda")
  ).toVector
  private val cudaRuntimeSurface   = RPCDerivation.methodNames[CUDA].filter(_.startsWith("cuda")).toVector
  private val cudaBootstrapSurface = RPCDerivation.methodNames[CUDA].filter(name =>
    name.startsWith("__cuda") && !name.startsWith("__cudaGemstone")
  ).toVector
  private val cudaCompatibilitySurface = Vector(
    // The CUDA loader still probes these legacy spellings with dlsym.  The
    // handwritten compatibility layer implements them as aliases of the
    // versioned entry points, so keep both spellings in libcuda's ABI.
    "cuCtxPushCurrent",
    "cuCtxPopCurrent",
    "cuDevicePrimaryCtxRelease",
    "cuDevicePrimaryCtxReset",
    "cuArray3DCreate",
    "cuArray3DGetDescriptor",
    "cuArrayCreate",
    "cuArrayGetDescriptor",
    "cuMemcpy2D",
    "cuMemcpy2DUnaligned",
    "cuMemcpy2DAsync",
    "cuMemcpy3D",
    "cuMemcpy3DAsync",
    "cuModuleGetGlobal",
    "cuGetProcAddress",
    "cuOccupancyMaxActiveBlocksPerMultiprocessor",
    "cuOccupancyMaxActiveBlocksPerMultiprocessorWithFlags",
    // CUDA 13's runtime resolves stream-specialized driver spellings directly
    // with dlsym in addition to cuGetProcAddress.  These ABI aliases are
    // implemented by the handwritten compatibility layer and must therefore
    // be exported even though they are not separate trait methods.
    "cuMemcpyHtoD_ptds",
    "cuMemcpyHtoD_v2_ptds",
    "cuMemcpyDtoH_ptds",
    "cuMemcpyDtoH_v2_ptds",
    "cuMemcpyDtoD_ptds",
    "cuMemcpyDtoD_v2_ptds",
    "cuMemcpy",
    "cuMemcpy_ptds",
    "cuMemcpyAsync",
    "cuMemcpyAsync_ptsz",
    "cuMemcpyHtoDAsync_ptsz",
    "cuMemcpyHtoDAsync_v2_ptsz",
    "cuMemcpyDtoHAsync_ptsz",
    "cuMemcpyDtoHAsync_v2_ptsz",
    "cuMemcpyDtoDAsync_ptsz",
    "cuMemcpyDtoDAsync_v2_ptsz",
    "cuMemsetD8_ptds",
    "cuMemsetD8_v2_ptds",
    "cuMemsetD8Async_ptsz"
  )
  def main(arguments: Array[String]): Unit = {
    val (check, output, scalaOutput, officialManifest) = arguments.toVector match {
      case Vector(path, scalaPath, manifest) =>
        (false, Paths.get(path), Paths.get(scalaPath), Paths.get(manifest))
      case Vector("--check", path, scalaPath, manifest) =>
        (true, Paths.get(path), Paths.get(scalaPath), Paths.get(manifest))
      case _ =>
        throw IllegalArgumentException(
          "usage: GenerateAPI [--check] <output-directory> <scala-output-directory> <official-header-manifest>"
        )
    }

    val generated = files(output, scalaOutput, officialManifest).map { case (path, content) =>
      path -> (if (path.getFileName.toString.endsWith(".scala")) GeneratedScalaFormatter(path, content) else content)
    }
    if (check) verify(generated)
    else write(generated)
  }

  private def files(output: Path, scalaOutput: Path, officialManifest: Path): Vector[(Path, String)] =
    Vector(
      output.resolve("c/gemstone_status.h")                -> renderStatus,
      output.resolve("c/gemstone_cuda_driver_stubs.c")     -> renderCUDADriverStubs(officialManifest),
      output.resolve("c/gemstone_cuda.map")                -> renderVersionMap,
      output.resolve("c/gemstone_cuda_driver_surface.inc") -> renderCUDASurfaceInclude,
      output.resolve("c/gemstone_cuda_rpc.inc")            -> renderCUDARPCInclude,
      output.resolve("c/gemstone_cuda_rpc_client.c")       -> renderCUDARPCClient,
      scalaOutput.resolve("net/kurobako/gemstone/generated/GeneratedCUDARPC.scala") -> renderCUDARPCScala,
      output.resolve("manifest/cuda-rpc-schema.json")                               -> renderCUDARPCManifest,
      output.resolve("manifest/cuda-driver-surface.json")                           -> renderCUDASurfaceManifest
    )

  private def write(files: Vector[(Path, String)]): Unit = files.foreach { case (path, content) =>
    val _ = GeneratedFiles.write(path, content)
    println(s"generated $path")
  }

  private def verify(files: Vector[(Path, String)]): Unit = {
    val stale = files.collect {
      case (path, content) if !Files.exists(path) || Files.readString(path, StandardCharsets.UTF_8) != content => path
    }
    if (stale.nonEmpty) {
      throw IllegalStateException(s"generated API files are stale: ${stale.mkString(", ")}")
    }
    println(s"verified ${files.size} generated API files")
  }

  private def renderStatus: String =
    s"""|#ifndef GEMSTONE_STATUS_H
        |#define GEMSTONE_STATUS_H
        |
        |enum {
        |  GEMSTONE_STATUS_SUCCESS = 0,
        |  GEMSTONE_STATUS_INVALID_VALUE = 1,
        |  GEMSTONE_STATUS_INITIALIZATION_ERROR = 3,
        |  GEMSTONE_STATUS_NOT_SUPPORTED = 801,
        |  GEMSTONE_STATUS_NOT_FOUND = 500,
        |  GEMSTONE_STATUS_UNKNOWN = 999
        |};
        |
        |#endif
        |""".stripMargin
  private def renderVersionMap: String = {
    val symbols = (cudaDriverSurface ++ cudaRuntimeSurface ++ cudaBootstrapSurface ++ cudaCompatibilitySurface).distinct
      .map(symbol => s"    $symbol;")
      .mkString("\n")
    s"""|GEMSTONE_0.1 {
        |  global:
        |$symbols
        |  local:
        |    *;
        |};
        |""".stripMargin
  }

  private def renderCUDADriverStubs(officialManifest: Path): String = {
    val wired              = cudaRPC.methods.map(_.name).filterNot(_.startsWith("cuda")).toSet
    val driverDeclarations = officialCUDADeclarations(officialManifest)
    val delegatedNames     = cudaNativeDelegates.map(_.name).toSet
    val stubs              = driverDeclarations.collect {
      case declaration
          if !wired(declaration.name) && !delegatedNames(declaration.name) =>
        val signature = renderCFunctionDeclaration(declaration, declaration.name)
        val unused    = declaration.parameterNames.map(name => s"  (void)$name;").mkString("\n")
        val body      = if (unused.isEmpty) "" else s"\n$unused"
        val weak      = "GEMSTONE_CUDA_WEAK "
        if (declaration.name == "cuModuleGetLoadingMode")
          s"$weak$signature {\n  if (mode == NULL) return CUDA_ERROR_INVALID_VALUE;\n  *mode = CU_MODULE_EAGER_LOADING;\n  return CUDA_SUCCESS;\n}"
        else s"$weak$signature {$body\n  return CUDA_ERROR_NOT_SUPPORTED;\n}"
    }
    val delegates = cudaNativeDelegates.collect {
      case declaration @ ABIDeclaration(_, _, _, MethodImplementation.NativeDelegate(target)) =>
        val targetSignature = renderCFunctionDeclaration(declaration, target)
        val publicSignature = renderCFunctionDeclaration(declaration, declaration.name)
        val arguments       = declaration.parameterNames.mkString(", ")
        val invocation      =
          if (cReturnType(declaration) == "void") s"  $target($arguments);"
          else s"  return $target($arguments);"
        s"extern $targetSignature;\n\n$publicSignature {\n$invocation\n}"
    }
    s"""|#include <stdint.h>
        |#include <cuda.h>
        |
        |#if defined(__GNUC__)
        |#define GEMSTONE_CUDA_WEAK __attribute__((weak))
        |#else
        |#define GEMSTONE_CUDA_WEAK
        |#endif
        |
        |typedef struct GemstoneDim3 { uint32_t x; uint32_t y; uint32_t z; } GemstoneDim3;
        |
        |/* Exact ABI-presence stubs. Trait methods opt into RPC or a native delegate. */
        |${stubs.mkString("\n\n")}
        |
        |/* Thin wrappers generated from retained delegate[...] method bodies. */
        |${delegates.mkString("\n\n")}
        |""".stripMargin
  }

  private def officialCUDADeclarations(manifest: Path): Vector[ABIDeclaration] = {
    require(Files.isRegularFile(manifest), s"official API header manifest is absent: $manifest")
    val document = ujson.read(Files.readString(manifest, StandardCharsets.UTF_8))
    document("surfaces").arr.iterator
      .find(_("name").str == "cuda-driver")
      .getOrElse(throw IllegalStateException("cuda-driver is absent from the official API header manifest"))
      .apply("functions")
      .arr
      .iterator
      .map { function =>
        ABIDeclaration(
          function("name").str,
          function("functionType").str,
          Col.from(function("parameters").arr.iterator.map(_("name").str)),
          MethodImplementation.Unsupported
        )
      }
      .toVector
  }

  private def renderCFunctionDeclaration(declaration: ABIDeclaration, name: String): String = {
    val open = declaration.functionType.indexOf('(')
    require(open > 0 && declaration.functionType.endsWith(")"), s"invalid C function type: ${declaration.functionType}")
    val returnType        = declaration.functionType.substring(0, open).trim
    val encodedParameters = declaration.functionType.substring(open + 1, declaration.functionType.length - 1).trim
    val parameterTypes    =
      if (encodedParameters == "void" || encodedParameters.isEmpty) Vector.empty
      else splitCParameters(encodedParameters)
    require(
      parameterTypes.size == declaration.parameterNames.size,
      s"C/Scala parameter count mismatch for ${declaration.name}: ${declaration.functionType} / ${declaration.parameterNames.mkString(", ")}"
    )
    val parameters =
      if (parameterTypes.isEmpty) "void"
      else parameterTypes.zip(declaration.parameterNames).map { case (parameterType, parameterName) =>
        s"$parameterType $parameterName"
      }.mkString(", ")
    s"$returnType CUDAAPI $name($parameters)"
  }

  private def cReturnType(declaration: ABIDeclaration): String = {
    val open = declaration.functionType.indexOf('(')
    require(open > 0, s"invalid C function type: ${declaration.functionType}")
    declaration.functionType.substring(0, open).trim
  }

  private def splitCParameters(parameters: String): Vector[String] = {
    val (start, _, result) = parameters.indices.foldLeft((0, 0, Vector.empty[String])) {
      case ((start, depth, result), position) =>
        parameters.charAt(position) match {
          case '('               => (start, depth + 1, result)
          case ')'               => (start, depth - 1, result)
          case ',' if depth == 0 => (position + 1, depth, result :+ parameters.substring(start, position).trim)
          case _                 => (start, depth, result)
        }
    }
    result :+ parameters.substring(start).trim
  }

  private def renderCUDASurfaceInclude: String = {
    def renderMacro(name: String, entries: Vector[String]): String = {
      val body = entries.zipWithIndex.map { case (entry, index) =>
        val continuation = if (index == entries.size - 1) "" else s" ${'\\'}"
        s"  X($entry)$continuation"
      }
      s"#define $name(X) ${'\\'}\n${body.mkString("\n")}"
    }

    s"""|#ifndef GEMSTONE_CUDA_DRIVER_SURFACE_INC
        |#define GEMSTONE_CUDA_DRIVER_SURFACE_INC
        |
        |/* Generated from the concrete and abstract methods of net.kurobako.gemstone.cuda.CUDA. */
        |${renderMacro("GEMSTONE_CUDA_DRIVER_API", cudaDriverSurface)}
        |
        |#endif
        |""".stripMargin
  }

  private def renderCUDASurfaceManifest: String = {
    val wired     = cudaRPC.methods.map(_.name).filterNot(_.startsWith("cuda")).toSet
    val functions = cudaDriverSurface.map { name =>
      val state = if (wired(name)) "wired" else "stub"
      s"""|    {"name":"$name","transport":"$state"}""".stripMargin
    }
    s"""|{"profile":"cuda-${CUDA.HeaderVersion}","driverCount":${cudaDriverSurface.size},"functions":[
        |${functions.mkString(",\n")}
        |]}
        |""".stripMargin
  }

  private def renderCUDARPCInclude: String = {
    val methods = cudaRPC.methods.zipWithIndex.map { case (method, index) =>
      val continuation = if (index == cudaRPC.methods.size - 1) "" else s" ${'\\'}"
      val high         = f"${method.messageId.high}%016x"
      val low          = f"${method.messageId.low}%016x"
      s"  X(${method.name}, UINT64_C(0x$high), UINT64_C(0x$low))$continuation"
    }
    s"""|#ifndef GEMSTONE_CUDA_RPC_INC
        |#define GEMSTONE_CUDA_RPC_INC
        |
        |#include <stdint.h>
        |
        |#define GEMSTONE_CUDA_RPC_SCHEMA_FINGERPRINT "${cudaRPC.fingerprint}"
        |#define GEMSTONE_CUDA_RPC_METHODS(X) ${'\\'}
        |${methods.mkString("\n")}
        |
        |#endif
        |""".stripMargin
  }

  private def renderCUDARPCClient: String = {
    val ids = cudaRPC.methods.filterNot(_.name.startsWith("cuda")).map { method =>
      val high = f"${method.messageId.high}%016x"
      val low  = f"${method.messageId.low}%016x"
      s"static const uint64_t gemstone_${method.name}_high = UINT64_C(0x$high);\nstatic const uint64_t gemstone_${method.name}_low = UINT64_C(0x$low);"
    }
    s"""|#include "gemstone_cuda_rpc.inc"
        |#include "gemstone_status.h"
        |#include "gemstone_cuda_compat.h"
        |#include "gemstone_rpc_transport.h"
        |
        |#include <pthread.h>
        |#include <stddef.h>
        |#include <stdint.h>
        |#include <stdlib.h>
        |#include <string.h>
        |
        |${ids.mkString("\n")}
        |
        |static void gemstone_put_u32(uint8_t *destination, uint32_t value) {
        |  destination[0] = (uint8_t)(value >> 24);
        |  destination[1] = (uint8_t)(value >> 16);
        |  destination[2] = (uint8_t)(value >> 8);
        |  destination[3] = (uint8_t)value;
        |}
        |
        |static void gemstone_put_u64(uint8_t *destination, uint64_t value) {
        |  gemstone_put_u32(destination, (uint32_t)(value >> 32));
        |  gemstone_put_u32(destination + 4, (uint32_t)value);
        |}
        |
        |static uint32_t gemstone_get_u32(const uint8_t *source) {
        |  return ((uint32_t)source[0] << 24) | ((uint32_t)source[1] << 16) | ((uint32_t)source[2] << 8) | (uint32_t)source[3];
        |}
        |
        |static uint64_t gemstone_get_u64(const uint8_t *source) {
        |  return ((uint64_t)gemstone_get_u32(source) << 32) | gemstone_get_u32(source + 4);
        |}
        |
        |static size_t gemstone_bounded_string_length(const char *value, size_t limit) {
        |  size_t size = 0;
        |  while (size < limit && value[size] != '\\0') ++size;
        |  return size;
        |}
        |
        |static int gemstone_is_probably_textual_ptx(const void *image) {
        |  const unsigned char first = *(const unsigned char *)image;
        |  return first == '\\0' || first == '.' || first == '/' || first == ' ' ||
        |      first == '\\t' || first == '\\r' || first == '\\n';
        |}
        |
        |typedef struct GemstoneKernelSignature {
        |  uint64_t function;
        |  uint64_t module;
        |  uint32_t count;
        |  uint32_t *widths;
        |  struct GemstoneKernelSignature *next;
        |} GemstoneKernelSignature;
        |
        |static pthread_mutex_t gemstone_kernel_mutex = PTHREAD_MUTEX_INITIALIZER;
        |static GemstoneKernelSignature *gemstone_kernel_signatures = NULL;
        |
        |static int gemstone_store_kernel_signature(
        |    uint64_t function,
        |    uint64_t module,
        |    const uint8_t *widths,
        |    uint32_t count) {
        |  GemstoneKernelSignature *entry = (GemstoneKernelSignature *)calloc(1, sizeof(*entry));
        |  if (entry == NULL) return -1;
        |  if (count != 0) {
        |    entry->widths = (uint32_t *)malloc((size_t)count * sizeof(*entry->widths));
        |    if (entry->widths == NULL) { free(entry); return -1; }
        |    for (uint32_t index = 0; index < count; ++index)
        |      entry->widths[index] = gemstone_get_u32(widths + (size_t)index * 4);
        |  }
        |  entry->function = function;
        |  entry->module = module;
        |  entry->count = count;
        |  pthread_mutex_lock(&gemstone_kernel_mutex);
        |  entry->next = gemstone_kernel_signatures;
        |  gemstone_kernel_signatures = entry;
        |  pthread_mutex_unlock(&gemstone_kernel_mutex);
        |  return 0;
        |}
        |
        |static void gemstone_forget_module(uint64_t module) {
        |  pthread_mutex_lock(&gemstone_kernel_mutex);
        |  GemstoneKernelSignature **cursor = &gemstone_kernel_signatures;
        |  while (*cursor != NULL) {
        |    GemstoneKernelSignature *entry = *cursor;
        |    if (entry->module == module) {
        |      *cursor = entry->next;
        |      free(entry->widths);
        |      free(entry);
        |    } else cursor = &entry->next;
        |  }
        |  pthread_mutex_unlock(&gemstone_kernel_mutex);
        |}
        |
        |static int32_t gemstone_call(
        |    uint64_t high,
        |    uint64_t low,
        |    const char *name,
        |    const uint8_t *payload,
        |    uint32_t payload_size,
        |    GemstoneRpcBuffer *response) {
        |  int32_t status = GEMSTONE_STATUS_INITIALIZATION_ERROR;
        |  if (gemstone_rpc_call(high, low, name, payload, payload_size, &status, response) != 0) return GEMSTONE_STATUS_INITIALIZATION_ERROR;
        |  if (status != GEMSTONE_STATUS_SUCCESS) gemstone_rpc_buffer_release(response);
        |  return status;
        |}
        |
        |static int32_t gemstone_expect_unit(int32_t status, GemstoneRpcBuffer *response) {
        |  if (status == GEMSTONE_STATUS_SUCCESS && response->size != 0) status = GEMSTONE_STATUS_UNKNOWN;
        |  gemstone_rpc_buffer_release(response);
        |  return status;
        |}
        |
        |static int32_t gemstone_expect_u32(int32_t status, GemstoneRpcBuffer *response, uint32_t *value) {
        |  if (status == GEMSTONE_STATUS_SUCCESS) {
        |    if (response->size != 4 || value == NULL) status = GEMSTONE_STATUS_UNKNOWN;
        |    else *value = gemstone_get_u32(response->data);
        |  }
        |  gemstone_rpc_buffer_release(response);
        |  return status;
        |}
        |
        |static int32_t gemstone_expect_u64(int32_t status, GemstoneRpcBuffer *response, uint64_t *value) {
        |  if (status == GEMSTONE_STATUS_SUCCESS) {
        |    if (response->size != 8 || value == NULL) status = GEMSTONE_STATUS_UNKNOWN;
        |    else *value = gemstone_get_u64(response->data);
        |  }
        |  gemstone_rpc_buffer_release(response);
        |  return status;
        |}
        |
        |int32_t cuInit(uint32_t flags) {
        |  uint8_t payload[4];
        |  gemstone_put_u32(payload, flags);
        |  GemstoneRpcBuffer response = {0};
        |  return gemstone_expect_unit(gemstone_call(gemstone_cuInit_high, gemstone_cuInit_low, "cuInit", payload, sizeof(payload), &response), &response);
        |}
        |
        |int32_t cuDriverGetVersion(int32_t *version) {
        |  if (version == NULL) return GEMSTONE_STATUS_INVALID_VALUE;
        |  GemstoneRpcBuffer response = {0};
        |  return gemstone_expect_u32(gemstone_call(gemstone_cuDriverGetVersion_high, gemstone_cuDriverGetVersion_low, "cuDriverGetVersion", NULL, 0, &response), &response, (uint32_t *)version);
        |}
        |
        |int32_t cuDeviceGetCount(int32_t *count) {
        |  if (count == NULL) return GEMSTONE_STATUS_INVALID_VALUE;
        |  GemstoneRpcBuffer response = {0};
        |  return gemstone_expect_u32(gemstone_call(gemstone_cuDeviceGetCount_high, gemstone_cuDeviceGetCount_low, "cuDeviceGetCount", NULL, 0, &response), &response, (uint32_t *)count);
        |}
        |
        |int32_t cuDeviceGet(int32_t *device, int32_t ordinal) {
        |  if (device == NULL) return GEMSTONE_STATUS_INVALID_VALUE;
        |  uint8_t payload[4];
        |  gemstone_put_u32(payload, (uint32_t)ordinal);
        |  GemstoneRpcBuffer response = {0};
        |  return gemstone_expect_u32(gemstone_call(gemstone_cuDeviceGet_high, gemstone_cuDeviceGet_low, "cuDeviceGet", payload, sizeof(payload), &response), &response, (uint32_t *)device);
        |}
        |
        |_Thread_local void *gemstone_current_context = NULL;
        |
        |int32_t cuCtxCreate_v2(void **context, uint32_t flags, int32_t device) {
        |  if (context == NULL) return GEMSTONE_STATUS_INVALID_VALUE;
        |  uint8_t payload[8];
        |  gemstone_put_u32(payload, flags);
        |  gemstone_put_u32(payload + 4, (uint32_t)device);
        |  uint64_t handle = 0;
        |  GemstoneRpcBuffer response = {0};
        |  const int32_t status = gemstone_expect_u64(gemstone_call(gemstone_cuCtxCreate_v2_high, gemstone_cuCtxCreate_v2_low, "cuCtxCreate_v2", payload, sizeof(payload), &response), &response, &handle);
        |  if (status == GEMSTONE_STATUS_SUCCESS) {
        |    *context = (void *)(uintptr_t)handle;
        |    gemstone_current_context = *context;
        |  }
        |  return status;
        |}
        |
        |#if defined(_WIN32)
        |#define GEMSTONE_CUDA_API __stdcall
        |#else
        |#define GEMSTONE_CUDA_API
        |#endif
        |
        |int32_t GEMSTONE_CUDA_API gemstone_cuCtxCreate_v4(
        |    void **context,
        |    void *parameters,
        |    uint32_t flags,
        |    int32_t device) {
        |  (void)parameters;
        |  return cuCtxCreate_v2(context, flags, device);
        |}
        |
        |int32_t cuCtxDestroy_v2(void *context) {
        |  uint8_t payload[8];
        |  gemstone_put_u64(payload, (uint64_t)(uintptr_t)context);
        |  GemstoneRpcBuffer response = {0};
        |  const int32_t status = gemstone_expect_unit(gemstone_call(gemstone_cuCtxDestroy_v2_high, gemstone_cuCtxDestroy_v2_low, "cuCtxDestroy_v2", payload, sizeof(payload), &response), &response);
        |  if (status == GEMSTONE_STATUS_SUCCESS && context == gemstone_current_context) gemstone_current_context = NULL;
        |  return status;
        |}
        |
        |int32_t cuCtxSynchronize(void) {
        |  GemstoneRpcBuffer response = {0};
        |  const int32_t status = gemstone_expect_unit(
        |      gemstone_call(gemstone_cuCtxSynchronize_high, gemstone_cuCtxSynchronize_low, "cuCtxSynchronize", NULL, 0, &response),
        |      &response);
        |  if (status == GEMSTONE_STATUS_SUCCESS) return gemstone_cuda_managed_synchronize_from_device();
        |  return status;
        |}
        |
        |int32_t cuLaunchHostFunc(void *stream, GemstoneHostCallback callback, void *user_data) {
        |  const uint64_t callback_id = gemstone_rpc_register_host_callback(callback, user_data);
        |  if (callback_id == 0) return GEMSTONE_STATUS_INVALID_VALUE;
        |  uint8_t payload[24];
        |  gemstone_put_u64(payload, (uint64_t)(uintptr_t)stream);
        |  gemstone_put_u64(payload + 8, callback_id);
        |  gemstone_put_u64(payload + 16, callback_id);
        |  GemstoneRpcBuffer response = {0};
        |  const int32_t status = gemstone_expect_unit(
        |      gemstone_call(gemstone_cuLaunchHostFunc_high, gemstone_cuLaunchHostFunc_low, "cuLaunchHostFunc", payload, sizeof(payload), &response),
        |      &response);
        |  if (status != GEMSTONE_STATUS_SUCCESS) gemstone_rpc_unregister_callback(callback_id);
        |  return status;
        |}
        |
        |int32_t cuStreamAddCallback(
        |    void *stream,
        |    GemstoneStreamCallback callback,
        |    void *user_data,
        |    uint32_t flags) {
        |  const uint64_t callback_id = gemstone_rpc_register_stream_callback(callback, user_data);
        |  if (callback_id == 0) return GEMSTONE_STATUS_INVALID_VALUE;
        |  uint8_t payload[28];
        |  gemstone_put_u64(payload, (uint64_t)(uintptr_t)stream);
        |  gemstone_put_u64(payload + 8, callback_id);
        |  gemstone_put_u64(payload + 16, callback_id);
        |  gemstone_put_u32(payload + 24, flags);
        |  GemstoneRpcBuffer response = {0};
        |  const int32_t status = gemstone_expect_unit(
        |      gemstone_call(gemstone_cuStreamAddCallback_high, gemstone_cuStreamAddCallback_low, "cuStreamAddCallback", payload, sizeof(payload), &response),
        |      &response);
        |  if (status != GEMSTONE_STATUS_SUCCESS) gemstone_rpc_unregister_callback(callback_id);
        |  return status;
        |}
        |
        |int32_t cuMemAlloc_v2(uint64_t *pointer, size_t size) {
        |  if (pointer == NULL) return GEMSTONE_STATUS_INVALID_VALUE;
        |  uint8_t payload[8];
        |  gemstone_put_u64(payload, (uint64_t)size);
        |  GemstoneRpcBuffer response = {0};
        |  return gemstone_expect_u64(gemstone_call(gemstone_cuMemAlloc_v2_high, gemstone_cuMemAlloc_v2_low, "cuMemAlloc_v2", payload, sizeof(payload), &response), &response, pointer);
        |}
        |
        |int32_t cuMemFree_v2(uint64_t pointer) {
        |  if (pointer == 0) return GEMSTONE_STATUS_SUCCESS;
        |  int found = 0;
        |  const int32_t managed_status = gemstone_cuda_managed_free(pointer, &found);
        |  if (found) return managed_status;
        |  uint8_t payload[8];
        |  gemstone_put_u64(payload, pointer);
        |  GemstoneRpcBuffer response = {0};
        |  return gemstone_expect_unit(gemstone_call(gemstone_cuMemFree_v2_high, gemstone_cuMemFree_v2_low, "cuMemFree_v2", payload, sizeof(payload), &response), &response);
        |}
        |
        |int32_t cuMemcpyHtoD_v2(uint64_t destination, const void *source, size_t size) {
        |  if ((source == NULL && size != 0) || size > GEMSTONE_RPC_MAXIMUM_PAYLOAD_SIZE - 20) return GEMSTONE_STATUS_INVALID_VALUE;
        |  const uint32_t payload_size = (uint32_t)size + 20;
        |  uint8_t *payload = (uint8_t *)malloc(payload_size);
        |  if (payload == NULL) return GEMSTONE_STATUS_UNKNOWN;
        |  gemstone_put_u64(payload, destination);
        |  gemstone_put_u32(payload + 8, (uint32_t)size);
        |  if (size != 0) memcpy(payload + 12, source, size);
        |  gemstone_put_u64(payload + 12 + size, (uint64_t)size);
        |  GemstoneRpcBuffer response = {0};
        |  const int32_t status = gemstone_expect_unit(gemstone_call(gemstone_cuMemcpyHtoD_v2_high, gemstone_cuMemcpyHtoD_v2_low, "cuMemcpyHtoD_v2", payload, payload_size, &response), &response);
        |  free(payload);
        |  if (status == GEMSTONE_STATUS_SUCCESS) gemstone_cuda_managed_copy_from_host(destination, source, size);
        |  return status;
        |}
        |
        |int32_t cuMemcpyDtoH_v2(void *destination, uint64_t source, size_t size) {
        |  if ((destination == NULL && size != 0) || size > GEMSTONE_RPC_MAXIMUM_PAYLOAD_SIZE - 4) return GEMSTONE_STATUS_INVALID_VALUE;
        |  if (gemstone_cuda_managed_copy_to_host(destination, source, size)) return GEMSTONE_STATUS_SUCCESS;
        |  uint8_t payload[16];
        |  gemstone_put_u64(payload, source);
        |  gemstone_put_u64(payload + 8, (uint64_t)size);
        |  GemstoneRpcBuffer response = {0};
        |  int32_t status = gemstone_call(gemstone_cuMemcpyDtoH_v2_high, gemstone_cuMemcpyDtoH_v2_low, "cuMemcpyDtoH_v2", payload, sizeof(payload), &response);
        |  if (status == GEMSTONE_STATUS_SUCCESS) {
        |    if (response.size < 4 || gemstone_get_u32(response.data) != size || response.size != size + 4) status = GEMSTONE_STATUS_UNKNOWN;
        |    else if (size != 0) memcpy(destination, response.data + 4, size);
        |  }
        |  gemstone_rpc_buffer_release(&response);
        |  return status;
        |}
        |
        |int32_t cuMemcpyDtoD_v2(uint64_t destination, uint64_t source, size_t size) {
        |  uint8_t payload[24];
        |  gemstone_put_u64(payload, destination);
        |  gemstone_put_u64(payload + 8, source);
        |  gemstone_put_u64(payload + 16, (uint64_t)size);
        |  GemstoneRpcBuffer response = {0};
        |  return gemstone_expect_unit(gemstone_call(gemstone_cuMemcpyDtoD_v2_high, gemstone_cuMemcpyDtoD_v2_low, "cuMemcpyDtoD_v2", payload, sizeof(payload), &response), &response);
        |}
        |
        |int32_t cuMemsetD8_v2(uint64_t destination, uint8_t value, size_t count) {
        |  uint8_t payload[17];
        |  gemstone_put_u64(payload, destination);
        |  payload[8] = value;
        |  gemstone_put_u64(payload + 9, (uint64_t)count);
        |  GemstoneRpcBuffer response = {0};
        |  return gemstone_expect_unit(gemstone_call(gemstone_cuMemsetD8_v2_high, gemstone_cuMemsetD8_v2_low, "cuMemsetD8_v2", payload, sizeof(payload), &response), &response);
        |}
        |
        |int32_t gemstone_managed_register_remote(uint64_t pointer, size_t size) {
        |  uint8_t payload[16];
        |  gemstone_put_u64(payload, pointer);
        |  gemstone_put_u64(payload + 8, (uint64_t)size);
        |  GemstoneRpcBuffer response = {0};
        |  return gemstone_expect_unit(
        |      gemstone_call(
        |          gemstone___cudaGemstoneManagedRegister_high,
        |          gemstone___cudaGemstoneManagedRegister_low,
        |          "__cudaGemstoneManagedRegister",
        |          payload,
        |          sizeof(payload),
        |          &response),
        |      &response);
        |}
        |
        |int32_t gemstone_managed_unregister_remote(uint64_t pointer) {
        |  uint8_t payload[8];
        |  gemstone_put_u64(payload, pointer);
        |  GemstoneRpcBuffer response = {0};
        |  return gemstone_expect_unit(
        |      gemstone_call(
        |          gemstone___cudaGemstoneManagedUnregister_high,
        |          gemstone___cudaGemstoneManagedUnregister_low,
        |          "__cudaGemstoneManagedUnregister",
        |          payload,
        |          sizeof(payload),
        |          &response),
        |      &response);
        |}
        |
        |int32_t gemstone_register_global_remote(
        |    uint64_t module,
        |    const char *name,
        |    const void *initial,
        |    size_t size) {
        |  if (module == 0 || name == NULL || (initial == NULL && size != 0)) return GEMSTONE_STATUS_INVALID_VALUE;
        |  const size_t name_size = gemstone_bounded_string_length(name, 4096);
        |  if (name_size == 0 || name_size == 4096 ||
        |      size > GEMSTONE_RPC_MAXIMUM_PAYLOAD_SIZE - 16 - name_size) return GEMSTONE_STATUS_INVALID_VALUE;
        |  const uint32_t payload_size = (uint32_t)(16 + name_size + size);
        |  uint8_t *payload = (uint8_t *)malloc(payload_size);
        |  if (payload == NULL) return GEMSTONE_STATUS_UNKNOWN;
        |  gemstone_put_u64(payload, module);
        |  gemstone_put_u32(payload + 8, (uint32_t)name_size);
        |  memcpy(payload + 12, name, name_size);
        |  gemstone_put_u32(payload + 12 + name_size, (uint32_t)size);
        |  if (size != 0) memcpy(payload + 16 + name_size, initial, size);
        |  GemstoneRpcBuffer response = {0};
        |  const int32_t status = gemstone_expect_unit(
        |      gemstone_call(
        |          gemstone___cudaGemstoneRegisterGlobal_high,
        |          gemstone___cudaGemstoneRegisterGlobal_low,
        |          "__cudaGemstoneRegisterGlobal",
        |          payload,
        |          payload_size,
        |          &response),
        |      &response);
        |  free(payload);
        |  return status;
        |}
        |
        |int32_t cuModuleLoadData(void **module, const void *image) {
        |  if (module == NULL || image == NULL) return GEMSTONE_STATUS_INVALID_VALUE;
        |  if (!gemstone_is_probably_textual_ptx(image)) {
        |    const char *ptx = gemstone_cuda_find_uncompressed_ptx(image);
        |    if (ptx == NULL) return GEMSTONE_STATUS_NOT_SUPPORTED;
        |    image = ptx;
        |  }
        |  const size_t size = gemstone_bounded_string_length((const char *)image, GEMSTONE_RPC_MAXIMUM_PAYLOAD_SIZE);
        |  if (size == GEMSTONE_RPC_MAXIMUM_PAYLOAD_SIZE || size > GEMSTONE_RPC_MAXIMUM_PAYLOAD_SIZE - 4) {
        |    return GEMSTONE_STATUS_INVALID_VALUE;
        |  }
        |  const uint32_t payload_size = (uint32_t)size + 4;
        |  uint8_t *payload = (uint8_t *)malloc(payload_size);
        |  if (payload == NULL) return GEMSTONE_STATUS_UNKNOWN;
        |  gemstone_put_u32(payload, (uint32_t)size);
        |  memcpy(payload + 4, image, size);
        |  uint64_t handle = 0;
        |  GemstoneRpcBuffer response = {0};
        |  const int32_t status = gemstone_expect_u64(
        |      gemstone_call(gemstone_cuModuleLoadData_high, gemstone_cuModuleLoadData_low, "cuModuleLoadData", payload, payload_size, &response),
        |      &response,
        |      &handle);
        |  free(payload);
        |  if (status == GEMSTONE_STATUS_SUCCESS) *module = (void *)(uintptr_t)handle;
        |  return status;
        |}
        |
        |int32_t cuModuleGetFunction(void **function, void *module, const char *name) {
        |  if (function == NULL || module == NULL || name == NULL) return GEMSTONE_STATUS_INVALID_VALUE;
        |  const size_t name_size = gemstone_bounded_string_length(name, 4096);
        |  if (name_size == 0 || name_size == 4096) return GEMSTONE_STATUS_INVALID_VALUE;
        |  const uint32_t payload_size = (uint32_t)name_size + 12;
        |  uint8_t *payload = (uint8_t *)malloc(payload_size);
        |  if (payload == NULL) return GEMSTONE_STATUS_UNKNOWN;
        |  gemstone_put_u64(payload, (uint64_t)(uintptr_t)module);
        |  gemstone_put_u32(payload + 8, (uint32_t)name_size);
        |  memcpy(payload + 12, name, name_size);
        |  GemstoneRpcBuffer response = {0};
        |  int32_t status = gemstone_call(
        |      gemstone_cuModuleGetFunction_high,
        |      gemstone_cuModuleGetFunction_low,
        |      "cuModuleGetFunction",
        |      payload,
        |      payload_size,
        |      &response);
        |  free(payload);
        |  if (status == GEMSTONE_STATUS_SUCCESS) {
        |    if (response.size < 12) status = GEMSTONE_STATUS_UNKNOWN;
        |    else {
        |      const uint64_t handle = gemstone_get_u64(response.data);
        |      const uint32_t count = gemstone_get_u32(response.data + 8);
        |      if (count > 4096 || response.size != 12 + (size_t)count * 4 ||
        |          gemstone_store_kernel_signature(handle, (uint64_t)(uintptr_t)module, response.data + 12, count) != 0) {
        |        status = GEMSTONE_STATUS_UNKNOWN;
        |      } else *function = (void *)(uintptr_t)handle;
        |    }
        |  }
        |  gemstone_rpc_buffer_release(&response);
        |  return status;
        |}
        |
        |int32_t cuModuleGetGlobal_v2(uint64_t *pointer, size_t *bytes, void *module, const char *name) {
        |  if (pointer == NULL || module == NULL || name == NULL) return GEMSTONE_STATUS_INVALID_VALUE;
        |  size_t ignored_bytes = 0;
        |  if (bytes == NULL) bytes = &ignored_bytes;
        |  const size_t name_size = gemstone_bounded_string_length(name, 4096);
        |  if (name_size == 0 || name_size == 4096) return GEMSTONE_STATUS_INVALID_VALUE;
        |  const uint32_t payload_size = (uint32_t)name_size + 12;
        |  uint8_t *payload = (uint8_t *)malloc(payload_size);
        |  if (payload == NULL) return GEMSTONE_STATUS_UNKNOWN;
        |  gemstone_put_u64(payload, (uint64_t)(uintptr_t)module);
        |  gemstone_put_u32(payload + 8, (uint32_t)name_size);
        |  memcpy(payload + 12, name, name_size);
        |  GemstoneRpcBuffer response = {0};
        |  int32_t status = gemstone_call(
        |      gemstone_cuModuleGetGlobal_v2_high,
        |      gemstone_cuModuleGetGlobal_v2_low,
        |      "cuModuleGetGlobal_v2",
        |      payload,
        |      payload_size,
        |      &response);
        |  free(payload);
        |  if (status == GEMSTONE_STATUS_SUCCESS) {
        |    if (response.size != 16) status = GEMSTONE_STATUS_UNKNOWN;
        |    else {
        |      *pointer = gemstone_get_u64(response.data);
        |      *bytes = (size_t)gemstone_get_u64(response.data + 8);
        |    }
        |  }
        |  gemstone_rpc_buffer_release(&response);
        |  return status;
        |}
        |
        |int32_t cuModuleUnload(void *module) {
        |  if (module == NULL) return GEMSTONE_STATUS_INVALID_VALUE;
        |  uint8_t payload[8];
        |  gemstone_put_u64(payload, (uint64_t)(uintptr_t)module);
        |  GemstoneRpcBuffer response = {0};
        |  const int32_t status = gemstone_expect_unit(
        |      gemstone_call(gemstone_cuModuleUnload_high, gemstone_cuModuleUnload_low, "cuModuleUnload", payload, sizeof(payload), &response),
        |      &response);
        |  if (status == GEMSTONE_STATUS_SUCCESS) gemstone_forget_module((uint64_t)(uintptr_t)module);
        |  return status;
        |}

        |int32_t cuLaunchKernel(
        |    void *function,
        |    uint32_t grid_x,
        |    uint32_t grid_y,
        |    uint32_t grid_z,
        |    uint32_t block_x,
        |    uint32_t block_y,
        |    uint32_t block_z,
        |    uint32_t shared_memory_bytes,
        |    void *stream,
        |    void **kernel_params,
        |    void **extra) {
        |  if (function == NULL || grid_x == 0 || grid_y == 0 || grid_z == 0 ||
        |      block_x == 0 || block_y == 0 || block_z == 0 || extra != NULL) {
        |    return GEMSTONE_STATUS_INVALID_VALUE;
        |  }
        |  int32_t managed_status = gemstone_cuda_managed_synchronize_to_device();
        |  if (managed_status != GEMSTONE_STATUS_SUCCESS) return managed_status;
        |  (void)stream;
        |  pthread_mutex_lock(&gemstone_kernel_mutex);
        |  GemstoneKernelSignature *signature = gemstone_kernel_signatures;
        |  while (signature != NULL && signature->function != (uint64_t)(uintptr_t)function) signature = signature->next;
        |  if (signature == NULL || (signature->count != 0 && kernel_params == NULL)) {
        |    pthread_mutex_unlock(&gemstone_kernel_mutex);
        |    return GEMSTONE_STATUS_INVALID_VALUE;
        |  }
        |  size_t payload_size = 49;
        |  for (uint32_t index = 0; index < signature->count; ++index) {
        |    const uint32_t width = signature->widths[index];
        |    if (width == 0 || kernel_params[index] == NULL ||
        |        payload_size > GEMSTONE_RPC_MAXIMUM_PAYLOAD_SIZE - 4 - width) {
        |      pthread_mutex_unlock(&gemstone_kernel_mutex);
        |      return GEMSTONE_STATUS_INVALID_VALUE;
        |    }
        |    payload_size += 4 + width;
        |  }
        |  uint8_t *payload = (uint8_t *)malloc(payload_size);
        |  if (payload == NULL) { pthread_mutex_unlock(&gemstone_kernel_mutex); return GEMSTONE_STATUS_UNKNOWN; }
        |  gemstone_put_u64(payload, (uint64_t)(uintptr_t)function);
        |  gemstone_put_u32(payload + 8, grid_x);
        |  gemstone_put_u32(payload + 12, grid_y);
        |  gemstone_put_u32(payload + 16, grid_z);
        |  gemstone_put_u32(payload + 20, block_x);
        |  gemstone_put_u32(payload + 24, block_y);
        |  gemstone_put_u32(payload + 28, block_z);
        |  gemstone_put_u32(payload + 32, shared_memory_bytes);
        |  gemstone_put_u64(payload + 36, (uint64_t)(uintptr_t)stream);
        |  gemstone_put_u32(payload + 44, signature->count);
        |  size_t offset = 48;
        |  for (uint32_t index = 0; index < signature->count; ++index) {
        |    const uint32_t width = signature->widths[index];
        |    gemstone_put_u32(payload + offset, width);
        |    memcpy(payload + offset + 4, kernel_params[index], width);
        |    offset += 4 + width;
        |  }
        |  payload[offset] = 0;
        |  pthread_mutex_unlock(&gemstone_kernel_mutex);
        |  GemstoneRpcBuffer response = {0};
        |  int32_t status = gemstone_expect_unit(
        |      gemstone_call(gemstone_cuLaunchKernel_high, gemstone_cuLaunchKernel_low, "cuLaunchKernel", payload, (uint32_t)payload_size, &response),
        |      &response);
        |  free(payload);
        |  if (status == GEMSTONE_STATUS_SUCCESS) {
        |    gemstone_cuda_managed_mark_device_dirty();
        |    status = gemstone_cuda_managed_synchronize_from_device();
        |  }
        |  return status;
        |}
        |
        |""".stripMargin
  }

  private def renderCUDARPCManifest: String = {
    val methods = cudaRPC.methods.map { method =>
      val parameters = method.parameters.map(parameter =>
        s"{\"name\":\"${parameter.name}\",\"wireType\":\"${parameter.wireType.protocolName}\"}"
      )
      s"""|    {"name":"${method.name}","messageId":"${method.messageId.hex}","parameters":[${parameters.mkString(
           ","
         )}],"result":"${method.result.protocolName}"}""".stripMargin
    }
    s"""|{"interface":"${cudaRPC.interfaceName}","identity":"exact-c-function-name","messageId":"sha256-128(function-name-utf8)","fingerprint":"${cudaRPC.fingerprint}","methods":[
        |${methods.mkString(",\n")}
        |]}
        |""".stripMargin
  }

  private def renderCUDARPCScala: String = {
    val ids = cudaRPC.methods.map { method =>
      val high = s"0x${f"${method.messageId.high}%016x"}L"
      val low  = s"0x${f"${method.messageId.low}%016x"}L"
      s"  private val ${method.name}Id = MessageId($high, $low)"
    }
    val clients = cudaRPC.methods.map { method =>
      val parameters = method.parameters.map(parameter => s"${parameter.name}: ${scalaParameterType(parameter)}")
      val inputs     = method.parameters.collect {
        case parameter if parameter.direction != WireDirection.Out => encodeClientInput(parameter)
      }
      val outputs       = method.parameters.filter(_.direction != WireDirection.In)
      val outputPattern = outputs.indices.map(index => decodePattern(outputs(index).wireType, s"output$index"))
      val assignments   = outputs.indices.map { index =>
        val parameter = outputs(index)
        if (parameter.wireType == WireType.ByteBuffer) s"writeBytes(${parameter.name}, output$index)"
        else s"writeOne(${parameter.name}, output$index)"
      }
      val outputHandling =
        if (outputs.isEmpty) "if (outputs.isEmpty) status else CUDAStatus.Unknown"
        else
          s"""|outputs match {
              |          case Col(${outputPattern.mkString(", ")}) =>
              |            if (${assignments.mkString(" && ")}) status else CUDAStatus.InvalidValue
              |          case _ => CUDAStatus.Unknown
              |        }""".stripMargin
      val encodedInputs = if (inputs.isEmpty) "Col.empty[WireValue]" else s"Col(${inputs.mkString(", ")})"
      s"""|  override def ${method.name}(${parameters.mkString(", ")}): CUDAStatus =
          |    transport.call(RPCRequest(${method.name}Id, "${method.name}", $encodedInputs)) match {
          |      case RPCResponse.Success(WireValue.CallResult(status, outputs)) if status.isSuccess =>
          |        ${outputHandling.linesIterator.mkString("\n        ")}
          |      case RPCResponse.Success(WireValue.CallResult(status, _)) => status
          |      case _ => CUDAStatus.Unknown
          |    }""".stripMargin
    }
    val dispatchCases = cudaRPC.methods.map { method =>
      val indexed         = method.parameters.zipWithIndex
      val inputParameters = indexed.filter(_._1.direction != WireDirection.Out)
      val patterns        = inputParameters.map { case (parameter, index) =>
        decodePattern(parameter.wireType, s"value$index")
      }
      val declarations = indexed.map { case (parameter, index) =>
        s"val argument$index = ${dispatcherArgument(parameter, index, indexed)}"
      }
      val arguments = indexed.indices.map(index => s"argument$index").mkString(", ")
      val outputs   = indexed.collect {
        case (parameter, index) if parameter.direction != WireDirection.In => encodeDispatcherOutput(parameter, index)
      }
      val declarationsBlock =
        if (declarations.isEmpty) ""
        else declarations.mkString("            ", "\n            ", "\n")
      s"""|      case `${method.name}Id` if request.functionName == "${method.name}" =>
          |        request.arguments match {
          |          case Col(${patterns.mkString(", ")}) =>
          |$declarationsBlock            val status = implementation.${method.name}($arguments)
          |            val outputs = if (status.isSuccess) ${
           if (outputs.isEmpty) "Col.empty[WireValue]" else s"Col(${outputs.mkString(", ")})"
         } else Col.empty[WireValue]
          |            RPCResponse.Success(WireValue.CallResult(status, outputs))
          |          case _ => invalid("invalid arguments for ${method.name}")
          |        }""".stripMargin
    }
    val schemaMethods = cudaRPC.methods.map { method =>
      val parameters = method.parameters.map { parameter =>
        s"RPCParameter(\"${parameter.name}\", WireType.${parameter.wireType}, WireDirection.${parameter.direction}, ${renderWireExtent(parameter.extent)})"
      }
      val encodedParameters =
        if (parameters.isEmpty) "Col.empty[RPCParameter]" else s"Col(${parameters.mkString(", ")})"
      s"RPCMethod(\"${method.name}\", ${method.name}Id, $encodedParameters, WireType.${method.result})"
    }
    s"""|package net.kurobako.gemstone.generated
        |
        |import net.kurobako.gemstone.Col
        |import net.kurobako.gemstone.abi.*
        |import net.kurobako.gemstone.cuda.*
        |import net.kurobako.gemstone.rpc.*
        |
        |object GeneratedCUDARPC {
        |  val SchemaFingerprint: String = "${cudaRPC.fingerprint}"
        |
        |${ids.mkString("\n")}
        |
        |  val Schema: RPCSchema = RPCSchema("${cudaRPC.interfaceName}", SchemaFingerprint, Col(
        |    ${schemaMethods.mkString(",\n    ")}
        |  ))
        |
        |  final class Client(transport: RPCTransport) extends CUDA {
        |    require(
        |      transport.schemaFingerprint == SchemaFingerprint,
        |      s"CUDA RPC schema mismatch: client=$$SchemaFingerprint server=$${transport.schemaFingerprint}"
        |    )
        |
        |${clients.map(_.linesIterator.map(line => s"  $line").mkString("\n")).mkString("\n\n")}
        |  }
        |
        |  object Dispatcher extends RPCDispatcher[CUDA] {
        |    override val schemaFingerprint: String = SchemaFingerprint
        |
        |    override def dispatch(request: RPCRequest, implementation: CUDA): RPCResponse =
        |      request.messageId match {
        |${dispatchCases.mkString("\n")}
        |        case _ => invalid(s"unknown or mismatched CUDA RPC function $${request.functionName}")
        |      }
        |  }
        |
        |  private def invalid(message: String): RPCResponse = RPCResponse.Failure(CUDAError(999, message))
        |
        |  private def writeOne[A, Mode <: Access, Size <: Extent](pointer: Pointer[A, Mode, Size], value: A)(using CanWrite[Mode], CanDereference[Size]): Boolean = {
        |    pointer.write(value)
        |    true
        |  }
        |
        |  private def writeBytes[Mode <: Access, Size <: Extent](pointer: Pointer[Byte, Mode, Size], value: Col[Byte])(using CanWrite[Mode], CanDereference[Size]): Boolean = {
        |    if (pointer.length != value.length) false
        |    else {
        |      value.indices.foreach(index => pointer.write(value(index), index))
        |      true
        |    }
        |  }
        |}
        |""".stripMargin
  }

  private def scalaParameterType(parameter: RPCParameter): String =
    if (parameter.extent == WireExtent.Value) scalaType(parameter.wireType)
    else {
      val element = if (parameter.wireType == WireType.ByteBuffer) "Byte" else scalaType(parameter.wireType)
      (parameter.direction, parameter.extent) match {
        case (WireDirection.In, WireExtent.CountedBy(name))    => s"InputBuffer[$element, \"$name\"]"
        case (WireDirection.Out, WireExtent.CountedBy(name))   => s"OutputBuffer[$element, \"$name\"]"
        case (WireDirection.InOut, WireExtent.CountedBy(name)) => s"InOutBuffer[$element, \"$name\"]"
        case (WireDirection.In, WireExtent.One)                => s"In[$element]"
        case (WireDirection.Out, WireExtent.One)               => s"Out[$element]"
        case (WireDirection.InOut, WireExtent.One)             => s"InOut[$element]"
        case (direction, extent)                               =>
          s"Pointer[$element, ${scalaAccess(direction)}, ${scalaExtent(extent)}]"
      }
    }

  private def scalaAccess(direction: WireDirection): String = direction match {
    case WireDirection.In    => "Read"
    case WireDirection.Out   => "Write"
    case WireDirection.InOut => "ReadWrite"
  }

  private def scalaExtent(extent: WireExtent): String = extent match {
    case WireExtent.Value           => sys.error("a value is not a pointer extent")
    case WireExtent.One             => "One"
    case WireExtent.Opaque          => "Opaque"
    case WireExtent.CountedBy(name) => s"CountedBy[\"$name\"]"
    case WireExtent.Fixed(count)    => s"Fixed[$count]"
  }

  private def renderWireExtent(extent: WireExtent): String = extent match {
    case WireExtent.Value           => "WireExtent.Value"
    case WireExtent.One             => "WireExtent.One"
    case WireExtent.Opaque          => "WireExtent.Opaque"
    case WireExtent.CountedBy(name) => s"WireExtent.CountedBy(\"$name\")"
    case WireExtent.Fixed(count)    => s"WireExtent.Fixed($count)"
  }

  private def encodeClientInput(parameter: RPCParameter): String =
    if (parameter.extent == WireExtent.Value) encodeWire(parameter.wireType, parameter.name)
    else if (parameter.wireType == WireType.ByteBuffer)
      s"WireValue.ByteBufferValue(${parameter.name}.snapshot.getOrElse(Col.empty[Byte]))"
    else {
      val value =
        s"${parameter.name}.snapshot.flatMap(_.headOption).getOrElse(throw IllegalArgumentException(\"${parameter.name} is uninitialized\"))"
      encodeWire(parameter.wireType, value)
    }

  private def dispatcherArgument(
      parameter: RPCParameter,
      index: Int,
      parameters: Col[(RPCParameter, Int)]
  ): String = parameter.direction match {
    case WireDirection.In if parameter.extent == WireExtent.Value =>
      decodeWireValue(parameter.wireType, s"value$index")
    case WireDirection.In if parameter.wireType == WireType.ByteBuffer =>
      s"Pointer.input[Byte, ${scalaExtent(parameter.extent)}](value$index)"
    case WireDirection.Out =>
      val element = if (parameter.wireType == WireType.ByteBuffer) "Byte" else scalaType(parameter.wireType)
      s"Pointer.output[$element, ${scalaExtent(parameter.extent)}](${extentLength(parameter.extent, parameters)})"
    case WireDirection.InOut if parameter.wireType == WireType.ByteBuffer =>
      s"Pointer.inputOutput[Byte, ${scalaExtent(parameter.extent)}](value$index)"
    case _ => sys.error(s"unsupported generated dispatcher parameter: $parameter")
  }

  private def extentLength(extent: WireExtent, parameters: Col[(RPCParameter, Int)]): String = extent match {
    case WireExtent.One             => "1"
    case WireExtent.Fixed(count)    => count.toString
    case WireExtent.CountedBy(name) =>
      parameters.find(_._1.name == name) match {
        case Some((parameter, index)) => parameter.wireType match {
            case WireType.Int32  => s"value$index"
            case WireType.UInt32 => s"value$index.toLong.toInt"
            case WireType.SizeT  => s"value$index.toLong.toInt"
            case other           => sys.error(s"unsupported count type $other for $name")
          }
        case None => sys.error(s"count parameter is absent: $name")
      }
    case WireExtent.Opaque => sys.error("cannot allocate an opaque output pointer")
    case WireExtent.Value  => sys.error("a value has no pointer length")
  }

  private def encodeDispatcherOutput(parameter: RPCParameter, index: Int): String =
    if (parameter.wireType == WireType.ByteBuffer)
      s"WireValue.ByteBufferValue(argument$index.snapshot.getOrElse(throw IllegalStateException(\"${parameter.name} was not initialized\")))"
    else {
      val value =
        s"argument$index.snapshot.flatMap(_.headOption).getOrElse(throw IllegalStateException(\"${parameter.name} was not initialized\"))"
      encodeWire(parameter.wireType, value)
    }

  private def decodeWireValue(wireType: WireType, value: String): String = wireType match {
    case WireType.NullableOpaquePointer =>
      s"if ($value) throw IllegalArgumentException(\"non-null opaque RPC pointers are unsupported\") else Nullable.Null"
    case _ => value
  }

  private def scalaType(wireType: WireType): String = wireType match {
    case WireType.UnitValue             => "Unit"
    case WireType.Int32                 => "Int"
    case WireType.UInt8                 => "UInt8"
    case WireType.UInt32                => "UInt32"
    case WireType.SizeT                 => "SizeT"
    case WireType.Device                => "Device"
    case WireType.DevicePtr             => "DevicePtr"
    case WireType.Context               => "Context"
    case WireType.CUDAModule            => "CUDAModule"
    case WireType.CUDAFunction          => "CUDAFunction"
    case WireType.HostInput             => "HostInput"
    case WireType.HostOutput            => "HostOutput"
    case WireType.ModuleImage           => "ModuleImage"
    case WireType.FunctionName          => "FunctionName"
    case WireType.KernelFunction        => "KernelFunction"
    case WireType.KernelLaunch          => "KernelLaunch"
    case WireType.Stream                => "Stream"
    case WireType.ByteBuffer            => "Col[Byte]"
    case WireType.KernelParameters      => "KernelParameters"
    case WireType.NullableOpaquePointer =>
      "Nullable[Pointer[net.kurobako.gemstone.abi.OpaquePointer, ReadWrite, Opaque]]"
    case WireType.CallbackData       => "CallbackData"
    case WireType.CUDAHostCallback   => "CUhostFn"
    case WireType.CUDAStreamCallback => "CUstreamCallback"
    case WireType.CUDAStatus         => "CUDAStatus"
  }

  private def encodeWire(wireType: WireType, value: String): String = wireType match {
    case WireType.UnitValue             => "WireValue.UnitValue"
    case WireType.Int32                 => s"WireValue.Int32Value($value)"
    case WireType.UInt8                 => s"WireValue.UInt8Value($value)"
    case WireType.UInt32                => s"WireValue.UInt32Value($value)"
    case WireType.SizeT                 => s"WireValue.SizeTValue($value)"
    case WireType.Device                => s"WireValue.DeviceValue($value)"
    case WireType.DevicePtr             => s"WireValue.DevicePtrValue($value)"
    case WireType.Context               => s"WireValue.ContextValue($value)"
    case WireType.CUDAModule            => s"WireValue.CUDAModuleValue($value)"
    case WireType.CUDAFunction          => s"WireValue.CUDAFunctionValue($value)"
    case WireType.HostInput             => s"WireValue.HostInputValue($value)"
    case WireType.HostOutput            => s"WireValue.HostOutputValue($value)"
    case WireType.ModuleImage           => s"WireValue.ModuleImageValue($value)"
    case WireType.FunctionName          => s"WireValue.FunctionNameValue($value)"
    case WireType.KernelFunction        => s"WireValue.KernelFunctionValue($value)"
    case WireType.KernelLaunch          => s"WireValue.KernelLaunchValue($value)"
    case WireType.Stream                => s"WireValue.StreamValue($value)"
    case WireType.ByteBuffer            => s"WireValue.ByteBufferValue($value)"
    case WireType.KernelParameters      => s"WireValue.KernelParametersValue($value)"
    case WireType.NullableOpaquePointer =>
      s"WireValue.NullableOpaquePointerValue($value.toOption.nonEmpty)"
    case WireType.CallbackData       => s"WireValue.CallbackDataValue($value)"
    case WireType.CUDAHostCallback   => s"WireValue.CUDAHostCallbackValue($value)"
    case WireType.CUDAStreamCallback => s"WireValue.CUDAStreamCallbackValue($value)"
    case WireType.CUDAStatus         => s"WireValue.CUDAStatusValue($value)"
  }

  private def decodePattern(wireType: WireType, binding: String): String = wireType match {
    case WireType.UnitValue             => "WireValue.UnitValue"
    case WireType.Int32                 => s"WireValue.Int32Value($binding)"
    case WireType.UInt8                 => s"WireValue.UInt8Value($binding)"
    case WireType.UInt32                => s"WireValue.UInt32Value($binding)"
    case WireType.SizeT                 => s"WireValue.SizeTValue($binding)"
    case WireType.Device                => s"WireValue.DeviceValue($binding)"
    case WireType.DevicePtr             => s"WireValue.DevicePtrValue($binding)"
    case WireType.Context               => s"WireValue.ContextValue($binding)"
    case WireType.CUDAModule            => s"WireValue.CUDAModuleValue($binding)"
    case WireType.CUDAFunction          => s"WireValue.CUDAFunctionValue($binding)"
    case WireType.HostInput             => s"WireValue.HostInputValue($binding)"
    case WireType.HostOutput            => s"WireValue.HostOutputValue($binding)"
    case WireType.ModuleImage           => s"WireValue.ModuleImageValue($binding)"
    case WireType.FunctionName          => s"WireValue.FunctionNameValue($binding)"
    case WireType.KernelFunction        => s"WireValue.KernelFunctionValue($binding)"
    case WireType.KernelLaunch          => s"WireValue.KernelLaunchValue($binding)"
    case WireType.Stream                => s"WireValue.StreamValue($binding)"
    case WireType.ByteBuffer            => s"WireValue.ByteBufferValue($binding)"
    case WireType.KernelParameters      => s"WireValue.KernelParametersValue($binding)"
    case WireType.NullableOpaquePointer => s"WireValue.NullableOpaquePointerValue($binding)"
    case WireType.CallbackData          => s"WireValue.CallbackDataValue($binding)"
    case WireType.CUDAHostCallback      => s"WireValue.CUDAHostCallbackValue($binding)"
    case WireType.CUDAStreamCallback    => s"WireValue.CUDAStreamCallbackValue($binding)"
    case WireType.CUDAStatus            => s"WireValue.CUDAStatusValue($binding)"
  }

}
