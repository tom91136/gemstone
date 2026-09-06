#include "../provider.hpp"

#include "unified_suite/adapters/thrust_algorithms.hpp"
#include "unified_suite/algorithms.hpp"
#include "unified_suite/legacy_reds.hpp"
#include "unified_suite/rocm_red_cases.hpp"

namespace unified_suite::program {

std::vector<Case> algorithm_i32_cases(const Configuration &configuration) {
  auto result = algorithms::cases_for<adapters::ThrustAlgorithms, ScalarType::i32>(configuration);
  const bool i32_selected =
      std::find(configuration.algorithm_types.begin(), configuration.algorithm_types.end(), ScalarType::i32) !=
      configuration.algorithm_types.end();
  if (i32_selected) {
    auto semantic = rocm_red_cases::cases(configuration);
    result.insert(result.end(), std::make_move_iterator(semantic.begin()), std::make_move_iterator(semantic.end()));
  }
  if (configuration.include_legacy_reds && i32_selected) {
    auto reds = legacy_reds::roc_thrust_cases(configuration);
    result.insert(result.end(), std::make_move_iterator(reds.begin()), std::make_move_iterator(reds.end()));
    auto fixed = legacy_reds::roc_thrust_fixed_cases(configuration);
    result.insert(result.end(), std::make_move_iterator(fixed.begin()), std::make_move_iterator(fixed.end()));
  }
  return result;
}

} // namespace unified_suite::program
