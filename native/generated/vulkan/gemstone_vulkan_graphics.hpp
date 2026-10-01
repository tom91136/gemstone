/* Generated from GenerateVulkanAdapter. Regenerate with: sbt codegen */
/* Generated from GraphicsProtocol enums. Regenerate with: sbt codegen */
#pragma once
#include <stdexcept>
#include <vulkan/vulkan.h>
#include "../rpc/gemstone_execution_rpc.hpp"
namespace gemstone::vulkan::adapter {
inline execution::GraphicsCompare graphicsCompare(VkCompareOp value) {
  switch (value) {
    case VK_COMPARE_OP_NEVER: return execution::GraphicsCompare::Never;
    case VK_COMPARE_OP_LESS: return execution::GraphicsCompare::Less;
    case VK_COMPARE_OP_EQUAL: return execution::GraphicsCompare::Equal;
    case VK_COMPARE_OP_LESS_OR_EQUAL: return execution::GraphicsCompare::LessOrEqual;
    case VK_COMPARE_OP_GREATER: return execution::GraphicsCompare::Greater;
    case VK_COMPARE_OP_NOT_EQUAL: return execution::GraphicsCompare::NotEqual;
    case VK_COMPARE_OP_GREATER_OR_EQUAL: return execution::GraphicsCompare::GreaterOrEqual;
    case VK_COMPARE_OP_ALWAYS: return execution::GraphicsCompare::Always;
    default: throw std::invalid_argument("unsupported GraphicsCompare");
  }
}
inline execution::GraphicsStencilOp graphicsStencilOp(VkStencilOp value) {
  switch (value) {
    case VK_STENCIL_OP_KEEP: return execution::GraphicsStencilOp::Keep;
    case VK_STENCIL_OP_ZERO: return execution::GraphicsStencilOp::Zero;
    case VK_STENCIL_OP_REPLACE: return execution::GraphicsStencilOp::Replace;
    case VK_STENCIL_OP_INCREMENT_AND_CLAMP: return execution::GraphicsStencilOp::IncrementAndClamp;
    case VK_STENCIL_OP_DECREMENT_AND_CLAMP: return execution::GraphicsStencilOp::DecrementAndClamp;
    case VK_STENCIL_OP_INVERT: return execution::GraphicsStencilOp::Invert;
    case VK_STENCIL_OP_INCREMENT_AND_WRAP: return execution::GraphicsStencilOp::IncrementAndWrap;
    case VK_STENCIL_OP_DECREMENT_AND_WRAP: return execution::GraphicsStencilOp::DecrementAndWrap;
    default: throw std::invalid_argument("unsupported GraphicsStencilOp");
  }
}
inline execution::GraphicsBlendOp graphicsBlendOp(VkBlendOp value) {
  switch (value) {
    case VK_BLEND_OP_ADD: return execution::GraphicsBlendOp::Add;
    case VK_BLEND_OP_SUBTRACT: return execution::GraphicsBlendOp::Subtract;
    case VK_BLEND_OP_REVERSE_SUBTRACT: return execution::GraphicsBlendOp::ReverseSubtract;
    case VK_BLEND_OP_MIN: return execution::GraphicsBlendOp::Min;
    case VK_BLEND_OP_MAX: return execution::GraphicsBlendOp::Max;
    default: throw std::invalid_argument("unsupported GraphicsBlendOp");
  }
}
inline execution::GraphicsBlendFactor graphicsBlendFactor(VkBlendFactor value) {
  switch (value) {
    case VK_BLEND_FACTOR_ZERO: return execution::GraphicsBlendFactor::Zero;
    case VK_BLEND_FACTOR_ONE: return execution::GraphicsBlendFactor::One;
    case VK_BLEND_FACTOR_SRC_COLOR: return execution::GraphicsBlendFactor::SrcColor;
    case VK_BLEND_FACTOR_ONE_MINUS_SRC_COLOR: return execution::GraphicsBlendFactor::OneMinusSrcColor;
    case VK_BLEND_FACTOR_DST_COLOR: return execution::GraphicsBlendFactor::DstColor;
    case VK_BLEND_FACTOR_ONE_MINUS_DST_COLOR: return execution::GraphicsBlendFactor::OneMinusDstColor;
    case VK_BLEND_FACTOR_SRC_ALPHA: return execution::GraphicsBlendFactor::SrcAlpha;
    case VK_BLEND_FACTOR_ONE_MINUS_SRC_ALPHA: return execution::GraphicsBlendFactor::OneMinusSrcAlpha;
    case VK_BLEND_FACTOR_DST_ALPHA: return execution::GraphicsBlendFactor::DstAlpha;
    case VK_BLEND_FACTOR_ONE_MINUS_DST_ALPHA: return execution::GraphicsBlendFactor::OneMinusDstAlpha;
    case VK_BLEND_FACTOR_CONSTANT_COLOR: return execution::GraphicsBlendFactor::ConstantColor;
    case VK_BLEND_FACTOR_ONE_MINUS_CONSTANT_COLOR: return execution::GraphicsBlendFactor::OneMinusConstantColor;
    case VK_BLEND_FACTOR_CONSTANT_ALPHA: return execution::GraphicsBlendFactor::ConstantAlpha;
    case VK_BLEND_FACTOR_ONE_MINUS_CONSTANT_ALPHA: return execution::GraphicsBlendFactor::OneMinusConstantAlpha;
    case VK_BLEND_FACTOR_SRC_ALPHA_SATURATE: return execution::GraphicsBlendFactor::SrcAlphaSaturate;
    default: throw std::invalid_argument("unsupported GraphicsBlendFactor");
  }
}
inline execution::GraphicsTopology graphicsTopology(VkPrimitiveTopology value) {
  switch (value) {
    case VK_PRIMITIVE_TOPOLOGY_POINT_LIST: return execution::GraphicsTopology::PointList;
    case VK_PRIMITIVE_TOPOLOGY_LINE_LIST: return execution::GraphicsTopology::LineList;
    case VK_PRIMITIVE_TOPOLOGY_LINE_STRIP: return execution::GraphicsTopology::LineStrip;
    case VK_PRIMITIVE_TOPOLOGY_TRIANGLE_LIST: return execution::GraphicsTopology::TriangleList;
    case VK_PRIMITIVE_TOPOLOGY_TRIANGLE_STRIP: return execution::GraphicsTopology::TriangleStrip;
    case VK_PRIMITIVE_TOPOLOGY_TRIANGLE_FAN: return execution::GraphicsTopology::TriangleFan;
    default: throw std::invalid_argument("unsupported GraphicsTopology");
  }
}
inline execution::GraphicsFilter graphicsFilter(VkFilter value) {
  switch (value) {
    case VK_FILTER_NEAREST: return execution::GraphicsFilter::Nearest;
    case VK_FILTER_LINEAR: return execution::GraphicsFilter::Linear;
    default: throw std::invalid_argument("unsupported GraphicsFilter");
  }
}
inline execution::GraphicsAddressMode graphicsAddressMode(VkSamplerAddressMode value) {
  switch (value) {
    case VK_SAMPLER_ADDRESS_MODE_REPEAT: return execution::GraphicsAddressMode::Repeat;
    case VK_SAMPLER_ADDRESS_MODE_MIRRORED_REPEAT: return execution::GraphicsAddressMode::MirroredRepeat;
    case VK_SAMPLER_ADDRESS_MODE_CLAMP_TO_EDGE: return execution::GraphicsAddressMode::ClampToEdge;
    case VK_SAMPLER_ADDRESS_MODE_CLAMP_TO_BORDER: return execution::GraphicsAddressMode::ClampToBorder;
    case VK_SAMPLER_ADDRESS_MODE_MIRROR_CLAMP_TO_EDGE: return execution::GraphicsAddressMode::MirrorClampToEdge;
    default: throw std::invalid_argument("unsupported GraphicsAddressMode");
  }
}
inline execution::GraphicsMipmapMode graphicsMipmapMode(VkSamplerMipmapMode value) {
  switch (value) {
    case VK_SAMPLER_MIPMAP_MODE_NEAREST: return execution::GraphicsMipmapMode::Nearest;
    case VK_SAMPLER_MIPMAP_MODE_LINEAR: return execution::GraphicsMipmapMode::Linear;
    default: throw std::invalid_argument("unsupported GraphicsMipmapMode");
  }
}
inline execution::GraphicsBorderColor graphicsBorderColor(VkBorderColor value) {
  switch (value) {
    case VK_BORDER_COLOR_FLOAT_TRANSPARENT_BLACK: return execution::GraphicsBorderColor::FloatTransparentBlack;
    case VK_BORDER_COLOR_INT_TRANSPARENT_BLACK: return execution::GraphicsBorderColor::IntTransparentBlack;
    case VK_BORDER_COLOR_FLOAT_OPAQUE_BLACK: return execution::GraphicsBorderColor::FloatOpaqueBlack;
    case VK_BORDER_COLOR_INT_OPAQUE_BLACK: return execution::GraphicsBorderColor::IntOpaqueBlack;
    case VK_BORDER_COLOR_FLOAT_OPAQUE_WHITE: return execution::GraphicsBorderColor::FloatOpaqueWhite;
    case VK_BORDER_COLOR_INT_OPAQUE_WHITE: return execution::GraphicsBorderColor::IntOpaqueWhite;
    default: throw std::invalid_argument("unsupported GraphicsBorderColor");
  }
}
inline execution::GraphicsSwizzle graphicsSwizzle(VkComponentSwizzle value) {
  switch (value) {
    case VK_COMPONENT_SWIZZLE_IDENTITY: return execution::GraphicsSwizzle::Identity;
    case VK_COMPONENT_SWIZZLE_ZERO: return execution::GraphicsSwizzle::Zero;
    case VK_COMPONENT_SWIZZLE_ONE: return execution::GraphicsSwizzle::One;
    case VK_COMPONENT_SWIZZLE_R: return execution::GraphicsSwizzle::R;
    case VK_COMPONENT_SWIZZLE_G: return execution::GraphicsSwizzle::G;
    case VK_COMPONENT_SWIZZLE_B: return execution::GraphicsSwizzle::B;
    case VK_COMPONENT_SWIZZLE_A: return execution::GraphicsSwizzle::A;
    default: throw std::invalid_argument("unsupported GraphicsSwizzle");
  }
}
}
