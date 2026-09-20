#ifndef GEMSTONE_HSA_VIRTUAL_KFD_H
#define GEMSTONE_HSA_VIRTUAL_KFD_H

#include "gemstone/hsa/aql_execution.h"
#include "gemstone/hsa/doorbell_monitor.h"

#ifdef __cplusplus
extern "C" {
#endif

typedef struct gemstone_hsa_virtual_kfd gemstone_hsa_virtual_kfd;
typedef struct gemstone_hsa_virtual_queue gemstone_hsa_virtual_queue;

// Pointer-level counterpart of the queue resources returned by
// hsaKmtCreateQueue. A future libhsakmt or KFD adapter translates this into
// HsaQueueResource and/or KFD mmap offsets without changing queue execution.
typedef struct gemstone_hsa_virtual_queue_resource {
  uint64_t queue_id;
  uint64_t *doorbell;
  uint64_t *write_pointer;
  uint64_t *read_pointer;
} gemstone_hsa_virtual_queue_resource;

gemstone_hsa_virtual_kfd *gemstone_hsa_virtual_kfd_create(uint32_t max_queues);
void gemstone_hsa_virtual_kfd_destroy(gemstone_hsa_virtual_kfd *kfd);

gemstone_hsa_virtual_queue *gemstone_hsa_virtual_kfd_create_aql_queue(gemstone_hsa_virtual_kfd *kfd, void *ring_base,
                                                                      size_t ring_size_packets, uint64_t *read_pointer,
                                                                      uint64_t *write_pointer, gemstone_hsa_packet_consumer consume,
                                                                      void *user_data, gemstone_hsa_virtual_queue_resource *resource);

gemstone_hsa_virtual_queue *gemstone_hsa_virtual_kfd_create_execution_queue(gemstone_hsa_virtual_kfd *kfd,
                                                                            gemstone_hsa_aql_runtime *runtime, void *ring_base,
                                                                            size_t ring_size_packets,
                                                                            gemstone_hsa_virtual_queue_resource *resource);

void gemstone_hsa_virtual_kfd_destroy_queue(gemstone_hsa_virtual_queue *queue);

#ifdef __cplusplus
}
#endif

#endif
