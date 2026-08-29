// SPDX-License-Identifier: Apache-2.0
#include "gemstone/hsa/virtual_kfd.h"

#include <atomic>
#include <cstddef>
#include <cstdint>
#include <new>

struct gemstone_hsa_virtual_kfd {
  gemstone_hsa_doorbell_monitor *monitor = nullptr;
  std::atomic<uint64_t> next_queue_id{1};
};

struct gemstone_hsa_virtual_queue {
  gemstone_hsa_virtual_kfd *owner = nullptr;
  gemstone_hsa_queue_registration *registration = nullptr;
  alignas(8) uint64_t owned_read_pointer = 0;
  alignas(8) uint64_t owned_write_pointer = 0;
  uint64_t *read_pointer = &owned_read_pointer;
  uint64_t *write_pointer = &owned_write_pointer;
  uint64_t *doorbell = nullptr;
  uint64_t id = 0;
};

extern "C" gemstone_hsa_virtual_kfd *gemstone_hsa_virtual_kfd_create(uint32_t max_queues) {
  auto *kfd = new (std::nothrow) gemstone_hsa_virtual_kfd();
  if (kfd == nullptr) return nullptr;
  gemstone_hsa_doorbell_monitor_config config{};
  config.max_queues = max_queues;
  kfd->monitor = gemstone_hsa_doorbell_monitor_create(&config);
  if (kfd->monitor == nullptr) {
    delete kfd;
    return nullptr;
  }
  return kfd;
}

extern "C" void gemstone_hsa_virtual_kfd_destroy(gemstone_hsa_virtual_kfd *kfd) {
  if (kfd == nullptr) return;
  gemstone_hsa_doorbell_monitor_destroy(kfd->monitor);
  delete kfd;
}

extern "C" gemstone_hsa_virtual_queue *gemstone_hsa_virtual_kfd_create_aql_queue(gemstone_hsa_virtual_kfd *kfd, void *ring_base,
                                                                                 size_t ring_size_packets, uint64_t *read_pointer,
                                                                                 uint64_t *write_pointer,
                                                                                 gemstone_hsa_packet_consumer consume, void *user_data,
                                                                                 gemstone_hsa_virtual_queue_resource *resource) {
  if (kfd == nullptr || resource == nullptr) return nullptr;
  auto *queue = new (std::nothrow) gemstone_hsa_virtual_queue();
  if (queue == nullptr) return nullptr;
  queue->owner = kfd;
  queue->id = kfd->next_queue_id.fetch_add(1, std::memory_order_relaxed);
  if (read_pointer != nullptr) queue->read_pointer = read_pointer;
  if (write_pointer != nullptr) queue->write_pointer = write_pointer;

  gemstone_hsa_queue_descriptor descriptor{};
  descriptor.queue_id = queue->id;
  descriptor.ring_base = ring_base;
  descriptor.ring_size_packets = ring_size_packets;
  descriptor.packet_size = 64;
  descriptor.packet_header_offset = 0;
  descriptor.packet_type_mask = 0xff;
  descriptor.invalid_packet_type = 1;
  descriptor.read_index = queue->read_pointer;
  descriptor.write_index = queue->write_pointer;
  descriptor.consume = consume;
  descriptor.user_data = user_data;
  queue->registration = gemstone_hsa_doorbell_monitor_register(kfd->monitor, &descriptor, &queue->doorbell);
  if (queue->registration == nullptr) {
    delete queue;
    return nullptr;
  }

  resource->queue_id = queue->id;
  resource->doorbell = queue->doorbell;
  resource->write_pointer = queue->write_pointer;
  resource->read_pointer = queue->read_pointer;
  return queue;
}

extern "C" gemstone_hsa_virtual_queue *gemstone_hsa_virtual_kfd_create_execution_queue(gemstone_hsa_virtual_kfd *kfd,
                                                                                       gemstone_hsa_aql_runtime *runtime, void *ring_base,
                                                                                       size_t ring_size_packets,
                                                                                       gemstone_hsa_virtual_queue_resource *resource) {
  if (runtime == nullptr) return nullptr;
  return gemstone_hsa_virtual_kfd_create_aql_queue(kfd, ring_base, ring_size_packets, nullptr, nullptr, gemstone_hsa_aql_runtime_consume,
                                                   runtime, resource);
}

extern "C" void gemstone_hsa_virtual_kfd_destroy_queue(gemstone_hsa_virtual_queue *queue) {
  if (queue == nullptr) return;
  gemstone_hsa_doorbell_monitor_unregister(queue->registration);
  delete queue;
}
