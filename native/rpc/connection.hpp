#pragma once

#include <chrono>
#include <cstdint>
#include <span>
#include <string>
#include <string_view>
#include <vector>

#include <gemstone/file_descriptor.hpp>

namespace gemstone::execution {

/** One ordered RPC session. Callers serialise exchanges and never retry a lost session. */
class Connection {
public:
  Connection(uint64_t client, uint64_t high, uint64_t low) : client(client), sessionHigh(high), sessionLow(low) {}
  Connection(const Connection &) = delete;
  Connection &operator=(const Connection &) = delete;
  ~Connection() = default;
  bool exchange(std::string_view operation, std::string_view payload, uint32_t &status, std::vector<uint8_t> &response);
  void poison();

  std::string error;

private:
  bool exchangeFrames(std::string_view operation, std::string_view payload, uint32_t &status, std::vector<uint8_t> &response);
  bool connect();
  bool send(std::span<const uint8_t> bytes);
  bool receive(std::span<uint8_t> bytes);
  FileDescriptor fd;
  int timeoutMillis = 300000;
  bool established = false;
  bool poisoned = false;
  std::string sessionEndpoint;
  uint64_t client, sessionHigh, sessionLow;
};

/** Steady-clock time of the latest server heartbeat read by any session. */
std::chrono::steady_clock::time_point lastHeartbeat();

} // namespace gemstone::execution
