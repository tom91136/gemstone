#include <stdint.h>

#include "gemsi_protocol.h"

/* ABI-presence stubs. Exact typed marshallers replace these one function at a time. */
int32_t cuArray3DCreate_v2() { return GEMSI_STATUS_NOT_SUPPORTED; }

int32_t cuArray3DGetDescriptor_v2() { return GEMSI_STATUS_NOT_SUPPORTED; }

int32_t cuArrayCreate_v2() { return GEMSI_STATUS_NOT_SUPPORTED; }

int32_t cuArrayDestroy() { return GEMSI_STATUS_NOT_SUPPORTED; }

int32_t cuArrayGetDescriptor_v2() { return GEMSI_STATUS_NOT_SUPPORTED; }

int32_t cuCtxAttach() { return GEMSI_STATUS_NOT_SUPPORTED; }

int32_t cuCtxCreate() { return GEMSI_STATUS_NOT_SUPPORTED; }

int32_t cuCtxDestroy() { return GEMSI_STATUS_NOT_SUPPORTED; }

int32_t cuCtxDetach() { return GEMSI_STATUS_NOT_SUPPORTED; }

int32_t cuCtxGetApiVersion() { return GEMSI_STATUS_NOT_SUPPORTED; }

int32_t cuCtxGetCacheConfig() { return GEMSI_STATUS_NOT_SUPPORTED; }

int32_t cuCtxGetDevice() { return GEMSI_STATUS_NOT_SUPPORTED; }

int32_t cuCtxGetLimit() { return GEMSI_STATUS_NOT_SUPPORTED; }

int32_t cuCtxPopCurrent() { return GEMSI_STATUS_NOT_SUPPORTED; }

int32_t cuCtxPushCurrent() { return GEMSI_STATUS_NOT_SUPPORTED; }

int32_t cuCtxPushCurrent_v2() { return GEMSI_STATUS_NOT_SUPPORTED; }

int32_t cuCtxSetCacheConfig() { return GEMSI_STATUS_NOT_SUPPORTED; }

int32_t cuCtxSetLimit() { return GEMSI_STATUS_NOT_SUPPORTED; }

int32_t cuDeviceComputeCapability() { return GEMSI_STATUS_NOT_SUPPORTED; }

int32_t cuDeviceGetAttribute() { return GEMSI_STATUS_NOT_SUPPORTED; }

int32_t cuDeviceGetName() { return GEMSI_STATUS_NOT_SUPPORTED; }

int32_t cuDeviceGetProperties() { return GEMSI_STATUS_NOT_SUPPORTED; }

int32_t cuDevicePrimaryCtxRelease() { return GEMSI_STATUS_NOT_SUPPORTED; }

int32_t cuDevicePrimaryCtxRetain() { return GEMSI_STATUS_NOT_SUPPORTED; }

int32_t cuDeviceTotalMem() { return GEMSI_STATUS_NOT_SUPPORTED; }

int32_t cuDeviceTotalMem_v2() { return GEMSI_STATUS_NOT_SUPPORTED; }

int32_t cuEventCreate() { return GEMSI_STATUS_NOT_SUPPORTED; }

int32_t cuEventDestroy() { return GEMSI_STATUS_NOT_SUPPORTED; }

int32_t cuEventElapsedTime() { return GEMSI_STATUS_NOT_SUPPORTED; }

int32_t cuEventQuery() { return GEMSI_STATUS_NOT_SUPPORTED; }

int32_t cuEventRecord() { return GEMSI_STATUS_NOT_SUPPORTED; }

int32_t cuEventSynchronize() { return GEMSI_STATUS_NOT_SUPPORTED; }

int32_t cuFuncGetAttribute() { return GEMSI_STATUS_NOT_SUPPORTED; }

int32_t cuFuncSetBlockShape() { return GEMSI_STATUS_NOT_SUPPORTED; }

int32_t cuFuncSetCacheConfig() { return GEMSI_STATUS_NOT_SUPPORTED; }

int32_t cuFuncSetSharedSize() { return GEMSI_STATUS_NOT_SUPPORTED; }

int32_t cuGLCtxCreate() { return GEMSI_STATUS_NOT_SUPPORTED; }

int32_t cuGLInit() { return GEMSI_STATUS_NOT_SUPPORTED; }

int32_t cuGLRegisterBufferObject() { return GEMSI_STATUS_NOT_SUPPORTED; }

int32_t cuGLSetBufferObjectMapFlags() { return GEMSI_STATUS_NOT_SUPPORTED; }

int32_t cuGetErrorName() { return GEMSI_STATUS_NOT_SUPPORTED; }

int32_t cuGetErrorString() { return GEMSI_STATUS_NOT_SUPPORTED; }

int32_t cuGetExportTable() { return GEMSI_STATUS_NOT_SUPPORTED; }

int32_t cuGraphicsGLRegisterBuffer() { return GEMSI_STATUS_NOT_SUPPORTED; }

int32_t cuGraphicsGLRegisterImage() { return GEMSI_STATUS_NOT_SUPPORTED; }

int32_t cuGraphicsMapResources() { return GEMSI_STATUS_NOT_SUPPORTED; }

int32_t cuGraphicsResourceGetMappedPointer_v2() { return GEMSI_STATUS_NOT_SUPPORTED; }

int32_t cuGraphicsResourceSetMapFlags() { return GEMSI_STATUS_NOT_SUPPORTED; }

int32_t cuGraphicsSubResourceGetMappedArray() { return GEMSI_STATUS_NOT_SUPPORTED; }

int32_t cuGraphicsUnmapResources() { return GEMSI_STATUS_NOT_SUPPORTED; }

int32_t cuGraphicsUnregisterResource() { return GEMSI_STATUS_NOT_SUPPORTED; }

int32_t cuLaunch() { return GEMSI_STATUS_NOT_SUPPORTED; }

int32_t cuLaunchGrid() { return GEMSI_STATUS_NOT_SUPPORTED; }

int32_t cuLaunchGridAsync() { return GEMSI_STATUS_NOT_SUPPORTED; }

int32_t cuLaunchHostFunc() { return GEMSI_STATUS_NOT_SUPPORTED; }

int32_t cuMemAllocHost() { return GEMSI_STATUS_NOT_SUPPORTED; }

int32_t cuMemAllocHost_v2() { return GEMSI_STATUS_NOT_SUPPORTED; }

int32_t cuMemAllocManaged() { return GEMSI_STATUS_NOT_SUPPORTED; }

int32_t cuMemAllocPitch_v2() { return GEMSI_STATUS_NOT_SUPPORTED; }

int32_t cuMemFreeHost() { return GEMSI_STATUS_NOT_SUPPORTED; }

int32_t cuMemGetAddressRange_v2() { return GEMSI_STATUS_NOT_SUPPORTED; }

int32_t cuMemGetInfo_v2() { return GEMSI_STATUS_NOT_SUPPORTED; }

int32_t cuMemHostAlloc() { return GEMSI_STATUS_NOT_SUPPORTED; }

int32_t cuMemHostGetDevicePointer_v2() { return GEMSI_STATUS_NOT_SUPPORTED; }

int32_t cuMemHostGetFlags() { return GEMSI_STATUS_NOT_SUPPORTED; }

int32_t cuMemcpy2DAsync_v2() { return GEMSI_STATUS_NOT_SUPPORTED; }

int32_t cuMemcpy2DUnaligned_v2() { return GEMSI_STATUS_NOT_SUPPORTED; }

int32_t cuMemcpy2D_v2() { return GEMSI_STATUS_NOT_SUPPORTED; }

int32_t cuMemcpy3DAsync_v2() { return GEMSI_STATUS_NOT_SUPPORTED; }

int32_t cuMemcpy3D_v2() { return GEMSI_STATUS_NOT_SUPPORTED; }

int32_t cuMemcpyAtoA_v2() { return GEMSI_STATUS_NOT_SUPPORTED; }

int32_t cuMemcpyAtoD_v2() { return GEMSI_STATUS_NOT_SUPPORTED; }

int32_t cuMemcpyAtoHAsync_v2() { return GEMSI_STATUS_NOT_SUPPORTED; }

int32_t cuMemcpyAtoH_v2() { return GEMSI_STATUS_NOT_SUPPORTED; }

int32_t cuMemcpyDtoA_v2() { return GEMSI_STATUS_NOT_SUPPORTED; }

int32_t cuMemcpyDtoDAsync_v2() { return GEMSI_STATUS_NOT_SUPPORTED; }

int32_t cuMemcpyDtoHAsync_v2() { return GEMSI_STATUS_NOT_SUPPORTED; }

int32_t cuMemcpyHtoAAsync_v2() { return GEMSI_STATUS_NOT_SUPPORTED; }

int32_t cuMemcpyHtoA_v2() { return GEMSI_STATUS_NOT_SUPPORTED; }

int32_t cuMemcpyHtoDAsync_v2() { return GEMSI_STATUS_NOT_SUPPORTED; }

int32_t cuMemcpyHtoH_v2() { return GEMSI_STATUS_NOT_SUPPORTED; }

int32_t cuMemsetD16Async() { return GEMSI_STATUS_NOT_SUPPORTED; }

int32_t cuMemsetD16_v2() { return GEMSI_STATUS_NOT_SUPPORTED; }

int32_t cuMemsetD2D16Async() { return GEMSI_STATUS_NOT_SUPPORTED; }

int32_t cuMemsetD2D16_v2() { return GEMSI_STATUS_NOT_SUPPORTED; }

int32_t cuMemsetD2D32Async() { return GEMSI_STATUS_NOT_SUPPORTED; }

int32_t cuMemsetD2D32_v2() { return GEMSI_STATUS_NOT_SUPPORTED; }

int32_t cuMemsetD2D8Async() { return GEMSI_STATUS_NOT_SUPPORTED; }

int32_t cuMemsetD2D8_v2() { return GEMSI_STATUS_NOT_SUPPORTED; }

int32_t cuMemsetD32Async() { return GEMSI_STATUS_NOT_SUPPORTED; }

int32_t cuMemsetD32_v2() { return GEMSI_STATUS_NOT_SUPPORTED; }

int32_t cuMemsetD8Async() { return GEMSI_STATUS_NOT_SUPPORTED; }

int32_t cuModuleGetGlobal_v2() { return GEMSI_STATUS_NOT_SUPPORTED; }

int32_t cuModuleGetSurfRef() { return GEMSI_STATUS_NOT_SUPPORTED; }

int32_t cuModuleGetTexRef() { return GEMSI_STATUS_NOT_SUPPORTED; }

int32_t cuModuleLoad() { return GEMSI_STATUS_NOT_SUPPORTED; }

int32_t cuModuleLoadDataEx() { return GEMSI_STATUS_NOT_SUPPORTED; }

int32_t cuModuleLoadFatBinary() { return GEMSI_STATUS_NOT_SUPPORTED; }

int32_t cuParamSetSize() { return GEMSI_STATUS_NOT_SUPPORTED; }

int32_t cuParamSetTexRef() { return GEMSI_STATUS_NOT_SUPPORTED; }

int32_t cuParamSetf() { return GEMSI_STATUS_NOT_SUPPORTED; }

int32_t cuParamSeti() { return GEMSI_STATUS_NOT_SUPPORTED; }

int32_t cuParamSetv() { return GEMSI_STATUS_NOT_SUPPORTED; }

int32_t cuStreamAddCallback() { return GEMSI_STATUS_NOT_SUPPORTED; }

int32_t cuStreamCreate() { return GEMSI_STATUS_NOT_SUPPORTED; }

int32_t cuStreamDestroy() { return GEMSI_STATUS_NOT_SUPPORTED; }

int32_t cuStreamDestroy_v2() { return GEMSI_STATUS_NOT_SUPPORTED; }

int32_t cuStreamQuery() { return GEMSI_STATUS_NOT_SUPPORTED; }

int32_t cuStreamSynchronize() { return GEMSI_STATUS_NOT_SUPPORTED; }

int32_t cuStreamWaitEvent() { return GEMSI_STATUS_NOT_SUPPORTED; }

int32_t cuSurfRefGetArray() { return GEMSI_STATUS_NOT_SUPPORTED; }

int32_t cuSurfRefSetArray() { return GEMSI_STATUS_NOT_SUPPORTED; }

int32_t cuTexRefCreate() { return GEMSI_STATUS_NOT_SUPPORTED; }

int32_t cuTexRefDestroy() { return GEMSI_STATUS_NOT_SUPPORTED; }

int32_t cuTexRefGetAddressMode() { return GEMSI_STATUS_NOT_SUPPORTED; }

int32_t cuTexRefGetAddress_v2() { return GEMSI_STATUS_NOT_SUPPORTED; }

int32_t cuTexRefGetArray() { return GEMSI_STATUS_NOT_SUPPORTED; }

int32_t cuTexRefGetFilterMode() { return GEMSI_STATUS_NOT_SUPPORTED; }

int32_t cuTexRefGetFlags() { return GEMSI_STATUS_NOT_SUPPORTED; }

int32_t cuTexRefGetFormat() { return GEMSI_STATUS_NOT_SUPPORTED; }

int32_t cuTexRefSetAddress2D_v2() { return GEMSI_STATUS_NOT_SUPPORTED; }

int32_t cuTexRefSetAddressMode() { return GEMSI_STATUS_NOT_SUPPORTED; }

int32_t cuTexRefSetAddress_v2() { return GEMSI_STATUS_NOT_SUPPORTED; }

int32_t cuTexRefSetArray() { return GEMSI_STATUS_NOT_SUPPORTED; }

int32_t cuTexRefSetFilterMode() { return GEMSI_STATUS_NOT_SUPPORTED; }

int32_t cuTexRefSetFlags() { return GEMSI_STATUS_NOT_SUPPORTED; }

int32_t cuTexRefSetFormat() { return GEMSI_STATUS_NOT_SUPPORTED; }
