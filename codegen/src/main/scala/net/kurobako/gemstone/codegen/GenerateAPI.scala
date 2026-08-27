package net.kurobako.gemstone.codegen

import net.kurobako.gemstone.apispec.API
import net.kurobako.gemstone.apispec.APIFunction
import net.kurobako.gemstone.apispec.Direction
import net.kurobako.gemstone.apispec.GemstoneAPIRegistry
import net.kurobako.gemstone.apispec.ReturnConvention
import net.kurobako.gemstone.cuda.CUDA
import net.kurobako.gemstone.rpc.RPCDerivation
import net.kurobako.gemstone.rpc.RPCSchema
import net.kurobako.gemstone.rpc.WireType

import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

object GenerateAPI {
  // C entry-point spelling is protocol identity and must never be acronym-normalized.
  private val cudaRPC: RPCSchema = RPCDerivation.schema[CUDA]
  private val cudaDriverSurface  = RPCDerivation.methodNames[CUDA].filter(name =>
    name.startsWith("cu") && !name.startsWith("cuda")
  )
  private val cudaRuntimeSurface   = RPCDerivation.methodNames[CUDA].filter(_.startsWith("cuda"))
  private val cudaBootstrapSurface = RPCDerivation.methodNames[CUDA].filter(_.startsWith("__cuda"))
  require(cudaDriverSurface.size == 144, s"expected 144 CUDA Driver methods, found ${cudaDriverSurface.size}")

  def main(arguments: Array[String]): Unit = {
    val (check, output) = arguments.toVector match {
      case Vector(path)            => false -> Paths.get(path)
      case Vector("--check", path) => true  -> Paths.get(path)
      case _                       =>
        throw new IllegalArgumentException("usage: GenerateAPI [--check] <output-directory>")
    }

    val generated = files(output)
    if (check) verify(generated)
    else write(generated)
  }

  private def files(output: Path): Vector[(Path, String)] = {
    val api = GemstoneAPIRegistry.cuda
    Vector(
      output.resolve("c/gemsi_protocol.h")                                           -> renderProtocol(api),
      output.resolve("c/gemsi_cuda.h")                                               -> renderHeader(api),
      output.resolve("c/gemsi_cuda.c")                                               -> renderFacade(api),
      output.resolve("c/gemsi_cuda_driver_stubs.c")                                  -> renderCUDADriverStubs,
      output.resolve("c/gemsi_cuda.map")                                             -> renderVersionMap(api),
      output.resolve("c/gemsi_cuda_driver_surface.inc")                              -> renderCUDASurfaceInclude,
      output.resolve("c/gemsi_cuda_rpc.inc")                                         -> renderCUDARPCInclude,
      output.resolve("c/gemsi_cuda_rpc_client.c")                                    -> renderCUDARPCClient,
      output.resolve("scala/net/kurobako/gemstone/generated/GeneratedAPI.scala")     -> renderScala(api),
      output.resolve("scala/net/kurobako/gemstone/generated/GeneratedCUDARPC.scala") -> renderCUDARPCScala,
      output.resolve("manifest/api-manifest.json")                                   -> renderManifest(api),
      output.resolve("manifest/cuda-rpc-schema.json")                                -> renderCUDARPCManifest,
      output.resolve("manifest/cuda-driver-surface.json")                            -> renderCUDASurfaceManifest(api)
    )
  }

  private def write(files: Vector[(Path, String)]): Unit = files.foreach { case (path, content) =>
    Files.createDirectories(path.getParent)
    Files.writeString(path, content, StandardCharsets.UTF_8)
    println(s"generated $path")
  }

  private def verify(files: Vector[(Path, String)]): Unit = {
    val stale = files.collect {
      case (path, content) if !Files.exists(path) || Files.readString(path, StandardCharsets.UTF_8) != content => path
    }
    if (stale.nonEmpty) {
      throw new IllegalStateException(s"generated API files are stale: ${stale.mkString(", ")}")
    }
    println(s"verified ${files.size} generated API files")
  }

  private def renderProtocol(api: API): String = {
    val opcodes = api.functions.map(function => s"  GEMSI_OPCODE_${upperSnake(function.name)} = ${function.opcode}")
    val names   = api.functions.map { function =>
      s"    case GEMSI_OPCODE_${upperSnake(function.name)}: return \"${function.name}\";"
    }
    s"""|#ifndef GEMSI_PROTOCOL_H
        |#define GEMSI_PROTOCOL_H
        |
        |#include <stddef.h>
        |#include <stdint.h>
        |
        |#ifdef __cplusplus
        |extern "C" {
        |#endif
        |
        |enum {
        |  GEMSI_PROTOCOL_VERSION = ${GemstoneAPIRegistry.protocolVersion},
        |  GEMSI_OPCODE_SHUTDOWN = 0,
        |${opcodes.mkString(",\n")}
        |};
        |
        |enum {
        |  GEMSI_STATUS_SUCCESS = 0,
        |  GEMSI_STATUS_INVALID_VALUE = 1,
        |  GEMSI_STATUS_INITIALIZATION_ERROR = 3,
        |  GEMSI_STATUS_NOT_SUPPORTED = 801,
        |  GEMSI_STATUS_UNKNOWN = 999
        |};
        |
        |typedef struct GemsiWireRequest {
        |  uint32_t protocol_version;
        |  uint32_t opcode;
        |  uint64_t request_id;
        |  uint64_t foreign_thread_id;
        |  int64_t argument0;
        |  int64_t argument1;
        |} GemsiWireRequest;
        |
        |typedef struct GemsiWireResponse {
        |  uint32_t protocol_version;
        |  int32_t status;
        |  uint64_t request_id;
        |  int64_t result0;
        |} GemsiWireResponse;
        |
        |#if defined(__cplusplus)
        |#define GEMSI_STATIC_ASSERT(condition, message) static_assert(condition, message)
        |#else
        |#define GEMSI_STATIC_ASSERT(condition, message) _Static_assert(condition, message)
        |#endif
        |
        |GEMSI_STATIC_ASSERT(sizeof(GemsiWireRequest) == 40, "GemsiWireRequest ABI size changed");
        |GEMSI_STATIC_ASSERT(offsetof(GemsiWireRequest, protocol_version) == 0, "request protocol_version offset changed");
        |GEMSI_STATIC_ASSERT(offsetof(GemsiWireRequest, opcode) == 4, "request opcode offset changed");
        |GEMSI_STATIC_ASSERT(offsetof(GemsiWireRequest, request_id) == 8, "request request_id offset changed");
        |GEMSI_STATIC_ASSERT(offsetof(GemsiWireRequest, foreign_thread_id) == 16, "request foreign_thread_id offset changed");
        |GEMSI_STATIC_ASSERT(offsetof(GemsiWireRequest, argument0) == 24, "request argument0 offset changed");
        |GEMSI_STATIC_ASSERT(offsetof(GemsiWireRequest, argument1) == 32, "request argument1 offset changed");
        |GEMSI_STATIC_ASSERT(sizeof(GemsiWireResponse) == 24, "GemsiWireResponse ABI size changed");
        |GEMSI_STATIC_ASSERT(offsetof(GemsiWireResponse, protocol_version) == 0, "response protocol_version offset changed");
        |GEMSI_STATIC_ASSERT(offsetof(GemsiWireResponse, status) == 4, "response status offset changed");
        |GEMSI_STATIC_ASSERT(offsetof(GemsiWireResponse, request_id) == 8, "response request_id offset changed");
        |GEMSI_STATIC_ASSERT(offsetof(GemsiWireResponse, result0) == 16, "response result0 offset changed");
        |
        |#undef GEMSI_STATIC_ASSERT
        |
        |static inline const char *gemsi_operation_name(uint32_t opcode) {
        |  switch (opcode) {
        |    case GEMSI_OPCODE_SHUTDOWN: return "shutdown";
        |${names.mkString("\n")}
        |    default: return "unknown";
        |  }
        |}
        |
        |#ifdef __cplusplus
        |}
        |#endif
        |
        |#endif
        |""".stripMargin
  }

  private def renderHeader(api: API): String = {
    val declarations = api.functions.map { function =>
      val parameters = function.parameters.map(renderCParameter).mkString(", ")
      s"int32_t ${function.name}($parameters);"
    }
    s"""|#ifndef GEMSI_CUDA_H
        |#define GEMSI_CUDA_H
        |
        |#include <stdint.h>
        |
        |#ifdef __cplusplus
        |extern "C" {
        |#endif
        |
        |${declarations.mkString("\n")}
        |
        |#ifdef __cplusplus
        |}
        |#endif
        |
        |#endif
        |""".stripMargin
  }

  private def renderFacade(api: API): String = {
    val functions = api.functions.map(renderFunction)
    s"""|#include "gemsi_cuda.h"
        |#include "gemsi_dispatch.h"
        |#include "gemsi_protocol.h"
        |
        |#include <stddef.h>
        |
        |static _Thread_local int32_t gemsi_cuda_last_error = GEMSI_STATUS_SUCCESS;
        |
        |${functions.mkString("\n\n")}
        |""".stripMargin
  }

  private def renderFunction(function: APIFunction): String = {
    val parameters    = function.parameters.map(renderCParameter).mkString(", ")
    val invalidReturn =
      if (function.returnConvention == ReturnConvention.StatusCode) "GEMSI_STATUS_INVALID_VALUE" else "0"
    val invalidChecks = function.parameters.collect {
      case parameter if parameter.direction != Direction.In && !parameter.nullable =>
        s"""|  if (${parameter.name} == NULL) {
            |    gemsi_dispatch_record_boundary(GEMSI_OPCODE_${upperSnake(function.name)}, GEMSI_STATUS_INVALID_VALUE);
            |    return $invalidReturn;
            |  }""".stripMargin
    }
    val inputs = function.parameters.filter(_.direction != Direction.Out)
    require(inputs.size <= 2, s"wire prototype supports at most two input values: ${function.name}")
    val arguments = Vector.tabulate(2) { index =>
      inputs.lift(index).map(parameter => s"(int64_t)${parameter.name}").getOrElse("0")
    }
    val outputAssignments = function.parameters.collect {
      case parameter if parameter.direction != Direction.In =>
        s"    *${parameter.name} = (int32_t)response.result0;"
    }
    val statusUpdate = if (function.threadLocalError) "  gemsi_cuda_last_error = response.status;\n" else ""
    val result       = function.returnConvention match {
      case ReturnConvention.StatusCode =>
        s"""|$statusUpdate  if (response.status == GEMSI_STATUS_SUCCESS) {
            |${outputAssignments.mkString("\n")}
            |  }
            |  return response.status;""".stripMargin
      case ReturnConvention.DirectInt32 =>
        "  if (response.status != GEMSI_STATUS_SUCCESS) return 0;\n  return (int32_t)response.result0;"
    }
    val checkedBoundary = if (invalidChecks.isEmpty) "" else s"${invalidChecks.mkString("\n")}\n"
    s"""|int32_t ${function.name}($parameters) {
        |$checkedBoundary  GemsiWireRequest request = {
        |    .protocol_version = GEMSI_PROTOCOL_VERSION,
        |    .opcode = GEMSI_OPCODE_${upperSnake(function.name)},
        |    .argument0 = ${arguments(0)},
        |    .argument1 = ${arguments(1)}
        |  };
        |  GemsiWireResponse response = {0};
        |  if (gemsi_dispatch_request(&request, &response) != 0) {
        |    gemsi_cuda_last_error = GEMSI_STATUS_INITIALIZATION_ERROR;
        |    return ${
         if (function.returnConvention == ReturnConvention.StatusCode) "GEMSI_STATUS_INITIALIZATION_ERROR" else "0"
       };
        |  }
        |$result
        |}""".stripMargin
  }

  private def renderVersionMap(api: API): String = {
    val symbols =
      (api.functions.map(_.name) ++ cudaDriverSurface ++ cudaRuntimeSurface ++ cudaBootstrapSurface).distinct
        .map(symbol => s"    $symbol;")
        .mkString("\n")
    s"""|${api.symbolVersion} {
        |  global:
        |$symbols
        |  local:
        |    *;
        |};
        |""".stripMargin
  }

  private def renderCUDADriverStubs: String = {
    val wired = GemstoneAPIRegistry.cuda.functions.map(_.name).toSet ++ cudaRPC.methods.map(
      _.name
    ).filterNot(_.startsWith("cuda"))
    val stubs = cudaDriverSurface.filterNot(wired).map { name =>
      s"int32_t $name() { return GEMSI_STATUS_NOT_SUPPORTED; }"
    }
    s"""|#include <stdint.h>
        |
        |#include "gemsi_protocol.h"
        |
        |/* ABI-presence stubs. Exact typed marshallers replace these one function at a time. */
        |${stubs.mkString("\n\n")}
        |""".stripMargin
  }

  private def renderScala(api: API): String = {
    val opcodes = api.functions.map { function =>
      s"    final val ${scalaName(function.name)}: Int = ${function.opcode}"
    }
    val handlerMethods = api.functions.map { function =>
      val inputs     = function.parameters.filter(_.direction != Direction.Out)
      val parameters = inputs.map(parameter => s"${parameter.name}: Int").mkString(", ")
      s"    def ${function.name}($parameters): DispatchResult"
    }
    val cases = api.functions.map { function =>
      val inputs =
        function.parameters.filter(_.direction != Direction.Out).indices.map(index => s"argument$index.toInt")
      s"      case Opcode.${scalaName(function.name)} => handler.${function.name}(${inputs.mkString(", ")})"
    }
    s"""|package net.kurobako.gemstone.generated
        |
        |object GeneratedAPI {
        |  final val ProtocolVersion: Int = ${GemstoneAPIRegistry.protocolVersion}
        |
        |  object Opcode {
        |    final val Shutdown: Int = 0
        |${opcodes.mkString("\n")}
        |  }
        |
        |  opaque type DispatchResult = Long
        |
        |  object DispatchResult {
        |    def apply(status: Int, result0: Long): DispatchResult =
        |      (status.toLong << 32) | (result0 & 0xffffffffL)
        |
        |    extension (result: DispatchResult) {
        |      def status: Int   = (result >>> 32).toInt
        |      def result0: Long = result.toInt.toLong
        |    }
        |  }
        |
        |  trait Handler {
        |${handlerMethods.mkString("\n")}
        |  }
        |
        |  def dispatch(opcode: Int, argument0: Long, argument1: Long, handler: Handler): DispatchResult = {
        |    opcode match {
        |${cases.mkString("\n")}
        |      case _ => DispatchResult(status = 999, result0 = 0L)
        |    }
        |  }
        |}
        |""".stripMargin
  }

  private def renderManifest(api: API): String = {
    val functions = api.functions.map { function =>
      s"""|    {"name":"${function.name}","opcode":${function.opcode},"synchronous":${function.synchronous},"threadLocalError":${function.threadLocalError}}""".stripMargin
    }
    s"""|{"protocolVersion":${GemstoneAPIRegistry.protocolVersion},"api":"${api.name}","abiVersion":"${api.abiVersion}","soname":"${api.soname}","symbolVersion":"${api.symbolVersion}","functions":[
        |${functions.mkString(",\n")}
        |]}
        |""".stripMargin
  }

  private def renderCUDASurfaceInclude: String = {
    def renderMacro(name: String, entries: Vector[String]): String = {
      val body = entries.zipWithIndex.map { case (entry, index) =>
        val continuation = if (index == entries.size - 1) "" else s" ${'\\'}"
        s"  X($entry)$continuation"
      }
      s"#define $name(X) ${'\\'}\n${body.mkString("\n")}"
    }

    s"""|#ifndef GEMSI_CUDA_DRIVER_SURFACE_INC
        |#define GEMSI_CUDA_DRIVER_SURFACE_INC
        |
        |/* Generated from the concrete and abstract methods of net.kurobako.gemstone.cuda.CUDA. */
        |${renderMacro("GEMSI_CUDA_DRIVER_API", cudaDriverSurface)}
        |
        |#endif
        |""".stripMargin
  }

  private def renderCUDASurfaceManifest(wiredAPI: API): String = {
    val wired     = wiredAPI.functions.map(_.name).toSet ++ cudaRPC.methods.map(_.name).filterNot(_.startsWith("cuda"))
    val functions = cudaDriverSurface.map { name =>
      val state = if (wired(name)) "wired" else "stub"
      s"""|    {"name":"$name","transport":"$state"}""".stripMargin
    }
    s"""|{"profile":"gpuocelot-cuda-driver-3.2","cudaVersion":${CUDA.CUDAVersion},"upstreamRevision":"${CUDA.UpstreamRevision}","driverCount":${cudaDriverSurface.size},"functions":[
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
    s"""|#ifndef GEMSI_CUDA_RPC_INC
        |#define GEMSI_CUDA_RPC_INC
        |
        |#include <stdint.h>
        |
        |#define GEMSI_CUDA_RPC_SCHEMA_FINGERPRINT "${cudaRPC.fingerprint}"
        |#define GEMSI_CUDA_RPC_METHODS(X) ${'\\'}
        |${methods.mkString("\n")}
        |
        |#endif
        |""".stripMargin
  }

  private def renderCUDARPCClient: String = {
    val ids = cudaRPC.methods.filterNot(_.name.startsWith("cuda")).map { method =>
      val high = f"${method.messageId.high}%016x"
      val low  = f"${method.messageId.low}%016x"
      s"static const uint64_t gemsi_${method.name}_high = UINT64_C(0x$high);\nstatic const uint64_t gemsi_${method.name}_low = UINT64_C(0x$low);"
    }
    s"""|#include "gemsi_cuda_rpc.inc"
        |#include "gemsi_protocol.h"
        |#include "gemsi_rpc_transport.h"
        |
        |#include <limits.h>
        |#include <pthread.h>
        |#include <stddef.h>
        |#include <stdint.h>
        |#include <stdlib.h>
        |#include <string.h>
        |
        |${ids.mkString("\n")}
        |
        |static void gemsi_put_u32(uint8_t *destination, uint32_t value) {
        |  destination[0] = (uint8_t)(value >> 24);
        |  destination[1] = (uint8_t)(value >> 16);
        |  destination[2] = (uint8_t)(value >> 8);
        |  destination[3] = (uint8_t)value;
        |}
        |
        |static void gemsi_put_u64(uint8_t *destination, uint64_t value) {
        |  gemsi_put_u32(destination, (uint32_t)(value >> 32));
        |  gemsi_put_u32(destination + 4, (uint32_t)value);
        |}
        |
        |static uint32_t gemsi_get_u32(const uint8_t *source) {
        |  return ((uint32_t)source[0] << 24) | ((uint32_t)source[1] << 16) | ((uint32_t)source[2] << 8) | (uint32_t)source[3];
        |}
        |
        |static uint64_t gemsi_get_u64(const uint8_t *source) {
        |  return ((uint64_t)gemsi_get_u32(source) << 32) | gemsi_get_u32(source + 4);
        |}
        |
        |static size_t gemsi_bounded_string_length(const char *value, size_t limit) {
        |  size_t size = 0;
        |  while (size < limit && value[size] != '\\0') ++size;
        |  return size;
        |}
        |
        |typedef struct GemsiKernelSignature {
        |  uint64_t function;
        |  uint64_t module;
        |  uint32_t count;
        |  uint8_t *widths;
        |  struct GemsiKernelSignature *next;
        |} GemsiKernelSignature;
        |
        |static pthread_mutex_t gemsi_kernel_mutex = PTHREAD_MUTEX_INITIALIZER;
        |static GemsiKernelSignature *gemsi_kernel_signatures = NULL;
        |
        |static int gemsi_store_kernel_signature(
        |    uint64_t function,
        |    uint64_t module,
        |    const uint8_t *widths,
        |    uint32_t count) {
        |  GemsiKernelSignature *entry = (GemsiKernelSignature *)calloc(1, sizeof(*entry));
        |  if (entry == NULL) return -1;
        |  if (count != 0) {
        |    entry->widths = (uint8_t *)malloc(count);
        |    if (entry->widths == NULL) { free(entry); return -1; }
        |    memcpy(entry->widths, widths, count);
        |  }
        |  entry->function = function;
        |  entry->module = module;
        |  entry->count = count;
        |  pthread_mutex_lock(&gemsi_kernel_mutex);
        |  entry->next = gemsi_kernel_signatures;
        |  gemsi_kernel_signatures = entry;
        |  pthread_mutex_unlock(&gemsi_kernel_mutex);
        |  return 0;
        |}
        |
        |static void gemsi_forget_module(uint64_t module) {
        |  pthread_mutex_lock(&gemsi_kernel_mutex);
        |  GemsiKernelSignature **cursor = &gemsi_kernel_signatures;
        |  while (*cursor != NULL) {
        |    GemsiKernelSignature *entry = *cursor;
        |    if (entry->module == module) {
        |      *cursor = entry->next;
        |      free(entry->widths);
        |      free(entry);
        |    } else cursor = &entry->next;
        |  }
        |  pthread_mutex_unlock(&gemsi_kernel_mutex);
        |}
        |
        |static int32_t gemsi_call(
        |    uint64_t high,
        |    uint64_t low,
        |    const char *name,
        |    const uint8_t *payload,
        |    uint32_t payload_size,
        |    GemsiRpcBuffer *response) {
        |  int32_t status = GEMSI_STATUS_INITIALIZATION_ERROR;
        |  if (gemsi_rpc_call(high, low, name, payload, payload_size, &status, response) != 0) return GEMSI_STATUS_INITIALIZATION_ERROR;
        |  if (status != GEMSI_STATUS_SUCCESS) gemsi_rpc_buffer_release(response);
        |  return status;
        |}
        |
        |static int32_t gemsi_expect_unit(int32_t status, GemsiRpcBuffer *response) {
        |  if (status == GEMSI_STATUS_SUCCESS && response->size != 0) status = GEMSI_STATUS_UNKNOWN;
        |  gemsi_rpc_buffer_release(response);
        |  return status;
        |}
        |
        |static int32_t gemsi_expect_u32(int32_t status, GemsiRpcBuffer *response, uint32_t *value) {
        |  if (status == GEMSI_STATUS_SUCCESS) {
        |    if (response->size != 4 || value == NULL) status = GEMSI_STATUS_UNKNOWN;
        |    else *value = gemsi_get_u32(response->data);
        |  }
        |  gemsi_rpc_buffer_release(response);
        |  return status;
        |}
        |
        |static int32_t gemsi_expect_u64(int32_t status, GemsiRpcBuffer *response, uint64_t *value) {
        |  if (status == GEMSI_STATUS_SUCCESS) {
        |    if (response->size != 8 || value == NULL) status = GEMSI_STATUS_UNKNOWN;
        |    else *value = gemsi_get_u64(response->data);
        |  }
        |  gemsi_rpc_buffer_release(response);
        |  return status;
        |}
        |
        |int32_t cuInit(uint32_t flags) {
        |  uint8_t payload[4];
        |  gemsi_put_u32(payload, flags);
        |  GemsiRpcBuffer response = {0};
        |  return gemsi_expect_unit(gemsi_call(gemsi_cuInit_high, gemsi_cuInit_low, "cuInit", payload, sizeof(payload), &response), &response);
        |}
        |
        |int32_t cuDriverGetVersion(int32_t *version) {
        |  if (version == NULL) return GEMSI_STATUS_INVALID_VALUE;
        |  GemsiRpcBuffer response = {0};
        |  return gemsi_expect_u32(gemsi_call(gemsi_cuDriverGetVersion_high, gemsi_cuDriverGetVersion_low, "cuDriverGetVersion", NULL, 0, &response), &response, (uint32_t *)version);
        |}
        |
        |int32_t cuDeviceGetCount(int32_t *count) {
        |  if (count == NULL) return GEMSI_STATUS_INVALID_VALUE;
        |  GemsiRpcBuffer response = {0};
        |  return gemsi_expect_u32(gemsi_call(gemsi_cuDeviceGetCount_high, gemsi_cuDeviceGetCount_low, "cuDeviceGetCount", NULL, 0, &response), &response, (uint32_t *)count);
        |}
        |
        |int32_t cuDeviceGet(int32_t *device, int32_t ordinal) {
        |  if (device == NULL) return GEMSI_STATUS_INVALID_VALUE;
        |  uint8_t payload[4];
        |  gemsi_put_u32(payload, (uint32_t)ordinal);
        |  GemsiRpcBuffer response = {0};
        |  return gemsi_expect_u32(gemsi_call(gemsi_cuDeviceGet_high, gemsi_cuDeviceGet_low, "cuDeviceGet", payload, sizeof(payload), &response), &response, (uint32_t *)device);
        |}
        |
        |int32_t cuCtxCreate_v2(void **context, uint32_t flags, int32_t device) {
        |  if (context == NULL) return GEMSI_STATUS_INVALID_VALUE;
        |  uint8_t payload[8];
        |  gemsi_put_u32(payload, flags);
        |  gemsi_put_u32(payload + 4, (uint32_t)device);
        |  uint64_t handle = 0;
        |  GemsiRpcBuffer response = {0};
        |  const int32_t status = gemsi_expect_u64(gemsi_call(gemsi_cuCtxCreate_v2_high, gemsi_cuCtxCreate_v2_low, "cuCtxCreate_v2", payload, sizeof(payload), &response), &response, &handle);
        |  if (status == GEMSI_STATUS_SUCCESS) *context = (void *)(uintptr_t)handle;
        |  return status;
        |}
        |
        |int32_t cuCtxDestroy_v2(void *context) {
        |  uint8_t payload[8];
        |  gemsi_put_u64(payload, (uint64_t)(uintptr_t)context);
        |  GemsiRpcBuffer response = {0};
        |  return gemsi_expect_unit(gemsi_call(gemsi_cuCtxDestroy_v2_high, gemsi_cuCtxDestroy_v2_low, "cuCtxDestroy_v2", payload, sizeof(payload), &response), &response);
        |}
        |
        |int32_t cuCtxSynchronize(void) {
        |  GemsiRpcBuffer response = {0};
        |  return gemsi_expect_unit(gemsi_call(gemsi_cuCtxSynchronize_high, gemsi_cuCtxSynchronize_low, "cuCtxSynchronize", NULL, 0, &response), &response);
        |}
        |
        |int32_t cuMemAlloc_v2(uint64_t *pointer, size_t size) {
        |  if (pointer == NULL) return GEMSI_STATUS_INVALID_VALUE;
        |  uint8_t payload[8];
        |  gemsi_put_u64(payload, (uint64_t)size);
        |  GemsiRpcBuffer response = {0};
        |  return gemsi_expect_u64(gemsi_call(gemsi_cuMemAlloc_v2_high, gemsi_cuMemAlloc_v2_low, "cuMemAlloc_v2", payload, sizeof(payload), &response), &response, pointer);
        |}
        |
        |int32_t cuMemFree_v2(uint64_t pointer) {
        |  uint8_t payload[8];
        |  gemsi_put_u64(payload, pointer);
        |  GemsiRpcBuffer response = {0};
        |  return gemsi_expect_unit(gemsi_call(gemsi_cuMemFree_v2_high, gemsi_cuMemFree_v2_low, "cuMemFree_v2", payload, sizeof(payload), &response), &response);
        |}
        |
        |int32_t cuMemcpyHtoD_v2(uint64_t destination, const void *source, size_t size) {
        |  if ((source == NULL && size != 0) || size > GEMSI_RPC_MAXIMUM_PAYLOAD_SIZE - 12) return GEMSI_STATUS_INVALID_VALUE;
        |  const uint32_t payload_size = (uint32_t)size + 12;
        |  uint8_t *payload = (uint8_t *)malloc(payload_size);
        |  if (payload == NULL) return GEMSI_STATUS_UNKNOWN;
        |  gemsi_put_u64(payload, destination);
        |  gemsi_put_u32(payload + 8, (uint32_t)size);
        |  if (size != 0) memcpy(payload + 12, source, size);
        |  GemsiRpcBuffer response = {0};
        |  const int32_t status = gemsi_expect_unit(gemsi_call(gemsi_cuMemcpyHtoD_v2_high, gemsi_cuMemcpyHtoD_v2_low, "cuMemcpyHtoD_v2", payload, payload_size, &response), &response);
        |  free(payload);
        |  return status;
        |}
        |
        |int32_t cuMemcpyDtoH_v2(void *destination, uint64_t source, size_t size) {
        |  if ((destination == NULL && size != 0) || size > GEMSI_RPC_MAXIMUM_PAYLOAD_SIZE - 4) return GEMSI_STATUS_INVALID_VALUE;
        |  uint8_t payload[16];
        |  gemsi_put_u64(payload, source);
        |  gemsi_put_u64(payload + 8, (uint64_t)size);
        |  GemsiRpcBuffer response = {0};
        |  int32_t status = gemsi_call(gemsi_cuMemcpyDtoH_v2_high, gemsi_cuMemcpyDtoH_v2_low, "cuMemcpyDtoH_v2", payload, sizeof(payload), &response);
        |  if (status == GEMSI_STATUS_SUCCESS) {
        |    if (response.size < 4 || gemsi_get_u32(response.data) != size || response.size != size + 4) status = GEMSI_STATUS_UNKNOWN;
        |    else if (size != 0) memcpy(destination, response.data + 4, size);
        |  }
        |  gemsi_rpc_buffer_release(&response);
        |  return status;
        |}
        |
        |int32_t cuMemcpyDtoD_v2(uint64_t destination, uint64_t source, size_t size) {
        |  uint8_t payload[24];
        |  gemsi_put_u64(payload, destination);
        |  gemsi_put_u64(payload + 8, source);
        |  gemsi_put_u64(payload + 16, (uint64_t)size);
        |  GemsiRpcBuffer response = {0};
        |  return gemsi_expect_unit(gemsi_call(gemsi_cuMemcpyDtoD_v2_high, gemsi_cuMemcpyDtoD_v2_low, "cuMemcpyDtoD_v2", payload, sizeof(payload), &response), &response);
        |}
        |
        |int32_t cuMemsetD8_v2(uint64_t destination, uint8_t value, size_t count) {
        |  uint8_t payload[17];
        |  gemsi_put_u64(payload, destination);
        |  payload[8] = value;
        |  gemsi_put_u64(payload + 9, (uint64_t)count);
        |  GemsiRpcBuffer response = {0};
        |  return gemsi_expect_unit(gemsi_call(gemsi_cuMemsetD8_v2_high, gemsi_cuMemsetD8_v2_low, "cuMemsetD8_v2", payload, sizeof(payload), &response), &response);
        |}
        |
        |int32_t cuModuleLoadData(void **module, const void *image) {
        |  if (module == NULL || image == NULL) return GEMSI_STATUS_INVALID_VALUE;
        |  const size_t size = gemsi_bounded_string_length((const char *)image, GEMSI_RPC_MAXIMUM_PAYLOAD_SIZE);
        |  if (size == GEMSI_RPC_MAXIMUM_PAYLOAD_SIZE || size > GEMSI_RPC_MAXIMUM_PAYLOAD_SIZE - 4) {
        |    return GEMSI_STATUS_INVALID_VALUE;
        |  }
        |  const uint32_t payload_size = (uint32_t)size + 4;
        |  uint8_t *payload = (uint8_t *)malloc(payload_size);
        |  if (payload == NULL) return GEMSI_STATUS_UNKNOWN;
        |  gemsi_put_u32(payload, (uint32_t)size);
        |  memcpy(payload + 4, image, size);
        |  uint64_t handle = 0;
        |  GemsiRpcBuffer response = {0};
        |  const int32_t status = gemsi_expect_u64(
        |      gemsi_call(gemsi_cuModuleLoadData_high, gemsi_cuModuleLoadData_low, "cuModuleLoadData", payload, payload_size, &response),
        |      &response,
        |      &handle);
        |  free(payload);
        |  if (status == GEMSI_STATUS_SUCCESS) *module = (void *)(uintptr_t)handle;
        |  return status;
        |}
        |
        |int32_t cuModuleGetFunction(void **function, void *module, const char *name) {
        |  if (function == NULL || module == NULL || name == NULL) return GEMSI_STATUS_INVALID_VALUE;
        |  const size_t name_size = gemsi_bounded_string_length(name, 4096);
        |  if (name_size == 0 || name_size == 4096) return GEMSI_STATUS_INVALID_VALUE;
        |  const uint32_t payload_size = (uint32_t)name_size + 12;
        |  uint8_t *payload = (uint8_t *)malloc(payload_size);
        |  if (payload == NULL) return GEMSI_STATUS_UNKNOWN;
        |  gemsi_put_u64(payload, (uint64_t)(uintptr_t)module);
        |  gemsi_put_u32(payload + 8, (uint32_t)name_size);
        |  memcpy(payload + 12, name, name_size);
        |  GemsiRpcBuffer response = {0};
        |  int32_t status = gemsi_call(
        |      gemsi_cuModuleGetFunction_high,
        |      gemsi_cuModuleGetFunction_low,
        |      "cuModuleGetFunction",
        |      payload,
        |      payload_size,
        |      &response);
        |  free(payload);
        |  if (status == GEMSI_STATUS_SUCCESS) {
        |    if (response.size < 12) status = GEMSI_STATUS_UNKNOWN;
        |    else {
        |      const uint64_t handle = gemsi_get_u64(response.data);
        |      const uint32_t count = gemsi_get_u32(response.data + 8);
        |      if (count > 4096 || response.size != 12 + count ||
        |          gemsi_store_kernel_signature(handle, (uint64_t)(uintptr_t)module, response.data + 12, count) != 0) {
        |        status = GEMSI_STATUS_UNKNOWN;
        |      } else *function = (void *)(uintptr_t)handle;
        |    }
        |  }
        |  gemsi_rpc_buffer_release(&response);
        |  return status;
        |}
        |
        |int32_t cuModuleUnload(void *module) {
        |  if (module == NULL) return GEMSI_STATUS_INVALID_VALUE;
        |  uint8_t payload[8];
        |  gemsi_put_u64(payload, (uint64_t)(uintptr_t)module);
        |  GemsiRpcBuffer response = {0};
        |  const int32_t status = gemsi_expect_unit(
        |      gemsi_call(gemsi_cuModuleUnload_high, gemsi_cuModuleUnload_low, "cuModuleUnload", payload, sizeof(payload), &response),
        |      &response);
        |  if (status == GEMSI_STATUS_SUCCESS) gemsi_forget_module((uint64_t)(uintptr_t)module);
        |  return status;
        |}
        |
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
        |      block_x == 0 || block_y == 0 || block_z == 0 || stream != NULL || extra != NULL) {
        |    return GEMSI_STATUS_INVALID_VALUE;
        |  }
        |  pthread_mutex_lock(&gemsi_kernel_mutex);
        |  GemsiKernelSignature *signature = gemsi_kernel_signatures;
        |  while (signature != NULL && signature->function != (uint64_t)(uintptr_t)function) signature = signature->next;
        |  if (signature == NULL || (signature->count != 0 && kernel_params == NULL)) {
        |    pthread_mutex_unlock(&gemsi_kernel_mutex);
        |    return GEMSI_STATUS_INVALID_VALUE;
        |  }
        |  size_t payload_size = 40;
        |  for (uint32_t index = 0; index < signature->count; ++index) {
        |    const uint8_t width = signature->widths[index];
        |    if ((width != 1 && width != 2 && width != 4 && width != 8) || kernel_params[index] == NULL ||
        |        payload_size > GEMSI_RPC_MAXIMUM_PAYLOAD_SIZE - 4 - width) {
        |      pthread_mutex_unlock(&gemsi_kernel_mutex);
        |      return GEMSI_STATUS_INVALID_VALUE;
        |    }
        |    payload_size += 4 + width;
        |  }
        |  uint8_t *payload = (uint8_t *)malloc(payload_size);
        |  if (payload == NULL) { pthread_mutex_unlock(&gemsi_kernel_mutex); return GEMSI_STATUS_UNKNOWN; }
        |  gemsi_put_u64(payload, (uint64_t)(uintptr_t)function);
        |  gemsi_put_u32(payload + 8, grid_x);
        |  gemsi_put_u32(payload + 12, grid_y);
        |  gemsi_put_u32(payload + 16, grid_z);
        |  gemsi_put_u32(payload + 20, block_x);
        |  gemsi_put_u32(payload + 24, block_y);
        |  gemsi_put_u32(payload + 28, block_z);
        |  gemsi_put_u32(payload + 32, shared_memory_bytes);
        |  gemsi_put_u32(payload + 36, signature->count);
        |  size_t offset = 40;
        |  for (uint32_t index = 0; index < signature->count; ++index) {
        |    const uint8_t width = signature->widths[index];
        |    gemsi_put_u32(payload + offset, width);
        |    memcpy(payload + offset + 4, kernel_params[index], width);
        |    offset += 4 + width;
        |  }
        |  pthread_mutex_unlock(&gemsi_kernel_mutex);
        |  GemsiRpcBuffer response = {0};
        |  const int32_t status = gemsi_expect_unit(
        |      gemsi_call(gemsi_cuLaunchKernel_high, gemsi_cuLaunchKernel_low, "cuLaunchKernel", payload, (uint32_t)payload_size, &response),
        |      &response);
        |  free(payload);
        |  return status;
        |}
        |
        |#if defined(__GNUC__)
        |__attribute__((weak))
        |#endif
        |int32_t cudaGetDeviceCount(int32_t *count) {
        |  return cuDeviceGetCount(count);
        |}
        |
        |int32_t cudaMalloc(void **pointer, size_t size) {
        |  if (pointer == NULL) return GEMSI_STATUS_INVALID_VALUE;
        |  uint64_t device_pointer = 0;
        |  const int32_t status = cuMemAlloc_v2(&device_pointer, size);
        |  if (status == GEMSI_STATUS_SUCCESS) *pointer = (void *)(uintptr_t)device_pointer;
        |  return status;
        |}
        |
        |int32_t cudaFree(void *pointer) {
        |  return cuMemFree_v2((uint64_t)(uintptr_t)pointer);
        |}
        |
        |int32_t cudaMemcpy(void *destination, const void *source, size_t size, int32_t kind) {
        |  switch (kind) {
        |    case 1:
        |      return cuMemcpyHtoD_v2((uint64_t)(uintptr_t)destination, source, size);
        |    case 2:
        |      return cuMemcpyDtoH_v2(destination, (uint64_t)(uintptr_t)source, size);
        |    case 3:
        |      return cuMemcpyDtoD_v2((uint64_t)(uintptr_t)destination, (uint64_t)(uintptr_t)source, size);
        |    default:
        |      return GEMSI_STATUS_INVALID_VALUE;
        |  }
        |}
        |
        |const char *cudaGetErrorString(int32_t status) {
        |  switch (status) {
        |    case GEMSI_STATUS_SUCCESS: return "success";
        |    case GEMSI_STATUS_INVALID_VALUE: return "invalid value";
        |    case GEMSI_STATUS_NOT_SUPPORTED: return "not supported";
        |    case GEMSI_STATUS_INITIALIZATION_ERROR: return "initialization error";
        |    default: return "Gemstone CUDA error";
        |  }
        |}
        |
        |static void *gemsi_fat_binary_token = NULL;
        |
        |void **__cudaRegisterFatBinary(void *fat_binary) {
        |  gemsi_fat_binary_token = fat_binary;
        |  return &gemsi_fat_binary_token;
        |}
        |
        |void __cudaRegisterFatBinaryEnd(void **handle) {
        |  (void)handle;
        |}
        |
        |void __cudaUnregisterFatBinary(void **handle) {
        |  if (handle == &gemsi_fat_binary_token) gemsi_fat_binary_token = NULL;
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
      val parameters = method.parameters.map(parameter => s"${parameter.name}: ${scalaType(parameter.wireType)}")
      val arguments  = method.parameters.map(parameter => encodeWire(parameter.wireType, parameter.name))
      s"""|  override def ${method.name}(${parameters.mkString(", ")}): CUDAResult[${scalaType(method.result)}] =
          |    decode${method.result}(transport.call(RPCRequest(${method.name}Id, "${method.name}", Vector(${arguments.mkString(
           ", "
         )}))))""".stripMargin
    }
    val dispatchCases = cudaRPC.methods.map { method =>
      val patterns  = method.parameters.map(parameter => decodePattern(parameter.wireType, parameter.name))
      val arguments = method.parameters.map(_.name).mkString(", ")
      s"""|      case `${method.name}Id` if request.functionName == "${method.name}" =>
          |        request.arguments match {
          |          case Vector(${patterns.mkString(
           ", "
         )}) => encode${method.result}(implementation.${method.name}($arguments))
          |          case _ => invalid("invalid arguments for ${method.name}")
          |        }""".stripMargin
    }
    val codecs = cudaRPC.methods.map(_.result).distinct.map { wireType =>
      val scala        = scalaType(wireType)
      val decodedValue = if (wireType == WireType.UnitValue) "()" else "value"
      val encodedValue = if (wireType == WireType.UnitValue) "_" else "value"
      s"""|  private def decode$wireType(response: RPCResponse): CUDAResult[$scala] = response match {
          |    case RPCResponse.Success(${decodePattern(wireType, "value")}) => CUDAResult.Success($decodedValue)
          |    case RPCResponse.Success(_) => CUDAResult.Failure(CUDAError(999, "invalid $wireType response payload"))
          |    case RPCResponse.Failure(error) => CUDAResult.Failure(error)
          |  }
          |
          |  private def encode$wireType(result: CUDAResult[$scala]): RPCResponse = result match {
          |    case CUDAResult.Success($encodedValue) => RPCResponse.Success(${encodeWire(wireType, "value")})
          |    case CUDAResult.Failure(error) => RPCResponse.Failure(error)
          |  }""".stripMargin
    }
    val schemaMethods = cudaRPC.methods.map { method =>
      val parameters =
        method.parameters.map(parameter => s"RPCParameter(\"${parameter.name}\", WireType.${parameter.wireType})")
      s"RPCMethod(\"${method.name}\", ${method.name}Id, Vector(${parameters.mkString(", ")}), WireType.${method.result})"
    }
    s"""|package net.kurobako.gemstone.generated
        |
        |import net.kurobako.gemstone.cuda.*
        |import net.kurobako.gemstone.rpc.*
        |
        |object GeneratedCUDARPC {
        |  val SchemaFingerprint: String = "${cudaRPC.fingerprint}"
        |
        |${ids.mkString("\n")}
        |
        |  val Schema: RPCSchema = RPCSchema("${cudaRPC.interfaceName}", SchemaFingerprint, Vector(
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
        |${codecs.mkString("\n\n")}
        |}
        |""".stripMargin
  }

  private def scalaType(wireType: WireType): String = wireType match {
    case WireType.UnitValue      => "Unit"
    case WireType.Int32          => "Int"
    case WireType.UInt8          => "UInt8"
    case WireType.UInt32         => "UInt32"
    case WireType.SizeT          => "SizeT"
    case WireType.Device         => "Device"
    case WireType.DevicePtr      => "DevicePtr"
    case WireType.Context        => "Context"
    case WireType.CUDAModule     => "CUDAModule"
    case WireType.CUDAFunction   => "CUDAFunction"
    case WireType.HostInput      => "HostInput"
    case WireType.HostOutput     => "HostOutput"
    case WireType.ModuleImage    => "ModuleImage"
    case WireType.FunctionName   => "FunctionName"
    case WireType.KernelFunction => "KernelFunction"
    case WireType.KernelLaunch   => "KernelLaunch"
  }

  private def encodeWire(wireType: WireType, value: String): String = wireType match {
    case WireType.UnitValue      => "WireValue.UnitValue"
    case WireType.Int32          => s"WireValue.Int32Value($value)"
    case WireType.UInt8          => s"WireValue.UInt8Value($value)"
    case WireType.UInt32         => s"WireValue.UInt32Value($value)"
    case WireType.SizeT          => s"WireValue.SizeTValue($value)"
    case WireType.Device         => s"WireValue.DeviceValue($value)"
    case WireType.DevicePtr      => s"WireValue.DevicePtrValue($value)"
    case WireType.Context        => s"WireValue.ContextValue($value)"
    case WireType.CUDAModule     => s"WireValue.CUDAModuleValue($value)"
    case WireType.CUDAFunction   => s"WireValue.CUDAFunctionValue($value)"
    case WireType.HostInput      => s"WireValue.HostInputValue($value)"
    case WireType.HostOutput     => s"WireValue.HostOutputValue($value)"
    case WireType.ModuleImage    => s"WireValue.ModuleImageValue($value)"
    case WireType.FunctionName   => s"WireValue.FunctionNameValue($value)"
    case WireType.KernelFunction => s"WireValue.KernelFunctionValue($value)"
    case WireType.KernelLaunch   => s"WireValue.KernelLaunchValue($value)"
  }

  private def decodePattern(wireType: WireType, binding: String): String = wireType match {
    case WireType.UnitValue      => "WireValue.UnitValue"
    case WireType.Int32          => s"WireValue.Int32Value($binding)"
    case WireType.UInt8          => s"WireValue.UInt8Value($binding)"
    case WireType.UInt32         => s"WireValue.UInt32Value($binding)"
    case WireType.SizeT          => s"WireValue.SizeTValue($binding)"
    case WireType.Device         => s"WireValue.DeviceValue($binding)"
    case WireType.DevicePtr      => s"WireValue.DevicePtrValue($binding)"
    case WireType.Context        => s"WireValue.ContextValue($binding)"
    case WireType.CUDAModule     => s"WireValue.CUDAModuleValue($binding)"
    case WireType.CUDAFunction   => s"WireValue.CUDAFunctionValue($binding)"
    case WireType.HostInput      => s"WireValue.HostInputValue($binding)"
    case WireType.HostOutput     => s"WireValue.HostOutputValue($binding)"
    case WireType.ModuleImage    => s"WireValue.ModuleImageValue($binding)"
    case WireType.FunctionName   => s"WireValue.FunctionNameValue($binding)"
    case WireType.KernelFunction => s"WireValue.KernelFunctionValue($binding)"
    case WireType.KernelLaunch   => s"WireValue.KernelLaunchValue($binding)"
  }

  private def upperSnake(value: String): String = value
    .flatMap(character => if (character.isUpper) s"_${character}" else character.toString)
    .stripPrefix("_")
    .toUpperCase

  private def scalaName(value: String): String =
    if (value.startsWith("cuda")) s"CUDA${value.drop(4)}"
    else s"${value.head.toUpper}${value.tail}"

  private def renderCParameter(parameter: net.kurobako.gemstone.apispec.Parameter): String = {
    val separator = if (parameter.cType.cName.endsWith("*")) "" else " "
    s"${parameter.cType.cName}$separator${parameter.name}"
  }
}
