#include <algorithm>
#include <cmath>
#include <filesystem>
#include <fstream>
#include <iostream>
#include <stdexcept>
#include <string>

#include "imgui_scene.hpp"

#ifndef IMGUI_REFERENCE_GENERATOR
  #include <catch2/catch_test_macros.hpp>
#endif

namespace {
using imgui_scene::Image;
using imgui_scene::Scene;

void write(const std::filesystem::path &path, const Image &image) {
  std::ofstream stream(path, std::ios::binary);
  stream.exceptions(std::ios::badbit | std::ios::failbit);
  stream << "P6\n" << image.width << ' ' << image.height << "\n255\n";
  stream.write(reinterpret_cast<const char *>(image.rgb.data()), static_cast<std::streamsize>(image.rgb.size()));
}

#ifndef IMGUI_REFERENCE_GENERATOR
Image read(const std::filesystem::path &path) {
  std::ifstream stream(path, std::ios::binary);
  stream.exceptions(std::ios::badbit | std::ios::failbit);
  std::string format;
  uint32_t width, height, maximum;
  stream >> format >> width >> height >> maximum;
  if (format != "P6" || maximum != 255 || width == 0 || width > 1024 || height == 0 || height > 1024 || stream.get() != '\n')
    throw std::runtime_error("invalid ImGui reference image: " + path.string());
  Image image{width, height, std::vector<uint8_t>(size_t(width) * height * 3)};
  stream.read(reinterpret_cast<char *>(image.rgb.data()), static_cast<std::streamsize>(image.rgb.size()));
  return image;
}

void check(Scene scene, const std::string &name) {
  const auto root = std::filesystem::canonical("/proc/self/exe").parent_path();
  const auto expected = read(root / "imgui" / (name + ".ppm"));
  const auto actual = imgui_scene::render(scene, false);
  REQUIRE(actual.width == expected.width);
  REQUIRE(actual.height == expected.height);
  Image difference{actual.width, actual.height, std::vector<uint8_t>(actual.rgb.size())};
  size_t mismatches = 0;
  for (size_t pixel = 0; pixel < actual.rgb.size() / 3; ++pixel) {
    int maximum = 0;
    for (size_t channel = 0; channel < 3; ++channel) {
      const auto index = pixel * 3 + channel;
      const int delta = std::abs(int(actual.rgb[index]) - int(expected.rgb[index]));
      maximum = std::max(maximum, delta);
      difference.rgb[index] = static_cast<uint8_t>(std::min(255, delta * 8));
    }
    // Allow small interpolation/blending differences and a bounded number of other pixels.
    if (maximum > 3) ++mismatches;
  }
  const size_t edgeBudget = size_t(actual.width) * actual.height / 500;
  INFO("mismatched pixels: " << mismatches << ", edge budget: " << edgeBudget);
  if (mismatches > edgeBudget) {
    write(name + "-actual.ppm", actual);
    write(name + "-reference.ppm", expected);
    write(name + "-difference.ppm", difference);
    WARN("ImGui pixel mismatch: " << std::filesystem::current_path() / (name + "-difference.ppm"));
  }
  REQUIRE(mismatches <= edgeBudget);
}
#endif
} // namespace

#ifdef IMGUI_REFERENCE_GENERATOR
int main(int argc, char **argv) {
  try {
    if (argc != 2) throw std::runtime_error("usage: imgui_reference <output-directory>");
    const std::filesystem::path directory(argv[1]);
    std::filesystem::create_directories(directory);
    write(directory / "primitives.ppm", imgui_scene::render(Scene::Primitives, true));
    write(directory / "layout.ppm", imgui_scene::render(Scene::Layout, true));
    write(directory / "resize.ppm", imgui_scene::render(Scene::Resize, true));
    return 0;
  } catch (const std::exception &error) {
    std::cerr << error.what() << '\n';
    return 1;
  }
}
#else

TEST_CASE("miniapps/imgui/vulkan/primitives", "[gpu-conformance][miniapps][vulkan][requires-execution]") {
  check(Scene::Primitives, "primitives");
}

TEST_CASE("miniapps/imgui/vulkan/layout", "[gpu-conformance][miniapps][vulkan][requires-execution][regression]") {
  check(Scene::Layout, "layout");
}

TEST_CASE("miniapps/imgui/vulkan/resize", "[gpu-conformance][miniapps][vulkan][requires-execution][regression]") {
  check(Scene::Resize, "resize");
}
#endif
