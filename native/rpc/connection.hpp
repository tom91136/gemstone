#pragma once

#include <cstdint>
#include <span>
#include <string>
#include <string_view>
#include <vector>

namespace gemstone::execution {

/** One ordered RPC session. Callers serialize exchanges and never retry a lost session. */
class Connection {
public:
  Connection(uint64_t client, uint64_t high, uint64_t low) : client_(client), sessionHigh_(high), sessionLow_(low) {}
  ~Connection();
  Connection(const Connection &) = delete;
  Connection &operator=(const Connection &) = delete;
  bool exchange(std::string_view operation, std::string_view payload, uint32_t &status, std::vector<uint8_t> &response);
  void poison();
  const std::string &error() const { return error_; }

private:
  bool connect();
  bool send(std::span<const uint8_t> bytes);
  bool receive(std::span<uint8_t> bytes);
  int fd_ = -1;
  int timeoutMillis_ = 300000;
  bool established_ = false;
  bool poisoned_ = false;
  uint64_t client_, sessionHigh_, sessionLow_;
  std::string error_;
};

} // namespace gemstone::execution
