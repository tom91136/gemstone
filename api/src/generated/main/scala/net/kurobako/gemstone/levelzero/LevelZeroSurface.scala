// Generated from pinned official headers by GenerateAPISurfaces. Do not edit mechanically.
package net.kurobako.gemstone.levelzero

import net.kurobako.gemstone.abi.*

type const_uint32_t = CValue["const uint32_t"]
type const_ze_group_count_t = CValue["const ze_group_count_t"]
type const_ze_group_size_t = CValue["const ze_group_size_t"]
type int32_t = CValue["int32_t"]
type size_t = CValue["size_t"]
type uint32_t = CValue["uint32_t"]
type uint64_t = CValue["uint64_t"]
type uint8_t = CValue["uint8_t"]
type unsigned_int = CValue["unsigned int"]
type ze_api_version_t = CValue["ze_api_version_t"]
type ze_bool_t = CValue["ze_bool_t"]
type ze_cache_config_flags_t = CValue["ze_cache_config_flags_t"]
type ze_cache_ext_region_t = CValue["ze_cache_ext_region_t"]
type ze_command_list_desc_t = CValue["ze_command_list_desc_t"]
type ze_command_list_flags_t = CValue["ze_command_list_flags_t"]
type ze_command_list_handle_t = CValue["ze_command_list_handle_t"]
type ze_command_queue_desc_t = CValue["ze_command_queue_desc_t"]
type ze_command_queue_flags_t = CValue["ze_command_queue_flags_t"]
type ze_command_queue_group_properties_t = CValue["ze_command_queue_group_properties_t"]
type ze_command_queue_handle_t = CValue["ze_command_queue_handle_t"]
type ze_command_queue_mode_t = CValue["ze_command_queue_mode_t"]
type ze_command_queue_priority_t = CValue["ze_command_queue_priority_t"]
type ze_context_desc_t = CValue["ze_context_desc_t"]
type ze_context_handle_t = CValue["ze_context_handle_t"]
type ze_copy_region_t = CValue["ze_copy_region_t"]
type ze_device_cache_properties_t = CValue["ze_device_cache_properties_t"]
type ze_device_compiler_info_t = CValue["ze_device_compiler_info_t"]
type ze_device_compute_properties_t = CValue["ze_device_compute_properties_t"]
type ze_device_external_memory_properties_t = CValue["ze_device_external_memory_properties_t"]
type ze_device_handle_t = CValue["ze_device_handle_t"]
type ze_device_image_properties_t = CValue["ze_device_image_properties_t"]
type ze_device_mem_alloc_desc_t = CValue["ze_device_mem_alloc_desc_t"]
type ze_device_memory_access_properties_t = CValue["ze_device_memory_access_properties_t"]
type ze_device_memory_properties_t = CValue["ze_device_memory_properties_t"]
type ze_device_module_properties_t = CValue["ze_device_module_properties_t"]
type ze_device_p2p_properties_t = CValue["ze_device_p2p_properties_t"]
type ze_device_properties_t = CValue["ze_device_properties_t"]
type ze_device_thread_t = CValue["ze_device_thread_t"]
type ze_device_vector_width_properties_ext_t = CValue["ze_device_vector_width_properties_ext_t"]
type ze_driver_extension_properties_t = CValue["ze_driver_extension_properties_t"]
type ze_driver_handle_t = CValue["ze_driver_handle_t"]
type ze_driver_ipc_properties_t = CValue["ze_driver_ipc_properties_t"]
type ze_driver_properties_t = CValue["ze_driver_properties_t"]
type ze_event_counter_based_desc_t = CValue["ze_event_counter_based_desc_t"]
type ze_event_counter_based_flags_t = CValue["ze_event_counter_based_flags_t"]
type ze_event_desc_t = CValue["ze_event_desc_t"]
type ze_event_handle_t = CValue["ze_event_handle_t"]
type ze_event_pool_desc_t = CValue["ze_event_pool_desc_t"]
type ze_event_pool_flags_t = CValue["ze_event_pool_flags_t"]
type ze_event_pool_handle_t = CValue["ze_event_pool_handle_t"]
type ze_event_query_kernel_timestamps_results_ext_properties_t =
  CValue["ze_event_query_kernel_timestamps_results_ext_properties_t"]
type ze_event_scope_flags_t = CValue["ze_event_scope_flags_t"]
type ze_executable_graph_handle_t = CValue["ze_executable_graph_handle_t"]
type ze_external_semaphore_ext_desc_t = CValue["ze_external_semaphore_ext_desc_t"]
type ze_external_semaphore_ext_handle_t = CValue["ze_external_semaphore_ext_handle_t"]
type ze_external_semaphore_signal_params_ext_t = CValue["ze_external_semaphore_signal_params_ext_t"]
type ze_external_semaphore_wait_params_ext_t = CValue["ze_external_semaphore_wait_params_ext_t"]
type ze_fabric_edge_exp_properties_t = CValue["ze_fabric_edge_exp_properties_t"]
type ze_fabric_edge_handle_t = CValue["ze_fabric_edge_handle_t"]
type ze_fabric_vertex_exp_properties_t = CValue["ze_fabric_vertex_exp_properties_t"]
type ze_fabric_vertex_handle_t = CValue["ze_fabric_vertex_handle_t"]
type ze_fence_desc_t = CValue["ze_fence_desc_t"]
type ze_fence_handle_t = CValue["ze_fence_handle_t"]
type ze_graph_handle_t = CValue["ze_graph_handle_t"]
type ze_group_count_t = CValue["ze_group_count_t"]
type ze_host_function_callback_t = CallbackTypedef["ze_host_function_callback_t", Tuple1[OpaquePointer], Unit]
type ze_host_mem_alloc_desc_t = CValue["ze_host_mem_alloc_desc_t"]
type ze_image_allocation_ext_properties_t = CValue["ze_image_allocation_ext_properties_t"]
type ze_image_desc_t = CValue["ze_image_desc_t"]
type ze_image_handle_t = CValue["ze_image_handle_t"]
type ze_image_memory_properties_exp_t = CValue["ze_image_memory_properties_exp_t"]
type ze_image_properties_t = CValue["ze_image_properties_t"]
type ze_image_region_t = CValue["ze_image_region_t"]
type ze_init_driver_type_desc_t = CValue["ze_init_driver_type_desc_t"]
type ze_init_flags_t = CValue["ze_init_flags_t"]
type ze_ipc_event_counter_based_handle_t = CValue["ze_ipc_event_counter_based_handle_t"]
type ze_ipc_event_pool_handle_t = CValue["ze_ipc_event_pool_handle_t"]
type ze_ipc_mem_handle_t = CValue["ze_ipc_mem_handle_t"]
type ze_ipc_memory_flags_t = CValue["ze_ipc_memory_flags_t"]
type ze_kernel_allocation_exp_properties_t = CValue["ze_kernel_allocation_exp_properties_t"]
type ze_kernel_desc_t = CValue["ze_kernel_desc_t"]
type ze_kernel_handle_t = CValue["ze_kernel_handle_t"]
type ze_kernel_indirect_access_flags_t = CValue["ze_kernel_indirect_access_flags_t"]
type ze_kernel_properties_t = CValue["ze_kernel_properties_t"]
type ze_kernel_timestamp_result_t = CValue["ze_kernel_timestamp_result_t"]
type ze_linkage_inspection_ext_desc_t = CValue["ze_linkage_inspection_ext_desc_t"]
type ze_memory_access_attribute_t = CValue["ze_memory_access_attribute_t"]
type ze_memory_advice_t = CValue["ze_memory_advice_t"]
type ze_memory_allocation_properties_t = CValue["ze_memory_allocation_properties_t"]
type ze_memory_atomic_attr_exp_flags_t = CValue["ze_memory_atomic_attr_exp_flags_t"]
type ze_memory_free_ext_desc_t = CValue["ze_memory_free_ext_desc_t"]
type ze_module_build_log_handle_t = CValue["ze_module_build_log_handle_t"]
type ze_module_desc_t = CValue["ze_module_desc_t"]
type ze_module_handle_t = CValue["ze_module_handle_t"]
type ze_module_properties_t = CValue["ze_module_properties_t"]
type ze_mutable_command_id_exp_desc_t = CValue["ze_mutable_command_id_exp_desc_t"]
type ze_mutable_commands_exp_desc_t = CValue["ze_mutable_commands_exp_desc_t"]
type ze_pci_ext_properties_t = CValue["ze_pci_ext_properties_t"]
type ze_physical_mem_desc_t = CValue["ze_physical_mem_desc_t"]
type ze_physical_mem_handle_t = CValue["ze_physical_mem_handle_t"]
type ze_physical_mem_properties_t = CValue["ze_physical_mem_properties_t"]
type ze_result_t = CValue["ze_result_t"]
type ze_rtas_aabb_exp_t = CValue["ze_rtas_aabb_exp_t"]
type ze_rtas_aabb_ext_t = CValue["ze_rtas_aabb_ext_t"]
type ze_rtas_builder_build_op_exp_desc_t = CValue["ze_rtas_builder_build_op_exp_desc_t"]
type ze_rtas_builder_build_op_ext_desc_t = CValue["ze_rtas_builder_build_op_ext_desc_t"]
type ze_rtas_builder_exp_desc_t = CValue["ze_rtas_builder_exp_desc_t"]
type ze_rtas_builder_exp_handle_t = CValue["ze_rtas_builder_exp_handle_t"]
type ze_rtas_builder_exp_properties_t = CValue["ze_rtas_builder_exp_properties_t"]
type ze_rtas_builder_ext_desc_t = CValue["ze_rtas_builder_ext_desc_t"]
type ze_rtas_builder_ext_handle_t = CValue["ze_rtas_builder_ext_handle_t"]
type ze_rtas_builder_ext_properties_t = CValue["ze_rtas_builder_ext_properties_t"]
type ze_rtas_format_exp_t = CValue["ze_rtas_format_exp_t"]
type ze_rtas_format_ext_t = CValue["ze_rtas_format_ext_t"]
type ze_rtas_parallel_operation_exp_handle_t = CValue["ze_rtas_parallel_operation_exp_handle_t"]
type ze_rtas_parallel_operation_exp_properties_t = CValue["ze_rtas_parallel_operation_exp_properties_t"]
type ze_rtas_parallel_operation_ext_handle_t = CValue["ze_rtas_parallel_operation_ext_handle_t"]
type ze_rtas_parallel_operation_ext_properties_t = CValue["ze_rtas_parallel_operation_ext_properties_t"]
type ze_sampler_desc_t = CValue["ze_sampler_desc_t"]
type ze_sampler_handle_t = CValue["ze_sampler_handle_t"]
type ze_scheduling_hint_exp_desc_t = CValue["ze_scheduling_hint_exp_desc_t"]
type ze_validate_runtime_requirements_output_t = CValue["ze_validate_runtime_requirements_output_t"]
type zes_control_property_t = CValue["zes_control_property_t"]
type zes_control_state_t = CValue["zes_control_state_t"]
type zes_device_action_t = CValue["zes_device_action_t"]
type zes_device_ecc_desc_t = CValue["zes_device_ecc_desc_t"]
type zes_device_ecc_properties_t = CValue["zes_device_ecc_properties_t"]
type zes_device_handle_t = CValue["zes_device_handle_t"]
type zes_device_health_status_ext_t = CValue["zes_device_health_status_ext_t"]
type zes_device_properties_t = CValue["zes_device_properties_t"]
type zes_device_state_t = CValue["zes_device_state_t"]
type zes_diag_handle_t = CValue["zes_diag_handle_t"]
type zes_diag_properties_t = CValue["zes_diag_properties_t"]
type zes_diag_result_t = CValue["zes_diag_result_t"]
type zes_diag_test_t = CValue["zes_diag_test_t"]
type zes_driver_extension_properties_t = CValue["zes_driver_extension_properties_t"]
type zes_driver_handle_t = CValue["zes_driver_handle_t"]
type zes_energy_threshold_t = CValue["zes_energy_threshold_t"]
type zes_engine_handle_t = CValue["zes_engine_handle_t"]
type zes_engine_properties_t = CValue["zes_engine_properties_t"]
type zes_engine_stats_t = CValue["zes_engine_stats_t"]
type zes_event_type_flags_t = CValue["zes_event_type_flags_t"]
type zes_fabric_link_type_t = CValue["zes_fabric_link_type_t"]
type zes_fabric_port_config_t = CValue["zes_fabric_port_config_t"]
type zes_fabric_port_error_counters_t = CValue["zes_fabric_port_error_counters_t"]
type zes_fabric_port_handle_t = CValue["zes_fabric_port_handle_t"]
type zes_fabric_port_properties_t = CValue["zes_fabric_port_properties_t"]
type zes_fabric_port_state_t = CValue["zes_fabric_port_state_t"]
type zes_fabric_port_throughput_t = CValue["zes_fabric_port_throughput_t"]
type zes_fan_config_t = CValue["zes_fan_config_t"]
type zes_fan_handle_t = CValue["zes_fan_handle_t"]
type zes_fan_properties_t = CValue["zes_fan_properties_t"]
type zes_fan_speed_t = CValue["zes_fan_speed_t"]
type zes_fan_speed_table_t = CValue["zes_fan_speed_table_t"]
type zes_fan_speed_units_t = CValue["zes_fan_speed_units_t"]
type zes_firmware_handle_t = CValue["zes_firmware_handle_t"]
type zes_firmware_properties_t = CValue["zes_firmware_properties_t"]
type zes_freq_handle_t = CValue["zes_freq_handle_t"]
type zes_freq_properties_t = CValue["zes_freq_properties_t"]
type zes_freq_range_t = CValue["zes_freq_range_t"]
type zes_freq_state_t = CValue["zes_freq_state_t"]
type zes_freq_throttle_time_t = CValue["zes_freq_throttle_time_t"]
type zes_init_flags_t = CValue["zes_init_flags_t"]
type zes_led_color_t = CValue["zes_led_color_t"]
type zes_led_handle_t = CValue["zes_led_handle_t"]
type zes_led_properties_t = CValue["zes_led_properties_t"]
type zes_led_state_t = CValue["zes_led_state_t"]
type zes_mem_bandwidth_t = CValue["zes_mem_bandwidth_t"]
type zes_mem_handle_t = CValue["zes_mem_handle_t"]
type zes_mem_properties_t = CValue["zes_mem_properties_t"]
type zes_mem_state_t = CValue["zes_mem_state_t"]
type zes_oc_capabilities_t = CValue["zes_oc_capabilities_t"]
type zes_oc_mode_t = CValue["zes_oc_mode_t"]
type zes_overclock_control_t = CValue["zes_overclock_control_t"]
type zes_overclock_domain_t = CValue["zes_overclock_domain_t"]
type zes_overclock_handle_t = CValue["zes_overclock_handle_t"]
type zes_overclock_mode_t = CValue["zes_overclock_mode_t"]
type zes_overclock_properties_t = CValue["zes_overclock_properties_t"]
type zes_pci_bar_properties_t = CValue["zes_pci_bar_properties_t"]
type zes_pci_properties_t = CValue["zes_pci_properties_t"]
type zes_pci_state_t = CValue["zes_pci_state_t"]
type zes_pci_stats_t = CValue["zes_pci_stats_t"]
type zes_pending_action_t = CValue["zes_pending_action_t"]
type zes_perf_handle_t = CValue["zes_perf_handle_t"]
type zes_perf_properties_t = CValue["zes_perf_properties_t"]
type zes_power_burst_limit_t = CValue["zes_power_burst_limit_t"]
type zes_power_energy_counter_t = CValue["zes_power_energy_counter_t"]
type zes_power_limit_ext_desc_t = CValue["zes_power_limit_ext_desc_t"]
type zes_power_peak_limit_t = CValue["zes_power_peak_limit_t"]
type zes_power_properties_t = CValue["zes_power_properties_t"]
type zes_power_sustained_limit_t = CValue["zes_power_sustained_limit_t"]
type zes_process_state_t = CValue["zes_process_state_t"]
type zes_psu_handle_t = CValue["zes_psu_handle_t"]
type zes_psu_properties_t = CValue["zes_psu_properties_t"]
type zes_psu_state_t = CValue["zes_psu_state_t"]
type zes_pwr_handle_t = CValue["zes_pwr_handle_t"]
type zes_ras_config_exp_t = CValue["zes_ras_config_exp_t"]
type zes_ras_config_t = CValue["zes_ras_config_t"]
type zes_ras_error_category_exp_t = CValue["zes_ras_error_category_exp_t"]
type zes_ras_handle_t = CValue["zes_ras_handle_t"]
type zes_ras_properties_t = CValue["zes_ras_properties_t"]
type zes_ras_state_exp2_t = CValue["zes_ras_state_exp2_t"]
type zes_ras_state_exp_t = CValue["zes_ras_state_exp_t"]
type zes_ras_state_t = CValue["zes_ras_state_t"]
type zes_reset_properties_t = CValue["zes_reset_properties_t"]
type zes_sched_handle_t = CValue["zes_sched_handle_t"]
type zes_sched_mode_t = CValue["zes_sched_mode_t"]
type zes_sched_properties_t = CValue["zes_sched_properties_t"]
type zes_sched_timeout_properties_t = CValue["zes_sched_timeout_properties_t"]
type zes_sched_timeslice_properties_t = CValue["zes_sched_timeslice_properties_t"]
type zes_standby_handle_t = CValue["zes_standby_handle_t"]
type zes_standby_promo_mode_t = CValue["zes_standby_promo_mode_t"]
type zes_standby_properties_t = CValue["zes_standby_properties_t"]
type zes_subdevice_exp_properties_t = CValue["zes_subdevice_exp_properties_t"]
type zes_temp_config_t = CValue["zes_temp_config_t"]
type zes_temp_handle_t = CValue["zes_temp_handle_t"]
type zes_temp_properties_t = CValue["zes_temp_properties_t"]
type zes_uuid_t = CValue["zes_uuid_t"]
type zes_vf_array_type_t = CValue["zes_vf_array_type_t"]
type zes_vf_exp2_capabilities_t = CValue["zes_vf_exp2_capabilities_t"]
type zes_vf_exp_capabilities_t = CValue["zes_vf_exp_capabilities_t"]
type zes_vf_exp_properties_t = CValue["zes_vf_exp_properties_t"]
type zes_vf_handle_t = CValue["zes_vf_handle_t"]
type zes_vf_info_util_exp_flags_t = CValue["zes_vf_info_util_exp_flags_t"]
type zes_vf_property_t = CValue["zes_vf_property_t"]
type zes_vf_type_t = CValue["zes_vf_type_t"]
type zes_vf_util_engine_exp2_t = CValue["zes_vf_util_engine_exp2_t"]
type zes_vf_util_engine_exp_t = CValue["zes_vf_util_engine_exp_t"]
type zes_vf_util_mem_exp2_t = CValue["zes_vf_util_mem_exp2_t"]
type zes_vf_util_mem_exp_t = CValue["zes_vf_util_mem_exp_t"]
type zet_command_list_handle_t = CValue["zet_command_list_handle_t"]
type zet_context_handle_t = CValue["zet_context_handle_t"]
type zet_core_callbacks_t = CValue["zet_core_callbacks_t"]
type zet_debug_config_t = CValue["zet_debug_config_t"]
type zet_debug_event_t = CValue["zet_debug_event_t"]
type zet_debug_memory_space_desc_t = CValue["zet_debug_memory_space_desc_t"]
type zet_debug_regset_properties_t = CValue["zet_debug_regset_properties_t"]
type zet_debug_session_handle_t = CValue["zet_debug_session_handle_t"]
type zet_device_debug_properties_t = CValue["zet_device_debug_properties_t"]
type zet_device_handle_t = CValue["zet_device_handle_t"]
type zet_kernel_handle_t = CValue["zet_kernel_handle_t"]
type zet_metric_calculate_exp_desc_t = CValue["zet_metric_calculate_exp_desc_t"]
type zet_metric_decoder_exp_handle_t = CValue["zet_metric_decoder_exp_handle_t"]
type zet_metric_entry_exp_t = CValue["zet_metric_entry_exp_t"]
type zet_metric_group_calculation_type_t = CValue["zet_metric_group_calculation_type_t"]
type zet_metric_group_handle_t = CValue["zet_metric_group_handle_t"]
type zet_metric_group_properties_t = CValue["zet_metric_group_properties_t"]
type zet_metric_group_sampling_type_flags_t = CValue["zet_metric_group_sampling_type_flags_t"]
type zet_metric_handle_t = CValue["zet_metric_handle_t"]
type zet_metric_programmable_exp_handle_t = CValue["zet_metric_programmable_exp_handle_t"]
type zet_metric_programmable_exp_properties_t = CValue["zet_metric_programmable_exp_properties_t"]
type zet_metric_programmable_param_info_exp_t = CValue["zet_metric_programmable_param_info_exp_t"]
type zet_metric_programmable_param_value_exp_t = CValue["zet_metric_programmable_param_value_exp_t"]
type zet_metric_programmable_param_value_info_exp_t = CValue["zet_metric_programmable_param_value_info_exp_t"]
type zet_metric_properties_t = CValue["zet_metric_properties_t"]
type zet_metric_query_handle_t = CValue["zet_metric_query_handle_t"]
type zet_metric_query_pool_desc_t = CValue["zet_metric_query_pool_desc_t"]
type zet_metric_query_pool_handle_t = CValue["zet_metric_query_pool_handle_t"]
type zet_metric_streamer_desc_t = CValue["zet_metric_streamer_desc_t"]
type zet_metric_streamer_handle_t = CValue["zet_metric_streamer_handle_t"]
type zet_metric_tracer_exp_desc_t = CValue["zet_metric_tracer_exp_desc_t"]
type zet_metric_tracer_exp_handle_t = CValue["zet_metric_tracer_exp_handle_t"]
type zet_module_debug_info_format_t = CValue["zet_module_debug_info_format_t"]
type zet_module_handle_t = CValue["zet_module_handle_t"]
type zet_profile_properties_t = CValue["zet_profile_properties_t"]
type zet_tracer_exp_desc_t = CValue["zet_tracer_exp_desc_t"]
type zet_tracer_exp_handle_t = CValue["zet_tracer_exp_handle_t"]
type zet_typed_value_t = CValue["zet_typed_value_t"]
type zex_mem_graph_free_callback_fn_t = CallbackTypedef["zex_mem_graph_free_callback_fn_t", Tuple1[OpaquePointer], Unit]

trait LevelZeroSurface {
  def zeCommandListAppendBarrier(
      hCommandList: ze_command_list_handle_t,
      hSignalEvent: ze_event_handle_t,
      numWaitEvents: uint32_t,
      phWaitEvents: Pointer[ze_event_handle_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zeCommandListAppendEventReset(hCommandList: ze_command_list_handle_t, hEvent: ze_event_handle_t): ze_result_t =
    unsupported
  def zeCommandListAppendGraphExt(
      hCommandList: ze_command_list_handle_t,
      hGraph: ze_executable_graph_handle_t,
      pNext: Pointer[CVoid, Read, Opaque],
      hSignalEvent: ze_event_handle_t,
      numWaitEvents: uint32_t,
      phWaitEvents: Pointer[ze_event_handle_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zeCommandListAppendHostFunction(
      hCommandList: ze_command_list_handle_t,
      pfnHostFunction: ze_host_function_callback_t,
      pUserData: OpaquePointer,
      pNext: Pointer[CVoid, Read, Opaque],
      hSignalEvent: ze_event_handle_t,
      numWaitEvents: uint32_t,
      phWaitEvents: Pointer[ze_event_handle_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zeCommandListAppendImageCopy(
      hCommandList: ze_command_list_handle_t,
      hDstImage: ze_image_handle_t,
      hSrcImage: ze_image_handle_t,
      hSignalEvent: ze_event_handle_t,
      numWaitEvents: uint32_t,
      phWaitEvents: Pointer[ze_event_handle_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zeCommandListAppendImageCopyFromMemory(
      hCommandList: ze_command_list_handle_t,
      hDstImage: ze_image_handle_t,
      srcptr: Pointer[CVoid, Read, Opaque],
      pDstRegion: Pointer[ze_image_region_t, Read, Opaque],
      hSignalEvent: ze_event_handle_t,
      numWaitEvents: uint32_t,
      phWaitEvents: Pointer[ze_event_handle_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zeCommandListAppendImageCopyFromMemoryExt(
      hCommandList: ze_command_list_handle_t,
      hDstImage: ze_image_handle_t,
      srcptr: Pointer[CVoid, Read, Opaque],
      pDstRegion: Pointer[ze_image_region_t, Read, Opaque],
      srcRowPitch: uint32_t,
      srcSlicePitch: uint32_t,
      hSignalEvent: ze_event_handle_t,
      numWaitEvents: uint32_t,
      phWaitEvents: Pointer[ze_event_handle_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zeCommandListAppendImageCopyRegion(
      hCommandList: ze_command_list_handle_t,
      hDstImage: ze_image_handle_t,
      hSrcImage: ze_image_handle_t,
      pDstRegion: Pointer[ze_image_region_t, Read, Opaque],
      pSrcRegion: Pointer[ze_image_region_t, Read, Opaque],
      hSignalEvent: ze_event_handle_t,
      numWaitEvents: uint32_t,
      phWaitEvents: Pointer[ze_event_handle_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zeCommandListAppendImageCopyToMemory(
      hCommandList: ze_command_list_handle_t,
      dstptr: OpaquePointer,
      hSrcImage: ze_image_handle_t,
      pSrcRegion: Pointer[ze_image_region_t, Read, Opaque],
      hSignalEvent: ze_event_handle_t,
      numWaitEvents: uint32_t,
      phWaitEvents: Pointer[ze_event_handle_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zeCommandListAppendImageCopyToMemoryExt(
      hCommandList: ze_command_list_handle_t,
      dstptr: OpaquePointer,
      hSrcImage: ze_image_handle_t,
      pSrcRegion: Pointer[ze_image_region_t, Read, Opaque],
      destRowPitch: uint32_t,
      destSlicePitch: uint32_t,
      hSignalEvent: ze_event_handle_t,
      numWaitEvents: uint32_t,
      phWaitEvents: Pointer[ze_event_handle_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zeCommandListAppendLaunchCooperativeKernel(
      hCommandList: ze_command_list_handle_t,
      hKernel: ze_kernel_handle_t,
      pLaunchFuncArgs: Pointer[ze_group_count_t, Read, Opaque],
      hSignalEvent: ze_event_handle_t,
      numWaitEvents: uint32_t,
      phWaitEvents: Pointer[ze_event_handle_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zeCommandListAppendLaunchKernel(
      hCommandList: ze_command_list_handle_t,
      hKernel: ze_kernel_handle_t,
      pLaunchFuncArgs: Pointer[ze_group_count_t, Read, Opaque],
      hSignalEvent: ze_event_handle_t,
      numWaitEvents: uint32_t,
      phWaitEvents: Pointer[ze_event_handle_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zeCommandListAppendLaunchKernelIndirect(
      hCommandList: ze_command_list_handle_t,
      hKernel: ze_kernel_handle_t,
      pLaunchArgumentsBuffer: Pointer[ze_group_count_t, Read, Opaque],
      hSignalEvent: ze_event_handle_t,
      numWaitEvents: uint32_t,
      phWaitEvents: Pointer[ze_event_handle_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zeCommandListAppendLaunchKernelWithArguments(
      hCommandList: ze_command_list_handle_t,
      hKernel: ze_kernel_handle_t,
      groupCounts: const_ze_group_count_t,
      groupSizes: const_ze_group_size_t,
      pArguments: Pointer[OpaquePointer, ReadWrite, Opaque],
      pNext: Pointer[CVoid, Read, Opaque],
      hSignalEvent: ze_event_handle_t,
      numWaitEvents: uint32_t,
      phWaitEvents: Pointer[ze_event_handle_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zeCommandListAppendLaunchKernelWithParameters(
      hCommandList: ze_command_list_handle_t,
      hKernel: ze_kernel_handle_t,
      pGroupCounts: Pointer[ze_group_count_t, Read, Opaque],
      pNext: Pointer[CVoid, Read, Opaque],
      hSignalEvent: ze_event_handle_t,
      numWaitEvents: uint32_t,
      phWaitEvents: Pointer[ze_event_handle_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zeCommandListAppendLaunchMultipleKernelsIndirect(
      hCommandList: ze_command_list_handle_t,
      numKernels: uint32_t,
      phKernels: Pointer[ze_kernel_handle_t, ReadWrite, Opaque],
      pCountBuffer: Pointer[uint32_t, Read, Opaque],
      pLaunchArgumentsBuffer: Pointer[ze_group_count_t, Read, Opaque],
      hSignalEvent: ze_event_handle_t,
      numWaitEvents: uint32_t,
      phWaitEvents: Pointer[ze_event_handle_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zeCommandListAppendMemAdvise(
      hCommandList: ze_command_list_handle_t,
      hDevice: ze_device_handle_t,
      ptr: Pointer[CVoid, Read, Opaque],
      size: size_t,
      advice: ze_memory_advice_t
  ): ze_result_t = unsupported
  def zeCommandListAppendMemoryCopy(
      hCommandList: ze_command_list_handle_t,
      dstptr: OpaquePointer,
      srcptr: Pointer[CVoid, Read, Opaque],
      size: size_t,
      hSignalEvent: ze_event_handle_t,
      numWaitEvents: uint32_t,
      phWaitEvents: Pointer[ze_event_handle_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zeCommandListAppendMemoryCopyFromContext(
      hCommandList: ze_command_list_handle_t,
      dstptr: OpaquePointer,
      hContextSrc: ze_context_handle_t,
      srcptr: Pointer[CVoid, Read, Opaque],
      size: size_t,
      hSignalEvent: ze_event_handle_t,
      numWaitEvents: uint32_t,
      phWaitEvents: Pointer[ze_event_handle_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zeCommandListAppendMemoryCopyRegion(
      hCommandList: ze_command_list_handle_t,
      dstptr: OpaquePointer,
      dstRegion: Pointer[ze_copy_region_t, Read, Opaque],
      dstPitch: uint32_t,
      dstSlicePitch: uint32_t,
      srcptr: Pointer[CVoid, Read, Opaque],
      srcRegion: Pointer[ze_copy_region_t, Read, Opaque],
      srcPitch: uint32_t,
      srcSlicePitch: uint32_t,
      hSignalEvent: ze_event_handle_t,
      numWaitEvents: uint32_t,
      phWaitEvents: Pointer[ze_event_handle_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zeCommandListAppendMemoryCopyWithParameters(
      hCommandList: ze_command_list_handle_t,
      dstptr: OpaquePointer,
      srcptr: Pointer[CVoid, Read, Opaque],
      size: size_t,
      pNext: Pointer[CVoid, Read, Opaque],
      hSignalEvent: ze_event_handle_t,
      numWaitEvents: uint32_t,
      phWaitEvents: Pointer[ze_event_handle_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zeCommandListAppendMemoryFill(
      hCommandList: ze_command_list_handle_t,
      ptr: OpaquePointer,
      pattern: Pointer[CVoid, Read, Opaque],
      pattern_size: size_t,
      size: size_t,
      hSignalEvent: ze_event_handle_t,
      numWaitEvents: uint32_t,
      phWaitEvents: Pointer[ze_event_handle_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zeCommandListAppendMemoryFillWithParameters(
      hCommandList: ze_command_list_handle_t,
      ptr: OpaquePointer,
      pattern: Pointer[CVoid, Read, Opaque],
      pattern_size: size_t,
      size: size_t,
      pNext: Pointer[CVoid, Read, Opaque],
      hSignalEvent: ze_event_handle_t,
      numWaitEvents: uint32_t,
      phWaitEvents: Pointer[ze_event_handle_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zeCommandListAppendMemoryPrefetch(
      hCommandList: ze_command_list_handle_t,
      ptr: Pointer[CVoid, Read, Opaque],
      size: size_t
  ): ze_result_t = unsupported
  def zeCommandListAppendMemoryRangesBarrier(
      hCommandList: ze_command_list_handle_t,
      numRanges: uint32_t,
      pRangeSizes: Pointer[size_t, Read, Opaque],
      pRanges: Pointer[OpaquePointer, Read, Opaque],
      hSignalEvent: ze_event_handle_t,
      numWaitEvents: uint32_t,
      phWaitEvents: Pointer[ze_event_handle_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zeCommandListAppendQueryKernelTimestamps(
      hCommandList: ze_command_list_handle_t,
      numEvents: uint32_t,
      phEvents: Pointer[ze_event_handle_t, ReadWrite, Opaque],
      dstptr: OpaquePointer,
      pOffsets: Pointer[size_t, Read, Opaque],
      hSignalEvent: ze_event_handle_t,
      numWaitEvents: uint32_t,
      phWaitEvents: Pointer[ze_event_handle_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zeCommandListAppendSignalEvent(hCommandList: ze_command_list_handle_t, hEvent: ze_event_handle_t): ze_result_t =
    unsupported
  def zeCommandListAppendSignalEventWithParameters(
      hCommandList: ze_command_list_handle_t,
      pNext: Pointer[CVoid, Read, Opaque],
      hEvent: ze_event_handle_t
  ): ze_result_t = unsupported
  def zeCommandListAppendSignalExternalSemaphoreExt(
      hCommandList: ze_command_list_handle_t,
      numSemaphores: uint32_t,
      phSemaphores: Pointer[ze_external_semaphore_ext_handle_t, ReadWrite, Opaque],
      signalParams: Pointer[ze_external_semaphore_signal_params_ext_t, ReadWrite, Opaque],
      hSignalEvent: ze_event_handle_t,
      numWaitEvents: uint32_t,
      phWaitEvents: Pointer[ze_event_handle_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zeCommandListAppendWaitExternalSemaphoreExt(
      hCommandList: ze_command_list_handle_t,
      numSemaphores: uint32_t,
      phSemaphores: Pointer[ze_external_semaphore_ext_handle_t, ReadWrite, Opaque],
      waitParams: Pointer[ze_external_semaphore_wait_params_ext_t, ReadWrite, Opaque],
      hSignalEvent: ze_event_handle_t,
      numWaitEvents: uint32_t,
      phWaitEvents: Pointer[ze_event_handle_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zeCommandListAppendWaitOnEvents(
      hCommandList: ze_command_list_handle_t,
      numEvents: uint32_t,
      phEvents: Pointer[ze_event_handle_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zeCommandListAppendWaitOnEventsWithParameters(
      hCommandList: ze_command_list_handle_t,
      pNext: Pointer[CVoid, Read, Opaque],
      numEvents: uint32_t,
      phEvents: Pointer[ze_event_handle_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zeCommandListAppendWriteGlobalTimestamp(
      hCommandList: ze_command_list_handle_t,
      dstptr: Pointer[uint64_t, ReadWrite, Opaque],
      hSignalEvent: ze_event_handle_t,
      numWaitEvents: uint32_t,
      phWaitEvents: Pointer[ze_event_handle_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zeCommandListBeginCaptureIntoGraphExt(
      hCommandList: ze_command_list_handle_t,
      hGraph: ze_graph_handle_t,
      pNext: Pointer[CVoid, Read, Opaque]
  ): ze_result_t = unsupported
  def zeCommandListBeginGraphCaptureExt(
      hCommandList: ze_command_list_handle_t,
      pNext: Pointer[CVoid, Read, Opaque]
  ): ze_result_t = unsupported
  def zeCommandListClose(hCommandList: ze_command_list_handle_t): ze_result_t = unsupported
  def zeCommandListCreate(
      hContext: ze_context_handle_t,
      hDevice: ze_device_handle_t,
      desc: Pointer[ze_command_list_desc_t, Read, Opaque],
      phCommandList: Pointer[ze_command_list_handle_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zeCommandListCreateCloneExp(
      hCommandList: ze_command_list_handle_t,
      phClonedCommandList: Pointer[ze_command_list_handle_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zeCommandListCreateImmediate(
      hContext: ze_context_handle_t,
      hDevice: ze_device_handle_t,
      altdesc: Pointer[ze_command_queue_desc_t, Read, Opaque],
      phCommandList: Pointer[ze_command_list_handle_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zeCommandListDestroy(hCommandList: ze_command_list_handle_t): ze_result_t = unsupported
  def zeCommandListEndGraphCaptureExt(
      hCommandList: ze_command_list_handle_t,
      pNext: Pointer[CVoid, Read, Opaque],
      phGraph: Pointer[ze_graph_handle_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zeCommandListGetContextHandle(
      hCommandList: ze_command_list_handle_t,
      phContext: Pointer[ze_context_handle_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zeCommandListGetDeviceHandle(
      hCommandList: ze_command_list_handle_t,
      phDevice: Pointer[ze_device_handle_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zeCommandListGetFlags(
      hCommandList: ze_command_list_handle_t,
      pFlags: Pointer[ze_command_list_flags_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zeCommandListGetGraphExt(
      hCommandList: ze_command_list_handle_t,
      phGraph: Pointer[ze_graph_handle_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zeCommandListGetNextCommandIdExp(
      hCommandList: ze_command_list_handle_t,
      desc: Pointer[ze_mutable_command_id_exp_desc_t, Read, Opaque],
      pCommandId: Pointer[uint64_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zeCommandListGetNextCommandIdWithKernelsExp(
      hCommandList: ze_command_list_handle_t,
      desc: Pointer[ze_mutable_command_id_exp_desc_t, Read, Opaque],
      numKernels: uint32_t,
      phKernels: Pointer[ze_kernel_handle_t, ReadWrite, Opaque],
      pCommandId: Pointer[uint64_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zeCommandListGetOrdinal(
      hCommandList: ze_command_list_handle_t,
      pOrdinal: Pointer[uint32_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zeCommandListHostSynchronize(hCommandList: ze_command_list_handle_t, timeout: uint64_t): ze_result_t = unsupported
  def zeCommandListImmediateAppendCommandListsExp(
      hCommandListImmediate: ze_command_list_handle_t,
      numCommandLists: uint32_t,
      phCommandLists: Pointer[ze_command_list_handle_t, ReadWrite, Opaque],
      hSignalEvent: ze_event_handle_t,
      numWaitEvents: uint32_t,
      phWaitEvents: Pointer[ze_event_handle_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zeCommandListImmediateAppendCommandListsWithParameters(
      hCommandListImmediate: ze_command_list_handle_t,
      numCommandLists: uint32_t,
      phCommandLists: Pointer[ze_command_list_handle_t, ReadWrite, Opaque],
      pNext: Pointer[CVoid, Read, Opaque],
      hSignalEvent: ze_event_handle_t,
      numWaitEvents: uint32_t,
      phWaitEvents: Pointer[ze_event_handle_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zeCommandListImmediateGetFlags(
      hCommandList: ze_command_list_handle_t,
      pFlags: Pointer[ze_command_queue_flags_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zeCommandListImmediateGetIndex(
      hCommandListImmediate: ze_command_list_handle_t,
      pIndex: Pointer[uint32_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zeCommandListImmediateGetMode(
      hCommandList: ze_command_list_handle_t,
      pMode: Pointer[ze_command_queue_mode_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zeCommandListImmediateGetPriority(
      hCommandList: ze_command_list_handle_t,
      pPriority: Pointer[ze_command_queue_priority_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zeCommandListIsGraphCaptureEnabledExt(hCommandList: ze_command_list_handle_t): ze_result_t = unsupported
  def zeCommandListIsImmediate(
      hCommandList: ze_command_list_handle_t,
      pIsImmediate: Pointer[ze_bool_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zeCommandListIsMutableExp(
      hCommandList: ze_command_list_handle_t,
      pIsMutable: Pointer[ze_bool_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zeCommandListReset(hCommandList: ze_command_list_handle_t): ze_result_t = unsupported
  def zeCommandListUpdateMutableCommandKernelsExp(
      hCommandList: ze_command_list_handle_t,
      numKernels: uint32_t,
      pCommandId: Pointer[uint64_t, ReadWrite, Opaque],
      phKernels: Pointer[ze_kernel_handle_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zeCommandListUpdateMutableCommandSignalEventExp(
      hCommandList: ze_command_list_handle_t,
      commandId: uint64_t,
      hSignalEvent: ze_event_handle_t
  ): ze_result_t = unsupported
  def zeCommandListUpdateMutableCommandWaitEventsExp(
      hCommandList: ze_command_list_handle_t,
      commandId: uint64_t,
      numWaitEvents: uint32_t,
      phWaitEvents: Pointer[ze_event_handle_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zeCommandListUpdateMutableCommandsExp(
      hCommandList: ze_command_list_handle_t,
      desc: Pointer[ze_mutable_commands_exp_desc_t, Read, Opaque]
  ): ze_result_t = unsupported
  def zeCommandQueueCreate(
      hContext: ze_context_handle_t,
      hDevice: ze_device_handle_t,
      desc: Pointer[ze_command_queue_desc_t, Read, Opaque],
      phCommandQueue: Pointer[ze_command_queue_handle_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zeCommandQueueDestroy(hCommandQueue: ze_command_queue_handle_t): ze_result_t = unsupported
  def zeCommandQueueExecuteCommandLists(
      hCommandQueue: ze_command_queue_handle_t,
      numCommandLists: uint32_t,
      phCommandLists: Pointer[ze_command_list_handle_t, ReadWrite, Opaque],
      hFence: ze_fence_handle_t
  ): ze_result_t = unsupported
  def zeCommandQueueGetFlags(
      hCmdQueue: ze_command_queue_handle_t,
      pFlags: Pointer[ze_command_queue_flags_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zeCommandQueueGetIndex(
      hCommandQueue: ze_command_queue_handle_t,
      pIndex: Pointer[uint32_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zeCommandQueueGetMode(
      hCmdQueue: ze_command_queue_handle_t,
      pMode: Pointer[ze_command_queue_mode_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zeCommandQueueGetOrdinal(
      hCommandQueue: ze_command_queue_handle_t,
      pOrdinal: Pointer[uint32_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zeCommandQueueGetPriority(
      hCmdQueue: ze_command_queue_handle_t,
      pPriority: Pointer[ze_command_queue_priority_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zeCommandQueueSetPriorityExt(
      hCommandQueue: ze_command_queue_handle_t,
      priority: ze_command_queue_priority_t
  ): ze_result_t = unsupported
  def zeCommandQueueSynchronize(hCommandQueue: ze_command_queue_handle_t, timeout: uint64_t): ze_result_t = unsupported
  def zeContextCreate(
      hDriver: ze_driver_handle_t,
      desc: Pointer[ze_context_desc_t, Read, Opaque],
      phContext: Pointer[ze_context_handle_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zeContextCreateEx(
      hDriver: ze_driver_handle_t,
      desc: Pointer[ze_context_desc_t, Read, Opaque],
      numDevices: uint32_t,
      phDevices: Pointer[ze_device_handle_t, ReadWrite, Opaque],
      phContext: Pointer[ze_context_handle_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zeContextDestroy(hContext: ze_context_handle_t): ze_result_t = unsupported
  def zeContextEvictImage(
      hContext: ze_context_handle_t,
      hDevice: ze_device_handle_t,
      hImage: ze_image_handle_t
  ): ze_result_t = unsupported
  def zeContextEvictMemory(
      hContext: ze_context_handle_t,
      hDevice: ze_device_handle_t,
      ptr: OpaquePointer,
      size: size_t
  ): ze_result_t = unsupported
  def zeContextGetStatus(hContext: ze_context_handle_t): ze_result_t = unsupported
  def zeContextMakeImageResident(
      hContext: ze_context_handle_t,
      hDevice: ze_device_handle_t,
      hImage: ze_image_handle_t
  ): ze_result_t = unsupported
  def zeContextMakeMemoryResident(
      hContext: ze_context_handle_t,
      hDevice: ze_device_handle_t,
      ptr: OpaquePointer,
      size: size_t
  ): ze_result_t = unsupported
  def zeContextSystemBarrier(hContext: ze_context_handle_t, hDevice: ze_device_handle_t): ze_result_t = unsupported
  def zeDeviceCanAccessPeer(
      hDevice: ze_device_handle_t,
      hPeerDevice: ze_device_handle_t,
      value: Pointer[ze_bool_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zeDeviceGet(
      hDriver: ze_driver_handle_t,
      pCount: Pointer[uint32_t, ReadWrite, Opaque],
      phDevices: Pointer[ze_device_handle_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zeDeviceGetAggregatedCopyOffloadIncrementValue(
      hDevice: ze_device_handle_t,
      incrementValue: Pointer[uint32_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zeDeviceGetCacheProperties(
      hDevice: ze_device_handle_t,
      pCount: Pointer[uint32_t, ReadWrite, Opaque],
      pCacheProperties: Pointer[ze_device_cache_properties_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zeDeviceGetCommandQueueGroupProperties(
      hDevice: ze_device_handle_t,
      pCount: Pointer[uint32_t, ReadWrite, Opaque],
      pCommandQueueGroupProperties: Pointer[ze_command_queue_group_properties_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zeDeviceGetCompilerInfo(
      hDevice: ze_device_handle_t,
      paramName: ze_device_compiler_info_t,
      pNext: Pointer[CVoid, Read, Opaque],
      pSize: Pointer[size_t, ReadWrite, Opaque],
      pData: OpaquePointer
  ): ze_result_t = unsupported
  def zeDeviceGetComputeProperties(
      hDevice: ze_device_handle_t,
      pComputeProperties: Pointer[ze_device_compute_properties_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zeDeviceGetCounterBasedEventMaxValue(
      hDevice: ze_device_handle_t,
      maxValue: Pointer[uint64_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zeDeviceGetExternalMemoryProperties(
      hDevice: ze_device_handle_t,
      pExternalMemoryProperties: Pointer[ze_device_external_memory_properties_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zeDeviceGetFabricVertexExp(
      hDevice: ze_device_handle_t,
      phVertex: Pointer[ze_fabric_vertex_handle_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zeDeviceGetGlobalTimestamps(
      hDevice: ze_device_handle_t,
      hostTimestamp: Pointer[uint64_t, ReadWrite, Opaque],
      deviceTimestamp: Pointer[uint64_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zeDeviceGetImageProperties(
      hDevice: ze_device_handle_t,
      pImageProperties: Pointer[ze_device_image_properties_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zeDeviceGetMemoryAccessProperties(
      hDevice: ze_device_handle_t,
      pMemAccessProperties: Pointer[ze_device_memory_access_properties_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zeDeviceGetMemoryProperties(
      hDevice: ze_device_handle_t,
      pCount: Pointer[uint32_t, ReadWrite, Opaque],
      pMemProperties: Pointer[ze_device_memory_properties_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zeDeviceGetModuleProperties(
      hDevice: ze_device_handle_t,
      pModuleProperties: Pointer[ze_device_module_properties_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zeDeviceGetP2PProperties(
      hDevice: ze_device_handle_t,
      hPeerDevice: ze_device_handle_t,
      pP2PProperties: Pointer[ze_device_p2p_properties_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zeDeviceGetProperties(
      hDevice: ze_device_handle_t,
      pDeviceProperties: Pointer[ze_device_properties_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zeDeviceGetRootDevice(
      hDevice: ze_device_handle_t,
      phRootDevice: Pointer[ze_device_handle_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zeDeviceGetRuntimeRequirements(
      hDevice: ze_device_handle_t,
      pObjDesc: Pointer[CVoid, Read, Opaque],
      pSize: Pointer[size_t, ReadWrite, Opaque],
      pRequirements: Pointer[Byte, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zeDeviceGetRuntimeRequirementsKey(
      hDevice: ze_device_handle_t,
      pKey: Pointer[Pointer[Byte, ReadWrite, Opaque], Read, Opaque]
  ): ze_result_t = unsupported
  def zeDeviceGetStatus(hDevice: ze_device_handle_t): ze_result_t = unsupported
  def zeDeviceGetSubDevices(
      hDevice: ze_device_handle_t,
      pCount: Pointer[uint32_t, ReadWrite, Opaque],
      phSubdevices: Pointer[ze_device_handle_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zeDeviceGetVectorWidthPropertiesExt(
      hDevice: ze_device_handle_t,
      pCount: Pointer[uint32_t, ReadWrite, Opaque],
      pVectorWidthProperties: Pointer[ze_device_vector_width_properties_ext_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zeDeviceImportExternalSemaphoreExt(
      hDevice: ze_device_handle_t,
      desc: Pointer[ze_external_semaphore_ext_desc_t, Read, Opaque],
      phSemaphore: Pointer[ze_external_semaphore_ext_handle_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zeDevicePciGetPropertiesExt(
      hDevice: ze_device_handle_t,
      pPciProperties: Pointer[ze_pci_ext_properties_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zeDeviceReleaseExternalSemaphoreExt(hSemaphore: ze_external_semaphore_ext_handle_t): ze_result_t = unsupported
  def zeDeviceReserveCacheExt(
      hDevice: ze_device_handle_t,
      cacheLevel: size_t,
      cacheReservationSize: size_t
  ): ze_result_t = unsupported
  def zeDeviceSetCacheAdviceExt(
      hDevice: ze_device_handle_t,
      ptr: OpaquePointer,
      regionSize: size_t,
      cacheRegion: ze_cache_ext_region_t
  ): ze_result_t = unsupported
  def zeDeviceSynchronize(hDevice: ze_device_handle_t): ze_result_t = unsupported
  def zeDeviceValidateRuntimeRequirements(
      hDevice: ze_device_handle_t,
      pRequirements: CString,
      pOut: Pointer[ze_validate_runtime_requirements_output_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zeDriverGet(
      pCount: Pointer[uint32_t, ReadWrite, Opaque],
      phDrivers: Pointer[ze_driver_handle_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zeDriverGetApiVersion(
      hDriver: ze_driver_handle_t,
      version: Pointer[ze_api_version_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zeDriverGetDefaultContext(hDriver: ze_driver_handle_t): ze_context_handle_t = unsupported
  def zeDriverGetExtensionFunctionAddress(
      hDriver: ze_driver_handle_t,
      name: CString,
      ppFunctionAddress: Pointer[OpaquePointer, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zeDriverGetExtensionProperties(
      hDriver: ze_driver_handle_t,
      pCount: Pointer[uint32_t, ReadWrite, Opaque],
      pExtensionProperties: Pointer[ze_driver_extension_properties_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zeDriverGetIpcProperties(
      hDriver: ze_driver_handle_t,
      pIpcProperties: Pointer[ze_driver_ipc_properties_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zeDriverGetLastErrorDescription(
      hDriver: ze_driver_handle_t,
      ppString: Pointer[Pointer[Byte, ReadWrite, Opaque], Read, Opaque]
  ): ze_result_t = unsupported
  def zeDriverGetProperties(
      hDriver: ze_driver_handle_t,
      pDriverProperties: Pointer[ze_driver_properties_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zeDriverRTASFormatCompatibilityCheckExp(
      hDriver: ze_driver_handle_t,
      rtasFormatA: ze_rtas_format_exp_t,
      rtasFormatB: ze_rtas_format_exp_t
  ): ze_result_t = unsupported
  def zeDriverRTASFormatCompatibilityCheckExt(
      hDriver: ze_driver_handle_t,
      rtasFormatA: ze_rtas_format_ext_t,
      rtasFormatB: ze_rtas_format_ext_t
  ): ze_result_t = unsupported
  def zeEventCounterBasedCloseIpcHandle(hEvent: ze_event_handle_t): ze_result_t = unsupported
  def zeEventCounterBasedCreate(
      hContext: ze_context_handle_t,
      hDevice: ze_device_handle_t,
      desc: Pointer[ze_event_counter_based_desc_t, Read, Opaque],
      phEvent: Pointer[ze_event_handle_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zeEventCounterBasedGetDeviceAddress(
      hEvent: ze_event_handle_t,
      completionValue: Pointer[uint64_t, ReadWrite, Opaque],
      deviceAddress: Pointer[uint64_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zeEventCounterBasedGetIpcHandle(
      hEvent: ze_event_handle_t,
      phIpc: Pointer[ze_ipc_event_counter_based_handle_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zeEventCounterBasedOpenIpcHandle(
      hContext: ze_context_handle_t,
      hIpc: ze_ipc_event_counter_based_handle_t,
      phEvent: Pointer[ze_event_handle_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zeEventCreate(
      hEventPool: ze_event_pool_handle_t,
      desc: Pointer[ze_event_desc_t, Read, Opaque],
      phEvent: Pointer[ze_event_handle_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zeEventDestroy(hEvent: ze_event_handle_t): ze_result_t = unsupported
  def zeEventGetCounterBasedFlags(
      hEvent: ze_event_handle_t,
      pFlags: Pointer[ze_event_counter_based_flags_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zeEventGetEventPool(
      hEvent: ze_event_handle_t,
      phEventPool: Pointer[ze_event_pool_handle_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zeEventGetSignalScope(
      hEvent: ze_event_handle_t,
      pSignalScope: Pointer[ze_event_scope_flags_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zeEventGetWaitScope(
      hEvent: ze_event_handle_t,
      pWaitScope: Pointer[ze_event_scope_flags_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zeEventHostReset(hEvent: ze_event_handle_t): ze_result_t = unsupported
  def zeEventHostSignal(hEvent: ze_event_handle_t): ze_result_t = unsupported
  def zeEventHostSynchronize(hEvent: ze_event_handle_t, timeout: uint64_t): ze_result_t = unsupported
  def zeEventPoolCloseIpcHandle(hEventPool: ze_event_pool_handle_t): ze_result_t = unsupported
  def zeEventPoolCreate(
      hContext: ze_context_handle_t,
      desc: Pointer[ze_event_pool_desc_t, Read, Opaque],
      numDevices: uint32_t,
      phDevices: Pointer[ze_device_handle_t, ReadWrite, Opaque],
      phEventPool: Pointer[ze_event_pool_handle_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zeEventPoolDestroy(hEventPool: ze_event_pool_handle_t): ze_result_t = unsupported
  def zeEventPoolGetContextHandle(
      hEventPool: ze_event_pool_handle_t,
      phContext: Pointer[ze_context_handle_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zeEventPoolGetFlags(
      hEventPool: ze_event_pool_handle_t,
      pFlags: Pointer[ze_event_pool_flags_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zeEventPoolGetIpcHandle(
      hEventPool: ze_event_pool_handle_t,
      phIpc: Pointer[ze_ipc_event_pool_handle_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zeEventPoolOpenIpcHandle(
      hContext: ze_context_handle_t,
      hIpc: ze_ipc_event_pool_handle_t,
      phEventPool: Pointer[ze_event_pool_handle_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zeEventPoolPutIpcHandle(hContext: ze_context_handle_t, hIpc: ze_ipc_event_pool_handle_t): ze_result_t =
    unsupported
  def zeEventQueryKernelTimestamp(
      hEvent: ze_event_handle_t,
      dstptr: Pointer[ze_kernel_timestamp_result_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zeEventQueryKernelTimestampsExt(
      hEvent: ze_event_handle_t,
      hDevice: ze_device_handle_t,
      pCount: Pointer[uint32_t, ReadWrite, Opaque],
      pResults: Pointer[ze_event_query_kernel_timestamps_results_ext_properties_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zeEventQueryStatus(hEvent: ze_event_handle_t): ze_result_t = unsupported
  def zeEventQueryTimestampsExp(
      hEvent: ze_event_handle_t,
      hDevice: ze_device_handle_t,
      pCount: Pointer[uint32_t, ReadWrite, Opaque],
      pTimestamps: Pointer[ze_kernel_timestamp_result_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zeExecutableGraphDestroyExt(hGraph: ze_executable_graph_handle_t): ze_result_t = unsupported
  def zeExecutableGraphGetSourceGraphExt(
      hGraph: ze_executable_graph_handle_t,
      phSourceGraph: Pointer[ze_graph_handle_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zeFabricEdgeGetExp(
      hVertexA: ze_fabric_vertex_handle_t,
      hVertexB: ze_fabric_vertex_handle_t,
      pCount: Pointer[uint32_t, ReadWrite, Opaque],
      phEdges: Pointer[ze_fabric_edge_handle_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zeFabricEdgeGetPropertiesExp(
      hEdge: ze_fabric_edge_handle_t,
      pEdgeProperties: Pointer[ze_fabric_edge_exp_properties_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zeFabricEdgeGetVerticesExp(
      hEdge: ze_fabric_edge_handle_t,
      phVertexA: Pointer[ze_fabric_vertex_handle_t, ReadWrite, Opaque],
      phVertexB: Pointer[ze_fabric_vertex_handle_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zeFabricVertexGetDeviceExp(
      hVertex: ze_fabric_vertex_handle_t,
      phDevice: Pointer[ze_device_handle_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zeFabricVertexGetExp(
      hDriver: ze_driver_handle_t,
      pCount: Pointer[uint32_t, ReadWrite, Opaque],
      phVertices: Pointer[ze_fabric_vertex_handle_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zeFabricVertexGetPropertiesExp(
      hVertex: ze_fabric_vertex_handle_t,
      pVertexProperties: Pointer[ze_fabric_vertex_exp_properties_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zeFabricVertexGetSubVerticesExp(
      hVertex: ze_fabric_vertex_handle_t,
      pCount: Pointer[uint32_t, ReadWrite, Opaque],
      phSubvertices: Pointer[ze_fabric_vertex_handle_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zeFenceCreate(
      hCommandQueue: ze_command_queue_handle_t,
      desc: Pointer[ze_fence_desc_t, Read, Opaque],
      phFence: Pointer[ze_fence_handle_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zeFenceDestroy(hFence: ze_fence_handle_t): ze_result_t = unsupported
  def zeFenceHostSynchronize(hFence: ze_fence_handle_t, timeout: uint64_t): ze_result_t = unsupported
  def zeFenceQueryStatus(hFence: ze_fence_handle_t): ze_result_t = unsupported
  def zeFenceReset(hFence: ze_fence_handle_t): ze_result_t = unsupported
  def zeGraphCreateExt(
      hContext: ze_context_handle_t,
      pNext: Pointer[CVoid, Read, Opaque],
      phGraph: Pointer[ze_graph_handle_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zeGraphDestroyExt(hGraph: ze_graph_handle_t): ze_result_t = unsupported
  def zeGraphDumpContentsExt(
      hGraph: ze_graph_handle_t,
      filePath: CString,
      pNext: Pointer[CVoid, Read, Opaque]
  ): ze_result_t = unsupported
  def zeGraphGetIdExt(hGraph: ze_graph_handle_t, pGraphId: Pointer[uint64_t, ReadWrite, Opaque]): ze_result_t =
    unsupported
  def zeGraphGetPrimaryCommandListExt(
      hGraph: ze_graph_handle_t,
      phCommandList: Pointer[ze_command_list_handle_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zeGraphInstantiateExt(
      hGraph: ze_graph_handle_t,
      pNext: Pointer[CVoid, Read, Opaque],
      phExecutableGraph: Pointer[ze_executable_graph_handle_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zeGraphIsEmptyExt(hGraph: ze_graph_handle_t): ze_result_t = unsupported
  def zeGraphPauseCaptureExt(hGraph: ze_graph_handle_t): ze_result_t = unsupported
  def zeGraphResumeCaptureExt(hGraph: ze_graph_handle_t): ze_result_t = unsupported
  def zeGraphSetDestructionCallbackExt(
      hGraph: ze_graph_handle_t,
      pfnCallback: zex_mem_graph_free_callback_fn_t,
      pUserData: OpaquePointer,
      pNext: Pointer[CVoid, Read, Opaque]
  ): ze_result_t = unsupported
  def zeImageCreate(
      hContext: ze_context_handle_t,
      hDevice: ze_device_handle_t,
      desc: Pointer[ze_image_desc_t, Read, Opaque],
      phImage: Pointer[ze_image_handle_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zeImageDestroy(hImage: ze_image_handle_t): ze_result_t = unsupported
  def zeImageGetAllocPropertiesExt(
      hContext: ze_context_handle_t,
      hImage: ze_image_handle_t,
      pImageAllocProperties: Pointer[ze_image_allocation_ext_properties_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zeImageGetDeviceOffsetExp(
      hImage: ze_image_handle_t,
      pDeviceOffset: Pointer[uint64_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zeImageGetMemoryPropertiesExp(
      hImage: ze_image_handle_t,
      pMemoryProperties: Pointer[ze_image_memory_properties_exp_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zeImageGetProperties(
      hDevice: ze_device_handle_t,
      desc: Pointer[ze_image_desc_t, Read, Opaque],
      pImageProperties: Pointer[ze_image_properties_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zeImageViewCreateExp(
      hContext: ze_context_handle_t,
      hDevice: ze_device_handle_t,
      desc: Pointer[ze_image_desc_t, Read, Opaque],
      hImage: ze_image_handle_t,
      phImageView: Pointer[ze_image_handle_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zeImageViewCreateExt(
      hContext: ze_context_handle_t,
      hDevice: ze_device_handle_t,
      desc: Pointer[ze_image_desc_t, Read, Opaque],
      hImage: ze_image_handle_t,
      phImageView: Pointer[ze_image_handle_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zeInit(flags: ze_init_flags_t): ze_result_t = unsupported
  def zeInitDrivers(
      pCount: Pointer[uint32_t, ReadWrite, Opaque],
      phDrivers: Pointer[ze_driver_handle_t, ReadWrite, Opaque],
      desc: Pointer[ze_init_driver_type_desc_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zeKernelCreate(
      hModule: ze_module_handle_t,
      desc: Pointer[ze_kernel_desc_t, Read, Opaque],
      phKernel: Pointer[ze_kernel_handle_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zeKernelDestroy(hKernel: ze_kernel_handle_t): ze_result_t = unsupported
  def zeKernelGetAllocationPropertiesExp(
      hKernel: ze_kernel_handle_t,
      pCount: Pointer[uint32_t, ReadWrite, Opaque],
      pAllocationProperties: Pointer[ze_kernel_allocation_exp_properties_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zeKernelGetBinaryExp(
      hKernel: ze_kernel_handle_t,
      pSize: Pointer[size_t, ReadWrite, Opaque],
      pKernelBinary: Pointer[uint8_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zeKernelGetIndirectAccess(
      hKernel: ze_kernel_handle_t,
      pFlags: Pointer[ze_kernel_indirect_access_flags_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zeKernelGetModuleHandle(
      hKernel: ze_kernel_handle_t,
      phModule: Pointer[ze_module_handle_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zeKernelGetName(
      hKernel: ze_kernel_handle_t,
      pSize: Pointer[size_t, ReadWrite, Opaque],
      pName: Pointer[Byte, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zeKernelGetProperties(
      hKernel: ze_kernel_handle_t,
      pKernelProperties: Pointer[ze_kernel_properties_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zeKernelGetSourceAttributes(
      hKernel: ze_kernel_handle_t,
      pSize: Pointer[uint32_t, ReadWrite, Opaque],
      pString: Pointer[Pointer[Byte, ReadWrite, Opaque], ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zeKernelSchedulingHintExp(
      hKernel: ze_kernel_handle_t,
      pHint: Pointer[ze_scheduling_hint_exp_desc_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zeKernelSetArgumentValue(
      hKernel: ze_kernel_handle_t,
      argIndex: uint32_t,
      argSize: size_t,
      pArgValue: Pointer[CVoid, Read, Opaque]
  ): ze_result_t = unsupported
  def zeKernelSetCacheConfig(hKernel: ze_kernel_handle_t, flags: ze_cache_config_flags_t): ze_result_t = unsupported
  def zeKernelSetGlobalOffsetExp(
      hKernel: ze_kernel_handle_t,
      offsetX: uint32_t,
      offsetY: uint32_t,
      offsetZ: uint32_t
  ): ze_result_t = unsupported
  def zeKernelSetGroupSize(
      hKernel: ze_kernel_handle_t,
      groupSizeX: uint32_t,
      groupSizeY: uint32_t,
      groupSizeZ: uint32_t
  ): ze_result_t = unsupported
  def zeKernelSetIndirectAccess(hKernel: ze_kernel_handle_t, flags: ze_kernel_indirect_access_flags_t): ze_result_t =
    unsupported
  def zeKernelSuggestGroupSize(
      hKernel: ze_kernel_handle_t,
      globalSizeX: uint32_t,
      globalSizeY: uint32_t,
      globalSizeZ: uint32_t,
      groupSizeX: Pointer[uint32_t, ReadWrite, Opaque],
      groupSizeY: Pointer[uint32_t, ReadWrite, Opaque],
      groupSizeZ: Pointer[uint32_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zeKernelSuggestMaxCooperativeGroupCount(
      hKernel: ze_kernel_handle_t,
      totalGroupCount: Pointer[uint32_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zeMemAllocDevice(
      hContext: ze_context_handle_t,
      device_desc: Pointer[ze_device_mem_alloc_desc_t, Read, Opaque],
      size: size_t,
      alignment: size_t,
      hDevice: ze_device_handle_t,
      pptr: Pointer[OpaquePointer, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zeMemAllocHost(
      hContext: ze_context_handle_t,
      host_desc: Pointer[ze_host_mem_alloc_desc_t, Read, Opaque],
      size: size_t,
      alignment: size_t,
      pptr: Pointer[OpaquePointer, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zeMemAllocShared(
      hContext: ze_context_handle_t,
      device_desc: Pointer[ze_device_mem_alloc_desc_t, Read, Opaque],
      host_desc: Pointer[ze_host_mem_alloc_desc_t, Read, Opaque],
      size: size_t,
      alignment: size_t,
      hDevice: ze_device_handle_t,
      pptr: Pointer[OpaquePointer, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zeMemCloseIpcHandle(hContext: ze_context_handle_t, ptr: Pointer[CVoid, Read, Opaque]): ze_result_t = unsupported
  def zeMemFree(hContext: ze_context_handle_t, ptr: OpaquePointer): ze_result_t = unsupported
  def zeMemFreeExt(
      hContext: ze_context_handle_t,
      pMemFreeDesc: Pointer[ze_memory_free_ext_desc_t, Read, Opaque],
      ptr: OpaquePointer
  ): ze_result_t = unsupported
  def zeMemGetAddressRange(
      hContext: ze_context_handle_t,
      ptr: Pointer[CVoid, Read, Opaque],
      pBase: Pointer[OpaquePointer, ReadWrite, Opaque],
      pSize: Pointer[size_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zeMemGetAllocProperties(
      hContext: ze_context_handle_t,
      ptr: Pointer[CVoid, Read, Opaque],
      pMemAllocProperties: Pointer[ze_memory_allocation_properties_t, ReadWrite, Opaque],
      phDevice: Pointer[ze_device_handle_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zeMemGetAtomicAccessAttributeExp(
      hContext: ze_context_handle_t,
      hDevice: ze_device_handle_t,
      ptr: Pointer[CVoid, Read, Opaque],
      size: size_t,
      pAttr: Pointer[ze_memory_atomic_attr_exp_flags_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zeMemGetFileDescriptorFromIpcHandleExp(
      hContext: ze_context_handle_t,
      ipcHandle: ze_ipc_mem_handle_t,
      pHandle: Pointer[uint64_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zeMemGetIpcHandle(
      hContext: ze_context_handle_t,
      ptr: Pointer[CVoid, Read, Opaque],
      pIpcHandle: Pointer[ze_ipc_mem_handle_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zeMemGetIpcHandleFromFileDescriptorExp(
      hContext: ze_context_handle_t,
      handle: uint64_t,
      pIpcHandle: Pointer[ze_ipc_mem_handle_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zeMemGetIpcHandleWithProperties(
      hContext: ze_context_handle_t,
      ptr: Pointer[CVoid, Read, Opaque],
      pNext: OpaquePointer,
      pIpcHandle: Pointer[ze_ipc_mem_handle_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zeMemGetPitchFor2dImage(
      hContext: ze_context_handle_t,
      hDevice: ze_device_handle_t,
      imageWidth: size_t,
      imageHeight: size_t,
      elementSizeInBytes: unsigned_int,
      rowPitch: Pointer[size_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zeMemOpenIpcHandle(
      hContext: ze_context_handle_t,
      hDevice: ze_device_handle_t,
      handle: ze_ipc_mem_handle_t,
      flags: ze_ipc_memory_flags_t,
      pptr: Pointer[OpaquePointer, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zeMemPutIpcHandle(hContext: ze_context_handle_t, handle: ze_ipc_mem_handle_t): ze_result_t = unsupported
  def zeMemSetAtomicAccessAttributeExp(
      hContext: ze_context_handle_t,
      hDevice: ze_device_handle_t,
      ptr: Pointer[CVoid, Read, Opaque],
      size: size_t,
      attr: ze_memory_atomic_attr_exp_flags_t
  ): ze_result_t = unsupported
  def zeModuleBuildLogDestroy(hModuleBuildLog: ze_module_build_log_handle_t): ze_result_t = unsupported
  def zeModuleBuildLogGetString(
      hModuleBuildLog: ze_module_build_log_handle_t,
      pSize: Pointer[size_t, ReadWrite, Opaque],
      pBuildLog: Pointer[Byte, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zeModuleCreate(
      hContext: ze_context_handle_t,
      hDevice: ze_device_handle_t,
      desc: Pointer[ze_module_desc_t, Read, Opaque],
      phModule: Pointer[ze_module_handle_t, ReadWrite, Opaque],
      phBuildLog: Pointer[ze_module_build_log_handle_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zeModuleDestroy(hModule: ze_module_handle_t): ze_result_t = unsupported
  def zeModuleDynamicLink(
      numModules: uint32_t,
      phModules: Pointer[ze_module_handle_t, ReadWrite, Opaque],
      phLinkLog: Pointer[ze_module_build_log_handle_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zeModuleGetDeviceHandle(
      hModule: ze_module_handle_t,
      phDevice: Pointer[ze_device_handle_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zeModuleGetFunctionPointer(
      hModule: ze_module_handle_t,
      pFunctionName: CString,
      pfnFunction: Pointer[OpaquePointer, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zeModuleGetGlobalPointer(
      hModule: ze_module_handle_t,
      pGlobalName: CString,
      pSize: Pointer[size_t, ReadWrite, Opaque],
      pptr: Pointer[OpaquePointer, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zeModuleGetKernelNames(
      hModule: ze_module_handle_t,
      pCount: Pointer[uint32_t, ReadWrite, Opaque],
      pNames: Pointer[Pointer[Byte, ReadWrite, Opaque], Read, Opaque]
  ): ze_result_t = unsupported
  def zeModuleGetNativeBinary(
      hModule: ze_module_handle_t,
      pSize: Pointer[size_t, ReadWrite, Opaque],
      pModuleNativeBinary: Pointer[uint8_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zeModuleGetProperties(
      hModule: ze_module_handle_t,
      pModuleProperties: Pointer[ze_module_properties_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zeModuleInspectLinkageExt(
      pInspectDesc: Pointer[ze_linkage_inspection_ext_desc_t, ReadWrite, Opaque],
      numModules: uint32_t,
      phModules: Pointer[ze_module_handle_t, ReadWrite, Opaque],
      phLog: Pointer[ze_module_build_log_handle_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zePhysicalMemCreate(
      hContext: ze_context_handle_t,
      hDevice: ze_device_handle_t,
      desc: Pointer[ze_physical_mem_desc_t, ReadWrite, Opaque],
      phPhysicalMemory: Pointer[ze_physical_mem_handle_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zePhysicalMemDestroy(hContext: ze_context_handle_t, hPhysicalMemory: ze_physical_mem_handle_t): ze_result_t =
    unsupported
  def zePhysicalMemGetProperties(
      hContext: ze_context_handle_t,
      hPhysicalMem: ze_physical_mem_handle_t,
      pMemProperties: Pointer[ze_physical_mem_properties_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zeRTASBuilderBuildExp(
      hBuilder: ze_rtas_builder_exp_handle_t,
      pBuildOpDescriptor: Pointer[ze_rtas_builder_build_op_exp_desc_t, Read, Opaque],
      pScratchBuffer: OpaquePointer,
      scratchBufferSizeBytes: size_t,
      pRtasBuffer: OpaquePointer,
      rtasBufferSizeBytes: size_t,
      hParallelOperation: ze_rtas_parallel_operation_exp_handle_t,
      pBuildUserPtr: OpaquePointer,
      pBounds: Pointer[ze_rtas_aabb_exp_t, ReadWrite, Opaque],
      pRtasBufferSizeBytes: Pointer[size_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zeRTASBuilderBuildExt(
      hBuilder: ze_rtas_builder_ext_handle_t,
      pBuildOpDescriptor: Pointer[ze_rtas_builder_build_op_ext_desc_t, Read, Opaque],
      pScratchBuffer: OpaquePointer,
      scratchBufferSizeBytes: size_t,
      pRtasBuffer: OpaquePointer,
      rtasBufferSizeBytes: size_t,
      hParallelOperation: ze_rtas_parallel_operation_ext_handle_t,
      pBuildUserPtr: OpaquePointer,
      pBounds: Pointer[ze_rtas_aabb_ext_t, ReadWrite, Opaque],
      pRtasBufferSizeBytes: Pointer[size_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zeRTASBuilderCommandListAppendCopyExt(
      hCommandList: ze_command_list_handle_t,
      dstptr: OpaquePointer,
      srcptr: Pointer[CVoid, Read, Opaque],
      size: size_t,
      hSignalEvent: ze_event_handle_t,
      numWaitEvents: uint32_t,
      phWaitEvents: Pointer[ze_event_handle_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zeRTASBuilderCreateExp(
      hDriver: ze_driver_handle_t,
      pDescriptor: Pointer[ze_rtas_builder_exp_desc_t, Read, Opaque],
      phBuilder: Pointer[ze_rtas_builder_exp_handle_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zeRTASBuilderCreateExt(
      hDriver: ze_driver_handle_t,
      pDescriptor: Pointer[ze_rtas_builder_ext_desc_t, Read, Opaque],
      phBuilder: Pointer[ze_rtas_builder_ext_handle_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zeRTASBuilderDestroyExp(hBuilder: ze_rtas_builder_exp_handle_t): ze_result_t = unsupported
  def zeRTASBuilderDestroyExt(hBuilder: ze_rtas_builder_ext_handle_t): ze_result_t = unsupported
  def zeRTASBuilderGetBuildPropertiesExp(
      hBuilder: ze_rtas_builder_exp_handle_t,
      pBuildOpDescriptor: Pointer[ze_rtas_builder_build_op_exp_desc_t, Read, Opaque],
      pProperties: Pointer[ze_rtas_builder_exp_properties_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zeRTASBuilderGetBuildPropertiesExt(
      hBuilder: ze_rtas_builder_ext_handle_t,
      pBuildOpDescriptor: Pointer[ze_rtas_builder_build_op_ext_desc_t, Read, Opaque],
      pProperties: Pointer[ze_rtas_builder_ext_properties_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zeRTASParallelOperationCreateExp(
      hDriver: ze_driver_handle_t,
      phParallelOperation: Pointer[ze_rtas_parallel_operation_exp_handle_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zeRTASParallelOperationCreateExt(
      hDriver: ze_driver_handle_t,
      phParallelOperation: Pointer[ze_rtas_parallel_operation_ext_handle_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zeRTASParallelOperationDestroyExp(hParallelOperation: ze_rtas_parallel_operation_exp_handle_t): ze_result_t =
    unsupported
  def zeRTASParallelOperationDestroyExt(hParallelOperation: ze_rtas_parallel_operation_ext_handle_t): ze_result_t =
    unsupported
  def zeRTASParallelOperationGetPropertiesExp(
      hParallelOperation: ze_rtas_parallel_operation_exp_handle_t,
      pProperties: Pointer[ze_rtas_parallel_operation_exp_properties_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zeRTASParallelOperationGetPropertiesExt(
      hParallelOperation: ze_rtas_parallel_operation_ext_handle_t,
      pProperties: Pointer[ze_rtas_parallel_operation_ext_properties_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zeRTASParallelOperationJoinExp(hParallelOperation: ze_rtas_parallel_operation_exp_handle_t): ze_result_t =
    unsupported
  def zeRTASParallelOperationJoinExt(hParallelOperation: ze_rtas_parallel_operation_ext_handle_t): ze_result_t =
    unsupported
  def zeSamplerCreate(
      hContext: ze_context_handle_t,
      hDevice: ze_device_handle_t,
      desc: Pointer[ze_sampler_desc_t, Read, Opaque],
      phSampler: Pointer[ze_sampler_handle_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zeSamplerDestroy(hSampler: ze_sampler_handle_t): ze_result_t = unsupported
  def zeVirtualMemFree(hContext: ze_context_handle_t, ptr: Pointer[CVoid, Read, Opaque], size: size_t): ze_result_t =
    unsupported
  def zeVirtualMemGetAccessAttribute(
      hContext: ze_context_handle_t,
      ptr: Pointer[CVoid, Read, Opaque],
      size: size_t,
      access: Pointer[ze_memory_access_attribute_t, ReadWrite, Opaque],
      outSize: Pointer[size_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zeVirtualMemMap(
      hContext: ze_context_handle_t,
      ptr: Pointer[CVoid, Read, Opaque],
      size: size_t,
      hPhysicalMemory: ze_physical_mem_handle_t,
      offset: size_t,
      access: ze_memory_access_attribute_t
  ): ze_result_t = unsupported
  def zeVirtualMemQueryPageSize(
      hContext: ze_context_handle_t,
      hDevice: ze_device_handle_t,
      size: size_t,
      pagesize: Pointer[size_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zeVirtualMemReserve(
      hContext: ze_context_handle_t,
      pStart: Pointer[CVoid, Read, Opaque],
      size: size_t,
      pptr: Pointer[OpaquePointer, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zeVirtualMemSetAccessAttribute(
      hContext: ze_context_handle_t,
      ptr: Pointer[CVoid, Read, Opaque],
      size: size_t,
      access: ze_memory_access_attribute_t
  ): ze_result_t = unsupported
  def zeVirtualMemUnmap(hContext: ze_context_handle_t, ptr: Pointer[CVoid, Read, Opaque], size: size_t): ze_result_t =
    unsupported
  def zesDeviceEccAvailable(
      hDevice: zes_device_handle_t,
      pAvailable: Pointer[ze_bool_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zesDeviceEccConfigurable(
      hDevice: zes_device_handle_t,
      pConfigurable: Pointer[ze_bool_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zesDeviceEnumActiveVFExp(
      hDevice: zes_device_handle_t,
      pCount: Pointer[uint32_t, ReadWrite, Opaque],
      phVFhandle: Pointer[zes_vf_handle_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zesDeviceEnumDiagnosticTestSuites(
      hDevice: zes_device_handle_t,
      pCount: Pointer[uint32_t, ReadWrite, Opaque],
      phDiagnostics: Pointer[zes_diag_handle_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zesDeviceEnumEnabledVFExp(
      hDevice: zes_device_handle_t,
      pCount: Pointer[uint32_t, ReadWrite, Opaque],
      phVFhandle: Pointer[zes_vf_handle_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zesDeviceEnumEngineGroups(
      hDevice: zes_device_handle_t,
      pCount: Pointer[uint32_t, ReadWrite, Opaque],
      phEngine: Pointer[zes_engine_handle_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zesDeviceEnumFabricPorts(
      hDevice: zes_device_handle_t,
      pCount: Pointer[uint32_t, ReadWrite, Opaque],
      phPort: Pointer[zes_fabric_port_handle_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zesDeviceEnumFans(
      hDevice: zes_device_handle_t,
      pCount: Pointer[uint32_t, ReadWrite, Opaque],
      phFan: Pointer[zes_fan_handle_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zesDeviceEnumFirmwares(
      hDevice: zes_device_handle_t,
      pCount: Pointer[uint32_t, ReadWrite, Opaque],
      phFirmware: Pointer[zes_firmware_handle_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zesDeviceEnumFrequencyDomains(
      hDevice: zes_device_handle_t,
      pCount: Pointer[uint32_t, ReadWrite, Opaque],
      phFrequency: Pointer[zes_freq_handle_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zesDeviceEnumLeds(
      hDevice: zes_device_handle_t,
      pCount: Pointer[uint32_t, ReadWrite, Opaque],
      phLed: Pointer[zes_led_handle_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zesDeviceEnumMemoryModules(
      hDevice: zes_device_handle_t,
      pCount: Pointer[uint32_t, ReadWrite, Opaque],
      phMemory: Pointer[zes_mem_handle_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zesDeviceEnumOverclockDomains(
      hDevice: zes_device_handle_t,
      pCount: Pointer[uint32_t, ReadWrite, Opaque],
      phDomainHandle: Pointer[zes_overclock_handle_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zesDeviceEnumPerformanceFactorDomains(
      hDevice: zes_device_handle_t,
      pCount: Pointer[uint32_t, ReadWrite, Opaque],
      phPerf: Pointer[zes_perf_handle_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zesDeviceEnumPowerDomains(
      hDevice: zes_device_handle_t,
      pCount: Pointer[uint32_t, ReadWrite, Opaque],
      phPower: Pointer[zes_pwr_handle_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zesDeviceEnumPsus(
      hDevice: zes_device_handle_t,
      pCount: Pointer[uint32_t, ReadWrite, Opaque],
      phPsu: Pointer[zes_psu_handle_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zesDeviceEnumRasErrorSets(
      hDevice: zes_device_handle_t,
      pCount: Pointer[uint32_t, ReadWrite, Opaque],
      phRas: Pointer[zes_ras_handle_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zesDeviceEnumSchedulers(
      hDevice: zes_device_handle_t,
      pCount: Pointer[uint32_t, ReadWrite, Opaque],
      phScheduler: Pointer[zes_sched_handle_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zesDeviceEnumStandbyDomains(
      hDevice: zes_device_handle_t,
      pCount: Pointer[uint32_t, ReadWrite, Opaque],
      phStandby: Pointer[zes_standby_handle_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zesDeviceEnumTemperatureSensors(
      hDevice: zes_device_handle_t,
      pCount: Pointer[uint32_t, ReadWrite, Opaque],
      phTemperature: Pointer[zes_temp_handle_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zesDeviceEventRegister(hDevice: zes_device_handle_t, events: zes_event_type_flags_t): ze_result_t = unsupported
  def zesDeviceGet(
      hDriver: zes_driver_handle_t,
      pCount: Pointer[uint32_t, ReadWrite, Opaque],
      phDevices: Pointer[zes_device_handle_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zesDeviceGetCardPowerDomain(
      hDevice: zes_device_handle_t,
      phPower: Pointer[zes_pwr_handle_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zesDeviceGetEccState(
      hDevice: zes_device_handle_t,
      pState: Pointer[zes_device_ecc_properties_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zesDeviceGetHealthStatusExt(
      hDevice: zes_device_handle_t,
      pHealth: Pointer[zes_device_health_status_ext_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zesDeviceGetOverclockControls(
      hDevice: zes_device_handle_t,
      domainType: zes_overclock_domain_t,
      pAvailableControls: Pointer[uint32_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zesDeviceGetOverclockDomains(
      hDevice: zes_device_handle_t,
      pOverclockDomains: Pointer[uint32_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zesDeviceGetProperties(
      hDevice: zes_device_handle_t,
      pProperties: Pointer[zes_device_properties_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zesDeviceGetState(
      hDevice: zes_device_handle_t,
      pState: Pointer[zes_device_state_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zesDeviceGetSubDevicePropertiesExp(
      hDevice: zes_device_handle_t,
      pCount: Pointer[uint32_t, ReadWrite, Opaque],
      pSubdeviceProps: Pointer[zes_subdevice_exp_properties_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zesDevicePciGetBars(
      hDevice: zes_device_handle_t,
      pCount: Pointer[uint32_t, ReadWrite, Opaque],
      pProperties: Pointer[zes_pci_bar_properties_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zesDevicePciGetProperties(
      hDevice: zes_device_handle_t,
      pProperties: Pointer[zes_pci_properties_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zesDevicePciGetState(
      hDevice: zes_device_handle_t,
      pState: Pointer[zes_pci_state_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zesDevicePciGetStats(
      hDevice: zes_device_handle_t,
      pStats: Pointer[zes_pci_stats_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zesDevicePciLinkSpeedUpdateExt(
      hDevice: zes_device_handle_t,
      shouldDowngrade: ze_bool_t,
      pendingAction: Pointer[zes_device_action_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zesDeviceProcessesGetState(
      hDevice: zes_device_handle_t,
      pCount: Pointer[uint32_t, ReadWrite, Opaque],
      pProcesses: Pointer[zes_process_state_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zesDeviceReadOverclockState(
      hDevice: zes_device_handle_t,
      pOverclockMode: Pointer[zes_overclock_mode_t, ReadWrite, Opaque],
      pWaiverSetting: Pointer[ze_bool_t, ReadWrite, Opaque],
      pOverclockState: Pointer[ze_bool_t, ReadWrite, Opaque],
      pPendingAction: Pointer[zes_pending_action_t, ReadWrite, Opaque],
      pPendingReset: Pointer[ze_bool_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zesDeviceReset(hDevice: zes_device_handle_t, force: ze_bool_t): ze_result_t = unsupported
  def zesDeviceResetExt(
      hDevice: zes_device_handle_t,
      pProperties: Pointer[zes_reset_properties_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zesDeviceResetOverclockSettings(hDevice: zes_device_handle_t, onShippedState: ze_bool_t): ze_result_t =
    unsupported
  def zesDeviceSetEccState(
      hDevice: zes_device_handle_t,
      newState: Pointer[zes_device_ecc_desc_t, Read, Opaque],
      pState: Pointer[zes_device_ecc_properties_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zesDeviceSetHealthStatusExt(hDevice: zes_device_handle_t, health: zes_device_health_status_ext_t): ze_result_t =
    unsupported
  def zesDeviceSetOverclockWaiver(hDevice: zes_device_handle_t): ze_result_t = unsupported
  def zesDiagnosticsGetProperties(
      hDiagnostics: zes_diag_handle_t,
      pProperties: Pointer[zes_diag_properties_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zesDiagnosticsGetTests(
      hDiagnostics: zes_diag_handle_t,
      pCount: Pointer[uint32_t, ReadWrite, Opaque],
      pTests: Pointer[zes_diag_test_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zesDiagnosticsRunTests(
      hDiagnostics: zes_diag_handle_t,
      startIndex: uint32_t,
      endIndex: uint32_t,
      pResult: Pointer[zes_diag_result_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zesDriverEventListen(
      hDriver: ze_driver_handle_t,
      timeout: uint32_t,
      count: uint32_t,
      phDevices: Pointer[zes_device_handle_t, ReadWrite, Opaque],
      pNumDeviceEvents: Pointer[uint32_t, ReadWrite, Opaque],
      pEvents: Pointer[zes_event_type_flags_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zesDriverEventListenEx(
      hDriver: ze_driver_handle_t,
      timeout: uint64_t,
      count: uint32_t,
      phDevices: Pointer[zes_device_handle_t, ReadWrite, Opaque],
      pNumDeviceEvents: Pointer[uint32_t, ReadWrite, Opaque],
      pEvents: Pointer[zes_event_type_flags_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zesDriverGet(
      pCount: Pointer[uint32_t, ReadWrite, Opaque],
      phDrivers: Pointer[zes_driver_handle_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zesDriverGetDeviceByUuidExp(
      hDriver: zes_driver_handle_t,
      uuid: zes_uuid_t,
      phDevice: Pointer[zes_device_handle_t, ReadWrite, Opaque],
      onSubdevice: Pointer[ze_bool_t, ReadWrite, Opaque],
      subdeviceId: Pointer[uint32_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zesDriverGetExtensionFunctionAddress(
      hDriver: zes_driver_handle_t,
      name: CString,
      ppFunctionAddress: Pointer[OpaquePointer, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zesDriverGetExtensionProperties(
      hDriver: zes_driver_handle_t,
      pCount: Pointer[uint32_t, ReadWrite, Opaque],
      pExtensionProperties: Pointer[zes_driver_extension_properties_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zesEngineGetActivity(
      hEngine: zes_engine_handle_t,
      pStats: Pointer[zes_engine_stats_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zesEngineGetActivityExt(
      hEngine: zes_engine_handle_t,
      pCount: Pointer[uint32_t, ReadWrite, Opaque],
      pStats: Pointer[zes_engine_stats_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zesEngineGetProperties(
      hEngine: zes_engine_handle_t,
      pProperties: Pointer[zes_engine_properties_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zesFabricPortGetConfig(
      hPort: zes_fabric_port_handle_t,
      pConfig: Pointer[zes_fabric_port_config_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zesFabricPortGetFabricErrorCounters(
      hPort: zes_fabric_port_handle_t,
      pErrors: Pointer[zes_fabric_port_error_counters_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zesFabricPortGetLinkType(
      hPort: zes_fabric_port_handle_t,
      pLinkType: Pointer[zes_fabric_link_type_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zesFabricPortGetMultiPortThroughput(
      hDevice: zes_device_handle_t,
      numPorts: uint32_t,
      phPort: Pointer[zes_fabric_port_handle_t, ReadWrite, Opaque],
      pThroughput: Pointer[Pointer[zes_fabric_port_throughput_t, ReadWrite, Opaque], ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zesFabricPortGetProperties(
      hPort: zes_fabric_port_handle_t,
      pProperties: Pointer[zes_fabric_port_properties_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zesFabricPortGetState(
      hPort: zes_fabric_port_handle_t,
      pState: Pointer[zes_fabric_port_state_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zesFabricPortGetThroughput(
      hPort: zes_fabric_port_handle_t,
      pThroughput: Pointer[zes_fabric_port_throughput_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zesFabricPortSetConfig(
      hPort: zes_fabric_port_handle_t,
      pConfig: Pointer[zes_fabric_port_config_t, Read, Opaque]
  ): ze_result_t = unsupported
  def zesFanGetConfig(hFan: zes_fan_handle_t, pConfig: Pointer[zes_fan_config_t, ReadWrite, Opaque]): ze_result_t =
    unsupported
  def zesFanGetProperties(
      hFan: zes_fan_handle_t,
      pProperties: Pointer[zes_fan_properties_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zesFanGetState(
      hFan: zes_fan_handle_t,
      units: zes_fan_speed_units_t,
      pSpeed: Pointer[int32_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zesFanSetDefaultMode(hFan: zes_fan_handle_t): ze_result_t = unsupported
  def zesFanSetFixedSpeedMode(hFan: zes_fan_handle_t, speed: Pointer[zes_fan_speed_t, Read, Opaque]): ze_result_t =
    unsupported
  def zesFanSetSpeedTableMode(
      hFan: zes_fan_handle_t,
      speedTable: Pointer[zes_fan_speed_table_t, Read, Opaque]
  ): ze_result_t = unsupported
  def zesFirmwareFlash(hFirmware: zes_firmware_handle_t, pImage: OpaquePointer, size: uint32_t): ze_result_t =
    unsupported
  def zesFirmwareGetConsoleLogs(
      hFirmware: zes_firmware_handle_t,
      pSize: Pointer[size_t, ReadWrite, Opaque],
      pFirmwareLog: Pointer[Byte, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zesFirmwareGetFlashProgress(
      hFirmware: zes_firmware_handle_t,
      pCompletionPercent: Pointer[uint32_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zesFirmwareGetProperties(
      hFirmware: zes_firmware_handle_t,
      pProperties: Pointer[zes_firmware_properties_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zesFirmwareGetSecurityVersionExp(
      hFirmware: zes_firmware_handle_t,
      pVersion: Pointer[Byte, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zesFirmwareSetSecurityVersionExp(hFirmware: zes_firmware_handle_t): ze_result_t = unsupported
  def zesFrequencyGetAvailableClocks(
      hFrequency: zes_freq_handle_t,
      pCount: Pointer[uint32_t, ReadWrite, Opaque],
      phFrequency: Pointer[Double, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zesFrequencyGetProperties(
      hFrequency: zes_freq_handle_t,
      pProperties: Pointer[zes_freq_properties_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zesFrequencyGetRange(
      hFrequency: zes_freq_handle_t,
      pLimits: Pointer[zes_freq_range_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zesFrequencyGetState(
      hFrequency: zes_freq_handle_t,
      pState: Pointer[zes_freq_state_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zesFrequencyGetThrottleTime(
      hFrequency: zes_freq_handle_t,
      pThrottleTime: Pointer[zes_freq_throttle_time_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zesFrequencyOcGetCapabilities(
      hFrequency: zes_freq_handle_t,
      pOcCapabilities: Pointer[zes_oc_capabilities_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zesFrequencyOcGetFrequencyTarget(
      hFrequency: zes_freq_handle_t,
      pCurrentOcFrequency: Pointer[Double, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zesFrequencyOcGetIccMax(
      hFrequency: zes_freq_handle_t,
      pOcIccMax: Pointer[Double, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zesFrequencyOcGetMode(
      hFrequency: zes_freq_handle_t,
      pCurrentOcMode: Pointer[zes_oc_mode_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zesFrequencyOcGetTjMax(hFrequency: zes_freq_handle_t, pOcTjMax: Pointer[Double, ReadWrite, Opaque]): ze_result_t =
    unsupported
  def zesFrequencyOcGetVoltageTarget(
      hFrequency: zes_freq_handle_t,
      pCurrentVoltageTarget: Pointer[Double, ReadWrite, Opaque],
      pCurrentVoltageOffset: Pointer[Double, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zesFrequencyOcSetFrequencyTarget(hFrequency: zes_freq_handle_t, CurrentOcFrequency: Double): ze_result_t =
    unsupported
  def zesFrequencyOcSetIccMax(hFrequency: zes_freq_handle_t, ocIccMax: Double): ze_result_t = unsupported
  def zesFrequencyOcSetMode(hFrequency: zes_freq_handle_t, CurrentOcMode: zes_oc_mode_t): ze_result_t = unsupported
  def zesFrequencyOcSetTjMax(hFrequency: zes_freq_handle_t, ocTjMax: Double): ze_result_t = unsupported
  def zesFrequencyOcSetVoltageTarget(
      hFrequency: zes_freq_handle_t,
      CurrentVoltageTarget: Double,
      CurrentVoltageOffset: Double
  ): ze_result_t = unsupported
  def zesFrequencySetRange(
      hFrequency: zes_freq_handle_t,
      pLimits: Pointer[zes_freq_range_t, Read, Opaque]
  ): ze_result_t = unsupported
  def zesInit(flags: zes_init_flags_t): ze_result_t = unsupported
  def zesLedGetProperties(
      hLed: zes_led_handle_t,
      pProperties: Pointer[zes_led_properties_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zesLedGetState(hLed: zes_led_handle_t, pState: Pointer[zes_led_state_t, ReadWrite, Opaque]): ze_result_t =
    unsupported
  def zesLedSetColor(hLed: zes_led_handle_t, pColor: Pointer[zes_led_color_t, Read, Opaque]): ze_result_t = unsupported
  def zesLedSetState(hLed: zes_led_handle_t, enable: ze_bool_t): ze_result_t = unsupported
  def zesMemoryGetBandwidth(
      hMemory: zes_mem_handle_t,
      pBandwidth: Pointer[zes_mem_bandwidth_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zesMemoryGetProperties(
      hMemory: zes_mem_handle_t,
      pProperties: Pointer[zes_mem_properties_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zesMemoryGetState(hMemory: zes_mem_handle_t, pState: Pointer[zes_mem_state_t, ReadWrite, Opaque]): ze_result_t =
    unsupported
  def zesOverclockGetControlCurrentValue(
      hDomainHandle: zes_overclock_handle_t,
      DomainControl: zes_overclock_control_t,
      pValue: Pointer[Double, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zesOverclockGetControlPendingValue(
      hDomainHandle: zes_overclock_handle_t,
      DomainControl: zes_overclock_control_t,
      pValue: Pointer[Double, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zesOverclockGetControlState(
      hDomainHandle: zes_overclock_handle_t,
      DomainControl: zes_overclock_control_t,
      pControlState: Pointer[zes_control_state_t, ReadWrite, Opaque],
      pPendingAction: Pointer[zes_pending_action_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zesOverclockGetDomainControlProperties(
      hDomainHandle: zes_overclock_handle_t,
      DomainControl: zes_overclock_control_t,
      pControlProperties: Pointer[zes_control_property_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zesOverclockGetDomainProperties(
      hDomainHandle: zes_overclock_handle_t,
      pDomainProperties: Pointer[zes_overclock_properties_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zesOverclockGetDomainVFProperties(
      hDomainHandle: zes_overclock_handle_t,
      pVFProperties: Pointer[zes_vf_property_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zesOverclockGetVFPointValues(
      hDomainHandle: zes_overclock_handle_t,
      VFType: zes_vf_type_t,
      VFArrayType: zes_vf_array_type_t,
      PointIndex: uint32_t,
      PointValue: Pointer[uint32_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zesOverclockSetControlUserValue(
      hDomainHandle: zes_overclock_handle_t,
      DomainControl: zes_overclock_control_t,
      pValue: Double,
      pPendingAction: Pointer[zes_pending_action_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zesOverclockSetVFPointValues(
      hDomainHandle: zes_overclock_handle_t,
      VFType: zes_vf_type_t,
      PointIndex: uint32_t,
      PointValue: uint32_t
  ): ze_result_t = unsupported
  def zesPerformanceFactorGetConfig(
      hPerf: zes_perf_handle_t,
      pFactor: Pointer[Double, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zesPerformanceFactorGetProperties(
      hPerf: zes_perf_handle_t,
      pProperties: Pointer[zes_perf_properties_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zesPerformanceFactorSetConfig(hPerf: zes_perf_handle_t, factor: Double): ze_result_t = unsupported
  def zesPowerGetEnergyCounter(
      hPower: zes_pwr_handle_t,
      pEnergy: Pointer[zes_power_energy_counter_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zesPowerGetEnergyThreshold(
      hPower: zes_pwr_handle_t,
      pThreshold: Pointer[zes_energy_threshold_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zesPowerGetLimits(
      hPower: zes_pwr_handle_t,
      pSustained: Pointer[zes_power_sustained_limit_t, ReadWrite, Opaque],
      pBurst: Pointer[zes_power_burst_limit_t, ReadWrite, Opaque],
      pPeak: Pointer[zes_power_peak_limit_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zesPowerGetLimitsExt(
      hPower: zes_pwr_handle_t,
      pCount: Pointer[uint32_t, ReadWrite, Opaque],
      pSustained: Pointer[zes_power_limit_ext_desc_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zesPowerGetLimitsExt2(hPower: zes_pwr_handle_t, pLimit: Pointer[uint32_t, ReadWrite, Opaque]): ze_result_t =
    unsupported
  def zesPowerGetProperties(
      hPower: zes_pwr_handle_t,
      pProperties: Pointer[zes_power_properties_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zesPowerGetUsage(
      hPower: zes_pwr_handle_t,
      pInstantPower: Pointer[uint32_t, ReadWrite, Opaque],
      pAveragePower: Pointer[uint32_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zesPowerSetEnergyThreshold(hPower: zes_pwr_handle_t, threshold: Double): ze_result_t = unsupported
  def zesPowerSetLimits(
      hPower: zes_pwr_handle_t,
      pSustained: Pointer[zes_power_sustained_limit_t, Read, Opaque],
      pBurst: Pointer[zes_power_burst_limit_t, Read, Opaque],
      pPeak: Pointer[zes_power_peak_limit_t, Read, Opaque]
  ): ze_result_t = unsupported
  def zesPowerSetLimitsExt(
      hPower: zes_pwr_handle_t,
      pCount: Pointer[uint32_t, ReadWrite, Opaque],
      pSustained: Pointer[zes_power_limit_ext_desc_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zesPowerSetLimitsExt2(hPower: zes_pwr_handle_t, limit: const_uint32_t): ze_result_t = unsupported
  def zesPsuGetProperties(
      hPsu: zes_psu_handle_t,
      pProperties: Pointer[zes_psu_properties_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zesPsuGetState(hPsu: zes_psu_handle_t, pState: Pointer[zes_psu_state_t, ReadWrite, Opaque]): ze_result_t =
    unsupported
  def zesRasClearStateExp(hRas: zes_ras_handle_t, category: zes_ras_error_category_exp_t): ze_result_t = unsupported
  def zesRasGetConfig(hRas: zes_ras_handle_t, pConfig: Pointer[zes_ras_config_t, ReadWrite, Opaque]): ze_result_t =
    unsupported
  def zesRasGetConfigExp(
      hRas: zes_ras_handle_t,
      count: const_uint32_t,
      pConfig: Pointer[zes_ras_config_exp_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zesRasGetProperties(
      hRas: zes_ras_handle_t,
      pProperties: Pointer[zes_ras_properties_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zesRasGetState(
      hRas: zes_ras_handle_t,
      clear: ze_bool_t,
      pState: Pointer[zes_ras_state_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zesRasGetStateExp(
      hRas: zes_ras_handle_t,
      pCount: Pointer[uint32_t, ReadWrite, Opaque],
      pState: Pointer[zes_ras_state_exp_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zesRasGetStateExp2(
      hRas: zes_ras_handle_t,
      count: const_uint32_t,
      pCategories: Pointer[zes_ras_error_category_exp_t, Read, Opaque],
      pState: Pointer[zes_ras_state_exp2_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zesRasGetSupportedCategoriesExp(
      hRas: zes_ras_handle_t,
      pCount: Pointer[uint32_t, ReadWrite, Opaque],
      pCategories: Pointer[zes_ras_error_category_exp_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zesRasSetConfig(hRas: zes_ras_handle_t, pConfig: Pointer[zes_ras_config_t, Read, Opaque]): ze_result_t =
    unsupported
  def zesRasSetConfigExp(
      hRas: zes_ras_handle_t,
      count: const_uint32_t,
      pConfig: Pointer[zes_ras_config_exp_t, Read, Opaque]
  ): ze_result_t = unsupported
  def zesSchedulerGetCurrentMode(
      hScheduler: zes_sched_handle_t,
      pMode: Pointer[zes_sched_mode_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zesSchedulerGetProperties(
      hScheduler: zes_sched_handle_t,
      pProperties: Pointer[zes_sched_properties_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zesSchedulerGetTimeoutModeProperties(
      hScheduler: zes_sched_handle_t,
      getDefaults: ze_bool_t,
      pConfig: Pointer[zes_sched_timeout_properties_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zesSchedulerGetTimesliceModeProperties(
      hScheduler: zes_sched_handle_t,
      getDefaults: ze_bool_t,
      pConfig: Pointer[zes_sched_timeslice_properties_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zesSchedulerSetComputeUnitDebugMode(
      hScheduler: zes_sched_handle_t,
      pNeedReload: Pointer[ze_bool_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zesSchedulerSetExclusiveMode(
      hScheduler: zes_sched_handle_t,
      pNeedReload: Pointer[ze_bool_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zesSchedulerSetTimeoutMode(
      hScheduler: zes_sched_handle_t,
      pProperties: Pointer[zes_sched_timeout_properties_t, ReadWrite, Opaque],
      pNeedReload: Pointer[ze_bool_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zesSchedulerSetTimesliceMode(
      hScheduler: zes_sched_handle_t,
      pProperties: Pointer[zes_sched_timeslice_properties_t, ReadWrite, Opaque],
      pNeedReload: Pointer[ze_bool_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zesStandbyGetMode(
      hStandby: zes_standby_handle_t,
      pMode: Pointer[zes_standby_promo_mode_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zesStandbyGetProperties(
      hStandby: zes_standby_handle_t,
      pProperties: Pointer[zes_standby_properties_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zesStandbySetMode(hStandby: zes_standby_handle_t, mode: zes_standby_promo_mode_t): ze_result_t = unsupported
  def zesTemperatureGetConfig(
      hTemperature: zes_temp_handle_t,
      pConfig: Pointer[zes_temp_config_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zesTemperatureGetProperties(
      hTemperature: zes_temp_handle_t,
      pProperties: Pointer[zes_temp_properties_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zesTemperatureGetState(
      hTemperature: zes_temp_handle_t,
      pTemperature: Pointer[Double, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zesTemperatureSetConfig(
      hTemperature: zes_temp_handle_t,
      pConfig: Pointer[zes_temp_config_t, Read, Opaque]
  ): ze_result_t = unsupported
  def zesVFManagementGetVFCapabilitiesExp(
      hVFhandle: zes_vf_handle_t,
      pCapability: Pointer[zes_vf_exp_capabilities_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zesVFManagementGetVFCapabilitiesExp2(
      hVFhandle: zes_vf_handle_t,
      pCapability: Pointer[zes_vf_exp2_capabilities_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zesVFManagementGetVFEngineUtilizationExp(
      hVFhandle: zes_vf_handle_t,
      pCount: Pointer[uint32_t, ReadWrite, Opaque],
      pEngineUtil: Pointer[zes_vf_util_engine_exp_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zesVFManagementGetVFEngineUtilizationExp2(
      hVFhandle: zes_vf_handle_t,
      pCount: Pointer[uint32_t, ReadWrite, Opaque],
      pEngineUtil: Pointer[zes_vf_util_engine_exp2_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zesVFManagementGetVFMemoryUtilizationExp(
      hVFhandle: zes_vf_handle_t,
      pCount: Pointer[uint32_t, ReadWrite, Opaque],
      pMemUtil: Pointer[zes_vf_util_mem_exp_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zesVFManagementGetVFMemoryUtilizationExp2(
      hVFhandle: zes_vf_handle_t,
      pCount: Pointer[uint32_t, ReadWrite, Opaque],
      pMemUtil: Pointer[zes_vf_util_mem_exp2_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zesVFManagementGetVFPropertiesExp(
      hVFhandle: zes_vf_handle_t,
      pProperties: Pointer[zes_vf_exp_properties_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zesVFManagementSetVFTelemetryModeExp(
      hVFhandle: zes_vf_handle_t,
      flags: zes_vf_info_util_exp_flags_t,
      enable: ze_bool_t
  ): ze_result_t = unsupported
  def zesVFManagementSetVFTelemetrySamplingIntervalExp(
      hVFhandle: zes_vf_handle_t,
      flag: zes_vf_info_util_exp_flags_t,
      samplingInterval: uint64_t
  ): ze_result_t = unsupported
  def zetCommandListAppendMarkerExp(
      hCommandList: zet_command_list_handle_t,
      hMetricGroup: zet_metric_group_handle_t,
      value: uint32_t
  ): ze_result_t = unsupported
  def zetCommandListAppendMetricMemoryBarrier(hCommandList: zet_command_list_handle_t): ze_result_t = unsupported
  def zetCommandListAppendMetricQueryBegin(
      hCommandList: zet_command_list_handle_t,
      hMetricQuery: zet_metric_query_handle_t
  ): ze_result_t = unsupported
  def zetCommandListAppendMetricQueryEnd(
      hCommandList: zet_command_list_handle_t,
      hMetricQuery: zet_metric_query_handle_t,
      hSignalEvent: ze_event_handle_t,
      numWaitEvents: uint32_t,
      phWaitEvents: Pointer[ze_event_handle_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zetCommandListAppendMetricStreamerMarker(
      hCommandList: zet_command_list_handle_t,
      hMetricStreamer: zet_metric_streamer_handle_t,
      value: uint32_t
  ): ze_result_t = unsupported
  def zetContextActivateMetricGroups(
      hContext: zet_context_handle_t,
      hDevice: zet_device_handle_t,
      count: uint32_t,
      phMetricGroups: Pointer[zet_metric_group_handle_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zetDebugAcknowledgeEvent(
      hDebug: zet_debug_session_handle_t,
      event: Pointer[zet_debug_event_t, Read, Opaque]
  ): ze_result_t = unsupported
  def zetDebugAttach(
      hDevice: zet_device_handle_t,
      config: Pointer[zet_debug_config_t, Read, Opaque],
      phDebug: Pointer[zet_debug_session_handle_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zetDebugDetach(hDebug: zet_debug_session_handle_t): ze_result_t = unsupported
  def zetDebugGetRegisterSetProperties(
      hDevice: zet_device_handle_t,
      pCount: Pointer[uint32_t, ReadWrite, Opaque],
      pRegisterSetProperties: Pointer[zet_debug_regset_properties_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zetDebugGetThreadRegisterSetProperties(
      hDebug: zet_debug_session_handle_t,
      thread: ze_device_thread_t,
      pCount: Pointer[uint32_t, ReadWrite, Opaque],
      pRegisterSetProperties: Pointer[zet_debug_regset_properties_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zetDebugInterrupt(hDebug: zet_debug_session_handle_t, thread: ze_device_thread_t): ze_result_t = unsupported
  def zetDebugReadEvent(
      hDebug: zet_debug_session_handle_t,
      timeout: uint64_t,
      event: Pointer[zet_debug_event_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zetDebugReadMemory(
      hDebug: zet_debug_session_handle_t,
      thread: ze_device_thread_t,
      desc: Pointer[zet_debug_memory_space_desc_t, Read, Opaque],
      size: size_t,
      buffer: OpaquePointer
  ): ze_result_t = unsupported
  def zetDebugReadRegisters(
      hDebug: zet_debug_session_handle_t,
      thread: ze_device_thread_t,
      `type`: uint32_t,
      start: uint32_t,
      count: uint32_t,
      pRegisterValues: OpaquePointer
  ): ze_result_t = unsupported
  def zetDebugResume(hDebug: zet_debug_session_handle_t, thread: ze_device_thread_t): ze_result_t = unsupported
  def zetDebugWriteMemory(
      hDebug: zet_debug_session_handle_t,
      thread: ze_device_thread_t,
      desc: Pointer[zet_debug_memory_space_desc_t, Read, Opaque],
      size: size_t,
      buffer: Pointer[CVoid, Read, Opaque]
  ): ze_result_t = unsupported
  def zetDebugWriteRegisters(
      hDebug: zet_debug_session_handle_t,
      thread: ze_device_thread_t,
      `type`: uint32_t,
      start: uint32_t,
      count: uint32_t,
      pRegisterValues: OpaquePointer
  ): ze_result_t = unsupported
  def zetDeviceCreateMetricGroupsFromMetricsExp(
      hDevice: zet_device_handle_t,
      metricCount: uint32_t,
      phMetrics: Pointer[zet_metric_handle_t, ReadWrite, Opaque],
      pMetricGroupNamePrefix: CString,
      pDescription: CString,
      pMetricGroupCount: Pointer[uint32_t, ReadWrite, Opaque],
      phMetricGroup: Pointer[zet_metric_group_handle_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zetDeviceDisableMetricsExp(hDevice: zet_device_handle_t): ze_result_t = unsupported
  def zetDeviceEnableMetricsExp(hDevice: zet_device_handle_t): ze_result_t = unsupported
  def zetDeviceGetConcurrentMetricGroupsExp(
      hDevice: zet_device_handle_t,
      metricGroupCount: uint32_t,
      phMetricGroups: Pointer[zet_metric_group_handle_t, ReadWrite, Opaque],
      pMetricGroupsCountPerConcurrentGroup: Pointer[uint32_t, ReadWrite, Opaque],
      pConcurrentGroupCount: Pointer[uint32_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zetDeviceGetDebugProperties(
      hDevice: zet_device_handle_t,
      pDebugProperties: Pointer[zet_device_debug_properties_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zetKernelGetProfileInfo(
      hKernel: zet_kernel_handle_t,
      pProfileProperties: Pointer[zet_profile_properties_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zetMetricCreateFromProgrammableExp(
      hMetricProgrammable: zet_metric_programmable_exp_handle_t,
      pParameterValues: Pointer[zet_metric_programmable_param_value_exp_t, ReadWrite, Opaque],
      parameterCount: uint32_t,
      pName: CString,
      pDescription: CString,
      pMetricHandleCount: Pointer[uint32_t, ReadWrite, Opaque],
      phMetricHandles: Pointer[zet_metric_handle_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zetMetricCreateFromProgrammableExp2(
      hMetricProgrammable: zet_metric_programmable_exp_handle_t,
      parameterCount: uint32_t,
      pParameterValues: Pointer[zet_metric_programmable_param_value_exp_t, ReadWrite, Opaque],
      pName: CString,
      pDescription: CString,
      pMetricHandleCount: Pointer[uint32_t, ReadWrite, Opaque],
      phMetricHandles: Pointer[zet_metric_handle_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zetMetricDecoderCreateExp(
      hMetricTracer: zet_metric_tracer_exp_handle_t,
      phMetricDecoder: Pointer[zet_metric_decoder_exp_handle_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zetMetricDecoderDestroyExp(phMetricDecoder: zet_metric_decoder_exp_handle_t): ze_result_t = unsupported
  def zetMetricDecoderGetDecodableMetricsExp(
      hMetricDecoder: zet_metric_decoder_exp_handle_t,
      pCount: Pointer[uint32_t, ReadWrite, Opaque],
      phMetrics: Pointer[zet_metric_handle_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zetMetricDestroyExp(hMetric: zet_metric_handle_t): ze_result_t = unsupported
  def zetMetricGet(
      hMetricGroup: zet_metric_group_handle_t,
      pCount: Pointer[uint32_t, ReadWrite, Opaque],
      phMetrics: Pointer[zet_metric_handle_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zetMetricGetProperties(
      hMetric: zet_metric_handle_t,
      pProperties: Pointer[zet_metric_properties_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zetMetricGroupAddMetricExp(
      hMetricGroup: zet_metric_group_handle_t,
      hMetric: zet_metric_handle_t,
      pErrorStringSize: Pointer[size_t, ReadWrite, Opaque],
      pErrorString: Pointer[Byte, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zetMetricGroupCalculateMetricExportDataExp(
      hDriver: ze_driver_handle_t,
      `type`: zet_metric_group_calculation_type_t,
      exportDataSize: size_t,
      pExportData: Pointer[uint8_t, Read, Opaque],
      pCalculateDescriptor: Pointer[zet_metric_calculate_exp_desc_t, ReadWrite, Opaque],
      pSetCount: Pointer[uint32_t, ReadWrite, Opaque],
      pTotalMetricValueCount: Pointer[uint32_t, ReadWrite, Opaque],
      pMetricCounts: Pointer[uint32_t, ReadWrite, Opaque],
      pMetricValues: Pointer[zet_typed_value_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zetMetricGroupCalculateMetricValues(
      hMetricGroup: zet_metric_group_handle_t,
      `type`: zet_metric_group_calculation_type_t,
      rawDataSize: size_t,
      pRawData: Pointer[uint8_t, Read, Opaque],
      pMetricValueCount: Pointer[uint32_t, ReadWrite, Opaque],
      pMetricValues: Pointer[zet_typed_value_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zetMetricGroupCalculateMultipleMetricValuesExp(
      hMetricGroup: zet_metric_group_handle_t,
      `type`: zet_metric_group_calculation_type_t,
      rawDataSize: size_t,
      pRawData: Pointer[uint8_t, Read, Opaque],
      pSetCount: Pointer[uint32_t, ReadWrite, Opaque],
      pTotalMetricValueCount: Pointer[uint32_t, ReadWrite, Opaque],
      pMetricCounts: Pointer[uint32_t, ReadWrite, Opaque],
      pMetricValues: Pointer[zet_typed_value_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zetMetricGroupCloseExp(hMetricGroup: zet_metric_group_handle_t): ze_result_t = unsupported
  def zetMetricGroupCreateExp(
      hDevice: zet_device_handle_t,
      pName: CString,
      pDescription: CString,
      samplingType: zet_metric_group_sampling_type_flags_t,
      phMetricGroup: Pointer[zet_metric_group_handle_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zetMetricGroupDestroyExp(hMetricGroup: zet_metric_group_handle_t): ze_result_t = unsupported
  def zetMetricGroupGet(
      hDevice: zet_device_handle_t,
      pCount: Pointer[uint32_t, ReadWrite, Opaque],
      phMetricGroups: Pointer[zet_metric_group_handle_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zetMetricGroupGetExportDataExp(
      hMetricGroup: zet_metric_group_handle_t,
      pRawData: Pointer[uint8_t, Read, Opaque],
      rawDataSize: size_t,
      pExportDataSize: Pointer[size_t, ReadWrite, Opaque],
      pExportData: Pointer[uint8_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zetMetricGroupGetGlobalTimestampsExp(
      hMetricGroup: zet_metric_group_handle_t,
      synchronizedWithHost: ze_bool_t,
      globalTimestamp: Pointer[uint64_t, ReadWrite, Opaque],
      metricTimestamp: Pointer[uint64_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zetMetricGroupGetProperties(
      hMetricGroup: zet_metric_group_handle_t,
      pProperties: Pointer[zet_metric_group_properties_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zetMetricGroupRemoveMetricExp(
      hMetricGroup: zet_metric_group_handle_t,
      hMetric: zet_metric_handle_t
  ): ze_result_t = unsupported
  def zetMetricProgrammableGetExp(
      hDevice: zet_device_handle_t,
      pCount: Pointer[uint32_t, ReadWrite, Opaque],
      phMetricProgrammables: Pointer[zet_metric_programmable_exp_handle_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zetMetricProgrammableGetParamInfoExp(
      hMetricProgrammable: zet_metric_programmable_exp_handle_t,
      pParameterCount: Pointer[uint32_t, ReadWrite, Opaque],
      pParameterInfo: Pointer[zet_metric_programmable_param_info_exp_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zetMetricProgrammableGetParamValueInfoExp(
      hMetricProgrammable: zet_metric_programmable_exp_handle_t,
      parameterOrdinal: uint32_t,
      pValueInfoCount: Pointer[uint32_t, ReadWrite, Opaque],
      pValueInfo: Pointer[zet_metric_programmable_param_value_info_exp_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zetMetricProgrammableGetPropertiesExp(
      hMetricProgrammable: zet_metric_programmable_exp_handle_t,
      pProperties: Pointer[zet_metric_programmable_exp_properties_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zetMetricQueryCreate(
      hMetricQueryPool: zet_metric_query_pool_handle_t,
      index: uint32_t,
      phMetricQuery: Pointer[zet_metric_query_handle_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zetMetricQueryDestroy(hMetricQuery: zet_metric_query_handle_t): ze_result_t = unsupported
  def zetMetricQueryGetData(
      hMetricQuery: zet_metric_query_handle_t,
      pRawDataSize: Pointer[size_t, ReadWrite, Opaque],
      pRawData: Pointer[uint8_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zetMetricQueryPoolCreate(
      hContext: zet_context_handle_t,
      hDevice: zet_device_handle_t,
      hMetricGroup: zet_metric_group_handle_t,
      desc: Pointer[zet_metric_query_pool_desc_t, Read, Opaque],
      phMetricQueryPool: Pointer[zet_metric_query_pool_handle_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zetMetricQueryPoolDestroy(hMetricQueryPool: zet_metric_query_pool_handle_t): ze_result_t = unsupported
  def zetMetricQueryReset(hMetricQuery: zet_metric_query_handle_t): ze_result_t = unsupported
  def zetMetricStreamerClose(hMetricStreamer: zet_metric_streamer_handle_t): ze_result_t = unsupported
  def zetMetricStreamerOpen(
      hContext: zet_context_handle_t,
      hDevice: zet_device_handle_t,
      hMetricGroup: zet_metric_group_handle_t,
      desc: Pointer[zet_metric_streamer_desc_t, ReadWrite, Opaque],
      hNotificationEvent: ze_event_handle_t,
      phMetricStreamer: Pointer[zet_metric_streamer_handle_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zetMetricStreamerReadData(
      hMetricStreamer: zet_metric_streamer_handle_t,
      maxReportCount: uint32_t,
      pRawDataSize: Pointer[size_t, ReadWrite, Opaque],
      pRawData: Pointer[uint8_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zetMetricTracerCreateExp(
      hContext: zet_context_handle_t,
      hDevice: zet_device_handle_t,
      metricGroupCount: uint32_t,
      phMetricGroups: Pointer[zet_metric_group_handle_t, ReadWrite, Opaque],
      desc: Pointer[zet_metric_tracer_exp_desc_t, ReadWrite, Opaque],
      hNotificationEvent: ze_event_handle_t,
      phMetricTracer: Pointer[zet_metric_tracer_exp_handle_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zetMetricTracerDecodeExp(
      phMetricDecoder: zet_metric_decoder_exp_handle_t,
      pRawDataSize: Pointer[size_t, ReadWrite, Opaque],
      pRawData: Pointer[uint8_t, ReadWrite, Opaque],
      metricsCount: uint32_t,
      phMetrics: Pointer[zet_metric_handle_t, ReadWrite, Opaque],
      pSetCount: Pointer[uint32_t, ReadWrite, Opaque],
      pMetricEntriesCountPerSet: Pointer[uint32_t, ReadWrite, Opaque],
      pMetricEntriesCount: Pointer[uint32_t, ReadWrite, Opaque],
      pMetricEntries: Pointer[zet_metric_entry_exp_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zetMetricTracerDestroyExp(hMetricTracer: zet_metric_tracer_exp_handle_t): ze_result_t = unsupported
  def zetMetricTracerDisableExp(hMetricTracer: zet_metric_tracer_exp_handle_t, synchronous: ze_bool_t): ze_result_t =
    unsupported
  def zetMetricTracerEnableExp(hMetricTracer: zet_metric_tracer_exp_handle_t, synchronous: ze_bool_t): ze_result_t =
    unsupported
  def zetMetricTracerReadDataExp(
      hMetricTracer: zet_metric_tracer_exp_handle_t,
      pRawDataSize: Pointer[size_t, ReadWrite, Opaque],
      pRawData: Pointer[uint8_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zetModuleGetDebugInfo(
      hModule: zet_module_handle_t,
      format: zet_module_debug_info_format_t,
      pSize: Pointer[size_t, ReadWrite, Opaque],
      pDebugInfo: Pointer[uint8_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zetTracerExpCreate(
      hContext: zet_context_handle_t,
      desc: Pointer[zet_tracer_exp_desc_t, Read, Opaque],
      phTracer: Pointer[zet_tracer_exp_handle_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zetTracerExpDestroy(hTracer: zet_tracer_exp_handle_t): ze_result_t = unsupported
  def zetTracerExpSetEnabled(hTracer: zet_tracer_exp_handle_t, enable: ze_bool_t): ze_result_t = unsupported
  def zetTracerExpSetEpilogues(
      hTracer: zet_tracer_exp_handle_t,
      pCoreCbs: Pointer[zet_core_callbacks_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
  def zetTracerExpSetPrologues(
      hTracer: zet_tracer_exp_handle_t,
      pCoreCbs: Pointer[zet_core_callbacks_t, ReadWrite, Opaque]
  ): ze_result_t = unsupported
}

object LevelZeroSurface {
  val HeaderVersion: String = "1.33.1"
  val SurfaceSize: Int = 485
}
