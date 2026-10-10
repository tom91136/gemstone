package net.kurobako.gemstone.codegen

import java.nio.file.Files
import java.nio.file.Path
import scala.jdk.CollectionConverters.*
import scala.util.Using

/** The native API surfaces: pinned headers to probe, and how each facade covers the declared functions. */
object NativeAPIs {

  /** `entryPointHeader` declares a `PFN_<name>_v<version>` typedef for each revision of a versioned entry point. */
  case class Surface(
      name: String,
      headers: Vector[String],
      include: Path => Path,
      accepts: String => Boolean,
      defines: Vector[String] = Vector.empty,
      dispatchTable: Option[String] = None,
      entryPointHeader: Option[String] = None
  )

  /** A not-supported result; unimplemented functions returning `cType` alias the internal `stub`. */
  case class Status(cType: String, value: String, stub: String)

  enum Coverage {
    case Complete

    /** Functions outside `implemented` alias a status stub or get a default body. `legacy` maps exported names absent
      * from the pinned headers to their alias type. `erased` aliases take the status type instead of the declared one.
      */
    case Partial(
        statuses: Vector[Status],
        implemented: Set[String],
        legacy: Vector[(String, String)] = Vector.empty,
        erased: Boolean = false
    )

    /** Per-function policies in [[VulkanAdapter]]. */
    case Vulkan
  }

  case class Platform(name: String, surfaces: Vector[Surface], coverage: Coverage) {
    def preamble: Vector[String] =
      surfaces.flatMap(_.defines).distinct.map(define => s"#define ${define.replace('=', ' ')}") ++
        surfaces.flatMap(_.headers).distinct.map(header => s"#include <$header>")
  }

  private def names(text: String): Set[String] = text.trim.split("\\s+").toSet

  private def upperCamelAfter(prefix: String)(value: String): Boolean =
    value.startsWith(prefix) && value.drop(prefix.length).headOption.exists(_.isUpper) &&
      value.drop(prefix.length).forall(character => character.isLetterOrDigit || character == '_')

  private def lowerSnakeAfter(prefix: String)(value: String): Boolean =
    value.startsWith(prefix) && value.length > prefix.length &&
      value.drop(prefix.length).forall(character => character.isLower || character.isDigit || character == '_')

  private def findParent(root: Path, name: String): Path =
    Using.resource(Files.walk(root)) { stream =>
      stream.toList.asScala.view.find(path => Files.isRegularFile(path) && path.getFileName.toString == name)
        .map(_.getParent)
        .getOrElse(throw IllegalArgumentException(s"$name is absent below $root"))
    }

  private val cudaInclude: Path => Path = root => findParent(root.resolve("nvidia/cuda"), "cuda.h")

  val platforms: Vector[Platform] = Vector(
    Platform(
      "cuda",
      Vector(
        Surface(
          "cuda-driver",
          Vector("cuda.h"),
          cudaInclude,
          upperCamelAfter("cu"),
          entryPointHeader = Some("cudaTypedefs.h")
        ),
        Surface("cuda-runtime", Vector("cuda_runtime_api.h"), cudaInclude, upperCamelAfter("cuda"))
      ),
      Coverage.Partial(
        Vector(
          Status("CUresult", "CUDA_ERROR_NOT_SUPPORTED", "gemstone_cuda_unsupported_driver"),
          Status("cudaError_t", "cudaErrorNotSupported", "gemstone_cuda_unsupported_runtime")
        ),
        names("""
cuArray3DCreate_v2 cuArray3DGetDescriptor_v2 cuArrayCreate_v2 cuArrayDestroy cuArrayGetDescriptor_v2 cuCtxCreate_v4
cuCtxDestroy_v2 cuCtxDetach cuCtxGetApiVersion cuCtxGetCacheConfig cuCtxGetCurrent cuCtxGetDevice cuCtxGetDevice_v2
cuCtxGetFlags cuCtxGetLimit cuCtxGetSharedMemConfig cuCtxGetStreamPriorityRange cuCtxPopCurrent_v2
cuCtxPushCurrent_v2 cuCtxResetPersistingL2Cache cuCtxSetCacheConfig cuCtxSetCurrent cuCtxSetLimit
cuCtxSetSharedMemConfig cuCtxSynchronize cuCtxSynchronize_v2 cuDeviceCanAccessPeer cuDeviceComputeCapability
cuDeviceGet cuDeviceGetAttribute cuDeviceGetByPCIBusId cuDeviceGetCount cuDeviceGetDefaultMemPool
cuDeviceGetHostAtomicCapabilities cuDeviceGetMemPool cuDeviceGetName cuDeviceGetP2PAtomicCapabilities
cuDeviceGetP2PAttribute cuDeviceGetPCIBusId cuDeviceGetProperties cuDeviceGetTexture1DLinearMaxWidth
cuDeviceGetUuid_v2 cuDevicePrimaryCtxGetState cuDevicePrimaryCtxRelease_v2 cuDevicePrimaryCtxReset_v2
cuDevicePrimaryCtxRetain cuDevicePrimaryCtxSetFlags_v2 cuDeviceSetMemPool cuDeviceTotalMem_v2 cuDriverGetVersion
cuEventCreate cuEventDestroy_v2 cuEventElapsedTime_v2 cuEventQuery cuEventRecord cuEventRecordWithFlags
cuEventSynchronize cuFlushGPUDirectRDMAWrites cuFuncGetAttribute cuFuncSetAttribute cuFuncSetCacheConfig
cuFuncSetSharedMemConfig cuGetErrorName cuGetErrorString cuGetExportTable cuGetProcAddress_v2 cuInit
cuKernelGetAttribute cuKernelGetFunction cuKernelSetAttribute cuLaunchHostFunc cuLaunchHostFunc_v2 cuLaunchKernel
cuLaunchKernelEx
cuLibraryGetGlobal cuLibraryGetKernel cuLibraryGetManaged cuLibraryGetModule cuLibraryLoadData cuLibraryUnload
cuLinkAddData_v2 cuLinkComplete cuLinkCreate_v2 cuLinkDestroy cuMemAllocAsync cuMemAllocFromPoolAsync
cuMemAllocHost_v2 cuMemAllocManaged cuMemAllocPitch_v2 cuMemAlloc_v2 cuMemFreeAsync cuMemFreeHost cuMemFree_v2
cuMemGetAddressRange_v2 cuMemGetDefaultMemPool cuMemGetInfo_v2 cuMemGetMemPool cuMemHostAlloc
cuMemHostGetDevicePointer_v2 cuMemHostGetFlags cuMemHostRegister_v2 cuMemHostUnregister cuMemPoolCreate
cuMemPoolDestroy cuMemPoolGetAttribute cuMemPoolSetAttribute cuMemPoolTrimTo cuMemSetMemPool cuMemcpy
cuMemcpy2DAsync_v2 cuMemcpy2DUnaligned_v2 cuMemcpy2D_v2 cuMemcpy3DAsync_v2 cuMemcpy3D_v2 cuMemcpyAsync
cuMemcpyDtoDAsync_v2 cuMemcpyDtoD_v2 cuMemcpyDtoHAsync_v2 cuMemcpyDtoH_v2 cuMemcpyHtoDAsync_v2 cuMemcpyHtoD_v2
cuMemsetD8Async cuMemsetD8_v2 cuModuleGetFunction cuModuleGetGlobal_v2 cuModuleGetLoadingMode cuModuleGetSurfRef
cuModuleGetTexRef cuModuleLoadData cuModuleLoadDataEx cuModuleLoadFatBinary cuModuleUnload
cuOccupancyAvailableDynamicSMemPerBlock cuOccupancyMaxActiveBlocksPerMultiprocessor
cuOccupancyMaxActiveBlocksPerMultiprocessorWithFlags cuOccupancyMaxActiveClusters cuPointerGetAttribute
cuPointerGetAttributes cuStreamAddCallback cuStreamCreate cuStreamCreateWithPriority cuStreamDestroy_v2
cuStreamGetCaptureInfo_v3 cuStreamGetGreenCtx cuStreamIsCapturing cuStreamQuery cuStreamSynchronize
cuStreamWaitEvent cudaCreateChannelDesc cudaDeviceGetAttribute cudaDeviceSynchronize cudaDriverGetVersion
cudaEventCreate cudaEventCreateWithFlags cudaEventDestroy cudaEventElapsedTime cudaEventQuery cudaEventRecord
cudaEventRecordWithFlags cudaEventSynchronize cudaFree cudaFreeHost cudaFuncGetAttributes cudaGetDevice
cudaGetDeviceCount cudaGetDeviceProperties cudaGetErrorName cudaGetErrorString cudaGetLastError cudaHostAlloc
cudaHostGetDevicePointer cudaHostGetFlags cudaHostRegister cudaHostUnregister cudaMalloc cudaMallocHost
cudaMallocManaged cudaMemcpy cudaMemcpyAsync cudaOccupancyMaxActiveBlocksPerMultiprocessorWithFlags
cudaPeekAtLastError cudaPointerGetAttributes cudaRuntimeGetVersion cudaSetDevice cudaStreamCreate
cudaStreamCreateWithFlags cudaStreamCreateWithPriority cudaStreamDestroy cudaStreamGetCaptureInfo
cudaStreamIsCapturing cudaStreamQuery cudaStreamSynchronize cudaStreamWaitEvent"""),
        // vendor libraries still request these older ABI names directly
        legacy = Vector(
          "cuGraphExecKernelNodeSetParams" -> "decltype(cuGraphExecKernelNodeSetParams_v2)",
          "cuGraphInstantiate"             -> "decltype(cuGraphInstantiateWithFlags)",
          "cuGraphKernelNodeGetParams"     -> "decltype(cuGraphKernelNodeGetParams_v2)",
          "cuMemPrefetchAsync"             -> "decltype(cuMemPrefetchAsync_v2)",
          "cuStreamBatchMemOp"             -> "decltype(cuStreamBatchMemOp_v2)",
          "cuStreamWaitValue32"            -> "decltype(cuStreamWaitValue32_v2)"
        )
      )
    ),
    Platform(
      "hip",
      Vector(Surface(
        "hip",
        Vector("hip/hip_runtime_api.h"),
        _.resolve("amd/hip/include"),
        upperCamelAfter("hip"),
        defines = Vector("__HIP_PLATFORM_AMD__=1")
      )),
      Coverage.Partial(
        Vector(Status("hipError_t", "hipErrorNotSupported", "gemstone_hip_unsupported")),
        names("""
hipCtxPopCurrent hipCtxPushCurrent hipDeviceComputeCapability hipDeviceGet hipDeviceGetAttribute
hipDeviceGetDefaultMemPool hipDeviceGetName hipDevicePrimaryCtxRelease hipDevicePrimaryCtxRetain hipDeviceReset
hipDeviceSynchronize hipDeviceTotalMem hipDriverGetVersion hipEventCreate hipEventCreateWithFlags hipEventDestroy
hipEventElapsedTime hipEventQuery hipEventRecord hipEventRecordWithFlags hipEventSynchronize hipExtGetLastError
hipFree hipFreeAsync hipFreeHost hipFuncGetAttributes hipGetDevice hipGetDeviceCount hipGetDevicePropertiesR0600
hipGetErrorName hipGetErrorString hipGetLastError hipGetStreamDeviceId hipGraphDestroy hipGraphExecDestroy
hipGraphInstantiate hipGraphLaunch hipHostFree hipHostGetDevicePointer hipHostGetFlags hipHostMalloc hipHostRegister
hipHostUnregister hipInit hipLaunchKernel hipMalloc hipMallocAsync hipMallocHost hipMallocManaged hipMemAdvise
hipMemGetInfo hipMemPoolTrimTo hipMemcpy hipMemcpy2D hipMemcpy2DAsync hipMemcpyAsync hipMemcpyDtoD
hipMemcpyDtoDAsync hipMemcpyDtoH hipMemcpyDtoHAsync hipMemcpyFromSymbol hipMemcpyFromSymbolAsync hipMemcpyHtoD
hipMemcpyHtoDAsync hipMemcpyToSymbol hipMemcpyToSymbolAsync hipMemcpyWithStream hipMemset hipMemsetAsync
hipMemsetD32 hipMemsetD8 hipModuleGetFunction hipModuleGetFunctionCount hipModuleLaunchKernel hipModuleLoadData
hipModuleLoadDataEx hipModuleLoadFatBinary hipModuleOccupancyMaxActiveBlocksPerMultiprocessor hipModuleUnload
hipPeekAtLastError hipPointerGetAttributes hipRuntimeGetVersion hipSetDevice hipStreamAddCallback
hipStreamBeginCapture hipStreamCreate hipStreamCreateWithFlags hipStreamCreateWithPriority hipStreamDestroy
hipStreamEndCapture hipStreamGetDevice hipStreamIsCapturing hipStreamQuery hipStreamSynchronize hipStreamWaitEvent"""),
        legacy = Vector(
          "hipKernelGetLibrary",
          "hipKernelGetName",
          "hipLibraryEnumerateKernels",
          "hipOccupancyAvailableDynamicSMemPerBlock",
          "hipStreamCopyAttributes"
        ).map(_ -> "hipError_t()"),
        // the pinned headers overload some entry points, so aliases cannot take the declared type
        erased = true
      )
    ),
    Platform(
      "hsa",
      Vector(Surface(
        "hsa",
        Vector("hsa.h", "hsa_ext_amd.h"),
        _.resolve("amd/hsa/runtime/hsa-runtime/inc"),
        lowerSnakeAfter("hsa_")
      )),
      Coverage.Partial(
        Vector(Status(
          "hsa_status_t",
          "static_cast<hsa_status_t>(HSA_STATUS_ERROR_NOT_SUPPORTED)",
          "gemstone_hsa_unsupported"
        )),
        names("""
hsa_agent_get_info hsa_agent_iterate_isas hsa_agent_iterate_regions hsa_amd_agent_iterate_memory_pools
hsa_amd_agent_memory_pool_get_info hsa_amd_agents_allow_access hsa_amd_memory_async_copy
hsa_amd_memory_async_copy_on_engine hsa_amd_memory_copy_engine_status hsa_amd_memory_fill
hsa_amd_memory_get_preferred_copy_engine hsa_amd_memory_lock hsa_amd_memory_lock_to_pool
hsa_amd_memory_pool_allocate hsa_amd_memory_pool_free hsa_amd_memory_pool_get_info hsa_amd_memory_unlock
hsa_amd_pointer_info hsa_amd_profiling_async_copy_enable hsa_amd_profiling_get_async_copy_time
hsa_amd_profiling_get_dispatch_time hsa_amd_profiling_set_profiler_enabled hsa_amd_register_system_event_handler
hsa_amd_signal_async_handler hsa_amd_signal_create hsa_amd_signal_value_pointer hsa_amd_signal_wait_all
hsa_amd_signal_wait_any hsa_amd_svm_attributes_set hsa_code_object_deserialize hsa_code_object_destroy
hsa_code_object_reader_create_from_file hsa_code_object_reader_create_from_memory hsa_code_object_reader_destroy
hsa_executable_create hsa_executable_create_alt hsa_executable_destroy hsa_executable_freeze
hsa_executable_get_symbol hsa_executable_get_symbol_by_name hsa_executable_load_agent_code_object
hsa_executable_load_code_object hsa_executable_load_program_code_object hsa_executable_symbol_get_info
hsa_executable_validate hsa_init hsa_isa_compatible hsa_isa_from_name hsa_isa_get_info hsa_isa_get_info_alt
hsa_iterate_agents hsa_memory_allocate hsa_memory_assign_agent hsa_memory_copy hsa_memory_deregister hsa_memory_free
hsa_memory_register hsa_queue_add_write_index_acq_rel hsa_queue_add_write_index_acquire
hsa_queue_add_write_index_relaxed hsa_queue_add_write_index_release hsa_queue_add_write_index_scacq_screl
hsa_queue_add_write_index_scacquire hsa_queue_add_write_index_screlease hsa_queue_cas_write_index_acq_rel
hsa_queue_cas_write_index_acquire hsa_queue_cas_write_index_relaxed hsa_queue_cas_write_index_release
hsa_queue_cas_write_index_scacq_screl hsa_queue_cas_write_index_scacquire hsa_queue_cas_write_index_screlease
hsa_queue_create hsa_queue_destroy hsa_queue_inactivate hsa_queue_load_read_index_acquire
hsa_queue_load_read_index_relaxed hsa_queue_load_read_index_scacquire hsa_queue_load_write_index_acquire
hsa_queue_load_write_index_relaxed hsa_queue_load_write_index_scacquire hsa_queue_store_read_index_relaxed
hsa_queue_store_read_index_release hsa_queue_store_read_index_screlease hsa_queue_store_write_index_relaxed
hsa_queue_store_write_index_release hsa_queue_store_write_index_screlease hsa_region_get_info hsa_shut_down
hsa_signal_add_acq_rel hsa_signal_add_acquire hsa_signal_add_relaxed hsa_signal_add_release
hsa_signal_add_scacq_screl hsa_signal_add_scacquire hsa_signal_add_screlease hsa_signal_and_acq_rel
hsa_signal_and_acquire hsa_signal_and_relaxed hsa_signal_and_release hsa_signal_and_scacq_screl
hsa_signal_and_scacquire hsa_signal_and_screlease hsa_signal_cas_acq_rel hsa_signal_cas_acquire
hsa_signal_cas_relaxed hsa_signal_cas_release hsa_signal_cas_scacq_screl hsa_signal_cas_scacquire
hsa_signal_cas_screlease hsa_signal_create hsa_signal_destroy hsa_signal_exchange_acq_rel
hsa_signal_exchange_acquire hsa_signal_exchange_relaxed hsa_signal_exchange_release hsa_signal_exchange_scacq_screl
hsa_signal_exchange_scacquire hsa_signal_exchange_screlease hsa_signal_load_acquire hsa_signal_load_relaxed
hsa_signal_load_scacquire hsa_signal_or_acq_rel hsa_signal_or_acquire hsa_signal_or_relaxed hsa_signal_or_release
hsa_signal_or_scacq_screl hsa_signal_or_scacquire hsa_signal_or_screlease hsa_signal_silent_store_relaxed
hsa_signal_silent_store_screlease hsa_signal_store_relaxed hsa_signal_store_release hsa_signal_store_screlease
hsa_signal_subtract_acq_rel hsa_signal_subtract_acquire hsa_signal_subtract_relaxed hsa_signal_subtract_release
hsa_signal_subtract_scacq_screl hsa_signal_subtract_scacquire hsa_signal_subtract_screlease hsa_signal_wait_acquire
hsa_signal_wait_relaxed hsa_signal_wait_scacquire hsa_signal_xor_acq_rel hsa_signal_xor_acquire
hsa_signal_xor_relaxed hsa_signal_xor_release hsa_signal_xor_scacq_screl hsa_signal_xor_scacquire
hsa_signal_xor_screlease hsa_status_string hsa_system_get_info hsa_system_get_major_extension_table""")
      )
    ),
    Platform(
      "hsakmt",
      Vector(Surface(
        "hsakmt",
        Vector("hsakmt/hsakmt.h"),
        _.resolve("amd/hsa/libhsakmt/include"),
        upperCamelAfter("hsaKmt")
      )),
      Coverage.Partial(
        Vector(Status("HSAKMT_STATUS", "HSAKMT_STATUS_NOT_SUPPORTED", "gemstone_hsakmt_unsupported")),
        names("""
hsaKmtAcquireSystemProperties hsaKmtAllocMemory hsaKmtAllocMemoryAlign hsaKmtAvailableMemory hsaKmtCloseKFD
hsaKmtCreateEvent hsaKmtCreateQueue hsaKmtCreateQueueExt hsaKmtDeregisterMemory hsaKmtDestroyEvent
hsaKmtDestroyQueue hsaKmtFreeMemory hsaKmtGetAMDGPUDeviceHandle hsaKmtGetClockCounters hsaKmtGetNodeCacheProperties
hsaKmtGetNodeIoLinkProperties hsaKmtGetNodeMemoryProperties hsaKmtGetNodeProperties hsaKmtGetQueueInfo
hsaKmtGetRuntimeCapabilities hsaKmtGetTileConfig hsaKmtGetVersion hsaKmtGetXNACKMode hsaKmtMapMemoryToGPU
hsaKmtMapMemoryToGPUNodes hsaKmtModelEnabled hsaKmtOpenKFD hsaKmtQueryEventState hsaKmtQueryPointerInfo
hsaKmtRegisterMemory hsaKmtRegisterMemoryToNodes hsaKmtRegisterMemoryWithFlags hsaKmtReleaseSystemProperties
hsaKmtResetEvent hsaKmtRuntimeDisable hsaKmtRuntimeEnable hsaKmtSVMSetAttr hsaKmtSetEvent hsaKmtSetMemoryPolicy
hsaKmtSetMemoryUserData hsaKmtSetQueueCUMask hsaKmtSetTrapHandler hsaKmtSetXNACKMode hsaKmtUnmapMemoryToGPU
hsaKmtUpdateQueue hsaKmtWaitOnEvent hsaKmtWaitOnEvent_Ext hsaKmtWaitOnMultipleEvents hsaKmtWaitOnMultipleEvents_Ext""")
      )
    ),
    Platform(
      "levelzero",
      Vector(Surface(
        "level-zero-ddi",
        Vector("ze_ddi.h", "zes_ddi.h", "zet_ddi.h"),
        _.resolve("oneapi/level-zero/include"),
        name => name.endsWith("ProcAddrTable") && Vector("zeGet", "zesGet", "zetGet").exists(name.startsWith(_))
      )),
      Coverage.Complete
    ),
    Platform(
      "opencl",
      Vector(Surface(
        "opencl",
        Vector("CL/cl_icd.h", "CL/cl_ext.h", "CL/cl_gl.h"),
        _.resolve("khronos/opencl"),
        upperCamelAfter("cl"),
        defines = "CL_TARGET_OPENCL_VERSION=300" +:
          Vector("1_0", "1_1", "1_2", "2_0", "2_1", "2_2").map(version =>
            s"CL_USE_DEPRECATED_OPENCL_${version}_APIS=1"
          ),
        dispatchTable = Some("_cl_icd_dispatch")
      )),
      Coverage.Partial(
        Vector(Status("cl_int", "CL_INVALID_OPERATION", "gemstone_opencl_unsupported")),
        names("""
clBuildProgram clCloneKernel clCompileProgram clCreateBuffer clCreateBufferWithProperties clCreateCommandQueue
clCreateCommandQueueWithProperties clCreateCommandQueueWithPropertiesKHR clCreateContext clCreateContextFromType clCreateImage clCreateImage2D clCreateImage3D clCreateImageWithProperties clCreateSampler
clCreateSamplerWithProperties
clCreateKernel clCreateKernelsInProgram clCreatePipe clCreateProgramWithBinary clCreateProgramWithIL
clCreateProgramWithSource clCreateSubBuffer clCreateUserEvent clDeviceMemAllocINTEL clEnqueueBarrierWithWaitList
clEnqueueCopyBuffer clEnqueueCopyBufferRect clEnqueueCopyBufferToImage clEnqueueCopyImage
clEnqueueCopyImageToBuffer clEnqueueFillImage clEnqueueMapImage clEnqueueFillBuffer clEnqueueMapBuffer clEnqueueMarkerWithWaitList
clEnqueueMemAdviseINTEL clEnqueueMemFillINTEL clEnqueueMemcpyINTEL clEnqueueMigrateMemObjects clEnqueueNDRangeKernel
clEnqueueReadBuffer clEnqueueReadBufferRect clEnqueueReadImage clEnqueueSVMFree clEnqueueSVMMap clEnqueueSVMMemFill clEnqueueSVMMemcpy
clEnqueueSVMMigrateMem clEnqueueSVMUnmap clEnqueueTask clEnqueueUnmapMemObject clEnqueueWriteBuffer
clEnqueueWriteBufferRect clEnqueueWriteImage clFinish clFlush clGetCommandQueueInfo clGetContextInfo clGetDeviceAndHostTimer
clGetDeviceIDs clGetDeviceInfo clGetEventInfo clGetEventProfilingInfo clGetExtensionFunctionAddress
clGetExtensionFunctionAddressForPlatform clGetHostTimer clGetImageInfo clGetKernelArgInfo clGetKernelInfo
clGetKernelSubGroupInfo clGetKernelSubGroupInfoKHR clGetKernelSuggestedLocalWorkSizeKHR clGetKernelWorkGroupInfo clGetMemAllocInfoINTEL
clGetMemObjectInfo clGetPipeInfo clGetPlatformIDs clGetPlatformInfo clGetProgramBuildInfo clGetProgramInfo clGetSamplerInfo
clGetSupportedImageFormats clHostMemAllocINTEL clIcdGetFunctionAddressForPlatformKHR clIcdGetPlatformIDsKHR
clIcdSetPlatformDispatchDataKHR clLinkProgram clMemBlockingFreeINTEL clMemFreeINTEL clReleaseCommandQueue
clReleaseContext clReleaseDevice clReleaseEvent clReleaseKernel clReleaseMemObject clReleaseProgram clReleaseSampler
clRetainCommandQueue clRetainContext clRetainDevice clRetainEvent clRetainKernel clRetainMemObject clRetainProgram clRetainSampler
clSVMAlloc clSVMFree clSetCommandQueueProperty clSetContextDestructorCallback clSetDefaultDeviceCommandQueue
clSetEventCallback clSetKernelArg clSetKernelArgMemPointerINTEL clSetKernelArgSVMPointer clSetKernelExecInfo
clSetMemObjectDestructorCallback clSetProgramReleaseCallback clSetProgramSpecializationConstant clSetUserEventStatus
clSharedMemAllocINTEL clUnloadCompiler clUnloadPlatformCompiler clWaitForEvents""")
      )
    ),
    Platform(
      "vulkan",
      Vector(Surface(
        "vulkan",
        Vector("vulkan/vulkan.h"),
        _.resolve("khronos/vulkan/include"),
        upperCamelAfter("vk")
      )),
      Coverage.Vulkan
    )
  )
}
