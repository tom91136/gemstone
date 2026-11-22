package net.kurobako.gemstone.codegen

import java.nio.file.Path
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.profile.Architecture

/** Renders the architecture limits for the native runtimes to calculate occupancy. */
object GenerateTargetLimits {

  def render(architectures: Vec[Architecture]): String = {
    val rows = architectures.sortBy(_.name).map { arch =>
      val l             = arch.limits
      val workgroupRegs = l.workgroupRegisters.fold(l.partitionRegisters.value * l.partitions.value)(_.value)
      s"""  if (target == "${arch.name}") return TargetLimits{
         |    .maxWarpsPerMultiprocessor = ${l.subgroups.value},
         |    .maxBlocksPerMultiprocessor = ${l.workgroups.value},
         |    .maxThreadsPerBlock = ${l.workgroupLanes.value},
         |    .partitions = ${l.partitions.value},
         |    .partitionRegisters = ${l.partitionRegisters.value},
         |    .registerGranule = ${l.registerGranule.value},
         |    .maxRegistersPerThread = ${l.laneRegisters.value},
         |    .maxRegistersPerBlock = $workgroupRegs,
         |    .maxSharedMemoryPerMultiprocessor = ${l.shared.value},
         |    .maxSharedMemoryPerBlock = ${l.workgroupShared.value},
         |    .reservedSharedMemoryPerBlock = ${l.reservedShared.value},
         |    .sharedMemoryGranule = ${l.sharedGranule.value},
         |    .clusterLaunch = ${l.clusterLaunch.fold(false)(_.value)}
         |  };""".stripMargin
    }
    GeneratedHeader.cpp("from the Architecture table") +
      s"""#pragma once
         |#include <cstdint>
         |#include <optional>
         |#include <string_view>
         |
         |namespace gemstone {
         |struct TargetLimits {
         |  int32_t maxWarpsPerMultiprocessor;
         |  int32_t maxBlocksPerMultiprocessor;
         |  int32_t maxThreadsPerBlock;
         |  int32_t partitions;
         |  int32_t partitionRegisters;
         |  int32_t registerGranule;
         |  int32_t maxRegistersPerThread;
         |  int32_t maxRegistersPerBlock;
         |  int32_t maxSharedMemoryPerMultiprocessor;
         |  int32_t maxSharedMemoryPerBlock;
         |  int32_t reservedSharedMemoryPerBlock;
         |  int32_t sharedMemoryGranule;
         |  bool clusterLaunch;
         |};
         |
         |inline std::optional<TargetLimits> targetLimits(std::string_view target) noexcept {
         |${rows.mkString("\n")}
         |  return std::nullopt;
         |}
         |} // namespace gemstone
         |""".stripMargin
  }

  def main(arguments: Array[String]): Unit =
    GeneratedFiles.write(Path.of(arguments(0)).resolve("gemstone_target_limits.hpp"), render(Architecture.all))
}
