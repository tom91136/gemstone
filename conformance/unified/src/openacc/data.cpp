#include <vector>

#include "support.hpp"

namespace unified::openacc {
namespace {
float declared[Count];
#pragma acc declare create(declared)

void scaleDeclared() {
#pragma acc parallel loop present(declared)
  // NOLINTNEXTLINE(modernize-loop-convert)
  for (int i = 0; i < Count; ++i)
    declared[i] = (declared[i] * 3.0F) + 1.0F;
}
} // namespace

TEST_CASE("openacc/data/copy", "[unified][openacc][data]") {
  auto a = iotaScaled<int>(Count);
  int *p = a.data();
#pragma acc parallel loop copy(p[0 : Count])
  for (int i = 0; i < Count; ++i)
    p[i] = (p[i] * 2) + 1;
  REQUIRE(a == iotaScaled<int>(Count, 2, 1));
}

TEST_CASE("openacc/data/copyin", "[unified][openacc][data]") {
  auto in = iotaScaled<int>(Count);
  std::vector<int> out(Count, 0);
  int *pi = in.data();
  int *po = out.data();
#pragma acc parallel loop copyin(pi[0 : Count]) copyout(po[0 : Count])
  for (int i = 0; i < Count; ++i) {
    po[i] = pi[i] + 10;
    pi[i] = -1;
  }
  REQUIRE(out == iotaScaled<int>(Count, 1, 10));
  REQUIRE(in == iotaScaled<int>(Count));
}

TEST_CASE("openacc/data/copyout", "[unified][openacc][data]") {
  std::vector<double> out(Count, -1.0);
  double *p = out.data();
#pragma acc parallel loop copyout(p[0 : Count])
  for (int i = 0; i < Count; ++i)
    p[i] = i * 3.0;
  REQUIRE(out == iotaScaled<double>(Count, 3.0));
}

TEST_CASE("openacc/data/create", "[unified][openacc][data]") {
  auto a = iotaScaled<int>(Count);
  std::vector<int> b(Count, 0);
  std::vector<int> scratch(Count, -7);
  int *pa = a.data();
  int *pb = b.data();
  int *ps = scratch.data();
#pragma acc data copyin(pa[0 : Count]) copyout(pb[0 : Count]) create(ps[0 : Count])
  {
#pragma acc parallel loop present(pa[0 : Count], ps[0 : Count])
    for (int i = 0; i < Count; ++i)
      ps[i] = pa[i] * 2;
#pragma acc parallel loop present(ps[0 : Count], pb[0 : Count])
    for (int i = 0; i < Count; ++i)
      pb[i] = ps[i] + 1;
  }
  REQUIRE(b == iotaScaled<int>(Count, 2, 1));
  REQUIRE(scratch == std::vector<int>(Count, -7));
}

TEST_CASE("openacc/data/present", "[unified][openacc][data]") {
  auto a = iotaScaled<float>(Count);
  float *p = a.data();
  REQUIRE_FALSE(acc_is_present(p, Count * sizeof(float)));
#pragma acc data copy(p[0 : Count])
  {
    REQUIRE(acc_is_present(p, Count * sizeof(float)));
#pragma acc parallel loop present(p[0 : Count])
    for (int i = 0; i < Count; ++i)
      p[i] += 0.5F;
  }
  REQUIRE_FALSE(acc_is_present(p, Count * sizeof(float)));
  REQUIRE(a == iotaScaled<float>(Count, 1.0F, 0.5F));
}

TEST_CASE("openacc/data/enter-exit", "[unified][openacc][data]") {
  auto a = iotaScaled<int>(Count);
  std::vector<int> b(Count, 0);
  int *pa = a.data();
  int *pb = b.data();
#pragma acc enter data copyin(pa[0 : Count]) create(pb[0 : Count])
  REQUIRE(acc_is_present(pa, Count * sizeof(int)));
  REQUIRE(acc_is_present(pb, Count * sizeof(int)));
#pragma acc parallel loop present(pa[0 : Count], pb[0 : Count])
  for (int i = 0; i < Count; ++i)
    pb[i] = pa[i] * pa[i];
#pragma acc exit data copyout(pb[0 : Count]) delete (pa[0 : Count])
  REQUIRE_FALSE(acc_is_present(pa, Count * sizeof(int)));
  REQUIRE_FALSE(acc_is_present(pb, Count * sizeof(int)));
  std::vector<int> expected(Count);
  for (int i = 0; i < Count; ++i)
    expected[sz(i)] = i * i;
  REQUIRE(b == expected);
}

TEST_CASE("openacc/data/reference-counting", "[unified][openacc][data]") {
  std::vector<int> a(Count, 1);
  int *p = a.data();
#pragma acc enter data copyin(p[0 : Count])
#pragma acc enter data copyin(p[0 : Count])
#pragma acc exit data delete (p[0 : Count])
  REQUIRE(acc_is_present(p, Count * sizeof(int)));
#pragma acc exit data delete (p[0 : Count])
  REQUIRE_FALSE(acc_is_present(p, Count * sizeof(int)));
}

TEST_CASE("openacc/data/exit-finalize", "[unified][openacc][data]") {
  std::vector<int> a(Count, 1);
  int *p = a.data();
#pragma acc enter data copyin(p[0 : Count])
#pragma acc enter data copyin(p[0 : Count])
#pragma acc exit data delete (p[0 : Count])finalize
  REQUIRE_FALSE(acc_is_present(p, Count * sizeof(int)));
}

TEST_CASE("openacc/data/update", "[unified][openacc][data]") {
  auto a = iotaScaled<int>(Count);
  int *p = a.data();
#pragma acc data create(p[0 : Count])
  {
    for (int i = 0; i < Count; ++i)
      a[sz(i)] = i + 100;
#pragma acc update device(p[0 : Count])
#pragma acc parallel loop present(p[0 : Count])
    for (int i = 0; i < Count; ++i)
      p[i] *= 2;
    for (auto &v : a)
      v = -1;
#pragma acc update host(p[0 : Count])
    auto expected = iotaScaled<int>(Count, 2, 200);
    REQUIRE(a == expected);
#pragma acc parallel loop present(p[0 : Count])
    for (int i = 0; i < Count; ++i)
      p[i] += 1;
#pragma acc update self(p[0 : Count])
    REQUIRE(a == iotaScaled<int>(Count, 2, 201));
  }
}

TEST_CASE("openacc/data/update-section", "[unified][openacc][data]") {
  std::vector<int> a(Count, 5);
  int *p = a.data();
#pragma acc data copy(p[0 : Count])
  {
    for (int i = 0; i < Count; ++i)
      a[sz(i)] = 9;
#pragma acc update device(p[8 : 16])
#pragma acc parallel loop present(p[0 : Count])
    for (int i = 0; i < Count; ++i)
      p[i] += 1;
  }
  std::vector<int> expected(Count, 6);
  for (int i = 8; i < 24; ++i)
    expected[sz(i)] = 10;
  REQUIRE(a == expected);
}

TEST_CASE("openacc/data/declare-create", "[unified][openacc][data]") {
  for (int i = 0; i < Count; ++i)
    declared[i] = static_cast<float>(i);
#pragma acc update device(declared)
  scaleDeclared();
#pragma acc update host(declared)
  std::vector<float> got(declared, declared + Count);
  REQUIRE(got == iotaScaled<float>(Count, 3.0F, 1.0F));
}

TEST_CASE("openacc/data/host-data-use-device", "[unified][openacc][data]") {
  auto a = iotaScaled<int>(Count);
  int *p = a.data();
#pragma acc enter data copyin(p[0 : Count])
  int *device = nullptr;
#pragma acc host_data use_device(p)
  {
    device = p;
  }
  REQUIRE(device != nullptr);
  REQUIRE(device == acc_deviceptr(p));
#pragma acc parallel loop deviceptr(device)
  for (int i = 0; i < Count; ++i)
    device[i] = device[i] * 4;
#pragma acc exit data copyout(p[0 : Count])
  REQUIRE(a == iotaScaled<int>(Count, 4));
}

TEST_CASE("openacc/data/deviceptr", "[unified][openacc][data]") {
  auto *device = static_cast<double *>(acc_malloc(Count * sizeof(double)));
  REQUIRE(device != nullptr);
#pragma acc parallel loop deviceptr(device)
  for (int i = 0; i < Count; ++i)
    device[i] = i * 0.5;
  std::vector<double> host(Count, -1.0);
  acc_memcpy_from_device(host.data(), device, Count * sizeof(double));
  acc_free(device);
  REQUIRE(host == iotaScaled<double>(Count, 0.5));
}

TEST_CASE("openacc/data/multi-dimensional", "[unified][openacc][data]") {
  constexpr int rows = 6;
  constexpr int cols = 10;
  std::vector<int> m(sz(rows * cols));
  int *p = m.data();
#pragma acc parallel loop collapse(2) copyout(p[0 : rows * cols])
  for (int r = 0; r < rows; ++r)
    for (int c = 0; c < cols; ++c)
      p[(r * cols) + c] = (r * 100) + c;
  for (int r = 0; r < rows; ++r)
    for (int c = 0; c < cols; ++c)
      REQUIRE(m[sz((r * cols) + c)] == (r * 100) + c);
}
} // namespace unified::openacc
