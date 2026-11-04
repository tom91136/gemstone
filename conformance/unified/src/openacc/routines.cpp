#include <vector>

#include "support.hpp"

namespace unified::openacc {
namespace {
constexpr int Rows = 6;
constexpr int Columns = 16;

#pragma acc routine seq
int cubePlus(int x) { return (x * x * x) + 1; }

#pragma acc routine seq
double blend(double a, double b) { return (a * 0.5) + (b * 0.25); }

#pragma acc routine vector
void scaleRow(float *row, int n, float factor) {
#pragma acc loop independent
  for (int i = 0; i < n; ++i)
    row[i] *= factor;
}

#pragma acc routine vector
float sumRow(const float *row, int n) {
  float s = 0.0F;
#pragma acc loop reduction(+ : s)
  for (int i = 0; i < n; ++i)
    s += row[i];
  return s;
}

#pragma acc routine seq
int collatzSteps(int n) {
  int steps = 0;
  while (n != 1 && steps < 200) {
    n = (n % 2) == 0 ? n / 2 : (3 * n) + 1;
    ++steps;
  }
  return steps;
}
} // namespace

TEST_CASE("openacc/routine/seq", "[unified][openacc][routine]") {
  std::vector<int> out(Count, 0);
  int *po = out.data();
#pragma acc parallel loop copyout(po[0 : Count])
  for (int i = 0; i < Count; ++i)
    po[i] = cubePlus(i - 10);
  std::vector<int> expected(Count);
  for (int i = 0; i < Count; ++i)
    expected[sz(i)] = ((i - 10) * (i - 10) * (i - 10)) + 1;
  REQUIRE(out == expected);
}

TEST_CASE("openacc/routine/seq-floating", "[unified][openacc][routine]") {
  auto a = iotaScaled<double>(Count);
  auto b = iotaScaled<double>(Count, 2.0);
  std::vector<double> out(Count, 0.0);
  const double *pa = a.data();
  const double *pb = b.data();
  double *po = out.data();
#pragma acc parallel loop copyin(pa[0 : Count], pb[0 : Count]) copyout(po[0 : Count])
  for (int i = 0; i < Count; ++i)
    po[i] = blend(pa[i], pb[i]);
  REQUIRE(out == iotaScaled<double>(Count, 1.0));
}

TEST_CASE("openacc/routine/seq-control-flow", "[unified][openacc][routine]") {
  std::vector<int> out(Count, 0);
  int *po = out.data();
#pragma acc parallel loop copyout(po[0 : Count])
  for (int i = 0; i < Count; ++i)
    po[i] = collatzSteps(i + 1);
  std::vector<int> expected(Count);
  for (int i = 0; i < Count; ++i)
    expected[sz(i)] = collatzSteps(i + 1);
  REQUIRE(out == expected);
}

TEST_CASE("openacc/routine/vector", "[unified][openacc][routine]") {
  auto m = iotaScaled<float>(Rows * Columns);
  float *p = m.data();
#pragma acc parallel loop gang copy(p[0 : Rows * Columns])
  for (int r = 0; r < Rows; ++r) {
    const int offset = r * Columns;
    scaleRow(p + offset, Columns, static_cast<float>(r + 1));
  }
  std::vector<float> expected(sz(Rows * Columns));
  for (int r = 0; r < Rows; ++r)
    for (int c = 0; c < Columns; ++c)
      expected[sz((r * Columns) + c)] = static_cast<float>((r * Columns) + c) * static_cast<float>(r + 1);
  REQUIRE(m == expected);
}

TEST_CASE("openacc/routine/vector-reduction", "[unified][openacc][routine]") {
  auto m = iotaScaled<float>(Rows * Columns);
  std::vector<float> sums(Rows, 0.0F);
  const float *p = m.data();
  float *ps = sums.data();
#pragma acc parallel loop gang copyin(p[0 : Rows * Columns]) copyout(ps[0 : Rows])
  for (int r = 0; r < Rows; ++r) {
    const int offset = r * Columns;
    ps[r] = sumRow(p + offset, Columns);
  }
  std::vector<float> expected(Rows, 0.0F);
  for (int r = 0; r < Rows; ++r)
    for (int c = 0; c < Columns; ++c)
      expected[sz(r)] += static_cast<float>((r * Columns) + c);
  REQUIRE(sums == expected);
}
} // namespace unified::openacc
