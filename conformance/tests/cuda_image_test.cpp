#include <array>
#include <cstdlib>
#include <optional>
#include <string>
#include <string_view>
#include <vector>

#include <catch2/catch_test_macros.hpp>

#define UNIFIED_API_CUDA 1
#include "../unified/src/memory_files.hpp"

namespace {
struct CodeMode {
  std::optional<std::string> previous;
  CodeMode() {
    if (const auto *value = std::getenv("CONFORMANCE_CUDA_CODE")) previous = value;
  }
  static int set(const char *value) { return value ? setenv("CONFORMANCE_CUDA_CODE", value, 1) : unsetenv("CONFORMANCE_CUDA_CODE"); }
  ~CodeMode() { set(previous ? previous->c_str() : nullptr); }
};
} // namespace

TEST_CASE("CUDA embedded images default to PTX and honour explicit code modes", "[cuda-images]") {
  CodeMode restore;
  for (const char *mode : std::array<const char *, 3>{nullptr, "ptx", "sass"}) {
    CAPTURE(mode ? mode : "unset");
    REQUIRE(CodeMode::set(mode) == 0);
    const bool sass = mode && std::string_view(mode) == "sass";
    const auto expected = [sass](const auto &ptx, const auto &binary) {
      const auto &selected = sass ? binary : ptx;
      std::vector<char> bytes(selected.begin(), selected.end());
      bytes.push_back(0);
      return bytes;
    };
    CHECK(image("mma884.ptx") == expected(mma884_ptx_image, mma884_sass_image));
    CHECK(image("kernel.ptx") == expected(memory_image, kernel_sass_image));
    CHECK(image("exchange.ptx") == expected(exchange_image, exchange_sass_image));
    CHECK(image("atomic.ptx") == expected(atomic_image, atomic_sass_image));
    CHECK(image("fence.ptx") == expected(fence_image, fence_sass_image));
    CHECK(image("contended.ptx") == expected(contended_image, contended_sass_image));
  }
}
