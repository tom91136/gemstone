/* Generated from the official API manifest format 6. Regenerate with: sbt codegen */
#pragma once

#include <gemstone/export.h>
#include <rocm_smi/rocm_smi.h>

#ifdef __cplusplus
extern "C" {
#endif

extern rsmi_status_t rsmi_compute_process_gpus_get(uint32_t, uint32_t *, uint32_t *) GEMSTONE_PUBLIC_API;
extern rsmi_status_t rsmi_compute_process_info_by_device_get(uint32_t, uint32_t, rsmi_process_info_t *) GEMSTONE_PUBLIC_API;
extern rsmi_status_t rsmi_compute_process_info_by_pid_get(uint32_t, rsmi_process_info_t *) GEMSTONE_PUBLIC_API;
extern rsmi_status_t rsmi_compute_process_info_get(rsmi_process_info_t *, uint32_t *) GEMSTONE_PUBLIC_API;
extern rsmi_status_t rsmi_counter_available_counters_get(uint32_t, rsmi_event_group_t, uint32_t *) GEMSTONE_PUBLIC_API;
extern rsmi_status_t rsmi_counter_control(rsmi_event_handle_t, rsmi_counter_command_t, void *) GEMSTONE_PUBLIC_API;
extern rsmi_status_t rsmi_counter_read(rsmi_event_handle_t, rsmi_counter_value_t *) GEMSTONE_PUBLIC_API;
extern rsmi_status_t rsmi_dev_activity_avg_mm_get(uint32_t, uint16_t *) GEMSTONE_PUBLIC_API;
extern rsmi_status_t rsmi_dev_activity_metric_get(uint32_t, rsmi_activity_metric_t, rsmi_activity_metric_counter_t *) GEMSTONE_PUBLIC_API;
extern rsmi_status_t rsmi_dev_brand_get(uint32_t, char *, uint32_t) GEMSTONE_PUBLIC_API;
extern rsmi_status_t rsmi_dev_busy_percent_get(uint32_t, uint32_t *) GEMSTONE_PUBLIC_API;
extern rsmi_status_t rsmi_dev_clk_extremum_set(uint32_t, rsmi_freq_ind_t, uint64_t, rsmi_clk_type_t) GEMSTONE_PUBLIC_API;
extern rsmi_status_t rsmi_dev_clk_range_set(uint32_t, uint64_t, uint64_t, rsmi_clk_type_t) GEMSTONE_PUBLIC_API;
extern rsmi_status_t rsmi_dev_compute_partition_get(uint32_t, char *, uint32_t) GEMSTONE_PUBLIC_API;
extern rsmi_status_t rsmi_dev_compute_partition_set(uint32_t, rsmi_compute_partition_type_t) GEMSTONE_PUBLIC_API;
extern rsmi_status_t rsmi_dev_counter_create(uint32_t, rsmi_event_type_t, rsmi_event_handle_t *) GEMSTONE_PUBLIC_API;
extern rsmi_status_t rsmi_dev_counter_destroy(rsmi_event_handle_t) GEMSTONE_PUBLIC_API;
extern rsmi_status_t rsmi_dev_counter_group_supported(uint32_t, rsmi_event_group_t) GEMSTONE_PUBLIC_API;
extern rsmi_status_t rsmi_dev_current_socket_power_get(uint32_t, uint64_t *) GEMSTONE_PUBLIC_API;
extern rsmi_status_t rsmi_dev_device_identifiers_get(uint32_t, rsmi_device_identifiers_t *) GEMSTONE_PUBLIC_API;
extern rsmi_status_t rsmi_dev_drm_render_minor_get(uint32_t, uint32_t *) GEMSTONE_PUBLIC_API;
extern rsmi_status_t rsmi_dev_ecc_count_get(uint32_t, rsmi_gpu_block_t, rsmi_error_count_t *) GEMSTONE_PUBLIC_API;
extern rsmi_status_t rsmi_dev_ecc_enabled_get(uint32_t, uint64_t *) GEMSTONE_PUBLIC_API;
extern rsmi_status_t rsmi_dev_ecc_status_get(uint32_t, rsmi_gpu_block_t, rsmi_ras_err_state_t *) GEMSTONE_PUBLIC_API;
extern rsmi_status_t rsmi_dev_energy_count_get(uint32_t, uint64_t *, float *, uint64_t *) GEMSTONE_PUBLIC_API;
extern rsmi_status_t rsmi_dev_fan_reset(uint32_t, uint32_t) GEMSTONE_PUBLIC_API;
extern rsmi_status_t rsmi_dev_fan_rpms_get(uint32_t, uint32_t, int64_t *) GEMSTONE_PUBLIC_API;
extern rsmi_status_t rsmi_dev_fan_speed_get(uint32_t, uint32_t, int64_t *) GEMSTONE_PUBLIC_API;
extern rsmi_status_t rsmi_dev_fan_speed_max_get(uint32_t, uint32_t, uint64_t *) GEMSTONE_PUBLIC_API;
extern rsmi_status_t rsmi_dev_fan_speed_set(uint32_t, uint32_t, uint64_t) GEMSTONE_PUBLIC_API;
extern rsmi_status_t rsmi_dev_firmware_version_get(uint32_t, rsmi_fw_block_t, uint64_t *) GEMSTONE_PUBLIC_API;
extern rsmi_status_t rsmi_dev_gpu_clk_freq_get(uint32_t, rsmi_clk_type_t, rsmi_frequencies_t *) GEMSTONE_PUBLIC_API;
extern rsmi_status_t rsmi_dev_gpu_clk_freq_set(uint32_t, rsmi_clk_type_t, uint64_t) GEMSTONE_PUBLIC_API;
extern rsmi_status_t rsmi_dev_gpu_metrics_info_get(uint32_t, rsmi_gpu_metrics_t *) GEMSTONE_PUBLIC_API;
extern rsmi_status_t rsmi_dev_gpu_reset(uint32_t) GEMSTONE_PUBLIC_API;
extern rsmi_status_t rsmi_dev_guid_get(uint32_t, uint64_t *) GEMSTONE_PUBLIC_API;
extern rsmi_status_t rsmi_dev_id_get(uint32_t, uint16_t *) GEMSTONE_PUBLIC_API;
extern rsmi_status_t rsmi_dev_market_name_get(uint32_t, char *, uint32_t) GEMSTONE_PUBLIC_API;
extern rsmi_status_t rsmi_dev_mem_overdrive_level_get(uint32_t, uint32_t *) GEMSTONE_PUBLIC_API;
extern rsmi_status_t rsmi_dev_memory_busy_percent_get(uint32_t, uint32_t *) GEMSTONE_PUBLIC_API;
extern rsmi_status_t rsmi_dev_memory_partition_capabilities_get(uint32_t, char *, uint32_t) GEMSTONE_PUBLIC_API;
extern rsmi_status_t rsmi_dev_memory_partition_get(uint32_t, char *, uint32_t) GEMSTONE_PUBLIC_API;
extern rsmi_status_t rsmi_dev_memory_partition_set(uint32_t, rsmi_memory_partition_type_t) GEMSTONE_PUBLIC_API;
extern rsmi_status_t rsmi_dev_memory_reserved_pages_get(uint32_t, uint32_t *, rsmi_retired_page_record_t *) GEMSTONE_PUBLIC_API;
extern rsmi_status_t rsmi_dev_memory_total_get(uint32_t, rsmi_memory_type_t, uint64_t *) GEMSTONE_PUBLIC_API;
extern rsmi_status_t rsmi_dev_memory_usage_get(uint32_t, rsmi_memory_type_t, uint64_t *) GEMSTONE_PUBLIC_API;
extern rsmi_status_t rsmi_dev_metrics_header_info_get(uint32_t, metrics_table_header_t *) GEMSTONE_PUBLIC_API;
extern rsmi_status_t rsmi_dev_metrics_log_get(uint32_t) GEMSTONE_PUBLIC_API;
extern rsmi_status_t rsmi_dev_metrics_xcd_counter_get(uint32_t, uint16_t *) GEMSTONE_PUBLIC_API;
extern rsmi_status_t rsmi_dev_name_get(uint32_t, char *, size_t) GEMSTONE_PUBLIC_API;
extern rsmi_status_t rsmi_dev_node_id_get(uint32_t, uint32_t *) GEMSTONE_PUBLIC_API;
extern rsmi_status_t rsmi_dev_od_clk_info_set(uint32_t, rsmi_freq_ind_t, uint64_t, rsmi_clk_type_t) GEMSTONE_PUBLIC_API;
extern rsmi_status_t rsmi_dev_od_volt_curve_regions_get(uint32_t, uint32_t *, rsmi_freq_volt_region_t *) GEMSTONE_PUBLIC_API;
extern rsmi_status_t rsmi_dev_od_volt_info_get(uint32_t, rsmi_od_volt_freq_data_t *) GEMSTONE_PUBLIC_API;
extern rsmi_status_t rsmi_dev_od_volt_info_set(uint32_t, uint32_t, uint64_t, uint64_t) GEMSTONE_PUBLIC_API;
extern rsmi_status_t rsmi_dev_overdrive_level_get(uint32_t, uint32_t *) GEMSTONE_PUBLIC_API;
extern rsmi_status_t rsmi_dev_overdrive_level_set(uint32_t, uint32_t) GEMSTONE_PUBLIC_API;
extern rsmi_status_t rsmi_dev_overdrive_level_set_v1(uint32_t, uint32_t) GEMSTONE_PUBLIC_API;
extern rsmi_status_t rsmi_dev_partition_id_get(uint32_t, uint32_t *) GEMSTONE_PUBLIC_API;
extern rsmi_status_t rsmi_dev_pci_bandwidth_get(uint32_t, rsmi_pcie_bandwidth_t *) GEMSTONE_PUBLIC_API;
extern rsmi_status_t rsmi_dev_pci_bandwidth_set(uint32_t, uint64_t) GEMSTONE_PUBLIC_API;
extern rsmi_status_t rsmi_dev_pci_id_get(uint32_t, uint64_t *) GEMSTONE_PUBLIC_API;
extern rsmi_status_t rsmi_dev_pci_replay_counter_get(uint32_t, uint64_t *) GEMSTONE_PUBLIC_API;
extern rsmi_status_t rsmi_dev_pci_throughput_get(uint32_t, uint64_t *, uint64_t *, uint64_t *) GEMSTONE_PUBLIC_API;
extern rsmi_status_t rsmi_dev_perf_level_get(uint32_t, rsmi_dev_perf_level_t *) GEMSTONE_PUBLIC_API;
extern rsmi_status_t rsmi_dev_perf_level_set(uint32_t, rsmi_dev_perf_level_t) GEMSTONE_PUBLIC_API;
extern rsmi_status_t rsmi_dev_perf_level_set_v1(uint32_t, rsmi_dev_perf_level_t) GEMSTONE_PUBLIC_API;
extern rsmi_status_t rsmi_dev_power_ave_get(uint32_t, uint32_t, uint64_t *) GEMSTONE_PUBLIC_API;
extern rsmi_status_t rsmi_dev_power_cap_default_get(uint32_t, uint64_t *) GEMSTONE_PUBLIC_API;
extern rsmi_status_t rsmi_dev_power_cap_get(uint32_t, uint32_t, uint64_t *) GEMSTONE_PUBLIC_API;
extern rsmi_status_t rsmi_dev_power_cap_range_get(uint32_t, uint32_t, uint64_t *, uint64_t *) GEMSTONE_PUBLIC_API;
extern rsmi_status_t rsmi_dev_power_cap_set(uint32_t, uint32_t, uint64_t) GEMSTONE_PUBLIC_API;
extern rsmi_status_t rsmi_dev_power_get(uint32_t, uint64_t *, RSMI_POWER_TYPE *) GEMSTONE_PUBLIC_API;
extern rsmi_status_t rsmi_dev_power_profile_presets_get(uint32_t, uint32_t, rsmi_power_profile_status_t *) GEMSTONE_PUBLIC_API;
extern rsmi_status_t rsmi_dev_power_profile_set(uint32_t, uint32_t, rsmi_power_profile_preset_masks_t) GEMSTONE_PUBLIC_API;
extern rsmi_status_t rsmi_dev_revision_get(uint32_t, uint16_t *) GEMSTONE_PUBLIC_API;
extern rsmi_status_t rsmi_dev_serial_number_get(uint32_t, char *, uint32_t) GEMSTONE_PUBLIC_API;
extern rsmi_status_t rsmi_dev_sku_get(uint32_t, uint16_t *) GEMSTONE_PUBLIC_API;
extern rsmi_status_t rsmi_dev_subsystem_id_get(uint32_t, uint16_t *) GEMSTONE_PUBLIC_API;
extern rsmi_status_t rsmi_dev_subsystem_name_get(uint32_t, char *, size_t) GEMSTONE_PUBLIC_API;
extern rsmi_status_t rsmi_dev_subsystem_vendor_id_get(uint32_t, uint16_t *) GEMSTONE_PUBLIC_API;
extern rsmi_status_t rsmi_dev_supported_func_iterator_close(rsmi_func_id_iter_handle_t *) GEMSTONE_PUBLIC_API;
extern rsmi_status_t rsmi_dev_supported_func_iterator_open(uint32_t, rsmi_func_id_iter_handle_t *) GEMSTONE_PUBLIC_API;
extern rsmi_status_t rsmi_dev_supported_variant_iterator_open(rsmi_func_id_iter_handle_t, rsmi_func_id_iter_handle_t *) GEMSTONE_PUBLIC_API;
extern rsmi_status_t rsmi_dev_target_graphics_version_get(uint32_t, uint64_t *) GEMSTONE_PUBLIC_API;
extern rsmi_status_t rsmi_dev_temp_metric_get(uint32_t, uint32_t, rsmi_temperature_metric_t, int64_t *) GEMSTONE_PUBLIC_API;
extern rsmi_status_t rsmi_dev_unique_id_get(uint32_t, uint64_t *) GEMSTONE_PUBLIC_API;
extern rsmi_status_t rsmi_dev_vbios_version_get(uint32_t, char *, uint32_t) GEMSTONE_PUBLIC_API;
extern rsmi_status_t rsmi_dev_vendor_id_get(uint32_t, uint16_t *) GEMSTONE_PUBLIC_API;
extern rsmi_status_t rsmi_dev_vendor_name_get(uint32_t, char *, size_t) GEMSTONE_PUBLIC_API;
extern rsmi_status_t rsmi_dev_volt_metric_get(uint32_t, rsmi_voltage_type_t, rsmi_voltage_metric_t, int64_t *) GEMSTONE_PUBLIC_API;
extern rsmi_status_t rsmi_dev_vram_vendor_get(uint32_t, char *, uint32_t) GEMSTONE_PUBLIC_API;
extern rsmi_status_t rsmi_dev_xgmi_error_reset(uint32_t) GEMSTONE_PUBLIC_API;
extern rsmi_status_t rsmi_dev_xgmi_error_status(uint32_t, rsmi_xgmi_status_t *) GEMSTONE_PUBLIC_API;
extern rsmi_status_t rsmi_dev_xgmi_hive_id_get(uint32_t, uint64_t *) GEMSTONE_PUBLIC_API;
extern rsmi_status_t rsmi_dev_xgmi_physical_id_get(uint32_t, uint16_t *) GEMSTONE_PUBLIC_API;
extern rsmi_status_t rsmi_event_notification_get(int, uint32_t *, rsmi_evt_notification_data_t *) GEMSTONE_PUBLIC_API;
extern rsmi_status_t rsmi_event_notification_init(uint32_t) GEMSTONE_PUBLIC_API;
extern rsmi_status_t rsmi_event_notification_mask_set(uint32_t, uint64_t) GEMSTONE_PUBLIC_API;
extern rsmi_status_t rsmi_event_notification_stop(uint32_t) GEMSTONE_PUBLIC_API;
extern rsmi_status_t rsmi_func_iter_next(rsmi_func_id_iter_handle_t) GEMSTONE_PUBLIC_API;
extern rsmi_status_t rsmi_func_iter_value_get(rsmi_func_id_iter_handle_t, rsmi_func_id_value_t *) GEMSTONE_PUBLIC_API;
extern rsmi_status_t rsmi_init(uint64_t) GEMSTONE_PUBLIC_API;
extern rsmi_status_t rsmi_is_P2P_accessible(uint32_t, uint32_t, bool *) GEMSTONE_PUBLIC_API;
extern rsmi_status_t rsmi_minmax_bandwidth_get(uint32_t, uint32_t, uint64_t *, uint64_t *) GEMSTONE_PUBLIC_API;
extern rsmi_status_t rsmi_num_monitor_devices(uint32_t *) GEMSTONE_PUBLIC_API;
extern rsmi_status_t rsmi_perf_determinism_mode_set(uint32_t, uint64_t) GEMSTONE_PUBLIC_API;
extern rsmi_status_t rsmi_shut_down(void) GEMSTONE_PUBLIC_API;
extern rsmi_status_t rsmi_status_string(rsmi_status_t, const char **) GEMSTONE_PUBLIC_API;
extern rsmi_status_t rsmi_topo_get_link_type(uint32_t, uint32_t, uint64_t *, RSMI_IO_LINK_TYPE *) GEMSTONE_PUBLIC_API;
extern rsmi_status_t rsmi_topo_get_link_weight(uint32_t, uint32_t, uint64_t *) GEMSTONE_PUBLIC_API;
extern rsmi_status_t rsmi_topo_get_numa_node_number(uint32_t, uint32_t *) GEMSTONE_PUBLIC_API;
extern rsmi_status_t rsmi_topo_numa_affinity_get(uint32_t, int32_t *) GEMSTONE_PUBLIC_API;
extern rsmi_status_t rsmi_utilization_count_get(uint32_t, rsmi_utilization_counter_t *, uint32_t, uint64_t *) GEMSTONE_PUBLIC_API;
extern rsmi_status_t rsmi_version_get(rsmi_version_t *) GEMSTONE_PUBLIC_API;
extern rsmi_status_t rsmi_version_str_get(rsmi_sw_component_t, char *, uint32_t) GEMSTONE_PUBLIC_API;

#ifdef __cplusplus
}
#endif
