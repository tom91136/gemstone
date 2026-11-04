#include <vector>

#include "support.hpp"

namespace unified::openacc {
namespace {
constexpr int Blocks = 4;
constexpr int Rows = 5;
constexpr int Cols = 8;

std::vector<int> cubeReference() {
  std::vector<int> expected(sz(Blocks * Rows * Cols));
  for (int b = 0; b < Blocks; ++b)
    for (int r = 0; r < Rows; ++r)
      for (int c = 0; c < Cols; ++c)
        expected[sz((((b * Rows) + r) * Cols) + c)] = (b * 10000) + (r * 100) + c;
  return expected;
}
} // namespace

TEST_CASE("openacc/construct/parallel", "[unified][openacc][construct]") {
  std::vector<int> a(Count, 0);
  int *p = a.data();
#pragma acc parallel copy(p[0 : Count]) num_gangs(4) vector_length(32)
  {
#pragma acc loop gang vector
    for (int i = 0; i < Count; ++i)
      p[i] = i * i;
  }
  std::vector<int> expected(Count);
  for (int i = 0; i < Count; ++i)
    expected[sz(i)] = i * i;
  REQUIRE(a == expected);
}

TEST_CASE("openacc/construct/parallel-loop-clauses", "[unified][openacc][construct]") {
  std::vector<int> a(Count, 0);
  int *p = a.data();
#pragma acc parallel loop copy(p[0 : Count]) num_gangs(2) num_workers(2) vector_length(16) gang worker vector
  for (int i = 0; i < Count; ++i)
    p[i] = i + 1;
  REQUIRE(a == iotaScaled<int>(Count, 1, 1));
}

TEST_CASE("openacc/construct/kernels", "[unified][openacc][construct]") {
  std::vector<int> a(Count, 0);
  std::vector<int> b(Count, 0);
  int *pa = a.data();
  int *pb = b.data();
#pragma acc kernels copyout(pa[0 : Count], pb[0 : Count])
  {
#pragma acc loop independent gang vector
    for (int i = 0; i < Count; ++i)
      pa[i] = i * 2;
#pragma acc loop independent gang vector
    for (int i = 0; i < Count; ++i)
      pb[i] = pa[i] + 1;
  }
  REQUIRE(a == iotaScaled<int>(Count, 2));
  REQUIRE(b == iotaScaled<int>(Count, 2, 1));
}

TEST_CASE("openacc/construct/kernels-loop", "[unified][openacc][construct]") {
  std::vector<double> a(Count, 0.0);
  double *p = a.data();
#pragma acc kernels loop copyout(p[0 : Count]) independent gang vector
  for (int i = 0; i < Count; ++i)
    p[i] = i * 0.25;
  REQUIRE(a == iotaScaled<double>(Count, 0.25));
}

TEST_CASE("openacc/construct/serial", "[unified][openacc][construct]") {
  std::vector<int> a(Count, 0);
  int *p = a.data();
  int total = 0;
#pragma acc serial copy(p[0 : Count], total)
  {
    for (int i = 0; i < Count; ++i) {
      p[i] = i == 0 ? 1 : p[i - 1] * 2 % 1009;
      total += p[i];
    }
  }
  std::vector<int> expected(Count);
  int expectedTotal = 0;
  for (int i = 0; i < Count; ++i) {
    expected[sz(i)] = i == 0 ? 1 : expected[sz(i - 1)] * 2 % 1009;
    expectedTotal += expected[sz(i)];
  }
  REQUIRE(a == expected);
  REQUIRE(total == expectedTotal);
}

TEST_CASE("openacc/construct/equivalence", "[unified][openacc][construct]") {
  auto input = iotaScaled<float>(Count, 0.5F);
  std::vector<float> fromParallel(Count);
  std::vector<float> fromKernels(Count);
  std::vector<float> fromSerial(Count);
  const float *pi = input.data();
  float *pp = fromParallel.data();
  float *pk = fromKernels.data();
  float *ps = fromSerial.data();
#pragma acc parallel loop copyin(pi[0 : Count]) copyout(pp[0 : Count])
  for (int i = 0; i < Count; ++i)
    pp[i] = (pi[i] * pi[i]) + 1.0F;
#pragma acc kernels loop copyin(pi[0 : Count]) copyout(pk[0 : Count])
  for (int i = 0; i < Count; ++i)
    pk[i] = (pi[i] * pi[i]) + 1.0F;
#pragma acc serial copyin(pi[0 : Count]) copyout(ps[0 : Count])
  for (int i = 0; i < Count; ++i)
    ps[i] = (pi[i] * pi[i]) + 1.0F;
  REQUIRE(fromParallel == fromKernels);
  REQUIRE(fromParallel == fromSerial);
}

TEST_CASE("openacc/loop/gang-worker-vector", "[unified][openacc][loop]") {
  std::vector<int> a(sz(Blocks * Rows * Cols), 0);
  int *p = a.data();
#pragma acc parallel copy(p[0 : Blocks * Rows * Cols]) num_gangs(Blocks) num_workers(Rows) vector_length(Cols)
  {
#pragma acc loop gang
    for (int b = 0; b < Blocks; ++b) {
#pragma acc loop worker
      for (int r = 0; r < Rows; ++r) {
#pragma acc loop vector
        for (int c = 0; c < Cols; ++c)
          p[(((b * Rows) + r) * Cols) + c] = (b * 10000) + (r * 100) + c;
      }
    }
  }
  REQUIRE(a == cubeReference());
}

TEST_CASE("openacc/loop/gang-vector-split", "[unified][openacc][loop]") {
  std::vector<int> a(sz(Rows * Cols), 0);
  int *p = a.data();
#pragma acc parallel loop gang copy(p[0 : Rows * Cols])
  for (int r = 0; r < Rows; ++r) {
#pragma acc loop vector
    for (int c = 0; c < Cols; ++c)
      p[(r * Cols) + c] = (r * Cols) + c;
  }
  REQUIRE(a == iotaScaled<int>(Rows * Cols));
}

TEST_CASE("openacc/loop/seq-independent", "[unified][openacc][loop]") {
  std::vector<int> a(sz(Rows * Cols), 0);
  int *p = a.data();
#pragma acc parallel loop gang independent copy(p[0 : Rows * Cols])
  for (int r = 0; r < Rows; ++r) {
#pragma acc loop seq
    for (int c = 0; c < Cols; ++c)
      p[(r * Cols) + c] = c == 0 ? r : p[(r * Cols) + c - 1] + 2;
  }
  std::vector<int> expected(sz(Rows * Cols));
  for (int r = 0; r < Rows; ++r)
    for (int c = 0; c < Cols; ++c)
      expected[sz((r * Cols) + c)] = r + (2 * c);
  REQUIRE(a == expected);
}

TEST_CASE("openacc/loop/collapse2", "[unified][openacc][loop]") {
  std::vector<int> a(sz(Rows * Cols), 0);
  int *p = a.data();
#pragma acc parallel loop collapse(2) copy(p[0 : Rows * Cols])
  for (int r = 0; r < Rows; ++r)
    for (int c = 0; c < Cols; ++c)
      p[(r * Cols) + c] = (r * 100) + c;
  std::vector<int> expected(sz(Rows * Cols));
  for (int r = 0; r < Rows; ++r)
    for (int c = 0; c < Cols; ++c)
      expected[sz((r * Cols) + c)] = (r * 100) + c;
  REQUIRE(a == expected);
}

TEST_CASE("openacc/loop/collapse3", "[unified][openacc][loop]") {
  std::vector<int> a(sz(Blocks * Rows * Cols), 0);
  int *p = a.data();
#pragma acc parallel loop collapse(3) copy(p[0 : Blocks * Rows * Cols])
  for (int b = 0; b < Blocks; ++b)
    for (int r = 0; r < Rows; ++r)
      for (int c = 0; c < Cols; ++c)
        p[(((b * Rows) + r) * Cols) + c] = (b * 10000) + (r * 100) + c;
  REQUIRE(a == cubeReference());
}

TEST_CASE("openacc/loop/private", "[unified][openacc][loop]") {
  std::vector<int> out(Rows, 0);
  int *po = out.data();
  int scratch[Cols];
#pragma acc parallel loop gang private(scratch) copyout(po[0 : Rows])
  for (int r = 0; r < Rows; ++r) {
#pragma acc loop seq
    for (int c = 0; c < Cols; ++c)
      scratch[c] = r * (c + 1);
    int sum = 0;
#pragma acc loop seq
    // NOLINTNEXTLINE(modernize-loop-convert)
    for (int c = 0; c < Cols; ++c)
      sum += scratch[c];
    po[r] = sum;
  }
  std::vector<int> expected(Rows);
  for (int r = 0; r < Rows; ++r)
    expected[sz(r)] = r * Cols * (Cols + 1) / 2;
  REQUIRE(out == expected);
}

TEST_CASE("openacc/loop/private-scalar", "[unified][openacc][loop]") {
  std::vector<int> out(Count, 0);
  int *po = out.data();
  int temporary = -1;
#pragma acc parallel loop private(temporary) copyout(po[0 : Count])
  for (int i = 0; i < Count; ++i) {
    temporary = i * 3;
    po[i] = temporary + 1;
  }
  REQUIRE(out == iotaScaled<int>(Count, 3, 1));
  REQUIRE(temporary == -1);
}

TEST_CASE("openacc/loop/firstprivate", "[unified][openacc][loop]") {
  std::vector<int> out(Count, 0);
  int *po = out.data();
  int base = 1000;
  int table[4] = {1, 2, 3, 4};
#pragma acc parallel loop firstprivate(base, table) copyout(po[0 : Count])
  for (int i = 0; i < Count; ++i) {
    po[i] = base + table[i % 4] + i;
    base += 0;
  }
  std::vector<int> expected(Count);
  for (int i = 0; i < Count; ++i)
    expected[sz(i)] = 1000 + ((i % 4) + 1) + i;
  REQUIRE(out == expected);
  REQUIRE(base == 1000);
}

TEST_CASE("openacc/loop/firstprivate-modified", "[unified][openacc][loop]") {
  std::vector<int> out(Rows, 0);
  int *po = out.data();
  int seed = 7;
#pragma acc parallel loop gang firstprivate(seed) copyout(po[0 : Rows])
  for (int r = 0; r < Rows; ++r) {
    seed += r;
    po[r] = seed;
  }
  std::vector<int> expected(Rows);
  for (int r = 0; r < Rows; ++r)
    expected[sz(r)] = 7 + r;
  REQUIRE(out == expected);
  REQUIRE(seed == 7);
}

namespace {
int whereRuns(bool onDevice) {
  int result = -1;
#pragma acc parallel if (onDevice) copyout(result)
  {
    result = acc_on_device(acc_device_nvidia) ? 1 : 0;
  }
  return result;
}

bool presentInside(int *p, bool condition) {
  bool present = false;
#pragma acc data copy(p[0 : Count]) if (condition)
  {
    present = acc_is_present(p, Count * sizeof(int)) != 0;
  }
  return present;
}
} // namespace

TEST_CASE("openacc/construct/if", "[unified][openacc][construct]") {
  REQUIRE(whereRuns(true) == 1);
  REQUIRE(whereRuns(false) == 0);
}

TEST_CASE("openacc/construct/if-data", "[unified][openacc][construct]") {
  std::vector<int> a(Count, 1);
  REQUIRE(presentInside(a.data(), true));
  REQUIRE_FALSE(presentInside(a.data(), false));
}

TEST_CASE("openacc/construct/if-update", "[unified][openacc][construct]") {
  std::vector<int> a(Count, 1);
  int *p = a.data();
#pragma acc data copyin(p[0 : Count])
  {
    for (auto &v : a)
      v = 2;
#pragma acc update device(p[0 : Count]) if (false)
#pragma acc update device(p[0 : Count]) if_present
#pragma acc parallel loop present(p[0 : Count])
    for (int i = 0; i < Count; ++i)
      p[i] += 1;
#pragma acc update host(p[0 : Count]) if (true)
  }
  REQUIRE(a == std::vector<int>(Count, 3));
}
} // namespace unified::openacc
