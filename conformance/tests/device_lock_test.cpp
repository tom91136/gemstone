#include "device_lock.hpp"

#include <cerrno>
#include <chrono>
#include <csignal>
#include <cstdint>
#include <cstdlib>

#include <catch2/catch_test_macros.hpp>

#include "conformance/device.hpp"
#include "device_selector.hpp"

#if !defined(_WIN32)
  #include <poll.h>
  #include <unistd.h>

  #include <sys/wait.h>
#endif

namespace {

using Clock = std::chrono::steady_clock;

#if !defined(_WIN32)
bool lock_contention_serializes_processes() {
  int ready[2]{};
  int release[2]{};
  int attempting[2]{};
  int acquired[2]{};
  if (::pipe(ready) != 0 || ::pipe(release) != 0 || ::pipe(attempting) != 0 || ::pipe(acquired) != 0) return false;
  const auto device = conformance::make_pci_device(static_cast<uint32_t>(::getpid()), 0xfe, 0x1f, 7);
  const auto holder = ::fork();
  if (holder == 0) {
    ::close(ready[0]);
    ::close(release[1]);
    ::close(attempting[0]);
    ::close(attempting[1]);
    ::close(acquired[0]);
    ::close(acquired[1]);
    try {
      conformance::DeviceLock lock(device);
      const char signal = '1';
      (void)::write(ready[1], &signal, 1);
      char release_signal = 0;
      (void)::read(release[0], &release_signal, 1);
    } catch (...) {
      std::_Exit(1);
    }
    ::close(ready[1]);
    ::close(release[0]);
    std::_Exit(0);
  }
  if (holder < 0) {
    ::close(ready[0]);
    ::close(ready[1]);
    ::close(release[0]);
    ::close(release[1]);
    ::close(attempting[0]);
    ::close(attempting[1]);
    ::close(acquired[0]);
    ::close(acquired[1]);
    return false;
  }
  ::close(ready[1]);
  ::close(release[0]);

  auto terminate = [](pid_t process) {
    if (process > 0) {
      (void)::kill(process, SIGTERM);
      int status = 0;
      (void)::waitpid(process, &status, 0);
    }
  };

  char signal = 0;
  if (::read(ready[0], &signal, 1) != 1) {
    ::close(ready[0]);
    ::close(release[1]);
    ::close(attempting[0]);
    ::close(attempting[1]);
    ::close(acquired[0]);
    ::close(acquired[1]);
    terminate(holder);
    return false;
  }

  const auto waiter = ::fork();
  if (waiter == 0) {
    ::close(ready[0]);
    ::close(release[1]);
    ::close(attempting[0]);
    ::close(acquired[0]);
    try {
      const char attempting_signal = '1';
      (void)::write(attempting[1], &attempting_signal, 1);
      conformance::DeviceLock lock(device);
      const char acquired_signal = '1';
      (void)::write(acquired[1], &acquired_signal, 1);
    } catch (...) {
      std::_Exit(1);
    }
    std::_Exit(0);
  }
  if (waiter < 0) {
    ::close(ready[0]);
    ::close(release[1]);
    ::close(attempting[0]);
    ::close(attempting[1]);
    ::close(acquired[0]);
    ::close(acquired[1]);
    terminate(holder);
    return false;
  }
  ::close(attempting[1]);
  ::close(acquired[1]);

  char attempting_signal = 0;
  if (::read(attempting[0], &attempting_signal, 1) != 1) {
    ::close(ready[0]);
    ::close(release[1]);
    ::close(attempting[0]);
    ::close(acquired[0]);
    terminate(waiter);
    terminate(holder);
    return false;
  }

  pollfd descriptor{acquired[0], POLLIN, 0};
  const auto acquired_before_release = [&] {
    int ready = 0;
    do {
      ready = ::poll(&descriptor, 1, 250);
    } while (ready < 0 && errno == EINTR);
    if (ready <= 0) return false;
    char acquired_signal = 0;
    return ::read(acquired[0], &acquired_signal, 1) == 1;
  }();

  const char release_signal = '1';
  (void)::write(release[1], &release_signal, 1);
  char acquired_signal = 0;
  const auto acquired_after_release = !acquired_before_release && ::read(acquired[0], &acquired_signal, 1) == 1;
  ::close(ready[0]);
  ::close(release[1]);
  ::close(attempting[0]);
  ::close(acquired[0]);

  int holder_status = 0;
  int waiter_status = 0;
  (void)::waitpid(holder, &holder_status, 0);
  (void)::waitpid(waiter, &waiter_status, 0);
  return signal == '1' && !acquired_before_release && acquired_after_release && WIFEXITED(holder_status) && WEXITSTATUS(holder_status) == 0
         && WIFEXITED(waiter_status) && WEXITSTATUS(waiter_status) == 0;
}

bool distinct_devices_do_not_contend() {
  const auto domain = static_cast<uint32_t>(::getpid());
  conformance::DeviceLock first(conformance::make_pci_device(domain, 0xfe, 0x1f, 6));
  const auto started = Clock::now();
  conformance::DeviceLock second(conformance::make_pci_device(domain, 0xfe, 0x1f, 5));
  return std::chrono::duration_cast<std::chrono::milliseconds>(Clock::now() - started).count() < 100;
}
#endif

} // namespace

TEST_CASE("physical device keys use canonical encoding", "[device-lock]") {
  const auto pci = conformance::make_pci_device(0x12345678, 0x9a, 0xbc, 0xde);
  CHECK(conformance::physical_device_key(pci) == "pci-123456789abcde000000000000000000");
  CHECK(conformance::physical_device_key(conformance::make_registry_device("platform:device")).starts_with("reg-"));
  CHECK(conformance::physical_device_key(conformance::make_registry_device("platform:device"))
        == conformance::physical_device_key(conformance::make_registry_device("platform:device")));
  CHECK(conformance::physical_device_key(conformance::physical_device_from_key(conformance::physical_device_key(pci)))
        == conformance::physical_device_key(pci));
  CHECK_THROWS_AS(conformance::physical_device_from_key("host-00000000000000000000000000000000"), std::invalid_argument);
}

TEST_CASE("device selector supports glob patterns", "[device-selector]") {
  CHECK(conformance::glob_matches("*Arc?A750*", "Intel Arc A750 Graphics"));
  CHECK_FALSE(conformance::glob_matches("*AMD*", "Intel Arc A750 Graphics"));
}

#if !defined(_WIN32)

TEST_CASE("same-device locks serialize processes", "[device-lock]") { CHECK(lock_contention_serializes_processes()); }

TEST_CASE("different-device locks do not contend", "[device-lock]") { CHECK(distinct_devices_do_not_contend()); }
#endif
