/* Generated from the official API manifest format 6. Regenerate with: sbt codegen */
#pragma once

#include <gemstone/export.h>
#include <hsakmt/hsakmt.h>

#ifdef __cplusplus
extern "C" {
#endif

extern HSAKMT_STATUS hsaKmtAcquireSystemProperties(HsaSystemProperties *) GEMSTONE_PUBLIC_API;
extern HSAKMT_STATUS hsaKmtAisReadWriteFile(void *, HSAuint64, HSAint32, HSAint64, HsaAisFlags, HSAuint64 *, HSAint32 *) GEMSTONE_PUBLIC_API;
extern HSAKMT_STATUS hsaKmtAllocMemory(HSAuint32, HSAuint64, HsaMemFlags, void **) GEMSTONE_PUBLIC_API;
extern HSAKMT_STATUS hsaKmtAllocMemoryAlign(HSAuint32, HSAuint64, HSAuint64, HsaMemFlags, void **) GEMSTONE_PUBLIC_API;
extern HSAKMT_STATUS hsaKmtAllocQueueGWS(HSA_QUEUEID, HSAuint32, HSAuint32 *) GEMSTONE_PUBLIC_API;
extern HSAKMT_STATUS hsaKmtAvailableMemory(HSAuint32, HSAuint64 *) GEMSTONE_PUBLIC_API;
extern HSAKMT_STATUS hsaKmtCheckRuntimeDebugSupport(void) GEMSTONE_PUBLIC_API;
extern HSAKMT_STATUS hsaKmtCloseKFD(void) GEMSTONE_PUBLIC_API;
extern HSAKMT_STATUS hsaKmtCreateEvent(HsaEventDescriptor *, bool, bool, HsaEvent **) GEMSTONE_PUBLIC_API;
extern HSAKMT_STATUS hsaKmtCreateQueue(HSAuint32, HSA_QUEUE_TYPE, HSAuint32, HSA_QUEUE_PRIORITY, void *, HSAuint64, HsaEvent *, HsaQueueResource *) GEMSTONE_PUBLIC_API;
extern HSAKMT_STATUS hsaKmtCreateQueueExt(HSAuint32, HSA_QUEUE_TYPE, HSAuint32, HSA_QUEUE_PRIORITY, HSAuint32, void *, HSAuint64, HsaEvent *, HsaQueueResource *) GEMSTONE_PUBLIC_API;
extern HSAKMT_STATUS hsaKmtDbgAddressWatch(HSAuint32, HSAuint32, HSA_DBG_WATCH_MODE *, void **, HSAuint64 *, HsaEvent **) GEMSTONE_PUBLIC_API;
extern HSAKMT_STATUS hsaKmtDbgDisable(void) GEMSTONE_PUBLIC_API;
extern HSAKMT_STATUS hsaKmtDbgEnable(void **, HSAuint32 *) GEMSTONE_PUBLIC_API;
extern HSAKMT_STATUS hsaKmtDbgGetDeviceData(void **, HSAuint32 *, HSAuint32 *) GEMSTONE_PUBLIC_API;
extern HSAKMT_STATUS hsaKmtDbgGetQueueData(void **, HSAuint32 *, HSAuint32 *, bool) GEMSTONE_PUBLIC_API;
extern HSAKMT_STATUS hsaKmtDbgRegister(HSAuint32) GEMSTONE_PUBLIC_API;
extern HSAKMT_STATUS hsaKmtDbgUnregister(HSAuint32) GEMSTONE_PUBLIC_API;
extern HSAKMT_STATUS hsaKmtDbgWavefrontControl(HSAuint32, HSA_DBG_WAVEOP, HSA_DBG_WAVEMODE, HSAuint32, HsaDbgWaveMessage *) GEMSTONE_PUBLIC_API;
extern HSAKMT_STATUS hsaKmtDebugTrapIoctl(struct kfd_ioctl_dbg_trap_args *, HSA_QUEUEID *, HSAuint64 *) GEMSTONE_PUBLIC_API;
extern HSAKMT_STATUS hsaKmtDeregisterMemory(void *) GEMSTONE_PUBLIC_API;
extern HSAKMT_STATUS hsaKmtDestroyEvent(HsaEvent *) GEMSTONE_PUBLIC_API;
extern HSAKMT_STATUS hsaKmtDestroyQueue(HSA_QUEUEID) GEMSTONE_PUBLIC_API;
extern HSAKMT_STATUS hsaKmtExportDMABufHandle(void *, HSAuint64, int *, HSAuint64 *) GEMSTONE_PUBLIC_API;
extern HSAKMT_STATUS hsaKmtFreeMemory(void *, HSAuint64) GEMSTONE_PUBLIC_API;
extern HSAKMT_STATUS hsaKmtGetAMDGPUDeviceHandle(HSAuint32, HsaAMDGPUDeviceHandle *) GEMSTONE_PUBLIC_API;
extern HSAKMT_STATUS hsaKmtGetClockCounters(HSAuint32, HsaClockCounters *) GEMSTONE_PUBLIC_API;
extern HSAKMT_STATUS hsaKmtGetNodeCacheProperties(HSAuint32, HSAuint32, HSAuint32, HsaCacheProperties *) GEMSTONE_PUBLIC_API;
extern HSAKMT_STATUS hsaKmtGetNodeIoLinkProperties(HSAuint32, HSAuint32, HsaIoLinkProperties *) GEMSTONE_PUBLIC_API;
extern HSAKMT_STATUS hsaKmtGetNodeMemoryProperties(HSAuint32, HSAuint32, HsaMemoryProperties *) GEMSTONE_PUBLIC_API;
extern HSAKMT_STATUS hsaKmtGetNodeProperties(HSAuint32, HsaNodeProperties *) GEMSTONE_PUBLIC_API;
extern HSAKMT_STATUS hsaKmtGetQueueInfo(HSA_QUEUEID, HsaQueueInfo *) GEMSTONE_PUBLIC_API;
extern HSAKMT_STATUS hsaKmtGetRuntimeCapabilities(HSAuint32 *) GEMSTONE_PUBLIC_API;
extern HSAKMT_STATUS hsaKmtGetTileConfig(HSAuint32, HsaGpuTileConfig *) GEMSTONE_PUBLIC_API;
extern HSAKMT_STATUS hsaKmtGetVersion(HsaVersionInfo *) GEMSTONE_PUBLIC_API;
extern HSAKMT_STATUS hsaKmtGetXNACKMode(HSAint32 *) GEMSTONE_PUBLIC_API;
extern HSAKMT_STATUS hsaKmtMapGraphicHandle(HSAuint32, HSAuint64, HSAuint64, HSAuint64, HSAuint64, HSAuint64 *) GEMSTONE_PUBLIC_API;
extern HSAKMT_STATUS hsaKmtMapMemoryToGPU(void *, HSAuint64, HSAuint64 *) GEMSTONE_PUBLIC_API;
extern HSAKMT_STATUS hsaKmtMapMemoryToGPUNodes(void *, HSAuint64, HSAuint64 *, HsaMemMapFlags, HSAuint64, HSAuint32 *) GEMSTONE_PUBLIC_API;
extern HSAKMT_STATUS hsaKmtModelEnabled(bool *) GEMSTONE_PUBLIC_API;
extern HSAKMT_STATUS hsaKmtOpenKFD(void) GEMSTONE_PUBLIC_API;
extern HSAKMT_STATUS hsaKmtOpenSMI(HSAuint32, int *) GEMSTONE_PUBLIC_API;
extern HSAKMT_STATUS hsaKmtPcSamplingCreate(HSAuint32, HsaPcSamplingInfo *, HsaPcSamplingTraceId *) GEMSTONE_PUBLIC_API;
extern HSAKMT_STATUS hsaKmtPcSamplingDestroy(HSAuint32, HsaPcSamplingTraceId) GEMSTONE_PUBLIC_API;
extern HSAKMT_STATUS hsaKmtPcSamplingQueryCapabilities(HSAuint32, void *, HSAuint32, HSAuint32 *) GEMSTONE_PUBLIC_API;
extern HSAKMT_STATUS hsaKmtPcSamplingStart(HSAuint32, HsaPcSamplingTraceId) GEMSTONE_PUBLIC_API;
extern HSAKMT_STATUS hsaKmtPcSamplingStop(HSAuint32, HsaPcSamplingTraceId) GEMSTONE_PUBLIC_API;
extern HSAKMT_STATUS hsaKmtPcSamplingSupport(void) GEMSTONE_PUBLIC_API;
extern HSAKMT_STATUS hsaKmtPmcAcquireTraceAccess(HSAuint32, HSATraceId) GEMSTONE_PUBLIC_API;
extern HSAKMT_STATUS hsaKmtPmcGetCounterProperties(HSAuint32, HsaCounterProperties **) GEMSTONE_PUBLIC_API;
extern HSAKMT_STATUS hsaKmtPmcQueryTrace(HSATraceId) GEMSTONE_PUBLIC_API;
extern HSAKMT_STATUS hsaKmtPmcRegisterTrace(HSAuint32, HSAuint32, HsaCounter *, HsaPmcTraceRoot *) GEMSTONE_PUBLIC_API;
extern HSAKMT_STATUS hsaKmtPmcReleaseTraceAccess(HSAuint32, HSATraceId) GEMSTONE_PUBLIC_API;
extern HSAKMT_STATUS hsaKmtPmcStartTrace(HSATraceId, void *, HSAuint64) GEMSTONE_PUBLIC_API;
extern HSAKMT_STATUS hsaKmtPmcStopTrace(HSATraceId) GEMSTONE_PUBLIC_API;
extern HSAKMT_STATUS hsaKmtPmcUnregisterTrace(HSAuint32, HSATraceId) GEMSTONE_PUBLIC_API;
extern HSAKMT_STATUS hsaKmtProcessVMRead(HSAuint32, HsaMemoryRange *, HSAuint64, HsaMemoryRange *, HSAuint64, HSAuint64 *) GEMSTONE_PUBLIC_API;
extern HSAKMT_STATUS hsaKmtProcessVMWrite(HSAuint32, HsaMemoryRange *, HSAuint64, HsaMemoryRange *, HSAuint64, HSAuint64 *) GEMSTONE_PUBLIC_API;
extern HSAKMT_STATUS hsaKmtQueryEventState(HsaEvent *) GEMSTONE_PUBLIC_API;
extern HSAKMT_STATUS hsaKmtQueryPointerInfo(const void *, HsaPointerInfo *) GEMSTONE_PUBLIC_API;
extern HSAKMT_STATUS hsaKmtRegisterGraphicsHandleToNodes(HSAuint64, HsaGraphicsResourceInfo *, HSAuint64, HSAuint32 *) GEMSTONE_PUBLIC_API;
extern HSAKMT_STATUS hsaKmtRegisterGraphicsHandleToNodesExt(HSAuint64, HsaGraphicsResourceInfo *, HSAuint64, HSAuint32 *, HSA_REGISTER_MEM_FLAGS) GEMSTONE_PUBLIC_API;
extern HSAKMT_STATUS hsaKmtRegisterMemory(void *, HSAuint64) GEMSTONE_PUBLIC_API;
extern HSAKMT_STATUS hsaKmtRegisterMemoryToNodes(void *, HSAuint64, HSAuint64, HSAuint32 *) GEMSTONE_PUBLIC_API;
extern HSAKMT_STATUS hsaKmtRegisterMemoryWithFlags(void *, HSAuint64, HsaMemFlags) GEMSTONE_PUBLIC_API;
extern HSAKMT_STATUS hsaKmtRegisterSharedHandle(const HsaSharedMemoryHandle *, void **, HSAuint64 *) GEMSTONE_PUBLIC_API;
extern HSAKMT_STATUS hsaKmtRegisterSharedHandleToNodes(const HsaSharedMemoryHandle *, void **, HSAuint64 *, HSAuint64, HSAuint32 *) GEMSTONE_PUBLIC_API;
extern HSAKMT_STATUS hsaKmtReleaseSystemProperties(void) GEMSTONE_PUBLIC_API;
extern HSAKMT_STATUS hsaKmtReplaceAsanHeaderPage(void *) GEMSTONE_PUBLIC_API;
extern HSAKMT_STATUS hsaKmtReportQueue(HSA_QUEUEID, HsaQueueReport *) GEMSTONE_PUBLIC_API;
extern HSAKMT_STATUS hsaKmtResetEvent(HsaEvent *) GEMSTONE_PUBLIC_API;
extern HSAKMT_STATUS hsaKmtReturnAsanHeaderPage(void *) GEMSTONE_PUBLIC_API;
extern HSAKMT_STATUS hsaKmtRuntimeDisable(void) GEMSTONE_PUBLIC_API;
extern HSAKMT_STATUS hsaKmtRuntimeEnable(void *, bool) GEMSTONE_PUBLIC_API;
extern HSAKMT_STATUS hsaKmtSPMAcquire(HSAuint32) GEMSTONE_PUBLIC_API;
extern HSAKMT_STATUS hsaKmtSPMRelease(HSAuint32) GEMSTONE_PUBLIC_API;
extern HSAKMT_STATUS hsaKmtSPMSetDestBuffer(HSAuint32, HSAuint32, HSAuint32 *, HSAuint32 *, void *, bool *) GEMSTONE_PUBLIC_API;
extern HSAKMT_STATUS hsaKmtSVMGetAttr(void *, HSAuint64, unsigned int, HSA_SVM_ATTRIBUTE *) GEMSTONE_PUBLIC_API;
extern HSAKMT_STATUS hsaKmtSVMSetAttr(void *, HSAuint64, unsigned int, HSA_SVM_ATTRIBUTE *) GEMSTONE_PUBLIC_API;
extern HSAKMT_STATUS hsaKmtSetEvent(HsaEvent *) GEMSTONE_PUBLIC_API;
extern HSAKMT_STATUS hsaKmtSetMemoryPolicy(HSAuint32, HSAuint32, HSAuint32, void *, HSAuint64) GEMSTONE_PUBLIC_API;
extern HSAKMT_STATUS hsaKmtSetMemoryUserData(const void *, void *) GEMSTONE_PUBLIC_API;
extern HSAKMT_STATUS hsaKmtSetQueueCUMask(HSA_QUEUEID, HSAuint32, HSAuint32 *) GEMSTONE_PUBLIC_API;
extern HSAKMT_STATUS hsaKmtSetTrapHandler(HSAuint32, void *, HSAuint64, void *, HSAuint64) GEMSTONE_PUBLIC_API;
extern HSAKMT_STATUS hsaKmtSetXNACKMode(HSAint32) GEMSTONE_PUBLIC_API;
extern HSAKMT_STATUS hsaKmtShareMemory(void *, HSAuint64, HsaSharedMemoryHandle *) GEMSTONE_PUBLIC_API;
extern HSAKMT_STATUS hsaKmtUnmapGraphicHandle(HSAuint32, HSAuint64, HSAuint64) GEMSTONE_PUBLIC_API;
extern HSAKMT_STATUS hsaKmtUnmapMemoryToGPU(void *) GEMSTONE_PUBLIC_API;
extern HSAKMT_STATUS hsaKmtUpdateQueue(HSA_QUEUEID, HSAuint32, HSA_QUEUE_PRIORITY, void *, HSAuint64, HsaEvent *) GEMSTONE_PUBLIC_API;
extern HSAKMT_STATUS hsaKmtWaitOnEvent(HsaEvent *, HSAuint32) GEMSTONE_PUBLIC_API;
extern HSAKMT_STATUS hsaKmtWaitOnEvent_Ext(HsaEvent *, HSAuint32, uint64_t *) GEMSTONE_PUBLIC_API;
extern HSAKMT_STATUS hsaKmtWaitOnMultipleEvents(HsaEvent **, HSAuint32, bool, HSAuint32) GEMSTONE_PUBLIC_API;
extern HSAKMT_STATUS hsaKmtWaitOnMultipleEvents_Ext(HsaEvent **, HSAuint32, bool, HSAuint32, uint64_t *) GEMSTONE_PUBLIC_API;

#ifdef __cplusplus
}
#endif
