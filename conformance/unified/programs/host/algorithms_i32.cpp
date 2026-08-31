#include "../provider.hpp"

#include "unified_suite/adapters/host_algorithms.hpp"
#include "unified_suite/algorithms.hpp"

namespace unified_suite::program {

std::vector<Case> algorithm_i32_cases(const Configuration &configuration) {
  return algorithms::cases_for<adapters::HostAlgorithms, ScalarType::i32>(configuration);
}

} // namespace unified_suite::program
