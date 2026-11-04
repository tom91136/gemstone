#include <algorithm>
#include <vector>

#include "support.hpp"

namespace unified::openacc {

TEMPLATE_TEST_CASE("openacc/atomic/update", "[unified][openacc][atomic]", int, float, double) {
  TestType counter = 0;
#pragma acc parallel loop copy(counter)
  for (int i = 0; i < Count; ++i) {
#pragma acc atomic update
    counter += TestType{1};
  }
  REQUIRE(counter == static_cast<TestType>(Count));
}

TEST_CASE("openacc/atomic/update-increment", "[unified][openacc][atomic]") {
  int counter = 5;
#pragma acc parallel loop copy(counter)
  for (int i = 0; i < Count; ++i) {
#pragma acc atomic update
    counter++;
  }
  REQUIRE(counter == 5 + Count);
}

TEST_CASE("openacc/atomic/update-histogram", "[unified][openacc][atomic]") {
  constexpr int binCount = 8;
  std::vector<int> bins(binCount, 0);
  int *p = bins.data();
#pragma acc parallel loop copy(p[0 : binCount])
  for (int i = 0; i < Count; ++i) {
    const int bin = (i * 3) % binCount;
#pragma acc atomic update
    p[bin] += i;
  }
  std::vector<int> expected(binCount, 0);
  for (int i = 0; i < Count; ++i)
    expected[sz((i * 3) % binCount)] += i;
  REQUIRE(bins == expected);
}

TEST_CASE("openacc/atomic/update-bitwise", "[unified][openacc][atomic]") {
  unsigned mask = 0;
  unsigned all = 0xffffffffU;
#pragma acc parallel loop copy(mask, all)
  for (int i = 0; i < 32; ++i) {
#pragma acc atomic update
    mask |= 1U << i;
#pragma acc atomic update
    all &= ~(1U << i);
  }
  REQUIRE(mask == 0xffffffffU);
  REQUIRE(all == 0U);
}

TEMPLATE_TEST_CASE("openacc/atomic/write", "[unified][openacc][atomic]", int, float, double) {
  TestType cell = 0;
#pragma acc parallel loop copy(cell)
  for (int i = 0; i < Count; ++i) {
#pragma acc atomic write
    cell = TestType{42};
  }
  REQUIRE(cell == TestType{42});
}

TEMPLATE_TEST_CASE("openacc/atomic/read", "[unified][openacc][atomic]", int, float, double) {
  TestType cell = TestType{7};
  std::vector<TestType> out(Count, TestType{0});
  TestType *po = out.data();
#pragma acc parallel loop copyin(cell) copyout(po[0 : Count])
  for (int i = 0; i < Count; ++i) {
    TestType v = 0;
#pragma acc atomic read
    v = cell;
    po[i] = v + static_cast<TestType>(i);
  }
  for (int i = 0; i < Count; ++i)
    REQUIRE(out[sz(i)] == TestType{7} + static_cast<TestType>(i));
}

TEST_CASE("openacc/atomic/capture-post-increment", "[unified][openacc][atomic]") {
  int counter = 0;
  std::vector<int> captured(Count, -1);
  int *pc = captured.data();
#pragma acc parallel loop copy(counter) copyout(pc[0 : Count])
  for (int i = 0; i < Count; ++i) {
    int old = 0;
#pragma acc atomic capture
    old = counter++;
    pc[i] = old;
  }
  REQUIRE(counter == Count);
  std::sort(captured.begin(), captured.end());
  REQUIRE(captured == iotaScaled<int>(Count));
}

TEST_CASE("openacc/atomic/capture-block-before", "[unified][openacc][atomic]") {
  int counter = 10;
  std::vector<int> captured(Count, -1);
  int *pc = captured.data();
#pragma acc parallel loop copy(counter) copyout(pc[0 : Count])
  for (int i = 0; i < Count; ++i) {
    int old = 0;
#pragma acc atomic capture
    {
      old = counter;
      counter += 3;
    }
    pc[i] = old;
  }
  REQUIRE(counter == 10 + (3 * Count));
  std::sort(captured.begin(), captured.end());
  REQUIRE(captured == iotaScaled<int>(Count, 3, 10));
}

TEST_CASE("openacc/atomic/capture-block-after", "[unified][openacc][atomic]") {
  int counter = 0;
  std::vector<int> captured(Count, -1);
  int *pc = captured.data();
#pragma acc parallel loop copy(counter) copyout(pc[0 : Count])
  for (int i = 0; i < Count; ++i) {
    int now = 0;
#pragma acc atomic capture
    {
      counter++;
      now = counter;
    }
    pc[i] = now;
  }
  REQUIRE(counter == Count);
  std::sort(captured.begin(), captured.end());
  REQUIRE(captured == iotaScaled<int>(Count, 1, 1));
}

TEST_CASE("openacc/atomic/capture-double", "[unified][openacc][atomic]") {
  double counter = 0.0;
  std::vector<double> captured(Count, -1.0);
  double *pc = captured.data();
#pragma acc parallel loop copy(counter) copyout(pc[0 : Count])
  for (int i = 0; i < Count; ++i) {
    double old = 0.0;
#pragma acc atomic capture
    {
      old = counter;
      counter += 0.5;
    }
    pc[i] = old;
  }
  REQUIRE(counter == 0.5 * Count);
  std::sort(captured.begin(), captured.end());
  REQUIRE(captured == iotaScaled<double>(Count, 0.5));
}
} // namespace unified::openacc
