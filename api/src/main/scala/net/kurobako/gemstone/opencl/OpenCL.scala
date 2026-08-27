// Generated from pinned official headers by GenerateAPISurfaces. Do not edit mechanically.
package net.kurobako.gemstone.opencl

import net.kurobako.gemstone.abi.*

type cl_GLenum = CValue["cl_GLenum"]
type cl_GLint = CValue["cl_GLint"]
type cl_GLsync = CValue["cl_GLsync"]
type cl_GLuint = CValue["cl_GLuint"]
type cl_accelerator_info_intel = CValue["cl_accelerator_info_intel"]
type cl_accelerator_intel = CValue["cl_accelerator_intel"]
type cl_accelerator_type_intel = CValue["cl_accelerator_type_intel"]
type cl_addressing_mode = CValue["cl_addressing_mode"]
type cl_bool = CValue["cl_bool"]
type cl_buffer_create_type = CValue["cl_buffer_create_type"]
type cl_command_queue = CValue["cl_command_queue"]
type cl_command_queue_info = CValue["cl_command_queue_info"]
type cl_command_queue_properties = CValue["cl_command_queue_properties"]
type cl_context = CValue["cl_context"]
type cl_context_info = CValue["cl_context_info"]
type cl_context_properties = CValue["cl_context_properties"]
type cl_device_id = CValue["cl_device_id"]
type cl_device_info = CValue["cl_device_info"]
type cl_device_partition_property = CValue["cl_device_partition_property"]
type cl_device_partition_property_ext = CValue["cl_device_partition_property_ext"]
type cl_device_type = CValue["cl_device_type"]
type cl_event = CValue["cl_event"]
type cl_event_info = CValue["cl_event_info"]
type cl_external_semaphore_handle_type_khr = CValue["cl_external_semaphore_handle_type_khr"]
type cl_filter_mode = CValue["cl_filter_mode"]
type cl_gl_context_info = CValue["cl_gl_context_info"]
type cl_gl_object_type = CValue["cl_gl_object_type"]
type cl_gl_texture_info = CValue["cl_gl_texture_info"]
type cl_icdl_info = CValue["cl_icdl_info"]
type cl_image_desc = CValue["cl_image_desc"]
type cl_image_format = CValue["cl_image_format"]
type cl_image_info = CValue["cl_image_info"]
type cl_image_pitch_info_qcom = CValue["cl_image_pitch_info_qcom"]
type cl_image_requirements_info_ext = CValue["cl_image_requirements_info_ext"]
type cl_import_properties_arm = CValue["cl_import_properties_arm"]
type cl_int = CValue["cl_int"]
type cl_kernel = CValue["cl_kernel"]
type cl_kernel_arg_info = CValue["cl_kernel_arg_info"]
type cl_kernel_exec_info = CValue["cl_kernel_exec_info"]
type cl_kernel_exec_info_arm = CValue["cl_kernel_exec_info_arm"]
type cl_kernel_info = CValue["cl_kernel_info"]
type cl_kernel_sub_group_info = CValue["cl_kernel_sub_group_info"]
type cl_kernel_work_group_info = CValue["cl_kernel_work_group_info"]
type cl_map_flags = CValue["cl_map_flags"]
type cl_mem = CValue["cl_mem"]
type cl_mem_advice_intel = CValue["cl_mem_advice_intel"]
type cl_mem_device_address_ext = CValue["cl_mem_device_address_ext"]
type cl_mem_flags = CValue["cl_mem_flags"]
type cl_mem_info = CValue["cl_mem_info"]
type cl_mem_info_intel = CValue["cl_mem_info_intel"]
type cl_mem_migration_flags = CValue["cl_mem_migration_flags"]
type cl_mem_migration_flags_ext = CValue["cl_mem_migration_flags_ext"]
type cl_mem_object_type = CValue["cl_mem_object_type"]
type cl_mem_properties = CValue["cl_mem_properties"]
type cl_mem_properties_intel = CValue["cl_mem_properties_intel"]
type cl_mipmap_filter_mode_img = CValue["cl_mipmap_filter_mode_img"]
type cl_perf_hint_qcom = CValue["cl_perf_hint_qcom"]
type cl_pipe_info = CValue["cl_pipe_info"]
type cl_pipe_properties = CValue["cl_pipe_properties"]
type cl_platform_id = CValue["cl_platform_id"]
type cl_platform_info = CValue["cl_platform_info"]
type cl_profiling_info = CValue["cl_profiling_info"]
type cl_program = CValue["cl_program"]
type cl_program_build_info = CValue["cl_program_build_info"]
type cl_program_info = CValue["cl_program_info"]
type cl_queue_properties = CValue["cl_queue_properties"]
type cl_queue_properties_khr = CValue["cl_queue_properties_khr"]
type cl_sampler = CValue["cl_sampler"]
type cl_sampler_info = CValue["cl_sampler_info"]
type cl_sampler_properties = CValue["cl_sampler_properties"]
type cl_semaphore_info_khr = CValue["cl_semaphore_info_khr"]
type cl_semaphore_khr = CValue["cl_semaphore_khr"]
type cl_semaphore_payload_khr = CValue["cl_semaphore_payload_khr"]
type cl_semaphore_properties_khr = CValue["cl_semaphore_properties_khr"]
type cl_semaphore_reimport_properties_khr = CValue["cl_semaphore_reimport_properties_khr"]
type cl_svm_mem_flags = CValue["cl_svm_mem_flags"]
type cl_svm_mem_flags_arm = CValue["cl_svm_mem_flags_arm"]
type cl_uint = CValue["cl_uint"]
type cl_ulong = CValue["cl_ulong"]
type size_t = CValue["size_t"]
type unsigned_char = CValue["unsigned char"]

trait OpenCL {
  @CSignature("cl_int (cl_program, cl_uint, const cl_device_id *, const char *, void (*)(cl_program, void *), void *)")
  def clBuildProgram(program: cl_program, num_devices: cl_uint, device_list: Pointer[cl_device_id, Read, Opaque], options: CString, pfn_notify: Callback["void (*)(cl_program, void *)"], user_data: Pointer[CVoid, ReadWrite, Opaque]): cl_int = unsupported
  @CSignature("cl_int (const cl_event *, size_t)")
  def clCancelCommandsIMG(event_list: Pointer[cl_event, Read, Opaque], num_events_in_list: size_t): cl_int = unsupported
  @CSignature("cl_kernel (cl_kernel, cl_int *)")
  def clCloneKernel(source_kernel: cl_kernel, errcode_ret: Pointer[cl_int, ReadWrite, Opaque]): cl_kernel = unsupported
  @CSignature("cl_int (cl_program, cl_uint, const cl_device_id *, const char *, cl_uint, const cl_program *, const char **, void (*)(cl_program, void *), void *)")
  def clCompileProgram(program: cl_program, num_devices: cl_uint, device_list: Pointer[cl_device_id, Read, Opaque], options: CString, num_input_headers: cl_uint, input_headers: Pointer[cl_program, Read, Opaque], header_include_names: Pointer[Pointer[Byte, ReadWrite, Opaque], Read, Opaque], pfn_notify: Callback["void (*)(cl_program, void *)"], user_data: Pointer[CVoid, ReadWrite, Opaque]): cl_int = unsupported
  @CSignature("cl_accelerator_intel (cl_context, cl_accelerator_type_intel, size_t, const void *, cl_int *)")
  def clCreateAcceleratorINTEL(context: cl_context, accelerator_type: cl_accelerator_type_intel, descriptor_size: size_t, descriptor: Pointer[CVoid, Read, Opaque], errcode_ret: Pointer[cl_int, ReadWrite, Opaque]): cl_accelerator_intel = unsupported
  @CSignature("cl_mem (cl_context, cl_mem_flags, size_t, void *, cl_int *)")
  def clCreateBuffer(context: cl_context, flags: cl_mem_flags, size: size_t, host_ptr: Pointer[CVoid, ReadWrite, Opaque], errcode_ret: Pointer[cl_int, ReadWrite, Opaque]): cl_mem = unsupported
  @CSignature("cl_mem (cl_context, const cl_mem_properties *, cl_mem_flags, size_t, void *, cl_int *)")
  def clCreateBufferWithProperties(context: cl_context, properties: Pointer[cl_mem_properties, Read, Opaque], flags: cl_mem_flags, size: size_t, host_ptr: Pointer[CVoid, ReadWrite, Opaque], errcode_ret: Pointer[cl_int, ReadWrite, Opaque]): cl_mem = unsupported
  @CSignature("cl_mem (cl_context, const cl_mem_properties_intel *, cl_mem_flags, size_t, void *, cl_int *)")
  def clCreateBufferWithPropertiesINTEL(context: cl_context, properties: Pointer[cl_mem_properties_intel, Read, Opaque], flags: cl_mem_flags, size: size_t, host_ptr: Pointer[CVoid, ReadWrite, Opaque], errcode_ret: Pointer[cl_int, ReadWrite, Opaque]): cl_mem = unsupported
  @CSignature("cl_command_queue (cl_context, cl_device_id, cl_command_queue_properties, cl_int *)")
  def clCreateCommandQueue(context: cl_context, device: cl_device_id, properties: cl_command_queue_properties, errcode_ret: Pointer[cl_int, ReadWrite, Opaque]): cl_command_queue = unsupported
  @CSignature("cl_command_queue (cl_context, cl_device_id, const cl_queue_properties *, cl_int *)")
  def clCreateCommandQueueWithProperties(context: cl_context, device: cl_device_id, properties: Pointer[cl_queue_properties, Read, Opaque], errcode_ret: Pointer[cl_int, ReadWrite, Opaque]): cl_command_queue = unsupported
  @CSignature("cl_command_queue (cl_context, cl_device_id, const cl_queue_properties_khr *, cl_int *)")
  def clCreateCommandQueueWithPropertiesKHR(context: cl_context, device: cl_device_id, properties: Pointer[cl_queue_properties_khr, Read, Opaque], errcode_ret: Pointer[cl_int, ReadWrite, Opaque]): cl_command_queue = unsupported
  @CSignature("cl_context (const cl_context_properties *, cl_uint, const cl_device_id *, void (*)(const char *, const void *, size_t, void *), void *, cl_int *)")
  def clCreateContext(properties: Pointer[cl_context_properties, Read, Opaque], num_devices: cl_uint, devices: Pointer[cl_device_id, Read, Opaque], pfn_notify: Callback["void (*)(const char *, const void *, size_t, void *)"], user_data: Pointer[CVoid, ReadWrite, Opaque], errcode_ret: Pointer[cl_int, ReadWrite, Opaque]): cl_context = unsupported
  @CSignature("cl_context (const cl_context_properties *, cl_device_type, void (*)(const char *, const void *, size_t, void *), void *, cl_int *)")
  def clCreateContextFromType(properties: Pointer[cl_context_properties, Read, Opaque], device_type: cl_device_type, pfn_notify: Callback["void (*)(const char *, const void *, size_t, void *)"], user_data: Pointer[CVoid, ReadWrite, Opaque], errcode_ret: Pointer[cl_int, ReadWrite, Opaque]): cl_context = unsupported
  @CSignature("cl_event (cl_context, cl_GLsync, cl_int *)")
  def clCreateEventFromGLsyncKHR(context: cl_context, sync: cl_GLsync, errcode_ret: Pointer[cl_int, ReadWrite, Opaque]): cl_event = unsupported
  @CSignature("cl_mem (cl_context, cl_mem_flags, cl_GLuint, cl_int *)")
  def clCreateFromGLBuffer(context: cl_context, flags: cl_mem_flags, bufobj: cl_GLuint, errcode_ret: Pointer[cl_int, ReadWrite, Opaque]): cl_mem = unsupported
  @CSignature("cl_mem (cl_context, cl_mem_flags, cl_GLuint, cl_int *)")
  def clCreateFromGLRenderbuffer(context: cl_context, flags: cl_mem_flags, renderbuffer: cl_GLuint, errcode_ret: Pointer[cl_int, ReadWrite, Opaque]): cl_mem = unsupported
  @CSignature("cl_mem (cl_context, cl_mem_flags, cl_GLenum, cl_GLint, cl_GLuint, cl_int *)")
  def clCreateFromGLTexture(context: cl_context, flags: cl_mem_flags, target: cl_GLenum, miplevel: cl_GLint, texture: cl_GLuint, errcode_ret: Pointer[cl_int, ReadWrite, Opaque]): cl_mem = unsupported
  @CSignature("cl_mem (cl_context, cl_mem_flags, cl_GLenum, cl_GLint, cl_GLuint, cl_int *)")
  def clCreateFromGLTexture2D(context: cl_context, flags: cl_mem_flags, target: cl_GLenum, miplevel: cl_GLint, texture: cl_GLuint, errcode_ret: Pointer[cl_int, ReadWrite, Opaque]): cl_mem = unsupported
  @CSignature("cl_mem (cl_context, cl_mem_flags, cl_GLenum, cl_GLint, cl_GLuint, cl_int *)")
  def clCreateFromGLTexture3D(context: cl_context, flags: cl_mem_flags, target: cl_GLenum, miplevel: cl_GLint, texture: cl_GLuint, errcode_ret: Pointer[cl_int, ReadWrite, Opaque]): cl_mem = unsupported
  @CSignature("cl_mem (cl_context, cl_mem_flags, const cl_image_format *, const cl_image_desc *, void *, cl_int *)")
  def clCreateImage(context: cl_context, flags: cl_mem_flags, image_format: Pointer[cl_image_format, Read, Opaque], image_desc: Pointer[cl_image_desc, Read, Opaque], host_ptr: Pointer[CVoid, ReadWrite, Opaque], errcode_ret: Pointer[cl_int, ReadWrite, Opaque]): cl_mem = unsupported
  @CSignature("cl_mem (cl_context, cl_mem_flags, const cl_image_format *, size_t, size_t, size_t, void *, cl_int *)")
  def clCreateImage2D(context: cl_context, flags: cl_mem_flags, image_format: Pointer[cl_image_format, Read, Opaque], image_width: size_t, image_height: size_t, image_row_pitch: size_t, host_ptr: Pointer[CVoid, ReadWrite, Opaque], errcode_ret: Pointer[cl_int, ReadWrite, Opaque]): cl_mem = unsupported
  @CSignature("cl_mem (cl_context, cl_mem_flags, const cl_image_format *, size_t, size_t, size_t, size_t, size_t, void *, cl_int *)")
  def clCreateImage3D(context: cl_context, flags: cl_mem_flags, image_format: Pointer[cl_image_format, Read, Opaque], image_width: size_t, image_height: size_t, image_depth: size_t, image_row_pitch: size_t, image_slice_pitch: size_t, host_ptr: Pointer[CVoid, ReadWrite, Opaque], errcode_ret: Pointer[cl_int, ReadWrite, Opaque]): cl_mem = unsupported
  @CSignature("cl_mem (cl_context, const cl_mem_properties *, cl_mem_flags, const cl_image_format *, const cl_image_desc *, void *, cl_int *)")
  def clCreateImageWithProperties(context: cl_context, properties: Pointer[cl_mem_properties, Read, Opaque], flags: cl_mem_flags, image_format: Pointer[cl_image_format, Read, Opaque], image_desc: Pointer[cl_image_desc, Read, Opaque], host_ptr: Pointer[CVoid, ReadWrite, Opaque], errcode_ret: Pointer[cl_int, ReadWrite, Opaque]): cl_mem = unsupported
  @CSignature("cl_kernel (cl_program, const char *, cl_int *)")
  def clCreateKernel(program: cl_program, kernel_name: CString, errcode_ret: Pointer[cl_int, ReadWrite, Opaque]): cl_kernel = unsupported
  @CSignature("cl_int (cl_program, cl_uint, cl_kernel *, cl_uint *)")
  def clCreateKernelsInProgram(program: cl_program, num_kernels: cl_uint, kernels: Pointer[cl_kernel, ReadWrite, Opaque], num_kernels_ret: Pointer[cl_uint, ReadWrite, Opaque]): cl_int = unsupported
  @CSignature("cl_mem (cl_context, cl_mem_flags, cl_uint, cl_uint, const cl_pipe_properties *, cl_int *)")
  def clCreatePipe(context: cl_context, flags: cl_mem_flags, pipe_packet_size: cl_uint, pipe_max_packets: cl_uint, properties: Pointer[cl_pipe_properties, Read, Opaque], errcode_ret: Pointer[cl_int, ReadWrite, Opaque]): cl_mem = unsupported
  @CSignature("cl_program (cl_context, cl_uint, const cl_device_id *, const size_t *, const unsigned char **, cl_int *, cl_int *)")
  def clCreateProgramWithBinary(context: cl_context, num_devices: cl_uint, device_list: Pointer[cl_device_id, Read, Opaque], lengths: Pointer[size_t, Read, Opaque], binaries: Pointer[Pointer[unsigned_char, ReadWrite, Opaque], Read, Opaque], binary_status: Pointer[cl_int, ReadWrite, Opaque], errcode_ret: Pointer[cl_int, ReadWrite, Opaque]): cl_program = unsupported
  @CSignature("cl_program (cl_context, cl_uint, const cl_device_id *, const char *, cl_int *)")
  def clCreateProgramWithBuiltInKernels(context: cl_context, num_devices: cl_uint, device_list: Pointer[cl_device_id, Read, Opaque], kernel_names: CString, errcode_ret: Pointer[cl_int, ReadWrite, Opaque]): cl_program = unsupported
  @CSignature("cl_program (cl_context, const void *, size_t, cl_int *)")
  def clCreateProgramWithIL(context: cl_context, il: Pointer[CVoid, Read, Opaque], length: size_t, errcode_ret: Pointer[cl_int, ReadWrite, Opaque]): cl_program = unsupported
  @CSignature("cl_program (cl_context, const void *, size_t, cl_int *)")
  def clCreateProgramWithILKHR(context: cl_context, il: Pointer[CVoid, Read, Opaque], length: size_t, errcode_ret: Pointer[cl_int, ReadWrite, Opaque]): cl_program = unsupported
  @CSignature("cl_program (cl_context, cl_uint, const char **, const size_t *, cl_int *)")
  def clCreateProgramWithSource(context: cl_context, count: cl_uint, strings: Pointer[Pointer[Byte, ReadWrite, Opaque], Read, Opaque], lengths: Pointer[size_t, Read, Opaque], errcode_ret: Pointer[cl_int, ReadWrite, Opaque]): cl_program = unsupported
  @CSignature("cl_sampler (cl_context, cl_bool, cl_addressing_mode, cl_filter_mode, cl_int *)")
  def clCreateSampler(context: cl_context, normalized_coords: cl_bool, addressing_mode: cl_addressing_mode, filter_mode: cl_filter_mode, errcode_ret: Pointer[cl_int, ReadWrite, Opaque]): cl_sampler = unsupported
  @CSignature("cl_sampler (cl_context, const cl_sampler_properties *, cl_int *)")
  def clCreateSamplerWithProperties(context: cl_context, sampler_properties: Pointer[cl_sampler_properties, Read, Opaque], errcode_ret: Pointer[cl_int, ReadWrite, Opaque]): cl_sampler = unsupported
  @CSignature("cl_semaphore_khr (cl_context, const cl_semaphore_properties_khr *, cl_int *)")
  def clCreateSemaphoreWithPropertiesKHR(context: cl_context, sema_props: Pointer[cl_semaphore_properties_khr, Read, Opaque], errcode_ret: Pointer[cl_int, ReadWrite, Opaque]): cl_semaphore_khr = unsupported
  @CSignature("cl_mem (cl_mem, cl_mem_flags, cl_buffer_create_type, const void *, cl_int *)")
  def clCreateSubBuffer(buffer: cl_mem, flags: cl_mem_flags, buffer_create_type: cl_buffer_create_type, buffer_create_info: Pointer[CVoid, Read, Opaque], errcode_ret: Pointer[cl_int, ReadWrite, Opaque]): cl_mem = unsupported
  @CSignature("cl_int (cl_device_id, const cl_device_partition_property *, cl_uint, cl_device_id *, cl_uint *)")
  def clCreateSubDevices(in_device: cl_device_id, properties: Pointer[cl_device_partition_property, Read, Opaque], num_devices: cl_uint, out_devices: Pointer[cl_device_id, ReadWrite, Opaque], num_devices_ret: Pointer[cl_uint, ReadWrite, Opaque]): cl_int = unsupported
  @CSignature("cl_int (cl_device_id, const cl_device_partition_property_ext *, cl_uint, cl_device_id *, cl_uint *)")
  def clCreateSubDevicesEXT(in_device: cl_device_id, properties: Pointer[cl_device_partition_property_ext, Read, Opaque], num_entries: cl_uint, out_devices: Pointer[cl_device_id, ReadWrite, Opaque], num_devices: Pointer[cl_uint, ReadWrite, Opaque]): cl_int = unsupported
  @CSignature("cl_event (cl_context, cl_int *)")
  def clCreateUserEvent(context: cl_context, errcode_ret: Pointer[cl_int, ReadWrite, Opaque]): cl_event = unsupported
  @CSignature("void *(cl_context, cl_device_id, const cl_mem_properties_intel *, size_t, cl_uint, cl_int *)")
  def clDeviceMemAllocINTEL(context: cl_context, device: cl_device_id, properties: Pointer[cl_mem_properties_intel, Read, Opaque], size: size_t, alignment: cl_uint, errcode_ret: Pointer[cl_int, ReadWrite, Opaque]): Pointer[CVoid, ReadWrite, Opaque] = unsupported
  @CSignature("cl_int (cl_command_queue, cl_uint, const cl_mem *, cl_uint, const cl_event *, cl_event *)")
  def clEnqueueAcquireExternalMemObjectsKHR(command_queue: cl_command_queue, num_mem_objects: cl_uint, mem_objects: Pointer[cl_mem, Read, Opaque], num_events_in_wait_list: cl_uint, event_wait_list: Pointer[cl_event, Read, Opaque], event: Pointer[cl_event, ReadWrite, Opaque]): cl_int = unsupported
  @CSignature("cl_int (cl_command_queue, cl_uint, const cl_mem *, cl_uint, const cl_event *, cl_event *)")
  def clEnqueueAcquireGLObjects(command_queue: cl_command_queue, num_objects: cl_uint, mem_objects: Pointer[cl_mem, Read, Opaque], num_events_in_wait_list: cl_uint, event_wait_list: Pointer[cl_event, Read, Opaque], event: Pointer[cl_event, ReadWrite, Opaque]): cl_int = unsupported
  @CSignature("cl_int (cl_command_queue, cl_uint, const cl_mem *, cl_uint, const cl_event *, cl_event *)")
  def clEnqueueAcquireGrallocObjectsIMG(command_queue: cl_command_queue, num_objects: cl_uint, mem_objects: Pointer[cl_mem, Read, Opaque], num_events_in_wait_list: cl_uint, event_wait_list: Pointer[cl_event, Read, Opaque], event: Pointer[cl_event, ReadWrite, Opaque]): cl_int = unsupported
  @CSignature("cl_int (cl_command_queue)")
  def clEnqueueBarrier(command_queue: cl_command_queue): cl_int = unsupported
  @CSignature("cl_int (cl_command_queue, cl_uint, const cl_event *, cl_event *)")
  def clEnqueueBarrierWithWaitList(command_queue: cl_command_queue, num_events_in_wait_list: cl_uint, event_wait_list: Pointer[cl_event, Read, Opaque], event: Pointer[cl_event, ReadWrite, Opaque]): cl_int = unsupported
  @CSignature("cl_int (cl_command_queue, cl_mem, cl_mem, size_t, size_t, size_t, cl_uint, const cl_event *, cl_event *)")
  def clEnqueueCopyBuffer(command_queue: cl_command_queue, src_buffer: cl_mem, dst_buffer: cl_mem, src_offset: size_t, dst_offset: size_t, size: size_t, num_events_in_wait_list: cl_uint, event_wait_list: Pointer[cl_event, Read, Opaque], event: Pointer[cl_event, ReadWrite, Opaque]): cl_int = unsupported
  @CSignature("cl_int (cl_command_queue, cl_mem, cl_mem, const size_t *, const size_t *, const size_t *, size_t, size_t, size_t, size_t, cl_uint, const cl_event *, cl_event *)")
  def clEnqueueCopyBufferRect(command_queue: cl_command_queue, src_buffer: cl_mem, dst_buffer: cl_mem, src_origin: Pointer[size_t, Read, Opaque], dst_origin: Pointer[size_t, Read, Opaque], region: Pointer[size_t, Read, Opaque], src_row_pitch: size_t, src_slice_pitch: size_t, dst_row_pitch: size_t, dst_slice_pitch: size_t, num_events_in_wait_list: cl_uint, event_wait_list: Pointer[cl_event, Read, Opaque], event: Pointer[cl_event, ReadWrite, Opaque]): cl_int = unsupported
  @CSignature("cl_int (cl_command_queue, cl_mem, cl_mem, size_t, const size_t *, const size_t *, cl_uint, const cl_event *, cl_event *)")
  def clEnqueueCopyBufferToImage(command_queue: cl_command_queue, src_buffer: cl_mem, dst_image: cl_mem, src_offset: size_t, dst_origin: Pointer[size_t, Read, Opaque], region: Pointer[size_t, Read, Opaque], num_events_in_wait_list: cl_uint, event_wait_list: Pointer[cl_event, Read, Opaque], event: Pointer[cl_event, ReadWrite, Opaque]): cl_int = unsupported
  @CSignature("cl_int (cl_command_queue, cl_mem, cl_mem, const size_t *, const size_t *, const size_t *, cl_uint, const cl_event *, cl_event *)")
  def clEnqueueCopyImage(command_queue: cl_command_queue, src_image: cl_mem, dst_image: cl_mem, src_origin: Pointer[size_t, Read, Opaque], dst_origin: Pointer[size_t, Read, Opaque], region: Pointer[size_t, Read, Opaque], num_events_in_wait_list: cl_uint, event_wait_list: Pointer[cl_event, Read, Opaque], event: Pointer[cl_event, ReadWrite, Opaque]): cl_int = unsupported
  @CSignature("cl_int (cl_command_queue, cl_mem, cl_mem, const size_t *, const size_t *, size_t, cl_uint, const cl_event *, cl_event *)")
  def clEnqueueCopyImageToBuffer(command_queue: cl_command_queue, src_image: cl_mem, dst_buffer: cl_mem, src_origin: Pointer[size_t, Read, Opaque], region: Pointer[size_t, Read, Opaque], dst_offset: size_t, num_events_in_wait_list: cl_uint, event_wait_list: Pointer[cl_event, Read, Opaque], event: Pointer[cl_event, ReadWrite, Opaque]): cl_int = unsupported
  @CSignature("cl_int (cl_command_queue, cl_mem, const void *, size_t, size_t, size_t, cl_uint, const cl_event *, cl_event *)")
  def clEnqueueFillBuffer(command_queue: cl_command_queue, buffer: cl_mem, pattern: Pointer[CVoid, Read, Opaque], pattern_size: size_t, offset: size_t, size: size_t, num_events_in_wait_list: cl_uint, event_wait_list: Pointer[cl_event, Read, Opaque], event: Pointer[cl_event, ReadWrite, Opaque]): cl_int = unsupported
  @CSignature("cl_int (cl_command_queue, cl_mem, const void *, const size_t *, const size_t *, cl_uint, const cl_event *, cl_event *)")
  def clEnqueueFillImage(command_queue: cl_command_queue, image: cl_mem, fill_color: Pointer[CVoid, Read, Opaque], origin: Pointer[size_t, Read, Opaque], region: Pointer[size_t, Read, Opaque], num_events_in_wait_list: cl_uint, event_wait_list: Pointer[cl_event, Read, Opaque], event: Pointer[cl_event, ReadWrite, Opaque]): cl_int = unsupported
  @CSignature("cl_int (cl_command_queue, cl_mem, cl_mem, cl_mipmap_filter_mode_img, const size_t *, const size_t *, cl_uint, const cl_event *, cl_event *)")
  def clEnqueueGenerateMipmapIMG(command_queue: cl_command_queue, src_image: cl_mem, dst_image: cl_mem, mipmap_filter_mode: cl_mipmap_filter_mode_img, array_region: Pointer[size_t, Read, Opaque], mip_region: Pointer[size_t, Read, Opaque], num_events_in_wait_list: cl_uint, event_wait_list: Pointer[cl_event, Read, Opaque], event: Pointer[cl_event, ReadWrite, Opaque]): cl_int = unsupported
  @CSignature("void *(cl_command_queue, cl_mem, cl_bool, cl_map_flags, size_t, size_t, cl_uint, const cl_event *, cl_event *, cl_int *)")
  def clEnqueueMapBuffer(command_queue: cl_command_queue, buffer: cl_mem, blocking_map: cl_bool, map_flags: cl_map_flags, offset: size_t, size: size_t, num_events_in_wait_list: cl_uint, event_wait_list: Pointer[cl_event, Read, Opaque], event: Pointer[cl_event, ReadWrite, Opaque], errcode_ret: Pointer[cl_int, ReadWrite, Opaque]): Pointer[CVoid, ReadWrite, Opaque] = unsupported
  @CSignature("void *(cl_command_queue, cl_mem, cl_bool, cl_map_flags, const size_t *, const size_t *, size_t *, size_t *, cl_uint, const cl_event *, cl_event *, cl_int *)")
  def clEnqueueMapImage(command_queue: cl_command_queue, image: cl_mem, blocking_map: cl_bool, map_flags: cl_map_flags, origin: Pointer[size_t, Read, Opaque], region: Pointer[size_t, Read, Opaque], image_row_pitch: Pointer[size_t, ReadWrite, Opaque], image_slice_pitch: Pointer[size_t, ReadWrite, Opaque], num_events_in_wait_list: cl_uint, event_wait_list: Pointer[cl_event, Read, Opaque], event: Pointer[cl_event, ReadWrite, Opaque], errcode_ret: Pointer[cl_int, ReadWrite, Opaque]): Pointer[CVoid, ReadWrite, Opaque] = unsupported
  @CSignature("cl_int (cl_command_queue, cl_event *)")
  def clEnqueueMarker(command_queue: cl_command_queue, event: Pointer[cl_event, ReadWrite, Opaque]): cl_int = unsupported
  @CSignature("cl_int (cl_command_queue, cl_uint, const cl_event *, cl_event *)")
  def clEnqueueMarkerWithWaitList(command_queue: cl_command_queue, num_events_in_wait_list: cl_uint, event_wait_list: Pointer[cl_event, Read, Opaque], event: Pointer[cl_event, ReadWrite, Opaque]): cl_int = unsupported
  @CSignature("cl_int (cl_command_queue, const void *, size_t, cl_mem_advice_intel, cl_uint, const cl_event *, cl_event *)")
  def clEnqueueMemAdviseINTEL(command_queue: cl_command_queue, ptr: Pointer[CVoid, Read, Opaque], size: size_t, advice: cl_mem_advice_intel, num_events_in_wait_list: cl_uint, event_wait_list: Pointer[cl_event, Read, Opaque], event: Pointer[cl_event, ReadWrite, Opaque]): cl_int = unsupported
  @CSignature("cl_int (cl_command_queue, void *, const void *, size_t, size_t, cl_uint, const cl_event *, cl_event *)")
  def clEnqueueMemFillINTEL(command_queue: cl_command_queue, dst_ptr: Pointer[CVoid, ReadWrite, Opaque], pattern: Pointer[CVoid, Read, Opaque], pattern_size: size_t, size: size_t, num_events_in_wait_list: cl_uint, event_wait_list: Pointer[cl_event, Read, Opaque], event: Pointer[cl_event, ReadWrite, Opaque]): cl_int = unsupported
  @CSignature("cl_int (cl_command_queue, cl_bool, void *, const void *, size_t, cl_uint, const cl_event *, cl_event *)")
  def clEnqueueMemcpyINTEL(command_queue: cl_command_queue, blocking: cl_bool, dst_ptr: Pointer[CVoid, ReadWrite, Opaque], src_ptr: Pointer[CVoid, Read, Opaque], size: size_t, num_events_in_wait_list: cl_uint, event_wait_list: Pointer[cl_event, Read, Opaque], event: Pointer[cl_event, ReadWrite, Opaque]): cl_int = unsupported
  @CSignature("cl_int (cl_command_queue, void *, cl_int, size_t, cl_uint, const cl_event *, cl_event *)")
  def clEnqueueMemsetINTEL(command_queue: cl_command_queue, dst_ptr: Pointer[CVoid, ReadWrite, Opaque], value: cl_int, size: size_t, num_events_in_wait_list: cl_uint, event_wait_list: Pointer[cl_event, Read, Opaque], event: Pointer[cl_event, ReadWrite, Opaque]): cl_int = unsupported
  @CSignature("cl_int (cl_command_queue, const void *, size_t, cl_mem_migration_flags, cl_uint, const cl_event *, cl_event *)")
  def clEnqueueMigrateMemINTEL(command_queue: cl_command_queue, ptr: Pointer[CVoid, Read, Opaque], size: size_t, flags: cl_mem_migration_flags, num_events_in_wait_list: cl_uint, event_wait_list: Pointer[cl_event, Read, Opaque], event: Pointer[cl_event, ReadWrite, Opaque]): cl_int = unsupported
  @CSignature("cl_int (cl_command_queue, cl_uint, const cl_mem *, cl_mem_migration_flags_ext, cl_uint, const cl_event *, cl_event *)")
  def clEnqueueMigrateMemObjectEXT(command_queue: cl_command_queue, num_mem_objects: cl_uint, mem_objects: Pointer[cl_mem, Read, Opaque], flags: cl_mem_migration_flags_ext, num_events_in_wait_list: cl_uint, event_wait_list: Pointer[cl_event, Read, Opaque], event: Pointer[cl_event, ReadWrite, Opaque]): cl_int = unsupported
  @CSignature("cl_int (cl_command_queue, cl_uint, const cl_mem *, cl_mem_migration_flags, cl_uint, const cl_event *, cl_event *)")
  def clEnqueueMigrateMemObjects(command_queue: cl_command_queue, num_mem_objects: cl_uint, mem_objects: Pointer[cl_mem, Read, Opaque], flags: cl_mem_migration_flags, num_events_in_wait_list: cl_uint, event_wait_list: Pointer[cl_event, Read, Opaque], event: Pointer[cl_event, ReadWrite, Opaque]): cl_int = unsupported
  @CSignature("cl_int (cl_command_queue, cl_kernel, cl_uint, const size_t *, const size_t *, const size_t *, cl_uint, const cl_event *, cl_event *)")
  def clEnqueueNDRangeKernel(command_queue: cl_command_queue, kernel: cl_kernel, work_dim: cl_uint, global_work_offset: Pointer[size_t, Read, Opaque], global_work_size: Pointer[size_t, Read, Opaque], local_work_size: Pointer[size_t, Read, Opaque], num_events_in_wait_list: cl_uint, event_wait_list: Pointer[cl_event, Read, Opaque], event: Pointer[cl_event, ReadWrite, Opaque]): cl_int = unsupported
  @CSignature("cl_int (cl_command_queue, void (*)(void *), void *, size_t, cl_uint, const cl_mem *, const void **, cl_uint, const cl_event *, cl_event *)")
  def clEnqueueNativeKernel(command_queue: cl_command_queue, user_func: Callback["void (*)(void *)"], args: Pointer[CVoid, ReadWrite, Opaque], cb_args: size_t, num_mem_objects: cl_uint, mem_list: Pointer[cl_mem, Read, Opaque], args_mem_loc: Pointer[Pointer[CVoid, ReadWrite, Opaque], Read, Opaque], num_events_in_wait_list: cl_uint, event_wait_list: Pointer[cl_event, Read, Opaque], event: Pointer[cl_event, ReadWrite, Opaque]): cl_int = unsupported
  @CSignature("cl_int (cl_command_queue, cl_mem, cl_bool, size_t, size_t, void *, cl_uint, const cl_event *, cl_event *)")
  def clEnqueueReadBuffer(command_queue: cl_command_queue, buffer: cl_mem, blocking_read: cl_bool, offset: size_t, size: size_t, ptr: Pointer[CVoid, ReadWrite, Opaque], num_events_in_wait_list: cl_uint, event_wait_list: Pointer[cl_event, Read, Opaque], event: Pointer[cl_event, ReadWrite, Opaque]): cl_int = unsupported
  @CSignature("cl_int (cl_command_queue, cl_mem, cl_bool, const size_t *, const size_t *, const size_t *, size_t, size_t, size_t, size_t, void *, cl_uint, const cl_event *, cl_event *)")
  def clEnqueueReadBufferRect(command_queue: cl_command_queue, buffer: cl_mem, blocking_read: cl_bool, buffer_origin: Pointer[size_t, Read, Opaque], host_origin: Pointer[size_t, Read, Opaque], region: Pointer[size_t, Read, Opaque], buffer_row_pitch: size_t, buffer_slice_pitch: size_t, host_row_pitch: size_t, host_slice_pitch: size_t, ptr: Pointer[CVoid, ReadWrite, Opaque], num_events_in_wait_list: cl_uint, event_wait_list: Pointer[cl_event, Read, Opaque], event: Pointer[cl_event, ReadWrite, Opaque]): cl_int = unsupported
  @CSignature("cl_int (cl_command_queue, cl_program, const char *, cl_bool, void *, size_t, cl_uint, const cl_event *, cl_event *)")
  def clEnqueueReadHostPipeINTEL(command_queue: cl_command_queue, program: cl_program, pipe_symbol: CString, blocking_read: cl_bool, ptr: Pointer[CVoid, ReadWrite, Opaque], size: size_t, num_events_in_wait_list: cl_uint, event_wait_list: Pointer[cl_event, Read, Opaque], event: Pointer[cl_event, ReadWrite, Opaque]): cl_int = unsupported
  @CSignature("cl_int (cl_command_queue, cl_mem, cl_bool, const size_t *, const size_t *, size_t, size_t, void *, cl_uint, const cl_event *, cl_event *)")
  def clEnqueueReadImage(command_queue: cl_command_queue, image: cl_mem, blocking_read: cl_bool, origin: Pointer[size_t, Read, Opaque], region: Pointer[size_t, Read, Opaque], row_pitch: size_t, slice_pitch: size_t, ptr: Pointer[CVoid, ReadWrite, Opaque], num_events_in_wait_list: cl_uint, event_wait_list: Pointer[cl_event, Read, Opaque], event: Pointer[cl_event, ReadWrite, Opaque]): cl_int = unsupported
  @CSignature("cl_int (cl_command_queue, cl_uint, const cl_mem *, cl_uint, const cl_event *, cl_event *)")
  def clEnqueueReleaseExternalMemObjectsKHR(command_queue: cl_command_queue, num_mem_objects: cl_uint, mem_objects: Pointer[cl_mem, Read, Opaque], num_events_in_wait_list: cl_uint, event_wait_list: Pointer[cl_event, Read, Opaque], event: Pointer[cl_event, ReadWrite, Opaque]): cl_int = unsupported
  @CSignature("cl_int (cl_command_queue, cl_uint, const cl_mem *, cl_uint, const cl_event *, cl_event *)")
  def clEnqueueReleaseGLObjects(command_queue: cl_command_queue, num_objects: cl_uint, mem_objects: Pointer[cl_mem, Read, Opaque], num_events_in_wait_list: cl_uint, event_wait_list: Pointer[cl_event, Read, Opaque], event: Pointer[cl_event, ReadWrite, Opaque]): cl_int = unsupported
  @CSignature("cl_int (cl_command_queue, cl_uint, const cl_mem *, cl_uint, const cl_event *, cl_event *)")
  def clEnqueueReleaseGrallocObjectsIMG(command_queue: cl_command_queue, num_objects: cl_uint, mem_objects: Pointer[cl_mem, Read, Opaque], num_events_in_wait_list: cl_uint, event_wait_list: Pointer[cl_event, Read, Opaque], event: Pointer[cl_event, ReadWrite, Opaque]): cl_int = unsupported
  @CSignature("cl_int (cl_command_queue, cl_uint, void **, void (*)(cl_command_queue, cl_uint, void **, void *), void *, cl_uint, const cl_event *, cl_event *)")
  def clEnqueueSVMFree(command_queue: cl_command_queue, num_svm_pointers: cl_uint, svm_pointers: Pointer[Pointer[CVoid, ReadWrite, Opaque], ReadWrite, Opaque], pfn_free_func: Callback["void (*)(cl_command_queue, cl_uint, void **, void *)"], user_data: Pointer[CVoid, ReadWrite, Opaque], num_events_in_wait_list: cl_uint, event_wait_list: Pointer[cl_event, Read, Opaque], event: Pointer[cl_event, ReadWrite, Opaque]): cl_int = unsupported
  @CSignature("cl_int (cl_command_queue, cl_uint, void **, void (*)(cl_command_queue, cl_uint, void **, void *), void *, cl_uint, const cl_event *, cl_event *)")
  def clEnqueueSVMFreeARM(command_queue: cl_command_queue, num_svm_pointers: cl_uint, svm_pointers: Pointer[Pointer[CVoid, ReadWrite, Opaque], ReadWrite, Opaque], pfn_free_func: Callback["void (*)(cl_command_queue, cl_uint, void **, void *)"], user_data: Pointer[CVoid, ReadWrite, Opaque], num_events_in_wait_list: cl_uint, event_wait_list: Pointer[cl_event, Read, Opaque], event: Pointer[cl_event, ReadWrite, Opaque]): cl_int = unsupported
  @CSignature("cl_int (cl_command_queue, cl_bool, cl_map_flags, void *, size_t, cl_uint, const cl_event *, cl_event *)")
  def clEnqueueSVMMap(command_queue: cl_command_queue, blocking_map: cl_bool, flags: cl_map_flags, svm_ptr: Pointer[CVoid, ReadWrite, Opaque], size: size_t, num_events_in_wait_list: cl_uint, event_wait_list: Pointer[cl_event, Read, Opaque], event: Pointer[cl_event, ReadWrite, Opaque]): cl_int = unsupported
  @CSignature("cl_int (cl_command_queue, cl_bool, cl_map_flags, void *, size_t, cl_uint, const cl_event *, cl_event *)")
  def clEnqueueSVMMapARM(command_queue: cl_command_queue, blocking_map: cl_bool, flags: cl_map_flags, svm_ptr: Pointer[CVoid, ReadWrite, Opaque], size: size_t, num_events_in_wait_list: cl_uint, event_wait_list: Pointer[cl_event, Read, Opaque], event: Pointer[cl_event, ReadWrite, Opaque]): cl_int = unsupported
  @CSignature("cl_int (cl_command_queue, void *, const void *, size_t, size_t, cl_uint, const cl_event *, cl_event *)")
  def clEnqueueSVMMemFill(command_queue: cl_command_queue, svm_ptr: Pointer[CVoid, ReadWrite, Opaque], pattern: Pointer[CVoid, Read, Opaque], pattern_size: size_t, size: size_t, num_events_in_wait_list: cl_uint, event_wait_list: Pointer[cl_event, Read, Opaque], event: Pointer[cl_event, ReadWrite, Opaque]): cl_int = unsupported
  @CSignature("cl_int (cl_command_queue, void *, const void *, size_t, size_t, cl_uint, const cl_event *, cl_event *)")
  def clEnqueueSVMMemFillARM(command_queue: cl_command_queue, svm_ptr: Pointer[CVoid, ReadWrite, Opaque], pattern: Pointer[CVoid, Read, Opaque], pattern_size: size_t, size: size_t, num_events_in_wait_list: cl_uint, event_wait_list: Pointer[cl_event, Read, Opaque], event: Pointer[cl_event, ReadWrite, Opaque]): cl_int = unsupported
  @CSignature("cl_int (cl_command_queue, cl_bool, void *, const void *, size_t, cl_uint, const cl_event *, cl_event *)")
  def clEnqueueSVMMemcpy(command_queue: cl_command_queue, blocking_copy: cl_bool, dst_ptr: Pointer[CVoid, ReadWrite, Opaque], src_ptr: Pointer[CVoid, Read, Opaque], size: size_t, num_events_in_wait_list: cl_uint, event_wait_list: Pointer[cl_event, Read, Opaque], event: Pointer[cl_event, ReadWrite, Opaque]): cl_int = unsupported
  @CSignature("cl_int (cl_command_queue, cl_bool, void *, const void *, size_t, cl_uint, const cl_event *, cl_event *)")
  def clEnqueueSVMMemcpyARM(command_queue: cl_command_queue, blocking_copy: cl_bool, dst_ptr: Pointer[CVoid, ReadWrite, Opaque], src_ptr: Pointer[CVoid, Read, Opaque], size: size_t, num_events_in_wait_list: cl_uint, event_wait_list: Pointer[cl_event, Read, Opaque], event: Pointer[cl_event, ReadWrite, Opaque]): cl_int = unsupported
  @CSignature("cl_int (cl_command_queue, cl_uint, const void **, const size_t *, cl_mem_migration_flags, cl_uint, const cl_event *, cl_event *)")
  def clEnqueueSVMMigrateMem(command_queue: cl_command_queue, num_svm_pointers: cl_uint, svm_pointers: Pointer[Pointer[CVoid, ReadWrite, Opaque], Read, Opaque], sizes: Pointer[size_t, Read, Opaque], flags: cl_mem_migration_flags, num_events_in_wait_list: cl_uint, event_wait_list: Pointer[cl_event, Read, Opaque], event: Pointer[cl_event, ReadWrite, Opaque]): cl_int = unsupported
  @CSignature("cl_int (cl_command_queue, void *, cl_uint, const cl_event *, cl_event *)")
  def clEnqueueSVMUnmap(command_queue: cl_command_queue, svm_ptr: Pointer[CVoid, ReadWrite, Opaque], num_events_in_wait_list: cl_uint, event_wait_list: Pointer[cl_event, Read, Opaque], event: Pointer[cl_event, ReadWrite, Opaque]): cl_int = unsupported
  @CSignature("cl_int (cl_command_queue, void *, cl_uint, const cl_event *, cl_event *)")
  def clEnqueueSVMUnmapARM(command_queue: cl_command_queue, svm_ptr: Pointer[CVoid, ReadWrite, Opaque], num_events_in_wait_list: cl_uint, event_wait_list: Pointer[cl_event, Read, Opaque], event: Pointer[cl_event, ReadWrite, Opaque]): cl_int = unsupported
  @CSignature("cl_int (cl_command_queue, cl_uint, const cl_semaphore_khr *, const cl_semaphore_payload_khr *, cl_uint, const cl_event *, cl_event *)")
  def clEnqueueSignalSemaphoresKHR(command_queue: cl_command_queue, num_sema_objects: cl_uint, sema_objects: Pointer[cl_semaphore_khr, Read, Opaque], sema_payload_list: Pointer[cl_semaphore_payload_khr, Read, Opaque], num_events_in_wait_list: cl_uint, event_wait_list: Pointer[cl_event, Read, Opaque], event: Pointer[cl_event, ReadWrite, Opaque]): cl_int = unsupported
  @CSignature("cl_int (cl_command_queue, cl_kernel, cl_uint, const cl_event *, cl_event *)")
  def clEnqueueTask(command_queue: cl_command_queue, kernel: cl_kernel, num_events_in_wait_list: cl_uint, event_wait_list: Pointer[cl_event, Read, Opaque], event: Pointer[cl_event, ReadWrite, Opaque]): cl_int = unsupported
  @CSignature("cl_int (cl_command_queue, cl_mem, void *, cl_uint, const cl_event *, cl_event *)")
  def clEnqueueUnmapMemObject(command_queue: cl_command_queue, memobj: cl_mem, mapped_ptr: Pointer[CVoid, ReadWrite, Opaque], num_events_in_wait_list: cl_uint, event_wait_list: Pointer[cl_event, Read, Opaque], event: Pointer[cl_event, ReadWrite, Opaque]): cl_int = unsupported
  @CSignature("cl_int (cl_command_queue, cl_uint, const cl_event *)")
  def clEnqueueWaitForEvents(command_queue: cl_command_queue, num_events: cl_uint, event_list: Pointer[cl_event, Read, Opaque]): cl_int = unsupported
  @CSignature("cl_int (cl_command_queue, cl_uint, const cl_semaphore_khr *, const cl_semaphore_payload_khr *, cl_uint, const cl_event *, cl_event *)")
  def clEnqueueWaitSemaphoresKHR(command_queue: cl_command_queue, num_sema_objects: cl_uint, sema_objects: Pointer[cl_semaphore_khr, Read, Opaque], sema_payload_list: Pointer[cl_semaphore_payload_khr, Read, Opaque], num_events_in_wait_list: cl_uint, event_wait_list: Pointer[cl_event, Read, Opaque], event: Pointer[cl_event, ReadWrite, Opaque]): cl_int = unsupported
  @CSignature("cl_int (cl_command_queue, cl_mem, cl_bool, size_t, size_t, const void *, cl_uint, const cl_event *, cl_event *)")
  def clEnqueueWriteBuffer(command_queue: cl_command_queue, buffer: cl_mem, blocking_write: cl_bool, offset: size_t, size: size_t, ptr: Pointer[CVoid, Read, Opaque], num_events_in_wait_list: cl_uint, event_wait_list: Pointer[cl_event, Read, Opaque], event: Pointer[cl_event, ReadWrite, Opaque]): cl_int = unsupported
  @CSignature("cl_int (cl_command_queue, cl_mem, cl_bool, const size_t *, const size_t *, const size_t *, size_t, size_t, size_t, size_t, const void *, cl_uint, const cl_event *, cl_event *)")
  def clEnqueueWriteBufferRect(command_queue: cl_command_queue, buffer: cl_mem, blocking_write: cl_bool, buffer_origin: Pointer[size_t, Read, Opaque], host_origin: Pointer[size_t, Read, Opaque], region: Pointer[size_t, Read, Opaque], buffer_row_pitch: size_t, buffer_slice_pitch: size_t, host_row_pitch: size_t, host_slice_pitch: size_t, ptr: Pointer[CVoid, Read, Opaque], num_events_in_wait_list: cl_uint, event_wait_list: Pointer[cl_event, Read, Opaque], event: Pointer[cl_event, ReadWrite, Opaque]): cl_int = unsupported
  @CSignature("cl_int (cl_command_queue, cl_program, const char *, cl_bool, const void *, size_t, cl_uint, const cl_event *, cl_event *)")
  def clEnqueueWriteHostPipeINTEL(command_queue: cl_command_queue, program: cl_program, pipe_symbol: CString, blocking_write: cl_bool, ptr: Pointer[CVoid, Read, Opaque], size: size_t, num_events_in_wait_list: cl_uint, event_wait_list: Pointer[cl_event, Read, Opaque], event: Pointer[cl_event, ReadWrite, Opaque]): cl_int = unsupported
  @CSignature("cl_int (cl_command_queue, cl_mem, cl_bool, const size_t *, const size_t *, size_t, size_t, const void *, cl_uint, const cl_event *, cl_event *)")
  def clEnqueueWriteImage(command_queue: cl_command_queue, image: cl_mem, blocking_write: cl_bool, origin: Pointer[size_t, Read, Opaque], region: Pointer[size_t, Read, Opaque], input_row_pitch: size_t, input_slice_pitch: size_t, ptr: Pointer[CVoid, Read, Opaque], num_events_in_wait_list: cl_uint, event_wait_list: Pointer[cl_event, Read, Opaque], event: Pointer[cl_event, ReadWrite, Opaque]): cl_int = unsupported
  @CSignature("cl_int (cl_command_queue)")
  def clFinish(command_queue: cl_command_queue): cl_int = unsupported
  @CSignature("cl_int (cl_command_queue)")
  def clFlush(command_queue: cl_command_queue): cl_int = unsupported
  @CSignature("cl_int (cl_accelerator_intel, cl_accelerator_info_intel, size_t, void *, size_t *)")
  def clGetAcceleratorInfoINTEL(accelerator: cl_accelerator_intel, param_name: cl_accelerator_info_intel, param_value_size: size_t, param_value: Pointer[CVoid, ReadWrite, Opaque], param_value_size_ret: Pointer[size_t, ReadWrite, Opaque]): cl_int = unsupported
  @CSignature("cl_int (cl_command_queue, cl_command_queue_info, size_t, void *, size_t *)")
  def clGetCommandQueueInfo(command_queue: cl_command_queue, param_name: cl_command_queue_info, param_value_size: size_t, param_value: Pointer[CVoid, ReadWrite, Opaque], param_value_size_ret: Pointer[size_t, ReadWrite, Opaque]): cl_int = unsupported
  @CSignature("cl_int (cl_context, cl_context_info, size_t, void *, size_t *)")
  def clGetContextInfo(context: cl_context, param_name: cl_context_info, param_value_size: size_t, param_value: Pointer[CVoid, ReadWrite, Opaque], param_value_size_ret: Pointer[size_t, ReadWrite, Opaque]): cl_int = unsupported
  @CSignature("cl_int (cl_device_id, cl_ulong *, cl_ulong *)")
  def clGetDeviceAndHostTimer(device: cl_device_id, device_timestamp: Pointer[cl_ulong, ReadWrite, Opaque], host_timestamp: Pointer[cl_ulong, ReadWrite, Opaque]): cl_int = unsupported
  @CSignature("cl_int (cl_platform_id, cl_device_type, cl_uint, cl_device_id *, cl_uint *)")
  def clGetDeviceIDs(platform: cl_platform_id, device_type: cl_device_type, num_entries: cl_uint, devices: Pointer[cl_device_id, ReadWrite, Opaque], num_devices: Pointer[cl_uint, ReadWrite, Opaque]): cl_int = unsupported
  @CSignature("cl_int (cl_device_id, size_t, size_t, const cl_image_format *, cl_image_pitch_info_qcom, size_t, void *, size_t *)")
  def clGetDeviceImageInfoQCOM(device: cl_device_id, image_width: size_t, image_height: size_t, image_format: Pointer[cl_image_format, Read, Opaque], param_name: cl_image_pitch_info_qcom, param_value_size: size_t, param_value: Pointer[CVoid, ReadWrite, Opaque], param_value_size_ret: Pointer[size_t, ReadWrite, Opaque]): cl_int = unsupported
  @CSignature("cl_int (cl_device_id, cl_device_info, size_t, void *, size_t *)")
  def clGetDeviceInfo(device: cl_device_id, param_name: cl_device_info, param_value_size: size_t, param_value: Pointer[CVoid, ReadWrite, Opaque], param_value_size_ret: Pointer[size_t, ReadWrite, Opaque]): cl_int = unsupported
  @CSignature("cl_int (cl_event, cl_event_info, size_t, void *, size_t *)")
  def clGetEventInfo(event: cl_event, param_name: cl_event_info, param_value_size: size_t, param_value: Pointer[CVoid, ReadWrite, Opaque], param_value_size_ret: Pointer[size_t, ReadWrite, Opaque]): cl_int = unsupported
  @CSignature("cl_int (cl_event, cl_profiling_info, size_t, void *, size_t *)")
  def clGetEventProfilingInfo(event: cl_event, param_name: cl_profiling_info, param_value_size: size_t, param_value: Pointer[CVoid, ReadWrite, Opaque], param_value_size_ret: Pointer[size_t, ReadWrite, Opaque]): cl_int = unsupported
  @CSignature("void *(const char *)")
  def clGetExtensionFunctionAddress(func_name: CString): Pointer[CVoid, ReadWrite, Opaque] = unsupported
  @CSignature("void *(cl_platform_id, const char *)")
  def clGetExtensionFunctionAddressForPlatform(platform: cl_platform_id, func_name: CString): Pointer[CVoid, ReadWrite, Opaque] = unsupported
  @CSignature("cl_int (const cl_context_properties *, cl_gl_context_info, size_t, void *, size_t *)")
  def clGetGLContextInfoKHR(properties: Pointer[cl_context_properties, Read, Opaque], param_name: cl_gl_context_info, param_value_size: size_t, param_value: Pointer[CVoid, ReadWrite, Opaque], param_value_size_ret: Pointer[size_t, ReadWrite, Opaque]): cl_int = unsupported
  @CSignature("cl_int (cl_mem, cl_gl_object_type *, cl_GLuint *)")
  def clGetGLObjectInfo(memobj: cl_mem, gl_object_type: Pointer[cl_gl_object_type, ReadWrite, Opaque], gl_object_name: Pointer[cl_GLuint, ReadWrite, Opaque]): cl_int = unsupported
  @CSignature("cl_int (cl_mem, cl_gl_texture_info, size_t, void *, size_t *)")
  def clGetGLTextureInfo(memobj: cl_mem, param_name: cl_gl_texture_info, param_value_size: size_t, param_value: Pointer[CVoid, ReadWrite, Opaque], param_value_size_ret: Pointer[size_t, ReadWrite, Opaque]): cl_int = unsupported
  @CSignature("cl_int (cl_device_id, cl_ulong *)")
  def clGetHostTimer(device: cl_device_id, host_timestamp: Pointer[cl_ulong, ReadWrite, Opaque]): cl_int = unsupported
  @CSignature("cl_int (cl_icdl_info, size_t, void *, size_t *)")
  def clGetICDLoaderInfoOCLICD(param_name: cl_icdl_info, param_value_size: size_t, param_value: Pointer[CVoid, ReadWrite, Opaque], param_value_size_ret: Pointer[size_t, ReadWrite, Opaque]): cl_int = unsupported
  @CSignature("cl_int (cl_mem, cl_image_info, size_t, void *, size_t *)")
  def clGetImageInfo(image: cl_mem, param_name: cl_image_info, param_value_size: size_t, param_value: Pointer[CVoid, ReadWrite, Opaque], param_value_size_ret: Pointer[size_t, ReadWrite, Opaque]): cl_int = unsupported
  @CSignature("cl_int (cl_context, const cl_mem_properties *, cl_mem_flags, const cl_image_format *, const cl_image_desc *, cl_image_requirements_info_ext, size_t, void *, size_t *)")
  def clGetImageRequirementsInfoEXT(context: cl_context, properties: Pointer[cl_mem_properties, Read, Opaque], flags: cl_mem_flags, image_format: Pointer[cl_image_format, Read, Opaque], image_desc: Pointer[cl_image_desc, Read, Opaque], param_name: cl_image_requirements_info_ext, param_value_size: size_t, param_value: Pointer[CVoid, ReadWrite, Opaque], param_value_size_ret: Pointer[size_t, ReadWrite, Opaque]): cl_int = unsupported
  @CSignature("cl_int (cl_kernel, cl_uint, cl_kernel_arg_info, size_t, void *, size_t *)")
  def clGetKernelArgInfo(kernel: cl_kernel, arg_indx: cl_uint, param_name: cl_kernel_arg_info, param_value_size: size_t, param_value: Pointer[CVoid, ReadWrite, Opaque], param_value_size_ret: Pointer[size_t, ReadWrite, Opaque]): cl_int = unsupported
  @CSignature("cl_int (cl_kernel, cl_kernel_info, size_t, void *, size_t *)")
  def clGetKernelInfo(kernel: cl_kernel, param_name: cl_kernel_info, param_value_size: size_t, param_value: Pointer[CVoid, ReadWrite, Opaque], param_value_size_ret: Pointer[size_t, ReadWrite, Opaque]): cl_int = unsupported
  @CSignature("cl_int (cl_kernel, cl_device_id, cl_kernel_sub_group_info, size_t, const void *, size_t, void *, size_t *)")
  def clGetKernelSubGroupInfo(kernel: cl_kernel, device: cl_device_id, param_name: cl_kernel_sub_group_info, input_value_size: size_t, input_value: Pointer[CVoid, Read, Opaque], param_value_size: size_t, param_value: Pointer[CVoid, ReadWrite, Opaque], param_value_size_ret: Pointer[size_t, ReadWrite, Opaque]): cl_int = unsupported
  @CSignature("cl_int (cl_kernel, cl_device_id, cl_kernel_sub_group_info, size_t, const void *, size_t, void *, size_t *)")
  def clGetKernelSubGroupInfoKHR(in_kernel: cl_kernel, in_device: cl_device_id, param_name: cl_kernel_sub_group_info, input_value_size: size_t, input_value: Pointer[CVoid, Read, Opaque], param_value_size: size_t, param_value: Pointer[CVoid, ReadWrite, Opaque], param_value_size_ret: Pointer[size_t, ReadWrite, Opaque]): cl_int = unsupported
  @CSignature("cl_int (cl_command_queue, cl_kernel, cl_uint, const size_t *, const size_t *, size_t *)")
  def clGetKernelSuggestedLocalWorkSizeKHR(command_queue: cl_command_queue, kernel: cl_kernel, work_dim: cl_uint, global_work_offset: Pointer[size_t, Read, Opaque], global_work_size: Pointer[size_t, Read, Opaque], suggested_local_work_size: Pointer[size_t, ReadWrite, Opaque]): cl_int = unsupported
  @CSignature("cl_int (cl_kernel, cl_device_id, cl_kernel_work_group_info, size_t, void *, size_t *)")
  def clGetKernelWorkGroupInfo(kernel: cl_kernel, device: cl_device_id, param_name: cl_kernel_work_group_info, param_value_size: size_t, param_value: Pointer[CVoid, ReadWrite, Opaque], param_value_size_ret: Pointer[size_t, ReadWrite, Opaque]): cl_int = unsupported
  @CSignature("cl_int (cl_context, const void *, cl_mem_info_intel, size_t, void *, size_t *)")
  def clGetMemAllocInfoINTEL(context: cl_context, ptr: Pointer[CVoid, Read, Opaque], param_name: cl_mem_info_intel, param_value_size: size_t, param_value: Pointer[CVoid, ReadWrite, Opaque], param_value_size_ret: Pointer[size_t, ReadWrite, Opaque]): cl_int = unsupported
  @CSignature("cl_int (cl_mem, cl_mem_info, size_t, void *, size_t *)")
  def clGetMemObjectInfo(memobj: cl_mem, param_name: cl_mem_info, param_value_size: size_t, param_value: Pointer[CVoid, ReadWrite, Opaque], param_value_size_ret: Pointer[size_t, ReadWrite, Opaque]): cl_int = unsupported
  @CSignature("cl_int (cl_mem, cl_pipe_info, size_t, void *, size_t *)")
  def clGetPipeInfo(pipe: cl_mem, param_name: cl_pipe_info, param_value_size: size_t, param_value: Pointer[CVoid, ReadWrite, Opaque], param_value_size_ret: Pointer[size_t, ReadWrite, Opaque]): cl_int = unsupported
  @CSignature("cl_int (cl_uint, cl_platform_id *, cl_uint *)")
  def clGetPlatformIDs(num_entries: cl_uint, platforms: Pointer[cl_platform_id, ReadWrite, Opaque], num_platforms: Pointer[cl_uint, ReadWrite, Opaque]): cl_int = unsupported
  @CSignature("cl_int (cl_platform_id, cl_platform_info, size_t, void *, size_t *)")
  def clGetPlatformInfo(platform: cl_platform_id, param_name: cl_platform_info, param_value_size: size_t, param_value: Pointer[CVoid, ReadWrite, Opaque], param_value_size_ret: Pointer[size_t, ReadWrite, Opaque]): cl_int = unsupported
  @CSignature("cl_int (cl_program, cl_device_id, cl_program_build_info, size_t, void *, size_t *)")
  def clGetProgramBuildInfo(program: cl_program, device: cl_device_id, param_name: cl_program_build_info, param_value_size: size_t, param_value: Pointer[CVoid, ReadWrite, Opaque], param_value_size_ret: Pointer[size_t, ReadWrite, Opaque]): cl_int = unsupported
  @CSignature("cl_int (cl_program, cl_program_info, size_t, void *, size_t *)")
  def clGetProgramInfo(program: cl_program, param_name: cl_program_info, param_value_size: size_t, param_value: Pointer[CVoid, ReadWrite, Opaque], param_value_size_ret: Pointer[size_t, ReadWrite, Opaque]): cl_int = unsupported
  @CSignature("cl_int (cl_sampler, cl_sampler_info, size_t, void *, size_t *)")
  def clGetSamplerInfo(sampler: cl_sampler, param_name: cl_sampler_info, param_value_size: size_t, param_value: Pointer[CVoid, ReadWrite, Opaque], param_value_size_ret: Pointer[size_t, ReadWrite, Opaque]): cl_int = unsupported
  @CSignature("cl_int (cl_semaphore_khr, cl_device_id, cl_external_semaphore_handle_type_khr, size_t, void *, size_t *)")
  def clGetSemaphoreHandleForTypeKHR(sema_object: cl_semaphore_khr, device: cl_device_id, handle_type: cl_external_semaphore_handle_type_khr, handle_size: size_t, handle_ptr: Pointer[CVoid, ReadWrite, Opaque], handle_size_ret: Pointer[size_t, ReadWrite, Opaque]): cl_int = unsupported
  @CSignature("cl_int (cl_semaphore_khr, cl_semaphore_info_khr, size_t, void *, size_t *)")
  def clGetSemaphoreInfoKHR(sema_object: cl_semaphore_khr, param_name: cl_semaphore_info_khr, param_value_size: size_t, param_value: Pointer[CVoid, ReadWrite, Opaque], param_value_size_ret: Pointer[size_t, ReadWrite, Opaque]): cl_int = unsupported
  @CSignature("cl_int (cl_context, cl_mem_flags, cl_mem_object_type, cl_uint, cl_GLenum *, cl_uint *)")
  def clGetSupportedGLTextureFormatsINTEL(context: cl_context, flags: cl_mem_flags, image_type: cl_mem_object_type, num_entries: cl_uint, gl_formats: Pointer[cl_GLenum, ReadWrite, Opaque], num_texture_formats: Pointer[cl_uint, ReadWrite, Opaque]): cl_int = unsupported
  @CSignature("cl_int (cl_context, cl_mem_flags, cl_mem_object_type, cl_uint, cl_image_format *, cl_uint *)")
  def clGetSupportedImageFormats(context: cl_context, flags: cl_mem_flags, image_type: cl_mem_object_type, num_entries: cl_uint, image_formats: Pointer[cl_image_format, ReadWrite, Opaque], num_image_formats: Pointer[cl_uint, ReadWrite, Opaque]): cl_int = unsupported
  @CSignature("void *(cl_context, const cl_mem_properties_intel *, size_t, cl_uint, cl_int *)")
  def clHostMemAllocINTEL(context: cl_context, properties: Pointer[cl_mem_properties_intel, Read, Opaque], size: size_t, alignment: cl_uint, errcode_ret: Pointer[cl_int, ReadWrite, Opaque]): Pointer[CVoid, ReadWrite, Opaque] = unsupported
  @CSignature("void *(cl_platform_id, const char *)")
  def clIcdGetFunctionAddressForPlatformKHR(platform: cl_platform_id, func_name: CString): Pointer[CVoid, ReadWrite, Opaque] = unsupported
  @CSignature("cl_int (cl_uint, cl_platform_id *, cl_uint *)")
  def clIcdGetPlatformIDsKHR(num_entries: cl_uint, platforms: Pointer[cl_platform_id, ReadWrite, Opaque], num_platforms: Pointer[cl_uint, ReadWrite, Opaque]): cl_int = unsupported
  @CSignature("cl_int (cl_platform_id, void *)")
  def clIcdSetPlatformDispatchDataKHR(platform: cl_platform_id, dispatch_data: Pointer[CVoid, ReadWrite, Opaque]): cl_int = unsupported
  @CSignature("cl_mem (cl_context, cl_mem_flags, const cl_import_properties_arm *, void *, size_t, cl_int *)")
  def clImportMemoryARM(context: cl_context, flags: cl_mem_flags, properties: Pointer[cl_import_properties_arm, Read, Opaque], memory: Pointer[CVoid, ReadWrite, Opaque], size: size_t, errcode_ret: Pointer[cl_int, ReadWrite, Opaque]): cl_mem = unsupported
  @CSignature("cl_program (cl_context, cl_uint, const cl_device_id *, const char *, cl_uint, const cl_program *, void (*)(cl_program, void *), void *, cl_int *)")
  def clLinkProgram(context: cl_context, num_devices: cl_uint, device_list: Pointer[cl_device_id, Read, Opaque], options: CString, num_input_programs: cl_uint, input_programs: Pointer[cl_program, Read, Opaque], pfn_notify: Callback["void (*)(cl_program, void *)"], user_data: Pointer[CVoid, ReadWrite, Opaque], errcode_ret: Pointer[cl_int, ReadWrite, Opaque]): cl_program = unsupported
  @CSignature("void (const char *, const void *, size_t, void *)")
  def clLogMessagesToStderrAPPLE(errstr: CString, private_info: Pointer[CVoid, Read, Opaque], cb: size_t, user_data: Pointer[CVoid, ReadWrite, Opaque]): Unit = unsupported
  @CSignature("void (const char *, const void *, size_t, void *)")
  def clLogMessagesToStdoutAPPLE(errstr: CString, private_info: Pointer[CVoid, Read, Opaque], cb: size_t, user_data: Pointer[CVoid, ReadWrite, Opaque]): Unit = unsupported
  @CSignature("void (const char *, const void *, size_t, void *)")
  def clLogMessagesToSystemLogAPPLE(errstr: CString, private_info: Pointer[CVoid, Read, Opaque], cb: size_t, user_data: Pointer[CVoid, ReadWrite, Opaque]): Unit = unsupported
  @CSignature("cl_int (cl_context, void *)")
  def clMemBlockingFreeINTEL(context: cl_context, ptr: Pointer[CVoid, ReadWrite, Opaque]): cl_int = unsupported
  @CSignature("cl_int (cl_context, void *)")
  def clMemFreeINTEL(context: cl_context, ptr: Pointer[CVoid, ReadWrite, Opaque]): cl_int = unsupported
  @CSignature("cl_int (cl_semaphore_khr, cl_semaphore_reimport_properties_khr *, int)")
  def clReImportSemaphoreSyncFdKHR(sema_object: cl_semaphore_khr, reimport_props: Pointer[cl_semaphore_reimport_properties_khr, ReadWrite, Opaque], fd: Int): cl_int = unsupported
  @CSignature("cl_int (cl_accelerator_intel)")
  def clReleaseAcceleratorINTEL(accelerator: cl_accelerator_intel): cl_int = unsupported
  @CSignature("cl_int (cl_command_queue)")
  def clReleaseCommandQueue(command_queue: cl_command_queue): cl_int = unsupported
  @CSignature("cl_int (cl_context)")
  def clReleaseContext(context: cl_context): cl_int = unsupported
  @CSignature("cl_int (cl_device_id)")
  def clReleaseDevice(device: cl_device_id): cl_int = unsupported
  @CSignature("cl_int (cl_device_id)")
  def clReleaseDeviceEXT(device: cl_device_id): cl_int = unsupported
  @CSignature("cl_int (cl_event)")
  def clReleaseEvent(event: cl_event): cl_int = unsupported
  @CSignature("cl_int (cl_kernel)")
  def clReleaseKernel(kernel: cl_kernel): cl_int = unsupported
  @CSignature("cl_int (cl_mem)")
  def clReleaseMemObject(memobj: cl_mem): cl_int = unsupported
  @CSignature("cl_int (cl_program)")
  def clReleaseProgram(program: cl_program): cl_int = unsupported
  @CSignature("cl_int (cl_sampler)")
  def clReleaseSampler(sampler: cl_sampler): cl_int = unsupported
  @CSignature("cl_int (cl_semaphore_khr)")
  def clReleaseSemaphoreKHR(sema_object: cl_semaphore_khr): cl_int = unsupported
  @CSignature("cl_int (cl_accelerator_intel)")
  def clRetainAcceleratorINTEL(accelerator: cl_accelerator_intel): cl_int = unsupported
  @CSignature("cl_int (cl_command_queue)")
  def clRetainCommandQueue(command_queue: cl_command_queue): cl_int = unsupported
  @CSignature("cl_int (cl_context)")
  def clRetainContext(context: cl_context): cl_int = unsupported
  @CSignature("cl_int (cl_device_id)")
  def clRetainDevice(device: cl_device_id): cl_int = unsupported
  @CSignature("cl_int (cl_device_id)")
  def clRetainDeviceEXT(device: cl_device_id): cl_int = unsupported
  @CSignature("cl_int (cl_event)")
  def clRetainEvent(event: cl_event): cl_int = unsupported
  @CSignature("cl_int (cl_kernel)")
  def clRetainKernel(kernel: cl_kernel): cl_int = unsupported
  @CSignature("cl_int (cl_mem)")
  def clRetainMemObject(memobj: cl_mem): cl_int = unsupported
  @CSignature("cl_int (cl_program)")
  def clRetainProgram(program: cl_program): cl_int = unsupported
  @CSignature("cl_int (cl_sampler)")
  def clRetainSampler(sampler: cl_sampler): cl_int = unsupported
  @CSignature("cl_int (cl_semaphore_khr)")
  def clRetainSemaphoreKHR(sema_object: cl_semaphore_khr): cl_int = unsupported
  @CSignature("void *(cl_context, cl_svm_mem_flags, size_t, cl_uint)")
  def clSVMAlloc(context: cl_context, flags: cl_svm_mem_flags, size: size_t, alignment: cl_uint): Pointer[CVoid, ReadWrite, Opaque] = unsupported
  @CSignature("void *(cl_context, cl_svm_mem_flags_arm, size_t, cl_uint)")
  def clSVMAllocARM(context: cl_context, flags: cl_svm_mem_flags_arm, size: size_t, alignment: cl_uint): Pointer[CVoid, ReadWrite, Opaque] = unsupported
  @CSignature("void (cl_context, void *)")
  def clSVMFree(context: cl_context, svm_pointer: Pointer[CVoid, ReadWrite, Opaque]): Unit = unsupported
  @CSignature("void (cl_context, void *)")
  def clSVMFreeARM(context: cl_context, svm_pointer: Pointer[CVoid, ReadWrite, Opaque]): Unit = unsupported
  @CSignature("cl_int (cl_mem, cl_mem)")
  def clSetContentSizeBufferPoCL(buffer: cl_mem, content_size_buffer: cl_mem): cl_int = unsupported
  @CSignature("cl_int (cl_context, void (*)(cl_context, void *), void *)")
  def clSetContextDestructorCallback(context: cl_context, pfn_notify: Callback["void (*)(cl_context, void *)"], user_data: Pointer[CVoid, ReadWrite, Opaque]): cl_int = unsupported
  @CSignature("cl_int (cl_context, cl_device_id, cl_command_queue)")
  def clSetDefaultDeviceCommandQueue(context: cl_context, device: cl_device_id, command_queue: cl_command_queue): cl_int = unsupported
  @CSignature("cl_int (cl_event, cl_int, void (*)(cl_event, cl_int, void *), void *)")
  def clSetEventCallback(event: cl_event, command_exec_callback_type: cl_int, pfn_notify: Callback["void (*)(cl_event, cl_int, void *)"], user_data: Pointer[CVoid, ReadWrite, Opaque]): cl_int = unsupported
  @CSignature("cl_int (cl_kernel, cl_uint, size_t, const void *)")
  def clSetKernelArg(kernel: cl_kernel, arg_index: cl_uint, arg_size: size_t, arg_value: Pointer[CVoid, Read, Opaque]): cl_int = unsupported
  @CSignature("cl_int (cl_kernel, cl_uint, cl_mem_device_address_ext)")
  def clSetKernelArgDevicePointerEXT(kernel: cl_kernel, arg_index: cl_uint, arg_value: cl_mem_device_address_ext): cl_int = unsupported
  @CSignature("cl_int (cl_kernel, cl_uint, const void *)")
  def clSetKernelArgMemPointerINTEL(kernel: cl_kernel, arg_index: cl_uint, arg_value: Pointer[CVoid, Read, Opaque]): cl_int = unsupported
  @CSignature("cl_int (cl_kernel, cl_uint, const void *)")
  def clSetKernelArgSVMPointer(kernel: cl_kernel, arg_index: cl_uint, arg_value: Pointer[CVoid, Read, Opaque]): cl_int = unsupported
  @CSignature("cl_int (cl_kernel, cl_uint, const void *)")
  def clSetKernelArgSVMPointerARM(kernel: cl_kernel, arg_index: cl_uint, arg_value: Pointer[CVoid, Read, Opaque]): cl_int = unsupported
  @CSignature("cl_int (cl_kernel, cl_kernel_exec_info, size_t, const void *)")
  def clSetKernelExecInfo(kernel: cl_kernel, param_name: cl_kernel_exec_info, param_value_size: size_t, param_value: Pointer[CVoid, Read, Opaque]): cl_int = unsupported
  @CSignature("cl_int (cl_kernel, cl_kernel_exec_info_arm, size_t, const void *)")
  def clSetKernelExecInfoARM(kernel: cl_kernel, param_name: cl_kernel_exec_info_arm, param_value_size: size_t, param_value: Pointer[CVoid, Read, Opaque]): cl_int = unsupported
  @CSignature("cl_int (cl_mem, void (*)(cl_mem, void *), void *)")
  def clSetMemObjectDestructorAPPLE(memobj: cl_mem, pfn_notify: Callback["void (*)(cl_mem, void *)"], user_data: Pointer[CVoid, ReadWrite, Opaque]): cl_int = unsupported
  @CSignature("cl_int (cl_mem, void (*)(cl_mem, void *), void *)")
  def clSetMemObjectDestructorCallback(memobj: cl_mem, pfn_notify: Callback["void (*)(cl_mem, void *)"], user_data: Pointer[CVoid, ReadWrite, Opaque]): cl_int = unsupported
  @CSignature("cl_int (cl_context, cl_perf_hint_qcom)")
  def clSetPerfHintQCOM(context: cl_context, perf_hint: cl_perf_hint_qcom): cl_int = unsupported
  @CSignature("cl_int (cl_program, void (*)(cl_program, void *), void *)")
  def clSetProgramReleaseCallback(program: cl_program, pfn_notify: Callback["void (*)(cl_program, void *)"], user_data: Pointer[CVoid, ReadWrite, Opaque]): cl_int = unsupported
  @CSignature("cl_int (cl_program, cl_uint, size_t, const void *)")
  def clSetProgramSpecializationConstant(program: cl_program, spec_id: cl_uint, spec_size: size_t, spec_value: Pointer[CVoid, Read, Opaque]): cl_int = unsupported
  @CSignature("cl_int (cl_event, cl_int)")
  def clSetUserEventStatus(event: cl_event, execution_status: cl_int): cl_int = unsupported
  @CSignature("void *(cl_context, cl_device_id, const cl_mem_properties_intel *, size_t, cl_uint, cl_int *)")
  def clSharedMemAllocINTEL(context: cl_context, device: cl_device_id, properties: Pointer[cl_mem_properties_intel, Read, Opaque], size: size_t, alignment: cl_uint, errcode_ret: Pointer[cl_int, ReadWrite, Opaque]): Pointer[CVoid, ReadWrite, Opaque] = unsupported
  @CSignature("cl_int (cl_context)")
  def clTerminateContextKHR(context: cl_context): cl_int = unsupported
  @CSignature("cl_int (void)")
  def clUnloadCompiler(): cl_int = unsupported
  @CSignature("cl_int (cl_platform_id)")
  def clUnloadPlatformCompiler(platform: cl_platform_id): cl_int = unsupported
  @CSignature("cl_int (cl_uint, const cl_event *)")
  def clWaitForEvents(num_events: cl_uint, event_list: Pointer[cl_event, Read, Opaque]): cl_int = unsupported
}

object OpenCL {
  val HeaderVersion: String = "2026.05.29"
  val SurfaceSize: Int = 190
}
