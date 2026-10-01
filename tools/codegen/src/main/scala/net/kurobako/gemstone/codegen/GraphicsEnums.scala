package net.kurobako.gemstone.codegen

import net.kurobako.gemstone.graphics.GraphicsProtocol.*
import net.kurobako.gemstone.string.*

/** Vulkan translations for the supported execution enums; values come from the shared Scala types. */
object GraphicsEnums {
  private case class Mapping(name: String, vulkan: String, prefix: String, cases: Vector[String])
  private val mappings = Vector(
    Mapping("GraphicsCompare", "VkCompareOp", "VK_COMPARE_OP_", GraphicsCompare.values.toVector.map(_.toString)),
    Mapping("GraphicsStencilOp", "VkStencilOp", "VK_STENCIL_OP_", GraphicsStencilOp.values.toVector.map(_.toString)),
    Mapping("GraphicsBlendOp", "VkBlendOp", "VK_BLEND_OP_", GraphicsBlendOp.values.toVector.map(_.toString)),
    Mapping(
      "GraphicsBlendFactor",
      "VkBlendFactor",
      "VK_BLEND_FACTOR_",
      GraphicsBlendFactor.values.toVector.map(_.toString)
    ),
    Mapping(
      "GraphicsTopology",
      "VkPrimitiveTopology",
      "VK_PRIMITIVE_TOPOLOGY_",
      GraphicsTopology.values.toVector.map(_.toString)
    ),
    Mapping("GraphicsFilter", "VkFilter", "VK_FILTER_", GraphicsFilter.values.toVector.map(_.toString)),
    Mapping(
      "GraphicsAddressMode",
      "VkSamplerAddressMode",
      "VK_SAMPLER_ADDRESS_MODE_",
      GraphicsAddressMode.values.toVector.map(_.toString)
    ),
    Mapping(
      "GraphicsMipmapMode",
      "VkSamplerMipmapMode",
      "VK_SAMPLER_MIPMAP_MODE_",
      GraphicsMipmapMode.values.toVector.map(_.toString)
    ),
    Mapping(
      "GraphicsBorderColor",
      "VkBorderColor",
      "VK_BORDER_COLOR_",
      GraphicsBorderColor.values.toVector.map(_.toString)
    ),
    Mapping(
      "GraphicsSwizzle",
      "VkComponentSwizzle",
      "VK_COMPONENT_SWIZZLE_",
      GraphicsSwizzle.values.toVector.map(_.toString)
    )
  )
  val descriptorGroups: Vector[(String, String, Vector[(String, Long)])] = mappings.filter(mapping =>
    Set(
      "GraphicsCompare",
      "GraphicsFilter",
      "GraphicsAddressMode",
      "GraphicsMipmapMode",
      "GraphicsBorderColor",
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
    GeneratedHeader.cpp("from GraphicsProtocol enums") +
      s"#pragma once\n#include <stdexcept>\n#include <vulkan/vulkan.h>\n#include \"../rpc/gemstone_execution_rpc.hpp\"\nnamespace gemstone::vulkan::adapter {\n$functions\n}\n"
  }
}
