#include <stdint.h>
#include <cuda.h>

#include "gemstone_protocol.h"

typedef struct GemstoneDim3 { uint32_t x; uint32_t y; uint32_t z; } GemstoneDim3;

/* Exact ABI-presence stubs. Trait methods opt into RPC or a native delegate. */
CUresult CUDAAPI cuArray3DCreate_v2(CUarray * pHandle, const CUDA_ARRAY3D_DESCRIPTOR * pAllocateArray) {
  (void)pHandle;
  (void)pAllocateArray;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuArray3DGetDescriptor_v2(CUDA_ARRAY3D_DESCRIPTOR * pArrayDescriptor, CUarray hArray) {
  (void)pArrayDescriptor;
  (void)hArray;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuArrayCreate_v2(CUarray * pHandle, const CUDA_ARRAY_DESCRIPTOR * pAllocateArray) {
  (void)pHandle;
  (void)pAllocateArray;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuArrayDestroy(CUarray hArray) {
  (void)hArray;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuArrayGetDescriptor_v2(CUDA_ARRAY_DESCRIPTOR * pArrayDescriptor, CUarray hArray) {
  (void)pArrayDescriptor;
  (void)hArray;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuArrayGetMemoryRequirements(CUDA_ARRAY_MEMORY_REQUIREMENTS * memoryRequirements, CUarray array, CUdevice device) {
  (void)memoryRequirements;
  (void)array;
  (void)device;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuArrayGetPlane(CUarray * pPlaneArray, CUarray hArray, unsigned int planeIdx) {
  (void)pPlaneArray;
  (void)hArray;
  (void)planeIdx;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuArrayGetSparseProperties(CUDA_ARRAY_SPARSE_PROPERTIES * sparseProperties, CUarray array) {
  (void)sparseProperties;
  (void)array;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuCheckpointProcessCheckpoint(int pid, CUcheckpointCheckpointArgs * args) {
  (void)pid;
  (void)args;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuCheckpointProcessGetRestoreThreadId(int pid, int * tid) {
  (void)pid;
  (void)tid;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuCheckpointProcessGetState(int pid, CUprocessState * state) {
  (void)pid;
  (void)state;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuCheckpointProcessLock(int pid, CUcheckpointLockArgs * args) {
  (void)pid;
  (void)args;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuCheckpointProcessRestore(int pid, CUcheckpointRestoreArgs * args) {
  (void)pid;
  (void)args;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuCheckpointProcessUnlock(int pid, CUcheckpointUnlockArgs * args) {
  (void)pid;
  (void)args;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuCoredumpDeregisterCompleteCallback(CUcoredumpCallbackHandle callback) {
  (void)callback;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuCoredumpDeregisterStartCallback(CUcoredumpCallbackHandle callback) {
  (void)callback;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuCoredumpGetAttribute(CUcoredumpSettings attrib, void * value, size_t * size) {
  (void)attrib;
  (void)value;
  (void)size;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuCoredumpGetAttributeGlobal(CUcoredumpSettings attrib, void * value, size_t * size) {
  (void)attrib;
  (void)value;
  (void)size;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuCoredumpRegisterCompleteCallback(CUcoredumpStatusCallback callback, void * userData, CUcoredumpCallbackHandle * callbackOut) {
  (void)callback;
  (void)userData;
  (void)callbackOut;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuCoredumpRegisterStartCallback(CUcoredumpStatusCallback callback, void * userData, CUcoredumpCallbackHandle * callbackOut) {
  (void)callback;
  (void)userData;
  (void)callbackOut;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuCoredumpSetAttribute(CUcoredumpSettings attrib, void * value, size_t * size) {
  (void)attrib;
  (void)value;
  (void)size;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuCoredumpSetAttributeGlobal(CUcoredumpSettings attrib, void * value, size_t * size) {
  (void)attrib;
  (void)value;
  (void)size;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuCtxAttach(CUcontext * pctx, unsigned int flags) {
  (void)pctx;
  (void)flags;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuCtxDetach(CUcontext ctx) {
  (void)ctx;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuCtxDisablePeerAccess(CUcontext peerContext) {
  (void)peerContext;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuCtxEnablePeerAccess(CUcontext peerContext, unsigned int Flags) {
  (void)peerContext;
  (void)Flags;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuCtxFromGreenCtx(CUcontext * pContext, CUgreenCtx hCtx) {
  (void)pContext;
  (void)hCtx;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuCtxGetApiVersion(CUcontext ctx, unsigned int * version) {
  (void)ctx;
  (void)version;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuCtxGetCacheConfig(CUfunc_cache * pconfig) {
  (void)pconfig;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuCtxGetCurrent(CUcontext * pctx) {
  (void)pctx;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuCtxGetDevResource(CUcontext hCtx, CUdevResource * resource, CUdevResourceType type) {
  (void)hCtx;
  (void)resource;
  (void)type;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuCtxGetDevice(CUdevice * device) {
  (void)device;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuCtxGetDevice_v2(CUdevice * device, CUcontext ctx) {
  (void)device;
  (void)ctx;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuCtxGetExecAffinity(CUexecAffinityParam * pExecAffinity, CUexecAffinityType type) {
  (void)pExecAffinity;
  (void)type;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuCtxGetFlags(unsigned int * flags) {
  (void)flags;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuCtxGetId(CUcontext ctx, unsigned long long * ctxId) {
  (void)ctx;
  (void)ctxId;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuCtxGetLimit(size_t * pvalue, CUlimit limit) {
  (void)pvalue;
  (void)limit;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuCtxGetSharedMemConfig(CUsharedconfig * pConfig) {
  (void)pConfig;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuCtxGetStreamPriorityRange(int * leastPriority, int * greatestPriority) {
  (void)leastPriority;
  (void)greatestPriority;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuCtxPopCurrent_v2(CUcontext * pctx) {
  (void)pctx;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuCtxPushCurrent_v2(CUcontext ctx) {
  (void)ctx;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuCtxRecordEvent(CUcontext hCtx, CUevent hEvent) {
  (void)hCtx;
  (void)hEvent;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuCtxResetPersistingL2Cache(void) {
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuCtxSetCacheConfig(CUfunc_cache config) {
  (void)config;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuCtxSetCurrent(CUcontext ctx) {
  (void)ctx;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuCtxSetFlags(unsigned int flags) {
  (void)flags;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuCtxSetLimit(CUlimit limit, size_t value) {
  (void)limit;
  (void)value;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuCtxSetSharedMemConfig(CUsharedconfig config) {
  (void)config;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuCtxSynchronize_v2(CUcontext ctx) {
  (void)ctx;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuCtxWaitEvent(CUcontext hCtx, CUevent hEvent) {
  (void)hCtx;
  (void)hEvent;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuDestroyExternalMemory(CUexternalMemory extMem) {
  (void)extMem;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuDestroyExternalSemaphore(CUexternalSemaphore extSem) {
  (void)extSem;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuDevResourceGenerateDesc(CUdevResourceDesc * phDesc, CUdevResource * resources, unsigned int nbResources) {
  (void)phDesc;
  (void)resources;
  (void)nbResources;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuDevSmResourceSplit(CUdevResource * result, unsigned int nbGroups, const CUdevResource * input, CUdevResource * remainder, unsigned int flags, CU_DEV_SM_RESOURCE_GROUP_PARAMS * groupParams) {
  (void)result;
  (void)nbGroups;
  (void)input;
  (void)remainder;
  (void)flags;
  (void)groupParams;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuDevSmResourceSplitByCount(CUdevResource * result, unsigned int * nbGroups, const CUdevResource * input, CUdevResource * remainder, unsigned int flags, unsigned int minCount) {
  (void)result;
  (void)nbGroups;
  (void)input;
  (void)remainder;
  (void)flags;
  (void)minCount;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuDeviceCanAccessPeer(int * canAccessPeer, CUdevice dev, CUdevice peerDev) {
  (void)canAccessPeer;
  (void)dev;
  (void)peerDev;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuDeviceComputeCapability(int * major, int * minor, CUdevice dev) {
  (void)major;
  (void)minor;
  (void)dev;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuDeviceGetAttribute(int * pi, CUdevice_attribute attrib, CUdevice dev) {
  (void)pi;
  (void)attrib;
  (void)dev;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuDeviceGetByPCIBusId(CUdevice * dev, const char * pciBusId) {
  (void)dev;
  (void)pciBusId;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuDeviceGetDefaultMemPool(CUmemoryPool * pool_out, CUdevice dev) {
  (void)pool_out;
  (void)dev;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuDeviceGetDevResource(CUdevice device, CUdevResource * resource, CUdevResourceType type) {
  (void)device;
  (void)resource;
  (void)type;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuDeviceGetExecAffinitySupport(int * pi, CUexecAffinityType type, CUdevice dev) {
  (void)pi;
  (void)type;
  (void)dev;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuDeviceGetGraphMemAttribute(CUdevice device, CUgraphMem_attribute attr, void * value) {
  (void)device;
  (void)attr;
  (void)value;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuDeviceGetHostAtomicCapabilities(unsigned int * capabilities, const CUatomicOperation * operations, unsigned int count, CUdevice dev) {
  (void)capabilities;
  (void)operations;
  (void)count;
  (void)dev;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuDeviceGetLuid(char * luid, unsigned int * deviceNodeMask, CUdevice dev) {
  (void)luid;
  (void)deviceNodeMask;
  (void)dev;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuDeviceGetMemPool(CUmemoryPool * pool, CUdevice dev) {
  (void)pool;
  (void)dev;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuDeviceGetName(char * name, int len, CUdevice dev) {
  (void)name;
  (void)len;
  (void)dev;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuDeviceGetNvSciSyncAttributes(void * nvSciSyncAttrList, CUdevice dev, int flags) {
  (void)nvSciSyncAttrList;
  (void)dev;
  (void)flags;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuDeviceGetP2PAtomicCapabilities(unsigned int * capabilities, const CUatomicOperation * operations, unsigned int count, CUdevice srcDevice, CUdevice dstDevice) {
  (void)capabilities;
  (void)operations;
  (void)count;
  (void)srcDevice;
  (void)dstDevice;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuDeviceGetP2PAttribute(int * value, CUdevice_P2PAttribute attrib, CUdevice srcDevice, CUdevice dstDevice) {
  (void)value;
  (void)attrib;
  (void)srcDevice;
  (void)dstDevice;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuDeviceGetPCIBusId(char * pciBusId, int len, CUdevice dev) {
  (void)pciBusId;
  (void)len;
  (void)dev;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuDeviceGetProperties(CUdevprop * prop, CUdevice dev) {
  (void)prop;
  (void)dev;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuDeviceGetTexture1DLinearMaxWidth(size_t * maxWidthInElements, CUarray_format format, unsigned int numChannels, CUdevice dev) {
  (void)maxWidthInElements;
  (void)format;
  (void)numChannels;
  (void)dev;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuDeviceGetUuid_v2(CUuuid * uuid, CUdevice dev) {
  (void)uuid;
  (void)dev;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuDeviceGraphMemTrim(CUdevice device) {
  (void)device;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuDevicePrimaryCtxGetState(CUdevice dev, unsigned int * flags, int * active) {
  (void)dev;
  (void)flags;
  (void)active;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuDevicePrimaryCtxRelease_v2(CUdevice dev) {
  (void)dev;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuDevicePrimaryCtxReset_v2(CUdevice dev) {
  (void)dev;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuDevicePrimaryCtxRetain(CUcontext * pctx, CUdevice dev) {
  (void)pctx;
  (void)dev;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuDevicePrimaryCtxSetFlags_v2(CUdevice dev, unsigned int flags) {
  (void)dev;
  (void)flags;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuDeviceRegisterAsyncNotification(CUdevice device, CUasyncCallback callbackFunc, void * userData, CUasyncCallbackHandle * callback) {
  (void)device;
  (void)callbackFunc;
  (void)userData;
  (void)callback;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuDeviceSetGraphMemAttribute(CUdevice device, CUgraphMem_attribute attr, void * value) {
  (void)device;
  (void)attr;
  (void)value;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuDeviceSetMemPool(CUdevice dev, CUmemoryPool pool) {
  (void)dev;
  (void)pool;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuDeviceTotalMem_v2(size_t * bytes, CUdevice dev) {
  (void)bytes;
  (void)dev;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuDeviceUnregisterAsyncNotification(CUdevice device, CUasyncCallbackHandle callback) {
  (void)device;
  (void)callback;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuEventCreate(CUevent * phEvent, unsigned int Flags) {
  (void)phEvent;
  (void)Flags;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuEventDestroy_v2(CUevent hEvent) {
  (void)hEvent;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuEventElapsedTime_v2(float * pMilliseconds, CUevent hStart, CUevent hEnd) {
  (void)pMilliseconds;
  (void)hStart;
  (void)hEnd;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuEventQuery(CUevent hEvent) {
  (void)hEvent;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuEventRecord(CUevent hEvent, CUstream hStream) {
  (void)hEvent;
  (void)hStream;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuEventRecordWithFlags(CUevent hEvent, CUstream hStream, unsigned int flags) {
  (void)hEvent;
  (void)hStream;
  (void)flags;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuEventSynchronize(CUevent hEvent) {
  (void)hEvent;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuExternalMemoryGetMappedBuffer(CUdeviceptr * devPtr, CUexternalMemory extMem, const CUDA_EXTERNAL_MEMORY_BUFFER_DESC * bufferDesc) {
  (void)devPtr;
  (void)extMem;
  (void)bufferDesc;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuExternalMemoryGetMappedMipmappedArray(CUmipmappedArray * mipmap, CUexternalMemory extMem, const CUDA_EXTERNAL_MEMORY_MIPMAPPED_ARRAY_DESC * mipmapDesc) {
  (void)mipmap;
  (void)extMem;
  (void)mipmapDesc;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuFlushGPUDirectRDMAWrites(CUflushGPUDirectRDMAWritesTarget target, CUflushGPUDirectRDMAWritesScope scope) {
  (void)target;
  (void)scope;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuFuncGetAttribute(int * pi, CUfunction_attribute attrib, CUfunction hfunc) {
  (void)pi;
  (void)attrib;
  (void)hfunc;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuFuncGetModule(CUmodule * hmod, CUfunction hfunc) {
  (void)hmod;
  (void)hfunc;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuFuncGetName(const char ** name, CUfunction hfunc) {
  (void)name;
  (void)hfunc;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuFuncGetParamCount(CUfunction func, size_t * paramCount) {
  (void)func;
  (void)paramCount;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuFuncGetParamInfo(CUfunction func, size_t paramIndex, size_t * paramOffset, size_t * paramSize) {
  (void)func;
  (void)paramIndex;
  (void)paramOffset;
  (void)paramSize;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuFuncIsLoaded(CUfunctionLoadingState * state, CUfunction function) {
  (void)state;
  (void)function;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuFuncLoad(CUfunction function) {
  (void)function;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuFuncSetAttribute(CUfunction hfunc, CUfunction_attribute attrib, int value) {
  (void)hfunc;
  (void)attrib;
  (void)value;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuFuncSetBlockShape(CUfunction hfunc, int x, int y, int z) {
  (void)hfunc;
  (void)x;
  (void)y;
  (void)z;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuFuncSetCacheConfig(CUfunction hfunc, CUfunc_cache config) {
  (void)hfunc;
  (void)config;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuFuncSetSharedMemConfig(CUfunction hfunc, CUsharedconfig config) {
  (void)hfunc;
  (void)config;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuFuncSetSharedSize(CUfunction hfunc, unsigned int bytes) {
  (void)hfunc;
  (void)bytes;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuGetErrorName(CUresult error, const char ** pStr) {
  (void)error;
  (void)pStr;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuGetErrorString(CUresult error, const char ** pStr) {
  (void)error;
  (void)pStr;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuGetExportTable(const void ** ppExportTable, const CUuuid * pExportTableId) {
  (void)ppExportTable;
  (void)pExportTableId;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuGetProcAddress_v2(const char * symbol, void ** pfn, int cudaVersion, cuuint64_t flags, CUdriverProcAddressQueryResult * symbolStatus) {
  (void)symbol;
  (void)pfn;
  (void)cudaVersion;
  (void)flags;
  (void)symbolStatus;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuGraphAddBatchMemOpNode(CUgraphNode * phGraphNode, CUgraph hGraph, const CUgraphNode * dependencies, size_t numDependencies, const CUDA_BATCH_MEM_OP_NODE_PARAMS * nodeParams) {
  (void)phGraphNode;
  (void)hGraph;
  (void)dependencies;
  (void)numDependencies;
  (void)nodeParams;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuGraphAddChildGraphNode(CUgraphNode * phGraphNode, CUgraph hGraph, const CUgraphNode * dependencies, size_t numDependencies, CUgraph childGraph) {
  (void)phGraphNode;
  (void)hGraph;
  (void)dependencies;
  (void)numDependencies;
  (void)childGraph;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuGraphAddDependencies_v2(CUgraph hGraph, const CUgraphNode * from, const CUgraphNode * to, const CUgraphEdgeData * edgeData, size_t numDependencies) {
  (void)hGraph;
  (void)from;
  (void)to;
  (void)edgeData;
  (void)numDependencies;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuGraphAddEmptyNode(CUgraphNode * phGraphNode, CUgraph hGraph, const CUgraphNode * dependencies, size_t numDependencies) {
  (void)phGraphNode;
  (void)hGraph;
  (void)dependencies;
  (void)numDependencies;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuGraphAddEventRecordNode(CUgraphNode * phGraphNode, CUgraph hGraph, const CUgraphNode * dependencies, size_t numDependencies, CUevent event) {
  (void)phGraphNode;
  (void)hGraph;
  (void)dependencies;
  (void)numDependencies;
  (void)event;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuGraphAddEventWaitNode(CUgraphNode * phGraphNode, CUgraph hGraph, const CUgraphNode * dependencies, size_t numDependencies, CUevent event) {
  (void)phGraphNode;
  (void)hGraph;
  (void)dependencies;
  (void)numDependencies;
  (void)event;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuGraphAddExternalSemaphoresSignalNode(CUgraphNode * phGraphNode, CUgraph hGraph, const CUgraphNode * dependencies, size_t numDependencies, const CUDA_EXT_SEM_SIGNAL_NODE_PARAMS * nodeParams) {
  (void)phGraphNode;
  (void)hGraph;
  (void)dependencies;
  (void)numDependencies;
  (void)nodeParams;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuGraphAddExternalSemaphoresWaitNode(CUgraphNode * phGraphNode, CUgraph hGraph, const CUgraphNode * dependencies, size_t numDependencies, const CUDA_EXT_SEM_WAIT_NODE_PARAMS * nodeParams) {
  (void)phGraphNode;
  (void)hGraph;
  (void)dependencies;
  (void)numDependencies;
  (void)nodeParams;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuGraphAddHostNode(CUgraphNode * phGraphNode, CUgraph hGraph, const CUgraphNode * dependencies, size_t numDependencies, const CUDA_HOST_NODE_PARAMS * nodeParams) {
  (void)phGraphNode;
  (void)hGraph;
  (void)dependencies;
  (void)numDependencies;
  (void)nodeParams;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuGraphAddKernelNode_v2(CUgraphNode * phGraphNode, CUgraph hGraph, const CUgraphNode * dependencies, size_t numDependencies, const CUDA_KERNEL_NODE_PARAMS * nodeParams) {
  (void)phGraphNode;
  (void)hGraph;
  (void)dependencies;
  (void)numDependencies;
  (void)nodeParams;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuGraphAddMemAllocNode(CUgraphNode * phGraphNode, CUgraph hGraph, const CUgraphNode * dependencies, size_t numDependencies, CUDA_MEM_ALLOC_NODE_PARAMS * nodeParams) {
  (void)phGraphNode;
  (void)hGraph;
  (void)dependencies;
  (void)numDependencies;
  (void)nodeParams;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuGraphAddMemFreeNode(CUgraphNode * phGraphNode, CUgraph hGraph, const CUgraphNode * dependencies, size_t numDependencies, CUdeviceptr dptr) {
  (void)phGraphNode;
  (void)hGraph;
  (void)dependencies;
  (void)numDependencies;
  (void)dptr;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuGraphAddMemcpyNode(CUgraphNode * phGraphNode, CUgraph hGraph, const CUgraphNode * dependencies, size_t numDependencies, const CUDA_MEMCPY3D * copyParams, CUcontext ctx) {
  (void)phGraphNode;
  (void)hGraph;
  (void)dependencies;
  (void)numDependencies;
  (void)copyParams;
  (void)ctx;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuGraphAddMemsetNode(CUgraphNode * phGraphNode, CUgraph hGraph, const CUgraphNode * dependencies, size_t numDependencies, const CUDA_MEMSET_NODE_PARAMS * memsetParams, CUcontext ctx) {
  (void)phGraphNode;
  (void)hGraph;
  (void)dependencies;
  (void)numDependencies;
  (void)memsetParams;
  (void)ctx;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuGraphAddNode_v2(CUgraphNode * phGraphNode, CUgraph hGraph, const CUgraphNode * dependencies, const CUgraphEdgeData * dependencyData, size_t numDependencies, CUgraphNodeParams * nodeParams) {
  (void)phGraphNode;
  (void)hGraph;
  (void)dependencies;
  (void)dependencyData;
  (void)numDependencies;
  (void)nodeParams;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuGraphBatchMemOpNodeGetParams(CUgraphNode hNode, CUDA_BATCH_MEM_OP_NODE_PARAMS * nodeParams_out) {
  (void)hNode;
  (void)nodeParams_out;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuGraphBatchMemOpNodeSetParams(CUgraphNode hNode, const CUDA_BATCH_MEM_OP_NODE_PARAMS * nodeParams) {
  (void)hNode;
  (void)nodeParams;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuGraphChildGraphNodeGetGraph(CUgraphNode hNode, CUgraph * phGraph) {
  (void)hNode;
  (void)phGraph;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuGraphClone(CUgraph * phGraphClone, CUgraph originalGraph) {
  (void)phGraphClone;
  (void)originalGraph;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuGraphConditionalHandleCreate(CUgraphConditionalHandle * pHandle_out, CUgraph hGraph, CUcontext ctx, unsigned int defaultLaunchValue, unsigned int flags) {
  (void)pHandle_out;
  (void)hGraph;
  (void)ctx;
  (void)defaultLaunchValue;
  (void)flags;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuGraphCreate(CUgraph * phGraph, unsigned int flags) {
  (void)phGraph;
  (void)flags;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuGraphDebugDotPrint(CUgraph hGraph, const char * path, unsigned int flags) {
  (void)hGraph;
  (void)path;
  (void)flags;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuGraphDestroy(CUgraph hGraph) {
  (void)hGraph;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuGraphDestroyNode(CUgraphNode hNode) {
  (void)hNode;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuGraphEventRecordNodeGetEvent(CUgraphNode hNode, CUevent * event_out) {
  (void)hNode;
  (void)event_out;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuGraphEventRecordNodeSetEvent(CUgraphNode hNode, CUevent event) {
  (void)hNode;
  (void)event;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuGraphEventWaitNodeGetEvent(CUgraphNode hNode, CUevent * event_out) {
  (void)hNode;
  (void)event_out;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuGraphEventWaitNodeSetEvent(CUgraphNode hNode, CUevent event) {
  (void)hNode;
  (void)event;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuGraphExecBatchMemOpNodeSetParams(CUgraphExec hGraphExec, CUgraphNode hNode, const CUDA_BATCH_MEM_OP_NODE_PARAMS * nodeParams) {
  (void)hGraphExec;
  (void)hNode;
  (void)nodeParams;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuGraphExecChildGraphNodeSetParams(CUgraphExec hGraphExec, CUgraphNode hNode, CUgraph childGraph) {
  (void)hGraphExec;
  (void)hNode;
  (void)childGraph;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuGraphExecDestroy(CUgraphExec hGraphExec) {
  (void)hGraphExec;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuGraphExecEventRecordNodeSetEvent(CUgraphExec hGraphExec, CUgraphNode hNode, CUevent event) {
  (void)hGraphExec;
  (void)hNode;
  (void)event;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuGraphExecEventWaitNodeSetEvent(CUgraphExec hGraphExec, CUgraphNode hNode, CUevent event) {
  (void)hGraphExec;
  (void)hNode;
  (void)event;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuGraphExecExternalSemaphoresSignalNodeSetParams(CUgraphExec hGraphExec, CUgraphNode hNode, const CUDA_EXT_SEM_SIGNAL_NODE_PARAMS * nodeParams) {
  (void)hGraphExec;
  (void)hNode;
  (void)nodeParams;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuGraphExecExternalSemaphoresWaitNodeSetParams(CUgraphExec hGraphExec, CUgraphNode hNode, const CUDA_EXT_SEM_WAIT_NODE_PARAMS * nodeParams) {
  (void)hGraphExec;
  (void)hNode;
  (void)nodeParams;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuGraphExecGetFlags(CUgraphExec hGraphExec, cuuint64_t * flags) {
  (void)hGraphExec;
  (void)flags;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuGraphExecGetId(CUgraphExec hGraphExec, unsigned int * graphId) {
  (void)hGraphExec;
  (void)graphId;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuGraphExecHostNodeSetParams(CUgraphExec hGraphExec, CUgraphNode hNode, const CUDA_HOST_NODE_PARAMS * nodeParams) {
  (void)hGraphExec;
  (void)hNode;
  (void)nodeParams;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuGraphExecKernelNodeSetParams_v2(CUgraphExec hGraphExec, CUgraphNode hNode, const CUDA_KERNEL_NODE_PARAMS * nodeParams) {
  (void)hGraphExec;
  (void)hNode;
  (void)nodeParams;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuGraphExecMemcpyNodeSetParams(CUgraphExec hGraphExec, CUgraphNode hNode, const CUDA_MEMCPY3D * copyParams, CUcontext ctx) {
  (void)hGraphExec;
  (void)hNode;
  (void)copyParams;
  (void)ctx;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuGraphExecMemsetNodeSetParams(CUgraphExec hGraphExec, CUgraphNode hNode, const CUDA_MEMSET_NODE_PARAMS * memsetParams, CUcontext ctx) {
  (void)hGraphExec;
  (void)hNode;
  (void)memsetParams;
  (void)ctx;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuGraphExecNodeSetParams(CUgraphExec hGraphExec, CUgraphNode hNode, CUgraphNodeParams * nodeParams) {
  (void)hGraphExec;
  (void)hNode;
  (void)nodeParams;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuGraphExecUpdate_v2(CUgraphExec hGraphExec, CUgraph hGraph, CUgraphExecUpdateResultInfo * resultInfo) {
  (void)hGraphExec;
  (void)hGraph;
  (void)resultInfo;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuGraphExternalSemaphoresSignalNodeGetParams(CUgraphNode hNode, CUDA_EXT_SEM_SIGNAL_NODE_PARAMS * params_out) {
  (void)hNode;
  (void)params_out;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuGraphExternalSemaphoresSignalNodeSetParams(CUgraphNode hNode, const CUDA_EXT_SEM_SIGNAL_NODE_PARAMS * nodeParams) {
  (void)hNode;
  (void)nodeParams;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuGraphExternalSemaphoresWaitNodeGetParams(CUgraphNode hNode, CUDA_EXT_SEM_WAIT_NODE_PARAMS * params_out) {
  (void)hNode;
  (void)params_out;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuGraphExternalSemaphoresWaitNodeSetParams(CUgraphNode hNode, const CUDA_EXT_SEM_WAIT_NODE_PARAMS * nodeParams) {
  (void)hNode;
  (void)nodeParams;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuGraphGetEdges_v2(CUgraph hGraph, CUgraphNode * from, CUgraphNode * to, CUgraphEdgeData * edgeData, size_t * numEdges) {
  (void)hGraph;
  (void)from;
  (void)to;
  (void)edgeData;
  (void)numEdges;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuGraphGetId(CUgraph hGraph, unsigned int * graphId) {
  (void)hGraph;
  (void)graphId;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuGraphGetNodes(CUgraph hGraph, CUgraphNode * nodes, size_t * numNodes) {
  (void)hGraph;
  (void)nodes;
  (void)numNodes;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuGraphGetRootNodes(CUgraph hGraph, CUgraphNode * rootNodes, size_t * numRootNodes) {
  (void)hGraph;
  (void)rootNodes;
  (void)numRootNodes;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuGraphHostNodeGetParams(CUgraphNode hNode, CUDA_HOST_NODE_PARAMS * nodeParams) {
  (void)hNode;
  (void)nodeParams;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuGraphHostNodeSetParams(CUgraphNode hNode, const CUDA_HOST_NODE_PARAMS * nodeParams) {
  (void)hNode;
  (void)nodeParams;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuGraphInstantiateWithFlags(CUgraphExec * phGraphExec, CUgraph hGraph, unsigned long long flags) {
  (void)phGraphExec;
  (void)hGraph;
  (void)flags;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuGraphInstantiateWithParams(CUgraphExec * phGraphExec, CUgraph hGraph, CUDA_GRAPH_INSTANTIATE_PARAMS * instantiateParams) {
  (void)phGraphExec;
  (void)hGraph;
  (void)instantiateParams;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuGraphKernelNodeCopyAttributes(CUgraphNode dst, CUgraphNode src) {
  (void)dst;
  (void)src;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuGraphKernelNodeGetAttribute(CUgraphNode hNode, CUkernelNodeAttrID attr, CUkernelNodeAttrValue * value_out) {
  (void)hNode;
  (void)attr;
  (void)value_out;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuGraphKernelNodeGetParams_v2(CUgraphNode hNode, CUDA_KERNEL_NODE_PARAMS * nodeParams) {
  (void)hNode;
  (void)nodeParams;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuGraphKernelNodeSetAttribute(CUgraphNode hNode, CUkernelNodeAttrID attr, const CUkernelNodeAttrValue * value) {
  (void)hNode;
  (void)attr;
  (void)value;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuGraphKernelNodeSetParams_v2(CUgraphNode hNode, const CUDA_KERNEL_NODE_PARAMS * nodeParams) {
  (void)hNode;
  (void)nodeParams;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuGraphLaunch(CUgraphExec hGraphExec, CUstream hStream) {
  (void)hGraphExec;
  (void)hStream;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuGraphMemAllocNodeGetParams(CUgraphNode hNode, CUDA_MEM_ALLOC_NODE_PARAMS * params_out) {
  (void)hNode;
  (void)params_out;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuGraphMemFreeNodeGetParams(CUgraphNode hNode, CUdeviceptr * dptr_out) {
  (void)hNode;
  (void)dptr_out;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuGraphMemcpyNodeGetParams(CUgraphNode hNode, CUDA_MEMCPY3D * nodeParams) {
  (void)hNode;
  (void)nodeParams;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuGraphMemcpyNodeSetParams(CUgraphNode hNode, const CUDA_MEMCPY3D * nodeParams) {
  (void)hNode;
  (void)nodeParams;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuGraphMemsetNodeGetParams(CUgraphNode hNode, CUDA_MEMSET_NODE_PARAMS * nodeParams) {
  (void)hNode;
  (void)nodeParams;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuGraphMemsetNodeSetParams(CUgraphNode hNode, const CUDA_MEMSET_NODE_PARAMS * nodeParams) {
  (void)hNode;
  (void)nodeParams;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuGraphNodeFindInClone(CUgraphNode * phNode, CUgraphNode hOriginalNode, CUgraph hClonedGraph) {
  (void)phNode;
  (void)hOriginalNode;
  (void)hClonedGraph;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuGraphNodeGetContainingGraph(CUgraphNode hNode, CUgraph * phGraph) {
  (void)hNode;
  (void)phGraph;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuGraphNodeGetDependencies_v2(CUgraphNode hNode, CUgraphNode * dependencies, CUgraphEdgeData * edgeData, size_t * numDependencies) {
  (void)hNode;
  (void)dependencies;
  (void)edgeData;
  (void)numDependencies;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuGraphNodeGetDependentNodes_v2(CUgraphNode hNode, CUgraphNode * dependentNodes, CUgraphEdgeData * edgeData, size_t * numDependentNodes) {
  (void)hNode;
  (void)dependentNodes;
  (void)edgeData;
  (void)numDependentNodes;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuGraphNodeGetEnabled(CUgraphExec hGraphExec, CUgraphNode hNode, unsigned int * isEnabled) {
  (void)hGraphExec;
  (void)hNode;
  (void)isEnabled;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuGraphNodeGetLocalId(CUgraphNode hNode, unsigned int * nodeId) {
  (void)hNode;
  (void)nodeId;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuGraphNodeGetParams(CUgraphNode hNode, CUgraphNodeParams * nodeParams) {
  (void)hNode;
  (void)nodeParams;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuGraphNodeGetToolsId(CUgraphNode hNode, unsigned long long * toolsNodeId) {
  (void)hNode;
  (void)toolsNodeId;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuGraphNodeGetType(CUgraphNode hNode, CUgraphNodeType * type) {
  (void)hNode;
  (void)type;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuGraphNodeSetEnabled(CUgraphExec hGraphExec, CUgraphNode hNode, unsigned int isEnabled) {
  (void)hGraphExec;
  (void)hNode;
  (void)isEnabled;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuGraphNodeSetParams(CUgraphNode hNode, CUgraphNodeParams * nodeParams) {
  (void)hNode;
  (void)nodeParams;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuGraphReleaseUserObject(CUgraph graph, CUuserObject object, unsigned int count) {
  (void)graph;
  (void)object;
  (void)count;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuGraphRemoveDependencies_v2(CUgraph hGraph, const CUgraphNode * from, const CUgraphNode * to, const CUgraphEdgeData * edgeData, size_t numDependencies) {
  (void)hGraph;
  (void)from;
  (void)to;
  (void)edgeData;
  (void)numDependencies;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuGraphRetainUserObject(CUgraph graph, CUuserObject object, unsigned int count, unsigned int flags) {
  (void)graph;
  (void)object;
  (void)count;
  (void)flags;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuGraphUpload(CUgraphExec hGraphExec, CUstream hStream) {
  (void)hGraphExec;
  (void)hStream;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuGraphicsMapResources(unsigned int count, CUgraphicsResource * resources, CUstream hStream) {
  (void)count;
  (void)resources;
  (void)hStream;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuGraphicsResourceGetMappedMipmappedArray(CUmipmappedArray * pMipmappedArray, CUgraphicsResource resource) {
  (void)pMipmappedArray;
  (void)resource;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuGraphicsResourceGetMappedPointer_v2(CUdeviceptr * pDevPtr, size_t * pSize, CUgraphicsResource resource) {
  (void)pDevPtr;
  (void)pSize;
  (void)resource;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuGraphicsResourceSetMapFlags_v2(CUgraphicsResource resource, unsigned int flags) {
  (void)resource;
  (void)flags;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuGraphicsSubResourceGetMappedArray(CUarray * pArray, CUgraphicsResource resource, unsigned int arrayIndex, unsigned int mipLevel) {
  (void)pArray;
  (void)resource;
  (void)arrayIndex;
  (void)mipLevel;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuGraphicsUnmapResources(unsigned int count, CUgraphicsResource * resources, CUstream hStream) {
  (void)count;
  (void)resources;
  (void)hStream;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuGraphicsUnregisterResource(CUgraphicsResource resource) {
  (void)resource;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuGreenCtxCreate(CUgreenCtx * phCtx, CUdevResourceDesc desc, CUdevice dev, unsigned int flags) {
  (void)phCtx;
  (void)desc;
  (void)dev;
  (void)flags;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuGreenCtxDestroy(CUgreenCtx hCtx) {
  (void)hCtx;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuGreenCtxGetDevResource(CUgreenCtx hCtx, CUdevResource * resource, CUdevResourceType type) {
  (void)hCtx;
  (void)resource;
  (void)type;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuGreenCtxGetId(CUgreenCtx greenCtx, unsigned long long * greenCtxId) {
  (void)greenCtx;
  (void)greenCtxId;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuGreenCtxRecordEvent(CUgreenCtx hCtx, CUevent hEvent) {
  (void)hCtx;
  (void)hEvent;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuGreenCtxStreamCreate(CUstream * phStream, CUgreenCtx greenCtx, unsigned int flags, int priority) {
  (void)phStream;
  (void)greenCtx;
  (void)flags;
  (void)priority;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuGreenCtxWaitEvent(CUgreenCtx hCtx, CUevent hEvent) {
  (void)hCtx;
  (void)hEvent;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuImportExternalMemory(CUexternalMemory * extMem_out, const CUDA_EXTERNAL_MEMORY_HANDLE_DESC * memHandleDesc) {
  (void)extMem_out;
  (void)memHandleDesc;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuImportExternalSemaphore(CUexternalSemaphore * extSem_out, const CUDA_EXTERNAL_SEMAPHORE_HANDLE_DESC * semHandleDesc) {
  (void)extSem_out;
  (void)semHandleDesc;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuIpcCloseMemHandle(CUdeviceptr dptr) {
  (void)dptr;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuIpcGetEventHandle(CUipcEventHandle * pHandle, CUevent event) {
  (void)pHandle;
  (void)event;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuIpcGetMemHandle(CUipcMemHandle * pHandle, CUdeviceptr dptr) {
  (void)pHandle;
  (void)dptr;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuIpcOpenEventHandle(CUevent * phEvent, CUipcEventHandle handle) {
  (void)phEvent;
  (void)handle;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuIpcOpenMemHandle_v2(CUdeviceptr * pdptr, CUipcMemHandle handle, unsigned int Flags) {
  (void)pdptr;
  (void)handle;
  (void)Flags;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuKernelGetAttribute(int * pi, CUfunction_attribute attrib, CUkernel kernel, CUdevice dev) {
  (void)pi;
  (void)attrib;
  (void)kernel;
  (void)dev;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuKernelGetFunction(CUfunction * pFunc, CUkernel kernel) {
  (void)pFunc;
  (void)kernel;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuKernelGetLibrary(CUlibrary * pLib, CUkernel kernel) {
  (void)pLib;
  (void)kernel;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuKernelGetName(const char ** name, CUkernel hfunc) {
  (void)name;
  (void)hfunc;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuKernelGetParamCount(CUkernel kernel, size_t * paramCount) {
  (void)kernel;
  (void)paramCount;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuKernelGetParamInfo(CUkernel kernel, size_t paramIndex, size_t * paramOffset, size_t * paramSize) {
  (void)kernel;
  (void)paramIndex;
  (void)paramOffset;
  (void)paramSize;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuKernelSetAttribute(CUfunction_attribute attrib, int val, CUkernel kernel, CUdevice dev) {
  (void)attrib;
  (void)val;
  (void)kernel;
  (void)dev;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuKernelSetCacheConfig(CUkernel kernel, CUfunc_cache config, CUdevice dev) {
  (void)kernel;
  (void)config;
  (void)dev;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuLaunch(CUfunction f) {
  (void)f;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuLaunchCooperativeKernel(CUfunction f, unsigned int gridDimX, unsigned int gridDimY, unsigned int gridDimZ, unsigned int blockDimX, unsigned int blockDimY, unsigned int blockDimZ, unsigned int sharedMemBytes, CUstream hStream, void ** kernelParams) {
  (void)f;
  (void)gridDimX;
  (void)gridDimY;
  (void)gridDimZ;
  (void)blockDimX;
  (void)blockDimY;
  (void)blockDimZ;
  (void)sharedMemBytes;
  (void)hStream;
  (void)kernelParams;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuLaunchCooperativeKernelMultiDevice(CUDA_LAUNCH_PARAMS * launchParamsList, unsigned int numDevices, unsigned int flags) {
  (void)launchParamsList;
  (void)numDevices;
  (void)flags;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuLaunchGrid(CUfunction f, int grid_width, int grid_height) {
  (void)f;
  (void)grid_width;
  (void)grid_height;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuLaunchGridAsync(CUfunction f, int grid_width, int grid_height, CUstream hStream) {
  (void)f;
  (void)grid_width;
  (void)grid_height;
  (void)hStream;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuLaunchHostFunc_v2(CUstream hStream, CUhostFn fn, void * userData, unsigned int syncMode) {
  (void)hStream;
  (void)fn;
  (void)userData;
  (void)syncMode;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuLaunchKernelEx(const CUlaunchConfig * config, CUfunction f, void ** kernelParams, void ** extra) {
  (void)config;
  (void)f;
  (void)kernelParams;
  (void)extra;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuLibraryEnumerateKernels(CUkernel * kernels, unsigned int numKernels, CUlibrary lib) {
  (void)kernels;
  (void)numKernels;
  (void)lib;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuLibraryGetGlobal(CUdeviceptr * dptr, size_t * bytes, CUlibrary library, const char * name) {
  (void)dptr;
  (void)bytes;
  (void)library;
  (void)name;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuLibraryGetKernel(CUkernel * pKernel, CUlibrary library, const char * name) {
  (void)pKernel;
  (void)library;
  (void)name;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuLibraryGetKernelCount(unsigned int * count, CUlibrary lib) {
  (void)count;
  (void)lib;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuLibraryGetManaged(CUdeviceptr * dptr, size_t * bytes, CUlibrary library, const char * name) {
  (void)dptr;
  (void)bytes;
  (void)library;
  (void)name;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuLibraryGetModule(CUmodule * pMod, CUlibrary library) {
  (void)pMod;
  (void)library;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuLibraryGetUnifiedFunction(void ** fptr, CUlibrary library, const char * symbol) {
  (void)fptr;
  (void)library;
  (void)symbol;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuLibraryLoadData(CUlibrary * library, const void * code, CUjit_option * jitOptions, void ** jitOptionsValues, unsigned int numJitOptions, CUlibraryOption * libraryOptions, void ** libraryOptionValues, unsigned int numLibraryOptions) {
  (void)library;
  (void)code;
  (void)jitOptions;
  (void)jitOptionsValues;
  (void)numJitOptions;
  (void)libraryOptions;
  (void)libraryOptionValues;
  (void)numLibraryOptions;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuLibraryLoadFromFile(CUlibrary * library, const char * fileName, CUjit_option * jitOptions, void ** jitOptionsValues, unsigned int numJitOptions, CUlibraryOption * libraryOptions, void ** libraryOptionValues, unsigned int numLibraryOptions) {
  (void)library;
  (void)fileName;
  (void)jitOptions;
  (void)jitOptionsValues;
  (void)numJitOptions;
  (void)libraryOptions;
  (void)libraryOptionValues;
  (void)numLibraryOptions;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuLibraryUnload(CUlibrary library) {
  (void)library;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuLinkAddData_v2(CUlinkState state, CUjitInputType type, void * data, size_t size, const char * name, unsigned int numOptions, CUjit_option * options, void ** optionValues) {
  (void)state;
  (void)type;
  (void)data;
  (void)size;
  (void)name;
  (void)numOptions;
  (void)options;
  (void)optionValues;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuLinkAddFile_v2(CUlinkState state, CUjitInputType type, const char * path, unsigned int numOptions, CUjit_option * options, void ** optionValues) {
  (void)state;
  (void)type;
  (void)path;
  (void)numOptions;
  (void)options;
  (void)optionValues;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuLinkComplete(CUlinkState state, void ** cubinOut, size_t * sizeOut) {
  (void)state;
  (void)cubinOut;
  (void)sizeOut;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuLinkCreate_v2(unsigned int numOptions, CUjit_option * options, void ** optionValues, CUlinkState * stateOut) {
  (void)numOptions;
  (void)options;
  (void)optionValues;
  (void)stateOut;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuLinkDestroy(CUlinkState state) {
  (void)state;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuLogicalEndpointAddDevice(CUlogicalEndpointId leId, CUdevice dev) {
  (void)leId;
  (void)dev;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuLogicalEndpointBindAddr(CUlogicalEndpointId leId, CUdevice dev, cuuint64_t offset, void * ptr, cuuint64_t size, unsigned long long flags) {
  (void)leId;
  (void)dev;
  (void)offset;
  (void)ptr;
  (void)size;
  (void)flags;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuLogicalEndpointBindMem(CUlogicalEndpointId leId, CUdevice dev, cuuint64_t offset, CUmemGenericAllocationHandle memHandle, cuuint64_t memOffset, cuuint64_t size, unsigned long long flags) {
  (void)leId;
  (void)dev;
  (void)offset;
  (void)memHandle;
  (void)memOffset;
  (void)size;
  (void)flags;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuLogicalEndpointCreate(CUlogicalEndpointId leId, const CUlogicalEndpointProp * prop) {
  (void)leId;
  (void)prop;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuLogicalEndpointDestroy(CUlogicalEndpointId leId) {
  (void)leId;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuLogicalEndpointExport(void * handle, CUlogicalEndpointId leId, CUlogicalEndpointIpcHandleType handleType) {
  (void)handle;
  (void)leId;
  (void)handleType;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuLogicalEndpointGetLimits(cuuint64_t * bindAlignment, cuuint64_t * maxSize, const CUlogicalEndpointProp * prop) {
  (void)bindAlignment;
  (void)maxSize;
  (void)prop;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuLogicalEndpointIdRelease(CUlogicalEndpointId baseLeId, cuuint32_t count) {
  (void)baseLeId;
  (void)count;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuLogicalEndpointIdReserve(CUlogicalEndpointId * baseLeId, cuuint32_t count) {
  (void)baseLeId;
  (void)count;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuLogicalEndpointImport(CUlogicalEndpointId leId, const void * handle, CUlogicalEndpointIpcHandleType handleType) {
  (void)leId;
  (void)handle;
  (void)handleType;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuLogicalEndpointQuery(CUlogicalEndpointId leId, cuuint32_t count, int * queryStatus) {
  (void)leId;
  (void)count;
  (void)queryStatus;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuLogicalEndpointUnbind(CUlogicalEndpointId leId, CUdevice dev, cuuint64_t offset, cuuint64_t size) {
  (void)leId;
  (void)dev;
  (void)offset;
  (void)size;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuLogsCurrent(CUlogIterator * iterator_out, unsigned int flags) {
  (void)iterator_out;
  (void)flags;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuLogsDumpToFile(CUlogIterator * iterator, const char * pathToFile, unsigned int flags) {
  (void)iterator;
  (void)pathToFile;
  (void)flags;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuLogsDumpToMemory(CUlogIterator * iterator, char * buffer, size_t * size, unsigned int flags) {
  (void)iterator;
  (void)buffer;
  (void)size;
  (void)flags;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuLogsRegisterCallback(CUlogsCallback callbackFunc, void * userData, CUlogsCallbackHandle * callback_out) {
  (void)callbackFunc;
  (void)userData;
  (void)callback_out;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuLogsUnregisterCallback(CUlogsCallbackHandle callback) {
  (void)callback;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuMemAddressFree(CUdeviceptr ptr, size_t size) {
  (void)ptr;
  (void)size;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuMemAddressReserve(CUdeviceptr * ptr, size_t size, size_t alignment, CUdeviceptr addr, unsigned long long flags) {
  (void)ptr;
  (void)size;
  (void)alignment;
  (void)addr;
  (void)flags;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuMemAdvise_v2(CUdeviceptr devPtr, size_t count, CUmem_advise advice, CUmemLocation location) {
  (void)devPtr;
  (void)count;
  (void)advice;
  (void)location;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuMemAllocAsync(CUdeviceptr * dptr, size_t bytesize, CUstream hStream) {
  (void)dptr;
  (void)bytesize;
  (void)hStream;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuMemAllocFromPoolAsync(CUdeviceptr * dptr, size_t bytesize, CUmemoryPool pool, CUstream hStream) {
  (void)dptr;
  (void)bytesize;
  (void)pool;
  (void)hStream;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuMemAllocHost_v2(void ** pp, size_t bytesize) {
  (void)pp;
  (void)bytesize;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuMemAllocManaged(CUdeviceptr * dptr, size_t bytesize, unsigned int flags) {
  (void)dptr;
  (void)bytesize;
  (void)flags;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuMemAllocPitch_v2(CUdeviceptr * dptr, size_t * pPitch, size_t WidthInBytes, size_t Height, unsigned int ElementSizeBytes) {
  (void)dptr;
  (void)pPitch;
  (void)WidthInBytes;
  (void)Height;
  (void)ElementSizeBytes;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuMemBatchDecompressAsync(CUmemDecompressParams * paramsArray, size_t count, unsigned int flags, size_t * errorIndex, CUstream stream) {
  (void)paramsArray;
  (void)count;
  (void)flags;
  (void)errorIndex;
  (void)stream;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuMemCreate(CUmemGenericAllocationHandle * handle, size_t size, const CUmemAllocationProp * prop, unsigned long long flags) {
  (void)handle;
  (void)size;
  (void)prop;
  (void)flags;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuMemDiscardAndPrefetchBatchAsync(CUdeviceptr * dptrs, size_t * sizes, size_t count, CUmemLocation * prefetchLocs, size_t * prefetchLocIdxs, size_t numPrefetchLocs, unsigned long long flags, CUstream hStream) {
  (void)dptrs;
  (void)sizes;
  (void)count;
  (void)prefetchLocs;
  (void)prefetchLocIdxs;
  (void)numPrefetchLocs;
  (void)flags;
  (void)hStream;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuMemDiscardBatchAsync(CUdeviceptr * dptrs, size_t * sizes, size_t count, unsigned long long flags, CUstream hStream) {
  (void)dptrs;
  (void)sizes;
  (void)count;
  (void)flags;
  (void)hStream;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuMemExportToShareableHandle(void * shareableHandle, CUmemGenericAllocationHandle handle, CUmemAllocationHandleType handleType, unsigned long long flags) {
  (void)shareableHandle;
  (void)handle;
  (void)handleType;
  (void)flags;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuMemFreeAsync(CUdeviceptr dptr, CUstream hStream) {
  (void)dptr;
  (void)hStream;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuMemFreeHost(void * p) {
  (void)p;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuMemGetAccess(unsigned long long * flags, const CUmemLocation * location, CUdeviceptr ptr) {
  (void)flags;
  (void)location;
  (void)ptr;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuMemGetAddressRange_v2(CUdeviceptr * pbase, size_t * psize, CUdeviceptr dptr) {
  (void)pbase;
  (void)psize;
  (void)dptr;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuMemGetAllocationGranularity(size_t * granularity, const CUmemAllocationProp * prop, CUmemAllocationGranularity_flags option) {
  (void)granularity;
  (void)prop;
  (void)option;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuMemGetAllocationPropertiesFromHandle(CUmemAllocationProp * prop, CUmemGenericAllocationHandle handle) {
  (void)prop;
  (void)handle;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuMemGetDefaultMemPool(CUmemoryPool * pool_out, CUmemLocation * location, CUmemAllocationType type) {
  (void)pool_out;
  (void)location;
  (void)type;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuMemGetHandleForAddressRange(void * handle, CUdeviceptr dptr, size_t size, CUmemRangeHandleType handleType, unsigned long long flags) {
  (void)handle;
  (void)dptr;
  (void)size;
  (void)handleType;
  (void)flags;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuMemGetInfo_v2(size_t * free, size_t * total) {
  (void)free;
  (void)total;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuMemGetMemPool(CUmemoryPool * pool, CUmemLocation * location, CUmemAllocationType type) {
  (void)pool;
  (void)location;
  (void)type;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuMemHostAlloc(void ** pp, size_t bytesize, unsigned int Flags) {
  (void)pp;
  (void)bytesize;
  (void)Flags;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuMemHostGetDevicePointer_v2(CUdeviceptr * pdptr, void * p, unsigned int Flags) {
  (void)pdptr;
  (void)p;
  (void)Flags;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuMemHostGetFlags(unsigned int * pFlags, void * p) {
  (void)pFlags;
  (void)p;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuMemHostRegister_v2(void * p, size_t bytesize, unsigned int Flags) {
  (void)p;
  (void)bytesize;
  (void)Flags;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuMemHostUnregister(void * p) {
  (void)p;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuMemImportFromShareableHandle(CUmemGenericAllocationHandle * handle, void * osHandle, CUmemAllocationHandleType shHandleType) {
  (void)handle;
  (void)osHandle;
  (void)shHandleType;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuMemMap(CUdeviceptr ptr, size_t size, size_t offset, CUmemGenericAllocationHandle handle, unsigned long long flags) {
  (void)ptr;
  (void)size;
  (void)offset;
  (void)handle;
  (void)flags;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuMemMapArrayAsync(CUarrayMapInfo * mapInfoList, unsigned int count, CUstream hStream) {
  (void)mapInfoList;
  (void)count;
  (void)hStream;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuMemPoolCreate(CUmemoryPool * pool, const CUmemPoolProps * poolProps) {
  (void)pool;
  (void)poolProps;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuMemPoolDestroy(CUmemoryPool pool) {
  (void)pool;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuMemPoolExportPointer(CUmemPoolPtrExportData * shareData_out, CUdeviceptr ptr) {
  (void)shareData_out;
  (void)ptr;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuMemPoolExportToShareableHandle(void * handle_out, CUmemoryPool pool, CUmemAllocationHandleType handleType, unsigned long long flags) {
  (void)handle_out;
  (void)pool;
  (void)handleType;
  (void)flags;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuMemPoolGetAccess(CUmemAccess_flags * flags, CUmemoryPool memPool, CUmemLocation * location) {
  (void)flags;
  (void)memPool;
  (void)location;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuMemPoolGetAttribute(CUmemoryPool pool, CUmemPool_attribute attr, void * value) {
  (void)pool;
  (void)attr;
  (void)value;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuMemPoolImportFromShareableHandle(CUmemoryPool * pool_out, void * handle, CUmemAllocationHandleType handleType, unsigned long long flags) {
  (void)pool_out;
  (void)handle;
  (void)handleType;
  (void)flags;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuMemPoolImportPointer(CUdeviceptr * ptr_out, CUmemoryPool pool, CUmemPoolPtrExportData * shareData) {
  (void)ptr_out;
  (void)pool;
  (void)shareData;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuMemPoolSetAccess(CUmemoryPool pool, const CUmemAccessDesc * map, size_t count) {
  (void)pool;
  (void)map;
  (void)count;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuMemPoolSetAttribute(CUmemoryPool pool, CUmemPool_attribute attr, void * value) {
  (void)pool;
  (void)attr;
  (void)value;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuMemPoolTrimTo(CUmemoryPool pool, size_t minBytesToKeep) {
  (void)pool;
  (void)minBytesToKeep;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuMemPrefetchAsync_v2(CUdeviceptr devPtr, size_t count, CUmemLocation location, unsigned int flags, CUstream hStream) {
  (void)devPtr;
  (void)count;
  (void)location;
  (void)flags;
  (void)hStream;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuMemPrefetchBatchAsync(CUdeviceptr * dptrs, size_t * sizes, size_t count, CUmemLocation * prefetchLocs, size_t * prefetchLocIdxs, size_t numPrefetchLocs, unsigned long long flags, CUstream hStream) {
  (void)dptrs;
  (void)sizes;
  (void)count;
  (void)prefetchLocs;
  (void)prefetchLocIdxs;
  (void)numPrefetchLocs;
  (void)flags;
  (void)hStream;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuMemRangeGetAttribute(void * data, size_t dataSize, CUmem_range_attribute attribute, CUdeviceptr devPtr, size_t count) {
  (void)data;
  (void)dataSize;
  (void)attribute;
  (void)devPtr;
  (void)count;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuMemRangeGetAttributes(void ** data, size_t * dataSizes, CUmem_range_attribute * attributes, size_t numAttributes, CUdeviceptr devPtr, size_t count) {
  (void)data;
  (void)dataSizes;
  (void)attributes;
  (void)numAttributes;
  (void)devPtr;
  (void)count;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuMemRelease(CUmemGenericAllocationHandle handle) {
  (void)handle;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuMemRetainAllocationHandle(CUmemGenericAllocationHandle * handle, void * addr) {
  (void)handle;
  (void)addr;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuMemSetAccess(CUdeviceptr ptr, size_t size, const CUmemAccessDesc * desc, size_t count) {
  (void)ptr;
  (void)size;
  (void)desc;
  (void)count;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuMemSetMemPool(CUmemLocation * location, CUmemAllocationType type, CUmemoryPool pool) {
  (void)location;
  (void)type;
  (void)pool;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuMemUnmap(CUdeviceptr ptr, size_t size) {
  (void)ptr;
  (void)size;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuMemcpy(CUdeviceptr dst, CUdeviceptr src, size_t ByteCount) {
  (void)dst;
  (void)src;
  (void)ByteCount;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuMemcpy2DAsync_v2(const CUDA_MEMCPY2D * pCopy, CUstream hStream) {
  (void)pCopy;
  (void)hStream;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuMemcpy2DUnaligned_v2(const CUDA_MEMCPY2D * pCopy) {
  (void)pCopy;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuMemcpy2D_v2(const CUDA_MEMCPY2D * pCopy) {
  (void)pCopy;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuMemcpy3DAsync_v2(const CUDA_MEMCPY3D * pCopy, CUstream hStream) {
  (void)pCopy;
  (void)hStream;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuMemcpy3DBatchAsync_v2(size_t numOps, CUDA_MEMCPY3D_BATCH_OP * opList, unsigned long long flags, CUstream hStream) {
  (void)numOps;
  (void)opList;
  (void)flags;
  (void)hStream;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuMemcpy3DPeer(const CUDA_MEMCPY3D_PEER * pCopy) {
  (void)pCopy;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuMemcpy3DPeerAsync(const CUDA_MEMCPY3D_PEER * pCopy, CUstream hStream) {
  (void)pCopy;
  (void)hStream;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuMemcpy3DWithAttributesAsync(CUDA_MEMCPY3D_BATCH_OP * op, unsigned long long flags, CUstream hStream) {
  (void)op;
  (void)flags;
  (void)hStream;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuMemcpy3D_v2(const CUDA_MEMCPY3D * pCopy) {
  (void)pCopy;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuMemcpyAsync(CUdeviceptr dst, CUdeviceptr src, size_t ByteCount, CUstream hStream) {
  (void)dst;
  (void)src;
  (void)ByteCount;
  (void)hStream;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuMemcpyAtoA_v2(CUarray dstArray, size_t dstOffset, CUarray srcArray, size_t srcOffset, size_t ByteCount) {
  (void)dstArray;
  (void)dstOffset;
  (void)srcArray;
  (void)srcOffset;
  (void)ByteCount;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuMemcpyAtoD_v2(CUdeviceptr dstDevice, CUarray srcArray, size_t srcOffset, size_t ByteCount) {
  (void)dstDevice;
  (void)srcArray;
  (void)srcOffset;
  (void)ByteCount;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuMemcpyAtoHAsync_v2(void * dstHost, CUarray srcArray, size_t srcOffset, size_t ByteCount, CUstream hStream) {
  (void)dstHost;
  (void)srcArray;
  (void)srcOffset;
  (void)ByteCount;
  (void)hStream;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuMemcpyAtoH_v2(void * dstHost, CUarray srcArray, size_t srcOffset, size_t ByteCount) {
  (void)dstHost;
  (void)srcArray;
  (void)srcOffset;
  (void)ByteCount;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuMemcpyBatchAsync_v2(CUdeviceptr * dsts, CUdeviceptr * srcs, size_t * sizes, size_t count, CUmemcpyAttributes * attrs, size_t * attrsIdxs, size_t numAttrs, CUstream hStream) {
  (void)dsts;
  (void)srcs;
  (void)sizes;
  (void)count;
  (void)attrs;
  (void)attrsIdxs;
  (void)numAttrs;
  (void)hStream;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuMemcpyDtoA_v2(CUarray dstArray, size_t dstOffset, CUdeviceptr srcDevice, size_t ByteCount) {
  (void)dstArray;
  (void)dstOffset;
  (void)srcDevice;
  (void)ByteCount;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuMemcpyDtoDAsync_v2(CUdeviceptr dstDevice, CUdeviceptr srcDevice, size_t ByteCount, CUstream hStream) {
  (void)dstDevice;
  (void)srcDevice;
  (void)ByteCount;
  (void)hStream;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuMemcpyDtoHAsync_v2(void * dstHost, CUdeviceptr srcDevice, size_t ByteCount, CUstream hStream) {
  (void)dstHost;
  (void)srcDevice;
  (void)ByteCount;
  (void)hStream;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuMemcpyHtoAAsync_v2(CUarray dstArray, size_t dstOffset, const void * srcHost, size_t ByteCount, CUstream hStream) {
  (void)dstArray;
  (void)dstOffset;
  (void)srcHost;
  (void)ByteCount;
  (void)hStream;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuMemcpyHtoA_v2(CUarray dstArray, size_t dstOffset, const void * srcHost, size_t ByteCount) {
  (void)dstArray;
  (void)dstOffset;
  (void)srcHost;
  (void)ByteCount;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuMemcpyHtoDAsync_v2(CUdeviceptr dstDevice, const void * srcHost, size_t ByteCount, CUstream hStream) {
  (void)dstDevice;
  (void)srcHost;
  (void)ByteCount;
  (void)hStream;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuMemcpyPeer(CUdeviceptr dstDevice, CUcontext dstContext, CUdeviceptr srcDevice, CUcontext srcContext, size_t ByteCount) {
  (void)dstDevice;
  (void)dstContext;
  (void)srcDevice;
  (void)srcContext;
  (void)ByteCount;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuMemcpyPeerAsync(CUdeviceptr dstDevice, CUcontext dstContext, CUdeviceptr srcDevice, CUcontext srcContext, size_t ByteCount, CUstream hStream) {
  (void)dstDevice;
  (void)dstContext;
  (void)srcDevice;
  (void)srcContext;
  (void)ByteCount;
  (void)hStream;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuMemcpyWithAttributesAsync(CUdeviceptr dst, CUdeviceptr src, size_t size, CUmemcpyAttributes * attr, CUstream hStream) {
  (void)dst;
  (void)src;
  (void)size;
  (void)attr;
  (void)hStream;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuMemsetD16Async(CUdeviceptr dstDevice, unsigned short us, size_t N, CUstream hStream) {
  (void)dstDevice;
  (void)us;
  (void)N;
  (void)hStream;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuMemsetD16_v2(CUdeviceptr dstDevice, unsigned short us, size_t N) {
  (void)dstDevice;
  (void)us;
  (void)N;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuMemsetD2D16Async(CUdeviceptr dstDevice, size_t dstPitch, unsigned short us, size_t Width, size_t Height, CUstream hStream) {
  (void)dstDevice;
  (void)dstPitch;
  (void)us;
  (void)Width;
  (void)Height;
  (void)hStream;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuMemsetD2D16_v2(CUdeviceptr dstDevice, size_t dstPitch, unsigned short us, size_t Width, size_t Height) {
  (void)dstDevice;
  (void)dstPitch;
  (void)us;
  (void)Width;
  (void)Height;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuMemsetD2D32Async(CUdeviceptr dstDevice, size_t dstPitch, unsigned int ui, size_t Width, size_t Height, CUstream hStream) {
  (void)dstDevice;
  (void)dstPitch;
  (void)ui;
  (void)Width;
  (void)Height;
  (void)hStream;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuMemsetD2D32_v2(CUdeviceptr dstDevice, size_t dstPitch, unsigned int ui, size_t Width, size_t Height) {
  (void)dstDevice;
  (void)dstPitch;
  (void)ui;
  (void)Width;
  (void)Height;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuMemsetD2D8Async(CUdeviceptr dstDevice, size_t dstPitch, unsigned char uc, size_t Width, size_t Height, CUstream hStream) {
  (void)dstDevice;
  (void)dstPitch;
  (void)uc;
  (void)Width;
  (void)Height;
  (void)hStream;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuMemsetD2D8_v2(CUdeviceptr dstDevice, size_t dstPitch, unsigned char uc, size_t Width, size_t Height) {
  (void)dstDevice;
  (void)dstPitch;
  (void)uc;
  (void)Width;
  (void)Height;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuMemsetD32Async(CUdeviceptr dstDevice, unsigned int ui, size_t N, CUstream hStream) {
  (void)dstDevice;
  (void)ui;
  (void)N;
  (void)hStream;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuMemsetD32_v2(CUdeviceptr dstDevice, unsigned int ui, size_t N) {
  (void)dstDevice;
  (void)ui;
  (void)N;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuMemsetD8Async(CUdeviceptr dstDevice, unsigned char uc, size_t N, CUstream hStream) {
  (void)dstDevice;
  (void)uc;
  (void)N;
  (void)hStream;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuMipmappedArrayCreate(CUmipmappedArray * pHandle, const CUDA_ARRAY3D_DESCRIPTOR * pMipmappedArrayDesc, unsigned int numMipmapLevels) {
  (void)pHandle;
  (void)pMipmappedArrayDesc;
  (void)numMipmapLevels;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuMipmappedArrayDestroy(CUmipmappedArray hMipmappedArray) {
  (void)hMipmappedArray;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuMipmappedArrayGetLevel(CUarray * pLevelArray, CUmipmappedArray hMipmappedArray, unsigned int level) {
  (void)pLevelArray;
  (void)hMipmappedArray;
  (void)level;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuMipmappedArrayGetMemoryRequirements(CUDA_ARRAY_MEMORY_REQUIREMENTS * memoryRequirements, CUmipmappedArray mipmap, CUdevice device) {
  (void)memoryRequirements;
  (void)mipmap;
  (void)device;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuMipmappedArrayGetSparseProperties(CUDA_ARRAY_SPARSE_PROPERTIES * sparseProperties, CUmipmappedArray mipmap) {
  (void)sparseProperties;
  (void)mipmap;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuModuleEnumerateFunctions(CUfunction * functions, unsigned int numFunctions, CUmodule mod) {
  (void)functions;
  (void)numFunctions;
  (void)mod;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuModuleGetFunctionCount(unsigned int * count, CUmodule mod) {
  (void)count;
  (void)mod;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuModuleGetGlobal_v2(CUdeviceptr * dptr, size_t * bytes, CUmodule hmod, const char * name) {
  (void)dptr;
  (void)bytes;
  (void)hmod;
  (void)name;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuModuleGetLoadingMode(CUmoduleLoadingMode * mode) {
  (void)mode;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuModuleGetSurfRef(CUsurfref * pSurfRef, CUmodule hmod, const char * name) {
  (void)pSurfRef;
  (void)hmod;
  (void)name;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuModuleGetTexRef(CUtexref * pTexRef, CUmodule hmod, const char * name) {
  (void)pTexRef;
  (void)hmod;
  (void)name;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuModuleLoad(CUmodule * module, const char * fname) {
  (void)module;
  (void)fname;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuModuleLoadDataEx(CUmodule * module, const void * image, unsigned int numOptions, CUjit_option * options, void ** optionValues) {
  (void)module;
  (void)image;
  (void)numOptions;
  (void)options;
  (void)optionValues;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuModuleLoadFatBinary(CUmodule * module, const void * fatCubin) {
  (void)module;
  (void)fatCubin;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuMulticastAddDevice(CUmemGenericAllocationHandle mcHandle, CUdevice dev) {
  (void)mcHandle;
  (void)dev;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuMulticastBindAddr(CUmemGenericAllocationHandle mcHandle, size_t mcOffset, CUdeviceptr memptr, size_t size, unsigned long long flags) {
  (void)mcHandle;
  (void)mcOffset;
  (void)memptr;
  (void)size;
  (void)flags;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuMulticastBindAddr_v2(CUmemGenericAllocationHandle mcHandle, CUdevice dev, size_t mcOffset, CUdeviceptr memptr, size_t size, unsigned long long flags) {
  (void)mcHandle;
  (void)dev;
  (void)mcOffset;
  (void)memptr;
  (void)size;
  (void)flags;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuMulticastBindMem(CUmemGenericAllocationHandle mcHandle, size_t mcOffset, CUmemGenericAllocationHandle memHandle, size_t memOffset, size_t size, unsigned long long flags) {
  (void)mcHandle;
  (void)mcOffset;
  (void)memHandle;
  (void)memOffset;
  (void)size;
  (void)flags;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuMulticastBindMem_v2(CUmemGenericAllocationHandle mcHandle, CUdevice dev, size_t mcOffset, CUmemGenericAllocationHandle memHandle, size_t memOffset, size_t size, unsigned long long flags) {
  (void)mcHandle;
  (void)dev;
  (void)mcOffset;
  (void)memHandle;
  (void)memOffset;
  (void)size;
  (void)flags;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuMulticastCreate(CUmemGenericAllocationHandle * mcHandle, const CUmulticastObjectProp * prop) {
  (void)mcHandle;
  (void)prop;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuMulticastGetGranularity(size_t * granularity, const CUmulticastObjectProp * prop, CUmulticastGranularity_flags option) {
  (void)granularity;
  (void)prop;
  (void)option;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuMulticastUnbind(CUmemGenericAllocationHandle mcHandle, CUdevice dev, size_t mcOffset, size_t size) {
  (void)mcHandle;
  (void)dev;
  (void)mcOffset;
  (void)size;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuOccupancyAvailableDynamicSMemPerBlock(size_t * dynamicSmemSize, CUfunction func, int numBlocks, int blockSize) {
  (void)dynamicSmemSize;
  (void)func;
  (void)numBlocks;
  (void)blockSize;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuOccupancyMaxActiveBlocksPerMultiprocessor(int * numBlocks, CUfunction func, int blockSize, size_t dynamicSMemSize) {
  (void)numBlocks;
  (void)func;
  (void)blockSize;
  (void)dynamicSMemSize;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuOccupancyMaxActiveBlocksPerMultiprocessorWithFlags(int * numBlocks, CUfunction func, int blockSize, size_t dynamicSMemSize, unsigned int flags) {
  (void)numBlocks;
  (void)func;
  (void)blockSize;
  (void)dynamicSMemSize;
  (void)flags;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuOccupancyMaxActiveClusters(int * numClusters, CUfunction func, const CUlaunchConfig * config) {
  (void)numClusters;
  (void)func;
  (void)config;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuOccupancyMaxPotentialBlockSize(int * minGridSize, int * blockSize, CUfunction func, CUoccupancyB2DSize blockSizeToDynamicSMemSize, size_t dynamicSMemSize, int blockSizeLimit) {
  (void)minGridSize;
  (void)blockSize;
  (void)func;
  (void)blockSizeToDynamicSMemSize;
  (void)dynamicSMemSize;
  (void)blockSizeLimit;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuOccupancyMaxPotentialBlockSizeWithFlags(int * minGridSize, int * blockSize, CUfunction func, CUoccupancyB2DSize blockSizeToDynamicSMemSize, size_t dynamicSMemSize, int blockSizeLimit, unsigned int flags) {
  (void)minGridSize;
  (void)blockSize;
  (void)func;
  (void)blockSizeToDynamicSMemSize;
  (void)dynamicSMemSize;
  (void)blockSizeLimit;
  (void)flags;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuOccupancyMaxPotentialClusterSize(int * clusterSize, CUfunction func, const CUlaunchConfig * config) {
  (void)clusterSize;
  (void)func;
  (void)config;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuParamSetSize(CUfunction hfunc, unsigned int numbytes) {
  (void)hfunc;
  (void)numbytes;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuParamSetTexRef(CUfunction hfunc, int texunit, CUtexref hTexRef) {
  (void)hfunc;
  (void)texunit;
  (void)hTexRef;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuParamSetf(CUfunction hfunc, int offset, float value) {
  (void)hfunc;
  (void)offset;
  (void)value;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuParamSeti(CUfunction hfunc, int offset, unsigned int value) {
  (void)hfunc;
  (void)offset;
  (void)value;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuParamSetv(CUfunction hfunc, int offset, void * ptr, unsigned int numbytes) {
  (void)hfunc;
  (void)offset;
  (void)ptr;
  (void)numbytes;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuPointerGetAttribute(void * data, CUpointer_attribute attribute, CUdeviceptr ptr) {
  (void)data;
  (void)attribute;
  (void)ptr;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuPointerGetAttributes(unsigned int numAttributes, CUpointer_attribute * attributes, void ** data, CUdeviceptr ptr) {
  (void)numAttributes;
  (void)attributes;
  (void)data;
  (void)ptr;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuPointerSetAttribute(const void * value, CUpointer_attribute attribute, CUdeviceptr ptr) {
  (void)value;
  (void)attribute;
  (void)ptr;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuSignalExternalSemaphoresAsync(const CUexternalSemaphore * extSemArray, const CUDA_EXTERNAL_SEMAPHORE_SIGNAL_PARAMS * paramsArray, unsigned int numExtSems, CUstream stream) {
  (void)extSemArray;
  (void)paramsArray;
  (void)numExtSems;
  (void)stream;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuStreamAttachMemAsync(CUstream hStream, CUdeviceptr dptr, size_t length, unsigned int flags) {
  (void)hStream;
  (void)dptr;
  (void)length;
  (void)flags;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuStreamBatchMemOp_v2(CUstream stream, unsigned int count, CUstreamBatchMemOpParams * paramArray, unsigned int flags) {
  (void)stream;
  (void)count;
  (void)paramArray;
  (void)flags;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuStreamBeginCaptureToCig(CUstream hStream, CUstreamCigCaptureParams * streamCigCaptureParams) {
  (void)hStream;
  (void)streamCigCaptureParams;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuStreamBeginCaptureToGraph(CUstream hStream, CUgraph hGraph, const CUgraphNode * dependencies, const CUgraphEdgeData * dependencyData, size_t numDependencies, CUstreamCaptureMode mode) {
  (void)hStream;
  (void)hGraph;
  (void)dependencies;
  (void)dependencyData;
  (void)numDependencies;
  (void)mode;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuStreamBeginCapture_v2(CUstream hStream, CUstreamCaptureMode mode) {
  (void)hStream;
  (void)mode;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuStreamBeginRecaptureToGraph(CUstream hStream, CUstreamCaptureMode mode, CUgraph hGraph, CUgraphRecaptureCallback callbackFunc, void * userData) {
  (void)hStream;
  (void)mode;
  (void)hGraph;
  (void)callbackFunc;
  (void)userData;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuStreamCopyAttributes(CUstream dst, CUstream src) {
  (void)dst;
  (void)src;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuStreamCreate(CUstream * phStream, unsigned int Flags) {
  (void)phStream;
  (void)Flags;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuStreamCreateWithPriority(CUstream * phStream, unsigned int flags, int priority) {
  (void)phStream;
  (void)flags;
  (void)priority;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuStreamDestroy_v2(CUstream hStream) {
  (void)hStream;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuStreamEndCapture(CUstream hStream, CUgraph * phGraph) {
  (void)hStream;
  (void)phGraph;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuStreamEndCaptureToCig(CUstream hStream) {
  (void)hStream;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuStreamGetAttribute(CUstream hStream, CUstreamAttrID attr, CUstreamAttrValue * value_out) {
  (void)hStream;
  (void)attr;
  (void)value_out;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuStreamGetCaptureInfo_v3(CUstream hStream, CUstreamCaptureStatus * captureStatus_out, cuuint64_t * id_out, CUgraph * graph_out, const CUgraphNode ** dependencies_out, const CUgraphEdgeData ** edgeData_out, size_t * numDependencies_out) {
  (void)hStream;
  (void)captureStatus_out;
  (void)id_out;
  (void)graph_out;
  (void)dependencies_out;
  (void)edgeData_out;
  (void)numDependencies_out;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuStreamGetCtx(CUstream hStream, CUcontext * pctx) {
  (void)hStream;
  (void)pctx;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuStreamGetCtx_v2(CUstream hStream, CUcontext * pCtx, CUgreenCtx * pGreenCtx) {
  (void)hStream;
  (void)pCtx;
  (void)pGreenCtx;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuStreamGetDevResource(CUstream hStream, CUdevResource * resource, CUdevResourceType type) {
  (void)hStream;
  (void)resource;
  (void)type;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuStreamGetDevice(CUstream hStream, CUdevice * device) {
  (void)hStream;
  (void)device;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuStreamGetFlags(CUstream hStream, unsigned int * flags) {
  (void)hStream;
  (void)flags;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuStreamGetGreenCtx(CUstream hStream, CUgreenCtx * phCtx) {
  (void)hStream;
  (void)phCtx;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuStreamGetId(CUstream hStream, unsigned long long * streamId) {
  (void)hStream;
  (void)streamId;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuStreamGetPriority(CUstream hStream, int * priority) {
  (void)hStream;
  (void)priority;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuStreamIsCapturing(CUstream hStream, CUstreamCaptureStatus * captureStatus) {
  (void)hStream;
  (void)captureStatus;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuStreamQuery(CUstream hStream) {
  (void)hStream;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuStreamSetAttribute(CUstream hStream, CUstreamAttrID attr, const CUstreamAttrValue * value) {
  (void)hStream;
  (void)attr;
  (void)value;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuStreamSynchronize(CUstream hStream) {
  (void)hStream;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuStreamUpdateCaptureDependencies_v2(CUstream hStream, CUgraphNode * dependencies, const CUgraphEdgeData * dependencyData, size_t numDependencies, unsigned int flags) {
  (void)hStream;
  (void)dependencies;
  (void)dependencyData;
  (void)numDependencies;
  (void)flags;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuStreamWaitEvent(CUstream hStream, CUevent hEvent, unsigned int Flags) {
  (void)hStream;
  (void)hEvent;
  (void)Flags;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuStreamWaitValue32_v2(CUstream stream, CUdeviceptr addr, cuuint32_t value, unsigned int flags) {
  (void)stream;
  (void)addr;
  (void)value;
  (void)flags;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuStreamWaitValue64_v2(CUstream stream, CUdeviceptr addr, cuuint64_t value, unsigned int flags) {
  (void)stream;
  (void)addr;
  (void)value;
  (void)flags;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuStreamWriteValue32_v2(CUstream stream, CUdeviceptr addr, cuuint32_t value, unsigned int flags) {
  (void)stream;
  (void)addr;
  (void)value;
  (void)flags;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuStreamWriteValue64_v2(CUstream stream, CUdeviceptr addr, cuuint64_t value, unsigned int flags) {
  (void)stream;
  (void)addr;
  (void)value;
  (void)flags;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuSurfObjectCreate(CUsurfObject * pSurfObject, const CUDA_RESOURCE_DESC * pResDesc) {
  (void)pSurfObject;
  (void)pResDesc;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuSurfObjectDestroy(CUsurfObject surfObject) {
  (void)surfObject;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuSurfObjectGetResourceDesc(CUDA_RESOURCE_DESC * pResDesc, CUsurfObject surfObject) {
  (void)pResDesc;
  (void)surfObject;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuSurfRefGetArray(CUarray * phArray, CUsurfref hSurfRef) {
  (void)phArray;
  (void)hSurfRef;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuSurfRefSetArray(CUsurfref hSurfRef, CUarray hArray, unsigned int Flags) {
  (void)hSurfRef;
  (void)hArray;
  (void)Flags;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuTensorMapEncodeIm2col(CUtensorMap * tensorMap, CUtensorMapDataType tensorDataType, cuuint32_t tensorRank, void * globalAddress, const cuuint64_t * globalDim, const cuuint64_t * globalStrides, const int * pixelBoxLowerCorner, const int * pixelBoxUpperCorner, cuuint32_t channelsPerPixel, cuuint32_t pixelsPerColumn, const cuuint32_t * elementStrides, CUtensorMapInterleave interleave, CUtensorMapSwizzle swizzle, CUtensorMapL2promotion l2Promotion, CUtensorMapFloatOOBfill oobFill) {
  (void)tensorMap;
  (void)tensorDataType;
  (void)tensorRank;
  (void)globalAddress;
  (void)globalDim;
  (void)globalStrides;
  (void)pixelBoxLowerCorner;
  (void)pixelBoxUpperCorner;
  (void)channelsPerPixel;
  (void)pixelsPerColumn;
  (void)elementStrides;
  (void)interleave;
  (void)swizzle;
  (void)l2Promotion;
  (void)oobFill;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuTensorMapEncodeIm2colWide(CUtensorMap * tensorMap, CUtensorMapDataType tensorDataType, cuuint32_t tensorRank, void * globalAddress, const cuuint64_t * globalDim, const cuuint64_t * globalStrides, int pixelBoxLowerCornerWidth, int pixelBoxUpperCornerWidth, cuuint32_t channelsPerPixel, cuuint32_t pixelsPerColumn, const cuuint32_t * elementStrides, CUtensorMapInterleave interleave, CUtensorMapIm2ColWideMode mode, CUtensorMapSwizzle swizzle, CUtensorMapL2promotion l2Promotion, CUtensorMapFloatOOBfill oobFill) {
  (void)tensorMap;
  (void)tensorDataType;
  (void)tensorRank;
  (void)globalAddress;
  (void)globalDim;
  (void)globalStrides;
  (void)pixelBoxLowerCornerWidth;
  (void)pixelBoxUpperCornerWidth;
  (void)channelsPerPixel;
  (void)pixelsPerColumn;
  (void)elementStrides;
  (void)interleave;
  (void)mode;
  (void)swizzle;
  (void)l2Promotion;
  (void)oobFill;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuTensorMapEncodeTiled(CUtensorMap * tensorMap, CUtensorMapDataType tensorDataType, cuuint32_t tensorRank, void * globalAddress, const cuuint64_t * globalDim, const cuuint64_t * globalStrides, const cuuint32_t * boxDim, const cuuint32_t * elementStrides, CUtensorMapInterleave interleave, CUtensorMapSwizzle swizzle, CUtensorMapL2promotion l2Promotion, CUtensorMapFloatOOBfill oobFill) {
  (void)tensorMap;
  (void)tensorDataType;
  (void)tensorRank;
  (void)globalAddress;
  (void)globalDim;
  (void)globalStrides;
  (void)boxDim;
  (void)elementStrides;
  (void)interleave;
  (void)swizzle;
  (void)l2Promotion;
  (void)oobFill;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuTensorMapReplaceAddress(CUtensorMap * tensorMap, void * globalAddress) {
  (void)tensorMap;
  (void)globalAddress;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuTexObjectCreate(CUtexObject * pTexObject, const CUDA_RESOURCE_DESC * pResDesc, const CUDA_TEXTURE_DESC * pTexDesc, const CUDA_RESOURCE_VIEW_DESC * pResViewDesc) {
  (void)pTexObject;
  (void)pResDesc;
  (void)pTexDesc;
  (void)pResViewDesc;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuTexObjectDestroy(CUtexObject texObject) {
  (void)texObject;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuTexObjectGetResourceDesc(CUDA_RESOURCE_DESC * pResDesc, CUtexObject texObject) {
  (void)pResDesc;
  (void)texObject;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuTexObjectGetResourceViewDesc(CUDA_RESOURCE_VIEW_DESC * pResViewDesc, CUtexObject texObject) {
  (void)pResViewDesc;
  (void)texObject;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuTexObjectGetTextureDesc(CUDA_TEXTURE_DESC * pTexDesc, CUtexObject texObject) {
  (void)pTexDesc;
  (void)texObject;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuTexRefCreate(CUtexref * pTexRef) {
  (void)pTexRef;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuTexRefDestroy(CUtexref hTexRef) {
  (void)hTexRef;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuTexRefGetAddressMode(CUaddress_mode * pam, CUtexref hTexRef, int dim) {
  (void)pam;
  (void)hTexRef;
  (void)dim;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuTexRefGetAddress_v2(CUdeviceptr * pdptr, CUtexref hTexRef) {
  (void)pdptr;
  (void)hTexRef;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuTexRefGetArray(CUarray * phArray, CUtexref hTexRef) {
  (void)phArray;
  (void)hTexRef;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuTexRefGetBorderColor(float * pBorderColor, CUtexref hTexRef) {
  (void)pBorderColor;
  (void)hTexRef;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuTexRefGetFilterMode(CUfilter_mode * pfm, CUtexref hTexRef) {
  (void)pfm;
  (void)hTexRef;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuTexRefGetFlags(unsigned int * pFlags, CUtexref hTexRef) {
  (void)pFlags;
  (void)hTexRef;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuTexRefGetFormat(CUarray_format * pFormat, int * pNumChannels, CUtexref hTexRef) {
  (void)pFormat;
  (void)pNumChannels;
  (void)hTexRef;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuTexRefGetMaxAnisotropy(int * pmaxAniso, CUtexref hTexRef) {
  (void)pmaxAniso;
  (void)hTexRef;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuTexRefGetMipmapFilterMode(CUfilter_mode * pfm, CUtexref hTexRef) {
  (void)pfm;
  (void)hTexRef;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuTexRefGetMipmapLevelBias(float * pbias, CUtexref hTexRef) {
  (void)pbias;
  (void)hTexRef;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuTexRefGetMipmapLevelClamp(float * pminMipmapLevelClamp, float * pmaxMipmapLevelClamp, CUtexref hTexRef) {
  (void)pminMipmapLevelClamp;
  (void)pmaxMipmapLevelClamp;
  (void)hTexRef;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuTexRefGetMipmappedArray(CUmipmappedArray * phMipmappedArray, CUtexref hTexRef) {
  (void)phMipmappedArray;
  (void)hTexRef;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuTexRefSetAddress2D_v3(CUtexref hTexRef, const CUDA_ARRAY_DESCRIPTOR * desc, CUdeviceptr dptr, size_t Pitch) {
  (void)hTexRef;
  (void)desc;
  (void)dptr;
  (void)Pitch;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuTexRefSetAddressMode(CUtexref hTexRef, int dim, CUaddress_mode am) {
  (void)hTexRef;
  (void)dim;
  (void)am;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuTexRefSetAddress_v2(size_t * ByteOffset, CUtexref hTexRef, CUdeviceptr dptr, size_t bytes) {
  (void)ByteOffset;
  (void)hTexRef;
  (void)dptr;
  (void)bytes;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuTexRefSetArray(CUtexref hTexRef, CUarray hArray, unsigned int Flags) {
  (void)hTexRef;
  (void)hArray;
  (void)Flags;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuTexRefSetBorderColor(CUtexref hTexRef, float * pBorderColor) {
  (void)hTexRef;
  (void)pBorderColor;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuTexRefSetFilterMode(CUtexref hTexRef, CUfilter_mode fm) {
  (void)hTexRef;
  (void)fm;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuTexRefSetFlags(CUtexref hTexRef, unsigned int Flags) {
  (void)hTexRef;
  (void)Flags;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuTexRefSetFormat(CUtexref hTexRef, CUarray_format fmt, int NumPackedComponents) {
  (void)hTexRef;
  (void)fmt;
  (void)NumPackedComponents;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuTexRefSetMaxAnisotropy(CUtexref hTexRef, unsigned int maxAniso) {
  (void)hTexRef;
  (void)maxAniso;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuTexRefSetMipmapFilterMode(CUtexref hTexRef, CUfilter_mode fm) {
  (void)hTexRef;
  (void)fm;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuTexRefSetMipmapLevelBias(CUtexref hTexRef, float bias) {
  (void)hTexRef;
  (void)bias;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuTexRefSetMipmapLevelClamp(CUtexref hTexRef, float minMipmapLevelClamp, float maxMipmapLevelClamp) {
  (void)hTexRef;
  (void)minMipmapLevelClamp;
  (void)maxMipmapLevelClamp;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuTexRefSetMipmappedArray(CUtexref hTexRef, CUmipmappedArray hMipmappedArray, unsigned int Flags) {
  (void)hTexRef;
  (void)hMipmappedArray;
  (void)Flags;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuThreadExchangeStreamCaptureMode(CUstreamCaptureMode * mode) {
  (void)mode;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuUserObjectCreate(CUuserObject * object_out, void * ptr, CUhostFn destroy, unsigned int initialRefcount, unsigned int flags) {
  (void)object_out;
  (void)ptr;
  (void)destroy;
  (void)initialRefcount;
  (void)flags;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuUserObjectRelease(CUuserObject object, unsigned int count) {
  (void)object;
  (void)count;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuUserObjectRetain(CUuserObject object, unsigned int count) {
  (void)object;
  (void)count;
  return CUDA_ERROR_NOT_SUPPORTED;
}

CUresult CUDAAPI cuWaitExternalSemaphoresAsync(const CUexternalSemaphore * extSemArray, const CUDA_EXTERNAL_SEMAPHORE_WAIT_PARAMS * paramsArray, unsigned int numExtSems, CUstream stream) {
  (void)extSemArray;
  (void)paramsArray;
  (void)numExtSems;
  (void)stream;
  return CUDA_ERROR_NOT_SUPPORTED;
}

/* Thin wrappers generated from retained delegate[...] method bodies. */
extern int32_t CUDAAPI gemstone_cudaGetKernel(void ** kernel, const void * hostFunction);

int32_t CUDAAPI __cudaGetKernel(void ** kernel, const void * hostFunction) {
  return gemstone_cudaGetKernel(kernel, hostFunction);
}

extern int32_t CUDAAPI gemstone_cudaLaunchKernel(void * kernel, GemstoneDim3 grid, GemstoneDim3 block, void ** arguments, size_t sharedMemory, void * stream);

int32_t CUDAAPI __cudaLaunchKernel(void * kernel, GemstoneDim3 grid, GemstoneDim3 block, void ** arguments, size_t sharedMemory, void * stream) {
  return gemstone_cudaLaunchKernel(kernel, grid, block, arguments, sharedMemory, stream);
}

extern int32_t CUDAAPI gemstone_cudaPopCallConfiguration(GemstoneDim3 * grid, GemstoneDim3 * block, size_t * sharedMemory, void * stream);

int32_t CUDAAPI __cudaPopCallConfiguration(GemstoneDim3 * grid, GemstoneDim3 * block, size_t * sharedMemory, void * stream) {
  return gemstone_cudaPopCallConfiguration(grid, block, sharedMemory, stream);
}

extern unsigned int CUDAAPI gemstone_cudaPushCallConfiguration(GemstoneDim3 grid, GemstoneDim3 block, size_t sharedMemory, void * stream);

unsigned int CUDAAPI __cudaPushCallConfiguration(GemstoneDim3 grid, GemstoneDim3 block, size_t sharedMemory, void * stream) {
  return gemstone_cudaPushCallConfiguration(grid, block, sharedMemory, stream);
}

extern void ** CUDAAPI gemstone_cudaRegisterFatBinary(void * fatBinary);

void ** CUDAAPI __cudaRegisterFatBinary(void * fatBinary) {
  return gemstone_cudaRegisterFatBinary(fatBinary);
}

extern void CUDAAPI gemstone_cudaRegisterFatBinaryEnd(void ** handle);

void CUDAAPI __cudaRegisterFatBinaryEnd(void ** handle) {
  gemstone_cudaRegisterFatBinaryEnd(handle);
}

extern void CUDAAPI gemstone_cudaRegisterFunction(void ** handle, const char * hostFunction, char * deviceFunction, const char * deviceName, int32_t threadLimit, void * threadId, void * blockId, void * blockDimension, void * gridDimension, int32_t * warpSize);

void CUDAAPI __cudaRegisterFunction(void ** handle, const char * hostFunction, char * deviceFunction, const char * deviceName, int32_t threadLimit, void * threadId, void * blockId, void * blockDimension, void * gridDimension, int32_t * warpSize) {
  gemstone_cudaRegisterFunction(handle, hostFunction, deviceFunction, deviceName, threadLimit, threadId, blockId, blockDimension, gridDimension, warpSize);
}

extern void CUDAAPI gemstone_cudaRegisterVar(void ** handle, char * hostVariable, char * deviceAddress, const char * deviceName, int32_t external, size_t size, int32_t constant, int32_t global);

void CUDAAPI __cudaRegisterVar(void ** handle, char * hostVariable, char * deviceAddress, const char * deviceName, int32_t external, size_t size, int32_t constant, int32_t global) {
  gemstone_cudaRegisterVar(handle, hostVariable, deviceAddress, deviceName, external, size, constant, global);
}

extern void CUDAAPI gemstone_cudaUnregisterFatBinary(void ** handle);

void CUDAAPI __cudaUnregisterFatBinary(void ** handle) {
  gemstone_cudaUnregisterFatBinary(handle);
}

extern CUresult CUDAAPI gemstone_cuCtxCreate_v4(CUcontext * pctx, CUctxCreateParams * ctxCreateParams, unsigned int flags, CUdevice dev);

CUresult CUDAAPI cuCtxCreate_v4(CUcontext * pctx, CUctxCreateParams * ctxCreateParams, unsigned int flags, CUdevice dev) {
  return gemstone_cuCtxCreate_v4(pctx, ctxCreateParams, flags, dev);
}
