#include <cstddef>
#include <vector>

#include "support.hpp"

namespace unified::openacc {

TEST_CASE("openacc/runtime/devices", "[unified][openacc][runtime]") {
  REQUIRE(acc_get_num_devices(acc_device_nvidia) >= 1);
  REQUIRE(acc_get_device_type() == acc_device_nvidia);
  REQUIRE(acc_get_device_num(acc_device_nvidia) >= 0);
  REQUIRE(acc_get_device_num(acc_device_nvidia) < acc_get_num_devices(acc_device_nvidia));
}

TEST_CASE("openacc/runtime/on-device", "[unified][openacc][runtime]") {
  REQUIRE(acc_on_device(acc_device_host) != 0);
  REQUIRE(acc_on_device(acc_device_nvidia) == 0);
  int onDevice = 0;
#pragma acc parallel copyout(onDevice)
  {
    onDevice = acc_on_device(acc_device_not_host);
  }
  REQUIRE(onDevice != 0);
}

TEST_CASE("openacc/runtime/malloc-free", "[unified][openacc][runtime]") {
  void *p = acc_malloc(1024);
  REQUIRE(p != nullptr);
  acc_free(p);
  acc_free(nullptr);
}

TEST_CASE("openacc/runtime/memcpy", "[unified][openacc][runtime]") {
  auto input = iotaScaled<int>(Count, 3, 1);
  std::vector<int> output(Count, 0);
  auto *device = static_cast<int *>(acc_malloc(Count * sizeof(int)));
  REQUIRE(device != nullptr);
  acc_memcpy_to_device(device, input.data(), Count * sizeof(int));
  acc_memcpy_from_device(output.data(), device, Count * sizeof(int));
  REQUIRE(output == input);
  acc_free(device);
}

TEST_CASE("openacc/runtime/memcpy-device", "[unified][openacc][runtime]") {
  auto input = iotaScaled<int>(Count, 5);
  std::vector<int> output(Count, 0);
  auto *source = static_cast<int *>(acc_malloc(Count * sizeof(int)));
  auto *destination = static_cast<int *>(acc_malloc(Count * sizeof(int)));
  REQUIRE(source != nullptr);
  REQUIRE(destination != nullptr);
  acc_memcpy_to_device(source, input.data(), Count * sizeof(int));
  acc_memcpy_device(destination, source, Count * sizeof(int));
  acc_memcpy_from_device(output.data(), destination, Count * sizeof(int));
  REQUIRE(output == input);
  acc_free(source);
  acc_free(destination);
}

TEST_CASE("openacc/runtime/is-present", "[unified][openacc][runtime]") {
  std::vector<int> a(Count, 1);
  int *p = a.data();
  REQUIRE_FALSE(acc_is_present(p, Count * sizeof(int)));
#pragma acc enter data copyin(p[0 : Count])
  REQUIRE(acc_is_present(p, Count * sizeof(int)));
  REQUIRE(acc_is_present(p + 8, 16 * sizeof(int)));
  REQUIRE_FALSE(acc_is_present(p, (Count + 1) * sizeof(int)));
#pragma acc exit data delete (p[0 : Count])
  REQUIRE_FALSE(acc_is_present(p, Count * sizeof(int)));
}

TEST_CASE("openacc/runtime/map-data", "[unified][openacc][runtime]") {
  std::vector<int> host(Count, 0);
  int *p = host.data();
  auto *device = static_cast<int *>(acc_malloc(Count * sizeof(int)));
  REQUIRE(device != nullptr);
  acc_map_data(p, device, Count * sizeof(int));
  REQUIRE(acc_is_present(p, Count * sizeof(int)));
  REQUIRE(acc_deviceptr(p) == device);
  REQUIRE(acc_hostptr(device) == p);
#pragma acc parallel loop present(p[0 : Count])
  for (int i = 0; i < Count; ++i)
    p[i] = (i * 7) + 1;
  std::vector<int> fetched(Count, 0);
  acc_memcpy_from_device(fetched.data(), device, Count * sizeof(int));
  REQUIRE(fetched == iotaScaled<int>(Count, 7, 1));
  REQUIRE(host == std::vector<int>(Count, 0));
  acc_unmap_data(p);
  REQUIRE_FALSE(acc_is_present(p, Count * sizeof(int)));
  acc_free(device);
}

TEST_CASE("openacc/runtime/data-api", "[unified][openacc][runtime]") {
  auto a = iotaScaled<int>(Count);
  int *p = a.data();
  void *device = acc_copyin(p, Count * sizeof(int));
  REQUIRE(device != nullptr);
  REQUIRE(acc_is_present(p, Count * sizeof(int)));
#pragma acc parallel loop present(p[0 : Count])
  for (int i = 0; i < Count; ++i)
    p[i] += 10;
  acc_update_self(p, Count * sizeof(int));
  REQUIRE(a == iotaScaled<int>(Count, 1, 10));
  for (auto &v : a)
    v = 1;
  acc_update_device(p, Count * sizeof(int));
#pragma acc parallel loop present(p[0 : Count])
  for (int i = 0; i < Count; ++i)
    p[i] *= 3;
  acc_copyout(p, Count * sizeof(int));
  REQUIRE_FALSE(acc_is_present(p, Count * sizeof(int)));
  REQUIRE(a == std::vector<int>(Count, 3));
}

TEST_CASE("openacc/runtime/create-delete", "[unified][openacc][runtime]") {
  std::vector<int> a(Count, 4);
  int *p = a.data();
  void *device = acc_create(p, Count * sizeof(int));
  REQUIRE(device != nullptr);
  REQUIRE(acc_is_present(p, Count * sizeof(int)));
#pragma acc parallel loop present(p[0 : Count])
  for (int i = 0; i < Count; ++i)
    p[i] = i;
  REQUIRE(a == std::vector<int>(Count, 4));
  acc_delete(p, Count * sizeof(int));
  REQUIRE_FALSE(acc_is_present(p, Count * sizeof(int)));
}
} // namespace unified::openacc
