#pragma once

#include <cstddef>
#include <cstdint>

// Hand-written mirrors of the CUDA driver ABI structs. The facade deliberately
// avoids the vendor headers so it stays independent of a header revision, and
// the generated layout check pins every field against the pinned header.
namespace gemstone::cuda {

struct CudaArrayDescriptor {
  size_t width;
  size_t height;
  uint32_t format;
  uint32_t channels;
};

struct CudaArray3DDescriptor {
  size_t width;
  size_t height;
  size_t depth;
  uint32_t format;
  uint32_t channels;
  uint32_t flags;
};

struct CudaMemcpy2D {
  size_t src_x_in_bytes;
  size_t src_y;
  uint32_t src_memory_type;
  const void *src_host;
  uint64_t src_device;
  void *src_array;
  size_t src_pitch;
  size_t dst_x_in_bytes;
  size_t dst_y;
  uint32_t dst_memory_type;
  void *dst_host;
  uint64_t dst_device;
  void *dst_array;
  size_t dst_pitch;
  size_t width_in_bytes;
  size_t height;
};

struct CudaMemcpy3D {
  size_t src_x_in_bytes;
  size_t src_y;
  size_t src_z;
  size_t src_lod;
  uint32_t src_memory_type;
  const void *src_host;
  uint64_t src_device;
  void *src_array;
  void *src_reserved;
  size_t src_pitch;
  size_t src_height;
  size_t dst_x_in_bytes;
  size_t dst_y;
  size_t dst_z;
  size_t dst_lod;
  uint32_t dst_memory_type;
  void *dst_host;
  uint64_t dst_device;
  void *dst_array;
  void *dst_reserved;
  size_t dst_pitch;
  size_t dst_height;
  size_t width_in_bytes;
  size_t height;
  size_t depth;
};

struct CudaArrayDescriptorV1 {
  uint32_t width;
  uint32_t height;
  uint32_t format;
  uint32_t channels;
};

struct CudaArray3DDescriptorV1 {
  uint32_t width;
  uint32_t height;
  uint32_t depth;
  uint32_t format;
  uint32_t channels;
  uint32_t flags;
};

struct CudaMemcpy2DV1 {
  uint32_t src_x_in_bytes;
  uint32_t src_y;
  uint32_t src_memory_type;
  const void *src_host;
  uint32_t src_device;
  void *src_array;
  uint32_t src_pitch;
  uint32_t dst_x_in_bytes;
  uint32_t dst_y;
  uint32_t dst_memory_type;
  void *dst_host;
  uint32_t dst_device;
  void *dst_array;
  uint32_t dst_pitch;
  uint32_t width_in_bytes;
  uint32_t height;
};

struct CudaMemcpy3DV1 {
  uint32_t src_x_in_bytes;
  uint32_t src_y;
  uint32_t src_z;
  uint32_t src_lod;
  uint32_t src_memory_type;
  const void *src_host;
  uint32_t src_device;
  void *src_array;
  void *src_reserved;
  uint32_t src_pitch;
  uint32_t src_height;
  uint32_t dst_x_in_bytes;
  uint32_t dst_y;
  uint32_t dst_z;
  uint32_t dst_lod;
  uint32_t dst_memory_type;
  void *dst_host;
  uint32_t dst_device;
  void *dst_array;
  void *dst_reserved;
  uint32_t dst_pitch;
  uint32_t dst_height;
  uint32_t width_in_bytes;
  uint32_t height;
  uint32_t depth;
};

struct CudaLaunchConfig {
  uint32_t grid_x;
  uint32_t grid_y;
  uint32_t grid_z;
  uint32_t block_x;
  uint32_t block_y;
  uint32_t block_z;
  uint32_t shared_memory_bytes;
  void *stream;
  void *attributes;
  uint32_t attribute_count;
};

struct CudaPointerAttributesPrefix {
  int32_t type;
  int32_t device;
  void *device_pointer;
  void *host_pointer;
};

struct CudaPointerAttributes {
  int32_t type;
  int32_t device;
  void *device_pointer;
  void *host_pointer;
  long reserved[8];
};

struct Dim3 {
  uint32_t x;
  uint32_t y;
  uint32_t z;
};

struct DevicePropertiesPrefix {
  char name[256];
  uint8_t uuid[16];
  char luid[8];
  uint32_t luid_device_node_mask;
  size_t total_global_memory;
  size_t shared_memory_per_block;
  int32_t registers_per_block;
  int32_t warp_size;
  size_t memory_pitch;
  int32_t max_threads_per_block;
  int32_t max_threads_dimension[3];
  int32_t max_grid_size[3];
  size_t total_constant_memory;
  int32_t major;
  int32_t minor;
  size_t texture_alignment;
  size_t texture_pitch_alignment;
  int32_t multiprocessor_count;
};

struct FunctionAttributesPrefix {
  size_t shared_size_bytes;
  size_t constant_size_bytes;
  size_t local_size_bytes;
  int32_t max_threads_per_block;
  int32_t register_count;
  int32_t ptx_version;
  int32_t binary_version;
  int32_t cache_mode_ca;
  int32_t max_dynamic_shared_size_bytes;
  int32_t preferred_shared_memory_carveout;
};

struct LegacyDeviceProperties {
  int32_t max_threads_per_block;
  int32_t max_threads_dim[3];
  int32_t max_grid_size[3];
  int32_t shared_memory_per_block;
  int32_t total_constant_memory;
  int32_t simd_width;
  int32_t memory_pitch;
  int32_t registers_per_block;
  int32_t clock_rate;
  int32_t texture_alignment;
};

} // namespace gemstone::cuda
