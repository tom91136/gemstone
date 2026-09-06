#include "../provider.hpp"

#include "unified_suite/adapters/thrust_algorithms.hpp"
#include "unified_suite/algorithms.hpp"
#include "unified_suite/legacy_reds.hpp"

namespace unified_suite::program {

std::vector<Case> algorithm_i32_cases(const Configuration &configuration) {
  auto result = algorithms::cases_for<adapters::ThrustAlgorithms, ScalarType::i32>(configuration);
  if (configuration.include_legacy_reds &&
      std::find(configuration.algorithm_types.begin(), configuration.algorithm_types.end(),
                ScalarType::i32) != configuration.algorithm_types.end()) {
    auto reds = legacy_reds::cuda_thrust_cases(configuration);
    result.insert(result.end(), std::make_move_iterator(reds.begin()), std::make_move_iterator(reds.end()));
  }
  return result;
}

} // namespace unified_suite::program
