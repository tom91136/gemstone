package net.kurobako.gemstone.codegen

import net.kurobako.gemstone.graphics.GraphicsProtocol.*
import net.kurobako.gemstone.string.*

/** Vulkan translations for the supported execution enums; values come from the shared Scala types. */
object GraphicsEnums {
  private case class Mapping(name: String, vulkan: String, prefix: String, values: Vector[AnyRef]) {
    def cases: Vector[String] = values.map(_.toString)
  }
  private val mappings = Vector(
    Mapping("GraphicsCompare", "VkCompareOp", "VK_COMPARE_OP_", GraphicsCompare.values.toVector),
    Mapping("GraphicsStencilOp", "VkStencilOp", "VK_STENCIL_OP_", GraphicsStencilOp.values.toVector),
    Mapping("GraphicsBlendOp", "VkBlendOp", "VK_BLEND_OP_", GraphicsBlendOp.values.toVector),
    Mapping("GraphicsBlendFactor", "VkBlendFactor", "VK_BLEND_FACTOR_", GraphicsBlendFactor.values.toVector),
    Mapping("GraphicsTopology", "VkPrimitiveTopology", "VK_PRIMITIVE_TOPOLOGY_", GraphicsTopology.values.toVector),
    Mapping("GraphicsFilter", "VkFilter", "VK_FILTER_", GraphicsFilter.values.toVector),
    Mapping(
      "GraphicsAddressMode",
      "VkSamplerAddressMode",
      "VK_SAMPLER_ADDRESS_MODE_",
      GraphicsAddressMode.values.toVector
    ),
    Mapping("GraphicsMipmapMode", "VkSamplerMipmapMode", "VK_SAMPLER_MIPMAP_MODE_", GraphicsMipmapMode.values.toVector),
    Mapping("GraphicsBorderColour", "VkBorderColor", "VK_BORDER_COLOR_", GraphicsBorderColour.values.toVector),
    Mapping("GraphicsSwizzle", "VkComponentSwizzle", "VK_COMPONENT_SWIZZLE_", GraphicsSwizzle.values.toVector)
  )
  val descriptorGroups: Vector[(String, String, Vector[(String, Long)])] = mappings.filter(mapping =>
    Set(
      "GraphicsCompare",
      "GraphicsFilter",
      "GraphicsAddressMode",
      "GraphicsMipmapMode",
      "GraphicsBorderColour",
      "GraphicsSwizzle"
    ).contains(mapping.name)
  ).map { mapping =>
    val name = if (mapping.name == "GraphicsCompare") "CompareOp" else mapping.name.stripPrefix("Graphics")
    (name, "Int", mapping.cases.zipWithIndex.map((name, ordinal) => name -> ordinal.toLong))
  }
  def nativeSource: String = {
    val functions = mappings.map { mapping =>
      val cases = mapping.cases.map { name =>
        val symbol = mapping.prefix + name.replaceAll("([a-z])([A-Z])", "$1_$2").toUpperCaseRoot
        s"    case $symbol: return execution::${mapping.name}::$name;"
      }.mkString("\n")
      val method = "graphics" + mapping.name.stripPrefix("Graphics")
      s"""inline execution::${mapping.name} $method(${mapping.vulkan} value) {
         |  switch (value) {
         |$cases
         |    default: throw std::invalid_argument("unsupported ${mapping.name}");
         |  }
         |}""".stripMargin
    }.mkString("\n")
    s"#pragma once\n#include <stdexcept>\n#include <vulkan/vulkan.h>\n#include \"../rpc/gemstone_execution_rpc.hpp\"\nnamespace gemstone::vulkan {\n$functions\n}\n"
  }
}
