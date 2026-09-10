#ifndef GEMSTONE_CUDA_COMPAT_H
#define GEMSTONE_CUDA_COMPAT_H

#include <stddef.h>
#include <stdint.h>

int32_t gemstone_cuda_track_device(uint64_t pointer, size_t size, void *module);
int32_t gemstone_cuda_track_context(void *context);
int gemstone_cuda_context_is_live(void *context);
int32_t gemstone_cuda_activate_created_context(void *context);
void *gemstone_cuda_rpc_context(void);
void gemstone_cuda_untrack_device(uint64_t pointer);
void gemstone_cuda_untrack_module(void *module);
void gemstone_cuda_untrack_context(void *context);

int32_t cuArray3DCreate_v2(void **handle, const void *descriptor);
int32_t cuArray3DGetDescriptor_v2(void *descriptor, void *handle);
int32_t cuArrayCreate_v2(void **handle, const void *descriptor);
int32_t cuArrayDestroy(void *handle);
int32_t cuArrayGetDescriptor_v2(void *descriptor, void *handle);
int32_t cuArray3DCreate(void **handle, const void *descriptor);
int32_t cuArray3DGetDescriptor(void *descriptor, void *handle);
int32_t cuArrayCreate(void **handle, const void *descriptor);
int32_t cuArrayGetDescriptor(void *descriptor, void *handle);
int32_t cuKernelGetFunction(void **function, void *kernel);
int32_t cuLibraryGetGlobal(uint64_t *pointer, size_t *bytes, void *library, const char *name);
int32_t cuMemcpy2D_v2(const void *copy);
int32_t cuMemcpy2DUnaligned_v2(const void *copy);
int32_t cuMemcpy2DAsync_v2(const void *copy, void *stream);
int32_t cuMemcpy2D(const void *copy);
int32_t cuMemcpy2DUnaligned(const void *copy);
int32_t cuMemcpy2DAsync(const void *copy, void *stream);
int32_t cuMemcpy3D_v2(const void *copy);
int32_t cuMemcpy3DAsync_v2(const void *copy, void *stream);
int32_t cuMemcpy3D(const void *copy);
int32_t cuMemcpy3DAsync(const void *copy, void *stream);
int32_t cuModuleGetGlobal(uint32_t *pointer, uint32_t *bytes, void *module, const char *name);
int32_t cuFuncGetAttribute(int32_t *value, int32_t attribute, void *function);
int32_t cuKernelGetAttribute(int32_t *value, int32_t attribute, void *kernel, int32_t device);
int32_t cuOccupancyMaxActiveBlocksPerMultiprocessor(
    int32_t *blocks, void *function, int32_t block_size, size_t dynamic_shared_memory);
int32_t cuOccupancyMaxActiveBlocksPerMultiprocessorWithFlags(
    int32_t *blocks, void *function, int32_t block_size, size_t dynamic_shared_memory, uint32_t flags);
int32_t gemstone_cuda_managed_allocate(uint64_t *pointer, size_t size);
int32_t gemstone_cuda_managed_free(uint64_t pointer, int *found);
int32_t gemstone_cuda_managed_synchronize_to_device(void);
int gemstone_cuda_managed_copy_to_host(void *destination, uint64_t pointer, size_t size);
int gemstone_cuda_managed_copy_from_host(uint64_t pointer, const void *source, size_t size);
void gemstone_cuda_managed_mark_device_dirty(void);
int32_t gemstone_cuda_managed_synchronize_from_device(void);
int32_t gemstone_register_global_remote(
    uint64_t module, const char *name, const void *initial, size_t size);
int32_t gemstone_resolve_functions_remote(
    uint64_t module, const char *const *names, uint32_t count, uint64_t *handles);
int32_t cuMemAllocManaged(uint64_t *pointer, size_t size, uint32_t flags);
int32_t cuMemAllocHost_v2(void **pointer, size_t size);
int32_t cuMemAllocHost(void **pointer, size_t size);
int32_t cuMemFreeHost(void *pointer);
int32_t cuMemHostAlloc(void **pointer, size_t size, uint32_t flags);
int32_t cuMemHostGetDevicePointer_v2(uint64_t *pointer, void *host_pointer, uint32_t flags);
int32_t cuMemHostGetFlags(uint32_t *flags, void *host_pointer);
const char *gemstone_cuda_find_uncompressed_ptx(const void *fat_binary);

#endif
