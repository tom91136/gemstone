#include "../provider.hpp"

#include "unified_suite/adapters/host_edge_cases.hpp"
#include "unified_suite/stress.hpp"

namespace unified_suite::program {

std::vector<Case> stress_cases(const Configuration &configuration) {
  return stress::cases<adapters::HostEdgeCases>(configuration);
}

} // namespace unified_suite::program
