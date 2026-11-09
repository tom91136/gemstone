package net.kurobako.gemstone.codegen

import java.nio.file.Path
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.profile.Architecture

/** Renders the device memory values of [[Architecture]] for the native runtimes to report. */
object GenerateTargetMemory {

  def render(architectures: Vec[Architecture]): String = {
    val rows = architectures.flatMap(architecture =>
      architecture.memory.map(memory => (architecture.name, memory.busWidthBits.value, memory.l2Bytes.value))
    ).sortBy(_._1).map((name, bus, l2) =>
      s"""  if (target == "$name") return TargetMemory{.busWidthBits = $bus, .l2Bytes = $l2};"""
    )
    GeneratedHeader.cpp("from the Architecture table") +
      s"""#pragma once
         |#include <cstdint>
         |#include <optional>
         |#include <string_view>
         |
         |namespace gemstone {
         |struct TargetMemory {
         |  int32_t busWidthBits;
         |  int32_t l2Bytes;
         |};
         |
         |inline std::optional<TargetMemory> targetMemory(std::string_view target) noexcept {
         |${rows.mkString("\n")}
         |  return std::nullopt;
         |}
         |} // namespace gemstone
         |""".stripMargin
  }

  def main(arguments: Array[String]): Unit =
    GeneratedFiles.write(Path.of(arguments(0)).resolve("gemstone_target_memory.hpp"), render(Architecture.all))
}
