#include "connection.hpp"

#include <array>
#include <cerrno>
#include <charconv>
#include <chrono>
#include <cstring>
#include <limits>

#include <fcntl.h>
#include <netdb.h>
#include <poll.h>
#include <unistd.h>

#include "gemstone/environment.hpp"
#include "gemstone_execution_rpc.hpp"

#include <environment_keys.hpp>
#include <netinet/tcp.h>
#include <sys/socket.h>
#include <sys/un.h>

namespace gemstone::execution {
namespace {
bool ready(int fd, short events, int timeout) {
  const auto deadline = std::chrono::steady_clock::now() + std::chrono::milliseconds(timeout);
  for (;;) {
    const auto remaining = std::chrono::duration_cast<std::chrono::milliseconds>(deadline - std::chrono::steady_clock::now()).count();
    if (remaining <= 0) return false;
    pollfd item{fd, events, 0};
    const int result = ::poll(&item, 1, static_cast<int>(remaining));
    if (result < 0 && errno == EINTR) continue;
    return result > 0 && (item.revents & POLLNVAL) == 0;
  }
}
int socketTimeoutMillis() { return environment::rpcTimeoutSeconds() * 1000; }

int openSocket(int family, const sockaddr *address, socklen_t size, int timeout) {
  FileDescriptor fd(::socket(family, SOCK_STREAM | SOCK_CLOEXEC | SOCK_NONBLOCK, 0));
  if (fd.get() < 0) return -1;
  if (::connect(fd.get(), address, size) < 0) {
    if (errno != EINPROGRESS || !ready(fd.get(), POLLOUT, timeout)) {
      return -1;
    }
    int error = 0;
    socklen_t length = sizeof(error);
    if (::getsockopt(fd.get(), SOL_SOCKET, SO_ERROR, &error, &length) < 0 || error != 0) {
      return -1;
    }
  }
  if (family != AF_UNIX) {
    const int enabled = 1;
    if (::setsockopt(fd.get(), IPPROTO_TCP, TCP_NODELAY, &enabled, sizeof(enabled)) < 0) {
      return -1;
    }
  }
  return fd.release();
}
int openEndpoint(std::string_view endpoint, int timeout, std::string &error) {
  if (endpoint.starts_with("unix:///")) {
    std::string path;
    auto encoded = endpoint.substr(7);
    for (size_t index = 0; index < encoded.size(); ++index) {
      char byte = encoded[index];
      if (byte == '?' || byte == '#') {
        error = "Unix endpoint must not contain a query or fragment";
        return -1;
      }
      if (byte == '%') {
        unsigned value = 0;
        if (index + 2 >= encoded.size()) {
          error = "invalid escaped Unix endpoint";
          return -1;
        }
        const char *first = encoded.data() + index + 1;
        const auto parsed = std::from_chars(first, first + 2, value, 16);
        if (parsed.ec != std::errc{} || parsed.ptr != first + 2 || value == 0) {
          error = "invalid escaped Unix endpoint";
          return -1;
        }
        byte = static_cast<char>(value);
        index += 2;
      }
      path.push_back(byte);
    }
    sockaddr_un address{};
    address.sun_family = AF_UNIX;
    if (path.size() >= sizeof(address.sun_path)) {
      error = "Unix endpoint path is too long";
      return -1;
    }
    std::memcpy(address.sun_path, path.c_str(), path.size() + 1);
    return openSocket(AF_UNIX, reinterpret_cast<const sockaddr *>(&address), sizeof(address), timeout);
  }
  if (endpoint.starts_with("tcp://")) {
    const auto authority = endpoint.substr(6);
    const auto colon = authority.rfind(':');
    if (colon == std::string_view::npos || authority.find_first_of("/@?#%") != std::string_view::npos) {
      error = "TCP endpoint requires a host and port";
      return -1;
    }
    auto host = authority.substr(0, colon);
    if (host.starts_with('[') && host.ends_with(']')) host = host.substr(1, host.size() - 2);
    const auto service = authority.substr(colon + 1);
    unsigned port = 0;
    const auto parsed = std::from_chars(service.data(), service.data() + service.size(), port);
    if (host.empty() || parsed.ec != std::errc{} || parsed.ptr != service.data() + service.size() || port == 0 || port > 65535) {
      error = "invalid TCP endpoint";
      return -1;
    }
    addrinfo hints{};
    hints.ai_family = AF_UNSPEC;
    hints.ai_socktype = SOCK_STREAM;
    hints.ai_flags = AI_NUMERICSERV;
    addrinfo *addresses = nullptr;
    if (::getaddrinfo(std::string(host).c_str(), std::string(service).c_str(), &hints, &addresses) != 0) {
      error = "cannot resolve Gemstone endpoint";
      return -1;
    }
    int fd = -1;
    for (auto *address = addresses; address; address = address->ai_next) {
      fd = openSocket(address->ai_family, address->ai_addr, address->ai_addrlen, timeout);
      if (fd >= 0) break;
    }
    ::freeaddrinfo(addresses);
    return fd;
  }
  error = std::string(environment::RpcEndpoint) + " scheme must be tcp or unix";
  return -1;
}
void appendU32(std::vector<uint8_t> &out, uint32_t value) {
  for (int shift : {24, 16, 8, 0})
    out.push_back(static_cast<uint8_t>(value >> shift));
}
void appendU64(std::vector<uint8_t> &out, uint64_t value) {
  appendU32(out, static_cast<uint32_t>(value >> 32));
  appendU32(out, static_cast<uint32_t>(value));
}
uint32_t readU32(const uint8_t *data) { return (uint32_t{data[0]} << 24) | (uint32_t{data[1]} << 16) | (uint32_t{data[2]} << 8) | data[3]; }
} // namespace

void Connection::poison() {
  fd = FileDescriptor();
  if (established) poisoned = true;
}
bool Connection::connect() {
  if (fd.get() >= 0) return true;
  if (poisoned) {
    error = "execution session was lost; create a fresh client and rebuild its state";
    return false;
  }
  const char *endpoint = environment::value(environment::RpcEndpoint);
  if (!endpoint || !*endpoint) {
    error = std::string(environment::RpcEndpoint) + " is not set";
    return false;
  }
  timeoutMillis = socketTimeoutMillis();
  fd = FileDescriptor(openEndpoint(endpoint, timeoutMillis, error));
  if (fd.get() < 0) {
    if (error.empty()) error = "cannot connect to the Gemstone RPC endpoint";
    return false;
  }
  established = true;
  std::vector<uint8_t> hello;
  appendU32(hello, HelloMagic);
  appendU32(hello, static_cast<uint32_t>(Schema.size()));
  hello.insert(hello.end(), Schema.begin(), Schema.end());
  std::array<uint8_t, 8> reply{};
  if (Schema.size() > MaximumSchemaBytes || !send(hello) || !receive(reply) || readU32(reply.data()) != HelloResponseMagic
      || readU32(reply.data() + 4) != 0) {
    poison();
    error = "execution RPC schema negotiation failed";
    return false;
  }
  return true;
}
bool Connection::send(std::span<const uint8_t> bytes) {
  while (!bytes.empty()) {
    const auto count = ::send(fd.get(), bytes.data(), bytes.size(), MSG_NOSIGNAL);
    if (count < 0 && errno == EINTR) continue;
    if (count < 0 && (errno == EAGAIN || errno == EWOULDBLOCK)) {
      if (ready(fd.get(), POLLOUT, timeoutMillis)) continue;
      return false;
    }
    if (count <= 0) return false;
    bytes = bytes.subspan(static_cast<size_t>(count));
  }
  return true;
}
bool Connection::receive(std::span<uint8_t> bytes) {
  while (!bytes.empty()) {
    const auto count = ::recv(fd.get(), bytes.data(), bytes.size(), 0);
    if (count < 0 && errno == EINTR) continue;
    if (count < 0 && (errno == EAGAIN || errno == EWOULDBLOCK)) {
      if (ready(fd.get(), POLLIN, timeoutMillis)) continue;
      return false;
    }
    if (count <= 0) return false;
    bytes = bytes.subspan(static_cast<size_t>(count));
  }
  return true;
}
bool Connection::exchange(std::string_view operation, std::string_view payload, uint32_t &status, std::vector<uint8_t> &response) {
  error.clear();
  if (operation.empty() || operation.size() > MaximumTagBytes || payload.size() > MaximumPayload) {
    error = "execution request exceeds frame bounds";
    return false;
  }
  if (!connect()) return false;
  std::vector<uint8_t> header;
  header.reserve(RequestHeaderBytes + operation.size());
  appendU32(header, RequestMagic);
  appendU64(header, client);
  appendU64(header, sessionHigh);
  appendU64(header, sessionLow);
  appendU32(header, static_cast<uint32_t>(operation.size()));
  appendU32(header, static_cast<uint32_t>(payload.size()));
  header.insert(header.end(), operation.begin(), operation.end());
  if (!send(header) || !send({reinterpret_cast<const uint8_t *>(payload.data()), payload.size()})) {
    poison();
    error = "execution request transport failed";
    return false;
  }
  std::array<uint8_t, ResponseHeaderBytes> bytes{};
  if (!receive(bytes)) {
    poison();
    error = "execution response transport failed";
    return false;
  }
  status = readU32(bytes.data() + ResponseStatusOffset);
  const uint32_t size = readU32(bytes.data() + ResponsePayloadSizeOffset);
  if (readU32(bytes.data()) != ResponseMagic || status > 1 || size > MaximumPayload) {
    poison();
    error = "execution response header is invalid";
    return false;
  }
  response.resize(size);
  if (!receive(response)) {
    poison();
    error = "execution response body is truncated";
    return false;
  }
  return true;
}
} // namespace gemstone::execution
