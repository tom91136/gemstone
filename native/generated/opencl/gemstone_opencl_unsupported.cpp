/* Generated from the official API manifest format 6. Regenerate with: sbt codegen */
#include "gemstone_opencl_api.h"
#include <gemstone/unsupported.hpp>

extern "C" {
GEMSTONE_INTERNAL_API cl_int gemstone_opencl_unsupported() { return CL_INVALID_OPERATION; }
cl_accelerator_intel clCreateAcceleratorINTEL(cl_context, cl_accelerator_type_intel, size_t, const void *, cl_int *error) { if (error) *error = CL_INVALID_OPERATION; return {}; }
cl_mem clCreateBufferWithPropertiesINTEL(cl_context, const cl_mem_properties_intel *, cl_mem_flags, size_t, void *, cl_int *error) { if (error) *error = CL_INVALID_OPERATION; return {}; }
cl_event clCreateEventFromEGLSyncKHR(cl_context, CLeglSyncKHR, CLeglDisplayKHR, cl_int *error) { if (error) *error = CL_INVALID_OPERATION; return {}; }
cl_event clCreateEventFromGLsyncKHR(cl_context, cl_GLsync, cl_int *error) { if (error) *error = CL_INVALID_OPERATION; return {}; }
cl_mem clCreateFromEGLImageKHR(cl_context, CLeglDisplayKHR, CLeglImageKHR, cl_mem_flags, const cl_egl_image_properties_khr *, cl_int *error) { if (error) *error = CL_INVALID_OPERATION; return {}; }
cl_mem clCreateFromGLBuffer(cl_context, cl_mem_flags, cl_GLuint, cl_int *error) { if (error) *error = CL_INVALID_OPERATION; return {}; }
cl_mem clCreateFromGLRenderbuffer(cl_context, cl_mem_flags, cl_GLuint, cl_int *error) { if (error) *error = CL_INVALID_OPERATION; return {}; }
cl_mem clCreateFromGLTexture(cl_context, cl_mem_flags, cl_GLenum, cl_GLint, cl_GLuint, cl_int *error) { if (error) *error = CL_INVALID_OPERATION; return {}; }
cl_mem clCreateFromGLTexture2D(cl_context, cl_mem_flags, cl_GLenum, cl_GLint, cl_GLuint, cl_int *error) { if (error) *error = CL_INVALID_OPERATION; return {}; }
cl_mem clCreateFromGLTexture3D(cl_context, cl_mem_flags, cl_GLenum, cl_GLint, cl_GLuint, cl_int *error) { if (error) *error = CL_INVALID_OPERATION; return {}; }
cl_program clCreateProgramWithBuiltInKernels(cl_context, cl_uint, const cl_device_id *, const char *, cl_int *error) { if (error) *error = CL_INVALID_OPERATION; return {}; }
cl_program clCreateProgramWithILKHR(cl_context, const void *, size_t, cl_int *error) { if (error) *error = CL_INVALID_OPERATION; return {}; }
cl_semaphore_khr clCreateSemaphoreWithPropertiesKHR(cl_context, const cl_semaphore_properties_khr *, cl_int *error) { if (error) *error = CL_INVALID_OPERATION; return {}; }
cl_mem clImportMemoryARM(cl_context, cl_mem_flags, const cl_import_properties_arm *, void *, size_t, cl_int *error) { if (error) *error = CL_INVALID_OPERATION; return {}; }
void clLogMessagesToStderrAPPLE(const char *, const void *, size_t, void *) { }
void clLogMessagesToStdoutAPPLE(const char *, const void *, size_t, void *) { }
void clLogMessagesToSystemLogAPPLE(const char *, const void *, size_t, void *) { }
void * clSVMAllocARM(cl_context, cl_svm_mem_flags_arm, size_t, cl_uint) { return {}; }
void clSVMFreeARM(cl_context, void *) { }
}

GEMSTONE_UNSUPPORTED(clCancelCommandsIMG, gemstone_opencl_unsupported)
GEMSTONE_UNSUPPORTED(clCreateSubDevices, gemstone_opencl_unsupported)
GEMSTONE_UNSUPPORTED(clCreateSubDevicesEXT, gemstone_opencl_unsupported)
GEMSTONE_UNSUPPORTED(clEnqueueAcquireEGLObjectsKHR, gemstone_opencl_unsupported)
GEMSTONE_UNSUPPORTED(clEnqueueAcquireExternalMemObjectsKHR, gemstone_opencl_unsupported)
GEMSTONE_UNSUPPORTED(clEnqueueAcquireGLObjects, gemstone_opencl_unsupported)
GEMSTONE_UNSUPPORTED(clEnqueueAcquireGrallocObjectsIMG, gemstone_opencl_unsupported)
GEMSTONE_UNSUPPORTED(clEnqueueBarrier, gemstone_opencl_unsupported)
GEMSTONE_UNSUPPORTED(clEnqueueGenerateMipmapIMG, gemstone_opencl_unsupported)
GEMSTONE_UNSUPPORTED(clEnqueueMarker, gemstone_opencl_unsupported)
GEMSTONE_UNSUPPORTED(clEnqueueMemsetINTEL, gemstone_opencl_unsupported)
GEMSTONE_UNSUPPORTED(clEnqueueMigrateMemINTEL, gemstone_opencl_unsupported)
GEMSTONE_UNSUPPORTED(clEnqueueMigrateMemObjectEXT, gemstone_opencl_unsupported)
GEMSTONE_UNSUPPORTED(clEnqueueNativeKernel, gemstone_opencl_unsupported)
GEMSTONE_UNSUPPORTED(clEnqueueReadHostPipeINTEL, gemstone_opencl_unsupported)
GEMSTONE_UNSUPPORTED(clEnqueueReleaseEGLObjectsKHR, gemstone_opencl_unsupported)
GEMSTONE_UNSUPPORTED(clEnqueueReleaseExternalMemObjectsKHR, gemstone_opencl_unsupported)
GEMSTONE_UNSUPPORTED(clEnqueueReleaseGLObjects, gemstone_opencl_unsupported)
GEMSTONE_UNSUPPORTED(clEnqueueReleaseGrallocObjectsIMG, gemstone_opencl_unsupported)
GEMSTONE_UNSUPPORTED(clEnqueueSVMFreeARM, gemstone_opencl_unsupported)
GEMSTONE_UNSUPPORTED(clEnqueueSVMMapARM, gemstone_opencl_unsupported)
GEMSTONE_UNSUPPORTED(clEnqueueSVMMemFillARM, gemstone_opencl_unsupported)
GEMSTONE_UNSUPPORTED(clEnqueueSVMMemcpyARM, gemstone_opencl_unsupported)
GEMSTONE_UNSUPPORTED(clEnqueueSVMUnmapARM, gemstone_opencl_unsupported)
GEMSTONE_UNSUPPORTED(clEnqueueSignalSemaphoresKHR, gemstone_opencl_unsupported)
GEMSTONE_UNSUPPORTED(clEnqueueWaitForEvents, gemstone_opencl_unsupported)
GEMSTONE_UNSUPPORTED(clEnqueueWaitSemaphoresKHR, gemstone_opencl_unsupported)
GEMSTONE_UNSUPPORTED(clEnqueueWriteHostPipeINTEL, gemstone_opencl_unsupported)
GEMSTONE_UNSUPPORTED(clGetAcceleratorInfoINTEL, gemstone_opencl_unsupported)
GEMSTONE_UNSUPPORTED(clGetDeviceImageInfoQCOM, gemstone_opencl_unsupported)
GEMSTONE_UNSUPPORTED(clGetGLContextInfoKHR, gemstone_opencl_unsupported)
GEMSTONE_UNSUPPORTED(clGetGLObjectInfo, gemstone_opencl_unsupported)
GEMSTONE_UNSUPPORTED(clGetGLTextureInfo, gemstone_opencl_unsupported)
GEMSTONE_UNSUPPORTED(clGetICDLoaderInfoOCLICD, gemstone_opencl_unsupported)
GEMSTONE_UNSUPPORTED(clGetImageRequirementsInfoEXT, gemstone_opencl_unsupported)
GEMSTONE_UNSUPPORTED(clGetSemaphoreHandleForTypeKHR, gemstone_opencl_unsupported)
GEMSTONE_UNSUPPORTED(clGetSemaphoreInfoKHR, gemstone_opencl_unsupported)
GEMSTONE_UNSUPPORTED(clGetSupportedGLTextureFormatsINTEL, gemstone_opencl_unsupported)
GEMSTONE_UNSUPPORTED(clReImportSemaphoreSyncFdKHR, gemstone_opencl_unsupported)
GEMSTONE_UNSUPPORTED(clReleaseAcceleratorINTEL, gemstone_opencl_unsupported)
GEMSTONE_UNSUPPORTED(clReleaseDeviceEXT, gemstone_opencl_unsupported)
GEMSTONE_UNSUPPORTED(clReleaseSemaphoreKHR, gemstone_opencl_unsupported)
GEMSTONE_UNSUPPORTED(clRetainAcceleratorINTEL, gemstone_opencl_unsupported)
GEMSTONE_UNSUPPORTED(clRetainDeviceEXT, gemstone_opencl_unsupported)
GEMSTONE_UNSUPPORTED(clRetainSemaphoreKHR, gemstone_opencl_unsupported)
GEMSTONE_UNSUPPORTED(clSetContentSizeBufferPoCL, gemstone_opencl_unsupported)
GEMSTONE_UNSUPPORTED(clSetKernelArgDevicePointerEXT, gemstone_opencl_unsupported)
GEMSTONE_UNSUPPORTED(clSetKernelArgSVMPointerARM, gemstone_opencl_unsupported)
GEMSTONE_UNSUPPORTED(clSetKernelExecInfoARM, gemstone_opencl_unsupported)
GEMSTONE_UNSUPPORTED(clSetMemObjectDestructorAPPLE, gemstone_opencl_unsupported)
GEMSTONE_UNSUPPORTED(clSetPerfHintQCOM, gemstone_opencl_unsupported)
GEMSTONE_UNSUPPORTED(clTerminateContextKHR, gemstone_opencl_unsupported)
