// Generated from pinned official headers by GenerateAPISurfaces. Regenerate with: sbt codegen
package net.kurobako.gemstone.hsa

import net.kurobako.gemstone.Constraint.Positive
import net.kurobako.gemstone.Constraint.NonEmptyString
import net.kurobako.gemstone.abi.ABI.*

@javax.annotation.processing.Generated(Array("sbt codegen"))
object HSA {
  type hsa_access_permission_t = CValue["hsa_access_permission_t"]
  type hsa_agent_info_t = CValue["hsa_agent_info_t"]
  type hsa_agent_t = CValue["hsa_agent_t"]
  type hsa_amd_agent_memory_pool_info_t = CValue["hsa_amd_agent_memory_pool_info_t"]
  type hsa_amd_ais_file_handle_t = CValue["hsa_amd_ais_file_handle_t"]
  type hsa_amd_coherency_type_t = CValue["hsa_amd_coherency_type_t"]
  type hsa_amd_copy_direction_t = CValue["hsa_amd_copy_direction_t"]
  type hsa_amd_deallocation_callback_t =
    CallbackTypedef["hsa_amd_deallocation_callback_t", (OpaquePointer, OpaquePointer), Unit]
  type hsa_amd_event_t = CValue["hsa_amd_event_t"]
  type hsa_amd_image_descriptor_t = CValue["hsa_amd_image_descriptor_t"]
  type hsa_amd_ipc_memory_t = CValue["hsa_amd_ipc_memory_t"]
  type hsa_amd_ipc_signal_t = CValue["hsa_amd_ipc_signal_t"]
  type hsa_amd_memory_access_desc_t = CValue["hsa_amd_memory_access_desc_t"]
  type hsa_amd_memory_pool_info_t = CValue["hsa_amd_memory_pool_info_t"]
  type hsa_amd_memory_pool_t = CValue["hsa_amd_memory_pool_t"]
  type hsa_amd_memory_type_t = CValue["hsa_amd_memory_type_t"]
  type hsa_amd_pointer_info_t = CValue["hsa_amd_pointer_info_t"]
  type hsa_amd_profiling_async_copy_time_t = CValue["hsa_amd_profiling_async_copy_time_t"]
  type hsa_amd_profiling_dispatch_time_t = CValue["hsa_amd_profiling_dispatch_time_t"]
  type hsa_amd_queue_priority_t = CValue["hsa_amd_queue_priority_t"]
  type hsa_amd_sdma_engine_id_t = CValue["hsa_amd_sdma_engine_id_t"]
  type hsa_amd_signal_handler = CallbackTypedef["hsa_amd_signal_handler", (hsa_signal_value_t, OpaquePointer), Boolean]
  type hsa_amd_svm_attribute_pair_t = CValue["hsa_amd_svm_attribute_pair_t"]
  type hsa_amd_system_event_callback_t = CallbackTypedef[
    "hsa_amd_system_event_callback_t",
    (Pointer[hsa_amd_event_t, Read, Opaque], OpaquePointer),
    hsa_status_t
  ]
  type hsa_amd_vmem_alloc_handle_t = CValue["hsa_amd_vmem_alloc_handle_t"]
  type hsa_cache_info_t = CValue["hsa_cache_info_t"]
  type hsa_cache_t = CValue["hsa_cache_t"]
  type hsa_callback_data_t = CValue["hsa_callback_data_t"]
  type hsa_code_object_info_t = CValue["hsa_code_object_info_t"]
  type hsa_code_object_reader_t = CValue["hsa_code_object_reader_t"]
  type hsa_code_object_t = CValue["hsa_code_object_t"]
  type hsa_code_symbol_info_t = CValue["hsa_code_symbol_info_t"]
  type hsa_code_symbol_t = CValue["hsa_code_symbol_t"]
  type hsa_default_float_rounding_mode_t = CValue["hsa_default_float_rounding_mode_t"]
  type hsa_dim3_t = CValue["hsa_dim3_t"]
  type hsa_executable_info_t = CValue["hsa_executable_info_t"]
  type hsa_executable_state_t = CValue["hsa_executable_state_t"]
  type hsa_executable_symbol_info_t = CValue["hsa_executable_symbol_info_t"]
  type hsa_executable_symbol_t = CValue["hsa_executable_symbol_t"]
  type hsa_executable_t = CValue["hsa_executable_t"]
  type hsa_ext_image_data_info_t = CValue["hsa_ext_image_data_info_t"]
  type hsa_ext_image_data_layout_t = CValue["hsa_ext_image_data_layout_t"]
  type hsa_ext_image_descriptor_t = CValue["hsa_ext_image_descriptor_t"]
  type hsa_ext_image_format_t = CValue["hsa_ext_image_format_t"]
  type hsa_ext_image_geometry_t = CValue["hsa_ext_image_geometry_t"]
  type hsa_ext_image_region_t = CValue["hsa_ext_image_region_t"]
  type hsa_ext_image_t = CValue["hsa_ext_image_t"]
  type hsa_ext_sampler_descriptor_t = CValue["hsa_ext_sampler_descriptor_t"]
  type hsa_ext_sampler_descriptor_v2_t = CValue["hsa_ext_sampler_descriptor_v2_t"]
  type hsa_ext_sampler_t = CValue["hsa_ext_sampler_t"]
  type hsa_file_t = CValue["hsa_file_t"]
  type hsa_flush_mode_t = CValue["hsa_flush_mode_t"]
  type hsa_fp_type_t = CValue["hsa_fp_type_t"]
  type hsa_isa_info_t = CValue["hsa_isa_info_t"]
  type hsa_isa_t = CValue["hsa_isa_t"]
  type hsa_loaded_code_object_t = CValue["hsa_loaded_code_object_t"]
  type hsa_pitched_ptr_t = CValue["hsa_pitched_ptr_t"]
  type hsa_profile_t = CValue["hsa_profile_t"]
  type hsa_queue_info_attribute_t = CValue["hsa_queue_info_attribute_t"]
  type hsa_queue_t = CValue["hsa_queue_t"]
  type hsa_queue_type32_t = CValue["hsa_queue_type32_t"]
  type hsa_region_info_t = CValue["hsa_region_info_t"]
  type hsa_region_t = CValue["hsa_region_t"]
  type hsa_round_method_t = CValue["hsa_round_method_t"]
  type hsa_signal_condition_t = CValue["hsa_signal_condition_t"]
  type hsa_signal_group_t = CValue["hsa_signal_group_t"]
  type hsa_signal_t = CValue["hsa_signal_t"]
  type hsa_signal_value_t = CValue["hsa_signal_value_t"]
  type hsa_status_t = CValue["hsa_status_t"]
  type hsa_system_info_t = CValue["hsa_system_info_t"]
  type hsa_ven_amd_pcs_configuration_t = CValue["hsa_ven_amd_pcs_configuration_t"]
  type hsa_ven_amd_pcs_data_copy_callback_t =
    CallbackTypedef["hsa_ven_amd_pcs_data_copy_callback_t", (OpaquePointer, size_t, OpaquePointer), hsa_status_t]
  type hsa_ven_amd_pcs_data_ready_callback_t = CallbackTypedef[
    "hsa_ven_amd_pcs_data_ready_callback_t",
    (OpaquePointer, size_t, size_t, hsa_ven_amd_pcs_data_copy_callback_t, OpaquePointer),
    Unit
  ]
  type hsa_ven_amd_pcs_iterate_configuration_callback_t = CallbackTypedef[
    "hsa_ven_amd_pcs_iterate_configuration_callback_t",
    (Pointer[hsa_ven_amd_pcs_configuration_t, Read, Opaque], OpaquePointer),
    hsa_status_t
  ]
  type hsa_ven_amd_pcs_method_kind_t = CValue["hsa_ven_amd_pcs_method_kind_t"]
  type hsa_ven_amd_pcs_t = CValue["hsa_ven_amd_pcs_t"]
  type hsa_ven_amd_pcs_units_t = CValue["hsa_ven_amd_pcs_units_t"]
  type hsa_wait_state_t = CValue["hsa_wait_state_t"]
  type hsa_wavefront_info_t = CValue["hsa_wavefront_info_t"]
  type hsa_wavefront_t = CValue["hsa_wavefront_t"]
  type int32_t = CValue["int32_t"]
  type int64_t = CValue["int64_t"]
  type size_t = CValue["size_t"]
  type uint16_t = CValue["uint16_t"]
  type uint32_t = CValue["uint32_t"]
  type uint64_t = CValue["uint64_t"]
  type uint8_t = CValue["uint8_t"]
  type volatile_hsa_signal_value_t = CValue["volatile hsa_signal_value_t"]

  val HeaderVersion: NonEmptyString = NonEmptyString("7.1.1")
  val FunctionCount: Positive[Int] = 254
}

import HSA.*

trait HSA {
  def hsa_agent_extension_supported(
      extension: uint16_t,
      agent: hsa_agent_t,
      version_major: uint16_t,
      version_minor: uint16_t,
      result: Pointer[Boolean, ReadWrite, Opaque]
  ): hsa_status_t = unsupported
  def hsa_agent_get_exception_policies(
      agent: hsa_agent_t,
      profile: hsa_profile_t,
      mask: Pointer[uint16_t, ReadWrite, Opaque]
  ): hsa_status_t = unsupported
  def hsa_agent_get_info(agent: hsa_agent_t, attribute: hsa_agent_info_t, value: OpaquePointer): hsa_status_t =
    unsupported
  def hsa_agent_iterate_caches(
      agent: hsa_agent_t,
      callback: Callback[(hsa_cache_t, OpaquePointer), hsa_status_t],
      data: OpaquePointer
  ): hsa_status_t = unsupported
  def hsa_agent_iterate_isas(
      agent: hsa_agent_t,
      callback: Callback[(hsa_isa_t, OpaquePointer), hsa_status_t],
      data: OpaquePointer
  ): hsa_status_t = unsupported
  def hsa_agent_iterate_regions(
      agent: hsa_agent_t,
      callback: Callback[(hsa_region_t, OpaquePointer), hsa_status_t],
      data: OpaquePointer
  ): hsa_status_t = unsupported
  def hsa_agent_major_extension_supported(
      extension: uint16_t,
      agent: hsa_agent_t,
      version_major: uint16_t,
      version_minor: Pointer[uint16_t, ReadWrite, Opaque],
      result: Pointer[Boolean, ReadWrite, Opaque]
  ): hsa_status_t = unsupported
  def hsa_amd_agent_iterate_memory_pools(
      agent: hsa_agent_t,
      callback: Callback[(hsa_amd_memory_pool_t, OpaquePointer), hsa_status_t],
      data: OpaquePointer
  ): hsa_status_t = unsupported
  def hsa_amd_agent_memory_pool_get_info(
      agent: hsa_agent_t,
      memory_pool: hsa_amd_memory_pool_t,
      attribute: hsa_amd_agent_memory_pool_info_t,
      value: OpaquePointer
  ): hsa_status_t = unsupported
  def hsa_amd_agent_set_async_scratch_limit(agent: hsa_agent_t, threshold: size_t): hsa_status_t = unsupported
  def hsa_amd_agents_allow_access(
      num_agents: uint32_t,
      agents: Pointer[hsa_agent_t, Read, Opaque],
      flags: Pointer[uint32_t, Read, Opaque],
      ptr: Pointer[CVoid, Read, Opaque]
  ): hsa_status_t = unsupported
  def hsa_amd_ais_file_read(
      handle: hsa_amd_ais_file_handle_t,
      devicePtr: OpaquePointer,
      size: uint64_t,
      file_offset: int64_t,
      size_copied: Pointer[uint64_t, ReadWrite, Opaque],
      status: Pointer[int32_t, ReadWrite, Opaque]
  ): hsa_status_t = unsupported
  def hsa_amd_ais_file_write(
      handle: hsa_amd_ais_file_handle_t,
      devicePtr: OpaquePointer,
      size: uint64_t,
      file_offset: int64_t,
      size_copied: Pointer[uint64_t, ReadWrite, Opaque],
      status: Pointer[int32_t, ReadWrite, Opaque]
  ): hsa_status_t = unsupported
  def hsa_amd_async_function(callback: Callback[Tuple1[OpaquePointer], Unit], arg: OpaquePointer): hsa_status_t =
    unsupported
  def hsa_amd_coherency_get_type(
      agent: hsa_agent_t,
      `type`: Pointer[hsa_amd_coherency_type_t, ReadWrite, Opaque]
  ): hsa_status_t = unsupported
  def hsa_amd_coherency_set_type(agent: hsa_agent_t, `type`: hsa_amd_coherency_type_t): hsa_status_t = unsupported
  def hsa_amd_deregister_deallocation_callback(
      ptr: OpaquePointer,
      callback: hsa_amd_deallocation_callback_t
  ): hsa_status_t = unsupported
  def hsa_amd_enable_logging(flags: Pointer[uint8_t, ReadWrite, Opaque], file: OpaquePointer): hsa_status_t =
    unsupported
  def hsa_amd_image_create(
      agent: hsa_agent_t,
      image_descriptor: Pointer[hsa_ext_image_descriptor_t, Read, Opaque],
      image_layout: Pointer[hsa_amd_image_descriptor_t, Read, Opaque],
      image_data: Pointer[CVoid, Read, Opaque],
      access_permission: hsa_access_permission_t,
      image: Pointer[hsa_ext_image_t, ReadWrite, Opaque]
  ): hsa_status_t = unsupported
  def hsa_amd_image_get_info_max_dim(
      agent: hsa_agent_t,
      attribute: hsa_agent_info_t,
      value: OpaquePointer
  ): hsa_status_t = unsupported
  def hsa_amd_interop_map_buffer(
      num_agents: uint32_t,
      agents: Pointer[hsa_agent_t, ReadWrite, Opaque],
      interop_handle: Int,
      flags: uint32_t,
      size: Pointer[size_t, ReadWrite, Opaque],
      ptr: Pointer[OpaquePointer, ReadWrite, Opaque],
      metadata_size: Pointer[size_t, ReadWrite, Opaque],
      metadata: Pointer[Pointer[CVoid, Read, Opaque], ReadWrite, Opaque]
  ): hsa_status_t = unsupported
  def hsa_amd_interop_unmap_buffer(ptr: OpaquePointer): hsa_status_t = unsupported
  def hsa_amd_ipc_memory_attach(
      handle: Pointer[hsa_amd_ipc_memory_t, Read, Opaque],
      len: size_t,
      num_agents: uint32_t,
      mapping_agents: Pointer[hsa_agent_t, Read, Opaque],
      mapped_ptr: Pointer[OpaquePointer, ReadWrite, Opaque]
  ): hsa_status_t = unsupported
  def hsa_amd_ipc_memory_create(
      ptr: OpaquePointer,
      len: size_t,
      handle: Pointer[hsa_amd_ipc_memory_t, ReadWrite, Opaque]
  ): hsa_status_t = unsupported
  def hsa_amd_ipc_memory_detach(mapped_ptr: OpaquePointer): hsa_status_t = unsupported
  def hsa_amd_ipc_signal_attach(
      handle: Pointer[hsa_amd_ipc_signal_t, Read, Opaque],
      signal: Pointer[hsa_signal_t, ReadWrite, Opaque]
  ): hsa_status_t = unsupported
  def hsa_amd_ipc_signal_create(
      signal: hsa_signal_t,
      handle: Pointer[hsa_amd_ipc_signal_t, ReadWrite, Opaque]
  ): hsa_status_t = unsupported
  def hsa_amd_memory_async_copy(
      dst: OpaquePointer,
      dst_agent: hsa_agent_t,
      src: Pointer[CVoid, Read, Opaque],
      src_agent: hsa_agent_t,
      size: size_t,
      num_dep_signals: uint32_t,
      dep_signals: Pointer[hsa_signal_t, Read, Opaque],
      completion_signal: hsa_signal_t
  ): hsa_status_t = unsupported
  def hsa_amd_memory_async_copy_on_engine(
      dst: OpaquePointer,
      dst_agent: hsa_agent_t,
      src: Pointer[CVoid, Read, Opaque],
      src_agent: hsa_agent_t,
      size: size_t,
      num_dep_signals: uint32_t,
      dep_signals: Pointer[hsa_signal_t, Read, Opaque],
      completion_signal: hsa_signal_t,
      engine_id: hsa_amd_sdma_engine_id_t,
      force_copy_on_sdma: Boolean
  ): hsa_status_t = unsupported
  def hsa_amd_memory_async_copy_rect(
      dst: Pointer[hsa_pitched_ptr_t, Read, Opaque],
      dst_offset: Pointer[hsa_dim3_t, Read, Opaque],
      src: Pointer[hsa_pitched_ptr_t, Read, Opaque],
      src_offset: Pointer[hsa_dim3_t, Read, Opaque],
      range: Pointer[hsa_dim3_t, Read, Opaque],
      copy_agent: hsa_agent_t,
      dir: hsa_amd_copy_direction_t,
      num_dep_signals: uint32_t,
      dep_signals: Pointer[hsa_signal_t, Read, Opaque],
      completion_signal: hsa_signal_t
  ): hsa_status_t = unsupported
  def hsa_amd_memory_copy_engine_status(
      dst_agent: hsa_agent_t,
      src_agent: hsa_agent_t,
      engine_ids_mask: Pointer[uint32_t, ReadWrite, Opaque]
  ): hsa_status_t = unsupported
  def hsa_amd_memory_fill(ptr: OpaquePointer, value: uint32_t, count: size_t): hsa_status_t = unsupported
  def hsa_amd_memory_get_preferred_copy_engine(
      dst_agent: hsa_agent_t,
      src_agent: hsa_agent_t,
      recommended_ids_mask: Pointer[uint32_t, ReadWrite, Opaque]
  ): hsa_status_t = unsupported
  def hsa_amd_memory_lock(
      host_ptr: OpaquePointer,
      size: size_t,
      agents: Pointer[hsa_agent_t, ReadWrite, Opaque],
      num_agent: Int,
      agent_ptr: Pointer[OpaquePointer, ReadWrite, Opaque]
  ): hsa_status_t = unsupported
  def hsa_amd_memory_lock_to_pool(
      host_ptr: OpaquePointer,
      size: size_t,
      agents: Pointer[hsa_agent_t, ReadWrite, Opaque],
      num_agent: Int,
      pool: hsa_amd_memory_pool_t,
      flags: uint32_t,
      agent_ptr: Pointer[OpaquePointer, ReadWrite, Opaque]
  ): hsa_status_t = unsupported
  def hsa_amd_memory_migrate(
      ptr: Pointer[CVoid, Read, Opaque],
      memory_pool: hsa_amd_memory_pool_t,
      flags: uint32_t
  ): hsa_status_t = unsupported
  def hsa_amd_memory_pool_allocate(
      memory_pool: hsa_amd_memory_pool_t,
      size: size_t,
      flags: uint32_t,
      ptr: Pointer[OpaquePointer, ReadWrite, Opaque]
  ): hsa_status_t = unsupported
  def hsa_amd_memory_pool_can_migrate(
      src_memory_pool: hsa_amd_memory_pool_t,
      dst_memory_pool: hsa_amd_memory_pool_t,
      result: Pointer[Boolean, ReadWrite, Opaque]
  ): hsa_status_t = unsupported
  def hsa_amd_memory_pool_free(ptr: OpaquePointer): hsa_status_t = unsupported
  def hsa_amd_memory_pool_get_info(
      memory_pool: hsa_amd_memory_pool_t,
      attribute: hsa_amd_memory_pool_info_t,
      value: OpaquePointer
  ): hsa_status_t = unsupported
  def hsa_amd_memory_unlock(host_ptr: OpaquePointer): hsa_status_t = unsupported
  def hsa_amd_pointer_info(
      ptr: Pointer[CVoid, Read, Opaque],
      info: Pointer[hsa_amd_pointer_info_t, ReadWrite, Opaque],
      alloc: Callback[Tuple1[size_t], OpaquePointer],
      num_agents_accessible: Pointer[uint32_t, ReadWrite, Opaque],
      accessible: Pointer[Pointer[hsa_agent_t, ReadWrite, Opaque], ReadWrite, Opaque]
  ): hsa_status_t = unsupported
  def hsa_amd_pointer_info_set_userdata(ptr: Pointer[CVoid, Read, Opaque], userdata: OpaquePointer): hsa_status_t =
    unsupported
  def hsa_amd_portable_close_dmabuf(dmabuf: Int): hsa_status_t = unsupported
  def hsa_amd_portable_export_dmabuf(
      ptr: Pointer[CVoid, Read, Opaque],
      size: size_t,
      dmabuf: Pointer[Int, ReadWrite, Opaque],
      offset: Pointer[uint64_t, ReadWrite, Opaque]
  ): hsa_status_t = unsupported
  def hsa_amd_portable_export_dmabuf_v2(
      ptr: Pointer[CVoid, Read, Opaque],
      size: size_t,
      dmabuf: Pointer[Int, ReadWrite, Opaque],
      offset: Pointer[uint64_t, ReadWrite, Opaque],
      flags: uint64_t
  ): hsa_status_t = unsupported
  def hsa_amd_profiling_async_copy_enable(enable: Boolean): hsa_status_t = unsupported
  def hsa_amd_profiling_convert_tick_to_system_domain(
      agent: hsa_agent_t,
      agent_tick: uint64_t,
      system_tick: Pointer[uint64_t, ReadWrite, Opaque]
  ): hsa_status_t = unsupported
  def hsa_amd_profiling_get_async_copy_time(
      signal: hsa_signal_t,
      time: Pointer[hsa_amd_profiling_async_copy_time_t, ReadWrite, Opaque]
  ): hsa_status_t = unsupported
  def hsa_amd_profiling_get_dispatch_time(
      agent: hsa_agent_t,
      signal: hsa_signal_t,
      time: Pointer[hsa_amd_profiling_dispatch_time_t, ReadWrite, Opaque]
  ): hsa_status_t = unsupported
  def hsa_amd_profiling_set_profiler_enabled(
      queue: Pointer[hsa_queue_t, ReadWrite, Opaque],
      enable: Int
  ): hsa_status_t = unsupported
  def hsa_amd_queue_cu_get_mask(
      queue: Pointer[hsa_queue_t, Read, Opaque],
      num_cu_mask_count: uint32_t,
      cu_mask: Pointer[uint32_t, ReadWrite, Opaque]
  ): hsa_status_t = unsupported
  def hsa_amd_queue_cu_set_mask(
      queue: Pointer[hsa_queue_t, Read, Opaque],
      num_cu_mask_count: uint32_t,
      cu_mask: Pointer[uint32_t, Read, Opaque]
  ): hsa_status_t = unsupported
  def hsa_amd_queue_get_info(
      queue: Pointer[hsa_queue_t, ReadWrite, Opaque],
      attribute: hsa_queue_info_attribute_t,
      value: OpaquePointer
  ): hsa_status_t = unsupported
  def hsa_amd_queue_set_priority(
      queue: Pointer[hsa_queue_t, ReadWrite, Opaque],
      priority: hsa_amd_queue_priority_t
  ): hsa_status_t = unsupported
  def hsa_amd_register_deallocation_callback(
      ptr: OpaquePointer,
      callback: hsa_amd_deallocation_callback_t,
      user_data: OpaquePointer
  ): hsa_status_t = unsupported
  def hsa_amd_register_system_event_handler(
      callback: hsa_amd_system_event_callback_t,
      data: OpaquePointer
  ): hsa_status_t = unsupported
  def hsa_amd_signal_async_handler(
      signal: hsa_signal_t,
      cond: hsa_signal_condition_t,
      value: hsa_signal_value_t,
      handler: hsa_amd_signal_handler,
      arg: OpaquePointer
  ): hsa_status_t = unsupported
  def hsa_amd_signal_create(
      initial_value: hsa_signal_value_t,
      num_consumers: uint32_t,
      consumers: Pointer[hsa_agent_t, Read, Opaque],
      attributes: uint64_t,
      signal: Pointer[hsa_signal_t, ReadWrite, Opaque]
  ): hsa_status_t = unsupported
  def hsa_amd_signal_value_pointer(
      signal: hsa_signal_t,
      value_ptr: Pointer[Pointer[volatile_hsa_signal_value_t, ReadWrite, Opaque], ReadWrite, Opaque]
  ): hsa_status_t = unsupported
  def hsa_amd_signal_wait_all(
      signal_count: uint32_t,
      signals: Pointer[hsa_signal_t, ReadWrite, Opaque],
      conds: Pointer[hsa_signal_condition_t, ReadWrite, Opaque],
      values: Pointer[hsa_signal_value_t, ReadWrite, Opaque],
      timeout_hint: uint64_t,
      wait_hint: hsa_wait_state_t,
      satisfying_values: Pointer[hsa_signal_value_t, ReadWrite, Opaque]
  ): uint32_t = unsupported
  def hsa_amd_signal_wait_any(
      signal_count: uint32_t,
      signals: Pointer[hsa_signal_t, ReadWrite, Opaque],
      conds: Pointer[hsa_signal_condition_t, ReadWrite, Opaque],
      values: Pointer[hsa_signal_value_t, ReadWrite, Opaque],
      timeout_hint: uint64_t,
      wait_hint: hsa_wait_state_t,
      satisfying_value: Pointer[hsa_signal_value_t, ReadWrite, Opaque]
  ): uint32_t = unsupported
  def hsa_amd_spm_acquire(preferred_agent: hsa_agent_t): hsa_status_t = unsupported
  def hsa_amd_spm_release(preferred_agent: hsa_agent_t): hsa_status_t = unsupported
  def hsa_amd_spm_set_dest_buffer(
      preferred_agent: hsa_agent_t,
      size_in_bytes: size_t,
      timeout: Pointer[uint32_t, ReadWrite, Opaque],
      size_copied: Pointer[uint32_t, ReadWrite, Opaque],
      dest: OpaquePointer,
      is_data_loss: Pointer[Boolean, ReadWrite, Opaque]
  ): hsa_status_t = unsupported
  def hsa_amd_svm_attributes_get(
      ptr: OpaquePointer,
      size: size_t,
      attribute_list: Pointer[hsa_amd_svm_attribute_pair_t, ReadWrite, Opaque],
      attribute_count: size_t
  ): hsa_status_t = unsupported
  def hsa_amd_svm_attributes_set(
      ptr: OpaquePointer,
      size: size_t,
      attribute_list: Pointer[hsa_amd_svm_attribute_pair_t, ReadWrite, Opaque],
      attribute_count: size_t
  ): hsa_status_t = unsupported
  def hsa_amd_svm_prefetch_async(
      ptr: OpaquePointer,
      size: size_t,
      agent: hsa_agent_t,
      num_dep_signals: uint32_t,
      dep_signals: Pointer[hsa_signal_t, Read, Opaque],
      completion_signal: hsa_signal_t
  ): hsa_status_t = unsupported
  def hsa_amd_vmem_address_free(va: OpaquePointer, size: size_t): hsa_status_t = unsupported
  def hsa_amd_vmem_address_reserve(
      va: Pointer[OpaquePointer, ReadWrite, Opaque],
      size: size_t,
      address: uint64_t,
      flags: uint64_t
  ): hsa_status_t = unsupported
  def hsa_amd_vmem_address_reserve_align(
      va: Pointer[OpaquePointer, ReadWrite, Opaque],
      size: size_t,
      address: uint64_t,
      alignment: uint64_t,
      flags: uint64_t
  ): hsa_status_t = unsupported
  def hsa_amd_vmem_export_shareable_handle(
      dmabuf_fd: Pointer[Int, ReadWrite, Opaque],
      handle: hsa_amd_vmem_alloc_handle_t,
      flags: uint64_t
  ): hsa_status_t = unsupported
  def hsa_amd_vmem_get_access(
      va: OpaquePointer,
      perms: Pointer[hsa_access_permission_t, ReadWrite, Opaque],
      agent_handle: hsa_agent_t
  ): hsa_status_t = unsupported
  def hsa_amd_vmem_get_alloc_properties_from_handle(
      memory_handle: hsa_amd_vmem_alloc_handle_t,
      pool: Pointer[hsa_amd_memory_pool_t, ReadWrite, Opaque],
      `type`: Pointer[hsa_amd_memory_type_t, ReadWrite, Opaque]
  ): hsa_status_t = unsupported
  def hsa_amd_vmem_handle_create(
      pool: hsa_amd_memory_pool_t,
      size: size_t,
      `type`: hsa_amd_memory_type_t,
      flags: uint64_t,
      memory_handle: Pointer[hsa_amd_vmem_alloc_handle_t, ReadWrite, Opaque]
  ): hsa_status_t = unsupported
  def hsa_amd_vmem_handle_release(memory_handle: hsa_amd_vmem_alloc_handle_t): hsa_status_t = unsupported
  def hsa_amd_vmem_import_shareable_handle(
      dmabuf_fd: Int,
      handle: Pointer[hsa_amd_vmem_alloc_handle_t, ReadWrite, Opaque]
  ): hsa_status_t = unsupported
  def hsa_amd_vmem_map(
      va: OpaquePointer,
      size: size_t,
      in_offset: size_t,
      memory_handle: hsa_amd_vmem_alloc_handle_t,
      flags: uint64_t
  ): hsa_status_t = unsupported
  def hsa_amd_vmem_retain_alloc_handle(
      memory_handle: Pointer[hsa_amd_vmem_alloc_handle_t, ReadWrite, Opaque],
      addr: OpaquePointer
  ): hsa_status_t = unsupported
  def hsa_amd_vmem_set_access(
      va: OpaquePointer,
      size: size_t,
      desc: Pointer[hsa_amd_memory_access_desc_t, Read, Opaque],
      desc_cnt: size_t
  ): hsa_status_t = unsupported
  def hsa_amd_vmem_unmap(va: OpaquePointer, size: size_t): hsa_status_t = unsupported
  def hsa_cache_get_info(cache: hsa_cache_t, attribute: hsa_cache_info_t, value: OpaquePointer): hsa_status_t =
    unsupported
  def hsa_code_object_deserialize(
      serialized_code_object: OpaquePointer,
      serialized_code_object_size: size_t,
      options: CString,
      code_object: Pointer[hsa_code_object_t, ReadWrite, Opaque]
  ): hsa_status_t = unsupported
  def hsa_code_object_destroy(code_object: hsa_code_object_t): hsa_status_t = unsupported
  def hsa_code_object_get_info(
      code_object: hsa_code_object_t,
      attribute: hsa_code_object_info_t,
      value: OpaquePointer
  ): hsa_status_t = unsupported
  def hsa_code_object_get_symbol(
      code_object: hsa_code_object_t,
      symbol_name: CString,
      symbol: Pointer[hsa_code_symbol_t, ReadWrite, Opaque]
  ): hsa_status_t = unsupported
  def hsa_code_object_get_symbol_from_name(
      code_object: hsa_code_object_t,
      module_name: CString,
      symbol_name: CString,
      symbol: Pointer[hsa_code_symbol_t, ReadWrite, Opaque]
  ): hsa_status_t = unsupported
  def hsa_code_object_iterate_symbols(
      code_object: hsa_code_object_t,
      callback: Callback[(hsa_code_object_t, hsa_code_symbol_t, OpaquePointer), hsa_status_t],
      data: OpaquePointer
  ): hsa_status_t = unsupported
  def hsa_code_object_reader_create_from_file(
      file: hsa_file_t,
      code_object_reader: Pointer[hsa_code_object_reader_t, ReadWrite, Opaque]
  ): hsa_status_t = unsupported
  def hsa_code_object_reader_create_from_memory(
      code_object: Pointer[CVoid, Read, Opaque],
      size: size_t,
      code_object_reader: Pointer[hsa_code_object_reader_t, ReadWrite, Opaque]
  ): hsa_status_t = unsupported
  def hsa_code_object_reader_destroy(code_object_reader: hsa_code_object_reader_t): hsa_status_t = unsupported
  def hsa_code_object_serialize(
      code_object: hsa_code_object_t,
      alloc_callback: Callback[(size_t, hsa_callback_data_t, Pointer[OpaquePointer, ReadWrite, Opaque]), hsa_status_t],
      callback_data: hsa_callback_data_t,
      options: CString,
      serialized_code_object: Pointer[OpaquePointer, ReadWrite, Opaque],
      serialized_code_object_size: Pointer[size_t, ReadWrite, Opaque]
  ): hsa_status_t = unsupported
  def hsa_code_symbol_get_info(
      code_symbol: hsa_code_symbol_t,
      attribute: hsa_code_symbol_info_t,
      value: OpaquePointer
  ): hsa_status_t = unsupported
  def hsa_executable_agent_global_variable_define(
      executable: hsa_executable_t,
      agent: hsa_agent_t,
      variable_name: CString,
      address: OpaquePointer
  ): hsa_status_t = unsupported
  def hsa_executable_create(
      profile: hsa_profile_t,
      executable_state: hsa_executable_state_t,
      options: CString,
      executable: Pointer[hsa_executable_t, ReadWrite, Opaque]
  ): hsa_status_t = unsupported
  def hsa_executable_create_alt(
      profile: hsa_profile_t,
      default_float_rounding_mode: hsa_default_float_rounding_mode_t,
      options: CString,
      executable: Pointer[hsa_executable_t, ReadWrite, Opaque]
  ): hsa_status_t = unsupported
  def hsa_executable_destroy(executable: hsa_executable_t): hsa_status_t = unsupported
  def hsa_executable_freeze(executable: hsa_executable_t, options: CString): hsa_status_t = unsupported
  def hsa_executable_get_info(
      executable: hsa_executable_t,
      attribute: hsa_executable_info_t,
      value: OpaquePointer
  ): hsa_status_t = unsupported
  def hsa_executable_get_symbol(
      executable: hsa_executable_t,
      module_name: CString,
      symbol_name: CString,
      agent: hsa_agent_t,
      call_convention: int32_t,
      symbol: Pointer[hsa_executable_symbol_t, ReadWrite, Opaque]
  ): hsa_status_t = unsupported
  def hsa_executable_get_symbol_by_name(
      executable: hsa_executable_t,
      symbol_name: CString,
      agent: Pointer[hsa_agent_t, Read, Opaque],
      symbol: Pointer[hsa_executable_symbol_t, ReadWrite, Opaque]
  ): hsa_status_t = unsupported
  def hsa_executable_global_variable_define(
      executable: hsa_executable_t,
      variable_name: CString,
      address: OpaquePointer
  ): hsa_status_t = unsupported
  def hsa_executable_iterate_agent_symbols(
      executable: hsa_executable_t,
      agent: hsa_agent_t,
      callback: Callback[(hsa_executable_t, hsa_agent_t, hsa_executable_symbol_t, OpaquePointer), hsa_status_t],
      data: OpaquePointer
  ): hsa_status_t = unsupported
  def hsa_executable_iterate_program_symbols(
      executable: hsa_executable_t,
      callback: Callback[(hsa_executable_t, hsa_executable_symbol_t, OpaquePointer), hsa_status_t],
      data: OpaquePointer
  ): hsa_status_t = unsupported
  def hsa_executable_iterate_symbols(
      executable: hsa_executable_t,
      callback: Callback[(hsa_executable_t, hsa_executable_symbol_t, OpaquePointer), hsa_status_t],
      data: OpaquePointer
  ): hsa_status_t = unsupported
  def hsa_executable_load_agent_code_object(
      executable: hsa_executable_t,
      agent: hsa_agent_t,
      code_object_reader: hsa_code_object_reader_t,
      options: CString,
      loaded_code_object: Pointer[hsa_loaded_code_object_t, ReadWrite, Opaque]
  ): hsa_status_t = unsupported
  def hsa_executable_load_code_object(
      executable: hsa_executable_t,
      agent: hsa_agent_t,
      code_object: hsa_code_object_t,
      options: CString
  ): hsa_status_t = unsupported
  def hsa_executable_load_program_code_object(
      executable: hsa_executable_t,
      code_object_reader: hsa_code_object_reader_t,
      options: CString,
      loaded_code_object: Pointer[hsa_loaded_code_object_t, ReadWrite, Opaque]
  ): hsa_status_t = unsupported
  def hsa_executable_readonly_variable_define(
      executable: hsa_executable_t,
      agent: hsa_agent_t,
      variable_name: CString,
      address: OpaquePointer
  ): hsa_status_t = unsupported
  def hsa_executable_symbol_get_info(
      executable_symbol: hsa_executable_symbol_t,
      attribute: hsa_executable_symbol_info_t,
      value: OpaquePointer
  ): hsa_status_t = unsupported
  def hsa_executable_validate(
      executable: hsa_executable_t,
      result: Pointer[uint32_t, ReadWrite, Opaque]
  ): hsa_status_t = unsupported
  def hsa_executable_validate_alt(
      executable: hsa_executable_t,
      options: CString,
      result: Pointer[uint32_t, ReadWrite, Opaque]
  ): hsa_status_t = unsupported
  def hsa_ext_image_clear(
      agent: hsa_agent_t,
      image: hsa_ext_image_t,
      data: Pointer[CVoid, Read, Opaque],
      image_region: Pointer[hsa_ext_image_region_t, Read, Opaque]
  ): hsa_status_t = unsupported
  def hsa_ext_image_copy(
      agent: hsa_agent_t,
      src_image: hsa_ext_image_t,
      src_offset: Pointer[hsa_dim3_t, Read, Opaque],
      dst_image: hsa_ext_image_t,
      dst_offset: Pointer[hsa_dim3_t, Read, Opaque],
      range: Pointer[hsa_dim3_t, Read, Opaque]
  ): hsa_status_t = unsupported
  def hsa_ext_image_create(
      agent: hsa_agent_t,
      image_descriptor: Pointer[hsa_ext_image_descriptor_t, Read, Opaque],
      image_data: Pointer[CVoid, Read, Opaque],
      access_permission: hsa_access_permission_t,
      image: Pointer[hsa_ext_image_t, ReadWrite, Opaque]
  ): hsa_status_t = unsupported
  def hsa_ext_image_create_with_layout(
      agent: hsa_agent_t,
      image_descriptor: Pointer[hsa_ext_image_descriptor_t, Read, Opaque],
      image_data: Pointer[CVoid, Read, Opaque],
      access_permission: hsa_access_permission_t,
      image_data_layout: hsa_ext_image_data_layout_t,
      image_data_row_pitch: size_t,
      image_data_slice_pitch: size_t,
      image: Pointer[hsa_ext_image_t, ReadWrite, Opaque]
  ): hsa_status_t = unsupported
  def hsa_ext_image_data_get_info(
      agent: hsa_agent_t,
      image_descriptor: Pointer[hsa_ext_image_descriptor_t, Read, Opaque],
      access_permission: hsa_access_permission_t,
      image_data_info: Pointer[hsa_ext_image_data_info_t, ReadWrite, Opaque]
  ): hsa_status_t = unsupported
  def hsa_ext_image_data_get_info_with_layout(
      agent: hsa_agent_t,
      image_descriptor: Pointer[hsa_ext_image_descriptor_t, Read, Opaque],
      access_permission: hsa_access_permission_t,
      image_data_layout: hsa_ext_image_data_layout_t,
      image_data_row_pitch: size_t,
      image_data_slice_pitch: size_t,
      image_data_info: Pointer[hsa_ext_image_data_info_t, ReadWrite, Opaque]
  ): hsa_status_t = unsupported
  def hsa_ext_image_destroy(agent: hsa_agent_t, image: hsa_ext_image_t): hsa_status_t = unsupported
  def hsa_ext_image_export(
      agent: hsa_agent_t,
      src_image: hsa_ext_image_t,
      dst_memory: OpaquePointer,
      dst_row_pitch: size_t,
      dst_slice_pitch: size_t,
      image_region: Pointer[hsa_ext_image_region_t, Read, Opaque]
  ): hsa_status_t = unsupported
  def hsa_ext_image_get_capability(
      agent: hsa_agent_t,
      geometry: hsa_ext_image_geometry_t,
      image_format: Pointer[hsa_ext_image_format_t, Read, Opaque],
      capability_mask: Pointer[uint32_t, ReadWrite, Opaque]
  ): hsa_status_t = unsupported
  def hsa_ext_image_get_capability_with_layout(
      agent: hsa_agent_t,
      geometry: hsa_ext_image_geometry_t,
      image_format: Pointer[hsa_ext_image_format_t, Read, Opaque],
      image_data_layout: hsa_ext_image_data_layout_t,
      capability_mask: Pointer[uint32_t, ReadWrite, Opaque]
  ): hsa_status_t = unsupported
  def hsa_ext_image_import(
      agent: hsa_agent_t,
      src_memory: Pointer[CVoid, Read, Opaque],
      src_row_pitch: size_t,
      src_slice_pitch: size_t,
      dst_image: hsa_ext_image_t,
      image_region: Pointer[hsa_ext_image_region_t, Read, Opaque]
  ): hsa_status_t = unsupported
  def hsa_ext_sampler_create(
      agent: hsa_agent_t,
      sampler_descriptor: Pointer[hsa_ext_sampler_descriptor_t, Read, Opaque],
      sampler: Pointer[hsa_ext_sampler_t, ReadWrite, Opaque]
  ): hsa_status_t = unsupported
  def hsa_ext_sampler_create_v2(
      agent: hsa_agent_t,
      sampler_descriptor: Pointer[hsa_ext_sampler_descriptor_v2_t, Read, Opaque],
      sampler: Pointer[hsa_ext_sampler_t, ReadWrite, Opaque]
  ): hsa_status_t = unsupported
  def hsa_ext_sampler_destroy(agent: hsa_agent_t, sampler: hsa_ext_sampler_t): hsa_status_t = unsupported
  def hsa_extension_get_name(extension: uint16_t, name: Pointer[CString, ReadWrite, Opaque]): hsa_status_t = unsupported
  def hsa_init(): hsa_status_t = unsupported
  def hsa_isa_compatible(
      code_object_isa: hsa_isa_t,
      agent_isa: hsa_isa_t,
      result: Pointer[Boolean, ReadWrite, Opaque]
  ): hsa_status_t = unsupported
  def hsa_isa_from_name(name: CString, isa: Pointer[hsa_isa_t, ReadWrite, Opaque]): hsa_status_t = unsupported
  def hsa_isa_get_exception_policies(
      isa: hsa_isa_t,
      profile: hsa_profile_t,
      mask: Pointer[uint16_t, ReadWrite, Opaque]
  ): hsa_status_t = unsupported
  def hsa_isa_get_info(isa: hsa_isa_t, attribute: hsa_isa_info_t, index: uint32_t, value: OpaquePointer): hsa_status_t =
    unsupported
  def hsa_isa_get_info_alt(isa: hsa_isa_t, attribute: hsa_isa_info_t, value: OpaquePointer): hsa_status_t = unsupported
  def hsa_isa_get_round_method(
      isa: hsa_isa_t,
      fp_type: hsa_fp_type_t,
      flush_mode: hsa_flush_mode_t,
      round_method: Pointer[hsa_round_method_t, ReadWrite, Opaque]
  ): hsa_status_t = unsupported
  def hsa_isa_iterate_wavefronts(
      isa: hsa_isa_t,
      callback: Callback[(hsa_wavefront_t, OpaquePointer), hsa_status_t],
      data: OpaquePointer
  ): hsa_status_t = unsupported
  def hsa_iterate_agents(
      callback: Callback[(hsa_agent_t, OpaquePointer), hsa_status_t],
      data: OpaquePointer
  ): hsa_status_t = unsupported
  def hsa_memory_allocate(
      region: hsa_region_t,
      size: size_t,
      ptr: Pointer[OpaquePointer, ReadWrite, Opaque]
  ): hsa_status_t = unsupported
  def hsa_memory_assign_agent(ptr: OpaquePointer, agent: hsa_agent_t, access: hsa_access_permission_t): hsa_status_t =
    unsupported
  def hsa_memory_copy(dst: OpaquePointer, src: Pointer[CVoid, Read, Opaque], size: size_t): hsa_status_t = unsupported
  def hsa_memory_deregister(ptr: OpaquePointer, size: size_t): hsa_status_t = unsupported
  def hsa_memory_free(ptr: OpaquePointer): hsa_status_t = unsupported
  def hsa_memory_register(ptr: OpaquePointer, size: size_t): hsa_status_t = unsupported
  def hsa_queue_add_write_index_acq_rel(queue: Pointer[hsa_queue_t, Read, Opaque], value: uint64_t): uint64_t =
    unsupported
  def hsa_queue_add_write_index_acquire(queue: Pointer[hsa_queue_t, Read, Opaque], value: uint64_t): uint64_t =
    unsupported
  def hsa_queue_add_write_index_relaxed(queue: Pointer[hsa_queue_t, Read, Opaque], value: uint64_t): uint64_t =
    unsupported
  def hsa_queue_add_write_index_release(queue: Pointer[hsa_queue_t, Read, Opaque], value: uint64_t): uint64_t =
    unsupported
  def hsa_queue_add_write_index_scacq_screl(queue: Pointer[hsa_queue_t, Read, Opaque], value: uint64_t): uint64_t =
    unsupported
  def hsa_queue_add_write_index_scacquire(queue: Pointer[hsa_queue_t, Read, Opaque], value: uint64_t): uint64_t =
    unsupported
  def hsa_queue_add_write_index_screlease(queue: Pointer[hsa_queue_t, Read, Opaque], value: uint64_t): uint64_t =
    unsupported
  def hsa_queue_cas_write_index_acq_rel(
      queue: Pointer[hsa_queue_t, Read, Opaque],
      expected: uint64_t,
      value: uint64_t
  ): uint64_t = unsupported
  def hsa_queue_cas_write_index_acquire(
      queue: Pointer[hsa_queue_t, Read, Opaque],
      expected: uint64_t,
      value: uint64_t
  ): uint64_t = unsupported
  def hsa_queue_cas_write_index_relaxed(
      queue: Pointer[hsa_queue_t, Read, Opaque],
      expected: uint64_t,
      value: uint64_t
  ): uint64_t = unsupported
  def hsa_queue_cas_write_index_release(
      queue: Pointer[hsa_queue_t, Read, Opaque],
      expected: uint64_t,
      value: uint64_t
  ): uint64_t = unsupported
  def hsa_queue_cas_write_index_scacq_screl(
      queue: Pointer[hsa_queue_t, Read, Opaque],
      expected: uint64_t,
      value: uint64_t
  ): uint64_t = unsupported
  def hsa_queue_cas_write_index_scacquire(
      queue: Pointer[hsa_queue_t, Read, Opaque],
      expected: uint64_t,
      value: uint64_t
  ): uint64_t = unsupported
  def hsa_queue_cas_write_index_screlease(
      queue: Pointer[hsa_queue_t, Read, Opaque],
      expected: uint64_t,
      value: uint64_t
  ): uint64_t = unsupported
  def hsa_queue_create(
      agent: hsa_agent_t,
      size: uint32_t,
      `type`: hsa_queue_type32_t,
      callback: Callback[(hsa_status_t, Pointer[hsa_queue_t, ReadWrite, Opaque], OpaquePointer), Unit],
      data: OpaquePointer,
      private_segment_size: uint32_t,
      group_segment_size: uint32_t,
      queue: Pointer[Pointer[hsa_queue_t, ReadWrite, Opaque], ReadWrite, Opaque]
  ): hsa_status_t = unsupported
  def hsa_queue_destroy(queue: Pointer[hsa_queue_t, ReadWrite, Opaque]): hsa_status_t = unsupported
  def hsa_queue_inactivate(queue: Pointer[hsa_queue_t, ReadWrite, Opaque]): hsa_status_t = unsupported
  def hsa_queue_load_read_index_acquire(queue: Pointer[hsa_queue_t, Read, Opaque]): uint64_t = unsupported
  def hsa_queue_load_read_index_relaxed(queue: Pointer[hsa_queue_t, Read, Opaque]): uint64_t = unsupported
  def hsa_queue_load_read_index_scacquire(queue: Pointer[hsa_queue_t, Read, Opaque]): uint64_t = unsupported
  def hsa_queue_load_write_index_acquire(queue: Pointer[hsa_queue_t, Read, Opaque]): uint64_t = unsupported
  def hsa_queue_load_write_index_relaxed(queue: Pointer[hsa_queue_t, Read, Opaque]): uint64_t = unsupported
  def hsa_queue_load_write_index_scacquire(queue: Pointer[hsa_queue_t, Read, Opaque]): uint64_t = unsupported
  def hsa_queue_store_read_index_relaxed(queue: Pointer[hsa_queue_t, Read, Opaque], value: uint64_t): Unit = unsupported
  def hsa_queue_store_read_index_release(queue: Pointer[hsa_queue_t, Read, Opaque], value: uint64_t): Unit = unsupported
  def hsa_queue_store_read_index_screlease(queue: Pointer[hsa_queue_t, Read, Opaque], value: uint64_t): Unit =
    unsupported
  def hsa_queue_store_write_index_relaxed(queue: Pointer[hsa_queue_t, Read, Opaque], value: uint64_t): Unit =
    unsupported
  def hsa_queue_store_write_index_release(queue: Pointer[hsa_queue_t, Read, Opaque], value: uint64_t): Unit =
    unsupported
  def hsa_queue_store_write_index_screlease(queue: Pointer[hsa_queue_t, Read, Opaque], value: uint64_t): Unit =
    unsupported
  def hsa_region_get_info(region: hsa_region_t, attribute: hsa_region_info_t, value: OpaquePointer): hsa_status_t =
    unsupported
  def hsa_shut_down(): hsa_status_t = unsupported
  def hsa_signal_add_acq_rel(signal: hsa_signal_t, value: hsa_signal_value_t): Unit = unsupported
  def hsa_signal_add_acquire(signal: hsa_signal_t, value: hsa_signal_value_t): Unit = unsupported
  def hsa_signal_add_relaxed(signal: hsa_signal_t, value: hsa_signal_value_t): Unit = unsupported
  def hsa_signal_add_release(signal: hsa_signal_t, value: hsa_signal_value_t): Unit = unsupported
  def hsa_signal_add_scacq_screl(signal: hsa_signal_t, value: hsa_signal_value_t): Unit = unsupported
  def hsa_signal_add_scacquire(signal: hsa_signal_t, value: hsa_signal_value_t): Unit = unsupported
  def hsa_signal_add_screlease(signal: hsa_signal_t, value: hsa_signal_value_t): Unit = unsupported
  def hsa_signal_and_acq_rel(signal: hsa_signal_t, value: hsa_signal_value_t): Unit = unsupported
  def hsa_signal_and_acquire(signal: hsa_signal_t, value: hsa_signal_value_t): Unit = unsupported
  def hsa_signal_and_relaxed(signal: hsa_signal_t, value: hsa_signal_value_t): Unit = unsupported
  def hsa_signal_and_release(signal: hsa_signal_t, value: hsa_signal_value_t): Unit = unsupported
  def hsa_signal_and_scacq_screl(signal: hsa_signal_t, value: hsa_signal_value_t): Unit = unsupported
  def hsa_signal_and_scacquire(signal: hsa_signal_t, value: hsa_signal_value_t): Unit = unsupported
  def hsa_signal_and_screlease(signal: hsa_signal_t, value: hsa_signal_value_t): Unit = unsupported
  def hsa_signal_cas_acq_rel(
      signal: hsa_signal_t,
      expected: hsa_signal_value_t,
      value: hsa_signal_value_t
  ): hsa_signal_value_t = unsupported
  def hsa_signal_cas_acquire(
      signal: hsa_signal_t,
      expected: hsa_signal_value_t,
      value: hsa_signal_value_t
  ): hsa_signal_value_t = unsupported
  def hsa_signal_cas_relaxed(
      signal: hsa_signal_t,
      expected: hsa_signal_value_t,
      value: hsa_signal_value_t
  ): hsa_signal_value_t = unsupported
  def hsa_signal_cas_release(
      signal: hsa_signal_t,
      expected: hsa_signal_value_t,
      value: hsa_signal_value_t
  ): hsa_signal_value_t = unsupported
  def hsa_signal_cas_scacq_screl(
      signal: hsa_signal_t,
      expected: hsa_signal_value_t,
      value: hsa_signal_value_t
  ): hsa_signal_value_t = unsupported
  def hsa_signal_cas_scacquire(
      signal: hsa_signal_t,
      expected: hsa_signal_value_t,
      value: hsa_signal_value_t
  ): hsa_signal_value_t = unsupported
  def hsa_signal_cas_screlease(
      signal: hsa_signal_t,
      expected: hsa_signal_value_t,
      value: hsa_signal_value_t
  ): hsa_signal_value_t = unsupported
  def hsa_signal_create(
      initial_value: hsa_signal_value_t,
      num_consumers: uint32_t,
      consumers: Pointer[hsa_agent_t, Read, Opaque],
      signal: Pointer[hsa_signal_t, ReadWrite, Opaque]
  ): hsa_status_t = unsupported
  def hsa_signal_destroy(signal: hsa_signal_t): hsa_status_t = unsupported
  def hsa_signal_exchange_acq_rel(signal: hsa_signal_t, value: hsa_signal_value_t): hsa_signal_value_t = unsupported
  def hsa_signal_exchange_acquire(signal: hsa_signal_t, value: hsa_signal_value_t): hsa_signal_value_t = unsupported
  def hsa_signal_exchange_relaxed(signal: hsa_signal_t, value: hsa_signal_value_t): hsa_signal_value_t = unsupported
  def hsa_signal_exchange_release(signal: hsa_signal_t, value: hsa_signal_value_t): hsa_signal_value_t = unsupported
  def hsa_signal_exchange_scacq_screl(signal: hsa_signal_t, value: hsa_signal_value_t): hsa_signal_value_t = unsupported
  def hsa_signal_exchange_scacquire(signal: hsa_signal_t, value: hsa_signal_value_t): hsa_signal_value_t = unsupported
  def hsa_signal_exchange_screlease(signal: hsa_signal_t, value: hsa_signal_value_t): hsa_signal_value_t = unsupported
  def hsa_signal_group_create(
      num_signals: uint32_t,
      signals: Pointer[hsa_signal_t, Read, Opaque],
      num_consumers: uint32_t,
      consumers: Pointer[hsa_agent_t, Read, Opaque],
      signal_group: Pointer[hsa_signal_group_t, ReadWrite, Opaque]
  ): hsa_status_t = unsupported
  def hsa_signal_group_destroy(signal_group: hsa_signal_group_t): hsa_status_t = unsupported
  def hsa_signal_group_wait_any_relaxed(
      signal_group: hsa_signal_group_t,
      conditions: Pointer[hsa_signal_condition_t, Read, Opaque],
      compare_values: Pointer[hsa_signal_value_t, Read, Opaque],
      wait_state_hint: hsa_wait_state_t,
      signal: Pointer[hsa_signal_t, ReadWrite, Opaque],
      value: Pointer[hsa_signal_value_t, ReadWrite, Opaque]
  ): hsa_status_t = unsupported
  def hsa_signal_group_wait_any_scacquire(
      signal_group: hsa_signal_group_t,
      conditions: Pointer[hsa_signal_condition_t, Read, Opaque],
      compare_values: Pointer[hsa_signal_value_t, Read, Opaque],
      wait_state_hint: hsa_wait_state_t,
      signal: Pointer[hsa_signal_t, ReadWrite, Opaque],
      value: Pointer[hsa_signal_value_t, ReadWrite, Opaque]
  ): hsa_status_t = unsupported
  def hsa_signal_load_acquire(signal: hsa_signal_t): hsa_signal_value_t = unsupported
  def hsa_signal_load_relaxed(signal: hsa_signal_t): hsa_signal_value_t = unsupported
  def hsa_signal_load_scacquire(signal: hsa_signal_t): hsa_signal_value_t = unsupported
  def hsa_signal_or_acq_rel(signal: hsa_signal_t, value: hsa_signal_value_t): Unit = unsupported
  def hsa_signal_or_acquire(signal: hsa_signal_t, value: hsa_signal_value_t): Unit = unsupported
  def hsa_signal_or_relaxed(signal: hsa_signal_t, value: hsa_signal_value_t): Unit = unsupported
  def hsa_signal_or_release(signal: hsa_signal_t, value: hsa_signal_value_t): Unit = unsupported
  def hsa_signal_or_scacq_screl(signal: hsa_signal_t, value: hsa_signal_value_t): Unit = unsupported
  def hsa_signal_or_scacquire(signal: hsa_signal_t, value: hsa_signal_value_t): Unit = unsupported
  def hsa_signal_or_screlease(signal: hsa_signal_t, value: hsa_signal_value_t): Unit = unsupported
  def hsa_signal_silent_store_relaxed(signal: hsa_signal_t, value: hsa_signal_value_t): Unit = unsupported
  def hsa_signal_silent_store_screlease(signal: hsa_signal_t, value: hsa_signal_value_t): Unit = unsupported
  def hsa_signal_store_relaxed(signal: hsa_signal_t, value: hsa_signal_value_t): Unit = unsupported
  def hsa_signal_store_release(signal: hsa_signal_t, value: hsa_signal_value_t): Unit = unsupported
  def hsa_signal_store_screlease(signal: hsa_signal_t, value: hsa_signal_value_t): Unit = unsupported
  def hsa_signal_subtract_acq_rel(signal: hsa_signal_t, value: hsa_signal_value_t): Unit = unsupported
  def hsa_signal_subtract_acquire(signal: hsa_signal_t, value: hsa_signal_value_t): Unit = unsupported
  def hsa_signal_subtract_relaxed(signal: hsa_signal_t, value: hsa_signal_value_t): Unit = unsupported
  def hsa_signal_subtract_release(signal: hsa_signal_t, value: hsa_signal_value_t): Unit = unsupported
  def hsa_signal_subtract_scacq_screl(signal: hsa_signal_t, value: hsa_signal_value_t): Unit = unsupported
  def hsa_signal_subtract_scacquire(signal: hsa_signal_t, value: hsa_signal_value_t): Unit = unsupported
  def hsa_signal_subtract_screlease(signal: hsa_signal_t, value: hsa_signal_value_t): Unit = unsupported
  def hsa_signal_wait_acquire(
      signal: hsa_signal_t,
      condition: hsa_signal_condition_t,
      compare_value: hsa_signal_value_t,
      timeout_hint: uint64_t,
      wait_state_hint: hsa_wait_state_t
  ): hsa_signal_value_t = unsupported
  def hsa_signal_wait_relaxed(
      signal: hsa_signal_t,
      condition: hsa_signal_condition_t,
      compare_value: hsa_signal_value_t,
      timeout_hint: uint64_t,
      wait_state_hint: hsa_wait_state_t
  ): hsa_signal_value_t = unsupported
  def hsa_signal_wait_scacquire(
      signal: hsa_signal_t,
      condition: hsa_signal_condition_t,
      compare_value: hsa_signal_value_t,
      timeout_hint: uint64_t,
      wait_state_hint: hsa_wait_state_t
  ): hsa_signal_value_t = unsupported
  def hsa_signal_xor_acq_rel(signal: hsa_signal_t, value: hsa_signal_value_t): Unit = unsupported
  def hsa_signal_xor_acquire(signal: hsa_signal_t, value: hsa_signal_value_t): Unit = unsupported
  def hsa_signal_xor_relaxed(signal: hsa_signal_t, value: hsa_signal_value_t): Unit = unsupported
  def hsa_signal_xor_release(signal: hsa_signal_t, value: hsa_signal_value_t): Unit = unsupported
  def hsa_signal_xor_scacq_screl(signal: hsa_signal_t, value: hsa_signal_value_t): Unit = unsupported
  def hsa_signal_xor_scacquire(signal: hsa_signal_t, value: hsa_signal_value_t): Unit = unsupported
  def hsa_signal_xor_screlease(signal: hsa_signal_t, value: hsa_signal_value_t): Unit = unsupported
  def hsa_soft_queue_create(
      region: hsa_region_t,
      size: uint32_t,
      `type`: hsa_queue_type32_t,
      features: uint32_t,
      doorbell_signal: hsa_signal_t,
      queue: Pointer[Pointer[hsa_queue_t, ReadWrite, Opaque], ReadWrite, Opaque]
  ): hsa_status_t = unsupported
  def hsa_status_string(status: hsa_status_t, status_string: Pointer[CString, ReadWrite, Opaque]): hsa_status_t =
    unsupported
  def hsa_system_extension_supported(
      extension: uint16_t,
      version_major: uint16_t,
      version_minor: uint16_t,
      result: Pointer[Boolean, ReadWrite, Opaque]
  ): hsa_status_t = unsupported
  def hsa_system_get_extension_table(
      extension: uint16_t,
      version_major: uint16_t,
      version_minor: uint16_t,
      table: OpaquePointer
  ): hsa_status_t = unsupported
  def hsa_system_get_info(attribute: hsa_system_info_t, value: OpaquePointer): hsa_status_t = unsupported
  def hsa_system_get_major_extension_table(
      extension: uint16_t,
      version_major: uint16_t,
      table_length: size_t,
      table: OpaquePointer
  ): hsa_status_t = unsupported
  def hsa_system_major_extension_supported(
      extension: uint16_t,
      version_major: uint16_t,
      version_minor: Pointer[uint16_t, ReadWrite, Opaque],
      result: Pointer[Boolean, ReadWrite, Opaque]
  ): hsa_status_t = unsupported
  def hsa_ven_amd_pcs_create(
      agent: hsa_agent_t,
      method: hsa_ven_amd_pcs_method_kind_t,
      units: hsa_ven_amd_pcs_units_t,
      interval: size_t,
      latency: size_t,
      buffer_size: size_t,
      data_ready_callback: hsa_ven_amd_pcs_data_ready_callback_t,
      client_callback_data: OpaquePointer,
      pc_sampling: Pointer[hsa_ven_amd_pcs_t, ReadWrite, Opaque]
  ): hsa_status_t = unsupported
  def hsa_ven_amd_pcs_create_from_id(
      pcs_id: uint32_t,
      agent: hsa_agent_t,
      method: hsa_ven_amd_pcs_method_kind_t,
      units: hsa_ven_amd_pcs_units_t,
      interval: size_t,
      latency: size_t,
      buffer_size: size_t,
      data_ready_callback: hsa_ven_amd_pcs_data_ready_callback_t,
      client_callback_data: OpaquePointer,
      pc_sampling: Pointer[hsa_ven_amd_pcs_t, ReadWrite, Opaque]
  ): hsa_status_t = unsupported
  def hsa_ven_amd_pcs_destroy(pc_sampling: hsa_ven_amd_pcs_t): hsa_status_t = unsupported
  def hsa_ven_amd_pcs_flush(pc_sampling: hsa_ven_amd_pcs_t): hsa_status_t = unsupported
  def hsa_ven_amd_pcs_iterate_configuration(
      agent: hsa_agent_t,
      configuration_callback: hsa_ven_amd_pcs_iterate_configuration_callback_t,
      callback_data: OpaquePointer
  ): hsa_status_t = unsupported
  def hsa_ven_amd_pcs_start(pc_sampling: hsa_ven_amd_pcs_t): hsa_status_t = unsupported
  def hsa_ven_amd_pcs_stop(pc_sampling: hsa_ven_amd_pcs_t): hsa_status_t = unsupported
  def hsa_wavefront_get_info(
      wavefront: hsa_wavefront_t,
      attribute: hsa_wavefront_info_t,
      value: OpaquePointer
  ): hsa_status_t = unsupported
}
