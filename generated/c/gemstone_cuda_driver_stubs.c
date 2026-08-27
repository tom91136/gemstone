#include <stdint.h>

#include "gemstone_protocol.h"

/* ABI-presence stubs. Exact typed marshallers replace these one function at a time. */
int32_t cuArray3DCreate_v2() { return GEMSTONE_STATUS_NOT_SUPPORTED; }

int32_t cuArray3DGetDescriptor_v2() { return GEMSTONE_STATUS_NOT_SUPPORTED; }

int32_t cuArrayCreate_v2() { return GEMSTONE_STATUS_NOT_SUPPORTED; }

int32_t cuArrayDestroy() { return GEMSTONE_STATUS_NOT_SUPPORTED; }

int32_t cuArrayGetDescriptor_v2() { return GEMSTONE_STATUS_NOT_SUPPORTED; }

int32_t cuCtxAttach() { return GEMSTONE_STATUS_NOT_SUPPORTED; }

int32_t cuCtxCreate() { return GEMSTONE_STATUS_NOT_SUPPORTED; }

int32_t cuCtxDestroy() { return GEMSTONE_STATUS_NOT_SUPPORTED; }

int32_t cuCtxDetach() { return GEMSTONE_STATUS_NOT_SUPPORTED; }

int32_t cuCtxGetApiVersion() { return GEMSTONE_STATUS_NOT_SUPPORTED; }

int32_t cuCtxGetCacheConfig() { return GEMSTONE_STATUS_NOT_SUPPORTED; }

int32_t cuCtxGetDevice() { return GEMSTONE_STATUS_NOT_SUPPORTED; }

int32_t cuCtxGetLimit() { return GEMSTONE_STATUS_NOT_SUPPORTED; }

int32_t cuCtxPopCurrent() { return GEMSTONE_STATUS_NOT_SUPPORTED; }

int32_t cuCtxPushCurrent() { return GEMSTONE_STATUS_NOT_SUPPORTED; }

int32_t cuCtxPushCurrent_v2() { return GEMSTONE_STATUS_NOT_SUPPORTED; }

int32_t cuCtxSetCacheConfig() { return GEMSTONE_STATUS_NOT_SUPPORTED; }

int32_t cuCtxSetLimit() { return GEMSTONE_STATUS_NOT_SUPPORTED; }

int32_t cuDeviceComputeCapability() { return GEMSTONE_STATUS_NOT_SUPPORTED; }

int32_t cuDeviceGetAttribute() { return GEMSTONE_STATUS_NOT_SUPPORTED; }

int32_t cuDeviceGetName() { return GEMSTONE_STATUS_NOT_SUPPORTED; }

int32_t cuDeviceGetProperties() { return GEMSTONE_STATUS_NOT_SUPPORTED; }

int32_t cuDevicePrimaryCtxRelease() { return GEMSTONE_STATUS_NOT_SUPPORTED; }

int32_t cuDevicePrimaryCtxRetain() { return GEMSTONE_STATUS_NOT_SUPPORTED; }

int32_t cuDeviceTotalMem() { return GEMSTONE_STATUS_NOT_SUPPORTED; }

int32_t cuDeviceTotalMem_v2() { return GEMSTONE_STATUS_NOT_SUPPORTED; }

int32_t cuEventCreate() { return GEMSTONE_STATUS_NOT_SUPPORTED; }

int32_t cuEventDestroy() { return GEMSTONE_STATUS_NOT_SUPPORTED; }

int32_t cuEventElapsedTime() { return GEMSTONE_STATUS_NOT_SUPPORTED; }

int32_t cuEventQuery() { return GEMSTONE_STATUS_NOT_SUPPORTED; }

int32_t cuEventRecord() { return GEMSTONE_STATUS_NOT_SUPPORTED; }

int32_t cuEventSynchronize() { return GEMSTONE_STATUS_NOT_SUPPORTED; }

int32_t cuFuncGetAttribute() { return GEMSTONE_STATUS_NOT_SUPPORTED; }

int32_t cuFuncSetBlockShape() { return GEMSTONE_STATUS_NOT_SUPPORTED; }

int32_t cuFuncSetCacheConfig() { return GEMSTONE_STATUS_NOT_SUPPORTED; }

int32_t cuFuncSetSharedSize() { return GEMSTONE_STATUS_NOT_SUPPORTED; }

int32_t cuGLCtxCreate() { return GEMSTONE_STATUS_NOT_SUPPORTED; }

int32_t cuGLInit() { return GEMSTONE_STATUS_NOT_SUPPORTED; }

int32_t cuGLRegisterBufferObject() { return GEMSTONE_STATUS_NOT_SUPPORTED; }

int32_t cuGLSetBufferObjectMapFlags() { return GEMSTONE_STATUS_NOT_SUPPORTED; }

int32_t cuGetErrorName() { return GEMSTONE_STATUS_NOT_SUPPORTED; }

int32_t cuGetErrorString() { return GEMSTONE_STATUS_NOT_SUPPORTED; }

int32_t cuGetExportTable() { return GEMSTONE_STATUS_NOT_SUPPORTED; }

int32_t cuGraphicsGLRegisterBuffer() { return GEMSTONE_STATUS_NOT_SUPPORTED; }

int32_t cuGraphicsGLRegisterImage() { return GEMSTONE_STATUS_NOT_SUPPORTED; }

int32_t cuGraphicsMapResources() { return GEMSTONE_STATUS_NOT_SUPPORTED; }

int32_t cuGraphicsResourceGetMappedPointer_v2() { return GEMSTONE_STATUS_NOT_SUPPORTED; }

int32_t cuGraphicsResourceSetMapFlags() { return GEMSTONE_STATUS_NOT_SUPPORTED; }

int32_t cuGraphicsSubResourceGetMappedArray() { return GEMSTONE_STATUS_NOT_SUPPORTED; }

int32_t cuGraphicsUnmapResources() { return GEMSTONE_STATUS_NOT_SUPPORTED; }

int32_t cuGraphicsUnregisterResource() { return GEMSTONE_STATUS_NOT_SUPPORTED; }

int32_t cuLaunch() { return GEMSTONE_STATUS_NOT_SUPPORTED; }

int32_t cuLaunchGrid() { return GEMSTONE_STATUS_NOT_SUPPORTED; }

int32_t cuLaunchGridAsync() { return GEMSTONE_STATUS_NOT_SUPPORTED; }

int32_t cuLaunchHostFunc() { return GEMSTONE_STATUS_NOT_SUPPORTED; }

int32_t cuMemAllocHost() { return GEMSTONE_STATUS_NOT_SUPPORTED; }

int32_t cuMemAllocHost_v2() { return GEMSTONE_STATUS_NOT_SUPPORTED; }

int32_t cuMemAllocManaged() { return GEMSTONE_STATUS_NOT_SUPPORTED; }

int32_t cuMemAllocPitch_v2() { return GEMSTONE_STATUS_NOT_SUPPORTED; }

int32_t cuMemFreeHost() { return GEMSTONE_STATUS_NOT_SUPPORTED; }

int32_t cuMemGetAddressRange_v2() { return GEMSTONE_STATUS_NOT_SUPPORTED; }

int32_t cuMemGetInfo_v2() { return GEMSTONE_STATUS_NOT_SUPPORTED; }

int32_t cuMemHostAlloc() { return GEMSTONE_STATUS_NOT_SUPPORTED; }

int32_t cuMemHostGetDevicePointer_v2() { return GEMSTONE_STATUS_NOT_SUPPORTED; }

int32_t cuMemHostGetFlags() { return GEMSTONE_STATUS_NOT_SUPPORTED; }

int32_t cuMemcpy2DAsync_v2() { return GEMSTONE_STATUS_NOT_SUPPORTED; }

int32_t cuMemcpy2DUnaligned_v2() { return GEMSTONE_STATUS_NOT_SUPPORTED; }

int32_t cuMemcpy2D_v2() { return GEMSTONE_STATUS_NOT_SUPPORTED; }

int32_t cuMemcpy3DAsync_v2() { return GEMSTONE_STATUS_NOT_SUPPORTED; }

int32_t cuMemcpy3D_v2() { return GEMSTONE_STATUS_NOT_SUPPORTED; }

int32_t cuMemcpyAtoA_v2() { return GEMSTONE_STATUS_NOT_SUPPORTED; }

int32_t cuMemcpyAtoD_v2() { return GEMSTONE_STATUS_NOT_SUPPORTED; }

int32_t cuMemcpyAtoHAsync_v2() { return GEMSTONE_STATUS_NOT_SUPPORTED; }

int32_t cuMemcpyAtoH_v2() { return GEMSTONE_STATUS_NOT_SUPPORTED; }

int32_t cuMemcpyDtoA_v2() { return GEMSTONE_STATUS_NOT_SUPPORTED; }

int32_t cuMemcpyDtoDAsync_v2() { return GEMSTONE_STATUS_NOT_SUPPORTED; }

int32_t cuMemcpyDtoHAsync_v2() { return GEMSTONE_STATUS_NOT_SUPPORTED; }

int32_t cuMemcpyHtoAAsync_v2() { return GEMSTONE_STATUS_NOT_SUPPORTED; }

int32_t cuMemcpyHtoA_v2() { return GEMSTONE_STATUS_NOT_SUPPORTED; }

int32_t cuMemcpyHtoDAsync_v2() { return GEMSTONE_STATUS_NOT_SUPPORTED; }

int32_t cuMemcpyHtoH_v2() { return GEMSTONE_STATUS_NOT_SUPPORTED; }

int32_t cuMemsetD16Async() { return GEMSTONE_STATUS_NOT_SUPPORTED; }

int32_t cuMemsetD16_v2() { return GEMSTONE_STATUS_NOT_SUPPORTED; }

int32_t cuMemsetD2D16Async() { return GEMSTONE_STATUS_NOT_SUPPORTED; }

int32_t cuMemsetD2D16_v2() { return GEMSTONE_STATUS_NOT_SUPPORTED; }

int32_t cuMemsetD2D32Async() { return GEMSTONE_STATUS_NOT_SUPPORTED; }

int32_t cuMemsetD2D32_v2() { return GEMSTONE_STATUS_NOT_SUPPORTED; }

int32_t cuMemsetD2D8Async() { return GEMSTONE_STATUS_NOT_SUPPORTED; }

int32_t cuMemsetD2D8_v2() { return GEMSTONE_STATUS_NOT_SUPPORTED; }

int32_t cuMemsetD32Async() { return GEMSTONE_STATUS_NOT_SUPPORTED; }

int32_t cuMemsetD32_v2() { return GEMSTONE_STATUS_NOT_SUPPORTED; }

int32_t cuMemsetD8Async() { return GEMSTONE_STATUS_NOT_SUPPORTED; }

int32_t cuModuleGetGlobal_v2() { return GEMSTONE_STATUS_NOT_SUPPORTED; }

int32_t cuModuleGetSurfRef() { return GEMSTONE_STATUS_NOT_SUPPORTED; }

int32_t cuModuleGetTexRef() { return GEMSTONE_STATUS_NOT_SUPPORTED; }

int32_t cuModuleLoad() { return GEMSTONE_STATUS_NOT_SUPPORTED; }

int32_t cuModuleLoadDataEx() { return GEMSTONE_STATUS_NOT_SUPPORTED; }

int32_t cuModuleLoadFatBinary() { return GEMSTONE_STATUS_NOT_SUPPORTED; }

int32_t cuParamSetSize() { return GEMSTONE_STATUS_NOT_SUPPORTED; }

int32_t cuParamSetTexRef() { return GEMSTONE_STATUS_NOT_SUPPORTED; }

int32_t cuParamSetf() { return GEMSTONE_STATUS_NOT_SUPPORTED; }

int32_t cuParamSeti() { return GEMSTONE_STATUS_NOT_SUPPORTED; }

int32_t cuParamSetv() { return GEMSTONE_STATUS_NOT_SUPPORTED; }

int32_t cuStreamAddCallback() { return GEMSTONE_STATUS_NOT_SUPPORTED; }

int32_t cuStreamCreate() { return GEMSTONE_STATUS_NOT_SUPPORTED; }

int32_t cuStreamDestroy() { return GEMSTONE_STATUS_NOT_SUPPORTED; }

int32_t cuStreamDestroy_v2() { return GEMSTONE_STATUS_NOT_SUPPORTED; }

int32_t cuStreamQuery() { return GEMSTONE_STATUS_NOT_SUPPORTED; }

int32_t cuStreamSynchronize() { return GEMSTONE_STATUS_NOT_SUPPORTED; }

int32_t cuStreamWaitEvent() { return GEMSTONE_STATUS_NOT_SUPPORTED; }

int32_t cuSurfRefGetArray() { return GEMSTONE_STATUS_NOT_SUPPORTED; }

int32_t cuSurfRefSetArray() { return GEMSTONE_STATUS_NOT_SUPPORTED; }

int32_t cuTexRefCreate() { return GEMSTONE_STATUS_NOT_SUPPORTED; }

int32_t cuTexRefDestroy() { return GEMSTONE_STATUS_NOT_SUPPORTED; }

int32_t cuTexRefGetAddressMode() { return GEMSTONE_STATUS_NOT_SUPPORTED; }

int32_t cuTexRefGetAddress_v2() { return GEMSTONE_STATUS_NOT_SUPPORTED; }

int32_t cuTexRefGetArray() { return GEMSTONE_STATUS_NOT_SUPPORTED; }

int32_t cuTexRefGetFilterMode() { return GEMSTONE_STATUS_NOT_SUPPORTED; }

int32_t cuTexRefGetFlags() { return GEMSTONE_STATUS_NOT_SUPPORTED; }

int32_t cuTexRefGetFormat() { return GEMSTONE_STATUS_NOT_SUPPORTED; }

int32_t cuTexRefSetAddress2D_v2() { return GEMSTONE_STATUS_NOT_SUPPORTED; }

int32_t cuTexRefSetAddressMode() { return GEMSTONE_STATUS_NOT_SUPPORTED; }

int32_t cuTexRefSetAddress_v2() { return GEMSTONE_STATUS_NOT_SUPPORTED; }

int32_t cuTexRefSetArray() { return GEMSTONE_STATUS_NOT_SUPPORTED; }

int32_t cuTexRefSetFilterMode() { return GEMSTONE_STATUS_NOT_SUPPORTED; }

int32_t cuTexRefSetFlags() { return GEMSTONE_STATUS_NOT_SUPPORTED; }

int32_t cuTexRefSetFormat() { return GEMSTONE_STATUS_NOT_SUPPORTED; }
