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
type ze_event_query_kernel_timestamps_results_ext_properties_t = CValue["ze_event_query_kernel_timestamps_results_ext_properties_t"]
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
type ze_host_function_callback_t = CValue["ze_host_function_callback_t"]
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
type zex_mem_graph_free_callback_fn_t = CValue["zex_mem_graph_free_callback_fn_t"]

trait LevelZero {
  @CSignature("ze_result_t (ze_command_list_handle_t, ze_event_handle_t, uint32_t, ze_event_handle_t *)")
  def zeCommandListAppendBarrier(hCommandList: ze_command_list_handle_t, hSignalEvent: ze_event_handle_t, numWaitEvents: uint32_t, phWaitEvents: Pointer[ze_event_handle_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_command_list_handle_t, ze_event_handle_t)")
  def zeCommandListAppendEventReset(hCommandList: ze_command_list_handle_t, hEvent: ze_event_handle_t): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_command_list_handle_t, ze_executable_graph_handle_t, const void *, ze_event_handle_t, uint32_t, ze_event_handle_t *)")
  def zeCommandListAppendGraphExt(hCommandList: ze_command_list_handle_t, hGraph: ze_executable_graph_handle_t, pNext: Pointer[CVoid, Read, Opaque], hSignalEvent: ze_event_handle_t, numWaitEvents: uint32_t, phWaitEvents: Pointer[ze_event_handle_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_command_list_handle_t, ze_host_function_callback_t, void *, const void *, ze_event_handle_t, uint32_t, ze_event_handle_t *)")
  def zeCommandListAppendHostFunction(hCommandList: ze_command_list_handle_t, pfnHostFunction: ze_host_function_callback_t, pUserData: Pointer[CVoid, ReadWrite, Opaque], pNext: Pointer[CVoid, Read, Opaque], hSignalEvent: ze_event_handle_t, numWaitEvents: uint32_t, phWaitEvents: Pointer[ze_event_handle_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_command_list_handle_t, ze_image_handle_t, ze_image_handle_t, ze_event_handle_t, uint32_t, ze_event_handle_t *)")
  def zeCommandListAppendImageCopy(hCommandList: ze_command_list_handle_t, hDstImage: ze_image_handle_t, hSrcImage: ze_image_handle_t, hSignalEvent: ze_event_handle_t, numWaitEvents: uint32_t, phWaitEvents: Pointer[ze_event_handle_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_command_list_handle_t, ze_image_handle_t, const void *, const ze_image_region_t *, ze_event_handle_t, uint32_t, ze_event_handle_t *)")
  def zeCommandListAppendImageCopyFromMemory(hCommandList: ze_command_list_handle_t, hDstImage: ze_image_handle_t, srcptr: Pointer[CVoid, Read, Opaque], pDstRegion: Pointer[ze_image_region_t, Read, Opaque], hSignalEvent: ze_event_handle_t, numWaitEvents: uint32_t, phWaitEvents: Pointer[ze_event_handle_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_command_list_handle_t, ze_image_handle_t, const void *, const ze_image_region_t *, uint32_t, uint32_t, ze_event_handle_t, uint32_t, ze_event_handle_t *)")
  def zeCommandListAppendImageCopyFromMemoryExt(hCommandList: ze_command_list_handle_t, hDstImage: ze_image_handle_t, srcptr: Pointer[CVoid, Read, Opaque], pDstRegion: Pointer[ze_image_region_t, Read, Opaque], srcRowPitch: uint32_t, srcSlicePitch: uint32_t, hSignalEvent: ze_event_handle_t, numWaitEvents: uint32_t, phWaitEvents: Pointer[ze_event_handle_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_command_list_handle_t, ze_image_handle_t, ze_image_handle_t, const ze_image_region_t *, const ze_image_region_t *, ze_event_handle_t, uint32_t, ze_event_handle_t *)")
  def zeCommandListAppendImageCopyRegion(hCommandList: ze_command_list_handle_t, hDstImage: ze_image_handle_t, hSrcImage: ze_image_handle_t, pDstRegion: Pointer[ze_image_region_t, Read, Opaque], pSrcRegion: Pointer[ze_image_region_t, Read, Opaque], hSignalEvent: ze_event_handle_t, numWaitEvents: uint32_t, phWaitEvents: Pointer[ze_event_handle_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_command_list_handle_t, void *, ze_image_handle_t, const ze_image_region_t *, ze_event_handle_t, uint32_t, ze_event_handle_t *)")
  def zeCommandListAppendImageCopyToMemory(hCommandList: ze_command_list_handle_t, dstptr: Pointer[CVoid, ReadWrite, Opaque], hSrcImage: ze_image_handle_t, pSrcRegion: Pointer[ze_image_region_t, Read, Opaque], hSignalEvent: ze_event_handle_t, numWaitEvents: uint32_t, phWaitEvents: Pointer[ze_event_handle_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_command_list_handle_t, void *, ze_image_handle_t, const ze_image_region_t *, uint32_t, uint32_t, ze_event_handle_t, uint32_t, ze_event_handle_t *)")
  def zeCommandListAppendImageCopyToMemoryExt(hCommandList: ze_command_list_handle_t, dstptr: Pointer[CVoid, ReadWrite, Opaque], hSrcImage: ze_image_handle_t, pSrcRegion: Pointer[ze_image_region_t, Read, Opaque], destRowPitch: uint32_t, destSlicePitch: uint32_t, hSignalEvent: ze_event_handle_t, numWaitEvents: uint32_t, phWaitEvents: Pointer[ze_event_handle_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_command_list_handle_t, ze_kernel_handle_t, const ze_group_count_t *, ze_event_handle_t, uint32_t, ze_event_handle_t *)")
  def zeCommandListAppendLaunchCooperativeKernel(hCommandList: ze_command_list_handle_t, hKernel: ze_kernel_handle_t, pLaunchFuncArgs: Pointer[ze_group_count_t, Read, Opaque], hSignalEvent: ze_event_handle_t, numWaitEvents: uint32_t, phWaitEvents: Pointer[ze_event_handle_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_command_list_handle_t, ze_kernel_handle_t, const ze_group_count_t *, ze_event_handle_t, uint32_t, ze_event_handle_t *)")
  def zeCommandListAppendLaunchKernel(hCommandList: ze_command_list_handle_t, hKernel: ze_kernel_handle_t, pLaunchFuncArgs: Pointer[ze_group_count_t, Read, Opaque], hSignalEvent: ze_event_handle_t, numWaitEvents: uint32_t, phWaitEvents: Pointer[ze_event_handle_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_command_list_handle_t, ze_kernel_handle_t, const ze_group_count_t *, ze_event_handle_t, uint32_t, ze_event_handle_t *)")
  def zeCommandListAppendLaunchKernelIndirect(hCommandList: ze_command_list_handle_t, hKernel: ze_kernel_handle_t, pLaunchArgumentsBuffer: Pointer[ze_group_count_t, Read, Opaque], hSignalEvent: ze_event_handle_t, numWaitEvents: uint32_t, phWaitEvents: Pointer[ze_event_handle_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_command_list_handle_t, ze_kernel_handle_t, const ze_group_count_t, const ze_group_size_t, void **, const void *, ze_event_handle_t, uint32_t, ze_event_handle_t *)")
  def zeCommandListAppendLaunchKernelWithArguments(hCommandList: ze_command_list_handle_t, hKernel: ze_kernel_handle_t, groupCounts: const_ze_group_count_t, groupSizes: const_ze_group_size_t, pArguments: Pointer[Pointer[CVoid, ReadWrite, Opaque], ReadWrite, Opaque], pNext: Pointer[CVoid, Read, Opaque], hSignalEvent: ze_event_handle_t, numWaitEvents: uint32_t, phWaitEvents: Pointer[ze_event_handle_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_command_list_handle_t, ze_kernel_handle_t, const ze_group_count_t *, const void *, ze_event_handle_t, uint32_t, ze_event_handle_t *)")
  def zeCommandListAppendLaunchKernelWithParameters(hCommandList: ze_command_list_handle_t, hKernel: ze_kernel_handle_t, pGroupCounts: Pointer[ze_group_count_t, Read, Opaque], pNext: Pointer[CVoid, Read, Opaque], hSignalEvent: ze_event_handle_t, numWaitEvents: uint32_t, phWaitEvents: Pointer[ze_event_handle_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_command_list_handle_t, uint32_t, ze_kernel_handle_t *, const uint32_t *, const ze_group_count_t *, ze_event_handle_t, uint32_t, ze_event_handle_t *)")
  def zeCommandListAppendLaunchMultipleKernelsIndirect(hCommandList: ze_command_list_handle_t, numKernels: uint32_t, phKernels: Pointer[ze_kernel_handle_t, ReadWrite, Opaque], pCountBuffer: Pointer[uint32_t, Read, Opaque], pLaunchArgumentsBuffer: Pointer[ze_group_count_t, Read, Opaque], hSignalEvent: ze_event_handle_t, numWaitEvents: uint32_t, phWaitEvents: Pointer[ze_event_handle_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_command_list_handle_t, ze_device_handle_t, const void *, size_t, ze_memory_advice_t)")
  def zeCommandListAppendMemAdvise(hCommandList: ze_command_list_handle_t, hDevice: ze_device_handle_t, ptr: Pointer[CVoid, Read, Opaque], size: size_t, advice: ze_memory_advice_t): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_command_list_handle_t, void *, const void *, size_t, ze_event_handle_t, uint32_t, ze_event_handle_t *)")
  def zeCommandListAppendMemoryCopy(hCommandList: ze_command_list_handle_t, dstptr: Pointer[CVoid, ReadWrite, Opaque], srcptr: Pointer[CVoid, Read, Opaque], size: size_t, hSignalEvent: ze_event_handle_t, numWaitEvents: uint32_t, phWaitEvents: Pointer[ze_event_handle_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_command_list_handle_t, void *, ze_context_handle_t, const void *, size_t, ze_event_handle_t, uint32_t, ze_event_handle_t *)")
  def zeCommandListAppendMemoryCopyFromContext(hCommandList: ze_command_list_handle_t, dstptr: Pointer[CVoid, ReadWrite, Opaque], hContextSrc: ze_context_handle_t, srcptr: Pointer[CVoid, Read, Opaque], size: size_t, hSignalEvent: ze_event_handle_t, numWaitEvents: uint32_t, phWaitEvents: Pointer[ze_event_handle_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_command_list_handle_t, void *, const ze_copy_region_t *, uint32_t, uint32_t, const void *, const ze_copy_region_t *, uint32_t, uint32_t, ze_event_handle_t, uint32_t, ze_event_handle_t *)")
  def zeCommandListAppendMemoryCopyRegion(hCommandList: ze_command_list_handle_t, dstptr: Pointer[CVoid, ReadWrite, Opaque], dstRegion: Pointer[ze_copy_region_t, Read, Opaque], dstPitch: uint32_t, dstSlicePitch: uint32_t, srcptr: Pointer[CVoid, Read, Opaque], srcRegion: Pointer[ze_copy_region_t, Read, Opaque], srcPitch: uint32_t, srcSlicePitch: uint32_t, hSignalEvent: ze_event_handle_t, numWaitEvents: uint32_t, phWaitEvents: Pointer[ze_event_handle_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_command_list_handle_t, void *, const void *, size_t, const void *, ze_event_handle_t, uint32_t, ze_event_handle_t *)")
  def zeCommandListAppendMemoryCopyWithParameters(hCommandList: ze_command_list_handle_t, dstptr: Pointer[CVoid, ReadWrite, Opaque], srcptr: Pointer[CVoid, Read, Opaque], size: size_t, pNext: Pointer[CVoid, Read, Opaque], hSignalEvent: ze_event_handle_t, numWaitEvents: uint32_t, phWaitEvents: Pointer[ze_event_handle_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_command_list_handle_t, void *, const void *, size_t, size_t, ze_event_handle_t, uint32_t, ze_event_handle_t *)")
  def zeCommandListAppendMemoryFill(hCommandList: ze_command_list_handle_t, ptr: Pointer[CVoid, ReadWrite, Opaque], pattern: Pointer[CVoid, Read, Opaque], pattern_size: size_t, size: size_t, hSignalEvent: ze_event_handle_t, numWaitEvents: uint32_t, phWaitEvents: Pointer[ze_event_handle_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_command_list_handle_t, void *, const void *, size_t, size_t, const void *, ze_event_handle_t, uint32_t, ze_event_handle_t *)")
  def zeCommandListAppendMemoryFillWithParameters(hCommandList: ze_command_list_handle_t, ptr: Pointer[CVoid, ReadWrite, Opaque], pattern: Pointer[CVoid, Read, Opaque], pattern_size: size_t, size: size_t, pNext: Pointer[CVoid, Read, Opaque], hSignalEvent: ze_event_handle_t, numWaitEvents: uint32_t, phWaitEvents: Pointer[ze_event_handle_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_command_list_handle_t, const void *, size_t)")
  def zeCommandListAppendMemoryPrefetch(hCommandList: ze_command_list_handle_t, ptr: Pointer[CVoid, Read, Opaque], size: size_t): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_command_list_handle_t, uint32_t, const size_t *, const void **, ze_event_handle_t, uint32_t, ze_event_handle_t *)")
  def zeCommandListAppendMemoryRangesBarrier(hCommandList: ze_command_list_handle_t, numRanges: uint32_t, pRangeSizes: Pointer[size_t, Read, Opaque], pRanges: Pointer[Pointer[CVoid, ReadWrite, Opaque], Read, Opaque], hSignalEvent: ze_event_handle_t, numWaitEvents: uint32_t, phWaitEvents: Pointer[ze_event_handle_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_command_list_handle_t, uint32_t, ze_event_handle_t *, void *, const size_t *, ze_event_handle_t, uint32_t, ze_event_handle_t *)")
  def zeCommandListAppendQueryKernelTimestamps(hCommandList: ze_command_list_handle_t, numEvents: uint32_t, phEvents: Pointer[ze_event_handle_t, ReadWrite, Opaque], dstptr: Pointer[CVoid, ReadWrite, Opaque], pOffsets: Pointer[size_t, Read, Opaque], hSignalEvent: ze_event_handle_t, numWaitEvents: uint32_t, phWaitEvents: Pointer[ze_event_handle_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_command_list_handle_t, ze_event_handle_t)")
  def zeCommandListAppendSignalEvent(hCommandList: ze_command_list_handle_t, hEvent: ze_event_handle_t): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_command_list_handle_t, const void *, ze_event_handle_t)")
  def zeCommandListAppendSignalEventWithParameters(hCommandList: ze_command_list_handle_t, pNext: Pointer[CVoid, Read, Opaque], hEvent: ze_event_handle_t): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_command_list_handle_t, uint32_t, ze_external_semaphore_ext_handle_t *, ze_external_semaphore_signal_params_ext_t *, ze_event_handle_t, uint32_t, ze_event_handle_t *)")
  def zeCommandListAppendSignalExternalSemaphoreExt(hCommandList: ze_command_list_handle_t, numSemaphores: uint32_t, phSemaphores: Pointer[ze_external_semaphore_ext_handle_t, ReadWrite, Opaque], signalParams: Pointer[ze_external_semaphore_signal_params_ext_t, ReadWrite, Opaque], hSignalEvent: ze_event_handle_t, numWaitEvents: uint32_t, phWaitEvents: Pointer[ze_event_handle_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_command_list_handle_t, uint32_t, ze_external_semaphore_ext_handle_t *, ze_external_semaphore_wait_params_ext_t *, ze_event_handle_t, uint32_t, ze_event_handle_t *)")
  def zeCommandListAppendWaitExternalSemaphoreExt(hCommandList: ze_command_list_handle_t, numSemaphores: uint32_t, phSemaphores: Pointer[ze_external_semaphore_ext_handle_t, ReadWrite, Opaque], waitParams: Pointer[ze_external_semaphore_wait_params_ext_t, ReadWrite, Opaque], hSignalEvent: ze_event_handle_t, numWaitEvents: uint32_t, phWaitEvents: Pointer[ze_event_handle_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_command_list_handle_t, uint32_t, ze_event_handle_t *)")
  def zeCommandListAppendWaitOnEvents(hCommandList: ze_command_list_handle_t, numEvents: uint32_t, phEvents: Pointer[ze_event_handle_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_command_list_handle_t, const void *, uint32_t, ze_event_handle_t *)")
  def zeCommandListAppendWaitOnEventsWithParameters(hCommandList: ze_command_list_handle_t, pNext: Pointer[CVoid, Read, Opaque], numEvents: uint32_t, phEvents: Pointer[ze_event_handle_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_command_list_handle_t, uint64_t *, ze_event_handle_t, uint32_t, ze_event_handle_t *)")
  def zeCommandListAppendWriteGlobalTimestamp(hCommandList: ze_command_list_handle_t, dstptr: Pointer[uint64_t, ReadWrite, Opaque], hSignalEvent: ze_event_handle_t, numWaitEvents: uint32_t, phWaitEvents: Pointer[ze_event_handle_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_command_list_handle_t, ze_graph_handle_t, const void *)")
  def zeCommandListBeginCaptureIntoGraphExt(hCommandList: ze_command_list_handle_t, hGraph: ze_graph_handle_t, pNext: Pointer[CVoid, Read, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_command_list_handle_t, const void *)")
  def zeCommandListBeginGraphCaptureExt(hCommandList: ze_command_list_handle_t, pNext: Pointer[CVoid, Read, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_command_list_handle_t)")
  def zeCommandListClose(hCommandList: ze_command_list_handle_t): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_context_handle_t, ze_device_handle_t, const ze_command_list_desc_t *, ze_command_list_handle_t *)")
  def zeCommandListCreate(hContext: ze_context_handle_t, hDevice: ze_device_handle_t, desc: Pointer[ze_command_list_desc_t, Read, Opaque], phCommandList: Pointer[ze_command_list_handle_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_command_list_handle_t, ze_command_list_handle_t *)")
  def zeCommandListCreateCloneExp(hCommandList: ze_command_list_handle_t, phClonedCommandList: Pointer[ze_command_list_handle_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_context_handle_t, ze_device_handle_t, const ze_command_queue_desc_t *, ze_command_list_handle_t *)")
  def zeCommandListCreateImmediate(hContext: ze_context_handle_t, hDevice: ze_device_handle_t, altdesc: Pointer[ze_command_queue_desc_t, Read, Opaque], phCommandList: Pointer[ze_command_list_handle_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_command_list_handle_t)")
  def zeCommandListDestroy(hCommandList: ze_command_list_handle_t): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_command_list_handle_t, const void *, ze_graph_handle_t *)")
  def zeCommandListEndGraphCaptureExt(hCommandList: ze_command_list_handle_t, pNext: Pointer[CVoid, Read, Opaque], phGraph: Pointer[ze_graph_handle_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_command_list_handle_t, ze_context_handle_t *)")
  def zeCommandListGetContextHandle(hCommandList: ze_command_list_handle_t, phContext: Pointer[ze_context_handle_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_command_list_handle_t, ze_device_handle_t *)")
  def zeCommandListGetDeviceHandle(hCommandList: ze_command_list_handle_t, phDevice: Pointer[ze_device_handle_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_command_list_handle_t, ze_command_list_flags_t *)")
  def zeCommandListGetFlags(hCommandList: ze_command_list_handle_t, pFlags: Pointer[ze_command_list_flags_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_command_list_handle_t, ze_graph_handle_t *)")
  def zeCommandListGetGraphExt(hCommandList: ze_command_list_handle_t, phGraph: Pointer[ze_graph_handle_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_command_list_handle_t, const ze_mutable_command_id_exp_desc_t *, uint64_t *)")
  def zeCommandListGetNextCommandIdExp(hCommandList: ze_command_list_handle_t, desc: Pointer[ze_mutable_command_id_exp_desc_t, Read, Opaque], pCommandId: Pointer[uint64_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_command_list_handle_t, const ze_mutable_command_id_exp_desc_t *, uint32_t, ze_kernel_handle_t *, uint64_t *)")
  def zeCommandListGetNextCommandIdWithKernelsExp(hCommandList: ze_command_list_handle_t, desc: Pointer[ze_mutable_command_id_exp_desc_t, Read, Opaque], numKernels: uint32_t, phKernels: Pointer[ze_kernel_handle_t, ReadWrite, Opaque], pCommandId: Pointer[uint64_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_command_list_handle_t, uint32_t *)")
  def zeCommandListGetOrdinal(hCommandList: ze_command_list_handle_t, pOrdinal: Pointer[uint32_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_command_list_handle_t, uint64_t)")
  def zeCommandListHostSynchronize(hCommandList: ze_command_list_handle_t, timeout: uint64_t): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_command_list_handle_t, uint32_t, ze_command_list_handle_t *, ze_event_handle_t, uint32_t, ze_event_handle_t *)")
  def zeCommandListImmediateAppendCommandListsExp(hCommandListImmediate: ze_command_list_handle_t, numCommandLists: uint32_t, phCommandLists: Pointer[ze_command_list_handle_t, ReadWrite, Opaque], hSignalEvent: ze_event_handle_t, numWaitEvents: uint32_t, phWaitEvents: Pointer[ze_event_handle_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_command_list_handle_t, uint32_t, ze_command_list_handle_t *, const void *, ze_event_handle_t, uint32_t, ze_event_handle_t *)")
  def zeCommandListImmediateAppendCommandListsWithParameters(hCommandListImmediate: ze_command_list_handle_t, numCommandLists: uint32_t, phCommandLists: Pointer[ze_command_list_handle_t, ReadWrite, Opaque], pNext: Pointer[CVoid, Read, Opaque], hSignalEvent: ze_event_handle_t, numWaitEvents: uint32_t, phWaitEvents: Pointer[ze_event_handle_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_command_list_handle_t, ze_command_queue_flags_t *)")
  def zeCommandListImmediateGetFlags(hCommandList: ze_command_list_handle_t, pFlags: Pointer[ze_command_queue_flags_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_command_list_handle_t, uint32_t *)")
  def zeCommandListImmediateGetIndex(hCommandListImmediate: ze_command_list_handle_t, pIndex: Pointer[uint32_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_command_list_handle_t, ze_command_queue_mode_t *)")
  def zeCommandListImmediateGetMode(hCommandList: ze_command_list_handle_t, pMode: Pointer[ze_command_queue_mode_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_command_list_handle_t, ze_command_queue_priority_t *)")
  def zeCommandListImmediateGetPriority(hCommandList: ze_command_list_handle_t, pPriority: Pointer[ze_command_queue_priority_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_command_list_handle_t)")
  def zeCommandListIsGraphCaptureEnabledExt(hCommandList: ze_command_list_handle_t): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_command_list_handle_t, ze_bool_t *)")
  def zeCommandListIsImmediate(hCommandList: ze_command_list_handle_t, pIsImmediate: Pointer[ze_bool_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_command_list_handle_t, ze_bool_t *)")
  def zeCommandListIsMutableExp(hCommandList: ze_command_list_handle_t, pIsMutable: Pointer[ze_bool_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_command_list_handle_t)")
  def zeCommandListReset(hCommandList: ze_command_list_handle_t): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_command_list_handle_t, uint32_t, uint64_t *, ze_kernel_handle_t *)")
  def zeCommandListUpdateMutableCommandKernelsExp(hCommandList: ze_command_list_handle_t, numKernels: uint32_t, pCommandId: Pointer[uint64_t, ReadWrite, Opaque], phKernels: Pointer[ze_kernel_handle_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_command_list_handle_t, uint64_t, ze_event_handle_t)")
  def zeCommandListUpdateMutableCommandSignalEventExp(hCommandList: ze_command_list_handle_t, commandId: uint64_t, hSignalEvent: ze_event_handle_t): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_command_list_handle_t, uint64_t, uint32_t, ze_event_handle_t *)")
  def zeCommandListUpdateMutableCommandWaitEventsExp(hCommandList: ze_command_list_handle_t, commandId: uint64_t, numWaitEvents: uint32_t, phWaitEvents: Pointer[ze_event_handle_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_command_list_handle_t, const ze_mutable_commands_exp_desc_t *)")
  def zeCommandListUpdateMutableCommandsExp(hCommandList: ze_command_list_handle_t, desc: Pointer[ze_mutable_commands_exp_desc_t, Read, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_context_handle_t, ze_device_handle_t, const ze_command_queue_desc_t *, ze_command_queue_handle_t *)")
  def zeCommandQueueCreate(hContext: ze_context_handle_t, hDevice: ze_device_handle_t, desc: Pointer[ze_command_queue_desc_t, Read, Opaque], phCommandQueue: Pointer[ze_command_queue_handle_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_command_queue_handle_t)")
  def zeCommandQueueDestroy(hCommandQueue: ze_command_queue_handle_t): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_command_queue_handle_t, uint32_t, ze_command_list_handle_t *, ze_fence_handle_t)")
  def zeCommandQueueExecuteCommandLists(hCommandQueue: ze_command_queue_handle_t, numCommandLists: uint32_t, phCommandLists: Pointer[ze_command_list_handle_t, ReadWrite, Opaque], hFence: ze_fence_handle_t): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_command_queue_handle_t, ze_command_queue_flags_t *)")
  def zeCommandQueueGetFlags(hCmdQueue: ze_command_queue_handle_t, pFlags: Pointer[ze_command_queue_flags_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_command_queue_handle_t, uint32_t *)")
  def zeCommandQueueGetIndex(hCommandQueue: ze_command_queue_handle_t, pIndex: Pointer[uint32_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_command_queue_handle_t, ze_command_queue_mode_t *)")
  def zeCommandQueueGetMode(hCmdQueue: ze_command_queue_handle_t, pMode: Pointer[ze_command_queue_mode_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_command_queue_handle_t, uint32_t *)")
  def zeCommandQueueGetOrdinal(hCommandQueue: ze_command_queue_handle_t, pOrdinal: Pointer[uint32_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_command_queue_handle_t, ze_command_queue_priority_t *)")
  def zeCommandQueueGetPriority(hCmdQueue: ze_command_queue_handle_t, pPriority: Pointer[ze_command_queue_priority_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_command_queue_handle_t, ze_command_queue_priority_t)")
  def zeCommandQueueSetPriorityExt(hCommandQueue: ze_command_queue_handle_t, priority: ze_command_queue_priority_t): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_command_queue_handle_t, uint64_t)")
  def zeCommandQueueSynchronize(hCommandQueue: ze_command_queue_handle_t, timeout: uint64_t): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_driver_handle_t, const ze_context_desc_t *, ze_context_handle_t *)")
  def zeContextCreate(hDriver: ze_driver_handle_t, desc: Pointer[ze_context_desc_t, Read, Opaque], phContext: Pointer[ze_context_handle_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_driver_handle_t, const ze_context_desc_t *, uint32_t, ze_device_handle_t *, ze_context_handle_t *)")
  def zeContextCreateEx(hDriver: ze_driver_handle_t, desc: Pointer[ze_context_desc_t, Read, Opaque], numDevices: uint32_t, phDevices: Pointer[ze_device_handle_t, ReadWrite, Opaque], phContext: Pointer[ze_context_handle_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_context_handle_t)")
  def zeContextDestroy(hContext: ze_context_handle_t): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_context_handle_t, ze_device_handle_t, ze_image_handle_t)")
  def zeContextEvictImage(hContext: ze_context_handle_t, hDevice: ze_device_handle_t, hImage: ze_image_handle_t): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_context_handle_t, ze_device_handle_t, void *, size_t)")
  def zeContextEvictMemory(hContext: ze_context_handle_t, hDevice: ze_device_handle_t, ptr: Pointer[CVoid, ReadWrite, Opaque], size: size_t): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_context_handle_t)")
  def zeContextGetStatus(hContext: ze_context_handle_t): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_context_handle_t, ze_device_handle_t, ze_image_handle_t)")
  def zeContextMakeImageResident(hContext: ze_context_handle_t, hDevice: ze_device_handle_t, hImage: ze_image_handle_t): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_context_handle_t, ze_device_handle_t, void *, size_t)")
  def zeContextMakeMemoryResident(hContext: ze_context_handle_t, hDevice: ze_device_handle_t, ptr: Pointer[CVoid, ReadWrite, Opaque], size: size_t): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_context_handle_t, ze_device_handle_t)")
  def zeContextSystemBarrier(hContext: ze_context_handle_t, hDevice: ze_device_handle_t): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_device_handle_t, ze_device_handle_t, ze_bool_t *)")
  def zeDeviceCanAccessPeer(hDevice: ze_device_handle_t, hPeerDevice: ze_device_handle_t, value: Pointer[ze_bool_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_driver_handle_t, uint32_t *, ze_device_handle_t *)")
  def zeDeviceGet(hDriver: ze_driver_handle_t, pCount: Pointer[uint32_t, ReadWrite, Opaque], phDevices: Pointer[ze_device_handle_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_device_handle_t, uint32_t *)")
  def zeDeviceGetAggregatedCopyOffloadIncrementValue(hDevice: ze_device_handle_t, incrementValue: Pointer[uint32_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_device_handle_t, uint32_t *, ze_device_cache_properties_t *)")
  def zeDeviceGetCacheProperties(hDevice: ze_device_handle_t, pCount: Pointer[uint32_t, ReadWrite, Opaque], pCacheProperties: Pointer[ze_device_cache_properties_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_device_handle_t, uint32_t *, ze_command_queue_group_properties_t *)")
  def zeDeviceGetCommandQueueGroupProperties(hDevice: ze_device_handle_t, pCount: Pointer[uint32_t, ReadWrite, Opaque], pCommandQueueGroupProperties: Pointer[ze_command_queue_group_properties_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_device_handle_t, ze_device_compiler_info_t, const void *, size_t *, void *)")
  def zeDeviceGetCompilerInfo(hDevice: ze_device_handle_t, paramName: ze_device_compiler_info_t, pNext: Pointer[CVoid, Read, Opaque], pSize: Pointer[size_t, ReadWrite, Opaque], pData: Pointer[CVoid, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_device_handle_t, ze_device_compute_properties_t *)")
  def zeDeviceGetComputeProperties(hDevice: ze_device_handle_t, pComputeProperties: Pointer[ze_device_compute_properties_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_device_handle_t, uint64_t *)")
  def zeDeviceGetCounterBasedEventMaxValue(hDevice: ze_device_handle_t, maxValue: Pointer[uint64_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_device_handle_t, ze_device_external_memory_properties_t *)")
  def zeDeviceGetExternalMemoryProperties(hDevice: ze_device_handle_t, pExternalMemoryProperties: Pointer[ze_device_external_memory_properties_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_device_handle_t, ze_fabric_vertex_handle_t *)")
  def zeDeviceGetFabricVertexExp(hDevice: ze_device_handle_t, phVertex: Pointer[ze_fabric_vertex_handle_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_device_handle_t, uint64_t *, uint64_t *)")
  def zeDeviceGetGlobalTimestamps(hDevice: ze_device_handle_t, hostTimestamp: Pointer[uint64_t, ReadWrite, Opaque], deviceTimestamp: Pointer[uint64_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_device_handle_t, ze_device_image_properties_t *)")
  def zeDeviceGetImageProperties(hDevice: ze_device_handle_t, pImageProperties: Pointer[ze_device_image_properties_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_device_handle_t, ze_device_memory_access_properties_t *)")
  def zeDeviceGetMemoryAccessProperties(hDevice: ze_device_handle_t, pMemAccessProperties: Pointer[ze_device_memory_access_properties_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_device_handle_t, uint32_t *, ze_device_memory_properties_t *)")
  def zeDeviceGetMemoryProperties(hDevice: ze_device_handle_t, pCount: Pointer[uint32_t, ReadWrite, Opaque], pMemProperties: Pointer[ze_device_memory_properties_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_device_handle_t, ze_device_module_properties_t *)")
  def zeDeviceGetModuleProperties(hDevice: ze_device_handle_t, pModuleProperties: Pointer[ze_device_module_properties_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_device_handle_t, ze_device_handle_t, ze_device_p2p_properties_t *)")
  def zeDeviceGetP2PProperties(hDevice: ze_device_handle_t, hPeerDevice: ze_device_handle_t, pP2PProperties: Pointer[ze_device_p2p_properties_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_device_handle_t, ze_device_properties_t *)")
  def zeDeviceGetProperties(hDevice: ze_device_handle_t, pDeviceProperties: Pointer[ze_device_properties_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_device_handle_t, ze_device_handle_t *)")
  def zeDeviceGetRootDevice(hDevice: ze_device_handle_t, phRootDevice: Pointer[ze_device_handle_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_device_handle_t, const void *, size_t *, char *)")
  def zeDeviceGetRuntimeRequirements(hDevice: ze_device_handle_t, pObjDesc: Pointer[CVoid, Read, Opaque], pSize: Pointer[size_t, ReadWrite, Opaque], pRequirements: Pointer[Byte, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_device_handle_t, const char **)")
  def zeDeviceGetRuntimeRequirementsKey(hDevice: ze_device_handle_t, pKey: Pointer[Pointer[Byte, ReadWrite, Opaque], Read, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_device_handle_t)")
  def zeDeviceGetStatus(hDevice: ze_device_handle_t): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_device_handle_t, uint32_t *, ze_device_handle_t *)")
  def zeDeviceGetSubDevices(hDevice: ze_device_handle_t, pCount: Pointer[uint32_t, ReadWrite, Opaque], phSubdevices: Pointer[ze_device_handle_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_device_handle_t, uint32_t *, ze_device_vector_width_properties_ext_t *)")
  def zeDeviceGetVectorWidthPropertiesExt(hDevice: ze_device_handle_t, pCount: Pointer[uint32_t, ReadWrite, Opaque], pVectorWidthProperties: Pointer[ze_device_vector_width_properties_ext_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_device_handle_t, const ze_external_semaphore_ext_desc_t *, ze_external_semaphore_ext_handle_t *)")
  def zeDeviceImportExternalSemaphoreExt(hDevice: ze_device_handle_t, desc: Pointer[ze_external_semaphore_ext_desc_t, Read, Opaque], phSemaphore: Pointer[ze_external_semaphore_ext_handle_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_device_handle_t, ze_pci_ext_properties_t *)")
  def zeDevicePciGetPropertiesExt(hDevice: ze_device_handle_t, pPciProperties: Pointer[ze_pci_ext_properties_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_external_semaphore_ext_handle_t)")
  def zeDeviceReleaseExternalSemaphoreExt(hSemaphore: ze_external_semaphore_ext_handle_t): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_device_handle_t, size_t, size_t)")
  def zeDeviceReserveCacheExt(hDevice: ze_device_handle_t, cacheLevel: size_t, cacheReservationSize: size_t): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_device_handle_t, void *, size_t, ze_cache_ext_region_t)")
  def zeDeviceSetCacheAdviceExt(hDevice: ze_device_handle_t, ptr: Pointer[CVoid, ReadWrite, Opaque], regionSize: size_t, cacheRegion: ze_cache_ext_region_t): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_device_handle_t)")
  def zeDeviceSynchronize(hDevice: ze_device_handle_t): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_device_handle_t, const char *, ze_validate_runtime_requirements_output_t *)")
  def zeDeviceValidateRuntimeRequirements(hDevice: ze_device_handle_t, pRequirements: CString, pOut: Pointer[ze_validate_runtime_requirements_output_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (uint32_t *, ze_driver_handle_t *)")
  def zeDriverGet(pCount: Pointer[uint32_t, ReadWrite, Opaque], phDrivers: Pointer[ze_driver_handle_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_driver_handle_t, ze_api_version_t *)")
  def zeDriverGetApiVersion(hDriver: ze_driver_handle_t, version: Pointer[ze_api_version_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_context_handle_t (ze_driver_handle_t)")
  def zeDriverGetDefaultContext(hDriver: ze_driver_handle_t): ze_context_handle_t = unsupported
  @CSignature("ze_result_t (ze_driver_handle_t, const char *, void **)")
  def zeDriverGetExtensionFunctionAddress(hDriver: ze_driver_handle_t, name: CString, ppFunctionAddress: Pointer[Pointer[CVoid, ReadWrite, Opaque], ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_driver_handle_t, uint32_t *, ze_driver_extension_properties_t *)")
  def zeDriverGetExtensionProperties(hDriver: ze_driver_handle_t, pCount: Pointer[uint32_t, ReadWrite, Opaque], pExtensionProperties: Pointer[ze_driver_extension_properties_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_driver_handle_t, ze_driver_ipc_properties_t *)")
  def zeDriverGetIpcProperties(hDriver: ze_driver_handle_t, pIpcProperties: Pointer[ze_driver_ipc_properties_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_driver_handle_t, const char **)")
  def zeDriverGetLastErrorDescription(hDriver: ze_driver_handle_t, ppString: Pointer[Pointer[Byte, ReadWrite, Opaque], Read, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_driver_handle_t, ze_driver_properties_t *)")
  def zeDriverGetProperties(hDriver: ze_driver_handle_t, pDriverProperties: Pointer[ze_driver_properties_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_driver_handle_t, ze_rtas_format_exp_t, ze_rtas_format_exp_t)")
  def zeDriverRTASFormatCompatibilityCheckExp(hDriver: ze_driver_handle_t, rtasFormatA: ze_rtas_format_exp_t, rtasFormatB: ze_rtas_format_exp_t): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_driver_handle_t, ze_rtas_format_ext_t, ze_rtas_format_ext_t)")
  def zeDriverRTASFormatCompatibilityCheckExt(hDriver: ze_driver_handle_t, rtasFormatA: ze_rtas_format_ext_t, rtasFormatB: ze_rtas_format_ext_t): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_event_handle_t)")
  def zeEventCounterBasedCloseIpcHandle(hEvent: ze_event_handle_t): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_context_handle_t, ze_device_handle_t, const ze_event_counter_based_desc_t *, ze_event_handle_t *)")
  def zeEventCounterBasedCreate(hContext: ze_context_handle_t, hDevice: ze_device_handle_t, desc: Pointer[ze_event_counter_based_desc_t, Read, Opaque], phEvent: Pointer[ze_event_handle_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_event_handle_t, uint64_t *, uint64_t *)")
  def zeEventCounterBasedGetDeviceAddress(hEvent: ze_event_handle_t, completionValue: Pointer[uint64_t, ReadWrite, Opaque], deviceAddress: Pointer[uint64_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_event_handle_t, ze_ipc_event_counter_based_handle_t *)")
  def zeEventCounterBasedGetIpcHandle(hEvent: ze_event_handle_t, phIpc: Pointer[ze_ipc_event_counter_based_handle_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_context_handle_t, ze_ipc_event_counter_based_handle_t, ze_event_handle_t *)")
  def zeEventCounterBasedOpenIpcHandle(hContext: ze_context_handle_t, hIpc: ze_ipc_event_counter_based_handle_t, phEvent: Pointer[ze_event_handle_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_event_pool_handle_t, const ze_event_desc_t *, ze_event_handle_t *)")
  def zeEventCreate(hEventPool: ze_event_pool_handle_t, desc: Pointer[ze_event_desc_t, Read, Opaque], phEvent: Pointer[ze_event_handle_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_event_handle_t)")
  def zeEventDestroy(hEvent: ze_event_handle_t): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_event_handle_t, ze_event_counter_based_flags_t *)")
  def zeEventGetCounterBasedFlags(hEvent: ze_event_handle_t, pFlags: Pointer[ze_event_counter_based_flags_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_event_handle_t, ze_event_pool_handle_t *)")
  def zeEventGetEventPool(hEvent: ze_event_handle_t, phEventPool: Pointer[ze_event_pool_handle_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_event_handle_t, ze_event_scope_flags_t *)")
  def zeEventGetSignalScope(hEvent: ze_event_handle_t, pSignalScope: Pointer[ze_event_scope_flags_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_event_handle_t, ze_event_scope_flags_t *)")
  def zeEventGetWaitScope(hEvent: ze_event_handle_t, pWaitScope: Pointer[ze_event_scope_flags_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_event_handle_t)")
  def zeEventHostReset(hEvent: ze_event_handle_t): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_event_handle_t)")
  def zeEventHostSignal(hEvent: ze_event_handle_t): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_event_handle_t, uint64_t)")
  def zeEventHostSynchronize(hEvent: ze_event_handle_t, timeout: uint64_t): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_event_pool_handle_t)")
  def zeEventPoolCloseIpcHandle(hEventPool: ze_event_pool_handle_t): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_context_handle_t, const ze_event_pool_desc_t *, uint32_t, ze_device_handle_t *, ze_event_pool_handle_t *)")
  def zeEventPoolCreate(hContext: ze_context_handle_t, desc: Pointer[ze_event_pool_desc_t, Read, Opaque], numDevices: uint32_t, phDevices: Pointer[ze_device_handle_t, ReadWrite, Opaque], phEventPool: Pointer[ze_event_pool_handle_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_event_pool_handle_t)")
  def zeEventPoolDestroy(hEventPool: ze_event_pool_handle_t): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_event_pool_handle_t, ze_context_handle_t *)")
  def zeEventPoolGetContextHandle(hEventPool: ze_event_pool_handle_t, phContext: Pointer[ze_context_handle_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_event_pool_handle_t, ze_event_pool_flags_t *)")
  def zeEventPoolGetFlags(hEventPool: ze_event_pool_handle_t, pFlags: Pointer[ze_event_pool_flags_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_event_pool_handle_t, ze_ipc_event_pool_handle_t *)")
  def zeEventPoolGetIpcHandle(hEventPool: ze_event_pool_handle_t, phIpc: Pointer[ze_ipc_event_pool_handle_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_context_handle_t, ze_ipc_event_pool_handle_t, ze_event_pool_handle_t *)")
  def zeEventPoolOpenIpcHandle(hContext: ze_context_handle_t, hIpc: ze_ipc_event_pool_handle_t, phEventPool: Pointer[ze_event_pool_handle_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_context_handle_t, ze_ipc_event_pool_handle_t)")
  def zeEventPoolPutIpcHandle(hContext: ze_context_handle_t, hIpc: ze_ipc_event_pool_handle_t): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_event_handle_t, ze_kernel_timestamp_result_t *)")
  def zeEventQueryKernelTimestamp(hEvent: ze_event_handle_t, dstptr: Pointer[ze_kernel_timestamp_result_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_event_handle_t, ze_device_handle_t, uint32_t *, ze_event_query_kernel_timestamps_results_ext_properties_t *)")
  def zeEventQueryKernelTimestampsExt(hEvent: ze_event_handle_t, hDevice: ze_device_handle_t, pCount: Pointer[uint32_t, ReadWrite, Opaque], pResults: Pointer[ze_event_query_kernel_timestamps_results_ext_properties_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_event_handle_t)")
  def zeEventQueryStatus(hEvent: ze_event_handle_t): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_event_handle_t, ze_device_handle_t, uint32_t *, ze_kernel_timestamp_result_t *)")
  def zeEventQueryTimestampsExp(hEvent: ze_event_handle_t, hDevice: ze_device_handle_t, pCount: Pointer[uint32_t, ReadWrite, Opaque], pTimestamps: Pointer[ze_kernel_timestamp_result_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_executable_graph_handle_t)")
  def zeExecutableGraphDestroyExt(hGraph: ze_executable_graph_handle_t): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_executable_graph_handle_t, ze_graph_handle_t *)")
  def zeExecutableGraphGetSourceGraphExt(hGraph: ze_executable_graph_handle_t, phSourceGraph: Pointer[ze_graph_handle_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_fabric_vertex_handle_t, ze_fabric_vertex_handle_t, uint32_t *, ze_fabric_edge_handle_t *)")
  def zeFabricEdgeGetExp(hVertexA: ze_fabric_vertex_handle_t, hVertexB: ze_fabric_vertex_handle_t, pCount: Pointer[uint32_t, ReadWrite, Opaque], phEdges: Pointer[ze_fabric_edge_handle_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_fabric_edge_handle_t, ze_fabric_edge_exp_properties_t *)")
  def zeFabricEdgeGetPropertiesExp(hEdge: ze_fabric_edge_handle_t, pEdgeProperties: Pointer[ze_fabric_edge_exp_properties_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_fabric_edge_handle_t, ze_fabric_vertex_handle_t *, ze_fabric_vertex_handle_t *)")
  def zeFabricEdgeGetVerticesExp(hEdge: ze_fabric_edge_handle_t, phVertexA: Pointer[ze_fabric_vertex_handle_t, ReadWrite, Opaque], phVertexB: Pointer[ze_fabric_vertex_handle_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_fabric_vertex_handle_t, ze_device_handle_t *)")
  def zeFabricVertexGetDeviceExp(hVertex: ze_fabric_vertex_handle_t, phDevice: Pointer[ze_device_handle_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_driver_handle_t, uint32_t *, ze_fabric_vertex_handle_t *)")
  def zeFabricVertexGetExp(hDriver: ze_driver_handle_t, pCount: Pointer[uint32_t, ReadWrite, Opaque], phVertices: Pointer[ze_fabric_vertex_handle_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_fabric_vertex_handle_t, ze_fabric_vertex_exp_properties_t *)")
  def zeFabricVertexGetPropertiesExp(hVertex: ze_fabric_vertex_handle_t, pVertexProperties: Pointer[ze_fabric_vertex_exp_properties_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_fabric_vertex_handle_t, uint32_t *, ze_fabric_vertex_handle_t *)")
  def zeFabricVertexGetSubVerticesExp(hVertex: ze_fabric_vertex_handle_t, pCount: Pointer[uint32_t, ReadWrite, Opaque], phSubvertices: Pointer[ze_fabric_vertex_handle_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_command_queue_handle_t, const ze_fence_desc_t *, ze_fence_handle_t *)")
  def zeFenceCreate(hCommandQueue: ze_command_queue_handle_t, desc: Pointer[ze_fence_desc_t, Read, Opaque], phFence: Pointer[ze_fence_handle_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_fence_handle_t)")
  def zeFenceDestroy(hFence: ze_fence_handle_t): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_fence_handle_t, uint64_t)")
  def zeFenceHostSynchronize(hFence: ze_fence_handle_t, timeout: uint64_t): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_fence_handle_t)")
  def zeFenceQueryStatus(hFence: ze_fence_handle_t): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_fence_handle_t)")
  def zeFenceReset(hFence: ze_fence_handle_t): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_context_handle_t, const void *, ze_graph_handle_t *)")
  def zeGraphCreateExt(hContext: ze_context_handle_t, pNext: Pointer[CVoid, Read, Opaque], phGraph: Pointer[ze_graph_handle_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_graph_handle_t)")
  def zeGraphDestroyExt(hGraph: ze_graph_handle_t): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_graph_handle_t, const char *, const void *)")
  def zeGraphDumpContentsExt(hGraph: ze_graph_handle_t, filePath: CString, pNext: Pointer[CVoid, Read, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_graph_handle_t, uint64_t *)")
  def zeGraphGetIdExt(hGraph: ze_graph_handle_t, pGraphId: Pointer[uint64_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_graph_handle_t, ze_command_list_handle_t *)")
  def zeGraphGetPrimaryCommandListExt(hGraph: ze_graph_handle_t, phCommandList: Pointer[ze_command_list_handle_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_graph_handle_t, const void *, ze_executable_graph_handle_t *)")
  def zeGraphInstantiateExt(hGraph: ze_graph_handle_t, pNext: Pointer[CVoid, Read, Opaque], phExecutableGraph: Pointer[ze_executable_graph_handle_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_graph_handle_t)")
  def zeGraphIsEmptyExt(hGraph: ze_graph_handle_t): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_graph_handle_t)")
  def zeGraphPauseCaptureExt(hGraph: ze_graph_handle_t): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_graph_handle_t)")
  def zeGraphResumeCaptureExt(hGraph: ze_graph_handle_t): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_graph_handle_t, zex_mem_graph_free_callback_fn_t, void *, const void *)")
  def zeGraphSetDestructionCallbackExt(hGraph: ze_graph_handle_t, pfnCallback: zex_mem_graph_free_callback_fn_t, pUserData: Pointer[CVoid, ReadWrite, Opaque], pNext: Pointer[CVoid, Read, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_context_handle_t, ze_device_handle_t, const ze_image_desc_t *, ze_image_handle_t *)")
  def zeImageCreate(hContext: ze_context_handle_t, hDevice: ze_device_handle_t, desc: Pointer[ze_image_desc_t, Read, Opaque], phImage: Pointer[ze_image_handle_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_image_handle_t)")
  def zeImageDestroy(hImage: ze_image_handle_t): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_context_handle_t, ze_image_handle_t, ze_image_allocation_ext_properties_t *)")
  def zeImageGetAllocPropertiesExt(hContext: ze_context_handle_t, hImage: ze_image_handle_t, pImageAllocProperties: Pointer[ze_image_allocation_ext_properties_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_image_handle_t, uint64_t *)")
  def zeImageGetDeviceOffsetExp(hImage: ze_image_handle_t, pDeviceOffset: Pointer[uint64_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_image_handle_t, ze_image_memory_properties_exp_t *)")
  def zeImageGetMemoryPropertiesExp(hImage: ze_image_handle_t, pMemoryProperties: Pointer[ze_image_memory_properties_exp_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_device_handle_t, const ze_image_desc_t *, ze_image_properties_t *)")
  def zeImageGetProperties(hDevice: ze_device_handle_t, desc: Pointer[ze_image_desc_t, Read, Opaque], pImageProperties: Pointer[ze_image_properties_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_context_handle_t, ze_device_handle_t, const ze_image_desc_t *, ze_image_handle_t, ze_image_handle_t *)")
  def zeImageViewCreateExp(hContext: ze_context_handle_t, hDevice: ze_device_handle_t, desc: Pointer[ze_image_desc_t, Read, Opaque], hImage: ze_image_handle_t, phImageView: Pointer[ze_image_handle_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_context_handle_t, ze_device_handle_t, const ze_image_desc_t *, ze_image_handle_t, ze_image_handle_t *)")
  def zeImageViewCreateExt(hContext: ze_context_handle_t, hDevice: ze_device_handle_t, desc: Pointer[ze_image_desc_t, Read, Opaque], hImage: ze_image_handle_t, phImageView: Pointer[ze_image_handle_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_init_flags_t)")
  def zeInit(flags: ze_init_flags_t): ze_result_t = unsupported
  @CSignature("ze_result_t (uint32_t *, ze_driver_handle_t *, ze_init_driver_type_desc_t *)")
  def zeInitDrivers(pCount: Pointer[uint32_t, ReadWrite, Opaque], phDrivers: Pointer[ze_driver_handle_t, ReadWrite, Opaque], desc: Pointer[ze_init_driver_type_desc_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_module_handle_t, const ze_kernel_desc_t *, ze_kernel_handle_t *)")
  def zeKernelCreate(hModule: ze_module_handle_t, desc: Pointer[ze_kernel_desc_t, Read, Opaque], phKernel: Pointer[ze_kernel_handle_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_kernel_handle_t)")
  def zeKernelDestroy(hKernel: ze_kernel_handle_t): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_kernel_handle_t, uint32_t *, ze_kernel_allocation_exp_properties_t *)")
  def zeKernelGetAllocationPropertiesExp(hKernel: ze_kernel_handle_t, pCount: Pointer[uint32_t, ReadWrite, Opaque], pAllocationProperties: Pointer[ze_kernel_allocation_exp_properties_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_kernel_handle_t, size_t *, uint8_t *)")
  def zeKernelGetBinaryExp(hKernel: ze_kernel_handle_t, pSize: Pointer[size_t, ReadWrite, Opaque], pKernelBinary: Pointer[uint8_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_kernel_handle_t, ze_kernel_indirect_access_flags_t *)")
  def zeKernelGetIndirectAccess(hKernel: ze_kernel_handle_t, pFlags: Pointer[ze_kernel_indirect_access_flags_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_kernel_handle_t, ze_module_handle_t *)")
  def zeKernelGetModuleHandle(hKernel: ze_kernel_handle_t, phModule: Pointer[ze_module_handle_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_kernel_handle_t, size_t *, char *)")
  def zeKernelGetName(hKernel: ze_kernel_handle_t, pSize: Pointer[size_t, ReadWrite, Opaque], pName: Pointer[Byte, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_kernel_handle_t, ze_kernel_properties_t *)")
  def zeKernelGetProperties(hKernel: ze_kernel_handle_t, pKernelProperties: Pointer[ze_kernel_properties_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_kernel_handle_t, uint32_t *, char **)")
  def zeKernelGetSourceAttributes(hKernel: ze_kernel_handle_t, pSize: Pointer[uint32_t, ReadWrite, Opaque], pString: Pointer[Pointer[Byte, ReadWrite, Opaque], ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_kernel_handle_t, ze_scheduling_hint_exp_desc_t *)")
  def zeKernelSchedulingHintExp(hKernel: ze_kernel_handle_t, pHint: Pointer[ze_scheduling_hint_exp_desc_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_kernel_handle_t, uint32_t, size_t, const void *)")
  def zeKernelSetArgumentValue(hKernel: ze_kernel_handle_t, argIndex: uint32_t, argSize: size_t, pArgValue: Pointer[CVoid, Read, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_kernel_handle_t, ze_cache_config_flags_t)")
  def zeKernelSetCacheConfig(hKernel: ze_kernel_handle_t, flags: ze_cache_config_flags_t): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_kernel_handle_t, uint32_t, uint32_t, uint32_t)")
  def zeKernelSetGlobalOffsetExp(hKernel: ze_kernel_handle_t, offsetX: uint32_t, offsetY: uint32_t, offsetZ: uint32_t): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_kernel_handle_t, uint32_t, uint32_t, uint32_t)")
  def zeKernelSetGroupSize(hKernel: ze_kernel_handle_t, groupSizeX: uint32_t, groupSizeY: uint32_t, groupSizeZ: uint32_t): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_kernel_handle_t, ze_kernel_indirect_access_flags_t)")
  def zeKernelSetIndirectAccess(hKernel: ze_kernel_handle_t, flags: ze_kernel_indirect_access_flags_t): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_kernel_handle_t, uint32_t, uint32_t, uint32_t, uint32_t *, uint32_t *, uint32_t *)")
  def zeKernelSuggestGroupSize(hKernel: ze_kernel_handle_t, globalSizeX: uint32_t, globalSizeY: uint32_t, globalSizeZ: uint32_t, groupSizeX: Pointer[uint32_t, ReadWrite, Opaque], groupSizeY: Pointer[uint32_t, ReadWrite, Opaque], groupSizeZ: Pointer[uint32_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_kernel_handle_t, uint32_t *)")
  def zeKernelSuggestMaxCooperativeGroupCount(hKernel: ze_kernel_handle_t, totalGroupCount: Pointer[uint32_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_context_handle_t, const ze_device_mem_alloc_desc_t *, size_t, size_t, ze_device_handle_t, void **)")
  def zeMemAllocDevice(hContext: ze_context_handle_t, device_desc: Pointer[ze_device_mem_alloc_desc_t, Read, Opaque], size: size_t, alignment: size_t, hDevice: ze_device_handle_t, pptr: Pointer[Pointer[CVoid, ReadWrite, Opaque], ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_context_handle_t, const ze_host_mem_alloc_desc_t *, size_t, size_t, void **)")
  def zeMemAllocHost(hContext: ze_context_handle_t, host_desc: Pointer[ze_host_mem_alloc_desc_t, Read, Opaque], size: size_t, alignment: size_t, pptr: Pointer[Pointer[CVoid, ReadWrite, Opaque], ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_context_handle_t, const ze_device_mem_alloc_desc_t *, const ze_host_mem_alloc_desc_t *, size_t, size_t, ze_device_handle_t, void **)")
  def zeMemAllocShared(hContext: ze_context_handle_t, device_desc: Pointer[ze_device_mem_alloc_desc_t, Read, Opaque], host_desc: Pointer[ze_host_mem_alloc_desc_t, Read, Opaque], size: size_t, alignment: size_t, hDevice: ze_device_handle_t, pptr: Pointer[Pointer[CVoid, ReadWrite, Opaque], ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_context_handle_t, const void *)")
  def zeMemCloseIpcHandle(hContext: ze_context_handle_t, ptr: Pointer[CVoid, Read, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_context_handle_t, void *)")
  def zeMemFree(hContext: ze_context_handle_t, ptr: Pointer[CVoid, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_context_handle_t, const ze_memory_free_ext_desc_t *, void *)")
  def zeMemFreeExt(hContext: ze_context_handle_t, pMemFreeDesc: Pointer[ze_memory_free_ext_desc_t, Read, Opaque], ptr: Pointer[CVoid, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_context_handle_t, const void *, void **, size_t *)")
  def zeMemGetAddressRange(hContext: ze_context_handle_t, ptr: Pointer[CVoid, Read, Opaque], pBase: Pointer[Pointer[CVoid, ReadWrite, Opaque], ReadWrite, Opaque], pSize: Pointer[size_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_context_handle_t, const void *, ze_memory_allocation_properties_t *, ze_device_handle_t *)")
  def zeMemGetAllocProperties(hContext: ze_context_handle_t, ptr: Pointer[CVoid, Read, Opaque], pMemAllocProperties: Pointer[ze_memory_allocation_properties_t, ReadWrite, Opaque], phDevice: Pointer[ze_device_handle_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_context_handle_t, ze_device_handle_t, const void *, size_t, ze_memory_atomic_attr_exp_flags_t *)")
  def zeMemGetAtomicAccessAttributeExp(hContext: ze_context_handle_t, hDevice: ze_device_handle_t, ptr: Pointer[CVoid, Read, Opaque], size: size_t, pAttr: Pointer[ze_memory_atomic_attr_exp_flags_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_context_handle_t, ze_ipc_mem_handle_t, uint64_t *)")
  def zeMemGetFileDescriptorFromIpcHandleExp(hContext: ze_context_handle_t, ipcHandle: ze_ipc_mem_handle_t, pHandle: Pointer[uint64_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_context_handle_t, const void *, ze_ipc_mem_handle_t *)")
  def zeMemGetIpcHandle(hContext: ze_context_handle_t, ptr: Pointer[CVoid, Read, Opaque], pIpcHandle: Pointer[ze_ipc_mem_handle_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_context_handle_t, uint64_t, ze_ipc_mem_handle_t *)")
  def zeMemGetIpcHandleFromFileDescriptorExp(hContext: ze_context_handle_t, handle: uint64_t, pIpcHandle: Pointer[ze_ipc_mem_handle_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_context_handle_t, const void *, void *, ze_ipc_mem_handle_t *)")
  def zeMemGetIpcHandleWithProperties(hContext: ze_context_handle_t, ptr: Pointer[CVoid, Read, Opaque], pNext: Pointer[CVoid, ReadWrite, Opaque], pIpcHandle: Pointer[ze_ipc_mem_handle_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_context_handle_t, ze_device_handle_t, size_t, size_t, unsigned int, size_t *)")
  def zeMemGetPitchFor2dImage(hContext: ze_context_handle_t, hDevice: ze_device_handle_t, imageWidth: size_t, imageHeight: size_t, elementSizeInBytes: unsigned_int, rowPitch: Pointer[size_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_context_handle_t, ze_device_handle_t, ze_ipc_mem_handle_t, ze_ipc_memory_flags_t, void **)")
  def zeMemOpenIpcHandle(hContext: ze_context_handle_t, hDevice: ze_device_handle_t, handle: ze_ipc_mem_handle_t, flags: ze_ipc_memory_flags_t, pptr: Pointer[Pointer[CVoid, ReadWrite, Opaque], ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_context_handle_t, ze_ipc_mem_handle_t)")
  def zeMemPutIpcHandle(hContext: ze_context_handle_t, handle: ze_ipc_mem_handle_t): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_context_handle_t, ze_device_handle_t, const void *, size_t, ze_memory_atomic_attr_exp_flags_t)")
  def zeMemSetAtomicAccessAttributeExp(hContext: ze_context_handle_t, hDevice: ze_device_handle_t, ptr: Pointer[CVoid, Read, Opaque], size: size_t, attr: ze_memory_atomic_attr_exp_flags_t): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_module_build_log_handle_t)")
  def zeModuleBuildLogDestroy(hModuleBuildLog: ze_module_build_log_handle_t): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_module_build_log_handle_t, size_t *, char *)")
  def zeModuleBuildLogGetString(hModuleBuildLog: ze_module_build_log_handle_t, pSize: Pointer[size_t, ReadWrite, Opaque], pBuildLog: Pointer[Byte, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_context_handle_t, ze_device_handle_t, const ze_module_desc_t *, ze_module_handle_t *, ze_module_build_log_handle_t *)")
  def zeModuleCreate(hContext: ze_context_handle_t, hDevice: ze_device_handle_t, desc: Pointer[ze_module_desc_t, Read, Opaque], phModule: Pointer[ze_module_handle_t, ReadWrite, Opaque], phBuildLog: Pointer[ze_module_build_log_handle_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_module_handle_t)")
  def zeModuleDestroy(hModule: ze_module_handle_t): ze_result_t = unsupported
  @CSignature("ze_result_t (uint32_t, ze_module_handle_t *, ze_module_build_log_handle_t *)")
  def zeModuleDynamicLink(numModules: uint32_t, phModules: Pointer[ze_module_handle_t, ReadWrite, Opaque], phLinkLog: Pointer[ze_module_build_log_handle_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_module_handle_t, ze_device_handle_t *)")
  def zeModuleGetDeviceHandle(hModule: ze_module_handle_t, phDevice: Pointer[ze_device_handle_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_module_handle_t, const char *, void **)")
  def zeModuleGetFunctionPointer(hModule: ze_module_handle_t, pFunctionName: CString, pfnFunction: Pointer[Pointer[CVoid, ReadWrite, Opaque], ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_module_handle_t, const char *, size_t *, void **)")
  def zeModuleGetGlobalPointer(hModule: ze_module_handle_t, pGlobalName: CString, pSize: Pointer[size_t, ReadWrite, Opaque], pptr: Pointer[Pointer[CVoid, ReadWrite, Opaque], ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_module_handle_t, uint32_t *, const char **)")
  def zeModuleGetKernelNames(hModule: ze_module_handle_t, pCount: Pointer[uint32_t, ReadWrite, Opaque], pNames: Pointer[Pointer[Byte, ReadWrite, Opaque], Read, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_module_handle_t, size_t *, uint8_t *)")
  def zeModuleGetNativeBinary(hModule: ze_module_handle_t, pSize: Pointer[size_t, ReadWrite, Opaque], pModuleNativeBinary: Pointer[uint8_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_module_handle_t, ze_module_properties_t *)")
  def zeModuleGetProperties(hModule: ze_module_handle_t, pModuleProperties: Pointer[ze_module_properties_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_linkage_inspection_ext_desc_t *, uint32_t, ze_module_handle_t *, ze_module_build_log_handle_t *)")
  def zeModuleInspectLinkageExt(pInspectDesc: Pointer[ze_linkage_inspection_ext_desc_t, ReadWrite, Opaque], numModules: uint32_t, phModules: Pointer[ze_module_handle_t, ReadWrite, Opaque], phLog: Pointer[ze_module_build_log_handle_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_context_handle_t, ze_device_handle_t, ze_physical_mem_desc_t *, ze_physical_mem_handle_t *)")
  def zePhysicalMemCreate(hContext: ze_context_handle_t, hDevice: ze_device_handle_t, desc: Pointer[ze_physical_mem_desc_t, ReadWrite, Opaque], phPhysicalMemory: Pointer[ze_physical_mem_handle_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_context_handle_t, ze_physical_mem_handle_t)")
  def zePhysicalMemDestroy(hContext: ze_context_handle_t, hPhysicalMemory: ze_physical_mem_handle_t): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_context_handle_t, ze_physical_mem_handle_t, ze_physical_mem_properties_t *)")
  def zePhysicalMemGetProperties(hContext: ze_context_handle_t, hPhysicalMem: ze_physical_mem_handle_t, pMemProperties: Pointer[ze_physical_mem_properties_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_rtas_builder_exp_handle_t, const ze_rtas_builder_build_op_exp_desc_t *, void *, size_t, void *, size_t, ze_rtas_parallel_operation_exp_handle_t, void *, ze_rtas_aabb_exp_t *, size_t *)")
  def zeRTASBuilderBuildExp(hBuilder: ze_rtas_builder_exp_handle_t, pBuildOpDescriptor: Pointer[ze_rtas_builder_build_op_exp_desc_t, Read, Opaque], pScratchBuffer: Pointer[CVoid, ReadWrite, Opaque], scratchBufferSizeBytes: size_t, pRtasBuffer: Pointer[CVoid, ReadWrite, Opaque], rtasBufferSizeBytes: size_t, hParallelOperation: ze_rtas_parallel_operation_exp_handle_t, pBuildUserPtr: Pointer[CVoid, ReadWrite, Opaque], pBounds: Pointer[ze_rtas_aabb_exp_t, ReadWrite, Opaque], pRtasBufferSizeBytes: Pointer[size_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_rtas_builder_ext_handle_t, const ze_rtas_builder_build_op_ext_desc_t *, void *, size_t, void *, size_t, ze_rtas_parallel_operation_ext_handle_t, void *, ze_rtas_aabb_ext_t *, size_t *)")
  def zeRTASBuilderBuildExt(hBuilder: ze_rtas_builder_ext_handle_t, pBuildOpDescriptor: Pointer[ze_rtas_builder_build_op_ext_desc_t, Read, Opaque], pScratchBuffer: Pointer[CVoid, ReadWrite, Opaque], scratchBufferSizeBytes: size_t, pRtasBuffer: Pointer[CVoid, ReadWrite, Opaque], rtasBufferSizeBytes: size_t, hParallelOperation: ze_rtas_parallel_operation_ext_handle_t, pBuildUserPtr: Pointer[CVoid, ReadWrite, Opaque], pBounds: Pointer[ze_rtas_aabb_ext_t, ReadWrite, Opaque], pRtasBufferSizeBytes: Pointer[size_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_command_list_handle_t, void *, const void *, size_t, ze_event_handle_t, uint32_t, ze_event_handle_t *)")
  def zeRTASBuilderCommandListAppendCopyExt(hCommandList: ze_command_list_handle_t, dstptr: Pointer[CVoid, ReadWrite, Opaque], srcptr: Pointer[CVoid, Read, Opaque], size: size_t, hSignalEvent: ze_event_handle_t, numWaitEvents: uint32_t, phWaitEvents: Pointer[ze_event_handle_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_driver_handle_t, const ze_rtas_builder_exp_desc_t *, ze_rtas_builder_exp_handle_t *)")
  def zeRTASBuilderCreateExp(hDriver: ze_driver_handle_t, pDescriptor: Pointer[ze_rtas_builder_exp_desc_t, Read, Opaque], phBuilder: Pointer[ze_rtas_builder_exp_handle_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_driver_handle_t, const ze_rtas_builder_ext_desc_t *, ze_rtas_builder_ext_handle_t *)")
  def zeRTASBuilderCreateExt(hDriver: ze_driver_handle_t, pDescriptor: Pointer[ze_rtas_builder_ext_desc_t, Read, Opaque], phBuilder: Pointer[ze_rtas_builder_ext_handle_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_rtas_builder_exp_handle_t)")
  def zeRTASBuilderDestroyExp(hBuilder: ze_rtas_builder_exp_handle_t): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_rtas_builder_ext_handle_t)")
  def zeRTASBuilderDestroyExt(hBuilder: ze_rtas_builder_ext_handle_t): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_rtas_builder_exp_handle_t, const ze_rtas_builder_build_op_exp_desc_t *, ze_rtas_builder_exp_properties_t *)")
  def zeRTASBuilderGetBuildPropertiesExp(hBuilder: ze_rtas_builder_exp_handle_t, pBuildOpDescriptor: Pointer[ze_rtas_builder_build_op_exp_desc_t, Read, Opaque], pProperties: Pointer[ze_rtas_builder_exp_properties_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_rtas_builder_ext_handle_t, const ze_rtas_builder_build_op_ext_desc_t *, ze_rtas_builder_ext_properties_t *)")
  def zeRTASBuilderGetBuildPropertiesExt(hBuilder: ze_rtas_builder_ext_handle_t, pBuildOpDescriptor: Pointer[ze_rtas_builder_build_op_ext_desc_t, Read, Opaque], pProperties: Pointer[ze_rtas_builder_ext_properties_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_driver_handle_t, ze_rtas_parallel_operation_exp_handle_t *)")
  def zeRTASParallelOperationCreateExp(hDriver: ze_driver_handle_t, phParallelOperation: Pointer[ze_rtas_parallel_operation_exp_handle_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_driver_handle_t, ze_rtas_parallel_operation_ext_handle_t *)")
  def zeRTASParallelOperationCreateExt(hDriver: ze_driver_handle_t, phParallelOperation: Pointer[ze_rtas_parallel_operation_ext_handle_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_rtas_parallel_operation_exp_handle_t)")
  def zeRTASParallelOperationDestroyExp(hParallelOperation: ze_rtas_parallel_operation_exp_handle_t): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_rtas_parallel_operation_ext_handle_t)")
  def zeRTASParallelOperationDestroyExt(hParallelOperation: ze_rtas_parallel_operation_ext_handle_t): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_rtas_parallel_operation_exp_handle_t, ze_rtas_parallel_operation_exp_properties_t *)")
  def zeRTASParallelOperationGetPropertiesExp(hParallelOperation: ze_rtas_parallel_operation_exp_handle_t, pProperties: Pointer[ze_rtas_parallel_operation_exp_properties_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_rtas_parallel_operation_ext_handle_t, ze_rtas_parallel_operation_ext_properties_t *)")
  def zeRTASParallelOperationGetPropertiesExt(hParallelOperation: ze_rtas_parallel_operation_ext_handle_t, pProperties: Pointer[ze_rtas_parallel_operation_ext_properties_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_rtas_parallel_operation_exp_handle_t)")
  def zeRTASParallelOperationJoinExp(hParallelOperation: ze_rtas_parallel_operation_exp_handle_t): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_rtas_parallel_operation_ext_handle_t)")
  def zeRTASParallelOperationJoinExt(hParallelOperation: ze_rtas_parallel_operation_ext_handle_t): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_context_handle_t, ze_device_handle_t, const ze_sampler_desc_t *, ze_sampler_handle_t *)")
  def zeSamplerCreate(hContext: ze_context_handle_t, hDevice: ze_device_handle_t, desc: Pointer[ze_sampler_desc_t, Read, Opaque], phSampler: Pointer[ze_sampler_handle_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_sampler_handle_t)")
  def zeSamplerDestroy(hSampler: ze_sampler_handle_t): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_context_handle_t, const void *, size_t)")
  def zeVirtualMemFree(hContext: ze_context_handle_t, ptr: Pointer[CVoid, Read, Opaque], size: size_t): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_context_handle_t, const void *, size_t, ze_memory_access_attribute_t *, size_t *)")
  def zeVirtualMemGetAccessAttribute(hContext: ze_context_handle_t, ptr: Pointer[CVoid, Read, Opaque], size: size_t, access: Pointer[ze_memory_access_attribute_t, ReadWrite, Opaque], outSize: Pointer[size_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_context_handle_t, const void *, size_t, ze_physical_mem_handle_t, size_t, ze_memory_access_attribute_t)")
  def zeVirtualMemMap(hContext: ze_context_handle_t, ptr: Pointer[CVoid, Read, Opaque], size: size_t, hPhysicalMemory: ze_physical_mem_handle_t, offset: size_t, access: ze_memory_access_attribute_t): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_context_handle_t, ze_device_handle_t, size_t, size_t *)")
  def zeVirtualMemQueryPageSize(hContext: ze_context_handle_t, hDevice: ze_device_handle_t, size: size_t, pagesize: Pointer[size_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_context_handle_t, const void *, size_t, void **)")
  def zeVirtualMemReserve(hContext: ze_context_handle_t, pStart: Pointer[CVoid, Read, Opaque], size: size_t, pptr: Pointer[Pointer[CVoid, ReadWrite, Opaque], ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_context_handle_t, const void *, size_t, ze_memory_access_attribute_t)")
  def zeVirtualMemSetAccessAttribute(hContext: ze_context_handle_t, ptr: Pointer[CVoid, Read, Opaque], size: size_t, access: ze_memory_access_attribute_t): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_context_handle_t, const void *, size_t)")
  def zeVirtualMemUnmap(hContext: ze_context_handle_t, ptr: Pointer[CVoid, Read, Opaque], size: size_t): ze_result_t = unsupported
  @CSignature("ze_result_t (zes_device_handle_t, ze_bool_t *)")
  def zesDeviceEccAvailable(hDevice: zes_device_handle_t, pAvailable: Pointer[ze_bool_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (zes_device_handle_t, ze_bool_t *)")
  def zesDeviceEccConfigurable(hDevice: zes_device_handle_t, pConfigurable: Pointer[ze_bool_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (zes_device_handle_t, uint32_t *, zes_vf_handle_t *)")
  def zesDeviceEnumActiveVFExp(hDevice: zes_device_handle_t, pCount: Pointer[uint32_t, ReadWrite, Opaque], phVFhandle: Pointer[zes_vf_handle_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (zes_device_handle_t, uint32_t *, zes_diag_handle_t *)")
  def zesDeviceEnumDiagnosticTestSuites(hDevice: zes_device_handle_t, pCount: Pointer[uint32_t, ReadWrite, Opaque], phDiagnostics: Pointer[zes_diag_handle_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (zes_device_handle_t, uint32_t *, zes_vf_handle_t *)")
  def zesDeviceEnumEnabledVFExp(hDevice: zes_device_handle_t, pCount: Pointer[uint32_t, ReadWrite, Opaque], phVFhandle: Pointer[zes_vf_handle_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (zes_device_handle_t, uint32_t *, zes_engine_handle_t *)")
  def zesDeviceEnumEngineGroups(hDevice: zes_device_handle_t, pCount: Pointer[uint32_t, ReadWrite, Opaque], phEngine: Pointer[zes_engine_handle_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (zes_device_handle_t, uint32_t *, zes_fabric_port_handle_t *)")
  def zesDeviceEnumFabricPorts(hDevice: zes_device_handle_t, pCount: Pointer[uint32_t, ReadWrite, Opaque], phPort: Pointer[zes_fabric_port_handle_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (zes_device_handle_t, uint32_t *, zes_fan_handle_t *)")
  def zesDeviceEnumFans(hDevice: zes_device_handle_t, pCount: Pointer[uint32_t, ReadWrite, Opaque], phFan: Pointer[zes_fan_handle_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (zes_device_handle_t, uint32_t *, zes_firmware_handle_t *)")
  def zesDeviceEnumFirmwares(hDevice: zes_device_handle_t, pCount: Pointer[uint32_t, ReadWrite, Opaque], phFirmware: Pointer[zes_firmware_handle_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (zes_device_handle_t, uint32_t *, zes_freq_handle_t *)")
  def zesDeviceEnumFrequencyDomains(hDevice: zes_device_handle_t, pCount: Pointer[uint32_t, ReadWrite, Opaque], phFrequency: Pointer[zes_freq_handle_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (zes_device_handle_t, uint32_t *, zes_led_handle_t *)")
  def zesDeviceEnumLeds(hDevice: zes_device_handle_t, pCount: Pointer[uint32_t, ReadWrite, Opaque], phLed: Pointer[zes_led_handle_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (zes_device_handle_t, uint32_t *, zes_mem_handle_t *)")
  def zesDeviceEnumMemoryModules(hDevice: zes_device_handle_t, pCount: Pointer[uint32_t, ReadWrite, Opaque], phMemory: Pointer[zes_mem_handle_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (zes_device_handle_t, uint32_t *, zes_overclock_handle_t *)")
  def zesDeviceEnumOverclockDomains(hDevice: zes_device_handle_t, pCount: Pointer[uint32_t, ReadWrite, Opaque], phDomainHandle: Pointer[zes_overclock_handle_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (zes_device_handle_t, uint32_t *, zes_perf_handle_t *)")
  def zesDeviceEnumPerformanceFactorDomains(hDevice: zes_device_handle_t, pCount: Pointer[uint32_t, ReadWrite, Opaque], phPerf: Pointer[zes_perf_handle_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (zes_device_handle_t, uint32_t *, zes_pwr_handle_t *)")
  def zesDeviceEnumPowerDomains(hDevice: zes_device_handle_t, pCount: Pointer[uint32_t, ReadWrite, Opaque], phPower: Pointer[zes_pwr_handle_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (zes_device_handle_t, uint32_t *, zes_psu_handle_t *)")
  def zesDeviceEnumPsus(hDevice: zes_device_handle_t, pCount: Pointer[uint32_t, ReadWrite, Opaque], phPsu: Pointer[zes_psu_handle_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (zes_device_handle_t, uint32_t *, zes_ras_handle_t *)")
  def zesDeviceEnumRasErrorSets(hDevice: zes_device_handle_t, pCount: Pointer[uint32_t, ReadWrite, Opaque], phRas: Pointer[zes_ras_handle_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (zes_device_handle_t, uint32_t *, zes_sched_handle_t *)")
  def zesDeviceEnumSchedulers(hDevice: zes_device_handle_t, pCount: Pointer[uint32_t, ReadWrite, Opaque], phScheduler: Pointer[zes_sched_handle_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (zes_device_handle_t, uint32_t *, zes_standby_handle_t *)")
  def zesDeviceEnumStandbyDomains(hDevice: zes_device_handle_t, pCount: Pointer[uint32_t, ReadWrite, Opaque], phStandby: Pointer[zes_standby_handle_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (zes_device_handle_t, uint32_t *, zes_temp_handle_t *)")
  def zesDeviceEnumTemperatureSensors(hDevice: zes_device_handle_t, pCount: Pointer[uint32_t, ReadWrite, Opaque], phTemperature: Pointer[zes_temp_handle_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (zes_device_handle_t, zes_event_type_flags_t)")
  def zesDeviceEventRegister(hDevice: zes_device_handle_t, events: zes_event_type_flags_t): ze_result_t = unsupported
  @CSignature("ze_result_t (zes_driver_handle_t, uint32_t *, zes_device_handle_t *)")
  def zesDeviceGet(hDriver: zes_driver_handle_t, pCount: Pointer[uint32_t, ReadWrite, Opaque], phDevices: Pointer[zes_device_handle_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (zes_device_handle_t, zes_pwr_handle_t *)")
  def zesDeviceGetCardPowerDomain(hDevice: zes_device_handle_t, phPower: Pointer[zes_pwr_handle_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (zes_device_handle_t, zes_device_ecc_properties_t *)")
  def zesDeviceGetEccState(hDevice: zes_device_handle_t, pState: Pointer[zes_device_ecc_properties_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (zes_device_handle_t, zes_device_health_status_ext_t *)")
  def zesDeviceGetHealthStatusExt(hDevice: zes_device_handle_t, pHealth: Pointer[zes_device_health_status_ext_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (zes_device_handle_t, zes_overclock_domain_t, uint32_t *)")
  def zesDeviceGetOverclockControls(hDevice: zes_device_handle_t, domainType: zes_overclock_domain_t, pAvailableControls: Pointer[uint32_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (zes_device_handle_t, uint32_t *)")
  def zesDeviceGetOverclockDomains(hDevice: zes_device_handle_t, pOverclockDomains: Pointer[uint32_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (zes_device_handle_t, zes_device_properties_t *)")
  def zesDeviceGetProperties(hDevice: zes_device_handle_t, pProperties: Pointer[zes_device_properties_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (zes_device_handle_t, zes_device_state_t *)")
  def zesDeviceGetState(hDevice: zes_device_handle_t, pState: Pointer[zes_device_state_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (zes_device_handle_t, uint32_t *, zes_subdevice_exp_properties_t *)")
  def zesDeviceGetSubDevicePropertiesExp(hDevice: zes_device_handle_t, pCount: Pointer[uint32_t, ReadWrite, Opaque], pSubdeviceProps: Pointer[zes_subdevice_exp_properties_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (zes_device_handle_t, uint32_t *, zes_pci_bar_properties_t *)")
  def zesDevicePciGetBars(hDevice: zes_device_handle_t, pCount: Pointer[uint32_t, ReadWrite, Opaque], pProperties: Pointer[zes_pci_bar_properties_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (zes_device_handle_t, zes_pci_properties_t *)")
  def zesDevicePciGetProperties(hDevice: zes_device_handle_t, pProperties: Pointer[zes_pci_properties_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (zes_device_handle_t, zes_pci_state_t *)")
  def zesDevicePciGetState(hDevice: zes_device_handle_t, pState: Pointer[zes_pci_state_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (zes_device_handle_t, zes_pci_stats_t *)")
  def zesDevicePciGetStats(hDevice: zes_device_handle_t, pStats: Pointer[zes_pci_stats_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (zes_device_handle_t, ze_bool_t, zes_device_action_t *)")
  def zesDevicePciLinkSpeedUpdateExt(hDevice: zes_device_handle_t, shouldDowngrade: ze_bool_t, pendingAction: Pointer[zes_device_action_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (zes_device_handle_t, uint32_t *, zes_process_state_t *)")
  def zesDeviceProcessesGetState(hDevice: zes_device_handle_t, pCount: Pointer[uint32_t, ReadWrite, Opaque], pProcesses: Pointer[zes_process_state_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (zes_device_handle_t, zes_overclock_mode_t *, ze_bool_t *, ze_bool_t *, zes_pending_action_t *, ze_bool_t *)")
  def zesDeviceReadOverclockState(hDevice: zes_device_handle_t, pOverclockMode: Pointer[zes_overclock_mode_t, ReadWrite, Opaque], pWaiverSetting: Pointer[ze_bool_t, ReadWrite, Opaque], pOverclockState: Pointer[ze_bool_t, ReadWrite, Opaque], pPendingAction: Pointer[zes_pending_action_t, ReadWrite, Opaque], pPendingReset: Pointer[ze_bool_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (zes_device_handle_t, ze_bool_t)")
  def zesDeviceReset(hDevice: zes_device_handle_t, force: ze_bool_t): ze_result_t = unsupported
  @CSignature("ze_result_t (zes_device_handle_t, zes_reset_properties_t *)")
  def zesDeviceResetExt(hDevice: zes_device_handle_t, pProperties: Pointer[zes_reset_properties_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (zes_device_handle_t, ze_bool_t)")
  def zesDeviceResetOverclockSettings(hDevice: zes_device_handle_t, onShippedState: ze_bool_t): ze_result_t = unsupported
  @CSignature("ze_result_t (zes_device_handle_t, const zes_device_ecc_desc_t *, zes_device_ecc_properties_t *)")
  def zesDeviceSetEccState(hDevice: zes_device_handle_t, newState: Pointer[zes_device_ecc_desc_t, Read, Opaque], pState: Pointer[zes_device_ecc_properties_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (zes_device_handle_t, zes_device_health_status_ext_t)")
  def zesDeviceSetHealthStatusExt(hDevice: zes_device_handle_t, health: zes_device_health_status_ext_t): ze_result_t = unsupported
  @CSignature("ze_result_t (zes_device_handle_t)")
  def zesDeviceSetOverclockWaiver(hDevice: zes_device_handle_t): ze_result_t = unsupported
  @CSignature("ze_result_t (zes_diag_handle_t, zes_diag_properties_t *)")
  def zesDiagnosticsGetProperties(hDiagnostics: zes_diag_handle_t, pProperties: Pointer[zes_diag_properties_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (zes_diag_handle_t, uint32_t *, zes_diag_test_t *)")
  def zesDiagnosticsGetTests(hDiagnostics: zes_diag_handle_t, pCount: Pointer[uint32_t, ReadWrite, Opaque], pTests: Pointer[zes_diag_test_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (zes_diag_handle_t, uint32_t, uint32_t, zes_diag_result_t *)")
  def zesDiagnosticsRunTests(hDiagnostics: zes_diag_handle_t, startIndex: uint32_t, endIndex: uint32_t, pResult: Pointer[zes_diag_result_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_driver_handle_t, uint32_t, uint32_t, zes_device_handle_t *, uint32_t *, zes_event_type_flags_t *)")
  def zesDriverEventListen(hDriver: ze_driver_handle_t, timeout: uint32_t, count: uint32_t, phDevices: Pointer[zes_device_handle_t, ReadWrite, Opaque], pNumDeviceEvents: Pointer[uint32_t, ReadWrite, Opaque], pEvents: Pointer[zes_event_type_flags_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_driver_handle_t, uint64_t, uint32_t, zes_device_handle_t *, uint32_t *, zes_event_type_flags_t *)")
  def zesDriverEventListenEx(hDriver: ze_driver_handle_t, timeout: uint64_t, count: uint32_t, phDevices: Pointer[zes_device_handle_t, ReadWrite, Opaque], pNumDeviceEvents: Pointer[uint32_t, ReadWrite, Opaque], pEvents: Pointer[zes_event_type_flags_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (uint32_t *, zes_driver_handle_t *)")
  def zesDriverGet(pCount: Pointer[uint32_t, ReadWrite, Opaque], phDrivers: Pointer[zes_driver_handle_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (zes_driver_handle_t, zes_uuid_t, zes_device_handle_t *, ze_bool_t *, uint32_t *)")
  def zesDriverGetDeviceByUuidExp(hDriver: zes_driver_handle_t, uuid: zes_uuid_t, phDevice: Pointer[zes_device_handle_t, ReadWrite, Opaque], onSubdevice: Pointer[ze_bool_t, ReadWrite, Opaque], subdeviceId: Pointer[uint32_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (zes_driver_handle_t, const char *, void **)")
  def zesDriverGetExtensionFunctionAddress(hDriver: zes_driver_handle_t, name: CString, ppFunctionAddress: Pointer[Pointer[CVoid, ReadWrite, Opaque], ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (zes_driver_handle_t, uint32_t *, zes_driver_extension_properties_t *)")
  def zesDriverGetExtensionProperties(hDriver: zes_driver_handle_t, pCount: Pointer[uint32_t, ReadWrite, Opaque], pExtensionProperties: Pointer[zes_driver_extension_properties_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (zes_engine_handle_t, zes_engine_stats_t *)")
  def zesEngineGetActivity(hEngine: zes_engine_handle_t, pStats: Pointer[zes_engine_stats_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (zes_engine_handle_t, uint32_t *, zes_engine_stats_t *)")
  def zesEngineGetActivityExt(hEngine: zes_engine_handle_t, pCount: Pointer[uint32_t, ReadWrite, Opaque], pStats: Pointer[zes_engine_stats_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (zes_engine_handle_t, zes_engine_properties_t *)")
  def zesEngineGetProperties(hEngine: zes_engine_handle_t, pProperties: Pointer[zes_engine_properties_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (zes_fabric_port_handle_t, zes_fabric_port_config_t *)")
  def zesFabricPortGetConfig(hPort: zes_fabric_port_handle_t, pConfig: Pointer[zes_fabric_port_config_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (zes_fabric_port_handle_t, zes_fabric_port_error_counters_t *)")
  def zesFabricPortGetFabricErrorCounters(hPort: zes_fabric_port_handle_t, pErrors: Pointer[zes_fabric_port_error_counters_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (zes_fabric_port_handle_t, zes_fabric_link_type_t *)")
  def zesFabricPortGetLinkType(hPort: zes_fabric_port_handle_t, pLinkType: Pointer[zes_fabric_link_type_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (zes_device_handle_t, uint32_t, zes_fabric_port_handle_t *, zes_fabric_port_throughput_t **)")
  def zesFabricPortGetMultiPortThroughput(hDevice: zes_device_handle_t, numPorts: uint32_t, phPort: Pointer[zes_fabric_port_handle_t, ReadWrite, Opaque], pThroughput: Pointer[Pointer[zes_fabric_port_throughput_t, ReadWrite, Opaque], ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (zes_fabric_port_handle_t, zes_fabric_port_properties_t *)")
  def zesFabricPortGetProperties(hPort: zes_fabric_port_handle_t, pProperties: Pointer[zes_fabric_port_properties_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (zes_fabric_port_handle_t, zes_fabric_port_state_t *)")
  def zesFabricPortGetState(hPort: zes_fabric_port_handle_t, pState: Pointer[zes_fabric_port_state_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (zes_fabric_port_handle_t, zes_fabric_port_throughput_t *)")
  def zesFabricPortGetThroughput(hPort: zes_fabric_port_handle_t, pThroughput: Pointer[zes_fabric_port_throughput_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (zes_fabric_port_handle_t, const zes_fabric_port_config_t *)")
  def zesFabricPortSetConfig(hPort: zes_fabric_port_handle_t, pConfig: Pointer[zes_fabric_port_config_t, Read, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (zes_fan_handle_t, zes_fan_config_t *)")
  def zesFanGetConfig(hFan: zes_fan_handle_t, pConfig: Pointer[zes_fan_config_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (zes_fan_handle_t, zes_fan_properties_t *)")
  def zesFanGetProperties(hFan: zes_fan_handle_t, pProperties: Pointer[zes_fan_properties_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (zes_fan_handle_t, zes_fan_speed_units_t, int32_t *)")
  def zesFanGetState(hFan: zes_fan_handle_t, units: zes_fan_speed_units_t, pSpeed: Pointer[int32_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (zes_fan_handle_t)")
  def zesFanSetDefaultMode(hFan: zes_fan_handle_t): ze_result_t = unsupported
  @CSignature("ze_result_t (zes_fan_handle_t, const zes_fan_speed_t *)")
  def zesFanSetFixedSpeedMode(hFan: zes_fan_handle_t, speed: Pointer[zes_fan_speed_t, Read, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (zes_fan_handle_t, const zes_fan_speed_table_t *)")
  def zesFanSetSpeedTableMode(hFan: zes_fan_handle_t, speedTable: Pointer[zes_fan_speed_table_t, Read, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (zes_firmware_handle_t, void *, uint32_t)")
  def zesFirmwareFlash(hFirmware: zes_firmware_handle_t, pImage: Pointer[CVoid, ReadWrite, Opaque], size: uint32_t): ze_result_t = unsupported
  @CSignature("ze_result_t (zes_firmware_handle_t, size_t *, char *)")
  def zesFirmwareGetConsoleLogs(hFirmware: zes_firmware_handle_t, pSize: Pointer[size_t, ReadWrite, Opaque], pFirmwareLog: Pointer[Byte, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (zes_firmware_handle_t, uint32_t *)")
  def zesFirmwareGetFlashProgress(hFirmware: zes_firmware_handle_t, pCompletionPercent: Pointer[uint32_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (zes_firmware_handle_t, zes_firmware_properties_t *)")
  def zesFirmwareGetProperties(hFirmware: zes_firmware_handle_t, pProperties: Pointer[zes_firmware_properties_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (zes_firmware_handle_t, char *)")
  def zesFirmwareGetSecurityVersionExp(hFirmware: zes_firmware_handle_t, pVersion: Pointer[Byte, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (zes_firmware_handle_t)")
  def zesFirmwareSetSecurityVersionExp(hFirmware: zes_firmware_handle_t): ze_result_t = unsupported
  @CSignature("ze_result_t (zes_freq_handle_t, uint32_t *, double *)")
  def zesFrequencyGetAvailableClocks(hFrequency: zes_freq_handle_t, pCount: Pointer[uint32_t, ReadWrite, Opaque], phFrequency: Pointer[Double, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (zes_freq_handle_t, zes_freq_properties_t *)")
  def zesFrequencyGetProperties(hFrequency: zes_freq_handle_t, pProperties: Pointer[zes_freq_properties_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (zes_freq_handle_t, zes_freq_range_t *)")
  def zesFrequencyGetRange(hFrequency: zes_freq_handle_t, pLimits: Pointer[zes_freq_range_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (zes_freq_handle_t, zes_freq_state_t *)")
  def zesFrequencyGetState(hFrequency: zes_freq_handle_t, pState: Pointer[zes_freq_state_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (zes_freq_handle_t, zes_freq_throttle_time_t *)")
  def zesFrequencyGetThrottleTime(hFrequency: zes_freq_handle_t, pThrottleTime: Pointer[zes_freq_throttle_time_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (zes_freq_handle_t, zes_oc_capabilities_t *)")
  def zesFrequencyOcGetCapabilities(hFrequency: zes_freq_handle_t, pOcCapabilities: Pointer[zes_oc_capabilities_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (zes_freq_handle_t, double *)")
  def zesFrequencyOcGetFrequencyTarget(hFrequency: zes_freq_handle_t, pCurrentOcFrequency: Pointer[Double, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (zes_freq_handle_t, double *)")
  def zesFrequencyOcGetIccMax(hFrequency: zes_freq_handle_t, pOcIccMax: Pointer[Double, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (zes_freq_handle_t, zes_oc_mode_t *)")
  def zesFrequencyOcGetMode(hFrequency: zes_freq_handle_t, pCurrentOcMode: Pointer[zes_oc_mode_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (zes_freq_handle_t, double *)")
  def zesFrequencyOcGetTjMax(hFrequency: zes_freq_handle_t, pOcTjMax: Pointer[Double, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (zes_freq_handle_t, double *, double *)")
  def zesFrequencyOcGetVoltageTarget(hFrequency: zes_freq_handle_t, pCurrentVoltageTarget: Pointer[Double, ReadWrite, Opaque], pCurrentVoltageOffset: Pointer[Double, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (zes_freq_handle_t, double)")
  def zesFrequencyOcSetFrequencyTarget(hFrequency: zes_freq_handle_t, CurrentOcFrequency: Double): ze_result_t = unsupported
  @CSignature("ze_result_t (zes_freq_handle_t, double)")
  def zesFrequencyOcSetIccMax(hFrequency: zes_freq_handle_t, ocIccMax: Double): ze_result_t = unsupported
  @CSignature("ze_result_t (zes_freq_handle_t, zes_oc_mode_t)")
  def zesFrequencyOcSetMode(hFrequency: zes_freq_handle_t, CurrentOcMode: zes_oc_mode_t): ze_result_t = unsupported
  @CSignature("ze_result_t (zes_freq_handle_t, double)")
  def zesFrequencyOcSetTjMax(hFrequency: zes_freq_handle_t, ocTjMax: Double): ze_result_t = unsupported
  @CSignature("ze_result_t (zes_freq_handle_t, double, double)")
  def zesFrequencyOcSetVoltageTarget(hFrequency: zes_freq_handle_t, CurrentVoltageTarget: Double, CurrentVoltageOffset: Double): ze_result_t = unsupported
  @CSignature("ze_result_t (zes_freq_handle_t, const zes_freq_range_t *)")
  def zesFrequencySetRange(hFrequency: zes_freq_handle_t, pLimits: Pointer[zes_freq_range_t, Read, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (zes_init_flags_t)")
  def zesInit(flags: zes_init_flags_t): ze_result_t = unsupported
  @CSignature("ze_result_t (zes_led_handle_t, zes_led_properties_t *)")
  def zesLedGetProperties(hLed: zes_led_handle_t, pProperties: Pointer[zes_led_properties_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (zes_led_handle_t, zes_led_state_t *)")
  def zesLedGetState(hLed: zes_led_handle_t, pState: Pointer[zes_led_state_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (zes_led_handle_t, const zes_led_color_t *)")
  def zesLedSetColor(hLed: zes_led_handle_t, pColor: Pointer[zes_led_color_t, Read, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (zes_led_handle_t, ze_bool_t)")
  def zesLedSetState(hLed: zes_led_handle_t, enable: ze_bool_t): ze_result_t = unsupported
  @CSignature("ze_result_t (zes_mem_handle_t, zes_mem_bandwidth_t *)")
  def zesMemoryGetBandwidth(hMemory: zes_mem_handle_t, pBandwidth: Pointer[zes_mem_bandwidth_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (zes_mem_handle_t, zes_mem_properties_t *)")
  def zesMemoryGetProperties(hMemory: zes_mem_handle_t, pProperties: Pointer[zes_mem_properties_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (zes_mem_handle_t, zes_mem_state_t *)")
  def zesMemoryGetState(hMemory: zes_mem_handle_t, pState: Pointer[zes_mem_state_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (zes_overclock_handle_t, zes_overclock_control_t, double *)")
  def zesOverclockGetControlCurrentValue(hDomainHandle: zes_overclock_handle_t, DomainControl: zes_overclock_control_t, pValue: Pointer[Double, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (zes_overclock_handle_t, zes_overclock_control_t, double *)")
  def zesOverclockGetControlPendingValue(hDomainHandle: zes_overclock_handle_t, DomainControl: zes_overclock_control_t, pValue: Pointer[Double, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (zes_overclock_handle_t, zes_overclock_control_t, zes_control_state_t *, zes_pending_action_t *)")
  def zesOverclockGetControlState(hDomainHandle: zes_overclock_handle_t, DomainControl: zes_overclock_control_t, pControlState: Pointer[zes_control_state_t, ReadWrite, Opaque], pPendingAction: Pointer[zes_pending_action_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (zes_overclock_handle_t, zes_overclock_control_t, zes_control_property_t *)")
  def zesOverclockGetDomainControlProperties(hDomainHandle: zes_overclock_handle_t, DomainControl: zes_overclock_control_t, pControlProperties: Pointer[zes_control_property_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (zes_overclock_handle_t, zes_overclock_properties_t *)")
  def zesOverclockGetDomainProperties(hDomainHandle: zes_overclock_handle_t, pDomainProperties: Pointer[zes_overclock_properties_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (zes_overclock_handle_t, zes_vf_property_t *)")
  def zesOverclockGetDomainVFProperties(hDomainHandle: zes_overclock_handle_t, pVFProperties: Pointer[zes_vf_property_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (zes_overclock_handle_t, zes_vf_type_t, zes_vf_array_type_t, uint32_t, uint32_t *)")
  def zesOverclockGetVFPointValues(hDomainHandle: zes_overclock_handle_t, VFType: zes_vf_type_t, VFArrayType: zes_vf_array_type_t, PointIndex: uint32_t, PointValue: Pointer[uint32_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (zes_overclock_handle_t, zes_overclock_control_t, double, zes_pending_action_t *)")
  def zesOverclockSetControlUserValue(hDomainHandle: zes_overclock_handle_t, DomainControl: zes_overclock_control_t, pValue: Double, pPendingAction: Pointer[zes_pending_action_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (zes_overclock_handle_t, zes_vf_type_t, uint32_t, uint32_t)")
  def zesOverclockSetVFPointValues(hDomainHandle: zes_overclock_handle_t, VFType: zes_vf_type_t, PointIndex: uint32_t, PointValue: uint32_t): ze_result_t = unsupported
  @CSignature("ze_result_t (zes_perf_handle_t, double *)")
  def zesPerformanceFactorGetConfig(hPerf: zes_perf_handle_t, pFactor: Pointer[Double, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (zes_perf_handle_t, zes_perf_properties_t *)")
  def zesPerformanceFactorGetProperties(hPerf: zes_perf_handle_t, pProperties: Pointer[zes_perf_properties_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (zes_perf_handle_t, double)")
  def zesPerformanceFactorSetConfig(hPerf: zes_perf_handle_t, factor: Double): ze_result_t = unsupported
  @CSignature("ze_result_t (zes_pwr_handle_t, zes_power_energy_counter_t *)")
  def zesPowerGetEnergyCounter(hPower: zes_pwr_handle_t, pEnergy: Pointer[zes_power_energy_counter_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (zes_pwr_handle_t, zes_energy_threshold_t *)")
  def zesPowerGetEnergyThreshold(hPower: zes_pwr_handle_t, pThreshold: Pointer[zes_energy_threshold_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (zes_pwr_handle_t, zes_power_sustained_limit_t *, zes_power_burst_limit_t *, zes_power_peak_limit_t *)")
  def zesPowerGetLimits(hPower: zes_pwr_handle_t, pSustained: Pointer[zes_power_sustained_limit_t, ReadWrite, Opaque], pBurst: Pointer[zes_power_burst_limit_t, ReadWrite, Opaque], pPeak: Pointer[zes_power_peak_limit_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (zes_pwr_handle_t, uint32_t *, zes_power_limit_ext_desc_t *)")
  def zesPowerGetLimitsExt(hPower: zes_pwr_handle_t, pCount: Pointer[uint32_t, ReadWrite, Opaque], pSustained: Pointer[zes_power_limit_ext_desc_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (zes_pwr_handle_t, uint32_t *)")
  def zesPowerGetLimitsExt2(hPower: zes_pwr_handle_t, pLimit: Pointer[uint32_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (zes_pwr_handle_t, zes_power_properties_t *)")
  def zesPowerGetProperties(hPower: zes_pwr_handle_t, pProperties: Pointer[zes_power_properties_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (zes_pwr_handle_t, uint32_t *, uint32_t *)")
  def zesPowerGetUsage(hPower: zes_pwr_handle_t, pInstantPower: Pointer[uint32_t, ReadWrite, Opaque], pAveragePower: Pointer[uint32_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (zes_pwr_handle_t, double)")
  def zesPowerSetEnergyThreshold(hPower: zes_pwr_handle_t, threshold: Double): ze_result_t = unsupported
  @CSignature("ze_result_t (zes_pwr_handle_t, const zes_power_sustained_limit_t *, const zes_power_burst_limit_t *, const zes_power_peak_limit_t *)")
  def zesPowerSetLimits(hPower: zes_pwr_handle_t, pSustained: Pointer[zes_power_sustained_limit_t, Read, Opaque], pBurst: Pointer[zes_power_burst_limit_t, Read, Opaque], pPeak: Pointer[zes_power_peak_limit_t, Read, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (zes_pwr_handle_t, uint32_t *, zes_power_limit_ext_desc_t *)")
  def zesPowerSetLimitsExt(hPower: zes_pwr_handle_t, pCount: Pointer[uint32_t, ReadWrite, Opaque], pSustained: Pointer[zes_power_limit_ext_desc_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (zes_pwr_handle_t, const uint32_t)")
  def zesPowerSetLimitsExt2(hPower: zes_pwr_handle_t, limit: const_uint32_t): ze_result_t = unsupported
  @CSignature("ze_result_t (zes_psu_handle_t, zes_psu_properties_t *)")
  def zesPsuGetProperties(hPsu: zes_psu_handle_t, pProperties: Pointer[zes_psu_properties_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (zes_psu_handle_t, zes_psu_state_t *)")
  def zesPsuGetState(hPsu: zes_psu_handle_t, pState: Pointer[zes_psu_state_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (zes_ras_handle_t, zes_ras_error_category_exp_t)")
  def zesRasClearStateExp(hRas: zes_ras_handle_t, category: zes_ras_error_category_exp_t): ze_result_t = unsupported
  @CSignature("ze_result_t (zes_ras_handle_t, zes_ras_config_t *)")
  def zesRasGetConfig(hRas: zes_ras_handle_t, pConfig: Pointer[zes_ras_config_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (zes_ras_handle_t, const uint32_t, zes_ras_config_exp_t *)")
  def zesRasGetConfigExp(hRas: zes_ras_handle_t, count: const_uint32_t, pConfig: Pointer[zes_ras_config_exp_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (zes_ras_handle_t, zes_ras_properties_t *)")
  def zesRasGetProperties(hRas: zes_ras_handle_t, pProperties: Pointer[zes_ras_properties_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (zes_ras_handle_t, ze_bool_t, zes_ras_state_t *)")
  def zesRasGetState(hRas: zes_ras_handle_t, clear: ze_bool_t, pState: Pointer[zes_ras_state_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (zes_ras_handle_t, uint32_t *, zes_ras_state_exp_t *)")
  def zesRasGetStateExp(hRas: zes_ras_handle_t, pCount: Pointer[uint32_t, ReadWrite, Opaque], pState: Pointer[zes_ras_state_exp_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (zes_ras_handle_t, const uint32_t, const zes_ras_error_category_exp_t *, zes_ras_state_exp2_t *)")
  def zesRasGetStateExp2(hRas: zes_ras_handle_t, count: const_uint32_t, pCategories: Pointer[zes_ras_error_category_exp_t, Read, Opaque], pState: Pointer[zes_ras_state_exp2_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (zes_ras_handle_t, uint32_t *, zes_ras_error_category_exp_t *)")
  def zesRasGetSupportedCategoriesExp(hRas: zes_ras_handle_t, pCount: Pointer[uint32_t, ReadWrite, Opaque], pCategories: Pointer[zes_ras_error_category_exp_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (zes_ras_handle_t, const zes_ras_config_t *)")
  def zesRasSetConfig(hRas: zes_ras_handle_t, pConfig: Pointer[zes_ras_config_t, Read, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (zes_ras_handle_t, const uint32_t, const zes_ras_config_exp_t *)")
  def zesRasSetConfigExp(hRas: zes_ras_handle_t, count: const_uint32_t, pConfig: Pointer[zes_ras_config_exp_t, Read, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (zes_sched_handle_t, zes_sched_mode_t *)")
  def zesSchedulerGetCurrentMode(hScheduler: zes_sched_handle_t, pMode: Pointer[zes_sched_mode_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (zes_sched_handle_t, zes_sched_properties_t *)")
  def zesSchedulerGetProperties(hScheduler: zes_sched_handle_t, pProperties: Pointer[zes_sched_properties_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (zes_sched_handle_t, ze_bool_t, zes_sched_timeout_properties_t *)")
  def zesSchedulerGetTimeoutModeProperties(hScheduler: zes_sched_handle_t, getDefaults: ze_bool_t, pConfig: Pointer[zes_sched_timeout_properties_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (zes_sched_handle_t, ze_bool_t, zes_sched_timeslice_properties_t *)")
  def zesSchedulerGetTimesliceModeProperties(hScheduler: zes_sched_handle_t, getDefaults: ze_bool_t, pConfig: Pointer[zes_sched_timeslice_properties_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (zes_sched_handle_t, ze_bool_t *)")
  def zesSchedulerSetComputeUnitDebugMode(hScheduler: zes_sched_handle_t, pNeedReload: Pointer[ze_bool_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (zes_sched_handle_t, ze_bool_t *)")
  def zesSchedulerSetExclusiveMode(hScheduler: zes_sched_handle_t, pNeedReload: Pointer[ze_bool_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (zes_sched_handle_t, zes_sched_timeout_properties_t *, ze_bool_t *)")
  def zesSchedulerSetTimeoutMode(hScheduler: zes_sched_handle_t, pProperties: Pointer[zes_sched_timeout_properties_t, ReadWrite, Opaque], pNeedReload: Pointer[ze_bool_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (zes_sched_handle_t, zes_sched_timeslice_properties_t *, ze_bool_t *)")
  def zesSchedulerSetTimesliceMode(hScheduler: zes_sched_handle_t, pProperties: Pointer[zes_sched_timeslice_properties_t, ReadWrite, Opaque], pNeedReload: Pointer[ze_bool_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (zes_standby_handle_t, zes_standby_promo_mode_t *)")
  def zesStandbyGetMode(hStandby: zes_standby_handle_t, pMode: Pointer[zes_standby_promo_mode_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (zes_standby_handle_t, zes_standby_properties_t *)")
  def zesStandbyGetProperties(hStandby: zes_standby_handle_t, pProperties: Pointer[zes_standby_properties_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (zes_standby_handle_t, zes_standby_promo_mode_t)")
  def zesStandbySetMode(hStandby: zes_standby_handle_t, mode: zes_standby_promo_mode_t): ze_result_t = unsupported
  @CSignature("ze_result_t (zes_temp_handle_t, zes_temp_config_t *)")
  def zesTemperatureGetConfig(hTemperature: zes_temp_handle_t, pConfig: Pointer[zes_temp_config_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (zes_temp_handle_t, zes_temp_properties_t *)")
  def zesTemperatureGetProperties(hTemperature: zes_temp_handle_t, pProperties: Pointer[zes_temp_properties_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (zes_temp_handle_t, double *)")
  def zesTemperatureGetState(hTemperature: zes_temp_handle_t, pTemperature: Pointer[Double, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (zes_temp_handle_t, const zes_temp_config_t *)")
  def zesTemperatureSetConfig(hTemperature: zes_temp_handle_t, pConfig: Pointer[zes_temp_config_t, Read, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (zes_vf_handle_t, zes_vf_exp_capabilities_t *)")
  def zesVFManagementGetVFCapabilitiesExp(hVFhandle: zes_vf_handle_t, pCapability: Pointer[zes_vf_exp_capabilities_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (zes_vf_handle_t, zes_vf_exp2_capabilities_t *)")
  def zesVFManagementGetVFCapabilitiesExp2(hVFhandle: zes_vf_handle_t, pCapability: Pointer[zes_vf_exp2_capabilities_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (zes_vf_handle_t, uint32_t *, zes_vf_util_engine_exp_t *)")
  def zesVFManagementGetVFEngineUtilizationExp(hVFhandle: zes_vf_handle_t, pCount: Pointer[uint32_t, ReadWrite, Opaque], pEngineUtil: Pointer[zes_vf_util_engine_exp_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (zes_vf_handle_t, uint32_t *, zes_vf_util_engine_exp2_t *)")
  def zesVFManagementGetVFEngineUtilizationExp2(hVFhandle: zes_vf_handle_t, pCount: Pointer[uint32_t, ReadWrite, Opaque], pEngineUtil: Pointer[zes_vf_util_engine_exp2_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (zes_vf_handle_t, uint32_t *, zes_vf_util_mem_exp_t *)")
  def zesVFManagementGetVFMemoryUtilizationExp(hVFhandle: zes_vf_handle_t, pCount: Pointer[uint32_t, ReadWrite, Opaque], pMemUtil: Pointer[zes_vf_util_mem_exp_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (zes_vf_handle_t, uint32_t *, zes_vf_util_mem_exp2_t *)")
  def zesVFManagementGetVFMemoryUtilizationExp2(hVFhandle: zes_vf_handle_t, pCount: Pointer[uint32_t, ReadWrite, Opaque], pMemUtil: Pointer[zes_vf_util_mem_exp2_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (zes_vf_handle_t, zes_vf_exp_properties_t *)")
  def zesVFManagementGetVFPropertiesExp(hVFhandle: zes_vf_handle_t, pProperties: Pointer[zes_vf_exp_properties_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (zes_vf_handle_t, zes_vf_info_util_exp_flags_t, ze_bool_t)")
  def zesVFManagementSetVFTelemetryModeExp(hVFhandle: zes_vf_handle_t, flags: zes_vf_info_util_exp_flags_t, enable: ze_bool_t): ze_result_t = unsupported
  @CSignature("ze_result_t (zes_vf_handle_t, zes_vf_info_util_exp_flags_t, uint64_t)")
  def zesVFManagementSetVFTelemetrySamplingIntervalExp(hVFhandle: zes_vf_handle_t, flag: zes_vf_info_util_exp_flags_t, samplingInterval: uint64_t): ze_result_t = unsupported
  @CSignature("ze_result_t (zet_command_list_handle_t, zet_metric_group_handle_t, uint32_t)")
  def zetCommandListAppendMarkerExp(hCommandList: zet_command_list_handle_t, hMetricGroup: zet_metric_group_handle_t, value: uint32_t): ze_result_t = unsupported
  @CSignature("ze_result_t (zet_command_list_handle_t)")
  def zetCommandListAppendMetricMemoryBarrier(hCommandList: zet_command_list_handle_t): ze_result_t = unsupported
  @CSignature("ze_result_t (zet_command_list_handle_t, zet_metric_query_handle_t)")
  def zetCommandListAppendMetricQueryBegin(hCommandList: zet_command_list_handle_t, hMetricQuery: zet_metric_query_handle_t): ze_result_t = unsupported
  @CSignature("ze_result_t (zet_command_list_handle_t, zet_metric_query_handle_t, ze_event_handle_t, uint32_t, ze_event_handle_t *)")
  def zetCommandListAppendMetricQueryEnd(hCommandList: zet_command_list_handle_t, hMetricQuery: zet_metric_query_handle_t, hSignalEvent: ze_event_handle_t, numWaitEvents: uint32_t, phWaitEvents: Pointer[ze_event_handle_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (zet_command_list_handle_t, zet_metric_streamer_handle_t, uint32_t)")
  def zetCommandListAppendMetricStreamerMarker(hCommandList: zet_command_list_handle_t, hMetricStreamer: zet_metric_streamer_handle_t, value: uint32_t): ze_result_t = unsupported
  @CSignature("ze_result_t (zet_context_handle_t, zet_device_handle_t, uint32_t, zet_metric_group_handle_t *)")
  def zetContextActivateMetricGroups(hContext: zet_context_handle_t, hDevice: zet_device_handle_t, count: uint32_t, phMetricGroups: Pointer[zet_metric_group_handle_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (zet_debug_session_handle_t, const zet_debug_event_t *)")
  def zetDebugAcknowledgeEvent(hDebug: zet_debug_session_handle_t, event: Pointer[zet_debug_event_t, Read, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (zet_device_handle_t, const zet_debug_config_t *, zet_debug_session_handle_t *)")
  def zetDebugAttach(hDevice: zet_device_handle_t, config: Pointer[zet_debug_config_t, Read, Opaque], phDebug: Pointer[zet_debug_session_handle_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (zet_debug_session_handle_t)")
  def zetDebugDetach(hDebug: zet_debug_session_handle_t): ze_result_t = unsupported
  @CSignature("ze_result_t (zet_device_handle_t, uint32_t *, zet_debug_regset_properties_t *)")
  def zetDebugGetRegisterSetProperties(hDevice: zet_device_handle_t, pCount: Pointer[uint32_t, ReadWrite, Opaque], pRegisterSetProperties: Pointer[zet_debug_regset_properties_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (zet_debug_session_handle_t, ze_device_thread_t, uint32_t *, zet_debug_regset_properties_t *)")
  def zetDebugGetThreadRegisterSetProperties(hDebug: zet_debug_session_handle_t, thread: ze_device_thread_t, pCount: Pointer[uint32_t, ReadWrite, Opaque], pRegisterSetProperties: Pointer[zet_debug_regset_properties_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (zet_debug_session_handle_t, ze_device_thread_t)")
  def zetDebugInterrupt(hDebug: zet_debug_session_handle_t, thread: ze_device_thread_t): ze_result_t = unsupported
  @CSignature("ze_result_t (zet_debug_session_handle_t, uint64_t, zet_debug_event_t *)")
  def zetDebugReadEvent(hDebug: zet_debug_session_handle_t, timeout: uint64_t, event: Pointer[zet_debug_event_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (zet_debug_session_handle_t, ze_device_thread_t, const zet_debug_memory_space_desc_t *, size_t, void *)")
  def zetDebugReadMemory(hDebug: zet_debug_session_handle_t, thread: ze_device_thread_t, desc: Pointer[zet_debug_memory_space_desc_t, Read, Opaque], size: size_t, buffer: Pointer[CVoid, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (zet_debug_session_handle_t, ze_device_thread_t, uint32_t, uint32_t, uint32_t, void *)")
  def zetDebugReadRegisters(hDebug: zet_debug_session_handle_t, thread: ze_device_thread_t, `type`: uint32_t, start: uint32_t, count: uint32_t, pRegisterValues: Pointer[CVoid, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (zet_debug_session_handle_t, ze_device_thread_t)")
  def zetDebugResume(hDebug: zet_debug_session_handle_t, thread: ze_device_thread_t): ze_result_t = unsupported
  @CSignature("ze_result_t (zet_debug_session_handle_t, ze_device_thread_t, const zet_debug_memory_space_desc_t *, size_t, const void *)")
  def zetDebugWriteMemory(hDebug: zet_debug_session_handle_t, thread: ze_device_thread_t, desc: Pointer[zet_debug_memory_space_desc_t, Read, Opaque], size: size_t, buffer: Pointer[CVoid, Read, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (zet_debug_session_handle_t, ze_device_thread_t, uint32_t, uint32_t, uint32_t, void *)")
  def zetDebugWriteRegisters(hDebug: zet_debug_session_handle_t, thread: ze_device_thread_t, `type`: uint32_t, start: uint32_t, count: uint32_t, pRegisterValues: Pointer[CVoid, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (zet_device_handle_t, uint32_t, zet_metric_handle_t *, const char *, const char *, uint32_t *, zet_metric_group_handle_t *)")
  def zetDeviceCreateMetricGroupsFromMetricsExp(hDevice: zet_device_handle_t, metricCount: uint32_t, phMetrics: Pointer[zet_metric_handle_t, ReadWrite, Opaque], pMetricGroupNamePrefix: CString, pDescription: CString, pMetricGroupCount: Pointer[uint32_t, ReadWrite, Opaque], phMetricGroup: Pointer[zet_metric_group_handle_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (zet_device_handle_t)")
  def zetDeviceDisableMetricsExp(hDevice: zet_device_handle_t): ze_result_t = unsupported
  @CSignature("ze_result_t (zet_device_handle_t)")
  def zetDeviceEnableMetricsExp(hDevice: zet_device_handle_t): ze_result_t = unsupported
  @CSignature("ze_result_t (zet_device_handle_t, uint32_t, zet_metric_group_handle_t *, uint32_t *, uint32_t *)")
  def zetDeviceGetConcurrentMetricGroupsExp(hDevice: zet_device_handle_t, metricGroupCount: uint32_t, phMetricGroups: Pointer[zet_metric_group_handle_t, ReadWrite, Opaque], pMetricGroupsCountPerConcurrentGroup: Pointer[uint32_t, ReadWrite, Opaque], pConcurrentGroupCount: Pointer[uint32_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (zet_device_handle_t, zet_device_debug_properties_t *)")
  def zetDeviceGetDebugProperties(hDevice: zet_device_handle_t, pDebugProperties: Pointer[zet_device_debug_properties_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (zet_kernel_handle_t, zet_profile_properties_t *)")
  def zetKernelGetProfileInfo(hKernel: zet_kernel_handle_t, pProfileProperties: Pointer[zet_profile_properties_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (zet_metric_programmable_exp_handle_t, zet_metric_programmable_param_value_exp_t *, uint32_t, const char *, const char *, uint32_t *, zet_metric_handle_t *)")
  def zetMetricCreateFromProgrammableExp(hMetricProgrammable: zet_metric_programmable_exp_handle_t, pParameterValues: Pointer[zet_metric_programmable_param_value_exp_t, ReadWrite, Opaque], parameterCount: uint32_t, pName: CString, pDescription: CString, pMetricHandleCount: Pointer[uint32_t, ReadWrite, Opaque], phMetricHandles: Pointer[zet_metric_handle_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (zet_metric_programmable_exp_handle_t, uint32_t, zet_metric_programmable_param_value_exp_t *, const char *, const char *, uint32_t *, zet_metric_handle_t *)")
  def zetMetricCreateFromProgrammableExp2(hMetricProgrammable: zet_metric_programmable_exp_handle_t, parameterCount: uint32_t, pParameterValues: Pointer[zet_metric_programmable_param_value_exp_t, ReadWrite, Opaque], pName: CString, pDescription: CString, pMetricHandleCount: Pointer[uint32_t, ReadWrite, Opaque], phMetricHandles: Pointer[zet_metric_handle_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (zet_metric_tracer_exp_handle_t, zet_metric_decoder_exp_handle_t *)")
  def zetMetricDecoderCreateExp(hMetricTracer: zet_metric_tracer_exp_handle_t, phMetricDecoder: Pointer[zet_metric_decoder_exp_handle_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (zet_metric_decoder_exp_handle_t)")
  def zetMetricDecoderDestroyExp(phMetricDecoder: zet_metric_decoder_exp_handle_t): ze_result_t = unsupported
  @CSignature("ze_result_t (zet_metric_decoder_exp_handle_t, uint32_t *, zet_metric_handle_t *)")
  def zetMetricDecoderGetDecodableMetricsExp(hMetricDecoder: zet_metric_decoder_exp_handle_t, pCount: Pointer[uint32_t, ReadWrite, Opaque], phMetrics: Pointer[zet_metric_handle_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (zet_metric_handle_t)")
  def zetMetricDestroyExp(hMetric: zet_metric_handle_t): ze_result_t = unsupported
  @CSignature("ze_result_t (zet_metric_group_handle_t, uint32_t *, zet_metric_handle_t *)")
  def zetMetricGet(hMetricGroup: zet_metric_group_handle_t, pCount: Pointer[uint32_t, ReadWrite, Opaque], phMetrics: Pointer[zet_metric_handle_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (zet_metric_handle_t, zet_metric_properties_t *)")
  def zetMetricGetProperties(hMetric: zet_metric_handle_t, pProperties: Pointer[zet_metric_properties_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (zet_metric_group_handle_t, zet_metric_handle_t, size_t *, char *)")
  def zetMetricGroupAddMetricExp(hMetricGroup: zet_metric_group_handle_t, hMetric: zet_metric_handle_t, pErrorStringSize: Pointer[size_t, ReadWrite, Opaque], pErrorString: Pointer[Byte, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (ze_driver_handle_t, zet_metric_group_calculation_type_t, size_t, const uint8_t *, zet_metric_calculate_exp_desc_t *, uint32_t *, uint32_t *, uint32_t *, zet_typed_value_t *)")
  def zetMetricGroupCalculateMetricExportDataExp(hDriver: ze_driver_handle_t, `type`: zet_metric_group_calculation_type_t, exportDataSize: size_t, pExportData: Pointer[uint8_t, Read, Opaque], pCalculateDescriptor: Pointer[zet_metric_calculate_exp_desc_t, ReadWrite, Opaque], pSetCount: Pointer[uint32_t, ReadWrite, Opaque], pTotalMetricValueCount: Pointer[uint32_t, ReadWrite, Opaque], pMetricCounts: Pointer[uint32_t, ReadWrite, Opaque], pMetricValues: Pointer[zet_typed_value_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (zet_metric_group_handle_t, zet_metric_group_calculation_type_t, size_t, const uint8_t *, uint32_t *, zet_typed_value_t *)")
  def zetMetricGroupCalculateMetricValues(hMetricGroup: zet_metric_group_handle_t, `type`: zet_metric_group_calculation_type_t, rawDataSize: size_t, pRawData: Pointer[uint8_t, Read, Opaque], pMetricValueCount: Pointer[uint32_t, ReadWrite, Opaque], pMetricValues: Pointer[zet_typed_value_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (zet_metric_group_handle_t, zet_metric_group_calculation_type_t, size_t, const uint8_t *, uint32_t *, uint32_t *, uint32_t *, zet_typed_value_t *)")
  def zetMetricGroupCalculateMultipleMetricValuesExp(hMetricGroup: zet_metric_group_handle_t, `type`: zet_metric_group_calculation_type_t, rawDataSize: size_t, pRawData: Pointer[uint8_t, Read, Opaque], pSetCount: Pointer[uint32_t, ReadWrite, Opaque], pTotalMetricValueCount: Pointer[uint32_t, ReadWrite, Opaque], pMetricCounts: Pointer[uint32_t, ReadWrite, Opaque], pMetricValues: Pointer[zet_typed_value_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (zet_metric_group_handle_t)")
  def zetMetricGroupCloseExp(hMetricGroup: zet_metric_group_handle_t): ze_result_t = unsupported
  @CSignature("ze_result_t (zet_device_handle_t, const char *, const char *, zet_metric_group_sampling_type_flags_t, zet_metric_group_handle_t *)")
  def zetMetricGroupCreateExp(hDevice: zet_device_handle_t, pName: CString, pDescription: CString, samplingType: zet_metric_group_sampling_type_flags_t, phMetricGroup: Pointer[zet_metric_group_handle_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (zet_metric_group_handle_t)")
  def zetMetricGroupDestroyExp(hMetricGroup: zet_metric_group_handle_t): ze_result_t = unsupported
  @CSignature("ze_result_t (zet_device_handle_t, uint32_t *, zet_metric_group_handle_t *)")
  def zetMetricGroupGet(hDevice: zet_device_handle_t, pCount: Pointer[uint32_t, ReadWrite, Opaque], phMetricGroups: Pointer[zet_metric_group_handle_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (zet_metric_group_handle_t, const uint8_t *, size_t, size_t *, uint8_t *)")
  def zetMetricGroupGetExportDataExp(hMetricGroup: zet_metric_group_handle_t, pRawData: Pointer[uint8_t, Read, Opaque], rawDataSize: size_t, pExportDataSize: Pointer[size_t, ReadWrite, Opaque], pExportData: Pointer[uint8_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (zet_metric_group_handle_t, ze_bool_t, uint64_t *, uint64_t *)")
  def zetMetricGroupGetGlobalTimestampsExp(hMetricGroup: zet_metric_group_handle_t, synchronizedWithHost: ze_bool_t, globalTimestamp: Pointer[uint64_t, ReadWrite, Opaque], metricTimestamp: Pointer[uint64_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (zet_metric_group_handle_t, zet_metric_group_properties_t *)")
  def zetMetricGroupGetProperties(hMetricGroup: zet_metric_group_handle_t, pProperties: Pointer[zet_metric_group_properties_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (zet_metric_group_handle_t, zet_metric_handle_t)")
  def zetMetricGroupRemoveMetricExp(hMetricGroup: zet_metric_group_handle_t, hMetric: zet_metric_handle_t): ze_result_t = unsupported
  @CSignature("ze_result_t (zet_device_handle_t, uint32_t *, zet_metric_programmable_exp_handle_t *)")
  def zetMetricProgrammableGetExp(hDevice: zet_device_handle_t, pCount: Pointer[uint32_t, ReadWrite, Opaque], phMetricProgrammables: Pointer[zet_metric_programmable_exp_handle_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (zet_metric_programmable_exp_handle_t, uint32_t *, zet_metric_programmable_param_info_exp_t *)")
  def zetMetricProgrammableGetParamInfoExp(hMetricProgrammable: zet_metric_programmable_exp_handle_t, pParameterCount: Pointer[uint32_t, ReadWrite, Opaque], pParameterInfo: Pointer[zet_metric_programmable_param_info_exp_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (zet_metric_programmable_exp_handle_t, uint32_t, uint32_t *, zet_metric_programmable_param_value_info_exp_t *)")
  def zetMetricProgrammableGetParamValueInfoExp(hMetricProgrammable: zet_metric_programmable_exp_handle_t, parameterOrdinal: uint32_t, pValueInfoCount: Pointer[uint32_t, ReadWrite, Opaque], pValueInfo: Pointer[zet_metric_programmable_param_value_info_exp_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (zet_metric_programmable_exp_handle_t, zet_metric_programmable_exp_properties_t *)")
  def zetMetricProgrammableGetPropertiesExp(hMetricProgrammable: zet_metric_programmable_exp_handle_t, pProperties: Pointer[zet_metric_programmable_exp_properties_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (zet_metric_query_pool_handle_t, uint32_t, zet_metric_query_handle_t *)")
  def zetMetricQueryCreate(hMetricQueryPool: zet_metric_query_pool_handle_t, index: uint32_t, phMetricQuery: Pointer[zet_metric_query_handle_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (zet_metric_query_handle_t)")
  def zetMetricQueryDestroy(hMetricQuery: zet_metric_query_handle_t): ze_result_t = unsupported
  @CSignature("ze_result_t (zet_metric_query_handle_t, size_t *, uint8_t *)")
  def zetMetricQueryGetData(hMetricQuery: zet_metric_query_handle_t, pRawDataSize: Pointer[size_t, ReadWrite, Opaque], pRawData: Pointer[uint8_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (zet_context_handle_t, zet_device_handle_t, zet_metric_group_handle_t, const zet_metric_query_pool_desc_t *, zet_metric_query_pool_handle_t *)")
  def zetMetricQueryPoolCreate(hContext: zet_context_handle_t, hDevice: zet_device_handle_t, hMetricGroup: zet_metric_group_handle_t, desc: Pointer[zet_metric_query_pool_desc_t, Read, Opaque], phMetricQueryPool: Pointer[zet_metric_query_pool_handle_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (zet_metric_query_pool_handle_t)")
  def zetMetricQueryPoolDestroy(hMetricQueryPool: zet_metric_query_pool_handle_t): ze_result_t = unsupported
  @CSignature("ze_result_t (zet_metric_query_handle_t)")
  def zetMetricQueryReset(hMetricQuery: zet_metric_query_handle_t): ze_result_t = unsupported
  @CSignature("ze_result_t (zet_metric_streamer_handle_t)")
  def zetMetricStreamerClose(hMetricStreamer: zet_metric_streamer_handle_t): ze_result_t = unsupported
  @CSignature("ze_result_t (zet_context_handle_t, zet_device_handle_t, zet_metric_group_handle_t, zet_metric_streamer_desc_t *, ze_event_handle_t, zet_metric_streamer_handle_t *)")
  def zetMetricStreamerOpen(hContext: zet_context_handle_t, hDevice: zet_device_handle_t, hMetricGroup: zet_metric_group_handle_t, desc: Pointer[zet_metric_streamer_desc_t, ReadWrite, Opaque], hNotificationEvent: ze_event_handle_t, phMetricStreamer: Pointer[zet_metric_streamer_handle_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (zet_metric_streamer_handle_t, uint32_t, size_t *, uint8_t *)")
  def zetMetricStreamerReadData(hMetricStreamer: zet_metric_streamer_handle_t, maxReportCount: uint32_t, pRawDataSize: Pointer[size_t, ReadWrite, Opaque], pRawData: Pointer[uint8_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (zet_context_handle_t, zet_device_handle_t, uint32_t, zet_metric_group_handle_t *, zet_metric_tracer_exp_desc_t *, ze_event_handle_t, zet_metric_tracer_exp_handle_t *)")
  def zetMetricTracerCreateExp(hContext: zet_context_handle_t, hDevice: zet_device_handle_t, metricGroupCount: uint32_t, phMetricGroups: Pointer[zet_metric_group_handle_t, ReadWrite, Opaque], desc: Pointer[zet_metric_tracer_exp_desc_t, ReadWrite, Opaque], hNotificationEvent: ze_event_handle_t, phMetricTracer: Pointer[zet_metric_tracer_exp_handle_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (zet_metric_decoder_exp_handle_t, size_t *, uint8_t *, uint32_t, zet_metric_handle_t *, uint32_t *, uint32_t *, uint32_t *, zet_metric_entry_exp_t *)")
  def zetMetricTracerDecodeExp(phMetricDecoder: zet_metric_decoder_exp_handle_t, pRawDataSize: Pointer[size_t, ReadWrite, Opaque], pRawData: Pointer[uint8_t, ReadWrite, Opaque], metricsCount: uint32_t, phMetrics: Pointer[zet_metric_handle_t, ReadWrite, Opaque], pSetCount: Pointer[uint32_t, ReadWrite, Opaque], pMetricEntriesCountPerSet: Pointer[uint32_t, ReadWrite, Opaque], pMetricEntriesCount: Pointer[uint32_t, ReadWrite, Opaque], pMetricEntries: Pointer[zet_metric_entry_exp_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (zet_metric_tracer_exp_handle_t)")
  def zetMetricTracerDestroyExp(hMetricTracer: zet_metric_tracer_exp_handle_t): ze_result_t = unsupported
  @CSignature("ze_result_t (zet_metric_tracer_exp_handle_t, ze_bool_t)")
  def zetMetricTracerDisableExp(hMetricTracer: zet_metric_tracer_exp_handle_t, synchronous: ze_bool_t): ze_result_t = unsupported
  @CSignature("ze_result_t (zet_metric_tracer_exp_handle_t, ze_bool_t)")
  def zetMetricTracerEnableExp(hMetricTracer: zet_metric_tracer_exp_handle_t, synchronous: ze_bool_t): ze_result_t = unsupported
  @CSignature("ze_result_t (zet_metric_tracer_exp_handle_t, size_t *, uint8_t *)")
  def zetMetricTracerReadDataExp(hMetricTracer: zet_metric_tracer_exp_handle_t, pRawDataSize: Pointer[size_t, ReadWrite, Opaque], pRawData: Pointer[uint8_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (zet_module_handle_t, zet_module_debug_info_format_t, size_t *, uint8_t *)")
  def zetModuleGetDebugInfo(hModule: zet_module_handle_t, format: zet_module_debug_info_format_t, pSize: Pointer[size_t, ReadWrite, Opaque], pDebugInfo: Pointer[uint8_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (zet_context_handle_t, const zet_tracer_exp_desc_t *, zet_tracer_exp_handle_t *)")
  def zetTracerExpCreate(hContext: zet_context_handle_t, desc: Pointer[zet_tracer_exp_desc_t, Read, Opaque], phTracer: Pointer[zet_tracer_exp_handle_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (zet_tracer_exp_handle_t)")
  def zetTracerExpDestroy(hTracer: zet_tracer_exp_handle_t): ze_result_t = unsupported
  @CSignature("ze_result_t (zet_tracer_exp_handle_t, ze_bool_t)")
  def zetTracerExpSetEnabled(hTracer: zet_tracer_exp_handle_t, enable: ze_bool_t): ze_result_t = unsupported
  @CSignature("ze_result_t (zet_tracer_exp_handle_t, zet_core_callbacks_t *)")
  def zetTracerExpSetEpilogues(hTracer: zet_tracer_exp_handle_t, pCoreCbs: Pointer[zet_core_callbacks_t, ReadWrite, Opaque]): ze_result_t = unsupported
  @CSignature("ze_result_t (zet_tracer_exp_handle_t, zet_core_callbacks_t *)")
  def zetTracerExpSetPrologues(hTracer: zet_tracer_exp_handle_t, pCoreCbs: Pointer[zet_core_callbacks_t, ReadWrite, Opaque]): ze_result_t = unsupported
}

object LevelZero {
  val HeaderVersion: String = "1.33.0"
  val SurfaceSize: Int = 485
}
