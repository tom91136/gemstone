#define CL_TARGET_OPENCL_VERSION 300
#define CL_USE_DEPRECATED_OPENCL_1_0_APIS 1
#define CL_USE_DEPRECATED_OPENCL_1_1_APIS 1
#define CL_USE_DEPRECATED_OPENCL_1_2_APIS 1
#define CL_USE_DEPRECATED_OPENCL_2_0_APIS 1
#define CL_USE_DEPRECATED_OPENCL_2_1_APIS 1
#define CL_USE_DEPRECATED_OPENCL_2_2_APIS 1
#include <CL/cl_ext.h>
#include <CL/cl_gl.h>
#include <CL/cl_icd.h>

extern "C" cl_int clCancelCommandsIMG(const cl_event *, size_t) { return CL_INVALID_OPERATION; }

#if defined(__GNUC__) && !defined(__clang__)
  #pragma GCC diagnostic push
  #pragma GCC diagnostic ignored "-Wattribute-alias"
  #pragma GCC diagnostic ignored "-Wmissing-attributes"
#endif

#define GEMSTONE_OPENCL_UNSUPPORTED(name) extern "C" decltype(name) name __attribute__((alias("clCancelCommandsIMG")));

// Each strong alias is an explicit facade decision. It is intentionally not
// generated: an API added to the official headers must first fail the ABI
// implementation check and then be implemented or reviewed into this inventory.
GEMSTONE_OPENCL_UNSUPPORTED(clCreateSubDevices)
GEMSTONE_OPENCL_UNSUPPORTED(clCreateSubDevicesEXT)
GEMSTONE_OPENCL_UNSUPPORTED(clEnqueueAcquireEGLObjectsKHR)
GEMSTONE_OPENCL_UNSUPPORTED(clEnqueueAcquireExternalMemObjectsKHR)
GEMSTONE_OPENCL_UNSUPPORTED(clEnqueueAcquireGLObjects)
GEMSTONE_OPENCL_UNSUPPORTED(clEnqueueAcquireGrallocObjectsIMG)
GEMSTONE_OPENCL_UNSUPPORTED(clEnqueueBarrier)
GEMSTONE_OPENCL_UNSUPPORTED(clEnqueueCopyBufferToImage)
GEMSTONE_OPENCL_UNSUPPORTED(clEnqueueCopyImage)
GEMSTONE_OPENCL_UNSUPPORTED(clEnqueueCopyImageToBuffer)
GEMSTONE_OPENCL_UNSUPPORTED(clEnqueueFillImage)
GEMSTONE_OPENCL_UNSUPPORTED(clEnqueueGenerateMipmapIMG)
GEMSTONE_OPENCL_UNSUPPORTED(clEnqueueMarker)
GEMSTONE_OPENCL_UNSUPPORTED(clEnqueueMemsetINTEL)
GEMSTONE_OPENCL_UNSUPPORTED(clEnqueueMigrateMemINTEL)
GEMSTONE_OPENCL_UNSUPPORTED(clEnqueueMigrateMemObjectEXT)
GEMSTONE_OPENCL_UNSUPPORTED(clEnqueueNativeKernel)
GEMSTONE_OPENCL_UNSUPPORTED(clEnqueueReadHostPipeINTEL)
GEMSTONE_OPENCL_UNSUPPORTED(clEnqueueReadImage)
GEMSTONE_OPENCL_UNSUPPORTED(clEnqueueReleaseEGLObjectsKHR)
GEMSTONE_OPENCL_UNSUPPORTED(clEnqueueReleaseExternalMemObjectsKHR)
GEMSTONE_OPENCL_UNSUPPORTED(clEnqueueReleaseGLObjects)
GEMSTONE_OPENCL_UNSUPPORTED(clEnqueueReleaseGrallocObjectsIMG)
GEMSTONE_OPENCL_UNSUPPORTED(clEnqueueSVMFreeARM)
GEMSTONE_OPENCL_UNSUPPORTED(clEnqueueSVMMapARM)
GEMSTONE_OPENCL_UNSUPPORTED(clEnqueueSVMMemFillARM)
GEMSTONE_OPENCL_UNSUPPORTED(clEnqueueSVMMemcpyARM)
GEMSTONE_OPENCL_UNSUPPORTED(clEnqueueSVMUnmapARM)
GEMSTONE_OPENCL_UNSUPPORTED(clEnqueueSignalSemaphoresKHR)
GEMSTONE_OPENCL_UNSUPPORTED(clEnqueueWaitForEvents)
GEMSTONE_OPENCL_UNSUPPORTED(clEnqueueWaitSemaphoresKHR)
GEMSTONE_OPENCL_UNSUPPORTED(clEnqueueWriteHostPipeINTEL)
GEMSTONE_OPENCL_UNSUPPORTED(clEnqueueWriteImage)
GEMSTONE_OPENCL_UNSUPPORTED(clGetAcceleratorInfoINTEL)
GEMSTONE_OPENCL_UNSUPPORTED(clGetDeviceImageInfoQCOM)
GEMSTONE_OPENCL_UNSUPPORTED(clGetGLContextInfoKHR)
GEMSTONE_OPENCL_UNSUPPORTED(clGetGLObjectInfo)
GEMSTONE_OPENCL_UNSUPPORTED(clGetGLTextureInfo)
GEMSTONE_OPENCL_UNSUPPORTED(clGetICDLoaderInfoOCLICD)
GEMSTONE_OPENCL_UNSUPPORTED(clGetImageRequirementsInfoEXT)
GEMSTONE_OPENCL_UNSUPPORTED(clGetKernelSuggestedLocalWorkSizeKHR)
GEMSTONE_OPENCL_UNSUPPORTED(clGetSamplerInfo)
GEMSTONE_OPENCL_UNSUPPORTED(clGetSemaphoreHandleForTypeKHR)
GEMSTONE_OPENCL_UNSUPPORTED(clGetSemaphoreInfoKHR)
GEMSTONE_OPENCL_UNSUPPORTED(clGetSupportedGLTextureFormatsINTEL)
GEMSTONE_OPENCL_UNSUPPORTED(clReImportSemaphoreSyncFdKHR)
GEMSTONE_OPENCL_UNSUPPORTED(clReleaseAcceleratorINTEL)
GEMSTONE_OPENCL_UNSUPPORTED(clReleaseDeviceEXT)
GEMSTONE_OPENCL_UNSUPPORTED(clReleaseSampler)
GEMSTONE_OPENCL_UNSUPPORTED(clReleaseSemaphoreKHR)
GEMSTONE_OPENCL_UNSUPPORTED(clRetainAcceleratorINTEL)
GEMSTONE_OPENCL_UNSUPPORTED(clRetainDeviceEXT)
GEMSTONE_OPENCL_UNSUPPORTED(clRetainSampler)
GEMSTONE_OPENCL_UNSUPPORTED(clRetainSemaphoreKHR)
GEMSTONE_OPENCL_UNSUPPORTED(clSetContentSizeBufferPoCL)
GEMSTONE_OPENCL_UNSUPPORTED(clSetKernelArgDevicePointerEXT)
GEMSTONE_OPENCL_UNSUPPORTED(clSetKernelArgSVMPointerARM)
GEMSTONE_OPENCL_UNSUPPORTED(clSetKernelExecInfoARM)
GEMSTONE_OPENCL_UNSUPPORTED(clSetMemObjectDestructorAPPLE)
GEMSTONE_OPENCL_UNSUPPORTED(clSetPerfHintQCOM)
GEMSTONE_OPENCL_UNSUPPORTED(clTerminateContextKHR)

#undef GEMSTONE_OPENCL_UNSUPPORTED

#if defined(__GNUC__) && !defined(__clang__)
  #pragma GCC diagnostic pop
#endif

extern "C" {

cl_accelerator_intel clCreateAcceleratorINTEL(cl_context, cl_accelerator_type_intel, size_t, const void *, cl_int *error) {
  if (error) *error = CL_INVALID_OPERATION;
  return nullptr;
}

cl_mem clCreateBufferWithPropertiesINTEL(cl_context, const cl_mem_properties_intel *, cl_mem_flags, size_t, void *, cl_int *error) {
  if (error) *error = CL_INVALID_OPERATION;
  return nullptr;
}

cl_command_queue clCreateCommandQueueWithPropertiesKHR(cl_context, cl_device_id, const cl_queue_properties_khr *, cl_int *error) {
  if (error) *error = CL_INVALID_OPERATION;
  return nullptr;
}

cl_event clCreateEventFromEGLSyncKHR(cl_context, CLeglSyncKHR, CLeglDisplayKHR, cl_int *error) {
  if (error) *error = CL_INVALID_OPERATION;
  return nullptr;
}

cl_event clCreateEventFromGLsyncKHR(cl_context, cl_GLsync, cl_int *error) {
  if (error) *error = CL_INVALID_OPERATION;
  return nullptr;
}

cl_mem clCreateFromEGLImageKHR(cl_context, CLeglDisplayKHR, CLeglImageKHR, cl_mem_flags, const cl_egl_image_properties_khr *,
                               cl_int *error) {
  if (error) *error = CL_INVALID_OPERATION;
  return nullptr;
}

cl_mem clCreateFromGLBuffer(cl_context, cl_mem_flags, cl_GLuint, cl_int *error) {
  if (error) *error = CL_INVALID_OPERATION;
  return nullptr;
}

cl_mem clCreateFromGLRenderbuffer(cl_context, cl_mem_flags, cl_GLuint, cl_int *error) {
  if (error) *error = CL_INVALID_OPERATION;
  return nullptr;
}

cl_mem clCreateFromGLTexture(cl_context, cl_mem_flags, cl_GLenum, cl_GLint, cl_GLuint, cl_int *error) {
  if (error) *error = CL_INVALID_OPERATION;
  return nullptr;
}

cl_mem clCreateFromGLTexture2D(cl_context, cl_mem_flags, cl_GLenum, cl_GLint, cl_GLuint, cl_int *error) {
  if (error) *error = CL_INVALID_OPERATION;
  return nullptr;
}

cl_mem clCreateFromGLTexture3D(cl_context, cl_mem_flags, cl_GLenum, cl_GLint, cl_GLuint, cl_int *error) {
  if (error) *error = CL_INVALID_OPERATION;
  return nullptr;
}

cl_mem clCreateImage2D(cl_context, cl_mem_flags, const cl_image_format *, size_t, size_t, size_t, void *, cl_int *error) {
  if (error) *error = CL_INVALID_OPERATION;
  return nullptr;
}

cl_mem clCreateImage3D(cl_context, cl_mem_flags, const cl_image_format *, size_t, size_t, size_t, size_t, size_t, void *, cl_int *error) {
  if (error) *error = CL_INVALID_OPERATION;
  return nullptr;
}

cl_program clCreateProgramWithBuiltInKernels(cl_context, cl_uint, const cl_device_id *, const char *, cl_int *error) {
  if (error) *error = CL_INVALID_OPERATION;
  return nullptr;
}

cl_program clCreateProgramWithILKHR(cl_context, const void *, size_t, cl_int *error) {
  if (error) *error = CL_INVALID_OPERATION;
  return nullptr;
}

cl_sampler clCreateSampler(cl_context, cl_bool, cl_addressing_mode, cl_filter_mode, cl_int *error) {
  if (error) *error = CL_INVALID_OPERATION;
  return nullptr;
}

cl_sampler clCreateSamplerWithProperties(cl_context, const cl_sampler_properties *, cl_int *error) {
  if (error) *error = CL_INVALID_OPERATION;
  return nullptr;
}

cl_semaphore_khr clCreateSemaphoreWithPropertiesKHR(cl_context, const cl_semaphore_properties_khr *, cl_int *error) {
  if (error) *error = CL_INVALID_OPERATION;
  return nullptr;
}

void *clEnqueueMapImage(cl_command_queue, cl_mem, cl_bool, cl_map_flags, const size_t *, const size_t *, size_t *, size_t *, cl_uint,
                        const cl_event *, cl_event *, cl_int *error) {
  if (error) *error = CL_INVALID_OPERATION;
  return nullptr;
}

cl_mem clImportMemoryARM(cl_context, cl_mem_flags, const cl_import_properties_arm *, void *, size_t, cl_int *error) {
  if (error) *error = CL_INVALID_OPERATION;
  return nullptr;
}

void clLogMessagesToStderrAPPLE(const char *, const void *, size_t, void *) {}
void clLogMessagesToStdoutAPPLE(const char *, const void *, size_t, void *) {}
void clLogMessagesToSystemLogAPPLE(const char *, const void *, size_t, void *) {}

void *clSVMAllocARM(cl_context, cl_svm_mem_flags_arm, size_t, cl_uint) { return nullptr; }
void clSVMFreeARM(cl_context, void *) {}

} // extern "C"
