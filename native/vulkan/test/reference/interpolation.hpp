// Frozen pre-migration reference, used only by differential tests.
#pragma once

#include <array>

namespace gemstone::vulkan {

// Clip-space t becomes a framebuffer-space edge parameter after division by w.
inline double clippedLinearParameter(double t, double wa, double wb) {
  const auto w = (1 - t) * wa + t * wb;
  return w == 0 ? t : t * wb / w;
}

inline double interpolationWeight(double barycentric, double reciprocalW, double interpolatedReciprocalW, bool linear) {
  return linear ? barycentric : barycentric * reciprocalW / interpolatedReciprocalW;
}

// Round once after accumulation, preserving an identical value at all vertices.
// Per-term float rounding can turn a constant 1 into the float just below 1,
// which then becomes 0 when a fragment shader converts it to an integer.
inline float interpolateTriangleValue(const std::array<float, 3> &values, const std::array<double, 3> &weights) {
  if (values[0] == values[1] && values[1] == values[2]) return values[0];
  return static_cast<float>(weights[0] * values[0] + weights[1] * values[1] + weights[2] * values[2]);
}

} // namespace gemstone::vulkan
