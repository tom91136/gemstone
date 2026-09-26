#ifndef GEMSTONE_EXECUTION_CLIENT_HPP
#define GEMSTONE_EXECUTION_CLIENT_HPP

#include <memory>
#include <mutex>
#include <string>

#include "gemstone_execution_rpc.hpp"
#include <gemstone/environment.hpp>

namespace gemstone::execution {

enum class ClientMode { Uninitialized, Disabled, Ready, Misconfigured };

/** Lazily connects one facade process to the execution service. The service
 * location comes from `GEMSTONE_RPC_CLIENT`/`GEMSTONE_RPC_PORT`; a `Ping`
 * handshake confirms the transport before the connection is published as ready.
 */
class ClientManager {
public:
  Client *client() {
    ensure();
    return client_.get();
  }
  ClientMode mode() {
    ensure();
    return mode_;
  }
  bool ready() { return mode() == ClientMode::Ready; }

  std::string error() {
    ensure();
    if (client_) {
      const auto value = client_->error();
      if (!value.empty()) return value;
    }
    return error_.empty() ? "Gemstone execution RPC failed" : error_;
  }

  /** The underlying client's most recent error, or empty when it has none. */
  std::string lastError() {
    ensure();
    return client_ ? client_->error() : std::string{};
  }

private:
  void ensure() {
    std::call_once(initialized_, [this] {
      const char *port = environment::value(environment::RpcPort);
      const char *client = environment::value(environment::RpcClient);
      const bool hasPort = port && port[0] != '\0';
      const bool hasClient = client && client[0] != '\0';
      if (!hasPort && !hasClient) {
        mode_ = ClientMode::Disabled;
        return;
      }
      if (!hasPort || !hasClient) {
        mode_ = ClientMode::Misconfigured;
        error_ = "GEMSTONE_RPC_PORT and GEMSTONE_RPC_CLIENT must be set together";
        return;
      }
      client_ = Client::fromEnvironment();
      if (!client_) {
        mode_ = ClientMode::Misconfigured;
        error_ = "GEMSTONE_RPC_CLIENT must be a positive integer";
        return;
      }
      if (!client_->call(Ping{})) {
        error_ = client_->error();
        client_.reset();
        mode_ = ClientMode::Misconfigured;
        return;
      }
      mode_ = ClientMode::Ready;
    });
  }

  std::once_flag initialized_;
  std::unique_ptr<Client> client_;
  ClientMode mode_ = ClientMode::Uninitialized;
  std::string error_;
};

} // namespace gemstone::execution

#endif
