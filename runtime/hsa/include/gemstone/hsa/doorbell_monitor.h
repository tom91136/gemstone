// SPDX-License-Identifier: Apache-2.0
#ifndef GEMSTONE_HSA_DOORBELL_MONITOR_H
#define GEMSTONE_HSA_DOORBELL_MONITOR_H

#include <stddef.h>
#include <stdint.h>

#ifdef __cplusplus
extern "C" {
#endif

typedef struct gemstone_hsa_doorbell_monitor gemstone_hsa_doorbell_monitor;
typedef struct gemstone_hsa_queue_registration gemstone_hsa_queue_registration;

typedef enum gemstone_hsa_packet_result {
  GEMSTONE_HSA_PACKET_ACCEPTED = 0,
  GEMSTONE_HSA_PACKET_RETRY = 1,
  GEMSTONE_HSA_PACKET_FATAL = 2
} gemstone_hsa_packet_result;

typedef gemstone_hsa_packet_result (*gemstone_hsa_packet_consumer)(uint64_t queue_id, uint64_t packet_id, const void *packet,
                                                                   size_t packet_size, void *user_data);

typedef struct gemstone_hsa_doorbell_monitor_config {
  // Maximum number of simultaneously registered queues. Zero selects 4096.
  uint32_t max_queues;
  // Busy scan passes before the monitor yields. Zero selects 64.
  uint32_t spin_passes;
  // Maximum sleep while queues exist. Zero selects 50 microseconds.
  uint32_t maximum_sleep_ns;
} gemstone_hsa_doorbell_monitor_config;

typedef struct gemstone_hsa_queue_descriptor {
  uint64_t queue_id;
  void *ring_base;
  size_t ring_size_packets;
  size_t packet_size;
  size_t packet_header_offset;
  uint16_t packet_type_mask;
  uint16_t invalid_packet_type;
  uint64_t *read_index;
  uint64_t *write_index;
  gemstone_hsa_packet_consumer consume;
  void *user_data;
} gemstone_hsa_queue_descriptor;

// Creates one process-wide monitor. The implementation owns one polling thread,
// regardless of queue count. It sleeps without polling while no queues exist.
gemstone_hsa_doorbell_monitor *gemstone_hsa_doorbell_monitor_create(const gemstone_hsa_doorbell_monitor_config *config);

void gemstone_hsa_doorbell_monitor_destroy(gemstone_hsa_doorbell_monitor *monitor);

// Registers an AQL-compatible ring. On success, *doorbell_address is a stable,
// naturally aligned address suitable for the ordinary release/volatile stores
// performed by ROCR. The address remains valid until unregister returns.
gemstone_hsa_queue_registration *gemstone_hsa_doorbell_monitor_register(gemstone_hsa_doorbell_monitor *monitor,
                                                                        const gemstone_hsa_queue_descriptor *queue,
                                                                        uint64_t **doorbell_address);

void gemstone_hsa_doorbell_monitor_unregister(gemstone_hsa_queue_registration *registration);

uint32_t gemstone_hsa_doorbell_monitor_thread_count(const gemstone_hsa_doorbell_monitor *monitor);

#ifdef __cplusplus
}
#endif

#endif
