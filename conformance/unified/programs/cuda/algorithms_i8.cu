#include "../provider.hpp"

#include "unified_suite/adapters/thrust_algorithms.hpp"
#include "unified_suite/algorithms.hpp"

namespace unified_suite::program {

std::vector<Case> algorithm_i8_cases(const Configuration &configuration) {
  return algorithms::cases_for<adapters::ThrustAlgorithms, ScalarType::i8>(configuration);
}

} // namespace unified_suite::program
