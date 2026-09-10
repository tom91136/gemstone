#ifndef GEMSTONE_TEST_RPC_FIXTURE_HPP
#define GEMSTONE_TEST_RPC_FIXTURE_HPP

#include <algorithm>
#include <array>
#include <atomic>
#include <cstdint>
#include <cstdio>
#include <cstdlib>
#include <cstring>
#include <map>
#include <mutex>
#include <optional>
#include <thread>
#include <vector>

#include <unistd.h>

#include <arpa/inet.h>
#include <netinet/tcp.h>
#include <sys/socket.h>

#define GEMSTONE_RPC_FIXTURE_CHECK(condition)                                                                                              \
  do {                                                                                                                                     \
    if (!(condition)) {                                                                                                                    \
      std::fprintf(stderr, "%s:%d: %s\n", __FILE__, __LINE__, #condition);                                                                 \
      std::abort();                                                                                                                        \
    }                                                                                                                                      \
  } while (false)

namespace gemstone_test {

// Real native API/transport test peer. Kernel execution is deliberately mocked:
// validate launch geometry, publish one range per group, and complete after two polls.
class RpcFixture {
public:
  inline static constexpr char token[] = "0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef";
  struct Expected {
    uint32_t local, groups, global, offset, seed;
    std::array<uint32_t, 2> local_yz{1, 1}, groups_yz{1, 1}, global_yz{1, 1};
    // Boundary-sized geometry can be inspected without mocking billions of output elements.
    bool geometry_only = false;
  };
  RpcFixture() {
    listener_ = socket(AF_INET, SOCK_STREAM, 0);
    GEMSTONE_RPC_FIXTURE_CHECK(listener_ >= 0);
    sockaddr_in address{};
    address.sin_family = AF_INET;
    address.sin_addr.s_addr = htonl(INADDR_LOOPBACK);
    GEMSTONE_RPC_FIXTURE_CHECK(bind(listener_, reinterpret_cast<sockaddr *>(&address), sizeof(address)) == 0);
    GEMSTONE_RPC_FIXTURE_CHECK(listen(listener_, 8) == 0);
    socklen_t length = sizeof(address);
    GEMSTONE_RPC_FIXTURE_CHECK(getsockname(listener_, reinterpret_cast<sockaddr *>(&address), &length) == 0);
    port_ = ntohs(address.sin_port);
    acceptor_ = std::thread([this] {
      while (!stopped_.load()) {
        const int connection = accept(listener_, nullptr, nullptr);
        if (connection < 0) {
          GEMSTONE_RPC_FIXTURE_CHECK(stopped_.load());
          break;
        }
        const int enabled = 1;
        const timeval timeout{5, 0};
        GEMSTONE_RPC_FIXTURE_CHECK(setsockopt(connection, IPPROTO_TCP, TCP_NODELAY, &enabled, sizeof(enabled)) == 0);
        GEMSTONE_RPC_FIXTURE_CHECK(setsockopt(connection, SOL_SOCKET, SO_RCVTIMEO, &timeout, sizeof(timeout)) == 0);
        GEMSTONE_RPC_FIXTURE_CHECK(setsockopt(connection, SOL_SOCKET, SO_SNDTIMEO, &timeout, sizeof(timeout)) == 0);
        std::lock_guard lock(connections_mutex_);
        connections_.push_back(connection);
        workers_.emplace_back([this, connection] {
          serve(connection);
          std::lock_guard guard(connections_mutex_);
          connections_.erase(std::remove(connections_.begin(), connections_.end(), connection), connections_.end());
          close(connection);
        });
      }
    });
  }
  ~RpcFixture() {
    stopped_.store(true);
    shutdown(listener_, SHUT_RDWR);
    close(listener_);
    acceptor_.join();
    {
      std::lock_guard lock(connections_mutex_);
      for (int connection : connections_)
        shutdown(connection, SHUT_RDWR);
    }
    for (auto &worker : workers_)
      worker.join();
  }
  uint16_t port() const { return port_; }
  void expect(Expected expected) {
    std::lock_guard lock(state_mutex_);
    GEMSTONE_RPC_FIXTURE_CHECK(!active_);
    expected_ = expected;
    trailing_arguments_.reset();
    indirect_input_.reset();
    indirect_write_.reset();
  }
  void expectTrailingArguments(std::vector<uint8_t> bytes) {
    std::lock_guard lock(state_mutex_);
    trailing_arguments_ = std::move(bytes);
  }
  void expectGlobalBinding(std::string name, uint64_t offset, std::vector<uint8_t> initial) {
    std::lock_guard lock(state_mutex_);
    expected_globals_.emplace(std::move(name), std::make_pair(offset, std::move(initial)));
  }
  void checkAllocationAddress(const void *pointer, bool present = true) {
    std::lock_guard lock(state_mutex_);
    const auto address = reinterpret_cast<uint64_t>(pointer);
    const auto found = std::find_if(allocation_addresses_.begin(), allocation_addresses_.end(),
                                    [&](const auto &entry) { return entry.second == address; });
    GEMSTONE_RPC_FIXTURE_CHECK((found != allocation_addresses_.end()) == present);
  }
  void checkAllocationWord(const void *base, size_t offset, uint64_t expected) {
    std::lock_guard lock(state_mutex_);
    const auto found = std::find_if(allocation_addresses_.begin(), allocation_addresses_.end(),
                                    [&](const auto &entry) { return entry.second == reinterpret_cast<uint64_t>(base); });
    GEMSTONE_RPC_FIXTURE_CHECK(found != allocation_addresses_.end());
    const auto &bytes = allocations_.at(found->first);
    GEMSTONE_RPC_FIXTURE_CHECK(offset <= bytes.size() && bytes.size() - offset >= sizeof(uint64_t));
    uint64_t actual = 0;
    std::memcpy(&actual, bytes.data() + offset, sizeof(actual));
    GEMSTONE_RPC_FIXTURE_CHECK(actual == expected);
  }
  void expectIndirectInput(const void *table, uint32_t value, std::optional<uint32_t> device_write = std::nullopt) {
    std::lock_guard lock(state_mutex_);
    indirect_input_ = IndirectInput{reinterpret_cast<uint64_t>(table), value, device_write};
  }
  uint64_t writtenBytes(const void *address) {
    std::lock_guard lock(state_mutex_);
    for (const auto &[id, base] : allocation_addresses_)
      if (base == reinterpret_cast<uint64_t>(address)) return written_bytes_[id];
    GEMSTONE_RPC_FIXTURE_CHECK(false);
    return 0;
  }
  void checkGlobalBindings() {
    std::lock_guard lock(state_mutex_);
    GEMSTONE_RPC_FIXTURE_CHECK(expected_globals_.empty());
  }
  void check() {
    std::lock_guard lock(state_mutex_);
    GEMSTONE_RPC_FIXTURE_CHECK(!active_ && launches_ > 0 && pending_ == launches_ * 2);
  }
  void failNextCompletion() { fail_completion_ = true; }
  unsigned launches() const { return launches_.load(); }
  unsigned programLoads() const { return program_loads_.load(); }

  inline static constexpr uint32_t spirv[] = {0x07230203,
                                              0x00010600,
                                              0,
                                              10,
                                              0,
                                              (2U << 16) | 17U,
                                              6,
                                              (3U << 16) | 14U,
                                              2,
                                              2,
                                              (5U << 16) | 15U,
                                              6,
                                              5,
                                              0x756f7267,
                                              0x00007370,
                                              (2U << 16) | 19U,
                                              1,
                                              (4U << 16) | 21U,
                                              2,
                                              32,
                                              0,
                                              (4U << 16) | 32U,
                                              3,
                                              5,
                                              2,
                                              (5U << 16) | 33U,
                                              4,
                                              1,
                                              3,
                                              2,
                                              (5U << 16) | 54U,
                                              1,
                                              5,
                                              0,
                                              4,
                                              (3U << 16) | 55U,
                                              3,
                                              6,
                                              (3U << 16) | 55U,
                                              2,
                                              7,
                                              (2U << 16) | 248U,
                                              8,
                                              (1U << 16) | 253U,
                                              (1U << 16) | 56U};

private:
  static uint32_t u32(const uint8_t *p) { return uint32_t(p[0]) << 24 | uint32_t(p[1]) << 16 | uint32_t(p[2]) << 8 | p[3]; }
  static uint64_t u64(const uint8_t *p) { return uint64_t(u32(p)) << 32 | u32(p + 4); }
  static void put32(std::vector<uint8_t> &v, uint32_t x) {
    for (int shift = 24; shift >= 0; shift -= 8)
      v.push_back(static_cast<uint8_t>(x >> shift));
  }
  static void put64(std::vector<uint8_t> &v, uint64_t x) {
    put32(v, static_cast<uint32_t>(x >> 32));
    put32(v, static_cast<uint32_t>(x));
  }
  bool receive(int socket, void *output, size_t size) {
    auto *bytes = static_cast<uint8_t *>(output);
    while (size) {
      const auto count = recv(socket, bytes, size, 0);
      if (count == 0 || stopped_.load()) return false;
      GEMSTONE_RPC_FIXTURE_CHECK(count > 0);
      bytes += count;
      size -= count;
    }
    return true;
  }
  static void send(int socket, const std::vector<uint8_t> &bytes) {
    size_t offset = 0;
    while (offset < bytes.size()) {
      auto count = ::send(socket, bytes.data() + offset, bytes.size() - offset, MSG_NOSIGNAL);
      GEMSTONE_RPC_FIXTURE_CHECK(count > 0);
      offset += count;
    }
  }
  void serve(int socket) {
    std::array<uint8_t, 96> header{};
    while (receive(socket, header.data(), header.size())) {
      GEMSTONE_RPC_FIXTURE_CHECK(u32(header.data()) == 0x474d4531 && u32(header.data() + 4) == 3);
      GEMSTONE_RPC_FIXTURE_CHECK(std::memcmp(header.data() + 8, token, 64) == 0);
      const auto operation = u32(header.data() + 88);
      const auto size = u32(header.data() + 92);
      GEMSTONE_RPC_FIXTURE_CHECK(size <= 16U * 1024U * 1024U);
      std::vector<uint8_t> payload(size);
      if (size && !receive(socket, payload.data(), size)) break;
      std::lock_guard lock(state_mutex_);
      std::vector<uint8_t> result;
      switch (operation) {
        case 0: break;
        case 1:
          GEMSTONE_RPC_FIXTURE_CHECK(size == 20);
          allocations_[u64(payload.data())].resize(u64(payload.data() + 12));
          break;
        case 2:
          GEMSTONE_RPC_FIXTURE_CHECK(allocations_.erase(u64(payload.data())) == 1);
          allocation_addresses_.erase(u64(payload.data()));
          break;
        case 17: {
          GEMSTONE_RPC_FIXTURE_CHECK(size == 16);
          const auto id = u64(payload.data()), base = u64(payload.data() + 8);
          GEMSTONE_RPC_FIXTURE_CHECK(allocations_.count(id) == 1 && base != 0);
          GEMSTONE_RPC_FIXTURE_CHECK(allocation_addresses_.emplace(id, base).second);
          break;
        }
        case 3: {
          auto &bytes = allocations_.at(u64(payload.data()));
          const auto offset = u64(payload.data() + 8);
          const auto count = u32(payload.data() + 16);
          GEMSTONE_RPC_FIXTURE_CHECK(offset + count <= bytes.size() && size == 20 + count);
          std::memcpy(bytes.data() + offset, payload.data() + 20, count);
          written_bytes_[u64(payload.data())] += count;
          break;
        }
        case 4: {
          GEMSTONE_RPC_FIXTURE_CHECK(!active_ || completed_);
          auto &bytes = allocations_.at(u64(payload.data()));
          const auto offset = u64(payload.data() + 8);
          const auto count = u32(payload.data() + 16);
          GEMSTONE_RPC_FIXTURE_CHECK(offset + count <= bytes.size());
          result.assign(bytes.begin() + offset, bytes.begin() + offset + count);
          break;
        }
        case 5: ++program_loads_; put64(result, 91); break;
        case 6: break;
        case 16: {
          GEMSTONE_RPC_FIXTURE_CHECK(size >= 28 && u64(payload.data()) == 91);
          const auto name_size = u32(payload.data() + 8);
          GEMSTONE_RPC_FIXTURE_CHECK(size == 28 + name_size);
          const std::string name(reinterpret_cast<const char *>(payload.data() + 12), name_size);
          const auto allocation = u64(payload.data() + 12 + name_size);
          const auto offset = u64(payload.data() + 20 + name_size);
          auto expected = expected_globals_.find(name);
          GEMSTONE_RPC_FIXTURE_CHECK(expected != expected_globals_.end() && expected->second.first == offset);
          auto &memory = allocations_.at(allocation);
          const auto &initial = expected->second.second;
          GEMSTONE_RPC_FIXTURE_CHECK(offset <= memory.size() && initial.size() <= memory.size() - offset);
          std::copy(initial.begin(), initial.end(), memory.begin() + offset);
          expected_globals_.erase(expected);
          break;
        }
        case 7:
        case 11:
        case 12:
        case 13:
          launch(operation, payload);
          put64(result, completion_);
          break;
        case 8:
          GEMSTONE_RPC_FIXTURE_CHECK(u64(payload.data()) == completion_);
          if (polls_++ < 2) {
            ++pending_;
            put32(result, 1);
          } else {
            completed_ = true;
            put32(result, fail_completion_.exchange(false) ? 3 : 2);
          }
          break;
        case 9:
          GEMSTONE_RPC_FIXTURE_CHECK(completed_ && u64(payload.data()) == completion_);
          active_ = false;
          break;
        case 10:
          GEMSTONE_RPC_FIXTURE_CHECK(completed_);
          put32(result, static_cast<uint32_t>(ranges_.size() + (indirect_write_ ? 1 : 0)));
          for (const auto &range : ranges_) {
            put64(result, output_id_);
            put64(result, range.first);
            put32(result, range.second);
          }
          if (indirect_write_) {
            put64(result, indirect_write_->first);
            put64(result, indirect_write_->second);
            put32(result, 4);
          }
          break;
        case 14:
          GEMSTONE_RPC_FIXTURE_CHECK(completed_);
          put32(result, 0);
          break;
        default: GEMSTONE_RPC_FIXTURE_CHECK(false);
      }
      std::vector<uint8_t> response;
      put32(response, 0x474d4631);
      put32(response, 3);
      put32(response, 0);
      put32(response, 0);
      put32(response, static_cast<uint32_t>(result.size()));
      response.insert(response.end(), result.begin(), result.end());
      send(socket, response);
    }
  }
  void launch(uint32_t operation, const std::vector<uint8_t> &payload) {
    GEMSTONE_RPC_FIXTURE_CHECK(!active_);
    if (indirect_input_) {
      uint64_t child = 0;
      bool found_table = false;
      for (const auto &[id, address] : allocation_addresses_)
        if (address == indirect_input_->table) {
          const auto &bytes = allocations_.at(id);
          GEMSTONE_RPC_FIXTURE_CHECK(bytes.size() >= sizeof(child));
          std::memcpy(&child, bytes.data(), sizeof(child));
          found_table = true;
        }
      GEMSTONE_RPC_FIXTURE_CHECK(found_table);
      bool found_child = false;
      for (const auto &[id, address] : allocation_addresses_) {
        auto &bytes = allocations_.at(id);
        if (child < address || child - address > bytes.size() || bytes.size() - (child - address) < 4) continue;
        uint32_t value = 0;
        std::memcpy(&value, bytes.data() + child - address, sizeof(value));
        GEMSTONE_RPC_FIXTURE_CHECK(value == indirect_input_->value);
        if (indirect_input_->device_write) {
          std::memcpy(bytes.data() + child - address, &*indirect_input_->device_write, 4);
          indirect_write_ = std::make_pair(id, child - address);
        }
        found_child = true;
        break;
      }
      GEMSTONE_RPC_FIXTURE_CHECK(found_child);
    }
    size_t cursor = 8;
    const auto name_size = u32(payload.data() + cursor);
    cursor += 4 + name_size;
    auto take = [&]() {
      GEMSTONE_RPC_FIXTURE_CHECK(cursor + 4 <= payload.size());
      auto value = u32(payload.data() + cursor);
      cursor += 4;
      return value;
    };
    GEMSTONE_RPC_FIXTURE_CHECK(take() == expected_.local);
    GEMSTONE_RPC_FIXTURE_CHECK(take() == expected_.local_yz[0]);
    GEMSTONE_RPC_FIXTURE_CHECK(take() == expected_.local_yz[1]);
    GEMSTONE_RPC_FIXTURE_CHECK(take() == expected_.groups);
    GEMSTONE_RPC_FIXTURE_CHECK(take() == expected_.groups_yz[0]);
    GEMSTONE_RPC_FIXTURE_CHECK(take() == expected_.groups_yz[1]);
    GEMSTONE_RPC_FIXTURE_CHECK(take() == expected_.global);
    GEMSTONE_RPC_FIXTURE_CHECK(take() == expected_.global_yz[0]);
    GEMSTONE_RPC_FIXTURE_CHECK(take() == expected_.global_yz[1]);
    if (operation == 12 || operation == 13) {
      const auto dim = take();
      GEMSTONE_RPC_FIXTURE_CHECK(dim >= 1 && dim <= 3);
      GEMSTONE_RPC_FIXTURE_CHECK(take() == expected_.offset);
      GEMSTONE_RPC_FIXTURE_CHECK(take() == 0);
      GEMSTONE_RPC_FIXTURE_CHECK(take() == 0);
    } else GEMSTONE_RPC_FIXTURE_CHECK(expected_.offset == 0);
    take();
    take();
    if (operation == 11 || operation == 13) {
      const auto count = take();
      cursor += count * 40;
    }
    const auto argument_count = take();
    GEMSTONE_RPC_FIXTURE_CHECK(argument_count >= 2);
    GEMSTONE_RPC_FIXTURE_CHECK(take() == 2);
    output_id_ = u64(payload.data() + cursor);
    cursor += 8;
    const auto base = u64(payload.data() + cursor);
    cursor += 8;
    GEMSTONE_RPC_FIXTURE_CHECK(take() == 1);
    GEMSTONE_RPC_FIXTURE_CHECK(take() == 4);
    uint32_t seed = 0;
    std::memcpy(&seed, payload.data() + cursor, 4);
    GEMSTONE_RPC_FIXTURE_CHECK(seed == expected_.seed);
    cursor += 4;
    if (trailing_arguments_) {
      std::vector<uint8_t> bytes;
      for (uint32_t index = 2; index < argument_count; ++index) {
        GEMSTONE_RPC_FIXTURE_CHECK(take() == 1);
        const auto size = take();
        GEMSTONE_RPC_FIXTURE_CHECK(cursor + size <= payload.size());
        bytes.insert(bytes.end(), payload.begin() + cursor, payload.begin() + cursor + size);
        cursor += size;
      }
      GEMSTONE_RPC_FIXTURE_CHECK(cursor == payload.size() && bytes == *trailing_arguments_);
    }

    auto &bytes = allocations_.at(output_id_);
    ranges_.clear();
    if (!expected_.geometry_only) {
      for (uint32_t group_z = 0; group_z < expected_.groups_yz[1]; ++group_z)
        for (uint32_t group_y = 0; group_y < expected_.groups_yz[0]; ++group_y)
          for (uint32_t group_x = 0; group_x < expected_.groups; ++group_x) {
            const uint32_t first_x = group_x * expected_.local;
            const uint32_t first_y = group_y * expected_.local_yz[0];
            const uint32_t first_z = group_z * expected_.local_yz[1];
            const auto count_x = std::min(expected_.local, expected_.global - first_x);
            const auto count_y = std::min(expected_.local_yz[0], expected_.global_yz[0] - first_y);
            const auto count_z = std::min(expected_.local_yz[1], expected_.global_yz[1] - first_z);
            for (uint32_t z = 0; z < count_z; ++z)
              for (uint32_t y = 0; y < count_y; ++y) {
                const uint64_t first = ((uint64_t(first_z + z) * expected_.global_yz[0]) + first_y + y) * expected_.global + first_x;
                GEMSTONE_RPC_FIXTURE_CHECK(base + (first + count_x) * 4 <= bytes.size());
                for (uint32_t x = 0; x < count_x; ++x) {
                  const uint32_t value = seed + expected_.offset + static_cast<uint32_t>(first + x);
                  std::memcpy(bytes.data() + base + (first + x) * 4, &value, 4);
                }
                ranges_.emplace_back(base + first * 4, count_x * 4);
              }
          }
    }
    ++completion_;
    ++launches_;
    polls_ = 0;
    active_ = true;
    completed_ = false;
  }
  int listener_ = -1;
  uint16_t port_ = 0;
  std::atomic<bool> stopped_{false};
  std::thread acceptor_;
  std::mutex connections_mutex_, state_mutex_;
  std::vector<int> connections_;
  std::vector<std::thread> workers_;
  Expected expected_{};
  std::optional<std::vector<uint8_t>> trailing_arguments_;
  struct IndirectInput {
    uint64_t table;
    uint32_t value;
    std::optional<uint32_t> device_write;
  };
  std::optional<IndirectInput> indirect_input_;
  std::optional<std::pair<uint64_t, uint64_t>> indirect_write_;
  std::map<uint64_t, uint64_t> written_bytes_;
  std::map<uint64_t, std::vector<uint8_t>> allocations_;
  std::map<uint64_t, uint64_t> allocation_addresses_;
  std::map<std::string, std::pair<uint64_t, std::vector<uint8_t>>> expected_globals_;
  std::vector<std::pair<uint64_t, uint32_t>> ranges_;
  std::atomic_bool fail_completion_{false};
  uint64_t output_id_ = 0, completion_ = 100;
  uint32_t polls_ = 0;
  std::atomic<unsigned> launches_{0}, pending_{0}, program_loads_{0};
  bool active_ = false, completed_ = false;
};
} // namespace gemstone_test
#undef GEMSTONE_RPC_FIXTURE_CHECK
#endif
