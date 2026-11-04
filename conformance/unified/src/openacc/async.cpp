#include <vector>

#include "support.hpp"

namespace unified::openacc {
namespace {
constexpr int Spin = 32;

#pragma acc routine seq
int spinValue(int v) {
  for (int k = 0; k < Spin; ++k)
    v = ((v * 5) + k) % 100003;
  return v;
}

std::vector<int> chainedReference() {
  std::vector<int> expected(Count);
  for (int i = 0; i < Count; ++i)
    expected[sz(i)] = spinValue(i) * 2;
  return expected;
}
} // namespace

TEST_CASE("openacc/async/queues", "[unified][openacc][async]") {
  std::vector<int> a(Count, 0);
  std::vector<int> b(Count, 0);
  int *pa = a.data();
  int *pb = b.data();
#pragma acc parallel loop copyout(pa[0 : Count]) async(1)
  for (int i = 0; i < Count; ++i)
    pa[i] = i + 1;
#pragma acc parallel loop copyout(pb[0 : Count]) async(2)
  for (int i = 0; i < Count; ++i)
    pb[i] = i * 2;
#pragma acc wait(1)
  REQUIRE(a == iotaScaled<int>(Count, 1, 1));
#pragma acc wait(2)
  REQUIRE(b == iotaScaled<int>(Count, 2));
}

TEST_CASE("openacc/async/wait-multiple-queues", "[unified][openacc][async]") {
  std::vector<int> a(Count, 0);
  std::vector<int> b(Count, 0);
  std::vector<int> c(Count, 0);
  int *pa = a.data();
  int *pb = b.data();
  int *pc = c.data();
#pragma acc parallel loop copyout(pa[0 : Count]) async(1)
  for (int i = 0; i < Count; ++i)
    pa[i] = i + 1;
#pragma acc parallel loop copyout(pb[0 : Count]) async(2)
  for (int i = 0; i < Count; ++i)
    pb[i] = i * 2;
#pragma acc parallel loop copyout(pc[0 : Count]) async(3)
  for (int i = 0; i < Count; ++i)
    pc[i] = i * 3;
#pragma acc wait(1, 2, 3)
  REQUIRE(a == iotaScaled<int>(Count, 1, 1));
  REQUIRE(b == iotaScaled<int>(Count, 2));
  REQUIRE(c == iotaScaled<int>(Count, 3));
}

TEST_CASE("openacc/async/wait-all", "[unified][openacc][async]") {
  std::vector<int> a(Count, 0);
  std::vector<int> b(Count, 0);
  int *pa = a.data();
  int *pb = b.data();
#pragma acc parallel loop copyout(pa[0 : Count]) async(4)
  for (int i = 0; i < Count; ++i)
    pa[i] = i;
#pragma acc parallel loop copyout(pb[0 : Count]) async(5)
  for (int i = 0; i < Count; ++i)
    pb[i] = -i;
#pragma acc wait
  REQUIRE(a == iotaScaled<int>(Count));
  REQUIRE(b == iotaScaled<int>(Count, -1));
}

TEST_CASE("openacc/async/wait-clause-dependency", "[unified][openacc][async]") {
  std::vector<int> out(Count, 0);
  int *po = out.data();
  std::vector<int> tmp(Count, 0);
  int *pt = tmp.data();
#pragma acc enter data create(pt[0 : Count]) copyin(po[0 : Count])
#pragma acc parallel loop present(pt[0 : Count]) async(1)
  for (int i = 0; i < Count; ++i)
    pt[i] = spinValue(i);
#pragma acc parallel loop present(pt[0 : Count], po[0 : Count]) async(2) wait(1)
  for (int i = 0; i < Count; ++i)
    po[i] = pt[i] * 2;
#pragma acc update self(po[0 : Count]) async(2)
#pragma acc wait(2)
  REQUIRE(out == chainedReference());
#pragma acc exit data delete (pt[0 : Count], po[0 : Count])
}

TEST_CASE("openacc/async/wait-directive-async", "[unified][openacc][async]") {
  std::vector<int> out(Count, 0);
  int *po = out.data();
  std::vector<int> tmp(Count, 0);
  int *pt = tmp.data();
#pragma acc enter data create(pt[0 : Count]) copyin(po[0 : Count])
#pragma acc parallel loop present(pt[0 : Count]) async(1)
  for (int i = 0; i < Count; ++i)
    pt[i] = spinValue(i);
#pragma acc wait(1) async(2)
#pragma acc parallel loop present(pt[0 : Count], po[0 : Count]) async(2)
  for (int i = 0; i < Count; ++i)
    po[i] = pt[i] * 2;
#pragma acc update self(po[0 : Count]) async(2)
#pragma acc wait(2)
  REQUIRE(out == chainedReference());
#pragma acc exit data delete (pt[0 : Count], po[0 : Count])
}

TEST_CASE("openacc/async/default-queue", "[unified][openacc][async]") {
  std::vector<int> a(Count, 0);
  int *pa = a.data();
#pragma acc parallel loop copyout(pa[0 : Count]) async
  for (int i = 0; i < Count; ++i)
    pa[i] = i + 5;
#pragma acc wait
  REQUIRE(a == iotaScaled<int>(Count, 1, 5));
}

TEST_CASE("openacc/async/data-movement", "[unified][openacc][async]") {
  auto a = iotaScaled<int>(Count);
  int *pa = a.data();
#pragma acc enter data copyin(pa[0 : Count]) async(1)
#pragma acc parallel loop present(pa[0 : Count]) async(1)
  for (int i = 0; i < Count; ++i)
    pa[i] += 100;
#pragma acc update self(pa[0 : Count]) async(1)
#pragma acc wait(1)
  REQUIRE(a == iotaScaled<int>(Count, 1, 100));
#pragma acc exit data delete (pa[0 : Count])async(1)
#pragma acc wait(1)
  REQUIRE_FALSE(acc_is_present(pa, Count * sizeof(int)));
}

TEST_CASE("openacc/async/runtime-api", "[unified][openacc][async]") {
  std::vector<int> a(Count, 0);
  std::vector<int> b(Count, 0);
  int *pa = a.data();
  int *pb = b.data();
#pragma acc parallel loop copyout(pa[0 : Count]) async(1)
  for (int i = 0; i < Count; ++i)
    pa[i] = i;
#pragma acc parallel loop copyout(pb[0 : Count]) async(2)
  for (int i = 0; i < Count; ++i)
    pb[i] = i + 1;
  acc_wait(1);
  REQUIRE(acc_async_test(1) != 0);
  acc_wait_async(2, 3);
  acc_wait(3);
  REQUIRE(acc_async_test(2) != 0);
  acc_wait_all();
  REQUIRE(acc_async_test_all() != 0);
  REQUIRE(a == iotaScaled<int>(Count));
  REQUIRE(b == iotaScaled<int>(Count, 1, 1));
}

TEST_CASE("openacc/async/runtime-api-dependency", "[unified][openacc][async]") {
  std::vector<int> out(Count, 0);
  int *po = out.data();
  std::vector<int> tmp(Count, 0);
  int *pt = tmp.data();
#pragma acc enter data create(pt[0 : Count]) copyin(po[0 : Count])
#pragma acc parallel loop present(pt[0 : Count]) async(1)
  for (int i = 0; i < Count; ++i)
    pt[i] = spinValue(i);
  acc_wait_async(1, 2);
#pragma acc parallel loop present(pt[0 : Count], po[0 : Count]) async(2)
  for (int i = 0; i < Count; ++i)
    po[i] = pt[i] * 2;
#pragma acc update self(po[0 : Count]) async(2)
  acc_wait(2);
  REQUIRE(out == chainedReference());
#pragma acc exit data delete (pt[0 : Count], po[0 : Count])
}
} // namespace unified::openacc
