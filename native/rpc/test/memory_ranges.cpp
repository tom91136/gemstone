#include <cstdint>
#include <limits>

#include <catch2/catch_test_macros.hpp>

#include <gemstone/memory.hpp>

TEST_CASE("Memory registrations retain independent overlapping leases", "[memory]") {
  gemstone::MemoryRegistrations views;
  using gemstone::MemoryOwnership;
  REQUIRE(views.add({0x1000, 64}, MemoryOwnership::Borrowed, 3));
  REQUIRE(views.add({0x1010, 64}, MemoryOwnership::Borrowed, 7, true));
  REQUIRE(views.add({0x1010, 64}, MemoryOwnership::Borrowed, 7, true));
  CHECK_FALSE(views.remove(0x1000, MemoryOwnership::Owned));
  REQUIRE(views.remove(0x1000, MemoryOwnership::Borrowed));
  CHECK(views.find(0x1000) == nullptr);
  REQUIRE(views.find(0x104f));
  CHECK(views.find(0x104f)->flags == 7);
  REQUIRE(views.remove(0x1010, MemoryOwnership::Borrowed));
  CHECK(views.find(0x1010) != nullptr);
  REQUIRE(views.remove(0x1010, MemoryOwnership::Borrowed));
  CHECK(views.find(0x1010) == nullptr);
  CHECK(views.add({0x1000, 64}, MemoryOwnership::Owned, 0));
}

TEST_CASE("Memory range discovery preserves holes and excludes overflow", "[memory]") {
  auto missing = gemstone::missingMemoryRanges({.base = 0x1000, .size = 64},
                                               {{.base = 0x1008, .size = 8}, {.base = 0x1020, .size = 16}, {.base = 0x1028, .size = 16}});
  REQUIRE(missing.size() == 3);
  CHECK(missing[0].base == 0x1000);
  CHECK(missing[0].size == 8);
  CHECK(missing[1].base == 0x1010);
  CHECK(missing[1].size == 16);
  CHECK(missing[2].base == 0x1038);
  CHECK(missing[2].size == 8);
  gemstone::MemoryRegistrations views;
  CHECK_FALSE(views.add({std::numeric_limits<uintptr_t>::max() - 3, 8}, gemstone::MemoryOwnership::Borrowed, 0));
  CHECK_FALSE(views.add({0x1000, 0}, gemstone::MemoryOwnership::Borrowed, 0));
  CHECK_FALSE((gemstone::MemoryRange{0x1000, 8}.contains(0x1007, 2)));
  CHECK((gemstone::MemoryRange{0x1000, 8}.contains(0x1008, 0)));
}

TEST_CASE("Registration release selects the matching extent at a shared base", "[memory]") {
  gemstone::MemoryRegistrations views;
  using gemstone::MemoryOwnership;
  REQUIRE(views.add({0x1000, 64}, MemoryOwnership::Borrowed, 0));
  REQUIRE(views.add({0x1000, 16}, MemoryOwnership::Borrowed, 0, true));
  REQUIRE(views.remove(0x1000, MemoryOwnership::Borrowed, 64));
  REQUIRE(views.exact(0x1000));
  CHECK(views.exact(0x1000)->range.size == 16);
  CHECK_FALSE(views.find(0x1020));
  CHECK_FALSE(views.remove(0x1000, MemoryOwnership::Borrowed, 64));
  CHECK(views.remove(0x1000, MemoryOwnership::Borrowed));
}

TEST_CASE("Live transfers exclude released prefixes and preserve registration holes", "[memory]") {
  const auto ranges = gemstone::coveredMemoryRanges(
      {.base = 0x1000, .size = 64}, {{.base = 0x1008, .size = 8}, {.base = 0x1020, .size = 16}, {.base = 0x1028, .size = 32}});
  REQUIRE(ranges.size() == 2);
  CHECK(ranges[0].base == 0x1008);
  CHECK(ranges[0].size == 8);
  CHECK(ranges[1].base == 0x1020);
  CHECK(ranges[1].size == 32);
}
