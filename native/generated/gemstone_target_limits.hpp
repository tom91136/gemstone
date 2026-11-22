/* Generated from the Architecture table. Regenerate with: sbt codegen */
#pragma once
#include <cstdint>
#include <optional>
#include <string_view>

namespace gemstone {
struct TargetLimits {
  int32_t maxWarpsPerMultiprocessor;
  int32_t maxBlocksPerMultiprocessor;
  int32_t maxThreadsPerBlock;
  int32_t partitions;
  int32_t partitionRegisters;
  int32_t registerGranule;
  int32_t maxRegistersPerThread;
  int32_t maxRegistersPerBlock;
  int32_t maxSharedMemoryPerMultiprocessor;
  int32_t maxSharedMemoryPerBlock;
  int32_t reservedSharedMemoryPerBlock;
  int32_t sharedMemoryGranule;
  bool clusterLaunch;
};

inline std::optional<TargetLimits> targetLimits(std::string_view target) noexcept {
  if (target == "gfx1010") return TargetLimits{
    .maxWarpsPerMultiprocessor = 80,
    .maxBlocksPerMultiprocessor = 32,
    .maxThreadsPerBlock = 1024,
    .partitions = 4,
    .partitionRegisters = 32768,
    .registerGranule = 256,
    .maxRegistersPerThread = 256,
    .maxRegistersPerBlock = 131072,
    .maxSharedMemoryPerMultiprocessor = 131072,
    .maxSharedMemoryPerBlock = 65536,
    .reservedSharedMemoryPerBlock = 0,
    .sharedMemoryGranule = 512,
    .clusterLaunch = false
  };
  if (target == "gfx1011") return TargetLimits{
    .maxWarpsPerMultiprocessor = 80,
    .maxBlocksPerMultiprocessor = 32,
    .maxThreadsPerBlock = 1024,
    .partitions = 4,
    .partitionRegisters = 32768,
    .registerGranule = 256,
    .maxRegistersPerThread = 256,
    .maxRegistersPerBlock = 131072,
    .maxSharedMemoryPerMultiprocessor = 131072,
    .maxSharedMemoryPerBlock = 65536,
    .reservedSharedMemoryPerBlock = 0,
    .sharedMemoryGranule = 512,
    .clusterLaunch = false
  };
  if (target == "gfx1012") return TargetLimits{
    .maxWarpsPerMultiprocessor = 80,
    .maxBlocksPerMultiprocessor = 32,
    .maxThreadsPerBlock = 1024,
    .partitions = 4,
    .partitionRegisters = 32768,
    .registerGranule = 256,
    .maxRegistersPerThread = 256,
    .maxRegistersPerBlock = 131072,
    .maxSharedMemoryPerMultiprocessor = 131072,
    .maxSharedMemoryPerBlock = 65536,
    .reservedSharedMemoryPerBlock = 0,
    .sharedMemoryGranule = 512,
    .clusterLaunch = false
  };
  if (target == "gfx1013") return TargetLimits{
    .maxWarpsPerMultiprocessor = 80,
    .maxBlocksPerMultiprocessor = 32,
    .maxThreadsPerBlock = 1024,
    .partitions = 4,
    .partitionRegisters = 32768,
    .registerGranule = 256,
    .maxRegistersPerThread = 256,
    .maxRegistersPerBlock = 131072,
    .maxSharedMemoryPerMultiprocessor = 131072,
    .maxSharedMemoryPerBlock = 65536,
    .reservedSharedMemoryPerBlock = 0,
    .sharedMemoryGranule = 512,
    .clusterLaunch = false
  };
  if (target == "gfx1030") return TargetLimits{
    .maxWarpsPerMultiprocessor = 64,
    .maxBlocksPerMultiprocessor = 32,
    .maxThreadsPerBlock = 1024,
    .partitions = 4,
    .partitionRegisters = 32768,
    .registerGranule = 512,
    .maxRegistersPerThread = 256,
    .maxRegistersPerBlock = 131072,
    .maxSharedMemoryPerMultiprocessor = 131072,
    .maxSharedMemoryPerBlock = 65536,
    .reservedSharedMemoryPerBlock = 0,
    .sharedMemoryGranule = 512,
    .clusterLaunch = false
  };
  if (target == "gfx1031") return TargetLimits{
    .maxWarpsPerMultiprocessor = 64,
    .maxBlocksPerMultiprocessor = 32,
    .maxThreadsPerBlock = 1024,
    .partitions = 4,
    .partitionRegisters = 32768,
    .registerGranule = 512,
    .maxRegistersPerThread = 256,
    .maxRegistersPerBlock = 131072,
    .maxSharedMemoryPerMultiprocessor = 131072,
    .maxSharedMemoryPerBlock = 65536,
    .reservedSharedMemoryPerBlock = 0,
    .sharedMemoryGranule = 512,
    .clusterLaunch = false
  };
  if (target == "gfx1032") return TargetLimits{
    .maxWarpsPerMultiprocessor = 64,
    .maxBlocksPerMultiprocessor = 32,
    .maxThreadsPerBlock = 1024,
    .partitions = 4,
    .partitionRegisters = 32768,
    .registerGranule = 512,
    .maxRegistersPerThread = 256,
    .maxRegistersPerBlock = 131072,
    .maxSharedMemoryPerMultiprocessor = 131072,
    .maxSharedMemoryPerBlock = 65536,
    .reservedSharedMemoryPerBlock = 0,
    .sharedMemoryGranule = 512,
    .clusterLaunch = false
  };
  if (target == "gfx1033") return TargetLimits{
    .maxWarpsPerMultiprocessor = 64,
    .maxBlocksPerMultiprocessor = 32,
    .maxThreadsPerBlock = 1024,
    .partitions = 4,
    .partitionRegisters = 32768,
    .registerGranule = 512,
    .maxRegistersPerThread = 256,
    .maxRegistersPerBlock = 131072,
    .maxSharedMemoryPerMultiprocessor = 131072,
    .maxSharedMemoryPerBlock = 65536,
    .reservedSharedMemoryPerBlock = 0,
    .sharedMemoryGranule = 512,
    .clusterLaunch = false
  };
  if (target == "gfx1034") return TargetLimits{
    .maxWarpsPerMultiprocessor = 64,
    .maxBlocksPerMultiprocessor = 32,
    .maxThreadsPerBlock = 1024,
    .partitions = 4,
    .partitionRegisters = 32768,
    .registerGranule = 512,
    .maxRegistersPerThread = 256,
    .maxRegistersPerBlock = 131072,
    .maxSharedMemoryPerMultiprocessor = 131072,
    .maxSharedMemoryPerBlock = 65536,
    .reservedSharedMemoryPerBlock = 0,
    .sharedMemoryGranule = 512,
    .clusterLaunch = false
  };
  if (target == "gfx1035") return TargetLimits{
    .maxWarpsPerMultiprocessor = 64,
    .maxBlocksPerMultiprocessor = 32,
    .maxThreadsPerBlock = 1024,
    .partitions = 4,
    .partitionRegisters = 32768,
    .registerGranule = 512,
    .maxRegistersPerThread = 256,
    .maxRegistersPerBlock = 131072,
    .maxSharedMemoryPerMultiprocessor = 131072,
    .maxSharedMemoryPerBlock = 65536,
    .reservedSharedMemoryPerBlock = 0,
    .sharedMemoryGranule = 512,
    .clusterLaunch = false
  };
  if (target == "gfx1036") return TargetLimits{
    .maxWarpsPerMultiprocessor = 64,
    .maxBlocksPerMultiprocessor = 32,
    .maxThreadsPerBlock = 1024,
    .partitions = 4,
    .partitionRegisters = 32768,
    .registerGranule = 512,
    .maxRegistersPerThread = 256,
    .maxRegistersPerBlock = 131072,
    .maxSharedMemoryPerMultiprocessor = 131072,
    .maxSharedMemoryPerBlock = 65536,
    .reservedSharedMemoryPerBlock = 0,
    .sharedMemoryGranule = 512,
    .clusterLaunch = false
  };
  if (target == "gfx1100") return TargetLimits{
    .maxWarpsPerMultiprocessor = 64,
    .maxBlocksPerMultiprocessor = 32,
    .maxThreadsPerBlock = 1024,
    .partitions = 4,
    .partitionRegisters = 49152,
    .registerGranule = 768,
    .maxRegistersPerThread = 256,
    .maxRegistersPerBlock = 196608,
    .maxSharedMemoryPerMultiprocessor = 131072,
    .maxSharedMemoryPerBlock = 65536,
    .reservedSharedMemoryPerBlock = 0,
    .sharedMemoryGranule = 512,
    .clusterLaunch = false
  };
  if (target == "gfx1101") return TargetLimits{
    .maxWarpsPerMultiprocessor = 64,
    .maxBlocksPerMultiprocessor = 32,
    .maxThreadsPerBlock = 1024,
    .partitions = 4,
    .partitionRegisters = 49152,
    .registerGranule = 768,
    .maxRegistersPerThread = 256,
    .maxRegistersPerBlock = 196608,
    .maxSharedMemoryPerMultiprocessor = 131072,
    .maxSharedMemoryPerBlock = 65536,
    .reservedSharedMemoryPerBlock = 0,
    .sharedMemoryGranule = 512,
    .clusterLaunch = false
  };
  if (target == "gfx1102") return TargetLimits{
    .maxWarpsPerMultiprocessor = 64,
    .maxBlocksPerMultiprocessor = 32,
    .maxThreadsPerBlock = 1024,
    .partitions = 4,
    .partitionRegisters = 32768,
    .registerGranule = 512,
    .maxRegistersPerThread = 256,
    .maxRegistersPerBlock = 131072,
    .maxSharedMemoryPerMultiprocessor = 131072,
    .maxSharedMemoryPerBlock = 65536,
    .reservedSharedMemoryPerBlock = 0,
    .sharedMemoryGranule = 512,
    .clusterLaunch = false
  };
  if (target == "gfx1103") return TargetLimits{
    .maxWarpsPerMultiprocessor = 64,
    .maxBlocksPerMultiprocessor = 32,
    .maxThreadsPerBlock = 1024,
    .partitions = 4,
    .partitionRegisters = 32768,
    .registerGranule = 512,
    .maxRegistersPerThread = 256,
    .maxRegistersPerBlock = 131072,
    .maxSharedMemoryPerMultiprocessor = 131072,
    .maxSharedMemoryPerBlock = 65536,
    .reservedSharedMemoryPerBlock = 0,
    .sharedMemoryGranule = 512,
    .clusterLaunch = false
  };
  if (target == "gfx1150") return TargetLimits{
    .maxWarpsPerMultiprocessor = 64,
    .maxBlocksPerMultiprocessor = 32,
    .maxThreadsPerBlock = 1024,
    .partitions = 4,
    .partitionRegisters = 32768,
    .registerGranule = 512,
    .maxRegistersPerThread = 256,
    .maxRegistersPerBlock = 131072,
    .maxSharedMemoryPerMultiprocessor = 131072,
    .maxSharedMemoryPerBlock = 65536,
    .reservedSharedMemoryPerBlock = 0,
    .sharedMemoryGranule = 512,
    .clusterLaunch = false
  };
  if (target == "gfx1151") return TargetLimits{
    .maxWarpsPerMultiprocessor = 64,
    .maxBlocksPerMultiprocessor = 32,
    .maxThreadsPerBlock = 1024,
    .partitions = 4,
    .partitionRegisters = 49152,
    .registerGranule = 768,
    .maxRegistersPerThread = 256,
    .maxRegistersPerBlock = 196608,
    .maxSharedMemoryPerMultiprocessor = 131072,
    .maxSharedMemoryPerBlock = 65536,
    .reservedSharedMemoryPerBlock = 0,
    .sharedMemoryGranule = 512,
    .clusterLaunch = false
  };
  if (target == "gfx1152") return TargetLimits{
    .maxWarpsPerMultiprocessor = 64,
    .maxBlocksPerMultiprocessor = 32,
    .maxThreadsPerBlock = 1024,
    .partitions = 4,
    .partitionRegisters = 32768,
    .registerGranule = 512,
    .maxRegistersPerThread = 256,
    .maxRegistersPerBlock = 131072,
    .maxSharedMemoryPerMultiprocessor = 131072,
    .maxSharedMemoryPerBlock = 65536,
    .reservedSharedMemoryPerBlock = 0,
    .sharedMemoryGranule = 512,
    .clusterLaunch = false
  };
  if (target == "gfx1153") return TargetLimits{
    .maxWarpsPerMultiprocessor = 64,
    .maxBlocksPerMultiprocessor = 32,
    .maxThreadsPerBlock = 1024,
    .partitions = 4,
    .partitionRegisters = 32768,
    .registerGranule = 512,
    .maxRegistersPerThread = 256,
    .maxRegistersPerBlock = 131072,
    .maxSharedMemoryPerMultiprocessor = 131072,
    .maxSharedMemoryPerBlock = 65536,
    .reservedSharedMemoryPerBlock = 0,
    .sharedMemoryGranule = 512,
    .clusterLaunch = false
  };
  if (target == "gfx1154") return TargetLimits{
    .maxWarpsPerMultiprocessor = 64,
    .maxBlocksPerMultiprocessor = 32,
    .maxThreadsPerBlock = 1024,
    .partitions = 4,
    .partitionRegisters = 32768,
    .registerGranule = 512,
    .maxRegistersPerThread = 256,
    .maxRegistersPerBlock = 131072,
    .maxSharedMemoryPerMultiprocessor = 131072,
    .maxSharedMemoryPerBlock = 65536,
    .reservedSharedMemoryPerBlock = 0,
    .sharedMemoryGranule = 512,
    .clusterLaunch = false
  };
  if (target == "gfx1170") return TargetLimits{
    .maxWarpsPerMultiprocessor = 64,
    .maxBlocksPerMultiprocessor = 32,
    .maxThreadsPerBlock = 1024,
    .partitions = 4,
    .partitionRegisters = 32768,
    .registerGranule = 512,
    .maxRegistersPerThread = 256,
    .maxRegistersPerBlock = 131072,
    .maxSharedMemoryPerMultiprocessor = 131072,
    .maxSharedMemoryPerBlock = 65536,
    .reservedSharedMemoryPerBlock = 0,
    .sharedMemoryGranule = 512,
    .clusterLaunch = false
  };
  if (target == "gfx1171") return TargetLimits{
    .maxWarpsPerMultiprocessor = 64,
    .maxBlocksPerMultiprocessor = 32,
    .maxThreadsPerBlock = 1024,
    .partitions = 4,
    .partitionRegisters = 32768,
    .registerGranule = 512,
    .maxRegistersPerThread = 256,
    .maxRegistersPerBlock = 131072,
    .maxSharedMemoryPerMultiprocessor = 131072,
    .maxSharedMemoryPerBlock = 65536,
    .reservedSharedMemoryPerBlock = 0,
    .sharedMemoryGranule = 512,
    .clusterLaunch = false
  };
  if (target == "gfx1172") return TargetLimits{
    .maxWarpsPerMultiprocessor = 64,
    .maxBlocksPerMultiprocessor = 32,
    .maxThreadsPerBlock = 1024,
    .partitions = 4,
    .partitionRegisters = 32768,
    .registerGranule = 512,
    .maxRegistersPerThread = 256,
    .maxRegistersPerBlock = 131072,
    .maxSharedMemoryPerMultiprocessor = 131072,
    .maxSharedMemoryPerBlock = 65536,
    .reservedSharedMemoryPerBlock = 0,
    .sharedMemoryGranule = 512,
    .clusterLaunch = false
  };
  if (target == "gfx1200") return TargetLimits{
    .maxWarpsPerMultiprocessor = 64,
    .maxBlocksPerMultiprocessor = 32,
    .maxThreadsPerBlock = 1024,
    .partitions = 4,
    .partitionRegisters = 49152,
    .registerGranule = 768,
    .maxRegistersPerThread = 256,
    .maxRegistersPerBlock = 196608,
    .maxSharedMemoryPerMultiprocessor = 131072,
    .maxSharedMemoryPerBlock = 65536,
    .reservedSharedMemoryPerBlock = 0,
    .sharedMemoryGranule = 512,
    .clusterLaunch = false
  };
  if (target == "gfx1201") return TargetLimits{
    .maxWarpsPerMultiprocessor = 64,
    .maxBlocksPerMultiprocessor = 32,
    .maxThreadsPerBlock = 1024,
    .partitions = 4,
    .partitionRegisters = 49152,
    .registerGranule = 768,
    .maxRegistersPerThread = 256,
    .maxRegistersPerBlock = 196608,
    .maxSharedMemoryPerMultiprocessor = 131072,
    .maxSharedMemoryPerBlock = 65536,
    .reservedSharedMemoryPerBlock = 0,
    .sharedMemoryGranule = 512,
    .clusterLaunch = false
  };
  if (target == "gfx1250") return TargetLimits{
    .maxWarpsPerMultiprocessor = 64,
    .maxBlocksPerMultiprocessor = 16,
    .maxThreadsPerBlock = 1024,
    .partitions = 4,
    .partitionRegisters = 32768,
    .registerGranule = 512,
    .maxRegistersPerThread = 1024,
    .maxRegistersPerBlock = 131072,
    .maxSharedMemoryPerMultiprocessor = 327680,
    .maxSharedMemoryPerBlock = 327680,
    .reservedSharedMemoryPerBlock = 0,
    .sharedMemoryGranule = 2048,
    .clusterLaunch = false
  };
  if (target == "gfx1251") return TargetLimits{
    .maxWarpsPerMultiprocessor = 64,
    .maxBlocksPerMultiprocessor = 16,
    .maxThreadsPerBlock = 1024,
    .partitions = 4,
    .partitionRegisters = 32768,
    .registerGranule = 512,
    .maxRegistersPerThread = 1024,
    .maxRegistersPerBlock = 131072,
    .maxSharedMemoryPerMultiprocessor = 327680,
    .maxSharedMemoryPerBlock = 327680,
    .reservedSharedMemoryPerBlock = 0,
    .sharedMemoryGranule = 2048,
    .clusterLaunch = false
  };
  if (target == "gfx600") return TargetLimits{
    .maxWarpsPerMultiprocessor = 40,
    .maxBlocksPerMultiprocessor = 16,
    .maxThreadsPerBlock = 1024,
    .partitions = 4,
    .partitionRegisters = 16384,
    .registerGranule = 256,
    .maxRegistersPerThread = 256,
    .maxRegistersPerBlock = 65536,
    .maxSharedMemoryPerMultiprocessor = 32768,
    .maxSharedMemoryPerBlock = 32768,
    .reservedSharedMemoryPerBlock = 0,
    .sharedMemoryGranule = 256,
    .clusterLaunch = false
  };
  if (target == "gfx601") return TargetLimits{
    .maxWarpsPerMultiprocessor = 40,
    .maxBlocksPerMultiprocessor = 16,
    .maxThreadsPerBlock = 1024,
    .partitions = 4,
    .partitionRegisters = 16384,
    .registerGranule = 256,
    .maxRegistersPerThread = 256,
    .maxRegistersPerBlock = 65536,
    .maxSharedMemoryPerMultiprocessor = 32768,
    .maxSharedMemoryPerBlock = 32768,
    .reservedSharedMemoryPerBlock = 0,
    .sharedMemoryGranule = 256,
    .clusterLaunch = false
  };
  if (target == "gfx602") return TargetLimits{
    .maxWarpsPerMultiprocessor = 40,
    .maxBlocksPerMultiprocessor = 16,
    .maxThreadsPerBlock = 1024,
    .partitions = 4,
    .partitionRegisters = 16384,
    .registerGranule = 256,
    .maxRegistersPerThread = 256,
    .maxRegistersPerBlock = 65536,
    .maxSharedMemoryPerMultiprocessor = 32768,
    .maxSharedMemoryPerBlock = 32768,
    .reservedSharedMemoryPerBlock = 0,
    .sharedMemoryGranule = 256,
    .clusterLaunch = false
  };
  if (target == "gfx700") return TargetLimits{
    .maxWarpsPerMultiprocessor = 40,
    .maxBlocksPerMultiprocessor = 16,
    .maxThreadsPerBlock = 1024,
    .partitions = 4,
    .partitionRegisters = 16384,
    .registerGranule = 256,
    .maxRegistersPerThread = 256,
    .maxRegistersPerBlock = 65536,
    .maxSharedMemoryPerMultiprocessor = 65536,
    .maxSharedMemoryPerBlock = 65536,
    .reservedSharedMemoryPerBlock = 0,
    .sharedMemoryGranule = 512,
    .clusterLaunch = false
  };
  if (target == "gfx701") return TargetLimits{
    .maxWarpsPerMultiprocessor = 40,
    .maxBlocksPerMultiprocessor = 16,
    .maxThreadsPerBlock = 1024,
    .partitions = 4,
    .partitionRegisters = 16384,
    .registerGranule = 256,
    .maxRegistersPerThread = 256,
    .maxRegistersPerBlock = 65536,
    .maxSharedMemoryPerMultiprocessor = 65536,
    .maxSharedMemoryPerBlock = 65536,
    .reservedSharedMemoryPerBlock = 0,
    .sharedMemoryGranule = 512,
    .clusterLaunch = false
  };
  if (target == "gfx702") return TargetLimits{
    .maxWarpsPerMultiprocessor = 40,
    .maxBlocksPerMultiprocessor = 16,
    .maxThreadsPerBlock = 1024,
    .partitions = 4,
    .partitionRegisters = 16384,
    .registerGranule = 256,
    .maxRegistersPerThread = 256,
    .maxRegistersPerBlock = 65536,
    .maxSharedMemoryPerMultiprocessor = 65536,
    .maxSharedMemoryPerBlock = 65536,
    .reservedSharedMemoryPerBlock = 0,
    .sharedMemoryGranule = 512,
    .clusterLaunch = false
  };
  if (target == "gfx703") return TargetLimits{
    .maxWarpsPerMultiprocessor = 40,
    .maxBlocksPerMultiprocessor = 16,
    .maxThreadsPerBlock = 1024,
    .partitions = 4,
    .partitionRegisters = 16384,
    .registerGranule = 256,
    .maxRegistersPerThread = 256,
    .maxRegistersPerBlock = 65536,
    .maxSharedMemoryPerMultiprocessor = 65536,
    .maxSharedMemoryPerBlock = 65536,
    .reservedSharedMemoryPerBlock = 0,
    .sharedMemoryGranule = 512,
    .clusterLaunch = false
  };
  if (target == "gfx704") return TargetLimits{
    .maxWarpsPerMultiprocessor = 40,
    .maxBlocksPerMultiprocessor = 16,
    .maxThreadsPerBlock = 1024,
    .partitions = 4,
    .partitionRegisters = 16384,
    .registerGranule = 256,
    .maxRegistersPerThread = 256,
    .maxRegistersPerBlock = 65536,
    .maxSharedMemoryPerMultiprocessor = 65536,
    .maxSharedMemoryPerBlock = 65536,
    .reservedSharedMemoryPerBlock = 0,
    .sharedMemoryGranule = 512,
    .clusterLaunch = false
  };
  if (target == "gfx705") return TargetLimits{
    .maxWarpsPerMultiprocessor = 40,
    .maxBlocksPerMultiprocessor = 16,
    .maxThreadsPerBlock = 1024,
    .partitions = 4,
    .partitionRegisters = 16384,
    .registerGranule = 256,
    .maxRegistersPerThread = 256,
    .maxRegistersPerBlock = 65536,
    .maxSharedMemoryPerMultiprocessor = 65536,
    .maxSharedMemoryPerBlock = 65536,
    .reservedSharedMemoryPerBlock = 0,
    .sharedMemoryGranule = 512,
    .clusterLaunch = false
  };
  if (target == "gfx801") return TargetLimits{
    .maxWarpsPerMultiprocessor = 40,
    .maxBlocksPerMultiprocessor = 16,
    .maxThreadsPerBlock = 1024,
    .partitions = 4,
    .partitionRegisters = 16384,
    .registerGranule = 256,
    .maxRegistersPerThread = 256,
    .maxRegistersPerBlock = 65536,
    .maxSharedMemoryPerMultiprocessor = 65536,
    .maxSharedMemoryPerBlock = 65536,
    .reservedSharedMemoryPerBlock = 0,
    .sharedMemoryGranule = 512,
    .clusterLaunch = false
  };
  if (target == "gfx802") return TargetLimits{
    .maxWarpsPerMultiprocessor = 40,
    .maxBlocksPerMultiprocessor = 16,
    .maxThreadsPerBlock = 1024,
    .partitions = 4,
    .partitionRegisters = 16384,
    .registerGranule = 256,
    .maxRegistersPerThread = 256,
    .maxRegistersPerBlock = 65536,
    .maxSharedMemoryPerMultiprocessor = 65536,
    .maxSharedMemoryPerBlock = 65536,
    .reservedSharedMemoryPerBlock = 0,
    .sharedMemoryGranule = 512,
    .clusterLaunch = false
  };
  if (target == "gfx803") return TargetLimits{
    .maxWarpsPerMultiprocessor = 40,
    .maxBlocksPerMultiprocessor = 16,
    .maxThreadsPerBlock = 1024,
    .partitions = 4,
    .partitionRegisters = 16384,
    .registerGranule = 256,
    .maxRegistersPerThread = 256,
    .maxRegistersPerBlock = 65536,
    .maxSharedMemoryPerMultiprocessor = 65536,
    .maxSharedMemoryPerBlock = 65536,
    .reservedSharedMemoryPerBlock = 0,
    .sharedMemoryGranule = 512,
    .clusterLaunch = false
  };
  if (target == "gfx805") return TargetLimits{
    .maxWarpsPerMultiprocessor = 40,
    .maxBlocksPerMultiprocessor = 16,
    .maxThreadsPerBlock = 1024,
    .partitions = 4,
    .partitionRegisters = 16384,
    .registerGranule = 256,
    .maxRegistersPerThread = 256,
    .maxRegistersPerBlock = 65536,
    .maxSharedMemoryPerMultiprocessor = 65536,
    .maxSharedMemoryPerBlock = 65536,
    .reservedSharedMemoryPerBlock = 0,
    .sharedMemoryGranule = 512,
    .clusterLaunch = false
  };
  if (target == "gfx810") return TargetLimits{
    .maxWarpsPerMultiprocessor = 40,
    .maxBlocksPerMultiprocessor = 16,
    .maxThreadsPerBlock = 1024,
    .partitions = 4,
    .partitionRegisters = 16384,
    .registerGranule = 256,
    .maxRegistersPerThread = 256,
    .maxRegistersPerBlock = 65536,
    .maxSharedMemoryPerMultiprocessor = 65536,
    .maxSharedMemoryPerBlock = 65536,
    .reservedSharedMemoryPerBlock = 0,
    .sharedMemoryGranule = 512,
    .clusterLaunch = false
  };
  if (target == "gfx900") return TargetLimits{
    .maxWarpsPerMultiprocessor = 40,
    .maxBlocksPerMultiprocessor = 16,
    .maxThreadsPerBlock = 1024,
    .partitions = 4,
    .partitionRegisters = 16384,
    .registerGranule = 256,
    .maxRegistersPerThread = 256,
    .maxRegistersPerBlock = 65536,
    .maxSharedMemoryPerMultiprocessor = 65536,
    .maxSharedMemoryPerBlock = 65536,
    .reservedSharedMemoryPerBlock = 0,
    .sharedMemoryGranule = 512,
    .clusterLaunch = false
  };
  if (target == "gfx902") return TargetLimits{
    .maxWarpsPerMultiprocessor = 40,
    .maxBlocksPerMultiprocessor = 16,
    .maxThreadsPerBlock = 1024,
    .partitions = 4,
    .partitionRegisters = 16384,
    .registerGranule = 256,
    .maxRegistersPerThread = 256,
    .maxRegistersPerBlock = 65536,
    .maxSharedMemoryPerMultiprocessor = 65536,
    .maxSharedMemoryPerBlock = 65536,
    .reservedSharedMemoryPerBlock = 0,
    .sharedMemoryGranule = 512,
    .clusterLaunch = false
  };
  if (target == "gfx904") return TargetLimits{
    .maxWarpsPerMultiprocessor = 40,
    .maxBlocksPerMultiprocessor = 16,
    .maxThreadsPerBlock = 1024,
    .partitions = 4,
    .partitionRegisters = 16384,
    .registerGranule = 256,
    .maxRegistersPerThread = 256,
    .maxRegistersPerBlock = 65536,
    .maxSharedMemoryPerMultiprocessor = 65536,
    .maxSharedMemoryPerBlock = 65536,
    .reservedSharedMemoryPerBlock = 0,
    .sharedMemoryGranule = 512,
    .clusterLaunch = false
  };
  if (target == "gfx906") return TargetLimits{
    .maxWarpsPerMultiprocessor = 40,
    .maxBlocksPerMultiprocessor = 16,
    .maxThreadsPerBlock = 1024,
    .partitions = 4,
    .partitionRegisters = 16384,
    .registerGranule = 256,
    .maxRegistersPerThread = 256,
    .maxRegistersPerBlock = 65536,
    .maxSharedMemoryPerMultiprocessor = 65536,
    .maxSharedMemoryPerBlock = 65536,
    .reservedSharedMemoryPerBlock = 0,
    .sharedMemoryGranule = 512,
    .clusterLaunch = false
  };
  if (target == "gfx908") return TargetLimits{
    .maxWarpsPerMultiprocessor = 40,
    .maxBlocksPerMultiprocessor = 16,
    .maxThreadsPerBlock = 1024,
    .partitions = 4,
    .partitionRegisters = 16384,
    .registerGranule = 256,
    .maxRegistersPerThread = 256,
    .maxRegistersPerBlock = 65536,
    .maxSharedMemoryPerMultiprocessor = 65536,
    .maxSharedMemoryPerBlock = 65536,
    .reservedSharedMemoryPerBlock = 0,
    .sharedMemoryGranule = 512,
    .clusterLaunch = false
  };
  if (target == "gfx909") return TargetLimits{
    .maxWarpsPerMultiprocessor = 40,
    .maxBlocksPerMultiprocessor = 16,
    .maxThreadsPerBlock = 1024,
    .partitions = 4,
    .partitionRegisters = 16384,
    .registerGranule = 256,
    .maxRegistersPerThread = 256,
    .maxRegistersPerBlock = 65536,
    .maxSharedMemoryPerMultiprocessor = 65536,
    .maxSharedMemoryPerBlock = 65536,
    .reservedSharedMemoryPerBlock = 0,
    .sharedMemoryGranule = 512,
    .clusterLaunch = false
  };
  if (target == "gfx90a") return TargetLimits{
    .maxWarpsPerMultiprocessor = 32,
    .maxBlocksPerMultiprocessor = 16,
    .maxThreadsPerBlock = 1024,
    .partitions = 4,
    .partitionRegisters = 32768,
    .registerGranule = 512,
    .maxRegistersPerThread = 512,
    .maxRegistersPerBlock = 131072,
    .maxSharedMemoryPerMultiprocessor = 65536,
    .maxSharedMemoryPerBlock = 65536,
    .reservedSharedMemoryPerBlock = 0,
    .sharedMemoryGranule = 512,
    .clusterLaunch = false
  };
  if (target == "gfx90c") return TargetLimits{
    .maxWarpsPerMultiprocessor = 40,
    .maxBlocksPerMultiprocessor = 16,
    .maxThreadsPerBlock = 1024,
    .partitions = 4,
    .partitionRegisters = 16384,
    .registerGranule = 256,
    .maxRegistersPerThread = 256,
    .maxRegistersPerBlock = 65536,
    .maxSharedMemoryPerMultiprocessor = 65536,
    .maxSharedMemoryPerBlock = 65536,
    .reservedSharedMemoryPerBlock = 0,
    .sharedMemoryGranule = 512,
    .clusterLaunch = false
  };
  if (target == "gfx940") return TargetLimits{
    .maxWarpsPerMultiprocessor = 32,
    .maxBlocksPerMultiprocessor = 16,
    .maxThreadsPerBlock = 1024,
    .partitions = 4,
    .partitionRegisters = 32768,
    .registerGranule = 512,
    .maxRegistersPerThread = 512,
    .maxRegistersPerBlock = 131072,
    .maxSharedMemoryPerMultiprocessor = 65536,
    .maxSharedMemoryPerBlock = 65536,
    .reservedSharedMemoryPerBlock = 0,
    .sharedMemoryGranule = 512,
    .clusterLaunch = false
  };
  if (target == "gfx941") return TargetLimits{
    .maxWarpsPerMultiprocessor = 32,
    .maxBlocksPerMultiprocessor = 16,
    .maxThreadsPerBlock = 1024,
    .partitions = 4,
    .partitionRegisters = 32768,
    .registerGranule = 512,
    .maxRegistersPerThread = 512,
    .maxRegistersPerBlock = 131072,
    .maxSharedMemoryPerMultiprocessor = 65536,
    .maxSharedMemoryPerBlock = 65536,
    .reservedSharedMemoryPerBlock = 0,
    .sharedMemoryGranule = 512,
    .clusterLaunch = false
  };
  if (target == "gfx942") return TargetLimits{
    .maxWarpsPerMultiprocessor = 32,
    .maxBlocksPerMultiprocessor = 16,
    .maxThreadsPerBlock = 1024,
    .partitions = 4,
    .partitionRegisters = 32768,
    .registerGranule = 512,
    .maxRegistersPerThread = 512,
    .maxRegistersPerBlock = 131072,
    .maxSharedMemoryPerMultiprocessor = 65536,
    .maxSharedMemoryPerBlock = 65536,
    .reservedSharedMemoryPerBlock = 0,
    .sharedMemoryGranule = 512,
    .clusterLaunch = false
  };
  if (target == "gfx950") return TargetLimits{
    .maxWarpsPerMultiprocessor = 32,
    .maxBlocksPerMultiprocessor = 16,
    .maxThreadsPerBlock = 1024,
    .partitions = 4,
    .partitionRegisters = 32768,
    .registerGranule = 512,
    .maxRegistersPerThread = 512,
    .maxRegistersPerBlock = 131072,
    .maxSharedMemoryPerMultiprocessor = 163840,
    .maxSharedMemoryPerBlock = 163840,
    .reservedSharedMemoryPerBlock = 0,
    .sharedMemoryGranule = 1280,
    .clusterLaunch = false
  };
  if (target == "sm_100") return TargetLimits{
    .maxWarpsPerMultiprocessor = 64,
    .maxBlocksPerMultiprocessor = 32,
    .maxThreadsPerBlock = 1024,
    .partitions = 4,
    .partitionRegisters = 16384,
    .registerGranule = 256,
    .maxRegistersPerThread = 255,
    .maxRegistersPerBlock = 65536,
    .maxSharedMemoryPerMultiprocessor = 233472,
    .maxSharedMemoryPerBlock = 232448,
    .reservedSharedMemoryPerBlock = 1024,
    .sharedMemoryGranule = 128,
    .clusterLaunch = true
  };
  if (target == "sm_101") return TargetLimits{
    .maxWarpsPerMultiprocessor = 64,
    .maxBlocksPerMultiprocessor = 24,
    .maxThreadsPerBlock = 1024,
    .partitions = 4,
    .partitionRegisters = 16384,
    .registerGranule = 256,
    .maxRegistersPerThread = 255,
    .maxRegistersPerBlock = 65536,
    .maxSharedMemoryPerMultiprocessor = 233472,
    .maxSharedMemoryPerBlock = 232448,
    .reservedSharedMemoryPerBlock = 1024,
    .sharedMemoryGranule = 128,
    .clusterLaunch = true
  };
  if (target == "sm_103") return TargetLimits{
    .maxWarpsPerMultiprocessor = 64,
    .maxBlocksPerMultiprocessor = 32,
    .maxThreadsPerBlock = 1024,
    .partitions = 4,
    .partitionRegisters = 16384,
    .registerGranule = 256,
    .maxRegistersPerThread = 255,
    .maxRegistersPerBlock = 65536,
    .maxSharedMemoryPerMultiprocessor = 233472,
    .maxSharedMemoryPerBlock = 232448,
    .reservedSharedMemoryPerBlock = 1024,
    .sharedMemoryGranule = 128,
    .clusterLaunch = true
  };
  if (target == "sm_110") return TargetLimits{
    .maxWarpsPerMultiprocessor = 64,
    .maxBlocksPerMultiprocessor = 24,
    .maxThreadsPerBlock = 1024,
    .partitions = 4,
    .partitionRegisters = 16384,
    .registerGranule = 256,
    .maxRegistersPerThread = 255,
    .maxRegistersPerBlock = 65536,
    .maxSharedMemoryPerMultiprocessor = 233472,
    .maxSharedMemoryPerBlock = 232448,
    .reservedSharedMemoryPerBlock = 1024,
    .sharedMemoryGranule = 128,
    .clusterLaunch = true
  };
  if (target == "sm_120") return TargetLimits{
    .maxWarpsPerMultiprocessor = 48,
    .maxBlocksPerMultiprocessor = 24,
    .maxThreadsPerBlock = 1024,
    .partitions = 4,
    .partitionRegisters = 16384,
    .registerGranule = 256,
    .maxRegistersPerThread = 255,
    .maxRegistersPerBlock = 65536,
    .maxSharedMemoryPerMultiprocessor = 102400,
    .maxSharedMemoryPerBlock = 101376,
    .reservedSharedMemoryPerBlock = 1024,
    .sharedMemoryGranule = 128,
    .clusterLaunch = true
  };
  if (target == "sm_121") return TargetLimits{
    .maxWarpsPerMultiprocessor = 48,
    .maxBlocksPerMultiprocessor = 24,
    .maxThreadsPerBlock = 1024,
    .partitions = 4,
    .partitionRegisters = 16384,
    .registerGranule = 256,
    .maxRegistersPerThread = 255,
    .maxRegistersPerBlock = 65536,
    .maxSharedMemoryPerMultiprocessor = 102400,
    .maxSharedMemoryPerBlock = 101376,
    .reservedSharedMemoryPerBlock = 1024,
    .sharedMemoryGranule = 128,
    .clusterLaunch = true
  };
  if (target == "sm_35") return TargetLimits{
    .maxWarpsPerMultiprocessor = 64,
    .maxBlocksPerMultiprocessor = 16,
    .maxThreadsPerBlock = 1024,
    .partitions = 4,
    .partitionRegisters = 16384,
    .registerGranule = 256,
    .maxRegistersPerThread = 255,
    .maxRegistersPerBlock = 65536,
    .maxSharedMemoryPerMultiprocessor = 49152,
    .maxSharedMemoryPerBlock = 49152,
    .reservedSharedMemoryPerBlock = 0,
    .sharedMemoryGranule = 256,
    .clusterLaunch = false
  };
  if (target == "sm_37") return TargetLimits{
    .maxWarpsPerMultiprocessor = 64,
    .maxBlocksPerMultiprocessor = 16,
    .maxThreadsPerBlock = 1024,
    .partitions = 4,
    .partitionRegisters = 32768,
    .registerGranule = 256,
    .maxRegistersPerThread = 255,
    .maxRegistersPerBlock = 65536,
    .maxSharedMemoryPerMultiprocessor = 114688,
    .maxSharedMemoryPerBlock = 49152,
    .reservedSharedMemoryPerBlock = 0,
    .sharedMemoryGranule = 256,
    .clusterLaunch = false
  };
  if (target == "sm_50") return TargetLimits{
    .maxWarpsPerMultiprocessor = 64,
    .maxBlocksPerMultiprocessor = 32,
    .maxThreadsPerBlock = 1024,
    .partitions = 4,
    .partitionRegisters = 16384,
    .registerGranule = 256,
    .maxRegistersPerThread = 255,
    .maxRegistersPerBlock = 65536,
    .maxSharedMemoryPerMultiprocessor = 65536,
    .maxSharedMemoryPerBlock = 49152,
    .reservedSharedMemoryPerBlock = 0,
    .sharedMemoryGranule = 256,
    .clusterLaunch = false
  };
  if (target == "sm_52") return TargetLimits{
    .maxWarpsPerMultiprocessor = 64,
    .maxBlocksPerMultiprocessor = 32,
    .maxThreadsPerBlock = 1024,
    .partitions = 4,
    .partitionRegisters = 16384,
    .registerGranule = 256,
    .maxRegistersPerThread = 255,
    .maxRegistersPerBlock = 65536,
    .maxSharedMemoryPerMultiprocessor = 98304,
    .maxSharedMemoryPerBlock = 49152,
    .reservedSharedMemoryPerBlock = 0,
    .sharedMemoryGranule = 256,
    .clusterLaunch = false
  };
  if (target == "sm_53") return TargetLimits{
    .maxWarpsPerMultiprocessor = 64,
    .maxBlocksPerMultiprocessor = 32,
    .maxThreadsPerBlock = 1024,
    .partitions = 4,
    .partitionRegisters = 16384,
    .registerGranule = 256,
    .maxRegistersPerThread = 255,
    .maxRegistersPerBlock = 32768,
    .maxSharedMemoryPerMultiprocessor = 65536,
    .maxSharedMemoryPerBlock = 49152,
    .reservedSharedMemoryPerBlock = 0,
    .sharedMemoryGranule = 256,
    .clusterLaunch = false
  };
  if (target == "sm_60") return TargetLimits{
    .maxWarpsPerMultiprocessor = 64,
    .maxBlocksPerMultiprocessor = 32,
    .maxThreadsPerBlock = 1024,
    .partitions = 2,
    .partitionRegisters = 32768,
    .registerGranule = 256,
    .maxRegistersPerThread = 255,
    .maxRegistersPerBlock = 65536,
    .maxSharedMemoryPerMultiprocessor = 65536,
    .maxSharedMemoryPerBlock = 49152,
    .reservedSharedMemoryPerBlock = 0,
    .sharedMemoryGranule = 256,
    .clusterLaunch = false
  };
  if (target == "sm_61") return TargetLimits{
    .maxWarpsPerMultiprocessor = 64,
    .maxBlocksPerMultiprocessor = 32,
    .maxThreadsPerBlock = 1024,
    .partitions = 4,
    .partitionRegisters = 16384,
    .registerGranule = 256,
    .maxRegistersPerThread = 255,
    .maxRegistersPerBlock = 65536,
    .maxSharedMemoryPerMultiprocessor = 98304,
    .maxSharedMemoryPerBlock = 49152,
    .reservedSharedMemoryPerBlock = 0,
    .sharedMemoryGranule = 256,
    .clusterLaunch = false
  };
  if (target == "sm_62") return TargetLimits{
    .maxWarpsPerMultiprocessor = 64,
    .maxBlocksPerMultiprocessor = 32,
    .maxThreadsPerBlock = 1024,
    .partitions = 4,
    .partitionRegisters = 16384,
    .registerGranule = 256,
    .maxRegistersPerThread = 255,
    .maxRegistersPerBlock = 32768,
    .maxSharedMemoryPerMultiprocessor = 65536,
    .maxSharedMemoryPerBlock = 49152,
    .reservedSharedMemoryPerBlock = 0,
    .sharedMemoryGranule = 256,
    .clusterLaunch = false
  };
  if (target == "sm_70") return TargetLimits{
    .maxWarpsPerMultiprocessor = 64,
    .maxBlocksPerMultiprocessor = 32,
    .maxThreadsPerBlock = 1024,
    .partitions = 4,
    .partitionRegisters = 16384,
    .registerGranule = 256,
    .maxRegistersPerThread = 255,
    .maxRegistersPerBlock = 65536,
    .maxSharedMemoryPerMultiprocessor = 98304,
    .maxSharedMemoryPerBlock = 98304,
    .reservedSharedMemoryPerBlock = 0,
    .sharedMemoryGranule = 256,
    .clusterLaunch = false
  };
  if (target == "sm_72") return TargetLimits{
    .maxWarpsPerMultiprocessor = 64,
    .maxBlocksPerMultiprocessor = 32,
    .maxThreadsPerBlock = 1024,
    .partitions = 4,
    .partitionRegisters = 16384,
    .registerGranule = 256,
    .maxRegistersPerThread = 255,
    .maxRegistersPerBlock = 65536,
    .maxSharedMemoryPerMultiprocessor = 98304,
    .maxSharedMemoryPerBlock = 98304,
    .reservedSharedMemoryPerBlock = 0,
    .sharedMemoryGranule = 256,
    .clusterLaunch = false
  };
  if (target == "sm_75") return TargetLimits{
    .maxWarpsPerMultiprocessor = 32,
    .maxBlocksPerMultiprocessor = 16,
    .maxThreadsPerBlock = 1024,
    .partitions = 4,
    .partitionRegisters = 16384,
    .registerGranule = 256,
    .maxRegistersPerThread = 255,
    .maxRegistersPerBlock = 65536,
    .maxSharedMemoryPerMultiprocessor = 65536,
    .maxSharedMemoryPerBlock = 65536,
    .reservedSharedMemoryPerBlock = 0,
    .sharedMemoryGranule = 256,
    .clusterLaunch = false
  };
  if (target == "sm_80") return TargetLimits{
    .maxWarpsPerMultiprocessor = 64,
    .maxBlocksPerMultiprocessor = 32,
    .maxThreadsPerBlock = 1024,
    .partitions = 4,
    .partitionRegisters = 16384,
    .registerGranule = 256,
    .maxRegistersPerThread = 255,
    .maxRegistersPerBlock = 65536,
    .maxSharedMemoryPerMultiprocessor = 167936,
    .maxSharedMemoryPerBlock = 166912,
    .reservedSharedMemoryPerBlock = 1024,
    .sharedMemoryGranule = 128,
    .clusterLaunch = false
  };
  if (target == "sm_86") return TargetLimits{
    .maxWarpsPerMultiprocessor = 48,
    .maxBlocksPerMultiprocessor = 16,
    .maxThreadsPerBlock = 1024,
    .partitions = 4,
    .partitionRegisters = 16384,
    .registerGranule = 256,
    .maxRegistersPerThread = 255,
    .maxRegistersPerBlock = 65536,
    .maxSharedMemoryPerMultiprocessor = 102400,
    .maxSharedMemoryPerBlock = 101376,
    .reservedSharedMemoryPerBlock = 1024,
    .sharedMemoryGranule = 128,
    .clusterLaunch = false
  };
  if (target == "sm_87") return TargetLimits{
    .maxWarpsPerMultiprocessor = 48,
    .maxBlocksPerMultiprocessor = 16,
    .maxThreadsPerBlock = 1024,
    .partitions = 4,
    .partitionRegisters = 16384,
    .registerGranule = 256,
    .maxRegistersPerThread = 255,
    .maxRegistersPerBlock = 65536,
    .maxSharedMemoryPerMultiprocessor = 167936,
    .maxSharedMemoryPerBlock = 166912,
    .reservedSharedMemoryPerBlock = 1024,
    .sharedMemoryGranule = 128,
    .clusterLaunch = false
  };
  if (target == "sm_88") return TargetLimits{
    .maxWarpsPerMultiprocessor = 48,
    .maxBlocksPerMultiprocessor = 16,
    .maxThreadsPerBlock = 1024,
    .partitions = 4,
    .partitionRegisters = 16384,
    .registerGranule = 256,
    .maxRegistersPerThread = 255,
    .maxRegistersPerBlock = 65536,
    .maxSharedMemoryPerMultiprocessor = 167936,
    .maxSharedMemoryPerBlock = 166912,
    .reservedSharedMemoryPerBlock = 1024,
    .sharedMemoryGranule = 128,
    .clusterLaunch = false
  };
  if (target == "sm_89") return TargetLimits{
    .maxWarpsPerMultiprocessor = 48,
    .maxBlocksPerMultiprocessor = 24,
    .maxThreadsPerBlock = 1024,
    .partitions = 4,
    .partitionRegisters = 16384,
    .registerGranule = 256,
    .maxRegistersPerThread = 255,
    .maxRegistersPerBlock = 65536,
    .maxSharedMemoryPerMultiprocessor = 102400,
    .maxSharedMemoryPerBlock = 101376,
    .reservedSharedMemoryPerBlock = 1024,
    .sharedMemoryGranule = 128,
    .clusterLaunch = false
  };
  if (target == "sm_90") return TargetLimits{
    .maxWarpsPerMultiprocessor = 64,
    .maxBlocksPerMultiprocessor = 32,
    .maxThreadsPerBlock = 1024,
    .partitions = 4,
    .partitionRegisters = 16384,
    .registerGranule = 256,
    .maxRegistersPerThread = 255,
    .maxRegistersPerBlock = 65536,
    .maxSharedMemoryPerMultiprocessor = 233472,
    .maxSharedMemoryPerBlock = 232448,
    .reservedSharedMemoryPerBlock = 1024,
    .sharedMemoryGranule = 128,
    .clusterLaunch = true
  };
  return std::nullopt;
}
} // namespace gemstone
