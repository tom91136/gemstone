package net.kurobako.gemstone.codegen

import java.nio.file.Path
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.profile.Architecture

/** Renders the device memory values of [[Architecture]] for the native runtimes to report. */
object GenerateTargetMemory {

  def render(architectures: Vec[Architecture]): String = {
    val rows = architectures.flatMap(architecture => architecture.memory.map(memory => (architecture, memory)))
      .sortBy(_._1.name)
      .map((architecture, memory) =>
        s"""  if (target == "${architecture.name}") return TargetMemory{${initializers(
            memory,
            architecture.identity
          )}};"""
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
         |  std::optional<int32_t> persistingL2Bytes = std::nullopt;
         |  std::optional<int32_t> persistingL2DefaultBytes = std::nullopt;
         |  std::optional<int32_t> l2FetchBytes = std::nullopt;
         |  std::optional<int32_t> architectureId = std::nullopt;
         |  std::optional<int32_t> chipImplementation = std::nullopt;
         |  std::optional<int32_t> chipRevision = std::nullopt;
         |};
         |
         |inline std::optional<TargetMemory> targetMemory(std::string_view target) noexcept {
         |${rows.mkString("\n")}
         |  return std::nullopt;
         |}
         |} // namespace gemstone
         |""".stripMargin
  }

  private def initializers(memory: Architecture.Memory, identity: Option[Architecture.Identity]): String = {
    val base    = Vec(s".busWidthBits = ${memory.busWidthBits.value}", s".l2Bytes = ${memory.l2Bytes.value}")
    val control = memory.l2Control.fold(Vec.empty[String]) { control =>
      Vec(
        s".persistingL2Bytes = ${control.persistingBytes.value}",
        s".persistingL2DefaultBytes = ${control.persistingDefaultBytes.value}",
        s".l2FetchBytes = ${control.fetchBytes.value}"
      )
    }
    val ids = identity.fold(Vec.empty[String])(id =>
      Vec(
        s".architectureId = ${id.architectureId.value}",
        s".chipImplementation = ${id.implementation.value}, .chipRevision = ${id.revision.value}"
      )
    )
    (base ++ control ++ ids).mkString(", ")
  }

  def main(arguments: Array[String]): Unit =
    GeneratedFiles.write(Path.of(arguments(0)).resolve("gemstone_target_memory.hpp"), render(Architecture.all))
}
