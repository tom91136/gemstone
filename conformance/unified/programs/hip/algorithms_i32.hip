#include "../provider.hpp"

#include "unified_suite/adapters/thrust_algorithms.hpp"
#include "unified_suite/algorithms.hpp"
#include "unified_suite/rocthrust_semantic_cases.hpp"

namespace unified_suite::program {

std::vector<Case> algorithm_i32_cases(const Configuration &configuration) {
  auto result = algorithms::cases_for<adapters::ThrustAlgorithms, ScalarType::i32>(configuration);
  const bool i32_selected =
      std::find(configuration.algorithm_types.begin(), configuration.algorithm_types.end(), ScalarType::i32) !=
      configuration.algorithm_types.end();
  if (i32_selected) {
    auto semantic = rocthrust_semantic_cases::cases(configuration);
    result.insert(result.end(), std::make_move_iterator(semantic.begin()), std::make_move_iterator(semantic.end()));
  }
  return result;
}

} // namespace unified_suite::program
