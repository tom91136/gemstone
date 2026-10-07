#pragma once

#include <memory>
#include <mutex>
#include <string>

#include "gemstone_execution_rpc.hpp"
#include <gemstone/environment.hpp>

#include <environment_keys.hpp>

namespace gemstone::execution {

enum class ClientMode { Disabled, Ready, Misconfigured };

/** Lazily connects one facade process to the execution service. The service
 * location comes from `GEMSTONE_RPC_CLIENT`/`GEMSTONE_RPC_ENDPOINT`; a `Ping`
 * handshake confirms the transport before the connection is published as ready.
 */
class ClientManager {
public:
  Client *client() {
    ensure();
    return instance.get();
  }
  ClientMode mode() {
    ensure();
    return state;
  }
  bool ready() { return mode() == ClientMode::Ready; }

  std::string error() {
    ensure();
    if (instance) {
      const auto value = instance->error();
      if (!value.empty()) return value;
    }
    return failure.empty() ? "Gemstone execution RPC failed" : failure;
  }

private:
  void ensure() {
    std::call_once(initialised, [this] {
      const char *endpoint = environment::value(environment::RpcEndpoint);
      const char *client = environment::value(environment::RpcClient);
      const bool hasEndpoint = endpoint && endpoint[0] != '\0';
      const bool hasClient = client && client[0] != '\0';
      if (!hasEndpoint && !hasClient) return;
      if (!hasEndpoint || !hasClient) {
        state = ClientMode::Misconfigured;
        failure = std::string(environment::RpcEndpoint) + " and " + environment::RpcClient + " must be set together";
        return;
      }
      instance = Client::fromEnvironment();
      if (!instance) {
        state = ClientMode::Misconfigured;
        failure = std::string(environment::RpcClient) + " must be a positive signed 64-bit integer";
        return;
      }
      if (!instance->call(Ping{})) {
        failure = instance->error();
        instance.reset();
        state = ClientMode::Misconfigured;
        return;
      }
      state = ClientMode::Ready;
    });
  }

  std::once_flag initialised;
  std::unique_ptr<Client> instance;
  ClientMode state = ClientMode::Disabled;
  std::string failure;
};

} // namespace gemstone::execution
