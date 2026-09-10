package net.kurobako.gemstone.codegen

/** Callback typedef structures extracted from the pinned official headers. `GenerateOfficialAPIManifest` checks these
  * definitions against Clang on every conformance run; `GenerateAPISurfaces` uses the same data to bootstrap typed
  * Scala aliases.
  */
object CallbackTypedefCatalog {
  final case class Definition(surface: String, api: String, name: String, signature: String)

  val definitions: Vector[Definition] = Vector(
    Definition(
      "cuda-driver",
      "CUDA",
      "CUasyncCallback",
      "void (*)(CUasyncNotificationInfo *, void *, CUasyncCallbackHandle)"
    ),
    Definition("cuda-driver", "CUDA", "CUcoredumpStatusCallback", "void (*)(void *, int, CUdevice)"),
    Definition(
      "cuda-driver",
      "CUDA",
      "CUgraphRecaptureCallback",
      "CUresult (*)(void *, CUgraphNode, const CUgraphNodeParams *, const CUgraphNodeParams *, CUgraphRecaptureStatus)"
    ),
    Definition("cuda-driver", "CUDA", "CUhostFn", "void (*)(void *)"),
    Definition("cuda-driver", "CUDA", "CUlogsCallback", "void (*)(void *, CUlogLevel, char *, size_t)"),
    Definition("cuda-driver", "CUDA", "CUoccupancyB2DSize", "size_t (*)(int)"),
    Definition("cuda-driver", "CUDA", "CUstreamCallback", "void (*)(CUstream, CUresult, void *)"),
    Definition(
      "cuda-runtime",
      "CUDA",
      "cudaAsyncCallback",
      "void (*)(cudaAsyncNotificationInfo_t *, void *, cudaAsyncCallbackHandle_t)"
    ),
    Definition("cuda-runtime", "CUDA", "cudaHostFn_t", "void (*)(void *)"),
    Definition("cuda-runtime", "CUDA", "cudaLogsCallback_t", "void (*)(void *, cudaLogLevel, char *, size_t)"),
    Definition("cuda-runtime", "CUDA", "cudaStreamCallback_t", "void (*)(cudaStream_t, cudaError_t, void *)"),
    Definition("hip", "HIP", "hipHostFn_t", "void (*)(void *)"),
    Definition("hip", "HIP", "hipStreamCallback_t", "void (*)(hipStream_t, hipError_t, void *)"),
    Definition("hsa", "HSA", "hsa_amd_deallocation_callback_t", "void (*)(void *, void *)"),
    Definition("hsa", "HSA", "hsa_amd_signal_handler", "bool (*)(hsa_signal_value_t, void *)"),
    Definition("hsa", "HSA", "hsa_amd_system_event_callback_t", "hsa_status_t (*)(const hsa_amd_event_t *, void *)"),
    Definition("hsa", "HSA", "hsa_ven_amd_pcs_data_copy_callback_t", "hsa_status_t (*)(void *, size_t, void *)"),
    Definition(
      "hsa",
      "HSA",
      "hsa_ven_amd_pcs_data_ready_callback_t",
      "void (*)(void *, size_t, size_t, hsa_ven_amd_pcs_data_copy_callback_t, void *)"
    ),
    Definition(
      "hsa",
      "HSA",
      "hsa_ven_amd_pcs_iterate_configuration_callback_t",
      "hsa_status_t (*)(const hsa_ven_amd_pcs_configuration_t *, void *)"
    ),
    Definition("level-zero", "LevelZero", "ze_host_function_callback_t", "void (*)(void *)"),
    Definition("level-zero", "LevelZero", "zex_mem_graph_free_callback_fn_t", "void (*)(void *)")
  )

  private val byAPIAndName = definitions.map(value => (value.api, value.name) -> value.signature).toMap

  def find(api: String, name: String): Option[String] = byAPIAndName.get(api -> name)
  def forSurface(surface: String): Vector[Definition] = definitions.filter(_.surface == surface)
}
