// Frozen pre-migration reference, used only by differential tests.
#pragma once

#include <algorithm>
#include <array>
#include <cmath>
#include <optional>

namespace gemstone::vulkan {
// Clip the centerline in homogeneous coordinates before expanding its width.
inline std::optional<std::array<double, 2>> clipLineInterval(const std::array<float, 4> &a, const std::array<float, 4> &b) {
  const auto distances = [](const auto &p) {
    return std::array<double, 6>{double(p[3]) + p[0], double(p[3]) - p[0], double(p[3]) + p[1],
                                 double(p[3]) - p[1], double(p[2]),        double(p[3]) - p[2]};
  };
  const auto da = distances(a), db = distances(b);
  double first = 0, last = 1;
  for (size_t plane = 0; plane < da.size(); ++plane) {
    if (da[plane] < 0 && db[plane] < 0) return std::nullopt;
    if (da[plane] < 0) first = std::max(first, da[plane] / (da[plane] - db[plane]));
    if (db[plane] < 0) last = std::min(last, da[plane] / (da[plane] - db[plane]));
  }
  if (first > last) return std::nullopt;
  return std::array<double, 2>{first, last};
}

// Default, non-strict width-one lines use a parallelogram with caps along the
// minor axis. Coverage follows polygon top-left rules; interpolation uses the
// major axis, as on the equivalent parallelogram with duplicated end attributes.
struct LineFootprint {
  using Point = std::array<double, 2>;
  std::array<Point, 4> corners;
  Point a, b;
  size_t major;

  static double edge(const Point &a, const Point &b, const Point &p) {
    return (b[0] - a[0]) * (p[1] - a[1]) - (b[1] - a[1]) * (p[0] - a[0]);
  }
  static std::optional<LineFootprint> make(Point a, Point b) {
    for (size_t axis = 0; axis < 2; ++axis) {
      if (!std::isfinite(a[axis]) || !std::isfinite(b[axis])) return std::nullopt;
      a[axis] = std::round(a[axis] * 256) / 256;
      b[axis] = std::round(b[axis] * 256) / 256;
    }
    if (a == b) return std::nullopt;
    const size_t major = std::abs(b[0] - a[0]) >= std::abs(b[1] - a[1]) ? 0 : 1;
    LineFootprint result{{a, b, b, a}, a, b, major};
    result.corners[0][1 - major] -= .5;
    result.corners[1][1 - major] -= .5;
    result.corners[2][1 - major] += .5;
    result.corners[3][1 - major] += .5;
    if (edge(result.corners[0], result.corners[1], result.corners[2]) < 0) std::swap(result.corners[1], result.corners[3]);
    return result;
  }
  bool covers(Point p) const {
    for (size_t i = 0; i < corners.size(); ++i) {
      const auto &a = corners[i], &b = corners[(i + 1) % corners.size()];
      const auto e = edge(a, b, p);
      const bool topLeft = b[1] < a[1] || (b[1] == a[1] && b[0] > a[0]);
      if (e < 0 || (e == 0 && !topLeft)) return false;
    }
    return true;
  }
  double parameter(Point p) const { return (p[major] - a[major]) / (b[major] - a[major]); }
};
} // namespace gemstone::vulkan
