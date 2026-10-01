#pragma once

#include <cstdint>
#include <vector>

namespace imgui_scene {
enum class Scene { primitives, layout, resize };
struct Image {
  uint32_t width, height;
  std::vector<uint8_t> rgb;
};
Image render(Scene scene, bool reference);
} // namespace imgui_scene
