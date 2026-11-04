#include "gemstone/hsa/hostcall.hpp"

#include <cstdint>
#include <cstring>
#include <set>
#include <vector>

#include <catch2/catch_test_macros.hpp>

namespace {

uint64_t word(const std::vector<uint8_t> &image, size_t offset) {
  uint64_t value = 0;
  std::memcpy(&value, image.data() + offset, sizeof(value));
  return value;
}

TEST_CASE("the hostcall image points its header at its own packets and doorbell", "[hostcall]") {
  const uint64_t base = 0x7f0000000000;
  const auto layout = gemstone::hsa::hostcallLayout();
  const auto image = gemstone::hsa::hostcallImage(base);
  REQUIRE(image.size() == layout.bytes);
  const auto headers = word(image, 0) - base;
  const auto payloads = word(image, 8) - base;
  const auto signal = word(image, 16) - base;
  REQUIRE(headers + (24 * layout.packets) <= payloads);
  REQUIRE(payloads + (4096 * layout.packets) <= signal);
  REQUIRE(word(image, 40) == layout.packets - 1);
  REQUIRE(word(image, 32) == 0);
  REQUIRE(word(image, signal) == 1);
  const auto mailbox = word(image, signal + 16);
  REQUIRE(mailbox != 0);
  REQUIRE(mailbox - base + 8 <= layout.bytes);
}

TEST_CASE("the free stack visits every packet once and ends at packet zero", "[hostcall]") {
  const uint64_t base = 0x7f0000000000;
  const auto layout = gemstone::hsa::hostcallLayout();
  const auto image = gemstone::hsa::hostcallImage(base);
  const auto headers = word(image, 0) - base;
  const uint64_t mask = word(image, 40);
  std::set<uint64_t> seen;
  uint64_t top = word(image, 24);
  while (top != 0) {
    REQUIRE(seen.insert(top & mask).second);
    top = word(image, headers + ((top & mask) * 24));
  }
  REQUIRE(seen.size() == layout.packets);
}

} // namespace
