#include "../provider.hpp"

#include "unified_suite/adapters/host_blas.hpp"
#include "unified_suite/adapters/host_edge_cases.hpp"
#include "unified_suite/edge_cases.hpp"

namespace unified_suite::program {

std::vector<Case> edge_case_cases(const Configuration &configuration) {
  return edge_cases::cases<adapters::HostEdgeCases, adapters::HostBlas>(configuration);
}

} // namespace unified_suite::program
